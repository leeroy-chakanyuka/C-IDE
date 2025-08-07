import javax.swing.*;
import java.awt.*;

public class OutputConsole {

    private final JTextArea outputArea;
    private final JPanel outputPanel;

    public OutputConsole() {
        this.outputArea = new JTextArea();
        this.outputPanel = createOutputPanelContainer();
        setupOutputArea();
        JScrollPane outputScroll = createOutputScrollPane();
        outputPanel.add(outputScroll, BorderLayout.CENTER);
    }

    public JPanel getOutputPanelContainer() {
        return this.outputPanel;
    }

    public JPanel createOutputPanelContainer() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(73, 69, 69));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Output",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.WHITE));
        return panel;
    }

    public void setupOutputArea() {
        outputArea.setBackground(new Color(30, 30, 30));
        outputArea.setForeground(new Color(255, 255, 255));
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setFocusable(false);
        outputArea.setText("Chax IDE Output Console v1.0\n\n");
    }

    public JScrollPane createOutputScrollPane() {
        return new JScrollPane(outputArea);
    }

    public void append(String text) {
        outputArea.append(text);
        scrollOutputToEnd();
    }

    public void clear() {
        outputArea.setText("Chax IDE Output Console v1.0\n\n");
    }

    public void scrollOutputToEnd() {
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
}