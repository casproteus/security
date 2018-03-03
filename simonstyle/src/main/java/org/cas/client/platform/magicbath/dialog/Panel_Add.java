package org.cas.client.platform.magicbath.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.metal.MetalComboBoxEditor;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.employee.EmployeeDefaultViews;
import org.cas.client.platform.input.InputDefaultViews;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.product.ProductDefaultViews;

public class Panel_Add extends JPanel implements ActionListener {
    public Panel_Add() {
        // init---------
        tabBoxNum = new JTabbedPane();
        tabVIPNum = new JTabbedPane();
        tabEmploy = new JTabbedPane();
        tabProduct = new JTabbedPane();
        panelBoxNum = new JPanel();
        panelVIPNum = new JPanel();
        panelEmploy = new JPanel();
        panelProduct = new JPanel();

        lblBoxNum = new JLabel(MagicbathDlgConst.SeleBoxNumber);
        fldBoxNum = new JTextField();
        btnBoxCat = new JButton(MagicbathDlgConst.SeleCATE);
        fldBoxCat = new JTextField();

        lblVipNum = new JLabel(MagicbathDlgConst.SeleVipNumber);
        fldVipNum = new JTextField();
        lblMonLeft = new JLabel(MagicbathDlgConst.MonLeft);
        fldMonLeft = new JTextField();

        lblEmpNumber = new JLabel(MagicbathDlgConst.EmpNumber);
        fldEmpNum = new JTextField();
        lblEmpName = new JLabel(MagicbathDlgConst.SeleEmployee);
        fldEmpName = new JTextField();
        btnEmpCat = new JButton(MagicbathDlgConst.SeleCATE);
        fldEmpCat = new JTextField();

        lblProdName = new JLabel(MagicbathDlgConst.SeleService);
        fldProdName = new JTextField();
        btnProdCat = new JButton(MagicbathDlgConst.SeleCATE);
        fldProdCat = new JTextField();
        lblPrice = new JLabel(MagicbathDlgConst.Price);
        fldPrice = new JTextField();
        lblCost = new JLabel(MagicbathDlgConst.Cost);
        fldCost = new JTextField();

        // propety--------------------
        setLayout(null);
        panelBoxNum.setLayout(null);
        panelVIPNum.setLayout(null);
        panelEmploy.setLayout(null);
        panelProduct.setLayout(null);
        btnBoxCat.setMargin(new Insets(0, 0, 0, 0));
        btnEmpCat.setMargin(btnBoxCat.getMargin());
        btnProdCat.setMargin(btnEmpCat.getMargin());
        btnBoxCat.setOpaque(false);
        btnEmpCat.setOpaque(false);
        btnProdCat.setOpaque(false);
        lblBoxNum.setHorizontalAlignment(SwingConstants.CENTER);
        lblVipNum.setHorizontalAlignment(SwingConstants.CENTER);
        lblMonLeft.setHorizontalAlignment(SwingConstants.CENTER);
        lblEmpNumber.setHorizontalAlignment(SwingConstants.CENTER);
        lblEmpName.setHorizontalAlignment(SwingConstants.CENTER);
        lblProdName.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrice.setHorizontalAlignment(SwingConstants.CENTER);
        lblCost.setHorizontalAlignment(SwingConstants.CENTER);

        // relayout------------------
        reLayout();

        // build------------
        panelBoxNum.add(lblBoxNum);
        panelBoxNum.add(fldBoxNum);
        panelBoxNum.add(btnBoxCat);
        panelBoxNum.add(fldBoxCat);
        tabBoxNum.addTab(MagicbathDlgConst.BoxNumber, panelBoxNum);
        add(tabBoxNum);

        panelVIPNum.add(lblVipNum);
        panelVIPNum.add(fldVipNum);
        panelVIPNum.add(lblMonLeft);
        panelVIPNum.add(fldMonLeft);
        tabVIPNum.addTab(MagicbathDlgConst.VipNumber, panelVIPNum);
        add(tabVIPNum);

        panelEmploy.add(lblEmpNumber);
        panelEmploy.add(fldEmpNum);
        panelEmploy.add(lblEmpName);
        panelEmploy.add(fldEmpName);
        panelEmploy.add(btnEmpCat);
        panelEmploy.add(fldEmpCat);
        tabEmploy.addTab(MagicbathDlgConst.Employee, panelEmploy);
        add(tabEmploy);

        panelProduct.add(lblProdName);
        panelProduct.add(fldProdName);
        panelProduct.add(btnProdCat);
        panelProduct.add(fldProdCat);
        panelProduct.add(lblPrice);
        panelProduct.add(fldPrice);
        panelProduct.add(lblCost);
        panelProduct.add(fldCost);
        tabProduct.addTab(MagicbathDlgConst.Service, panelProduct);
        add(tabProduct);

        // listeners-------------------
        btnBoxCat.addActionListener(this);
        btnEmpCat.addActionListener(this);
        btnProdCat.addActionListener(this);
    }

    void reLayout() {
        int tabHeight = 60;
        int lblWidth = 50;
        tabBoxNum.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - CustOpts.HOR_GAP * 2, tabHeight);
        tabVIPNum.setBounds(CustOpts.HOR_GAP, tabBoxNum.getY() + tabBoxNum.getHeight() + CustOpts.VER_GAP, getWidth()
                - CustOpts.HOR_GAP * 2, tabHeight);
        tabEmploy.setBounds(CustOpts.HOR_GAP, tabVIPNum.getY() + tabVIPNum.getHeight() + CustOpts.VER_GAP, getWidth()
                - CustOpts.HOR_GAP * 2, tabHeight);
        tabProduct.setBounds(CustOpts.HOR_GAP, tabEmploy.getY() + tabEmploy.getHeight() + CustOpts.VER_GAP, getWidth()
                - CustOpts.HOR_GAP * 2, 88);

        lblBoxNum.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        btnBoxCat.setBounds(tabBoxNum.getWidth() / 2, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT - 1);
        fldBoxNum.setBounds(lblBoxNum.getX() + lblBoxNum.getWidth(), CustOpts.VER_GAP,
                btnBoxCat.getX() - lblBoxNum.getX() - lblBoxNum.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldBoxCat.setBounds(btnBoxCat.getX() + btnBoxCat.getWidth(), CustOpts.VER_GAP,
                tabBoxNum.getWidth() - btnBoxCat.getX() - btnBoxCat.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        lblVipNum.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        lblMonLeft.setBounds(tabVIPNum.getWidth() / 2, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        fldVipNum.setBounds(lblVipNum.getX() + lblVipNum.getWidth(), CustOpts.VER_GAP,
                lblMonLeft.getX() - lblVipNum.getX() - lblVipNum.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldMonLeft.setBounds(lblMonLeft.getX() + lblMonLeft.getWidth(), CustOpts.VER_GAP, tabVIPNum.getWidth()
                - lblMonLeft.getX() - lblMonLeft.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        lblEmpNumber.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        lblEmpName.setBounds((int) (tabEmploy.getWidth() / 3), CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        btnEmpCat.setBounds((int) (tabEmploy.getWidth() * 2 / 3), CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT - 1);
        fldEmpNum.setBounds(lblEmpNumber.getX() + lblEmpNumber.getWidth(), CustOpts.VER_GAP, lblEmpName.getX()
                - lblEmpNumber.getX() - lblEmpNumber.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldEmpName.setBounds(lblEmpName.getX() + lblEmpName.getWidth(), CustOpts.VER_GAP,
                btnEmpCat.getX() - lblEmpName.getX() - lblEmpName.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldEmpCat.setBounds(btnEmpCat.getX() + btnEmpCat.getWidth(), CustOpts.VER_GAP,
                tabEmploy.getWidth() - btnEmpCat.getX() - btnEmpCat.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        lblProdName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        lblPrice.setBounds(tabProduct.getWidth() / 2, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT - 1);
        fldProdName.setBounds(lblProdName.getX() + lblProdName.getWidth(), lblProdName.getY(), lblPrice.getX()
                - lblProdName.getX() - lblProdName.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldPrice.setBounds(lblPrice.getX() + lblPrice.getWidth(), CustOpts.VER_GAP,
                tabProduct.getWidth() - lblPrice.getX() - lblPrice.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        btnProdCat.setBounds(CustOpts.HOR_GAP, lblProdName.getY() + lblProdName.getHeight() + CustOpts.VER_GAP,
                lblWidth, CustOpts.BTN_HEIGHT);
        lblCost.setBounds(lblPrice.getX(), lblPrice.getY() + lblPrice.getHeight() + CustOpts.VER_GAP, lblWidth,
                CustOpts.BTN_HEIGHT);
        fldProdCat.setBounds(btnProdCat.getX() + btnProdCat.getWidth(), btnProdCat.getY(), fldProdName.getWidth(),
                CustOpts.BTN_HEIGHT);
        fldCost.setBounds(lblCost.getX() + lblCost.getWidth(), lblCost.getY(), fldPrice.getWidth(), CustOpts.BTN_HEIGHT);
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Object src = e.getSource();
        if (src == btnBoxCat) {
            CategoryDialog tmpDlg = new CategoryDialog(Dlg_ModifyData.getInstance(), fldBoxCat.getText());
            tmpDlg.setVisible(true);
            if (tmpDlg.isModified())
                fldBoxCat.setText(tmpDlg.getCategories());
        } else if (src == btnEmpCat) {
            CategoryDialog tmpDlg = new CategoryDialog(Dlg_ModifyData.getInstance(), fldEmpCat.getText());
            tmpDlg.setVisible(true);
            if (tmpDlg.isModified())
                fldEmpCat.setText(tmpDlg.getCategories());
        } else if (src == btnProdCat) {
            CategoryDialog tmpDlg = new CategoryDialog(Dlg_ModifyData.getInstance(), fldProdCat.getText());
            tmpDlg.setVisible(true);
            if (tmpDlg.isModified())
                fldProdCat.setText(tmpDlg.getCategories());
        }
    }

    void apply() {
        if (fldBoxNum.getText().length() > 0) {
            int tContactAppIdx = CustOpts.custOps.APPNameVec.indexOf("Contact");
            ICASModel tModel = CASControl.ctrl.getModel();
            PIMRecord tRec = new PIMRecord();
            tRec.setFieldValues(new Hashtable());
            tRec.setAppIndex(tContactAppIdx);
            tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.FOLDERID), Integer.valueOf(101));
            tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT), fldBoxNum.getText());
            tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.CATEGORY), fldBoxCat.getText());
            tModel.insertRecord(tRec, true);
        }
        if (fldVipNum.getText().length() > 0) {
            int tContactAppIdx = CustOpts.custOps.APPNameVec.indexOf("Contact");
            ICASModel tModel = CASControl.ctrl.getModel();
            PIMRecord tRec = new PIMRecord();
            tRec.setFieldValues(new Hashtable());
            tRec.setAppIndex(tContactAppIdx);
            tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.FOLDERID), Integer.valueOf(102));
            tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT), fldVipNum.getText());
            int tMonLeft = CASUtility.getPriceByCent(Double.valueOf(fldMonLeft.getText()).doubleValue());
            tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.ACCOUNT), Integer.valueOf(tMonLeft));
            tModel.insertRecord(tRec, true);
        }
        if (fldEmpNum.getText().length() > 0 || fldEmpName.getText().length() > 0) {
            int tEmployAppIdx = CustOpts.custOps.APPNameVec.indexOf("Employee");
            ICASModel tModel = CASControl.ctrl.getModel();
            PIMRecord tRec = new PIMRecord();
            tRec.setFieldValues(new Hashtable());
            tRec.setAppIndex(tEmployAppIdx);
            tRec.setFieldValue(PIMPool.pool.getKey(EmployeeDefaultViews.CODE), fldEmpNum.getText());
            tRec.setFieldValue(PIMPool.pool.getKey(EmployeeDefaultViews.SUBJECT), fldEmpName.getText());
            tRec.setFieldValue(PIMPool.pool.getKey(EmployeeDefaultViews.CATEGORY), fldEmpCat.getText());
            if (fldEmpCat.getText().indexOf("计件") > 0)
                tRec.setFieldValue(PIMPool.pool.getKey(EmployeeDefaultViews.FOLDERID), Integer.valueOf(5001));
            tModel.insertRecord(tRec, true);
        }
        if (fldProdName.getText().length() > 0) {
            int tProdAppIdx = CustOpts.custOps.APPNameVec.indexOf("Product");
            ICASModel tModel = CASControl.ctrl.getModel();
            PIMRecord tRec = new PIMRecord();
            tRec.setFieldValues(new Hashtable());
            tRec.setAppIndex(tProdAppIdx);
            tRec.setFieldValue(PIMPool.pool.getKey(ProductDefaultViews.SUBJECT), fldProdName.getText());
            int tPrice = CASUtility.getPriceByCent(Double.valueOf(fldPrice.getText()).doubleValue());
            tRec.setFieldValue(PIMPool.pool.getKey(ProductDefaultViews.PRICE), Integer.valueOf(tPrice));
            tRec.setFieldValue(PIMPool.pool.getKey(ProductDefaultViews.CATEGORY), fldProdCat.getText());
            if (fldEmpCat.getText().indexOf("足疗") > 0)
                tRec.setFieldValue(PIMPool.pool.getKey(ProductDefaultViews.FOLDERID), Integer.valueOf(5101));
            else if (fldEmpCat.getText().indexOf("桑拿") > 0)
                tRec.setFieldValue(PIMPool.pool.getKey(ProductDefaultViews.FOLDERID), Integer.valueOf(5102));
            tModel.insertRecord(tRec, true);

            int tInputAppIdx = CustOpts.custOps.APPNameVec.indexOf("Input");
            tRec = new PIMRecord();
            tRec.setFieldValues(new Hashtable());
            tRec.setAppIndex(tInputAppIdx);
            int tCost = CASUtility.getPriceByCent(Double.valueOf(fldCost.getText()).doubleValue());
            tRec.setFieldValue(PIMPool.pool.getKey(InputDefaultViews.TOLTALPRICE), Integer.valueOf(tCost));
            tRec.setFieldValue(PIMPool.pool.getKey(InputDefaultViews.FOLDERID), Integer.valueOf(5201));
            tModel.insertRecord(tRec, true);
        }
        resetComps();
    }

    private void resetComps() {
        fldBoxCat.setText(null);
        fldCost.setText(null);
        fldEmpCat.setText(null);
        fldMonLeft.setText(null);
        fldPrice.setText(null);
        fldProdCat.setText(null);
        fldBoxNum.setText(null);
        fldEmpName.setText(null);
        fldEmpNum.setText(null);
        fldProdName.setText(null);
        fldVipNum.setText(null);
    }

    private JTabbedPane tabBoxNum;
    private JTabbedPane tabVIPNum;
    private JTabbedPane tabEmploy;
    private JTabbedPane tabProduct;
    private JPanel panelBoxNum;
    private JPanel panelVIPNum;
    private JPanel panelEmploy;
    private JPanel panelProduct;

    private JLabel lblBoxNum;
    JTextField fldBoxNum;
    private JButton btnBoxCat;
    JTextField fldBoxCat;

    private JLabel lblVipNum;
    JTextField fldVipNum;
    private JLabel lblMonLeft;
    JTextField fldMonLeft;

    private JLabel lblEmpNumber;
    JTextField fldEmpNum;
    private JLabel lblEmpName;
    JTextField fldEmpName;
    private JButton btnEmpCat;
    JTextField fldEmpCat;

    private JLabel lblProdName;
    JTextField fldProdName;
    private JButton btnProdCat;
    JTextField fldProdCat;
    private JLabel lblPrice;
    JTextField fldPrice;
    private JLabel lblCost;
    JTextField fldCost;
}
