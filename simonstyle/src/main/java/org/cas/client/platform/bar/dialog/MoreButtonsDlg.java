package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.ResultSet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cas.client.platform.bar.i18n.BarDlgConst0;
import org.cas.client.platform.bar.i18n.BarDlgConst1;
import org.cas.client.platform.bar.i18n.BarDlgConst2;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class MoreButtonsDlg extends JFrame implements ActionListener, WindowFocusListener{
	SalesPanel barGeneralPanel;

    public MoreButtonsDlg(SalesPanel general) {
    	super();
    	setTitle(BarFrame.consts.MORE());
        barGeneralPanel = general;
        initPanel();
    }

    /**
     * Invoked when an action occurs. NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
     * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
     * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
     * 
     * @param e
     *            动作事件
     */
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnLine_3_1) { // enter the setting mode.(admin interface)
        	this.setVisible(false);
        	BarFrame.instance.switchMode(3);
        	
//        } else if (o == btnLine_3_2) {	//QTY
//        	this.setVisible(false);
//
//     		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.QTY());
//     		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.QTYNOTICE());
//    		BarFrame.instance.numberPanelDlg.setBtnSource(btnLine_3_2);//pomp up a numberPanelDlg
//     		BarFrame.numberPanelDlg.setFloatSupport(false);
//     		BarFrame.numberPanelDlg.setPercentSupport(false);
//     		BarFrame.numberPanelDlg.setModal(true);
//    		//should no record selected, select the last one.
//    		BarFrame.instance.numberPanelDlg.setVisible(btnLine_3_2.isSelected());	//@NOTE: it's not model mode.
//    		if(btnLine_3_2.isSelected()) {
//    			try {
//    				String curContent = BarFrame.instance.numberPanelDlg.curContent;
//            		int tQTY = Integer.valueOf(curContent);
//                	int row = barGeneralPanel.billPanel.tblBillPanel.getSelectedRow();
//                	barGeneralPanel.billPanel.tblBillPanel.setValueAt(curContent + "x", row, 0);
//                	barGeneralPanel.billPanel.orderedDishAry.get(row).setNum(tQTY);
//                	barGeneralPanel.billPanel.updateTotleArea();
//            	}catch(Exception exp) {
//                	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
//            		return;
//            	}
//    		}
//    		if(barGeneralPanel.billPanel.tblBillPanel.getSelectedRow() < 0) {
//    			barGeneralPanel.billPanel.tblBillPanel.setSelectedRow(barGeneralPanel.billPanel.tblBillPanel.getRowCount()-1);
//    		}
//    		//present the value in number dialog.
//    		Object obj = barGeneralPanel.billPanel.tblBillPanel.getValueAt(barGeneralPanel.billPanel.tblBillPanel.getSelectedRow(), 3);
//    		BarFrame.instance.numberPanelDlg.setContents(obj.toString());

    	} else if (o == btnCoupon) {
    		String couponCode  = JOptionPane.showInputDialog(null, BarFrame.consts.couponCode());
    		StringBuilder sql = new StringBuilder("SELECT * from hardware where name = '").append(couponCode)
    				.append("' and category = 1 and (status is null or status = 0)");
    		try {
    			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
    			rs.afterLast();
                rs.relative(-1);
                int tmpPos = rs.getRow();
                if(tmpPos == 0) {
                	JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidCoupon());
                }else {
                	 rs.beforeFirst();
                     tmpPos = 0;
                     rs.next();
                     int id = rs.getInt("id");
                     int category = rs.getInt("Type");
                     String productCode = rs.getString("IP");
                     int price = rs.getInt("langType");
                     
                     sql = new StringBuilder("update hardware set ");
                    		 //.append(
                	//check the price
                	int value = 0;
                	//if not enought
                	if(value < price) {
                		sql.append("status = 1").append(" where id = ").append(id);
                		
                	}else {              	//if enought
	                	PayDlg.exactMoney(barGeneralPanel.billPanel.getBillId(), "cash");
	                	
	                	this.setVisible(false);
	                	PrintService.openDrawer();
	                	BarFrame.instance.switchMode(0);
                	}
                }
    		}catch(Exception exp) {
    			L.e("", "", exp);
    		}
    		
        	
        } else if (o == btnLine_3_3) {
    		BarFrame.consts = new BarDlgConst0();
        	updateInterface("update employee set subject = 'EN' where id = " + LoginDlg.USERID);
        } else if (o == btnLine_3_4) {
    		BarFrame.consts = new BarDlgConst1();
    		updateInterface("update employee set subject = 'FR' where id = " + LoginDlg.USERID);
        } else if (o == btnLine_3_5) {
    		BarFrame.consts = new BarDlgConst2();
        	updateInterface("update employee set subject = 'CN' where id = " + LoginDlg.USERID);
        }
//        else if (o == btnLine_3_7) {
//        	
//        } else if (o == btnLine_3_8) {
//        	
//        } else if (o == btnLine_3_9) {
//        	
//        }
    }
    
    private void updateInterface(String sb) {
    	try {
    		PIMDBModel.getStatement().executeUpdate(sb);
    		this.setVisible(false);
    		BarFrame.instance.initComponent();
    	}catch(Exception exp) {
    		ErrorUtil.write(exp);
    	}
    }
	public void show(FunctionButton btnMore) {
		reLayout(btnMore);
		this.setVisible(true);
	}
	
	private void reLayout(FunctionButton btnMore) {
		int x = btnMore.getX();
		int y = btnMore.getY();
		int width = btnMore.getWidth();
		int height = btnMore.getHeight();
		
		btnLine_3_1.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width, height);
		btnCoupon.setBounds(btnLine_3_1.getX(), btnLine_3_1.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		btnLine_3_3.setBounds(btnLine_3_1.getX(), btnCoupon.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		btnLine_3_4.setBounds(btnLine_3_1.getX(), btnLine_3_3.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		btnLine_3_5.setBounds(btnLine_3_1.getX(), btnLine_3_4.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		//btnLine_3_6.setBounds(btnLine_3_1.getX(), btnLine_3_5.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		//btnLine_3_7.setBounds(btnLine_3_1.getX(), btnLine_3_6.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		//btnLine_3_8.setBounds(btnLine_3_1.getX(), btnLine_3_7.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
		//btnLine_3_9.setBounds(btnLine_3_1.getX(), btnLine_3_8.getY() + btnLine_3_1.getHeight() + CustOpts.VER_GAP, width, height);
        
		int panelHeight = height * 5 + CustOpts.VER_GAP * 6;
		setBounds(x, y - panelHeight, 
				width + CustOpts.HOR_GAP * 2 + CustOpts.SIZE_EDGE * 2 + 10,
				panelHeight + CustOpts.SIZE_EDGE * 2 + 40);
	}
	
	private void initPanel() {
		// 初始化－－－－－－－－－－－－－－－－
		btnLine_3_1 = new FunctionButton(BarFrame.consts.SETTINGS());
        //btnLine_3_2 = new JToggleButton(BarFrame.consts.QTY());
        btnCoupon = new FunctionButton(BarFrame.consts.COUPON());
		btnLine_3_3 = new FunctionButton("EN");
		btnLine_3_4 = new FunctionButton("FR");
		btnLine_3_5 = new FunctionButton("CN");
		//btnLine_3_7 = new FunctionButton(BarFrame.consts.DISC_VOLUMN);
		//btnLine_3_8 = new FunctionButton(BarFrame.consts.LOGOUT);
		//btnLine_3_9 = new FunctionButton(BarFrame.consts.MORE);

		// 属性设置－－－－－－－－－－－－－－
		btnLine_3_1.setMargin(new Insets(0, 0, 0, 0));
		btnCoupon.setMargin(btnLine_3_1.getMargin());
		btnLine_3_3.setMargin(btnLine_3_1.getMargin());
		btnLine_3_4.setMargin(btnLine_3_1.getMargin());
		btnLine_3_5.setMargin(btnLine_3_1.getMargin());
		//btnLine_3_6.setMargin(btnLine_3_1.getMargin());
		//btnLine_3_7.setMargin(btnLine_3_1.getMargin());
		//btnLine_3_8.setMargin(btnLine_3_1.getMargin());
		//btnLine_3_9.setMargin(btnLine_3_1.getMargin());
		
		// 布局---------------
		setLayout(null);
		
		// 搭建－－－－－－－－－－－－－
		add(btnLine_3_1);
		add(btnCoupon);
		add(btnLine_3_3);
		add(btnLine_3_4);
		add(btnLine_3_5);
		//add(btnLine_3_6);
		//add(btnLine_3_7);
		//add(btnLine_3_8);
		//add(btnLine_3_9);

		// 加监听器－－－－－－－－
		btnLine_3_1.addActionListener(this);
		btnCoupon.addActionListener(this);
		btnLine_3_3.addActionListener(this);
		btnLine_3_4.addActionListener(this);
		btnLine_3_5.addActionListener(this);
		//btnLine_3_6.addActionListener(this);
		//btnLine_3_7.addActionListener(this);
		//btnLine_3_8.addActionListener(this);
		//btnLine_3_9.addActionListener(this);
		
		this.addWindowFocusListener(this);
	}
	private FunctionButton btnLine_3_1;
	//private JToggleButton btnLine_3_2;
	private FunctionButton btnCoupon;
	private FunctionButton btnLine_3_3;
	private FunctionButton btnLine_3_4;
	private FunctionButton btnLine_3_5;
//	private FunctionButton btnLine_3_7;
//	private FunctionButton btnLine_3_8;
//	private FunctionButton btnLine_3_9;

	@Override
	public void windowGainedFocus(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		// TODO Auto-generated method stub
		dispose();
	}
}
