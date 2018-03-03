package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.resource.international.OptionDlgConst;

class TabNote extends JPanel implements ActionListener {

    /**
     * 日记面板构建器
     * 
     * @param prmDialog
     *            : 父对话盒
     * @param prmWidth
     *            : 面板宽度
     * @param prmHeight
     *            : 面板高度
     * @param prmCustomOptions
     *            : 存取数据的地方
     */
    public TabNote(JDialog prmDialog, int prmWidth, int prmHeight) {
        dialog = prmDialog;
        width = prmWidth;
        height = prmHeight;
        initComponents();
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        setBorder(null);
        // 日记格式
        PIMSeparator noteformatTitle = new PIMSeparator(OptionDlgConst.OPTION_DIARY_FORMAT);
        noteformatTitle
                .setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(noteformatTitle);

        // 字形
        JLabel fontStyleLabel = new JLabel(OptionDlgConst.OPTION_FONT_STYLE, 'F');
        // 字体颜色
        JLabel fontColorLabel = new JLabel(OptionDlgConst.OPTION_FONT_COLOR, 'C');
        // 背景
        JLabel backgroundLabel = new JLabel(OptionDlgConst.OPTION_FONT_BACKGROUND, 'K');
        // 当前密码
        JLabel currsorPasswordLabel = new JLabel(OptionDlgConst.OPTION_CURRENT_PASSWORD, 'P');
        int labelWidth =
                10 + CASDialogKit.getMaxWidth(new JComponent[] { fontStyleLabel, fontColorLabel, backgroundLabel,
                        currsorPasswordLabel });
        JComboBox fontStyleCombo = new JComboBox();
        JComboBox fontColorCombo = new JComboBox();
        JComboBox backgroundCombo = new JComboBox();
        fontStyleLabel.setLabelFor(fontStyleCombo);
        fontStyleLabel.setBounds(4 * CustOpts.HOR_GAP, noteformatTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        fontStyleCombo.setBounds(fontStyleLabel.getX() + fontStyleLabel.getWidth(), fontStyleLabel.getY(), 140,
                CustOpts.BTN_HEIGHT);
        add(fontStyleLabel);
        add(fontStyleCombo);
        // fontStyleCombo.setSelectedIndex(CustomOptions.custOps.getFontStyle());
        fontColorLabel.setLabelFor(fontStyleCombo);
        fontColorLabel.setBounds(4 * CustOpts.HOR_GAP, fontStyleCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        fontColorCombo.setBounds(fontColorLabel.getX() + fontColorLabel.getWidth(), fontColorLabel.getY(), 140,
                CustOpts.BTN_HEIGHT);
        add(fontColorLabel);
        add(fontStyleCombo);
        // fontColorCombo.setSelectedIndex(CustomOptions.custOps.getFontColor());
        backgroundLabel.setLabelFor(backgroundCombo);
        backgroundLabel.setBounds(4 * CustOpts.HOR_GAP, fontColorCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        backgroundCombo.setBounds(backgroundLabel.getX() + backgroundLabel.getWidth(), backgroundLabel.getY(), 140,
                CustOpts.BTN_HEIGHT);
        add(backgroundLabel);
        add(backgroundCombo);
        // backgroundCombo.setSelectedIndex(CustomOptions.custOps.getBackColor());

        // 字号
        JLabel fontSizeLabel = new JLabel(OptionDlgConst.OPTION_FONT_SIZE, 'Z');
        int fontSizeLabelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { fontSizeLabel });
        fontSizeCombo = new JComboBox();
        fontSizeLabel.setLabelFor(fontSizeCombo);
        fontSizeLabel.setBounds(fontStyleCombo.getX() + fontStyleCombo.getWidth() + 2 * CustOpts.HOR_GAP,
                fontStyleCombo.getY(), fontSizeLabelWidth, CustOpts.LBL_HEIGHT);
        fontSizeCombo.setBounds(fontSizeLabel.getX() + fontSizeLabel.getWidth(), fontSizeLabel.getY(), 140,
                CustOpts.BTN_HEIGHT);
        add(fontSizeLabel);
        add(fontSizeCombo);
        // fontSizeCombo.setSelectedIndex(CustomOptions.custOps.getFontSize());

        // 密码选项----------------------
        PIMSeparator passwordOptionTitle = new PIMSeparator(OptionDlgConst.OPTION_PASSWORD_OPTION);
        passwordOptionTitle.setBounds(CustOpts.HOR_GAP,
                backgroundCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP,
                CustOpts.SEP_HEIGHT);
        add(passwordOptionTitle);
        currsorPasswordField = new JPasswordField();
        currsorPasswordLabel.setLabelFor(currsorPasswordField);
        currsorPasswordLabel.setBounds(4 * CustOpts.HOR_GAP, passwordOptionTitle.getY() + CustOpts.LBL_HEIGHT
                + CustOpts.VER_GAP, labelWidth, CustOpts.LBL_HEIGHT);
        currsorPasswordField.setBounds(currsorPasswordLabel.getX() + currsorPasswordLabel.getWidth(),
                currsorPasswordLabel.getY(), 150, CustOpts.BTN_HEIGHT);
        add(currsorPasswordLabel);
        add(currsorPasswordField);
        // currsorPasswordField.setText(CustomOptions.custOps.getCurrsorPassword());

        // 修改密码
        modifyButton = new JButton(OptionDlgConst.OPTION_CHANGE_PASSWORDS);
        modifyButton.setMnemonic('M');
        int modifyButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { modifyButton });
        modifyButton.setBounds(currsorPasswordField.getX() + currsorPasswordField.getWidth() + 2 * CustOpts.HOR_GAP,
                currsorPasswordField.getY(), modifyButtonWidth, CustOpts.BTN_HEIGHT);
        modifyButton.addActionListener(this);
    }

    /**
     * 设置日记信息
     */
    void setNotInfo() {
        // CustomOptions.custOps.setFontStyle(fontStyleCombo.getSelectedIndex());
        // CustomOptions.custOps.setFontColor(fontColorCombo.getSelectedIndex());
        // CustomOptions.custOps.setFontSize(fontSizeCombo.getSelectedIndex());
        // CustomOptions.custOps.setBackColor(backgroundCombo.getSelectedIndex());
        CustOpts.custOps.setCurrsorPassword(currsorPasswordField.getText());
    }

    /**
     * 动作监听器
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        // 取得事件源
        Object source = e.getSource();
        // 修改按钮
        if (source == modifyButton) {
            String storePassword = CustOpts.custOps.getCurrsorPassword();
            String inputPassword = currsorPasswordField.getText();
            if (storePassword.equals(inputPassword)) {
                new DlgChangePassword(dialog, true, currsorPasswordField).setVisible(true);
            } else {
                // 您输入的密码错误，请检查Caps Lock键是否被按下，并重新输入。
                SOptionPane.showErrorDialog(MessageCons.E10207);
            }
        }
    }

    private int width;
    private int height;
    private JDialog dialog;
    private JButton modifyButton;
    private JComboBox fontSizeCombo;
    private JPasswordField currsorPasswordField;

}
