package org.cas.client.platform.badminton.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.employee.EmployeeDefaultViews;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.resource.international.CustViewConsts;
import org.cas.client.resource.international.DlgConst;

public class InnerCleanFrm extends JFrame implements ActionListener, ComponentListener {
    public InnerCleanFrm(Object[][] pValues, int pID) {
        values = pValues;
        employeeID = pID;
        initComponent();
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Object s = e.getSource();
        if (s == ok) {
            closeBox();
            setVisible(false);
        } else if (s == cancel) {
            setVisible(false);
            dispose();
        }
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

    private void initComponent() {
        setIconImage(CustOpts.custOps.getFrameLogoImage()); // 图标暂设为草稿图标，（一张纸上加一支笔）
        setTitle(DlgConst.DlgTitle); // 设置标题。

        // init-------------
        general = new JPanel();
        tblContent = new PIMTable();
        srpContent = new JScrollPane(tblContent);
        lblBoxNumber = new JLabel(BadmintonDlgConst.BoxNumber);
        lblService = new JLabel(BadmintonDlgConst.Service);
        lblHour = new JLabel(BadmintonDlgConst.Hours);
        lblPrice = new JLabel(BadmintonDlgConst.Salary);
        pHeader = new JPanel();
        lblTotalPrice = new JLabel(BadmintonDlgConst.TotalPrice);
        fldTotalPrice = new JTextField(calculateToltalPrice());
        ok = new JButton(BadmintonDlgConst.CloseBox);
        cancel = new JButton(CustViewConsts.CANCEL);

        // properties----------------------
        general.setLayout(null);
        pHeader.setLayout(null);
        lblBoxNumber.setOpaque(true);
        lblService.setOpaque(true);
        lblPrice.setOpaque(true);
        lblHour.setOpaque(true);
        lblBoxNumber.setHorizontalAlignment(JLabel.CENTER);
        lblService.setHorizontalAlignment(JLabel.CENTER);
        lblPrice.setHorizontalAlignment(JLabel.CENTER);
        lblHour.setHorizontalAlignment(JLabel.CENTER);
        lblBoxNumber.setBackground(Color.LIGHT_GRAY);
        lblService.setBackground(Color.LIGHT_GRAY);
        lblPrice.setBackground(Color.LIGHT_GRAY);
        lblHour.setBackground(Color.LIGHT_GRAY);
        lblBoxNumber.setBorder(new LineBorder(Color.GRAY));
        lblService.setBorder(new LineBorder(Color.GRAY));
        lblPrice.setBorder(new LineBorder(Color.GRAY));
        lblHour.setBorder(new LineBorder(Color.GRAY));
        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setRowHeight(16);
        tblContent.setDataVector(values, new Object[values[0].length]);
        for (int i = 0, len = values[0].length; i < len; i++) {
            PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
            tmpCol.setWidth(80);
            tmpCol.setPreferredWidth(80);
        }
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBorder(new LineBorder(Color.GRAY));
        tLbl.setBackground(Color.LIGHT_GRAY);
        srpContent.setCorner(JScrollPane.LOWER_LEFT_CORNER, tLbl);

        // build-------------------------
        pHeader.add(lblService);
        pHeader.add(lblBoxNumber);
        pHeader.add(lblHour);
        pHeader.add(lblPrice);
        srpContent.setRowHeaderView(pHeader);
        getContentPane().add(general);
        general.add(srpContent);
        general.add(lblTotalPrice);
        general.add(fldTotalPrice);
        general.add(ok);
        general.add(cancel);

        // relayout-----------------------
        setBounds((CustOpts.SCRWIDTH - 560) / 2, (CustOpts.SCRHEIGHT - 160) / 2, 560, 160); // 对话框的默认尺寸。
        pHeader.setPreferredSize(new Dimension(CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT * 3));
        pHeader.setSize(new Dimension(CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT * 3));
        relayout();

        // listeners-------------------
        general.addComponentListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
    }

    private String calculateToltalPrice() {
        toltalPrice = 0;
        for (int i = 0, len = values[0].length; i < len; i++) {
            Object tObj = values[3][i];
            if (tObj != null && tObj instanceof Integer)
                toltalPrice += ((Integer) tObj).intValue();
        }
        return String.valueOf(toltalPrice);
    }

    private void relayout() {
        ok.setBounds(general.getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.BTN_WIDTH * 2, general.getHeight()
                - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cancel.setBounds(ok.getX() + ok.getWidth() + CustOpts.HOR_GAP, ok.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        lblTotalPrice.setBounds(CustOpts.HOR_GAP, ok.getY(), lblTotalPrice.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        fldTotalPrice.setBounds(lblTotalPrice.getX() + lblTotalPrice.getWidth() + CustOpts.HOR_GAP,
                lblTotalPrice.getY(), 80, CustOpts.BTN_HEIGHT);
        srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, general.getWidth() - CustOpts.HOR_GAP * 2,
                lblTotalPrice.getY() - CustOpts.HOR_GAP * 2);

        lblBoxNumber.setBounds(0, 0, CustOpts.BTN_WIDTH, tblContent.getRowHeight());
        lblService.setBounds(0, lblBoxNumber.getY() + lblBoxNumber.getHeight(), CustOpts.BTN_WIDTH,
                lblBoxNumber.getHeight());
        lblHour.setBounds(0, lblService.getY() + lblService.getHeight(), CustOpts.BTN_WIDTH, lblService.getHeight());
        lblPrice.setBounds(0, lblHour.getY() + lblHour.getHeight(), CustOpts.BTN_WIDTH, lblHour.getHeight());

        tblContent.revalidate();
        tblContent.validate();
        tblContent.invalidate();
        invalidate();
    }

    private void closeBox() {
        PIMRecord tmpRec =
                CASControl.ctrl.getModel().selectRecord(CustOpts.custOps.APPNameVec.indexOf("Employee"), employeeID,
                        5000); // to select a record from DB.
        tmpRec.setFieldValue(EmployeeDefaultViews.JOINTIME, Calendar.getInstance().getTime());// modify the Time info of
                                                                                              // the record.
        CASControl.ctrl.getModel().updateRecord(tmpRec, true); // save the record back to DB.

        CASBathBar.addMoneyOut((int) toltalPrice);
    }

    int employeeID;
    Object[][] values;
    Object[] headers;

    private PIMTable tblContent;
    private JScrollPane srpContent;
    private JLabel lblBoxNumber;
    private JLabel lblService;
    private JLabel lblHour;
    private JLabel lblPrice;
    private JPanel pHeader;
    private JPanel general;
    private JLabel lblTotalPrice;
    private JTextField fldTotalPrice;
    private JButton ok;
    private JButton cancel;

    private double toltalPrice;
}
