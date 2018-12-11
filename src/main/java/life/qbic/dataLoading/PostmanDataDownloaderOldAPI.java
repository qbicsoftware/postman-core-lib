package life.qbic.dataLoading;

import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import life.qbic.core.PostmanFilterOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * This uses the old and deprecated openBIS API!
 * The old API features some better optimized code for downloading files
 */
public class PostmanDataDownloaderOldAPI implements PostmanDataDownloader {

    private final static Logger LOG = LogManager.getLogger(PostmanDataDownloaderOldAPI.class);

    private IDataStoreServerApi dataStoreServer;
    private String sessionToken;

    private final int DEFAULTBUFFERSIZE = 1024;
    private int buffersize = DEFAULTBUFFERSIZE;

    public PostmanDataDownloaderOldAPI(IDataStoreServerApi dataStoreServer, String sessionToken, int buffersize) {
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
        this.buffersize = buffersize;
    }

    public PostmanDataDownloaderOldAPI(IDataStoreServerApi dataStoreServer, String sessionToken) {
        this.dataStoreServer = dataStoreServer;
        this.sessionToken = sessionToken;
    }

    @Override
    public void downloadRequestedFilesOfDatasets(List<String> IDs, PostmanFilterOptions postmanFilterOptions, PostmanDataFinder postmanDataFinder, String outputPath) throws IOException {
//        IDssComponent component = DssComponentFactory.tryCreate(sessionToken, url, timeOut);
//
//        IDataSetDss dataSet = component.getDataSet(dataSetCode);
//        FileInfoDssDTO[] fileInfos = dataSet.listFiles("/", true);
//
//        for (FileInfoDssDTO fileInfo : fileInfos)
//        {
//            if (fileInfo.isDirectory() == false)
//            {
//                InputStream is = dataSet.getFile(fileInfo.getPathInDataSet());
//                // read input stream
//            }
//        }
    }

    @Override
    public void downloadFilesFilteredByIDs(String ident, List<DataSetFilePermId> foundFilteredIDs, String outputPath) throws IOException {

    }
}
