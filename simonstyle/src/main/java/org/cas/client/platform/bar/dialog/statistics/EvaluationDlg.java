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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.bar.BarUtil;
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

public class EvaluationDlg  extends JDialog
	implements ICASDialog, ActionListener, ComponentListener, KeyListener{
	/** Creates a new instance of ContactDialog
	 * @called by PasteAction 为Copy邮件到联系人应用。
	 */
	public EvaluationDlg(JFrame pParent){
		super(pParent, true);
		initDialog();
	}
	@Override
	public void keyTyped(KeyEvent e){}
    @Override
	public void keyPressed(KeyEvent e){
    	Object o = e.getSource();
    	if(o == tfdMoneyCurrent){
    		switch (e.getKeyCode()){
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
    }
    @Override
	public void keyReleased(KeyEvent e){}
	/* 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局，
	 * 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
	 * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
	 */
	@Override
	public void reLayout(){
		srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
				getWidth() - CustOpts.SIZE_EDGE*2 - CustOpts.HOR_GAP * 2,
				getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP*3 - CustOpts.BTN_HEIGHT);
		
		lblMoneyLeft.setBounds(srpContent.getX(), srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP,
				lblMoneyLeft.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		tfdMoneyLeft.setBounds(lblMoneyLeft.getX() + lblMoneyLeft.getWidth(), lblMoneyLeft.getY(),
				50, CustOpts.BTN_HEIGHT);
		lblUnit2.setBounds(tfdMoneyLeft.getX() + tfdMoneyLeft.getWidth(), tfdMoneyLeft.getY(),
				lblUnit2.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		
		lblMoneyCurrent.setBounds(lblUnit2.getX() + lblUnit2.getWidth() + CustOpts.HOR_GAP * 3,	lblUnit2.getY(),
				lblMoneyCurrent.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		tfdMoneyCurrent.setBounds(lblMoneyCurrent.getX() + lblMoneyCurrent.getWidth(), lblMoneyCurrent.getY(),
				50, CustOpts.BTN_HEIGHT);
		lblUnit.setBounds(tfdMoneyCurrent.getX() + tfdMoneyCurrent.getWidth(), tfdMoneyCurrent.getY(),
				lblUnit.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		
		btnClose.setBounds(getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.BTN_WIDTH,
				lblMoneyCurrent.getY(),CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);//关闭
		btnFocus.setBounds(btnClose.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, btnClose.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		
		IPIMTableColumnModel tTCM = tblContent.getColumnModel();
		tTCM.getColumn(0).setPreferredWidth(70);
		tTCM.getColumn(1).setPreferredWidth(130);
		tTCM.getColumn(2).setPreferredWidth(130);
		tTCM.getColumn(3).setPreferredWidth(60);
		tTCM.getColumn(4).setPreferredWidth(60);
		tTCM.getColumn(5).setPreferredWidth(srpContent.getWidth() - 450 - 4);
		
    	validate();
	}
	@Override
	public PIMRecord getContents(){return null;}
	@Override
	public boolean setContents(PIMRecord prmRecord){return true;}
	@Override
	public void makeBestUseOfTime(){}
	@Override
	public void addAttach(File[] file, Vector actualAttachFiles){}
	@Override
	public PIMTextPane getTextPane(){return null;}
	
	@Override
	public void release(){
		btnClose.removeActionListener(this);
		btnFocus.removeActionListener(this);
		dispose();//对于对话盒，如果不加这句话，就很难释放掉。
		System.gc();//@TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
	}

    @Override
	public void componentResized(ComponentEvent e){
    	reLayout();
    };
    @Override
	public void componentMoved(ComponentEvent e){};
    @Override
	public void componentShown(ComponentEvent e){};
    @Override
	public void componentHidden(ComponentEvent e){};

	@Override
	public void actionPerformed(ActionEvent e){
		Object o = e.getSource();
		if(o == btnClose){
			float tMoneyChange = -1;//检查输入的钱箱余额的格式是否正确。
			try{
				tMoneyChange = Float.parseFloat(tfdMoneyCurrent.getText());
			}catch(Exception exp){
				JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
				tfdMoneyCurrent.selectAll();
				return;
			}
			CustOpts.custOps.setKeyAndValue(PosDlgConst.Shoestring, String.valueOf(CASUtility.getPriceByCent(tMoneyChange)));
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
		}
	}

	@Override
	public Container getContainer(){
		return getContentPane();
	}
    
	private void initDialog(){
		setTitle(PosDlgConst.WorkRecs);
		
		//初始化－－－－－－－－－－－－－－－－
		tblContent = new PIMTable();//显示字段的表格,设置模型
		srpContent = new PIMScrollPane(tblContent);
		lblMoneyCurrent = new JLabel(PosDlgConst.MoneyInBox);
		lblMoneyLeft = new JLabel(PosDlgConst.LeftMoney);
		
		int tShoestring = 0;
		try{
			tShoestring = Integer.parseInt((String)CustOpts.custOps.getValue(PosDlgConst.Shoestring));
		}catch(Exception exp){
		}
		tfdMoneyCurrent = new JTextField(BarUtil.formatMoney(tShoestring / 100.0));
		tfdMoneyLeft = new JTextField();
		lblUnit = new JLabel(PosDlgConst.Unit);
		lblUnit2 = new JLabel(PosDlgConst.Unit);
		btnClose = new JButton(DlgConst.FINISH_BUTTON);
		btnFocus = new JButton(PosDlgConst.Focus);
		
		//properties
		btnClose.setMnemonic('o');
		btnClose.setMargin(new Insets(0,0,0,0));
		btnFocus.setMnemonic('F');
		btnFocus.setMargin(btnClose.getMargin());
		
		tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblContent.setAutoscrolls(true);
		tblContent.setRowHeight(20);
		tblContent.setBorder(new JTextField().getBorder());
		tblContent.setFocusable(false);
		
		JLabel tLbl = new JLabel();
		tLbl.setOpaque(true);
		tLbl.setBackground(Color.GRAY);
		srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
		tfdMoneyLeft.setEditable(false);
		getRootPane().setDefaultButton(btnClose);
		
		//布局---------------
		setBounds((CustOpts.SCRWIDTH - 540)/2, (CustOpts.SCRHEIGHT - 300)/2, 540, 300);	//对话框的默认尺寸。
		getContentPane().setLayout(null);
		
		//搭建－－－－－－－－－－－－－
		getContentPane().add(srpContent);
		getContainer().add(lblMoneyCurrent);
		getContainer().add(tfdMoneyCurrent);
		getContainer().add(lblUnit);
		getContainer().add(lblMoneyLeft);
		getContainer().add(tfdMoneyLeft);
		getContainer().add(lblUnit2);
		getContentPane().add(btnClose);
		getContentPane().add(btnFocus);
		
		//加监听器－－－－－－－－
		btnClose.addActionListener(this);
		btnFocus.addActionListener(this);
		tfdMoneyCurrent.addKeyListener(this);
		getContentPane().addComponentListener(this);
		//initContents--------------
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				initTable();
				initMoneyInBox2();
				tfdMoneyCurrent.grabFocus();
			}
		});
	}
	
	private void initTable(){
		Object[][] tValues = null;
		String sql = "select * from evaluation";
		
		try{
    		ResultSet rs =  PIMDBModel.getReadOnlyStatement().executeQuery(sql);
    		rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();
			tValues = new Object[tmpPos][header.length];
			rs.beforeFirst();
			tmpPos = 0;
			while (rs.next()){
				tValues[tmpPos][0] = rs.getString("SUBJECT");
				tValues[tmpPos][1] = rs.getString("startTime");
				tValues[tmpPos][2] = rs.getString("endTime");
				tValues[tmpPos][3] = Float.valueOf((float)(rs.getInt("target") / 100.0));
				tValues[tmpPos][4] = Float.valueOf((float)(rs.getInt("receive") / 100.0));
				tValues[tmpPos][5] = Float.valueOf((float)(rs.getInt("profit") / 100.0));
				tmpPos++;
			}
			rs.close();//关闭
    	}catch(SQLException e){
    		ErrorUtil.write(e);
    	}
    	
		tblContent.setDataVector(tValues, header);
		DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
		tCellRender.setOpaque(true);
		tCellRender.setBackground(Color.LIGHT_GRAY);
		tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
	}
	
	private void initMoneyInBox2(){
		if(tblContent.getRowCount() > 0){
			int tShoestring = 0;	
			try{
				tShoestring = Integer.parseInt((String)CustOpts.custOps.getValue(PosDlgConst.Shoestring));
			}catch(Exception exp){
			}
			Float tReceived = (Float)tblContent.getValueAt(tblContent.getRowCount() - 1, 4);
			tfdMoneyLeft.setText(BarUtil.formatMoney(
					Math.round(tShoestring - tReceived.floatValue() * 100.0) / 100.0));
		}
	}
	
	private String[] header = new String[]{
			PosDlgConst.Operator, 	//"操作员"
			PosDlgConst.StartTime, 	//"开始时间"
			PosDlgConst.EndTime, 	//"结束时间"
			PosDlgConst.Calculate, 	//"结算"
			PosDlgConst.Receive, 	//"收银"
			PosDlgConst.Profit}; 	//"盈利"
	
	PIMTable tblContent;
	PIMScrollPane srpContent;
	private JLabel lblMoneyCurrent;
	private JTextField tfdMoneyCurrent;
	private JLabel lblUnit;
	private JLabel lblMoneyLeft;
	private JTextField tfdMoneyLeft;
	private JLabel lblUnit2;
	private JButton btnFocus;
	private JButton btnClose;
}
