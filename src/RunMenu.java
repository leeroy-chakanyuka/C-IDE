import javax.swing.*;
import java.awt.*;

public class RunMenu extends JMenu{
    private final Font myFont = new Font("verdana", Font.BOLD, 14);

    public RunMenu(){
        this.setText("Run");
        this.setFont(myFont);
        this.createRunItem();
        this.CreateViewOnlineItem();
    }

    public void createRunItem(){
        JMenuItem runCurrentFile = new JMenuItem("Run Current File");
        runCurrentFile.setFont(myFont);
        this.add(runCurrentFile);
    }

    //change to only work if what we have rn is HTML
    public void CreateViewOnlineItem(){
        JMenuItem viewOnline = new JMenuItem("View Current File Online");
        viewOnline.setFont(myFont);
        this.add(viewOnline);
    }
}
