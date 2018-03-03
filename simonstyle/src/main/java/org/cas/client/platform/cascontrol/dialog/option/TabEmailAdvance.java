package org.cas.client.platform.cascontrol.dialog.option;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.MailOptionConstant;
import org.cas.client.resource.international.OptionDlgConst;

class TabEmailAdvance extends JPanel {

    /**
     * 高级面板
     * 
     * @param prmDialog
     *            父窗体
     * @param prmCustomOptions用户自定义
     * @param prmWidth
     *            宽度
     * @param prmHeight
     *            高度
     */
    public TabEmailAdvance(JDialog prmDialog, int prmWidth, int prmHeight) {
        width = prmWidth;
        height = prmHeight;
        dialog = prmDialog;
        initComponent();
    }

    /**
     * 初始化高级面板组件
     */
    private void initComponent() {
        // 保存邮件-------------
        PIMSeparator saveTitle = new PIMSeparator(OptionDlgConst.OPTION_SAVE_EMAIL);
        saveTitle.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(saveTitle);

        // 未发送的邮件保存位置
        JLabel notSendLabel = new JLabel(OptionDlgConst.OPTION_NO_SEND_MAIL_ADDRESS, 'U');
        // 已发送的邮件保存位置
        JLabel haveSendLabel = new JLabel(OptionDlgConst.OPTION_HAVE_SEND_MAIL_ADDRESS, 'S');
        // 设置重要性
        JLabel importanceLabel = new JLabel(OptionDlgConst.OPTION_CONFIG_IMPORTANCE, 'I');
        // 设置敏感度
        JLabel tendemessLabel = new JLabel(OptionDlgConst.OPTION_CONFIG_TENDMESS, 'T');
        int labelWidth =
                10 + CASDialogKit.getMaxWidth(new JComponent[] { notSendLabel, haveSendLabel, importanceLabel,
                        tendemessLabel });

        //
        notSendCombo = new JComboBox(OptionDlgConst.SEND_DATE);
        notSendLabel.setLabelFor(notSendCombo);
        notSendLabel.setBounds(4 * CustOpts.HOR_GAP, saveTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        notSendCombo.setBounds(notSendLabel.getX() + notSendLabel.getWidth(), notSendLabel.getY(), width - labelWidth
                - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(notSendLabel);
        add(notSendCombo);
        int notSendIndex = CustOpts.custOps.getNotMailSavePos();
        notSendCombo.setSelectedIndex(notSendIndex);

        //
        haveSendCombo = new JComboBox(OptionDlgConst.SEND_DATE);
        haveSendLabel.setLabelFor(haveSendCombo);
        haveSendLabel.setBounds(4 * CustOpts.HOR_GAP, notSendCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        haveSendCombo.setBounds(haveSendLabel.getX() + haveSendLabel.getWidth(), haveSendLabel.getY(), width
                - labelWidth - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(haveSendLabel);
        add(haveSendCombo);
        int sendIndex = CustOpts.custOps.getHaveMailSavePos();
        haveSendCombo.setSelectedIndex(sendIndex);

        // 发送邮件时------------------------------
        PIMSeparator sendTitle = new PIMSeparator(OptionDlgConst.OPTION_WHEN_SEND_MAIL);
        sendTitle.setBounds(CustOpts.HOR_GAP, haveSendCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width - 3
                * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(sendTitle);
        //
        impCombo = new JComboBox(MailOptionConstant.IMPORTANT);
        importanceLabel.setLabelFor(impCombo);
        importanceLabel.setBounds(4 * CustOpts.HOR_GAP, sendTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        impCombo.setBounds(importanceLabel.getX() + importanceLabel.getWidth(), importanceLabel.getY(), width
                - labelWidth - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(importanceLabel);
        add(impCombo);
        int impIndex = CustOpts.custOps.getImportance();
        impCombo.setSelectedIndex(impIndex);

        //
        tendemessCombo = new JComboBox(MailOptionConstant.SENSITIVE);
        tendemessLabel.setLabelFor(tendemessCombo);
        tendemessLabel.setBounds(4 * CustOpts.HOR_GAP, impCombo.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        tendemessCombo.setBounds(tendemessLabel.getX() + tendemessLabel.getWidth(), tendemessLabel.getY(), width
                - labelWidth - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(tendemessLabel);
        add(tendemessCombo);
        int tendeIndex = CustOpts.custOps.getSensitive();
        tendemessCombo.setSelectedIndex(tendeIndex);

        // 自动检查姓名
        boolean isAuto = CustOpts.custOps.getAutoCheckName();
        autoCheckBox = new JCheckBox(OptionDlgConst.OPTION_CHECK_NAME, isAuto);
        autoCheckBox.setMnemonic('N');
        autoCheckBox.setBounds(4 * CustOpts.HOR_GAP, tendemessCombo.getY() + 2 * CustOpts.BTN_HEIGHT, width - 6
                * CustOpts.HOR_GAP, CustOpts.BAR_HEIGHT);
        add(autoCheckBox);

        // 应答时从收件箱删除会议要求
        boolean isDel = CustOpts.custOps.getDelMeetRequestion();
        delCheckBox = new JCheckBox(OptionDlgConst.OPTION_DELETE_MEETINGL, isDel);
        delCheckBox.setMnemonic('M');
        delCheckBox.setBounds(4 * CustOpts.HOR_GAP, autoCheckBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width
                - 6 * CustOpts.HOR_GAP, CustOpts.BAR_HEIGHT);
        add(delCheckBox);

    }

    /**
     * 设置邮件信息
     */
    void setMailAdvanceInfo() {
        CustOpts.custOps.setNotMailSavePos(notSendCombo.getSelectedIndex());
        CustOpts.custOps.setHaveMailSavePos(haveSendCombo.getSelectedIndex());
        CustOpts.custOps.setImportance(impCombo.getSelectedIndex());
        CustOpts.custOps.setSensitive(tendemessCombo.getSelectedIndex());
        CustOpts.custOps.setAutoCheckName(autoCheckBox.isSelected());
        CustOpts.custOps.setDelMeetRequestion(delCheckBox.isSelected());
    }

    private JDialog dialog;
    private int width;
    private int height;
    private JCheckBox autoCheckBox;
    private JCheckBox delCheckBox;
    private JComboBox notSendCombo;
    private JComboBox haveSendCombo;
    private JComboBox tendemessCombo;
    private JComboBox impCombo;
}
