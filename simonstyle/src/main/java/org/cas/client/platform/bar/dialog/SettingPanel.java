package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimview.PicturePane;
import org.cas.client.resource.international.PaneConsts;

//Identity表应该和Employ表合并。
public class SettingPanel extends PicturePane implements ComponentListener, ActionListener, FocusListener {
    
    static final int MAX_CMDBTN_QT = 10;

	public static String startTime;

    //flags
    NumberPanelDlg numberPanelDlg; 
    
    //for print
    public static String SUCCESS = "0";
    public static String ERROR = "2";
    
    
    public SettingPanel() {
    	super(CustOpts.custOps.getMainPaneBGImg());
        initComponent();
    }

    // ComponentListener-----------------------------
    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(ComponentEvent e) {}

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(ComponentEvent e) {}

    /** Invoked when the component has been made invisible. */
    @Override
    public void componentHidden(ComponentEvent e) {}

    private String oldContent = null;
    @Override
    public void focusGained(
            FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField) {
            ((JTextField) o).selectAll();
            oldContent = ((JTextField) o).getText().trim();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        Object o = e.getSource();
        //if content not changed, ignore.
        if (o instanceof JTextField) {
        	if(oldContent.equals(((JTextField)o).getText().trim())) {
        		return;
        	}
        }
        
        
        if(o == tfdGST) {
    		float num;
    		try {
    			num = Float.valueOf(tfdGST.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num > 100 || num < 0) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setGST(tfdGST.getText());
            
    	}else if(o == tfdQST) {
    		float num;
    		try {
    			num = Float.valueOf(tfdQST.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num > 100 || num < 0) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setQST(tfdQST.getText());
            
    	}else if(o == tfdBillPageRow) {
    		int num;
    		try {
    			num = Integer.valueOf(tfdBillPageRow.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num < 1 || num > 2) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setBillPageRow(tfdBillPageRow.getText());
            ((BillListPanel)BarFrame.instance.panels[1]).initComponent();
            
    	}else if(o == tfdBillPageCol) {
    		int num;
    		try {
    			num = Integer.valueOf(tfdBillPageCol.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num < 1 || num > 16) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setBillPageCol(tfdBillPageCol.getText());
            ((BillListPanel)BarFrame.instance.panels[1]).initComponent();
            
    	}else if(o == tfdStartTimeOfDay) {
    		String time = tfdStartTimeOfDay.getText();
    		try {
	    		int endIndex = time.indexOf(":");
	    		int hh = Integer.valueOf(time.substring(0, endIndex));
	    		if(hh > 23 || hh < 0) {
	    			throw new Exception("invalidate format of hh");
	    		}
	    		
	    		time = time.substring(endIndex + 1);
	    		endIndex = time.indexOf(":");
	    		int mm = Integer.valueOf(time.substring(0, endIndex));
	    		if(mm > 59 || hh < 0) {
	    			throw new Exception("invalidate format of mm");
	    		}
	    		
	    		int ss = Integer.valueOf(time.substring(endIndex + 1));
	    		if(ss > 59 || ss < 0) {
	    			throw new Exception("invalidate format of ss");
	    		}
	    		BarOption.setStartTime(tfdStartTimeOfDay.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidInput());
    			tfdStartTimeOfDay.grabFocus();
    		}
    	}else if(o == tfdPrinterMinReachTime) {
    		String content = tfdPrinterMinReachTime.getText();
    		if(content.toLowerCase().equals("cuscmd")) {
    			new CmdBtnsDlg(BarFrame.instance).setVisible(true);
    			return;
    		}
    		if(content.contains(":") || content.contains("=") || content.contains("?")) {
    			int idx = content.indexOf(":");
        		if(idx >= 0) {
        			String key = content.substring(0, idx);
        			if(key.startsWith("-")) {
        				key = key.substring(1);
        			}
        			String value = BarUtil.encryptIfNeeded(key, content.substring(idx + 1));
        	    	CustOpts.custOps.setKeyAndValue(key, value);
        		}else {
        			idx = content.indexOf(":");
        			if(idx <= 0) {
        				idx = content.indexOf("=");
        			}
            		if(idx >= 0) {
            			idx = content.indexOf("?");
	            		if(idx >= 0) {
	            			String key = content.substring(0, idx);
	            			if(key.startsWith("-")) {
	            				key = key.substring(1);
	            			}
	            			String value = BarUtil.encryptIfNeeded(key, content.substring(idx + 1));
	            	    	CustOpts.custOps.setKeyAndValue(key, value);
	            		}
            		}else {
            			String key = content.substring(0, idx);
            			if(key.startsWith("-")) {
            				key = key.substring(1);
            			}
            			String value = (String)CustOpts.custOps.getValue(key);
            			tfdPrinterMinReachTime.setText(value);
        			}
        		}
    		}else {
	    		try {
		    		int t = Integer.valueOf(content);
		    		if(t < 10) {
		    			t *= 1000;
		    		}
		    		BarOption.setPrinterMinReachTime(String.valueOf(t));
	    		}catch(Exception exp) {
	    			JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidInput());
	    		}
    		}
    		
    	}else if(o == tfdServerHost) {
			BarOption.setServerHost(tfdServerHost.getText());
    	}else if(o == tfdCategoryRow) {
    		int num;
    		try {
    			num = Integer.valueOf(tfdCategoryRow.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num < 1 || num > 6) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setCategoryRow(tfdCategoryRow.getText());
    		BarFrame.menuPanel.initComponent();
    		
    	}else if(o == tfdCategoryCol) {
    		int num;
    		try {
    			num = Integer.valueOf(tfdCategoryCol.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num < 1 || num > 16) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setCategoryCol(tfdCategoryCol.getText());
    		BarFrame.menuPanel.initComponent();
    		
    	}else if(o == tfdDishRow) {
    		int num;
    		try {
    			num = Integer.valueOf(tfdDishRow.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num < 1 || num > 7) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setDishRow(tfdDishRow.getText());
    		BarFrame.menuPanel.initComponent();
    		
    	}else if(o == tfdDishCol) {
    		int num;
    		try {
    			num = Integer.valueOf(tfdDishCol.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num < 1 || num > 16) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setDishCol(tfdDishCol.getText());
    		BarFrame.menuPanel.initComponent();
    		
    	}else if(o == tfdCategoryAreaPortion) {
    		Float num;
    		try {
    			num = Float.valueOf(tfdCategoryAreaPortion.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num < 0 || num > 1) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setCategoryAreaPortion(tfdCategoryAreaPortion.getText());
    		BarFrame.menuPanel.initComponent();
    	}else if(o == tfdMenuAreaPortion) {
    		Float num;
    		try {
    			num = Float.valueOf(tfdMenuAreaPortion.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		if(num < 0 || num > 1) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
				return;
    		}
    		BarOption.setMenuAreaPortion(tfdMenuAreaPortion.getText());
    		((SalesPanel)BarFrame.instance.panels[2]).initComponent();
    		reLayout();
    	}
    }

    // ActionListner-------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if(o instanceof JToggleButton) {
        	if(o == cbxIsTrainingMode) {
        		BarOption.setIsTrainingMode(cbxIsTrainingMode.isSelected() ? "true" : "false");
        	}else if(o == cbxIsSingleUserMode) {
        		BarOption.setSingleUser(cbxIsSingleUserMode.isSelected() ? "true" : "false");
        	}else if(o == cbxIsDiscoutAffectTax) {
        		BarOption.setIsDisCountBeforeTax(cbxIsDiscoutAffectTax.isSelected() ? true : false);
        	}else if(o == cbxIsServiceFeeAffectTax) {
        		BarOption.setIsServiceFeeAffectTax(cbxIsServiceFeeAffectTax.isSelected() ? true : false);
        	}else  if(o == cbxIsBuffetMode) {
        		BarOption.setIsBuffetMode(cbxIsBuffetMode.isSelected() ? true : false);
        	}else if(o == cbxIsFastFoodMode) {
        		BarOption.setCounterMode(cbxIsFastFoodMode.isSelected() ? true : false);
        	}else if(o == cbxIsHideRecordFromOtherWaiter) {
        		BarOption.setHideRecordFromOtherWaiter(cbxIsHideRecordFromOtherWaiter.isSelected() ? true : false);
        	}else if(o == cbxIsWaiterAllowedToDiscount) {
        		BarOption.setIsWaiterAllowedToDiscount(cbxIsWaiterAllowedToDiscount.isSelected() ? true : false);
        	}else if(o == cbxIsWaiterAllowedToChangePrice) {
        		BarOption.setIsWaiterAllowedToChangePrice(cbxIsWaiterAllowedToChangePrice.isSelected() ? true : false);
        	}else if(o == cbxTreatPricePromtAsTaxInclude) {
        		BarOption.setTreatPricePromtAsTaxInclude(cbxTreatPricePromtAsTaxInclude.isSelected() ? true : false);
        	}else if(o == cbxShowCustomerFrame) {
        		BarOption.setShowCustomerFrame(cbxShowCustomerFrame.isSelected() ? true : false);
        	}
        }
    }

    void reLayout() {

        // command buttons--------------
        int top = BarUtil.layoutCommandButtons(this, CmdBtnsDlg.groupedButtons[3], MAX_CMDBTN_QT);
        if(top < 0)
        	return;
        // TOP part============================
		lblGST.setBounds(CustOpts.HOR_GAP * 4, CustOpts.VER_GAP * 4,
				lblGST.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdGST.setBounds(lblGST.getX() + lblGST.getWidth() + CustOpts.HOR_GAP, lblGST.getY(),
        		90, CustOpts.BTN_HEIGHT);
        lblQST.setBounds(tfdGST.getX() + tfdGST.getWidth() + CustOpts.HOR_GAP * 2, tfdGST.getY(),
        		lblQST.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdQST.setBounds(lblQST.getX() + lblQST.getWidth() + CustOpts.HOR_GAP, lblQST.getY(),
        		90, CustOpts.BTN_HEIGHT);
        
        lblBillPageRow.setBounds(lblGST.getX(), lblGST.getY() + lblGST.getHeight() + CustOpts.VER_GAP,
        		lblBillPageRow.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdBillPageRow.setBounds(lblBillPageRow.getX() + lblBillPageRow.getWidth() + CustOpts.HOR_GAP, lblBillPageRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblBillPageCol.setBounds(tfdBillPageRow.getX() + tfdBillPageRow.getWidth() + CustOpts.HOR_GAP, lblBillPageRow.getY(),
        		lblBillPageCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdBillPageCol.setBounds(lblBillPageCol.getX() + lblBillPageCol.getWidth() + CustOpts.HOR_GAP, lblBillPageRow.getY(),
        		20, CustOpts.BTN_HEIGHT);

        lblStartTimeOfDay.setBounds(lblBillPageRow.getX(), lblBillPageCol.getY() + lblBillPageCol.getHeight() + CustOpts.VER_GAP,
        		lblStartTimeOfDay.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdStartTimeOfDay.setBounds(lblStartTimeOfDay.getX() + lblStartTimeOfDay.getWidth() + CustOpts.HOR_GAP, lblStartTimeOfDay.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblPrinterMinReachTime.setBounds(lblBillPageRow.getX(), lblStartTimeOfDay.getY() + lblStartTimeOfDay.getHeight() + CustOpts.VER_GAP,
        		lblPrinterMinReachTime.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdPrinterMinReachTime.setBounds(lblPrinterMinReachTime.getX() + lblPrinterMinReachTime.getWidth() + CustOpts.HOR_GAP, lblPrinterMinReachTime.getY(),
        		100, CustOpts.BTN_HEIGHT);

    	sepH1.setBounds(CustOpts.HOR_GAP * 4, 
    			lblPrinterMinReachTime.getY() + lblPrinterMinReachTime.getHeight() + CustOpts.VER_GAP * 2,
    			BarFrame.menuPanel.getX() - CustOpts.HOR_GAP * 7 ,
    			sepH1.getPreferredSize().height);
    	
        //cbx------------------------------------
    	cbxIsTrainingMode.setBounds(lblServerHost.getX(), sepH1.getY() + sepH1.getHeight() + CustOpts.VER_GAP * 2,
    			cbxIsTrainingMode.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsSingleUserMode.setBounds(cbxIsTrainingMode.getX(), cbxIsTrainingMode.getY() + cbxIsTrainingMode.getHeight() + CustOpts.VER_GAP * 2,
        		cbxIsSingleUserMode.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsBuffetMode.setBounds(cbxIsSingleUserMode.getX(), cbxIsSingleUserMode.getY() + cbxIsSingleUserMode.getHeight() + CustOpts.VER_GAP,
        		cbxIsBuffetMode.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsFastFoodMode.setBounds(cbxIsBuffetMode.getX(), cbxIsBuffetMode.getY() + cbxIsBuffetMode.getHeight() + CustOpts.VER_GAP,
        		cbxIsFastFoodMode.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsDiscoutAffectTax.setBounds(cbxIsFastFoodMode.getX(), cbxIsFastFoodMode.getY() + cbxIsFastFoodMode.getHeight() + CustOpts.VER_GAP,
        		cbxIsDiscoutAffectTax.getPreferredSize().width, CustOpts.BTN_HEIGHT);
//        cbxIsServiceFeeAffectTax.setBounds(cbxIsDiscoutAffectTax.getX(), cbxIsDiscoutAffectTax.getY() + cbxIsDiscoutAffectTax.getHeight() + CustOpts.VER_GAP,
//        		cbxIsServiceFeeAffectTax.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsHideRecordFromOtherWaiter.setBounds(cbxIsDiscoutAffectTax.getX(), cbxIsDiscoutAffectTax.getY() + cbxIsDiscoutAffectTax.getHeight() + CustOpts.VER_GAP,
        		cbxIsHideRecordFromOtherWaiter.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsWaiterAllowedToDiscount.setBounds(cbxIsHideRecordFromOtherWaiter.getX(), cbxIsHideRecordFromOtherWaiter.getY() + cbxIsHideRecordFromOtherWaiter.getHeight() + CustOpts.VER_GAP,
        		cbxIsWaiterAllowedToDiscount.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsWaiterAllowedToChangePrice.setBounds(cbxIsWaiterAllowedToDiscount.getX(), cbxIsWaiterAllowedToDiscount.getY() + cbxIsWaiterAllowedToDiscount.getHeight() + CustOpts.VER_GAP,
        		cbxIsWaiterAllowedToChangePrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		cbxTreatPricePromtAsTaxInclude.setBounds(cbxIsWaiterAllowedToChangePrice.getX(), cbxIsWaiterAllowedToChangePrice.getY() + cbxIsWaiterAllowedToChangePrice.getHeight() + CustOpts.VER_GAP,
        		cbxTreatPricePromtAsTaxInclude.getPreferredSize().width, CustOpts.BTN_HEIGHT);		
        cbxShowCustomerFrame.setBounds(cbxTreatPricePromtAsTaxInclude.getX(), cbxTreatPricePromtAsTaxInclude.getY() + cbxTreatPricePromtAsTaxInclude.getHeight() + CustOpts.VER_GAP,
        		cbxShowCustomerFrame.getPreferredSize().width, CustOpts.BTN_HEIGHT);
    	sepH2.setBounds(CustOpts.HOR_GAP * 4, 
    			cbxShowCustomerFrame.getY() + cbxShowCustomerFrame.getHeight() + CustOpts.VER_GAP * 2,
    			BarFrame.menuPanel.getX() - CustOpts.HOR_GAP * 7 ,
    			sepH2.getPreferredSize().height);
    	
    	//------------------------------------------------
    	lblServerHost.setBounds(sepH2.getX(), sepH2.getY() + sepH2.getHeight() + CustOpts.VER_GAP * 2,
         		lblServerHost.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdServerHost.setBounds(lblServerHost.getX() + lblServerHost.getWidth() + CustOpts.HOR_GAP, lblServerHost.getY(),
         		200, CustOpts.BTN_HEIGHT);
        
        //==========================================================
    	sepV.setBounds(BarFrame.menuPanel.getX() - sepV.getPreferredSize().width - CustOpts.HOR_GAP, 
    			BarFrame.menuPanel.getY(),
    			sepV.getPreferredSize().width, 
    			BarFrame.menuPanel.getHeight() + CustOpts.BTN_HEIGHT * 2 + CustOpts.VER_GAP);
    	
        //menu area----------
		if(BarFrame.instance != null && BarFrame.menuPanel != null) {
			BarFrame.menuPanel.reLayout();
		}
        lblCategoryRow.setBounds(BarFrame.menuPanel.getX(), BarFrame.menuPanel.getY() + BarFrame.menuPanel.getHeight() + CustOpts.HOR_GAP,
        		lblCategoryRow.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCategoryRow.setBounds(lblCategoryRow.getX() + lblCategoryRow.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblCategoryCol.setBounds(tfdCategoryRow.getX() + tfdCategoryRow.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		lblCategoryCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCategoryCol.setBounds(lblCategoryCol.getX() + lblCategoryCol.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblDishRow.setBounds(lblCategoryRow.getX(), lblCategoryRow.getY() + lblCategoryRow.getHeight() + CustOpts.HOR_GAP,
        		lblDishRow.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdDishRow.setBounds(tfdCategoryRow.getX(), lblDishRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblDishCol.setBounds(lblCategoryCol.getX(), lblDishRow.getY(),
        		lblDishCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdDishCol.setBounds(tfdCategoryCol.getX(), lblDishRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblCategoryAreaPortion.setBounds(BarFrame.menuPanel.getX() + BarFrame.menuPanel.getWidth() - 40 - CustOpts.HOR_GAP - lblCategoryAreaPortion.getPreferredSize().width, 
        		tfdCategoryCol.getY(),
        		lblCategoryAreaPortion.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCategoryAreaPortion.setBounds(lblCategoryAreaPortion.getX() + lblCategoryAreaPortion.getWidth() + CustOpts.HOR_GAP,
        		lblCategoryAreaPortion.getY(),
        		40, CustOpts.BTN_HEIGHT);
        lblMenuAreaPortion.setBounds(lblCategoryAreaPortion.getX(), tfdDishCol.getY(),
        		lblMenuAreaPortion.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdMenuAreaPortion.setBounds(tfdCategoryAreaPortion.getX(), lblMenuAreaPortion.getY(),
        		40, CustOpts.BTN_HEIGHT);
    }

    private void initComponent() {
    	removeAll();

        sepH1 = new JSeparator(SwingConstants.HORIZONTAL);
        sepH2 = new JSeparator(SwingConstants.HORIZONTAL);
        sepV = new JSeparator(SwingConstants.VERTICAL);
        
        lblGST = new JLabel(BarFrame.consts.GST());
        tfdGST = new JTextField(String.valueOf(BarOption.getGST()));
        lblQST = new JLabel(BarFrame.consts.QST());
        tfdQST = new JTextField(String.valueOf(BarOption.getQST()));
        
        lblBillPageRow = new JLabel(BarFrame.consts.BillPageRow());
        tfdBillPageRow = new JTextField(String.valueOf(BarOption.getBillPageRow()));
        lblBillPageCol = new JLabel(BarFrame.consts.BillPageCol());
        tfdBillPageCol = new JTextField(String.valueOf(BarOption.getBillPageCol()));

        cbxIsTrainingMode = new JCheckBox(BarFrame.consts.IsTrainingMode());
        cbxIsSingleUserMode = new JCheckBox(BarFrame.consts.IsSingleUser());
        cbxIsDiscoutAffectTax = new JCheckBox(BarFrame.consts.IsTaxNotAllowDiscount());
        cbxIsServiceFeeAffectTax = new JCheckBox(BarFrame.consts.IsServiceFeeAffectTax());
        cbxIsBuffetMode = new JCheckBox(BarFrame.consts.IsBuffetMode());
        cbxIsFastFoodMode = new JCheckBox(BarFrame.consts.IsFastFoodMode());
        cbxIsHideRecordFromOtherWaiter = new JCheckBox(BarFrame.consts.IsHideRecordFromOtherWaiter());
        cbxIsWaiterAllowedToDiscount = new JCheckBox(BarFrame.consts.isWaiterAllowedToDiscount());
        cbxIsWaiterAllowedToChangePrice = new JCheckBox(BarFrame.consts.isWaiterAllowedToChangePrice());
        
        cbxTreatPricePromtAsTaxInclude = new JCheckBox(BarFrame.consts.TreatPricePromtAsTaxInclude());
        cbxShowCustomerFrame = new JCheckBox(BarFrame.consts.IsShowCustomerFrame());
        lblStartTimeOfDay = new JLabel(BarFrame.consts.StartTimeOfDay());
        tfdStartTimeOfDay = new JTextField(String.valueOf(BarOption.getStartTime()));
        lblPrinterMinReachTime = new JLabel(BarFrame.consts.PrinterMinReachTime());
        tfdPrinterMinReachTime = new JTextField(String.valueOf(BarOption.getPrinterMinReachTime()));
        lblServerHost = new JLabel(BarFrame.consts.ServerHost());
        tfdServerHost = new JTextField(String.valueOf(BarOption.getServerHost()));
        
        //compomnents for setting category and menus
        lblCategoryRow = new JLabel(BarFrame.consts.RowsofCategoryButton());
        tfdCategoryRow = new JTextField(String.valueOf(BarOption.getCategoryRow()));
        lblCategoryCol = new JLabel(BarFrame.consts.ColumnsofCategoryButton());
        tfdCategoryCol = new JTextField(String.valueOf(BarOption.getCategoryCol()));
        lblDishRow = new JLabel(BarFrame.consts.RowsofDishButton());
        tfdDishRow = new JTextField(String.valueOf(BarOption.getDishRow()));
        lblDishCol = new JLabel(BarFrame.consts.ColumnsofDishButton());
        tfdDishCol = new JTextField(String.valueOf(BarOption.getDishCol()));
        lblCategoryAreaPortion = new JLabel(BarFrame.consts.PortionofCategoryArea());
        tfdCategoryAreaPortion = new JTextField(String.valueOf(BarOption.getCategoryAreaPortion()));
        lblMenuAreaPortion = new JLabel(BarFrame.consts.PortionofMenuArea());
        tfdMenuAreaPortion = new JTextField(String.valueOf(BarOption.getMenuAreaPortion()));
        
        // properties
        Color bg = BarOption.getBK("Setting");
    	if(bg == null) {
    		bg = new Color(216,216,216);
    	}
        setBackground(bg);
        setLayout(null);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);
        
        cbxIsTrainingMode.setOpaque(false);
        cbxIsSingleUserMode.setOpaque(false);
        cbxIsDiscoutAffectTax.setOpaque(false);
        cbxIsServiceFeeAffectTax.setOpaque(false);
        cbxIsBuffetMode.setOpaque(false);
        cbxIsFastFoodMode.setOpaque(false);
        cbxIsHideRecordFromOtherWaiter.setOpaque(false);
        cbxIsWaiterAllowedToDiscount.setOpaque(false);
        cbxIsWaiterAllowedToChangePrice.setOpaque(false);

        cbxTreatPricePromtAsTaxInclude.setOpaque(false);
        cbxShowCustomerFrame.setOpaque(false);
        
        cbxIsTrainingMode.setSelected(BarOption.isTrainingMode());
        cbxIsSingleUserMode.setSelected(BarOption.isSingleUser());
        cbxIsDiscoutAffectTax.setSelected(BarOption.isDiscountAffectTax());
        cbxIsServiceFeeAffectTax.setSelected(BarOption.isServiceFeeAffectTax());
        cbxIsBuffetMode.setSelected(BarOption.isBuffetMode());
        cbxIsFastFoodMode.setSelected(BarOption.isCounterMode());
        cbxIsHideRecordFromOtherWaiter.setSelected(BarOption.isHideRecordFromOtherWaiter());
        cbxIsWaiterAllowedToDiscount.setSelected(BarOption.isWaiterAllowedToDiscount());
        cbxIsWaiterAllowedToChangePrice.setSelected(BarOption.isWaiterAllowedToChangePrice());

        cbxTreatPricePromtAsTaxInclude.setSelected(BarOption.isTreatPricePromtAsTaxInclude());
        cbxShowCustomerFrame.setSelected(BarOption.isShowCustomerFrame());

        BarUtil.addFunctionButtons(this, CmdBtnsDlg.groupedButtons[3], MAX_CMDBTN_QT);
        addMenuRelatedComps();
        addOtherComponentes();

        // add listener
        addComponentListener(this);

        tfdCategoryRow.addFocusListener(this);
        tfdCategoryCol.addFocusListener(this);
        tfdDishRow.addFocusListener(this);
        tfdDishCol.addFocusListener(this);
        tfdCategoryAreaPortion.addFocusListener(this);
        lblMenuAreaPortion.addFocusListener(this);
        tfdMenuAreaPortion.addFocusListener(this);
        
        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        // tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）

        tfdGST.addFocusListener(this);
        tfdQST.addFocusListener(this);
        tfdBillPageRow.addFocusListener(this);
        tfdBillPageCol.addFocusListener(this);
        cbxIsTrainingMode.addActionListener(this);
        cbxIsSingleUserMode.addActionListener(this);
        cbxIsDiscoutAffectTax.addActionListener(this);
        cbxIsServiceFeeAffectTax.addActionListener(this);
        cbxIsBuffetMode.addActionListener(this);
        cbxIsFastFoodMode.addActionListener(this);
        cbxIsHideRecordFromOtherWaiter.addActionListener(this);
        cbxIsWaiterAllowedToDiscount.addActionListener(this);
        cbxIsWaiterAllowedToChangePrice.addActionListener(this);
        
        cbxTreatPricePromtAsTaxInclude.addActionListener(this);
        cbxShowCustomerFrame.addActionListener(this);
        
        tfdStartTimeOfDay.addFocusListener(this);
        tfdPrinterMinReachTime.addFocusListener(this);
        tfdServerHost.addFocusListener(this);
		reLayout();
    }

	private void addMenuRelatedComps() {
		add(BarFrame.menuPanel);
		add(lblCategoryRow);
		add(tfdCategoryRow);
		add(lblCategoryCol);
		add(tfdCategoryCol);
		add(lblDishRow);
		add(tfdDishRow);
		add(lblDishCol);
		add(tfdDishCol);
		add(lblCategoryAreaPortion);
		add(tfdCategoryAreaPortion);
		add(lblMenuAreaPortion);
		add(tfdMenuAreaPortion);
	}
	
	private void addOtherComponentes() {

		add(lblGST);
        add(tfdGST);
        
        add(lblQST);
        add(tfdQST);
        
		add(lblBillPageRow);
        add(tfdBillPageRow);
        
        add(lblBillPageCol);
        add(tfdBillPageCol);
   
        add(lblStartTimeOfDay);
        add(tfdStartTimeOfDay);
        
        add(lblPrinterMinReachTime);
        add(tfdPrinterMinReachTime);
        add(lblServerHost);
        add(tfdServerHost);
        
        add(cbxIsTrainingMode);
        add(cbxIsSingleUserMode);
        add(cbxIsDiscoutAffectTax);
        add(cbxIsServiceFeeAffectTax);
        add(cbxIsBuffetMode);
        add(cbxIsFastFoodMode);
        add(cbxIsHideRecordFromOtherWaiter);
        add(cbxIsWaiterAllowedToDiscount);
        add(cbxIsWaiterAllowedToChangePrice);
        add(cbxTreatPricePromtAsTaxInclude);
        add(cbxShowCustomerFrame);
        
        add(sepH1);
        add(sepH2);
        add(sepV);
	}

    JLabel lblGST;
    JTextField tfdGST;
    JLabel lblQST;
    JTextField tfdQST;
    
	JLabel lblBillPageRow;
    JTextField tfdBillPageRow;
    JLabel lblBillPageCol;
    JTextField tfdBillPageCol;

    JLabel lblStartTimeOfDay;
    JTextField tfdStartTimeOfDay;
    JLabel lblPrinterMinReachTime;
    JTextField tfdPrinterMinReachTime;
    JLabel lblServerHost;
    JTextField tfdServerHost;

    JCheckBox cbxIsTrainingMode;
    JCheckBox cbxIsSingleUserMode;
    JCheckBox cbxIsBuffetMode;
    JCheckBox cbxIsFastFoodMode;
    
    JCheckBox cbxIsDiscoutAffectTax;
    JCheckBox cbxIsServiceFeeAffectTax;
    JCheckBox cbxIsHideRecordFromOtherWaiter;
    JCheckBox cbxIsWaiterAllowedToDiscount;
    JCheckBox cbxIsWaiterAllowedToChangePrice;
    
    JCheckBox cbxTreatPricePromtAsTaxInclude;
    
    JCheckBox cbxShowCustomerFrame;
    
    //component for setting category and menus
    JLabel lblCategoryRow;
    JTextField tfdCategoryRow;
    JLabel lblCategoryCol;
    JTextField tfdCategoryCol;
    JLabel lblDishRow;
    JTextField tfdDishRow;
    JLabel lblDishCol;
    JTextField tfdDishCol;
    JLabel lblCategoryAreaPortion;
    JTextField tfdCategoryAreaPortion;
    JLabel lblMenuAreaPortion;
    JTextField tfdMenuAreaPortion;
    
	JSeparator sepH1;
	JSeparator sepH2;
	JSeparator sepV;
	

}
