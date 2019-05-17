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
import org.cas.client.resource.international.DlgConst;

public class Cmd_DiscBill  implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		if(!BarOption.isWaiterAllowedToDiscount()) {
    		if (!BarFrame.instance.adminAuthentication()) 
				return;
    	}
    	
 		BarFrame.discountDlg.setTitle(BarFrame.consts.DISCOUNT_BILL());
 		BarFrame.discountDlg.setNotice(BarFrame.consts.VolumnDiscountNotice());
 		BarFrame.discountDlg.setBtnSource(null);
 		BarFrame.discountDlg.setFloatSupport(true);
 		BarFrame.discountDlg.setPercentSupport(true);
 		BarFrame.discountDlg.setModal(true);
 		BarFrame.discountDlg.setVisible(true);
 		
 		try {
				String curContent = BarFrame.discountDlg.curContent;
				if(curContent == null || curContent.length() == 0)
					return;
     		float discount = BarFrame.discountDlg.isPercentage ? 
     				(billPanel.subTotal + billPanel.discount) * (Float.valueOf(curContent)/100f)
     				: Float.valueOf(curContent);
     		
     		salesPanel.discountBill(Math.round(discount * 100));
     		
     	}catch(Exception exp) {
         	JOptionPane.showMessageDialog(BarFrame.discountDlg, DlgConst.FORMATERROR);
     		return;
     	}
	}
}
