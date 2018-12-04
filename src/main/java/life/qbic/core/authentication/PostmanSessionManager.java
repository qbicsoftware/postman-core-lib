package life.qbic.core.authentication;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import life.qbic.exceptions.OpenBISAuthenticationFailedException;
import life.qbic.exceptions.PostmanOpenBISLoginFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton to provide access to OpenBIS
 * Provides login and logout
 */
public class PostmanSessionManager {

    /**
     * enum specifying the current status of PostmanSessionManager
     */
    public enum PostmanSessionManagerStatus {
        LOGGED_IN,
        LOGGED_OUT
    }

    private final static Logger LOG = LogManager.getLogger(PostmanSessionManager.class);

    private static final PostmanSessionManager POSTMAN_SESSION_MANAGER = new PostmanSessionManager();
    private IApplicationServerApi applicationServer;
    private IDataStoreServerApi dataStoreServer;
    private String sessionToken;
    private int buffersize;
    private PostmanSessionManagerStatus postmanSessionManagerStatus;

    /**
     * private, since we don't want any further initialization to happen -> singleton pattern is used
     */
    private PostmanSessionManager() {

    }

    public static PostmanSessionManager getPostmanSessionManager() {
        return POSTMAN_SESSION_MANAGER;
    }

    /**
     * logs into OpenBIS
     * sets all properties of the current PostmanSessionManager singleton
     *
     * @param postmanConfig either created by commandline parameters or after having read a properties file
     * @throws PostmanOpenBISLoginFailedException if login failed in any way
     */
    public void loginToOpenBIS(PostmanConfig postmanConfig) throws PostmanOpenBISLoginFailedException {
        PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
        postmanSessionManager.setApplicationServer(postmanConfig.getAs_url());
        postmanSessionManager.setDataStoreServer(postmanConfig.getDss_url());

        // authenticate at openBIS and verify
        try {
            postmanSessionManager.openBISAuthentication(
                    postmanSessionManager.getApplicationServer(),
                    postmanConfig.getUsername(), postmanConfig.getPassword());
            LOG.info("OpenBIS connection established successfully!");
        } catch (OpenBISAuthenticationFailedException e) {
            LOG.error("Connection to openBIS failed.");
            throw new PostmanOpenBISLoginFailedException("Connection to openBIS failed");
        }

        postmanSessionManagerStatus = PostmanSessionManagerStatus.LOGGED_IN;

        LOG.info("Connection to openBIS was successful.");
    }

    /**
     * provides login for openBIS authentication
     * uses username and password to login to OpenBIS
     * @param applicationServer
     * @param user
     * @param password
     * @throws OpenBISAuthenticationFailedException
     */
    private void openBISAuthentication(IApplicationServerApi applicationServer, String user, String password) throws OpenBISAuthenticationFailedException {
        try {
            String sessionTokenReturned = applicationServer.login(user, password);
            applicationServer.getSessionInformation(sessionTokenReturned);

            sessionToken = sessionTokenReturned;
        } catch (Exception err) {
            LOG.debug(err);

            sessionToken = "";
            throw new OpenBISAuthenticationFailedException("Authentication for OpenBIS failed! Setting sessionToken to empty string");
        }
    }

    /**
     * logs out from OpenBIS
     * resets the sessiontoken -> null
     */
    public void logoutFromOpenBIS() {
        if (applicationServer.isSessionActive(sessionToken)) {
            applicationServer.logout(sessionToken);
            sessionToken = null;
            postmanSessionManagerStatus = PostmanSessionManagerStatus.LOGGED_OUT;
        }
    }

    /**
     * creates an ApplicationServer for the passed url
     * sets the ApplicationServer of the current PostmanSessionManager singleton
     *
     * @param applicationServerURL url to create ApplicationServer for
     */
    private void setApplicationServer(String applicationServerURL) {
        if (!applicationServerURL.isEmpty()) {
            applicationServer = HttpInvokerUtils.createServiceStub(
                    IApplicationServerApi.class,
                    applicationServerURL + IApplicationServerApi.SERVICE_URL, 10000);
        } else {
            applicationServer = null;
        }
    }

    /**
     * creates a DataStoreServer for the passed url
     * sets the DataStoreServer for the current PostmanSessionManager singleton
     *
     * @param dataStoreServerURL url to create DataStoreServer for
     */
    private void setDataStoreServer(String dataStoreServerURL) {
        if (!dataStoreServerURL.isEmpty()) {
            dataStoreServer = HttpInvokerUtils.createStreamSupportingServiceStub(
                    IDataStoreServerApi.class,
                    dataStoreServerURL + IDataStoreServerApi.SERVICE_URL, 10000);
        } else {
            dataStoreServer = null;
        }
    }

    public IApplicationServerApi getApplicationServer() {
        return applicationServer;
    }

    public IDataStoreServerApi getDataStoreServer() {
        return dataStoreServer;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public int getBuffersize() {
        return buffersize;
    }

    public void setBuffersize(int buffersize) {
        this.buffersize = buffersize;
    }

    public PostmanSessionManagerStatus getPostmanSessionManagerStatus() {
        return postmanSessionManagerStatus;
    }
}

