package menu;

import javax.swing.*;
import java.awt.*;

public class SearchMenu extends JMenu {
    private final Font myFont = new Font("verdana", Font.BOLD, 14);

    public SearchMenu(){
        this.setText("Files");
        this.setFont(myFont);
    }


}
