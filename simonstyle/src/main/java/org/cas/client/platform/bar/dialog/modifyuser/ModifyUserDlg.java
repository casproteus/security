package org.cas.client.platform.bar.dialog.modifyuser;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlgConst;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;

public class ModifyUserDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener{
	public static boolean PASSED;
	static int userID;		// 标志对话框是否已关闭
	static int dlgMode;
	
	/** Creates a new instance of ContactDialog
	 * @called by PasteAction 为Copy邮件到联系人应用。
	 */
	public ModifyUserDlg(JFrame PDlg){
		super(PDlg, true);
		initDialog();
	}
	
	/* 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局，
	 * 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
	 * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
	 */
	public void reLayout(){
		cancel.setBounds(getContainer().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP,
				getContainer().getHeight() - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP,
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);//关闭
		ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);//关闭
		general.setBounds(CustOpts.HOR_GAP,	CustOpts.VER_GAP,
				getContainer().getWidth() - 2 * CustOpts.HOR_GAP,
				(ok.getY()) - 2 * CustOpts.VER_GAP);
    	general.componentResized(null);
    	validate();
	}
	public PIMRecord getContents(){return null;}
	public boolean setContents(PIMRecord prmRecord){return true;}
	public void makeBestUseOfTime(){}
	public void addAttach(File[] file, Vector actualAttachFiles){}
	public PIMTextPane getTextPane(){return null;}
	
	public void release(){
		ok.removeActionListener(this);
		cancel.removeActionListener(this);
		if (general != null){
			general.removeAll();
			general = null;
		}
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
		Object o = e.getSource();
		if(o == ok){
			String tPassword1 = general.pfdPassword.getText();
			String tPassword2 = general.pfdMakeSure.getText();
			if(tPassword1.length() > 0 && tPassword2.length() == 0){		//确认项是否已经填写  检查。
				JOptionPane.showMessageDialog(this, BarFrame.consts.PasswordMakeSure);				//提示在第二个里面填内容。
				general.pfdMakeSure.grabFocus();
				return;
			}
			if(!tPassword1.equals(tPassword2)){								//两个内容的符合性检查，提示重新填。
				JOptionPane.showMessageDialog(this, BarFrame.consts.PasswordNotEqual);
				general.pfdMakeSure.setText("");
				general.pfdPassword.setText("");
				general.pfdPassword.grabFocus();
				return;
			}																//两个内容相符，修改记录。
			
			String sql = "update UserIdentity set Password = '".concat(general.pfdPassword.getText())
			.concat("', type = ").concat(String.valueOf(general.cmbType.getSelectedIndex()))
			.concat(" where username = '").concat(general.cmbUserName.getSelectedItem().toString()).concat("'");
			try{
				Statement smt = PIMDBModel.getStatement();
				smt.executeUpdate(sql.toString());
	
				smt.close();
				smt = null;
	
				dispose();
			}catch (SQLException exp){
				exp.printStackTrace();
			}
		}else if(o == cancel){
			dispose();
		}
	}
	
	public Container getContainer(){
		return getContentPane();
	}
    
	private void initDialog(){
		setTitle(LoginDlgConst.ModifyUserInfoTitle);
		setResizable(false);
		
		//初始化－－－－－－－－－－－－－－－－
		general = new ModifyUserGeneralPanel();
		ok = new JButton(DlgConst.OK);
		cancel = new JButton(DlgConst.CANCEL);

		//属性设置－－－－－－－－－－－－－－
//		ok.setFont(CustOpts.custOps.getFontOfDefault());
		ok.setFocusable(false);
		cancel.setFocusable(false);
		ok.setMnemonic('o');
		cancel.setMnemonic('c');
		ok.setMargin(new Insets(0,0,0,0));
		cancel.setMargin(ok.getMargin());
		getRootPane().setDefaultButton(ok);
		//布局---------------
		int tHight = general.getPreferredSize().height + CustOpts.BTN_HEIGHT
			+ 2 * CustOpts.VER_GAP + CustOpts.SIZE_EDGE + CustOpts.SIZE_TITLE;
		setBounds((CustOpts.SCRWIDTH - 260)/2, (CustOpts.SCRHEIGHT - tHight)/2, 260, tHight);	//对话框的默认尺寸。
		getContentPane().setLayout(null);
		//搭建－－－－－－－－－－－－－
		getContentPane().add(general);
		getContentPane().add(ok);
		getContentPane().add(cancel);
		
		//加监听器－－－－－－－－
		ok.addActionListener(this);
		cancel.addActionListener(this);
		getContentPane().addComponentListener(this);

		//init Contents
		PASSED = false;
		general.setUserName(CustOpts.custOps.getUserName());
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				general.pfdPassword.grabFocus();
			}
		});
	}
	
	private JButton ok;
	private JButton cancel;
	private ModifyUserGeneralPanel general;
}
