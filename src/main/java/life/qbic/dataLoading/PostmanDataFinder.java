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
    private PostmanDataFilterer postmanDataFilterer;
    private String sessionToken;

    public PostmanDataFinder(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer,
                             PostmanDataFilterer postmanDataFilterer, String sessionToken) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.postmanDataFilterer = postmanDataFilterer;
        this.sessionToken = sessionToken;
    }

    /**
     * Finds all datasets of a given sampleID, even those of its children - recursively
     *
     * @param sampleId id to find all datasets for
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
     * @param sample sample to find all datasets for
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
     * @param ident identifier to find all IDs for
     * @param regexPatterns regex patterns which are applied as filters
     * @return
     */
    public List<DataSetFilePermId> findAllRegexFilteredPermIDs(final String ident, final List<String> regexPatterns) {
        if (ident.isEmpty()) {
            throw new IllegalArgumentException("Attempted to find empty IDs. IDs cannot be empty!");
        }

        final List<DataSet> allDatasets = findAllDatasetsRecursive(ident);

        return RegexFilterUtil.findAllRegexFilteredIDsGroovy(regexPatterns, allDatasets, dataStoreServer, sessionToken);
    }

    /**
     * Finds all IDs of files filtered by a suffix
     *
     * @param ident identifier to find all IDs for
     * @param suffixes suffixes which are applied as filters
     * @return
     */
    public List<DataSetFilePermId> findAllSuffixFilteredPermIDs(final String ident, final List<String> suffixes) {
        if (ident.isEmpty()) {
            throw new IllegalArgumentException("Attempted to find empty IDs. IDs cannot be empty!");
        }

        final List<DataSet> allDatasets = findAllDatasetsRecursive(ident);
        List<DataSetFilePermId> allFileIDs = new ArrayList<>();

        for (DataSet ds : allDatasets) {
            // we cannot access the files directly of the datasets -> we need to query for the files first using the datasetID
            DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
            criteria.withDataSet().withCode().thatEquals(ds.getCode());
            SearchResult<DataSetFile> result = dataStoreServer.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
            final List<DataSetFile> files = result.getObjects();

            List<DataSetFile> suffixFilteredDataSetFiles = postmanDataFilterer.filterDataSetFilesBySuffix(files, suffixes);

            suffixFilteredDataSetFiles.stream()
                    .map(DataSetFile::getPermId)
                    .forEachOrdered(allFileIDs::add);
        }

        return allFileIDs;
    }

    /**
     *
     * @param ident identifier to find all IDs for
     * @param fileType filetype which is used for filtering
     * @return all datasets which were filtered by fileType
     */
    public List<DataSet> findAllTypeFilteredDataSets(final String ident, final String fileType) {
        SampleSearchCriteria criteria = new SampleSearchCriteria();
        criteria.withCode().thatEquals(ident);

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
            if (fileType.equals(ds.getType().getCode())){
                filteredDatasets.add(ds);
            }
        }

        return filteredDatasets;
    }


    /**
     * Finds all IDs of files - no filtering applied
     *
     * @param ident identifier to find all IDs for
     * @return all permIDs without any filtering applied
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

    public IApplicationServerApi getApplicationServer() {
        return applicationServer;
    }

    public void setApplicationServer(IApplicationServerApi applicationServer) {
        this.applicationServer = applicationServer;
    }

    public PostmanDataFilterer getPostmanDataFilterer() {
        return postmanDataFilterer;
    }

    public void setPostmanDataFilterer(PostmanDataFilterer postmanDataFilterer) {
        this.postmanDataFilterer = postmanDataFilterer;
    }

    public IDataStoreServerApi getDataStoreServer() {
        return dataStoreServer;
    }

    public void setDataStoreServer(IDataStoreServerApi dataStoreServer) {
        this.dataStoreServer = dataStoreServer;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
