package org.cas.client.platform.bar.dialog;

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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.resource.international.DlgConst;

public class HangupDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener {
    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public HangupDlg(JFrame pParent) {
        super(pParent, true);
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    public void reLayout() {
        srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP
                * 2, getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP * 3
                - CustOpts.BTN_HEIGHT);
        ok.setBounds(getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.BTN_WIDTH, srpContent.getY() + srpContent.getHeight()
                + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭

        for (int i = 0, len = header.length; i < len; i++) {
            PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
            tmpCol.setWidth(i == 0 ? 50 : (getWidth() - 80) / 4);
            tmpCol.setPreferredWidth(i == 0 ? 50 : (getWidth() - 80) / 4);
        }
        tblContent.validate();
        tblContent.revalidate();
        tblContent.invalidate();
        validate();
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
        ok.removeActionListener(this);
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    public void keyPressed(
            KeyEvent e) {
        if (e.getSource() == ok) {
            if (e.getKeyCode() == 38)
                tblContent.setSelectedRow(tblContent.getSelectedRow() - 1);
            else if (e.getKeyCode() == 40)
                tblContent.setSelectedRow(tblContent.getSelectedRow() + 1);
        }
    }

    public void keyReleased(
            KeyEvent e) {
    }

    public void keyTyped(
            KeyEvent e) {
    }

    /** Invoked when the component's size changes. */
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

    /**
     * Invoked when an action occurs. NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
     * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
     * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        int tSelectedRow = tblContent.getSelectedRow();
        if (tSelectedRow < tblContent.getRowCount() && tSelectedRow >= 0) {
            BarFrame.instance.general.cancelHangup(tSelectedRow);
        }
        dispose();
    }

    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarDlgConst.Hangup);

        // 初始化－－－－－－－－－－－－－－－－
        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);
        ok = new JButton(DlgConst.OK);

        // properties
        ok.setMnemonic('o');
        ok.setMargin(new Insets(0, 0, 0, 0));

        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(20);
        tblContent.setBorder(new JTextField().getBorder());
        tblContent.setFocusable(false);

        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);

        getRootPane().setDefaultButton(ok);

        // 布局---------------
        setBounds((CustOpts.SCRWIDTH - 400) / 2, (CustOpts.SCRHEIGHT - 260) / 2, 400, 260); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(srpContent);
        getContentPane().add(ok);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        ok.addKeyListener(this);
        getContentPane().addComponentListener(this);
        // initContents--------------
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initTable();
            }
        });
    }

    private void initTable() {
        Vector tHangupVec = BarFrame.instance.general.hangupVec;
        Object[][] tValues = new Object[tHangupVec.size()][header.length];
        for (int i = 0, len = tHangupVec.size(); i < len; i++) {
            Object[][] tValueHangedup = (Object[][]) tHangupVec.get(i);
            for (int j = 0; j < header.length; j++)
                tValues[i][j] =
                        j == 0 ? BarDlgConst.Customer.concat(String.valueOf(i + 1)) : (tValueHangedup.length < j ? null
                                : tValueHangedup[j - 1][1]);
        }
        tblContent.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
        // Object[][] tValues = null;
        // String sql =
        // "select CODE, SUBJECT, MNEMONIC, STORE, UNIT, PRICE, CATEGORY, CONTENT from Product where DELETED != 'true'";
        // try{
        // ResultSet rs = PIMDBModel.getConection().createStatement(
        // ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
        // rs.afterLast();
        // rs.relative(-1);
        // int tmpPos = rs.getRow();
        // tValues = new Object[tmpPos][header.length];
        // rs.beforeFirst();
        // tmpPos = 0;
        // while (rs.next()){
        // tValues[tmpPos][0] = rs.getString("CODE");
        // tValues[tmpPos][1] = rs.getString("SUBJECT");
        // tValues[tmpPos][2] = rs.getString("MNEMONIC");
        // tValues[tmpPos][3] = Integer.valueOf(rs.getInt("STORE"));
        // tValues[tmpPos][4] = rs.getString("UNIT");
        // tValues[tmpPos][5] = Float.valueOf((float)(rs.getInt("PRICE")/100.0));
        // tValues[tmpPos][6] = rs.getString("CATEGORY");
        // tValues[tmpPos][7] = rs.getString("CONTENT");
        // tmpPos++;
        // }
        // rs.close();//关闭
        // }catch(SQLException e){
        // ErrorUtil.write(e);
        // }
        //
        // tblContent.setDataVector(tValues, header);
        // DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        // tCellRender.setOpaque(true);
        // tCellRender.setBackground(Color.LIGHT_GRAY);
        // tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
    }

    private String[] header = new String[] { " ", BarDlgConst.Product.concat("1"), BarDlgConst.Product.concat("2"),
            BarDlgConst.Product.concat("3"), BarDlgConst.Product.concat("4") };
    PIMTable tblContent;
    PIMScrollPane srpContent;
    private JButton ok;
}
