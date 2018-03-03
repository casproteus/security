package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;

public class PaneAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        boolean tmpIsVisible = CustOpts.custOps.isNavigationVisible();

        ((CASMainFrame) CASControl.ctrl.getMainFrame()).setNavigationVisible(!tmpIsVisible);
        CustOpts.custOps.setNavigationVisible(!tmpIsVisible);
    }
}
