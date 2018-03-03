package org.cas.client.platform.contact.action;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.pimmodel.PIMRecord;

public class SendAppointmentToAction extends SAction {
    /**
     * 构建器: 右键菜单中发送约会或会议请求到指定联系人动作
     * 
     * @param prmIsMeeting
     *            : true: 会议 false:约会
     */
    public SendAppointmentToAction(boolean prmIsMeeting) {
        isMeeting = prmIsMeeting;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        Vector tmpRecords = CASControl.ctrl.getSelectRecords();
        if (tmpRecords != null && tmpRecords.size() > 0) {
            Hashtable tmpValueHash = new Hashtable();
            boolean tmpContainNonContactRecord = false;
            String tmpReceiveAddress = CASUtility.EMPTYSTR;
            PIMRecord tmpRecord = null;
            Object tmpEmailObj = null;
            for (int i = 0; i < tmpRecords.size(); i++) {
                tmpRecord = (PIMRecord) tmpRecords.get(i);
                if (tmpRecord.getAppIndex() == ModelCons.CONTACT_APP) {
                    tmpEmailObj = tmpRecord.getFieldValue(ContactDefaultViews.EMAIL);

                    if (tmpEmailObj == null) // emialAddress 仍然为空的情况.
                    {
                        Object tmpValue = tmpRecord.getFieldValue(ContactDefaultViews.TYPE);
                        if (tmpValue instanceof Short && ((Short) tmpValue).intValue() == 1) // 虽然email仍然为null,但如果该条项目是通讯组列表,也视为有效.
                        {
                            int tmpIDs[] =
                                    CASUtility.stringToArray((String) tmpRecord
                                            .getFieldValue(ContactDefaultViews.MEMBERLIST));
                            for (int j = 0; j < tmpIDs.length; j++) {
                                tmpRecord =
                                        CASControl.ctrl.getModel().selectRecord(ModelCons.CONTACT_APP, tmpIDs[j],
                                                tmpRecord.getInfolderID());
                                tmpEmailObj = tmpRecord.getFieldValue(ContactDefaultViews.EMAIL);

                                if (tmpEmailObj != null) {
                                    tmpReceiveAddress =
                                            tmpReceiveAddress
                                                    .concat(CASUtility.DOUBlEQUOTATION)
                                                    .concat((String) tmpRecord
                                                            .getFieldValue(ContactDefaultViews.SUBJECT))
                                                    .concat(CASUtility.DOUBlEQUOTATION)
                                                    .concat(CASUtility.LEFTSHARPBRACKET).concat((String) tmpEmailObj)
                                                    .concat(CASUtility.RIGHTSHARPBRACKET).concat(CASUtility.COMMA);
                                }
                            }
                        } else // 说明:选中联系人不含有电子邮件地址信息,跳过,并设置标记,使在秀邮件窗体前报错误信息框.
                        {
                            if (isMeeting) // 因为要发送邮件,所以没有邮件地址的联系人(已经确定不会是通讯组列表)要导致错误警告.
                            {
                                tmpContainNonContactRecord = true;
                            } else // 因为只需要加入联系人中,所以没有邮件地址无所谓,以空串代替.
                            {
                                tmpReceiveAddress =
                                        tmpReceiveAddress.concat(CASUtility.DOUBlEQUOTATION)
                                                .concat((String) tmpRecord.getFieldValue(ContactDefaultViews.SUBJECT))
                                                .concat(CASUtility.DOUBlEQUOTATION).concat(CASUtility.LEFTSHARPBRACKET)
                                                .concat(CASUtility.RIGHTSHARPBRACKET).concat(CASUtility.COMMA);
                            }
                        }
                    } else {
                        tmpReceiveAddress =
                                tmpReceiveAddress.concat(CASUtility.DOUBlEQUOTATION)
                                        .concat((String) tmpRecord.getFieldValue(ContactDefaultViews.SUBJECT))
                                        .concat(CASUtility.DOUBlEQUOTATION).concat(CASUtility.LEFTSHARPBRACKET)
                                        .concat((String) tmpEmailObj).concat(CASUtility.RIGHTSHARPBRACKET)
                                        .concat(CASUtility.COMMA);
                    }
                } else // 说明选中项不是联系人
                {
                    tmpContainNonContactRecord = true;
                }
            }
            if (tmpContainNonContactRecord) {
                // "选中的项中一项或多项因为不是联系人或不含有电子邮件地址信息,向这类项目的投递动作无法实现,此类项目已被自动忽略."
                SOptionPane.showErrorDialog(MessageCons.W10540);
            }
            if (tmpReceiveAddress.length() > 5) // 估计有效的联系人至少要有6位吧:---"a"<>;---
            {
                tmpValueHash.put(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT), tmpReceiveAddress);
                if (isMeeting) {
                    tmpValueHash.put(PIMPool.pool.getKey(ModelDBCons.CALENDAR_ADDRESSEE), tmpReceiveAddress);
                }
                tmpRecord.setFieldValues(tmpValueHash);
                tmpRecord.setAppIndex(ModelCons.CALENDAR_APP);
                tmpRecord.setRecordID(-1); // 表明是新建的约会,直接新建对话盒即可,不必到HashTable中寻找是否已经存在.
                MainPane.getApp("Appointment").showDialog(CASControl.ctrl.getMainFrame(), new SaveContentsAction(),
                        tmpRecord, isMeeting, true);
            }
        }
    }

    private boolean isMeeting;
}
