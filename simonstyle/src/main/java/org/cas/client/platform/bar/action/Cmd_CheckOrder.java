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
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.dialog.statistics.CheckBillDlg;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;

public class Cmd_CheckOrder implements SamActionListener {

	private static Cmd_CheckOrder instance;
	private Cmd_CheckOrder() {}
	public static Cmd_CheckOrder getInstance() {
		if(instance == null)
			instance = new Cmd_CheckOrder();
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
		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
    	String endNow = BarOption.df.format(new Date());
		int p = endNow.indexOf(" ");
		String startTime = endNow.substring(0, p + 1) + BarOption.getStartTime();
		CheckBillDlg dlg = new CheckBillDlg(BarFrame.instance);
		dlg.initContent(startTime, endNow);
		dlg.setVisible(true);
	}
}
