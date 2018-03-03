package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casutil.CASUtility;

public class CurrentViewAction extends SAction {
    /**
     * Creates a new instance of CurrentViewAction
     * 
     * @param 应用类型
     */
    public CurrentViewAction(int prmAppIndex) {
        this.appIndex = prmAppIndex;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        int tmpFolderID = CASUtility.getAPPNodeID(appIndex);
        CASControl.ctrl.changeApplication(appIndex, tmpFolderID);
    }

    private final int appIndex;

}
