package codeEditor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.File;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import utils.handleFiles;

/**
 * Custom JPanel to hold the RSyntaxTextArea and its associated File.
 * This helps in tracking file path and modification status.
 */
public class EditorPanel extends JPanel {
    private File associatedFile;
    private boolean modified = false;
    private static JPanel infoPanel;
    private RSyntaxTextArea textArea;
    private UndoManager undoManager;
    private handleFiles fh;

    public EditorPanel(File file, String content, String syntaxStyle) {
        super(new BorderLayout());
        this.associatedFile = file;
        this.modified = false;
        this.undoManager = new UndoManager();
        this.textArea = new RSyntaxTextArea(70, 180);
        fh = new handleFiles();

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
        JLabel fileInfo = new JLabel("Language: " + fh.getSyntaxStyleName(syntaxStyle) + " | Columns: 180");
        fileInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        infoPanel.add(fileInfo);
        this.add(infoPanel, BorderLayout.SOUTH);
    }
    public boolean isModified() {
        return modified;
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public File getAssociatedFile() {
        return associatedFile;
    }

    public void setAssociatedFile(File file) {
        this.associatedFile = file;
        updateTabTitleAndInfo();
    }

    public void setModified(boolean modified) {
        this.modified = modified;
        updateTabTitleAndInfo();
    }

    public static void setLangPanelCol(Color col) {
        if (infoPanel != null) {
            infoPanel.setBackground(col);
        }
    }

    private void updateTabTitleAndInfo() {
        if (getParent() instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) getParent();
            int index = tabbedPane.indexOfComponent(this);
            if (index != -1) {
                String title = (associatedFile != null) ? associatedFile.getName() : "Untitled";
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