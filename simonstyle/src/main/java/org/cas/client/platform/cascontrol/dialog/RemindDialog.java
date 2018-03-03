package org.cas.client.platform.cascontrol.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.menuaction.OpenAction;
import org.cas.client.platform.cascontrol.thread.ThreadActionsFacade;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableModelVecBased;
import org.cas.client.platform.pimview.pimtable.specialrenderers.DateRenderer;
import org.cas.client.resource.international.RemindDialogConstant;

/**
 * 本类用于显示到期提醒信息，新邮件到达提醒信息的对话盒。
 */
public class RemindDialog extends JFrame implements ActionListener, MouseListener {
    private static RemindDialog instance;

    /**
     * 工厂方法
     * 
     * @return 本类实例
     */
    public static synchronized RemindDialog getInstance() {
        if (instance == null) {
            instance = new RemindDialog();
        }
        return instance;
    }

    /**
     * Creates a new instance of RemindDialog
     * 
     * @param prmRecord
     *            : 调用此构建器必须传入的PIMRecord数组
     * @param title
     *            : 现实在此对话框的标题
     */
    private RemindDialog() {
        getContentPane().setLayout(null);
        // 初始化用来显示为第一行第一列的内容的label @NOTE:暂时不初始化显示的内容，在增加记录时统一初始化
        displaySubjectLabel = new JLabel();
        // 初始化用来显示为第一行第二列的内容的label @NOTE:暂时不初始化显示的内容，在增加记录时统一初始化
        displayDueLabel = new JLabel();
        displaySubjectLabel.setBounds(5, 0, width - 2 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        displayDueLabel.setBounds(2 * CustOpts.HOR_GAP, displaySubjectLabel.getY() + CustOpts.LBL_HEIGHT, width - 2
                * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        // 添加标签
        getContentPane().add(displaySubjectLabel);
        getContentPane().add(displayDueLabel);

        // 组合框标签
        JLabel tmpClickLabel = new JLabel(RemindDialogConstant.NOTICE2, 'C');
        int tmpClickLabelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { tmpClickLabel });
        tmpClickLabel.setSize(tmpClickLabelWidth, CustOpts.LBL_HEIGHT);
        // 打盹按钮"等一下提示"
        snoozeButton = new JButton(RemindDialogConstant.SNOOZE);
        snoozeButton.setMnemonic('S');
        // 忽略所有按钮
        dismissAllButton = new JButton(RemindDialogConstant.DISMISS_ALL);
        dismissAllButton.setMnemonic('A');
        // 打开按钮
        openItemButton = new JButton(RemindDialogConstant.OPEN_ITEM);
        openItemButton.setMnemonic('O');
        openItemButton.setEnabled(false);
        // 忽略按钮
        dismissButton = new JButton(RemindDialogConstant.DISMISS);
        dismissButton.setMnemonic('D');
        dismissButton.setEnabled(false);
        // 取得按钮的最大宽度
        int tmpButtonMaxWidth =
                10 + CASDialogKit.getMaxWidth(new JComponent[] { snoozeButton, dismissAllButton, openItemButton,
                        dismissButton });
        snoozeButton.setBounds(width - tmpButtonMaxWidth + 10, height - CustOpts.HOR_GAP - CustOpts.BTN_HEIGHT,
                tmpButtonMaxWidth, CustOpts.BTN_HEIGHT);
        getContentPane().add(snoozeButton);
        // 从中选择需要打盹的时间 "组合框"
        timeComboBox = new JComboBox(comboBoxData);
        tmpClickLabel.setLabelFor(timeComboBox);
        tmpClickLabel.setBounds(5, height - CustOpts.HOR_GAP - CustOpts.LBL_HEIGHT - CustOpts.BTN_HEIGHT,
                tmpClickLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(tmpClickLabel);
        timeComboBox.setBounds(tmpClickLabel.getX(), tmpClickLabel.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                width - CustOpts.HOR_GAP - tmpButtonMaxWidth + 10, CustOpts.BTN_HEIGHT);
        getContentPane().add(tmpClickLabel);

        // 添加滚动窗格
        // 添加滚动窗格的Y坐标
        int tmpScrollPaneY = displayDueLabel.getY() + CustOpts.LBL_HEIGHT + 3 * CustOpts.VER_GAP;
        // 添加滚动窗格的高度
        int tmpScrollPaneHeight =
                height - tmpScrollPaneY - 2 * CustOpts.BTN_HEIGHT - CustOpts.LBL_HEIGHT - 3 * CustOpts.VER_GAP
                        - CustOpts.HOR_GAP;
        // 表格模型
        dataVector = new Vector(); // 存放表格内容的vector
        columnNamesVector = new Vector(); // 表格头显示的内容
        columnNamesVector.add(RemindDialogConstant.SUBJECT); // 提示内容
        columnNamesVector.add(RemindDialogConstant.DUE_IN); // 到期时间
        model = new PIMTableModelVecBased(dataVector, columnNamesVector);
        model.setCellEditable(false);
        table = new PIMTable(); // ---表格单元不可编辑
        table.setModel(model);
        table.setShowGrid(false); // --不显示网格线
        table.setHasSorter(false); // 禁止排序; //TODO:应该考虑允许排序。
        table.addMouseListener(this);
        // table.setAutoResizeMode(false);
        // ----------------------------------------------------------
        // table.getColumnModel().getColumn(RemindDialogConstant.DUE_IN).setPreferredWidth(width/2);
        // table.getColumnModel().getColumn(RemindDialogConstant.SUBJECT).setPreferredWidth(width - width/2 );
        // ----------------------------------------------------------
        // 滚动窗格
        PIMScrollPane tmpScrollPane = new PIMScrollPane(table);
        tmpScrollPane.getViewport().setBackground(Color.white);
        tmpScrollPane.setBounds(5, tmpScrollPaneY, width, tmpScrollPaneHeight);
        getContentPane().add(tmpScrollPane);
        // 添加按钮
        dismissAllButton.setBounds(5, tmpScrollPane.getY() + tmpScrollPaneHeight + CustOpts.VER_GAP, tmpButtonMaxWidth,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(dismissAllButton);

        openItemButton.setBounds(width - 2 * tmpButtonMaxWidth - CustOpts.HOR_GAP + 10, dismissAllButton.getY(),
                tmpButtonMaxWidth, CustOpts.BTN_HEIGHT);
        getContentPane().add(openItemButton);

        dismissButton.setBounds(width - tmpButtonMaxWidth + 10, dismissAllButton.getY(), tmpButtonMaxWidth,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(dismissButton);

        dismissAllButton.addActionListener(this);
        openItemButton.addActionListener(this);
        dismissButton.addActionListener(this);
        snoozeButton.addActionListener(this);
        // 设置标题
        setTitle(RemindDialogConstant.TITLE);
        setSize(width + 15, height + 30);
        setResizable(false);
        setIconImage(CustOpts.custOps.getFrameLogoImage());
    }

    /**
     * 添加新的内容
     * 
     * @param prmRecord
     *            : 新的记录数组
     * @for:TaskTimerTask,MailTimer
     * 
     */
    public void addRemindContent(
            Vector prmVec) {
        if (remindRecVec == null || remindRecVec.size() == 0) {
            remindRecVec = prmVec;
        } else {
            for (int i = 0; i < prmVec.size(); i++) // 先将参数中的内容吸收到remindRecVec中
            {
                remindRecVec.addElement(prmVec.get(i));
            }
        }
        int tmpSize = prmVec.size() / 5;
        updateDataVectorContent(prmVec); // 再将参数中的内容吸收到dataVector中，并更新显示
        model.setData(dataVector, columnNamesVector); // 改方法中触发他不了刷新
        table.getColumnModel().getColumn(1).setCellRenderer(new DateRenderer(true)); // 设置时间的绘制器
        // @note:必须每次更新界面时都记得设置一次时间的绘制器
        table.setDefaultSelected(0, 0);
        mousePressed(new MouseEvent(table, 0, 0, 0, 0, 0, 1, false));
    }

    /**
     * 根据传入的新的记录数组更新 dataVector 中的内容
     * 
     * @param records
     *            记录数组
     */
    private void updateDataVectorContent(
            Vector prmVec) {
        Vector tmpRecordsVector = null;
        int tmpType;
        int tmpSize = prmVec.size() / 5;
        for (int i = 0; i < tmpSize; i++) {
            tmpRecordsVector = new Vector();
            // @NOTE：不能用removeAllElements();，因为需要不同的实例好加入dataVector中
            tmpRecordsVector.add(prmVec.get(i * 5 + 3));
            tmpRecordsVector.add(prmVec.get(i * 5 + 4));
            tmpType = ((Integer) prmVec.get(i * 5)).intValue();
            PIMRecord tmpRecord = getSelectedRecord(i);
            // if (tmpType == ModelCons.TASK_APP) //任务,加入主题和到期时间
            // {
            // tmpRecordsVector.add(tmpRecord.getFieldValue(ModelDBCons.CAPTION));
            // tmpRecordsVector.add(tmpRecord.getFieldValue(ModelDBCons.END_TIME));
            // }
            // else if (tmpType == ModelCons.INBOX_APP) //收件箱,加入主题和接收时间
            // {
            // tmpRecordsVector.add(tmpRecord.getFieldValue(ModelDBCons.CAPTION));
            // tmpRecordsVector.add(tmpRecord.getFieldValue(MailDefaultViews.SENDTIME));
            // }
            // else if (tmpType == ModelCons.CALENDAR_APP) //日历，加入主题和开始时间
            // {
            // tmpRecordsVector.add(tmpRecord.getFieldValue(ModelDBCons.CAPTION));
            // tmpRecordsVector.add(tmpRecord.getFieldValue(ModelDBCons.CALENDAR_BEGIN_TIME));
            // }
            dataVector.add(tmpRecordsVector);
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
        Object tmpSource = e.getSource(); // 取得事件源
        /* 忽略所有事件 */
        if (tmpSource == dismissAllButton) {
            dataVector.removeAllElements(); // 先清空对话盒中table组件中的内容。@NOTE:因为忽略全部时必然导致对话盒
            // 关闭，而下一次显示时必然时有新的记录加入时，所以没有必要更新视图，
            setVisible(false); // 因为加入新记录时更新一次就够了。更不需要设时间绘制器
            /**
             * 更新全部需要提醒的记录数组中的每一条记录 记录信息存放在Vector中，以5个为一单位 依次为记录类型，记录ID，记录所在路径，记录主题，记录收取时间(或任务到期时间，约会开始时间)
             */
            int tmpSize = remindRecVec.size() / 5;
            for (int i = 0; i < tmpSize; i++) {
                PIMRecord tmpRecord = getSelectedRecord(i);
                int tmpType = ((Integer) remindRecVec.get(i * 5)).intValue();
                if (tmpType == ModelCons.TASK_APP) {
                    tmpRecord.getFieldValues().put(PIMPool.pool.getKey(ModelDBCons.NEED_AWOKE), Boolean.FALSE);
                    CASControl.ctrl.getModel().updateRecord(tmpRecord, true);
                } else if (tmpType == ModelCons.CALENDAR_APP) {
                    tmpRecord.setFieldValue(ModelDBCons.CALENDAR_NEED_REMIND, Boolean.FALSE);
                    CASControl.ctrl.getModel().updateRecord(tmpRecord, true);
                }
            }
            remindRecVec.clear(); // 使内存释放
        }
        /* 打盹事件 */
        else if (tmpSource == snoozeButton) {
            String tmpTimeString = (String) (timeComboBox.getSelectedItem());
            long time = getSelectedTime(tmpTimeString);
            snooze(time);
        }
        /* 打开事件 */
        else if (tmpSource == openItemButton) {
            openItem(getOpenPimRecords());
        }
        /* 忽略事件 */
        else if (tmpSource == dismissButton) {
            // 删除所选记录
            if (selectedRows == null) {
                return;
            }
            for (int i = 0; i < selectedRows.length; i++) {
                PIMRecord selectedionRecord = getSelectedRecord(selectedRows[i]);
                int tmpType = selectedionRecord.getAppIndex();
                if (tmpType == ModelCons.TASK_APP) {
                    selectedionRecord.setFieldValue(ModelDBCons.NEED_AWOKE, Boolean.FALSE);
                    CASControl.ctrl.getModel().updateRecord(selectedionRecord, true);
                } else if (tmpType == ModelCons.CALENDAR_APP) {
                    selectedionRecord.setFieldValue(ModelDBCons.CALENDAR_NEED_REMIND, Boolean.FALSE);
                    CASControl.ctrl.getModel().updateRecord(selectedionRecord, true);
                }
                dataVector.removeElementAt(selectedRows[i]);
            }
            model.setData(dataVector, columnNamesVector);
            table.getColumnModel().getColumn(1).setCellRenderer(new DateRenderer(true)); // 设置时间的绘制器
            if (model.getRowCount() == 0) {
                setVisible(false);
            }
            dismissButton.setEnabled(false);
            openItemButton.setEnabled(false);
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
        // 取得选取的行
        selectedRows = table.getSelectedRows();
        Object source = e.getSource();
        if (source == table) {
            if (selectedRows[0] >= 0) {
                dismissButton.setEnabled(true);
                openItemButton.setEnabled(true);
                // 取得该行的内容
                String tempNameString = (String) table.getValueAt(selectedRows[0], 0);
                Date tempAddressDate = (Date) table.getValueAt(selectedRows[0], 1);
                String tmpDueStr = CASUtility.EMPTYSTR;
                if (tempAddressDate != null) {
                    tmpDueStr = CASUtility.getFormatDateString(tempAddressDate);
                }
                displaySubjectLabel.setIcon(icon);
                // 将内容分别显示在标签上
                displaySubjectLabel.setText(tempNameString);
                if (tmpDueStr != null) {
                    displayDueLabel.setText(tmpDueStr);
                } else {
                    displayDueLabel.setText(CASUtility.EMPTYSTR);
                }
            } else {
                dismissButton.setEnabled(false);
                openItemButton.setEnabled(false);
            }
        } else {
            dismissButton.setEnabled(false);
            openItemButton.setEnabled(false);
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
    }

    /**
     * 打开选定的项目。
     * 
     * @param record
     *            被选定打开的项目
     */
    private void openItem(
            Vector prmSeleRecs) {
        if (prmSeleRecs != null) {
            OpenAction tmpAction = new OpenAction();
            tmpAction.setRecords(prmSeleRecs);
            tmpAction.actionPerformed(null);
        }
        // for (int i = 0; i < records.length; i++)
        // {
        // //已打开以后不提醒
        // if (records[i].getType() == ModelConstants.TASK_APP)
        // {
        // records[i].getFieldValues().put(PIMPool.pool.getIntegerKey(ModelDBConstants.NEED_AWOKE),
        // PIMPool.pool.getIntegerKey(0));
        // PIMControl.ctrl.getModel().updateRecord(records[i]);
        // }
        // }
    }

    /**
     * 打盹。用于隐藏提醒对话框，并在指定时间后显示。
     * 
     * @param time
     *            打盹事件，单位毫秒。
     */
    private void snooze(
            long time) {
        RemindDialog.this.setVisible(false); // 隐藏提醒对话框
        ThreadActionsFacade.getInstance().snooze(this, time); // 向后台线程中添加定时任务
    }

    /**
     * 将选择的时间转换为毫秒
     * 
     * @param selectedItem
     *            : 选择的字符串
     */
    private long getSelectedTime(
            String selectedItem) {
        long number = 0;
        if (selectedItem.endsWith(RemindDialogConstant.MINUTE)) // 单位为分
        {
            int sginIndex = selectedItem.indexOf(RemindDialogConstant.MINUTE);
            String numberString = selectedItem.substring(0, sginIndex);
            try {
                number = Long.parseLong(numberString); // 转换为整型
                number = number * 60 * 1000; // 转换为毫秒
            } catch (NumberFormatException e) {
            }
        }
        if (selectedItem.endsWith(RemindDialogConstant.HOUR)) // 单位为小时
        {
            int sginIndex = selectedItem.indexOf(RemindDialogConstant.HOUR);
            String numberString = selectedItem.substring(0, sginIndex);
            try {
                number = Long.parseLong(numberString); // 转换为整型
                number = number * 60 * 60 * 1000; // 转换为毫秒
            } catch (NumberFormatException e) {
            }
        }
        if (selectedItem.endsWith(RemindDialogConstant.DAY)) // 单位为天
        {
            int sginIndex = selectedItem.indexOf(RemindDialogConstant.DAY);
            String numberString = selectedItem.substring(0, sginIndex);
            try {
                number = Long.parseLong(numberString); // 转换为整型
                number = number * 24 * 60 * 60 * 1000; // 转换为毫秒
            } catch (NumberFormatException e) {
            }
        }
        if (selectedItem.endsWith(RemindDialogConstant.WEEK)) // 单位为周
        {
            int sginIndex = selectedItem.indexOf(RemindDialogConstant.WEEK);
            String numberString = selectedItem.substring(0, sginIndex);
            try {
                number = Long.parseLong(numberString); // 转换为整型
                number = number * 7 * 24 * 60 * 60 * 1000; // 转换为毫秒
            } catch (NumberFormatException e) {
            }
        }
        return number;
    }

    /**
     * 取得需要打开的PIMRecord数组
     */
    private Vector getOpenPimRecords() {
        if (selectedRows == null) {
            return new Vector();
        }
        Vector tepSeleRecs = new Vector();
        for (int i = 0; i < selectedRows.length; i++) {
            tepSeleRecs.add(getSelectedRecord(selectedRows[i]));
        }
        return tepSeleRecs;
    }

    /**
     * 获得选取的记录
     * 
     * @param prmIndex
     * @return
     */
    private PIMRecord getSelectedRecord(
            int prmIndex) {
        int tmpType = ((Integer) remindRecVec.get(prmIndex * 5)).intValue();
        int tmpID = ((Integer) remindRecVec.get(prmIndex * 5 + 1)).intValue();
        int tmpFoldID = ((Integer) remindRecVec.get(prmIndex * 5 + 2)).intValue();
        return CASControl.ctrl.getModel().selectRecord(tmpType, tmpID, tmpFoldID);
    }

    private int width = 450;
    private int height = 260;
    private int[] selectedRows;
    private JButton dismissAllButton;
    private JButton snoozeButton;
    private JButton openItemButton;
    private JButton dismissButton;
    private Vector remindRecVec = new Vector(); // 用来存放全部的需要提示的记录
    private Vector dataVector; // 用来存放需要显示在对话盒上的记录。
    private PIMTableModelVecBased model;
    private Vector columnNamesVector;
    private JLabel displaySubjectLabel;
    private JLabel displayDueLabel;
    private JComboBox timeComboBox;
    private PIMTable table;
    private Icon icon = CustOpts.custOps.getUnreadMailIcon();
    private Object[] comboBoxData = { "5".concat(RemindDialogConstant.MINUTE),
            "10".concat(RemindDialogConstant.MINUTE), "15".concat(RemindDialogConstant.MINUTE),
            "20".concat(RemindDialogConstant.MINUTE), "25".concat(RemindDialogConstant.MINUTE),
            "30".concat(RemindDialogConstant.MINUTE), "1".concat(RemindDialogConstant.HOUR),
            "2".concat(RemindDialogConstant.HOUR), "3".concat(RemindDialogConstant.HOUR),
            "1".concat(RemindDialogConstant.DAY), "2".concat(RemindDialogConstant.DAY),
            "3".concat(RemindDialogConstant.DAY), "1".concat(RemindDialogConstant.WEEK),
            "2".concat(RemindDialogConstant.WEEK), "3".concat(RemindDialogConstant.WEEK) };
    // 组合框中的信息
}
// /**
// *
// */
// private PIMRecord [] getAndArray(PIMRecord [] array1,PIMRecord [] array2)
// {
// if(array1.length == 0)
// {
// return array2;
// }
// else if(array2.length == 0)
// {
// return array1;
// }
// else
// {
// int length1 = array1.length;
// int length2 = array2.length;
// Vector vector = new Vector();
// for(int i = 0;i<length1;i++)
// {
// boolean isDefferent = true;
// boolean result = true;
// int id = array1[i].getRecordID();
// for(int j = 0;j<length2;j++)
// {
// if(array2[j].getRecordID() != id)
// {
// isDefferent = true;
// }
// else
// {
// isDefferent = false;
// }
// result &= isDefferent;
// }
// if(result)
// {
// vector.add(array1[i]);
// }
// }
// PIMRecord [] record = new PIMRecord[vector.size()];
// for(int i = 0;i<record.length;i++)
// {
// record[i] = (PIMRecord)vector.get(i);
// }
// return record;
// }
// }
