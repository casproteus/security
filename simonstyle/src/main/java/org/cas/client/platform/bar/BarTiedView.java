package org.cas.client.platform.bar;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimview.AbstractPIMView;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pos.dialog.PosDlgConst;

public class BarTiedView extends AbstractPIMView implements MouseListener, FocusListener, KeyListener,
        ComponentListener, Releasable, PropertyChangeListener {
    /**
     * @called by: AbstractAppPane 生成一个新的View实例，但是这时候View是空的，没有关于IApplication或ViewInfo信息的。 所以预览区域不能初始化－－涉及对话盒panel信息。
     */
    public BarTiedView() {
        general = new JPanel();
        lblTitle = new JLabel(PosDlgConst.Title);
        lblProdNumber = new JLabel(PosDlgConst.ProdNumber);
        lblProdName = new JLabel(PosDlgConst.ProdName);
        lblCount = new JLabel(PosDlgConst.Count);
        lblPrice = new JLabel(PosDlgConst.Price);
        lblTotlePrice = new JLabel(PosDlgConst.Subtotal);
        lblCustomer = new JLabel(PosDlgConst.Customer);
        lblPackage = new JLabel(PosDlgConst.Package);
        lblNote = new JLabel(PosDlgConst.Note);

        tfdTitle = new JTextField();
        tfdProdNumber = new JTextField();
        tfdProdName = new JTextField();
        tfdCount = new JTextField();
        tfdPrice = new JTextField();
        tfdTotlePrice = new JTextField();
        tfdCustomer = new JTextField();
        tfdPackage = new JTextField();
        tarNote = new JTextArea();

        btnOffDuty = new JButton(PosDlgConst.OffDuty);
        btnCheck = new JButton(PosDlgConst.Check);
        btnMUser = new JButton(PosDlgConst.MUser);
        btnMProd = new JButton(PosDlgConst.Hangup);
        btnMRate = new JButton(PosDlgConst.MRate);
        btnStatic = new JButton(PosDlgConst.Static);
        btnOption = new JButton(PosDlgConst.Option);
        btnDelete = new JButton(PosDlgConst.Input);
        btnClear = new JButton(PosDlgConst.Refund);

        tblRecs = new PIMTable();
        lblCalculate = new JLabel(PosDlgConst.Calculate);
        tfdShoudReceive = new JTextField();
        lblUnit = new JLabel(PosDlgConst.Unit);
        lblReceive = new JLabel(PosDlgConst.Receive);
        tfdActuallyReceive = new JTextField();
        lblMoneyType = new JLabel(PosDlgConst.MoneyType);
        tfdMoneyType = new JTextField();
        lblChange = new JLabel(PosDlgConst.Change);
        tfdChange = new JTextField();

        // properties
        general.setLayout(null);
        general.setBackground(Color.black);
        lblTitle.setBackground(Color.black);
        lblProdNumber.setBackground(Color.black);
        lblProdName.setBackground(Color.black);
        lblCount.setBackground(Color.black);
        lblPrice.setBackground(Color.black);
        lblTotlePrice.setBackground(Color.black);
        lblCustomer.setBackground(Color.black);
        lblPackage.setBackground(Color.black);
        lblNote.setBackground(Color.black);

        tfdTitle.setBackground(Color.black);
        tfdProdNumber.setBackground(Color.black);
        tfdProdName.setBackground(Color.black);
        tfdCount.setBackground(Color.black);
        tfdPrice.setBackground(Color.black);
        tfdTotlePrice.setBackground(Color.black);
        tfdCustomer.setBackground(Color.black);
        tfdPackage.setBackground(Color.black);
        tarNote.setBackground(Color.black);

        btnOffDuty.setBackground(Color.black);
        btnCheck.setBackground(Color.black);
        btnMUser.setBackground(Color.black);
        btnMProd.setBackground(Color.black);
        btnMRate.setBackground(Color.black);
        btnStatic.setBackground(Color.black);
        btnOption.setBackground(Color.black);
        btnDelete.setBackground(Color.black);
        btnClear.setBackground(Color.black);

        tblRecs.setBackground(Color.black);
        lblCalculate.setBackground(Color.black);
        tfdShoudReceive.setBackground(Color.black);
        lblUnit.setBackground(Color.black);
        lblReceive.setBackground(Color.black);
        tfdActuallyReceive.setBackground(Color.black);
        lblMoneyType.setBackground(Color.black);
        tfdMoneyType.setBackground(Color.black);
        lblChange.setBackground(Color.black);
        tfdChange.setBackground(Color.black);

        // built
        general.add(lblTitle);
        general.add(lblProdNumber);
        general.add(lblProdName);
        general.add(lblCount);
        general.add(lblPrice);
        general.add(lblTotlePrice);
        general.add(lblCustomer);
        general.add(lblPackage);
        general.add(lblNote);

        general.add(tfdTitle);
        general.add(tfdProdNumber);
        general.add(tfdProdName);
        general.add(tfdCount);
        general.add(tfdPrice);
        general.add(tfdTotlePrice);
        general.add(tfdCustomer);
        general.add(tfdPackage);
        general.add(tarNote);

        general.add(btnOffDuty);
        general.add(btnCheck);
        general.add(btnMUser);
        general.add(btnMProd);
        general.add(btnMRate);
        general.add(btnStatic);
        general.add(btnOption);
        general.add(btnDelete);
        general.add(btnClear);

        general.add(tblRecs);
        general.add(lblCalculate);
        general.add(tfdShoudReceive);
        general.add(lblUnit);
        general.add(lblReceive);
        general.add(tfdActuallyReceive);
        general.add(lblMoneyType);
        general.add(tfdMoneyType);
        general.add(lblChange);
        general.add(tfdChange);

        add(general);
        // lblTitle;
        // lblProdNumber;
        // lblProdName;
        // lblCount;
        // lblPrice;
        // lblTotlePrice;
        // lblCustomer;
        // lblPackage;
        // lblNote;
        //
        // tfdTitle;
        // tfdProdNumber;
        // tfdProdName;
        // tfdCount;
        // tfdPrice;
        // tfdTotlePrice;
        // tfdCustomer;
        // tfdPackage;
        // tarNote;
        //
        // btnOffDuty;
        // btnCheck;
        // btnMUser;
        // btnMProd;
        // btnMRate;
        // btnStatic;
        // btnOption;
        // btnDelete;
        // btnClear;
        //
        // tblRecs;
        // lblCalculate;
        // tfdShoudReceive;
        // lblUnit;
        // lblReceive;
        // tfdActuallyReceive;
        // lblMoneyType;
        // tfdMoneyType;
        // lblChange;
        // tfdChange;
    }

    // 实现IView的接口-------------------------------------------------------------------
    public void setIconAndTitle(
            Icon prmIcon,
            String prmTitle) {
    }

    public static int titlePaneHeight = 20;// 默认标题高度

    /** 自我更新一下 */
    public void updatePIMUI() {
    }

    /**
     * 用来处理表格头中的鼠标点击事件, 在某些应用视图为 PIMTable 表格时,表格头的鼠标动作,会激发排序,列宽度尺寸等 发生变化,表格会在适当时候调用本方法以保存该项信息
     * 
     * @param changedType
     *            见本类的几个常量
     * @param changedInfo
     *            封装变化的信息 在排序时,代表一个 int [2], 第一个为表格头上的排序列的索引,第二个为表示升降序标 志, 因为表格头目前尚未提供鼠标操作后得到排序信息的方法暂如此.其他几种变化目前似乎 用这个参数
     * @called by 如HeaderListener(目前)
     */
    public void updateTableInfo(
            int changedType,
            Object changedInfo) {
    }

    /** 更新Model */
    public void viewToModel() {
    }

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的ID的方法
     * 
     * @return 所有选中的记录的ID
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public int[] getSelectedRecordIDs() {
        return null;
    }

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的方法
     * 
     * @return 所有选中的记录
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public Vector getSelectedRecords() {
        return null;
    }

    /**
     * 应控制的要求,需要有选取视图中的所有记录的方法
     * 
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public void seleteAllRecords() {
    }

    /**
     * 处理行高/字体问题,本方法在视图更新和字体对话盒操作后,以及本类init方法中调用,以处理所有字体和前景色 对于以下几个应用取到的视图显示的记录列表时，列表的最后几个字段依次为：
     * ModelConstants.INBOX_APP ModelDBConstants.FOLOWUPENDTIME ModelDBConstants.READED
     * 
     * ModelConstants.CONTACT_APP ModelDBConstants.FOLOWUPENDTIME ModelDBConstants.READED
     * 
     * ModelConstants.TASK_APP ModelDBConstants.END_TIME ModelDBConstants.FINISH_FLAG ModelDBConstants.READED
     * 
     * ModelConstants.CALENDAR_APP ModelDBConstants.CALENDAR_END_TIM
     * 
     * 对于已删除记录最后两个字段为INFOLDER字段和APPTYPE字段，为区分绘制已删除项中各条记录的ICON图标
     */
    public void updateFontsAndColor() {
    }

    /**
     * called by PIMApplication,when 调此方法:对Table视图实现上下翻页,对文本视图实现按日期翻页,卡片 视图实现左右翻页.
     */
    public void setPageBack(
            boolean prmIsBack) {
    }

    public void closed() {
    }

    // 实现MouseListener接口------------------------------------------------------------------
    /**
     * Invoked when the mouse button has been clicked (pressed and released) on the ViewPort 或者 PIMTable.
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
     * Invoked when a mouse button has been pressed on the ViewPort 或者 PIMTable.
     * 问题在于如果仅仅在这个方法内部改变系统的"有记录选中"状态的话,那么键盘等其它硬件输入设备触发的选中将没有相应的系统状态调整. 为了能够一劳永逸地应付所有的输入设备,我们应该选择在更底层地UI中找合适地监控点.
     * 这样做的缺点是:1/系统内部的模块化粒度变粗.2/会不会有这种情况:二次开发厂商希望通过二次开发选中一条记录,但不影响系统状态.
     * 目前认为这两个缺点不构成问题:View认识了控制的问题,可以通过后续版本的进一步模块化完成,改成监听器模式.二次开发厂商应该用更好的方式 达到自己的目的,而不应该希望系统发生不合理的机制.
     * 所以,对于本视图,我们选择在table的SelectionModelListener中设点.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on the ViewPort 或者 PIMTable.
     * 
     * @NOTE: 如果正在table的单元格的编辑器中编辑时,触发了Table的mouse方法,
     *        必定已经触发过了CellEditorListener接口中的editingStopped,即是说:已经触发过viewToModel(存盘方法).
     *        而如果正在快速编辑栏上编辑时,触发了Table或viewport的mouse方法,则还没有存盘过.
     *        如果正在table的编辑器中编辑,点击viewPort触发本方法,则没有触发过editingStopped方法,也即没有存过盘.
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
    }

    // 实现FocusListener的接口------------------------------------------------------------
    // readed by : 只有联系人视图中的表格是可以编辑的.
    /**
     * Invoked when a component gains the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusGained(
            FocusEvent e) {
    }

    /**
     * Invoked when a component loses the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusLost(
            FocusEvent e) {
    }

    // 实现ComponentLisener的接口-------------------------------------------------------
    /** Invoked when a key has been Typed. */
    public void keyTyped(
            KeyEvent e) {
    }

    /** 处理table视图上的上下键盘事件。@NTOE:本方法中的处理和mousePressed方法比较类似. */
    public void keyPressed(
            KeyEvent e) {
    }

    /** Invoked when a key has been released. */
    public void keyReleased(
            KeyEvent e) {
    }

    // 实现ComponentLisener的接口-------------------------------------------------------
    /**
     * Invoked when the component has been made invisible.
     * 
     * @param e
     *            组件变化事件
     */
    public void componentHidden(
            ComponentEvent e) {
    }

    /**
     * Invoked when the component's position changes.
     * 
     * @param e
     *            组件变化事件
     */
    public void componentMoved(
            ComponentEvent e) {
    }

    /**
     * Invoked when the component's size changes.
     * 
     * @param e
     *            组件变化事件
     */
    public void componentResized(
            ComponentEvent e) {
    }

    /**
     * Invoked when the component has been made visible.
     * 
     * @param e
     *            组件变化事件
     */
    public void componentShown(
            ComponentEvent e) {
    }

    // 实现propertyChangeListener接口------------------------------------------------------------------
    /**
     * This method gets called when a bound property is changed. 保存新设的分隔值
     * 
     * @param e
     *            属性事件
     */
    public void propertyChange(
            PropertyChangeEvent e) {
    }

    // 实现Releaseable接口---------------------------------------------------------------------------
    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     */
    public void release() {
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    public void setBounds(
            int prmX,
            int prmY,
            int prmWidth,
            int prmHeight) {
        super.setBounds(prmX, prmY, prmWidth, prmHeight);
        int tWidth = lblTitle.getPreferredSize().width;
        lblTitle.setBounds((prmWidth - tWidth) / 2, CustOpts.VER_GAP, tWidth, lblTitle.getPreferredSize().height);
        int tHalfHeight = (prmHeight - lblTitle.getY() - lblTitle.getHeight()) / 2;
        int tBtnYPos = lblTitle.getY() + lblTitle.getHeight() + tHalfHeight;
        int tBtnWidht = (prmWidth - CustOpts.HOR_GAP * 10);
        int tBtnHeight = prmHeight / 20;
        int tGap = tHalfHeight / 11;
        int tFieldWidth1 = prmWidth / 2;
        lblProdNumber.setBounds(CustOpts.HOR_GAP, lblTitle.getY() + lblTitle.getHeight() + tGap,
                lblProdNumber.getPreferredSize().width, tGap);
        tfdProdNumber.setBounds(lblProdNumber.getX() + lblProdNumber.getWidth(), lblProdNumber.getY(), tFieldWidth1,
                tGap);
        lblProdName.setBounds(lblProdNumber.getX(), lblProdNumber.getY() + 2 * tGap,
                lblProdName.getPreferredSize().width, tGap);
        tfdProdName.setBounds(lblProdName.getX() + lblProdName.getWidth(), lblProdName.getY(), tFieldWidth1, tGap);
        lblCount.setBounds(lblProdName.getX(), lblProdName.getY() + 2 * tGap, lblCount.getPreferredSize().width, tGap);
        tfdCount.setBounds(lblCount.getX() + lblCount.getWidth(), lblCount.getY(), tFieldWidth1, tGap);
        lblPrice.setBounds(lblCount.getX(), lblCount.getY() + 2 * tGap, lblPrice.getPreferredSize().width, tGap);
        tfdPrice.setBounds(lblPrice.getX() + lblPrice.getWidth(), lblPrice.getY(), tFieldWidth1, tGap);
        lblTotlePrice.setBounds(lblPrice.getX(), lblPrice.getY() + 2 * tGap, lblTotlePrice.getPreferredSize().width,
                tGap);
        tfdTotlePrice.setBounds(lblTotlePrice.getX() + lblTotlePrice.getWidth(), lblTotlePrice.getY(), tFieldWidth1,
                tGap);

        lblCustomer.setBounds(tfdProdNumber.getX() + tfdProdNumber.getWidth() + CustOpts.HOR_GAP, tfdProdNumber.getY(),
                lblCustomer.getPreferredSize().width, tGap);
        tfdCustomer.setBounds(lblCustomer.getX() + lblCustomer.getWidth(), lblCustomer.getY(),
                prmWidth - lblCustomer.getX() - lblCustomer.getWidth() - CustOpts.HOR_GAP, tGap);
        lblPackage.setBounds(lblCustomer.getX(), lblCustomer.getY() + 2 * tGap, lblPackage.getPreferredSize().width,
                tGap);
        tfdPackage.setBounds(lblPackage.getX() + lblPackage.getWidth(), lblPackage.getY(), prmWidth - lblPackage.getX()
                - lblPackage.getWidth() - CustOpts.HOR_GAP, tGap);
        lblNote.setBounds(lblPackage.getX(), lblPackage.getY() + 2 * tGap, lblNote.getPreferredSize().width, tGap);
        tarNote.setBounds(lblNote.getX() + lblNote.getWidth(), lblNote.getY(),
                prmWidth - lblNote.getX() - lblNote.getWidth() - CustOpts.HOR_GAP, tGap * 5);

        btnOffDuty.setBounds(CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);
        btnCheck.setBounds(btnOffDuty.getX() + tBtnWidht + CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);
        btnMUser.setBounds(btnCheck.getX() + tBtnWidht + CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);
        btnMProd.setBounds(btnMUser.getX() + tBtnWidht + CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);
        btnMRate.setBounds(btnMProd.getX() + tBtnWidht + CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);
        btnStatic.setBounds(btnMRate.getX() + tBtnWidht + CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);
        btnOption.setBounds(btnStatic.getX() + tBtnWidht + CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);
        btnDelete.setBounds(btnOption.getX() + tBtnWidht + CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);
        btnClear.setBounds(btnDelete.getX() + tBtnWidht + CustOpts.HOR_GAP, tBtnYPos, tBtnWidht, tBtnHeight);

        tblRecs.setBounds(CustOpts.HOR_GAP, btnOffDuty.getY() + btnOffDuty.getHeight() + tGap, prmWidth * 3 / 5,
                tHalfHeight - btnOffDuty.getHeight() - tGap - CustOpts.VER_GAP);
        lblCalculate.setBounds(tblRecs.getX() + tblRecs.getWidth() + CustOpts.HOR_GAP, tblRecs.getY(), prmWidth
                - tblRecs.getWidth() - CustOpts.HOR_GAP * 3, tGap);
        tfdShoudReceive.setBounds(lblCalculate.getX(), lblCalculate.getY() + lblCalculate.getHeight(),
                lblCalculate.getWidth() * 2 / 3, (tblRecs.getHeight() - lblCalculate.getHeight()) / 3);
        lblUnit.setBounds(tfdShoudReceive.getX() + tfdShoudReceive.getWidth(), tfdShoudReceive.getY(),
                lblCalculate.getWidth() - tfdShoudReceive.getWidth(), tfdShoudReceive.getHeight());
        lblReceive.setBounds(tfdShoudReceive.getX(), tfdShoudReceive.getY() + tfdShoudReceive.getHeight(),
                lblCalculate.getWidth() / 3, tfdShoudReceive.getHeight() / 2);
        tfdActuallyReceive.setBounds(lblReceive.getX() + lblReceive.getWidth(), lblReceive.getY(),
                lblCalculate.getWidth() - lblReceive.getWidth(), lblReceive.getHeight());
        lblMoneyType.setBounds(lblReceive.getX(), lblReceive.getY() + lblReceive.getHeight(), lblReceive.getWidth(),
                lblReceive.getHeight());
        tfdMoneyType.setBounds(lblMoneyType.getX() + lblMoneyType.getWidth(), lblMoneyType.getY(),
                tfdActuallyReceive.getWidth(), lblMoneyType.getHeight());
        lblChange.setBounds(lblMoneyType.getX(), lblMoneyType.getY() + lblMoneyType.getHeight(),
                lblMoneyType.getWidth(), tblRecs.getHeight() - lblMoneyType.getY() - lblMoneyType.getHeight());
        tfdChange.setBounds(lblChange.getX() + lblChange.getWidth(), lblChange.getY(), tfdMoneyType.getWidth(),
                lblChange.getHeight());
    }

    JPanel general;

    JLabel lblTitle;
    JLabel lblProdNumber;
    JLabel lblProdName;
    JLabel lblCount;
    JLabel lblPrice;
    JLabel lblTotlePrice;
    JLabel lblCustomer;
    JLabel lblPackage;
    JLabel lblNote;

    JTextField tfdTitle;
    JTextField tfdProdNumber;
    JTextField tfdProdName;
    JTextField tfdCount;
    JTextField tfdPrice;
    JTextField tfdTotlePrice;
    JTextField tfdCustomer;
    JTextField tfdPackage;
    JTextArea tarNote;

    JButton btnOffDuty;
    JButton btnCheck;
    JButton btnMUser;
    JButton btnMProd;
    JButton btnMRate;
    JButton btnStatic;
    JButton btnOption;
    JButton btnDelete;
    JButton btnClear;

    PIMTable tblRecs;
    JLabel lblCalculate;
    JTextField tfdShoudReceive;
    JLabel lblUnit;
    JLabel lblReceive;
    JTextField tfdActuallyReceive;
    JLabel lblMoneyType;
    JTextField tfdMoneyType;
    JLabel lblChange;
    JTextField tfdChange;
}
