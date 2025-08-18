import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class FileActionHandler {

    private SideBar sidePanel;
    public JTabbedPane editorPane;
    public JFrame owner;

    public FileActionHandler(SideBar sidePanel, JTabbedPane editorPane, JFrame owner ){
        this.sidePanel = sidePanel;
        this.editorPane = editorPane;
        this.owner = owner;

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
            newPopulatedTab(newFileOnDisk.getName(), "", syntaxStyle, newFileOnDisk);

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

    public void newPopulatedTab(String fileName, String content, String syntaxStyle, File file) {
        EditorPanel editorPanel = new EditorPanel(file, content, syntaxStyle);

        int tabIndex = this.editorPane.getTabCount();
        this.editorPane.addTab(fileName, editorPanel);

        // Create custom tab component with close button
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);

        JLabel tabLabel = new JLabel(fileName);
        tabLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton closeButton = new JButton("×");
        closeButton.setPreferredSize(new Dimension(17, 17));
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setBorder(null);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setRolloverEnabled(true);
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeButton.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeButton.setForeground(Color.BLACK);
            }
        });


        closeButton.addActionListener(e -> {
            int tabToClose = editorPane.indexOfTabComponent(tabPanel);
            if (tabToClose != -1) {
                EditorPanel panel = (EditorPanel) editorPane.getComponentAt(tabToClose);
                if (panel.isModified()) {
                    int result = JOptionPane.showConfirmDialog(
                            owner,
                            "File '" + editorPane.getTitleAt(tabToClose).replace("*", "") + "' has unsaved changes. Save before closing?",
                            "Unsaved Changes",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        saveTabContent(tabToClose);
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        return; // Don't close
                    }
                }
                editorPane.removeTabAt(tabToClose);
            }
        });

        tabPanel.add(tabLabel);
        tabPanel.add(Box.createHorizontalStrut(5));
        tabPanel.add(closeButton);

        this.editorPane.setTabComponentAt(tabIndex, tabPanel);
        this.editorPane.setSelectedIndex(tabIndex);
        editorPanel.getTextArea().requestFocusInWindow();
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
//    public String detectSyntaxStyle(String fileName) {
//        String lowerName = fileName.toLowerCase();
//        if (lowerName.endsWith(".java")) return SyntaxConstants.SYNTAX_STYLE_JAVA;
//        else if (lowerName.endsWith(".js") || lowerName.endsWith(".mjs")) return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
//        else if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) return SyntaxConstants.SYNTAX_STYLE_HTML;
//        else if (lowerName.endsWith(".css")) return SyntaxConstants.SYNTAX_STYLE_CSS;
//        else if (lowerName.endsWith(".xml") || lowerName.endsWith(".xhtml")) return SyntaxConstants.SYNTAX_STYLE_XML;
//        else if (lowerName.endsWith(".json")) return SyntaxConstants.SYNTAX_STYLE_JSON;
//        else if (lowerName.endsWith(".py")) return SyntaxConstants.SYNTAX_STYLE_PYTHON;
//        else if (lowerName.endsWith(".c") || lowerName.endsWith(".h")) return SyntaxConstants.SYNTAX_STYLE_C;
//        else if (lowerName.endsWith(".cpp") || lowerName.endsWith(".cxx") || lowerName.endsWith(".hpp") || lowerName.endsWith(".cc")) return SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
//        else if (lowerName.endsWith(".cs")) return SyntaxConstants.SYNTAX_STYLE_CSHARP;
//        else if (lowerName.endsWith(".php")) return SyntaxConstants.SYNTAX_STYLE_PHP;
//        else if (lowerName.endsWith(".sql")) return SyntaxConstants.SYNTAX_STYLE_SQL;
//        else if (lowerName.endsWith(".bat") || lowerName.endsWith(".cmd")) return SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH;
//        else if (lowerName.endsWith(".sh") || lowerName.endsWith(".bash")) return SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
//        else if (lowerName.endsWith(".properties")) return SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE;
//        else if (lowerName.endsWith(".yml") || lowerName.endsWith(".yaml")) return SyntaxConstants.SYNTAX_STYLE_YAML;
//        else if (lowerName.endsWith(".md") || lowerName.endsWith(".markdown")) return SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
//        return SyntaxConstants.SYNTAX_STYLE_NONE;
//    }
//    public void openFile(){
//        JFileChooser fileChooser = new JFileChooser();
//        int result = fileChooser.showOpenDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File selectedFile = fileChooser.getSelectedFile();
//            try {
//                String content = readFileContent(selectedFile);
//                String syntaxStyle = detectSyntaxStyle(selectedFile.getName());
//                // Check if the file is already open
//                int existingTabIndex = findOpenFileTab(selectedFile);
//                if (existingTabIndex != -1) {
//                    editorPane.setSelectedIndex(existingTabIndex);
//                    JOptionPane.showMessageDialog(this, "File is already open.", "Info", JOptionPane.INFORMATION_MESSAGE);
//                } else {
//                    newPopulatedTab(selectedFile.getName(), content, syntaxStyle, selectedFile);
//                }
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//    }

public String readFileContent(File file) throws IOException {
    StringBuilder content = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
    }
    return content.toString();
}
}
