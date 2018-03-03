package org.cas.client.platform.cascontrol.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.group.PIMButtonGroup;
import org.cas.client.platform.casbeans.group.PIMButtonGroupListener;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.AccountDialogConstant;

public class ConfigDialog extends JDialog implements ActionListener, PIMButtonGroupListener {
    final int SETTING = 0;
    final int LOGGING = 1;

    /**
     * Creates a new instance of ConfigDialog 设置对话盒构建器
     * 
     * @param dialog
     *            父对话盒
     * @param model
     *            : 是否为模态
     */
    public ConfigDialog(JDialog dialog, boolean model) // ,Vector vector,Hashtable prmHashtable)
    {
        super(dialog, model);

        // 我的服务器需要安全验证
        setTitle(AccountDialogConstant.CONFIG_TITLE);

        // this.vector = vector;
        // hashtable = prmHashtable;

        // 取得对话盒的大小
        Dimension size = new Dimension(300, 180);
        width = size.width;
        height = size.height;

        // 初始化对话框
        initDialog(SETTING);

        setBounds((CustOpts.SCRWIDTH - width) / 2, (CustOpts.SCRHEIGHT - height) / 2, width, height); // 对话框的默认尺寸。
    }

    /*
     * @called by 邮件接收发送的方法。
     */
    /**
     * 构建器
     * 
     * @param prmParent
     *            父对话盒
     */
    public ConfigDialog(Frame prmParent) {
        super(prmParent, true);

        // 我的服务器需要安全验证
        setTitle(AccountDialogConstant.CONFIG_TITLE);

        // 在初始化各组件之前先确定对话盒的大小
        Dimension temSize = new Dimension(300, 140);
        width = temSize.width;
        height = temSize.height;

        // 初始化对话框
        initDialog(LOGGING);
        setBounds((CustOpts.SCRWIDTH - width) / 2, (CustOpts.SCRHEIGHT - height) / 2, width, height); // 对话框的默认尺寸。
    }

    /**
     * 初始化对话框
     * 
     * @param prmType
     *            应用类型
     */
    public void initDialog(
            int prmType) {
        // 登陆信息
        PIMSeparator title = new PIMSeparator(AccountDialogConstant.ENTRY_INFO);
        // "帐户名(C)"
        JLabel accountName = new JLabel(AccountDialogConstant.ACCOUNT_NAME, 'C');
        // "密码(P)"
        JLabel password = new JLabel(AccountDialogConstant.PASSWORD, 'P');
        int labelMaxWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { accountName, password });

        // ------
        nameField = new JTextField(CASUtility.EMPTYSTR);
        // 取得密码
        // ------
        passwordField = new JPasswordField(width - 4 * CustOpts.HOR_GAP - labelMaxWidth);
        // 取得密码
        // ------
        title.setBounds(0, 0, width, CustOpts.SEP_HEIGHT);
        getContentPane().add(title);
        if (prmType == SETTING) {
            buttonGroup = new PIMButtonGroup();

            // 登陆方式
            radioButton1 = new JRadioButton(AccountDialogConstant.RADIOBUTTON1, true);
            radioButton1.setMnemonic('U');
            radioButton1.setBounds(CustOpts.HOR_GAP, title.getY() + CustOpts.LBL_HEIGHT,
                    radioButton1.getPreferredSize().width, CustOpts.BTN_HEIGHT);
            getContentPane().add(radioButton1);
            radioButton2 = new JRadioButton(AccountDialogConstant.ENTRY_WAY, false);
            radioButton2.setMnemonic('D');
            radioButton2.setBounds(CustOpts.HOR_GAP, radioButton1.getY() + CustOpts.LBL_HEIGHT + 8,
                    radioButton2.getPreferredSize().width, CustOpts.BTN_HEIGHT);
            getContentPane().add(radioButton2);
            accountName.setLabelFor(nameField);
            accountName.setBounds(4 * CustOpts.HOR_GAP, radioButton2.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    labelMaxWidth, CustOpts.BTN_HEIGHT);
            nameField.setBounds(accountName.getX() + labelMaxWidth, accountName.getY(), width - 4 * CustOpts.HOR_GAP
                    - labelMaxWidth, CustOpts.BTN_HEIGHT);// 文本域的宽度
            getContentPane().add(nameField);
            password.setLabelFor(passwordField);
            password.setBounds(4 * CustOpts.HOR_GAP, nameField.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    labelMaxWidth, CustOpts.BTN_HEIGHT);
            getContentPane().add(password);
            passwordField.setBounds(password.getX() + labelMaxWidth, password.getY(), width - 4 * CustOpts.HOR_GAP
                    - labelMaxWidth, CustOpts.BTN_HEIGHT);
            getContentPane().add(passwordField);

            buttonGroup.add(radioButton1);
            buttonGroup.add(radioButton2);
            buttonGroup.addEButtonGroupListener(this);
        } else if (prmType == LOGGING) {
            accountName.setLabelFor(nameField);
            accountName.setBounds(4 * CustOpts.HOR_GAP, title.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    labelMaxWidth, CustOpts.BTN_HEIGHT);
            getContentPane().add(accountName);
            nameField.setBounds(accountName.getX() + labelMaxWidth, accountName.getY(), width - 4 * CustOpts.HOR_GAP
                    - labelMaxWidth, CustOpts.BTN_HEIGHT);
            getContentPane().add(nameField);
            password.setLabelFor(passwordField);
            password.setBounds(4 * CustOpts.HOR_GAP, nameField.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    labelMaxWidth, CustOpts.BTN_HEIGHT);
            getContentPane().add(passwordField);
            passwordField.setBounds(password.getX() + labelMaxWidth, password.getY(), width - 4 * CustOpts.HOR_GAP
                    - labelMaxWidth, CustOpts.BTN_HEIGHT);
            getContentPane().add(passwordField);
        }

        // "记住密码(W)"
        checkBox = new JCheckBox(AccountDialogConstant.REMBER_PAS, true);
        checkBox.setMnemonic('W');
        int checkBoxWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { checkBox });
        checkBox.setBounds(passwordField.getX(), password.getY() + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                checkBoxWidth, CustOpts.BTN_HEIGHT);
        getContentPane().add(checkBox);
        checkBox.addActionListener(this);
        if (checkBox.isSelected()) {
            passwordField.setEnabled(true);
        } else {
            passwordField.setEnabled(false);
        }

        if (prmType == SETTING) {
            if (radioButton1.isSelected()) {
                nameField.setEnabled(false);
                passwordField.setEnabled(false);
                checkBox.setEnabled(false);
            }
            if (radioButton2.isSelected()) {
                nameField.setEnabled(true);
                if (checkBox.isSelected()) {
                    passwordField.setEnabled(true);
                } else {
                    passwordField.setEnabled(false);
                }
                checkBox.setEnabled(true);
            }
        }

        // "确定"
        ok = new JButton(AccountDialogConstant.OK);
        ok.setBounds(width - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, height - CustOpts.BTN_HEIGHT,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);
        // "取消"
        cancel = new JButton(AccountDialogConstant.CANCEL);
        cancel.setBounds(width - CustOpts.BTN_WIDTH, height - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);

        ok.addActionListener(this);
        cancel.addActionListener(this);
    }

    /**
     * 设置用户名
     * 
     * @called by: 邮件发送时。
     * @param prmName
     *            用户名
     */
    public void setValidateName(
            String prmName) {
        nameField.setText(prmName);
    }

    /**
     * 设置验证密码
     * 
     * @param prmPassWord
     *            验证密码
     */
    public void setValidatePassword(
            String prmPassWord) {
        passwordField.setText(prmPassWord);
    }

    /**
     * 设置是否记住密码
     * 
     * @param prmInteger
     *            是否记住
     */
    public void setRemenberPassword(
            Boolean prmBoolean) {
        if (prmBoolean != null) {
            checkBox.setSelected(prmBoolean.booleanValue());
        }
    }

    // --------------------------------------------------------------------
    // 取得验证信息
    /**
     * 取得验证用户名
     * 
     * @return 验证用户名
     */
    public String getValidateName() {
        return nameField.getText();
    }

    /**
     * 取得验证密码
     * 
     * @return 验证密码
     */
    public String getValidatePassword() {
        return passwordField.getText();
    }

    /**
     * 返回是否记住密码
     * 
     * @return 是否记住
     */
    public Boolean getRemenberPassword() {
        return checkBox.isSelected() ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 返回按钮组
     * 
     * @return 按钮组
     */
    public PIMButtonGroup returenButtonGroup() {
        return buttonGroup;
    }

    // /////////////////////////////////////////////////////////
    // //////// 动 作 监 听 器 /////////////
    // /////////////////////////////////////////////////////////
    /**
     * Invoked when an action occurs. 确定动作
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        Object source = e.getSource();
        if (source == checkBox) {
            if (checkBox.isSelected()) {
                passwordField.setEnabled(true);
                passwordField.grabFocus();
            } else {
                passwordField.setEnabled(false);
            }
        } else if (source == ok) {
            isConfirm = true;
            dispose();
        } else if (source == cancel) {
            isConfirm = false;
            dispose();
        }
    }

    /**
     * 得到确认信息
     * 
     * @return 是否确认
     */
    public boolean getConfirmAction() {
        return isConfirm;
    }

    /**
     * 得到用户名文本框
     * 
     * @return 用户名文本框
     */
    public JTextField getUseNameField() {
        return nameField;
    }

    /**
     * 得到密码域
     * 
     * @return 密码域
     */
    public JPasswordField getPasswordField() {
        return passwordField;
    }

    /**
     * 得到原密码
     * 
     * @return 原密码
     */
    public JCheckBox getRememberPassword() {
        return checkBox;
    }

    /**
     * Invoked when selection changed.
     * 
     * @param group
     *            the button group whose selection changed.
     * @param select
     *            the group selected index.
     */
    public void selected(
            PIMButtonGroup group,
            int select) {
        if (radioButton1.isSelected()) {
            nameField.setEnabled(false);
            passwordField.setEnabled(false);
            checkBox.setEnabled(false);
        }
        if (radioButton2.isSelected()) {
            nameField.setEnabled(true);
            if (checkBox.isSelected()) {
                passwordField.setEnabled(true);
            } else {
                passwordField.setEnabled(false);
            }
            checkBox.setEnabled(true);
        }
    }

    // -------------------------------------------------------
    // //////////////////////////////////////////////
    // ///////// 变 量 //////////
    // /////////////////////////////////////////////
    private JButton ok, cancel;
    private JCheckBox checkBox;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private PIMButtonGroup buttonGroup;
    private int width;
    private int height;
    boolean isConfirm;
}
