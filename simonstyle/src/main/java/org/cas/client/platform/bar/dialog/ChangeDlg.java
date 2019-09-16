package org.cas.client.platform.bar.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import org.cas.client.platform.cascustomize.CustOpts;

public class ChangeDlg extends JDialog implements ComponentListener, ActionListener{
	
	public ChangeDlg(BarFrame parent, String change) {
		super(parent);
		setTitle(BarFrame.consts.Change());
		
		sep = new JSeparator();
		valChange = new JLabel(change);
		ok = new JButton("OK");

		valChange.setFont(BarOption.bigFont);
		
		getContentPane().setLayout(null);
		getContentPane().add(valChange);
		getContentPane().add(sep);
		getContentPane().add(ok);
		
		int labelWidth = valChange.getPreferredSize().width;
		int labelHeight = valChange.getPreferredSize().height;
		setBounds((BarFrame.instance.getWidth() - labelWidth - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2)/2, 
				(BarFrame.instance.getHeight() - labelHeight - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2 - CustOpts.BTN_HEIGHT - CustOpts.HOR_GAP * 2)/2, 
				labelWidth + CustOpts.SIZE_EDGE * 2 + CustOpts.HOR_GAP * 3 + 10,
				labelHeight + CustOpts.SIZE_EDGE * 2 + CustOpts.HOR_GAP * 2 + CustOpts.BTN_HEIGHT + CustOpts.HOR_GAP * 2 + 40);
		
		ok.addActionListener(this);
		addComponentListener(this);
		
		reLayout();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(BarFrame.secondScreen != null) {
			BarFrame.customerFrame.initContent();
			BarFrame.customerFrame.updateChange("", "", false);
		}
		this.dispose();
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		reLayout();
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	
	private void reLayout() {
		valChange.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, valChange.getPreferredSize().width, valChange.getPreferredSize().height);
		sep.setBounds(CustOpts.HOR_GAP, valChange.getY() + valChange.getHeight() + CustOpts.VER_GAP, 
				getWidth() - CustOpts.HOR_GAP * 3 - CustOpts.SIZE_EDGE * 2, sep.getPreferredSize().height);
		ok.setBounds((getWidth() - CustOpts.BTN_WIDTH)/2,
				sep.getY() + sep.getHeight() + CustOpts.VER_GAP * 2,
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
	}

	JSeparator sep;
	JLabel valChange;
	JButton ok;
}
