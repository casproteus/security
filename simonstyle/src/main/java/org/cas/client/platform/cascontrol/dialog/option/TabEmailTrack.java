package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.group.PIMButtonGroup;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.OptionDlgConst;

class TabEmailTrack extends JPanel implements ActionListener {

    // 构造器
    /**
     * 构建器
     * 
     * @param prmDialog
     *            父窗体
     * @param prmCustomOptions用户自定义
     * @param prmWidth
     *            宽度
     * @param prmHeight
     *            高度
     */
    public TabEmailTrack(JDialog prmDialog, int prmWidth, int prmHeight) {
        dialog = prmDialog;
        width = prmWidth;
        height = prmHeight;
        initComponent();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        // 选项--------------------
        // PIMTitle optionTitle = new PIMTitle(DialogConstant.OPTION_DIALOG,width - 3*CustOpts.HOR_GAP);
        PIMSeparator optionTitle = new PIMSeparator(OptionDlgConst.OPTION_DIALOG);
        optionTitle.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(optionTitle);

        // 用这些选项跟踪收件人收到邮件的日期和时间
        JLabel label = new JLabel(OptionDlgConst.OPTION_STRACK_ADDRESSER);
        label.setBounds(4 * CustOpts.HOR_GAP, optionTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, width - 6
                * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);

        // 在到达时处理回执
        // boolean isDeal = CustomOptions.custOps.getDealReturnReceipt();
        // dealChBox = new JCheckBox(OptionDialogConstant.OPTION_DEAL_RECEIPT,isDeal,'C');
        // dealChBox.added(this,4*CustOpts.HOR_GAP,label.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,width -
        // 6*CustOpts.HOR_GAP);
        // dealChBox.addActionListener(this);

        // 处理后将回执移至
        // boolean isMove = CustomOptions.custOps.getMoveRetrunReceipt();
        // moveChBox = new JCheckBox(OptionDialogConstant.OPTION_DEAL_MOVED,isMove,'M');
        // int moveChBoxWidth = 10+PIMDialogKit.getMaxWidth(new JComponent[]{moveChBox});
        // moveChBox.added(this,4*CustOpts.HOR_GAP,dealChBox.getY()+CustOpts.BTN_HEIGHT+CustOpts.VER_GAP,moveChBoxWidth);
        // com = new JComboBox(OptionDialogConstant.TRACK_DATA,width - 8*CustOpts.HOR_GAP - moveChBoxWidth);
        // com.added(this,moveChBox.getX()+moveChBoxWidth+2*CustOpts.HOR_GAP,moveChBox.getY());
        // int comIndex = CustomOptions.custOps.getReceiptMovePos();
        // com.setSelectedIndex(comIndex);

        // 请在阅读所有邮件后给出”已读“回执的请求
        boolean isApp = CustOpts.custOps.getReadRequest();
        appChBox = new JCheckBox(OptionDlgConst.OPTION_SEND_RECEIPT_QUEST, isApp);
        appChBox.setMnemonic('R');
        // appChBox.added(this,4*CustOpts.HOR_GAP,moveChBox.getY()+CustOpts.BTN_HEIGHT+CustOpts.VER_GAP,width -
        // 6*CustOpts.HOR_GAP);
        appChBox.setBounds(4 * CustOpts.HOR_GAP, label.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width - 6
                * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(appChBox);
        appChBox.addActionListener(this);

        // 处理回执-------------------------------------------------------
        PIMSeparator dealTitel = new PIMSeparator(OptionDlgConst.OPTION_RECEIPT);
        dealTitel.setBounds(CustOpts.HOR_GAP, appChBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width - 3
                * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(dealTitel);

        // 问题发送响应
        boolean isSendRes = CustOpts.custOps.getAlwaysSend();
        sendResponseButton = new JRadioButton(OptionDlgConst.OPTION_SEND_RESPONSE, isSendRes);
        sendResponseButton.setMnemonic('A');
        sendResponseButton.setBounds(4 * CustOpts.HOR_GAP, dealTitel.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                sendResponseButton.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        add(sendResponseButton);

        // 从不发送响应
        boolean isNotSend = CustOpts.custOps.getAlwaysNotSend();
        notSendButton = new JRadioButton(OptionDlgConst.OPTION_NEVER_SEND_RESPONSE, isNotSend);
        notSendButton.setMnemonic('N');
        notSendButton.setBounds(4 * CustOpts.HOR_GAP, sendResponseButton.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, notSendButton.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        add(notSendButton);

        // 发送响应前向我询问
        boolean isQuestion = CustOpts.custOps.getRequestMe();
        questionButton = new JRadioButton(OptionDlgConst.OPTION_REMIND_ME, isQuestion);
        questionButton.setMnemonic('K');
        questionButton.setBounds(4 * CustOpts.HOR_GAP, notSendButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                questionButton.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        add(questionButton);

        //
        PIMButtonGroup buttonGroup = new PIMButtonGroup();
        buttonGroup.add(sendResponseButton);
        buttonGroup.add(notSendButton);
        buttonGroup.add(questionButton);
    }

    /**
     * @called by: EmailOptionDialog
     */
    void setTrackInfo() {
        // CustomOptions.custOps.setDealReturnReceipt(dealChBox.isSelected());
        // CustomOptions.custOps.setMoveRetrunReceipt(moveChBox.isSelected());
        // CustomOptions.custOps.setReceiptMovePos(com.getSelectedIndex());
        CustOpts.custOps.setReadRequest(appChBox.isSelected());
        CustOpts.custOps.setAlwaysSend(sendResponseButton.isSelected());
        CustOpts.custOps.setAlwaysNotSend(notSendButton.isSelected());
        CustOpts.custOps.setRequestMe(questionButton.isSelected());
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        // Object source = e.getSource ();
        // if (source == dealChBox)
        // {
        // if (dealChBox.isSelected())
        // {
        // sendResponseButton.setEnabled(true);
        // notSendButton.setEnabled(true);
        // questionButton.setEnabled(true);
        // }
        // else if (!dealChBox.isSelected() && !appChBox.isSelected())
        // {
        // sendResponseButton.setEnabled(false);
        // notSendButton.setEnabled(false);
        // questionButton.setEnabled(false);
        // }
        // }
        // if (source == appChBox)
        // {
        // if (appChBox.isSelected())
        // {
        // sendResponseButton.setEnabled(true);
        // notSendButton.setEnabled(true);
        // questionButton.setEnabled(true);
        // }
        // else if (!dealChBox.isSelected() && !appChBox.isSelected())
        // {
        // sendResponseButton.setEnabled(false);
        // notSendButton.setEnabled(false);
        // questionButton.setEnabled(false);
        // }
        // }
    }

    private JDialog dialog;
    private int width;
    private int height;
    private JCheckBox appChBox;

    private JRadioButton sendResponseButton;
    private JRadioButton notSendButton;
    private JRadioButton questionButton;
}
