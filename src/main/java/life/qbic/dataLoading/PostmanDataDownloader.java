package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadReader;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.util.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;


public class PostmanDataDownloader {

    private final static Logger LOG = LogManager.getLogger(PostmanDataDownloader.class);

    private IApplicationServerApi applicationServer;
    private IDataStoreServerApi dataStoreServer;
    private String sessionToken;
    private String filterType;

    private final int DEFAULTBUFFERSIZE = 1024;
    private int buffersize = DEFAULTBUFFERSIZE;


    /**
     *
     * @param applicationServer
     * @param dataStoreServer
     * @param bufferSize
     * @param filterType
     */
    public PostmanDataDownloader(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer,
                                 String sessionToken, int bufferSize, String filterType) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
        this.buffersize = bufferSize;
        this.filterType = filterType;
    }

    public PostmanDataDownloader(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer,
                                 String sessionToken, int bufferSize) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
        this.buffersize = bufferSize;
    }

    public PostmanDataDownloader(IApplicationServerApi applicationServer, IDataStoreServerApi dataStoreServer,
                                 String sessionToken) {
        this.applicationServer = applicationServer;
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
    }

    /**
     * Downloads the files that the user requested
     * checks whether any filtering option (suffix or regex) has been passed and applies filtering if needed
     * @param IDs
     * @param postmanFilterOptions
     * @throws IOException
     */
    public void downloadRequestedFilesOfDatasets(final List<String> IDs,
                                                 final PostmanFilterOptions postmanFilterOptions,
                                                 final PostmanDataFinder postmanDataFinder,
                                                 final String outputPath) throws IOException {
        LOG.info(String.format("%s provided openBIS identifiers have been found: %s",
                IDs.size(), IDs.toString()));

        // a suffix was provided -> only download files which contain the suffix string
        if (!postmanFilterOptions.getSuffixes().isEmpty()) {
            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                final List<DataSetFilePermId> foundSuffixFilteredIDs = postmanDataFinder.findAllSuffixFilteredIDs(ident, postmanFilterOptions.getSuffixes());

                LOG.info(String.format("Number of files found: %s", foundSuffixFilteredIDs.size()));

                downloadFilesFilteredByIDs(ident, foundSuffixFilteredIDs, outputPath);
            }
            // a regex pattern was provided -> only download files which contain the regex pattern
        } else if (!postmanFilterOptions.getRegexPatterns().isEmpty()) {
            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                final List<DataSetFilePermId> foundRegexFilteredIDs = postmanDataFinder.findAllRegexFilteredIDs(ident, postmanFilterOptions.getRegexPatterns());

                LOG.info(String.format("Number of files found: %s", foundRegexFilteredIDs.size()));

                downloadFilesFilteredByIDs(ident, foundRegexFilteredIDs, outputPath);
            }
        } else {
            // no suffix or regex was supplied -> download all datasets
            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                final List<DataSet> foundDataSets = postmanDataFinder.findAllDatasetsRecursive(ident);

                LOG.info(String.format("Number of datasets found: %s", foundDataSets.size()));

                if (foundDataSets.size() > 0) {
                    LOG.info("Initialize download ...");
                    int datasetDownloadReturnCode = -1;
                    try {
                        datasetDownloadReturnCode = downloadDataset(foundDataSets, outputPath);
                    } catch (NullPointerException e) {
                        LOG.error("Datasets were found by the application server, but could not be found on the datastore server for "
                                + ident + "." + " Try to supply the correct datastore server using a config file!");
                    }

                    if (datasetDownloadReturnCode != 0) {
                        LOG.error("Error while downloading dataset: " + ident);
                    } else {
                        LOG.info("Download successfully finished.");
                    }

                } else {
                    LOG.info("Nothing to download.");
                }
            }
        }
    }


    /**
     * Downloads all IDs which were previously filtered by either suffixes or regexPatterns
     *
     * @param ident
     * @param foundFilteredIDs
     * @throws IOException
     */
    private void downloadFilesFilteredByIDs(final String ident,
                                            final List<DataSetFilePermId> foundFilteredIDs,
                                            final String outputPath) throws IOException {
        if (foundFilteredIDs.size() > 0) {
            LOG.info("Initialize download ...");
            int filesDownloadReturnCode = -1;
            try {
                filesDownloadReturnCode = downloadFilesByID(foundFilteredIDs, outputPath);
            } catch (NullPointerException e) {
                LOG.error("Datasets were found by the application server, but could not be found on the datastore server for "
                        + ident + "." + " Try to supply the correct datastore server using a config file!");
            }
            if (filesDownloadReturnCode != 0) {
                LOG.error("Error while downloading dataset: " + ident);
            } else {
                LOG.info("Download successfully finished");
            }

        } else {
            LOG.info("Nothing to download.");
        }
    }

    /**
     * Downloads files that have been found after filtering for suffixes/regexPatterns by a list of supplied IDs
     *
     * @param filteredIDs
     * @param outputPath path to write all downloaded files to
     * @return exitcode
     * @throws IOException
     */
    private int downloadFilesByID(final List<DataSetFilePermId> filteredIDs, final String outputPath) throws IOException{
        for (IDataSetFileId id : filteredIDs) {
            DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
            options.setRecursive(true);
            InputStream stream = this.dataStoreServer.downloadFiles(sessionToken, Collections.singletonList(id), options);
            DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
            DataSetFileDownload file;

            while ((file = reader.read()) != null) {
                InputStream initialStream = file.getInputStream();

                if (file.getDataSetFile().getFileLength() > 0) {
                    String[] splitted = file.getDataSetFile().getPath().split("/");
                    String lastOne = splitted[splitted.length - 1];
                    OutputStream os = new FileOutputStream(outputPath + File.separator + lastOne);
                    ProgressBar progressBar = new ProgressBar(lastOne, file.getDataSetFile().getFileLength());
                    int bufferSize = (file.getDataSetFile().getFileLength() < DEFAULTBUFFERSIZE) ?
                            (int) file.getDataSetFile().getFileLength() : DEFAULTBUFFERSIZE;
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead;
                    //read from is to buffer
                    while ((bytesRead = initialStream.read(buffer)) != -1) {
                        progressBar.updateProgress(bufferSize);
                        os.write(buffer, 0, bytesRead);
                        os.flush();

                    }
                    System.out.print("\n");
                    initialStream.close();

                    os.flush();
                    os.close();
                }

            }
        }

        return 0;
    }

    /**
     * Download a given list of datasets
     * There was no filtering applied here!
     *
     * @param dataSetList A list of datasets
     * @param outputPath path to write all downloaded files to
     * @return 0 if successful, 1 else
     */
    private int downloadDataset(final List<DataSet> dataSetList, final String outputPath) throws IOException{
        for (DataSet dataset : dataSetList) {
            DataSetPermId permID = dataset.getPermId();
            DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
            IDataSetFileId fileId = new DataSetFilePermId(new DataSetPermId(permID.toString()));
            options.setRecursive(true);
            InputStream stream = this.dataStoreServer.downloadFiles(sessionToken, Collections.singletonList(fileId), options);
            DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
            DataSetFileDownload file;

            while ((file = reader.read()) != null) {
                InputStream initialStream = file.getInputStream();

                if (file.getDataSetFile().getFileLength() > 0) {
                    String[] splitted = file.getDataSetFile().getPath().split("/");
                    String lastOne = splitted[splitted.length - 1];
                    OutputStream os = new FileOutputStream(outputPath + File.separator + lastOne);
                    ProgressBar progressBar = new ProgressBar(lastOne, file.getDataSetFile().getFileLength());
                    int bufferSize = (file.getDataSetFile().getFileLength() < DEFAULTBUFFERSIZE) ? (int) file.getDataSetFile().getFileLength() : DEFAULTBUFFERSIZE;
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead;
                    //read from is to buffer
                    while ((bytesRead = initialStream.read(buffer)) != -1) {
                        progressBar.updateProgress(bufferSize);
                        os.write(buffer, 0, bytesRead);
                        os.flush();

                    }
                    System.out.print("\n");
                    initialStream.close();

                    os.flush();
                    os.close();
                }

            }
        }

        return 0;
    }

}
    