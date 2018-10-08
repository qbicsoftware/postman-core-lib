package life.qbic;

import life.qbic.util.RegexFilterDownloadUtilKt;

public class App {

    public static void main(String[] args) {
        RegexFilterDownloadUtilKt.testRegex("jobscript.FastQC.", "jobscript.FastQC.blablabla");
    }
}
