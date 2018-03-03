package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.option.OptionDialog;

public class OptionsAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        new OptionDialog(CASControl.ctrl.getMainFrame(), true);
    }

}
