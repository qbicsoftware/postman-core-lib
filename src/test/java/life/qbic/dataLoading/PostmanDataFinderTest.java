package life.qbic.dataLoading;

import life.qbic.core.authentication.PostmanConfig;
import life.qbic.core.authentication.PostmanSessionManager;
import life.qbic.io.parser.PostmanPropertiesParser;
import org.junit.BeforeClass;
import org.junit.Test;

public class PostmanDataFinderTest {

    private static PostmanConfig postmanConfig;

    @BeforeClass
    public static void setupBeforeClass() throws Exception {
        postmanConfig = PostmanPropertiesParser.parserProperties("qbicPropertiesFile.conf");
        // openBISAuthentication to OpenBIS
        PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
        postmanSessionManager.loginToOpenBIS(postmanConfig);
        
    }

    @Test
    public void testFindAllDatasetsRecursive() {

    }

    @Test
    public void testFetchDescendantDatasets() {

    }

    @Test
    public void testFindAllRegexFilteredIDs() {

    }

    @Test
    public void testFindAllSuffixFilteredIDs() {

    }


}
