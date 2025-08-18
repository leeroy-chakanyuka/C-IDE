package utils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class handleDirs {

    private final JFrame parentFrame;
    private final JTextField pathField;
    private final JTextField nameField;
    private static final String DATA_FILE_NAME = "data.txt";
    private static final String META_CONFIG_FILE = "chax_ide_config.path";

    /**
     * These are listeners to returns outputs of processing to welcome
     */
    public interface SetupCompletionListener {
        void onSetupComplete(String ideHomePath, String userName) throws IOException;
        void onSetupCancelled();
        void onSetupError(String message, Throwable cause);
    }

    private SetupCompletionListener listener;

    public handleDirs(JFrame parentFrame, JTextField pathField, JTextField nameField) {
        this.parentFrame = parentFrame;
        this.pathField = pathField;
        this.nameField = nameField;
    }

    public void setSetupCompletionListener(SetupCompletionListener listener) {
        this.listener = listener;
    }

    /**
     * Opens a directory chooser, processes the selection, writes configuration,
     * and notifies the listener if theres sucess or error
     */
    public void handleIdeSetup() {
        configWriter writer = null;
        JFileChooser up = new JFileChooser();
        up.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        up.setDialogTitle("Select IDE Home Directory");
        int result = up.showOpenDialog(parentFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = up.getSelectedFile();
            String selectedPath = selectedDirectory.getAbsolutePath();
            pathField.setText(selectedPath);

            System.out.println("Selected directory: " + selectedPath);

            String outPath = pathField.getText();
            String outName = nameField.getText();

            if (outPath != null && !outPath.isBlank() && outName != null && !outName.isBlank()) {
                try {
                    //new writer here because the path could have changed
                    writer = new configWriter(outPath, DATA_FILE_NAME, META_CONFIG_FILE);
                    writer.writeData(outPath, outName, parentFrame);
                    writer.writeIdeHomePathToMetaConfig(outPath);

                    if (listener != null) {
                        listener.onSetupComplete(outPath, outName);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Error accessing file system during setup: " + ex.getMessage(),
                            "File System Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    if (listener != null) {
                        listener.onSetupError("File system error during setup.", ex);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                        "Please enter your name and select a valid IDE home path.",
                        "Input Required", JOptionPane.WARNING_MESSAGE);
                if (listener != null) {
                    listener.onSetupError("Incomplete input for setup.", null);
                }
            }

        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("Directory selection cancelled by user.");
            if (listener != null) {
                listener.onSetupCancelled();
            }
        }
    }
}