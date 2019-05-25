package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;
import org.cas.client.platform.bar.dialog.NumberPanelDlg;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.bar.uibeans.FunctionToggleButton;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class Cmd_MoveItem implements SamActionListener {

	private static Cmd_MoveItem instance;
	private Cmd_MoveItem() {}
	public static Cmd_MoveItem getInstance() {
		if(instance == null)
			instance = new Cmd_MoveItem();
		return instance;
	}
	
	private ISButton sourceBtn;
	
	public ISButton getSourceBtn() {
		return sourceBtn;
	}
	@Override
	public void setSourceBtn(ISButton sourceBtn) {
		this.sourceBtn = sourceBtn;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.sourceBtn = (FunctionToggleButton)e.getSource();
		BillListPanel billListPanel = (BillListPanel)BarFrame.instance.panels[1];
		
		if(billListPanel.curDish == null) {
			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
			sourceBtn.setSelected(false);
			return;
		}
		BillPanel panel = billListPanel.getCurBillPanel();
		if(panel == null) {
			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.OnlyOneShouldBeSelected());
			sourceBtn.setSelected(false);
			return;
		}
		if(!panel.checkStatus())	//create a new bill for original bill
			return;
		moveItemAction(sourceBtn);
	}
	
	private void moveItemAction(ISButton button) {
		BarFrame.numberPanelDlg.setTitle(BarFrame.consts.BILL());
		BarFrame.numberPanelDlg.setBtnSource(button);
		BarFrame.numberPanelDlg.setFloatSupport(false);
		BarFrame.numberPanelDlg.setPercentSupport(false);
		BarFrame.numberPanelDlg.setModal(true);
		BarFrame.numberPanelDlg.reLayout();
		BarFrame.numberPanelDlg.setNotice(BarFrame.consts.QTYNOTICE());
		BarFrame.numberPanelDlg.setVisible(button.isSelected());
		if(NumberPanelDlg.confirmed) {
			BillListPanel billListPanel = (BillListPanel)BarFrame.instance.panels[1];
			int targetBillIdx = Integer.valueOf(NumberPanelDlg.curContent);
			if(targetBillIdx < 1) {
				JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
			}else {
				//check if the original panel need to be regenerate
	        	BillPanel originalBillPanel = billListPanel.getBillPanelByBillNumber(Integer.valueOf(billListPanel.curDish.getBillIndex()));
	        	int origianlBillstatus = originalBillPanel.status;	//save the status before it's changed when regenerate the bill.
	        	if(origianlBillstatus >= DBConsts.billPrinted || origianlBillstatus < DBConsts.original) {
	        		originalBillPanel.reGenerate(billListPanel.getBillPanelByBillNumber(targetBillIdx).billButton.getText());
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
	        				if (JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.ConvertClosedBillBack(), BarFrame.consts.Operator(),
	        			            JOptionPane.YES_NO_OPTION) != 0) {// are you sure to convert the voided bill back？
	        			        return;
	        				}else {
	        					billListPanel.getBillPanelByBillNumber(targetBillIdx).reGenerate(billListPanel.getBillPanelByBillNumber(targetBillIdx).billButton.getText());
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
						.append(" where id = ").append(billListPanel.curDish.getOutputID());
		        try {
		        	PIMDBModel.getStatement().executeUpdate(sql.toString());
		        }catch(Exception exp) {
		        	ErrorUtil.write(exp);
		        }
		        BillListPanel.curDish = null;
		        billListPanel.initContent();
		        //find the two bill, and update the total price in db.
//TODO: this is not the right way to find out the two relevant panel, since it's a little complex, let's leave to next version	
//		        for (BillPanel billPanel : billPanels) {
//			        BarUtil.updateBillRecordPrices(billPanel);
//				}
			}
		}
	}
}
