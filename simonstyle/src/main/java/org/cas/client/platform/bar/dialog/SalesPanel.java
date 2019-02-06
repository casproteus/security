package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.action.UpdateItemDiscountAction;
import org.cas.client.platform.bar.action.UpdateItemPriceAction;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.CategoryToggleButton;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.bar.uibeans.MenuButton;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

//Identity表应该和Employ表合并。
public class SalesPanel extends JPanel implements ComponentListener, ActionListener, FocusListener {

	String[][] categoryNameMetrix;
    ArrayList<ArrayList<CategoryToggleButton>> onSrcCategoryTgbMatrix = new ArrayList<ArrayList<CategoryToggleButton>>();
    CategoryToggleButton tgbActiveCategory;
    
    //Dish is more complecated than category, it's devided by category first, then divided by page.
    String[][] dishNameMetrix;// the struction must be [3][index]. it's more convenient than [index][3]
    String[][] onScrDishNameMetrix;// it's sub set of all menuNameMetrix
    private ArrayList<ArrayList<MenuButton>> onSrcMenuBtnMatrix = new ArrayList<ArrayList<MenuButton>>();

    //for print
    public static String SUCCESS = "0";
    public static String ERROR = "2";
    
    
    public SalesPanel() {
        initComponent();
    }

    // ComponentListener-----------------------------
    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(ComponentEvent e) {}

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(ComponentEvent e) {}

    /** Invoked when the component has been made invisible. */
    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void focusGained(FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {}

    // ActionListner-------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        //FunctionButton------------------------------------------------------------------------------------------------
        if (o instanceof FunctionButton) {
        	if(o == btnLine_1_1 || o == btnLine_1_2 || o == btnLine_1_3 || o == btnLine_2_3) { //pay
        		if(!checkBillStatus()) {
            		return;
            	}
        		outputStatusCheck();
    			billPricesUpdate();
        		
        		//if it's already paid, show comfirmDialog.
        		if(billPanel.status >= DBConsts.completed)
        			if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.ConfirmPayAgain(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0)
            			return;
        		
        		//check the pay dialog is already visible, if yes, then update bill received values.
        		if(BarFrame.payDlg.isVisible()) {
        			BarFrame.payDlg.updateBill(billPanel.getBillId());
        		}
        		//show dialog-------------------------------------
         		BarFrame.payDlg.setFloatSupport(true);
         		if(o == btnLine_1_1) {
         			BarFrame.payDlg.setTitle(BarFrame.consts.EnterCashPayment());
         		}else if(o == btnLine_1_2) {
         			BarFrame.payDlg.setTitle(BarFrame.consts.EnterDebitPayment());
         		}else if(o == btnLine_1_3) {
         			BarFrame.payDlg.setTitle(BarFrame.consts.EnterVisaPayment());
         		}else if(o == btnLine_2_3) {
         			BarFrame.payDlg.setTitle(BarFrame.consts.EnterMasterPayment());
         		}
         		BarFrame.payDlg.setVisible(true);
         		
         		//init payDialog content base on bill.
         		BarFrame.payDlg.initContent(billPanel);
        		
        	} else if (o == btnLine_1_4) {		//split bill
        		//check if there unsaved dish, and give warning.
        		if(BillListPanel.curDish == null) {
            		if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.UnSavedRecordFound(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0)
            			return;
        		}
        		List<Dish> newDishes = billPanel.getNewDishes();
        		if(newDishes.size() > 0) {
        			for (Dish dish : newDishes) {
						if(dish.getPrinter() != null && dish.getPrinter().length() > 1) {
		        			JOptionPane.showMessageDialog(this, BarFrame.consts.UnSendRecordFound());
		        			return;
						}
					}
        		}
        		
        		outputStatusCheck();
    			billPricesUpdate();
        		BarFrame.instance.switchMode(1);
        		
        	} else if (o == btnLine_1_5) {	//remove item.
        		removeItem();
        		
        	} else if(o == btnLine_1_6) {	//Modify
        		//if there's a curDish?
        		if(BillListPanel.curDish == null) {//check if there's an item selected.
        			JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
        			return;
        		}
        		new AddModificationDialog(BarFrame.instance, BillListPanel.curDish.getModification()).setVisible(true);
        		
        	}else if(o == btnLine_1_9) {	//service fee
         		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.ServiceFee());
         		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.ServiceFeeNotice());
        		BarFrame.numberPanelDlg.setBtnSource(null);
         		BarFrame.numberPanelDlg.setFloatSupport(true);
         		BarFrame.numberPanelDlg.setPercentSupport(true);
         		
         		BarFrame.numberPanelDlg.setModal(true);
				BarFrame.numberPanelDlg.reLayout();
         		BarFrame.numberPanelDlg.setVisible(true);
         		
         		try {
     				String curContent = BarFrame.numberPanelDlg.curContent;
     				if(curContent == null || curContent.length() == 0)
     					return;
             		float serviceFee = BarFrame.numberPanelDlg.isPercentage ? 
             				Float.valueOf(billPanel.valTotlePrice.getText()) * Float.valueOf(curContent)
             				: Float.valueOf(curContent);
             		billPanel.serviceFee = (int)(serviceFee * 100);
             		billPanel.updateTotleArea();
             		
             		outputStatusCheck();
             		billPricesUpdate();
             		
             	}catch(Exception exp) {
                 	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
             		return;
             	}
        		
        	}else if (o == btnLine_1_10) { // print bill
            	//check bill status
            	if(!checkBillStatus()) {
            		return;
            	}
        		outputStatusCheck();		//will send new added(not printed yet) dishes to kitchen.
        		billPricesUpdate();
        		billPanel.printBill(BarFrame.instance.valCurTable.getText(), BarFrame.instance.getCurBillIndex(), BarFrame.instance.valStartTime.getText());
        		billPanel.initContent();
        		
            } else if (o == btnLine_2_1) { // return
            	if(billPanel.orderedDishAry.size() > 0) {
	            	Dish dish = billPanel.orderedDishAry.get(billPanel.orderedDishAry.size() - 1);
	            	if(dish.getId() < 0) {	//has new record.
	            		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
	            				BarFrame.consts.COMFIRMLOSTACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) == 0) {
	    	                 return;	
	    	            }
	            	}
            	}
            	BarFrame.instance.switchMode(0);
            	
            } else if(o == btnLine_2_2) {		//Add bill
            	//save unsaved output
            	outputStatusCheck();
            	addNewBill();
        	} else if (o == btnLine_2_4) { // cancel all
            	if(billPanel.orderedDishAry.size() > 0) {
            		int lastSavedRow = billPanel.orderedDishAry.size() - 1 - billPanel.getNewDishes().size();
            		//update array first.
            		for(int i = billPanel.orderedDishAry.size() - 1; i > lastSavedRow; i--) {
            			billPanel.orderedDishAry.remove(i);
            		}
            		//update the table view
            		int tColCount = billPanel.tblBillPanel.getColumnCount();
            		int tValidRowCount = billPanel.orderedDishAry.size(); // get the used RowCount
            		Object[][] tValues = new Object[tValidRowCount][tColCount];
            		for (int r = 0; r < tValidRowCount; r++) {
            			for (int c = 0; c < tColCount; c++)
            				tValues[r][c] = billPanel.tblBillPanel.getValueAt(r, c);
            		}
            		billPanel.tblBillPanel.setDataVector(tValues, billPanel.header);
            		billPanel.resetColWidth(billPanel.getWidth());
            		billPanel.tblBillPanel.setSelectedRow(tValues.length - 1);
            		billPanel.updateTotleArea();
            	}else {
            		//update db.
            		if(isNoMoreNonEmptyBillOfCurTable()) {
            			resetCurTable();
            		}
            		BarFrame.instance.switchMode(0);
            	}
            	
            } else if (o == btnLine_2_5) { // void order include saved ones
            	int dishLength = billPanel.orderedDishAry.size();
            	int billID = dishLength > 0 ? billPanel.orderedDishAry.get(0).getBillID() : -1;
            	if(billID > 0) {//if already paid
            		try {
        				String sql = "select * from bill where id = " + billID;
                        ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
                        rs.beforeFirst();
                        rs.next();
                        if(rs.getInt("status") != 0) {
                        	JOptionPane.showMessageDialog(this, BarFrame.consts.ClosedBillCantVoid());
                        	return;
                        }
        			}catch(Exception exp) {
        				L.e("Refund function", "error happend when searching for bill with ID:"+billID, exp);
        			}
            	}
            	
            	if(billPanel.getNewDishes().size() < dishLength) {	//not all new
	        		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
	        				BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
		                 return;	
		            }
	            }else {			//all new
	            	if(JOptionPane.showConfirmDialog(BarFrame.instance, 
	        				BarFrame.consts.COMFIRMLOSTACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
		                 return;	
		            }
	            }
        	
        		for(int i = billPanel.orderedDishAry.size() - 1; i >= 0; i--) {
	        		if(billPanel.orderedDishAry.get(i).getOutputID() <= 0) {
	        			billPanel.orderedDishAry.remove(i);
	            	}
        		}
            	//update array second.
        		
        		
    	        //update db, update status of relevant bills to 100.
            	for (Dish dish : billPanel.orderedDishAry) {
            		dish.setCanceled(true);	// to make it printed in special format(so it's know as a cancelled dish)
            		//Dish.deleteRelevantOutput(dish);
				}
            	if(billPanel.orderedDishAry.size() > 0) {
            		billPanel.sendDishesToKitchen(billPanel.orderedDishAry, true);
            	}
            	//we need to process cur bill, give it a special status, so we can see the voided bills
            	//in check order dialog. and have to process it to be not null, better to be a negative. 
            	//so will not be considered as there's still non closed bill, when checking in isLastBill()
            	String curBill = BarFrame.instance.valCurBill.getText();
        		try {
        			//update bill
        			StringBuilder sql = new StringBuilder("update bill set status = ").append(DBConsts.voided)
        					.append(" where billIndex = ").append("".equals(curBill) ? 1 : curBill)
        					.append(" and openTime = '").append(BarFrame.instance.valStartTime.getText()).append("'");
                	PIMDBModel.getStatement().executeQuery(sql.toString());
                	//update output
                	sql = new StringBuilder("update output set deleted = ").append(DBConsts.voided)
                			.append(" where contactID = ").append("".equals(curBill) ? 1 : curBill)
                			.append(" and time = '").append(BarFrame.instance.valStartTime.getText()).append("'");
                    PIMDBModel.getStatement().executeQuery(sql.toString());
        		}catch(Exception exp) {
        			L.e("void all", "failed when setting bill status = " + DBConsts.voided + " aftetr void all command", exp);
        		}
            	
            	//if the bill amount is 1, cancel the selected status of the table.
        		if(isNoMoreNonEmptyBillOfCurTable()) {
        			resetCurTable();
        		}
            	BarFrame.instance.switchMode(0);
            	
            } else if (o == btnLine_2_6) {		//open drawer
            	PrintService.openDrawer();
            	
            } else if (o == btnLine_2_7) {//disc bill
         		BarFrame.discountDlg.setTitle(BarFrame.consts.DISCOUNT_BILL());
         		BarFrame.discountDlg.setNotice(BarFrame.consts.VolumnDiscountNotice());
         		BarFrame.discountDlg.setBtnSource(null);
         		BarFrame.discountDlg.setFloatSupport(true);
         		BarFrame.discountDlg.setPercentSupport(true);
         		BarFrame.discountDlg.setModal(true);
         		BarFrame.discountDlg.setVisible(true);
         		
         		try {
     				String curContent = BarFrame.discountDlg.curContent;
     				if(curContent == null || curContent.length() == 0)
     					return;
             		float discount = BarFrame.discountDlg.isPercentage ? 
             				(billPanel.subTotal + billPanel.discount) * (Float.valueOf(curContent)/100f)
             				: Float.valueOf(curContent);
             		discountBill(Math.round(discount * 100));
             		
             	}catch(Exception exp) {
                 	JOptionPane.showMessageDialog(BarFrame.discountDlg, DlgConst.FORMATERROR);
             		return;
             	}
            }else if(o == btnLine_2_8) {	//refund
            	//check if it's already paid.
            	boolean notPaiedYet = billPanel.orderedDishAry.size() < 1;
            	if(!notPaiedYet) {
            		int billID = billPanel.orderedDishAry.get(0).getBillID();
            		notPaiedYet = billID == 0;
            		if(!notPaiedYet) { //if already has billid, then check bill status.
            			try {
            				String sql = "select * from bill where id = " + billID;
                            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
                            rs.beforeFirst();
                            rs.next();
                            notPaiedYet = rs.getInt("status") == 0;
            			}catch(Exception exp) {
            				L.e("Refund function", "error happend when searching for bill with ID:"+billID, exp);
            			}
            		}
            	}
            	
            	if(notPaiedYet) {
            		JOptionPane.showMessageDialog(this, BarFrame.consts.NotPayYet());
            		return;
            	}

         		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.Refund());
         		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.RefundNotice());
            	BarFrame.numberPanelDlg.setBtnSource(null);
         		BarFrame.numberPanelDlg.setFloatSupport(true);
         		BarFrame.numberPanelDlg.setPercentSupport(false);
         		
         		BarFrame.numberPanelDlg.setModal(true);
         		BarFrame.numberPanelDlg.setVisible(true);
         		
         		try {
     				String curContent = BarFrame.numberPanelDlg.curContent;
     				if(curContent == null || curContent.length() == 0)
     					return;
     				
             		float refund = BarFrame.numberPanelDlg.isPercentage ? 
             				Float.valueOf(billPanel.valTotlePrice.getText()) * Float.valueOf(curContent)
             				: Float.valueOf(curContent);
             		
             		// get out existing status.
             		String sql = "select * from bill where id = " + billPanel.orderedDishAry.get(0).getBillID();
                    ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
                    rs.beforeFirst();
                    rs.next();
                    int refundAmount = rs.getInt("status");
                    if(refundAmount < -1) {	//if already refund, then add into existing amount.
                    	if (JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.AllreadyRefund() + BarOption.getMoneySign() + (0-refundAmount)/100.0, DlgConst.DlgTitle,
    		                    JOptionPane.YES_NO_OPTION) != 0) {// allready refunded, sure to refund again?
    						return;
    					}else {
    						refundAmount -= (int)(refund * 100);
    					}
                    }else {		//first time refund, then set the refund.
                    	refundAmount = 0 - (int)(refund * 100);
                    }
                    
            		new ChangeDlg(BarFrame.instance, 
            				BarOption.getMoneySign() + new DecimalFormat("#0.00").format(refund)).setVisible(true); //it's a non-modal dialog.

             		sql = "update bill set status = " + refundAmount + " where id = " + billPanel.orderedDishAry.get(0).getBillID();
             		PIMDBModel.getStatement().executeUpdate(sql);
             		
             		//print a bill, so let revenue know the store didn't receive the money.
             		BillPanel bp = ((SalesPanel)BarFrame.instance.panels[2]).billPanel;
            		PrintService.exePrintRefund(bp, bp.orderedDishAry, refundAmount);
            		BarFrame.instance.switchMode(0);
            		
             		PrintService.openDrawer();
             	}catch(Exception exp) {
                 	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
             		return;
             	}
            	
            } else if (o == btnLine_2_9) {//more
            	new MoreButtonsDlg(this).show((FunctionButton)o);
            	
            } else if (o == btnLine_2_10) {//send
        		outputStatusCheck();
        		billPricesUpdate();
            	if(BarOption.isFastFoodMode()) {
    		    	BarFrame.instance.valCurBill.setText(String.valueOf(BillListPanel.getANewBillNumber()));
    		    	billPanel.initContent();
    		    }else {
    		    	BarFrame.instance.switchMode(0);
    		    }
            }
        }
        //JToggleButton-------------------------------------------------------------------------------------
        else if(o instanceof JToggleButton) {
        	 if (o == btnLine_1_7) {	//disc item
         		if(BillListPanel.curDish == null) {//check if there's an item selected.
         			JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
         			return;
         		}

         		BarFrame.discountDlg.setTitle(BarFrame.consts.DISCITEM());
         		BarFrame.discountDlg.setNotice(BarFrame.consts.DISC_ITEMNotice());
         		BarFrame.discountDlg.setBtnSource(btnLine_1_7);//pomp up a discountDlg
         		BarFrame.discountDlg.setFloatSupport(true);
         		BarFrame.discountDlg.setPercentSupport(true);
         		BarFrame.discountDlg.setModal(false);
         		//should no record selected, select the last one.
         		BarFrame.discountDlg.setVisible(btnLine_1_7.isSelected());	//@NOTE: it's not model mode.
         		BarFrame.discountDlg.setAction(new UpdateItemDiscountAction(btnLine_1_7, billPanel));
         		
             }else if(o == btnLine_1_8) {	//change price
            	if(BillListPanel.curDish == null) {//check if there's an item selected.
          			JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
          			return;
          		}
         		showPriceChangeDlg();
        	}
        }
    }

	public void discountBill(int discount) {
		billPanel.discount = discount;
		billPanel.updateTotleArea();
		
		outputStatusCheck();
		billPricesUpdate();
	}

	public void addNewBill() {
		//add new bill with a new billID and billIdx.
		try {
			StringBuilder sql = new StringBuilder("SELECT DISTINCT contactID from output where SUBJECT = '").append(BarFrame.instance.valCurTable.getText())
					.append("' and (deleted is null or deleted = ").append(DBConsts.original)
					.append(") and time = '").append(BarFrame.instance.valStartTime.getText()).append("' order by contactID DESC");
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.beforeFirst();
			rs.next();

			BarFrame.instance.valCurBill.setText(String.valueOf(rs.getInt("contactID") + 1));
			//
			int billId = billPanel.generateBillRecord(BarFrame.instance.valCurTable.getText(), BarFrame.instance.valCurBill.getText(), BarFrame.instance.valStartTime.getText());
			billPanel.billID = billId;
		}catch(Exception exp) {
			L.e("Add Bill function",
					"SELECT DISTINCT contactID from output where SUBJECT = '" + BarFrame.instance.valCurTable.getText()
				+ "' and (deleted is null or deleted = " + DBConsts.original + ") and time = '" + BarFrame.instance.valStartTime.getText() + "' order by contactID DESC", exp);
		}
		BarFrame.instance.switchMode(2);
	}

	private boolean checkBillStatus() {
		if(billPanel.status == DBConsts.voided) {//check if the bill is .
			if (JOptionPane.showConfirmDialog(this, BarFrame.consts.ConvertVoidBillBack(), BarFrame.consts.Operator(),
		            JOptionPane.YES_NO_OPTION) != 0) {// are you sure to convert the voided bill back？
		        return false;
			}else {
				reOpenBill();
				reOpenOutput();
			}
		}
		return true;
	}

	public void reOpenOutput() {
		//convert the status of relevant output.
		StringBuilder sql = new StringBuilder("update output set deleted = ").append(DBConsts.original)
				.append(" where deleted = ").append(DBConsts.voided)
				.append(" and category = ").append(billPanel.billID);
		try {
			PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
		}catch(Exception exp) {
			L.e("SalesPane", "Exception happenned when converting output's status to 0", exp);
		}
	}

	public void reOpenBill() {
		//convert the status of the bill; 
		StringBuilder sql = new StringBuilder("update bill set status = ").append(DBConsts.original)
				.append(" where id = ").append(billPanel.billID);
		try {
			PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
		}catch(Exception exp) {
			L.e("SalesPane", "Exception happenned when converting bill's status to 0", exp);
		}
	}

	public void showPriceChangeDlg() {
		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.CHANGEPRICE());
		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.ChangePriceNotice());
		BarFrame.numberPanelDlg.setBtnSource(btnLine_1_8);//pomp up a numberPanelDlg
		BarFrame.numberPanelDlg.setFloatSupport(true);
		BarFrame.numberPanelDlg.setPercentSupport(false);
		BarFrame.numberPanelDlg.setModal(false);
		//should no record selected, select the last one.
		btnLine_1_8.setSelected(true);
		BarFrame.numberPanelDlg.setVisible(btnLine_1_8.isSelected());	//@NOTE: it's not model mode.
		BarFrame.numberPanelDlg.setAction(new UpdateItemPriceAction(btnLine_1_8, billPanel));
	}

	//and make sure new added dish will be updated with new information.
	private void billPricesUpdate() {
		//if there's new dish added, or discount, service fee changed.... update the total value field of bill record.
		updateBillRecordPrices(billPanel.getBillId());		//in case if added service fee or discout of bill.
		billPanel.initContent();	//always need to initContent, to make sure dish has new price. e.g. when adding a dish to a printed bill,
	}								//and click print bill immediatly, will need the initContent. 

	private void outputStatusCheck() {
		//if there's any new bill, send it to kitchen first, and this also made the output generated.
		List<Dish> dishes = billPanel.getNewDishes();
		if (dishes != null && dishes.size() > 0) {
			billPanel.sendNewOrdersToKitchenAndDB(dishes);
		}
	}

	public void discountADish(int value, Dish mostExpensiveDish) throws SQLException {
		
		int outputID = mostExpensiveDish.getOutputID();
		if(outputID >= 0) {
			StringBuilder sql = new StringBuilder("update output set discount = ").append(value)
					.append(", toltalprice = ").append(Math.round(mostExpensiveDish.getTotalPrice() - value))
					.append(" where id = ").append(outputID);
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		}
		
		billPanel.updateTotleArea();
		outputStatusCheck();
		billPricesUpdate();
	}
	
	void removeItem() {
		if(BillListPanel.curDish == null) {//check if there's an item selected.
			JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
			return;
		}
		if(BillListPanel.curDish.getOutputID() >= 0) {//check if it's send
			if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0)
				return;
			//clean output from db.
			Dish.deleteRelevantOutput(BillListPanel.curDish);
			//send cancel message to kitchen
			BillListPanel.curDish.setCanceled(true);	//set the dish with cancelled flag, so when it's printout, will with "!!!!!".
			billPanel.sendDishToKitchen(BillListPanel.curDish, true);
			//clean from screen.
			billPanel.removeFromSelection(billPanel.tblBillPanel.getSelectedRow());
			//update bill info, must be after the screen update, because will get total from screen.
			updateBillRecordPrices(BillListPanel.curDish.getBillID());
		}else {
			//only do clean from screen, because the output not generated yet, and will not affect the toltal in bill.
			billPanel.removeFromSelection(billPanel.tblBillPanel.getSelectedRow());
		}
	}

	private void updateBillRecordPrices(int billId) {
		try {
			PayDlg.updateBill(billId, "total", Math.round(Float.valueOf(billPanel.valTotlePrice.getText()) * 100));
			PayDlg.updateBill(billId, "discount", billPanel.discount);
			PayDlg.updateBill(billId, "otherReceived", billPanel.serviceFee);
		}catch(Exception exp) {
			L.e("SalesPanel", "unexpected error when updating the totalvalue of bill.", exp);
		}
	}

    public static boolean isNoMoreNonEmptyBillOfCurTable(){
    	int num = 0;
    	try {
			StringBuilder sql = new StringBuilder("SELECT DISTINCT contactID from output where SUBJECT = '").append(BarFrame.instance.valCurTable.getText())
					.append("' and (deleted is null or deleted = ").append(DBConsts.original)
					.append(") and time = '").append(BarFrame.instance.valStartTime.getText())
					.append("' order by contactID DESC");
//@ There could be not closed bill left during the splitting bill
//			ResultSet rs = smt.executeQuery("SELECT * from bill where tableID = '"
//                    + BarFrame.instance.valCurTable.getText() + "' and opentime = '"
//            		+ BarFrame.instance.valStartTime.getText() + "' and status is null");
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
			rs.relative(-1);
			num = rs.getRow();
		} catch (Exception exp) {
			ErrorUtil.write(exp);
		}
    	return num == 0;
    }
    
    public static void resetCurTable(){
    	try {
    		Statement smt =  PIMDBModel.getReadOnlyStatement();
    		//check if there's any non empty bill (with output connect to it)
    		StringBuilder sql = new StringBuilder(
    				"Select * from bill, output where output.category = bill.id and bill.tableID = '")
    				.append(BarFrame.instance.valCurTable.getText())
    				.append("' and bill.opentime = '").append(BarFrame.instance.valStartTime.getText())
    				.append("' and (bill.status is null or bill.status = ").append(DBConsts.original)
    				.append(") and (output.deleted is null or output.deleted = ").append(DBConsts.original).append(")");
    		ResultSet rs = smt.executeQuery(sql.toString());
    		rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            if(tmpPos > 0) {
            	if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.nonEmptyBillFound(),
            			DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
            		return;
            	}
            }
            
            //clean all empty bill (match table id and opentime, status is null, while doesn't exist in any output.).
            //if there's an output was deleted from this bill, this bill is still considered as empty.
            //if there's an output was completed 10
//            sql = new StringBuilder("update bill set status = ").append(DBConsts.deleted)
//            		.append(" WHERE bill.id IN ( SELECT id FROM bill WHERE tableID = ").append(BarFrame.instance.valCurTable.getText())
//    				.append(" and OPENTIME = '").append(BarFrame.instance.valStartTime.getText())
//    				.append("' and status IS NULL OR status = ").append(DBConsts.original)
//    				.append(") AND NOT EXISTS (SELECT category FROM OUTPUT WHERE (deleted IS null or deleted = ").append(DBConsts.completed)
//    				.append(" AND time = '").append(BarFrame.instance.valStartTime.getText())
//    				.append("' and SUBJECT = '").append(BarFrame.instance.valCurTable.getText()).append("')");
            
            //no need to be complex, all ortiginal status bills of this table should be cleaned.
            sql = new StringBuilder("update bill set status = ").append(DBConsts.deleted)
            		.append(" WHERE tableID = ").append(BarFrame.instance.valCurTable.getText())
    				.append(" and OPENTIME = '").append(BarFrame.instance.valStartTime.getText())
    				.append("' and status IS NULL OR status = ").append(DBConsts.original);
            smt.executeUpdate(sql.toString());
            //update table
            sql = new StringBuilder("update dining_Table set status = ").append(DBConsts.original)
            		.append(" WHERE name = '").append(BarFrame.instance.valCurTable.getText()).append("'");
            smt.executeUpdate(sql.toString());
    	}catch(Exception exp) {
    		ErrorUtil.write(exp);
    	}
    }
    
    void reLayout() {
        int panelHeight = getHeight();

        int tBtnWidht = (getWidth() - CustOpts.HOR_GAP * 10) / 10;
        int tBtnHeight = panelHeight / 10;

        // command buttons--------------
        // line 2
        btnLine_2_1.setBounds(CustOpts.HOR_GAP, panelHeight - tBtnHeight - CustOpts.VER_GAP, tBtnWidht, tBtnHeight);
        btnLine_2_2.setBounds(btnLine_2_1.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_3.setBounds(btnLine_2_2.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_4.setBounds(btnLine_2_3.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_5.setBounds(btnLine_2_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_6.setBounds(btnLine_2_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_7.setBounds(btnLine_2_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_8.setBounds(btnLine_2_7.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_9.setBounds(btnLine_2_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_10.setBounds(btnLine_2_9.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        // line 1
        btnLine_1_1.setBounds(btnLine_2_1.getX(),  btnLine_2_1.getY() - tBtnHeight - CustOpts.VER_GAP, tBtnWidht, tBtnHeight);
        btnLine_1_2.setBounds(btnLine_2_2.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_3.setBounds(btnLine_2_3.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_4.setBounds(btnLine_2_4.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_5.setBounds(btnLine_2_5.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_6.setBounds(btnLine_2_6.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_7.setBounds(btnLine_2_7.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_8.setBounds(btnLine_2_8.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_9.setBounds(btnLine_2_9.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_10.setBounds(btnLine_2_10.getX(), btnLine_1_1.getY(), tBtnWidht, tBtnHeight);

//        btnLine_2_11.setBounds(btnLine_2_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_2.getY(), tBtnWidht, tBtnHeight);
//        btnLine_2_12.setBounds(btnLine_1_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_14.getY(), tBtnWidht, tBtnHeight);
//        btnLine_2_13.setBounds(btnLine_1_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_14.getY(), tBtnWidht, tBtnHeight);
//        btnLine_2_14.setBounds(CustOpts.HOR_GAP,, tBtnWidht, tBtnHeight);
        
        // TOP part============================
        int topAreaHeight = btnLine_1_1.getY() - 3 * CustOpts.VER_GAP;

        billPanel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
                (int) (getWidth() * (1 - BarOption.getMenuAreaPortion())), topAreaHeight);
        
        // menu area--------------
        int xMenuArea = billPanel.getX() + billPanel.getWidth() + CustOpts.HOR_GAP;
        int widthMenuArea = getWidth() - billPanel.getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.SIZE_EDGE;

        BarFrame.menuPanel.setBounds(xMenuArea, billPanel.getY(), widthMenuArea, topAreaHeight);
        BarFrame.menuPanel.reLayout();

        billPanel.resetColWidth(billPanel.getWidth());
    }

    void initComponent() {
    	removeAll();	//when it's called by setting panel(changed colors...), it will be called to refresh.
        btnLine_1_1 = new FunctionButton(BarFrame.consts.CASH());
        btnLine_1_2 = new FunctionButton(BarFrame.consts.DEBIT());
        btnLine_1_3 = new FunctionButton(BarFrame.consts.VISA());
        btnLine_1_4 = new FunctionButton(BarFrame.consts.SPLIT_BILL());
        btnLine_1_5 = new FunctionButton(BarFrame.consts.REMOVEITEM());
        btnLine_1_6 = new FunctionButton(BarFrame.consts.MODIFY());
        btnLine_1_7 = new JToggleButton(BarFrame.consts.DISC_ITEM());
        btnLine_1_8 = new JToggleButton(BarFrame.consts.ChangePrice());
        btnLine_1_9 = new FunctionButton(BarFrame.consts.SERVICEFEE());
        btnLine_1_10 = new FunctionButton(BarFrame.consts.PRINT_BILL());

        btnLine_2_1 = new FunctionButton(BarFrame.consts.RETURN());
        btnLine_2_2 = new FunctionButton(BarFrame.consts.AddUser());
        btnLine_2_3 = new FunctionButton(BarFrame.consts.MASTER());
        btnLine_2_4 = new FunctionButton(BarFrame.consts.CANCEL_ALL());
        btnLine_2_5 = new FunctionButton(BarFrame.consts.VOID_ORDER());
        btnLine_2_6 = new FunctionButton(BarFrame.consts.OpenDrawer());
        btnLine_2_7 = new FunctionButton(BarFrame.consts.VolumnDiscount());
        btnLine_2_8 = new FunctionButton(BarFrame.consts.Refund());
        btnLine_2_9 = new FunctionButton(BarFrame.consts.MORE());
        btnLine_2_10 = new FunctionButton(BarFrame.consts.SEND());
        
        billPanel = new BillPanel(this);
        // properties
        Color bg = BarOption.getBK("Sales");
    	if(bg == null) {
    		bg = new Color(216,216,216);
    	}
		setBackground(bg);
        setLayout(null);
        
        // built
        add(btnLine_1_1);
        add(btnLine_1_2);
        add(btnLine_1_3);
        add(btnLine_1_4);
        add(btnLine_1_5);
        add(btnLine_1_6);
        add(btnLine_1_7);
        add(btnLine_1_8);
        add(btnLine_1_9);
        add(btnLine_1_10);

        add(btnLine_2_1);
        add(btnLine_2_2);
        add(btnLine_2_3);
        add(btnLine_2_4);
        add(btnLine_2_5);
        add(btnLine_2_6);
        add(btnLine_2_7);
        add(btnLine_2_8);
        add(btnLine_2_9);
        add(btnLine_2_10);
        
        add(billPanel);
        // add listener
        addComponentListener(this);

        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        // tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）

        btnLine_1_1.addActionListener(this);
        btnLine_1_2.addActionListener(this);
        btnLine_1_3.addActionListener(this);
        btnLine_1_4.addActionListener(this);
        btnLine_1_5.addActionListener(this);
        btnLine_1_6.addActionListener(this);
        btnLine_1_7.addActionListener(this);
        btnLine_1_8.addActionListener(this);
        btnLine_1_9.addActionListener(this);
        btnLine_1_10.addActionListener(this);

        btnLine_2_1.addActionListener(this);
        btnLine_2_2.addActionListener(this);
        btnLine_2_3.addActionListener(this);
        btnLine_2_4.addActionListener(this);
        btnLine_2_5.addActionListener(this);
        btnLine_2_6.addActionListener(this);
        btnLine_2_7.addActionListener(this);
        btnLine_2_8.addActionListener(this);
        btnLine_2_9.addActionListener(this);
        btnLine_2_10.addActionListener(this);
        
		reLayout();
    }

    private FunctionButton btnLine_1_1;
    private FunctionButton btnLine_1_2;
    private FunctionButton btnLine_1_3;
    private FunctionButton btnLine_1_4;
    private FunctionButton btnLine_1_5;
    private FunctionButton btnLine_1_6;
    public JToggleButton btnLine_1_7;
    JToggleButton btnLine_1_8;
    private FunctionButton btnLine_1_9;
    private FunctionButton btnLine_1_10;

    private FunctionButton btnLine_2_1;
    private FunctionButton btnLine_2_2;
    private FunctionButton btnLine_2_3;
    private FunctionButton btnLine_2_4;
    private FunctionButton btnLine_2_5;
    private FunctionButton btnLine_2_6;
    private FunctionButton btnLine_2_7;
    private FunctionButton btnLine_2_8;
    private FunctionButton btnLine_2_9;
    private FunctionButton btnLine_2_10;
    
    public BillPanel billPanel;
}
