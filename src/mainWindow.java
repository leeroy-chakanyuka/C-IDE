import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;


public class mainWindow extends JFrame {
    private SideBar sidePanel;
    public JTabbedPane editorPane;
    private Font myFont = new Font("verdana", Font.BOLD, 14);
    private JTextField commandInput;
    private JTextArea outputArea;
    private configReader reader = new configReader(config.getIdeHomePath(), config.getDataFile(), config.META_CONFIG_FILE);

    public mainWindow() throws IOException {

        this.setLayout(null);

        this.editorPane = new JTabbedPane();
        this.editorPane.setFocusable(false);
        this.editorPane.setBounds(320, 5, 1060, 500);
        this.editorPane.setBackground(new Color(203, 108, 230));
        this.add(this.editorPane);
        this.sidePanel = new SideBar(this, editorPane);
        this.add(sidePanel);

        JMenuBar myMenu = new JMenuBar();
        myMenu.putClientProperty("JComponent.sizeVariant", "large");
        myMenu.setBackground(new Color(245, 245, 245));
        myMenu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        createOutputPanel();
        JMenu files = new JMenu("Files");

        files.setFont(myFont);
        JMenuItem newFile = new JMenuItem("New File");
        newFile.setFont(myFont);
        newFile.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        files.add(newFile);
        newFile.addActionListener((e) -> {
            NewFileDialog dialog = new NewFileDialog(this);
            dialog.setVisible(true);

            if (dialog.isCreated()) {
                File newFileOnDisk = dialog.getSelectedFile();
                String syntaxStyle = dialog.getSelectedLanguageSyntax();
                try {
                    // Create the file on disk
                    if (!newFileOnDisk.exists()) {
                        newFileOnDisk.createNewFile();
                    }
                    // Open it in a new tab
                    newPopulatedTab(newFileOnDisk.getName(), "", syntaxStyle, newFileOnDisk);
                    refreshFileExplorer();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error creating new file: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            refreshFileExplorer();
        });

        JMenuItem openFile = new JMenuItem("Open");
        openFile.setFont(myFont);
        files.add(openFile);
        openFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    String content = readFileContent(selectedFile);
                    String syntaxStyle = detectSyntaxStyle(selectedFile.getName());
                    // Check if the file is already open
                    int existingTabIndex = findOpenFileTab(selectedFile);
                    if (existingTabIndex != -1) {
                        editorPane.setSelectedIndex(existingTabIndex);
                        JOptionPane.showMessageDialog(this, "File is already open.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        newPopulatedTab(selectedFile.getName(), content, syntaxStyle, selectedFile);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        JMenu runMenu = new JMenu("Run");
        runMenu.setFont(myFont);
        JMenuItem runCurrentFile = new JMenuItem("Run Current File");
        runCurrentFile.setFont(myFont);
        runCurrentFile.addActionListener(e -> runCurrentFile());
        runMenu.add(runCurrentFile);

        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.setFont(myFont);
        files.add(saveFile);
        saveFile.addActionListener(e -> saveCurrentEditor());

        JMenuItem saveAsFile = new JMenuItem("Save As...");
        saveAsFile.setFont(myFont);
        files.add(saveAsFile);
        saveAsFile.addActionListener(e -> saveCurrentEditorAs());


        JMenu edit = new JMenu("Edit");
        edit.setFont(myFont);
        JMenu viewOnline = new JMenu("View page");
        viewOnline.setFont(myFont);
        JMenu search = new JMenu("Search");
        search.setFont(myFont);

        myMenu.add(files);
        myMenu.add(edit);
        myMenu.add(search);
        myMenu.add(viewOnline);
        myMenu.add(runMenu);

        this.setIconImage(ImageIO.read(new File("resources/icons/logo.png")));
        this.setJMenuBar(myMenu);
        this.setSize(1400, 800);
        this.setTitle("Chax IDE");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    public void refreshFileExplorer() {
        if (sidePanel != null) {
            sidePanel.refreshFileExplorer();
        }
    }

    /**
     * Custom JPanel to hold the RSyntaxTextArea and its associated File.
     * This helps in tracking file path and modification status.
     */
    private class EditorPanel extends JPanel {
        private RSyntaxTextArea textArea;
        private File associatedFile;
        private boolean modified = false;

        public EditorPanel(File file, String content, String syntaxStyle) {
            super(new BorderLayout());
            this.associatedFile = file;
            this.modified = false;

            this.textArea = new RSyntaxTextArea(70, 180);
            this.textArea.setText(content);
            this.textArea.setSyntaxEditingStyle(syntaxStyle);
            this.textArea.setCodeFoldingEnabled(true);
            this.textArea.setAntiAliasingEnabled(true);

            this.textArea.setTabSize(4);
            this.textArea.setWhitespaceVisible(false);
            this.textArea.setMarkOccurrences(true);
            this.textArea.setAutoIndentEnabled(true);
            this.textArea.setPaintTabLines(true);

            this.textArea.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { setModified(true); }
                public void removeUpdate(DocumentEvent e) { setModified(true); }
                public void changedUpdate(DocumentEvent e) {}
            });

//            try {
//                org.fife.ui.rsyntaxtextarea.Theme theme = org.fife.ui.rsyntaxtextarea.Theme.load(
//                        getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
//                theme.apply(this.textArea);
//            } catch (Exception e) {
//                System.out.println("Could not load theme: " + e.getMessage());
//            }

            RTextScrollPane sp = new RTextScrollPane(textArea);
            sp.setLineNumbersEnabled(true);
            sp.setFoldIndicatorEnabled(true);
            this.add(sp, BorderLayout.CENTER);

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(new Color(240, 240, 240));
            JLabel fileInfo = new JLabel("Language: " + getSyntaxStyleName(syntaxStyle) + " | Columns: 180");
            fileInfo.setFont(new Font("Arial", Font.PLAIN, 10));
            infoPanel.add(fileInfo);
            this.add(infoPanel, BorderLayout.SOUTH);
        }

        public RSyntaxTextArea getTextArea() {
            return textArea;
        }

        public File getAssociatedFile() {
            return associatedFile;
        }

        public void setAssociatedFile(File file) {
            this.associatedFile = file;
            updateTabTitleAndInfo();
        }

        public boolean isModified() {
            return modified;
        }

        public void setModified(boolean modified) {
            System.out.println("here");
            this.modified = modified;
            updateTabTitleAndInfo();
        }

        private void updateTabTitleAndInfo() {
            if (getParent() instanceof JTabbedPane) {
                JTabbedPane tabbedPane = (JTabbedPane) getParent();
                int index = tabbedPane.indexOfComponent(this);
                if (index != -1) {
                    String title = (associatedFile != null) ? associatedFile.getName() : "Untitled"; // Fallback
                    if (modified) {
                        tabbedPane.setTitleAt(index, title + "*");
                    } else {
                        tabbedPane.setTitleAt(index, title);
                    }
                    Component tabComp = tabbedPane.getTabComponentAt(index);
                    if (tabComp instanceof JPanel) {
                        for (Component c : ((JPanel) tabComp).getComponents()) {
                            if (c instanceof JLabel) {
                                ((JLabel) c).setText(modified ? title + "*" : title);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    public void newPopulatedTab(String fileName, String content, String syntaxStyle, File file) {
        EditorPanel editorPanel = new EditorPanel(file, content, syntaxStyle);

        int tabIndex = this.editorPane.getTabCount();
        this.editorPane.addTab(fileName, editorPanel);


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
                            this,
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



    public void newEditor(String name, File file) {
        String tabName = (file != null) ? file.getName() : name;
        String content = "";
        String syntaxStyle = SyntaxConstants.SYNTAX_STYLE_NONE;

        EditorPanel editorPanel = new EditorPanel(file, content, syntaxStyle);

        int tabIndex = this.editorPane.getTabCount();
        this.editorPane.addTab(tabName, editorPanel);

        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);

        JLabel tabLabel = new JLabel(tabName);
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
                            this,
                            "File '" + editorPane.getTitleAt(tabToClose).replace("*", "") + "' has unsaved changes. Save before closing?",
                            "Unsaved Changes",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        saveTabContent(tabToClose);
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        return;
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


    public void focusTerminalInput() {
        if (commandInput != null) {
            commandInput.requestFocusInWindow();
        }
    }

    private boolean isTabModified(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < editorPane.getTabCount()) {
            Component comp = editorPane.getComponentAt(tabIndex);
            if (comp instanceof EditorPanel) {
                return ((EditorPanel) comp).isModified();
            }
        }
        return false;
    }

    private void saveTabContent(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < editorPane.getTabCount()) {
            Component comp = editorPane.getComponentAt(tabIndex);
            if (comp instanceof EditorPanel) {
                EditorPanel editorPanel = (EditorPanel) comp;
                File file = editorPanel.getAssociatedFile();
                String content = editorPanel.getTextArea().getText();

                if (file == null) {
                    saveCurrentEditorAs();
                } else {
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(content);
                        editorPanel.setModified(false);
                        JOptionPane.showMessageDialog(this, "File saved successfully!");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error saving file: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void createOutputPanel() {
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBounds(320, 505, 1068, 265);
        outputPanel.setBackground(new Color(73, 69, 69));
        outputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Output",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.WHITE));

        outputArea = new JTextArea();
        outputArea.setBackground(new Color(30, 30, 30));
        outputArea.setForeground(new Color(255, 255, 255));
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setFocusable(false);
        outputArea.setText("Chax IDE Output Console v1.0\n\n");

        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        outputPanel.add(outputScroll, BorderLayout.CENTER);

        this.add(outputPanel);
    }

    public String getCurrentEditorContent() {
        int selectedIndex = this.editorPane.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < editorPane.getTabCount()) {
            Component comp = this.editorPane.getComponentAt(selectedIndex);
            if (comp instanceof EditorPanel) {
                EditorPanel editorPanel = (EditorPanel) comp;
                return editorPanel.getTextArea().getText();
            }
        }
        return null;
    }

    public void saveCurrentEditor() {
        int selectedIndex = this.editorPane.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < editorPane.getTabCount()) {
            saveTabContent(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, "No file selected to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
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

                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(content);
                        editorPanel.setAssociatedFile(file);
                        editorPanel.setModified(false);
                        editorPane.setTitleAt(selectedIndex, file.getName());

                        Component tabComp = editorPane.getTabComponentAt(selectedIndex);
                        if (tabComp instanceof JPanel) {
                            for (Component c : ((JPanel) tabComp).getComponents()) {
                                if (c instanceof JLabel) {
                                    ((JLabel) c).setText(file.getName());
                                    break;
                                }
                            }
                        }
                        JOptionPane.showMessageDialog(this, "File saved successfully!");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error saving file: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file selected to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public int findOpenFileTab(File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            return -1;
        }

        for (int i = 0; i < editorPane.getTabCount(); i++) {
            Component comp = editorPane.getComponentAt(i);
            if (comp instanceof EditorPanel) {
                EditorPanel editorPanel = (EditorPanel) comp;
                File associatedFile = editorPanel.getAssociatedFile();
                if (associatedFile != null) {
                    try {
                        if (associatedFile.getCanonicalPath().equals(canonicalPath)) {
                            return i;
                        }
                    } catch (IOException e) {

                    }
                }
            }
        }
        return -1;
    }


    public String getSyntaxStyleName(String syntaxStyle) {
        switch (syntaxStyle) {
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JAVA:
                return "Java";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT:
                return "JavaScript";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_HTML:
                return "HTML";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_CSS:
                return "CSS";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_XML:
                return "XML";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JSON:
                return "JSON";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_PYTHON:
                return "Python";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_C:
                return "C";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS:
                return "C++";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_CSHARP:
                return "C#";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_PHP:
                return "PHP";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_SQL:
                return "SQL";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_MARKDOWN:
                return "Markdown";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_YAML:
                return "YAML";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH:
                return "Batch";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL:
                return "Shell";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE:
                return "Properties";
            default:
                return "Plain Text";
        }
    }

    public void runCurrentFile() {
        outputArea.setText("Chax IDE Output Console v1.0\n\n");

        int selectedIndex = editorPane.getSelectedIndex();
        if (selectedIndex == -1) {
            outputArea.append("[ERROR] No file selected to run. Please open or create a file.\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
            return;
        }

        EditorPanel currentEditorPanel = (EditorPanel) editorPane.getComponentAt(selectedIndex);


        if (currentEditorPanel.isModified()) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Current file has unsaved changes. Save before running?",
                    "Save File", JOptionPane.YES_NO_CANCEL_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                saveTabContent(selectedIndex);

            } else if (result == JOptionPane.CANCEL_OPTION) {
                outputArea.append("[INFO] Run cancelled by user.\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
                return;
            }

        }

        File fileToRun = currentEditorPanel.getAssociatedFile();
        String fileContent = currentEditorPanel.getTextArea().getText();


        if (fileToRun == null) {
            try {

                String tempFileName = "temp_run_" + System.currentTimeMillis();
                String suggestedExtension = NewFileDialog.getExtensionForSyntax(currentEditorPanel.getTextArea().getSyntaxEditingStyle());
                if (!suggestedExtension.isEmpty() && !tempFileName.endsWith(suggestedExtension)) {
                    tempFileName += suggestedExtension;
                }
                fileToRun = File.createTempFile(tempFileName.replace(".", "_"), suggestedExtension);
                fileToRun.deleteOnExit();

                try (FileWriter writer = new FileWriter(fileToRun)) {
                    writer.write(fileContent);
                }
                outputArea.append("[INFO] Running from temporary file: " + fileToRun.getName() + "\n");

            } catch (IOException ex) {
                outputArea.append("[ERROR] Failed to create temporary file for running: " + ex.getMessage() + "\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
                return;
            }
        }


        String tabTitle = fileToRun.getName();

        if (fileContent == null || fileContent.trim().isEmpty()) {
            outputArea.append("[INFO] Current file is empty. Nothing to run.\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
            return;
        }

        outputArea.append("\n--- Running: " + tabTitle + " ---\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());

        try {
            if (tabTitle.endsWith(".java")) {
                runJavaFile(fileToRun, fileContent);
            } else if (tabTitle.endsWith(".html") || tabTitle.endsWith(".htm")) {
                String[] options = {"Preview HTML", "Open in Browser", "Cancel"};
                int choice = JOptionPane.showOptionDialog(this,
                        "How would you like to run this HTML file?",
                        "Run HTML File",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                switch (choice) {
                    case 0: // Preview HTML
                        showHtmlPreview(fileToRun);
                        break;
                    case 1: // Open in Browser
                        openFileInDefaultBrowser(fileToRun);
                        break;
                    default:
                        outputArea.append("[INFO] HTML run cancelled by user.\n");
                        break;
                }
            } else {
                outputArea.append("[ERROR] Unsupported file type for running: " + tabTitle + "\n");
                outputArea.append("[INFO] Attempting to open with system default application.\n");
                openFileInDefaultBrowser(fileToRun);
            }
        } catch (Exception ex) {
            outputArea.append("[ERROR] An unexpected error occurred while running the file: " + ex.getMessage() + "\n");
        } finally {
            outputArea.append("--- Run finished for: " + tabTitle + " ---\n\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }
    }


    private void runJavaFile(final File sourceFile, final String content) throws IOException, InterruptedException { // Made sourceFile and content final
        final String className = sourceFile.getName().replace(".java", "");
        File tempDirLocal = sourceFile.getParentFile();

        if (tempDirLocal == null || !tempDirLocal.isDirectory()) {
            tempDirLocal = Files.createTempDirectory("chax_java_run_").toFile();
            tempDirLocal.deleteOnExit();
        }

        final File tempDir = tempDirLocal;

        if (sourceFile.getName().startsWith("temp_run_")) {
            try (FileWriter writer = new FileWriter(sourceFile)) {
                writer.write(content);
            }
        }


        outputArea.append("[JAVA] Compiling " + sourceFile.getName() + "...\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());

        new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                ProcessBuilder compilePb = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
                compilePb.directory(tempDir);
                Process compileProcess = compilePb.start();

                readProcessOutput(compileProcess, "[JAVA-COMPILER-ERROR]");
                int compileExitCode = compileProcess.waitFor();

                if (compileExitCode == 0) {
                    publish("[JAVA] Compilation successful.\n");
                    publish("[JAVA] Running " + className + "...\n");

                    ProcessBuilder runPb = new ProcessBuilder("java", "-cp", tempDir.getAbsolutePath(), className);
                    runPb.directory(tempDir);
                    Process runProcess = runPb.start();
                    readProcessOutput(runProcess, "[JAVA-RUNTIME-ERROR]");
                    return runProcess.waitFor();
                } else {
                    return compileExitCode;
                }
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String chunk : chunks) {
                    outputArea.append(chunk);
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                }
            }

            @Override
            protected void done() {
                try {
                    int exitCode = get();
                    if (exitCode == 0) {
                        outputArea.append("[JAVA] Program finished successfully.\n");
                    } else if (exitCode != 0) {
                        outputArea.append("[JAVA] Program exited with code: " + exitCode + " (See errors above).\n");
                    }
                } catch (Exception ex) {
                    outputArea.append("[ERROR] Failed to run Java program: " + ex.getMessage() + "\n");
                } finally {
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
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                }
            }

            private void readProcessOutput(Process process, String errorPrefix) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    publish(line + "\n");
                }

                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    publish(errorPrefix + " " + line + "\n");
                }
            }
        }.execute();
    }

    public void openFileInDefaultBrowser(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(file);
                    outputArea.append("[SYSTEM] Opened '" + file.getName() + "' with system default application.\n");
                } else {
                    outputArea.append("[ERROR] Desktop OPEN action not supported on this system.\n");
                }
            } else {
                outputArea.append("[ERROR] Desktop operations not supported on this system.\n");
            }
        } catch (IOException e) {
            outputArea.append("[ERROR] Could not open file '" + file.getName() + "': " + e.getMessage() + "\n");
        }
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    /**
     * Opens an HTML file in a custom preview window.
     * we should think about doing this in FX
     */
    public void showHtmlPreview(File file) {
        try {
            String content = readFileContent(file);

            JFrame htmlFrame = new JFrame("HTML Preview - " + file.getName());
            htmlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            htmlFrame.setSize(900, 700);

            JTabbedPane tabbedPane = new JTabbedPane();


            JEditorPane htmlPane = new JEditorPane();
            htmlPane.setContentType("text/html");
            htmlPane.setEditable(false);

            try {
                htmlPane.setPage(file.toURI().toURL());
            } catch (IOException e) {
                htmlPane.setText(content);
            }


            htmlPane.addHyperlinkListener(e -> {
                if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        }
                    } catch (Exception ex) {
                        System.err.println("Could not open link: " + ex.getMessage());
                        outputArea.append("[HTML-PREVIEW-ERROR] Could not open link: " + ex.getMessage() + "\n");
                    }
                }
            });

            JScrollPane htmlScrollPane = new JScrollPane(htmlPane);
            tabbedPane.addTab("Preview", htmlScrollPane);

            JTextArea sourceArea = new JTextArea(content);
            sourceArea.setEditable(false);
            sourceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            sourceArea.setTabSize(2);
            sourceArea.setBackground(new Color(40, 44, 52));
            sourceArea.setForeground(new Color(171, 178, 191));
            sourceArea.setCaretColor(Color.WHITE);

            JScrollPane sourceScrollPane = new JScrollPane(sourceArea);
            tabbedPane.addTab("Source", sourceScrollPane);

            //  buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> {
                try {
                    String newContent = readFileContent(file);
                    htmlPane.setText(newContent);
                    sourceArea.setText(newContent);
                    outputArea.append("[HTML-PREVIEW] Refreshed content for " + file.getName() + "\n");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(htmlFrame,
                            "Error refreshing file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    outputArea.append("[HTML-PREVIEW-ERROR] Error refreshing file: " + ex.getMessage() + "\n");
                }
            });

            JButton openInBrowserButton = new JButton("Open in Browser");
            openInBrowserButton.addActionListener(e -> {
                openFileInDefaultBrowser(file);
            });

            buttonPanel.add(refreshButton);
            buttonPanel.add(openInBrowserButton);

            htmlFrame.setLayout(new BorderLayout());
            htmlFrame.add(tabbedPane, BorderLayout.CENTER);
            htmlFrame.add(buttonPanel, BorderLayout.SOUTH);

            htmlFrame.setLocationRelativeTo(null);
            htmlFrame.setVisible(true);
            outputArea.append("[HTML-PREVIEW] HTML preview opened for " + file.getName() + "\n");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not read HTML file for preview: " + file.getName() + "\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            outputArea.append("[HTML-PREVIEW-ERROR] Could not read HTML file: " + e.getMessage() + "\n");
        }
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

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

    public String detectSyntaxStyle(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".java")) return SyntaxConstants.SYNTAX_STYLE_JAVA;
        else if (lowerName.endsWith(".js") || lowerName.endsWith(".mjs")) return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
        else if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) return SyntaxConstants.SYNTAX_STYLE_HTML;
        else if (lowerName.endsWith(".css")) return SyntaxConstants.SYNTAX_STYLE_CSS;
        else if (lowerName.endsWith(".xml") || lowerName.endsWith(".xhtml")) return SyntaxConstants.SYNTAX_STYLE_XML;
        else if (lowerName.endsWith(".json")) return SyntaxConstants.SYNTAX_STYLE_JSON;
        else if (lowerName.endsWith(".py")) return SyntaxConstants.SYNTAX_STYLE_PYTHON;
        else if (lowerName.endsWith(".c") || lowerName.endsWith(".h")) return SyntaxConstants.SYNTAX_STYLE_C;
        else if (lowerName.endsWith(".cpp") || lowerName.endsWith(".cxx") || lowerName.endsWith(".hpp") || lowerName.endsWith(".cc")) return SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
        else if (lowerName.endsWith(".cs")) return SyntaxConstants.SYNTAX_STYLE_CSHARP;
        else if (lowerName.endsWith(".php")) return SyntaxConstants.SYNTAX_STYLE_PHP;
        else if (lowerName.endsWith(".sql")) return SyntaxConstants.SYNTAX_STYLE_SQL;
        else if (lowerName.endsWith(".bat") || lowerName.endsWith(".cmd")) return SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH;
        else if (lowerName.endsWith(".sh") || lowerName.endsWith(".bash")) return SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
        else if (lowerName.endsWith(".properties")) return SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE;
        else if (lowerName.endsWith(".yml") || lowerName.endsWith(".yaml")) return SyntaxConstants.SYNTAX_STYLE_YAML;
        else if (lowerName.endsWith(".md") || lowerName.endsWith(".markdown")) return SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
        return SyntaxConstants.SYNTAX_STYLE_NONE;
    }

    public boolean isTextFile(String fileName) {
        String[] textExtensions = {
                ".txt", ".java", ".js", ".html", ".css", ".xml", ".json",
                ".md", ".py", ".cpp", ".c", ".h", ".cs", ".php", ".sql",
                ".properties", ".yml", ".yaml", ".log", ".bat", ".sh", ".mjs"
        };

        String lowerName = fileName.toLowerCase();
        for (String ext : textExtensions) {
            if (lowerName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
