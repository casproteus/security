package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.beans.TableButton;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.jfree.chart.labels.CustomXYToolTipGenerator;

public class BillListDlg extends JDialog implements ActionListener, ComponentListener{
	TableButton tbnTable;
	int TBN_WIDTH = 300;
	Dish curDish;
	JButton curBillButton;
	
	public BillListDlg(TableButton tbnTable, String tableID) {
		
		super(BarFrame.instance, tableID);
		this.tbnTable = tbnTable;
		
		billPanels= new ArrayList<BillPanel>();
		onScrBills= new ArrayList<BillPanel>();
		
		btnAddUser = new JButton(BarDlgConst.AddUser);
		btnPrintAll = new JButton(BarDlgConst.PrintAll);

		btnEqualBill = new JToggleButton(BarDlgConst.EqualBill);
		btnEqualItem = new JButton(BarDlgConst.EqualItem);
		btnSplitItem = new JButton(BarDlgConst.SplitItem);
		btnMoveItem = new JButton(BarDlgConst.MoveItem);
		btnCombineAll = new JButton(BarDlgConst.CombineAll);
		
		btnCompleteAll = new JButton(BarDlgConst.CompleteAll);
		btnCancelAll = new JButton(BarDlgConst.CancelAll);
		separator= new JSeparator();
		
		btnAddUser.setMargin(new Insets(0, 0, 0, 0));
		btnPrintAll.setMargin(btnAddUser.getMargin());
		btnEqualBill.setMargin(btnAddUser.getMargin());
		btnEqualItem.setMargin(btnAddUser.getMargin());
		btnSplitItem.setMargin(btnAddUser.getMargin());
		btnMoveItem.setMargin(btnAddUser.getMargin());
		btnCombineAll.setMargin(btnAddUser.getMargin());
		btnCompleteAll.setMargin(btnAddUser.getMargin());
		btnCancelAll.setMargin(btnAddUser.getMargin());
		
		setModal(true);
		setLayout(null);
		setResizable(false);
		
		add(btnAddUser);
		add(separator);
		add(btnPrintAll);
		add(btnEqualBill);
		add(btnEqualItem);
		add(btnSplitItem);
		add(btnMoveItem);
		add(btnCombineAll);
		add(btnCompleteAll);
		add(btnCancelAll);
		
		btnAddUser.addActionListener(this);
		btnPrintAll.addActionListener(this);
		btnEqualBill.addActionListener(this);
		btnEqualItem.addActionListener(this);
		btnSplitItem.addActionListener(this);
		btnMoveItem.addActionListener(this);
		btnCombineAll.addActionListener(this);
		btnCompleteAll.addActionListener(this);
		btnCancelAll.addActionListener(this);

		initContent(tableID);
	}
	
	private void initContent(String tableID) {
		for(int i = billPanels.size() - 1; i >= 0; i--) {
			remove(billPanels.get(i));
		}
		billPanels.clear();
		onScrBills.clear();
		
		// load all the unclosed outputs under this table with ---------------------------
		try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
			ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '" + tableID
					+ "' and deleted = false order by contactID");
			rs.beforeFirst();

			while (rs.next()) {
				JButton billButton = new JButton();
				billButton.setText(String.valueOf(rs.getInt("contactID")));
				billButton.setMargin(new Insets(0, 0, 0, 0));
				billButton.addActionListener(this);
				BillPanel billPanel = new BillPanel(this, billButton);
				billPanels.add(billPanel);
				add(billPanel);
			}

			//do it outside the above loop, because there's another qb query inside.
			for(int i = 0; i < billPanels.size(); i++) {
				billPanels.get(i).initComponent();
				billPanels.get(i).initTable();
				if(i < 4)
					onScrBills.add(billPanels.get(i));
			}
		} catch (Exception e) {
 			ErrorUtil.write("Unexpected exception when init the tables from db." + e);
 		}
		reLayout();
	}
	
	private void reLayout() {
		int col = billPanels.size();	//calculate together with the new button.
		col = col > 4 ? 4 : col;		//I think the screen is enought for only 4 column.
		
		int width = col < 3 ?  (CustOpts.BTN_WIDTH + CustOpts.HOR_GAP) * 9 + CustOpts.SIZE_EDGE * 2 + CustOpts.HOR_GAP * 8 
				: (TBN_WIDTH + CustOpts.HOR_GAP) * col + CustOpts.SIZE_EDGE * 2 + CustOpts.HOR_GAP *2;
		
		int height = BarFrame.instance.getHeight()*2/3;
		
		setBounds((BarFrame.instance.getWidth() - width) / 2, (BarFrame.instance.getHeight() - height)/2, width , height);
		
		btnCancelAll.setBounds(width / 2 - (CustOpts.BTN_WIDTH + CustOpts.HOR_GAP) * 4 + 40 , 
				height - CustOpts.SIZE_EDGE - CustOpts.VER_GAP - CustOpts.BTN_WIDTH_NUM - 40,
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		btnPrintAll.setBounds(btnCancelAll.getX() + btnCancelAll.getWidth() + CustOpts.HOR_GAP, 
				btnCancelAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		
		btnEqualBill.setBounds(btnPrintAll.getX() + btnPrintAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		btnEqualItem.setBounds(btnEqualBill.getX() + btnEqualBill.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		btnSplitItem.setBounds(btnEqualItem.getX() + btnEqualItem.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		btnMoveItem.setBounds(btnSplitItem.getX() + btnSplitItem.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		btnCombineAll.setBounds(btnMoveItem.getX() + btnMoveItem.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		
		btnCompleteAll.setBounds(width - CustOpts.SIZE_EDGE*2 - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP*2, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		separator.setBounds(CustOpts.HOR_GAP, 
				btnCancelAll.getY() - CustOpts.VER_GAP * 2,
				width - CustOpts.HOR_GAP * 2, CustOpts.BTN_WIDTH_NUM);

		btnAddUser.setBounds(CustOpts.SIZE_EDGE, btnCancelAll.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		
		for (int i = 0; i < onScrBills.size(); i++) {
			int x = col < 4 ? 	//there move than 4 bills. put from left to right
					(width  - (TBN_WIDTH + CustOpts.HOR_GAP)* col) / 2 + ((TBN_WIDTH + CustOpts.HOR_GAP)) * i
					:(CustOpts.HOR_GAP + CustOpts.BTN_WIDTH) * i + CustOpts.HOR_GAP + CustOpts.SIZE_EDGE;
			
			onScrBills.get(i).setBounds(x, CustOpts.VER_GAP,
					TBN_WIDTH, separator.getY() - CustOpts.VER_GAP * 2);
			onScrBills.get(i).resetColWidth(TBN_WIDTH - 40);
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
		initContent(BarFrame.instance.valCurTable.getText());
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		reLayout();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {	//@NOTE: the bill button could trigger two times of event.
		Object o = e.getSource();
		if(o instanceof JToggleButton) {
			if(o == btnEqualBill) {
				this.setModal(false);
				BarFrame.numberPanelDlg.setBtnSource(btnEqualBill);
				BarFrame.numberPanelDlg.setVisible(btnEqualBill.isSelected());
				BarFrame.numberPanelDlg.setModal(true);
			}else if(o == btnEqualItem) {
				
			}else if(o == btnSplitItem) {
				
			}else if(o == btnMoveItem) {
				
			}
		}else {
			if(o == btnCancelAll) {
				//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
				//set the table as unselected.
			}else if(o == btnPrintAll) {
			}else if(o == btnCombineAll) {
				//set the table as unselected.
			}else if( o == btnCompleteAll) {
				//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
				//set the table as unselected.
			}else if(o == btnAddUser){
				BarFrame.instance.lblCurBill.setText("0");
				BarFrame.instance.switchMode(1);
			}else {		//when table buttons are clicked.
	    		BarFrame.instance.lblCurBill.setText(((JButton)o).getText());
	            BarFrame.instance.switchMode(1);
			}
			this.setVisible(false);
		}
	}

	List<BillPanel> billPanels;
	List<BillPanel> onScrBills;
	
	JButton btnAddUser;
	JSeparator separator;
	JButton btnPrintAll;
	JToggleButton btnEqualBill;
	JButton btnEqualItem;
	JButton btnSplitItem;
	JButton btnMoveItem;
	JButton btnCombineAll;
	
	JButton btnCompleteAll;
	JButton btnCancelAll;
}
