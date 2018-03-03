package org.cas.client.platform.pimmodel.event;

import java.util.EventObject;

import org.cas.client.platform.ICASModel;
import org.cas.client.platform.pimmodel.PIMRecord;

/**
 */

public class PIMModelEvent extends EventObject {
    // 没有事件
    public static final int NO_EVENT = -1;
    // 插入一条记录
    public static final int INSERT = 0;
    // 修改一条记录
    public static final int UPDATE = 1;
    // 删除一条记录
    public static final int DELETE = 2;
    // 修改了指定视图的显示字段
    public static final int UPDATE_FIELD = 3;
    /**
     * 以下的值是为viewInfo发生改变时定义的，参照PIMViewInfo中的变量
     */
    // 添加或者删除了字段，即：字段产生了变化
    public static final int FIELD_CHANGED = 4;
    // 恢复默认值的时候发的事件类型
    public static final int RESET_VIEWINFO = 5;
    // 视图格式发生变化
    public static final int UPDATE_FORMAT = 6;

    // 排序变化
    // public static final int SORT_CHANGED = 4;
    // 筛选变化
    // public static final int FILTER_CHANGED = 5;
    // 分组变化
    // public static final int GROUP_CHANGED = 6;
    // 其他设置变化，包括字体，网格线等
    // public static final int OTHER_SET_CHANGED = 7;

    /**
     * Creates a new instance of PIMModelEvent
     * 
     * @param source
     *            : 更新事件源 模型更新事件
     */
    public PIMModelEvent(ICASModel source) {
        this(source, UPDATE);
    }

    /**
     * 指定事件类型
     * 
     * @param source
     *            : 事件源
     * @param type
     *            : 事件类型
     */
    public PIMModelEvent(ICASModel source, int type) {
        super(source);
        this.type = type;
    }

    /**
     * 指定事件类型及视图ID号
     * 
     * @param source
     *            : 事件源
     * @param type
     *            : 事件类型
     * @param prmAppType
     *            : 视图的id号
     */
    public PIMModelEvent(ICASModel source, int type, int prmAppType) {
        super(source);
        this.type = type;
        appType = prmAppType;
    }

    /**
     * 指定事件类型\视图ID号\对应记录
     * 
     * @param source
     *            : 事件源
     * @param type
     *            : 事件类型
     * @param prmAppType
     *            : 视图的id号
     * @param record
     *            : 变化的记录数组
     */
    public PIMModelEvent(ICASModel source, int type, int prmAppType, PIMRecord record[]) {
        super(source);
        this.type = type;
        appType = prmAppType;
        this.record = record;
    }

    /**
     * 指定事件类型\视图ID号\对应记录
     * 
     * @param source
     *            : 事件源
     * @param type
     *            : 事件类型
     * @param prmAppType
     *            : 视图的id号
     * @param recordID
     *            : 变化的记录的ID号
     */
    public PIMModelEvent(ICASModel source, int type, int prmAppType, int recordID) {
        super(source);
        this.type = type;
        appType = prmAppType;
        this.recordID = recordID;
    }

    /**
     * 指定事件类型\视图ID号\对应记录数组\记录ID
     * 
     * @param source
     *            : 事件源
     * @param type
     *            : 事件类型
     * @param prmAppType
     *            : 视图的id号
     * @param recordID
     *            : 变化的记录的ID号
     * @param record
     *            : 变化的记录数组
     */
    public PIMModelEvent(ICASModel source, int type, int prmAppType, int recordID, PIMRecord record[]) {
        super(source);
        this.type = type;
        appType = prmAppType;
        this.recordID = recordID;
        this.record = record;
    }

    /**
     * 返回事件的类型 -1 -------------------- DELETE 0 -------------------- UPDATE 1 -------------------- INSERT
     * 
     * @return: int
     */
    public int getType() {
        return type;
    }

    /**
     * 取得变化视图的Id号
     */
    public int getAppType() {
        return appType;
    }

    /**
     * 取得变化记录的Id号
     */
    public int getRecordID() {
        return recordID;
    }

    /**
     * 取得变化记录数组
     */
    public PIMRecord[] getRecord() {
        return record;
    }

    protected int type;
    protected int appType;
    protected int recordID = -1;
    protected PIMRecord record[];
}
