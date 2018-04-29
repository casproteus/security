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

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.cascontrol.menuaction.UpdateContactAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.employee.EmployeeDefaultViews;
import org.cas.client.platform.employee.dialog.EmployeeDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pos.dialog.PosDlgConst;
import org.cas.client.platform.pos.dialog.PosFrame;
import org.cas.client.resource.international.DlgConst;

public class EmployeeListDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener,
        MouseListener {
    final int IDCOLUM = 19;

    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public EmployeeListDlg(JFrame pParent) {
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
        btnUnFocus.setBounds(btnClose.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, btnClose.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        btnFocus.setBounds(btnUnFocus.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, btnUnFocus.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        btnAdd.setBounds(srpContent.getX(), btnClose.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        btnDelete.setBounds(btnAdd.getX() + btnAdd.getWidth() + CustOpts.HOR_GAP, btnAdd.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

        IPIMTableColumnModel tTCM = tblContent.getColumnModel();
        tTCM.getColumn(0).setPreferredWidth(80);
        tTCM.getColumn(1).setPreferredWidth(120);
        tTCM.getColumn(2).setPreferredWidth(40);
        tTCM.getColumn(3).setPreferredWidth(50);
        tTCM.getColumn(4).setPreferredWidth(90);
        tTCM.getColumn(5).setPreferredWidth(60);
        tTCM.getColumn(6).setPreferredWidth(160);
        tTCM.getColumn(7).setPreferredWidth(80);
        tTCM.getColumn(8).setPreferredWidth(120);
        tTCM.getColumn(9).setPreferredWidth(120);
        tTCM.getColumn(10).setPreferredWidth(60);
        tTCM.getColumn(11).setPreferredWidth(70);
        tTCM.getColumn(12).setPreferredWidth(50);
        tTCM.getColumn(13).setPreferredWidth(100);
        tTCM.getColumn(14).setPreferredWidth(100);
        tTCM.getColumn(15).setPreferredWidth(140);
        tTCM.getColumn(16).setPreferredWidth(100);
        tTCM.getColumn(17).setPreferredWidth(100);
        tTCM.getColumn(18).setPreferredWidth(180);
        tTCM.getColumn(19).setPreferredWidth(40);

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
        btnFocus.removeActionListener(this);
        btnFocus.removeActionListener(this);
        btnAdd.removeActionListener(this);
        btnDelete.removeActionListener(this);
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
        } else if (o == btnFocus) {
            int[] tRowAry = tblContent.getSelectedRows();
            if (tRowAry.length < 2) {
                JOptionPane.showMessageDialog(this, PosDlgConst.ValidateFucusAction);// 选中项目太少！请先用鼠标选中多条记录，然后点击聚焦选中按钮，重点对选中的记录进行观察。
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

            tblContent.setDataVector(tValues, header);
            DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
            tCellRender.setOpaque(true);
            tCellRender.setBackground(Color.LIGHT_GRAY);
            tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
            reLayout();
        } else if (o == btnUnFocus) {// 如果点击了Unfocus按钮，则取消Focus动作，返回到显示全部内容的状态。
            initTable();
            reLayout();
        } else if (o == btnAdd) {
            // 创建一个空记录，赋予正确的path值，然后再传给对话盒显示。以确保saveContentAction保存后，记录能显示到正确的地方。
            PIMRecord tRec = new PIMRecord();
            tRec.setFieldValue(PIMPool.pool.getKey(EmployeeDefaultViews.FOLDERID), Integer.valueOf(5002));
            EmployeeDlg tDlg = new EmployeeDlg(this, new SaveContentsAction(), tRec);
            tDlg.setForOneTimeAddition();
            tDlg.setVisible(true);
            initTable();
            reLayout();
        } else if (o == btnDelete) {
            if (JOptionPane.showConfirmDialog(this, DlgConst.COMFIRMDELETEACTION, DlgConst.DlgTitle,
                    JOptionPane.YES_NO_OPTION) != 0)// 确定删除吗？
                return;
            int tSeleRow = tblContent.getSelectedRow();
            String sql =
                    "delete from Employee where ID = '".concat(
                            ((Integer) tblContent.getValueAt(tSeleRow, IDCOLUM)).toString()).concat("'");
            try {
                Statement smt = PIMDBModel.getStatement();
                smt.executeUpdate(sql.toString());
                smt.close();
                smt = null;

                initTable();
                reLayout();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
    }

    @Override
    public void mouseClicked(
            MouseEvent e) {
        if (e.getClickCount() > 1) {
            if (Integer.parseInt(CustOpts.custOps.getUserType()) > 0) {// 如果当前登陆用户是个普通员工，则显示普通登陆对话盒。等待再次登陆
                new LoginDlg(PosFrame.instance).setVisible(true);// 结果不会被保存到ini
                if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
                    if (LoginDlg.USERTYPE >= 2) {// 进一步判断，如果新登陆是经理，弹出对话盒
                        int tRow = tblContent.getSelectedRow();
                        PIMRecord tRec =
                                CASControl.ctrl.getModel().selectRecord(
                                        CustOpts.custOps.APPNameVec.indexOf("Employee"),
                                        ((Integer) tblContent.getValueAt(tRow, IDCOLUM)).intValue(), 5002); // to select
                                                                                                            // a record
                                                                                                            // from DB.
                        // 不合适重用OpenAction。因为OpenAction的结果是调用View系统的更新机制。而这里需要的是更新list对话盒。
                        new EmployeeDlg(this, new UpdateContactAction(), tRec).setVisible(true);
                        initTable();
                        reLayout();
                    }
                }
            } else {
                int tRow = tblContent.getSelectedRow();
                PIMRecord tRec =
                        CASControl.ctrl.getModel().selectRecord(CustOpts.custOps.APPNameVec.indexOf("Employee"),
                                ((Integer) tblContent.getValueAt(tRow, IDCOLUM)).intValue(), 5002); // to select a
                                                                                                    // record from DB.
                // 不合适重用OpenAction。因为OpenAction的结果是调用View系统的更新机制。而这里需要的是更新list对话盒。
                new EmployeeDlg(this, new UpdateContactAction(), tRec).setVisible(true);
                initTable();
                reLayout();
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

    private void initDialog() {
        setTitle(PosDlgConst.EmployeeInfo);

        // 初始化－－－－－－－－－－－－－－－－
        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);
        btnClose = new JButton(DlgConst.FINISH_BUTTON);
        btnFocus = new JButton(PosDlgConst.Focus);
        btnUnFocus = new JButton(PosDlgConst.UnFocus);
        btnAdd = new JButton(PosDlgConst.Add);
        btnDelete = new JButton(DlgConst.DELETE);

        // properties
        btnClose.setMnemonic('o');
        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnFocus.setMnemonic('F');
        btnFocus.setMargin(btnClose.getMargin());
        btnUnFocus.setMnemonic('U');
        btnUnFocus.setMargin(btnClose.getMargin());
        btnAdd.setMnemonic('A');
        btnAdd.setMargin(btnClose.getMargin());
        btnDelete.setMnemonic('D');
        btnDelete.setMargin(btnClose.getMargin());

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
        getContentPane().add(btnFocus);
        getContentPane().add(btnUnFocus);
        getContentPane().add(btnAdd);
        getContentPane().add(btnDelete);

        // 加监听器－－－－－－－－
        btnClose.addActionListener(this);
        btnFocus.addActionListener(this);
        btnFocus.addActionListener(this);
        btnAdd.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClose.addKeyListener(this);
        btnFocus.addKeyListener(this);
        btnUnFocus.addKeyListener(this);
        btnAdd.addKeyListener(this);
        btnDelete.addKeyListener(this);
        tblContent.addMouseListener(this);
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
                "select NNAME,SUBJECT,SEX,TITLE,CPHONE,PHONE,ADDRESS,CNUMBER,EMAIL,WEBPAGE,CATEGORY,JOINTIME, SALARY, INSURANCE, SSCNUMBER,IDCARD,BIRTHDAY,BANKNUMBER,CONTENT,ID from employee where DELETED != true";

        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            tValues = new Object[tmpPos][header.length];
            rs.beforeFirst();
            tmpPos = 0;
            while (rs.next()) {
                tValues[tmpPos][0] = rs.getString("NNAME");
                tValues[tmpPos][1] = rs.getString("SUBJECT");
                tValues[tmpPos][2] = rs.getString("SEX");
                tValues[tmpPos][3] = rs.getString("TITLE");
                tValues[tmpPos][4] = rs.getString("CPHONE");
                tValues[tmpPos][5] = rs.getString("PHONE");
                tValues[tmpPos][6] = rs.getString("ADDRESS");
                tValues[tmpPos][7] = rs.getString("CNUMBER");
                tValues[tmpPos][8] = rs.getString("EMAIL");
                tValues[tmpPos][9] = rs.getString("WEBPAGE");
                tValues[tmpPos][10] = rs.getString("CATEGORY");
                tValues[tmpPos][11] = rs.getDate("JOINTIME");
                tValues[tmpPos][12] = Integer.valueOf(rs.getInt("SALARY"));
                tValues[tmpPos][13] = rs.getString("INSURANCE");
                tValues[tmpPos][14] = rs.getString("SSCNUMBER");
                tValues[tmpPos][15] = rs.getString("IDCARD");
                tValues[tmpPos][16] = rs.getDate("BIRTHDAY");
                tValues[tmpPos][17] = rs.getString("BANKNUMBER");
                tValues[tmpPos][18] = rs.getString("CONTENT");
                tValues[tmpPos][IDCOLUM] = rs.getInt("ID");
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

    private String[] header = new String[] { PosDlgConst.NickName, // "昵称"
            PosDlgConst.Name, // "显示为"
            PosDlgConst.Sex, // "性别";
            PosDlgConst.JobTitle, // "职位"
            PosDlgConst.Cellphone, // "手机"
            PosDlgConst.PhoneNum, // "宅电"
            PosDlgConst.HomeAddress,// "家庭住址";
            PosDlgConst.QQ, // "即时通讯号码"
            PosDlgConst.MailAddress,// "电子邮件地址"
            PosDlgConst.MainPage,// "主页";
            PosDlgConst.Type,// 类别
            PosDlgConst.JoinTime,// "进单位时间";
            PosDlgConst.Salary,// "工资";
            PosDlgConst.INSURANCE,// "保险";
            PosDlgConst.SSCNUMBER,// "社保号码";
            PosDlgConst.IDCARD,// "身份证";
            PosDlgConst.BIRTHDAY,// "生日";
            PosDlgConst.BANKNUMBER,// "银行卡号";
            PosDlgConst.Note, ContactDefaultViews.TEXTS[0] }; // "备注"

    PIMTable tblContent;
    PIMScrollPane srpContent;
    private JButton btnAdd;
    private JButton btnDelete;
    private JButton btnFocus;
    private JButton btnUnFocus;
    private JButton btnClose;
}
