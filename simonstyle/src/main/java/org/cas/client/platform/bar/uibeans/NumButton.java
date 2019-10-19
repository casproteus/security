package org.cas.client.platform.bar.uibeans;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class NumButton extends JButton {
	
	public NumButton(String title) {
		super(title);
		Color bg = BarOption.getBK("NumBtn");
    	if(bg == null) {
    		bg = new Color(50,97,141);
    	}
		setBackground(bg);
		setFont(BarOption.lessBigFont);
		setBorder(BorderFactory.createEtchedBorder());
	}
}
