package IDE;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;

import Bottom.*;
import codeEditor.EditorPanel;
import menu.menuBar;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import sideBar.SideBar;
import utils.*;
import sideBar.*;



public class mainWindow extends JFrame {
    private static UndoManager undoManager;
    private SideBar sidePanel;
    public JTabbedPane editorPane;
    private Font myFont = new Font("verdana", Font.BOLD, 14);
    private JTextField commandInput;
    private OutputConsole outputArea;
    private configReader reader = new configReader(config.getIdeHomePath(), config.getDataFile(), config.META_CONFIG_FILE);
    private Font menuFont = new Font("vedana", Font.PLAIN, 14 );
    public static RSyntaxTextArea textArea = new RSyntaxTextArea(70, 180);
    private Bottom.Container bottomPanel;
    public mainWindow() throws IOException {
        this.undoManager = new UndoManager();

        this.setLayout(null);

        this.editorPane = new JTabbedPane();
        this.editorPane.setFocusable(false);
        this.editorPane.setBounds(320, 5, 1060, 500);
        this.editorPane.setBackground(new Color(203, 108, 230));
        this.add(this.editorPane);
        this.outputArea = new OutputConsole(320, 510, 1060, 240);

        this.bottomPanel = new Bottom.Container(outputArea);
        this.bottomPanel.setBounds(320, 510, 1060, 240);
        this.add(this.bottomPanel);


        this.sidePanel = new SideBar(this, editorPane, outputArea.getOutputArea());
        this.add(sidePanel);

        JMenuBar myMenu = new menuBar(this.sidePanel, this.editorPane, this, outputArea.getOutputArea());

        this.setIconImage(ImageIO.read(getClass().getClassLoader().getResource("icons/logo.png")));
        this.setJMenuBar(myMenu);
        this.setSize(1400, 800);
        this.setTitle("Chax IDE");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    public void setEditorPaneColor(Color col){
        this.editorPane.setBackground(col);
    }



    public int findOpenFileTab(File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            return -1;
        }

        for (int i = 0; i < editorPane.getTabCount(); i++) {
            Component comp = editorPane.getComponentAt(i);
            if (comp instanceof EditorPanel) {
                EditorPanel editorPanel = (EditorPanel) comp;
                File associatedFile = editorPanel.getAssociatedFile();
                if (associatedFile != null) {
                    try {
                        if (associatedFile.getCanonicalPath().equals(canonicalPath)) {
                            return i;
                        }
                    } catch (IOException e) {

                    }
                }
            }
        }
        return -1;
    }
}
