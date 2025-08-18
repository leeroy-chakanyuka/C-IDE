package menu;

import sideBar.SideBar;

import javax.swing.*;
import java.awt.*;


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
        this.saveFile();
        this.saveFileAs();
        this.newFolder();
    }

    public void newFile(){
        JMenuItem newFile = new JMenuItem("New File");
        newFile.setFont(myFont);
        this.add(newFile);
        newFile.addActionListener((e) -> {doThings.createNewFile();});
    }

    public void openFile(){
        JMenuItem openFile = new JMenuItem("Open");
        openFile.setFont(myFont);
        this.add(openFile);
        openFile.addActionListener((e -> doThings.openFile()));
    }

    public void saveFile(){
        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.setFont(myFont);
        this.add(saveFile);
        saveFile.addActionListener((e) -> {doThings.saveFile();});
    }

    public void saveFileAs(){
        JMenuItem saveAs = new JMenuItem("Save File as..");
        saveAs.setFont(myFont);
        this.add(saveAs);
        saveAs.addActionListener((e) -> {doThings.saveFileAs();});
    }

    public void newFolder(){
        JMenuItem newFolder = new JMenuItem("New Folder");
        newFolder.setFont(myFont);
        this.add(newFolder);
        newFolder.addActionListener((e) -> {doThings.createFolder();});
    }

    public void openFolder(){

    }
}
