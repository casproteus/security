package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.textfield.LimitedIntTextField;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.OptionDlgConst;

class TabOrther extends JPanel implements ItemListener, ActionListener {
    /**
     * 其他面板构建器
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
    public TabOrther(JDialog prmDialog, int prmWidth, int prmHeight) {
        dialog = prmDialog;
        width = prmWidth;
        height = prmHeight;

        initComponents();
        setStatic();
    }

    /**
     * 初始化对话盒
     */
    private void initComponents() {
        setBorder(null);
        // 常规--------------
        PIMSeparator normalTitle = new PIMSeparator(OptionDlgConst.OPTION_NORMAL);
        normalTitle.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(normalTitle);

        // 推出时清空[已删除邮件]文件夹
        outOfficeChBox = new JCheckBox(OptionDlgConst.OPTION_EMPTY_DELETE_ITEM, CustOpts.custOps.getClearHaveDelMail());
        outOfficeChBox.setMnemonic('E');
        outOfficeChBox.setBounds(4 * CustOpts.HOR_GAP, normalTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(outOfficeChBox);

        // 启动时定位到
        JLabel startGoToLabel = new JLabel(OptionDlgConst.OPTION_START_GOTO);
        startGoToCombo = new JComboBox(OptionDlgConst.GOTO_DATA);
        startGoToLabel.setDisplayedMnemonic('S');
        startGoToLabel.setLabelFor(startGoToCombo);
        int startGoToLabelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { startGoToLabel });
        startGoToLabel.setBounds(4 * CustOpts.HOR_GAP, outOfficeChBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                startGoToLabelWidth, CustOpts.LBL_HEIGHT);
        startGoToCombo.setBounds(startGoToLabel.getX() + startGoToLabel.getWidth(), startGoToLabel.getY(), 200,
                CustOpts.BTN_HEIGHT);
        this.add(startGoToLabel);
        this.add(startGoToCombo);
        startGoToCombo.setSelectedIndex(CustOpts.custOps.getGotoPos());

        // 永久删除项目前提出警告
        foreverDelChBox = new JCheckBox(OptionDlgConst.OPTION_WARN_DELFOREVER, CustOpts.custOps.getDelWarning());
        foreverDelChBox.setMnemonic('B');
        foreverDelChBox.setBounds(4 * CustOpts.HOR_GAP, startGoToCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(foreverDelChBox);

        // 存档----------------------------
        PIMSeparator saveTitle = new PIMSeparator(OptionDlgConst.OPTION_SAVE_ATTACH);
        saveTitle.setBounds(4 * CustOpts.HOR_GAP, foreverDelChBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(saveTitle);

        JLabel allSubLabel = new JLabel(OptionDlgConst.OPTION_ALL_PATH);
        allSubLabel.setBounds(4 * CustOpts.HOR_GAP, saveTitle.getY() + CustOpts.LBL_HEIGHT, width - 6
                * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        add(allSubLabel);
        JLabel savePathLabel = new JLabel(OptionDlgConst.OPTION_DEFAULT_PATH);
        pathField = new JTextField(CustOpts.custOps.getSavePath());
        savePathLabel.setDisplayedMnemonic('F');
        savePathLabel.setLabelFor(pathField);
        savePathLabel.setBounds(4 * CustOpts.HOR_GAP, allSubLabel.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                width - 6 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        pathField.setBounds(savePathLabel.getX(), savePathLabel.getY() + savePathLabel.getHeight() + CustOpts.VER_GAP,
                300, CustOpts.BTN_HEIGHT);
        add(savePathLabel);
        add(pathField);

        browseButton = new JButton(OptionDlgConst.OPTION_BROUNS);
        browseButton.setMnemonic('W');
        int browerButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { browseButton });
        browseButton.setBounds(pathField.getX() + pathField.getWidth() + 2 * CustOpts.HOR_GAP, pathField.getY(),
                browerButtonWidth, CustOpts.BTN_HEIGHT);
        add(browseButton);
        browseButton.addActionListener(this);

        // 预览窗格-----------------
        PIMSeparator previewFrameTitle = new PIMSeparator(OptionDlgConst.OPTION_BROUNS_FRAME);
        previewFrameTitle.setBounds(CustOpts.HOR_GAP, browseButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(previewFrameTitle);

        // 在预览窗口中将邮件标记[已读]
        isSginHaveReadChBox = new JCheckBox(OptionDlgConst.OPTION_SGIN_READED, CustOpts.custOps.getSginHaveRead());
        isSginHaveReadChBox.setMnemonic('M');
        isSginHaveReadChBox.setBounds(4 * CustOpts.HOR_GAP, previewFrameTitle.getY() + CustOpts.LBL_HEIGHT
                + CustOpts.VER_GAP, width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(isSginHaveReadChBox);

        //
        JLabel waitLabel = new JLabel(OptionDlgConst.OPTION_WAITING);
        int waitLabelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { waitLabel });
        waitLabel.setBounds(4 * CustOpts.HOR_GAP, isSginHaveReadChBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                waitLabelWidth, CustOpts.LBL_HEIGHT);
        add(waitLabel);
        secField = new LimitedIntTextField(8);
        secField.setText(Integer.toString(CustOpts.custOps.getWaitTime()));
        secField.setBounds(waitLabel.getX() + waitLabelWidth, waitLabel.getY(), secField.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        add(secField);

        JLabel contLabel = new JLabel(OptionDlgConst.OPTION_AFTER_READED);
        contLabel.setBounds(secField.getX() + secField.getWidth() + CustOpts.HOR_GAP, secField.getY(),
                width - secField.getX() - secField.getWidth() - 3 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        add(contLabel);

        chooseChBox =
                new JCheckBox(OptionDlgConst.OPTION_SELECTED_READED, CustOpts.custOps.getSginHaveReadWhenSelected());
        chooseChBox.setMnemonic('R');
        chooseChBox.setBounds(4 * CustOpts.HOR_GAP, contLabel.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, width
                - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(chooseChBox);
        isSginHaveReadChBox.addItemListener(this);
        chooseChBox.addItemListener(this);
    }

    /**
     * 设置其他选择信息
     */
    void setOtherInfor() {
        CustOpts.custOps.setClearHaveDelMail(outOfficeChBox.isSelected());
        CustOpts.custOps.setGotoPos(startGoToCombo.getSelectedIndex());
        CustOpts.custOps.setDelWarning(foreverDelChBox.isSelected());
        CustOpts.custOps.setSavePath(pathField.getText());
        CustOpts.custOps.setSginHaveRead(isSginHaveReadChBox.isSelected());
        try {
            int second = Integer.parseInt(secField.getText());
            CustOpts.custOps.setWaitTime(second);
        } catch (NumberFormatException e) {
        }
        CustOpts.custOps.setSginHaveReadWhenSelected(chooseChBox.isSelected());
    }

    /**
     * 设置状态
     */
    private void setStatic() {
        if (isSginHaveReadChBox.isSelected()) {
            secField.setEnabled(true);
            chooseChBox.setSelected(false);
        } else {
            secField.setEnabled(false);
            chooseChBox.setSelected(true);
        }

    }

    /**
     * 监听器方法
     * 
     * @param e
     *            状态变化事件
     */
    public void itemStateChanged(
            ItemEvent e) {
        Object source = e.getSource();
        if (source == isSginHaveReadChBox) {
            // 控制逻辑上与outlook不一致
            // if(isSginHaveReadChBox.isSelected())
            // {
            // secField.setEnabled(true);
            // chooseChBox.setSelected(false);
            // }
            // else
            // {
            // secField.setEnabled(false);
            // chooseChBox.setSelected(true);
            // }
            if (e.getStateChange() == ItemEvent.SELECTED) {
                secField.setEnabled(true);
                chooseChBox.setSelected(false);
            } else {
                secField.setEnabled(false);
            }
        } else if (source == chooseChBox) {
            // if(chooseChBox.isSelected())
            // {
            // secField.setEnabled(false);
            // isSginHaveReadChBox.setSelected(false);
            // }
            // else
            // {
            // isSginHaveReadChBox.setSelected(true);
            // }
            if (e.getStateChange() == ItemEvent.SELECTED) {
                secField.setEnabled(false);
                isSginHaveReadChBox.setSelected(false);
            }
        }
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Object source = e.getSource();
        if (source == browseButton) {
            JFileChooser tmpFileChooser = new JFileChooser();
            tmpFileChooser.setDialogType(1);
            tmpFileChooser.setAcceptAllFileFilterUsed(false);
            if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
                return;

            String savePath = tmpFileChooser.getCurrentDirectory().getPath(); // 设置只显示保存的文件夹记录
            pathField.setText(savePath);
        }
    }

    private int width;

    private int height;

    private JDialog dialog;

    private JCheckBox outOfficeChBox;

    private JComboBox startGoToCombo;

    private JCheckBox foreverDelChBox;

    private JTextField pathField;

    private JCheckBox isSginHaveReadChBox;

    private LimitedIntTextField secField;

    private JCheckBox chooseChBox;

    private JButton browseButton;
}
