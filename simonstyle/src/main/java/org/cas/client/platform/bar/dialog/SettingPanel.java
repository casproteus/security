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
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.beans.FunctionButton;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.dialog.statistics.CheckInOutListDlg;
import org.cas.client.platform.bar.dialog.statistics.EmployeeListDlg;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.resource.international.PaneConsts;

//Identity表应该和Employ表合并。
public class SettingPanel extends JPanel implements ComponentListener, ActionListener, FocusListener {
    
    private int width = 24;
    private String code = "GBK";
    private String SEP_STR1 = "=";
    private String SEP_STR2 = "-";
    
    public static String startTime;

    //flags
    NumberPanelDlg numberPanelDlg; 
    
    //for print
    public static String SUCCESS = "0";
    public static String ERROR = "2";
    
    
    public SettingPanel() {
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
        
        
        if(o == tfdBillPageRow) {
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
    		int idx = content.indexOf(":");
    		if(idx >= 0) {
    			String key = content.substring(0, idx);
    			String value = content.substring(idx + 1);
    	    	CustOpts.custOps.setKeyAndValue(key, value);
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
    	}
    }

    // ActionListner-------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof FunctionButton) {
        	if(o == btnLine_2_1) {
        		LoginDlg.USERTYPE = -1;
        		BarFrame.instance.switchMode(0);
        		if(BarOption.isSingleUser()) {
    				BarFrame.instance.setVisible(false);
    				BarFrame.singleUserLoginProcess();
    			}else {
    				new LoginDlg(null).setVisible(true);
	                if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
	                	BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
	                }
    			}
        		
        	}else if(o == btnLine_2_2) {
        		new TabbleSettingDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_3) {
        		//addMenuRelatedComps();
        	}else if(o == btnLine_2_4) {
        		new AddModificationDialog(BarFrame.instance, "").setVisible(true);
        	}else if(o == btnLine_2_5) {
        		new SettingPrinterDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_6) {
        		new BillFootDialog(BarFrame.instance, BarOption.getBillFootInfo()).setVisible(true);
        	}else if(o == btnLine_2_7) {
        		new EmployeeListDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_8) {
        		new CheckInOutListDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_9) {
        		new SettingColorDlg(BarFrame.instance).setVisible(true);
        	}
        }
        //JToggleButton-------------------------------------------------------------------------------------
        else if(o instanceof JToggleButton) {
        	if(o == cbxIsSingleUser) {
        		BarOption.setSingleUser(cbxIsSingleUser.isSelected() ? "true" : "false");
        	}else if(o == cbxIsDiscBeforeTax) {
        		BarOption.setIsDisCountBeforeTax(cbxIsDiscBeforeTax.isSelected() ? true : false);
        	}else if(o == cbxIsBuffetMode) {
        		BarOption.setIsBuffetMode(cbxIsBuffetMode.isSelected() ? true : false);
        	}else if(o == cbxIsFastFoodMode) {
        		BarOption.setFastFoodMode(cbxIsFastFoodMode.isSelected() ? true : false);
//        	}else if(o == cbxIsPrintBillWhenPay) {
//        		BarOption.setIsPrintBillWhenPay(cbxIsPrintBillWhenPay.isSelected() ? true : false);
        	}
        }
    }

    void reLayout() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 10) / 9;
        int tBtnHeight = panelHeight / 10;

        // command buttons--------------
        btnLine_2_1.setBounds(CustOpts.HOR_GAP, panelHeight - tBtnHeight - CustOpts.VER_GAP, tBtnWidht,
                tBtnHeight);
        btnLine_2_2.setBounds(btnLine_2_1.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_3.setBounds(btnLine_2_2.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_4.setBounds(btnLine_2_3.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_5.setBounds(btnLine_2_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_6.setBounds(btnLine_2_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_7.setBounds(btnLine_2_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_8.setBounds(btnLine_2_7.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_9.setBounds(btnLine_2_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);

        // TOP part============================
        lblBillPageRow.setBounds(CustOpts.HOR_GAP * 4, CustOpts.VER_GAP * 4,
        		lblBillPageRow.getPreferredSize().width, lblBillPageRow.getPreferredSize().height);
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

        //cbx------------------------------------
        cbxIsSingleUser.setBounds(lblBillPageRow.getX(), lblPrinterMinReachTime.getY() + lblPrinterMinReachTime.getHeight() + CustOpts.VER_GAP * 4,
        		cbxIsSingleUser.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsDiscBeforeTax.setBounds(cbxIsSingleUser.getX(), cbxIsSingleUser.getY() + cbxIsSingleUser.getHeight() + CustOpts.VER_GAP,
        		cbxIsDiscBeforeTax.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsBuffetMode.setBounds(cbxIsDiscBeforeTax.getX(), cbxIsDiscBeforeTax.getY() + cbxIsDiscBeforeTax.getHeight() + CustOpts.VER_GAP,
        		cbxIsBuffetMode.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsFastFoodMode.setBounds(cbxIsBuffetMode.getX(), cbxIsBuffetMode.getY() + cbxIsBuffetMode.getHeight() + CustOpts.VER_GAP,
        		cbxIsFastFoodMode.getPreferredSize().width, CustOpts.BTN_HEIGHT);
//        cbxIsPrintBillWhenPay.setBounds(CustOpts.HOR_GAP, cbxIsDiscBeforeTax.getY() + cbxIsDiscBeforeTax.getHeight() + CustOpts.VER_GAP,
//        		cbxIsPrintBillWhenPay.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        //menu area----------
		if(BarFrame.instance != null && BarFrame.instance.menuPanel != null) {
			BarFrame.instance.menuPanel.reLayout();
		}
        lblCategoryRow.setBounds(BarFrame.instance.menuPanel.getX(), BarFrame.menuPanel.getY() + BarFrame.menuPanel.getHeight() + CustOpts.HOR_GAP,
        		lblCategoryRow.getPreferredSize().width, lblCategoryRow.getPreferredSize().height);
        tfdCategoryRow.setBounds(lblCategoryRow.getX() + lblCategoryRow.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblCategoryCol.setBounds(tfdCategoryRow.getX() + tfdCategoryRow.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		lblCategoryCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCategoryCol.setBounds(lblCategoryCol.getX() + lblCategoryCol.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblDishRow.setBounds(lblCategoryRow.getX(), lblCategoryRow.getY() + lblCategoryRow.getHeight() + CustOpts.HOR_GAP,
        		lblDishRow.getPreferredSize().width, lblDishRow.getPreferredSize().height);
        tfdDishRow.setBounds(tfdCategoryRow.getX(), lblDishRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblDishCol.setBounds(lblCategoryCol.getX(), lblDishRow.getY(),
        		lblDishCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdDishCol.setBounds(tfdCategoryCol.getX(), lblDishRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblCategoryAreaPortion.setBounds(tfdCategoryCol.getX() + tfdCategoryCol.getWidth() + CustOpts.HOR_GAP * 4, tfdCategoryCol.getY(),
        		lblCategoryAreaPortion.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCategoryAreaPortion.setBounds(lblCategoryAreaPortion.getX() + lblCategoryAreaPortion.getWidth() + CustOpts.HOR_GAP, lblCategoryAreaPortion.getY(),
        		40, CustOpts.BTN_HEIGHT);
        
    }

    private void initComponent() {
    	removeAll();
        lblBillPageRow = new JLabel(BarFrame.consts.BillPageRow());
        tfdBillPageRow = new JTextField(String.valueOf(BarOption.getBillPageRow()));
        lblBillPageCol = new JLabel(BarFrame.consts.BillPageCol());
        tfdBillPageCol = new JTextField(String.valueOf(BarOption.getBillPageCol()));

        cbxIsSingleUser = new JCheckBox(BarFrame.consts.IsSingleUser());
        cbxIsDiscBeforeTax = new JCheckBox(BarFrame.consts.IsDiscBeforeTax());
        cbxIsBuffetMode = new JCheckBox(BarFrame.consts.IsBuffetMode());
        cbxIsFastFoodMode = new JCheckBox(BarFrame.consts.IsFastFoodMode());
//        cbxIsPrintBillWhenPay = new JCheckBox(BarFrame.consts.IsPrintBillWhenPay);
        lblStartTimeOfDay = new JLabel(BarFrame.consts.StartTimeOfDay());
        tfdStartTimeOfDay = new JTextField(String.valueOf(BarOption.getStartTime()));
        lblPrinterMinReachTime = new JLabel(BarFrame.consts.PrinterMinReachTime());
        tfdPrinterMinReachTime = new JTextField(String.valueOf(BarOption.getPrinterMinReachTime()));
        
        //compomnents for setting category and menus
        lblCategoryRow = new JLabel(BarFrame.consts.CategoryRow());
        tfdCategoryRow = new JTextField(String.valueOf(BarOption.getCategoryRow()));
        lblCategoryCol = new JLabel(BarFrame.consts.CategoryCol());
        tfdCategoryCol = new JTextField(String.valueOf(BarOption.getCategoryCol()));
        lblDishRow = new JLabel(BarFrame.consts.DishRow());
        tfdDishRow = new JTextField(String.valueOf(BarOption.getDishRow()));
        lblDishCol = new JLabel(BarFrame.consts.DishCol());
        tfdDishCol = new JTextField(String.valueOf(BarOption.getDishCol()));
        lblCategoryAreaPortion = new JLabel(BarFrame.consts.CategoryAreaPortion());
        tfdCategoryAreaPortion = new JTextField(String.valueOf(BarOption.getCategoryAreaPortion()));
        
        btnLine_2_1 = new FunctionButton(BarFrame.consts.RETURN());
        btnLine_2_2 = new FunctionButton(BarFrame.consts.TABLE());
        btnLine_2_3 = new FunctionButton("TBD");
        btnLine_2_4 = new FunctionButton(BarFrame.consts.Modify());
        btnLine_2_5 = new FunctionButton(BarFrame.consts.PRINTER());
        btnLine_2_6 = new FunctionButton(BarFrame.consts.BillInfo());
        btnLine_2_7 = new FunctionButton(BarFrame.consts.Operator());
        btnLine_2_8 = new FunctionButton(BarFrame.consts.CheckInOut());
        btnLine_2_9 = new FunctionButton(BarFrame.consts.Color().toUpperCase());

        // properties
        setBackground(BarOption.getBK("Setting"));
        setLayout(null);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);
        
        cbxIsSingleUser.setBackground(null);
        cbxIsDiscBeforeTax.setBackground(null);
        cbxIsSingleUser.setSelected(BarOption.isSingleUser());
        cbxIsDiscBeforeTax.setSelected(BarOption.isDisCountBeforeTax());
        cbxIsBuffetMode.setSelected(BarOption.isBuffetMode());
        cbxIsFastFoodMode.setSelected(BarOption.isFastFoodMode());
//        cbxIsPrintBillWhenPay.setSelected(BarOption.isPrintBillWhenPay());

        addControlButtons();
        addMenuRelatedComps();
        addOtherComponentes();

        // add listener
        addComponentListener(this);

        tfdCategoryRow.addFocusListener(this);
        tfdCategoryCol.addFocusListener(this);
        tfdDishRow.addFocusListener(this);
        tfdDishCol.addFocusListener(this);
        tfdCategoryAreaPortion.addFocusListener(this);
        
        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        // tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）
        btnLine_2_1.addActionListener(this);
        btnLine_2_2.addActionListener(this);
        btnLine_2_3.addActionListener(this);
        btnLine_2_4.addActionListener(this);
        btnLine_2_5.addActionListener(this);
        btnLine_2_6.addActionListener(this);
        btnLine_2_7.addActionListener(this);
        btnLine_2_8.addActionListener(this);
        btnLine_2_9.addActionListener(this);

        tfdBillPageRow.addFocusListener(this);
        tfdBillPageCol.addFocusListener(this);
        cbxIsSingleUser.addActionListener(this);
        cbxIsDiscBeforeTax.addActionListener(this);
        cbxIsBuffetMode.addActionListener(this);
        cbxIsFastFoodMode.addActionListener(this);
//        cbxIsPrintBillWhenPay.addActionListener(this);
        tfdStartTimeOfDay.addFocusListener(this);
        tfdPrinterMinReachTime.addFocusListener(this);
        
		reLayout();
    }

	private void addControlButtons() {
		add(btnLine_2_9);
        add(btnLine_2_2);
        add(btnLine_2_3);
        add(btnLine_2_4);
        add(btnLine_2_5);
        add(btnLine_2_6);
        add(btnLine_2_7);
        add(btnLine_2_8);
        add(btnLine_2_1);
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
		
	}
	
	private void addOtherComponentes() {
		add(lblBillPageRow);
        add(tfdBillPageRow);
        
        add(lblBillPageCol);
        add(tfdBillPageCol);
   
        add(lblStartTimeOfDay);
        add(tfdStartTimeOfDay);
        
        add(lblPrinterMinReachTime);
        add(tfdPrinterMinReachTime);
        
        add(cbxIsSingleUser);
        add(cbxIsDiscBeforeTax);
        add(cbxIsBuffetMode);
        add(cbxIsFastFoodMode);
//        add(cbxIsPrintBillWhenPay);
	}

	JLabel lblBillPageRow;
    JTextField tfdBillPageRow;
    JLabel lblBillPageCol;
    JTextField tfdBillPageCol;

    JLabel lblStartTimeOfDay;
    JTextField tfdStartTimeOfDay;
    JLabel lblPrinterMinReachTime;
    JTextField tfdPrinterMinReachTime;
    
    JCheckBox cbxIsSingleUser;
    JCheckBox cbxIsDiscBeforeTax;
    JCheckBox cbxIsBuffetMode;
    JCheckBox cbxIsFastFoodMode;
//    JCheckBox cbxIsPrintBillWhenPay;
    
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
    
    private FunctionButton btnLine_2_1;
    private FunctionButton btnLine_2_2;
    private FunctionButton btnLine_2_3;
    private FunctionButton btnLine_2_4;
    private FunctionButton btnLine_2_5;
    private FunctionButton btnLine_2_6;
    private FunctionButton btnLine_2_7;
    private FunctionButton btnLine_2_8;
    private FunctionButton btnLine_2_9;
}
