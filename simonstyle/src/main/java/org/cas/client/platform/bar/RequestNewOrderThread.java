package org.cas.client.platform.bar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cas.client.platform.bar.dialog.BarFrame;

public class RequestNewOrderThread extends Thread {
	
	String url;
	String jsonStr;
	
	public RequestNewOrderThread(String url) {
		if(!url.endsWith("/")) {
			url = url + "/";
		}
		if(!url.startsWith("http")) {
			url = "http://" + url;
		}
		this.url =  url + "requestNewOrders";
		jsonStr = BarFrame.prepareLicenceJSONString();
	}
    @Override
    public void run() {
    	while(true) {
    		try {
    			Thread.sleep(20000);
    		}catch(Exception e) {
    			//do nothing.
    		}
    		new HttpRequestClient(url, "POST", jsonStr, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.print(e.getActionCommand());
					
				}
			}).start();
    	}
    }
}
