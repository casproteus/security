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
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;

public class Cmd_PrintOneBill implements ActionListener {

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
