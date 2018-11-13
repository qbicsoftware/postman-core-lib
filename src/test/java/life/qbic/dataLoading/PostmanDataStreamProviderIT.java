package life.qbic.dataLoading;

import life.qbic.SuperPostmanSessionSetupManagerForTestsIT;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.testConfigurations.IntegrationTest;
import life.qbic.testConfigurations.Slow;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

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
    public void testProvideInputStreamPerID() {

    }

    @Test
    public void testGetDatasetStreamFromFilteredIds() {

    }

    @Test
    public void testGetDatasetStreamFromDatasetList() {

    }

}
