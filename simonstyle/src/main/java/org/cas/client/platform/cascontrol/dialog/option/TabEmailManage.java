package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.OptionDlgConst;

class TabEmailManage extends JPanel {

    /**
     * 邮件处理面板
     * 
     * @param prmDialog
     *            父窗体
     * @param prmCustomOptions用户自定义
     * @param prmWidth
     *            宽度
     * @param prmHeight
     *            高度
     */
    public TabEmailManage(JDialog prmDialog, int prmWidth, int prmHeight) {
        dialog = prmDialog;
        width = prmWidth;
        height = prmHeight;
        initComponent();
    }

    /**
     * 初始化对话盒组件
     */
    private void initComponent() {
        // 邮件处理---------------------------------------------------
        PIMSeparator dealTitle = new PIMSeparator(OptionDlgConst.OPTION_EMAIL_DISPOSE);
        dealTitle.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(dealTitle);

        // 在答复和转发时关闭原始邮件
        boolean isClose = CustOpts.custOps.getCloseMail();
        answerChBox = new JCheckBox(OptionDlgConst.OPTION_CLOSE_MAIL, isClose);
        answerChBox.setMnemonic('C');
        answerChBox.setBounds(4 * CustOpts.HOR_GAP, dealTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, width
                - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(answerChBox);

        // 在”已发送的邮件“文件夹中保留副本
        boolean isSave = CustOpts.custOps.getSaveMailCopy();
        saveChbox = new JCheckBox(OptionDlgConst.OPTION_SAVE_COPY, isSave);
        saveChbox.setMnemonic('V');
        saveChbox.setBounds(4 * CustOpts.HOR_GAP, answerChBox.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, width
                - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(saveChbox);

        // 新邮件到达时给出通知
        boolean isRemind = CustOpts.custOps.getRemindMailReceive();
        reminerChBox = new JCheckBox(OptionDlgConst.OPTION_REMIND_MAIL_RECEIVED, isRemind);
        reminerChBox.setMnemonic('N');
        reminerChBox.setBounds(4 * CustOpts.HOR_GAP, saveChbox.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, width
                - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(reminerChBox);

        // 答复和转发-------------------------------------------
        PIMSeparator anTitle = new PIMSeparator(OptionDlgConst.OPTION_ANSWER_RETURN);
        anTitle.setBounds(CustOpts.HOR_GAP, reminerChBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width - 3
                * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(anTitle);

        // 答复邮件时
        JLabel anLabel = new JLabel(OptionDlgConst.OPTION_WHEN_ANSWERED, 'R');
        anLabel.setPreferredSize(new Dimension(width - 6 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT));
        anCombo = new JComboBox(OptionDlgConst.COMBO_DATA);
        anLabel.setLabelFor(anCombo);
        anLabel.setBounds(4 * CustOpts.HOR_GAP, anTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                anLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        anCombo.setBounds(anLabel.getX(), anLabel.getY() + anLabel.getHeight() + CustOpts.VER_GAP, 200,
                CustOpts.BTN_HEIGHT);
        add(anLabel);
        add(anCombo);
        int answerIndex = CustOpts.custOps.getAnswerMail();
        anCombo.setSelectedIndex(answerIndex);

        // 转发邮件时
        JLabel returnLabel = new JLabel(OptionDlgConst.OPTION_WHEN_RETURN);
        returnCombo = new JComboBox(OptionDlgConst.COMBO_DATA);
        returnLabel.setDisplayedMnemonic('F');
        returnLabel.setLabelFor(returnCombo);
        returnLabel.setBounds(4 * CustOpts.HOR_GAP, anCombo.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, width - 6
                * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        returnCombo.setBounds(returnLabel.getX(), returnLabel.getY() + returnLabel.getHeight() + CustOpts.VER_GAP, 200,
                CustOpts.BTN_HEIGHT);
        add(returnLabel);
        add(returnCombo);
        int returnIndex = CustOpts.custOps.getReturnMail();
        returnCombo.setSelectedIndex(returnIndex);

        // 编辑邮件时标记修订
        boolean isEdit = CustOpts.custOps.getSginEditMail();
        editCheckBox = new JCheckBox(OptionDlgConst.OPTION_WHEN_EDITED, isEdit);
        editCheckBox.setMnemonic('M');
        editCheckBox.setBounds(4 * CustOpts.HOR_GAP, returnCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width
                - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(editCheckBox);
    }

    /**
     * 设置处理邮件信息
     */
    void setMailDealInfo() {
        CustOpts.custOps.setCloseMail(answerChBox.isSelected());
        CustOpts.custOps.setSaveMailCopy(saveChbox.isSelected());
        CustOpts.custOps.setRemindMailReceive(reminerChBox.isSelected());
        CustOpts.custOps.setAnswerMail(anCombo.getSelectedIndex());
        CustOpts.custOps.setReturnMail(returnCombo.getSelectedIndex());
        CustOpts.custOps.setSginEditMail(editCheckBox.isSelected());
    }

    private JDialog dialog;
    private int width;
    private int height;
    private JCheckBox answerChBox;
    private JCheckBox saveChbox;
    private JCheckBox reminerChBox;
    private JCheckBox editCheckBox;
    private JComboBox anCombo;
    private JComboBox returnCombo;
}
