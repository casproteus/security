package org.cas.client.platform.product.dialog;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.employee.EmployeeDefaultViews;
import org.cas.client.platform.product.ProductDefaultViews;
import org.cas.client.resource.international.DlgConst;

/** 联系人对话框 */
class ProductGeneralPanel extends JScrollPane implements ActionListener, Runnable, Releasable {
    /**
     * Creates a new instance of ContactGeneralPanel 新建和编辑联系人对话框
     * 
     * @param prmDlg
     *            父窗体
     */
    ProductGeneralPanel(ProductDlg prmDlg) {
        dlg = prmDlg;
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     * 
     * @param e
     *            the document event
     */
    public void changedUpdate(
            DocumentEvent e) {
    }

    /** Invoked when an action occurs. 保存ismail的状态。 */
    public void actionPerformed(
            ActionEvent e) {
        Object tmpObj = e.getSource();
        if (tmpObj == btnCategories) {
            // new TypeDialog(dlg, true).setVisible(true);
            // if(categoryDialog == null)
            // {
            CategoryDialog tmpDlg = new CategoryDialog(dlg, fldCategories.getText());
            tmpDlg.setVisible(true);
            // }
            // else
            // {
            // categoryDialog.show(categoriesField.getText());
            // }
            if (tmpDlg.isModified())
                if (tmpDlg.isModified()) {
                    fldCategories.setText(tmpDlg.getCategories());
                    dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.CATEGORY), tmpDlg.getCategories());
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
                swingInvoker = 0; // 设置焦点默认落点
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
    public void release() {
        if (btnCategories != null)
            btnCategories.removeActionListener(this);
    }

    boolean isValueChanged() {
        // 第一区域信息保存========================================================================================
        // 保存产品编号。------------------------
        Object tmpValue1 = fldCode.getText();
        Object tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.CODE));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存产品名称。-----------------------
        tmpValue1 = fldName.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.SUBJECT));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存品牌。------------------------------
        tmpValue1 = fldBrand.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.BRAND));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存产地。----------------------------
        tmpValue1 = fldProducarea.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.PRODUCAREA));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存价格。-----------------------------
        tmpValue1 = fldPrice.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.PRICE));
        tmpValue2 = (tmpValue2 == null) ? "" : String.valueOf(((Integer) tmpValue2).floatValue() / 100);
        if (!tmpValue1.equals(tmpValue2))
            return true;
        // 保存单位.----------------------------------
        tmpValue1 = fldUnit.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.UNIT));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存备注信息-----------------------------------
        tmpValue1 = areComment.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.CONTENT));
        if ((!tmpValue1.equals(tmpValue2)) && !(tmpValue1.equals(CASUtility.EMPTYSTR) && tmpValue2 == null))
            return true;
        // 保存categories信息----------------------------
        tmpValue1 = fldCategories.getText();
        tmpValue2 = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.CATEGORY));
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
        lblCode = new JLabel(ProductDlgConst.CODE);
        fldCode = new JTextField();
        lblName = new JLabel(ProductDlgConst.NAME);
        fldName = new JTextField();
        lblMnemonic = new JLabel(ProductDlgConst.MNEMONIC);
        fldMnemonic = new JTextField();
        lblBrand = new JLabel(ProductDlgConst.BRAND);
        fldBrand = new JTextField();
        lblProducarea = new JLabel(ProductDlgConst.PRODUCAREA);
        fldProducarea = new JTextField();
        lblUnit = new JLabel(ProductDlgConst.UNIT);
        fldUnit = new JTextField();
        lblPrice = new JLabel(ProductDlgConst.PRICE);
        lblPriceUnit = new JLabel(ProductDlgConst.PRICEUNIT);
        fldPrice = new JTextField();
        lblCost = new JLabel(ProductDlgConst.COST);
        fldCost = new JTextField();
        lblBatchRelation = new JLabel();
        lblUnitRelation = new JLabel();
        fldTimes = new JTextField();
        lblComment = new JLabel(ProductDlgConst.COMMENT);
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
        lblName.setFont(CustOpts.custOps.getFontOfDefault());
        lblName.setDisplayedMnemonic('m');
        lblName.setLabelFor(fldName);
        lblBrand.setFont(CustOpts.custOps.getFontOfDefault());
        lblBrand.setDisplayedMnemonic('i');
        lblBrand.setLabelFor(fldBrand);
        lblProducarea.setFont(CustOpts.custOps.getFontOfDefault());
        lblProducarea.setDisplayedMnemonic('j');
        lblProducarea.setLabelFor(fldProducarea);
        lblPrice.setFont(CustOpts.custOps.getFontOfDefault());
        lblPriceUnit.setFont(CustOpts.custOps.getFontOfDefault());
        lblUnit.setFont(CustOpts.custOps.getFontOfDefault());
        areComment.setLineWrap(true);
        scrComment.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        btnCategories.setFont(CustOpts.custOps.getFontOfDefault());
        btnCategories.setMnemonic('g');

        // ------------------------------------------------------
        setContent();

        swingInvoker = 1;
        SwingUtilities.invokeLater(this);

        // 搭建=============================================================
        panel.add(lblCode);
        panel.add(fldCode);
        panel.add(lblName);
        panel.add(fldName);
        panel.add(lblMnemonic);
        panel.add(fldMnemonic);
        panel.add(lblBrand);
        panel.add(fldBrand);
        panel.add(lblProducarea);
        panel.add(fldProducarea);
        panel.add(lblPrice);
        panel.add(lblPriceUnit);
        panel.add(fldPrice);
        panel.add(lblUnit);
        panel.add(fldUnit);
        panel.add(lblCost);
        panel.add(fldCost);
        panel.add(lblBatchRelation); // 这三个实现时隐的组件之所以必须在这里添加，
        panel.add(lblUnitRelation); // 而不是在焦点事件中添加，
        panel.add(fldTimes); // 是为了tab键顺序的一致性。
        panel.add(lblComment);
        panel.add(scrComment);
        panel.add(btnCategories);
        panel.add(fldCategories); // 搭建的同时也就确定了Tab建焦点顺序

        setViewportView(panel);

        // 添加监听器======================================================================
        btnCategories.addActionListener(this);
    }

    int SUBJECT = 3;
    int CONTENT = 4;
    int CODE = 5;
    int UNIT = 6;
    int PRICE = 7;
    int PRODUCAREA = 8;
    int BRAND = 9;
    int CATEGORY = 10;

    void setContent() {
        fldCode.setText((String) dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.CODE)));
        fldName.setText((String) dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.SUBJECT)));
        fldMnemonic.setText((String) dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.MNEMONIC)));
        fldBrand.setText((String) dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.BRAND)));
        fldProducarea.setText((String) dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.PRODUCAREA)));
        Object tObj = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.PRICE));
        if (tObj != null)
            fldPrice.setText(String.valueOf(((Integer) tObj).floatValue() / 100));
        else
            fldPrice.setText(CASUtility.EMPTYSTR);
        fldUnit.setText((String) dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.UNIT)));
        tObj = dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.COST));
        if (tObj != null)
            fldTimes.setText(String.valueOf(((Integer) tObj).intValue()));
        else
            fldTimes.setText(CASUtility.EMPTYSTR);
        areComment.setText((String) dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.CONTENT)));
        fldCategories.setText((String) dlg.getValue(PIMPool.pool.getKey(ProductDefaultViews.CATEGORY)));
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     */
    void reLayout() {
        // 左边一半（称为第一区域）的最左侧标签的宽度。因为标签的字应该尽量全部显示，所以所有标签的宽度向最宽者看齐。并加个gap。
        final int temLableWidthLeft =
                CASDialogKit.getMaxWidth(new JComponent[] { lblCode, lblName, lblBrand, lblProducarea, lblPrice,
                        lblUnit })
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
            // 产品名称
            lblName.setBounds(lblCode.getX(), lblCode.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldName.setBounds(lblName.getX() + temLableWidthLeft, lblName.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 助记
            lblMnemonic.setBounds(lblName.getX(), lblName.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldMnemonic.setBounds(lblMnemonic.getX() + temLableWidthLeft, lblMnemonic.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 品牌
            lblBrand.setBounds(lblMnemonic.getX(), lblMnemonic.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldBrand.setBounds(lblBrand.getX() + temLableWidthLeft, lblBrand.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 单位
            lblUnit.setBounds(lblBrand.getX(), lblBrand.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            fldUnit.setBounds(lblUnit.getX() + temLableWidthLeft, lblUnit.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 价格
            lblPrice.setBounds(lblUnit.getX(), lblUnit.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            int tWidth = getFontMetrics(getFont()).stringWidth(lblPriceUnit.getText());
            lblPriceUnit.setBounds(lblPrice.getX() + temLableWidthLeft + temFieldWidthLeft - tWidth, lblPrice.getY(),
                    tWidth, CustOpts.LBL_HEIGHT);
            fldPrice.setBounds(lblPrice.getX() + temLableWidthLeft, lblPrice.getY(), temFieldWidthLeft - tWidth,
                    CustOpts.BTN_HEIGHT);
            // 批量单位
            lblCost.setBounds(lblPrice.getX(), lblPrice.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            int tWidth1 = getFontMetrics(getFont()).stringWidth(lblBatchRelation.getText());
            int tWidth2 = getFontMetrics(getFont()).stringWidth(lblUnitRelation.getText());
            fldCost.setBounds(lblCost.getX() + temLableWidthLeft, lblCost.getY(), temFieldWidthLeft - tWidth1 - tWidth2
                    - 30, CustOpts.BTN_HEIGHT);
            lblBatchRelation.setBounds(fldCost.getX() + fldCost.getWidth(), fldCost.getY(), tWidth1,
                    CustOpts.LBL_HEIGHT);
            fldTimes.setBounds(lblBatchRelation.getX() + lblBatchRelation.getWidth(), lblBatchRelation.getY(), 30,
                    CustOpts.BTN_HEIGHT);
            lblUnitRelation.setBounds(fldTimes.getX() + fldTimes.getWidth(), fldTimes.getY(), tWidth2,
                    CustOpts.LBL_HEIGHT);
            // 产地
            lblProducarea.setBounds(lblCost.getX(), lblCost.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldProducarea.setBounds(lblProducarea.getX() + temLableWidthLeft, lblProducarea.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
        } else {
            // 产品名称
            lblName.setBounds(tmpXPosOfArea2, lblCode.getY(), temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldName.setBounds(lblName.getX() + temLableWidthLeft, lblName.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 助记
            lblMnemonic.setBounds(lblCode.getX(), lblCode.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldMnemonic.setBounds(lblMnemonic.getX() + temLableWidthLeft, lblMnemonic.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 品牌
            lblBrand.setBounds(lblName.getX(), lblName.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldBrand.setBounds(lblBrand.getX() + temLableWidthLeft, lblBrand.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 最小单位
            lblUnit.setBounds(lblMnemonic.getX(), lblMnemonic.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldUnit.setBounds(lblUnit.getX() + temLableWidthLeft, lblUnit.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
            // 价格
            lblPrice.setBounds(lblBrand.getX(), lblBrand.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            int tWidth = getFontMetrics(getFont()).stringWidth(lblPriceUnit.getText());
            lblPriceUnit.setBounds(lblPrice.getX() + temLableWidthLeft + temFieldWidthLeft - tWidth, lblPrice.getY(),
                    tWidth, CustOpts.LBL_HEIGHT);
            fldPrice.setBounds(lblPrice.getX() + temLableWidthLeft, lblPrice.getY(), temFieldWidthLeft - tWidth,
                    CustOpts.BTN_HEIGHT);
            // 批量单位
            lblCost.setBounds(lblUnit.getX(), lblUnit.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.LBL_HEIGHT);
            int tWidth1 = getFontMetrics(getFont()).stringWidth(lblBatchRelation.getText());
            int tWidth2 = getFontMetrics(getFont()).stringWidth(lblUnitRelation.getText());
            fldCost.setBounds(lblCost.getX() + temLableWidthLeft, lblCost.getY(), temFieldWidthLeft - tWidth1 - tWidth2
                    - 30, CustOpts.BTN_HEIGHT);
            lblBatchRelation.setBounds(fldCost.getX() + fldCost.getWidth(), fldCost.getY(), tWidth1,
                    CustOpts.LBL_HEIGHT);
            fldTimes.setBounds(lblBatchRelation.getX() + lblBatchRelation.getWidth(), lblBatchRelation.getY(), 30,
                    CustOpts.BTN_HEIGHT);
            lblUnitRelation.setBounds(fldTimes.getX() + fldTimes.getWidth(), fldTimes.getY(), tWidth2,
                    CustOpts.LBL_HEIGHT);
            // 产地
            lblProducarea.setBounds(lblPrice.getX(), lblPrice.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    temLableWidthLeft, CustOpts.BTN_HEIGHT);
            fldProducarea.setBounds(lblProducarea.getX() + temLableWidthLeft, lblProducarea.getY(), temFieldWidthLeft,
                    CustOpts.BTN_HEIGHT);
        }

        // 备注－－－－－－－－－－－－
        int tmpNotePaneHeight = CustOpts.BTN_HEIGHT + 2 * CustOpts.VER_GAP;
        // @NOTE：故意少减了20，可能因为ScrollPane下面留有滚动条的高度？
        int tmpNotePaneHeight2 =
                getHeight() - lblProducarea.getY() - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT - tmpNotePaneHeight;
        if (tmpNotePaneHeight2 < tmpNotePaneHeight)// 如果为横向则高度从下面算起，确保组件占满屏幕。
            tmpNotePaneHeight2 = tmpNotePaneHeight;

        lblComment.setBounds(tmpXPosOfArea1, lblProducarea.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                temLableWidthLeft, CustOpts.LBL_HEIGHT);
        scrComment.setBounds(lblComment.getX() + temLableWidthLeft, lblComment.getY(), fldProducarea.getX()
                + fldProducarea.getWidth() - lblComment.getX() - temLableWidthLeft, tmpNotePaneHeight2);
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
        // 保存产品编号。------------------------
        String tmpTextInField = fldCode.getText();
        if (tmpTextInField != null)
            dlg.putValue((PIMPool.pool.getKey(ProductDefaultViews.CODE)), tmpTextInField);
        // 保存产品名称。-----------------------
        tmpTextInField = fldName.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.SUBJECT), tmpTextInField);
        // 保存助记字串。------------------------------
        tmpTextInField = fldMnemonic.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.MNEMONIC), tmpTextInField);
        // 保存品牌。------------------------------
        tmpTextInField = fldBrand.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.BRAND), tmpTextInField);
        // 保存产地。----------------------------
        tmpTextInField = fldProducarea.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.PRODUCAREA), tmpTextInField);
        // 保存价格。-----------------------------
        tmpTextInField = fldPrice.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.PRICE),
                    new Integer(CASUtility.getPriceByCent(Double.valueOf(tmpTextInField).doubleValue())));
        else
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.PRICE), Integer.valueOf(0));
        // 保存小单位.----------------------------------
        tmpTextInField = fldUnit.getText();
        dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.UNIT), tmpTextInField);
        // 保存成本关系.----------------------------------
        tmpTextInField = fldTimes.getText();
        if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.COST), Integer.valueOf(tmpTextInField));
        // 保存备注信息-----------------------------------
        tmpTextInField = areComment.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.CONTENT), tmpTextInField);
        // 保存categories信息----------------------------
        tmpTextInField = fldCategories.getText();
        if (tmpTextInField != null)
            dlg.putValue(PIMPool.pool.getKey(ProductDefaultViews.CATEGORY), tmpTextInField);
    }

    private void prepareCurrentFileAsVec(
            Vector prmEmptyVec) {
        for (int i = 0; i < 11; i++) {
            showedFileAsMap[i] = -1;
        }
        // personal field
        String tmpLastName = fldCode.getText();
        String tmpFirstName = fldName.getText();
        String tmpUnit = fldBrand.getText();
        // 鉴于在中国极少有人用称谓,多数用到职位,故暂时注释掉.
        // //NOTE:因为在EDialog的ESC或叉号被按时，dlg的释放资源的方法会先被调到，而调本方法的FocusLost事件后触发，所以detailPane可能为null。
        // if (dlg.getContactDetailPanel() == null)
        // {
        // return;
        // }
        // String tmpSuffix = dlg.getContactDetailPanel().biInitialized ?
        // (String)dlg.getContactDetailPanel().chinesenameField.getText() :
        // (String)dlg.getValue(PIMPool.pool.getIntegerKey(EmployeeDefaultViewstants.SUFFIX));
        String tmpSuffix = fldProducarea.getText();
        // 准备各字符串是否为有效的标志----------------------------
        boolean isLastNameValid = (tmpLastName != null && tmpLastName.length() > 0);
        boolean isFirstNameValid = (tmpFirstName != null && tmpFirstName.length() > 0);
        boolean isUnitValid = (tmpUnit != null && tmpUnit.length() > 0);
        boolean isSuffixValid = (tmpSuffix != null && tmpSuffix.length() > 0);

        if (isLastNameValid && isFirstNameValid) {
            if (isSuffixValid) {
                prmEmptyVec.add(tmpLastName.concat(CASUtility.COMMA).concat(tmpSuffix).concat(CASUtility.SPACE)
                        .concat(tmpFirstName));
                showedFileAsMap[0] = prmEmptyVec.size() - 1;
                prmEmptyVec.add(tmpLastName.concat(tmpFirstName).concat(CASUtility.SPACE).concat(tmpSuffix));
                showedFileAsMap[1] = prmEmptyVec.size() - 1;
            }
            if (isUnitValid) {
                prmEmptyVec.add(tmpFirstName.concat(CASUtility.COMMA).concat(tmpLastName)
                        .concat(CASUtility.LEFT_BRACKET).concat(tmpUnit).concat(CASUtility.RIGHT_BRACKET));
                showedFileAsMap[2] = prmEmptyVec.size() - 1;
                prmEmptyVec.add(tmpFirstName.concat(tmpLastName).concat(CASUtility.LEFT_BRACKET).concat(tmpUnit)
                        .concat(CASUtility.RIGHT_BRACKET));
                showedFileAsMap[3] = prmEmptyVec.size() - 1;
                prmEmptyVec.add(tmpUnit.concat(CASUtility.LEFT_BRACKET).concat(tmpFirstName).concat(CASUtility.COMMA)
                        .concat(tmpLastName).concat(CASUtility.RIGHT_BRACKET));
                showedFileAsMap[4] = prmEmptyVec.size() - 1;
            }
            prmEmptyVec.add(tmpLastName.concat(tmpFirstName));
            showedFileAsMap[5] = prmEmptyVec.size() - 1;
            prmEmptyVec.add(tmpFirstName.concat(tmpLastName));
            showedFileAsMap[6] = prmEmptyVec.size() - 1;
        }
        if (isLastNameValid) // 名字字符串为有效的。
        {
            if (isUnitValid) {
                prmEmptyVec.add(tmpUnit.concat(CASUtility.SPACE).concat(tmpLastName));
                showedFileAsMap[7] = prmEmptyVec.size() - 1;
            }
            prmEmptyVec.add(tmpLastName);
            showedFileAsMap[8] = prmEmptyVec.size() - 1;
        }
        if (isFirstNameValid) // 姓字符串为有效的。
        {
            prmEmptyVec.add(tmpFirstName);
            showedFileAsMap[9] = prmEmptyVec.size() - 1;
        }
        if (isUnitValid) {
            prmEmptyVec.add(tmpUnit);
            showedFileAsMap[10] = prmEmptyVec.size() - 1;
        }
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
    ProductDlg dlg;// @called by: self; ArrowButton;

    // 第一区域-------------------------------------------------------
    private JLabel lblCode;
    JTextField fldCode;
    private JLabel lblName;
    private JTextField fldName;
    private JLabel lblMnemonic;
    private JTextField fldMnemonic;
    private JLabel lblBrand;
    private JTextField fldBrand;
    private JLabel lblProducarea;
    private JTextField fldProducarea;
    private JLabel lblPrice;
    private JLabel lblPriceUnit;
    JTextField fldPrice;
    private JLabel lblUnit;
    private JTextField fldUnit;
    private JLabel lblCost;
    JTextField fldCost;
    private JLabel lblBatchRelation;
    private JLabel lblUnitRelation;
    JTextField fldTimes;
    private JTextArea areComment;
    private JLabel lblComment;
    private JScrollPane scrComment;
    private JButton btnCategories;
    private JTextField fldCategories;

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
// /**
// * @called by:ContactGeneralPanel;
// */
// public int getContactsFirstPhoneIndex()
// {
// int tmpIndex;
// try
// {
// tmpIndex = Integer.parseInt((String)hash.get(PropertyName.CONTACTS_PHONE1));
// }
// catch(Exception e)
// {
// tmpIndex = EmployeeDefaultViewstants.BUSINESS_TELE;
// hash.put(PropertyName.CONTACTS_PHONE1, Integer.toString(tmpIndex));
// }
// return tmpIndex;
// }
