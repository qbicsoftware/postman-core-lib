package life.qbic.io.parser;

import life.qbic.core.authentication.PostmanConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides the parsing of properties files containing information to create a PostmanConfig.
 */
public class PostmanPropertiesParser {

    private PostmanPropertiesParser() {

    }

    /**
     * parses a properties file and returns a PostmanConfig
     *
     * @param propertiesFilepath the properties file path
     * @return PostmanConfig build using the properties file
     * @throws IOException
     */
    public static PostmanConfig parserProperties(final String propertiesFilepath) throws IOException {
        try {
            Properties properties = new Properties();

            InputStream inputStream = PostmanConfig.class.getClassLoader().getResourceAsStream(propertiesFilepath);
            if (inputStream != null) {
                properties.load(inputStream);
            }

            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String as_url = properties.getProperty("as_url");
            String dss_url = properties.getProperty("dss_url");

            return new PostmanConfig(username, password, as_url, dss_url);
        } catch (IOException e) {
            throw new FileNotFoundException("property file '" + propertiesFilepath + "' not found in the classpath");
        }
    }

}
