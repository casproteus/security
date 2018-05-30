package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.casutil.ErrorUtil;
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
 				String curContent = BarFrame.numberPanelDlg.tfdQTY.getText();
         		float discount = Float.valueOf(curContent);
             	int row = billPanel.tblSelectedDish.getSelectedRow();
             	billPanel.tblSelectedDish.setValueAt("-"+ BarOption.getMoneySign() + curContent, row, 2);
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
