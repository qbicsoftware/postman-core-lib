package life.qbic.io.parser;

import life.qbic.core.authentication.PostmanConfig;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class PostmanPropertiesParserTest {

    @Test
    public void parseDummyProperties() {
        try {
            PostmanConfig postmanConfig = PostmanPropertiesParser.parserProperties("dummy_properties");
            assertEquals("qbicssuperprogrammer2000", postmanConfig.getUsername());
            assertEquals("harrypotter", postmanConfig.getPassword());
            assertEquals("https://superfancywebsite.com/openbis/as", postmanConfig.getAs_url());
            assertEquals("https://evenbetterwebsite.com:444/dss", postmanConfig.getDss_url());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
