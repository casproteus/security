package org.cas.client.platform.employee.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.pimmodel.PIMRecord;

/***/

public class NewEmployeeAction extends SAction {
    public NewEmployeeAction() {
        this(new PIMRecord());
    }

    /**
     * Creates new ToolsOptionAction 新建联系人
     * 
     * @param prmRecord
     *            : 联系人记录
     */
    public NewEmployeeAction(PIMRecord prmRecord) {
        record = prmRecord;
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent e) {
        MainPane.getApp("Employee").showDialog(CASControl.ctrl.getMainFrame(), new SaveContentsAction(), record, true,
                false);
    }

    private PIMRecord record;
}
