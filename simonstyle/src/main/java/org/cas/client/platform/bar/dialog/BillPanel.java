package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.ArrowButton;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pimview.pimtable.PIMTableRenderAgent;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PaneConsts;

public class BillPanel extends JPanel implements ActionListener, ComponentListener, PIMTableRenderAgent, ListSelectionListener, MouseMotionListener, MouseListener{
	
	SalesPanel salesPanel;
	BillListPanel billListPanel;
	public JToggleButton billButton;
	
	private int stepCounter;
	private int minStepCounter = CustOpts.custOps.getValue("minMoveStep") == null ? 5 : Integer.valueOf((String)CustOpts.custOps.getValue("minMoveStep"));

    private boolean isDragging;
    public ArrayList<Dish> orderedDishAry = new ArrayList<Dish>();
    
    //Bill property (not for specific item).the info should be retrieved from bill record if have.
    public int billID;
    public float discount;
    public float totalGst;
	public float totalQst;
	public float subTotal;
	public int tip;
	public int serviceFee;
    
    int received;
    int cashback;
    public String comment = "";
    public int status = DBConsts.original;
    
	public BillPanel(SalesPanel salesPanel) {
		this.salesPanel = salesPanel;
		initComponent();
	}

	public BillPanel(BillListPanel billListPanel, JToggleButton billButton) {
		this.billListPanel = billListPanel;
		this.billButton = billButton;
		initComponent();
	}
	
	public void printBill(String tableID, String billIndex, String opentime, boolean isToCustomer) {
		
        if(orderedDishAry.size() == 0){
            return;
        }
        if(status < 0 || status >= DBConsts.completed) {
        	PrintService.exePrintInvoice(this, false, isToCustomer, true);
        }else {
	        PrintService.exePrintBill(this, orderedDishAry);
	        status = DBConsts.billPrinted;
			//update the total price of the target bill, 
			//---because when add dish into the billPane, bill in db will not get updated.
			StringBuilder sql = new StringBuilder("update bill set total = ")
					.append(Math.round(Float.valueOf(valTotlePrice.getText()) * 100))
					.append(", discount = ").append(discount)
					.append(", serviceFee = ").append(serviceFee)
					.append(", status = ").append(DBConsts.billPrinted)//so the invoice can be saved.
					.append(", comment = comment + ' ").append(PrintService.REF_TO).append(billID).append("'")
					.append(" where tableID = '").append(tableID).append("'")
					.append(" and BillIndex = '").append(billIndex).append("'")
					.append(" and openTime = '").append(opentime).append("'")
					.append(" and (status is null or status = ").append(DBConsts.original).append(")");
			try {
				PIMDBModel.getStatement().executeUpdate(sql.toString());
			}catch(Exception e) {
				L.e("BillPane", "Excepioint in print bill:" + sql, e);
			}
        }
	}

	//will not duplicat the comment property of current bill.
	//when need to creat an empty new bill instead of  expire one and regenerate one and at the same time should call this.
	public int generateEmptyBillRecord(String tableID, String billIndex, String opentime) {
		//generate a bill in db and update the output with the new bill id
		String createtime = BarOption.df.format(new Date());
		try {
			StringBuilder sql = new StringBuilder(
	            "INSERT INTO bill(createtime, tableID, BillIndex, EMPLOYEEID, opentime, comment) VALUES ('")
				.append(createtime).append("', '")
	            .append(tableID).append("', '")	//table
	            .append(billIndex).append("', ")			//billIdx
	            .append(LoginDlg.USERID).append(", '")		//emoployid
	            .append(opentime).append("', '')");				//comment
		
			PIMDBModel.getStatement().executeUpdate(sql.toString());
			
		   	sql = new StringBuilder("Select id from bill where createtime = '").append(createtime)
		   			.append("' and billIndex = '").append(billIndex).append("'");
	        ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
	        rs.beforeFirst();
	        rs.next();
	        return rs.getInt("id");
		 }catch(Exception e) {
			ErrorUtil.write(e);
			return -1;
		 }
	}
	
	public int cloneCurrentBillRecord(String tableID, String billIndex, String opentime, int total) {
		if(total < 0) {
			total = Math.round(Float.valueOf(valTotlePrice.getText()) * 100);
		}
		
		//get other field out from db:
		StringBuilder sql = new StringBuilder("select * from bill where id = " + billID);
    	try {
    		ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.next();
        	int cashReceived = rs.getInt("cashReceived");
        	int debitReceived = rs.getInt("debitReceived");
            int visaReceived = rs.getInt("visaReceived");
            int masterReceived = rs.getInt("masterReceived");
            int otherreceived = rs.getInt("otherreceived");
        	int cashBack = rs.getInt("cashback");
            int tip = rs.getInt("tip");
        
			//generate a bill in db and update the output with the new bill id
			String createtime = BarOption.df.format(new Date());
			sql = new StringBuilder(
	            "INSERT INTO bill(createtime, tableID, BillIndex, total, discount, tip, serviceFee, cashback, EMPLOYEEID, Comment,")
	            .append(" opentime, cashReceived, debitReceived, visaReceived, masterReceived, otherreceived) VALUES ('")
				.append(createtime).append("', '")
	            .append(tableID).append("', '")	//table
	            .append(billIndex).append("', ")			//bill
	            .append(total).append(", ")//Math.round(Float.valueOf(valTotlePrice.getText()) * 100)/num).append(", ")	//total
	            .append(discount).append(", ")
	            .append(tip).append(", ")
	            .append(serviceFee).append(", ")			//currently used for storing service fee -_-!
	            .append(cashBack).append(", ")	//discount
	            .append(LoginDlg.USERID).append(", '")		//emoployid
	            .append(comment).append("', '")
	            .append(opentime).append("', ")
	            .append(cashReceived).append(", ")
	            .append(debitReceived).append(", ")
	            .append(visaReceived).append(", ")
	            .append(masterReceived).append(", ")
	            .append(otherreceived).append(")");				//content
		
			PIMDBModel.getStatement().executeUpdate(sql.toString());
			
		   	sql = new StringBuilder("Select id from bill where createtime = '").append(createtime)
		   			.append("' and billIndex = '").append(billIndex).append("'");
            rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.beforeFirst();
            rs.next();
            return rs.getInt("id");
		 }catch(Exception e) {
			ErrorUtil.write(e);
			return -1;
		 }
	}

	
	void sendDishToKitchen(Dish dish, boolean isCancelled) {
		List<Dish> dishes = new ArrayList<Dish>();
		dishes.add(dish);
		sendDishesToKitchen(dishes, isCancelled);
	}
	
	//send to printer
	void sendDishesToKitchen(List<Dish> dishes, boolean isCancelled) {
		//prepare the printing String and do printing
		String curTable = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
		String curCustomerIdx = BarFrame.instance.valCurBillIdx.getText();
		String waiterName = BarFrame.instance.valOperator.getText();
		PrintService.exePrintOrderList(dishes, curTable, curCustomerIdx, waiterName, isCancelled);
	}
	
	//save to db output
	void persistDishesToOutput(List<Dish> dishes) {
		try {
		    for (Dish dish : dishes) {
		    	String curBillIndex = BarFrame.instance.getCurBillIndex();
		    	Dish.createOutput(dish, curBillIndex);	//at this moment, the num should have not been soplitted.
		        //in case some store need to stay in the interface after clicking the send button. 
                StringBuilder sql = new StringBuilder("Select id from output where SUBJECT = '")
                    .append(BarFrame.instance.cmbCurTable.getSelectedItem().toString()).append("' and CONTACTID = ")
                    .append(curBillIndex).append(" and PRODUCTID = ")
                    .append(dish.getId()).append(" and AMOUNT = ")
                    .append(dish.getNum()).append(" and TOLTALPRICE = ")
                    .append(dish.getTotalPrice()).append(" and DISCOUNT = ")
                    .append(dish.getDiscount()).append(" and EMPLOYEEID = ")
                    .append(LoginDlg.USERID).append(" and TIME = '")
                    .append(dish.getOpenTime()).append("'");
                ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
                rs.beforeFirst();
                while (rs.next()) {
                	dish.setOutputID(rs.getInt("id"));
                }
                
                rs.close();
		    }
		}catch(Exception exp) {
			JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
		    exp.printStackTrace();
		}
	}
	
	List<Dish> getNewDishes() {
		List<Dish> newDishes = new ArrayList<Dish>();
		for (Dish dish : orderedDishAry) {
			if(dish.getOutputID() > -1)	//if it's already saved into db, ignore.
				continue;
			else {
				newDishes.add(dish);
			}
		}
		return newDishes;
	}
	
    @Override
	public void actionPerformed(ActionEvent e) {

        Object o = e.getSource();
		if(o instanceof ArrowButton) {
	        if(o == btnMore) {
            	if(!checkStatus()) {
            		return;
            	}
	        	int selectedRow =  tblBillPanel.getSelectedRow();
				if(orderedDishAry.get(selectedRow).getOutputID() >= 0) {	//already saved
					BarFrame.setStatusMes(BarFrame.consts.SendItemCanNotModify());
					addContentToList(orderedDishAry.get(selectedRow));
				}else {			//not saved yet  //NOTE:getNum() couldn't be bigger than 10000, if it's saved, + button will insert a new line.
					int tQTY = orderedDishAry.get(selectedRow).getNum() + 1;
					int row = tblBillPanel.getSelectedRow();
					Dish dish = orderedDishAry.get(row);
					dish.setNum(tQTY);
					dish.setTotalPrice((dish.getPrice() - dish.getDiscount()) * tQTY);
					
					tblBillPanel.setValueAt(tQTY % BarOption.MaxQTY + "x", row, 0);
					tblBillPanel.setValueAt(BarOption.getMoneySign() + BarUtil.formatMoney((orderedDishAry.get(selectedRow).getPrice() - orderedDishAry.get(selectedRow).getDiscount()) * tQTY/100f), row, 3);
				}
				updateTotleArea();
				tblBillPanel.setSelectedRow(selectedRow);
	        } else if (o == btnLess) {
	        	if(!checkStatus()) {
            		return;
            	}
	    		int selectedRow =  tblBillPanel.getSelectedRow();
	    		Dish dish = orderedDishAry.get(selectedRow);
				if(dish.getOutputID() >= 0) {	//if it's already send, then do the removePanel.
					salesPanel.removeItem();
				}else {
					if(orderedDishAry.get(selectedRow).getNum() == 1) {
						if("true".equals(CustOpts.custOps.getValue("noticeForLastOne"))) {
							if (JOptionPane.showConfirmDialog(this, BarFrame.consts.COMFIRMDELETEACTION2(), DlgConst.DlgTitle,
				                    JOptionPane.YES_NO_OPTION) != 0) {
								tblBillPanel.setSelectedRow(-1);
								return;
							}
						}
						removeFromSelection(selectedRow);
					} else {
						int tQTY = orderedDishAry.get(selectedRow).getNum() - 1;
						int row = tblBillPanel.getSelectedRow();
						orderedDishAry.get(row).setNum(tQTY);
						tblBillPanel.setValueAt(tQTY == 1 ? "" : tQTY + "x"  , row, 0);		
						tblBillPanel.setValueAt(BarOption.getMoneySign() + BarUtil.formatMoney((orderedDishAry.get(selectedRow).getPrice() - orderedDishAry.get(selectedRow).getDiscount()) * tQTY/100f), row, 3);
					}
				}
				updateTotleArea();
	        }
        }else if(o == billButton){		//when bill button on top are clicked.
        	if(billListPanel != null && billListPanel.btnSplitItem.isSelected()) {
        		billButton.setSelected(!billButton.isSelected());
        		return;
        	}
        	
    		BarFrame.instance.valCurBillIdx.setText(((JToggleButton)o).getText());
            BarFrame.instance.switchMode(2);
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
	
//	@deprecated
	//was used when click send button, and found isFastFoodMode==true, then instead of returning back to table view, stay in
	//the sales view. now I decide to use unified processor : initContent().
//    void resetTableArea() {
//    	resetStatus();
//        Object[][] tValues = new Object[0][tblSelectedDish.getColumnCount()];
//        tblSelectedDish.setDataVector(tValues, header);
//        resetColWidth(scrContent.getWidth());
//        updateTotleArea();
//    }
    
    //table selection listener---------------------
	@Override
	public void valueChanged(ListSelectionEvent e) {
		//adjust more and less button status.
		int selectedRow =  tblBillPanel.getSelectedRow();
		btnMore.setEnabled(selectedRow >= 0 && selectedRow <= orderedDishAry.size());
		btnLess.setEnabled(selectedRow >= 0 && selectedRow <= orderedDishAry.size());
		
		//if no row selected, reset curdish and return.
		if(!btnMore.isEnabled()) {	//some time the selectedRow can be -1.
			BillListPanel.curDish = null;
			return;
		}

		//if in salesPanel mode, then adjust it's curDish and numberDlg if it's on show.
		//if in billListPanel mode, then change bill selection status and do moving dish.
		Dish selectedDish = orderedDishAry.get(selectedRow);
		if(salesPanel != null) {
			BillListPanel.curDish = selectedDish;
			if( BarFrame.numberPanelDlg.isVisible()) {	//if qty button seleted.
				Object obj = tblBillPanel.getValueAt(selectedRow,0);
				//update the qty in qtyDlg.
				if(obj != null)
					BarFrame.numberPanelDlg.setContents(obj.toString());
			}
			if( salesPanel.btnLine_1_7.isSelected()) {
				Object obj = tblBillPanel.getValueAt(selectedRow,2);
				//update the discount in qtyDlg.
				if(obj != null)
					BarFrame.numberPanelDlg.setContents(obj.toString());
			}
		}else if(billListPanel != null) {
			if(billListPanel.btnSplitItem.isSelected()) {	//if in splite item mode, then do nothing but select the bill button.
				billButton.setSelected(!billButton.isSelected());
				return;
			}
			//only not in splitting can reach here. 
 			if(BillListPanel.curDish != null && billListPanel.getCurBillPanel() != null && billListPanel.getCurBillPanel() != this) {
				billListPanel.moveDishToBill(this);
				BillListPanel.curDish = null;
			}else {
				billButton.setSelected(true);
				BillListPanel.curDish = selectedDish;
			}
		}
	}

	//table row color----------------------------------------------------------
	@Override
	public Color getBackgroundAtRow(int row) {
		return null;
	}

	@Override
	public Color getForegroundAtRow(int row) {
		if(row < orderedDishAry.size()) {
			Dish dish = orderedDishAry.get(row);
			if(dish.getOutputID() > -1) {
				return Color.BLACK;
			}else {
				return Color.RED;
			}
		}else
			return null;
	}

	@Override
	//this method is used for deleting an item in the billPanel by a drag action.
	public void mouseReleased(MouseEvent e) {
		stepCounter = 0;
		if(isDragging == true) {
			isDragging = false;
			ListSelectionModel selectionModel = ((PIMTable)e.getSource()).getSelectionModel();
			int selectedRow =  selectionModel.getMinSelectionIndex();
			if(selectedRow < 0 || selectedRow >= orderedDishAry.size()) 
				return;
			
			if(salesPanel != null && !BarFrame.numberPanelDlg.isVisible()) {	//if qty button not seleted.
				if(orderedDishAry.get(selectedRow).getOutputID() >= 0) {
					if (JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle,
		                    JOptionPane.YES_NO_OPTION) != 0) {// 确定删除吗？
						tblBillPanel.setSelectedRow(-1);
						return;
					}
				}

	        	if(!checkStatus()) {	//check if it's bill printed, if yes, need to reopen a bill.
            		return;
            	}
	        	
				removeFromSelection(selectedRow);
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == scrContent.getViewport()) {
			if(billListPanel != null) {
				if(billListPanel.btnSplitItem.isSelected()) {	//if in splite item mode, then do nothing but select the bill button.
					billButton.setSelected(!billButton.isSelected());
					return;
				}
				//if not in split item mode (split item button not pressed.)
	 			if(BillListPanel.curDish != null && billListPanel.getCurBillPanel() != this) {	//this there's already an item ready for move.
					billListPanel.moveDishToBill(this);
					BillListPanel.curDish = null;
				}else {	//no current item ready for split, then just select the. 
					billButton.setSelected(!billButton.isSelected());
					if(billButton.isSelected()) {
						BarFrame.instance.valCurBillIdx.setText(billButton.getText());
						BarFrame.instance.curBillID = billID;
					}else {
						BillPanel panel = billListPanel.getCurBillPanel();
						if(panel != null) {
							BarFrame.instance.valCurBillIdx.setText(panel.billButton.getText());
							BarFrame.instance.curBillID = panel.billID;
						}else {
							BarFrame.instance.valCurBillIdx.setText("");
							BarFrame.instance.curBillID = 0;
						}
					}
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		stepCounter++;
		if(stepCounter > minStepCounter ) {
			stepCounter = 0;
			isDragging = true;
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	
    //add dish into the billPanel, tiggered by "+"button or the bubttons on menuPanel.
    void addContentToList(Dish dish) {
        int tRowCount = tblBillPanel.getRowCount(); // add content to the table.
        int tColCount = tblBillPanel.getColumnCount();
        int tValidRowCount = getUsedRowCount(); // get the used RowCount
        if (tRowCount == tValidRowCount) { // no line is empty, add a new Line.
            Object[][] tValues = new Object[tRowCount + 1][tColCount];
            for (int r = 0; r < tRowCount; r++)
                for (int c = 0; c < tColCount; c++)
                    tValues[r][c] = tblBillPanel.getValueAt(r, c);
            tblBillPanel.setDataVector(tValues, header);
            resetColWidth(scrContent.getWidth());
        }else {
        	tRowCount--;
        }
        
        Dish newDish = dish.clone();		//@NOTE: incase the cloned dish contains outpurID properties.
        newDish.setOutputID(-1);
        newDish.setNum(1);
        int price = dish.getPrice();
        if("true".equals(dish.getPrompPrice()) && BarOption.isTreatPricePromtAsTaxInclude()) {
        	price = (int)Math.round(price / ((100 + BarOption.getGST() + BarOption.getQST()) / 100.0));
        }
        newDish.setTotalPrice(price * 1);
        newDish.setOpenTime(BarFrame.instance.valStartTime.getText());
        newDish.setBillIndex(BarFrame.instance.valCurBillIdx.getText());
        newDish.setBillID(billID);
        orderedDishAry.add(newDish);				//valueChanged process. not being cleared immediately-----while now dosn't matter
        BillListPanel.curDish = newDish;
        
        //update the interface.
        tblBillPanel.setValueAt("", tValidRowCount, 0); // set the count.
        tblBillPanel.setValueAt(dish.getLanguage(CustOpts.custOps.getUserLang()), tValidRowCount, 1);// set the Name.
        tblBillPanel.setValueAt(dish.getSize() > 1 ? dish.getSize() : "", tValidRowCount, 2); // set the count.
        tblBillPanel.setValueAt(BarOption.getMoneySign() + BarUtil.formatMoney(price/100f), tValidRowCount, 3); // set the price.
        
        updateTotleArea();								//because value change will not be used to remove the record.
        SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
		        tblBillPanel.setSelectedRow(orderedDishAry.size() - 1);
		        if("true".equals(newDish.getPrompMofify())) {
		        	new AddModificationDialog(BarFrame.instance, BillListPanel.curDish.getModification()).setVisible(true);
		        }
		        if("true".equals(newDish.getPrompPrice()) && !BarOption.isTreatPricePromtAsTaxInclude()) {
		        	salesPanel.btnLine_1_8.setSelected(true);
		        	salesPanel.showPriceChangeDlg();
		        }
			}
		});
    }

	public void sendNewOrdersToKitchenAndDB(List<Dish> dishes) {
		//if all record are new, means it's adding a new bill.otherwise, it's adding output to exixting bill.
//		if(dishes.size() == orderedDishAry.size()) {	//didn't set the idx when bill created, because don't wanto display idx if there's only 1 bill.
//		    BarFrame.instance.valCurBillIdx.setText(String.valueOf(BillListPanel.getANewBillIdx()));
//		}
		sendDishesToKitchen(dishes, false);
		persistDishesToOutput(dishes);
		tblBillPanel.repaint();//to update the color of dishes, it's saved, so it's not red anymore.
	}

    public void updateTotleArea() {
    	float gstRate = BarOption.getGST();
    	float qstRate = BarOption.getQST();
    	totalGst = 0;
    	totalQst = 0;
    	subTotal = 0;
    	
    	for(Dish dish: orderedDishAry) {
    		//get out the num.
    		int num = dish.getNum();
    		int pK = num /(BarOption.MaxQTY * 100);
    		if(num > BarOption.MaxQTY * 100) {
    			num = num %(BarOption.MaxQTY * 100);
    		}
    		int pS = num /BarOption.MaxQTY;
    		if(num > BarOption.MaxQTY) {
    			num = num % BarOption.MaxQTY;
    		}
    		
    		int totalPrice = dish.getTotalPrice();
    		float gst = totalPrice * (dish.getGst() * gstRate / 100f);	//an item could have a different tax rate.
    		float qst = totalPrice * (dish.getQst() * qstRate / 100f);
    		
    		if(BarOption.isDiscountAffectTax()) {
    			gst += dish.getDiscount() * (dish.getGst() * gstRate / 100f);
    			qst += dish.getDiscount() * (dish.getQst() * gstRate / 100f);
    		}
    		
//@NOTE: the price is already the final item totalprice (even the discount calculated), so no need to devide again.
//    		if(pS > 0) {
//    			price /= pS;
//    			gst /= pS;
//    			qst /= pS;
//    		}
//    		if(pK > 0) {
//    			price /= pK;
//    			gst /= pK;
//    			qst /= pK;
//    		}
    		
    		subTotal += totalPrice;
    		totalGst += gst;
    		totalQst += qst;
    	}
    	
    	subTotal -= discount;
    	subTotal += serviceFee;
		
    	if(BarOption.isDiscountAffectTax()) {
    		totalGst -= Math.round(discount * gstRate / 100f);
    		totalQst -= Math.round(discount * qstRate / 100f);
    	}
    	
    	if(BarOption.isServiceFeeAffectTax()) {
    		totalGst += Math.round(serviceFee * gstRate / 100f);
    		totalQst += Math.round(serviceFee * qstRate / 100f);
    	}
    	totalGst = Math.round(totalGst);
    	totalQst = Math.round(totalQst);
    	lblDiscount.setText(discount > 0 ? BarFrame.consts.Discount() + " : -" + BarOption.getMoneySign() + BarUtil.formatMoney((discount)/100f) : "");
    	lblServiceFee.setText(serviceFee > 0 ? BarFrame.consts.ServiceFee() + " : " + BarOption.getMoneySign() + BarUtil.formatMoney((serviceFee)/100f) : "");
    	lblSubTotle.setText(BarFrame.consts.Subtotal() + " : " + BarOption.getMoneySign() + BarUtil.formatMoney(subTotal/100f));
    	lblTPS.setText(BarFrame.consts.GST() + " : " + BarOption.getMoneySign() + BarUtil.formatMoney(totalGst/100f));
    	lblTVQ.setText(BarFrame.consts.QST() + " : " + BarOption.getMoneySign() + BarUtil.formatMoney(totalQst/100f));
        int total = Math.round(subTotal + totalGst + totalQst);
        valTotlePrice.setText(BarUtil.formatMoney((total)/100f));
    }
    
    void initContent() {
    	resetProperties();
    	//get outputs of current table and bill id.
    	StringBuilder sql = null;
		try {
			String billIndex = billButton == null ? BarFrame.instance.getCurBillIndex() : billButton.getText();
			//used deleted <= 1, means both uncompleted and normally completed will be displayed, unnormally delted recored will be delted = 100
			String tableName = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
			String openTime = BarFrame.instance.valStartTime.getText();
			boolean isShowingExpiredBill = BarFrame.instance.isShowingAnExpiredBill;
			sql = new StringBuilder("select * from OUTPUT, PRODUCT where OUTPUT.SUBJECT = '")
				.append(tableName)
				.append("' and CONTACTID = ").append(billIndex)
				.append(" and (deleted is null or deleted < ").append(isShowingExpiredBill ? DBConsts.deleted : DBConsts.expired)	//dumpted also should show.
				.append(") AND OUTPUT.PRODUCTID = PRODUCT.ID and output.time = '")
				.append(openTime).append(isShowingExpiredBill ? "' and output.category = " + BarFrame.instance.curBillID : "'");	//new added after dump should not display.
			
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();

			int tColCount = tblBillPanel.getColumnCount();
			Object[][] tValues = new Object[tmpPos][tColCount];
			rs.beforeFirst();
			tmpPos = 0;
			while (rs.next()) {
				Dish dish = new Dish();
				dish.setCATEGORY(rs.getString("PRODUCT.CATEGORY"));
				dish.setDiscount(rs.getInt("OUTPUT.discount"));//
				dish.setDspIndex(rs.getInt("PRODUCT.INDEX"));
				dish.setGst(rs.getInt("PRODUCT.FOLDERID"));
				dish.setId(rs.getInt("PRODUCT.ID"));
				dish.setLanguage(0, rs.getString("PRODUCT.CODE"));
				dish.setLanguage(1, rs.getString("PRODUCT.MNEMONIC"));
				dish.setLanguage(2, rs.getString("PRODUCT.SUBJECT"));
				dish.setModification(rs.getString("OUTPUT.CONTENT"));//
				dish.setNum(rs.getInt("OUTPUT.AMOUNT"));//
				dish.setOutputID(rs.getInt("OUTPUT.ID"));//
				dish.setPrice(rs.getInt("PRODUCT.PRICE"));
				dish.setPrinter(rs.getString("PRODUCT.BRAND"));
				dish.setPrompMenu(rs.getString("PRODUCT.UNIT"));
				dish.setPrompMofify(rs.getString("PRODUCT.PRODUCAREA"));
				dish.setPrompPrice(rs.getString("PRODUCT.CONTENT"));
				dish.setQst(rs.getInt("PRODUCT.STORE"));
				dish.setSize(rs.getInt("PRODUCT.COST"));
				dish.setBillIndex(billIndex);
				dish.setOpenTime(rs.getString("OUTPUT.TIME"));	//output time is table's open time. no need to remember output created time.
				dish.setBillID(rs.getInt("OUTPUT.Category"));
				dish.setTotalPrice(rs.getInt("OUTPUT.TOLTALPRICE"));
				orderedDishAry.add(dish);

				tValues[tmpPos][0] = dish.getDisplayableNum(dish.getNum());
				
				tValues[tmpPos][1] = dish.getLanguage(LoginDlg.USERLANG);

				String[] langs = dish.getModification().split(BarDlgConst.semicolon);
				String lang = langs.length > LoginDlg.USERLANG ? langs[LoginDlg.USERLANG] : langs[0];
				if(lang.length() == 0 || "null".equalsIgnoreCase(lang))
					lang = langs[0].length() == 0 || "null".equalsIgnoreCase(lang) ? "" : langs[0];
				
				tValues[tmpPos][2] = lang;
				if(dish.getDiscount() > 0) {
					tValues[tmpPos][2] = lang + "  -" + BarOption.getMoneySign() + BarUtil.formatMoney(dish.getDiscount() / 100.0);
				}
				
				tValues[tmpPos][3] =  BarOption.getMoneySign() + dish.getTotalPrice() / 100f;
				tmpPos++;
			}

			tblBillPanel.setDataVector(tValues, header);
			// do not set the default selected value, if it's used in billListDlg.
			if (salesPanel != null)
				tblBillPanel.setSelectedRow(tmpPos - 1);
			//rs.close();
			
			//update the discount and service fee, and tip info, and (don't forget the billID).
			//if has output, then get the billID from any output, 
			if(orderedDishAry.size() > 0 && orderedDishAry.get(0).getBillID() > 0) {
				sql = new StringBuilder("select * from Bill where opentime = '").append(openTime)
						.append("' and billIndex = '").append(billIndex).append("'")
						.append(" and tableID = '").append(tableName).append("'")
						.append(" and (status is null or status ").append(isShowingExpiredBill ? "<=" : "<").append(DBConsts.expired).append(")");
				  
				rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
				rs.beforeFirst();
				if(rs.next()) {
					billID = rs.getInt("id");	//@NOTE: do not use orderedDishAry.get(0).getBillID() to get billID, because when combine all we don't modify bill id in output and dish (for undo use)
				    discount = rs.getInt("discount");
				    serviceFee = rs.getInt("serviceFee");
				    tip = rs.getInt("tip");
				    cashback = rs.getInt("cashback");
				    status = rs.getInt("status");
				    comment = rs.getString("comment");
				    setBackground(status >= DBConsts.completed || status < 0 ? Color.gray : null);
				}
			}
			//if has no output, then search related bill from db. ---could be an non-first but empty bill, 
			//so must consider the bill ID if it's not empty string.
			else if(tableName.length() > 0 && openTime.length() > 0) {
				sql = new StringBuilder("Select id from bill where tableID = '").append(tableName)
						.append("' and opentime = '").append(openTime).append("'");
				if(BarFrame.instance.valCurBillIdx.getText().length() > 0) {
					sql.append(" and billIndex = '").append(BarFrame.instance.valCurBillIdx.getText()).append("'");
				}
                ResultSet resultSet = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
                resultSet.beforeFirst();
                resultSet.next();
                billID = resultSet.getInt("id");
			}
			rs.close();
		} catch (Exception e) {
			L.e("BillPanel", " exception when initContent()" + sql, e);
		}

		resetColWidth(scrContent.getWidth());
		
		updateTotleArea();
		//reset the flag whichi is only used for showing expired bills.
		BarFrame.instance.isShowingAnExpiredBill = false;
	}

    private void resetProperties(){
        orderedDishAry.clear();
        discount = 0;
        tip = 0;
        serviceFee = 0;
        received = 0;
        cashback = 0;
        billID = 0;
        comment = "";
        status = DBConsts.original;
        setBackground(null);
        
        totalGst = 0;
    	totalQst = 0;
    	subTotal = 0;
        
    }
    
    void resetColWidth(int tableWidth) {
        PIMTableColumn tmpCol1 = tblBillPanel.getColumnModel().getColumn(0);
        tmpCol1.setWidth(60);
        tmpCol1.setPreferredWidth(60);
        PIMTableColumn tmpCol4 = tblBillPanel.getColumnModel().getColumn(3);
        tmpCol4.setWidth(60);
        tmpCol4.setPreferredWidth(60);
        //at first, teh tableWidth is 0, then after, the tableWidth will be 260. 
        PIMTableColumn tmpCol2 = tblBillPanel.getColumnModel().getColumn(1);
        int width = (tableWidth - tmpCol1.getWidth() - tmpCol4.getWidth())/2 - 3;
        tmpCol2.setWidth(width);
        tmpCol2.setPreferredWidth(width);
        PIMTableColumn tmpCol3 = tblBillPanel.getColumnModel().getColumn(2);
        tmpCol3.setWidth(width - (scrContent.getVerticalScrollBar().isVisible() ? scrContent.getVerticalScrollBar().getWidth() : 0));
        tmpCol3.setPreferredWidth(width - (scrContent.getVerticalScrollBar().isVisible() ? scrContent.getVerticalScrollBar().getWidth() : 0));
        
        tblBillPanel.validate();
        tblBillPanel.revalidate();
        tblBillPanel.invalidate();
    }

    void removeFromSelection(int selectedRow) {
    	
		int tValidRowCount = getUsedRowCount(); // get the used RowCount
    	if(selectedRow < 0 || selectedRow > tValidRowCount - 1) {
    		JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
    		ErrorUtil.write("Unexpected row number when calling removeAtSelection : " + selectedRow);
    		return;
    	}
    	
    	//update db first
    	Dish dish = orderedDishAry.get(selectedRow);
    	if(dish.getOutputID() > -1) {
    		dish.changeOutputStatus(comment.contains(PrintService.OLD_SUBTOTAL) ? DBConsts.expired : DBConsts.deleted);
    	}
    	//update array second.
		orderedDishAry.remove(selectedRow);
		//update the table view
		int tColCount = tblBillPanel.getColumnCount();
		Object[][] tValues = new Object[tValidRowCount - 1][tColCount];
		for (int r = 0; r < tValidRowCount; r++) {
			if(r == selectedRow) {
				continue;
			}else {
				int rowNum = r > selectedRow ? r : r + 1;
			    for (int c = 0; c < tColCount; c++)
			        tValues[rowNum-1][c] = tblBillPanel.getValueAt(r, c);
			}
		}
		tblBillPanel.setDataVector(tValues, header);
		resetColWidth(scrContent.getWidth());
		tblBillPanel.setSelectedRow(tValues.length - 1); //@Note this will trigger a value change event, to set the curDish.
		updateTotleArea();
	}
    
    //return true means can move on, return false means user don't want to move on.
	public boolean checkStatus() {
		if(status >= DBConsts.billPrinted || status < 0) {//check if the bill is .
			if (JOptionPane.showConfirmDialog(this, BarFrame.consts.ConvertClosedBillBack(), BarFrame.consts.Operator(),
		            JOptionPane.YES_NO_OPTION) != 0) {// are you sure to convert the voided bill back？
		        return false;
			}else {
				reGenerate(null);
			}
		}
		return true;
	}
	
	//caller can specify the billIdx, if the billIdx is not specified, then use the billIdx on Frame.
	public void reGenerate(String billIdx) {
		if(billIdx == null || billIdx.length() == 0) {
			billIdx = BarFrame.instance.valCurBillIdx.getText();
		}
		
		try {
			//dump the old bill and create a new bill
			StringBuilder sql = new StringBuilder("update bill set status = ").append(DBConsts.expired)
	 				.append(" where id = ").append(billID);
	 		PIMDBModel.getStatement().executeUpdate(sql.toString());
	 		
	 		//Generate new bill with ref to dumped bill everything else use the data on current billPane
	 		//@NOTE:no need to generate new output. because the output will be choose by table and billIdx, so old output will go to new bill automatically.
	 		//while, if user open the dumped old bill, then the removed item will be disappears and new added item will appear on old bill also.
	 		//this will be a known bug. TDOO:we can make it better by searching output by billID when it's a dumped bill. hope no one will need to check the dumped bills.
	 		//??what do we do when removing an saved item from billPanel?
	 		if(status > 0) {
		 		StringBuilder newComment = new StringBuilder(PrintService.REF_TO).append(billID);
				if(status >= DBConsts.completed) {	//if already paid, then need to know old moneys, so in mev can report how much added or returned.
					newComment.append("F");	
				}
				newComment.append("\n").append(PrintService.OLD_SUBTOTAL).append(BarUtil.formatMoney(subTotal / 100.0));	//this value will be needed anyway.
				if(status >= DBConsts.completed) {	//@Note mess the order of each money.
					newComment.append("\n").append(PrintService.OLD_GST).append(BarUtil.formatMoney(totalGst / 100.0))
						.append("\n").append(PrintService.OLD_QST).append(BarUtil.formatMoney(totalQst / 100.0))
						.append("\n").append(PrintService.OLD_TOTAL).append(valTotlePrice.getText());
				}
				
				//as long as a bill is regenerated, the status of the bill must be at least billprinted.
				//now the problem is shall we remember the old price? if we append the old moneys, then when we calculate the mtTransAvTaxes, sub total will minus 
				//the oldSubtotal, which means the mtTransAVTaxes will eventurally be the differents----this is good for modified invoice, not for modified check.
				//so we currently decide only when it's completed, we append the old moneys. that mean, when we are calculating a mtTransAvTaxes for checks, we
				//should remenber that there's only one old money(subtotal) in end message if it's not completed, but bill printed....we have to keep this one because 
				//when the check modified, we need to know the old subtotal in ref part.
				this.comment = newComment.toString();	//set the comment property, so when creating a new bill base on current one, will copy the comment into the new bill.
			}else {	//according to revenue test case, if it's refunded bill, when regenerate a bill base on it, should have not ref part, (personally don't understand it yet).
				this.comment = "";
			}
	 		
	 		int newBillID = cloneCurrentBillRecord(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
					billIdx,
					BarFrame.instance.valStartTime.getText(),
					Math.round(Float.valueOf(valTotlePrice.getText()) * 100));
	 		
	        //when we reopen a refunded bill, we create a new bill which is a original bill, so we must clean the received money with the refund count.

	 		if(status < 0){	//save the old money numbers, in case the old status is negative(means have returned some money.
	 			StringBuilder sb = new StringBuilder("select * from bill where id = " + newBillID);

 	    		ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sb.toString());
 	            rs.next();
 	        	
 	            int oldTotal = rs.getInt("total");
 	            int oldCashReceived =rs.getInt("cashReceived");
 	            int oldDebitReceived = rs.getInt("debitReceived");
 	            int oldVisaReceived = rs.getInt("visaReceived");
 	            int oldMasterReceived = rs.getInt("masterReceived");
 	        	
 	            //clean cashback and tip first, and clean status at the same time.
 	        	//set status = 0 immediatly when refund is covered by received money, in case another receive can also cover the refund, will be minus twice.
 	            int newCashReceived = oldCashReceived + cashback;	//cashReceived is always related with cashback.
	        	if(newCashReceived + status > 0) {
	        		newCashReceived += status;
	        		status = 0;
	        	}else {
	        		status += newCashReceived;
	        		newCashReceived = 0;
	        	}
 	            
 	        	//debitReceived
 	        	int newDebitReceived = oldDebitReceived;
 	        	if(oldDebitReceived > tip) {
 	        		newDebitReceived -= tip;
 	        	}
 	        	if(newDebitReceived + status > 0) {
    				newDebitReceived += status;
    				status = 0;
    			} else {
    				status += newDebitReceived;
    				newDebitReceived = 0;
    			}
 	        	
 	        	//visaReceived
 	            int newVisaReceived = oldVisaReceived;
 	            if(newVisaReceived > tip) {
 	            	newVisaReceived -= tip;
 	        	}
    			if(newVisaReceived + status > 0) {
    				newVisaReceived += status;
    				status = 0;
    			} else {
    				status += newVisaReceived;
    				newVisaReceived = 0;
    			}
 	            
 	            //masterReceived
 	            int newMasterReceived = oldMasterReceived;
 	            if(newMasterReceived > tip) {
 	            	newMasterReceived -= tip;
 	        	}
    			if(newMasterReceived + status > 0) {
    				newMasterReceived += status;
    				status = 0;
    			} else {
    				status += newMasterReceived;
    				newMasterReceived = 0;
    			}
 	            
 	            sql = new StringBuilder("update bill set tip = 0, cashback = 0, status = 0, cashReceived = ").append(newCashReceived)
 	            		.append(", debitReceived = ").append(newDebitReceived)
 	            		.append(", visaReceived = ").append(newVisaReceived)
 	            		.append(", masterReceived = ").append(newMasterReceived)
 	            		.append(" where id = ").append(newBillID);
 	           PIMDBModel.getStatement().executeUpdate(sql.toString());
	 		}
	 		
   			//tip must be able to set to 0.
	        tip = 0;
	        cashback = 0;
	 		status = DBConsts.original;	//will be used when clicking buttons, set to original, so will not trigger warning dialogs.
	 		billID = newBillID;			//will be used when adding new item into the bill
		}catch(Exception exp) {
			L.e("SalesPane", "Exception happenned when converting bill's status to 0", exp);
		}
	}
	
    private int getUsedRowCount() {
        for (int i = 0, len = tblBillPanel.getRowCount(); i < len; i++)
            if (tblBillPanel.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblBillPanel.getRowCount();
    }

    public int getBillId(){
    	return billID;
    }
    
    void reLayout() {
        int panelHeight = getHeight();

        int tBtnWidht = (getWidth() - CustOpts.HOR_GAP * 10) / 9;
        int tBtnHeight = panelHeight / 10;
        
     // table area-------------
        int poxX = 0;
        int posY = 0;
        int scrContentHeight = getHeight() - BarFrame.consts.SubTotal_HEIGHT;
        if(billButton != null) {
        	billButton.setBounds(poxX, posY, getWidth(), CustOpts.BTN_HEIGHT + 16);
        	posY += billButton.getHeight();
        	scrContentHeight -= billButton.getHeight() - lblSubTotle.getPreferredSize().height;
        }
        scrContent.setBounds(poxX, posY, getWidth(), scrContentHeight);
        
		// sub total-------
		if(billButton == null){
        	btnMore.setBounds(scrContent.getX() + scrContent.getWidth() - BarFrame.consts.SCROLLBAR_WIDTH,
        			scrContent.getY() + scrContent.getHeight() + CustOpts.VER_GAP, 
            		BarFrame.consts.SCROLLBAR_WIDTH, BarFrame.consts.SCROLLBAR_WIDTH);
            btnLess.setBounds(btnMore.getX() - CustOpts.HOR_GAP - BarFrame.consts.SCROLLBAR_WIDTH, btnMore.getY(), 
            		BarFrame.consts.SCROLLBAR_WIDTH, BarFrame.consts.SCROLLBAR_WIDTH);
    		lblSubTotle.setBounds(btnLess.getX() - 120, 
    				scrContent.getY() + scrContent.getHeight() + CustOpts.VER_GAP,
    				120, lblSubTotle.getPreferredSize().height);
        }else {
        	lblSubTotle.setBounds(scrContent.getX() + scrContent.getWidth() - 120, 
    				scrContent.getY() + scrContent.getHeight() + CustOpts.VER_GAP,
    				120, lblSubTotle.getHeight());
        }
        lblDiscount.setBounds(scrContent.getX(), scrContent.getY() + scrContent.getHeight() + CustOpts.VER_GAP, 
        		scrContent.getWidth() / 4, lblSubTotle.getHeight());
        lblServiceFee.setBounds(lblDiscount.getX() + lblDiscount.getWidth() + CustOpts.HOR_GAP, lblDiscount.getY(), 
        		scrContent.getWidth() / 4, lblSubTotle.getHeight());

		lblTPS.setBounds(scrContent.getX(), getHeight() - CustOpts.BTN_HEIGHT, scrContent.getWidth() / 4,
				lblTPS.getPreferredSize().height);
		lblTVQ.setBounds(lblTPS.getX() + lblTPS.getWidth() + CustOpts.HOR_GAP, lblTPS.getY(), lblTPS.getWidth(), lblTPS.getHeight());
		lblTotlePrice.setBounds(lblSubTotle.getX(), lblTVQ.getY(), lblTotlePrice.getPreferredSize().width, lblTVQ.getHeight());
		valTotlePrice.setBounds(lblTotlePrice.getX() + lblTotlePrice.getWidth(), lblTotlePrice.getY(),
				120 - lblTotlePrice.getWidth(), lblTotlePrice.getHeight() + CustOpts.VER_GAP);
    }
    
    void initComponent() {
    	removeAll();
        tblBillPanel = new PIMTable();// 显示字段的表格,设置模型
        scrContent = new PIMScrollPane(tblBillPanel);
        lblDiscount = new JLabel();
        lblServiceFee = new JLabel();
        lblSubTotle = new JLabel(BarFrame.consts.Subtotal());
        lblTPS = new JLabel(BarFrame.consts.GST());
        lblTVQ = new JLabel(BarFrame.consts.QST());
        lblTotlePrice = new JLabel(BarFrame.consts.Total() + " : " + BarOption.getMoneySign());
        valTotlePrice = new JLabel();
        btnMore = new ArrowButton("<html><h1 style='text-align: center; padding-bottom: 5px; color:#18F507;'>+</h1></html>");
        btnLess = new ArrowButton("<html><h1 style='text-align: center; padding-bottom: 5px; color:#FB112C;'>-</h1></html>");
        
        Color bg = BarOption.getBK("Bill");
    	if(bg == null) {
    		bg = new Color(216,216,216);
    	}
        setBackground(bg);
        setLayout(null);
        tblBillPanel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblBillPanel.setAutoscrolls(true);
        tblBillPanel.setRowHeight(30);
        tblBillPanel.setCellEditable(false);
        tblBillPanel.setRenderAgent(this);
        tblBillPanel.setHasSorter(false);
        tblBillPanel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        tblBillPanel.setDataVector(new Object[1][header.length], header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblBillPanel.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
        valTotlePrice.setFont(BarOption.lessBigFont);
        //@do_not_work! valTotlePrice.setHorizontalAlignment(SwingConstants.RIGHT);
        //@work! valTotlePrice.setAlignmentX(Component.RIGHT_ALIGNMENT);
      //@do_not_work!valTotlePrice.setBackground(Color.RED);
      //@do_not_work!valTotlePrice.setOpaque(false);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        scrContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);

        // Margin-----------------
        btnMore.setMargin(new Insets(0,0,0,0));
        btnLess.setMargin(btnMore.getInsets());
        
        // border----------
        tblBillPanel.setBorder(null);
        tblBillPanel.setShowGrid(false);
        // forcus-------------
        tblBillPanel.setFocusable(false);
        btnMore.setFocusable(false);
        btnLess.setFocusable(false);
        
        // disables
        btnMore.setEnabled(false);
        btnLess.setEnabled(false);
        
        // built
        if(billButton != null) {
        	add(billButton);
            billButton.addActionListener(this);
        }else {
            add(btnMore);
            add(btnLess);
            add(lblDiscount);
            add(lblServiceFee);
            add(lblSubTotle);
            add(lblTPS);
            add(lblTVQ);
        }
        add(lblTotlePrice);
        add(valTotlePrice);
        add(scrContent);

        addComponentListener(this);
        btnMore.addActionListener(this);
        btnLess.addActionListener(this);
        tblBillPanel.addMouseMotionListener(this);
        tblBillPanel.addMouseListener(this);
        tblBillPanel.getSelectionModel().addListSelectionListener(this);
        scrContent.getViewport().addMouseListener(this);
		reLayout();
    }
    
    public PIMTable tblBillPanel;
    private PIMScrollPane scrContent;

    public JLabel lblSubTotle;
    public JLabel lblTPS;
    public JLabel lblTVQ;
    public JLabel lblTotlePrice;
    public JLabel lblDiscount;
    public JLabel lblServiceFee;
    public JLabel valTotlePrice;
    
    private ArrowButton btnMore;
    private ArrowButton btnLess;
    
    String[] header = new String[] {BarFrame.consts.Count(), BarFrame.consts.ProdName(), "", BarFrame.consts.Price()};

}
