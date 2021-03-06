package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;

public class RemoveFollowupFlagAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        Vector tmpSeleRecVec = CASControl.ctrl.getSelectRecords();
        if (tmpSeleRecVec == null) {
            return;
        }
        PIMRecord tmpRecord;
        for (int i = 0; i < tmpSeleRecVec.size(); i++) {
            tmpRecord = (PIMRecord) tmpSeleRecVec.get(i);
            // 这是一个关联字段"标志状态"
            tmpRecord.setFieldValue(ModelDBCons.FLAGSTATUS, PIMPool.pool.getKey(0));
            // 后续标志的字符串为空
            tmpRecord.setFieldValue(ModelDBCons.FOLLOWFLAGS, CASUtility.EMPTYSTR);
            // 保存是否提醒标志
            tmpRecord.setFieldValue(ModelDBCons.FOLLOWUPCOMPLETE, Boolean.FALSE);
        }
        if (((PIMRecord) tmpSeleRecVec.get(0)).getFieldValues() != null)// 如果记录值为空则不更新
        {
            CASControl.ctrl.getModel().updateRecords(tmpSeleRecVec);
        }
    }
}
