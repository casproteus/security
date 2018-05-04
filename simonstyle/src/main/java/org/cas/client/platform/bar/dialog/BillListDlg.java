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

import org.cas.client.platform.bar.beans.TableButton;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class BillListDlg extends JDialog implements ActionListener, ComponentListener{
	TableButton tbnTable;
	public BillListDlg(TableButton tbnTable, String tableID) {
		
		super(BarFrame.instance, tableID);
		this.tbnTable = tbnTable;
		
		btns = new ArrayList<>();
		btnAddUser = new JButton(BarDlgConst.AddUser);
		btnPrintAll = new JButton(BarDlgConst.PrintAll);
		btnCompleteAll = new JButton(BarDlgConst.CompleteAll);
		btnCancelAll = new JButton(BarDlgConst.CancelAll);
		separator= new JSeparator();
		
		btnAddUser.setMargin(new Insets(0, 0, 0, 0));
		btnPrintAll.setMargin(new Insets(0, 0, 0, 0));
		btnCompleteAll.setMargin(new Insets(0, 0, 0, 0));
		btnCancelAll.setMargin(new Insets(0, 0, 0, 0));
		
		setLayout(null);
		
		add(btnAddUser);
		add(separator);
		add(btnPrintAll);
		add(btnCompleteAll);
		add(btnCancelAll);
		
		btnAddUser.addActionListener(this);
		btnPrintAll.addActionListener(this);
		btnCompleteAll.addActionListener(this);
		btnCancelAll.addActionListener(this);

		initContent(tableID);
		reLayout();
	}
	
	private void initContent(String tableID) {
		// load all the unclosed outputs under this table with ---------------------------
    	try {
	        Statement smt = PIMDBModel.getReadOnlyStatement();
            ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '"
                    + tableID + "' and deleted = false order by contactID");
            rs.beforeFirst();

             while (rs.next()) {
             	JButton tableToggleButton = new JButton();
             	tableToggleButton.setText(String.valueOf(rs.getInt("contactID")));
             	tableToggleButton.setMargin(new Insets(0, 0, 0, 0));
     			tableToggleButton.addActionListener(this);
     			btns.add(tableToggleButton);
     			
     			add(tableToggleButton);
             }
             btns.add(btnAddUser);
             rs.close();// 关闭
             smt.close();
 		}catch(Exception e) {
 			ErrorUtil.write("Unexpected exception when init the tables from db." + e);
 		}
	}
	
	private void reLayout() {
		int col = btns.size() + 1;	//calculate together with the new button.
		col = col > 5 ? 5 : 1;
		int row = (btns.size() + 1) / 5 + 1;
		row = (row - 1) * 5 == btns.size() + 1 ? row - 1 : row;
		row = col == 1 ? btns.size() + 1 : row;	//if less than 5 button, then one button/row
				
		int width = col == 1 ?  
				(CustOpts.BTN_WIDTH + CustOpts.HOR_GAP) * 3 + CustOpts.SIZE_EDGE * 2 + CustOpts.HOR_GAP * 3
				: (CustOpts.BTN_WIDTH + CustOpts.HOR_GAP) * col + CustOpts.HOR_GAP * 2 + CustOpts.SIZE_EDGE * 2;
		int height = (CustOpts.BAR_HEIGHT + CustOpts.VER_GAP) * (row + 1) + CustOpts.SIZE_EDGE * 2 +  CustOpts.VER_GAP * 3;
		setBounds(tbnTable.getX(), tbnTable.getY(), width , height);
		
		btnCancelAll.setBounds(width / 2 - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP * 2 - CustOpts.BTN_WIDTH/2, 
				getHeight() - CustOpts.SIZE_EDGE * 3 - CustOpts.VER_GAP * 3 - CustOpts.BTN_HEIGHT * 2,
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnPrintAll.setBounds(btnCancelAll.getX() + btnCancelAll.getWidth() + CustOpts.HOR_GAP, 
				btnCancelAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnCompleteAll.setBounds(btnPrintAll.getX() + btnPrintAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		separator.setBounds(CustOpts.HOR_GAP, 
				btnCancelAll.getY() - CustOpts.VER_GAP * 2,
				width - CustOpts.HOR_GAP * 2, CustOpts.BTN_HEIGHT);
		
		int i = 0;
		for (; i < btns.size(); i++) {
			if(col == 1) {
				btns.get(i).setBounds(width / 2 - CustOpts.BTN_WIDTH /2,
					(CustOpts.VER_GAP + CustOpts.BTN_HEIGHT) * i + CustOpts.VER_GAP,
					CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
			}else {
				btns.get(i).setBounds((CustOpts.HOR_GAP + CustOpts.BTN_WIDTH) * i + CustOpts.HOR_GAP,
						(CustOpts.VER_GAP + CustOpts.BTN_HEIGHT) * (i / 5) + CustOpts.VER_GAP,
						CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
			}
		}
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
		JButton o = (JButton)e.getSource();
		if(o == btnCancelAll) {
			//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
			//set the table as unselected.
		}else if(o == btnPrintAll) {

			//set the table as unselected.
		}else if( o == btnCompleteAll) {
			//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
			//set the table as unselected.
		}else if(o == btnAddUser){
			BarFrame.instance.lblCurBill.setText("0");
			BarFrame.instance.switchMode(1);
		}else {
    		BarFrame.instance.lblCurBill.setText(o.getText());
            BarFrame.instance.switchMode(1);
		}

		this.setVisible(false);
	}
	
	List<JButton> btns;
	JButton btnAddUser;
	JSeparator separator;
	JButton btnPrintAll;
	JButton btnCompleteAll;
	JButton btnCancelAll;
}
