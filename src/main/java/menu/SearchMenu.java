package menu;

import javax.swing.*;
import java.awt.*;

public class SearchMenu extends JMenu {
    private final Font myFont = new Font("verdana", Font.PLAIN, 13);
    private SearchActionHandler actionHandler;

    public SearchMenu(JTabbedPane editorPane) {
        super("Search");
        this.actionHandler = new SearchActionHandler(editorPane);
        setFont(myFont);

        addFindItem();
        addFindAndReplaceItem();
    }

    private void addFindItem() {
        JMenuItem find = new JMenuItem("Find");
        find.setFont(myFont);
        // Add accelerator if desired, e.g., Ctrl+F
        // find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        find.addActionListener(e -> actionHandler.find());
        add(find);
    }

    private void addFindAndReplaceItem() {
        JMenuItem findAndReplace = new JMenuItem("Find and Replace");
        findAndReplace.setFont(myFont);
        // Add accelerator if desired, e.g., Ctrl+H
        // findAndReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
        findAndReplace.addActionListener(e -> actionHandler.findAndReplace());
        add(findAndReplace);
    }
}
