package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascontrol.navigation.menu.FolderPopupMenu;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.resource.international.PaneConsts;

/** 将回收站中的项目恢复到原来位置上去。 */
public class ResumeAction extends SAction {
    /**
     * Creates a new instance of ResumeAction 恢复记录动作
     */
    public ResumeAction(PIMRecord prmRecord) {
        pimRecords = new Vector();
        pimRecords.add(prmRecord);
    }

    /**
     * Creates a new instance of ResumeAction 恢复记录动作
     */
    public ResumeAction(Vector prmRecords) {
        pimRecords = prmRecords;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        if (pimRecords == null) {
            return;
        }
        for (int i = 0; i < pimRecords.size(); i++) {
            PIMRecord tmpRecord = (PIMRecord) pimRecords.get(i);
            ResumeCreateFolder(tmpRecord);
        }
        CASControl.ctrl.getModel().restoreDeletedRecords(pimRecords);
    }

    /**
     * 恢复指定记录，通过检查该记录的非”回收站“下的路径是否还存在，如果不存在的话，则重新根据”回收站“下的路径建立起来。
     * 
     * @param 等待被恢复的记录
     *            。该记录的“已删除”属性肯定是被设置了的。
     */
    private void ResumeCreateFolder(
            PIMRecord prmRecord) {
        CASTree tmpPIMTree = CASControl.ctrl.getFolderTree(); // 获得导航树
        TreePath tmpRecyclePath = tmpPIMTree.getPathFromRoot(PaneConsts.RECYCLE); // 获得‘垃圾箱’的路径
        TreePath tmpPath = tmpPIMTree.getTreePath(prmRecord.getInfolderID());// 获得NodeID，并找与之匹配路径－－如果匹配

        if (tmpRecyclePath.isDescendant(tmpPath)) // 的节点是在回收站下，说明外面没有匹配的节点。那么需要在外面建立一个。
        { // @NOTE:这里故意与RecyclePath比较，是为了同时应付两种规格，即无论垃圾箱与”资讯管理“平级还是在资讯管理里面,这种写法都不用修改。
            CASNode[] tmpNodes = new CASNode[tmpPath.getPathCount() - 2]; // 初始化一个数组，用于暂存这个在回收站中的节点.“－2”为了去掉了回收站级和应用级（主要不是为了省内存，而是使后人理解代码时不产生歧义）。
            CASNode tmpNode = (CASNode) tmpPath.getLastPathComponent(); // 的各级父节点。遍历前指针先指向末尾节点。

            for (int i = 0; i < tmpNodes.length; i++) // 该遍历为了将各级节点压入堆栈。
            {
                String tmpNodeCaption = tmpNode.getUserObject().toString();
                int tmpAppIndex = CustOpts.custOps.APPCapsVec.indexOf(tmpNodeCaption);

                if (tmpAppIndex < 0)
                    tmpNodes[i] = tmpNode; // 只要没有到应用级，就把节点压到堆栈中。
                else // 到达应用级，开始反向遍历（@NOTE:这时i值已经增加了1）
                {
                    TreePath tmpParentPath = tmpPIMTree.getTreePath(tmpNode.getPathID());// 得到’垃圾箱‘外面的应用级的节点路径。
                    for (i--; i > 0; i--) {
                        String tmpNodeName = tmpNodes[i].getUserObject().toString();
                        TreePath childPath = tmpPIMTree.getNodePath(tmpParentPath, tmpNodeName);
                        if (childPath == null) // 为null，说明tmpNodes[i]同名的文件夹不存在，开始建立这个节点。
                        {
                            CASNode tmpChildNode =
                                    new CASNode(tmpNodeName, MainPane.getApp(
                                            CustOpts.custOps.APPNameVec.get(tmpAppIndex)).getAppIcon(true), true,
                                            FolderPopupMenu.CAN_NEW_FOLDER, tmpNodes[i].getPathID());

                            tmpPIMTree.addNode(tmpParentPath, tmpNodeCaption, tmpChildNode);

                            createViewInfo(tmpAppIndex, ((CASNode) tmpParentPath.getLastPathComponent()).getPathID(),
                                    tmpChildNode);

                            childPath = new TreePath(tmpChildNode.getPath());
                        }
                        tmpParentPath = childPath;
                    }
                    break;
                }

                tmpNode = (CASNode) tmpNode.getParent();
            }
        }
    }

    /*
     * 创建PIMViewInfo
     * @param prmAppType: 应用类型
     * @param prmParentNodeID 父结点路径
     * @param prmChildNode : 子结点
     */
    private void createViewInfo(
            int prmAppType,
            int prmParentNodeID,
            CASNode prmChildNode) {
        PIMViewInfo tmpParentViewInfo = CASControl.ctrl.getViewInfo(prmParentNodeID);
        PIMViewInfo tmpChildViewInfo = (PIMViewInfo) tmpParentViewInfo.clone();
        tmpChildViewInfo.setFolderID(prmChildNode.getPathID());
        CASControl.ctrl.getModel().saveDataSource(tmpChildViewInfo, 0);
    }

    private Vector pimRecords;
}
