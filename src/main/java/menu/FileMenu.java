package menu;

import sideBar.SideBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class FileMenu extends JMenu {
    private final Font myFont = new Font("verdana", Font.PLAIN, 13);
    private FileActionHandler doThings;
    private JFrame frame;

    public FileMenu(){}

    public FileMenu(SideBar sidePanel, JTabbedPane editorPane, JFrame owner){
        this.frame = owner;
        this.doThings = new FileActionHandler(sidePanel,  editorPane, owner);
        this.setText("Files");
        this.setFont(myFont);

        this.newFile();
        this.openFile();
        this.newFolder();
        this.openFolder();
        this.addSeparator();

        this.saveFile();
        this.saveFileAs();
        this.saveAll();
        this.addSeparator();

        this.closeTab();
        this.closeAll();
        this.addSeparator();

        this.exitApp();
    }
    public void exitApp() {
        JMenuItem exit = new JMenuItem("Exit");
        exit.setFont(myFont);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        this.add(exit);
        exit.addActionListener(e -> doThings.exitApplication());
    }

    public void newFile(){
        JMenuItem newFile = new JMenuItem("New File");
        newFile.setFont(myFont);
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        this.add(newFile);
        newFile.addActionListener((e) -> {doThings.createNewFile();});
    }

    public void openFile(){
        JMenuItem openFile = new JMenuItem("Open");
        openFile.setFont(myFont);
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        this.add(openFile);
        openFile.addActionListener((e -> doThings.openFile()));
    }

    public void saveFile(){
        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.setFont(myFont);
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        this.add(saveFile);
        saveFile.addActionListener((e) -> {doThings.saveFile();});
    }

    public void saveFileAs(){
        JMenuItem saveAs = new JMenuItem("Save File as..");
        saveAs.setFont(myFont);
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        this.add(saveAs);
        saveAs.addActionListener((e) -> {doThings.saveFileAs();});
    }

    public void newFolder(){
        JMenuItem newFolder = new JMenuItem("New Folder");
        newFolder.setFont(myFont);
        newFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        this.add(newFolder);
        newFolder.addActionListener((e) -> {doThings.createFolder();});
    }

    public void openFolder(){
        JMenuItem openFolder = new JMenuItem("Open Folder");
        openFolder.setFont(myFont);
        openFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        this.add(openFolder);
        openFolder.addActionListener((e) -> {doThings.openFolder();});
    }

    public void saveAll(){
        JMenuItem saveAll = new JMenuItem("Save All");
        saveAll.setFont(myFont);
        saveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK));
        this.add(saveAll);
        saveAll.addActionListener((e) -> {doThings.saveAllFiles();});
    }

    public void closeTab(){
        JMenuItem ct = new JMenuItem("Close Current Editor");
        ct.setFont(myFont);
        ct.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        this.add(ct);
        ct.addActionListener((e) -> {doThings.closeCurrentEditor();});
    }

    public void closeAll(){
        JMenuItem closeAll = new JMenuItem("Close All");
        closeAll.setFont(myFont);
        closeAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        this.add(closeAll);
        closeAll.addActionListener((e) -> {doThings.closeAllEditors();});
    }

}