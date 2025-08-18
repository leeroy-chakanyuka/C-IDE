package menu;

import javax.swing.*;
import java.awt.*;

public class EditMenu extends JMenu {
    private final Font myFont = new Font("verdana", Font.BOLD, 14);


    public EditMenu(){
        this.setText("Edit");
        this.setFont(myFont);
    }



}
