package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.beans.FunctionButton;
import org.cas.client.platform.bar.beans.TableButton;
import org.cas.client.platform.bar.dialog.statistics.CheckInOutListDlg;
import org.cas.client.platform.bar.dialog.statistics.SaleListDlg;
import org.cas.client.platform.bar.model.User;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

//Identity表应该和Employ表合并。
public class TablesPanel extends JPanel implements ComponentListener, ActionListener, FocusListener {

    static ArrayList<TableButton> btnTables = new ArrayList<TableButton>();
    
    private final int USER_STATUS = 1;
    private final int ADMIN_STATUS = 2;
    static Color colorSelected = new Color(123, 213, 132);
    static Color colorDefault = new Color(255, 255, 255);
    
    private int curSecurityStatus = USER_STATUS;
    
    User curUser;
    
    Integer tableColumn = (Integer) CustOpts.custOps.hash2.get("tableColumn");
    Integer tableRow = (Integer) CustOpts.custOps.hash2.get("tableRow");

    public static String startTime;
   
    //for print
    public static String SUCCESS = "0";
    public static String ERROR = "2";
    
    
    public TablesPanel() {
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
    public void focusGained(FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {}

    // ActionListner-------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        JComponent o = (JComponent)e.getSource();
        // category buttons---------------------------------------------------------------------------------
        if (o instanceof TableButton) {
            TableButton tableToggle = (TableButton) o;
        	BarFrame.instance.valCurTable.setText(tableToggle.getText());
        	
			if(!BarOption.isSingleUser()) {	//if it's multi user, then login every time open table.
				new LoginDlg(null).setVisible(true);
	            if (LoginDlg.PASSED == true) {
	            	BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
	            	//@note: lowdown a little the level, to enable the admin do sales work.
	            	if ("admin".equalsIgnoreCase(LoginDlg.USERNAME))
	            		 LoginDlg.USERTYPE = LoginDlg.USER_STATUS;
	            }else {
	            	return;
	            }
			}

        	if(tableToggle.getBackground() != colorSelected){	//if before is not selected, then update the status
        		o.setBackground(colorSelected);
        		String openTime = BarOption.df.format(new Date());
        		tableToggle.setOpenTime(openTime);
        		
        		try {
        			Statement smt = PIMDBModel.getReadOnlyStatement();
        			smt.executeQuery("update dining_Table set status = 1, opentime = '"
        			+ openTime + "' WHERE name = '" + tableToggle.getText() + "'");
        		}catch(Exception exp) {
        			ErrorUtil.write(exp);
        		}
			}
        	
			try {
	        	BarFrame.instance.valStartTime.setText(tableToggle.getOpenTime());	//update ui's time field.
	        	
				Statement smt = PIMDBModel.getReadOnlyStatement();
				ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '"
						+ tableToggle.getText() + "' and deleted = false and time = '" + tableToggle.getOpenTime() + "' order by contactID");
				rs.afterLast();
				rs.relative(-1);
				int num = rs.getRow();

				if (num == 0) { // check if it's empty
					BarFrame.instance.valCurBill.setText("0");
					BarFrame.instance.switchMode(2);
				} else { // if it's not empty, display a dialog to show all the bills.
					if(num == 1 && CustOpts.custOps.getValue("FrobiddenQuickEnter") == null) {
						BarFrame.instance.valCurBill.setText(rs.getString("contactID"));
						BarFrame.instance.switchMode(2);
					}else {
						BarFrame.instance.switchMode(1);
					}
				}
				tableToggle.setSelected(true);
			} catch (Exception exp) {
				ErrorUtil.write(exp);
			}
		}
		// FunctionButton------------------------------------------------------------------------------------------------
		else if (o instanceof FunctionButton) {
        	if(o == btnLine_2_2) {		//add table
        		if(!BarOption.isSingleUser()) {
        			new LoginDlg(null).setVisible(true);
    	            if (LoginDlg.PASSED == true) {
    	            	BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
    	            	//@note: lowdown a little the level, to enable the admin do sales work.
    	            	if ("admin".equalsIgnoreCase(LoginDlg.USERNAME))
    	            		 LoginDlg.USERTYPE = LoginDlg.USER_STATUS;
    	            }else {
    	            	return;
    	            }
        		}
    			new ModifyTableDlg(null, null).setVisible(true);
    			initContent();
        	}else if(o == btnLine_2_3) {	//order management
        		String endNow = BarOption.df.format(new Date());
        		int p = endNow.indexOf(" ");
        		String startTime = endNow.substring(0, p + 1) + BarOption.getStartTime();
        		SaleListDlg dlg = new SaleListDlg(BarFrame.instance);
        		dlg.initContent(startTime, endNow);
        		dlg.setVisible(true);
        	} else if (o == btnLine_2_4) {	//open drawer.
        		BarUtil.openMoneyBox();
            } else if (o == btnLine_2_5) {
            } else if (o == btnLine_2_6) {
                BarFrame.instance.switchMode(3);
//            } else if (o == btnLine_2_7) {
            }else if (o == btnLine_2_8) {
            }else if (o == btnCheckInOut) {
            	if(BarOption.isSingleUser()) {
            		CheckInOutListDlg.updateCheckInRecord();
    				BarFrame.instance.setVisible(false);
    				BarFrame.singleUserLoginProcess();
    			}else {
    				new LoginDlg(null).setVisible(true);
	                if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
	                    //insert a record of start to work.
	                	CheckInOutListDlg.updateCheckInRecord();
	                }
    			}
            }
        }else if(o instanceof JToggleButton) {
        	if(o == btnLine_2_1) {
        		BarOption.setFastFoodMode(btnLine_2_1.isSelected());
        	}
        }
    }

    void reLayout() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 9) / 8;
        int tBtnHeight = panelHeight / 10;

        // command buttons--------------
        // line 2
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
        btnLine_2_8.setBounds(btnLine_2_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnCheckInOut.setBounds(btnLine_2_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
    }

    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            if ("System".equalsIgnoreCase(LoginDlg.USERNAME)) {
                curSecurityStatus++;
                BarFrame.setStatusMes(BarFrame.consts.ADMIN_MODE());
                // @TODO: might need to do some modification on the interface.
                revalidate();
                return true;
            }
        }
        return false;
    }
    
    void initComponent() {
    	removeAll();
        btnLine_2_1 = new JToggleButton(BarFrame.consts.ChangeMode());
		btnLine_2_2 = new FunctionButton(BarFrame.consts.AddTable());
		btnLine_2_3 = new FunctionButton(BarFrame.consts.OrderManage());
		btnLine_2_4 = new FunctionButton(BarFrame.consts.OpenDrawer());
		btnLine_2_5 = new FunctionButton(BarFrame.consts.WaiterReport());
		btnLine_2_6 = new FunctionButton(BarFrame.consts.SETTINGS());
		btnLine_2_8 = new FunctionButton(BarFrame.consts.Report());
		btnCheckInOut = new FunctionButton(BarFrame.consts.CheckInOut());

        // border----------
        setBackground(BarOption.getBK("TablePanel"));
        setLayout(null);

        // built
        add(btnLine_2_1);
        add(btnLine_2_2);
        add(btnLine_2_3);
        add(btnLine_2_4);
        add(btnLine_2_5);
        add(btnLine_2_6);
        add(btnLine_2_8);
        add(btnCheckInOut);

        // add listener
        addComponentListener(this);

        btnLine_2_1.addActionListener(this);
        btnLine_2_2.addActionListener(this);
        btnLine_2_3.addActionListener(this);
        btnLine_2_4.addActionListener(this);
        btnLine_2_5.addActionListener(this);
        btnLine_2_6.addActionListener(this);
        btnLine_2_8.addActionListener(this);
        btnCheckInOut.addActionListener(this);
        
        btnLine_2_1.setSelected(BarOption.isFastFoodMode());

		reLayout();
    }

    // menu and category buttons must be init after initContent---------
	void initContent() {
		//clean existing btns
		for (int i = btnTables.size() - 1; i >=0; i--) {
			TableButton tableToggleButton = btnTables.get(i);
			btnTables.remove(i);
			remove(tableToggleButton);
		}
		//renite buttons.
		try {
            Statement smt = PIMDBModel.getReadOnlyStatement();

            // load all the categorys---------------------------
            ResultSet rs = smt.executeQuery("select * from dining_Table order by DSP_INDEX");
            rs.beforeFirst();

            int tmpPos = 0;
            while (rs.next()) {
            	TableButton tableToggleButton = new TableButton();
            	
            	tableToggleButton.setId(tmpPos);
            	tableToggleButton.setText(rs.getString("Name"));
            	tableToggleButton.setBounds(rs.getInt("posX"), rs.getInt("posY"), rs.getInt("width"), rs.getInt("height"));
            	tableToggleButton.setType(rs.getInt("type"));		//it's rectanglee or round?
            	tableToggleButton.setOpenTime(rs.getString("openTime"));
            	if(rs.getInt("status") > 0)
            		tableToggleButton.setBackground(colorSelected);
            	tableToggleButton.setMargin(new Insets(0, 0, 0, 0));
    			tableToggleButton.addActionListener(this);
    			add(tableToggleButton);
            	btnTables.add(tableToggleButton);
                tmpPos++;
            }
            
            rs.close();// 关闭
            smt.close();
		}catch(Exception e) {
			ErrorUtil.write("Unexpected exception when init the tables from db." + e);
		}
		invalidate();
		revalidate();
		validate();
		repaint();
	}

    private JToggleButton btnLine_2_1;
	private FunctionButton btnLine_2_2;
	private FunctionButton btnLine_2_3;
	private FunctionButton btnLine_2_4;
	private FunctionButton btnLine_2_5;
	private FunctionButton btnLine_2_6;
	private FunctionButton btnLine_2_8;
	private FunctionButton btnCheckInOut;
}
