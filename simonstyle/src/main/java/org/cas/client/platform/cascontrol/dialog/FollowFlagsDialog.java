package org.cas.client.platform.cascontrol.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.cas.client.platform.casbeans.calendar.CalendarCombo;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.FollowFlagsDialogConstants;

public class FollowFlagsDialog extends JDialog implements ActionListener, ICASDialog {
    // 保存本对话盒的宽度和高度
    public static final int WIDTH = 300;
    public static final int HEIGHT = 150;

    /**
     * 创建一个 FollowFlagsDialog 的实例
     * 
     * @param prmParent
     *            父窗体引用
     * @param prmRecords
     *            记录列表
     * @called by: FollowUpAction（When选中多条记录时）;
     */
    public FollowFlagsDialog(Frame prmParent, Vector prmRecords) {
        super(prmParent, true);
        if (prmRecords == null) {
            return;
        }
        pimRecords = prmRecords;

        // 组件初始化并布局
        initComponent();
    }

    public void reLayout() {
    };

    /**
     * 初始化并布局; called by : 构建器
     */
    private void initComponent() {
        Dimension tmpDlgSize = new Dimension(WIDTH, HEIGHT); // 先设定对话盒的大小，为布局打下基础。
        int X_Coordinate = 0; // 这两个变量用来给组件定位,所有的组件都根据它来计算
        int Y_Coordinate = 0;

        setTitle(FollowFlagsDialogConstants.FOLLOW_FLAG_TITLE); // 设置标题
        getContentPane().setLayout(null);
        // 添加最上面的提示信息。
        JTextArea promptLabel = new JTextArea(FollowFlagsDialogConstants.TOP_LABEL);
        promptLabel.setBounds(X_Coordinate, Y_Coordinate, tmpDlgSize.width, CustOpts.LBL_HEIGHT * 2);
        getContentPane().add(promptLabel);

        // 添加"标志"的标签
        Y_Coordinate += promptLabel.getPreferredSize().height;
        JLabel flagLabel = new JLabel(FollowFlagsDialogConstants.FLAG);
        flagLabel.setDisplayedMnemonic('F');
        flagLabel.setBounds(X_Coordinate, Y_Coordinate, flagLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(flagLabel);

        // 添加"到期时间"标签
        JLabel expireLabel = new JLabel(FollowFlagsDialogConstants.EXPIRE_TIME);
        expireLabel.setDisplayedMnemonic('D');
        expireLabel.setBounds(X_Coordinate, Y_Coordinate + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                expireLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(expireLabel);
        int tempY = Y_Coordinate + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;

        // 添加"已完成"按钮
        finishedBTN = new JCheckBox(FollowFlagsDialogConstants.FINISHED, false);
        finishedBTN.setMnemonic('O');
        X_Coordinate = tmpDlgSize.width - finishedBTN.getPreferredSize().width;
        finishedBTN
                .setBounds(X_Coordinate, Y_Coordinate - 1, finishedBTN.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        getContentPane().add(finishedBTN);

        // 添加"标志"组合框
        int interval = expireLabel.getPreferredSize().width + CustOpts.VER_GAP;
        int tmpWidth =
                tmpDlgSize.width - finishedBTN.getPreferredSize().width - expireLabel.getPreferredSize().width
                        - CustOpts.VER_GAP * 2;
        flagCombo = new JComboBox(FollowFlagsDialogConstants.FLAG_ITEMS);
        flagLabel.setLabelFor(flagCombo);
        flagLabel.setBounds(0, Y_Coordinate, interval, CustOpts.BTN_HEIGHT);
        getContentPane().add(flagLabel);
        flagCombo.setEditable(true);
        flagCombo.setBounds(flagLabel.getX() + interval, flagLabel.getY(), tmpWidth, CustOpts.BTN_HEIGHT);
        getContentPane().add(flagCombo);

        // 添加"到期时间"组合框
        tmpWidth =
                tmpDlgSize.width - expireLabel.getPreferredSize().width - 2 * CustOpts.VER_GAP
                        - finishedBTN.getPreferredSize().width;
        Y_Coordinate += CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;
        expireCombo = new CalendarCombo();
        expireLabel.setLabelFor(expireCombo);
        expireLabel.setBounds(0, tempY, interval, CustOpts.BTN_HEIGHT);
        getContentPane().add(expireLabel);
        expireCombo.setBounds(expireLabel.getX() + interval, expireLabel.getY(), tmpWidth,
                expireCombo.getPreferredSize().height);
        getContentPane().add(expireCombo);
        expireCombo.setTime(time);

        // 添加时间组合框
        timeCombo = new JComboBox(timeData);

        timeCombo.setBounds(X_Coordinate, tempY, timeCombo.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        timeCombo.setEditable(true);
        getContentPane().add(timeCombo);

        // 添加"清除标志"按钮
        X_Coordinate = 0;
        Y_Coordinate += CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;
        clearFlagBox = new JCheckBox(FollowFlagsDialogConstants.CLEAR_FLAG, false);
        clearFlagBox.setMnemonic('C');
        clearFlagBox.setBounds(X_Coordinate, Y_Coordinate, clearFlagBox.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        getContentPane().add(clearFlagBox);
        clearFlagBox.addActionListener(this);

        // 添加"取消"按钮
        cancel = new JButton(FollowFlagsDialogConstants.CANCEL_BTN);
        X_Coordinate = tmpDlgSize.width - cancel.getPreferredSize().width;
        Y_Coordinate = tmpDlgSize.height - cancel.getPreferredSize().height;
        cancel.setBounds(X_Coordinate, Y_Coordinate, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);

        // 添加"确定"按钮
        ok = new JButton(FollowFlagsDialogConstants.OK_BTN);
        X_Coordinate -= cancel.getPreferredSize().width + CustOpts.VER_GAP;
        ok.setBounds(X_Coordinate, Y_Coordinate, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);

        ok.addActionListener(this);
        cancel.addActionListener(this);

        // 设置本对话盒尺寸
        setSize(tmpDlgSize);

        setBounds((CustOpts.SCRWIDTH - tmpDlgSize.width) / 2, (CustOpts.SCRHEIGHT - tmpDlgSize.height) / 2,
                tmpDlgSize.width, tmpDlgSize.height); // 对话框的默认尺寸。

        // 当选中为单条记录时，对话盒显示该条记录已有的设置记录。
        if (pimRecords.size() == 1) {
            PIMRecord tmpRecord = (PIMRecord) pimRecords.get(0);
            // 后续标志的字符串
            Object tmpValue = tmpRecord.getFieldValue(ModelDBCons.FOLLOWFLAGS);
            if (tmpValue != null && tmpValue.toString().trim().length() > 0) {
                flagCombo.setSelectedItem(tmpValue.toString().trim());
            }
            // 是否提醒的布尔值
            tmpValue = tmpRecord.getFieldValue(ModelDBCons.FOLLOWUPCOMPLETE);
            if (tmpValue != null && tmpValue.toString().trim().equals(PIMPool.BOOLEAN_TRUE)) // @NOTE:这个标记被用反了。
            {
                finishedBTN.setSelected(true);
            }
            // 提醒时间
            tmpValue = tmpRecord.getFieldValue(ModelDBCons.FOLOWUPENDTIME);
            if (tmpValue != null && tmpValue instanceof Date) {
                expireCombo.setSelectedItem((Date) tmpValue);
                String timeStr = getTimeString((Date) tmpValue);
                timeCombo.setSelectedItem(timeStr);
            }
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
        Object tmpSource = e.getSource();
        if (tmpSource == clearFlagBox) {
            finishedBTN.setEnabled(!clearFlagBox.isSelected());
            expireCombo.setEnabled(!clearFlagBox.isSelected());
            flagCombo.setEnabled(!clearFlagBox.isSelected());
            timeCombo.setEnabled(!clearFlagBox.isSelected());
        } else if (tmpSource == ok) // 确定按钮事件触发
        {
            // 计算出日期是否过期,如过期要弹出信息框不然
            Object tmpDate = expireCombo.getSelectedItem();
            String endTimeStr = timeCombo.getEditor().getItem().toString();
            if (tmpDate != null && tmpDate instanceof Date) {
                Vector vector = getRemindTime(expireCombo, endTimeStr);
                ((Date) tmpDate).setHours(((Integer) (vector.get(0))).intValue());
                ((Date) tmpDate).setMinutes(((Integer) (vector.get(1))).intValue());
                ((Date) tmpDate).setSeconds(0);
                Date currentDate = Calendar.getInstance().getTime();
                if (((Date) tmpDate).getTime() < currentDate.getTime() && !finishedBTN.isSelected()) // 设置的时间遭遇当前的时间,且没有设为已完成.
                {
                    // 系统发现您设置的提醒时间早于系统当前的时间，因此您设置的提醒将不会发生，是否继续？
                    int i = SOptionPane.showErrorDialog(MessageCons.Q50679);
                    if (i == 1) {
                        return;
                    }
                }
            } // 除错完毕--------------------------------------------------------------------
            isOKPressed = true;
            setVisible(false);
        }
    }

    /**
     * 通过日期对象取得时间
     * 
     * @return 格式化后的string
     */
    private String getTimeString(
            Date date) {
        String str = CASUtility.EMPTYSTR;
        int hour = date.getHours();
        int minute = date.getMinutes();
        str = hour < 10 ? str + "0" + hour : str.concat(Integer.toString(hour));
        str = minute < 10 ? str + ":0" + minute : str + ':' + minute;
        return str;
    }

    /**
     * 取得时间
     * 
     * @param prmComboBox
     *            :时间组合框
     * @param prmTimeStr
     *            :时间
     * @return vector 0: hour 1: minute
     */
    private Vector getRemindTime(
            JComboBox prmComboBox,
            String prmTimeStr) {
        Vector timeVector = new Vector();
        int length = prmTimeStr.length();
        int sginPos1 = prmTimeStr.indexOf(':');
        int sginPos2 = prmTimeStr.indexOf('.');
        if (sginPos1 != -1 && (sginPos1 + 1) <= length) {
            String hourstr = prmTimeStr.substring(0, sginPos1);
            String minutestr = prmTimeStr.substring(sginPos1 + 1);
            try {
                timeVector.add(PIMPool.pool.getIntegerKey(hourstr.trim()));
                timeVector.add(PIMPool.pool.getIntegerKey(minutestr.trim()));
            } catch (NumberFormatException e) {
                prmComboBox.grabFocus();
                Toolkit.getDefaultToolkit().beep();
            }
        } else if (sginPos2 != -1) {
            String hourstr = prmTimeStr.substring(0, sginPos2);
            String minutestr = prmTimeStr.substring(sginPos2 + 1);
            try {
                timeVector.add(PIMPool.pool.getIntegerKey(hourstr.trim()));
                timeVector.add(PIMPool.pool.getIntegerKey(minutestr.trim()));
            } catch (NumberFormatException e) {
                prmComboBox.grabFocus();
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            prmComboBox.grabFocus();
            Toolkit.getDefaultToolkit().beep();
        }
        return timeVector;
    }

    /**
     * Creates a new instance of IPIMDialog
     * 
     * @called by: FollowUpAction;
     */
    public PIMRecord getContents() {
        if (!isOKPressed) {
            return null;
        } // 如果本方法被调时,确定按钮没有被点,则返回null,

        PIMRecord tmpRecord = new PIMRecord();
        tmpRecord.setFieldValues(new Hashtable());
        if (clearFlagBox.isSelected()) {
            // 后续标志的字符串为空
            tmpRecord.setFieldValue(ModelDBCons.FOLLOWFLAGS, CASUtility.EMPTYSTR);
            // 保存是否提醒标志
            tmpRecord.setFieldValue(ModelDBCons.FOLLOWUPCOMPLETE, Boolean.FALSE);
            // 这是一个关联字段"标志状态"
            tmpRecord.setFieldValue(ModelDBCons.FLAGSTATUS, PIMPool.pool.getKey(0));
        } else {
            // 取出后续标志的字符串，并设置进record。
            Object tmpOb = flagCombo.getSelectedItem();
            tmpRecord.setFieldValue(ModelDBCons.FOLLOWFLAGS, tmpOb == null ? CASUtility.EMPTYSTR : tmpOb.toString());
            // 保存是否已完成标志
            tmpRecord.setFieldValue(ModelDBCons.FOLLOWUPCOMPLETE, finishedBTN.isSelected() ? Boolean.TRUE
                    : Boolean.FALSE);
            // 保存是否已完成字段的关联字段"标志状态"
            tmpRecord.setFieldValue(ModelDBCons.FLAGSTATUS, PIMPool.pool.getKey(finishedBTN.isSelected() ? 2 : 1));
            Object tmpDate = expireCombo.getSelectedItem();
            String endTimeStr = timeCombo.getEditor().getItem().toString();
            if (tmpDate != null && tmpDate instanceof Date) {
                // 保存提醒时间
                Vector vector = getRemindTime(expireCombo, endTimeStr);
                ((Date) tmpDate).setHours(((Integer) (vector.get(0))).intValue());
                ((Date) tmpDate).setMinutes(((Integer) (vector.get(1))).intValue());
                tmpRecord.setFieldValue(ModelDBCons.FOLOWUPENDTIME, (Date) tmpDate);
            }
        }
        return tmpRecord;
    }

    public boolean setContents(
            PIMRecord prmRecord) {
        return false;
    }

    public void makeBestUseOfTime() {
    }

    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    public PIMTextPane getTextPane() {
        return null;
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        clearFlagBox.removeActionListener(this);
        dispose();
    }

    public Container getContainer() {
        return getContentPane();
    }

    // 以下为本类的变量声明
    // ******************************************************
    private JCheckBox clearFlagBox;
    // 用于日期选择区中的时间
    private String time;

    private JButton ok, cancel;
    private JComboBox flagCombo, timeCombo;
    private CalendarCombo expireCombo;
    private JCheckBox finishedBTN;

    private Vector pimRecords;
    private boolean isOKPressed;

    private Object[] timeData = new Object[] { " 00:00", " 00:30", " 01:00", " 01:30", " 02:00", " 02:30", " 03:00",
            " 03:30", " 04:00", " 04:30", " 05:00", " 05:30", " 06:00", " 06:30", " 07:00", " 07:30", " 08:00",
            " 08:30", " 09:00", " 09:30", " 10:00", " 10:30", " 11:00", " 11:30", " 12:00", " 12:30", " 13:00",
            " 13:30", " 14:00", " 14:30", " 15:00", " 15:30", " 16:00", " 16:30", " 17:00", " 17:30", " 18:00",
            " 18:30", " 19:00", " 19:30", " 20:00", " 20:30", " 21:00", " 21:30", " 22:00", " 22:30", " 23:00",
            " 23:30", };

}
