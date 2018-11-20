package org.cas.client.platform.bar.dialog.statistics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.model.Bill;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;

public class ReportDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, ListSelectionListener {
	
	/**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public ReportDlg(JFrame pParent) {
        super(pParent, true);
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        panel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 
        		getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2, 
                getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP * 3
                - CustOpts.BTN_WIDTH_NUM);
        
        int startX = (getWidth() - 360 - CustOpts.HOR_GAP * 8 - lblFrom.getPreferredSize().width - lblTo.getPreferredSize().width) / 2;
        lblFrom.setBounds(startX, panel.getY() + panel.getHeight() + CustOpts.VER_GAP * 2 + lblYearFrom.getPreferredSize().height,
        		lblFrom.getPreferredSize().width, lblFrom.getPreferredSize().height);
        lblYearFrom.setBounds(lblFrom.getX() + lblFrom.getWidth() + CustOpts.HOR_GAP, panel.getY() + panel.getHeight() + CustOpts.VER_GAP,
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
        btnPrint.setBounds(tfdDayTo.getX() + tfdDayTo.getWidth() + CustOpts.HOR_GAP, lblDayTo.getY(),
                btnWidth, tfdDayTo.getHeight() + lblDayTo.getHeight() + CustOpts.VER_GAP);
        
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
        btnPrint.removeActionListener(this);
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
        if (o == btnPrint) {
        	StringBuilder startTime = new StringBuilder();
        	startTime.append(tfdYearFrom.getText());
        	startTime.append("-");
        	startTime.append(tfdMonthFrom.getText());
        	startTime.append("-");
        	startTime.append(tfdDayFrom.getText());
        	String startDate = startTime.toString();
        	startTime.append(" 00:00:00");

        	StringBuilder endTime = new StringBuilder();
        	endTime.append(tfdYearTo.getText());
        	endTime.append("-");
        	endTime.append(tfdMonthTo.getText());
        	endTime.append("-");
        	endTime.append(tfdDayTo.getText());
        	String endDate = endTime.toString();
        	endTime.append(" 23:59:59");
        	ArrayList<Bill> bills = queryBillList(startTime.toString(), endTime.toString());
        	PrintService.exePrintReport(bills, startDate, endDate);
        }
    }
    
	@Override
	public void valueChanged(ListSelectionEvent e) {
	}
    
    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarFrame.consts.SaleRecs());
        setModal(false);

        // 初始化－－－－－－－－－－－－－－－－
        panel = new JPanel();

        lblFrom = new JLabel(BarFrame.consts.FROM());
        lblTo = new JLabel(BarFrame.consts.TO());
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
        btnPrint = new JButton(BarFrame.consts.PRINT());
        // properties
        btnPrint.setMnemonic('F');
        btnPrint.setMargin(new Insets(0, 0, 0, 0));
        
        lblTo.setHorizontalTextPosition(SwingConstants.CENTER);
        lblYearFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblMonthFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblDayFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblYearTo.setHorizontalTextPosition(SwingConstants.CENTER);
        lblMonthTo.setHorizontalTextPosition(SwingConstants.CENTER);
        lblDayTo.setHorizontalTextPosition(SwingConstants.CENTER);
        

        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);

        // 布局---------------
        setBounds((CustOpts.SCRWIDTH - 780) / 2, (CustOpts.SCRHEIGHT - 500) / 2, 550, 120); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(panel);
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
        getContentPane().add(btnPrint);

        // 加监听器－－－－－－－－
        btnPrint.addActionListener(this);
        getContentPane().addComponentListener(this);
        
        //initContent
        String endNow = BarOption.df.format(new Date());
		int p = endNow.indexOf(" ");
		String startTime = endNow.substring(0, p + 1) + BarOption.getStartTime();
		initContent(startTime, endNow);
    }

	private void initContent(String startTime, String endTime) {
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
    
    public ArrayList<Bill> queryBillList(String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("select * from bill, employee where createTime >= '").append(startTime)
        		.append("' and createTime <= '").append(endTime).append("' and bill.employeeId = employee.id");
        if(LoginDlg.USERTYPE < 2) {	//if is not admin, then get out only user related records.
        	sql.append(" and employee.id = ").append(LoginDlg.USERID);
        }

        ArrayList<Bill> bills = new ArrayList<Bill>();
        
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.afterLast();
            rs.relative(-1);
            rs.beforeFirst();
            while (rs.next()) {
            	Bill bill = new Bill();
            	bill.setCreateTime(rs.getString("createTime"));
            	bill.setTableID(rs.getString("tableID"));
            	bill.setBillIndex(rs.getString("billIndex"));
            	bill.setTotal(Float.valueOf((float) (rs.getInt("total") / 100.0)));
            	bill.setDiscount(Float.valueOf((float) (rs.getInt("discount") / 100.0)));
            	bill.setCashReceived(rs.getInt("cashReceived"));
            	bill.setDebitReceived(rs.getInt("debitReceived"));
            	bill.setVisaReceived(rs.getInt("visaReceived"));
            	bill.setMasterReceived(rs.getInt("masterReceived")); 
            	bill.setTip(rs.getInt("tip"));
            	bill.setCashback(rs.getInt("cashback"));
            	bill.setStatus(rs.getInt("status"));
            	bill.setEmployeeName(rs.getString("employee.nName"));
            	bill.setComment(rs.getString("Comment"));
            	bill.setOpenTime(rs.getString("OpenTime"));
            	bills.add(bill);
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        return bills;
    }
    

    int[] employIdAry;
    String[] employNameAry;
    int[] prodIdAry;
    String[] prodNameAry;
    
    JPanel panel;
    
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
    private JButton btnPrint;
}
