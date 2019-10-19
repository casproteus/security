package org.cas.client.platform.bar.uibeans;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.cas.client.platform.bar.dialog.BarOption;

public class FunctionButton extends JButton implements ISButton{
	
	String title;
	SamActionListener actionListener;
	
	public FunctionButton(String title) {
		super(title);
		this.title = title;
		Color bg = BarOption.getBK("Function");
    	if(bg == null) {
    		bg = new Color(153,153,255);
    	}
		setBackground(bg);
		setFont(new Font("Arial", Font.PLAIN, BarOption.getCmdCustFontSize()));
		setBorder(BorderFactory.createEtchedBorder());
	}

	public void addActionListener(SamActionListener actionListener) {
		super.addActionListener(actionListener);
		this.actionListener = actionListener;
		actionListener.setSourceBtn(this);
	}
	
	public JComponent clone() {
		FunctionButton btn = new FunctionButton(title);
		btn.addActionListener(actionListener);
		actionListener.setSourceBtn(btn);
		return btn;
	}
	
}
