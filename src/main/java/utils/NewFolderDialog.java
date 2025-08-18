package utils;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class NewFolderDialog extends JDialog {



        private final Font myFont = new Font("verdana", Font.PLAIN, 12);
        private JTextField folderNameField;
        private JLabel locationLabel;
        private File selectedLocation;
        private boolean folderCreated = false;

        public NewFolderDialog(JFrame owner) {
            super(owner, "New Folder", true); // modal dialog
            initUI();
            pack();
            setLocationRelativeTo(owner);
        }

        private void initUI() {
            setLayout(new BorderLayout(10, 10));
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;


            JLabel nameLabel = new JLabel("Folder Name:");
            nameLabel.setFont(myFont);
            gbc.gridx = 0;
            gbc.gridy = 0;
            mainPanel.add(nameLabel, gbc);

            folderNameField = new JTextField(20);
            folderNameField.setFont(myFont);
            gbc.gridx = 1;
            gbc.gridy = 0;
            mainPanel.add(folderNameField, gbc);


            JLabel locationPrompt = new JLabel("Location:");
            locationPrompt.setFont(myFont);
            gbc.gridx = 0;
            gbc.gridy = 1;
            mainPanel.add(locationPrompt, gbc);

            locationLabel = new JLabel("No location selected");
            locationLabel.setFont(myFont);
            gbc.gridx = 1;
            gbc.gridy = 1;
            mainPanel.add(locationLabel, gbc);

            JButton chooseLocationButton = new JButton("Choose Location");
            chooseLocationButton.setFont(myFont);
            gbc.gridx = 1;
            gbc.gridy = 2;
            mainPanel.add(chooseLocationButton, gbc);

            chooseLocationButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                //  only show directories
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showDialog(this, "Select Directory");
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedLocation = fileChooser.getSelectedFile();
                    locationLabel.setText(selectedLocation.getAbsolutePath());
                }
            });


            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton createButton = new JButton("Create");
            createButton.setFont(myFont);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(myFont);
            buttonPanel.add(createButton);
            buttonPanel.add(cancelButton);

            //  for the "Create" button
            createButton.addActionListener(e -> {
                if (folderNameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a folder name.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (selectedLocation == null) {
                    JOptionPane.showMessageDialog(this, "Please select a location.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    folderCreated = true;
                    dispose(); // Close the dialog
                }
            });

            // for the "Cancel" button
            cancelButton.addActionListener(e -> {
                folderCreated = false;
                dispose(); // Close the dialog
            });

            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }


        public File getSelectedFolder() {
            if(selectedLocation == null || folderNameField.getText().trim().isEmpty()){
                return null;
            }
            return new File(selectedLocation, folderNameField.getText().trim());
        }

        public boolean isCreated() {
            return folderCreated;
        }
    }


