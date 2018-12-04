package life.qbic.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides string utility functions.
 */
public final class StringUtil {

    private final static Logger LOG = LogManager.getLogger(StringUtil.class);

    private StringUtil() {
        throw new AssertionError("Instantiating StringUtil class");
    }

    /**
     * checks whether or not a string ends with a passed suffix case sensitive
     *
     * @param str the string that is being checked
     * @param suffix suffix to check for
     * @return true if it str ends with suffix case insensitive, false otherwise
     */
    public static boolean endsWithIgnoreCase(final String str, final String suffix) {
        final int suffixLength = suffix.length();
        return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
    }
}
