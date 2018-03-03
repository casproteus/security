package org.cas.client.platform.cascontrol.undo;

import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;

import org.cas.client.platform.ICASModel;
import org.cas.client.platform.pimmodel.PIMRecord;

class MoveUndoRedoEdit extends AbstractUndoableEdit {
    /**
     * Creates a new instance of MoveRecordsUndoEdit
     * 
     * @param model
     *            :数据库model
     * @param records
     *            :记录容器
     * @param oldApp
     *            : 原来的应用类型
     * @param newApp
     *            : 新的应用类型
     */
    public MoveUndoRedoEdit(ICASModel model, Vector records, int oldApp, int newApp) {
        this.model = model;
        this.records = records;
        this.oldApp = oldApp;
        this.newApp = newApp;
    }

    /**
     * Creates a new instance of MoveRecordsUndoEdit
     * 
     * @param model
     *            :数据库model
     * @param oldApp
     *            : 原来的应用类型
     * @param newApp
     *            : 新的应用类型
     * @param id
     *            : 记录在新的表中的id，undo、redo中可以通过此id得到record并进行操作
     */
    // public MoveUndoRedoEdit(IModel model, int[] prmIds, int prmAppType, String prmOldFolder, String prmNewFolder)
    // {
    // this.model = model;
    // this.ids = prmIds;
    // this.appType = prmAppType;
    // this.oldFolder = prmOldFolder;
    // this.newFolder = prmNewFolder;
    // }

    /**
     * undo动作
     */
    public void undo() {
        changeApp(model, records, oldApp);
    }

    /**
     * redo动作
     */
    public void redo() {
        changeApp(model, records, newApp);
    }

    /**
     * 移动指定记录到指定应用。 此方法仅由moveRecords调用，在moveRecords中已对records进行非空校验， 故在此方法中不进行非空校验。
     * 
     * @param records
     *            被移动的记录
     * @param newApp
     *            移动的目的应用
     * @Called by: ModelUndoMethod
     */
    private static void changeApp(
            ICASModel model,
            Vector records,
            int newApp) {
        model.deleteRecords(records);
        final int size = records.size();
        PIMRecord tmpRecord;
        for (int i = 0; i < size; i++) {
            tmpRecord = (PIMRecord) records.elementAt(i);
            tmpRecord.setAppIndex(newApp);
        }
        model.insertRecords(records);
    }

    /**
     * 取得Undo名
     * 
     * @return Undo名
     */
    public String getUndoPresentationName() {
        return "撤销(U)移动";
    }

    /**
     * 取得redo名
     * 
     * @return Undo名
     */
    public String getRedoPresentationName() {
        return "重复(R)移动";
    }

    /**
     * 销毁Undo/Redo记录
     */
    public void die() {
        model = null;
        records.clear();
        records = null;
    }

    ICASModel model;
    Vector records;
    int oldApp;
    int newApp;
    // private String oldFolder;
    // private String newFolder;
    // private int[] ids;
    // private int appType;
}
