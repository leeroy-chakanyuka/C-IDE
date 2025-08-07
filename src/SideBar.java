import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class SideBar extends JPanel {
    String name;
    private JTree fileTree;
    private mainWindow parentWindow;
    private FileActionHandler fileHandler;

    private static ImageIcon fileIcon;
    private static ImageIcon folderIcon;

    //TO DO
    //RIGHT CLICK FUNCTIONALITY
    //

//    static {
//        try {
//            fileIcon = new ImageIcon(ImageIO.read(new File("resources/icons/file.png")));
//            folderIcon = new ImageIcon(ImageIO.read(new File("resources/icons/folder.png")));
//        } catch (IOException e) {
//            System.err.println("Error loading sidebar icons: " + e.getMessage());
//
//            fileIcon = null;
//            folderIcon = null;
//        }
//    }


    public SideBar(mainWindow parent, JTabbedPane editor) throws IOException {
        this.parentWindow = parent;
        this.setBounds(0, 0, 320, 800);
        this.setLayout(null);
        this.greetUser(config.getName());
        createFullFileExplorer();
    }



    private void createFullFileExplorer() throws IOException {
        String rootPath = config.getPath();
        File rootDirectory = new File(rootPath);

        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                    "Project root directory not found: " + rootPath + "\nPlease create this directory or configure a valid path.",
                    "Directory Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FileNode rootNode = new FileNode(rootDirectory);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);

        loadDirectoryContents(root, rootDirectory);

        fileTree = new JTree(root);
        fileTree.setBounds(0, 40, 320, 760);

        fileTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                TreePath path = event.getPath();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();

                if (userObject instanceof FileNode) {
                    FileNode fileNode = (FileNode) userObject;
                    if (fileNode.getFile().isDirectory() && !fileNode.isLoaded()) {
                        if (node.getChildCount() == 1 && node.getFirstChild().toString().equals("Loading...")) {
                            node.removeAllChildren();
                        }
                        loadDirectoryContents(node, fileNode.getFile());
                        fileNode.setLoaded(true);
                    }
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            }
        });

        fileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = fileTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        Object userObject = node.getUserObject();
                        if (userObject instanceof FileNode) {
                            FileNode fileNode = (FileNode) userObject;
                            File clickedFile = fileNode.getFile();

                            if (clickedFile.isFile()) {
                                openFileInEditor(clickedFile);
                            }
                        }
                    }
                }
            }
        });

        setupTreeIcons(fileTree);

        JScrollPane scrollPane = new JScrollPane(fileTree);
        scrollPane.setBounds(0, 40, 320, 760);
        this.add(scrollPane);

        fileTree.expandRow(0);
    }

    private void openFileInEditor(File file) {
        if (parentWindow == null) {
            JOptionPane.showMessageDialog(this,
                    "Cannot open file: Parent window reference is missing.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fileName = file.getName().toLowerCase();


        try {
            // Check if the file is already open in mainWindow
            int existingTabIndex = parentWindow.findOpenFileTab(file);
            if (existingTabIndex != -1) {
                parentWindow.editorPane.setSelectedIndex(existingTabIndex);
                JOptionPane.showMessageDialog(this, "File is already open.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                String[] options = {"Open in Editor", "Preview HTML", "Both", "Cancel"};
                int choice = JOptionPane.showOptionDialog(this,
                        "How would you like to open this HTML file?",
                        "Open HTML File",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                switch (choice) {
                    case 0:
                        parentWindow.newPopulatedTab(file.getName(), parentWindow.readFileContent(file), parentWindow.detectSyntaxStyle(file.getName()), file);
                        break;
                    case 1:
                        parentWindow.showHtmlPreview(file);
                        break;
                    case 2:
                        parentWindow.newPopulatedTab(file.getName(), parentWindow.readFileContent(file), parentWindow.detectSyntaxStyle(file.getName()), file);
                        parentWindow.showHtmlPreview(file);
                        break;
                    case 3:
                        break;
                }
            } else if (parentWindow.isTextFile(fileName)) {
                parentWindow.newPopulatedTab(file.getName(), parentWindow.readFileContent(file), parentWindow.detectSyntaxStyle(file.getName()), file);
            } else {
                parentWindow.openFileInDefaultBrowser(file);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not read file for opening: " + file.getName() + "\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadDirectoryContents(DefaultMutableTreeNode parentNode, File directory) {
        if (!directory.isDirectory()) return;

        if (parentNode.getChildCount() == 1 && parentNode.getFirstChild().toString().equals("Loading...")) {
            parentNode.removeAllChildren();
        }

        File[] files = directory.listFiles();
        if (files != null) {
            java.util.Arrays.sort(files, (f1, f2) -> {
                if (f1.isDirectory() && !f2.isDirectory()) return -1;
                if (!f1.isDirectory() && f2.isDirectory()) return 1;
                return f1.getName().compareToIgnoreCase(f2.getName());
            });

            for (File file : files) {
                //hide .class files and data.txt
                if (file.getName().endsWith(".class") || file.getName().equals("data.txt")) {
                    continue;
                }

                if (file.isHidden()) continue;

                FileNode fileNode = new FileNode(file);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(fileNode);
                parentNode.add(childNode);

                if (file.isDirectory()) {
                    boolean hasChildren = false;
                    File[] subFiles = file.listFiles();
                    if (subFiles != null) {
                        for (File subFile : subFiles) {

                            if (!subFile.isHidden() && !subFile.getName().endsWith(".class") && !subFile.getName().equals("data.txt")) {
                                hasChildren = true;
                                break;
                            }
                        }
                    }
                    if (hasChildren) {
                        childNode.add(new DefaultMutableTreeNode("Loading..."));
                    }
                }
            }
        }
    }

    private void setupTreeIcons(JTree tree) {
        // default ones looked nicer than what we have in resources
        Icon defaultFileIcon = UIManager.getIcon("Tree.leafIcon");
        Icon defaultFolderIcon = UIManager.getIcon("Tree.closedIcon");

        ImageIcon scaledFileIcon = scaleIcon(defaultFileIcon, 30, 30);
        ImageIcon scaledFolderIcon = scaleIcon(defaultFolderIcon, 35, 35);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                          boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    Object userObject = node.getUserObject();

                    if (userObject instanceof FileNode) {
                        FileNode fileNode = (FileNode) userObject;
                        File file = fileNode.getFile();

                        if (file.isDirectory()) {
                            setIcon(scaledFolderIcon);
                        } else {
                            setIcon(scaledFileIcon);
                        }
                        setText(file.getName());
                    } else if (userObject instanceof String && userObject.equals("Loading...")) {
                        setIcon(scaledFileIcon);
                        setText("Loading...");
                    }
                }
                return this;
            }
        };
        tree.setCellRenderer(renderer);
    }


    private ImageIcon scaleIcon(Icon icon, int width, int height) {
        if (icon == null) {
            return null;
        }


        BufferedImage originalImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = originalImage.createGraphics();

        // Draw the icon instead - this happened because we cant coerce flatlaf icons to ImageIcons
        icon.paintIcon(null, g, 0, 0);
        g.dispose();


        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }


    private static class FileNode {
        private File file;
        private boolean loaded = false;

        public FileNode(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public boolean isLoaded() {
            return loaded;
        }

        public void setLoaded(boolean loaded) {
            this.loaded = loaded;
        }

        @Override
        public String toString() {
            return file.getName();
        }
    }

    public void refreshFileExplorer() {
        try {
            for (Component comp : getComponents()) {
                if (comp instanceof JScrollPane) {
                    remove(comp);
                    break;
                }
            }
            createFullFileExplorer();
            revalidate();
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error refreshing file explorer: " + e.getMessage(), "Refresh Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void greetUser() {
        greetUser(null);
    }

    public void greetUser(String name) {
        JPanel greet = new JPanel();
        greet.setBounds(0, 0, 320, 30);
        JLabel yourName = new JLabel();
        if (name != null) {
            yourName.setText("Let's Build " + name);
        } else {
            yourName.setText("Let's Build");
        }
        yourName.setFont(new Font("verdana", Font.BOLD, 18));
        greet.add(yourName);
        this.add(greet);
    }
}
