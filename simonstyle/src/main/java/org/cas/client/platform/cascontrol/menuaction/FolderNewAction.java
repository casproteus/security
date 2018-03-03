package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascontrol.navigation.menu.FolderPopupMenu;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.resource.international.PaneConsts;

public class FolderNewAction extends SAction {
    /**
     * Creates new NewFolderAction 新建文件夹
     */
    public FolderNewAction() {
        super(IStatCons.FOLDER_SELECTED);
    }

    /**
     * Creates new NewFolderAction 新建文件夹
     * 
     * @param flag
     *            :action标记
     */
    public FolderNewAction(int flag) {
        super(flag);
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        // 选中状态检查================================================================
        CASTree tmpTree = CASControl.ctrl.getFolderTree(); // 选中路径检查-----------
        TreePath tmpParentPath = tmpTree.getSelectedPath();
        if (tmpParentPath == null) // 没有文件夹被选中,那么就无从
            return; // 知道文件夹建到那里.所以返回

        CASNode tmpSeleNode = (CASNode) tmpParentPath.getLastPathComponent(); // 选中结点检查-----------
        String tmpParentNodeName = tmpSeleNode.getName();
        if (tmpParentNodeName == null)
            tmpParentNodeName = PaneConsts.HEAD_PAGE;

        // 构建节点================================================================
        String tmpNewNodeName = getNewFolderName(tmpParentPath);
        int tmpAppIndex = CASUtility.getAppIndexByFolderID(tmpSeleNode.getPathID());
        CASNode tmpNewNode =
                new CASNode(tmpNewNodeName, MainPane.getApp(CustOpts.custOps.APPNameVec.get(tmpAppIndex)).getAppIcon(
                        true), true, FolderPopupMenu.CAN_NEW_FOLDER, CustOpts.custOps.getANewFolderID());

        // 开始插入节点================================================================
        if (tmpTree.addNode(tmpParentPath, tmpParentNodeName, tmpNewNode) == false)
            return; // 如果失败的话,就此返回.－－－

        // 使子节点有所怎加的父节点重新绘制.@NOTE:添加节点只是对节点属性的调整,不导致重回,必须显示调reload(),否则只有当添加第一个节点时
        // 能正常重绘,第二个以上的节点不会重回.这个机制稍显麻烦,也许jdk以后版本会改为自动加标记,或invalidate()+repaint()即可搞定.
        ((DefaultTreeModel) tmpTree.getTree().getModel()).reload((CASNode) tmpParentPath.getLastPathComponent());

        PIMViewInfo tmpPNodeVIf = CASControl.ctrl.getViewInfo(tmpSeleNode.getPathID());
        PIMViewInfo tmpCNodeVIf = (PIMViewInfo) tmpPNodeVIf.clone(); // 取新结点的ViewInfo.

        tmpCNodeVIf.setFolderID(tmpNewNode.getPathID());
        CASControl.ctrl.getModel().addViewInfo(tmpCNodeVIf);
        CustOpts.custOps.setActivePathID(tmpNewNode.getPathID());
        tmpTree.resetPath(tmpParentPath);
        CustOpts.custOps.setKeyAndValue(new TreePath(tmpNewNode.getPath()).toString().concat("_ID"),
                PIMPool.pool.getKey(tmpNewNode.getPathID())); // 因为是新产生的一个节点,所以需要把新产生的节点ID存入到hash中,该ID将一直不
                                                              // 改变,所以修改,调整,删除等动作不需要重新存ID,只要重新保存节点名的树结构即可.
        tmpTree.setSelectNode(tmpNewNode.getPathID());
        CASControl.ctrl.getFolderPane().setFolderTree(tmpTree);
    }

    /**
     * 取得新建文件夹的名字
     * 
     * @renturn 文件夹名字
     */
    private String getNewFolderName(
            TreePath prmParentPath) {
        String tmpKeyStrForCustOps = prmParentPath.toString();// @NOTE:修改前用的是一种笨办法取得key:new
                                                              // TreePath(prmParentNode.getPath()).toString();
        String tmpNodeNames = (String) CustOpts.custOps.getValue(tmpKeyStrForCustOps);
        if (tmpNodeNames == null)
            return PaneConsts.NEW_FOLDER;

        Vector childNamesVector = CASUtility.parserStrToVec(tmpNodeNames, CustOpts.BIAS, tmpNodeNames.length());

        String[] tmpNames = new String[childNamesVector.size()];
        for (int i = 0; i < childNamesVector.size(); i++) {
            tmpNames[i] = (String) childNamesVector.get(i);
        }

        String tepNewFolderName = PaneConsts.NEW_FOLDER;
        if (tmpNames == null || tmpNames.length == 0) {
            return tepNewFolderName;
        }
        int tmpLength = tepNewFolderName.length();
        Vector tmpVector = new Vector();

        boolean tmpStandard = false; // 是否存在新建文件夹项
        for (int i = 0; i < tmpNames.length; i++) // 此循环把新建文件夹后的编号保存起来
        {
            if (tmpNames[i].trim().equals(tepNewFolderName)) {
                tmpStandard = true;
            } else if (tmpNames[i].startsWith(tepNewFolderName) && tmpNames[i].length() > tmpLength) {
                String tmpEndStr = tmpNames[i].substring(tmpLength, tmpLength + 1).trim();
                try {
                    int tmpEnd = Integer.parseInt(tmpEndStr);
                    tmpVector.add(PIMPool.pool.getKey(tmpEnd));
                } catch (NumberFormatException e) {
                }
            }
        }

        if (!tmpStandard) // 如果不存在新建文件夹直接返回"新建文件夹"
        {
            tmpVector.removeAllElements();
            tmpVector = null;
            return tepNewFolderName;
        }

        if (tmpVector.size() == 0) // 如果只有新建文件夹返回"新建文件夹2"
        {
            tmpVector.removeAllElements();
            tmpVector = null;
            return tepNewFolderName + 2;
        }

        int tmpMin = 0; // 此循环开始便利使vector中的数字从小到大排列
        int tmpMinPos = 0;
        for (int i = 0; i < tmpVector.size(); i++) {
            int tmpPosition1 = ((Integer) tmpVector.get(i)).intValue();
            tmpMin = tmpPosition1;
            tmpMinPos = i;
            for (int j = i + 1; j < tmpVector.size(); j++) {
                int tmpPosition2 = ((Integer) tmpVector.get(j)).intValue();
                if (tmpMin > tmpPosition2) {
                    tmpMin = tmpPosition2;
                    tmpMinPos = j;
                }
            }
            Object tmpObj = tmpVector.get(i);
            tmpVector.set(i, tmpVector.get(tmpMinPos));
            tmpVector.set(tmpMinPos, tmpObj);
        }

        tmpMin = ((Integer) tmpVector.get(0)).intValue();// 取得最小值
        if (tmpMin == 2 || tmpMin == 1) { // 最小值=1或=2
            for (int i = 1; i < tmpVector.size(); i++) { // 数字是否连续 不连续
                int tmpNum = ((Integer) tmpVector.get(i)).intValue();
                tmpMin++;
                if (tmpNum > tmpMin) {
                    tmpVector.removeAllElements();
                    tmpVector = null;
                    return tepNewFolderName + tmpMin;
                }
            }

            tmpVector.removeAllElements(); // 连续
            tmpVector = null;
            return tepNewFolderName + (tmpMin + 1);
        } else // 最小值 !=2 返回"新建文件夹2"
        {
            tmpVector.removeAllElements();
            tmpVector = null;
            return tepNewFolderName + 2;
        }
    }
}
