package cecs429.ui;

import cecs429.ui.views.MainFrame;
import edu.csulb.BetterTermDocumentIndexer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrameController {
    private MainFrame mainFrame;
    private JButton chooseFolderButton;

    private BetterTermDocumentIndexer indexer;

    public MainFrameController() {
        indexer = new BetterTermDocumentIndexer();

        initComponents();
        initListeners();
    }

    public static void main(String[] args) {
        MainFrameController ctrl = new MainFrameController();
    }

    private void initComponents() {
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        chooseFolderButton = mainFrame.getChooseFolderButton() ;
    }

    private void initListeners() {
        chooseFolderButton.addActionListener(new FolderBtnListener());
    }

    private class FolderBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("select folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            int result = chooser.showOpenDialog((Component) e.getSource());
            if(result==JFileChooser.APPROVE_OPTION) {
                indexer.run(chooser.getSelectedFile().toPath());
            }
        }
    }
}
