package org.cas.client.platform.bar.print;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JOptionPane;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.model.Bill;
import org.cas.client.platform.bar.model.Category;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.model.Printer;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

//If the ip of a printer is "LPT1", then will actually user com interface to drive the printer.
public class PrintService{

    public static final String OLD_SUBTOTAL = "*Old Subtotal:";
    public static final String OLD_GST = "*Old GST:";
    public static final String OLD_QST = "*Old QST:";
    public static final String OLD_TOTAL = "*Old Total:";
    
	public static final String REF_TO = "*ref to:";

	private static final String RE_PRINTED_INTERNAL_USE = "*re-printed* *internal use*\n";

	private static final String RE_PRINTED = "*re-printed*\n";

	public static int SUCCESS = -1;	//@NOTE:must be less than 0, because if it's 0, means the first element caused error.
    
    public static Category[] allCategory;
    public static String[] sepLines;
    
    public static HashMap<String,Printer> ipPrinterMap = new HashMap<>();
    private static HashMap<String,List<Dish>> ipSelectionsMap;
    private static HashMap<String,List<String>> ipContentMap;

    private static String curPrintIp = "";
    private String code = "GBK";
    private static String SEP_STR1 = "=";
    private static String SEP_STR2 = "-";

    private static String SOUSTOTAL = "SOUS-TOTAL";
    private static String TPS = "TPS";
    private static String TVQ = "TVQ";

    private static String SERVICE_FEE = "Service Fee";
    private static String DISCOUNT = "Discount";
    
    private static String ADDI = "ADDI";
    private static String RFER = "RFER";
    
    private boolean printerConnectedFlag;
    private boolean contentReadyForPrintFlag;
    
    private static javax.print.PrintService defaultService;

    //fro BeiYangPrinter----------------------

    //WIFI port variable
    private static final int POSPORT = 9100; 	// The port handle of Processing instruction
    private static final int STATEPORT = 4000; 	// The port handle of Query State

    //Print Mode
    private static final int PRINT_MODE_STANDARD = 0;
    private static final int PRINT_MODE_PAGE = 1;
    public static int printMode = PRINT_MODE_STANDARD;

    public static final int POS_SUCCESS=1000;		//success
    public static final int ERR_PROCESSING = 1001;	//processing error
    public static final int ERR_PARAM = 1002;		//parameter error

	private static final String CASH = "ARGENT";
	private static final String REFUND = "Refund : ";
    public static final String METHOD = "Method:";
	private static final String VOID = "*Voided*";
    
    //The start time and end time are long format, need to be translate for print.
    public static void exePrintBill(BillPanel billPanel, List<Dish> saleRecords){
    	//init status
    	flushIpContent();
        reInitPrintRelatedMaps();
        
        String printerIP = BarFrame.menuPanel.getPrinters()[0].getIp();
        List<String> contents = ipContentMap.get(printerIP);
		if(contents == null) {
        	ipContentMap.put(printerIP,new ArrayList<String>());
        	contents = ipContentMap.get(printerIP);
		}
        int tWidth = BarUtil.getPreferedWidth();
        
        //push head info
	    pushBillHeadInfo(contents, tWidth, String.valueOf(billPanel.billID));
	    
	    //push table, bill waiter and time
	    String tableIdx = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
	    StringBuilder startTimeStr = new StringBuilder(BarFrame.instance.valStartTime.getText());
	    if(billPanel.billButton != null) {
	    	startTimeStr.append("(").append(billPanel.billButton.getText()).append(")");
	    }
	    pushWaiterAndTime(contents, tWidth, tableIdx, startTimeStr.toString(), "");
	    
	    //push content and total
	    String content = formatContentForBill(saleRecords, printerIP, billPanel, tWidth).toString();
	    
	    int pEnd = content.lastIndexOf("\n");
	    int pStart = content.substring(0, pEnd).lastIndexOf("\n");//ignore the "\n" at the end
	    String lastRow = content.substring(pStart, pEnd);
	    
	    contents.add(content.substring(0, pStart + 1));
	    contents.add("BigFont");	    //push bigger font. while it will not work for mev.
	    contents.add(lastRow);
	    contents.add("NormalFont");     //push normal font
        
        //push end message
	    if(billPanel.comment.length() >= 0) {
	    	contents.add("\n" + billPanel.comment);	//has comment means bill printed(will be co-considered when saving paper flag is set) or invoice was reopened,
        }else {
            pushEndMessage(contents);
        }
        
        //push cut
        contents.add("\n\n\n\n\n");
        contents.add("cut");
        
        printContents();
    }
    
    //The start time and end time are long format, need to be translate for print.
    //@NOTE: not openned for now.
    public static void exePrintBills(List<BillPanel> unclosedBillPanels){
		flushIpContent();
        reInitPrintRelatedMaps();
        
        String printerIP = BarFrame.menuPanel.getPrinters()[0].getIp();
        if(ipContentMap.get(printerIP) == null)
        	ipContentMap.put(printerIP,new ArrayList<String>());
        List<String> contentList = ipContentMap.get(printerIP);
        
    	pushAllInOneBillContent(unclosedBillPanels, contentList, printerIP);
    	
        pushEndMessage(contentList);	//couldn't be an expired bill. so warry no about comment.
        contentList.add("\n\n\n\n\n");
        contentList.add("cut");
        
        printContents();
    }

    //The start time and end time are long format, need to be translate for print.
//    private static void exePrintConbinedInvoice(List<BillPanel> unclosedBillPanels, boolean isCashBack){
//		flushIpContent();
//        reInitPrintRelatedMaps();
//        
//        String printerIP = BarFrame.menuPanel.getPrinters()[0].getIp();
//        if(ipContentMap.get(printerIP) == null)
//        	ipContentMap.put(printerIP,new ArrayList<String>());
//        List<String> contentList = ipContentMap.get(printerIP);
//        
//    	pushAllInOneBillContent(unclosedBillPanels, contentList, printerIP);
//    	
//    	//payInfo
//        pushPayInfo(unclosedBillPanels.get(0), contentList,  BarUtil.getPreferedWidth(), isCashBack);
//        
//        //end message
//        pushEndMessage(contentList);
//        contentList.add("\n\n\n\n\n");
//        contentList.add("cut");
//        
//        printContents();
//    }
    
	public static void pushAllInOneBillContent(List<BillPanel> unclosedBillPanels, List<String> contentList, String printerIP) {
        
        int tWidth = BarUtil.getPreferedWidth();
        
        pushBillHeadInfo(contentList, tWidth, String.valueOf(unclosedBillPanels.get(0).getBillId()));
        
	    String tableIdx = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
	    StringBuilder startTimeStr = new StringBuilder(BarFrame.instance.valStartTime.getText());
	    pushWaiterAndTime(contentList, tWidth, tableIdx, startTimeStr.toString(), "");
        
        float totalSubTotal = 0.0f;
        float totalTPS = 0.0f;
        float totalTVQ = 0.0f;
        float totalPrice = 0.0f;
        StringBuilder content = new StringBuilder();
        for (BillPanel billPanel : unclosedBillPanels) {
        	content.append(formatContentForBill(billPanel.orderedDishAry, printerIP, billPanel, tWidth));
        	//fetch out total price in content.
        	String tContent = content.substring(content.lastIndexOf("-") + 2); //there's a "\n".
			String[] a = tContent.split("\n");
			if(a.length >= 3) {
				String strAvT = a[0].substring(a[0].indexOf(":") + 1);
				totalSubTotal += Float.valueOf(strAvT.trim());
				String strTPS = a[1].substring(a[1].indexOf(":") + 1);
				totalTPS += Float.valueOf(strTPS.trim());
				String strTVQ = a[2].substring(a[2].indexOf(":") + 1);
				totalTVQ += Float.valueOf(strTVQ.trim());
				
	    		String strTotal = a[3].substring(a[3].indexOf(":") + 1);
	    		totalPrice += Float.valueOf(strTotal.trim());
			}
		}

        //seperator
        String sep_str2 = (String)CustOpts.custOps.getValue("sep_str2");
        if(sep_str2 == null || sep_str2.length() == 0){
            sep_str2 = SEP_STR2;
        }
        content.append(BarUtil.generateString(tWidth, sep_str2)).append("\n");
        //subtotal, tps, tvq
        StringBuilder subTotal = new StringBuilder(SOUSTOTAL).append(":").append(BarUtil.formatMoney(totalSubTotal));
        int lengthOfSpaceBeforeTime = tWidth - subTotal.length();
        if(lengthOfSpaceBeforeTime > 0) 
        	content.append(BarUtil.generateString(lengthOfSpaceBeforeTime, " "));        	
        content.append(subTotal).append("\n");
        
        StringBuilder tps = new StringBuilder(TPS).append(":").append(BarUtil.formatMoney(totalTPS));
        lengthOfSpaceBeforeTime = tWidth - tps.length();
        if(lengthOfSpaceBeforeTime > 0) 
        	content.append(BarUtil.generateString(lengthOfSpaceBeforeTime, " "));
        content.append(tps).append("\n");
        
        StringBuilder tvq = new StringBuilder(TVQ).append(":").append(BarUtil.formatMoney(totalTVQ));
        lengthOfSpaceBeforeTime = tWidth - tvq.length();
        if(lengthOfSpaceBeforeTime > 0) 
        	content.append(BarUtil.generateString(lengthOfSpaceBeforeTime, " "));
        content.append(tvq).append("\n");
        
        contentList.add(content.toString());
        //total
        contentList.add("BigFont");
        contentList.add("\n\n               Total:" + totalPrice);
        contentList.add("NormalFont");
		
	}
    
	//The start time and end time are long format, need to be translate for print.
	//needPrint: sometimes we dont need to print out content, while we always need to send mev a query.
    public static void exePrintInvoice(BillPanel billPanel, boolean isCashBack, boolean isToCustomer, boolean needPrint){
        flushIpContent();
        reInitPrintRelatedMaps();

        //there's a case that we don't print invoice: when it's not mev print, and and bill printed, and set the flag of saving paper.
        String printerIP = BarFrame.menuPanel.getPrinters()[0].getIp();
        if(!"mev".equalsIgnoreCase(printerIP) && !needPrint) {	//needPrint == false means bill printed and saving paper flag is set.
        	return;												//when called from Print button on checkListDlg, the needPrint is true.
        }
        
        if(ipContentMap.get(printerIP) == null)
        	ipContentMap.put(printerIP,new ArrayList<String>());
        
        ipContentMap.get(printerIP).addAll(formatContentForInvoice(printerIP, billPanel, isCashBack, isToCustomer));
        
        printContents();
    }
    
    public static void exePrintVoid(BillPanel billPanel){
    	 flushIpContent();
         reInitPrintRelatedMaps();

         String printerIP = BarFrame.menuPanel.getPrinters()[0].getIp();
         if(ipContentMap.get(printerIP) == null)
         	ipContentMap.put(printerIP,new ArrayList<String>());
         ipContentMap.get(printerIP).addAll(
         		formatContentForVoid(printerIP, billPanel));
         printContents();
    }
    
    public static void exePrintRefund(BillPanel billPanel, int refundAmount){
        flushIpContent();
        reInitPrintRelatedMaps();

        String printerIP = BarFrame.menuPanel.getPrinters()[0].getIp();
        if(ipContentMap.get(printerIP) == null)
        	ipContentMap.put(printerIP,new ArrayList<String>());
        ipContentMap.get(printerIP).addAll(
        		formatContentForRefund(printerIP, billPanel, refundAmount));
        printContents();
    }
    
    public static void exePrintReport(String printerIP, List<String> formatedContent){
    	L.d("PrintService", "inside exePrint");
    	flushIpContent();
        reInitPrintRelatedMaps();
      
        if(ipContentMap.get(printerIP) == null)
        	ipContentMap.put(printerIP,new ArrayList<String>());
        ipContentMap.get(printerIP).addAll(formatedContent);
        printContents();
    }
    
    public static void exePrintOrderList(List<Dish> selectdDishAry, String curTable, String curBill, String waiterName, boolean isCancelled){

		Printer[] printers = BarFrame.menuPanel.getPrinters();

		//ErrorUtil.(TAG,"start to translate selection into ipContent for printing.");
        if(!isIpContentMapEmpty()){
        	printContents();
        	// when there's content left from last print, we will try to print it out, and success or not, we will add new content to queue;
        	// return;
        }
        
        reInitPrintRelatedMaps();
        
        //1、遍历每个选中的菜，并分别遍历加在其上的打印机。并在ipSelectionsMap上对应IP后面增加菜品
        for(Dish dish : selectdDishAry){
            String printerStr = dish.getPrinter();
            if(printerStr == null) {
            	continue;
            }
            String[] printerIdx = printerStr.split(",");
            String[] ips = new String[printerIdx.length];
            for(int i = 0; i < printerIdx.length; i++) {
            	if(printerIdx[i].length() > 0) {	//some dish might has no printer set.
            		if(printerIdx[i].lastIndexOf(".") > printerIdx[i].indexOf(".")) {
            			ips[i] = printerIdx[i];
            		}else {
            			ips[i] = mapToIP(printers, Integer.valueOf(printerIdx[i]));
            		}
            	}
            }
            for(String ip: ips) {
                Printer printer = ipPrinterMap.get(ip);
                if(printer == null) {                   //should never happen, just in case someone changed db.
                    L.d("Printing to Kitchen", "Selected dish not connected with any printer yet.");
                    continue;
                }
                ipSelectionsMap.get(ip).add(dish);
            }
        }

        //2、遍历ipSelectionsMap，如对应打印机type为0, 则对其后的value(dishes)按照类别进行排序
        for(Map.Entry entry: ipSelectionsMap.entrySet()){
            String key = (String)entry.getKey();
            List<Dish> dishList = (List<Dish>) entry.getValue();

            if(ipPrinterMap.get(key).getType() == 0 && dishList.size() > 0){
                //订单排序
                Collections.sort(ipSelectionsMap.get(key), new Comparator<Dish>() {
                    @Override
                    public int compare(Dish dish1, Dish dish2) {
                        String c1 = dish1.getCATEGORY();
                        String c2 = dish2.getCATEGORY();
                        int dspIdx = 0;
                        for(int i = 0; i < allCategory.length; i++) {
                        	if(c1.equals(allCategory[i].getLanguage()[CustOpts.custOps.getUserLang()])){
                        		dspIdx = i;
                        		break;
                        	}
                        }
                        for(int i = 0; i < allCategory.length; i++) {
                        	if(c2.equals(allCategory[i].getLanguage()[CustOpts.custOps.getUserLang()])){
                        		return dspIdx - i;
                        	}
                        }
                        return c1.compareTo(c2);
                    }
                });
            }
        }

        //3、再次遍历ipSelectionsMap, 封装打印信息
        for(Map.Entry entry: ipSelectionsMap.entrySet()){
            String printerIP = (String)entry.getKey();
            List<Dish> dishList = (List<Dish>) entry.getValue();

            if(dishList.size() > 0){
                if(ipSelectionsMap.get(printerIP) != dishList){
                    ErrorUtil.write("the dishList are different from ipSelectionsMap.get(printerIP)!");
                }

        		if (!"silent".equals(CustOpts.custOps.getValue("mode"))) {
        			ipContentMap.get(printerIP).add("Command.BEEP");
        		}
        		ipContentMap.get(printerIP).add("BigFont");
                if(ipPrinterMap.get(printerIP).getFirstPrint() == 1){  //全单封装
                    ipContentMap.get(printerIP).add(
                    		formatContentForOrder(dishList, printerIP, curTable, curBill, waiterName, isCancelled)
                    		+ "\n\n\n\n\n");
                    ipContentMap.get(printerIP).add("cut");
                }else{   												//分单封装
                    for(Dish dish : dishList){
                        List<Dish> tlist = new ArrayList<Dish>();
                        tlist.add(dish);
                        ipContentMap.get(printerIP).add(
                        		formatContentForOrder(tlist, printerIP, curTable, curBill, waiterName, isCancelled) + "\n\n");
                        ipContentMap.get(printerIP).add("cut");
                    }
                }
        		ipContentMap.get(printerIP).add("NormalFont");
            }
            //clear the ipSelectionsMap immediately
            ipSelectionsMap.get(printerIP).clear();
        }

        //L.d(TAG, "Order is translated into ipContentMap map and ready for print.");
        printContents();
    }
    
    private static String mapToIP(Printer[] printers, int id){
    	String ip = "";
    	for (Printer printer : printers) {
			if(printer.getId() == id) {
				return printer.getIp();
			}
		}
    	return ip;
    }
  
    private static int printContents() {
    	
    	BarFrame.setStatusMes(BarFrame.consts.printing());
    	
    	int errorsAmount = 0;
        for(Entry<String,List<String>> entry : ipContentMap.entrySet()) {
        	List<String> sndMsg = entry.getValue();
        	if(sndMsg.size() > 0) {
        		boolean printSuccessful = false;
        		String printerID = entry.getKey();
        		//the first printer (P1) might be printer connected by serial port. and might be through a mev device.
        		if("mev".equalsIgnoreCase(printerID)) {	// if it's for mev. before print, should change to unique "mev-format", no command inside.
             		printSuccessful = doMevPrint(sndMsg);
             	} else if("serial".equalsIgnoreCase(printerID)) { 
            		printSuccessful = doSerialPrint(sndMsg);
            	} else {//if it's not connected to the serial port.
            		printSuccessful = doWebSocketPrint(printerID, sndMsg);
            	}
            	
            	if(printSuccessful) {
            		sndMsg.clear();//clean ip content; no need to do ipContentMap.remove(entry.getKey()), because will do ipContentMap.clear later.
             	}else {
                    JOptionPane.showMessageDialog(BarFrame.instance, 
         	        		"Content NOT print!!! Please check printer and try again. --ip: "+entry.getKey());
                    errorsAmount ++;
             	}
        	}
        }
        return errorsAmount;
    }

	public static boolean openDrawer(){
		String key = BarFrame.menuPanel.getPrinters()[0].getIp();
		if(key.equalsIgnoreCase("mev")) {
			try{
				printThroughOSdriver(getMevCommandFilePath("mevOpenCashierCommand.xml", Command.OPEN_CASHIER), new HashPrintRequestAttributeSet(), false);
				return true;
			} catch (Exception exp) {
				ErrorUtil.write(exp);
				return false;
			}
			
		}else if (key.equalsIgnoreCase("serial")){	//TODO: not implemented yet.
			try{
				return true;
			} catch (Exception exp) {
				ErrorUtil.write(exp);
				return false;
			}
		}else {	//TODO: not tested yet. worked around year 2005
	    	try{
				Socket socket = new Socket(key != null ? key : BarFrame.menuPanel.getPrinters()[0].getIp(), 9100);
				OutputStream outputStream = socket.getOutputStream();
				outputStream.write(Command.DLE_DC4);
		
				outputStream.flush();
				socket.close();
				return true;
			} catch (Exception exp) {
				ErrorUtil.write(exp);
				return false;
			}
		}
    }
    
    private static boolean doMevPrint(List<String> sndMsg) {

    	//check if mev device and printer has been installed?
    	checkPrinter(DocFlavor.INPUT_STREAM.AUTOSENSE, new HashPrintRequestAttributeSet());

		//build the content, save contents into a file
		String filePath = CASUtility.getPIMDirPath().concat(new Date().getTime() + "transaction.xml");
		Path path = Paths.get(filePath);
		byte[] contentToWirteToFile;
		
    	//check the msg (it's a bill/check(6 item inside)  or a receipt/invoice (7 item inside)
    	String transType = checkTransType(sndMsg);	//we've added special content when prparing the content, in method:
    	
    	//base on different type cleaned useless part, to left only useful elements.
    	switch (transType) {
			case "V_RFER":		//voided receipt //TODO: when we reprint receipt, we should make the msg added with a new Item like "re-printed invoice".
				sndMsg.remove(7);
				sndMsg.remove(5);
	    		sndMsg.remove(3);
				contentToWirteToFile = transferToMevFormat(sndMsg, transType);
				break;
			
	    	case "ADDI":	//check
				sndMsg.remove(8);
	    		sndMsg.remove(7);
	    		sndMsg.remove(5);
				sndMsg.remove(3);
				String total = sndMsg.get(3);
				int p = total.indexOf(":");
				//NOTE:there's a \n at the end of total, use PreferedWidth() - (total.length() - 1)
				total = total.substring(0, p + 1) + BarUtil.generateString(BarUtil.getPreferedWidth() - (total.length() - 1), " ") + total.substring(p+1);
				sndMsg.remove(3);
				sndMsg.add(3, total);
				
				contentToWirteToFile = transferToMevFormat(sndMsg, ADDI);
				break;
				
			case "RFER":		//receipt
				sndMsg.remove(9);
	    		sndMsg.remove(8);
	    		sndMsg.remove(5);
	    		sndMsg.remove(3);
				contentToWirteToFile = transferToMevFormat(sndMsg, RFER);
				break;
				
			case "R_RFER":		//refund receipt //TODO: when we reprint receipt, we should make the msg added with a new Item like "re-printed invoice".
				sndMsg.remove(10);
	    		sndMsg.remove(9);
	    		sndMsg.remove(8);
	    		sndMsg.remove(6);
	    		sndMsg.remove(4);
				contentToWirteToFile = transferToMevFormat(sndMsg, transType);
				break;
				
			case "REPORT":		//report //TODO: when we reprint receipt, we should make the msg added with a new Item like "re-printed invoice.".
	    		sndMsg.remove(5);
	    		sndMsg.remove(4);

				contentToWirteToFile = buildMevReportContent(sndMsg);
				break;
				
			default:
				return false;
		}
    	
		return printMevFormattedContent(path, contentToWirteToFile);
	}

	private static boolean printMevFormattedContent(Path path, byte[] contentToWirteToFile) {
		//write content to file.
		try {
	    	Files.write(path, contentToWirteToFile);
        } catch (IOException e) {
        	ErrorUtil.write(e);
        	return false;
        }
		
		//print the file content out.
		boolean isSuccess = printThroughOSdriver(path, new HashPrintRequestAttributeSet(), !BarOption.isDebugMode());
		
		//print cut paper command.
		if(isSuccess) {
			isSuccess = printThroughOSdriver(getMevCommandFilePath("mevCutCommand.xml", Command.GS_V_m_n), new HashPrintRequestAttributeSet(), false);
		}
		
		return isSuccess;
	}

	private static String checkTransType(List<String> sndMsg) {
		if(sndMsg.size() == 6) {	//currently if it's REPORT, sndMsg has 6 element. 
			return "REPORT";
		}else if(sndMsg.size() == 8) {		//currently if it's VOID receipt/invoice, sndMsg has 11 element. 
			return "V_RFER";
		}else if(sndMsg.size() == 9) {		//currently if it's bill/check, sndMsg has 8 element. 
			return ADDI;
		}else if (sndMsg.size() == 10) {	//currently if it's original receipt/invoice, sndMsg has 9 element. 
			return RFER;
		}else if (sndMsg.size() == 11) {	//currently if it's Refund/invoice, sndMsg has 12 element. 
			return "R_RFER";
		}
		return "";
	}

	private static boolean printThroughOSdriver(Path path, PrintRequestAttributeSet pras,
			boolean deleteCommandFile) {
		try {
			DocPrintJob job = defaultService.createPrintJob();
	    	FileInputStream stream = new FileInputStream(path.toFile());
			job.print(new SimpleDoc(stream,  DocFlavor.INPUT_STREAM.AUTOSENSE, new HashDocAttributeSet()), pras);
			if(deleteCommandFile) {
				Files.deleteIfExists(path);
			}
			return true;
		} catch (PrintException e) {
			L.e("PrintService", ": Print through OS", e);
		} catch(IOException ioe) {
			L.e("PrintService", ": Print through OS", ioe);
		}
		return false;
	}

	private static byte[] buildMevReportContent(List<String> sndMsg) {
		//the contents could be composed by :1/Command.BEEP 2/BigFont 3/NormalFont 4/cut 5/content.
		//while when sending to mev device, the sndMsg could only be content. and length will always be 1.
		//we will make the content into 
		byte[] formattedContent = null;
		
		StringBuilder printContent = new StringBuilder(mevReportDocAutre1);		
		for (int i = 0; i < sndMsg.size(); i++) {					
			printContent.append(sndMsg.get(i));
		}
		
		printContent.append(mevReportDocAutre2);
		
		try {
			formattedContent = printContent.toString().getBytes("ASCII");
		}catch(UnsupportedEncodingException e) {
			ErrorUtil.write(e);
		}
		
		return formattedContent;
	}
	
	private static Path getMevCommandFilePath(String fileName, byte[] command) {
		String filePath = CASUtility.getPIMDirPath().concat(fileName);
		Path path = Paths.get(filePath);
		File file = new File(filePath);
		
		//check if it's exist.
		if(!file.exists()) {
			//creata the file
			try {
				byte[] a = mevReportDocAutre1.getBytes("ASCII");
				byte[] b = mevReportDocAutre2.getBytes("ASCII");
				byte[] c = new byte[a.length + command.length + b.length];
				System.arraycopy(a, 0, c, 0, a.length);
				System.arraycopy(command, 0, c, a.length, command.length);
				System.arraycopy(b, 0, c, a.length + command.length, b.length);
				Files.write(path, c);
			}catch(Exception e) {
				ErrorUtil.write(e);
			}
		}

		return path;
	}

	//the contents could be composed by :1/Command.BEEP 2/BigFont 3/NormalFont 4/cut 5/content.
	//while when sending to mev device, the sndMsg could only be content. and length will always be 1.
	private static byte[] transferToMevFormat(List<String> sndMsg, String transType) {
		
		byte[] formattedContent = null;//the byte ary we will make the content into 
		
		boolean isRefund = false;
		boolean isVoided = false;
		boolean needReference = transType.endsWith("RFER");
		boolean isOriginalInvoiceAndBillPrinted = false;
		//we don't care what type the message is, just go through every line of it. to get out all the information needed,
		//the first round of check, check if it's a original? reprint? or duplicate print of invoice?
		String duplicata = "N";// whether this is for internal used.• O (Yes) • N (No), By default, it's not a internal used bill, so duplicata = N
		String reimpression = "N";


		String numeroRef = null;//the bill number of dumpted/printed bill, @NOTE: when a bill printed, the numRef will be set, so the mev invoice can do only record, no print.
		String oldsubtotal = null; 
		String oldGST = null;
		String oldQST = null;
		String oldTotal = null;
		
		//find out the bill Number
		String billNumberStartStr = BarOption.getBillNumberStartStr();
		int startPos = sndMsg.get(0).indexOf(billNumberStartStr);
		String billID = sndMsg.get(0).substring(startPos + billNumberStartStr.length(), sndMsg.get(0).indexOf("\n", startPos)); //billID
		String numeroTrans = transType.endsWith("RFER") ? billNumberStartStr + billID + "F" :  billNumberStartStr + billID;
		String paiementTrans = "SOB";
		String refundvalue = "0";
		//make sure the number not too long.
		if(numeroTrans.length() > 10) {
			numeroTrans = billNumberStartStr + numeroTrans.substring(billNumberStartStr.length() + numeroTrans.length() - 10); 
		}
		
		String endMessage = sndMsg.get(sndMsg.size() - 1).trim();
		if(endMessage.startsWith(RE_PRINTED)) {	//if the first element is "*re-printed invoice*\n\n" then set to be duplicated.
			duplicata = "N"; 				//means for internal use only.
			reimpression = "O";	//set reprint flag.
			
			needReference = true;
			numeroRef = endMessage.substring(endMessage.indexOf("\n") + 1);
			numeroTrans = "R" + numeroTrans.substring(billNumberStartStr.length());
		}else if(endMessage.startsWith(RE_PRINTED_INTERNAL_USE)) {
			duplicata = "O";
			reimpression = "N";	//set reprint flag.
			
			needReference = true;
			numeroRef = endMessage.substring(endMessage.indexOf("\n") + 1);
			numeroTrans = "D" + numeroTrans.substring(billNumberStartStr.length());
		}else if(endMessage.startsWith(REF_TO)) {
			
			needReference = true;	//if there's a ref to, means we need to add a reference element at the end no matter it's a invoice of a bill.. 
			
			numeroRef = endMessage.substring(REF_TO.length());	//get out the number ref, and the subtotal before.
			int p = numeroRef.indexOf(OLD_SUBTOTAL);			//add another check to see if the oldSubtotal are different.
			if(p > 0) {
				oldsubtotal = numeroRef.substring(p + OLD_SUBTOTAL.length()).trim();
				numeroRef = numeroRef.substring(0, p).trim();
				p = oldsubtotal.indexOf(OLD_GST);
				if(p > 0) {
					oldGST = oldsubtotal.substring(p + OLD_GST.length());
					oldsubtotal = oldsubtotal.substring(0, p).trim();
					p = oldGST.indexOf(OLD_QST);
					if(p > 0) {
						oldQST = oldGST.substring(p + OLD_QST.length());
						oldGST = oldGST.substring(0, p);
						p = oldQST.indexOf(OLD_TOTAL);
						if(p > 0) {
							oldTotal = oldQST.substring(p + OLD_TOTAL.length());
							oldQST = oldQST.substring(0, p);
							p = oldTotal.indexOf(REF_TO);
							if(p > 0) {
								oldTotal = oldTotal.substring(0, p);
							}
						}
					}
				}
			}
			
			if(sndMsg.get(3).startsWith(REFUND)){	//if it's refund.
				transType = transType.substring(2);
				isRefund = true;	//modify the content to be negative.
				reimpression = "N";
				
				p = sndMsg.get(3).indexOf(METHOD);
				if(p > 0) {
					refundvalue = sndMsg.get(3).substring(REFUND.length(), p).trim();
					paiementTrans = sndMsg.get(3).substring(p + METHOD.length()).trim();
				}
				
			}else if(sndMsg.get(3).contains(VOID)){	//if it's Voided.
				transType = transType.substring(2);
				isVoided = true;	//modify the content to be 0.
				numeroTrans = billNumberStartStr + billID;
				
			}else if(oldsubtotal != null){
				
				
			}else if(numeroRef.endsWith(billID)){
				//NOTE: when bill printed, will put a ref to in the comment. and then it will go into the end message. and reach here.
				isOriginalInvoiceAndBillPrinted = true;	//it's invoice, and it's not reprinted, it's not void, it's not refund, and the ref is to it self, then it's it.
				
			}
		}else {
			needReference = false;
		}
		
		//need to know if it's printed, if printed, the first row must be the number of the bill.
		String etatDoc = BarOption.isSavePrintInvoiceWhenBilled() && isOriginalInvoiceAndBillPrinted ? "N" : "I";	//Whether the contents of the <doc> tag are present and if they must be printed.
												//• A (Absent and not to be printed)  • I (present and to be Printed)		• N (present but Not to be printed)
		
		String modeTrans = LoginDlg.MODETRANS;	// the mode of transactions • O (Operational)	• F (Training)   when user login with system user, or set into
												// "training mode" in setting view by manager. it' will be change to "F". until relogin with a normal user.
		
		//==========the first part=====================
		//mev1 = "<reqMEV><trans noVersionTrans="v0%s.00" etatDoc="%s" modeTrans="%s" duplicata="%s"><doc><texte><![CDATA[";
		StringBuilder printContent = new StringBuilder(String.format(mev1, 2, etatDoc, modeTrans, duplicata));	
		
		//==========the second part====================
		for (int i = 0; i < sndMsg.size(); i++) {
			printContent.append(sndMsg.get(i));
		}
		printContent.append(mev2);
		
		//==========the third part========
		String comptoir = BarOption.isFastFoodMode() ? "O" : "N";
		String autreCompte = "S";	//Identifies any sales recorded in a system other than the SRS.• F(Package deal)• G(Group event)• S Sans objet (N/A)

		String tableTrans = null;//"T1";
		String serveurTrans = null;//"Tao";
		String dateTrans = null;//"00000000000000";	//20090128084800
		//String dateRef = null;//dateTrans;		//should allways be same, so no need to give a new variable.
		String mtTransAvTaxes = null;//"+000000.00";	//+000021.85
		String mtRefAvTaxes = null;//mtTransAvTaxes;
		String TPSTrans = null;//"+000000.00";		//+000001.09
		String TVQTrans = null;//"+000000.00";		//+000001.72
		String mtTransApTaxes = null;//"+000000.00";	//+000024.66
		
		for (int i = 1; i < sndMsg.size(); i++) {
			String tText = sndMsg.get(i).trim();
			
			if(i == 1) {//find out the table and client and time and sub total and tps tpq
				String[] ary = tText.split(" ");
				if(ary.length > 0) {
					int s = ary[0].indexOf("(");
					if(s >= 0) {
						tableTrans = ary[0].substring(s + 1, ary[0].indexOf(")"));
					}
				}
				
				if(ary[2].length() > 0) {	//user is configurable, so the ary.length could be 3.
					serveurTrans = ary[2]; 
				}
				String time =  ary[ary.length - 1];
				String date =  ary[ary.length - 2];
				String[] tAry = time.split(":");
				String[] dAry = date.split("-");
				dateTrans = new StringBuilder(dAry[0]).append(dAry[1]).append(dAry[2]).append(tAry[0]).append(tAry[1]).toString();
				if(tAry.length == 3) {
					dateTrans  = dateTrans.concat(tAry[2].endsWith(")") ? tAry[2].substring(0,  tAry[2].indexOf("(")) : tAry[2]);
				}else {
					dateTrans  = dateTrans.concat("00");
				}
				//dateRef = dateTrans;
			}else if(i == 2) {	//money
				String tContent = tText.substring(tText.lastIndexOf(SOUSTOTAL)); //there's a "\n".
				String[] a = tContent.split("\n");
				if(a.length >= 3) {
					if(isRefund) {
						Float refund = Math.abs(Float.valueOf(refundvalue));	//@NOTE: have to make it a positive number to avoid Math.round(-108.5) = -108 instead of -109.
						int price = (int)(refund * 100);
						Object tps = BarOption.getGST();
			        	Object tvq = BarOption.getQST();
			        	float gstRate = tps == null ? 5f : Float.valueOf((String)tps);
			        	float qstRate = tvq == null ? 9.975f : Float.valueOf((String)tvq);
			        	price = (int)Math.round(price / ((100 + gstRate + qstRate) / 100.0));
			        	float floatPrice = (float)(price / 100.0);
			        	
						mtTransAvTaxes = formatMoneyForMev(BarUtil.formatMoney(floatPrice), null, isRefund);//+000021.85
						TPSTrans = formatMoneyForMev(BarUtil.formatMoney(Math.round(floatPrice * gstRate) / 100.0), null, isRefund);//+000001.09
						TVQTrans = formatMoneyForMev(BarUtil.formatMoney(Math.round(floatPrice * qstRate) / 100.0), null, isRefund);//+000001.72
						mtTransApTaxes = formatMoneyForMev(BarUtil.formatMoney(refund), null, isRefund);
					} else if (isVoided) {
						oldsubtotal = a[0].substring(a[0].indexOf(":") + 1);
						oldsubtotal = formatMoneyForMev(oldsubtotal, null, false);//+000021.85

						mtTransAvTaxes = formatMoneyForMev("0.00", null, false);//+000021.85
						TPSTrans = formatMoneyForMev("0.00", null, false);//+000001.09
						TVQTrans = formatMoneyForMev("0.00", null, false);//+000001.72
						mtTransApTaxes = formatMoneyForMev("0.00", null, false);
					} else {
						String strAvT = a[0].substring(a[0].indexOf(":") + 1);
						mtTransAvTaxes = formatMoneyForMev(strAvT, oldsubtotal, isRefund);//+000021.85
						String strTPS = a[1].substring(a[1].indexOf(":") + 1);
						TPSTrans = formatMoneyForMev(strTPS, oldGST, isRefund);//+000001.09
						String strTVQ = a[2].substring(a[2].indexOf(":") + 1);
						TVQTrans = formatMoneyForMev(strTVQ, oldQST, isRefund);//+000001.72
						Float total = Float.valueOf(strAvT) +  Float.valueOf(strTPS) +  Float.valueOf(strTVQ);
						mtTransApTaxes = formatMoneyForMev(BarUtil.formatMoney(total), oldTotal, isRefund);
					}
				}
			}else if(i == 3) {//find out the total
				if(isVoided) {	//this case is duplicated, because when i== 2, we have set the value for mtTransApTaxes.
					mtTransApTaxes = formatMoneyForMev("0.00", oldTotal, isRefund);
				} else {
					String total = tText.substring(tText.indexOf(":") + 1).trim();
					int p = total.indexOf(METHOD);
					if(p > 0) {
						total = total.substring(0, p).trim();
					}
					mtTransApTaxes = formatMoneyForMev(total, oldTotal, isRefund);
				}
			}else if(i == 4) { // find out the payment.
				if(!tText.startsWith(REF_TO)) { //the element at this position might be a ref(including old moneys) not for sure a paid methods.
					String[] a = tText.split("\n");
					if(a.length == 2) {		//we use I, because there's a line of "change" or "tip".
						paiementTrans = getMatechPaytrans(a[0]);
					}else if (a.length  > 2){
						paiementTrans = "MIX";
					}
				}
			}
		}

		HashMap<String, String> map = new HashMap<String, String>();//use this map to save every properties for mev.
		map.put("reimpression", reimpression);	//set reprint flag.
		map.put("typeTrans", transType);	//ADDI--ADDItion (Check)    RFER--Reçu de FERmeture (Closing receipt)
		//When a previously generated bill needs to be reprinted. The date and time displayed, must be exactly the same.
		//Only the attribute “reimpression” (reprint) will have a different value in the reprinting request.
		//The reprinting request must reproduce the references contained in the original request.
		
		map.put("autreCompte", autreCompte);	//the amount paid or to be paid (in full or in part) is recorded in a system
		//other than the SRS. possible values:• F(Package deal)• G(Group event) • S(N/A)
		
		map.put("serveurTrans", serveurTrans);	//Identifier of the waiter responsible for the transaction. 
		map.put("tableTrans", tableTrans);	//table associated with the transaction.
		map.put("paiementTrans", paiementTrans); //method of payment for the transaction.ARG ARGent (cash) • AUT(other) • CRE(credit card)• DEB(debit card)• MIX(mixed payment) • SOB(N/A)
		map.put("comptoir", comptoir);	//whether the operator uses  the SRS’ Counter service operating mode
		
		map.put("numeroTrans", numeroTrans);//Number of the current transaction. This number must match the one in the body of the bill ( in the <doc> tag).
		map.put("dateTrans", dateTrans);
		map.put("mtTransAvTaxes", mtTransAvTaxes);
		map.put("TPSTrans", TPSTrans);
		map.put("TVQTrans", TVQTrans);
		map.put("mtTransApTaxes", mtTransApTaxes);
		
		for (Entry<String, String> entry : map.entrySet()) {
			printContent.append(entry.getKey());
			printContent.append("=\"");
			printContent.append(entry.getValue());
			printContent.append("\" ");
		}

		printContent.append(mev3);
		
		//=======================the fourth part==========
		if(needReference) {
			//ref: This element is present for each transaction referenced by the current transaction.
			//The reference indicates a link between the current transaction and one or more prior transactions.
			//• Optional • Cardinality: 0 to N • This element is mandatory for all transactions (sale or credit) modifying a prior transaction. For more
			//information on the applicable conditions, see section 4 of Part 3 – Development standards and section 1.2 of this document.
	
			//numeroRef: Number of the referenced transaction. This attribute is an integral component of the unique identifier of each referenced transaction.
			//• Mandatory • Format: Limited ASCII. See the validation J3-02149E_ MES in Appendix F – Validation tests and error messages for more details on the characters accepted.
			//• See also details about this attribute in section 1.2.10 of this document.
			
			//dateRef: Date and time of the referenced transaction. This attribute is an integral component of the unique identifier of each referenced transaction.
			//• Mandatory • Format: YYYYMMDDhhmmss • See also details about this attribute in section 1.2.11 of this document.
	
			//mtRefAvTaxes: Total amount before taxes appearing on the referenced transaction.
			//• Mandatory • Format: +/–999999.99 • The following 10 characters are mandatory:
			
			//"<ref numeroRef=\"%s\" dateRef=\"%s\" mtRefAvTaxes=\"%s\"/>";//»AAAAMMJJhhmmss»//»+/-999999.99»
			
			mtRefAvTaxes = oldsubtotal == null ? mtTransAvTaxes : oldsubtotal;
			numeroRef = numeroRef == null? billNumberStartStr + billID : numeroRef;
			//@NOTE add a patch, because just found that if it's refund, the revenue test case do not want a refrence. I don't understand why they don't need the ref, to me
			//it make more sense if a refund bill has a ref to the bill which was refunded.......
			if(!isRefund) {
				printContent.append(String.format(mevRef, numeroRef, dateTrans, mtRefAvTaxes));
			}
		}
		
		printContent.append(mevEnd);
		
		//=====================convert to ASCII and return========================
		try {
			formattedContent = printContent.toString().getBytes("ASCII");
		}catch(UnsupportedEncodingException e) {
			ErrorUtil.write(e);
		}
		
		return formattedContent;
	}
	
    private static String getMatechPaytrans(String string) {
		if(string == null || string.length() == 0) {
			return "SOB";
		}else {
			switch (string.substring(0, string.indexOf(":")).trim()) {
			case CASH:
				return "ARG";
			case "DEBIT":
				return "DEB";
			case "VISA":
				return "CRE";
			case "MASTER":
				return "CRE";
			default:
				return "AUT";
			}
		}
	}

	private static String formatMoneyForMev(String newValue, String oldValue, boolean isRefund) {
		float finalValue = Float.valueOf(newValue) - (oldValue == null ? 0.0f : Float.valueOf(oldValue));
		newValue = BarUtil.formatMoney(finalValue);
		
		StringBuilder stringFR = new StringBuilder();
    	String cleanText = newValue.trim();
    	
    	if(newValue.startsWith("-")) {
    		cleanText = cleanText.substring(1);
    		stringFR.append("-");
    	} else if(isRefund) {
    		stringFR.append("-");
    	} else {
    		stringFR.append("+");
    	}
    	
    	for(int i = 1; i < 10 - cleanText.length(); i++) {
    		stringFR.append("0");
    	}
		return stringFR.append(cleanText).toString();
	}

	private static boolean doSerialPrint(List<String> sndMsg) {
        CommPortIdentifier commPortIdentifier;
        SerialPort tSerialPort = null;
        DataOutputStream outputStream = null;
        try {
            Enumeration tPorts = CommPortIdentifier.getPortIdentifiers();
            if (tPorts == null || !tPorts.hasMoreElements()) {
            	JOptionPane.showMessageDialog(BarFrame.instance, "no comm ports found! please check the printer connection.");
                return false;
            }

            while (tPorts.hasMoreElements()) {
                commPortIdentifier = (CommPortIdentifier) tPorts.nextElement();
                if (commPortIdentifier.getPortType() != CommPortIdentifier.PORT_SERIAL)
                    continue;
                
                //if (!commPortIdentifier.isCurrentlyOwned()) {
                    tSerialPort = (SerialPort)commPortIdentifier.open("PrintService", 10000);//并口用"ParallelBlackBox"
                    outputStream = new DataOutputStream(tSerialPort.getOutputStream());
                    for (String string : sndMsg) {
            			sendContentOutThroughStream(string, outputStream);
					}
//                    outputStream.write(27); // 打印机初始化：
//                    outputStream.write(64);
//
//                    char[] tTime = pDate.toCharArray(); // 输出日期时间 输出操作员工号
//                    for (int i = 0; i < tTime.length; i++)
//                        outputStream.write(tTime[i]);
//
//                    outputStream.write(13); // 回车
//                    outputStream.write(10); // 换行
//                    outputStream.write(10); // 进纸一行
//
//                    outputStream.write(28); // 设置为中文模式：
//                    outputStream.write(38);
//                    String tContent = ((String) CustOpts.custOps.getValue(PosDlgConst.PrintTitle)).concat("\n");
//                    for (int i = 0, len = getUsedRowCount(); i < len; i++) { // 遍历有效行。
//                        tContent = tContent.concat((String) tblContent.getValueAt(i, 1)).concat("\n"); // 再取出品名
//                        tContent = tContent.concat((String) tblContent.getValueAt(i, 3)).concat("   "); // 再取出单价
//                        tContent = tContent.concat((String) tblContent.getValueAt(i, 2)).concat("   "); // 再取出数量
//                        tContent = tContent.concat((String) tblContent.getValueAt(i, 4)).concat("\n"); // 再取出小计
//                    }
//                    for (int i = 0; i < 4; i++)
//                        // 换行
//                        tContent = tContent.concat("\n");
//
//                    tContent = tContent.concat(PosDlgConst.SumTotal);
//                    tContent = tContent.concat(tfdShoudReceive.getText());
//                    tContent = tContent.concat(PosDlgConst.Unit).concat("   ");// 总计
//
//                    tContent = tContent.concat(PosDlgConst.Receive);
//                    tContent = tContent.concat(tfdActuallyReve.getText());
//                    tContent = tContent.concat(PosDlgConst.Unit).concat("\n");// 收银
//
//                    tContent = tContent.concat(PosDlgConst.Change);
//                    tContent = tContent.concat(tfdChange.getText());
//                    tContent = tContent.concat(PosDlgConst.Unit);// 找零
//
//                    tContent =
//                            tContent.concat("\n\n      ").concat(
//                                    (String) CustOpts.custOps.getValue(PosDlgConst.Thankword));
//                    tContent = tContent.concat("\n\n");
//
//                    Object tEncodType = CustOpts.custOps.getValue(PosDlgConst.EncodeStyle);
//                    if (tEncodType == null)
//                        tEncodType = "GBK";
//                    if (!Charset.isSupported(tEncodType.toString()))
//                        return;
//                    BufferedWriter tWriter =
//                            new BufferedWriter(new OutputStreamWriter(outputStream, tEncodType.toString()));
//                    tWriter.write(tContent);
//                    tWriter.close();
                    
        			return true;
                //}
            }
            return false;
        } catch (Exception e) {
			ErrorUtil.write(e);
			L.e("LPT printing", "Error when printing content to LPT.", e);
			return false;
        }finally {
        	if(outputStream != null) {
        		try {
        			outputStream.close();
        		}catch(Exception exp) {
        			System.out.println(exp);
					L.e("serial", "Error when trying to close outputString of serial port", exp);
        		}
        	}
        	if(tSerialPort != null) {
        		try {
        			tSerialPort.close();
        		}catch(Exception e) {
        			System.out.println(e);
					L.e("serial", "Error when trying to close serial port", e);
        		}
        	}
        }
    }
    
    private static boolean doWebSocketPrint(String ip, List<String> sndMsg){
    	if(ip == null || ip.length() == 0)
    		return false;
    	
    	String[] ipAry = ip.split("\\.");
    	if(ipAry.length != 4) {
    		L.e("Invalidate Ip found in doWebSocketPrint, the ip is:", ip, null);
    		return false;
    	}
    	
    	Socket socket = null;
    	OutputStream outputStream = null;
		try {
			InetAddress inet = InetAddress.getByAddress(new byte[] {
	        		Short.valueOf(ipAry[0]).byteValue(), Short.valueOf(ipAry[1]).byteValue(), 
	        		Short.valueOf(ipAry[2]).byteValue(), Short.valueOf(ipAry[3]).byteValue()});  
	        if(!inet.isReachable(BarOption.getPrinterMinWaiTime())) {
	        	L.e("printer slow. ip is: ", ip, null);
	        	return false;
	        }
	        
			socket = new Socket(ip, 9100);
			outputStream = socket.getOutputStream();
			for (String msg : sndMsg) {
				sendContentOutThroughStream(msg, outputStream);
			}
			outputStream.close();
			socket.close();
			return true;
		} catch (Exception exp) {
			ErrorUtil.write(exp);
			L.e(ip, "Error when printing content to socket", exp);
			return false;
		} finally {
			if(outputStream != null) {
				try {
					outputStream.close();
				}catch(Exception exp) {
					L.e(ip, "Error when trying to close outputStream", exp);
				}
			}
			if(socket != null) {
				try {
					socket.close();
				}catch(Exception exp) {
					L.e(ip, "Error when trying to close socket", exp);
				}
			}
		}
    }

	private static void sendContentOutThroughStream(String sndMsg, OutputStream outputStream)
			throws IOException {
		if("Command.BEEP".equals(sndMsg)) {
			outputStream.write(Command.BEEP);
		}else if("BigFont".equals(sndMsg)) {

	        String font = (String)CustOpts.custOps.getValue(curPrintIp + "font");
	        if(font ==  null || font.length() < 1) {
	            font = (String)CustOpts.custOps.getValue("font");
	        }
	        
			if (font == null || font.length() < 1) {
				outputStream.write(Command.GS_ExclamationMark);
			} else {
				// default: "27, 33, 48" because it works for both thermal and non-thermal
				String[] pieces = font.split(",");
				if (pieces.length != 3) {
					outputStream.write(Command.GS_ExclamationMark);
				} else {
					for (int i = 0; i < 3; i++) {
						Command.GS_ExclamationMark[i] = Integer.valueOf(pieces[i].trim()).byteValue();
					}
					outputStream.write(Command.GS_ExclamationMark);
				}
			}
		}else if("NormalFont".equals(sndMsg)) {
			outputStream.write(Command.ESC_ExclamationMark);
		}else if("cut".equals(sndMsg.trim())){
			// cut the paper.
			outputStream.write(Command.GS_V_m_n);
		}else {
			// code can be customized
			String charset = (String)CustOpts.custOps.getValue("code");
			if (charset == null || charset.length() <= 2) {
				charset = "GBK";
			}
			 if(sndMsg != null) {
			    byte[] send;
			    try {
			        send = sndMsg.getBytes(charset);
			    } catch (UnsupportedEncodingException var5) {
			    	ErrorUtil.write("Can not conver with code:" + charset);
			        send = sndMsg.getBytes();
			    	ErrorUtil.write("content to print will be:" + send);
			    }
			    outputStream.write(send);
			}
		}
		outputStream.flush();
	}
    
    private static StringBuilder formatContentForBill(List<Dish> list, String curPrintIp, BillPanel billPanel, int tWidth){
    	StringBuilder contentFR = getServiceDetailContent(list, curPrintIp, billPanel, tWidth);
    	contentFR.append(getTotalContent(billPanel));
    	return contentFR;
    }

    private static ArrayList<String> formatContentForInvoice(String curPrintIp, BillPanel billPanel, boolean isCashBack, boolean isToCustomer){
    	ArrayList<String> strAryFR = new ArrayList<String>();
    	int tWidth = BarUtil.getPreferedWidth();
    	
    	//push head info
	    pushBillHeadInfo(strAryFR, tWidth, String.valueOf(billPanel.getBillId()));
	    
	    //push table, bill waiter and time
	    String tableIdx = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
	    StringBuilder startTimeStr = new StringBuilder(BarFrame.instance.valStartTime.getText());
	    if(billPanel.billButton != null) {
	    	startTimeStr.append("(").append(billPanel.billButton.getText()).append(")");
	    }
	    pushWaiterAndTime(strAryFR, tWidth, tableIdx, startTimeStr.toString(), "");
	    
	    //service details
	    String content = getServiceDetailContent(billPanel.orderedDishAry, curPrintIp, billPanel, tWidth).append(getTotalContent(billPanel)).toString();
	    int pEnd = content.lastIndexOf("\n");
	    int pStart = content.substring(0, pEnd).lastIndexOf("\n");//ignore the "\n" at the end
	    String lastRow = content.substring(pStart, pEnd);
	    strAryFR.add(content.substring(0, pStart + 1));
        strAryFR.add("BigFont");	    //push bigger font. while it will not work for mev.
        strAryFR.add(lastRow);
        strAryFR.add("NormalFont");     //push normal font
        
        //payInfo
        strAryFR.add(getOutPayInfo(billPanel, tWidth, isCashBack));
        
        // when formatting invoice for different status of bill(original, printted, completed) will set different foot message..
    	// so when the message was fetched out from map, if it's to be print in mev format, we'll know how to process it.
        // and even if it's not printed through mev, it's still in good format.
    	if(billPanel.status >= DBConsts.completed) {
    		String comment = isToCustomer ? RE_PRINTED : RE_PRINTED_INTERNAL_USE;
    		strAryFR.add(comment + billPanel.comment);	//@NOTE: no need to set ref number, should be same.
        	
    	}else if(billPanel.comment.length() >= 0) {
    		strAryFR.add("\n" + billPanel.comment);	//has comment means bill printed(will be co-considered when saving paper flag is set) or invoice was reopened,
    		
    	}else {
        	pushEndMessage(strAryFR);	//only original bill print will display foot message.
        }
    	
        strAryFR.add("\n\n\n\n\n");
        strAryFR.add("cut");
        return strAryFR;
    }
    
    private static ArrayList<String> formatContentForVoid(String curPrintIp, BillPanel billPanel){
    	ArrayList<String> strAryFR = new ArrayList<String>();
    	int tWidth = BarUtil.getPreferedWidth();
    	
	    pushBillHeadInfo(strAryFR, tWidth, String.valueOf(billPanel.getBillId()));
	    
	    String tableIdx = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
	    StringBuilder startTimeStr = new StringBuilder(BarFrame.instance.valStartTime.getText());
	    pushWaiterAndTime(strAryFR, tWidth, tableIdx, startTimeStr.toString(), "");
	    
	    strAryFR.add(getServiceDetailContent(billPanel.orderedDishAry, curPrintIp, billPanel, tWidth).toString());
	    
        pushVoidedTotal(billPanel, strAryFR, tWidth);
        
        if(billPanel.comment.length() >= 0) {
    		strAryFR.add(billPanel.comment);	//has comment means bill printed(will be co-considered when saving paper flag is set) or invoice was reopened,
        }else {
        	pushEndMessage(strAryFR);
        }
        
        strAryFR.add("\n\n\n\n\ncut");
        return strAryFR;
    }
    
    private static ArrayList<String> formatContentForRefund(String curPrintIp, BillPanel billPanel, int refundAmount){
    	ArrayList<String> strAryFR = new ArrayList<String>();
    	int tWidth = BarUtil.getPreferedWidth();
    	
	    pushBillHeadInfo(strAryFR, tWidth, String.valueOf(billPanel.getBillId()));
	    String tableIdx = BarFrame.instance.cmbCurTable.getSelectedItem().toString();
	    StringBuilder startTimeStr = new StringBuilder(BarFrame.instance.valStartTime.getText());
	    pushWaiterAndTime(strAryFR, tWidth, tableIdx, startTimeStr.toString(), "");
	    
	    strAryFR.add(getServiceDetailContent(billPanel.orderedDishAry, curPrintIp, billPanel, tWidth).toString());
	    String payMethodInfo = getOutPayInfo(billPanel, tWidth, false);
	    String a[] = payMethodInfo.trim().split("\n");
	    String payMethod = "";
	    if(a.length == 2) {		//we use I, because there's a line of "change" or "tip".
	    	payMethod = getMatechPaytrans(a[0]);
		}else if (a.length  > 2){
			payMethod = "MIX";
		}
        pushRefundAndNewTotal(billPanel, strAryFR, refundAmount, payMethod, tWidth);
        
        strAryFR.add(billPanel.comment);
    	strAryFR.add("\n##REFUND##\n\n");
        strAryFR.add("\n\n\n\n\n");
        strAryFR.add("cut");
        return strAryFR;
    }
    
    public static ArrayList<String> formatContentForReport(List<Bill> list, String curPrintIp, String startTime, String endTime){
    	ArrayList<String> strAryFR = new ArrayList<String>();
    	int tWidth = BarUtil.getPreferedWidth();
        
        //initContent
        //content
  		int refundCount = 0,refoundAmount = 0;
  		int salesGrossCount = 0,salesGrossAmount = 0;
  		float net = 0, HST = 0;
  		
    	float gstRate = Float.valueOf(BarOption.getGST());
    	float qstRate = Float.valueOf(BarOption.getQST());
    	
  		for (Bill bill : list) {
  			int status = bill.getStatus();
  			if(status < DBConsts.original) {//means has refund)
  				refundCount++;
  				status = -1 * status;
  				refoundAmount += status;
  				salesGrossAmount -= status;
  			}
			salesGrossCount++;
			salesGrossAmount += bill.getCashReceived() + bill.getDebitReceived()
			+ bill.getVisaReceived() + bill.getMasterReceived() + bill.getOtherReceived() + bill.getCashback();
  		}
  		//@NOTE times goes first, to make the number bigger, then do divide. to avoid lost number after ".".
  		net = salesGrossAmount * 100 /(100 + gstRate + qstRate);
  		HST = net * (gstRate + qstRate)/100;
  		//
      		
	    //pushBillHeadInfo(strAryFR, tWidth, null);
	    pushWaiterAndTime(strAryFR, tWidth, null, startTime, endTime);
        pushSalesSummary(strAryFR, list, tWidth,
        		String.valueOf(salesGrossCount), String.valueOf(refundCount), 
        		BarUtil.formatMoney(salesGrossAmount / 100.0),
        		BarUtil.formatMoney(refoundAmount / 100.0),
        		BarUtil.formatMoney(net / 100.0),
        		BarUtil.formatMoney(HST / 100.0),
        		BarUtil.formatMoney((net + HST) / 100.0)
        		);
        pushPaymentSummary(strAryFR, list, tWidth);
        pushSummaryByServiceType(strAryFR, list, tWidth);
        pushVoidItemSummary(strAryFR, tWidth, startTime, endTime);
        pushOtherSummary(list);
        strAryFR.add("\n\n\n\n\n");
        strAryFR.add("cut");
        return strAryFR;
    }
    
	private static void pushBillHeadInfo(List<String> strAryFR, int tWidth, String billID) {
		StringBuilder content = getFormattedBillHeader(tWidth, billID);
        content.append("\n");
        //push bill header
        strAryFR.add(content.toString());
	}
	
	private static void pushWaiterAndTime(List<String> strAryFR, int tWidth, String tableIdx, String sartTimeStr, String endTimeStr) {
		StringBuilder content = new StringBuilder();
		//table
		if(tableIdx != null && tableIdx.trim().length() > 0) {
			content.append("(").append(tableIdx).append(")");
		}
        //bill index
        if(!BarUtil.isMoreThanOneBill()) {
        	content.append(" ");
        }else {
        	content.append(sartTimeStr);
        }
        
        //waiter
        String waiterName = LoginDlg.USERNAME;
        if(waiterName.length() > 10) {
        	waiterName = waiterName.substring(0,8) + "..";
        }
        content.append(" ").append(waiterName).append(" ");
        
        //space and times
        int lengthOfSpaceBeforeTime = tWidth - content.length() - sartTimeStr.length() - endTimeStr.length() - 3;
        if(lengthOfSpaceBeforeTime > 0) {
        	String spaceStr = BarUtil.generateString(lengthOfSpaceBeforeTime, " ");
        	content.append(spaceStr).append(sartTimeStr).append("   ").append(endTimeStr);;
        }else {
        	String spaceStr = BarUtil.generateString(tWidth - sartTimeStr.length() - endTimeStr.length() - 3, " ");
        	content.append("\n").append(spaceStr).append(sartTimeStr).append("   ").append(endTimeStr);
        }
        content.append("\n");
        strAryFR.add(content.toString());
	}
	
	private static StringBuilder getServiceDetailContent(List<Dish> dishList, String curPrintIp, BillPanel billPanel, int tWidth) {
        //bills---classify the dishes into different bill. in case the content of the bill is combined.
        HashMap<Integer, ArrayList<Dish>> billDishesMap = new HashMap<Integer, ArrayList<Dish>>();
        for(Dish dish:dishList){
        	ArrayList<Dish> dishes = billDishesMap.get(dish.getBillID());
        	if(dishes == null) {
        		dishes = new ArrayList<Dish>();
        		dishes.add(dish);
        		billDishesMap.put(dish.getBillID(), dishes);
        	}else {
        		dishes.add(dish);
        	}
        }
        
        //get the value of current bill ready (current bill might be a combined bill.
        String[] strs = billPanel.lblSubTotle.getText().split(":");
        String strSubTotal = strs[1].trim().substring(1);
        Float fSubTotal = Float.valueOf(strSubTotal);

        strs = billPanel.lblTPS.getText().split(":");
        String strTPS = strs[1].trim().substring(1);
        Float fTPS = Float.valueOf(strTPS);

        strs = billPanel.lblTVQ.getText().split(":");
        String strTVQ = strs[1].trim().substring(1);
        Float fTVQ = Float.valueOf(strTVQ);
        
        Float fServiceFee = 0.0f;
        
        String strServiceFee = billPanel.lblServiceFee.getText();
		if(strServiceFee.length() > 0) {
        	strs = strServiceFee.split(":");
        	strServiceFee = strs[1].trim().substring(1);
        	fServiceFee = Float.valueOf(strServiceFee);
        }

        Float fDiscount = 0.0f;
        String strDiscount = billPanel.lblDiscount.getText();
		if(strDiscount.length() > 0) {
        	strs = strDiscount.split(":");
        	strDiscount = strs[1].trim().substring(2);
        	fDiscount = Float.valueOf(strDiscount);  //@NOTE:an "-" is displayed before the number of discount on totalArea.
        }

		StringBuilder content = new StringBuilder();
        if(billDishesMap.size() == 1) {
            formatDishListContent(dishList, curPrintIp, tWidth, content,
            		strSubTotal, strTPS, strTVQ,
            		fServiceFee > 0 ? strServiceFee : null,
            		fDiscount > 0 ? strDiscount : null);
        }else {
        	//@NOTE:in case the original bill was used again after combination, so the values of the bill record could changed.
        	//so have to use the dish list to calculate every thing again,  while the service fee and discount will be problem.
        	//to avoid the mismatch in bill, will average the service fee and discount.
	        for (Entry<Integer, ArrayList<Dish>> entry : billDishesMap.entrySet()) {
	        	//apply list into a new panel, and calculate the subtotal and taxes.
        		BillPanel bp = new BillPanel(null);
        		bp.orderedDishAry = entry.getValue();
        		bp.updateTotleArea();
        		
        		formatDishListContent(entry.getValue(), curPrintIp, tWidth, content,
	            		BarUtil.formatMoney(bp.subTotal / 100.0),
	            		BarUtil.formatMoney(bp.totalGst / 100.0),
	            		BarUtil.formatMoney(bp.totalQst / 100.0),
	            		BarUtil.formatMoney(fServiceFee/billDishesMap.size()),
	            		BarUtil.formatMoney(fDiscount/billDishesMap.size()));
			}
	        
        	//it's a combined bill, so need to add an extra total part.	
	        String sep_str1 = (String)CustOpts.custOps.getValue("sep_str1");
	        if(sep_str1 == null || sep_str1.length() == 0){
	            sep_str1 = SEP_STR1;
	        }
	        content.append(BarUtil.generateString(tWidth, sep_str1)).append("\n\n");
	        
	        //add the tooooootal.
	        String subtotal = BarUtil.formatMoney(fSubTotal);
	        content.append(SOUSTOTAL).append(" : ")
	        	.append(BarUtil.generateString(tWidth - subtotal.length() - SOUSTOTAL.length() - 3, " "))
	        	.append(subtotal).append("\n");
	        
	        String tps = String.valueOf(fTPS);
	        content.append(TPS).append(" : ")
	    		.append(BarUtil.generateString(tWidth - tps.length() - TPS.length() - 3, " "))
	        	.append(tps).append("\n");
	        
	        String tvq = String.valueOf(fTVQ);
	        content.append(TVQ).append(" : ")
				.append(BarUtil.generateString(tWidth - tvq.length() - TVQ.length() - 3, " "))
	        	.append(tvq).append("\n");
	        
	        if(fServiceFee > 0) {
	            String serviceFee = String.valueOf(fServiceFee);
	            content.append(SERVICE_FEE).append(" : ")
					.append(BarUtil.generateString(tWidth - serviceFee.length() - SERVICE_FEE.length() - 3, " "))
	            	.append(serviceFee).append("\n");
	        }
	        if(fDiscount > 0) {
	            String dicount = String.valueOf(fDiscount);
	            content.append(DISCOUNT).append(" : ")
					.append(BarUtil.generateString(tWidth - dicount.length() - DISCOUNT.length() - 3, " "))
	            	.append(dicount).append("\n");
	        }
        }
        return content;
	}

	//generate an ===== items ----- subTotal taxes area 
	private static void formatDishListContent(List<Dish> dishList, String curPrintIp, int tWidth,
			StringBuilder content, String subTotal, String tps, String tvq, String serviceFee, String discount) {
		//seperator
        String sep_str1 = (String)CustOpts.custOps.getValue("sep_str1");
        if(sep_str1 == null || sep_str1.length() == 0){
            sep_str1 = SEP_STR1;
        }
        String sep_str2 = (String)CustOpts.custOps.getValue("sep_str2");
        if(sep_str2 == null || sep_str2.length() == 0){
            sep_str2 = SEP_STR2;
        }
        
		content.append(BarUtil.generateString(tWidth, sep_str1)).append("\n\n");
        int langIndex = ipPrinterMap.get(curPrintIp).getType();
        for(Dish d:dishList){
            StringBuilder sb = new StringBuilder();
            
            if(BarOption.isDisDishIDInKitchen()) {
                sb.append(d.getId());
                sb.append(BarUtil.generateString(5 - String.valueOf(d.getId()).length(), " "));
            }
            
            String dishName = d.getLanguage(langIndex); 
            sb.append(dishName);
            
            if(d.getNum() > 1){//NOTE: the number could be bigger than 10000.
            	sb.append(" ").append(Dish.getDisplayableNum(d.getNum()));
            }
            
            String price = BarUtil.formatMoney(d.getTotalPrice()/100f);
            int occupiedLength = BarUtil.getLengthOfString(sb.toString());
            sb.append(BarUtil.generateString(tWidth - occupiedLength - (price.length()), " "));
            sb.append(price);
            content.append(sb);

            content.append("\n");
        }
        
        //seperator
        content.append(BarUtil.generateString(tWidth, sep_str2)).append("\n");
        
        //add total part.
        //totals
        
        //String strSubtotal = BarUtil.format(subtotal/100f);
        content.append(SOUSTOTAL).append(" : ")
        	.append(BarUtil.generateString(tWidth - subTotal.length() - SOUSTOTAL.length() - 3, " "))
        	.append(subTotal).append("\n");
        
        content.append(TPS).append(" : ")
    		.append(BarUtil.generateString(tWidth - tps.length() - TPS.length() - 3, " "))
        	.append(tps).append("\n");
       
        content.append(TVQ).append(" : ")
			.append(BarUtil.generateString(tWidth - tvq.length() - TVQ.length() - 3, " "))
        	.append(tvq).append("\n");
        
        if(serviceFee != null && serviceFee.length() > 0 && !"0.00".equals(serviceFee)) {
            content.append(SERVICE_FEE).append(" : ")
				.append(BarUtil.generateString(tWidth - serviceFee.length() - SERVICE_FEE.length() - 3, " "))
            	.append(serviceFee).append("\n");
        }
        if(discount != null && discount.length() > 0 && !"0.00".equals(discount)) {
            content.append(DISCOUNT).append(" : ")
				.append(BarUtil.generateString(tWidth - discount.length() - DISCOUNT.length() - 3, " "))
            	.append(discount).append("\n");
        }
	}

	private static StringBuilder getTotalContent(BillPanel billPanel) {
		StringBuilder content = new StringBuilder();
        
        //push total
        String strTotal = billPanel.valTotlePrice.getText();
        content.append("Total").append(" : ")//.append(BarUtil.generateString(tWidth - strTotal.length() - Total.length() - 3 - 1, " "))
			.append(strTotal).append("\n");
        
        return content;
	}

	private static void pushRound(BillPanel billPanel, ArrayList<String> strAryFR, int tWidth) {
		StringBuilder content = new StringBuilder();
        //push total
        String strTotal = roundCent(billPanel.valTotlePrice.getText());
        content.append("Round").append(" : ").append(BarUtil.generateString(tWidth - 9 - strTotal.length(), " "))
			.append(strTotal).append("\n");
        strAryFR.add(content.toString());
	}
	
	private static void pushVoidedTotal(BillPanel billPanel, ArrayList<String> strAryFR, int tWidth) {
	    //push new totall
		//push bigger font. while it will not work for mev.
        strAryFR.add("BigFont");
        //push NEW total
		//push refund into ary.
        StringBuilder content = new StringBuilder("          ****Voided****").append("\n").append("Total : 0.00").append("\n");
        strAryFR.add(content.toString());
        //push normal font
        strAryFR.add("NormalFont");
	}
	
	private static void pushRefundAndNewTotal(BillPanel billPanel, ArrayList<String> strAryFR, int refund, String method, int tWidth) {
		StringBuilder content = new StringBuilder();
		//push refund into ary.
		String refundStr = BarUtil.formatMoney(refund/100f);
	    content.append(REFUND).append(BarUtil.generateString(tWidth - 9 - refundStr.length(), " ")).append(refundStr).append("\n");
	    content.append(METHOD).append(BarUtil.generateString(tWidth - METHOD.length() - method.length(), " ")).append(method).append("\n");
	    strAryFR.add(content.toString());    
	    
	    //push new totall
		//push bigger font. while it will not work for mev.
        strAryFR.add("BigFont");
        //push NEW total
        String strTotal = billPanel.valTotlePrice.getText();
        String newTotal = BarUtil.formatMoney(Float.valueOf(strTotal) + Float.valueOf(refundStr));
        content = new StringBuilder("Total : ").append(newTotal).append("\n");
        strAryFR.add(content.toString());
        //push normal font
        strAryFR.add("NormalFont");
	}
	
	private static String getOutPayInfo(BillPanel billPanel, int width, boolean isCashBack) {

		StringBuilder content = new StringBuilder("\n");
		int billId = billPanel.getBillId();
		
    	StringBuilder sb = new StringBuilder("select * from bill where id = " + billId);
    	try {
    		ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sb.toString());
            rs.next();
            float total = (float) (rs.getInt("total") / 100.0);
            int cashReceived = rs.getInt("cashReceived");
            String str;
            if(cashReceived > 0) {
            	str = BarUtil.formatMoney(cashReceived/100f);
    			content.append(CASH).append(" : ")
    			.append(BarUtil.generateString(width - 9 - str.length(), " "))
    			.append(str).append("\n");
    		}
            int debitReceived = rs.getInt("debitReceived");
            if(debitReceived > 0) {
                str = BarUtil.formatMoney(debitReceived/100f);
    			content.append("DEBIT").append(" : ")
    			.append(BarUtil.generateString(width - 8 - str.length(), " "))
    			.append(str).append("\n");
    		}
            int visaReceived = rs.getInt("visaReceived");
            if(visaReceived > 0) {
                str = BarUtil.formatMoney(visaReceived/100f);
    			content.append("VISA").append(" : ")
    			.append(BarUtil.generateString(width - 7 - str.length(), " "))
    			.append(str).append("\n");
    		}
            int masterReceived = rs.getInt("masterReceived");
        	if(masterReceived > 0) {
                str = BarUtil.formatMoney(masterReceived/100f);
    			content.append("MASTER").append(" : ")
    			.append(BarUtil.generateString(width - 9 - str.length(), " "))
    			.append(str).append("\n");
    		}

            int otherReceived = rs.getInt("otherReceived");
        	if(otherReceived > 0) {
                str = BarUtil.formatMoney(otherReceived/100f);
    			content.append("OTHER").append(" : ")
    			.append(BarUtil.generateString(width - 9 - str.length(), " "))
    			.append(str).append("\n");
    		}
        	
            float left = -1 * ((int)((total * 100 - cashReceived - debitReceived - visaReceived - masterReceived - otherReceived)));
            str = BarUtil.formatMoney(left/100f);
            String lblText;
            if(isCashBack) {
            	lblText = "MONNAIE : ";
            	content.append(lblText)
 				.append(BarUtil.generateString(width - lblText.length() - str.length(), " "))
 				.append(str).append("\n");
            	
            	String roundedStr = roundCent(str);
            	if(!roundedStr.equals(str)) {
            		lblText = "ARRONDI DE MONTANT : ";	//rounded price
            		content.append(lblText)
     				.append(BarUtil.generateString(width - lblText.length() - roundedStr.length(), " "))
     				.append(roundedStr).append("\n");
            	}
            }else {
            	lblText = "TIP : ";
            	content.append(lblText)
 				.append(BarUtil.generateString(width - lblText.length() - str.length(), " "))
 				.append(str).append("\n");
            }
            

    	}catch(Exception e) {
    		ErrorUtil.write(e);
    	}
    	
		return content.toString();
	}
	
	private static void pushEndMessage(List<String> strAryFR) {
		 StringBuilder content = new StringBuilder();
		//push end message.
        String endMes = BarOption.getBillFootInfo();
        if(endMes != null && endMes.length() > 0) {
        	//content.append(BarUtil.generateString(tWidth, sep_str2));
        	content.append("\n");
        	content.append(endMes);
        }
        strAryFR.add(content.toString());
	}
    
	private static void pushSalesSummary(ArrayList<String> strAryFR, List<Bill> list, int width, 
			String countSale, String countRefund, String amountSaleGross, String refundGross,
			String net, String HST, String total) {
		StringBuilder content = new StringBuilder();
		//title
		content.append(getSeperatorLine(1, width)).append("\n");
		String saleSummary = "Sale Summary";
		String emptySpaceStr = BarUtil.generateString((width - saleSummary.length())/2, " ");
		content.append(emptySpaceStr).append(saleSummary).append("\n");
		content.append(getSeperatorLine(0, width)).append("\n");
		content.append("Tran Type").append(BarUtil.generateString(width - 15 - 9, " "))
		.append("Count").append("    ").append("Amount").append("\n");
		content.append(getSeperatorLine(0, width)).append("\n");
		//countSaleGross + amountSaleGross
		String totalSaleCount = String.valueOf(Integer.valueOf(countRefund) + Integer.valueOf(countSale));
		content.append("Sale Gross").append(BarUtil.generateString(width - 10 - 10 - totalSaleCount.length(), " "))
		.append(totalSaleCount).append(BarUtil.generateString(10 - amountSaleGross.length() , " ")).append(amountSaleGross)
		.append("\n");
		//countRefond
		content.append("Refund Gross").append(BarUtil.generateString(width - 12 - 10 - countRefund.length(), " "))
		.append(countRefund).append(BarUtil.generateString(10 - refundGross.length() , " ")).append(refundGross)
		.append("\n");
		content.append(getSeperatorLine(1, width)).append("\n");
		
		//Net
		content.append("Net").append(BarUtil.generateString(width - 3 - net.length(), " ")).append(net)
		.append("\n");
		
		//HST
		content.append("HST").append(BarUtil.generateString(width - 3 - HST.length(), " ")).append(HST)
		.append("\n");
		//total
		content.append(getSeperatorLine(1, width)).append("\n");
		content.append(BarUtil.generateString(width - total.length(), " ")).append(total);
		
		strAryFR.add(content.toString());
	}
	
	private static void pushPaymentSummary(ArrayList<String> strAryFR, List<Bill> list, int width) {
		StringBuilder content = new StringBuilder();
		//title
		String paymentSummary = "Payment Summary";
		String emptySpaceStr = BarUtil.generateString((width - paymentSummary.length())/2, " ");
		content.append("\n");
		content.append(emptySpaceStr).append(paymentSummary).append("\n");
		content.append(getSeperatorLine(0, width)).append("\n");
		content.append("PayBy  Qt     Sales   Tip   ").append(BarUtil.generateString(width - 34, " ")).append("TOTAL").append("\n");
		content.append(getSeperatorLine(0, width)).append("\n");

		int cashQt = 0, cashTotal = 0;
		int debitQt = 0, debitSales = 0, debitTip = 0, debitTotal = 0;
		int visaQt = 0, visaSales = 0, visaTip = 0, visaTotal = 0;
		int masterQt = 0, masterSales = 0, masterTip = 0, masterTotal = 0;
		int otherQt = 0, otherSales = 0, otherTip = 0, otherTotal = 0;
		
		for (Bill bill : list) {
  			int status = bill.getStatus();
  			if(status < -1) {//means has refund)
		  	  	cashTotal += status;
  			}
  			
			boolean tipCounted = false;
			if(bill.getCashReceived() > 0) {
  				cashQt++;
	  	  		cashTotal += bill.getCashReceived() + bill.getCashback();	//@NOTE: cashback is negtive value.
			}
			if(bill.getDebitReceived() > 0) {
				debitQt++;
				debitTotal += bill.getDebitReceived();
				debitSales = debitTotal;
				if(bill.getTip() > 0 && !tipCounted) {
					debitTip += bill.getTip();
					debitSales -= debitTip;
					tipCounted = true;
				}
			}
			if(bill.getVisaReceived() > 0) {
  				visaQt++;
	  	  		visaTotal += bill.getVisaReceived();
	  	  		visaSales = visaTotal;
				if(bill.getTip() > 0 && !tipCounted) {
					visaTip += bill.getTip();
					visaSales -= visaTip;
					tipCounted = true;
				}
			}
			if(bill.getMasterReceived() > 0) {
  				masterQt++;
  				masterTotal += bill.getMasterReceived();
  				masterSales = masterTotal;
				if(bill.getTip() > 0 && !tipCounted) {
					masterTip += bill.getTip();
					masterSales -= masterTip;
					tipCounted = true;
				}
			}
			if(bill.getOtherReceived() > 0) {
  				otherQt++;
	  	  		otherTotal += bill.getOtherReceived();
	  	  		otherSales = otherTotal;
				if(bill.getTip() > 0 && !tipCounted) {
					otherTip += bill.getTip();
					otherSales -= otherTip;
					tipCounted = true;
				}
			}
  		}
		
		String strcashTotal = BarUtil.formatMoney(cashTotal / 100.0);
		String strdebitSales = BarUtil.formatMoney(debitSales / 100.0), strdebitTip = BarUtil.formatMoney(debitTip / 100.0), strdebitTotal = BarUtil.formatMoney(debitTotal / 100.0);
		String strvisaSales = BarUtil.formatMoney(visaSales / 100.0), strvisaTip = BarUtil.formatMoney(visaTip / 100.0), strvisaTotal = BarUtil.formatMoney(visaTotal / 100.0);
		String strmasterSales = BarUtil.formatMoney(masterSales / 100.0), strmasterTip = BarUtil.formatMoney(masterTip / 100.0), strmasterTotal = BarUtil.formatMoney(masterTotal / 100.0);
		String strotherSales = BarUtil.formatMoney(otherSales / 100.0), strotherTip = BarUtil.formatMoney(otherTip / 100.0), strotherTotal = BarUtil.formatMoney(otherTotal / 100.0);
		String strtotalTip = BarUtil.formatMoney((debitTip + visaTip + masterTip + otherTip) / 100.0);
		//content.append("PayBy  Qt     Sales      Tip   
		content.append("Cash   ").append(cashQt)
		.append(BarUtil.generateString(width - 7 - getNumberStrLength(cashQt, strcashTotal), " "))
		.append(strcashTotal).append("\n");
		
		content.append("Debit  ").append(debitQt)
		.append(BarUtil.generateString(12 - getNumberStrLength(debitQt, strdebitSales), " ")).append(strdebitSales)
		.append(BarUtil.generateString(9 - strdebitTip.length(), " ")).append(strdebitTip)
		.append(BarUtil.generateString(width - 28 - strdebitTotal.length(), " ")).append(strdebitTotal).append("\n");
		
		content.append("Visa   ").append(visaQt)
		.append(BarUtil.generateString(12 - getNumberStrLength(visaQt, strvisaSales), " ")).append(strvisaSales)
		.append(BarUtil.generateString(9 - strvisaTip.length(), " ")).append(strvisaTip)
		.append(BarUtil.generateString(width - 28 - strvisaTotal.length(), " ")).append(strvisaTotal).append("\n");
		
		content.append("Master ").append(masterQt)
		.append(BarUtil.generateString(12 - getNumberStrLength(masterQt, strmasterSales), " ")).append(strmasterSales)
		.append(BarUtil.generateString(9 - strmasterTip.length(), " ")).append(strmasterTip)
		.append(BarUtil.generateString(width - 28 - strmasterTotal.length(), " ")).append(strmasterTotal).append("\n");
		
		content.append("Other  ").append(otherQt)
		.append(BarUtil.generateString(12 - getNumberStrLength(otherQt, strotherSales), " ")).append(strotherSales)
		.append(BarUtil.generateString(9 - strotherTip.length(), " ")).append(strotherTip)
		.append(BarUtil.generateString(width - 28 - strotherTotal.length(), " ")).append(strotherTotal).append("\n");
		
		content.append(getSeperatorLine(1, width)).append("\n");
		
		content.append(BarUtil.generateString(18 - strtotalTip.length(), " ")).append("total tip:").append(strtotalTip)
		.append(BarUtil.generateString(width - 28 - strcashTotal.length() - 5, " ")).append("cash:").append(strcashTotal).append("\n");
		
		strAryFR.add(content.toString());
	}

	private static int getNumberStrLength(int cashQt, String cashTotal) {
		return String.valueOf(cashQt).length() + cashTotal.length();
	};
	
	private static void pushSummaryByServiceType(ArrayList<String> strAryFR, List<Bill> list, int width) {};
	
	private static void pushOtherSummary(List<Bill> list) {};
	
	private static void pushVoidItemSummary(ArrayList<String> strAryFR, int width, String startDateStr, String endDateStr) {
		StringBuilder content = new StringBuilder();
		//title
		String voidItemSummary = "Void Item Summary";
		String emptySpaceStr = BarUtil.generateString((width - voidItemSummary.length())/2, " ");
		content.append("\n");
		content.append(emptySpaceStr).append(voidItemSummary).append("\n");
		content.append(getSeperatorLine(0, width)).append("\n");
		content.append("Time        Table  Dish         Qt   Sales").append("\n");
		content.append(getSeperatorLine(0, width)).append("\n");

		StringBuilder sql = new StringBuilder("Select * from Output, product, bill where bill.status = ").append(DBConsts.voided)
				.append(" and time >= '").append(startDateStr).append("' and time <= '").append(endDateStr)
				.append("' and output.productID = product.id and output.category = bill.id");
        if(LoginDlg.USERTYPE < 2) {
        	sql.append(" and employeeId = ").append(LoginDlg.USERID);
        }
        
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息
            rs.afterLast();
            rs.relative(-1);
            rs.beforeFirst();
            float totalSales = 0f;
            while (rs.next()) {
                String time = rs.getString("Output.time");
                content.append(time.substring(5, 16));	//remove the year to make it shorter.
                
                String table = rs.getString("Output.subject");
                content.append(BarUtil.generateString(6 - table.length(), " ")).append(table);
        		
                String product = rs.getString("product.subject");
                content.append("  ").append(product);
                
                int i = rs.getInt("Output.amount");
                String displayableNum = Dish.getDisplayableNum(i);
                content.append(BarUtil.generateString(15 - product.length() - String.valueOf(displayableNum).length(), " ")).append(displayableNum);
                
                float f = Float.valueOf((float) (rs.getInt("Output.toltalPrice") / 100.0));
                String money = BarUtil.formatMoney(f);
                content.append(BarUtil.generateString(width - 34 - money.length(), " ")).append(money).append("\n");
                totalSales += f;
            }
            
    		content.append(getSeperatorLine(1, width)).append("\n");
    		String toltal = BarUtil.formatMoney(totalSales);
    		content.append(BarUtil.generateString(width - toltal.length(), " "))
    		.append(toltal).append("\n");
    		
    		strAryFR.add(content.toString());

            rs.close();// 关闭
        }catch(Exception e) {
        	L.e("Pos", "Printing Report", e);
        }
	};
    
	private static String getSeperatorLine(int index, int tWidth) {
		//seperator
		if(sepLines == null) {
			sepLines = new String[2];
	        String sep_str1 = (String)CustOpts.custOps.getValue("sep_str1");
	        if(sep_str1 == null || sep_str1.length() == 0){
	            sep_str1 = SEP_STR1;
	        }
	        sepLines[0] = BarUtil.generateString(tWidth, sep_str1);

	        String sep_str2 = (String)CustOpts.custOps.getValue("sep_str2");
	        if(sep_str2 == null || sep_str2.length() == 0){
	            sep_str2 = SEP_STR2;
	        }

	        sepLines[1] = BarUtil.generateString(tWidth, sep_str2);
		}
		if(index > sepLines.length) {
			L.e("PrintService", "Unexpect index when getting item from sepLines with Index: " + index
					+ "the width of sepLines is " + sepLines.length, null);
		}
		return sepLines[index];
	}
	
    private static StringBuilder getFormattedBillHeader(int tWidth, String id) {
    	StringBuilder sb = id == null ? new StringBuilder() : new StringBuilder("#").append(id).append("\n");
    	String s = BarOption.getBillHeadInfo();
    	if(s != null && s.trim().length() > 0) {
    		String[] infos = s.split(":");
    		for(String info : infos) {
    			int length = (tWidth - info.length())/2;
    			sb.append(BarUtil.generateString(length, " "));
    			sb.append(info);
    			sb.append("\n");
    		}
    	}
    	return sb;
    }
    
    private static String formatContentForOrder(List<Dish> list, String curPrintIp,
    		String curTable, String curBill, String waiterName, boolean isCancelled){
        //L.d(TAG,"formatContentForPrint");
        String font = (String)CustOpts.custOps.getValue(curPrintIp + "font");
        if(font ==  null || font.length() < 1) {
            font = (String)CustOpts.custOps.getValue("font");
        }
        
        int tWidth = BarUtil.getPreferedWidth();
        Integer w = ipPrinterMap.get(curPrintIp).getState();
        if(w != null) {
        	tWidth = w.intValue();
        }
        StringBuilder content = new StringBuilder("\n\n");
        if(isCancelled) {
        	content.append(BarUtil.generateString((tWidth - 4)/2, " ")).append("STOP");
        }
        if(tWidth < 20) {
            content.append("\n\n");
        }
        content.append("(").append(curTable).append(")");

        int lengthOfStrToDisplay = 3 + curTable.length();
        if(BarOption.isDisplayBillInKitchen()) {
        	content.append(curBill);
        	lengthOfStrToDisplay += curBill.length();
        }
        
        content.append("   ");	//the first 3 spaces of the spaceStr is consumed first.
        if(BarOption.isDisplayWaiterInKitchen()) {
        	content.append(waiterName);
        	lengthOfStrToDisplay += waiterName.length();
        }

        DateFormat df = new SimpleDateFormat("HH:mm");
        String dateStr = df.format(new Date());
        lengthOfStrToDisplay += dateStr.length();
        String spaceStr = BarUtil.generateString(tWidth - lengthOfStrToDisplay - 3, " ");
        
        content.append(spaceStr).append(dateStr).append("\n");

        String sep_str1 = isCancelled ? "!" : (String)CustOpts.custOps.getValue("sep_str1");
        if(sep_str1 == null || sep_str1.length() == 0){
            sep_str1 = SEP_STR1;
        }
        String sep_str2 = isCancelled ? "X" : (String)CustOpts.custOps.getValue("sep_str2");
        if(sep_str2 == null || sep_str2.length() == 0){
            sep_str2 = SEP_STR2;
        }

        content.append(BarUtil.generateString(tWidth, sep_str1)).append("\n\n");
        int langIndex = ipPrinterMap.get(curPrintIp).getType();
        for(Dish d:list){
            StringBuilder sb = new StringBuilder();
            if(BarOption.isDisDishIDInKitchen()) {
                sb.append(d.getId());
                sb.append(BarUtil.generateString(5 - String.valueOf(d.getId()).length(), " "));
            }
            sb.append(d.getLanguage(langIndex));
            if(d.getNum() > 1){//Note：when sending to kitchen, the number couldnot be bigger than 10000.
                String space = " ";
                int occupiedLength = BarUtil.getLengthOfString(sb.toString());
                sb.append(BarUtil.generateString(tWidth - occupiedLength - (d.getNum() < 10 ? 2 : 3), " "));
                sb.append("x").append(Integer.toString(d.getNum()));
            }
            content.append(sb);
            content.append("\n");
            String modification = d.getModification();
            if(modification != null && !"null".equalsIgnoreCase(modification)) {
            	String modifyStr = d.getModification();
            	String[] notes = modifyStr.split(BarDlgConst.delimiter); 
                for (String str : notes) {
                	String[] langs = str.split(BarDlgConst.semicolon);
                	String lang = langs.length > langIndex ? langs[langIndex] : langs[0];
                	if(lang.length() == 0)
                		lang = langs[0];
                    content.append(BarUtil.generateString(5, " ")).append("* ").append(lang).append(" *\n");
                }
            }
            Object sep = CustOpts.custOps.getValue("customizedSeperator");
            if(sep != null && !"null".equalsIgnoreCase(sep.toString())) {
            	if("".equals(sep)) {
            		sep = " ";
            	}
            	content.append(BarUtil.generateString(tWidth, (String)sep)).append("\n");
            }
        }
        
        return content.substring(0, content.length());//spec change: do not to show separator!  - (tWidth + 1));
    }

    private static void flushIpContent() {
    	if(!isIpContentMapEmpty()){
        	printContents();
        	ErrorUtil.write("found non-empty ipContentMap when printing report.");
        }
    }

    private static boolean isIpContentMapEmpty(){
    	if(ipContentMap == null) {
        	ipContentMap = new HashMap<String,List<String>>();
        }
    	if(ipContentMap.isEmpty()) {
        	for(Entry<String,Printer> entry: ipPrinterMap.entrySet()){
                ipContentMap.put(entry.getKey(),new ArrayList<String>());
            }
    	}
		for (Map.Entry entry : ipContentMap.entrySet()) {
			List<String> listTypeValue = (List<String>) entry.getValue();
			if (listTypeValue.size() > 0) {
				return false;
			}
		}
		return true;
    }    

    private static void reInitPrintRelatedMaps(){
        ipSelectionsMap = new HashMap<String,List<Dish>>();

        for(Entry<String,Printer> entry: ipPrinterMap.entrySet()){
            ipSelectionsMap.put(entry.getKey(),new ArrayList<Dish>());
        }
    }

	private static void checkPrinter(DocFlavor flavor, PrintRequestAttributeSet pras) {
		//check printer.
	    if(defaultService == null) {
		    defaultService = PrintServiceLookup.lookupDefaultPrintService(); 
		    if(defaultService == null) {
		    	javax.print.PrintService[] printService = PrintServiceLookup.lookupPrintServices(flavor, pras); 
			    if( printService.length > 0 ){  
			    	defaultService = printService[0];
			    }
			    
			    if (defaultService == null) {
			    	ErrorUtil.write("Please install printer first.");
			    }
		    }
	    }
	}
	
	private static String roundCent(String priceStr) {
		Float f = Float.parseFloat(priceStr);
		int t = (int)(f * 100);
    	//get the last bit
    	int last = t % 10;
    	if(last == 1 || last == 2)
    		last = 0;
    	else if(last == 3 || last == 4 || last == 6 || last == 7)
    		last = 5;
    	else if(last == 8 || last == 9)
    		last = 10;
    	return BarUtil.formatMoney((t/ 10 * 10 + last) / 100.0);
    }
	
    final static String mev1 = "<reqMEV><trans noVersionTrans=\"v0%s.00\" etatDoc=\"%s\" modeTrans=\"%s\" duplicata=\"%s\"><doc><texte><![CDATA[\n";
    final static String mev2 = "]]></texte></doc>\r\n		<donneesTrans ";
    final static String mev3 = "/>\n";
    final static String mevRef = "<ref numeroRef=\"%s\" dateRef=\"%s\" mtRefAvTaxes=\"%s\"/>";//»AAAAMMJJhhmmss»//»+/-999999.99»
    //TODO add "verif" part. not necessary for now.      .append("    <verif taille=\"4569\"/>\r\n")
    final static String mevEnd = "</trans>\r\n</reqMEV>";
    
    final static String mevReportDocAutre1 = "<reqMEV><docAutre noVersionAutre=\"v01.00\"><![CDATA[";
    final static String mevReportDocAutre2 = "]]></docAutre></reqMEV>";

}
