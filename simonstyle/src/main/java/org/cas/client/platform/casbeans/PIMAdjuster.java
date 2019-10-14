package org.cas.client.platform.casbeans;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.uibeans.ArrowButton;

public class PIMAdjuster extends JPanel{
	private JLabel text;
    private ArrowButton btnLess;
	private JTextField tfdNum;
    private ArrowButton btnMore;
    
    public PIMAdjuster(String name, String num) {
    	initComponent(name, num);
    }

	private void initComponent(String name, String num) {
		text = new JLabel(name);
		btnLess = new ArrowButton("-");
		tfdNum = new JTextField(num);
		btnMore = new ArrowButton("+");
		
		tfdNum.setEditable(false);
		btnLess.setPreferredSize(new Dimension(BarFrame.consts.SCROLLBAR_WIDTH, BarFrame.consts.SCROLLBAR_WIDTH));
		btnMore.setPreferredSize(new Dimension(BarFrame.consts.SCROLLBAR_WIDTH, BarFrame.consts.SCROLLBAR_WIDTH));
		
		setLayout(new FlowLayout());
		
		add(text);
		add(btnLess);
		add(tfdNum);
		add(btnMore);
	}
}
