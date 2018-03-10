package org.cas.client.platform.bar.dialog.modifyuser;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlgConst;
import org.cas.client.platform.cascustomize.CustOpts;

public class ModifyPasswordGneeralPanel  extends JPanel implements ComponentListener{

	public ModifyPasswordGneeralPanel(){
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
		lblPassword = new JLabel(LoginDlgConst.Password);
		lblMakeSure = new JLabel(LoginDlgConst.MakeSure);

		tfdUserName = new JTextField();
		pfdPassword = new JPasswordField();
		pfdMakeSure = new JPasswordField();
		
		//properties
		setLayout(null);
		tfdUserName.setEnabled(false);

		reLayout();
		//built
		add(lblUserName);
		add(tfdUserName);
		add(lblPassword);
		add(pfdPassword);
		add(lblMakeSure);
		add(pfdMakeSure);
		
		addComponentListener(this);
	}

	/** 本方法用于设置View上各个组件的尺寸。 */
	public void reLayout() {
		int prmWidth = getWidth();
		lblUserName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblUserName.getPreferredSize().width, 
				CustOpts.BTN_HEIGHT);
		tfdUserName.setBounds(lblUserName.getX() + lblUserName.getWidth() + CustOpts.HOR_GAP, lblUserName.getY(),
				prmWidth - lblUserName.getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
		lblPassword.setBounds(lblUserName.getX(), lblUserName.getY() + lblUserName.getHeight() + CustOpts.VER_GAP,
				lblUserName.getWidth(), CustOpts.BTN_HEIGHT);
		pfdPassword.setBounds(tfdUserName.getX(), lblPassword.getY(),
				tfdUserName.getWidth(), CustOpts.BTN_HEIGHT);
		lblMakeSure.setBounds(lblPassword.getX(), lblPassword.getY() + lblPassword.getHeight() + CustOpts.VER_GAP,
				lblPassword.getWidth(), CustOpts.BTN_HEIGHT);
		pfdMakeSure.setBounds(pfdPassword.getX(), lblMakeSure.getY(),
				pfdPassword.getWidth(), CustOpts.BTN_HEIGHT);
			
		setPreferredSize(new Dimension(getWidth(), pfdMakeSure.getY() + pfdMakeSure.getHeight() + CustOpts.VER_GAP));
	}
	
	public void setUserName(String pName){
		tfdUserName.setText(pName);
	}
	
	JLabel lblUserName;
	JLabel lblPassword;
	JLabel lblMakeSure;

	JTextField tfdUserName;
	JPasswordField pfdPassword;
	JPasswordField pfdMakeSure;
	int[] userIDAry;
	int[] typeAry;
	String[] subjectAry;
	String[] passwordAry;
}
