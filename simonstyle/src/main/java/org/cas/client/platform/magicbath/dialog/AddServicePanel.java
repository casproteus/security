package org.cas.client.platform.magicbath.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.output.OutputDefaultViews;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;

public class AddServicePanel extends JTabbedPane implements ComponentListener, ActionListener {
    public AddServicePanel() {
        // init-----------
        general = new JPanel();
        lblEmployee = new JLabel(MagicbathDlgConst.SeleEmployee);
        cmbEmployee = new JComboBox();
        lblSeleService = new JLabel(MagicbathDlgConst.SeleService);
        cmbService = new JComboBox();
        lblSeleBoxNumbers = new JLabel(MagicbathDlgConst.SeleBoxNumber);
        cmbBoxNumbers = new JComboBox();
        lblCount = new JLabel(MagicbathDlgConst.Times);
        fldCount = new JTextField();
        ok = new JButton(MagicbathDlgConst.AddIn);

        // properties----------------
        general.setLayout(null);
        lblEmployee.setOpaque(true);
        lblSeleService.setOpaque(true);
        lblSeleBoxNumbers.setOpaque(true);
        lblEmployee.setHorizontalAlignment(JLabel.CENTER);
        lblSeleService.setHorizontalAlignment(JLabel.CENTER);
        lblSeleBoxNumbers.setHorizontalAlignment(JLabel.CENTER);
        lblEmployee.setBackground(Color.LIGHT_GRAY);
        lblSeleService.setBackground(Color.LIGHT_GRAY);
        lblSeleBoxNumbers.setBackground(Color.LIGHT_GRAY);
        lblEmployee.setBorder(new LineBorder(Color.GRAY));
        lblSeleService.setBorder(new LineBorder(Color.GRAY));
        lblSeleBoxNumbers.setBorder(new LineBorder(Color.GRAY));
        lblEmployee.setLabelFor(cmbEmployee);
        lblSeleService.setLabelFor(cmbService);
        lblSeleBoxNumbers.setLabelFor(cmbBoxNumbers);
        fldCount.setText("1");
        fldCount.setHorizontalAlignment(JLabel.CENTER);

        // relayout------------
        general.setPreferredSize(new Dimension(getWidth(), CustOpts.BTN_HEIGHT * 2 + CustOpts.VER_GAP * 3));
        relayout();

        // build----------------
        general.add(lblEmployee);
        general.add(cmbEmployee);
        general.add(lblSeleService);
        general.add(cmbService);
        general.add(lblSeleBoxNumbers);
        general.add(cmbBoxNumbers);
        general.add(lblCount);
        general.add(fldCount);
        general.add(ok);
        addTab(MagicbathDlgConst.RecordService, general);

        // listener-------------
        general.addComponentListener(this);
        ok.addActionListener(this);

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
            String tmpStr = (String) cmbEmployee.getSelectedItem();
            if (tmpStr == null || tmpStr.length() == 0) {
                JOptionPane.showMessageDialog(this, MagicbathDlgConst.MsgSelectEmployee);
                cmbEmployee.requestFocus();
                return;
            }
            tmpStr = (String) cmbService.getSelectedItem();
            if (tmpStr == null || tmpStr.length() == 0) {
                JOptionPane.showMessageDialog(this, MagicbathDlgConst.MsgSelectService);
                cmbService.requestFocus();
                return;
            }
            tmpStr = (String) cmbBoxNumbers.getSelectedItem();
            if (tmpStr == null || tmpStr.length() == 0) {
                JOptionPane.showMessageDialog(this, MagicbathDlgConst.MsgSeleBoxNumber);
                cmbBoxNumbers.requestFocus();
                return;
            }
            tmpStr = fldCount.getText();
            if (tmpStr == null || tmpStr.length() < 1) {
                JOptionPane.showMessageDialog(this, MagicbathDlgConst.MsgInputCount);
                fldCount.requestFocus();
                return;
            } else {
                try {
                    count = Integer.parseInt(tmpStr);
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this, MagicbathDlgConst.MsgInputCount);
                    fldCount.selectAll();
                    fldCount.requestFocus();
                    return;
                }
            }

            addService((String) cmbEmployee.getSelectedItem(), (String) cmbService.getSelectedItem(),
                    (String) cmbBoxNumbers.getSelectedItem(), count); // to add a sale record.
            cmbEmployee.setSelectedIndex(-1); // to reset the generalPane.
            cmbService.setSelectedIndex(-1);
            cmbBoxNumbers.setSelectedIndex(-1);
            initComponents();
        }
    }

    public void addService(
            String pEmployee,
            String pService,
            String pBoxNumber,
            int pCount) {
        int tmpEmployeeID = -1;
        int tmpServiceID = -1;
        int tmpBoxNumberID = -1;

        String tmpStr = pEmployee; // get the ID of EmployeeID
        for (int i = 0; i < employeeSubjectAry.length; i++)
            if (tmpStr.equals(employeeSubjectAry[i])) {
                tmpEmployeeID = employeeIDAry[i];
                break;
            }

        tmpStr = pService; // get the ID of ProductID
        for (int i = 0; i < productSubjectAry.length; i++)
            if (tmpStr.equals(productSubjectAry[i])) {
                tmpServiceID = productIDAry[i];
                break;
            }

        tmpStr = pBoxNumber; // get the ID of CostomerID
        for (int i = 0; i < contactSubjectAry.length; i++)
            if (tmpStr.equals(contactSubjectAry[i])) {
                tmpBoxNumberID = contactIDAry[i];
                break;
            }

        tmpStr = fldCount.getText(); // get the count;

        PIMRecord tmpRec = new PIMRecord(); // to select a record from DB.
        tmpRec.setAppIndex(CustOpts.custOps.APPNameVec.indexOf("Output"));
        tmpRec.setInfolderID(5300);
        tmpRec.setFieldValue(5, Integer.valueOf(tmpEmployeeID));
        tmpRec.setFieldValue(7, Integer.valueOf(tmpServiceID));
        tmpRec.setFieldValue(11, Integer.valueOf(tmpBoxNumberID));
        tmpRec.setFieldValue(8, Integer.valueOf(pCount));
        tmpRec.setFieldValue(OutputDefaultViews.FOLDERID, Integer.valueOf(5301));

        tmpRec.setFieldValue(6, Calendar.getInstance().getTime()); // modify the Time info of the record.
        CASControl.ctrl.getModel().insertRecord(tmpRec, true); // save the record back to DB.
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
        cmbService.setModel(new DefaultComboBoxModel(productSubjectAry));
        cmbService.setSelectedIndex(-1);

        // for the boxNumber
        initBoxNumbers();
        fldCount.setText("1");
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
        cmbBoxNumbers.setModel(new DefaultComboBoxModel(contactSubjectAry));
        cmbBoxNumbers.setSelectedIndex(-1);
    }

    private void relayout() {
        int tmpComboBoxWidth = (getWidth() - 7 * CustOpts.HOR_GAP - 3 * CustOpts.BTN_WIDTH) / 3;
        lblEmployee.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cmbEmployee.setBounds(lblEmployee.getX() + lblEmployee.getWidth() + CustOpts.HOR_GAP, lblEmployee.getY(),
                tmpComboBoxWidth, CustOpts.BTN_HEIGHT);
        lblSeleService.setBounds(cmbEmployee.getX() + cmbEmployee.getWidth() + CustOpts.HOR_GAP, cmbEmployee.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cmbService.setBounds(lblSeleService.getX() + lblSeleService.getWidth() + CustOpts.HOR_GAP,
                lblSeleService.getY(), tmpComboBoxWidth, CustOpts.BTN_HEIGHT);
        lblSeleBoxNumbers.setBounds(cmbService.getX() + cmbService.getWidth() + CustOpts.HOR_GAP, cmbService.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cmbBoxNumbers.setBounds(lblSeleBoxNumbers.getX() + lblSeleBoxNumbers.getWidth() + CustOpts.HOR_GAP,
                lblSeleBoxNumbers.getY(), tmpComboBoxWidth, CustOpts.BTN_HEIGHT);
        lblCount.setBounds(lblEmployee.getX(), lblEmployee.getY() + lblEmployee.getHeight() + 7, 10,
                CustOpts.BTN_HEIGHT);
        fldCount.setBounds(lblCount.getX() + lblCount.getWidth(), lblEmployee.getY() + lblEmployee.getHeight()
                + CustOpts.VER_GAP + 5, 40, 18);

        ok.setBounds(getWidth() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, lblEmployee.getY() + lblEmployee.getHeight()
                + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

        invalidate();
        revalidate();
        repaint();
    }

    private JPanel general;
    private JLabel lblEmployee;
    private JComboBox cmbEmployee;
    private JLabel lblSeleService;
    private JComboBox cmbService;
    private JLabel lblSeleBoxNumbers;
    private JComboBox cmbBoxNumbers;
    private JTextField fldCount;
    private JLabel lblCount;
    private JButton ok;

    private int[] employeeIDAry;
    private String[] employeeSubjectAry;
    private int[] productIDAry;
    private String[] productSubjectAry;
    private int[] contactIDAry;
    private String[] contactSubjectAry;
    private int count;
}
