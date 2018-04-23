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
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

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
import org.cas.client.platform.bar.beans.User;
import org.cas.client.platform.bar.model.Dish;
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
public class BarGeneralPanel extends JPanel implements ComponentListener, ActionListener, FocusListener, ListSelectionListener, PIMTableRenderAgent {
    private final int PRICECOLID = 3;
    private final int TOTALCOLID = 4;
    private final int COUNDCOLID = 2;

    private final int USER_STATUS = 1;
    private final int ADMIN_STATUS = 2;
    private int curSecurityStatus = USER_STATUS;
    
    private String curTable = "";
    int curBillID;
    
    User curUser;
    
    private int curCategoryPage = 0;
    private int categoryNumPerPage = 0;

    private int curMenuPageNum = 0;
    private int curMenuPerPage = 0;


    Integer categoryColumn = (Integer) CustOpts.custOps.hash2.get("categoryColumn");
    Integer categoryRow = (Integer) CustOpts.custOps.hash2.get("categoryRow");
    Integer menuColumn = (Integer) CustOpts.custOps.hash2.get("menuColumn");
    Integer menuRow = (Integer) CustOpts.custOps.hash2.get("menuRow");
    
    private int[] categoryIdAry;
    String[][] categoryNameMetrix;
    ArrayList<ArrayList<CategoryToggleButton>> onSrcCategoryTgbMatrix = new ArrayList<ArrayList<CategoryToggleButton>>();
    CategoryToggleButton tgbActiveCategory;
    
    //Dish is more complecated than category, it's devided by category first, then divided by page.
    String[][] dishNameMetrix;// the struction must be [3][index]. it's more convenient than [index][3]
    String[][] onScrDishNameMetrix;// it's sub set of all menuNameMetrix
    private ArrayList<ArrayList<MenuButton>> onSrcMenuBtnMatrix = new ArrayList<ArrayList<MenuButton>>();
    
    private Dish[] dishAry;
    private Dish[] onScrDishAry;
    private ArrayList<Dish> selectdDishAry = new ArrayList<Dish>();
    
    public static String startTime;

    //flags
    NumberPanelDlg qtyDlg; 
    
    public BarGeneralPanel() {
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
        //array button------------------------------------------------------------------------------------------
        if(o instanceof ArrayButton) {
	        if (o == btnPageUpTable) {
	            btnPageDownTable.setEnabled(true);
	            if (true) {
	                btnPageUpTable.setEnabled(false);
	            }
	        } else if (o == btnPageDownTable) {
	            btnPageUpTable.setEnabled(true);
	            if (true) {
	                btnPageDownTable.setEnabled(false);
	            }
	        } else if (o == btnPageUpCategory) {
	            curCategoryPage--;
	            // adjust status
	            btnPageDownCategory.setEnabled(true);
	            if (curCategoryPage == 0) {
	                btnPageUpCategory.setEnabled(false);
	            }
	
	            reInitCategoryAndMenuBtns();
	            reLayout();
	        } else if (o == btnPageDownCategory) {
	            curCategoryPage++;
	            // adjust status
	            btnPageUpCategory.setEnabled(true);
	            if (curCategoryPage * categoryNumPerPage > categoryNameMetrix.length) {
	                btnPageDownCategory.setEnabled(false);
	            }
	
	            reInitCategoryAndMenuBtns();
	            reLayout();
	        } else if (o == btnPageUpMenu) {
	            curMenuPageNum--;
	            btnPageDownMenu.setEnabled(true);
	            if (curMenuPageNum == 0) {
	                btnPageUpMenu.setEnabled(false);
	            }
	            reInitCategoryAndMenuBtns();
	            reLayout();
	        } else if (o == btnPageDownMenu) {
	            curMenuPageNum++;
	            btnPageUpMenu.setEnabled(true);
	            if (curMenuPageNum * curMenuPerPage > dishNameMetrix.length) {
	                btnPageDownMenu.setEnabled(false);
	            }
	            reInitCategoryAndMenuBtns();
	            reLayout();
	        }
        }
        // category buttons---------------------------------------------------------------------------------
        else if (o instanceof CategoryToggleButton) {
            CategoryToggleButton categoryToggle = (CategoryToggleButton) o;
            String text = categoryToggle.getText();
            if (text == null || text.length() == 0) { // check if it's empty
                if (curSecurityStatus == ADMIN_STATUS) { // and it's admin mode, add a Category.
                    CategoryDlg addCategoryDlg = new CategoryDlg(BarFrame.instance);
                    addCategoryDlg.setIndex(categoryToggle.getIndex());
                    addCategoryDlg.setVisible(true);
                } else {
                    if (adminAuthentication()) {
                        CategoryDlg addCategoryDlg = new CategoryDlg(BarFrame.instance);
                        addCategoryDlg.setIndex(categoryToggle.getIndex());
                        addCategoryDlg.setVisible(true);
                    }
                }
            } else { // if it's not empty
                if (!text.equals(tgbActiveCategory.getText())) {
                    //change active toggle button, and update active menus.
                    if (tgbActiveCategory != null) {
                        tgbActiveCategory.setSelected(false);
                    }
                    tgbActiveCategory = categoryToggle;
                    initCategoryAndDishes();	//fill menu buttons with menus belong to this category.
                    reLayout();
                } else if (curSecurityStatus == ADMIN_STATUS) {
                    CategoryDlg categoryDlg = new CategoryDlg(BarFrame.instance);
                    categoryDlg.setIndex(categoryToggle.getIndex());
                    categoryDlg.setVisible(true);
                }
            }
        }
        // menu buttons------------------------------------------------------------------------------------------
        else if (o instanceof MenuButton) {
            MenuButton menuButton = (MenuButton) o;
            String text = menuButton.getText();
            if (text == null || text.length() == 0) { // check if it's empty
                if (curSecurityStatus == ADMIN_STATUS) { // and it's admin mode, add a Category.
                    new DishDlg(BarFrame.instance, menuButton.getDspIndex()).setVisible(true);
                } else {
                    if (adminAuthentication())
                        new DishDlg(BarFrame.instance, menuButton.getDspIndex()).setVisible(true);
                }
            } else { // if it's not empty
                if (curSecurityStatus == ADMIN_STATUS) {
                    new DishDlg(BarFrame.instance, menuButton.getDish()).setVisible(true);
                } else {
                    // add into table.
                	addContentToList(menuButton.getDish());
                }
            }
        } 
        //JButton------------------------------------------------------------------------------------------------
        else if (o instanceof JButton) {
        	if (o == btnLine_1_1) { // cancel all
        		selectdDishAry.clear();
    	        Object[][] tValues = new Object[0][tblOrder.getColumnCount()];
                tblOrder.setDataVector(tValues, header);
                resetColWidth(srpContent.getWidth());
    	        updateTotleArea();
            } else if (o == btnLine_1_7) { // cancel all
        		
            } else if (o == btnLine_2_4) { // cancel all
        		selectdDishAry.clear();
    	        Object[][] tValues = new Object[0][tblOrder.getColumnCount()];
                tblOrder.setDataVector(tValues, header);
                resetColWidth(srpContent.getWidth());
    	        updateTotleArea();
            } else if (o == btnLine_2_5) { // cancel all
        		selectdDishAry.clear();
    	        Object[][] tValues = new Object[0][tblOrder.getColumnCount()];
                tblOrder.setDataVector(tValues, header);
                resetColWidth(srpContent.getWidth());
    	        updateTotleArea();
    	        //TODO update db, delete relevant orders.
            } else if (o == btnLine_2_6) { // enter the setting mode.(admin interface)
                adminAuthentication();
            } else if (o == btnLine_2_7) { // Logout
                if (curSecurityStatus == ADMIN_STATUS) {
                    curSecurityStatus--;
                    // @TODO: might need to do some modification on the interface.
                    setStatusMes(BarDlgConst.USE_MODE);
                } else {
                    BarFrame.instance.setVisible(false); //TODO： do we need to dispose?
                    new LoginDlg(null).setVisible(true);
                    if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
                    	curUser.setId(LoginDlg.USERID);
                    	curUser.setName(LoginDlg.USERNAME);
                    	curUser.setType(LoginDlg.USERTYPE);
                    	curUser.setLang(LoginDlg.USERLANG);
                        lblOperator.setText(BarDlgConst.Operator.concat(BarDlgConst.Colon).concat(
                                curUser.getName()));
                        
                        BarFrame.instance.setVisible(true);
                    }else {
                    	System.exit(0);
                    }
                }
            }else if (o == btnLine_2_8) {//more
            	new MoreButtonsDlg(this).show((JButton)o);
            }else if (o == btnLine_2_9) {//send
            	//TODO:send to printer
            	//save to db output
                try {
                    Connection conn = PIMDBModel.getConection();
                    Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    for (Dish dish : selectdDishAry) {
                    	String time = new Date().toLocaleString();
	                    StringBuilder sql = new StringBuilder(
	                        "INSERT INTO output(SUBJECT, CONTACTID, PRODUCTID, AMOUNT, TOLTALPRICE, DISCOUNT, CONTENT, EMPLOYEEID, TIME) VALUES ('")
	                        .append(curTable).append("', ")	//subject ->table id
	                        .append(curBillID).append(", ")			//contactID ->bill id
	                        .append(dish.getId()).append(", ")	//productid
	                        .append(dish.getNum()).append(", ")	//amount
	                        .append((dish.getPrice() - dish.getDiscount()) * dish.getNum()).append(", ")	//totalprice int
	                        .append(dish.getDiscount() * dish.getNum()).append(", '")	//discount
	                        .append(dish.getModification()).append("', ")				//content
	                        .append(curUser.getId()).append(", '")		//emoployid
	                        .append(time).append("') ");
	                    smt.executeUpdate(sql.toString());
                    
	                    sql = new StringBuilder("Select id from output where SUBJECT = '")
	                        .append(curTable).append("' and CONTACTID = ")
	                        .append(curBillID).append(" and PRODUCTID = ")
	                        .append(dish.getId()).append(" and AMOUNT = ")
	                        .append(dish.getNum()).append(" and TOLTALPRICE = ")
	                        .append((dish.getPrice() - dish.getDiscount()) * dish.getNum()).append(" and DISCOUNT = ")
	                        .append(dish.getDiscount() * dish.getNum()).append(" and EMPLOYEEID = ")
	                        .append(curUser.getId()).append(" and TIME = '")
	                        .append(time).append("'");
	                    ResultSet rs = smt.executeQuery(sql.toString());
	                    rs.beforeFirst();
	                    rs.next();
	                    dish.setOutputID(rs.getInt("id"));
	                    rs.close();
                    }
                    smt.close();
                    smt = null;
                    tblOrder.invalidate();
                    //tblOrder.repaint();
                }catch(Exception exp) {
                	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                    exp.printStackTrace();
                }
            }
        }
        //JToggleButton-------------------------------------------------------------------------------------
        else if(o instanceof JToggleButton) {
        	if(o == btnLine_1_4) {
        	}else if (o == btnLine_1_5) {
        		
        	}else if (o == btnLine_1_7) {	//QTY
        		//pomp up a numberPanelDlg
        		if(qtyDlg == null) {
        			qtyDlg = new NumberPanelDlg(BarFrame.instance, btnLine_1_7);
        		}
        		qtyDlg.setVisible(btnLine_1_7.isSelected());
        		if(tblOrder.getSelectedRow() > 0) {
        			Object obj = tblOrder.getValueAt(tblOrder.getSelectedRow(), 3);
        			qtyDlg.setContents(obj.toString());
        		}else {
        			tblOrder.setSelectedRow(tblOrder.getRowCount()-1);
        		}
        	}
        }
    }

    //table selection listener---------------------
	@Override
	public void valueChanged(ListSelectionEvent e) {
		DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)e.getSource();
		int selectedRow =  selectionModel.getMinSelectionIndex();
		if(selectedRow < 0 || selectedRow >= selectdDishAry.size()) 
			return;
		
		if(!btnLine_1_7.isSelected()) {	//if qty button not seleted.
			selectdDishAry.remove(selectedRow);
			
	        int tColCount = tblOrder.getColumnCount();
	        int tValidRowCount = getUsedRowCount(); // get the used RowCount
	        Object[][] tValues = new Object[tValidRowCount - 1][tColCount];
            for (int r = 0; r < tValidRowCount; r++) {
            	int rowNum = r + 1;
            	if(r == selectedRow) 
            		continue;
            	else if(r > selectedRow)
            		rowNum--;
            	
                for (int c = 0; c < tColCount; c++)
                    tValues[rowNum-1][c] = c == 0 ? rowNum: tblOrder.getValueAt(r, c);
            }
            tblOrder.setDataVector(tValues, header);
            resetColWidth(srpContent.getWidth());
	
	        updateTotleArea();
		}else {
			Object obj = tblOrder.getValueAt(selectedRow,3);
			//update the qty in qtyDlg.
			if(obj != null)
				qtyDlg.setContents(obj.toString());
		}
	}

	//table row color----------------------------------------------------------
	@Override
	public Color getBackgroundAtRow(int row) {
		if(selectdDishAry.size() > row) {
			Dish dish = selectdDishAry.get(row);
			if(dish.getOutputID() > 0) {
				return new Color(222, 111, 34);
			}else {
				return new Color(250, 250, 250);
			}
		}else
			return null;
	}

	@Override
	public Color getForegroundAtRow(int row) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void setStatusMes(
            String pMes) {
        lblStatus.setText(pMes);
    }

    void reLayout() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        lblOperator.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblOperator.getPreferredSize().width,
                lblOperator.getPreferredSize().height);
        int tHalfHeight = (panelHeight - lblOperator.getY() - lblOperator.getHeight()) / 2;
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 10) / 9;
        int tBtnHeight = panelHeight / 10;
        int tGap = tHalfHeight / 11;
        int tVGap = tGap * 2 / 3;
        int tCompH = tGap + tGap - tVGap;
        int tFieldWidth1 = panelWidth / 2;

        lblStartTime.setBounds(panelWidth - lblStartTime.getPreferredSize().width - CustOpts.HOR_GAP,
                lblOperator.getY(), lblStartTime.getPreferredSize().width, lblOperator.getHeight());
        lblShoestring.setBounds(
                lblOperator.getX()
                        + lblOperator.getWidth()
                        + (lblStartTime.getX() - lblOperator.getX() - lblOperator.getWidth() - lblShoestring
                                .getPreferredSize().width) / 2, lblOperator.getY(),
                lblShoestring.getPreferredSize().width, lblOperator.getHeight());

        // status---------
        lblStatus.setBounds(CustOpts.HOR_GAP, panelHeight - CustOpts.LBL_HEIGHT - CustOpts.VER_GAP, panelWidth
                - CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);

        // command buttons--------------
        // line 2
        btnLine_2_1.setBounds(CustOpts.HOR_GAP, lblStatus.getY() - tBtnHeight - CustOpts.VER_GAP, tBtnWidht,
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
        int topAreaHeight = btnLine_1_1.getY() - 3 * CustOpts.VER_GAP - lblOperator.getY() - lblOperator.getHeight();
        // table area-------------
        Double tableWidth = (Double) CustOpts.custOps.hash2.get("TableWidth");
        tableWidth = (tableWidth == null || tableWidth < 0.2) ? 0.4 : tableWidth;
        srpContent.setBounds(CustOpts.HOR_GAP, lblOperator.getY() + lblOperator.getHeight() + CustOpts.VER_GAP,
                (int) (panelWidth * tableWidth) - BarDlgConst.SCROLLBAR_WIDTH, topAreaHeight
                        - BarDlgConst.SubTotal_HEIGHT);

        btnPageUpTable.setBounds(CustOpts.HOR_GAP + srpContent.getWidth(), srpContent.getY() + srpContent.getHeight()
                - BarDlgConst.SCROLLBAR_WIDTH * 4 - CustOpts.VER_GAP, BarDlgConst.SCROLLBAR_WIDTH,
                BarDlgConst.SCROLLBAR_WIDTH * 2);
        btnPageDownTable.setBounds(btnPageUpTable.getX(), btnPageUpTable.getY() + btnPageUpTable.getHeight()
                + CustOpts.VER_GAP, BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH * 2);

        // sub total-------
        lblGSQ.setBounds(srpContent.getX(), srpContent.getY() + srpContent.getHeight(), srpContent.getWidth() / 4,
                BarDlgConst.SubTotal_HEIGHT * 1 / 3);
        lblQSQ.setBounds(lblGSQ.getX() + lblGSQ.getWidth(), lblGSQ.getY(), lblGSQ.getWidth(), lblGSQ.getHeight());
        lblSubTotle.setBounds(lblQSQ.getX() + lblQSQ.getWidth(), lblGSQ.getY(), lblQSQ.getWidth() * 2,
                lblGSQ.getHeight());
        lblTotlePrice.setBounds(lblSubTotle.getX(), lblSubTotle.getY() + lblSubTotle.getHeight(),
                lblSubTotle.getWidth(), BarDlgConst.SubTotal_HEIGHT * 2 / 3);

        // category area--------------
        int xMenuArea = srpContent.getX() + srpContent.getWidth() + CustOpts.HOR_GAP + BarDlgConst.SCROLLBAR_WIDTH;
        int widthMenuArea =
                (panelWidth - srpContent.getWidth() - CustOpts.HOR_GAP * 3) - BarDlgConst.SCROLLBAR_WIDTH * 2;
        Double categoryHeight = (Double) CustOpts.custOps.hash2.get("categoryHeight");
        categoryHeight = (categoryHeight == null || categoryHeight < 0.2) ? 0.4 : categoryHeight;

        int categeryBtnWidth = (widthMenuArea - CustOpts.HOR_GAP * (categoryColumn - 1)) / categoryColumn;
        int categeryBtnHeight =
                (int) ((topAreaHeight * categoryHeight - CustOpts.VER_GAP * (categoryRow - 1)) / categoryRow);

        for (int r = 0; r < categoryRow; r++) {
            for (int c = 0; c < categoryColumn; c++) {
                JToggleButton toggleButton = onSrcCategoryTgbMatrix.get(r).get(c);
                toggleButton.setBounds(xMenuArea + (categeryBtnWidth + CustOpts.HOR_GAP) * c, srpContent.getY()
                        + (categeryBtnHeight + CustOpts.VER_GAP) * r, categeryBtnWidth, categeryBtnHeight);
            }
        }
        btnPageUpCategory.setBounds(xMenuArea + widthMenuArea, srpContent.getY(), BarDlgConst.SCROLLBAR_WIDTH,
                BarDlgConst.SCROLLBAR_WIDTH * 2);
        btnPageDownCategory.setBounds(btnPageUpCategory.getX(),
                btnPageUpCategory.getY() + btnPageUpCategory.getHeight() + CustOpts.VER_GAP,
                BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH * 2);

        // menu area--------------
        int menuY = srpContent.getY() + (categeryBtnHeight + CustOpts.VER_GAP) * categoryRow + CustOpts.VER_GAP;
        int menuBtnWidth = (widthMenuArea - CustOpts.HOR_GAP * (menuColumn - 1)) / menuColumn;
        int menuBtnHeight = (int) ((topAreaHeight * (1 - categoryHeight) - CustOpts.VER_GAP * (menuRow)) / menuRow);
        for (int r = 0; r < menuRow; r++) {
            for (int c = 0; c < menuColumn; c++) {
                onSrcMenuBtnMatrix
                        .get(r)
                        .get(c)
                        .setBounds(xMenuArea + (menuBtnWidth + CustOpts.HOR_GAP) * c,
                                menuY + (menuBtnHeight + CustOpts.VER_GAP) * r, menuBtnWidth, menuBtnHeight);
            }
        }
        btnPageUpMenu.setBounds(btnPageUpCategory.getX(), srpContent.getY() + topAreaHeight
                - BarDlgConst.SCROLLBAR_WIDTH * 4 - CustOpts.VER_GAP, BarDlgConst.SCROLLBAR_WIDTH,
                BarDlgConst.SCROLLBAR_WIDTH * 2);
        btnPageDownMenu.setBounds(btnPageUpMenu.getX(), btnPageUpMenu.getY() + btnPageUpMenu.getHeight()
                + CustOpts.VER_GAP, BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH * 2);

        resetColWidth(srpContent.getWidth());
    }

    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            if ("System".equalsIgnoreCase(LoginDlg.USERNAME)) {
                curSecurityStatus++;
                setStatusMes(BarDlgConst.ADMIN_MODE);
                // @TODO: might need to do some modification on the interface.
                revalidate();
                return true;
            }
        }
        return false;
    }

    private void resetColWidth(int tableWidth) {
        PIMTableColumn tmpCol1 = tblOrder.getColumnModel().getColumn(0);
        tmpCol1.setWidth(40);
        tmpCol1.setPreferredWidth(40);
        PIMTableColumn tmpCol3 = tblOrder.getColumnModel().getColumn(2);
        tmpCol3.setWidth(40);
        tmpCol3.setPreferredWidth(40);
        PIMTableColumn tmpCol4 = tblOrder.getColumnModel().getColumn(3);
        tmpCol4.setWidth(40);
        tmpCol4.setPreferredWidth(40);
        PIMTableColumn tmpCol5 = tblOrder.getColumnModel().getColumn(4);
        tmpCol5.setWidth(40);
        tmpCol5.setPreferredWidth(40);

        PIMTableColumn tmpCol2 = tblOrder.getColumnModel().getColumn(1);
        tmpCol2.setWidth(tableWidth - tmpCol1.getWidth() - tmpCol3.getWidth() - tmpCol4.getWidth() - tmpCol5.getWidth() - 3);
        tmpCol2.setPreferredWidth(tmpCol2.getWidth());
        
        tblOrder.validate();
        tblOrder.revalidate();
        tblOrder.invalidate();
    }

    // 将对话盒区域的内容加入到列表
    private void addContentToList(Dish dish) {
        int tRowCount = tblOrder.getRowCount(); // add content to the table.
        int tColCount = tblOrder.getColumnCount();
        int tValidRowCount = getUsedRowCount(); // get the used RowCount
        if (tRowCount == tValidRowCount) { // no line is empty, add a new Line.
            Object[][] tValues = new Object[tRowCount + 1][tColCount];
            for (int r = 0; r < tRowCount; r++)
                for (int c = 0; c < tColCount; c++)
                    tValues[r][c] = tblOrder.getValueAt(r, c);
            tblOrder.setDataVector(tValues, header);
            resetColWidth(srpContent.getWidth());
        }else {
        	tRowCount--;
        }
        tblOrder.setValueAt(tRowCount + 1, tValidRowCount, 0); // set the code.
        tblOrder.setValueAt(dish.getLanguage(CustOpts.custOps.getUserLang()), tValidRowCount, 1);// set the Name.
        tblOrder.setValueAt(dish.getSize() > 1 ? dish.getSize() : "", tValidRowCount, 2); // set the count.
        tblOrder.setValueAt("x1", tValidRowCount, 3); // set the count.
        tblOrder.setValueAt(dish.getPrice()/100f, tValidRowCount, 4); // set the price.
        
        //tblOrder.setSelectedRow(tValidRowCount);	//@NOTE:must before adding into the array, so it can be ignored by 
        selectdDishAry.add(dish.clone());					//valueChanged process. not being cleared immediately
        
        updateTotleArea();
    }

    private void updateTotleArea() {
    	Object g = CustOpts.custOps.getValue(BarDlgConst.GST);
    	Object q = CustOpts.custOps.getValue(BarDlgConst.QST);
    	float gstRate = g == null ? 5 : Float.valueOf((String)g);
    	float qstRate = q == null ? 9.975f : Float.valueOf((String)q);
    	float totalGst = 0;
    	float totalQst = 0;
    	int subTotal = 0;
    	
    	for(Dish dish: selectdDishAry) {
    		int price = dish.getPrice();
    		int gst = (int) (price * (dish.getGst() * gstRate / 100f));
    		int qst = (int) (price * (dish.getQst() * qstRate / 100f));
    		int num = dish.getNum();
    	
    		subTotal += price * num;
    		totalGst += gst;
    		totalQst += qst;
    	}
    	
    	lblSubTotle.setText(BarDlgConst.Subtotal + " : " + String.valueOf(subTotal/100f));
        lblGSQ.setText(BarDlgConst.QST + " : " + String.valueOf(((int)totalGst)/100f));
        lblQSQ.setText(BarDlgConst.GST + " : " + String.valueOf(((int)totalQst)/100f));
        lblTotlePrice.setText(BarDlgConst.Total + " : " + String.valueOf(((int) (subTotal + totalGst + totalQst))/100f));
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
        int tShoestring = 0;
        try {
            tShoestring = Integer.parseInt((String) CustOpts.custOps.getValue(BarDlgConst.Shoestring));
        } catch (Exception exp) {
        }
        startTime = Calendar.getInstance().getTime().toLocaleString();
        lblOperator = new JLabel(BarDlgConst.Operator.concat(BarDlgConst.Colon).concat(LoginDlg.USERNAME));
        lblShoestring =
                new JLabel(BarDlgConst.LeftMoney.concat(BarDlgConst.Colon)
                        .concat(decimalFormat.format(tShoestring / 100.0)).concat(BarDlgConst.Unit));
        lblStartTime = new JLabel(BarDlgConst.StartTime.concat(BarDlgConst.Colon).concat(startTime));// @Todo:以后改为从服务器上获取。
        lblSubTotle = new JLabel(BarDlgConst.Subtotal);
        lblGSQ = new JLabel(BarDlgConst.QST);
        lblQSQ = new JLabel(BarDlgConst.GST);
        lblTotlePrice = new JLabel(BarDlgConst.Total);
        
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
        btnLine_2_7 = new JButton(BarDlgConst.LOGOUT);
        btnLine_2_8 = new JButton(BarDlgConst.MORE);
        btnLine_2_9 = new JButton(BarDlgConst.SEND);
        
        btnPageUpTable = new ArrayButton("↑");
        btnPageDownTable = new ArrayButton("↓");
        btnPageUpCategory = new ArrayButton("↑");
        btnPageDownCategory = new ArrayButton("↓");
        btnPageUpMenu = new ArrayButton("↑");
        btnPageDownMenu = new ArrayButton("↓");

        tblOrder = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblOrder);
        lblStatus = new JLabel();

        // properties
        setLayout(null);
        tblOrder.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblOrder.setAutoscrolls(true);
        tblOrder.setRowHeight(30);
        tblOrder.setCellEditable(false);
        tblOrder.setRenderAgent(this);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);

        // Margin-----------------
        btnPageUpTable.setMargin(new Insets(0,0,0,0));
        btnPageDownTable.setMargin(btnPageUpTable.getInsets());
        btnPageUpCategory.setMargin(btnPageUpTable.getInsets());
        btnPageDownCategory.setMargin(btnPageUpTable.getInsets());
        btnPageUpMenu.setMargin(btnPageUpTable.getInsets());
        btnPageDownMenu.setMargin(btnPageUpTable.getInsets());

        // border----------
        tblOrder.setBorder(null);
        lblStatus.setBorder(null);
        // forcus-------------
        tblOrder.setFocusable(false);

        // disables
        btnPageUpCategory.setEnabled(false);
        btnPageUpMenu.setEnabled(false);
        btnPageUpTable.setEnabled(false);

        // built
        add(lblOperator);
        add(lblShoestring);
        add(lblStartTime);

        add(lblSubTotle);
        add(lblGSQ);
        add(lblQSQ);
        add(lblTotlePrice);

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

        add(btnPageUpTable);
        add(btnPageDownTable);
        add(btnPageUpCategory);
        add(btnPageDownCategory);
        add(btnPageUpMenu);
        add(btnPageDownMenu);

        add(srpContent);
        add(lblStatus);

        // add listener
        addComponentListener(this);

        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        // tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）
        btnPageUpTable.addActionListener(this);
        btnPageDownTable.addActionListener(this);
        btnPageUpCategory.addActionListener(this);
        btnPageDownCategory.addActionListener(this);
        btnPageUpMenu.addActionListener(this);
        btnPageDownMenu.addActionListener(this);

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

        // initContents--------------
        initCategoryAndDishes();
        initTable();
    }

    public void initCategoryAndDishes() {
        try {
            Connection connection = PIMDBModel.getConection();
            Statement statement =
                    connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // load all the categorys---------------------------
            ResultSet categoryRS = statement.executeQuery("select ID, LANG1, LANG2, LANG3 from CATEGORY order by DSP_INDEX");
            categoryRS.afterLast();
            categoryRS.relative(-1);
            int tmpPos = categoryRS.getRow();
            categoryIdAry = new int[tmpPos];
            categoryNameMetrix = new String[3][tmpPos];
            categoryRS.beforeFirst();

            tmpPos = 0;
            while (categoryRS.next()) {
                categoryIdAry[tmpPos] = categoryRS.getInt("ID");
                
                categoryNameMetrix[0][tmpPos] = categoryRS.getString("LANG1");
                categoryNameMetrix[1][tmpPos] = categoryRS.getString("LANG2");
                categoryNameMetrix[2][tmpPos] = categoryRS.getString("LANG3");
                
                tmpPos++;
            }
            categoryRS.close();// 关闭

            // load all the dishes----------------------------
            ResultSet productRS =
                    statement
                            .executeQuery("select ID, CODE, MNEMONIC, SUBJECT, PRICE, FOLDERID, STORE,  COST, BRAND, CATEGORY, CONTENT, UNIT, PRODUCAREA, INDEX from product where deleted != true order by index");
            productRS.afterLast();
            productRS.relative(-1);
            tmpPos = productRS.getRow();
            dishNameMetrix = new String[3][tmpPos];
            dishAry = new Dish[tmpPos];
            productRS.beforeFirst();
            
            //compose the record into dish objects--------------
            tmpPos = 0;
            while (productRS.next()) { // @NOTE: don't load all the content, because menu can be many
                dishNameMetrix[0][tmpPos] = productRS.getString("CODE");
                dishNameMetrix[1][tmpPos] = productRS.getString("MNEMONIC");
                dishNameMetrix[2][tmpPos] = productRS.getString("SUBJECT");

                dishAry[tmpPos] = new Dish();
                dishAry[tmpPos].setId(productRS.getInt("ID"));
                dishAry[tmpPos].setLanguage(0, dishNameMetrix[0][tmpPos]);
                dishAry[tmpPos].setLanguage(1, dishNameMetrix[1][tmpPos]);
                dishAry[tmpPos].setLanguage(2, dishNameMetrix[2][tmpPos]);
                dishAry[tmpPos].setPrice(productRS.getInt("PRICE"));
                dishAry[tmpPos].setGst(productRS.getInt("FOLDERID"));
                dishAry[tmpPos].setQst(productRS.getInt("STORE"));
                dishAry[tmpPos].setSize(productRS.getInt("COST"));
                dishAry[tmpPos].setPrinter(productRS.getString("BRAND"));
                dishAry[tmpPos].setCATEGORY(productRS.getString("CATEGORY"));
                dishAry[tmpPos].setPrompPrice(productRS.getString("CONTENT"));
                dishAry[tmpPos].setPrompMenu(productRS.getString("UNIT"));
                dishAry[tmpPos].setPrompMofify(productRS.getString("PRODUCAREA"));
                dishAry[tmpPos].setDspIndex(productRS.getInt("INDEX"));
                tmpPos++;
            }
            productRS.close();// 关闭

        } catch (Exception e) {
            ErrorUtil.write(e);
        }
        reInitCategoryAndMenuBtns();
    }

    // menu and category buttons must be init after initContent---------
    private void reInitCategoryAndMenuBtns() {
        // validate rows and columns first(in case they are changed into bad value)--------
        categoryColumn = (categoryColumn == null || categoryColumn < 4) ? 5 : categoryColumn;
        categoryRow = (categoryRow == null || categoryRow < 1 || categoryRow > 9) ? 3 : categoryRow;
        categoryNumPerPage = categoryColumn * categoryRow;

        menuColumn = (menuColumn == null || menuColumn < 1) ? 4 : menuColumn;
        menuRow = (menuRow == null || menuRow < 1) ? 4 : menuRow;
        curMenuPerPage = menuColumn * menuRow;

        // clean current catogory and menus from both screen and metrix if have---------------
        for (int r = 0; r < categoryRow; r++) {
            if (r < onSrcCategoryTgbMatrix.size()) {
                for (int c = 0; c < categoryColumn; c++) {
                    if (c < onSrcCategoryTgbMatrix.get(r).size())
                        remove(onSrcCategoryTgbMatrix.get(r).get(c));
                }
            }
        }
        for (int r = 0; r < menuRow; r++) {
            if (r < onSrcMenuBtnMatrix.size()) {
                for (int c = 0; c < menuColumn; c++) {
                    if (c < onSrcMenuBtnMatrix.get(r).size())
                        remove(onSrcMenuBtnMatrix.get(r).get(c));
                }
            }
        }
        onSrcCategoryTgbMatrix.clear();
        onSrcMenuBtnMatrix.clear();

        // create new buttons and add onto the screen (no layout yet)------------
        int dspIndex = curCategoryPage * categoryNumPerPage;
        for (int r = 0; r < categoryRow; r++) {
            ArrayList<CategoryToggleButton> btnCategoryArry = new ArrayList<CategoryToggleButton>();
            for (int c = 0; c < categoryColumn; c++) {
                dspIndex++;
                CategoryToggleButton btnCategory = new CategoryToggleButton(dspIndex);
                btnCategory.setMargin(new Insets(0, 0, 0, 0));
                add(btnCategory);
                btnCategory.addActionListener(this);
                btnCategoryArry.add(btnCategory);
                if (dspIndex <= categoryNameMetrix[0].length) {
                    btnCategory.setText(categoryNameMetrix[CustOpts.custOps.getUserLang()][dspIndex - 1]);
                    if (tgbActiveCategory != null
                            && categoryNameMetrix[CustOpts.custOps.getUserLang()][dspIndex - 1].equalsIgnoreCase(tgbActiveCategory.getText())) {
                        btnCategory.setSelected(true);
                    }
                } else {
                    btnPageDownCategory.setEnabled(false);
                }
            }
            onSrcCategoryTgbMatrix.add(btnCategoryArry);
        }

        // if no activeCategory, use the first one on screen.
        if (tgbActiveCategory == null) {
            tgbActiveCategory = onSrcCategoryTgbMatrix.get(0).get(0);
            tgbActiveCategory.setSelected(true);
        }

        // initialize on screen menus===============================================================
        //find out menus matching to current category and current lang
        onScrDishNameMetrix = new String[3][dishNameMetrix[0].length];
        onScrDishAry = new Dish[dishNameMetrix[0].length];
        
        int onscrMenuIndex = 0;
        for (int i = 0; i < dishAry.length; i++) {
			if(dishAry[i].getCATEGORY().equals(tgbActiveCategory.getText())) {
				
				onScrDishNameMetrix[0][onscrMenuIndex] = dishNameMetrix[0][i];
				onScrDishNameMetrix[1][onscrMenuIndex] = dishNameMetrix[1][i];
				onScrDishNameMetrix[2][onscrMenuIndex] = dishNameMetrix[2][i];
				
				onScrDishAry[onscrMenuIndex] = dishAry[i];
				//make sure the display index are lined
				if(dishAry[i].getDspIndex() != onscrMenuIndex + 1) {
					try {
		                Connection conn = PIMDBModel.getConection();
		                Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			            StringBuilder sql = new StringBuilder("UPDATE product SET INDEX = ").append(onscrMenuIndex + 1)
			                            	.append(" where ID = ").append(dishAry[i].getId());
			            smt.executeUpdate(sql.toString());
			            smt.close();
		                smt = null;
		            }catch(Exception exp) {
		                exp.printStackTrace();
		            }
					dishAry[i].setDspIndex(onscrMenuIndex + 1);
				}
				
				onscrMenuIndex++;
			}
		}
        
        dspIndex = curMenuPageNum * curMenuPerPage;
        for (int r = 0; r < menuRow; r++) {
            ArrayList<MenuButton> btnMenuArry = new ArrayList<MenuButton>();
            for (int c = 0; c < menuColumn; c++) {
                MenuButton btnMenu = new MenuButton(dspIndex + 1);
                btnMenu.setMargin(new Insets(0, 0, 0, 0));
                add(btnMenu);
                btnMenu.addActionListener(this);
                btnMenuArry.add(btnMenu);
                if (dspIndex < onscrMenuIndex) {
                    btnMenu.setText(onScrDishNameMetrix[CustOpts.custOps.getUserLang()][dspIndex]);
                    btnMenu.setDish(onScrDishAry[dspIndex]);
                } else {
                    btnPageDownMenu.setEnabled(false);
                }

                dspIndex++;
            }
            onSrcMenuBtnMatrix.add(btnMenuArry);
        }
    }

    private void initTable() {
        Object[][] tValues = new Object[1][header.length];
        tblOrder.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblOrder.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
        tblOrder.getSelectionModel().addListSelectionListener(this);
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    private boolean isOnProcess() {
        return getUsedRowCount() > 0;
    }

    private int getUsedRowCount() {
        for (int i = 0, len = tblOrder.getRowCount(); i < len; i++)
            if (tblOrder.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblOrder.getRowCount();
    }

    private void resetAll() {
        resetListArea();
    }

    private void resetListArea() {
        int tRowCount = tblOrder.getRowCount();
        int tColCount = tblOrder.getColumnCount();
        for (int j = 0; j < tRowCount; j++)
            for (int i = 0; i < tColCount; i++)
                tblOrder.setValueAt(null, j, i);
    }

    private void enableBtns(
            boolean pIsEnable) {
    }

    private JLabel lblOperator;
    private JLabel lblShoestring;
    private JLabel lblStartTime;

    private JLabel lblSubTotle;
    private JLabel lblGSQ;
    private JLabel lblQSQ;
    private JLabel lblTotlePrice;

    static JLabel lblStatus;

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

    private ArrayButton btnPageUpTable;
    private ArrayButton btnPageDownTable;
    private ArrayButton btnPageUpCategory;
    private ArrayButton btnPageDownCategory;
    private ArrayButton btnPageUpMenu;
    private ArrayButton btnPageDownMenu;

    PIMTable tblOrder;
    private PIMScrollPane srpContent;
    private String[] header = new String[] { BarDlgConst.ProdNumber, BarDlgConst.ProdName, BarDlgConst.Size, BarDlgConst.Count,
            BarDlgConst.Price};
    
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
}
