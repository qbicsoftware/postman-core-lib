package life.qbic.util;

import life.qbic.dataLoading.util.StringUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @Test
    public void endsWithIgnoreCase() {
        assertTrue(StringUtil.endsWithIgnoreCase("thisissomerandomTESTBLA", "randomTESTbla"));
        assertFalse(StringUtil.endsWithIgnoreCase("thisissomerandomTESTBLA", "ayyyyynope"));
    }

}
