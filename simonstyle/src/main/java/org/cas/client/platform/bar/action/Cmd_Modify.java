package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.MenuPanel;
import org.cas.client.platform.bar.dialog.ModificationPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;

public class Cmd_Modify implements SamActionListener {

	private static Cmd_Modify instance;
	private Cmd_Modify() {}
	public static Cmd_Modify getInstance() {
		if(instance == null)
			instance = new Cmd_Modify();
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
		if(BillListPanel.curDish == null) {
			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.AtLeastOneShouldBeSelected());
			return;
		}
		//if already send, then do not allow to modify the marks.
		if(BillListPanel.curDish.getOutputID() > 0) {
			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.SendItemCanNotModify());
			return;
		}
		
		//if there's a curDish?
		if(BillListPanel.curDish == null) {//check if there's an item selected.
			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
			return;
		}
		ModificationPanel.getInstance().initContent(BillListPanel.curDish.getModification(), 0);
		ModificationPanel.getInstance().setVisible(true);
		BarFrame.instance.menuPanel.setVisible(false);
	}
}
