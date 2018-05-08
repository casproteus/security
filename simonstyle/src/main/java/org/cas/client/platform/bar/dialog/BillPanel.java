package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.bar.beans.ArrayButton;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pimview.pimtable.PIMTableRenderAgent;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PaneConsts;

public class BillPanel extends JPanel implements ActionListener, ComponentListener, PIMTableRenderAgent, ListSelectionListener, MouseMotionListener, MouseListener{
	
	SalesPanel salesPanel;
	BillListPanel billListDlg;
	JToggleButton billButton;
    private boolean isDragging;
    
	public BillPanel(SalesPanel salesPanel) {
		this.salesPanel = salesPanel;
		initComponent();
	}

	public BillPanel(BillListPanel billListDlg, JToggleButton billButton) {
		this.billListDlg = billListDlg;
		this.billButton = billButton;
	}
	
    ArrayList<Dish> selectdDishAry = new ArrayList<Dish>();
	
    @Override
	public void actionPerformed(ActionEvent e) {

        Object o = e.getSource();
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
	        } else if(o == btnMore) {
	        	int selectedRow =  tblSelectedDish.getSelectedRow();
				if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {	//already saved
					addContentToList(selectdDishAry.get(selectedRow));
					tblSelectedDish.setSelectedRow(selectdDishAry.size() - 1);
				}else {
					int tQTY = selectdDishAry.get(selectedRow).getNum() + 1;
					int row = tblSelectedDish.getSelectedRow();
					tblSelectedDish.setValueAt("x" + tQTY, row, 3);
					selectdDishAry.get(row).setNum(tQTY);
				}
				updateTotleArea();
				tblSelectedDish.setSelectedRow(selectedRow);
	        } else if (o == btnLess) {
	    		int selectedRow =  tblSelectedDish.getSelectedRow();
				if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {
					if (JOptionPane.showConfirmDialog(this, BarDlgConst.COMFIRMDELETEACTION, DlgConst.DlgTitle,
		                    JOptionPane.YES_NO_OPTION) != 0) {
						return;
					}
				}
				
				if(selectdDishAry.get(selectedRow).getNum()== 1) {
					if (JOptionPane.showConfirmDialog(this, BarDlgConst.COMFIRMDELETEACTION2, DlgConst.DlgTitle,
		                    JOptionPane.YES_NO_OPTION) != 0) {// 确定删除吗？
						tblSelectedDish.setSelectedRow(-1);
						return;
					}
					removeAtSelection(selectedRow);
				} else {
					int tQTY = selectdDishAry.get(selectedRow).getNum() - 1;
					int row = tblSelectedDish.getSelectedRow();
					tblSelectedDish.setValueAt("x" + tQTY, row, 3);
					selectdDishAry.get(row).setNum(tQTY);
				}

				updateTotleArea();
	        }
        }
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
        reLayout();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
    void resetTableArea() {
    	selectdDishAry.clear();
        Object[][] tValues = new Object[0][tblSelectedDish.getColumnCount()];
        tblSelectedDish.setDataVector(tValues, header);
        resetColWidth(srpContent.getWidth());
        updateTotleArea();
    }
    
    //table selection listener---------------------
	@Override
	public void valueChanged(ListSelectionEvent e) {
		DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)e.getSource();
		int selectedRow =  selectionModel.getMinSelectionIndex();
		btnMore.setEnabled(selectedRow >= 0 && selectedRow <= selectdDishAry.size());
		btnLess.setEnabled(selectedRow >= 0 && selectedRow <= selectdDishAry.size());
		if(!btnMore.isEnabled()) {
			return;
		}
		
		if(salesPanel != null && salesPanel.btnLine_1_7.isSelected()) {	//if qty button seleted.
			Object obj = tblSelectedDish.getValueAt(selectedRow,3);
			//update the qty in qtyDlg.
			if(obj != null)
				BarFrame.instance.numberPanelDlg.setContents(obj.toString());
		}else if(billListDlg != null) {
			Dish selectedDish = selectdDishAry.get(selectedRow);
 			if(billListDlg.curDish != null && billListDlg.curBillButton != billButton) {
				billListDlg.moveDishToBill(this);
				billListDlg.curDish = null;
			}else {
				billListDlg.curDish = selectedDish;
				billListDlg.curBillButton = billButton;
			}
		}
	}

	//table row color----------------------------------------------------------
	@Override
	public Color getBackgroundAtRow(int row) {
		if(selectdDishAry.size() > row) {
			Dish dish = selectdDishAry.get(row);
			if(dish.getOutputID() > -1) {
				return new Color(222, 111, 34);
			}else {
				return new Color(150, 150, 150);
			}
		}else
			return null;
	}

	@Override
	public Color getForegroundAtRow(int row) {
		return null;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(isDragging == true) {
			isDragging = false;
			ListSelectionModel selectionModel = ((PIMTable)e.getSource()).getSelectionModel();
			int selectedRow =  selectionModel.getMinSelectionIndex();
			if(selectedRow < 0 || selectedRow >= selectdDishAry.size()) 
				return;
			
			if(salesPanel != null && !salesPanel.btnLine_1_7.isSelected()) {	//if qty button not seleted.
				if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {
					if (JOptionPane.showConfirmDialog(BarFrame.instance, BarDlgConst.COMFIRMDELETEACTION, DlgConst.DlgTitle,
		                    JOptionPane.YES_NO_OPTION) != 0) {// 确定删除吗？
						tblSelectedDish.setSelectedRow(-1);
						return;
					}
				}
				removeAtSelection(selectedRow);
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == srpContent.getViewport()) {
			if(billListDlg != null) {
	 			if(billListDlg.curDish != null && billListDlg.curBillButton != billButton) {
					billListDlg.moveDishToBill(this);
					billListDlg.curDish = null;
				}else {
					billButton.setSelected(!billButton.isSelected());
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		isDragging = true;
	}
	@Override
	public void mouseMoved(MouseEvent e) {}
	
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
        
        tblSelectedDish.setSelectedRow(tValidRowCount);	//@NOTE:must before adding into the array, so it can be ignored by 
        
        Dish newDish = dish.clone();		//@NOTE: incase the cloned dish contains outpurID properties.
        newDish.setOutputID(-1);
        selectdDishAry.add(newDish);				//valueChanged process. not being cleared immediately-----while now dosn't matter
        updateTotleArea();								//because value change will not be used to remove the record.
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
    
    void initTable() {
    	selectdDishAry.clear();
    	//get outputs of current table and bill id.
		try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
			String billID = billButton == null ? BarFrame.instance.lblCurBill.getText() : billButton.getText();
			String sql = "select * from OUTPUT, PRODUCT where OUTPUT.SUBJECT = '" + BarFrame.instance.valCurTable.getText()
					+ "' and CONTACTID = " + billID + " and deleted = false AND OUTPUT.PRODUCTID = PRODUCT.ID";
			ResultSet rs = smt.executeQuery(sql);
			rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();

			int tColCount = tblSelectedDish.getColumnCount();
			Object[][] tValues = new Object[tmpPos][tColCount];
			rs.beforeFirst();
			tmpPos = 0;
			while (rs.next()) {
				Dish dish = new Dish();
				dish.setCATEGORY(rs.getString("PRODUCT.CATEGORY"));
				dish.setDiscount(rs.getInt("OUTPUT.discount"));//
				dish.setDspIndex(rs.getInt("PRODUCT.INDEX"));
				dish.setGst(rs.getInt("PRODUCT.FOLDERID"));
				dish.setId(rs.getInt("PRODUCT.ID"));
				dish.setLanguage(0, rs.getString("PRODUCT.CODE"));
				dish.setLanguage(1, rs.getString("PRODUCT.MNEMONIC"));
				dish.setLanguage(2, rs.getString("PRODUCT.SUBJECT"));
				dish.setModification(rs.getString("OUTPUT.CONTENT"));//
				dish.setNum(rs.getInt("OUTPUT.AMOUNT"));//
				dish.setOutputID(rs.getInt("OUTPUT.ID"));//
				dish.setPrice(rs.getInt("PRODUCT.PRICE"));
				dish.setPrinter(rs.getString("PRODUCT.BRAND"));
				dish.setPrompMenu(rs.getString("PRODUCT.UNIT"));
				dish.setPrompMofify(rs.getString("PRODUCT.PRODUCAREA"));
				dish.setPrompPrice(rs.getString("PRODUCT.CONTENT"));
				dish.setQst(rs.getInt("PRODUCT.STORE"));
				dish.setSize(rs.getInt("PRODUCT.COST"));
				selectdDishAry.add(dish);

				tValues[tmpPos][0] = tmpPos + 1;
				tValues[tmpPos][1] = dish.getLanguage(LoginDlg.USERLANG);
				tValues[tmpPos][3] = "x" + rs.getInt("AMOUNT");
				tValues[tmpPos][4] = rs.getInt("TOLTALPRICE") / 100f;
				tmpPos++;
			}

			tblSelectedDish.setDataVector(tValues, header);
			// do not set the default selected value, if it's used in billListDlg.
			if (salesPanel != null)
				tblSelectedDish.setSelectedRow(tmpPos - 1);
			rs.close();
		} catch (Exception e) {
			ErrorUtil.write(e);
		}

		resetColWidth(srpContent.getWidth());
		updateTotleArea();
	}
    
    void resetColWidth(int tableWidth) {
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
        int width = tableWidth - tmpCol1.getWidth() - tmpCol3.getWidth() - tmpCol4.getWidth() - tmpCol5.getWidth() - 3;
        width -= srpContent.getVerticalScrollBar().isVisible() ? srpContent.getVerticalScrollBar().getWidth() : 0;
        tmpCol2.setWidth(width);
        tmpCol2.setPreferredWidth(width);
        
        tblSelectedDish.validate();
        tblSelectedDish.revalidate();
        tblSelectedDish.invalidate();
    }

    private void removeAtSelection(int selectedRow) {
    	//update db first
    	Dish dish = selectdDishAry.get(selectedRow);
    	if(dish.getOutputID() > -1) {
    		Dish.delete(dish);
    	}
    	//update array second.
		selectdDishAry.remove(selectedRow);
		//update the table view
		int tColCount = tblSelectedDish.getColumnCount();
		int tValidRowCount = getUsedRowCount(); // get the used RowCount
		Object[][] tValues = new Object[tValidRowCount - 1][tColCount];
		for (int r = 0; r < tValidRowCount; r++) {
			if(r == selectedRow) {
				continue;
			}else {
				int rowNum = r > selectedRow ? r : r + 1;
			    for (int c = 0; c < tColCount; c++)
			        tValues[rowNum-1][c] = c == 0 ? rowNum: tblSelectedDish.getValueAt(r, c);
			}
		}
		tblSelectedDish.setDataVector(tValues, header);
		resetColWidth(srpContent.getWidth());
		tblSelectedDish.setSelectedRow(tValues.length - 1);
		updateTotleArea();
	}
    
    private void resetAll() {
        resetListArea();
    }
    
    private int getUsedRowCount() {
        for (int i = 0, len = tblSelectedDish.getRowCount(); i < len; i++)
            if (tblSelectedDish.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblSelectedDish.getRowCount();
    }
    
    private void resetListArea() {
        int tRowCount = tblSelectedDish.getRowCount();
        int tColCount = tblSelectedDish.getColumnCount();
        for (int j = 0; j < tRowCount; j++)
            for (int i = 0; i < tColCount; i++)
                tblSelectedDish.setValueAt(null, j, i);
    }
    
    void reLayout() {
        int panelHeight = getHeight();

        int tBtnWidht = (getWidth() - CustOpts.HOR_GAP * 10) / 9;
        int tBtnHeight = panelHeight / 10;
        
     // table area-------------
        int poxX = 0;
        int posY = 0;
        if(billButton != null) {
        	billButton.setBounds(poxX, posY, getWidth() - BarDlgConst.SCROLLBAR_WIDTH, CustOpts.BTN_HEIGHT + 16);
        	posY += billButton.getHeight();
        }
        srpContent.setBounds(poxX, posY, getWidth() - BarDlgConst.SCROLLBAR_WIDTH, getHeight() - BarDlgConst.SubTotal_HEIGHT);
        
        btnMore.setBounds(srpContent.getWidth(), srpContent.getY(), 
        		BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH);
        btnLess.setBounds(btnMore.getX(), btnMore.getY() + btnMore.getHeight() + CustOpts.VER_GAP, 
        		BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH);

        btnPageUpTable.setBounds(btnLess.getX(), srpContent.getY() + srpContent.getHeight()
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

		lblTotlePrice.setBounds(lblSubTotle.getX(), getHeight() - CustOpts.BTN_HEIGHT, lblSubTotle.getWidth(), BarDlgConst.SubTotal_HEIGHT * 2 / 3);
    }
    
    void initComponent() {

        tblSelectedDish = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblSelectedDish);
        lblSubTotle = new JLabel(BarDlgConst.Subtotal);
        lblGSQ = new JLabel(BarDlgConst.QST);
        lblQSQ = new JLabel(BarDlgConst.GST);
        lblTotlePrice = new JLabel(BarDlgConst.Total);
        btnMore = new ArrayButton("+");
        btnLess = new ArrayButton("-");
        btnPageUpTable = new ArrayButton("↑");
        btnPageDownTable = new ArrayButton("↓");
        
        setLayout(null);
        tblSelectedDish.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblSelectedDish.setAutoscrolls(true);
        tblSelectedDish.setRowHeight(30);
        tblSelectedDish.setCellEditable(false);
        tblSelectedDish.setRenderAgent(this);
        tblSelectedDish.setHasSorter(false);
        tblSelectedDish.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        tblSelectedDish.setDataVector(new Object[1][header.length], header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblSelectedDish.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);

        // Margin-----------------
        btnPageUpTable.setMargin(new Insets(0,0,0,0));
        btnPageDownTable.setMargin(btnPageUpTable.getInsets());
        btnMore.setMargin(btnPageUpTable.getInsets());
        btnLess.setMargin(btnPageUpTable.getInsets());
        
        // border----------
        tblSelectedDish.setBorder(null);
        // forcus-------------
        tblSelectedDish.setFocusable(false);

        // disables
        btnPageUpTable.setEnabled(false);
        btnMore.setEnabled(false);
        btnLess.setEnabled(false);
        
        // built
        if(billButton != null) {
        	add(billButton);
        }else {
            add(btnMore);
            add(btnLess);
            add(lblSubTotle);
            add(lblGSQ);
            add(lblQSQ);
        }
        add(btnPageUpTable);
        add(btnPageDownTable);
        add(lblTotlePrice);
        add(srpContent);

        addComponentListener(this);
        btnMore.addActionListener(this);
        btnLess.addActionListener(this);
        btnPageUpTable.addActionListener(this);
        btnPageDownTable.addActionListener(this);
        tblSelectedDish.addMouseMotionListener(this);
        tblSelectedDish.addMouseListener(this);
        tblSelectedDish.getSelectionModel().addListSelectionListener(this);
        srpContent.getViewport().addMouseListener(this);
    }
    
    PIMTable tblSelectedDish;
    private PIMScrollPane srpContent;

    private JLabel lblSubTotle;
    private JLabel lblGSQ;
    private JLabel lblQSQ;
    private JLabel lblTotlePrice;
    
    private ArrayButton btnMore;
    private ArrayButton btnLess;
    private ArrayButton btnPageUpTable;
    private ArrayButton btnPageDownTable;
    
    String[] header = new String[] { BarDlgConst.ProdNumber, BarDlgConst.ProdName, BarDlgConst.Size, BarDlgConst.Count,
            BarDlgConst.Price};

}
