package menu;

import sideBar.*;

import javax.swing.*;
import java.awt.*;

/*
TODO :
    Menus
    1. add the following menus to File Menu : showInFileExplorer and recent projects
        1.1 (these could all be one release with running python)
    2. group them using the separator
    3. (optional) may have to split the files up again
    ------------------------
    Running Code
    1. create a class for each language
    ------------------------
    Create Terminal
    ------------------------
    Responsive Design
    1. also include dark mode
    ------------------------
    make it so that when you open a file it shows the last directory you had open
    ------------------------
    custom icons and
    dark mode by using the text file store

 */
public class menuBar extends JMenuBar {

    private final Font myFont = new Font("verdana", Font.BOLD, 12);


    public menuBar(SideBar sidePanel, JTabbedPane editorPane, JFrame owner){
        this.putClientProperty("JComponent.sizeVariant", "large");
        this.setBackground(new Color(245, 245, 245));
        this.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        this.add(new FileMenu(sidePanel, editorPane, owner));
        this.add(new EditMenu(editorPane, owner));
        this.add(new RunMenu());
        this.add(new SearchMenu());
        this.add(new HelpMenu());
    }


}
