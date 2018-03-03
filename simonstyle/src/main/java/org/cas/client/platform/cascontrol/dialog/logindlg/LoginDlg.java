package org.cas.client.platform.cascontrol.dialog.logindlg;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;

//@NOTE：如果参数为null，则登陆成功后用户名和等级将被存入ini文件。否则不会被存入。
public class LoginDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {
    public static boolean PASSED;
    public static String USERNAME;
    public static String USERTYPE;

    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public LoginDlg(JFrame pParent) {
        super(pParent, true);
        parent = pParent;
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    public void reLayout() {
        cancel.setBounds(getContainer().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getContainer().getHeight()
                - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);// 关闭
        general.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getContainer().getWidth() - 2 * CustOpts.HOR_GAP,
                (ok.getY()) - 2 * CustOpts.VER_GAP);
        general.componentResized(null);
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
        cancel.removeActionListener(this);
        if (general != null) {
            general.removeAll();
            general = null;
        }
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    /** Invoked when the component's position changes. */
    public void componentMoved(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made visible. */
    public void componentShown(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made invisible. */
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
        Object o = e.getSource();
        if (o == ok) {
            Object tUserName = general.cmbUserName.getSelectedItem();
            String tPassword = general.pfdPassword.getText();
            // 先判断密码是否是超级密码：
            String sql = "select type from useridentity where ID < 6";
            try {
                ResultSet rs =
                        PIMDBModel.getConection()
                                .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                .executeQuery(sql);
                ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

                rs.afterLast();
                rs.relative(-1);
                int tmpPos = rs.getRow();
                if (tmpPos < 6)
                    ErrorUtil.write("Data base was destroyed by some body.");
                String[] tSuperKey = new String[tmpPos];
                rs.beforeFirst();
                tmpPos = 0;
                while (rs.next()) {
                    tSuperKey[tmpPos] = rs.getString("type");
                    tmpPos++;
                }
                rs.close();// 关闭
                if (tSuperKey[0].concat(tSuperKey[1]).concat(tSuperKey[3]).concat(tSuperKey[5]).equals(tPassword)) {
                    PASSED = true;
                    USERTYPE = "-1";// 无论姓名选的是什么，只要密码和超级密码相符，则级别就是经理人（或者更高）。
                    USERNAME = "System";// 无论姓名选的是什么，只要密码和超级密码相符，则级别就是经理人（或者更高）。
                    setVisible(false);
                    return;
                }
            } catch (SQLException exp) {
                ErrorUtil.write(exp);
            }
            // 以下为密码不是超级密码的情况：
            for (int i = 0, len = general.subjectAry.length; i < len; i++) { // 遍历所有的用户名，
                if (general.subjectAry[i].equals(tUserName)) { // 找到用户名的匹配项，
                    if (!general.passwordAry[i].equals(tPassword)) { // 查验密码是否吻合。
                        JOptionPane.showMessageDialog(this, DlgConst.UserPasswordNotCorrect);
                        general.pfdPassword.requestFocus();
                        general.pfdPassword.selectAll();
                        return; // 不吻合就推出，等待用户重新输入密码。
                    }
                    PASSED = true; // 吻合就标记通过。
                    USERTYPE = String.valueOf(general.typeAry[i]); // 并标记下用户的级别
                    USERNAME = tUserName.toString();
                    break;
                }
            }
            setVisible(false);
            if (parent == null) { // parent为null说明是系统刚刚启动时候的登陆操作，将用户名和级别记载到ini中。
                CustOpts.custOps.setUserName(tUserName.toString()); // 用户名将被用来作为下一次,作为默认的选中项。
                CustOpts.custOps.setUserType(USERTYPE); // 级别将被用来作为子功能限制的依据。
            }
        } else if (o == cancel) {
            dispose();
        }
    }

    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(LoginDlgConst.LoginDlgTitle);
        setResizable(false);

        // 初始化－－－－－－－－－－－－－－－－
        general = new LoginGeneralPanel();
        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // 属性设置－－－－－－－－－－－－－－
        // ok.setFont(CustOpts.custOps.getFontOfDefault());
        ok.setFocusable(false);
        cancel.setFocusable(false);
        ok.setMnemonic('o');
        cancel.setMnemonic('c');
        ok.setMargin(new Insets(0, 0, 0, 0));
        cancel.setMargin(ok.getMargin());
        getRootPane().setDefaultButton(ok);
        // 布局---------------
        int tHight =
                general.getPreferredSize().height + CustOpts.BTN_HEIGHT + 2 * CustOpts.VER_GAP + CustOpts.SIZE_EDGE
                        + CustOpts.SIZE_TITLE;
        setBounds((CustOpts.SCRWIDTH - 260) / 2, (CustOpts.SCRHEIGHT - tHight) / 2, 260, tHight); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        // 搭建－－－－－－－－－－－－－
        getContentPane().add(general);
        getContentPane().add(ok);
        getContentPane().add(cancel);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        cancel.addActionListener(this);
        getContentPane().addComponentListener(this);

        // init Contents
        PASSED = false;
        USERTYPE = "100";
        general.setUserName(CustOpts.custOps.getUserName());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                general.pfdPassword.grabFocus();
            }
        });
    }

    private JFrame parent;
    private JButton ok;
    private JButton cancel;
    private LoginGeneralPanel general;
}
