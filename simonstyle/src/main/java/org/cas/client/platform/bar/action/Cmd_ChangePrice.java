package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;
import org.cas.client.platform.bar.dialog.NumberPanelDlg;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.uibeans.FunctionToggleButton;

public class Cmd_ChangePrice implements ActionListener {
	
	private static Cmd_ChangePrice instance = new Cmd_ChangePrice();
	private Cmd_ChangePrice() {}
	public static Cmd_ChangePrice getInstance() {
		if(instance == null) {
			instance = new Cmd_ChangePrice();
		}
		return instance;
	}

	private FunctionToggleButton sourceBtn;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.sourceBtn = (FunctionToggleButton)e.getSource();
		if(BillListPanel.curDish == null) {//check if there's an item selected.
  			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
  			return;
  		}
    	if(!BarOption.isWaiterAllowedToChangePrice()) {
    		if (!BarFrame.instance.adminAuthentication()) 
				return;
    	}
    	showPriceChangeDlg(sourceBtn);
	}
	
	public void showPriceChangeDlg(FunctionToggleButton sourceBtn) {
		this.sourceBtn = sourceBtn;
		NumberPanelDlg numberPanelDlg = BarFrame.numberPanelDlg;
		numberPanelDlg.setTitle(BarFrame.consts.CHANGEPRICE());
		numberPanelDlg.setNotice(BarFrame.consts.ChangePriceNotice());
		numberPanelDlg.setBtnSource(sourceBtn);//pomp up a numberPanelDlg
		numberPanelDlg.setFloatSupport(true);
		numberPanelDlg.setPercentSupport(false);
		numberPanelDlg.setModal(false);
		
		if(sourceBtn == null) {		//when added a "change price dish" onto the billPanel frome menu panel, will show dlg with null as sourcebtn.
			numberPanelDlg.setVisible(true);
		}else {
			sourceBtn.setSelected(true);
			numberPanelDlg.setVisible(sourceBtn.isSelected());	//@NOTE: it's not model mode.
		}
		numberPanelDlg.setAction(new UpdateItemPriceAction(sourceBtn));
	}

	public FunctionToggleButton getSourceBtn() {
		return sourceBtn;
	}

	public void setSourceBtn(FunctionToggleButton sourceBtn) {
		this.sourceBtn = sourceBtn;
	}

}
