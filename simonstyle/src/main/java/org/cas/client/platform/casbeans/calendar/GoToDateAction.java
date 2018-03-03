package org.cas.client.platform.casbeans.calendar;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.menuaction.SAction;

public class GoToDateAction extends SAction {
    /** Creates new GotoDateAction */
    public GoToDateAction() {
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        Frame frame = CASControl.ctrl.getMainFrame();
        new GotoDateDialog(frame).show();
    }
}
