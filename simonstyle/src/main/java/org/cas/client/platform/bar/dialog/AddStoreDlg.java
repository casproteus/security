package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Container;
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.contact.dialog.ContactDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.resource.international.DlgConst;
import org.hsqldb.Record;

public class AddStoreDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, FocusListener,
        KeyListener, DocumentListener {
    public AddStoreDlg(JFrame pParent) {
        super(pParent, false);
        initDialog();
    }

    public PIMRecord getContents() {
        return null;
    }

    public boolean setContents(
            PIMRecord prmRecord) {
        return true;
    }

    public void makeBestUseOfTime() {
    }

    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    public PIMTextPane getTextPane() {
        return null;
    }

    public void release() {
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    public void componentMoved(
            ComponentEvent e) {
    };

    public void componentShown(
            ComponentEvent e) {
    };

    public void componentHidden(
            ComponentEvent e) {
    };

    public Container getContainer() {
        return getContentPane();
    }

    public void insertUpdate(
            DocumentEvent e) {
        String tProdNumber = tfdProdNumber.getText();
        if (tProdNumber.length() == BarUtility.getProdCodeLen()) {
            String sql =
                    "select id, subject, cost, unit, content from product where code = '".concat(tProdNumber).concat(
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
                        float tCost = rs.getInt("cost");
                        tfdCost.setText(decimalFormat.format(tCost / 100.0)); // 单价
                        tfdTotlePrice.setText(decimalFormat.format(tCount * tCost / 100)); // 总价
                        tfdPackage.setText(rs.getString("unit"));
                        tfdType.setText(rs.getString("CATEGORY"));
                        tfdNote.setText(rs.getString("content"));
                        break;
                    }
                } else {
                    MerchandiseDlg tDlg = new MerchandiseDlg(BarFrame.instance, tProdNumber);
                    tDlg.enableProdCode(false);
                    tDlg.setVisible(true);
                    if (tDlg.ADDED) {// 如果添加产品对话盒成功加入了产品
                        prodID = tDlg.getProdID();
                        Vector tVec = new Vector();// 直接设置ComboBox的Model，因为扫描仪导致的新品输入，是不会改条码的。
                        tVec.add(tDlg.getProdName());
                        DefaultComboBoxModel tModel = new DefaultComboBoxModel(tVec);
                        cmbProdName.setModel(tModel);
                        cmbProdName.setSelectedIndex(0);
                        int tCount = 1;
                        try {
                            tCount = Integer.parseInt(tfdCount.getText());
                        } catch (Exception exp) {
                        }
                        float tCost = Float.parseFloat(tDlg.getCost());
                        tfdCost.setText(decimalFormat.format(tCost / 100.0)); // 单价
                        tfdTotlePrice.setText(decimalFormat.format(tCount * tCost / 100)); // 总价
                        tfdPackage.setText(tDlg.getPackage());
                        tfdType.setText(tDlg.getProdType());
                        tfdNote.setText(tDlg.getRemark());
                    }
                }
                rs.close();// 关闭
            } catch (Exception exp) {
                ErrorUtil.write(exp);
            }
        }
    }

    public void removeUpdate(
            DocumentEvent e) {
    }

    public void changedUpdate(
            DocumentEvent e) {
    }

    public void focusGained(
            FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    public void focusLost(
            FocusEvent e) {
        Object o = e.getSource();
        if (o == tfdCount) {
            float tCost = 0;
            int tCount = 0;
            try {
                tCount = Integer.parseInt(tfdCount.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdCount.grabFocus();
                return;
            }
            try {
                tCost = Float.parseFloat(tfdCost.getText());
                tfdTotlePrice.setText(decimalFormat.format(tCost * tCount));
            } catch (Exception exp) {
                try {
                    float tTotalCost = Float.parseFloat(tfdTotlePrice.getText());
                    tfdCost.setText(decimalFormat.format(tTotalCost / tCount));
                } catch (Exception exp2) {
                }
            }// 至此格式验证完成------------
        } else if (o == tfdCost) {
            float tCost = 0;
            int tCount = 0;
            try {
                tCost = Float.parseFloat(tfdCost.getText());// 检查成本格式
            } catch (Exception exp) {
                if (!tfdCost.getText().equals("")) { // 有输入且不合法，则提示重新写。并返回。
                    JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                    tfdCost.grabFocus();
                    return;
                }
            }
            try { // 成本合法，检查数量是否合法，
                tCount = Integer.parseInt(tfdCount.getText()); // tfdCount中的内容有效，则更新总价。
                tfdTotlePrice.setText(decimalFormat.format(tCost * tCount));
            } catch (Exception exp) {
                tfdCount.grabFocus(); // 数量无效，则将光标移过去，便于更正。
            }
        } else if (o == tfdTotlePrice) {
            float tTotalCost = 0;
            int tCount = 0;

            try {
                tTotalCost = Float.parseFloat(tfdTotlePrice.getText());
            } catch (Exception exp) {
                if (!tfdCost.getText().equals("")) { // 有输入且不合法，则提示重新写。并返回。
                    JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                    tfdTotlePrice.grabFocus();
                    return;
                }
            }
            try {
                tCount = Integer.parseInt(tfdCount.getText());
                tfdCost.setText(decimalFormat.format(tTotalCost / tCount));
            } catch (Exception exp) {
                tfdCount.grabFocus();
            }// 至此格式验证完成------------
        }
    }

    // ActionListner----------------------------------
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == ok) {
            if (tfdProdNumber.getText().length() > 0 || cmbProdName.getSelectedIndex() >= 0) {// 对话盒区域有内容，检查后加入列表。
                try {
                    Float.parseFloat(tfdCost.getText());
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                    tfdCost.grabFocus();
                }
                try {
                    Integer.parseInt(tfdCount.getText());
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                    tfdCount.grabFocus();
                    return;
                }
                try {
                    Float.parseFloat(tfdTotlePrice.getText());
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                    tfdTotlePrice.grabFocus();
                    return;
                }// 至此检查输入是否格式OK完毕---------
                startWaitThread();
                return;
            } else { // 名字，条码框中都没有内容时按确定，表示记录要进库了。
                String tDate = Calendar.getInstance().getTime().toLocaleString();// 至此得到本批记录入库的时间。

                // String sql =
                // "Select id from userIdentity where username = '".concat(CustOpts.custOps.getUserName()).concat("'");
                // int tID = -1;
                try {
                    // ResultSet rs = PIMDBModel.getConection().createStatement(
                    // ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
                    // ResultSetMetaData rd = rs.getMetaData(); //得到结果集相关信息
                    //
                    // rs.afterLast();
                    // rs.relative(-1);
                    // rs.beforeFirst();
                    // while(rs.next())
                    // tID = rs.getInt("id");
                    // rs.close();//关闭 //至此得到当前用户对应的在userIdentity表中的ID号。

                    for (int i = 0, len = getUsedRowCount(); i < len; i++) { // 遍历有效行。
                        int tProdId = ((Integer) tblContent.getValueAt(i, 0)).intValue(); // 先取出产品ID，
                        String sql =
                                "select PRICE, COST, STORE from product where id = ".concat(String.valueOf(tProdId));
                        Connection conn = PIMDBModel.getConection();
                        Statement smt =
                                conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        ResultSet rs = smt.executeQuery(sql);
                        rs.beforeFirst();
                        rs.next();
                        int tCount = Integer.parseInt((String) tblContent.getValueAt(i, 2));
                        int tLeftCount = rs.getInt("STORE");
                        float cost = (rs.getInt("COST") * tLeftCount / 100 // 库中总成本
                        + Float.parseFloat((String) tblContent.getValueAt(i, 4))) / (tCount + tLeftCount);
                        rs.close(); // 至此通过取出产品PRICE，COST，Store字段内容，乘以数量，得到了本交易的利润---------

                        int tSeleIndex = cmbSupplier.getSelectedIndex();
                        int tSplierId = tSeleIndex < 0 ? -1 : supplierIDAry[tSeleIndex];

                        sql =
                                "INSERT INTO input(EMPLOYEEID, TIME, PRODUCTID, AMOUNT, TOLTALPRICE, FOLDERID) VALUES ("
                                        .concat(String.valueOf(tSeleIndex)).concat(", '")
                                        .concat(tDate)
                                        .concat("', ")
                                        .concat(String.valueOf(tProdId))
                                        .concat(", ")
                                        .concat(String.valueOf(tCount))
                                        .concat(", ")
                                        // set the count.
                                        .concat(String.valueOf(CASUtility.getPriceByCent(Float
                                                .parseFloat((String) tblContent.getValueAt(i, 4)))))// set the total
                                                                                                    // price.
                                        .concat(", 5201)");
                        smt.executeUpdate(sql); // 至此将本条交易记录入销售表中。

                        sql =
                                "update product set Store = ".concat(String.valueOf(tLeftCount + tCount))
                                        .concat(", cost = ").concat(String.valueOf(CASUtility.getPriceByCent(cost)))
                                        .concat(" where id = ").concat(String.valueOf(tProdId));
                        smt.executeUpdate(sql);
                        smt.close();
                        smt = null; // 至此产品表中的库存量被更改。
                    }
                    dispose();
                } catch (SQLException exp) {
                    ErrorUtil.write(exp);
                }
            }
        } else if (o == cancel)
            dispose();
        else if (o == cmbSupplier) {
            int tRowCount = supplierSubjectAry.length - 1;
            if (cmbSupplier.getSelectedIndex() == tRowCount) {
                // 创建一个空记录，赋予正确的path值，然后再传给对话盒显示。以确保saveContentAction保存后，记录能显示到正确的地方。
                PIMRecord tRec = new PIMRecord();
                tRec.setFieldValue(PIMPool.pool.getKey(ContactDefaultViews.FOLDERID), Integer.valueOf(104));
                ContactDlg tDlg = new ContactDlg(this, new SaveContentsAction(), tRec);
                tDlg.setForOneTimeAddition();
                tDlg.setVisible(true);
                initComponents();
            }
        }
    }

    // Key Listener--------------------------------
    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
        if (o == tfdProdNumber) { // 默认情况下，焦点应该都在本组件里的。
            int tKeyCode = e.getKeyCode();
            if (tKeyCode == 10) { // 货号框中，随时当按回车键，立即开始在库中查询匹配商品。（通常执行不到，因为扫描输入后，会根据位数判断自动执行查询。
                String tProdNumber = tfdProdNumber.getText();
                if (tProdNumber == null || tProdNumber.length() < 1) {// 发现并没有选中什么商品，这种回车和ProdNumberField中的无码回车的意义一样---结算！
                    actionPerformed(new ActionEvent(ok, 0, null));
                    return;
                }

                ok.setText(BarDlgConst.Add);
                ok.setMnemonic('A');
                cancel.setText(DlgConst.FINISH_BUTTON);

                String sql =
                        "select id, subject, COST, unit, CATEGORY,content from product where code = '".concat(
                                tProdNumber).concat("'");
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
                            float tCost = rs.getInt("COST");
                            tfdCost.setText(decimalFormat.format(tCost / 100.0)); // 单价
                            tfdTotlePrice.setText(decimalFormat.format(tCount * tCost / 100)); // 总价
                            tfdPackage.setText(rs.getString("unit"));
                            tfdType.setText(rs.getString("CATEGORY"));
                            tfdNote.setText(rs.getString("content"));
                            tfdCost.grabFocus();
                            break;
                        }
                    } else {
                        MerchandiseDlg tDlg =
                                new MerchandiseDlg(BarFrame.instance, -1, tProdNumber, CASUtility.getPriceByCent(Double
                                        .parseDouble(tfdShoudReceive.getText())), "",
                                        (String) cmbProdName.getSelectedItem(), 0, tfdPackage.getText(),
                                        tfdType.getText(), tfdCost.getText().length() < 1 ? 0
                                                : CASUtility.getPriceByCent(Double.parseDouble(tfdCost.getText())),
                                        tfdNote.getText());
                        tDlg.enableStorCount(false);
                        // 将对话盒上既有的信息添加到merchandiseDlg上。
                        tDlg.setVisible(true);
                        if (tDlg.ADDED) {// 如果添加产品对话盒成功加入了产品
                            prodID = tDlg.getProdID();
                            tfdProdNumber.setText(tDlg.getProdCode());// 则新的产品信息加入到当前对话盒区域，并启动线程。
                            Vector tVec = new Vector();
                            tVec.add(tDlg.getProdName());
                            DefaultComboBoxModel tModel = new DefaultComboBoxModel(tVec);
                            cmbProdName.setModel(tModel);
                            cmbProdName.setSelectedIndex(0);
                            int tCount = 1;
                            try {
                                tCount = Integer.parseInt(tfdCount.getText());
                            } catch (Exception exp) {
                            }
                            float tCost = Float.parseFloat(tDlg.getCost());
                            tfdCost.setText(decimalFormat.format(tCost));
                            tfdTotlePrice.setText(decimalFormat.format(tCount * tCost));
                            tfdPackage.setText(tDlg.getPackage());
                            tfdType.setText(tDlg.getProdType());
                            tfdNote.setText(tDlg.getRemark());
                            tfdCost.grabFocus();
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
                    actionPerformed(new ActionEvent(ok, 0, null));
                    return;
                }
                for (int i = 0, len = prodSubjectAry.length; i < len; i++) {
                    if (prodSubjectAry[i].equals(tProdName)) {
                        prodID = prodIDAry[i];
                        String sql =
                                "select code, Cost, unit, CATEGORY, content from product where id = ".concat(String
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
                                float tCost = rs.getInt("Cost");
                                tfdCost.setText(String.valueOf(tCost / 100)); // 单价
                                tfdTotlePrice.setText(decimalFormat.format(tCount * tCost)); // 总价
                                tfdPackage.setText(rs.getString("unit")); // 包装
                                tfdType.setText(rs.getString("CATEGORY"));
                                tfdNote.setText(rs.getString("content")); // 说明
                                tfdCost.grabFocus();
                                break;
                            }
                            rs.close();// 关闭
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
                tfdProdNumber.grabFocus(); // 跳回之前光标所在的那个组件。
            }
        } else if (o == tfdCost) {
            if (e.getKeyCode() == 10)
                ok.grabFocus();
        } else if (o == tfdTotlePrice) {
            if (e.getKeyCode() == 10)
                ok.grabFocus();
        } else if (o == tfdNote) {
            if (e.getKeyCode() == 10)
                ok.grabFocus();
        } else if (o == ok) {
            if (e.getKeyCode() == 10)
                actionPerformed(new ActionEvent(ok, 0, null));
        } else if (o == cancel) {
            if (e.getKeyCode() == 10)
                actionPerformed(new ActionEvent(cancel, 0, null));
        }
    }

    private void commKeyProcess(
            KeyEvent e) {
        int tKeyCode = e.getKeyCode();
        if (tKeyCode == 27) {// ESC 表示取消对话盒区域内容，取消列表中项目，退出系统
            if (((JTextField) e.getSource()).getText().length() > 0)// 如果条码框或者产品名中有内容，就表示清空本条记录。
                resetDlgArea();
            else
                // 如果是对话盒区域已经是空的了
                actionPerformed(new ActionEvent(cancel, 0, null));
        } else if (tKeyCode == 17) {// 按"ctrl"键使光标跳至count.必须先输入数量再输入产品的道理是，扫描枪有可能会带回车，使你没有机会后敲数量。
            tfdCount.grabFocus();
            tfdCount.selectAll();
        } else if (tKeyCode == 38) {
            tblContent.setSelectedRow(tblContent.getSelectedRow() - 1);
            tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
        } else if (tKeyCode == 40) {
            tblContent.setSelectedRow(tblContent.getSelectedRow() + 1);
            tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
        } else if (tKeyCode == 8 || tKeyCode == 127) {
            // @NOTE:当Del键或者BackSpace键被按时，如果条码框中没有内容，则直接进行list中记录的删除动作。
            if (tfdProdNumber.getText().length() == 0) {
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

    public void keyReleased(
            KeyEvent e) {
        if (BarUtility.isNumber(e.getKeyCode())) {
            Object o = e.getSource();
            if (o == tfdCost) {
                String tValue = tfdCost.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，自动结算
                    tfdTotlePrice.grabFocus();
            } else if (o == tfdTotlePrice) {
                String tValue = tfdTotlePrice.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，自动结算
                    cmbSupplier.grabFocus();
            }
        }
    }

    public void keyTyped(
            KeyEvent e) {
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    public void reLayout() {
        int prmWidth = getWidth();
        int tFieldWidth1 = prmWidth / 2 - CustOpts.HOR_GAP;

        lblProdNumber.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblProdNumber.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdProdNumber.setBounds(lblProdNumber.getX() + lblProdNumber.getWidth(), lblProdNumber.getY(), tFieldWidth1
                - lblProdNumber.getX() - lblProdNumber.getWidth(), CustOpts.BTN_HEIGHT);
        lblCost.setBounds(lblProdNumber.getX(), lblProdNumber.getY() + lblProdNumber.getHeight() + CustOpts.VER_GAP,
                lblProdNumber.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCost.setBounds(lblCost.getX() + lblCost.getWidth(), lblCost.getY(),
                tFieldWidth1 - lblCost.getX() - lblCost.getWidth(), CustOpts.BTN_HEIGHT);
        lblUnit1.setBounds(tfdCost.getX() + tfdCost.getWidth() - lblUnit1.getPreferredSize().width - CustOpts.HOR_GAP,
                tfdCost.getY(), lblUnit1.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblCount.setBounds(lblCost.getX(), lblCost.getY() + lblCost.getHeight() + CustOpts.VER_GAP,
                lblCost.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCount.setBounds(lblCount.getX() + lblCount.getWidth(), lblCount.getY(), tFieldWidth1 - lblCount.getX()
                - lblCount.getWidth(), CustOpts.BTN_HEIGHT);
        lblTotlePrice.setBounds(lblCount.getX(), lblCount.getY() + lblCount.getHeight() + CustOpts.VER_GAP,
                lblTotlePrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdTotlePrice.setBounds(lblTotlePrice.getX() + lblTotlePrice.getWidth(), lblTotlePrice.getY(), tFieldWidth1
                - lblTotlePrice.getX() - lblTotlePrice.getWidth(), CustOpts.BTN_HEIGHT);
        lblUnit2.setBounds(tfdTotlePrice.getX() + tfdTotlePrice.getWidth() - lblUnit2.getPreferredSize().width
                - CustOpts.HOR_GAP, tfdTotlePrice.getY(), lblUnit1.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        lblProdName.setBounds(tfdProdNumber.getX() + tfdProdNumber.getWidth() + CustOpts.HOR_GAP, tfdProdNumber.getY(),
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

        lblNote.setBounds(lblType.getX(), lblType.getY() + lblType.getHeight() + CustOpts.VER_GAP,
                lblNote.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdNote.setBounds(lblNote.getX() + lblNote.getWidth(), lblNote.getY(),
                prmWidth - lblNote.getX() - lblNote.getWidth() - CustOpts.HOR_GAP - CustOpts.SIZE_EDGE * 2,
                CustOpts.BTN_HEIGHT);

        srpContent
                .setBounds(CustOpts.HOR_GAP, tfdNote.getY() + tfdNote.getHeight() + CustOpts.VER_GAP, prmWidth
                        - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2,
                        getHeight() - CustOpts.VER_GAP * 3 - CustOpts.BTN_HEIGHT - CustOpts.SIZE_TITLE
                                - CustOpts.SIZE_EDGE - tfdNote.getY() - tfdNote.getHeight());
        lblShoudReceive.setBounds(srpContent.getX(), srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP,
                lblShoudReceive.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdShoudReceive.setBounds(lblShoudReceive.getX() + lblShoudReceive.getWidth(), lblShoudReceive.getY(), 60,
                CustOpts.BTN_HEIGHT);
        lblUnit3.setBounds(tfdShoudReceive.getX() + tfdShoudReceive.getWidth(), tfdShoudReceive.getY(), 60,
                CustOpts.BTN_HEIGHT);
        lblSupplier.setBounds(lblUnit3.getX() + lblUnit3.getWidth(), lblUnit3.getY(),
                lblSupplier.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cmbSupplier.setBounds(lblSupplier.getX() + lblSupplier.getWidth(), lblSupplier.getY(),
                cmbSupplier.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        ok.setBounds(srpContent.getWidth() + srpContent.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH * 2,
                srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cancel.setBounds(ok.getWidth() + ok.getX() + CustOpts.HOR_GAP, ok.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

        resetColWidth();
    }

    private void initDialog() {
        // 初始化－－－－－－－－－－－－－－－－
        lblProdNumber = new JLabel(BarDlgConst.ProdNumber);
        lblProdName = new JLabel(BarDlgConst.ProdName);
        lblCount = new JLabel(BarDlgConst.Count);
        lblCost = new JLabel(BarDlgConst.Cost);
        lblTotlePrice = new JLabel(BarDlgConst.Subtotal);
        lblNote = new JLabel(BarDlgConst.Note);
        lblPackage = new JLabel(BarDlgConst.Package);
        lblType = new JLabel(BarDlgConst.Type);
        lblSupplier = new JLabel(BarDlgConst.Supplier);

        tfdProdNumber = new JTextField();
        cmbProdName = new JComboBox();
        tfdCount = new JTextField("1");
        tfdCost = new JTextField();
        tfdTotlePrice = new JTextField();
        tfdNote = new JTextField();
        tfdPackage = new JTextField();
        tfdType = new JTextField();
        cmbSupplier = new JComboBox();

        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);

        lblUnit1 = new JLabel(BarDlgConst.Unit);
        lblUnit2 = new JLabel(BarDlgConst.Unit);
        lblUnit3 = new JLabel(BarDlgConst.Unit);
        lblShoudReceive = new JLabel(BarDlgConst.Calculate);
        tfdShoudReceive = new JTextField("0.00");
        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // properties
        setTitle(BarDlgConst.Input);
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
        ok.setMnemonic('O');
        cancel.setMnemonic('C');
        // border----------
        tblContent.setBorder(tfdPackage.getBorder());
        // forcus-------------
        tfdPackage.setFocusable(false);
        tfdType.setFocusable(false);
        tblContent.setFocusable(false);
        tfdShoudReceive.setFocusable(false);
        tfdCount.setNextFocusableComponent(tfdProdNumber);
        tfdCost.setNextFocusableComponent(tfdTotlePrice);
        tfdNote.setNextFocusableComponent(ok);
        cancel.setNextFocusableComponent(tfdCount);

        ok.setMargin(new Insets(0, 0, 0, 0));
        cancel.setMargin(ok.getInsets());

        // 搭建－－－－－－－－－－－－－
        add(lblUnit1);
        add(lblUnit2);
        add(lblProdNumber);
        add(lblProdName);
        add(lblCount);
        add(lblCost);
        add(lblTotlePrice);
        add(lblNote);
        add(lblPackage);
        add(lblType);
        add(lblSupplier);
        add(tfdProdNumber);
        add(cmbProdName);
        add(tfdCount);
        add(tfdCost);
        add(tfdTotlePrice);
        add(tfdNote);
        add(tfdPackage);
        add(tfdType);
        add(cmbSupplier);
        add(srpContent);

        add(lblUnit3);
        add(lblShoudReceive);
        add(tfdShoudReceive);
        add(ok);
        add(cancel);
        // 加监听器－－－－－－－－
        tfdProdNumber.addFocusListener(this);
        tfdCount.addFocusListener(this);
        tfdCost.addFocusListener(this);
        tfdTotlePrice.addFocusListener(this);

        tfdProdNumber.addKeyListener(this);
        cmbProdName.addKeyListener(this);
        tfdCost.addKeyListener(this);
        tfdCount.addKeyListener(this);
        tfdTotlePrice.addKeyListener(this);
        tfdNote.addKeyListener(this);
        ok.addKeyListener(this);
        cancel.addKeyListener(this);
        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）
        getContentPane().addComponentListener(this);

        cmbSupplier.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);

        // initContents--------------
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initComponents();
                initTable();
                tfdCount.grabFocus();
            }
        });
    }

    // 当对话盒区域内容填写完了以后，通过本方法启动一个线程，实现休眠500ms之后将对话盒区域内容增加到表中。
    // 同时更新tfdShoudReceive的内容。
    private void startWaitThread() {
        addContentToList(); // 将对话盒区域内容加入到列表中去。
        shouldReceive += Float.parseFloat(tfdTotlePrice.getText());
        tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
        resetDlgArea(); // 对话和区域内容复位。;
        tfdCount.grabFocus();
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
        tblContent.setValueAt(tfdCost.getText(), tValidRowCount, 3); // set the price.
        tblContent.setValueAt(tfdTotlePrice.getText(), tValidRowCount, 4); // set the total price.
    }

    private void resetColWidth() {
        for (int i = 0, len = header.length; i < len; i++) {
            PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
            tmpCol.setWidth(i == 1 ? getWidth() - 267 : 60);
            tmpCol.setPreferredWidth(i == 1 ? getWidth() - 267 : 60);
        }
        validate();
        tblContent.revalidate();
        invalidate();
    }

    private void initComponents() {
        String sql = "select ID, SUBJECT from product where code = '' and deleted != 'true'";
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

            sql = "select ID, SUBJECT from Contact where deleted != 'true'  and FOLDERID = 104";
            rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            tmpPos = rs.getRow();
            supplierIDAry = new int[tmpPos];
            supplierSubjectAry = new String[tmpPos + 1];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                supplierIDAry[tmpPos] = rs.getInt("ID");
                supplierSubjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            supplierSubjectAry[tmpPos] = BarDlgConst.AddNewSupplier;
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        cmbSupplier.setModel(new DefaultComboBoxModel(supplierSubjectAry));
        cmbSupplier.setSelectedIndex(-1);
    }

    private void initTable() {
        Object[][] tValues = new Object[1][header.length];
        tblContent.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
    }

    private int getUsedRowCount() {
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++)
            if (tblContent.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblContent.getRowCount();
    }

    private void resetDlgArea() {
        tfdProdNumber.setText(""); // 并清空对话盒区域.
        cmbProdName.setModel(new DefaultComboBoxModel(prodSubjectAry));
        cmbProdName.setSelectedIndex(-1);
        tfdCount.setText("1");
        tfdCost.setText("");
        tfdTotlePrice.setText("");
        tfdPackage.setText("");
        tfdType.setText("");
        tfdNote.setText("");
        ok.setText(DlgConst.OK);
        ok.setMnemonic('O');
        cancel.setText(DlgConst.CANCEL);
        cancel.setMnemonic('C');
    }

    JLabel lblProdNumber;
    JLabel lblProdName;
    JLabel lblCount;
    JLabel lblCost;
    JLabel lblTotlePrice;
    JLabel lblNote;
    JLabel lblPackage;
    JLabel lblType;
    JLabel lblSupplier;

    JTextField tfdProdNumber;
    JComboBox cmbProdName;
    JTextField tfdCount;
    JTextField tfdCost;
    JTextField tfdTotlePrice;
    JTextField tfdPackage;
    JTextField tfdType;
    JComboBox cmbSupplier;
    JTextField tfdNote;
    JTextField tfdShoudReceive;

    PIMTable tblContent;
    PIMScrollPane srpContent;
    JLabel lblUnit1;
    JLabel lblUnit2;
    JLabel lblUnit3;
    JLabel lblShoudReceive;

    private JButton ok;
    private JButton cancel;

    public Vector hangupVec = new Vector();
    private int prodID;
    private float shouldReceive;
    private int[] prodIDAry;
    private String[] prodSubjectAry;
    private int[] supplierIDAry;
    private String[] supplierSubjectAry;

    private String[] header = new String[] { BarDlgConst.ProdNumber, BarDlgConst.ProdName, BarDlgConst.Count,
            BarDlgConst.Cost, BarDlgConst.Subtotal };
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
}
