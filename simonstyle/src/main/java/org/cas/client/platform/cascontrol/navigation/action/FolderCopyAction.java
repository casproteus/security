package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.menuaction.SAction;




public class FolderCopyAction extends SAction
{
    /** Creates a new instance of FolderCopyAction
     * 构建器:复制文件夹动作
     */
    public FolderCopyAction()
    {
        super(IStatCons.FOLDER_SELECTED);
    }

    /** Creates a new instance of FolderCopyAction
     * 构建器:复制文件夹动作
     * @param flag : action标记
     */
    public FolderCopyAction(int flag)
    {
        super(flag);
    }

    /** Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        new CopyFolderDialog(CASControl.ctrl.getMainFrame()).show();
    }
}
