package org.cas.client.platform.bar.uibeans;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class FunctionButton extends JButton{
	
	public FunctionButton(String title) {
		super(title);
		Color bg = BarOption.getBK("Function");
    	if(bg == null) {
    		bg = new Color(153,153,255);
    	}
		setBackground(bg);

		setBorder(BorderFactory.createEtchedBorder());
	}
}
