package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.SettingPrinterDlg;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;

public class Cmd_Printer implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		new SettingPrinterDlg(BarFrame.instance).setVisible(true);
	}
}
