package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.statistics.CheckBillDlg;
import org.cas.client.platform.bar.dialog.statistics.ReportDlg;
import org.cas.client.platform.bar.i18n.BarDlgConst0;
import org.cas.client.platform.bar.i18n.BarDlgConst1;
import org.cas.client.platform.bar.i18n.BarDlgConst2;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class MoreButtonsDlg extends JDialog implements ActionListener, WindowFocusListener{
	
	SalesPanel salesPanel;

    public MoreButtonsDlg(SalesPanel general) {
    	super();
    	setTitle(BarFrame.consts.MORE());
    	setIconImage(CustOpts.custOps.getFrameLogoImage());
        salesPanel = general;
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
        if (o == btnReport) { // enter the setting mode.(admin interface)
        	this.setVisible(false);
        	ReportDlg dlg = new ReportDlg(BarFrame.instance);
    		dlg.setVisible(true);
        	
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
//                	int row = salesPanel.billPanel.tblBillPanel.getSelectedRow();
//                	salesPanel.billPanel.tblBillPanel.setValueAt(curContent + "x", row, 0);
//                	salesPanel.billPanel.orderedDishAry.get(row).setNum(tQTY);
//                	salesPanel.billPanel.updateTotleArea();
//            	}catch(Exception exp) {
//                	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
//            		return;
//            	}
//    		}
//    		if(salesPanel.billPanel.tblBillPanel.getSelectedRow() < 0) {
//    			salesPanel.billPanel.tblBillPanel.setSelectedRow(salesPanel.billPanel.tblBillPanel.getRowCount()-1);
//    		}
//    		//present the value in number dialog.
//    		Object obj = salesPanel.billPanel.tblBillPanel.getValueAt(salesPanel.billPanel.tblBillPanel.getSelectedRow(), 3);
//    		BarFrame.instance.numberPanelDlg.setContents(obj.toString());

    	} else if (o == salesPanel.btnOTHER) {
    		String giftCardNumber  = JOptionPane.showInputDialog(null, BarFrame.consts.Account());
    		if(giftCardNumber == null || giftCardNumber.length() == 0)
    			return;
    		
    		StringBuilder sql = new StringBuilder("SELECT * from hardware where category = 2 and name = '").append(giftCardNumber)
    				.append("' and (status is null or status = 0)");
    		try {
    			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
    			rs.afterLast();
                rs.relative(-1);
                int tmpPos = rs.getRow();
                if(tmpPos == 0) {	//if there's no this coupon number in database, then warning and return.
                	JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidCoupon());
                	return;
                }else {			//if the number is OK.
                	//get out every field of first matching record.
                	rs.beforeFirst();
                    tmpPos = 0;
                    rs.next();
                    int id = rs.getInt("id");
                    int category = rs.getInt("style");
                    String productCode = rs.getString("IP");
                    int value = rs.getInt("langType");
                    
                    //show up the payDialog, waiting for user to input money, after confirm, the money should be deduct from the account of this card
                    SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
                    BarFrame.payDlg.maxInput = (float)(value / 100.0);
                    salesPanel.actionPerformed(new ActionEvent(salesPanel.btnOTHER, 0, ""));
                    //how to know the number user inputed, and how to verify if it's bigger than the money left in card?
                    if (BarFrame.payDlg.inputedContent != null && BarFrame.payDlg.inputedContent.length() > 0) {
	                    float usedMoneyQT = Math.round(Float.valueOf(BarFrame.payDlg.inputedContent) * 100);
	                    sql = new StringBuilder("update hardware set langType = langType - ").append(usedMoneyQT)
	                    		.append(" where id = ").append(id);
	                    PIMDBModel.getStatement().executeUpdate(sql.toString());
                    }
                }
    		}catch(Exception exp) {
    			L.e("Redeem Coupon", "exception happend when redeem coupon: " + sql, exp);
    		}
        	
    	} else if (o == btnDiscountCoupon) {
    		
    		String couponCode  = JOptionPane.showInputDialog(null, BarFrame.consts.couponCode());
    		if(couponCode == null || couponCode.length() == 0)
    			return;
    		
    		StringBuilder sql = new StringBuilder("SELECT * from hardware where category = 1 and name = '").append(couponCode)
    				.append("' and (status is null or status = 0)");
    		try {
    			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
    			rs.afterLast();
                rs.relative(-1);
                int tmpPos = rs.getRow();
                if(tmpPos == 0) {	//if there's no this coupon number in database, then warning and return.
                	JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidCoupon());
                	return;
                }else {			//if the number is OK.
                	//get out every field of first matching record.
                	rs.beforeFirst();
                    tmpPos = 0;
                    rs.next();
                    int id = rs.getInt("id");
                    int category = rs.getInt("style");
                    String productCode = rs.getString("IP");
                    int value = rs.getInt("langType");
                    
                    //check if the coupon can be applied on current bill
                    int langIdx = CustOpts.custOps.getUserLang();
                    ArrayList<String> nameAry = getAppliableDishNames(productCode, langIdx); //if nameAry is null, mean apply to whole bill.
                    ArrayList<Dish> dishesOnBill = salesPanel.billPanel.orderedDishAry;
                    ArrayList<Dish> matchedDishesOnBill = getMatchedItem(dishesOnBill, nameAry, langIdx);
                    //@NOTE: if nameAry and matchedDishesOnBill are null, mean apply to whole bill.
                    if(matchedDishesOnBill != null && matchedDishesOnBill.size() == 0) {
                    	JOptionPane.showMessageDialog(this, BarFrame.consts.couponNotApplyToBill());
                    	return;
                    }
                    
                    //if matchedDishesOnBill is null, then apply the coupon to the whole bill.
                    if(matchedDishesOnBill == null) {
	                    if(category == 0) {//mean the price is absolute price, not percentage.
	                    	int total = Math.round(Float.valueOf(salesPanel.billPanel.valTotlePrice.getText()) * 100);
	                    	value = value > total ?  total : value;
	                    	salesPanel.discountBill(value);
	                    }else {
	                    	value = Math.round((salesPanel.billPanel.subTotal + salesPanel.billPanel.discount) * (Float.valueOf(value/100f) / 100f));
	                    	salesPanel.discountBill(value);
	                    }
	                    //recalculate the left
	                    
	                    //if the total is 0, then close cur bill.
	                    if("0.00".equals(salesPanel.billPanel.valTotlePrice.getText())) {
	                    	PrintService.exePrintInvoice(salesPanel.billPanel, false, true, true);
	                    	BarFrame.instance.closeCurrentBill();
		                	this.setVisible(false);
		                	if(BarOption.isFastFoodMode()) {
		            	    	BarFrame.instance.valStartTime.setText(BarOption.df.format(new Date()));
		            	    	salesPanel.addNewBillInCurTable();
		                	}else {
			            		if(BarFrame.instance.isTableEmpty(null, null)) {
			            			BarFrame.instance.closeATable(null, null);
			            		}
			            		BarFrame.instance.switchMode(0);
		                	}
	                    }
                    } else {//apply the coupon only to the dish item.
	                	//find out the most expensive dish
                    	Dish mostExpensiveDish = null;
                    	for (Dish dish : matchedDishesOnBill) {
                    		mostExpensiveDish = mostExpensiveDish.getPrice() < dish.getPrice() ? dish : mostExpensiveDish;
						}
                    	//calculate coupon value:
                    	if(category == 0) {//mean the price is absolute price, not persentage.
                    		value = value > mostExpensiveDish.getPrice() ? mostExpensiveDish.getPrice() : value;
	                    }else {
	                    	value = Math.round(mostExpensiveDish.getPrice() * (Float.valueOf(value) / 100f));
	                    }
                    	
                    	salesPanel.discountADish(value, mostExpensiveDish);
                    }
                    
                	//update the status of the coupon.
                	sql = new StringBuilder("update hardware set status = 1 where id = ").append(id);
                	PIMDBModel.getStatement().executeUpdate(sql.toString());
                }
    		}catch(Exception exp) {
    			L.e("Redeem Coupon", "exception happend when redeem coupon: " + sql, exp);
    		}
        	
        } else if (o == btnEn) {
    		BarFrame.consts = new BarDlgConst0();
        	updateInterface("update employee set subject = 'EN' where id = " + LoginDlg.USERID);
        } else if (o == btnFR) {
    		BarFrame.consts = new BarDlgConst1();
    		updateInterface("update employee set subject = 'FR' where id = " + LoginDlg.USERID);
        } else if (o == btnCN) {
    		BarFrame.consts = new BarDlgConst2();
        	updateInterface("update employee set subject = 'CN' where id = " + LoginDlg.USERID);
        } else if (o == btnSetting) { // enter the setting mode.(admin interface)
        	this.setVisible(false);
        	BarFrame.instance.switchMode(3);
        } else if (o == btnSuspend) { // suspend bill
        	salesPanel.createAndPrintNewOutput();
        	BarUtil.updateBillRecordPrices(salesPanel.billPanel);
        	
        	this.setVisible(false);
        	if(salesPanel.billPanel.status > DBConsts.suspended || salesPanel.billPanel.status < DBConsts.original) {
				return;
			}
			
	        try {
	        	String tableID = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
	        	//update outputs
				StringBuilder sql = new StringBuilder("update output set deleted = ").append(DBConsts.suspended)
		                .append(" where SUBJECT = '").append(tableID)
		                .append("' and time = '").append(BarFrame.instance.valStartTime.getText())
		                .append("' and (deleted is null or deleted = ").append(DBConsts.original).append(")");
				PIMDBModel.getStatement().executeUpdate(sql.toString());
				
				//update bills
				sql = new StringBuilder("update bill set status = ").append(DBConsts.suspended)
						.append(" where openTime = '").append(BarFrame.instance.valStartTime.getText())
						.append("' and (status is null or status = ").append(DBConsts.original).append(")");
				PIMDBModel.getStatement().executeUpdate(sql.toString());
				
	        }catch(Exception exp) {
	        	ErrorUtil.write(exp);
	        }
	        
        	if(BarOption.isFastFoodMode()) {
    	    	((SalesPanel)BarFrame.instance.panels[2]).addNewBillInCurTable();
        	}else {
				BarFrame.instance.setCurBillIdx("");
				BarFrame.instance.switchMode(0);
        	}
        } else if (o == btnCheckOrder) { // check order
        	this.setVisible(false);
        	String endNow = BarOption.df.format(new Date());
    		int p = endNow.indexOf(" ");
    		String startTime = endNow.substring(0, p + 1) + BarOption.getStartTime();
    		CheckBillDlg dlg = new CheckBillDlg(BarFrame.instance);
    		dlg.initContent(startTime, endNow);
    		dlg.setVisible(true);
        } else if (o == btnSignOut) {
        	BarFrame.instance.userCheckOut();
        }
    }
    
    //find out which dishes in current bill can be apply on the coupon.
    private ArrayList<Dish> getMatchedItem(ArrayList<Dish> dishesOnBill, ArrayList<String> nameAry, int langIdx) {
    	if(nameAry == null) {
    		return null;
    	}
    	ArrayList<Dish> appliableDishes = new ArrayList<Dish>();
    	for (Dish dish : dishesOnBill) {
			if(nameAry.contains(dish.getLanguage(langIdx))) {
				appliableDishes.add(dish);
			}
		}
		return appliableDishes;
	}

	private ArrayList<String> getAppliableDishNames(String productCode, int langIdx) {
    	ArrayList<String> appliableDishes = new ArrayList<String>();
    	if(productCode == null || productCode.trim().length() == 0) {
    		return null;
    	}
    	//if it's a category
    	String[] codes = productCode.split(",");
        Dish[] dishAry = BarFrame.menuPanel.getDishAry();
    	for(int m = 0; m < codes.length; m++) {
			if(codes[m].length() == 0) {
				continue;
			}
			boolean matched = false;
	    	for(int i = 0; i < BarFrame.menuPanel.categoryNameMetrix[0].length; i++) {
	    		//check 3 languages
    			if(codes[m].trim().equalsIgnoreCase(BarFrame.menuPanel.categoryNameMetrix[0][i].trim())
    					|| codes[m].trim().equalsIgnoreCase(BarFrame.menuPanel.categoryNameMetrix[1][i].trim())
    					|| codes[m].trim().equalsIgnoreCase(BarFrame.menuPanel.categoryNameMetrix[2][i].trim())) {
    				//add all relavant dishes
    		        for (int j = 0; j < dishAry.length; j++) {
    					if(dishAry[j].getCATEGORY().trim().equals(BarFrame.menuPanel.categoryNameMetrix[0][i].trim())) {
    						appliableDishes.add(dishAry[j].getLanguage(langIdx));
    					}
    		        }
    				matched = true;
    				break;
    			}
	    	}
	    	
	    	//didn't match any category, then it's a menu name, add the lang0 into the list directly.
	    	if(!matched) {
	    		for (int j = 0; j < dishAry.length; j++) {
					if(codes[m].trim().equalsIgnoreCase(dishAry[j].getLanguage(0).trim())
						|| codes[m].trim().equalsIgnoreCase(dishAry[j].getLanguage(1).trim())
						|| codes[m].trim().equalsIgnoreCase(dishAry[j].getLanguage(2).trim())){
						appliableDishes.add(dishAry[j].getLanguage(langIdx));
					}
		        }
	    	}
    	}
		return appliableDishes;
	}

	private void updateInterface(String sb) {
    	try {
    		PIMDBModel.getStatement().executeUpdate(sb);
    		this.setVisible(false);
    		BarFrame.instance.initComponent();
    		BarFrame.instance.switchMode(0);
    	}catch(Exception exp) {
    		ErrorUtil.write(exp);
    	}
    }
	
	public void show(FunctionButton btnMore) {
		reLayout(btnMore);
		this.setVisible(true);
	}
	
	private void reLayout(FunctionButton btnMore) {
		int width = btnMore.getWidth();
		int height = btnMore.getHeight();
		
		salesPanel.btnOTHER.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width, height);
		btnDiscountCoupon.setBounds(CustOpts.HOR_GAP, salesPanel.btnOTHER.getY() + height + CustOpts.VER_GAP, width, height);
		btnSuspend.setBounds(CustOpts.HOR_GAP, btnDiscountCoupon.getY() + height + CustOpts.VER_GAP, width, height);
		btnCheckOrder.setBounds(CustOpts.HOR_GAP, btnSuspend.getY() + height + CustOpts.VER_GAP, width, height);
		btnReport.setBounds(CustOpts.HOR_GAP, btnCheckOrder.getY() + height + CustOpts.VER_GAP, width, height);
		btnEn.setBounds(btnReport.getX() + width + CustOpts.HOR_GAP, salesPanel.btnOTHER.getY(), width, height);
		btnFR.setBounds(btnEn.getX(), btnEn.getY() + height + CustOpts.VER_GAP, width, height);
		btnCN.setBounds(btnEn.getX(), btnFR.getY() + height + CustOpts.VER_GAP, width, height);
		btnSetting.setBounds(btnEn.getX(), btnCN.getY() + height + CustOpts.VER_GAP, width, height);
		btnSignOut.setBounds(btnEn.getX(), btnSetting.getY() + height + CustOpts.VER_GAP, width, height);

		int x = btnMore.getX() + salesPanel.getRootPane().getParent().getX();
		int y = btnMore.getY() + salesPanel.getRootPane().getParent().getY();
		int panelHeight = height * 5 + CustOpts.VER_GAP * 4;
		setBounds(x - width - CustOpts.HOR_GAP, y - panelHeight, 
				width * 2 + CustOpts.HOR_GAP * 3 + CustOpts.SIZE_EDGE * 2 + 10,
				panelHeight + CustOpts.SIZE_EDGE * 2 + 40);
	}
	
	private void initPanel() {
		// 初始化－－－－－－－－－－－－－－－－
		btnReport = new FunctionButton(BarFrame.consts.Report());
        //btnLine_3_2 = new JToggleButton(BarFrame.consts.QTY());
        salesPanel.btnOTHER = new FunctionButton(BarFrame.consts.GIFTCARD());
        btnDiscountCoupon = new FunctionButton(BarFrame.consts.COUPON());
		btnSuspend = new FunctionButton(BarFrame.consts.SUSPEND());
		btnCheckOrder = new FunctionButton(BarFrame.consts.OrderManage());
		btnEn = new FunctionButton("EN");
		btnFR = new FunctionButton("FR");
		btnCN = new FunctionButton("CN");
		btnSetting = new FunctionButton(BarFrame.consts.SETTINGS());
		btnSignOut = new FunctionButton(BarFrame.consts.CheckOut());

		// 属性设置－－－－－－－－－－－－－－
		btnReport.setMargin(new Insets(0, 0, 0, 0));
		salesPanel.btnOTHER.setMargin(btnReport.getMargin());
		btnDiscountCoupon.setMargin(btnReport.getMargin());
		btnEn.setMargin(btnReport.getMargin());
		btnFR.setMargin(btnReport.getMargin());
		btnCN.setMargin(btnReport.getMargin());
		btnSetting.setMargin(btnReport.getMargin());
		btnSuspend.setMargin(btnReport.getMargin());
		btnCheckOrder.setMargin(btnReport.getMargin());
		btnSignOut.setMargin(btnReport.getMargin());
		
		// 布局---------------
		setLayout(null);
		
		// 搭建－－－－－－－－－－－－－
		add(btnReport);
		add(salesPanel.btnOTHER);
		add(btnDiscountCoupon);
		add(btnEn);
		add(btnFR);
		add(btnCN);
		add(btnSetting);
		add(btnSuspend);
		add(btnCheckOrder);
		add(btnSignOut);

		// 加监听器－－－－－－－－
		btnReport.addActionListener(this);
		salesPanel.btnOTHER.addActionListener(this);
		btnDiscountCoupon.addActionListener(this);
		btnEn.addActionListener(this);
		btnFR.addActionListener(this);
		btnCN.addActionListener(this);
		btnSetting.addActionListener(this);
		btnSuspend.addActionListener(this);
		btnCheckOrder.addActionListener(this);
		btnSignOut.addActionListener(this);
		
		this.addWindowFocusListener(this);
	}
	
	private FunctionButton btnReport;
	private FunctionButton btnDiscountCoupon;
	private FunctionButton btnEn;
	private FunctionButton btnFR;
	private FunctionButton btnCN;
	private FunctionButton btnSetting;
	private FunctionButton btnSignOut;
	private FunctionButton btnSuspend;
	private FunctionButton btnCheckOrder;

	@Override
	public void windowGainedFocus(WindowEvent e) {}

	@Override
	public void windowLostFocus(WindowEvent e) {
		dispose();
	}
}
