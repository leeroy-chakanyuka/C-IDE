package menu;

import codeEditor.EditorPanel;
import codeEditor.Tabs;
import sideBar.*;
import utils.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;

public class FileActionHandler {

    private final SideBar sidePanel;
    public JTabbedPane editorPane;
    public JFrame owner;
    private final Tabs tab;
    private final handleFiles fileHandler = new handleFiles();
    private String lastOpenedDirectory;


    private final configReader reader = config.getReaderInstance();
    private final configWriter writer = config.getWriterInstance();

    public FileActionHandler(SideBar sidePanel, JTabbedPane editorPane, JFrame owner ){
        this.sidePanel = sidePanel;
        this.editorPane = editorPane;
        this.owner = owner;
        tab = new Tabs(sidePanel, editorPane, owner);
    }

    public void createNewFile(){
        NewFileDialog dialog = new NewFileDialog(owner);
        dialog.setVisible(true);
        if (dialog.isCreated()) {
            createFile(dialog);
        }
    }

    public void createFile(NewFileDialog dialog){
        File newFileOnDisk = dialog.getSelectedFile();
        String syntaxStyle = dialog.getSelectedLanguageSyntax();
        try {
            // Create the file on disk
            if (!newFileOnDisk.exists()) {
                newFileOnDisk.createNewFile();
            }
            // Open it in a new tab
            tab.newPopulatedTab(newFileOnDisk.getName(), "", syntaxStyle, newFileOnDisk);

            // Refresh the sidebar to show the newly created file
            if (sidePanel != null) {
                sidePanel.refreshFileExplorer();
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(owner,
                    "Error creating new file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveFile(){
        tab.saveCurrentEditor();
    }

    public void saveFileAs(){
        tab.saveCurrentEditorAs();
    }

    public void openFile(){
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(owner);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try{
                String content = fileHandler.readFileContent(selectedFile);
                String syntaxStyle = fileHandler.detectSyntaxStyle(selectedFile.getName());

                int existingTabIndex = tab.findOpenFileTab(selectedFile);
                if (existingTabIndex != -1) {
                    editorPane.setSelectedIndex(existingTabIndex);
                    JOptionPane.showMessageDialog(owner, "File is already open.", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    tab.newPopulatedTab(selectedFile.getName(), content, syntaxStyle, selectedFile);
                }
            }catch (IOException ex) {
                JOptionPane.showMessageDialog(owner, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void createFolder(){
        NewFolderDialog dialog = new NewFolderDialog(owner);
        dialog.setVisible(true);
        if(dialog.isCreated()){
            File newFolder = dialog.getSelectedFolder();

            boolean created = newFolder.mkdirs();
            if(created){
                JOptionPane.showMessageDialog(owner,
                        "Folder created successfully: " + newFolder.getAbsolutePath(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                if (sidePanel != null) {
                    sidePanel.refreshFileExplorer();
                }
            } else {
                JOptionPane.showMessageDialog(owner,
                        "Failed to create folder. It might already exist or a permission issue occurred.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveAllFiles(){
        for (int i = 0; i < editorPane.getTabCount(); i++) {
            Component comp = editorPane.getComponentAt(i);
            if (comp instanceof EditorPanel) {
                EditorPanel editorPanel = (EditorPanel) comp;
                if (editorPanel.isModified()) {
                    tab.saveTabContent(i);
                }
            }
        }

    }

    public void openFolder(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(owner);

        if(result == JFileChooser.APPROVE_OPTION){
            File selectedFolder = fileChooser.getSelectedFile();
            if(selectedFolder != null && selectedFolder.isDirectory()){
                lastOpenedDirectory = selectedFolder.getAbsolutePath();

                try {
                    sidePanel.renderDirectory(selectedFolder.getAbsolutePath());
                    JOptionPane.showMessageDialog(owner, "Folder opened: " + lastOpenedDirectory);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(owner,
                            "Error opening folder: " + selectedFolder.getAbsolutePath() + "\n" + e.getMessage(),
                            "Folder Open Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void closeCurrentEditor() {
        int selectedIndex = editorPane.getSelectedIndex();
        if (selectedIndex != -1) {
            tab.closeTab(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(owner, "No file is currently open.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void closeAllEditors() {
        // Loop until all tabs are closed
        while (editorPane.getTabCount() > 0) {
            tab.closeTab(0);
        }
    }

    private void closeAllTabs() {
        // Work backwards to avoid index shifting issues
        for (int i = editorPane.getTabCount() - 1; i >= 0; i--) {
            if (!tab.closeTab(i)) {

                break;
            }
        }
    }

    public void exitApplication() {
        closeAllEditors();


        if (editorPane.getTabCount() == 0) {
            System.exit(0);
        }
    }


}
