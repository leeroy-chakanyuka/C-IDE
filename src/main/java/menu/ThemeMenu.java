package menu;

import IDE.mainWindow;
import codeEditor.EditorPanel;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ThemeMenu extends JMenu {


    private JMenuItem darkTheme = new JMenuItem("Dark Theme");
    private JMenuItem lightTheme = new JMenuItem("Light Theme");
    private mainWindow owner;
    private JTabbedPane tabbedPane;


    public ThemeMenu(mainWindow owner, JTabbedPane tabbedPane) {
        super("Themes");
        this.owner = owner;
        this.tabbedPane = tabbedPane;

        this.add(lightTheme);
        this.add(darkTheme);

        setupActionListeners();
    }

    private void setupActionListeners() {
        // Switch these method calls to fix the issue
        lightTheme.addActionListener(e -> applyLight());
        darkTheme.addActionListener(e -> applyDark());
    }

    private void applyLight() {
        try {
            FlatLightLaf.setup();
            SwingUtilities.updateComponentTreeUI(owner);

            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex != -1) {
                Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);

                if (selectedComponent instanceof EditorPanel) {
                    EditorPanel editorPanel = (EditorPanel) selectedComponent;
                    RSyntaxTextArea textArea = editorPanel.getTextArea(); // Get the correct instance
                    Theme theme = Theme.load(
                            getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/default.xml"));
                    theme.apply(textArea); // Apply the theme to the correct instance
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load theme: " + e.getMessage());
        }
    }

    private void applyDark() {
        try {
            FlatDarkLaf.setup();
            SwingUtilities.updateComponentTreeUI(owner);
            owner.setEditorPaneColor(new Color(0, 5, 193));
            EditorPanel.setLangPanelCol(new Color(70, 73, 75));

            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex != -1) {
                Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);

                if (selectedComponent instanceof EditorPanel) {
                    EditorPanel editorPanel = (EditorPanel) selectedComponent;
                    RSyntaxTextArea textArea = editorPanel.getTextArea(); // Get the correct instance
                    Theme theme = Theme.load(
                            getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
                    theme.apply(textArea);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load theme: " + e.getMessage());
        }
    }
}