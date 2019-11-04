package cecs429.main;

import cecs429.ui.MainFrameController;

import javax.swing.*;
import java.awt.*;

public class UIMain {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setUIFont(new javax.swing.plaf.FontUIResource("Serif",Font.PLAIN,16));
            MainFrameController ctrl = new MainFrameController(new BetterTermDocumentIndexer(null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }
}
