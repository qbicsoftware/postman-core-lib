package life.qbic;

import jdk.internal.util.xml.impl.Input;
import life.qbic.core.authentication.PostmanConfig;
import life.qbic.core.authentication.PostmanSessionManager;
import life.qbic.dataLoading.PostmanDataDownloader;
import life.qbic.dataLoading.PostmanDataFilterer;
import life.qbic.dataLoading.PostmanDataFinder;
import life.qbic.dataLoading.PostmanDataStreamProvider;
import life.qbic.exceptions.PostmanOpenBISLoginFailedException;
import life.qbic.io.parser.PostmanPropertiesParser;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class SuperPostmanSessionSetupManagerForIntegrationTestsIT {

    private SuperPostmanSessionSetupManagerForIntegrationTests postmanSessionSetupManagerForIntegrationTests = new SuperPostmanSessionSetupManagerForIntegrationTests();
    private final String DOWNLOADED_FILES_OUTPUT_PATH = "src/test/ITOutput/superPostmanSessionSetupManagerForIntegrationTestsTest";

    private static PostmanSessionManager postmanSessionManager;
    private static PostmanDataFinder postmanDataFinder;
    private static PostmanDataDownloader postmanDataDownloader;
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
        postmanDataDownloader = new PostmanDataDownloader(
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
        postmanDataStreamProvider = new PostmanDataStreamProvider(
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
        postmanDataFilterer = new PostmanDataFilterer();
    }

    /**
     * does connection exist after logging in?
     */
    @Test
    public void testLoggedIn() {
        assertTrue(postmanSessionManager.getApplicationServer().isSessionActive(postmanSessionManager.getSessionToken()));
        assertEquals(postmanSessionManager.getPostmanSessionManagerStatus(), PostmanSessionManager.PostmanSessionManagerStatus.LOGGED_IN);
    }

    /**
     * is connection closed after logging out?
     */
    @Test
    public void testLogout() {
        assertTrue(postmanSessionManager.getApplicationServer().isSessionActive(postmanSessionManager.getSessionToken()));
        assertEquals(postmanSessionManager.getPostmanSessionManagerStatus(), PostmanSessionManager.PostmanSessionManagerStatus.LOGGED_IN);
        postmanSessionManager.logoutFromOpenBIS();
        assertEquals(postmanSessionManager.getPostmanSessionManagerStatus(), PostmanSessionManager.PostmanSessionManagerStatus.LOGGED_OUT);
    }

    /**
     * tests two sample inputstreams - one not empty, the other one being empty
     *
     * @throws IOException
     */
    @Test
    public void testIsStreamNotEmpty() throws IOException {
        final String nonEmptyString = "gfnxdfgloys";
        final InputStream nonEmptyInputStream = new ByteArrayInputStream(nonEmptyString.getBytes());

        assertFalse(postmanSessionSetupManagerForIntegrationTests.isStreamEmpty(nonEmptyInputStream));

        final String emptyString = "";
        final InputStream emptyInputStream = new ByteArrayInputStream(emptyString.getBytes());

        assertTrue(postmanSessionSetupManagerForIntegrationTests.isStreamEmpty(emptyInputStream));
    }

    /**
     * attempts to create folder in a specified directory
     * attempts to create a folder over an already existing folder
     *
     */
    @Test
    public void testCreateFolderIfNotExisting() {
        final String sampleFilePath = DOWNLOADED_FILES_OUTPUT_PATH + "/testCreateFolderIfNotExisting";

        postmanSessionSetupManagerForIntegrationTests.createFolderIfNotExisting(sampleFilePath);
        assertTrue(new File(sampleFilePath).exists());

        // file exists now - lets try again
        postmanSessionSetupManagerForIntegrationTests.createFolderIfNotExisting(sampleFilePath);
        assertTrue(new File(sampleFilePath).exists());
    }

    /**
     * verifies the number of files count matches of a prespecified directory
     *
     */
    @Test
    public void testCountFilesInDirectory() throws IOException {
        final String pathToTest = DOWNLOADED_FILES_OUTPUT_PATH + "/testCountFilesInDirectory";

        assertEquals(postmanSessionSetupManagerForIntegrationTests.countFilesInDirectory(pathToTest), 4);
    }

    /**
     * verifies that the file extensions of a prespecified directory match
     */
    @Test
    public void testCountFileOfExtensionInDirectory() {
        final String pathToTest = DOWNLOADED_FILES_OUTPUT_PATH + "/testCountFileOfExtensionInDirectory";

        assertEquals(postmanSessionSetupManagerForIntegrationTests.countFileOfExtensionInDirectory(pathToTest, "pdf"), 0);
        assertEquals(postmanSessionSetupManagerForIntegrationTests.countFileOfExtensionInDirectory(pathToTest, "txt"), 2);
        assertEquals(postmanSessionSetupManagerForIntegrationTests.countFileOfExtensionInDirectory(pathToTest, "json"), 1);
        assertEquals(postmanSessionSetupManagerForIntegrationTests.countFileOfExtensionInDirectory(pathToTest, "xml"), 1);
        assertEquals(postmanSessionSetupManagerForIntegrationTests.countFileOfExtensionInDirectory(pathToTest, "png"), 0);
    }

    /**
     *
     */
    @Test
    public void testGetFileSizeOfDirectory() {
        final String pathToTest = DOWNLOADED_FILES_OUTPUT_PATH + "/testGetFileSizeOfDirectory";
        
        assertEquals(postmanSessionSetupManagerForIntegrationTests.getFileSizeOfDirectory(pathToTest), 716);
    }

    @Test
    @Ignore // for now this is not needed
    public void testDownloadInputStreams() {

    }
}
