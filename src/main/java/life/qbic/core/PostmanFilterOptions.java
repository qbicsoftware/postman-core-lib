package life.qbic.core;

import java.util.ArrayList;
import java.util.List;

public class PostmanFilterOptions {

    private List<String> suffixes;
    private List<String> regexPatterns;
    private String filterType = "";

    public PostmanFilterOptions() {
        this.suffixes = new ArrayList<>();
        this.regexPatterns = new ArrayList<>();
    }

    public PostmanFilterOptions(List<String> suffixes, List<String> regexPatterns, String filterType) {
        this.suffixes = suffixes;
        this.regexPatterns = regexPatterns;
        this.filterType = filterType;
    }

    public List<String> getSuffixes() {
        return suffixes;
    }

    public List<String> getRegexPatterns() {
        return regexPatterns;
    }

    public void setSuffixes(List<String> suffixes) {
        this.suffixes = suffixes;
    }

    public void setRegexPatterns(List<String> regexPatterns) {
        this.regexPatterns = regexPatterns;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
}
