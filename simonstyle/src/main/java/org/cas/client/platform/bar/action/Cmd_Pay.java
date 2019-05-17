package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.resource.international.DlgConst;

public class Cmd_Pay implements ActionListener {
	private static Cmd_Pay instance;
	
	private Cmd_Pay() {}
	
	public static Cmd_Pay getInstance() {
		if(instance == null) {
			instance = new Cmd_Pay();
		}
		return instance;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		billPanel.createAndPrintNewOutput();	//process the new added items (send to printer and db).
		billPanel.billPricesUpdateToDB();		//the total price could has changed, because user added new item.
		
		//if it's already paid, show comfirmDialog.
		if(billPanel.status >= DBConsts.completed || billPanel.status < DBConsts.original) {
			if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.ConfirmPayAgain(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
    			return;
			}else {
				billPanel.reGenerate(null);
			}
		}
		//check if the pay dialog is already visible, if yes, then update bill received values.
		if(BarFrame.payDlg.isVisible()) {
			BarFrame.payDlg.updateBill(billPanel.getBillID());
		}
		//show dialog-------------------------------------
 		BarFrame.payDlg.setFloatSupport(true);
 		if(o == BarFrame.btnCASH) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterCashPayment());
 		}else if(o == BarFrame.btnDEBIT) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterDebitPayment());
 		}else if(o == BarFrame.btnVISA) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterVisaPayment());
 		}else if(o == BarFrame.btnMASTER) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterMasterPayment());
 		}else if(o == BarFrame.btnOTHER) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterOtherPayment());
 		}
 		//init payDialog content base on bill.
 		BarFrame.payDlg.initContent(billPanel);
 		BarFrame.payDlg.setVisible(true);
	}
}
