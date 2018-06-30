package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.beans.ColorChooserButton;
import org.cas.client.platform.bar.beans.ColorChooserButton.ColorChangedListener;
import org.cas.client.platform.bar.beans.FunctionButton;
import org.cas.client.platform.bar.dialog.modifyDish.AddModificationDialog;
import org.cas.client.platform.bar.dialog.statistics.CheckInOutListDlg;
import org.cas.client.platform.bar.dialog.statistics.EmployeeListDlg;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.resource.international.PaneConsts;

import gnu.io.CommPortIdentifier;
import gnu.io.ParallelPort;
import gnu.io.PortInUseException;

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

    @Override
    public void focusGained(
            FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        Object o = e.getSource();
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
    		String time = tfdPrinterMinReachTime.getText();
    		try {
	    		int t = Integer.valueOf(time);
	    		if(t < 10) {
	    			t *= 1000;
	    		}
	    		BarOption.setPrinterMinReachTime(String.valueOf(t));
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidInput());
    		}
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
        	}else if(o == btnLine_2_2) {
        		new TabbleSettingDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_3) {
        		new SettingPrinterDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_4) {
        		new CheckInOutListDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_5) {
        		new EmployeeListDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_6) {
        		new AddModificationDialog(BarFrame.instance, "").setVisible(true);
        	}else if(o == btnLine_2_7) {
        		new BillFootDialog(BarFrame.instance, BarOption.getBillFootInfo()).setVisible(true);
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
        int topAreaHeight = btnLine_2_1.getY() - 3 * CustOpts.VER_GAP;
        // table area-------------
        Double tableWidth = (Double) CustOpts.custOps.hash2.get("TableWidth");
        tableWidth = (tableWidth == null || tableWidth < 0.2) ? 0.4 : tableWidth;

        lblBillPageRow.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
        		lblBillPageRow.getPreferredSize().width, lblBillPageRow.getPreferredSize().height);
        tfdBillPageRow.setBounds(lblBillPageRow.getX() + lblBillPageRow.getWidth() + CustOpts.HOR_GAP, lblBillPageRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblBillPageCol.setBounds(tfdBillPageRow.getX() + tfdBillPageRow.getWidth() + CustOpts.HOR_GAP, lblBillPageRow.getY(),
        		lblBillPageCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdBillPageCol.setBounds(lblBillPageCol.getX() + lblBillPageCol.getWidth() + CustOpts.HOR_GAP, lblBillPageRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblCategoryRow.setBounds(CustOpts.HOR_GAP, lblBillPageRow.getY() + lblBillPageRow.getHeight() + CustOpts.HOR_GAP,
        		lblCategoryRow.getPreferredSize().width, lblCategoryRow.getPreferredSize().height);
        tfdCategoryRow.setBounds(lblCategoryRow.getX() + lblCategoryRow.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblCategoryCol.setBounds(tfdCategoryRow.getX() + tfdCategoryRow.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		lblCategoryCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdCategoryCol.setBounds(lblCategoryCol.getX() + lblCategoryCol.getWidth() + CustOpts.HOR_GAP, lblCategoryRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblDishRow.setBounds(CustOpts.HOR_GAP, lblCategoryRow.getY() + lblCategoryRow.getHeight() + CustOpts.HOR_GAP,
        		lblDishRow.getPreferredSize().width, lblDishRow.getPreferredSize().height);
        tfdDishRow.setBounds(lblDishRow.getX() + lblDishRow.getWidth() + CustOpts.HOR_GAP, lblDishRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        lblDishCol.setBounds(tfdDishRow.getX() + tfdDishRow.getWidth() + CustOpts.HOR_GAP, lblDishRow.getY(),
        		lblDishCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdDishCol.setBounds(lblDishCol.getX() + lblDishCol.getWidth() + CustOpts.HOR_GAP, lblDishRow.getY(),
        		20, CustOpts.BTN_HEIGHT);
        
        cbxIsSingleUser.setBounds(CustOpts.HOR_GAP, lblDishRow.getY() + lblDishRow.getHeight() + CustOpts.VER_GAP,
        		cbxIsSingleUser.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsDiscBeforeTax.setBounds(CustOpts.HOR_GAP, cbxIsSingleUser.getY() + cbxIsSingleUser.getHeight() + CustOpts.VER_GAP,
        		cbxIsDiscBeforeTax.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsBuffetMode.setBounds(CustOpts.HOR_GAP, cbxIsDiscBeforeTax.getY() + cbxIsDiscBeforeTax.getHeight() + CustOpts.VER_GAP,
        		cbxIsBuffetMode.getPreferredSize().width, CustOpts.BTN_HEIGHT);
//        cbxIsPrintBillWhenPay.setBounds(CustOpts.HOR_GAP, cbxIsDiscBeforeTax.getY() + cbxIsDiscBeforeTax.getHeight() + CustOpts.VER_GAP,
//        		cbxIsPrintBillWhenPay.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        
        lblStartTimeOfDay.setBounds(CustOpts.HOR_GAP, cbxIsBuffetMode.getY() + cbxIsBuffetMode.getHeight() + CustOpts.VER_GAP,
        		lblStartTimeOfDay.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdStartTimeOfDay.setBounds(lblStartTimeOfDay.getX() + lblStartTimeOfDay.getWidth() + CustOpts.HOR_GAP, lblStartTimeOfDay.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblPrinterMinReachTime.setBounds(CustOpts.HOR_GAP, lblStartTimeOfDay.getY() + lblStartTimeOfDay.getHeight() + CustOpts.VER_GAP,
        		lblPrinterMinReachTime.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdPrinterMinReachTime.setBounds(lblPrinterMinReachTime.getX() + lblPrinterMinReachTime.getWidth() + CustOpts.HOR_GAP, lblPrinterMinReachTime.getY(),
        		100, CustOpts.BTN_HEIGHT);
        
        lblLoginBackGround.setBounds(CustOpts.HOR_GAP, lblPrinterMinReachTime.getY() + lblPrinterMinReachTime.getHeight() + CustOpts.VER_GAP,
        		lblLoginBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbLogin.setBounds(lblLoginBackGround.getX() + lblLoginBackGround.getWidth() + CustOpts.HOR_GAP, lblLoginBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        
        lblTablePanelBackGround.setBounds(CustOpts.HOR_GAP, lblLoginBackGround.getY() + lblLoginBackGround.getHeight() + CustOpts.VER_GAP,
        		lblTablePanelBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbTablePanel.setBounds(lblTablePanelBackGround.getX() + lblTablePanelBackGround.getWidth() + CustOpts.HOR_GAP, lblTablePanelBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblBillBackGround.setBounds(CustOpts.HOR_GAP, lblTablePanelBackGround.getY() + lblTablePanelBackGround.getHeight() + CustOpts.VER_GAP,
        		lblBillBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbBill.setBounds(lblBillBackGround.getX() + lblBillBackGround.getWidth() + CustOpts.HOR_GAP, lblBillBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblSalesBackGround.setBounds(CustOpts.HOR_GAP, lblBillBackGround.getY() + lblBillBackGround.getHeight() + CustOpts.VER_GAP,
        		lblSalesBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbSales.setBounds(lblSalesBackGround.getX() + lblSalesBackGround.getWidth() + CustOpts.HOR_GAP, lblSalesBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblSettingsBackGround.setBounds(CustOpts.HOR_GAP, lblSalesBackGround.getY() + lblSalesBackGround.getHeight() + CustOpts.VER_GAP,
        		lblSettingsBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbSetting.setBounds(lblSettingsBackGround.getX() + lblSettingsBackGround.getWidth() + CustOpts.HOR_GAP, lblSettingsBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblCategoryBackGround.setBounds(CustOpts.HOR_GAP, lblSettingsBackGround.getY() + lblSettingsBackGround.getHeight() + CustOpts.VER_GAP,
        		lblCategoryBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbCategory.setBounds(lblCategoryBackGround.getX() + lblCategoryBackGround.getWidth() + CustOpts.HOR_GAP, lblCategoryBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblDishBackGround.setBounds(CustOpts.HOR_GAP, lblCategoryBackGround.getY() + lblCategoryBackGround.getHeight() + CustOpts.VER_GAP,
        		lblDishBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbDish.setBounds(lblDishBackGround.getX() + lblDishBackGround.getWidth() + CustOpts.HOR_GAP, lblDishBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblFunctionBackGround.setBounds(CustOpts.HOR_GAP, lblDishBackGround.getY() + lblDishBackGround.getHeight() + CustOpts.VER_GAP,
        		lblFunctionBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbFunctionBtn.setBounds(lblFunctionBackGround.getX() + lblFunctionBackGround.getWidth() + CustOpts.HOR_GAP, lblFunctionBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblTableBackGround.setBounds(CustOpts.HOR_GAP, lblFunctionBackGround.getY() + lblFunctionBackGround.getHeight() + CustOpts.VER_GAP,
        		lblTableBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbTable.setBounds(lblTableBackGround.getX() + lblTableBackGround.getWidth() + CustOpts.HOR_GAP, lblTableBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblSelectedTableBackGround.setBounds(CustOpts.HOR_GAP, lblTableBackGround.getY() + lblTableBackGround.getHeight() + CustOpts.VER_GAP,
        		lblSelectedTableBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbTableSelected.setBounds(lblSelectedTableBackGround.getX() + lblSelectedTableBackGround.getWidth() + CustOpts.HOR_GAP, lblSelectedTableBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
        lblNumBtnBackGround.setBounds(CustOpts.HOR_GAP, lblSelectedTableBackGround.getY() + lblSelectedTableBackGround.getHeight() + CustOpts.VER_GAP,
        		lblNumBtnBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        ccbNumBtn.setBounds(lblNumBtnBackGround.getX() + lblNumBtnBackGround.getWidth() + CustOpts.HOR_GAP, lblNumBtnBackGround.getY(),
        		100, CustOpts.BTN_HEIGHT);
		lblArrowBtnBackGround.setBounds(CustOpts.HOR_GAP,
				lblNumBtnBackGround.getY() + lblNumBtnBackGround.getHeight() + CustOpts.VER_GAP,
				lblArrowBtnBackGround.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		ccbArrowBtn.setBounds(lblArrowBtnBackGround.getX() + lblArrowBtnBackGround.getWidth() + CustOpts.HOR_GAP,
				lblArrowBtnBackGround.getY(), 100, CustOpts.BTN_HEIGHT);

        //menu area----------
		if(BarFrame.instance != null && BarFrame.instance.menuPanel != null) {
			BarFrame.instance.menuPanel.reLayout();
		}
    }

    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            if ("System".equalsIgnoreCase(LoginDlg.USERNAME)) {
                BarFrame.setStatusMes(BarFrame.consts.ADMIN_MODE());
                // @TODO: might need to do some modification on the interface.
                revalidate();
                return true;
            }
        }
        return false;
    }

    private void openMoneyBox() {
        int[] ccs = new int[5];
        ccs[0] = 27;
        ccs[1] = 112;
        ccs[2] = 0;
        ccs[3] = 80;
        ccs[4] = 250;

        CommPortIdentifier tPortIdty;
        try {
            Enumeration tPorts = CommPortIdentifier.getPortIdentifiers();
            if (tPorts == null)
                JOptionPane.showMessageDialog(this, "no comm ports found!");
            else
                while (tPorts.hasMoreElements()) {
                    tPortIdty = (CommPortIdentifier) tPorts.nextElement();
                    if (tPortIdty.getName().equals("LPT1")) {
                        if (!tPortIdty.isCurrentlyOwned()) {
                            ParallelPort tParallelPort = (ParallelPort) tPortIdty.open("ParallelBlackBox", 2000);
                            DataOutputStream tOutStream = new DataOutputStream(tParallelPort.getOutputStream());
                            for (int i = 0; i < 5; i++)
                                tOutStream.write(ccs[i]);
                            tOutStream.flush();
                            tOutStream.close();
                            tParallelPort.close();
                        }
                    }
                }
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    private void initComponent() {
    	removeAll();
        lblBillPageRow = new JLabel(BarFrame.consts.BillPageRow());
        tfdBillPageRow = new JTextField(String.valueOf(BarOption.getBillPageRow()));
        lblBillPageCol = new JLabel(BarFrame.consts.BillPageCol());
        tfdBillPageCol = new JTextField(String.valueOf(BarOption.getBillPageCol()));
        lblCategoryRow = new JLabel(BarFrame.consts.CategoryRow());
        tfdCategoryRow = new JTextField(String.valueOf(BarOption.getCategoryRow()));
        lblCategoryCol = new JLabel(BarFrame.consts.CategoryCol());
        tfdCategoryCol = new JTextField(String.valueOf(BarOption.getCategoryCol()));
        lblDishRow = new JLabel(BarFrame.consts.DishRow());
        tfdDishRow = new JTextField(String.valueOf(BarOption.getDishRow()));
        lblDishCol = new JLabel(BarFrame.consts.DishCol());
        tfdDishCol = new JTextField(String.valueOf(BarOption.getDishCol()));
        
        cbxIsSingleUser = new JCheckBox(BarFrame.consts.IsSingleUser());
        cbxIsDiscBeforeTax = new JCheckBox(BarFrame.consts.IsDiscBeforeTax());
        cbxIsBuffetMode = new JCheckBox(BarFrame.consts.IsBuffetMode());
//        cbxIsPrintBillWhenPay = new JCheckBox(BarFrame.consts.IsPrintBillWhenPay);
        lblStartTimeOfDay = new JLabel(BarFrame.consts.StartTimeOfDay());
        tfdStartTimeOfDay = new JTextField(String.valueOf(BarOption.getStartTime()));
        lblPrinterMinReachTime = new JLabel(BarFrame.consts.PrinterMinReachTime());
        tfdPrinterMinReachTime = new JTextField(String.valueOf(BarOption.getPrinterMinReachTime()));
        
        lblLoginBackGround = new JLabel(BarFrame.consts.LoginBK());
        ccbLogin = new ColorChooserButton(BarOption.getBK("Login"));
        lblTablePanelBackGround = new JLabel(BarFrame.consts.TablePanelBK());
        ccbTablePanel = new ColorChooserButton(BarOption.getBK("TablePanel"));
        lblBillBackGround = new JLabel(BarFrame.consts.BillPanelBK());
        ccbBill = new ColorChooserButton(BarOption.getBK("Bill"));
        lblSalesBackGround = new JLabel(BarFrame.consts.SalesPanelBK());
        ccbSales = new ColorChooserButton(BarOption.getBK("Sales"));
        lblSettingsBackGround = new JLabel(BarFrame.consts.SettingsPanelBK());
        ccbSetting = new ColorChooserButton(BarOption.getBK("Setting"));
        lblCategoryBackGround = new JLabel(BarFrame.consts.CategoryBtnBK());
        ccbCategory = new ColorChooserButton(BarOption.getBK("Category"));
        lblDishBackGround = new JLabel(BarFrame.consts.DishBtnBK());
        ccbDish = new ColorChooserButton(BarOption.getBK("Dish"));
        lblFunctionBackGround = new JLabel(BarFrame.consts.FunctionBtnBK());
        ccbFunctionBtn = new ColorChooserButton(BarOption.getBK("Function"));
        lblTableBackGround = new JLabel(BarFrame.consts.TableBtnBK());
        ccbTable = new ColorChooserButton(BarOption.getBK("Table"));
        lblSelectedTableBackGround = new JLabel(BarFrame.consts.SelectedTableBtnBK());
        ccbTableSelected = new ColorChooserButton(BarOption.getBK("TableSelected"));
        lblNumBtnBackGround = new JLabel(BarFrame.consts.NumberBtnBk());
        ccbNumBtn = new ColorChooserButton(BarOption.getBK("NumBtn"));
		lblArrowBtnBackGround = new JLabel(BarFrame.consts.ArrowBtnBk());
		ccbArrowBtn = new ColorChooserButton(BarOption.getBK("Arrow"));

        btnLine_2_1 = new FunctionButton(BarFrame.consts.RETURN());
        btnLine_2_2 = new FunctionButton(BarFrame.consts.TABLE());
        btnLine_2_3 = new FunctionButton(BarFrame.consts.PRINTER());
        btnLine_2_4 = new FunctionButton(BarFrame.consts.CheckInOut());
       
        btnLine_2_5 = new FunctionButton(BarFrame.consts.Operator());
        btnLine_2_6 = new FunctionButton(BarFrame.consts.Modify());
        btnLine_2_7 = new FunctionButton(BarFrame.consts.BillInfo());//BarFrame.consts.Color());
        btnLine_2_8 = new FunctionButton("");
        btnLine_2_9 = new FunctionButton("");

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
//        cbxIsPrintBillWhenPay.setSelected(BarOption.isPrintBillWhenPay());
        // built

        add(lblBillPageRow);
        add(tfdBillPageRow);
        add(lblBillPageCol);
        add(tfdBillPageCol);
        add(lblCategoryRow);
        add(tfdCategoryRow);
        add(lblCategoryCol);
        add(tfdCategoryCol);
        add(lblDishRow);
        add(tfdDishRow);
        add(lblDishCol);
        add(tfdDishCol);
        
        add(cbxIsSingleUser);
        add(cbxIsDiscBeforeTax);
        add(cbxIsBuffetMode);
//        add(cbxIsPrintBillWhenPay);
        add(lblStartTimeOfDay);
        add(tfdStartTimeOfDay);
        add(lblPrinterMinReachTime);
        add(tfdPrinterMinReachTime);
        
        add(lblLoginBackGround);
        add(ccbLogin);
        add(lblLoginBackGround);
        add(ccbLogin);
        add(lblTablePanelBackGround);
        add(ccbTablePanel);
        add(lblBillBackGround);
        add(ccbBill);
        add(lblSalesBackGround);
        add(ccbSales);
        add(lblSettingsBackGround);
        add(ccbSetting);
        add(lblCategoryBackGround);
        add(ccbCategory);
        add(lblDishBackGround);
        add(ccbDish);
        add(lblFunctionBackGround);
        add(ccbFunctionBtn);
        add(lblTableBackGround);
        add(ccbTable);
        add(lblSelectedTableBackGround);
        add(ccbTableSelected);
        add(lblNumBtnBackGround);
        add(ccbNumBtn);
		add(lblArrowBtnBackGround);
		add(ccbArrowBtn);
        
        add(btnLine_2_9);
        add(btnLine_2_2);
        add(btnLine_2_3);
        add(btnLine_2_4);
        add(btnLine_2_5);
        add(btnLine_2_6);
        add(btnLine_2_7);
        add(btnLine_2_8);
        add(btnLine_2_1);

        // add listener
        addComponentListener(this);

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
        tfdCategoryRow.addFocusListener(this);
        tfdCategoryCol.addFocusListener(this);
        tfdDishRow.addFocusListener(this);
        tfdDishCol.addFocusListener(this);
        cbxIsSingleUser.addActionListener(this);
        cbxIsDiscBeforeTax.addActionListener(this);
        cbxIsBuffetMode.addActionListener(this);
//        cbxIsPrintBillWhenPay.addActionListener(this);
        tfdStartTimeOfDay.addFocusListener(this);
        tfdPrinterMinReachTime.addFocusListener(this);
        
        ccbLogin.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Login");
            }
        });
        ccbTablePanel.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"TablePanel");
                 ((TablesPanel)BarFrame.instance.panels[0]).initComponent();
            }
        });
        ccbBill.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Bill");
                 ((BillListPanel)BarFrame.instance.panels[1]).initComponent();
            }
        });
        ccbSales.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Sales");
                 ((SalesPanel)BarFrame.instance.panels[2]).initComponent();
            }
        });
        ccbSetting.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Setting");
                 initComponent();
                 add(BarFrame.menuPanel);
            }
        });
        ccbCategory.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Category");
                 BarFrame.menuPanel.initComponent();
            }
        });
        ccbDish.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Dish");
                 BarFrame.menuPanel.initComponent();
            }
        });
        ccbFunctionBtn.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Function");
                 ((TablesPanel)BarFrame.instance.panels[0]).initComponent();
                 ((BillListPanel)BarFrame.instance.panels[1]).initComponent();
                 ((SalesPanel)BarFrame.instance.panels[2]).initComponent();
                 initComponent();
                 add(BarFrame.menuPanel);
            }
        });
        ccbTable.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"Table");
                 ((TablesPanel)BarFrame.instance.panels[0]).initComponent();
            }
        });
        ccbTableSelected.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"TableSelected");
                 ((TablesPanel)BarFrame.instance.panels[0]).initComponent();
            }
        });
        ccbNumBtn.addColorChangedListener(new ColorChangedListener() {
            @Override
            public void colorChanged(Color newColor) {
                 BarOption.setBK(newColor,"NumBtn");
                 BarFrame.numberPanelDlg.initComponent();
                 BarFrame.payCashDlg.initComponent();
            }
        });
		ccbArrowBtn.addColorChangedListener(new ColorChangedListener() {
			@Override
			public void colorChanged(Color newColor) {
				BarOption.setBK(newColor, "Arrow");
			}
		});
		
		reLayout();
    }

    JLabel lblBillPageRow;
    JTextField tfdBillPageRow;
    JLabel lblBillPageCol;
    JTextField tfdBillPageCol;
    JLabel lblCategoryRow;
    JTextField tfdCategoryRow;
    JLabel lblCategoryCol;
    JTextField tfdCategoryCol;
    JLabel lblDishRow;
    JTextField tfdDishRow;
    JLabel lblDishCol;
    JTextField tfdDishCol;
    
    JLabel lblStartTimeOfDay;
    JTextField tfdStartTimeOfDay;
    JLabel lblPrinterMinReachTime;
    JTextField tfdPrinterMinReachTime;
    
    JCheckBox cbxIsSingleUser;
    JCheckBox cbxIsDiscBeforeTax;
    JCheckBox cbxIsBuffetMode;
//    JCheckBox cbxIsPrintBillWhenPay;
    JLabel lblLoginBackGround;
    ColorChooserButton ccbLogin;
    JLabel lblTablePanelBackGround;
    ColorChooserButton ccbTablePanel;
    JLabel lblBillBackGround;
    ColorChooserButton ccbBill;
    JLabel lblSalesBackGround;
    ColorChooserButton ccbSales;
    JLabel lblSettingsBackGround;
    ColorChooserButton ccbSetting;
    JLabel lblCategoryBackGround;
    ColorChooserButton ccbCategory;
    JLabel lblDishBackGround;
    ColorChooserButton ccbDish;
    JLabel lblFunctionBackGround;
    ColorChooserButton ccbFunctionBtn;
    JLabel lblTableBackGround;
    ColorChooserButton ccbTable;
    JLabel lblSelectedTableBackGround;
    ColorChooserButton ccbTableSelected;
    JLabel lblNumBtnBackGround;
    ColorChooserButton ccbNumBtn;
	JLabel lblArrowBtnBackGround;
	ColorChooserButton ccbArrowBtn;
    
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
