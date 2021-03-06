package org.cas.client.platform.input.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.casbeans.calendar.CalendarCombo;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.input.InputDefaultViews;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

/** 联系人对话框 */
class InputGeneralPanel extends JScrollPane implements MouseListener, ActionListener, Runnable, Releasable {
    /**
     * Creates a new instance of ContactGeneralPanel 新建和编辑联系人对话框
     * 
     * @param prmDlg
     *            父窗体
     */
    InputGeneralPanel(InputDlg prmDlg) {
        dlg = prmDlg;
    }

    /** 当ArrowButton被按下时，记下拿一个Button被按。 */
    @Override
    public void mousePressed(
            java.awt.event.MouseEvent e) {
    }

    /** Invoked when a mouse button has been released on a component. */
    @Override
    public void mouseReleased(
            java.awt.event.MouseEvent e) {
    }

    /** Invoked when the mouse button has been clicked (pressed and released) on a component. */
    @Override
    public void mouseClicked(
            java.awt.event.MouseEvent e) {
    }

    /** Invoked when the mouse enters a component. */
    @Override
    public void mouseEntered(
            java.awt.event.MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    @Override
    public void mouseExited(
            java.awt.event.MouseEvent e) {
    }

    /** Invoked when an action occurs. 保存ismail的状态。 */
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object tmpObj = e.getSource();
        if (tmpObj == btnCategories) {
            // new TypeDialog(dlg, true).setVisible(true);
            // if(categoryDialog == null)
            // {
            CategoryDialog tmpDlg = new CategoryDialog(dlg, fldCategories.getText());
            tmpDlg.show();
            // }
            // else
            // {
            // categoryDialog.show(categoriesField.getText());
            // }
            if (tmpDlg.isModified()) {
                fldCategories.setText(tmpDlg.getCategories());
                dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.CATEGORY), tmpDlg.getCategories());
            }
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        switch (swingInvoker) {
            case 1:
                swingInvoker = 0;
                // 设置焦点默认落点
                fldCode.grabFocus();
                break;
            case 3:
                swingInvoker = 0;
                break;

            default:
                break;
        }
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    @Override
    public void release() {
        if (fldCode != null) {
            fldCode.setNextFocusableComponent(null);
        }
        if (btnCategories != null)
            btnCategories.removeActionListener(this);
        if (areComment != null)
            areComment.setNextFocusableComponent(null);
    }

    /**
     * 初始化对话框
     * 
     * @param width
     *            对话框宽度
     * @param height
     *            对话框高度
     */
    void init() {
        setBorder(null);

        panel = new JPanel();
        lblCode = new JLabel(InputDlgConst.CODE);
        fldCode = new JTextField();
        lblProdName = new JLabel(InputDlgConst.NAME);
        cmbProdName = new JComboBox();
        lblAmount = new JLabel(InputDlgConst.AMOUNT);
        fldAmount = new JTextField();
        lblTime = new JLabel(InputDlgConst.TIME);
        clbTime = new CalendarCombo();
        lblEmployee = new JLabel(InputDlgConst.EMPLOYEE);
        cmdEmployee = new JComboBox();
        lblTotlePrice = new JLabel(InputDlgConst.TOLTALPRICE);
        fldTotlePrice = new JTextField();
        lblOthercost = new JLabel(InputDlgConst.OTHERCOST);
        fldOthercost = new JTextField();
        lblArrearage = new JLabel(InputDlgConst.ARREARAGE);
        fldArrearage = new JTextField();
        lblComment = new JLabel(InputDlgConst.COMMENT);
        areComment = new JTextArea();
        scrComment = new JScrollPane(areComment);
        btnCategories = new JButton(DlgConst.CATEGORIES);
        fldCategories = new JTextField();

        // 属性设置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// 面板的滚动策略设置.
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.setLayout(null);
        lblCode.setFont(CustOpts.custOps.getFontOfDefault());
        lblCode.setDisplayedMnemonic('l');
        lblCode.setLabelFor(fldCode);
        lblProdName.setFont(CustOpts.custOps.getFontOfDefault());
        lblProdName.setDisplayedMnemonic('m');
        lblProdName.setLabelFor(cmbProdName);
        lblAmount.setFont(CustOpts.custOps.getFontOfDefault());
        lblAmount.setDisplayedMnemonic('i');
        lblAmount.setLabelFor(fldAmount);
        lblTime.setFont(CustOpts.custOps.getFontOfDefault());
        lblTime.setDisplayedMnemonic('j');
        lblTime.setLabelFor(clbTime);
        lblEmployee.setFont(CustOpts.custOps.getFontOfDefault());
        lblTotlePrice.setFont(CustOpts.custOps.getFontOfDefault());
        clbTime.setBorder(fldAmount.getBorder());
        areComment.setLineWrap(true);
        scrComment.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        btnCategories.setFont(CustOpts.custOps.getFontOfDefault());
        btnCategories.setMnemonic('g');

        // ------------------------------------------------------
        setContent();

        // 设置Tab建焦点顺序。
        fldCode.setNextFocusableComponent(cmbProdName);
        cmbProdName.setNextFocusableComponent(fldAmount);
        fldAmount.setNextFocusableComponent(clbTime);
        clbTime.setNextFocusableComponent(cmdEmployee);
        cmdEmployee.setNextFocusableComponent(fldTotlePrice);
        btnCategories.setNextFocusableComponent(fldCategories);
        fldCategories.setNextFocusableComponent(areComment);

        swingInvoker = 1;
        SwingUtilities.invokeLater(this);

        // 搭建=============================================================
        panel.add(lblCode);
        panel.add(fldCode);
        panel.add(lblProdName);
        panel.add(cmbProdName);
        panel.add(lblAmount);
        panel.add(fldAmount);
        panel.add(lblTime);
        panel.add(clbTime);
        panel.add(lblEmployee);
        panel.add(cmdEmployee);
        panel.add(lblTotlePrice);
        panel.add(fldTotlePrice);
        panel.add(lblOthercost);
        panel.add(fldOthercost);
        panel.add(lblArrearage);
        panel.add(fldArrearage);
        panel.add(lblComment);
        panel.add(scrComment);
        panel.add(btnCategories);
        panel.add(fldCategories);

        setViewportView(panel);

        // 添加监听器======================================================================
        btnCategories.addActionListener(this);
        addMouseListener(this);
    }

    void setContent() {
        initForeignKeys();
        // ID
        Object tObj = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.ID));
        if (tObj != null)
            fldCode.setText(String.valueOf(((Integer) tObj).intValue()));
        else
            fldCode.setText(null);
        // ProductID
        tObj = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.PRODUCTID));
        if (tObj != null) {
            int tProdID = ((Integer) tObj).intValue();
            for (int i = 0; i < productIDAry.length; i++)
                if (tProdID == productIDAry[i]) {
                    cmbProdName.setSelectedIndex(i);
                    break;
                }
        }
        // Amount
        tObj = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.AMOUNT));
        if (tObj != null)
            fldAmount.setText(String.valueOf(((Integer) tObj).intValue()));
        else
            fldAmount.setText(null);
        // Time
        tObj = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.TIME));
        if (tObj != null)
            clbTime.setSelectedItem(tObj);
        else {
            clbTime.setSelectedItem(null);
            clbTime.setTimeText("");
        }
        // employeeID
        tObj = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.EMPLOYEEID));
        if (tObj != null) {
            int tProdID = ((Integer) tObj).intValue();
            for (int i = 0; i < employeeIDAry.length; i++)
                if (tProdID == employeeIDAry[i]) {
                    cmdEmployee.setSelectedIndex(i);
                    break;
                }
        }
        // toltalprice
        tObj = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.TOLTALPRICE));
        if (tObj != null)
            fldTotlePrice.setText(String.valueOf(((Integer) tObj).floatValue() / 100));
        else
            fldTotlePrice.setText(null);
        // otherCost
        tObj = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.OTHERCOST));
        if (tObj != null)
            fldOthercost.setText(String.valueOf(((Integer) tObj).floatValue() / 100));
        else
            fldOthercost.setText(null);
        // arrearage
        tObj = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.ARREARAGE));
        if (tObj != null)
            fldArrearage.setText(String.valueOf(((Integer) tObj).floatValue() / 100));
        else
            fldArrearage.setText(null);
        // content
        areComment.setText((String) dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.CONTENT)));
        // categary
        fldCategories.setText((String) dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.CATEGORY)));
    }

    boolean isValueChanged() {
        // 备注----------------------------
        Object tmpValue1 = areComment.getText();
        Object tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.CONTENT));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 类别------------------------------
        tmpValue1 = fldCategories.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.CATEGORY));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 日期信息----------------------------
        tmpValue1 = clbTime.getSelectedItem();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.TIME));
        if ((tmpValue1 != null && !tmpValue1.equals(tmpValue2)) || (tmpValue1 == null && tmpValue2 != null))
            return true;
        // 产品ID-----------------------------------------
        tmpValue1 =
                cmbProdName.getSelectedIndex() >= 0 ? Integer.valueOf(productIDAry[cmbProdName.getSelectedIndex()])
                        : "";
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.PRODUCTID));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 雇员ID-----------------------------------------
        tmpValue1 =
                cmdEmployee.getSelectedIndex() >= 0 ? Integer.valueOf(employeeIDAry[cmdEmployee.getSelectedIndex()])
                        : "";
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.EMPLOYEEID));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 数量信息-----------------------------------------
        tmpValue1 = fldAmount.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.AMOUNT));
        if ((!tmpValue1.equals(String.valueOf(tmpValue2)))
                && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 价格信息-----------------------------------------
        tmpValue1 = fldTotlePrice.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.TOLTALPRICE));
        tmpValue2 = (tmpValue2 == null) ? "" : String.valueOf(((Integer) tmpValue2).floatValue() / 100);
        if (!tmpValue1.equals(tmpValue2))
            return true;
        // 其他开销-----------------------------------------
        tmpValue1 = fldOthercost.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.OTHERCOST));
        tmpValue2 = (tmpValue2 == null) ? "" : String.valueOf(((Integer) tmpValue2).floatValue() / 100);
        if (!tmpValue1.equals(tmpValue2))
            return true;
        // arrearage
        tmpValue1 = fldArrearage.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(InputDefaultViews.ARREARAGE));
        tmpValue2 = (tmpValue2 == null) ? "" : String.valueOf(((Integer) tmpValue2).floatValue() / 100);
        if (!tmpValue1.equals(tmpValue2))
            return true;

        return false;
    }

    private void initForeignKeys() {
        // for the Employee
        String sql = "select ID, SUBJECT from Employee".concat(dlg.newFlag ? " where DELETED != true" : "");
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            employeeIDAry = new int[tmpPos];
            employeeSubjectAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                employeeIDAry[tmpPos] = rs.getInt("ID");
                employeeSubjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        cmdEmployee.setModel(new DefaultComboBoxModel(employeeSubjectAry));
        cmdEmployee.setSelectedIndex(-1);

        // for the service.
        sql = "select ID, SUBJECT from Product".concat(dlg.newFlag ? " where DELETED != true" : "");
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            productIDAry = new int[tmpPos];
            productSubjectAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                productIDAry[tmpPos] = rs.getInt("ID");
                productSubjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        cmbProdName.setModel(new DefaultComboBoxModel(productSubjectAry));
        cmbProdName.setSelectedIndex(-1);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     */
    void reLayout() {
        // 左边一半（称为第一区域）的最左侧标签的宽度。因为标签的字应该尽量全部显示，所以所有标签的宽度向最宽者看齐。并加个gap。
        final int temLableWidthLeft =
                CASDialogKit.getMaxWidth(new JComponent[] { lblCode, lblProdName, lblAmount, lblTime, lblEmployee,
                        lblTotlePrice })
                        + CustOpts.HOR_GAP;
        // 第一区域的TextField等组件的宽度。先假设空间够左右布局，发现不够的话设定布局模式为上下布局。
        int temFieldWidthLeft = getWidth() / 2 - temLableWidthLeft - 2 * CustOpts.HOR_GAP;// 减去Label宽和两头的缩进。
        boolean tmpIsVerLayout = temFieldWidthLeft < 150;
        if (tmpIsVerLayout)
            temFieldWidthLeft = getWidth() - temLableWidthLeft - 4 * CustOpts.HOR_GAP;// 减去Label宽和两头的缩进。

        final int tmpXPosOfArea1 = CustOpts.HOR_GAP;
        final int tmpYPosOfArea1 = CustOpts.VER_GAP;
        final int tmpXPosOfArea2 = getWidth() / 2 + tmpXPosOfArea1;

        // 产品编号
        lblCode.setBounds(tmpXPosOfArea1, tmpYPosOfArea1, temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldCode.setBounds(lblCode.getX() + temLableWidthLeft, lblCode.getY(), temFieldWidthLeft, CustOpts.BTN_HEIGHT);
        if (tmpIsVerLayout) {
            // 时间
            lblTime.setBounds(lblCode.getX(), lblCode.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            clbTime.setBounds(lblTime.getX() + temLableWidthLeft, lblTime.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 营业员
            lblEmployee.setBounds(lblTime.getX(), lblTime.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            cmdEmployee.setBounds(lblEmployee.getX() + temLableWidthLeft, lblEmployee.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 产品名称
            lblProdName.setBounds(lblEmployee.getX(), lblEmployee.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            cmbProdName.setBounds(lblProdName.getX() + temLableWidthLeft, lblProdName.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 数量
            lblAmount.setBounds(lblProdName.getX(), lblProdName.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldAmount.setBounds(lblAmount.getX() + temLableWidthLeft, lblAmount.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 总价
            lblTotlePrice.setBounds(lblAmount.getX(), lblAmount.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            fldTotlePrice.setBounds(lblTotlePrice.getX() + temLableWidthLeft, lblTotlePrice.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 客户
            lblOthercost.setBounds(lblTotlePrice.getX(), lblTotlePrice.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            fldOthercost.setBounds(lblOthercost.getX() + temLableWidthLeft, lblOthercost.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 欠款
            lblArrearage.setBounds(lblOthercost.getX(), lblOthercost.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            fldArrearage.setBounds(lblArrearage.getX() + temLableWidthLeft, lblArrearage.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
        } else {
            // 产品名称
            lblProdName.setBounds(tmpXPosOfArea2, lblCode.getY(), temLableWidthLeft, CustOpts.BTN_HEIGHT);
            cmbProdName.setBounds(lblProdName.getX() + temLableWidthLeft, lblProdName.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 时间
            lblTime.setBounds(lblCode.getX(), lblCode.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            clbTime.setBounds(lblTime.getX() + temLableWidthLeft, lblTime.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 数量
            lblAmount.setBounds(lblProdName.getX(), lblProdName.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldAmount.setBounds(lblAmount.getX() + temLableWidthLeft, lblAmount.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);

            // 雇员
            lblEmployee.setBounds(lblTime.getX(), lblTime.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            cmdEmployee.setBounds(lblEmployee.getX() + temLableWidthLeft, lblEmployee.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 总价
            lblTotlePrice.setBounds(lblAmount.getX(), lblAmount.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            fldTotlePrice.setBounds(lblTotlePrice.getX() + temLableWidthLeft, lblTotlePrice.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 客户
            lblOthercost.setBounds(lblEmployee.getX(), lblEmployee.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            fldOthercost.setBounds(lblOthercost.getX() + temLableWidthLeft, lblOthercost.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 欠款
            lblArrearage.setBounds(lblTotlePrice.getX(), lblTotlePrice.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            fldArrearage.setBounds(lblArrearage.getX() + temLableWidthLeft, lblArrearage.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);

        }

        // 备注－－－－－－－－－－－－
        int tmpNotePaneHeight = CustOpts.BTN_HEIGHT + 2 * CustOpts.VER_GAP;
        // @NOTE：故意少减了20，可能因为ScrollPane下面留有滚动条的高度？
        int tmpNotePaneHeight2 =
                getHeight() - lblArrearage.getY() - 2 * CustOpts.BTN_HEIGHT - 3 * CustOpts.VER_GAP + 20;
        if (tmpNotePaneHeight2 > tmpNotePaneHeight)// 如果为横向则高度从下面算起，确保组件占满屏幕。
            tmpNotePaneHeight = tmpNotePaneHeight2;

        lblComment.setBounds(tmpXPosOfArea1, lblArrearage.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.LBL_HEIGHT);
        scrComment.setBounds(lblComment.getX() + temLableWidthLeft, lblComment.getY(), fldTotlePrice.getX()
                + fldTotlePrice.getWidth() - lblComment.getX() - temLableWidthLeft, tmpNotePaneHeight);
        // 类别
        btnCategories.setBounds(lblComment.getX(), scrComment.getY() + scrComment.getHeight() + CustOpts.VER_GAP,
                btnCategories.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        fldCategories.setBounds(btnCategories.getX() + btnCategories.getWidth() + CustOpts.HOR_GAP,
                btnCategories.getY(),
                scrComment.getX() + scrComment.getWidth() - btnCategories.getX() - btnCategories.getWidth()
                        - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        panel.setPreferredSize(new Dimension(getWidth(), btnCategories.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP));// 放在滚动面板上的Panel组件必须设置Prefered尺寸，否则无效。

        validate();
    }

    /**
     * @called by :EmployeeDlg, when ok button is clicked. 员工编号不需要记,系统自动分配的RecID即为员工号,以后允许它跟部门等信息组合形成比较好看的形式.
     */
    void saveData() {
        // 第一区域信息保存========================================================================================
        // 保存产品编号。------------------------
        Object tmpTextInField = fldCode.getText();
        // if (tmpTextInField != null)
        // dlg.putValue((PIMPool.pool.getKey(InputDefaultViews.ID)), tmpTextInField);
        // 保存产品名称。-----------------------
        tmpTextInField = cmbProdName.getSelectedItem();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.PRODUCTID),
                    Integer.valueOf(productIDAry[cmbProdName.getSelectedIndex()]));
        // 保存数量。------------------------------
        tmpTextInField = fldAmount.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.AMOUNT), Integer.valueOf((String) tmpTextInField));
        else
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.AMOUNT), Integer.valueOf(0));
        // 保存时间。----------------------------
        tmpTextInField = clbTime.getSelectedItem();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.TIME), tmpTextInField);
        // 保存经手人。-----------------------------
        tmpTextInField = cmdEmployee.getSelectedItem();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.EMPLOYEEID),
                    Integer.valueOf(productIDAry[cmdEmployee.getSelectedIndex()]));
        // 保存总价.----------------------------------
        tmpTextInField = fldTotlePrice.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.TOLTALPRICE),
                    Integer.valueOf(CASUtility.getPriceByCent(Double.valueOf((String) tmpTextInField).doubleValue())));
        else
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.TOLTALPRICE), Integer.valueOf(0));
        // 其他开销
        tmpTextInField = fldOthercost.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.OTHERCOST),
                    Integer.valueOf(CASUtility.getPriceByCent(Double.valueOf((String) tmpTextInField).doubleValue())));
        else
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.OTHERCOST), Integer.valueOf(0));
        // 拖欠
        tmpTextInField = fldArrearage.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.ARREARAGE),
                    Integer.valueOf(CASUtility.getPriceByCent(Double.valueOf((String) tmpTextInField).doubleValue())));
        else
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.ARREARAGE), Integer.valueOf(0));
        // 保存备注信息-----------------------------------
        tmpTextInField = areComment.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.CONTENT), tmpTextInField);
        // 保存categories信息----------------------------
        tmpTextInField = fldCategories.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(InputDefaultViews.CATEGORY), tmpTextInField);
    }

    // variables-----------------------------------------------------------------------------------------------------------------------
    InputDlg dlg;// @called by: self; ArrowButton;

    // 第一区域-------------------------------------------------------
    private JLabel lblCode;
    JTextField fldCode;
    private JLabel lblProdName;
    private JComboBox cmbProdName;
    private JLabel lblAmount;
    JTextField fldAmount;
    private JLabel lblTime;
    private CalendarCombo clbTime;
    private JLabel lblEmployee;
    private JComboBox cmdEmployee;
    private JLabel lblTotlePrice;
    JTextField fldTotlePrice;
    private JTextArea areComment;
    private JLabel lblOthercost;
    JTextField fldOthercost;
    private JLabel lblArrearage;
    JTextField fldArrearage;
    private JLabel lblComment;
    private JScrollPane scrComment;
    private JButton btnCategories;
    private JTextField fldCategories;

    // -----------------------------------------------

    // 因为三个ArrowButton的右键上的item中的文字有重复的，导致interArrowButton方法中不能直接以文字匹配。
    // 加此标志，指示当前是哪一个ArrowButton被按下。同一个ArrowButton的右键菜单中的项是不可能重复的。
    private int swingInvoker;
    private JPanel panel;

    int[] employeeIDAry;
    String[] employeeSubjectAry;
    int[] productIDAry;
    String[] productSubjectAry;
}
