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

public class RefundListDlg extends JDialog
	implements ICASDialog, ActionListener, ComponentListener, KeyListener{
	/** Creates a new instance of ContactDialog
	 * @called by PasteAction 为Copy邮件到联系人应用。
	 */
	public RefundListDlg(JFrame pParent){
		super(pParent, true);
		initDialog();
	}
	public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
    	Object o = e.getSource();
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
    public void keyReleased(KeyEvent e){}
	/* 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局，
	 * 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
	 * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
	 */
	public void reLayout(){
		srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
				getWidth() - CustOpts.SIZE_EDGE*2 - CustOpts.HOR_GAP * 2,
				getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP*3 - CustOpts.BTN_HEIGHT);
		
		btnClose.setBounds(getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.BTN_WIDTH,
				srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP,
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);//关闭
		btnUnFocus.setBounds(btnClose.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, btnClose.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnFocus.setBounds(btnUnFocus.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, btnUnFocus.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		
		IPIMTableColumnModel tTCM = tblContent.getColumnModel();
		tTCM.getColumn(0).setPreferredWidth(150);
		tTCM.getColumn(1).setPreferredWidth(130);
		tTCM.getColumn(2).setPreferredWidth(50);
		tTCM.getColumn(3).setPreferredWidth(60);
		tTCM.getColumn(4).setPreferredWidth(srpContent.getWidth() - 390 - 4);
		
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
		btnUnFocus.removeActionListener(this);
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
			if(tRowAry.length < 2){
				JOptionPane.showMessageDialog(this, PosDlgConst.ValidateFucusAction);//选中项目太少！请先用鼠标选中多条记录，然后点击聚焦选中按钮，重点对选中的记录进行观察。
				return;
			}
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
		}else if(o == btnUnFocus){//如果点击了Unfocus按钮，则取消Focus动作，返回到显示全部内容的状态。
			initTable();
			reLayout();
		}
	}

	public Container getContainer(){
		return getContentPane();
	}
    
	private void initDialog(){
		setTitle(PosDlgConst.RefundRecs);
		
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
		btnUnFocus = new JButton(PosDlgConst.UnFocus);
		
		//properties
		btnClose.setMnemonic('o');
		btnClose.setMargin(new Insets(0,0,0,0));
		btnFocus.setMnemonic('F');
		btnFocus.setMargin(btnClose.getMargin());
		btnUnFocus.setMnemonic('U');
		btnUnFocus.setMargin(btnClose.getMargin());
		
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
		getContentPane().add(btnUnFocus);
		
		//加监听器－－－－－－－－
		btnClose.addActionListener(this);
		btnFocus.addActionListener(this);
		btnUnFocus.addActionListener(this);
		btnClose.addKeyListener(this);
		btnFocus.addKeyListener(this);
		btnUnFocus.addKeyListener(this);
		
		getContentPane().addComponentListener(this);
		//initContents--------------
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				initPordAndEmploy();
				initTable();
			}
		});
	}
	
	private void initTable(){
		Object[][] tValues = null;
		String sql = "select TIME, PRODUCTID, AMOUNT, TOLTALPRICE, PROFIT from refund";
		try{
    		ResultSet rs =  PIMDBModel.getReadOnlyStatement().executeQuery(sql);
    		rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();
			tValues = new Object[tmpPos][header.length];
			rs.beforeFirst();
			tmpPos = 0;
			while (rs.next()){
				tValues[tmpPos][0] = rs.getString("TIME");
				tValues[tmpPos][1] = prodNameAry[CASUtility.getIndexInAry(prodIdAry, rs.getInt("PRODUCTID"))];
				tValues[tmpPos][2] = Integer.valueOf(rs.getInt("AMOUNT"));
				tValues[tmpPos][3] = Float.valueOf((float)(rs.getInt("TOLTALPRICE")/100.0));
				tValues[tmpPos][4] = Float.valueOf((float)(rs.getInt("PROFIT")/100.0));
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
	private void initPordAndEmploy(){
    	//for the service.
    	String sql = "select ID, SUBJECT from Product";
    	try{
    		ResultSet rs =  PIMDBModel.getReadOnlyStatement().executeQuery(sql);
    		rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();
			prodIdAry = new int[tmpPos];
			prodNameAry = new String[tmpPos];
			rs.beforeFirst();

			tmpPos = 0;
			while (rs.next()){
				prodIdAry[tmpPos] = rs.getInt("ID");
				prodNameAry[tmpPos] = rs.getString("SUBJECT");
				tmpPos++;
			}
			rs.close();//关闭
    	}catch(SQLException e){
    		ErrorUtil.write(e);
    	}
	}
	
	private String[] header = new String[]{
			PosDlgConst.TIME, 			//"时间"
			PosDlgConst.Product, 		//"产品"
			PosDlgConst.Count, 			//"数量"
			PosDlgConst.Subtotal, 		//"退款"
			PosDlgConst.Porfit};		//"盈利"
	
	int[] employIdAry;
	String[] employNameAry;
	int[] prodIdAry;
	String[] prodNameAry;
	PIMTable tblContent;
	PIMScrollPane srpContent;
	private JButton btnFocus;
	private JButton btnUnFocus;
	private JButton btnClose;
}
