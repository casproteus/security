package org.cas.client.platform.magicbath.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.employee.EmployeeDefaultViews;
import org.cas.client.platform.input.InputDefaultViews;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.product.ProductDefaultViews;

public class Panel_Modify extends JPanel implements ActionListener {
    public Panel_Modify() {
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
        cmbBoxNum = new JComboBox();
        btnBoxCat = new JButton(MagicbathDlgConst.SeleCATE);
        fldBoxCat = new JTextField();

        lblVipNum = new JLabel(MagicbathDlgConst.SeleVipNumber);
        cmbVipNum = new JComboBox();
        lblMonLeft = new JLabel(MagicbathDlgConst.MonLeft);
        fldMonLeft = new JTextField();

        lblEmpNumber = new JLabel(MagicbathDlgConst.EmpNumber);
        cmbEmpNum = new JComboBox();
        lblEmpName = new JLabel(MagicbathDlgConst.SeleEmployee);
        cmbEmpName = new JComboBox();
        btnEmpCat = new JButton(MagicbathDlgConst.SeleCATE);
        fldEmpCat = new JTextField();

        lblProdName = new JLabel(MagicbathDlgConst.SeleService);
        cmbProdName = new JComboBox();
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
        cmbBoxNum.setEditable(true);
        cmbEmpName.setEditable(true);
        cmbEmpNum.setEditable(true);
        cmbProdName.setEditable(true);
        cmbVipNum.setEditable(true);
        // relayout------------------
        reLayout();

        // build------------
        panelBoxNum.add(lblBoxNum);
        panelBoxNum.add(cmbBoxNum);
        panelBoxNum.add(btnBoxCat);
        panelBoxNum.add(fldBoxCat);
        tabBoxNum.addTab(MagicbathDlgConst.BoxNumber, panelBoxNum);
        add(tabBoxNum);

        panelVIPNum.add(lblVipNum);
        panelVIPNum.add(cmbVipNum);
        panelVIPNum.add(lblMonLeft);
        panelVIPNum.add(fldMonLeft);
        tabVIPNum.addTab(MagicbathDlgConst.VipNumber, panelVIPNum);
        add(tabVIPNum);

        panelEmploy.add(lblEmpNumber);
        panelEmploy.add(cmbEmpNum);
        panelEmploy.add(lblEmpName);
        panelEmploy.add(cmbEmpName);
        panelEmploy.add(btnEmpCat);
        panelEmploy.add(fldEmpCat);
        tabEmploy.addTab(MagicbathDlgConst.Employee, panelEmploy);
        add(tabEmploy);

        panelProduct.add(lblProdName);
        panelProduct.add(cmbProdName);
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
        cmbBoxNum.addActionListener(this);
        cmbEmpName.addActionListener(this);
        cmbEmpNum.addActionListener(this);
        cmbProdName.addActionListener(this);
        cmbVipNum.addActionListener(this);
        // contents--------------------
        initComboBox();
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
        cmbBoxNum.setBounds(lblBoxNum.getX() + lblBoxNum.getWidth(), CustOpts.VER_GAP,
                btnBoxCat.getX() - lblBoxNum.getX() - lblBoxNum.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldBoxCat.setBounds(btnBoxCat.getX() + btnBoxCat.getWidth(), CustOpts.VER_GAP,
                tabBoxNum.getWidth() - btnBoxCat.getX() - btnBoxCat.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        lblVipNum.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        lblMonLeft.setBounds(tabVIPNum.getWidth() / 2, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        cmbVipNum.setBounds(lblVipNum.getX() + lblVipNum.getWidth(), CustOpts.VER_GAP,
                lblMonLeft.getX() - lblVipNum.getX() - lblVipNum.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldMonLeft.setBounds(lblMonLeft.getX() + lblMonLeft.getWidth(), CustOpts.VER_GAP, tabVIPNum.getWidth()
                - lblMonLeft.getX() - lblMonLeft.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        lblEmpNumber.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        lblEmpName.setBounds(tabEmploy.getWidth() / 3, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        btnEmpCat.setBounds(tabEmploy.getWidth() * 2 / 3, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT - 1);
        cmbEmpNum.setBounds(lblEmpNumber.getX() + lblEmpNumber.getWidth(), CustOpts.VER_GAP, lblEmpName.getX()
                - lblEmpNumber.getX() - lblEmpNumber.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        cmbEmpName.setBounds(lblEmpName.getX() + lblEmpName.getWidth(), CustOpts.VER_GAP,
                btnEmpCat.getX() - lblEmpName.getX() - lblEmpName.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldEmpCat.setBounds(btnEmpCat.getX() + btnEmpCat.getWidth(), CustOpts.VER_GAP,
                tabEmploy.getWidth() - btnEmpCat.getX() - btnEmpCat.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        lblProdName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT);
        lblPrice.setBounds(tabProduct.getWidth() / 2, CustOpts.VER_GAP, lblWidth, CustOpts.BTN_HEIGHT - 1);
        cmbProdName.setBounds(lblProdName.getX() + lblProdName.getWidth(), lblProdName.getY(), lblPrice.getX()
                - lblProdName.getX() - lblProdName.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        fldPrice.setBounds(lblPrice.getX() + lblPrice.getWidth(), CustOpts.VER_GAP,
                tabProduct.getWidth() - lblPrice.getX() - lblPrice.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        btnProdCat.setBounds(CustOpts.HOR_GAP, lblProdName.getY() + lblProdName.getHeight() + CustOpts.VER_GAP,
                lblWidth, CustOpts.BTN_HEIGHT);
        lblCost.setBounds(lblPrice.getX(), lblPrice.getY() + lblPrice.getHeight() + CustOpts.VER_GAP, lblWidth,
                CustOpts.BTN_HEIGHT);
        fldProdCat.setBounds(btnProdCat.getX() + btnProdCat.getWidth(), btnProdCat.getY(), cmbProdName.getWidth(),
                CustOpts.BTN_HEIGHT);
        fldCost.setBounds(lblCost.getX() + lblCost.getWidth(), lblCost.getY(), fldPrice.getWidth(), CustOpts.BTN_HEIGHT);
    }

    /** Invoked when an action occurs. */
    @Override
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
        } else if (src == cmbBoxNum) {
            if ((idx_cmbBox = cmbBoxNum.getSelectedIndex()) > -1)
                fldBoxCat.setText(boxCatAry[idx_cmbBox]);
        } else if (src == cmbEmpName) {
            if ((idx_cmbEmp = cmbEmpName.getSelectedIndex()) > -1) {
                cmbEmpNum.setSelectedIndex(idx_cmbEmp);
                fldEmpCat.setText(empCatAry[idx_cmbEmp]);
            }
        } else if (src == cmbEmpNum) {
            if ((idx_cmbEmp = cmbEmpNum.getSelectedIndex()) > -1) {
                cmbEmpName.setSelectedIndex(idx_cmbEmp);
                fldEmpCat.setText(empCatAry[idx_cmbEmp]);

            }
        } else if (src == cmbProdName) {
            if ((idx_cmbProd = cmbProdName.getSelectedIndex()) > -1) {
                fldPrice.setText(String.valueOf(Integer.valueOf(prodPriceAry[idx_cmbProd]).floatValue() / 100));
                fldProdCat.setText(prodCatAry[idx_cmbProd]);
                fldCost.setText(String.valueOf(Integer.valueOf(prodCostAry[idx_cmbProd]).floatValue() / 100));
            }
        } else if (src == cmbVipNum) {
            if ((idx_cmbVip = cmbVipNum.getSelectedIndex()) > -1)
                fldMonLeft.setText(String.valueOf(Integer.valueOf(vipAccount[idx_cmbVip]).floatValue() / 100));
        }
    }

    private void initComboBox() {
        // Box-----------
        String sql = "select ID, SUBJECT, category from contact where DELETED != true and FOLDERID = 101";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            boxIDAry = new int[tmpPos];
            boxSubAry = new String[tmpPos];
            boxCatAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                boxIDAry[tmpPos] = rs.getInt("ID");
                boxSubAry[tmpPos] = rs.getString("SUBJECT");
                boxCatAry[tmpPos] = rs.getString("category");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        cmbBoxNum.setModel(new DefaultComboBoxModel(boxSubAry));
        cmbBoxNum.setSelectedIndex(-1);

        // VIP----------------------
        sql = "select ID, SUBJECT, ACCOUNT from contact where DELETED != true and FOLDERID = 102";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            vipIDAry = new int[tmpPos];
            vipSubAry = new String[tmpPos];
            vipAccount = new int[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                vipIDAry[tmpPos] = rs.getInt("ID");
                vipSubAry[tmpPos] = rs.getString("SUBJECT");
                vipAccount[tmpPos] = rs.getInt("ACCOUNT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        cmbVipNum.setModel(new DefaultComboBoxModel(vipSubAry));
        cmbVipNum.setSelectedIndex(-1);

        // Employee----------------
        sql = "select ID, SUBJECT, CODE, CATEGORY from employee where DELETED != true";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            empIDAry = new int[tmpPos];
            empCode = new String[tmpPos];
            empSubAry = new String[tmpPos];
            empCatAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                empIDAry[tmpPos] = rs.getInt("ID");
                empCode[tmpPos] = rs.getString("CODE");
                empSubAry[tmpPos] = rs.getString("SUBJECT");
                empCatAry[tmpPos] = rs.getString("CATEGORY");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        cmbEmpNum.setModel(new DefaultComboBoxModel(empCode));
        cmbEmpNum.setSelectedIndex(-1);
        cmbEmpName.setModel(new DefaultComboBoxModel(empSubAry));
        cmbEmpName.setSelectedIndex(-1);

        // Product----------------
        sql = "select ID, SUBJECT, PRICE, CATEGORY from product where DELETED != true";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            prodIDAry = new int[tmpPos];
            prodCatAry = new String[tmpPos];
            prodPriceAry = new int[tmpPos];
            prodSubAry = new String[tmpPos];
            costIDAry = new int[tmpPos];
            prodCostAry = new int[tmpPos];

            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                prodIDAry[tmpPos] = rs.getInt("ID");
                prodCatAry[tmpPos] = rs.getString("CATEGORY");
                prodPriceAry[tmpPos] = rs.getInt("PRICE");
                prodSubAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        cmbProdName.setModel(new DefaultComboBoxModel(prodSubAry));
        cmbProdName.setSelectedIndex(-1);
        // cost----------------
        sql = "select ID, PRODUCTID, TOLTALPRICE from input where DELETED != true";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            int[] tIDAry = new int[tmpPos];
            int[] tProdIDAry = new int[tmpPos];
            int[] tPriceAry = new int[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                tIDAry[tmpPos] = rs.getInt("ID");
                tProdIDAry[tmpPos] = rs.getInt("PRODUCTID");
                tPriceAry[tmpPos] = rs.getInt("TOLTALPRICE");
                tmpPos++;
            }
            rs.close();// 关闭
            for (int i = 0; i < prodIDAry.length; i++) {
                for (int j = 0; j < tProdIDAry.length; j++) {
                    if (prodIDAry[i] == tProdIDAry[j]) {
                        prodCostAry[i] = tPriceAry[j];
                        costIDAry[i] = tIDAry[j];
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
    }

    void apply() {
        if (idx_cmbBox > -1) {
            String tConBoxNum =
                    ((JTextField) ((MetalComboBoxEditor) cmbBoxNum.getEditor()).getEditorComponent()).getText();
            if (!boxSubAry[idx_cmbBox].equals(tConBoxNum) || !boxCatAry[idx_cmbBox].equals(fldBoxCat.getText())) {
                int tContactAppIdx = CustOpts.custOps.APPNameVec.indexOf("Contact");
                ICASModel tModel = CASControl.ctrl.getModel();
                PIMRecord tRec =
                        tModel.selectRecord(tContactAppIdx, boxIDAry[idx_cmbBox],
                                CASUtility.getAPPNodeID(tContactAppIdx));
                tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT), tConBoxNum);
                tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.CATEGORY), fldBoxCat.getText());
                tModel.updateRecord(tRec, true);
            }
        }
        if (idx_cmbVip > -1) {
            String tConVipNum =
                    ((JTextField) ((MetalComboBoxEditor) cmbVipNum.getEditor()).getEditorComponent()).getText();
            int tMonLeft = CASUtility.getPriceByCent(Double.valueOf(fldMonLeft.getText()).doubleValue());
            if (!vipSubAry[idx_cmbVip].equals(tConVipNum) || vipAccount[idx_cmbVip] != tMonLeft) {
                int tContactAppIdx = CustOpts.custOps.APPNameVec.indexOf("Contact");
                ICASModel tModel = CASControl.ctrl.getModel();
                PIMRecord tRec =
                        tModel.selectRecord(tContactAppIdx, vipIDAry[idx_cmbVip],
                                CASUtility.getAPPNodeID(tContactAppIdx));
                tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT), tConVipNum);
                tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.ACCOUNT), Integer.valueOf(tMonLeft));
                tModel.updateRecord(tRec, true);
            }
        }
        if (idx_cmbEmp > -1) {
            String tConEmpNum =
                    ((JTextField) ((MetalComboBoxEditor) cmbEmpNum.getEditor()).getEditorComponent()).getText();
            String tConEmpName =
                    ((JTextField) ((MetalComboBoxEditor) cmbEmpName.getEditor()).getEditorComponent()).getText();
            if (!empCode[idx_cmbEmp].equals(tConEmpNum) || !empSubAry[idx_cmbEmp].equals(tConEmpName)
                    || !empCatAry[idx_cmbEmp].equals(fldEmpCat.getText())) {
                int tEmployAppIdx = CustOpts.custOps.APPNameVec.indexOf("Employee");
                ICASModel tModel = CASControl.ctrl.getModel();
                PIMRecord tRec =
                        tModel.selectRecord(tEmployAppIdx, empIDAry[idx_cmbEmp], CASUtility.getAPPNodeID(tEmployAppIdx));
                tRec.setFieldValue(PIMPool.pool.getKey(EmployeeDefaultViews.CODE), tConEmpNum);
                tRec.setFieldValue(PIMPool.pool.getKey(EmployeeDefaultViews.SUBJECT), tConEmpName);
                tRec.setFieldValue(PIMPool.pool.getKey(EmployeeDefaultViews.CATEGORY), fldEmpCat.getText());
                tModel.updateRecord(tRec, true);
            }
        }
        if (idx_cmbProd > -1) {
            String tConProdNam =
                    ((JTextField) ((MetalComboBoxEditor) cmbProdName.getEditor()).getEditorComponent()).getText();
            int tPrice = CASUtility.getPriceByCent(Double.valueOf(fldPrice.getText()).doubleValue());
            int tCost = CASUtility.getPriceByCent(Double.valueOf(fldCost.getText()).doubleValue());
            if (!prodSubAry[idx_cmbProd].equals(tConProdNam) || !prodCatAry[idx_cmbProd].equals(fldProdCat.getText())
                    || prodPriceAry[idx_cmbProd] != tPrice || prodCostAry[idx_cmbProd] != tCost) {
                int tProdAppIdx = CustOpts.custOps.APPNameVec.indexOf("Product");
                ICASModel tModel = CASControl.ctrl.getModel();
                PIMRecord tRec =
                        tModel.selectRecord(tProdAppIdx, prodIDAry[idx_cmbProd], CASUtility.getAPPNodeID(tProdAppIdx));
                tRec.setFieldValue(PIMPool.pool.getKey(ProductDefaultViews.SUBJECT), tConProdNam);
                tRec.setFieldValue(PIMPool.pool.getKey(ProductDefaultViews.PRICE), Integer.valueOf(tPrice));
                tRec.setFieldValue(PIMPool.pool.getKey(ProductDefaultViews.CATEGORY), fldProdCat.getText());
                tModel.updateRecord(tRec, true);

                int tInputAppIdx = CustOpts.custOps.APPNameVec.indexOf("Input");
                tRec = tModel.selectRecord(tInputAppIdx, costIDAry[idx_cmbProd], CASUtility.getAPPNodeID(tInputAppIdx));
                tRec.setFieldValue(PIMPool.pool.getKey(InputDefaultViews.TOLTALPRICE), Integer.valueOf(tCost));
                tModel.updateRecord(tRec, true);
            }
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
        cmbBoxNum.setSelectedIndex(-1);
        cmbEmpName.setSelectedIndex(-1);
        cmbEmpNum.setSelectedIndex(-1);
        cmbProdName.setSelectedIndex(-1);
        cmbVipNum.setSelectedIndex(-1);
    }

    int idx_cmbBox = -1;
    private int idx_cmbEmp = -1;
    int idx_cmbProd = -1;
    int idx_cmbVip = -1;

    private int[] boxIDAry; // Box---------
    private String[] boxSubAry;
    private String[] boxCatAry;
    private int[] vipIDAry; // VIP---------
    private String[] vipSubAry;
    private int[] vipAccount;
    private int[] empIDAry; // EMP---------
    private String[] empCode;
    private String[] empSubAry;
    private String[] empCatAry;
    private int[] prodIDAry; // Prod---------
    private int[] costIDAry;
    private int[] prodPriceAry;
    private int[] prodCostAry;
    private String[] prodSubAry;
    private String[] prodCatAry;

    private JTabbedPane tabBoxNum;
    private JTabbedPane tabVIPNum;
    private JTabbedPane tabEmploy;
    private JTabbedPane tabProduct;
    private JPanel panelBoxNum;
    private JPanel panelVIPNum;
    private JPanel panelEmploy;
    private JPanel panelProduct;

    private JLabel lblBoxNum;
    private JComboBox cmbBoxNum;
    private JButton btnBoxCat;
    JTextField fldBoxCat;

    private JLabel lblVipNum;
    private JComboBox cmbVipNum;
    private JLabel lblMonLeft;
    JTextField fldMonLeft;

    private JLabel lblEmpNumber;
    private JComboBox cmbEmpNum;
    private JLabel lblEmpName;
    private JComboBox cmbEmpName;
    private JButton btnEmpCat;
    private JTextField fldEmpCat;

    private JLabel lblProdName;
    private JComboBox cmbProdName;
    private JButton btnProdCat;
    private JTextField fldProdCat;
    private JLabel lblPrice;
    JTextField fldPrice;
    private JLabel lblCost;
    JTextField fldCost;
}
