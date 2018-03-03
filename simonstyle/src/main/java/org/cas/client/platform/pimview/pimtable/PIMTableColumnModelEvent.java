package org.cas.client.platform.pimview.pimtable;

/**
 * 本类定义了PIMTable列模型变化所生成的事件源,有相当部分是关于用鼠标拖动列
 */

class PIMTableColumnModelEvent extends java.util.EventObject {
    //
    // Instance Variables
    //

    /** 保存要移动的那一列 The index of the column from where it was moved or removed */
    protected int fromIndex;

    /** 保存将移动到的目的列 The index of the column to where it was moved or added from */
    protected int toIndex;

    //
    // Constructors
    //

    /**
     * 构建器,创建一个 PIMTableColumnModelEvent 的实例, Constructs a PIMTableColumnModelEvent object.
     *
     * @param source
     *            the IPIMTableColumnModel that originated the event (typically <code>this</code>)
     * @param from
     *            an int specifying the first row in a range of affected rows
     * @param to
     *            an int specifying the last row in a range of affected rows
     */
    PIMTableColumnModelEvent(IPIMTableColumnModel source, int from, int to) {
        // 调用父类构建器
        super(source);
        // 赋值给本地变量
        fromIndex = from;
        toIndex = to;
    }

    //
    // Querying Methods
    //

    /**
     * 返回将移动到的目的列 Returns the fromIndex. Valid for removed or moved events
     * 
     * @return 目的列
     */
    int getFromIndex() {
        return fromIndex;
    }

    /**
     * 返回要移动的那一列 Returns the toIndex. Valid for add and moved events
     * 
     * @return 目的列
     */
    int getToIndex() {
        return toIndex;
    }
}
