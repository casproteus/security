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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.BarUtil;
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
        	if(o == btnCASH || o == btnDEBIT || o == btnVISA || o == btnMASTER) { //pay
        		if(!checkBillStatus()) {
            		return;
            	}
        		createAndPrintNewOutput();
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
         		if(o == btnCASH) {
         			BarFrame.payDlg.setTitle(BarFrame.consts.EnterCashPayment());
         		}else if(o == btnDEBIT) {
         			BarFrame.payDlg.setTitle(BarFrame.consts.EnterDebitPayment());
         		}else if(o == btnVISA) {
         			BarFrame.payDlg.setTitle(BarFrame.consts.EnterVisaPayment());
         		}else if(o == btnMASTER) {
         			BarFrame.payDlg.setTitle(BarFrame.consts.EnterMasterPayment());
         		}
         		//init payDialog content base on bill.
         		BarFrame.payDlg.initContent(billPanel);
         		BarFrame.payDlg.setVisible(true);
        		
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
        		
        		createAndPrintNewOutput();
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
     				billPanel.serviceFee  = BarFrame.numberPanelDlg.isPercentage ? 
             				Math.round(Float.valueOf((billPanel.subTotal - billPanel.serviceFee + billPanel.discount) * Float.valueOf(curContent)))
             				: Math.round(Float.valueOf(curContent) * 100);
             				
             		billPanel.updateTotleArea();
             		
             		createAndPrintNewOutput();
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
        		createAndPrintNewOutput();		//will send new added(not printed yet) dishes to kitchen.
        		billPricesUpdate();
        		billPanel.printBill(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
        				BarFrame.instance.getCurBillIndex(),
        				BarFrame.instance.valStartTime.getText());
        		billPanel.initContent();
        		
            } else if (o == btnLine_2_1) { // return
            	if(billPanel.getNewDishes().size() > 0) {
            		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
            				BarFrame.consts.COMFIRMLOSTACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
    	                 return;	
    	            }
            	}
            	BarFrame.instance.switchMode(0);
            	
            } else if(o == btnLine_2_2) {		//Add bill
            	//save unsaved output
            	createAndPrintNewOutput();
            	addNewBill(null, null);
        	} else if (o == btnLine_2_4) { // cancel all---- if bill is empty, then check if table is empty, if yes, close current table. yes or not, all back to table view.
            	if(billPanel.orderedDishAry.size() > 0) {	//if not empty, remove all new added items.
            		int newDishQT = billPanel.getNewDishes().size();
            		if(newDishQT == 0) {
            			if(JOptionPane.showConfirmDialog(this, BarFrame.consts.NoNewSelectionToCancel(), DlgConst.DlgTitle,
    		                    JOptionPane.YES_NO_OPTION) != 0) {
            				return;
            			}else {
            				voidCurrentOrder();
            			}
            		}
            		
            		int lastSavedRow = billPanel.orderedDishAry.size() - 1 - newDishQT;
            		
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
            		//@NOTE: we don't close current bill, because maybe there's output still have billID of this bill, all the empty bill will be closed when table closed.
            		//update bill and dining_table in db.
            		if(BarFrame.instance.isTableEmpty(null, null)) {
            			BarFrame.instance.closeATable(null, null);
            		}
            		BarFrame.instance.switchMode(0);
            	}
            	
            } else if (o == btnLine_2_5) { // void order include saved ones
            	//if there's no dish on it at all (including deleted outputs), delete the bill directly
            	voidCurrentOrder();
            } else if (o == btnLine_2_6) {		//open drawer
            	PrintService.openDrawer();
            	
            } else if (o == btnLine_2_7) {//disc bill
            	
            	if(!BarOption.isWaiterAllowedToDiscount()) {
            		if (!BarFrame.instance.adminAuthentication()) 
        				return;
            	}
            	
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
         		BarFrame.numberPanelDlg.reLayout();
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
             		int refundAmount = billPanel.status;
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
            				BarOption.getMoneySign() + BarUtil.formatMoney(refund)).setVisible(true); //it's a non-modal dialog.
            		
            		//dump the old bill and create a new bill
            		StringBuilder sql = new StringBuilder("update bill set status = ").append(DBConsts.expired)
             				.append(" where id = ").append(billPanel.billID);
             		PIMDBModel.getStatement().executeUpdate(sql.toString());
             		
             		//generat new bill with ref to dumpted bill everything else use the data on current billPane
             		//@NOTE:no need to generata new output. the output will be choosed by table and billIdx.
            		billPanel.comment = PrintService.REF_TO + billPanel.billID + "F";
             		int newBillID = BarFrame.instance.generateBillRecord(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
            				String.valueOf(BarFrame.instance.valCurBillIdx.getText()),
            				BarFrame.instance.valStartTime.getText(),
            				Math.round(Float.valueOf(billPanel.valTotlePrice.getText()) * 100), 
            				billPanel);
             		
             		//change something on cur billPane, then use it to print the refund bill, to let revenue know the store refund some money.
             		billPanel.billID = newBillID;
            		PrintService.exePrintRefund(billPanel, - (int)(refund * 100));
            		
            		//update the status with new refund amount for the new bill, so next time refund will base on new number.
             		sql = new StringBuilder("update bill set status = ").append(refundAmount)
             				.append(" where id = ").append(newBillID);
             		PIMDBModel.getStatement().executeUpdate(sql.toString());
            		
            		BarFrame.instance.switchMode(0);
             		PrintService.openDrawer();
             	}catch(Exception exp) {
                 	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
             		return;
             	}
            	
            } else if (o == btnLine_2_9) {//more
            	new MoreButtonsDlg(this).show((FunctionButton)o);
            	
            } else if (o == btnLine_2_10) {//send
        		createAndPrintNewOutput();
        		billPricesUpdate();
            	if(BarOption.isFastFoodMode()) {
    		    	BarFrame.instance.valCurBillIdx.setText(String.valueOf(BillListPanel.getANewBillIdx()));
    		    	BarFrame.instance.createAnEmptyBill(null, null, 0);//create new bill;
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
         		
         		if(!BarOption.isWaiterAllowedToDiscount()) {
            		if (!BarFrame.instance.adminAuthentication()) 
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
            	if(!BarOption.isWaiterAllowedToChangePrice()) {
            		if (!BarFrame.instance.adminAuthentication()) 
        				return;
            	}
         		showPriceChangeDlg();
        	}
        }
    }

	public void voidCurrentOrder() {
		int dishLength = billPanel.orderedDishAry.size();
		int billID = billPanel.billID;
		
		try {
			//check if it's a mistake-opening-table-action or adding bill action by check if there's any output on it already. 
			//will be considered as non-empty as long as there's output connecting to the id, even the output is not currently displaying on this bill.
			StringBuilder sql = new StringBuilder("select * from output where category = ").append(billID);
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
		    rs.relative(-1);
		    
		    if(rs.getRow() == 0) {			//if still empty 
		    	//no related output, then don't record this bill in db at all.
		    	sql = new StringBuilder("delete from bill where id = ").append(billID);
		    	PIMDBModel.getStatement().executeUpdate(sql.toString());
		    	
		    } else { 						//if already has output.
		        //check if bill is already closed.
		        if(billPanel.status >= DBConsts.completed) {
		        	JOptionPane.showMessageDialog(this, BarFrame.consts.ClosedBillCantVoid());
		        	return;
		        }
		        
		        //check if there's already send dish, give different warning message and remove them from panel.
		        if(billPanel.getNewDishes().size() < dishLength) {	//not all new
		    		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
		    				BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
		                 return;	
		            }
		        }else {												//all new
		        	if(JOptionPane.showConfirmDialog(BarFrame.instance, 
		    				BarFrame.consts.COMFIRMLOSTACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
		                 return;	
		            }
		        }
		        
		        //clean the unsend items from billPanel first.
				for(int i = billPanel.orderedDishAry.size() - 1; i >= 0; i--) {
		    		if(billPanel.orderedDishAry.get(i).getOutputID() <= 0) {
		    			billPanel.orderedDishAry.remove(i);
		        	}
				}

		        //then mark all dishes which already send
		    	for (Dish dish : billPanel.orderedDishAry) {
		    		dish.setCanceled(true);	// to make it printed in special format(so it's know as a cancelled dish)
				}
		    	
		    	//print a final receipt or notice kitchen to stop preparing.
		    	if(billPanel.status >= DBConsts.billPrinted) {		//if bill printed, print a refund bill.
		    		PrintService.exePrintVoid(billPanel);
		    	}else if(billPanel.orderedDishAry.size() > 0) { 	//otherwise, tell kitchen to stop preparing.
		    		billPanel.sendDishesToKitchen(billPanel.orderedDishAry, true);
		    	}
		    	
		    	//@NOTE: we need to process cur bill, give it a special status, so we can see the voided bills in check order dialog. 
		    	//and have to process it to be not null, better will not be considered as there's still non closed bill, when checking in isLastBill()
		    	String curBill = BarFrame.instance.valCurBillIdx.getText();
		    	//update bill
				sql = new StringBuilder("update bill set status = ").append(DBConsts.voided)
						.append(" where billIndex = ").append("".equals(curBill) ? 1 : curBill)
						.append(" and openTime = '").append(BarFrame.instance.valStartTime.getText()).append("'");
		    	PIMDBModel.getStatement().executeQuery(sql.toString());
		    	//update output
		    	sql = new StringBuilder("update output set deleted = ").append(DBConsts.voided)
		    			.append(" where contactID = ").append("".equals(curBill) ? 1 : curBill)
		    			.append(" and time = '").append(BarFrame.instance.valStartTime.getText()).append("'");
		        PIMDBModel.getStatement().executeQuery(sql.toString());
		        
		    }
		}catch(Exception exp) {
			L.e("void order", "error happend when voiding a bill with ID:"+ billID, exp);
		}
		
		//if the bill amount is 1, cancel the selected status of the table.
		if(BarFrame.instance.isTableEmpty(null, null)) {
			BarFrame.instance.closeATable(null, null);
		}
		BarFrame.instance.switchMode(0);
	}

	public void discountBill(int discount) {
		if (discount > billPanel.subTotal) {
			discount = Math.round(billPanel.subTotal);
		}
		billPanel.discount = discount;
		billPanel.updateTotleArea();
		
		createAndPrintNewOutput();
		billPricesUpdate();
	}

	//add new bill with a new billID and billIdx.
	public void addNewBill(String tableName, String openTime) {
		if(tableName == null) {
			tableName = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
		}
		if(openTime == null) {
			openTime = BarFrame.instance.valStartTime.getText();
		}
		
		int existingBillQT = getExistingBillQt(tableName, openTime);
		String newBillIdx = String.valueOf(existingBillQT + 1);
		
		int billId = billPanel.generateBillRecord(tableName, newBillIdx, openTime);
		billPanel.billID = billId;
		BarFrame.instance.valCurBillIdx.setText(newBillIdx);
		BarFrame.instance.switchMode(2);
	}

	public int getExistingBillQt(String tableName, String openTime) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT contactID from output where SUBJECT = '").append(tableName)
				.append("' and (deleted is null or deleted < ").append(DBConsts.voided)
				.append(") and time = '").append(openTime).append("' order by contactID DESC");
		try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.beforeFirst();
			rs.next();
			return rs.getInt("contactID");
			
		}catch(Exception exp) {
			return 0;
		}
	}

	public boolean checkBillStatus() {
		if(billPanel.status >= DBConsts.completed || billPanel.status < 0) {//check if the bill is .
			if (JOptionPane.showConfirmDialog(this, BarFrame.consts.ConvertClosedBillBack(), BarFrame.consts.Operator(),
		            JOptionPane.YES_NO_OPTION) != 0) {// are you sure to convert the voided bill back？
		        return false;
			}else {
				reopenBill();
			}
		}
		return true;
	}

	//Todo: Maybe it's safe to delete this method, because I think no need to touch ouputs.
	private void reopenOutput() {
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

	public void reopenBill() {
		try {
			//dump the old bill and create a new bill
			StringBuilder sql = new StringBuilder("update bill set status = ").append(DBConsts.expired)
	 				.append(" where id = ").append(billPanel.billID);
	 		PIMDBModel.getStatement().executeUpdate(sql.toString());
	 		
	 		//generat new bill with ref to dumpted bill everything else use the data on current billPane
	 		//@NOTE:no need to generata new output. because the output will be choosed by table and billIdx, so old output will goto new bill automatically.
	 		//while, if user open the dumpted old bill, then the removed item will be dissappears and new added item will appear on old bill also.
	 		//this will be a known bug. TDOO:we can make it better by searching output by billID when it's a dumpted bill. hope no one will need to check the dumpted bills.
	 		//??what do we do when removing an saved item from billPanel?
			billPanel.comment = new StringBuilder(PrintService.REF_TO).append(billPanel.billID).append("F").append("\n")
					.append("Old Subtotal:").append(billPanel.subTotal).append("\n")
					.append("Old GST:").append(billPanel.totalGst).append("\n")
					.append("Old QST:").append(billPanel.totalQst).append("\n")
					.append("Old Total:").append(billPanel.valTotlePrice.getText()).toString();
	 		int newBillID = BarFrame.instance.generateBillRecord(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
					String.valueOf(BarFrame.instance.valCurBillIdx.getText()),
					BarFrame.instance.valStartTime.getText(),
					Math.round(Float.valueOf(billPanel.valTotlePrice.getText()) * 100), 
					billPanel);
	 		
	 		//save the old money numbers
	 		int oldStatus = billPanel.status;
	 		//change something on cur billPane, then use it to print the refund bill, to let revenue know the store refund some money.
	 		billPanel.billID = newBillID;
	 		billPanel.status = DBConsts.original;
	 		
	 		//todo: waiting for operation, when print bill, will generate subtotal in endmessage, and eventually use the old subtotal to calculate the value for mev bill.
	 		
			
	 		
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

	private void createAndPrintNewOutput() {
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
		createAndPrintNewOutput();
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
			//No need to do it, will be called in method removeFromSelection() BillListPanel.curDish.changeOutputStatus(DBConsts.deleted);
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
			PayDlg.updateBill(billId, "discount", Math.round(billPanel.discount));
			PayDlg.updateBill(billId, "otherReceived", billPanel.serviceFee);
		}catch(Exception exp) {
			L.e("SalesPanel", "unexpected error when updating the totalvalue of bill.", exp);
		}
	}
    
//    public static void resetCurTable(){
//    	try {
//            //clean all empty bill (match table id and opentime, status is null, while doesn't exist in any output.).
//            //if there's an output was deleted from this bill, this bill is still considered as empty.
//            //if there's an output was completed 10
////            sql = new StringBuilder("update bill set status = ").append(DBConsts.deleted)
////            		.append(" WHERE bill.id IN ( SELECT id FROM bill WHERE tableID = ").append(BarFrame.instance.valCurTable.getText())
////    				.append(" and OPENTIME = '").append(BarFrame.instance.valStartTime.getText())
////    				.append("' and status IS NULL OR status = ").append(DBConsts.original)
////    				.append(") AND NOT EXISTS (SELECT category FROM OUTPUT WHERE (deleted IS null or deleted = ").append(DBConsts.completed)
////    				.append(" AND time = '").append(BarFrame.instance.valStartTime.getText())
////    				.append("' and SUBJECT = '").append(BarFrame.instance.valCurTable.getText()).append("')");
//            
//            //no need to be complex, all ortiginal status bills of this table should be cleaned.
//            //close table
//            BarFrame.instance.closeATable(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
//            		BarFrame.instance.valStartTime.getText());
//    	}catch(Exception exp) {
//    		ErrorUtil.write(exp);
//    	}
//    }
    
    void reLayout() {
        int panelHeight = getHeight();

        int tBtnWidht = (getWidth() - CustOpts.HOR_GAP * 10) / 10;
        int tBtnHeight = panelHeight / 10;

        // command buttons--------------
        // line 2
        btnLine_2_1.setBounds(CustOpts.HOR_GAP, panelHeight - tBtnHeight - CustOpts.VER_GAP, tBtnWidht, tBtnHeight);
        btnLine_2_2.setBounds(btnLine_2_1.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnMASTER.setBounds(btnLine_2_2.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_4.setBounds(btnMASTER.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_5.setBounds(btnLine_2_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_6.setBounds(btnLine_2_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_7.setBounds(btnLine_2_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_8.setBounds(btnLine_2_7.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_9.setBounds(btnLine_2_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        btnLine_2_10.setBounds(btnLine_2_9.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht, tBtnHeight);
        // line 1
        btnCASH.setBounds(btnLine_2_1.getX(),  btnLine_2_1.getY() - tBtnHeight - CustOpts.VER_GAP, tBtnWidht, tBtnHeight);
        btnDEBIT.setBounds(btnLine_2_2.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);
        btnVISA.setBounds(btnMASTER.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_4.setBounds(btnLine_2_4.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_5.setBounds(btnLine_2_5.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_6.setBounds(btnLine_2_6.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_7.setBounds(btnLine_2_7.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_8.setBounds(btnLine_2_8.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_9.setBounds(btnLine_2_9.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);
        btnLine_1_10.setBounds(btnLine_2_10.getX(), btnCASH.getY(), tBtnWidht, tBtnHeight);

//        btnLine_2_11.setBounds(btnLine_2_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_2.getY(), tBtnWidht, tBtnHeight);
//        btnLine_2_12.setBounds(btnLine_1_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_14.getY(), tBtnWidht, tBtnHeight);
//        btnLine_2_13.setBounds(btnLine_1_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_14.getY(), tBtnWidht, tBtnHeight);
//        btnLine_2_14.setBounds(CustOpts.HOR_GAP,, tBtnWidht, tBtnHeight);
        
        // TOP part============================
        int topAreaHeight = btnCASH.getY() - 3 * CustOpts.VER_GAP;

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
        btnCASH = new FunctionButton(BarFrame.consts.CASH());
        btnDEBIT = new FunctionButton(BarFrame.consts.DEBIT());
        btnVISA = new FunctionButton(BarFrame.consts.VISA());
        btnLine_1_4 = new FunctionButton(BarFrame.consts.SPLIT_BILL());
        btnLine_1_5 = new FunctionButton(BarFrame.consts.REMOVEITEM());
        btnLine_1_6 = new FunctionButton(BarFrame.consts.MODIFY());
        btnLine_1_7 = new JToggleButton(BarFrame.consts.DISC_ITEM());
        btnLine_1_8 = new JToggleButton(BarFrame.consts.ChangePrice());
        btnLine_1_9 = new FunctionButton(BarFrame.consts.SERVICEFEE());
        btnLine_1_10 = new FunctionButton(BarFrame.consts.PRINT_BILL());

        btnLine_2_1 = new FunctionButton(BarFrame.consts.RETURN());
        btnLine_2_2 = new FunctionButton(BarFrame.consts.AddUser());
        btnMASTER = new FunctionButton(BarFrame.consts.MASTER());
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
        add(btnCASH);
        add(btnDEBIT);
        add(btnVISA);
        add(btnLine_1_4);
        add(btnLine_1_5);
        add(btnLine_1_6);
        add(btnLine_1_7);
        add(btnLine_1_8);
        add(btnLine_1_9);
        add(btnLine_1_10);

        add(btnLine_2_1);
        add(btnLine_2_2);
        add(btnMASTER);
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

        btnCASH.addActionListener(this);
        btnDEBIT.addActionListener(this);
        btnVISA.addActionListener(this);
        btnLine_1_4.addActionListener(this);
        btnLine_1_5.addActionListener(this);
        btnLine_1_6.addActionListener(this);
        btnLine_1_7.addActionListener(this);
        btnLine_1_8.addActionListener(this);
        btnLine_1_9.addActionListener(this);
        btnLine_1_10.addActionListener(this);

        btnLine_2_1.addActionListener(this);
        btnLine_2_2.addActionListener(this);
        btnMASTER.addActionListener(this);
        btnLine_2_4.addActionListener(this);
        btnLine_2_5.addActionListener(this);
        btnLine_2_6.addActionListener(this);
        btnLine_2_7.addActionListener(this);
        btnLine_2_8.addActionListener(this);
        btnLine_2_9.addActionListener(this);
        btnLine_2_10.addActionListener(this);
        
		reLayout();
    }

    private FunctionButton btnCASH;
    private FunctionButton btnDEBIT;
    private FunctionButton btnVISA;
    private FunctionButton btnLine_1_4;
    private FunctionButton btnLine_1_5;
    private FunctionButton btnLine_1_6;
    public JToggleButton btnLine_1_7;
    JToggleButton btnLine_1_8;
    private FunctionButton btnLine_1_9;
    private FunctionButton btnLine_1_10;

    private FunctionButton btnLine_2_1;
    private FunctionButton btnLine_2_2;
    private FunctionButton btnMASTER;
    private FunctionButton btnLine_2_4;
    private FunctionButton btnLine_2_5;
    private FunctionButton btnLine_2_6;
    private FunctionButton btnLine_2_7;
    private FunctionButton btnLine_2_8;
    private FunctionButton btnLine_2_9;
    private FunctionButton btnLine_2_10;
    
    public BillPanel billPanel;
}
