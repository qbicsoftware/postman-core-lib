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
    private static PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();

    @Test
    public void testDownloadRequestedFilesOfDatasets() throws IOException {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "testDownloadRequestedFilesOfDatasets";
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };
        final long expectedNumberOfFiles = 176;

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
                .forEach(s -> {
                    foundFileExtensions.put(s, countFileOfExtensionInDirectory(OUTPUTPATH, s));
                });

        // do the file extensions of all downloaded files match?
        assertEquals(expectedFileExtensions, foundFileExtensions); // 06.11.2018

        final long expectedSumFilesSize = 8249866146L; // 06.11.2018

        // is the file size of all downloaded files large enough?
        assertThat(getFileSizeOfDirectory(OUTPUTPATH)).isAtLeast(expectedSumFilesSize);
    }

    @Test
    public void testDownloadFilesFilteredByIDs() {

    }

    @Test
    public void testDownloadFilesByID() {

    }

    @Test
    public void testDownloadDataset() {

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
