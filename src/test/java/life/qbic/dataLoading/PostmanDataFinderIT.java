package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.SuperPostmanSessionSetupManagerForTestsIT;
import life.qbic.testConfigurations.IntegrationTest;
import life.qbic.testConfigurations.Slow;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.google.common.truth.Truth.assertThat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Category({IntegrationTest.class, Slow.class})
public class PostmanDataFinderIT extends SuperPostmanSessionSetupManagerForTestsIT {

    private static PostmanDataFinder postmanDataFinder = getPostmanDataFinder();

    /**
     * uses sampleID /CONFERENCE_DEMO/QTGPR014A2
     *
     * tests whether recursively found datasets' count and codes are matching
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

    /**
     * uses regex: '.pdf'
     *
     * tests whether regex filtering returns the
     * correct count of IDs, matching DataSetPermIDs and the corresponding filepaths
     *
     */
    @Test
    public void testFindAllRegexFilteredIDsSimple() {
        List<DataSetFilePermId> expectedIDs = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170214193727212-160772"), "original/NGSQTGPR032A0_workflow_results/2017_02_14_19_25_59_coverage_plot.pdf"));
            }
        };

        List<String> expectedPermIDs = expectedIDs.stream()
                .map(DataSetFilePermId::toString)
                .collect(Collectors.toList());

        List<String> expectedIDsFilePath = expectedIDs.stream()
                .map(DataSetFilePermId::getFilePath)
                .collect(Collectors.toList());

        List<DataSetFilePermId> foundRegexFilteredIDs = postmanDataFinder.findAllRegexFilteredIDs("/CONFERENCE_DEMO/QTGPR014A2", new ArrayList<>(Collections.singleton(".pdf")));

        List<String> foundPermIDs = foundRegexFilteredIDs.stream()
                .map(DataSetFilePermId::toString)
                .collect(Collectors.toList());

        List<String> foundIDsFilepath = foundRegexFilteredIDs.stream()
                .map(DataSetFilePermId::getFilePath)
                .collect(Collectors.toList());

        // correct number of IDs found?
        assertEquals(expectedIDs.size(), foundRegexFilteredIDs.size());

        // all DataSetPermIDs matching?
        assertEquals(expectedPermIDs, foundPermIDs);

        // all filePaths matching?
        assertEquals(expectedIDsFilePath, foundIDsFilepath);
    }

    /**
     * uses regex: '.html'
     *
     * tests whether regex filtering returns the
     * correct count of IDs, matching DataSetPermIDs and the corresponding filepaths
     *
     */
    @Test
    public void testFindAllRegexFilteredIDsModeratelyComplex() {
        List<DataSetFilePermId> expectedIDs = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
            }
        };

        List<String> expectedPermIDs = expectedIDs.stream()
                .map(DataSetFilePermId::toString)
                .collect(Collectors.toList());

        List<String> expectedIDsFilePath = expectedIDs.stream()
                .map(DataSetFilePermId::getFilePath)
                .collect(Collectors.toList());

        List<DataSetFilePermId> foundRegexFilteredIDs = postmanDataFinder.findAllRegexFilteredIDs(
                "/CONFERENCE_DEMO/QTGPR014A2",
                new ArrayList<>(Collections.singleton(".html")));

        List<String> foundPermIDs = foundRegexFilteredIDs.stream()
                .limit(5)
                .map(DataSetFilePermId::toString)
                .collect(Collectors.toList());

        List<String> foundIDsFilepath = foundRegexFilteredIDs.stream()
                .limit(5)
                .map(DataSetFilePermId::getFilePath)
                .collect(Collectors.toList());

        // all DataSetPermIDs matching?
        assertEquals(expectedPermIDs, foundPermIDs);

        // all filePaths matching?
        assertEquals(expectedIDsFilePath, foundIDsFilepath);

        // correct number of IDs found?
        assertThat(foundRegexFilteredIDs.size()).isAtLeast(41); // 06.11.2018
    }

    /**
     * uses regex: '.jobscript.FastQC.'
     *
     * tests whether regex filtering returns the correct count of IDs
     *
     */
    @Test
    public void testFindAllRegexFilteredIDsComplex() {
        List<DataSetFilePermId> foundRegexFilteredIDs = postmanDataFinder.findAllRegexFilteredIDs("/CONFERENCE_DEMO/QTGPR014A2", new ArrayList<>(Collections.singleton(".jobscript.FastQC.")));

        // correct number of IDs found?
        assertThat(foundRegexFilteredIDs.size()).isAtLeast(252); // 06.11.2018
    }

    /**
     * uses suffix: '.pdf'
     *
     * tests whether suffix filtering returns the
     * correct count of IDs, matching DataSetPermIDs and the corresponding filepaths
     *
     */
    @Test
    public void testFindAllSuffixFilteredIDs() {
        List<DataSetFilePermId> expectedIDs = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170214193727212-160772"), "original/NGSQTGPR032A0_workflow_results/2017_02_14_19_25_59_coverage_plot.pdf"));
            }
        };

        List<String> expectedPermIDs = expectedIDs.stream()
                .map(DataSetFilePermId::toString)
                .collect(Collectors.toList());

        List<String> expectedIDsFilePath = expectedIDs.stream()
                .map(DataSetFilePermId::getFilePath)
                .collect(Collectors.toList());

        List<DataSetFilePermId> foundSuffixFilteredIDs = postmanDataFinder.findAllSuffixFilteredIDs("/CONFERENCE_DEMO/QTGPR014A2", new ArrayList<>(Collections.singleton(".pdf")));

        List<String> foundPermIDs = foundSuffixFilteredIDs.stream()
                .map(DataSetFilePermId::toString)
                .collect(Collectors.toList());

        List<String> foundIDsFilepath = foundSuffixFilteredIDs.stream()
                .map(DataSetFilePermId::getFilePath)
                .collect(Collectors.toList());

        // correct number of IDs found?
        assertEquals(expectedIDs.size(), foundSuffixFilteredIDs.size());

        // all DataSetPermIDs matching?
        assertEquals(expectedPermIDs, foundPermIDs);

        // all filePaths matching?
        assertEquals(expectedIDsFilePath, foundIDsFilepath);
    }

    /**
     * uses regex: '.html'
     *
     * tests whether regex filtering returns the
     * correct count of IDs, matching DataSetPermIDs and the corresponding filepaths
     *
     */
    @Test
    public void testFindAllSuffixFilteredIDsLarger() {
        List<DataSetFilePermId> expectedIDs = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
            }
        };

        List<String> expectedPermIDs = expectedIDs.stream()
                .map(DataSetFilePermId::toString)
                .collect(Collectors.toList());

        List<String> expectedIDsFilePath = expectedIDs.stream()
                .map(DataSetFilePermId::getFilePath)
                .collect(Collectors.toList());

        List<DataSetFilePermId> foundRegexFilteredIDs = postmanDataFinder.findAllSuffixFilteredIDs("/CONFERENCE_DEMO/QTGPR014A2", new ArrayList<>(Collections.singleton(".html")));

        List<String> foundPermIDs = foundRegexFilteredIDs.stream()
                .limit(5)
                .map(DataSetFilePermId::toString)
                .collect(Collectors.toList());

        List<String> foundIDsFilepath = foundRegexFilteredIDs.stream()
                .limit(5)
                .map(DataSetFilePermId::getFilePath)
                .collect(Collectors.toList());

        // all DataSetPermIDs matching?
        assertEquals(expectedPermIDs, foundPermIDs);

        // all filePaths matching?
        assertEquals(expectedIDsFilePath, foundIDsFilepath);

        // correct number of IDs found?
        assertThat(foundRegexFilteredIDs.size()).isAtLeast(42); // 06.11.2018
    }

}
