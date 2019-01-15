package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.create.SampleCreation;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

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

        // create the real objects here?
        when(searchResult.getObjects()).thenReturn(Collections.emptyList());

        List<String> expectedCodes = new ArrayList<String>() {
            {
                add("");
                add("");
            }
        };

        List<DataSet> foundDatasets = postmanDataFinder.findAllDatasetsRecursive("");

        verify(iApplicationServerApi, times(1))
                .searchSamples(Mockito.anyString(), any(SampleSearchCriteria.class), any(SampleFetchOptions.class));

//        // correct dataset count?
//        assertEquals(expectedCodes.size(), foundDatasets.size());
    }

    @Test
    public void testFindAllRegexFilteredIDsSimple() {

    }

    @Test
    public void testFindAllRegexFilteredIDsModeratelyComplex() {

    }

    @Test
    public void testFindAllRegexFilteredIDsComplex() {

    }

    @Test
    public void testFindAllSuffixFilteredIDs() {

    }

    @Test
    public void testFindAllSuffixFilteredIDsLarger() {

    }
}
