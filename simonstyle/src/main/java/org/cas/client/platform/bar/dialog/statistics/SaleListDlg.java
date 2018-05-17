package org.cas.client.platform.bar.dialog.statistics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.cas.client.platform.bar.dialog.BarDlgConst;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pos.dialog.PosDlgConst;
import org.cas.client.resource.international.DlgConst;

public class SaleListDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener {
    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public SaleListDlg(JFrame pParent) {
        super(pParent, true);
        initDialog();
    }

    @Override
    public void keyTyped(
            KeyEvent e) {
    }

    @Override
    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
        switch (e.getKeyCode()) {
            case 37:
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn() - 1);
                break;
            case 38:
                tblContent.setSelectedRow(tblContent.getSelectedRow() - 1);
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
                break;
            case 39:
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn() + 1);
                break;
            case 40:
                tblContent.setSelectedRow(tblContent.getSelectedRow() + 1);
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
                break;
        }
    }

    @Override
    public void keyReleased(
            KeyEvent e) {
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 
        		getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2, 
                getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP * 3
                - CustOpts.BTN_WIDTH_NUM);

        int btnWidth = (srpContent.getWidth() - CustOpts.HOR_GAP * 3)/4;
        btnClose.setBounds(getWidth() - CustOpts.HOR_GAP * 2 - btnWidth,
                srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, btnWidth, CustOpts.BTN_WIDTH_NUM);// 关闭
        btnPrintBill.setBounds(btnClose.getX() - CustOpts.HOR_GAP - btnWidth, btnClose.getY(),
                btnWidth, CustOpts.BTN_WIDTH_NUM);
        btnViewDetail.setBounds(btnPrintBill.getX() - CustOpts.HOR_GAP - btnWidth, btnClose.getY(),
                btnWidth, CustOpts.BTN_WIDTH_NUM);
        btnChangeDate.setBounds(btnViewDetail.getX() - CustOpts.HOR_GAP - btnWidth, btnViewDetail.getY(),
                btnWidth, CustOpts.BTN_WIDTH_NUM);

        IPIMTableColumnModel tTCM = tblContent.getColumnModel();
        tTCM.getColumn(0).setPreferredWidth(130);	//BarDlgConst.TIME
        tTCM.getColumn(1).setPreferredWidth(40);	//BarDlgConst.Table,
        tTCM.getColumn(2).setPreferredWidth(40);	//BarDlgConst.Bill
        tTCM.getColumn(3).setPreferredWidth(60);	//BarDlgConst.Total
        tTCM.getColumn(4).setPreferredWidth(60);	//BarDlgConst.Discount
        tTCM.getColumn(5).setPreferredWidth(60);	//BarDlgConst.Receive
        tTCM.getColumn(6).setPreferredWidth(60);	//BarDlgConst.Tip
        tTCM.getColumn(7).setPreferredWidth(60);	//BarDlgConst.CashBack
        tTCM.getColumn(8).setPreferredWidth(60);	//BarDlgConst.Status
        tTCM.getColumn(9).setPreferredWidth(60);	//BarDlgConst.Operator
        tTCM.getColumn(10).setPreferredWidth(srpContent.getWidth() - 635);	//BarDlgConst.comment
        tTCM.getColumn(11).setPreferredWidth(0);	//table open time, invisible.
        validate();
    }

    @Override
    public PIMRecord getContents() {
        return null;
    }

    @Override
    public boolean setContents( PIMRecord prmRecord) {
        return true;
    }

    @Override
    public void makeBestUseOfTime() {}

    @Override
    public void addAttach(File[] file, Vector actualAttachFiles) {}

    @Override
    public PIMTextPane getTextPane() {
        return null;
    }

    @Override
    public void release() {
        btnClose.removeActionListener(this);
        btnChangeDate.removeActionListener(this);
        btnViewDetail.removeActionListener(this);
        btnPrintBill.removeActionListener(this);
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    @Override
    public void componentMoved(ComponentEvent e) {};

    @Override
    public void componentShown(ComponentEvent e) {};

    @Override
    public void componentHidden(ComponentEvent e) {};

    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnClose) {
            dispose();
        } else if (o == btnChangeDate) {
            
        } else if (o == btnViewDetail) {
        	//check if a row is selected
        	showInSalesPanel();
        } else if(o == btnPrintBill) {
        	showInSalesPanel();
        	((SalesPanel)BarFrame.instance.panels[BarFrame.instance.curPanel]).billPanel
        	.printBill(BarFrame.instance.valCurTable.getText(), BarFrame.instance.valCurBill.getText(), BarFrame.instance.valStartTime.getText());
        }
    }
    
    private void showInSalesPanel() {
    	int selectedRow = tblContent.getSelectedRow();
    	int tValidRowCount = getUsedRowCount();
    	if(selectedRow < 0 || selectedRow > tValidRowCount - 1) {
    		JOptionPane.showMessageDialog(this, BarDlgConst.OnlyOneShouldBeSelected);
    		ErrorUtil.write("Unexpected row number when calling removeAtSelection : " + selectedRow);
    		return;
    	}
    	//swith to sales panel.
    	
    	BarFrame.instance.valCurTable.setText(String.valueOf(tblContent.getValueAt(selectedRow, 1)));
    	BarFrame.instance.valCurBill.setText(String.valueOf(tblContent.getValueAt(selectedRow, 2)));
    	BarFrame.instance.valOperator.setText(String.valueOf(tblContent.getValueAt(selectedRow, 9)));
    	BarFrame.instance.valStartTime.setText(String.valueOf(tblContent.getValueAt(selectedRow, 11)));
    	BarFrame.instance.switchMode(2);
    }
    
    private int getUsedRowCount() {
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++)
            if (tblContent.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblContent.getRowCount();
    }
    
    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(PosDlgConst.SaleRecs);

        // 初始化－－－－－－－－－－－－－－－－
        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);

        btnClose = new JButton(BarDlgConst.CLOSE);
        btnChangeDate = new JButton(BarDlgConst.ChangeDate);
        btnViewDetail = new JButton(BarDlgConst.ViewDetail);
        btnPrintBill = new JButton(BarDlgConst.PRINT_BILL);
        
        // properties
        btnClose.setMnemonic('o');
        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnChangeDate.setMnemonic('F');
        btnChangeDate.setMargin(btnClose.getMargin());
        btnViewDetail.setMnemonic('U');
        btnPrintBill.setMargin(btnClose.getMargin());

        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(30);
        tblContent.setBorder(new JTextField().getBorder());
        tblContent.setFocusable(false);

        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        getRootPane().setDefaultButton(btnClose);

        // 布局---------------
        setBounds((CustOpts.SCRWIDTH - 600) / 2, (CustOpts.SCRHEIGHT - 500) / 2, 600, 500); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(srpContent);
        getContentPane().add(btnClose);
        getContentPane().add(btnChangeDate);
        getContentPane().add(btnViewDetail);
        getContentPane().add(btnPrintBill);

        // 加监听器－－－－－－－－
        btnClose.addActionListener(this);
        btnChangeDate.addActionListener(this);
        btnViewDetail.addActionListener(this);
        btnPrintBill.addActionListener(this);
        btnClose.addKeyListener(this);
        btnChangeDate.addKeyListener(this);
        btnViewDetail.addKeyListener(this);
        btnPrintBill.addKeyListener(this);
        getContentPane().addComponentListener(this);
    }

    public void initTable(String startTime, String endTime) {
        Object[][] tValues = null;
        String sql = "select * from bill where createTime >= '" + startTime + "' and createTime < '" + endTime + "'";

        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            tValues = new Object[tmpPos][header.length];
            rs.beforeFirst();
            tmpPos = 0;
            while (rs.next()) {
                tValues[tmpPos][0] = rs.getString("createTime");;
                tValues[tmpPos][1] = rs.getString("tableID");
                tValues[tmpPos][2] = rs.getString("billIndex");
                tValues[tmpPos][3] = Float.valueOf((float) (rs.getInt("total") / 100.0));
                tValues[tmpPos][4] = Float.valueOf((float) (rs.getInt("discount") / 100.0));
                tValues[tmpPos][5] = Float.valueOf((float) (rs.getInt("received") / 100.0));
                tValues[tmpPos][6] = Float.valueOf((float) (rs.getInt("tip") / 100.0));
                tValues[tmpPos][7] = Float.valueOf((float) (rs.getInt("cashback") / 100.0));
                tValues[tmpPos][8] = rs.getInt("status");
                tValues[tmpPos][9] = rs.getInt("employeeId");
                tValues[tmpPos][10] = rs.getString("Comment");
                tValues[tmpPos][11] = rs.getString("OpenTime");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        tblContent.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
    }
    
	/**this init is for displaying all bills of a day.
	void initContent(String startTime, String endTime) {
		btnLeft.setEnabled(true);
		btnRight.setEnabled(!BarOption.df.format(new Date()).startsWith(endTime.substring(0, 10)));
		for(int i = onScrBills.size() - 1; i >= 0; i--) {
			remove(onScrBills.get(i));
		}
		billPanels.clear();
		onScrBills.clear();
		
		// load all the unclosed outputs under this table with ---------------------------
		try {
			Statement smt = PIMDBModel.getReadOnlyStatement();
			ResultSet rs = smt.executeQuery("SELECT DISTINCT category from output where category > '" + startTime
					+ "' and category < '" + endTime + "' order by category");
			rs.beforeFirst();
			String time = "";
			while (rs.next()) {
				JToggleButton billButton = new JToggleButton();
				time = rs.getString("category");
				billButton.setText(time);
				billButton.setMargin(new Insets(0, 0, 0, 0));
				
				BillPanel billPanel = new BillPanel(this, billButton);
				billPanels.add(billPanel);
			}

			//do it outside the above loop, because there's another qb query inside.
			int col = BarOption.getBillPageCol();
			int row = BarOption.getBillPageRow();

			for(int i = 0; i < row * col; i++) {
				if(row * col * curPageNum + i < billPanels.size()) {
					billPanels.get(row * col * curPageNum + i).initComponent();
					billPanels.get(row * col * curPageNum + i).initContent(time);
					onScrBills.add(billPanels.get(row * col * curPageNum + i));
					btnRight.setEnabled(true);
				}else {
					BillPanel panel = new BillPanel(this, new JToggleButton("0"));	//have to give a number to construct valid sql.
					panel.initComponent();
					panel.initContent();
					onScrBills.add(panel);
					btnRight.setEnabled(false);
				}
			}
		} catch (Exception e) {
 			ErrorUtil.write("Unexpected exception when init the tables from db." + e);
 		}
		reLayout();
	}*/
	
    private void initPordAndEmploy() {
        String sql = "select ID, UserName from UserIdentity";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            employIdAry = new int[tmpPos + 1];
            employNameAry = new String[tmpPos + 1];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                employIdAry[tmpPos] = rs.getInt("ID");
                employNameAry[tmpPos] = rs.getString("UserName");
                tmpPos++;
            }
            employIdAry[tmpPos] = -1;
            employNameAry[tmpPos] = "-";
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        // for the service.
        sql = "select ID, SUBJECT from Product";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            prodIdAry = new int[tmpPos];
            prodNameAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                prodIdAry[tmpPos] = rs.getInt("ID");
                prodNameAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
    }

    private String[] header = new String[] {
    		BarDlgConst.TIME, // "时间"
    		BarDlgConst.Table,
    		BarDlgConst.Bill,
    		BarDlgConst.Total,
    		BarDlgConst.Discount,
    		BarDlgConst.Receive,
    		BarDlgConst.Tip,
    		BarDlgConst.CashBack,
    		BarDlgConst.Status,
    		BarDlgConst.Operator, // "操作员"
    		BarDlgConst.comment,
    		BarDlgConst.OpenTime};

    int[] employIdAry;
    String[] employNameAry;
    int[] prodIdAry;
    String[] prodNameAry;
    PIMTable tblContent;
    PIMScrollPane srpContent;
    private JButton btnChangeDate;
    private JButton btnViewDetail;
    private JButton btnPrintBill;
    private JButton btnClose;
}
