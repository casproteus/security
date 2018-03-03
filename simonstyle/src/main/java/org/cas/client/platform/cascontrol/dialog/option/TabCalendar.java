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
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.resource.international.OptionDlgConst;

public class TabCalendar extends JPanel implements ActionListener, ItemListener {

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
    public TabCalendar(JDialog prmDialog, int prmWidth, int prmHeight) {
        setBorder(null);
        dialog = prmDialog;
        width = prmWidth;
        height = prmHeight;

        initComponents();
        addListeners();
    }

    private void initComponents() {
        // 工作日
        workDayTitle = new PIMSeparator(OptionDlgConst.OPTION_WORKDAY);
        workDayTitle.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(workDayTitle);

        // 星期日
        sunday = new JCheckBox();
        sunday.setText(OptionDlgConst.WEEK[0]);
        sunday.setSelected(CustOpts.custOps.getSunday());
        // 星期一
        monday = new JCheckBox();
        monday.setText(OptionDlgConst.WEEK[1]);
        monday.setSelected(CustOpts.custOps.getMonday());
        // 星期二
        tuesday = new JCheckBox();
        tuesday.setText(OptionDlgConst.WEEK[2]);
        tuesday.setSelected(CustOpts.custOps.getTuesday());
        // 星期三
        wednsday = new JCheckBox();
        wednsday.setText(OptionDlgConst.WEEK[3]);
        wednsday.setSelected(CustOpts.custOps.getWendsday());
        // 星期四
        thursday = new JCheckBox();
        thursday.setText(OptionDlgConst.WEEK[4]);
        thursday.setSelected(CustOpts.custOps.getThursday());
        // 星期五
        firday = new JCheckBox();
        firday.setText(OptionDlgConst.WEEK[5]);
        firday.setSelected(CustOpts.custOps.getFirday());
        // 星期六
        sateday = new JCheckBox();
        sateday.setText(OptionDlgConst.WEEK[6]);
        sateday.setSelected(CustOpts.custOps.getSateday());

        weekWidth =
                5 + CASDialogKit.getMaxWidth(new JComponent[] { sunday, monday, tuesday, wednsday, thursday, firday,
                        sateday });
        sunday.setBounds(4 * CustOpts.HOR_GAP, workDayTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP, weekWidth,
                CustOpts.BTN_HEIGHT);
        monday.setBounds(sunday.getX() + weekWidth, sunday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
        tuesday.setBounds(monday.getX() + weekWidth, monday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
        wednsday.setBounds(tuesday.getX() + weekWidth, tuesday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
        thursday.setBounds(wednsday.getX() + weekWidth, wednsday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
        firday.setBounds(thursday.getX() + weekWidth, thursday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
        sateday.setBounds(firday.getX() + weekWidth, firday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
        add(sunday);
        add(monday);
        add(tuesday);
        add(wednsday);
        add(thursday);
        add(firday);
        add(sateday);
        // 一周的第一天
        JLabel weekFirstDayLabel = new JLabel(OptionDlgConst.OPTION_FIRST_DAY_OF_WEEK);
        weekFirstDayLabel.setDisplayedMnemonic('E');
        // 一年的第一周
        JLabel yearFirstWeekyLabel = new JLabel(OptionDlgConst.OPTION_FIRST_WEEK_OF_YEAR);
        yearFirstWeekyLabel.setDisplayedMnemonic('Y');
        // 背景颜色
        JLabel backgroundLabel = new JLabel(OptionDlgConst.OPTION_BACKGROUND);
        backgroundLabel.setDisplayedMnemonic('B');
        int labelWidth =
                10 + CASDialogKit.getMaxWidth(new JComponent[] { weekFirstDayLabel, yearFirstWeekyLabel,
                        backgroundLabel });
        firstDayCombo = new JComboBox(OptionDlgConst.WEEK);
        weekFirstDayLabel.setLabelFor(firstDayCombo);
        weekFirstDayLabel.setBounds(4 * CustOpts.HOR_GAP, sateday.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        firstDayCombo.setBounds(weekFirstDayLabel.getX() + weekFirstDayLabel.getWidth(), weekFirstDayLabel.getY(), 150,
                CustOpts.BTN_HEIGHT);
        add(weekFirstDayLabel);
        add(firstDayCombo);
        firstDayCombo.setSelectedIndex(CustOpts.custOps.getFirstDayWeek());

        JLabel AMLabel = new JLabel(OptionDlgConst.OPTION_AM);
        AMLabel.setBounds(4 * CustOpts.HOR_GAP, firstDayCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                AMLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        add(AMLabel);
        // 开始时间
        JLabel startAMTimeLabel = new JLabel(OptionDlgConst.OPTION_START_AM_TIME);
        startAMTimeLabel.setDisplayedMnemonic('S');
        // 结束时间
        JLabel finishAMTimeLabel = new JLabel(OptionDlgConst.OPTION_END_AM_TIEM);
        finishAMTimeLabel.setDisplayedMnemonic('N');
        int timeLabelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { startAMTimeLabel, finishAMTimeLabel });
        int timeComboWidth = width - firstDayCombo.getWidth() - labelWidth - timeLabelWidth - 8 * CustOpts.HOR_GAP;
        startAMCombo = new JComboBox(timeAM);
        startAMTimeLabel.setLabelFor(startAMCombo);
        startAMTimeLabel.setBounds(AMLabel.getX() + AMLabel.getWidth() + CustOpts.HOR_GAP, AMLabel.getY(),
                timeLabelWidth, CustOpts.LBL_HEIGHT);
        startAMCombo.setBounds(startAMTimeLabel.getX() + startAMTimeLabel.getWidth(), startAMTimeLabel.getY(),
                timeComboWidth, CustOpts.BTN_HEIGHT);
        add(startAMTimeLabel);
        add(startAMCombo);
        startAMCombo.setSelectedIndex(CustOpts.custOps.getStartAMTime());
        finishAMCombo = new JComboBox(timeAM);
        finishAMTimeLabel.setLabelFor(finishAMCombo);
        finishAMTimeLabel.setBounds(startAMTimeLabel.getX(), startAMCombo.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, timeLabelWidth, CustOpts.LBL_HEIGHT);
        finishAMCombo.setBounds(finishAMTimeLabel.getX() + finishAMTimeLabel.getWidth(), finishAMTimeLabel.getY(),
                timeComboWidth, CustOpts.BTN_HEIGHT);
        add(finishAMTimeLabel);
        add(finishAMCombo);
        finishAMCombo.setSelectedIndex(CustOpts.custOps.getEndAMTime());
        tmpFinishAMIndex = finishAMCombo.getSelectedIndex();

        JLabel PMLabel = new JLabel(OptionDlgConst.OPTION_PM);
        PMLabel.setBounds(startAMCombo.getX() + timeComboWidth + 2 * CustOpts.HOR_GAP, AMLabel.getY(),
                PMLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        add(PMLabel);
        // 开始时间
        JLabel startPMTimeLabel = new JLabel(OptionDlgConst.OPTION_START_PM_TIME, 'B');
        // 结束时间
        JLabel finishPMTimeLabel = new JLabel(OptionDlgConst.OPTION_END_PM_TIME, 'F');
        startPMCombo = new JComboBox(timePM);
        startPMTimeLabel.setLabelFor(startPMCombo);
        startPMTimeLabel.setBounds(PMLabel.getX() + PMLabel.getWidth() + CustOpts.HOR_GAP, PMLabel.getY(),
                timeLabelWidth, CustOpts.LBL_HEIGHT);
        startPMCombo.setBounds(startPMTimeLabel.getX() + startPMTimeLabel.getWidth(), startPMTimeLabel.getY(),
                timeComboWidth, CustOpts.BTN_HEIGHT);
        add(startPMTimeLabel);
        add(startPMCombo);
        startPMCombo.setSelectedIndex(CustOpts.custOps.getStartPMTime());
        finishPMCombo = new JComboBox(timePM);
        finishPMTimeLabel.setLabelFor(finishPMCombo);
        finishPMTimeLabel.setBounds(startPMTimeLabel.getX(), startPMCombo.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, timeLabelWidth, CustOpts.LBL_HEIGHT);
        finishPMCombo.setBounds(finishPMTimeLabel.getX() + finishPMTimeLabel.getWidth(), finishPMTimeLabel.getY(),
                timeComboWidth, CustOpts.BTN_HEIGHT);
        add(finishPMTimeLabel);
        add(finishPMCombo);
        finishPMCombo.setSelectedIndex(CustOpts.custOps.getEndPMTime());
        tmpFinishPMIndex = finishPMCombo.getSelectedIndex();

        firstWeekCombo = new JComboBox(OptionDlgConst.YEAR_DATA);
        yearFirstWeekyLabel.setLabelFor(firstWeekCombo);
        yearFirstWeekyLabel.setBounds(4 * CustOpts.HOR_GAP, finishAMCombo.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, labelWidth, CustOpts.LBL_HEIGHT);
        firstWeekCombo.setBounds(yearFirstWeekyLabel.getX() + yearFirstWeekyLabel.getWidth(),
                yearFirstWeekyLabel.getY(), 150, CustOpts.BTN_HEIGHT);
        add(yearFirstWeekyLabel);
        add(firstWeekCombo);
        firstWeekCombo.setSelectedIndex(CustOpts.custOps.getFirstWeekYear());

        // //外观------------------
        // PIMTitle faceTitle = new PIMTitle(OptionDialogConstant.OPTION_GUISE,width - 3*CustOpts.HOR_GAP);
        // faceTitle.added(this,CustOpts.HOR_GAP,finishCombo.getY()+CustOpts.BTN_HEIGHT+CustOpts.VER_GAP);
        // //在日期选择区中显示周数
        // chooseZoonBox = new JCheckBox(OptionDialogConstant.OPTION_DIAPLAY_WEEKS,true,'K');
        // chooseZoonBox.added(this,4*CustOpts.HOR_GAP,faceTitle.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,width -
        // 6*CustOpts.HOR_GAP);
        // chooseZoonBox.setSelected(CustomOptions.custOps.getDisplayWeeks());
        //
        // //背景颜色
        // backCombo = new JComboBox(150);
        // backCombo.added(this,
        // 4*CustOpts.HOR_GAP,
        // chooseZoonBox.getY()+CustOpts.BTN_HEIGHT+CustOpts.VER_GAP,
        // backgroundLabel,
        // labelWidth,
        // dialog);
        // //TODO ~~~___~~~ ========
        // //backCombo.setSelectedIndex(CustomOptions.custOps.getSelectedBackColor());
        //
        // //时区
        // zonnButton = new JButton(OptionDialogConstant.OPTION_ZONE,'Z');
        // int zonnButtonWidth = 10+PIMDialogKit.getMaxWidth(new JComponent[]
        // {
        // zonnButton
        // });
        // zonnButton.added(this,width - zonnButtonWidth - 2*CustOpts.HOR_GAP,backCombo.getY(),zonnButtonWidth,dialog);

        // 处理会议----------------------
        PIMSeparator dealMeetingTitle = new PIMSeparator(OptionDlgConst.OPTION_DEAL_MEETING);
        dealMeetingTitle.setBounds(CustOpts.HOR_GAP, firstWeekCombo.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                width - 3 * CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT);
        add(dealMeetingTitle);

        // 自动接收会议并处理取消通知
        boolean isAutoReceive = CustOpts.custOps.getAutoReceive();
        autoRecCheckBox = new JCheckBox(OptionDlgConst.OPTION_RECE_MEETING, isAutoReceive);
        autoRecCheckBox.setMnemonic('M');
        autoRecCheckBox.setEnabled(false);
        // 自动谢绝冲突的会议要求
        autorefuseCheckBox = new JCheckBox(OptionDlgConst.OPTION_REFUSE_MEETING, CustOpts.custOps.getAutoCollision());
        autorefuseCheckBox.setMnemonic('C');
        // 自动谢绝重复的会议要求
        autoRepeatCheckBox = new JCheckBox(OptionDlgConst.OPTION_REF_RE_MEETING, CustOpts.custOps.getAutoRepeat());
        autoRepeatCheckBox.setMnemonic('A');
        autoRecCheckBox.setBounds(4 * CustOpts.HOR_GAP, dealMeetingTitle.getY() + CustOpts.LBL_HEIGHT
                + CustOpts.VER_GAP, width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        autorefuseCheckBox.setBounds(4 * CustOpts.HOR_GAP, autoRecCheckBox.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        autoRepeatCheckBox.setBounds(4 * CustOpts.HOR_GAP, autorefuseCheckBox.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, width - 6 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        add(autoRecCheckBox);
        add(autorefuseCheckBox);
        add(autoRepeatCheckBox);
        if (isAutoReceive == true) {
            autorefuseCheckBox.setEnabled(true);
            autoRepeatCheckBox.setEnabled(true);
        } else {
            autorefuseCheckBox.setEnabled(false);
            autoRepeatCheckBox.setEnabled(false);
        }
    }

    /**
     * 添加监听器
     */
    private void addListeners() {
        startAMCombo.addActionListener(this);
        finishAMCombo.addActionListener(this);
        startPMCombo.addActionListener(this);
        finishPMCombo.addActionListener(this);
        // zonnButton.addActionListener(this);
        autoRecCheckBox.addActionListener(this);
        firstDayCombo.addItemListener(this);
    }

    /**
     * 设置日历信息
     */
    void setCalendarInfo() {
        CustOpts.custOps.setSunday(sunday.isSelected());
        CustOpts.custOps.setMonday(monday.isSelected());
        CustOpts.custOps.setTuesday(tuesday.isSelected());
        CustOpts.custOps.setWendsday(wednsday.isSelected());
        CustOpts.custOps.setThursday(thursday.isSelected());
        CustOpts.custOps.setFirday(firday.isSelected());
        CustOpts.custOps.setSateDay(sateday.isSelected());
        CustOpts.custOps.setFirstDayWeek(firstDayCombo.getSelectedIndex());
        CustOpts.custOps.setFirstWeekYear(firstWeekCombo.getSelectedIndex());
        CustOpts.custOps.setStartAMTime(startAMCombo.getSelectedIndex());
        CustOpts.custOps.setEndAMTime(finishAMCombo.getSelectedIndex());
        CustOpts.custOps.setStartPMTime(startPMCombo.getSelectedIndex());
        CustOpts.custOps.setEndPMTime(finishPMCombo.getSelectedIndex());
        // CustomOptions.custOps.setDisplayWeeks(chooseZoonBox.isSelected());
        // CustomOptions.custOps.setBackColor(backCombo.getSelectedIndex());
        CustOpts.custOps.setAutoReceive(autoRecCheckBox.isSelected());
        CustOpts.custOps.setAutoCollision(autorefuseCheckBox.isSelected());
        CustOpts.custOps.setAutoRepeat(autoRepeatCheckBox.isSelected());
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        Object objSource = e.getSource();
        if (objSource == zonnButton) {
            if (timeZoonDialog == null) {
                timeZoonDialog = new TimeZoonDialog(dialog, false);
                timeZoonDialog.show();
            } else {
                timeZoonDialog.setVisible(true);
            }
        }
        if (objSource == autoRecCheckBox) {
            if (autoRecCheckBox.isSelected()) {
                autorefuseCheckBox.setEnabled(true);
                autoRepeatCheckBox.setEnabled(true);
            } else {
                autorefuseCheckBox.setEnabled(false);
                autoRepeatCheckBox.setEnabled(false);
            }
        }
        // 开始时间组合框
        if (objSource == startAMCombo) {
            // 取得开始时间的索引
            startAMIndex = startAMCombo.getSelectedIndex();
            // 取得结束时间的索引
            finishAMIndex = finishAMCombo.getSelectedIndex();
            if (startAMIndex > finishAMIndex) {
                finishAMCombo.setSelectedIndex(startAMIndex);
                // 临时存储结束时间的索引
                tmpFinishAMIndex = startAMIndex;
            }
        }
        // 结束时间组合框
        if (objSource == finishAMCombo) {
            // 取得开始时间的索引
            startAMIndex = startAMCombo.getSelectedIndex();
            // 取得结束时间的索引
            finishAMIndex = finishAMCombo.getSelectedIndex();
            if (finishAMIndex < startAMIndex) {
                finishAMCombo.setSelectedIndex(tmpFinishAMIndex);
                // 对不起，您输入的结束时间不能早于开始时间。
                SOptionPane.showErrorDialog(MessageCons.W10712);
            } else {
                tmpFinishAMIndex = finishAMIndex;
            }
        }
        // 开始时间组合框
        if (objSource == startPMCombo) {
            // 取得开始时间的索引
            startPMIndex = startPMCombo.getSelectedIndex();
            // 取得结束时间的索引
            finishPMIndex = finishPMCombo.getSelectedIndex();
            if (startPMIndex > finishPMIndex) {
                finishPMCombo.setSelectedIndex(startPMIndex);
                // 临时存储结束时间的索引
                tmpFinishPMIndex = startPMIndex;
            }
        }
        // 结束时间组合框
        if (objSource == finishPMCombo) {
            // 取得开始时间的索引
            startPMIndex = startPMCombo.getSelectedIndex();
            // 取得结束时间的索引
            finishPMIndex = finishPMCombo.getSelectedIndex();
            if (finishPMIndex < startPMIndex) {
                finishPMCombo.setSelectedIndex(tmpFinishPMIndex);
                // 对不起，您输入的结束时间不能早于开始时间。
                SOptionPane.showErrorDialog(MessageCons.W10712);
            } else {
                tmpFinishPMIndex = finishPMIndex;
            }
        }

    }

    /**
     * 选择星期监听器
     * 
     * @param e
     *            状态变化事件
     */
    public void itemStateChanged(
            ItemEvent e) {
        Object source = e.getSource();
        // 选择每周的第一天
        if (source == firstDayCombo) {
            int weekdayIndex = firstDayCombo.getSelectedIndex();
            if (weekdayIndex == 0) {
                sunday.setSelected(false);
                monday.setSelected(true);
                tuesday.setSelected(true);
                wednsday.setSelected(true);
                thursday.setSelected(true);
                firday.setSelected(true);
                sateday.setSelected(false);
                sunday.setBounds(4 * CustOpts.HOR_GAP, workDayTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                        weekWidth, CustOpts.BTN_HEIGHT);
                monday.setBounds(sunday.getX() + weekWidth, sunday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                tuesday.setBounds(monday.getX() + weekWidth, monday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                wednsday.setBounds(tuesday.getX() + weekWidth, tuesday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                thursday.setBounds(wednsday.getX() + weekWidth, wednsday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                firday.setBounds(thursday.getX() + weekWidth, thursday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sateday.setBounds(firday.getX() + weekWidth, firday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                add(sunday);
                add(monday);
                add(tuesday);
                add(wednsday);
                add(thursday);
                add(firday);
                add(sateday);

            }
            if (weekdayIndex == 1) {
                monday.setSelected(false);
                tuesday.setSelected(true);
                wednsday.setSelected(true);
                thursday.setSelected(true);
                firday.setSelected(true);
                sateday.setSelected(true);
                sunday.setSelected(false);
                monday.setBounds(4 * CustOpts.HOR_GAP, workDayTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                        weekWidth, CustOpts.BTN_HEIGHT);
                tuesday.setBounds(monday.getX() + weekWidth, monday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                wednsday.setBounds(tuesday.getX() + weekWidth, tuesday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                thursday.setBounds(wednsday.getX() + weekWidth, wednsday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                firday.setBounds(thursday.getX() + weekWidth, thursday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sateday.setBounds(firday.getX() + weekWidth, firday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sunday.setBounds(sateday.getX() + weekWidth, sateday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                add(monday);
                add(tuesday);
                add(wednsday);
                add(thursday);
                add(firday);
                add(sateday);
                add(sunday);
            }
            if (weekdayIndex == 2) {
                tuesday.setSelected(false);
                wednsday.setSelected(true);
                thursday.setSelected(true);
                firday.setSelected(true);
                sateday.setSelected(true);
                sunday.setSelected(true);
                monday.setSelected(false);
                tuesday.setBounds(4 * CustOpts.HOR_GAP, workDayTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                        weekWidth, CustOpts.BTN_HEIGHT);
                wednsday.setBounds(tuesday.getX() + weekWidth, tuesday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                thursday.setBounds(wednsday.getX() + weekWidth, wednsday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                firday.setBounds(thursday.getX() + weekWidth, thursday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sateday.setBounds(firday.getX() + weekWidth, firday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sunday.setBounds(sateday.getX() + weekWidth, sateday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                monday.setBounds(sunday.getX() + weekWidth, sunday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                add(tuesday);
                add(wednsday);
                add(thursday);
                add(firday);
                add(sateday);
                add(sunday);
                add(monday);
            }
            if (weekdayIndex == 3) {
                wednsday.setSelected(false);
                thursday.setSelected(true);
                firday.setSelected(true);
                sateday.setSelected(true);
                sunday.setSelected(true);
                monday.setSelected(true);
                tuesday.setSelected(false);
                wednsday.setBounds(4 * CustOpts.HOR_GAP, workDayTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                        weekWidth, CustOpts.BTN_HEIGHT);
                thursday.setBounds(wednsday.getX() + weekWidth, wednsday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                firday.setBounds(thursday.getX() + weekWidth, thursday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sateday.setBounds(firday.getX() + weekWidth, firday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sunday.setBounds(sateday.getX() + weekWidth, sateday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                monday.setBounds(sunday.getX() + weekWidth, sunday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                tuesday.setBounds(monday.getX() + weekWidth, monday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                add(wednsday);
                add(thursday);
                add(firday);
                add(sateday);
                add(sunday);
                add(monday);
                add(tuesday);
            }
            if (weekdayIndex == 4) {
                thursday.setSelected(false);
                firday.setSelected(true);
                sateday.setSelected(true);
                sunday.setSelected(true);
                monday.setSelected(true);
                tuesday.setSelected(true);
                wednsday.setSelected(false);
                thursday.setBounds(4 * CustOpts.HOR_GAP, workDayTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                        weekWidth, CustOpts.BTN_HEIGHT);
                firday.setBounds(thursday.getX() + weekWidth, thursday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sateday.setBounds(firday.getX() + weekWidth, firday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sunday.setBounds(sateday.getX() + weekWidth, sateday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                monday.setBounds(sunday.getX() + weekWidth, sunday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                tuesday.setBounds(monday.getX() + weekWidth, monday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                wednsday.setBounds(tuesday.getX() + weekWidth, tuesday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                add(thursday);
                add(firday);
                add(sateday);
                add(sunday);
                add(monday);
                add(tuesday);
                add(wednsday);
            }
            if (weekdayIndex == 5) {
                firday.setSelected(false);
                sateday.setSelected(true);
                sunday.setSelected(true);
                monday.setSelected(true);
                tuesday.setSelected(true);
                wednsday.setSelected(true);
                thursday.setSelected(false);
                firday.setBounds(4 * CustOpts.HOR_GAP, workDayTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                        weekWidth, CustOpts.BTN_HEIGHT);
                sateday.setBounds(firday.getX() + weekWidth, firday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                sunday.setBounds(sateday.getX() + weekWidth, sateday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                monday.setBounds(sunday.getX() + weekWidth, sunday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                tuesday.setBounds(monday.getX() + weekWidth, monday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                wednsday.setBounds(tuesday.getX() + weekWidth, tuesday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                thursday.setBounds(wednsday.getX() + weekWidth, wednsday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                add(firday);
                add(sateday);
                add(sunday);
                add(monday);
                add(tuesday);
                add(wednsday);
                add(thursday);
            }
            if (weekdayIndex == 6) {
                sateday.setSelected(false);
                sunday.setSelected(true);
                monday.setSelected(true);
                tuesday.setSelected(true);
                wednsday.setSelected(true);
                thursday.setSelected(true);
                firday.setSelected(false);
                sateday.setBounds(4 * CustOpts.HOR_GAP, workDayTitle.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                        weekWidth, CustOpts.BTN_HEIGHT);
                sunday.setBounds(sateday.getX() + weekWidth, sateday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                monday.setBounds(sunday.getX() + weekWidth, sunday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                tuesday.setBounds(monday.getX() + weekWidth, monday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                wednsday.setBounds(tuesday.getX() + weekWidth, tuesday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                thursday.setBounds(wednsday.getX() + weekWidth, wednsday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                firday.setBounds(thursday.getX() + weekWidth, thursday.getY(), weekWidth, CustOpts.BTN_HEIGHT);
                add(sateday);
                add(sunday);
                add(monday);
                add(tuesday);
                add(wednsday);
                add(thursday);
                add(firday);
            }
        }
    }

    private int width;
    private int height;
    private JDialog dialog;
    private TimeZoonDialog timeZoonDialog;
    private JButton zonnButton;
    private JComboBox firstDayCombo;
    private JComboBox firstWeekCombo;
    private PIMSeparator workDayTitle;
    private int weekWidth;
    private JCheckBox sunday;
    private JCheckBox monday;
    private JCheckBox tuesday;
    private JCheckBox wednsday;
    private JCheckBox thursday;
    private JCheckBox firday;
    private JCheckBox sateday;
    private JCheckBox chooseZoonBox;
    private JCheckBox autoRecCheckBox;
    private JCheckBox autorefuseCheckBox;
    private JCheckBox autoRepeatCheckBox;
    private JComboBox startAMCombo;
    private JComboBox finishAMCombo;
    private JComboBox startPMCombo;
    private JComboBox finishPMCombo;
    private JComboBox backCombo;

    // 取得开始时间的索引
    private int startAMIndex;
    private int startPMIndex;
    // 取得结束时间的索引
    private int finishAMIndex;
    private int finishPMIndex;
    // 存储上次的索引值
    private int tmpFinishAMIndex;
    private int tmpFinishPMIndex;

    private Object[] timeAM = new Object[] { "0.00", "0.15", "0.30", "0.45", "1.00", "1.15", "1.30", "1.45", "2.00",
            "2.15", "2.30", "2.45", "3.00", "3.15", "3.30", "3.45", "4.00", "4.15", "4.30", "4.45", "5.00", "5.15",
            "5.30", "5.45", "6.00", "6.15", "6.30", "6.45", "7.00", "7.15", "7.30", "7.45", "8.00", "8.15", "8.30",
            "8.45", "9.00", "9.15", "9.30", "9.45", "10.00", "10.15", "10.30", "10.45", "11.00", "11.15", "11.30",
            "11.45", "12.00", };

    private Object[] timePM = new Object[] { "12.00", "12.15", "12.30", "12.45", "13.00", "13.15", "13.30", "13.45",
            "14.00", "14.15", "14.30", "14.45", "15.00", "15.15", "15.30", "15.45", "16.00", "16.15", "16.30", "16.45",
            "17.00", "17.15", "17.30", "17.45", "18.00", "18.15", "18.30", "18.45", "19.00", "19.15", "19.30", "19.45",
            "20.00", "20.15", "20.30", "20.45", "21.00", "21.15", "21.30", "21.45", "22.00", "22.15", "22.30", "22.45",
            "23.00", "23.15", "23.30", "23.45", "0.00" };

}
