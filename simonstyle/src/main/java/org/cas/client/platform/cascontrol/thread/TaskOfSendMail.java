package org.cas.client.platform.cascontrol.thread;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casutil.CASUtility;

class TaskOfSendMail extends APIMTimingTask {

    /**
     * Creates a new instance of SendTask 构建器: 处理发送邮件任务
     */
    TaskOfSendMail(boolean prmBool) {
        this.bool = prmBool;
    }

    /**
     * 任务到期执行的动作。
     */
    void execute() {
        CASUtility.setTaskLife(bool); // 标记手动发送任务的状态
        CASControl.ctrl.lockModel();
        // 此标记用于通知数据库此时正在执行跟数据库有关并且比较费时的工作，故此时不能关闭数据库。
        // MailFacade.getInstance().sendMail(bool);
        CASControl.ctrl.unlockModel(); // 标记工作已完成
        CASUtility.setTaskLife(false); // 恢复初始状态
    }

    public void setStatus(
            boolean status) {
        bool = status;
    }

    private boolean bool;
}
