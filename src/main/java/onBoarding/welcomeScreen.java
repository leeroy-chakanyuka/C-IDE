package onBoarding;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import utils.*;
/**
 * This class handles the initial user onboarding process, including name entry,
 * IDE home path selection, and user state management. It displays a welcome screen
 * for new users and automatically launches the main IDE for returning users.
 *
 * The UI construction and event handling logic are largely delegated to
 * utils.makeConfig and utils.makeConfigHandler classes, respectively.
 * Configuration data access is delegated to a separate Config/ConfigReader structure.
 */
public class welcomeScreen {

    private int width = 650;
    private int height = 500;
    private JFrame myWindow;
    private JTextField pathField;
    private JTextField nameField;
    private int isLoggedIn = 0;
    private configReader reader; // onBoarding.welcomeScreen still needs an instance of utils.configReader for its own logic
    private JPanel welcomePanel;
    private makeConfig makeConfigInstance;
    private makeConfigHandler configHandler;

    private static final String DATA_FILE_NAME = "data.txt";
    private static final String META_CONFIG_FILE = "chax_ide_config.path";


    private static String IDE_HOME_PATH = null;

    /**
     * Static initializer block that runs once before the class is loaded.
     * Pre-loads the IDE home path from the meta configuration file to determine
     * if the user has already been onboarded.
     */
    static {
        configReader tempReaderForStaticInit = new configReader(null, DATA_FILE_NAME, META_CONFIG_FILE);
        IDE_HOME_PATH = tempReaderForStaticInit.readIdeHomePathFromMetaConfig();
    }

    public welcomeScreen() throws IOException {
        initializeReader();
        initializeUIComponents();
        initializeDelegates();

        if (isLoggedIn == 0) {
            setupWelcomeScreen();
        } else {
            configHandler.handleExistingUser();
        }
    }

    private void initializeReader() {
        reader = new configReader(IDE_HOME_PATH, DATA_FILE_NAME, META_CONFIG_FILE);
        isLoggedIn = reader.readInitialState();
    }

    private void initializeUIComponents() {
        this.nameField = new JTextField(23);
        this.pathField = new JTextField(23);
    }

    private void initializeDelegates() {
        this.configHandler = new makeConfigHandler(nameField, pathField, IDE_HOME_PATH, myWindow, reader);

    }

    private void setupWelcomeScreen() throws IOException {
        myWindow = new JFrame();
        myWindow.setSize(width, height);
        myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myWindow.setLayout(null);
        myWindow.setTitle("Chax IDE");
        myWindow.setResizable(false);
        myWindow.setLocationRelativeTo(null);

        welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBounds(0, 0, width, height);

        this.makeConfigInstance = new makeConfig(welcomePanel, nameField, pathField, IDE_HOME_PATH, myWindow, configHandler, reader);

        addWelcomeComponents();
        configHandler.setWindowIcon(myWindow);
        myWindow.add(welcomePanel);
        myWindow.setVisible(true);


        configHandler.setMyWindow(myWindow);
        makeConfigInstance.setMyWindow(myWindow);
    }

    private void addWelcomeComponents() throws IOException {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 20, 8);

        addWelcomeMessage(gbc);
        makeConfigInstance.addNameField(gbc);
        makeConfigInstance.addPathField(gbc);
        makeConfigInstance.addFolderIcon(gbc);
    }


    private void addWelcomeMessage(GridBagConstraints gbc) {
        JLabel welcomeMessage = new JLabel("Welcome to Chax, IDE. Let's change the World");
        welcomeMessage.setFont(new Font("verdana", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        welcomePanel.add(welcomeMessage, gbc);
    }

}