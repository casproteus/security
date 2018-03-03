package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.resource.international.PaneConsts;



/**
 */

public class RemoveFolderAction extends AbstractAction
{
    /** Creates new RemoveFolderAction
     * 构建器: 删除应用文件夹动作
     */
    public RemoveFolderAction()
    {
    }

    /* action is performed */
    public void actionPerformed(ActionEvent evt)
    {
        CASTree tmpTree = CASControl.ctrl.getFolderTree();
        TreePath tmpDelPath = tmpTree.getSelectedPath();
        CASNode tmpNode = (CASNode) tmpDelPath.getLastPathComponent(); //tmpTree.getSelectNode();
        tmpTree.removeNode(tmpDelPath);

        tmpTree.setSelectNode(((CASNode)tmpNode.getParent()).getPathID());

        TreePath tmpItemPath = tmpTree.getPathFromRoot(PaneConsts.ACTIVE_TODAY);
        if (tmpItemPath == null)
        {
            return;
        }
        tmpItemPath = tmpTree.getNodePath(tmpItemPath, PaneConsts.DELETE_ITEM);
        if (tmpItemPath == null)
        {
            return;
        }
        if (tmpItemPath.isDescendant(tmpDelPath))
        {
            return;
        }

        tmpTree.addNode(tmpItemPath, PaneConsts.DELETE_ITEM, tmpNode);

        //PIMControl.ctrl.setFolderTree(tmpTree);
    }
}
