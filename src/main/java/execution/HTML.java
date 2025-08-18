//package execution;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.File;
//import java.io.IOException;
//
//public class HTML {
//    public void showHtmlPreview(File file) {
//        try {
//            String content = readFileContent(file);
//
//            JFrame htmlFrame = new JFrame("HTML Preview - " + file.getName());
//            htmlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            htmlFrame.setSize(900, 700);
//
//            JTabbedPane tabbedPane = new JTabbedPane();
//
//
//            JEditorPane htmlPane = new JEditorPane();
//            htmlPane.setContentType("text/html");
//            htmlPane.setEditable(false);
//
//            try {
//                htmlPane.setPage(file.toURI().toURL());
//            } catch (IOException e) {
//                htmlPane.setText(content);
//            }
//
//
//            htmlPane.addHyperlinkListener(e -> {
//                if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
//                    try {
//                        if (Desktop.isDesktopSupported()) {
//                            Desktop.getDesktop().browse(e.getURL().toURI());
//                        }
//                    } catch (Exception ex) {
//                        System.err.println("Could not open link: " + ex.getMessage());
//                        outputArea.append("[HTML-PREVIEW-ERROR] Could not open link: " + ex.getMessage() + "\n");
//                    }
//                }
//            });
//
//            JScrollPane htmlScrollPane = new JScrollPane(htmlPane);
//            tabbedPane.addTab("Preview", htmlScrollPane);
//
//            JTextArea sourceArea = new JTextArea(content);
//            sourceArea.setEditable(false);
//            sourceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
//            sourceArea.setTabSize(2);
//            sourceArea.setBackground(new Color(40, 44, 52));
//            sourceArea.setForeground(new Color(171, 178, 191));
//            sourceArea.setCaretColor(Color.WHITE);
//
//            JScrollPane sourceScrollPane = new JScrollPane(sourceArea);
//            tabbedPane.addTab("Source", sourceScrollPane);
//
//            //  buttons
//            JPanel buttonPanel = new JPanel(new FlowLayout());
//            JButton refreshButton = new JButton("Refresh");
//            refreshButton.addActionListener(e -> {
//                try {
//                    String newContent = readFileContent(file);
//                    htmlPane.setText(newContent);
//                    sourceArea.setText(newContent);
//                    outputArea.append("[HTML-PREVIEW] Refreshed content for " + file.getName() + "\n");
//                } catch (IOException ex) {
//                    JOptionPane.showMessageDialog(htmlFrame,
//                            "Error refreshing file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                    outputArea.append("[HTML-PREVIEW-ERROR] Error refreshing file: " + ex.getMessage() + "\n");
//                }
//            });
//
//            JButton openInBrowserButton = new JButton("Open in Browser");
//            openInBrowserButton.addActionListener(e -> {
//                openFileInDefaultBrowser(file);
//            });
//
//            buttonPanel.add(refreshButton);
//            buttonPanel.add(openInBrowserButton);
//
//            htmlFrame.setLayout(new BorderLayout());
//            htmlFrame.add(tabbedPane, BorderLayout.CENTER);
//            htmlFrame.add(buttonPanel, BorderLayout.SOUTH);
//
//            htmlFrame.setLocationRelativeTo(null);
//            htmlFrame.setVisible(true);
//            outputArea.append("[HTML-PREVIEW] HTML preview opened for " + file.getName() + "\n");
//
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(this,
//                    "Could not read HTML file for preview: " + file.getName() + "\n" + e.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//            outputArea.append("[HTML-PREVIEW-ERROR] Could not read HTML file: " + e.getMessage() + "\n");
//        }
//        outputArea.setCaretPosition(outputArea.getDocument().getLength());
//    }
//
//    public void openFileInDefaultBrowser(File file) {
//        try {
//            if (Desktop.isDesktopSupported()) {
//                Desktop desktop = Desktop.getDesktop();
//                if (desktop.isSupported(Desktop.Action.OPEN)) {
//                    desktop.open(file);
//                    outputArea.append("[SYSTEM] Opened '" + file.getName() + "' with system default application.\n");
//                } else {
//                    outputArea.append("[ERROR] Desktop OPEN action not supported on  system.\n");
//                }
//            } else {
//                outputArea.append("[ERROR] Desktop operations not supported on this system.\n");
//            }
//        } catch (IOException e) {
//            outputArea.append("[ERROR] Could not open file '" + file.getName() + "': " + e.getMessage() + "\n");
//        }
//        outputArea.setCaretPosition(outputArea.getDocument().getLength());
//    }
//}
