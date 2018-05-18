package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.bar.beans.ArrayButton;
import org.cas.client.platform.bar.beans.CategoryToggleButton;
import org.cas.client.platform.bar.beans.MenuButton;
import org.cas.client.platform.bar.dialog.statistics.CheckInOutListDlg;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.model.Mark;
import org.cas.client.platform.bar.model.Printer;
import org.cas.client.platform.bar.model.User;
import org.cas.client.platform.bar.model.Category;
import org.cas.client.platform.bar.print.Command;
import org.cas.client.platform.bar.print.WifiPrintService;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.contact.dialog.selectcontacts.SelectedNewMemberDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pimview.pimtable.PIMTableRenderAgent;
import org.cas.client.platform.pos.dialog.statistics.Statistic;
import org.cas.client.platform.refund.dialog.RefundDlg;
import org.cas.client.resource.international.DlgConst;
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
    			JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.InvalidInput);
				return;
    		}
    		if(num < 1 || num > 2) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.InvalidInput);
				return;
    		}
    		BarOption.setBillPageRow(tfdBillPageRow.getText());
    	}else if(o == tfdBillPageCol) {
    		int num;
    		try {
    			num = Integer.valueOf(tfdBillPageCol.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.InvalidInput);
				return;
    		}
    		if(num < 1 || num > 16) {
    			JOptionPane.showMessageDialog(BarFrame.instance, BarDlgConst.InvalidInput);
				return;
    		}
    		BarOption.setBillPageCol(tfdBillPageCol.getText());
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
    			JOptionPane.showMessageDialog(this, BarDlgConst.InvalidInput);
    			tfdStartTimeOfDay.grabFocus();
    		}
    	}
    }

    // ActionListner-------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof JButton) {
        	if(o == btnLine_2_1) {
        		LoginDlg.reset();
        		BarFrame.instance.switchMode(0);
        	}else if(o == btnLine_2_2) {
        		new TabbleSettingDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_3) {
        		new SettingPrinterDlg(BarFrame.instance).setVisible(true);
        	}else if(o == btnLine_2_4) {
        		new CheckInOutListDlg(BarFrame.instance).setVisible(true);
        	} 
        }
        //JToggleButton-------------------------------------------------------------------------------------
        else if(o instanceof JToggleButton) {
        	if(o == cbxIsSingleUser) {
        		BarOption.setSingleUser(cbxIsSingleUser.isSelected() ? "true" : "false");
        	}else if(o == cbxIsDiscBeforeTax) {
        		BarOption.setIsDisCountBeforeTax(cbxIsDiscBeforeTax.isSelected() ? true : false);
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
        
        cbxIsSingleUser.setBounds(CustOpts.HOR_GAP, lblBillPageRow.getY() + lblBillPageRow.getHeight() + CustOpts.VER_GAP,
        		cbxIsSingleUser.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxIsDiscBeforeTax.setBounds(CustOpts.HOR_GAP, cbxIsSingleUser.getY() + cbxIsSingleUser.getHeight() + CustOpts.VER_GAP,
        		cbxIsDiscBeforeTax.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        lblStartTimeOfDay.setBounds(CustOpts.HOR_GAP, cbxIsDiscBeforeTax.getY() + cbxIsDiscBeforeTax.getHeight() + CustOpts.VER_GAP,
        		lblStartTimeOfDay.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdStartTimeOfDay.setBounds(lblStartTimeOfDay.getX() + lblStartTimeOfDay.getWidth() + CustOpts.HOR_GAP, lblStartTimeOfDay.getY(),
        		100, CustOpts.BTN_HEIGHT);
        //menu area----------
        BarFrame.instance.menuPanel.reLayout();
    }

    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            if ("System".equalsIgnoreCase(LoginDlg.USERNAME)) {
                BarFrame.setStatusMes(BarDlgConst.ADMIN_MODE);
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
        lblBillPageRow = new JLabel(BarDlgConst.BillPageRow);
        tfdBillPageRow = new JTextField(String.valueOf(BarOption.getBillPageRow()));
        lblBillPageCol = new JLabel(BarDlgConst.BillPageCol);
        tfdBillPageCol = new JTextField(String.valueOf(BarOption.getBillPageCol()));
        cbxIsSingleUser = new JCheckBox(BarDlgConst.IsSingleUser);
        cbxIsDiscBeforeTax = new JCheckBox(BarDlgConst.IsDiscBeforeTax);
        lblStartTimeOfDay = new JLabel(BarDlgConst.StartTimeOfDay);
        tfdStartTimeOfDay = new JTextField(String.valueOf(BarOption.getStartTime()));
        
        btnLine_2_1 = new JButton(BarDlgConst.RETURN);
        btnLine_2_2 = new JButton(BarDlgConst.TABLE);
        btnLine_2_3 = new JButton(BarDlgConst.PRINTER);
        btnLine_2_4 = new JButton(BarDlgConst.CheckInOut);
       
        btnLine_2_5 = new JButton("");
        btnLine_2_6 = new JButton("");
        btnLine_2_7 = new JButton("");
        btnLine_2_8 = new JButton("");
        btnLine_2_9 = new JButton("");

        // properties
        setLayout(null);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);
        
        cbxIsSingleUser.setSelected(BarOption.isSingleUser());
        cbxIsDiscBeforeTax.setSelected(BarOption.isDisCountBeforeTax());
        // built

        add(lblBillPageRow);
        add(tfdBillPageRow);
        add(lblBillPageCol);
        add(tfdBillPageCol);
        add(cbxIsSingleUser);
        add(cbxIsDiscBeforeTax);
        add(lblStartTimeOfDay);
        add(tfdStartTimeOfDay);
        
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
        cbxIsSingleUser.addActionListener(this);
        cbxIsDiscBeforeTax.addActionListener(this);
        tfdStartTimeOfDay.addFocusListener(this);
    }

    JLabel lblBillPageRow;
    JTextField tfdBillPageRow;
    JLabel lblBillPageCol;
    JTextField tfdBillPageCol;
    JLabel lblStartTimeOfDay;
    JTextField tfdStartTimeOfDay;
    JCheckBox cbxIsSingleUser;
    JCheckBox cbxIsDiscBeforeTax;
    
    
    private JButton btnLine_2_1;
    private JButton btnLine_2_2;
    private JButton btnLine_2_3;
    private JButton btnLine_2_4;
    private JButton btnLine_2_5;
    private JButton btnLine_2_6;
    private JButton btnLine_2_7;
    private JButton btnLine_2_8;
    private JButton btnLine_2_9;
}
