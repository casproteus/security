package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;

public class CustomerFrame extends JDialog implements ComponentListener{
	
	Image temp = null;
	private ImageIcon icon;
	
	public CustomerFrame() {
		initComponent();
	}
 
	void initComponent() {
        
        billPanel = new BillPanel(null);

    	String fileName = CASUtility.getPIMDirPath() + "CustomerFrameBG.jpg";
		icon = new ImageIcon(fileName);
    	imagePanel = new JLabel(icon);
    	
    	lblTotalPrice = new Label(BarFrame.consts.Total() + " : ");
    	lblReceived = new Label(BarFrame.consts.Receive() + " : ");
    	lblChange = new Label(BarFrame.consts.Change() + " : ");
    	
    	valTotalPrice = new Label();
    	valReceived = new Label();
    	valChange = new Label();

    	lblTotalPrice.setFont(BarOption.bigFont);
    	lblReceived.setFont(BarOption.lessBigFont);
    	lblChange.setFont(BarOption.bigFont);
    	valTotalPrice.setFont(BarOption.bigFont);
    	valReceived.setFont(BarOption.lessBigFont);
    	valChange.setFont(BarOption.bigFont);

    	valTotalPrice.setAlignment(Label.RIGHT);
    	valReceived.setAlignment(Label.RIGHT);
    	valChange.setAlignment(Label.RIGHT);
    	
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
        billPanel.resetColWidth(CustOpts.SCRWIDTH/2 - CustOpts.HOR_GAP * 4);
    }
	
    private void reLayout() {
    	if(temp == null && getWidth() > 0 && getHeight() > 0) {
	   		temp = icon.getImage().getScaledInstance(getWidth()/2 - CustOpts.HOR_GAP,  getHeight() - CustOpts.VER_GAP * 9  - 300,
				icon.getImage().SCALE_DEFAULT);
		
	   		icon = new ImageIcon(temp);
	   		imagePanel.setIcon(icon);
    	}
        billPanel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 
        		getWidth()/2 - CustOpts.HOR_GAP * 4, getHeight() - CustOpts.VER_GAP * 9);
        
        imagePanel.setBounds(billPanel.getX() + billPanel.getWidth() + CustOpts.HOR_GAP, billPanel.getY(),
        		getWidth()/2 - CustOpts.HOR_GAP,  getHeight() - CustOpts.VER_GAP * 9  - 300);

    	lblTotalPrice.setBounds(imagePanel.getX(), imagePanel.getY() + imagePanel.getHeight() + CustOpts.VER_GAP, 
    			lblTotalPrice.getPreferredSize().width, 100 - CustOpts.VER_GAP);
    	lblReceived.setBounds(lblTotalPrice.getX(), lblTotalPrice.getY() + lblTotalPrice.getHeight() + CustOpts.VER_GAP,
    			lblReceived.getPreferredSize().width, 100);
    	lblChange.setBounds(lblReceived.getX(), lblReceived.getY() + lblReceived.getHeight() + CustOpts.VER_GAP, 
    			lblChange.getPreferredSize().width, 100);
    	
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
	public JLabel imagePanel;
	private Label lblTotalPrice;
	private Label lblReceived;
	private Label lblChange;
	private Label valTotalPrice;
	private Label valReceived;
	private Label valChange;

	public void initContent() {
		billPanel.initContent();
		valTotalPrice.setText(BarOption.getMoneySign() + billPanel.valTotlePrice.getText());
	}

	public void updateTotal(String total) {
		valTotalPrice.setText(BarOption.getMoneySign() + total);
	}
	
	public void updateChange(String received, String left, boolean isPayingWithCash) {
		valReceived.setText(BarOption.getMoneySign() + received);
		valChange.setText(BarOption.getMoneySign() + left);
		if(left.startsWith("-") && !isPayingWithCash) {
			lblChange.setText(BarFrame.consts.Tip() + " : ");
		}else {
			lblChange.setText(BarFrame.consts.Change() + " : ");
		}
	}
	
}
