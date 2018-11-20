package life.qbic.core.authentication;

import life.qbic.exceptions.PostmanOpenBISLoginFailedException;
import life.qbic.SuperPostmanSessionSetupManagerForIntegrationTestsIT;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Contains tests related to session managment
 * starts logged in, since it extends SuperPostmanSessionSetupManager
 */
public class PostmanSessionManagerIT extends SuperPostmanSessionSetupManagerForIntegrationTestsIT {

    private static PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();

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

    /**
     * is sessionToken set after having logged in?
     * is sessionToken null after having logged out?
     * is connection for former sessionToken closed after having logged out?
     * //TODO It's a bad idea to recreate setup -> I should modify this test
     */
    @Test
    public void testSessionToken() {
        assertNotNull(postmanSessionManager.getSessionToken());
        assertTrue(postmanSessionManager.getApplicationServer().isSessionActive(postmanSessionManager.getSessionToken()));
        String old_sessiontoken = postmanSessionManager.getSessionToken();
        postmanSessionManager.logoutFromOpenBIS();
        assertNull(postmanSessionManager.getSessionToken());
        assertFalse(postmanSessionManager.getApplicationServer().isSessionActive(old_sessiontoken));

        // since the order of execution is NOT guaranteed in junit we need to login again for other tests
        try {
            setupBeforeClass();
        } catch (IOException | PostmanOpenBISLoginFailedException e) {
            e.printStackTrace();
        }
    }
}
