package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.casutil.CASUtility;


/**
 * This class is the Action class of file menu of PIM
 */
public class GoToFolderAction extends SAction
{
    /** Creates a new instance of GoToFolderAction */
    public GoToFolderAction()
    {
    }

    /** Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        MoveFolderDialog movedialog = new MoveFolderDialog(CASControl.ctrl.getMainFrame());
        movedialog.show();
        int tmpAppIndex = movedialog.getAppIndex();
        int tmpPathID = CASUtility.getAPPNodeID(tmpAppIndex);
        CASControl.ctrl.changeApplication(tmpAppIndex,tmpPathID);
    }
}
