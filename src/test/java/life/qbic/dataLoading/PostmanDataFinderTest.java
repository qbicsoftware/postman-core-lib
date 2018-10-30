package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import life.qbic.SuperPostmanSessionSetupForTests;
import life.qbic.core.authentication.PostmanSessionManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class PostmanDataFinderTest extends SuperPostmanSessionSetupForTests {

    private static PostmanDataFinder postmanDataFinder = getPostmanDataFinder();

    /**
     * uses sampleID /CONFERENCE_DEMO/QTGPR014A2 to
     */
    @Test
    public void testFindAllDatasetsRecursive() {
        List<String> expectedCodes = new ArrayList<String>() {
            {
                add("20170210172702893-159387");
                add("20170214193727212-160772");
                add("20170214193727223-160773");
                add("20170221165026653-162100");
                add("20170221165027152-162101");
            }
        };

        List<DataSet> foundDatasets = postmanDataFinder.findAllDatasetsRecursive("/CONFERENCE_DEMO/QTGPR014A2");

        // correct dataset count?
        assertEquals(expectedCodes.size(), foundDatasets.size());

        // all codes are matching?
        List<String> foundDatasetsCodes = foundDatasets.stream()
                .map(DataSet::getCode)
                .collect(Collectors.toList());
        assertEquals(expectedCodes, foundDatasetsCodes);
    }

    @Test
    public void testFetchDescendantDatasets() {

    }

    @Test
    public void testFindAllRegexFilteredIDs() {

    }

    @Test
    public void testFindAllSuffixFilteredIDs() {

    }


}
