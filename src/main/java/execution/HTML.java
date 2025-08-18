package execution;

import IDE.mainWindow;
import utils.handleFiles;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HTML {

    private final mainWindow parentWindow;
    private final JTabbedPane editorPane;
    private final JTextArea outputArea;
    private codeRunner run;
    private handleFiles fh;

    public HTML(mainWindow parentWindow, JTabbedPane editorPane, JTextArea outputArea) {
        this.parentWindow = parentWindow;
        this.editorPane = editorPane;
        this.outputArea = outputArea;
        this.fh = new handleFiles();
    }

    void handleHtmlFile(File file) throws IOException {
        run = new codeRunner(parentWindow, editorPane, outputArea);
        String[] options = {"Preview HTML", "Open in Browser", "Cancel"};
        int choice = JOptionPane.showOptionDialog(parentWindow,
                "How would you like to run this HTML file?",
                "Run HTML File",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Preview HTML
                this.showHtmlPreview(file);
                break;
            case 1: // Open in Browser
                this.openFileInDefaultBrowser(file);
                break;
            default:
                run.appendOutput("[INFO] HTML run cancelled by user.\n");
                break;
        }
    }
    /**
     * Opens an HTML file in a custom preview window.
     * we should think about doing this in FX
     */
    public void showHtmlPreview(File file) {
        try {
            String content = fh.readFileContent(file);

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
                    String newContent = fh.readFileContent(file);
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
            JOptionPane.showMessageDialog(parentWindow,
                    "Could not read HTML file for preview: " + file.getName() + "\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            outputArea.append("[HTML-PREVIEW-ERROR] Could not read HTML file: " + e.getMessage() + "\n");
        }
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    public void openFileInDefaultBrowser(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(file);
                    outputArea.append("[SYSTEM] Opened '" + file.getName() + "' with system default application.\n");
                } else {
                    outputArea.append("[ERROR] Desktop OPEN action not supported on  system.\n");
                }
            } else {
                outputArea.append("[ERROR] Desktop operations not supported on this system.\n");
            }
        } catch (IOException e) {
            outputArea.append("[ERROR] Could not open file '" + file.getName() + "': " + e.getMessage() + "\n");
        }
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    public void runHtmlInBrowser(File file) {
        try {
            String fileName = file.getName().toLowerCase();
            if (!fileName.endsWith(".html") && !fileName.endsWith(".htm")) {
                outputArea.append("[ERROR] The selected file is not an HTML file.\n");
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(file.toURI());
                    outputArea.append("[SYSTEM] Opened '" + file.getName() + "' in the default web browser.\n");
                } else {
                    outputArea.append("[ERROR] Desktop BROWSE action not supported on this system.\n");
                }
            } else {
                outputArea.append("[ERROR] Desktop operations not supported on this system.\n");
            }
        } catch (IOException e) {
            outputArea.append("[ERROR] Could not open HTML file '" + file.getName() + "': " + e.getMessage() + "\n");
        }
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }


}
