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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSeparator;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class BillListDlg extends JDialog implements ActionListener, ComponentListener{

	public BillListDlg(String tableID) {
		
		super(BarFrame.instance, tableID);
		
		btns = new ArrayList<>();
		btnNew = new JButton();
		btnPrintAll = new JButton();
		btnCompleteAll = new JButton();
		btnCancelAll = new JButton();
		separator= new JSeparator();
		
		setLayout(null);
		
		add(btnNew);
		add(separator);
		add(btnPrintAll);
		add(btnCompleteAll);
		add(btnCancelAll);
		
		btnNew.addActionListener(this);
		btnPrintAll.addActionListener(this);
		btnCompleteAll.addActionListener(this);
		btnCancelAll.addActionListener(this);

		initContent(tableID);
		reLayout();
	}
	
	private void initContent(String tableID) {
		// load all the unclosed outputs under this table with ---------------------------
    	try {
        	Statement stmt = PIMDBModel.getConection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '"
                    + tableID + "' and deleted = false order by contactID");
            rs.beforeFirst();

             while (rs.next()) {
             	JButton tableToggleButton = new JButton();
             	tableToggleButton.setText(String.valueOf(rs.getInt("contactID")));
             	tableToggleButton.setBounds(0, 0, 0, 0);
             	tableToggleButton.setMargin(new Insets(0, 0, 0, 0));
     			tableToggleButton.addActionListener(this);
     			btns.add(tableToggleButton);
             }
             
             rs.close();// 关闭
             stmt.close();
 		}catch(Exception e) {
 			ErrorUtil.write("Unexpected exception when init the tables from db." + e);
 		}
	}
	
	private void reLayout() {
		int col = btns.size() + 1;	//calculate together with the new button.
		col = col > 5 ? 5 : col;
		int row = (btns.size() + 1) / 5 + 1;
		row = (row - 1) * 5 == btns.size() + 1 ? row - 1 : row;
				
		int width = (CustOpts.BTN_WIDTH + CustOpts.HOR_GAP) * col + CustOpts.HOR_GAP + CustOpts.SIZE_EDGE * 2;
		int height = (CustOpts.BAR_HEIGHT + CustOpts.VER_GAP) * (row + 2) + CustOpts.SIZE_EDGE * 2 +  CustOpts.VER_GAP;
		setSize(width, height);
		
		btnCancelAll.setBounds(width / 2 - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, 
				height - CustOpts.SIZE_EDGE - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT,
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnPrintAll.setBounds(btnCancelAll.getX() + btnCancelAll.getWidth() + CustOpts.HOR_GAP, 
				btnCancelAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnCompleteAll.setBounds(btnPrintAll.getX() + btnPrintAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		separator.setBounds(CustOpts.HOR_GAP, 
				btnCancelAll.getY() - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP,
				width - CustOpts.HOR_GAP * 2, CustOpts.BTN_HEIGHT);
		
		int i = 0;
		for (; i < btns.size(); i++) {
			btns.get(i).setBounds((CustOpts.HOR_GAP + CustOpts.BTN_WIDTH) * i + CustOpts.HOR_GAP,
					(CustOpts.VER_GAP + CustOpts.BTN_HEIGHT) * (i / 5), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		}
		
		btnNew.setBounds((CustOpts.HOR_GAP + CustOpts.BTN_WIDTH) * i + CustOpts.HOR_GAP,
				(CustOpts.VER_GAP + CustOpts.BTN_HEIGHT) * (i / 5), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
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
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	List<JButton> btns;
	JButton btnNew;
	JSeparator separator;
	JButton btnPrintAll;
	JButton btnCompleteAll;
	JButton btnCancelAll;
}
