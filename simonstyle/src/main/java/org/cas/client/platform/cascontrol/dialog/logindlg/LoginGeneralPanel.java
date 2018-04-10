package org.cas.client.platform.cascontrol.dialog.logindlg;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.plaf.metal.MetalComboBoxEditor;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class LoginGeneralPanel extends JPanel implements ComponentListener {

    public LoginGeneralPanel() {
        initConponent();
    }

    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    @Override
    public void componentHidden(
            ComponentEvent e) {
    }

    private void initConponent() {
        lblUserName = new JLabel(LoginDlgConst.UserName);
        lblType = new JLabel(LoginDlgConst.Type);
        lblPassword = new JLabel(LoginDlgConst.Password);
        lblMakeSure = new JLabel(LoginDlgConst.MakeSure);

        cmbUserName = new JComboBox();
        cmbType = new JComboBox();
        pfdPassword = new JPasswordField();

        // properties
        setLayout(null);
        cmbUserName.setFocusable(false);

        // initContent@NOTE:本类中故意将内容初始化提前，因为需要将当前用户信息的对应的级别拿出来，并据此判断是否增加”级别“组件。
        initContent();
        reLayout();
        // built
        // add(lblUserName);
        // add(cmbUserName);
        add(lblPassword);
        add(pfdPassword);

        // add listener
        pfdPassword.addKeyListener(new KeyListener() { // 在密码框中相应上下左右键。使用户名做变化。
                    @Override
                    public void keyPressed(
                            KeyEvent e) {
                        int tSeleIdx = cmbUserName.getSelectedIndex();
                        int tCount = cmbUserName.getItemCount();
                        int tCode = e.getKeyCode();
                        if (tCode == 38) {
                            if (tSeleIdx > 0)
                                cmbUserName.setSelectedIndex(tSeleIdx - 1);
                            else
                                cmbUserName.setSelectedIndex(tCount - 1);
                        } else if (tCode == 40) {
                            if (tSeleIdx < tCount - 1)
                                cmbUserName.setSelectedIndex(tSeleIdx + 1);
                            else
                                cmbUserName.setSelectedIndex(0);
                        } else if (tCode == 37)
                            cmbUserName.setSelectedIndex(0);
                        else if (tCode == 39)
                            cmbUserName.setSelectedIndex(cmbUserName.getItemCount() - 1);
                    }

                    @Override
                    public void keyTyped(
                            KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(
                            KeyEvent e) {
                    }
                });
        addComponentListener(this);
    }

    private void initContent() {
        String sql = "select ID, Type, UserName, PASSWORD from useridentity where ID > 5";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            userIDAry = new int[tmpPos];
            typeAry = new int[tmpPos];
            subjectAry = new String[tmpPos];
            passwordAry = new String[tmpPos];
            rs.beforeFirst();
            tmpPos = 0;
            while (rs.next()) {
                userIDAry[tmpPos] = rs.getInt("id");
                typeAry[tmpPos] = rs.getInt("type");
                subjectAry[tmpPos] = rs.getString("UserName");
                passwordAry[tmpPos] = rs.getString("PASSWORD");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        // cmbUserName.setModel(new DefaultComboBoxModel(subjectAry));
        // cmbUserName.setSelectedIndex(0);
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    public void reLayout() {
        int prmWidth = getWidth();
        lblPassword.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblPassword.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        pfdPassword.setBounds(lblUserName.getX() + lblPassword.getWidth() + CustOpts.HOR_GAP, lblUserName.getY(),
                prmWidth - lblPassword.getWidth() - CustOpts.HOR_GAP * 3, 40);
        // lblPassword.setBounds(lblUserName.getX(), lblUserName.getY() + lblUserName.getHeight() + CustOpts.VER_GAP,
        // lblUserName.getWidth(), CustOpts.BTN_HEIGHT);
        // pfdPassword.setBounds(cmbUserName.getX(), lblPassword.getY(), cmbUserName.getWidth(), CustOpts.BTN_HEIGHT);
        setPreferredSize(new Dimension(getWidth(), pfdPassword.getY() + pfdPassword.getHeight() + CustOpts.VER_GAP));
    }

    public void setUserName(
            String pName) {
        if (pName != null) {
            int tIdx = 0;
            for (int i = 0, len = subjectAry.length; i < len; i++) {
                if (pName.equals(subjectAry[i])) {
                    tIdx = i;
                }
            }
            cmbUserName.setSelectedIndex(tIdx);
        }
    }

    String[] getState() {
        return new String[] {
                ((JTextField) ((MetalComboBoxEditor) cmbUserName.getEditor()).getEditorComponent()).getText(),
                pfdPassword.getText() };
    }

    JLabel lblUserName;
    JLabel lblType;
    JLabel lblPassword;
    JLabel lblMakeSure;

    JComboBox cmbUserName;
    JComboBox cmbType;
    JPasswordField pfdPassword;
    int[] userIDAry;
    int[] typeAry;
    String[] subjectAry;
    String[] passwordAry;
}
