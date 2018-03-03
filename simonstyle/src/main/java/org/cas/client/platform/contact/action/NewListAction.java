package org.cas.client.platform.contact.action;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.contact.dialog.selectcontacts.MainListDialog;
import org.cas.client.platform.pimmodel.PIMRecord;

public class NewListAction extends SAction {
    /**
     * Creates a new instance of NewListAction 新建通讯组
     * 
     * @param prmRecord
     *            :通讯组记录
     */
    public NewListAction(PIMRecord prmRecord) {
        record = prmRecord;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        new MainListDialog(CASControl.ctrl.getMainFrame(), new SaveContentsAction(), record).show();
    }

    private PIMRecord record;

}
