package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;
import org.cas.client.platform.bar.uibeans.FunctionToggleButton;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;

public class Cmd_DiscItem implements SamActionListener {
	private static Cmd_DiscItem instance;
	private Cmd_DiscItem() {}
	public static Cmd_DiscItem getInstance() {
		if(instance == null)
			instance = new Cmd_DiscItem();
		return instance;
	}
	
	ISButton sourceBtn;
	
	public ISButton getSourceBtn() {
		return sourceBtn;
	}

	@Override
	public void setSourceBtn(ISButton btn) {
		this.sourceBtn = sourceBtn;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.sourceBtn = (FunctionToggleButton)e.getSource();
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
 		BarFrame.discountDlg.setBtnSource(sourceBtn);//pomp up a discountDlg
 		BarFrame.discountDlg.setFloatSupport(true);
 		BarFrame.discountDlg.setPercentSupport(true);
 		BarFrame.discountDlg.setModal(false);
 		//should no record selected, select the last one.
 		BarFrame.discountDlg.setVisible(sourceBtn.isSelected());	//@NOTE: it's not model mode.
 		BarFrame.discountDlg.setAction(new UpdateItemDiscountAction(sourceBtn));
 		
	}
}
