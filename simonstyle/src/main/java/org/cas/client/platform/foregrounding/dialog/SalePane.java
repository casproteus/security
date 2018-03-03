package org.cas.client.platform.foregrounding.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;

public class SalePane extends JTabbedPane implements ComponentListener, ActionListener {
    public SalePane() {
        // init-----------
        general = new JPanel();
        lblProdCode = new JLabel(ForegroundingDlgConst.ProdCode);
        lblProdName = new JLabel(ForegroundingDlgConst.ProdName);
        lblProdBrand = new JLabel(ForegroundingDlgConst.ProdBrand);
        lblProdFactory = new JLabel(ForegroundingDlgConst.ProdFactory);
        lblProdStyle = new JLabel(ForegroundingDlgConst.ProdStyle);
        lblProdColor = new JLabel(ForegroundingDlgConst.ProdColor);
        lblProdPack = new JLabel(ForegroundingDlgConst.ProdPack);
        lblProdCount = new JLabel(ForegroundingDlgConst.ProdCount);
        lblUniPrice = new JLabel(ForegroundingDlgConst.UniPrice);
        lblTotlePrice = new JLabel(ForegroundingDlgConst.TotlePrice);
        lblCustomer = new JLabel(ForegroundingDlgConst.Customer);
        cmbProdCode = new JComboBox();
        cmbProdName = new JComboBox();
        cmbProdBrand = new JComboBox();
        cmbProdFactory = new JComboBox();
        cmbProdStyle = new JComboBox();
        cmbProdColor = new JComboBox();
        cmbProdPack = new JComboBox();
        cmbProdCount = new JComboBox();
        tfdUnitPrice = new JTextField();
        tfdTotlePrice = new JTextField();
        lblUnit1 = new JLabel(ForegroundingDlgConst.Unit);
        lblUnit2 = new JLabel(ForegroundingDlgConst.Unit);
        cmbCustomer = new JComboBox();
        btnContinue = new JButton(ForegroundingDlgConst.Continue);
        btnFinish = new JButton(ForegroundingDlgConst.Finish);

        // properties----------------
        // lblProdCode.setLabelFor(cmbProdCode);
        // lblProdName.setLabelFor(cmbProdName);
        // lblProdBrand.setLabelFor(cmbProdBrand);
        // lblProdFactory.setLabelFor(cmbProdFactory);
        // lblPordStyle.setLabelFor(cmbProdStyle);
        // lblProdColor.setLabelFor(cmbProdColor);
        // lblProdPack.setLabelFor(cmbProdPack);
        // lblProdCount.setLabelFor(cmbProdCount);
        // lblUniPrice.setLabelFor(tfdUnitPrice);
        // lblTotlePrice.setLabelFor(tfdTotlePrice);
        // lblCustomer.setLabelFor(cmbCustomer);

        // relayout------------
        general.setPreferredSize(new Dimension(getWidth(), CustOpts.BTN_HEIGHT * 4 + CustOpts.VER_GAP * 5));
        general.setLayout(null);
        relayout();

        // build----------------
        general.add(lblProdCode);
        general.add(lblProdName);
        general.add(lblProdBrand);
        general.add(lblProdFactory);
        general.add(lblProdStyle);
        general.add(lblProdColor);
        general.add(lblProdPack);
        general.add(lblProdCount);
        general.add(lblUniPrice);
        general.add(lblTotlePrice);
        general.add(lblCustomer);
        general.add(cmbProdCode);
        general.add(cmbProdName);
        general.add(cmbProdBrand);
        general.add(cmbProdFactory);
        general.add(cmbProdStyle);
        general.add(cmbProdColor);
        general.add(cmbProdPack);
        general.add(cmbProdCount);
        general.add(tfdUnitPrice);
        general.add(tfdTotlePrice);
        general.add(lblUnit1);
        general.add(lblUnit2);
        general.add(cmbCustomer);
        general.add(btnContinue);
        general.add(btnFinish);
        addTab(ForegroundingDlgConst.OpenBox, general);

        // listener-------------
        general.addComponentListener(this);
        cmbProdCode.addActionListener(this);
        cmbProdName.addActionListener(this);
        cmbProdCount.addActionListener(this);
        cmbCustomer.addActionListener(this);
        btnContinue.addActionListener(this);
        btnFinish.addActionListener(this);

        // init contents-------------
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initContents();
            }
        });
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        relayout();
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

    public void actionPerformed(
            ActionEvent e) {
        Object tSource = e.getSource();
        if (tSource == btnContinue) {
            String tmpBoxNumber = tfdTotlePrice.getText();
            if (tmpBoxNumber == null || tmpBoxNumber.length() == 0) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                return;
            }
            tmpBoxNumber = (String) cmbProdName.getSelectedItem();
            if (tmpBoxNumber == null || tmpBoxNumber.length() == 0) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                return;
            }
            addRecord(tmpBoxNumber);
            initContents();
        } else if (tSource == btnFinish) {
            String tmpBoxNumber = tfdTotlePrice.getText();
            if (tmpBoxNumber == null || tmpBoxNumber.length() == 0) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                return;
            }
            tmpBoxNumber = (String) cmbProdName.getSelectedItem();
            if (tmpBoxNumber == null || tmpBoxNumber.length() == 0) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                return;
            }
            addRecord(tmpBoxNumber);
            initContents();
            calculate();
        } else if (tSource == cmbProdCode) {
            initContents();
        } else if (tSource == cmbProdName)
            initContents();
    }

    public void addRecord(
            String pBoxNumber) {
    }

    public void initContents() {
        String tProdName = (String) cmbProdName.getSelectedItem();
        if (tProdName == null || tProdName.length() < 1) {
            cmbProdCode.setSelectedIndex(-1);
            cmbProdName.setSelectedIndex(-1);
            cmbProdBrand.setSelectedIndex(-1);
            cmbProdFactory.setSelectedIndex(-1);
            cmbProdStyle.setSelectedIndex(-1);
            cmbProdColor.setSelectedIndex(-1);
            cmbProdPack.setSelectedIndex(-1);
            cmbProdCount.setSelectedIndex(-1);
            tfdUnitPrice.setText("");
            tfdTotlePrice.setText("");
            cmbCustomer.setSelectedIndex(-1);
            btnContinue.setEnabled(false);
            btnFinish.setEnabled(false);
        } else {

        }
    }

    private void calculate() {

    }

    private void relayout() {
        int tWidth = getWidth();
        int tColumn1 = tWidth - CustOpts.HOR_GAP * 4 - CustOpts.BTN_WIDTH * 4;
        lblProdCode.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblProdCode.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        cmbProdCode.setBounds(lblProdCode.getX() + lblProdCode.getWidth(), lblProdCode.getY(),
                tColumn1 - lblProdCode.getWidth(), CustOpts.LBL_HEIGHT);
        lblProdName.setBounds(lblProdCode.getX(), lblProdCode.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblProdCode.getWidth(), CustOpts.BTN_HEIGHT);
        cmbProdName.setBounds(lblProdName.getX() + lblProdName.getWidth(), lblProdName.getY(), cmbProdCode.getWidth(),
                CustOpts.LBL_HEIGHT);
        lblProdBrand.setBounds(lblProdName.getX(), lblProdName.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblProdName.getWidth(), CustOpts.BTN_HEIGHT);
        cmbProdBrand.setBounds(lblProdBrand.getX() + lblProdBrand.getWidth(), lblProdBrand.getY(),
                cmbProdCode.getWidth(), CustOpts.LBL_HEIGHT);
        lblProdFactory.setBounds(lblProdBrand.getX(), lblProdBrand.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblProdBrand.getWidth(), CustOpts.BTN_HEIGHT);
        cmbProdFactory.setBounds(lblProdFactory.getX() + lblProdFactory.getWidth(), lblProdFactory.getY(),
                cmbProdCode.getWidth(), CustOpts.LBL_HEIGHT);

        lblProdStyle.setBounds(cmbProdCode.getX() + cmbProdCode.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP,// cmbProdCode.getY(),
                lblProdStyle.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cmbProdStyle.setBounds(lblProdStyle.getX() + lblProdStyle.getWidth(), lblProdStyle.getY(), CustOpts.BTN_WIDTH
                * 2 - lblProdStyle.getWidth(), CustOpts.LBL_HEIGHT);
        lblProdColor.setBounds(lblProdStyle.getX(), lblProdStyle.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblProdStyle.getWidth(), CustOpts.BTN_HEIGHT);
        cmbProdColor.setBounds(cmbProdStyle.getX(), cmbProdStyle.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                cmbProdStyle.getWidth(), CustOpts.LBL_HEIGHT);
        lblProdPack.setBounds(lblProdColor.getX(), lblProdColor.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblProdColor.getWidth(), CustOpts.BTN_HEIGHT);
        cmbProdPack.setBounds(cmbProdColor.getX(), cmbProdColor.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                cmbProdColor.getWidth(), CustOpts.LBL_HEIGHT);
        lblProdCount.setBounds(lblProdPack.getX(), lblProdPack.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblProdPack.getWidth(), CustOpts.BTN_HEIGHT);
        cmbProdCount.setBounds(cmbProdPack.getX(), cmbProdPack.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                cmbProdPack.getWidth(), CustOpts.LBL_HEIGHT);

        lblUniPrice.setBounds(cmbProdStyle.getX() + cmbProdStyle.getWidth() + CustOpts.HOR_GAP, cmbProdStyle.getY(),
                lblUniPrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdUnitPrice.setBounds(lblUniPrice.getX() + lblUniPrice.getWidth(), lblUniPrice.getY(), CustOpts.BTN_WIDTH * 2
                - lblUniPrice.getWidth() - lblUnit1.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblUnit1.setBounds(tfdUnitPrice.getX() + tfdUnitPrice.getWidth(), tfdUnitPrice.getY(),
                lblUnit1.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblTotlePrice.setBounds(lblUniPrice.getX(), lblUniPrice.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblUniPrice.getWidth(), CustOpts.BTN_HEIGHT);
        tfdTotlePrice
                .setBounds(tfdUnitPrice.getX(), lblTotlePrice.getY(), tfdUnitPrice.getWidth(), CustOpts.LBL_HEIGHT);
        lblUnit2.setBounds(lblUnit1.getX(), tfdTotlePrice.getY(), lblUnit2.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        lblCustomer.setBounds(lblTotlePrice.getX(), lblTotlePrice.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                lblTotlePrice.getWidth(), CustOpts.BTN_HEIGHT);
        cmbCustomer.setBounds(tfdTotlePrice.getX(), lblCustomer.getY(),
                CustOpts.BTN_WIDTH * 2 - lblUniPrice.getWidth(), CustOpts.LBL_HEIGHT);
        btnContinue.setBounds(lblCustomer.getX(), lblCustomer.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        btnFinish.setBounds(btnContinue.getX() + CustOpts.BTN_WIDTH + CustOpts.HOR_GAP, btnContinue.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
    }

    private JPanel general;

    private JLabel lblProdCode;
    private JLabel lblProdName;
    private JLabel lblProdBrand;
    private JLabel lblProdFactory;
    private JLabel lblProdStyle;
    private JLabel lblProdColor;
    private JLabel lblProdPack;
    private JLabel lblProdCount;
    private JLabel lblUniPrice;
    private JLabel lblTotlePrice;
    private JLabel lblCustomer;

    private JComboBox cmbProdCode;
    private JComboBox cmbProdName;
    private JComboBox cmbProdBrand;
    private JComboBox cmbProdFactory;
    private JComboBox cmbProdStyle;
    private JComboBox cmbProdColor;
    private JComboBox cmbProdPack;
    private JComboBox cmbProdCount;
    private JTextField tfdUnitPrice;
    private JTextField tfdTotlePrice;
    private JLabel lblUnit1;
    private JLabel lblUnit2;
    private JComboBox cmbCustomer;

    private JButton btnContinue;
    private JButton btnFinish;
}
