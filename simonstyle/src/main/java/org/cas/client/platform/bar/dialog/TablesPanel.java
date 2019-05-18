package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Component;
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.uibeans.MoreButton;
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
        	if(tableToggle.getBackground() != TableButton.colorSelected){
        		tableToggle.open();
			}
        	//update ui's time field.
        	BarFrame.instance.valStartTime.setText(tableToggle.getOpenTime());
        	
			try {
				StringBuilder sql = new StringBuilder("select * from bill where tableId = '").append(tableToggle.getText()).append("'")
						.append(" and opentime = '").append(tableToggle.getOpenTime()).append("'")
						.append(" and (status is null or status < ").append(DBConsts.completed)
						.append(" and status >= ").append(DBConsts.original).append(")");
				ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
				rs.afterLast();
				rs.relative(-1);
				int num = rs.getRow();
				
				//if it's table mode, will not be empty anymore, because whenever click a table, we will create a bill for it.
				if (num == 0) {// if it's empty, means it's fastfood mode, and the previous bill was deleted (should not happen though). switch to sales panel
					L.w("Warning:", "when open table, found no related bill in a open status table", null);
					tableToggle.open();
					BarFrame.instance.setCurBillIdx("");
					BarFrame.instance.switchMode(2);
				} else { // if it's not empty
					if(num == 1 && CustOpts.custOps.getValue("FrobiddenQuickEnter") == null) {	// display the only bill
						BarFrame.instance.setCurBillIdx(rs.getString("billIndex"));
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
//		else if(o instanceof JToggleButton) {
//        	if(o == btnChangeMode) {
//        		BarOption.setFastFoodMode(btnChangeMode.isSelected());
//        	}
//        }
    }

    void reLayout() {
    	BarUtil.layoutCommandButtons(BarFrame.instance.groupedButtons[0]);
    }
    
    void initComponent() {
    	removeAll();
//        btnChangeMode = new JToggleButton(BarFrame.consts.ChangeMode());

    	// border----------
        Color bg = BarOption.getBK("TablePanel");
    	if(bg == null) {
    		bg = new Color(216,216,216);
    	}
        setBackground(bg);
        setLayout(null);

        // command buttons
        BarUtil.addFunctionButtons(this, BarFrame.instance.groupedButtons[0]);

        // add listener
        addComponentListener(this);

//        btnChangeMode.addActionListener(this);
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
            ResultSet rs = smt.executeQuery("select * from dining_Table order by Name");
            rs.beforeFirst();

            while (rs.next()) {
            	TableButton tableToggleButton = new TableButton();
            	
            	tableToggleButton.setId(rs.getInt("ID"));
            	tableToggleButton.setText(rs.getString("Name"));
            	tableToggleButton.setBounds(rs.getInt("posX"), rs.getInt("posY"), rs.getInt("width"), rs.getInt("height"));
            	tableToggleButton.setOpenTime(rs.getString("openTime"));
            	if(rs.getInt("status") > 0)
            		tableToggleButton.setBackground(TableButton.colorSelected);
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
