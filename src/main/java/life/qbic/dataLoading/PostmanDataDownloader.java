package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.core.PostmanFilterOptions;

import java.io.IOException;
import java.util.List;

public interface PostmanDataDownloader {

    void downloadRequestedFilesOfDatasets(final List<String> IDs,
                                                 final PostmanFilterOptions postmanFilterOptions,
                                                 final PostmanDataFinder postmanDataFinder,
                                                 final String outputPath) throws IOException;

//    void downloadFilesFilteredByIDsSuffix(final String ident,
//                                    final List<DataSetFilePermId> foundFilteredIDs,
//                                    final String outputPath) throws IOException;


}
