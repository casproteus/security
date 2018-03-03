package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casutil.CASUtility;


public class FolderChangerAction implements ActionListener
{

    /** Creates a new instance of AppChangerAction */
    public FolderChangerAction()
    {
    }

    /** Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        int tmpApp = ((Integer)(e.getSource())).intValue();
		int tmpPathID = CASUtility.getAPPNodeID(tmpApp);
		CASControl.ctrl.changeApplication(tmpApp, tmpPathID);
    }

}
