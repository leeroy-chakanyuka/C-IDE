package codeEditor;

import sideBar.SideBar;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveOperations {

    private SideBar sidePanel;
    public JTabbedPane editorPane;
    public JFrame owner;

    public SaveOperations(SideBar sidePanel, JTabbedPane editorPane, JFrame owner ){
        this.sidePanel = sidePanel;
        this.editorPane = editorPane;
        this.owner = owner;
    }

    void saveTabContent(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < editorPane.getTabCount()) {
            Component comp = editorPane.getComponentAt(tabIndex);
            if (comp instanceof EditorPanel) {
                EditorPanel editorPanel = (EditorPanel) comp;
                File file = editorPanel.getAssociatedFile();
                String content = editorPanel.getTextArea().getText();

                if (file == null) { // This case should be rare now, only if a tab was created without a file
                    saveCurrentEditorAs(); // Force "Save As" for truly unassociated content
                } else {
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(content);
                        editorPanel.setModified(false);
                        JOptionPane.showMessageDialog(owner, "File saved successfully!");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(owner,
                                "Error saving file: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public void saveCurrentEditorAs() {
        int selectedIndex = this.editorPane.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < editorPane.getTabCount()) {
            Component comp = editorPane.getComponentAt(selectedIndex);
            if (comp instanceof EditorPanel) {
                EditorPanel editorPanel = (EditorPanel) comp;
                String content = editorPanel.getTextArea().getText();
                File currentFile = editorPanel.getAssociatedFile();

                JFileChooser fileChooser = new JFileChooser();
                if (currentFile != null) {
                    fileChooser.setSelectedFile(currentFile);
                } else {
                    String tabTitle = editorPane.getTitleAt(selectedIndex).replace("*", "");
                    fileChooser.setSelectedFile(new File(tabTitle));
                }

                int result = fileChooser.showSaveDialog(owner);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(content);
                        editorPanel.setAssociatedFile(file); // Update the file association
                        editorPanel.setModified(false); // Mark as not modified
                        // Update tab title to reflect new file name
                        editorPane.setTitleAt(selectedIndex, file.getName());
                        // Update custom tab component label
                        Component tabComp = editorPane.getTabComponentAt(selectedIndex);
                        if (tabComp instanceof JPanel) {
                            for (Component c : ((JPanel) tabComp).getComponents()) {
                                if (c instanceof JLabel) {
                                    ((JLabel) c).setText(file.getName());
                                    break;
                                }
                            }
                        }
                        JOptionPane.showMessageDialog(owner, "File saved successfully!");
                        // Refresh the sidebar after saving a new file (or saving as a new file)
                        if (sidePanel != null) {
                            sidePanel.refreshFileExplorer();
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(owner,
                                "Error saving file: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(owner, "No file selected to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void saveCurrentEditor() {
        int selectedIndex = this.editorPane.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < editorPane.getTabCount()) {
            saveTabContent(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(owner, "No file selected to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
