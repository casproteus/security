package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class PayMethodDlg extends JFrame implements ActionListener, WindowFocusListener{
	
	BillListPanel billListPanel;

    public PayMethodDlg(BillListPanel general) {
    	super();
    	setTitle(BarFrame.consts.MORE());
        billListPanel = general;
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
        if(o == btnCASH || o == btnDEBIT || o == btnVISA || o == btnMASTER) {
        	if(!billListPanel.checkColosedBill()) {
				return;
			}

     		//check if all bills are not closed, and find the first panel
			ArrayList<BillPanel> unclosedBillPanels = billListPanel.gatherAllUnclosedBillPanels();            
			
    		//check the pay dialog is already visible, if yes, then update bill received values.
    		if(BarFrame.payDlg.isVisible()) {
    			BarFrame.payDlg.updateBill(unclosedBillPanels.get(0).getBillId());
    		}
    		//show dialog-------------------------------------
     		BarFrame.payDlg.setFloatSupport(true);
     		if(o == btnCASH) {
     			BarFrame.payDlg.setTitle(BarFrame.consts.EnterCashPayment());
     		}else if(o == btnDEBIT) {
     			BarFrame.payDlg.setTitle(BarFrame.consts.EnterDebitPayment());
     		}else if(o == btnVISA) {
     			BarFrame.payDlg.setTitle(BarFrame.consts.EnterVisaPayment());
     		}else if(o == btnMASTER) {
     			BarFrame.payDlg.setTitle(BarFrame.consts.EnterMasterPayment());
     		}
     		BarFrame.payDlg.setVisible(true);
     		
     		//init payDialog content base on bill.
     		BarFrame.payDlg.initContent(unclosedBillPanels);
     		
     		//combine bills--------------------------------------------------------------------------------------------
			billListPanel.combineBills(unclosedBillPanels);
			
    	} else if (o == btnCOUPON) {
    		
    		String couponCode  = JOptionPane.showInputDialog(null, BarFrame.consts.couponCode());
    		
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
                    SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
                    int langIdx = CustOpts.custOps.getUserLang();
                    ArrayList<String> nameAry = getAppliableDishNames(productCode, langIdx); //if nameAry is null, mean apply to whole bill.
                    ArrayList<Dish> dishesOnBill = salesPanel.billPanel.orderedDishAry;
                    ArrayList<Dish> matchedDishesOnBill = getMatchedItem(dishesOnBill, nameAry, langIdx);
                    //@NOTE: if nameAry and matchedDishesOnBill are null, mean apply to whole bill.
                    if(matchedDishesOnBill != null && matchedDishesOnBill.size() == 0) {
                    	JOptionPane.showMessageDialog(this, BarFrame.consts.couponNotApplyToBill());
                    	return;
                    }
                    
                    //if matchedDishesOnBill is null, then apply the coupon to the whle bill.
                    if(matchedDishesOnBill == null) {	
	                    if(category == 0) {//mean the price is absolute price, not persentage.
	                    	salesPanel.discountBill(value);
	                    }else {
	                    	value = Math.round((salesPanel.billPanel.subTotal + salesPanel.billPanel.discount) * (Float.valueOf(value) / 100f));
	                    	salesPanel.discountBill(value);
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
		
		btnCASH.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, width, height);
		btnDEBIT.setBounds(btnCASH.getX(), btnCASH.getY() + btnCASH.getHeight() + CustOpts.VER_GAP, width, height);
		btnVISA.setBounds(btnCASH.getX(), btnDEBIT.getY() + btnCASH.getHeight() + CustOpts.VER_GAP, width, height);
		btnMASTER.setBounds(btnCASH.getX(), btnVISA.getY() + btnCASH.getHeight() + CustOpts.VER_GAP, width, height);
		btnCOUPON.setBounds(btnCASH.getX(), btnMASTER.getY() + btnCASH.getHeight() + CustOpts.VER_GAP, width, height);
        
		int panelHeight = height * 5 + CustOpts.VER_GAP * 6;
		setBounds(x, y - panelHeight, 
				width + CustOpts.HOR_GAP * 2 + CustOpts.SIZE_EDGE * 2 + 10,
				panelHeight + CustOpts.SIZE_EDGE * 2 + 40);
	}
	
	private void initPanel() {
		// 初始化－－－－－－－－－－－－－－－－
		btnCASH = new FunctionButton(BarFrame.consts.CASH());
		btnDEBIT = new FunctionButton(BarFrame.consts.DEBIT());
		btnVISA = new FunctionButton(BarFrame.consts.VISA());
		btnMASTER = new FunctionButton(BarFrame.consts.MASTER());
        btnCOUPON = new FunctionButton(BarFrame.consts.COUPON());

		// 属性设置－－－－－－－－－－－－－－
		btnCASH.setMargin(new Insets(0, 0, 0, 0));
		btnDEBIT.setMargin(btnCASH.getMargin());
		btnVISA.setMargin(btnCASH.getMargin());
		btnMASTER.setMargin(btnCASH.getMargin());
		btnCOUPON.setMargin(btnCASH.getMargin());
		
		// 布局---------------
		setLayout(null);
		
		// 搭建－－－－－－－－－－－－－
		add(btnCASH);
		add(btnDEBIT);
		add(btnVISA);
		add(btnMASTER);
		add(btnCOUPON);

		// 加监听器－－－－－－－－
		btnCASH.addActionListener(this);
		btnDEBIT.addActionListener(this);
		btnVISA.addActionListener(this);
		btnMASTER.addActionListener(this);
		btnCOUPON.addActionListener(this);
		
		this.addWindowFocusListener(this);
	}
	
	private FunctionButton btnCASH;
	private FunctionButton btnDEBIT;
	private FunctionButton btnVISA;
	private FunctionButton btnMASTER;
	private FunctionButton btnCOUPON;

	@Override
	public void windowGainedFocus(WindowEvent e) {}

	@Override
	public void windowLostFocus(WindowEvent e) {
		dispose();
	}
}
