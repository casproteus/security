package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.ChangeDlg;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class Cmd_Refund implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		//check if it's already paid.
    	if(billPanel.status != DBConsts.completed) {
    		JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.NotPayYet());
    		return;
    	}
    		
 		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.Refund());
 		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.RefundNotice());
    	BarFrame.numberPanelDlg.setBtnSource(null);
 		BarFrame.numberPanelDlg.setFloatSupport(true);
 		BarFrame.numberPanelDlg.setPercentSupport(false);
 		BarFrame.numberPanelDlg.reLayout();
 		BarFrame.numberPanelDlg.setModal(true);
 		BarFrame.numberPanelDlg.setContents(billPanel.valTotlePrice.getText());
 		BarFrame.numberPanelDlg.setVisible(true);
 		
 		try {
				String curContent = BarFrame.numberPanelDlg.curContent;
				if(curContent == null || curContent.length() == 0)
					return;
				
     		float refund = BarFrame.numberPanelDlg.isPercentage ? 
     				Float.valueOf(billPanel.valTotlePrice.getText()) * Float.valueOf(curContent)
     				: Float.valueOf(curContent);
     		
     		if(refund > Float.valueOf(billPanel.valTotlePrice.getText())) {
     			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
     			return;
     		}
     		
     		// get out existing status.
     		int refundAmount = billPanel.status;
            if(refundAmount < -1) {	//if already refund, then add into existing amount.
            	if (JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.AllreadyRefund() + BarOption.getMoneySign() + (0-refundAmount) / 100.0, DlgConst.DlgTitle,
	                    JOptionPane.YES_NO_OPTION) != 0) {// allready refunded, sure to refund again?
					return;
				}else {
					refundAmount -= (int)(refund * 100);
				}
            }else {		//first time refund, then set the refund.
            	refundAmount = 0 - (int)(refund * 100);
            }
            
    		new ChangeDlg(BarFrame.instance, 
    				BarOption.getMoneySign() + BarUtil.formatMoney(refund)).setVisible(true); //it's a non-modal dialog.
    		
    		//dump the old bill and create a new bill
    		StringBuilder sql = new StringBuilder("update bill set status = ").append(DBConsts.expired)
     				.append(" where id = ").append(billPanel.getBillID());
     		PIMDBModel.getStatement().executeUpdate(sql.toString());
     		
     		//generat new bill with ref to dumpted bill everything else use the data on current billPane
     		//@NOTE:no need to generata new output. the output will be choosed by table and billIdx.
    		billPanel.comment = PrintService.REF_TO + billPanel.getBillID() + "F";
     		int newBillID = billPanel.cloneCurrentBillRecord(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
    				String.valueOf(BarFrame.instance.getCurBillIndex()),
    				BarFrame.instance.valStartTime.getText(),
    				Math.round(Float.valueOf(billPanel.valTotlePrice.getText()) * 100));
     		
     		//change something on cur billPane, then use it to print the refund bill, to let revenue know the store refund some money.
     		billPanel.setBillID(newBillID);
    		PrintService.exePrintRefund(billPanel, - (int)(refund * 100));
    		
    		//update the status with new refund amount for the new bill, so next time refund will base on new number.
     		sql = new StringBuilder("update bill set status = ").append(refundAmount)
     				.append(" where id = ").append(newBillID);
     		PIMDBModel.getStatement().executeUpdate(sql.toString());
    		
     		if(!BarOption.isFastFoodMode()) {
     			BarFrame.instance.switchMode(0);
     		}				
     		Cmd_OpenDrawer.getInstance().actionPerformed(null);
     	}catch(Exception exp) {
         	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
     		return;
     	}
    	
	}
}
