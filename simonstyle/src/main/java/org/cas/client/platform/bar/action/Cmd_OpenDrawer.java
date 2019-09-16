package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Enumeration;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.PrinterState;
import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.print.Command;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.casutil.ErrorUtil;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class Cmd_OpenDrawer implements SamActionListener {

	private static Cmd_OpenDrawer instance;
	private Cmd_OpenDrawer() {}
	public static Cmd_OpenDrawer getInstance() {
		if(instance == null)
			instance = new Cmd_OpenDrawer();
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
		String key = BarFrame.menuPanel.getPrinters()[0].getIp();
		if("".equals(key)) {
			return;// false;
		}
		
		if(key.equalsIgnoreCase("mev")) {
			try{
				PrintService.printThroughOSdriver(PrintService.getMevCommandFilePath("mevOpenCashierCommand.xml", Command.OPEN_CASHIER), new HashPrintRequestAttributeSet(), false);
				return;// true;
			} catch (Exception exp) {
				ErrorUtil.write(exp);
				return;// false;
			}
			
		}else if (key.equalsIgnoreCase("serial")){
			CommPortIdentifier commPortIdentifier;
			try{
				Enumeration tPorts = CommPortIdentifier.getPortIdentifiers();
		        if (tPorts == null || !tPorts.hasMoreElements()) {
		        	JOptionPane.showMessageDialog(BarFrame.instance, "no comm ports found! please check the printer connection.");
		        	return;// false;
		        }

		        while (tPorts.hasMoreElements()) {
		        	commPortIdentifier = (CommPortIdentifier) tPorts.nextElement();
		        	if (commPortIdentifier.getPortType() != CommPortIdentifier.PORT_SERIAL)
		                    continue;
					SerialPort tSerialPort = (SerialPort)commPortIdentifier.open("PrintService", 10000);//并口用"ParallelBlackBox"
					OutputStream outputStream = new DataOutputStream(tSerialPort.getOutputStream());
	                int[] cmd = BarOption.getOpenDrawerCommand();
	 				for (int i : cmd) {
	 					outputStream.write(i);
	 				}
	 				return;// true;
		        }
		        
		        return;// false;
			} catch (Exception exp) {
				ErrorUtil.write(exp);
				return;// false;
			}
		}else {
	    	try{
				Socket socket = new Socket(key != null ? key : BarFrame.menuPanel.getPrinters()[0].getIp(), 9100);
				BarFrame.setStatusMes("sockeet connected!");
				OutputStream outputStream = socket.getOutputStream();
				int[] cmd = BarOption.getOpenDrawerCommand();
				for (int i : cmd) {
					outputStream.write(i);
				}
				
				BarFrame.setStatusMes("Command Send!");
				outputStream.flush();
				socket.close();
				BarFrame.setStatusMes("Drawer Openned!");
				return;// true;
			} catch (Exception exp) {
				ErrorUtil.write(exp);
				return;// false;
			}
		}
	}
}
