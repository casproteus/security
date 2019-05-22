package org.cas.client.platform.bar.uibeans;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class FunctionToggleButton extends JToggleButton implements ISbutton{
	
	private String title;
	private ActionListener actionListener;
	
	public FunctionToggleButton(String title) {
		super(title);
		this.title = title;
//		Color bg = BarOption.getBK("Function");
//    	if(bg == null) {
//    		bg = new Color(153,153,255);
//    	}
//		setBackground(bg);

		setBorder(BorderFactory.createEtchedBorder());
	}
	
	public void addActionListener(ActionListener actionListener) {
		super.addActionListener(actionListener);
		this.actionListener = actionListener;
	}
	
	public JComponent clone() {
		FunctionToggleButton btn = new FunctionToggleButton(title);
		btn.addActionListener(actionListener);
		return btn;
	}
}