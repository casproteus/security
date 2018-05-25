package org.cas.client.platform.employee.dialog;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalComboBoxEditor;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.casbeans.PIMImage;
import org.cas.client.platform.casbeans.calendar.CalendarCombo;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.ImageToFile;
import org.cas.client.platform.casutil.PIMDBUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.employee.EmployeeDefaultViews;
import org.cas.client.resource.international.DlgConst;

/** 联系人对话框 */
class EmployeeGeneralPanel extends JScrollPane implements FocusListener, MouseListener, ActionListener, Runnable,
        Releasable {
    /**
     * Creates a new instance of ContactGeneralPanel 新建和编辑联系人对话框
     * 
     * @param prmDlg
     *            父窗体
     */
    EmployeeGeneralPanel(EmployeeDlg prmDlg) {
        dlg = prmDlg;
    }

    /**
     * Invoked when a component gains the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusGained(
            FocusEvent e) {
        Object source = e.getSource();
        if (source == cmbLanguage) {
            Vector tmpVec = new Vector();
            prepareCurrentFileAsVec(tmpVec);
            tmpVec.add(dlg.getValue(PIMPool.pool.getKey(3)));
            fileAsModel = new DefaultComboBoxModel(tmpVec);
            cmbLanguage.setModel(fileAsModel);
        }
    }

    /** Invoked when an action occurs. */
    public void focusLost(
            FocusEvent e) {
        Object source = e.getSource();
        // 新建联系人时，lastField、firstField、companyField失去焦点时，应调整提示域中的内容。----------------------------------------
        // @NOTE: chinesenameField失去焦点时，也调整提示域中的内容，但放在contactDetailPanel中处理了。
        if (source == fldLastName || source == fldFirstName || source == fldNickName || source == fldTitle)
            reprepairFileAsVec();
    }

    /** 当ArrowButton被按下时，记下拿一个Button被按。 */
    public void mousePressed(
            java.awt.event.MouseEvent e) {
    }

    /** Invoked when a mouse button has been released on a component. */
    public void mouseReleased(
            java.awt.event.MouseEvent e) {
    }

    /** Invoked when the mouse button has been clicked (pressed and released) on a component. */
    public void mouseClicked(
            java.awt.event.MouseEvent e) {
    }

    /** Invoked when the mouse enters a component. */
    public void mouseEntered(
            java.awt.event.MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            java.awt.event.MouseEvent e) {
    }

    /** Invoked when an action occurs. 保存ismail的状态。 */
    public void actionPerformed(
            ActionEvent e) {
        Object tmpObj = e.getSource();
        if (tmpObj == photo) {
            JFileChooser tmpFileChooser = new JFileChooser();
            tmpFileChooser.setAcceptAllFileFilterUsed(false);
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("jpg"));
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("gif"));
            // tmpFileChooser.setOpenFilter(filter);
            if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
                return;
            File photoFile = tmpFileChooser.getSelectedFile();
            // tmpFileChooser.clearMem();
            if (photoFile != null) {
                photo.setBorder(null);
                photo.setText(CASUtility.EMPTYSTR);
                String fileName = processPhoto(photoFile.getAbsolutePath());
                dlg.putValue(PIMPool.pool.getKey(19), fileName);
            }
        } else if (tmpObj == btnCategories) {
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
            if (tmpDlg.isModified())
                if (tmpDlg.isModified()) {
                    fldCategories.setText(tmpDlg.getCategories());
                    dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.CATEGORY), tmpDlg.getCategories());
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
    public void run() {
        switch (swingInvoker) {
            case 1:
                swingInvoker = 0;
                fldEmployID.grabFocus(); // 设置焦点默认落点
                break;
            case 2:
                swingInvoker = 0;
//                if (fileAsVec.size() > 0) {
//                    int tmpRealIndex = showedFileAsMap[CustOpts.custOps.getDisplayAsOrder()];
//                    cmbLanguage.setSelectedIndex(tmpRealIndex == -1 ? 0 : tmpRealIndex);
//                }
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
    public void release() {
        if (fldLastName != null) {
            fldLastName.removeFocusListener(this);
            fldLastName.setNextFocusableComponent(null);
        }
        if (fldFirstName != null)
            fldFirstName.removeFocusListener(this);
        if (fldNickName != null)
            fldNickName.removeFocusListener(this);
        if (cmbLanguage != null)
            cmbLanguage.removeFocusListener(this);
        if (cbxIsmFemale != null)
            cbxIsmFemale.removeActionListener(this);
        if (btnRelation != null)
            btnRelation.removeActionListener(this);
        if (photo != null)
            photo.removeActionListener(this);
        if (btnCategories != null)
            btnCategories.removeActionListener(this);
        if (areComment != null)
            areComment.setNextFocusableComponent(null);
    }

    boolean isValueChanged() {
        Object tmpValue1 = fldLastName.getText();
        Object tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.FNAME));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // firstName。-----------------------
        tmpValue1 = fldFirstName.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.NAME));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // filsAs--------------------------
        tmpValue1 = ((JTextField) ((MetalComboBoxEditor) cmbLanguage.getEditor()).getEditorComponent()).getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.SUBJECT));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 昵称------------------------------
        tmpValue1 = fldNickName.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.NNAME));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // Title----------------------------
        tmpValue1 = fldTitle.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.TITLE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // email-----------------------------
        tmpValue1 = fldEmail.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.EMAIL));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // web信息。---------------------------------
        tmpValue1 = fldWebPage.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.WEBPAGE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 备注信息-----------------------------------
        tmpValue1 = areComment.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.CONTENT));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // categories信息----------------------------
        tmpValue1 = fldCategories.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.CATEGORY));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;

        // 第二区域的信息===============================================================================
        // 生日信息----------------------------
        tmpValue1 = clbBirthday.getSelectedItem();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.BIRTHDAY));
        if ((tmpValue1 != null && !tmpValue1.equals(tmpValue2)) || (tmpValue1 == null && tmpValue2 != null))
            return true;
        // 纪念日信息-----------------------------------------
        tmpValue1 = clbAnniversary.getSelectedItem();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.ANNIVERSARY));
        if ((tmpValue1 != null && !tmpValue1.equals(tmpValue2)) || (tmpValue1 == null && tmpValue2 != null))
            return true;
        // 纪念日类型------------------------------------
        tmpValue1 = lblAnniversary.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.ANNIVERSARYTYPE));
        if (!tmpValue1.equals(tmpValue2) && tmpValue2 != null)
            return true;
        // 加入时间
        tmpValue1 = clbJointime.getSelectedItem();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.JOINTIME));
        if ((tmpValue1 != null && !tmpValue1.equals(tmpValue2)) || (tmpValue1 == null && tmpValue2 != null))
            return true;
        // 保存家庭住址信息-----------------------------------------
        tmpValue1 = areAddress.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.ADDRESS));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存宅电信息-----------------------------------------
        tmpValue1 = fldPhone.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.PHONE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存移动电话信息-----------------------------------------
        tmpValue1 = fldMobile.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.CPHONE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存银行帐号信息-----------------------------------------
        tmpValue1 = fldBankNumber.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.BANKNUMBER));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存社保卡号信息-----------------------------------------
        tmpValue1 = fldSSCNumber.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.SSCNUMBER));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存薪资信息-----------------------------------------
        tmpValue1 = fldSalary.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.SALARY));
        tmpValue2 = (tmpValue2 == null) ? "" : String.valueOf(((Integer) tmpValue2).floatValue() / 100);
        if (!tmpValue1.equals(tmpValue2))
            return true;
        // 保存保险金信息-----------------------------------------
        tmpValue1 = fldInsurance.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.INSURANCE));
        tmpValue2 = (tmpValue2 == null) ? "" : String.valueOf(((Integer) tmpValue2).floatValue() / 100);
        if (!tmpValue1.equals(tmpValue2))
            return true;
        // 保存登录密码信息-----------------------------------------
        tmpValue1 = fldPassword.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.PASSWORD));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存关系信息------------------------------
        tmpValue1 = fldRelation.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.RELATION));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;

        return false;
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
        // 第一区域----------------------------------------------------------
        lblEmployID = new JLabel(BarFrame.consts.EMPLOYEEID());
        fldEmployID = new JTextField();
        cbxIsmFemale = new JCheckBox(BarFrame.consts.IS_LADY(), false);

        // 上部信息块-----------------------
        photo = new PIMImage(BarFrame.consts.PHOTO());
        lblLastName = new JLabel(BarFrame.consts.LASTNAME());
        fldLastName = new JTextField();
        lblFirstName = new JLabel(BarFrame.consts.FIRSTNAME());
        fldFirstName = new JTextField();
        lblDisplayAs = new JLabel(BarFrame.consts.DISPLAYAS());
        cmbLanguage = new JComboBox(); // @NOTE:初始化显示model中存的值，它在每次被点开时会用model中的值加上所有的可能组合作为ComboBox的Model。
        lblNickname = new JLabel(BarFrame.consts.NICKNAME());
        fldNickName = new JTextField();
        lblTitle = new JLabel(BarFrame.consts.JOB());
        fldTitle = new JTextField();
        lblEmail = new JLabel(BarFrame.consts.EMAIL());
        fldEmail = new JTextField();
        lblWebPage = new JLabel(BarFrame.consts.WEBPAGE());
        fldWebPage = new JTextField();
        areComment = new JTextArea();
        scrComment = new JScrollPane(areComment);
        btnCategories = new JButton(DlgConst.CATEGORIES);
        fldCategories = new JTextField();

        // 第二区域----------------------------------------------------------------------
        lblBirthday = new JLabel(BarFrame.consts.BIRTHDAY());
        clbBirthday = new CalendarCombo();
        lblAnniversary = new JLabel(BarFrame.consts.ANNIVERSARY2());
        clbAnniversary = new CalendarCombo();
        arbAnniversary = new EmployeeArrowButton(this);
        lblJointime = new JLabel(BarFrame.consts.JOIN_TIME());
        clbJointime = new CalendarCombo();
        lblAddress = new JLabel(BarFrame.consts.ADDRESS());
        areAddress = new JTextArea();
        scrAddress = new JScrollPane(areAddress);
        lblPhone = new JLabel(BarFrame.consts.PHONE());
        fldPhone = new JTextField();
        lblMobile = new JLabel(BarFrame.consts.MOBILE());
        fldMobile = new JTextField();
        lblBankNumber = new JLabel(BarFrame.consts.BANKNUMBER());
        fldBankNumber = new JTextField();
        lblSSCNumber = new JLabel(BarFrame.consts.SSCN());
        fldSSCNumber = new JTextField();
        lblSalary = new JLabel(BarFrame.consts.SALARY());
        fldSalary = new JTextField();
        lblPriceUnit1 = new JLabel(BarOption.MoneySign);
        lblInsurance = new JLabel(BarFrame.consts.INSURANCE());
        fldInsurance = new JTextField();
        lblPriceUnit2 = new JLabel(BarOption.MoneySign);
        lblPasswrod = new JLabel(BarFrame.consts.PASSWORD());
        fldPassword = new JPasswordField();
        btnRelation = new JButton(BarFrame.consts.RELATION());
        fldRelation = new JTextField();

        // 属性设置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        // 第一区域---------------------------------------------------
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// 面板的滚动策略设置.
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.setLayout(null);
        fldEmployID.setFont(CustOpts.custOps.getFontOfDefault());// 地址信息块
        cbxIsmFemale.setFont(CustOpts.custOps.getFontOfDefault());
        cbxIsmFemale.setMnemonic('r');
        // photo.setText(CONTACT_PHOTO);
        // photo.setPictureAlignment(2);
        photo.setFont(CustOpts.custOps.getFontOfDefault());
        lblLastName.setFont(CustOpts.custOps.getFontOfDefault());
        lblLastName.setDisplayedMnemonic('l');
        lblLastName.setLabelFor(fldLastName);
        lblFirstName.setFont(CustOpts.custOps.getFontOfDefault());
        lblFirstName.setDisplayedMnemonic('m');
        lblFirstName.setLabelFor(fldFirstName);
        lblDisplayAs.setFont(CustOpts.custOps.getFontOfDefault());
        lblDisplayAs.setDisplayedMnemonic('f');
        lblDisplayAs.setLabelFor(cmbLanguage);
        cmbLanguage.setFont(CustOpts.custOps.getFontOfDefault());
        cmbLanguage.setEditable(true);
        lblNickname.setFont(CustOpts.custOps.getFontOfDefault());
        lblNickname.setDisplayedMnemonic('i');
        lblNickname.setLabelFor(fldNickName);
        lblTitle.setFont(CustOpts.custOps.getFontOfDefault());
        lblTitle.setDisplayedMnemonic('j');
        lblTitle.setLabelFor(fldTitle);
        lblEmail.setFont(CustOpts.custOps.getFontOfDefault());
        lblWebPage.setFont(CustOpts.custOps.getFontOfDefault());
        lblWebPage.setDisplayedMnemonic('a');
        lblWebPage.setLabelFor(fldWebPage);
        areComment.setLineWrap(true);
        scrComment.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        btnCategories.setFont(CustOpts.custOps.getFontOfDefault());
        btnCategories.setMnemonic('g');
        // 第二区域-------------------------------
        lblBirthday.setFont(CustOpts.custOps.getFontOfDefault());
        lblBirthday.setDisplayedMnemonic('z');
        lblBirthday.setLabelFor(clbBirthday);
        clbBirthday.setBorder(fldCategories.getBorder());
        lblAnniversary.setFont(CustOpts.custOps.getFontOfDefault());
        lblAnniversary.setDisplayedMnemonic('d');
        lblAnniversary.setLabelFor(clbAnniversary);
        clbAnniversary.setBorder(clbBirthday.getBorder());
        arbAnniversary.setItems(BarFrame.consts.ANNIVERSARYTYPE());
        arbAnniversary.setArrowButtonLabel(lblAnniversary);
        lblJointime.setFont(CustOpts.custOps.getFontOfDefault());
        lblJointime.setDisplayedMnemonic('q');
        lblJointime.setLabelFor(clbJointime);
        clbJointime.setBorder(clbAnniversary.getBorder());
        lblAddress.setFont(CustOpts.custOps.getFontOfDefault());
        lblAddress.setDisplayedMnemonic('b');
        lblAddress.setLabelFor(areAddress);
        areAddress.setLineWrap(true);
        scrAddress.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        lblPhone.setFont(CustOpts.custOps.getFontOfDefault());
        lblPhone.setDisplayedMnemonic('u');
        fldPhone.setEditable(true);
        lblPhone.setLabelFor(fldPhone);
        fldPhone.setFont(CustOpts.custOps.getFontOfDefault());
        lblMobile.setFont(CustOpts.custOps.getFontOfDefault());
        lblBankNumber.setFont(CustOpts.custOps.getFontOfDefault());
        lblPasswrod.setFont(CustOpts.custOps.getFontOfDefault());
        btnRelation.setFont(CustOpts.custOps.getFontOfDefault());
        btnRelation.setMnemonic('c');

        // ------------------------------------------------------
        setContent();

        // 设置Tab建焦点顺序。
        fldEmployID.setNextFocusableComponent(cbxIsmFemale);
        cbxIsmFemale.setNextFocusableComponent(fldLastName);
        fldLastName.setNextFocusableComponent(fldFirstName);
        fldFirstName.setNextFocusableComponent(fldNickName);
        fldNickName.setNextFocusableComponent(fldTitle);
        fldTitle.setNextFocusableComponent(fldEmail);
        fldEmail.setNextFocusableComponent(fldWebPage);

        fldWebPage.setNextFocusableComponent(areComment);
        areComment.setNextFocusableComponent(btnCategories);
        btnCategories.setNextFocusableComponent(fldCategories);
        fldCategories.setNextFocusableComponent(clbBirthday);
        clbBirthday.setNextFocusableComponent(clbAnniversary);
        clbAnniversary.setNextFocusableComponent(clbJointime);
        clbJointime.setNextFocusableComponent(areAddress);
        areAddress.setNextFocusableComponent(fldPhone);

        fldPhone.setNextFocusableComponent(fldMobile);
        fldMobile.setNextFocusableComponent(fldBankNumber);
        fldBankNumber.setNextFocusableComponent(fldSSCNumber);
        fldSSCNumber.setNextFocusableComponent(fldSalary);
        fldSalary.setNextFocusableComponent(fldInsurance);
        fldPassword.setNextFocusableComponent(btnRelation);
        btnRelation.setNextFocusableComponent(fldRelation);
        fldRelation.setNextFocusableComponent(fldEmployID);
        cbxIsmFemale.setNextFocusableComponent(photo);

        swingInvoker = 1;
        SwingUtilities.invokeLater(this);

        // 搭建=============================================================
        panel.add(lblEmployID);
        panel.add(fldEmployID);
        panel.add(cbxIsmFemale);
        panel.add(photo);
        panel.add(lblLastName);
        panel.add(fldLastName);
        panel.add(lblFirstName);
        panel.add(fldFirstName);
        panel.add(lblDisplayAs);
        panel.add(cmbLanguage);
        panel.add(lblNickname);
        panel.add(fldNickName);
        panel.add(lblTitle);
        panel.add(fldTitle);
        panel.add(lblEmail);
        panel.add(fldEmail);
        panel.add(lblWebPage);
        panel.add(fldWebPage);
        panel.add(scrComment);
        panel.add(btnCategories);
        panel.add(fldCategories);

        panel.add(lblBirthday);
        panel.add(clbBirthday);
        panel.add(arbAnniversary);
        panel.add(lblAnniversary);
        panel.add(clbAnniversary);
        panel.add(lblJointime);
        panel.add(clbJointime);
        panel.add(lblAddress);
        panel.add(scrAddress);
        panel.add(lblPhone);
        panel.add(fldPhone);
        panel.add(lblMobile);
        panel.add(fldMobile);
        panel.add(lblBankNumber);
        panel.add(fldBankNumber);
        panel.add(lblSSCNumber);
        panel.add(fldSSCNumber);
        panel.add(lblSalary);
        panel.add(fldSalary);
        panel.add(lblPriceUnit1);
        panel.add(lblInsurance);
        panel.add(fldInsurance);
        panel.add(lblPriceUnit2);
        panel.add(lblPasswrod);
        panel.add(fldPassword);
        panel.add(btnRelation);
        panel.add(fldRelation);

        setViewportView(panel);

        // 添加监听器======================================================================
        cbxIsmFemale.addActionListener(this);
        photo.addActionListener(this);
        btnCategories.addActionListener(this);
        arbAnniversary.addMouseListener(this);
        btnRelation.addActionListener(this);

        fldLastName.addFocusListener(this);
        fldFirstName.addFocusListener(this);
        fldNickName.addFocusListener(this);
        fldTitle.addFocusListener(this);
        if (!dlg.newFlag)
            cmbLanguage.addFocusListener(this);
        addMouseListener(this);
    }

    void setContent() {
        // 第一区域--------------------------------------------------------
        fldEmployID.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.CODE)));
        if (fldEmployID.getText().length() > 0) {
            fldEmployID.setOpaque(false);
            fldEmployID.setBorder(null);
        }
        // sex
        Object tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.SEX));
        if (tObj != null)
            cbxIsmFemale.setSelected(((Boolean) tObj).booleanValue());
        else
            cbxIsmFemale.setSelected(false);
        // photo
        tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.PHOTO));
        if (tObj == null) {
            photo.setPicture(null);
            photo.setText(BarFrame.consts.PHOTO());
        } else {
            String fileName =
                    PIMDBUtil.decodeByteArrayToImage((byte[]) tObj,
                            ((Integer) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.ID))).intValue());
            photo.setPicture(fileName);
            photo.setBorder(null);
            photo.setText(CASUtility.EMPTYSTR);
        }
        // Fname
        fldLastName.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.FNAME))); // personal field
        fldFirstName.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.NAME)));
        // FileAs
//        fileAsVec = new Vector();
//        fileAsVec.add();
        fileAsModel = new DefaultComboBoxModel(BarFrame.consts.langs()); // 给fileAsBox赋值
        cmbLanguage.setModel(fileAsModel);
        ((JTextField) ((MetalComboBoxEditor) cmbLanguage.getEditor()).getEditorComponent()).setText((String)dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.SUBJECT)));

        fldNickName.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.NNAME)));
        fldTitle.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.TITLE)));
        fldEmail.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.EMAIL)));
        tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.CTYPE));
        fldWebPage.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.WEBPAGE)));
        areComment.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.CONTENT)));
        fldCategories.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.CATEGORY)));

        // 第二区域---------------------------------------------------------------
        tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.BIRTHDAY));
        if (tObj != null)
            clbBirthday.setSelectedItem(tObj);
        else {
            clbBirthday.setSelectedItem(null);
            clbBirthday.setTimeText("");
        }
        tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.ANNIVERSARY));
        if (tObj != null)
            clbAnniversary.setSelectedItem(tObj);
        else {
            clbAnniversary.setSelectedItem(null);
            clbAnniversary.setTimeText("");
        }
        tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.ANNIVERSARYTYPE));
        lblAnniversary.setText(tObj == null ? BarFrame.consts.ANNIVERSARY1() : (String) tObj);
        tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.JOINTIME));
        if (tObj != null)
            clbJointime.setSelectedItem(tObj);
        else {
            clbJointime.setSelectedItem(null);
            clbJointime.setTimeText("");
        }
        areAddress.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.ADDRESS)));
        fldPhone.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.PHONE)));
        fldMobile.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.CPHONE)));
        fldBankNumber.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.BANKNUMBER)));
        fldSSCNumber.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.SSCNUMBER)));
        tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.SALARY));
        if (tObj != null)
            fldSalary.setText(String.valueOf(((Integer) tObj).floatValue() / 100));
        else
            fldSalary.setText(CASUtility.EMPTYSTR);
        tObj = dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.INSURANCE));
        if (tObj != null)
            fldInsurance.setText(String.valueOf(((Integer) tObj).floatValue() / 100));
        else
            fldInsurance.setText(CASUtility.EMPTYSTR);

        fldPassword.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.PASSWORD)));
        fldRelation.setText((String) dlg.getValue(PIMPool.pool.getKey(EmployeeDefaultViews.RELATION)));
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     */
    void reLayout() {
        // 左边一半（称为第一区域）的最左侧标签的宽度。因为标签的字应该尽量全部显示，所以所有标签的宽度向最宽者看齐。并加个gap。
        final int temLableWidthLeft =
                CASDialogKit.getMaxWidth(new JComponent[] { lblEmployID, lblBirthday, lblAnniversary, lblJointime,
                        lblAddress, lblPhone })
                        + CustOpts.HOR_GAP;
        // 第一区域的TextField等组件的宽度。先假设空间够左右布局，发现不够的话设定布局模式为上下布局。
        int temFieldWidthLeft = getWidth() / 2 - temLableWidthLeft - 4 * CustOpts.HOR_GAP;// 减去Label宽和两头的缩进。
        boolean tmpIsVerLayout = temFieldWidthLeft < 150;
        if (tmpIsVerLayout)
            temFieldWidthLeft = getWidth() - temLableWidthLeft - 6 * CustOpts.HOR_GAP;// 减去Label宽和两头的缩进。

        final int tmpXPosOfArea1 = CustOpts.HOR_GAP;
        final int tmpYPosOfArea1 = CustOpts.VER_GAP;
        final int tmpXPosOfArea2 = !tmpIsVerLayout ? getWidth() / 2 + CustOpts.HOR_GAP : CustOpts.HOR_GAP;
        final int tmpYPosOfArea2 =
                !tmpIsVerLayout ? lblEmployID.getY() : btnCategories.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP;

        // 第一区域===============================================================
        // 照片下方的Label的宽度。@NOTE:该变量同时也是photo的宽度。
        final int tmpLblWidthUnderPhoto =
                CustOpts.HOR_GAP + CASDialogKit.getMaxWidth(new JComponent[] { photo, lblWebPage, lblEmail });

        // 照片右边的Label的宽度。
        final int tmpLblWidthBesidPhoto =
                CustOpts.HOR_GAP
                        + CASDialogKit.getMaxWidth(new JComponent[] { lblLastName, lblFirstName, lblDisplayAs,
                                lblNickname, lblTitle });
        // 照片右边的TextField的宽度。
        final int tmpFldWidthBesidPhoto =
                !tmpIsVerLayout ? getWidth() / 2 - tmpLblWidthUnderPhoto - tmpLblWidthBesidPhoto - 4 *CustOpts.HOR_GAP 
                        : getWidth() - CustOpts.HOR_GAP - tmpLblWidthUnderPhoto - tmpLblWidthBesidPhoto - 4 * CustOpts.HOR_GAP;

        // 员工号
        lblEmployID.setBounds(tmpXPosOfArea1, CustOpts.HOR_GAP, lblEmployID.getPreferredSize().width,
                lblEmployID.getPreferredSize().height);
        fldEmployID.setBounds(lblEmployID.getX() + lblEmployID.getWidth(), CustOpts.HOR_GAP, tmpLblWidthUnderPhoto
                - lblEmployID.getWidth() - CustOpts.HOR_GAP, fldEmployID.getPreferredSize().height);
        cbxIsmFemale.setBounds(lblEmployID.getX() + tmpLblWidthUnderPhoto, tmpYPosOfArea1, tmpLblWidthBesidPhoto
                + tmpFldWidthBesidPhoto, CustOpts.BTN_HEIGHT);
        // 照片
        photo.setBounds(lblEmployID.getX(), lblEmployID.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                tmpLblWidthUnderPhoto - CustOpts.VER_GAP, 5 * CustOpts.BTN_HEIGHT + 3 * CustOpts.VER_GAP);
        // 姓氏
        lblLastName.setBounds(photo.getX() + tmpLblWidthUnderPhoto,// + CustOpts.HOR_GAP,
                photo.getY(), tmpLblWidthBesidPhoto, CustOpts.BTN_HEIGHT);
        fldLastName.setBounds(lblLastName.getX() + tmpLblWidthBesidPhoto, lblLastName.getY(), tmpFldWidthBesidPhoto,
                CustOpts.BTN_HEIGHT);
        // 名字
        lblFirstName.setBounds(lblLastName.getX(), lblLastName.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                tmpLblWidthBesidPhoto, CustOpts.BTN_HEIGHT);
        fldFirstName.setBounds(lblFirstName.getX() + tmpLblWidthBesidPhoto, lblFirstName.getY(), tmpFldWidthBesidPhoto,
                CustOpts.BTN_HEIGHT);
        // 表示为
        lblDisplayAs.setBounds(lblFirstName.getX(), lblFirstName.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                tmpLblWidthBesidPhoto, CustOpts.BTN_HEIGHT);
        cmbLanguage.setBounds(lblDisplayAs.getX() + tmpLblWidthBesidPhoto, lblDisplayAs.getY(), tmpFldWidthBesidPhoto,
                CustOpts.BTN_HEIGHT);
        // 规格变动:要求可写
        // 昵称
        lblNickname.setBounds(lblDisplayAs.getX(), lblDisplayAs.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                tmpLblWidthBesidPhoto, CustOpts.BTN_HEIGHT);
        fldNickName.setBounds(lblNickname.getX() + tmpLblWidthBesidPhoto, lblNickname.getY(), tmpFldWidthBesidPhoto,
                CustOpts.BTN_HEIGHT);
        // 职务
        lblTitle.setBounds(lblNickname.getX(), lblNickname.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                tmpLblWidthBesidPhoto, CustOpts.BTN_HEIGHT);
        fldTitle.setBounds(lblTitle.getX() + tmpLblWidthBesidPhoto, lblTitle.getY(), tmpFldWidthBesidPhoto,
                CustOpts.BTN_HEIGHT);
        // 电子邮件
        lblEmail.setBounds(photo.getX(), fldTitle.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, lblEmail
                .getPreferredSize().width > lblWebPage.getPreferredSize().width ? lblEmail.getPreferredSize().width
                : lblWebPage.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        fldEmail.setBounds(lblEmail.getX() + lblEmail.getWidth() + CustOpts.HOR_GAP,
        		lblEmail.getY(), 
        		fldTitle.getX() + fldTitle.getWidth() - lblEmail.getX() - lblEmail.getWidth() - CustOpts.HOR_GAP,
        		CustOpts.BTN_HEIGHT);
        // Web页地址
        lblWebPage.setBounds(lblEmail.getX(), lblEmail.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblEmail.getWidth(), CustOpts.BTN_HEIGHT);
        fldWebPage.setBounds(fldEmail.getX(), lblWebPage.getY(), fldEmail.getWidth(), CustOpts.BTN_HEIGHT);

        // 备注－－－－－－－－－－－－
        int tmpNotePaneHeight = 2 * +CustOpts.BTN_HEIGHT + CustOpts.VER_GAP;
        if (!tmpIsVerLayout)// 如果纵向的话街道高度固定为上值,否则进行下面的计算。
        { // @NOTE：故意少减了20，可能因为ScrollPane下面留有滚动条的高度？
            int tmpNotePaneHeight2 =
                    getHeight() - lblWebPage.getY() - 2 * CustOpts.BTN_HEIGHT - 3 * CustOpts.VER_GAP + 20;
            if (tmpNotePaneHeight2 > tmpNotePaneHeight)// 如果为横向则高度从下面算起，确保组件占满屏幕。
                tmpNotePaneHeight = tmpNotePaneHeight2;
        }
        scrComment.setBounds(lblWebPage.getX(), lblWebPage.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblWebPage.getWidth() + CustOpts.HOR_GAP + fldWebPage.getWidth(), tmpNotePaneHeight);
        // 类别
        btnCategories.setBounds(scrComment.getX(), scrComment.getY() + scrComment.getHeight() + CustOpts.VER_GAP,
                btnCategories.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        fldCategories.setBounds(btnCategories.getX() + btnCategories.getWidth() + CustOpts.HOR_GAP,
                btnCategories.getY(), scrComment.getWidth() - btnCategories.getWidth() - CustOpts.HOR_GAP,
                CustOpts.BTN_HEIGHT);

        // 第二区域===============================================================
        // 生日
        lblBirthday.setBounds(tmpXPosOfArea2, tmpYPosOfArea2, temLableWidthLeft, CustOpts.BTN_HEIGHT);
        clbBirthday.setBounds(lblBirthday.getX() + temLableWidthLeft, lblBirthday.getY(), fldWebPage.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 纪念日
        lblAnniversary.setBounds(lblBirthday.getX(), lblBirthday.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        clbAnniversary.setBounds(lblAnniversary.getX() + temLableWidthLeft, lblAnniversary.getY(),
                clbBirthday.getWidth(), CustOpts.BTN_HEIGHT);
        arbAnniversary.setBounds(clbAnniversary.getX() - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT, clbAnniversary.getY(),
                CustOpts.BTN_HEIGHT, CustOpts.BTN_HEIGHT);
        // 加入时间
        lblJointime.setBounds(lblAnniversary.getX(), lblAnniversary.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        clbJointime.setBounds(lblJointime.getX() + temLableWidthLeft, lblJointime.getY(), clbAnniversary.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 住址
        lblAddress.setBounds(lblJointime.getX(), lblJointime.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        int tmpPaneHeight = 2 * CustOpts.BTN_HEIGHT;
        if (!tmpIsVerLayout)// 如果纵向的话街道高度固定为上值,否则进行下面的计算。
        { // 少减了20，可能因为ScrollPane下面留有滚动条的高度？
            tmpPaneHeight = CustOpts.BTN_HEIGHT;
            int tmpPaneHeight2 = getHeight() - lblAddress.getY() - 8 * CustOpts.BTN_HEIGHT - 9 * CustOpts.VER_GAP + 20;
            if (tmpPaneHeight2 > tmpPaneHeight)// 如果为横向则高度从下面算起，确保组件占满屏幕。
                tmpPaneHeight = tmpPaneHeight2;
        }
        scrAddress.setBounds(lblAddress.getX() + temLableWidthLeft, lblAddress.getY(), clbJointime.getWidth(),
                tmpPaneHeight);

        // 住所电话
        lblPhone.setBounds(lblAddress.getX(), lblAddress.getY() + scrAddress.getHeight() + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldPhone.setBounds(lblPhone.getX() + temLableWidthLeft, lblPhone.getY(), scrAddress.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 移动电话
        lblMobile.setBounds(lblPhone.getX(), lblPhone.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblPhone.getWidth(), CustOpts.BTN_HEIGHT);
        fldMobile.setBounds(lblMobile.getX() + temLableWidthLeft, lblMobile.getY(), fldPhone.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 银行卡号
        lblBankNumber.setBounds(lblMobile.getX(), lblMobile.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblMobile.getWidth(), CustOpts.BTN_HEIGHT);
        fldBankNumber.setBounds(lblBankNumber.getX() + temLableWidthLeft, lblBankNumber.getY(), fldMobile.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 社保号
        lblSSCNumber.setBounds(lblBankNumber.getX(), lblBankNumber.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblBankNumber.getWidth(), CustOpts.BTN_HEIGHT);
        fldSSCNumber.setBounds(lblSSCNumber.getX() + temLableWidthLeft, lblSSCNumber.getY(), fldBankNumber.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 工资
        lblSalary.setBounds(lblSSCNumber.getX(), lblSSCNumber.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        int tWidth = getFontMetrics(getFont()).stringWidth(lblPriceUnit1.getText());
        lblPriceUnit1.setBounds(lblSalary.getX() + temLableWidthLeft + fldSSCNumber.getWidth() - tWidth,
                lblSalary.getY(), tWidth, CustOpts.LBL_HEIGHT);
        fldSalary.setBounds(lblSalary.getX() + temLableWidthLeft, lblSalary.getY(), fldSSCNumber.getWidth() - tWidth,
                CustOpts.BTN_HEIGHT);
        // 保险
        lblInsurance.setBounds(lblSalary.getX(), lblSalary.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblSSCNumber.getWidth(), CustOpts.BTN_HEIGHT);
        tWidth = getFontMetrics(getFont()).stringWidth(lblPriceUnit2.getText());
        lblPriceUnit2.setBounds(lblInsurance.getX() + temLableWidthLeft + fldSSCNumber.getWidth() - tWidth,
                lblInsurance.getY(), tWidth, CustOpts.LBL_HEIGHT);
        fldInsurance.setBounds(lblInsurance.getX() + temLableWidthLeft, lblInsurance.getY(), fldSSCNumber.getWidth()
                - tWidth, CustOpts.BTN_HEIGHT);
        // 登录密码
        lblPasswrod.setBounds(lblInsurance.getX(), lblInsurance.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
        		lblInsurance.getWidth(), CustOpts.BTN_HEIGHT);
        fldPassword.setBounds(lblPasswrod.getX() + temLableWidthLeft, lblPasswrod.getY(), fldInsurance.getWidth()
                + lblPriceUnit2.getWidth(), CustOpts.BTN_HEIGHT);
        // 关系
        btnRelation.setBounds(lblPasswrod.getX(), lblPasswrod.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                btnRelation.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        fldRelation.setBounds(btnRelation.getX() + btnRelation.getWidth() + CustOpts.HOR_GAP, btnRelation.getY(),
                fldMobile.getX() + fldMobile.getWidth() - btnRelation.getX() - btnRelation.getWidth()
                        - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        panel.setPreferredSize(new Dimension(getWidth(), btnRelation.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP));// 放在滚动面板上的Panel组件必须设置Prefered尺寸，否则无效。

        validate();
    }

    /**
     * 仅为fileAs ComboBox准备Model。先删除，再重组，最后更新fileAs的视图； @ called by: self; ContactDetailPanel;
     */
    void reprepairFileAsVec() {
        // clear all
//        fileAsVec.removeAllElements();
//        prepareCurrentFileAsVec(fileAsVec);
        swingInvoker = 2;
        // ===================更新内容的显示======================
        SwingUtilities.invokeLater(this);
        // ======================================================
    }

    /**
     * @called by :EmployeeDlg, when ok button is clicked. 员工编号不需要记,系统自动分配的RecID即为员工号,以后允许它跟部门等信息组合形成比较好看的形式.
     */
    void saveData() {
        // 第一区域信息保存========================================================================================
        // 员工号-----------------------
        String tmpTextInField = fldEmployID.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.CODE), tmpTextInField);
        // 性别-----------------------
        dlg.putValue((PIMPool.pool.getKey(EmployeeDefaultViews.SEX)), cbxIsmFemale.isSelected());
        // 照片信息----------------------

        // lastName信息。------------------------
        tmpTextInField = fldLastName.getText();
        if (tmpTextInField != null)
            dlg.putValue((PIMPool.pool.getKey(EmployeeDefaultViews.FNAME)), tmpTextInField);
        // firstName信息。-----------------------
        tmpTextInField = fldFirstName.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.NAME), tmpTextInField);
        // filsAs信息。--------------------------
        tmpTextInField = ((JTextField) ((MetalComboBoxEditor) cmbLanguage.getEditor()).getEditorComponent()).getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.SUBJECT), tmpTextInField);
        // 昵称信息。------------------------------
        tmpTextInField = fldNickName.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.NNAME), tmpTextInField);
        // Title信息。----------------------------
        tmpTextInField = fldTitle.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.TITLE), tmpTextInField);
        // email信息。-----------------------------
        tmpTextInField = fldEmail.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.EMAIL), tmpTextInField);
        // web信息。---------------------------------
        tmpTextInField = fldWebPage.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.WEBPAGE), tmpTextInField);
        // 备注信息-----------------------------------
        tmpTextInField = areComment.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.CONTENT), tmpTextInField);
        // categories信息----------------------------
        tmpTextInField = fldCategories.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.CATEGORY), tmpTextInField);

        // 保存第二区域的信息===============================================================================
        // 生日信息----------------------------
        Object tmpDate = clbBirthday.getSelectedItem();
        if (tmpDate != null)
            dlg.putValue((PIMPool.pool.getKey(EmployeeDefaultViews.BIRTHDAY)), tmpDate);
        // 纪念日信息-----------------------------------------
        tmpDate = clbAnniversary.getSelectedItem();
        if (tmpDate != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.ANNIVERSARY), tmpDate);
        // 纪念日类型------------------------------------
        tmpTextInField = lblAnniversary.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.ANNIVERSARYTYPE), tmpTextInField);
        // 加入时间
        tmpDate = clbJointime.getSelectedItem();
        if (tmpDate != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.JOINTIME), tmpDate);
        // 家庭住址信息-----------------------------------------
        tmpTextInField = areAddress.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.ADDRESS), tmpTextInField);
        // 保存宅电信息-----------------------------------------
        tmpTextInField = fldPhone.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.PHONE), tmpTextInField);
        // 保存移动电话信息-----------------------------------------
        tmpTextInField = fldMobile.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.CPHONE), tmpTextInField);
        // 保存银行帐号信息-----------------------------------------
        tmpTextInField = fldBankNumber.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.BANKNUMBER), tmpTextInField);
        // 保存社保卡号信息-----------------------------------------
        tmpTextInField = fldSSCNumber.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.SSCNUMBER), tmpTextInField);
        // 保存薪资信息-----------------------------------------
        tmpTextInField = fldSalary.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.SALARY),
                    Integer.valueOf(CASUtility.getPriceByCent(Double.valueOf((String) tmpTextInField).doubleValue())));
        else
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.SALARY), Integer.valueOf(0));
        // 保存保险金信息-----------------------------------------
        tmpTextInField = fldInsurance.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.INSURANCE),
                    Integer.valueOf(CASUtility.getPriceByCent(Double.valueOf((String) tmpTextInField).doubleValue())));
        else
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.INSURANCE), Integer.valueOf(0));
        // 保存登录密码信息-----------------------------------------
        tmpTextInField = fldPassword.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.PASSWORD), tmpTextInField);
        // 保存contacts信息------------------------------
        tmpTextInField = fldRelation.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(EmployeeDefaultViews.RELATION), tmpTextInField);
    }

    private void prepareCurrentFileAsVec(
            Vector prmEmptyVec) {
        for (int i = 0; i < 11; i++)
            showedFileAsMap[i] = -1;
        // personal field
        String tmpLastName = fldLastName.getText();
        String tmpFirstName = fldFirstName.getText();
        String tmpNickName = fldNickName.getText();
        String tmpTitle = fldTitle.getText();
        // 准备各字符串是否为有效的标志----------------------------
        boolean isLastNameValid = (tmpLastName != null && tmpLastName.length() > 0);
        boolean isFirstNameValid = (tmpFirstName != null && tmpFirstName.length() > 0);
        boolean isNickNameValid = (tmpNickName != null && tmpNickName.length() > 0);
        boolean isTitleValid = (tmpTitle != null && tmpTitle.length() > 0);

        if (isLastNameValid && isFirstNameValid) {
            if (isNickNameValid && isTitleValid) {
                prmEmptyVec.add(tmpLastName.concat(tmpFirstName).concat(tmpTitle).concat(CASUtility.LEFT_BRACKET)
                        .concat(tmpNickName).concat(CASUtility.RIGHT_BRACKET));
                showedFileAsMap[0] = prmEmptyVec.size() - 1;
            }
            if (isTitleValid) {
                prmEmptyVec.add(tmpLastName.concat(tmpFirstName).concat(tmpTitle));
                showedFileAsMap[1] = prmEmptyVec.size() - 1;
            }
            if (isNickNameValid) {
                prmEmptyVec.add(tmpLastName.concat(tmpFirstName).concat(CASUtility.LEFT_BRACKET).concat(tmpNickName)
                        .concat(CASUtility.RIGHT_BRACKET));
                showedFileAsMap[2] = prmEmptyVec.size() - 1;
            }
            prmEmptyVec.add(tmpLastName.concat(tmpFirstName));
            showedFileAsMap[3] = prmEmptyVec.size() - 1;
        } else if (isLastNameValid) { // 姓字符串为有效的。
            if (isNickNameValid && isTitleValid) {
                prmEmptyVec.add(tmpLastName.concat(tmpTitle).concat(CASUtility.LEFT_BRACKET).concat(tmpNickName)
                        .concat(CASUtility.RIGHT_BRACKET));
                showedFileAsMap[4] = prmEmptyVec.size() - 1;
            }
            if (isTitleValid) {
                prmEmptyVec.add(tmpLastName.concat(tmpTitle));
                showedFileAsMap[5] = prmEmptyVec.size() - 1;
            }
            if (isNickNameValid) {
                prmEmptyVec.add(tmpLastName.concat(CASUtility.LEFT_BRACKET).concat(tmpNickName)
                        .concat(CASUtility.RIGHT_BRACKET));
                showedFileAsMap[6] = prmEmptyVec.size() - 1;
            }
            prmEmptyVec.add(tmpLastName);
            showedFileAsMap[7] = prmEmptyVec.size() - 1;
        } else if (isFirstNameValid) { // 姓字符串为有效的。
            if (isNickNameValid) {
                prmEmptyVec.add(tmpFirstName.concat(CASUtility.LEFT_BRACKET).concat(tmpNickName)
                        .concat(CASUtility.RIGHT_BRACKET));
                showedFileAsMap[8] = prmEmptyVec.size() - 1;
            }
            prmEmptyVec.add(tmpFirstName);
            showedFileAsMap[9] = prmEmptyVec.size() - 1;
        } else if (isNickNameValid) {
            prmEmptyVec.add(tmpNickName);
            showedFileAsMap[10] = prmEmptyVec.size() - 1;
        }
    }

    // 仅处理照片，将大的图片剪切为小图片，方便保存。
    private String processPhoto(
            String fileName) {
        Image image = Toolkit.getDefaultToolkit().getImage(fileName);

        MediaTracker tracker = new MediaTracker(EmployeeGeneralPanel.this);
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ex) {
            ErrorUtil.write(ex);
        }
        image = photo.recreateImage(fileName, image);
        java.io.File temp = null;
        try {
            temp = java.io.File.createTempFile("TMP", ".png", PIMDBUtil.getPIMTempDirectory());
            ImageToFile.imageToFile(image, temp.getAbsolutePath());
        } catch (java.io.IOException ie) {
            // ie.printStackTrace();
        }

        if (temp != null) {
            return temp.toString();
        }

        return null;
    }

    // 计算ArrowButton中的菜单项的宽度。
    private int getNeedWidth(
            String[] array) {
        FontMetrics fm = getFontMetrics(getFont());
        int width = 0;
        for (int i = 0; i < array.length; i++) {
            int tempWidth = fm.stringWidth(array[i]);
            if (width < tempWidth) {
                width = tempWidth;
            }
        }
        return width + 1;
    }

    // private String[] getFileAsBoxItem()
    // {
    // firstAndLast = (String)dlg.getValue(PIMPool.pool.getIntegerKey(EmployeeDefaultViewstants.FIRST_NAME))
    // + (String)dlg.getValue(PIMPool.pool.getIntegerKey(EmployeeDefaultViewstants.LAST_NAME));
    // companyName = (String)dlg.getValue(PIMPool.pool.getIntegerKey(EmployeeDefaultViewstants.UNIT));
    // fileAsitems = new String[]
    // {
    // firstAndLast,
    // firstAndLast + PIMUtility.LEFT_BRACKET + companyName + PIMUtility.RIGHT_BRACKET,
    // companyName,
    // companyName + PIMUtility.LEFT_BRACKET + firstAndLast + PIMUtility.RIGHT_BRACKET
    // };
    // return fileAsitems;
    // }

    // variables-----------------------------------------------------------------------------------------------------------------------
    EmployeeDlg dlg;// @called by: self; ArrowButton;

    // 第一区域-------------------------------------------------------
    private JLabel lblEmployID;
    JTextField fldEmployID;
    private JCheckBox cbxIsmFemale;
    private PIMImage photo;
    private JLabel lblLastName;
    JTextField fldLastName;
    private JLabel lblFirstName;
    private JTextField fldFirstName;
    private JLabel lblDisplayAs;
    JComboBox cmbLanguage;
    private JLabel lblNickname;
    private JTextField fldNickName;
    private JLabel lblTitle;
    private JTextField fldTitle;

    private JLabel lblEmail;
    private JTextField fldEmail;
    private JLabel lblWebPage;
    private JTextField fldWebPage;
    private JTextArea areComment;
    private JScrollPane scrComment;
    private JButton btnCategories;
    private JTextField fldCategories;

    // 第二区域---------------------------------------------------------
    private JLabel lblBirthday;
    private CalendarCombo clbBirthday;
    private JLabel lblAnniversary;
    private CalendarCombo clbAnniversary;
    private EmployeeArrowButton arbAnniversary;
    private JLabel lblJointime;
    private CalendarCombo clbJointime;
    private JLabel lblAddress;
    private JScrollPane scrAddress;
    private JTextArea areAddress;
    private JLabel lblPhone;
    private JTextField fldPhone;
    private JLabel lblMobile;
    private JTextField fldMobile;
    private JLabel lblBankNumber;
    private JTextField fldBankNumber;
    private JLabel lblSSCNumber;
    private JTextField fldSSCNumber;
    private JLabel lblSalary;
    JTextField fldSalary;
    private JLabel lblPriceUnit1;
    private JLabel lblInsurance;
    JTextField fldInsurance;
    private JLabel lblPriceUnit2;
    private JLabel lblPasswrod;
    private JPasswordField fldPassword;
    private JButton btnRelation;
    private JTextField fldRelation;
    // -----------------------------------------------

    // 因为三个ArrowButton的右键上的item中的文字有重复的，导致interArrowButton方法中不能直接以文字匹配。
    // 加此标志，指示当前是哪一个ArrowButton被按下。同一个ArrowButton的右键菜单中的项是不可能重复的。
    private int QQType;
    private int anniversaryType;

    private DefaultComboBoxModel fileAsModel; // FileAs组件的Model。
    private int swingInvoker;
    // 由于在表示fileAs的组合框的model中只存放有合法的匹配,而且是不按顺序的.用该数组存与Option中设置的映射关系.
    private int[] showedFileAsMap = new int[11];
    private JPanel panel;
}
