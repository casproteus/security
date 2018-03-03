package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.OptionDlgConst;

class TimeZoonDialog extends JDialog implements ActionListener {

    /**
     * 时区对话盒构建器
     * 
     * @param prmDialog
     *            : 父对话盒
     * @param prmModel
     *            : 是否模态
     * @param prmCustomOptions
     *            : 存取数据
     */
    public TimeZoonDialog(JDialog prmDialog, boolean prmModel) {
        super(prmDialog, prmModel);
        // 时区
        setTitle(OptionDlgConst.OPTION_TIMEZONE);

        Dimension size =
                new Dimension(400, displayPanelHeight + currsorZonePanelHeight + CustOpts.BTN_HEIGHT + 2
                        * CustOpts.VER_GAP);
        width = size.width;
        height = size.height;

        initDialog();
        setStatic();
        addComponentListener();

        setBounds((CustOpts.SCRWIDTH - width) / 2, (CustOpts.SCRHEIGHT - height) / 2, width, height); // 对话框的默认尺寸。

    }

    /**
     * 初始化对话盒
     */
    private void initDialog() {
        JPanel currsorZonePanel = getCurrsorZonePanel(currsorZonePanelHeight);

        displayTitleCheckBox =
                new JCheckBox(OptionDlgConst.OPTION_DISLAY_APPEND_ZONE, CustOpts.custOps.getDisplayAppendZone());
        displayTitleCheckBox.setMnemonic('S');
        int displayTitleCheckBoxWidth = CASDialogKit.getMaxWidth(new JComponent[] { displayTitleCheckBox });

        JPanel displayAppendZonePanel = getDisplayAppendZonePanel(displayPanelHeight);
        // 显示附加时区

        getContentPane().add(currsorZonePanel);
        // displayTitleCheckBox.added(panel,CustOpts.HOR_GAP,currsorZonePanel.getHeight()+CustOpts.VER_GAP -
        // CustOpts.SEP_HEIGHT,displayTitleCheckBoxWidth);
        displayAppendZonePanel.setBounds(0, currsorZonePanel.getHeight() + CustOpts.VER_GAP,
                displayAppendZonePanel.getPreferredSize().width, displayAppendZonePanel.getPreferredSize().height);
        getContentPane().add(displayAppendZonePanel);
        displayTitleCheckBox.setBounds(CustOpts.HOR_GAP, displayAppendZonePanel.getY(), displayTitleCheckBoxWidth,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(displayTitleCheckBox);
        // 交换时区
        changeZoneButton = new JButton(OptionDlgConst.OPTION_CHANGE_ZONE);
        changeZoneButton.setMnemonic('W');
        int changeButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { changeZoneButton });
        changeZoneButton.setBounds(0, height - CustOpts.BTN_HEIGHT, changeButtonWidth, CustOpts.BTN_HEIGHT);
        getContentPane().add(changeZoneButton);

        // 确定
        ok = new JButton(DlgConst.OK);
        ok.setBounds(width - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, height - CustOpts.BTN_HEIGHT,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        // 取消
        cancel = new JButton(DlgConst.CANCEL);
        cancel.setBounds(width - CustOpts.BTN_WIDTH, height - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);
        getContentPane().add(cancel);
    }

    /**
     * 当前时区
     */
    private JPanel getCurrsorZonePanel(
            int prmHeight) {
        JPanel currosrZonePanel = new JPanel();
        // OptionDlgConst.OPTION_CURRENT_ZONE,
        currosrZonePanel.setSize(width, prmHeight);
        // 标签
        JLabel label = new JLabel(OptionDlgConst.OPTION_LABEL, 'L');
        // 标签
        JLabel zoneLabel = new JLabel(OptionDlgConst.OPTION_ZONE, 'Z');
        // 标签
        JLabel currsorTimeLabel = new JLabel(OptionDlgConst.OPTION_CURRENT_TIME, 'T');
        labelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { label, zoneLabel, currsorTimeLabel });
        labelField = new JTextField(CASUtility.EMPTYSTR);
        label.setLabelFor(labelField);
        label.setBounds(2 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, labelWidth, CustOpts.LBL_HEIGHT);
        labelField.setBounds(label.getX() + label.getWidth(), label.getY(), 150, CustOpts.BTN_HEIGHT);
        currosrZonePanel.add(label);
        currosrZonePanel.add(labelField);
        labelField.setText(CustOpts.custOps.getCurrsorLabel());
        zoneCombo = new JComboBox(OptionDlgConst.zoneData);// ,width - labelWidth - 4*CustOpts.HOR_GAP);
        zoneLabel.setLabelFor(zoneCombo);
        zoneLabel.setBounds(2 * CustOpts.HOR_GAP, labelField.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        zoneCombo.setBounds(zoneLabel.getX() + zoneLabel.getWidth(), zoneLabel.getY(),
                zoneCombo.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        currosrZonePanel.add(zoneLabel);
        currosrZonePanel.add(zoneCombo);
        zoneCombo.setSelectedIndex(CustOpts.custOps.getCurrsorZone());

        // 为夏令时调整时间
        timeCheckBox = new JCheckBox(OptionDlgConst.OPTION_ADJUST_TIME, CustOpts.custOps.getCurrsorAdjust());
        timeCheckBox.setMnemonic('D');
        timeCheckBox.setBounds(zoneCombo.getX(), zoneCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                zoneCombo.getWidth(), CustOpts.BTN_HEIGHT);
        currosrZonePanel.add(timeCheckBox);

        currsorTimeField = new JTextField(CustOpts.custOps.getCurrsorTime());
        currsorTimeLabel.setLabelFor(currsorTimeField);
        currsorTimeLabel.setBounds(2 * CustOpts.HOR_GAP, timeCheckBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        currsorTimeField.setBounds(currsorTimeLabel.getX() + currsorTimeLabel.getWidth(), currsorTimeLabel.getY(),
                zoneCombo.getWidth(), CustOpts.BTN_HEIGHT);
        currosrZonePanel.add(currsorTimeLabel);
        currosrZonePanel.add(currsorTimeField);

        return currosrZonePanel;
    }

    /**
     * 显示附加时区
     */
    private JPanel getDisplayAppendZonePanel(
            int prmHeight) {
        int length = displayTitleCheckBox.getText().length();
        StringBuffer tmpSB = new StringBuffer(length);
        for (int i = 0; i < 2 * length; i++) {
            tmpSB.append(' ');
        }
        JPanel displayPanel = new JPanel();// tmpSB.toString()
        displayPanel.setSize(width, prmHeight);
        // 标签
        JLabel newlabel = new JLabel(OptionDlgConst.OPTION_LABEL_APPEND);
        newlabel.setDisplayedMnemonic('B');
        newLabelField = new JTextField(CASUtility.EMPTYSTR, 150);
        newlabel.setLabelFor(newLabelField);
        newlabel.setBounds(2 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, labelWidth,
                CustOpts.LBL_HEIGHT);
        newLabelField.setBounds(newlabel.getX() + newlabel.getWidth(), newlabel.getY(),
                newLabelField.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        displayPanel.add(newlabel);
        displayPanel.add(newLabelField);
        newLabelField.setText(CustOpts.custOps.getAppendLabel());

        // 时区
        JLabel newZoneLabel = new JLabel(OptionDlgConst.OPTION_ZONE_APPEND);
        newZoneLabel.setDisplayedMnemonic('O');
        newZoneCombo = new JComboBox(OptionDlgConst.zoneData);
        newZoneLabel.setLabelFor(newZoneCombo);
        newZoneLabel.setBounds(2 * CustOpts.HOR_GAP, newLabelField.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        newZoneCombo.setBounds(newZoneLabel.getX() + newZoneLabel.getWidth(), newZoneLabel.getY(), width - labelWidth
                - 4 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        displayPanel.add(newZoneLabel);
        displayPanel.add(newZoneCombo);
        newZoneCombo.setSelectedIndex(CustOpts.custOps.getAppendZone());

        // 为夏令时调整时间
        newTimeCheckBox = new JCheckBox(OptionDlgConst.OPTION_ADJUST_TIEM_APPEND, CustOpts.custOps.getAppendAdjust());
        newTimeCheckBox.setMnemonic('U');
        newTimeCheckBox.setBounds(newZoneCombo.getX(), newZoneCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                newZoneCombo.getWidth(), CustOpts.BTN_HEIGHT);
        displayPanel.add(newTimeCheckBox);
        return displayPanel;
    }

    /**
     * 添加监听器
     */
    private void addComponentListener() {
        changeZoneButton.addActionListener(this);
        displayTitleCheckBox.addActionListener(this);
        ok.addActionListener(this);
    }

    /**
     * 设置改变后的属性
     */
    void setTimeZone() {
        CustOpts.custOps.setCurrsorLabel(labelField.getText());
        CustOpts.custOps.setCurrsorZone(zoneCombo.getSelectedIndex());
        CustOpts.custOps.setCurrsorAdjust(timeCheckBox.isSelected());
        CustOpts.custOps.setCurrsorTime(currsorTimeField.getText());
        CustOpts.custOps.setDisplayAppendZone(displayTitleCheckBox.isSelected());
        CustOpts.custOps.setAppendLabel(newLabelField.getText());
        CustOpts.custOps.setAppendZone(newZoneCombo.getSelectedIndex());
        CustOpts.custOps.setAppendAdjust(newTimeCheckBox.isSelected());
    }

    /**
     * 在点击对话盒右上角的关闭按钮或按下"ESCAPE"键,会使对话盒销毁,此过程会调用 此方法,所以重载此方法,如父容器是对话盒,只是不显示而不销毁.
     */
    protected void realClose() {
        if (getParent() instanceof JDialog) {
            setVisible(false);
        } else {
            dispose();
        }
    }

    /**
     * 设置组件状态
     */
    private void setStatic() {
        if (displayTitleCheckBox.isSelected()) {
            newLabelField.setEnabled(true);
            newZoneCombo.setEnabled(true);
            newTimeCheckBox.setEnabled(true);
            changeZoneButton.setEnabled(true);
        } else {
            newLabelField.setEnabled(false);
            newZoneCombo.setEnabled(false);
            newTimeCheckBox.setEnabled(false);
            changeZoneButton.setEnabled(false);
        }
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        Object source = e.getSource();
        // 改变时区按钮
        if (source == changeZoneButton) {
            int zoneindex = zoneCombo.getSelectedIndex();
            int newZoneindex = newZoneCombo.getSelectedIndex();
            String labelString = labelField.getText();
            String newLabelString = newLabelField.getText();
            zoneCombo.setSelectedIndex(newZoneindex);
            newZoneCombo.setSelectedIndex(zoneindex);
            labelField.setText(newLabelString);
            newLabelField.setText(labelString);
        }
        // 显示附加时区复选框
        if (source == displayTitleCheckBox) {
            setStatic();
        }
        // 确定
        if (source == ok) {
            setTimeZone();
            setVisible(false);
        }

    }

    private int width;
    private int height;
    private int displayPanelHeight = CustOpts.LBL_HEIGHT + 3 * CustOpts.BTN_HEIGHT + 4 * CustOpts.HOR_GAP
            + CustOpts.SEP_HEIGHT;
    private int currsorZonePanelHeight = CustOpts.LBL_HEIGHT + 4 * CustOpts.BTN_HEIGHT + 5 * CustOpts.VER_GAP
            + CustOpts.SEP_HEIGHT;
    private int labelWidth;
    private JButton changeZoneButton;
    private JButton ok;
    private JButton cancel;
    private JComboBox zoneCombo;
    private JComboBox newZoneCombo;
    private JCheckBox newTimeCheckBox;
    private JCheckBox displayTitleCheckBox;
    private JTextField labelField;
    private JCheckBox timeCheckBox;
    private JTextField currsorTimeField;
    private JTextField newLabelField;

}
