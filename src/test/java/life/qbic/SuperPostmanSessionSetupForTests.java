package life.qbic;

import life.qbic.Exceptions.PostmanOpenBISLoginFailedException;
import life.qbic.core.authentication.PostmanConfig;
import life.qbic.core.authentication.PostmanSessionManager;
import life.qbic.dataLoading.PostmanDataFinder;
import life.qbic.io.parser.PostmanPropertiesParser;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.io.IOException;

@Ignore
public class SuperPostmanSessionSetupForTests {

    private static PostmanConfig postmanConfig;
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
        postmanConfig = PostmanPropertiesParser.parserProperties("qbicPropertiesFile.conf");
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

