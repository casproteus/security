package org.cas.client.platform.pos.dialog.modifyuser;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlgConst;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pos.dialog.PosDlgConst;

public class DeleteUserGeneralPanel  extends JPanel implements ComponentListener, ActionListener{

	public DeleteUserGeneralPanel(){
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
    
    public void actionPerformed(ActionEvent e){
    	Object o = e.getSource();
    	if(o == cmbUserName){
    		int tIdx = cmbUserName.getSelectedIndex();
    		cmbType.setSelectedIndex(typeAry[tIdx]);
    	}
    }
    
	private void initConponent(){
		lblUserName = new JLabel(LoginDlgConst.UserName);
		lblType = new JLabel(LoginDlgConst.Type);
		cmbUserName = new JComboBox();
		cmbType = new JComboBox();
		
		//properties
		setLayout(null);
		cmbType.setEnabled(false);
		
		//initContent@NOTE:本类中故意将内容初始化提前，因为需要将当前用户信息的对应的级别拿出来，并据此判断是否增加”级别“组件。
		initContent();
		reLayout();
		//built
		add(lblUserName);
		add(cmbUserName);
		add(lblType);
		add(cmbType);
		
		cmbUserName.addActionListener(this);
		addComponentListener(this);
	}

	private void initContent(){
		String sql = "select ID, Type, UserName, PASSWORD from useridentity where ID > 5";
    	try{
    		ResultSet rs = PIMDBModel.getConection().createStatement(
    				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
    		ResultSetMetaData rd = rs.getMetaData(); //得到结果集相关信息
    		
    		rs.afterLast();
			rs.relative(-1);
			int tmpPos = rs.getRow();
			userIDAry = new int[tmpPos];
			typeAry = new int[tmpPos];
			subjectAry = new String[tmpPos];
			passwordAry = new String[tmpPos];
			rs.beforeFirst();
			tmpPos = 0;
			while (rs.next()){
				userIDAry[tmpPos] = rs.getInt("id");
				typeAry[tmpPos] = rs.getInt("type");
				subjectAry[tmpPos] = rs.getString("UserName");
				passwordAry[tmpPos] = rs.getString("PASSWORD");
				tmpPos++;
			}
			rs.close();//关闭
    	}catch(SQLException e){
    		ErrorUtil.write(e);
    	}

    	cmbUserName.setModel(new DefaultComboBoxModel(subjectAry));
    	cmbUserName.setSelectedIndex(0);
    	cmbType.setModel(new DefaultComboBoxModel(PosDlgConst.USERTYPE));
    	cmbUserName.setSelectedIndex(typeAry[0]);
	}
	/** 本方法用于设置View上各个组件的尺寸。 */
	public void reLayout() {
		int prmWidth = getWidth();
		lblUserName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblUserName.getPreferredSize().width, 
				CustOpts.BTN_HEIGHT);
		cmbUserName.setBounds(lblUserName.getX() + lblUserName.getWidth() + CustOpts.HOR_GAP, lblUserName.getY(),
				prmWidth - lblUserName.getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
		lblType.setBounds(lblUserName.getX(), lblUserName.getY() + lblUserName.getHeight() + CustOpts.VER_GAP,
				lblUserName.getWidth(), CustOpts.BTN_HEIGHT);
		cmbType.setBounds(cmbUserName.getX(), lblType.getY(),
				cmbUserName.getWidth(), CustOpts.BTN_HEIGHT);
			
		setPreferredSize(new Dimension(getWidth(), cmbType.getY() + cmbType.getHeight() + CustOpts.VER_GAP));
	}
	
	public void setUserName(String pName){
		if(pName != null){
			int tIdx = 0;
			for(int i = 0, len = subjectAry.length; i < len; i++)
				if(pName.equals(subjectAry[i]))
					tIdx = i;
			cmbUserName.setSelectedIndex(tIdx);
			cmbType.setSelectedIndex(typeAry[tIdx]);
		}
	}
	
	JLabel lblUserName;
	JLabel lblType;

	JComboBox cmbUserName;
	JComboBox cmbType;
	int[] userIDAry;
	int[] typeAry;
	String[] subjectAry;
	String[] passwordAry;
}
