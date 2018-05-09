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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

public class BillListPanel extends  JPanel  implements ActionListener, ComponentListener{
	int TBN_WIDTH = 300;
	Dish curDish;
	
	public BillListPanel() {
		
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
		btnReturn = new JButton(BarDlgConst.RETURN);
		
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
		btnReturn.setMargin(btnAddUser.getMargin());
		
		setLayout(null);
		
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
		add(btnReturn);
		
		addComponentListener(this);
		btnAddUser.addActionListener(this);
		btnPrintAll.addActionListener(this);
		btnEqualBill.addActionListener(this);
		btnEqualItem.addActionListener(this);
		btnSplitItem.addActionListener(this);
		btnMoveItem.addActionListener(this);
		btnCombineAll.addActionListener(this);
		btnCompleteAll.addActionListener(this);
		btnCancelAll.addActionListener(this);
		btnReturn.addActionListener(this);
	}
	
	void initContent() {
		for(int i = billPanels.size() - 1; i >= 0; i--) {
			remove(billPanels.get(i));
		}
		billPanels.clear();
		onScrBills.clear();
		
		// load all the unclosed outputs under this table with ---------------------------
		try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
			ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '" + BarFrame.instance.valCurTable.getText()
					+ "' and deleted = false order by contactID");
			rs.beforeFirst();

			while (rs.next()) {
				JToggleButton billButton = new JToggleButton();
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

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 10) / 9;
        int tBtnHeight = panelHeight / 10;
        
		int col = billPanels.size();	//calculate together with the new button.
		col = col > 4 ? 4 : col;		//I think the screen is enought for only 4 column.
		btnCancelAll.setBounds(getWidth() / 2 - (CustOpts.BTN_WIDTH + CustOpts.HOR_GAP) * 4 + 40 , 
				panelHeight - tBtnHeight - CustOpts.VER_GAP,
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
		
		btnCompleteAll.setBounds(btnCombineAll.getX() + btnCombineAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		separator.setBounds(CustOpts.HOR_GAP, 
				btnCancelAll.getY() - CustOpts.VER_GAP * 2,
				getWidth() - CustOpts.HOR_GAP * 2, CustOpts.BTN_WIDTH_NUM);

		btnAddUser.setBounds(CustOpts.SIZE_EDGE, btnCancelAll.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		btnReturn.setBounds(getWidth() - CustOpts.SIZE_EDGE*2 - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP*2, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_WIDTH_NUM);
		
		for (int i = 0; i < onScrBills.size(); i++) {
			int x = col < 4 ? 	//there move than 4 bills. put from left to right
					(getWidth()  - (TBN_WIDTH + CustOpts.HOR_GAP)* col) / 2 + ((TBN_WIDTH + CustOpts.HOR_GAP)) * i
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
		initContent();
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
				BillPanel panel = getCurBillPanel();
				if(panel == null) {
					JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.OnlyOneShouldBeSelected);
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
					//TODO:splet into num bills. each dish's number and price will be devide by "num".
					//update existing outputs
					for(int i = 0; i < panel.selectdDishAry.size(); i++) {
						Dish dish = panel.selectdDishAry.get(i);
						Dish.split(dish, num);
					}
				}
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
				BarFrame.instance.switchMode(2);
			}else if(o == btnReturn) {
				BarFrame.instance.lblCurBill.setText("0");
				BarFrame.instance.switchMode(0);
			}else {		//when table buttons are clicked.
	    		BarFrame.instance.lblCurBill.setText(((JButton)o).getText());
	            BarFrame.instance.switchMode(2);
			}
		}
	}
	
    static String getANewBillNumber(){
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
    	return String.valueOf(num + 1);
    }

	BillPanel getCurBillPanel(){
		List<BillPanel> panels = getSelectedBillPannels();
		return panels.size() == 1 ? panels.get(0) : null;
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

	JButton btnReturn;
}
