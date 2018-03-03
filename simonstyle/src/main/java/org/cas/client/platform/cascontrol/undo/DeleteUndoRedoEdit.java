package org.cas.client.platform.cascontrol.undo;

import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;

import org.cas.client.platform.ICASModel;
import org.cas.client.platform.pimmodel.PIMRecord;

/**
 * 删除一条或多条记录时，通过本类的
 */

class DeleteUndoRedoEdit extends AbstractUndoableEdit {
    /**
     * Creates a new instance of InsertRecordsUndoEdit 构建器:插入Undo动作
     * 
     * @param mdoel
     *            :数据库
     * @param records
     *            :记录容器
     */
    // public DeleteUndoRedoEdit(IModel model, Vector records)
    // {
    // this.model = model;
    // this.records = records;
    // }
    /**
     * Creates a new instance of InsertRecordsUndoEdit 构建器:插入Undo动作
     * 
     * @param mdoel
     *            :数据库
     * @param records
     *            :记录容器
     */
    public DeleteUndoRedoEdit(ICASModel prmModel, int prmIds[], int prmFolderID, int prmAppType) {
        model = prmModel;
        ids = prmIds;
        folderID = prmFolderID;
        appType = prmAppType;
    }

    /**
     * Undo动作
     */
    public void undo() {
        // A.s("undo before----folder : "+folder+"; appType : "+appType);
        // String id = "ids : ";
        // for(int i = 0; i < ids.length; i++)
        // {
        // id += ids[i] + "; ";
        // }
        // A.s("" + id);
        // model.restoreDeletedRecords(records);
        Vector tmpRecords = model.selectRecords(appType, ids, folderID);
        if (tmpRecords == null || tmpRecords.size() < 1) {
            return;
        }
        model.restoreDeletedRecords(tmpRecords);
        int size = tmpRecords.size();
        for (int i = 0; i < size; i++) {
            ids[i] = ((PIMRecord) (tmpRecords.get(i))).getRecordID();
        }
        PIMRecord tmpRecord = (PIMRecord) (tmpRecords.get(0));
        folderID = tmpRecord.getInfolderID();
        appType = tmpRecord.getAppIndex();
        // A.s("undo after ----folder : "+folder+"; appType : "+appType);
        // id = "ids : ";
        // for(int i = 0; i < ids.length; i++)
        // {
        // id += ids[i] + "; ";
        // }
        // A.s("" + id);
    }

    /**
     * redo动作
     */
    public void redo() {
        // A.s("redo before----folder : "+folder+"; appType : "+appType);
        // String id = "ids : ";
        // for(int i = 0; i < ids.length; i++)
        // {
        // id += ids[i] + "; ";
        // }
        // A.s("" + id);
        // model.restoreDeletedRecords(records);
        Vector tmpRecords = model.selectRecords(appType, ids, folderID);
        if (tmpRecords == null || tmpRecords.size() < 1) {
            return;
        }
        model.deleteRecords(tmpRecords);
        int size = tmpRecords.size();
        for (int i = 0; i < size; i++) {
            ids[i] = ((PIMRecord) (tmpRecords.get(i))).getRecordID();
        }
        PIMRecord tmpRecord = (PIMRecord) (tmpRecords.get(0));
        folderID = tmpRecord.getInfolderID();
        appType = tmpRecord.getAppIndex();
        // A.s("redo after ----folder : "+folder+"; appType : "+appType);
        // id = "ids : ";
        // for(int i = 0; i < ids.length; i++)
        // {
        // id += ids[i] + "; ";
        // }
        // A.s("" + id);
    }

    /**
     * 取得Undo名
     * 
     * @return Undo名
     */
    public String getUndoPresentationName() {
        return "撤销(U)删除";
    }

    /**
     * 取得redo名
     * 
     * @return Undo名
     */
    public String getRedoPresentationName() {
        return "重复(R)删除";
    }

    /**
     * 销毁Undo/Redo记录。改方法释放保存的records所占内存
     */
    public void die() {
        model = null;
        ids = null;
        // records.clear();
        // records = null;
    }

    private ICASModel model;
    // private Vector records;
    private int[] ids;
    private int folderID;
    private int appType;
}
