package org.cas.client.platform.bar.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.cas.client.platform.cascustomize.CustOpts;

public class CouponDlg extends JDialog implements ComponentListener, ActionListener{
	public CouponDlg(JDialog parent) {
		initComponent();
	}
	
	private void initComponent() {
		
		setTitle(BarFrame.consts.COUPON());

		btnAdd = new JButton(BarFrame.consts.OK());
		btnClose = new JButton(BarFrame.consts.Close());
		
		lblCouponCode = new JLabel(BarFrame.consts.couponCode());
		lblValue = new JLabel(BarFrame.consts.Price());
		lblCategory = new JLabel(BarFrame.consts.Categary());
		lblStatus = new JLabel(BarFrame.consts.Status());
		lblProduct = new JLabel(BarFrame.consts.Product());
		
		tfdCouponCode = new JTextField();
		tfdValue = new JTextField();
		choCategory = new JComboBox<String>();
		choStatus = new JComboBox<String>();
		txtProduct = new JTextArea();
		
		// 布局---------------
		setBounds((CustOpts.SCRWIDTH - 540) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 540, 320); // 对话框的默认尺寸。
		getContentPane().setLayout(null);
		
		// 搭建－－－－－－－－－－－－－
		getContentPane().add(lblCouponCode);
		getContentPane().add(lblValue);
		getContentPane().add(lblCategory);
		getContentPane().add(lblStatus);
		getContentPane().add(lblProduct);
		getContentPane().add(tfdCouponCode);
		getContentPane().add(tfdValue);
		getContentPane().add(choCategory);
		getContentPane().add(choStatus);
		getContentPane().add(txtProduct);

		getContentPane().add(btnAdd);
		getContentPane().add(btnClose);
		
		// 加监听器－－－－－－－－
		btnClose.addActionListener(this);
		btnAdd.addActionListener(this);
		
	}
	
	private void reLayout() {
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == btnClose) {
		    dispose();
		} else if (o == btnAdd) {
		    
		}
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
	
	private JLabel lblCouponCode;
	private JTextField tfdCouponCode;
	private JLabel lblValue;
	private JTextField tfdValue;
	private JLabel lblCategory;
	private JComboBox<String> choCategory;
	private JLabel lblStatus;
	private JComboBox<String> choStatus;
	private JLabel lblProduct;
	private JTextArea txtProduct;
	
	private JButton btnClose;
	private JButton btnAdd;
}
