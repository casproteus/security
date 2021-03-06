package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.NumberPanelDlg;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.resource.international.DlgConst;

public class Cmd_ServiceFee implements SamActionListener {

	private static Cmd_ServiceFee instance;
	private Cmd_ServiceFee() {}
	public static Cmd_ServiceFee getInstance() {
		if(instance == null)
			instance = new Cmd_ServiceFee();
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
		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.ServiceFee());
 		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.ServiceFeeNotice());
		BarFrame.numberPanelDlg.setBtnSource(null);
 		BarFrame.numberPanelDlg.setFloatSupport(true);
 		BarFrame.numberPanelDlg.setPercentSupport(true);
 		
 		BarFrame.numberPanelDlg.setModal(true);
		BarFrame.numberPanelDlg.reLayout();
 		BarFrame.numberPanelDlg.setVisible(true);

		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
 		try {
				String curContent = NumberPanelDlg.curContent;
				if(curContent == null || curContent.length() == 0) {
					return;
				}
				if(!billPanel.checkStatus()) {
					return;
				}
				billPanel.serviceFee  = BarFrame.numberPanelDlg.isPercentage ? 
     				Math.round(Float.valueOf((billPanel.subTotal - billPanel.serviceFee + billPanel.discount) * Float.valueOf(curContent)))
     				: Math.round(Float.valueOf(curContent) * 100);
     				
     		billPanel.updateTotalArea();
     		
     		billPanel.createAndPrintNewOutput();
     		billPanel.billPricesUpdateToDB();
     		
     	}catch(Exception exp) {
         	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
     		return;
     	}
		
	}
}
