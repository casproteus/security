package org.cas.client.platform.pimview.pimtable;

import java.io.Serializable;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import org.cas.client.platform.casutil.CASUtility;

/**
 * 一个抽象类,初步实现 PIMTableModel 接口中的部分方法,实现了排序,监听器的处理 但数据操纵部分没有实现
 */

abstract class AbstractPIMTableModel implements IPIMTableModel, Serializable {
    /**
     * 本方法根据一列来进行排序 sort value of selected column
     * 
     * @param column
     *            所选择的列索引值.
     * @param isAscent
     *            用于决定是按正序还是逆序进行排序.
     */
    void sortByColumn(
            int column,
            boolean isAscent) {
        if (sorter == null) // 如未构建则以本类作参数构建一个实例
        {
            sorter = new PIMTableSorter(this);
        }
        sorter.sort(column, isAscent); // 排序
        fireTableDataChanged(); // 激发表格数据改变事件
    }

    /**
     * 得到当前行数的索引(用于 PIMTableSorter 排序)
     * 
     * @return 当前行数的索引
     */
    int[] getIndexes() {
        int n = getRowCount();
        if (indexes != null && indexes.length == n) // 如indexes不为空并且数目和
            return indexes; // 行数相同就将它返回

        indexes = new int[n]; // 否则根据行数设置其容量,并按次序赋初始值.
        for (int i = 0; i < n; i++) {
            indexes[i] = i;
        }
        return indexes;
    }

    // Default Implementation of the Interface
    /**
     * 返回给定<code>column</code>（列索引）所在列的名称。 本方法为实现 IPIMTableModel 接口中的方法 返回 spreadsheet 约定中列的缺省名字:A, B, C, ... Z, AA,
     * AB,等,如 <code>column</code>列没有则返回空字符串
     *
     * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc. If
     * <code>column</code> cannot be found, returns an empty string.
     *
     * @param column
     *            the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(
            int column) {
        String result = CASUtility.EMPTYSTR; // 先定义一个空串
        for (; column >= 0; column = column / 26 - 1) // 每次递减26
        {
            result = (char) ((char) (column % 26) + 'A') + result; // 以'A'为基数加上偏移量
        }
        return result;
    }

    /**
     * 根据给定列名查找所在列的索引,本方法并非实现 IPIMTableModel 接口中的方法 且不被 <code>PIMTable</code> 所使用.本方法如经常被调用最好重载 Returns a column given
     * its name. Implementation is naive so this should be overridden if this method is to be called often. This method
     * is not in the <code>IPIMTableModel</code> interface and is not used by the <code>PIMTable</code>.
     *
     * @param columnName
     *            string containing name of column to be located
     * @return the column with <code>columnName</code>, or -1 if not found
     */
    int findColumn(
            String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (columnName.equals(getColumnName(i))) // 判断列名是否相等
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * 实现 IPIMTableModel 接口中方法 本方法不管参数 <code>columnIndex</code> 是多少 均返回 <code>Object.class</code> . Returns
     * <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex
     *            the column being queried
     * @return the Object.class
     */
    public Class getColumnClass(
            int columnIndex) {
        return Object.class;
    }

    /**
     * 实现 IPIMTableModel 接口中方法 缺省情况下,认为所有单元格均不可编辑,返回false. Returns false. This is the default implementation for all
     * cells.
     *
     * @param rowIndex
     *            the row being queried
     * @param columnIndex
     *            the column being queried
     * @return false
     */
    public boolean isCellEditable(
            int rowIndex,
            int columnIndex) {
        return false;
    }

    /**
     * 设置单元格值 本方法为实现 IPIMTableModel 接口中方法 这是个空方法,以便其他程序员使用本类时可以不必要实现这个方法(数据模型不可编辑) This empty implementation is provided
     * so users don't have to implement this method if their data model is not editable.
     *
     * @param aValue
     *            要设置的值
     * @param rowIndex
     *            单元格所在行
     * @param columnIndex
     *            单元格所在列
     */
    public void setValueAt(
            Object aValue,
            int rowIndex,
            int columnIndex) {
    }

    // Managing Listeners
    /**
     * 将一个监听器添加到监听器队列中去. 这个队列在每次数据模型发生变化时就会得到通知. 本方法为实现 IPIMTableModel 接口中的方法 Adds a listener to the list that's
     * notified each time a change to the data model occurs.
     *
     * @param l
     *            the IPIMTableModelListener
     */
    public void addTableModelListener(
            IPIMTableModelListener l) {
        listenerList.add(IPIMTableModelListener.class, l);
    }

    /**
     * 将一个监听器从监听器队列中删除, 这个队列在每次数据模型发生变化时就会得到通知. 本方法为实现 IPIMTableModel 接口中的方法 Removes a listener from the list that's
     * notified each time a change to the data model occurs.
     *
     * @param l
     *            the IPIMTableModelListener
     */
    public void removeTableModelListener(
            IPIMTableModelListener l) {
        listenerList.remove(IPIMTableModelListener.class, l);
    }

    /**
     * 返回一个对象数组,这个数组包含了在本模型已登记的所有表格监听器 Returns an array of all the table model listeners registered on this model.
     *
     * @return all of this model's <code>IPIMTableModelListener</code>s or an empty array if no table model listeners
     *         are currently registered
     *
     * @see #addTableModelListener
     * @see #removeTableModelListener
     *
     * @since 1.4
     */
    IPIMTableModelListener[] getTableModelListeners() {
        return (IPIMTableModelListener[]) listenerList.getListeners(IPIMTableModelListener.class); // 返回
                                                                                                   // IPIMTableModelListener
                                                                                                   // 类型的对象数组
    }

    // Fire methods
    /**
     * 通知所有的监听器:表格行中的所有单元格内容可能变化了,包括行数也可能变化了, 这个 <code>PIMTable</code> 必须根据新数据模型重新绘制. 表格结构(列)不发生变化. Notifies all
     * listeners that all cell values in the table's rows may have changed. The number of rows may also have changed and
     * the <code>PIMTable</code> should redraw the table from scratch. The structure of the table (as in the order of
     * the columns) is assumed to be the same.
     *
     * @see PIMTableModelEvent
     * @see EventListenerList
     * @see PIMTable#tableChanged(PIMTableModelEvent)
     */
    void fireTableDataChanged() {// 激发表格变化事件
        fireTableChanged(new PIMTableModelEvent(this)); // 激发表格变化事件
    }

    /**
     * 通知所有的监听器:表格的结构变化了,表格的列数、列名和新列的类型可能 与原有状态不同.如果这个 <code>PIMTable</code> 表格实例收到这个事件 它就必须 <code>'自动根据新模型创建列'</code>
     * 这个标志, Notifies all listeners that the table's structure has changed. The number of columns in the table, and the
     * names and types of the new columns may be different from the previous state. If the <code>PIMTable</code>
     * receives this event and its <code>autoCreateColumnsFromModel</code> flag is set it discards any table columns
     * that it had and reallocates default columns in the order they appear in the model. This is the same as calling
     * <code>setModel(PIMTableModel)</code> on the <code>PIMTable</code>.
     *
     * @see PIMTableModelEvent
     * @see EventListenerList
     */
    void fireTableStructureChanged() {
        fireTableChanged(new PIMTableModelEvent(this, PIMTableModelEvent.HEADER_ROW));
    }

    /**
     * 通知所有的监听器:表格数据模型中有新行插入 Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>, inclusive,
     * have been inserted.
     *
     * @param firstRow
     *            the first row
     * @param lastRow
     *            the last row
     *
     * @see PIMTableModelEvent
     * @see EventListenerList
     *
     */
    void fireTableRowsInserted(
            int firstRow,
            int lastRow) {
        // 激发表格变化事件
        fireTableChanged(new PIMTableModelEvent(this, firstRow, lastRow, PIMTableModelEvent.ALL_COLUMNS,
                PIMTableModelEvent.INSERT));
    }

    /**
     * 通知所有的监听器:表格数据模型中某行数据更新了 Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>,
     * inclusive, have been updated.
     *
     * @param firstRow
     *            the first row
     * @param lastRow
     *            the last row
     *
     * @see PIMTableModelEvent
     * @see EventListenerList
     */
    void fireTableRowsUpdated(
            int firstRow,
            int lastRow) {
        // 激发表格变化事件
        fireTableChanged(new PIMTableModelEvent(this, firstRow, lastRow, PIMTableModelEvent.ALL_COLUMNS,
                PIMTableModelEvent.UPDATE));
    }

    /**
     * 通知所有的监听器:表格中某行被删除了 Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>, inclusive,
     * have been deleted.
     *
     * @param firstRow
     *            the first row
     * @param lastRow
     *            the last row
     *
     * @see PIMTableModelEvent
     * @see EventListenerList
     */
    void fireTableRowsDeleted(
            int firstRow,
            int lastRow) {
        // 激发表格变化事件
        fireTableChanged(new PIMTableModelEvent(this, firstRow, lastRow, PIMTableModelEvent.ALL_COLUMNS,
                PIMTableModelEvent.DELETE));
    }

    /**
     * 通知所有的监听器:表格某单元格中的值更新了 Notifies all listeners that the value of the cell at <code>[row, column]</code> has been
     * updated.
     *
     * @param row
     *            row of cell which has been updated
     * @param column
     *            column of cell which has been updated
     * @see PIMTableModelEvent
     * @see EventListenerList
     */
    void fireTableCellUpdated(
            int row,
            int column) {
        // 激发表格变化事件
        fireTableChanged(new PIMTableModelEvent(this, row, row, column));
    }

    /**
     * 激发表格变化事件 Forwards the given notification event to all <code>TableModelListeners</code> that registered themselves
     * as listeners for this table model.
     *
     * @param e
     *            the event to be forwarded
     *
     * @see #addTableModelListener
     * @see PIMTableModelEvent
     * @see EventListenerList
     */
    void fireTableChanged(
            PIMTableModelEvent e) {
        // Guaranteed to return a non-null array
        // 得到所有注册的监听器,
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        // 遍历该数组,全通知一下
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IPIMTableModelListener.class) {
                ((IPIMTableModelListener) listeners[i + 1]).tableChanged(e);
            }
        }
    }

    /**
     * 用于返回所有已注册的监听器 Returns an array of all the objects currently registered as <code><em>Foo</em>Listener</code>s upon
     * this <code>AbstractPIMTableModel</code>. <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     *
     * You can specify the <code>listenerType</code> argument with a class literal, such as
     * <code><em>Foo</em>Listener.class</code>. For example, you can query a model <code>m</code> for its table model
     * listeners with the following code:
     *
     * <pre>
     * PIMTableModelListener[] tmls = (PIMTableModelListener[]) (m.getListeners(PIMTableModelListener.class));
     * </pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @return an array of all objects registered as <code><em>Foo</em>Listener</code>s on this component, or an empty
     *         array if no such listeners have been added
     * @see #getTableModelListeners
     * @since 1.3
     * @param listenerType
     *            the type of listeners requested; this parameter should specify an interface that descends from
     *            <code>java.util.EventListener</code>
     */
    EventListener[] getListeners(
            Class listenerType) {
        return listenerList.getListeners(listenerType);
    }

    protected EventListenerList listenerList = new EventListenerList();// 用来保存所有注册的监听器 List of listeners
    protected PIMTableSorter sorter;// 用来保存排序的实现者 List of listeners
    protected int[] indexes;// 用来保存通过排序所得到的索引 List of listeners
}
