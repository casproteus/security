package org.cas.client.platform.magicbath.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;

public class CleanSalaryPanel extends JTabbedPane implements ComponentListener, ActionListener {
    public CleanSalaryPanel() {
        // init-----------
        general = new JPanel();
        lblSeleEmployee = new JLabel(MagicbathDlgConst.SeleEmployee);
        cmbEmployee = new JComboBox();
        pHeader = new JPanel();
        lblBoxNumber = new JLabel(MagicbathDlgConst.BoxNumber);
        lblService = new JLabel(MagicbathDlgConst.Service);
        lblHour = new JLabel(MagicbathDlgConst.Hours);
        lblPrice = new JLabel(MagicbathDlgConst.Salary);
        tblContent = new PIMTable();
        srpContent = new JScrollPane(tblContent);
        ok = new JButton(MagicbathDlgConst.Calculate);

        // properties----------------
        general.setLayout(null);
        pHeader.setLayout(null);
        lblSeleEmployee.setOpaque(true);
        lblBoxNumber.setOpaque(true);
        lblService.setOpaque(true);
        lblHour.setOpaque(true);
        lblPrice.setOpaque(true);
        lblSeleEmployee.setHorizontalAlignment(JLabel.CENTER);
        lblBoxNumber.setHorizontalAlignment(JLabel.CENTER);
        lblService.setHorizontalAlignment(JLabel.CENTER);
        lblPrice.setHorizontalAlignment(JLabel.CENTER);
        lblHour.setHorizontalAlignment(JLabel.CENTER);
        lblSeleEmployee.setBackground(Color.LIGHT_GRAY);
        lblBoxNumber.setBackground(Color.LIGHT_GRAY);
        lblService.setBackground(Color.LIGHT_GRAY);
        lblPrice.setBackground(Color.LIGHT_GRAY);
        lblHour.setBackground(Color.LIGHT_GRAY);
        lblSeleEmployee.setBorder(new LineBorder(Color.GRAY));
        lblBoxNumber.setBorder(new LineBorder(Color.GRAY));
        lblService.setBorder(new LineBorder(Color.GRAY));
        lblPrice.setBorder(new LineBorder(Color.GRAY));
        lblHour.setBorder(new LineBorder(Color.GRAY));
        lblSeleEmployee.setLabelFor(cmbEmployee);
        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setRowHeight(16);
        tblContent.setDataVector(new Object[4][10], new Object[10]);
        for (int i = 0, len = 10; i < len; i++) {
            PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
            tmpCol.setWidth(80);
            tmpCol.setPreferredWidth(80);
        }
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBorder(new LineBorder(Color.GRAY));
        tLbl.setBackground(Color.LIGHT_GRAY);
        srpContent.setCorner(JScrollPane.LOWER_LEFT_CORNER, tLbl);

        // relayout------------
        general.setPreferredSize(new Dimension(getWidth(), CustOpts.BTN_HEIGHT * 6 + CustOpts.VER_GAP * 3));
        pHeader.setPreferredSize(new Dimension(CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT * 3));
        pHeader.setSize(new Dimension(CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT * 3));

        relayout();

        // build----------------
        pHeader.add(lblService);
        pHeader.add(lblBoxNumber);
        pHeader.add(lblHour);
        pHeader.add(lblPrice);
        srpContent.setRowHeaderView(pHeader);
        general.add(lblSeleEmployee);
        general.add(cmbEmployee);
        general.add(srpContent);
        general.add(ok);
        addTab(MagicbathDlgConst.Clean, general);

        // listener-------------
        general.addComponentListener(this);
        ok.addActionListener(this);
        cmbEmployee.addActionListener(this);

        // init contents-------------
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initComponents();
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
        if (tSource == ok) {
            String tEmployee = (String) cmbEmployee.getSelectedItem();
            if (tEmployee == null) { // the first cell is null, means the table is empty.
                JOptionPane.showMessageDialog(this, MagicbathDlgConst.NoEmpSelected);
                return;
            }
            Object[][] tmpObj = new Object[4][tblContent.getColumnCount()];
            for (int i = 0; i < tblContent.getColumnCount(); i++)
                for (int j = 0; j < 4; j++)
                    tmpObj[j][i] = tblContent.getValueAt(j, i);
            for (int i = 0; i < employeeSubjectAry.length; i++)
                if (tEmployee.equals(employeeSubjectAry[i])) {
                    new InnerCleanFrm(tmpObj, employeeIDAry[i]).setVisible(true);
                    break;
                }
            tblContent.setDataVector(new Object[4][10], new Object[10]);
            for (int i = 0, len = 10; i < len; i++) {
                PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
                tmpCol.setWidth(80);
                tmpCol.setPreferredWidth(80);
            }
            cmbEmployee.setSelectedIndex(-1);
        } else if (tSource == cmbEmployee) {
            String tEmployee = (String) cmbEmployee.getSelectedItem();
            if (tEmployee == null || tEmployee.length() < 1)
                return;
            for (int i = 0; i < employeeSubjectAry.length; i++) {
                if (tEmployee.equals(employeeSubjectAry[i])) { // get the matched ID of the Employee.
                    Time tmpTime = null; // get the time property of the Employee.
                    Date tmpDate = null;
                    String sql = "Select JOINTIME from Employee where ID = ".concat(String.valueOf(employeeIDAry[i]));
                    try {
                        ResultSet rs =
                                PIMDBModel.getConection()
                                        .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                        .executeQuery(sql);
                        rs.beforeFirst();
                        while (rs.next()) {
                            tmpTime = rs.getTime("JOINTIME");
                            tmpDate = rs.getDate("JOINTIME");
                            break;
                        }
                        rs.close();// 关闭
                    } catch (SQLException exp) {
                        ErrorUtil.write(exp);
                    }

                    int[] tmpCustomIDAryOfSalRec = null;// get all the SaleRecord related with the boxNumber
                    int[] tmpProducIDAryOfSalRec = null;// get all the SaleRecord related with the boxNumber
                    int[] tmpCountAryOfSaleRec = null; // since the OpenBox time. and fetch the Product IDs and the
                                                       // Counts.
                    sql =
                            "Select CONTACTID, PRODUCTID, AMOUNT from Output where EMPLOYEEID = "
                                    .concat(String.valueOf(employeeIDAry[i])).concat(" and time >= '")
                                    .concat(tmpDate.toString()).concat(" ").concat(tmpTime.toString())
                                    .concat("' and DELETED != 'true'");
                    try {
                        ResultSet rs =
                                PIMDBModel.getConection()
                                        .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                        .executeQuery(sql);
                        ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

                        rs.afterLast();
                        rs.relative(-1);
                        int tmpPos = rs.getRow();
                        tmpCustomIDAryOfSalRec = new int[tmpPos];
                        tmpProducIDAryOfSalRec = new int[tmpPos];
                        tmpCountAryOfSaleRec = new int[tmpPos];
                        rs.beforeFirst();

                        tmpPos = 0;
                        while (rs.next()) {
                            tmpCustomIDAryOfSalRec[tmpPos] = rs.getInt("CONTACTID");
                            tmpProducIDAryOfSalRec[tmpPos] = rs.getInt("PRODUCTID");
                            tmpCountAryOfSaleRec[tmpPos] = rs.getInt("AMOUNT"); // count the number of each Product.
                            tmpPos++;
                        }
                        rs.close();// 关闭
                    } catch (SQLException exp) {
                        ErrorUtil.write(exp);
                    }

                    int tCols = tmpProducIDAryOfSalRec.length > 10 ? tmpProducIDAryOfSalRec.length : 10;
                    Object[][] tValues = new Object[4][tCols];
                    tblContent.setDataVector(tValues, new Object[tCols]);
                    for (int m = 0; m < tCols; m++) {
                        PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(m);
                        tmpCol.setWidth(80);
                        tmpCol.setPreferredWidth(80);
                    }

                    for (int j = 0; j < tmpProducIDAryOfSalRec.length; j++) { // add the content to the table.
                        for (int m = 0; m < contactIDAry.length; m++)
                            if (contactIDAry[m] == tmpCustomIDAryOfSalRec[j]) {
                                tblContent.setValueAt(contactSubjectAry[m], 0, j);
                                break;
                            }
                        for (int m = 0; m < productIDAry.length; m++)
                            if (productIDAry[m] == tmpProducIDAryOfSalRec[j]) {
                                tblContent.setValueAt(productSubjectAry[m], 1, j);
                                break;
                            }

                        tblContent.setValueAt(tmpCountAryOfSaleRec[j] > 0 ? tmpCountAryOfSaleRec[j] : "", 2, j);// set
                                                                                                                // the
                                                                                                                // total
                                                                                                                // price.

                        for (int m = 0; m < prodIDsInInput.length; m++)
                            if (prodIDsInInput[m] == tmpProducIDAryOfSalRec[j]) {
                                tblContent.setValueAt(tmpCountAryOfSaleRec[j] > 0 ? tmpCountAryOfSaleRec[j]
                                        * prodPricesInInput[m] / 100 : "", 3, j);// set the total price.
                                break;
                            }
                    }
                    tblContent.validate();
                    tblContent.revalidate();
                    tblContent.invalidate();
                    break;
                }
            }
        }
    }

    private void initComponents() {
        // for the Employee
        String sql = "select ID, SUBJECT from Employee where DELETED != 'true'";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            employeeIDAry = new int[tmpPos];
            employeeSubjectAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                employeeIDAry[tmpPos] = rs.getInt("ID");
                employeeSubjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        cmbEmployee.setModel(new DefaultComboBoxModel(employeeSubjectAry));
        cmbEmployee.setSelectedIndex(-1);

        // for the service.
        sql = "select ID, SUBJECT from Product where DELETED != 'true'";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            productIDAry = new int[tmpPos];
            productSubjectAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                productIDAry[tmpPos] = rs.getInt("ID");
                productSubjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        // for the boxNumber
        sql = "select ID, SUBJECT from Contact where DELETED != 'true'";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            contactIDAry = new int[tmpPos];
            contactSubjectAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                contactIDAry[tmpPos] = rs.getInt("ID");
                contactSubjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        // for the prices
        sql = "select PRODUCTID, TOLTALPRICE from Input where DELETED != 'true'";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            prodIDsInInput = new int[tmpPos];
            prodPricesInInput = new int[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                prodIDsInInput[tmpPos] = rs.getInt("PRODUCTID");
                prodPricesInInput[tmpPos] = rs.getInt("TOLTALPRICE");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
    }

    private void relayout() {
        lblSeleEmployee.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cmbEmployee.setBounds(lblSeleEmployee.getX() + lblSeleEmployee.getWidth() + CustOpts.HOR_GAP,
                lblSeleEmployee.getY(), (getWidth() - 7 * CustOpts.HOR_GAP - 3 * CustOpts.BTN_WIDTH) / 3,
                CustOpts.BTN_HEIGHT);
        srpContent.setBounds(lblSeleEmployee.getX(), lblSeleEmployee.getY() + lblSeleEmployee.getHeight()
                + CustOpts.VER_GAP, getWidth() - CustOpts.HOR_GAP * 2, 82);
        ok.setBounds(getWidth() - CustOpts.HOR_GAP - 100,
                srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);

        lblBoxNumber.setBounds(0, 0, CustOpts.BTN_WIDTH, tblContent.getRowHeight());
        lblService.setBounds(0, lblBoxNumber.getY() + lblBoxNumber.getHeight(), CustOpts.BTN_WIDTH,
                lblBoxNumber.getHeight());
        lblHour.setBounds(0, lblService.getY() + lblService.getHeight(), CustOpts.BTN_WIDTH, lblService.getHeight());
        lblPrice.setBounds(0, lblHour.getY() + lblHour.getHeight(), CustOpts.BTN_WIDTH, lblHour.getHeight());

        revalidate();
        invalidate();
        repaint();
    }

    private JPanel general;
    private JLabel lblSeleEmployee;
    private JComboBox cmbEmployee;
    private JScrollPane srpContent;
    private JPanel pHeader;
    private JLabel lblBoxNumber;
    private JLabel lblService;
    private JLabel lblHour;
    private JLabel lblPrice;
    private PIMTable tblContent;
    private JButton ok;

    private int[] employeeIDAry;
    private String[] employeeSubjectAry;
    private int[] productIDAry;
    private String[] productSubjectAry;
    private int[] contactIDAry;
    private String[] contactSubjectAry;
    private int[] prodIDsInInput;
    private int[] prodPricesInInput;
}
