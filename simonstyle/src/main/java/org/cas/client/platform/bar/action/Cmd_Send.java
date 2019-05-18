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
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class Cmd_Send implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		billPanel.createAndPrintNewOutput();
    	billPanel.billPricesUpdateToDB();
		
    	if(BarOption.isFastFoodMode()) {
	    	BarFrame.instance.valStartTime.setText(BarOption.df.format(new Date()));
	    	BarFrame.instance.addNewBillInCurTable();
	    }else {
	    	if(salesPanel.partialPaid) {
    			if(JOptionPane.showConfirmDialog(BarFrame.instance, 
	    				BarFrame.consts.COMFIRMCLEARMONEYRECEIVED(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) == 0) {
	            	//clean partial input
	            	StringBuilder sql = new StringBuilder("update bill set cashReceived = 0, debitReceived = 0, visaReceived = 0, masterReceived = 0, otherReceived = 0");
	            	sql.append(" where id = ").append(billPanel.getBillID());
	            	try {
	        			PIMDBModel.getStatement().executeUpdate(sql.toString());
	        			salesPanel.partialPaid = false;
	        		}catch(Exception exp) {
	        			ErrorUtil.write(exp);
	        		}
	            }
    		}
	    	BarFrame.instance.switchMode(0);
	    }
	}
}
