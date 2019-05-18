package org.cas.client.platform.bar.uibeans;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.MoreButtonsDlg;
import org.cas.client.platform.bar.dialog.SalesPanel;

public class MoreButton extends JButton implements ActionListener{
	
	public MoreButton(String title) {
		super(title);
		Color bg = BarOption.getBK("Function");
    	if(bg == null) {
    		bg = new Color(153,153,255);
    	}
		setBackground(bg);

		setBorder(BorderFactory.createEtchedBorder());
		addActionListener(this);
	}
	
	public void addButton(JComponent button) {
		buttons.add(button);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		new MoreButtonsDlg(buttons).show((MoreButton)o);
	}
	
	ArrayList<JComponent> buttons = new ArrayList<JComponent>();
}
