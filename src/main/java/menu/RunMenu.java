package menu;

import IDE.mainWindow;
import execution.codeRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class RunMenu extends JMenu{

    private JTabbedPane editorPane;
    private JTextArea outputArea;
    private JFrame owner;
    private final Font myFont = new Font("verdana", Font.PLAIN, 13);
    RunActionHandler runCode ;

    public RunMenu(JTabbedPane editorPane, JTextArea outputArea, mainWindow owner) throws IOException {
        this.editorPane = editorPane;
        this.outputArea = outputArea;
        this.owner = owner;
        this.runCode = new RunActionHandler(editorPane,owner, outputArea);
        this.setText("Run");
        this.setFont(myFont);
        this.createRunItem();
    }


    public void createRunItem(){
        JMenuItem runCurrentFile = new JMenuItem("Run Current File");
        runCurrentFile.setFont(myFont);
        runCurrentFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        this.add(runCurrentFile);
        runCurrentFile.addActionListener(e -> runCode.runCurrentFile());
    }



}