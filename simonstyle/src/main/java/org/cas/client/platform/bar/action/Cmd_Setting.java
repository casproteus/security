package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.resource.international.DlgConst;

public class Cmd_Setting implements SamActionListener {

	private static Cmd_Setting instance;
	private Cmd_Setting() {}
	public static Cmd_Setting getInstance() {
		if(instance == null)
			instance = new Cmd_Setting();
		return instance;
	}
	
	private ISButton sourceBtn;
	
	public ISButton getSourceBtn() {
		return sourceBtn;
	}
	@Override
	public void setSourceBtn(ISButton sourceBtn) {
		this.sourceBtn = sourceBtn;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		if(billPanel.getNewDishes().size() > 0) {
    		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
    				BarFrame.consts.COMFIRMLOSTACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
                return;	
            }else {
    			//call cancel all
    			Cmd_CancelAll.getInstance().actionPerformed(null);
            }
    	}
		BarFrame.instance.switchMode(3);
	}
}
