package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;

import org.cas.client.platform.bar.net.RequestNewOrderThread;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.casutil.TaoEncrypt;

public class BarOption {
	public static final int MaxQTY = 10000;
	public static Font bigFont = new Font("Arial", Font.PLAIN, 48);
	public static Font lessBigFont = new Font("Arial", Font.PLAIN, 24);
	private static String qstAccount;
	private static String gstAccount;
	private static String billHeadInfo;
	
	public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getLicense(){
    	return (String)CustOpts.custOps.getValue("license");
    }
    public static void setLicense(String licence){
    	CustOpts.custOps.setKeyAndValue("license", licence);
    }
    
    public static String getLimitation(){
    	String limitation = (String)CustOpts.custOps.getValue("limitation");
    	if(limitation != null) {
    		try {
    			qstAccount = TaoEncrypt.decrypt(qstAccount, 1);
    		}catch(Exception e) {
    		}
    	}
    	return limitation;
    }

    public static long getActivateTimeLeft(){
    	String activateTimeLeft = (String)CustOpts.custOps.getValue("activateTimeLeft");
    	if(activateTimeLeft != null) {
    		try {
    			activateTimeLeft = TaoEncrypt.decrypt(activateTimeLeft, 1);
    		}catch(Exception e) {
    		}
    	}
    	return activateTimeLeft == null ? -1 : Long.valueOf(activateTimeLeft);
    }
    public static void setActivateTimeLeft(String activateTimeLeft){
    	CustOpts.custOps.setKeyAndValue("activateTimeLeft", TaoEncrypt.encryptPassword(activateTimeLeft));
    }

    public static String getLastSuccessStr() {
    	String lastsuccessStr = (String)CustOpts.custOps.getValue("lastsuccessStr");
    	if(lastsuccessStr != null) {
    		try {
    			lastsuccessStr = TaoEncrypt.decrypt(lastsuccessStr, 1);
    		}catch(Exception e) {
    		}
    	}
    	return lastsuccessStr;
    }
    public static void setLastSuccessStr(String lastsuccessStr) {
    	CustOpts.custOps.setKeyAndValue("lastsuccessStr", TaoEncrypt.encryptPassword(lastsuccessStr));
    }
    
    public static boolean isDebugMode() {
		return CustOpts.custOps.getValue("debug") == null ? false : 
			"true".equals(CustOpts.custOps.getValue("debug"));
	}
    
	public static int getPrinterMinWaiTime() {
		return CustOpts.custOps.getValue("PrinterMinWaiTime") == null ? 5000 : 
			Integer.valueOf((String)CustOpts.custOps.getValue("PrinterMinWaiTime"));
	}
	
	public static void setBK(Color color, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(color.getRed()).append(",").append(color.getGreen()).append(",").append(color.getBlue());
		CustOpts.custOps.setKeyAndValue(key, sb.toString());
	}
	public static Color getBK(String key) {
		String colorStr = (String)CustOpts.custOps.getValue(key);
		if(colorStr != null) {
			try {
				String[] colors = colorStr.split(",");
				return new Color(Integer.valueOf(colors[0]), Integer.valueOf(colors[1]), Integer.valueOf(colors[2]));
			}catch(Exception e) {
				return null;
			}
		}
		return null;
	}

    public static String getBillHeadInfo() {
    	if(billHeadInfo == null) {
	    	billHeadInfo = (String)CustOpts.custOps.getValue("jingqi");
	    	if(billHeadInfo != null) {
	    		try {
	    			billHeadInfo = TaoEncrypt.decrypt(billHeadInfo, 1);
	    		}catch(Exception e) {
	    		}
	    	}
    	}
    	return billHeadInfo;
    }
    public static void setBillHeadInfo(String billHeadInfo) {
    	if(billHeadInfo == null) {
    		CustOpts.custOps.setKeyAndValue("jingqi", "");
    		return;
    	}
    	
    	if(!billHeadInfo.endsWith("\n"))
    		billHeadInfo += "\n";
    	CustOpts.custOps.setKeyAndValue("jingqi", TaoEncrypt.encryptPassword(billHeadInfo));
    }
    
    public static String getBillFootInfo(){
       	return (String)CustOpts.custOps.getValue("BillFootInfo");
    }
    public static void setBillFootInfo(String billFootInfo) {
    	if(billFootInfo == null) {
    		L.e("setting BillFoot", "trying to set null to bill foot info.", null);
    		return;
    	}
    	
    	if(!billFootInfo.endsWith("\n"))
    		billFootInfo += "\n";
    	CustOpts.custOps.setKeyAndValue("BillFootInfo", billFootInfo);
    }
    
    private static Object getWebSite() {
		String headInfo = getBillHeadInfo();;
		if (headInfo != null) {
			int p = headInfo.indexOf(':');
			if(p > 0)
				return "www.sharethegoodones.com/" + headInfo.substring(0, p).trim();
	    }
		return "www.sharethegoodones.com/";
	}

	private static Object getTel() {
		String headInfo = getBillHeadInfo();;
		if (headInfo != null) {
			int p = headInfo.lastIndexOf(':');
			if(p > 0) {
				return headInfo.substring(p + 1).trim();
			}
		}
		return "514 552 5771";
	}

	private static Object getPostCode() {
		String headInfo = getBillHeadInfo();
		if (headInfo != null) {
			int p = headInfo.lastIndexOf(':');
			if(p > 0) {
				return headInfo.substring(p + 1).trim();
			}
		}
		return "H4L 3E8";
	}

	public static String getShopName() {
		String headInfo = getBillHeadInfo();;
		if (headInfo != null) {
			int p = headInfo.indexOf(':');
			if(p > 0) {
				return headInfo.substring(0, p).trim();
			}
		}
		return "noname";
	}
	
//	public static void setMaxRooBackWorkHour(int workhour) {
//		CustOpts.custOps.setKeyAndValue("MaxRooBackWorkHour", workhour);
//	}
//	public static int getMaxRollBackWorkHour() {
//		return CustOpts.custOps.getValue("MaxRooBackWorkHour") == null ? 
//				17 * 60 * 60 * 1000 : Integer.valueOf((String)CustOpts.custOps.getValue("MaxRooBackWorkHour"));
//	}
	
//	public static boolean isPrintBillWhenPay() {
//    	return CustOpts.custOps.getValue("isPrintBillWhenPay") == null ? 
//    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isPrintBillWhenPay"));
//    }
//    public static void setIsPrintBillWhenPay(Boolean isPrintBillWhenPay) {
//    	CustOpts.custOps.setKeyAndValue("isPrintBillWhenPay", String.valueOf(isPrintBillWhenPay));
//    }
	
	public static boolean isDisDishIDInKitchen(){
		return CustOpts.custOps.getValue("isDisDishIDInKitchen") == null ? 
    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isDisDishIDInKitchen"));
	}
	
	public static void setIsDisplayBillInKitchen(boolean isDisDishIDInKitchen){
		CustOpts.custOps.setKeyAndValue("isDisDishIDInKitchen", String.valueOf(isDisDishIDInKitchen));
	}
	public static boolean isDisplayBillInKitchen(){
		return CustOpts.custOps.getValue("isDisplayBillInKitchen") == null ? 
    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isDisplayBillInKitchen"));
	}

	public static boolean isDisplayWaiterInKitchen(){
		return CustOpts.custOps.getValue("displayWaiterInKitchen") == null ? 
    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("displayWaiterInKitchen"));
	}
	public static void setIsDisplayWaiterInKitchen(boolean displayWaiterInKitchen){
		CustOpts.custOps.setKeyAndValue("displayWaiterInKitchen", String.valueOf(displayWaiterInKitchen));
	}
	
    public static String getMoneySign() {
    	return CustOpts.custOps.getValue("moneySign") == null ? 
    			"$" : String.valueOf(CustOpts.custOps.getValue("moneySign"));
    }
    public static void setMoneySign(String moneySign) {
    	CustOpts.custOps.setKeyAndValue("moneySign", moneySign);
    }
    
    public static boolean isDiscountAffectTax() {
    	return CustOpts.custOps.getValue("isDisCountBeforeTax") == null ? 
    			true : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("isDisCountBeforeTax"));
    }
    public static void setIsDisCountBeforeTax(Boolean isDisCountBeforeTax) {
    	CustOpts.custOps.setKeyAndValue("isDisCountBeforeTax", String.valueOf(isDisCountBeforeTax));
    }
    
    public static boolean isServiceFeeAffectTax() {
    	return CustOpts.custOps.getValue("isServiceFeeAffectTax") == null ? 
    			true : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("isServiceFeeAffectTax"));
    }
    public static void setIsServiceFeeAffectTax(Boolean isServiceFeeAffectTax) {
    	CustOpts.custOps.setKeyAndValue("isServiceFeeAffectTax", String.valueOf(isServiceFeeAffectTax));
    }
    
    public static boolean isBuffetMode() {
    	return CustOpts.custOps.getValue("isBuffetMode") == null ? 
    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isBuffetMode"));
    }
    public static void setIsBuffetMode(Boolean isBuffetMode) {
    	CustOpts.custOps.setKeyAndValue("isBuffetMode", String.valueOf(isBuffetMode));
    }
    
    public static int getBillPageRow() {
    	return CustOpts.custOps.getValue("BillPanel_Row") == null ? 
    			1 : Integer.valueOf((String)CustOpts.custOps.getValue("BillPanel_Row"));
    }
    public static void setBillPageRow(String row) {
    	CustOpts.custOps.setKeyAndValue("BillPanel_Row", row);
    }
    
    public static int getBillPageCol() {
    	return CustOpts.custOps.getValue("BillPanel_Col") == null ? 
    			4 : Integer.valueOf((String)CustOpts.custOps.getValue("BillPanel_Col"));
    }
    public static void setBillPageCol(String col) {
    	CustOpts.custOps.setKeyAndValue("BillPanel_Col", col);
    }
    
    public static int getCategoryRow() {
    	return CustOpts.custOps.getValue("Category_Row") == null ? 
    			3 : Integer.valueOf((String)CustOpts.custOps.getValue("Category_Row"));
    }
    public static void setCategoryRow(String row) {
    	CustOpts.custOps.setKeyAndValue("Category_Row", row);
    }
    
    public static int getCategoryCol() {
    	return CustOpts.custOps.getValue("Category_Col") == null ? 
    			5 : Integer.valueOf((String)CustOpts.custOps.getValue("Category_Col"));
    }
    public static void setCategoryCol(String col) {
    	CustOpts.custOps.setKeyAndValue("Category_Col", col);
    }
    
    public static int getDishRow() {
    	return CustOpts.custOps.getValue("Dish_Row") == null ? 
    			4 : Integer.valueOf((String)CustOpts.custOps.getValue("Dish_Row"));
    }
    public static void setDishRow(String row) {
    	CustOpts.custOps.setKeyAndValue("Dish_Row", row);
    }
    
    public static int getDishCol() {
    	return CustOpts.custOps.getValue("Dish_Col") == null ? 
    			4 : Integer.valueOf((String)CustOpts.custOps.getValue("Dish_Col"));
    }
    public static void setDishCol(String col) {
    	CustOpts.custOps.setKeyAndValue("Dish_Col", col);
    }

    public static float getCategoryAreaPortion() {
    	return CustOpts.custOps.getValue("CategoryAreaPortion") == null ? 
    			0.4f : Float.valueOf((String)CustOpts.custOps.getValue("CategoryAreaPortion"));
    }
    public static void setCategoryAreaPortion(String col) {
    	CustOpts.custOps.setKeyAndValue("CategoryAreaPortion", col);
    }
    
    public static float getMenuAreaPortion() {
    	return CustOpts.custOps.getValue("MenuAreaPortion") == null ? 
    			0.6f : Float.valueOf((String)CustOpts.custOps.getValue("MenuAreaPortion"));
    }
    public static void setMenuAreaPortion(String col) {
    	CustOpts.custOps.setKeyAndValue("MenuAreaPortion", col);
    }
    
    public static boolean isSingleUser() {
    	return "true".equals(CustOpts.custOps.getValue("SingleUserMode"));
    }
    public static void setSingleUser(String is) {
    	CustOpts.custOps.setKeyAndValue("SingleUserMode", is);
    }
    
    public static void setFastFoodMode(boolean isFastFoodMode) {
    	CustOpts.custOps.setKeyAndValue("FastFoodMode", String.valueOf(isFastFoodMode));
    }
    public static boolean isFastFoodMode() {
    	return CustOpts.custOps.getValue("FastFoodMode") == null ? 
    			false : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("FastFoodMode"));
    }

    public static void setHideRecordFromOtherWaiter(boolean isHideRecordFromOtherWaiter) {
    	CustOpts.custOps.setKeyAndValue("HideRecordFromOtherWaiter", String.valueOf(isHideRecordFromOtherWaiter));
    }
    public static boolean isHideRecordFromOtherWaiter() {
    	return CustOpts.custOps.getValue("HideRecordFromOtherWaiter") == null ? 
    			false : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("HideRecordFromOtherWaiter"));
    }
    
    public static void setStartTime(String startTimeOfDay) {
    	CustOpts.custOps.setKeyAndValue("StartTimeOfDay", startTimeOfDay);
    }
    public static String getStartTime() {
    	return CustOpts.custOps.getValue("StartTimeOfDay") == null ? 
    			"00:00:00" : String.valueOf(CustOpts.custOps.getValue("StartTimeOfDay"));
    }
    
    public static void setPrinterMinReachTime(String printerMinReachTime) {
    	CustOpts.custOps.setKeyAndValue("PrinterMinReachTime", printerMinReachTime);
    }
    public static String getPrinterMinReachTime() {
    	return CustOpts.custOps.getValue("PrinterMinReachTime") == null ? 
    			"2468" : String.valueOf(CustOpts.custOps.getValue("PrinterMinReachTime"));
    }

    public static void setServerHost(String serverHost) {
    	CustOpts.custOps.setKeyAndValue("serverHost", serverHost);
    	RequestNewOrderThread.reInitURLString();	//a loop in thread is using a modified copy of this value.
    }
    public static String getServerHost() {
    	return CustOpts.custOps.getValue("serverHost") == null ? 
    		"" : String.valueOf(CustOpts.custOps.getValue("serverHost"));
    }

    public static void setShowCustomerFrame(boolean isShowCustomerFrame) {
    	CustOpts.custOps.setKeyAndValue("ShowCustomerFrame", String.valueOf(isShowCustomerFrame));
    }
	public static boolean isShowCustomerFrame() {
		return CustOpts.custOps.getValue("ShowCustomerFrame") == null ? 
    			false : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("ShowCustomerFrame"));
	}
	
	public static float getGST() {
	    	return CustOpts.custOps.getValue(BarFrame.consts.GST()) == null ? 
	    			5f : Float.valueOf((String)CustOpts.custOps.getValue(BarFrame.consts.GST()));
    }
    public static void setGST(String gst) {
    	CustOpts.custOps.setKeyAndValue(BarFrame.consts.GST(), gst);
    }
    
	public static float getQST() {
    	return CustOpts.custOps.getValue(BarFrame.consts.QST()) == null ? 
    			9.975f : Float.valueOf((String)CustOpts.custOps.getValue(BarFrame.consts.QST()));
	}
	public static void setQST(String qst) {
		CustOpts.custOps.setKeyAndValue(BarFrame.consts.QST(), qst);
	}

	public static String getGSTAccount() {
    	if(gstAccount == null) {
    		gstAccount = (String)CustOpts.custOps.getValue("ningmeng");
	    	if(gstAccount != null) {
	    		try {
	    			gstAccount = TaoEncrypt.decrypt(gstAccount, 1);
	    		}catch(Exception e) {
	    		}
	    	}
    	}
    	return gstAccount;
	}
	public static void setGSTAccount(String gst) {
		CustOpts.custOps.setKeyAndValue("ningmeng", TaoEncrypt.encryptPassword(gst));
	}
	public static String getQSTAccount() {
    	if(qstAccount == null) {
    		qstAccount = (String)CustOpts.custOps.getValue("aika");
	    	if(qstAccount != null) {
	    		try {
	    			qstAccount = TaoEncrypt.decrypt(qstAccount, 1);
	    		}catch(Exception e) {
	    		}
	    	}
    	}
    	return qstAccount;
	}
	public static void setQSTAccount(String qst) {
		CustOpts.custOps.setKeyAndValue("aika", TaoEncrypt.encryptPassword(qst));
	}

	public static boolean isTreatPricePromtAsTaxInclude() {
		return CustOpts.custOps.getValue("TreatPricePromtAsTaxInClude") == null ? 
    			false : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("TreatPricePromtAsTaxInClude"));
	}
	public static void setTreatPricePromtAsTaxInclude(boolean treatPricePromtAsTaxInClude) {
    	CustOpts.custOps.setKeyAndValue("TreatPricePromtAsTaxInClude", String.valueOf(treatPricePromtAsTaxInClude));
	}

	public static boolean isSavePrintInvoiceWhenBilled() {
		return CustOpts.custOps.getValue("isSavePrintInvoiceWhenBilled") == null ? 
    			false : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("isSavePrintInvoiceWhenBilled"));
	}
	public static void setSavePrintInvoiceWhenBilled(boolean isSavePrintInvoiceWhenBilled) {
		CustOpts.custOps.setKeyAndValue("isSavePrintInvoiceWhenBilled", String.valueOf(isSavePrintInvoiceWhenBilled));
	}
	
	public static boolean isWaiterAllowedToDiscount() {
		return CustOpts.custOps.getValue("isWaiterAllowedToDiscount") == null ? 
    			false : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("isWaiterAllowedToDiscount"));
	}
	public static void setIsWaiterAllowedToDiscount(boolean isWaiterAllowedToDiscount) {
		CustOpts.custOps.setKeyAndValue("isWaiterAllowedToDiscount", String.valueOf(isWaiterAllowedToDiscount));
	}

	public static boolean isWaiterAllowedToChangePrice() {
		return CustOpts.custOps.getValue("isWaiterAllowedToChangePrice") == null ? 
    			false : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("isWaiterAllowedToChangePrice"));
	}
	public static void setIsWaiterAllowedToChangePrice(boolean isWaiterAllowedToChangePrice) {
		CustOpts.custOps.setKeyAndValue("isWaiterAllowedToChangePrice", String.valueOf(isWaiterAllowedToChangePrice));
	}
	
	public static String getBillNumberStartStr() {
    	return CustOpts.custOps.getValue("BillNumberStartStr") == null ? 
    			"#" : (String)CustOpts.custOps.getValue("BillNumberStartStr");
	}
	public static void setBillNumberStartStr(String BillNumberStartStr) {
    	CustOpts.custOps.setKeyAndValue("BillNumberStartStr", BillNumberStartStr);
	}
	
	public static void setIsTrainingMode(String isTrainingMode) {
		CustOpts.custOps.setKeyAndValue("isTrainingMode", isTrainingMode);
		
	}
	public static boolean isTrainingMode() {
		return CustOpts.custOps.getValue("isTrainingMode") == null ? 
				false : "true".equalsIgnoreCase((String)CustOpts.custOps.getValue("isTrainingMode"));
	}
	
	public static int getTableRowHeight() {
		try{
			return Integer.valueOf((String)CustOpts.custOps.getValue("TableRowHeight"));
		}catch(Exception exp) {
			return 30;
		}
	}
	public static void setTableRowHeight(String height) {
		try {
	    	CustOpts.custOps.setKeyAndValue("TableRowHeight", Integer.valueOf(height));
		}catch(Exception exp) {
			CustOpts.custOps.setKeyAndValue("TableRowHeight", null);
		}
	}
}
