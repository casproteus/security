package org.cas.client.platform.bar.beans;

import java.awt.Color;

import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class FunctionButton extends JButton {
	
	public FunctionButton(String title) {
		super(title);
		Color bg = BarOption.getBK("Function");
    	if(bg == null) {
    		bg = new Color(153,153,255);
    	}
		setBackground(bg);
	}
}
