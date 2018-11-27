package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.beans.ArrowButton;
import org.cas.client.platform.bar.beans.FunctionButton;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class BillListPanel extends  JPanel  implements ActionListener, ComponentListener{
	public static Dish curDish;
	int curPageNum;
	
	public BillListPanel() {
		initComponent();
	}

	void initComponent() {
		removeAll();
		billPanels= new ArrayList<BillPanel>();
		onScrBills= new ArrayList<BillPanel>();

	    btnLeft = new ArrowButton("<<");
	    btnRight = new ArrowButton(">>");
		btnAddUser = new FunctionButton(BarFrame.consts.AddUser());
		btnPrintAll = new FunctionButton(BarFrame.consts.PrintAll());

		btnEqualBill = new JToggleButton(BarFrame.consts.EqualBill());
		btnSplitItem = new JToggleButton(BarFrame.consts.SplitItem());
		btnMoveItem = new JToggleButton(BarFrame.consts.MoveItem());
		btnCombineAll = new FunctionButton(BarFrame.consts.CombineAll());
		
		btnCompleteAll = new FunctionButton(BarFrame.consts.CompleteAll());
		btnReturn = new FunctionButton(BarFrame.consts.RETURN());
		
		separator= new JSeparator();

		btnLeft.setMargin(new Insets(0, 0, 0, 0));
		btnRight.setMargin(new Insets(0, 0, 0, 0));
		btnAddUser.setMargin(new Insets(0, 0, 0, 0));
		btnPrintAll.setMargin(btnAddUser.getMargin());
		btnEqualBill.setMargin(btnAddUser.getMargin());
		btnCombineAll.setMargin(btnAddUser.getMargin());
		btnSplitItem.setMargin(btnAddUser.getMargin());
		btnMoveItem.setMargin(btnAddUser.getMargin());
		btnCompleteAll.setMargin(btnAddUser.getMargin());
		btnReturn.setMargin(btnAddUser.getMargin());
		
		setLayout(null);
		
		add(btnLeft);
		add(btnRight);
		add(btnAddUser);
		add(separator);
		add(btnPrintAll);
		add(btnEqualBill);
		add(btnCombineAll);
		add(btnSplitItem);
		add(btnMoveItem);
		add(btnCompleteAll);
		add(btnReturn);
		
		addComponentListener(this);
		btnLeft.addActionListener(this);
		btnRight.addActionListener(this);
		btnAddUser.addActionListener(this);
		btnPrintAll.addActionListener(this);
		btnEqualBill.addActionListener(this);
		btnCombineAll.addActionListener(this);
		btnSplitItem.addActionListener(this);
		btnMoveItem.addActionListener(this);
		btnCompleteAll.addActionListener(this);
		btnReturn.addActionListener(this);
		
		btnLeft.setEnabled(curPageNum > 0);
		reLayout();
	}
	
	void initContent() {
		for(int i = onScrBills.size() - 1; i >= 0; i--) {
			remove(onScrBills.get(i));
		}
		billPanels.clear();
		onScrBills.clear();
		
		// load all the unclosed outputs under this table with ---------------------------
		try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
			ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '" + BarFrame.instance.valCurTable.getText()
					+ "' and deleted = 0 and time = '" + BarFrame.instance.valStartTime.getText() + "' order by contactID");
			rs.beforeFirst();

			while (rs.next()) {
				JToggleButton billButton = new JToggleButton();
				billButton.setText(String.valueOf(rs.getInt("contactID")));
				billButton.setMargin(new Insets(0, 0, 0, 0));
				
				BillPanel billPanel = new BillPanel(this, billButton);
				billPanels.add(billPanel);
			}

			//do it outside the above loop, because there's another qb query inside.
			int col = BarOption.getBillPageCol();
			int row = BarOption.getBillPageRow();
			
			int billNum = getANewBillNumber();
			for(int i = 0; i < row * col; i++) {
				if(row * col * curPageNum + i < billPanels.size()) {
					billPanels.get(row * col * curPageNum + i).initComponent();
					billPanels.get(row * col * curPageNum + i).initContent();
					onScrBills.add(billPanels.get(row * col * curPageNum + i));
					btnRight.setEnabled(true);
				}else {
					BillPanel panel = new BillPanel(this, new JToggleButton(String.valueOf(billNum)));	//have to give a number to construct valid sql.
					panel.initComponent();
					panel.initContent();
					onScrBills.add(panel);
					btnRight.setEnabled(false);
					billNum++;
				}
			}
		} catch (Exception e) {
 			ErrorUtil.write("Unexpected exception when init the tables from db." + e);
 		}
		reLayout();
	}
	
	private void reLayout() {

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 9) / 8;
        int tBtnHeight = panelHeight / 10;


		btnReturn.setBounds(CustOpts.SIZE_EDGE, panelHeight - tBtnHeight - CustOpts.VER_GAP, tBtnWidht, tBtnHeight);
		
		btnAddUser.setBounds(btnReturn.getX() + btnReturn.getWidth() + CustOpts.HOR_GAP,
				btnReturn.getY(), tBtnWidht, tBtnHeight);
		
		btnPrintAll.setBounds(btnAddUser.getX() + btnAddUser.getWidth() + CustOpts.HOR_GAP, 
				panelHeight - tBtnHeight - CustOpts.VER_GAP,
				tBtnWidht, tBtnHeight);
		
		btnEqualBill.setBounds(btnPrintAll.getX() + btnPrintAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				tBtnWidht, tBtnHeight);
		btnCombineAll.setBounds(btnEqualBill.getX() + btnEqualBill.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				tBtnWidht, tBtnHeight);
		btnSplitItem.setBounds(btnCombineAll.getX() + btnCombineAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				tBtnWidht, tBtnHeight);
		btnMoveItem.setBounds(btnSplitItem.getX() + btnSplitItem.getWidth() + CustOpts.HOR_GAP, 
				btnSplitItem.getY(),
				tBtnWidht, tBtnHeight);
		btnCompleteAll.setBounds(btnMoveItem.getX() + btnMoveItem.getWidth() + CustOpts.HOR_GAP, 
				btnMoveItem.getY(),
				tBtnWidht, tBtnHeight);
		
		separator.setBounds(CustOpts.HOR_GAP, 
				btnCompleteAll.getY() - CustOpts.VER_GAP * 2,
				getWidth() - CustOpts.HOR_GAP * 2, tBtnHeight);
		
		btnLeft.setBounds(CustOpts.SIZE_EDGE, 
				separator.getY() - 40 - CustOpts.VER_GAP * 2,
				40, 40);
		btnRight.setBounds(getWidth() - 40 - CustOpts.SIZE_EDGE, 
				btnLeft.getY(),
				40, 40);

		if(onScrBills.size() > 0) { //@NOTE: when barframe initialized, this will be called.
			int col = CustOpts.custOps.getValue("BillPanel_Col") == null ? 4 : Integer.valueOf((String)CustOpts.custOps.getValue("BillPanel_Col"));
			int row = CustOpts.custOps.getValue("BillPanel_Row") == null ? 1 : Integer.valueOf((String)CustOpts.custOps.getValue("BillPanel_Row"));
			int table_H = (separator.getY() - CustOpts.VER_GAP * 2)/row;
			int table_W = (getWidth() - btnLeft.getWidth() * 2 - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2) / col - CustOpts.HOR_GAP;
			for(int r = 0, i = 0; r < row; r++) {
				for (int c = 0; c < col; c++) {
					int x = btnLeft.getX() + btnLeft.getWidth() + CustOpts.HOR_GAP + (CustOpts.HOR_GAP + table_W) * c;
					int y = (table_H + CustOpts.VER_GAP) * r + CustOpts.VER_GAP;
					onScrBills.get(i).setBounds(x, y, table_W, table_H);
					onScrBills.get(i).reLayout();
					onScrBills.get(i).resetColWidth(table_W);
					add(onScrBills.get(i));
					
					i++;
				}
			}
		}
		invalidate();
		revalidate();
		validate();
		repaint();
	}
	
	void moveDishToBill(BillPanel targetBillPanel) {
		int originalBillId = curDish.getBillID();
		int targetBillId = targetBillPanel.orderedDishAry != null && targetBillPanel.orderedDishAry.size() > 0? 
				targetBillPanel.orderedDishAry.get(0).getBillID() : 0;
		
		try { 
			//get the output price of the moving dish, it will be used to adjust the total of two bill.
			int outputTotalPrice = 0;
			StringBuilder sql = new StringBuilder("select toltalprice from output where id = ").append(curDish.getOutputID());
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
	        rs.beforeFirst();
	        while (rs.next()) {
	        	outputTotalPrice = rs.getInt("toltalprice");
	            break;
	        }
	        rs.close();// 关闭
			
			//update existing bill.
			if(originalBillId > 0) {
				sql = new StringBuilder("update bill set total = total - ")
						.append(outputTotalPrice)
						.append(" where id = ").append(originalBillId);
				PIMDBModel.getStatement().executeUpdate(sql.toString());
			}
			
			//generate/update bill for target bill
			if(targetBillId > 0) {
				//update the total price of the target bill
				sql = new StringBuilder("update bill set total = total + ")
						.append(outputTotalPrice)
						.append(" where id = ").append(targetBillId);
				PIMDBModel.getStatement().executeUpdate(sql.toString());
			}else {
				targetBillId = targetBillPanel.generateBillRecord(BarFrame.instance.valCurTable.getText(), targetBillPanel.billButton.getText(), BarFrame.instance.valStartTime.getText());
				sql = new StringBuilder("update bill set total = ")
						.append(outputTotalPrice)
						.append(" where id = ").append(targetBillId);
				PIMDBModel.getStatement().executeUpdate(sql.toString());
			}
			curDish.setBillID(targetBillId);	//might not necessary, just in case the billPanel will not updated before it's used anywhere.
			
			// Update the output to belongs to the new ContactID (contactID is bill Index,not  bill id)
			sql = new StringBuilder("update output set CONTACTID = ")
					.append(targetBillPanel.billButton.getText()).append(", category = " + targetBillId)
					.append(" where id = ").append(curDish.getOutputID());
			PIMDBModel.getStatement().executeUpdate(sql.toString());

		}catch(Exception exp) {
			L.e("SalesPanel", "unexpected error when updating the totalvalue of bill.", exp);
		}

		// update all the table content.
		curDish = null;
		initContent();
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

	@Override
	public void actionPerformed(ActionEvent e) {	//@NOTE: the bill button could trigger two times of event.
		Object o = e.getSource();
		if(o instanceof JToggleButton) {
			if(o == btnEqualBill) {	//aplite bill eually
				BillPanel panel = getCurBillPanel();
				if(panel == null) {
					JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
					btnEqualBill.setSelected(false);
					return;
				}
				BarFrame.numberPanelDlg.setBtnSource(btnEqualBill);
				BarFrame.numberPanelDlg.setFloatSupport(false);
				BarFrame.numberPanelDlg.setPercentSupport(false);
				BarFrame.numberPanelDlg.setModal(true);
				BarFrame.numberPanelDlg.setVisible(btnEqualBill.isSelected());
				if(NumberPanelDlg.confirmed) {
					int num = Integer.valueOf(NumberPanelDlg.curContent);
					if(num < 2) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
						return;
					}
					//split into {num} bills. each dish's number and price will be divided by {num}.
					Dish.splitOutputList(panel.orderedDishAry, num, null);//the third para is null, means update existing outputs
					//update existing bill.
					int billId = panel.orderedDishAry.get(0).getBillID();
					if(billId > 0) {
						try {
							StringBuilder sql = new StringBuilder("update bill set total = ")
							.append((int)(Float.valueOf(panel.valTotlePrice.getText()) * 100)/num).append(" where id = ").append(billId);
							PIMDBModel.getStatement().executeUpdate(sql.toString());
							
							panel.discount /= num;
							sql = new StringBuilder("update bill set discount = discount/" + num).append(" where id = ").append(billId);
							PIMDBModel.getStatement().executeUpdate(sql.toString());

							panel.serviceFee /= num;
							sql = new StringBuilder("update bill set otherReceived = otherReceived/" + num).append(" where id = ").append(billId);
							PIMDBModel.getStatement().executeUpdate(sql.toString());

						}catch(Exception exp) {
							L.e("SalesPanel", "unexpected error when updating the totalvalue of bill.", exp);
						}
					}
											
					for (int i = 1; i < num; i++) {				//generate output for splited ones.
						int billIndex = BillListPanel.getANewBillNumber();
						
						//generate a bill for each new occupied panel, incase there's discount info need to set into it.
						//@Note, when the initContent of the panel called, the bill ID will be set into the dish instance in memory.
						//and eventually, if the bill id is not 0, will calculate the service fee and discount into Total.
						int id = panel.generateBillRecord(BarFrame.instance.valCurTable.getText(), String.valueOf(billIndex), BarFrame.instance.valStartTime.getText(), num);

						ArrayList<Dish> tDishAry = new ArrayList<Dish>();
						for (Dish dish : panel.orderedDishAry) {
							tDishAry.add(dish.clone());
						}
						Dish.splitOutputList(tDishAry, num, String.valueOf(billIndex), id);
					}
				}
				initContent();
			}else if(o == btnSplitItem) {
				if(btnSplitItem.isSelected()) {//select
					//Todo:check if there's one item selected.
					if(curDish == null) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
						btnSplitItem.setSelected(false);
					}
				}else {
				
					// unselect: if here reached, there must be curDish.
					// remove the bill where the curDish is. @because sometimes that bill might be
					// unselected.
					List<BillPanel> panels = getSelectedBillPannels();
					for (BillPanel billPanel : panels) { // remove the original panel from the list.
						if (billPanel.billButton.getText().equals(curDish.getBillIndex())) {
							panels.remove(billPanel);
							break;
						}
					}
					if(panels.size() == 0) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.NoBillSeleted());
						return;
					}
					Dish.splitOutput(curDish, panels.size() + 1, null); // update the num and totalprice of curDish
					for (BillPanel billPanel : panels) { // insert new output with other billID
						Dish.splitOutput(curDish.clone(), panels.size() + 1, billPanel.billButton.getText());
					}

					curDish = null;
					initContent();
				}
			}else if(o == btnMoveItem) {//@note should consider the time, incase there'ss some bill not paid before, while was calculated into current client.
				if(curDish == null) {
					JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
					btnMoveItem.setSelected(false);
					return;
				}
				BillPanel panel = getCurBillPanel();
				if(panel == null) {
					JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
					btnMoveItem.setSelected(false);
					return;
				}
				BarFrame.numberPanelDlg.setBtnSource(btnMoveItem);
				BarFrame.numberPanelDlg.setFloatSupport(false);
				BarFrame.numberPanelDlg.setPercentSupport(false);
				BarFrame.numberPanelDlg.setModal(true);
				BarFrame.numberPanelDlg.setVisible(btnMoveItem.isSelected());
				if(NumberPanelDlg.confirmed) {
					int num = Integer.valueOf(NumberPanelDlg.curContent);
					if(num < 1) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
						return;
					}
					// the selecteed output to this bill.
					String sql = "update output set contactID = " + num + " where id = " + curDish.getOutputID();
			        try {
			        	PIMDBModel.getStatement().executeUpdate(sql);
			        }catch(Exception exp) {
			        	ErrorUtil.write(exp);
			        }
			        
			        initContent();
				}
			}
		}else if(o instanceof ArrowButton){
			if(o == btnLeft) {
				curPageNum--;
			}else if(o == btnRight) {
				curPageNum++;
			}
			btnLeft.setEnabled(curPageNum > 0);
			initContent();
		}else {
			if(o == btnAddUser){
				BarFrame.instance.valCurBill.setText("");
				BarFrame.instance.switchMode(2);
			}else if(o == btnPrintAll) {
				for (BillPanel billPanel : billPanels) {
					billPanel.printBill(BarFrame.instance.valCurTable.getText(), billPanel.billButton.getText(), BarFrame.instance.valStartTime.getText());
				}
				
			}else if(o == btnCombineAll) {//@note should consider the time, incase there'ss some bill not paid before, while was calculated into current client.
		        String sql =
		                "update output set contactID = 1 where SUBJECT = '" + BarFrame.instance.valCurTable.getText()
		                + "' and time = '" + BarFrame.instance.valStartTime.getText() + "' and DELETED != true";
		        try {
		        	PIMDBModel.getStatement().executeUpdate(sql);
		        }catch(Exception exp) {
		        	ErrorUtil.write(exp);
		        }
		        
	        	//combineOutputs(BarFrame.instance.valCurTable.getText(), BarFrame.instance.valStartTime.getText(), 1);

		        initContent();
			}else if( o == btnCompleteAll) {
		        try {
		        	Statement smt = PIMDBModel.getStatement();
		        	String tableID = BarFrame.instance.valCurTable.getText();
					String sql =
			                "update output set deleted = 100 where SUBJECT = '" + tableID
			                + "' and time > '" + BarFrame.instance.valStartTime.getText() + "' and DELETED != 1";
					smt.executeUpdate(sql);
					
					smt.executeUpdate("update bill set status = -1 where openTime = '" + BarFrame.instance.valStartTime.getText() + "'");
		        	//update the tabel status
		        	smt.executeUpdate("update dining_Table set status = 0 WHERE name = '" + tableID + "'");
		        }catch(Exception exp) {
		        	ErrorUtil.write(exp);
		        }
		        
				BarFrame.instance.valCurBill.setText("");
				BarFrame.instance.switchMode(0);
				
			}else if(o == btnReturn) {
				BarFrame.instance.valCurBill.setText("");
				BarFrame.instance.switchMode(0);
			}

			//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
			//set the table as unselected.
		}
	}
	
	//if in same table same openTime same billIndex, we should combine the outputs.
	//quy, total price, discount, otherReceived
    private void combineOutputs(String tableName, String startTime, int tableIndex) {
    	
//    			StringBuilder sql = new StringBuilder("update bill set total = ")
//				.append((int)(Float.valueOf(panel.valTotlePrice.getText()) * 100)/num).append(" where id = ").append(billId);
//				PIMDBModel.getStatement().executeUpdate(sql.toString());
//				
//				panel.discount /= num;
//				sql = new StringBuilder("update bill set discount = discount/" + num).append(" where id = ").append(billId);
//				PIMDBModel.getStatement().executeUpdate(sql.toString());
//
//				panel.serviceFee /= num;
//				sql = new StringBuilder("update bill set otherReceived = otherReceived/" + num).append(" where id = ").append(billId);
//				PIMDBModel.getStatement().executeUpdate(sql.toString());
				
    	String sql =
                "select * from output where SUBJECT = '" + tableName
                + "' and time = '" + startTime + "' and contactID = " + tableIndex + " and DELETED == 0";
        try {
        	PIMDBModel.getReadOnlyStatement().executeQuery(sql);
        	ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();

			int tColCount = getCurBillPanel().tblBillPanel.getColumnCount();
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
				dish.setOpenTime(rs.getString("OUTPUT.TIME"));	//output time is table's open time. no need to remember output created time.
				dish.setBillID(rs.getInt("OUTPUT.Category"));
				dish.setTotalPrice(rs.getInt("OUTPUT.TOLTALPRICE"));

				tValues[tmpPos][0] = dish.getDisplayableNum();				
				tValues[tmpPos][1] =  dish.getTotalPrice() / 100f;		
				tValues[tmpPos][2] =  dish.getOutputID();
				
				
				tmpPos++;
			}
        }catch(Exception exp) {
        	ErrorUtil.write(exp);
        }
	}

	public static int getANewBillNumber(){
    	int num = 0;
    	try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
            ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '"
                    + BarFrame.instance.valCurTable.getText()
                    + "' and deleted = 0 and time = '" + BarFrame.instance.valStartTime.getText()
                    + "' order by contactID");
			rs.afterLast();
			rs.relative(-1);
			num = rs.getInt("contactID");
		} catch (Exception exp) {
			L.d("BillListPane.getANewBillNumber", "lagest num is 0.");
		}
    	return num + 1;
    }

	BillPanel getCurBillPanel(){
		if(billPanels.size() == 1)
			return billPanels.get(0);
		else {
			List<BillPanel> panels = getSelectedBillPannels();
			return panels.size() == 1 ? panels.get(0) : null;
		}
	}
    
	List<BillPanel> getSelectedBillPannels(){
		List<BillPanel> panels = new ArrayList<>();
		for (BillPanel billPanel : onScrBills) {
			if(billPanel.billButton.isSelected())
				panels.add(billPanel);
		}
		
		return panels;
	}
	
	List<BillPanel> billPanels;
	List<BillPanel> onScrBills;

    private ArrowButton btnLeft;
    private ArrowButton btnRight;
	FunctionButton btnAddUser;
	JSeparator separator;
	FunctionButton btnPrintAll;
	JToggleButton btnEqualBill;
	JToggleButton btnSplitItem;
	JToggleButton btnMoveItem;
	FunctionButton btnCombineAll;
	
	FunctionButton btnCompleteAll;

	FunctionButton btnReturn;
}
