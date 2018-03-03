package org.cas.client.platform.pos.dialog.statistics;

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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pos.dialog.PosDlgConst;
import org.cas.client.resource.international.DlgConst;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

public class ProfitDlg extends JDialog
	implements ICASDialog, ActionListener, ComponentListener, KeyListener{
	/** Creates a new instance of ContactDialog
	 * @called by PasteAction 为Copy邮件到联系人应用。
	 */
	public ProfitDlg(JFrame pParent){
		super(pParent, true);
		initDialog();
	}
	public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){}
    public void keyReleased(KeyEvent e){}
	/* 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局，
	 * 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
	 * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
	 */
	public void reLayout(){
		srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 143,
				getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP*3 - CustOpts.BTN_HEIGHT);
		
		
		btnClose.setBounds(getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.BTN_WIDTH,
				srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP,
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);//关闭
		
		btnFocus.setBounds(srpContent.getX(), btnClose.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnYear.setBounds(btnFocus.getX() + btnFocus.getWidth() + CustOpts.HOR_GAP, btnFocus.getY(),
				CustOpts.BTN_WIDTH/2, CustOpts.BTN_HEIGHT);
		btnMonth.setBounds(btnYear.getX() + btnYear.getWidth() + CustOpts.HOR_GAP, btnYear.getY(),
				CustOpts.BTN_WIDTH/2, CustOpts.BTN_HEIGHT);
		btnDay.setBounds(btnMonth.getX() + btnMonth.getWidth() + CustOpts.HOR_GAP, btnMonth.getY(),
				CustOpts.BTN_WIDTH/2, CustOpts.BTN_HEIGHT);
		int p = btnDay.getX() + btnDay.getWidth() + CustOpts.HOR_GAP;
		btnChart.setBounds((btnClose.getX() - p)/2 + p - CustOpts.BTN_WIDTH/2, btnDay.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		IPIMTableColumnModel tTCM = tblContent.getColumnModel();
		tTCM.getColumn(0).setPreferredWidth(70);
		tTCM.getColumn(1).setPreferredWidth(70);
		
		if(chartPanel != null)
			chartPanel.setBounds(srpContent.getX() + srpContent.getWidth() + CustOpts.HOR_GAP, srpContent.getY(),
					btnClose.getX() + btnClose.getWidth() - srpContent.getX() - srpContent.getWidth() - CustOpts.HOR_GAP,
					srpContent.getHeight());
    	validate();
	}
	public PIMRecord getContents(){return null;}
	public boolean setContents(PIMRecord prmRecord){return true;}
	public void makeBestUseOfTime(){}
	public void addAttach(File[] file, Vector actualAttachFiles){}
	public PIMTextPane getTextPane(){return null;}
	
	public void release(){
		btnClose.removeActionListener(this);
		btnFocus.removeActionListener(this);
		dispose();//对于对话盒，如果不加这句话，就很难释放掉。
		System.gc();//@TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
	}

    public void componentResized(ComponentEvent e){
    	reLayout();
    };
    public void componentMoved(ComponentEvent e){};
    public void componentShown(ComponentEvent e){};
    public void componentHidden(ComponentEvent e){};

	public void actionPerformed(ActionEvent e){
		Object o = e.getSource();
		if(o == btnClose){
			dispose();
		}else if(o == btnFocus){
			int[] tRowAry = tblContent.getSelectedRows();
			if(tRowAry.length < 2)
				return;
			Object[][] tValues = new Object[tRowAry.length][tblContent.getColumnCount()];
			for(int i = 0; i < tRowAry.length; i++)
				for(int j = 0, len = tblContent.getColumnCount(); j < len; j++)
					tValues[i][j] = tblContent.getValueAt(tRowAry[i], j);
			//必须重新new一个Table，否则PIM会在绘制的时候报数组越界错误。原因不明。
			getContentPane().remove(srpContent);
			tblContent = new PIMTable();//显示字段的表格,设置模型
			srpContent = new PIMScrollPane(tblContent);
			
			tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tblContent.setAutoscrolls(true);
			tblContent.setRowHeight(20);
			tblContent.setBorder(new JTextField().getBorder());
			tblContent.setFocusable(false);

			getContentPane().add(srpContent);
			
			tblContent.setDataVector(tValues, header);
			DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
			tCellRender.setOpaque(true);
			tCellRender.setBackground(Color.LIGHT_GRAY);
			tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
			reLayout();
		}else if(o == btnYear){
			mode = 1;
			countProfit();
			btnChart.setEnabled(true);
		}else if(o == btnMonth){
			mode = 2;
			countProfit();
			btnChart.setEnabled(true);
		}else if(o == btnDay){
			mode = 3;
			countProfit();
			btnChart.setEnabled(true);
		}else if(o == btnChart){
			refreshChart();
		}
	}

	public Container getContainer(){
		return getContentPane();
	}
    
	private void initDialog(){
		setTitle(PosDlgConst.Static);
		
		//初始化－－－－－－－－－－－－－－－－
		tblContent = new PIMTable();//显示字段的表格,设置模型
		srpContent = new PIMScrollPane(tblContent);
		
		int tShoestring = 0;
		try{
			tShoestring = Integer.parseInt((String)CustOpts.custOps.getValue(PosDlgConst.Shoestring));
		}catch(Exception exp){
		}

		btnClose = new JButton(DlgConst.FINISH_BUTTON);
		btnFocus = new JButton(PosDlgConst.Focus);
		btnYear = new JButton(PosDlgConst.YEAR);
		btnMonth = new JButton(PosDlgConst.MONTH);
		btnDay = new JButton(PosDlgConst.DAY);
		btnChart = new JButton(PosDlgConst.RefreshChart);
		
		//properties
		btnClose.setMnemonic('o');
		btnClose.setMargin(new Insets(0,0,0,0));
		btnFocus.setMnemonic('F');
		btnFocus.setMargin(btnClose.getMargin());
		btnDay.setMargin(btnClose.getMargin());
		btnMonth.setMargin(btnClose.getMargin());
		btnYear.setMargin(btnClose.getMargin());
		btnChart.setMargin(btnClose.getMargin());
		btnChart.setEnabled(false);
		
		tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblContent.setAutoscrolls(true);
		tblContent.setRowHeight(20);
		tblContent.setBorder(new JTextField().getBorder());
		tblContent.setFocusable(false);
		
		JLabel tLbl = new JLabel();
		tLbl.setOpaque(true);
		tLbl.setBackground(Color.GRAY);
		srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
		getRootPane().setDefaultButton(btnClose);
		
		//布局---------------
		setBounds((CustOpts.SCRWIDTH - 540)/2, (CustOpts.SCRHEIGHT - 300)/2, 540, 300);	//对话框的默认尺寸。
		getContentPane().setLayout(null);
		
		//搭建－－－－－－－－－－－－－
		getContentPane().add(srpContent);
		getContentPane().add(btnClose);
		getContentPane().add(btnFocus);
		getContentPane().add(btnYear);
		getContentPane().add(btnMonth);
		getContentPane().add(btnDay);
		getContentPane().add(btnChart);
		
		//加监听器－－－－－－－－
		btnClose.addActionListener(this);
		btnFocus.addActionListener(this);
		btnDay.addActionListener(this);
		btnMonth.addActionListener(this);
		btnYear.addActionListener(this);
		btnChart.addActionListener(this);
		
		getContentPane().addComponentListener(this);
		//initContents--------------
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				initTable();
			}
		});
	}
	
	private void initTable(){
		tableValues = null;
		String sql = "select startTime, profit from evaluation";
		
		try{
    		ResultSet rs = PIMDBModel.getConection().createStatement(
    				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
    		rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();
			tableValues = new Object[tmpPos][header.length];
			rs.beforeFirst();
			tmpPos = 0;
			while (rs.next()){
				String tTime = rs.getString("startTime");
				tableValues[tmpPos][0] = tTime.substring(0, tTime.indexOf(" "));
				tableValues[tmpPos][1] = Float.valueOf((float)(rs.getInt("profit")/100.0));
				tmpPos++;
			}
			rs.close();//关闭
    	}catch(SQLException e){
    		ErrorUtil.write(e);
    	}
    	
		tblContent.setDataVector(tableValues, header);
		DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
		tCellRender.setOpaque(true);
		tCellRender.setBackground(Color.LIGHT_GRAY);
		tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
	}
	
	private void countProfit(){
		
		//判断是否有非时间的列被排序，如果有，则报错，或者直接给时间列进行排序。
		Vector tTimeVec = new Vector();
		Vector tProfVec = new Vector();
		String tOldTime = null;
		float tOldTotProf = 0;
		for(int i = 0; i < tableValues.length; i++){
			String tTime = (String)tableValues[i][0];
			if(mode == 1){
				int p = tTime.indexOf("-");
				tTime = tTime.substring(0, p);
			}else if(mode == 2){
				int p = tTime.indexOf("-");
				String tTime2 = tTime.substring(p + 1);
				int p2 = tTime2.indexOf("-");
				tTime = tTime.substring(0, p + p2 + 1);
			}else if(mode == 3){
			}
			if (tOldTime == null){
				tOldTime = tTime;
				tOldTotProf = ((Float)tableValues[i][1]).floatValue();
			}else if(tTime.equals(tOldTime)){
				tOldTotProf += ((Float)tableValues[i][1]).floatValue();
			}else{
				tTimeVec.add(tOldTime);
				tProfVec.add(tOldTotProf);
				tOldTime = tTime;
				tOldTotProf = ((Float)tableValues[i][1]).floatValue();
			}
		}
		tTimeVec.add(tOldTime);
		tProfVec.add(tOldTotProf);
		
		Object[][] tValues = new Object[tTimeVec.size()][header.length];
		for(int i = 0, len = tTimeVec.size(); i < len; i++){
			tValues[i][0] = tTimeVec.get(i);
			tValues[i][1] = tProfVec.get(i);
		}
		tblContent.setDataVector(tValues, header);
		DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
		tCellRender.setOpaque(true);
		tCellRender.setBackground(Color.LIGHT_GRAY);
		tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
		reLayout();
	}
	
	private void refreshChart(){
		if (chartPanel != null)
			getContentPane().remove(chartPanel);
		TimeSeries s = null;

		if(mode == 1)
			s = new TimeSeries("", Year.class);
		else if(mode == 2)
			s = new TimeSeries("", Month.class);
		else if(mode == 3)
			s = new TimeSeries("", Day.class);
		
		for(int i = 0, len = tblContent.getRowCount(); i < len; i++){
			String tTime = (String)tblContent.getValueAt(i, 0);
			if(mode == 1){
				s.add(new Year(Integer.parseInt(tTime.substring(0,4))), ((Float)tblContent.getValueAt(i, 1)).floatValue());
			}else if(mode == 2){
				String tYear = tTime.substring(0,4);
				String tMonth = tTime.substring(5);
				int tEnd = tMonth.indexOf("-");
				tMonth = tMonth.substring(0,tEnd);
				s.add(new Month(Integer.parseInt(tMonth), Integer.parseInt(tYear)), ((Float)tblContent.getValueAt(i, 1)).floatValue());
			}else if(mode == 3){
				String tYear = tTime.substring(0,4);
				String tMonth = tTime.substring(5);
				int tEnd = tMonth.indexOf("-");
				tMonth = tMonth.substring(0,tEnd);
				String tDay = tTime.substring(5);
				tDay = tDay.substring(tDay.indexOf("-") + 1);
				s.add(new Day(Integer.parseInt(tDay), Integer.parseInt(tMonth), Integer.parseInt(tYear)), ((Float)tblContent.getValueAt(i, 1)).floatValue());
			}
		}
		TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s);
        
//		dataset.setDomainIsPointsInTime(true);
		
		JFreeChart chart = createChart(dataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setMouseZoomable(true, false);

		getContentPane().add(chartPanel);
		reLayout();
	}
	private static JFreeChart createChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createTimeSeriesChart("", // title
				PosDlgConst.TIME, // x-axis label
				PosDlgConst.ProfitStatChange, // y-axis label
				dataset, // data
				true, // create legend?
				false, // generate tooltips?
				false // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(false);
			renderer.setBaseShapesFilled(false);
		}
		return chart;
	}
	
	private String[] header = new String[]{
			PosDlgConst.TIME, 	//"时间"
			PosDlgConst.ProfitStatChange}; 	//"盈利情况"
	
	private int mode;
	private Object[][] tableValues;
	private PIMTable tblContent;
	private PIMScrollPane srpContent;
	private JButton btnFocus;
	private JButton btnYear;
	private JButton btnMonth;
	private JButton btnDay;
	private JButton btnChart;
	private JButton btnClose;
	ChartPanel chartPanel;
}
