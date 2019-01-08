package life.qbic;


import life.qbic.core.PostmanFilterOptions;
import life.qbic.core.authentication.PostmanConfig;
import life.qbic.core.authentication.PostmanSessionManager;
import life.qbic.dataLoading.PostmanDataDownloaderOldAPI;
import life.qbic.dataLoading.PostmanDataDownloaderV3;
import life.qbic.dataLoading.PostmanDataFinder;
import life.qbic.exceptions.PostmanOpenBISLoginFailedException;
import life.qbic.io.parser.PostmanPropertiesParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PostmanCoreLib {

    private final static Logger LOG = LogManager.getLogger(PostmanCoreLib.class);

    public static void main(String[] args) throws IOException, PostmanOpenBISLoginFailedException {
        List<String> ids = new ArrayList<>();
        ids.add("/CONFERENCE_DEMO/QTGPR014A2");
        PostmanFilterOptions postmanFilterOptions = new PostmanFilterOptions();
        List<String> datasetCodes = new ArrayList<>();
        datasetCodes.add("20170210172702893-159387");
        datasetCodes.add("20170214193727212-160772");
        postmanFilterOptions.setDatasetCodes(datasetCodes);
//        List<String> regexPatterns = new ArrayList<>();
//        regexPatterns.add(".html");
//        postmanFilterOptions.setRegexPatterns(regexPatterns);

        PostmanConfig postmanConfig = PostmanPropertiesParser.parserProperties("qbicPropertiesFile.conf");
        PostmanSessionManager postmanSessionManager = PostmanSessionManager.getPostmanSessionManager();
        postmanSessionManager.loginToOpenBIS(postmanConfig);

        PostmanDataFinder postmanDataFinder = new PostmanDataFinder(postmanSessionManager.getApplicationServer(),
                postmanSessionManager.getDataStoreServer(), postmanSessionManager.getSessionToken());

//        long startTime = System.nanoTime();
//        PostmanDataDownloaderOldAPI postmanDataDownloaderOldAPI = new PostmanDataDownloaderOldAPI("https://qbis.qbic.uni-tuebingen.de/openbis/openbis", postmanSessionManager.getSessionToken());
//        postmanDataDownloaderOldAPI.downloadRequestedFilesOfDatasets(ids, postmanFilterOptions, postmanDataFinder, "/home/lukas/Desktop/postman_output/");
//        long endTime = System.nanoTime();
//
//        long duration = (endTime - startTime);
//        System.out.println("old API" + duration);

        long startTime2 = System.nanoTime();
        PostmanDataDownloaderV3 postmanDataDownloaderV3 = new PostmanDataDownloaderV3(postmanSessionManager.getDataStoreServer(), postmanSessionManager.getSessionToken());
        postmanDataDownloaderV3.downloadRequestedFilesOfDatasets(ids, postmanFilterOptions, postmanDataFinder, "/home/lukas/Desktop/postman_output/");
        long endTime2 = System.nanoTime();

        long duration2 = (endTime2 - startTime2);
        System.out.println("new API" + duration2);
    }

}

