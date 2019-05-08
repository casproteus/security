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
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
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
import org.cas.client.platform.pos.dialog.PosFrame;

public class GiftCardListDialog  extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener,
MouseListener {
	final int IDCOLUM = 0; //with column id field stays.
	
	private String[][] tableModel = null;
	
	/**
	* Creates a new instance of ContactDialog
	* 
	* @called by PasteAction 为Copy邮件到联系人应用。
	*/
	public GiftCardListDialog(JFrame pParent) {
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
		
		btnHistory.setBounds(getWidth() - CustOpts.HOR_GAP * 3 - CustOpts.BTN_WIDTH,
		        srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, 
		        CustOpts.BTN_WIDTH, 
		        CustOpts.BTN_HEIGHT);// 关闭
		btnAdd.setBounds(srpContent.getX(), btnHistory.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnModify.setBounds(btnAdd.getX() + btnAdd.getWidth() + CustOpts.HOR_GAP, btnAdd.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		btnDelete.setBounds(btnModify.getX() + btnModify.getWidth() + CustOpts.HOR_GAP, btnModify.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		
		IPIMTableColumnModel tTCM = tblContent.getColumnModel();
		tTCM.getColumn(0).setPreferredWidth(40);
		tTCM.getColumn(1).setPreferredWidth(60);
		tTCM.getColumn(2).setPreferredWidth(130);
		tTCM.getColumn(3).setPreferredWidth(75);
		tTCM.getColumn(4).setPreferredWidth(60);
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
		btnDelete.removeActionListener(this);
		btnAdd.removeActionListener(this);
		btnHistory.removeActionListener(this);
		btnModify.removeActionListener(this);
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
		if (o == btnDelete) {
			new LoginDlg(PosFrame.instance).setVisible(true);// 结果不会被保存到ini
		    if (LoginDlg.PASSED != true || LoginDlg.USERTYPE != LoginDlg.SUPER_ADMIN) { // 如果用户选择了确定按钮。
		    	JOptionPane.showMessageDialog(this, BarFrame.consts.PasswordMakeSure());
		    	return;
		    }
		    
			int[] tRow = tblContent.getSelectedRows();
	        if(tRow.length == 0) {
	        	JOptionPane.showMessageDialog(this, BarFrame.consts.AtLeastOneShouldBeSelected());
	        	return;
	        }
	        
		    if (JOptionPane.showConfirmDialog(this, BarFrame.consts.COMFIRMDELETEACTION2(), BarFrame.consts.Operator(), JOptionPane.YES_NO_OPTION) != 0) {// 纭®瀹氬垹闄ゅ悧锛
		        return;
		    }
		    
		    try {
		    	for (int i : tRow) {
				    StringBuilder sql = new StringBuilder("update hardware set status = ").append(DBConsts.deleted)
					    	.append(" where ID = ").append(tblContent.getValueAt(i, IDCOLUM));
			        PIMDBModel.getStatement().executeUpdate(sql.toString());
				}
		
		        initTable(false);
		        reLayout();
		    } catch (SQLException exp) {
		        L.e("copy coupon", "Exception when deleting a coupon: ", exp);
		    }
		} else if (o == btnAdd) {
		    GiftCardDlg tDlg = new GiftCardDlg(this);
		    tDlg.setVisible(true);
		    initTable(false);
		    reLayout();
		    int rows = tblContent.getRowCount();
		    tblContent.setSelectedRow(rows - 1);
		    tblContent.scrollToRect(rows - 1, 0);
		} else if(o == btnModify) {
			modifyGiftCard();
		} else if (o == btnHistory) {
			new LoginDlg(PosFrame.instance).setVisible(true);// 结果不会被保存到ini
		    if (LoginDlg.PASSED != true || LoginDlg.USERTYPE != LoginDlg.SUPER_ADMIN) { // 如果用户选择了确定按钮。
		    	JOptionPane.showMessageDialog(this, BarFrame.consts.PasswordMakeSure());
		    	return;
		    }
		    initTable(true);
		    reLayout();
		    int rows = tblContent.getRowCount();
		    tblContent.setSelectedRow(rows - 1);
		    tblContent.scrollToRect(rows - 1, 0);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
		    modifyGiftCard();
		}
	}
	
	public void modifyGiftCard() {
//		new LoginDlg(PosFrame.instance).setVisible(true);// 结果不会被保存到ini
//		if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
//			BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
//		    if (LoginDlg.USERTYPE >= 2) {// 进一步判断，如果新登陆是经理，弹出对话盒
		        int tRow = tblContent.getSelectedRow();
		        if(tRow < tableModel.length && tRow >= 0) {
		            // 不合适重用OpenAction。因为OpenAction的结果是调用View系统的更新机制。而这里需要的是更新list对话盒。
		        	String id = tableModel[tRow][0];
		        	//non-expired record check. we do not allow to modify a history.
		        	try {
	        			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery("select * from hardware where id = " + id);
				        rs.beforeFirst();
						rs.next();
				        if(rs.getInt("Status") != DBConsts.original) {
				        	JOptionPane.showMessageDialog(this, BarFrame.consts.InvalidInput());
				        	return;
				        }
		        	}catch(Exception e) {
		        		L.e("Gift card list dialog", "exception when searching a hardware record with id:" + id, e);
		        	}
		        	
		        	Coupon giftCard = new Coupon();
		        	giftCard.setId(id);
		        	giftCard.setCouponCode(tableModel[tRow][1]);
		        	giftCard.setProductCode(tableModel[tRow][2]);
		        	giftCard.setPrice(tableModel[tRow][3]);
		        	
		        	GiftCardDlg giftCardDlg = new GiftCardDlg(this);
		        	giftCardDlg.initContent(giftCard);
		        	giftCardDlg.setVisible(true);
		            initTable(false);
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
		setTitle(BarFrame.consts.GIFTCARD());
		
		// 初始化－－－－－－－－－－－－－－－－
		tblContent = new PIMTable();// 显示字段的表格,设置模型
		srpContent = new PIMScrollPane(tblContent);
		btnDelete = new JButton(BarFrame.consts.Delete());
		btnAdd = new JButton(BarFrame.consts.Add());//NewUser());
		btnModify = new JButton(BarFrame.consts.Modify());
		btnHistory = new JButton(BarFrame.consts.History());
		// properties
		btnDelete.setMnemonic('o');
		btnDelete.setMargin(new Insets(0, 0, 0, 0));
		btnAdd.setMnemonic('A');
		btnAdd.setMargin(btnDelete.getMargin());
		btnModify.setMnemonic('M');
		btnModify.setMargin(btnDelete.getMargin());
		btnHistory.setMnemonic('D');
		btnHistory.setMargin(btnDelete.getMargin());
		
		tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblContent.setAutoscrolls(true);
		tblContent.setRowHeight(20);
		tblContent.setBorder(new JTextField().getBorder());
		tblContent.setFocusable(false);
		
		JLabel tLbl = new JLabel();
		tLbl.setOpaque(true);
		tLbl.setBackground(Color.GRAY);
		srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
		
		// 布局---------------
		setBounds((CustOpts.SCRWIDTH - 400) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 400, 320); // 对话框的默认尺寸。
		getContentPane().setLayout(null);
		
		// 搭建－－－－－－－－－－－－－
		getContentPane().add(srpContent);
		getContentPane().add(btnDelete);
		getContentPane().add(btnAdd);
		getContentPane().add(btnModify);
		getContentPane().add(btnHistory);
		
		// 加监听器－－－－－－－－
		btnDelete.addActionListener(this);
		btnAdd.addActionListener(this);
		btnModify.addActionListener(this);
		btnHistory.addActionListener(this);
		btnDelete.addKeyListener(this);
		btnAdd.addKeyListener(this);
		btnHistory.addKeyListener(this);
		tblContent.addMouseListener(this);
		getContentPane().addComponentListener(this);
		// initContents--------------
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        initTable(false);
		    }
		});
	}
	
	private void initTable(boolean dipalyAll) {
		StringBuilder sql = new StringBuilder("select * from hardware, employee where hardware.style = employee.id and hardware.category = 2");
		if(!dipalyAll) {
			sql.append(" and hardware.status < ").append(DBConsts.expired);
		}
		
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
		        tableModel[tmpPos][4] = rs.getString("NNAME");
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
		BarFrame.consts.Account(), // "Account"
	    BarFrame.consts.TIME(), // "TIME"
	    BarFrame.consts.Balance(), // "Balance"
	    BarFrame.consts.Employee() // "Employee"
	};
	
	PIMTable tblContent;
	PIMScrollPane srpContent;
	private JButton btnAdd;
	private JButton btnModify;
	private JButton btnHistory;
	private JButton btnDelete;
}
