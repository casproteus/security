package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.navigation.CASTree;

public class LastFolderAction extends SAction {

    /** Creates a new instance of LastFolderAction */
    public LastFolderAction() {
        update();
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        folderManager.backward();
        CASTree.getInstance().getNextFolderAction().update();
        update();
    }

    /**
     * 更新文件夹状态
     */
    public void update() {
        setEnabled(folderManager.hasLastFolder());
    }

    private CASFolderManager folderManager = CASControl.ctrl.getFolderManager();
}
