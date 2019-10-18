package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.cascustomize.CustOptsConsts;

public class NextMainBGAction extends SAction {
    public void actionPerformed(
            ActionEvent e) {
        CustOpts.custOps.removeKeyAndValue(CustOptsConsts.MainBGImgPath);
        Object tIndex = CustOpts.custOps.getValue(CustOptsConsts.MainBGIndex);
        if (tIndex == null)
            CustOpts.custOps.setKeyAndValue(CustOptsConsts.MainBGIndex, String.valueOf(1));
        else {
            int tIdx = (Integer.parseInt((String) tIndex) + 1) % 5;
            CustOpts.custOps.setKeyAndValue(CustOptsConsts.MainBGIndex, String.valueOf(tIdx));
        }

        CASControl.ctrl.getMainPane().preparePicture();
        CASControl.ctrl.getMainPane().repaint();
    }
}
