package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.customizeview.CustomizeViewDialog;
import org.cas.client.platform.cascustomize.CustOpts;

public class DifineViewAction extends SAction {
    /** Creates a new instance of DifineViewAction */
    public DifineViewAction() {
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        int tmpActiveAppType = CustOpts.custOps.getActiveAppType();
        // 暂时日历不能开禁
        // A.s("tmpActiveAppType" + tmpActiveAppType);
        if (tmpActiveAppType >= 0 && tmpActiveAppType < 7) // != ModelConstants.CALENDAR_APP)
            new CustomizeViewDialog(CASControl.ctrl.getMainFrame()).show();
    }

}
