package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import life.qbic.core.filtering.FilterOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataStreamProvider {

    private final static Logger LOG = LogManager.getLogger(DataStreamProvider.class);

    private IApplicationServerApi applicationServer;

    private IDataStoreServerApi dataStoreServer;

    private String sessionToken;

    private String filterType;

    public DataStreamProvider(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer, String sessionToken, String filterType) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
        this.filterType = filterType;
    }

    /**
     * Provides Inputstreams for IDs
     * checks whether any filtering option (suffix or regex) has been passed and applies filtering if needed
     * @param IDs
     * @return
     */
    public InputStream provideInputStreamForIds(List<String> IDs, FilterOptions filterOptions) {
        DataFinder dataFinder = new DataFinder(applicationServer,
                dataStoreServer,
                sessionToken,
                filterType);

        LOG.info(String.format("%s provided openBIS identifiers have been found: %s",
                IDs.size(), IDs.toString()));

        // a suffix was provided -> only provide stream for files which contain the suffix string
        if (!filterOptions.getSuffixes().isEmpty()) {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                List<IDataSetFileId> foundSuffixFilteredIDs = dataFinder.findAllSuffixFilteredIDs(ident, filterOptions.getSuffixes());

                LOG.info(String.format("Number of files found: %s", foundSuffixFilteredIDs.size()));

                inputStreams.add(getDatasetStreamFromFilteredIds(foundSuffixFilteredIDs));
            }

            return new SequenceInputStream(Collections.enumeration(inputStreams));

            // a regex pattern was provided -> only provide stream for files which contain the regex pattern
        } else if (!filterOptions.getRegexPatterns().isEmpty()) {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                List<IDataSetFileId> foundRegexFilteredIDs = dataFinder.findAllRegexFilteredIDs(ident, filterOptions.getRegexPatterns());

                LOG.info(String.format("Number of files found: %s", foundRegexFilteredIDs.size()));

                inputStreams.add(getDatasetStreamFromFilteredIds(foundRegexFilteredIDs));
            }

            return new SequenceInputStream(Collections.enumeration(inputStreams));

            // no suffix or regex was supplied -> provide stream for all datasets
        } else {
            List<InputStream> inputStreams = new ArrayList<>();

            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                List<DataSet> foundDataSets = dataFinder.findAllDatasetsRecursive(ident);

                LOG.info(String.format("Number of datasets found: %s", foundDataSets.size()));

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
    private InputStream getDatasetStreamFromFilteredIds(List<IDataSetFileId> filteredIDs) {
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
    private InputStream getDatasetStreamFromDatasetList(List<DataSet> dataSetList) {
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
