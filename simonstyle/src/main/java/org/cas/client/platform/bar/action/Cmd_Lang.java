package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;
import org.cas.client.platform.bar.i18n.BarDlgConst0;
import org.cas.client.platform.bar.i18n.BarDlgConst1;
import org.cas.client.platform.bar.i18n.BarDlgConst2;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class Cmd_Lang implements SamActionListener {

	private static Cmd_Lang instance;
	private Cmd_Lang() {}
	public static Cmd_Lang getInstance() {
		if(instance == null)
			instance = new Cmd_Lang();
		return instance;
	}
	
	private ISButton sourceBtn;
	
	public ISButton getSourceBtn() {
		return sourceBtn;
	}
	@Override
	public void setSourceBtn(ISButton sourceBtn) {
		this.sourceBtn = sourceBtn;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String o = ((FunctionButton)e.getSource()).getText();
 		if(o.equals("EN")) {
 			BarFrame.consts = new BarDlgConst0();
        	updateInterface("update employee set subject = 'EN' where id = " + LoginDlg.USERID);
 		}else if (o.equals("FR")) {
    		BarFrame.consts = new BarDlgConst1();
    		updateInterface("update employee set subject = 'FR' where id = " + LoginDlg.USERID);
        } else if (o.equals("CN")) {
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
