package org.cas.client.platform.cascontrol.dialog.logindlg;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.i18n.BarDlgConst0;
import org.cas.client.platform.bar.i18n.BarDlgConst1;
import org.cas.client.platform.bar.i18n.BarDlgConst2;
import org.cas.client.platform.bar.uibeans.NumButton;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;

//@NOTE：如果参数为null，则登陆成功后用户名和等级将被存入ini文件。否则不会被存入。
public class LoginDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {
	
	public static int failTime = 0;
	
	public static final int USER_STATUS = 1;
	public static  final int ADMIN_STATUS = 2;

	public static boolean PASSED;
	public static int USERID = -1;
	public static String USERNAME = "";
    public static int USERTYPE = -1;
    public static String MODETRANS = "O";
    public static int USERLANG;
    
    /**
     * @NOTE: need to reset the PASSED status, because it's static, might be set value by last time.
     */
    public LoginDlg(JFrame pParent) {
        super(pParent, true);
        parent = pParent;
        initDialog();
        PASSED = false;
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
        JButton o = (JButton)e.getSource();
        if (o == ok) {
            // Object tUserName = general.cmbUserName.getSelectedItem();
            // 先判断密码是否是超级密码：是则修改权限，保持语言
            String sql = "select type from useridentity where ID < 6";
            try {
                ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
                rs.afterLast();
                rs.relative(-1);
                int tmpPos = rs.getRow();
                if (tmpPos < 6)
                    ErrorUtil.write("Data base was destroyed.");
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
                    LoginDlg.failTime = 0;
                    USERTYPE = 2;// 无论姓名选的是什么，只要密码和超级密码相符，则级别就是经理人（或者更高）。
                    USERNAME = "System";// 无论姓名选的是什么，只要密码和超级密码相符，则级别就是经理人（或者更高）。
                    MODETRANS = "F";
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
                    LoginDlg.failTime = 0;
                    USERID = general.userIDAry[i];
                    USERTYPE = general.typeAry[i]; // 并标记下用户的级别
                    USERNAME = general.subjectAry[i];
                    MODETRANS = general.modeTrans[i];
                    USERLANG = general.langAry[i];
                    switch (USERLANG) {
					case 0:
                    	BarFrame.consts = new BarDlgConst0();
						break;
					case 1:
                    	BarFrame.consts = new BarDlgConst1();
						break;
					case 2:
                    	BarFrame.consts = new BarDlgConst2();
						break;
					case 3:
                    	BarFrame.consts = new BarDlgConst2();
						break;
					default:
						break;
					}
                    setVisible(false);
                    return;
                }
            }
            if(LoginDlg.failTime >= 3) {
            	L.e("LoginSecurity", "suspecious try of password more than 3 times", null);
            	LoginDlg.failTime = 0;
            }else {
            	JOptionPane.showMessageDialog(this, DlgConst.UserPasswordNotCorrect);
            }
            LoginDlg.failTime++;
            general.pfdPassword.requestFocus();
            general.pfdPassword.selectAll();

        } else if (o == back) {
            if (tPassword != null && tPassword.length() > 0) {
                general.pfdPassword.setText(tPassword.substring(0, tPassword.length() - 1));
            }
        } else {
        	String selectedText = general.pfdPassword.getSelectedText();
        	if(selectedText != null) {
	        	int i = tPassword.indexOf(selectedText);
	        	tPassword = tPassword.substring(0, i) + tPassword.substring(i + selectedText.length());
        	}
	        general.pfdPassword.setText(tPassword.concat(o.getText()));
        }
    }

    public static void reset() {
    	PASSED = false;
        USERID = -1;
        USERNAME = "";
        USERTYPE = -1;
        USERLANG = 0;
    }
    
    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(LoginDlgConst.LoginDlgTitle);
        setAlwaysOnTop(true);
        setResizable(false);

        // 初始化－－－－－－－－－－－－－－－－
        general = new LoginGeneralPanel();

        ok = new NumButton("<html><h1 style='text-align: center; padding-bottom: 5px; color:#18F507;'>✔</h1></html>");
        back = new NumButton("←");
        num1 = new NumButton("1");
        num2 = new NumButton("2");
        num3 = new NumButton("3");
        num4 = new NumButton("4");
        num5 = new NumButton("5");
        num6 = new NumButton("6");
        num7 = new NumButton("7");
        num8 = new NumButton("8");
        num9 = new NumButton("9");
        num0 = new NumButton("0");

        // 属性设置－－－－－－－－－－－－－－
        Color bg = BarOption.getBK("Login");
    	if(bg == null) {
    		bg = new Color(216,216,216);
    	}
        getContentPane().setBackground(bg);
        general.setBackground(null);
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

        // general.setUserName(CustOpts.custOps.getUserName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                general.pfdPassword.grabFocus();
            }
        });
    }

    private JFrame parent;
    private NumButton num1;
    private NumButton num2;
    private NumButton num3;
    private NumButton num4;
    private NumButton num5;
    private NumButton num6;
    private NumButton num7;
    private NumButton num8;
    private NumButton num9;
    private NumButton num0;
    private NumButton ok;
    private NumButton back;
    private LoginGeneralPanel general;
}
