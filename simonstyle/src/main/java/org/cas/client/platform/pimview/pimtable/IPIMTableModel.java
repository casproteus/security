package org.cas.client.platform.pimview.pimtable;

/**
 * 数据模型接口,PIMTable 针对本接口进行数据操作与管理
 * 
 * @TODO:增加方法：int getCushionSize（）返回缓冲的尺寸，它表示Model中维护的数据结构的真正容量，不像getRow（）的结果表示的是应该显示
 *                的完整的结果集的总得尺寸（记录条目总数）。这种机制使得功能的扩展成为可能（如减少了数据库操作次数，从而支持批处理的快速操作，网络模式， cancel操作等）
 */
public interface IPIMTableModel {
    /**
     * 返回表格数据的总行数 Returns the number of rows in the model. A <code>PIMTable</code> uses this method to determine how
     * many rows it should display. This method should be quick, as it is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount();

    /**
     * 返回表格数据的总行数 Returns the number of columns in the model. A <code>PIMTable</code> uses this method to determine how
     * many columns it should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount();

    /**
     * 返回表格列头的名字,注意,这只在视图中绘制出来给人看,不是唯一的,非ID Returns the name of the column at <code>columnIndex</code>. This is used to
     * initialize the table's column header name. Note: this name does not need to be unique; two columns in a table can
     * have the same name.
     *
     * @param columnIndex
     *            the index of the column
     * @return the name of the column
     */
    public String getColumnName(
            int columnIndex);

    /**
     * 得到列头对象
     * 
     * @called by PIMTable; Header中，返回参数位置得元素。与getColumnName不同，它返回的是Object而非String。
     * @return 列头对象
     * @param columnIndex
     *            所在列
     */
    public Object getColumnTitle(
            int columnIndex);

    /**
     * 返回某一列中的数据的类型 Returns the most specific superclass for all the cell values in the column. This is used by the
     * <code>PIMTable</code> to set up a default renderer and editor for the column.
     *
     * @param columnIndex
     *            the index of the column
     * @return the common ancestor class of the object values in the model.
     */
    public Class getColumnClass(
            int columnIndex);

    /**
     * 用判断某一单元格是否可被编辑(设置编辑器用), Returns true if the cell at <code>rowIndex</code> and <code>columnIndex</code> is
     * editable. Otherwise, <code>setValueAt</code> on the cell will not change the value of that cell.
     *
     * @param rowIndex
     *            the row whose value to be queried
     * @param columnIndex
     *            the column whose value to be queried
     * @return true if the cell is editable
     * @see #setValueAt
     */
    public boolean isCellEditable(
            int rowIndex,
            int columnIndex);

    /**
     * 返回某一单元格中的值 Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
     *
     * @param rowIndex
     *            the row whose value is to be queried
     * @param columnIndex
     *            the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(
            int rowIndex,
            int columnIndex);

    /**
     * 设置某一单元格中的值 Sets the value in the cell at <code>columnIndex</code> and <code>rowIndex</code> to
     * <code>aValue</code>.
     *
     * @param aValue
     *            the new value
     * @param rowIndex
     *            the row whose value is to be changed
     * @param columnIndex
     *            the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt(
            Object aValue,
            int rowIndex,
            int columnIndex);

    /**
     * 添加表格数据模型的监听器,以便于其他类针对本模型的实例的变化作出反应 Adds a listener to the list that is notified each time a change to the data
     * model occurs.
     *
     * @param l
     *            the IPIMTableModelListener
     */
    public void addTableModelListener(
            IPIMTableModelListener l);

    /**
     * 移除表格数据模型的监听器 Removes a listener from the list that is notified each time a change to the data model occurs.
     *
     * @param l
     *            the IPIMTableModelListener
     */
    public void removeTableModelListener(
            IPIMTableModelListener l);
}
