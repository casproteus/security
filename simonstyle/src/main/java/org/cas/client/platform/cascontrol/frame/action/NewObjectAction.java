package org.cas.client.platform.cascontrol.frame.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;


/**新建邮件动作
*/

public class NewObjectAction extends SAction 
{
	public NewObjectAction()
	{
		super(IStatCons.FOLDER_SELECTED);
	}
	
	public NewObjectAction(PIMRecord prmRecord)
	{
		this();
		record = prmRecord;
	}
	public void actionPerformed(ActionEvent e)
	{
		int activeApp = CustOpts.custOps.getActiveAppType();
		String tmpName = (String)CustOpts.custOps.APPNameVec.get(activeApp);
		MainPane.getApp(tmpName).showDialog(
				CASControl.ctrl.getMainFrame(),new SaveContentsAction(), record, false, false);
	}
	
	PIMRecord record;
}
