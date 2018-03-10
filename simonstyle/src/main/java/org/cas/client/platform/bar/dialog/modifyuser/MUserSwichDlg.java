package org.cas.client.platform.bar.dialog.modifyuser;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pos.dialog.PosDlgConst;
import org.cas.client.resource.international.DlgConst;

public class MUserSwichDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener{

	public MUserSwichDlg(JFrame pFrame){
		super(pFrame, true);
		parent = pFrame;
		initDialog();
	}
	
	/* 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局，
	 * 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
	 * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
	 */
	public void reLayout(){
		btnAddNewUser.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
				getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
		btnModiFyUser.setBounds(btnAddNewUser.getX(),
				btnAddNewUser.getY() + btnAddNewUser.getHeight() + CustOpts.VER_GAP,
				btnAddNewUser.getWidth(), btnAddNewUser.getHeight());
		btnDeleteUser.setBounds(btnModiFyUser.getX(),
				btnModiFyUser.getY() + btnModiFyUser.getHeight() + CustOpts.VER_GAP,
				btnModiFyUser.getWidth(), btnModiFyUser.getHeight());
		cancel.setBounds(btnDeleteUser.getX(),
				btnDeleteUser.getY() + btnDeleteUser.getHeight() + CustOpts.VER_GAP,
				btnDeleteUser.getWidth(), btnDeleteUser.getHeight());

    	validate();
	}
	public PIMRecord getContents(){return null;}
	public boolean setContents(PIMRecord prmRecord){return true;}
	public void makeBestUseOfTime(){}
	public void addAttach(File[] file, Vector actualAttachFiles){}
	public PIMTextPane getTextPane(){return null;}
	
	public void release(){
		btnAddNewUser.removeActionListener(this);
		btnModiFyUser.removeActionListener(this);
		btnDeleteUser.removeActionListener(this);
		cancel.removeActionListener(this);
		dispose();//对于对话盒，如果不加这句话，就很难释放掉。
		System.gc();//@TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
	}

	/**Invoked when the component's size changes.*/
    public void componentResized(ComponentEvent e){
    	reLayout();
    };

    /**Invoked when the component's position changes.*/    
    public void componentMoved(ComponentEvent e){};

    /**Invoked when the component has been made visible.*/
    public void componentShown(ComponentEvent e){};

    /**Invoked when the component has been made invisible.*/
    public void componentHidden(ComponentEvent e){};

	/** Invoked when an action occurs.
	 * NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
	 * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的
	 * 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
	 * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
	 * @param e 动作事件
	 */
	public void actionPerformed(ActionEvent e){
		dispose();
		Object o = e.getSource();
		if(o == btnAddNewUser)
			new AddUserDlg(parent).setVisible(true);
		else if(o == btnModiFyUser)
			new ModifyUserDlg(parent).setVisible(true);
		else if(o == btnDeleteUser)
			new DeleteUserDlg(parent).setVisible(true);
	}
	
	public Container getContainer(){
		return getContentPane();
	}
    
	private void initDialog(){
        setTitle(PosDlgConst.MUser);
		setResizable(false);
		
		//初始化－－－－－－－－－－－－－－－－
		btnAddNewUser = new JButton(PosDlgConst.AddNewUser);
		btnModiFyUser = new JButton(PosDlgConst.ModiFyUser);
		btnDeleteUser = new JButton(PosDlgConst.DeleteUser);
		cancel = new JButton(DlgConst.CANCEL);

		//属性设置－－－－－－－－－－－－－－
		btnAddNewUser.setMnemonic('a');
		btnModiFyUser.setMnemonic('m');
		btnDeleteUser.setMnemonic('d');
		cancel.setMnemonic('c');
		btnAddNewUser.setMargin(new Insets(0,0,0,0));
		btnModiFyUser.setMargin(btnAddNewUser.getMargin());
		btnDeleteUser.setMargin(btnAddNewUser.getMargin());
		cancel.setMargin(btnAddNewUser.getMargin());
		setBounds((CustOpts.SCRWIDTH - 120)/2, (CustOpts.SCRHEIGHT - 160)/2, 120, 160);	//对话框的默认尺寸。
		getContentPane().setLayout(null);
		//搭建－－－－－－－－－－－－－
		getContentPane().add(btnAddNewUser);
		getContentPane().add(btnModiFyUser);
		getContentPane().add(btnDeleteUser);
		getContentPane().add(cancel);
		
		//加监听器－－－－－－－－
		btnAddNewUser.addActionListener(this);
		btnModiFyUser.addActionListener(this);
		btnDeleteUser.addActionListener(this);
		cancel.addActionListener(this);
		getContentPane().addComponentListener(this);
	}
	private JFrame parent;
	private JButton btnAddNewUser;
	private JButton btnModiFyUser;
	private JButton btnDeleteUser;
	private JButton cancel;
}
