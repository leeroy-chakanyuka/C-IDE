package Bottom;

import javax.swing.*;
import java.awt.*;

/**
 * A container panel that uses a JTabbedPane to hold both the
 * OutputConsole and a new Terminal panel.
 */
public class Container extends JPanel {
    private JTabbedPane tabbedPane;
    private OutputConsole outputConsole;
    private Terminal terminal;

    public Container(OutputConsole out) {
        super(new BorderLayout());
        initializePanel();
    }

    private void initializePanel() {
        setBackground(new Color(40, 40, 40));
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(40, 40, 40));
        tabbedPane.setForeground(new Color(255, 255, 255));
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Create new instances
        outputConsole = new OutputConsole();
        terminal = new Terminal();

        tabbedPane.addTab("Output", outputConsole);
        tabbedPane.addTab("Terminal", terminal);

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private void initializePanelWithExisting() {
        setBackground(new Color(40, 40, 40));
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(40, 40, 40));
        tabbedPane.setForeground(new Color(255, 255, 255));
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Use existing console, create new terminal
        terminal = new Terminal();

        tabbedPane.addTab("Output", outputConsole);
        tabbedPane.addTab("Terminal", terminal);

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Provides access to the JTextArea from the OutputConsole,
     * which is needed by other components like the menu bar.
     * @return The JTextArea from the OutputConsole.
     */
    public JTextArea getOutputArea() {
        return outputConsole.getOutputArea();
    }

    /**
     * Get access to the OutputConsole instance
     */
    public OutputConsole getOutputConsole() {
        return outputConsole;
    }

    /**
     * Get access to the Terminal instance
     */
    public Terminal getTerminal() {
        return terminal;
    }
}