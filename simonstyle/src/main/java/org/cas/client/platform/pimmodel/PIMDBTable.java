package org.cas.client.platform.pimmodel;

import java.util.Hashtable;
import java.util.Iterator;

import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;

class PIMDBTable {
    /**
     * 保存到EIO的系统表中
     */
    final static int SYS_SAVE = 0;
    /**
     * 保存到数据库表中
     */
    final static int DBS_SAVE = 1;
    /**
     * 保存到XML文件中
     */
    final static int XML_SAVE = 2;

    protected String tableName;
    protected Hashtable fieldHash;

    /** Creates a new instance of PIMDBTable */
    PIMDBTable(String tableName, Hashtable fieldHash) {
        this.tableName = tableName;
        this.fieldHash = fieldHash;
    }

    /**
     *
     */
    String getDBPrimaryKey() {
        Iterator temKeys = fieldHash.keySet().iterator();
        String temKey = CASUtility.EMPTYSTR;
        while (temKeys.hasNext()) {
            temKey = (String) temKeys.next();
            if (isPrimaryKey(temKey)) {
                break;
            }
        }

        return temKey;
    }

    /**
     *
     */
    boolean isPrimaryKey(
            String fieldName) {
        return getField(fieldName).isPrimaryKey();
    }

    /**
     * 根据字段名返回字段信息
     * 
     * @param: String fieldName
     * @return: PIMField 请不要使用！
     */
    public PIMField getField(
            String fieldName) {
        return (PIMField) fieldHash.get(fieldName);
    }

    /**
     * 根据字段名返回字段信息
     * 
     * @param: String fieldName
     * @return: PIMField
     */
    PIMField getField(
            Object fieldID) {
        return (PIMField) fieldHash.get(fieldID);
    }

    /**
     * 根据字段名返回字段信息
     * 
     * @param: String fieldName
     * @return: PIMField
     */
    PIMField getField(
            int fieldID) {
        return (PIMField) fieldHash.get(PIMPool.pool.getKey(fieldID));
    }

    /**
     *
     */
    Hashtable getFieldHashtable() {
        return fieldHash;
    }

    /**
     *
     */
    String getFieldsList() {
        StringBuffer sb = new StringBuffer();
        Iterator keys = fieldHash.keySet().iterator();
        while (keys.hasNext()) {
            PIMField field = (PIMField) getField(keys.next());
            sb.append(", ").append(field.getFieldName());
        }

        return sb.substring(2);
    }

    /**
     * 返回数据库表的名字
     * 
     * @return: String
     */
    String getTableName() {
        return tableName;
    }

    /**
     * 存储结构的类型 ISaveType.SYS_SAVE --------------- SysSheet ISaveType.DBS_SAVE --------------- Database Table
     * ISaveType.XML_SAVE --------------- XML File
     * 
     * @return: int
     */
    public int getType() {
        return DBS_SAVE;
    }

    /**
     * 添加新的字段
     * 
     * @param: PIMField field
     * @return: boolean
     */
    public boolean addNewField(
            PIMField field) {
        return false;
    }

    /**
     * 删除字段 注意该字段必须是用户自定义字段才可被删除。
     * 
     * @param: PIMField field
     * @return: boolean
     */
    public boolean deleteField(
            PIMField field) {
        return false;
    }

    /**
     * 修改已存在的字段 注意该字段必须是用户自定义字段才可被修改。
     * 
     * @param: PIMField field
     * @return: boolean
     */
    public boolean modifyField(
            PIMField field) {
        return false;
    }
}
