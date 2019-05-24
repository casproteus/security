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
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class Cmd_SuspendAll implements  SamActionListener {

	private static Cmd_SuspendAll instance;
	private Cmd_SuspendAll() {}
	public static Cmd_SuspendAll getInstance() {
		if(instance == null)
			instance = new Cmd_SuspendAll();
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

		BillListPanel billListPanel = (BillListPanel)BarFrame.instance.panels[1];
		
		if(!billListPanel.checkColosedBill()) {
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
			
        	//update the tabel status
			BarFrame.instance.closeATable(tableID, null);
        }catch(Exception exp) {
        	ErrorUtil.write(exp);
        }
        
		BarFrame.instance.setCurBillIdx("");
		BarFrame.instance.switchMode(0);
		
	}
}
