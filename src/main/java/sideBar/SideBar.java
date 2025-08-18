package sideBar;

import IDE.mainWindow;
import utils.config;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SideBar extends JPanel {
    private String name;
    private mainWindow parentWindow;
    private RenderFileTree treeRenderer;
    private JScrollPane currentTreePane;

    public SideBar(mainWindow parent, JTabbedPane editor) throws IOException {
        this.parentWindow = parent;
        this.setBounds(0, 0, 320, 800);
        this.setLayout(null);


        this.treeRenderer = new RenderFileTree(this::handleFileOpen);

        this.greetUser(config.getName());
        this.renderProjectDirectory();
    }


    public void renderProjectDirectory() throws IOException {
        renderDirectory(config.getPath());
    }


    public void renderDirectory(String directoryPath) throws IOException {
        try {

            if (currentTreePane != null) {
                this.remove(currentTreePane);
            }

            currentTreePane = treeRenderer.createTreeFromDirectory(directoryPath);
            this.add(currentTreePane);

            revalidate();
            repaint();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading directory: " + directoryPath + "\n" + e.getMessage(),
                    "Directory Error",
                    JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }


    public void refreshFileExplorer() {
        try {
            renderProjectDirectory();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error refreshing file explorer: " + e.getMessage(),
                    "Refresh Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshFileExplorer(String newPath) {
        try {
            renderDirectory(newPath != null ? newPath : config.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error refreshing file explorer: " + e.getMessage(),
                    "Refresh Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleFileOpen(File file) {
        if (parentWindow == null) {
            JOptionPane.showMessageDialog(this,
                    "Cannot open file: Parent window reference is missing.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fileName = file.getName().toLowerCase();

        try {

            int existingTabIndex = parentWindow.findOpenFileTab(file);
            if (existingTabIndex != -1) {
                parentWindow.editorPane.setSelectedIndex(existingTabIndex);
                JOptionPane.showMessageDialog(this, "File is already open.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                String[] options = {"Open in Editor", "Preview HTML", "Both", "Cancel"};
                int choice = JOptionPane.showOptionDialog(this,
                        "How would you like to open this HTML file?",
                        "Open HTML File",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                switch (choice) {
                    case 0:
                        parentWindow.newPopulatedTab(file.getName(), parentWindow.readFileContent(file), parentWindow.detectSyntaxStyle(file.getName()), file);
                        break;
                    case 1:
                        parentWindow.showHtmlPreview(file);
                        break;
                    case 2:
                        parentWindow.newPopulatedTab(file.getName(), parentWindow.readFileContent(file), parentWindow.detectSyntaxStyle(file.getName()), file);
                        parentWindow.showHtmlPreview(file);
                        break;
                    case 3:
                        break;
                }
            } else if (parentWindow.isTextFile(fileName)) {
                parentWindow.newPopulatedTab(file.getName(), parentWindow.readFileContent(file), parentWindow.detectSyntaxStyle(file.getName()), file);
            } else {
                parentWindow.openFileInDefaultBrowser(file);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not read file for opening: " + file.getName() + "\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void greetUser(String name) {
        JPanel greet = new JPanel();
        greet.setBounds(0, 0, 320, 30);
        JLabel yourName = new JLabel();
        if (name != null) {
            yourName.setText("Let's Build " + name);
        } else {
            yourName.setText("Let's Build");
        }
        yourName.setFont(new Font("verdana", Font.BOLD, 18));
        greet.add(yourName);
        this.add(greet);
    }

    public RenderFileTree getTreeRenderer() {
        return treeRenderer;
    }


    public void switchProject(String projectPath) {
        try {
            renderDirectory(projectPath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to switch to project: " + projectPath + "\n" + e.getMessage(),
                    "Project Switch Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}