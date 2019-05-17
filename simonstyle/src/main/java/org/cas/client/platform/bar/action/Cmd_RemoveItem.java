package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;

public class Cmd_RemoveItem  implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
		BillPanel billPanel = salesPanel.billPanel;
		if(!billPanel.checkStatus()) {
			return;
		}
		salesPanel.removeItem();
	}
}
