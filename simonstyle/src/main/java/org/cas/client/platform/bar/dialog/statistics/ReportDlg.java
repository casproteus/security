package org.cas.client.platform.bar.dialog.statistics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.model.Bill;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;

public class ReportDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, ListSelectionListener, ChangeListener {
	
	private List<String> formattedString;
	private StringBuilder startTime;
	private StringBuilder endTime;
	private ArrayList<Bill> bills;
	private String printerIP;
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
        lblFrom.setBounds(CustOpts.HOR_GAP,  50,
        		lblFrom.getPreferredSize().width, lblFrom.getPreferredSize().height);
        
        lblYearFrom.setBounds(lblFrom.getX() + lblFrom.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP,
        		35, lblYearFrom.getPreferredSize().height);
        tfdYearFrom.setBounds(lblYearFrom.getX(), lblYearFrom.getY() + lblYearFrom.getHeight() + CustOpts.VER_GAP, 
        		lblYearFrom.getWidth(), 30);
        
        lblMonthFrom.setBounds(lblYearFrom.getX() + lblYearFrom.getWidth() + CustOpts.HOR_GAP, lblYearFrom.getY(), 
        		25, lblMonthFrom.getPreferredSize().height);
        tfdMonthFrom.setBounds(lblMonthFrom.getX(), tfdYearFrom.getY(), lblMonthFrom.getWidth(), tfdYearFrom.getHeight());
        
        lblDayFrom.setBounds(lblMonthFrom.getX() + lblMonthFrom.getWidth() + CustOpts.HOR_GAP, lblMonthFrom.getY(), 
        		lblMonthFrom.getWidth(), lblDayFrom.getPreferredSize().height);
        tfdDayFrom.setBounds(lblDayFrom.getX(), tfdYearFrom.getY(), lblDayFrom.getWidth(), tfdYearFrom.getHeight());
        
        lblHourFrom.setBounds(lblYearFrom.getX() + 10, tfdYearFrom.getY() + tfdYearFrom.getHeight() + CustOpts.VER_GAP,
        		lblMonthFrom.getWidth(), lblHourFrom.getPreferredSize().height);
        tfdHourFrom.setBounds(lblHourFrom.getX(), lblHourFrom.getY() + lblHourFrom.getHeight() + CustOpts.VER_GAP, 
        		lblHourFrom.getWidth(), 30);
        
        lblMinuteFrom.setBounds(lblHourFrom.getX() + lblHourFrom.getWidth() + CustOpts.HOR_GAP, lblHourFrom.getY(), 
        		lblHourFrom.getWidth(), lblMinuteFrom.getPreferredSize().height);
        tfdMinuteFrom.setBounds(lblMinuteFrom.getX(), tfdHourFrom.getY(), lblMinuteFrom.getWidth(), tfdHourFrom.getHeight());
        
        lblSecondFrom.setBounds(lblMinuteFrom.getX() + lblMinuteFrom.getWidth() + CustOpts.HOR_GAP, lblMinuteFrom.getY(), 
        		lblMinuteFrom.getWidth(), lblSecondFrom.getPreferredSize().height);
        tfdSecondFrom.setBounds(lblSecondFrom.getX(), tfdMinuteFrom.getY(), lblSecondFrom.getWidth(), tfdMinuteFrom.getHeight());
       
        
        
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
        
        lblHourTo.setBounds(lblYearTo.getX() + 10, lblMinuteFrom.getY(), 
        		lblHourFrom.getWidth(), lblHourFrom.getHeight());
        tfdHourTo.setBounds(lblHourTo.getX(), tfdHourFrom.getY(), lblHourTo.getWidth(), tfdHourFrom.getHeight());
        
        lblMinuteTo.setBounds(lblHourTo.getX() + lblHourTo.getWidth() + CustOpts.HOR_GAP, lblMinuteFrom.getY(), 
        		lblMinuteFrom.getWidth(), lblMinuteFrom.getHeight());
        tfdMinuteTo.setBounds(lblMinuteTo.getX(), tfdHourFrom.getY(), lblMinuteTo.getWidth(), tfdHourFrom.getHeight());
        
        lblSecondTo.setBounds(lblMinuteTo.getX() + lblMinuteTo.getWidth() + CustOpts.HOR_GAP, lblMinuteFrom.getY(), 
        		lblSecondFrom.getWidth(), lblSecondFrom.getHeight());
        tfdSecondTo.setBounds(lblSecondTo.getX(), tfdHourFrom.getY(), lblSecondTo.getWidth(), tfdHourFrom.getHeight());

        int btnWidth = 80;
        btnPrint.setBounds(tfdSecondTo.getX() + tfdSecondTo.getWidth() + CustOpts.HOR_GAP, 40,
                btnWidth, tfdSecondTo.getHeight() + lblSecondTo.getHeight() + CustOpts.VER_GAP);
        
        scrPane.setBounds(lblFrom.getX(), tfdSecondTo.getY() + tfdSecondTo.getHeight() + CustOpts.VER_GAP * 2,
        		btnPrint.getX() + btnPrint.getWidth(), 495);
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
    public void actionPerformed(ActionEvent e) {
    	PrintService.exePrintReport(printerIP, formattedString);
    	//todo delete relevant record.
    	StringBuilder sql = new StringBuilder("update Bill set status = ").append(DBConsts.deleted)
    			.append(" where createTime >= '").append(startTime).append("'")
    			.append(" and createTime <= '").append(endTime).append("'");
    	try {
    		PIMDBModel.getStatement().executeUpdate(sql.toString());
    	}catch(Exception exp) {
    		L.e("Report", " exception when trying to delete records from db", exp);
    	}
    }
    
	@Override
	public void valueChanged(ListSelectionEvent e) {}

	@Override
	public void stateChanged(ChangeEvent e) {
		startTime = new StringBuilder();
    	startTime.append(tfdYearFrom.getText());
    	startTime.append("-");
    	startTime.append(tfdMonthFrom.getText());
    	startTime.append("-");
    	startTime.append(tfdDayFrom.getText());
    	String startDate = startTime.toString();
    	startTime.append(" 00:00:00");

    	endTime = new StringBuilder();
    	endTime.append(tfdYearTo.getText());
    	endTime.append("-");
    	endTime.append(tfdMonthTo.getText());
    	endTime.append("-");
    	endTime.append(tfdDayTo.getText());
    	String endDate = endTime.toString();
    	endTime.append(" 23:59:59");
    	
    	bills = queryBillList(startTime.toString(), endTime.toString());
    	HashMap<String, ArrayList<Bill>> map = divideIntoMap(bills); 
		printerIP = BarFrame.menuPanel.getPrinters()[0].getIp();

		StringBuilder wholeContent = new StringBuilder();
		//title
		int width = BarUtil.getPreferedWidth();
		
		String saleSummary = "Sale Summary";
		wholeContent.append("\n");
		wholeContent.append("\n");
		String emptySpaceStr = BarUtil.generateString((width - saleSummary.length())/2, " ");
		wholeContent.append(emptySpaceStr).append(saleSummary).append("\n");
		
        //times
        wholeContent.append(startTime).append(" To ").append(endTime);;
        wholeContent.append("\n");
		
        int totalQT = 0;
        int totalMoney = 0;
		for (Entry<String, ArrayList<Bill>> entry : map.entrySet()) {
			wholeContent.append(PrintService.getSeperatorLine(1, width)).append("\n");
	  		int salesGrossCount = 0;
	  		int salesGrossAmount = 0;
	    	
	  		for (Bill bill : entry.getValue()) {
	  			int status = bill.getStatus();
				salesGrossCount++;
				salesGrossAmount += bill.getCashReceived() + bill.getCashback();
	  		}

	  		float net = salesGrossAmount * 100 /(100 + BarOption.getGST() + BarOption.getQST());
	  		float GST = net * BarOption.getGST()/100;
	  		float QST = net * BarOption.getQST()/100;
			
	  		//name and qt
	  		wholeContent.append(entry.getKey()).append("(").append(salesGrossCount).append(")\n");
			//Net
			String netIncome = BarUtil.formatMoney(net / 100.0);
			wholeContent.append("Net").append(BarUtil.generateString(width - 3 - netIncome.length(), " ")).append(netIncome).append("\n");
			//GST
			String strGST = BarUtil.formatMoney(GST / 100.0);
			wholeContent.append("GST").append(BarUtil.generateString(width - 3 - strGST.length(), " ")).append(strGST).append("\n");
			//QST
			String strQST = BarUtil.formatMoney(QST / 100.0);
			wholeContent.append("QST").append(BarUtil.generateString(width - 3 - strQST.length(), " ")).append(strQST).append("\n");
			
			//total
			String total = BarUtil.formatMoney(salesGrossAmount / 100.0);
			wholeContent.append("Total").append(BarUtil.generateString(width - total.length() - 5, " ")).append(total).append("\n");

	  		totalQT += salesGrossCount;
	  		totalMoney += salesGrossAmount;
		}
		
		float net = totalMoney * 100 /(100 + BarOption.getGST() + BarOption.getQST());
  		float GST = net * BarOption.getGST()/100;
  		float QST = net * BarOption.getQST()/100;
  		
		wholeContent.append(PrintService.getSeperatorLine(0, width)).append("\n");
		wholeContent.append("Total QT").append("(").append(totalQT).append(")\n");
		//Net
		String netIncome = BarUtil.formatMoney(net / 100.0);
		wholeContent.append("Net").append(BarUtil.generateString(width - 3 - netIncome.length(), " ")).append(netIncome).append("\n");
		//GST
		String strGST = BarUtil.formatMoney(GST / 100.0);
		wholeContent.append("GST").append(BarUtil.generateString(width - 3 - strGST.length(), " ")).append(strGST).append("\n");
		//QST
		String strQST = BarUtil.formatMoney(QST / 100.0);
		wholeContent.append("QST").append(BarUtil.generateString(width - 3 - strQST.length(), " ")).append(strQST).append("\n");
		//total
		String total = BarUtil.formatMoney(totalMoney / 100.0);
		wholeContent.append("Total").append(BarUtil.generateString(width - total.length() - 5, " ")).append(total).append("\n");
		

		
		txpPreview.setText(wholeContent.toString());
		formattedString = new ArrayList<String>();
		formattedString.add(wholeContent.toString());

		formattedString.add("\n\n\n");
		formattedString.add("cut");
	}
	
    private HashMap<String, ArrayList<Bill>> divideIntoMap(ArrayList<Bill> bills2) {
    	HashMap<String, ArrayList<Bill>> map = new HashMap<String, ArrayList<Bill>>();
    	for (Bill bill : bills2) {
			String waiterName = bill.getEmployeeName();
			if(map.containsKey(waiterName)) {
				ArrayList<Bill> list = map.get(waiterName);
				list.add(bill);
			}else {
				ArrayList<Bill> list = new ArrayList<Bill>();
				list.add(bill);
				map.put(waiterName, list);
			}
		}
		return map;
	}

	@Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarFrame.consts.SaleReport());
        setResizable(false);
        setModal(false);

        // 初始化－－－－－－－－－－－－－－－－
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
        
        lblHourFrom = new JLabel("HH");
        lblMinuteFrom = new JLabel("mm");
        lblSecondFrom = new JLabel("SS");
        lblHourTo = new JLabel("HH");
        lblMinuteTo = new JLabel("mm");
        lblSecondTo = new JLabel("SS");
        tfdHourFrom = new JTextField();
        tfdMinuteFrom = new JTextField();
        tfdSecondFrom = new JTextField();
        tfdHourTo = new JTextField();
        tfdMinuteTo = new JTextField();
        tfdSecondTo = new JTextField();
        
        btnPrint = new JButton(BarFrame.consts.PRINT());
        txpPreview = new JTextArea();
        scrPane = new JScrollPane(txpPreview);
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
        
        lblHourFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblMinuteFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblSecondFrom.setHorizontalTextPosition(SwingConstants.CENTER);
        lblHourTo.setHorizontalTextPosition(SwingConstants.CENTER);
        lblMinuteTo.setHorizontalTextPosition(SwingConstants.CENTER);
        lblSecondTo.setHorizontalTextPosition(SwingConstants.CENTER);
        
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        txpPreview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        txpPreview.setEditable(false);
        
        // 布局---------------
        setBounds((CustOpts.SCRWIDTH - 390) / 2, (CustOpts.SCRHEIGHT - 660) / 2, 390, 660); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 搭建－－－－－－－－－－－－－
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

        getContentPane().add(lblHourFrom);
        getContentPane().add(lblMinuteFrom);
        getContentPane().add(lblSecondFrom);
        getContentPane().add(lblHourTo);
        getContentPane().add(lblMinuteTo);
        getContentPane().add(lblSecondTo);
        getContentPane().add(tfdHourFrom);
        getContentPane().add(tfdMinuteFrom);
        getContentPane().add(tfdSecondFrom);
        getContentPane().add(tfdHourTo);
        getContentPane().add(tfdMinuteTo);
        getContentPane().add(tfdSecondTo);
        
        getContentPane().add(btnPrint);
        getContentPane().add(scrPane);
        // 加监听器－－－－－－－－
        btnPrint.addActionListener(this);
        addChangeListener(tfdYearFrom, this);
        addChangeListener(tfdMonthFrom,this);
        addChangeListener(tfdDayFrom,this);
        addChangeListener(tfdYearTo,this);
        addChangeListener(tfdMonthTo,this);
        addChangeListener(tfdDayTo,this);
        
        addChangeListener(tfdHourFrom,this);
        addChangeListener(tfdMinuteFrom,this);
        addChangeListener(tfdSecondFrom,this);
        addChangeListener(tfdHourTo,this);
        addChangeListener(tfdMinuteTo,this);
        addChangeListener(tfdSecondTo,this);
        getContentPane().addComponentListener(this);
        
        //initContent
        String endNow = BarOption.df.format(new Date());
		int p = endNow.indexOf(" ");
		String startTime = endNow.substring(0, p + 1) + BarOption.getStartTime();
		initContent(startTime, endNow);
    }

    //fill the dataselection area
	private void initContent(String startTime, String endTime) {
	      //start date and time-----------------
	      int p = startTime.indexOf(" ");
	      String startDate = startTime.substring(0, p);
	      startTime = startTime.substring(p+1);
	      
	      p = startDate.indexOf("-");
	      tfdYearFrom.setText(startDate.substring(0, p));
	      startDate = startDate.substring(p + 1);
	      p = startDate.indexOf("-");
	      tfdMonthFrom.setText(startDate.substring(0, p));
	      tfdDayFrom.setText(startDate.substring(p + 1));
	      
	      p = startTime.indexOf(":");
	      tfdHourFrom.setText(startTime.substring(0, p));
	      startTime = startTime.substring(p + 1);
	      p = startTime.indexOf(":");
	      tfdMinuteFrom.setText(startTime.substring(0, p));
	      tfdSecondFrom.setText(startTime.substring(p + 1));
	      
	      //end date and time-------------------
	      p = endTime.indexOf(" ");
	      String endDate = endTime.substring(0, p);
	      endTime = endTime.substring(p+1);
	      
	      p = endDate.indexOf("-");
	      tfdYearTo.setText(endDate.substring(0, p));
	      endDate = endDate.substring(p + 1);
	      p = endDate.indexOf("-");
	      tfdMonthTo.setText(endDate.substring(0, p));
	      tfdDayTo.setText(endDate.substring(p + 1));

	      p = endTime.indexOf(":");
	      tfdHourTo.setText(endTime.substring(0, p));
	      endTime = endTime.substring(p + 1);
	      p = endTime.indexOf(":");
	      tfdMinuteTo.setText(endTime.substring(0, p));
	      tfdSecondTo.setText(endTime.substring(p + 1));
    }
    
    public ArrayList<Bill> queryBillList(String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("select * from bill, employee where createTime >= '").append(startTime)
        		.append("' and createTime <= '").append(endTime).append("'")
        		.append(" and bill.employeeId = employee.id")
        		.append(" and bill.status < ").append(DBConsts.expired);
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

    JLabel lblHourFrom;
    JLabel lblMinuteFrom;
    JLabel lblSecondFrom;
    JLabel lblHourTo;
    JLabel lblMinuteTo;
    JLabel lblSecondTo;
    
    JTextField tfdHourFrom;
    JTextField tfdMinuteFrom;
    JTextField tfdSecondFrom;
    JTextField tfdHourTo;
    JTextField tfdMinuteTo;
    JTextField tfdSecondTo;
    
    JTextArea txpPreview;
    JScrollPane scrPane;
    private JButton btnPrint;
	
	/**
	 * Installs a listener to receive notification when the text of any
	 * {@code JTextComponent} is changed. Internally, it installs a
	 * {@link DocumentListener} on the text component's {@link Document},
	 * and a {@link PropertyChangeListener} on the text component to detect
	 * if the {@code Document} itself is replaced.
	 * 
	 * @param text any text component, such as a {@link JTextField}
	 *        or {@link JTextArea}
	 * @param changeListener a listener to receieve {@link ChangeEvent}s
	 *        when the text is changed; the source object for the events
	 *        will be the text component
	 * @throws NullPointerException if either parameter is null
	 */
	public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
	    Objects.requireNonNull(text);
	    Objects.requireNonNull(changeListener);
	    DocumentListener dl = new DocumentListener() {
	        private int lastChange = 0, lastNotifiedChange = 0;

	        @Override
	        public void insertUpdate(DocumentEvent e) {
	            changedUpdate(e);
	        }

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	            changedUpdate(e);
	        }

	        @Override
	        public void changedUpdate(DocumentEvent e) {
	            lastChange++;
	            SwingUtilities.invokeLater(() -> {
	                if (lastNotifiedChange != lastChange) {
	                    lastNotifiedChange = lastChange;
	                    changeListener.stateChanged(new ChangeEvent(text));
	                }
	            });
	        }
	    };
	    text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
	        Document d1 = (Document)e.getOldValue();
	        Document d2 = (Document)e.getNewValue();
	        if (d1 != null) d1.removeDocumentListener(dl);
	        if (d2 != null) d2.addDocumentListener(dl);
	        dl.changedUpdate(null);
	    });
	    Document d = text.getDocument();
	    if (d != null) d.addDocumentListener(dl);
	}

}
