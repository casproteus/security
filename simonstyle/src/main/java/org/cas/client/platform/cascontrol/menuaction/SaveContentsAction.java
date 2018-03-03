package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.undo.ModelUndoRedo;
import org.cas.client.platform.contact.dialog.selectcontacts.MainListDialog;
import org.cas.client.platform.pimmodel.PIMRecord;

/**
 * 新建记录保存动作 本类暂时引用了一个非接口类MainListDialog.祥见代码中注释.
 */
public class SaveContentsAction extends SAction {
    /* action is performed */
    public void actionPerformed(
            ActionEvent e) {
        ICASDialog tmpDialog = (ICASDialog) e.getSource();
        PIMRecord tmpRecord = (PIMRecord) tmpDialog.getContents();
        boolean isOpened = tmpRecord.isOpened();

        // 添加记录
        ICASModel model = CASControl.ctrl.getModel();
        if (isOpened)
            model.updateRecord(tmpRecord, true);
        else
            model.insertRecord(tmpRecord, true);
        // 加入undo事件@TODO:可以另外提供一个方法，省却new一个Vector。
        Vector tmpVec = new Vector();
        tmpVec.add(tmpRecord);
        UndoRedoAction.getUndoAction().addEdit(ModelUndoRedo.getInstance().getInsertUndoEdit(tmpVec));

        tmpDialog.release();
    }
}
