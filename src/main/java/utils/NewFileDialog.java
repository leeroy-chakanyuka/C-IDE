package utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class NewFileDialog extends JDialog {

    private JTextField fileNameField;
    private JTextField directoryField;
    private JComboBox<String> languageComboBox;
    private JButton createButton;
    private JButton cancelButton;
    private JButton browseButton;

    private File selectedDirectory;
    private String selectedLanguageSyntax;
    private boolean created = false;

    private static final Map<String, String> LANGUAGE_MAP = new LinkedHashMap<>();
    static {
        LANGUAGE_MAP.put("Plain Text", SyntaxConstants.SYNTAX_STYLE_NONE);
        LANGUAGE_MAP.put("Java", SyntaxConstants.SYNTAX_STYLE_JAVA);
        LANGUAGE_MAP.put("HTML", SyntaxConstants.SYNTAX_STYLE_HTML);
        LANGUAGE_MAP.put("CSS", SyntaxConstants.SYNTAX_STYLE_CSS);
        LANGUAGE_MAP.put("JavaScript", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        LANGUAGE_MAP.put("Python", SyntaxConstants.SYNTAX_STYLE_PYTHON);
        LANGUAGE_MAP.put("XML", SyntaxConstants.SYNTAX_STYLE_XML);
        LANGUAGE_MAP.put("JSON", SyntaxConstants.SYNTAX_STYLE_JSON);
        LANGUAGE_MAP.put("C", SyntaxConstants.SYNTAX_STYLE_C);
        LANGUAGE_MAP.put("C++", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        LANGUAGE_MAP.put("C#", SyntaxConstants.SYNTAX_STYLE_CSHARP);
        LANGUAGE_MAP.put("PHP", SyntaxConstants.SYNTAX_STYLE_PHP);
        LANGUAGE_MAP.put("SQL", SyntaxConstants.SYNTAX_STYLE_SQL);
        LANGUAGE_MAP.put("Markdown", SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        LANGUAGE_MAP.put("YAML", SyntaxConstants.SYNTAX_STYLE_YAML);
        LANGUAGE_MAP.put("Batch (Windows)", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
        LANGUAGE_MAP.put("Shell (Unix)", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        LANGUAGE_MAP.put("Properties", SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
    }

    private static final Map<String, String> EXTENSION_MAP = new LinkedHashMap<>();
    static {
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_NONE, ".txt");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_JAVA, ".java");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_HTML, ".html");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_CSS, ".css");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, ".js");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_PYTHON, ".py");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_XML, ".xml");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_JSON, ".json");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_C, ".c");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS, ".cpp");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_CSHARP, ".cs");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_PHP, ".php");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_SQL, ".sql");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_MARKDOWN, ".md");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_YAML, ".yaml");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH, ".bat");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL, ".sh");
        EXTENSION_MAP.put(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE, ".properties");
    }

    public NewFileDialog(Frame owner) {
        super(owner, "Create New File", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        addListeners();
        populateDefaultValues();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // File Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("File Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        fileNameField = new JTextField(25);
        formPanel.add(fileNameField, gbc);

        // Directory
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Directory:"), gbc);
        gbc.gridx = 1;
        directoryField = new JTextField(25);
        directoryField.setEditable(false);
        formPanel.add(directoryField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        browseButton = new JButton("Browse...");
        formPanel.add(browseButton, gbc);

        // Language
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Language:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        languageComboBox = new JComboBox<>(LANGUAGE_MAP.keySet().toArray(new String[0]));
        formPanel.add(languageComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        createButton.addActionListener(e -> onCreate());
        cancelButton.addActionListener(e -> onCancel());
        browseButton.addActionListener(e -> onBrowse());
    }

    private void populateDefaultValues() {
        // Use utils.config path instead of current working directory
        String configPath = config.getPath();
        if (configPath != null && !configPath.isEmpty()) {
            selectedDirectory = new File(configPath);
            // Verify the directory exists, create if it doesn't
            if (!selectedDirectory.exists()) {
                selectedDirectory.mkdirs();
            }
        } else {
            // Fallback to current working directory if utils.config path is not available
            selectedDirectory = new File(System.getProperty("user.dir"));
        }

        directoryField.setText(selectedDirectory.getAbsolutePath());

        languageComboBox.setSelectedItem("Plain Text");
        selectedLanguageSyntax = LANGUAGE_MAP.get("Plain Text");
    }

    private void onBrowse() {
        JFileChooser fileChooser = new JFileChooser(selectedDirectory);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = fileChooser.getSelectedFile();
            directoryField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void onCreate() {
        String fileName = fileNameField.getText().trim();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "File name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedDirectory == null || !selectedDirectory.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Please select a valid directory.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedLanguageName = (String) languageComboBox.getSelectedItem();
        selectedLanguageSyntax = LANGUAGE_MAP.get(selectedLanguageName);
        String defaultExtension = EXTENSION_MAP.get(selectedLanguageSyntax);

        if (!fileName.contains(".")) {
            fileNameField.setText(fileName + defaultExtension);
        } else {
            String userExtension = fileName.substring(fileName.lastIndexOf("."));
            if (!userExtension.equals(defaultExtension) && !selectedLanguageSyntax.equals(SyntaxConstants.SYNTAX_STYLE_NONE)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "The file extension '" + userExtension + "' does not match the default for " + selectedLanguageName + " (" + defaultExtension + ").\nDo you want to use " + defaultExtension + " instead?",
                        "Extension Mismatch", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    fileNameField.setText(fileName.substring(0, fileName.lastIndexOf(".")) + defaultExtension);
                }
            }
        }

        File finalFile = new File(selectedDirectory, fileNameField.getText());
        if (finalFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(this,
                    "File '" + finalFile.getName() + "' already exists in this directory. Overwrite?",
                    "File Exists", JOptionPane.YES_NO_OPTION);
            if (overwrite == JOptionPane.NO_OPTION) {
                return;
            }
        }

        created = true;
        dispose();
    }

    private void onCancel() {
        created = false;
        dispose();
    }

    public boolean isCreated() {
        return created;
    }

    public File getSelectedFile() {
        if (created) {
            return new File(selectedDirectory, fileNameField.getText());
        }
        return null;
    }

    public String getSelectedLanguageSyntax() {
        return selectedLanguageSyntax;
    }

    public static String getExtensionForSyntax(String syntaxConstant) {
        return EXTENSION_MAP.getOrDefault(syntaxConstant, ".txt");
    }
}