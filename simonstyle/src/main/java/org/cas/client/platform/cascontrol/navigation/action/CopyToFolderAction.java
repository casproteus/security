package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;



/**
 * @NOTE:如果是从任何地方拷贝记录到已删除项下的某个文件夹,拷贝到已删除项各表中的记录和被删除到已删除项中的各记录的
 * Infoder字段是不同的----通过删除动作进入已删除项表的记录的infolder字段里记的是没有被删除时所在表对应的路径.
 * 通过拷贝动作进入已删除项表中的记录的infolder字段中记的是用户通过路径选择对话盒中的到的目标地址,也就是当前所在的已
 * 删除项中的表.所以通过拷贝动作进入已删除项表中的记录是::::不:可:恢:复:的:::::!!!
 */

public class CopyToFolderAction extends SAction
{
    /** Creates a new instance of CopyToFolderAction
     * 构建器: 复制记录到指定文件夹
     */
    public CopyToFolderAction()
    {
        super(IStatCons.RECORD_SELECTED);
    }

    /** Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        //取得选择的记录(其实是从当前正在显示的table的某行对应的tablemodel--暂时是
		//二维Vector--中新构造出来的一些Record组成的Vector,所以可以随便修改,不必担心源数据被改.
        Vector tmpSeleRecsVec = CASControl.ctrl.getSelectRecords();
        if (tmpSeleRecsVec != null && tmpSeleRecsVec.size() > 0)
        {
            MoveFolderDialog tmpMovedialog = new MoveFolderDialog(CASControl.ctrl.getMainFrame());
            tmpMovedialog.show();	
            int tmpSelePathID = tmpMovedialog.getSelectedPathID();//取得要移动到的目标位置
            int tmpApp = CASUtility.getAppIndexByFolderID(tmpSelePathID);	 //取得要移动到的位置所属于的应用类型(提高速度用)
			
			PIMRecord tmpRecord = null;
            for (int i = 0; i<tmpSeleRecsVec.size();i++)
            {
                tmpRecord = (PIMRecord)tmpSeleRecsVec.get(i);
                tmpRecord.setInfolderID(tmpSelePathID);	//为tmpSeleRecsVec中每一个新构造的record设置新的路径信息.
				tmpRecord.setAppIndex(tmpApp);			//为tmpSeleRecsVec中每一个新构造的record设置新的appType.
           }
           
           CASControl.ctrl.getModel().insertRecords(tmpSeleRecsVec);	//向model中插入复制的记录
        }
    }
}

