package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.util.RegexFilterUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.verification.VerificationMode;
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
public class PostmanDataFinderTest {

    private PostmanDataFinder postmanDataFinder;
    private PostmanDataFinder postmanDataFinderSpy;

    @Mock
    IApplicationServerApi iApplicationServerApi;

    @Mock
    IDataStoreServerApi iDataStoreServerApi;

    @Mock
    SearchResult<Sample> searchResult;

    @Mock
    PostmanDataFinder postmanDataFinderMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        postmanDataFinder = new PostmanDataFinder(iApplicationServerApi, iDataStoreServerApi, "");
        postmanDataFinderSpy = Mockito.spy(postmanDataFinder);

        PowerMockito.mockStatic(RegexFilterUtil.class);
    }

    @Test
    public void testFindAllDatasetsRecursive() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResult);
        when(searchResult.getObjects())
                .thenReturn(Collections.emptyList());
        List<DataSet> foundDatasets = postmanDataFinder.findAllDatasetsRecursive("");

        verify(iApplicationServerApi, times(1))
                .searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class));
    }

    @Test
    public void testFindAllRegexFilteredIDs() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResult);
        when(searchResult.getObjects())
                .thenReturn(Collections.emptyList());

        List<DataSetFilePermId> result = postmanDataFinderSpy.findAllRegexFilteredPermIDs("test", Collections.singletonList("regex"));
        PowerMockito.verifyStatic(RegexFilterUtil.class, VerificationModeFactory.times(1));
        RegexFilterUtil.findAllRegexFilteredIDsGroovy(Mockito.anyList(), Mockito.anyList(), Mockito.any(), Mockito.any());

        verify(postmanDataFinderSpy, Mockito.atLeastOnce())
                .findAllDatasetsRecursive(Mockito.anyString());
    }

    @Test
    public void testFindAllRegexFilteredIDsNoID() {
//        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
//                .thenReturn(searchResult);
//        when(searchResult.getObjects())
//                .thenReturn(Collections.emptyList());
//        when(postmanDataFinderMock.findAllDatasetsRecursive(Mockito.anyString()))
//                .thenReturn(Collections.emptyList());
//
//        List<DataSetFilePermId> result = postmanDataFinderMock.findAllRegexFilteredPermIDs("", Collections.singletonList("regex"));
    }

    @Test
    public void testFindAllSuffixFilteredIDs() {

    }

}
