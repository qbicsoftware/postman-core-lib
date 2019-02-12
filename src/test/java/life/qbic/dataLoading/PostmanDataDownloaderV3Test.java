package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.core.PostmanFilterOptions;
import life.qbic.core.authentication.PostmanSessionManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.verification.PrivateMethodVerification;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PostmanDataDownloaderV3.class)
@PowerMockIgnore({"javax.management.*", "javax.script*"})
public class PostmanDataDownloaderV3Test {

    private PostmanDataDownloaderV3 postmanDataDownloaderV3;

    @Mock
    private PostmanSessionManager postmanSessionManager;
    @Mock
    private PostmanDataFinder postmanDataFinder;
    @Mock
    private List<DataSet> datasets;
    @Mock
    private List<DataSetFilePermId> filePermIds;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setupTests() {
        postmanDataDownloaderV3 = new PostmanDataDownloaderV3(postmanSessionManager.getDataStoreServer(),
                                                                                      postmanDataFinder,
                                                                                      "");
        postmanDataDownloaderV3 = PowerMockito.spy(postmanDataDownloaderV3);
    }

    @Test
    public void testDownloadRequestedFilesOfDatasetsNoFiltering() throws Exception {
        when(postmanDataFinder.findAllDatasetsRecursive(Mockito.anyString()))
                .thenReturn(datasets);
        when(datasets.size()).thenReturn(1);
        postmanDataDownloaderV3.downloadRequestedFilesOfDatasets(Collections.singletonList("randomSampleID"),
                                                                new PostmanFilterOptions(),
                                                                "somepath");

        PrivateMethodVerification privateMethodInvocation = PowerMockito.verifyPrivate(postmanDataDownloaderV3);
        privateMethodInvocation.invoke("downloadDataset", anyList(), anyString());

        verify(postmanDataFinder, times(1))
                .findAllDatasetsRecursive(Mockito.anyString());
    }

    @Test
    public void testDownloadRequestedFilesOfDatasetsSuffixFiltering() throws Exception {
        when(postmanDataFinder.findAllSuffixFilteredPermIDs(Mockito.anyString(), anyList()))
                .thenReturn(filePermIds);
        when(filePermIds.size()).thenReturn(1);
        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        postmanFilterOptions.setSuffixes(Collections.singletonList("suffix"));
        postmanDataDownloaderV3.downloadRequestedFilesOfDatasets(Collections.singletonList("randomSampleID"),
                postmanFilterOptions,
                "somepath");

        verify(postmanDataDownloaderV3, times(1))
                .downloadFilesFilteredByIDs(Mockito.anyString(), Mockito.anyList(), Mockito.anyString());

        verify(postmanDataFinder, times(1))
                .findAllSuffixFilteredPermIDs(Mockito.anyString(), Mockito.anyList());
    }

    @Test
    public void testDownloadRequestedFilesOfDatasetsRegexFiltering() throws Exception {
        when(postmanDataFinder.findAllSuffixFilteredPermIDs(Mockito.anyString(), anyList()))
                .thenReturn(filePermIds);
        when(filePermIds.size()).thenReturn(1);
        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        postmanFilterOptions.setRegexPatterns(Collections.singletonList("regex"));
        postmanDataDownloaderV3.downloadRequestedFilesOfDatasets(Collections.singletonList("randomSampleID"),
                postmanFilterOptions,
                "somepath");

        verify(postmanDataDownloaderV3, times(1))
                .downloadFilesFilteredByIDs(Mockito.anyString(), Mockito.anyList(), Mockito.anyString());

        verify(postmanDataFinder, times(1))
                .findAllRegexFilteredPermIDs(Mockito.anyString(), Mockito.anyList());
    }

    @Test
    public void testDownloadRequestedFilesOfDatasetCodeFiltering() throws Exception {
        when(postmanDataFinder.findAllDatasetsRecursive(Mockito.anyString()))
                .thenReturn(datasets);
        when(datasets.size()).thenReturn(1);
        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        postmanFilterOptions.setDatasetCodes(Collections.singletonList("someCode"));
        postmanDataDownloaderV3.downloadRequestedFilesOfDatasets(Collections.singletonList("randomSampleID"),
                postmanFilterOptions,
                "somepath");

        PrivateMethodVerification privateMethodInvocation = PowerMockito.verifyPrivate(postmanDataDownloaderV3);
        privateMethodInvocation.invoke("filterDatasetCodes", anyList(), anyString(), anyList());

        verify(postmanDataFinder, times(1))
                .findAllDatasetsRecursive(Mockito.anyString());
    }

    @Test
    public void downloadFilesFilteredAfterFileTypeFiltering() throws Exception {
        when(postmanDataFinder.findAllSuffixFilteredPermIDs(Mockito.anyString(), anyList()))
                .thenReturn(filePermIds);
        when(filePermIds.size()).thenReturn(1);
        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        postmanFilterOptions.setFileType("filetype");
        postmanDataDownloaderV3.downloadRequestedFilesOfDatasets(Collections.singletonList("randomSampleID"),
                postmanFilterOptions,
                "somepath");

        PrivateMethodVerification privateMethodInvocation = PowerMockito.verifyPrivate(postmanDataDownloaderV3);
        privateMethodInvocation.invoke("downloadDataset", anyList(), anyString());

        verify(postmanDataFinder, times(1))
                .findAllTypeFilteredDataSets(Mockito.anyString(), Mockito.anyString());
    }
}
