package org.cas.client.platform.cascontrol.menuaction;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.navigation.CASTree;

public class NextFolderAction extends SAction {

    /** Creates a new instance of NextFolderAction */
    public NextFolderAction() {
        folderManager = CASControl.ctrl.getFolderManager();
        update();
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            java.awt.event.ActionEvent e) {
        folderManager.forward();
        CASTree.getInstance().getLastFolderAction().update();
        update();
    }

    /**
     * call by:PIMControl and LastFolderAction
     */
    public void update() {
        setEnabled(folderManager.hasNextFolder());
    }

    private CASFolderManager folderManager;
}
