package org.cas.client.platform.contact.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.customizeview.SortDialog;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascustomize.CustOpts;

public class SortAction extends SAction {

    /**
     * Creates a new instance of SortAction 构建器: 排序动作
     */
    public SortAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        int tmpPathID = CustOpts.custOps.getActivePathID();
        new SortDialog(CASControl.ctrl.getMainFrame(), CASControl.ctrl.getViewInfo(tmpPathID)).show();
    }

}
