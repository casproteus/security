package org.cas.client.platform.bar.dialog;

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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;

public class OffDutyDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, FocusListener,
        KeyListener {
    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public OffDutyDlg(JFrame pParent) {
        super(pParent, true);
        initDialog();
    }

    public void keyTyped(
            KeyEvent e) {
    }

    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
        int tKeyCode = e.getKeyCode();
        if (o == tfdRMB) {
            if (tKeyCode == 38)
                tfd7.grabFocus();
            else if (tKeyCode == 40)
                tfd0.grabFocus();
        } else if (o == tfd0) {
            if (tKeyCode == 38)
                tfdRMB.grabFocus();
            else if (tKeyCode == 40)
                tfd1.grabFocus();
        } else if (o == tfd1) {
            if (tKeyCode == 38)
                tfd0.grabFocus();
            else if (tKeyCode == 40)
                tfd2.grabFocus();
        } else if (o == tfd2) {
            if (tKeyCode == 38)
                tfd1.grabFocus();
            else if (tKeyCode == 40)
                tfd3.grabFocus();
        } else if (o == tfd3) {
            if (tKeyCode == 38)
                tfd2.grabFocus();
            else if (tKeyCode == 40)
                tfd4.grabFocus();
        } else if (o == tfd4) {
            if (tKeyCode == 38)
                tfd3.grabFocus();
            else if (tKeyCode == 40)
                tfd5.grabFocus();
        } else if (o == tfd5) {
            if (tKeyCode == 38)
                tfd4.grabFocus();
            else if (tKeyCode == 40)
                tfd6.grabFocus();
        } else if (o == tfd6) {
            if (tKeyCode == 38)
                tfd5.grabFocus();
            else if (tKeyCode == 40)
                tfd7.grabFocus();
        } else if (o == tfd7) {
            if (tKeyCode == 38)
                tfd6.grabFocus();
            else if (tKeyCode == 40)
                tfdRMB.grabFocus();
        }
    }

    public void keyReleased(
            KeyEvent e) {
        int tKeyCode = e.getKeyCode();
        if (BarUtility.isNumber(tKeyCode)) {
            Object o = e.getSource();
            if (o == tfdRMB) {
                String tValue = tfdRMB.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfd0.grabFocus();
            } else if (o == tfd0) {
                String tValue = tfd0.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfd1.grabFocus();
            } else if (o == tfd1) {
                String tValue = tfd1.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfd2.grabFocus();
            } else if (o == tfd2) {
                String tValue = tfd2.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfd3.grabFocus();
            } else if (o == tfd3) {
                String tValue = tfd3.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfd4.grabFocus();
            } else if (o == tfd4) {
                String tValue = tfd4.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfd5.grabFocus();
            } else if (o == tfd5) {
                String tValue = tfd5.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfd6.grabFocus();
            } else if (o == tfd6) {
                String tValue = tfd6.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfd7.grabFocus();
            } else if (o == tfd7) {
                String tValue = tfd7.getText();
                int tDotPosition = tValue.indexOf('.');
                if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3)// 位数满了，转移焦点
                    tfdRMB.grabFocus();
            }
        }
    }

    public void focusGained(
            FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    public void focusLost(
            FocusEvent e) {
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    public void reLayout() {
        // 右边的TextField
        tfdRMB.setBounds(getWidth() - CustOpts.HOR_GAP * 3 - 100, CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);
        tfd0.setBounds(tfdRMB.getX(), tfdRMB.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);
        tfd1.setBounds(tfd0.getX(), tfd0.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);
        tfd2.setBounds(tfd1.getX(), tfd1.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);
        tfd3.setBounds(tfd2.getX(), tfd2.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);
        tfd4.setBounds(tfd3.getX(), tfd3.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);
        tfd5.setBounds(tfd4.getX(), tfd4.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);
        tfd6.setBounds(tfd5.getX(), tfd5.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);
        tfd7.setBounds(tfd6.getX(), tfd6.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, 100, CustOpts.BTN_HEIGHT);

        // 左边的标签
        lblRMB.setBounds(tfdRMB.getX() - lblRMB.getPreferredSize().width, tfdRMB.getY(),
                lblRMB.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblForeign0.setBounds(tfd0.getX() - lblForeign0.getPreferredSize().width, tfd0.getY(),
                lblForeign0.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblForeign1.setBounds(tfd1.getX() - lblForeign1.getPreferredSize().width, tfd1.getY(),
                lblForeign1.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblForeign2.setBounds(tfd2.getX() - lblForeign2.getPreferredSize().width, tfd2.getY(),
                lblForeign2.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblForeign3.setBounds(tfd3.getX() - lblForeign3.getPreferredSize().width, tfd3.getY(),
                lblForeign3.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblForeign4.setBounds(tfd4.getX() - lblForeign4.getPreferredSize().width, tfd4.getY(),
                lblForeign4.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblForeign5.setBounds(tfd5.getX() - lblForeign5.getPreferredSize().width, tfd5.getY(),
                lblForeign5.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblForeign6.setBounds(tfd6.getX() - lblForeign6.getPreferredSize().width, tfd6.getY(),
                lblForeign6.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblForeign7.setBounds(tfd7.getX() - lblForeign7.getPreferredSize().width, tfd7.getY(),
                lblForeign7.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        cancel.setBounds(getWidth() - CustOpts.HOR_GAP * 3 - CustOpts.BTN_WIDTH, getHeight() - CustOpts.SIZE_TITLE
                - CustOpts.SIZE_EDGE - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        ok.setBounds(cancel.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
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
            float tMoneyRMB;
            try {
                tMoneyRMB = Float.parseFloat(tfdRMB.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdRMB.selectAll();
                return;
            }
            float tMoney0;
            try {
                tMoney0 = Float.parseFloat(tfd0.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfd0.selectAll();
                return;
            }
            float tMoney1;
            try {
                tMoney1 = Float.parseFloat(tfd1.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfd1.selectAll();
                return;
            }
            float tMoney2;
            try {
                tMoney2 = Float.parseFloat(tfd2.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfd2.selectAll();
                return;
            }
            float tMoney3;
            try {
                tMoney3 = Float.parseFloat(tfd3.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfd3.selectAll();
                return;
            }
            float tMoney4;
            try {
                tMoney4 = Float.parseFloat(tfd4.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfd4.selectAll();
                return;
            }
            float tMoney5;
            try {
                tMoney5 = Float.parseFloat(tfd5.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfd5.selectAll();
                return;
            }
            float tMoney7;
            try {
                tMoney7 = Float.parseFloat(tfd6.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfd6.selectAll();
                return;
            }
            float tMoney9;
            try {
                tMoney9 = Float.parseFloat(tfd7.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfd7.selectAll();
                return;
            }// 数据有效性检验完毕------------------------------------------------------------

            int tChange = 0; // 钱箱中原有的金额。
            try {
                tChange = Integer.parseInt((String) CustOpts.custOps.getValue(BarDlgConst.Shoestring));
            } catch (Exception exp) {
            }

            int tReceived =
                    (int) ((tMoneyRMB // 实收金额（float被强制转换成以分为单位的int）。
                            + tMoney0
                            / Float.parseFloat((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[1]))
                            + tMoney1
                            / Float.parseFloat((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[2]))
                            + tMoney2
                            / Float.parseFloat((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[3]))
                            + tMoney3
                            / Float.parseFloat((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[4]))
                            + tMoney4
                            / Float.parseFloat((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[5]))
                            + tMoney5
                            / Float.parseFloat((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[6]))
                            + tMoney7 / Float.parseFloat((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[7])) + tMoney9
                            / Float.parseFloat((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[8]))) * 100)
                            - tChange;

            // 查询交易记录，取出每笔交易中的应收账款项。然后加总，即可得target。
            String sql =
                    "Select id from userIdentity where username = '".concat(CustOpts.custOps.getUserName()).concat("'");
            int tID = -1;
            try {
                ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
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

            int target = 0; // 排除原有零钱，今天根据交易记录应该收到的金额。
            int profit = 0; // 累加每笔交易的收益得到今天的真实收益
            sql =
                    "Select TOLTALPRICE, PROFIT from Output where EMPLOYEEID = ".concat(String.valueOf(tID))
                            .concat(" and time >= '").concat(BarFrame.startTime).concat("' and time <= '")
                            .concat(Calendar.getInstance().getTime().toLocaleString()).concat("'");
            try {
                ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
                ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

                rs.afterLast();
                rs.relative(-1);
                int tmpPos = rs.getRow();
                rs.beforeFirst();

                tmpPos = 0;
                while (rs.next()) {
                    target += rs.getInt("TOLTALPRICE");
                    profit += rs.getInt("PROFIT");
                    tmpPos++;
                }
                rs.close();// 关闭
            } catch (SQLException exp) {
                ErrorUtil.write(exp);
            }

            // 增加一条绩效记录到数据库员工绩效考核表。
            sql =
                    "INSERT INTO evaluation(startTime, endTime, SUBJECT, receive, target, profit) VALUES ('"
                            .concat(BarFrame.startTime.concat("', '")
                                    .concat(Calendar.getInstance().getTime().toLocaleString()).concat("', '")
                                    .concat(CustOpts.custOps.getUserName()).concat("', ")
                                    .concat(String.valueOf(tReceived)).concat(", ").concat(String.valueOf(target))
                                    .concat(", ").concat(String.valueOf(profit)).concat(")"));
            try {
                Statement smt =  PIMDBModel.getStatement();
                smt.executeUpdate(sql.toString());

                smt.close();
                smt = null;
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
            // 更新新的零钱数进入ini文件。
            CustOpts.custOps.setKeyAndValue(BarDlgConst.Shoestring, String.valueOf(tReceived + tChange));
            // 显示信息
            JOptionPane.showMessageDialog(this, BarDlgConst.GoodBye);
            BarFrame.instance.setVisible(false);
            CASControl.ctrl.getMainFrame().setVisible(false); // 先隐藏视图，为的是给用户感觉关闭得比较快。
            try {
                CASControl.ctrl.exitSystem(); // 保存所有状态和数据后退出。
            } catch (Exception exp) {
                ErrorUtil.write(exp);
                System.exit(0);
            }
        } else if (o == cancel) {
            dispose();
        }
    }

    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarDlgConst.OffDuty);
        setResizable(false);
        setBounds((CustOpts.SCRWIDTH - 200) / 2, (CustOpts.SCRHEIGHT - 330) / 2, 200, 330); // 对话框的默认尺寸。

        // 初始化－－－－－－－－－－－－－－－－
        lblRMB = new JLabel(BarDlgConst.MoneyUnit[0].concat("："));
        lblForeign0 = new JLabel(BarDlgConst.MoneyUnit[1].concat("："));
        lblForeign1 = new JLabel(BarDlgConst.MoneyUnit[2].concat("："));
        lblForeign2 = new JLabel(BarDlgConst.MoneyUnit[3].concat("："));
        lblForeign3 = new JLabel(BarDlgConst.MoneyUnit[4].concat("："));
        lblForeign4 = new JLabel(BarDlgConst.MoneyUnit[5].concat("："));
        lblForeign5 = new JLabel(BarDlgConst.MoneyUnit[6].concat("："));
        lblForeign6 = new JLabel(BarDlgConst.MoneyUnit[7].concat("："));
        lblForeign7 = new JLabel(BarDlgConst.MoneyUnit[8].concat("："));

        tfdRMB = new JTextField("0.00");
        tfd0 = new JTextField("0.00");
        tfd1 = new JTextField("0.00");
        tfd2 = new JTextField("0.00");
        tfd3 = new JTextField("0.00");
        tfd4 = new JTextField("0.00");
        tfd5 = new JTextField("0.00");
        tfd6 = new JTextField("0.00");
        tfd7 = new JTextField("0.00");

        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // properties
        ok.setMnemonic('o');
        cancel.setMnemonic('c');

        ok.setMargin(new Insets(0, 0, 0, 0));
        cancel.setMargin(ok.getMargin());
        getRootPane().setDefaultButton(ok);

        // 布局---------------
        getContentPane().setLayout(null);
        reLayout();

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(ok);
        getContentPane().add(cancel);
        getContentPane().add(lblRMB);
        getContentPane().add(lblForeign0);
        getContentPane().add(lblForeign1);
        getContentPane().add(lblForeign2);
        getContentPane().add(lblForeign3);
        getContentPane().add(lblForeign4);
        getContentPane().add(lblForeign5);
        getContentPane().add(lblForeign6);
        getContentPane().add(lblForeign7);

        getContentPane().add(tfdRMB);
        getContentPane().add(tfd0);
        getContentPane().add(tfd1);
        getContentPane().add(tfd2);
        getContentPane().add(tfd3);
        getContentPane().add(tfd4);
        getContentPane().add(tfd5);
        getContentPane().add(tfd6);
        getContentPane().add(tfd7);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        cancel.addActionListener(this);
        getContentPane().addComponentListener(this);
        tfdRMB.addFocusListener(this);
        tfd0.addFocusListener(this);
        tfd1.addFocusListener(this);
        tfd2.addFocusListener(this);
        tfd3.addFocusListener(this);
        tfd4.addFocusListener(this);
        tfd5.addFocusListener(this);
        tfd6.addFocusListener(this);
        tfd7.addFocusListener(this);
        tfdRMB.addKeyListener(this);
        tfd0.addKeyListener(this);
        tfd1.addKeyListener(this);
        tfd2.addKeyListener(this);
        tfd3.addKeyListener(this);
        tfd4.addKeyListener(this);
        tfd5.addKeyListener(this);
        tfd6.addKeyListener(this);
        tfd7.addKeyListener(this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tfdRMB.selectAll();
                tfdRMB.grabFocus();
            }
        });
    }

    private JLabel lblRMB;
    private JLabel lblForeign0;
    private JLabel lblForeign1;
    private JLabel lblForeign2;
    private JLabel lblForeign3;
    private JLabel lblForeign4;
    private JLabel lblForeign5;
    private JLabel lblForeign6;
    private JLabel lblForeign7;

    private JTextField tfdRMB;
    private JTextField tfd0;
    private JTextField tfd1;
    private JTextField tfd2;
    private JTextField tfd3;
    private JTextField tfd4;
    private JTextField tfd5;
    private JTextField tfd6;
    private JTextField tfd7;

    private JButton ok;
    private JButton cancel;
}
