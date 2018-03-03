package org.cas.client.platform.badminton.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;

public class CASBathBar extends JPanel implements ComponentListener, ActionListener {
    // 构造方法
    public CASBathBar() {
        init();
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

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Object tSource = e.getSource();
        if (tSource == btnOK) {
            CASUtility.addToTotleIn(Integer.parseInt(lblMoneyIn.getText())); // 本次收入计入总收入中
            CASUtility.addToTotleOut(Integer.parseInt(lblMoneyOut.getText())); // 本次支出计入总支出中
            CustOpts.custOps.setKeyAndValue("TotleMoneyIn", "0");
            CustOpts.custOps.setKeyAndValue("TotleMoneyOut", "0");
            lblMoneyIn.setText("0");
            lblMoneyOut.setText("0");
            lblMoneyLeft.setText("0");
        }
    }

    private void init() {
        setLayout(null);// new FlowLayout(FlowLayout.LEFT, 2, 0));
        setBorder(BorderFactory.createLineBorder(Color.lightGray));
        // 初始化------------------------
        lblDragLabel = new JLabel();
        lblMoneyInLabel = new JLabel(BadmintonDlgConst.MoneyIn);
        lblMoneyOutLabel = new JLabel(BadmintonDlgConst.MoneyOut);
        lblMoneyLeftLabel = new JLabel(BadmintonDlgConst.MoneyLeft);
        lblMoneyEarnLabel = new JLabel(BadmintonDlgConst.MoneyEarn);
        lblMoneyUnit = new JLabel(BadmintonDlgConst.MoneyUnit);
        lblMoneyIn = new JLabel();
        lblMoneyOut = new JLabel();
        lblMoneyLeft = new JLabel();
        lblMoneyEarn = new JLabel();
        btnOK = new JButton(BadmintonDlgConst.CheckOut);

        // 属性---------------------------------
        lblDragLabel.setIcon(CustOpts.custOps.getHorBarIcon());
        Object tValueIn = CustOpts.custOps.getValue("TotleMoneyIn");
        Object tValueOut = CustOpts.custOps.getValue("TotleMoneyOut");
        lblMoneyIn.setText(tValueIn == null ? "0" : (String) tValueIn);
        lblMoneyOut.setText(tValueOut == null ? "0" : (String) tValueOut);
        int tIn = tValueIn == null ? 0 : Integer.parseInt((String) tValueIn);
        int tOut = tValueOut == null ? 0 : Integer.parseInt((String) tValueOut);
        lblMoneyLeft.setText(String.valueOf(tIn - tOut));

        // 搭建----------------------------
        add(lblDragLabel);
        add(lblMoneyInLabel);
        add(lblMoneyIn);
        add(lblMoneyOutLabel);
        add(lblMoneyOut);
        add(lblMoneyLeftLabel);
        add(lblMoneyLeft);
        add(lblMoneyEarnLabel);
        add(lblMoneyEarn);
        add(lblMoneyUnit);
        add(btnOK);

        // 布局----------------------------
        setPreferredSize(new Dimension(300, CustOpts.LBL_HEIGHT));
        reLayout();
        // 监听----------------------------
        addComponentListener(this);
        btnOK.addActionListener(this);
    }

    private void reLayout() {
        lblDragLabel.setBounds(3, 0, lblDragLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblMoneyInLabel.setBounds(lblDragLabel.getX() + lblDragLabel.getWidth() + CustOpts.HOR_GAP,
                lblDragLabel.getY(), lblMoneyInLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        btnOK.setBounds(getWidth() - CustOpts.BTN_WIDTH - CustOpts.SIZE_EDGE * 2 - 4, lblMoneyEarn.getY(),
                CustOpts.BTN_WIDTH, CustOpts.LBL_HEIGHT);
        lblMoneyUnit.setBounds(btnOK.getX() - lblMoneyUnit.getPreferredSize().width - 10, lblMoneyEarn.getY(),
                lblMoneyUnit.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        int tWidth = lblMoneyUnit.getX() - lblMoneyInLabel.getX();
        lblMoneyOutLabel.setBounds(tWidth / 4, lblMoneyInLabel.getY(), lblMoneyOutLabel.getPreferredSize().width,
                CustOpts.LBL_HEIGHT);
        lblMoneyLeftLabel.setBounds(tWidth / 2, lblMoneyOutLabel.getY(), lblMoneyLeftLabel.getPreferredSize().width,
                CustOpts.LBL_HEIGHT);
        lblMoneyEarnLabel.setBounds(tWidth * 3 / 4, lblMoneyLeftLabel.getY(),
                lblMoneyEarnLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblMoneyIn.setBounds(lblMoneyInLabel.getX() + lblMoneyInLabel.getWidth(), lblMoneyInLabel.getY(),
                lblMoneyOutLabel.getX() - lblMoneyInLabel.getX() - lblMoneyInLabel.getWidth(), CustOpts.LBL_HEIGHT);
        lblMoneyOut.setBounds(lblMoneyOutLabel.getX() + lblMoneyOutLabel.getWidth(), lblMoneyOutLabel.getY(),
                lblMoneyLeftLabel.getX() - lblMoneyOutLabel.getX() - lblMoneyOutLabel.getWidth(), CustOpts.LBL_HEIGHT);
        lblMoneyLeft
                .setBounds(lblMoneyLeftLabel.getX() + lblMoneyLeftLabel.getWidth(), lblMoneyLeftLabel.getY(),
                        lblMoneyEarnLabel.getX() - lblMoneyLeftLabel.getX() - lblMoneyLeftLabel.getWidth(),
                        CustOpts.LBL_HEIGHT);
        lblMoneyEarn.setBounds(lblMoneyEarnLabel.getX() + lblMoneyEarnLabel.getWidth(), lblMoneyEarnLabel.getY(),
                lblMoneyUnit.getX() - lblMoneyEarnLabel.getX() - lblMoneyEarnLabel.getWidth(), CustOpts.LBL_HEIGHT);
    }

    public static void addMoneyIn(
            int pCount) {
        int tNewValueIn = Integer.parseInt(lblMoneyIn.getText()) + pCount;
        int tNewValueOut = Integer.parseInt(lblMoneyOut.getText());
        lblMoneyIn.setText(String.valueOf(tNewValueIn));
        lblMoneyLeft.setText(String.valueOf(tNewValueIn - tNewValueOut));
        CustOpts.custOps.setKeyAndValue("TotleMoneyIn", lblMoneyIn.getText());
    }

    public static void addMoneyOut(
            int pCount) {
        int tNewValueIn = Integer.parseInt(lblMoneyIn.getText());
        int tNewValueOut = Integer.parseInt(lblMoneyOut.getText()) + pCount;
        lblMoneyOut.setText(String.valueOf(tNewValueOut));
        lblMoneyLeft.setText(String.valueOf(tNewValueIn - tNewValueOut));
        CustOpts.custOps.setKeyAndValue("TotleMoneyOut", lblMoneyOut.getText());
    }

    private JButton btnOK;
    private JLabel lblDragLabel;
    private JLabel lblMoneyInLabel;
    private JLabel lblMoneyOutLabel;
    private JLabel lblMoneyLeftLabel;
    private JLabel lblMoneyEarnLabel;
    private JLabel lblMoneyUnit;
    private static JLabel lblMoneyIn;
    private static JLabel lblMoneyOut;
    private static JLabel lblMoneyLeft;
    private static JLabel lblMoneyEarn;
}
