package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.cas.client.platform.CASControl;

public class CloseBookLeftAction extends AbstractAction {
    private static CloseBookLeftAction instance;

    public static CloseBookLeftAction getInstance() {
        if (instance == null) {
            instance = new CloseBookLeftAction();
        }
        return instance;
    }

    /** Creates new MonthViewAction */
    public CloseBookLeftAction() {
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        CASControl.ctrl.changeApplication(-1, 0);
    }
}
