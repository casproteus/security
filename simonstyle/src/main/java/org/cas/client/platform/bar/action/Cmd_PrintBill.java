package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;

public class Cmd_PrintBill implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		billPanel.createAndPrintNewOutput();		//will send new added(not printed yet) dishes to kitchen.
		billPanel.billPricesUpdateToDB();
		billPanel.printBill(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
				BarFrame.instance.getCurBillIndex(),
				BarFrame.instance.valStartTime.getText(),
				true);
		billPanel.initContent();
	}
}
