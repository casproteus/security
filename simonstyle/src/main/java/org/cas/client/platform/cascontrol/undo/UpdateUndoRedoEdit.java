package org.cas.client.platform.cascontrol.undo;

import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;

import org.cas.client.platform.ICASModel;
import org.cas.client.platform.casutil.ErrorUtil;

class UpdateUndoRedoEdit extends AbstractUndoableEdit {

    /**
     * Creates a new instance of UpdateUndoEdit 构建器:更新记录Undo
     * 
     * @param model
     *            : pimModel
     * @param oldRcords
     *            : 被更新记录
     * @param newRecords
     *            : new记录
     */
    public UpdateUndoRedoEdit(ICASModel model, Vector oldRecords, Vector newRecords) {
        this.model = model;
        this.oldRecords = oldRecords;
        this.newRecords = newRecords;
    }

    /**
     * Undo动作
     */
    public void undo() {
        if (oldRecords == null) {
            ErrorUtil.write("0001", new Exception("update undo of oldRecords ==  null !"));
            // /*****/A.s("更新记录 undo of oldRecords ==  空 !");
            return;
        }
        model.updateRecords(oldRecords);
    }

    /**
     * redo动作
     */
    public void redo() {
        if (newRecords == null) {
            ErrorUtil.write("0002", new Exception("update redo of newRecords ==  null !"));
            // /*****/A.s("更新记录 redo of newRecords ==  空 !");
            return;
        }
        model.updateRecords(newRecords);
    }

    /**
     * 取得Undo名
     * 
     * @return Undo名
     */
    public String getUndoPresentationName() {
        return "撤销(U)更新";
    }

    /**
     * 取得redo名
     * 
     * @return Undo名
     */
    public String getRedoPresentationName() {
        return "重复(R)更新";
    }

    /**
     * 设置新的Undo记录
     * 
     * @param prmOld
     *            : 老记录
     * @param prmNew
     *            : 新记录
     */
    public void setRecords(
            Vector prmOld,
            Vector prmNew) {
        oldRecords = prmOld;
        newRecords = prmNew;
    }

    /**
     * 销毁Undo/Redo记录
     */
    public void die() {
        model = null;
        oldRecords.clear();
        newRecords.clear();
        oldRecords = null;
        newRecords = null;
    }

    ICASModel model;
    private Vector oldRecords;
    private Vector newRecords;
}
