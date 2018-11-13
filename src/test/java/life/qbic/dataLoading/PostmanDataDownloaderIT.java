package life.qbic.dataLoading;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

import life.qbic.SuperPostmanSessionSetupManagerForTests;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.testConfigurations.IntegrationTest;
import life.qbic.testConfigurations.Slow;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Category({IntegrationTest.class, Slow.class})
public class PostmanDataDownloaderIT extends SuperPostmanSessionSetupManagerForTests {

    private static PostmanDataDownloader postmanDataDownloader = getPostmanDataDownloader();
    private final String DOWNLOADED_FILES_OUTPUT_PATH = "src/test/ITOutput";

    @Test
    public void testDownloadRequestedFilesOfDatasets() throws IOException {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadRequestedFilesOfDatasets/nofilter";
        createFolderIfNotExisting(OUTPUTPATH);
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };
        final long expectedNumberOfFiles = 176;

        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        postmanDataDownloader.downloadRequestedFilesOfDatasets(IDsToDownload,
                                                               postmanFilterOptions,
                                                               getPostmanDataFinder(),
                                                               OUTPUTPATH);

        final long foundNumberOfFiles = countFilesInDirectory(OUTPUTPATH);

        // all files downloaded?
        assertThat(foundNumberOfFiles).isAtLeast(expectedNumberOfFiles); // 06.11.2018

        HashMap<String, Integer> expectedFileExtensions = new HashMap<String, Integer>() {
            {
                put("html", 42);
                put("zip", 42);
                put("tsv", 1);
                put("pdf", 1);
                put("json", 1);
                put("alleles", 1);
                put("log", 1);
                put("err", 1);
            }
        };

        HashMap<String, Integer> foundFileExtensions = new HashMap<>();

        expectedFileExtensions.keySet()
                .forEach(s -> foundFileExtensions.put(s, countFileOfExtensionInDirectory(OUTPUTPATH, s)));

        // do the file extensions of all downloaded files match?
        assertEquals(expectedFileExtensions, foundFileExtensions); // 06.11.2018

        final long expectedSumFilesSize = 8249866146L; // 06.11.2018

        // is the file size of all downloaded files large enough?
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
    }

    @Test
    public void testDownloadRequestedFilesOfDatasetsSuffixFiltered() throws IOException {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadRequestedFilesOfDatasets/suffixFiltered";
        createFolderIfNotExisting(OUTPUTPATH);
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };

        List<String> suffixes = new ArrayList<String>() {
            {
                add(".pdf");
                add(".html");
            }
        };


        final long expectedNumberOfFiles = 43;

        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        postmanFilterOptions.setSuffixes(suffixes);

        postmanDataDownloader.downloadRequestedFilesOfDatasets(IDsToDownload,
                postmanFilterOptions,
                getPostmanDataFinder(),
                OUTPUTPATH);

        final long foundNumberOfFiles = countFilesInDirectory(OUTPUTPATH);

        // all files downloaded?
        assertThat(foundNumberOfFiles).isAtLeast(expectedNumberOfFiles); // 06.11.2018

        HashMap<String, Integer> expectedFileExtensions = new HashMap<String, Integer>() {
            {
                put("html", 42);
                put("pdf", 1);
            }
        };

        HashMap<String, Integer> foundFileExtensions = new HashMap<>();

        expectedFileExtensions.keySet()
                .forEach(s -> foundFileExtensions.put(s, countFileOfExtensionInDirectory(OUTPUTPATH, s)));

        // do the file extensions of all downloaded files match?
        assertEquals(expectedFileExtensions, foundFileExtensions); // 06.11.2018

        final long expectedSumFilesSize = 14603726L; // 13.11.2018

        // is the file size of all downloaded files large enough?
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
    }

    @Test
    public void testDownloadRequestedFilesOfDatasetsRegexFiltered() throws IOException {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadRequestedFilesOfDatasets/regexFiltered";
        createFolderIfNotExisting(OUTPUTPATH);
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };
        List<String> regexes = new ArrayList<String>() {
            {
                add(".jobscript.FastQC.");
                add(".pdf");
            }
        };
        final long expectedNumberOfFiles = 85;

        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        postmanFilterOptions.setRegexPatterns(regexes);

        postmanDataDownloader.downloadRequestedFilesOfDatasets(IDsToDownload,
                postmanFilterOptions,
                getPostmanDataFinder(),
                OUTPUTPATH);

        final long foundNumberOfFiles = countFilesInDirectory(OUTPUTPATH);

        // all files downloaded?
        assertThat(foundNumberOfFiles).isAtLeast(expectedNumberOfFiles); // 06.11.2018

        System.out.println(getFileSizeOfDirectory(OUTPUTPATH));

        final long expectedSumFilesSize = 1650888; // 13.11.2018

        // is the file size of all downloaded files large enough?
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
    }

    @Test
    public void testDownloadFilesFilteredByIDsSuffix() {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadFilesFilteredByIDs/suffix";
        createFolderIfNotExisting(OUTPUTPATH);
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };
        final long expectedNumberOfFoundDatasets = 0;

//        postmanDataDownloader.downloadFilesFilteredByIDs(IDsToDownload,
//                                                         postmanFilterOptions,
//                                                         OUTPUTPATH);



        // TODO
    }

    @Test
    public void testDownloadForFilesFilteredByIDsRegex() {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadFilesFilteredByIDs/regex";
        createFolderIfNotExisting(OUTPUTPATH);

    }

    @Test
    public void testDownloadFilesByID() {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadFilesByID";
        createFolderIfNotExisting(OUTPUTPATH);
        // TODO
    }

    @Test
    public void testDownloadDataset() {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadDataset";
        createFolderIfNotExisting(OUTPUTPATH);
        // TODO
    }

    //TODO I should test this as well - behold - they're not part of IT, but Test
    private static void createFolderIfNotExisting(final String directoryPath) {
        new File(directoryPath).mkdirs();
    }

    private static long countFilesInDirectory(final String directoryPath) throws IOException {
        long count;
        try (Stream<Path> files = Files.list(Paths.get(directoryPath))) {
            count = files.count();
            return count;
        }
    }

    private static int countFileOfExtensionInDirectory(final String directoryPath, final String fileExtension) {
        Collection allFoundFiles = FileUtils.listFiles(new File(directoryPath), new String[]{fileExtension}, true);
        return allFoundFiles.size();
    }

    private static long getFileSizeOfDirectory(final String directoryPath) {
        return FileUtils.sizeOfDirectory(new File(directoryPath));
    }

}
