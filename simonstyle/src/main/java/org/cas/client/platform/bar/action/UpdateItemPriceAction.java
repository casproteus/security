package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class UpdateItemPriceAction implements ActionListener{

	JToggleButton btn;
	BillPanel billPanel;
	
	public UpdateItemPriceAction(JToggleButton button, BillPanel billPanel) {
		this.btn = button;
		this.billPanel = billPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(btn.isSelected()) {
 			try {
             	int row = billPanel.tblBillPanel.getSelectedRow();
         		Dish dish = billPanel.orderedDishAry.get(row);
         		float price = 0;
 				String curContent = BarFrame.numberPanelDlg.tfdQTY.getText();
            	if(curContent.endsWith("%")) {
                	String tContent = curContent.substring(0, curContent.length() - 1);
            		Float f = Float.valueOf(tContent);
            		curContent = String.valueOf(f/100f);
         			price = dish.getPrice() * Float.valueOf(curContent)/100f;
            	}else {
            		Float.valueOf(curContent);
         			price = Float.valueOf(curContent);
            	}
         		
             	dish.setPrice((int)(price * 100));

 				int num = dish.getNum();
 				int pK = num /(BarOption.MaxQTY * 100);
 	    		if(num > BarOption.MaxQTY * 100) {
 	    			num = num %(BarOption.MaxQTY * 100);
 	    		}
 	    		int pS = num /BarOption.MaxQTY;
 	    		if(num > BarOption.MaxQTY) {
 	    			num = num % BarOption.MaxQTY;
 	    		}
 	    		
 	    		int priceDSP = dish.getPrice() * num;
 	    		if(pS > 0)
 	    			priceDSP /= pS;
 	    		if(pK > 0)
 	    			priceDSP /= pK;
 	    		
             	billPanel.tblBillPanel.setValueAt(BarOption.getMoneySign() + priceDSP/100f, row, 3);
             	billPanel.updateTotleArea();
             	int outputID = billPanel.orderedDishAry.get(row).getOutputID();
             	if(outputID >= 0) {
             		String sql = "update output set TOLTALPRICE = "
             				+ (dish.getPrice() - dish.getDiscount()) + " where id = " + outputID;
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
