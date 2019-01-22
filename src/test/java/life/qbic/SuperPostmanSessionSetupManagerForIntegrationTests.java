package life.qbic;

import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadReader;
import life.qbic.dataLoading.*;
import life.qbic.exceptions.PostmanOpenBISLoginFailedException;
import life.qbic.core.authentication.PostmanConfig;
import life.qbic.core.authentication.PostmanSessionManager;
import life.qbic.io.parser.PostmanPropertiesParser;
import life.qbic.util.ProgressBar;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

/**
 * Super class for all tests which require A PostmanSession
 * Furthermore, objects for all DataLoading operations are created and provided
 */
public class SuperPostmanSessionSetupManagerForIntegrationTests {

    private static PostmanSessionManager postmanSessionManager;
    private static PostmanDataFinder postmanDataFinder;
    private static PostmanDataDownloaderV3 postmanDataDownloaderV3;
    private static PostmanDataDownloaderOldAPI postmanDataDownloaderOldAPI;
    private static PostmanDataStreamProvider postmanDataStreamProvider;
    private static PostmanDataFilterer postmanDataFilterer;

    /**
     * setups PostmanSessionManager
     * logs into openBIS
     *
     * @throws IOException                        if unable to parse Properties
     * @throws PostmanOpenBISLoginFailedException if anything went wrong while logging in
     */
    @BeforeClass
    public static void setupBeforeClass() throws IOException, PostmanOpenBISLoginFailedException {
        PostmanConfig postmanConfig = PostmanPropertiesParser.parserProperties("qbicPropertiesFile.conf");
        postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
        postmanSessionManager.loginToOpenBIS(postmanConfig);

        // create all dataloading objects
        postmanDataFinder = new PostmanDataFinder(
                postmanSessionManager.getApplicationServer(),
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
        postmanDataDownloaderV3 = new PostmanDataDownloaderV3(
                postmanSessionManager.getDataStoreServer(),
                postmanDataFinder,
                postmanSessionManager.getSessionToken()
        );
        postmanDataDownloaderOldAPI = new PostmanDataDownloaderOldAPI(
                postmanDataFinder,
                "https://qbis.qbic.uni-tuebingen.de/openbis/openbis",
                postmanSessionManager.getSessionToken()
        );

        postmanDataStreamProvider = new PostmanDataStreamProvider(
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
        postmanDataFilterer = new PostmanDataFilterer();
    }

    // TODO maybe add this stuff below (all of it) to our core lib
    /**
     * tests whether or not an Inputstream contains any data or not
     *
     * @param inputStream
     * @throws IOException
     */
    protected boolean isStreamEmpty(final InputStream inputStream) throws IOException {
        try (PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream)) {
            byte[] buffer = new byte[8 * 1024];
            int readBytes = pushbackInputStream.read(buffer);
            if (readBytes > 1) {
                assertThat(readBytes).isAtLeast(1);
                pushbackInputStream.unread(readBytes);

                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * creates a folder in a specified path if there doesn't exist folder yet
     * mkdirs simply does nothing if the folder already exists
     *
     * @param directoryPath
     */
    protected void createFolderIfNotExisting(final String directoryPath) {
        new File(directoryPath).mkdirs();
    }

    /**
     * counts all files in a specified folder no matter the extension
     *
     * @param directoryPath path to the folder to count
     * @return count of all files in a specific folder
     * @throws IOException
     */
    protected long countFilesInDirectory(final String directoryPath) throws IOException {
        long count;
        try (Stream<Path> files = Files.list(Paths.get(directoryPath))) {
            count = files.count();
            return count;
        }
    }

    /**
     * counts the occurrences of a specific file extension in a specified folder
     *
     * @param directoryPath path to the folder to check
     * @param fileExtension the extension to check for and count
     * @return
     */
    protected int countFileOfExtensionInDirectory(final String directoryPath, final String fileExtension) {
        Collection allFoundFiles = FileUtils.listFiles(new File(directoryPath), new String[]{fileExtension}, true);
        return allFoundFiles.size();
    }

    /**
     * counts the filesizes of a directory as bytes
     *
     * @param directoryPath
     * @return
     */
    protected long getFileSizeOfDirectory(final String directoryPath) {
        return FileUtils.sizeOfDirectory(new File(directoryPath));
    }

//    /**
//     * Downloads all inputstreams into a specified folder
//     *
//     * @param IDToInputStreams usually IDs to lists of provided inputstreams
//     * @param outputPath path to the folder to download all files into
//     * @throws IOException
//     */
//    protected void downloadInputStreams(final Map<String, List<InputStream>> IDToInputStreams, final String outputPath) throws IOException {
//        int buffersize = 1024;
//        for (Map.Entry<String, List<InputStream>> entry : IDToInputStreams.entrySet()) {
//            for (InputStream inputStream : entry.getValue()) {
//                DataSetFileDownloadReader reader = new DataSetFileDownloadReader(inputStream);
//                DataSetFileDownload file;
//
//                while ((file = reader.read()) != null) {
//                    InputStream initialStream = file.getInputStream();
//
//                    if (file.getDataSetFile().getFileLength() > 0) {
//                        String[] splitted = file.getDataSetFile().getPath().split("/");
//                        String lastOne = splitted[splitted.length - 1];
//                        OutputStream os = new FileOutputStream(outputPath + File.separator + lastOne);
//                        ProgressBar progressBar = new ProgressBar(lastOne, file.getDataSetFile().getFileLength());
//                        int bufferSize = (file.getDataSetFile().getFileLength() < buffersize) ? (int) file.getDataSetFile().getFileLength() : buffersize;
//                        byte[] buffer = new byte[bufferSize];
//                        int bytesRead;
//                        //read from IS to buffer
//                        while ((bytesRead = initialStream.read(buffer)) != -1) {
//                            progressBar.updateProgress(bufferSize);
//                            os.write(buffer, 0, bytesRead);
//                            os.flush();
//                        }
//
//                        System.out.print("\n");
//                        initialStream.close();
//
//                        os.flush();
//                        os.close();
//                    }
//
//                }
//            }
//        }
//
//    }

    protected static PostmanDataFinder getPostmanDataFinder() {
        return postmanDataFinder;
    }

    protected static PostmanDataDownloaderV3 getPostmanDataDownloaderV3() {
        return postmanDataDownloaderV3;
    }

    protected static PostmanDataDownloaderOldAPI getPostmanDataDownloaderOldAPI() { return postmanDataDownloaderOldAPI; }

    protected static PostmanDataStreamProvider getPostmanDataStreamProvider() {
        return postmanDataStreamProvider;
    }

    protected static PostmanDataFilterer getPostmanDataFilterer() {
        return postmanDataFilterer;
    }
}

