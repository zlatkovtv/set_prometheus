package cecs429.ui;

import cecs429.documents.DocumentCorpus;
import cecs429.index.DiskIndexWriter;
import cecs429.index.Posting;
import cecs429.index.ScorePosting;
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
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrameController {
    private final int VOCABULARY_LIMIT = 1000;

    private MainFrame mainFrame;
    private JButton chooseFolderButton;
    private JButton stemTokenButton;
    private JButton printVocabButton;
    private JButton quitButton;
    private JButton buildButton;
    private JTable console;
    private JTextField queryInput;
    private JButton queryButton;
    private JScrollPane scrollPane;
    private JLabel indexTimer;
    private JComboBox comboBox;

    private BetterTermDocumentIndexer indexer;
    private List<Posting> lastQueryResults;
    private List<ScorePosting> lastScoredQueryResults;
    private boolean isLastQueryScored;
    private Path path;
    private DocumentCorpus corpus;

    public MainFrameController(BetterTermDocumentIndexer indexer) {
        this.indexer = indexer;
        initComponents();
        initListeners();
//        chooseFolderButton.doClick();
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
        scrollPane = mainFrame.getScrollPane();
        indexTimer = mainFrame.getIndexTimer();
        comboBox = mainFrame.getComboBox1();
        buildButton = mainFrame.getButton1();

        setButtonsEnabled(false);
    }

    private void initListeners() {
        chooseFolderButton.addActionListener(new FolderBtnListener());
        stemTokenButton.addActionListener(new StemBtnListener());
        printVocabButton.addActionListener(new VocabBtnListener());
        quitButton.addActionListener(new QuitBtnListener());
        queryButton.addActionListener(new QueryBtnListener());
        comboBox.addActionListener(cbActionListener);
        buildButton.addActionListener(new BuildNtmListener());
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
        int selectedDocId;
        if(isLastQueryScored) {
            selectedDocId = lastScoredQueryResults.get(index).getDocumentId();
        } else {
            selectedDocId = lastQueryResults.get(index).getDocumentId();
        }

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
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            setButtonsEnabled(false);
//            this.path = chooser.getCurrentDirectory().getAbsolutePath();
            this.path = chooser.getSelectedFile().toPath();
            indexer = new BetterTermDocumentIndexer(this.path);
            this.corpus = indexer.getCorpus();
            this.corpus.getDocuments();
//            long ranFor = indexer.runIndexer();
//            indexTimer.setText("Indexing took " + ranFor + " seconds.");
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
            if (token == null) {
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

    private int getDropDownValue() {
        return this.comboBox.getSelectedIndex();
    }

    private class QueryBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<String> termsStrings = new ArrayList<String>();
            if (queryInput.getText().isEmpty()) {
                buildTable("Please enter a valid query");
                return;
            }

            int dropDownValue = getDropDownValue();

            if(dropDownValue == 0) {
                isLastQueryScored = false;
                try {
                    lastQueryResults = indexer.getResults(queryInput.getText(), path.toAbsolutePath().toString());
                    List<Integer> docIds = lastQueryResults.stream()
                            .map(x -> x.getDocumentId())
                            .collect(Collectors.toList());

                    Set<Integer> set = new HashSet<>(docIds);
                    docIds.clear();
                    docIds.addAll(set);
                    Collections.sort(docIds);

                    if (lastQueryResults.size() == 0) {
                        buildTable("0 documents found for this query");
                        return;
                    }

                    for (int i = 0; i < docIds.size(); i++) {
                        termsStrings.add("Document " + (i + 1) + ": " + corpus.getDocument(docIds.get(i)).getTitle());
                    }

                    termsStrings.add("Total number of documents: " + docIds.size());
                } catch(Exception ex) {
                    buildTable(ex.getMessage());
                    return;
                }
            } else {
                isLastQueryScored = true;
                lastScoredQueryResults = indexer.getScoreResults(queryInput.getText(), path.toAbsolutePath().toString());

                if (lastScoredQueryResults.size() == 0) {
                    buildTable("0 documents found for this query");
                    return;
                }

                for (int i = 0; i < lastScoredQueryResults.size(); i++) {
                    termsStrings.add("Document " + (i + 1) + ": "
                            + corpus.getDocument(lastScoredQueryResults.get(i).getDocumentId()).getTitle()
                            + ", Score: " + lastScoredQueryResults.get(i).getAccumulator());
                }

                termsStrings.add("Total number of documents: " + lastScoredQueryResults.size());
            }

            buildTable(termsStrings);
        }
    }

    private void buildTable(List<String> data) {
        DefaultTableModel model = getTableModel();
        model.addColumn("Results", data.toArray());
        this.console.setModel(model);
        scrollToBottom();
    }

    private void buildTable(String data) {
        DefaultTableModel model = getTableModel();
        model.addColumn("Results", new String[]{data});
        this.console.setModel(model);
        scrollToBottom();
    }

    private DefaultTableModel getTableModel() {
        return new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void scrollToBottom() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        scrollPane.validate();
        vertical.setValue( vertical.getMaximum() );
    }

    ActionListener cbActionListener = new ActionListener() {//add actionlistner to listen for change
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private class BuildNtmListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            try {
                indexer.runIndexer();
                DiskIndexWriter writer = new DiskIndexWriter(indexer.getIndex(), path.toAbsolutePath().toString(), indexer.getDocWeight());
                writer.writeIndex();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
