package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.cas.client.platform.CASControl;

public class CloseBookRightAction extends AbstractAction {
    private static CloseBookRightAction instance;

    public static CloseBookRightAction getInstance() {
        if (instance == null) {
            instance = new CloseBookRightAction();
        }
        return instance;
    }

    /** Creates new MonthViewAction */
    private CloseBookRightAction() {
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        CASControl.ctrl.changeApplication(-1, 0);
    }
}
