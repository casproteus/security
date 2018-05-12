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
import javax.swing.SwingUtilities;
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
	BillListPanel billListPanel;
	JToggleButton billButton;
    private boolean isDragging;
    ArrayList<Dish> selectdDishAry = new ArrayList<Dish>();
    
	public BillPanel(SalesPanel salesPanel) {
		this.salesPanel = salesPanel;
		initComponent();
	}

	public BillPanel(BillListPanel billListDlg, JToggleButton billButton) {
		this.billListPanel = billListDlg;
		this.billButton = billButton;
	}
	
    @Override
	public void actionPerformed(ActionEvent e) {

        Object o = e.getSource();
		if(o instanceof ArrayButton) {
	        if(o == btnMore) {
	        	int selectedRow =  tblSelectedDish.getSelectedRow();
				if(selectdDishAry.get(selectedRow).getOutputID() >= 0) {	//already saved
					BarFrame.setStatusMes(BarDlgConst.SendItemCanNotModify);
					addContentToList(selectdDishAry.get(selectedRow));
				}else {
					int tQTY = selectdDishAry.get(selectedRow).getNum() + 1;
					int row = tblSelectedDish.getSelectedRow();
					selectdDishAry.get(row).setNum(tQTY);
					tblSelectedDish.setValueAt("x" + tQTY % BarOption.MaxQTY, row, 0);
					tblSelectedDish.setValueAt((selectdDishAry.get(selectedRow).getPrice() - selectdDishAry.get(selectedRow).getDiscount()) * tQTY/100f, row, 3);
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
					//TODO: send a message to kitchen.
					
				}else {
					if(selectdDishAry.get(selectedRow).getNum() == 1) {
						if (JOptionPane.showConfirmDialog(this, BarDlgConst.COMFIRMDELETEACTION2, DlgConst.DlgTitle,
			                    JOptionPane.YES_NO_OPTION) != 0) {// 确定删除吗？
							tblSelectedDish.setSelectedRow(-1);
							return;
						}
						removeAtSelection(selectedRow);
					} else {
						int tQTY = selectdDishAry.get(selectedRow).getNum() - 1;
						int row = tblSelectedDish.getSelectedRow();
						selectdDishAry.get(row).setNum(tQTY);
						tblSelectedDish.setValueAt("x" + tQTY, row, 0);		
						tblSelectedDish.setValueAt((selectdDishAry.get(selectedRow).getPrice() - selectdDishAry.get(selectedRow).getDiscount()) * tQTY/100f, row, 3);
					}
				}
				updateTotleArea();
	        }
        }else if(o == billButton){		//when bill button on top are clicked.
        	if(billListPanel != null && billListPanel.btnSplitItem.isSelected()) {
        		billButton.setSelected(!billButton.isSelected());
        		return;
        	}
        	
    		BarFrame.instance.lblCurBill.setText(((JToggleButton)o).getText());
            BarFrame.instance.switchMode(2);
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
        resetColWidth(scrContent.getWidth());
        updateTotleArea();
    }
    
    //table selection listener---------------------
	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow =  tblSelectedDish.getSelectedRow();
		btnMore.setEnabled(selectedRow >= 0 && selectedRow <= selectdDishAry.size());
		btnLess.setEnabled(selectedRow >= 0 && selectedRow <= selectdDishAry.size());
		if(!btnMore.isEnabled()) {	//some time the selectedRow can be -1.
			BillListPanel.curDish = null;
			return;
		}

		Dish selectedDish = selectdDishAry.get(selectedRow);
		if(salesPanel != null) {
			BillListPanel.curDish = selectedDish;
			if( salesPanel.btnLine_1_7.isSelected()) {	//if qty button seleted.
				Object obj = tblSelectedDish.getValueAt(selectedRow,3);
				//update the qty in qtyDlg.
				if(obj != null)
					BarFrame.numberPanelDlg.setContents(obj.toString());
			}
		}else if(billListPanel != null) {
			if(billListPanel.btnSplitItem.isSelected()) {	//if in splite item mode, then do nothing but select the bill button.
				billButton.setSelected(!billButton.isSelected());
				return;
			}
			
 			if(BillListPanel.curDish != null && billListPanel.getCurBillPanel() != null && billListPanel.getCurBillPanel() != this) {
				billListPanel.moveDishToBill(this);
				BillListPanel.curDish = null;
			}else {
				billButton.setSelected(true);
				BillListPanel.curDish = selectedDish;
			}
		}
	}

	//table row color----------------------------------------------------------
	@Override
	public Color getBackgroundAtRow(int row) {
		return null;
	}

	@Override
	public Color getForegroundAtRow(int row) {
		if(row < selectdDishAry.size()) {
			Dish dish = selectdDishAry.get(row);
			if(dish.getOutputID() > -1) {
				return Color.BLACK;
			}else {
				return Color.RED;
			}
		}else
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
		if(e.getSource() == scrContent.getViewport()) {
			if(billListPanel != null) {
				if(billListPanel.btnSplitItem.isSelected()) {	//if in splite item mode, then do nothing but select the bill button.
					billButton.setSelected(!billButton.isSelected());
					return;
				}
				
	 			if(billListPanel.curDish != null && billListPanel.getCurBillPanel() != this) {
					billListPanel.moveDishToBill(this);
					billListPanel.curDish = null;
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
            resetColWidth(scrContent.getWidth());
        }else {
        	tRowCount--;
        }
        
        Dish newDish = dish.clone();		//@NOTE: incase the cloned dish contains outpurID properties.
        newDish.setOutputID(-1);
        newDish.setNum(1);
        selectdDishAry.add(newDish);				//valueChanged process. not being cleared immediately-----while now dosn't matter
        BillListPanel.curDish = newDish;

        //update the interface.
        tblSelectedDish.setValueAt("X1", tValidRowCount, 0); // set the count.
        tblSelectedDish.setValueAt(dish.getLanguage(CustOpts.custOps.getUserLang()), tValidRowCount, 1);// set the Name.
        tblSelectedDish.setValueAt(dish.getSize() > 1 ? dish.getSize() : "", tValidRowCount, 2); // set the count.
        tblSelectedDish.setValueAt(dish.getPrice()/100f, tValidRowCount, 3); // set the price.
        
        updateTotleArea();								//because value change will not be used to remove the record.
        SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
		        tblSelectedDish.setSelectedRow(selectdDishAry.size() - 1);
			}
		});
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
    		int pK = num /(BarOption.MaxQTY * 100);
    		if(num > BarOption.MaxQTY * 100) {
    			num = num %(BarOption.MaxQTY * 100);
    		}
    		int pS = (int)num /BarOption.MaxQTY;
    		if(num > BarOption.MaxQTY) {
    			num = num % BarOption.MaxQTY;
    		}
    	
    		price *= num;
    		gst *= num;
    		qst *= num;
    		if(pS > 0) {
    			price /= pS;
    			gst /= pS;
    			qst /= pS;
    		}
    		if(pK > 0) {
    			price /= pK;
    			gst /= pK;
    			qst /= pK;
    		}

    		subTotal += price;
    		totalGst += gst;
    		totalQst += qst;
    	}
    	
    	lblSubTotle.setText(BarDlgConst.Subtotal + " : " + String.valueOf(subTotal/100f));
        lblGSQ.setText(BarDlgConst.QST + " : " + String.valueOf(((int)totalGst)/100f));
        lblQSQ.setText(BarDlgConst.GST + " : " + String.valueOf(((int)totalQst)/100f));
        lblTotlePrice.setText(BarDlgConst.Total + " : " + String.valueOf(((int) (subTotal + totalGst + totalQst))/100f));
    }
    
    void initContent() {
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
				dish.setBillID(billID);
				selectdDishAry.add(dish);

				tValues[tmpPos][1] = dish.getLanguage(LoginDlg.USERLANG);
				
				int num = dish.getNum();
				//first pick out the number on 100,0000 and 10000 position
	    		int pK = num /(BarOption.MaxQTY * 100);
	    		if(num > BarOption.MaxQTY * 100) {
	    			num = num %(BarOption.MaxQTY * 100);
	    		}
	    		int pS = (int)num /BarOption.MaxQTY;
	    		if(num > BarOption.MaxQTY) {
	    			num = num % BarOption.MaxQTY;
	    		}
				StringBuilder strNum = new StringBuilder("X");
				strNum.append(num);
				if(pS > 0)
					strNum.append("/").append(pS);
				if(pK > 0)
					strNum.append("/").append(pK);
				tValues[tmpPos][0] = strNum.toString();
				
				tValues[tmpPos][3] = "$" + rs.getInt("TOLTALPRICE") / 100f;
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

		resetColWidth(scrContent.getWidth());
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
        //at first, teh tableWidth is 0, then after, the tableWidth will be 260. 
        PIMTableColumn tmpCol2 = tblSelectedDish.getColumnModel().getColumn(1);
        int width = tableWidth - tmpCol1.getWidth() - tmpCol3.getWidth() - tmpCol4.getWidth() - 3;
        width -= scrContent.getVerticalScrollBar().isVisible() ? scrContent.getVerticalScrollBar().getWidth() : 0;
        tmpCol2.setWidth(width);
        tmpCol2.setPreferredWidth(width);
        
        tblSelectedDish.validate();
        tblSelectedDish.revalidate();
        tblSelectedDish.invalidate();
    }

    void removeAtSelection(int selectedRow) {
		int tValidRowCount = getUsedRowCount(); // get the used RowCount
    	if(selectedRow < 0 || selectedRow > tValidRowCount - 1) {
    		JOptionPane.showMessageDialog(this, BarDlgConst.OnlyOneShouldBeSelected);
    		ErrorUtil.write("Unexpected row number when calling removeAtSelection : " + selectedRow);
    		return;
    	}
    	//update db first
    	Dish dish = selectdDishAry.get(selectedRow);
    	if(dish.getOutputID() > -1) {
    		Dish.delete(dish);
    	}
    	//update array second.
		selectdDishAry.remove(selectedRow);
		//update the table view
		int tColCount = tblSelectedDish.getColumnCount();
		Object[][] tValues = new Object[tValidRowCount - 1][tColCount];
		for (int r = 0; r < tValidRowCount; r++) {
			if(r == selectedRow) {
				continue;
			}else {
				int rowNum = r > selectedRow ? r : r + 1;
			    for (int c = 0; c < tColCount; c++)
			        tValues[rowNum-1][c] = tblSelectedDish.getValueAt(r, c);
			}
		}
		tblSelectedDish.setDataVector(tValues, header);
		resetColWidth(scrContent.getWidth());
		tblSelectedDish.setSelectedRow(tValues.length - 1); //@Note this will trigger a value change event, to set the curDish.
		updateTotleArea();
	}
    
    private int getUsedRowCount() {
        for (int i = 0, len = tblSelectedDish.getRowCount(); i < len; i++)
            if (tblSelectedDish.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblSelectedDish.getRowCount();
    }
    
    void reLayout() {
        int panelHeight = getHeight();

        int tBtnWidht = (getWidth() - CustOpts.HOR_GAP * 10) / 9;
        int tBtnHeight = panelHeight / 10;
        
     // table area-------------
        int poxX = 0;
        int posY = 0;
        if(billButton != null) {
        	billButton.setBounds(poxX, posY, getWidth(), CustOpts.BTN_HEIGHT + 16);
        	posY += billButton.getHeight();
        }
        scrContent.setBounds(poxX, posY, getWidth(), getHeight() - BarDlgConst.SubTotal_HEIGHT);
        
		// sub total-------
		lblGSQ.setBounds(scrContent.getX(), scrContent.getY() + scrContent.getHeight(), scrContent.getWidth() / 4,
				BarDlgConst.SubTotal_HEIGHT * 1 / 3);
		lblQSQ.setBounds(lblGSQ.getX() + lblGSQ.getWidth(), lblGSQ.getY(), lblGSQ.getWidth(), lblGSQ.getHeight());
		lblSubTotle.setBounds(lblQSQ.getX() + lblQSQ.getWidth(), lblGSQ.getY(), lblQSQ.getWidth() * 2,
				lblGSQ.getHeight());

		lblTotlePrice.setBounds(lblSubTotle.getX(), getHeight() - CustOpts.BTN_HEIGHT, lblSubTotle.getWidth(), BarDlgConst.SubTotal_HEIGHT * 2 / 3);
		
		if(billButton == null){
        	btnMore.setBounds(scrContent.getX() + scrContent.getWidth() - BarDlgConst.SCROLLBAR_WIDTH, lblGSQ.getY(), 
            		BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH);
            btnLess.setBounds(btnMore.getX() - CustOpts.HOR_GAP - BarDlgConst.SCROLLBAR_WIDTH, btnMore.getY(), 
            		BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH);
        }
    }
    
    void initComponent() {

        tblSelectedDish = new PIMTable();// 显示字段的表格,设置模型
        scrContent = new PIMScrollPane(tblSelectedDish);
        lblSubTotle = new JLabel(BarDlgConst.Subtotal);
        lblGSQ = new JLabel(BarDlgConst.QST);
        lblQSQ = new JLabel(BarDlgConst.GST);
        lblTotlePrice = new JLabel(BarDlgConst.Total);
        btnMore = new ArrayButton("+");
        btnLess = new ArrayButton("-");
        
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
        scrContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);

        // Margin-----------------
        btnMore.setMargin(new Insets(0,0,0,0));
        btnLess.setMargin(btnMore.getInsets());
        
        // border----------
        tblSelectedDish.setBorder(null);
        tblSelectedDish.setShowGrid(false);
        // forcus-------------
        tblSelectedDish.setFocusable(false);

        // disables
        btnMore.setEnabled(false);
        btnLess.setEnabled(false);
        
        // built
        if(billButton != null) {
        	add(billButton);
            billButton.addActionListener(this);
        }else {
            add(btnMore);
            add(btnLess);
            add(lblSubTotle);
            add(lblGSQ);
            add(lblQSQ);
        }
        add(lblTotlePrice);
        add(scrContent);

        addComponentListener(this);
        btnMore.addActionListener(this);
        btnLess.addActionListener(this);
        tblSelectedDish.addMouseMotionListener(this);
        tblSelectedDish.addMouseListener(this);
        tblSelectedDish.getSelectionModel().addListSelectionListener(this);
        scrContent.getViewport().addMouseListener(this);
    }
    
    PIMTable tblSelectedDish;
    private PIMScrollPane scrContent;

    private JLabel lblSubTotle;
    private JLabel lblGSQ;
    private JLabel lblQSQ;
    private JLabel lblTotlePrice;
    
    private ArrayButton btnMore;
    private ArrayButton btnLess;
    
    String[] header = new String[] {BarDlgConst.Count, BarDlgConst.ProdName, "", 
            BarDlgConst.Price};

}
