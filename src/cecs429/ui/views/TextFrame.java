package cecs429.ui.views;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TextFrame extends JFrame
{
    public TextFrame(String content) {
        super("TextFrame");

        JTextArea ta = new JTextArea();
        ta.setText(content);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        getContentPane().add(ta);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        setSize(600, 600);
    }
}
