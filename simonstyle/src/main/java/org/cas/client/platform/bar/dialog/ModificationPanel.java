package org.cas.client.platform.bar.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;

public class ModificationPanel extends PIMScrollPane implements ActionListener {
	
	private static ModificationPanel instance = null;
	public static ModificationPanel getInstance() {
		if(instance == null) {
			instance = new ModificationPanel();
		}
		return instance;
	}
	

	public ModificationPanel() {
		initComponent();
	}

	public void initComponent() {
		validate();
		repaint();
	}
    
	@Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
	}
	
	public void initContent(String modification, int i) {
		// TODO Auto-generated method stub
		
	}
}
