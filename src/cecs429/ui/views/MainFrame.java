package cecs429.ui.views;

import javax.swing.*;
import java.awt.*;

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
    private JButton button1;
    private JComboBox comboBox1;

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

    public JComboBox getComboBox1() {return comboBox1; }

    public JButton getButton1() {return button1; }

    public MainFrame() {
        setSize(1000, 850);
        setContentPane(mainWindow);
        setLocationRelativeTo(null);
    }

    private void createUIComponents() {
        String[] queries = {"Boolean Query", "Ranked Query"};
        comboBox1 = new JComboBox(queries);
    }
}
