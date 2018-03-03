/*
 * SnoozeRemind.java
 *
 * Created on 2004年4月19日, 下午8:15
 */

package org.cas.client.platform.cascontrol.thread;

import javax.swing.JFrame;

/**
 */
class TaskOfSnooze extends APIMTimingTask {
    /**
     * 构建器
     * 
     * @param prmThisDialog
     *            父窗体
     */
    public TaskOfSnooze(JFrame prmDialog) {
        thisDialog = prmDialog;
    }

    /**
     * 重载父类的方法
     */
    void execute() {
        // 若到指定时刻，提醒对话框不在显示状态，则显示
        if (!thisDialog.isVisible()) {
            thisDialog.setVisible(true);
        }
    }

    void setStatus(
            boolean status) {
    }

    private JFrame thisDialog;
}
