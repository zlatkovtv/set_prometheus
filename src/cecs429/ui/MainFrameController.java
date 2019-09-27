package cecs429.ui;

import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.ui.views.MainFrame;
import edu.csulb.BetterTermDocumentIndexer;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrameController {
    private final int VOCABULARY_LIMIT = 1000;

    private MainFrame mainFrame;
    private JButton chooseFolderButton;
    private JButton stemTokenButton;
    private JButton printVocabButton;
    private JButton quitButton;
    private JTextArea console;
    private JTextField queryInput;
    private JButton queryButton;
    private JLabel indexTimer;

    private BetterTermDocumentIndexer indexer;

    public MainFrameController() {
        indexer = new BetterTermDocumentIndexer();

        initComponents();
        initListeners();
        chooseFolderButton.doClick();
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        MainFrameController ctrl = new MainFrameController();
    }

    private void initComponents() {
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        chooseFolderButton = mainFrame.getChooseFolderButton();
        stemTokenButton = mainFrame.getStemTokenButton();
        printVocabButton = mainFrame.getPrintVocabButton();
        quitButton = mainFrame.getQuitButton();
        console = mainFrame.getConsole();
        queryInput = mainFrame.getQueryInput();
        queryButton = mainFrame.getQueryButton();
        indexTimer = mainFrame.getIndexTimer();
        setButtonsEnabled(false);
    }

    private void initListeners() {
        chooseFolderButton.addActionListener(new FolderBtnListener());
        stemTokenButton.addActionListener(new StemBtnListener());
        printVocabButton.addActionListener(new VocabBtnListener());
        quitButton.addActionListener(new QuitBtnListener());
        queryButton.addActionListener(new QueryBtnListener());
    }

    private class FolderBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            popFileChooser(e);
        }
    }

    private void popFileChooser(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("select folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog((Component) e.getSource());
        if(result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            setButtonsEnabled(false);
            long ranFor = indexer.runIndexer(chooser.getSelectedFile().toPath());
            indexTimer.setText("Indexing took " + ranFor + " seconds.");
            setButtonsEnabled(true);
        } catch (Exception ex) {
            setButtonsEnabled(false);
            indexTimer.setText("");
        }
    }

    private void setButtonsEnabled(boolean state) {
        printVocabButton.setEnabled(state);
        queryButton.setEnabled(state);
        queryInput.setEditable(state);
    }

    private class StemBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String token = JOptionPane.showInputDialog("Enter a token to be stemmed");
            if(token == null) {
                return;
            }

            String stem = indexer.stemToken(token);
            String text = "Original token: " + token;
            text += "\nStem: " + stem;
            console.setText(text);
        }
    }

    private class VocabBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder termsString = new StringBuilder();
            List<String> terms = indexer.getVocabulary();
            for (int i = 0; i < VOCABULARY_LIMIT; i++) {
                termsString.append(terms.get(i));
                termsString.append("\n");
            }

            termsString.append("Total number of terms: " + terms.size());
            console.setText(termsString.toString());
        }
    }

    private class QuitBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private class QueryBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder termsString = new StringBuilder();
            List<Integer> docIds = indexer.runQuery(queryInput.getText());
            DocumentCorpus corpus = indexer.getCorpus();
            if(docIds.size() == 0) {
                console.setText("0 documents found for this query");
                return;
            }

            for (int i = 0; i < docIds.size(); i++) {
                termsString.append("Document " + (i + 1) +": " + corpus.getDocument(docIds.get(i)).getTitle());
                termsString.append("\n");
            }

            termsString.append("Total number of documents: " + docIds.size());

            console.setText(termsString.toString());
        }
    }
}
