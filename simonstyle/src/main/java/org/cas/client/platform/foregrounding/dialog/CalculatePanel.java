package org.cas.client.platform.foregrounding.dialog;

import java.awt.Color;
import java.awt.Dimension;
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

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pimview.pimtable.PIMTableModelAryBased;
import org.cas.client.resource.international.DlgConst;

public class CalculatePanel extends JTabbedPane implements ComponentListener, ActionListener {
    public CalculatePanel() {
        // init-----------
        general = new JPanel();
        lblSeleBoxNumber = new JLabel(ForegroundingDlgConst.SeleBoxNumber);
        cmbBoxNumber = new JComboBox();
        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);
        ok = new JButton(ForegroundingDlgConst.Calculate);

        // properties----------------
        general.setLayout(null);
        lblSeleBoxNumber.setOpaque(true);
        lblSeleBoxNumber.setHorizontalAlignment(JLabel.CENTER);
        lblSeleBoxNumber.setBackground(Color.LIGHT_GRAY);
        lblSeleBoxNumber.setBorder(new LineBorder(Color.GRAY));
        lblSeleBoxNumber.setLabelFor(cmbBoxNumber);
        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(16);

        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);

        // relayout------------
        general.setPreferredSize(new Dimension(getWidth(), CustOpts.BTN_HEIGHT * 2 + CustOpts.VER_GAP * 3));
        relayout();

        // build----------------
        general.add(lblSeleBoxNumber);
        general.add(cmbBoxNumber);
        general.add(srpContent);
        general.add(ok);
        addTab(ForegroundingDlgConst.CustCalculate, general);

        // listener-------------
        general.addComponentListener(this);
        cmbBoxNumber.addActionListener(this);
        ok.addActionListener(this);

        // init contents-------------
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initHeader();
                Object[][] tValues = new Object[10][header.length];
                tblContent.setDataVector(tValues, header);
                DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
                tCellRender.setOpaque(true);
                tCellRender.setBackground(Color.LIGHT_GRAY);
                tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
                for (int i = 0, len = header.length; i < len; i++) {
                    PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
                    tmpCol.setWidth(80);
                    tmpCol.setPreferredWidth(80);
                }
                tblContent.validate();
                tblContent.revalidate();
                tblContent.invalidate();
                initBoxNumbers();
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
            if (tblContent.getValueAt(0, 0) == null) { // the first cell is null, means the table is empty.
                JOptionPane.showMessageDialog(this, ForegroundingDlgConst.NoUserSelected);
                return;
            }

            int tValidRowCount = -1;
            int tRowCount = tblContent.getRowCount();
            for (int i = 0; i < tRowCount; i++)
                // get the used RowCount
                if (tblContent.getValueAt(i, 0) == null) {
                    tValidRowCount = i;
                    break;
                }
            if (tValidRowCount == -1)
                tValidRowCount = tRowCount;
            Object[][] tValues = new Object[tValidRowCount][header.length];
            for (int i = 0; i < tValidRowCount; i++)
                for (int j = 0; j < header.length; j++)
                    tValues[i][j] = tblContent.getValueAt(i, j);
            // new CustResultFrm(tValues, header).setVisible(true);
            cmbBoxNumber.setSelectedIndex(-1);

            tValues = new Object[10][header.length];
            tblContent.setDataVector(tValues, header);
            DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
            tCellRender.setOpaque(true);
            tCellRender.setBackground(Color.LIGHT_GRAY);
            tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
            for (int i = 0, len = header.length; i < len; i++) {
                PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
                tmpCol.setWidth(80);
                tmpCol.setPreferredWidth(80);
            }

            tblContent.validate();
            tblContent.revalidate();
            tblContent.invalidate();
        } else if (tSource == cmbBoxNumber) {
            String tBoxNumberSub = (String) cmbBoxNumber.getSelectedItem();
            if (tBoxNumberSub == null || tBoxNumberSub.length() < 1)
                return;
            if (isRepeated(tBoxNumberSub))
                if (JOptionPane.showConfirmDialog(this, ForegroundingDlgConst.ConfirmMsgOfRepeatedBoxNumber,
                        DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0)
                    return;
            for (int i = 0; i < contactSubjectAry.length; i++) {
                if (tBoxNumberSub.equals(contactSubjectAry[i])) { // get the matched ID of the boxNubmer.
                    Time tmpTime = null; // get the time property of the boxNumber.
                    Date tmpDate = null;
                    String sql = "Select ANNIVERSARY from Contact where ID = ".concat(String.valueOf(contactIDAry[i]));
                    try {
                        ResultSet rs =
                                PIMDBModel.getConection()
                                        .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                        .executeQuery(sql);
                        rs.beforeFirst();
                        while (rs.next()) {
                            tmpTime = rs.getTime("ANNIVERSARY");
                            tmpDate = rs.getDate("ANNIVERSARY");
                            break;
                        }
                        rs.close();// 关闭
                    } catch (SQLException exp) {
                        ErrorUtil.write(exp);
                    }

                    int[] tmpProducIDAryOfSalRec = null;// get all the SaleRecord related with the boxNumber
                    int[] tmpCountAryOfSaleRec = null; // since the OpenBox time. and fetch the Product IDs and the
                                                       // Counts.
                    sql =
                            "Select PRODUCTID, AMOUNT from Output where CONTACTID = "
                                    .concat(String.valueOf(contactIDAry[i])).concat(" and time >= '")
                                    .concat(tmpDate.toString()).concat(" ").concat(tmpTime.toString()).concat("'")
                                    .concat(" and DELETED != 'true'");
                    try {
                        ResultSet rs =
                                PIMDBModel.getConection()
                                        .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                        .executeQuery(sql);
                        ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

                        rs.afterLast();
                        rs.relative(-1);
                        int tmpPos = rs.getRow();
                        tmpProducIDAryOfSalRec = new int[tmpPos];
                        tmpCountAryOfSaleRec = new int[tmpPos];
                        rs.beforeFirst();

                        tmpPos = 0;
                        while (rs.next()) {
                            tmpProducIDAryOfSalRec[tmpPos] = rs.getInt("PRODUCTID");
                            tmpCountAryOfSaleRec[tmpPos] = rs.getInt("AMOUNT"); // count the number of each Product.
                            tmpPos++;
                        }
                        rs.close();// 关闭
                    } catch (SQLException exp) {
                        ErrorUtil.write(exp);
                    }

                    int tRowCount = tblContent.getRowCount(); // add content to the table.
                    int tColCount = tblContent.getColumnCount();
                    int tValidRowCount = -1;
                    for (int j = 0; j < tRowCount; j++)
                        // get the used RowCount
                        if (tblContent.getValueAt(j, 0) == null) {
                            tValidRowCount = j;
                            break;
                        }
                    if (tValidRowCount == -1) { // no line is empty, add a new Line.
                        Object[][] tValues = new Object[tRowCount + 10][tColCount];
                        for (int r = 0; r < tRowCount; r++)
                            for (int c = 0; c < tColCount; c++)
                                tValues[r][c] = tblContent.getValueAt(r, c);
                        tblContent.setDataVector(tValues, header);
                        tblContent.validate();
                        tblContent.revalidate();
                        tblContent.invalidate();
                        tValidRowCount = tRowCount;
                    }
                    tblContent.setValueAt(tBoxNumberSub, tValidRowCount, 0); // set the value of the first cell.
                    int tPriceAddup = 0; // add up the price.
                    for (int j = 2; j < tColCount; j++) { // add the content to the table.
                        int tHeadStrID = productIDAry[j - 2]; // fetch the id of the product in this column
                        int tCount = 0;
                        for (int m = 0; m < tmpProducIDAryOfSalRec.length; m++)
                            // check each SaleRecord.
                            if (tHeadStrID == tmpProducIDAryOfSalRec[m]) {
                                tCount += tmpCountAryOfSaleRec[m];
                                tPriceAddup += productPriceAry[j - 2] * tmpCountAryOfSaleRec[m];
                            }
                        tblContent.setValueAt(tCount > 0 ? tCount : "", tValidRowCount, j);
                        tblContent.setValueAt(tPriceAddup > 0 ? tPriceAddup / 100 : "", tValidRowCount, 1);// set the
                                                                                                           // total
                                                                                                           // price.
                    }
                    break;
                }
            }
        }
    }

    private boolean isRepeated(
            String args) {
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++) {
            String tValue = (String) tblContent.getValueAt(i, 0);
            if (tValue == null)
                return false;
            else if (tValue.equals(args))
                return true;
        }
        return false;
    }

    private String[] initHeader() {
        String sql = "select ID, SUBJECT, PRICE from Product where DELETED != 'true'";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            productIDAry = new int[tmpPos];
            productPriceAry = new int[tmpPos];
            productSubjectAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                productIDAry[tmpPos] = rs.getInt("ID");
                productPriceAry[tmpPos] = rs.getInt("PRICE"); // get the prices of each Product.
                productSubjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭

            header = new String[tmpPos + 2];
            header[0] = ForegroundingDlgConst.BoxNumber;
            header[1] = ForegroundingDlgConst.AddUp;
            for (int i = 0; i < tmpPos; i++) {
                header[i + 2] = productSubjectAry[i];
            }
            return header;
        } catch (SQLException e) {
            ErrorUtil.write(e);
            return null;
        }
    }

    public void initBoxNumbers() {
        String sql = "select ID, SUBJECT from Contact where anniversary is not null and DELETED != 'true'";
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
        cmbBoxNumber.setModel(new DefaultComboBoxModel(contactSubjectAry));
        cmbBoxNumber.setSelectedIndex(-1);
    }

    private void relayout() {
        lblSeleBoxNumber.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cmbBoxNumber.setBounds(lblSeleBoxNumber.getX() + lblSeleBoxNumber.getWidth() + CustOpts.HOR_GAP,
                lblSeleBoxNumber.getY(), (getWidth() - 7 * CustOpts.HOR_GAP - 3 * CustOpts.BTN_WIDTH) / 3,
                CustOpts.BTN_HEIGHT);
        srpContent.setBounds(lblSeleBoxNumber.getX(), lblSeleBoxNumber.getY() + lblSeleBoxNumber.getHeight()
                + CustOpts.VER_GAP, general.getWidth() - CustOpts.HOR_GAP * 2,
                general.getHeight() - lblSeleBoxNumber.getY() - lblSeleBoxNumber.getHeight() - CustOpts.VER_GAP * 3
                        - CustOpts.BTN_HEIGHT);
        ok.setBounds(getWidth() - CustOpts.HOR_GAP - 100,
                srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);

        revalidate();
        invalidate();
        repaint();
    }

    private JPanel general;
    private JLabel lblSeleBoxNumber;
    private JComboBox cmbBoxNumber;
    private PIMScrollPane srpContent;
    private PIMTable tblContent;
    private JButton ok;

    int[] contactIDAry;
    String[] contactSubjectAry;
    private int[] productIDAry;
    private int[] productPriceAry;
    private String[] productSubjectAry;
    String[] header;
}
