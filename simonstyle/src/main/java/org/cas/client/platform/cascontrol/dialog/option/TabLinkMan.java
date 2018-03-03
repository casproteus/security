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

class TabLinkMan extends JPanel {

    /**
     * 联系人面板构建器
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
    public TabLinkMan(JDialog prmDialog, int prmWidth, int prmHeight) {
        dialog = prmDialog;
        width = prmWidth;
        height = prmHeight;

        initComponents();
    }

    /**
     * 初始化面板组件
     */
    private void initComponents() {
        setBorder(null);
        // 新联系人的姓名和档案选项
        PIMSeparator newContactsTitle = new PIMSeparator(OptionDlgConst.OPTION_NEW_CONTACT_OPTION);
        newContactsTitle.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP,
                CustOpts.SEP_HEIGHT);
        add(newContactsTitle);
        newContactsTitle.setPreferredSize(new Dimension(width - 6 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT));

        // //选择PIM使用新名称的顺序
        // JLabel tmpChoosePIMLabel = new JLabel(OptionDialogConstant.OPTION_SELECTED_GRADATION);
        // tmpChoosePIMLabel.added(this,4 * CustOpts.HOR_GAP,newContactsTitle.getY() + CustOpts.LBL_HEIGHT + 2 *
        // CustOpts.VER_GAP);
        // //默认的全名顺序
        // 所有defaultNameCombo相关的组件注释掉,因为按规格我们没有全名字段 JLabel tmpDefaultNameLabel = new
        // JLabel(OptionDialogConstant.OPTION_DEFAULT_ALLNAME_GRADATION,'N');
        // 默认的表示为顺序
        JLabel defaultDisplayLabel = new JLabel(OptionDlgConst.OPTION_DEFAULT_DIAPLAY_GRADATION, 'X');
        int labelWidth = 10 + defaultDisplayLabel.getPreferredSize().width;// PIMDialogKit.getMaxWidth(new JComponent[]
        // {
        // tmpDefaultNameLabel,defaultDisplayLabel
        // });
        // defaultNameCombo = new JComboBox(OptionDialogConstant.DEFAULT_DATA,200);
        //
        // defaultNameCombo.added(this, 4 * CustOpts.HOR_GAP,
        // tmpChoosePIMLabel.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,
        // tmpDefaultNameLabel, labelWidth, dialog);
        //
        // defaultNameCombo.setSelectedIndex(CustomOptions.custOps.getAllNameOrder());
        //
        // //选择存档联系人的默认设置
        // JLabel tmpChooseSaveLabel = new JLabel(OptionDialogConstant.OPTION_SAVE_ATTACH_DEFAULT_CONFIG);
        // tmpChooseSaveLabel.setPreferredSize(new Dimension(width - 6*CustOpts.HOR_GAP,CustOpts.LBL_HEIGHT));
        //
        // tmpChooseSaveLabel.added(this, 4 * CustOpts.HOR_GAP, defaultNameCombo.getY() + CustOpts.BTN_HEIGHT + 2 *
        // CustOpts.VER_GAP);
        defaultDisplayCombo = new JComboBox(OptionDlgConst.DEFAULT_DATA);
        defaultDisplayLabel.setLabelFor(defaultDisplayCombo);
        defaultDisplayLabel.setBounds(4 * CustOpts.HOR_GAP, newContactsTitle.getY() + CustOpts.LBL_HEIGHT + 2
                * CustOpts.VER_GAP, labelWidth, CustOpts.LBL_HEIGHT);
        defaultDisplayCombo.setBounds(defaultDisplayLabel.getX() + defaultDisplayLabel.getWidth(),
                defaultDisplayLabel.getY(), 200, CustOpts.BTN_HEIGHT);
        add(defaultDisplayLabel);
        add(defaultDisplayCombo);

        defaultDisplayCombo.setSelectedIndex(CustOpts.custOps.getDisplayAsOrder());

        // 自动加进要答复的人
        autoAddAddresser = new JCheckBox(OptionDlgConst.OPTION_AUTO_ADD_ANSWER, CustOpts.custOps.getAutoAddToAnswer());
        autoAddAddresser.setMnemonic('U');
        // 发送邮件时作为CARD发送
        sendMailAsCard = new JCheckBox(OptionDlgConst.OPTION_SEND_MAIL_AS_CARD, CustOpts.custOps.getSendCard());
        sendMailAsCard.setMnemonic('C');
        autoAddAddresser.setBounds(4 * CustOpts.HOR_GAP, defaultDisplayCombo.getY() + CustOpts.BTN_HEIGHT + 2
                * CustOpts.VER_GAP, width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(autoAddAddresser);
        sendMailAsCard.setBounds(4 * CustOpts.HOR_GAP,
                autoAddAddresser.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width - 6 * CustOpts.HOR_GAP,
                CustOpts.BTN_HEIGHT);
        add(sendMailAsCard);
    }

    /**
     * 设置联系人信息
     */
    void setContactsInfo() {
        // CustomOptions.custOps.setAllNameOrder(defaultNameCombo.getSelectedIndex());
        CustOpts.custOps.setDisplayAsOrder(defaultDisplayCombo.getSelectedIndex());
        CustOpts.custOps.setAutoAddToAnswer(autoAddAddresser.isSelected());
        CustOpts.custOps.setSendCard(sendMailAsCard.isSelected());
    }

    private int width;
    private int height;
    private JDialog dialog;
    // private JComboBox defaultNameCombo;
    private JComboBox defaultDisplayCombo;
    private JCheckBox autoAddAddresser;
    private JCheckBox sendMailAsCard;
}
