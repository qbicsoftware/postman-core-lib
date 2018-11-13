package life.qbic.dataLoading;

import com.sun.xml.internal.bind.v2.model.core.ID;
import life.qbic.SuperPostmanSessionSetupManagerForTestsIT;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.testConfigurations.IntegrationTest;
import life.qbic.testConfigurations.Slow;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import sun.java2d.pipe.OutlineTextRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

@Category({IntegrationTest.class, Slow.class})
public class PostmanDataStreamProviderIT extends SuperPostmanSessionSetupManagerForTestsIT {

    private static PostmanDataStreamProvider postmanDataStreamProvider = getPostmanDataStreamProvider();
    private final String DOWNLOADED_FILES_OUTPUT_PATH = "src/test/ITOutput/postmanDataStreamProviderTest";

    @Test
    public void testProvideSingleInputStreamForIDs() throws IOException {
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };

        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        PushbackInputStream pushbackInputStream = new PushbackInputStream(
                postmanDataStreamProvider.provideSingleInputStreamForIDs(IDsToDownload, postmanFilterOptions, getPostmanDataFinder()
                ));

        byte[] buffer = new byte[8 * 1024];
        int readBytes = pushbackInputStream.read(buffer);
        assertThat(readBytes).isAtLeast(1);
        pushbackInputStream.unread(readBytes);
    }

    @Test
    public void testProvideInputStreamPerID() throws IOException {
        final String OUTPUTPATH = DOWNLOADED_FILES_OUTPUT_PATH + File.separator + "provideInputStreamPerIDs";
        List<String> IDsToDownload = new ArrayList<String>() {
            {
                add("/CONFERENCE_DEMO/QTGPR014A2");
            }
        };

        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        Map<String, List<InputStream>> foundIDsToInputStreams = getPostmanDataStreamProvider().provideInputStreamPerID(IDsToDownload, postmanFilterOptions, getPostmanDataFinder());
        downloadInputStream(foundIDsToInputStreams, OUTPUTPATH);

        final long expectedNumberOfFiles = 176;

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
    public void testGetDatasetStreamFromFilteredIds() {

    }

    @Test
    public void testGetDatasetStreamFromDatasetList() {

    }

}
