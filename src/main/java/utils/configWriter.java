package utils;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class configWriter {
    private String IDE_HOME_PATH;
    private String DATA_FILE_NAME;
    private String META_CONFIG_FILE;

    public configWriter(String IDE_HOME_PATH, String DATA_FILE_NAME, String META_CONFIG_FILE){
        this.DATA_FILE_NAME = DATA_FILE_NAME;
        this.IDE_HOME_PATH = IDE_HOME_PATH;
        this.META_CONFIG_FILE = META_CONFIG_FILE;
    }
    public void writeIdeHomePathToMetaConfig(String path) throws IOException {
        String userHome = System.getProperty("user.home");
        File metaConfigFile = new File(userHome, this.META_CONFIG_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaConfigFile))) {
            writer.write(path);
            System.out.println("Wrote IDE_HOME_PATH to meta utils.config: " + metaConfigFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing meta utils.config file '" + metaConfigFile.getAbsolutePath() + "': " + e.getMessage());
            throw e;
        }
    }
    /**
     * Writes the determined IDE_HOME_PATH to a predictable meta-utils.config file
     * in the user's home directory. This is called after the user selects
     * their IDE home path during the initial onboarding.
     *
     * @param outPath The IDE home path to save.
     * @throws IOException If there's an error writing the file.
     */
    public void writeData(String outPath, String outName, JFrame window) throws IOException {
        File ideHomeDir = new File(outPath);
        if (!ideHomeDir.exists()) {
            ideHomeDir.mkdirs(); // Create directories if they don't exist
        }
        File dataFile = new File(outPath + File.separator + DATA_FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            writer.write("1\n");
            writer.write(outPath + "\n");
            writer.write(outName);
            System.out.println("Configuration data (data.txt) saved to: " + dataFile.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(window,
                    "Error saving configuration data: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }

}
