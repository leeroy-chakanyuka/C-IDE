import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;

public class EditorPanel extends JPanel {
    private RSyntaxTextArea textArea;
    private File associatedFile;
    private boolean modified = false;

    public EditorPanel(File file, String content, String syntaxStyle) {
        super(new BorderLayout());
        this.associatedFile = file;
        // A newly created file or opened file is not modified initially
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
            public void changedUpdate(DocumentEvent e) {} // Not used for text changes
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
        JLabel fileInfo = new JLabel("Language: " + getSyntaxStyleName(syntaxStyle) );
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
}