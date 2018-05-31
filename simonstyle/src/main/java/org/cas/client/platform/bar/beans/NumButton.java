package org.cas.client.platform.bar.beans;

import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class NumButton extends JButton {
	
	public NumButton(String title) {
		super(title);
		setBackground(BarOption.getBK("NumBtn"));
	}
}
