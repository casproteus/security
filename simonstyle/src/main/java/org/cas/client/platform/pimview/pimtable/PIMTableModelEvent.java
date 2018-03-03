package org.cas.client.platform.pimview.pimtable;

/**
 * 本类定义了PIMTable数据模型变化所生成的事件源
 */

class PIMTableModelEvent extends java.util.EventObject {
    /** 表格数据模型变化方式:数据插入 Identifies the addtion of new rows or columns. */
    public static final int INSERT = 1;
    /** 格数据模型变化方式:数据更新 Identifies a change to existing data. */
    public static final int UPDATE = 0;
    /** 格数据模型变化方式:数据删除 Identifies the removal of rows or columns. */
    public static final int DELETE = -1;

    /** 标识头一行 Identifies the header row. */
    public static final int HEADER_ROW = -1;

    /** 本值指定是所有行都发生某一变化 Specifies all columns in a row or rows. */
    public static final int ALL_COLUMNS = -1;

    //
    // Instance Variables
    // 以下几个变量用于保存实例中的变量
    //
    /** 事件类型. */
    protected int type;
    /** 保存变化的第一行. */
    protected int firstRow;
    /** 保存变化的最后一行. */
    protected int lastRow;
    /** 保存发生变化的列. */
    protected int column;

    //
    // Constructors
    //

    /**
     * 构建器,创建一个 IPIMTableModelEvent 的实例, 默认所有行,所有列均发生变化,变化方式为更新 All row data in the table has changed, listeners should
     * discard any state that was based on the rows and requery the <code>IPIMTableModel</code> to get the new row count
     * and all the appropriate values. The <code>PIMTable</code> will repaint the entire visible region on receiving
     * this event, querying the model for the cell values that are visible. The structure of the table ie, the column
     * names, types and order have not changed.
     * 
     * @param source
     *            事件源
     */
    PIMTableModelEvent(IPIMTableModel source) {
        // Use Integer.MAX_VALUE instead of getRowCount() in case rows were deleted.
        this(source, 0, Integer.MAX_VALUE, ALL_COLUMNS, UPDATE);
    }

    /**
     * 构建器,创建一个 IPIMTableModelEvent 的实例, 默认所有列均发生变化,变化方式为更新
     * 
     * @param source
     *            事件源
     * @param row
     *            事件发生所在行 This row of data has been updated. To denote the arrival of a completely new table with a
     *            different structure use <code>HEADER_ROW</code> as the value for the <code>row</code>. When the
     *            <code>PIMTable</code> receives this event and its <code>autoCreateColumnsFromModel</code> flag is set
     *            it discards any TableColumns that it had and reallocates default ones in the order they appear in the
     *            model. This is the same as calling <code>setModel(IPIMTableModel)</code> on the <code>PIMTable</code>.
     */
    PIMTableModelEvent(IPIMTableModel source, int row) {
        this(source, row, row, ALL_COLUMNS, UPDATE);
    }

    /**
     * 构建器,创建一个 PIMTableModelEvent 的实例, 默认所有列均发生变化,变化方式为更新
     * 
     * @param source
     *            事件源
     * @param firstRow
     *            事件发生所在的第一行
     * @param lastRow
     *            事件发生所在的最后一行 The data in rows [<I>firstRow</I>, <I>lastRow</I>] have been updated.
     */
    PIMTableModelEvent(IPIMTableModel source, int firstRow, int lastRow) {
        this(source, firstRow, lastRow, ALL_COLUMNS, UPDATE);
    }

    /**
     * 构建器,创建一个 PIMTableModelEvent 的实例,默认变化方式为更新
     * 
     * @param source
     *            事件源
     * @param firstRow
     *            事件发生所在的第一行
     * @param lastRow
     *            事件发生所在的最后一行
     * @param column
     *            事件发生所在的列 The cells in column <I>column</I> in the range [<I>firstRow</I>, <I>lastRow</I>] have been
     *            updated.
     */
    PIMTableModelEvent(IPIMTableModel source, int firstRow, int lastRow, int column) {
        this(source, firstRow, lastRow, column, UPDATE);
    }

    /**
     * 构建器,创建一个 PIMTableModelEvent 的实例
     * 
     * @param source
     *            事件源
     * @param firstRow
     *            事件发生所在的第一行
     * @param lastRow
     *            事件发生所在的最后一行
     * @param column
     *            事件发生所在的列
     * @param type
     *            模型数据变化方式(插入,删除,更新) The cells from (firstRow, column) to (lastRow, column) have been changed. The
     *            <I>column</I> refers to the column index of the cell in the model's co-ordinate system. When
     *            <I>column</I> is ALL_COLUMNS, all cells in the specified range of rows are considered changed.
     *            <p>
     *            The <I>type</I> should be one of: INSERT, UPDATE and DELETE.
     *
     */
    PIMTableModelEvent(IPIMTableModel source, int firstRow, int lastRow, int column, int type) {
        // 调用父类构建器
        super(source);
        // 赋值给本地变量
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.column = column;
        this.type = type;
    }

    //
    // Querying Methods
    //

    /**
     * 返回发生变化的第一行 Returns the first row that changed. HEADER_ROW means the meta data, ie. names, types and order of the
     * columns.
     * 
     * @return 表格数据变化的第一行
     */
    int getFirstRow() {
        return firstRow;
    }

    /**
     * 返回发生变化的最后一行 Returns the last row that changed.
     * 
     * @return 表格数据变化的最后一行
     */
    int getLastRow() {
        return lastRow;
    }

    /**
     * 返回发生事件的所在列,如果返回值为所有列, 则意味着指定行的所有列都发生了变化 Returns the column for the event. If the return value is ALL_COLUMNS; it
     * means every column in the specified rows changed.
     * 
     * @return 表格数据变化的所在列
     */
    int getColumn() {
        return column;
    }

    /**
     * 返回事件类型(是插入,删除或是更新) Returns the type of event - one of: INSERT, UPDATE and DELETE.
     * 
     * @return 事件类型
     */
    int getType() {
        return type;
    }
}
