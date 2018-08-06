package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class UpdateItemDiscountAction implements ActionListener{

	JToggleButton btn;
	BillPanel billPanel;
	
	public UpdateItemDiscountAction(JToggleButton button, BillPanel billPanel) {
		this.btn = button;
		this.billPanel = billPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(btn.isSelected()) {
 			try {
 				String curContent = BarFrame.numberPanelDlg.curContent;
 				
             	int row = billPanel.tblBillPanel.getSelectedRow();
             	float discount = 0;
 				if(BarFrame.numberPanelDlg.isPercentage) {
 					String price = (String)billPanel.tblBillPanel.getValueAt(row, 3);
 					price = price.trim().substring(1);
 					discount = Float.valueOf(price) * Float.valueOf(curContent);
 				}else {
 					discount = Float.valueOf(curContent);
 				}
 				
             	billPanel.tblBillPanel.setValueAt("-"+ BarOption.getMoneySign() + curContent, row, 2);
             	billPanel.orderedDishAry.get(row).setDiscount((int)(discount * 100));
             	billPanel.updateTotleArea();
             	int outputID = billPanel.orderedDishAry.get(row).getOutputID();
             	if(outputID >= 0) {
             		String sql = "update output set discount = " + (int)(discount * 100) + " where id = " + outputID;
             		PIMDBModel.getStatement().execute(sql);
             	}
         	}catch(Exception exp) {
             	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
         		return;
         	}
 		}
	}
}
