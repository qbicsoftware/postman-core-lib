package life.qbic.util

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria

class RegexFilterUtil {

    /**
     * Using dollar slashy regex of groovy to match all provided regexes to filter the dataset files
     *
     * @param regexPatterns
     * @param allDatasets
     * @param dataStoreServer
     * @param sessionToken
     * @return all fileIDs matching the regex
     */
    static List<DataSetFilePermId> findAllRegexFilteredIDsGroovy(List<String> regexPatterns,
                                                                 List<DataSet> allDatasets,
                                                                 IDataStoreServerApi dataStoreServer,
                                                                 String sessionToken) {
        def allFileIDs = new ArrayList<>()

        for (DataSet ds : allDatasets) {
            // we cannot access the files directly of the datasets -> we need to query for the files first using the datasetID
            DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria()
            criteria.withDataSet().withCode().thatEquals(ds.getCode())
            SearchResult<DataSetFile> result = dataStoreServer.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions())
            List<DataSetFile> files = result.getObjects()

            def fileIds = new ArrayList<>()

            // only add to the result if regex matches
            for (DataSetFile file : files)
            {
                for (String regex : regexPatterns) {
                    def fullRegex = $/$regex/$
                    def matched = file.getPermId().toString() =~ fullRegex

                    if (matched) {
                        fileIds.add(file.getPermId())
                    }
                }
            }

            allFileIDs.addAll(fileIds)
        }

        return allFileIDs
    }

    /**
     * Using dollar slashy regex of groovy to match all provided regex to filter the permIDs
     *
     * @param permIDs
     * @param regexPatterns
     * @return all permIDs matching the regex
     */
    static List<DataSetFilePermId> filterPermIDRegex(List<DataSetFilePermId> permIDs,
                                                     List<String> regexPatterns) {
        def regexFilteredPermIDs = new ArrayList()

        for (DataSetFilePermId permID : permIDs) {
            for (String regex : regexPatterns) {
                def fullRegex = $/$regex/$
                def matched = permID.toString() =~ fullRegex

                if (matched) {
                    regexFilteredPermIDs.add(permID)
                }
            }
        }

        return regexFilteredPermIDs
    }

    static List<DataSetFile> filterDataSetFilesRegex(List<DataSetFile> dataSetFiles,
                                                     List<String> regexPatterns) {
        def regexFilteredDataSetFiles = new ArrayList()

        for (DataSetFile dataSetFile : dataSetFiles) {
            for (String regex : regexPatterns) {
                def fullRegex = $/$regex/$
                def matched = dataSetFile.getPermId().toString() =~ fullRegex

                if (matched) {
                    regexFilteredDataSetFiles.add(dataSetFile)
                }
            }
        }

        return regexFilteredDataSetFiles
    }

}

