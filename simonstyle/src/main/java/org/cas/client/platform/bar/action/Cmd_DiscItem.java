package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;

public class Cmd_DiscItem implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(BillListPanel.curDish == null) {//check if there's an item selected.
 			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
 			return;
 		}
 		
 		if(!BarOption.isWaiterAllowedToDiscount()) {
    		if (!BarFrame.instance.adminAuthentication()) 
				return;
    	}
 		
 		BarFrame.discountDlg.setTitle(BarFrame.consts.DISCITEM());
 		BarFrame.discountDlg.setNotice(BarFrame.consts.DISC_ITEMNotice());
 		BarFrame.discountDlg.setBtnSource(CmdBtnsDlg.btnDiscItem);//pomp up a discountDlg
 		BarFrame.discountDlg.setFloatSupport(true);
 		BarFrame.discountDlg.setPercentSupport(true);
 		BarFrame.discountDlg.setModal(false);
 		//should no record selected, select the last one.
 		BarFrame.discountDlg.setVisible(CmdBtnsDlg.btnDiscItem.isSelected());	//@NOTE: it's not model mode.
 		BarFrame.discountDlg.setAction(new UpdateItemDiscountAction(CmdBtnsDlg.btnDiscItem));
 		
	}
}
