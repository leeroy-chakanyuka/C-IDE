import com.formdev.flatlaf.FlatLightLaf;
import onBoarding.welcomeScreen;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SwingUtilities.invokeLater(() -> {
            try {
                welcomeScreen ex = new welcomeScreen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}