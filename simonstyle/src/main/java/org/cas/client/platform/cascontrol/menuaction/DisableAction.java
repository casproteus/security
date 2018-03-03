package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

public class DisableAction extends SAction {
    private static DisableAction instance = null;

    public static DisableAction getInstance() {
        if (instance == null) {
            instance = new DisableAction();
        }
        return instance;
    }

    /** Creates a new instance of DisableAction */
    private DisableAction() {
        super(0xffffffff);

        // note: 此句是为邮件菜单添加,
        // 现在右键菜单action还没有和主菜单完全的统一,以后统一后将删除此句
        setEnabled(false);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
    }
}
