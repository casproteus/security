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
            		BarFrame.discountDlg.isPercentage = true;	//NOTE：maybe ok's action not called yet, so the percentage flag not set yet.
                	String tContent = curContent.substring(0, curContent.length() - 1);
            		curContent = String.valueOf(Float.valueOf(tContent)/100f);
            	}else {
            		BarFrame.discountDlg.isPercentage = false;
            		Float.valueOf(curContent);
            	}

             	int row = billPanel.tblBillPanel.getSelectedRow();
             	//NOTE: current total price + existing discount = original total price.
             	String strDisCount = (String)billPanel.tblBillPanel.getValueAt(row, 2);
             	strDisCount = (strDisCount == null || strDisCount.length() == 0) ? "0" : strDisCount.trim().substring(2);//remove "-$"
             	float oldDiscount = Float.valueOf(strDisCount);
             	
             	String strTotalPrice = (String)billPanel.tblBillPanel.getValueAt(row, 3);
				strTotalPrice = strTotalPrice.trim().substring(1);
             	float totalPrice = Float.valueOf(strTotalPrice);
             	
             	float newDiscount = 0;
 				if(BarFrame.discountDlg.isPercentage) {
 					newDiscount = (totalPrice + oldDiscount) * Float.valueOf(curContent);
 				}else {
 					newDiscount = Float.valueOf(curContent);
 				}
 				
 				totalPrice = totalPrice + oldDiscount - newDiscount;
             	billPanel.tblBillPanel.setValueAt("-"+ BarOption.getMoneySign() + new DecimalFormat("#0.00").format(newDiscount), row, 2);
             	billPanel.tblBillPanel.setValueAt(BarOption.getMoneySign() + new DecimalFormat("#0.00").format(totalPrice), row, 3);
             	billPanel.orderedDishAry.get(row).setDiscount((int)(newDiscount * 100));
             	billPanel.orderedDishAry.get(row).setTotalPrice((int)((totalPrice) * 100));
             	
             	billPanel.updateTotleArea();
             	int outputID = billPanel.orderedDishAry.get(row).getOutputID();
             	if(outputID >= 0) {
             		String sql = "update output set discount = " + (int)(newDiscount * 100) + ", toltalprice = " + Math.round(totalPrice * 100) + " where id = " + outputID;
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
