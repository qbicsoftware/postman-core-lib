package life.qbic;


import life.qbic.core.SupportedFileTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostmanCoreLib {

    private final static Logger LOG = LogManager.getLogger(PostmanCoreLib.class);

    public static void main(String[] args) {
        SupportedFileTypes.printSupportedFileTypes();
    }

}

