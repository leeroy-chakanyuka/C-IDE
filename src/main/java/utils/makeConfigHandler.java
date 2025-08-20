package utils;

import IDE.mainWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

public class makeConfigHandler {
    private JTextField nameField;
    private JTextField pathField;
    private int isLoggedIn;
    private String IDE_HOME_PATH;
    private JFrame myWindow;
    private final Font font = new Font("verdana", Font.PLAIN, 16);
    private configReader reader;


    public makeConfigHandler(JTextField nameField, JTextField pathField,
                             String IDE_HOME_PATH, JFrame myWindow, configReader reader) {
        this.nameField = nameField;
        this.pathField = pathField;
        this.IDE_HOME_PATH = IDE_HOME_PATH;
        this.myWindow = myWindow;
        this.reader = reader;
    }

    public MouseAdapter createFolderClickListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleFolderSelection();
            }
        };
    }

    public void setMyWindow(JFrame myWindow) {
        this.myWindow = myWindow;
    }

    public void setWindowIcon(JFrame frame) {
        if (frame == null) {
            System.err.println("Cannot set window icon: JFrame is null.");
            return;
        }
        try {
            URL logoImgUrl = getClass().getResource("/icons/logo.png");
            if (logoImgUrl == null) {
                System.err.println("Error: Logo icon resource '/icons/logo.png' not found on classpath.");

                JOptionPane.showMessageDialog(frame,
                        "Logo icon resource not found: /icons/logo.png",
                        "Icon Loading Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Image logoImg = ImageIO.read(logoImgUrl);
            if (logoImg != null) {
                frame.setIconImage(logoImg);
            } else {
                System.err.println("Error: ImageIO.read returned null for logo icon URL: " + logoImgUrl);
                JOptionPane.showMessageDialog(frame,
                        "Failed to read logo image from URL: " + logoImgUrl,
                        "Icon Loading Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException e) {

            handleIconLoadError(e, "logo", frame);
        } catch (Exception e) {
            System.err.println("Unexpected error loading window icon: " + e.getMessage());
            JOptionPane.showMessageDialog(frame,
                    "An unexpected error occurred while loading the window icon.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void handleFolderSelection() {
        JFrame parentFrame = (myWindow != null) ? myWindow : (JFrame) SwingUtilities.getWindowAncestor(pathField);

        handleDirs setupHandler = new handleDirs(parentFrame, pathField, nameField);
        setupHandler.setSetupCompletionListener(new handleDirs.SetupCompletionListener() {
            public void onSetupComplete(String ideHomePath, String userName) throws IOException {
                IDE_HOME_PATH = ideHomePath;
                isLoggedIn = 1;
                System.out.println("Setup Complete: IDE Home Path: " + ideHomePath + ", User: " + userName);
                if (parentFrame != null) {
                    parentFrame.dispose(); // Dispose the welcome screen
                }
                new mainWindow(); // Launch main IDE
            }

            public void onSetupCancelled() {
                System.out.println("IDE setup cancelled by user.");
            }

            public void onSetupError(String message, Throwable cause) {
                System.err.println("IDE setup error: " + message);
                if (cause != null) cause.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Setup Error: " + message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        setupHandler.handleIdeSetup();
    }

    public JLabel createFallbackIconLabel(String text) {
        JLabel iconLabel = new JLabel(text);
        iconLabel.setFont(font);
        return iconLabel;
    }


    public void handleIconLoadError(IOException e, String iconType, JFrame parentFrame) {
        System.err.println("Caught IOException while loading " + iconType + " icon: " + e.getMessage());
        JOptionPane.showMessageDialog(parentFrame,
                "Could not load " + iconType + " icon. Please check 'icons/" + iconType + ".png' and project setup.",
                "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }



    /**
     * make this like a js callback later, not a good idea to have this method
     * call or delete myWindow, delegate to caller
     * @throws IOException
     */
    public void handleExistingUser() throws IOException {
        String savedName = reader.readName(); // Use the injected reader
        System.out.println("Skipping welcome screen. User: " + savedName);

        if (IDE_HOME_PATH != null) {
            new mainWindow();
            if (myWindow != null) {
                myWindow.dispose();
            }
        } else {
            showConfigurationError();
        }
    }

    /**
     * This method is called when the IDE home path cannot be determined,
     * due to corrupted or missing configuration files
     */
    public void showConfigurationError() {
        JOptionPane.showMessageDialog(null,
                "Could not determine IDE home path. Please restart the application and set it.",
                "Configuration Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}