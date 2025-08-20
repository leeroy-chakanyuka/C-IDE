package Bottom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Custom terminal panel with an editable display area.
 * Executes actual system commands and allows direct editing in the terminal.
 */
public class Terminal extends JPanel {
    private JTextArea terminalDisplay;
    private static final String PROMPT = "> ";
    private File currentDirectory;
    private PrintStream originalOut;
    private PrintStream originalErr;
    private int promptPosition = 0; // Track where the current prompt starts

    public Terminal() {
        super(new BorderLayout());
        currentDirectory = new File(System.getProperty("user.dir"));
        initializePanel();
    }

    private void initializePanel() {
        // Main display area for terminal output - NOW EDITABLE
        terminalDisplay = new JTextArea();
        terminalDisplay.setEditable(true); // Make it editable
        terminalDisplay.setBackground(new Color(30, 30, 30));
        terminalDisplay.setForeground(new Color(255, 255, 255));
        terminalDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        terminalDisplay.setCaretColor(new Color(255, 255, 255));
        terminalDisplay.setText("Chax IDE Terminal v1.0\n");
        terminalDisplay.append("Current directory: " + currentDirectory.getAbsolutePath() + "\n\n");

        JScrollPane scrollPane = new JScrollPane(terminalDisplay);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add the prompt initially
        appendOutput(PROMPT);
        promptPosition = terminalDisplay.getDocument().getLength();

        this.add(scrollPane, BorderLayout.CENTER);

        // Use InputMap and ActionMap for better key handling
        InputMap inputMap = terminalDisplay.getInputMap();
        ActionMap actionMap = terminalDisplay.getActionMap();

        // Map Enter key to execute command
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "executeCommand");
        actionMap.put("executeCommand", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCurrentLine();
            }
        });

        // Also add a key listener as backup
        terminalDisplay.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume(); // Prevent default newline behavior
                    SwingUtilities.invokeLater(() -> processCurrentLine());
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    // Prevent backspace from going before the prompt
                    if (terminalDisplay.getCaretPosition() <= promptPosition) {
                        e.consume();
                    }
                }
            }
        });

        // Add mouse listener to ensure cursor doesn't go before prompt
        terminalDisplay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (terminalDisplay.getCaretPosition() < promptPosition) {
                        terminalDisplay.setCaretPosition(terminalDisplay.getDocument().getLength());
                    }
                });
            }
        });

        // Ensure cursor stays after prompt when clicking
        terminalDisplay.addCaretListener(e -> {
            if (e.getDot() < promptPosition) {
                SwingUtilities.invokeLater(() ->
                        terminalDisplay.setCaretPosition(Math.max(promptPosition, terminalDisplay.getDocument().getLength()))
                );
            }
        });
    }

    private void processCurrentLine() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("processCurrentLine called!"); // Debug

                String fullText = terminalDisplay.getText();
                System.out.println("Full text: " + fullText); // Debug

                // Find the last prompt position
                int lastPromptIndex = fullText.lastIndexOf(PROMPT);
                System.out.println("Last prompt index: " + lastPromptIndex); // Debug

                if (lastPromptIndex == -1) {
                    // No prompt found, add one and return
                    System.out.println("No prompt found, adding new prompt");
                    addNewPrompt();
                    return;
                }

                // Extract command after the last prompt
                String command = fullText.substring(lastPromptIndex + PROMPT.length()).trim();
                System.out.println("Extracted command: '" + command + "'"); // Debug

                // Add newline after the command
                terminalDisplay.append("\n");

                if (command.isEmpty()) {
                    System.out.println("Empty command, adding new prompt");
                    addNewPrompt();
                    return;
                }

                // Show what we're executing
                appendOutput("Executing: " + command + "\n");

                // Handle built-in commands
                if (handleBuiltinCommands(command)) {
                    addNewPrompt();
                    return;
                }

                // Execute system command in a separate thread to avoid blocking UI
                new Thread(() -> executeSystemCommand(command)).start();

            } catch (Exception ex) {
                System.out.println("Exception in processCurrentLine: " + ex.getMessage());
                appendOutput("Error processing command: " + ex.getMessage() + "\n");
                ex.printStackTrace(); // For debugging
                addNewPrompt();
            }
        });
    }

    private void addNewPrompt() {
        SwingUtilities.invokeLater(() -> {
            terminalDisplay.append(PROMPT);
            promptPosition = terminalDisplay.getDocument().getLength();
            terminalDisplay.setCaretPosition(promptPosition);
        });
    }

    private boolean handleBuiltinCommands(String command) {
        String[] parts = command.split("\\s+");
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "cd":
                if (parts.length > 1) {
                    changeDirectory(parts[1]);
                } else {
                    changeDirectory(System.getProperty("user.home"));
                }
                return true;
            case "pwd":
                appendOutput(currentDirectory.getAbsolutePath() + "\n");
                return true;
            case "clear":
                SwingUtilities.invokeLater(() -> {
                    terminalDisplay.setText("Chax IDE Terminal v1.0\n");
                    terminalDisplay.append("Current directory: " + currentDirectory.getAbsolutePath() + "\n\n");
                    terminalDisplay.append(PROMPT);
                    promptPosition = terminalDisplay.getDocument().getLength();
                });
                return true;
            case "exit":
                appendOutput("Terminal session ended.\n");
                terminalDisplay.setEditable(false);
                return true;
            case "help":
                appendOutput("Built-in commands:\n");
                appendOutput("  cd [directory] - Change directory\n");
                appendOutput("  pwd - Print working directory\n");
                appendOutput("  clear - Clear terminal\n");
                appendOutput("  exit - Exit terminal\n");
                appendOutput("  help - Show this help\n");
                appendOutput("You can also run any system command.\n");
                return true;
            default:
                return false;
        }
    }

    private void changeDirectory(String path) {
        File newDir;
        if (path.startsWith("/") || path.matches("[A-Za-z]:.*")) {
            // Absolute path
            newDir = new File(path);
        } else {
            // Relative path
            newDir = new File(currentDirectory, path);
        }

        if (newDir.exists() && newDir.isDirectory()) {
            currentDirectory = newDir;
            appendOutput("Changed directory to: " + currentDirectory.getAbsolutePath() + "\n");
        } else {
            appendOutput("Directory not found: " + path + "\n");
        }
    }

    private void executeSystemCommand(String command) {
        try {
            appendOutput("Starting execution of: " + command + "\n");

            ProcessBuilder pb;

            // Handle different operating systems
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd", "/c", command);
                appendOutput("Using Windows command: cmd /c " + command + "\n");
            } else {
                pb = new ProcessBuilder("sh", "-c", command);
                appendOutput("Using Unix command: sh -c " + command + "\n");
            }

            pb.directory(currentDirectory);
            pb.redirectErrorStream(true); // Merge stdout and stderr

            appendOutput("Working directory: " + currentDirectory.getAbsolutePath() + "\n");

            Process process = pb.start();

            // Read output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            appendOutput("--- Command Output ---\n");

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                // Show output in real-time
                appendOutput(line + "\n");
            }

            // Wait for process to complete (with timeout)
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                appendOutput("Command timed out (30 seconds)\n");
            } else {
                int exitCode = process.exitValue();
                appendOutput("--- End Command Output ---\n");
                appendOutput("Process exited with code: " + exitCode + "\n");
            }

        } catch (IOException e) {
            appendOutput("IOException executing command: " + e.getMessage() + "\n");
            e.printStackTrace();
        } catch (InterruptedException e) {
            appendOutput("Command was interrupted: " + e.getMessage() + "\n");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            appendOutput("Unexpected error: " + e.getMessage() + "\n");
            e.printStackTrace();
        } finally {
            // Always show prompt after command execution
            addNewPrompt();
        }
    }

    /**
     * Append text to the terminal display (maintains prompt position)
     */
    public void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            // Insert text before the current line if we're at a prompt
            int caretPos = terminalDisplay.getCaretPosition();
            int currentLength = terminalDisplay.getDocument().getLength();

            // If we're at the end and there's a current prompt, insert before it
            String currentText = terminalDisplay.getText();
            if (currentText.endsWith(PROMPT) && caretPos == currentLength) {
                try {
                    terminalDisplay.getDocument().insertString(currentLength - PROMPT.length(), text, null);
                    promptPosition = terminalDisplay.getDocument().getLength() - PROMPT.length();
                } catch (Exception e) {
                    terminalDisplay.append(text);
                }
            } else {
                terminalDisplay.append(text);
                promptPosition = terminalDisplay.getDocument().getLength();
            }

            terminalDisplay.setCaretPosition(terminalDisplay.getDocument().getLength());
        });
    }

    /**
     * Add text to terminal (public method for external use)
     */
    public void addText(String text) {
        appendOutput(text);
    }

    /**
     * Add a line of text to terminal
     */
    public void addLine(String text) {
        appendOutput(text + "\n");
    }

    /**
     * Clear the terminal display
     */
    public void clearTerminal() {
        SwingUtilities.invokeLater(() -> {
            terminalDisplay.setText("Chax IDE Terminal v1.0\n");
            terminalDisplay.append("Current directory: " + currentDirectory.getAbsolutePath() + "\n\n");
            terminalDisplay.append(PROMPT);
            promptPosition = terminalDisplay.getDocument().getLength();
            terminalDisplay.setCaretPosition(promptPosition);
        });
    }

    /**
     * Execute a command programmatically
     */
    public void executeCommand(String command) {
        SwingUtilities.invokeLater(() -> {
            // Move to end and add the command
            terminalDisplay.setCaretPosition(terminalDisplay.getDocument().getLength());
            terminalDisplay.replaceRange(command, promptPosition, terminalDisplay.getDocument().getLength());
            // Process the command
            processCurrentLine();
        });
    }

    /**
     * Get the terminal display component for advanced operations
     */
    public JTextArea getTerminalDisplay() {
        return terminalDisplay;
    }

    /**
     * Redirects System.out and System.err to this terminal's display area.
     * Call this method only if you want terminal to capture system output.
     */
    public void redirectSystemOutput() {
        originalOut = System.out;
        originalErr = System.err;

        System.setOut(new PrintStream(new TerminalOutputStream(originalOut)));
        System.setErr(new PrintStream(new TerminalOutputStream(originalErr)));
    }

    /**
     * Restore original system output streams
     */
    public void restoreSystemOutput() {
        if (originalOut != null) {
            System.setOut(originalOut);
        }
        if (originalErr != null) {
            System.setErr(originalErr);
        }
    }

    /**
     * Custom OutputStream to write to the terminal display.
     */
    private class TerminalOutputStream extends OutputStream {
        private final PrintStream originalStream;

        public TerminalOutputStream(PrintStream originalStream) {
            this.originalStream = originalStream;
        }

        @Override
        public void write(int b) {
            appendOutput(String.valueOf((char) b));
            originalStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            appendOutput(new String(b, off, len));
            originalStream.write(b, off, len);
        }
    }
}