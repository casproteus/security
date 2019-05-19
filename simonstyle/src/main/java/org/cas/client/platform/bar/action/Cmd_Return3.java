package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.CommandBtnDlg;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;

public class Cmd_Return3 implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		LoginDlg.USERTYPE = -1;
		BarFrame.instance.switchMode(BarOption.isFastFoodMode() ? 2 : 0);
		if(BarOption.isSingleUser()) {
			BarFrame.instance.setVisible(false);
			BarFrame.singleUserLoginProcess();
		}else {
			new LoginDlg(null).setVisible(true);
            if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            	BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
            }
		}
	}
}
