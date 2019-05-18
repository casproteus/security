package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;

public class Cmd_CheckInOut implements ActionListener {

	private static Cmd_CheckInOut instance;
	
	private Cmd_CheckInOut() {}
	
	public static Cmd_CheckInOut getInstance() {
		if(instance == null) {
			instance = new Cmd_CheckInOut();
		}
		return instance;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		BarFrame.instance.userCheckOut();
	}
}
