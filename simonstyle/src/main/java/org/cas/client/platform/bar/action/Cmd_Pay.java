package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.resource.international.DlgConst;

public class Cmd_Pay implements SamActionListener {

	private static Cmd_Pay instance;
	private Cmd_Pay() {}
	public static Cmd_Pay getInstance() {
		if(instance == null)
			instance = new Cmd_Pay();
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
		String o = ((FunctionButton)e.getSource()).getText();
		
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
 		if(o.equals(BarFrame.consts.CASH())) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterCashPayment());
 		}else if(o.equals(BarFrame.consts.DEBIT())) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterDebitPayment());
 		}else if(o.equals(BarFrame.consts.VISA())) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterVisaPayment());
 		}else if(o.equals(BarFrame.consts.MASTER())) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterMasterPayment());
 		}else if(o.equals(BarFrame.consts.OTHER())) {
 			BarFrame.payDlg.setTitle(BarFrame.consts.EnterOtherPayment());
 		}
 		//init payDialog content base on bill.
 		BarFrame.payDlg.initContent(billPanel);
 		BarFrame.payDlg.setVisible(true);
	}
}
