package utils;

import javax.swing.*;
import java.io.IOException;

/**
 * A central utility class for accessing application-wide configuration settings.
 * This class encapsulates the reading of configuration data, providing
 * static accessors for common settings like user name, IDE home path, etc.
 */
public class config {

    private static final String DATA_FILE_NAME = "data.txt";
    public static final String META_CONFIG_FILE = "chax_ide_config.path";
    private static configReader instanceReader;
    private static configWriter instanceWriter;


    public static synchronized configReader getReaderInstance() {
        if (instanceReader == null) {
            String initialIdeHomePath = new configReader(null, DATA_FILE_NAME, META_CONFIG_FILE)
                    .readIdeHomePathFromMetaConfig();
            instanceReader = new configReader(initialIdeHomePath, DATA_FILE_NAME, META_CONFIG_FILE);
        }
        return instanceReader;
    }

    public static synchronized configWriter getWriterInstance() {
        if (instanceWriter == null) {
            String ideHomePath = getIdeHomePath();
            instanceWriter = new configWriter(ideHomePath, DATA_FILE_NAME, META_CONFIG_FILE);
        }
        return instanceWriter;
    }


    public static String getName() {
        return getReaderInstance().readName();
    }

    public static String getPath() {
        return getReaderInstance().readPath();
    }

    public static String getIdeHomePath() {
        return getReaderInstance().readIdeHomePathFromMetaConfig();
    }

    public static String getDataFile(){
        return DATA_FILE_NAME;
    }


    public static void writeData(String outPath, String outName, JFrame window) throws IOException {
        getWriterInstance().writeData(outPath, outName, window);
    }

    public static void writeIdeHomePath(String path) throws IOException {
        getWriterInstance().writeIdeHomePathToMetaConfig(path);
    }

    public static int getInitialState() {
        return getReaderInstance().readInitialState();
    }
}