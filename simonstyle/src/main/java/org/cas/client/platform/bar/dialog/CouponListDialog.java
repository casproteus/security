package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.net.bean.Coupon;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;

public class CouponListDialog  extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener,
MouseListener {
	final int IDCOLUM = 0; //with column id field stays.
	
	private String[][] tableModel = null;
	
	/**
	* Creates a new instance of ContactDialog
	* 
	* @called by PasteAction 为Copy邮件到联系人应用。
	*/
	public CouponListDialog(JFrame pParent) {
		super(pParent, true);
		initDialog();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
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
	public void keyReleased(KeyEvent e) {}
	
	/*
	* 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
	* @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
	*/
	@Override
	public void reLayout() {
		srpContent.setBounds(CustOpts.HOR_GAP, 
				CustOpts.VER_GAP, 
				getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP  * 3, 
		        getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP * 4  - CustOpts.BTN_HEIGHT);
		
		btnClose.setBounds(getWidth() - CustOpts.HOR_GAP * 3 - CustOpts.BTN_WIDTH,
		        srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, 
		        CustOpts.BTN_WIDTH, 
		        CustOpts.BTN_HEIGHT);// 关闭
		btnAdd.setBounds(srpContent.getX(), btnClose.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnModify.setBounds(btnAdd.getX() + btnAdd.getWidth() + CustOpts.HOR_GAP, btnAdd.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnCopy.setBounds(btnModify.getX() + btnModify.getWidth() + CustOpts.HOR_GAP, btnModify.getY(), CustOpts.BTN_WIDTH,	CustOpts.BTN_HEIGHT);
		btnDelete.setBounds(btnCopy.getX() + btnCopy.getWidth() + CustOpts.HOR_GAP, btnCopy.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		
		IPIMTableColumnModel tTCM = tblContent.getColumnModel();
		tTCM.getColumn(0).setPreferredWidth(40);
		tTCM.getColumn(1).setPreferredWidth(120);
		tTCM.getColumn(2).setPreferredWidth(120);
		tTCM.getColumn(3).setPreferredWidth(75);
		tTCM.getColumn(4).setPreferredWidth(90);
		tTCM.getColumn(5).setPreferredWidth(60);
		validate();
	}
	
	@Override
	public PIMRecord getContents() {return null;}
	
	@Override
	public boolean setContents(PIMRecord prmRecord) {return true;}
	
	@Override
	public void makeBestUseOfTime() {}
	
	@Override
	public void addAttach(File[] file, Vector actualAttachFiles) {}
	
	@Override
	public PIMTextPane getTextPane() {return null;}
	
	@Override
	public void release() {
		btnClose.removeActionListener(this);
		btnAdd.removeActionListener(this);
		btnDelete.removeActionListener(this);
		btnModify.removeActionListener(this);
		btnCopy.removeActionListener(this);
		dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
		System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
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
		Object o = e.getSource();
		if (o == btnClose) {
		    dispose();
		} else if (o == btnAdd) {
		    CouponDlg tDlg = new CouponDlg(this);
		    tDlg.setVisible(true);
		    initTable();
		    reLayout();
		    int rows = tblContent.getRowCount();
		    tblContent.setSelectedRow(rows - 1);
		    tblContent.scrollToRect(rows - 1, 0);
		} else if(o == btnModify) {
			modifyCoupon();
		} else if(o == btnCopy) {
			int tRow = tblContent.getSelectedRow();
	        if(tRow < 0 || tRow >= tableModel.length) {
	        	JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
	        	return;
	        }
	        StringBuilder sql = new StringBuilder("INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('")
	        	.append(tableModel[tRow][1]).append("', 1, ").append(Math.round(Float.valueOf(tableModel[tRow][3]) * 100)).append(", '")
	        	.append(tableModel[tRow][2]).append("', ").append("%".equals(tableModel[tRow][4]) ? 1 : 0).append(", 0)");
	        try {
		        Statement smt = PIMDBModel.getStatement();
		        smt.executeUpdate(sql.toString());
	        } catch (SQLException exp) {
		        L.e("copy coupon", "Exception when copying a coupon: " + sql, exp);
		    }
	        initTable();
	        reLayout();
	        tblContent.setSelectedRow(tRow);
		    tblContent.scrollToRect(tblContent.getRowCount() - 1, 0);
		} else if (o == btnDelete) {
			int[] tRow = tblContent.getSelectedRows();
	        if(tRow.length == 0) {
	        	JOptionPane.showMessageDialog(this, BarFrame.consts.AtLeastOneShouldBeSelected());
	        	return;
	        }
	        
		    if (JOptionPane.showConfirmDialog(this, BarFrame.consts.COMFIRMDELETEACTION2(), BarFrame.consts.Operator(), JOptionPane.YES_NO_OPTION) != 0) {// 确定删除吗？
		        return;
		    }
		    
		    try {
		    	for (int i : tRow) {
				    StringBuilder sql = new StringBuilder("update hardware set status = ").append(DBConsts.deleted)
					    	.append(" where ID = ").append(tblContent.getValueAt(i, IDCOLUM));
			        PIMDBModel.getStatement().executeUpdate(sql.toString());
				}
		
		        initTable();
		        reLayout();
		    } catch (SQLException exp) {
		        L.e("copy coupon", "Exception when deleting a coupon: ", exp);
		    }
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
		    modifyCoupon();
		}
	}
	
	public void modifyCoupon() {
//		new LoginDlg(PosFrame.instance).setVisible(true);// 结果不会被保存到ini
//		if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
//			BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
//		    if (LoginDlg.USERTYPE >= 2) {// 进一步判断，如果新登陆是经理，弹出对话盒
		        int tRow = tblContent.getSelectedRow();
		        if(tRow < tableModel.length && tRow >= 0) {
		            // 不合适重用OpenAction。因为OpenAction的结果是调用View系统的更新机制。而这里需要的是更新list对话盒。
		        	Coupon coupon = new Coupon();
		        	coupon.setId(tableModel[tRow][0]);
		        	coupon.setCouponCode(tableModel[tRow][1]);
		        	coupon.setProductCode(tableModel[tRow][2]);
		        	coupon.setPrice(tableModel[tRow][3]);
		        	coupon.setCategory(tableModel[tRow][4]);
		        	coupon.setStatus(tableModel[tRow][5]);
		        	
		        	CouponDlg couponDlg = new CouponDlg(this);
		        	couponDlg.initContent(coupon);
		        	couponDlg.setVisible(true);
		            initTable();
		            reLayout();
		        }else {
		        	JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
		        }
//		    }
//		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public Container getContainer() {return getContentPane();}
	
	private void initDialog() {
		setTitle(BarFrame.consts.COUPON());
		
		// 初始化－－－－－－－－－－－－－－－－
		tblContent = new PIMTable();// 显示字段的表格,设置模型
		srpContent = new PIMScrollPane(tblContent);
		btnClose = new JButton(BarFrame.consts.Close());
		btnAdd = new JButton(BarFrame.consts.Add());//NewUser());
		btnModify = new JButton(BarFrame.consts.Modify());
		btnCopy = new JButton(BarFrame.consts.Copy());
		btnDelete = new JButton(BarFrame.consts.Delete());
		// properties
		btnClose.setMnemonic('o');
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnAdd.setMnemonic('A');
		btnAdd.setMargin(btnClose.getMargin());
		btnModify.setMnemonic('M');
		btnModify.setMargin(btnClose.getMargin());
		btnCopy.setMnemonic('C');
		btnCopy.setMargin(btnClose.getMargin());
		btnDelete.setMnemonic('D');
		btnDelete.setMargin(btnClose.getMargin());
		
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
		
		// 布局---------------
		setBounds((CustOpts.SCRWIDTH - 540) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 540, 320); // 对话框的默认尺寸。
		getContentPane().setLayout(null);
		
		// 搭建－－－－－－－－－－－－－
		getContentPane().add(srpContent);
		getContentPane().add(btnClose);
		getContentPane().add(btnAdd);
		getContentPane().add(btnModify);
		getContentPane().add(btnCopy);
		getContentPane().add(btnDelete);
		
		// 加监听器－－－－－－－－
		btnClose.addActionListener(this);
		btnAdd.addActionListener(this);
		btnModify.addActionListener(this);
		btnCopy.addActionListener(this);
		btnDelete.addActionListener(this);
		btnClose.addKeyListener(this);
		btnAdd.addKeyListener(this);
		btnDelete.addKeyListener(this);
		tblContent.addMouseListener(this);
		getContentPane().addComponentListener(this);
		// initContents--------------
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        initTable();
		    }
		});
	}
	
	private void initTable() {
		StringBuilder sql = new StringBuilder("select * from hardware where category = 1 and status != ")
				.append(DBConsts.deleted);//.append(" order by name");
		
		try {
		    ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
		    rs.afterLast();
		    rs.relative(-1);
		    int tmpPos = rs.getRow();
		    tableModel = new String[tmpPos][6];
		    rs.beforeFirst();
		    tmpPos = 0;
		    while (rs.next()) {
		        tableModel[tmpPos][0] = rs.getString("ID");
		        tableModel[tmpPos][1] = rs.getString("NAME");
		        tableModel[tmpPos][2] = rs.getString("IP");
		        tableModel[tmpPos][3] = BarUtil.formatMoney(rs.getInt("LANGTYPE") / 100.0);
		        tableModel[tmpPos][4] = rs.getInt("STYLE") == 1 ? "%" : BarOption.getMoneySign();
		        tableModel[tmpPos][5] = rs.getInt("STATUS") == 1 ? "Y" : "";
		        tmpPos++;
		    }
		    rs.close();// 关闭
		} catch (SQLException e) {
		    ErrorUtil.write(e);
		}
		
		tblContent.setDataVector(tableModel, header);
		DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
		tCellRender.setOpaque(true);
		tCellRender.setBackground(Color.LIGHT_GRAY);
		tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
	}
	
	private String[] header = new String[] { 
		ContactDefaultViews.TEXTS[0],
		BarFrame.consts.couponCode(), // "Coupon Code"
	    BarFrame.consts.Product(), // "Product Code"
	    BarFrame.consts.Price(), // "Price"
	    BarFrame.consts.Categary(), // "Category" 0-discount price/1-discount percentage/2-discount to price/3-discount to price
	    BarFrame.consts.isRedeemed() // "Status"; 0-ready for deam/10-deamed/50-suspended/1000-deleted
	};
	
	PIMTable tblContent;
	PIMScrollPane srpContent;
	private JButton btnAdd;
	private JButton btnModify;
	private JButton btnCopy;
	private JButton btnDelete;
	private JButton btnClose;
}
