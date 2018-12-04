package life.qbic;

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

import java.io.IOException;

import static org.junit.Assert.*;

public class SuperPostmanSessionSetupManagerForIntegrationTestsIT {

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

    @Test
    public void testIsStreamNotEmpty() {

    }

    @Test
    public void testCreateFolderIfNotExisting() {

    }

    @Test
    public void testCountFilesInDirectory() {

    }

    @Test
    public void testCountFileOfExtensionInDirectory() {

    }

    @Test
    public void testGetFileSizeOfDirectory() {

    }

    @Test
    @Ignore // for now this is not needed
    public void testDownloadInputStreams() {

    }
}
