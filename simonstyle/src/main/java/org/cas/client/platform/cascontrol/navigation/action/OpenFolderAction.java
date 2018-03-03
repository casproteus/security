package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;


/**
 */

public class OpenFolderAction extends SAction
{

    /** Creates a new instance of OpenFolderAction
     * 构建器: 打开应用动作
     */
    public OpenFolderAction()
    {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        CASTree tmpTree = CASControl.ctrl.getFolderTree();
        TreePath tmpPath = tmpTree.getSelectedPath();
        if (tmpPath != null)
        {
            int tmpPathCount = tmpPath.getPathCount();
            CASNode node = (CASNode)tmpPath.getLastPathComponent();
            int tmpAppIndex = CustOpts.custOps.APPCapsVec.indexOf(node.getName());
            if (tmpPathCount > 3)
            {
                tmpAppIndex = 4;
                CASControl.ctrl.changeApplication(tmpAppIndex, node.getPathID());
                tmpTree.setSelectionPath(tmpPath);
            }
            else
            {
                CASControl.ctrl.changeApplication(tmpAppIndex, node.getPathID());
            }
        }
    }
}
