package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.core.authentication.PostmanSessionManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;


public class PostmanDataDownloaderV3Test {

    private static PostmanDataDownloaderV3 postmanDataDownloaderV3;

    @Mock
    PostmanSessionManager postmanSessionManager;
    @Mock
    PostmanDataFinder postmanDataFinder;
    @Mock
    DataSetFilePermId dataSetFilePermId;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setupTests() {
        postmanDataDownloaderV3 = new PostmanDataDownloaderV3(postmanSessionManager.getDataStoreServer(),
                                                              postmanSessionManager.getSessionToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDownloadRequestedFilesOfDatasetsNoIDs() throws IOException {

    }

    @Test
    public void testDownloadRequestedFilesOfDatasets() throws IOException {


    }

    @Test
    public void downloadFilesFilteredByIDs() {

    }
}
