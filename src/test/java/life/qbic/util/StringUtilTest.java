package life.qbic.util;

import life.qbic.testConfigurations.Fast;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({Test.class, Fast.class})
public class StringUtilTest {

    @Test
    public void endsWithIgnoreCase() {
        assertTrue(StringUtil.endsWithIgnoreCase("thisissomerandomTESTBLA", "randomTESTbla"));
        assertFalse(StringUtil.endsWithIgnoreCase("thisissomerandomTESTBLA", "ayyyyynope"));
    }

}
