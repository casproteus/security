package org.cas.client.platform.pimview.pimtable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

/**
 * 因为我们的表格中的数据对象是基于列的,这是表格列监听器的接口,表格列模型将 针对此接口编程
 */

interface IPIMTableColumnModelListener extends java.util.EventListener {
    /**
     * 告诉所有监听器表格数据模型中有一列增加了 Tells listeners that a column was added to the model.
     * 
     * @param e
     *            列模型事件
     */
    public void columnAdded(
            PIMTableColumnModelEvent e);

    /**
     * 告诉所有监听器表格数据模型中有一列被删了 Tells listeners that a column was removed from the model.
     * 
     * @param e
     *            列模型事件
     */
    public void columnRemoved(
            PIMTableColumnModelEvent e);

    /**
     * 告诉所有监听器表格数据模型中列发生移动了 Tells listeners that a column was repositioned.
     * 
     * @param e
     *            列模型事件
     */
    public void columnMoved(
            PIMTableColumnModelEvent e);

    /**
     * 告诉所有监听器绘制出的表格数据的左右边缘空白变化了 Tells listeners that a column was moved due to a margin change.
     * 
     * @param e
     *            列模型事件
     */
    public void columnMarginChanged(
            ChangeEvent e);

    /**
     * 告诉所有监听器客户所选中的表格数据列变化了 Tells listeners that the selection model of the IPIMTableColumnModel changed.
     * 
     * @param e
     *            列模型事件
     */
    public void columnSelectionChanged(
            ListSelectionEvent e);
}
