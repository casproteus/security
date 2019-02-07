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
import java.sql.Statement;
import java.text.DecimalFormat;
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

import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.ArrowButton;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
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
    int billID;
    int discount;
	int subTotal;
    int tip;
    int serviceFee;
    
    int received;
    int cashback;
    String comment = "";
    int status = DBConsts.original;
    
	public BillPanel(SalesPanel salesPanel) {
		this.salesPanel = salesPanel;
		initComponent();
	}

	public BillPanel(BillListPanel billListPanel, JToggleButton billButton) {
		this.billListPanel = billListPanel;
		this.billButton = billButton;
		initComponent();
	}
	
	public void printBill(String tableID, String billIndex, String opentime) {
		
        if(orderedDishAry.size() == 0){
            return;
        }
        
        int targetBillId = orderedDishAry != null && orderedDishAry.size() > 0? 
				orderedDishAry.get(0).getBillID() : 0;
		if(targetBillId > 0) {
			//update the total price of the target bill, 
			//---beause when add dish into the billPane, bill in db will not get updated.
			StringBuilder sql = new StringBuilder("update bill set total = ")
					.append(Math.round(Float.valueOf(valTotlePrice.getText()) * 100))
					.append(", discount = ").append(discount)
					.append(", otherReceived = ").append(serviceFee)
					.append(", status = ").append(DBConsts.billPrinted)//so the invoice can be saved.
					.append(" where id = ").append(targetBillId);
			try {
				PIMDBModel.getStatement().executeUpdate(sql.toString());
			}catch(Exception e) {
				ErrorUtil.write(e);
			}
		}else {
			int newBillID = generateBillRecord(tableID, billIndex, opentime);
			updateOutputRecords(newBillID);
		}
		
        //send to printer @NOTE: run it behind saving to db, so the bill has an id to use.
        PrintService.exePrintBill(this, orderedDishAry);
	}

	public int generateBillRecord(String tableID, String billIndex, String opentime) {
		return generateBillRecord(tableID, billIndex, opentime, 1);
	}
	
	public int generateBillRecord(String tableID, String billIndex, String opentime, int num) {
		//generate a bill in db and update the output with the new bill id
		String createtime = BarOption.df.format(new Date());
		StringBuilder sql = new StringBuilder(
	            "INSERT INTO bill(createtime, tableID, BillIndex, total, discount, tip, otherreceived, cashback, EMPLOYEEID, Comment, opentime) VALUES ('")
				.append(createtime).append("', '")
	            .append(tableID).append("', '")	//table
	            .append(billIndex).append("', ")			//bill
	            .append(Math.round(Float.valueOf(valTotlePrice.getText()) * 100)/num).append(", ")	//total
	            .append(discount).append(", ")
	            .append(tip).append(", ")
	            .append(serviceFee).append(", ")
	            .append(cashback).append(", ")	//discount
	            .append(LoginDlg.USERID).append(", '")		//emoployid
	            .append(comment).append("', '")
	            .append(opentime).append("')");				//content
		try {
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		   	sql = new StringBuilder("Select id from bill where createtime = '").append(createtime).append("' and billIndex = ").append(billIndex);
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.beforeFirst();
            rs.next();
            return rs.getInt("id");
		 }catch(Exception e) {
			ErrorUtil.write(e);
			return -1;
		 }
	}
	
	public void updateOutputRecords(int newBillID) {
		if(newBillID < 0) {
			return;
		}
		
		String sql;
		Statement stm = PIMDBModel.getStatement();
		try {
		   	sql = new StringBuilder("update output set category = '").append(newBillID).append("' where id = ").toString();
			for (Dish dish : orderedDishAry) {
				if(dish.getOutputID() > 0) {
					try {
						stm.executeUpdate(sql + dish.getOutputID());
					}catch(Exception exp) {
						ErrorUtil.write(exp);
					}
				}
			}
	   }catch(Exception e) {
			ErrorUtil.write(e);
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
		String curTable = BarFrame.instance.valCurTable.getText();
		String curCustomerIdx = BarFrame.instance.valCurBill.getText();
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
                    .append(BarFrame.instance.valCurTable.getText()).append("' and CONTACTID = ")
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
					tblBillPanel.setValueAt(BarOption.getMoneySign() + new DecimalFormat("#0.00").format((orderedDishAry.get(selectedRow).getPrice() - orderedDishAry.get(selectedRow).getDiscount()) * tQTY/100f), row, 3);
				}
				updateTotleArea();
				tblBillPanel.setSelectedRow(selectedRow);
	        } else if (o == btnLess) {
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
						tblBillPanel.setValueAt(BarOption.getMoneySign() + new DecimalFormat("#0.00").format((orderedDishAry.get(selectedRow).getPrice() - orderedDishAry.get(selectedRow).getDiscount()) * tQTY/100f), row, 3);
					}
				}
				updateTotleArea();
	        }
        }else if(o == billButton){		//when bill button on top are clicked.
        	if(billListPanel != null && billListPanel.btnSplitItem.isSelected()) {
        		billButton.setSelected(!billButton.isSelected());
        		return;
        	}
        	
    		BarFrame.instance.valCurBill.setText(((JToggleButton)o).getText());
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
		int selectedRow =  tblBillPanel.getSelectedRow();
		btnMore.setEnabled(selectedRow >= 0 && selectedRow <= orderedDishAry.size());
		btnLess.setEnabled(selectedRow >= 0 && selectedRow <= orderedDishAry.size());
		if(!btnMore.isEnabled()) {	//some time the selectedRow can be -1.
			BillListPanel.curDish = null;
			return;
		}

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
				
	 			if(billListPanel.curDish != null && billListPanel.getCurBillPanel() != this) {
					billListPanel.moveDishToBill(this);
					billListPanel.curDish = null;
				}else {
					billButton.setSelected(!billButton.isSelected());
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
	
    // 将对话盒区域的内容加入到列表
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
        	Object tvq = BarOption.getGST();
        	Object tps = BarOption.getQST();
        	float gstRate = tps == null ? 5f : Float.valueOf((String)tps);
        	float qstRate = tvq == null ? 9.975f : Float.valueOf((String)tvq);
        	price = Math.round(price * (100f - gstRate - qstRate)/100);
        }
        newDish.setTotalPrice(price * 1);
        newDish.setOpenTime(BarFrame.instance.valStartTime.getText());
        newDish.setBillIndex(BarFrame.instance.valCurBill.getText());
        newDish.setBillID(billID);
        orderedDishAry.add(newDish);				//valueChanged process. not being cleared immediately-----while now dosn't matter
        BillListPanel.curDish = newDish;
        
        //update the interface.
        tblBillPanel.setValueAt("", tValidRowCount, 0); // set the count.
        tblBillPanel.setValueAt(dish.getLanguage(CustOpts.custOps.getUserLang()), tValidRowCount, 1);// set the Name.
        tblBillPanel.setValueAt(dish.getSize() > 1 ? dish.getSize() : "", tValidRowCount, 2); // set the count.
        tblBillPanel.setValueAt(BarOption.getMoneySign() + new DecimalFormat("#0.00").format(price/100f), tValidRowCount, 3); // set the price.
        
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
		if(dishes.size() == orderedDishAry.size()) {
		    BarFrame.instance.valCurBill.setText(String.valueOf(BillListPanel.getANewBillNumber()));
		}
		sendDishesToKitchen(dishes, false);
		persistDishesToOutput(dishes);
		tblBillPanel.repaint();//to update the color of dishes, it's saved, so it's not red anymore.
	}

    public void updateTotleArea() {
    	Object tvq = BarOption.getGST();
    	Object tps = BarOption.getQST();
    	float gstRate = tps == null ? 5f : Float.valueOf((String)tps);
    	float qstRate = tvq == null ? 9.975f : Float.valueOf((String)tvq);
    	float totalGst = 0;
    	float totalQst = 0;
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
    		int gst = Math.round(totalPrice * (dish.getGst() * gstRate / 100f));	//an item could have a different tax rate.
    		int qst = Math.round(totalPrice * (dish.getQst() * qstRate / 100f));
    		
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

        lblDiscount.setText(discount > 0 ? BarFrame.consts.Discount() + " : -" + BarOption.getMoneySign() + new DecimalFormat("#0.00").format((discount)/100f) : "");
        lblServiceFee.setText(serviceFee > 0 ? BarFrame.consts.ServiceFee() + " : " + BarOption.getMoneySign() + new DecimalFormat("#0.00").format((serviceFee)/100f) : "");
    	lblSubTotle.setText(BarFrame.consts.Subtotal() + " : " + BarOption.getMoneySign() + new DecimalFormat("#0.00").format(subTotal/100f));
        lblTPS.setText(BarFrame.consts.QST() + " : " + BarOption.getMoneySign() + new DecimalFormat("#0.00").format(((int)totalGst)/100f));
        lblTVQ.setText(BarFrame.consts.GST() + " : " + BarOption.getMoneySign() + new DecimalFormat("#0.00").format(((int)totalQst)/100f));
        int total = (int) (subTotal + totalGst + totalQst);
        valTotlePrice.setText(new DecimalFormat("#0.00").format((total)/100f));
    }
    
    void initContent() {
    	resetStatus();
    	//get outputs of current table and bill id.
		try {
			String billIndex = billButton == null ? BarFrame.instance.getCurBillIndex() : billButton.getText();
			//used deleted <= 1, means both uncompleted and normally completed will be displayed, unnormally delted recored will be delted = 100
			StringBuilder sql = new StringBuilder("select * from OUTPUT, PRODUCT where OUTPUT.SUBJECT = '")
					.append(BarFrame.instance.valCurTable.getText())
					.append("' and CONTACTID = ").append(billIndex)
					.append(" and (deleted is null or deleted < ").append(DBConsts.deleted)
					.append(") AND OUTPUT.PRODUCTID = PRODUCT.ID and output.time = '")
					.append(BarFrame.instance.valStartTime.getText()).append("'");
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
					tValues[tmpPos][2] = lang + "  -" + BarOption.getMoneySign() + new DecimalFormat("#0.00").format(dish.getDiscount()/100.0);
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
			//if has output, then get the billID from any output, if has no output, then search related bill from db.
			//@NOTE could be an non-first but empty bill, so must consider the bill ID if it's not empty string.
			if(orderedDishAry.size() > 0 && orderedDishAry.get(0).getBillID() > 0) {
				billID = orderedDishAry.get(0).getBillID();
				sql = new StringBuilder("select * from Bill where id = ").append(billID);
				rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
				rs.beforeFirst();
				if(rs.next()) {
				    discount = rs.getInt("discount");
				    tip = rs.getInt("tip");
				    serviceFee = rs.getInt("otherreceived");
				    status = rs.getInt("status");
				    setBackground(status >= DBConsts.completed ? Color.gray : null);
				}
			}else if(BarFrame.instance.valCurTable.getText().length() > 0 && BarFrame.instance.valStartTime.getText().length() > 0) {
				sql = new StringBuilder("Select id from bill where tableID = '").append(BarFrame.instance.valCurTable.getText())
						.append("' and opentime = '").append(BarFrame.instance.valStartTime.getText()).append("'");
				if(BarFrame.instance.valCurBill.getText().length() > 0) {
					sql.append(" and billIndex = ").append(BarFrame.instance.valCurBill.getText());
				}
                ResultSet resultSet = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
                resultSet.beforeFirst();
                resultSet.next();
                billID = resultSet.getInt("id");
			}
			rs.close();
		} catch (Exception e) {
			ErrorUtil.write(e);
		}

		resetColWidth(scrContent.getWidth());
		
		updateTotleArea();
	}

    private void resetStatus(){
        orderedDishAry.clear();
        discount = 0;
        tip = 0;
        serviceFee = 0;
        received = 0;
        cashback = 0;
        comment = "";
        status = DBConsts.original;
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
    		Dish.deleteRelevantOutput(dish);
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
        lblTPS = new JLabel(BarFrame.consts.QST());
        lblTVQ = new JLabel(BarFrame.consts.GST());
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
