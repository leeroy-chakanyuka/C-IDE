package execution;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;

import IDE.mainWindow;
import codeEditor.Tabs;
import sideBar.SideBar;
import utils.NewFileDialog;

/**
 * Handles the execution of files in the IDE.
 * Supports Java compilation and execution, HTML preview, and other file types.
 */
public class codeRunner {
    private final mainWindow parentWindow;
    private final JTabbedPane editorPane;
    private final JTextArea outputArea;
    private final SideBar sb;
    private Java runJava;
    private HTML runHTML;
    private Tabs tab;

    public codeRunner(mainWindow parentWindow, JTabbedPane editorPane, JTextArea outputArea) throws IOException {
        this.parentWindow = parentWindow;
        this.editorPane = editorPane;
        this.outputArea = outputArea;
        this.runJava = new Java(parentWindow, editorPane, outputArea);
        this.runHTML = new HTML(parentWindow, editorPane, outputArea);
        this.sb = new SideBar(parentWindow, editorPane, outputArea);
        this.tab = new Tabs(sb, editorPane, parentWindow);
    }

    public void runCurrentFile() {
        clearOutput();

        int selectedIndex = editorPane.getSelectedIndex();
        if (selectedIndex == -1) {
            appendOutput("[ERROR] No file selected to run. Please open or create a file.\n");
            return;
        }

        mainWindow.EditorPanel currentEditorPanel = (mainWindow.EditorPanel) editorPane.getComponentAt(selectedIndex);
        // Check if file needs to be saved
        if (currentEditorPanel.isModified()) {
            int result = handleUnsavedChanges();
            if (result == JOptionPane.CANCEL_OPTION) {
                appendOutput("[INFO] Run cancelled by user.\n");
                return;
            } else if (result == JOptionPane.YES_OPTION) {
                tab.saveCurrentEditorAs();
            }
        }

        File fileToRun = prepareFileForRunning(currentEditorPanel);
        if (fileToRun == null) return;

        String fileContent = currentEditorPanel.getTextArea().getText();
        if (isFileEmpty(fileContent)) {
            appendOutput("[INFO] Current file is empty. Nothing to run.\n");
            return;
        }

        executeFile(fileToRun, fileContent);
    }

    /**
     * Clears the output console and adds header
     */
    private void clearOutput() {
        outputArea.setText("Chax IDE Output Console v1.0\n\n");
    }

    /**
     * Appends text to output and scrolls to bottom
     */
    void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    /**
     * Handles dialog for unsaved changes
     */
    private int handleUnsavedChanges() {
        return JOptionPane.showConfirmDialog(parentWindow,
                "Current file has unsaved changes. Save before running?",
                "Save File", JOptionPane.YES_NO_CANCEL_OPTION);
    }

    private File prepareFileForRunning(mainWindow.EditorPanel editorPanel) {
        File fileToRun = editorPanel.getAssociatedFile();

        if (fileToRun == null) {
            try {
                String tempFileName = "temp_run_" + System.currentTimeMillis();
                String suggestedExtension = NewFileDialog.getExtensionForSyntax(
                        editorPanel.getTextArea().getSyntaxEditingStyle());

                if (!suggestedExtension.isEmpty() && !tempFileName.endsWith(suggestedExtension)) {
                    tempFileName += suggestedExtension;
                }

                fileToRun = File.createTempFile(tempFileName.replace(".", "_"), suggestedExtension);
                fileToRun.deleteOnExit();

                try (FileWriter writer = new FileWriter(fileToRun)) {
                    writer.write(editorPanel.getTextArea().getText());
                }

                appendOutput("[INFO] Running from temporary file: " + fileToRun.getName() + "\n");

            } catch (IOException ex) {
                appendOutput("[ERROR] Failed to create temporary file for running: " + ex.getMessage() + "\n");
                return null;
            }
        }

        return fileToRun;
    }

    private boolean isFileEmpty(String content) {
        return content == null || content.trim().isEmpty();
    }

    private void executeFile(File fileToRun, String fileContent) {
        String fileName = fileToRun.getName();
        appendOutput("\n--- Running: " + fileName + " ---\n");

        try {
            if (fileName.endsWith(".java")) {
                runJava.runJavaFile(fileToRun, fileContent);
            } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                runHTML.handleHtmlFile(fileToRun);
            } else {
                handleUnsupportedFile(fileToRun, fileName);
            }
        } catch (Exception ex) {
            appendOutput("[ERROR] An unexpected error occurred while running the file: " + ex.getMessage() + "\n");
        } finally {
            appendOutput("--- Run finished for: " + fileName + " ---\n\n");
        }
    }

    private void handleUnsupportedFile(File file, String fileName) {
        appendOutput("[ERROR] Unsupported file type for running: " + fileName + "\n");
        appendOutput("[INFO] Attempting to open with system default application.\n");
        this.openFileInDefaultBrowser(file);
    }

    void cleanupTempFiles(File sourceFile, File tempDir, String className) {
        if (sourceFile.getName().startsWith("temp_run_")) {
            sourceFile.delete();
            File classFile = new File(tempDir, className + ".class");
            if (classFile.exists()) {
                classFile.delete();
            }
            if (tempDir.getName().startsWith("chax_java_run_")) {
                if (tempDir.isDirectory() && tempDir.list().length == 0) {
                    tempDir.delete();
                }
            }
        }
    }

    public void openFileInDefaultBrowser(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(file);
                    outputArea.append("[SYSTEM] Opened '" + file.getName() + "' with system default application.\n");
                } else {
                    outputArea.append("[ERROR] Desktop OPEN action not supported on  system.\n");
                }
            } else {
                outputArea.append("[ERROR] Desktop operations not supported on this system.\n");
            }
        } catch (IOException e) {
            outputArea.append("[ERROR] Could not open file '" + file.getName() + "': " + e.getMessage() + "\n");
        }
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
}