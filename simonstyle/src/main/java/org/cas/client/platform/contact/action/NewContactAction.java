package org.cas.client.platform.contact.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.pimmodel.PIMRecord;

/***/

public class NewContactAction extends SAction {
    public NewContactAction() {
        this(new PIMRecord());
    }

    /**
     * Creates new ToolsOptionAction 新建联系人
     * 
     * @param prmRecord
     *            : 联系人记录
     */
    public NewContactAction(PIMRecord prmRecord) {
        record = prmRecord;
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent e) {
        MainPane.getApp("Contact").showDialog(CASControl.ctrl.getMainFrame(), new SaveContentsAction(), record, true,
                false);
    }

    private PIMRecord record;
}
