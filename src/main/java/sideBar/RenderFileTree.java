package sideBar;


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
import java.util.Arrays;

public class RenderFileTree {

        private JTree fileTree;
        private FileOpenCallback fileOpenCallback;

        public interface FileOpenCallback {
            void onFileDoubleClicked(File file);
        }

        public RenderFileTree(FileOpenCallback callback) {
            this.fileOpenCallback = callback;
        }


        public JScrollPane createTreeFromDirectory(String directoryPath, int x, int y, int width, int height) throws IOException {
            File directory = new File(directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                throw new IOException("Invalid directory: " + directoryPath);
            }

            FileNode rootNode = new FileNode(directory);
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);
            loadDirectoryContents(root, directory);

            fileTree = new JTree(root);
            fileTree.setBounds(x, y, width, height);

            addTreeListeners();
            setupTreeIcons();

            JScrollPane scrollPane = new JScrollPane(fileTree);
            scrollPane.setBounds(x, y, width, height);

            fileTree.expandRow(0);
            return scrollPane;
        }


        public JScrollPane createTreeFromDirectory(String directoryPath) throws IOException {
            return createTreeFromDirectory(directoryPath, 0, 40, 320, 760);
        }


        private void loadDirectoryContents(DefaultMutableTreeNode parentNode, File directory) {
            if (!directory.isDirectory()) return;

            if (parentNode.getChildCount() == 1 && parentNode.getFirstChild().toString().equals("Loading...")) {
                parentNode.removeAllChildren();
            }

            File[] files = directory.listFiles();
            if (files != null) {
                Arrays.sort(files, (f1, f2) -> {
                    if (f1.isDirectory() && !f2.isDirectory()) return -1;
                    if (!f1.isDirectory() && f2.isDirectory()) return 1;
                    return f1.getName().compareToIgnoreCase(f2.getName());
                });

                for (File file : files) {

                    if (file.getName().endsWith(".class") ||
                            file.getName().equals("data.txt") ||
                            file.isHidden()) {
                        continue;
                    }

                    FileNode fileNode = new FileNode(file);
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(fileNode);
                    parentNode.add(childNode);

                    if (file.isDirectory()) {
                        boolean hasChildren = hasVisibleChildren(file);
                        if (hasChildren) {
                            childNode.add(new DefaultMutableTreeNode("Loading..."));
                        }
                    }
                }
            }
        }


        private boolean hasVisibleChildren(File directory) {
            File[] subFiles = directory.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    if (!subFile.isHidden() &&
                            !subFile.getName().endsWith(".class") &&
                            !subFile.getName().equals("data.txt")) {
                        return true;
                    }
                }
            }
            return false;
        }


        private void addTreeListeners() {
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
                                if (clickedFile.isFile() && fileOpenCallback != null) {
                                    fileOpenCallback.onFileDoubleClicked(clickedFile);
                                }
                            }
                        }
                    }
                }
            });
        }


        private void setupTreeIcons() {
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
            fileTree.setCellRenderer(renderer);
        }


        private ImageIcon scaleIcon(Icon icon, int width, int height) {
            if (icon == null) {
                return null;
            }

            BufferedImage originalImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = originalImage.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }


        public JTree getFileTree() {
            return fileTree;
        }


        public void refreshTree() {
            if (fileTree != null) {
                fileTree.updateUI();
                fileTree.repaint();
            }
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
    }

