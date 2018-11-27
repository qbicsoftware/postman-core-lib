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

    public List<DataSetFilePermId> filterPermIDs(final List<DataSetFilePermId> permIDs, final PostmanFilterOptions postmanFilterOptions) {
        final List<DataSetFilePermId> filteredPermIDs = new ArrayList<>();

        // suffix was provided
        if (!postmanFilterOptions.getSuffixes().isEmpty()) {
            filteredPermIDs.addAll(filterPermIDsBySuffix(permIDs, postmanFilterOptions.getSuffixes()));
        }

        // regex was provided
        if (!postmanFilterOptions.getRegexPatterns().isEmpty()) {
            filteredPermIDs.addAll(filterPermIDsByRegex(permIDs, postmanFilterOptions.getRegexPatterns()));
        }

        return filteredPermIDs;
    }

    public List<DataSetFilePermId> filterPermIDsBySuffix(final List<DataSetFilePermId> permIDs, final List<String> suffixes) {
        final List<DataSetFilePermId> filteredPermIDs = new ArrayList<>();

        // suffix was provided
        if (!suffixes.isEmpty()) {
            suffixes.forEach(suffix -> {
                permIDs.stream()
                        .filter(ID -> StringUtil.endsWithIgnoreCase(ID.toString(), suffix))
                        .forEachOrdered(filteredPermIDs::add);
            });
        } else {
            LOG.warn("Attempted to filter PermIDs with empty list of suffixes");
        }

        return filteredPermIDs;
    }

    public List<DataSetFilePermId> filterPermIDsByRegex(final List<DataSetFilePermId> permIDs, final List<String> regexes) {
        final List<DataSetFilePermId> filteredPermIDs = new ArrayList<>();

        // regex was provided
        if (!regexes.isEmpty()) {
            filteredPermIDs.addAll(RegexFilterUtil.filterPermIDRegex(permIDs, regexes));
        } else {
            LOG.warn("Attempted to filter PermIDs with empty list of regexes");
        }

        return filteredPermIDs;
    }

    public List<DataSetFile> filterDataSetFiles(final List<DataSetFile> dataSetFiles, final PostmanFilterOptions postmanFilterOptions) {
        List<DataSetFile> filteredDataSets = new ArrayList<>();

        // suffix was provided
        if (!postmanFilterOptions.getSuffixes().isEmpty()) {
            filteredDataSets.addAll(filterDataSetFilesBySuffix(dataSetFiles, postmanFilterOptions.getSuffixes()));
        }

        // regex was provided
        if (!postmanFilterOptions.getRegexPatterns().isEmpty()) {
            filteredDataSets.addAll(RegexFilterUtil.filterDataSetFilesRegex(dataSetFiles, postmanFilterOptions.getRegexPatterns()));
        }

        return filteredDataSets;
    }

    public List<DataSetFile> filterDataSetFilesBySuffix(final List<DataSetFile> dataSetFiles, final List<String> suffixes) {
        List<DataSetFile> filteredDataSets = new ArrayList<>();

        if (!suffixes.isEmpty()) {
            suffixes.forEach(suffix -> {
                dataSetFiles.stream()
                        .filter(dataSetFile -> StringUtil.endsWithIgnoreCase(dataSetFile.getPermId().toString(), suffix))
                        .forEachOrdered(filteredDataSets::add);
            });
        }

        return filteredDataSets;
    }

    public List<DataSetFile> filterDataSetFilesByRegex(final List<DataSetFile> dataSetFiles, final List<String> regexPatterns) {
        List<DataSetFile> filteredDataSets = new ArrayList<>();

        if (!regexPatterns.isEmpty()) {
            filteredDataSets.addAll(RegexFilterUtil.filterDataSetFilesRegex(dataSetFiles, regexPatterns));
        }

        return filteredDataSets;
    }

}
