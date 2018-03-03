package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.navigation.action.MoveFolderDialog;

public class FolderMoveAction extends SAction {

    /**
     * Creates a new instance of FolderMoveAction 移动文件夹
     */
    public FolderMoveAction() {
        super(IStatCons.FOLDER_SELECTED);
    }

    /**
     * Creates a new instance of FolderMoveAction 移动文件夹
     * 
     * @param flag
     *            : action标记
     */
    public FolderMoveAction(int flag) {
        super(flag);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        new MoveFolderDialog(CASControl.ctrl.getMainFrame()).show();
    }
}
