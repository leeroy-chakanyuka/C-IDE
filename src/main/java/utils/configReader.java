package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class configReader {
    private String IDE_HOME_PATH;
    private String DATA_FILE_NAME;
    private String META_CONFIG_FILE;

    public configReader(String IDE_HOME_PATH, String DATA_FILE_NAME, String META_CONFIG_FILE){
       this.DATA_FILE_NAME = DATA_FILE_NAME;
       this.IDE_HOME_PATH = IDE_HOME_PATH;
       this.META_CONFIG_FILE = META_CONFIG_FILE;

    }

    /**
     * Reads the IDE_HOME_PATH from a meta-utils.config file located
     * in the user's home directory. This allows the application to find
     * its main configuration (data.txt) on subsequent launches.
     *
     * @return The IDE home path string, or null if not found/readable.
     */
    public String readIdeHomePathFromMetaConfig() {
        String userHome = System.getProperty("user.home");
        File metaConfigFile = new File(userHome, this.META_CONFIG_FILE);
        if (!metaConfigFile.exists() || metaConfigFile.length() == 0) {
            System.out.println("Info: Meta utils.config file not found at " + metaConfigFile.getAbsolutePath() + ". This is normal for the first run.");
            return null;
        }

        //these if and try catches could be put in seperate logic really
        try (BufferedReader reader = new BufferedReader(new FileReader(metaConfigFile))) {
            String pathLine = reader.readLine();
            if (pathLine != null && !pathLine.isBlank()) {
                System.out.println("Read IDE_HOME_PATH from meta utils.config: " + pathLine.trim());
                return pathLine.trim();
            }
        } catch (IOException e) {
            System.err.println("Error reading meta utils.config file '" + metaConfigFile.getAbsolutePath() + "': " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String readName() {
        if (IDE_HOME_PATH == null) {
            System.err.println("Warning: IDE_HOME_PATH is null, cannot read name from data.txt.");
            return null;
        }
        File dataFile = new File(IDE_HOME_PATH + File.separator + DATA_FILE_NAME);
        if (!dataFile.exists() || dataFile.length() == 0) {
            System.out.println("Info: Data file '" + dataFile.getAbsolutePath() + "' does not exist or is empty. Cannot read name.");
            return null;
        }

        try (BufferedReader
                     reader = new BufferedReader(new FileReader(dataFile))) {
            reader.readLine(); // Skip initial state (line 1)
            reader.readLine(); // Skip path (line 2)
            String nameLine = reader.readLine(); // Read name (line 3)
            if (nameLine != null && !nameLine.isBlank()) {
                return nameLine.trim();
            } else {
                System.out.println("Warning: Name line in '" + dataFile.getAbsolutePath() + "' is empty or null.");
            }
        } catch (IOException e) {
            System.err.println("Error reading name from '" + dataFile.getAbsolutePath() + "': " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method reads the first line of the text file which is 0 or 1
     * that then decides on whether we render this screen or not (it is onboarding after all)
     * @return 0 / 1
     */
    public int readInitialState() {

        if (IDE_HOME_PATH == null) { // if the path doesn't exist then we have to create one / render this screen
            return 0;
        }
        File dataFile = new File(IDE_HOME_PATH + File.separator + DATA_FILE_NAME);  //create the path to where the file is located
        if (!dataFile.exists() || dataFile.length() == 0) {
            System.out.println("Info: Main data file '" + dataFile.getAbsolutePath() + "' does not exist or is empty. Defaulting to 0 (not logged in).");
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line = reader.readLine(); // Read the isLoggedIn state
            if (line != null) {
                try {
                    return Integer.parseInt(line.trim());
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Data file '" + dataFile.getAbsolutePath() + "' contains invalid initial state: '" + line + "'. Defaulting to 0.");
                    e.printStackTrace();
                    return 0;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading initial state from '" + dataFile.getAbsolutePath() + "': " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }


    public  String readPath() {
        if (IDE_HOME_PATH == null) {
            System.err.println("Warning: IDE_HOME_PATH is null, cannot read path from data.txt.");
            return null;
        }
        File dataFile = new File(IDE_HOME_PATH + File.separator + DATA_FILE_NAME);
        if (!dataFile.exists() || dataFile.length() == 0) {
            System.out.println("Info: Data file '" + dataFile.getAbsolutePath() + "' does not exist or is empty. Cannot read path from data.txt.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            reader.readLine();
            String pathLine = reader.readLine();
            if (pathLine != null && !pathLine.isBlank()) {
                return pathLine.trim();
            } else {
                System.out.println("Warning: Path line in '" + dataFile.getAbsolutePath() + "' is empty or null.");
            }
        } catch (IOException e) {
            System.err.println("Error reading path from '" + dataFile.getAbsolutePath() + "': " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
