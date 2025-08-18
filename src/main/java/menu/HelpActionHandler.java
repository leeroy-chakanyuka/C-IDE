package menu;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JOptionPane;

public class HelpActionHandler {

    private void openUrl(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(null, "Desktop is not supported or cannot browse URLs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to open URL: " + url, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openLinkedIn() {
        openUrl("https://linkedin.com/in/leeroy-chakanyuka-a610b5303/");
    }

    public void openGitHub() {
        openUrl("https://github.com/leeroy-chakanyuka/C-IDE");
    }

    public void openDocs() {
        openUrl("https://github.com/leeroy-chakanyuka/C-IDE/README");
    }
}