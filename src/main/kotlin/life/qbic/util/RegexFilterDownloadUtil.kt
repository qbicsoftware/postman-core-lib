package life.qbic.util

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria

/**
 * Uses the Kotlin regex implementation to match all provided [regexPatterns] to filter [allDatasets]
 */
fun findAllRegexFilteredIDsKotlin(regexPatterns: List<String>,
                                  allDatasets: List<DataSet>,
                                  dataStoreServer: IDataStoreServerApi,
                                  sessionToken: String): List<IDataSetFileId> {
    val allFileIDs = ArrayList<DataSetFilePermId>()

    for (ds in allDatasets) {
        // we cannot access the files directly of the datasets -> we need to query for the files first using the datasetID
        val criteria = DataSetFileSearchCriteria()
        criteria.withDataSet().withCode().thatEquals(ds.code)
        val searchResult = dataStoreServer.searchFiles(sessionToken, criteria, DataSetFileFetchOptions())
        val foundFiles = searchResult.objects

        val fileIDs = ArrayList<DataSetFilePermId>()

        // remove everything that doesn't match the regex -> only add if regex matches
        for (file in foundFiles) {
            for (regex in regexPatterns) {
                if (containsMatch(regex, file.permId.toString())) {
                    fileIDs.add(file.permId)
                }
            }
        }

        allFileIDs.addAll(fileIDs)
    }

    return allFileIDs
}

fun containsMatch(pattern: String, apply: String) : Boolean{
    val escapedPattern = """$pattern""".toRegex()
    return (escapedPattern.containsMatchIn(apply))
}


