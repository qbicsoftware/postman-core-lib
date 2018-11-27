package life.qbic.core;

import java.util.HashMap;
import java.util.Map;

public class SupportedFileTypes {

    /**
     * key: valid filtertype
     * value: description of the file type
     */
    private static Map<String, String> supportedFilterTypes = new HashMap<String, String>() {
        {
            put("ARR", "");
            put("AUDIT", "");
            put("CEL", "");
            put("CSV", "");
            put("EXPERIMENTAL_DESIGN", "");
            put("EXPRESSION_MATRIX", "");
            put("FASTQ", "");
            put("FEATUREXML", "");
            put("GZ", "");
            put("IDXML", "");
            put("JPG", "");
            put("MAT", "");
            put("MZML", "");
            put("PDF", "");
            put("PNG", "");
            put("Q_BMI_IMAGING_DATA", "");
            put("Q_DOCUMENT", "");
            put("Q_EXT_MS_QUALITYCONTROL_RESULTS", "");
            put("Q_EXT_NGS_QUALITYCONTROL_RESULTS", "");
            put("Q_FASTA_DATA", "");
            put("Q_HT_QPCR_DATA", "");
            put("Q_MA_AGILENT_DATA", "");
            put("Q_MA_CHIP_IMAGE", "");
            put("Q_MA_RAW_DATA", "");
            put("Q_MS_MZML_DATA", "");
            put("Q_MS_RAW_DATA", "");
            put("Q_MTB_ARCHIVE", "");
            put("Q_NGS_HLATYPING_DATA", "");
            put("Q_NGS_IMMUNE_MONITORING_DATA", "");
            put("Q_NGS_IONTORRENT_DATA", "");
            put("Q_NGS_MAPPING_DATA", "");
            put("Q_NGS_MTB_DATA", "");
            put("Q_NGS_RAW_DATA", "");
            put("Q_NGS_READ_MATCH_ARCHIVE", "");
            put("Q_NGS_VARIANT_CALLING_DATA", "");
            put("Q_PEPTIDE_DATA", "");
            put("Q_PROJECT_DATA", "");
            put("Q_TEST", "");
            put("Q_VACCINE_CONSTRUCT_DATA", "");
            put("Q_WF_EDDA_BENCHMARK_LOGS", "");
            put("Q_WF_EDDA_BENCHMARK_RESULTS", "");
            put("Q_WF_MA_QUALITYCONTROL_LOGS", "");
            put("Q_WF_MA_QUALITYCONTROL_RESULTS", "");
            put("Q_WF_MS_INDIVIDUALIZED_PROTEOME_LOGS", "");
            put("Q_WF_MS_INDIVIDUALIZED_PROTEOME_RESULTS", "");
            put("Q_WF_MS_LIGANDOMICS_ID_LOGS", "");
            put("Q_WF_MS_LIGANDOMICS_ID_RESULTS", "");
            put("Q_WF_MS_LIGANDOMICS_QC_LOGS", "");
            put("Q_WF_MS_LIGANDOMICS_QC_RESULTS", "");
            put("Q_WF_MS_MAXQUANT_LOGS", "");
            put("Q_WF_MS_MAXQUANT_ORIGINAL_OUT", "");
            put("Q_WF_MS_MAXQUANT_RESULTS", "");
            put("Q_WF_MS_PEAKPICKING_LOGS", "");
            put("Q_WF_MS_PEPTIDEID_LOGS", "");
            put("Q_WF_MS_PEPTIDEID_RESULTS", "");
            put("Q_WF_MS_QUALITYCONTROL_LOGS", "");
            put("Q_WF_MS_QUALITYCONTROL_RESULTS", "");
            put("Q_WF_NGS_16S_TAXONOMIC_PROFILING_LOGS", "");
            put("Q_WF_NGS_EPITOPE_PREDICTION_LOGS", "");
            put("Q_WF_NGS_EPITOPE_PREDICTION_RESULTS", "");
            put("Q_WF_NGS_HLATYPING_LOGS", "");
            put("Q_WF_NGS_HLATYPING_RESULTS", "");
            put("Q_WF_NGS_MAPPING_LOGS", "");
            put("Q_WF_NGS_MAPPING_RESULTS", "");
            put("Q_WF_NGS_QUALITYCONTROL_LOGS", "");
            put("Q_WF_NGS_QUALITYCONTROL_RESULTS", "");
            put("Q_WF_NGS_RNAEXPRESSIONANALYSIS_LOGS", "");
            put("Q_WF_NGS_RNAEXPRESSIONANALYSIS_RESULTS", "");
            put("Q_WF_NGS_SHRNA_COUNTING_LOGS", "");
            put("Q_WF_NGS_SHRNA_COUNTING_RESULTS", "");
            put("Q_WF_NGS_VARIANT_ANNOTATION_LOGS", "");
            put("Q_WF_NGS_VARIANT_ANNOTATION_RESULTS", "");
            put("Q_WF_NGS_VARIANT_CALLING_LOGS", "");
            put("Q_WF_NGS_VARIANT_CALLING_RESULTS", "");
            put("RAW", "");
            put("SHA256SUM", "");
            put("TAR", "");
            put("VCF", "");
            put("UNKNOWN", "");
        }
    };

    public static Map<String, String> getSupportedFilterTypes() {
        return supportedFilterTypes;
    }
}
