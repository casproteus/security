package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.uibeans.FunctionButton;

public class Cmd_QTY implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		
		JToggleButton btnQTY = (JToggleButton)e.getSource();
		
 		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.QTY());
 		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.QTYNOTICE());
		BarFrame.instance.numberPanelDlg.setBtnSource(btnQTY);//pomp up a numberPanelDlg
 		BarFrame.numberPanelDlg.setFloatSupport(false);
 		BarFrame.numberPanelDlg.setPercentSupport(false);
 		BarFrame.numberPanelDlg.setModal(true);
		//should no record selected, select the last one.
		BarFrame.instance.numberPanelDlg.setVisible(btnQTY.isSelected());	//@NOTE: it's not model mode.
		if(btnQTY.isSelected()) {
			try {
				String curContent = BarFrame.instance.numberPanelDlg.curContent;
        		int tQTY = Integer.valueOf(curContent);
            	int row = billPanel.table.getSelectedRow();
            	billPanel.table.setValueAt(curContent + "x", row, 0);
            	billPanel.orderedDishAry.get(row).setNum(tQTY);
            	billPanel.updateTotleArea();
        	}catch(Exception exp) {
            	JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.FORMATERROR());
        		return;
        	}
		}
		if(billPanel.table.getSelectedRow() < 0) {
			billPanel.table.setSelectedRow(billPanel.table.getRowCount()-1);
		}
		//present the value in number dialog.
		Object obj = billPanel.table.getValueAt(billPanel.table.getSelectedRow(), 3);
		BarFrame.instance.numberPanelDlg.setContents(obj.toString());
	}
}
