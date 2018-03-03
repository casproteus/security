package org.cas.client.platform.casbeans;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.PaneConsts;

public class DialogInsertImage extends JDialog implements ActionListener, KeyListener {
    /** Creates a new instance of InsertImagJDialog */
    public DialogInsertImage(Frame f, PIMTextPane prmTextPane) {
        super(f, true);
        textPane = prmTextPane;
        init();
    }

    /** Creates a new instance of InsertImagJDialog */
    public DialogInsertImage(Dialog f, PIMTextPane prmTextPane) {
        super(f, true);
        textPane = prmTextPane;
        init();
    }

    private void init() {
        setTitle(PaneConsts.IMAGE);
        setSize(445, 210);
        setResizable(false);

        imageSource = new JLabel(PaneConsts.IMAGESOURCE, 'P');
        imageSource.setBounds(10, 10, 80, 20);
        replaceWord = new JLabel(PaneConsts.REPLACEWORD, 'T');
        replaceWord.setBounds(10, 40, 80, 20);
        imagePath = new JTextField();
        imagePath.setBounds(100, 10, 245, 23);
        rword = new JTextField();
        rword.setEditable(false);
        rword.setBounds(100, 40, 335, 23);
        browser = new JButton(PaneConsts.BROWSER);
        browser.setBounds(355, 10, 80, 23);
        northPanel = new JPanel();

        northPanel.add(imageSource);
        northPanel.add(replaceWord);
        northPanel.add(imagePath);
        northPanel.add(rword);
        northPanel.add(browser);
        northPanel.setBounds(0, 0, 435, 66);

        alignment = new JLabel(PaneConsts.ALIGNMENT, 'A');
        alignment.setBounds(10, 20, 70, 20);
        borderWidth = new JLabel(PaneConsts.BORDERWIDTH, 'B');
        borderWidth.setBounds(10, 50, 70, 23);
        combo = new JComboBox(PaneConsts.ALIGN);
        combo.setBounds(90, 20, 125, 23);
        bwidth = new JTextField();
        bwidth.setBounds(90, 50, 125, 23);
        layoutBorder = new TitledBorder(BorderFactory.createEtchedBorder(), PaneConsts.LAYOUT);
        layoutBorder.setTitleFont(new java.awt.Font(PaneConsts.SONG, 0, 12));
        borderPanel = new JPanel();
        borderPanel.setBorder(layoutBorder);
        borderPanel.add(alignment);
        borderPanel.add(borderWidth);
        borderPanel.add(combo);
        borderPanel.add(bwidth);
        borderPanel.setBounds(8, 70, 225, 80);

        horizontal = new JLabel(PaneConsts.HORIZONTAL, 'H');
        horizontal.setBounds(10, 20, 55, 20);
        vertical = new JLabel(PaneConsts.VERTICAL, 'V');
        vertical.setBounds(10, 50, 55, 20);
        horizon = new JTextField();
        horizon.setBounds(70, 20, 125, 23);
        vert = new JTextField();
        vert.setBounds(70, 50, 125, 23);
        spaceBorder = new TitledBorder(BorderFactory.createEtchedBorder(), PaneConsts.SPACING);
        spaceBorder.setTitleFont(new java.awt.Font(PaneConsts.SONG, 0, 12));
        spacJPanel = new JPanel();
        spacJPanel.setBorder(spaceBorder);
        spacJPanel.add(horizontal);
        spacJPanel.add(vertical);
        spacJPanel.add(horizon);
        spacJPanel.add(vert);
        spacJPanel.setBounds(235, 70, 202, 80);

        okButton = new JButton(PaneConsts.HYPERLINK_OK);
        okButton.setBounds(265, 10, 80, 23);
        okButton.setEnabled(false);
        cancelButton = new JButton(PaneConsts.HYPERLINK_CANCEL);
        cancelButton.setBounds(355, 10, 80, 23);
        southPanel = new JPanel();
        southPanel.add(okButton);
        southPanel.add(cancelButton);
        southPanel.setBounds(0, 145, 450, 56);

        centerPanel = new JPanel();
        centerPanel.add(northPanel);
        centerPanel.add(borderPanel);
        centerPanel.add(spacJPanel);
        centerPanel.add(southPanel);
        getContentPane().add(centerPanel);

        // 添加监听器
        browser.addActionListener(this);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        imagePath.addKeyListener(this);
    }

    public JTextField getTextField() {
        return rword;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(okButton)) {
            MutableAttributeSet inputAttributes = textPane.getInputAttributes();
            inputAttributes.addAttribute(StyleConstants.NameAttribute, HTML.Tag.IMG);
            String url = "file:///".concat(imagePath.getText());
            inputAttributes.addAttribute(HTML.Attribute.SRC, url);
            inputAttributes.addAttribute(HTML.Attribute.BORDER, bwidth.getText());
            inputAttributes.addAttribute(HTML.Attribute.HSPACE, horizon.getText());
            inputAttributes.addAttribute(HTML.Attribute.VSPACE, vert.getText());
            if (combo.getSelectedIndex() == 0)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, " ");
            else if (combo.getSelectedIndex() == 1)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "left");
            else if (combo.getSelectedIndex() == 2)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "right");
            else if (combo.getSelectedIndex() == 3)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "textTop");
            else if (combo.getSelectedIndex() == 4)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "absMiddle");
            else if (combo.getSelectedIndex() == 5)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "baseline");
            else if (combo.getSelectedIndex() == 6)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "absBottom");
            else if (combo.getSelectedIndex() == 7)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "bottom");
            else if (combo.getSelectedIndex() == 8)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "middle");
            else if (combo.getSelectedIndex() == 9)
                inputAttributes.addAttribute(HTML.Attribute.ALIGN, "top");
            textPane.replaceSelection(CASUtility.SPACE, inputAttributes.copyAttributes());
            dispose();
        } else if (source.equals(cancelButton))
            dispose();
        else if (source.equals(browser)) {
            tmpFileChooser = new JFileChooser();
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("jpg", PaneConsts.PICTURE_FILE));
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("gif", PaneConsts.PICTURE_FILE));
            tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame());// 弹出打开对话框
            File tmpFile = tmpFileChooser.getSelectedFile();// 得到被选中的文件
            if (tmpFile != null)
                imagePath.setText(tmpFile.getAbsolutePath());
            if (imagePath.getText() != null)
                okButton.setEnabled(true);
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

    private JLabel imageSource, replaceWord, borderWidth, alignment, horizontal, vertical;

    private JButton browser, okButton, cancelButton;

    private JTextField imagePath, rword, bwidth, horizon, vert;

    private TitledBorder layoutBorder, spaceBorder;

    private JPanel northPanel, southPanel, centerPanel, borderPanel, spacJPanel;

    private JComboBox combo;

    private PIMTextPane textPane;

    private JFileChooser tmpFileChooser;
}
