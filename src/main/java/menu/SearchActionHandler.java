package menu;

import codeEditor.EditorPanel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class SearchActionHandler {

    private JTabbedPane editorPane;
    private Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);

    public SearchActionHandler(JTabbedPane editorPane) {
        this.editorPane = editorPane;
    }

    public void find() {
        if (editorPane.getTabCount() == 0) return;

        String searchTerm = JOptionPane.showInputDialog(editorPane, "Find:", "Find", JOptionPane.PLAIN_MESSAGE);
        if (searchTerm == null || searchTerm.isEmpty()) {
            return;
        }

        Component selectedComponent = editorPane.getSelectedComponent();
        if (selectedComponent instanceof EditorPanel) {
            JTextArea textArea = ((EditorPanel) selectedComponent).getTextArea();
            String content = textArea.getText();
            Highlighter highlighter = textArea.getHighlighter();
            highlighter.removeAllHighlights();

            int index = content.indexOf(searchTerm);
            int count = 0;
            while (index >= 0) {
                try {
                    highlighter.addHighlight(index, index + searchTerm.length(), painter);
                    index = content.indexOf(searchTerm, index + searchTerm.length());
                    count++;
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(editorPane, count + " occurrences found.", "Find Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void findAndReplace() {
        if (editorPane.getTabCount() == 0) return;

        JTextField findField = new JTextField();
        JTextField replaceField = new JTextField();
        Object[] message = {
                "Find:", findField,
                "Replace with:", replaceField
        };

        int option = JOptionPane.showConfirmDialog(editorPane, message, "Find and Replace", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String findText = findField.getText();
            String replaceText = replaceField.getText();

            if (findText.isEmpty()) {
                return;
            }

            Component selectedComponent = editorPane.getSelectedComponent();
            if (selectedComponent instanceof EditorPanel) {
                JTextArea textArea = ((EditorPanel) selectedComponent).getTextArea();
                String content = textArea.getText();
                String newContent = content.replace(findText, replaceText);
                textArea.setText(newContent);
                JOptionPane.showMessageDialog(editorPane, "Replacement complete.", "Replace Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
