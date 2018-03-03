package org.cas.client.platform.pimview.pimtable;

/**
 * 表格数据模型监听器
 */

interface IPIMTableModelListener extends java.util.EventListener {
    /**
     * 本方法用于实现了本接口并注册了的对象实例在表格数据模型变化时去作什么事 This fine grain notification tells listeners the exact range of cells, rows,
     * or columns that changed.
     * 
     * @param e
     *            表格模型事件
     */
    public void tableChanged(
            PIMTableModelEvent e);
}
