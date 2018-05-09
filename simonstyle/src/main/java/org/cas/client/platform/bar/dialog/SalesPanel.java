package org.cas.client.platform.bar.dialog;

import java.awt.Color;
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
import org.hsqldb.Table;

//Identity表应该和Employ表合并。
public class SalesPanel extends JPanel implements ComponentListener, ActionListener, FocusListener {

	String[][] categoryNameMetrix;
    ArrayList<ArrayList<CategoryToggleButton>> onSrcCategoryTgbMatrix = new ArrayList<ArrayList<CategoryToggleButton>>();
    CategoryToggleButton tgbActiveCategory;
    
    //Dish is more complecated than category, it's devided by category first, then divided by page.
    String[][] dishNameMetrix;// the struction must be [3][index]. it's more convenient than [index][3]
    String[][] onScrDishNameMetrix;// it's sub set of all menuNameMetrix
    private ArrayList<ArrayList<MenuButton>> onSrcMenuBtnMatrix = new ArrayList<ArrayList<MenuButton>>();

    //for print
    public static String SUCCESS = "0";
    public static String ERROR = "2";
    
    
    public SalesPanel() {
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
        Object o = e.getSource();
        //JButton------------------------------------------------------------------------------------------------
        if (o instanceof JButton) {
        	if (o == btnLine_1_1) {
        		
            } else if (o == btnLine_1_9) {//send
            	List<Dish> newDishes = getNewDishes();
            	
            	//if all record are new, means it's adding a new bill.otherwise, it's adding output to exixting bill.
            	if(newDishes.size() == billPanel.selectdDishAry.size()) {
                    BarFrame.instance.lblCurBill.setText(BillListPanel.getANewBillNumber());
            	}
            	
            	//send to printer
            	//prepare the printing String and do printing
            	if(WifiPrintService.SUCCESS != WifiPrintService.exePrintCommand(
            			newDishes, BarFrame.instance.menuPanel.printers, BarFrame.instance.valCurTable.getText())) {
            		BarFrame.setStatusMes("WARNING!!!!!!!!!!!!! print error, please try again.");
            		return;
            	}
            	
            	//save to db output
                try {
                    Statement smt =  PIMDBModel.getReadOnlyStatement();
                    for (Dish dish : newDishes) {
//                    	if(dish.getOutputID() > -1)	//if it's already saved into db, don't ignore.
//                    		continue;
                    	
                    	String time = new Date().toLocaleString();
                    	String curBillId = BarFrame.instance.lblCurBill.getText();
                    	if("0".equals(curBillId))
                    		curBillId = "1";
	                    StringBuilder sql = new StringBuilder(
	                        "INSERT INTO output(SUBJECT, CONTACTID, PRODUCTID, AMOUNT, TOLTALPRICE, DISCOUNT, CONTENT, EMPLOYEEID, TIME) VALUES ('")
	                        .append(BarFrame.instance.valCurTable.getText()).append("', ")	//subject ->table id
	                        .append(curBillId).append(", ")			//contactID ->bill id
	                        .append(dish.getId()).append(", ")	//productid
	                        .append(dish.getNum()).append(", ")	//amount
	                        .append((dish.getPrice() - dish.getDiscount()) * dish.getNum()).append(", ")	//totalprice int
	                        .append(dish.getDiscount() * dish.getNum()).append(", '")	//discount
	                        .append(dish.getModification()).append("', ")				//content
	                        .append(LoginDlg.USERID).append(", '")		//emoployid
	                        .append(time).append("') ");
	                    smt.executeUpdate(sql.toString());

	                    //in case some store need to stay in the interface after clicking the send button. 
//	                    sql = new StringBuilder("Select id from output where SUBJECT = '")
//	                        .append(BarFrame.btnCurTable.getText()).append("' and CONTACTID = ")
//	                        .append(BarFrame.instance.lblCurBill.getText()).append(" and PRODUCTID = ")
//	                        .append(dish.getId()).append(" and AMOUNT = ")
//	                        .append(dish.getNum()).append(" and TOLTALPRICE = ")
//	                        .append((dish.getPrice() - dish.getDiscount()) * dish.getNum()).append(" and DISCOUNT = ")
//	                        .append(dish.getDiscount() * dish.getNum()).append(" and EMPLOYEEID = ")
//	                        .append(LoginDlg.USERID).append(" and TIME = '")
//	                        .append(time).append("'");
//	                    ResultSet rs = smt.executeQuery(sql.toString());
//	                    rs.beforeFirst();
//                        while (rs.next()) {
//                        	dish.setOutputID(rs.getInt("id"));
//                        }
//	                    
//	                    rs.close();
                    }
                    smt.close();
                    smt = null;
                    if("true".equalsIgnoreCase((String)CustOpts.custOps.getValue("isCounterMode"))) {
                    	billPanel.resetTableArea();
                    }else {
                    	BarFrame.instance.switchMode(0);
                    }
                }catch(Exception exp) {
                	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                    exp.printStackTrace();
                }
            }else if (o == btnLine_2_4) { // cancel all
            	if(billPanel.selectdDishAry.size() > 0) {
            		int lastSavedRow = billPanel.selectdDishAry.size() - 1 - getNewDishes().size();
            		//update array first.
            		for(int i = billPanel.selectdDishAry.size() - 1; i > lastSavedRow; i--) {
            			billPanel.selectdDishAry.remove(i);
            		}
            		//update the table view
            		int tColCount = billPanel.tblSelectedDish.getColumnCount();
            		int tValidRowCount = billPanel.selectdDishAry.size(); // get the used RowCount
            		Object[][] tValues = new Object[tValidRowCount][tColCount];
            		for (int r = 0; r < tValidRowCount; r++) {
            			for (int c = 0; c < tColCount; c++)
            				tValues[r][c] = c == 0 ? r + 1: billPanel.tblSelectedDish.getValueAt(r, c);
            		}
            		billPanel.tblSelectedDish.setDataVector(tValues, billPanel.header);
            		billPanel.resetColWidth(billPanel.getWidth());
            		billPanel.tblSelectedDish.setSelectedRow(tValues.length - 1);
            		billPanel.updateTotleArea();
            	}else {
            		//update db.
            		if(isLastBillOfCurTable()) {
            			resetCurTableDBStatus();
            		}
            		BarFrame.instance.switchMode(0);
            	}
            } else if (o == btnLine_2_5) { // void all includ saved ones
    	        //update db, delete relevant orders.
            	for (Dish dish : billPanel.selectdDishAry) {
            		Dish.delete(dish);
				}
            	//if the bill amount is 1, cancel the selected status of the table.
        		if(isLastBillOfCurTable()) {
        			resetCurTableDBStatus();
        		}
            	BarFrame.instance.switchMode(0);
            } else if (o == btnLine_2_6) { // enter the setting mode.(admin interface)
                BarFrame.instance.switchMode(3);
            } else if (o == btnLine_2_8) {//more
            	new MoreButtonsDlg(this).show((JButton)o);
            } else if (o == btnLine_2_9) { // return
            	if(billPanel.selectdDishAry.size() > 0) {
	            	Dish dish = billPanel.selectdDishAry.get(billPanel.selectdDishAry.size() - 1);
	            	if(dish.getId() < 0) {	//has new record.
	            		if(JOptionPane.showConfirmDialog(BarFrame.instance, 
	            				BarDlgConst.COMFIRMLOSTACTION, DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) == 0) {
	    	                 return;	
	    	            }
	            	}
            	}
            	BarFrame.instance.switchMode(0);
            }
        }
        //JToggleButton-------------------------------------------------------------------------------------
        else if(o instanceof JToggleButton) {
        	if(o == btnLine_1_4) {
        	}else if (o == btnLine_1_5) {
        		
        	}else if (o == btnLine_1_7) {	//QTY
        		//pomp up a numberPanelDlg
        		BarFrame.instance.numberPanelDlg.setBtnSource(btnLine_1_7);
        		//should no record selected, select the last one.
        		BarFrame.instance.numberPanelDlg.setVisible(btnLine_1_7.isSelected());	//@NOTE: it's not model mode.
        		if(btnLine_1_7.isSelected()) {
        			try {
        				String curContent = BarFrame.instance.numberPanelDlg.curContent;
                		int tQTY = Integer.valueOf(curContent);
                    	int row = billPanel.tblSelectedDish.getSelectedRow();
                    	billPanel.tblSelectedDish.setValueAt("x" + curContent, row, 3);
                    	billPanel.selectdDishAry.get(row).setNum(tQTY);
                    	billPanel.updateTotleArea();
                	}catch(Exception exp) {
                    	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                		return;
                	}
        		}
        		if(billPanel.tblSelectedDish.getSelectedRow() < 0) {
        			billPanel.tblSelectedDish.setSelectedRow(billPanel.tblSelectedDish.getRowCount()-1);
        		}
        		//present the value in number dialog.
        		Object obj = billPanel.tblSelectedDish.getValueAt(billPanel.tblSelectedDish.getSelectedRow(), 3);
        		BarFrame.instance.numberPanelDlg.setContents(obj.toString());
        	}
        }
    }
    
    private boolean isLastBillOfCurTable(){
    	int num = 0;
    	try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
            ResultSet rs = smt.executeQuery("SELECT DISTINCT contactID from output where SUBJECT = '"
                    + BarFrame.instance.valCurTable.getText() + "' and deleted = false order by contactID");
			rs.afterLast();
			rs.relative(-1);
			num = rs.getRow();
		} catch (Exception exp) {
			ErrorUtil.write(exp);
		}
    	return num <= 1;
    }
    
    private void resetCurTableDBStatus(){
    	try {
        	Statement smt =  PIMDBModel.getStatement();
            smt.executeQuery("update dining_Table set status = 0 WHERE name = '" + BarFrame.instance.valCurTable.getText() + "'");
    	}catch(Exception exp) {
    		ErrorUtil.write(exp);
    	}
    }
    
	private List<Dish> getNewDishes() {
		List<Dish> newDishes = new ArrayList<Dish>();
		for (Dish dish : billPanel.selectdDishAry) {
			if(dish.getOutputID() > -1)	//if it's already saved into db, ignore.
				continue;
			else {
				newDishes.add(dish);
			}
		}
		return newDishes;
	}
    
    void reLayout() {
        int panelHeight = getHeight();

        int tBtnWidht = (getWidth() - CustOpts.HOR_GAP * 10) / 9;
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
        
        // TOP part============================
        int topAreaHeight = btnLine_1_1.getY() - 3 * CustOpts.VER_GAP;

        Double tableWidth = (Double) CustOpts.custOps.hash2.get("TableWidth");
        tableWidth = (tableWidth == null || tableWidth < 0.2) ? 0.4 : tableWidth;
        
        billPanel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
                (int) (getWidth() * tableWidth), topAreaHeight);
        
        // menu area--------------
        int xMenuArea = billPanel.getX() + billPanel.getWidth() + CustOpts.HOR_GAP;
        int widthMenuArea = getWidth() - billPanel.getWidth() - CustOpts.HOR_GAP * 2;

        BarFrame.instance.menuPanel.setBounds(xMenuArea, billPanel.getY(), widthMenuArea, topAreaHeight);
        BarFrame.instance.menuPanel.reLayout();

        billPanel.resetColWidth(billPanel.getWidth());
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

    private void printInvoice(
            String pDate) {
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
        btnLine_1_9 = new JButton(BarDlgConst.SEND);
        
        btnLine_2_1 = new JButton(BarDlgConst.DEBIT);
        btnLine_2_2 = new JButton(BarDlgConst.VISA);
        btnLine_2_3 = new JButton(BarDlgConst.MASTER);
        btnLine_2_4 = new JButton(BarDlgConst.CANCEL_ALL);
        btnLine_2_5 = new JButton(BarDlgConst.VOID_ORDER);
        btnLine_2_6 = new JButton(BarDlgConst.SETTINGS);
        btnLine_2_7 = new JButton(BarDlgConst.PRINT_BILL);
        btnLine_2_8 = new JButton(BarDlgConst.MORE);
        btnLine_2_9 = new JButton(BarDlgConst.RETURN);

        billPanel = new BillPanel(this);
        // properties
        setLayout(null);
        
        // built
        add(btnLine_2_1);
        add(btnLine_2_2);
        add(btnLine_2_3);
        add(btnLine_2_4);
        add(btnLine_2_5);
        add(btnLine_2_6);
        add(btnLine_2_9);
        add(btnLine_2_8);
        add(btnLine_1_9);

        add(btnLine_1_1);
        add(btnLine_1_2);
        add(btnLine_1_3);
        add(btnLine_1_4);
        add(btnLine_1_5);
        add(btnLine_1_6);
        add(btnLine_1_7);
        add(btnLine_1_8);
        add(btnLine_2_7);

        add(billPanel);
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
        btnLine_2_9.addActionListener(this);
        btnLine_2_8.addActionListener(this);
        btnLine_1_9.addActionListener(this);

        btnLine_1_1.addActionListener(this);
        btnLine_1_2.addActionListener(this);
        btnLine_1_3.addActionListener(this);
        btnLine_1_4.addActionListener(this);
        btnLine_1_5.addActionListener(this);
        btnLine_1_6.addActionListener(this);
        btnLine_1_7.addActionListener(this);
        btnLine_1_8.addActionListener(this);
        btnLine_2_7.addActionListener(this);
    }

    private void enableBtns(
            boolean pIsEnable) {
    }

    private JButton btnLine_1_1;
    private JButton btnLine_1_2;
    private JButton btnLine_1_3;
    private JToggleButton btnLine_1_4;
    private JToggleButton btnLine_1_5;
    private JButton btnLine_1_6;
    JToggleButton btnLine_1_7;
    private JButton btnLine_1_8;
    private JButton btnLine_2_7;
    
    private JButton btnLine_2_1;
    private JButton btnLine_2_2;
    private JButton btnLine_2_3;
    private JButton btnLine_2_4;
    private JButton btnLine_2_5;
    private JButton btnLine_2_6;
    private JButton btnLine_2_9;
    private JButton btnLine_2_8;
    private JButton btnLine_1_9;
    
    BillPanel billPanel;
}
