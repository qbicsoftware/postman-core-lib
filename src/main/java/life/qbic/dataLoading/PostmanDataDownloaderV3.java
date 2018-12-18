package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadReader;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.core.SupportedFileTypes;
import life.qbic.util.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;


public class PostmanDataDownloaderV3 implements PostmanDataDownloader {

    private final static Logger LOG = LogManager.getLogger(PostmanDataDownloaderV3.class);

    private IDataStoreServerApi dataStoreServer;
    private String sessionToken;

    private final int DEFAULTBUFFERSIZE = 8192;
    private int buffersize = DEFAULTBUFFERSIZE;


    /**
     * @param dataStoreServer
     * @param bufferSize
     */
    public PostmanDataDownloaderV3(IDataStoreServerApi dataStoreServer,
                                   String sessionToken, int bufferSize) {
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
        this.buffersize = bufferSize;
    }

    public PostmanDataDownloaderV3(IDataStoreServerApi dataStoreServer,
                                   String sessionToken) {
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
    }

    /**
     * Downloads the files that the user requested
     * checks whether any filtering option (suffix or regex) has been passed and applies filtering if needed
     *
     * @param IDs                  specifies the IDs which are subsequently downloaded
     * @param postmanFilterOptions required to filter any data - pass it with empty lists of different filter options to download files without any filtering
     * @param postmanDataFinder    required to find the data before downloading
     * @param outputPath           where the files are downloaded to
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
                final List<DataSetFilePermId> foundSuffixFilteredIDs = postmanDataFinder.findAllSuffixFilteredPermIDs(ident, postmanFilterOptions.getSuffixes());

                LOG.info(String.format("Number of files found: %s", foundSuffixFilteredIDs.size()));

                downloadFilesFilteredByIDs(ident, foundSuffixFilteredIDs, outputPath);
            }
            // a regex pattern was provided -> only download files which contain the regex pattern
        } else if (!postmanFilterOptions.getRegexPatterns().isEmpty()) {
            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                final List<DataSetFilePermId> foundRegexFilteredIDs = postmanDataFinder.findAllRegexFilteredPermIDs(ident, postmanFilterOptions.getRegexPatterns());

                LOG.info(String.format("Number of files found: %s", foundRegexFilteredIDs.size()));

                downloadFilesFilteredByIDs(ident, foundRegexFilteredIDs, outputPath);
            }
            // filter type was specified
        } else if (!postmanFilterOptions.getFileType().isEmpty()) {
            if (!SupportedFileTypes.getSupportedFilterTypes().keySet().contains(postmanFilterOptions.getFileType())) {
                LOG.error("Provided file filter type " + postmanFilterOptions.getFileType() + " is not supported!");
                LOG.warn("Filtering may not be applied!");
            }

            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                final List<DataSet> foundTypeFilteredIDs = postmanDataFinder.findAllTypeFilteredDataSets(ident, postmanFilterOptions.getFileType());

                LOG.info(String.format("Number of files found: %s", foundTypeFilteredIDs.size()));

                downloadDataset(foundTypeFilteredIDs, outputPath);
            }
        }
        // no suffix or regex was supplied -> download all datasets
        else {
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
     * @param ident            current identifier to be downloaded
     * @param foundFilteredIDs IDs which were earlier filtered
     * @param outputPath       path to write all downloaded files to
     * @throws IOException
     */
    public void downloadFilesFilteredByIDs(final String ident,
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
     * @param filteredIDs IDs which were earlier filtered
     * @param outputPath  path to write all downloaded files to
     * @return exitcode: 0 if successful
     * @throws IOException
     */
    private int downloadFilesByID(final List<DataSetFilePermId> filteredIDs, final String outputPath) {
        for (IDataSetFileId id : filteredIDs) {
            try {
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
                        int bufferSize = (file.getDataSetFile().getFileLength() < buffersize) ? (int) file.getDataSetFile().getFileLength() : buffersize;
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * Download a given list of datasets
     * There was no filtering applied here!
     *
     * @param dataSetList A list of datasets to download
     * @param outputPath  path to write all downloaded files to
     * @return 0 if successful, 1 else
     */
    private int downloadDataset(final List<DataSet> dataSetList, final String outputPath) throws IOException {
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
                    String[] splittedPath = file.getDataSetFile().getPath().split("/");
                    String fileName = splittedPath[splittedPath.length - 1];
                    OutputStream os = new FileOutputStream(outputPath + File.separator + fileName);
                    ProgressBar progressBar = new ProgressBar(fileName, file.getDataSetFile().getFileLength());
                    int bufferSize = (file.getDataSetFile().getFileLength() < buffersize) ? (int) file.getDataSetFile().getFileLength() : buffersize;
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead;

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
    