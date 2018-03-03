package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.undo.ModelUndoRedo;
import org.cas.client.platform.pimmodel.PIMRecord;

/** 更新记录动作 */
public class UpdateContactAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        ICASDialog tmpDialog = (ICASDialog) e.getSource();

        // 备份新旧记录
        final PIMRecord tmpNewRecord = (PIMRecord) tmpDialog.getContents();
        final PIMRecord tmpOldRecord =
                CASControl.ctrl.getModel().selectRecord(tmpNewRecord.getAppIndex(), tmpNewRecord.getRecordID(),
                        tmpNewRecord.getInfolderID());

        // 更新记录
        CASControl.ctrl.getModel().updateRecord(tmpNewRecord, true);
        // 加入undo事件
        UndoRedoAction.getUndoAction().addEdit(
                ModelUndoRedo.getInstance().getUpdateUndoRedoEdit(tmpOldRecord, tmpNewRecord));

        tmpDialog.release();
    }
}
