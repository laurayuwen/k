// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.krun.gui.UIDesign;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.kframework.krun.gui.UIDesign.xmlEditor.XMLDocument;
import org.kframework.krun.gui.UIDesign.xmlEditor.XMLEditorKit;

public class ConfigurationPanel extends JPanel implements FocusListener, KeyListener {

    private static final boolean enableSave = false;
    private static final long serialVersionUID = 1L;
    public static HashMap<String, Integer> collapsedViews = new HashMap<String, Integer>();

    private String s;
    public JTextPane confText;
    public JButton save;
    private boolean changed;

    public ConfigurationPanel() {
    }

    public ConfigurationPanel(String s) {
        this.s = s;
        init();
    }

    public void init(String s) {
        this.s = s;
        init();
    }

    public void init() {
        this.setBackground(Color.WHITE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        confText = new JTextPane();
        confText.setEditorKit(new XMLEditorKit(confText));
        confText.setEditable(enableSave);
        confText.setText(s);
        confText.setPreferredSize(new Dimension(screenSize.width / 3 - 50, screenSize.height - 150));
        save = new JButton();
        save.setText("Save");
        this.removeAll();
        this.setVisible(false);
        this.setVisible(true);
        final JScrollPane thePane = new JScrollPane(confText);
        thePane.setPreferredSize(new Dimension(screenSize.width / 3 - 50, screenSize.height - 150));
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                thePane.getVerticalScrollBar().setValue(0);
                thePane.getHorizontalScrollBar().setValue(0);
            }
        });
        setDocumentEditable();
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(save);
        save.setVisible(false);
        this.add(thePane);
        this.revalidate();
        this.repaint();
        confText.addFocusListener(this);
        confText.addKeyListener(this);
    }

    public void setDocumentEditable() {
        ((XMLDocument) confText.getDocument()).setUserChanges(false);
    }

    @Override
    public void focusGained(FocusEvent arg0) {
    }

    @Override
    public void focusLost(FocusEvent arg0) {
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        String currentStr = confText.getText().replace(
                "<?xml version=\"1.0\" encoding=\"null\"?>", "");
        if (!s.equals(currentStr))
            changed = true;
        else {
            changed = false;
        }
        if (enableSave) {
            save.setVisible(changed);
        }
    }

}