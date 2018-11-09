package org.cas.client.platform.bar.print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.model.Bill;
import org.cas.client.platform.bar.model.Category;
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

    public static int SUCCESS = -1;	//@NOTE:must be less than 0, because if it's 0, means the first element caused error.
    
    public static Category[] allCategory;
    public static String[] sepLines;
    
    public static HashMap<String,Printer> ipPrinterMap = new HashMap<>();
    private static HashMap<String,List<Dish>> ipSelectionsMap;
    private static HashMap<String,List<String>> ipContentMap;

    private static String curPrintIp = "";
    private static int width = 46;
    private String code = "GBK";
    private static String SEP_STR1 = "=";
    private static String SEP_STR2 = "-";

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
    
    //The start time and end time are long format, need to be translate for print.
    public static void exePrintBill(BillPanel billPanel, List<Dish> saleRecords){
    	flushIpContent();
        reInitPrintRelatedMaps();
        
        String printerIP = BarFrame.instance.menuPanel.printers[0].getIp();
        if(ipContentMap.get(printerIP) == null)
        	ipContentMap.put(printerIP,new ArrayList<String>());
        ipContentMap.get(printerIP).addAll(
        		formatContentForBill(saleRecords, printerIP, billPanel));
        printContents();
    }
    
    //The start time and end time are long format, need to be translate for print.
    public static void exePrintInvoice(BillPanel billPanel, List<Dish> saleRecords, boolean isCashBack){
        flushIpContent();
        reInitPrintRelatedMaps();

        String printerIP = BarFrame.instance.menuPanel.printers[0].getIp();
        if(ipContentMap.get(printerIP) == null)
        	ipContentMap.put(printerIP,new ArrayList<String>());
        ipContentMap.get(printerIP).addAll(
        		formatContentForInvoice(saleRecords, printerIP, billPanel, isCashBack));
        printContents();
    }
    
    public static void exePrintReport(List<Bill> bills, String startTime, String endTime){
    	L.d("PrintService", "inside exePrint");
    	flushIpContent();
        reInitPrintRelatedMaps();
      
        String printerIP = BarFrame.instance.menuPanel.printers[0].getIp();
        if(ipContentMap.get(printerIP) == null)
        	ipContentMap.put(printerIP,new ArrayList<String>());
        ipContentMap.get(printerIP).addAll(
        		formatContentForReport(bills, printerIP, startTime, endTime));
        printContents();
    }
    
    public static void exePrintOrderList(List<Dish> selectdDishAry, boolean isCancelled){

		Printer[] printers = BarFrame.menuPanel.printers;
		String curTable = BarFrame.instance.valCurTable.getText();
		String curBill = BarFrame.instance.valCurBill.getText();
		String waiterName = BarFrame.instance.valOperator.getText();

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
            String[] ids = printerStr.split(",");
            String[] ips = new String[ids.length];
            for(int i = 0; i < ids.length; i++) {
            	if(ids[i].length() > 0)	//some dish might has no printer set.
            		ips[i] = mapToIP(printers, Integer.valueOf(ids[i]));
            }
            for(String ip: ips) {
                Printer printer = ipPrinterMap.get(ip);
                if(printer == null) {                   //should never happen, just in case someone changed db.
                    ErrorUtil.write("Selected dish not connected with any printer yet.");
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
    	BarFrame.setStatusMes("PRINTED...");
    	int errorsAmount = 0;
        for(Entry<String,List<String>> entry : ipContentMap.entrySet()) {
        	List<String> sndMsg = entry.getValue();
        	//for(int i = contents.size() - 1; i >= 0 ; i--) {
        	if(sndMsg.size() > 0) {
        		boolean printSuccessful = false;
        		String printerID = entry.getKey();
        		if("mev".equalsIgnoreCase(printerID)) {
             		printSuccessful = doSystemPrint(sndMsg);
             	} else if("serial".equalsIgnoreCase(printerID)) { 
            		printSuccessful = doSerialPrint(sndMsg);
            	} else {
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

	public static boolean openDrawer(String ip){
    	try{
			Socket socket = new Socket(ip != null ? ip : BarFrame.instance.menuPanel.printers[0].getIp(), 9100);
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
    
    private static boolean doSystemPrint(List<String> sndMsg) {
		// save contents into a file.
    	FileInputStream stream = null;
		String filePath = CASUtility.getPIMDirPath().concat(System.getProperty("file.separator")).concat(new Date().getTime() + "transaction.xml");
		Path path = Paths.get(filePath);
		try {
    		Files.write(path, buildMevFormatContent(sndMsg));
    		stream = new FileInputStream(new File(filePath));
        } catch (IOException e) {
        	ErrorUtil.write(e);
        }
    	
    	// print the file
	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
    	PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();  
	    
    	checkPrinter(flavor, pras);

		boolean isSuccess = false;
		DocPrintJob job = defaultService.createPrintJob();
		try {
			job.print(new SimpleDoc(stream, flavor, new HashDocAttributeSet()), pras);
			Files.deleteIfExists(path);
			isSuccess = true;
		} catch (PrintException e) {
			ErrorUtil.write(e);
		} catch(IOException ioe) {
			ErrorUtil.write(ioe);
		}
		
		//do cut paper.
		job = defaultService.createPrintJob();	
		try {
			job.print(new SimpleDoc(new FileInputStream(getCutCommandDocFile()), flavor, new HashDocAttributeSet()), pras);
			isSuccess = true;
		} catch (PrintException e) {
			ErrorUtil.write(e);
		}  catch(IOException ioe) {
			ErrorUtil.write(ioe);
		}
		
		
		return isSuccess;
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
    
	private static File getCutCommandDocFile() {
		String filePath = CASUtility.getPIMDirPath().concat(System.getProperty("file.separator")).concat("mevCutCommand.xml");
		File file = new File(filePath);
		
		//check if it's exist.
		if(!file.exists()) {
			//creata the file
			try {
				byte[] a = mevDocAutre1.getBytes("ASCII");
				byte[] b = mevDocAutre2.getBytes("ASCII");
				byte[] c = new byte[a.length + Command.GS_V_m_n.length + b.length];
				System.arraycopy(a, 0, c, 0, a.length);
				System.arraycopy(Command.GS_V_m_n, 0, c, a.length, Command.GS_V_m_n.length);
				System.arraycopy(b, 0, c, a.length + Command.GS_V_m_n.length, b.length);
				Files.write(Paths.get(filePath), c);
			}catch(Exception e) {
				ErrorUtil.write(e);
			}
		}
		
		return file;
	}

	private static byte[] buildMevFormatContent(List<String> sndMsg) {
		//the contents could be composed by :1/Command.BEEP 2/BigFont 3/NormalFont 4/cut 5/content.
		//while when sending to mev device, the sndMsg could only be content. and length will always be 1.
		//we will make the content into 
		byte[] formattedContent = null;
		StringBuilder content = new StringBuilder(mev1);
		for (int i = 0; i < sndMsg.size(); i++) {
			content.append(sndMsg.get(i));
		}
		
		content.append(mev2).append(mev3).append(mev4).toString();
		try {
			formattedContent = content.toString().getBytes("ASCII");
		}catch(UnsupportedEncodingException e) {
			ErrorUtil.write(e);
		}
		
		return formattedContent;
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
                JOptionPane.showMessageDialog(BarFrame.instance, 
                		"Content NOT printed!!! printer response too slow. Please check printer and try again. --ip: "+ip);
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
		}else if("cut".equals(sndMsg)){
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
    
    private static ArrayList<String> formatContentForBill(List<Dish> list, String curPrintIp, BillPanel billPanel){
    	ArrayList<String> strAryFR = new ArrayList<String>();
    	//L.d(TAG,"formatContentForPrint");
        int tWidth = width;
        try {
            tWidth = Integer.valueOf((String)CustOpts.custOps.getValue( "width"));
        }catch(Exception e){
        	//do nothing, if no with property set, width will keep default value.
        }
	            
	    pushBillHeadInfo(strAryFR, tWidth);
        pushServiceDetail(list, curPrintIp, billPanel, strAryFR, tWidth);
        //pushTotal(billPanel, strAryFR);
        pushEndMessage(strAryFR);

        //strAryFR.add("\n\n\n\n\n");
        strAryFR.add("cut");
        return strAryFR;
    }
    
    private static ArrayList<String> formatContentForInvoice(
    		List<Dish> list, String curPrintIp, BillPanel billPanel, boolean isCashBack){
    	ArrayList<String> strAryFR = new ArrayList<String>();
    	//L.d(TAG,"formatContentForPrint");
        int tWidth = width;
        try {
            tWidth = Integer.valueOf((String)CustOpts.custOps.getValue( "width"));
        }catch(Exception e){
        	//do nothing, if no with property set, width will keep default value.
        }
	            
	    pushBillHeadInfo(strAryFR, tWidth);
        pushServiceDetail(list, curPrintIp, billPanel, strAryFR, tWidth);
        //pushTotal(billPanel, strAryFR);
        pushPayInfo(billPanel, strAryFR, tWidth, isCashBack);
        pushEndMessage(strAryFR);
        return strAryFR;
    }

    private static ArrayList<String> formatContentForReport(List<Bill> list, String curPrintIp, String startTime, String endTime){
    	ArrayList<String> strAryFR = new ArrayList<String>();
    	//L.d(TAG,"formatContentForPrint");
        int tWidth = width;
        try {
            tWidth = Integer.valueOf((String)CustOpts.custOps.getValue( "width"));
        }catch(Exception e){
        	//do nothing, if no with property set, width will keep default value.
        }
        
        //initContent
        //content
  		int refundCount = 0,refoundAmount = 0;
  		int salesGrossCount = 0,salesGrossAmount = 0;
  		int cashSale,cashRefund,cashNet = 0;
  		int visaSale,visaRefund,visaNet = 0;
  		int masterSale,masterRefund,masterNet = 0;
  		float net = 0, HST = 0;
  		
  		Object g = CustOpts.custOps.getValue(BarFrame.consts.TVQ());
    	Object q = CustOpts.custOps.getValue(BarFrame.consts.TPS());
    	float gstRate = g == null ? 5 : Float.valueOf((String)g);
    	float qstRate = q == null ? 9.975f : Float.valueOf((String)q);
    	
  		for (Bill bill : list) {
  			int status = bill.getStatus();
  			if(status < -1) {//means has refund)
  				refundCount++;
  				status = 0 - status / 100;
  				refoundAmount += status;
  			}else {
  				salesGrossCount++;
  				salesGrossAmount += bill.getCashReceived() + bill.getDebitReceived()
  				+ bill.getVisaReceived() + bill.getMasterReceived() + bill.getOtherReceived() + bill.getCashback();
  			}
  		}
  		net = salesGrossAmount * 100 /(100 + gstRate + qstRate);
  		HST = net * (gstRate + qstRate)/100;
  		//
      		
	    pushBillHeadInfo(strAryFR, tWidth);
	    pushWaiterAndTime(strAryFR, list, tWidth, startTime, endTime);
        pushSalesSummary(strAryFR, list, tWidth,
        		String.valueOf(salesGrossCount), String.valueOf(refundCount), 
        		new DecimalFormat("#0.00").format(salesGrossAmount/100.0),
        		new DecimalFormat("#0.00").format(refoundAmount / 100.0),
        		new DecimalFormat("#0.00").format(net / 100.0),
        		new DecimalFormat("#0.00").format(HST / 100.0)
        		);
        pushPaymentSummary(list);
        pushSummaryByServiceType(list);
        pushSummaryByOrder(list);
        pushAuditSummary(list);
        strAryFR.add("\n\n\n\n\n");
        strAryFR.add("cut");
        return strAryFR;
    }
    
	private static void pushBillHeadInfo(ArrayList<String> strAryFR, int tWidth) {
		StringBuilder content = getFormattedBillHeader(tWidth);
        content.append("\n");
        //push bill header
        strAryFR.add(content.toString());
	}
	
	private static void pushWaiterAndTime(ArrayList<String> strAryFR, List<Bill> list, 
			int tWidth, String startDateStr, String endDateStr) {
		StringBuilder content = new StringBuilder();
        //waiter
        content.append(" ").append(LoginDlg.USERNAME).append(" ");
        //space
        int lengthOfSpaceBeforeTime = tWidth - content.length() - startDateStr.length() - endDateStr.length() - 3;
        if(lengthOfSpaceBeforeTime > 0) {
        	String spaceStr = generateString(lengthOfSpaceBeforeTime, " ");
        	content.append(spaceStr).append(startDateStr).append("   ").append(endDateStr);
        }else {
        	String spaceStr = generateString(tWidth - startDateStr.length() - endDateStr.length() - 3, " ");
        	content.append("\n").append(spaceStr).append(startDateStr).append("   ").append(endDateStr);
        }
        content.append("\n");
        strAryFR.add(content.toString());
	}
	
	private static void pushServiceDetail(List<Dish> list, String curPrintIp, BillPanel billPanel,
			ArrayList<String> strAryFR, int tWidth) {
		StringBuilder content = new StringBuilder();
		//table
        content.append("(").append(BarFrame.instance.valCurTable.getText()).append(")");
        //bill
        content.append(billPanel.billButton == null ? BarFrame.instance.valCurBill.getText() : billPanel.billButton.getText());
        //waiter
        content.append(" ").append(BarFrame.instance.valOperator.getText()).append(" ");
        //time
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = df.format(new Date());
        //space
        int lengthOfSpaceBeforeTime = tWidth - content.length() - dateStr.length();
        if(lengthOfSpaceBeforeTime > 0) {
        	String spaceStr = generateString(lengthOfSpaceBeforeTime, " ");
        	content.append(spaceStr).append(dateStr);
        }else {
        	String spaceStr = generateString(tWidth - dateStr.length(), " ");
        	content.append("\n").append(spaceStr).append(dateStr);
        }
        content.append("\n");
        //seperator
        String sep_str1 = (String)CustOpts.custOps.getValue("sep_str1");
        if(sep_str1 == null || sep_str1.length() == 0){
            sep_str1 = SEP_STR1;
        }
        String sep_str2 = (String)CustOpts.custOps.getValue("sep_str2");
        if(sep_str2 == null || sep_str2.length() == 0){
            sep_str2 = SEP_STR2;
        }
        //dishes
        content.append(generateString(tWidth, sep_str1)).append("\n\n");
        int langIndex = ipPrinterMap.get(curPrintIp).getType();
        for(Dish d:list){
            StringBuilder sb = new StringBuilder();
            
            if(BarOption.isDisDishIDInKitchen()) {
                sb.append(d.getId());
                sb.append(generateString(5 - String.valueOf(d.getId()).length(), " "));
            }
            
            String dishName = d.getLanguage(langIndex); 
            sb.append(dishName);
            
            if(d.getNum() > 1){
            	sb.append(" ").append(d.getDisplayableNum());
            }
            
            String price = new DecimalFormat("#0.00").format(d.getTotalPrice()/100f);
            int occupiedLength = getLengthOfString(sb.toString());
            sb.append(generateString(tWidth - occupiedLength - (price.length()), " "));
            sb.append(price);
            content.append(sb);

            content.append("\n");
        }
        
        //seperator
        content.append(generateString(tWidth, sep_str2)).append("\n");
        
        //totals
        String SubTotal = "SubTotal";
        String TPS = "TPS";
        String TVQ = "TVQ";
        String ServiceFee = "Service Fee";
        String Discount = "Discount";
        
        String[] strs = billPanel.lblSubTotle.getText().split(":");
        String subtotal = strs[1].trim().substring(1);
        content.append(SubTotal).append(" : ")
        	.append(generateString(tWidth - subtotal.length() - SubTotal.length() - 3, " "))
        	.append(subtotal).append("\n");
        strs = billPanel.lblTPS.getText().split(":");
        String tps = strs[1].trim().substring(1);
        content.append(TPS).append(" : ")
    		.append(generateString(tWidth - tps.length() - TPS.length() - 3, " "))
        	.append(tps).append("\n");
        strs = billPanel.lblTVQ.getText().split(":");
        String tvq = strs[1].trim().substring(1);
        content.append(TVQ).append(" : ")
			.append(generateString(tWidth - tvq.length() - TVQ.length() - 3, " "))
        	.append(tvq).append("\n");
        if(billPanel.lblServiceFee.getText().length() > 0) {
            strs = billPanel.lblServiceFee.getText().split(":");
            String serviceFee = strs[1].trim().substring(1);
            content.append(ServiceFee).append(" : ")
				.append(generateString(tWidth - serviceFee.length() - ServiceFee.length() - 3, " "))
            	.append(serviceFee).append("\n");
        }
        if(billPanel.lblDiscount.getText().length() > 0) {
            strs = billPanel.lblDiscount.getText().split(":");
            String dicount = strs[1].trim().substring(2);
            content.append(Discount).append(" : ")
				.append(generateString(tWidth - dicount.length() - Discount.length() - 3, " "))
            	.append(dicount).append("\n");
        }
        
        //do a push
        strAryFR.add(content.toString());
        content.delete(0, content.length());
	}

	private static void pushTotal(BillPanel billPanel, ArrayList<String> strAryFR) {
		StringBuilder content = new StringBuilder();
		//push bigger font. while it will not work for mev.
        strAryFR.add("BigFont");
        //push total
        String strTotal = billPanel.valTotlePrice.getText();
        content.append("Total").append(" : ")
			//.append(generateString(tWidth - strTotal.length() - Total.length() - 3 - 1, " "))
			.append(strTotal).append("\n");
        strAryFR.add(content.toString());
        //push normal font
        strAryFR.add("NormalFont");
	}

	private static void pushPayInfo(BillPanel billPanel, ArrayList<String> strAryFR, int width, boolean isCashBack) {

		StringBuilder content = new StringBuilder("\n");
		int billId = billPanel.getBillId();
		
    	StringBuilder sb = new StringBuilder("select * from bill where id = " + billId);
    	try {
    		ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sb.toString());
            rs.next();
            float total = (float) (rs.getInt("total") / 100.0);
            int cashReceived = rs.getInt("cashReceived");
            String str = new DecimalFormat("#0.00").format(cashReceived/100f);
            if(cashReceived > 0) {
    			content.append("CASH").append(" : ")
    			.append(generateString(width - 7 - str.length(), " "))
    			.append(str).append("\n");
    		}
            int debitReceived = rs.getInt("debitReceived");
            str = new DecimalFormat("#0.00").format(debitReceived/100f);
            if(debitReceived > 0) {
    			content.append("DEBIT").append(" : ")
    			.append(generateString(width - 8 - str.length(), " "))
    			.append(str).append("\n");
    		}
            int visaReceived = rs.getInt("visaReceived");
            str = new DecimalFormat("#0.00").format(visaReceived/100f);
            if(visaReceived > 0) {
    			content.append("VISA").append(" : ")
    			.append(generateString(width - 7 - str.length(), " "))
    			.append(str).append("\n");
    		}
            int masterReceived = rs.getInt("masterReceived");
            str = new DecimalFormat("#0.00").format(masterReceived/100f);
        	if(masterReceived > 0) {
    			content.append("MASTER").append(" : ")
    			.append(generateString(width - 9 - str.length(), " "))
    			.append(str).append("\n");
    		}
            
            float left = -1 * ((int)((total * 100 - cashReceived - debitReceived - visaReceived - masterReceived)));
            str = new DecimalFormat("#0.00").format(left/100f);
            String lblText = isCashBack ? "Change : " : "Tip : ";
            content.append(lblText)
 				.append(generateString(width - lblText.length() - str.length(), " "))
 				.append(str).append("\n");

    	}catch(Exception e) {
    		ErrorUtil.write(e);
    	}
    	
		strAryFR.add(content.toString());
	}
	
	private static void pushEndMessage(ArrayList<String> strAryFR) {
		 StringBuilder content = new StringBuilder();
		//push end message.
        String endMes = BarOption.getBillFootInfo();
        if(endMes != null && endMes.length() > 0) {
        	//content.append(generateString(tWidth, sep_str2));
        	content.append("\n");
        	content.append(endMes);
        }
        strAryFR.add(content.toString());
	}
    
	private static void pushSalesSummary(ArrayList<String> strAryFR, List<Bill> list, int width, 
			String countSale, String countRefund, String amountSaleGross, String refundGross,
			String net, String HST) {
		StringBuilder content = new StringBuilder();
		//title
		content.append(getSeperatorLine(1, width)).append("\n");
		String saleSummary = "Sale Summary";
		String emptySpaceStr = generateString((width - saleSummary.length())/2, " ");
		content.append(emptySpaceStr).append(saleSummary).append("\n");
		content.append(getSeperatorLine(0, width)).append("\n");
		content.append("Tran Type").append(generateString(width - 15 - 9, " "))
		.append("Count").append("    ").append("Amount").append("\n");
		content.append(getSeperatorLine(0, width)).append("\n");
		//countSaleGross + amountSaleGross
		String totalSaleCount = String.valueOf(Integer.valueOf(countRefund) + Integer.valueOf(countSale));
		content.append("Sale Gross").append(generateString(width - 10 - 10 - totalSaleCount.length(), " "))
		.append(totalSaleCount).append(generateString(10 - amountSaleGross.length() , " ")).append(amountSaleGross)
		.append("\n");
		//countRefond
		content.append("Refund Gross").append(generateString(width - 12 - 10 - countRefund.length(), " "))
		.append(countRefund).append(generateString(10 - refundGross.length() , " ")).append(refundGross)
		.append("\n");
		content.append(getSeperatorLine(1, width)).append("\n");
		
		//Net
		content.append("Net").append(generateString(width - 3 - 10 - countSale.length(), " "))
		.append(countSale).append(generateString(10 - net.length() , " ")).append(net)
		.append("\n");
		
		//HST
		content.append("HST").append(generateString(width - 3 - 10 - countRefund.length(), " "))
		.append(countRefund).append(generateString(10 - HST.length() , " ")).append(HST)
		.append("\n");
		
		strAryFR.add(content.toString());
	}
	
	private static void pushPaymentSummary(List<Bill> list) {};
	private static void pushSummaryByServiceType(List<Bill> list) {};
	private static void pushSummaryByOrder(List<Bill> list) {};
	private static void pushAuditSummary(List<Bill> list) {};
    
	private static String getSeperatorLine(int index, int tWidth) {
		//seperator
		if(sepLines == null) {
			sepLines = new String[2];
	        String sep_str1 = (String)CustOpts.custOps.getValue("sep_str1");
	        if(sep_str1 == null || sep_str1.length() == 0){
	            sep_str1 = SEP_STR1;
	        }
	        sepLines[0] = generateString(tWidth, sep_str1);

	        String sep_str2 = (String)CustOpts.custOps.getValue("sep_str2");
	        if(sep_str2 == null || sep_str2.length() == 0){
	            sep_str2 = SEP_STR2;
	        }

	        sepLines[1] = generateString(tWidth, sep_str2);
		}
		if(index > sepLines.length) {
			L.e("PrintService", "Unexpect index when getting item from sepLines with Index: " + index
					+ "the width of sepLines is " + sepLines.length, null);
		}
		return sepLines[index];
	}
	
    private static StringBuilder getFormattedBillHeader(int tWidth) {
    	StringBuilder sb = new StringBuilder();
    	String s = BarOption.getBillHeadInfo();
    	if(s != null && s.trim().length() > 0) {
    		String[] infos = s.split(":");
    		for(String info : infos) {
    			int length = (tWidth - info.length())/2;
    			sb.append(generateString(length, " "));
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
        int tWidth = width;
        if(font ==  null || font.length() < 1) {
            font = (String)CustOpts.custOps.getValue("font");
        }

        String w = (String)CustOpts.custOps.getValue(curPrintIp + "width");
        if( w== null || w.length() < 1) {
            w = (String)CustOpts.custOps.getValue("width");
        }
        try {
        	tWidth = Integer.valueOf(w);
        }catch(Exception e){
        	//do nothing, if no with property set, width will keep default value.
        }
        
        StringBuilder content = new StringBuilder("\n\n");
        if(tWidth < 20)
            content.append("\n\n");
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
        String spaceStr = generateString(tWidth - lengthOfStrToDisplay - 3, " ");
        
        content.append(spaceStr).append(dateStr).append("\n");

        String sep_str1 = isCancelled ? "!" : (String)CustOpts.custOps.getValue("sep_str1");
        if(sep_str1 == null || sep_str1.length() == 0){
            sep_str1 = SEP_STR1;
        }
        String sep_str2 = isCancelled ? "X" : (String)CustOpts.custOps.getValue("sep_str2");
        if(sep_str2 == null || sep_str2.length() == 0){
            sep_str2 = SEP_STR2;
        }

        content.append(generateString(tWidth, sep_str1)).append("\n\n");
        int langIndex = ipPrinterMap.get(curPrintIp).getType();
        for(Dish d:list){
            StringBuilder sb = new StringBuilder();
            if(BarOption.isDisDishIDInKitchen()) {
                sb.append(d.getId());
                sb.append(generateString(5 - String.valueOf(d.getId()).length(), " "));
            }
            sb.append(d.getLanguage(langIndex));
            if(d.getNum() > 1){
                String space = " ";
                int occupiedLength = getLengthOfString(sb.toString());
                sb.append(generateString(tWidth - occupiedLength - (d.getNum() < 10 ? 2 : 3), " "));
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
                    content.append(generateString(5, " ")).append("* ").append(lang).append(" *\n");
                }
            }
            Object sep = CustOpts.custOps.getValue("customizedSeperator");
            if(sep != null && !"null".equalsIgnoreCase(sep.toString())) {
            	if("".equals(sep)) {
            		sep = " ";
            	}
            	content.append(generateString(width, (String)sep)).append("\n");
            }
        }
        
        return content.substring(0, content.length());//spec change: do not to show separator!  - (tWidth + 1));
    }
    
    private static String generateString(int l, String character){
        StringBuilder sb = new StringBuilder("");
        for (int i = 0;i<l;i++){
            sb.append(character);
        }
        return sb.toString();
    }

    private static int getLengthOfString(String content){
        int length = content.length();
        int realWidth = length;
        for(int i = 0; i < length; i++) {
            char c = content.charAt(i);
            if(c >=19968 && c <= 171941) {
                realWidth++;
            }
        }
        return realWidth;
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
    
    final static StringBuilder mev1 = new StringBuilder("<reqMEV>\r\n")
    		.append("	<trans noVersionTrans=\"v02.00\" etatDoc=\"I\" modeTrans=\"O\" duplicata=\"N\">\r\n")
    		.append("		<doc><texte><![CDATA[");
    final static StringBuilder mev2 = 
    		new StringBuilder("				]]></texte></doc>\r\n")
    		.append("		<alignement>\r\n")
    		.append("			<![CDATA[\r\n")
    		.append("			]]>\r\n")
    		.append("		</alignement>\r\n");
    final static StringBuilder mev3 = new StringBuilder("		<donneesTrans typeTrans=\"ADDI\" reimpression=\"N\" autreCompte=\"S\" serveurTrans=\"\" tableTrans=\"Tab18\" \r\n")
    		.append("			paiementTrans=\"SOB\" comptoir=\"N\" numeroTrans=\"2\" dateTrans=\"20090128084800\" mtTransAvTaxes=\"+000021.85\"\r\n")
    		.append("			TPSTrans=\"+000001.09\" TVQTrans=\"+000001.72\" mtTransApTaxes=\"+000024.66\"/>\r\n");
    final static StringBuilder mev4 = new StringBuilder("		</trans>\r\n")
    		//.append("    <verif taille=\"4569\"/>\r\n")
    		.append("</reqMEV>");
    
    final static String mevDocAutre1 = "<reqMEV><docAutre noVersionAutre=\"v01.00\"><![CDATA[";
    final static String mevDocAutre2 = "]]></docAutre></reqMEV>";
}
