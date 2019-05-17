package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Date;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class Cmd_VoidOrder  implements ActionListener {

	private static Cmd_VoidOrder instance;
	
	private Cmd_VoidOrder() {}
	
	public static Cmd_VoidOrder getInstance() {
		if(instance == null) {
			instance = new Cmd_VoidOrder();
		}
		return instance;
	}
	
	@Override
	// void order include saved ones
	public void actionPerformed(ActionEvent e) {
		//if there's no dish on it at all (including deleted outputs), delete the bill directly
		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		int dishLength = billPanel.orderedDishAry.size();
		int billID = billPanel.getBillID();
    	String curBill = BarFrame.instance.getCurBillIndex();
    	
		try {
			//check if it's a "mistake-opening-table-action" or "adding bill action" by check if there's any output on it already. 
			//will be considered as non-empty as long as there's output connecting to the id, even the output is not currently displaying on this bill.
			StringBuilder sql = new StringBuilder("select * from output where category = ").append(billID)
					.append(" or (subject = '").append(BarFrame.instance.cmbCurTable.getSelectedItem()).append("'")
					.append(" and time = '").append(BarFrame.instance.valStartTime.getText()).append("'")
					.append(" and contactID = ").append(curBill).append(")"); 	//in future version, might need to check the deleted property.
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
		    rs.relative(-1);
		    
		    if(rs.getRow() == 0) {			//if still empty 
		    	//no related output, then don't record this bill in db at all.
		    	sql = new StringBuilder("delete from bill where id = ").append(billID);
		    	PIMDBModel.getStatement().executeUpdate(sql.toString());
		    	
		    } else { 						//if already has output.
		        //check if bill is already closed.
		        if(billPanel.status >= DBConsts.completed || billPanel.status < DBConsts.original) {
		        	JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.ClosedBillCantVoid());
		        	return;
		        }
		        
		        //check if there's already send dish, give different warning message and remove them from panel.
		        if(billPanel.getNewDishes().size() < dishLength) {	//not all new
		    		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
		    				BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
		                 return;	
		            }
		    		//if it's voiding a check printed bill, then we will regenerat a bill base on it, and set the regenerated bill as printed instead of original.
		    		if(billPanel.status >= DBConsts.billPrinted || billPanel.status < DBConsts.original) {
			    		if(!billPanel.checkStatus()) {	//this will regenerate a bill, but the new generated bill is original status. status will be unnecessarily checked again in
			    			return;						//method checkStatus(), but it dosn't harm, just some cpu time, so let it check again.
			    		}else {	//@NOTE if a new bill created, we want to set the status to be printed, so when it's print bill later, it will not send cancel info to kitchen.
					    	billPanel.status = DBConsts.billPrinted; //temperally set should be OK, because later in this method, will set status to void.
			    		}
		    		}
		        }else {												//all new
		        	if(JOptionPane.showConfirmDialog(BarFrame.instance, 
		    				BarFrame.consts.COMFIRMLOSTACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
		                 return;	
		            }
		        }
		        
		        //clean the unsend items from billPanel first.
				for(int i = billPanel.orderedDishAry.size() - 1; i >= 0; i--) {
		    		if(billPanel.orderedDishAry.get(i).getOutputID() <= 0) {
		    			billPanel.orderedDishAry.remove(i);
		        	}
				}

		        //then mark all dishes which already send
		    	for (Dish dish : billPanel.orderedDishAry) {
		    		dish.setCanceled(true);	// to make it printed in special format(so it's know as a cancelled dish)
				}
		    	
		    	//print a final receipt or notice kitchen to stop preparing.
		    	if(billPanel.status >= DBConsts.billPrinted || billPanel.status < DBConsts.original) {		//if bill printed, print a refund bill.
		    		PrintService.exePrintVoid(billPanel);
		    	}else if(billPanel.orderedDishAry.size() > 0) { 	//otherwise, tell kitchen to stop preparing.
		    		billPanel.sendDishesToKitchen(billPanel.orderedDishAry, true);
		    	}
		    	
		    	//@NOTE: we need to process cur bill, give it a special status, so we can see the voided bills in check order dialog. 
		    	//and have to process it to be not null, better will not be considered as there's still non closed bill, when checking in isLastBill()
		    	//update bill
				sql = new StringBuilder("update bill set status = ").append(DBConsts.voided)
						.append(" where billIndex = '").append(curBill).append("'")
						.append(" and openTime = '").append(BarFrame.instance.valStartTime.getText()).append("'");
		    	PIMDBModel.getStatement().executeQuery(sql.toString());
		    	//update output
		    	sql = new StringBuilder("update output set deleted = ").append(DBConsts.voided)
		    			.append(" where contactID = ").append(curBill)
		    			.append(" and time = '").append(BarFrame.instance.valStartTime.getText()).append("'");
		        PIMDBModel.getStatement().executeQuery(sql.toString());
		        
		    }
		}catch(Exception exp) {
			L.e("void order", "error happend when voiding a bill with ID:"+ billID, exp);
		}
		
		if(BarOption.isFastFoodMode()) {
			String tableName = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
			String newOpenTime = BarOption.df.format(new Date());
			int newBillIdx = BillListPanel.getANewBillIdx(tableName, newOpenTime);
			BarFrame.instance.valStartTime.setText(newOpenTime);
			BarFrame.instance.setCurBillIdx(String.valueOf(newBillIdx));
			
			billPanel.setBillID(BarFrame.instance.createAnEmptyBill(tableName, newOpenTime, newBillIdx));
			billPanel.initContent();
			
		}else {
			//if the bill amount is 1, cancel the selected status of the table.
			if(BarFrame.instance.isTableEmpty(null, null)) {
				BarFrame.instance.closeATable(null, null);
			}
			BarFrame.instance.switchMode(0);
		}
	}
}
