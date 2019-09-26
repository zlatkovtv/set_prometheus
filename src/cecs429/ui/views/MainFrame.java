package cecs429.ui.views;

import javax.swing.*;

public class MainFrame extends JFrame {
    private JPanel mainWindow;

    public JButton getChooseFolderButton() {
        return chooseFolderButton;
    }

    private JButton chooseFolderButton;

    public MainFrame() {
        setSize(600, 400);
        setContentPane(mainWindow);
        setLocationRelativeTo(null);
    }
}
