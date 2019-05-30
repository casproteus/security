package org.cas.client.platform.bar.uibeans;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.cas.client.platform.casutil.L;

public class FunctionToggleButton extends JToggleButton implements ISButton{
	
	private String title;
	private SamActionListener actionListener;
	
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
		if(actionListener == null) {
			L.e("FunctionToggleButton", "receieved a null as actionListener", null);
		}
		super.addActionListener(actionListener);
		this.actionListener = (SamActionListener)actionListener;
		this.actionListener.setSourceBtn(this);
	}
	
	public JComponent clone() {
		FunctionToggleButton btn = new FunctionToggleButton(title);
		btn.addActionListener(actionListener);
		if(actionListener == null) {
			L.e("FunctionToggleButton", "actionListener is null!", null);
		}
		actionListener.setSourceBtn(btn);
		return btn;
	}
}
