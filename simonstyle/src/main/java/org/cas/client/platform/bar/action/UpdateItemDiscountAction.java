package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class UpdateItemDiscountAction implements ActionListener{

	JToggleButton btn;
	
	public UpdateItemDiscountAction(JToggleButton button) {
		this.btn = button;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(btn.isSelected()) {

			SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
			BillPanel billPanel = salesPanel.billPanel;
			
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

             	int row = billPanel.table.getSelectedRow();
             	//NOTE: current total price + existing discount = original total price.
             	String strDisCount = (String)billPanel.table.getValueAt(row, 2);
             	strDisCount = (strDisCount == null || strDisCount.length() == 0) ? "0" : strDisCount.trim().substring(2);//remove "-$"
             	float oldDiscount = Float.valueOf(strDisCount);
             	
             	String strTotalPrice = (String)billPanel.table.getValueAt(row, 3);
				strTotalPrice = strTotalPrice.trim().substring(1);
             	float totalPrice = Float.valueOf(strTotalPrice);
             	
             	float newDiscount = 0;
 				if(BarFrame.discountDlg.isPercentage) {
 					newDiscount = (totalPrice + oldDiscount) * Float.valueOf(curContent);
 				}else {
 					newDiscount = Float.valueOf(curContent);
 				}
 				
 				totalPrice = totalPrice + oldDiscount - newDiscount;
             	billPanel.table.setValueAt("-"+ BarOption.getMoneySign() + BarUtil.formatMoney(newDiscount), row, 2);
             	billPanel.table.setValueAt(BarOption.getMoneySign() + BarUtil.formatMoney(totalPrice), row, 3);
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
