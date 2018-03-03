package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;

public class CategoryAppointmentAction extends SAction {
    /**
     * Creates a new instance of CategoryAppointmentAction
     * 
     * @param flag
     *            : action标记
     */
    public CategoryAppointmentAction(int flag) {
        super(flag);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        // PIMRecord record = null;
        // String str = null;
        // IApplication app = PIMControl.ctrl.getMainPane().getActiveView();
        //
        // if (app instanceof App_Calendar)
        // {
        // record = ((App_Calendar)app).getCurrentAppointRecord();
        // str = record.
        // }

        CategoryDialog categoryDialog = new CategoryDialog(CASControl.ctrl.getMainFrame(), "");
        categoryDialog.show();

        // AppointmentDialog.showDialog(MainPane.getFrame(), new SaveContentsAction(), record, isMeeting,true);
    }

}
