package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.magicbath.dialog.Dlg_ModifyData;

public class ModifyData extends SAction {
    /** Creates a new instance of CutAction */
    public ModifyData() {
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Dlg_ModifyData.getInstance().setVisible(true);
    }
}
