package org.cas.client.platform.casbeans;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.StyleConstants;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.resource.international.PaneConsts;

public class DialogHyperLink extends JDialog implements ActionListener, KeyListener {
    /** Creates a new instance of HyperlinkDialog */
    public DialogHyperLink(Frame f, PIMTextPane prmTextPane) {
        super(f, true);
        textPane = prmTextPane;
        init();
    }

    /** Creates a new instance of HyperlinkDialog */
    public DialogHyperLink(Dialog f, PIMTextPane prmTextPane) {
        super(f, true);
        textPane = prmTextPane;
        init();
    }

    private void init() {
        setTitle(PaneConsts.HYPERLINK);
        setSize(390, 110);
        setResizable(false);
        textField.requestFocusInWindow();
        // 布局
        panel = new JPanel();
        border = new TitledBorder(BorderFactory.createEtchedBorder(), PaneConsts.HYPERLINKINFOR);
        border.setTitleFont(new java.awt.Font(PaneConsts.SONG, 0, 12));
        panel.setBorder(border);
        label = new JLabel(PaneConsts.HYPERLINKSTYLE, 't');
        label.setBounds(20, 20, 50, 20);
        urlLabel = new JLabel(PaneConsts.HYPERLINKADRESS, 'u');
        urlLabel.setBounds(20, 50, 50, 20);
        combo =
                new JComboBox(new String[] { PaneConsts.HYPERLINK_OTHER, "file:", "ftp:", "gopher:", "http:", "https:",
                        "mailto:", "news:", "telnet:", "wais:" });
        combo.setSelectedIndex(4);
        combo.setBounds(100, 20, 80, 20);
        textField = new JTextField("http://", 150);
        textField.setBounds(100, 50, 150, 20);
        panel.add(label);
        panel.add(urlLabel);
        panel.add(combo);
        panel.add(textField);
        panel2 = new JPanel();
        okButton = new JButton(PaneConsts.HYPERLINK_OK);
        okButton.setBounds(10, 8, 80, 23);
        okButton.setEnabled(false);
        cancelButton = new JButton(PaneConsts.HYPERLINK_CANCEL);
        cancelButton.setBounds(10, 57, 80, 23);
        panel2.add(okButton);
        panel2.add(cancelButton);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(panel2, BorderLayout.EAST);
        getContentPane().add(mainPanel);

        // 添加监听器
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        combo.addActionListener(this);
        textField.addKeyListener(this);
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(okButton)) {
            textPane.insertLink(textField.getText());
            // textPane.addMouseListener(textPane);
            textPane.getInputAttributes().removeAttribute(StyleConstants.Underline);
            textPane.getInputAttributes().removeAttribute(StyleConstants.Foreground);
            dispose();
        } else if (source.equals(cancelButton))
            dispose();
        else if (source.equals(combo)) {
            if (combo.getSelectedIndex() == 0)
                textField.setText(null);
            else if (combo.getSelectedIndex() <= 5 && combo.getSelectedIndex() >= 1)
                textField.setText(((String) combo.getSelectedItem()).concat("//"));
            else if (combo.getSelectedIndex() > 5)
                textField.setText((String) combo.getSelectedItem());
        }
    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
     * pressed event.
     */
    public void keyPressed(
            KeyEvent e) {
        okButton.setEnabled(true);
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
     * released event.
     */
    public void keyReleased(
            KeyEvent e) {
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
     * event.
     */
    public void keyTyped(
            KeyEvent e) {
        okButton.setEnabled(true);
    }

    private JPanel panel, panel2, mainPanel;
    private JButton okButton, cancelButton;
    private JLabel label, urlLabel;
    private TitledBorder border;
    private JComboBox combo;
    private JTextField textField;
    private PIMTextPane textPane;
}
