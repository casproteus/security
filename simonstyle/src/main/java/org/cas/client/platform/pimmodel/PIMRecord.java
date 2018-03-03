package org.cas.client.platform.pimmodel;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.PIMPool;

public class PIMRecord implements Cloneable, Serializable {
    /** Creates a new instance of PIMRecord */
    public PIMRecord() {
        hash = new Hashtable();
    }

    /**
     * 该方法生成，修改项目中的记录。
     * 
     * @param Hashtable
     *            记录中包含的字段。
     * @param value
     *            记录中对应字段的数值。
     */
    public void setFieldValues(
            Hashtable r) {
        this.hash = r;
    }

    /**
     * 该方法得到记录中字段的所有数值。
     * 
     * @return 记录中字段的所有数值的列表
     */
    public Hashtable getFieldValues() {
        return hash;
    }

    /**
     * 该方法根据特定的名称，得到记录中字段对应的值。
     * 
     * @param name
     *            记录中字段的名称。
     * @return 记录中字段名称对应的数值。
     */
    public Object getFieldValue(
            Object name) {
        if (hash != null)
            if (hash.containsKey(name))
                return hash.get(name);

        return null;
    }

    /**
     * 该方法根据特定的FieldID，得到记录中字段对应的值。 对于未知的字段ID不作处理。
     * 
     * @param int fieldID 记录中字段的ID。
     * @return 记录中字段名称对应的数值。
     */
    public Object getFieldValue(
            int fieldID) {
        return getFieldValue(PIMPool.pool.getKey(fieldID));
    }

    /**
     * 该方法根据特定的名称，得到记录中字段对应的值。 对于未知的字段不作处理。
     * 
     * @param prmName
     *            记录中字段的名称。
     * @param Object
     *            prmValue 记录中字段的值。
     * @return 记录中字段名称对应的数值。
     */
    public void setFieldValue(
            Object prmName,
            Object prmValue) {
        if (hash != null) {
            if (hash.containsKey(prmName)) {
                Object oldValue = hash.get(prmName);
                if (!oldValue.equals(prmValue)) {
                    hash.put(prmName, prmValue);
                }
            } else {
                hash.put(prmName, prmValue);
            }
        }
    }

    /**
     * 该方法根据特定的FieldID，设置记录中字段对应的值。
     * 
     * @param int FieldID 记录中字段的ID。
     * @param Object
     *            prmValue 记录中字段的值。
     */
    public void setFieldValue(
            int prmFieldID,
            Object prmValue) {
        setFieldValue(PIMPool.pool.getKey(prmFieldID), prmValue);
    }

    /**
     * 该方法返回当前记录的ID号
     * 
     * @return 当前记录的ID号
     */
    public int getRecordID() {
        return recordID;
    }

    /**
     * 该方法返回当前记录的ID号
     * 
     * @return 当前记录的ID号
     */
    public void setRecordID(
            int prmID) {
        recordID = prmID;
    }

    /**
     * 判断当前的记录是否已经删除
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * 设置当前的记录为已经被删除
     */
    public void setDeleted(
            boolean prmIsDeleted) {
        isDeleted = prmIsDeleted;
        hash.put(PIMPool.pool.getKey(1), Boolean.valueOf(prmIsDeleted));
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(
            boolean opened) {
        isOpened = opened;
    }

    /**
     * 设置INFOLDER路径
     */
    public void setInfolderID(
            int prmInfolderID) {
        hash.put(PIMPool.pool.getKey(ModelCons.folderIDIdx), PIMPool.pool.getKey(prmInfolderID));
    }

    /** 得到当前的记录的ID @NOTE:可能存在某个记录hash不为null,但是却没有ID的情况,因为如转发邮件或新建记录中在存入数据库之前是没有ID的. */
    public int getInfolderID() {
        Object tmpObj = hash.get(PIMPool.pool.getKey(ModelCons.folderIDIdx));
        if (tmpObj != null)
            return ((Integer) tmpObj).intValue();
        else
            return -1;
    }

    /**
     * 设置存储的类型, 如：联系人，通讯组
     * 
     * @param int type
     */
    public void setAppIndex(
            int prmType) {
        type = prmType;
    }

    /**
     * 获取存储的类型， 如：联系人，通讯组
     * 
     * @return int
     */
    public int getAppIndex() {
        return type;
    }

    /**
     */
    public Object clone() {
        /*
         * try { return super.clone(); } catch(Exception e) { return null; }
         */
        PIMRecord tmpRecord = new PIMRecord();
        tmpRecord.setAppIndex(this.type);

        tmpRecord.setRecordID(this.recordID);
        tmpRecord.setTaskFlag(this.taskFlag);
        tmpRecord.setDeleted(this.isDeleted);
        tmpRecord.setOpened(this.isOpened);

        Hashtable table = new Hashtable();
        Enumeration tmpEnum = hash.keys();
        while (tmpEnum.hasMoreElements()) {
            Object key = tmpEnum.nextElement();
            Object value = hash.get(key);
            table.put(key, value);
        }
        tmpRecord.setFieldValues(table);
        return tmpRecord;
    }

    /**
     * Getter for property taskFlag.
     * 
     * @return Value of property taskFlag.
     */
    public boolean isTaskFlag() {
        return taskFlag;
    }

    /**
     * Setter for property taskFlag.
     * 
     * @param taskFlag
     *            New value of property taskFlag.
     */
    public void setTaskFlag(
            boolean prmTaskFlag) {
        taskFlag = prmTaskFlag;
    }

    private boolean isOpened;
    // @NOTE:此处标识此记录已删除
    private boolean isDeleted = false;
    // 存储记录的哈西表
    private Hashtable hash;
    // 储的类型， 如：联系人，通讯组
    private int type;
    // 存储记录的ID值
    protected int recordID = -1;
    // 是转发还是发送的任务
    private boolean taskFlag;
}
