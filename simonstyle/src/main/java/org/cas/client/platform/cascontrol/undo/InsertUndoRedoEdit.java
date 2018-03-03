package org.cas.client.platform.cascontrol.undo;

import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;

import org.cas.client.platform.ICASModel;
import org.cas.client.platform.casutil.ErrorUtil;

class InsertUndoRedoEdit extends AbstractUndoableEdit {

    /**
     * Creates a new instance of InsertRecordsUndoEdit 构建器:插入Undo动作
     * 
     * @param mdoel
     *            :数据库
     * @param newRecords
     *            :新的记录容器
     */
    public InsertUndoRedoEdit(ICASModel model, Vector newRecords) {
        this(model, newRecords, false);
    }

    /**
     * Creates a new instance of InsertRecordsUndoEdit 构建器:插入Undo动作
     * 
     * @param mdoel
     *            :数据库
     * @param newRecords
     *            :新的记录容器
     * @param isPaste
     *            : true:粘贴 false:添加
     */
    public InsertUndoRedoEdit(ICASModel model, Vector newRecords, boolean isPaste) {
        this.model = model;
        this.newRecords = newRecords;
        this.isPaste = isPaste;
    }

    /**
     * Undo动作
     */
    public void undo() {
        if (newRecords == null) {
            ErrorUtil.write("0003", new Exception("insert undo vector is null"));
            // /*****/A.s("更新记录 undo of oldRecords ==  空 !");
            return;
        }
        model.permanentlyDeleteRecords(newRecords, false);
    }

    /**
     * redo动作
     */
    public void redo() {
        if (newRecords == null) {
            ErrorUtil.write("0004", new Exception("insert redo vector is null"));
            // /*****/A.s("insert redo vector为空");
            return;
        }
        model.insertRecords(newRecords);
    }

    /**
     * 取得Undo名
     * 
     * @return Undo名
     */
    public String getUndoPresentationName() {
        return isPaste ? "撤销(U)粘贴" : "撤销(U)添加";
    }

    /**
     * 取得redo名
     * 
     * @return Undo名
     */
    public String getRedoPresentationName() {
        return isPaste ? "重复(R)粘贴" : "重复(R)添加";
    }

    /**
     * 销毁Undo/Redo记录
     */
    public void die() {
        model = null;
        if (newRecords != null) {
            newRecords.clear();
            newRecords = null;
        }
    }

    ICASModel model;
    Vector newRecords;
    boolean isPaste;
}
