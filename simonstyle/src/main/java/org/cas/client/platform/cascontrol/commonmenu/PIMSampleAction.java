package org.cas.client.platform.cascontrol.commonmenu;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.casutil.CASUtility;

/** This class is the Action class of file menu of PIM */
public class PIMSampleAction extends SAction {
    /** Creates a new instance of FileAction */
    public PIMSampleAction() {
    }

    /** Creates a new instance of FileAction */
    public PIMSampleAction(int flag) {
        super(flag);
    }

    /** Creates a new instance of FileAction */
    public PIMSampleAction(int flag, int prmAppIndex) {
        super(flag);
        this.appIndex = prmAppIndex;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        CASControl.ctrl.changeApplication(appIndex, CASUtility.getAPPNodeID(appIndex));
    }

    private int appIndex;
}
