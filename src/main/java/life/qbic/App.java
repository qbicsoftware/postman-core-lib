package life.qbic;


import life.qbic.io.commandline.CommandLineParser;
import life.qbic.io.commandline.OpenBISPasswordParser;
import life.qbic.io.commandline.PostmanCommandLineOptions;
import life.qbic.model.dataLoading.QbicDataDownloader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class App {

    private final static Logger LOG = LogManager.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        // parse and verify all commandline parameters
        PostmanCommandLineOptions commandLineParameters = CommandLineParser.parseAndVerifyCommandLineParameters(args);

        // login to OpenBIS
        QbicDataDownloader qbicDataDownloader = loginToOpenBIS(commandLineParameters);

        // download all requested files by the user
        qbicDataDownloader.downloadRequestedFilesOfDatasets(commandLineParameters, qbicDataDownloader);
    }

    /**
     * logs into OpenBIS
     * asks for and verifies password
     *
     * @param commandLineParameters
     * @return
     */
    private static QbicDataDownloader loginToOpenBIS(PostmanCommandLineOptions commandLineParameters) {
        System.out.format("Please provide password for user \'%s\':\n", commandLineParameters.user);

        String password = OpenBISPasswordParser.readPasswordFromInputStream();

        if (password.isEmpty()) {
            System.out.println("You need to provide a password.");
            System.exit(1);
        }

        QbicDataDownloader qbicDataDownloader = new QbicDataDownloader(commandLineParameters.as_url,
                commandLineParameters.dss_url,
                commandLineParameters.user,
                password,
                commandLineParameters.bufferMultiplier * 1024,
                commandLineParameters.datasetType);
        int returnCode = qbicDataDownloader.login();
        LOG.info(String.format("OpenBis login returned with %s", returnCode));
        if (returnCode != 0) {
            LOG.error("Connection to openBIS failed.");
            System.exit(1);
        }
        LOG.info("Connection to openBIS was successful.");

        return qbicDataDownloader;
    }
}

