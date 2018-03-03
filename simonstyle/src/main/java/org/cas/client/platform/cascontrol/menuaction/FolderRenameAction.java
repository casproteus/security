package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.navigation.CASTree;

public class FolderRenameAction extends SAction {
    /**
     * Creates new RenameFolderAction 构建器: 重命名文件夹动作
     */
    public FolderRenameAction() {
        super(IStatCons.FOLDER_SELECTED);
    }

    public FolderRenameAction(int flag) {
        super(flag);
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        CASTree tmpTree = CASControl.ctrl.getFolderTree();
        tmpTree.editTreeNode(tmpTree.getSelectedPath());
    }
}
