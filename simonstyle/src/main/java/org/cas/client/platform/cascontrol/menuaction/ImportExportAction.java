package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.ImportDialog;

public class ImportExportAction extends SAction {
    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        new ImportDialog(CASControl.ctrl.getMainFrame()).setVisible(true);
    }
}
