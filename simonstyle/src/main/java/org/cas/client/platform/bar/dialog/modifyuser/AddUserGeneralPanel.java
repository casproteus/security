package org.cas.client.platform.bar.dialog.modifyuser;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlgConst;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.bar.dialog.BarDlgConst;

public class AddUserGeneralPanel  extends JPanel implements ComponentListener{

	public AddUserGeneralPanel(){
		initConponent();
	}
	
	/**Invoked when the component's size changes. */
    public void componentResized(ComponentEvent e){
    	reLayout();
    }

    /**Invoked when the component's position changes. */    
    public void componentMoved(ComponentEvent e){}

    /**Invoked when the component has been made visible.*/
    public void componentShown(ComponentEvent e){}

    /**Invoked when the component has been made invisible.*/
    public void componentHidden(ComponentEvent e){}
    
	private void initConponent(){
		lblUserName = new JLabel(LoginDlgConst.UserName);
		lblType = new JLabel(LoginDlgConst.Type);
		lblPassword = new JLabel(LoginDlgConst.Password);
		lblMakeSure = new JLabel(LoginDlgConst.MakeSure);

		tfdUserName = new JTextField();
		cmbType = new JComboBox();
		pfdPassword = new JPasswordField();
		pfdMakeSure = new JPasswordField();
		
		//properties
		setLayout(null);

		//initContent@NOTE:本类中故意将内容初始化提前，因为需要将当前用户信息的对应的级别拿出来，并据此判断是否增加”级别“组件。
		initContent();
		reLayout();
		//built
		add(lblUserName);
		add(tfdUserName);
		add(lblType);
		add(cmbType);
		add(lblPassword);
		add(pfdPassword);
		add(lblMakeSure);
		add(pfdMakeSure);
		addComponentListener(this);
	}

	private void initContent(){
		String sql = "select ID, Type, UserName, PASSWORD from useridentity where ID > 5";
    	try{
    		ResultSet rs =  PIMDBModel.getReadOnlyStatement().executeQuery(sql);
    		ResultSetMetaData rd = rs.getMetaData(); //得到结果集相关信息
    		
    		rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();
			userIDAry = new int[tmpPos];
			typeAry = new int[tmpPos];
			userNameAry = new String[tmpPos];
			passwordAry = new String[tmpPos];
			rs.beforeFirst();
			tmpPos = 0;
			while (rs.next()){
				userIDAry[tmpPos] = rs.getInt("id");
				typeAry[tmpPos] = rs.getInt("type");
				userNameAry[tmpPos] = rs.getString("UserName");
				passwordAry[tmpPos] = rs.getString("PASSWORD");
				tmpPos++;
			}
			rs.close();//关闭
    	}catch(SQLException e){
    		ErrorUtil.write(e);
    	}

    	cmbType.setModel(new DefaultComboBoxModel(BarDlgConst.USERTYPE));
    	cmbType.setSelectedIndex(1);
	}
	/** 本方法用于设置View上各个组件的尺寸。 */
	public void reLayout() {
		int prmWidth = getWidth();
		lblUserName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblUserName.getPreferredSize().width, 
				CustOpts.BTN_HEIGHT);
		tfdUserName.setBounds(lblUserName.getX() + lblUserName.getWidth() + CustOpts.HOR_GAP, lblUserName.getY(),
				prmWidth - lblUserName.getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
		lblType.setBounds(lblUserName.getX(), lblUserName.getY() + lblUserName.getHeight() + CustOpts.VER_GAP,
				lblUserName.getWidth(), CustOpts.BTN_HEIGHT);
		cmbType.setBounds(tfdUserName.getX(), lblType.getY(),
				tfdUserName.getWidth(), CustOpts.BTN_HEIGHT);
		lblPassword.setBounds(lblType.getX(), lblType.getY() + lblType.getHeight() + CustOpts.VER_GAP,
				lblType.getWidth(), CustOpts.BTN_HEIGHT);
		pfdPassword.setBounds(cmbType.getX(), lblPassword.getY(),
				cmbType.getWidth(), CustOpts.BTN_HEIGHT);
		lblMakeSure.setBounds(lblPassword.getX(), lblPassword.getY() + lblPassword.getHeight() + CustOpts.VER_GAP,
				lblPassword.getWidth(), CustOpts.BTN_HEIGHT);
		pfdMakeSure.setBounds(pfdPassword.getX(), lblMakeSure.getY(),
				pfdPassword.getWidth(), CustOpts.BTN_HEIGHT);
			
		setPreferredSize(new Dimension(getWidth(), pfdMakeSure.getY() + pfdMakeSure.getHeight() + CustOpts.VER_GAP));
	}
	
	JLabel lblUserName;
	JLabel lblType;
	JLabel lblPassword;
	JLabel lblMakeSure;

	JTextField tfdUserName;
	JComboBox cmbType;
	JPasswordField pfdPassword;
	JPasswordField pfdMakeSure;
	int[] userIDAry;
	int[] typeAry;
	String[] userNameAry;
	String[] passwordAry;
}
