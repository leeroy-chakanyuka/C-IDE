//package menu;
//
//import IDE.mainWindow;
//import utils.NewFileDialog;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
//public class RunActionHandler {
//
//    private JTextArea outputArea;
//    private JFrame owner;
//    public RunActionHandler(JTextArea outputArea){
//        this.outputArea = outputArea;
//    }
//
//    public void runCurrentFile(mainWindow.EditorPanel editorPane, int selectedIndex) {
//        outputArea.setText("Chax IDE Output Console v1.0\n\n");
//
//        if (selectedIndex == -1) {
//            outputArea.append("[ERROR] No file selected to run. Please open or create a file.\n");
//            outputArea.setCaretPosition(outputArea.getDocument().getLength());
//            return;
//        }
//
//        mainWindow.EditorPanel currentEditorPanel = (mainWindow.EditorPanel) editorPane.getComponentAt((Point) selectedIndex);
//
//
//        if (currentEditorPanel.isModified()) {
//            int result = JOptionPane.showConfirmDialog(owner,
//                    "Current file has unsaved changes. Save before running?",
//                    "Save File", JOptionPane.YES_NO_CANCEL_OPTION);
//
//            if (result == JOptionPane.YES_OPTION) {
//                saveTabContent(selectedIndex);
//
//            } else if (result == JOptionPane.CANCEL_OPTION) {
//                outputArea.append("[INFO] Run cancelled by user.\n");
//                outputArea.setCaretPosition(outputArea.getDocument().getLength());
//                return;
//            }
//
//        }
//
//        File fileToRun = currentEditorPanel.getAssociatedFile();
//        String fileContent = currentEditorPanel.getTextArea().getText();
//
//
//        if (fileToRun == null) {
//            try {
//
//                String tempFileName = "temp_run_" + System.currentTimeMillis();
//                String suggestedExtension = NewFileDialog.getExtensionForSyntax(currentEditorPanel.getTextArea().getSyntaxEditingStyle());
//                if (!suggestedExtension.isEmpty() && !tempFileName.endsWith(suggestedExtension)) {
//                    tempFileName += suggestedExtension;
//                }
//                fileToRun = File.createTempFile(tempFileName.replace(".", "_"), suggestedExtension);
//                fileToRun.deleteOnExit();
//
//                try (FileWriter writer = new FileWriter(fileToRun)) {
//                    writer.write(fileContent);
//                }
//                outputArea.append("[INFO] Running from temporary file: " + fileToRun.getName() + "\n");
//
//            } catch (IOException ex) {
//                outputArea.append("[ERROR] Failed to create temporary file for running: " + ex.getMessage() + "\n");
//                outputArea.setCaretPosition(outputArea.getDocument().getLength());
//                return;
//            }
//        }
//
//
//        String tabTitle = fileToRun.getName();
//
//        if (fileContent == null || fileContent.trim().isEmpty()) {
//            outputArea.append("[INFO] Current file is empty. Nothing to run.\n");
//            outputArea.setCaretPosition(outputArea.getDocument().getLength());
//            return;
//        }
//
//        outputArea.append("\n--- Running: " + tabTitle + " ---\n");
//        outputArea.setCaretPosition(outputArea.getDocument().getLength());
//
//        try {
//            if (tabTitle.endsWith(".java")) {
//                runJavaFile(fileToRun, fileContent);
//            } else if (tabTitle.endsWith(".html") || tabTitle.endsWith(".htm")) {
//                String[] options = {"Preview HTML", "Open in Browser", "Cancel"};
//                int choice = JOptionPane.showOptionDialog(this,
//                        "How would you like to run this HTML file?",
//                        "Run HTML File",
//                        JOptionPane.YES_NO_CANCEL_OPTION,
//                        JOptionPane.QUESTION_MESSAGE,
//                        null,
//                        options,
//                        options[0]);
//
//                switch (choice) {
//                    case 0: // Preview HTML
//                        showHtmlPreview(fileToRun);
//                        break;
//                    case 1: // Open in Browser
//                        openFileInDefaultBrowser(fileToRun);
//                        break;
//                    default:
//                        outputArea.append("[INFO] HTML run cancelled by user.\n");
//                        break;
//                }
//            } else {
//                outputArea.append("[ERROR] Unsupported file type for running: " + tabTitle + "\n");
//                outputArea.append("[INFO] Attempting to open with system default application.\n");
//                openFileInDefaultBrowser(fileToRun);
//            }
//        } catch (Exception ex) {
//            outputArea.append("[ERROR] An unexpected error occurred while running the file: " + ex.getMessage() + "\n");
//        } finally {
//            outputArea.append("--- Run finished for: " + tabTitle + " ---\n\n");
//            outputArea.setCaretPosition(outputArea.getDocument().getLength());
//        }
//    }
//}
