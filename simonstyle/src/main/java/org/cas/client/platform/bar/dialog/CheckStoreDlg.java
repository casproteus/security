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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
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
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.IPIMTableModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.product.ProductDefaultViews;
import org.cas.client.resource.international.DlgConst;

public class CheckStoreDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener,
        FocusListener, MouseListener, DocumentListener {
    final int IDCOLUM = 8;

    public CheckStoreDlg(JFrame pParent) {
        super(pParent, true);
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP
                * 2, getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP * 3
                - CustOpts.BTN_HEIGHT);

        btnClose.setBounds(getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.BTN_WIDTH,
                srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        btnUnFocus.setBounds(btnClose.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, btnClose.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        btnFocus.setBounds(btnUnFocus.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, btnUnFocus.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

        lblProdCode.setBounds(CustOpts.HOR_GAP, btnFocus.getY(), lblProdCode.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdProdCode.setBounds(lblProdCode.getX() + lblProdCode.getWidth(), lblProdCode.getY(), CustOpts.BTN_WIDTH * 2,
                CustOpts.BTN_HEIGHT);
        btnDelete.setBounds(tfdProdCode.getX() + tfdProdCode.getWidth() + CustOpts.HOR_GAP, btnClose.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

        IPIMTableColumnModel tTCM = tblContent.getColumnModel();
        tTCM.getColumn(0).setPreferredWidth(90);
        tTCM.getColumn(1).setPreferredWidth(140);
        tTCM.getColumn(2).setPreferredWidth(50);
        tTCM.getColumn(3).setPreferredWidth(30);
        tTCM.getColumn(4).setPreferredWidth(30);
        tTCM.getColumn(5).setPreferredWidth(50);
        tTCM.getColumn(6).setPreferredWidth(70);
        tTCM.getColumn(7).setPreferredWidth(srpContent.getWidth() - 460 - 3);

        validate();
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
        btnClose.removeActionListener(this);
        btnDelete.removeActionListener(this);
        btnFocus.removeActionListener(this);
        btnUnFocus.removeActionListener(this);
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
    public void keyTyped(
            KeyEvent e) {
    }

    @Override
    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
        if (o == tfdProdCode) { // 默认情况下，焦点应该都在本组件里的。
            int tKeyCode = e.getKeyCode();
            if (tKeyCode == 10) // 货号框中，随时当按回车键，立即开始在库中查询匹配商品。（通常执行不到，因为扫描输入后，会根据位数判断自动执行查询。
                if (syncScroll(tfdProdCode.getText()) == -1) {// 没有匹配，则提示增加新产品
                    String tPordNumber = tfdProdCode.getText();
                    if (tPordNumber.length() > 0) {
                        MerchandiseDlg tDlg = new MerchandiseDlg(BarFrame.instance, tPordNumber);
                        tDlg.setVisible(true);
                        if (tDlg.ADDED) {// 如果添加产品对话盒成功加入了产品
                            initTable();
                            reLayout();
                            tblContent.scrollToRect(tblContent.getRowCount() - 1, 0);
                        }
                    }
                }
        }
    }

    @Override
    public void keyReleased(
            KeyEvent e) {
    }

    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnClose)
            dispose();
        if (o == btnDelete) {
            if (JOptionPane.showConfirmDialog(this, DlgConst.COMFIRMDELETEACTION, DlgConst.DlgTitle,
                    JOptionPane.YES_NO_OPTION) != 0)// 确定删除吗？
                return;
            int tSeleRowID = tblContent.getSelectedRecordID();
            int tSeleRow = tblContent.getSelectedRow();
            String sql =
                    "delete from Product where ID = '".concat(
                            ((Integer) tblContent.getValueAt(tSeleRow, IDCOLUM)).toString()).concat("'");
            try {
                Connection conn = PIMDBModel.getConection();
                Statement smt = conn.createStatement();
                smt.executeUpdate(sql.toString());
                smt.close();
                smt = null;

                initTable();
                reLayout();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        } else if (o == btnFocus) {
            int[] tRowAry = tblContent.getSelectedRows();
            if (tRowAry.length < 2) {
                JOptionPane.showMessageDialog(this, BarDlgConst.ValidateFucusAction);// 选中项目太少！请先用鼠标选中多条记录，然后点击聚焦选中按钮，重点对选中的记录进行观察。
                return;
            }
            Object[][] tValues = new Object[tRowAry.length][tblContent.getColumnCount()];
            for (int i = 0; i < tRowAry.length; i++)
                for (int j = 0, len = tblContent.getColumnCount(); j < len; j++)
                    tValues[i][j] = tblContent.getValueAt(tRowAry[i], j);
            // 必须重新new一个Table，否则PIM会在绘制的时候报数组越界错误。原因不明。
            getContentPane().remove(srpContent);
            tblContent = new PIMTable();// 显示字段的表格,设置模型
            srpContent = new PIMScrollPane(tblContent);

            tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tblContent.setAutoscrolls(true);
            tblContent.setRowHeight(20);
            tblContent.setBorder(new JTextField().getBorder());
            tblContent.setFocusable(false);

            getContentPane().add(srpContent);
            tblContent.addMouseListener(this);

            tblContent.setDataVector(tValues, header);
            DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
            tCellRender.setOpaque(true);
            tCellRender.setBackground(Color.LIGHT_GRAY);
            tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
            reLayout();
        } else if (o == btnUnFocus) {// 如果点击了Unfocus按钮，则取消Focus动作，返回到显示全部内容的状态。
            initTable();
            reLayout();
        }
    }

    @Override
    public void mouseClicked(
            MouseEvent e) {
        if (e.getClickCount() > 1) {
            if (Integer.parseInt(CustOpts.custOps.getUserType()) > 0) {// 如果当前登陆用户是个普通员工，则显示普通登陆对话盒。等待再次登陆
                new LoginDlg(BarFrame.instance).setVisible(true);// 结果不会被保存到ini
                if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
                    if (LoginDlg.USERTYPE >= 2) {// 进一步判断，如果新登陆是经理，弹出对话盒
                        int tRow = tblContent.getSelectedRow();
                        MerchandiseDlg tDlg =
                                new MerchandiseDlg(
                                        BarFrame.instance,
                                        ((Integer) tblContent.getValueAt(tRow, IDCOLUM)).intValue(),
                                        (String) tblContent.getValueAt(tRow, 0),
                                        CASUtility.getPriceByCent(((Float) tblContent.getValueAt(tRow, 5)).floatValue()),
                                        (String) tblContent.getValueAt(tRow, 2), (String) tblContent
                                                .getValueAt(tRow, 1), ((Integer) tblContent.getValueAt(tRow, 3))
                                                .intValue(), (String) tblContent.getValueAt(tRow, 4),
                                        (String) tblContent.getValueAt(tRow, 6), ((Integer) tblContent.getValueAt(tRow,
                                                9)).intValue(), (String) tblContent.getValueAt(tRow, 7));
                        tDlg.setVisible(true);
                        if (tDlg.ADDED) {
                            initTable();
                            reLayout();
                        }
                    }
                }
            } else {
                int tRow = tblContent.getSelectedRow();
                MerchandiseDlg tDlg =
                        new MerchandiseDlg(BarFrame.instance,
                                ((Integer) tblContent.getValueAt(tRow, IDCOLUM)).intValue(),
                                (String) tblContent.getValueAt(tRow, 0), CASUtility.getPriceByCent(((Float) tblContent
                                        .getValueAt(tRow, 5)).floatValue()), (String) tblContent.getValueAt(tRow, 2),
                                (String) tblContent.getValueAt(tRow, 1),
                                ((Integer) tblContent.getValueAt(tRow, 3)).intValue(), (String) tblContent.getValueAt(
                                        tRow, 4), (String) tblContent.getValueAt(tRow, 6),
                                ((Integer) tblContent.getValueAt(tRow, 9)).intValue(), (String) tblContent.getValueAt(
                                        tRow, 7));
                tDlg.setVisible(true);
                if (tDlg.ADDED) {
                    initTable();
                    reLayout();
                }
            }

        }
    }

    @Override
    public void mousePressed(
            MouseEvent e) {
    }

    @Override
    public void mouseReleased(
            MouseEvent e) {
    }

    @Override
    public void mouseEntered(
            MouseEvent e) {
    }

    @Override
    public void mouseExited(
            MouseEvent e) {
    }

    @Override
    public Container getContainer() {
        return getContentPane();
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

    @Override
    public void insertUpdate(
            DocumentEvent e) {
        syncScroll(tfdProdCode.getText());
    }

    @Override
    public void removeUpdate(
            DocumentEvent e) {
        syncScroll(tfdProdCode.getText());
    }

    @Override
    public void changedUpdate(
            DocumentEvent e) {
        syncScroll(tfdProdCode.getText());
    }

    private int syncScroll(
            String pContent) {
        IPIMTableModel tModel = tblContent.getModel();
        int maxRow = tModel.getRowCount();
        int tCurrntRow = tblContent.getSelectedRow();
        for (int i = 0; i < maxRow; i++) {
            if (((String) tModel.getValueAt(i, 0)).startsWith(pContent) && tCurrntRow != i) {
                tblContent.scrollToRect(i, 0);
                tblContent.setSelectedRow(i);
                return i;
            }
        }
        return -1;
    }

    private void initDialog() {
        setTitle(BarDlgConst.Check);

        // 初始化－－－－－－－－－－－－－－－－
        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);
        btnClose = new JButton(DlgConst.FINISH_BUTTON);
        btnDelete = new JButton(DlgConst.DELETE);
        btnFocus = new JButton(BarDlgConst.Focus);
        btnUnFocus = new JButton(BarDlgConst.UnFocus);
        lblProdCode = new JLabel(BarDlgConst.ProdNumber);
        tfdProdCode = new JTextField();
        // properties
        btnClose.setMnemonic('O');
        btnDelete.setMnemonic('D');
        btnFocus.setMnemonic('F');
        btnUnFocus.setMnemonic('U');

        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnDelete.setMargin(btnClose.getMargin());
        btnFocus.setMargin(btnClose.getMargin());
        btnUnFocus.setMargin(btnFocus.getMargin());

        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(20);
        tblContent.setBorder(new JTextField().getBorder());
        tblContent.setFocusable(false);

        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);

        // getRootPane().setDefaultButton(btnClose);

        // 布局---------------
        setBounds((CustOpts.SCRWIDTH - 700) / 2, (CustOpts.SCRHEIGHT - 500) / 2, 700, 500); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(srpContent);
        getContentPane().add(btnClose);
        getContentPane().add(btnFocus);
        getContentPane().add(btnDelete);
        getContentPane().add(btnUnFocus);
        getContentPane().add(lblProdCode);
        getContentPane().add(tfdProdCode);
        // 加监听器－－－－－－－－
        tfdProdCode.addFocusListener(this);
        btnClose.addActionListener(this);
        btnDelete.addActionListener(this);
        btnFocus.addActionListener(this);
        btnUnFocus.addActionListener(this);

        tfdProdCode.addKeyListener(this);
        tblContent.addMouseListener(this);
        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        tfdProdCode.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）

        getContentPane().addComponentListener(this);
        // initContents--------------
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initTable();
            }
        });
    }

    private void initTable() {
        Object[][] tValues = null;
        String sql =
                "select ID, CODE, SUBJECT, MNEMONIC, STORE, UNIT, PRICE, CATEGORY, CONTENT, COST from Product where DELETED != true";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            tValues = new Object[tmpPos][header.length + 2];
            rs.beforeFirst();
            tmpPos = 0;
            while (rs.next()) {
                tValues[tmpPos][0] = rs.getString("CODE");
                tValues[tmpPos][1] = rs.getString("SUBJECT");
                tValues[tmpPos][2] = rs.getString("MNEMONIC");
                tValues[tmpPos][3] = Integer.valueOf(rs.getInt("STORE"));
                tValues[tmpPos][4] = rs.getString("UNIT");
                tValues[tmpPos][5] = Float.valueOf((float) (rs.getInt("PRICE") / 100.0));
                tValues[tmpPos][6] = rs.getString("CATEGORY");
                tValues[tmpPos][7] = rs.getString("CONTENT");
                tValues[tmpPos][IDCOLUM] = rs.getInt("ID");
                tValues[tmpPos][9] = rs.getInt("COST");// 不显示，所以不用转换为以元为单位。
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        tblContent.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
    }

    private String[] header = new String[] { ProductDefaultViews.TEXTS[5], // "条码"
            ProductDefaultViews.TEXTS[3], // "品名"
            ProductDefaultViews.TEXTS[11], // "助记"
            ProductDefaultViews.TEXTS[13], // "库存"
            ProductDefaultViews.TEXTS[6], // "单位"
            ProductDefaultViews.TEXTS[7], // "售价"
            ProductDefaultViews.TEXTS[10], // "类别"
            ProductDefaultViews.TEXTS[4], // "类别"
            ProductDefaultViews.TEXTS[0], // "ID"
            ProductDefaultViews.TEXTS[12] }; // "成本"

    PIMTable tblContent;
    PIMScrollPane srpContent;
    private JLabel lblProdCode;
    private JTextField tfdProdCode;
    private JButton btnFocus;
    private JButton btnUnFocus;
    private JButton btnDelete;
    private JButton btnClose;
}
