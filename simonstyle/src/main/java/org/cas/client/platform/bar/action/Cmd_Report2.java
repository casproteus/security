package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.dialog.statistics.ReportDlg;

public class Cmd_Report2 implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// enter the setting mode.(admin interface)
    	//this.setVisible(false);
    	ReportDlg dlg = new ReportDlg(BarFrame.instance);
		dlg.setVisible(true);
    	
	}
}
