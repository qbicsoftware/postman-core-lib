package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.util.RegexFilterUtil;
import life.qbic.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PostmanDataFilterer {

    private final static Logger LOG = LogManager.getLogger(PostmanDataFilterer.class);

    public PostmanDataFilterer() {
    }

    /**
     * filters permIDs by suffix and/or regex patterns
     * all suffix matching and all regex matching permIDs are added!
     * already suffix filtered permIDs are NOT subsequently regex filtered
     *
     * @param permIDs permIDs which get filtered
     * @param postmanFilterOptions contains suffixes and/or regexes which are filtered for
     * @return filtered permIDs by suffix and/or regex patterns
     */
    public List<DataSetFilePermId> filterPermIDs(final List<DataSetFilePermId> permIDs, final PostmanFilterOptions postmanFilterOptions) {
        final List<DataSetFilePermId> filteredPermIDs = new ArrayList<>();

        // suffix was provided -> filter by suffix
        if (!postmanFilterOptions.getSuffixes().isEmpty()) {
            filteredPermIDs.addAll(filterPermIDsBySuffix(permIDs, postmanFilterOptions.getSuffixes()));
        }

        // regex was provided -> filter by regex
        if (!postmanFilterOptions.getRegexPatterns().isEmpty()) {
            filteredPermIDs.addAll(filterPermIDsByRegex(permIDs, postmanFilterOptions.getRegexPatterns()));
        }

        if (postmanFilterOptions.getSuffixes().isEmpty() && postmanFilterOptions.getRegexPatterns().isEmpty()) {
            LOG.warn("Attempted to filter permIDs with empty lists of suffixes and regexes!");
        }

        return filteredPermIDs;
    }

    /**
     * filters permIDs by suffixes
     *
     * @param permIDs permIDs which get filtered
     * @param suffixes list of suffixes which are filtered for
     * @return filtered permIDs by suffixes
     */
    public List<DataSetFilePermId> filterPermIDsBySuffix(final List<DataSetFilePermId> permIDs, final List<String> suffixes) {
        final List<DataSetFilePermId> filteredPermIDs = new ArrayList<>();

        if (!suffixes.isEmpty()) {
            suffixes.forEach(suffix -> permIDs.stream()
                    .filter(ID -> StringUtil.endsWithIgnoreCase(ID.toString(), suffix))
                    .forEachOrdered(filteredPermIDs::add));
        } else {
            LOG.warn("Attempted to filter PermIDs with empty list of suffixes");
        }

        return filteredPermIDs;
    }

    /**
     * filters permIDs by regexes
     *
     * @param permIDs permIDs which get filtered
     * @param regexes list of regexes which are filtered for
     * @return filtered permIDs by regex patterns
     */
    public List<DataSetFilePermId> filterPermIDsByRegex(final List<DataSetFilePermId> permIDs, final List<String> regexes) {
        final List<DataSetFilePermId> filteredPermIDs = new ArrayList<>();

        if (!regexes.isEmpty()) {
            filteredPermIDs.addAll(RegexFilterUtil.filterPermIDRegex(permIDs, regexes));
        } else {
            LOG.warn("Attempted to filter PermIDs with empty list of regexes");
        }

        return filteredPermIDs;
    }

    /**
     * filters dataSetFiles by suffix and/or regex patterns
     * all suffix matching and all regex matching permIDs are added!
     * already suffix filtered permIDs are NOT subsequently regex filtered
     *
     * @param dataSetFiles dataSetFiles which permIDs are filtered for
     * @param postmanFilterOptions contains suffixes and/or regexes which are filtered for
     * @return filtered dataSetFiles by suffix and/or regex patterns
     */
    public List<DataSetFile> filterDataSetFiles(final List<DataSetFile> dataSetFiles, final PostmanFilterOptions postmanFilterOptions) {
        List<DataSetFile> filteredDataSets = new ArrayList<>();

        // suffix was provided -> filter by suffix
        if (!postmanFilterOptions.getSuffixes().isEmpty()) {
            filteredDataSets.addAll(filterDataSetFilesBySuffix(dataSetFiles, postmanFilterOptions.getSuffixes()));
        }

        // regex was provided -> filter by regex
        if (!postmanFilterOptions.getRegexPatterns().isEmpty()) {
            filteredDataSets.addAll(RegexFilterUtil.filterDataSetFilesRegex(dataSetFiles, postmanFilterOptions.getRegexPatterns()));
        }

        if (postmanFilterOptions.getSuffixes().isEmpty() && postmanFilterOptions.getRegexPatterns().isEmpty()) {
            LOG.warn("Attempted to filter permIDs with empty lists of suffixes and regexes!");
        }

        return filteredDataSets;
    }

    /**
     * filters dataSetFiles' permIDs by suffixes
     *
     * @param dataSetFiles dataSetFiles which permIDs are filtered for
     * @param suffixes suffixes which are filtered for
     * @return suffix filtered dataSetFiles
     */
    public List<DataSetFile> filterDataSetFilesBySuffix(final List<DataSetFile> dataSetFiles, final List<String> suffixes) {
        List<DataSetFile> filteredDataSets = new ArrayList<>();

        if (!suffixes.isEmpty()) {
            suffixes.forEach(suffix -> dataSetFiles.stream()
                    .filter(dataSetFile -> StringUtil.endsWithIgnoreCase(dataSetFile.getPermId().toString(), suffix))
                    .forEachOrdered(filteredDataSets::add));
        }

        return filteredDataSets;
    }

    /**
     * filters dataSetFiles' permIDs by regex patterns
     * @param dataSetFiles dataSetFiles which permIDs are filtered for
     * @param regexPatterns regex patterns which are filtered for
     * @return regex filtered dataSetFiles
     */
    public List<DataSetFile> filterDataSetFilesByRegex(final List<DataSetFile> dataSetFiles, final List<String> regexPatterns) {
        List<DataSetFile> filteredDataSets = new ArrayList<>();

        if (!regexPatterns.isEmpty()) {
            filteredDataSets.addAll(RegexFilterUtil.filterDataSetFilesRegex(dataSetFiles, regexPatterns));
        }

        return filteredDataSets;
    }

}
