package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.AbstractButton;
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
 				String curContent = BarFrame.discountDlg.tfdQTY.getText();
            	if(curContent.endsWith("%")) {
                	String tContent = curContent.substring(0, curContent.length() - 1);
            		Float f = Float.valueOf(tContent);
            		curContent = String.valueOf(f/100f);
            	}else {
            		Float.valueOf(curContent);
            	}

             	int row = billPanel.tblBillPanel.getSelectedRow();
             	float discount = 0;
             	String strTotalPrice = (String)billPanel.tblBillPanel.getValueAt(row, 3);
				strTotalPrice = strTotalPrice.trim().substring(1);
             	float totalPrice = Float.valueOf(strTotalPrice);
 				if(BarFrame.numberPanelDlg.isPercentage) {
 					discount = totalPrice * Float.valueOf(curContent);
 				}else {
 					discount = Float.valueOf(curContent);
 				}
 				
             	billPanel.tblBillPanel.setValueAt("-"+ BarOption.getMoneySign() + new DecimalFormat("#0.00").format(discount), row, 2);
             	
             	billPanel.orderedDishAry.get(row).setDiscount((int)(discount * 100));
             	
             	billPanel.updateTotleArea();
             	int outputID = billPanel.orderedDishAry.get(row).getOutputID();
             	if(outputID >= 0) {
             		String sql = "update output set discount = " + (int)(discount * 100) + " and totalprice = " + totalPrice + " where id = " + outputID;
             		PIMDBModel.getStatement().executeUpdate(sql);
             	}
         	}catch(Exception exp) {
             	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
         		return;
         	}
        	((AbstractButton)e.getSource()).removeActionListener(this);
 		}
	}
}
