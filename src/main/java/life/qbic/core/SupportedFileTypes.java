package life.qbic.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for all supported file types which can be used for data filtering.
 *
 * PostmanFilterOptions' fileType has to be one of these here!
 */
public class SupportedFileTypes {

    /**
     * key: valid filtertype
     * value: description of the file type
     */
    private static Map<String, String> supportedFilterTypes = new HashMap<String, String>() {
        {
            put("ARR", "The ARR file contains any sample attributes and array information of an affymetrix microarray.");
            put("AUDIT", "An Audit file is an XML file that tracks the processing of each physical array processed by AGCC." +
                    " An Audit file is produced for each physical array and tracks all the processing steps that were performed on the array," +
                    " including multiple scannings and regridding. The audit file has the same root name as the physical array.");
            put("CEL", "Affymetrix CEL format raw file. The CEL file stores the results of the intensity calculations on the pixel values of the DAT file." +
                    " This includes an intensity value, standard deviation of the intensity, the number of pixels used to calculate the intensity value," +
                    " a flag to indicate an outlier as calculated by the algorithm and a user defined flag indicating the feature should be excluded from future analysis." +
                    " This data is used by the CHP writer software to extract the actual data of interest.");
            put("CSV", "A Comma-Separated Values file.");
            put("EXPERIMENTAL_DESIGN", "Contains TSV of the experimental design format used by the wizard." +
                    " To be attached to single experimental layers, namely the ones containing data to be analyzed with respect to the experimental design.");
            put("EXPRESSION_MATRIX", "Contains a CSV file measuring expression of some kind (genes, proteins)." +
                    " It should contain references to IDs used in openbis or the experimental design.");
            put("FASTQ", "A FASTQ file of a NGS experiment.");
            put("FEATUREXML", "featureXML file");
            put("GZ", "GZ archive file");
            put("IDXML", "IdXML file");
            put("JPG", "Lossy graphics file.");
            put("MAT", "Matlab formatted file");
            put("MZML", "Mass spectrometry data format. Unifiying mzXML and mzData formats, as released at the 2008 American Society for Mass Spectrometry Meeting.");
            put("PDF", "Portable Document Format file");
            put("PNG", "Portable Networks Graphics File");
            put("Q_BMI_IMAGING_DATA", "Imaging data (set of DICOM files) packaged as tarball." +
                    " This tarball comprises all imaging sequences (could be modeled separately in the future).");
            put("Q_DOCUMENT", "A document file carrying metainformation about one or more samples or sample sources");
            put("Q_EXT_MS_QUALITYCONTROL_RESULTS", "Results produced by OpenMS QC as qcML.");
            put("Q_EXT_NGS_QUALITYCONTROL_RESULTS", "Results produced by the FastQC.");
            put("Q_FASTA_DATA", "Dataset holding fasta file which can contain protein sequences or nucleotide sequences.");
            put("Q_HT_QPCR_DATA", "Data of a high-throughput qPCR run");
            put("Q_MA_AGILENT_DATA", "Agilent text format for microarrays");
            put("Q_MA_CHIP_IMAGE", "Images or Pseudo-images of microarray chips. Can for example be used for quality control.");
            put("Q_MA_RAW_DATA", "Designates the raw data generated during an MicroArray run. Such data is for example stored in a vendor-specific format (e.g., CEL files).");
            put("Q_MS_MZML_DATA", "Raw microspectrometry data that has been converted to mzML format.");
            put("Q_MS_RAW_DATA", "Designates the raw data generated during an MS run." +
                    " Such data is either stored in a vendor-specific format (e.g., binary RAW files for Thermo Orbitraps) or in the open mzML standard.");
            put("Q_MTB_ARCHIVE", "A ZIP file containing the reported somatic and germline SNVs and CNVs, plus the additional diagnosis." +
                    " The content are 6 tab-separated files, following the specification with CeGaT, IMGAG and ZPM." +
                    " This is the information that gets redirected to the CentraXX system and is stored here as additional backup.");
            put("Q_NGS_HLATYPING_DATA", "Dataset holding data of an HLA Typing experiment");
            put("Q_NGS_IMMUNE_MONITORING_DATA", "Dataset holding data of an immune monitoring experiment");
            put("Q_NGS_IONTORRENT_DATA", "Torrent Suite data folder packaged as tar. This data folder is intended to be re-imported to the Torrent Suite cloud solution at Pathology if needed." +
                    " VCF/XLS files are stored separately (as children).");
            put("Q_NGS_MAPPING_DATA", "Dataset holding aligned genomic data");
            put("Q_NGS_MTB_DATA", "Variant information and diagnosis data for the molecular tumor board.");
            put("Q_NGS_RAW_DATA", "Dataset holding raw data of an Next Generation Sequencing experiment");
            put("Q_NGS_READ_MATCH_ARCHIVE", "Files ending in .rma are in a compressed binary format called RMA (read-match archive) produced by MALT (MEGAN alignment tool)." +
                    " There are multiple versions, e.g. RMA2, RMA3, RMA6");
            put("Q_NGS_VARIANT_CALLING_DATA", "Dataset holding data of a variant calling experiment.");
            put("Q_PEPTIDE_DATA", "Dataset to hold peptide data stored in different file formats." +
                    " Peptide data may be stored as fasta file, simple peptide list or a tab-separated file with an identifier and a sequence column e.g..");
            put("Q_PROJECT_DATA", "Datasets that are attached to an instance of Q_Attachment_Sample and should be presented to the user on the project level." +
                    " This should only include datasets that are not specific workflow results.");
            put("Q_TEST", "this is just for quick testing");
            put("Q_VACCINE_CONSTRUCT_DATA", "Dataset to hold vaccine construct data such as the list of selected epitopes");
            put("Q_WF_EDDA_BENCHMARK_LOGS", "");
            put("Q_WF_EDDA_BENCHMARK_RESULTS", "");
            put("Q_WF_MA_QUALITYCONTROL_LOGS", "Log files of a microarray quality control run.");
            put("Q_WF_MA_QUALITYCONTROL_RESULTS", "Results of a microarray quality control run.");
            put("Q_WF_MS_INDIVIDUALIZED_PROTEOME_LOGS", "");
            put("Q_WF_MS_INDIVIDUALIZED_PROTEOME_RESULTS", "");
            put("Q_WF_MS_LIGANDOMICS_ID_LOGS", "");
            put("Q_WF_MS_LIGANDOMICS_ID_RESULTS", "");
            put("Q_WF_MS_LIGANDOMICS_QC_LOGS", "");
            put("Q_WF_MS_LIGANDOMICS_QC_RESULTS", "");
            put("Q_WF_MS_MAXQUANT_LOGS", "Log files of a maxquant run.");
            put("Q_WF_MS_MAXQUANT_ORIGINAL_OUT", "This data set type holds all the files that a MaxQuant run produces, except the input raw files. Data is held inside a tar.gz.");
            put("Q_WF_MS_MAXQUANT_RESULTS", "The result files generated by MaxQuant. Depending the operational mode of MaxQuant, the results may or may not contain protein quantification results.");
            put("Q_WF_MS_PEAKPICKING_LOGS", "Peak Picking of one or more mzml files done using openMS.");
            put("Q_WF_MS_PEPTIDEID_LOGS", "Log files created by the Peptide Identification Workflow run.");
            put("Q_WF_MS_PEPTIDEID_RESULTS", "Results produced by the Peptide Identification Workflow.");
            put("Q_WF_MS_QUALITYCONTROL_LOGS", "Log files produced by the OpenMS QC workflow.");
            put("Q_WF_MS_QUALITYCONTROL_RESULTS", "Results produced by the OpenMS QC workflow as qcML.");
            put("Q_WF_NGS_16S_TAXONOMIC_PROFILING_LOGS", "");
            put("Q_WF_NGS_EPITOPE_PREDICTION_LOGS", "");
            put("Q_WF_NGS_EPITOPE_PREDICTION_RESULTS", "");
            put("Q_WF_NGS_HLATYPING_LOGS", "");
            put("Q_WF_NGS_HLATYPING_RESULTS", "");
            put("Q_WF_NGS_MAPPING_LOGS", "");
            put("Q_WF_NGS_MAPPING_RESULTS", "");
            put("Q_WF_NGS_QUALITYCONTROL_LOGS", "");
            put("Q_WF_NGS_QUALITYCONTROL_RESULTS", "Results produced by the FastQC workflow.");
            put("Q_WF_NGS_RNAEXPRESSIONANALYSIS_LOGS", "");
            put("Q_WF_NGS_RNAEXPRESSIONANALYSIS_RESULTS", "");
            put("Q_WF_NGS_SHRNA_COUNTING_LOGS", "");
            put("Q_WF_NGS_SHRNA_COUNTING_RESULTS", "");
            put("Q_WF_NGS_VARIANT_ANNOTATION_LOGS", "");
            put("Q_WF_NGS_VARIANT_ANNOTATION_RESULTS", "");
            put("Q_WF_NGS_VARIANT_CALLING_LOGS", "");
            put("Q_WF_NGS_VARIANT_CALLING_RESULTS", "");
            put("RAW", "Raw data file");
            put("SHA256SUM", "Checksum for registered data files");
            put("TAR", "tar archive (tarball) file");
            put("VCF", "A Variant Call Format (VCF) file");
            put("UNKNOWN", "Unknown");
        }
    };

    /**
     * prints the supported file types in human readable format
     *
     */
    public static void printSupportedFileTypes() {
        System.out.println("FileFormat: description \n");
        supportedFilterTypes.forEach((fileFormat, description) -> {
            System.out.println(fileFormat + ": " + wrapText(description, 80));
        });
    }

    /**
     * wraps text into a more human readable format
     * ensures, that words are still together and not split somewhere inbetween
     *
     * @param stringToWrap string to make human readable
     * @param textWidth specifies the max length of a single line -> else new line + two tab characters
     * @return wrapped String
     */
    private static String wrapText(final String stringToWrap, final int textWidth) {
        StringBuilder temp = new StringBuilder();
        StringBuilder sentence = new StringBuilder();

        String[] array = stringToWrap.split(" ");

        for (String word : array) {
            if ((temp.length() + word.length()) < textWidth) {
                temp.append(" ").append(word);
            } else {
                sentence.append(temp).append("\n\t\t");
                temp = new StringBuilder(word);
            }

        }

        return (sentence.toString().replaceFirst(" ", "") + temp);
    }

    public static Map<String, String> getSupportedFilterTypes() {
        return supportedFilterTypes;
    }
}
