package life.qbic.util

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria

class RegexFilterDownloadUtil {

    /**
     * Using dollar slashy regex of groovy to match all provided regexes to filter the dataset files
     *
     * @param regexPatterns
     * @param allDatasets
     * @param dataStoreServer
     * @param sessionToken
     * @return all fileIDs which are forwarded to download
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

}

