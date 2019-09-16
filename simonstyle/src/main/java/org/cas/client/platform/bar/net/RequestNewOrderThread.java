package org.cas.client.platform.bar.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.TablesPanel;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.model.Printer;
import org.cas.client.platform.bar.net.action.CreateNewOrderAction;
import org.cas.client.platform.bar.net.bean.MainOrder;
import org.cas.client.platform.bar.net.bean.Material;
import org.cas.client.platform.bar.net.bean.TextContent;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

import flexjson.JSONDeserializer;

public class RequestNewOrderThread extends Thread{
	
	static String url;
	String jsonStr;
	 
    @Override
    public void run() {
    	
    	reInitURLString();
		jsonStr = BarFrame.prepareLicenceJSONString();
		
    	while(true) {
    		try {
    			Thread.sleep(10000);
    		}catch(Exception e) {
    			//do nothing.
    		}
    		if(url != null && url.trim().length() > 1 && !"null".equals(url.trim().toLowerCase())) {
    			new HttpRequestClient(url, "POST", jsonStr, CreateNewOrderAction.getInstance()).start();
    		}
    	}
    }

	public static void reInitURLString() {
		url = NetUtil.validateURL(BarOption.getServerHost());	//if not valid, the url will be return directly.
		if(url != null && url.trim().length() > 1 && !"null".equals(url.trim().toLowerCase())) {	//so need to check again. 
			if(!url.endsWith("/")) {
				url = url + "/";
			}
			
			String storeName = BarOption.getBillHeadInfo();
	    	int p = storeName.indexOf(":");
	    	if(p > 0) {
	    		storeName = storeName.substring(0, p).trim();
	    	}
	    	
			url =  url + storeName + "/requestNewOrders";
		}
	}
}
