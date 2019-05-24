package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.TableDlg;
import org.cas.client.platform.bar.dialog.TablesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;

public class Cmd_PrintOneBill implements SamActionListener {

	private static Cmd_PrintOneBill instance;
	private Cmd_PrintOneBill() {}
	public static Cmd_PrintOneBill getInstance() {
		if(instance == null)
			instance = new Cmd_PrintOneBill();
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

		BillListPanel billListPanel = (BillListPanel)BarFrame.instance.panels[1];
		
		if(!billListPanel.checkColosedBill()) {
			return;
		}

		//check if all bills are not closed, and find the first panel
		ArrayList<BillPanel> unclosedBillPanels = billListPanel.gatherAllUnclosedBillPanels();
		
		PrintService.exePrintBills(unclosedBillPanels);
		
		//combine bills--------------------------------------------------------------------------------------------
		billListPanel.combineBills(unclosedBillPanels);

	}
}
