package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.testConfigurations.Fast;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category({Test.class, Fast.class})
public class DataStreamProviderTest {

    @Mock
    IDataStoreServerApi iDataStoreServerApi;
    @Mock
    DataSetFilePermId dataSetFilePermID;
    @Mock
    DataSet dataSet;
    @Mock
    DataSetPermId dataSetPermId;

    private PostmanDataStreamProvider postmanDataStreamProvider;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        postmanDataStreamProvider = new PostmanDataStreamProvider(iDataStoreServerApi, "sessiontoken");
    }

    @Test
    public void testProvideInputStreamForPermID() {
        when(iDataStoreServerApi.downloadFiles(Mockito.anyString(), Mockito.anyList(), any(DataSetFileDownloadOptions.class)))
                .thenReturn(null);
        postmanDataStreamProvider.provideInputStreamForPermID(dataSetFilePermID);

        verify(iDataStoreServerApi, times(1))
                .downloadFiles(Mockito.anyString(), Mockito.anyList(), any(DataSetFileDownloadOptions.class));
    }

    @Test
    public void testGetDatasetStreamFromDatasetList() {
        when(iDataStoreServerApi.downloadFiles(Mockito.anyString(), Mockito.anyList(), any(DataSetFileDownloadOptions.class)))
                .thenReturn(null);
        when(dataSet.getPermId()).thenReturn(dataSetPermId);
        postmanDataStreamProvider.getDatasetStreamFromDatasetList(dataSet);

        verify(iDataStoreServerApi, times(1))
                .downloadFiles(Mockito.anyString(), Mockito.anyList(), any(DataSetFileDownloadOptions.class));
    }
}
