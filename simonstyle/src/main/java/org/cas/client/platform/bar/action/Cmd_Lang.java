package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.CommandBtnDlg;
import org.cas.client.platform.bar.i18n.BarDlgConst0;
import org.cas.client.platform.bar.i18n.BarDlgConst1;
import org.cas.client.platform.bar.i18n.BarDlgConst2;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class Cmd_Lang implements ActionListener {
	private static Cmd_Lang instance;
	
	private Cmd_Lang() {}
	
	public static Cmd_Lang getInstance() {
		if(instance == null) {
			instance = new Cmd_Lang();
		}
		return instance;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
 		if(o == CommandBtnDlg.btnEN) {
 			BarFrame.consts = new BarDlgConst0();
        	updateInterface("update employee set subject = 'EN' where id = " + LoginDlg.USERID);
 		}else if (o == CommandBtnDlg.btnFR) {
    		BarFrame.consts = new BarDlgConst1();
    		updateInterface("update employee set subject = 'FR' where id = " + LoginDlg.USERID);
        } else if (o == CommandBtnDlg.btnCN) {
    		BarFrame.consts = new BarDlgConst2();
        	updateInterface("update employee set subject = 'CN' where id = " + LoginDlg.USERID);
        }
	}
	
	private void updateInterface(String sb) {
    	try {
    		PIMDBModel.getStatement().executeUpdate(sb);
    		//this.setVisible(false);
    		BarFrame.instance.initComponent();
    		BarFrame.instance.switchMode(0);
    	}catch(Exception exp) {
    		ErrorUtil.write(exp);
    	}
    }
}
