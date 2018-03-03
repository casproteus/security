package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;

/***/
public class SelectAllAction extends SAction {

    /**
     * Creates a new instance of SelectAllAction 构建器: 选择所有动作
     */
    public SelectAllAction() {
        super(IStatCons.SELECT_ALL);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        CASControl.ctrl.getMainPane().seleteAllRecord();
    }

}
