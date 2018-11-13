package life.qbic;

import life.qbic.dataLoading.PostmanDataDownloader;
import life.qbic.dataLoading.PostmanDataStreamProvider;
import life.qbic.exceptions.PostmanOpenBISLoginFailedException;
import life.qbic.core.authentication.PostmanConfig;
import life.qbic.core.authentication.PostmanSessionManager;
import life.qbic.dataLoading.PostmanDataFinder;
import life.qbic.io.parser.PostmanPropertiesParser;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Super class for all tests which require A PostmanSession
 * Furthermore, objects for all DataLoading operations are created and provided
 */
public class SuperPostmanSessionSetupManagerForTestsIT {

    private static PostmanSessionManager postmanSessionManager;
    private static PostmanDataFinder postmanDataFinder;
    private static PostmanDataDownloader postmanDataDownloader;
    private static PostmanDataStreamProvider postmanDataStreamProvider;

    /**
     * setups PostmanSessionManager
     * logs into openBIS
     *
     * @throws IOException if unable to parse Properties
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
        postmanDataDownloader = new PostmanDataDownloader(
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
        postmanDataStreamProvider = new PostmanDataStreamProvider(
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
    }

    /**
     * does connection exist after logging in?
     */
    @Test
    public void testLoggedIn() {
        assertTrue(postmanSessionManager.getApplicationServer().isSessionActive(postmanSessionManager.getSessionToken()));
    }

    /**
     * is connection closed after logging out?
     */
    @Test
    public void testLogout() {
        assertTrue(postmanSessionManager.getApplicationServer().isSessionActive(postmanSessionManager.getSessionToken()));
        postmanSessionManager.getApplicationServer().logout(postmanSessionManager.getSessionToken());
        assertFalse(postmanSessionManager.getApplicationServer().isSessionActive(postmanSessionManager.getSessionToken()));
    }

    // TODO maybe add this stuff to our core lib


    protected void downloadInputStream(final InputStream inputStream, final String outputPath) throws IOException {
        final String outputFile = outputPath + "/bla";
        byte[] buffer = new byte[8 * 1024];

        try {
            OutputStream output = new FileOutputStream(outputFile);
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
    }

    protected static void createFolderIfNotExisting(final String directoryPath) {
        new File(directoryPath).mkdirs();
    }

    protected static long countFilesInDirectory(final String directoryPath) throws IOException {
        long count;
        try (Stream<Path> files = Files.list(Paths.get(directoryPath))) {
            count = files.count();
            return count;
        }
    }

    protected static int countFileOfExtensionInDirectory(final String directoryPath, final String fileExtension) {
        Collection allFoundFiles = FileUtils.listFiles(new File(directoryPath), new String[]{fileExtension}, true);
        return allFoundFiles.size();
    }

    protected static long getFileSizeOfDirectory(final String directoryPath) {
        return FileUtils.sizeOfDirectory(new File(directoryPath));
    }

    protected static PostmanDataFinder getPostmanDataFinder() {
        return postmanDataFinder;
    }

    protected static PostmanDataDownloader getPostmanDataDownloader() {
        return postmanDataDownloader;
    }

    protected static PostmanDataStreamProvider getPostmanDataStreamProvider() {
        return postmanDataStreamProvider;
    }


}

