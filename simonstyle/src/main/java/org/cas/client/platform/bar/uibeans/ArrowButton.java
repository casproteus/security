package org.cas.client.platform.bar.uibeans;

import java.awt.Color;

import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class ArrowButton extends JButton {
	public ArrowButton(String title) {
		super(title);
		Color bg = BarOption.getBK("Arrow");
    	if(bg == null) {
    		//bg = new Color(8,204,8);
    	}
		setBackground(bg);
	}
}
