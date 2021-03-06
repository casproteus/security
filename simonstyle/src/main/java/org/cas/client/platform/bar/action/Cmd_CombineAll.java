package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.bar.uibeans.FunctionToggleButton;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.hsqldb.lib.HashMap;

public class Cmd_CombineAll implements SamActionListener {

	private static Cmd_CombineAll instance;
	private Cmd_CombineAll() {}
	public static Cmd_CombineAll getInstance() {
		if(instance == null)
			instance = new Cmd_CombineAll();
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
		this.sourceBtn = (FunctionButton)e.getSource();
		//@note should consider the time, incase there's some bill not paid before, while was calculated into current client.

		BillListPanel billListPanel = (BillListPanel)BarFrame.instance.panels[1];
		
		if(sourceBtn.getText().equals(BarFrame.consts.CombineAll())) {
			if(!billListPanel.checkColosedBill()) {
				return;
			}
			
			//check if all bills are not closed
			ArrayList<BillPanel> unclosedBillPanels = billListPanel.gatherAllUnclosedBillPanels();
			billListPanel.combineBills(unclosedBillPanels);
		}
		
		//for uncombine action.
		else {
			HashMap idxMap = new HashMap();
			StringBuilder sql = new StringBuilder("select * from bill where tableId = '").append(BarFrame.instance.cmbCurTable.getSelectedItem().toString()).append("'")
					.append(" and opentime = '").append(BarFrame.instance.valStartTime.getText()).append("'")
					.append(" and (status is null or status < ").append(DBConsts.completed).append(")");
			try {
				ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
	            rs.beforeFirst();
	            while (rs.next()) {
	                idxMap.put(rs.getInt("id"), rs.getInt("billIndex"));
	            }
			}catch(Exception exp) {
				L.e("BillListPanel", "exception when change output back to original bill" + sql, exp);
			}
			
			//get the unclosed billPane.
			ArrayList<Dish> orderedDishes = new ArrayList<Dish>();
			for (BillPanel billPanel : billListPanel.billPanels) {
				if(billPanel.status < DBConsts.completed) {
					orderedDishes = billPanel.orderedDishAry;
					break;
				}
			}
			
			for (Dish dish : orderedDishes) {
				Object idx = idxMap.get(dish.getBillID());
				if(idx != null) {
					sql = new StringBuilder("update output set contactID = ").append(idx)
							.append(" where id = ").append(dish.getOutputID());
					try {
						PIMDBModel.getStatement().execute(sql.toString());
					}catch(Exception exp) {
						L.e("BillListPanel", "exception when change output back to original bill" + sql, exp);
					}
				}
			}
			billListPanel.initContent();
		}
	}
}
