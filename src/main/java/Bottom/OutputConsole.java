package Bottom;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Custom output panel for displaying console output and messages
 */
public class OutputConsole extends JPanel {
    private JTextArea outputArea;
    private JScrollPane outputScroll;
    private PrintStream originalOut;
    private PrintStream originalErr;

    public OutputConsole() {
        super(new BorderLayout());
        initializePanel();
    }

    public OutputConsole(int x, int y, int width, int height) {
        super(new BorderLayout());
        setBounds(x, y, width, height);
        initializePanel();
    }

    private void initializePanel() {
        setBackground(new Color(73, 69, 69));
        setBorder(BorderFactory.createTitledBorder(
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

        outputScroll = new JScrollPane(outputArea);
        outputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.add(outputScroll, BorderLayout.CENTER);
    }


    public JTextArea getOutputArea() {
        return outputArea;
    }

    public JScrollPane getOutputScroll() {
        return outputScroll;
    }


    public void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public void appendOutputLine(String text) {
        appendOutput(text + "\n");
    }

    public void clearOutput() {
        SwingUtilities.invokeLater(() -> {
            outputArea.setText("Chax IDE Output Console v1.0\n\n");
        });
    }

    public void setOutputText(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.setText(text);
        });
    }


    public void setOutputFont(Font font) {
        outputArea.setFont(font);
    }

    public void setOutputColors(Color background, Color foreground) {
        outputArea.setBackground(background);
        outputArea.setForeground(foreground);
    }

    public void setPanelColors(Color panelBackground, Color borderColor) {
        setBackground(panelBackground);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor),
                "Output",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.WHITE));
    }


    public void redirectSystemOutput() {
        originalOut = System.out;
        originalErr = System.err;

        System.setOut(new PrintStream(new OutputPanelOutputStream()));
        System.setErr(new PrintStream(new OutputPanelOutputStream()));
    }

    public void restoreSystemOutput() {
        if (originalOut != null) {
            System.setOut(originalOut);
        }
        if (originalErr != null) {
            System.setErr(originalErr);
        }
    }


    private class OutputPanelOutputStream extends OutputStream {
        @Override
        public void write(int b) {
            appendOutput(String.valueOf((char) b));
        }

        @Override
        public void write(byte[] b, int off, int len) {
            appendOutput(new String(b, off, len));
        }
    }
}