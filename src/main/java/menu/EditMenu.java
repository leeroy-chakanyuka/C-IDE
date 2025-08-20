package menu;

import menu.EditActionHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class EditMenu extends JMenu {

    private JTabbedPane editorPane;
    private JFrame owner;
    private EditActionHandler doThings;

    public EditMenu(JTabbedPane editorPane, JFrame owner) {
        this.editorPane = editorPane;
        this.owner = owner;
        this.doThings = new EditActionHandler(editorPane, owner);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setText("Edit");

        this.undo();
        this.redo();
        addSeparator();
        this.cut();
        this.copy();
        this.paste();
    }

    public void undo() {
        JMenuItem undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undo.addActionListener(e -> doThings.undo());
        add(undo);
    }

    public void redo() {
        JMenuItem redo = new JMenuItem("Redo");
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redo.addActionListener(e -> doThings.redo());
        add(redo);
    }

    public void cut() {
        JMenuItem cut = new JMenuItem("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        cut.addActionListener(e -> doThings.cut());
        add(cut);
    }

    public void copy() {
        JMenuItem copy = new JMenuItem("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copy.addActionListener(e -> doThings.copy());
        add(copy);
    }

    public void paste() {
        JMenuItem paste = new JMenuItem("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        paste.addActionListener(e -> doThings.paste());
        add(paste);
    }
}