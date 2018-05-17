package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.beans.ArrayButton;
import org.cas.client.platform.bar.beans.TableButton;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.jfree.chart.labels.CustomXYToolTipGenerator;

import jpos.profile.IntegerPropType;

public class BillListPanel extends  JPanel  implements ActionListener, ComponentListener{
	static Dish curDish;
	int curPageNum;
	
	public BillListPanel() {
		
		billPanels= new ArrayList<BillPanel>();
		onScrBills= new ArrayList<BillPanel>();

	    btnLeft = new ArrayButton("<<");
	    btnRight = new ArrayButton(">>");
		btnAddUser = new JButton(BarDlgConst.AddUser);
		btnPrintAll = new JButton(BarDlgConst.PrintAll);

		btnEqualBill = new JToggleButton(BarDlgConst.EqualBill);
		btnSplitItem = new JToggleButton(BarDlgConst.SplitItem);
		btnCombineAll = new JButton(BarDlgConst.CombineAll);
		
		btnCompleteAll = new JButton(BarDlgConst.CompleteAll);
		btnReturn = new JButton(BarDlgConst.RETURN);
		
		separator= new JSeparator();

		btnLeft.setMargin(new Insets(0, 0, 0, 0));
		btnRight.setMargin(new Insets(0, 0, 0, 0));
		btnAddUser.setMargin(new Insets(0, 0, 0, 0));
		btnPrintAll.setMargin(btnAddUser.getMargin());
		btnEqualBill.setMargin(btnAddUser.getMargin());
		btnCombineAll.setMargin(btnAddUser.getMargin());
		btnSplitItem.setMargin(btnAddUser.getMargin());
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
		btnCompleteAll.addActionListener(this);
		btnReturn.addActionListener(this);
		
		btnLeft.setEnabled(curPageNum > 0);
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
					+ "' and deleted = false and time = '" + BarFrame.instance.valStartTime.getText() + "' order by contactID");
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
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 8) / 7;
        int tBtnHeight = panelHeight / 10;

		btnAddUser.setBounds(CustOpts.SIZE_EDGE, panelHeight - tBtnHeight - CustOpts.VER_GAP, tBtnWidht, tBtnHeight);
		
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
		btnCompleteAll.setBounds(btnSplitItem.getX() + btnSplitItem.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				tBtnWidht, tBtnHeight);
		btnReturn.setBounds(btnCompleteAll.getX() + btnCompleteAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
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
	
	void moveDishToBill(BillPanel billPanel) {
		// Update the output to belongs to the new ContactID
		String sql = "update output set CONTACTID = " + billPanel.billButton.getText() + " where id = "
				+ curDish.getOutputID();
		Statement smt = PIMDBModel.getStatement();
		try {
			smt.execute(sql);
		}catch(Exception exp) {
			ErrorUtil.write(exp);
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
			if(o == btnEqualBill) {
				BillPanel panel = getCurBillPanel();
				if(panel == null) {
					JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.OnlyOneShouldBeSelected);
					btnEqualBill.setSelected(false);
					return;
				}
				BarFrame.numberPanelDlg.setBtnSource(btnEqualBill);
				BarFrame.numberPanelDlg.setModal(true);
				BarFrame.numberPanelDlg.setVisible(btnEqualBill.isSelected());
				if(NumberPanelDlg.confirmed) {
					int num = Integer.valueOf(NumberPanelDlg.curContent);
					if(num < 2) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.InvalidInput);
						return;
					}
					//splet into num bills. each dish's number and price will be devide by "num".
					Dish.split(panel.selectdDishAry, num, null);//update existing outputs
					for (int i = 1; i < num; i++) {				//generate splited ones.
						Dish.split(panel.selectdDishAry, num, String.valueOf(BillListPanel.getANewBillNumber()));
					}
				}
				initContent();
			}else if(o == btnSplitItem) {
				if(btnSplitItem.isSelected()) {//select
					//Todo:check if there's one item selected.
					if(curDish == null) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.OnlyOneShouldBeSelected);
						btnSplitItem.setSelected(false);
					}
				}else {
				
					// unselect: if here reached, there must be curDish.
					// remove the bill where the curDish is. @because sometimes that bill might be
					// unselected.
					List<BillPanel> panels = getSelectedBillPannels();
					for (BillPanel billPanel : panels) { // remove the original panel from the list.
						if (billPanel.billButton.getText().equals(curDish.getBillID())) {
							panels.remove(billPanel);
							break;
						}
					}
					if(panels.size() == 0) {
						JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.NoBillSeleted);
						return;
					}
					Dish.split(curDish, panels.size() + 1, null); // update the num and totalprice of curDish
					for (BillPanel billPanel : panels) { // insert new output with other billID
						Dish.split(curDish, panels.size() + 1, billPanel.billButton.getText());
					}

					curDish = null;
					initContent();
				}
			}
		}else if(o instanceof ArrayButton){
			if(o == btnLeft) {
				curPageNum--;
			}else if(o == btnRight) {
				curPageNum++;
			}
			btnLeft.setEnabled(curPageNum > 0);
			initContent();
		}else {
			if(o == btnAddUser){
				BarFrame.instance.valCurBill.setText("0");
				BarFrame.instance.switchMode(2);
			}else if(o == btnPrintAll) {
				//TODO: add print receipt code.
			}else if(o == btnCombineAll) {//@note should consider the time, incase there'ss some bill not paid before, while was calculated into current client.
		        String sql =
		                "update output set contactID = 1 where SUBJECT = '" + BarFrame.instance.valCurTable.getText()
		                + "' and time > '" + BarFrame.instance.valStartTime.getText() + "' and DELETED != true";
		        try {
		        	PIMDBModel.getStatement().execute(sql);
		        }catch(Exception exp) {
		        	ErrorUtil.write(exp);
		        }
				
		        initContent();
			}else if( o == btnCompleteAll) {
		        try {
		        	Statement smt = PIMDBModel.getStatement();
		        	String tableID = BarFrame.instance.valCurTable.getText();
					String sql =
			                "update output set deleted = 100 where SUBJECT = '" + tableID
			                + "' and time > '" + BarFrame.instance.valStartTime.getText() + "' and DELETED != true";
					smt.execute(sql);
		        	//update the tabel status
		        	smt.execute("update dining_Table set status = 0 WHERE name = '" + tableID + "'");
		        }catch(Exception exp) {
		        	ErrorUtil.write(exp);
		        }
		        
				BarFrame.instance.valCurBill.setText("0");
				BarFrame.instance.switchMode(0);
				
			}else if(o == btnReturn) {
				BarFrame.instance.valCurBill.setText("0");
				BarFrame.instance.switchMode(0);
			}

			//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
			//set the table as unselected.
		}
	}
	
    public static int getANewBillNumber(){
    	int num = 0;
    	try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
            ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '"
                    + BarFrame.instance.valCurTable.getText() + "' and deleted = false order by contactID");
			rs.afterLast();
			rs.relative(-1);
			num = rs.getInt("contactID");
		} catch (Exception exp) {
			System.out.println("lagest num is 0.");
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
		for (BillPanel billPanel : billPanels) {
			if(billPanel.billButton.isSelected())
				panels.add(billPanel);
		}
		return panels;
	}
	
	List<BillPanel> billPanels;
	List<BillPanel> onScrBills;

    private ArrayButton btnLeft;
    private ArrayButton btnRight;
	JButton btnAddUser;
	JSeparator separator;
	JButton btnPrintAll;
	JToggleButton btnEqualBill;
	JToggleButton btnSplitItem;
	JButton btnCombineAll;
	
	JButton btnCompleteAll;

	JButton btnReturn;
}
