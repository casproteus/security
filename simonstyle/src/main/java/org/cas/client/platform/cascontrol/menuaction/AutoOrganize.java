package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Date;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascustomize.CustOpts;

/**
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code
 * and Comments
 */
public class AutoOrganize extends SAction {
    public AutoOrganize() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        int tmpLastUpdateMonth = CustOpts.custOps.getLastUpdtedMonth();
        if (tmpLastUpdateMonth != currentMonth) {
            ICASModel tmpModel = CASControl.ctrl.getModel();
            if (tmpModel == null) {
                return;
            }
            CASControl.ctrl.getFolderTree().autoOrganizeByDate();
        }
        CustOpts.custOps.setLastUpdtedMonth(currentMonth);
    }

    private int currentMonth = new Date().getMonth() + 1; // 当前的月份
}
