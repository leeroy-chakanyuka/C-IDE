package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class makeConfig {

    private JPanel welcomePanel;
    private JTextField nameField;
    private JTextField pathField;
    private final Font font = new Font("verdana", Font.PLAIN, 16);
    private String IDE_HOME_PATH;
    private JFrame myWindow;
    private makeConfigHandler configHandler;
    private configReader reader;

    public makeConfig(JPanel welcomePanel, JTextField nameField, JTextField pathField,
                      String IDE_HOME_PATH, JFrame myWindow, makeConfigHandler configHandler, configReader reader) {
        this.welcomePanel = welcomePanel;
        this.nameField = nameField;
        this.pathField = pathField;
        this.IDE_HOME_PATH = IDE_HOME_PATH;
        this.myWindow = myWindow;
        this.configHandler = configHandler;
        this.reader = reader;
    }

    /**
     * Adds the name input field to the welcome screen.
     */
    public void addNameField(GridBagConstraints gbc) {
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        welcomePanel.add(nameLabel, gbc);

        nameField.setFont(font);

        String savedName = reader.readName();
        if (savedName != null && !savedName.isBlank()) {
            nameField.setText(savedName);
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        welcomePanel.add(nameField, gbc);
    }

    /**
     * Adds the text field for the IDE home directory path and pre-fills it
     * if the user already has something in their .path file
     */
    public void addPathField(GridBagConstraints gbc) {
        JLabel pathLabel = new JLabel("IDE Home Path:");
        pathLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        welcomePanel.add(pathLabel, gbc);

        pathField.setFont(font);
        if (IDE_HOME_PATH != null && !IDE_HOME_PATH.isBlank()) {
            pathField.setText(IDE_HOME_PATH);
        }
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        welcomePanel.add(pathField, gbc);
    }


    public void addFolderIcon(GridBagConstraints gbc) throws IOException {
        JLabel iconLabel = createFolderIconLabel();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        welcomePanel.add(iconLabel, gbc);
    }

    private JLabel createFolderIconLabel() {
        try {
            URL folderIconUrl = getClass().getResource("/icons/folder.png");

            if (folderIconUrl == null) {
                System.err.println("Error: Folder icon resource '/icons/folder.png' not found on classpath.");
                return configHandler.createFallbackIconLabel("Select Folder");
            }

            Image folderImg = ImageIO.read(folderIconUrl);
            if (folderImg == null) {
                System.err.println("Error: ImageIO.read returned null for folder icon URL: " + folderIconUrl);
                return configHandler.createFallbackIconLabel("Failed Load");
            }

            return createClickableIconLabel();

        } catch (IOException e) {
            configHandler.handleIconLoadError(e, "folder", myWindow);
            return configHandler.createFallbackIconLabel("Error Loading Icon");
        }
    }

    /**
     * Scales the image appropriately and adds mouse interaction functionality
     * for folder selection
     */
    private JLabel createClickableIconLabel() throws IOException {
        BufferedImage folderImg = ImageIO.read(getClass().getClassLoader().getResource("icons/folder.png"));
        ImageIcon folderIcon = new ImageIcon(folderImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        JLabel iconLabel = new JLabel(folderIcon);
        iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iconLabel.addMouseListener(configHandler.createFolderClickListener());
        return iconLabel;
    }

    public void setMyWindow(JFrame myWindow) {
        this.myWindow = myWindow;
    }

    void addWelcomeMessage(GridBagConstraints gbc) {
        JLabel welcomeMessage = new JLabel("Welcome to Chax, IDE. Let's change the World");
        welcomeMessage.setFont(new Font("verdana", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        welcomePanel.add(welcomeMessage, gbc);
    }
}