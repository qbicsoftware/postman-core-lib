package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.testConfigurations.Fast;
import life.qbic.util.RegexFilterUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RegexFilterUtil.class)
@PowerMockIgnore({"javax.management.*", "javax.script*"})
@Category({Test.class, Fast.class})
public class PostmanDataFinderTest {

    private PostmanDataFinder postmanDataFinder;
    private PostmanDataFinder postmanDataFinderSpy;

    @Mock
    private IApplicationServerApi iApplicationServerApi;
    @Mock
    private IDataStoreServerApi iDataStoreServerApi;
    @Mock
    private PostmanDataFilterer postmanDataFilterer;
    @Mock
    private SearchResult<Sample> searchResultSample;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        postmanDataFinder = new PostmanDataFinder(iApplicationServerApi, iDataStoreServerApi, postmanDataFilterer, "");
        postmanDataFinderSpy = Mockito.spy(postmanDataFinder);

        PowerMockito.mockStatic(RegexFilterUtil.class);
    }

    @Test
    public void testFindAllDatasetsRecursive() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResultSample);
        when(searchResultSample.getObjects())
                .thenReturn(Collections.emptyList());

        List<DataSet> foundDatasets = postmanDataFinder.findAllDatasetsRecursive("someID");

        verify(iApplicationServerApi, times(1))
                .searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class));
    }

    @Test
    public void testFindAllRegexFilteredIDs() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResultSample);
        when(searchResultSample.getObjects())
                .thenReturn(Collections.emptyList());

        List<DataSetFilePermId> result = postmanDataFinderSpy.findAllRegexFilteredPermIDs("test", Collections.singletonList("regex"));
        PowerMockito.verifyStatic(RegexFilterUtil.class, VerificationModeFactory.times(1));
        RegexFilterUtil.findAllRegexFilteredIDsGroovy(Mockito.anyList(), Mockito.anyList(), Mockito.any(), Mockito.any());

        verify(postmanDataFinderSpy, Mockito.atLeastOnce())
                .findAllDatasetsRecursive(Mockito.anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllRegexFilteredIDsNoID() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResultSample);
        when(searchResultSample.getObjects())
                .thenReturn(Collections.emptyList());

        List<DataSetFilePermId> result = postmanDataFinderSpy.findAllRegexFilteredPermIDs("", Collections.singletonList("regex"));

        PowerMockito.verifyStatic(RegexFilterUtil.class, VerificationModeFactory.times(1));
        RegexFilterUtil.findAllRegexFilteredIDsGroovy(Mockito.anyList(), Mockito.anyList(), Mockito.any(), Mockito.any());

        verify(postmanDataFinderSpy, Mockito.atLeastOnce())
                .findAllDatasetsRecursive(Mockito.anyString());
    }

    @Test
    public void testFindAllSuffixFilteredIDs() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResultSample);
        when(searchResultSample.getObjects())
                .thenReturn(Collections.emptyList());

        List<DataSetFilePermId> result = postmanDataFinderSpy.findAllSuffixFilteredPermIDs("test", Collections.singletonList("suffix"));

        verify(postmanDataFinderSpy, Mockito.atLeastOnce())
                .findAllDatasetsRecursive(Mockito.anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllSuffixFilteredIDsNoID() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResultSample);
        when(searchResultSample.getObjects())
                .thenReturn(Collections.emptyList());

        List<DataSetFilePermId> result = postmanDataFinderSpy.findAllSuffixFilteredPermIDs("", Collections.singletonList("suffix"));

        verify(postmanDataFinderSpy, Mockito.atLeastOnce())
                .findAllDatasetsRecursive(Mockito.anyString());
    }

    @Test
    public void testFindAllPermIDs() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResultSample);
        when(searchResultSample.getObjects())
                .thenReturn(Collections.emptyList());

        List<DataSetFilePermId> result = postmanDataFinderSpy.findAllPermIDs("test");

        verify(postmanDataFinderSpy, Mockito.atLeastOnce())
                .findAllDatasetsRecursive(Mockito.anyString());
    }

}
