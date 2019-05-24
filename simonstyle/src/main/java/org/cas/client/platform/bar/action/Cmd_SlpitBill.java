package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class Cmd_SlpitBill implements  SamActionListener {

	private static Cmd_SlpitBill instance;
	private Cmd_SlpitBill() {}
	public static Cmd_SlpitBill getInstance() {
		if(instance == null)
			instance = new Cmd_SlpitBill();
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
		//check if there unsaved dish, and give warning.
		if(BillListPanel.curDish == null) {
    		if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.UnSavedRecordFound(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0)
    			return;
		}
		//check if it's countermode
		if(BarOption.isFastFoodMode()) {
			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.ActionNotAllowed());
			return;
		}
		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		List<Dish> newDishes = billPanel.getNewDishes();
		if(newDishes.size() > 0) {
			for (Dish dish : newDishes) {
				if(dish.getPrinter() != null && dish.getPrinter().length() > 1) {
        			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.UnSendRecordFound());
        			return;
				}
			}
		}
		
		billPanel.createAndPrintNewOutput();
		billPanel.billPricesUpdateToDB();
		
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
            }else {
            	return;
            }
		}
		BarFrame.instance.switchMode(1);
		
	}

}
