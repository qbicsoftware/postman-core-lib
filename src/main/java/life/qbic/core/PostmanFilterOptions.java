package life.qbic.core;

import java.util.List;

public class PostmanFilterOptions {

    private List<String> suffixes;
    private List<String> regexPatterns;

    public PostmanFilterOptions(List<String> suffixes, List<String> regexPatterns) {
        this.suffixes = suffixes;
        this.regexPatterns = regexPatterns;
    }

    public List<String> getSuffixes() {
        return suffixes;
    }

    public List<String> getRegexPatterns() {
        return regexPatterns;
    }
}
