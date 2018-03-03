package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.undo.ModelUndoRedo;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.ErrorConstant;

public class DeleteAction extends SAction {

    /**
     * Creates a new instance of DeleteAction 构建器: 删除记录动作
     */
    public DeleteAction() {
        super(IStatCons.RECORD_SELECTED);
    }

    public DeleteAction(boolean isDel) {
        super(IStatCons.RECORD_SELECTED);
        this.isDel = isDel;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        Vector tmpVec = CASControl.ctrl.getSelectRecords();
        // 得到选中的记录集。

        if (tmpVec == null || tmpVec.size() < 1) // 检查选中记录是否为空。为空则直接返回。TODO：可能应该报错返回。
        {
            return;
        }
        // //如果删除已删除项中的记录，则警告并彻底删除，并不可Undo。
        // if (CustOpts.custOps.getActiveAppType() == ModelCons.DELETED_ITEM_APP || isDel)
        // {
        // if (SOptionPane.showErrorDialog(MessageCons.C20235, ErrorConstant.SELECTED_ITEMS, PIMUtility.EMPTYSTR) == 0)
        // //确实要删除吗？该动作不可恢复。
        // {
        // CASControl.ctrl.getModel().permanentlyDeleteRecords(tmpVec,true);
        // // UndoRedoAction.getUndoAction().discardAllEdits();
        // }
        // }
        // else
        // {
        // 执行删除操作
        CASControl.ctrl.getModel().deleteRecords(tmpVec);
        // tmpVec = CASControl.ctrl.getSelectRecords();
        // //添加undo/redo事件
        // int size = tmpVec.size();
        // int[] ids = new int[size];
        // for (int i = 0; i < size; i++)
        // {
        // ids[i] = ((PIMRecord) (tmpVec.get(i))).getRecordID();
        // }
        // PIMRecord tmpRecord = (PIMRecord) (tmpVec.get(0));
        // int tmpFolder = tmpRecord.getInfolderID();
        // int tmpAppType = tmpRecord.getAppIndex();
        // UndoRedoAction.getUndoAction().addEdit(ModelUndoRedo.getInstance().getDeleteUndoEdit(ids, tmpFolder,
        // tmpAppType));
        // }
    }

    private boolean isDel;
}
