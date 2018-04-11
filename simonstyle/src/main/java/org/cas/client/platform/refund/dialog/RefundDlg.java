package org.cas.client.platform.refund.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pos.dialog.MerchandiseDlg;
import org.cas.client.platform.pos.dialog.PosDlgConst;
import org.cas.client.platform.pos.dialog.PosUtility;
import org.cas.client.resource.international.DlgConst;

public class RefundDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, FocusListener,
        KeyListener, DocumentListener {
    public RefundDlg(JFrame pParent) {
        super(pParent, true);
        parent = pParent;
        initDialog();
    }

    public RefundDlg(Frame parent, ActionListener prmAction, PIMRecord prmRecord) {
        super(parent, false);
        isPartOfView = (parent == null);
        actionListener = prmAction; // 当对话盒采集输入结束后，调该监听的actionperform方法。
        record = prmRecord;
        initDialog();
    }

    @Override
    public PIMRecord getContents() {
        return null;
    }

    @Override
    public boolean setContents(
            PIMRecord prmRecord) {
        return true;
    }

    @Override
    public void makeBestUseOfTime() {
    }

    @Override
    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    @Override
    public PIMTextPane getTextPane() {
        return null;
    }

    @Override
    public void release() {
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    @Override
    public void componentMoved(
            ComponentEvent e) {
    };

    @Override
    public void componentShown(
            ComponentEvent e) {
    };

    @Override
    public void componentHidden(
            ComponentEvent e) {
    };

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        // 初始化－－－－－－－－－－－－－－－－
        lblProdNumber = new JLabel(PosDlgConst.ProdNumber);
        lblProdName = new JLabel(PosDlgConst.ProdName);
        lblCount = new JLabel(PosDlgConst.Count);
        lblPrice = new JLabel(PosDlgConst.Price);
        lblTotlePrice = new JLabel(PosDlgConst.Subtotal);
        lblCause = new JLabel(PosDlgConst.Cause);
        lblPackage = new JLabel(PosDlgConst.Package);
        lblType = new JLabel(PosDlgConst.Type);

        tfdProdCode = new JTextField();
        cmbProdName = new JComboBox();
        tfdCount = new JTextField("1");
        tfdPrice = new JTextField();
        tfdTotlePrice = new JTextField();
        tfdCause = new JTextField();
        tfdPackage = new JTextField();
        tfdType = new JTextField();

        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);

        lblUnit1 = new JLabel(PosDlgConst.Unit);
        lblUnit2 = new JLabel(PosDlgConst.Unit);
        lblUnit3 = new JLabel(PosDlgConst.Unit);
        lblShoudReceive = new JLabel(PosDlgConst.Calculate);
        tfdShoudReceive = new JTextField("0.00");
        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // properties
        setTitle(PosDlgConst.Refund);
        setBounds((CustOpts.SCRWIDTH - 500) / 2, (CustOpts.SCRHEIGHT - 400) / 2, 500, 400); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        setResizable(true);

        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(20);
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        // border----------
        tblContent.setBorder(tfdPackage.getBorder());
        tfdType.setBorder(tfdPackage.getBorder());
        // forcus-------------
        tfdPrice.setFocusable(false);
        tfdPackage.setFocusable(false);
        tfdType.setFocusable(false);
        tfdCause.setFocusable(false);
        tfdTotlePrice.setFocusable(false);
        tblContent.setFocusable(false);
        tfdShoudReceive.setFocusable(false);
        ok.setMargin(new Insets(0, 0, 0, 0));
        cancel.setMargin(ok.getInsets());

        ok.setMnemonic('O');
        cancel.setMnemonic('C');

        // 搭建－－－－－－－－－－－－－
        add(lblUnit1);
        add(lblUnit2);
        add(lblProdNumber);
        add(lblProdName);
        add(lblCount);
        add(lblPrice);
        add(lblTotlePrice);
        add(lblCause);
        add(lblPackage);
        add(lblType);
        add(tfdProdCode);
        add(cmbProdName);
        add(tfdCount);
        add(tfdPrice);
        add(tfdTotlePrice);
        add(tfdCause);
        add(tfdPackage);
        add(tfdType);
        add(srpContent);

        add(lblUnit3);
        add(lblShoudReceive);
        add(tfdShoudReceive);
        add(ok);
        add(cancel);
        // 加监听器－－－－－－－－
        tfdProdCode.addFocusListener(this);
        tfdCount.addFocusListener(this);

        tfdProdCode.addKeyListener(this);
        cmbProdName.addKeyListener(this);
        tfdCount.addKeyListener(this);
        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        tfdProdCode.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）
        getContentPane().addComponentListener(this);

        ok.addActionListener(this);
        cancel.addActionListener(this);
        // initContents--------------
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initComponents();
                initTable();
                tfdProdCode.grabFocus();
            }
        });
    }

    @Override
    public void insertUpdate(
            DocumentEvent e) {
        String tProdNumber = tfdProdCode.getText();
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
                        tfdType.setText(rs.getString("content"));
                        break;
                    }
                    startWaitThread();
                } else {
                    MerchandiseDlg tDlg = new MerchandiseDlg(parent, tProdNumber);
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
                        tfdType.setText(tDlg.getRemark());
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
        if (o == ok) {
            String tDate = Calendar.getInstance().getTime().toLocaleString();// 至此得到本批记录入库的时间。
            try {
                for (int i = 0, len = getUsedRowCount(); i < len; i++) { // 遍历有效行。
                    int tProdId = ((Integer) tblContent.getValueAt(i, 0)).intValue(); // 先取出产品ID，
                    String sql = "select PRICE, COST, STORE from product where id = ".concat(String.valueOf(tProdId));
                    Connection conn = PIMDBModel.getConection();
                    Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = smt.executeQuery(sql);
                    rs.beforeFirst();
                    rs.next();
                    int tCount = Integer.parseInt((String) tblContent.getValueAt(i, 2));
                    int tProfit = (rs.getInt("PRICE") - rs.getInt("COST")) * tCount;
                    int tLeftCount = rs.getInt("STORE") + tCount;
                    rs.close(); // 至此通过取出产品PRICE，COST，Store字段内容，乘以数量，得到了本交易的利润---------

                    sql =
                            "INSERT INTO refund( TIME, PRODUCTID, AMOUNT, TOLTALPRICE, PROFIT, FOLDERID) VALUES ('"
                                    .concat(tDate)
                                    .concat("', ")
                                    .concat(String.valueOf(tProdId))
                                    .concat(", ")
                                    .concat(String.valueOf(tCount))
                                    .concat(", ")
                                    // set the count.
                                    .concat(String.valueOf(CASUtility.getPriceByCent(Float
                                            .parseFloat((String) tblContent.getValueAt(i, 4))))).concat(", ")// set the
                                                                                                             // total
                                                                                                             // price.
                                    .concat(String.valueOf(tProfit)).concat(", 5702)");
                    smt.executeUpdate(sql); // 至此将本条交易记录入销售表中。

                    sql =
                            "update product set Store = ".concat(String.valueOf(tLeftCount)).concat(" where id = ")
                                    .concat(String.valueOf(tProdId));
                    smt.executeUpdate(sql);
                    smt.close();
                    smt = null; // 至此产品表中的库存量被更改。
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            // 检查
            dispose();
        } else if (o == cancel)
            dispose();
    }

    // Key Listener--------------------------------
    @Override
    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
        if (o == tfdProdCode) { // 默认情况下，焦点应该都在本组件里的。
            int tKeyCode = e.getKeyCode();
            if (tKeyCode == 10) { // 货号框中，随时当按回车键，立即开始在库中查询匹配商品。（通常执行不到，因为扫描输入后，会根据位数判断自动执行查询。
                String tProdNumber = tfdProdCode.getText();
                if (tProdNumber == null || tProdNumber.length() < 1) {// 发现并没有选中什么商品，这种回车和ProdNumberField中的无码回车的意义一样---结算！
                    actionPerformed(new ActionEvent(ok, 0, null));
                    return;
                }
                String sql =
                        "select id, subject, price, unit, content from product where code = '".concat(tProdNumber)
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
                            float tPrice = rs.getInt("price");
                            tfdPrice.setText(String.valueOf(tPrice / 100)); // 单价
                            tfdTotlePrice.setText(String.valueOf(tCount * tPrice / 100)); // 总价
                            tfdPackage.setText(rs.getString("unit"));
                            tfdType.setText(rs.getString("content"));
                            break;
                        }
                        startWaitThread();
                    } else {
                        MerchandiseDlg tDlg = new MerchandiseDlg(parent, tProdNumber);
                        tDlg.setVisible(true);
                        if (tDlg.ADDED) {// 如果添加产品对话盒成功加入了产品
                            prodID = tDlg.getProdID();
                            tfdProdCode.setText(tDlg.getProdCode());// 则新的产品信息加入到当前对话盒区域，并启动线程。
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
                            tfdType.setText(tDlg.getRemark());
                            startWaitThread();
                        }
                    }
                    rs.close();// 关闭
                } catch (SQLException exp) {// 如果没有匹配是否会到该代码块？
                    ErrorUtil.write(exp);
                }
            } else
                commKeyProcess(e);
        } else if (o == cmbProdName) { // 遇到一个没有来的及贴条码的商品，操作员果断采取直接在ComboBox中选则产品的方法。
            if (e.getKeyCode() == 10) { // 聪明的POSMM敲了一个回车。
                String tProdName = (String) cmbProdName.getSelectedItem();
                if (tProdName == null) { // 发现并没有选中什么商品，这种回车和ProdNumberField中的无码回车的意义一样---结算！

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
                                tfdProdCode.setText(rs.getString("code")); // 条码
                                int tCount = 1;
                                try {
                                    tCount = Integer.parseInt(tfdCount.getText());
                                } catch (Exception exp) {
                                }
                                tfdCount.setText(String.valueOf(tCount)); // 数量
                                float tPrice = rs.getInt("price");
                                tfdPrice.setText(String.valueOf(tPrice / 100)); // 单价
                                tfdTotlePrice.setText(decimalFormat.format(tCount * tPrice)); // 总价
                                tfdPackage.setText(rs.getString("unit")); // 包装
                                tfdType.setText(rs.getString("content")); // 说明
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
                } catch (Exception exp) {
                    tfdCount.setText("1");
                }
                tfdProdCode.grabFocus(); // 跳回之前光标所在的那个组件。
            }
        }
    }

    private void commKeyProcess(
            KeyEvent e) {
        int tKeyCode = e.getKeyCode();
        if (tKeyCode == 27) {// ESC 表示取消对话盒区域内容，取消列表中项目，退出系统
            if (((JTextField) e.getSource()).getText().length() > 0)// 如果条码框或者产品名中有内容，就表示清空本条记录。
                resetDlgArea();
            else { // 如果是对话盒区域已经是空的了
                if (getUsedRowCount() != 0) { // 就看列表中是否有记录，有的话，F1就表示情况列表。
                    resetAll();
                }
            }
        } else if (tKeyCode == 17) {// 按"ctrl"键使光标跳至count.必须先输入数量再输入产品的道理是，扫描枪有可能会带回车，使你没有机会后敲数量。
            tfdCount.grabFocus();
            tfdCount.selectAll();
            // }else if(tKeyCode == 18){
            // hangup(); //当Alt键被按时，进行挂单操作！！
        } else if (tKeyCode == 38) {
            tblContent.setSelectedRow(tblContent.getSelectedRow() - 1);
            tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
        } else if (tKeyCode == 40) {
            tblContent.setSelectedRow(tblContent.getSelectedRow() + 1);
            tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
        } else if (tKeyCode == 8 || tKeyCode == 127) {
            // @NOTE:当Del键或者BackSpace键被按时，如果条码框中没有内容，则直接进行list中记录的删除动作。
            if (tfdProdCode.getText().length() == 0) {
                int tRow = tblContent.getSelectedRow();
                if (tRow < 0) { // 没有选中行的话，看看最后一行是第几行，选中它。
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
    }

    @Override
    public void keyReleased(
            KeyEvent e) {
    }

    @Override
    public void keyTyped(
            KeyEvent e) {
    }

    // 当对话盒区域内容填写完了以后，通过本方法启动一个线程，实现休眠500ms之后将对话盒区域内容增加到表中。
    // 同时更新tfdShoudReceive的内容。
    private void startWaitThread() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    addContentToList(); // 将对话盒区域内容加入到列表中去。
                    shouldReceive += Float.parseFloat(tfdTotlePrice.getText());
                    tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                    resetDlgArea(); // 对话和区域内容复位。
                    tfdProdCode.grabFocus(); // 焦点复位。
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
    }

    private void resetColWidth() {
        for (int i = 0, len = header.length; i < len; i++) {
            PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
            tmpCol.setWidth(i == 1 ? getWidth() - 267 : 60);
            tmpCol.setPreferredWidth(i == 1 ? getWidth() - 267 : 60);
        }
        tblContent.validate();
        tblContent.revalidate();
        tblContent.invalidate();
    }

    private void initComponents() {
        String sql = "select ID, SUBJECT from product where code = '' and deleted != true";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
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
        } catch (SQLException e) {
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
    @Override
    public void reLayout() {
        int prmWidth = getWidth();
        int tFieldWidth1 = prmWidth / 2 - CustOpts.HOR_GAP;

        lblProdNumber.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblProdNumber.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdProdCode.setBounds(lblProdNumber.getX() + lblProdNumber.getWidth(), lblProdNumber.getY(), tFieldWidth1
                - lblProdNumber.getX() - lblProdNumber.getWidth(), CustOpts.BTN_HEIGHT);
        lblPrice.setBounds(lblProdNumber.getX(), lblProdNumber.getY() + lblProdNumber.getHeight() + CustOpts.VER_GAP,
                lblProdNumber.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdPrice.setBounds(lblPrice.getX() + lblPrice.getWidth(), lblPrice.getY(), tFieldWidth1 - lblPrice.getX()
                - lblPrice.getWidth(), CustOpts.BTN_HEIGHT);
        lblUnit1.setBounds(
                tfdPrice.getX() + tfdPrice.getWidth() - lblUnit1.getPreferredSize().width - CustOpts.HOR_GAP,
                tfdPrice.getY(), lblUnit1.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblCount.setBounds(lblPrice.getX(), lblPrice.getY() + lblPrice.getHeight() + CustOpts.VER_GAP,
                lblPrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCount.setBounds(lblCount.getX() + lblCount.getWidth(), lblCount.getY(), tFieldWidth1 - lblCount.getX()
                - lblCount.getWidth(), CustOpts.BTN_HEIGHT);
        lblCause.setBounds(lblCount.getX(), lblCount.getY() + lblCount.getHeight() + CustOpts.VER_GAP,
                lblCause.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCause.setBounds(lblCause.getX() + lblCause.getWidth(), tfdCount.getY() + tfdCount.getHeight()
                + CustOpts.VER_GAP, tFieldWidth1 - lblCause.getX() - lblCause.getWidth(), CustOpts.BTN_HEIGHT);

        lblProdName.setBounds(tfdProdCode.getX() + tfdProdCode.getWidth() + CustOpts.HOR_GAP, tfdProdCode.getY(),
                lblProdName.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cmbProdName.setBounds(lblProdName.getX() + lblProdName.getWidth(), lblProdName.getY(),
                prmWidth - lblProdName.getX() - lblProdName.getWidth() - CustOpts.HOR_GAP - CustOpts.SIZE_EDGE * 2,
                CustOpts.BTN_HEIGHT);
        lblPackage.setBounds(lblProdName.getX(), lblProdName.getY() + lblProdName.getHeight() + CustOpts.VER_GAP,
                lblProdName.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdPackage.setBounds(lblPackage.getX() + lblPackage.getWidth(), lblPackage.getY(), prmWidth - lblPackage.getX()
                - lblPackage.getWidth() - CustOpts.HOR_GAP - CustOpts.SIZE_EDGE * 2, CustOpts.BTN_HEIGHT);
        lblType.setBounds(lblPackage.getX(), lblPackage.getY() + lblPackage.getHeight() + CustOpts.VER_GAP,
                lblPackage.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdType.setBounds(lblType.getX() + lblType.getWidth(), lblType.getY(),
                prmWidth - lblType.getX() - lblType.getWidth() - CustOpts.HOR_GAP - CustOpts.SIZE_EDGE * 2,
                CustOpts.BTN_HEIGHT);
        lblTotlePrice.setBounds(lblType.getX(), lblType.getY() + lblType.getHeight() + CustOpts.VER_GAP,
                lblTotlePrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdTotlePrice.setBounds(lblTotlePrice.getX() + lblTotlePrice.getWidth(), lblTotlePrice.getY(), prmWidth
                - lblTotlePrice.getX() - lblTotlePrice.getWidth() - CustOpts.HOR_GAP - CustOpts.SIZE_EDGE * 2,
                CustOpts.BTN_HEIGHT);
        lblUnit2.setBounds(tfdTotlePrice.getX() + tfdTotlePrice.getWidth() - lblUnit2.getPreferredSize().width
                - CustOpts.HOR_GAP, tfdTotlePrice.getY(), lblUnit2.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        srpContent.setBounds(CustOpts.HOR_GAP, tfdTotlePrice.getY() + tfdTotlePrice.getHeight() + CustOpts.VER_GAP,
                prmWidth - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2, getHeight() - CustOpts.VER_GAP * 3
                        - CustOpts.BTN_HEIGHT - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - tfdTotlePrice.getY()
                        - tfdTotlePrice.getHeight());
        lblShoudReceive.setBounds(srpContent.getX(), srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP,
                lblShoudReceive.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdShoudReceive.setBounds(lblShoudReceive.getX() + lblShoudReceive.getWidth(), lblShoudReceive.getY(), 60,
                CustOpts.BTN_HEIGHT);
        lblUnit3.setBounds(tfdShoudReceive.getX() + tfdShoudReceive.getWidth(), tfdShoudReceive.getY(), 60,
                CustOpts.BTN_HEIGHT);
        ok.setBounds(srpContent.getWidth() + srpContent.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH * 2,
                srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cancel.setBounds(ok.getWidth() + ok.getX() + CustOpts.HOR_GAP, ok.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

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
    }

    private int getUsedRowCount() {
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++)
            if (tblContent.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblContent.getRowCount();
    }

    private void resetDlgArea() {
        tfdProdCode.setText(""); // 并清空对话盒区域.
        cmbProdName.setModel(new DefaultComboBoxModel(prodSubjectAry));
        cmbProdName.setSelectedIndex(-1);
        tfdCount.setText("1");
        tfdPrice.setText("");
        tfdTotlePrice.setText("");
        tfdCause.setText("");
        tfdPackage.setText("");
        tfdType.setText("");
    }

    private void resetAll() {
        resetDlgArea();
        resetListArea();
        shouldReceive = 0;
        tfdProdCode.grabFocus(); // 焦点复位。
    }

    private void resetListArea() {
        int tRowCount = tblContent.getRowCount();
        int tColCount = tblContent.getColumnCount();
        for (int j = 0; j < tRowCount; j++)
            for (int i = 0; i < tColCount; i++)
                tblContent.setValueAt(null, j, i);
    }

    JFrame parent;
    boolean isPartOfView; // 用于标记本对话盒是独立显示还是作为View的一部分.
    boolean hasClose; // 标志对话框是否已关闭
    private ActionListener actionListener; // 当对话盒采集输入结束后，调该监听的actionperform方法。
    private PIMRecord record;

    JLabel lblProdNumber;
    JTextField tfdProdCode;
    JLabel lblProdName;
    JComboBox cmbProdName;
    JLabel lblCount;
    JTextField tfdCount;
    JLabel lblPrice;
    JTextField tfdPrice;
    JLabel lblTotlePrice;
    JTextField tfdTotlePrice;
    JLabel lblCause;
    JTextField tfdCause;
    JLabel lblPackage;
    JTextField tfdPackage;
    JLabel lblType;
    JTextField tfdType;

    PIMTable tblContent;
    PIMScrollPane srpContent;
    JLabel lblUnit1;
    JLabel lblUnit2;
    JLabel lblUnit3;
    JLabel lblShoudReceive;
    JTextField tfdShoudReceive;
    private JButton ok;
    private JButton cancel;

    public Vector hangupVec = new Vector();
    private int prodID;
    private float shouldReceive;
    private int[] prodIDAry;
    private String[] prodSubjectAry;
    private String[] header = new String[] { PosDlgConst.ProdNumber, PosDlgConst.ProdName, PosDlgConst.Count,
            PosDlgConst.Price, PosDlgConst.Subtotal };
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
}
