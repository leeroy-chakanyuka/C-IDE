package menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class HelpMenu extends JMenu {

    private HelpActionHandler doThings;

    public HelpMenu() {
        this.doThings = new HelpActionHandler();
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setText("Help");

        JMenuItem myLinkedIn = new JMenuItem("My LinkedIn");
        myLinkedIn.addActionListener(e -> doThings.openLinkedIn());
        add(myLinkedIn);


        JMenuItem ourGitHub = new JMenuItem("Our GitHub");
        ourGitHub.addActionListener(e -> doThings.openGitHub());
        add(ourGitHub);


        JMenuItem docs = new JMenuItem("Documentation");
        docs.addActionListener(e -> doThings.openDocs());
        add(docs);
    }
}