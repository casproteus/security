package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.dialog.customizeview.FilterDialog;
import org.cas.client.platform.cascustomize.CustOpts;

/** 查找动作 */
public class FindAction extends SAction {
    /** 构建器 */
    public FindAction() {
        super(IStatCons.CAN_FIND);
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        new FilterDialog(CASControl.ctrl.getMainFrame(),
                CASControl.ctrl.getViewInfo(CustOpts.custOps.getActivePathID()), true).setVisible(true);
    }
}
