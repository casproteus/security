package org.cas.client.platform.bar.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.action.Cmd_AddBill;
import org.cas.client.platform.bar.action.Cmd_AddTable;
import org.cas.client.platform.bar.action.Cmd_AddUser;
import org.cas.client.platform.bar.action.Cmd_BillFoot;
import org.cas.client.platform.bar.action.Cmd_CancelAll;
import org.cas.client.platform.bar.action.Cmd_ChangePrice;
import org.cas.client.platform.bar.action.Cmd_CheckInOut;
import org.cas.client.platform.bar.action.Cmd_CheckInOutList;
import org.cas.client.platform.bar.action.Cmd_CheckOrder;
import org.cas.client.platform.bar.action.Cmd_Color;
import org.cas.client.platform.bar.action.Cmd_CombineAll;
import org.cas.client.platform.bar.action.Cmd_SettingCoupon;
import org.cas.client.platform.bar.action.Cmd_DiscBill;
import org.cas.client.platform.bar.action.Cmd_DiscItem;
import org.cas.client.platform.bar.action.Cmd_DiscountCoupon;
import org.cas.client.platform.bar.action.Cmd_Employee;
import org.cas.client.platform.bar.action.Cmd_EqualBill;
import org.cas.client.platform.bar.action.Cmd_GiftCard;
import org.cas.client.platform.bar.action.Cmd_Lang;
import org.cas.client.platform.bar.action.Cmd_Modify;
import org.cas.client.platform.bar.action.Cmd_ModifySetting;
import org.cas.client.platform.bar.action.Cmd_MoveItem;
import org.cas.client.platform.bar.action.Cmd_OpenDrawer;
import org.cas.client.platform.bar.action.Cmd_OrderManage;
import org.cas.client.platform.bar.action.Cmd_Other;
import org.cas.client.platform.bar.action.Cmd_Pay;
import org.cas.client.platform.bar.action.Cmd_PrintAll;
import org.cas.client.platform.bar.action.Cmd_PrintBill;
import org.cas.client.platform.bar.action.Cmd_PrintOneBill;
import org.cas.client.platform.bar.action.Cmd_PrintOneInVoice;
import org.cas.client.platform.bar.action.Cmd_Printer;
import org.cas.client.platform.bar.action.Cmd_Refund;
import org.cas.client.platform.bar.action.Cmd_RemoveItem;
import org.cas.client.platform.bar.action.Cmd_Report;
import org.cas.client.platform.bar.action.Cmd_Return;
import org.cas.client.platform.bar.action.Cmd_Send;
import org.cas.client.platform.bar.action.Cmd_ServiceFee;
import org.cas.client.platform.bar.action.Cmd_Setting;
import org.cas.client.platform.bar.action.Cmd_SlpitBill;
import org.cas.client.platform.bar.action.Cmd_SplitItem;
import org.cas.client.platform.bar.action.Cmd_Suspend;
import org.cas.client.platform.bar.action.Cmd_SuspendAll;
import org.cas.client.platform.bar.action.Cmd_Table;
import org.cas.client.platform.bar.action.Cmd_VoidOrder;
import org.cas.client.platform.bar.uibeans.ArrowButton;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.bar.uibeans.FunctionToggleButton;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.L;
import org.cas.client.resource.international.DlgConst;
import org.hsqldb.lib.HashMap;

public class CmdBtnsDlg extends JDialog implements ComponentListener, ActionListener, MouseListener{
   
	private static final String KEY_TABLE_CMD = "TableCmd";
	private static final String KEY_BILL_CMD = "BillCmd";
	private static final String KEY_SALE_CMD = "SaleCmd";
	private static final String KEY_SETTING_CMD = "SettingCmd";
	
	public static ArrayList[] groupedButtons = {new ArrayList<JComponent>(), new ArrayList<JComponent>(), new ArrayList<JComponent>(),new ArrayList<JComponent>()};
	HashMap btnListMap = new HashMap();
	
	public CmdBtnsDlg(JFrame parent) {
		super(parent);
		initComponent();
	}

	private void initComponent() {
		//title
		setTitle(BarFrame.consts.OrgonizeCommands());
		
		//list
		originalList = new JList<String>(btnNames);
		originalScrollBar = new JScrollPane(originalList);
		
		//arrow buttons
		toTable = new ArrowButton(">>");
		toBill = new ArrowButton(">>");
		toSale = new ArrowButton(">>");
		toSetting = new ArrowButton(">>");

		fromTable = new ArrowButton("<<");
		fromBill = new ArrowButton("<<");
		fromSale = new ArrowButton("<<");
		fromSetting = new ArrowButton("<<");
		
		//4 panels
		tablePanle = new JPanel();
		billPanle = new JPanel();
		salePanle = new JPanel();
		settingPanle = new JPanel();
		
		//reset button
		reset = new FunctionButton(BarFrame.consts.RESET());
		//close button
		close = new FunctionButton(BarFrame.consts.Close());
		
		//properties--------------------------------------------------------
		Border etchedBorder = BorderFactory.createEtchedBorder(); 
		tablePanle.setBorder(BorderFactory.createTitledBorder(etchedBorder,"Table"));
		billPanle.setBorder(BorderFactory.createTitledBorder(etchedBorder,"Bill"));
		salePanle.setBorder(BorderFactory.createTitledBorder(etchedBorder,"Sale"));
		settingPanle.setBorder(BorderFactory.createTitledBorder(etchedBorder,"Setting"));
		
		//layout------------------------------------------------------------
		tablePanle.setLayout(null);
		billPanle.setLayout(null);
		salePanle.setLayout(null);
		settingPanle.setLayout(null);
		getContentPane().setLayout(null);
		
		//build		
		setBounds(BarFrame.instance.getX(), BarFrame.instance.getY(), BarFrame.instance.getWidth(), BarFrame.instance.getHeight());
		getContentPane().add(originalScrollBar);
		getContentPane().add(toTable);
		getContentPane().add(toBill);
		getContentPane().add(toSale);
		getContentPane().add(toSetting);
		
		getContentPane().add(fromTable);
		getContentPane().add(fromBill);
		getContentPane().add(fromSale);
		getContentPane().add(fromSetting);
		
		getContentPane().add(tablePanle);
		getContentPane().add(billPanle);
		getContentPane().add(salePanle);
		getContentPane().add(settingPanle);
		
		getContentPane().add(reset);
		getContentPane().add(close);
		
		//listener
		toTable.addActionListener(this);
		toBill.addActionListener(this);
		toSale.addActionListener(this);
		toSetting.addActionListener(this);
		
		fromTable.addActionListener(this);
		fromBill.addActionListener(this);
		fromSale.addActionListener(this);
		fromSetting.addActionListener(this);
		
		tablePanle.addMouseListener(this);
		billPanle.addMouseListener(this);
		salePanle.addMouseListener(this);
		settingPanle.addMouseListener(this);
		
		reset.addActionListener(this);
		close.addActionListener(this);
		
		addComponentListener(this);
		
		initPanel(KEY_TABLE_CMD, tablePanle);
		initPanel(KEY_BILL_CMD, billPanle);
		initPanel(KEY_SALE_CMD, salePanle);
		initPanel(KEY_SETTING_CMD, settingPanle);
		
		relayout();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		int[] ids = originalList.getSelectedIndices();
		
		if(o == toTable) {
			if(ids.length < 1) {
				JOptionPane.showMessageDialog(this, BarFrame.consts.AtLeastOneShouldBeSelected());
				return;
			}
			addCmd(KEY_TABLE_CMD, ids, tablePanle);
			
		}else if(o == toBill) {
			if(ids.length < 1) {
				JOptionPane.showMessageDialog(this, BarFrame.consts.AtLeastOneShouldBeSelected());
				return;
			}
			addCmd(KEY_BILL_CMD, ids, billPanle);
			
		}else if(o == toSale) {
			if(ids.length < 1) {
				JOptionPane.showMessageDialog(this, BarFrame.consts.AtLeastOneShouldBeSelected());
				return;
			}
			addCmd(KEY_SALE_CMD, ids, salePanle);
			
		}else if(o == toSetting) {
			if(ids.length < 1) {
				JOptionPane.showMessageDialog(this, BarFrame.consts.AtLeastOneShouldBeSelected());
				return;
			}
			addCmd(KEY_SETTING_CMD, ids, settingPanle);
			
		}else if(o == fromTable) {
			clearCmd(KEY_TABLE_CMD, tablePanle);
			
		}else if(o == fromBill) {
			clearCmd(KEY_BILL_CMD, billPanle);
			
		}else if(o == fromSale) {
			clearCmd(KEY_SALE_CMD, salePanle);
			
		}else if(o == fromSetting) {
			clearCmd(KEY_SETTING_CMD, settingPanle);
			
		}else if(o == reset) {
			if(JOptionPane.showConfirmDialog(BarFrame.instance, "Are you sure to delete all your configration about command buttons? (interface will be reset to default layout.", DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0) {
				return;
			}			
			
			CustOpts.custOps.removeKeyAndValue(KEY_TABLE_CMD);
			CustOpts.custOps.removeKeyAndValue(KEY_BILL_CMD);
			CustOpts.custOps.removeKeyAndValue(KEY_SALE_CMD);
			CustOpts.custOps.removeKeyAndValue(KEY_SETTING_CMD);
			CustOpts.custOps.saveData();
			//update ui
			tablePanle.removeAll();
			billPanle.removeAll();
			salePanle.removeAll();
			settingPanle.removeAll();
		}else if(o == close) {
			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.RestartNeeded());
			this.dispose();
		}
		
		CustOpts.custOps.saveData();
		
		invalidate();
		revalidate();
		repaint();
	}

	private void clearCmd(String key, JPanel panel) {
		CustOpts.custOps.setKeyAndValue(key, "");
		CustOpts.custOps.saveData();
		//update ui
		panel.removeAll();
	}

	private void addCmd(String key, int[] ids, JPanel panel) {
		String strTableCmds = (String)CustOpts.custOps.getValue(key);
		if(strTableCmds == null)
			strTableCmds = "";
		
		if(strTableCmds.endsWith(",")) {
			strTableCmds = strTableCmds.substring(0, strTableCmds.length() - 1);
		}
		for (int i : ids) {
			strTableCmds += ("," + i);
		}
		CustOpts.custOps.setKeyAndValue(key, strTableCmds);
		
		//update ui
		initPanel(key, panel);
	}

	private void initPanel(String key, JPanel panel) {

		String TableCmds = (String)CustOpts.custOps.getValue(key);
		if(TableCmds == null)
			return;
		
		panel.removeAll();
		btnListMap.put(key, new ArrayList<JComponent>());
		
		String[] sIDs = TableCmds.split(",");
		for (String id : sIDs) {
			if(id.length() > 0) {//the first element could be an ""
				try {
					int idx = Integer.valueOf(id);
					((ArrayList) btnListMap.get(key)).add(buttons[idx].clone());
				}catch(Exception exp) {
					L.e("CmdBtnDlg", "exception when cloning a button and add into btnListMap.", exp);
					continue;
				}
			}
		}
		BarUtil.addFunctionButtons(panel, (ArrayList<JComponent>)btnListMap.get(key), 20);
		BarUtil.layoutCommandButtons(panel, (ArrayList<JComponent>)btnListMap.get(key), 20);
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		relayout();
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	
	private void relayout() {
		int heigth = getHeight();
		int width = getWidth();
		int panelHeight = (heigth - 120) / 4;
		
		//list
		originalScrollBar.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 169, heigth - CustOpts.SIZE_EDGE * 2 - 60);

		//buttons
		toTable.setBounds(originalScrollBar.getX() + originalScrollBar.getWidth() + CustOpts.HOR_GAP, 
				CustOpts.VER_GAP + panelHeight/2,  40, 20);
		fromTable.setBounds(toTable.getX(), toTable.getY() + CustOpts.VER_GAP + 20,  40, 20);
		
		toBill.setBounds(toTable.getX(), toTable.getY() + panelHeight + CustOpts.VER_GAP,  40, 20);
		fromBill.setBounds(toTable.getX(), toBill.getY() + CustOpts.VER_GAP + 20,  40, 20);
		
		toSale.setBounds(toBill.getX(), toBill.getY() + panelHeight + CustOpts.VER_GAP,  40, 20);
		fromSale.setBounds(toBill.getX(), toSale.getY() + CustOpts.VER_GAP + 20,  40, 20);
		
		toSetting.setBounds(toSale.getX(), toSale.getY() + panelHeight + CustOpts.VER_GAP,  40, 20);
		fromSetting.setBounds(toSale.getX(), toSetting.getY() + CustOpts.VER_GAP + 20,  40, 20);
		
		//panel
		tablePanle.setBounds(toTable.getX() + toTable.getWidth() + CustOpts.HOR_GAP,
				CustOpts.VER_GAP, 
				width - originalScrollBar.getWidth() - toTable.getWidth() - CustOpts.HOR_GAP * 3 - CustOpts.SIZE_EDGE * 2 - 20,
				panelHeight);
		billPanle.setBounds(tablePanle.getX(), tablePanle.getY() + tablePanle.getHeight() + CustOpts.VER_GAP,
				tablePanle.getWidth(), tablePanle.getHeight());
		salePanle.setBounds(billPanle.getX(), billPanle.getY() + billPanle.getHeight() + CustOpts.VER_GAP,
				billPanle.getWidth(), billPanle.getHeight());
		settingPanle.setBounds(salePanle.getX(), salePanle.getY() + salePanle.getHeight() + CustOpts.VER_GAP, 
				salePanle.getWidth(), salePanle.getHeight());

		reset.setBounds(settingPanle.getX(), settingPanle.getY() + settingPanle.getHeight() + CustOpts.VER_GAP, 80, 40);
		close.setBounds(settingPanle.getX() + settingPanle.getWidth() - 80, reset.getY(), 80, 40);

		BarUtil.layoutCommandButtons(tablePanle, (ArrayList<JComponent>)btnListMap.get(KEY_TABLE_CMD), TablesPanel.MAX_CMDBTN_QT);
		BarUtil.layoutCommandButtons(billPanle, (ArrayList<JComponent>)btnListMap.get(KEY_BILL_CMD), BillListPanel.MAX_CMDBTN_QT);
		BarUtil.layoutCommandButtons(salePanle, (ArrayList<JComponent>)btnListMap.get(KEY_SALE_CMD), SalesPanel.MAX_CMDBTN_QT);
		BarUtil.layoutCommandButtons(settingPanle, (ArrayList<JComponent>)btnListMap.get(KEY_SETTING_CMD), SettingPanel.MAX_CMDBTN_QT);

		invalidate();
		revalidate();
		repaint();
	}

	//buttons=========================================================================================================
    public static void initButtons() {
    	//TablePanel
		btnAddTable = new FunctionButton(BarFrame.consts.AddTable());
		btnOrderManage = new FunctionButton(BarFrame.consts.OrderManage());
		//btnWaiterReport = new FunctionButton(BarFrame.consts.WaiterReport());
		btnSetting = new FunctionButton(BarFrame.consts.SETTINGS());
		btnReport = new FunctionButton(BarFrame.consts.Report());
		btnCheckInOut = new FunctionButton(BarFrame.consts.CheckOut());
		
		//BillListPanel
		btnAddUser = new FunctionButton(BarFrame.consts.AddUser());
		btnPrintAll = new FunctionButton(BarFrame.consts.PrintAll());
		btnPrintOneBill = new FunctionButton(BarFrame.consts.PrintOneBill());
		btnPrintOneInVoice = new FunctionButton(BarFrame.consts.PrintOneInvoice());
		
		btnEqualBill = new FunctionToggleButton(BarFrame.consts.EqualBill());
		btnSplitItem = new FunctionToggleButton(BarFrame.consts.SplitItem());
		btnMoveItem = new FunctionToggleButton(BarFrame.consts.MoveItem());
		btnCombineAll = new FunctionButton(BarFrame.consts.CombineAll());
		
		btnSuspendAll = new FunctionButton(BarFrame.consts.SuspendAll());
		
    	//SalesPanel
    	btnCASH = new FunctionButton(BarFrame.consts.CASH());
    	btnDEBIT = new FunctionButton(BarFrame.consts.DEBIT());
    	btnVISA = new FunctionButton(BarFrame.consts.VISA());
        btnSplitBill = new FunctionButton(BarFrame.consts.SPLIT_BILL());
        btnRemoveItem = new FunctionButton(BarFrame.consts.REMOVEITEM());
        btnModify = new FunctionButton(BarFrame.consts.MODIFY());
        btnDiscItem = new FunctionToggleButton(BarFrame.consts.DISC_ITEM());
        btnChangePrice = new FunctionToggleButton(BarFrame.consts.ChangePrice());
        btnServiceFee = new FunctionButton(BarFrame.consts.SERVICEFEE());
        btnPrintBill = new FunctionButton(BarFrame.consts.PRINT_BILL());

        btnReturn = new FunctionButton(BarFrame.consts.RETURN());
        btnAddBill = new FunctionButton(BarFrame.consts.AddUser());
        btnMASTER = new FunctionButton(BarFrame.consts.MASTER());
        btnCancelAll = new FunctionButton(BarFrame.consts.CANCEL_ALL());
        btnVoidOrder = new FunctionButton(BarFrame.consts.VOID_ORDER());
        btnOpenDrawer = new FunctionButton(BarFrame.consts.OpenDrawer());
        btnDiscBill = new FunctionButton(BarFrame.consts.VolumnDiscount());
        btnRefund = new FunctionButton(BarFrame.consts.Refund());
        btnSend = new FunctionButton(BarFrame.consts.SEND());
        
        //btnLine_3_2 = new JToggleButton(BarFrame.consts.QTY());
		btnGIFTCARD = new FunctionButton(BarFrame.consts.GIFTCARD());
        btnDiscountCoupon = new FunctionButton(BarFrame.consts.COUPON());
		btnSuspend = new FunctionButton(BarFrame.consts.SUSPEND());
		btnCheckOrder = new FunctionButton(BarFrame.consts.OrderManage());
		btnEN = new FunctionButton("EN");
		btnFR = new FunctionButton("FR");
		btnCN = new FunctionButton("CN");
		btnCheckOut = new FunctionButton(BarFrame.consts.CheckOut());
		
        //SettingPanel
        btnEmployee = new FunctionButton(BarFrame.consts.Operator());
        //btnLine_2_3 = new FunctionButton("TBD");
        btnPrinter = new FunctionButton(BarFrame.consts.PRINTER());
        btnTable = new FunctionButton(BarFrame.consts.TABLE());
        btnBillFoot = new FunctionButton(BarFrame.consts.BillInfo());
        btnModifySetting = new FunctionButton(BarFrame.consts.MODIFY());
        btnGiftCardSetting = new FunctionButton(BarFrame.consts.GIFTCARD());
        btnCoupon = new FunctionButton(BarFrame.consts.COUPON());
        btnColor = new FunctionButton(BarFrame.consts.Color().toUpperCase());
        btnCheckInOutList = new FunctionButton(BarFrame.consts.CheckInOut());
        
        buttons = new ISButton[] {
        		btnAddTable,	//0
        		btnOrderManage,
        		btnSetting,
        		btnReport,
        		btnCheckInOut,
        			
        		btnAddUser,		//5
        		btnPrintAll,
//        		btnPrintOneBill,
//        		btnPrintOneInVoice,
        		btnEqualBill,
        		btnSplitItem,
        		btnMoveItem,
        		btnCombineAll,	//10
        		btnSuspendAll,
        			
        		btnCASH,
        		btnDEBIT,
        		btnVISA,
        		btnMASTER,		//15
        		btnGIFTCARD,
        		btnSplitBill,
        		btnRemoveItem,
        		btnModify,
        		btnDiscItem,	//20
        		btnChangePrice,
        		btnDiscBill,
        		btnServiceFee,
        		btnPrintBill,

        		btnReturn,		//25
        		btnAddBill,
        			   
        		btnCancelAll,
        		btnVoidOrder,
        		btnOpenDrawer,
        		btnDiscountCoupon,//30
        		btnRefund,		
        		btnSend,
        		btnEN,
        		btnFR,
        		btnCN,		//35
        			
        		btnEmployee,
        		btnPrinter,
        		btnTable,
        		btnBillFoot,
        		btnModifySetting,		//40
        		btnGiftCardSetting,
        		btnCoupon,
        		btnColor,
        		btnCheckInOutList};
        
        
        //listener------------------------------
        //tablePanel
        btnAddTable.addActionListener(Cmd_AddTable.getInstance());
        btnOrderManage.addActionListener(Cmd_OrderManage.getInstance());
        //btnWaiterReport.addActionListener(this);
        btnSetting.addActionListener(Cmd_Setting.getInstance());
        btnReport.addActionListener(Cmd_Report.getInstance());
        btnCheckInOut.addActionListener(Cmd_CheckInOut.getInstance());
        //billList
        btnAddUser.addActionListener(Cmd_AddUser.getInstance());
		btnPrintAll.addActionListener(Cmd_PrintAll.getInstance());
		btnPrintOneBill.addActionListener(Cmd_PrintOneBill.getInstance());
		btnPrintOneInVoice.addActionListener(Cmd_PrintOneInVoice.getInstance());
		btnEqualBill.addActionListener(Cmd_EqualBill.getInstance());
		btnCombineAll.addActionListener(Cmd_CombineAll.getInstance());
		btnSplitItem.addActionListener(Cmd_SplitItem.getInstance());
		btnMoveItem.addActionListener(Cmd_MoveItem.getInstance());
		btnSuspendAll.addActionListener(Cmd_SuspendAll.getInstance());
        //salesPanel
        btnCASH.addActionListener(Cmd_Pay.getInstance());
        btnDEBIT.addActionListener(Cmd_Pay.getInstance());
        btnVISA.addActionListener(Cmd_Pay.getInstance());
        btnSplitBill.addActionListener(Cmd_SlpitBill.getInstance());
        btnRemoveItem.addActionListener(Cmd_RemoveItem.getInstance());
        btnModify.addActionListener(Cmd_Modify.getInstance());
        btnDiscItem.addActionListener(Cmd_DiscItem.getInstance());
        btnChangePrice.addActionListener(Cmd_ChangePrice.getInstance());
        btnServiceFee.addActionListener(Cmd_ServiceFee.getInstance());
        btnPrintBill.addActionListener(Cmd_PrintBill.getInstance());

        btnReturn.addActionListener(Cmd_Return.getInstance());
        btnAddBill.addActionListener(Cmd_AddBill.getInstance());
        btnMASTER.addActionListener(Cmd_Pay.getInstance());
        btnCancelAll.addActionListener(Cmd_CancelAll.getInstance());
        btnVoidOrder.addActionListener(Cmd_VoidOrder.getInstance());
        btnOpenDrawer.addActionListener(Cmd_OpenDrawer.getInstance());
        btnDiscBill.addActionListener(Cmd_DiscBill.getInstance());
        btnRefund.addActionListener(Cmd_Refund.getInstance());
        btnSend.addActionListener(Cmd_Send.getInstance());
        
		btnGIFTCARD.addActionListener(Cmd_Other.getInstance());
		btnDiscountCoupon.addActionListener(Cmd_DiscountCoupon.getInstance());
		btnEN.addActionListener(Cmd_Lang.getInstance());
		btnFR.addActionListener(Cmd_Lang.getInstance());
		btnCN.addActionListener(Cmd_Lang.getInstance());
		btnSuspend.addActionListener(Cmd_Suspend.getInstance());
		btnCheckOrder.addActionListener(Cmd_CheckOrder.getInstance());
		btnCheckOut.addActionListener(Cmd_CheckInOut.getInstance());
        //
        btnEmployee.addActionListener(Cmd_Employee.getInstance());
        btnPrinter.addActionListener(Cmd_Printer.getInstance());
        btnTable.addActionListener(Cmd_Table.getInstance());
        btnBillFoot.addActionListener(Cmd_BillFoot.getInstance());
        btnModifySetting.addActionListener(Cmd_ModifySetting.getInstance());
        btnGiftCardSetting.addActionListener(Cmd_GiftCard.getInstance());
        btnCoupon.addActionListener(Cmd_SettingCoupon.getInstance());
        btnColor.addActionListener(Cmd_Color.getInstance());
        btnCheckInOutList.addActionListener(Cmd_CheckInOutList.getInstance());
        
        groupButtons();
	}
    
    private static void groupButtons() {
    	//get current customization
		Object strTableCmds = CustOpts.custOps.getValue(KEY_TABLE_CMD);
		Object strBillCmd = CustOpts.custOps.getValue(KEY_BILL_CMD);
		Object strSaleCmd = CustOpts.custOps.getValue(KEY_SALE_CMD);
		Object strSettingCmd = CustOpts.custOps.getValue(KEY_SETTING_CMD);
		
		//if not all set, then use default.
		if(strTableCmds == null || strBillCmd == null || strSaleCmd == null || strSettingCmd == null) {
			fillRegroupBtnWithDefault();
			
		}else {
			//all not null, then fill grouped button with custimizations.
			fillRegroupBtnWithCustimization(strTableCmds, groupedButtons[0]);
			fillRegroupBtnWithCustimization(strBillCmd, groupedButtons[1]);
			fillRegroupBtnWithCustimization(strSaleCmd, groupedButtons[2]);
			fillRegroupBtnWithCustimization(strSettingCmd, groupedButtons[3]);
		}
	}

	private static void fillRegroupBtnWithDefault(){
    	groupedButtons[0].add(btnAddTable);
    	groupedButtons[0].add(btnOrderManage);
    	groupedButtons[0].add(btnOpenDrawer);
    	//groupedButtons[0].add(btnWaiterReport);
    	groupedButtons[0].add(btnSetting);
    	groupedButtons[0].add(btnReport);
    	groupedButtons[0].add(btnCheckInOut);
    	
    	//BillListPanel
    	groupedButtons[1].add(btnReturn);
    	groupedButtons[1].add(btnAddUser);
    	groupedButtons[1].add(btnPrintAll);
//    	groupedButtons[1].add(btnPrintOneBill);
//    	groupedButtons[1].add(btnPrintOneInVoice);
    	groupedButtons[1].add(btnEqualBill);
    	groupedButtons[1].add(btnCombineAll);
    	groupedButtons[1].add(btnSplitItem);
    	groupedButtons[1].add(btnMoveItem);
    	groupedButtons[1].add(btnSuspendAll);
    	
    	//SalesPanel
    	groupedButtons[2].add(btnCASH);
    	groupedButtons[2].add(btnDEBIT);
    	groupedButtons[2].add(btnVISA);
    	groupedButtons[2].add(btnSplitBill);
    	groupedButtons[2].add(btnRemoveItem);
    	groupedButtons[2].add(btnModify);
    	groupedButtons[2].add(btnDiscItem);
    	groupedButtons[2].add(btnChangePrice);
    	groupedButtons[2].add(btnServiceFee);
    	groupedButtons[2].add(btnPrintBill);

    	groupedButtons[2].add(btnReturn.clone());
    	groupedButtons[2].add(btnAddBill);
    	groupedButtons[2].add(btnMASTER);
    	groupedButtons[2].add(btnCancelAll);
    	groupedButtons[2].add(btnVoidOrder);
    	groupedButtons[2].add(btnOpenDrawer.clone());
    	groupedButtons[2].add(btnDiscBill);
    	groupedButtons[2].add(btnRefund);
    	groupedButtons[2].add(btnSend);
		
    	groupedButtons[2].add(btnGIFTCARD);
    	groupedButtons[2].add(btnDiscountCoupon);
    	groupedButtons[2].add(btnSuspend);
    	groupedButtons[2].add(btnCheckOrder);
    	groupedButtons[2].add(btnSetting.clone());
    	groupedButtons[2].add(btnEN);
    	groupedButtons[2].add(btnFR);
    	groupedButtons[2].add(btnCN);
    	groupedButtons[2].add(btnReport.clone());
    	groupedButtons[2].add(btnCheckOut);
    	
    	//setting panels
        groupedButtons[3].add(btnReturn.clone());
        groupedButtons[3].add(btnEmployee);
        groupedButtons[3].add(btnPrinter);
        groupedButtons[3].add(btnTable);
        groupedButtons[3].add(btnBillFoot);
        groupedButtons[3].add(btnModifySetting);
        groupedButtons[3].add(btnGiftCardSetting);
        groupedButtons[3].add(btnCoupon);
        groupedButtons[3].add(btnColor);
        groupedButtons[3].add(btnCheckInOutList);
    }

	private static void fillRegroupBtnWithCustimization(Object customization, ArrayList<JComponent> groupedButtons) {
		String[] btns = ((String)customization).split(",");
		for (String string : btns) {
			try {
				int i = Integer.valueOf(string);
				groupedButtons.add(buttons[i].clone());
			}catch(Exception e) {
				continue;
			}
		}
	}	

	JList<String> originalList;
	JScrollPane originalScrollBar;
	
	ArrowButton toTable;
	ArrowButton toBill;
	ArrowButton toSale;
	ArrowButton toSetting;
	ArrowButton fromTable;
	ArrowButton fromBill;
	ArrowButton fromSale;
	ArrowButton fromSetting;
	
	JPanel tablePanle;
	JPanel billPanle;
	JPanel salePanle;
	JPanel settingPanle;

    private static FunctionButton reset;
    private static FunctionButton close;
    
	//TablePanel
    //private JToggleButton btnChangeMode;
	private static FunctionButton btnAddTable;
	private static FunctionButton btnOrderManage;
	//private static FunctionButton btnWaiterReport;
	private static FunctionButton btnSetting;
	private static FunctionButton btnReport;
	private static FunctionButton btnCheckInOut;
	
	//BillListPanel
	private static FunctionButton btnAddUser;
	private static FunctionButton btnPrintAll;
	private static FunctionButton btnPrintOneBill;
	private static FunctionButton btnPrintOneInVoice;
	private static FunctionToggleButton btnEqualBill;
	private static FunctionToggleButton btnSplitItem;
	private static FunctionToggleButton btnMoveItem;
	private static FunctionButton btnCombineAll;
	private static FunctionButton btnSuspendAll;
	
	//SalesPanel
	private static FunctionButton btnCASH;
	private static FunctionButton btnDEBIT;
	private static FunctionButton btnVISA;
	private static FunctionButton btnMASTER;
	private static FunctionButton btnGIFTCARD;
	private static FunctionButton btnSplitBill;
	private static FunctionButton btnRemoveItem;
	private static FunctionButton btnModify;
	private static FunctionToggleButton btnDiscItem;
	private static FunctionToggleButton btnChangePrice;
	private static FunctionButton btnServiceFee;
	private static FunctionButton btnPrintBill;

	private static FunctionButton btnReturn;
	private static FunctionButton btnAddBill;
	   
	private static FunctionButton btnCancelAll;
	private static FunctionButton btnVoidOrder;
	private static FunctionButton btnOpenDrawer;
	private static FunctionButton btnDiscBill;
	private static FunctionButton btnRefund;
	private static FunctionButton btnSend;
	
	private static FunctionButton btnDiscountCoupon;
	private static FunctionButton btnEN;
	private static FunctionButton btnFR;
	private static FunctionButton btnCN;
	private static FunctionButton btnCheckOut;
	private static FunctionButton btnSuspend;
	private static FunctionButton btnCheckOrder;
	
	//setting panels
    private static FunctionButton btnEmployee;
    private static FunctionButton btnPrinter;
    private static FunctionButton btnTable;
    private static FunctionButton btnBillFoot;
    private static FunctionButton btnModifySetting;
    private static FunctionButton btnGiftCardSetting;
    private static FunctionButton btnCoupon;
    private static FunctionButton btnColor;
    private static FunctionButton btnCheckInOutList;
    
    static ISButton[] buttons;
    
    static String[] btnNames = {
    		"AddTable",		//0
    		"OrderManage",
    		"Setting",
    		"Report",
    		"CheckInOut",
    			
    		"AddUser",		//5
    		"PrintAll",
//    		"PrintOneBill",
//    		"PrintOneInVoice",
    		"EqualBill",
    		"SplitItem",
    		"MoveItem",
    		"CombineAll",	//10
    		"SuspendAll",
    			
    		"CASH",
    		"DEBIT",
    		"VISA",			//
    		"MASTER",		//15
    		"GiftCard",		//
    		"SplitBill",	//
    		"RemoveItem",	//
    		"Modify",		//
    		"DiscItem",		//20
    		"ChangePrice",	//
    		"DiscBill",		//
    		"ServiceFee",	//
    		"PrintBill",	//

    		"Return",		//25
    		"AddBill",		//
    			   
    		"CancelAll",	//
    		"VoidOrder",	//
    		"OpenDrawer",	//
    		"DiscountCoupon",//30
    		"Refund",		//
    		"Send",			//
    		"EN",			//
    		"FR",			//
    		"ZH",			//35
    		"EmployeeList",	  //
    		"PrinterSetting", //
    		"TableSetting",	  //
    		"BillFootSetting",//
    		"ModifySetting",  //40
    		"GiftCardSetting",//
    		"CouponSetting",  //
    		"ColorSetting",	  //
    		"CheckInOutList"};//

	@Override
	public void mouseClicked(MouseEvent e) {
		Object o =e.getSource(); 
		JFileChooser requestFileChooser = new JFileChooser();
	    requestFileChooser.setFileFilter(new FileFilterRequest());
	    requestFileChooser.setMultiSelectionEnabled(false);
	    
	    String filePath = "";
	    final int res = requestFileChooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
        	File file = requestFileChooser.getSelectedFile();
        	filePath = file != null ? file.getAbsolutePath() : null;
        }else {
        	return;
        }
		if(o == tablePanle) {
			BarOption.setTablePanelBK(filePath);
		}else if(o == billPanle) {
			BarOption.setBillPanelBK(filePath);
		}else if(o == salePanle) {
			BarOption.setSalePanelBK(filePath);
		}else if(o == settingPanle) {
			BarOption.setSettingPanelBK(filePath);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
    /**
     * This file filter will accept folders, jpeg, jpg and png files.
     */
    public static class FileFilterRequest extends FileFilter {
        /**
         * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
         */
        @Override
        public boolean accept(final File file) {
            return (file.isDirectory() || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png"));
        }

        /**
         * @see javax.swing.filechooser.FileFilter#getDescription()
         */
        @Override
        public String getDescription() {
            return "Requests";
        }
    }
}
