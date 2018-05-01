package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Container;
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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.cas.client.platform.bar.beans.TableButton;
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

    //flags
    NumberPanelDlg numberPanelDlg; 
    
    //for print
    public static String SUCCESS = "0";
    public static String ERROR = "2";
    
    
    public TablesPanel() {
        initComponent();
    }

    // ComponentListener-----------------------------
    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    @Override
    public void componentHidden(
            ComponentEvent e) {
    }

    @Override
    public void focusGained(
            FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    @Override
    public void focusLost(
            FocusEvent e) {
    }

    // ActionListner-------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        JComponent o = (JComponent)e.getSource();
        // category buttons---------------------------------------------------------------------------------
        if (o instanceof TableButton) {
            TableButton tableToggle = (TableButton) o;
         	BarFrame.btnCurTable = tableToggle;
         	
        	if(((TableButton)o).getBackground() != colorSelected){	//if before is not selected, then update the status
        		o.setBackground(colorSelected);
        		try {
        			Statement smt = PIMDBModel.getReadOnlyStatement();
        			smt.executeQuery("update dining_Table set status = 1 WHERE name = '" + tableToggle.getText() + "'");
        		}catch(Exception exp) {
        			ErrorUtil.write(exp);
        		}
        	}else {
				try {
					Statement smt = PIMDBModel.getReadOnlyStatement();
					ResultSet rs = smt.executeQuery(
							"select billNum from dining_Table where name = '" + tableToggle.getText() + "'");
					rs.afterLast();
					rs.relative(-1);
					int num = rs.getInt("billNum");

					if (num == 0) { // check if it's empty
						BarFrame.instance.lblCurBill.setText("0");
						BarFrame.instance.switchMode(1);
					} else { // if it's not empty, display a dialog to show all the bills.
						new BillListDlg(tableToggle, tableToggle.getText()).setVisible(true);
					}
					tableToggle.setSelected(true);
				} catch (Exception exp) {
        			ErrorUtil.write(exp);
        		}
        	}
        }
        //JButton------------------------------------------------------------------------------------------------
        else if (o instanceof JButton) {
        	if (o == btnLine_1_1) {
            } else if (o == btnLine_1_7) { 
            } else if (o == btnLine_2_4) {
            } else if (o == btnLine_2_5) {
            } else if (o == btnLine_2_6) {
                BarFrame.instance.switchMode(2);
            } else if (o == btnLine_2_7) {
            }else if (o == btnLine_2_8) {
            }else if (o == btnLine_2_9) {
            }
        }
        //JToggleButton-------------------------------------------------------------------------------------
        else if(o instanceof JToggleButton) {
        	if(o == btnLine_1_4) {
        	}else if (o == btnLine_1_5) {
        		
        	}else if (o == btnLine_1_7) {
        	}
        }
    }

    void reLayout() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 10) / 9;
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
        btnLine_2_7.setBounds(btnLine_2_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_8.setBounds(btnLine_2_7.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_9.setBounds(btnLine_2_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        // line 1
        btnLine_1_1.setBounds(CustOpts.HOR_GAP, btnLine_2_1.getY() - tBtnHeight - CustOpts.VER_GAP, tBtnWidht,
                tBtnHeight);
        btnLine_1_2.setBounds(btnLine_1_1.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_3.setBounds(btnLine_1_2.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_4.setBounds(btnLine_1_3.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_5.setBounds(btnLine_1_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_6.setBounds(btnLine_1_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_7.setBounds(btnLine_1_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_8.setBounds(btnLine_1_7.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_9.setBounds(btnLine_1_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
    }

    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            if ("System".equalsIgnoreCase(LoginDlg.USERNAME)) {
                curSecurityStatus++;
                BarFrame.setStatusMes(BarDlgConst.ADMIN_MODE);
                // @TODO: might need to do some modification on the interface.
                revalidate();
                return true;
            }
        }
        return false;
    }

    private void initComponent() {
        btnLine_1_1 = new JButton(BarDlgConst.EXACT_AMOUNT);
        btnLine_1_2 = new JButton(BarDlgConst.CASH);
        btnLine_1_3 = new JButton(BarDlgConst.PAY);
        btnLine_1_4 = new JToggleButton("");//BarDlgConst.REMOVE);
        btnLine_1_5 = new JToggleButton("");//BarDlgConst.VOID_ITEM);
        btnLine_1_6 = new JButton(BarDlgConst.SPLIT_BILL);
        btnLine_1_7 = new JToggleButton(BarDlgConst.QTY);
        btnLine_1_8 = new JButton(BarDlgConst.DISC_ITEM);
        btnLine_1_9 = new JButton(BarDlgConst.PRINT_BILL);
        
        btnLine_2_1 = new JButton(BarDlgConst.DEBIT);
        btnLine_2_2 = new JButton(BarDlgConst.VISA);
        btnLine_2_3 = new JButton(BarDlgConst.MASTER);
        btnLine_2_4 = new JButton(BarDlgConst.CANCEL_ALL);
        btnLine_2_5 = new JButton(BarDlgConst.VOID_ORDER);
        btnLine_2_6 = new JButton(BarDlgConst.SETTINGS);
        btnLine_2_7 = new JButton(BarDlgConst.RETURN);
        btnLine_2_8 = new JButton(BarDlgConst.MORE);
        btnLine_2_9 = new JButton(BarDlgConst.SEND);

        // border----------
        setLayout(null);

        // built
        add(btnLine_2_1);
        add(btnLine_2_2);
        add(btnLine_2_3);
        add(btnLine_2_4);
        add(btnLine_2_5);
        add(btnLine_2_6);
        add(btnLine_2_7);
        add(btnLine_2_8);
        add(btnLine_2_9);

        add(btnLine_1_1);
        add(btnLine_1_2);
        add(btnLine_1_3);
        add(btnLine_1_4);
        add(btnLine_1_5);
        add(btnLine_1_6);
        add(btnLine_1_7);
        add(btnLine_1_8);
        add(btnLine_1_9);

        // add listener
        addComponentListener(this);

        btnLine_2_1.addActionListener(this);
        btnLine_2_2.addActionListener(this);
        btnLine_2_3.addActionListener(this);
        btnLine_2_4.addActionListener(this);
        btnLine_2_5.addActionListener(this);
        btnLine_2_6.addActionListener(this);
        btnLine_2_7.addActionListener(this);
        btnLine_2_8.addActionListener(this);
        btnLine_2_9.addActionListener(this);

        btnLine_1_1.addActionListener(this);
        btnLine_1_2.addActionListener(this);
        btnLine_1_3.addActionListener(this);
        btnLine_1_4.addActionListener(this);
        btnLine_1_5.addActionListener(this);
        btnLine_1_6.addActionListener(this);
        btnLine_1_7.addActionListener(this);
        btnLine_1_8.addActionListener(this);
        btnLine_1_9.addActionListener(this);
        
    }

    // menu and category buttons must be init after initContent---------
	static void initTableBtns(Container container, ActionListener actionListener) {
		//clean existing btns
		for (int i = btnTables.size() - 1; i >=0; i--) {
			TableButton tableToggleButton = btnTables.get(i);
			btnTables.remove(i);
			container.remove(tableToggleButton);
		}
		//renite buttons.
		try {
            Statement smt = PIMDBModel.getReadOnlyStatement();

            // load all the categorys---------------------------
            ResultSet rs = smt.executeQuery("select ID, Name, posX, posY, width, height, type, billNum, status from dining_Table order by DSP_INDEX");
            rs.beforeFirst();

            int tmpPos = 0;
            while (rs.next()) {
            	TableButton tableToggleButton = new TableButton();
            	
            	tableToggleButton.setId(tmpPos);
            	tableToggleButton.setText(rs.getString("Name"));
            	tableToggleButton.setBounds(rs.getInt("posX"), rs.getInt("posY"), rs.getInt("width"), rs.getInt("height"));
            	tableToggleButton.setType(rs.getInt("type"));		//it's rectanglee or round?
            	if(rs.getInt("status") > 0)
            		tableToggleButton.setBackground(colorSelected);
            	tableToggleButton.setMargin(new Insets(0, 0, 0, 0));
    			tableToggleButton.addActionListener(actionListener);
    			container.add(tableToggleButton);
    			
            	btnTables.add(tableToggleButton);
                tmpPos++;
            }
            
            rs.close();// 关闭
            smt.close();
		}catch(Exception e) {
			ErrorUtil.write("Unexpected exception when init the tables from db." + e);
		}
	}

    private JButton btnLine_1_1;
    private JButton btnLine_1_2;
    private JButton btnLine_1_3;
    private JToggleButton btnLine_1_4;
    private JToggleButton btnLine_1_5;
    private JButton btnLine_1_6;
    private JToggleButton btnLine_1_7;
    private JButton btnLine_1_8;
    private JButton btnLine_1_9;
    
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
