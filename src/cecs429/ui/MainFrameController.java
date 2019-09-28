package cecs429.ui;

import cecs429.documents.DocumentCorpus;
import cecs429.ui.views.MainFrame;
import cecs429.ui.views.TextFrame;
import cecs429.main.BetterTermDocumentIndexer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFrameController {
    private final int VOCABULARY_LIMIT = 1000;

    private MainFrame mainFrame;
    private JButton chooseFolderButton;
    private JButton stemTokenButton;
    private JButton printVocabButton;
    private JButton quitButton;
    private JTable console;
    private JTextField queryInput;
    private JButton queryButton;
    private JLabel indexTimer;

    private BetterTermDocumentIndexer indexer;
    private List<Integer> lastQueryResults;

    public MainFrameController(BetterTermDocumentIndexer indexer) {
        this.indexer = indexer;
        initComponents();
        initListeners();
        chooseFolderButton.doClick();
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
        console.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        console.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();

                    try {
                        openDocumentContent(row);
                    } catch (Exception ex) {
                        return;
                    }
                }
            }
        });
    }

    private void openDocumentContent(int index) throws IOException {
        int selectedDocId = lastQueryResults.get(index);
        BufferedReader documentContent = new BufferedReader(indexer.getCorpus().getDocument(selectedDocId).getContent());
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = documentContent.readLine()) != null) {
            sb.append(line);
        }

        TextFrame textFrame = new TextFrame(sb.toString());
        textFrame.setVisible(true);
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
            List<String> strings = new ArrayList<>();
            String token = JOptionPane.showInputDialog("Enter a token to be stemmed");
            if(token == null) {
                return;
            }

            String stem = indexer.stemToken(token);
            strings.add("Original token: " + token);
            strings.add("Stem: " + stem);
            buildTable(strings);
        }
    }

    private class VocabBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<String> termsStrings = new ArrayList<String>();
            List<String> terms = indexer.getVocabulary();
            for (int i = 0; i < VOCABULARY_LIMIT; i++) {
                termsStrings.add(terms.get(i));
            }

            termsStrings.add("Total number of terms: " + terms.size());
            buildTable(termsStrings);
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
            List<String> termsStrings = new ArrayList<String>();
            if(queryInput.getText().isEmpty()) {
                buildTable("Please enter a valid query");
                return;

            }
            lastQueryResults = indexer.runQuery(queryInput.getText());
            DocumentCorpus corpus = indexer.getCorpus();
            if(lastQueryResults.size() == 0) {
                buildTable("0 documents found for this query");
                return;
            }

            for (int i = 0; i < lastQueryResults.size(); i++) {
                termsStrings.add("Document " + (i + 1) +": " + corpus.getDocument(lastQueryResults.get(i)).getTitle());
            }

            termsStrings.add("Total number of documents: " + lastQueryResults.size());
            buildTable(termsStrings);
        }
    }

    private void buildTable(List<String> data) {
        DefaultTableModel model = getTableModel();

        model.addColumn("Document name", data.toArray());
        this.console.setModel(model);
    }

    private void buildTable(String data) {
        DefaultTableModel model = getTableModel();

        model.addColumn("Document name", new String[] {data});

        this.console.setModel(model);
    }

    private DefaultTableModel getTableModel() {
        return new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
