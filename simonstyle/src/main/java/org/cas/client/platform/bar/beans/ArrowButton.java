package org.cas.client.platform.bar.beans;

import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class ArrowButton extends JButton {
	public ArrowButton(String title) {
		super(title);
		setBackground(BarOption.getBK("Arrow"));
	}
}
