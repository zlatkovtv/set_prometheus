package cecs429.ui.views;

import javax.swing.*;

public class MainFrame extends JFrame {
    private JPanel mainWindow;
    private JButton chooseFolderButton;
    private JButton stemTokenButton;
    private JButton printVocabButton;
    private JButton quitButton;
    private JTextArea console;
    private JTextField queryInput;
    private JButton queryButton;
    private JLabel indexTimer;

    public JLabel getIndexTimer() {
        return indexTimer;
    }

    public JTextArea getConsole() {
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

    public MainFrame() {
        setSize(600, 400);
        setContentPane(mainWindow);
        setLocationRelativeTo(null);
    }
}
