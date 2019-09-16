package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.TableDlg;
import org.cas.client.platform.bar.dialog.TablesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;

public class Cmd_AddTable implements SamActionListener {

	private static Cmd_AddTable instance;
	private Cmd_AddTable() {}
	public static Cmd_AddTable getInstance() {
		if(instance == null)
			instance = new Cmd_AddTable();
		return instance;
	}
	
	private ISButton sourceBtn;
	
	public ISButton getSourceBtn() {
		return sourceBtn;
	}
	@Override
	public void setSourceBtn(ISButton sourceBtn) {
		this.sourceBtn = sourceBtn;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		TablesPanel tablesPanel = (TablesPanel)BarFrame.instance.panels[0];
		
		if(!BarOption.isSingleUser()) {
			new LoginDlg(null).setVisible(true);
            if (LoginDlg.PASSED == true) {
            	BarFrame.checkSignIn();
            	//@note: lowdown a little the level, to enable the admin do sales work.
            	if ("admin".equalsIgnoreCase(LoginDlg.USERNAME))
            		 LoginDlg.USERTYPE = LoginDlg.USER_STATUS;
            }else {
            	return;
            }
		}
		new TableDlg(null, null).setVisible(true);
		tablesPanel.initContent();
	}
}
