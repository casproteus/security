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
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;

public class Cmd_CancelAll implements SamActionListener {

	private static Cmd_CancelAll instance;
	private Cmd_CancelAll() {}
	public static Cmd_CancelAll getInstance() {
		if(instance == null)
			instance = new Cmd_CancelAll();
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
		// cancel all---- if bill is empty, then check if table is empty, if yes, close current table. yes or not, all back to table view.
		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		if(billPanel.orderedDishAry.size() > 0) {	//if not empty, remove all new added items.
    		int newDishQT = billPanel.getNewDishes().size();
    		if(newDishQT == 0) {
    			if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.NoNewSelectionToCancel(), DlgConst.DlgTitle,
	                    JOptionPane.YES_NO_OPTION) != 0) {
    				return;
    			}else {
    				Cmd_VoidOrder.getInstance().actionPerformed(null);
    			}
    		}
    		
    		int lastSavedRow = billPanel.orderedDishAry.size() - 1 - newDishQT;
    		
    		//update array first.
    		for(int i = billPanel.orderedDishAry.size() - 1; i > lastSavedRow; i--) {
    			billPanel.orderedDishAry.remove(i);
    		}
    		//update the table view
    		int tColCount = billPanel.table.getColumnCount();
    		int tValidRowCount = billPanel.orderedDishAry.size(); // get the used RowCount
    		Object[][] tValues = new Object[tValidRowCount][tColCount];
    		for (int r = 0; r < tValidRowCount; r++) {
    			for (int c = 0; c < tColCount; c++)
    				tValues[r][c] = billPanel.table.getValueAt(r, c);
    		}
    		billPanel.table.setDataVector(tValues, billPanel.header);
    		billPanel.resetColWidth(billPanel.getWidth());

            if(BarFrame.secondScreen != null) {
            	BarFrame.customerFrame.billPanel.resetColWidth(CustOpts.SCRWIDTH/2 - CustOpts.HOR_GAP * 4);
            }
    		billPanel.table.setSelectedRow(tValues.length - 1);
    		billPanel.updateTotalArea();
    	}else {
    		//if it's default table, then increse billidx, otherwise, if it counter mode, switch to other table, if not, switch view.
    		//close the table if not living bill of course.
        	BarFrame.instance.actionAfterBillClosed(); 
    	}
    	
	}
}
