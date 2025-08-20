package IDE;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;

import menu.menuBar;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import sideBar.SideBar;
import utils.*;
import sideBar.*;



public class mainWindow extends JFrame {
    private static UndoManager undoManager;
    private SideBar sidePanel;
    public JTabbedPane editorPane;
    private Font myFont = new Font("verdana", Font.BOLD, 14);
    private JTextField commandInput;
    private JTextArea outputArea;
    private configReader reader = new configReader(config.getIdeHomePath(), config.getDataFile(), config.META_CONFIG_FILE);
    private Font menuFont = new Font("vedana", Font.PLAIN, 14 );
    public static RSyntaxTextArea textArea = new RSyntaxTextArea(70, 180);

    public mainWindow() throws IOException {
        this.undoManager = new UndoManager();

        this.setLayout(null);

        this.editorPane = new JTabbedPane();
        this.editorPane.setFocusable(false);
        this.editorPane.setBounds(320, 5, 1060, 500);
        this.editorPane.setBackground(new Color(203, 108, 230));
        this.add(this.editorPane);
        this.createOutputPanel();
        this.sidePanel = new SideBar(this, editorPane, outputArea);
        this.add(sidePanel);

        JMenuBar myMenu = new menuBar(this.sidePanel, this.editorPane, this, outputArea);

        this.setIconImage(ImageIO.read(getClass().getClassLoader().getResource("icons/logo.png")));
        this.setJMenuBar(myMenu);
        this.setSize(1400, 800);
        this.setTitle("Chax IDE");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    public void setEditorPaneColor(Color col){
        this.editorPane.setBackground(col);
    }
    /**
     * Custom JPanel to hold the RSyntaxTextArea and its associated File.
     * This helps in tracking file path and modification status.
     */
    public static class EditorPanel extends JPanel {

        private File associatedFile;
        private boolean modified = false;
        private static JPanel infoPanel;
        public EditorPanel(File file, String content, String syntaxStyle) {
            super(new BorderLayout());
            this.associatedFile = file;
            this.modified = false;


            textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
            textArea.setText(content);
            textArea.setSyntaxEditingStyle(syntaxStyle);
            textArea.setCodeFoldingEnabled(true);
            textArea.setAntiAliasingEnabled(true);

            textArea.setTabSize(4);
            textArea.setWhitespaceVisible(false);
            textArea.setMarkOccurrences(true);
            textArea.setAutoIndentEnabled(true);
            textArea.setPaintTabLines(true);

            textArea.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { setModified(true); }
                public void removeUpdate(DocumentEvent e) { setModified(true); }
                public void changedUpdate(DocumentEvent e) {}
            });



            RTextScrollPane sp = new RTextScrollPane(textArea);
            sp.setLineNumbersEnabled(true);
            sp.setFoldIndicatorEnabled(true);
            this.add(sp, BorderLayout.CENTER);

            infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(new Color(240, 240, 240));
            JLabel fileInfo = new JLabel("Language: " + getSyntaxStyleName(syntaxStyle) + " | Columns: 180");
            fileInfo.setFont(new Font("Arial", Font.PLAIN, 10));
            infoPanel.add(fileInfo);
            this.add(infoPanel, BorderLayout.SOUTH);
        }

        public static void setLangPanelCol(Color col){
            infoPanel.setBackground(col);
        }

        public UndoManager getUndoManager() {
            return undoManager;
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

    public static String getSyntaxStyleName(String syntaxStyle) {
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
