package org.cas.client.platform.pimview.pimtable;

import java.util.Enumeration;

import javax.swing.ListSelectionModel;

/**
 * 接口,表格的模型
 */

public interface IPIMTableColumnModel {
    //
    // Modifying the model
    //
    /**
     * 在表格数据模型中添加一列 Appends <code>aColumn</code> to the end of the <code>tableColumns</code> array. This method posts a
     * <code>columnAdded</code> event to its listeners.
     *
     * @param aColumn
     *            the <code>PIMTableColumn</code> to be added
     * @see #removeColumn
     */
    public void addColumn(
            PIMTableColumn aColumn);

    /**
     * 在表格数据模型中移除一列 Deletes the <code>PIMTableColumn</code> <code>column</code> from the <code>tableColumns</code>
     * array. This method will do nothing if <code>column</code> is not in the table's column list. This method posts a
     * <code>columnRemoved</code> event to its listeners.
     *
     * @param column
     *            the <code>PIMTableColumn</code> to be removed
     * @see #addColumn
     */
    public void removeColumn(
            PIMTableColumn column);

    /**
     * 把表格数据模型中的某一列移到另一列 Moves the column and its header at <code>columnIndex</code> to <code>newIndex</code>. The old
     * column at <code>columnIndex</code> will now be found at <code>newIndex</code>. The column that used to be at
     * <code>newIndex</code> is shifted left or right to make room. This will not move any columns if
     * <code>columnIndex</code> equals <code>newIndex</code>. This method posts a <code>columnMoved</code> event to its
     * listeners.
     *
     * @param columnIndex
     *            the index of column to be moved
     * @param newIndex
     *            index of the column's new location
     */
    public void moveColumn(
            int columnIndex,
            int newIndex);

    /**
     * 设置列边缘空白,主要是用表格组件在屏幕上的绘制 Sets the <code>PIMTableColumn</code>'s column margin to <code>newMargin</code>. This
     * method posts a <code>columnMarginChanged</code> event to its listeners.
     *
     * @param newMargin
     *            the width, in pixels, of the new column margins
     * @see #getColumnMargin
     */
    public void setColumnMargin(
            int newMargin);

    //
    // Querying the model
    //

    /**
     * 返回总列数 Returns the number of columns in the model.
     * 
     * @return the number of columns in the model
     */
    public int getColumnCount();

    /**
     * 返回数据模型中的列,返回对象为一个列举器 Returns an <code>Enumeration</code> of all the columns in the model.
     * 
     * @return an <code>Enumeration</code> of all the columns in the model
     */
    public Enumeration getColumns();

    /**
     * 通过列标识ID返回所在列的索引 Returns the index of the first column in the table whose identifier is equal to
     * <code>identifier</code>, when compared using <code>equals</code>.
     *
     * @return the index of the first table column whose identifier is equal to <code>identifier</code>
     * @see #getColumn
     * @param columnIdentifier
     *            the identifier object
     */
    public int getColumnIndex(
            Object columnIdentifier);

    /**
     * 根据列索引值返回所在的列 Returns the <code>PIMTableColumn</code> object for the column at <code>columnIndex</code>.
     *
     * @param columnIndex
     *            the index of the desired column
     * @return the <code>PIMTableColumn</code> object for the column at <code>columnIndex</code>
     */
    public PIMTableColumn getColumn(
            int columnIndex);

    /**
     * 返回列边缘空白,主要是用表格组件在屏幕上的绘制 Returns the width between the cells in each column.
     * 
     * @return the margin, in pixels, between the cells
     */
    public int getColumnMargin();

    /**
     * 根据(鼠标点击的)X坐标值返回所在的列 Returns the index of the column that lies on the horizontal point, <code>xPosition</code>; or
     * -1 if it lies outside the any of the column's bounds.
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
     * @return the index of the column; or -1 if no column is found
     * @see PIMTable#columnAtPoint
     * @param xPosition
     *            X坐标
     */
    public int getColumnIndexAtX(
            int xPosition);

    /**
     * 得到总列宽 Returns the total width of all the columns.
     * 
     * @return the total computed width of all columns
     */
    public int getTotalColumnWidth();

    //
    // Selection
    //

    /**
     * 设置表格数据模型中的列是否可被选取 Sets whether the columns in this model may be selected.
     * 
     * @param flag
     *            true if columns may be selected; otherwise false
     * @see #getColumnSelectionAllowed
     */
    public void setColumnSelectionAllowed(
            boolean flag);

    /**
     * 返回表格数据模型中的列是否可被选取的标志 Returns true if columns may be selected.
     * 
     * @return true if columns may be selected
     * @see #setColumnSelectionAllowed
     */
    public boolean getColumnSelectionAllowed();

    /**
     * 得到所选取的列,返回一个数组 Returns an array of indicies of all selected columns.
     * 
     * @return an array of integers containing the indicies of all selected columns; or an empty array if nothing is
     *         selected
     */
    public int[] getSelectedColumns();

    /**
     * 得到所选取的列数 Returns the number of selected columns.
     *
     * @return the number of selected columns; or 0 if no columns are selected
     */
    public int getSelectedColumnCount();

    /**
     * 设置我们所使用的列选择模型 Sets the selection model.
     *
     * @param newModel
     *            a <code>ListSelectionModel</code> object
     * @see #getSelectionModel
     */
    public void setSelectionModel(
            ListSelectionModel newModel);

    /**
     * 返回我们所使用的列选择模型 Returns the current selection model.
     *
     * @return a <code>ListSelectionModel</code> object
     * @see #setSelectionModel
     */
    public ListSelectionModel getSelectionModel();

    // ///////////////////////////////////////////////////
    /**
     * 得到表格数据模型的总行数
     * 
     * @return 总行数
     */
    public int getRowCount();

    /**
     * 返回所在列索引的列的数据 我想应是一个Vector getValueAt (int rowIndex, int columnIndex)
     * 
     * @return 一个对象
     * @param columnIndex
     *            所在列
     */
    public Object getValueAt(
            int columnIndex);

    /**
     * 设置所在列索引的列的数据 我想应是一个Vector setValueAt (Object aValue, int rowIndex, int columnIndex)
     * 
     * @param aValue
     *            设置的值
     * @param columnIndex
     *            所在列
     */
    public void setValueAt(
            Object aValue,
            int columnIndex);

    //
    // Listener
    //

    /**
     * 添加表格列模型的监听器,以便于其他类针对本模型的实例的变化作出反应 Adds a listener for table column model events.
     *
     * @param x
     *            a <code>IPIMTableColumnModelListener</code> object
     */
    public void addColumnModelListener(
            IPIMTableColumnModelListener x);

    /**
     * 移除表格数据模型的监听器 Removes a listener for table column model events.
     *
     * @param x
     *            a <code>IPIMTableColumnModelListener</code> object
     */
    public void removeColumnModelListener(
            IPIMTableColumnModelListener x);
}
