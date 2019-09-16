package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.uibeans.FunctionToggleButton;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;

public class Cmd_SplitItem implements SamActionListener {

	private static Cmd_SplitItem instance;
	private Cmd_SplitItem() {}
	public static Cmd_SplitItem getInstance() {
		if(instance == null)
			instance = new Cmd_SplitItem();
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
		this.sourceBtn = (FunctionToggleButton)e.getSource();
		BillListPanel billListPanel = (BillListPanel)BarFrame.instance.panels[1];
		
		if(sourceBtn.isSelected()) {//select
			//check if there's one item selected.
			if(billListPanel.curDish == null) {
				JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
				sourceBtn.setSelected(false);
			}
		}else {
		
			// unselect: if here reached, there must be curDish.
			// remove the bill where the curDish is. @because sometimes that bill might be
			// unselected.
			List<BillPanel> selectedPanels = billListPanel.getSelectedBillPannels();
			for (BillPanel billPanel : selectedPanels) { // remove the original panel from the list.
				if (billPanel.billButton.getText().equals(billListPanel.curDish.getBillIndex())) {
					selectedPanels.remove(billPanel);
					break;
				}
			}
			if(selectedPanels.size() == 0) {
				JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.NoBillSeleted());
				return;
			}
			Dish.splitOutput(billListPanel.curDish, selectedPanels.size() + 1, null); // update the num and totalprice of curDish
			for (BillPanel billPanel : selectedPanels) { // insert new output with other billID
				int billIndex = BillListPanel.getANewBillIdx(null, null);
				//generate a bill for each new occupied panel, incase there's discount info need to set into it.
				//@Note, when the initContent of the panel called, the bill ID will be set into the dish instance in memory.
				//and eventually, if the bill id is not 0, will calculate the service fee and discount into Total.
				int id = BarFrame.instance.createAnEmptyBill(
						BarFrame.instance.cmbCurTable.getSelectedItem().toString(), 
						BarFrame.instance.valStartTime.getText(), 
						billIndex);
				Dish dish = billListPanel.curDish.clone();
				dish.setBillID(id);
				Dish.splitOutput(dish, selectedPanels.size() + 1, billPanel.billButton.getText());
			}

			billListPanel.curDish = null;
			billListPanel.initContent();
		}
	}
}
