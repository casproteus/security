package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.OptionDlgConst;

class TabMail extends JPanel implements ActionListener {
    /**
     * 邮件面板构建器
     * 
     * @param prmDialog
     *            父窗体
     * @param prmWidth
     *            : 邮件面板宽度
     * @param prmHeight
     *            邮件面板高度
     * @param prmcCustomOptions
     *            : 存取数据的地方
     */
    public TabMail(JDialog prmDialog, int prmWidth, int prmHeight) {
        setSize(prmWidth, prmHeight);

        //
        parentDialog = prmDialog;
        width = prmWidth;
        height = prmHeight;

        // 初始化组件
        initComponent();
        addListeners();
    }

    /**
     * 根据面板的尺寸初始化面板
     */
    private void initComponent() {
        setBorder(null);
        // 电子邮件--------------
        PIMSeparator mailTitle = new PIMSeparator(OptionDlgConst.OPTION_EMAIL);
        mailTitle.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(mailTitle);

        // 更改邮件的位置和处理方法
        JLabel changeMailAddressLabel = new JLabel(OptionDlgConst.OPTION_CHANGE_MAIL_POSITION);
        changeMailAddressLabel.setBounds(4 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT + 2 * CustOpts.VER_GAP, width - 6
                * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        add(changeMailAddressLabel);
        // 电子邮件选项
        mailOptionButton = new JButton(OptionDlgConst.OPTION_EMAIL_OPTION_MAIL);
        mailOptionButton.setMnemonic('M');
        int mailButtonWidth = 50 + CASDialogKit.getMaxWidth(new JComponent[] { mailOptionButton });
        mailOptionButton.setEnabled(false);
        mailOptionButton.setBounds(width - mailButtonWidth - 2 * CustOpts.HOR_GAP, changeMailAddressLabel.getY()
                + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, mailButtonWidth, CustOpts.BTN_HEIGHT);

        // 邮件帐号选项-----------
        PIMSeparator accountTitle = new PIMSeparator(OptionDlgConst.OPTION_MAIL_ACCOUNT_OPTION);
        accountTitle.setBounds(CustOpts.HOR_GAP, mailOptionButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(accountTitle);

        // 控制PIM何时接收和发送邮件
        JLabel controlLabel = new JLabel(OptionDlgConst.OPTION_CONTROL_SEND_RECEIVE);
        controlLabel.setBounds(4 * CustOpts.HOR_GAP, accountTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                width - 6 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        add(controlLabel);
        // 邮件打开方式
        boolean openType = CustOpts.custOps.getOpenType();
        openTypeBox = new JCheckBox(OptionDlgConst.OPTION_OPENTYPE, openType);
        openTypeBox.setMnemonic('D');
        openTypeBox.setBounds(4 * CustOpts.HOR_GAP, controlLabel.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, width
                - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(openTypeBox);

        // 连接后立即发送邮件 ##############
        boolean isImmediateSendMail = CustOpts.custOps.getImmediateSendMail();
        sendMailBox = new JCheckBox(OptionDlgConst.OPTION_CONNECT_SEND, isImmediateSendMail);
        sendMailBox.setMnemonic('S');
        sendMailBox.setBounds(4 * CustOpts.HOR_GAP, openTypeBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width
                - 6 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        add(sendMailBox);

        // 每隔 ##############
        boolean isCheck = CustOpts.custOps.getCheckMail();
        spanBox = new JCheckBox(OptionDlgConst.OPTION_PER, isCheck);
        spanBox.setMnemonic('N');
        int spanBoxWidth = CustOpts.HOR_GAP + CASDialogKit.getMaxWidth(new JComponent[] { spanBox });
        spanBox.setBounds(4 * CustOpts.HOR_GAP, sendMailBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                spanBoxWidth, CustOpts.BTN_HEIGHT);
        add(spanBox);
        // 时间间隔 ############
        int spanValue = CustOpts.custOps.getSpantime();
        spinner = new JSpinner();
        spinner.setValue(new Integer(spanValue));// ,1,50);
        // spinner.setMaximumValue(60);
        // spinner.setMinimumValue(1);
        spinner.setBounds(spanBox.getX() + spanBoxWidth, spanBox.getY(), spinner.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        add(spinner);
        if (isCheck == true)
            spinner.setEnabled(true);
        else
            spinner.setEnabled(false);

        // 分钟检查新邮件
        JLabel checkNewMailLabel = new JLabel(OptionDlgConst.OPTION_CHECK_NEWMAIL);
        checkNewMailLabel.setBounds(spinner.getX() + spinner.getWidth() + CustOpts.HOR_GAP, spinner.getY(),
                checkNewMailLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        add(checkNewMailLabel);
        // 邮件格式-----------
        PIMSeparator formatTitle = new PIMSeparator(OptionDlgConst.OPTION_MAIL_FORMAT);
        formatTitle.setBounds(CustOpts.HOR_GAP, checkNewMailLabel.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(formatTitle);

        // 以该邮件格式发送
        JLabel formatLabel = new JLabel(OptionDlgConst.OPTION_SEND_FORMAT, 'O');
        int formatLabelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { formatLabel });
        // 邮件格式 ############
        formatBox = new JComboBox(data);
        formatLabel.setLabelFor(formatBox);
        formatLabel.setBounds(4 * CustOpts.HOR_GAP, formatTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                formatLabelWidth, CustOpts.LBL_HEIGHT);
        formatBox.setEnabled(false);
        formatBox.setBounds(formatLabel.getX() + formatLabel.getWidth(), formatLabel.getY(), width - 6
                * CustOpts.HOR_GAP - formatLabelWidth, CustOpts.BTN_HEIGHT);
        add(formatLabel);
        add(formatBox);
        int formatIndex = CustOpts.custOps.getMailFormat();
        formatBox.setSelectedIndex(formatIndex);

        // 设置
        JButton configButton = new JButton(OptionDlgConst.OPTION_CONFIG);
        configButton.setEnabled(false);
        int configButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { configButton });
        configButton.setBounds(width - configButtonWidth - 2 * CustOpts.HOR_GAP, formatLabel.getY() + 3
                * CustOpts.BTN_HEIGHT, configButtonWidth, CustOpts.BTN_HEIGHT);
        add(configButton);
    }

    /**
     * 取得邮件面板的信息
     */
    void setMailInfo() {
        // 重新设置邮件打开方式
        CustOpts.custOps.setOpenType(openTypeBox.isSelected());
        // 重新设置立即发送选项复选框
        CustOpts.custOps.setImmediateSendMail(sendMailBox.isSelected());
        // 重新设置是时间间隔选项
        CustOpts.custOps.setCheckMail(spanBox.isSelected());
        // 重新设置时间间隔
        CustOpts.custOps.setSpantime((Integer) spinner.getValue());
        // 重新设置邮件格式
        CustOpts.custOps.setMailFormat(formatBox.getSelectedIndex());
    }

    /**
     *
     */
    private void addListeners() {
        mailOptionButton.addActionListener(this);
        spanBox.addActionListener(this);
    }

    // ***********************
    // 动 作 监 听 器
    // ***********************
    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        Object objSource = e.getSource();
        if (objSource == mailOptionButton) {
            // 电子邮件选项对话框
            if (emailOptionDialog == null) {
                emailOptionDialog = new EmailOptionDialog(parentDialog, true);
                emailOptionDialog.show();
            } else {
                emailOptionDialog.setVisible(true);
            }
        } else if (objSource == spanBox) {
            if (spanBox.isSelected()) {
                spinner.setEnabled(true);
            } else {
                spinner.setEnabled(false);
            }
        }
    }

    private JDialog parentDialog;
    private Object[] data = { "HTML", "TEXT" };
    private int width;
    private int height;
    private JButton mailOptionButton;
    private JCheckBox openTypeBox;
    private JCheckBox sendMailBox;
    private JCheckBox spanBox;
    private JSpinner spinner;
    private JComboBox formatBox;
    private EmailOptionDialog emailOptionDialog;

}
