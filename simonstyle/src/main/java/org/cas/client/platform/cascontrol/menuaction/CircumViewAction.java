package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;

public class CircumViewAction extends SAction {
    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        CustOpts.custOps.setViewTopAndDown(!CustOpts.custOps.isViewTopAndDown());

        CASControl.ctrl.getMainPane().layoutContainer(null);
        CASControl.ctrl.getMainPane().getBaseBookPane().layoutContainer(null);
        CASControl.ctrl.getMainPane().getBaseBookPane().revalidate();
        CASControl.ctrl.getMainPane().repaint();
    }
}
