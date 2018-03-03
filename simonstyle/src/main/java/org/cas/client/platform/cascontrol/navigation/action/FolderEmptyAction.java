package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.PaneConsts;



public class FolderEmptyAction extends SAction
{

    /** Creates a new instance of FolderEmptyAction
     * 清空文件夹 
     */
    public FolderEmptyAction()
    {
        super(IStatCons.FOLDER_SELECTED);
    }

    public FolderEmptyAction(int flag)
    {
        super(flag);
    }
    
    /** Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        //取得导航面板上的树
        CASTree tree = CASControl.ctrl.getFolderTree();
        //取得"首页"的路径
        TreePath rootpath = tree.getPathFromRoot(PaneConsts.HEAD_PAGE);
        //取得"已删除项"的路径
        TreePath path = tree.getNodePath(null, null);
        if (path == null)
        {
            return ;
        }
        CASNode node = (CASNode)path.getLastPathComponent();
        int childCount = node.getChildCount();
        //保存"已删除项"下的子结点
        TreePath [] delpaths = new TreePath[childCount];
        for (int i = 0; i < childCount;i++)
        {
        	CASNode childNode = (CASNode)node.getChildAt(i);
            delpaths[i] = new TreePath(childNode.getPath());
        }
        //删除"已删除项"下的文件夹
        for (int i = 0; i<childCount;i++)
        {
            tree.removeNode(delpaths[i]);
        } //重新保存该路径
        CustOpts.custOps.setKeyAndValue(path.toString(),CASUtility.EMPTYSTR);
        ICASModel model = CASControl.ctrl.getModel();
//        Vector vector = model.getAllRecord(model.getViewInfo(ModelConstants.DELETED_ITEM_APP,0,PIMUtility.getTreePath(ModelConstants.DELETED_ITEM_APP)));
//        if (vector == null) 
//        {
//            return ;
//        }
//        model.permanentlyDeleteRecords(vector,true);

    }
}
