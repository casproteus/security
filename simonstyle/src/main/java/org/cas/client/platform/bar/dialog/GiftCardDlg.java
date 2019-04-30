package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.cas.client.platform.bar.net.bean.Coupon;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class GiftCardDlg extends JDialog implements ComponentListener, ActionListener{
	private Coupon coupon;
	private String[] typeAry = new String[2];
	private String[] statusAry = new String[2];
	
	 
	public GiftCardDlg(JDialog parent) {
		super(parent);
		initComponent();
	}
	
	private void initComponent() {
		
		setTitle(BarFrame.consts.GIFTCARD());
		setModal(true);
		
		btnAdd = new JButton(BarFrame.consts.OK());
		btnClose = new JButton(BarFrame.consts.Close());
		
		lblCardAccount = new JLabel(BarFrame.consts.Account());
		lblValue = new JLabel(BarFrame.consts.Price());
		lblProduct = new JLabel(BarFrame.consts.Product());
		
		tfdCardAccount = new JTextField();
		tfdValue = new JTextField();
		txtProduct = new JTextArea();
		
		txtProduct.setBorder(new LineBorder(Color.GRAY));
		
		// 布局---------------
		setBounds((CustOpts.SCRWIDTH - 270) / 2, (CustOpts.SCRHEIGHT - 290) / 2, 270, 290); // 对话框的默认尺寸。
		getContentPane().setLayout(null);
		// 0-discount price/1-discount percentage/2-discount to price/3-discount to percentage
		typeAry[0] = "$";
		typeAry[1] = "%";
		statusAry[0] = "";
		statusAry[1] = "Y";
		
		// 搭建－－－－－－－－－－－－－
		getContentPane().add(lblCardAccount);
		getContentPane().add(lblValue);
		getContentPane().add(lblProduct);
		getContentPane().add(tfdCardAccount);
		getContentPane().add(tfdValue);
		getContentPane().add(txtProduct);

		getContentPane().add(btnAdd);
		getContentPane().add(btnClose);
		
		// 加监听器－－－－－－－－
		addComponentListener(this);
		btnClose.addActionListener(this);
		btnAdd.addActionListener(this);
		initContent(null);
		reLayout();
	}
	
	public void initContent(Coupon coupon) {
		this.coupon = coupon;
		if(coupon == null) {
			return;
		}
		tfdCardAccount.setText(coupon.getCouponCode());
		tfdValue.setText(coupon.getPrice());
		txtProduct.setText(coupon.getProductCode());
	}

	private void reLayout() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 9) / 8;
        int tBtnHeight = panelHeight / 10;

        lblCardAccount.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCardAccount.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		tfdCardAccount.setBounds(lblCardAccount.getX() + lblCardAccount.getWidth() + CustOpts.HOR_GAP, lblCardAccount.getY(),
				getWidth() - lblCardAccount.getX() - lblCardAccount.getWidth() - CustOpts.HOR_GAP * 4 - CustOpts.SIZE_EDGE, CustOpts.BTN_HEIGHT);
		lblValue.setBounds(lblCardAccount.getX(), lblCardAccount.getY() + lblCardAccount.getHeight() + CustOpts.VER_GAP,
				lblValue.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		tfdValue.setBounds(tfdCardAccount.getX(), lblValue.getY(),
				tfdCardAccount.getWidth(), CustOpts.BTN_HEIGHT);
		lblProduct.setBounds(lblValue.getX(), lblValue.getY() + lblValue.getHeight() + CustOpts.VER_GAP,
				lblProduct.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		
		btnAdd.setBounds(getWidth() - CustOpts.BTN_WIDTH * 2 - CustOpts.HOR_GAP * 2 - 20, getHeight() - CustOpts.BTN_HEIGHT - 50,
				 CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnClose.setBounds(btnAdd.getX() + btnAdd.getWidth() + CustOpts.HOR_GAP, btnAdd.getY(), 
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

		txtProduct.setBounds(tfdValue.getX(), lblProduct.getY(),
				tfdCardAccount.getWidth(), btnAdd.getY() - lblProduct.getY() - CustOpts.VER_GAP * 2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == btnClose) {
		    dispose();
		} else if (o == btnAdd) {
			String accountID = tfdCardAccount.getText();
			
			StringBuilder sql = new StringBuilder();
		    if(coupon == null) {	//create new coupon.
		    	try {
					ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery("select * from hardware where category = 2 and name = '" + accountID + "'");
					rs.afterLast();
			        rs.relative(-1);
			        if(rs.getRow() > 0) {
			        	JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidInput());
			        	return;
			        }
				}catch(Exception exp) {
		    		L.e("GiftCardDlg", "exception happenned when query hardware table", exp);
		    	}
		    	
		    	sql.append("INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('").append(accountID)
		    		.append("', 2, ").append(Math.round(Float.valueOf(tfdValue.getText()) * 100)).append(", '")
		    		.append(txtProduct.getText()).append("', 0, 0)");
		    	
		    }else {
		    	try {
					ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(
							"select * from hardware where category = 2 and name = '" + accountID + "' and id != " + coupon.getId());
					rs.afterLast();
			        rs.relative(-1);
			        if(rs.getRow() > 0) {
			        	JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidInput());
			        	return;
			        }
				}catch(Exception exp) {
		    		L.e("GiftCardDlg", "exception happenned when query hardware table", exp);
		    	}
		    	
		    	sql.append("UPDATE Hardware set name = '").append(accountID)
		    	.append("',  langType = ").append(Math.round(Float.valueOf(tfdValue.getText()) * 100))
		    	.append(", ip = '").append(txtProduct.getText())
		    	.append("' where id = ").append(coupon.getId());
		    }
		    
		    try {
	    		PIMDBModel.getStatement().executeUpdate(sql.toString());
	    		dispose();
	    	}catch(Exception exp) {
	    		L.e("GiftCardDlg", "exception happenned when update hardware table" + sql, exp);
	    		JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidInput());
	    	}
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
	
	private JLabel lblCardAccount;
	private JTextField tfdCardAccount;
	private JLabel lblValue;
	private JTextField tfdValue;
	private JLabel lblProduct;
	private JTextArea txtProduct;
	
	private JButton btnClose;
	private JButton btnAdd;
}
