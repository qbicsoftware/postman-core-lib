package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria;
import life.qbic.util.RegexFilterUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PostmanDataFinder {

    private final static Logger LOG = LogManager.getLogger(PostmanDataFinder.class);

    private IApplicationServerApi applicationServer;

    private IDataStoreServerApi dataStoreServer;

    private String sessionToken;

    private String filterType = "";

    PostmanDataFinder(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer, String sessionToken, String filterType) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
        this.filterType = filterType;
    }

    public PostmanDataFinder(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer, String sessionToken) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
    }

    /**
     * Finds all datasets of a given sampleID, even those of its children - recursively
     *
     * @param sampleId
     * @return all found datasets for a given sampleID
     */
    public List<DataSet> findAllDatasetsRecursive(final String sampleId) {
        SampleSearchCriteria criteria = new SampleSearchCriteria();
        criteria.withCode().thatEquals(sampleId);

        // tell the API to fetch all descendants for each returned sample
        SampleFetchOptions fetchOptions = new SampleFetchOptions();
        DataSetFetchOptions dsFetchOptions = new DataSetFetchOptions();
        dsFetchOptions.withType();
        fetchOptions.withChildrenUsing(fetchOptions);
        fetchOptions.withDataSetsUsing(dsFetchOptions);
        SearchResult<Sample> result = applicationServer.searchSamples(sessionToken, criteria, fetchOptions);

        List<DataSet> foundDatasets = new ArrayList<>();

        for (Sample sample : result.getObjects()) {
            // add the datasets of the sample itself
            foundDatasets.addAll(sample.getDataSets());

            // fetch all datasets of the children
            foundDatasets.addAll(fetchDescendantDatasets(sample));
        }

        return foundDatasets;
    }

    /**
     * Fetches all datasets, even those of children - recursively
     *
     * @param sample
     * @return all recursively found datasets
     */
    private static List<DataSet> fetchDescendantDatasets(final Sample sample) {
        List<DataSet> foundSets = new ArrayList<>();

        // fetch all datasets of the children recursively
        for (Sample child : sample.getChildren()) {
            final List<DataSet> foundChildrenDatasets = child.getDataSets();
            foundSets.addAll(foundChildrenDatasets);
            foundSets.addAll(fetchDescendantDatasets(child));
        }

        return foundSets;
    }

    /**
     * Calls groovy code
     * Filters all IDs by provided regex patterns
     *
     * @param ident
     * @param regexPatterns
     * @return
     */
    List<DataSetFilePermId> findAllRegexFilteredPermIDs(final String ident, final List<String> regexPatterns) {
        final List<DataSet> allDatasets = findAllDatasetsRecursive(ident);

        return RegexFilterUtil.findAllRegexFilteredIDsGroovy(regexPatterns, allDatasets, dataStoreServer, sessionToken);
    }

    /**
     * Finds all IDs of files filtered by a suffix
     *
     * @param ident
     * @param suffixes
     * @return
     */
    public List<DataSetFilePermId> findAllSuffixFilteredPermIDs(final String ident, final List<String> suffixes) {
        final List<DataSet> allDatasets = findAllDatasetsRecursive(ident);
        List<DataSetFilePermId> allFileIDs = new ArrayList<>();

        for (DataSet ds : allDatasets) {
            // we cannot access the files directly of the datasets -> we need to query for the files first using the datasetID
            DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
            criteria.withDataSet().withCode().thatEquals(ds.getCode());
            SearchResult<DataSetFile> result = dataStoreServer.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
            final List<DataSetFile> files = result.getObjects();

            PostmanDataFilterer postmanDataFilterer = new PostmanDataFilterer();

            List<DataSetFile> suffixFilteredDataSetFiles = postmanDataFilterer.filterDataSetFilesBySuffix(files, suffixes);

            suffixFilteredDataSetFiles.stream()
                    .map(DataSetFile::getPermId)
                    .forEachOrdered(allFileIDs::add);
        }

        return allFileIDs;
    }

    public List<DataSet> findAllTypeFilteredPermIDs(final String ident, final String filterType) {
        SampleSearchCriteria criteria = new SampleSearchCriteria();
        criteria.withCode().thatEquals(ident);
        List<DataSetPermId> allFileIDs = new ArrayList<>();

        // tell the API to fetch all descendants for each returned sample
        SampleFetchOptions fetchOptions = new SampleFetchOptions();
        DataSetFetchOptions dsFetchOptions = new DataSetFetchOptions();
        dsFetchOptions.withType();
        fetchOptions.withChildrenUsing(fetchOptions);
        fetchOptions.withDataSetsUsing(dsFetchOptions);
        SearchResult<Sample> result = applicationServer.searchSamples(sessionToken, criteria, fetchOptions);

        List<DataSet> foundDatasets = new ArrayList<>();

        for (Sample sample : result.getObjects()) {
            // add the datasets of the sample itself
            foundDatasets.addAll(sample.getDataSets());

            // fetch all datasets of the children
            foundDatasets.addAll(fetchDescendantDatasets(sample));
        }

        List<DataSet> filteredDatasets = new ArrayList<>();
        for (DataSet ds : foundDatasets){
            if (filterType.equals(ds.getType().getCode())){
                filteredDatasets.add(ds);
            }
        }

        return filteredDatasets;
    }


    /**
     * Finds all IDs of files - no filtering applied
     *
     * @param ident
     * @return
     */
    public List<DataSetFilePermId> findAllPermIDs(final String ident) {
        final List<DataSet> allDatasets = findAllDatasetsRecursive(ident);
        List<DataSetFilePermId> allFileIDs = new ArrayList<>();

        for (DataSet ds : allDatasets) {
            // we cannot access the files directly of the datasets -> we need to query for the files first using the datasetID
            DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
            criteria.withDataSet().withCode().thatEquals(ds.getCode());
            SearchResult<DataSetFile> result = dataStoreServer.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
            final List<DataSetFile> files = result.getObjects();

            for (DataSetFile file : files) {
                allFileIDs.add(file.getPermId());
            }
        }

        return allFileIDs;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
}
