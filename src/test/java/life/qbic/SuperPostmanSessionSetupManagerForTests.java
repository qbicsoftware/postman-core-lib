package life.qbic;

import life.qbic.dataLoading.PostmanDataDownloader;
import life.qbic.dataLoading.PostmanDataStreamProvider;
import life.qbic.exceptions.PostmanOpenBISLoginFailedException;
import life.qbic.core.authentication.PostmanConfig;
import life.qbic.core.authentication.PostmanSessionManager;
import life.qbic.dataLoading.PostmanDataFinder;
import life.qbic.io.parser.PostmanPropertiesParser;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.io.IOException;

/**
 * Super class for all tests which require A PostmanSession
 * Furthermore, objects for all DataLoading operations are created and provided
 */
@Ignore
public class SuperPostmanSessionSetupManagerForTests {

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
        PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
        postmanSessionManager.loginToOpenBIS(postmanConfig);

        // create all dataloading objects
        postmanDataFinder = new PostmanDataFinder(
                postmanSessionManager.getApplicationServer(),
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
        postmanDataDownloader = new PostmanDataDownloader(
                postmanSessionManager.getApplicationServer(),
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
        postmanDataStreamProvider = new PostmanDataStreamProvider(
                postmanSessionManager.getApplicationServer(),
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
    }

    protected static PostmanDataFinder getPostmanDataFinder() {
        return postmanDataFinder;
    }

    public static PostmanDataDownloader getPostmanDataDownloader() {
        return postmanDataDownloader;
    }

    public static PostmanDataStreamProvider getPostmanDataStreamProvider() {
        return postmanDataStreamProvider;
    }
}

