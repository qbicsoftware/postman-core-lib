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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostmanDataFinderTest {

    private PostmanDataFinder postmanDataFinder;

    @Mock
    IApplicationServerApi iApplicationServerApi;

    @Mock
    IDataStoreServerApi iDataStoreServerApi;

    @Mock
    SearchResult<Sample> searchResult;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        postmanDataFinder = new PostmanDataFinder(iApplicationServerApi, iDataStoreServerApi, "");
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
        when(postmanDataFinder.findAllRegexFilteredPermIDs(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Collections.emptyList());

        List<DataSetFilePermId> result = postmanDataFinder.findAllRegexFilteredPermIDs("test", Collections.singletonList("regex"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllRegexFilteredIDsNoID() {
        when(iApplicationServerApi.searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class)))
                .thenReturn(searchResult);
        when(searchResult.getObjects())
                .thenReturn(Collections.emptyList());
        when(postmanDataFinder.findAllDatasetsRecursive(Mockito.anyString()))
                .thenReturn(Collections.emptyList());

        List<DataSetFilePermId> result = postmanDataFinder.findAllRegexFilteredPermIDs("", Collections.singletonList("regex"));
    }

    @Test
    public void testFindAllSuffixFilteredIDs() {

    }

}
