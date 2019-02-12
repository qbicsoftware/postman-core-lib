package life.qbic.dataLoading;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

import life.qbic.SuperPostmanSessionSetupManagerForIntegrationTests;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.testConfigurations.IntegrationTest;
import life.qbic.testConfigurations.Slow;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Category({IntegrationTest.class, Slow.class})
public class PostmanDataDownloaderOldAPIIT extends SuperPostmanSessionSetupManagerForIntegrationTests {

    private static PostmanDataDownloaderOldAPI postmanDataDownloaderOldAPI = getPostmanDataDownloaderOldAPI();
    private final String DOWNLOADED_FILES_OUTPUT_PATH = "src/test/ITOutput/postmanDataDownloaderOldAPITest";

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
        postmanDataDownloaderOldAPI.downloadRequestedFilesOfDatasets(IDsToDownload,
                postmanFilterOptions,
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
                .forEach(extension -> foundFileExtensions.put(extension, countFileOfExtensionInDirectory(OUTPUTPATH, extension)));

        // do the file extensions of all downloaded files match?
        assertEquals(expectedFileExtensions, foundFileExtensions); // 06.11.2018

        final long expectedSumFilesSize = 8249866146L; // 06.11.2018

        // is the file size of all downloaded files large enough?
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtMost((long) (expectedSumFilesSize * 1.5)); // file sizes may change, but if they differ too much they should get reviewed!
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

        postmanDataDownloaderOldAPI.downloadRequestedFilesOfDatasets(IDsToDownload,
                postmanFilterOptions,
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
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtMost((long) (expectedSumFilesSize * 1.5)); // file sizes may change, but if they differ too much they should get reviewed!
    }

    @Test
    public void testDownloadRequestedFilesOfDatasetsRegexFiltered() throws IOException {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadRequestedFilesOfDatasetsOldAPI/regexFiltered";
        createFolderIfNotExisting(OUTPUTPATH);
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };
        List<String> regexes = new ArrayList<String>() {
            {
                add(".*.jobscript.FastQC.*");
                add(".*.pdf");
            }
        };
        final long expectedNumberOfFiles = 85;

        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        postmanFilterOptions.setRegexPatterns(regexes);

        postmanDataDownloaderOldAPI.downloadRequestedFilesOfDatasets(IDsToDownload,
                postmanFilterOptions,
                OUTPUTPATH);

        final long foundNumberOfFiles = countFilesInDirectory(OUTPUTPATH);

        // all files downloaded?
        assertThat(foundNumberOfFiles).isAtLeast(expectedNumberOfFiles); // 06.11.2018

        final long expectedSumFilesSize = 1650888; // 13.11.2018

        // is the file size of all downloaded files large enough?
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtMost((long) (expectedSumFilesSize * 1.5)); // file sizes may change, but if they differ too much they should get reviewed!
    }

    @Test
    public void testDownloadFilesByID() {
        // tested via public interface of downloadFilesFilteredByIDsSuffix
    }

    @Test
    public void testDownloadDataset() {
        // tested via public interfaces of all methods of postmanDataDownloaderOldAPI
    }

}
