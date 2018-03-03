package org.cas.client.platform.cascontrol.thread;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casutil.CASUtility;

/**
 * 定时接受邮件，如果接受到则转换成数组并显示到提醒对话框中去
 */

class TaskOfReceiveMail extends APIMTimingTask {
    TaskOfReceiveMail(boolean prmNeedNotice) {
        needNotice = prmNeedNotice;
    }

    /**
     * 任务到期执行的动作。
     */
    void execute() {
        CASUtility.setTaskLife(!needNotice); // 标记手动接收任务的状态
        if (needNotice) {
            // Vector tmpVec = MailFacade.getInstance().receiveMail(false);//定时接受邮件
            // if ((tmpVec != null) && (tmpVec.size() != 0))
            // {
            // //设置提醒对话框的内容，并显示。
            // RemindDialog.getInstance().addRemindContent(tmpVec);
            // RemindDialog.getInstance().setVisible(true);
            // }
        } else {
            /**
             * ======================================= 此标记用于通知数据库此时正在执行跟数据库 有关并且比较费时的工作，故此时不能关闭数据库
             * =======================================
             */
            CASControl.ctrl.lockModel();
            // APIMTimingTask tmpTask = ThreadActionsFacade.getInstance().getMailReciveTask();
            // if (tmpTask != null) //从任务列表中删除定时收发邮件任务
            // {
            // PIMTimer.getInstance().cancelTask(tmpTask);
            // }

            // MailFacade.getInstance().receiveMail(true);

            // ThreadActionsFacade.getInstance().startMailTask();
            CASControl.ctrl.unlockModel(); // 表明工作已完成(接上）
        }
        CASUtility.setTaskLife(false); // 恢复初始状态
    }

    public void setStatus(
            boolean status) {
        needNotice = status;
    }

    // 收完邮件候是否需要显示提醒对话盒提示用户的标志。
    private boolean needNotice;
}
