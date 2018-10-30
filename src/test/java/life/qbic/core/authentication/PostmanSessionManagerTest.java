package life.qbic.core.authentication;

import life.qbic.io.parser.PostmanPropertiesParser;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Contains tests related to session managment
 */
public class PostmanSessionManagerTest {

    private static PostmanConfig postmanConfig;
    private static PostmanSessionManager  postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
    @BeforeClass
    public static void setupBeforeClass() throws Exception {
        postmanConfig = PostmanPropertiesParser.parserProperties("qbicPropertiesFile.conf");
        // openBISAuthentication to OpenBIS
        postmanSessionManager.loginToOpenBIS(postmanConfig);
    }

    /**
     * tests whether or not a connection exists after logging in
     */
    @Test
    public void testLogin() {
        assertTrue(postmanSessionManager.getApplicationServer().isSessionActive(postmanSessionManager.getSessionToken()));
    }

    @Test
    public void testLogout() {
        postmanSessionManager.getApplicationServer().logout(postmanSessionManager.getSessionToken());
        assertFalse(postmanSessionManager.getApplicationServer().isSessionActive(postmanSessionManager.getSessionToken()));
    }
}
