import javax.swing.*;
import java.awt.*;

public class menuBar extends JMenuBar {
    //pass in this font then
    private final Font myFont = new Font("verdana", Font.BOLD, 14);


    public menuBar(SideBar sidePanel, JTabbedPane editorPane, JFrame owner){
        this.putClientProperty("JComponent.sizeVariant", "large");
        this.setBackground(new Color(245, 245, 245));
        this.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        System.err.println("MenuBAR WORKS");
        this.add(new FileMenu(sidePanel, editorPane, owner));
        this.add(new EditMenu());
        this.add(new RunMenu());
        this.add(new SearchMenu());
    }

    public menuBar() {
        this.putClientProperty("JComponent.sizeVariant", "large");
        this.setBackground(new Color(245, 245, 245));
        this.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        this.add(new FileMenu());
        this.add(new EditMenu());
        this.add(new RunMenu());
        this.add(new SearchMenu());
    }
}
