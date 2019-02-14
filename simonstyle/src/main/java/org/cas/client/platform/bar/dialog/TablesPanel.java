package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.bar.dialog.statistics.CheckBillDlg;
import org.cas.client.platform.bar.dialog.statistics.CheckInOutListDlg;
import org.cas.client.platform.bar.dialog.statistics.ReportDlg;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.bar.uibeans.TableButton;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

//Identity表应该和Employ表合并。
public class TablesPanel extends JPanel implements ComponentListener, ActionListener, FocusListener, MouseListener {

    static ArrayList<TableButton> btnTables = new ArrayList<TableButton>();
    
    private TableButton btnPressed;
    private boolean isDragged;
    private int xGap, yGap;
    
    static Color colorSelected = new Color(123, 213, 132);
    static Color colorDefault = new Color(255, 255, 255);
            
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
    public void actionPerformed(ActionEvent e) {
        JComponent o = (JComponent)e.getSource();
        // category buttons---------------------------------------------------------------------------------
        if (o instanceof TableButton) {
        	if(isDragged) {
        		return;	//do nothing.
        	}
            TableButton tableToggle = (TableButton) o;
            BarFrame.instance.ignoreItemChange = true;
        	BarFrame.instance.cmbCurTable.setSelectedItem(tableToggle.getText());
        	
			if(!BarOption.isSingleUser()) {	//if it's multi user, then login every time open table.
				new LoginDlg(null).setVisible(true);
	            if (LoginDlg.PASSED == true) {
	            	BarFrame.checkSignIn();
	            	//@note: lowdown a little the level, to enable the admin do sales work.
	            	if ("admin".equalsIgnoreCase(LoginDlg.USERNAME))
	            		 LoginDlg.USERTYPE = LoginDlg.USER_STATUS;
	            }else {
	            	return;
	            }
			}
			//if before is not selected, then update the status
        	if(tableToggle.getBackground() != colorSelected){
        		openATable(tableToggle);
			}
        	//update ui's time field.
        	BarFrame.instance.valStartTime.setText(tableToggle.getOpenTime());
			try {
	        	//including the output of closed bill.
				StringBuilder sql = new StringBuilder("SELECT DISTINCT contactID from output where SUBJECT = '").append(tableToggle.getText())
						.append("' and (deleted is null or deleted = ").append(DBConsts.original)
						.append(") and time = '").append(tableToggle.getOpenTime())
						.append("' order by contactID");
				ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
				rs.afterLast();
				rs.relative(-1);
				int num = rs.getRow();
				
				if (num == 0) {// if it's empty, switch to sales panel
					BarFrame.instance.valCurBillIdx.setText("");
					BarFrame.instance.switchMode(2);
				} else { // if it's not empty
					if(num == 1 && CustOpts.custOps.getValue("FrobiddenQuickEnter") == null) {	// display the only bill
						BarFrame.instance.valCurBillIdx.setText(rs.getString("contactID"));
						BarFrame.instance.switchMode(2);
					}else { //or switch to the bill panel to show all the bills.
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
        	if(o == btnAddTable) {		//add table
        		if(!BarOption.isSingleUser()) {
        			new LoginDlg(null).setVisible(true);
    	            if (LoginDlg.PASSED == true) {
    	            	BarFrame.checkSignIn();
    	            	//@note: lowdown a little the level, to enable the admin do sales work.
    	            	if ("admin".equalsIgnoreCase(LoginDlg.USERNAME))
    	            		 LoginDlg.USERTYPE = LoginDlg.USER_STATUS;
    	            }else {
    	            	return;
    	            }
        		}
    			new ModifyTableDlg(null, null).setVisible(true);
    			initContent();
        	}else if(o == btnOrderManage) {	//bill management
        		String endNow = BarOption.df.format(new Date());
        		int p = endNow.indexOf(" ");
        		String startTime = endNow.substring(0, p + 1) + BarOption.getStartTime();
        		CheckBillDlg dlg = new CheckBillDlg(BarFrame.instance);
        		dlg.initContent(startTime, endNow);
        		dlg.setVisible(true);
        	} else if (o == btnOpenDrawer) {	//open drawer.
        		PrintService.openDrawer();
            //} else if (o == btnWaiterReport) {
            } else if (o == btnSetting) {
                BarFrame.instance.switchMode(3);
            }else if (o == btnReport) {
	    		ReportDlg dlg = new ReportDlg(BarFrame.instance);
	    		dlg.setVisible(true);
            }else if (o == btnCheckInOut) {
            	if(BarOption.isSingleUser()) {
            		CheckInOutListDlg.updateCheckInRecord();
    				BarFrame.instance.setVisible(false);
    				BarFrame.singleUserLoginProcess();
    			}else {
    				new LoginDlg(null).setVisible(true);
	                if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
	                	BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
	                    //insert a record of start to work.
	                	CheckInOutListDlg.updateCheckInRecord();
	                }
    			}
            }
        }
//		else if(o instanceof JToggleButton) {
//        	if(o == btnChangeMode) {
//        		BarOption.setFastFoodMode(btnChangeMode.isSelected());
//        	}
//        }
    }

	private void openATable(TableButton tableToggle) {
		tableToggle.setBackground(colorSelected);
		String openTime = BarOption.df.format(new Date());
		tableToggle.setOpenTime(openTime);
		
		try {
			Statement smt = PIMDBModel.getStatement();
			smt.executeUpdate("update dining_Table set status = 1, opentime = '"
			+ openTime + "' WHERE name = '" + tableToggle.getText() + "'");
			
			//create a bill for it. in case there will be something like order fee in future.
			String createtime = BarOption.df.format(new Date());
			StringBuilder sql = new StringBuilder(
		            "INSERT INTO bill(createtime, tableID, BillIndex, total, discount, tip, otherreceived, cashback, EMPLOYEEID, Comment, opentime) VALUES ('")
					.append(createtime).append("', '")
		            .append(tableToggle.getText()).append("', '")	//table
		            .append("1").append("', ")			//bill
		            .append(0).append(", ")	//total
		            .append(0).append(", ")
		            .append(0).append(", ")
		            .append(0).append(", ")
		            .append(0).append(", ")	//discount
		            .append(LoginDlg.USERID).append(", '")		//emoployid
		            .append("").append("', '")
		            .append(tableToggle.getOpenTime()).append("')");				//content
			smt.executeUpdate(sql.toString());
		}catch(Exception exp) {
			ErrorUtil.write(exp);
		}
	}

    void reLayout() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 7) / 6;
        int tBtnHeight = panelHeight / 10;

        // command buttons--------------
        // line 2
//        btnChangeMode.setBounds(CustOpts.HOR_GAP, panelHeight - tBtnHeight - CustOpts.VER_GAP, tBtnWidht,
//                tBtnHeight);
        btnAddTable.setBounds(CustOpts.HOR_GAP, panelHeight - tBtnHeight - CustOpts.VER_GAP, tBtnWidht,
                tBtnHeight);
        btnOrderManage.setBounds(btnAddTable.getX() + tBtnWidht + CustOpts.HOR_GAP, btnAddTable.getY(), tBtnWidht,
                tBtnHeight);
        btnOpenDrawer.setBounds(btnOrderManage.getX() + tBtnWidht + CustOpts.HOR_GAP, btnOrderManage.getY(), tBtnWidht,
                tBtnHeight);
        //btnWaiterReport.setBounds(btnOpenDrawer.getX() + tBtnWidht + CustOpts.HOR_GAP, btnChangeMode.getY(), tBtnWidht,
        //        tBtnHeight);
        btnSetting.setBounds(btnOpenDrawer.getX() + tBtnWidht + CustOpts.HOR_GAP, btnOpenDrawer.getY(), tBtnWidht,
                tBtnHeight);
        btnReport.setBounds(btnSetting.getX() + tBtnWidht + CustOpts.HOR_GAP, btnSetting.getY(), tBtnWidht,
                tBtnHeight);
        btnCheckInOut.setBounds(btnReport.getX() + tBtnWidht + CustOpts.HOR_GAP, btnReport.getY(), tBtnWidht,
                tBtnHeight);
    }
    
    void initComponent() {
    	removeAll();
//        btnChangeMode = new JToggleButton(BarFrame.consts.ChangeMode());
		btnAddTable = new FunctionButton(BarFrame.consts.AddTable());
		btnOrderManage = new FunctionButton(BarFrame.consts.OrderManage());
		btnOpenDrawer = new FunctionButton(BarFrame.consts.OpenDrawer());
		//btnWaiterReport = new FunctionButton(BarFrame.consts.WaiterReport());
		btnSetting = new FunctionButton(BarFrame.consts.SETTINGS());
		btnReport = new FunctionButton(BarFrame.consts.Report());
		btnCheckInOut = new FunctionButton(BarFrame.consts.CheckOut());

        // border----------
        Color bg = BarOption.getBK("TablePanel");
    	if(bg == null) {
    		bg = new Color(216,216,216);
    	}
        setBackground(bg);
        setLayout(null);

        // built
//        add(btnChangeMode);
        add(btnAddTable);
        add(btnOrderManage);
        add(btnOpenDrawer);
        //add(btnWaiterReport);
        add(btnSetting);
        add(btnReport);
        add(btnCheckInOut);

        // add listener
        addComponentListener(this);

//        btnChangeMode.addActionListener(this);
        btnAddTable.addActionListener(this);
        btnOrderManage.addActionListener(this);
        btnOpenDrawer.addActionListener(this);
        //btnWaiterReport.addActionListener(this);
        btnSetting.addActionListener(this);
        btnReport.addActionListener(this);
        btnCheckInOut.addActionListener(this);
        
//        btnChangeMode.setSelected(BarOption.isFastFoodMode());

		reLayout();
    }

    // menu and category buttons must be init after initContent---------
	public void initContent() {
		//clean existing btns
		for (int i = btnTables.size() - 1; i >=0; i--) {
			TableButton tableToggleButton = btnTables.get(i);
			btnTables.remove(i);
			remove(tableToggleButton);
		}
		//btnTables.clear();
		//renite buttons.
		try {
            Statement smt = PIMDBModel.getReadOnlyStatement();

            // load all the categorys---------------------------
            ResultSet rs = smt.executeQuery("select * from dining_Table order by DSP_INDEX");
            rs.beforeFirst();

            while (rs.next()) {
            	TableButton tableToggleButton = new TableButton();
            	
            	tableToggleButton.setId(rs.getInt("ID"));
            	tableToggleButton.setText(rs.getString("Name"));
            	tableToggleButton.setBounds(rs.getInt("posX"), rs.getInt("posY"), rs.getInt("width"), rs.getInt("height"));
            	tableToggleButton.setOpenTime(rs.getString("openTime"));
            	if(rs.getInt("status") > 0)
            		tableToggleButton.setBackground(colorSelected);
            	tableToggleButton.setMargin(new Insets(4, 4, 4, 4));
    			tableToggleButton.addActionListener(this);
    			int type = rs.getInt("type");
    			if(type >= 100) {
    				type -= 100;
    				tableToggleButton.addMouseListener(this);
    				tableToggleButton.addMouseMotionListener(new MouseMotionListener(){
        	        	@Override
    					public void mouseDragged(MouseEvent e) {
        	        		if(btnPressed != null) {
        	        			btnPressed.setLocation(btnPressed.getX() + e.getX() - xGap, btnPressed.getY() + e.getY() - yGap);
        	        			isDragged = true;
        	        		}
        	        	}
        	        	@Override
    					public void mouseMoved(MouseEvent e) {}
        	        });
    			}
            	tableToggleButton.setType(type);		//it's rectanglee or round?
    			add(tableToggleButton);
            	btnTables.add(tableToggleButton);
            }
            //update the model of the table combobox on BarFrame.
            initCmbCurTable();
            
            rs.close();// 关闭
            smt.close();
		}catch(Exception e) {
			L.e("init tables", "Unexpected exception when init the tables from db.", e);
		}
		invalidate();
		revalidate();
		validate();
		repaint();
	}

	private void initCmbCurTable() {
		String[] tableNames = new String[btnTables.size() + 1];
		tableNames[0] = "";
		for (int i = 0; i < btnTables.size(); i++) {
			tableNames[i + 1] = btnTables.get(i).getText();
		}
		BarFrame.instance.cmbCurTable.setModel(new DefaultComboBoxModel<String>(tableNames));
	}
	
    //private JToggleButton btnChangeMode;
	private FunctionButton btnAddTable;
	private FunctionButton btnOrderManage;
	private FunctionButton btnOpenDrawer;
	//private FunctionButton btnWaiterReport;
	private FunctionButton btnSetting;
	private FunctionButton btnReport;
	private FunctionButton btnCheckInOut;


	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		Object o = e.getSource();
		if( o instanceof TableButton) {
			btnPressed = (TableButton)o;
			xGap = e.getX();
			yGap = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Object o = e.getSource();
		if(o instanceof TableButton) {
			if(btnPressed != null && isDragged) {
				TableButton btn = (TableButton)o;
				//updateDB;
				String sql = "Update DINING_TABLE set posX = " + (btn.getX() + e.getX() - xGap) + ", posY = " + (btn.getY() + e.getY()-yGap) + " where id = " + btn.getId();
	        	try {
	        		PIMDBModel.getStatement().execute(sql);
	                initContent();
	        	}catch(Exception exp) {
	        		ErrorUtil.write(exp);
	        	}
				reLayout();
			}
		}

		isDragged = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
