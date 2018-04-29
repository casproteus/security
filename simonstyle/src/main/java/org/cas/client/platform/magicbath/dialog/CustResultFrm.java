package org.cas.client.platform.magicbath.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.magicbath.MagicbathTiedView;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.CustViewConsts;
import org.cas.client.resource.international.DlgConst;

public class CustResultFrm extends JFrame implements ActionListener, FocusListener, ComponentListener {
    public CustResultFrm(Object[][] pValues, Object[] pHeaders) {
        values = pValues;
        headers = pHeaders;
        initComponent();
    }

    /** Invoked when an action occurs. */
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object s = e.getSource();
        if (s == ok) {
            closeBox();
            setVisible(false);
        } else if (s == cancel) {
            setVisible(false);
            dispose();
        } else if (s == cmbVIPNumber) {
            VIPIdx = cmbVIPNumber.getSelectedIndex();
            if (VIPIdx < 0) {
                fldReceive.setText("");
                fldChange.setText("");
                lblChange.setText(MagicbathDlgConst.Change);
            } else {
                int tMoney = Integer.valueOf(moneyAry[VIPIdx]).intValue();// 以分为单位作计算
                vipMoneyLeft = tMoney - (int) toltalPrice * 100;// 以分为单位作计算
                fldReceive.setText(String.valueOf(Integer.valueOf(tMoney).floatValue() / 100));
                fldChange.setText(String.valueOf(Integer.valueOf(vipMoneyLeft).floatValue() / 100));
                lblChange.setText(MagicbathDlgConst.MonLeft);
            }
        }
    }

    /** Invoked when a component gains the keyboard focus. */
    @Override
    public void focusGained(
            FocusEvent e) {
    }

    /** Invoked when a component loses the keyboard focus. */
    @Override
    public void focusLost(
            FocusEvent e) {
        Object s = e.getSource();
        if (s == fldReceive) {
            String tInputed = fldReceive.getText();
            int tRceive = 0;
            try {
                tRceive = Integer.parseInt(tInputed);
                fldChange.setText(String.valueOf(tRceive - toltalPrice));
            } catch (Exception exp) {
                fldReceive.selectAll();
            }
        } else if (s == cmbVIPNumber) {
            // TODO:add function here.
        }
    }

    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        relayout();
    }

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    @Override
    public void componentHidden(
            ComponentEvent e) {
    }

    private void initComponent() {
        setIconImage(CustOpts.custOps.getFrameLogoImage()); // 图标暂设为草稿图标，（一张纸上加一支笔）
        setTitle(DlgConst.DlgTitle); // 设置标题。

        // init-------------
        general = new JPanel();
        tblContent = new JTable(new DefaultTableModel(values, headers));
        srpContent = new JScrollPane(tblContent);
        lblTotalPrice = new JLabel(MagicbathDlgConst.TotalPrice);
        fldTotalPrice = new JTextField(calculateToltalPrice());
        lblVIPNumber = new JLabel(MagicbathDlgConst.SeleVipNumber);
        cmbVIPNumber = new JComboBox();
        lblReceive = new JLabel(MagicbathDlgConst.Received);
        fldReceive = new JTextField();
        lblChange = new JLabel(MagicbathDlgConst.Change);
        fldChange = new JTextField();
        ok = new JButton(MagicbathDlgConst.CloseBox);
        cancel = new JButton(CustViewConsts.CANCEL);

        // properties----------------------
        general.setLayout(null);
        fldTotalPrice.setEditable(false);
        fldChange.setEditable(false);

        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableCellRenderer tCellRender = new DefaultTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.UPPER_RIGHT_CORNER, tLbl);

        // relayout-----------------------
        setBounds((CustOpts.SCRWIDTH - 560) / 2, (CustOpts.SCRHEIGHT - 264) / 2, 560, 264); // 对话框的默认尺寸。
        relayout();

        // build-------------------------
        getContentPane().add(general);
        general.add(srpContent);
        general.add(lblTotalPrice);
        general.add(fldTotalPrice);
        general.add(lblVIPNumber);
        general.add(cmbVIPNumber);
        general.add(lblReceive);
        general.add(fldReceive);
        general.add(lblChange);
        general.add(fldChange);
        general.add(ok);
        general.add(cancel);

        // listeners-------------------
        general.addComponentListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        fldReceive.addFocusListener(this);
        cmbVIPNumber.addFocusListener(this);
        cmbVIPNumber.addActionListener(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fldReceive.requestFocus();
            }
        });

        initVipCompBox();
    }

    private void initVipCompBox() {
        String sql = "select ID, SUBJECT, ACCOUNT from contact where FolderID = 102 and DELETED != true";

        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            idAry = new int[tmpPos];
            subjectAry = new String[tmpPos];
            moneyAry = new int[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                idAry[tmpPos] = rs.getInt("ID");
                subjectAry[tmpPos] = rs.getString("SUBJECT");
                moneyAry[tmpPos] = rs.getInt("ACCOUNT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        cmbVIPNumber.setModel(new DefaultComboBoxModel(subjectAry));
        cmbVIPNumber.setSelectedIndex(-1);
    }

    private String calculateToltalPrice() {
        toltalPrice = 0;
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++) {
            Object tObj = tblContent.getValueAt(i, 1);
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
        lblTotalPrice.setBounds(CustOpts.HOR_GAP, ok.getY() - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP * 2,
                lblTotalPrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblChange.setBounds(general.getWidth() / 2, lblTotalPrice.getY(), lblChange.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        lblReceive.setBounds((lblTotalPrice.getX() + lblChange.getX()) / 2, lblChange.getY(),
                lblReceive.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblVIPNumber.setBounds(lblChange.getX() * 2 - lblReceive.getX(), lblReceive.getY(),
                lblVIPNumber.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        fldTotalPrice.setBounds(lblTotalPrice.getX() + lblTotalPrice.getWidth() + CustOpts.HOR_GAP,
                lblVIPNumber.getY(), lblReceive.getX() - lblTotalPrice.getX() - lblTotalPrice.getWidth()
                        - CustOpts.HOR_GAP * 2, CustOpts.BTN_HEIGHT);
        fldReceive.setBounds(lblReceive.getX() + lblReceive.getWidth() + CustOpts.HOR_GAP, lblVIPNumber.getY(),
                lblChange.getX() - lblReceive.getX() - lblReceive.getWidth() - CustOpts.HOR_GAP * 2,
                CustOpts.BTN_HEIGHT);
        fldChange.setBounds(lblChange.getX() + lblChange.getWidth() + CustOpts.HOR_GAP, lblVIPNumber.getY(),
                lblVIPNumber.getX() - lblChange.getX() - lblChange.getWidth() - CustOpts.HOR_GAP * 2,
                CustOpts.BTN_HEIGHT);
        cmbVIPNumber.setBounds(lblVIPNumber.getX() + lblVIPNumber.getWidth() + CustOpts.HOR_GAP, lblVIPNumber.getY(),
                general.getWidth() - lblVIPNumber.getX() - lblVIPNumber.getWidth() - CustOpts.HOR_GAP * 2,
                CustOpts.BTN_HEIGHT);

        srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, general.getWidth() - CustOpts.HOR_GAP * 2,
                lblVIPNumber.getY() - CustOpts.HOR_GAP * 2);

        tblContent.revalidate();
        tblContent.validate();
        tblContent.invalidate();

        invalidate();
    }

    private void closeBox() {
        for (int j = 0; j < values.length; j++) { // update the records in DB.
            String tBoxNumber = (String) values[j][0];
            if (tBoxNumber == null)
                break;
            for (int i = 0, len = MagicbathGeneralPanel.pCalculate.contactSubjectAry.length; i < len; i++)
                if (MagicbathGeneralPanel.pCalculate.contactSubjectAry[i].equals(tBoxNumber)) {
                    PIMRecord tmpRec =
                            CASControl.ctrl.getModel().selectRecord(CustOpts.custOps.APPNameVec.indexOf("Contact"),
                                    MagicbathGeneralPanel.pCalculate.contactIDAry[i], 100); // to select a record from
                                                                                            // DB.
                    tmpRec.setFieldValue(27, CASUtility.EMPTYSTR); // modify the Time info of the record.
                    CASControl.ctrl.getModel().updateRecord(tmpRec, true); // save the record back to DB.
                    MagicbathTiedView.getInstance().resetState(tBoxNumber, null);
                    break;
                }
        }

        MagicbathGeneralPanel.pCalculate.initBoxNumbers(); // reinit the ComboBox.
        MagicbathGeneralPanel.pAddService.initBoxNumbers();
        MagicbathGeneralPanel.pOpenBox.initBoxNumbers(null, null);
        CASBathBar.addMoneyIn((int) toltalPrice);

        if (VIPIdx > -1) {
            PIMRecord tmpRec =
                    CASControl.ctrl.getModel().selectRecord(CustOpts.custOps.APPNameVec.indexOf("Contact"),
                            idAry[VIPIdx], 102);
            tmpRec.setFieldValue(ContactDefaultViews.ACCOUNT, Integer.valueOf(vipMoneyLeft));
            CASControl.ctrl.getModel().updateRecord(tmpRec, true);
        }
    }

    private Object[][] values;
    private Object[] headers;
    private int[] idAry;
    private String[] subjectAry;
    private int[] moneyAry;
    private int VIPIdx = -1;
    private int vipMoneyLeft;

    private JTable tblContent;
    private JScrollPane srpContent;
    private JPanel general;
    private JLabel lblTotalPrice;
    private JTextField fldTotalPrice;
    private JLabel lblVIPNumber;
    private JComboBox cmbVIPNumber;
    private JLabel lblReceive;
    private JTextField fldReceive;
    private JLabel lblChange;
    private JTextField fldChange;
    private JButton ok;
    private JButton cancel;

    private double toltalPrice;// 和table中的钱的单位保持一致.
}
