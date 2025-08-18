import javax.swing.*;
import java.awt.*;

public class FileMenu extends JMenu {
    private final Font myFont = new Font("verdana", Font.BOLD, 14);
    private FileActionHandler doThings;
    private JFrame frame;

    public FileMenu(){}

    public FileMenu(SideBar sidePanel, JTabbedPane editorPane, JFrame owner){
        this.setText("Files");
        this.setFont(myFont);
        this.CreateNewFileItem();
        this.OpenFileItem();
        this.saveFileItem();
        this.frame = owner;
        this.doThings = new FileActionHandler(sidePanel,  editorPane, owner);
    }

    public void CreateNewFileItem(){
        JMenuItem newFile = new JMenuItem("New File");
        newFile.setFont(myFont);
        newFile.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.add(newFile);
        newFile.addActionListener((e) -> {doThings.createNewFile();});
    }

    public void OpenFileItem(){
        JMenuItem openFile = new JMenuItem("Open");
        openFile.setFont(myFont);
        this.add(openFile);
    }

    public void saveFileItem(){
        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.setFont(myFont);
        this.add(saveFile);
    }

}
