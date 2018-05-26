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

import org.cas.client.platform.bar.beans.ArrayButton;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.WifiPrintService;
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
	JToggleButton billButton;
    private boolean isDragging;
    public ArrayList<Dish> selectdDishAry = new ArrayList<Dish>();
    int discount;
    int tip;
    int serviceFee;
    int received;
    int cashback;
    String comment = "";
    int status = 0;
    
	public BillPanel(SalesPanel salesPanel) {
		this.salesPanel = salesPanel;
		initComponent();
	}

	public BillPanel(BillListPanel billListPanel, JToggleButton billButton) {
		this.billListPanel = billListPanel;
		this.billButton = billButton;
	}
	
	public int printBill(String tableID, String billIndex, String opentime) {
		//check if it's alreay printed
		//todo: send to printer
		
		//update category field with billID
		Statement stm = PIMDBModel.getStatement();
		String createtime = BarOption.df.format(new Date());
		StringBuilder sql = new StringBuilder(
	            "INSERT INTO bill(createtime, tableID, BillIndex, total, discount, tip, cashback, EMPLOYEEID, Comment, opentime) VALUES ('")
				.append(createtime).append("', '")
	            .append(tableID).append("', '")	//table
	            .append(billIndex).append("', ")			//bill
	            .append((int)(Float.valueOf(valTotlePrice.getText()) * 100)).append(", ")	//total
	            .append(discount).append(", ")
	            .append((tip)).append(", ")
	            .append(cashback).append(", ")	//discount
	            .append(LoginDlg.USERID).append(", '")		//emoployid
	            .append(comment).append("', '")
	            .append(opentime).append("')");				//content
		try {
		   	stm.executeUpdate(sql.toString());
		   	sql = new StringBuilder("Select id from bill where createtime = '").append(createtime).append("'");
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.beforeFirst();
            rs.next();
            int newBillID = rs.getInt("id");
                
		   	sql = new StringBuilder("update output set category = '").append(newBillID).append("' where id = ");
			for (Dish dish : selectdDishAry) {
				if(dish.getOutputID() > 0) {
					try {
						stm.executeUpdate(sql.append(dish.getOutputID()).toString());
					}catch(Exception exp) {
						ErrorUtil.write(exp);
					}
				}
			}
			return newBillID;
	   }catch(Exception e) {
			ErrorUtil.write(e);
	   }
		return -1;
	}
	
	void sendNewDishesToKitchen() {
		List<Dish> newDishes = getNewDishes();
		
		//if all record are new, means it's adding a new bill.otherwise, it's adding output to exixting bill.
		if(newDishes.size() == selectdDishAry.size()) {
		    BarFrame.instance.valCurBill.setText(String.valueOf(BillListPanel.getANewBillNumber()));
		}
		
		//send to printer
		//prepare the printing String and do printing
		if(WifiPrintService.SUCCESS != 
				WifiPrintService.exePrintCommand(newDishes, BarFrame.instance.menuPanel.printers, BarFrame.instance.valCurTable.getText())) {
			BarFrame.setStatusMes(BarFrame.consts.PrinterError());
			JOptionPane.showMessageDialog(this, BarFrame.consts.PrinterError());
		}
		
		//save to db output
		try {
		    for (Dish dish : newDishes) {
//                    	if(dish.getOutputID() > -1)	//if it's already saved into db, don't ignore.
//                    		continue;
		    	
		    	String curBillId = BarFrame.instance.valCurBill.getText();
		    	if("0".equals(curBillId))
		    		curBillId = "1";
		    	Dish.createOutput(dish, curBillId);	//at this moment, the num shoul have not been soplitted.

		        //in case some store need to stay in the interface after clicking the send button. 
//	                    sql = new StringBuilder("Select id from output where SUBJECT = '")
//	                        .append(BarFrame.btnCurTable.getText()).append("' and CONTACTID = ")
//	                        .append(BarFrame.instance.lblCurBill.getText()).append(" and PRODUCTID = ")
//	                        .append(dish.getId()).append(" and AMOUNT = ")
//	                        .append(dish.getNum()).append(" and TOLTALPRICE = ")
//	                        .append((dish.getPrice() - dish.getDiscount()) * dish.getNum()).append(" and DISCOUNT = ")
//	                        .append(dish.getDiscount() * dish.getNum()).append(" and EMPLOYEEID = ")
//	                        .append(LoginDlg.USERID).append(" and TIME = '")
//	                        .append(time).append("'");
//	                    ResultSet rs = smt.executeQuery(sql.toString());
//	                    rs.beforeFirst();
//                        while (rs.next()) {
//                        	dish.setOutputID(rs.getInt("id"));
//                        }
//	                    
//	                    rs.close();
		    }
		}catch(Exception exp) {
			JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
		    exp.printStackTrace();
		}
	}
	
	List<Dish> getNewDishes() {
		List<Dish> newDishes = new ArrayList<Dish>();
		for (Dish dish : selectdDishAry) {
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
		if(o instanceof ArrayButton) {
	        if(o == btnMore) {
	        	int selectedRow =  tblSelectedDish.getSelectedRow();
				if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {	//already saved
					BarFrame.setStatusMes(BarFrame.consts.SendItemCanNotModify());
					addContentToList(selectdDishAry.get(selectedRow));
				}else {
					int tQTY = selectdDishAry.get(selectedRow).getNum() + 1;
					int row = tblSelectedDish.getSelectedRow();
					selectdDishAry.get(row).setNum(tQTY);
					tblSelectedDish.setValueAt("x" + tQTY % BarOption.MaxQTY, row, 0);
					tblSelectedDish.setValueAt((selectdDishAry.get(selectedRow).getPrice() - selectdDishAry.get(selectedRow).getDiscount()) * tQTY/100f, row, 3);
				}
				updateTotleArea();
				tblSelectedDish.setSelectedRow(selectedRow);
	        } else if (o == btnLess) {
	    		int selectedRow =  tblSelectedDish.getSelectedRow();
				if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {
					if (JOptionPane.showConfirmDialog(this, BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle,
		                    JOptionPane.YES_NO_OPTION) != 0) {
						return;
					}
					//TODO: send a message to kitchen.
					
				}else {
					if(selectdDishAry.get(selectedRow).getNum() == 1) {
						if (JOptionPane.showConfirmDialog(this, BarFrame.consts.COMFIRMDELETEACTION2(), DlgConst.DlgTitle,
			                    JOptionPane.YES_NO_OPTION) != 0) {
							tblSelectedDish.setSelectedRow(-1);
							return;
						}
						removeAtSelection(selectedRow);
					} else {
						int tQTY = selectdDishAry.get(selectedRow).getNum() - 1;
						int row = tblSelectedDish.getSelectedRow();
						selectdDishAry.get(row).setNum(tQTY);
						tblSelectedDish.setValueAt("x" + tQTY, row, 0);		
						tblSelectedDish.setValueAt((selectdDishAry.get(selectedRow).getPrice() - selectdDishAry.get(selectedRow).getDiscount()) * tQTY/100f, row, 3);
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
	
    void resetTableArea() {
    	selectdDishAry.clear();
        Object[][] tValues = new Object[0][tblSelectedDish.getColumnCount()];
        tblSelectedDish.setDataVector(tValues, header);
        resetColWidth(scrContent.getWidth());
        updateTotleArea();
    }
    
    //table selection listener---------------------
	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow =  tblSelectedDish.getSelectedRow();
		btnMore.setEnabled(selectedRow >= 0 && selectedRow <= selectdDishAry.size());
		btnLess.setEnabled(selectedRow >= 0 && selectedRow <= selectdDishAry.size());
		if(!btnMore.isEnabled()) {	//some time the selectedRow can be -1.
			BillListPanel.curDish = null;
			return;
		}

		Dish selectedDish = selectdDishAry.get(selectedRow);
		if(salesPanel != null) {
			BillListPanel.curDish = selectedDish;
			if( BarFrame.numberPanelDlg.isVisible()) {	//if qty button seleted.
				Object obj = tblSelectedDish.getValueAt(selectedRow,0);
				//update the qty in qtyDlg.
				if(obj != null)
					BarFrame.numberPanelDlg.setContents(obj.toString());
			}
			if( salesPanel.btnLine_1_7.isSelected()) {
				Object obj = tblSelectedDish.getValueAt(selectedRow,2);
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
		if(row < selectdDishAry.size()) {
			Dish dish = selectdDishAry.get(row);
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
		if(isDragging == true) {
			isDragging = false;
			ListSelectionModel selectionModel = ((PIMTable)e.getSource()).getSelectionModel();
			int selectedRow =  selectionModel.getMinSelectionIndex();
			if(selectedRow < 0 || selectedRow >= selectdDishAry.size()) 
				return;
			
			if(salesPanel != null && !BarFrame.numberPanelDlg.isVisible()) {	//if qty button not seleted.
				if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {
					if (JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle,
		                    JOptionPane.YES_NO_OPTION) != 0) {// 确定删除吗？
						tblSelectedDish.setSelectedRow(-1);
						return;
					}
				}
				removeAtSelection(selectedRow);
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
		isDragging = true;
	}
	@Override
	public void mouseMoved(MouseEvent e) {}
	
    // 将对话盒区域的内容加入到列表
    void addContentToList(Dish dish) {
        int tRowCount = tblSelectedDish.getRowCount(); // add content to the table.
        int tColCount = tblSelectedDish.getColumnCount();
        int tValidRowCount = getUsedRowCount(); // get the used RowCount
        if (tRowCount == tValidRowCount) { // no line is empty, add a new Line.
            Object[][] tValues = new Object[tRowCount + 1][tColCount];
            for (int r = 0; r < tRowCount; r++)
                for (int c = 0; c < tColCount; c++)
                    tValues[r][c] = tblSelectedDish.getValueAt(r, c);
            tblSelectedDish.setDataVector(tValues, header);
            resetColWidth(scrContent.getWidth());
        }else {
        	tRowCount--;
        }
        
        Dish newDish = dish.clone();		//@NOTE: incase the cloned dish contains outpurID properties.
        newDish.setOutputID(-1);
        newDish.setNum(1);
        newDish.setOpenTime(BarFrame.instance.valStartTime.getText());
        selectdDishAry.add(newDish);				//valueChanged process. not being cleared immediately-----while now dosn't matter
        BillListPanel.curDish = newDish;

        //update the interface.
        tblSelectedDish.setValueAt("X1", tValidRowCount, 0); // set the count.
        tblSelectedDish.setValueAt(dish.getLanguage(CustOpts.custOps.getUserLang()), tValidRowCount, 1);// set the Name.
        tblSelectedDish.setValueAt(dish.getSize() > 1 ? dish.getSize() : "", tValidRowCount, 2); // set the count.
        tblSelectedDish.setValueAt(dish.getPrice()/100f, tValidRowCount, 3); // set the price.
        
        updateTotleArea();								//because value change will not be used to remove the record.
        SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
		        tblSelectedDish.setSelectedRow(selectdDishAry.size() - 1);
			}
		});
    }

    public void updateTotleArea() {
    	Object g = CustOpts.custOps.getValue(BarFrame.consts.GST());
    	Object q = CustOpts.custOps.getValue(BarFrame.consts.QST());
    	float gstRate = g == null ? 5 : Float.valueOf((String)g);
    	float qstRate = q == null ? 9.975f : Float.valueOf((String)q);
    	float totalGst = 0;
    	float totalQst = 0;
    	float itemDisc = 0;
    	int subTotal = 0;
    	
    	
    	for(Dish dish: selectdDishAry) {
    		//get out the num.
    		int num = dish.getNum();
    		int pK = num /(BarOption.MaxQTY * 100);
    		if(num > BarOption.MaxQTY * 100) {
    			num = num %(BarOption.MaxQTY * 100);
    		}
    		int pS = (int)num /BarOption.MaxQTY;
    		if(num > BarOption.MaxQTY) {
    			num = num % BarOption.MaxQTY;
    		}
    		
    		int price = dish.getPrice();
    		int gst = (int) (price * (dish.getGst() * gstRate / 100f));
    		int qst = (int) (price * (dish.getQst() * qstRate / 100f));
    		
    	
    		price *= num;
    		gst *= num;
    		qst *= num;
    		if(BarOption.isDisCountBeforeTax()) {
    			gst -= dish.getDiscount() * (dish.getGst() * gstRate / 100f);
    			qst -= dish.getDiscount() * (dish.getQst() * gstRate / 100f);
    		}
    		
    		if(pS > 0) {
    			price /= pS;
    			gst /= pS;
    			qst /= pS;
    		}
    		if(pK > 0) {
    			price /= pK;
    			gst /= pK;
    			qst /= pK;
    		}
    		
    		subTotal += price;
    		subTotal -= dish.getDiscount();
    		totalGst += gst;
    		totalQst += qst;
    	}
    	subTotal -= discount;
    	if(BarOption.isDisCountBeforeTax()) {
    		//@TODO: I am not sure if it's correct to do like this, we don't know what tax rate is good for the bill discount.
    	}

        lblDiscount.setText(discount > 0 ? BarFrame.consts.Discount() + " : $" + String.valueOf(((int)discount)/100f) : "");
        lblServiceFee.setText(serviceFee > 0 ? BarFrame.consts.ServiceFee() + " : $" + String.valueOf(((int)serviceFee)/100f) : "");
    	lblSubTotle.setText(BarFrame.consts.Subtotal() + " : $" + String.valueOf(subTotal/100f));
        lblGSQ.setText(BarFrame.consts.QST() + " : $" + String.valueOf(((int)totalGst)/100f));
        lblQSQ.setText(BarFrame.consts.GST() + " : $" + String.valueOf(((int)totalQst)/100f));
        valTotlePrice.setText(String.valueOf(((int) (subTotal + totalGst + totalQst))/100f));
    }
    
    void initContent() {
    	selectdDishAry.clear();
    	//get outputs of current table and bill id.
		try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
			String billIndex = billButton == null ? BarFrame.instance.valCurBill.getText() : billButton.getText();
			String sql = "select * from OUTPUT, PRODUCT where OUTPUT.SUBJECT = '" + BarFrame.instance.valCurTable.getText()
					+ "' and CONTACTID = " + billIndex + " and deleted = false AND OUTPUT.PRODUCTID = PRODUCT.ID and output.time = '"
					+ BarFrame.instance.valStartTime.getText() + "'";
			ResultSet rs = smt.executeQuery(sql);
			rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();

			int tColCount = tblSelectedDish.getColumnCount();
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
				selectdDishAry.add(dish);

				tValues[tmpPos][1] = dish.getLanguage(LoginDlg.USERLANG);
				
				int num = dish.getNum();
				//first pick out the number on 100,0000 and 10000 position
	    		int pK = num /(BarOption.MaxQTY * 100);
	    		if(num > BarOption.MaxQTY * 100) {
	    			num = num %(BarOption.MaxQTY * 100);
	    		}
	    		int pS = (int)num /BarOption.MaxQTY;
	    		if(num > BarOption.MaxQTY) {
	    			num = num % BarOption.MaxQTY;
	    		}
				StringBuilder strNum = new StringBuilder("X");
				strNum.append(num);
				if(pS > 0)
					strNum.append("/").append(pS);
				if(pK > 0)
					strNum.append("/").append(pK);
				tValues[tmpPos][0] = strNum.toString();
				
				tValues[tmpPos][3] = "$" + rs.getInt("TOLTALPRICE") / 100f;
				tmpPos++;
			}

			tblSelectedDish.setDataVector(tValues, header);
			// do not set the default selected value, if it's used in billListDlg.
			if (salesPanel != null)
				tblSelectedDish.setSelectedRow(tmpPos - 1);
			rs.close();
		} catch (Exception e) {
			ErrorUtil.write(e);
		}

		resetColWidth(scrContent.getWidth());
		updateTotleArea();
	}
//    
//    void initContent(String category) {
//    	selectdDishAry.clear();
//    	//get outputs of current table and bill id.
//		try {
//			Statement smt = PIMDBModel.getReadOnlyStatement();
//			String sql = "select * from OUTPUT, PRODUCT where OUTPUT.category = '" + category
//					+ "' AND OUTPUT.PRODUCTID = PRODUCT.ID";
//			ResultSet rs = smt.executeQuery(sql);
//			rs.afterLast();
//			rs.relative(-1);
//			int tmpPos = rs.getRow();
//
//			int tColCount = tblSelectedDish.getColumnCount();
//			Object[][] tValues = new Object[tmpPos][tColCount];
//			rs.beforeFirst();
//			tmpPos = 0;
//			while (rs.next()) {
//				Dish dish = new Dish();
//				dish.setCATEGORY(rs.getString("PRODUCT.CATEGORY"));
//				dish.setDiscount(rs.getInt("OUTPUT.discount"));//
//				dish.setDspIndex(rs.getInt("PRODUCT.INDEX"));
//				dish.setGst(rs.getInt("PRODUCT.FOLDERID"));
//				dish.setId(rs.getInt("PRODUCT.ID"));
//				dish.setLanguage(0, rs.getString("PRODUCT.CODE"));
//				dish.setLanguage(1, rs.getString("PRODUCT.MNEMONIC"));
//				dish.setLanguage(2, rs.getString("PRODUCT.SUBJECT"));
//				dish.setModification(rs.getString("OUTPUT.CONTENT"));//
//				dish.setNum(rs.getInt("OUTPUT.AMOUNT"));//
//				dish.setOutputID(rs.getInt("OUTPUT.ID"));//
//				dish.setPrice(rs.getInt("PRODUCT.PRICE"));
//				dish.setPrinter(rs.getString("PRODUCT.BRAND"));
//				dish.setPrompMenu(rs.getString("PRODUCT.UNIT"));
//				dish.setPrompMofify(rs.getString("PRODUCT.PRODUCAREA"));
//				dish.setPrompPrice(rs.getString("PRODUCT.CONTENT"));
//				dish.setQst(rs.getInt("PRODUCT.STORE"));
//				dish.setSize(rs.getInt("PRODUCT.COST"));
//				dish.setBillIndex(String.valueOf(rs.getInt("OUTPUT.ContactID")));
//				selectdDishAry.add(dish);
//
//				tValues[tmpPos][1] = dish.getLanguage(LoginDlg.USERLANG);
//				
//				int num = dish.getNum();
//				//first pick out the number on 100,0000 and 10000 position
//	    		int pK = num /(BarOption.MaxQTY * 100);
//	    		if(num > BarOption.MaxQTY * 100) {
//	    			num = num %(BarOption.MaxQTY * 100);
//	    		}
//	    		int pS = (int)num /BarOption.MaxQTY;
//	    		if(num > BarOption.MaxQTY) {
//	    			num = num % BarOption.MaxQTY;
//	    		}
//				StringBuilder strNum = new StringBuilder("X");
//				strNum.append(num);
//				if(pS > 0)
//					strNum.append("/").append(pS);
//				if(pK > 0)
//					strNum.append("/").append(pK);
//				tValues[tmpPos][0] = strNum.toString();
//				
//				tValues[tmpPos][3] = "$" + rs.getInt("TOLTALPRICE") / 100f;
//				tmpPos++;
//			}
//
//			tblSelectedDish.setDataVector(tValues, header);
//			StringBuilder sb = new StringBuilder(selectdDishAry.get(0).getCATEGORY()).append(" ")
//					.append(selectdDishAry.get(0).getBillIndex()).append(" ").append(category);
//			billButton.setText(sb.toString());
//			rs.close();
//		} catch (Exception e) {
//			ErrorUtil.write(e);
//		}
//
//		resetColWidth(scrContent.getWidth());
//		updateTotleArea();
//	}
    
    void resetColWidth(int tableWidth) {
        PIMTableColumn tmpCol1 = tblSelectedDish.getColumnModel().getColumn(0);
        tmpCol1.setWidth(40);
        tmpCol1.setPreferredWidth(40);
        PIMTableColumn tmpCol3 = tblSelectedDish.getColumnModel().getColumn(2);
        tmpCol3.setWidth(40);
        tmpCol3.setPreferredWidth(40);
        PIMTableColumn tmpCol4 = tblSelectedDish.getColumnModel().getColumn(3);
        tmpCol4.setWidth(40);
        tmpCol4.setPreferredWidth(40);
        //at first, teh tableWidth is 0, then after, the tableWidth will be 260. 
        PIMTableColumn tmpCol2 = tblSelectedDish.getColumnModel().getColumn(1);
        int width = tableWidth - tmpCol1.getWidth() - tmpCol3.getWidth() - tmpCol4.getWidth() - 3;
        width -= scrContent.getVerticalScrollBar().isVisible() ? scrContent.getVerticalScrollBar().getWidth() : 0;
        tmpCol2.setWidth(width);
        tmpCol2.setPreferredWidth(width);
        
        tblSelectedDish.validate();
        tblSelectedDish.revalidate();
        tblSelectedDish.invalidate();
    }

    void removeAtSelection(int selectedRow) {
		int tValidRowCount = getUsedRowCount(); // get the used RowCount
    	if(selectedRow < 0 || selectedRow > tValidRowCount - 1) {
    		JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
    		ErrorUtil.write("Unexpected row number when calling removeAtSelection : " + selectedRow);
    		return;
    	}
    	//update db first
    	Dish dish = selectdDishAry.get(selectedRow);
    	if(dish.getOutputID() > -1) {
    		Dish.delete(dish);
    	}
    	//update array second.
		selectdDishAry.remove(selectedRow);
		//update the table view
		int tColCount = tblSelectedDish.getColumnCount();
		Object[][] tValues = new Object[tValidRowCount - 1][tColCount];
		for (int r = 0; r < tValidRowCount; r++) {
			if(r == selectedRow) {
				continue;
			}else {
				int rowNum = r > selectedRow ? r : r + 1;
			    for (int c = 0; c < tColCount; c++)
			        tValues[rowNum-1][c] = tblSelectedDish.getValueAt(r, c);
			}
		}
		tblSelectedDish.setDataVector(tValues, header);
		resetColWidth(scrContent.getWidth());
		tblSelectedDish.setSelectedRow(tValues.length - 1); //@Note this will trigger a value change event, to set the curDish.
		updateTotleArea();
	}
    
    private int getUsedRowCount() {
        for (int i = 0, len = tblSelectedDish.getRowCount(); i < len; i++)
            if (tblSelectedDish.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblSelectedDish.getRowCount();
    }

    public int getBillId(){
    	if(selectdDishAry.size() > 0) {
    		return selectdDishAry.get(0).getBillID();
    	}
    	return 0;
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
    		lblSubTotle.setBounds(btnLess.getX() - 100, 
    				scrContent.getY() + scrContent.getHeight() + CustOpts.VER_GAP,
    				100, lblSubTotle.getPreferredSize().height);
        }else {
        	lblSubTotle.setBounds(scrContent.getX() + scrContent.getWidth() - 100, 
    				scrContent.getY() + scrContent.getHeight() + CustOpts.VER_GAP,
    				100, lblSubTotle.getHeight());
        }
        lblDiscount.setBounds(scrContent.getX(), scrContent.getY() + scrContent.getHeight() + CustOpts.VER_GAP, 
        		scrContent.getWidth() / 4, lblSubTotle.getHeight());
        lblServiceFee.setBounds(lblDiscount.getX() + lblDiscount.getWidth() + CustOpts.HOR_GAP, lblDiscount.getY(), 
        		scrContent.getWidth() / 4, lblSubTotle.getHeight());

		lblGSQ.setBounds(scrContent.getX(), getHeight() - CustOpts.BTN_HEIGHT, scrContent.getWidth() / 4,
				lblGSQ.getPreferredSize().height);
		lblQSQ.setBounds(lblGSQ.getX() + lblGSQ.getWidth() + CustOpts.HOR_GAP, lblGSQ.getY(), lblGSQ.getWidth(), lblGSQ.getHeight());
		lblTotlePrice.setBounds(lblSubTotle.getX(), lblQSQ.getY(), lblTotlePrice.getPreferredSize().width, lblQSQ.getHeight());
		valTotlePrice.setBounds(lblTotlePrice.getX() + lblTotlePrice.getWidth(), lblTotlePrice.getY(),
				100 - lblTotlePrice.getWidth() - CustOpts.HOR_GAP, lblQSQ.getHeight());
    }
    
    void initComponent() {

        tblSelectedDish = new PIMTable();// 显示字段的表格,设置模型
        scrContent = new PIMScrollPane(tblSelectedDish);
        lblDiscount = new JLabel();
        lblServiceFee = new JLabel();
        lblSubTotle = new JLabel(BarFrame.consts.Subtotal());
        lblGSQ = new JLabel(BarFrame.consts.QST());
        lblQSQ = new JLabel(BarFrame.consts.GST());
        lblTotlePrice = new JLabel(BarFrame.consts.Total() + " : $");
        valTotlePrice = new JLabel();
        btnMore = new ArrayButton("+");
        btnLess = new ArrayButton("-");
        
        setLayout(null);
        tblSelectedDish.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblSelectedDish.setAutoscrolls(true);
        tblSelectedDish.setRowHeight(30);
        tblSelectedDish.setCellEditable(false);
        tblSelectedDish.setRenderAgent(this);
        tblSelectedDish.setHasSorter(false);
        tblSelectedDish.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        tblSelectedDish.setDataVector(new Object[1][header.length], header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblSelectedDish.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        scrContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);

        // Margin-----------------
        btnMore.setMargin(new Insets(0,0,0,0));
        btnLess.setMargin(btnMore.getInsets());
        
        // border----------
        tblSelectedDish.setBorder(null);
        tblSelectedDish.setShowGrid(false);
        // forcus-------------
        tblSelectedDish.setFocusable(false);

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
            add(lblGSQ);
            add(lblQSQ);
        }
        add(lblTotlePrice);
        add(valTotlePrice);
        add(scrContent);

        addComponentListener(this);
        btnMore.addActionListener(this);
        btnLess.addActionListener(this);
        tblSelectedDish.addMouseMotionListener(this);
        tblSelectedDish.addMouseListener(this);
        tblSelectedDish.getSelectionModel().addListSelectionListener(this);
        scrContent.getViewport().addMouseListener(this);
    }
    
    public PIMTable tblSelectedDish;
    private PIMScrollPane scrContent;

    private JLabel lblSubTotle;
    private JLabel lblGSQ;
    private JLabel lblQSQ;
    private JLabel lblTotlePrice;
    JLabel valTotlePrice;
    private JLabel lblDiscount;
    private JLabel lblServiceFee;
    
    private ArrayButton btnMore;
    private ArrayButton btnLess;
    
    String[] header = new String[] {BarFrame.consts.Count(), BarFrame.consts.ProdName(), "", BarFrame.consts.Price()};

}