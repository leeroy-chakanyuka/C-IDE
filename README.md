
# Chax IDE Documentation

## 1. Overview

Welcome to the Chax IDE, a lightweight and extensible Integrated Development Environment built with Java Swing. This document provides a comprehensive overview of the project's architecture, features, and code structure.

## 2. Features

- **File and Folder Management**: Create, open, save, and manage files and folders.
- **Tabbed Editor**: Open multiple files in a tabbed interface for easy navigation.
- **Syntax Highlighting**: Code editor with syntax highlighting for various languages.
- **SideBar File Explorer**: A tree-based file explorer to navigate your project directory.
- **User Configuration**: Onboarding process for new users to set up their environment.

## 3. Project Structure

The project is organized into several packages, each responsible for a specific aspect of the IDE's functionality.

### 3.1. `IDE` Package

This is the main package that contains the entry point of the application.

- `mainWindow.java`: The main frame of the IDE, which orchestrates all the UI components like the menu bar, side panel, and editor pane.

### 3.2. `onBoarding` Package

Handles the initial setup and welcome screen for new users.

- `welcomeScreen.java`: The first screen a new user sees. It guides them through setting up their name and the IDE's home path.

### 3.3. `menu` Package

Contains all the classes related to the IDE's menu bar.

- `menuBar.java`: The main menu bar that houses all the different menus.
- `FileMenu.java`, `EditMenu.java`, `HelpMenu.java`: These classes define the "File", "Edit", and "Help" menus respectively.
- `FileActionHandler.java`, `EditActionHandler.java`, `HelpActionHandler.java`: These classes handle the logic for the actions performed from the menu items.

### 3.4. `sideBar` Package

Manages the file explorer panel on the left side of the IDE.

- `SideBar.java`: The main panel for the sidebar that includes a greeting message and the file tree.
- `RenderFileTree.java`: A utility class that creates and renders the file system tree within the sidebar.

### 3.5. `utils` Package

A collection of utility classes that provide helper functions used across the application.

- `config.java`: A centralized class to access application-wide configuration settings.
- `configReader.java` and `configWriter.java`: Classes responsible for reading from and writing to the configuration files.
- `handleDirs.java` and `handleFiles.java`: Helper classes for directory and file-related operations.
- `makeConfig.java` and `makeConfigHandler.java`: These classes manage the UI and logic for the initial user configuration.
- `NewFileDialog.java` and `NewFolderDialog.java`: Dialog windows for creating new files and folders.

## 4. How to Run

1. **Compile the source code**:
   ```bash
   javac -d . */*.java
   ```

2. **Run the main class**:
   ```bash
   java main
   ```

## 5. Coming Improvements

- **Terminal Integration**: Add an integrated terminal to the output panel.
- **Themes**: Add support for custom themes, including a dark mode.
