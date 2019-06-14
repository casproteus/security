package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class Cmd_Return implements SamActionListener {

	private static Cmd_Return instance;
	private Cmd_Return() {}
	public static Cmd_Return getInstance() {
		if(instance == null)
			instance = new Cmd_Return();
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
		
		if(billPanel.getNewDishes().size() > 0) {
    		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
    				BarFrame.consts.COMFIRMLOSTACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
                return;	
            }else {
    			//call cancel all
    			Cmd_CancelAll.getInstance().actionPerformed(null);
            }
    	}

    	if(BarOption.isCounterMode()) {
    		BarFrame.instance.setVisible(false);
			BarFrame.singleUserLoginProcess();
			BarFrame.instance.ignoreLogin = true;
			//get lateset bill
			StringBuilder sql = new StringBuilder("select * from bill where employeeID = ");
			sql.append(LoginDlg.USERID);
			sql.append(" order by id DESC limit 1");
			try {
				ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
	        	if(rs.next()) {
	        		int status = rs.getInt("status");
	        		if(status < DBConsts.suspended && status >= DBConsts.original) {
	        	        BarFrame.instance.ignoreItemChange = true;
	        	    	BarFrame.instance.cmbCurTable.setSelectedItem(rs.getString("tableID"));
	        	    	BarFrame.instance.setCurBillIdx(rs.getString("billIndex"));
	        	    	BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
	        	    	BarFrame.instance.valStartTime.setText(rs.getString("OPENTIME"));

	        	    	billPanel.setBillID(rs.getInt("id"));
	        	    	
	        	    	//if this flag set, the initContent will choose outputs and bill differently.
	        	    	//NOTE: there's could be one final and several expired bills under same tableid and billIdx and opentime. we don't support more than one exipred bill.
	        	    	BarFrame.instance.isShowingAnExpiredBill = false;
	        	    	BarFrame.instance.curBillID = rs.getInt("id");
	        	    	BarFrame.instance.switchMode(2);

	        		}else {
	        			initAnEmptyBill(billPanel);
	        		}
	        	}else {	//if has no bill with this employee, then create a new bill?
	        		initAnEmptyBill(billPanel);
	        	}
			}catch (SQLException exp) {
	            ErrorUtil.write(exp);
	        }
			BarFrame.instance.ignoreLogin = false;
			//if it's completed, then createa a new empty onee.
			//if it's not completed. this it's it.
    	}else {
        	if(salesPanel.partialPaid) {
    			if(JOptionPane.showConfirmDialog(BarFrame.instance, 
	    				BarFrame.consts.COMFIRMCLEARMONEYRECEIVED(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) == 0) {
    				//check if the otehr receivement is not null, add back to gift card.
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
	private void initAnEmptyBill(BillPanel billPanel) {
		BarFrame.instance.ignoreItemChange = true;
		BarFrame.instance.cmbCurTable.setSelectedItem("");
		BarFrame.instance.setCurBillIdx("1");
		
		String openTime = BarOption.df.format(new Date());
		BarFrame.instance.valStartTime.setText(openTime);

		BarFrame.instance.openATable("", openTime);
		BarFrame.instance.curBillID = BarFrame.instance.createAnEmptyBill("", openTime, 0);
		billPanel.setBillID(BarFrame.instance.curBillID);
		
		BarFrame.instance.switchMode(2);
	}
}
