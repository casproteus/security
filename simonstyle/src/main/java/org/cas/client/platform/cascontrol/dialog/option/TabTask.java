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
import org.cas.client.resource.international.OptionDlgConst;

class TabTask extends JPanel // implements ActionListener,EConstants
{
    /**
     * 任务面板构建器
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
    public TabTask(JDialog prmDialog, int prmWidth, int prmHeight) {
        dialog = prmDialog;
        width = prmWidth;
        height = prmHeight;

        initComponents();
    }

    /**
     * 初始化对话盒
     */
    private void initComponents() {
        setBorder(null);
        // 颜色选项------------------------
        PIMSeparator colorOptionTitle = new PIMSeparator(OptionDlgConst.OPTION_COLOR_OPTION);
        colorOptionTitle.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP,
                CustOpts.SEP_HEIGHT);
        add(colorOptionTitle);
        // 过期任务
        JLabel overTimeTaskLabel = new JLabel(OptionDlgConst.OPTION_OVERDUE_TASK, 'O');
        // 已完成任务
        JLabel finishedTaskLabel = new JLabel(OptionDlgConst.OPTION_FINISHED_TASK, 'C');
        // 提醒时间
        JLabel remineTimeLabel = new JLabel(OptionDlgConst.OPTION_REMIND_TIME, 'R');
        int labelWidth =
                10 + CASDialogKit
                        .getMaxWidth(new JComponent[] { overTimeTaskLabel, finishedTaskLabel, remineTimeLabel });

        overTimeTaskCombo = new JComboBox();
        finishedTaskCombo = new JComboBox();
        overTimeTaskLabel.setLabelFor(overTimeTaskCombo);
        overTimeTaskLabel.setBounds(4 * CustOpts.HOR_GAP, colorOptionTitle.getY() + CustOpts.LBL_HEIGHT
                + CustOpts.VER_GAP, labelWidth, CustOpts.LBL_HEIGHT);
        overTimeTaskCombo.setBounds(overTimeTaskLabel.getX() + overTimeTaskLabel.getWidth(), overTimeTaskLabel.getY(),
                200, CustOpts.BTN_HEIGHT);
        add(overTimeTaskLabel);
        add(overTimeTaskCombo);
        // overTimeTaskCombo.setSelectedIndex(CustomOptions.custOps.getOverdueTask());
        finishedTaskLabel.setLabelFor(finishedTaskCombo);
        finishedTaskLabel.setBounds(4 * CustOpts.HOR_GAP, overTimeTaskCombo.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, labelWidth, CustOpts.LBL_HEIGHT);
        finishedTaskCombo.setBounds(finishedTaskLabel.getX() + finishedTaskLabel.getWidth(), finishedTaskLabel.getY(),
                200, CustOpts.BTN_HEIGHT);
        add(finishedTaskLabel);
        add(finishedTaskCombo);
        // finishedTaskCombo.setSelectedIndex(CustomOptions.custOps.getFinishedTask());

        // 高级任务---------------------
        PIMSeparator advandeTaskTitle = new PIMSeparator(OptionDlgConst.OPITON_ADVANCE_TASK);
        advandeTaskTitle.setBounds(CustOpts.HOR_GAP, finishedTaskCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(advandeTaskTitle);
        // 给带有截至日期的任务设置提醒
        remineTastChBox =
                new JCheckBox(OptionDlgConst.OPTION_CONFIG_REMIND_FOR_HAVE_ENDTIME, CustOpts.custOps.getEndDate());
        remineTastChBox.setMnemonic('T');
        remineTastChBox.setBounds(4 * CustOpts.HOR_GAP, advandeTaskTitle.getY() + CustOpts.LBL_HEIGHT
                + CustOpts.VER_GAP, width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(remineTastChBox);

        // 提醒时间
        rimineTimeCombo = new JComboBox(timeData);
        remineTimeLabel.setLabelFor(rimineTimeCombo);
        remineTimeLabel.setBounds(4 * CustOpts.HOR_GAP,
                remineTastChBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, labelWidth, CustOpts.LBL_HEIGHT);
        rimineTimeCombo.setBounds(remineTimeLabel.getX() + remineTimeLabel.getWidth(), remineTimeLabel.getY(), 200,
                CustOpts.BTN_HEIGHT);
        add(remineTimeLabel);
        add(rimineTimeCombo);
        rimineTimeCombo.setSelectedIndex(CustOpts.custOps.getRemindTime());

        taskListChBox = new JCheckBox(OptionDlgConst.OPTION_SAVE_LIST_COPY, CustOpts.custOps.getSaveUpdateCopy());
        taskListChBox.setMnemonic('K');
        assignChBox =
                new JCheckBox(OptionDlgConst.OPTION_ASSGIN_TASK_SEND_REPORT, CustOpts.custOps.getSendStaticReport());
        assignChBox.setMnemonic('S');
        taskListChBox.setBounds(4 * CustOpts.HOR_GAP, rimineTimeCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        assignChBox.setBounds(4 * CustOpts.HOR_GAP, taskListChBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(taskListChBox);
        add(assignChBox);
    }

    /**
     * 设置任务信息
     */
    void setTaskInfo() {
        CustOpts.custOps.setOverdueTask(overTimeTaskCombo.getSelectedIndex());
        CustOpts.custOps.setFinishedTask(finishedTaskCombo.getSelectedIndex());
        CustOpts.custOps.setEndDate(remineTastChBox.isSelected());
        CustOpts.custOps.setRemindTime(rimineTimeCombo.getSelectedIndex());
        CustOpts.custOps.setSaveUpdateCopy(taskListChBox.isSelected());
        CustOpts.custOps.setSendStaticReport(assignChBox.isSelected());
    }

    int width;
    int height;
    JDialog dialog;
    JComboBox overTimeTaskCombo;
    JComboBox finishedTaskCombo;
    JCheckBox remineTastChBox;
    JComboBox rimineTimeCombo;
    JCheckBox taskListChBox;
    JCheckBox assignChBox;

    private Object[] timeData = new Object[] { "0.00", "0.30", "1.00", "1.30", "2.00", "2.30", "3.00", "3.30", "4.00",
            "4.30", "5.00", "5.30", "6.00", "6.30", "7.00", "7.30", "8.00", "8.30", "9.00", "9.30", "10.00", "10.30",
            "11.00", "11.30", "12.00", "12.30", "13.00", "13.30", "14.00", "14.30", "15.00", "15.30", "16.00", "16.30",
            "17.00", "17.30", "18.00", "18.30", "19.00", "19.30", "20.00", "20.30", "21.00", "21.30", "22.00", "22.30",
            "23.00", "23.30", };
}
