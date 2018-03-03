package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casutil.ErrorUtil;

import com.jeans.trayicon.WindowsTrayIcon;

/*** PIM退出事件 */
public class FileExitAction extends SAction {
    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        CASControl.ctrl.getMainFrame().setVisible(false); // 先隐藏视图，为的是给用户感觉关闭得比较快。
        try {
            CASControl.ctrl.exitSystem(); // 保存所有状态和数据后退出。
        } catch (Exception exp) {
            ErrorUtil.write(exp);
            System.exit(0);
        }
    }
}
