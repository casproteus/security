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

public class Cmd_ChangePrice  implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if(BillListPanel.curDish == null) {//check if there's an item selected.
  			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
  			return;
  		}
    	if(!BarOption.isWaiterAllowedToChangePrice()) {
    		if (!BarFrame.instance.adminAuthentication()) 
				return;
    	}
    	BarFrame.instance.showPriceChangeDlg();
	}
}
