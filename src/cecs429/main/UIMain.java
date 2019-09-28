package cecs429.main;

import cecs429.ui.MainFrameController;

import javax.swing.*;

public class UIMain {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            MainFrameController ctrl = new MainFrameController(new BetterTermDocumentIndexer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
