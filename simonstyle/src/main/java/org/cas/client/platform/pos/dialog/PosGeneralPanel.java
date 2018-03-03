package org.cas.client.platform.pos.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pos.dialog.statistics.Statistic;
import org.cas.client.platform.refund.dialog.RefundDlg;
import org.cas.client.resource.international.PaneConsts;

//Identity表应该和Employ表合并。
public class PosGeneralPanel extends JPanel implements ComponentListener, KeyListener, ActionListener, FocusListener,
        DocumentListener {
    final int PRICECOLID = 3;
    final int TOTALCOLID = 4;
    final int COUNDCOLID = 2;

    public static String startTime;

    public PosGeneralPanel() {
        initConponent();
    }

    // ComponentListener-----------------------------
    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
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

    @Override
    public void insertUpdate(
            DocumentEvent e) {
        String tProdNumber = tfdProdNumber.getText();
        if (tProdNumber.length() == PosUtility.getProdCodeLen()) {
            String sql =
                    "select id, subject, price, unit, content from product where code = '".concat(tProdNumber).concat(
                            "'");
            try {
                ResultSet rs =
                        PIMDBModel.getConection()
                                .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                .executeQuery(sql);
                rs.afterLast();
                rs.relative(-1);
                if (rs.getRow() > 0) {
                    rs.beforeFirst();
                    while (rs.next()) {
                        prodID = rs.getInt("ID");
                        String tSubj = rs.getString("Subject");
                        Vector tVec = new Vector();
                        tVec.add(tSubj);
                        DefaultComboBoxModel tModel = new DefaultComboBoxModel(tVec); // 给fileAsBox赋值
                        cmbProdName.setModel(tModel);
                        cmbProdName.setSelectedIndex(0); // 品名（加到列表后，复位时别忘给复位成无码商品啊）
                        int tCount = 1;
                        try {
                            tCount = Integer.parseInt(tfdCount.getText());
                        } catch (Exception exp) {
                        }
                        tfdCount.setText(String.valueOf(tCount)); // 数量（复位时别忘记给设成1啊）
                        float tPrice = rs.getInt("price");
                        tfdPrice.setText(String.valueOf(tPrice / 100)); // 单价
                        tfdTotlePrice.setText(String.valueOf(tCount * tPrice / 100)); // 总价
                        tfdPackage.setText(rs.getString("unit"));
                        tarNote.setText(rs.getString("content"));
                        break;
                    }
                    startWaitThread();
                } else {
                    MerchandiseDlg tDlg = new MerchandiseDlg(PosFrame.instance, tProdNumber);
                    tDlg.enableProdCode(false);
                    tDlg.setVisible(true);
                    if (tDlg.ADDED) {// 如果添加产品对话盒成功加入了产品
                        prodID = tDlg.getProdID();
                        Vector tVec = new Vector();// 直接设置ComboBox的Model，因为扫描仪导致的新品输入，是不会改条码的。
                        tVec.add(tDlg.getProdName());
                        DefaultComboBoxModel tModel = new DefaultComboBoxModel(tVec);
                        cmbProdName.setModel(tModel);
                        cmbProdName.setSelectedIndex(0);
                        tfdPrice.setText(tDlg.getPrice());
                        int tCount = 1;
                        try {
                            tCount = Integer.parseInt(tfdCount.getText());
                        } catch (Exception exp) {
                        }
                        float tPrice = Float.parseFloat(tDlg.getPrice());
                        tfdTotlePrice.setText(String.valueOf(tCount * tPrice));
                        tfdPackage.setText(tDlg.getPackage());
                        tarNote.setText(tDlg.getRemark());
                        startWaitThread();
                    }
                }
                rs.close();// 关闭
            } catch (Exception exp) {
                ErrorUtil.write(exp);
            }
        }
    }

    @Override
    public void removeUpdate(
            DocumentEvent e) {
    }

    @Override
    public void changedUpdate(
            DocumentEvent e) {
    }

    @Override
    public void focusGained(
            FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
        if (o == tfdProdNumber) {
            if (getUsedRowCount() == 0)
                setStatusMes(PosDlgConst.NoteProdNumber);
        } else if (o == cmbProdName) {
            setStatusMes(PosDlgConst.NoteProdName);
        } else if (o == tfdCount) {
            setStatusMes(PosDlgConst.NoteCount);
        } else if (o == tfdActuallyReceive) {
            setStatusMes(PosDlgConst.NoteActiveReceive);
        }
    }

    @Override
    public void focusLost(
            FocusEvent e) {
    }

    // ActionListner----------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnOffDuty) { // 交班
            new OffDutyDlg(PosFrame.instance).setVisible(true);
        } else if (o == btnCheck) { // 盘货,先检查是否存在尚未输入完整信息的产品，如果检查到存
            PosUtility.checkUnCompProdInfo(); // 在这种产品，方法中会自动弹出对话盒要求用户填写详细信息。
            new CheckStoreDlg(PosFrame.instance).setVisible(true);
        } else if (o == btnInput) {
            new AddStoreDlg(PosFrame.instance).setVisible(true);
        } else if (o == btnRefund) {
            new RefundDlg(PosFrame.instance).setVisible(true);
        } else if (o == btnHangup) {
            hangup();
        } else if (o == btnStatic) {
            int tType = Integer.parseInt(CustOpts.custOps.getUserType());
            if (tType > 0) {// 如果当前登陆用户是个普通员工，则显示普通登陆对话盒。等待再次登陆
                new LoginDlg(PosFrame.instance).setVisible(true);// 结果不会被保存到ini
                if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
                    int tLevel = Integer.parseInt(LoginDlg.USERTYPE);
                    if (tLevel <= 0)// 进一步判断，如果新登陆是经理，弹出对话盒
                        new Statistic(PosFrame.instance).setVisible(true);
                }
            } else
                // 如果当前的用户已经是管理员了，则弹出对话盒？
                new Statistic(PosFrame.instance).setVisible(true);
        } else if (o == btnOption) {
            new PosOptionDlg(PosFrame.instance).setVisible(true);
        } else if (o == cmbMoneyType) {
            String tRate = (String) CustOpts.custOps.getValue(cmbMoneyType.getSelectedItem());
            try {
                float tShouldReceive = shouldReceive * Float.parseFloat(tRate);
                tfdShoudReceive.setText(decimalFormat.format(tShouldReceive));
                lblUnit.setText((String) cmbMoneyType.getSelectedItem());
            } catch (Exception exp) {
            }
        }
    }

    // Key Listener--------------------------------
    @Override
    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
        if (o == tfdProdNumber) { // 默认情况下，焦点应该都在本组件里的。
            int tKeyCode = e.getKeyCode();
            if (tKeyCode == 10) { // 货号框中，随时当按回车键
                if (tfdActuallyReceive.getText().length() > 0) {// 判断是否尚未清空上次的结算数据
                    resetListArea();
                    cmbMoneyType.setSelectedIndex(0);// @note:必须先执行，因为本语句导致actionpermed方法，从而使tfdShoudReceive被赋值。
                    shouldReceive = 0;
                    tfdShoudReceive.setText("0.00");
                    tfdActuallyReceive.setText("");
                    lblUnit.setText(PosDlgConst.Unit);
                    tfdChange.setText("0.00");
                }

                // 检查输入有效性-------------------------------------------------------
                String tProdNumber = tfdProdNumber.getText();
                if (tProdNumber == null || tProdNumber.length() < 1) {// 发现并没有选中什么商品，这种回车和ProdNumberField中的无码回车的意义一样---结算！
                    calculate(); // 该方法内会判断是否已填入实收金额，否则光标跳转实收框，要求输入。
                    return;
                } else if (tProdNumber.startsWith("+") || tProdNumber.startsWith("＋")) {// 加收冷冻费。
                    String tStr = tProdNumber.substring(1);
                    try {
                        float tFloat = Float.parseFloat(tStr);
                        int tDotPos = tProdNumber.indexOf('.'); // @NOTE:因为对小数点在键按下时有处理，所以应永远是-1.
                        if (tDotPos < 0 || tProdNumber.substring(tDotPos + 1).length() <= 2) {
                            // 调整记录的单价。
                            int tR = tblContent.getSelectedRow(); // 如果有选中的，则调整选中的那条。
                            if (tR < 0) // 如果列表里没有选中的，则调整最后一条。
                                tR = getUsedRowCount() - 1;
                            if (tR >= 0) {
                                String tPrice = (String) tblContent.getValueAt(tR, PRICECOLID); // 取出单价
                                int tCount = Integer.parseInt((String) tblContent.getValueAt(tR, COUNDCOLID));// 取数量
                                float tTotle = (Float.parseFloat(tPrice) + tFloat) * tCount; // 算出新的小计值；
                                tblContent.setValueAt(tPrice.concat(tProdNumber), tR, PRICECOLID); // 调单价
                                tblContent.setValueAt(decimalFormat.format(tTotle), tR, TOTALCOLID); // 调小计。
                                shouldReceive += tFloat * tCount; // 调总计。
                                tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                                tfdProdNumber.setText("");
                                return;// 调整最后一笔记录的单价。
                            }
                        }
                    } catch (Exception exp) {
                    }
                    JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                    return;
                } else if (tProdNumber.startsWith("-") || tProdNumber.startsWith("－")) {// 返还现金降价
                    String tStr = tProdNumber.substring(1);
                    try {
                        float tFloat = Float.parseFloat(tStr);
                        int tDotPos = tProdNumber.indexOf('.'); // @NOTE:因为对小数点在键按下时有处理，所以应永远是-1.
                        if (tDotPos < 0 || tProdNumber.substring(tDotPos + 1).length() <= 2) {
                            // 调整记录的单价。
                            int tR = tblContent.getSelectedRow(); // 如果有选中的，则调整选中的那条。
                            if (tR < 0) // 如果列表里没有选中的，则调整最后一条。
                                tR = getUsedRowCount() - 1;
                            if (tR >= 0) {
                                String tPrice = (String) tblContent.getValueAt(tR, PRICECOLID); // 取出单价
                                int tCount = Integer.parseInt((String) tblContent.getValueAt(tR, COUNDCOLID));// 取数量
                                float tTotle = (Float.parseFloat(tPrice) - tFloat) * tCount; // 算出新的小计值；
                                tblContent.setValueAt(tPrice.concat(tProdNumber), tR, PRICECOLID); // 调单价
                                tblContent.setValueAt(decimalFormat.format(tTotle), tR, TOTALCOLID); // 调小计。
                                shouldReceive -= tFloat * tCount; // 调总计。
                                tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                                tfdProdNumber.setText("");
                                return;// 调整最后一笔记录的单价。
                            }
                        }
                    } catch (Exception exp) {
                    }
                    JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                    return;
                } else if (!tProdNumber.startsWith("****") && !tProdNumber.startsWith("××××")
                        && !tProdNumber.startsWith("＊＊＊＊")
                        && (tProdNumber.startsWith("*") || tProdNumber.startsWith("×") || tProdNumber.startsWith("＊"))) {// 打折降价
                    String tStr = tProdNumber.substring(1);
                    try {
                        float tFloat = Float.parseFloat(tStr);
                        int tDotPos = tProdNumber.indexOf('.'); // @NOTE:因为对小数点在键按下时有处理，所以应永远是-1.
                        if (tDotPos < 0 || tProdNumber.substring(tDotPos + 1).length() <= 2) {
                            // 调整记录的单价。
                            int tR = tblContent.getSelectedRow(); // 如果有选中的，则调整选中的那条。
                            if (tR < 0) // 如果列表里没有选中的，则调整最后一条。
                                tR = getUsedRowCount() - 1;
                            if (tR >= 0) {
                                String tPrice = (String) tblContent.getValueAt(tR, PRICECOLID); // 取出单价
                                int tCount = Integer.parseInt((String) tblContent.getValueAt(tR, COUNDCOLID));// 取数量
                                float tTotlePricet = Float.parseFloat((String) tblContent.getValueAt(tR, TOTALCOLID));// 取小计
                                if (tDotPos < 0) {
                                    tblContent.setValueAt(tStr, tR, COUNDCOLID); // 调整数量
                                    tblContent.setValueAt(decimalFormat.format(Float.parseFloat(tPrice) * tFloat), tR,
                                            TOTALCOLID);// 调整小计
                                } else {
                                    tblContent.setValueAt(tPrice.concat(tProdNumber), tR, PRICECOLID); // 调整单价
                                    tblContent.setValueAt(decimalFormat.format(tTotlePricet * tFloat), tR, TOTALCOLID); // 调整小计
                                }
                                shouldReceive -=
                                        tTotlePricet - Float.parseFloat((String) tblContent.getValueAt(tR, TOTALCOLID)); // 调总计。
                                tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                                tfdProdNumber.setText("");
                                return;// 调整最后一笔记录的单价。
                            }
                        }
                    } catch (Exception exp) {
                    }
                    JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                    return;
                } else if (tProdNumber.indexOf('.') != -1) { // 包含有小数点，认定用户在输入价格。
                    int tDotPos = tProdNumber.indexOf('.'); // @NOTE:因为对小数点在键按下时有处理，所以本段一般执行不到。
                    if (tProdNumber.substring(tDotPos + 1).length() > 2) {// 小数位数不和规范,报错返回
                        JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                        return;
                    }
                    try {
                        float tFloat = Float.parseFloat(tProdNumber);
                        float tShoudReceive = Float.parseFloat(tfdShoudReceive.getText());
                        if (tFloat - tShoudReceive < 100) { // 小数位不应多于两位，且整数比应收不多于100.
                            tfdActuallyReceive.setText(tfdProdNumber.getText());
                            tfdProdNumber.setText("");
                            tfdActuallyReceive.grabFocus();
                            return;
                        } else {
                            JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                            return;
                        }
                    } catch (Exception exp) {
                        JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                        return;
                    }
                } else if (tProdNumber.length() < 5) { // 内容小于5位的，进一步判断会不会是金额：
                    try {
                        int tInputedValue = Integer.parseInt(tProdNumber); // 应为不带'.',所以必须是整数。
                        float tShoudReceive = Float.parseFloat(tfdShoudReceive.getText());
                        if (tInputedValue - tShoudReceive < 100) {
                            tfdActuallyReceive.setText(tfdProdNumber.getText());// User如果输入100的话需要敲回车才会经过判断转到收银框中。
                            tfdProdNumber.setText("");
                            tfdActuallyReceive.grabFocus();
                            return;
                        }
                    } catch (Exception exp) { // 不是整数，那么必然不是金额，而是货码。不予处理。
                    }
                } else if (tProdNumber.length() > 13) { // 如果发现条码框中内容是大于13的，则报错，因为统一条码不会大于13，自定义条码也不会大于13.
                    JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                    return;
                } // 有效性检查结束--------------------------------------------

                String sql =
                        "select id, subject, price, unit, content from product where code like '%".concat(tProdNumber)
                                .concat("'");
                try {
                    ResultSet rs =
                            PIMDBModel.getConection()
                                    .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                    .executeQuery(sql);
                    rs.afterLast();
                    rs.relative(-1);
                    if (rs.getRow() > 0) {
                        rs.beforeFirst();
                        while (rs.next()) {
                            prodID = rs.getInt("id");
                            String tSubj = rs.getString("Subject");
                            Vector tVec = new Vector();
                            tVec.add(tSubj);
                            DefaultComboBoxModel tModel = new DefaultComboBoxModel(tVec); // 给fileAsBox赋值
                            cmbProdName.setModel(tModel);
                            cmbProdName.setSelectedIndex(0); // 品名（加到列表后，复位时别忘给复位成无码商品啊）
                            int tCount = 1;
                            try {
                                tCount = Integer.parseInt(tfdCount.getText());
                            } catch (Exception exp) {
                            }
                            tfdCount.setText(String.valueOf(tCount)); // 数量（复位时别忘记给设成1啊）
                            int tPrice = rs.getInt("price");
                            tfdPrice.setText(decimalFormat.format(tPrice / 100.0)); // 单价
                            tfdTotlePrice.setText(decimalFormat.format(tCount * tPrice / 100.0)); // 总价
                            tfdPackage.setText(rs.getString("unit"));
                            tarNote.setText(rs.getString("content"));
                            break;
                        }
                        startWaitThread();
                    } else {
                        MerchandiseDlg tDlg = new MerchandiseDlg(PosFrame.instance, tProdNumber);
                        tDlg.setVisible(true);
                        if (tDlg.ADDED) {// 如果添加产品对话盒成功加入了产品
                            prodID = tDlg.getProdID();
                            tfdProdNumber.setText(tDlg.getProdCode());// 则新的产品信息加入到当前对话盒区域，并启动线程。
                            Vector tVec = new Vector();
                            tVec.add(tDlg.getProdName());
                            DefaultComboBoxModel tModel = new DefaultComboBoxModel(tVec);
                            cmbProdName.setModel(tModel);
                            cmbProdName.setSelectedIndex(0);
                            tfdPrice.setText(tDlg.getPrice());
                            int tCount = 1;
                            try {
                                tCount = Integer.parseInt(tfdCount.getText());
                            } catch (Exception exp) {
                            }
                            float tPrice = Float.parseFloat(tDlg.getPrice());
                            tfdTotlePrice.setText(decimalFormat.format(tCount * tPrice));
                            tfdPackage.setText(tDlg.getPackage());
                            tarNote.setText(tDlg.getRemark());
                            startWaitThread();
                        }
                    }
                    rs.close();// 关闭
                } catch (SQLException exp) {// 如果没有匹配是否会到该代码块？
                    ErrorUtil.write(exp);
                }
            } else if (tKeyCode == 46 || tKeyCode == 110) {// 如果输入的是实际收银数（判断的依据是发现有小数点），则直接跳转至“收银”框去,并把内容也带过去。
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        tfdActuallyReceive.setText(tfdProdNumber.getText()); // User如果输入100的话需要敲回车才会经过判断转到收银框中。
                        tfdProdNumber.setText("");
                        tfdActuallyReceive.grabFocus();
                    }
                });
            } else
                commKeyProcess(e);
        } else if (o == cmbProdName) { // 遇到一个没有来的及贴条码的商品，操作员果断采取直接在ComboBox中选则产品的方法。
            if (e.getKeyCode() == 10) { // 聪明的POSMM敲了一个回车。
                if (tfdActuallyReceive.getText().length() > 0) {// 判断是否尚未清空上次的结算数据
                    resetListArea();
                    cmbMoneyType.setSelectedIndex(0);// @note:必须先执行，因为本语句导致actionpermed方法，从而使tfdShoudReceive被赋值。
                    shouldReceive = 0;
                    tfdShoudReceive.setText("0.00");
                    tfdActuallyReceive.setText("");
                    lblUnit.setText(PosDlgConst.Unit);
                    tfdChange.setText("0.00");
                }
                String tProdName = (String) cmbProdName.getSelectedItem();
                if (tProdName == null) { // 发现并没有选中什么商品，这种回车和ProdNumberField中的无码回车的意义一样---结算！
                    calculate();
                    return;
                }
                for (int i = 0, len = prodSubjectAry.length; i < len; i++) {
                    if (prodSubjectAry[i].equals(tProdName)) {
                        prodID = prodIDAry[i];
                        String sql =
                                "select code, price, unit, content from product where id = ".concat(String
                                        .valueOf(prodIDAry[i]));
                        try {
                            ResultSet rs =
                                    PIMDBModel
                                            .getConection()
                                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                    ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
                            rs.afterLast();
                            rs.relative(-1);
                            rs.beforeFirst();
                            while (rs.next()) {
                                tfdProdNumber.setText(rs.getString("code")); // 条码
                                int tCount = 1;
                                try {
                                    tCount = Integer.parseInt(tfdCount.getText());
                                } catch (Exception exp) {
                                }
                                tfdCount.setText(String.valueOf(tCount)); // 数量
                                int tPrice = rs.getInt("price");
                                tfdPrice.setText(decimalFormat.format(tPrice / 100.0)); // 单价
                                tfdTotlePrice.setText(decimalFormat.format(tCount * tPrice / 100)); // 总价
                                tfdPackage.setText(rs.getString("unit")); // 包装
                                tarNote.setText(rs.getString("content")); // 说明
                                break;
                            }
                            rs.close();// 关闭
                            startWaitThread();
                        } catch (SQLException exp) {
                            ErrorUtil.write(exp);
                        }
                        break;
                    }
                }
            } else
                commKeyProcess(e);
        } else if (o == tfdCount) { // 当焦点在条码框或者品名框内时用户按下了"数量"键时，焦点就跑到了数量框中。
            if (e.getKeyCode() == 10 || e.getKeyCode() == 17) { // 该组件内对"数量"键和Enter键给予特殊处理。
                try {
                    int tCount = Integer.parseInt(tfdCount.getText());
                    if (tCount < 1)
                        tfdCount.setText("1");
                    if (tCount > 1000) { // TODO：应该在Option中允许设置。
                        JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                        return;
                    }
                } catch (Exception exp) {
                    tfdCount.setText("1");
                }
                tfdProdNumber.grabFocus(); // 跳回之前光标所在的那个组件。
            }
        } else if (o == tfdActuallyReceive) { // 当焦点在条码框或者品名框内时用户按下了Enter键时，如果条码框和品名框中都是空的，焦点将转入实收金额中。
            int tKeyCode = e.getKeyCode();
            if (tKeyCode == 10 || tKeyCode == 32) { // 该组件内对Space键和Enter键给予特殊处理。
                // 检查输入有效性-------------------------------------------------------
                String tReceivedMoney = tfdActuallyReceive.getText();
                if (tReceivedMoney.length() == 0) { // 没有输入收银，直接回车。光标置入条码框。
                    tfdProdNumber.grabFocus();
                    return;
                } else if (tReceivedMoney.startsWith("+") || tReceivedMoney.startsWith("＋")) {// 加收冷冻费。
                    String tStr = tReceivedMoney.substring(1);
                    try {
                        float tFloat = Float.parseFloat(tStr);
                        int tDotPos = tReceivedMoney.indexOf('.');
                        if (tDotPos < 0 || tReceivedMoney.substring(tDotPos + 1).length() <= 2) {
                            // 调整记录的单价。
                            int tR = tblContent.getSelectedRow(); // 如果有选中的，则调整选中的那条。
                            if (tR < 0) // 如果列表里没有选中的，则调整最后一条。
                                tR = getUsedRowCount() - 1;
                            if (tR >= 0) {
                                String tPrice = (String) tblContent.getValueAt(tR, PRICECOLID); // 取出单价
                                int tCount = Integer.parseInt((String) tblContent.getValueAt(tR, COUNDCOLID));// 取数量
                                float tTotle = (Float.parseFloat(tPrice) + tFloat) * tCount; // 算出新的小计值；
                                tblContent.setValueAt(tPrice.concat(tReceivedMoney), tR, PRICECOLID); // 调单价
                                tblContent.setValueAt(decimalFormat.format(tTotle), tR, TOTALCOLID); // 调小计。
                                shouldReceive += tFloat * tCount; // 调总计。
                                tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                                tfdActuallyReceive.setText("");
                                return;// 调整最后一笔记录的单价。
                            }
                        }
                    } catch (Exception exp) {
                    }
                    JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                    return;
                } else if (tReceivedMoney.startsWith("-") || tReceivedMoney.startsWith("－")) {// 返还现金降价
                    String tStr = tReceivedMoney.substring(1);
                    try {
                        float tFloat = Float.parseFloat(tStr);
                        int tDotPos = tReceivedMoney.indexOf('.');
                        if (tDotPos < 0 || tReceivedMoney.substring(tDotPos + 1).length() <= 2) {
                            // 调整记录的单价。
                            int tR = tblContent.getSelectedRow(); // 如果有选中的，则调整选中的那条。
                            if (tR < 0) // 如果列表里没有选中的，则调整最后一条。
                                tR = getUsedRowCount() - 1;
                            if (tR >= 0) {
                                String tPrice = (String) tblContent.getValueAt(tR, PRICECOLID); // 取出单价
                                int tCount = Integer.parseInt((String) tblContent.getValueAt(tR, COUNDCOLID));// 取数量
                                float tTotle = (Float.parseFloat(tPrice) - tFloat) * tCount; // 算出新的小计值；
                                tblContent.setValueAt(tPrice.concat(tReceivedMoney), tR, PRICECOLID); // 调单价
                                tblContent.setValueAt(decimalFormat.format(tTotle), tR, TOTALCOLID); // 调小计。
                                shouldReceive -= tFloat * tCount; // 调总计。
                                tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                                tfdActuallyReceive.setText("");
                                return;// 调整最后一笔记录的单价。
                            }
                        }
                    } catch (Exception exp) {
                    }
                    JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                    return;
                } else if (tReceivedMoney.startsWith("*") || tReceivedMoney.startsWith("×")
                        || tReceivedMoney.startsWith("＊")) {// 打折降价
                    String tStr = tReceivedMoney.substring(1);
                    try {
                        float tFloat = Float.parseFloat(tStr);
                        int tDotPos = tReceivedMoney.indexOf('.');
                        if (tDotPos < 0 || tReceivedMoney.substring(tDotPos + 1).length() <= 2) {
                            // 调整记录的单价。
                            int tR = tblContent.getSelectedRow(); // 如果有选中的，则调整选中的那条。
                            if (tR < 0) // 如果列表里没有选中的，则调整最后一条。
                                tR = getUsedRowCount() - 1;
                            if (tR >= 0) {
                                String tPrice = (String) tblContent.getValueAt(tR, PRICECOLID); // 取出单价
                                int tCount = Integer.parseInt((String) tblContent.getValueAt(tR, COUNDCOLID));// 取数量
                                float tTotlePricet = Float.parseFloat((String) tblContent.getValueAt(tR, TOTALCOLID));// 取小计
                                if (tDotPos < 0) {
                                    tblContent.setValueAt(tStr, tR, COUNDCOLID); // 调整数量
                                    tblContent.setValueAt(decimalFormat.format(Float.parseFloat(tPrice) * tFloat), tR,
                                            TOTALCOLID);// 调整小计
                                } else {
                                    tblContent.setValueAt(tPrice.concat(tReceivedMoney), tR, PRICECOLID); // 调整单价
                                    tblContent.setValueAt(decimalFormat.format(tTotlePricet * tFloat), tR, TOTALCOLID); // 调整小计
                                }
                                shouldReceive -=
                                        tTotlePricet - Float.parseFloat((String) tblContent.getValueAt(tR, TOTALCOLID)); // 调总计。
                                tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                                tfdActuallyReceive.setText("");
                                return;// 调整最后一笔记录的单价。
                            }
                        }
                    } catch (Exception exp) {
                    }
                    JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                    return;
                } else if (tReceivedMoney.indexOf('.') != -1) { // 包含有小数点，认定用户在输入价格。
                    int tDotPos = tReceivedMoney.indexOf('.'); // @NOTE:因为对小数点在键按下时有处理，所以本段一般执行不到。
                    if (tReceivedMoney.substring(tDotPos + 1).length() > 2) {// 小数位数不和规范,报错返回
                        JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                        return;
                    }
                    try {
                        float tFloat = Float.parseFloat(tReceivedMoney);
                        float tShoudReceive = Float.parseFloat(tfdShoudReceive.getText());
                        if (tFloat - tShoudReceive >= 100) { // 小数位不应多于两位，且整数比应收不多于100.
                            JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                            return;
                        }
                    } catch (Exception exp) {
                        JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                        return;
                    }
                } else if (tReceivedMoney.length() < 5) { // 内容小于5位的，进一步判断会不会是金额：
                    try {
                        int tInputedValue = Integer.parseInt(tReceivedMoney); // 应为不带'.',所以必须是整数。
                        float tShoudReceive = Float.parseFloat(tfdShoudReceive.getText());
                        if (tInputedValue - tShoudReceive >= 100) {
                            JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                            return;
                        }
                    } catch (Exception exp) { // 不是整数，那么必然不是金额，而是货码。不予处理。
                        JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                        return;
                    }
                } else if (tReceivedMoney.length() == 13) {// 为了获得好感，特别对13位做了处理。使用户可以方便的继续增加
                    tfdProdNumber.setText(tfdActuallyReceive.getText());// User如果输入100的话需要敲回车才会经过判断转到收银框中。
                    tfdActuallyReceive.setText("");
                    keyPressed(new KeyEvent(tfdProdNumber, 0, 0, 0, 10));
                    return;
                } else { // 如果发现条码框中内容是大于13的，则报错，因为统一条码不会大于13，自定义条码也不会大于13.
                    JOptionPane.showMessageDialog(this, PosDlgConst.WrongFormatMes);
                    return;
                } // 有效性检查结束--------------------------------------------

                calculate();
            } else if (tKeyCode >= 112 && tKeyCode <= 119) { // 在输入应付账款前，可以输入A到J改变实收货币单位。
                cmbMoneyType.setSelectedIndex(e.getKeyCode() - 111);
            } else if (tKeyCode == 45) { // 在收到应付账款前，可以输入“人民币”键改变实收货币单位为人民币。
                cmbMoneyType.setSelectedIndex(0);
                tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                lblUnit.setText(PosDlgConst.Unit);
            } else
                commKeyProcess(e);
        }
    }

    private void commKeyProcess(
            KeyEvent e) {
        int tKeyCode = e.getKeyCode();
        if (tKeyCode == 112) {// F1库存
            if (btnCheck.isEnabled())
                actionPerformed(new ActionEvent(btnCheck, 0, null));
        } else if (tKeyCode == 113) {// F2为进货
            if (btnInput.isEnabled())
                actionPerformed(new ActionEvent(btnInput, 0, null));
        } else if (tKeyCode == 114) {// F3退货
            if (btnRefund.isEnabled())
                actionPerformed(new ActionEvent(btnRefund, 0, null));
        } else if (tKeyCode == 115) {// F4挂单
            if (btnHangup.isEnabled())
                actionPerformed(new ActionEvent(btnHangup, 0, null));
        } else if (tKeyCode == 116) {// F5统计
            if (btnStatic.isEnabled())
                actionPerformed(new ActionEvent(btnStatic, 0, null));
        }
        // else if(tKeyCode == 119){//F8改汇率
        // if(btnMRate.isEnabled())
        // actionPerformed(new ActionEvent(btnMRate, 0, null));
        // }else if(tKeyCode == 118){//F7改用户
        // if(btnMUser.isEnabled())
        // actionPerformed(new ActionEvent(btnMUser, 0, null));
        // }
        else if (tKeyCode == 117) {// F6系统设置
            if (btnOption.isEnabled())
                actionPerformed(new ActionEvent(btnOption, 0, null));
        } else if (tKeyCode == 27) {// ESC 表示取消对话盒区域内容，取消列表中项目，退出系统
            if (((JTextField) e.getSource()).getText().length() > 0)// 如果条码框或者产品名中有内容，就表示清空本条记录。
                resetDlgArea();
            else { // 如果是对话盒区域已经是空的了
                if (getUsedRowCount() != 0) { // 就看列表中是否有记录，有的话，F1就表示情况列表。
                    resetAll();
                } else
                    // 列表中也没有记录的话，就表示退出系统。提醒操作员盘点。
                    actionPerformed(new ActionEvent(btnOffDuty, 0, null));
            }
        } else if (tKeyCode == 17) {// 按"ctrl"键使光标跳至count.必须先输入数量再输入产品的道理是，扫描枪有可能会带回车，使你没有机会后敲数量。
            tfdCount.grabFocus();
            tfdCount.selectAll();
        } else if (tKeyCode == 38) {
            if (e.getSource() == cmbProdName)// 排除Combobox中的情况，如果是ComboBox，那么应该优先进行组件内的选择。
                return;
            tblContent.setSelectedRow(tblContent.getSelectedRow() - 1);
            tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
        } else if (tKeyCode == 40) {
            if (e.getSource() == cmbProdName)// 排除Combobox中的情况，如果是ComboBox，那么应该优先进行组件内的选择。
                return;
            tblContent.setSelectedRow(tblContent.getSelectedRow() + 1);
            tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
        } else if (tKeyCode == 8 || tKeyCode == 127) {
            if (e.getSource() == tfdActuallyReceive)
                return;
            // @NOTE:当Del键或者BackSpace键被按时，如果条码框中没有内容，则直接进行list中记录的删除动作。
            if (tfdProdNumber.getText().length() == 0) {
                int tRow = tblContent.getSelectedRow();
                if (tRow < 0 || tRow > getUsedRowCount() - 1) { // 没有选中行的话，看看最后一行是第几行，选中它。
                    tblContent.setSelectedRow(getUsedRowCount() - 1);
                    tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
                } else { // 有选中行的话，将选中行删除，应收金额相应减少
                    Float tPrice = Float.parseFloat((String) tblContent.getValueAt(tRow, 4));
                    shouldReceive = shouldReceive - tPrice;
                    tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                    for (int j = tRow; j < tblContent.getRowCount(); j++)
                        if (j == tblContent.getRowCount() - 1)
                            for (int i = 0; i < tblContent.getColumnCount(); i++)
                                tblContent.setValueAt(null, j, i);
                        else
                            for (int i = 0; i < tblContent.getColumnCount(); i++)
                                tblContent.setValueAt(tblContent.getValueAt(j + 1, i), j, i);
                }
            }
        }
        // 一时想不起来为什么加这么个处理，宏姐说不方便，于是就先注释掉试试再说。
        else if (tKeyCode == ' ') {
            Object tOneKeyOpen = CustOpts.custOps.getValue(PosDlgConst.OneKeyOpen);
            if ("true".equals(tOneKeyOpen) || tOneKeyOpen == null) {
                Object tUniCommand = CustOpts.custOps.getValue(PosDlgConst.UniCommand);
                if ("true".equals(tUniCommand) || tUniCommand == null) {// 如果是采用通用命令（没有钱箱卡），则向端口写数据
                    openMoneyBox();
                } else { // 如果是Windows系统，则直接调用exe格式的开钱箱程序
                    String tSrcPath = tUniCommand.toString();
                    try {
                        Runtime.getRuntime().exec(tSrcPath);
                    } catch (Exception exp) {

                    }
                }
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tfdProdNumber.setText("");
                }
            });
        }
    }

    @Override
    public void keyReleased(
            KeyEvent e) {
        if (PosUtility.isNumber(e.getKeyCode()) && e.getSource() == tfdActuallyReceive) {
            String tValue = tfdActuallyReceive.getText();
            int tDotPosition = tValue.indexOf('.');
            if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，自动结算
                keyPressed(new KeyEvent(tfdActuallyReceive, 0, 0, 0, 10));
        }
    }

    @Override
    public void keyTyped(
            KeyEvent e) {
    }

    private void resetColWidth() {
        for (int i = 0, len = header.length; i < len; i++) {
            PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
            tmpCol.setWidth(i == 1 ? getWidth() * 3 / 5 - 243 : 60);
            tmpCol.setPreferredWidth(i == 1 ? getWidth() * 3 / 5 - 243 : 60);
        }
        tblContent.validate();
        tblContent.revalidate();
        tblContent.invalidate();
    }

    // 当对话盒区域内容填写完了以后，通过本方法启动一个线程，实现休眠500ms之后将对话盒区域内容增加到表中。
    // 同时更新tfdShoudReceive的内容。
    private void startWaitThread() {
        setStatusMes(PosDlgConst.NotePordNumber2);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    addContentToList(); // 将对话盒区域内容加入到列表中去。
                    shouldReceive += Float.parseFloat(tfdTotlePrice.getText());
                    tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                    resetDlgArea(); // 对话和区域内容复位。
                    tfdProdNumber.grabFocus(); // 焦点复位。
                } catch (Exception e) {
                }
            }
        });
    }

    // 将对话盒区域的内容加入到列表
    private void addContentToList() {
        int tRowCount = tblContent.getRowCount(); // add content to the table.
        int tColCount = tblContent.getColumnCount();
        int tValidRowCount = getUsedRowCount(); // get the used RowCount
        if (tRowCount == tValidRowCount) { // no line is empty, add a new Line.
            Object[][] tValues = new Object[tRowCount + 1][tColCount];
            for (int r = 0; r < tRowCount; r++)
                for (int c = 0; c < tColCount; c++)
                    tValues[r][c] = tblContent.getValueAt(r, c);
            tblContent.setDataVector(tValues, header);
            resetColWidth();
        }
        tblContent.setValueAt(Integer.valueOf(prodID), tValidRowCount, 0); // set the code.
        tblContent.setValueAt(cmbProdName.getSelectedItem(), tValidRowCount, 1);// set the Name.
        tblContent.setValueAt(tfdCount.getText(), tValidRowCount, 2); // set the count.
        tblContent.setValueAt(tfdPrice.getText(), tValidRowCount, 3); // set the price.
        tblContent.setValueAt(tfdTotlePrice.getText(), tValidRowCount, 4); // set the total price.

        enableBtns(false);
        setStatusMes(PosDlgConst.NotePordNumber3);
    }

    // 当回车被按的时候通常表示结算。当然结算之前有些必要的检查，比如“实收金额”是否有填等。
    // 应收框内的单位和实际受到的单位是一致的，而找零框的单位永远是RMB
    // 考虑到混合币种付账的情况，如果某一币种结算后找零为负数，则不进行后续动作，而是光标停在清空的实收框等待继续输入其他币种。
    private void calculate() {
        try {
            float tReceived = Float.parseFloat(tfdActuallyReceive.getText()); // 先看看实收金额栏填了没有

            float tShoudReceive = Float.parseFloat(tfdShoudReceive.getText()); // 填过了就显示找零
            String rate = (String) CustOpts.custOps.getValue(cmbMoneyType.getSelectedItem());
            float tChange = (tReceived - tShoudReceive) / Float.parseFloat(rate == null ? "1" : rate); // 找零换算成人民币。

            tfdChange.setText(decimalFormat.format(tChange));

            if (tChange >= 0) { // 如果实收金额正确的话，Add the content in list into Database.
                final String tDate = Calendar.getInstance().getTime().toLocaleString();// 至此得到本批记录入库的时间。

                String sql =
                        "Select id from userIdentity where username = '".concat(CustOpts.custOps.getUserName()).concat(
                                "'");
                int tID = -1;
                try {
                    ResultSet rs =
                            PIMDBModel.getConection()
                                    .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                    .executeQuery(sql);
                    ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

                    rs.afterLast();
                    rs.relative(-1);
                    rs.beforeFirst();
                    while (rs.next())
                        tID = rs.getInt("id");
                    rs.close();// 关闭
                } catch (SQLException exp) {
                    ErrorUtil.write(exp);
                } // 至此得到当前用户对应的在userIdentity表中的ID号。

                for (int i = 0, len = getUsedRowCount(); i < len; i++) { // 遍历有效行。
                    int tProdId = ((Integer) tblContent.getValueAt(i, 0)).intValue(); // 先取出产品ID，
                    sql = "select PRICE, COST, STORE from product where id = ".concat(String.valueOf(tProdId));
                    Connection conn = PIMDBModel.getConection();
                    Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = smt.executeQuery(sql);
                    rs.beforeFirst();
                    rs.next();
                    int tCount = Integer.parseInt((String) tblContent.getValueAt(i, 2));
                    int tProfit = (rs.getInt("PRICE") - rs.getInt("COST")) * tCount;
                    int tLeftCount = rs.getInt("STORE") - tCount;
                    rs.close(); // 至此通过取出产品PRICE，COST，Store字段内容，乘以数量，得到了本交易的利润---------

                    sql =
                            "INSERT INTO output(EMPLOYEEID, TIME, PRODUCTID, AMOUNT, TOLTALPRICE, PROFIT, FOLDERID) VALUES ("
                                    .concat(String.valueOf(tID)).concat(", '")
                                    .concat(tDate)
                                    .concat("', ")
                                    .concat(String.valueOf(tProdId))
                                    .concat(", ")
                                    .concat(String.valueOf(tCount))
                                    .concat(", ")
                                    // set
                                    // the
                                    // count.
                                    .concat(String.valueOf(CASUtility.getPriceByCent(Float
                                            .parseFloat((String) tblContent.getValueAt(i, 4))))).concat(", ")// set the
                                                                                                             // total
                                                                                                             // price.
                                    .concat(String.valueOf(tProfit)).concat(", 5302)");
                    smt.executeUpdate(sql); // 至此将本条交易记录入销售表中。

                    sql =
                            "update product set Store = ".concat(String.valueOf(tLeftCount)).concat(" where id = ")
                                    .concat(String.valueOf(tProdId));
                    smt.executeUpdate(sql);
                    smt.close();
                    smt = null; // 至此产品表中的库存量被更改。
                }

                tfdProdNumber.grabFocus(); // 焦点复位。

                Object tUsePrinter = CustOpts.custOps.getValue(PosDlgConst.UsePrinter);
                if (tUsePrinter == null || tUsePrinter.equals("true")) { // 打印票据,界面复位
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            printInvoice(tDate); // 打印票据
                        }
                    });
                }

                Object tUseMoneyBox = CustOpts.custOps.getValue(PosDlgConst.UseMoenyBox);
                if (tUseMoneyBox == null || tUseMoneyBox.equals("true")) { // 弹开钱箱
                    Object tUniCommand = CustOpts.custOps.getValue(PosDlgConst.UniCommand);
                    if ("true".equals(tUniCommand) || tUniCommand == null) {// 如果是采用通用命令（没有钱箱卡），则向端口写数据
                        openMoneyBox();
                    } else { // 如果是Windows系统，则直接调用exe格式的开钱箱程序
                        String tSrcPath = tUniCommand.toString();
                        Runtime.getRuntime().exec(tSrcPath);
                    }
                }

                enableBtns(true);
                setStatusMes(PosDlgConst.NoteProdNumber1);
            } else { // 如果付得钱不够，则（可能换其他币种）继续支付）@TODO：注意所有的动作都要记录下来，以便于后面的(交班）统计。
                // @TODO：记录下来刚才所付钱的币种盒数量。
                float tLeft = tShoudReceive - tReceived;
                tfdShoudReceive.setText(decimalFormat.format(tLeft));

                shouldReceive = tLeft / Float.parseFloat( // 将找零的实例变量更新并换算成人民币。
                        (String) CustOpts.custOps.getValue(cmbMoneyType.getSelectedItem()));

                tfdChange.setText(PosDlgConst.Continue);
                tfdActuallyReceive.setText("");
            }
        } catch (Exception exp) { // 没有填的话，就移焦点过去填，
            tfdActuallyReceive.grabFocus();
            tfdActuallyReceive.selectAll();
        }
    }

    private void openMoneyBox() {
        int[] ccs = new int[5];
        ccs[0] = 27;
        ccs[1] = 112;
        ccs[2] = 0;
        ccs[3] = 80;
        ccs[4] = 250;

        CommPortIdentifier tPortIdty;
        try {
            Enumeration tPorts = CommPortIdentifier.getPortIdentifiers();
            if (tPorts == null)
                JOptionPane.showMessageDialog(this, "no comm ports found!");
            else
                while (tPorts.hasMoreElements()) {
                    tPortIdty = (CommPortIdentifier) tPorts.nextElement();
                    if (tPortIdty.getName().equals("LPT1")) {
                        if (!tPortIdty.isCurrentlyOwned()) {
                            ParallelPort tParallelPort = (ParallelPort) tPortIdty.open("ParallelBlackBox", 2000);
                            DataOutputStream tOutStream = new DataOutputStream(tParallelPort.getOutputStream());
                            for (int i = 0; i < 5; i++)
                                tOutStream.write(ccs[i]);
                            tOutStream.flush();
                            tOutStream.close();
                            tParallelPort.close();
                        }
                    }
                }
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printInvoice(
            String pDate) {
        CommPortIdentifier tPortIdty;
        try {
            Enumeration tPorts = CommPortIdentifier.getPortIdentifiers();
            if (tPorts == null) {
                JOptionPane.showMessageDialog(this, "no comm ports found!");
                return;
            }

            while (tPorts.hasMoreElements()) {
                tPortIdty = (CommPortIdentifier) tPorts.nextElement();
                if (!tPortIdty.getName().equals("LPT1"))
                    continue;

                if (!tPortIdty.isCurrentlyOwned()) {
                    ParallelPort tParallelPort = (ParallelPort) tPortIdty.open("ParallelBlackBox", 2000);
                    DataOutputStream tOutStream = new DataOutputStream(tParallelPort.getOutputStream());

                    tOutStream.write(27); // 打印机初始化：
                    tOutStream.write(64);

                    char[] tTime = pDate.toCharArray(); // 输出日期时间 输出操作员工号
                    for (int i = 0; i < tTime.length; i++)
                        tOutStream.write(tTime[i]);

                    tOutStream.write(13); // 回车
                    tOutStream.write(10); // 换行
                    tOutStream.write(10); // 进纸一行

                    tOutStream.write(28); // 设置为中文模式：
                    tOutStream.write(38);
                    String tContent = ((String) CustOpts.custOps.getValue(PosDlgConst.PrintTitle)).concat("\n");
                    for (int i = 0, len = getUsedRowCount(); i < len; i++) { // 遍历有效行。
                        tContent = tContent.concat((String) tblContent.getValueAt(i, 1)).concat("\n"); // 再取出品名
                        tContent = tContent.concat((String) tblContent.getValueAt(i, 3)).concat("   "); // 再取出单价
                        tContent = tContent.concat((String) tblContent.getValueAt(i, 2)).concat("   "); // 再取出数量
                        tContent = tContent.concat((String) tblContent.getValueAt(i, 4)).concat("\n"); // 再取出小计
                    }
                    for (int i = 0; i < 4; i++)
                        // 换行
                        tContent = tContent.concat("\n");

                    tContent = tContent.concat(PosDlgConst.SumTotal);
                    tContent = tContent.concat(tfdShoudReceive.getText());
                    tContent = tContent.concat(PosDlgConst.Unit).concat("   ");// 总计

                    tContent = tContent.concat(PosDlgConst.Receive);
                    tContent = tContent.concat(tfdActuallyReceive.getText());
                    tContent = tContent.concat(PosDlgConst.Unit).concat("\n");// 收银

                    tContent = tContent.concat(PosDlgConst.Change);
                    tContent = tContent.concat(tfdChange.getText());
                    tContent = tContent.concat(PosDlgConst.Unit);// 找零

                    tContent =
                            tContent.concat("\n\n      ").concat(
                                    (String) CustOpts.custOps.getValue(PosDlgConst.Thankword));
                    tContent = tContent.concat("\n\n");

                    Object tEncodType = CustOpts.custOps.getValue(PosDlgConst.EncodeStyle);
                    if (tEncodType == null)
                        tEncodType = "GBK";
                    if (!Charset.isSupported(tEncodType.toString()))
                        return;
                    BufferedWriter tWriter =
                            new BufferedWriter(new OutputStreamWriter(tOutStream, tEncodType.toString()));
                    tWriter.write(tContent);
                    tWriter.close();

                    tOutStream.close();
                    tParallelPort.close();
                }
            }
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initConponent() {
        int tShoestring = 0;
        try {
            tShoestring = Integer.parseInt((String) CustOpts.custOps.getValue(PosDlgConst.Shoestring));
        } catch (Exception exp) {
        }
        startTime = Calendar.getInstance().getTime().toLocaleString();
        lblOperator = new JLabel(PosDlgConst.Operator.concat(PosDlgConst.Colon).concat(CustOpts.custOps.getUserName()));
        lblShoestring =
                new JLabel(PosDlgConst.LeftMoney.concat(PosDlgConst.Colon)
                        .concat(decimalFormat.format(tShoestring / 100.0)).concat(PosDlgConst.Unit));
        lblStartTime = new JLabel(PosDlgConst.StartTime.concat(PosDlgConst.Colon).concat(startTime));// @Todo:以后改为从服务器上获取。
        lblProdNumber = new JLabel(PosDlgConst.ProdNumber);
        lblProdName = new JLabel(PosDlgConst.ProdName);
        lblCount = new JLabel(PosDlgConst.Count);
        lblPrice = new JLabel(PosDlgConst.Price);
        lblTotlePrice = new JLabel(PosDlgConst.Subtotal);
        lblCustomer = new JLabel(PosDlgConst.Customer);
        lblPackage = new JLabel(PosDlgConst.Package);
        lblNote = new JLabel(PosDlgConst.Note);

        tfdProdNumber = new JTextField();
        cmbProdName = new JComboBox();
        tfdCount = new JTextField("1");
        tfdPrice = new JTextField();
        tfdTotlePrice = new JTextField();
        tfdCustomer = new JTextField();
        tfdPackage = new JTextField();
        tarNote = new JTextArea();

        btnOffDuty = new JButton(PosDlgConst.OffDuty);
        btnCheck = new JButton(PosDlgConst.Check);
        btnInput = new JButton(PosDlgConst.Input);
        btnRefund = new JButton(PosDlgConst.Refund);
        btnHangup = new JButton(PosDlgConst.Hangup);
        btnStatic = new JButton(PosDlgConst.Static);
        btnOption = new JButton(PosDlgConst.Option);

        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);
        lblCalculate = new JLabel(PosDlgConst.Calculate);
        tfdShoudReceive = new JTextField("0.00");
        lblUnit = new JLabel(PosDlgConst.Unit);
        lblReceive = new JLabel(PosDlgConst.Receive);
        tfdActuallyReceive = new JTextField();
        lblMoneyType = new JLabel(PosDlgConst.MoneyType);
        cmbMoneyType = new JComboBox();
        lblChange = new JLabel(PosDlgConst.Change);
        tfdChange = new JTextField("0.00");
        lblChangeUnit = new JLabel(PosDlgConst.Unit);
        lblStatus = new JLabel();
        // properties
        setLayout(null);
        cmbMoneyType.setModel(new DefaultComboBoxModel(PosDlgConst.MoneyUnit));
        cmbMoneyType.setSelectedIndex(0);

        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(20);
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);
        tfdShoudReceive.setFont(tFont);
        tfdChange.setFont(tFont);

        // Margin-----------------
        btnOffDuty.setMargin(new Insets(0, 0, 0, 0));
        btnCheck.setMargin(btnOffDuty.getInsets());
        btnHangup.setMargin(btnOffDuty.getInsets());
        btnStatic.setMargin(btnOffDuty.getInsets());
        btnOption.setMargin(btnOffDuty.getInsets());
        btnInput.setMargin(btnOffDuty.getInsets());
        btnRefund.setMargin(btnOffDuty.getInsets());
        // border----------
        tblContent.setBorder(tfdPackage.getBorder());
        tarNote.setBorder(tfdPackage.getBorder());
        lblCalculate.setBorder(tfdPackage.getBorder());
        lblUnit.setBorder(tfdPackage.getBorder());
        lblReceive.setBorder(tfdPackage.getBorder());
        lblMoneyType.setBorder(tfdPackage.getBorder());
        lblChange.setBorder(tfdPackage.getBorder());
        lblChangeUnit.setBorder(tfdPackage.getBorder());
        lblStatus.setBorder(tfdPackage.getBorder());
        // forcus-------------
        tfdPrice.setFocusable(false);
        tfdTotlePrice.setFocusable(false);
        tfdCustomer.setFocusable(false);
        tfdPackage.setFocusable(false);
        tarNote.setFocusable(false);

        btnOffDuty.setFocusable(false);
        btnCheck.setFocusable(false);
        btnHangup.setFocusable(false);
        // btnMUser.setFocusable(false);
        // btnMRate.setFocusable(false);
        btnStatic.setFocusable(false);
        btnOption.setFocusable(false);
        btnInput.setFocusable(false);
        btnRefund.setFocusable(false);

        tblContent.setFocusable(false);
        tfdShoudReceive.setFocusable(false);
        cmbMoneyType.setFocusable(false);
        tfdChange.setFocusable(false);

        lblCalculate.setHorizontalAlignment(JLabel.CENTER);
        // built
        add(lblOperator);
        add(lblShoestring);
        add(lblStartTime);
        add(lblProdNumber);
        add(lblProdName);
        add(lblCount);
        add(lblPrice);
        add(lblTotlePrice);
        add(lblCustomer);
        add(lblPackage);
        add(lblNote);

        add(tfdProdNumber);
        add(cmbProdName);
        add(tfdCount);
        add(tfdPrice);
        add(tfdTotlePrice);
        add(tfdCustomer);
        add(tfdPackage);
        add(tarNote);

        add(btnOffDuty);
        add(btnCheck);
        add(btnHangup);
        add(btnStatic);
        add(btnOption);
        add(btnInput);
        add(btnRefund);

        add(srpContent);
        add(lblCalculate);
        add(tfdShoudReceive);
        add(lblUnit);
        add(lblReceive);
        add(tfdActuallyReceive);
        add(lblMoneyType);
        add(cmbMoneyType);
        add(lblChange);
        add(tfdChange);
        add(lblChangeUnit);
        add(lblStatus);

        // add listener
        addComponentListener(this);

        tfdProdNumber.addFocusListener(this);
        cmbProdName.addFocusListener(this);
        tfdCount.addFocusListener(this);
        tfdActuallyReceive.addFocusListener(this);

        tfdProdNumber.addKeyListener(this);
        cmbProdName.addKeyListener(this);
        tfdCount.addKeyListener(this);
        tfdActuallyReceive.addKeyListener(this);
        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）

        btnOffDuty.addActionListener(this);
        btnCheck.addActionListener(this);
        btnHangup.addActionListener(this);
        btnStatic.addActionListener(this);
        btnOption.addActionListener(this);
        btnInput.addActionListener(this);
        btnRefund.addActionListener(this);
        cmbMoneyType.addActionListener(this);// @NOTE：之所以另一个Cmb用keyListener而这个用actionListener，是因为ActionListner的话，上下移动过程中不停发事件。
        // initContents--------------
        // SwingUtilities.invokeLater(new Runnable() { //@NOTE: it seems that it's not promised to be called before ui
        // updated.
        // @Override
        // public void run() {
        initComponents();
        initTable();
        // }
        // });
    }

    public void initComponents() {
        String sql = "select ID, SUBJECT from product where code = '' and deleted != 'true'";
        try {
            Connection connection = PIMDBModel.getConection();
            Statement statement =
                    connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            prodIDAry = new int[tmpPos];
            prodSubjectAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                prodIDAry[tmpPos] = rs.getInt("ID");
                prodSubjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (Exception e) {
            ErrorUtil.write(e);
        }
        cmbProdName.setModel(new DefaultComboBoxModel(prodSubjectAry));
        cmbProdName.setSelectedIndex(-1);
    }

    private void initTable() {
        Object[][] tValues = new Object[1][header.length];
        tblContent.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    public void reLayout() {
        int prmWidth = getWidth();
        int prmHeight = getHeight();
        lblOperator.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblOperator.getPreferredSize().width,
                lblOperator.getPreferredSize().height);
        int tHalfHeight = (prmHeight - lblOperator.getY() - lblOperator.getHeight()) / 2;
        int tBtnWidht = (prmWidth - CustOpts.HOR_GAP * 8) / 7;
        int tBtnHeight = prmHeight / 20;
        int tGap = tHalfHeight / 11;
        int tVGap = tGap * 2 / 3;
        int tCompH = tGap + tGap - tVGap;
        int tFieldWidth1 = prmWidth / 2;

        lblStartTime.setBounds(prmWidth - lblStartTime.getPreferredSize().width - CustOpts.HOR_GAP, lblOperator.getY(),
                lblStartTime.getPreferredSize().width, lblOperator.getHeight());
        lblShoestring.setBounds(
                lblOperator.getX()
                        + lblOperator.getWidth()
                        + (lblStartTime.getX() - lblOperator.getX() - lblOperator.getWidth() - lblShoestring
                                .getPreferredSize().width) / 2, lblOperator.getY(),
                lblShoestring.getPreferredSize().width, lblOperator.getHeight());
        lblProdNumber.setBounds(CustOpts.HOR_GAP, lblOperator.getY() + lblOperator.getHeight() + tVGap,
                lblProdNumber.getPreferredSize().width, tCompH);
        tfdProdNumber.setBounds(lblProdNumber.getX() + lblProdNumber.getWidth(), lblProdNumber.getY(), tFieldWidth1,
                tCompH);
        lblProdName.setBounds(lblProdNumber.getX(), lblProdNumber.getY() + lblProdNumber.getHeight() + tVGap,
                lblProdName.getPreferredSize().width, tCompH);
        cmbProdName.setBounds(lblProdName.getX() + lblProdName.getWidth(), lblProdName.getY(), tFieldWidth1, tCompH);
        lblCount.setBounds(lblProdName.getX(), lblProdName.getY() + lblProdName.getHeight() + tVGap,
                lblCount.getPreferredSize().width, tCompH);
        tfdCount.setBounds(lblCount.getX() + lblCount.getWidth(), lblCount.getY(), tFieldWidth1, tCompH);
        lblPrice.setBounds(lblCount.getX(), lblCount.getY() + lblCount.getHeight() + tVGap,
                lblPrice.getPreferredSize().width, tCompH);
        tfdPrice.setBounds(lblPrice.getX() + lblPrice.getWidth(), lblPrice.getY(), tFieldWidth1, tCompH);
        lblTotlePrice.setBounds(lblPrice.getX(), lblPrice.getY() + lblPrice.getHeight() + tVGap,
                lblTotlePrice.getPreferredSize().width, tCompH);
        tfdTotlePrice.setBounds(lblTotlePrice.getX() + lblTotlePrice.getWidth(), lblTotlePrice.getY(), tFieldWidth1,
                tCompH);

        lblCustomer.setBounds(tfdProdNumber.getX() + tfdProdNumber.getWidth() + CustOpts.HOR_GAP, tfdProdNumber.getY(),
                lblCustomer.getPreferredSize().width, tCompH);
        tfdCustomer.setBounds(lblCustomer.getX() + lblCustomer.getWidth(), lblCustomer.getY(),
                prmWidth - lblCustomer.getX() - lblCustomer.getWidth() - CustOpts.HOR_GAP, tCompH);
        lblPackage.setBounds(lblCustomer.getX(), lblCustomer.getY() + lblCustomer.getHeight() + tVGap,
                lblPackage.getPreferredSize().width, tCompH);
        tfdPackage.setBounds(lblPackage.getX() + lblPackage.getWidth(), lblPackage.getY(), prmWidth - lblPackage.getX()
                - lblPackage.getWidth() - CustOpts.HOR_GAP, tCompH);
        lblNote.setBounds(lblPackage.getX(), lblPackage.getY() + lblPackage.getHeight() + tVGap,
                lblNote.getPreferredSize().width, tCompH);
        tarNote.setBounds(lblNote.getX() + lblNote.getWidth(), lblNote.getY(),
                prmWidth - lblNote.getX() - lblNote.getWidth() - CustOpts.HOR_GAP,
                lblTotlePrice.getY() + lblTotlePrice.getHeight() - lblNote.getY());

        btnOffDuty.setBounds(CustOpts.HOR_GAP, tarNote.getY() + tarNote.getHeight() + tVGap, tBtnWidht, tBtnHeight);
        btnInput.setBounds(btnOffDuty.getX() + tBtnWidht + CustOpts.HOR_GAP, btnOffDuty.getY(), tBtnWidht, tBtnHeight);
        btnRefund.setBounds(btnInput.getX() + tBtnWidht + CustOpts.HOR_GAP, btnOffDuty.getY(), tBtnWidht, tBtnHeight);
        btnHangup.setBounds(btnRefund.getX() + tBtnWidht + CustOpts.HOR_GAP, btnOffDuty.getY(), tBtnWidht, tBtnHeight);
        btnCheck.setBounds(btnHangup.getX() + tBtnWidht + CustOpts.HOR_GAP, btnOffDuty.getY(), tBtnWidht, tBtnHeight);
        btnStatic.setBounds(btnCheck.getX() + tBtnWidht + CustOpts.HOR_GAP, btnOffDuty.getY(), tBtnWidht, tBtnHeight);
        btnOption.setBounds(btnStatic.getX() + tBtnWidht + CustOpts.HOR_GAP, btnOffDuty.getY(), tBtnWidht, tBtnHeight);

        srpContent
                .setBounds(CustOpts.HOR_GAP, btnOffDuty.getY() + btnOffDuty.getHeight() + tVGap, prmWidth * 3 / 5,
                        prmHeight - btnOffDuty.getY() - btnOffDuty.getHeight() - tVGap - CustOpts.LBL_HEIGHT
                                - CustOpts.VER_GAP);
        lblCalculate.setBounds(srpContent.getX() + srpContent.getWidth() + CustOpts.HOR_GAP, srpContent.getY(),
                prmWidth - srpContent.getWidth() - CustOpts.HOR_GAP * 3, tGap);
        tfdShoudReceive.setBounds(lblCalculate.getX(), lblCalculate.getY() + lblCalculate.getHeight(),
                lblCalculate.getWidth() * 2 / 3, (srpContent.getHeight() - lblCalculate.getHeight()) / 3);
        lblUnit.setBounds(tfdShoudReceive.getX() + tfdShoudReceive.getWidth(), tfdShoudReceive.getY(),
                lblCalculate.getWidth() - tfdShoudReceive.getWidth(), tfdShoudReceive.getHeight());
        lblReceive.setBounds(tfdShoudReceive.getX(), tfdShoudReceive.getY() + tfdShoudReceive.getHeight(),
                lblCalculate.getWidth() / 3, tfdShoudReceive.getHeight() / 2);
        tfdActuallyReceive.setBounds(lblReceive.getX() + lblReceive.getWidth(), lblReceive.getY(),
                lblCalculate.getWidth() - lblReceive.getWidth(), lblReceive.getHeight());
        lblMoneyType.setBounds(lblReceive.getX(), lblReceive.getY() + lblReceive.getHeight(), lblReceive.getWidth(),
                lblReceive.getHeight());
        cmbMoneyType.setBounds(lblMoneyType.getX() + lblMoneyType.getWidth(), lblMoneyType.getY(),
                tfdActuallyReceive.getWidth(), lblMoneyType.getHeight());
        lblChange.setBounds(lblMoneyType.getX(), lblMoneyType.getY() + lblMoneyType.getHeight(),
                lblMoneyType.getWidth(), srpContent.getY() + srpContent.getHeight() - lblMoneyType.getY()
                        - lblMoneyType.getHeight());
        tfdChange.setBounds(lblChange.getX() + lblChange.getWidth(), lblChange.getY(), cmbMoneyType.getWidth()
                - lblChangeUnit.getPreferredSize().width * 2, lblChange.getHeight());
        lblChangeUnit.setBounds(tfdChange.getX() + tfdChange.getWidth(), tfdChange.getY(),
                lblChangeUnit.getPreferredSize().width * 2, lblChange.getHeight());
        lblStatus.setBounds(srpContent.getX(), srpContent.getY() + srpContent.getHeight() + 2, srpContent.getWidth()
                + lblCalculate.getWidth() + CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        resetColWidth();
    }

    public void cancelHangup(
            int pIdx) {
        // 先复位List中的内容
        Object[][] tValues = (Object[][]) hangupVec.get(pIdx);
        tblContent.setDataVector(tValues, header);

        resetColWidth();

        hangupVec.remove(pIdx);

        shouldReceive = 0;
        for (int i = 0; i < tValues.length; i++) {
            shouldReceive += Float.parseFloat((String) tValues[i][4]);
        }
        tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
    }

    private void hangup() {
        if (isOnProcess()) {// 如果正在收银,加入挂单库
            int tUsedRow = getUsedRowCount();
            int tColCount = tblContent.getColumnCount();
            Object[][] values = new Object[tUsedRow][tColCount];
            for (int i = 0; i < tUsedRow; i++)
                for (int j = 0; j < tColCount; j++)
                    values[i][j] = tblContent.getValueAt(i, j);
            hangupVec.add(values);
            resetAll();
        } else { // 如果不是正在收银，则显示挂单列表
            new HangupDlg(PosFrame.instance).setVisible(true);
        }
    }

    private boolean isOnProcess() {
        return getUsedRowCount() > 0 || tfdProdNumber.getText().length() > 0 || cmbProdName.getSelectedIndex() >= 0;
    }

    private int getUsedRowCount() {
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++)
            if (tblContent.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblContent.getRowCount();
    }

    private void resetAll() {
        resetDlgArea();
        resetListArea();
        cmbMoneyType.setSelectedIndex(0);// @note:必须先执行，因为本语句导致actionpermed方法，从而使tfdShoudReceive被赋值。
        shouldReceive = 0;
        tfdShoudReceive.setText("0.00");
        lblUnit.setText(PosDlgConst.Unit);
        tfdChange.setText("0.00");
        tfdProdNumber.grabFocus(); // 焦点复位。
    }

    private void resetDlgArea() {
        tfdProdNumber.setText(""); // 并清空对话盒区域.
        cmbProdName.setModel(new DefaultComboBoxModel(prodSubjectAry));
        cmbProdName.setSelectedIndex(-1);
        tfdCount.setText("1");
        tfdPrice.setText("");
        tfdTotlePrice.setText("");
        tfdCustomer.setText("");
        tfdPackage.setText("");
        tarNote.setText("");

        tfdActuallyReceive.setText("");
    }

    private void resetListArea() {
        int tRowCount = tblContent.getRowCount();
        int tColCount = tblContent.getColumnCount();
        for (int j = 0; j < tRowCount; j++)
            for (int i = 0; i < tColCount; i++)
                tblContent.setValueAt(null, j, i);
    }

    private void enableBtns(
            boolean pIsEnable) {
        btnOffDuty.setEnabled(pIsEnable);
        btnCheck.setEnabled(pIsEnable);
        btnInput.setEnabled(pIsEnable);
        btnRefund.setEnabled(pIsEnable);
    }

    public static void setStatusMes(
            String pMes) {
        lblStatus.setText(pMes);
    }

    private JLabel lblOperator;
    private JLabel lblShoestring;
    private JLabel lblStartTime;
    private JLabel lblProdNumber;
    private JLabel lblProdName;
    private JLabel lblCount;
    private JLabel lblPrice;
    private JLabel lblTotlePrice;
    private JLabel lblCustomer;
    private JLabel lblPackage;
    private JLabel lblNote;

    JTextField tfdProdNumber;
    private JComboBox cmbProdName;
    private JTextField tfdCount;
    private JTextField tfdPrice;
    private JTextField tfdTotlePrice;
    private JTextField tfdCustomer;
    private JTextField tfdPackage;
    private JTextArea tarNote;

    private JButton btnOffDuty;
    private JButton btnCheck;
    private JButton btnInput;
    private JButton btnRefund;
    private JButton btnHangup;
    private JButton btnStatic;
    private JButton btnOption;

    private PIMTable tblContent;
    private PIMScrollPane srpContent;
    private JLabel lblCalculate;
    private JTextField tfdShoudReceive;
    private JLabel lblUnit;
    private JLabel lblReceive;
    private JTextField tfdActuallyReceive;
    private JLabel lblMoneyType;
    private JComboBox cmbMoneyType;
    private JLabel lblChange;
    private JTextField tfdChange;
    private JLabel lblChangeUnit;
    static JLabel lblStatus;
    public Vector hangupVec = new Vector();
    private int prodID;
    private float shouldReceive;
    private int[] prodIDAry;
    private String[] prodSubjectAry;
    private String[] header = new String[] { PosDlgConst.ProdNumber, PosDlgConst.ProdName, PosDlgConst.Count,
            PosDlgConst.Price, PosDlgConst.Subtotal };
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
}
