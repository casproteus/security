package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.TableDlg;
import org.cas.client.platform.bar.dialog.TablesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.dialog.statistics.CheckBillDlg;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;

public class Cmd_OrderManage implements SamActionListener {

	private static Cmd_OrderManage instance;
	private Cmd_OrderManage() {}
	public static Cmd_OrderManage getInstance() {
		if(instance == null)
			instance = new Cmd_OrderManage();
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
		
		String endNow = BarOption.df.format(new Date());
		int p = endNow.indexOf(" ");
		String startTime = endNow.substring(0, p + 1) + BarOption.getStartTime();
		CheckBillDlg dlg = new CheckBillDlg(BarFrame.instance);
		dlg.initContent(startTime, endNow);
		dlg.setVisible(true);
	}
}
