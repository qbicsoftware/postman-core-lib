package life.qbic.core.authentication;

/**
 * Container used to define all required information for logging into openBIS.
 *
 * Commonly passed to the PostmanSessionManager to login to openBIS.
 */
public class PostmanConfig {

    private String as_url;
    private String dss_url;
    private String username;
    private String password;

    public PostmanConfig(String username, String password, String as_url, String dss_url) {
        this.as_url = as_url;
        this.dss_url = dss_url;
        this.username = username;
        this.password = password;
    }

    public String getAs_url() {
        return as_url;
    }

    public String getDss_url() {
        return dss_url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "PostmanConfig{" +
                "as_url='" + as_url + '\'' +
                ", dss_url='" + dss_url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
