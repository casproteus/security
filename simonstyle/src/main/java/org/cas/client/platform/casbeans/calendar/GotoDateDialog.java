package org.cas.client.platform.casbeans.calendar;

import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.EDate;
import org.cas.client.platform.casutil.EDaySet;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.MonthConstant;

class GotoDateDialog extends JDialog implements DlgConst {
    /** Creates new GotoDateDialog */
    public GotoDateDialog(Frame frame) {
        super(frame, true);
        setTitle(DlgConst.GOTOTITLE);
        dateSeleArea = DateSeleAreaPane.getInstance();

        int x = 0;
        int y = 0;
        FontMetrics fm = getFontMetrics(CustOpts.custOps.getFontOfDefault());
        int len1 = fm.stringWidth(SELECT_DATE + "W");
        int len2 = fm.stringWidth(SELECT_MODEL + "W");
        labelLen = Math.max(len1, len2);
        int maxWidth = labelLen + comboxLen;
        int maxHeight;

        y = initComponent(x, y);

        x = (maxWidth - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP) / 2;
        y += CustOpts.VER_GAP;
        okButton = new JButton(DlgConst.OK);
        okButton.setBounds(x, y, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(okButton);
        x += CustOpts.BTN_WIDTH + CustOpts.HOR_GAP;
        cancelButton = new JButton(DlgConst.CANCEL);
        cancelButton.setBounds(x, y, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancelButton);
        y += CustOpts.BTN_HEIGHT;

        maxHeight = y;
        setBounds((CustOpts.SCRWIDTH - maxWidth) / 2, (CustOpts.SCRHEIGHT - maxHeight) / 2, maxWidth, maxHeight); // 对话框的默认尺寸。

        addWindowListener(new WindowAdapter() {
            /* dilog is closing */
            public void windowClosing(
                    WindowEvent evt) {
                exitDialog();
            }
        });

        okButton.addActionListener(new ActionListener() {
            /* action is performed */
            public void actionPerformed(
                    ActionEvent evt) {
                dateSeleArea.setDaySet(selectMonth.getCustomDate());
                dateSeleArea.setCalendarViewModel(getSelectModel());
                exitDialog();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            /* action is performed */
            public void actionPerformed(
                    ActionEvent evt) {
                exitDialog();
            }
        });
    }

    private int initComponent(
            int x,
            int y) {
        JLabel label = new JLabel(SELECT_DATE, 'D');
        label.setBounds(x, y, labelLen, CustOpts.LBL_HEIGHT);
        getContentPane().add(label);
        int tmpX = x + labelLen;

        selectMonth = new ESingleCalendar();
        selectMonth.setBounds(tmpX, y, comboxLen, CustOpts.LBL_HEIGHT);
        getContentPane().add(selectMonth);
        label.setLabelFor(selectMonth);
        y += CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;
        EDate date = dateSeleArea.getDaySet().getIndexDate(0);
        EDaySet daySet = new EDaySet();
        daySet.addDate((EDate) date.clone());
        selectMonth.setCustomDate(daySet);

        label = new JLabel(SELECT_MODEL, 'S');
        label.setBounds(x, y, labelLen, CustOpts.LBL_HEIGHT);
        getContentPane().add(label);

        selectModel = new JComboBox(modeType);
        selectModel.setBounds(tmpX, y, comboxLen, CustOpts.BTN_HEIGHT);
        getContentPane().add(selectModel);
        label.setLabelFor(selectModel);
        y += CustOpts.LBL_HEIGHT;

        int model = dateSeleArea.getCalendarViewModel();
        switch (model) {
            case MonthConstant.WORK_MODEL:
                selectModel.setSelectedItem(WORK_VIEW_MODEL);
                break;

            case MonthConstant.WEEK_MODEL:
                selectModel.setSelectedItem(WEEK_VIEW_MODEL);
                break;

            case MonthConstant.MONTH_MODEL:
                selectModel.setSelectedItem(MONTH_VIEW_MODEL);
                break;

            /*
             * case EMonthGroup.YEAR_MODEL: selectModel.setSelectedItem(YEAR_VIEW_MODEL); break;
             */

            default:
                selectModel.setSelectedItem(DAY_VIEW_MODEL);
                break;
        }

        return y;
    }

    private int getSelectModel() {
        int model = MonthConstant.DAY_MODEL;
        String modelStr = (String) selectModel.getSelectedItem();
        if (modelStr.equals(WORK_VIEW_MODEL)) {
            model = MonthConstant.WORK_MODEL;
        } else if (modelStr.equals(WEEK_VIEW_MODEL)) {
            model = MonthConstant.WEEK_MODEL;
        } else if (modelStr.equals(MONTH_VIEW_MODEL)) {
            model = MonthConstant.MONTH_MODEL;
        }
        /*
         * else if (modelStr.equals(YEAR_VIEW_MODEL)) { model = EMonthGroup.YEAR_MODEL; }
         */
        return model;
    }

    private void exitDialog() {
        dispose();
    }

    private DateSeleAreaPane dateSeleArea;
    private ESingleCalendar selectMonth;
    private JComboBox selectModel;
    private JButton okButton, cancelButton;
    private int labelLen;
    private int comboxLen = 180;

    private String[] modeType = { DAY_VIEW_MODEL, WEEK_VIEW_MODEL, MONTH_VIEW_MODEL };
}
