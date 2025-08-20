package menu;

import IDE.mainWindow;
import codeEditor.EditorPanel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;

public class EditActionHandler {

    private JTabbedPane editorPane;
    private JFrame owner;

    public EditActionHandler(JTabbedPane editorPane, JFrame owner) {
        this.editorPane = editorPane;
        this.owner = owner;
    }

    private EditorPanel getActiveEditorPanel() {
        Component comp = editorPane.getSelectedComponent();
        if (comp instanceof EditorPanel) {
            return (EditorPanel) comp;
        }
        return null;
    }

    private RSyntaxTextArea getActiveEditor() {
        EditorPanel panel = getActiveEditorPanel();
        if (panel != null) {
            return panel.getTextArea();
        }
        return null;
    }

    public void cut() {
        RSyntaxTextArea activeEditor = getActiveEditor();
        if (activeEditor != null) {
            activeEditor.cut();
        }
    }

    public void copy() {
        RSyntaxTextArea activeEditor = getActiveEditor();
        if (activeEditor != null) {
            activeEditor.copy();
        }
    }

    public void paste() {
        RSyntaxTextArea activeEditor = getActiveEditor();
        if (activeEditor != null) {
            activeEditor.paste();
        }
    }

    public void undo() {
        EditorPanel panel = getActiveEditorPanel();
        if (panel != null) {
            try {
                UndoManager undoManager = panel.getUndoManager();
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            } catch (CannotUndoException ignored) {}
        }
    }

    public void redo() {
        EditorPanel panel = getActiveEditorPanel();
        if (panel != null) {
            try {
                UndoManager undoManager = panel.getUndoManager();
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            } catch (CannotRedoException ignored) {}
        }
    }
}