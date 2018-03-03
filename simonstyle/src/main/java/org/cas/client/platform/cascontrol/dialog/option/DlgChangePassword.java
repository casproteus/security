package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.OptionDlgConst;

class DlgChangePassword extends JDialog implements ActionListener {
    private int width = 300;
    private int height = 3 * CustOpts.BTN_HEIGHT + 3 * CustOpts.VER_GAP;
    private JButton ok;
    private JButton cancel;
    private JPasswordField newPassField;
    private JPasswordField password;
    private JPasswordField resultPassword;

    /**
     * 改变密码对话盒
     * 
     * @param prmDialog
     *            父窗体
     * @param prmIsModel
     *            是否模式
     * @param prmResultPassword
     *            密码
     * @param prmCustomOptions用户自定义
     */
    public DlgChangePassword(JDialog prmDialog, boolean prmIsModel, JPasswordField prmResultPassword) {
        super(prmDialog, prmIsModel);
        // 更改密码...
        setTitle(OptionDlgConst.OPTION_CHANGE_PASSWORD);

        resultPassword = prmResultPassword;
        initDialog();

        setBounds((CustOpts.SCRWIDTH - width) / 2, (CustOpts.SCRHEIGHT - height) / 2, width, height); // 对话框的默认尺寸。
    }

    private void initDialog() {
        // 新密码
        JLabel newPasswordLabel = new JLabel(OptionDlgConst.OPTION_NEW_PASSWORD, 'N');
        // 确认密码
        JLabel confirmPasswordLabel = new JLabel(OptionDlgConst.OPTION_CONFIRM_PASSWORD, 'C');
        int labelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { newPasswordLabel, confirmPasswordLabel });
        newPassField = new JPasswordField();
        newPasswordLabel.setLabelFor(newPassField);
        newPasswordLabel.setBounds(CustOpts.HOR_GAP, 0, labelWidth, CustOpts.LBL_HEIGHT);
        newPassField.setBounds(newPasswordLabel.getX() + newPasswordLabel.getWidth(), newPasswordLabel.getY(), width
                - labelWidth - 2 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        getContentPane().add(newPasswordLabel);
        getContentPane().add(newPassField);
        password = new JPasswordField();
        confirmPasswordLabel.setLabelFor(password);
        confirmPasswordLabel.setBounds(CustOpts.HOR_GAP, newPassField.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                labelWidth, CustOpts.LBL_HEIGHT);
        password.setBounds(confirmPasswordLabel.getX() + confirmPasswordLabel.getWidth(), confirmPasswordLabel.getY(),
                width - labelWidth - 2 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        getContentPane().add(confirmPasswordLabel);
        getContentPane().add(password);
        ok = new JButton(DlgConst.OK);
        ok.setBounds(width - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, height - CustOpts.BTN_HEIGHT,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cancel = new JButton(DlgConst.CANCEL);
        cancel.setBounds(width - CustOpts.BTN_WIDTH, height - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

        getContentPane().add(ok);
        getContentPane().add(cancel);
        ok.addActionListener(this);
    }

    /**
     * 确定动作
     */
    private void okClicked() {
        String newPasswardStr = newPassField.getText();
        String confirmPasswordStr = password.getText();
        if (newPasswardStr.equals(confirmPasswordStr)) {
            // 您确认要更改密码吗？
            int result = SOptionPane.showErrorDialog(MessageCons.Q50706);
            if (result == 4) {
                resultPassword.setText(newPasswardStr);
                CustOpts.custOps.setCurrsorPassword(newPasswardStr);
                dispose();
            }
            if (result == 1) {
                dispose();
            }
        } else {
            // 您输入的密码不一致，请重新输入。
            SOptionPane.showErrorDialog(MessageCons.E10684);
        }
    }

    /**
     * 处理按钮动作
     * 
     * @param actionEvent
     *            动作事件
     */
    public void actionPerformed(
            java.awt.event.ActionEvent actionEvent) {
        okClicked();
    }

}
