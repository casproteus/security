package org.cas.client.platform.bar.beans;

import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class FunctionButton extends JButton {
	
	public FunctionButton(String title) {
		super(title);
		setBackground(BarOption.getBK("Function"));
	}
}
