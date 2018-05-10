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
		btnSplitItem = new JToggleButton(BarDlgConst.SplitItem);
		btnCombineAll = new JButton(BarDlgConst.CombineAll);
		
		btnCompleteAll = new JButton(BarDlgConst.CompleteAll);
		btnCancelAll = new JButton(BarDlgConst.CancelAll);
		btnReturn = new JButton(BarDlgConst.RETURN);
		
		separator= new JSeparator();
		
		btnAddUser.setMargin(new Insets(0, 0, 0, 0));
		btnPrintAll.setMargin(btnAddUser.getMargin());
		btnEqualBill.setMargin(btnAddUser.getMargin());
		btnCombineAll.setMargin(btnAddUser.getMargin());
		btnSplitItem.setMargin(btnAddUser.getMargin());
		btnCompleteAll.setMargin(btnAddUser.getMargin());
		btnCancelAll.setMargin(btnAddUser.getMargin());
		btnReturn.setMargin(btnAddUser.getMargin());
		
		setLayout(null);
		
		add(btnAddUser);
		add(separator);
		add(btnPrintAll);
		add(btnEqualBill);
		add(btnCombineAll);
		add(btnSplitItem);
		add(btnCompleteAll);
		add(btnCancelAll);
		add(btnReturn);
		
		addComponentListener(this);
		btnAddUser.addActionListener(this);
		btnPrintAll.addActionListener(this);
		btnEqualBill.addActionListener(this);
		btnCombineAll.addActionListener(this);
		btnSplitItem.addActionListener(this);
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
				
				BillPanel billPanel = new BillPanel(this, billButton);
				billPanels.add(billPanel);
				add(billPanel);
			}

			//do it outside the above loop, because there's another qb query inside.
			for(int i = 0; i < billPanels.size(); i++) {
				billPanels.get(i).initComponent();
				billPanels.get(i).initContent();
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
				CustOpts.BTN_WIDTH, tBtnHeight);
		btnPrintAll.setBounds(btnCancelAll.getX() + btnCancelAll.getWidth() + CustOpts.HOR_GAP, 
				btnCancelAll.getY(),
				CustOpts.BTN_WIDTH, tBtnHeight);
		
		btnEqualBill.setBounds(btnPrintAll.getX() + btnPrintAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, tBtnHeight);
		btnCombineAll.setBounds(btnEqualBill.getX() + btnEqualBill.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, tBtnHeight);
		btnSplitItem.setBounds(btnCombineAll.getX() + btnCombineAll.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, tBtnHeight);
		btnCompleteAll.setBounds(btnSplitItem.getX() + btnSplitItem.getWidth() + CustOpts.HOR_GAP, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, tBtnHeight);
		separator.setBounds(CustOpts.HOR_GAP, 
				btnCancelAll.getY() - CustOpts.VER_GAP * 2,
				getWidth() - CustOpts.HOR_GAP * 2, tBtnHeight);

		btnAddUser.setBounds(CustOpts.SIZE_EDGE, btnCancelAll.getY(), CustOpts.BTN_WIDTH, tBtnHeight);
		btnReturn.setBounds(getWidth() - CustOpts.SIZE_EDGE*2 - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP*2, 
				btnPrintAll.getY(),
				CustOpts.BTN_WIDTH, tBtnHeight);
		
		for (int i = 0; i < onScrBills.size(); i++) {
			int x = col < 4 ? 	//there move than 4 bills. put from left to right
					(getWidth()  - (TBN_WIDTH + CustOpts.HOR_GAP)* col) / 2 + ((TBN_WIDTH + CustOpts.HOR_GAP)) * i
					: CustOpts.SIZE_EDGE + CustOpts.HOR_GAP + (CustOpts.HOR_GAP + TBN_WIDTH) * i;
			
			onScrBills.get(i).setBounds(x, CustOpts.VER_GAP,
					TBN_WIDTH, separator.getY() - CustOpts.VER_GAP * 2);
			onScrBills.get(i).resetColWidth(TBN_WIDTH);
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
						Dish.split(panel.selectdDishAry, num, BillListPanel.getANewBillNumber());
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
		}else {
			if(o == btnCancelAll) {
				//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
				//set the table as unselected.
			}else if(o == btnPrintAll) {
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
			}else if(o == btnAddUser){
				BarFrame.instance.lblCurBill.setText("0");
				BarFrame.instance.switchMode(2);
			}else if(o == btnReturn) {
				BarFrame.instance.lblCurBill.setText("0");
				BarFrame.instance.switchMode(0);
			}

			//select all output of each bill wich curtable and status is not completed, and set the status to be cancelled.
			//set the table as unselected.
		}
	}
	
    public static String getANewBillNumber(){
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
	
    private void openMoneyBox() {
        int[] ccs = new int[5];
        ccs[0] = 27;
        ccs[1] = 112;
        ccs[2] = 0;
        ccs[3] = 80;
        ccs[4] = 250;

        CommPortIdentifier tPortIdty;
        try {
            Enumeration tPorts = CommPortIdentifier.getPortIdentifiers();
            if (tPorts == null)
                JOptionPane.showMessageDialog(this, "no comm ports found!");
            else
                while (tPorts.hasMoreElements()) {
                    tPortIdty = (CommPortIdentifier) tPorts.nextElement();
                    if (tPortIdty.getName().equals("LPT1")) {
                        if (!tPortIdty.isCurrentlyOwned()) {
                            ParallelPort tParallelPort = (ParallelPort) tPortIdty.open("ParallelBlackBox", 2000);
                            DataOutputStream tOutStream = new DataOutputStream(tParallelPort.getOutputStream());
                            for (int i = 0; i < 5; i++)
                                tOutStream.write(ccs[i]);
                            tOutStream.flush();
                            tOutStream.close();
                            tParallelPort.close();
                        }
                    }
                }
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printInvoice(
            String pDate) {
    }
    
	List<BillPanel> billPanels;
	List<BillPanel> onScrBills;
	
	JButton btnAddUser;
	JSeparator separator;
	JButton btnPrintAll;
	JToggleButton btnEqualBill;
	JToggleButton btnSplitItem;
	JButton btnCombineAll;
	
	JButton btnCompleteAll;
	JButton btnCancelAll;

	JButton btnReturn;
}
