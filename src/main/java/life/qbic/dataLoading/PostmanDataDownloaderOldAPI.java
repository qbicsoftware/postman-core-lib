package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.systemsx.cisd.openbis.dss.client.api.v1.DssComponentFactory;
import ch.systemsx.cisd.openbis.dss.client.api.v1.IDataSetDss;
import ch.systemsx.cisd.openbis.dss.client.api.v1.IDssComponent;
import ch.systemsx.cisd.openbis.dss.generic.shared.api.v1.FileInfoDssDTO;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.core.SupportedFileTypes;
import life.qbic.util.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This uses the old and deprecated openBIS API!
 * The old API features some better optimized code for downloading files
 */
public class PostmanDataDownloaderOldAPI implements PostmanDataDownloader {

    private final static Logger LOG = LogManager.getLogger(PostmanDataDownloaderOldAPI.class);

    private String asURL;
    private String sessionToken;

    private final int DEFAULTBUFFERSIZE = 8192;
    private int buffersize = DEFAULTBUFFERSIZE;

    public PostmanDataDownloaderOldAPI(String asURL, String sessionToken, int buffersize) {
        this.asURL = asURL;
        this.sessionToken = sessionToken;
        this.buffersize = buffersize;
    }

    public PostmanDataDownloaderOldAPI(String asURL, String sessionToken) {
        this.asURL = asURL;
        this.sessionToken = sessionToken;
    }

    @Override
    public void downloadRequestedFilesOfDatasets(List<String> IDs, PostmanFilterOptions postmanFilterOptions,
                                                 PostmanDataFinder postmanDataFinder, String outputPath) throws IOException {
        LOG.info(String.format("%s provided openBIS identifiers have been found: %s",
                IDs.size(), IDs.toString()));

        // a suffix was provided -> only download files which contain the suffix string
        if (!postmanFilterOptions.getSuffixes().isEmpty()) {
            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                final List<DataSetFilePermId> foundSuffixFilteredIDs = postmanDataFinder.findAllSuffixFilteredPermIDs(ident, postmanFilterOptions.getSuffixes());

                LOG.info(String.format("Number of files found: %s", foundSuffixFilteredIDs.size()));

                downloadFilesFilteredByIDsSuffix(postmanFilterOptions.getSuffixes(), foundSuffixFilteredIDs, outputPath);
            }
            // a regex pattern was provided -> only download files which contain the regex pattern
        } else if (!postmanFilterOptions.getRegexPatterns().isEmpty()) {
            for (String ident : IDs) {
                LOG.info(String.format("Downloading files for provided identifier %s", ident));
                final List<DataSetFilePermId> foundRegexFilteredIDs = postmanDataFinder.findAllRegexFilteredPermIDs(ident, postmanFilterOptions.getRegexPatterns());

                LOG.info(String.format("Number of files found: %s", foundRegexFilteredIDs.size()));

//                downloadFilesFilteredByIDsSuffix(ident, foundRegexFilteredIDs, outputPath);
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

                //downloadDataset(foundTypeFilteredIDs, outputPath);
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
     * @param dataSets   datasets to download
     * @param outputPath path to save the downloaded files to
     * @return
     * @throws IOException thrown if an error occurs while downloading
     */
    private int downloadDataset(final List<DataSet> dataSets, final String outputPath) {
        final IDssComponent component = DssComponentFactory.tryCreate(sessionToken, asURL);

        for (DataSet dataSet : dataSets) {
            IDataSetDss dataSetDss = component.getDataSet(dataSet.getCode());
            FileInfoDssDTO[] fileInfos = dataSetDss.listFiles("/", true);

            for (FileInfoDssDTO fileInfo : fileInfos) {
                if (!fileInfo.isDirectory() && fileInfo.getFileSize() > 0) {
                    try (InputStream is = dataSetDss.getFile(fileInfo.getPathInDataSet())) {
                        String[] splittedPath = fileInfo.getPathInDataSet().split("/");
                        String fileName = splittedPath[splittedPath.length - 1];
                        try (OutputStream os = new FileOutputStream(outputPath + File.separator + fileName)) {
                            ProgressBar progressBar = new ProgressBar(fileName, fileInfo.getFileSize());
                            int bufferSize = (fileInfo.getFileSize() < buffersize) ? (int) fileInfo.getFileSize() : buffersize;
                            byte[] buffer = new byte[buffersize];
                            int bytesRead;

                            while ((bytesRead = is.read(buffer)) != -1) {
                                progressBar.updateProgress(bufferSize);
                                os.write(buffer, 0, bytesRead);
                                os.flush();
                            }

                            System.out.print("\n");
                            is.close();

                            os.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        return 0;
    }

    /**
     *
     * @param foundFilteredIDs
     * @param outputPath
     * @throws IOException
     */
    public void downloadFilesFilteredByIDsSuffix(final List<String> suffixes, final List<DataSetFilePermId> foundFilteredIDs, final String outputPath) {
        List<String> dataSetCodes = foundFilteredIDs.stream()
                .map(dataSetFilePermId -> dataSetFilePermId.getDataSetId().toString())
                .collect(Collectors.toList());

        final IDssComponent component = DssComponentFactory.tryCreate(sessionToken, asURL);

        dataSetCodes.forEach(dataSetCode -> {
            IDataSetDss dataSetDss = component.getDataSet(dataSetCode);
            FileInfoDssDTO[] fileInfos = dataSetDss.listFiles("/", true);

            List<FileInfoDssDTO> filteredFileInfosList = new ArrayList<>();
            suffixes.forEach(suffix -> {
                for (FileInfoDssDTO fileInfo : fileInfos) {
                    if (fileInfo.getPathInDataSet().endsWith(suffix)) {
                        filteredFileInfosList.add(fileInfo);
                    }
                }
            });

            for (FileInfoDssDTO fileInfo : filteredFileInfosList) {
                if (!fileInfo.isDirectory() && fileInfo.getFileSize() > 0) {
                    try (InputStream is = dataSetDss.getFile(fileInfo.getPathInDataSet())) {
                        String[] splittedPath = fileInfo.getPathInDataSet().split("/");
                        String fileName = splittedPath[splittedPath.length - 1];
                        try (OutputStream os = new FileOutputStream(outputPath + File.separator + fileName)) {
                            ProgressBar progressBar = new ProgressBar(fileName, fileInfo.getFileSize());
                            int bufferSize = (fileInfo.getFileSize() < buffersize) ? (int) fileInfo.getFileSize() : buffersize;
                            byte[] buffer = new byte[buffersize];
                            int bytesRead;

                            while ((bytesRead = is.read(buffer)) != -1) {
                                progressBar.updateProgress(bufferSize);
                                os.write(buffer, 0, bytesRead);
                                os.flush();
                            }

                            System.out.print("\n");
                            is.close();

                            os.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
