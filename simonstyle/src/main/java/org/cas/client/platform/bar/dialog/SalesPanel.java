package org.cas.client.platform.bar.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.action.UpdateItemDiscountAction;
import org.cas.client.platform.bar.action.UpdateItemPriceAction;
import org.cas.client.platform.bar.beans.CategoryToggleButton;
import org.cas.client.platform.bar.beans.FunctionButton;
import org.cas.client.platform.bar.beans.MenuButton;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.model.Dish;
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
        		outputStatusCheck();
    			billStatusCheck();
        		
        		//if it's already paid, show comfirmDialog.
        		if(billPanel.status >= 100)
        			if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.ConfirmPayAgain(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0)
            			return;
        		
        		//check the pay dialog is already visible, if yes, then update bill received values.
        		if(BarFrame.payCashDlg.isVisible()) {
        			BarFrame.payCashDlg.updateBill(billPanel.getBillId());
        		}
        		//show dialog-------------------------------------
         		BarFrame.payCashDlg.setFloatSupport(true);
         		if(o == btnLine_1_1) {
         			BarFrame.payCashDlg.setTitle(BarFrame.consts.EnterCashPayment());
         		}else if(o == btnLine_1_2) {
         			BarFrame.payCashDlg.setTitle(BarFrame.consts.EnterDebitPayment());
         		}else if(o == btnLine_1_3) {
         			BarFrame.payCashDlg.setTitle(BarFrame.consts.EnterVisaPayment());
         		}else if(o == btnLine_2_3) {
         			BarFrame.payCashDlg.setTitle(BarFrame.consts.EnterMasterPayment());
         		}
         		BarFrame.payCashDlg.setVisible(true);
         		
         		//init payDialog content base on bill.
         		BarFrame.payCashDlg.initContent(billPanel);
        		
        	} else if (o == btnLine_1_4) {		//split bill
        		//check if there unsaved dish, and give warning.
        		if(BillListPanel.curDish == null) {
            		if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.UnSavedRecordFound(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0)
            			return;
        		}
        		if(billPanel.getNewDishes().size() > 0) {
        			JOptionPane.showMessageDialog(this, BarFrame.consts.UnSendRecordFound());
        			return;
        		}
        		
        		outputStatusCheck();
    			billStatusCheck();
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
         		BarFrame.numberPanelDlg.setModal(true);
         		BarFrame.numberPanelDlg.setVisible(true);
         		
         		try {
     				String curContent = BarFrame.numberPanelDlg.curContent;
     				if(curContent == null || curContent.length() == 0)
     					return;
             		float serviceFee = Float.valueOf(curContent);
             		billPanel.serviceFee = (int)(serviceFee * 100);
             		billPanel.updateTotleArea();
             		
             		outputStatusCheck();
             		billStatusCheck();
             		
             	}catch(Exception exp) {
                 	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
             		return;
             	}
        		
        	}else if (o == btnLine_1_10) { // print bill
        		outputStatusCheck();
        		billStatusCheck();
        		billPanel.printBill(BarFrame.instance.valCurTable.getText(), BarFrame.instance.valCurBill.getText(), BarFrame.instance.valStartTime.getText());
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
            	
            } else if(o == btnLine_2_2) {		//Add client
				BarFrame.instance.valCurBill.setText("0");
				BarFrame.instance.switchMode(2);
				
        	} else if (o == btnLine_2_4) { // cancel all
            	if(billPanel.orderedDishAry.size() > 0) {
            		int lastSavedRow = billPanel.orderedDishAry.size() - 1 - billPanel.getNewDishes().size();
            		//update array first.
            		for(int i = billPanel.orderedDishAry.size() - 1; i > lastSavedRow; i--) {
            			billPanel.orderedDishAry.remove(i);
            		}
            		//update the table view
            		int tColCount = billPanel.tblSelectedDish.getColumnCount();
            		int tValidRowCount = billPanel.orderedDishAry.size(); // get the used RowCount
            		Object[][] tValues = new Object[tValidRowCount][tColCount];
            		for (int r = 0; r < tValidRowCount; r++) {
            			for (int c = 0; c < tColCount; c++)
            				tValues[r][c] = billPanel.tblSelectedDish.getValueAt(r, c);
            		}
            		billPanel.tblSelectedDish.setDataVector(tValues, billPanel.header);
            		billPanel.resetColWidth(billPanel.getWidth());
            		billPanel.tblSelectedDish.setSelectedRow(tValues.length - 1);
            		billPanel.updateTotleArea();
            	}else {
            		//update db.
            		if(isLastBillOfCurTable()) {
            			resetCurTableDBStatus();
            		}
            		BarFrame.instance.switchMode(0);
            	}
            	
            } else if (o == btnLine_2_5) { // void all include saved ones
        		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
        				BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
	                 return;	
	            }
    	        //update db, delete relevant orders.
            	for (Dish dish : billPanel.orderedDishAry) {
            		dish.setCanceled(true);
            		Dish.deleteRelevantOutput(dish);
				}

    			billPanel.sendDishesToKitchen(billPanel.orderedDishAry, true);
            	
            	//if the bill amount is 1, cancel the selected status of the table.
        		if(isLastBillOfCurTable()) {
        			resetCurTableDBStatus();
        		}
            	BarFrame.instance.switchMode(0);
            	
            } else if (o == btnLine_2_6) {		//open drawer
            	BarUtil.openMoneyBox();
            	
            } else if (o == btnLine_2_7) {//disc bill
         		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.DISCOUNT_BILL());
         		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.VolumnDiscountNotice());
         		BarFrame.numberPanelDlg.setBtnSource(null);
         		BarFrame.numberPanelDlg.setFloatSupport(true);
         		BarFrame.numberPanelDlg.setModal(true);
         		BarFrame.numberPanelDlg.setVisible(true);
         		
         		try {
     				String curContent = BarFrame.numberPanelDlg.curContent;
     				if(curContent == null || curContent.length() == 0)
     					return;
             		float discount = Float.valueOf(curContent);
             		billPanel.discount = (int)(discount * 100);
             		billPanel.updateTotleArea();
             		
             		outputStatusCheck();
             		billStatusCheck();
             		
             	}catch(Exception exp) {
                 	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
             		return;
             	}
            }else if(o == btnLine_2_8) {	//refund
            	//check if it's already paid.
            	if(billPanel.orderedDishAry.size() < 1 || billPanel.orderedDishAry.get(0).getBillID() == 0) {
            		JOptionPane.showMessageDialog(this, BarFrame.consts.NotPayYet());
            		return;
            	}

         		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.Refund());
         		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.RefundNotice());
            	BarFrame.numberPanelDlg.setBtnSource(null);
         		BarFrame.numberPanelDlg.setFloatSupport(true);
         		BarFrame.numberPanelDlg.setModal(true);
         		BarFrame.numberPanelDlg.setVisible(true);
         		
         		try {
     				String curContent = BarFrame.numberPanelDlg.curContent;
     				if(curContent == null || curContent.length() == 0)
     					return;
             		float refund = Float.valueOf(curContent);
             		
             		// get out existing status.
             		String sql = "select * from bill where id = " + billPanel.orderedDishAry.get(0).getBillID();
                    ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
                    rs.beforeFirst();
                    rs.next();
                    int status = rs.getInt("status");
                    if(status < 0) {
                    	if (JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.AllreadyRefund() + BarOption.getMoneySign() + (0-status)/100.0, DlgConst.DlgTitle,
    		                    JOptionPane.YES_NO_OPTION) != 0) {// allready refunded, sure to refund again?
    						return;
    					}else {
    						status -= (int)(refund * 100);
    					}
                    }else {
                    	status = 0 - (int)(refund * 100);
                    }
                    
             		sql = "update bill set status = " + status + " where id = " + billPanel.orderedDishAry.get(0).getBillID();
             		PIMDBModel.getStatement().execute(sql);
             		BarUtil.openMoneyBox();
             	}catch(Exception exp) {
                 	JOptionPane.showMessageDialog(BarFrame.numberPanelDlg, DlgConst.FORMATERROR);
             		return;
             	}
            	
            } else if (o == btnLine_2_9) {//more
            	new MoreButtonsDlg(this).show((FunctionButton)o);
            	
            } else if (o == btnLine_2_10) {//send
        		outputStatusCheck();
            	if(BarOption.isFastFoodMode()) {
    		    	BarFrame.instance.valCurBill.setText(String.valueOf(BillListPanel.getANewBillNumber()));
    		    	billPanel.initContent();
    		    }else {
    		    	BarFrame.instance.switchMode(0);
    		    }
            }
        }
        //JToggleButton-------------------------------------------------------------------------------------
        else if(o instanceof JToggleButton) {	//
        	 if (o == btnLine_1_7) {
         		if(BillListPanel.curDish == null) {//check if there's an item selected.
         			JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
         			return;
         		}

         		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.DISCITEM());
         		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.DISC_ITEMNotice());
         		BarFrame.numberPanelDlg.setBtnSource(btnLine_1_7);//pomp up a numberPanelDlg
         		BarFrame.numberPanelDlg.setFloatSupport(true);
         		BarFrame.numberPanelDlg.setModal(false);
         		//should no record selected, select the last one.
         		BarFrame.numberPanelDlg.setVisible(btnLine_1_7.isSelected());	//@NOTE: it's not model mode.
         		BarFrame.numberPanelDlg.setAction(new UpdateItemDiscountAction(btnLine_1_7, billPanel));
         		
             }else if(o == btnLine_1_8) {
            	if(BillListPanel.curDish == null) {//check if there's an item selected.
          			JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
          			return;
          		}
         		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.ChangePrice());
         		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.ChangePriceNotice());
            	BarFrame.numberPanelDlg.setBtnSource(btnLine_1_8);//pomp up a numberPanelDlg
         		BarFrame.numberPanelDlg.setFloatSupport(true);
         		BarFrame.numberPanelDlg.setModal(false);
         		//should no record selected, select the last one.
         		BarFrame.numberPanelDlg.setVisible(btnLine_1_8.isSelected());	//@NOTE: it's not model mode.
         		BarFrame.numberPanelDlg.setAction(new UpdateItemPriceAction(btnLine_1_8, billPanel));
        		
        	}
        }
    }

    //to make sure bill saved.
    //and make sure new added dish will be updated with new information.
	private void billStatusCheck() {
		//if the bill has not been generated, generate a bill.@because: some information on the payment dialog is fetched from bill record.
		//@NODE: normally should only generate the bill when clicked the ok of paymentdlg, while I don't mind to have the bill generated earlier
		//as we need to care if there's bill exist anywhere when splitting bill or moving items.
		//@because: we might have half completed bill.(haven't paid enough, then split, then continue pay)
		if(billPanel.getBillId() == 0) {
			int newBillID = billPanel.generateBillRecord(BarFrame.instance.valCurTable.getText(), BarFrame.instance.valCurBill.getText(), BarFrame.instance.valStartTime.getText());
			billPanel.updateOutputRecords(newBillID);
		}
		else {//if bill record already exist, and there's new dish added, or discount, service fee changed.... update the total value.
			//if(dishes != null && dishes.size() > 0) {
			updateBillRecord();		//in case if added service fee or discout of bill.
		}
		billPanel.initContent();	//always need to initContent, to make sure dish has new price. e.g. when adding a dish to a printed bill,
									//and click print bill immediatly, will need the initContent. 
	}

	private void outputStatusCheck() {
		//if there's any new bill, send it to kitchen first, and this also made the output generated.
		List<Dish> dishes = billPanel.getNewDishes();
		if (dishes != null && dishes.size() > 0) {
			billPanel.sendNewDishesToKitchen(dishes);
		}
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
			billPanel.removeFromSelection(billPanel.tblSelectedDish.getSelectedRow());
			//update bill info, must be after the screen update, because will get total from screen.
			updateBillRecord();
		}else {
			//only do clean from screen, because the output not generated yet, and will not affect the toltal in bill.
			billPanel.removeFromSelection(billPanel.tblSelectedDish.getSelectedRow());
		}
	}

	private void updateBillRecord() {
		int billId = billPanel.orderedDishAry.get(0).getBillID();
		try {
			StringBuilder sql = new StringBuilder("update bill set total = ")
					.append((int)(Float.valueOf(billPanel.valTotlePrice.getText()) * 100)).append(" where id = ").append(billId);
			PIMDBModel.getStatement().execute(sql.toString());
			
			sql = new StringBuilder("update bill set discount = ").append(billPanel.discount).append(" where id = ").append(billId);
			PIMDBModel.getStatement().execute(sql.toString());

			sql = new StringBuilder("update bill set otherReceived = ").append(billPanel.serviceFee).append(" where id = ").append(billId);
			PIMDBModel.getStatement().execute(sql.toString());
			
		}catch(Exception exp) {
			L.e("SalesPanel", "unexpected error when updating the totalvalue of bill.", exp);
		}

	}

    public static boolean isLastBillOfCurTable(){
    	int num = 0;
    	try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
            ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '"
                    + BarFrame.instance.valCurTable.getText() + "' and deleted = false and time = '"
            		+ BarFrame.instance.valStartTime.getText() + "' order by contactID");
			rs.afterLast();
			rs.relative(-1);
			num = rs.getRow();
		} catch (Exception exp) {
			ErrorUtil.write(exp);
		}
    	return num <= 1;
    }
    
    
    public static void resetCurTableDBStatus(){
    	try {
        	Statement smt =  PIMDBModel.getStatement();
            smt.executeQuery("update dining_Table set status = 0 WHERE name = '" + BarFrame.instance.valCurTable.getText() + "'");
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

        Double tableWidth = (Double) CustOpts.custOps.hash2.get("TableWidth");
        tableWidth = (tableWidth == null || tableWidth < 0.2) ? 0.4 : tableWidth;
        
        billPanel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
                (int) (getWidth() * tableWidth), topAreaHeight);
        
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
        btnLine_1_6 = new FunctionButton(BarFrame.consts.Modify());
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
        setBackground(BarOption.getBK("Sales"));
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
    private JToggleButton btnLine_1_8;
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
