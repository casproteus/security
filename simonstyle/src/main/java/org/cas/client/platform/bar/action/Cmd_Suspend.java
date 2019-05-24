package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class Cmd_Suspend implements SamActionListener {

	private static Cmd_Suspend instance;
	private Cmd_Suspend() {}
	public static Cmd_Suspend getInstance() {
		if(instance == null)
			instance = new Cmd_Suspend();
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
		
    	salesPanel.billPanel.createAndPrintNewOutput();
    	BillPanel.updateBillRecordPrices(salesPanel.billPanel);
    	
    	//this.setVisible(false);
    	if(salesPanel.billPanel.status > DBConsts.suspended || salesPanel.billPanel.status < DBConsts.original) {
			return;
		}
		
        try {
        	String tableID = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
        	//update outputs
			StringBuilder sql = new StringBuilder("update output set deleted = ").append(DBConsts.suspended)
	                .append(" where SUBJECT = '").append(tableID)
	                .append("' and time = '").append(BarFrame.instance.valStartTime.getText())
	                .append("' and (deleted is null or deleted = ").append(DBConsts.original).append(")");
			PIMDBModel.getStatement().executeUpdate(sql.toString());
			
			//update bills
			sql = new StringBuilder("update bill set status = ").append(DBConsts.suspended)
					.append(" where openTime = '").append(BarFrame.instance.valStartTime.getText())
					.append("' and (status is null or status = ").append(DBConsts.original).append(")");
			PIMDBModel.getStatement().executeUpdate(sql.toString());
			
        }catch(Exception exp) {
        	ErrorUtil.write(exp);
        }
        
    	if(BarOption.isFastFoodMode()) {
	    	BarFrame.instance.addNewBillInCurTable();
    	}else {
			BarFrame.instance.setCurBillIdx("");
			BarFrame.instance.switchMode(0);
    	}
		
	}
}
