package org.cas.client.platform.badminton.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.plaf.metal.MetalComboBoxEditor;

import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.resource.international.CustViewConsts;
import org.cas.client.resource.international.DlgConst;

public class Dlg_ModifyData extends JDialog implements ActionListener, ComponentListener {
    private static Dlg_ModifyData instance;

    public static Dlg_ModifyData getInstance() {
        if (instance == null)
            instance = new Dlg_ModifyData();
        return instance;
    }

    private Dlg_ModifyData() {
        super(CASMainFrame.mainFrame);
        initComponents();
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Object src = e.getSource();
        if (src == apply) {
            apply();
        } else if (src == ok) {
            if (apply()) {
                instance = null;
                dispose();
            }
        } else if (src == cancel) {
            instance = null;
            dispose();
        }
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    public void componentHidden(
            ComponentEvent e) {
    }

    private void initComponents() {
        // init----------------------
        tabPane = new JTabbedPane();
        addPanel = new Panel_Add();
        deletPanel = new Panel_Delet();
        modifyPanel = new Panel_Modify();
        apply = new JButton(CustViewConsts.APPLY);
        ok = new JButton(CustViewConsts.OK);
        cancel = new JButton(CustViewConsts.CANCEL);

        // propeties----------------
        setTitle(DlgConst.DlgTitle);
        setBounds((CustOpts.SCRWIDTH - 500) / 2, (CustOpts.SCRHEIGHT - 400) / 2, 500, 400); // 对话框的默认尺寸。
        setResizable(true);
        getContentPane().setLayout(null);
        getRootPane().setDefaultButton(apply);
        tabPane.setFont(CustOpts.custOps.getFontOfDefault());

        // Build---------------------
        tabPane.addTab(BadmintonDlgConst.ADDDATA, addPanel);
        tabPane.addTab(BadmintonDlgConst.DELDATA, deletPanel);
        tabPane.addTab(BadmintonDlgConst.MODDATA, modifyPanel);
        getContentPane().add(tabPane);
        getContentPane().add(apply);
        getContentPane().add(ok);
        getContentPane().add(cancel);

        // reLayout--------------------
        reLayout();

        // listener--------------------
        getContentPane().addComponentListener(this);
        apply.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
    }

    private void reLayout() {
        cancel.setBounds(getContentPane().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getContentPane()
                .getHeight() - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        apply.setBounds(ok.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, ok.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        tabPane.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getContentPane().getWidth() - 2 * CustOpts.HOR_GAP,
                ok.getY() - 2 * CustOpts.VER_GAP);
        addPanel.setBounds(0, 0, tabPane.getWidth() - CustOpts.HOR_GAP, tabPane.getHeight() - 50);
        addPanel.reLayout();
        deletPanel.setBounds(0, 0, tabPane.getWidth() - CustOpts.HOR_GAP, tabPane.getHeight() - 50);
        deletPanel.reLayout();
        modifyPanel.setBounds(0, 0, tabPane.getWidth() - CustOpts.HOR_GAP, tabPane.getHeight() - 50);
        modifyPanel.reLayout();
        validate();
    }

    private boolean apply() {
        // check the format of int values in ModifyPane.
        String tmpTextInField = null;
        Object tmpValue = null;
        if (modifyPanel.idx_cmbBox > -1) // Box
            if (modifyPanel.fldBoxCat.getText().length() < 1) {
                modifyPanel.fldBoxCat.grabFocus();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
        if (modifyPanel.idx_cmbVip > -1) { // VIP
            tmpTextInField = modifyPanel.fldMonLeft.getText();
            try {
                tmpValue = Double.valueOf(tmpTextInField);
            } catch (Exception exp) {
                modifyPanel.fldMonLeft.grabFocus();
                modifyPanel.fldMonLeft.selectAll();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
        }
        if (modifyPanel.idx_cmbProd > -1) {
            tmpTextInField = modifyPanel.fldPrice.getText();
            try {
                tmpValue = Double.valueOf(tmpTextInField);
            } catch (Exception exp) {
                modifyPanel.fldPrice.grabFocus();
                modifyPanel.fldPrice.selectAll();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
            tmpTextInField = modifyPanel.fldCost.getText();
            try {
                tmpValue = Double.valueOf(tmpTextInField);
            } catch (Exception exp) {
                modifyPanel.fldCost.grabFocus();
                modifyPanel.fldCost.selectAll();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
        }

        // check the format of int values in addPane.
        tmpTextInField = addPanel.fldBoxNum.getText(); // BoxNumber and money left
        tmpValue = addPanel.fldBoxCat.getText();
        if (!(tmpTextInField.length() < 1 && tmpValue.toString().length() < 1)) {
            if (tmpTextInField.length() < 1) {
                addPanel.fldVipNum.grabFocus();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
            if (tmpValue.toString().length() < 1) {
                addPanel.fldMonLeft.grabFocus();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
        }
        tmpTextInField = addPanel.fldVipNum.getText(); // VIPNumber and money left
        tmpValue = addPanel.fldMonLeft.getText();
        if (!(tmpTextInField.length() < 1 && tmpValue.toString().length() < 1)) {
            if (tmpTextInField.length() < 1) {
                addPanel.fldVipNum.grabFocus();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
            if (tmpValue.toString().length() < 1) {
                try {
                    Double.valueOf(tmpValue.toString());
                } catch (Exception exp) {
                    addPanel.fldMonLeft.grabFocus();
                    addPanel.fldMonLeft.selectAll();
                    JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                    return false;
                }
            }
        }
        if (addPanel.fldEmpNum.getText().length() < 1 // Employee check
                && addPanel.fldEmpName.getText().length() < 1 && addPanel.fldEmpCat.getText().length() > 0) {
            addPanel.fldEmpName.grabFocus();
            SOptionPane.showErrorDialog(MessageCons.W10619);
            return false;
        }
        if (!(addPanel.fldProdName.getText().length() < 1 // Product check
                && addPanel.fldProdCat.getText().length() < 1 && addPanel.fldPrice.getText().length() < 1 && addPanel.fldCost
                .getText().length() < 1)) {
            if (addPanel.fldProdName.getText().length() < 1) { // Prod Name
                addPanel.fldProdName.grabFocus();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
            tmpTextInField = addPanel.fldPrice.getText(); // Prod Price
            try {
                tmpValue = Double.valueOf(tmpTextInField);
            } catch (Exception exp) {
                addPanel.fldPrice.grabFocus();
                addPanel.fldPrice.selectAll();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
            tmpTextInField = addPanel.fldCost.getText(); // Prod Cost
            try {
                tmpValue = Double.valueOf(tmpTextInField);
            } catch (Exception exp) {
                addPanel.fldCost.grabFocus();
                addPanel.fldCost.selectAll();
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                return false;
            }
        }
        modifyPanel.apply();
        deletPanel.apply();
        addPanel.apply();
        return true;
    }

    private JTabbedPane tabPane;
    private Panel_Add addPanel;
    private Panel_Delet deletPanel;
    private Panel_Modify modifyPanel;
    private JButton apply;
    private JButton ok;
    private JButton cancel;
}
