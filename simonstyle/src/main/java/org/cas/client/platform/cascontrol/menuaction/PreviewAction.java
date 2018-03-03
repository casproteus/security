package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import javax.swing.JToggleButton;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascustomize.CustOpts;

public class PreviewAction extends SAction {
    /** Creates a new instance of PreviewAction */
    public PreviewAction() {
        super(IStatCons.PRVIEW);
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        boolean isPreViewShow = !CustOpts.custOps.isPreviewShown();
        CustOpts.custOps.setPreView(isPreViewShow);

        CASControl.ctrl.getMainPane().layoutContainer(null);
        CASControl.ctrl.getMainPane().getBaseBookPane().layoutContainer(null);
        CASControl.ctrl.getMainPane().getBaseBookPane().revalidate();
        CASControl.ctrl.getMainPane().repaint();
    }
}
