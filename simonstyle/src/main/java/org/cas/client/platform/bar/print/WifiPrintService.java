package org.cas.client.platform.bar.print;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.model.Category;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.model.Printer;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;

public class WifiPrintService{

    public static int SUCCESS = -1;	//@NOTE:must be less than 0, because if it's 0, means the first element caused error.
    
    public static Category[] allCategory;
    
    public static HashMap<String,Printer> ipPrinterMap = new HashMap<>();
    private static HashMap<String,List<Dish>> ipSelectionsMap;
    private static HashMap<String,List<String>> ipContentMap;

    private static String curPrintIp = "";
    private static int width = 24;
    private String code = "GBK";
    private static String SEP_STR1 = "=";
    private static String SEP_STR2 = "-";


    private boolean printerConnectedFlag;
    private boolean contentReadyForPrintFlag;

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
    
    private static String mapToIP(Printer[] printers, int id){
    	String ip = "";
    	for (Printer printer : printers) {
			if(printer.getId() == id) {
				return printer.getIp();
			}
		}
    	return ip;
    }
    
    public static int exePrintCommand(List<Dish> selectdDishAry, Printer[] printers, String curTable, String curBill, String waiterName){
        //ErrorUtil.(TAG,"start to translate selection into ipContent for printing.");
        if(!isIpContentMapEmpty()){
        	return printContents();
        }
        
        reInitPrintRelatedMaps();
        
        //1、遍历每个选中的菜，并分别遍历加在其上的打印机。并在ipSelectionsMap上对应IP后面增加菜品
        for(Dish dish : selectdDishAry){
            String printerStr = dish.getPrinter();
            String[] ids = printerStr.split(",");
            String[] ips = new String[ids.length];
            for(int i = 0; i < ids.length; i++) {
            	ips[i] = mapToIP(printers, Integer.valueOf(ids[i]));
            }
            for(String ip: ips) {
                Printer printer = ipPrinterMap.get(ip);
                if(printer == null) {                   //should never happen, jist in case someone changed db.
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
                if(ipPrinterMap.get(printerIP).getFirstPrint() == 1){  //全单封装
                    ipContentMap.get(printerIP).add(formatContentForPrint(dishList, printerIP, curTable, curBill, waiterName) + "\n\n\n\n\n");
                }else{                                          //分单封装
                    for(Dish dish : dishList){
                        List<Dish> tlist = new ArrayList<Dish>();
                        tlist.add(dish);
                        ipContentMap.get(printerIP).add(formatContentForPrint(tlist, printerIP, curTable, curBill, waiterName) + "\n\n");
                    }
                }
            }
            //clear the ipSelectionsMap immediately
            ipSelectionsMap.get(printerIP).clear();
        }

        //L.d(TAG, "Order is translated into ipContentMap map and ready for print.");
        return printContents();
    }
    
    private static int printContents() {
    	BarFrame.instance.setStatusMes("PRINTED...");
        for(Entry<String,List<String>> entry : ipContentMap.entrySet()) {
        	List<String> contents = entry.getValue();
        	for(int i = contents.size() - 1; i >= 0 ; i--) {
        		String sndMes = contents.get(i);
            	if(doZiJiangPrint(entry.getKey(), null, sndMes)) {
            		contents.remove(i);//clean ipcontent;
            	}else {
            		return i;	//stop here, and return the error index.
            	}
        	}
        }
    	ipContentMap.clear();
        return SUCCESS;
    }
    
    private static boolean doZiJiangPrint(String ip, String font, String sndMsg){
		try {
			Socket socket = new Socket(ip, 9100);
			OutputStream outputStream = socket.getOutputStream();

			if (!"silent".equals(CustOpts.custOps.getValue("mode"))) {
				outputStream.write(Command.BEEP);
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

			// cut the paper.
			outputStream.write(Command.GS_V_m_n);

			outputStream.flush();
			socket.close();
			return true;
		} catch (Exception exp) {
			ErrorUtil.write(exp);
			return false;
		}
    }
    
    private static String formatContentForPrint(List<Dish> list, String curPrintIp, String curTable, String curBill, String waiterName){
        //L.d(TAG,"formatContentForPrint");
        String font = (String)CustOpts.custOps.getValue(curPrintIp + "font");
        if(font ==  null || font.length() < 1) {
            font = (String)CustOpts.custOps.getValue("font");
        }
        if(font != null && font.length() > 0){
            String w = (String)CustOpts.custOps.getValue(curPrintIp + "width");
            if( w== null || w.length() < 1) {
                w = (String)CustOpts.custOps.getValue("width");
            }
            try {
                width = Integer.valueOf(w);
            }catch(Exception e){

            }
        }
        StringBuilder content = new StringBuilder("\n\n");
        if(width < 20)
            content.append("\n\n");
        content.append("(").append(curTable).append(")");

        int lengthOfStrToDisplay = 3 + curTable.length();
        if(BarOption.isDisplayBillInKitchen()) {
        	content.append(curBill);
        	lengthOfStrToDisplay += curBill.length();
        }
        
        if(BarOption.isDoNotDisplayWaiterInKitchen()) {	//the first 3 spaces of the spaceStr is consumed first.
        	content.append("   ").append(waiterName);
        	lengthOfStrToDisplay += waiterName.length();
        }else {
        	content.append("   ");
        }

        DateFormat df = new SimpleDateFormat("HH:mm");
        String dateStr = df.format(new Date());
        lengthOfStrToDisplay += dateStr.length();
        String spaceStr = generateString(width - lengthOfStrToDisplay - 3, " ");
        
        content.append(spaceStr).append(dateStr).append("\n");

        String sep_str1 = (String)CustOpts.custOps.getValue("sep_str1");
        if(sep_str1 == null || sep_str1.length() == 0){
            sep_str1 = SEP_STR1;
        }
        String sep_str2 = (String)CustOpts.custOps.getValue("sep_str2");
        if(sep_str2 == null || sep_str2.length() == 0){
            sep_str2 = SEP_STR2;
        }

        content.append(generateString(width, sep_str1)).append("\n\n");
        int langIndex = ipPrinterMap.get(curPrintIp).getType();
        for(Dish dd:list){
            StringBuilder sb = new StringBuilder();
            if(BarOption.isDisDishIDInKitchen()) {
                sb.append(dd.getId());
                sb.append(generateString(5 - String.valueOf(dd.getId()).length(), " "));
            }
            sb.append(dd.getLanguage(langIndex));
            if(dd.getNum() > 1){
                String space = " ";
                int occupiedLength = getLengthOfString(sb.toString());
                sb.append(generateString(width - occupiedLength - (dd.getNum() < 10 ? 2 : 3), " "));
                sb.append("x").append(Integer.toString(dd.getNum()));
            }
            content.append(sb);
            content.append("\n");
            if(dd.getModification() != null) {
            	String modifyStr = dd.getModification();
            	String[] notes = modifyStr.split(BarDlgConst.delimiter); 
                for (String str : notes) {
                	String[] langs = str.split(BarDlgConst.semicolon);
                	String lang = langs.length > langIndex ? langs[langIndex] : langs[0];
                	if(lang.length() == 0)
                		lang = langs[0];
                    content.append(generateString(5, " ")).append("* ").append(lang).append(" *\n");
                }
            }
            content.append(generateString(width, sep_str2)).append("\n");
        }
        return content.substring(0, content.length() - (width + 1));
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

    private static boolean isIpContentMapEmpty(){
		if (ipContentMap != null) {
			for (Map.Entry entry : ipContentMap.entrySet()) {
				List<String> listTypeValue = (List<String>) entry.getValue();
				if (listTypeValue.size() > 0) {
					return false;
				}
			}
		}
		return true;
    }    

    public static void reInitPrintRelatedMaps(){
        ipContentMap = new HashMap<String,List<String>>();
        ipSelectionsMap = new HashMap<String,List<Dish>>();

        for(Entry<String,Printer> entry: ipPrinterMap.entrySet()){
            ipSelectionsMap.put(entry.getKey(),new ArrayList<Dish>());
            ipContentMap.put(entry.getKey(),new ArrayList<String>());
        }
    }
}
