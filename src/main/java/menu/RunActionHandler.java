package menu;

import IDE.mainWindow;
import execution.HTML;
import execution.codeRunner;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class RunActionHandler {
    private codeRunner runner;
    private JTabbedPane editorPane;
    private JTextArea outputArea;
    private mainWindow owner;

    public RunActionHandler( JTabbedPane editorPane, mainWindow owner, JTextArea outputArea) throws IOException {
        this.editorPane = editorPane;
        this.owner = owner;
        this.outputArea = outputArea;
        this.runner = new codeRunner(owner, editorPane, outputArea);
    }

    public void runCurrentFile() {
        runner.runCurrentFile();
    }


}