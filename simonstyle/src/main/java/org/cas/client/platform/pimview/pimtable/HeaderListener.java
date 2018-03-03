package org.cas.client.platform.pimview.pimtable;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.IView;

/**
 * 表头监听器,继承 MouseAdapter TODO:需要去掉或注掉一个构造器，如果去掉第一个，header.getTable()需要调整。
 */

class HeaderListener extends MouseAdapter {
    /** 保存表头引用 */
    private PIMTableHeader header;
    /** 保存可排序列头绘制器 */
    private SortButtonRenderer renderer;
    /** 保存表体引用 */
    private PIMTable table;

    /**
     * Creates a new instance of HeaderListener 本构造器中传入了表头和绘制器。
     * 
     * @param header
     *            表头
     * @param renderer
     *            绘制器
     */
    HeaderListener(PIMTableHeader header, SortButtonRenderer renderer) {
        this.header = header;
        this.renderer = renderer;
    }

    /**
     * Creates a new instance of HeaderListener 本构造器中传入了表头和绘制器,外还传入table。
     * 
     * @called by: PIMTableHeader.
     * @param header
     *            表格头
     * @param renderer
     *            绘制器
     * @param table
     *            表格
     */
    HeaderListener(PIMTableHeader header, SortButtonRenderer renderer, PIMTable table) {
        this.header = header;
        this.renderer = renderer;
        this.table = table;
    }

    /**
     * 得到绘制器
     * 
     * @return 本绘制器
     */
    SortButtonRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * 鼠标按下----------------------------------------------------------------- 设置排序列号和其升降序标志，并调model的排序方法。
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
        // EMenuSelectionManager.clearPath();
        Point tmpPoint = e.getPoint(); // 得出鼠标所在点的坐标。
        // 如果点下的位置在表头的编辑栏里，在此不处理。
        if (header.getTable().hasEditor() && tmpPoint.y > header.getHeight() / 2)
            return;
        // 如果鼠标为改变尺寸形状，则不进行排序
        if (header.getCursor() == BasicPIMTableHeaderUI.resizeCursor)
            return;
        if (SwingUtilities.isRightMouseButton(e))
            return;

        int tmpCol = header.columnAtPoint(tmpPoint); // 得到鼠标所在的列的列号。
        int tmpSortCol = header.getTable().convertColumnIndexToModel(tmpCol); // 表头不一定一一对应于model中的每一列。比如列在被拖动时。
        if (tmpSortCol < 0)
            return;
        renderer.setPressedColumn(tmpCol); // 在render中设置一个按下标志。
        renderer.setSelectedColumn(tmpCol); // 在render中设置一个列号。
        // header.repaint(); //令header重绘。
        if (header.getTable().isEditing()) // 如果header所在的Tabel正处于编辑状态，则跳出编辑状态。
            header.getTable().getCellEditor().stopCellEditing();

        boolean tmpIsAscent;
        int tmpAscentType; // 用于储存入数据库的升降序标志

        if (SortButtonRenderer.DOWN == renderer.getState(tmpCol)) { // 如果前列为降序排列，则改为升序排列,反之亦反。
            tmpIsAscent = false;
            tmpAscentType = 1;
        } else {
            tmpIsAscent = true;
            tmpAscentType = 0;
        }

        // 下面要把排序信息记录入数据库
        IView tmpView = header.getTable().getView(); // 得到 PIMViewInfo 的实例,以便于将排序信息保存到数据库
        // @NOTE:因为Model中会发出事件来更新视图,所以主视图中表格本身不再排序
        // 如果视图即当前viewInfo都存在,则设置table中的selectedRecordID信息(作甚亚用?),并保存viewInfo.
        if (tmpView != null && tmpView.getApplication().getActiveViewInfo() != null) {
            // 排序不应该影响到选中的记录的ID,为何要重新设置? int tmpRow = header.getTable().getSelectedRow();
            // if (header.getTable().getSelectedRows().length == 1 && tmpRow >= 0)
            // {
            // int id = Integer.parseInt(header.getTable().getValueAt(tmpRow,0).toString());
            // header.getTable().setSelectedRecordID(id); //??排序会影响到选中的记录的ID?
            // }
            // else
            // {
            // header.getTable().setSelectedRecordID(-1);
            // }
            header.setOperatingColumn(tmpSortCol);
            tmpView.updateTableInfo(IView.SORTOR_CHANGED, new int[] { tmpSortCol, tmpAscentType });
        } else {
            // 这是给对话盒中的表格用的
            // 调tableModle的排序方法。
            ((AbstractPIMTableModel) header.getTable().getModel()).sortByColumn(tmpSortCol, tmpIsAscent);

            // ???这样岂不是要调两遍排序方法？
            if (table != null)
                ((AbstractPIMTableModel) table.getModel()).sortByColumn(tmpSortCol, tmpIsAscent);
        }
    }

    /**
     * 鼠标释放
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        renderer.setPressedColumn(-1); // clear
        header.repaint();
    }
}
