package org.cas.client.platform.contact.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.customizeview.FieldsDialog;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascustomize.CustOpts;

/**
 * 显示字段动作
 */
public class DisplayFieldsAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        int tmpPathID = CustOpts.custOps.getActivePathID();
        new FieldsDialog(CASControl.ctrl.getMainFrame(), CASControl.ctrl.getViewInfo(tmpPathID)).show();
    }
}
