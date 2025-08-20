package codeEditor;

import IDE.mainWindow;
import sideBar.SideBar;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Tabs {

    JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private SideBar sidePanel;
    public JTabbedPane editorPane;
    public JFrame owner;
    public SaveOperations saver;
    public Tabs(SideBar sidePanel, JTabbedPane editorPane, JFrame owner ){
        this.sidePanel = sidePanel;
        this.editorPane = editorPane;
        this.owner = owner;
        saver = new SaveOperations(sidePanel, editorPane, owner);

    }

    public void newPopulatedTab(String fileName, String content, String syntaxStyle, File file) {
     EditorPanel editorPanel = new EditorPanel(file, content, syntaxStyle);

        // Create a NEW custom JPanel for EACH tab
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
                Component component = editorPane.getComponentAt(tabToClose);
                if (component instanceof EditorPanel) {
                    EditorPanel panel = (EditorPanel) component;
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
                }
                editorPane.removeTabAt(tabToClose);
            }
        });

        tabPanel.add(tabLabel);
        tabPanel.add(Box.createHorizontalStrut(5));
        tabPanel.add(closeButton);

        this.editorPane.addTab(fileName, editorPanel);
        int tabIndex = this.editorPane.getTabCount() - 1;
        this.editorPane.setTabComponentAt(tabIndex, tabPanel);
        this.editorPane.setSelectedIndex(tabIndex);
        editorPanel.getTextArea().requestFocusInWindow();
    }

    public void saveTabContent(int tabIndex) {
        saver.saveTabContent(tabIndex);
    }

    public void saveCurrentEditorAs() {
       saver.saveCurrentEditorAs();
    }

    public void saveCurrentEditor() {
       saver.saveCurrentEditorAs();
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

    public boolean closeTab(int tabIndex) {
        if (tabIndex == -1) {
            return false;
        }
        Component comp = editorPane.getComponentAt(tabIndex);
        if (comp instanceof EditorPanel) {
        EditorPanel panel = (EditorPanel) editorPane.getComponentAt(tabIndex);
        if (panel.isModified()) {
            int result = JOptionPane.showConfirmDialog(
                    owner,
                    "File '" + editorPane.getTitleAt(tabIndex).replace("*", "") + "' has unsaved changes. Save before closing?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                saveTabContent(tabIndex);
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }

    }
        editorPane.removeTabAt(tabIndex);
        return true;
    }



}
