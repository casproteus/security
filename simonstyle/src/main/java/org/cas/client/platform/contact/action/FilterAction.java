package org.cas.client.platform.contact.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.customizeview.FilterDialog;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascustomize.CustOpts;

public class FilterAction extends SAction {
    /**
     * Creates a new instance of FilterAction 构建器:筛选动作
     */
    public FilterAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        int tmpPathID = CustOpts.custOps.getActivePathID();
        new FilterDialog(CASControl.ctrl.getMainFrame(), CASControl.ctrl.getViewInfo(tmpPathID), false).show();
    }
}
