package org.codeontology;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Utils {
    public static String encloseUri(String uri) {
        if (!uri.startsWith("<") && !uri.startsWith("(")) {
            return "<" + uri + ">";
        }
        return uri;
    }

    public static void configureLog() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
    }
}
