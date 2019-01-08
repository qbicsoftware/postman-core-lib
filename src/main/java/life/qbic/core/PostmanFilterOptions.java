package life.qbic.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for filter options (suffixes, regex, filetype)
 *
 * Commonly passed to the PostmanDataFilterer.
 */
public class PostmanFilterOptions {

    private List<String> suffixes;
    private List<String> regexPatterns;
    private List<String> datasetCodes;
    private String fileType = "";

    public PostmanFilterOptions() {
        this.suffixes = new ArrayList<>();
        this.regexPatterns = new ArrayList<>();
        this.datasetCodes = new ArrayList<>();
    }

    public PostmanFilterOptions(List<String> suffixes,
                                List<String> regexPatterns,
                                List<String> datasetCodes,
                                String fileType) {
        this.suffixes = suffixes;
        this.regexPatterns = regexPatterns;
        this.datasetCodes = datasetCodes;
        this.fileType = fileType;
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

    public List<String> getDatasetCodes() {
        return datasetCodes;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setDatasetCodes(List<String> datasetCodes) {
        this.datasetCodes = datasetCodes;
    }
}
