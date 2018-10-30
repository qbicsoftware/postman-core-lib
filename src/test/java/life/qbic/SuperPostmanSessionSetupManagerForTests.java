package life.qbic;

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

    private static PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
    private static PostmanDataFinder postmanDataFinder;

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
        // openBISAuthentication to OpenBIS
        postmanSessionManager.loginToOpenBIS(postmanConfig);

        postmanDataFinder = new PostmanDataFinder(
                postmanSessionManager.getApplicationServer(),
                postmanSessionManager.getDataStoreServer(),
                postmanSessionManager.getSessionToken()
        );
    }

    protected static PostmanDataFinder getPostmanDataFinder() {
        return postmanDataFinder;
    }
}

