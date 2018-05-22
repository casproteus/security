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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.bar.dialog.BarDlgConst;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.BillPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.model.Dish;
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

public class SaleListDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener, ListSelectionListener {
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
        
        int startX = (getWidth() - 360 - CustOpts.HOR_GAP * 8 - lblFrom.getPreferredSize().width - lblTo.getPreferredSize().width) / 2;
        lblFrom.setBounds(startX, srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP * 2 + lblYearFrom.getPreferredSize().height,
        		lblFrom.getPreferredSize().width, lblFrom.getPreferredSize().height);
        lblYearFrom.setBounds(lblFrom.getX() + lblFrom.getWidth() + CustOpts.HOR_GAP, srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP,
        		60, lblYearFrom.getPreferredSize().height);
        tfdYearFrom.setBounds(lblYearFrom.getX(), lblYearFrom.getY() + lblYearFrom.getHeight() + CustOpts.VER_GAP, 
        		lblYearFrom.getWidth(), 30);
        
        lblMonthFrom.setBounds(lblYearFrom.getX() + lblYearFrom.getWidth() + CustOpts.HOR_GAP, lblYearFrom.getY(), 
        		40, lblMonthFrom.getPreferredSize().height);
        tfdMonthFrom.setBounds(lblMonthFrom.getX(), tfdYearFrom.getY(), lblMonthFrom.getWidth(), tfdYearFrom.getHeight());
        
        lblDayFrom.setBounds(lblMonthFrom.getX() + lblMonthFrom.getWidth() + CustOpts.HOR_GAP, lblMonthFrom.getY(), 
        		lblMonthFrom.getWidth(), lblDayFrom.getPreferredSize().height);
        tfdDayFrom.setBounds(lblDayFrom.getX(), tfdYearFrom.getY(), lblDayFrom.getWidth(), tfdYearFrom.getHeight());
        
        lblTo.setBounds(tfdDayFrom.getX() + tfdDayFrom.getWidth() + CustOpts.HOR_GAP, lblFrom.getY(), lblTo.getPreferredSize().width, lblFrom.getHeight());
        
        lblYearTo.setBounds(lblTo.getX() + lblTo.getWidth() + CustOpts.HOR_GAP, lblMonthFrom.getY(), 
        		lblYearFrom.getWidth(), lblYearFrom.getHeight());
        tfdYearTo.setBounds(lblYearTo.getX(), tfdYearFrom.getY(), lblYearTo.getWidth(), tfdYearFrom.getHeight());
        
        lblMonthTo.setBounds(lblYearTo.getX() + lblYearTo.getWidth() + CustOpts.HOR_GAP, lblMonthFrom.getY(), 
        		lblMonthFrom.getWidth(), lblMonthFrom.getHeight());
        tfdMonthTo.setBounds(lblMonthTo.getX(), tfdYearFrom.getY(), lblMonthTo.getWidth(), tfdYearFrom.getHeight());
        
        lblDayTo.setBounds(lblMonthTo.getX() + lblMonthTo.getWidth() + CustOpts.HOR_GAP, lblMonthFrom.getY(), 
        		lblDayFrom.getWidth(), lblDayFrom.getHeight());
        tfdDayTo.setBounds(lblDayTo.getX(), tfdYearFrom.getY(), lblDayTo.getWidth(), tfdYearFrom.getHeight());

        int btnWidth = 80;
        btnChangeDate.setBounds(tfdDayTo.getX() + tfdDayTo.getWidth() + CustOpts.HOR_GAP, lblDayTo.getY(),
                btnWidth, tfdDayTo.getHeight() + lblDayTo.getHeight() + CustOpts.VER_GAP);
        
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
        btnChangeDate.removeActionListener(this);
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
        if (o == btnChangeDate) {
        	StringBuilder startTime = new StringBuilder();
        	startTime.append(tfdYearFrom.getText());
        	startTime.append("-");
        	startTime.append(tfdMonthFrom.getText());
        	startTime.append("-");
        	startTime.append(tfdDayFrom.getText());
        	startTime.append(" 00:00:00");

        	StringBuilder endTime = new StringBuilder();
        	endTime.append(tfdYearTo.getText());
        	endTime.append("-");
        	endTime.append(tfdMonthTo.getText());
        	endTime.append("-");
        	endTime.append(tfdDayTo.getText());
        	endTime.append(" 00:00:00");
        	
        	initContent(startTime.toString(), endTime.toString());
        	reLayout();
        }
//		else if(o == btnPrintBill) {
//        	showInSalesPanel();
//        	((SalesPanel)BarFrame.instance.panels[BarFrame.instance.curPanel]).billPanel
//        	.printBill(BarFrame.instance.valCurTable.getText(), BarFrame.instance.valCurBill.getText(), BarFrame.instance.valStartTime.getText());
//        }
    }
    
	@Override
	public void valueChanged(ListSelectionEvent e) {
		showInSalesPanel();
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
        setTitle(BarDlgConst.SaleRecs);
        setModal(false);

        // 初始化－－－－－－－－－－－－－－－－
        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);

        lblFrom = new JLabel(BarDlgConst.FROM);
        lblTo = new JLabel(BarDlgConst.TO);
        lblYearFrom = new JLabel("YYYY");
        lblMonthFrom = new JLabel("MM");
        lblDayFrom = new JLabel("DD");
        lblYearTo = new JLabel("YYYY");
        lblMonthTo = new JLabel("MM");
        lblDayTo = new JLabel("DD");
        tfdYearFrom = new JTextField();
        tfdMonthFrom = new JTextField();
        tfdDayFrom = new JTextField();
        tfdYearTo = new JTextField();
        tfdMonthTo = new JTextField();
        tfdDayTo = new JTextField();
        btnChangeDate = new JButton(BarDlgConst.APPLY);
        
        // properties
        btnChangeDate.setMnemonic('F');
        btnChangeDate.setMargin(new Insets(0, 0, 0, 0));
        lblTo.setHorizontalTextPosition(SwingConstants.CENTER);
        lblYearFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblMonthFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblDayFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblYearTo.setHorizontalTextPosition(SwingConstants.CENTER);
        lblMonthTo.setHorizontalTextPosition(SwingConstants.CENTER);
        lblDayTo.setHorizontalTextPosition(SwingConstants.CENTER);
        
        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(30);
        tblContent.setBorder(new JTextField().getBorder());
        tblContent.setFocusable(false);

        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);

        // 布局---------------
        setBounds((CustOpts.SCRWIDTH - 780) / 2, (CustOpts.SCRHEIGHT - 500) / 2, 780, 500); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(srpContent);
        getContentPane().add(lblFrom);
        getContentPane().add(lblTo);
        getContentPane().add(lblYearFrom);
        getContentPane().add(lblMonthFrom);
        getContentPane().add(lblDayFrom);
        getContentPane().add(lblYearTo);
        getContentPane().add(lblMonthTo);
        getContentPane().add(lblDayTo);
        getContentPane().add(tfdYearFrom);
        getContentPane().add(tfdMonthFrom);
        getContentPane().add(tfdDayFrom);
        getContentPane().add(tfdYearTo);
        getContentPane().add(tfdMonthTo);
        getContentPane().add(tfdDayTo);
        getContentPane().add(btnChangeDate);

        // 加监听器－－－－－－－－
        tblContent.getSelectionModel().addListSelectionListener(this);
        btnChangeDate.addActionListener(this);
        btnChangeDate.addKeyListener(this);
        getContentPane().addComponentListener(this);
    }

    public void initContent(String startTime, String endTime) {
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
                int received = rs.getInt("cashReceived");
                received += rs.getInt("debitReceived");
                received += rs.getInt("visaReceived");
                received += rs.getInt("masterReceived"); 
                received += rs.getInt("otherReceived");
                tValues[tmpPos][5] = Float.valueOf((float) (received / 100.0));
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
        
        //fill the dataselection area
        int p = startTime.indexOf(" ");
        startTime = startTime.substring(0, p);
        p = startTime.indexOf("-");
        tfdYearFrom.setText(startTime.substring(0, p));
        startTime = startTime.substring(p + 1);
        p = startTime.indexOf("-");
        tfdMonthFrom.setText(startTime.substring(0, p));
        tfdDayFrom.setText(startTime.substring(p + 1));
        
        p = endTime.indexOf(" ");
        endTime = endTime.substring(0, p);
        p = endTime.indexOf("-");
        tfdYearTo.setText(endTime.substring(0, p));
        endTime = endTime.substring(p + 1);
        p = endTime.indexOf("-");
        tfdMonthTo.setText(endTime.substring(0, p));
        tfdDayTo.setText(endTime.substring(p + 1));
        
    }
    
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
    		BarDlgConst.TABLE,
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
    
    JLabel lblFrom;
    JLabel lblTo;
    JLabel lblYearFrom;
    JLabel lblMonthFrom;
    JLabel lblDayFrom;
    JLabel lblYearTo;
    JLabel lblMonthTo;
    JLabel lblDayTo;
    JTextField tfdYearFrom;
    JTextField tfdMonthFrom;
    JTextField tfdDayFrom;
    JTextField tfdYearTo;
    JTextField tfdMonthTo;
    JTextField tfdDayTo;
    private JButton btnChangeDate;
}
