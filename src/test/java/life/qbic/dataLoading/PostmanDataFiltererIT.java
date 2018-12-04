package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.SuperPostmanSessionSetupManagerForIntegrationTestsIT;
import life.qbic.core.PostmanFilterOptions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PostmanDataFiltererIT extends SuperPostmanSessionSetupManagerForIntegrationTestsIT {

    /**
     * tests both: suffix and regex filtering
     * verifies whether the permIDs are matching after filtering has been applied
     *
     */
    @Test
    public void testFilterPermIDs() {
        List<DataSetFilePermId> allIDs = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/blablabla.pdf"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/lululululul.json"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/huhuhuhuhuhuu.json"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/hahahahahahah.mzml"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/stubstubstub.mzml"));
            }
        };

        // same IDs for the suffix case?
        List<DataSetFilePermId> expectedIDsSuffix = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/hahahahahahah.mzml"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/stubstubstub.mzml"));
            }
        };

        PostmanFilterOptions postmanFilterOptionsSuffix = new PostmanFilterOptions();
        List<String> suffixes = new ArrayList<String>() {
            {
                add(".html");
                add(".mzml");
            }
        };
        postmanFilterOptionsSuffix.setSuffixes(suffixes);

        List<DataSetFilePermId> suffixFilteredIDs = getPostmanDataFilterer().filterPermIDs(allIDs, postmanFilterOptionsSuffix);

        assertEquals(expectedIDsSuffix, suffixFilteredIDs);

        // same IDs for the regex case?
        List<DataSetFilePermId> expectedIDsRegex = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
            }
        };
        PostmanFilterOptions postmanFilterOptionsRegex = new PostmanFilterOptions();
        List<String> regexes = new ArrayList<String>() {
            {
                add(".*FastQC.*");
            }
        };
        postmanFilterOptionsRegex.setRegexPatterns(regexes);

        List<DataSetFilePermId> regexFilteredIDs = getPostmanDataFilterer().filterPermIDs(allIDs, postmanFilterOptionsRegex);

        assertEquals(expectedIDsRegex, regexFilteredIDs);
    }

    /**
     * tests just the suffix case!
     * verifies whether the permIDs are matching after filtering has been applied
     *
     */
    @Test
    public void testFilterPermIDsBySuffix() {
        List<DataSetFilePermId> allIDs = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/blablabla.pdf"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/lululululul.json"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/huhuhuhuhuhuu.json"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/hahahahahahah.mzml"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/stubstubstub.mzml"));
            }
        };

        // same IDs for the suffix case?
        List<DataSetFilePermId> expectedIDsSuffix = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/hahahahahahah.mzml"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/stubstubstub.mzml"));
            }
        };

        PostmanFilterOptions postmanFilterOptionsSuffix = new PostmanFilterOptions();
        List<String> suffixes = new ArrayList<String>() {
            {
                add(".html");
                add(".mzml");
            }
        };
        postmanFilterOptionsSuffix.setSuffixes(suffixes);

        List<DataSetFilePermId> suffixFilteredIDs = getPostmanDataFilterer().filterPermIDsBySuffix(allIDs, postmanFilterOptionsSuffix.getSuffixes());

        assertEquals(expectedIDsSuffix, suffixFilteredIDs);
    }

    /**
     * tests just the regex case
     * verifies whether the permIDs are matching after filtering has been applied
     *
     */
    @Test
    public void testFilterPermIDsByRegex() {
        List<DataSetFilePermId> allIDs = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/blablabla.pdf"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/lululululul.json"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/huhuhuhuhuhuu.json"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/hahahahahahah.mzml"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/stubstubstub.mzml"));
            }
        };

        // same IDs for the regex case?
        List<DataSetFilePermId> expectedIDsRegex = new ArrayList<DataSetFilePermId>() {
            {
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00119_SRR099967_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_1.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00121_ERR031964_2.html"));
                add(new DataSetFilePermId(new DataSetPermId("20170221165026653-162100"), "original/QTGPRE77_workflow_results/FastQC_HG00638_SRR070804_1.html"));
            }
        };
        PostmanFilterOptions postmanFilterOptionsRegex = new PostmanFilterOptions();
        List<String> regexes = new ArrayList<String>() {
            {
                add(".*FastQC.*");
            }
        };
        postmanFilterOptionsRegex.setRegexPatterns(regexes);

        List<DataSetFilePermId> regexFilteredIDs = getPostmanDataFilterer().filterPermIDsByRegex(allIDs, postmanFilterOptionsRegex.getRegexPatterns());

        assertEquals(expectedIDsRegex, regexFilteredIDs);
    }

    /**
     * tests both: suffix and regex filtering
     * verifies whether the datasetFiles are matching after filtering has been applied
     */
    @Test
    public void testFilterDataSetFiles () {
        List<DataSetFile> allDataSetFiles = new ArrayList<>();
        DataSetFile dataSetFile = new DataSetFile();
        dataSetFile.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387")));

        DataSetFile dataSetFile2 = new DataSetFile();
        dataSetFile2.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original")));

        DataSetFile dataSetFile3 = new DataSetFile();
        dataSetFile3.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female")));

        DataSetFile dataSetFile4 = new DataSetFile();
        dataSetFile4.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female/HG00121_ERR031964_1.fastq.gz")));

        DataSetFile dataSetFile5 = new DataSetFile();
        dataSetFile5.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female/HG00121_ERR031964_2.fastq.gz")));

        Collections.addAll(allDataSetFiles, dataSetFile, dataSetFile2, dataSetFile3, dataSetFile4, dataSetFile5);

        // suffix case
        List<DataSetFile> expectedDataSetFilesSuffix = new ArrayList<>();

        Collections.addAll(expectedDataSetFilesSuffix, dataSetFile4, dataSetFile5);

        PostmanFilterOptions postmanFilterOptionsSuffix = new PostmanFilterOptions();
        List<String> suffixes = new ArrayList<String>() {
            {
                add(".fastq.gz#null");
            }
        };
        postmanFilterOptionsSuffix.setSuffixes(suffixes);

        List<DataSetFile> suffixFilteredDataSetFiles = getPostmanDataFilterer().filterDataSetFiles(allDataSetFiles, postmanFilterOptionsSuffix);

        assertEquals(expectedDataSetFilesSuffix, suffixFilteredDataSetFiles);

        // regex case
        List<DataSetFile> expectedDataSetFilesRegex = new ArrayList<>();

        Collections.addAll(expectedDataSetFilesRegex, dataSetFile4, dataSetFile5);

        PostmanFilterOptions postmanFilterOptionsRegex = new PostmanFilterOptions();
        List<String> regexes = new ArrayList<String>() {
            {
                add(".*FASTQ.*");
            }
        };
        postmanFilterOptionsRegex.setRegexPatterns(regexes);

        List<DataSetFile> regexFilteredDataSetFiles = getPostmanDataFilterer().filterDataSetFiles(allDataSetFiles, postmanFilterOptionsRegex);

        assertEquals(expectedDataSetFilesRegex, regexFilteredDataSetFiles);
    }

    /**
     * tests only the suffix filtering
     * verifies whether the datasetFiles are matching after filtering has been applied
     */
    @Test
    public void testFilterDataSetFilesBySuffix() {
        List<DataSetFile> allDataSetFiles = new ArrayList<>();
        DataSetFile dataSetFile = new DataSetFile();
        dataSetFile.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387")));

        DataSetFile dataSetFile2 = new DataSetFile();
        dataSetFile2.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original")));

        DataSetFile dataSetFile3 = new DataSetFile();
        dataSetFile3.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female")));

        DataSetFile dataSetFile4 = new DataSetFile();
        dataSetFile4.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female/HG00121_ERR031964_1.fastq.gz")));

        DataSetFile dataSetFile5 = new DataSetFile();
        dataSetFile5.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female/HG00121_ERR031964_2.fastq.gz")));

        Collections.addAll(allDataSetFiles, dataSetFile, dataSetFile2, dataSetFile3, dataSetFile4, dataSetFile5);

        // suffix case
        List<DataSetFile> expectedDataSetFilesSuffix = new ArrayList<>();

        Collections.addAll(expectedDataSetFilesSuffix, dataSetFile4, dataSetFile5);

        PostmanFilterOptions postmanFilterOptionsSuffix = new PostmanFilterOptions();
        List<String> suffixes = new ArrayList<String>() {
            {
                add(".fastq.gz#null");
            }
        };
        postmanFilterOptionsSuffix.setSuffixes(suffixes);

        List<DataSetFile> suffixFilteredDataSetFiles = getPostmanDataFilterer().filterDataSetFiles(allDataSetFiles, postmanFilterOptionsSuffix);

        assertEquals(expectedDataSetFilesSuffix, suffixFilteredDataSetFiles);
    }

    /**
     * tests only the regex filtering
     * verifies whether the datasetFiles are matching after filtering has been applied
     */
    @Test
    public void testFilterDataSetFilesByRegex() {
        List<DataSetFile> allDataSetFiles = new ArrayList<>();
        DataSetFile dataSetFile = new DataSetFile();
        dataSetFile.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387")));

        DataSetFile dataSetFile2 = new DataSetFile();
        dataSetFile2.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original")));

        DataSetFile dataSetFile3 = new DataSetFile();
        dataSetFile3.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female")));

        DataSetFile dataSetFile4 = new DataSetFile();
        dataSetFile4.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female/HG00121_ERR031964_1.fastq.gz")));

        DataSetFile dataSetFile5 = new DataSetFile();
        dataSetFile5.setPermId(new DataSetFilePermId(new DataSetPermId("20170210172702893-159387#original/QTGPR032A0_HG00121_Britain_Female/HG00121_ERR031964_2.fastq.gz")));

        Collections.addAll(allDataSetFiles, dataSetFile, dataSetFile2, dataSetFile3, dataSetFile4, dataSetFile5);

        // regex case
        List<DataSetFile> expectedDataSetFilesRegex = new ArrayList<>();

        Collections.addAll(expectedDataSetFilesRegex, dataSetFile4, dataSetFile5);

        PostmanFilterOptions postmanFilterOptionsRegex = new PostmanFilterOptions();
        List<String> regexes = new ArrayList<String>() {
            {
                add(".*FASTQ.*");
            }
        };
        postmanFilterOptionsRegex.setRegexPatterns(regexes);

        List<DataSetFile> regexFilteredDataSetFiles = getPostmanDataFilterer().filterDataSetFiles(allDataSetFiles, postmanFilterOptionsRegex);

        assertEquals(expectedDataSetFilesRegex, regexFilteredDataSetFiles);
    }
}
