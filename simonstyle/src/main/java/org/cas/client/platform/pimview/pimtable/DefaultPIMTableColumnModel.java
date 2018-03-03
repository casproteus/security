package org.cas.client.platform.pimview.pimtable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;

/**
 */

public class DefaultPIMTableColumnModel implements IPIMTableColumnModel, PropertyChangeListener, ListSelectionListener,
        Serializable, Releasable {
    /**
     * 根据传入的参数来创建一个实例
     * 
     * @param prmHasEditor
     *            列是否具有编辑器的标志
     */
    public DefaultPIMTableColumnModel(boolean prmHasEditor) {
        super();
        hasEditor = prmHasEditor;
        tableColumns = new Vector();
        // 有编辑器, 就为 firstRowRecord 新建一个Vector
        if (prmHasEditor) {
            firstRowRecord = new Vector();
        }
        // 设置其选择模型
        setSelectionModel(createSelectionModel());
        getSelectionModel().setAnchorSelectionIndex(0);
        setColumnMargin(1);
        // 调用下面一个方法表示现在还没数据,列宽为-1
        invalidateWidthCache();
        setColumnSelectionAllowed(false);
    }

    /**
     * 在表格所有列的后面加入一新列 Appends <code>aColumn</code> to the end of the <code>tableColumns</code> array. This method also
     * posts the <code>columnAdded</code> event to its listeners.
     *
     * @param aColumn
     *            the <code>PIMTableColumn</code> to be added
     * @see #removeColumn
     */
    public void addColumn(
            PIMTableColumn aColumn) {
        // 保证传入正确
        if (aColumn == null) {
            throw new IllegalArgumentException("Object is null");
        }

        tableColumns.addElement(aColumn);
        if (hasEditor) {
            // 有编辑器就给 firstRowRecord 加一个空串
            firstRowRecord.addElement(CASUtility.EMPTYSTR);
        }
        aColumn.addPropertyChangeListener(this);
        invalidateWidthCache();

        // 激发列增加事件 Post columnAdded event notification
        fireColumnAdded(new PIMTableColumnModelEvent(this, 0, getColumnCount() - 1));
    }

    /**
     * 移除一列 Deletes the <code>column</code> from the <code>tableColumns</code> array. This method will do nothing if
     * <code>column</code> is not in the table's columns list. <code>tile</code> is called to resize both the header and
     * table views. This method also posts a <code>columnRemoved</code> event to its listeners.
     *
     * @param column
     *            the <code>PIMTableColumn</code> to be removed
     * @see #addColumn
     */
    public void removeColumn(
            PIMTableColumn column) {
        // 查找其所在索引
        int columnIndex = tableColumns.indexOf(column);

        // 如果有这个列
        if (columnIndex != -1) {
            // Adjust for the selection
            if (selectionModel != null) {
                // 如果选择模型不为空将此列移除
                selectionModel.removeIndexInterval(columnIndex, columnIndex);
            }
            // 移除属性变化监听器
            column.removePropertyChangeListener(this);
            // 将此列从列模型中移除
            tableColumns.removeElementAt(columnIndex);
            if (hasEditor) {
                // 有编辑器就将 firstRowRecord 移除一个元素,
                firstRowRecord.removeElementAt(columnIndex);
            }
            // 下面一句意味着要重新计算总列宽
            invalidateWidthCache();

            // Post columnAdded event notification. (PIMTable and PIMTableHeader
            // listens so they can adjust size and redraw)
            // 激发列移除事件,通知对此事件感兴趣的监听器
            fireColumnRemoved(new PIMTableColumnModelEvent(this, columnIndex, 0));
        }
    }

    /**
     * 将某一列移到新位置 Moves the column and heading at <code>columnIndex</code> to <code>newIndex</code>. The old column at
     * <code>columnIndex</code> will now be found at <code>newIndex</code>. The column that used to be at
     * <code>newIndex</code> is shifted left or right to make room. This will not move any columns if
     * <code>columnIndex</code> equals <code>newIndex</code>. This method also posts a <code>columnMoved</code> event to
     * its listeners.
     *
     * @param columnIndex
     *            the index of column to be moved
     * @param newIndex
     *            new index to move the column
     */
    public void moveColumn(
            int columnIndex,
            int newIndex) {
        // 保证传入的参数正确
        if ((columnIndex < 0) || (columnIndex >= getColumnCount()) || (newIndex < 0) || (newIndex >= getColumnCount())) {
            throw new IllegalArgumentException("moveColumn() - Index out of range");
        }

        PIMTableColumn aColumn;
        Object oldValue = CASUtility.EMPTYSTR;
        if (columnIndex == newIndex) {
            // 如新旧两列相等,就激发列移动事件.
            fireColumnMoved(new PIMTableColumnModelEvent(this, columnIndex, newIndex));
            return;
        }
        // 将这一列先保存,再从模型中删除.
        aColumn = (PIMTableColumn) tableColumns.elementAt(columnIndex);
        tableColumns.removeElementAt(columnIndex);
        // 将第一行记录保存,再从模型中删除.
        if (hasEditor) {
            oldValue = firstRowRecord.elementAt(columnIndex);
            firstRowRecord.removeElementAt(columnIndex);
        }
        // 将列选择模型中的这一列先保存,再从列选择模型中删除.
        boolean selected = selectionModel.isSelectedIndex(columnIndex);
        selectionModel.removeIndexInterval(columnIndex, columnIndex);

        // 将这一列插入到指定位置
        tableColumns.insertElementAt(aColumn, newIndex);
        // 将第一条记录插入到指定位置
        if (hasEditor) {
            firstRowRecord.insertElementAt(oldValue, newIndex);
        }
        // 将列选择模型中的这一列插入到指定位置
        selectionModel.insertIndexInterval(newIndex, 1, true);
        if (selected) {
            // 如选择了这一列,就在选择中加入这一列
            selectionModel.addSelectionInterval(newIndex, newIndex);
        } else {
            // 否则清除这一列的选择标志
            selectionModel.removeSelectionInterval(newIndex, newIndex);
        }
        // 激发列移动事件,通知对此事件感兴趣的监听器
        fireColumnMoved(new PIMTableColumnModelEvent(this, columnIndex, newIndex));
    }

    /**
     * 设置列边缘空白 Sets the column margin to <code>newMargin</code>. This method also posts a
     * <code>columnMarginChanged</code> event to its listeners.
     *
     * @param newMargin
     *            the new margin width, in pixels
     * @see #getColumnMargin
     * @see #getTotalColumnWidth
     */
    public void setColumnMargin(
            int newMargin) {
        // 如果两者不等才进行
        if (newMargin != columnMargin) {
            columnMargin = newMargin;
            // Post columnMarginChanged event notification.
            // 激发列边缘空白变化事件 ,通知对此事件感兴趣的监听器
            fireColumnMarginChanged();
        }
    }

    /**
     * 得到总列数 Returns the number of columns in the <code>tableColumns</code> array.
     *
     * @return the number of columns in the <code>tableColumns</code> array
     * @see #getColumns
     */
    public int getColumnCount() {
        return tableColumns.size();
    }

    /**
     * 返回所有列的列举器 Returns an <code>Enumeration</code> of all the columns in the model.
     * 
     * @return an <code>Enumeration</code> of the columns in the model
     */
    public Enumeration getColumns() {
        return tableColumns.elements();
    }

    /**
     * 根据每一个 PIMTableColumn 的 identifier 返回其所在列模型中的索引 Returns the index of the first column in the
     * <code>tableColumns</code> array whose identifier is equal to <code>identifier</code>, when compared using
     * <code>equals</code>.
     *
     * @return the index of the first column in the <code>tableColumns</code> array whose identifier is equal to
     *         <code>identifier</code>
     * @see #getColumn
     * @param identifier
     *            the identifier object
     */
    public int getColumnIndex(
            Object identifier) {
        // 保证传入的参数正确
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier is null");
        }

        // 得到所有列的列举器
        Enumeration enumeration = getColumns();
        PIMTableColumn aColumn;
        // 设置累加器(索引)的初始值
        int index = 0;
        // 进行列举
        while (enumeration.hasMoreElements()) {
            aColumn = (PIMTableColumn) enumeration.nextElement();
            // Compare them this way in case the column's identifier is null.
            // 找到就返回其索引
            if (identifier.equals(aColumn.getIdentifier())) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("Identifier not found");
    }

    /**
     * 返回给定索引值的 PIMTableColumn Returns the <code>PIMTableColumn</code> object for the column at <code>columnIndex</code>
     * .
     *
     * @param columnIndex
     *            the index of the column desired
     * @return the <code>PIMTableColumn</code> object for the column at <code>columnIndex</code>
     */
    public PIMTableColumn getColumn(
            int columnIndex) {
        return (PIMTableColumn) tableColumns.elementAt(columnIndex);
    }

    /**
     * 返回列边缘值 Returns the width margin for <code>PIMTableColumn</code>. The default <code>columnMargin</code> is 1.
     *
     * @return the maximum width for the <code>PIMTableColumn</code>
     * @see #setColumnMargin
     */
    public int getColumnMargin() {
        return columnMargin;
    }

    /**
     * 根据传入的x坐标得到所在列的索引 Returns the index of the column that lies at position <code>x</code>, or -1 if no column covers
     * this point.
     *
     * In keeping with Swing's separable model architecture, a IPIMTableColumnModel does not know how the table columns
     * actually appear on screen. The visual presentation of the columns is the responsibility of the view/controller
     * object using this model (typically PIMTable). The view/controller need not display the columns sequentially from
     * left to right. For example, columns could be displayed from right to left to accomodate a locale preference or
     * some columns might be hidden at the request of the user. Because the model does not know how the columns are laid
     * out on screen, the given <code>xPosition</code> should not be considered to be a coordinate in 2D graphics space.
     * Instead, it should be considered to be a width from the start of the first column in the model. If the column
     * index for a given X coordinate in 2D space is required, <code>PIMTable.columnAtPoint</code> can be used instead.
     *
     * @param x
     *            the horizontal location of interest
     * @return the index of the column or -1 if no column is found
     * @see PIMTable#columnAtPoint
     */
    public int getColumnIndexAtX(
            int x) {
        // 保证传入的参数正确
        if (x < 0) {
            return -1;
        }
        // 得到总列数
        int cc = getColumnCount();
        for (int column = 0; column < cc; column++) {
            // 每次减去当前列宽
            x = x - getColumn(column).getWidth();
            // 小于零表示就在这一列
            if (x < 0) {
                return column;
            }
        }
        return -1;
    }

    /**
     * 得到总列宽 Returns the total combined width of all columns.
     * 
     * @return the <code>totalColumnWidth</code> property
     */
    public int getTotalColumnWidth() {
        // totalColumnWidth 为-1 表示需要重新计算总宽度
        if (totalColumnWidth == -1) {
            recalcWidthCache();
        }
        return totalColumnWidth;
    }

    // Selection Model
    /**
     * 设置列的选择模型并注册监听器 Sets the selection model for this <code>IPIMTableColumnModel</code> to <code>newModel</code> and
     * registers for listener notifications from the new selection model. If <code>newModel</code> is <code>null</code>,
     * an exception is thrown.
     *
     * @see #getSelectionModel
     * @param newModel
     *            the new selection model
     */
    public void setSelectionModel(
            ListSelectionModel newModel) {
        // 保证传入的参数正确
        if (newModel == null) {
            throw new IllegalArgumentException("Cannot set a null SelectionModel");
        }

        ListSelectionModel oldModel = selectionModel;

        // 判断新旧两值是否相等
        if (newModel != oldModel) {
            if (oldModel != null) {
                // 原有模型不为空就必须移除监听器
                oldModel.removeListSelectionListener(this);
            }

            selectionModel = newModel;

            if (newModel != null) {
                // 新模型不为空就必须加上监听器
                newModel.addListSelectionListener(this);
            }
        }
    }

    /**
     * 返回列选择模型 Returns the <code>ListSelectionModel</code> that is used to maintain column selection state.
     *
     * @return the object that provides column selection state. Or <code>null</code> if row selection is not allowed.
     * @see #setSelectionModel
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    // implements IPIMTableColumnModel

    /**
     * 实现 IPIMTableColumnModel 接口中的方法.设置列是否可被选取,缺省为不可以 Sets whether column selection is allowed. The default is false.
     * 
     * @param flag
     *            是否允许列选中
     */
    public void setColumnSelectionAllowed(
            boolean flag) {
        columnSelectionAllowed = flag;
    }

    /**
     * 实现 IPIMTableColumnModel 接口中的方法.返回列是否可被选取的标志 Returns true if column selection is allowed, otherwise false. The
     * default is false.
     * 
     * @return the <code>columnSelectionAllowed</code> property
     */
    public boolean getColumnSelectionAllowed() {
        return columnSelectionAllowed;
    }

    /**
     * 返回被选择的列的索引数组 Returns an array of selected columns. If <code>selectionModel</code> is <code>null</code>, returns
     * an empty array.
     * 
     * @return an array of selected columns or an empty array if nothing is selected or the <code>selectionModel</code>
     *         is <code>null</code>
     */
    public int[] getSelectedColumns() {
        // 首先选择模型不能为空 否则返回空
        if (selectionModel != null) {
            // 得到选择的最大索引和最小索引
            int iMin = selectionModel.getMinSelectionIndex();
            int iMax = selectionModel.getMaxSelectionIndex();

            // 这个情况表示没有选择
            if ((iMin == -1) || (iMax == -1)) {
                return new int[0];
            }

            // 先造一个temp 和累加器
            int[] rvTmp = new int[1 + (iMax - iMin)];
            int n = 0;
            for (int i = iMin; i <= iMax; i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    // 如发现就检出并累加
                    rvTmp[n++] = i;
                }
            }
            // 根据实际大小造一个数组,
            int[] rv = new int[n];
            // 把 temp 中的值复制过去
            System.arraycopy(rvTmp, 0, rv, 0, n);
            return rv;
        }
        return new int[0];
    }

    /**
     * 得到被选择的列的总数. Returns the number of columns selected.
     * 
     * @return the number of columns selected
     */
    public int getSelectedColumnCount() {
        // 首先选择模型不能为空 否则返回0
        if (selectionModel != null) {
            // 得到选择的最大索引和最小索引
            int iMin = selectionModel.getMinSelectionIndex();
            int iMax = selectionModel.getMaxSelectionIndex();
            // 初始化累加器
            int count = 0;

            for (int i = iMin; i <= iMax; i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    // 如发现就累加计数
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    // Listener Support Methods
    // implements IPIMTableColumnModelListener
    /**
     * 加一个列模型事件监听器 Adds a listener for table column model events.
     * 
     * @param x
     *            a <code>IPIMTableColumnModelListener</code> object
     */
    public void addColumnModelListener(
            IPIMTableColumnModelListener x) {
        listenerList.add(IPIMTableColumnModelListener.class, x);
    }

    /**
     * 移除一个列模型事件监听器 Removes a listener for table column model events.
     * 
     * @param x
     *            a <code>IPIMTableColumnModelListener</code> object
     */
    public void removeColumnModelListener(
            IPIMTableColumnModelListener x) {
        listenerList.remove(IPIMTableColumnModelListener.class, x);
    }

    /**
     * 返回已注册的列模型事件监听器的数组 Returns an array of all the column model listeners registered on this model.
     *
     * @return all of this default table column model's <code>ColumnModelListener</code>s or an empty array if no column
     *         model listeners are currently registered
     *
     * @see #addColumnModelListener
     * @see #removeColumnModelListener
     *
     * @since 1.4
     */
    public IPIMTableColumnModelListener[] getColumnModelListeners() {
        return (IPIMTableColumnModelListener[]) listenerList.getListeners(IPIMTableColumnModelListener.class);
    }

    // 以下是事件激发器的方法 Event firing methods
    /**
     * 通知所有注册的监听器执行列增加后要执行的相应的方法 Notifies all listeners that have registered interest for notification on this event
     * type. The event instance is lazily created using the parameters passed into the fire method.
     * 
     * @param e
     *            the event received
     * @see EventListenerList
     */
    protected void fireColumnAdded(
            PIMTableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IPIMTableColumnModelListener.class) {
                // 执行实现了 IPIMTableColumnModelListener 接口的监听器的方法
                ((IPIMTableColumnModelListener) listeners[i + 1]).columnAdded(e);
            }
        }
    }

    /**
     * 通知所有注册的监听器执行列删除后要执行的相应的方法 Notifies all listeners that have registered interest for notification on this event
     * type. The event instance is lazily created using the parameters passed into the fire method.
     * 
     * @param e
     *            the event received
     * @see EventListenerList
     */
    protected void fireColumnRemoved(
            PIMTableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IPIMTableColumnModelListener.class) {
                // 执行实现了 IPIMTableColumnModelListener 接口的监听器的方法
                ((IPIMTableColumnModelListener) listeners[i + 1]).columnRemoved(e);
            }
        }
    }

    /**
     * 通知所有注册的监听器执行列移动后要执行的相应的方法 Notifies all listeners that have registered interest for notification on this event
     * type. The event instance is lazily created using the parameters passed into the fire method.
     * 
     * @param e
     *            the event received
     * @see EventListenerList
     */
    protected void fireColumnMoved(
            PIMTableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IPIMTableColumnModelListener.class) {
                // 执行实现了 IPIMTableColumnModelListener 接口的监听器的方法
                ((IPIMTableColumnModelListener) listeners[i + 1]).columnMoved(e);
            }
        }
    }

    /**
     * 通知所有注册的监听器执行列选择变化后要执行的相应的方法 Notifies all listeners that have registered interest for notification on this event
     * type. The event instance is lazily created using the parameters passed into the fire method.
     * 
     * @param e
     *            the event received
     * @see EventListenerList
     */
    protected void fireColumnSelectionChanged(
            ListSelectionEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IPIMTableColumnModelListener.class) {
                // 执行实现了 IPIMTableColumnModelListener 接口的监听器的方法
                ((IPIMTableColumnModelListener) listeners[i + 1]).columnSelectionChanged(e);
            }
        }
    }

    /**
     * 通知所有注册的监听器执行列边缘空白变化了要执行的相应的方法 Notifies all listeners that have registered interest for notification on this event
     * type. The event instance is lazily created using the parameters passed into the fire method.
     * 
     * @see EventListenerList
     */
    protected void fireColumnMarginChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IPIMTableColumnModelListener.class) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                // 执行实现了 IPIMTableColumnModelListener 接口的监听器的方法
                ((IPIMTableColumnModelListener) listeners[i + 1]).columnMarginChanged(changeEvent);
            }
        }
    }

    /**
     * 得到指定类型的监听器
     * 
     * @return 该类监听器的数组
     * @param listenerType
     *            监听器类型
     */
    public EventListener[] getListeners(
            Class listenerType) {
        return listenerList.getListeners(listenerType);
    }

    // Implementing the PropertyChangeListener interface
    /**
     * 实现PropertyChangeListener接口中的方法 属性变化监听器变化方法.用于跟踪列宽度和首选宽度变化. Property Change Listener change method. Used to track
     * changes to the column width or preferred column width.
     *
     * @param evt
     *            <code>PropertyChangeEvent</code>
     */
    public void propertyChange(
            PropertyChangeEvent evt) {
        String name = evt.getPropertyName();

        if (name == "width" || name == "preferredWidth") {
            // 将总宽度置为无效
            invalidateWidthCache();
            // 激发列边缘变化事件
            fireColumnMarginChanged();
        }

    }

    // Implementing ListSelectionListener interface
    /**
     * 实现 ListSelectionListener 接口中的方法 用于处理列选择变化事件 A <code>ListSelectionListener</code> that forwards
     * <code>ListSelectionEvents</code> when there is a column selection change.
     *
     * @param e
     *            the change event
     */
    public void valueChanged(
            ListSelectionEvent e) {
        fireColumnSelectionChanged(e);
    }

    /**
     * Creates a new default list selection model.
     * 
     * @return 列表选择模型
     */
    protected ListSelectionModel createSelectionModel() {
        // 返回一个 DefaultListSelectionModel 类缺省的实例
        return new DefaultListSelectionModel();
    }

    /**
     * 重新计算所有列加在一起的总宽度,更新 <code>totalColumnWidth</code> 属性 Recalculates the total combined width of all columns. Updates
     * the <code>totalColumnWidth</code> property.
     */
    protected void recalcWidthCache() {
        // 用一个列举器得到所有列
        Enumeration enumeration = getColumns();
        totalColumnWidth = 0;
        while (enumeration.hasMoreElements()) {
            totalColumnWidth += ((PIMTableColumn) enumeration.nextElement()).getWidth();
        }
    }

    /**
     * 将总宽度置为-1(无效)
     */
    private void invalidateWidthCache() {
        totalColumnWidth = -1;
    }

    /**
     * 判断有无编辑器
     * 
     * @return 有无编辑器
     */
    public boolean hasEditor() {
        return hasEditor;
    }

    /**
     * 返回行数
     * 
     * @return 行数
     */
    public int getRowCount() {
        if (hasEditor) {
            // 有编辑器返回1
            return 1;
        } else {
            // 没有编辑器返回0
            return 0;
        }
    }

    /**
     * 返回第一行中的元素集
     * 
     * @return 第一行中的元素集
     */
    public Enumeration getColumnValues() {
        // 有编辑器则返回其列举器
        if (hasEditor) {
            return firstRowRecord.elements();
        }
        // 没有编辑器则返回null
        return null;
    }

    /**
     * 返回一行新记录
     * 
     * @return 快速编辑栏中的记录
     */
    public Vector getNewRecord() {
        // 有编辑器则返回第一行
        if (hasEditor) {
            return firstRowRecord;
        }
        // 没有编辑器则返回null
        return null;
    }

    /**
     * getValueAt (int rowIndex, int columnIndex)
     * 
     * @return 本列的值
     * @param columnIndex
     *            列索引
     */
    public Object getValueAt(
            int columnIndex) {
        if (hasEditor) {
            return firstRowRecord.elementAt(columnIndex);
        }
        // 没有编辑器则返回null
        return null;
    }

    /**
     * 设置第一行中的值
     * 
     * @param aValue
     *            要设置的值
     * @param columnIndex
     *            所在列 setValueAt (Object aValue, int rowIndex, int columnIndex)
     */
    public void setValueAt(
            Object aValue,
            int columnIndex) {
        // 有编辑器才设值
        if (hasEditor) {
            firstRowRecord.setElementAt(aValue, columnIndex);
        }
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        if (firstRowRecord != null) {
            firstRowRecord.clear();
            firstRowRecord = null;
        }
        if (tableColumns != null) {
            tableColumns.clear();
            tableColumns = null;
        }
        if (selectionModel != null) {
            selectionModel = null;
        }
        if (listenerList != null) {
            listenerList = null;
        }
        if (changeEvent != null) {
            changeEvent = null;
        }
    }

    /** 这个变量特殊点,用来保存第一行记录 */
    protected Vector firstRowRecord;
    /** 这个 Vector 中的每个元素是一个 PIMTableColumn */
    protected Vector tableColumns;
    /** 保存列选择模型 */
    protected ListSelectionModel selectionModel;
    /** 保存边缘空白 */
    protected int columnMargin;
    /** 保存所有的事件监听器 */
    protected EventListenerList listenerList = new EventListenerList();
    /** 保存变化事件值 */
    transient protected ChangeEvent changeEvent;
    /** 保存列是否允许选择的标志 */
    protected boolean columnSelectionAllowed;
    /** 保存总列宽 */
    protected int totalColumnWidth;
    /** 保存'具有编辑器'标识符 */
    protected boolean hasEditor;

}
