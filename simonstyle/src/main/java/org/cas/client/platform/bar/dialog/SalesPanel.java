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

//Identity表应该和Employ表合并。
public class SalesPanel extends JPanel implements ComponentListener, ActionListener, FocusListener, ListSelectionListener, PIMTableRenderAgent {
    
    private boolean isDragging;
    int curBillID;
    
    String[][] categoryNameMetrix;
    ArrayList<ArrayList<CategoryToggleButton>> onSrcCategoryTgbMatrix = new ArrayList<ArrayList<CategoryToggleButton>>();
    CategoryToggleButton tgbActiveCategory;
    
    //Dish is more complecated than category, it's devided by category first, then divided by page.
    String[][] dishNameMetrix;// the struction must be [3][index]. it's more convenient than [index][3]
    String[][] onScrDishNameMetrix;// it's sub set of all menuNameMetrix
    private ArrayList<ArrayList<MenuButton>> onSrcMenuBtnMatrix = new ArrayList<ArrayList<MenuButton>>();
    
    private Dish[] dishAry;
    private Dish[] onScrDishAry;
    ArrayList<Dish> selectdDishAry = new ArrayList<Dish>();
    

    //flags
    NumberPanelDlg numberPanelDlg; 
    
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
	        }
        }
        
        //JButton------------------------------------------------------------------------------------------------
        else if (o instanceof JButton) {
        	if (o == btnLine_1_1) { // cancel all
        		selectdDishAry.clear();
    	        Object[][] tValues = new Object[0][tblSelectedDish.getColumnCount()];
                tblSelectedDish.setDataVector(tValues, header);
                resetColWidth(srpContent.getWidth());
    	        updateTotleArea();
            } else if (o == btnLine_1_7) { // cancel all
        		
            } else if (o == btnLine_1_9) {//send
            	//send to printer
            	//prepare the printing String
            	WifiPrintService.exePrintCommand(selectdDishAry, BarFrame.curTable);
            	//save to db output
                try {
                    Connection conn = PIMDBModel.getConection();
                    Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    for (Dish dish : selectdDishAry) {
                    	if(dish.getOutputID() > -1)	//if it's already saved into db, don't ignore.
                    		continue;
                    	
                    	String time = new Date().toLocaleString();
	                    StringBuilder sql = new StringBuilder(
	                        "INSERT INTO output(SUBJECT, CONTACTID, PRODUCTID, AMOUNT, TOLTALPRICE, DISCOUNT, CONTENT, EMPLOYEEID, TIME) VALUES ('")
	                        .append(BarFrame.curTable).append("', ")	//subject ->table id
	                        .append(curBillID).append(", ")			//contactID ->bill id
	                        .append(dish.getId()).append(", ")	//productid
	                        .append(dish.getNum()).append(", ")	//amount
	                        .append((dish.getPrice() - dish.getDiscount()) * dish.getNum()).append(", ")	//totalprice int
	                        .append(dish.getDiscount() * dish.getNum()).append(", '")	//discount
	                        .append(dish.getModification()).append("', ")				//content
	                        .append(LoginDlg.USERID).append(", '")		//emoployid
	                        .append(time).append("') ");
	                    smt.executeUpdate(sql.toString());
                    
	                    sql = new StringBuilder("Select id from output where SUBJECT = '")
	                        .append(BarFrame.curTable).append("' and CONTACTID = ")
	                        .append(curBillID).append(" and PRODUCTID = ")
	                        .append(dish.getId()).append(" and AMOUNT = ")
	                        .append(dish.getNum()).append(" and TOLTALPRICE = ")
	                        .append((dish.getPrice() - dish.getDiscount()) * dish.getNum()).append(" and DISCOUNT = ")
	                        .append(dish.getDiscount() * dish.getNum()).append(" and EMPLOYEEID = ")
	                        .append(LoginDlg.USERID).append(" and TIME = '")
	                        .append(time).append("'");
	                    ResultSet rs = smt.executeQuery(sql.toString());
	                    rs.beforeFirst();
	                    rs.next();
	                    dish.setOutputID(rs.getInt("id"));
	                    rs.close();
                    }
                    smt.close();
                    smt = null;
                    tblSelectedDish.repaint();
                }catch(Exception exp) {
                	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                    exp.printStackTrace();
                }
            }else if (o == btnLine_2_4) { // cancel all
        		selectdDishAry.clear();
    	        Object[][] tValues = new Object[0][tblSelectedDish.getColumnCount()];
                tblSelectedDish.setDataVector(tValues, header);
                resetColWidth(srpContent.getWidth());
    	        updateTotleArea();
            } else if (o == btnLine_2_5) { // cancel all
        		selectdDishAry.clear();
    	        Object[][] tValues = new Object[0][tblSelectedDish.getColumnCount()];
                tblSelectedDish.setDataVector(tValues, header);
                resetColWidth(srpContent.getWidth());
    	        updateTotleArea();
    	        //TODO update db, delete relevant orders.
            } else if (o == btnLine_2_6) { // enter the setting mode.(admin interface)
                BarFrame.instance.switchMode(2);
            } else if (o == btnLine_2_8) {//more
            	new MoreButtonsDlg(this).show((JButton)o);
            } else if (o == btnLine_2_9) { // return
            	if(selectdDishAry.size() > 0) {
	            	Dish dish = selectdDishAry.get(selectdDishAry.size() - 1);
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
        		if(numberPanelDlg == null) {
        			numberPanelDlg = new NumberPanelDlg(BarFrame.instance, btnLine_1_7);
        		}
        		numberPanelDlg.setVisible(btnLine_1_7.isSelected());	//@NOTE: it's not model mode.
        		if(tblSelectedDish.getSelectedRow() > 0) {
        			Object obj = tblSelectedDish.getValueAt(tblSelectedDish.getSelectedRow(), 3);
        			numberPanelDlg.setContents(obj.toString());
        		}else {
        			tblSelectedDish.setSelectedRow(tblSelectedDish.getRowCount()-1);
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
			//base on the design change, don't use touch to delete for now.
//			if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {
//				if (JOptionPane.showConfirmDialog(this, BarDlgConst.COMFIRMDELETEACTION, DlgConst.DlgTitle,
//	                    JOptionPane.YES_NO_OPTION) != 0) {// 确定删除吗？
//					tblOrder.setSelectedRow(-1);
//					return;
//				}
//			}
//			selectdDishAry.remove(selectedRow);
//			
//	        int tColCount = tblOrder.getColumnCount();
//	        int tValidRowCount = getUsedRowCount(); // get the used RowCount
//	        Object[][] tValues = new Object[tValidRowCount - 1][tColCount];
//            for (int r = 0; r < tValidRowCount; r++) {
//            	int rowNum = r + 1;
//            	if(r == selectedRow) 
//            		continue;
//            	else if(r > selectedRow)
//            		rowNum--;
//            	
//                for (int c = 0; c < tColCount; c++)
//                    tValues[rowNum-1][c] = c == 0 ? rowNum: tblOrder.getValueAt(r, c);
//            }
//            tblOrder.setDataVector(tValues, header);
//            resetColWidth(srpContent.getWidth());
//	
//	        updateTotleArea();
		}else {
			Object obj = tblSelectedDish.getValueAt(selectedRow,3);
			//update the qty in qtyDlg.
			if(obj != null)
				numberPanelDlg.setContents(obj.toString());
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
				return new Color(150, 150, 150);
			}
		}else
			return null;
	}

	@Override
	public Color getForegroundAtRow(int row) {
		// TODO Auto-generated method stub
		return null;
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
        // table area-------------
        Double tableWidth = (Double) CustOpts.custOps.hash2.get("TableWidth");
        tableWidth = (tableWidth == null || tableWidth < 0.2) ? 0.4 : tableWidth;
        srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
                (int) (getWidth() * tableWidth) - BarDlgConst.SCROLLBAR_WIDTH, topAreaHeight
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

        // menu area--------------
        int xMenuArea = srpContent.getX() + srpContent.getWidth() + CustOpts.HOR_GAP + BarDlgConst.SCROLLBAR_WIDTH;
        int widthMenuArea =
                (getWidth() - srpContent.getWidth() - CustOpts.HOR_GAP * 2) - BarDlgConst.SCROLLBAR_WIDTH;

        BarFrame.instance.menuPanel.setBounds(xMenuArea, srpContent.getY(), widthMenuArea, topAreaHeight);
        BarFrame.instance.menuPanel.reLayout();

        resetColWidth(srpContent.getWidth());
    }

    private void resetColWidth(int tableWidth) {
        PIMTableColumn tmpCol1 = tblSelectedDish.getColumnModel().getColumn(0);
        tmpCol1.setWidth(40);
        tmpCol1.setPreferredWidth(40);
        PIMTableColumn tmpCol3 = tblSelectedDish.getColumnModel().getColumn(2);
        tmpCol3.setWidth(40);
        tmpCol3.setPreferredWidth(40);
        PIMTableColumn tmpCol4 = tblSelectedDish.getColumnModel().getColumn(3);
        tmpCol4.setWidth(40);
        tmpCol4.setPreferredWidth(40);
        PIMTableColumn tmpCol5 = tblSelectedDish.getColumnModel().getColumn(4);
        tmpCol5.setWidth(40);
        tmpCol5.setPreferredWidth(40);

        PIMTableColumn tmpCol2 = tblSelectedDish.getColumnModel().getColumn(1);
        tmpCol2.setWidth(tableWidth - tmpCol1.getWidth() - tmpCol3.getWidth() - tmpCol4.getWidth() - tmpCol5.getWidth() - 3);
        tmpCol2.setPreferredWidth(tmpCol2.getWidth());
        
        tblSelectedDish.validate();
        tblSelectedDish.revalidate();
        tblSelectedDish.invalidate();
    }

    // 将对话盒区域的内容加入到列表
    void addContentToList(Dish dish) {
        int tRowCount = tblSelectedDish.getRowCount(); // add content to the table.
        int tColCount = tblSelectedDish.getColumnCount();
        int tValidRowCount = getUsedRowCount(); // get the used RowCount
        if (tRowCount == tValidRowCount) { // no line is empty, add a new Line.
            Object[][] tValues = new Object[tRowCount + 1][tColCount];
            for (int r = 0; r < tRowCount; r++)
                for (int c = 0; c < tColCount; c++)
                    tValues[r][c] = tblSelectedDish.getValueAt(r, c);
            tblSelectedDish.setDataVector(tValues, header);
            resetColWidth(srpContent.getWidth());
        }else {
        	tRowCount--;
        }
        tblSelectedDish.setValueAt(tRowCount + 1, tValidRowCount, 0); // set the code.
        tblSelectedDish.setValueAt(dish.getLanguage(CustOpts.custOps.getUserLang()), tValidRowCount, 1);// set the Name.
        tblSelectedDish.setValueAt(dish.getSize() > 1 ? dish.getSize() : "", tValidRowCount, 2); // set the count.
        tblSelectedDish.setValueAt("x1", tValidRowCount, 3); // set the count.
        tblSelectedDish.setValueAt(dish.getPrice()/100f, tValidRowCount, 4); // set the price.
        
        //tblOrder.setSelectedRow(tValidRowCount);	//@NOTE:must before adding into the array, so it can be ignored by 
        selectdDishAry.add(dish.clone());					//valueChanged process. not being cleared immediately
        
        updateTotleArea();
    }

    void updateTotleArea() {
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
        
        btnPageUpTable = new ArrayButton("↑");
        btnPageDownTable = new ArrayButton("↓");

        tblSelectedDish = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblSelectedDish);

        // properties
        setLayout(null);
        tblSelectedDish.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblSelectedDish.setAutoscrolls(true);
        tblSelectedDish.setRowHeight(30);
        tblSelectedDish.setCellEditable(false);
        tblSelectedDish.setRenderAgent(this);
        tblSelectedDish.setHasSorter(false);
        tblSelectedDish.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);

        // Margin-----------------
        btnPageUpTable.setMargin(new Insets(0,0,0,0));
        btnPageDownTable.setMargin(btnPageUpTable.getInsets());

        // border----------
        tblSelectedDish.setBorder(null);
        // forcus-------------
        tblSelectedDish.setFocusable(false);

        // disables
        btnPageUpTable.setEnabled(false);

        // built
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

        add(btnPageUpTable);
        add(btnPageDownTable);

        add(srpContent);

        // add listener
        addComponentListener(this);

        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        // tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）
        btnPageUpTable.addActionListener(this);
        btnPageDownTable.addActionListener(this);

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
        
        tblSelectedDish.addMouseMotionListener(new MouseMotionListener(){
        	public void mouseDragged(MouseEvent e) {
        		isDragging = true;
        	}
        	public void mouseMoved(MouseEvent e) {}
        });
        tblSelectedDish.addMouseListener(new MouseListener(){
			@Override
			public void mouseReleased(MouseEvent e) {
				if(isDragging == true) {
					isDragging = false;
					ListSelectionModel selectionModel = ((PIMTable)e.getSource()).getSelectionModel();
					int selectedRow =  selectionModel.getMinSelectionIndex();
					if(selectedRow < 0 || selectedRow >= selectdDishAry.size()) 
						return;
					
					if(!btnLine_1_7.isSelected()) {	//if qty button not seleted.
						if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {
							if (JOptionPane.showConfirmDialog(BarFrame.instance, BarDlgConst.COMFIRMDELETEACTION, DlgConst.DlgTitle,
				                    JOptionPane.YES_NO_OPTION) != 0) {// 确定删除吗？
								tblSelectedDish.setSelectedRow(-1);
								return;
							}
						}
						selectdDishAry.remove(selectedRow);
						
				        int tColCount = tblSelectedDish.getColumnCount();
				        int tValidRowCount = getUsedRowCount(); // get the used RowCount
				        Object[][] tValues = new Object[tValidRowCount - 1][tColCount];
			            for (int r = 0; r < tValidRowCount; r++) {
			            	int rowNum = r + 1;
			            	if(r == selectedRow) 
			            		continue;
			            	else if(r > selectedRow)
			            		rowNum--;
			            	
			                for (int c = 0; c < tColCount; c++)
			                    tValues[rowNum-1][c] = c == 0 ? rowNum: tblSelectedDish.getValueAt(r, c);
			            }
			            tblSelectedDish.setDataVector(tValues, header);
			            resetColWidth(srpContent.getWidth());
				
				        updateTotleArea();
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {			
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
        // initContents--------------
        initTable();
    }

    private void initTable() {
        Object[][] tValues = new Object[1][header.length];
        tblSelectedDish.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblSelectedDish.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
        tblSelectedDish.getSelectionModel().addListSelectionListener(this);
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    private boolean isOnProcess() {
        return getUsedRowCount() > 0;
    }

    private int getUsedRowCount() {
        for (int i = 0, len = tblSelectedDish.getRowCount(); i < len; i++)
            if (tblSelectedDish.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblSelectedDish.getRowCount();
    }

    private void resetAll() {
        resetListArea();
    }

    private void resetListArea() {
        int tRowCount = tblSelectedDish.getRowCount();
        int tColCount = tblSelectedDish.getColumnCount();
        for (int j = 0; j < tRowCount; j++)
            for (int i = 0; i < tColCount; i++)
                tblSelectedDish.setValueAt(null, j, i);
    }

    private void enableBtns(
            boolean pIsEnable) {
    }

    private JLabel lblSubTotle;
    private JLabel lblGSQ;
    private JLabel lblQSQ;
    private JLabel lblTotlePrice;

    private JButton btnLine_1_1;
    private JButton btnLine_1_2;
    private JButton btnLine_1_3;
    private JToggleButton btnLine_1_4;
    private JToggleButton btnLine_1_5;
    private JButton btnLine_1_6;
    private JToggleButton btnLine_1_7;
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

    private ArrayButton btnPageUpTable;
    private ArrayButton btnPageDownTable;
    
    PIMTable tblSelectedDish;
    private PIMScrollPane srpContent;
    private String[] header = new String[] { BarDlgConst.ProdNumber, BarDlgConst.ProdName, BarDlgConst.Size, BarDlgConst.Count,
            BarDlgConst.Price};
}
