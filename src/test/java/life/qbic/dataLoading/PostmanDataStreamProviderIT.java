package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.SuperPostmanSessionSetupManagerForIntegrationTests;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.testConfigurations.IntegrationTest;
import life.qbic.testConfigurations.Slow;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO SOMETHING IS VERY WRONG HERE
 * All the tests in this class run manually and solely isolated - they are running fine
 * Running the whole test class however leads to tests being stuck at the downloadFiles method of openBIS!
 */

@Category({IntegrationTest.class, Slow.class})
public class PostmanDataStreamProviderIT extends SuperPostmanSessionSetupManagerForIntegrationTests {

    private static PostmanDataStreamProvider postmanDataStreamProvider = getPostmanDataStreamProvider();
    private final String DOWNLOADED_FILES_OUTPUT_PATH = "src/test/ITOutput/postmanDataStreamProviderTest";

    /**
     * verifies that the streams are not empty!
     *
     * @throws IOException
     */
    @Test
    public void testProvideSingleInputStreamForIDs() throws IOException {
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };

        // is the stream NOT empty?
        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        isStreamEmpty(postmanDataStreamProvider.provideSingleInputStreamForIDs(IDsToDownload, postmanFilterOptions, getPostmanDataFinder()));
    }

    // TODO I could download the streams and compare the files

    /**
     * verifies that the streams are not empty!
     */
    @Test
    public void testProvideInputStreamForPermID() {
        List<DataSetFilePermId> permIDs = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
            }
        };

        // is the stream NOT empty?
        permIDs.forEach(dataSetFilePermId -> {
            try {
                isStreamEmpty(postmanDataStreamProvider.provideInputStreamForPermID(dataSetFilePermId));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    /**
     * verifies that the streams are not empty!
     */
    public void testGetDatasetStreamFromDatasetList() throws Exception {
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };
        final List<DataSet> foundDataSets = getPostmanDataFinder().findAllDatasetsRecursive(IDsToDownload.get(0));

        // is the stream NOT empty?
        foundDataSets.forEach(dataSet -> {
            try {
                isStreamEmpty(postmanDataStreamProvider.getDatasetStreamFromDatasetList(dataSet));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    /**
     *
     * UNSTABLE METHODS BEGIN
     *
     * The following methods outcommented below are all considered unstable!
     * They do not behave as expected!!!
     * Saving inputstreams in lists and reading them at a later time does not work, as some inputstreams will automatically close
     * after an unspecific, low amount of time.
     * We therefore decided to only offer methods which return lists of inputstreams!
     * In case of updates or concerns please open an issue on the postman-core-lib repository
     *
     */
//    @Test
//    public void testProvideMultipleInputStreamForIDs() throws IOException {
//        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "provideMultipleInputStreamForIDs";
//        List<String> IDsToDownload = new ArrayList<String>() {
//            {
//                add("/CONFERENCE_DEMO/QTGPR014A2");
//            }
//        };
//        final long expectedNumberOfFiles = 176;
//
//        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
//        Map<String, List<InputStream>> foundIDsToInputStreams = getPostmanDataStreamProvider().provideInputStreamPerID(IDsToDownload,
//                                                                                                                       postmanFilterOptions,
//                                                                                                                       getPostmanDataFinder());
//        downloadInputStreams(foundIDsToInputStreams, OUTPUTPATH);
//
//        final long foundNumberOfFiles = countFilesInDirectory(OUTPUTPATH);
//
//        // all files downloaded?
//        assertThat(foundNumberOfFiles).isAtLeast(expectedNumberOfFiles); // 06.11.2018
//
//        HashMap<String, Integer> expectedFileExtensions = new HashMap<String, Integer>() {
//            {
//                put("html", 42);
//                put("zip", 42);
//                put("tsv", 1);
//                put("pdf", 1);
//                put("json", 1);
//                put("alleles", 1);
//                put("log", 1);
//                put("err", 1);
//            }
//        };
//
//        HashMap<String, Integer> foundFileExtensions = new HashMap<>();
//
//        expectedFileExtensions.keySet()
//                .forEach(s -> foundFileExtensions.put(s, countFileOfExtensionInDirectory(OUTPUTPATH, s)));
//
//        // do the file extensions of all downloaded files match?
//        assertEquals(expectedFileExtensions, foundFileExtensions); // 06.11.2018
//
//        final long expectedSumFilesSize = 8249866146L; // 06.11.2018
//
//        // is the file size of all downloaded files large enough?
//        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
//    }
//
//    @Test
//    public void testGetDatasetInputStreamFromSuffixFilteredIDs() throws IOException {
//        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "getDatasetInputStreamFromSuffixFilteredIDs";
//        List<String> IDsToDownload = new ArrayList<String>() {
//            {
//                add("/CONFERENCE_DEMO/QTGPR014A2");
//            }
//        };
//
//        List<String> suffixes = new ArrayList<String>() {
//            {
//                add(".pdf");
//                add(".html");
//            }
//        };
//        final long expectedNumberOfFiles = 43; // 06.11.2018
//
//        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
//        postmanFilterOptions.setSuffixes(suffixes);
//        Map<String, List<InputStream>> foundIDsToInputStreams = getPostmanDataStreamProvider().provideInputStreamPerID(IDsToDownload,
//                postmanFilterOptions,
//                getPostmanDataFinder());
//        downloadInputStreams(foundIDsToInputStreams, OUTPUTPATH);
//
//        final long foundNumberOfFiles = countFilesInDirectory(OUTPUTPATH);
//
//        // all files downloaded?
//        assertThat(foundNumberOfFiles).isAtLeast(expectedNumberOfFiles); // 06.11.2018
//
//        HashMap<String, Integer> expectedFileExtensions = new HashMap<String, Integer>() {
//            {
//                put("html", 42);
//                put("pdf", 1);
//            }
//        };
//
//        HashMap<String, Integer> foundFileExtensions = new HashMap<>();
//
//        expectedFileExtensions.keySet()
//                .forEach(s -> foundFileExtensions.put(s, countFileOfExtensionInDirectory(OUTPUTPATH, s)));
//
//        // do the file extensions of all downloaded files match?
//        assertEquals(expectedFileExtensions, foundFileExtensions); // 06.11.2018
//
//        final long expectedSumFilesSize = 14603726L; // 13.11.2018
//
//        // is the file size of all downloaded files large enough?
//        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
//    }
//
//    @Test
//    public void testGetDatasetInputStreamFromRegexFilteredIDs() throws IOException {
//        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "getDatasetInputStreamFromRegexFilteredIDs";
//        List<String> IDsToDownload = new ArrayList<String>() {
//            {
//                add("/CONFERENCE_DEMO/QTGPR014A2");
//            }
//        };
//
//        List<String> regexes = new ArrayList<String>() {
//            {
//                add(".jobscript.FastQC.");
//                add(".pdf");
//            }
//        };
//        final long expectedNumberOfFiles = 85; // 06.11.2018
//
//        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
//        postmanFilterOptions.setRegexPatterns(regexes);
//        Map<String, List<InputStream>> foundIDsToInputStreams = getPostmanDataStreamProvider().provideInputStreamPerID(IDsToDownload,
//                postmanFilterOptions,
//                getPostmanDataFinder());
//        downloadInputStreams(foundIDsToInputStreams, OUTPUTPATH);
//
//        final long foundNumberOfFiles = countFilesInDirectory(OUTPUTPATH);
//
//        // all files downloaded?
//        assertThat(foundNumberOfFiles).isAtLeast(expectedNumberOfFiles); // 06.11.2018
//
//        final long expectedSumFilesSize = 1650888; // 13.11.2018
//
//        // is the file size of all downloaded files large enough?
//        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
//    }

    /**
     *
     * UNSTABLE METHODS END
     *
     */

}
