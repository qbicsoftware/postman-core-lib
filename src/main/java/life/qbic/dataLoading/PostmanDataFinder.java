package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria;
import life.qbic.util.RegexFilterDownloadUtil;
import life.qbic.util.StringUtil;
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
    List<DataSet> findAllDatasetsRecursive(final String sampleId) {
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

        if (filterType.isEmpty())
            return foundDatasets;

        List<DataSet> filteredDatasets = new ArrayList<>();
        for (DataSet ds : foundDatasets){
            LOG.debug(ds.getType().getCode() + " found.");
            if (filterType.equals(ds.getType().getCode())){
                filteredDatasets.add(ds);
            }
        }

        return filteredDatasets;
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
    List<DataSetFilePermId> findAllRegexFilteredIDs(final String ident, final List<String> regexPatterns) {
        final List<DataSet> allDatasets = findAllDatasetsRecursive(ident);

        return RegexFilterDownloadUtil.findAllRegexFilteredIDsGroovy(regexPatterns, allDatasets, dataStoreServer, sessionToken);
    }

    /**
     * Finds all IDs of files filtered by a suffix
     *
     * @param ident
     * @param suffixes
     * @return
     */
    List<DataSetFilePermId> findAllSuffixFilteredIDs(final String ident, final List<String> suffixes) {
        final List<DataSet> allDatasets = findAllDatasetsRecursive(ident);
        List<DataSetFilePermId> allFileIDs = new ArrayList<>();

        for (DataSet ds : allDatasets) {
            // we cannot access the files directly of the datasets -> we need to query for the files first using the datasetID
            DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
            criteria.withDataSet().withCode().thatEquals(ds.getCode());
            SearchResult<DataSetFile> result = dataStoreServer.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
            final List<DataSetFile> files = result.getObjects();

            List<DataSetFilePermId> fileIds = new ArrayList<>();

            // only add to the result if the suffix matches
            for (DataSetFile file : files)
            {
                for (String suffix : suffixes) {
                    if (StringUtil.endsWithIgnoreCase(file.getPermId().toString(), suffix)) {
                        fileIds.add(file.getPermId());
                    }
                }
            }

            allFileIDs.addAll(fileIds);
        }

        return allFileIDs;
    }



}
