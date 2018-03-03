/*
 * RemindTask.java
 *
 * Created on 2003年12月23日, 下午1:16
 */

package org.cas.client.platform.cascontrol.thread;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casutil.MessageDialog;

//import emo.pim.pimmodel.PIMRecord;
//import emo.pim.pimview.dialog.reminddialog.RemindDialog;
/**
 */
class TaskOfNoticeMessage extends APIMTimingTask {

    /** Creates a new instance of RemindTask */
    TaskOfNoticeMessage(String prmSpecMessage) {
        pimSpecMessage = prmSpecMessage; /* 显示消息 */
    }

    /**
     * 任务到期执行的动作。
     */
    @Override
    void execute() {
        // if (pimMailRecord == null || pimMailRecord.isEmpty())
        // {
        // return; /*不用提醒*/
        // }
        // PIMRecord[] tmpMailAry = (PIMRecord[]) pimMailRecord.toArray(new PIMRecord[0]);
        // RemindDialog.getInstance().addRemindContent(tmpMailAry);
        // RemindDialog.getInstance().setTitle(pimSpecMessage); /*注意特定消息信息*/
        // boolean isVisible = RemindDialog.getInstance().isVisible();
        // if (isVisible == false) /*如果没有显示*/
        // {
        // RemindDialog.getInstance().setVisible(true); /**/
        // }
        new MessageDialog(CASControl.ctrl.getMainFrame(), pimSpecMessage).show();
    }

    @Override
    void setStatus(
            boolean status) {
    }

    private String pimSpecMessage; /* 特殊信息 */
}
