package cecs429.ui.views;

import javax.swing.*;

public class MainFrame extends JFrame {
    private JPanel mainWindow;
    private JButton chooseFolderButton;
    private JButton stemTokenButton;
    private JButton printVocabButton;
    private JButton quitButton;
    private JTable console;
    private JTextField queryInput;
    private JButton queryButton;
    private JLabel indexTimer;
    private JScrollPane scrollPane;

    public JLabel getIndexTimer() {
        return indexTimer;
    }

    public JTable getConsole() {
        return console;
    }

    public JTextField getQueryInput() {
        return queryInput;
    }

    public JButton getQueryButton() {
        return queryButton;
    }

    public JButton getChooseFolderButton() {
        return chooseFolderButton;
    }

    public JButton getStemTokenButton() {
        return stemTokenButton;
    }

    public JButton getPrintVocabButton() {
        return printVocabButton;
    }

    public JButton getQuitButton() {
        return quitButton;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public MainFrame() {
        setSize(800, 650);
        setContentPane(mainWindow);
        setLocationRelativeTo(null);
    }
}
