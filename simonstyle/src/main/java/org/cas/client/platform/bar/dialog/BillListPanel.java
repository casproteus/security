package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.ArrowButton;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;
import org.hsqldb.lib.HashMap;

public class BillListPanel extends JPanel implements ActionListener, ComponentListener{
	public static Dish curDish;
	int curPageNum;
	
	public BillListPanel() {
		initComponent();
	}

	void initComponent() {
		removeAll();
		billPanels= new ArrayList<BillPanel>();
		onScrBillPanels= new ArrayList<BillPanel>();

	    btnLeft = new ArrowButton("<<");
	    btnRight = new ArrowButton(">>");
		btnAddUser = new FunctionButton(BarFrame.consts.AddUser());
		btnPrintAll = new FunctionButton(BarFrame.consts.PrintAll());
		btnPrintOneBill = new FunctionButton(BarFrame.consts.PrintOneBill());
		btnPrintOneInVoice = new FunctionButton(BarFrame.consts.PrintOneInvoice());
		
		btnEqualBill = new JToggleButton(BarFrame.consts.EqualBill());
		btnSplitItem = new JToggleButton(BarFrame.consts.SplitItem());
		btnMoveItem = new JToggleButton(BarFrame.consts.MoveItem());
		btnCombineAll = new FunctionButton(BarFrame.consts.CombineAll());
		
		btnSuspendAll = new FunctionButton(BarFrame.consts.SuspendAll());
		btnReturn = new FunctionButton(BarFrame.consts.RETURN());
		
		separator= new JSeparator();

		btnLeft.setMargin(new Insets(0, 0, 0, 0));
		btnRight.setMargin(new Insets(0, 0, 0, 0));
		btnAddUser.setMargin(new Insets(0, 0, 0, 0));
		btnPrintAll.setMargin(btnAddUser.getMargin());
		btnPrintOneBill.setMargin(btnAddUser.getMargin());
		btnPrintOneInVoice.setMargin(btnAddUser.getMargin());
		btnEqualBill.setMargin(btnAddUser.getMargin());
		btnCombineAll.setMargin(btnAddUser.getMargin());
		btnSplitItem.setMargin(btnAddUser.getMargin());
		btnMoveItem.setMargin(btnAddUser.getMargin());
		btnSuspendAll.setMargin(btnAddUser.getMargin());
		btnReturn.setMargin(btnAddUser.getMargin());
		
		setLayout(null);
		
		add(btnLeft);
		add(btnRight);
		add(btnAddUser);
		add(separator);
		add(btnPrintAll);
//		add(btnPrintOneBill);
//		add(btnPrintOneInVoice);
		add(btnEqualBill);
		add(btnCombineAll);
		add(btnSplitItem);
		add(btnMoveItem);
		add(btnSuspendAll);
		add(btnReturn);
		
		addComponentListener(this);
		btnLeft.addActionListener(this);
		btnRight.addActionListener(this);
		btnAddUser.addActionListener(this);
		btnPrintAll.addActionListener(this);
		btnPrintOneBill.addActionListener(this);
		btnPrintOneInVoice.addActionListener(this);
		btnEqualBill.addActionListener(this);
		btnCombineAll.addActionListener(this);
		btnSplitItem.addActionListener(this);
		btnMoveItem.addActionListener(this);
		btnSuspendAll.addActionListener(this);
		btnReturn.addActionListener(this);
		
		btnLeft.setEnabled(curPageNum > 0);
		reLayout();
	}
	
	void initContent() {
		BarFrame.instance.cmbCurTable.setEnabled(true);	//if can reach to the view, mean there un completed bills for sure, so the change table combobox is ennabled.
		
		cleanInterface();
		
		// load all the unclosed outputs under this table with content inside.---------------------------
		//output will be set as deleted=true only when click a "-" button. when bill closed, the output will not be set as deleted = true! 
		//so closed bill of this table will also be counted. but will displayed in different color.
		
		reInitBillPanels();
		
		reInitOnscreenBills();
		
		allowUnCombineCheck();
		
		reLayout();
	}

	public void allowUnCombineCheck() {
		int unclosedNum = 0;
		for (BillPanel billPanel : billPanels) {
			if(billPanel.status < DBConsts.completed) {
				unclosedNum ++;
			}
		}
		if(unclosedNum == 1) {
			String tableName = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
			String openTime = BarFrame.instance.valStartTime.getText();
			StringBuilder sql =  new StringBuilder("SELECT DISTINCT category from output where SUBJECT = '").append(tableName)
					.append("' and (deleted is null or deleted < ").append(DBConsts.completed)
					.append(") and time = '").append(openTime).append("'");
			try {
				ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
				rs.afterLast();
	            rs.relative(-1);
	            if (rs.getRow() > 1) {
	            	btnCombineAll.setText(BarFrame.consts.UnCombine());
	            }
			} catch (SQLException e) {
	            ErrorUtil.write(e);
	        }
		}else {
			btnCombineAll.setText(BarFrame.consts.CombineAll());
		}
	}

	private void reInitBillPanels(){
		String tableName = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
		String openTime = BarFrame.instance.valStartTime.getText();
		StringBuilder sql = new StringBuilder("SELECT DISTINCT contactID from output where SUBJECT = '").append(tableName)
				.append("' and (deleted is null or deleted < ").append(DBConsts.expired)
				.append(") and time = '").append(openTime).append("' order by contactID");
		
		try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.beforeFirst();
		
			while (rs.next()) {
				JToggleButton billButton = new JToggleButton();
				billButton.setText(String.valueOf(rs.getInt("contactID")));
				billButton.setMargin(new Insets(0, 0, 0, 0));
				
				BillPanel billPanel = new BillPanel(this, billButton);
				billPanel.initContent();
				billPanels.add(billPanel);
			}
			
		} catch (Exception e) {
			L.e("BillListPane", "Unexpected exception when init the bill panels." + sql, e);
		}
	}

	private void reInitOnscreenBills() {
		//do it outside the above loop, because there's another qb query inside.
		int col = BarOption.getBillPageCol();
		int row = BarOption.getBillPageRow();
		
		int billNum = getANewBillIdx(null, null);
		for(int i = 0; i < row * col; i++) {
			if(row * col * curPageNum + i < billPanels.size()) {	//some panel is using the panel in billPanels list.
				onScrBillPanels.add(billPanels.get(row * col * curPageNum + i));
				btnRight.setEnabled(true);
			}else {													//others are temperally newed BillPanel.
				BillPanel panel = new BillPanel(this, new JToggleButton(String.valueOf(billNum)));	//have to give a number to construct valid sql.
				panel.initComponent();
				panel.initContent();
				onScrBillPanels.add(panel);
				btnRight.setEnabled(false);
				billNum++;
			}
		}
	}

	public void cleanInterface() {
		for(int i = onScrBillPanels.size() - 1; i >= 0; i--) {
			remove(onScrBillPanels.get(i));
		}
		billPanels.clear();
		onScrBillPanels.clear();
	}
	
	private void reLayout() {

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 9) / 8;
        int tBtnHeight = panelHeight / 10;


		btnReturn.setBounds(CustOpts.SIZE_EDGE, panelHeight - tBtnHeight - CustOpts.VER_GAP, tBtnWidht, tBtnHeight);
		
		btnAddUser.setBounds(btnReturn.getX() + btnReturn.getWidth() + CustOpts.HOR_GAP, btnReturn.getY(), 
				tBtnWidht, tBtnHeight);
		btnPrintAll.setBounds(btnAddUser.getX() + btnAddUser.getWidth() + CustOpts.HOR_GAP, btnReturn.getY(),
				tBtnWidht, tBtnHeight);
		btnPrintOneBill.setBounds(btnPrintAll.getX() + btnPrintAll.getWidth() + CustOpts.HOR_GAP, btnPrintAll.getY(),
				tBtnWidht, tBtnHeight);
		btnPrintOneInVoice.setBounds(btnPrintOneBill.getX() + btnPrintOneBill.getWidth() + CustOpts.HOR_GAP, btnPrintOneBill.getY(),
				tBtnWidht, tBtnHeight);
		btnEqualBill.setBounds(btnPrintAll.getX() + btnPrintAll.getWidth() + CustOpts.HOR_GAP, btnPrintAll.getY(),
				tBtnWidht, tBtnHeight);
		btnCombineAll.setBounds(btnEqualBill.getX() + btnEqualBill.getWidth() + CustOpts.HOR_GAP, btnEqualBill.getY(),
				tBtnWidht, tBtnHeight);
		btnSplitItem.setBounds(btnCombineAll.getX() + btnCombineAll.getWidth() + CustOpts.HOR_GAP, btnCombineAll.getY(),
				tBtnWidht, tBtnHeight);
		btnMoveItem.setBounds(btnSplitItem.getX() + btnSplitItem.getWidth() + CustOpts.HOR_GAP, btnSplitItem.getY(),
				tBtnWidht, tBtnHeight);
		btnSuspendAll.setBounds(btnMoveItem.getX() + btnMoveItem.getWidth() + CustOpts.HOR_GAP, btnMoveItem.getY(),
				tBtnWidht, tBtnHeight);
		
		separator.setBounds(CustOpts.HOR_GAP, 
				btnSuspendAll.getY() - CustOpts.VER_GAP * 2,
				getWidth() - CustOpts.HOR_GAP * 2, tBtnHeight);
		
		btnLeft.setBounds(CustOpts.SIZE_EDGE, 
				separator.getY() - 40 - CustOpts.VER_GAP * 2,
				40, 40);
		btnRight.setBounds(getWidth() - 40 - CustOpts.SIZE_EDGE, 
				btnLeft.getY(),
				40, 40);

		if(onScrBillPanels.size() > 0) { //@NOTE: when barframe initialized, this will be called.
			int col = CustOpts.custOps.getValue("BillPanel_Col") == null ? 4 : Integer.valueOf((String)CustOpts.custOps.getValue("BillPanel_Col"));
			int row = CustOpts.custOps.getValue("BillPanel_Row") == null ? 1 : Integer.valueOf((String)CustOpts.custOps.getValue("BillPanel_Row"));
			int table_H = (separator.getY() - CustOpts.VER_GAP * 2)/row;
			int table_W = (getWidth() - btnLeft.getWidth() * 2 - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2) / col - CustOpts.HOR_GAP;
			for(int r = 0, i = 0; r < row; r++) {
				for (int c = 0; c < col; c++) {
					int x = btnLeft.getX() + btnLeft.getWidth() + CustOpts.HOR_GAP + (CustOpts.HOR_GAP + table_W) * c;
					int y = (table_H + CustOpts.VER_GAP) * r + CustOpts.VER_GAP;
					onScrBillPanels.get(i).setBounds(x, y, table_W, table_H);
					onScrBillPanels.get(i).reLayout();
					onScrBillPanels.get(i).resetColWidth(table_W);
					add(onScrBillPanels.get(i));
					
					i++;
				}
			}
		}
		invalidate();
		revalidate();
		validate();
		repaint();
	}
	
	//can only be triggered when clicking the billpanel's viewport or table area under "billListPanel mode".
	void moveDishToBill(BillPanel targetBillPanel) {
		int originalBillId = curDish.getBillID();
		int targetBillId = targetBillPanel.orderedDishAry != null && targetBillPanel.orderedDishAry.size() > 0? 
				targetBillPanel.orderedDishAry.get(0).getBillID() : 0;
		//generate the new bills and generate the ref in comment.
		if(getCurBillPanel().status >= DBConsts.billPrinted || getCurBillPanel().status < DBConsts.original
				|| targetBillPanel.status >= DBConsts.billPrinted || targetBillPanel.status < DBConsts.original) {
			if (JOptionPane.showConfirmDialog(this, BarFrame.consts.ConvertClosedBillBack(), BarFrame.consts.Operator(),
		            JOptionPane.YES_NO_OPTION) != 0) {// are you sure to convert the voided bill back？
		        return;
			}else {
				if(getCurBillPanel().status >= DBConsts.billPrinted || getCurBillPanel().status < DBConsts.original) {
					getCurBillPanel().reGenerate(getCurBillPanel().billButton.getText());
				}
				
				if(targetBillPanel.status >= DBConsts.billPrinted || targetBillPanel.status < DBConsts.original) {
					targetBillPanel.reGenerate(targetBillPanel.billButton.getText());
					if(getCurBillPanel().status >= DBConsts.billPrinted || getCurBillPanel().status < DBConsts.original) {
						StringBuilder sql = new StringBuilder("update bill set comment = comment + ' ")
								.append(PrintService.REF_TO).append(getCurBillPanel().getBillID()).append("'")
								.append(PrintService.OLD_SUBTOTAL).append(BarUtil.formatMoney(getCurBillPanel().subTotal/100.0))
								.append(" where id = ").append(targetBillPanel.getBillID());
						try {
							PIMDBModel.getStatement().executeUpdate(sql.toString());
						}catch(Exception e) {
							L.e("BillPane", "Excepioint in print bill:" + sql, e);
						}
					}
				}
			}
		}
		
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
				targetBillId = BarFrame.instance.createAnEmptyBill(
						BarFrame.instance.cmbCurTable.getSelectedItem().toString(), 
						BarFrame.instance.valStartTime.getText(), 
						Integer.valueOf(targetBillPanel.billButton.getText()));
				sql = new StringBuilder("update bill set total = ")
						.append(outputTotalPrice)
						.append(" where id = ").append(targetBillId);
				PIMDBModel.getStatement().executeUpdate(sql.toString());
			}
			curDish.setBillID(targetBillId);	//might not necessary, just in case the billPanel will not updated before it's used anywhere.
			
			// Update the output to belongs to the new ContactID (contactID is bill Index,not  bill id)
			sql = new StringBuilder("update output set CONTACTID = ").append(targetBillPanel.billButton.getText())
					.append(", category = " + targetBillId)
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
			if(o == btnEqualBill) {	//splite bill eually
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
				BarFrame.numberPanelDlg.reLayout();
				BarFrame.numberPanelDlg.setVisible(btnEqualBill.isSelected());
				if(NumberPanelDlg.confirmed) {
					int num = Integer.valueOf(NumberPanelDlg.curContent);
					if(num < 2) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
						return;
					}
					
					//if current billPanel is printed, then create a new one and expire it.
					if(getCurBillPanel().status >= DBConsts.billPrinted || getCurBillPanel().status < DBConsts.original) {
						getCurBillPanel().reGenerate(null);
					}
					
					//split into {num} bills. each dish's number and price will be divided by {num}.
					Dish.splitOutputList(panel.orderedDishAry, num, null);//the third parameter is null, means update existing outputs
					//update existing bill.
					int curBillId = getCurBillPanel().getBillID();
					if(curBillId > 0) {
						try {
							StringBuilder sql = new StringBuilder("update bill set total = ").append(Math.round(Float.valueOf(panel.valTotlePrice.getText()) * 100)/num)
									.append(", discount = discount/").append(num)
									.append(", serviceFee = serviceFee/").append(num)
							.append(" where id = ").append(curBillId);
							PIMDBModel.getStatement().executeUpdate(sql.toString());
							
							panel.discount /= num;
							panel.serviceFee /= num;
						}catch(Exception exp) {
							L.e("SalesPanel", "unexpected error when updating the totalvalue of bill.", exp);
						}
					}
											
					for (int i = 1; i < num; i++) {				//generate output for splited ones.
						int billIndex = BillListPanel.getANewBillIdx(null, null);
						
						//generate a bill for each new occupied panel, incase there's discount info need to set into it.
						//@Note, when the initContent of the panel called, the bill ID will be set into the dish instance in memory.
						//and eventually, if the bill id is not 0, will calculate the service fee and discount into Total.
						int id = panel.cloneCurrentBillRecord(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
								String.valueOf(billIndex),
								BarFrame.instance.valStartTime.getText(),
								Math.round(Float.valueOf(panel.valTotlePrice.getText()) * 100/num));

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
					//check if there's one item selected.
					if(curDish == null) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
						btnSplitItem.setSelected(false);
					}
				}else {
				
					// unselect: if here reached, there must be curDish.
					// remove the bill where the curDish is. @because sometimes that bill might be
					// unselected.
					List<BillPanel> selectedPanels = getSelectedBillPannels();
					for (BillPanel billPanel : selectedPanels) { // remove the original panel from the list.
						if (billPanel.billButton.getText().equals(curDish.getBillIndex())) {
							selectedPanels.remove(billPanel);
							break;
						}
					}
					if(selectedPanels.size() == 0) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.NoBillSeleted());
						return;
					}
					Dish.splitOutput(curDish, selectedPanels.size() + 1, null); // update the num and totalprice of curDish
					for (BillPanel billPanel : selectedPanels) { // insert new output with other billID
						int billIndex = BillListPanel.getANewBillIdx(null, null);
						//generate a bill for each new occupied panel, incase there's discount info need to set into it.
						//@Note, when the initContent of the panel called, the bill ID will be set into the dish instance in memory.
						//and eventually, if the bill id is not 0, will calculate the service fee and discount into Total.
						int id = BarFrame.instance.createAnEmptyBill(
								BarFrame.instance.cmbCurTable.getSelectedItem().toString(), 
								BarFrame.instance.valStartTime.getText(), 
								billIndex);
						Dish dish = curDish.clone();
						dish.setBillID(id);
						Dish.splitOutput(dish, selectedPanels.size() + 1, billPanel.billButton.getText());
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
				if(!panel.checkStatus())	//create a new bill for original bill
					return;
				moveItemAction();
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
				((SalesPanel)BarFrame.instance.panels[2]).addNewBillInCurTable();
			}else if(o == btnPrintAll) {
				ArrayList<BillPanel> unclosedBillPanels = gatherAllUnclosedBillPanels();
				for (BillPanel billPanel : unclosedBillPanels) {
					billPanel.printBill(
							BarFrame.instance.cmbCurTable.getSelectedItem().toString(), 
							billPanel.billButton.getText(), 
							BarFrame.instance.valStartTime.getText(),
							true);
				}
			}else if(o == btnPrintOneBill) {    //Print into one bill with client sub total
				if(!checkColosedBill()) {
					return;
				}

				//check if all bills are not closed, and find the first panel
				ArrayList<BillPanel> unclosedBillPanels = gatherAllUnclosedBillPanels();
				
				PrintService.exePrintBills(unclosedBillPanels);
				
				//combine bills--------------------------------------------------------------------------------------------
				combineBills(unclosedBillPanels);

			}else if(o == btnPrintOneInVoice){
				new PayMethodDlg(this).show((FunctionButton)o);
				
			}else if(o == btnCombineAll) {	//@note should consider the time, incase there's some bill not paid before, while was calculated into current client.
				if(btnCombineAll.getText().equals(BarFrame.consts.CombineAll())) {
					if(!checkColosedBill()) {
						return;
					}
					
					//check if all bills are not closed
					ArrayList<BillPanel> unclosedBillPanels = gatherAllUnclosedBillPanels();
					combineBills(unclosedBillPanels);
				}
				
				//for uncombine action.
				else {
					HashMap idxMap = new HashMap();
					StringBuilder sql = new StringBuilder("select * from bill where tableId = '").append(BarFrame.instance.cmbCurTable.getSelectedItem().toString()).append("'")
							.append(" and opentime = '").append(BarFrame.instance.valStartTime.getText()).append("'")
							.append(" and (status is null or status < ").append(DBConsts.completed).append(")");
					try {
						ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			            rs.beforeFirst();
			            while (rs.next()) {
			                idxMap.put(rs.getInt("id"), rs.getInt("billIndex"));
			            }
					}catch(Exception exp) {
						L.e("BillListPanel", "exception when change output back to original bill" + sql, exp);
					}
					
					//get the unclosed billPane.
					ArrayList<Dish> orderedDishes = new ArrayList<Dish>();
					for (BillPanel billPanel : billPanels) {
						if(billPanel.status < DBConsts.completed) {
							orderedDishes = billPanel.orderedDishAry;
							break;
						}
					}
					
					for (Dish dish : orderedDishes) {
						Object idx = idxMap.get(dish.getBillID());
						if(idx != null) {
							sql = new StringBuilder("update output set contactID = ").append(idx)
									.append(" where id = ").append(dish.getOutputID());
							try {
								PIMDBModel.getStatement().execute(sql.toString());
							}catch(Exception exp) {
								L.e("BillListPanel", "exception when change output back to original bill" + sql, exp);
							}
						}
					}
					initContent();
				}
			}else if( o == btnSuspendAll) {
				if(!checkColosedBill()) {
					return;
				}
				
		        try {
		        	Statement smt = PIMDBModel.getStatement();
		        	String tableID = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
		        	//update outputs
					StringBuilder sql = new StringBuilder("update output set deleted = ").append(DBConsts.suspended)
			                .append(" where SUBJECT = '").append(tableID)
			                .append("' and time = '").append(BarFrame.instance.valStartTime.getText())
			                .append("' and deleted is null or deleted = ").append(DBConsts.original);
					smt.executeUpdate(sql.toString());
					
					//update bills
					sql = new StringBuilder("update bill set status = ").append(DBConsts.suspended)
							.append(" where openTime = '").append(BarFrame.instance.valStartTime.getText())
							.append("' and deleted is null or deleted = ").append(DBConsts.original);
					smt.executeUpdate(sql.toString());
					
		        	//update the tabel status
					BarFrame.instance.closeATable(tableID, null);
		        }catch(Exception exp) {
		        	ErrorUtil.write(exp);
		        }
		        
				BarFrame.instance.setCurBillIdx("");
				BarFrame.instance.switchMode(0);
				
			}else if(o == btnReturn) {
				BarFrame.instance.setCurBillIdx("");
				BarFrame.instance.switchMode(0);
			}

			//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
			//set the table as unselected.
		}
	}

	void combineBills(ArrayList<BillPanel> unclosedBillPanels) {
		String firstUnclosedBillIdx = unclosedBillPanels.get(0).billButton.getText();
		
		//update all related output to belongs to first Bill, deleted and completed output will not be modified.
		//the billId will not be modified, so when waiter want to do a undo, can use the billID to undo combine all.
		String tableName = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
		String openTime = BarFrame.instance.valStartTime.getText();
		StringBuilder sql = new StringBuilder("update output set contactID = ").append(firstUnclosedBillIdx)
				//@NOTE we don't change the billID when combinAll(to support undo) .append(", category = '").append(firstUnclosedBillId).append("'")
				.append(" where SUBJECT = '").append(tableName)
				.append("' and time = '").append(openTime)
				.append("' and deleted is null or DELETED = ").append(DBConsts.original);
		try {
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		}catch(Exception exp) {
			L.e("Combine All", "exception when updating output with "+ sql, exp);
		}
		
//				一轮遍历过之后，可以确定消灭了二次分单的存在。但是一次分单的情况仍然存在。所以有必要再进行一遍，用来把pS也去掉。
//				两遍目前是足够的。但程序上应该写成说只要发现“pkOrPsFoundFlag”没有被打上，就表示合并结束（没分单过的是否合并是个问题，因为有的菜点了多次，但每个菜可能给了不同的折扣）
		combineOutputs(tableName, openTime, Integer.valueOf(firstUnclosedBillIdx));
		
		//combine bills discount and services fees---------------------------------------------------------------------
		//combine the discount, service fee, of all the none-empty bills.
		//Note: we don't deleted combined bills now, because might be used again later. 
		//when we reset the table. we check if it the last non-empty bill and clean all the empty bill .
		int combinedDiscount = 0;
		int combinedServiceFee = 0;
		StringBuilder combinedComment = new StringBuilder();
		for (BillPanel billPanel : unclosedBillPanels) {
			combinedDiscount += billPanel.discount;
			combinedServiceFee += billPanel.serviceFee;
			if(billPanel.status >= DBConsts.billPrinted || billPanel.status < DBConsts.original) {
				combinedComment.append(PrintService.REF_TO).append(billPanel.getBillID()).append(PrintService.OLD_SUBTOTAL).append(BarUtil.formatMoney(billPanel.subTotal/100.0));
			}
		}
		
		BillPanel billPanel = unclosedBillPanels.get(0);
		if(billPanel.status >= DBConsts.billPrinted || billPanel.status < DBConsts.original) {
			billPanel.reGenerate(billPanel.billButton.getText());
		}
		sql = new StringBuilder("update bill set discount = ").append(combinedDiscount)
				.append(", serviceFee = ").append(combinedServiceFee)
				.append(", comment = '").append(combinedComment).append("'")
				.append(" where id = ").append(billPanel.getBillId());	//billPanel's billID will be a new bill Id if it's regenerated.
		
		try {
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		}catch(Exception exp) {
			L.e("Combine All", "exception when updating bill with "+ sql, exp);
		}
		
		//reinit the display----------------------------------------------------------------------------------------------
		initContent();
	}

	ArrayList<BillPanel> gatherAllUnclosedBillPanels() {
		ArrayList<BillPanel> billPanelsToCombine = new ArrayList<BillPanel>();	//add all panel to be combined, for calculating the service fee and dicount in it.
		for (BillPanel billPanel : billPanels) {
			if(billPanel.status != DBConsts.completed) {
				billPanelsToCombine.add(billPanel);
			}
		}
		return billPanelsToCombine;
	}

	boolean checkColosedBill() {
		for (BillPanel billPanel : billPanels) {
			if(billPanel.status == DBConsts.completed) {
				if (JOptionPane.showConfirmDialog(this, BarFrame.consts.workOnOnlyUnclosedBills(),
		                DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
		            return false;
				}else {
					return true;
				}
			}
		}
		return true;
	}

	private void moveItemAction() {
		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.BILL());
		BarFrame.numberPanelDlg.setBtnSource(btnMoveItem);
		BarFrame.numberPanelDlg.setFloatSupport(false);
		BarFrame.numberPanelDlg.setPercentSupport(false);
		BarFrame.numberPanelDlg.setModal(true);
		BarFrame.numberPanelDlg.reLayout();
		BarFrame.numberPanelDlg.setVisible(btnMoveItem.isSelected());
		if(NumberPanelDlg.confirmed) {
			int targetBillIdx = Integer.valueOf(NumberPanelDlg.curContent);
			if(targetBillIdx < 1) {
				JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
			}else {
				//check if the original panel need to be regenerat
	        	BillPanel originalBillPanel = billPanels.get(Integer.valueOf(curDish.getBillIndex()) - 1);
	        	int origianlBillstatus = originalBillPanel.status;	//save the status before it's changed when regenerate the bill.
	        	if(origianlBillstatus >= DBConsts.billPrinted || origianlBillstatus < DBConsts.original) {
	        		originalBillPanel.reGenerate(billPanels.get(targetBillIdx - 1).billButton.getText());
	        	}
	        	
				int billId = 0;
				//check if the bill exist
				StringBuilder sql = new StringBuilder("Select id, status from Bill where billIndex = '").append(targetBillIdx)
				.append("' and tableId = '").append(BarFrame.instance.cmbCurTable.getSelectedItem().toString()).append("'")
				.append(" and opentime = '").append(BarFrame.instance.valStartTime.getText()).append("'")
				.append(" and (status is null or status < ").append(DBConsts.expired).append(")");
				try {
					ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
		        	if(rs.next()) {
		        		billId = rs.getInt("id");
		        		int status = rs.getInt("status");
		        		if(status >= DBConsts.billPrinted || status < DBConsts.original) {
	        				if (JOptionPane.showConfirmDialog(this, BarFrame.consts.ConvertClosedBillBack(), BarFrame.consts.Operator(),
	        			            JOptionPane.YES_NO_OPTION) != 0) {// are you sure to convert the voided bill back？
	        			        return;
	        				}else {
	        					billPanels.get(targetBillIdx - 1).reGenerate(billPanels.get(targetBillIdx - 1).billButton.getText());
	        				}
		        		}
		        	}else {
		        		billId = BarFrame.instance.createAnEmptyBill(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
								BarFrame.instance.valStartTime.getText(), targetBillIdx);
		        	}
		        	
		        	if(origianlBillstatus >= DBConsts.billPrinted || origianlBillstatus < DBConsts.original) {	//@NOTE: use the old one, new one might be changed when regenerate.
		        		//find the current bill's status and add comment with the id and subtotal of original bill.
		        		sql = new StringBuilder("update bill set comment = comment + '").append(PrintService.REF_TO).append(originalBillPanel.getBillID());
		        		if(originalBillPanel.status == DBConsts.completed || originalBillPanel.status < DBConsts.original) {
		        			sql.append("F");
		        		}
		        		sql.append(PrintService.OLD_SUBTOTAL).append(BarUtil.formatMoney(originalBillPanel.subTotal / 100.0))
		        		.append(" where id = ").append(billId);
		        		
		        		try {
				        	PIMDBModel.getStatement().executeUpdate(sql.toString());
				        }catch(Exception exp) {
				        	ErrorUtil.write(exp);
				        }
		        	}
		        }catch(Exception exp) {
		        	JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
		        	return;
		        }
				
				// the selected output to this bill.
				sql = new StringBuilder("update output set contactID = ").append(targetBillIdx)
						.append(", category = ").append(billId)
						.append(" where id = ").append(curDish.getOutputID());
		        try {
		        	PIMDBModel.getStatement().executeUpdate(sql.toString());
		        }catch(Exception exp) {
		        	ErrorUtil.write(exp);
		        }
		        BillListPanel.curDish = null;
		        initContent();
		        //find the two bill, and update the total price in db.
//TODO: this is not the right way to find out the two relevant panel, since it's a little complex, let's leave to next version	
//		        for (BillPanel billPanel : billPanels) {
//			        BarUtil.updateBillRecordPrices(billPanel);
//				}
			}
		}
	}
	
	//if in same table same openTime same billIndex, we should combine the outputs.
	//qty, total price, discount, serviceFee
    private void combineOutputs(String tableName, String startTime, int billIdx) {
    	
    	//target the items which can be combined.
    	List<Dish> dishes = new ArrayList<Dish>();
    	
    	StringBuilder sql = new StringBuilder("select * from output where SUBJECT = '").append(tableName)
        	.append("' and time = '").append(startTime)
        	.append("' and contactID = ").append(billIdx)
        	.append(" and (deleted is null or deleted = ").append(DBConsts.original).append(") order by id");
        try {
        	ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.beforeFirst();
			while (rs.next()) {
				int num = rs.getInt("OUTPUT.AMOUNT");
				if(num < BarOption.MaxQTY) {
					continue;
				}
 	    		Dish dish = new Dish();
 	    		dish.setNum(num);
 	    		dish.setDiscount(rs.getInt("discount"));//
 	    		dish.setId(rs.getInt("PRODUCTID"));
 	    		dish.setModification(rs.getString("OUTPUT.CONTENT"));//
 	    		dish.setOutputID(rs.getInt("OUTPUT.ID"));//
 	    		dish.setTotalPrice(rs.getInt("OUTPUT.TOLTALPRICE"));
				dishes.add(dish);
			}
        }catch(Exception exp) {
        	ErrorUtil.write(exp);
        }

        HashMap map = new HashMap();
    	for (int i = dishes.size() - 1; i >= 0; i--) {	//here, all dish是带有PS或者PK的
    		Dish dish = dishes.get(i);
    		int num = dish.getNum();
    		//记录下PK和PS.和Num。总单的discount，和totalPrice（total price应该是已经扣除了本菜的discount的）
			int pK = num /(BarOption.MaxQTY * 100);
    		if(num > BarOption.MaxQTY * 100) {
    			num = num %(BarOption.MaxQTY * 100);
    		}
    		int pS = num /BarOption.MaxQTY;
    		if(num > BarOption.MaxQTY) {
    			num = num % BarOption.MaxQTY;
    		}
    		
        	String key = new StringBuilder("pk:").append(pK).append("ps:").append(pS).append("num:").append(num)
        		.append("dish:").append(dish.getId()).append(String.valueOf(dish.getModification())).toString();
        	
    		if(map.containsKey(key)) {//if map already contains this key, then delete this output
    			
    			sql = new StringBuilder("update output set deleted = ").append(DBConsts.deleted)
    					.append(" where id = ").append(dish.getOutputID());
    			try {
    				PIMDBModel.getStatement().executeUpdate(sql.toString());
    			}catch(Exception e) {
    				L.e("combineOutputs", " exception when change status of an ouput which already in map:" + sql, e);
    			}
    			dishes.remove(i);
    			
    			//并把map中的value减少1.如果减完1后value值为1，map中去掉这个key。同时更新原始的（那个key相同但是没有被删除的）的output记录到数据库，
				//把discount*value，totalPrice也*valuev，num中的pK位去掉，如果已经去掉了，那么把PS位去掉。（继续遍历时，如果发现还有这个key的，将会新建一个key存入map）
    			Dish tD = (Dish)map.get(key);
    			int value = tD.getDspIndex() - 1;

				tD.setDspIndex(value);
				tD.setDiscount(tD.getDiscount() + dish.getDiscount());
				tD.setTotalPrice(tD.getTotalPrice() + dish.getTotalPrice());
				
    			if(value == 1) {
    				map.remove(key);
    				int newNum = pK > 1 ? pS * BarOption.MaxQTY + num : num;
    				sql = new StringBuilder("update output set AMOUNT = ").append(newNum)
    						.append(", discount = ").append(tD.getDiscount())
    						.append(", TOLTALPRICE = ").append(tD.getTotalPrice())
    						.append(" where id = ").append(tD.getOutputID());
        			try {
        				PIMDBModel.getStatement().executeUpdate(sql.toString());
        			}catch(Exception e) {
        				L.e("combineOutputs", "update output set amount = " + newNum + " where id = " + dish.getOutputID(), e);
        			}
    			}
    		}else {//如果发现map中没有这个key，那么创建一个key存入map。PK为value，如果PK不存在，或者为1，或者为0，那么用PS为value。
    			dish.setDspIndex(pK > 1 ? pK : pS);	//use dspIndex to save the temparal new value, so we can know if it's merged every
    			map.put(key, dish);
    		}
		}
	}

	public static int getANewBillIdx(String tableName, String openTime){
		if(tableName == null) {
			tableName = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
		}
		if(openTime == null) {
			openTime = BarFrame.instance.valStartTime.getText();
		}
		
    	int num = 0;
    	try {
			StringBuilder sql = new StringBuilder("select DISTINCT billIndex from bill where tableId = '").append(tableName).append("'")
				.append(" and opentime = '").append(openTime).append("'");
			if(BarOption.isFastFoodMode()) {
				sql.append(" and (status is null or status < ").append(DBConsts.deleted).append(")");
			} else {
				sql.append(" and (status is null or status < ").append(DBConsts.completed).append(" and status >= 0)");
			}
			sql.append(" order by billIndex");
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
			rs.relative(-1);
			num = rs.getInt("billIndex");
		} catch (Exception exp) {
			L.d("BillListPane.getANewBillNumber", "found no bill on an already opened table.");
			return 1;
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
		for (BillPanel billPanel : onScrBillPanels) {
			if(billPanel.billButton.isSelected())
				panels.add(billPanel);
		}
		
		return panels;
	}
	
	List<BillPanel> billPanels;
	List<BillPanel> onScrBillPanels;

    private ArrowButton btnLeft;
    private ArrowButton btnRight;
	FunctionButton btnAddUser;
	JSeparator separator;
	FunctionButton btnPrintAll;
	FunctionButton btnPrintOneBill;
	FunctionButton btnPrintOneInVoice;
	JToggleButton btnEqualBill;
	JToggleButton btnSplitItem;
	JToggleButton btnMoveItem;
	FunctionButton btnCombineAll;
	
	FunctionButton btnSuspendAll;

	FunctionButton btnReturn;
}
