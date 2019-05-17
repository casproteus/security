package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.MoreButtonsDlg;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.uibeans.FunctionButton;

public class Cmd_More  implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		new MoreButtonsDlg((SalesPanel)BarFrame.instance.panels[2]).show((FunctionButton)o);
	}
}
