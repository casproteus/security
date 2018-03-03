package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;

public class MarkAllAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        int app = CustOpts.custOps.getActiveAppType();

        // 对已删除项做特殊处理
        if (app != ModelCons.DELETED_ITEM_APP) {
            Vector tmpSeleRecs = CASControl.ctrl.getSelectRecords();
            for (int size = tmpSeleRecs.size(), i = 0; i < size; i++) {
                PIMRecord tmpRecord = (PIMRecord) tmpSeleRecs.get(i);
                int recordID = tmpRecord.getRecordID();
                Object obj = tmpRecord.getFieldValue(ModelDBCons.ICON);
                int iconIndex = (obj) == null ? 2 : ((Byte) obj).intValue();
                iconIndex = iconIndex == 2 ? 1 : iconIndex;
                int[] records = { recordID };
                CASControl.ctrl.getModel().markAsRead(CustOpts.custOps.getActiveAppType(),
                        CustOpts.custOps.getActivePathID(), records, true, iconIndex, true);
            }
        } else {
            Vector tmpSeleRecs = CASControl.ctrl.getSelectRecords();
            // 得到当前视图上所有选中的记录
            if (tmpSeleRecs == null) // 选中记录为null,直接返回.
            {
                return;
            }

            boolean tmpHasNonEMailRecord = false; // 设置是否需要提示警告框的标记.
            PIMRecord tmpRecord = null; // 用于暂存每一条选中的记录.
            int size = tmpSeleRecs.size();

            ArrayList refreshList = new ArrayList(size);
            // 对已删除项做特殊处理
            for (int i = 0; i < size; i++) // 遍历开始.
            {
                tmpRecord = (PIMRecord) tmpSeleRecs.get(i);
                int tmpFolderID = tmpRecord.getInfolderID();

                int type = CASUtility.getAppIndexByFolderID(tmpFolderID);
                if (type == ModelCons.INBOX_APP || type == ModelCons.OUTBOX_APP || type == ModelCons.SENDED_APP
                        || type == ModelCons.DRAFT_APP) // 如果该记录是y记录,则记录该记录的ID.
                {
                    refreshList.add(PIMPool.pool.getKey(i));
                } else if (!tmpHasNonEMailRecord) {
                    SOptionPane.showErrorDialog(MessageCons.W10747);
                    tmpHasNonEMailRecord = true;
                }
            }
            int len = refreshList.size();
            for (int i = 0; i < len - 1; i++) {
                tmpRecord = (PIMRecord) tmpSeleRecs.get(((Integer) refreshList.get(i)).intValue());
                Object obj = tmpRecord.getFieldValue(ModelDBCons.ICON);
                int iconIndex = obj == null ? 2 : ((Byte) obj).intValue();
                iconIndex = iconIndex == 2 ? 1 : iconIndex;
                CASControl.ctrl.getModel().markAsRead(ModelCons.DELETED_ITEM_APP, tmpRecord.getInfolderID(),
                        new int[] { tmpRecord.getRecordID() }, true, iconIndex, false);
            }
            if (len > 0) {
                tmpRecord = (PIMRecord) tmpSeleRecs.get(((Integer) refreshList.get(len - 1)).intValue());
                Object obj = tmpRecord.getFieldValue(ModelDBCons.ICON);
                int iconIndex = obj == null ? 2 : ((Byte) obj).intValue();
                iconIndex = iconIndex == 2 ? 1 : iconIndex;
                CASControl.ctrl.getModel().markAsRead(ModelCons.DELETED_ITEM_APP, tmpRecord.getInfolderID(),
                        new int[] { tmpRecord.getRecordID() }, true, iconIndex, true);
            }
        }
    }
}
