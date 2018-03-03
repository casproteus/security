package org.cas.client.platform.cascontrol.undo;

import java.util.Vector;

import javax.swing.undo.UndoableEdit;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.commonmenu.PIMActionName;
import org.cas.client.platform.cascontrol.menuaction.UndoRedoAction;
import org.cas.client.platform.pimmodel.PIMRecord;

/**
 * 封装对model的操作，并向Undo Manager添加Undo Edit事件。 本类是本包内唯一的公开类，是本包内所有用于备份用户动作的类（以便用于实现Undo/Redo）的对外窗口。
 */

public class ModelUndoRedoMethod {
    /**
     * 获得缺省的ModelUndoMethod
     */
    private static ModelUndoRedoMethod instance;

    public static ModelUndoRedoMethod getInstance() {
        if (instance == null) {
            instance =
                    new ModelUndoRedoMethod(CASControl.ctrl.getModel(),
                            (UndoRedoAction) PIMActionName.ACTIONS[PIMActionName.ID_EDIT_UNDO]);
        }
        return instance;
    }

    private ModelUndoRedoMethod(ICASModel prmModel, UndoRedoAction prmUndoRedoAction) {
        model = prmModel;
        undoRedoAction = prmUndoRedoAction;
    }

    /**
     * 封装Cut/Paste及其undo动作。移动的起始应用从记录中取得。 TODO：未考虑新建文件夹
     * 
     * @param records
     *            被移动的记录
     * @param newFolder
     *            移动的目的应用
     */
    // public void moveRecords(final Vector records, final int newApp)
    // {
    //
    // }
    //
    // public UndoableEdit getDeleteUndoEdit(Vector records)
    // {
    // return new DeleteUndoRedoEdit(model, records);
    // }

    public UndoableEdit getDeleteUndoEdit(
            int[] prmIds,
            int prmFolderID,
            int prmAppType) {
        return new DeleteUndoRedoEdit(model, prmIds, prmFolderID, prmAppType);
    }

    public UndoableEdit getUpdateUndoRedoEdit(
            PIMRecord oldRecord,
            PIMRecord newRecord) {
        Vector oldRecords = new Vector();
        oldRecords.add(oldRecord);
        Vector newRecords = new Vector();
        newRecords.add(newRecord);
        return getUpdateUndoRedoEdit(oldRecords, newRecords);
    }

    private UndoableEdit getUpdateUndoRedoEdit(
            Vector oldRecords,
            Vector newRecords) {
        // if (updateUndoEdit == null)
        // {
        // updateUndoEdit = new UpdateUndoEdit(model, oldRecords, newRecords);
        undoEdit = new UpdateUndoRedoEdit(model, oldRecords, newRecords);
        // }
        // else
        // {
        // updateUndoEdit.model = model;
        // /*****/A.s("oldRecords "+(oldRecords==null));
        // /*****/A.s("newRecords "+(newRecords==null));
        // updateUndoEdit.setRecords(oldRecords,newRecords);
        // updateUndoEdit.oldRecords = oldRecords;
        // /*****/A.s("updateUndoEdit.oldRecords "+(updateUndoEdit.oldRecords==null));
        // updateUndoEdit.newRecords = newRecords;
        // /*****/A.s("updateUndoEdit.newRecords "+(updateUndoEdit.newRecords==null));
        // }
        return undoEdit;
    }

    public UndoableEdit getInsertUndoEdit(
            Vector newRecord) {
        return getInsertUndoEdit(newRecord, false);
    }

    public UndoableEdit getPasteUndoEdit(
            Vector newRecord) {
        return getInsertUndoEdit(newRecord, true);
    }

    private UndoableEdit getInsertUndoEdit(
            Vector newRecords,
            boolean isPaste) {
        // if (insertUndoEdit == null)
        // {
        // insertUndoEdit = new InsertUndoEdit(model, newRecords, isPaste);
        undoEdit = new InsertUndoRedoEdit(model, newRecords, isPaste);
        // }
        // else
        // {
        // insertUndoEdit.model = model;
        // insertUndoEdit.newRecords = newRecords;
        // insertUndoEdit.isPaste = isPaste;
        // }
        return undoEdit;
    }

    public UndoableEdit getMoveUndoEdit(
            Vector records,
            int oldApp,
            int newApp) {
        undoEdit = new MoveUndoRedoEdit(model, records, oldApp, newApp);
        return undoEdit;
    }

    // public UndoableEdit getMoveUndoEdit(int[] prmIds, int prmAppType, String prmOldFolder, String prmNewFolder)
    // {
    // undoEdit = new MoveUndoRedoEdit(model, prmIds, prmAppType, prmOldFolder, prmNewFolder);
    // return undoEdit;
    // }

    /** 执行操作的model */
    private ICASModel model;
    /** Undo管理器 */
    private UndoRedoAction undoRedoAction;
    /** 用于Undo的UndoableEdit */
    private UndoableEdit undoEdit;
}
