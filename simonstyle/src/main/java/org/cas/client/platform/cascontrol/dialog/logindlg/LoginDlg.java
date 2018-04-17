package org.cas.client.platform.cascontrol.dialog.logindlg;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;

//@NOTE：如果参数为null，则登陆成功后用户名和等级将被存入ini文件。否则不会被存入。
public class LoginDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {
    public static boolean PASSED;
    public static String USERNAME;
    public static String USERTYPE;
    public static int USERLANG;
    
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
    @Override
    public void reLayout() {
        ok.setBounds(getContainer().getWidth() - CustOpts.BTN_WIDTH_NUM - CustOpts.HOR_GAP, getContainer().getHeight()
                - CustOpts.BTN_WIDTH_NUM * 4 - CustOpts.VER_GAP * 4, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM * 4
                + CustOpts.VER_GAP * 3);// 关闭

        back.setBounds(ok.getX() - CustOpts.BTN_WIDTH_NUM * 2 - CustOpts.HOR_GAP * 2, getContainer().getHeight()
                - CustOpts.BTN_WIDTH_NUM - CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM * 2 + CustOpts.HOR_GAP,
                CustOpts.BTN_WIDTH_NUM);// back
        num0.setBounds(back.getX() - CustOpts.BTN_WIDTH_NUM - CustOpts.HOR_GAP, back.getY(), CustOpts.BTN_WIDTH_NUM,
                CustOpts.BTN_WIDTH_NUM);
        num9.setBounds(ok.getX() - CustOpts.BTN_WIDTH_NUM - CustOpts.HOR_GAP, back.getY() - CustOpts.BTN_WIDTH_NUM
                - CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num8.setBounds(num9.getX() - CustOpts.BTN_WIDTH_NUM - CustOpts.HOR_GAP, num9.getY(), CustOpts.BTN_WIDTH_NUM,
                CustOpts.BTN_WIDTH_NUM);
        num7.setBounds(num8.getX() - CustOpts.BTN_WIDTH_NUM - CustOpts.HOR_GAP, num9.getY(), CustOpts.BTN_WIDTH_NUM,
                CustOpts.BTN_WIDTH_NUM);
        num6.setBounds(num9.getX(), num9.getY() - CustOpts.BTN_WIDTH_NUM - CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM,
                CustOpts.BTN_WIDTH_NUM);
        num5.setBounds(num8.getX(), num6.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num4.setBounds(num7.getX(), num6.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num3.setBounds(num9.getX(), num6.getY() - CustOpts.BTN_WIDTH_NUM - CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM,
                CustOpts.BTN_WIDTH_NUM);
        num2.setBounds(num8.getX(), num3.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num1.setBounds(num7.getX(), num3.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        general.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getContainer().getWidth() - 2 * CustOpts.HOR_GAP,
                (ok.getY()) - 2 * CustOpts.VER_GAP);
        general.componentResized(null);
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
        ok.removeActionListener(this);
        back.removeActionListener(this);
        num1.removeActionListener(this);
        num2.removeActionListener(this);
        num3.removeActionListener(this);
        num4.removeActionListener(this);
        num5.removeActionListener(this);
        num6.removeActionListener(this);
        num7.removeActionListener(this);
        num8.removeActionListener(this);
        num9.removeActionListener(this);
        num0.removeActionListener(this);
        if (general != null) {
            general.removeAll();
            general = null;
        }
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made invisible. */
    @Override
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
    @Override
    public void actionPerformed(
            ActionEvent e) {
        String tPassword = new String(general.pfdPassword.getPassword());
        Object o = e.getSource();
        if (o == ok) {
            // Object tUserName = general.cmbUserName.getSelectedItem();
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
                // if (general.subjectAry[i].equals(tUserName)) { // 找到用户名的匹配项，
                if (general.passwordAry[i].equals(tPassword)) { // 查验密码是否吻合。
                    PASSED = true; // 吻合就标记通过。
                    USERTYPE = String.valueOf(general.typeAry[i]); // 并标记下用户的级别
                    USERNAME = general.subjectAry[i];
                    USERLANG = general.langAry[i];
                    
                    setVisible(false);
                    if (parent == null) { // parent为null说明是系统刚刚启动时候的登陆操作，将用户名和级别记载到ini中。
                        CustOpts.custOps.setUserName(USERNAME); // 用户名将被用来作为下一次,作为默认的选中项。
                        CustOpts.custOps.setUserType(USERTYPE); // 级别将被用来作为子功能限制的依据。
                        CustOpts.custOps.setUserLang(USERLANG);
                    }
                    return; // 不吻合就推出，等待用户重新输入密码。
                }
                // }
            }
            JOptionPane.showMessageDialog(this, DlgConst.UserPasswordNotCorrect);
            general.pfdPassword.requestFocus();
            general.pfdPassword.selectAll();

        } else if (o == back) {
            if (tPassword != null && tPassword.length() > 0) {
                general.pfdPassword.setText(tPassword.substring(0, tPassword.length() - 1));
            }
        } else if (o == num1) {
            general.pfdPassword.setText(tPassword.concat("1"));
        } else if (o == num2) {
            general.pfdPassword.setText(tPassword.concat("2"));
        } else if (o == num3) {
            general.pfdPassword.setText(tPassword.concat("3"));
        } else if (o == num4) {
            general.pfdPassword.setText(tPassword.concat("4"));
        } else if (o == num5) {
            general.pfdPassword.setText(tPassword.concat("5"));
        } else if (o == num6) {
            general.pfdPassword.setText(tPassword.concat("6"));
        } else if (o == num7) {
            general.pfdPassword.setText(tPassword.concat("7"));
        } else if (o == num8) {
            general.pfdPassword.setText(tPassword.concat("8"));
        } else if (o == num9) {
            general.pfdPassword.setText(tPassword.concat("9"));
        } else if (o == num0) {
            general.pfdPassword.setText(tPassword.concat("0"));
        }
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(LoginDlgConst.LoginDlgTitle);
        setResizable(false);

        // 初始化－－－－－－－－－－－－－－－－
        general = new LoginGeneralPanel();

        ok = new JButton("✔");
        back = new JButton("←");
        num1 = new JButton("1");
        num2 = new JButton("2");
        num3 = new JButton("3");
        num4 = new JButton("4");
        num5 = new JButton("5");
        num6 = new JButton("6");
        num7 = new JButton("7");
        num8 = new JButton("8");
        num9 = new JButton("9");
        num0 = new JButton("0");

        // 属性设置－－－－－－－－－－－－－－
        // ok.setFont(CustOpts.custOps.getFontOfDefault());
        ok.setFocusable(false);
        back.setFocusable(false);
        ok.setMnemonic('o');
        back.setMnemonic('c');

        ok.setMargin(new Insets(0, 0, 0, 0));
        back.setMargin(ok.getMargin());
        num1.setMargin(ok.getMargin());
        num2.setMargin(ok.getMargin());
        num3.setMargin(ok.getMargin());
        num4.setMargin(ok.getMargin());
        num5.setMargin(ok.getMargin());
        num6.setMargin(ok.getMargin());
        num7.setMargin(ok.getMargin());
        num8.setMargin(ok.getMargin());
        num9.setMargin(ok.getMargin());
        num0.setMargin(ok.getMargin());
        getRootPane().setDefaultButton(ok);
        // 布局---------------
        int tHight =
                general.getPreferredSize().height + CustOpts.BTN_WIDTH_NUM * 4 + 5 * CustOpts.VER_GAP
                        + CustOpts.SIZE_EDGE + CustOpts.SIZE_TITLE;
        int tWidth = 310;
        setBounds((CustOpts.SCRWIDTH - tWidth) / 2, (CustOpts.SCRHEIGHT - tHight) / 2, tWidth, tHight); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        // 搭建－－－－－－－－－－－－－
        getContentPane().add(general);
        getContentPane().add(ok);
        getContentPane().add(back);
        getContentPane().add(num1);
        getContentPane().add(num2);
        getContentPane().add(num3);
        getContentPane().add(num4);
        getContentPane().add(num5);
        getContentPane().add(num6);
        getContentPane().add(num7);
        getContentPane().add(num8);
        getContentPane().add(num9);
        getContentPane().add(num0);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        back.addActionListener(this);
        num1.addActionListener(this);
        num2.addActionListener(this);
        num3.addActionListener(this);
        num4.addActionListener(this);
        num5.addActionListener(this);
        num6.addActionListener(this);
        num7.addActionListener(this);
        num8.addActionListener(this);
        num9.addActionListener(this);
        num0.addActionListener(this);
        getContentPane().addComponentListener(this);

        // init Contents
        PASSED = false;
        USERTYPE = "100";
        // general.setUserName(CustOpts.custOps.getUserName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                general.pfdPassword.grabFocus();
            }
        });
    }

    private JFrame parent;
    private JButton num1;
    private JButton num2;
    private JButton num3;
    private JButton num4;
    private JButton num5;
    private JButton num6;
    private JButton num7;
    private JButton num8;
    private JButton num9;
    private JButton num0;
    private JButton ok;
    private JButton back;
    private LoginGeneralPanel general;
}
