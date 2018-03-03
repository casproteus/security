package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.resource.international.DlgConst;

public class FolderDelAction extends SAction {
    /**
     * Creates a new instance of FolderDelAction 构建器:删除文件夹动作
     */
    public FolderDelAction() {
        super(IStatCons.FOLDER_SELECTED);
    }

    /**
     * Creates a new instance of FolderDelAction 构建器:删除文件夹动作
     * 
     * @param flag
     *            : action标记
     */
    public FolderDelAction(int flag) {
        super(flag);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        CASTree tmpPIMTree = CASControl.ctrl.getFolderTree(); // 取得选择的路径
        TreePath tmpDelPath = tmpPIMTree.getSelectedPath();
        if (tmpDelPath == null)
            return;

        CASNode tmpDelNode = (CASNode) tmpDelPath.getLastPathComponent(); // 取得选择的结点
        if (tmpDelNode.getPathID() < 10000) { // 非用户自定义的"预设节点"都不可删除.
            JOptionPane.showMessageDialog(CASMainFrame.mainFrame, DlgConst.NodeDelNotAllow);
            return;
        }
        TreePath tmpParentPath = new TreePath(((CASNode) tmpDelNode.getParent()).getPath()); // 取得该路径结点的父结点
        tmpPIMTree.removeNode(tmpDelPath);

        // 先去掉该节点对应的ID号属性.
        String tPath = tmpDelPath.toString();
        CustOpts.custOps.removeKeyAndValue(tPath.concat("_ID"));
        // 再将该节点本身以及多重子节点,以及多重子节点得ID号属性统统去除.
        Enumeration tEnum = CustOpts.custOps.getKeys();
        tPath = tPath.substring(0, tPath.length() - 1);
        while (tEnum.hasMoreElements()) {
            String tKey = tEnum.nextElement().toString();
            String tKey2 = tKey.substring(0, tKey.length() - 1);
            if (tKey2.equals(tPath) || tKey2.indexOf(tPath.concat(",")) == 0)
                CustOpts.custOps.removeKeyAndValue(tKey);
        }
        // 最后调整该节点的父节点的子节点内容,如果其父节点就这么一个子节点,则连同父节点一并去除.
        tmpPIMTree.resetPath(tmpParentPath);

        // TODO:子节点处理了吗?永远留在config里吗?要方回收站吗?
        CASControl.ctrl.getFolderPane().setFolderTree(tmpPIMTree);

        int pathCount = tmpDelPath.getPathCount();
        if (pathCount > 3) {
            PIMViewInfo viewInfo = CASControl.ctrl.getViewInfo(tmpDelNode.getPathID());
            CASControl.ctrl.getModel().deleteViewInfo(viewInfo);

            CASNode tmpAppNode = (CASNode) tmpDelPath.getPath()[3];
            int tmpAppType = CustOpts.custOps.APPCapsVec.indexOf(tmpAppNode.getName());
            CASControl.ctrl.changeApplication(tmpAppType, tmpDelNode.getPathID());
        }
    }
}
