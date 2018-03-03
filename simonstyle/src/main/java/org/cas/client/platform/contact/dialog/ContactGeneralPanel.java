package org.cas.client.platform.contact.dialog;

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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalComboBoxEditor;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.PIMImage;
import org.cas.client.platform.casbeans.calendar.CalendarCombo;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.ImageToFile;
import org.cas.client.platform.casutil.PIMDBUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.employee.EmployeeDefaultViews;
import org.cas.client.platform.product.ProductDefaultViews;
import org.cas.client.resource.international.DlgConst;

/** 联系人对话框 */
class ContactGeneralPanel extends JScrollPane implements FocusListener, MouseListener, ActionListener, Runnable,
        Releasable {
    /**
     * Creates a new instance of ContactGeneralPanel 新建和编辑联系人对话框
     * 
     * @param prmDlg
     *            父窗体
     */
    ContactGeneralPanel(ContactDlg prmDlg) {
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
        if (e.getSource() == cmbDisplayAs) {
            Vector tmpVec = new Vector();
            prepareCurrentFileAsVec(tmpVec);
            tmpVec.add(dlg.getValue(PIMPool.pool.getKey(3)));
            fileAsModel = new DefaultComboBoxModel(tmpVec);
            cmbDisplayAs.setModel(fileAsModel);
        }
    }

    /** Invoked when an action occurs. */
    public void focusLost(
            FocusEvent e) {
        Object source = e.getSource();
        // 新建联系人时，lastField、firstField、fldNickName失去焦点时，应调整提示域中的内容。----------------------------------------
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
            CategoryDialog tmpDlg;
            if (dlg.isPartOfView)
                tmpDlg = new CategoryDialog(CASMainFrame.mainFrame, fldCategories.getText());
            else
                tmpDlg = new CategoryDialog(dlg, fldCategories.getText());
            tmpDlg.show();
            // }
            // else
            // {
            // categoryDialog.show(categoriesField.getText());
            // }
            if (tmpDlg.isModified()) {
                fldCategories.setText(tmpDlg.getCategories());
                dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.CATEGORY), tmpDlg.getCategories());
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
                // 设置焦点默认落点
                fldCompName.grabFocus();
                break;

            case 2:
                swingInvoker = 0;
                if (fileAsVec.size() > 0) {
                    int tmpRealIndex = showedFileAsMap[CustOpts.custOps.getDisplayAsOrder()];
                    cmbDisplayAs.setSelectedIndex(tmpRealIndex == -1 ? 0 : tmpRealIndex);
                }
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
        if (cmbDisplayAs != null)
            cmbDisplayAs.removeFocusListener(this);
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
        Object tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.FNAME));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // firstName。-----------------------
        tmpValue1 = fldFirstName.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.NAME));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // filsAs--------------------------
        tmpValue1 = ((JTextField) ((MetalComboBoxEditor) cmbDisplayAs.getEditor()).getEditorComponent()).getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 昵称------------------------------
        tmpValue1 = fldNickName.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.NNAME));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // Title----------------------------
        tmpValue1 = fldTitle.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.TITLE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // email-----------------------------
        tmpValue1 = fldEmail.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.EMAIL));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // QQ号码.----------------------------------
        tmpValue1 = fldQQ.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CNUMBER));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存QQ类型
        tmpValue1 = lblQQ.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CTYPE));
        if (!tmpValue1.equals(tmpValue2) && tmpValue2 != null)
            return true;
        // web信息。---------------------------------
        tmpValue1 = fldWebPage.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.WEBPAGE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 备注信息-----------------------------------
        tmpValue1 = areComment.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CONTENT));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 帐务
        tmpValue1 = fldAccount.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ACCOUNT));
        tmpValue2 = (tmpValue2 == null) ? "" : String.valueOf(((Integer) tmpValue2).floatValue() / 100);
        if (!tmpValue1.equals(tmpValue2))
            return true;
        // categories信息----------------------------
        tmpValue1 = fldCategories.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CATEGORY));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;

        // 保存第二区域的信息===============================================================================
        // 保存生日信息----------------------------
        tmpValue1 = clbBirthday.getSelectedItem();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.BIRTHDAY));
        if ((tmpValue1 != null && !tmpValue1.equals(tmpValue2)) || (tmpValue1 == null && tmpValue2 != null))
            return true;
        // 保存纪念日信息-----------------------------------------
        tmpValue1 = clbAnniversary.getSelectedItem();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ANNIVERSARY));
        if ((tmpValue1 != null && !tmpValue1.equals(tmpValue2)) || (tmpValue1 == null && tmpValue2 != null))
            return true;
        // 保存纪念日类型------------------------------------
        tmpValue1 = lblAnniversary.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ANNIVERSARYTYPE));
        if (!tmpValue1.equals(tmpValue2) && tmpValue2 != null)
            return true;
        // 单位电话
        tmpValue1 = fldComptel.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPTEL));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存家庭住址信息-----------------------------------------
        tmpValue1 = areAddress.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ADDRESS));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存宅电信息-----------------------------------------
        tmpValue1 = fldPhone.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.PHONE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存移动电话信息-----------------------------------------
        tmpValue1 = fldMobile.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CPHONE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存公司名------------------------------------------
        tmpValue1 = fldCompName.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPANY));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存单位邮编信息-----------------------------------------
        tmpValue1 = fldCompPostCode.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPPOSTCODE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保单位地址信息-----------------------------------------
        tmpValue1 = areCompAddress.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPADDR));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存家庭邮编信息-----------------------------------------
        tmpValue1 = fldHomePostCode.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.HOMEPOSTCODE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存公司传真信息-----------------------------------------
        tmpValue1 = fldCompfax.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPFAX));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存关系信息------------------------------
        tmpValue1 = fldRelation.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.RELATION));
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
        // 上部信息块-----------------------
        photo = new PIMImage(ContactDlgConst.PHOTO);
        lblLastName = new JLabel(ContactDlgConst.LASTNAME);
        fldLastName = new JTextField();
        lblFirstName = new JLabel(ContactDlgConst.FIRSTNAME);
        fldFirstName = new JTextField();
        lblDisplayAs = new JLabel(ContactDlgConst.DISPLAYAS);
        cmbDisplayAs = new JComboBox(); // @NOTE:初始化显示model中存的值，它在每次被点开时会用model中的值加上所有的可能组合作为ComboBox的Model。
        lblNickname = new JLabel(ContactDlgConst.NICKNAME);
        fldNickName = new JTextField();
        lblTitle = new JLabel(ContactDlgConst.TITLE);
        fldTitle = new JTextField();
        lblEmail = new JLabel(ContactDlgConst.EMAIL);
        fldEmail = new JTextField();
        lblQQ = new JLabel(ContactDlgConst.QQ1);
        arbQQType = new ContactArrowButton(this);
        fldQQ = new JTextField();
        lblWebPage = new JLabel(ContactDlgConst.WEBPAGE);
        fldWebPage = new JTextField();
        areComment = new JTextArea();
        scrComment = new JScrollPane(areComment);
        lblAccount = new JLabel(ContactDlgConst.ACCOUNT);
        fldAccount = new JTextField();
        btnCategories = new JButton(DlgConst.CATEGORIES);
        fldCategories = new JTextField();

        // 第二区域----------------------------------------------------------------------lblCompName = new
        // JLabel(ContactDlgConst.IDCARD);
        lblCompName = new JLabel(ContactDlgConst.COMPNAME);
        fldCompName = new JTextField();
        lblCompPostCode = new JLabel(ContactDlgConst.COMPPOSTCODE);
        fldCompPostCode = new JTextField();
        lblCompAddress = new JLabel(ContactDlgConst.COMPADDRESS);
        areCompAddress = new JTextArea();
        scrCompAddress = new JScrollPane(areCompAddress);
        lblHomePostCode = new JLabel(ContactDlgConst.HOMEPOSTCODE);
        fldHomePostCode = new JTextField();
        lblAddress = new JLabel(ContactDlgConst.HOMEADDRESS);
        areAddress = new JTextArea();
        scrAddress = new JScrollPane(areAddress);
        lblBirthday = new JLabel(ContactDlgConst.BIRTHDAY);
        clbBirthday = new CalendarCombo();
        lblAnniversary = new JLabel(ContactDlgConst.ANNIVERSARY2);
        clbAnniversary = new CalendarCombo();
        arbAnniversary = new ContactArrowButton(this);
        lblComptel = new JLabel(ContactDlgConst.COMPTEL);
        fldComptel = new JTextField();
        lblCompfax = new JLabel(ContactDlgConst.COMPFAX);
        fldCompfax = new JTextField();
        lblMobile = new JLabel(ContactDlgConst.MOBILE);
        fldMobile = new JTextField();
        lblPhone = new JLabel(ContactDlgConst.PHONE);
        fldPhone = new JTextField();

        btnRelation = new JButton(ContactDlgConst.RELATION);
        fldRelation = new JTextField();

        // 属性设置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        // 第一区域---------------------------------------------------
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// 面板的滚动策略设置.
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.setLayout(null);
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
        lblDisplayAs.setLabelFor(cmbDisplayAs);
        cmbDisplayAs.setFont(CustOpts.custOps.getFontOfDefault());
        cmbDisplayAs.setEditable(true);
        lblNickname.setFont(CustOpts.custOps.getFontOfDefault());
        lblNickname.setDisplayedMnemonic('i');
        lblNickname.setLabelFor(fldNickName);
        lblTitle.setFont(CustOpts.custOps.getFontOfDefault());
        lblTitle.setDisplayedMnemonic('j');
        lblTitle.setLabelFor(fldTitle);
        lblEmail.setFont(CustOpts.custOps.getFontOfDefault());
        lblQQ.setFont(CustOpts.custOps.getFontOfDefault());
        arbQQType.setItems(ContactDlgConst.QQTYPE);
        arbQQType.setArrowButtonLabel(lblQQ);
        lblWebPage.setFont(CustOpts.custOps.getFontOfDefault());
        lblWebPage.setDisplayedMnemonic('a');
        lblWebPage.setLabelFor(fldWebPage);
        areComment.setLineWrap(true);
        scrComment.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        lblAccount.setLabelFor(fldAccount);
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
        arbAnniversary.setItems(ContactDlgConst.ANNIVERSARYTYPE);
        arbAnniversary.setArrowButtonLabel(lblAnniversary);
        lblComptel.setFont(CustOpts.custOps.getFontOfDefault());
        lblComptel.setDisplayedMnemonic('q');
        lblComptel.setLabelFor(fldComptel);
        fldComptel.setBorder(clbAnniversary.getBorder());
        areCompAddress.setLineWrap(true);
        scrCompAddress.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
        lblCompName.setFont(CustOpts.custOps.getFontOfDefault());
        lblCompPostCode.setFont(CustOpts.custOps.getFontOfDefault());
        lblCompfax.setFont(CustOpts.custOps.getFontOfDefault());
        btnRelation.setFont(CustOpts.custOps.getFontOfDefault());
        btnRelation.setMnemonic('c');

        // ------------------------------------------------------
        setContent();

        // 设置Tab建焦点顺序。
        fldCompName.setNextFocusableComponent(fldCompPostCode);
        fldCompPostCode.setNextFocusableComponent(areCompAddress);
        areCompAddress.setNextFocusableComponent(fldHomePostCode);
        fldHomePostCode.setNextFocusableComponent(areAddress);
        areAddress.setNextFocusableComponent(clbBirthday);
        clbBirthday.setNextFocusableComponent(clbAnniversary);
        clbAnniversary.setNextFocusableComponent(fldComptel);
        fldComptel.setNextFocusableComponent(fldCompfax);
        fldCompfax.setNextFocusableComponent(fldMobile);
        fldMobile.setNextFocusableComponent(fldPhone);
        fldPhone.setNextFocusableComponent(btnRelation);
        btnRelation.setNextFocusableComponent(fldRelation);

        fldRelation.setNextFocusableComponent(fldLastName);
        fldLastName.setNextFocusableComponent(fldFirstName);
        fldFirstName.setNextFocusableComponent(fldNickName);
        fldNickName.setNextFocusableComponent(fldTitle);
        fldTitle.setNextFocusableComponent(fldEmail);
        fldEmail.setNextFocusableComponent(fldQQ);
        fldQQ.setNextFocusableComponent(fldWebPage);
        fldWebPage.setNextFocusableComponent(areComment);
        areComment.setNextFocusableComponent(fldAccount);
        fldAccount.setNextFocusableComponent(btnCategories);
        btnCategories.setNextFocusableComponent(fldCategories);
        fldCategories.setNextFocusableComponent(photo);
        photo.setNextFocusableComponent(fldCompName);
        swingInvoker = 1;
        SwingUtilities.invokeLater(this);

        // 搭建=============================================================
        panel.add(lblBirthday);
        panel.add(clbBirthday);
        panel.add(arbAnniversary);// @NOTE:ArrowButton必须先添加,使ArrowButton显示在前面,以保证当Label上文字过长时,也不会绘到按扭上.
        panel.add(lblAnniversary);
        panel.add(clbAnniversary);
        panel.add(lblComptel);
        panel.add(fldComptel);
        panel.add(lblAddress);
        panel.add(scrAddress);
        panel.add(lblPhone);
        panel.add(fldPhone);
        panel.add(lblMobile);
        panel.add(fldMobile);
        panel.add(lblCompName);
        panel.add(fldCompName);
        panel.add(lblCompPostCode);
        panel.add(fldCompPostCode);
        panel.add(lblCompAddress);
        panel.add(scrCompAddress);
        panel.add(lblHomePostCode);
        panel.add(fldHomePostCode);
        panel.add(lblCompfax);
        panel.add(fldCompfax);
        panel.add(btnRelation);
        panel.add(fldRelation);

        panel.add(photo);
        panel.add(lblLastName);
        panel.add(fldLastName);
        panel.add(lblFirstName);
        panel.add(fldFirstName);
        panel.add(lblDisplayAs);
        panel.add(cmbDisplayAs);
        panel.add(lblNickname);
        panel.add(fldNickName);
        panel.add(lblTitle);
        panel.add(fldTitle);
        panel.add(lblEmail);
        panel.add(fldEmail);
        panel.add(arbQQType);// @NOTE:ArrowButton必须先添加,使ArrowButton显示在前面,以保证当Label上文字过长时,也不会绘到按扭上.
        panel.add(lblQQ);
        panel.add(fldQQ);
        panel.add(lblWebPage);
        panel.add(fldWebPage);
        panel.add(scrComment);
        panel.add(lblAccount);
        panel.add(fldAccount);
        panel.add(btnCategories);
        panel.add(fldCategories);

        setViewportView(panel);

        // 添加监听器======================================================================
        photo.addActionListener(this);
        arbQQType.addMouseListener(this);
        btnCategories.addActionListener(this);
        arbAnniversary.addMouseListener(this);
        btnRelation.addActionListener(this);
        fldLastName.addFocusListener(this);
        fldFirstName.addFocusListener(this);
        fldNickName.addFocusListener(this);
        fldTitle.addFocusListener(this);
        if (!dlg.newFlag)
            cmbDisplayAs.addFocusListener(this);
        addMouseListener(this);
    }

    void setContent() {
        // 第一区域--------------------------------------------------------
        Object tObj = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.PHOTO));
        if (tObj == null) {
            photo.setPicture(null);
            photo.setText(ContactDlgConst.PHOTO);
        } else {
            String fileName =
                    PIMDBUtil.decodeByteArrayToImage((byte[]) tObj,
                            ((Integer) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ID))).intValue());
            photo.setPicture(fileName);
            photo.setBorder(null);
            photo.setText(CASUtility.EMPTYSTR);
        }
        fldLastName.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.FNAME))); // personal field
        fldFirstName.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.NAME)));

        fileAsVec = new Vector();
        fileAsVec.add(dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT)));
        fileAsModel = new DefaultComboBoxModel(fileAsVec); // 给fileAsBox赋值
        cmbDisplayAs.setModel(fileAsModel);
        cmbDisplayAs.setSelectedIndex(0);// 设定缺省选中项。

        fldNickName.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.NNAME)));
        fldTitle.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.TITLE)));
        fldEmail.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.EMAIL)));
        tObj = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CTYPE));
        lblQQ.setText(tObj == null ? ContactDlgConst.QQ1 : (String) tObj);
        fldQQ.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CNUMBER)));
        fldWebPage.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.WEBPAGE)));
        areComment.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CONTENT)));
        tObj = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ACCOUNT));
        if (tObj != null)
            fldAccount.setText(String.valueOf(((Integer) tObj).floatValue() / 100));
        else
            fldAccount.setText(CASUtility.EMPTYSTR);
        // Categary
        fldCategories.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CATEGORY)));

        // 第二区域---------------------------------------------------------------
        tObj = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.BIRTHDAY));
        if (tObj != null)
            clbBirthday.setSelectedItem(tObj);
        tObj = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ANNIVERSARY));
        if (tObj != null)
            clbAnniversary.setSelectedItem(tObj);
        else {
            clbAnniversary.setSelectedItem(null);
            clbAnniversary.setTime("");
            clbAnniversary.setTimeText("");
        }
        tObj = dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ANNIVERSARYTYPE));
        lblAnniversary.setText(tObj == null ? ContactDlgConst.ANNIVERSARY1 : (String) tObj);
        fldComptel.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPTEL)));
        areAddress.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.ADDRESS)));
        fldPhone.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.PHONE)));
        fldMobile.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.CPHONE)));
        fldCompName.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPANY)));
        fldCompPostCode.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPPOSTCODE)));
        areCompAddress.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPADDR)));
        fldHomePostCode.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.HOMEPOSTCODE)));
        fldCompfax.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.COMPFAX)));
        fldRelation.setText((String) dlg.getValue(PIMPool.pool.getKey(ContactDefaultViews.RELATION)));
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     */
    void reLayout() {
        // 左边一半（称为第一区域）的最左侧标签的宽度。因为标签的字应该尽量全部显示，所以所有标签的宽度向最宽者看齐。并加个gap。
        final int temLableWidthLeft =
                CASDialogKit.getMaxWidth(new JComponent[] { lblBirthday, lblAnniversary, lblComptel, lblAddress,
                        lblPhone })
                        + CustOpts.HOR_GAP;
        // 第一区域的TextField等组件的宽度。先假设空间够左右布局，发现不够的话设定布局模式为上下布局。
        int temFieldWidthLeft = getWidth() / 2 - temLableWidthLeft - 2 * CustOpts.HOR_GAP;// 减去Label宽和两头的缩进。
        boolean tmpIsVerLayout = temFieldWidthLeft < 150;
        if (tmpIsVerLayout)
            temFieldWidthLeft = getWidth() - temLableWidthLeft - 4 * CustOpts.HOR_GAP;// 减去Label宽和两头的缩进。

        final int tmpXPosOfArea1 = CustOpts.HOR_GAP;
        final int tmpYPosOfArea1 = CustOpts.VER_GAP;

        // 第一区域===============================================================
        // 公司
        lblCompName.setBounds(tmpXPosOfArea1, tmpYPosOfArea1, temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldCompName.setBounds(lblCompName.getX() + temLableWidthLeft, lblCompName.getY(), temFieldWidthLeft,
                CustOpts.BTN_HEIGHT);
        // 公司邮编
        lblCompPostCode.setBounds(lblCompName.getX(), lblCompName.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldCompPostCode.setBounds(lblCompPostCode.getX() + temLableWidthLeft, lblCompPostCode.getY(),
                fldCompName.getWidth(), CustOpts.BTN_HEIGHT);
        // 公司地址
        lblCompAddress.setBounds(lblCompPostCode.getX(), lblCompPostCode.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, temLableWidthLeft, CustOpts.BTN_HEIGHT);
        int tmpPaneHeight = 2 * CustOpts.BTN_HEIGHT;
        if (!tmpIsVerLayout)// 如果纵向的话街道高度固定为上值,否则进行下面的计算。
        { // 少减了20，可能因为ScrollPane下面留有滚动条的高度？
            tmpPaneHeight = CustOpts.BTN_HEIGHT;
            int tmpPaneHeight2 =
                    getHeight() - lblCompAddress.getY() - 9 * CustOpts.BTN_HEIGHT - 10 * CustOpts.VER_GAP + 20;
            if (tmpPaneHeight2 > tmpPaneHeight)// 如果为横向则高度从下面算起，确保组件占满屏幕。
                tmpPaneHeight += (tmpPaneHeight2 - tmpPaneHeight) / 2;
        }
        scrCompAddress.setBounds(lblCompAddress.getX() + temLableWidthLeft, lblCompAddress.getY(),
                fldCompPostCode.getWidth(), tmpPaneHeight);
        // 住址邮编
        lblHomePostCode.setBounds(lblCompAddress.getX(), lblCompAddress.getY() + scrCompAddress.getHeight()
                + CustOpts.VER_GAP, temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldHomePostCode.setBounds(lblHomePostCode.getX() + temLableWidthLeft, lblHomePostCode.getY(),
                scrCompAddress.getWidth(), CustOpts.BTN_HEIGHT);
        // 住址
        lblAddress.setBounds(lblHomePostCode.getX(), lblHomePostCode.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        tmpPaneHeight = 2 * CustOpts.BTN_HEIGHT;
        if (!tmpIsVerLayout)// 如果纵向的话街道高度固定为上值,否则进行下面的计算。
        { // 少减了20，可能因为ScrollPane下面留有滚动条的高度？
            tmpPaneHeight = CustOpts.BTN_HEIGHT;
            int tmpPaneHeight2 = getHeight() - lblAddress.getY() - 7 * CustOpts.BTN_HEIGHT - 8 * CustOpts.VER_GAP + 20;
            if (tmpPaneHeight2 > tmpPaneHeight)// 如果为横向则高度从下面算起，确保组件占满屏幕。
                tmpPaneHeight = tmpPaneHeight2;
        }
        scrAddress.setBounds(lblAddress.getX() + temLableWidthLeft, lblAddress.getY(), fldHomePostCode.getWidth(),
                tmpPaneHeight);

        // 生日
        lblBirthday.setBounds(lblAddress.getX(), lblAddress.getY() + scrAddress.getHeight() + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        clbBirthday.setBounds(lblBirthday.getX() + temLableWidthLeft, lblBirthday.getY(), scrAddress.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 纪念日
        lblAnniversary.setBounds(lblBirthday.getX(), lblBirthday.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        clbAnniversary.setBounds(lblAnniversary.getX() + temLableWidthLeft, lblAnniversary.getY(),
                clbBirthday.getWidth(), CustOpts.BTN_HEIGHT);
        arbAnniversary.setBounds(clbAnniversary.getX() - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT, clbAnniversary.getY(),
                CustOpts.BTN_HEIGHT, CustOpts.BTN_HEIGHT);
        // 单位电话
        lblComptel.setBounds(lblAnniversary.getX(), lblAnniversary.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldComptel.setBounds(lblComptel.getX() + temLableWidthLeft, lblComptel.getY(), clbAnniversary.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 单位传真
        lblCompfax.setBounds(lblComptel.getX(), lblComptel.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldCompfax.setBounds(lblCompfax.getX() + temLableWidthLeft, lblCompfax.getY(), fldComptel.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 移动电话
        lblMobile.setBounds(lblCompfax.getX(), lblCompfax.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldMobile.setBounds(lblMobile.getX() + temLableWidthLeft, lblMobile.getY(), fldCompfax.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 住所电话
        lblPhone.setBounds(lblMobile.getX(), lblMobile.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.BTN_HEIGHT);
        fldPhone.setBounds(lblPhone.getX() + temLableWidthLeft, lblPhone.getY(), fldMobile.getWidth(),
                CustOpts.BTN_HEIGHT);
        // 关系
        btnRelation.setBounds(lblPhone.getX(), lblPhone.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                btnRelation.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        fldRelation.setBounds(btnRelation.getX() + btnRelation.getWidth() + CustOpts.HOR_GAP, btnRelation.getY(),
                fldMobile.getX() + fldMobile.getWidth() - btnRelation.getX() - btnRelation.getWidth()
                        - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        final int tmpXPosOfArea2 = !tmpIsVerLayout ? getWidth() / 2 + CustOpts.HOR_GAP : CustOpts.HOR_GAP;
        final int tmpYPosOfArea2 =
                !tmpIsVerLayout ? tmpYPosOfArea1 : btnRelation.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP;

        // 第二区域===============================================================
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
                !tmpIsVerLayout ? getWidth() / 2 - tmpLblWidthUnderPhoto - tmpLblWidthBesidPhoto - CustOpts.HOR_GAP
                        - CustOpts.VER_GAP : getWidth() - CustOpts.HOR_GAP - tmpLblWidthUnderPhoto
                        - tmpLblWidthBesidPhoto - 2 * CustOpts.HOR_GAP;

        // 照片
        photo.setBounds(tmpXPosOfArea2, tmpYPosOfArea2, tmpLblWidthUnderPhoto - CustOpts.VER_GAP, 5
                * CustOpts.BTN_HEIGHT + 3 * CustOpts.VER_GAP);
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
        cmbDisplayAs.setBounds(lblDisplayAs.getX() + tmpLblWidthBesidPhoto, lblDisplayAs.getY(), tmpFldWidthBesidPhoto,
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
        fldEmail.setBounds(lblEmail.getX() + lblEmail.getWidth() + CustOpts.HOR_GAP, lblEmail.getY(), fldTitle.getX()
                + fldTitle.getWidth() - lblEmail.getX() - lblEmail.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        // QQ
        lblQQ.setBounds(lblEmail.getX(), lblEmail.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, lblEmail.getWidth(),
                CustOpts.LBL_HEIGHT);
        fldQQ.setBounds(fldEmail.getX(), fldEmail.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, fldEmail.getWidth(),
                CustOpts.BTN_HEIGHT);
        arbQQType.setBounds(fldQQ.getX() - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT, fldQQ.getY(), CustOpts.BTN_HEIGHT,
                CustOpts.BTN_HEIGHT);
        // Web页地址
        lblWebPage.setBounds(lblEmail.getX(), fldQQ.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblEmail.getWidth(), CustOpts.BTN_HEIGHT);
        fldWebPage.setBounds(fldEmail.getX(), lblWebPage.getY(), fldEmail.getWidth(), CustOpts.BTN_HEIGHT);

        // 备注－－－－－－－－－－－－
        int tmpNotePaneHeight = 2 * +CustOpts.BTN_HEIGHT + 1 * CustOpts.VER_GAP;
        if (!tmpIsVerLayout)// 如果纵向的话街道高度固定为上值,否则进行下面的计算。
        { // @NOTE：故意少减了20，可能因为ScrollPane下面留有滚动条的高度？
            int tmpNotePaneHeight2 =
                    getHeight() - lblWebPage.getY() - 3 * CustOpts.BTN_HEIGHT - 4 * CustOpts.VER_GAP + 20;
            if (tmpNotePaneHeight2 > tmpNotePaneHeight)// 如果为横向则高度从下面算起，确保组件占满屏幕。
                tmpNotePaneHeight = tmpNotePaneHeight2;
        }
        scrComment.setBounds(lblWebPage.getX(), lblWebPage.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblWebPage.getWidth() + CustOpts.HOR_GAP + fldWebPage.getWidth(), tmpNotePaneHeight);
        lblAccount.setBounds(scrComment.getX(), scrComment.getY() + scrComment.getHeight() + CustOpts.VER_GAP,
                lblWebPage.getWidth(), CustOpts.BTN_HEIGHT);
        fldAccount.setBounds(lblAccount.getX() + lblAccount.getWidth() + CustOpts.HOR_GAP, lblAccount.getY(),
                fldWebPage.getWidth(), CustOpts.BTN_HEIGHT);
        // 类别
        btnCategories.setBounds(lblAccount.getX(), lblAccount.getY() + lblAccount.getHeight() + CustOpts.VER_GAP,
                btnCategories.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        fldCategories.setBounds(btnCategories.getX() + btnCategories.getWidth() + CustOpts.HOR_GAP,
                btnCategories.getY(), scrComment.getWidth() - btnCategories.getWidth() - CustOpts.HOR_GAP,
                CustOpts.BTN_HEIGHT);

        panel.setPreferredSize(new Dimension(getWidth(), btnCategories.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP));// 放在滚动面板上的Panel组件必须设置Prefered尺寸，否则无效。

        validate();
    }

    /**
     * 仅为fileAs ComboBox准备Model。先删除，再重组，最后更新fileAs的视图； @ called by: self; ContactDetailPanel;
     */
    void reprepairFileAsVec() {
        // clear all
        fileAsVec.removeAllElements();
        prepareCurrentFileAsVec(fileAsVec);
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
        // 照片信息----------------------

        // lastName------------------------
        String tmpTextInField = fldLastName.getText();
        if (tmpTextInField != null)
            dlg.putValue((PIMPool.pool.getKey(ContactDefaultViews.FNAME)), tmpTextInField);
        // firstName。-----------------------
        tmpTextInField = fldFirstName.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.NAME), tmpTextInField);
        // filsAs--------------------------
        tmpTextInField = ((JTextField) ((MetalComboBoxEditor) cmbDisplayAs.getEditor()).getEditorComponent()).getText();
        if (tmpTextInField != null && tmpTextInField.length() > 0)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT), tmpTextInField);
        // 昵称------------------------------
        tmpTextInField = fldNickName.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.NNAME), tmpTextInField);
        // Title----------------------------
        tmpTextInField = fldTitle.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.TITLE), tmpTextInField);
        // email-----------------------------
        tmpTextInField = fldEmail.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.EMAIL), tmpTextInField);
        // QQ号码.----------------------------------
        tmpTextInField = fldQQ.getText();
        dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.CNUMBER), tmpTextInField);
        // 保存QQ类型
        tmpTextInField = lblQQ.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.CTYPE), tmpTextInField);
        // web信息。---------------------------------
        tmpTextInField = fldWebPage.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.WEBPAGE), tmpTextInField);
        // 备注信息-----------------------------------
        tmpTextInField = areComment.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.CONTENT), tmpTextInField);
        // 帐务
        tmpTextInField = fldAccount.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.ACCOUNT),
                    Integer.valueOf(CASUtility.getPriceByCent(Double.valueOf((String) tmpTextInField).doubleValue())));
        else
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.ACCOUNT), Integer.valueOf(0));
        // categories信息----------------------------
        tmpTextInField = fldCategories.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.CATEGORY), tmpTextInField);

        // 保存第二区域的信息===============================================================================
        // 保存生日信息----------------------------
        Object tmpDate = clbBirthday.getSelectedItem();
        if (tmpDate != null)
            dlg.putValue((PIMPool.pool.getKey(ContactDefaultViews.BIRTHDAY)), tmpDate);
        // 保存纪念日信息-----------------------------------------
        tmpDate = clbAnniversary.getSelectedItem();
        if (tmpDate != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.ANNIVERSARY), tmpDate);
        // 保存纪念日类型------------------------------------
        tmpTextInField = lblAnniversary.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.ANNIVERSARYTYPE), tmpTextInField);
        // 单位电话
        tmpTextInField = fldComptel.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.COMPTEL), tmpTextInField);
        // 保存家庭住址信息-----------------------------------------
        tmpTextInField = areAddress.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.ADDRESS), tmpTextInField);
        // 保存宅电信息-----------------------------------------
        tmpTextInField = fldPhone.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.PHONE), tmpTextInField);
        // 保存移动电话信息-----------------------------------------
        tmpTextInField = fldMobile.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.CPHONE), tmpTextInField);
        // 保存公司名------------------------------------------
        tmpTextInField = fldCompName.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.COMPANY), tmpTextInField);
        // 保存单位邮编信息-----------------------------------------
        tmpTextInField = fldCompPostCode.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.COMPPOSTCODE), tmpTextInField);
        // 保单位地址信息-----------------------------------------
        tmpTextInField = areCompAddress.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.COMPADDR), tmpTextInField);
        // 保存家庭邮编信息-----------------------------------------
        tmpTextInField = fldHomePostCode.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.HOMEPOSTCODE), tmpTextInField);
        // 保存公司传真信息-----------------------------------------
        tmpTextInField = fldCompfax.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.COMPFAX), tmpTextInField);
        // 保存关系信息------------------------------
        tmpTextInField = fldRelation.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ContactDefaultViews.RELATION), tmpTextInField);
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

        MediaTracker tracker = new MediaTracker(ContactGeneralPanel.this);
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
    // firstAndLast = (String)dlg.getValue(PIMPool.pool.getIntegerKey(ContactDefaultViewstants.FIRST_NAME))
    // + (String)dlg.getValue(PIMPool.pool.getIntegerKey(ContactDefaultViewstants.LAST_NAME));
    // companyName = (String)dlg.getValue(PIMPool.pool.getIntegerKey(ContactDefaultViewstants.UNIT));
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
    ContactDlg dlg;// @called by: self; ArrowButton;

    // 第一区域-------------------------------------------------------
    private ContactArrowButton arbQQType;
    private PIMImage photo;
    private JLabel lblLastName;
    JTextField fldLastName;
    private JLabel lblFirstName;
    private JTextField fldFirstName;
    private JLabel lblDisplayAs;
    JComboBox cmbDisplayAs;
    private JLabel lblNickname;
    private JTextField fldNickName;
    private JLabel lblTitle;
    private JTextField fldTitle;

    private JLabel lblEmail;
    private JTextField fldEmail;
    private JLabel lblQQ;
    private JTextField fldQQ;
    private JLabel lblWebPage;
    private JTextField fldWebPage;
    private JTextArea areComment;
    private JScrollPane scrComment;
    private JLabel lblAccount;
    JTextField fldAccount;
    private JButton btnCategories;
    private JTextField fldCategories;

    // 第二区域---------------------------------------------------------
    private JLabel lblBirthday;
    private CalendarCombo clbBirthday;
    private JLabel lblAnniversary;
    private CalendarCombo clbAnniversary;
    private ContactArrowButton arbAnniversary;
    private JLabel lblComptel;
    private JTextField fldComptel;
    private JLabel lblAddress;
    private JScrollPane scrAddress;
    private JTextArea areAddress;
    private JLabel lblPhone;
    private JTextField fldPhone;
    private JLabel lblMobile;
    private JTextField fldMobile;
    private JLabel lblCompName;
    JTextField fldCompName;
    private JLabel lblCompPostCode;
    private JTextField fldCompPostCode;
    private JLabel lblCompAddress;
    private JScrollPane scrCompAddress;
    private JTextArea areCompAddress;
    private JLabel lblHomePostCode;
    private JTextField fldHomePostCode;
    private JLabel lblCompfax;
    private JTextField fldCompfax;
    private JButton btnRelation;
    private JTextField fldRelation;
    // -----------------------------------------------

    // 因为三个ArrowButton的右键上的item中的文字有重复的，导致interArrowButton方法中不能直接以文字匹配。
    // 加此标志，指示当前是哪一个ArrowButton被按下。同一个ArrowButton的右键菜单中的项是不可能重复的。
    private int QQType;
    private int anniversaryType;

    private Vector fileAsVec; // 用于保存FileAs组件内容。
    private DefaultComboBoxModel fileAsModel; // FileAs组件的Model。
    private int swingInvoker;
    // 由于在表示fileAs的组合框的model中只存放有合法的匹配,而且是不按顺序的.用该数组存与Option中设置的映射关系.
    private int[] showedFileAsMap = new int[11];
    private JPanel panel;
}
