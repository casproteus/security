package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;

public class Cmd_PrintAll implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		BillListPanel billListPanel = (BillListPanel)BarFrame.instance.panels[1];
		
		ArrayList<BillPanel> unclosedBillPanels = billListPanel.gatherAllUnclosedBillPanels();
		for (BillPanel billPanel : unclosedBillPanels) {
			billPanel.printBill(
					BarFrame.instance.cmbCurTable.getSelectedItem().toString(), 
					billPanel.billButton.getText(), 
					BarFrame.instance.valStartTime.getText(),
					true);
		}
	}
}
