package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.beans.FunctionButton;
import org.cas.client.platform.cascustomize.CustOpts;

public class CustomerFrame extends JDialog implements ComponentListener{
	
	public CustomerFrame() {
		initComponent();
	}
 
	void initComponent() {
        
        billPanel = new BillPanel(null);

    	imagePanel = new Panel();
    	lblTotalPrice = new Label(BarFrame.consts.Total());
    	lblReceived = new Label(BarFrame.consts.Receive());
    	lblChange = new Label(BarFrame.consts.Change());
    	
    	valTotalPrice = new Label();
    	valReceived = new Label();
    	valChange = new Label();
    	
        // properties
        Color bg = BarOption.getBK("Sales");
    	if(bg == null) {
    		bg = new Color(216,216,216);
    	}
		setBackground(bg);
        setLayout(null);
        
        getContentPane().add(billPanel);
        getContentPane().add(imagePanel);
        getContentPane().add(lblTotalPrice);
        getContentPane().add(lblReceived);
        getContentPane().add(lblChange);
        getContentPane().add(valTotalPrice);
        getContentPane().add(valReceived);
        getContentPane().add(valChange);

        // add listener
        addComponentListener(this);
        
		reLayout();
    }
	
    public void reLayout() {
        billPanel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth()/2 - CustOpts.HOR_GAP * 2, getHeight() - CustOpts.VER_GAP * 2);
        imagePanel.setBounds(billPanel.getX() + billPanel.getWidth() + CustOpts.HOR_GAP, billPanel.getY(),
        		getWidth()/2 - CustOpts.HOR_GAP,  getHeight() - 300);

    	lblTotalPrice.setBounds(imagePanel.getX(), imagePanel.getY() + imagePanel.getHeight() + CustOpts.VER_GAP, 
    			40, 100 - CustOpts.VER_GAP);
    	lblReceived.setBounds(lblTotalPrice.getX(), lblTotalPrice.getY() + lblTotalPrice.getHeight() + CustOpts.VER_GAP,
    			40, 100);
    	lblChange.setBounds(lblReceived.getX(), lblReceived.getY() + lblReceived.getHeight() + CustOpts.VER_GAP, 
    			40, 100);
    	
    	valTotalPrice.setBounds(lblTotalPrice.getX() + lblTotalPrice.getWidth(), lblTotalPrice.getY(), imagePanel.getWidth() - lblTotalPrice.getWidth(), 100 - CustOpts.VER_GAP);
    	valReceived.setBounds(lblReceived.getX() + lblReceived.getWidth(), lblReceived.getY(), imagePanel.getWidth() - lblReceived.getWidth(), 100);
    	valChange.setBounds(lblChange.getX() + lblChange.getWidth(), lblChange.getY(), imagePanel.getWidth() - lblChange.getWidth(), 100);
    }

	@Override
	public void componentResized(ComponentEvent e) {
        reLayout();
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

	public BillPanel billPanel;
	public Panel imagePanel;
	private Label lblTotalPrice;
	private Label lblReceived;
	private Label lblChange;
	private Label valTotalPrice;
	private Label valReceived;
	private Label valChange;
	
}
