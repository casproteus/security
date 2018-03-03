package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.dialog.FollowFlagsDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;

public class FollowUpAction extends SAction {

    /**
     * Creates a new instance of FollowUpAction
     * 
     * @param flag
     *            : 定义action内部状态
     */
    public FollowUpAction() {
        super(IStatCons.IS_MARK);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        Vector tmpSeleRecVec = CASControl.ctrl.getSelectRecords();
        if (tmpSeleRecVec != null) {
            FollowFlagsDialog tmpDlg = new FollowFlagsDialog(CASControl.ctrl.getMainFrame(), tmpSeleRecVec);
            tmpDlg.show();
            PIMRecord tmpRecordInDlg = tmpDlg.getContents();
            tmpDlg.release();
            if (tmpRecordInDlg == null) // 如果是点取消关闭的,则不做事.
            {
                return;
            }
            tmpDlg = null;
            PIMRecord tmpRecord = (PIMRecord) tmpSeleRecVec.get(0); // 用于暂存每一条被选中的记录。
            for (int i = 0; i < tmpSeleRecVec.size(); i++) {
                tmpRecord = (PIMRecord) tmpSeleRecVec.get(i);
                // 后续标志的字符串为空
                tmpRecord.setFieldValue(ModelDBCons.FOLLOWFLAGS, tmpRecordInDlg.getFieldValue(ModelDBCons.FOLLOWFLAGS));
                // 保存是否提醒标志
                tmpRecord.setFieldValue(ModelDBCons.FOLLOWUPCOMPLETE,
                        tmpRecordInDlg.getFieldValue(ModelDBCons.FOLLOWUPCOMPLETE));
                // 这是一个关联字段"标志状态"
                tmpRecord.setFieldValue(ModelDBCons.FLAGSTATUS, tmpRecordInDlg.getFieldValue(ModelDBCons.FLAGSTATUS));
                // 保存提醒时间
                if (tmpRecordInDlg.getFieldValue(ModelDBCons.FOLOWUPENDTIME) != null) // @Note:这一项可能为null，故需判断。
                {
                    tmpRecord.setFieldValue(ModelDBCons.FOLOWUPENDTIME,
                            tmpRecordInDlg.getFieldValue(ModelDBCons.FOLOWUPENDTIME));
                }
            }
            ICASModel tmpModel = CASControl.ctrl.getModel();
            if (tmpModel == null) {
                return;
            }
            // 日记页面视图特殊处理
            if (CustOpts.custOps.getActiveAppType() == ModelCons.DIARY_APP) {
                PIMRecord tmpRecordInModel = null;
                int tmpDiaryAppID = CASUtility.getAPPNodeID(CustOpts.custOps.APPNameVec.indexOf("Diary"));
                int tmpAppointAppID = CASUtility.getAPPNodeID(CustOpts.custOps.APPNameVec.indexOf("Appointment"));
                if (CustOpts.custOps.getActivePathID() == tmpRecord.getRecordID() // 系统定义的文件夹
                        || CustOpts.custOps.getActivePathID() == tmpDiaryAppID // 日记文件夹
                        || CustOpts.custOps.getActivePathID() == tmpAppointAppID) // 日历文件夹
                {
                    tmpRecordInModel =
                            tmpModel.selectRecord(ModelCons.DIARY_APP, tmpRecord.getRecordID(), tmpDiaryAppID);
                    tmpRecordInDlg.setInfolderID(tmpDiaryAppID);
                    if (tmpRecordInModel.getFieldValues() == null) {
                        tmpRecordInModel =
                                tmpModel.selectRecord(ModelCons.DIARY_APP, tmpRecord.getRecordID(), tmpDiaryAppID);
                        tmpRecordInDlg.setInfolderID(tmpDiaryAppID);
                    }
                } else // 如果是用户自己定义的文件夹
                {
                    tmpRecordInModel =
                            tmpModel.selectRecord(ModelCons.DIARY_APP, tmpRecord.getRecordID(),
                                    CustOpts.custOps.getActivePathID());
                    tmpRecordInDlg.setInfolderID(CustOpts.custOps.getActivePathID());
                }
                if (tmpRecordInModel.getFieldValues() == null) {
                    if (!(tmpRecordInDlg.getFieldValue(ModelDBCons.FOLLOWFLAGS) == null
                            && tmpRecordInDlg.getFieldValue(ModelDBCons.FOLLOWUPCOMPLETE) == null
                            && tmpRecordInDlg.getFieldValue(ModelDBCons.FLAGSTATUS) == null && tmpRecordInDlg
                                .getFieldValue(ModelDBCons.FOLOWUPENDTIME) == null)) {
                        tmpRecordInDlg.setFieldValue(ModelDBCons.ID, new Integer(tmpRecord.getRecordID()));
                        tmpRecordInDlg.setAppIndex(ModelCons.DIARY_APP);
                        tmpModel.insertRecord(tmpRecordInDlg, false);
                    }
                } else {
                    CASControl.ctrl.getModel().updateRecord(tmpRecordInDlg, false); // 传入Vecto
                }
            } else {
                CASControl.ctrl.getModel().updateRecords(tmpSeleRecVec); // 传入Vecto
            }
        }
    }
}
