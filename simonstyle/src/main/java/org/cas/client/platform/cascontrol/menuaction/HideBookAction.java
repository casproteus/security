package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;

public class HideBookAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        boolean isHide = !CustOpts.custOps.isBaseBookHide();
        CustOpts.custOps.setBaseBookHide(isHide);
        CASControl.ctrl.getMainPane().layoutContainer(null);
        CASControl.ctrl.getMainPane().getBaseBookPane().layoutContainer(null);
        CASControl.ctrl.getMainPane().revalidate();
        CASControl.ctrl.getMainPane().repaint();
    }
}
