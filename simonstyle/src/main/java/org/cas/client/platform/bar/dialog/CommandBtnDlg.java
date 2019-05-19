package org.cas.client.platform.bar.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.cas.client.platform.bar.action.Cmd_AddBill;
import org.cas.client.platform.bar.action.Cmd_AddTable;
import org.cas.client.platform.bar.action.Cmd_AddUser;
import org.cas.client.platform.bar.action.Cmd_BillFoot;
import org.cas.client.platform.bar.action.Cmd_CancelAll;
import org.cas.client.platform.bar.action.Cmd_ChangePrice;
import org.cas.client.platform.bar.action.Cmd_CheckInOut;
import org.cas.client.platform.bar.action.Cmd_CheckInOut3;
import org.cas.client.platform.bar.action.Cmd_CheckOrder;
import org.cas.client.platform.bar.action.Cmd_Color;
import org.cas.client.platform.bar.action.Cmd_CombineAll;
import org.cas.client.platform.bar.action.Cmd_Coupon;
import org.cas.client.platform.bar.action.Cmd_DiscBill;
import org.cas.client.platform.bar.action.Cmd_DiscItem;
import org.cas.client.platform.bar.action.Cmd_DiscountCoupon;
import org.cas.client.platform.bar.action.Cmd_Employee;
import org.cas.client.platform.bar.action.Cmd_EqualBill;
import org.cas.client.platform.bar.action.Cmd_GiftCard;
import org.cas.client.platform.bar.action.Cmd_Lang;
import org.cas.client.platform.bar.action.Cmd_Modify;
import org.cas.client.platform.bar.action.Cmd_Modify3;
import org.cas.client.platform.bar.action.Cmd_MoveItem;
import org.cas.client.platform.bar.action.Cmd_OpenDrawer;
import org.cas.client.platform.bar.action.Cmd_OrderManage;
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
import org.cas.client.platform.bar.action.Cmd_Return2;
import org.cas.client.platform.bar.action.Cmd_Return3;
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
import org.cas.client.platform.bar.uibeans.MoreButton;
import org.cas.client.platform.cascustomize.CustOpts;

public class CommandBtnDlg extends JDialog implements ComponentListener, ActionListener{
   
	public static ArrayList[] groupedButtons = {new ArrayList<JComponent>(), new ArrayList<JComponent>(), new ArrayList<JComponent>(),new ArrayList<JComponent>()};
	
	public CommandBtnDlg(JFrame parent) {
		super(parent);
		initComponent();
	}

	private void initComponent() {
		setTitle(BarFrame.consts.OrgonizeCommands());
		originalList = new JList<String>(btnNames);
		originalScrollBar = new JScrollPane(originalList);
		
		toTable = new ArrowButton(">>");
		toBill = new ArrowButton(">>");
		toSale = new ArrowButton(">>");
		toSetting = new ArrowButton(">>");

		fromTable = new ArrowButton("<<");
		fromBill = new ArrowButton("<<");
		fromSale = new ArrowButton("<<");
		fromSetting = new ArrowButton("<<");
		
		tablePanle = new JPanel();
		billPanle = new JPanel();
		salePanle = new JPanel();
		settingPanle = new JPanel();
		
		Border etchedBorder = BorderFactory.createEtchedBorder(); 
		tablePanle.setBorder(BorderFactory.createTitledBorder(etchedBorder,"Table"));
		billPanle.setBorder(BorderFactory.createTitledBorder(etchedBorder,"Bill"));
		salePanle.setBorder(BorderFactory.createTitledBorder(etchedBorder,"Sale"));
		settingPanle.setBorder(BorderFactory.createTitledBorder(etchedBorder,"Setting"));
		
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

		toTable.addActionListener(this);
		toBill.addActionListener(this);
		toSale.addActionListener(this);
		toSetting.addActionListener(this);
		
		fromTable.addActionListener(this);
		fromBill.addActionListener(this);
		fromSale.addActionListener(this);
		fromSetting.addActionListener(this);
		
		addComponentListener(this);
		
		relayout();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o == toTable) {
			
		}else if(o == toBill) {
			
		}else if(o == toSale) {
			
		}else if(o == toSetting) {
			
		}else if(o == fromTable) {
			
		}else if(o == fromBill) {
			
		}else if(o == fromSale) {
			
		}else if(o == fromSetting) {
			
		}
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
		int panelHeight = (heigth - 80) / 4;
		
		originalScrollBar.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 169, heigth - CustOpts.SIZE_EDGE * 2 - 60);

		toTable.setBounds(originalScrollBar.getX() + originalScrollBar.getWidth() + CustOpts.HOR_GAP, 
				CustOpts.VER_GAP + panelHeight/2,  40, 20);
		fromTable.setBounds(toTable.getX(), toTable.getY() + CustOpts.VER_GAP + 20,  40, 20);
		
		toBill.setBounds(toTable.getX(), toTable.getY() + panelHeight + CustOpts.VER_GAP,  40, 20);
		fromBill.setBounds(toTable.getX(), toBill.getY() + CustOpts.VER_GAP + 20,  40, 20);
		
		toSale.setBounds(toBill.getX(), toBill.getY() + panelHeight + CustOpts.VER_GAP,  40, 20);
		fromSale.setBounds(toBill.getX(), toSale.getY() + CustOpts.VER_GAP + 20,  40, 20);
		
		toSetting.setBounds(toSale.getX(), toSale.getY() + panelHeight + CustOpts.VER_GAP,  40, 20);
		fromSetting.setBounds(toSale.getX(), toSetting.getY() + CustOpts.VER_GAP + 20,  40, 20);
		
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
		
	}

	//buttons=========================================================================================================
    public static void initButtons() {
    	//TablePanel
		btnAddTable = new FunctionButton(BarFrame.consts.AddTable());
		btnOrderManage = new FunctionButton(BarFrame.consts.OrderManage());
		btnOpenDrawer2 = new FunctionButton(BarFrame.consts.OpenDrawer());
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
		btnReturn2 = new FunctionButton(BarFrame.consts.RETURN());
		
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
        btnMore = new MoreButton(BarFrame.consts.MORE());
        btnSend = new FunctionButton(BarFrame.consts.SEND());
        
        btnReport2 = new FunctionButton(BarFrame.consts.Report());
        //btnLine_3_2 = new JToggleButton(BarFrame.consts.QTY());
		btnOTHER = new FunctionButton(BarFrame.consts.GIFTCARD());
        btnDiscountCoupon = new FunctionButton(BarFrame.consts.COUPON());
		btnSuspend = new FunctionButton(BarFrame.consts.SUSPEND());
		btnCheckOrder = new FunctionButton(BarFrame.consts.OrderManage());
		btnEN = new FunctionButton("EN");
		btnFR = new FunctionButton("FR");
		btnCN = new FunctionButton("CN");
		btnSettings2 = new FunctionButton(BarFrame.consts.SETTINGS());
		btnCheckOut2 = new FunctionButton(BarFrame.consts.CheckOut());
		
        //SettingPanel
        btnReturn3 = new FunctionButton(BarFrame.consts.RETURN());
        btnEmployee = new FunctionButton(BarFrame.consts.Operator());
        //btnLine_2_3 = new FunctionButton("TBD");
        btnPrinter = new FunctionButton(BarFrame.consts.PRINTER());
        btnTable = new FunctionButton(BarFrame.consts.TABLE());
        btnBillFoot = new FunctionButton(BarFrame.consts.BillInfo());
        btnModify3 = new FunctionButton(BarFrame.consts.MODIFY());
        btnGiftCard = new FunctionButton(BarFrame.consts.GIFTCARD());
        btnCoupon = new FunctionButton(BarFrame.consts.COUPON());
        btnColor = new FunctionButton(BarFrame.consts.Color().toUpperCase());
        btnCheckInOut3 = new FunctionButton(BarFrame.consts.CheckInOut());
        //listener------------------------------
        //tablePanel
        btnAddTable.addActionListener(new Cmd_AddTable());
        btnOrderManage.addActionListener(new Cmd_OrderManage());
        btnOpenDrawer2.addActionListener(Cmd_OpenDrawer.getInstance());
        //btnWaiterReport.addActionListener(this);
        btnSetting.addActionListener(Cmd_Setting.getInstance());
        btnReport.addActionListener(Cmd_Report.getInstance());
        btnCheckInOut.addActionListener(Cmd_CheckInOut.getInstance());
        //billList
        btnAddUser.addActionListener(new Cmd_AddUser());
		btnPrintAll.addActionListener(new Cmd_PrintAll());
		btnPrintOneBill.addActionListener(new Cmd_PrintOneBill());
		btnPrintOneInVoice.addActionListener(new Cmd_PrintOneInVoice());
		btnEqualBill.addActionListener(new Cmd_EqualBill());
		btnCombineAll.addActionListener(new Cmd_CombineAll());
		btnSplitItem.addActionListener(new Cmd_SplitItem());
		btnMoveItem.addActionListener(new Cmd_MoveItem());
		btnSuspendAll.addActionListener(new Cmd_SuspendAll());
		btnReturn2.addActionListener(new Cmd_Return2());
        //salesPanel
        btnCASH.addActionListener(Cmd_Pay.getInstance());
        btnDEBIT.addActionListener(Cmd_Pay.getInstance());
        btnVISA.addActionListener(Cmd_Pay.getInstance());
        btnSplitBill.addActionListener(new Cmd_SlpitBill());
        btnRemoveItem.addActionListener(new Cmd_RemoveItem());
        btnModify.addActionListener(new Cmd_Modify());
        btnDiscItem.addActionListener(new Cmd_DiscItem());
        btnChangePrice.addActionListener(new Cmd_ChangePrice());
        btnServiceFee.addActionListener(new Cmd_ServiceFee());
        btnPrintBill.addActionListener(new Cmd_PrintBill());

        btnReturn.addActionListener(new Cmd_Return());
        btnAddBill.addActionListener(new Cmd_AddBill());
        btnMASTER.addActionListener(Cmd_Pay.getInstance());
        btnCancelAll.addActionListener(new Cmd_CancelAll());
        btnVoidOrder.addActionListener(Cmd_VoidOrder.getInstance());
        btnOpenDrawer.addActionListener(Cmd_OpenDrawer.getInstance());
        btnDiscBill.addActionListener(new Cmd_DiscBill());
        btnRefund.addActionListener(new Cmd_Refund());
        btnSend.addActionListener(new Cmd_Send());
        
		btnReport2.addActionListener(Cmd_Report.getInstance());
		btnOTHER.addActionListener(Cmd_Pay.getInstance());
		btnDiscountCoupon.addActionListener(new Cmd_DiscountCoupon());
		btnEN.addActionListener(Cmd_Lang.getInstance());
		btnFR.addActionListener(Cmd_Lang.getInstance());
		btnCN.addActionListener(Cmd_Lang.getInstance());
		btnSettings2.addActionListener(Cmd_Setting.getInstance());
		btnSuspend.addActionListener(new Cmd_Suspend());
		btnCheckOrder.addActionListener(new Cmd_CheckOrder());
		btnCheckOut2.addActionListener(Cmd_CheckInOut.getInstance());
        //
        btnReturn3.addActionListener(new Cmd_Return3());
        btnEmployee.addActionListener(new Cmd_Employee());
        btnPrinter.addActionListener(new Cmd_Printer());
        btnTable.addActionListener(new Cmd_Table());
        btnBillFoot.addActionListener(new Cmd_BillFoot());
        btnModify3.addActionListener(new Cmd_Modify3());
        btnGiftCard.addActionListener(new Cmd_GiftCard());
        btnCoupon.addActionListener(new Cmd_Coupon());
        btnColor.addActionListener(new Cmd_Color());
        btnCheckInOut3.addActionListener(new Cmd_CheckInOut3());
        
        regroupButtons();
	}
    
    private static void regroupButtons() {
		Object TableCmds = CustOpts.custOps.getValue("TableCmds");
		Object BillCmd = CustOpts.custOps.getValue("BillCmd");
		Object SaleCmd = CustOpts.custOps.getValue("SaleCmd");
		Object SettingCmd = CustOpts.custOps.getValue("SettingCmd");
		if(TableCmds == null && BillCmd == null && SaleCmd == null && SettingCmd == null) {
			regroupButtonsByDefault();
		}else {
			if(TableCmds != null) {
				addIntoGroupedButtons(TableCmds, groupedButtons[0]);
			}
			
			if(BillCmd != null) {
				addIntoGroupedButtons(TableCmds, groupedButtons[1]);
			}
			
			if(SaleCmd != null) {
				addIntoGroupedButtons(TableCmds, groupedButtons[2]);
			}
			
			if(SettingCmd != null) {
				addIntoGroupedButtons(TableCmds, groupedButtons[3]);
			}
		}
	}

	private static void addIntoGroupedButtons(Object TableCmds, ArrayList<JComponent> groupedButtons) {
		String[] btns = ((String)TableCmds).split(",");
		for (String string : btns) {
			groupedButtons.add(buttons[Integer.valueOf(string)]);
		}
	}

	private static void regroupButtonsByDefault(){
    	groupedButtons[0].add(btnAddTable);
    	groupedButtons[0].add(btnOrderManage);
    	groupedButtons[0].add(btnOpenDrawer2);
    	//groupedButtons[0].add(btnWaiterReport);
    	groupedButtons[0].add(btnSetting);
    	groupedButtons[0].add(btnReport);
    	groupedButtons[0].add(btnCheckInOut);
    	
    	//BillListPanel
    	groupedButtons[1].add(btnAddUser);
    	groupedButtons[1].add(btnPrintAll);
    	groupedButtons[1].add(btnPrintOneBill);
    	groupedButtons[1].add(btnPrintOneInVoice);
    	groupedButtons[1].add(btnEqualBill);
    	groupedButtons[1].add(btnSplitItem);
    	groupedButtons[1].add(btnMoveItem);
    	groupedButtons[1].add(btnCombineAll);
    	groupedButtons[1].add(btnSuspendAll);
    	groupedButtons[1].add(btnReturn2);
    	
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

    	groupedButtons[2].add(btnReturn);
    	groupedButtons[2].add(btnAddBill);
    	groupedButtons[2].add(btnMASTER);
    	groupedButtons[2].add(btnCancelAll);
    	groupedButtons[2].add(btnVoidOrder);
    	groupedButtons[2].add(btnOpenDrawer);
    	groupedButtons[2].add(btnDiscBill);
    	groupedButtons[2].add(btnRefund);
    	groupedButtons[2].add(btnSend);
		
    	groupedButtons[2].add(btnReport2);
    	groupedButtons[2].add(btnOTHER);
    	groupedButtons[2].add(btnDiscountCoupon);
    	groupedButtons[2].add(btnEN);
    	groupedButtons[2].add(btnFR);
    	groupedButtons[2].add(btnCN);
    	groupedButtons[2].add(btnSettings2);
    	groupedButtons[2].add(btnSuspend);
    	groupedButtons[2].add(btnCheckOrder);
    	groupedButtons[2].add(btnCheckOut2);
    	
    	//setting panels
        groupedButtons[3].add(btnReturn3);
        groupedButtons[3].add(btnEmployee);
        groupedButtons[3].add(btnPrinter);
        groupedButtons[3].add(btnTable);
        groupedButtons[3].add(btnBillFoot);
        groupedButtons[3].add(btnModify3);
        groupedButtons[3].add(btnGiftCard);
        groupedButtons[3].add(btnCoupon);
        groupedButtons[3].add(btnColor);
        groupedButtons[3].add(btnCheckInOut3);
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

    
	//TablePanel
    //private JToggleButton btnChangeMode;
	private static FunctionButton btnAddTable;
	private static FunctionButton btnOrderManage;
	private static FunctionButton btnOpenDrawer2;
	//private static FunctionButton btnWaiterReport;
	private static FunctionButton btnSetting;
	private static FunctionButton btnReport;
	private static FunctionButton btnCheckInOut;
	
	//BillListPanel
	private static FunctionButton btnAddUser;
	private static FunctionButton btnPrintAll;
	private static FunctionButton btnPrintOneBill;
	private static FunctionButton btnPrintOneInVoice;
	public static FunctionToggleButton btnEqualBill;
	public static FunctionToggleButton btnSplitItem;
	public static FunctionToggleButton btnMoveItem;
	public static FunctionButton btnCombineAll;
	private static FunctionButton btnSuspendAll;
	private static FunctionButton btnReturn2;
	
	//SalesPanel
	public static FunctionButton btnCASH;
	public static FunctionButton btnDEBIT;
	public static FunctionButton btnVISA;
	public static FunctionButton btnMASTER;
	public static FunctionButton btnOTHER;
	private static FunctionButton btnSplitBill;
	private static FunctionButton btnRemoveItem;
	private static FunctionButton btnModify;
	public static FunctionToggleButton btnDiscItem;
	public static FunctionToggleButton btnChangePrice;
	private static FunctionButton btnServiceFee;
	private static FunctionButton btnPrintBill;

	private static FunctionButton btnReturn;
	private static FunctionButton btnAddBill;
	   
	private static FunctionButton btnCancelAll;
	private static FunctionButton btnVoidOrder;
	private static FunctionButton btnOpenDrawer;
	private static FunctionButton btnDiscBill;
	private static FunctionButton btnRefund;
	private static MoreButton btnMore;
	private static FunctionButton btnSend;
	
	private static FunctionButton btnReport2;
	private static FunctionButton btnDiscountCoupon;
	public static FunctionButton btnEN;
	public static FunctionButton btnFR;
	public static FunctionButton btnCN;
	private static FunctionButton btnSettings2;
	private static FunctionButton btnCheckOut2;
	private static FunctionButton btnSuspend;
	private static FunctionButton btnCheckOrder;
	
	//setting panels
    private static FunctionButton btnReturn3;
    private static FunctionButton btnEmployee;
    private static FunctionButton btnPrinter;
    private static FunctionButton btnTable;
    private static FunctionButton btnBillFoot;
    private static FunctionButton btnModify3;
    private static FunctionButton btnGiftCard;
    private static FunctionButton btnCoupon;
    private static FunctionButton btnColor;
    private static FunctionButton btnCheckInOut3;
    
    static AbstractButton[] buttons = {
    		btnAddTable,		//0
    		btnOrderManage,		//1
    		btnOpenDrawer2,		//2
    		btnSetting,		//3
    		btnReport,		//4
    		btnCheckInOut,		//5
    			
    		btnAddUser,		//6
    		btnPrintAll,		//7
    		btnPrintOneBill,		//8
    		btnPrintOneInVoice,		//9
    		btnEqualBill,		//10
    		btnSplitItem,		//11
    		btnMoveItem,		//12
    		btnCombineAll,		//13
    		btnSuspendAll,		//14
    		btnReturn2,		//15
    			
    		btnCASH,		//16
    		btnDEBIT,		//17
    		btnVISA,		//18
    		btnMASTER,		//19
    		btnOTHER,		//20
    		btnSplitBill,		//21
    		btnRemoveItem,		//22
    		btnModify,		//23
    		btnDiscItem,		//24
    		btnChangePrice,		//25
    		btnServiceFee,		//26
    		btnPrintBill,		//27

    		btnReturn,		//28
    		btnAddBill,		//29
    			   
    		btnCancelAll,		//30
    		btnVoidOrder,		//31
    		btnOpenDrawer,		//32
    		btnDiscBill,		//33
    		btnRefund,		//34
    		btnMore,		//35
    		btnSend,		//36
    			
    		btnReturn3,		//37
    		btnEmployee,		//38
    		btnPrinter,		//39
    		btnTable,		//40
    		btnBillFoot,		//41
    		btnModify3,		//42
    		btnGiftCard,		//43
    		btnCoupon,		//44
    		btnColor,		//45
    		btnCheckInOut3}; 		//46
    
    static String[] btnNames = {
    		"AddTable",		//0
    		"OrderManage",		//1
    		"OpenDrawer2",		//2
    		"Setting",		//3
    		"Report",		//4
    		"CheckInOut",		//5
    			
    		"AddUser",		//6
    		"PrintAll",		//7
    		"PrintOneBill",		//8
    		"PrintOneInVoice",		//9
    		"EqualBill",		//10
    		"SplitItem",		//11
    		"MoveItem",		//12
    		"CombineAll",		//13
    		"SuspendAll",		//14
    		"Return2",		//15
    			
    		"CASH",		//16
    		"DEBIT",		//17
    		"VISA",		//18
    		"MASTER",		//19
    		"OTHER",		//20
    		"SplitBill",		//21
    		"RemoveItem",		//22
    		"Modify",		//23
    		"DiscItem",		//24
    		"ChangePrice",		//25
    		"ServiceFee",		//26
    		"PrintBill",		//27

    		"Return",		//28
    		"AddBill",		//29
    			   
    		"CancelAll",		//30
    		"VoidOrder",		//31
    		"OpenDrawer",		//32
    		"DiscBill",		//33
    		"Refund",		//34
    		"More",		//35
    		"Send",		//36
    			
    		"Return3",		//37
    		"Employee",		//38
    		"Printer",		//39
    		"Table",		//40
    		"BillFoot",		//41
    		"Modify3",		//42
    		"GiftCard",		//43
    		"Coupon",		//44
    		"Color",		//45
    		"CheckInOut3"}; 		//46
}
