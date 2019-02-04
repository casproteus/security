package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.DecimalFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class CouponDlg extends JDialog implements ComponentListener, ActionListener{
	private Coupon coupon;
	private String[] typeAry = new String[2];
	private String[] statusAry = new String[2];
	
	 
	public CouponDlg(JDialog parent) {
		super(parent);
		initComponent();
	}
	
	private void initComponent() {
		
		setTitle(BarFrame.consts.COUPON());
		setModal(true);
		
		btnAdd = new JButton(BarFrame.consts.OK());
		btnClose = new JButton(BarFrame.consts.Close());
		
		lblCouponCode = new JLabel(BarFrame.consts.couponCode());
		lblValue = new JLabel(BarFrame.consts.Price());
		lblCategory = new JLabel(BarFrame.consts.Categary());
		lblStatus = new JLabel(BarFrame.consts.isRedeemed());
		lblProduct = new JLabel(BarFrame.consts.Product());
		
		tfdCouponCode = new JTextField();
		tfdValue = new JTextField();
		choCategory = new JComboBox<String>();
		choStatus = new JComboBox<String>();
		txtProduct = new JTextArea();
		
		txtProduct.setBorder(new LineBorder(Color.GRAY));
		
		// 布局---------------
		setBounds((CustOpts.SCRWIDTH - 270) / 2, (CustOpts.SCRHEIGHT - 290) / 2, 270, 290); // 对话框的默认尺寸。
		getContentPane().setLayout(null);
		// 0-discount price/1-discount percentage/2-discount to price/3-discount to percentage
		typeAry[0] = "$";
		typeAry[1] = "%";
		choCategory.setModel(new DefaultComboBoxModel(typeAry));
		statusAry[0] = "";
		statusAry[1] = "Y";
		choStatus.setModel(new DefaultComboBoxModel(statusAry));
		
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
		addComponentListener(this);
		btnClose.addActionListener(this);
		btnAdd.addActionListener(this);
		initContent(null);
		reLayout();
	}
	
	public void initContent(Coupon coupon) {
		this.coupon = coupon;
		choStatus.setEnabled(coupon != null);
		if(coupon == null) {
			return;
		}
		tfdCouponCode.setText(coupon.getCouponCode());
		tfdValue.setText(coupon.getPrice());
		choCategory.setSelectedItem(coupon.getCategory());
		choStatus.setSelectedItem(coupon.getStatus());
		txtProduct.setText(coupon.getProductCode());
	}

	private void reLayout() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 9) / 8;
        int tBtnHeight = panelHeight / 10;

        lblCouponCode.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCouponCode.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		tfdCouponCode.setBounds(lblCouponCode.getX() + lblCouponCode.getWidth() + CustOpts.HOR_GAP, lblCouponCode.getY(),
				getWidth() - lblCouponCode.getX() - lblCouponCode.getWidth() - CustOpts.HOR_GAP * 4 - CustOpts.SIZE_EDGE, CustOpts.BTN_HEIGHT);
		lblValue.setBounds(lblCouponCode.getX(), lblCouponCode.getY() + lblCouponCode.getHeight() + CustOpts.VER_GAP,
				lblValue.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		tfdValue.setBounds(tfdCouponCode.getX(), lblValue.getY(),
				tfdCouponCode.getWidth(), CustOpts.BTN_HEIGHT);
		lblCategory.setBounds(lblValue.getX(), lblValue.getY() + lblValue.getHeight() + CustOpts.VER_GAP,
				lblCategory.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		choCategory.setBounds(tfdValue.getX(), lblCategory.getY(),
				tfdCouponCode.getWidth(), CustOpts.BTN_HEIGHT);
		lblStatus.setBounds(lblCategory.getX(), lblCategory.getY() + lblCategory.getHeight() + CustOpts.VER_GAP,
				lblStatus.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		choStatus.setBounds(choCategory.getX(), lblStatus.getY(),
				tfdCouponCode.getWidth(), CustOpts.BTN_HEIGHT);
		lblProduct.setBounds(lblStatus.getX(), lblStatus.getY() + lblStatus.getHeight() + CustOpts.VER_GAP,
				lblProduct.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		
		btnAdd.setBounds(getWidth() - CustOpts.BTN_WIDTH * 2 - CustOpts.HOR_GAP * 2 - 20, getHeight() - CustOpts.BTN_HEIGHT - 50,
				 CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnClose.setBounds(btnAdd.getX() + btnAdd.getWidth() + CustOpts.HOR_GAP, btnAdd.getY(), 
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

		txtProduct.setBounds(choStatus.getX(), lblProduct.getY(),
				tfdCouponCode.getWidth(), btnAdd.getY() - lblProduct.getY() - CustOpts.VER_GAP * 2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == btnClose) {
		    dispose();
		} else if (o == btnAdd) {
			StringBuilder sql = new StringBuilder();
		    if(coupon == null) {	//create new coupon.
		    	sql.append("INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('").append(tfdCouponCode.getText())
		    		.append("', 1, ").append(Math.round(Float.valueOf(tfdValue.getText()) * 100)).append(", '")
		    		.append(txtProduct.getText()).append("', 0, 0)");
		    	
		    }else {
		    	sql.append("UPDATE Hardware set name = '").append(tfdCouponCode.getText())
		    	.append("',  langType = ").append(Math.round(Float.valueOf(tfdValue.getText()) * 100))
		    	.append(", ip = '").append(txtProduct.getText())
		    	.append("', style = ").append(choCategory.getSelectedIndex())
		    	.append(", status = ").append(choStatus.getSelectedIndex())
		    	.append(" where id = ").append(coupon.getId());
		    }
		    
		    try {
	    		PIMDBModel.getStatement().executeUpdate(sql.toString());
	    		dispose();
	    	}catch(Exception exp) {
	    		L.e("CouponDlg", "exception happenned when update hardware table" + sql, exp);
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
