package org.cas.client.platform.bar.dialog.statistics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.DlgConst;

public class AddStoreListDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener {
    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public AddStoreListDlg(JFrame pParent) {
        super(pParent, true);
        initDialog();
    }

    @Override
    public void keyTyped(
            KeyEvent e) {
    }

    @Override
    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
        switch (e.getKeyCode()) {
            case 37:
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn() - 1);
                break;
            case 38:
                tblContent.setSelectedRow(tblContent.getSelectedRow() - 1);
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
                break;
            case 39:
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn() + 1);
                break;
            case 40:
                tblContent.setSelectedRow(tblContent.getSelectedRow() + 1);
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
                break;
        }
    }

    @Override
    public void keyReleased(
            KeyEvent e) {
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

        IPIMTableColumnModel tTCM = tblContent.getColumnModel();
        tTCM.getColumn(0).setPreferredWidth(70);
        tTCM.getColumn(1).setPreferredWidth(130);
        tTCM.getColumn(2).setPreferredWidth(130);
        tTCM.getColumn(3).setPreferredWidth(60);
        tTCM.getColumn(4).setPreferredWidth(srpContent.getWidth() - 390 - 4);

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
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnClose) {
            dispose();
        }
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarFrame.consts.InputRecs());

        // 初始化－－－－－－－－－－－－－－－－
        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);

        btnClose = new JButton(DlgConst.FINISH_BUTTON);

        // properties
        btnClose.setMnemonic('o');
        btnClose.setMargin(new Insets(0, 0, 0, 0));

        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(20);
        tblContent.setBorder(new JTextField().getBorder());
        tblContent.setFocusable(false);

        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        getRootPane().setDefaultButton(btnClose);

        // 布局---------------
        setBounds((CustOpts.SCRWIDTH - 540) / 2, (CustOpts.SCRHEIGHT - 300) / 2, 540, 300); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(srpContent);
        getContentPane().add(btnClose);

        // 加监听器－－－－－－－－
        btnClose.addActionListener(this);
        btnClose.addKeyListener(this);
        getContentPane().addComponentListener(this);
        // initContents--------------
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initPordAndSuplier();
                initTable();
            }
        });
    }

    private void initTable() {
        Object[][] tValues = null;
        String sql = "select EMPLOYEEID, TIME, PRODUCTID, AMOUNT, TOLTALPRICE from input where DELETED != true";

        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            tValues = new Object[tmpPos][header.length];
            rs.beforeFirst();
            tmpPos = 0;
            while (rs.next()) {
                int tIdx = CASUtility.getIndexInAry(supplierIdAry, rs.getInt("EMPLOYEEID"));
                tValues[tmpPos][0] = tIdx >= 0 ? supplierNameAry[tIdx] : "";
                tValues[tmpPos][1] = rs.getString("TIME");
                tValues[tmpPos][2] = prodNameAry[CASUtility.getIndexInAry(prodIdAry, rs.getInt("PRODUCTID"))];
                tValues[tmpPos][3] = Integer.valueOf(rs.getInt("AMOUNT"));
                tValues[tmpPos][4] = Float.valueOf((float) (rs.getInt("TOLTALPRICE") / 100.0));
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

    private void initPordAndSuplier() {
        String sql = "select ID, Subject from Contact";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            supplierIdAry = new int[tmpPos + 1];
            supplierNameAry = new String[tmpPos + 1];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                supplierIdAry[tmpPos] = rs.getInt("ID");
                supplierNameAry[tmpPos] = rs.getString("Subject");
                tmpPos++;
            }
            supplierIdAry[tmpPos] = -1;
            supplierNameAry[tmpPos] = "-";
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        // for the service.
        sql = "select ID, SUBJECT from Product";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            prodIdAry = new int[tmpPos];
            prodNameAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                prodIdAry[tmpPos] = rs.getInt("ID");
                prodNameAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
    }

    private String[] header = new String[] { BarFrame.consts.Supplier(), // "供货商"
            BarFrame.consts.TIME(), // "时间"
            BarFrame.consts.Product(), // "产品"
            BarFrame.consts.Count(), // "数量"
            BarFrame.consts.Receive() }; // "收银"

    int[] supplierIdAry;
    String[] supplierNameAry;
    int[] prodIdAry;
    String[] prodNameAry;
    PIMTable tblContent;
    PIMScrollPane srpContent;
    private JButton btnClose;
}
