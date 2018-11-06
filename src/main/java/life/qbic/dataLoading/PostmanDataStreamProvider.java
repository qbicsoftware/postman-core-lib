package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import life.qbic.core.PostmanFilterOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostmanDataStreamProvider {

    private final static Logger LOG = LogManager.getLogger(PostmanDataStreamProvider.class);

    private IApplicationServerApi applicationServer;
    private IDataStoreServerApi dataStoreServer;
    private String sessionToken;
    private String filterType;

    public PostmanDataStreamProvider(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer, String sessionToken, String filterType) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
        this.filterType = filterType;
    }

    public PostmanDataStreamProvider(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer, String sessionToken) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
    }

    /**
     * Provides Inputstreams for IDs
     * checks whether any filtering option (suffix or regex) has been passed and applies filtering if needed
     * @param IDs
     * @return
     */
    public InputStream provideInputStreamForIds(final List<String> IDs, final PostmanFilterOptions postmanFilterOptions) {
        PostmanDataFinder postmanDataFinder = new PostmanDataFinder(applicationServer,
                dataStoreServer,
                sessionToken,
                filterType);

        LOG.debug(String.format("%s provided openBIS identifiers have been found: %s",
                IDs.size(), IDs.toString()));

        // a suffix was provided -> only provide stream for files which contain the suffix string
        if (!postmanFilterOptions.getSuffixes().isEmpty()) {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : IDs) {
                LOG.debug(String.format("Providing datastream for provided identifier %s", ident));
                List<DataSetFilePermId> foundSuffixFilteredIDs = postmanDataFinder.findAllSuffixFilteredIDs(ident, postmanFilterOptions.getSuffixes());

                LOG.debug(String.format("Number of files found: %s", foundSuffixFilteredIDs.size()));

                inputStreams.add(getDatasetStreamFromFilteredIds(foundSuffixFilteredIDs));
            }

            return new SequenceInputStream(Collections.enumeration(inputStreams));

            // a regex pattern was provided -> only provide stream for files which contain the regex pattern
        } else if (!postmanFilterOptions.getRegexPatterns().isEmpty()) {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : IDs) {
                LOG.debug(String.format("Providing datastream for provided identifier %s", ident));
                List<DataSetFilePermId> foundRegexFilteredIDs = postmanDataFinder.findAllRegexFilteredIDs(ident, postmanFilterOptions.getRegexPatterns());

                LOG.debug(String.format("Number of files found: %s", foundRegexFilteredIDs.size()));

                inputStreams.add(getDatasetStreamFromFilteredIds(foundRegexFilteredIDs));
            }

            return new SequenceInputStream(Collections.enumeration(inputStreams));

            // no suffix or regex was supplied -> provide stream for all datasets
        } else {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : IDs) {
                LOG.debug(String.format("Providing datastream for provided identifier %s", ident));
                List<DataSet> foundDataSets = postmanDataFinder.findAllDatasetsRecursive(ident);

                LOG.debug(String.format("Number of datasets found: %s", foundDataSets.size()));

                inputStreams.add(getDatasetStreamFromDatasetList(foundDataSets));
            }

            return new SequenceInputStream(Collections.enumeration(inputStreams));
        }
    }

    /**
     * Provides an InputStream files that have been found after filtering for suffixes/regexPatterns by a list of supplied IDs
     *
     * @param filteredIDs
     * @return exitcode
     */
    private InputStream getDatasetStreamFromFilteredIds(final List<DataSetFilePermId> filteredIDs) {
        List<InputStream> inputStreams = new ArrayList<>();

        for (IDataSetFileId id : filteredIDs) {
            DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
            options.setRecursive(true);
            InputStream stream = this.dataStoreServer.downloadFiles(sessionToken, Collections.singletonList(id), options);
            inputStreams.add(stream);
        }

        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }

    /**
     * Provides an InputStream for a given list of datasets
     * There was no filtering applied here!
     *
     * @param dataSetList A list of datasets
     * @return InputStream
     */
    private InputStream getDatasetStreamFromDatasetList(final List<DataSet> dataSetList) {
        List<InputStream> inputStreams = new ArrayList<>();

        for (DataSet dataset : dataSetList) {
            DataSetPermId permID = dataset.getPermId();
            DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
            IDataSetFileId fileId = new DataSetFilePermId(new DataSetPermId(permID.toString()));
            options.setRecursive(true);
            InputStream stream = this.dataStoreServer.downloadFiles(sessionToken, Collections.singletonList(fileId), options);
            inputStreams.add(stream);
        }

        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }
}
