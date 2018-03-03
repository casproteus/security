package org.cas.client.platform.pimview.pimtable;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.CellRendererPane;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SizeSequence;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.UIResource;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.menuaction.DeleteAction;
import org.cas.client.platform.cascontrol.menuaction.OpenAction;
import org.cas.client.platform.cascontrol.menuaction.PIMRecordSelect;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.View_PIMDetails;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.specialeditors.FinishScaleEditor;
import org.cas.client.platform.pimview.pimtable.specialeditors.SexComboBox;
import org.cas.client.resource.international.PIMTableConstants;

/**
 * <p>
 * @NOTE: 在每次应用转换前,如果当前是Table视图,则必须调到Table的Release方法,
 * <p>
 * 释放editingRowRecords变量.
 */

public class PIMTable extends JComponent implements IPIMTableModelListener, IPIMTableColumnModelListener, Releasable,
        Scrollable, ListSelectionListener, CellEditorListener, Border, ChangeListener, ItemListener, MouseListener,
        DragSourceListener, DragGestureListener {
    private static final String uiClassID = "PIMTableUI";// UI类名标识符

    static // 静态初始化块,载入UI
    {
        UIManager.getDefaults().put(uiClassID, "org.cas.client.platform.pimview.pimtable.BasicPIMTableUI");
        UIManager.getDefaults().put("PIMTableHeaderUI",
                "org.cas.client.platform.pimview.pimtable.BasicPIMTableHeaderUI");
        UIManager.getDefaults().put("PIMTable.selectionBackground", CustOpts.custOps.getSelectedBackColor());
        UIManager.getDefaults().put("PIMTable.selectionForeground", CustOpts.custOps.getSelectedCellColor());
    }

    // 关于表格列尺寸调整方式的个属性--------------------------------------------
    public static final int AUTO_RESIZE_OFF = 0; // 各列宽度不可调整
    public static final int AUTO_RESIZE_NEXT_COLUMN = 1; // 单列宽度变化时,调整下一列的宽度
    public static final int AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2; // 单列宽度变化时,列宽度依次调整
    public static final int AUTO_RESIZE_LAST_COLUMN = 3; // 单列宽度变化时,调整最后一列的宽度
    public static final int AUTO_RESIZE_ALL_COLUMNS = 4; // 单列宽度变化时,列宽度自动调整

    // 构造器==============================================================================================================================
    /**
     * 构建一个PIMTable的实例
     * 
     * @param prmHasEditor
     *            表格有快速编辑栏的判断标识
     */
    public PIMTable() {
        this(null, null, null, false);
    }

    public PIMTable(IPIMTableModel dm, IPIMTableColumnModel cm, ListSelectionModel sm, boolean prmHasEditor) {
        super();
        hasEditor = prmHasEditor;
        initTable(dm, cm, sm);
    } // 构造器==========================================================================================================================

    // 实现<code>PIMTableModelListener</code>接口中的方法==============================================================================
    /**
     * 在数据模型结构变化时要做的事 Invoked when this table's <code>IPIMTableModel</code> generates a <code>PIMTableModelEvent</code>.
     * The <code>PIMTableModelEvent</code> should be constructed in the coordinate system of the model; the appropriate
     * mapping to the view coordinate system is performed by this <code>PIMTable</code> when it receives the event.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by <code>PIMTable</code>.
     * <p>
     * Note that as of 1.3, this method clears the selection, if any.
     * 
     * @param e
     *            表格数据模型结构变化事件源
     */
    public void tableChanged(
            PIMTableModelEvent e) {
        // 事件源为空或第一行在表格头中
        if (e == null || e.getFirstRow() == PIMTableModelEvent.HEADER_ROW) {
            // 清空选中的,置行选择模型为空
            clearSelection();
            rowModel = null;
            // 如允许自动从模型中创建列,就造出列后返回
            if (getAutoCreateColumnsFromModel()) {
                createDefaultColumnsFromModel();
                return;
            }
            resizeAndRepaint();
            return;
        }
        // 行模型为空就只要重绘一下就可以了
        if (rowModel != null) {
            repaint();
        }
        // 如是行插入事件就在表格中插入后返回
        if (e.getType() == PIMTableModelEvent.INSERT) {
            tableRowsInserted(e);
            return;
        }
        // 删除事件的处理
        if (e.getType() == PIMTableModelEvent.DELETE) {
            tableRowsDeleted(e);
            return;
        }
        // 得到出事的那列,起始行和最后一行
        int modelColumn = e.getColumn();
        int start = e.getFirstRow();
        int end = e.getLastRow();

        Rectangle dirtyRegion;
        // 所有列都变化的处理
        if (modelColumn == PIMTableModelEvent.ALL_COLUMNS) {
            dirtyRegion = new Rectangle(0, start * getRowHeight(), getColumnModel().getTotalColumnWidth(), 0);
        }
        // 这处两个分支的意思是取得要重绘的区域
        else {
            int column = convertColumnIndexToView(modelColumn);
            dirtyRegion = getCellRect(start, column, false);
        }
        // 在这个区域重绘一下
        if (end != Integer.MAX_VALUE) {
            dirtyRegion.height = (end - start + 1) * getRowHeight();
            repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
        }
        // 下面应是出错处理
        else {
            clearSelection();
            resizeAndRepaint();
            rowModel = null;
        }
    }// ======================================================================================================================================

    // 实现接口PIMTableColumnModelListener中的方法==========================================================================================
    /**
     * 告诉所有监听器表格数据模型中有一列增加了 Tells listeners that a column was added to the model. 在列模型中加入列时要做的事 Invoked when a column is
     * added to the table column model.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by PIMTable.
     *
     * @param e
     *            列模型事件源
     * @see IPIMTableColumnModelListener
     */
    public void columnAdded(
            PIMTableColumnModelEvent e) {
        // 很简单,在编辑嘛就不编辑了,
        if (isEditing()) {
            removeEditor();
        }
        // 调整尺寸并重绘
        resizeAndRepaint();
    }

    /**
     * 告诉所有监听器表格数据模型中有一列被删了 Tells listeners that a column was removed from the model. 在列模型中移除列时要做的事 Invoked when a
     * column is removed from the table column model.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by PIMTable.
     *
     * @param e
     *            列模型事件源
     * @see IPIMTableColumnModelListener
     */
    public void columnRemoved(
            PIMTableColumnModelEvent e) {
        // 很简单,在编辑嘛就不编辑了,
        if (isEditing()) {
            removeEditor();
        }
        // 调整尺寸并重绘
        resizeAndRepaint();
    }

    /**
     * 告诉所有监听器表格数据模型中列发生移动了 Tells listeners that a column was repositioned. 在列模型中移动列时要做的事 Invoked when a column is
     * repositioned. If a cell is being edited, then editing is stopped and the cell is redrawn.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by PIMTable.
     *
     * @param e
     *            列模型事件源
     * @see IPIMTableColumnModelListener
     */
    public void columnMoved(
            PIMTableColumnModelEvent e) {
        // 很简单,在编辑嘛就不编辑了,
        if (isEditing()) {
            removeEditor();
        }
        // 调整尺寸并重绘
        repaint();
    }

    /**
     * 告诉所有监听器绘制出的表格数据的左右边缘空白变化了 Tells listeners that a column was moved due to a margin change. 在列边缘空白调整时要做的事 Invoked
     * when a column is moved due to a margin change. If a cell is being edited, then editing is stopped and the cell is
     * redrawn.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by PIMTable.
     *
     * @param e
     *            变化事件源
     * @see IPIMTableColumnModelListener
     */
    public void columnMarginChanged(
            ChangeEvent e) {
        // 很简单,在编辑嘛就不编辑了,
        if (isEditing()) {
            removeEditor();
        }
        // 得到了正在调整尺寸的列,
        PIMTableColumn resizingColumn = getResizingColumn();

        // 有这列且允许自动调整,就将它的首选尺寸设为其宽度
        if (resizingColumn != null && autoResizeMode == AUTO_RESIZE_OFF) {
            resizingColumn.setPreferredWidth(resizingColumn.getWidth());
        }
        // 最后调整尺寸并重绘
        resizeAndRepaint();
    }

    /**
     * 告诉所有监听器客户所选中的表格数据列变化了 Tells listeners that the selection model of the IPIMTableColumnModel changed.
     * 在列选择模型中选择的列变化时要做的事 Invoked when the selection model of the <code>IPIMTableColumnModel</code> is changed.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by PIMTable.
     *
     * @param e
     *            the event received
     * @see IPIMTableColumnModelListener
     */
    public void columnSelectionChanged(
            ListSelectionEvent e) {
        // 保证正确
        if (getRowCount() <= 0 || getColumnCount() <= 0) {
            return;
        }
        // 得到第一列和最后一列的列号
        int firstIndex = limit(e.getFirstIndex(), 0, getColumnCount() - 1);
        int lastIndex = limit(e.getLastIndex(), 0, getColumnCount() - 1);
        // 由第一列和最后一列的矩形区域,来得到总要重绘区域,最后重绘
        Rectangle firstColumnRect = getCellRect(0, firstIndex, false);
        Rectangle lastColumnRect = getCellRect(getRowCount() - 1, lastIndex, false);
        Rectangle dirtyRegion = firstColumnRect.union(lastColumnRect);
        repaint(dirtyRegion);
    }

    // ====================================================================================================================================

    // 实现Releasable的接口===================================================================================================================
    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        getUI().uninstallUI(this);
        setAutoscrolls(false);
        if (dataModel != null) {
            dataModel.removeTableModelListener(this);
        }
        if (tableHeader != null) {
            tableHeader.release();
        }
        if (columnModel != null) {
            columnModel.removeColumnModelListener(this);
            if (columnModel instanceof DefaultPIMTableColumnModel) {
                ((DefaultPIMTableColumnModel) columnModel).release();
                columnModel = null;
            }
        }
        if (selectionModel != null) {
            selectionModel.removeListSelectionListener(this);
            selectionModel = null;
        }
        tableHeader = null;
        cellEditor = null;
        if (defaultRenderersByColumnClass != null) {
            defaultRenderersByColumnClass.clear();
            defaultRenderersByColumnClass = null;
        }
        if (defaultEditorsByColumnClass != null) {
            defaultEditorsByColumnClass.clear();
            defaultEditorsByColumnClass = null;
        }
        rowModel = null;
        editorRemover = null;
        if (editingRowRecords != null) {
            editingRowRecords.clear();
            editingRowRecords = null;
        }
        dragSource = null;
        if (recongizer != null) {
            recongizer.removeDragGestureListener(this);
        }
        recongizer = null;
        if (fontAttrubutePool != null) {
            fontAttrubutePool.clear();
            fontAttrubutePool = null;
        }
    }

    // ====================================================================================================================================

    // 实现Scrollable的接口===================================================================================================================
    /**
     * 得到要在视口中显示的面积尺寸 Returns the preferred size of the viewport for a view component. For example the preferredSize of
     * a JList component is the size required to accommodate all of the cells in its list however the value of
     * preferredScrollableViewportSize is the size required for JList.getVisibleRowCount() rows. A component without any
     * properties that would effect the viewport size should just return getPreferredSize() here.
     *
     * @return The preferredSize of a JViewport whose view is this Scrollable.
     * @see JViewport#getPreferredSize
     */
    public Dimension getPreferredScrollableViewportSize() {
        return preferredViewportSize;
    }

    /**
     * 得到滚动的单元增量 Components that display logical rows or columns should compute the scroll increment that will
     * completely expose one new row or column, depending on the value of orientation. Ideally, components should handle
     * a partially exposed row or column by returning the distance required to completely expose the item.
     * <p>
     * Scrolling containers, like JScrollPane, will use this method each time the user requests a unit scroll.
     *
     * @param visibleRect
     *            The view area visible within the viewport
     * @param orientation
     *            Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction
     *            Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction. This value should always be positive.
     * @see JScrollBar#setUnitIncrement
     */
    public int getScrollableUnitIncrement(
            Rectangle visibleRect,
            int orientation,
            int direction) {
        // 默认水平方向上按一下箭头是100个像素
        if (orientation == SwingConstants.HORIZONTAL) {
            return 100;
        }
        // 垂直方向上是一行
        return getRowHeight();
    }

    /**
     * 得到滚动的块增量 Components that display logical rows or columns should compute the scroll increment that will completely
     * expose one block of rows or columns, depending on the value of orientation.
     * <p>
     * Scrolling containers, like JScrollPane, will use this method each time the user requests a block scroll.
     *
     * @param visibleRect
     *            The view area visible within the viewport
     * @param orientation
     *            Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction
     *            Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction. This value should always be positive.
     * @see JScrollBar#setBlockIncrement
     */
    public int getScrollableBlockIncrement(
            Rectangle visibleRect,
            int orientation,
            int direction) {
        // 默认垂直方向上,如行高不固定就是一页,否则就要保证最边缘一行要重复显示出
        if (orientation == SwingConstants.VERTICAL) {
            int rh = getRowHeight();
            return (rh > 0) ? Math.max(rh, (visibleRect.height / rh) * rh) : visibleRect.height;
        }
        // 默认水平方向上是一页
        else {
            return visibleRect.width;
        }
    }

    /**
     * 返回为假时才会显示出水平的滚动条 Return true if a viewport should always force the width of this <code>Scrollable</code> to match
     * the width of the viewport. For example a normal text view that supported line wrapping would return true here,
     * since it would be undesirable for wrapped lines to disappear beyond the right edge of the viewport. Note that
     * returning true for a Scrollable whose ancestor is a JScrollPane effectively disables horizontal scrolling.
     * <p>
     * Scrolling containers, like JViewport, will use this method each time they are validated.
     *
     * @return True if a viewport should force the Scrollables width to match its own.
     */
    public boolean getScrollableTracksViewportWidth() {
        return !(autoResizeMode == AUTO_RESIZE_OFF); // 不可调整尺寸时才会显示出
    }

    /**
     * 返回为假时才会显示出垂直的滚动条 Return true if a viewport should always force the height of this Scrollable to match the height
     * of the viewport. For example a columnar text view that flowed text in left to right columns could effectively
     * disable vertical scrolling by returning true here.
     * <p>
     * Scrolling containers, like JViewport, will use this method each time they are validated.
     *
     * @return True if a viewport should force the Scrollables height to match its own.
     */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    // ========================================================================================================================

    // 实现<code>ListSelectionListener</code>接口中的方法========================================================================
    /**
     * 在列表选择模型变化时要做的事 Invoked when the row selection changes -- repaints to show the new selection.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by PIMTable.
     * ListSelectionListener
     * 
     * @param e
     *            the event received
     * @see ListSelectionListener
     */
    public void valueChanged(
            ListSelectionEvent e) {
        if (view != null) {
            ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();
            int minSelectionIndex = listSelectionModel.getMinSelectionIndex();
            if (minSelectionIndex != -1 && minSelectionIndex < getRowCount() && isRowSelected(minSelectionIndex)) {
                CASControl.ctrl.addSystemStatus(IStatCons.RECORD_SELECTED);
                // 处理邮件
                // 处理过程可以放在各个邮件面板中
            } else {
                CASControl.ctrl.resetSystemStatus(IStatCons.RECORD_SELECTED);
            }
        }
        // 保证正确
        if (getRowCount() <= 0 || getColumnCount() <= 0) {
            CASControl.ctrl.resetSystemStatus(IStatCons.RECORD_SELECTED);
            return;
        } else {
            CASControl.ctrl.addSystemStatus(IStatCons.RECORD_SELECTED);
        }
        // 得到第一列和最后一列的列号
        int firstIndex = limit(e.getFirstIndex(), 0, getRowCount() - 1);
        int lastIndex = limit(e.getLastIndex(), 0, getRowCount() - 1);
        // 由第一列和最后一列的矩形区域,来得到总要重绘区域,最后重绘
        Rectangle firstRowRect = getCellRect(firstIndex, 0, false);
        Rectangle lastRowRect = getCellRect(lastIndex, getColumnCount() - 1, false);
        Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
        repaint(dirtyRegion);
    }

    // ===================================================================================================================================

    // 实现<code>CellEditorListener</code>接口中的方法=============================================================================================
    /**
     * 在单元格停止编辑要做的事,保存记录的新内容，并移除编辑器。 Invoked when editing is finished. The changes are saved and the editor is
     * discarded.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by PIMTable.
     *
     * @param e
     *            the event received
     * @see CellEditorListener
     */
    public void editingStopped(
            ChangeEvent e) {
        if (cellEditor == null) {
            ErrorUtil.write("PIMTable.editingStopped():find the cellEditor had be made null");// 说明存在未知的会导致移除编辑器的动作。");
            return;
        }// 除错：如果编辑器已经为空，说明已经被移除，直接返回。----------------------------------

        Object tmpOldValue = getValueAt(editingRow, editingColumn);
        Object tmpNewValue = cellEditor.getCellEditorValue();

        if (view != null) // 如果视图不为null，则对所编辑行作适当调整后存盘。
        {
            // 准备实例变量editingRowRecords的值-------------------------------------------------------------------------
            if (editingRowRecords == null) // @NOTE: 再每次应用转换前,如果当前是Table视图,则必须调到Table的Release方法,释放editingRowRecords变量.
            { // @NOTE: 在每次调整ViewInfo时,比如减少显示的列数,如果当前是Table视图,也要调到Table的Release方法.
                editingRowRecords = new Vector(); // @NOTE:如果editingRowRecords没有被及时清空,则可能出现变量的前几位表示的是当前正在编辑的Record.
            } // 而后面的几位是上一个Table视图中的编辑行的后几位.
            if (editingRowRecords.size() == 0) {
                for (int i = 0; i < getColumnCount(); i++) {
                    editingRowRecords.add(i, null);
                }
            }
            // @TODO:此处不可以用移除再加入的方法,应该就原来的记录上做改变.
            if (tmpOldValue != tmpNewValue) {
                if (editingRowRecords.size() != 0) {
                    editingRowRecords.removeElementAt(editingColumn);
                    editingRowRecords.add(editingColumn, tmpNewValue);
                }
            }// 变量editingRowRecords的值准备完毕-------------------------------------------------------------------------

            // TODO:看看在这里处理完成率是否合适
            // 处理任务中的完成率单选-----------------------------------------------------------------------
            if (view.getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP) {
                int tmpFinishField = specialColumn(ModelDBCons.COMPLETED);
                if (tmpFinishField > 0 && tmpFinishField == getEditingColumn()) {
                    FinishScaleEditor prmComboBoxEditor =
                            (FinishScaleEditor) ((DefaultPIMCellEditor) cellEditor).getComponent();
                    String str = prmComboBoxEditor.getText();
                    boolean tmpIsFinished = str.equals("100");
                    boolean tmpNoStart = str.equals("0");
                    setIsDrawDeleteLine(tmpIsFinished);
                    // 处理完成率字段
                    int locationCol = specialColumn(ModelDBCons.FINISH_FLAG);
                    if (locationCol > 0) {
                        if (tmpIsFinished) {
                            // 在PIMTableConstants中的值
                            setValueAt(Boolean.TRUE, getEditingRow(), locationCol);
                            putRelationValue(locationCol, Boolean.TRUE);
                        } else {
                            // 在PIMTableConstants中的值
                            setValueAt(Boolean.FALSE, getEditingRow(), locationCol);
                            putRelationValue(locationCol, Boolean.FALSE);
                        }
                    }
                    // 处理状态字段
                    locationCol = specialColumn(ModelDBCons.STATUS);
                    if (locationCol > 0) {
                        if (tmpIsFinished) {
                            // 在PIMTableConstants中的值
                            setValueAt(PIMTableConstants.TASK_STATUS_CONSTANTS[2], getEditingRow(), locationCol);
                            putRelationValue(locationCol, PIMTableConstants.TASK_STATUS_CONSTANTS[2]);
                        } else if (tmpNoStart) {
                            // 在PIMTableConstants中的值
                            setValueAt(PIMTableConstants.TASK_STATUS_CONSTANTS[0], getEditingRow(), locationCol);
                            putRelationValue(locationCol, PIMTableConstants.TASK_STATUS_CONSTANTS[0]);
                        } else {
                            // 在PIMTableConstants中的值
                            setValueAt(PIMTableConstants.TASK_STATUS_CONSTANTS[1], getEditingRow(), locationCol);
                            putRelationValue(locationCol, PIMTableConstants.TASK_STATUS_CONSTANTS[1]);
                        }
                    }
                }
            }// 处理任务的完成率完毕-------------------------------------------------------------------------
        }

        // setValueAt(tmpNewValue, editingRow, editingColumn); //@TODO：这句话可否去掉,视图的更新可以完全根据model的变化来.
        if (view != null) {
            view.viewToModel();
        }

        removeEditor(); // 移除编辑器---------------
    }

    /**
     * 实现<code>CellEditorListener</code>接口中的方法 在单元格放弃编辑要做的事 Invoked when editing is canceled. The editor object is
     * discarded and the cell is rendered once again.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by PIMTable.
     *
     * @param e
     *            the event received
     * @see CellEditorListener
     */
    public void editingCanceled(
            ChangeEvent e) {
        removeEditor();
    }

    // ===================================================================================================================================

    // 实现Border定义的接口===================================================================================================================================
    /**
     * Paints the border for the specified component with the specified position and size.
     * 
     * @param c
     *            the component for which this border is being painted
     * @param g
     *            the paint graphics
     * @param x
     *            the x position of the painted border
     * @param y
     *            the y position of the painted border
     * @param width
     *            the width of the painted border
     * @param height
     *            the height of the painted border
     */
    public void paintBorder(
            Component c,
            Graphics g,
            int x,
            int y,
            int width,
            int height) {
    }

    /**
     * Returns the insets of the border.
     * 
     * @param c
     *            the component for which this border insets value applies
     */
    public Insets getBorderInsets(
            Component component) {
        return new Insets(0, 0, 0, 0);
    }

    /**
     * Returns whether or not the border is opaque. If the border is opaque, it is responsible for filling in it's own
     * background when painting.
     * 
     * @return 返回是否不透明的标志
     */
    public boolean isBorderOpaque() {
        return false;
    }

    // ===================================================================================================================================

    // 实现ChangeListener定义的接口=====================================================================================================================
    /**
     * Invoked when the target of the listener has changed its state. 本方法是给任务表格视图上的完成单选按钮加的,用来在它发生变化时
     * 决定视图上的当前选中行是否要绘制删除线
     * 
     * @param e
     *            a ChangeEvent object
     */
    public void stateChanged(
            ChangeEvent e) {
        if (e.getSource() instanceof PIMCheckBoxEditor) {
            PIMCheckBoxEditor prmCheckBoxEditor = (PIMCheckBoxEditor) getEditorComponent();
            if (prmCheckBoxEditor == null) {
                return;
            }
            setIsDrawDeleteLine(prmCheckBoxEditor.isSelected());
            // 处理状态字段
            int locationCol = specialColumn(ModelDBCons.STATUS);
            if (locationCol > 0) {
                if (prmCheckBoxEditor.isSelected()) {
                    // 在PIMTableConstants中的值
                    setValueAt(PIMTableConstants.TASK_STATUS_CONSTANTS[2], getEditingRow(), locationCol);
                    putRelationValue(locationCol, PIMTableConstants.TASK_STATUS_CONSTANTS[2]);
                } else {
                    // 在PIMTableConstants中的值
                    setValueAt(PIMTableConstants.TASK_STATUS_CONSTANTS[0], getEditingRow(), locationCol);
                    putRelationValue(locationCol, PIMTableConstants.TASK_STATUS_CONSTANTS[0]);
                }
            }
            // 处理完成率字段
            locationCol = specialColumn(ModelDBCons.COMPLETED);
            if (locationCol > 0) {
                if (prmCheckBoxEditor.isSelected()) {
                    setValueAt("100", getEditingRow(), locationCol);
                    putRelationValue(locationCol, "100");
                } else {
                    setValueAt("0", getEditingRow(), locationCol);
                    putRelationValue(locationCol, "0");
                }
            }
        }
    }

    // ===================================================================================================================================

    // 实现ItemListener定义的接口=========================================================================================================
    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected). 本方法是给任务表格视图上的状态康宝加的,用来在它发生变化时
     * 决定视图上的当前选中行是否要绘制删除线
     * 
     * @param e
     *            选中变化事件
     */
    public void itemStateChanged(
            ItemEvent e) {
        if (e.getSource() instanceof SexComboBox) {
            SexComboBox prmCheckBoxEditor = (SexComboBox) getEditorComponent();
            setIsDrawDeleteLine(prmCheckBoxEditor.getSelectedIndex() == 2);
            // 处理完成字段
            int locationCol = specialColumn(ModelDBCons.FINISH_FLAG);
            if (locationCol > 0) {
                if (prmCheckBoxEditor.getSelectedIndex() == 2) {
                    // 在PIMTableConstants中的值
                    setValueAt(Boolean.TRUE, getEditingRow(), locationCol);
                    putRelationValue(locationCol, Boolean.TRUE);
                } else {
                    // 在PIMTableConstants中的值
                    setValueAt(Boolean.FALSE, getEditingRow(), locationCol);
                    putRelationValue(locationCol, Boolean.FALSE);
                }
            }
            // 处理完成率字段
            locationCol = specialColumn(ModelDBCons.COMPLETED);
            if (locationCol > 0) {
                if (prmCheckBoxEditor.getSelectedIndex() == 2) {
                    setValueAt(PIMPool.pool.getKey(100), getEditingRow(), locationCol);
                    putRelationValue(locationCol, PIMPool.pool.getKey(100));
                } else {
                    setValueAt(PIMPool.pool.getKey(0), getEditingRow(), locationCol);
                    putRelationValue(locationCol, PIMPool.pool.getKey(0));
                }
            }
        }
    }

    // ===================================================================================================================================

    // 实现MouseListener定义的接口========================================================================================================
    /**
     * Invoked when the mouse button has been clicked (pressed and released) on the Cell编辑器..
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1) // && rowAtPoint(e.getPoint()) == 1)
        {
            if (getEditingColumn() < 0) // 在表格头双击会激发这个情况,屏蔽之
                return;
            if (view == null)// 如果是像POS这样的应用，那么可能存在View为Null的情况。
                return;
            // 除错完毕-----------------------

            PIMViewInfo tmpViewInfo = view.getApplication().getActiveViewInfo();

            if (tmpViewInfo.getAppIndex() == ModelCons.CONTACT_APP || // 如果是联系人或者任务,则先保存再进行对话盒编辑
                    tmpViewInfo.getAppIndex() == ModelCons.TASK_APP) {
                // int[] seleIDs = new int[]
                // {
                // Integer.parseInt(getValueAt(editingRow,0).toString())//得到选中记录对应的ID值.@NOTE:0位置为专为ID值保留.
                // };

                editingStopped(null);

                if (tmpViewInfo.getAppIndex() == ModelCons.CONTACT_APP
                        || tmpViewInfo.getAppIndex() == ModelCons.TASK_APP) {
                    new OpenAction().actionPerformed(null); // 弹出对话盒
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            processRightMenu(e);
        }
    }

    /**
     * Invoked when the mouse enters the Cell编辑器..
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits the Cell编辑器..
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on the Cell编辑器..
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on the Cell编辑器..
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
    }

    // ===================================================================================================================================

    // 实现dragSourceListener定义的接口===================================================================================================================================
    /**
     * This method is invoked to signify that the Drag and Drop operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to determine the termination state. The getDropAction() method
     * returns the operation that the drop site selected to apply to the Drop operation. Once this method is complete,
     * the current <code>DragSourceContext</code> and associated resources become invalid.
     *
     * @param dsde
     *            the <code>DragSourceDropEvent</code>
     */
    public void dragDropEnd(
            DragSourceDropEvent dsde) {
    }

    /**
     * Called as the cursor's hotspot enters a platform-dependent drop site. This method is invoked when all the
     * following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot enters the operable part of a platform- dependent drop site.
     * <LI>The drop site is active.
     * <LI>The drop site accepts the drag.
     * </UL>
     *
     * @param dsde
     *            the <code>DragSourceDragEvent</code>
     */
    public void dragEnter(
            DragSourceDragEvent dsde) {
    }

    /**
     * Called as the cursor's hotspot exits a platform-dependent drop site. This method is invoked when any of the
     * following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot no longer intersects the operable part of the drop site associated with the previous
     * dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The drop site associated with the previous dragEnter() invocation is no longer active.
     * </UL>
     * OR
     * <UL>
     * <LI>The current drop site has rejected the drag.
     * </UL>
     *
     * @param dse
     *            the <code>DragSourceEvent</code>
     */
    public void dragExit(
            DragSourceEvent dse) {
    }

    /**
     * Called as the cursor's hotspot moves over a platform-dependent drop site. This method is invoked when all the
     * following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot has moved, but still intersects the operable part of the drop site associated with the
     * previous dragEnter() invocation.
     * <LI>The drop site is still active.
     * <LI>The drop site accepts the drag.
     * </UL>
     *
     * @param dsde
     *            the <code>DragSourceDragEvent</code>
     */
    public void dragOver(
            DragSourceDragEvent dsde) {
    }

    /**
     * Called when the user has modified the drop gesture. This method is invoked when the state of the input device(s)
     * that the user is interacting with changes. Such devices are typically the mouse buttons or keyboard modifiers
     * that the user is interacting with.
     *
     * @param dsde
     *            the <code>DragSourceDragEvent</code>
     */
    public void dropActionChanged(
            DragSourceDragEvent dsde) {
    }

    // ===================================================================================================================================

    // 实现DragGestureRecognizer定义的接口================================================================================================
    /**
     * a platform-dependent drag initiating gesture and is notifying this listener in order for it to initiate the
     * action for the user.
     * <P>
     * 
     * @param prmDGE
     *            the <code>DragGestureEvent</code> describing the gesture that has just occurred
     */
    public void dragGestureRecognized(
            DragGestureEvent prmDGE) {
        // MouseEvent mouseEvent = (MouseEvent)prmDGE.getTriggerEvent();
        // Point point = mouseEvent.getPoint();
        // contains(point);
        int seleRow = getSelectedRow();
        if (seleRow < 0 || getView() == null || getEditorComponent() != null) {
            return;
        } // 除错-------------------------------------------------------------------
        if (allowMouseMultSeleOrDragEveFlag == ALLOWDRAG) {
            // int id = Integer.parseInt(getValueAt(seleRow,0).toString());
            // int apptype = getView().getApplication().getActiveViewInfo().getAppType();
            // PIMRecord record;
            // //特殊处理
            // if (apptype == ModelConstants.DELETED_ITEM_APP)
            // {
            // Vector ve = (Vector)getView().getApplication().getViewContents().get(seleRow);
            // String strType = ve.get(ve.size()-1).toString();
            // apptype = Integer.parseInt(strType);
            // record = PIMControl.ctrl.getModel().selectRecord(apptype,id);
            // }
            // else
            // {
            // record = PIMControl.ctrl.getModel().selectRecord(apptype,id);
            // }
            prmDGE.startDrag(DragSource.DefaultCopyDrop, new PIMRecordSelect(view.getSelectedRecords()), this);
        }
    }

    /**
     * 进行布局的方法
     */
    public void doLayout() {
        PIMTableColumn resizingColumn = getResizingColumn();
        // 没有正在调整中的列就设置不从首选宽度中求宽度
        if (resizingColumn == null) {
            setWidthsFromPreferredWidths(false);
        } else {
            // 得到其索引
            int columnIndex = viewIndexForColumn(resizingColumn);
            int delta = getWidth() - getColumnModel().getTotalColumnWidth();
            // 由调整列的索引和(总宽度和列总度之间的差)来适配
            accommodateDelta(columnIndex, delta);
            // 得到调整后差值
            delta = getWidth() - getColumnModel().getTotalColumnWidth();
            // 仍有偏差就把差值加在调整列上
            if (delta != 0) {
                resizingColumn.setWidth(resizingColumn.getWidth() + delta);
            }
        }

        super.doLayout();
    }

    /**
     * Calls the <code>configureEnclosingScrollPane</code> method. 调用配置封装滚动条的方法
     * 
     * @see #configureEnclosingScrollPane
     */
    public void addNotify() {
        super.addNotify();
        // 配置外部的滚动面板
        configureEnclosingScrollPane();
    }

    /**
     * 视图变动时,包括切换应用,改变视图风格,改变内容显示等时候PIMApplication会移除所有组件,并全部释放内存.
     * 届时PIMTable如果在视图上,本方法将被调到.本方法中将负责移除键盘监听器,滚筒位置是否合适,检查是否有没有正在编辑的内容尚未保存等. Calls the
     * <code>unconfigureEnclosingScrollPane</code> method.
     * 
     * @see #unconfigureEnclosingScrollPane
     */
    public void removeNotify() {
        // 调用键盘焦点管理器的方法,来移除本类的一个属性监听器.
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner",
                editorRemover);
        // 清空属性监听器引用,卸载滚动面板的配置
        editorRemover = null;
        unconfigureEnclosingScrollPane();
        if (cellEditor != null && view != null) // 如果这时候table上有单元格还处于编辑状态.
        {
            editingStopped(null);
        }
        // 最后调用一下父类的方法
        super.removeNotify();
    }

    /**
     * 把父类超掉
     * 
     * @param aRect
     *            要滚动到的矩形
     */
    public void scrollRectToVisible(
            Rectangle aRect) {
        Container parent;
        int dx = getX(), dy = getY();
        lastX = dx;
        for (parent = getParent(); !(parent == null) && !(parent instanceof JComponent)
                && !(parent instanceof CellRendererPane); parent = parent.getParent()) {
            Rectangle bounds = parent.getBounds();

            dx += bounds.x;
            dy += bounds.y;
        }

        if (!(parent == null) && !(parent instanceof CellRendererPane)) {
            aRect.x = isAddDelta() ? (aRect.x + dx) : lastX;
            aRect.y += dy;

            ((JComponent) parent).scrollRectToVisible(aRect);
            aRect.x = isAddDelta() ? (aRect.x + dx) : lastX;
            aRect.y -= dy;
        }
    }

    /**
     * 更新本组件的UI Notification from the <code>UIManager</code> that the L&F has changed. Replaces the current UI object
     * with the latest version from the <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        IPIMTableColumnModel cm = getColumnModel();
        // 把各列的编辑器和绘制器,表格头都更新一下UI
        for (int column = 0; column < cm.getColumnCount(); column++) {
            PIMTableColumn aColumn = cm.getColumn(column);
            updateSubComponentUI(aColumn.getCellRenderer());
            updateSubComponentUI(aColumn.getCellEditor());
            updateSubComponentUI(aColumn.getHeaderRenderer());
        }
        // 把本类中保存的由类的类型所设定的缺省绘制器都更新一下UI
        Enumeration defaultRenderers = defaultRenderersByColumnClass.elements();
        while (defaultRenderers.hasMoreElements()) {
            updateSubComponentUI(defaultRenderers.nextElement());
        }
        // 把本类中保存的由类的类型所设定的缺省编辑器都更新一下UI
        Enumeration defaultEditors = defaultEditorsByColumnClass.elements();
        while (defaultEditors.hasMoreElements()) {
            updateSubComponentUI(defaultEditors.nextElement());
        }
        // 自己也设一下UI
        setUI((BasicPIMTableUI) UIManager.getUI(this));
        resizeAndRepaint();
    }

    /**
     * 得到UI类的ID标识符 Returns the suffix used to construct the name of the L&F class used to render this component.
     *
     * @return 它应返回"PIMTableUI"这个字符串 the string "PIMTableUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * 进行键盘事件处理 protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) { boolean
     * retValue = super.processKeyBinding(ks, e, condition, pressed);
     *
     * if (!retValue && condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT && isFocusOwner() &&
     * !Boolean.FALSE.equals((Boolean)getClientProperty("JTable.autoStartsEdit"))) { // We do not have a binding for the
     * event. Component editorComponent = getEditorComponent(); if (editorComponent == null) { // Only attempt to
     * install the editor on a KEY_PRESSED, if (e == null || e.getID() != KeyEvent.KEY_PRESSED) { return false; } //
     * Don't start when just a modifier is pressed int code = e.getKeyCode(); if (code == KeyEvent.VK_SHIFT || code ==
     * KeyEvent.VK_CONTROL || code == KeyEvent.VK_ALT) { return false; } // Try to install the editor int anchorRow =
     * getSelectionModel().getAnchorSelectionIndex(); int anchorColumn = getColumnModel().getSelectionModel().
     * getAnchorSelectionIndex(); if (anchorRow != -1 && anchorColumn != -1 && !isEditing()) { if
     * (!editCellAt(anchorRow, anchorColumn)) { return false; } } editorComponent = getEditorComponent(); if
     * (editorComponent == null) { return false; } } } return retValue; } /**
     * 
     * @param e
     *            键盘事件源
     */
    public void processKeyEvent(
            KeyEvent e) {
        super.processKeyEvent(e);
        // 在用了JDK1.4后,当按下Alt键后按Page Down或Page Up键或Home键后,会在Alt键释放之后发一个'!',
        // 'c', 'M'三个键的Typed事件,若禁止,这三个键的真正的Typed事件就随之被禁止.暂时这样.
        if (e.getID() == KeyEvent.KEY_PRESSED && e.isConsumed()) {
            return;
        }
        // 得到虚拟键,字符,修饰键
        int code = e.getKeyCode();
        int keyChar = e.getKeyChar();
        int modifiers = e.getModifiers();

        // 暂时其他键不屏蔽
        if (view != null) {
            if (e.getID() == KeyEvent.KEY_RELEASED && code == KeyEvent.VK_DELETE) {
                int[] selectedRows = getSelectedRows();
                if (selectedRows != null) {
                    int length = selectedRows.length;
                    int[] ids = new int[length];
                    for (int i = 0; i < length; i++) {
                        // ids[i] = PIMPool.pool.getIntegerKey((String)getValueAt(selectedRows[i], 0)).intValue();
                        ids[i] = ((Integer) getValueAt(selectedRows[i], 0)).intValue();
                    }

                    if (modifiers == KeyEvent.SHIFT_MASK) {
                        new DeleteAction(true).actionPerformed(null);
                    } else {
                        new DeleteAction().actionPerformed(null);
                    }
                }
            } else if (code == KeyEvent.VK_DELETE) {
            }
        }

        // Test Copy and Paste
        if (!isEditing()) {
            if (code == KeyEvent.VK_V && e.isControlDown()) {
                int selectedRow = getSelectedRow();
                int selectedColumn = getSelectedColumn();
                if (selectedRow >= 0 && selectedColumn > 0) {
                    setValueAt("Test", selectedRow, selectedColumn);
                }
            }
        }

        Component editorComponent = getEditorComponent();
        // 有编辑器组件才进行
        if (editorComponent == null) {
            // 得到事件应为'敲击键盘'
            if (e == null || (e.getID() != KeyEvent.KEY_PRESSED && e.getID() != KeyEvent.KEY_RELEASED)) {
                return;
            }

            // 暂时如此，此处是JDK的BUG
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                // 如1.可打印字符,2,两个修饰键没有按
                // 目的是屏蔽!
                if ((keyChar > 32 && keyChar < 65535 && keyChar != 127) && (modifiers & KeyEvent.ALT_MASK) == 0
                        && (modifiers & KeyEvent.CTRL_MASK) == 0
                        || ((keyChar == 32 || code == KeyEvent.VK_BACK_SPACE) && modifiers == 0)) {
                    // return ;
                } else {
                    return;
                }
            }

            if (e.getID() == KeyEvent.KEY_RELEASED) {
                // 目的是屏蔽!
                if ((keyChar > 32 && keyChar < 65535 && keyChar != 127) && (modifiers & KeyEvent.ALT_MASK) == 0
                        && (modifiers & KeyEvent.CTRL_MASK) == 0
                        || ((keyChar == 32 || code == KeyEvent.VK_BACK_SPACE) && modifiers == 0)) {
                } else {
                    return;
                }
            }
            // 目的是屏蔽修饰键
            if (code == KeyEvent.VK_SHIFT || code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_ALT) {
                return;
            }
            // 得到锚定选中行和锚定选中列
            int anchorRow = getSelectionModel().getAnchorSelectionIndex();
            int anchorColumn = getColumnModel().getSelectionModel().getAnchorSelectionIndex();

            // 在有效的情况下
            if (anchorRow != -1 && anchorColumn != -1 && !isEditing()) { // 不在编辑该单元格就返回
                if (!editCellAt(anchorRow, anchorColumn, null)) {
                    return;
                }
            }
            // 此处含义模糊
            editorComponent = getEditorComponent();
            if (editorComponent == null) {
                return;
            }
        }
        // 是一个 JComponent 且允许通过键盘事件转移焦点时就请求焦点,
        if (editorComponent instanceof JComponent) {
            if (getSurrendersFocusOnKeystroke()) {
                editorComponent.requestFocus();
            }
            // 否则把事件重新派发给编辑器组件
            repostEvent(e, editorComponent);
        }
    }

    /**
     * 重载父类中的方法,得到工具条提示 Overrides <code>JComponent</code>'s <code>getToolTipText</code> method in order to allow the
     * renderer's tips to be used if it has text set.
     * <p>
     * <bold>Note:</bold> For <code>PIMTable</code> to properly display tooltips of its renderers <code>PIMTable</code>
     * must be a registered component with the <code>ToolTipManager</code>. This is done automatically in
     * <code>initializeLocalVars</code>, but if at a later point <code>PIMTable</code> is told
     * <code>setToolTipText(null)</code> it will unregister the table component, and no tips from renderers will display
     * anymore.
     *
     * @param event
     *            鼠标事件源
     * @return 代表工具条提示的字符串
     * @see JComponent#getToolTipText As of Swing version 1.0.3, replaced by <code>doLayout()</code>.
     */
    public String getToolTipText(
            MouseEvent event) {
        String tmpTip = null;
        Point p = event.getPoint();

        // Locate the renderer under the event location
        // 得到所在行和列索引
        int hitColumnIndex = columnAtPoint(p);
        int hitRowIndex = rowAtPoint(p);

        if ((hitColumnIndex != -1) && (hitRowIndex != -1)) {
            // 得到本单元格的绘制器,作预备处理
            IPIMCellRenderer renderer = getCellRenderer(hitRowIndex, hitColumnIndex);
            Component component = prepareRenderer(renderer, hitRowIndex, hitColumnIndex);

            // Now have to see if the component is a JComponent before
            // getting the tip
            if (component instanceof JComponent) {
                // Convert the event to the renderer's coordinate system
                // 得到本单元格的绘制区域
                Rectangle cellRect = getCellRect(hitRowIndex, hitColumnIndex, false);
                p.translate(-cellRect.x, -cellRect.y);
                // 造出本事件的复制品
                MouseEvent newEvent =
                        new MouseEvent(component, event.getID(), event.getWhen(), event.getModifiers(), p.x, p.y,
                                event.getClickCount(), event.isPopupTrigger());
                // 由父类方法得到工具条提示
                tmpTip = ((JComponent) component).getToolTipText(newEvent);
            }
        }

        // No tip from the renderer get our own tip
        // 为空使用我们自己的方法
        if (tmpTip == null) {
            tmpTip = getToolTipText();
        }

        return tmpTip;
    }

    /**
     * 处理键绑定
     * 
     * @param ks
     *            键击
     * @param e
     *            键盘事件
     * @param condition
     *            处理条件
     * @param pressed
     *            是否为按下
     * @return 处理是否成功
     */
    protected boolean processKeyBinding(
            KeyStroke ks,
            KeyEvent e,
            int condition,
            boolean pressed) {
        boolean retValue = super.processKeyBinding(ks, e, condition, pressed);
        return retValue;
    }

    /**
     * 返回代表本类实例状态的字符串 Returns a string representation of this table. This method is intended to be used only for
     * debugging purposes, and the content and format of the returned string may vary between implementations. The
     * returned string may be empty but may not be <code>null</code>.
     *
     * @return a string representation of this table
     */
    protected String paramString() {
        // 初始化各个字符串

        // 网格线颜色,是否显示水平线,垂直线
        String gridColorString = (gridColor != null ? gridColor.toString() : CASUtility.EMPTYSTR);
        String showHorizontalLinesString = (showHorizontalLines ? PIMPool.BOOLEAN_TRUE : PIMPool.BOOLEAN_FALSE);
        String showVerticalLinesString = (showVerticalLines ? PIMPool.BOOLEAN_TRUE : PIMPool.BOOLEAN_FALSE);

        // 尺寸调整模型
        String autoResizeModeString;
        if (autoResizeMode == AUTO_RESIZE_OFF) {
            autoResizeModeString = "AUTO_RESIZE_OFF";
        } else if (autoResizeMode == AUTO_RESIZE_NEXT_COLUMN) {
            autoResizeModeString = "AUTO_RESIZE_NEXT_COLUMN";
        } else if (autoResizeMode == AUTO_RESIZE_SUBSEQUENT_COLUMNS) {
            autoResizeModeString = "AUTO_RESIZE_SUBSEQUENT_COLUMNS";
        } else if (autoResizeMode == AUTO_RESIZE_LAST_COLUMN) {
            autoResizeModeString = "AUTO_RESIZE_LAST_COLUMN";
        } else if (autoResizeMode == AUTO_RESIZE_ALL_COLUMNS) {
            autoResizeModeString = "AUTO_RESIZE_ALL_COLUMNS";
        } else {
            autoResizeModeString = CASUtility.EMPTYSTR;
        }

        // 是否由表格数据模型自动创建列,视口尺寸,允许行选中,单元格选中
        String autoCreateColumnsFromModelString =
                (autoCreateColumnsFromModel ? PIMPool.BOOLEAN_TRUE : PIMPool.BOOLEAN_FALSE);
        String preferredViewportSizeString =
                (preferredViewportSize != null ? preferredViewportSize.toString() : CASUtility.EMPTYSTR);
        String rowSelectionAllowedString = (rowSelectionAllowed ? PIMPool.BOOLEAN_TRUE : PIMPool.BOOLEAN_FALSE);
        String cellSelectionEnabledString = (cellSelectionEnabled ? PIMPool.BOOLEAN_TRUE : PIMPool.BOOLEAN_FALSE);

        // 前景色和背景色
        String selectionForegroundString =
                (selectionForeground != null ? selectionForeground.toString() : CASUtility.EMPTYSTR);
        String selectionBackgroundString =
                (selectionBackground != null ? selectionBackground.toString() : CASUtility.EMPTYSTR);

        // 现在把这几个字符串统统加起来返回
        return super.paramString() + ",autoCreateColumnsFromModel=" + autoCreateColumnsFromModelString
                + ",autoResizeMode=" + autoResizeModeString + ",cellSelectionEnabled=" + cellSelectionEnabledString
                + ",editingColumn=" + editingColumn + ",editingRow=" + editingRow + ",gridColor=" + gridColorString
                + ",preferredViewportSize=" + preferredViewportSizeString + ",rowHeight=" + rowHeight + ",rowMargin="
                + rowMargin + ",rowSelectionAllowed=" + rowSelectionAllowedString + ",selectionBackground="
                + selectionBackgroundString + ",selectionForeground=" + selectionForegroundString
                + ",showHorizontalLines=" + showHorizontalLinesString + ",showVerticalLines=" + showVerticalLinesString;
    }

    /**
     * 初始化排序信息
     * 
     * @called by PIMTableView
     */
    public void initHeaderSortStatus() {
        if (view != null) {
            String sortCriteria = view.getApplication().getActiveViewInfo().getSortCritia();
            if (sortCriteria != null && sortCriteria.length() != 0) {
                int[] sortInfo = CASUtility.commaStrToIntAry(sortCriteria);
                int[] fieldIndex =
                        CASUtility.commaStrToIntAry(view.getApplication().getActiveViewInfo().getFieldNames());
                if (sortInfo == null || sortInfo.length == 0) {
                    return;
                }
                if (sortInfo.length % 2 == 1) {
                    throw new IllegalArgumentException("sortCriteria is error");
                }
                getTableHeader().getHeaderListener().getRenderer().clearState();
                for (int i = 0; i < sortInfo.length; i += 2) {
                    int sortCol = 0;
                    for (int j = 1; j < fieldIndex.length; j++) {
                        if (fieldIndex[j] == sortInfo[i]) {
                            sortCol = j;
                            break;
                        }
                    }
                    boolean isAscent = sortInfo[i + 1] == 0 ? true : false;
                    getTableHeader().getHeaderListener().getRenderer().setSelectedColumn(sortCol, isAscent);
                }
                getTableHeader().repaint();
            } else {
                getTableHeader().getHeaderListener().getRenderer().clearState();
            }
        }
    }

    public void addHeadFocusListener(
            FocusListener prmListener) {
        tableHeader.addFocusListener(prmListener);
    }

    /**
     * 等同于新建一个滚动面板,最好不要用呀,有问题的 Equivalent to <code>new PIMScrollPane(aTable)</code>.
     *
     * @deprecated As of Swing version 1.0.2, replaced by <code>new PIMScrollPane(aTable)</code>.
     * @param aTable
     *            传入的表格
     * @return 返回滚动面板的引用
     */
    static public PIMScrollPane createScrollPaneForTable(
            PIMTable aTable) {
        return new PIMScrollPane(aTable);
    }

    /**
     * 从数据模型中创建缺省的的列 Creates default columns for the table from the data model using the <code>getColumnCount</code>
     * method defined in the <code>IPIMTableModel</code> interface.
     * <p>
     * Clears any existing columns before creating the new columns based on information from the model.
     *
     * @see #getAutoCreateColumnsFromModel
     */
    public void createDefaultColumnsFromModel() {
        // 取表格数据模型,在不为空时进行
        IPIMTableModel m = getModel();
        if (m != null) {
            // 取列模型,将其中原有的列统统移除
            IPIMTableColumnModel cm = getColumnModel();
            while (cm.getColumnCount() > 0) {
                cm.removeColumn(cm.getColumn(0));
            }
            // 以表格数据模型列数为步长进行遍历
            for (int i = 0; i < m.getColumnCount(); i++) {
                // 新建出列,加上去
                PIMTableColumn newColumn = new PIMTableColumn(i);
                addColumn(newColumn);
            }
        }
    }

    /**
     * 选中所有单元格的方法 Selects all rows, columns, and cells in the table.
     */
    public void selectAll() {
        // If I'm currently editing, then I should stop editing
        // 如果正在编辑,就要移除编辑器
        if (isEditing()) {
            removeEditor();
        }
        // 有行有列才进行
        if (getRowCount() > 0 && getColumnCount() > 0) {
            // 所有行和所有列
            setRowSelectionInterval(0, getRowCount() - 1);
            setColumnSelectionInterval(0, getColumnCount() - 1);
        }
    }

    /**
     * 清空所选中的单元格 Deselects all selected columns and rows.
     */
    public void clearSelection() {
        // 在列模型中全部清空,本表格中也是
        columnModel.getSelectionModel().clearSelection();
        selectionModel.clearSelection();
    }

    /**
     * 配置包含本表的滚动面板 If this <code>PIMTable</code> is the <code>viewportView</code> of an enclosing
     * <code>PIMScrollPane</code> (the usual situation), configure this <code>ScrollPane</code> by, amongst other
     * things, installing the table's <code>tableHeader</code> as the <code>columnHeaderView</code> of the scroll pane.
     * When a <code>PIMTable</code> is added to a <code>PIMScrollPane</code> in the usual way, using
     * <code>new PIMScrollPane(myTable)</code>, <code>addNotify</code> is called in the <code>PIMTable</code> (when the
     * table is added to the viewport). <code>PIMTable</code>'s <code>addNotify</code> method in turn calls this method,
     * which is protected so that this default installation procedure can be overridden by a subclass.
     *
     * @see #addNotify
     */
    protected void configureEnclosingScrollPane() {
        // 得到父组件,是 JViewport 才进行
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent(); // 得到父级容器
            if (gp instanceof PIMScrollPane) // 如是一个JScrollPane
            {
                PIMScrollPane scrollPane = (PIMScrollPane) gp;
                JViewport viewport = scrollPane.getViewport(); // 得到视口
                // 保证视口非空且非指向自身
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                // 设置表格的头部是一个表格头,这里是用了滚动面板的特性
                scrollPane.setColumnHeaderView(getTableHeader());
                Border border = scrollPane.getBorder();
                // 设置表格的边框
                if (border == null || border instanceof UIResource) {
                    scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
                }
            }
        }
    }

    /**
     * 取消包含本表的滚动面板 Reverses the effect of <code>configureEnclosingScrollPane</code> by replacing the
     * <code>columnHeaderView</code> of the enclosing scroll pane with <code>null</code>. <code>PIMTable</code>'s
     * <code>removeNotify</code> method calls this method, which is protected so that this default uninstallation
     * procedure can be overridden by a subclass.
     *
     * @see #removeNotify
     * @see #configureEnclosingScrollPane
     */
    protected void unconfigureEnclosingScrollPane() {
        // 得到父组件,是 JViewport 才进行
        Container p = getParent();
        if (p instanceof JViewport) {
            // 得到父级容器
            Container gp = p.getParent();
            // 如是一个 PIMScrollPane
            if (gp instanceof PIMScrollPane) {
                PIMScrollPane scrollPane = (PIMScrollPane) gp;
                // 得到视口
                JViewport viewport = scrollPane.getViewport();

                // 保证视口非空且非指向自身
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                // 清空表格的头部引用,这里是用了滚动面板的特性
                scrollPane.setColumnHeaderView(null);
            }
        }
    }

    /*
     * 初始化实例变量,一般在初始化时调用 Initializes table properties to their default values.
     */
    protected void initializeLocalVars() {
        getSelectionModel().setAnchorSelectionIndex(0); // 设置起始锚定选择列号为0
        setOpaque(true); // 不透明,(有背景色)
        createDefaultRenderers(); // 设置缺省的单元格绘制器
        createDefaultEditors(); // 设置缺省的单元格编辑器

        setTableHeader(createDefaultTableHeader()); // 设置表格头

        setShowGrid(true); // 显示网格线
        setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS); // 默认允许自动调整调整列之后的各列尺寸以匹配视口
        setRowHeight(20);// 默认行高为20个像素
        // if (hasEditor)
        // {
        // setRowHeight (1, 2);
        // }
        setRowMargin(1); // 设置行边缘空白为1个像素,

        setRowSelectionAllowed(true); // 默认可选择一行
        setCellEditor(null);// 设置没有单元格编辑器(大概是使用默认的编辑器)
        // if (hasEditor && getSelectedRow() == 1)
        // {
        // setDefaultEditor (null, null);
        // setCellEditor(null);
        // }
        setEditingColumn(-1); // 目前没有正在编辑的单元格
        setEditingRow(-1);
        setSurrendersFocusOnKeystroke(false); // 默认不允许由键盘交出焦点
        setPreferredScrollableViewportSize(new Dimension(450, 400)); // 设置默认视口尺寸(开始一定要设置的,没有法子的事,虽然十之八九不符合实际需要)

        initHeaderSortStatus(); // 初始化排序信息

        ToolTipManager.sharedInstance().registerComponent(this); // 设置一个工具条提示管理器,把表格实例自身注册
        setAutoscrolls(true); // 允许自动滚动
        // editorKeyListener = new EditorKeyHandle();
        recongizer = dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }

    private void initTable(
            IPIMTableModel dm,
            IPIMTableColumnModel cm,
            ListSelectionModel sm) {
        if (cm == null) {// 列模型的处理
            cm = createDefaultColumnModel();
            autoCreateColumnsFromModel = true;// 打开一个开关,自动从数据模型中创建列
        }
        setColumnModel(cm);

        if (sm == null)// 选择模型的处理
            sm = createDefaultSelectionModel();
        setSelectionModel(sm);

        if (dm == null) // 数据模型的处理
            dm = new PIMTableModelAryBased(new Object[0][0], new Object[0]);
        setModel(dm);

        initializeLocalVars(); // 初始化本地变量
        updateUI(); // 更新UI
        setFocusTraversalKeysEnabled(false);
    }

    /*
     * 判断一下这行是否出界
     * @param row 传入的行
     * @throws IllegalArgumentException 本方法会抛出无效参数异常
     * @return 简单判断一下这行是否出界即将它返回
     */
    private int boundRow(
            int row) throws IllegalArgumentException {
        if (row < 0 || row >= getRowCount())
            throw new IllegalArgumentException("Row index out of range");
        return row;
    }

    /**
     * 判断一下这列是否出界
     * 
     * @param col
     *            传入的列
     * @return 简单判断一下这列是否出界即将它返回
     */
    private int boundColumn(
            int col) {
        if (col < 0 || col >= getColumnCount())
            throw new IllegalArgumentException("Column index out of range");
        return col;
    }

    /**
     * 添加新的选中行 Adds the rows from <code>index0</code> to <code>index1</code>, inclusive, to the current selection.
     *
     * @param index0
     *            起始行号 one end of the interval
     * @param index1
     *            结束行号 the other end of the interval
     */
    public void addRowSelectionInterval(
            int index0,
            int index1) {
        try { // 保证一下数据正确就将其设置
            selectionModel.addSelectionInterval(boundRow(index0), boundRow(index1));
        } catch (IllegalArgumentException e) {
            ErrorUtil.write("PIMTable.addRowSelectionInterval(): out of bounds");// 方法中发现行列越界.");
        }
    }

    /**
     * 添加新的选中列 Adds the columns from <code>index0</code> to <code>index1</code>, inclusive, to the current selection.
     *
     * @param index0
     *            起始列号 one end of the interval
     * @param index1
     *            结束列号 the other end of the interval
     */
    public void addColumnSelectionInterval(
            int index0,
            int index1) {// 保证一下数据正确就将其在列模型设置
        columnModel.getSelectionModel().addSelectionInterval(boundColumn(index0), boundColumn(index1));
    }

    /**
     * 移除几个选中的行 Deselects the rows from <code>index0</code> to <code>index1</code>, inclusive.
     * 
     * @param index0
     *            起始行号 one end of the interval
     * @param index1
     *            结束行号 the other end of the interval
     */
    public void removeRowSelectionInterval(
            int index0,
            int index1) { // 保证一下数据正确就将其设置
        try {
            selectionModel.removeSelectionInterval(boundRow(index0), boundRow(index1));
        } catch (IllegalArgumentException e) {
            ErrorUtil.write("PIMTable.removeRowSelectionInterval(): out of bounds");// addRowSelectionInterval方法中发现行列越界.");
        }
    }

    /**
     * 移除几个选中的列 Deselects the columns from <code>index0</code> to <code>index1</code>, inclusive.
     * 
     * @param index0
     *            起始列号 one end of the interval
     * @param index1
     *            结束列号 the other end of the interval
     */
    public void removeColumnSelectionInterval(
            int index0,
            int index1) {
        // 保证一下数据正确就将其在列模型设置
        columnModel.getSelectionModel().removeSelectionInterval(boundColumn(index0), boundColumn(index1));
    }

    /**
     * 得到所选中的第一行,-1表示没有选中的 Returns the index of the first selected row, -1 if no row is selected.
     * 
     * @return the index of the first selected row
     */
    public int getSelectedRow() {
        return selectionModel.getMinSelectionIndex();
    }

    /**
     * 用于设置选中的表格行
     */
    public void setSelectedRow(
            int pRow) {
        if (pRow >= 0 && pRow < getRowCount())
            selectionModel.setSelectionInterval(pRow, pRow);
    }

    /**
     * 得到所选中的第一列,-1表示没有选中的
     * 
     * @return the index of the first selected column
     */
    public int getSelectedColumn() { // 从列选择模型中取数据
        return columnModel.getSelectionModel().getMinSelectionIndex();
    }

    /**
     * 得到所有选中的行的索引值
     *
     * @return an array of integers containing the indices of all selected rows, or an empty array if no row is selected
     * @see #getSelectedRow
     */
    public int[] getSelectedRows() {
        // 得到选择模型中的最小行号和最大行号
        int iMin = selectionModel.getMinSelectionIndex();
        int iMax = selectionModel.getMaxSelectionIndex();

        // 没有就造个空的数组返回
        if ((iMin == -1) || (iMax == -1))
            return new int[0];

        // 造个数组容纳所有的选中行(中间可能有没有选中的,间隔选的)
        int[] rvTmp = new int[1 + (iMax - iMin)];
        int n = 0;

        for (int i = iMin; i <= iMax; i++)
            // 遍历,有就放进去
            if (selectionModel.isSelectedIndex(i))
                rvTmp[n++] = i;
        // 这个数组正合适,复制一下
        int[] rv = new int[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }

    /**
     * 得到所有选中的列的索引值
     * 
     * @return an array of integers containing the indices of all selected columns, or an empty array if no column is
     *         selected
     * @see #getSelectedColumn
     */
    public int[] getSelectedColumns() { // 在列模型中得到
        return columnModel.getSelectedColumns();
    }

    /**
     * 得到选中的行的总数
     * 
     * @return the number of selected rows, 0 if no rows are selected
     */
    public int getSelectedRowCount() {
        // 得到选择模型中的最小行号和最大行号
        int iMin = selectionModel.getMinSelectionIndex();
        int iMax = selectionModel.getMaxSelectionIndex();
        int count = 0;
        // 遍历,有就进累加器
        for (int i = iMin; i <= iMax; i++)
            if (selectionModel.isSelectedIndex(i))
                count++;
        // 返回累加结果
        return count;
    }

    /**
     * 得到选中的列的总数
     * 
     * @return the number of selected columns, 0 if no columns are selected
     */
    public int getSelectedColumnCount() { // 从列模型中得数据
        return columnModel.getSelectedColumnCount();
    }

    /**
     * 判断指定行是否被选中
     * 
     * @return 被选中为真 true if the row at index <code>row</code> is selected, where 0 is the * first row
     * @param row
     *            指定行
     */
    public boolean isRowSelected(
            int row) {
        return selectionModel.isSelectedIndex(row); // 从选择模型中得数据
    }

    /**
     * 判断指定列是否被选中
     * 
     * @param column
     *            指定列
     * @return 被选中为真 true if the row at index <code>row</code> is selected, where 0 is the first row
     */
    public boolean isColumnSelected(
            int column) { // 从列模型中得数据
        return columnModel.getSelectionModel().isSelectedIndex(column);
    }

    /**
     * 判断某单元格是否被选中 Returns true if the cell at the specified position is selected.
     * 
     * @param row
     *            指定行 the row being queried
     * @param column
     *            指定列 the column being queried
     * @return true if the cell at index <code>(row, column)</code> is selected, where the first row and first column
     *         are at index 0
     */
    public boolean isCellSelected(
            int row,
            int column) {
        // 行和列都不可选自然不可选
        if (!getRowSelectionAllowed() && !getColumnSelectionAllowed()) {
            return false;
        }
        // 返回都可选
        return (!getRowSelectionAllowed() || isRowSelected(row))
                && (!getColumnSelectionAllowed() || isColumnSelected(column));
    }

    /**
     * 改变列选择模型
     * 
     * @param sm
     *            新的列选择模型
     * @param index
     *            索引值
     * @param prmToggle
     *            是否为反转
     * @param prmExtend
     *            是否扩展
     * @param selected
     *            是否已有选择
     */
    private void changeSelectionModel(
            ListSelectionModel sm,
            int index,
            boolean prmToggle,
            boolean prmExtend,
            boolean selected) {
        // 扩展
        if (prmExtend) {
            // 反转
            if (prmToggle) {
                sm.setAnchorSelectionIndex(index);
            }
            // 不反转
            else {
                sm.setLeadSelectionIndex(index);
            }
        }
        // 不扩展
        else {
            // 反转
            if (prmToggle) {
                // 有选中
                if (selected) {
                    sm.removeSelectionInterval(index, index);
                }
                // 没有选中
                else {
                    sm.addSelectionInterval(index, index);
                }
            }
            // 不反转
            else {
                sm.setSelectionInterval(index, index);
            }
        }
    }

    /**
     * 根据反转和扩展这两个标志来更新表格的选中项
     * 
     * @called by: PIMTableView;BasicPIMTableUI; Updates the selection models of the table, depending on the state of
     *         the two flags: <code>prmToggle</code> and <code>prmExtend</code>. All changes to the selection that are
     *         the result of keyboard or mouse events received by the UI are channeled through this method so that the
     *         behavior may be overridden by a subclass.
     *         <p>
     *         This implementation uses the following conventions:
     *         <ul>
     *         <li> <code>prmToggle</code>: <em>false</em>, <code>prmExtend</code>: <em>false</em>. Clear the previous
     *         selection and ensure the new cell is selected.
     *         <li> <code>prmToggle</code>: <em>false</em>, <code>prmExtend</code>: <em>true</em>. prmExtend the previous
     *         selection to include the specified cell.
     *         <li> <code>prmToggle</code>: <em>true</em>, <code>prmExtend</code>: <em>false</em>. If the specified cell
     *         is selected, deselect it. If it is not selected, select it.
     *         <li> <code>prmToggle</code>: <em>true</em>, <code>prmExtend</code>: <em>true</em>. Leave the selection
     *         state as it is, but move the anchor index to the specified location.
     *         </ul>
     * @param prmIsTABFromBegin
     *            是否滚动到开始,
     * @param prmRow
     *            涉及的起始行 affects the selection at <code>row</code>
     * @param prmCol
     *            涉及的起始列 affects the selection at <code>column</code>
     * @param prmToggle
     *            是否反转 see description above
     * @param prmExtend
     *            是否扩展 if true, prmExtend the current selection
     */
    public void changeSelection(
            int prmRow,
            int prmCol,
            boolean prmToggle,
            boolean prmExtend,
            boolean prmIsTABFromBegin) {
        // 取行和列的选择模型
        ListSelectionModel rsm = getSelectionModel();
        ListSelectionModel csm = getColumnModel().getSelectionModel();

        // 得到指定单元格的选中状态
        boolean selected = isCellSelected(prmRow, prmCol);
        int tmpSelectedCol = prmCol;
        if (getView() != null) {
            int tmpAppType = getView().getApplication().getActiveViewInfo().getAppIndex();
            //
            if (// tmpAppType == ModelConstants.LOCAL_APP ||
            tmpAppType == ModelCons.INBOX_APP || tmpAppType == ModelCons.OUTBOX_APP
                    || tmpAppType == ModelCons.DIARY_APP || tmpAppType == ModelCons.SENDED_APP
                    || tmpAppType == ModelCons.DELETED_ITEM_APP) {
                //
                tmpSelectedCol = getSelectedColumn() < 0 ? getSelectedColumn() : prmCol;
            }
        }
        // 将其在行和列的选择模型中的状态均更新一下
        changeSelectionModel(csm, tmpSelectedCol, prmToggle, prmExtend, selected);
        changeSelectionModel(rsm, prmRow, prmToggle, prmExtend, selected);

        // 自动滚动的处理
        if (getAutoscrolls()) {
            // 取可视区域
            Rectangle cellRect = getCellRect(prmRow, tmpSelectedCol, false);
            // 如果是从最前面开始,滚动矩形的X坐标置0
            if (prmIsTABFromBegin && cellRect != null) {
                cellRect.x = 0;
            }
            if (cellRect != null) {
                // 使该选中区域可见
                scrollRectToVisible(cellRect);
            }
        }
        // 这里实际上是借用这个方法来模拟鼠标点击选中,让预览面板来相应调整
        if (view != null && prmRow != selectionModel.getMinSelectionIndex()
                && prmRow != selectionModel.getMaxSelectionIndex()) {
            view.getApplication().processMouseClickAction(this);
        }
    }

    /**
     * 重载父类方法
     * 
     * @param prmRow
     *            所在行
     * @param prmCol
     *            所在列
     */
    public void scrollToRect(
            int prmRow,
            int prmCol) {
        if (getAutoscrolls()) {
            // 取可视区域
            Rectangle cellRect = getCellRect(prmRow, prmCol);
            if (cellRect != null) {
                // 使该选中区域可见
                scrollRectToVisible(cellRect);
            }
        }
    }

    /**
     * 与它同名的三参数方法得到的宽度永远是75 所以重抄一遍以斧正
     * 
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @return 对应的矩形
     */
    protected Rectangle getCellRect(
            int row,
            int column) {
        Rectangle r = new Rectangle();
        // 先声明一个有效标志
        boolean valid = true;
        // 行小于0置无效
        if (row < 0) {
            valid = false;
        }
        // 大于行数置无效,但取得行高
        else if (row >= getRowCount()) {
            r.y = getHeight();
            valid = false;
        }
        // 正常处理
        else {
            r.height = getRowHeight(row);
            // 取行高,有行模型从中取值,否则取乘积
            r.y = (rowModel == null) ? row * r.height : rowModel.getPosition(row);
        }
        // 至此我们求得了Y坐标和高度
        // 列小于0,
        if (column < 0) {
            // 如不是自左到右,自然取得宽度
            if (!getComponentOrientation().isLeftToRight()) {
                r.x = getWidth();
            }
            valid = false;
        }
        // 大于列数置无效,但取得宽度
        else if (column >= getColumnCount()) {
            if (getComponentOrientation().isLeftToRight()) {
                r.x = getWidth();
            }
            valid = false;
        } else {
            IPIMTableColumnModel cm = getColumnModel();
            // 是自左到右就去遍历列,求得X坐标
            if (getComponentOrientation().isLeftToRight()) {
                for (int i = 0; i < column; i++) {
                    int temp = cm.getColumn(i).getPreferredWidth();
                    r.x += temp;
                }
            }
            // 否则从右边开始
            else {
                for (int i = cm.getColumnCount() - 1; i > column; i--) {
                    r.x += cm.getColumn(i).getWidth();
                }
            }
            r.width = cm.getColumn(column).getWidth();
        }
        // 至此我们求得了X坐标和宽度
        if (valid) // && !includeSpacing)
        {
            int rm = getRowMargin();
            int cm = getColumnModel().getColumnMargin();
            // 是在边缘空白内的就作如下调整
            r.setBounds(r.x + cm / 2, r.y + rm / 2, r.width - cm, r.height - rm);
        }
        return r;
    }

    /**
     * 根据列的ID对象得到相匹配的列 Returns the <code>PIMTableColumn</code> object for the column in the table whose identifier is
     * equal to <code>identifier</code>, when compared using <code>equals</code>.
     *
     * @return the <code>PIMTableColumn</code> object that matches the identifier
     * @param identifier
     *            the identifier object
     */
    public PIMTableColumn getColumn(
            Object identifier) {
        // 取列模型,根据表格头的对象得其索引
        IPIMTableColumnModel cm = getColumnModel();
        int columnIndex = cm.getColumnIndex(identifier);
        return cm.getColumn(columnIndex);
    }

    //
    // Informally implement the IPIMTableModel interface.
    //

    /**
     * 转化列在视图中的索引为表格数据模型中的索引 Maps the index of the column in the view at <code>viewColumnIndex</code> to the index of
     * the column in the table model. Returns the index of the corresponding column in the model. If
     * <code>viewColumnIndex</code> is less than zero, returns <code>viewColumnIndex</code>.
     *
     * @param viewColumnIndex
     *            视图中的索引 the index of the column in the view
     * @return 返回在数据模型中对应的索引 the index of the corresponding column in the model
     * @see #convertColumnIndexToView
     */
    public int convertColumnIndexToModel(
            int viewColumnIndex) {
        // 小于0直接返回
        if (viewColumnIndex < 0) {
            return viewColumnIndex;
        }
        // 否则取列模型中的该数据
        int modelIndex = getColumnModel().getColumn(viewColumnIndex).getModelIndex();
        // A.s("ModelIndex: " + modelIndex);
        return modelIndex;
    }

    /**
     * 转化列在表格数据模型中的索引为视图中的索引 Maps the index of the column in the table model at <code>modelColumnIndex</code> to the
     * index of the column in the view. Returns the index of the corresponding column in the view; returns -1 if this
     * column is not being displayed. If <code>modelColumnIndex</code> is less than zero, returns
     * <code>modelColumnIndex</code>.
     *
     * @param modelColumnIndex
     *            数据模型中对应的索引 the index of the column in the model
     * @return 返回在视图中的索引 the index of the corresponding column in the view
     * @see #convertColumnIndexToModel
     */
    public int convertColumnIndexToView(
            int modelColumnIndex) {
        // 小于0直接返回
        if (modelColumnIndex < 0) {
            return modelColumnIndex;
        }
        IPIMTableColumnModel cm = getColumnModel();
        // 在列模型中遍历,返回其索引
        for (int column = 0; column < getColumnCount(); column++) {
            if (cm.getColumn(column).getModelIndex() == modelColumnIndex) {
                return column;
            }
        }
        // 没有就用-1表示
        return -1;
    }

    /**
     * 得到总行数,从数据模型中得到 Returns the number of rows in this table's model.
     * 
     * @return the number of rows in this table's model
     * @see #getColumnCount
     */
    public int getRowCount() { // 从数据模型中得到
        return getModel().getRowCount();
    }

    /**
     * 得到总列数,是列模型中的,不同于表格数据模型中的 Returns the number of columns in the column model. Note that this may be different from
     * the number of columns in the table model.
     *
     * @return the number of columns in the table
     * @see #getRowCount
     * @see #removeColumn
     */
    public int getColumnCount() {
        return getColumnModel().getColumnCount();
    }

    /**
     * 得到列名 Returns the name of the column appearing in the view at column position <code>column</code>.
     *
     * @param column
     *            the column in the view being queried
     * @return the name of the column at position <code>column</code> in the view where the first column is column 0
     */
    public String getColumnName(
            int column) { // 从数据模型中得到
        return getModel().getColumnName(convertColumnIndexToModel(column));
    }

    /**
     * 得到列的类型 Returns the type of the column appearing in the view at column position <code>column</code>.
     *
     * @param column
     *            the column in the view being queried
     * @return the type of the column at position <code>column</code> in the view where the first column is column 0
     */
    public Class getColumnClass(
            int column) { // 从数据模型中得到
        return getModel().getColumnClass(convertColumnIndexToModel(column));
    }

    /**
     * 返回指定单元格中的值 Returns the cell value at <code>row</code> and <code>column</code>.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display order, and not in the
     * <code>IPIMTableModel</code>'s column order. This is an important distinction because as the user rearranges the
     * columns in the table, the column at a given index in the view will change. Meanwhile the user's actions never
     * affect the model's column ordering.
     *
     * @param row
     *            所在行 the row whose value is to be queried
     * @param column
     *            the column whose value is to be queried
     * @return the Object at the specified cell
     */
    public Object getValueAt(
            int row,
            int column) { // 从数据模型中得到
        return getModel().getValueAt(row, convertColumnIndexToModel(column));
    }

    /**
     * 设定指定单元格的中值 Sets the value for the cell in the table model at <code>row</code> and <code>column</code>.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display order, and not in the
     * <code>IPIMTableModel</code>'s column order. This is an important distinction because as the user rearranges the
     * columns in the table, the column at a given index in the view will change. Meanwhile the user's actions never
     * affect the model's column ordering.
     *
     * <code>aValue</code> is the new value.
     *
     * @param aValue
     *            要设置的值 the new value
     * @param row
     *            所在行 the row of the cell to be changed
     * @param column
     *            所在列 the column of the cell to be changed
     * @see #getValueAt
     */
    public void setValueAt(
            Object aValue,
            int row,
            int column) {
        // 从数据模型中得到
        getModel().setValueAt(aValue, row, convertColumnIndexToModel(column));
    }

    /**
     * 判断指定单元格是否可编辑 Returns true if the cell at <code>row</code> and <code>column</code> is editable. Otherwise,
     * invoking <code>setValueAt</code> on the cell will have no effect.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display order, and not in the
     * <code>IPIMTableModel</code>'s column order. This is an important distinction because as the user rearranges the
     * columns in the table, the column at a given index in the view will change. Meanwhile the user's actions never
     * affect the model's column ordering.
     *
     *
     * @param row
     *            指定行 the row whose value is to be queried
     * @param column
     *            指定列 the column whose value is to be queried
     * @return true if the cell is editable
     * @see #setValueAt
     */
    public boolean isCellEditable(
            int row,
            int column) { // 从数据模型中得到
        return getModel().isCellEditable(row, convertColumnIndexToModel(column));
    }

    //
    // Adding and removing columns in the view
    //

    /**
     * 在表格中加入一列 Appends <code>aColumn</code> to the end of the array of columns held by this <code>PIMTable</code>'s
     * column model. If the column name of <code>aColumn</code> is <code>null</code>, sets the column name of
     * <code>aColumn</code> to the name returned by <code>getModel().getColumnName()</code>.
     * <p>
     * To add a column to this <code>PIMTable</code> to display the <code>modelColumn</code>'th column of data in the
     * model with a given <code>width</code>, <code>cellRenderer</code>, and <code>cellEditor</code> you can use:
     * 
     * <pre>
     *
     * addColumn(new PIMTableColumn(modelColumn, width, cellRenderer, cellEditor));
     *
     * </pre>
     * 
     * [Any of the <code>PIMTableColumn</code> constructors can be used instead of this one.] The model column number is
     * stored inside the <code>PIMTableColumn</code> and is used during rendering and editing to locate the appropriates
     * data values in the model. The model column number does not change when columns are reordered in the view.
     *
     * @param aColumn
     *            一个 <code>PIMTableColumn</code> 的实例
     * @see #removeColumn
     */
    public void addColumn(
            PIMTableColumn aColumn) {
        // 没有表格头的处理
        if (aColumn.getHeaderValue() == null) {
            // 得到它在数据模型中的索引
            int modelColumn = aColumn.getModelIndex();
            // 从中取缺省的表格头对象,后设置进去
            Object columnName = getModel().getColumnTitle(modelColumn);
            aColumn.setHeaderValue(columnName);
        }
        // 在列模型中加入
        getColumnModel().addColumn(aColumn);
    }

    /**
     * 从表格中删除一列 Removes <code>aColumn</code> from this <code>PIMTable</code>'s array of columns. Note: this method does
     * not remove the column of data from the model; it just removes the <code>PIMTableColumn</code> that was
     * responsible for displaying it.
     *
     * @param aColumn
     *            一个 <code>PIMTableColumn</code> 的实例
     * @see #addColumn
     */
    public void removeColumn(
            PIMTableColumn aColumn) {
        // 在列模型中直接移除
        getColumnModel().removeColumn(aColumn);
    }

    /**
     * 将某一列从一处移动到另一列 Moves the column <code>column</code> to the position currently occupied by the column
     * <code>targetColumn</code> in the view. The old column at <code>targetColumn</code> is shifted left or right to
     * make room.
     *
     * @param column
     *            原先所在列the index of column to be moved
     * @param targetColumn
     *            目的列的索引 the new index of the column
     */
    public void moveColumn(
            int column,
            int targetColumn) {
        // 在列模型中直接操作
        getColumnModel().moveColumn(column, targetColumn);
    }

    //
    // Cover methods for various models and helper methods
    //

    /**
     * 得到点坐标所在的列号,-1表示越界 Returns the index of the column that <code>point</code> lies in, or -1 if the result is not in
     * the range [0, <code>getColumnCount()</code>-1].
     *
     * @param point
     *            the location of interest
     * @return the index of the column that <code>point</code> lies in, or -1 if the result is not in the range [0,
     *         <code>getColumnCount()</code>-1]
     * @see #rowAtPoint
     */
    public int columnAtPoint(
            Point point) {
        int x = point.x;
        // 非自左到右的情况下,反转一下
        if (!getComponentOrientation().isLeftToRight()) {
            x = getWidth() - x;
        }
        // 在列模型中直接操作
        return getColumnModel().getColumnIndexAtX(x);
    }

    /**
     * 得到点坐标所在的行号,-1表示越界 Returns the index of the row that <code>point</code> lies in, or -1 if the result is not in the
     * range [0, <code>getRowCount()</code>-1].
     *
     * @param point
     *            the location of interest
     * @return the index of the row that <code>point</code> lies in, or -1 if the result is not in the range [0,
     *         <code>getRowCount()</code>-1]
     * @see #columnAtPoint
     */
    public int rowAtPoint(
            Point point) {
        int y = point.y;
        // 行模型为空时直接用Y坐标去除缺省行高,否则从行模型中取数据
        int result = (rowModel == null) ? y / getRowHeight() : rowModel.getIndex(y);
        // 越界处理
        if (result < 0) {
            return -1;
        }
        // 越界处理
        else if (result >= getRowCount()) {
            return -1;
        }
        // 正常值
        else {
            return result;
        }
    }

    /**
     * 得到指定单元格的所在矩形, Returns a rectangle for the cell that lies at the intersection of <code>row</code> and
     * <code>column</code>. If <code>includeSpacing</code> is true then the value returned has the full height and width
     * of the row and column specified. If it is false, the returned rectangle is inset by the intercell spacing to
     * return the true bounds of the rendering or editing component as it will be set during rendering.
     * <p>
     * If the column index is valid but the row index is less than zero the method returns a rectangle with the
     * <code>y</code> and <code>height</code> values set appropriately and the <code>x</code> and <code>width</code>
     * values both set to zero. In general, when either the row or column indices indicate a cell outside the
     * appropriate range, the method returns a rectangle depicting the closest edge of the closest cell that is within
     * the table's range. When both row and column indices are out of range the returned rectangle covers the closest
     * point of the closest cell.
     * <p>
     * In all cases, calculations that use this method to calculate results along one axis will not fail because of
     * anomalies in calculations along the other axis. When the cell is not valid the <code>includeSpacing</code>
     * parameter is ignored.
     *
     * @param row
     *            the row index where the desired cell is located
     * @param column
     *            the column index where the desired cell is located in the display; this is not necessarily the same as
     *            the column index in the data model for the table; the {@link #convertColumnIndexToView(int)} method
     *            may be used to convert a data model column index to a display column index
     * @param includeSpacing
     *            本标志为假表示要减去空档 if false, return the true cell bounds - computed by subtracting the intercell spacing
     *            from the height and widths of the column and row models
     * @return the rectangle containing the cell at location <code>row</code>,<code>column</code>
     */
    public Rectangle getCellRect(
            int row,
            int column,
            boolean includeSpacing) {
        Rectangle r = new Rectangle();
        // 先声明一个有效标志
        boolean valid = true;
        // 行小于0置无效
        if (row < 0) {
            valid = false;
        }
        // 大于行数置无效,但取得行高
        else if (row >= getRowCount()) {
            r.y = getHeight();
            valid = false;
        }
        // 正常处理
        else {
            r.height = getRowHeight(row);
            // 取行高,有行模型从中取值,否则取乘积
            r.y = (rowModel == null) ? row * r.height : rowModel.getPosition(row);
        }
        // 至此我们求得了Y坐标和高度
        // 列小于0,
        if (column < 0) {
            // 如不是自左到右,自然取得宽度
            if (!getComponentOrientation().isLeftToRight()) {
                r.x = getWidth();
            }
            valid = false;
        }
        // 大于列数置无效,但取得宽度
        else if (column >= getColumnCount()) {
            if (getComponentOrientation().isLeftToRight()) {
                r.x = getWidth();
            }
            valid = false;
        } else {
            IPIMTableColumnModel cm = getColumnModel();
            // 是自左到右就去遍历列,求得X坐标
            if (getComponentOrientation().isLeftToRight()) {
                for (int i = 0; i < column; i++) {
                    r.x += cm.getColumn(i).getWidth();
                }
            }
            // 否则从右边开始
            else {
                for (int i = cm.getColumnCount() - 1; i > column; i--) {
                    r.x += cm.getColumn(i).getWidth();
                }
            }
            r.width = cm.getColumn(column).getWidth();
        }
        // 至此我们求得了X坐标和宽度
        if (valid && !includeSpacing) {
            int rm = getRowMargin();
            int cm = getColumnModel().getColumnMargin();
            // 是在边缘空白内的就作如下调整
            r.setBounds(r.x + cm / 2, r.y + rm / 2, r.width - cm, r.height - rm);
        }
        // 不知为何,表格体的X坐标总是向左偏移一个像素.先这么处理
        // 一下,看有什么问题.关键是不管怎么样这里我们不处理绘出的线在最后一列就不
        // 出现
        r.x += 1;
        if (column == getColumnCount() - 1) {
            r.width -= 1;
        }
        return r;
    }

    /**
     * 得到指定列的索引值
     * 
     * @param aColumn
     *            传入的列
     * @return 返回指定列的索引值
     */
    private int viewIndexForColumn(
            PIMTableColumn aColumn) {
        IPIMTableColumnModel cm = getColumnModel();
        // 在列中遍历,匹配相等就返回
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        // 表示没有对应的
        return -1;
    }

    /**
     * 得到正在调整中的列
     * 
     * @return 返回正在调整中的列
     */
    private PIMTableColumn getResizingColumn() {
        // 没有表格头就没有,否则从表格头中得到(因为我们中有拖动表格头中的才有效)
        return (tableHeader == null) ? null : tableHeader.getResizingColumn();
    }

    /**
     * 调整表格的列的尺寸以匹配现有空间 Sizes the table columns to fit the available space.
     * 
     * @param lastColumnOnly
     *            本标志为真表示牺牲最后一列的尺寸以匹配现有空间
     * @deprecated As of Swing version 1.0.3, replaced by <code>doLayout()</code>.
     * @see #addColumn
     */
    public void sizeColumnsToFit(
            boolean lastColumnOnly) {
        int oldAutoResizeMode = autoResizeMode;
        // 设置列宽调整方式
        setAutoResizeMode(lastColumnOnly ? AUTO_RESIZE_LAST_COLUMN : AUTO_RESIZE_ALL_COLUMNS);
        // 保证一下,再恢复一下
        sizeColumnsToFit(-1);
        setAutoResizeMode(oldAutoResizeMode);
    }

    /**
     * 调整表格的列的尺寸以匹配现有空间,废弃的一个方法,用<code>doLayout()</code>代替 Obsolete as of Java 2 platform v1.4. Please use the
     * <code>doLayout()</code> method instead.
     * 
     * @param resizingColumn
     *            the column whose resizing made this adjustment necessary or -1 if there is no such column
     * @see #doLayout
     */
    public void sizeColumnsToFit(
            int resizingColumn) {
        // 没有就设置不允许从首选宽度中得到列度
        if (resizingColumn == -1) {
            setWidthsFromPreferredWidths(false);
        } else {
            // 在宽度不可调整下
            if (autoResizeMode == AUTO_RESIZE_OFF) {
                PIMTableColumn aColumn = getColumnModel().getColumn(resizingColumn);
                // 以其宽度为首选宽度
                aColumn.setPreferredWidth(aColumn.getWidth());
            } else {
                // 否则通过一个算法来实现
                int delta = getWidth() - getColumnModel().getTotalColumnWidth();
                accommodateDelta(resizingColumn, delta);
            }
        }
    }

    /**
     * 设置由各列数首选宽度来设置各列宽度
     * 
     * @param inverse
     *            反转标志
     */
    private void setWidthsFromPreferredWidths(
            final boolean inverse) {
        // 先保存总宽度和总首选宽度
        int totalWidth = getWidth();
        int totalPreferred = getPreferredSize().width;
        // 不反转就取总宽度,否则取总首选宽度
        int target = !inverse ? totalWidth : totalPreferred;

        final IPIMTableColumnModel cm = columnModel;
        // 创建一个用于调整的类的实例
        Resizable3 r = new Resizable3_1(this, cm, inverse);
        // 进行调整
        adjustSizes(target, r, inverse);
    }

    // Distribute delta over columns, as indicated by the autoresize mode.
    /**
     * 根据不同的宽度调整方式进行尺寸调整,这是一个算法
     * 
     * @param resizingColumnIndex
     *            正在调整中的列索引
     * @param delta
     *            差量,迭代到各列中去
     */
    private void accommodateDelta(
            int resizingColumnIndex,
            int delta) {
        // 保存总列数,起始调整列,最后一列
        int tmpColCount = getColumnCount();
        int tmpFrom = resizingColumnIndex;
        int tmpTo = tmpColCount;
        // 这是JTable 的标准算法
        switch (autoResizeMode) {
            case AUTO_RESIZE_NEXT_COLUMN:
                tmpFrom = tmpFrom + 1;
                tmpTo = Math.min(tmpFrom + 1, tmpColCount);
                break;

            case AUTO_RESIZE_SUBSEQUENT_COLUMNS:
                tmpFrom = tmpFrom + 1;
                tmpTo = tmpColCount;
                break;

            case AUTO_RESIZE_LAST_COLUMN:
                tmpFrom = tmpColCount - 1;
                tmpTo = tmpFrom + 1;
                break;

            case AUTO_RESIZE_ALL_COLUMNS:
                tmpFrom = 0;
                tmpTo = tmpColCount;
                break;

            default:
                return;
        }

        final int start = tmpFrom;
        final int end = tmpTo;
        final IPIMTableColumnModel cm = columnModel;
        Resizable3 r = new Resizable3_2(this, cm, start, end);

        int totalWidth = 0;
        for (int i = tmpFrom; i < tmpTo; i++) {
            PIMTableColumn aColumn = columnModel.getColumn(i);
            int input = aColumn.getWidth();
            totalWidth = totalWidth + input;
        }

        adjustSizes(totalWidth + delta, r, false);

        setWidthsFromPreferredWidths(true);
        return;
    }

    /**
     * 调整尺寸
     * 
     * @param target
     *            目标
     * @param r
     *            三号接口型的对象
     * @param inverse
     *            是否反转
     */
    private void adjustSizes(
            long target,
            final Resizable3 r,
            boolean inverse) {
        int N = r.getElementCount();
        long totalPreferred = 0;
        for (int i = 0; i < N; i++) {
            totalPreferred += r.getMidPointAt(i);
        }
        Resizable2 s;
        if ((target < totalPreferred) == !inverse) {
            s = new Resizable2_1(this, r);
        } else {
            s = new Resizable2_2(this, r);
        }
        adjustSizes(target, s, !inverse);
    }

    /**
     * 调整尺寸
     * 
     * @param target
     *            目标
     * @param r
     *            二号接口型的对象
     * @param limitToRange
     *            是否在范围中进行限制
     */
    private void adjustSizes(
            long target,
            Resizable2 r,
            boolean limitToRange) {
        long totalLowerBound = 0;
        long totalUpperBound = 0;
        for (int i = 0; i < r.getElementCount(); i++) {
            totalLowerBound += r.getLowerBoundAt(i);
            totalUpperBound += r.getUpperBoundAt(i);
        }

        if (limitToRange) {
            target = Math.min(Math.max(totalLowerBound, target), totalUpperBound);
        }

        for (int i = 0; i < r.getElementCount(); i++) {
            int lowerBound = r.getLowerBoundAt(i);
            int upperBound = r.getUpperBoundAt(i);
            int newSize;
            if (totalLowerBound == totalUpperBound) {
                newSize = lowerBound;
            } else {
                double f = (double) (target - totalLowerBound) / (totalUpperBound - totalLowerBound);
                newSize = (int) Math.round(lowerBound + f * (upperBound - lowerBound));
            }
            r.setSizeAt(newSize, i);
            target -= newSize;
            totalLowerBound -= lowerBound;
            totalUpperBound -= upperBound;
        }
    }

    /**
     * 如果本单元格可编辑,就开始在该格编辑 Programmatically starts editing the cell at <code>prmRow</code> and <code>column</code>, if
     * the cell is editable. To prevent the <code>PIMTable</code> from editing a particular table, column or cell value,
     * return false from the <code>isCellEditable</code> method in the <code>IPIMTableModel</code> interface.
     *
     * @param prmRow
     *            the row to be edited
     * @param prmCol
     *            the column to be edited
     * @param e
     *            考进<code>shouldSelectCell</code>的事件源 event to pass into <code>shouldSelectCell</code>; note that as of
     *            Java 2 platform v1.2, the call to <code>shouldSelectCell</code> is no longer made
     * @return 为假就不能编辑
     */
    public boolean editCellAt(
            int prmRow,
            int prmCol,
            EventObject e) {
        if (cellEditor != null && !cellEditor.stopCellEditing()) {
            return false;
        } // 除错：因为改代码被执行前，可能已经调过令编辑器退出编辑状态的方法------------------------------------------------------------------------

        if (prmRow < 0 || prmRow >= getRowCount() || prmCol < 0 || prmCol >= getColumnCount()) {
            return false;
        } // 除错：如果传入的行号或列号是无效的，或时界外的。视为错误--------------------------------------------------------------------------

        int[] seleRows = getSelectedRows();
        if (seleRows.length != 1) {
            return false;
        } // 除错：如果表格当前选中行数为零或大于1,也不可进入编辑状态---------------------------------------------------------------------------------------

        if (!isCellEditable(prmRow, prmCol)) {
            return false;
        } // 如果该Cell是不许编辑的，也返回。---------------------------------------------------------------------------------------------------

        // TODOuser008:本段代码不应该在本方法中，可以移到adjustAdjustTableStatus或者stopEditing等方法中。
        // 如果本来该Cell不是已经选中的，则不进入编辑状态。
        // 加特殊处理,为任务表格的三个字段,完成,完成率,状态,应该来说;这个三个字段都要联动,先不管,只处理一个完成.
        if (getColumnModel().getSelectionModel().getAnchorSelectionIndex() != prmCol
                || getSelectionModel().getAnchorSelectionIndex() != prmRow) {
            // TODOuser008:本段代码不应该在本方法中，可以移到adjustAdjustTableStatus或者stopEditing等方法中。
            if (!(view != null && view.getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP && ((View_PIMDetails) view)
                    .getDisplayIndexes()[prmCol] == ModelDBCons.FINISH_FLAG)) {
                return false;
            }
        }

        // 至此已初步排除导致不能进入编辑状态的情况，开始准备让Cell进入编辑状态－－－－－－－－－－－－－－－－－－－－－－
        if (editorRemover == null) // 先确保移除器存在，以便于在编辑完之后，编辑器自动被移除。
        {
            KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager(); // 得到键盘焦点管理器。
            editorRemover = new CellEditorRemover(this, fm);
            fm.addPropertyChangeListener("permanentFocusOwner", editorRemover);
        }

        IPIMCellEditor editor = getCellEditor(prmRow, prmCol); // 得到该行列对应的编辑器。
        if (editor == null || !editor.isCellEditable(e)) {
            return false;
        } // 继续除错：如果编辑器为空或不可编辑，返回--------------------------------------------------

        editorComp = prepareEditor(editor, prmRow, prmCol);
        if (editorComp == null) {
            removeEditor();
            return false;
        } // 继续除错：如果编辑器中的Component为空，也返回--------------------------------------------

        // 至此方可真正放心地进入编辑状态－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－

        // 解第一次弹出时组合框没有超出边框的缺陷,2003.10.16 ----------------
        Rectangle tmpRect = getCellRect(prmRow, prmCol, false);
        if (editorComp instanceof JComboBox && prmCol != getColumnCount() - 1) {
            tmpRect.width = tmpRect.width + 18;
        }
        editorComp.setBounds(tmpRect);// --------------------------------------------
        // NOTE：如果需要处理编辑器内部的鼠标双击事件，比如鼠在编辑器内标双击时打开一条记录（见MS_OUTLOOK规格）
        // 则需要对editorComp加监听并作处理。但是不可以在这里处理，因为这是同用组件内部，不可以跟任何具体应用发生关系。
        // 应该提供公有的getEditorComp()方法，有具体应用来抓到editorComp并加具体的监听。(user008)
        add(editorComp);
        editorComp.validate();
        setCellEditor(editor);

        // 此处是JDK1.4.1的一个BUG,在鼠标单击选中单元格后,进行键盘输入,此时没有光标,解之,2003.10.15 ---------------------
        if (editorComp instanceof JTextField) {
            JTextField textfield = (JTextField) editorComp;
            textfield.getCaret().setVisible(true);
            textfield.setCaretColor(Color.black);
        } // ----------------------------------------------------------------------------------------------------------------

        setEditingRow(prmRow);
        setEditingColumn(prmCol);
        editor.addCellEditorListener(this);

        // 如果当前时任务应用，对任务中的完成复选框做特别处理---------------------------------
        if (view != null && view.getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP) {
            dealwithFinish(editorComp);
        } // -----------------------------------------------------------

        return true;
    }

    /**
     * 返回真表示一个单元格正在被编辑 Returns true if a cell is being edited.
     *
     * @return true if the table is editing a cell
     * @see #editingColumn
     * @see #editingRow
     */
    public boolean isEditing() {
        return (cellEditor == null) ? false : true;
    }

    /**
     * 得到正在处理编辑会话的组件,为空表示没有谁在被编辑 Returns the component that is handling the editing session. If nothing is being edited,
     * returns null.
     *
     * @return Component handling editing session
     */
    public Component getEditorComponent() {
        return editorComp;
    }

    /**
     * 返回当前编辑单元格所在列,没有为-1 Returns the index of the column that contains the cell currently being edited. If nothing is
     * being edited, returns -1.
     *
     * @return the index of the column that contains the cell currently being edited; returns -1 if nothing being edited
     * @see #editingRow
     */
    public int getEditingColumn() {
        return editingColumn;
    }

    /**
     * 返回当前编辑单元格所在行,没有为-1 Returns the index of the row that contains the cell currently being edited. If nothing is
     * being edited, returns -1.
     *
     * @return the index of the row that contains the cell currently being edited; returns -1 if nothing being edited
     * @see #editingColumn
     */
    public int getEditingRow() {
        return editingRow;
    }

    /**
     * 更新子组件UI的方法,主要是要得到一个 JComponent 才可能进行
     * 
     * @param componentShell
     *            组件的外壳
     */
    private void updateSubComponentUI(
            Object componentShell) {
        if (componentShell == null) {
            return;
        }
        // 先声明一个组件引用;
        Component component = null;
        // 是一个 Component,或是一个编辑器组件 就先造型一下
        if (componentShell instanceof Component) {
            component = (Component) componentShell;
        }
        if (componentShell instanceof DefaultPIMCellEditor) {
            component = ((DefaultPIMCellEditor) componentShell).getComponent();
        }
        // 不为空且是一个 JComponent 才进行
        if (component != null && component instanceof JComponent) {
            ((JComponent) component).updateUI();
        }
    }

    final int ALLOWDRAG = 1;
    final int ALLOWMULSELE = 0;
    int allowMouseMultSeleOrDragEveFlag = ALLOWMULSELE;

    // ===================================================================================================================================

    /*
     * Invoked when rows have been inserted into the table. <p> Application code will not use these methods explicitly,
     * they are used internally by PIMTable.
     * @param e the PIMTableModelEvent encapsulating the insertion
     */
    /**
     * 表格模型中行插入的方法
     * 
     * @param e
     *            表格模型事件源
     */
    private void tableRowsInserted(
            PIMTableModelEvent e) {
        // 得到起始行和结束行
        int start = e.getFirstRow();
        int end = e.getLastRow();
        // 确保一下
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = getRowCount() - 1;
        }
        // 确保结束

        int length = end - start + 1;
        // 在选择模型中插入
        selectionModel.insertIndexInterval(start, length, true);

        // 在行模型中插入
        if (rowModel != null) {
            rowModel.insertEntries(start, length, getRowHeight());
        }
        int rh = getRowHeight();
        // 得到要重绘区域
        Rectangle drawRect =
                new Rectangle(0, start * rh, getColumnModel().getTotalColumnWidth(), (getRowCount() - start) * rh);
        // 重绘
        revalidate();
        repaint(drawRect);
    }

    /*
     * Invoked when rows have been removed from the table. <p> Application code will not use these methods explicitly,
     * they are used internally by PIMTable.
     * @param e the PIMTableModelEvent encapsulating the deletion
     */
    /**
     * 表格行删除的方法
     * 
     * @param e
     *            表格模型事件源
     */
    private void tableRowsDeleted(
            PIMTableModelEvent e) {
        // 得到起始行和结束行
        int start = e.getFirstRow();
        int end = e.getLastRow();
        // 确保一下数据正确
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = getRowCount() - 1;
        }
        // 得到删除前,删除后的行数,
        int deletedCount = end - start + 1;
        int previousRowCount = getRowCount() + deletedCount;
        // 在选择模型中删除
        selectionModel.removeIndexInterval(start, end);
        // 在行模型中删除
        if (rowModel != null) {
            rowModel.removeEntries(start, deletedCount);
        }

        int rh = getRowHeight();
        // 得到要重绘区域
        Rectangle drawRect =
                new Rectangle(0, start * rh, getColumnModel().getTotalColumnWidth(), (previousRowCount - start) * rh);
        // 重绘
        revalidate();
        repaint(drawRect);
    }

    /**
     * 取极小值,主要目的也是为了保证数据正确
     * 
     * @param i
     *            第一操作数
     * @param a
     *            第二操作数
     * @param b
     *            第三操作数
     * @return 通过一个算法得到极小值
     */
    private int limit(
            int i,
            int a,
            int b) {
        return Math.min(b, Math.max(i, a));
    }

    /**
     * 得到编辑后的数据
     * 
     * @return 返回改变后的数据容器
     */
    public Vector getEditingRowRecords() {
        return editingRowRecords;
    }

    /**
     * 得到该键盘事件是否为编辑的快捷键
     * 
     * @param event
     *            键盘事件源
     * @return 判断该键盘事件是否为编辑的快捷键
     */
    public boolean isDirectionKey(
            KeyEvent event) {
        int inputKeyCode = event.getKeyCode();
        // 就那么几个键,都在下边,
        if (inputKeyCode == KeyEvent.VK_LEFT || inputKeyCode == KeyEvent.VK_RIGHT || inputKeyCode == KeyEvent.VK_UP
                || inputKeyCode == KeyEvent.VK_DOWN || inputKeyCode == KeyEvent.VK_PAGE_DOWN
                || inputKeyCode == KeyEvent.VK_PAGE_UP || inputKeyCode == KeyEvent.VK_HOME
                || inputKeyCode == KeyEvent.VK_END) {
            return true;
        }
        return false;
    }

    /**
     * 重新派发事件
     * 
     * @param e
     *            键盘事件源
     * @param editorComponent
     *            一个组件(是编辑器)
     * @return 返回派发成功的标志
     */
    private boolean repostEvent(
            KeyEvent e,
            Component editorComponent) {
        // 得到编辑器组件上的最顶层容器
        Component dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, 2, 2);
        if (dispatchComponent == null) {
            setRepostEventFlag(false);
            return false;
        }
        // 如它在屏幕上就把事件传递给它
        if (dispatchComponent.isShowing()) {
            setRepostEventFlag(true);
            ((JComponent) dispatchComponent).dispatchEvent(e);
        } else {
            setRepostEventFlag(false);
        }
        return true;
    }

    /**
     * 设置惰性值
     * 
     * @param h
     *            一个哈希表
     * @param c
     *            类的类型
     * @param s
     *            标识字符串
     */
    private void setLazyValue(
            Hashtable h,
            Class c,
            String s) {
        // h.put(c, new UIDefaults.ProxyLazyValue(s));
        try {
            h.put(c, Class.forName(s).newInstance());
        } catch (Exception e) {
        }
    }

    /**
     * 设置惰性绘制器
     * 
     * @param c
     *            类的类型
     * @param s
     *            标识字符串
     */
    private void setLazyRenderer(
            Class c,
            String s) {
        setLazyValue(defaultRenderersByColumnClass, c, s);
    }

    /**
     * 创建缺省的绘制器们,把本类的几个绘制器放入哈希表中 Creates default cell renderers for objects, numbers, doubles, dates, booleans, and
     * icons.
     * 
     * @see a.swing.pimtable.DefaultPIMTableCellRenderer
     */
    protected void createDefaultRenderers() {
        defaultRenderersByColumnClass = new UIDefaults();

        // Objects
        setLazyRenderer(Object.class, "org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer");

        // Numbers
        setLazyRenderer(Number.class, "org.cas.client.platform.pimview.pimtable.PIMTable$NumberRenderer");

        // Doubles and Floats
        setLazyRenderer(Float.class, "org.cas.client.platform.pimview.pimtable.PIMTable$DoubleRenderer");
        setLazyRenderer(Double.class, "org.cas.client.platform.pimview.pimtable.PIMTable$DoubleRenderer");

        // Dates
        setLazyRenderer(Date.class, "org.cas.client.platform.pimview.pimtable.PIMTable$DateRenderer");

        // Icons and ImageIcons
        setLazyRenderer(Icon.class, "org.cas.client.platform.pimview.pimtable.PIMTable$IconRenderer");
        setLazyRenderer(ImageIcon.class, "org.cas.client.platform.pimview.pimtable.PIMTable$IconRenderer");

        // Booleans
        setLazyRenderer(Boolean.class, "org.cas.client.platform.pimview.pimtable.PIMTable$BooleanRenderer");
    }

    /**
     * 设置惰性编辑器
     * 
     * @param c
     *            类的类型
     * @param s
     *            标识字符串
     */
    private void setLazyEditor(
            Class c,
            String s) {
        // 设置惰性值
        setLazyValue(defaultEditorsByColumnClass, c, s);
    }

    /**
     * 创建缺省的编辑器们 Creates default cell editors for objects, numbers, and boolean values.
     * 
     * @see DefaultPIMCellEditor
     */
    protected void createDefaultEditors() {
        // 一个哈希表
        defaultEditorsByColumnClass = new UIDefaults();

        // Objects(原来是这么设的)
        setLazyEditor(Object.class, "org.cas.client.platform.pimview.pimtable.PIMTable$GenericEditor");

        // Numbers
        setLazyEditor(Number.class, "org.cas.client.platform.pimview.pimtable.PIMTable$NumberEditor");

        // Booleans
        setLazyEditor(Boolean.class, "org.cas.client.platform.pimview.pimtable.PIMTable$BooleanEditor");
    }

    /**
     * 创建缺省的列模型 Returns the default column model object, which is a <code>DefaultPIMTableColumnModel</code>. A subclass
     * can override this method to return a different column model object.
     *
     * @return the default column model object
     * @see a.swing.pimtable.DefaultPIMTableColumnModel
     */
    protected IPIMTableColumnModel createDefaultColumnModel() {
        return new DefaultPIMTableColumnModel(hasEditor);
    }

    /**
     * 创建缺省的列选择模型 Returns the default selection model object, which is a <code>DefaultListSelectionModel</code>. A
     * subclass can override this method to return a different selection model object.
     *
     * @return 返回缺省的列选择模型
     * @see javax.swing.DefaultListSelectionModel
     */
    protected ListSelectionModel createDefaultSelectionModel() {
        return new DefaultListSelectionModel();
    }

    /**
     * 创建缺省的表格头部 Returns the default table header object, which is a <code>PIMTableHeader</code>. A subclass can
     * override this method to return a different table header object.
     *
     * @return 返回缺省的表格头部对象
     * @see a.swing.pimtable.PIMTableHeader
     */
    protected PIMTableHeader createDefaultTableHeader() {
        return new PIMTableHeader(columnModel);
    }

    /**
     * 重新调整各列尺寸并重绘的方法 Equivalent to <code>revalidate</code> followed by <code>repaint</code>.
     */
    protected void resizeAndRepaint() {
        revalidate();
        repaint();
    }

    /**
     * 准备该绘制器,得到绘制器组件,主体是准备各个参数 Prepares the renderer by querying the data model for the value and selection state of
     * the cell at <code>row</code>, <code>column</code>. Returns the component (may be a <code>Component</code> or a
     * <code>JComponent</code>) under the event location.
     * <p>
     * <b>Note:</b> Throughout the table package, the internal implementations always use this method to prepare
     * renderers so that this default behavior can be safely overridden by a subclass.
     *
     * @param prmRenderer
     *            the <code>IPIMTableCellRenderer</code> to prepare
     * @param prmRow
     *            the row of the cell to render, where 0 is the first row
     * @param prmColumn
     *            the column of the cell to render, where 0 is the first column
     * @return the <code>Component</code> under the event location
     */
    public Component prepareRenderer(
            IPIMCellRenderer prmRenderer,
            int prmRow,
            int prmColumn) {
        Object tmpValue = getValueAt(prmRow, prmColumn); // 得到数据模型中的值

        boolean isSelected = isCellSelected(prmRow, prmColumn); // 选中和锚定这个一格
        boolean rowIsAnchor = (selectionModel.getAnchorSelectionIndex() == prmRow); // 从选择模型和列选择模型中得到些信息,被锚定才有焦点
        boolean colIsAnchor = (columnModel.getSelectionModel().getAnchorSelectionIndex() == prmColumn);

        boolean hasFocus = (rowIsAnchor && colIsAnchor) && isFocusOwner(); // 准备焦点
        return prmRenderer.getTableCellRendererComponent(this, tmpValue, isSelected, hasFocus, prmRow, prmColumn);
        // 下面这些注释掉的先保留
        // if(getView() == null || !(renderer instanceof DefaultPIMTableCellRenderer))
        // {
        // return renderer.getTableCellRendererComponent(this, value,
        // isSelected, hasFocus,row, column);
        // }
        // else
        // {
        // DefaultPIMTableCellRenderer tmpRenderer = (DefaultPIMTableCellRenderer)renderer;
        // ViewFormat tmpViewFormat = view.getApplication().getActiveViewInfo().getViewFormat();
        // tmpRenderer.setFont(new Font(tmpViewFormat.getFontName(),
        // tmpViewFormat.getFontStyle(),tmpViewFormat.getFontSize()));
        // return renderer.getTableCellRendererComponent(this, value,
        // isSelected, hasFocus,row, column);
        // }
    }

    /**
     * 得到指定单元格的编辑器 Returns an appropriate editor for the cell specified by <code>row</code> and <code>column</code>. If
     * the <code>PIMTableColumn</code> for this column has a non-null editor, returns that. If not, finds the class of
     * the data in this column (using <code>getColumnClass</code>) and returns the default editor for this type of data.
     * <p>
     * <b>Note:</b> Throughout the table package, the internal implementations always use this method to provide editors
     * so that this default behavior can be safely overridden by a subclass.
     *
     * @param row
     *            the row of the cell to edit, where 0 is the first row
     * @param column
     *            the column of the cell to edit, where 0 is the first column
     * @return the editor for this cell; if <code>null</code> return the default editor for this type of cell
     * @see DefaultPIMCellEditor
     */
    public IPIMCellEditor getCellEditor(
            int row,
            int column) {
        // 从列号得到列,和绘制器
        PIMTableColumn tableColumn = getColumnModel().getColumn(column);
        IPIMCellEditor editor = tableColumn.getCellEditor();
        // 去池中取它的编辑器
        if (editor == null) {
            editor = getDefaultEditor(getColumnClass(column));
        }
        return editor;
    }

    /**
     * 准备该编辑器,得到该编辑器组件 Prepares the editor by querying the data model for the value and selection state of the cell at
     * <code>row</code>, <code>column</code>.
     * <p>
     * <b>Note:</b> Throughout the table package, the internal implementations always use this method to prepare editors
     * so that this default behavior can be safely overridden by a subclass.
     *
     * @param editor
     *            the <code>IPIMTableCellEditor</code> to set up
     * @param row
     *            the row of the cell to edit, where 0 is the first row
     * @param column
     *            the column of the cell to edit, where 0 is the first column
     * @return the <code>Component</code> being edited
     */
    public Component prepareEditor(
            IPIMCellEditor editor,
            int row,
            int column) {
        // 得到数据模型中的值
        Object tmpValue = getValueAt(row, column);
        // 选中和锚定这个一格
        boolean isSelected = isCellSelected(row, column);
        // 得到编辑器组件
        Component comp = editor.getTableCellEditorComponent(this, tmpValue, isSelected, row, column);
        // 是一个 JComponent 才进行

        if (comp instanceof JComponent) {
            JComponent jComp = (JComponent) comp;
            // 使自己的焦点得到更好的保护
            if (jComp.getNextFocusableComponent() == null) {
                jComp.setNextFocusableComponent(this);
            }
            jComp.addMouseListener(this);
        }
        return comp;
    }

    /**
     * 丢弃编辑器,以用于单元格绘制 Discards the editor object and frees the real estate it used for cell rendering.
     */
    public void removeEditor() {
        // 得到键盘
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner",
                editorRemover);
        editorRemover = null;

        if (cellEditor != null) {
            cellEditor.removeCellEditorListener(this);

            if (editorComp != null) {
                editorComp.removeMouseListener(this);
                remove(editorComp);
                editorComp = null;
            }

            Rectangle tmpCellRect = getCellRect(editingRow, editingColumn, false);
            // 复位CellEditor,EditingCol,EditingRow---------------
            setCellEditor(null);
            setEditingColumn(-1);
            setEditingRow(-1);
            // 重绘变动区域---------------
            repaint(tmpCellRect);
        }
    }

    /**
     * 本方法是用于序列化操作的,用与向流中写数据 See readObject() and writeObject() in JComponent for more information about serialization
     * in Swing.
     * 
     * @param s
     *            用于写入的流
     * @throws IOException
     *             本方法可能抛出IO异常
     */
    private void writeObject(
            ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            // byte count = JComponent.getWriteObjCounter (this);
            byte count = 0;
            // JComponent.setWriteObjCounter (this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }

    /**
     * 本方法是用于序列化操作的,用与从流中读数据
     * 
     * @param s
     *            用于读取的流
     * @throws IOException
     *             本方法可能抛出IO异常
     * @throws ClassNotFoundException
     *             本方法可能抛出类找不到异常
     */
    private void readObject(
            ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        if ((ui != null) && (getUIClassID().equals(uiClassID))) {
            ui.installUI(this);
        }
        createDefaultRenderers();
        createDefaultEditors();

        if (getToolTipText() == null) {
            ToolTipManager.sharedInstance().registerComponent(this);
        }
    }

    /**
     * 得到某行的记录
     * 
     * @param prmRow
     *            行号
     * @return 返回该行的记录
     */
    public int getRecordIDAt(
            int prmRow) {
        return 0;
    }

    /**
     * 对完成字段的特殊处理 得到"完成"这一字段的所在列
     * 
     * @return "完成"这一字段的所在列
     */
    int finishColumn() {
        int[] displayIndexes = ((View_PIMDetails) view).getDisplayIndexes();
        boolean tmpFindFinishField = false;
        int tmpFinishField = -1;

        // 遍历检查所有索引中有无"完成"这个字段
        for (int i = 0; i < displayIndexes.length; i++) {
            if (displayIndexes[i] == ModelDBCons.FINISH_FLAG) {
                tmpFinishField = i;
                tmpFindFinishField = true;
                break;
            }
        }

        // 没找到不管
        if (!tmpFindFinishField) {
            return -1;
        } else {
            return tmpFinishField;
        }
    }

    /**
     * 本方法用于给任务的三联动字段定位用的
     * 
     * @param prmDBInex
     *            所在索引
     * @return 完成字段
     */
    int specialColumn(
            int prmDBInex) {
        int[] displayIndexes = ((View_PIMDetails) view).getDisplayIndexes();
        boolean tmpFindFinishField = false;
        int tmpFinishField = -1;

        // 遍历检查所有索引中有无"完成"这个字段
        for (int i = 0; i < displayIndexes.length; i++) {
            if (displayIndexes[i] == prmDBInex) // ModelDBConstants.STATUS)
            {
                tmpFinishField = i;
                tmpFindFinishField = true;
                break;
            }
        }

        // 没找到不管
        if (!tmpFindFinishField) {
            return -1;
        } else {
            return tmpFinishField;
        }
    }

    /**
     * 给任务中的完成单选按钮加监听器
     * 
     * @param comp
     *            "完成"的编辑器
     */
    private void dealwithFinish(
            Component comp) {
        int tmpFinishField = finishColumn();
        if (tmpFinishField == -1) {
            return;
        }
        if (tmpFinishField == getEditingColumn()) {
            PIMCheckBoxEditor prmCheckBoxEditor = (PIMCheckBoxEditor) comp;
            prmCheckBoxEditor.removeChangeListener(this);
            prmCheckBoxEditor.addChangeListener(this);
            return;
        }
        // 以上一段是处理完成这一字段,给它加监听器

        // 下面要处理"状态"字段
        tmpFinishField = specialColumn(ModelDBCons.STATUS);
        if (tmpFinishField == -1) {
            return;
        }
        if (tmpFinishField == getEditingColumn()) {
            SexComboBox prmComboBoxEditor = (SexComboBox) comp;
            prmComboBoxEditor.removeItemListener(this);
            prmComboBoxEditor.addItemListener(this);
            return;
        }

        // 下面要处理"完成率"字段
        tmpFinishField = specialColumn(ModelDBCons.COMPLETED);
        if (tmpFinishField == -1) {
            return;
        }
        // if(tmpFinishField == getEditingColumn())
        // {
        // FinishScaleEditor prmComboBoxEditor = (FinishScaleEditor)comp;
        // prmComboBoxEditor.removeKeyListener(this);
        // prmComboBoxEditor.addKeyListener(this);
        // return ;
        // }
    }

    /**
     * 这个方法是专门给任务中的关联改动值用的
     * 
     * @param prmLocationCol
     *            所在列
     * @param prmValue
     *            新数据
     */
    private void putRelationValue(
            int prmLocationCol,
            Object prmValue) {
        if (view != null) {
            if (editingRowRecords == null) {
                editingRowRecords = new Vector(getColumnCount());
                for (int i = 0; i < getColumnCount(); i++) {
                    editingRowRecords.add(i, null);
                }
            }
            // 这个防空处理和字段的顺序有关
            if (prmLocationCol < editingRowRecords.size()) {
                editingRowRecords.removeElementAt(prmLocationCol);
                editingRowRecords.add(prmLocationCol, prmValue);
            } else {
                editingRowRecords.setSize(getColumnCount());
                editingRowRecords.removeElementAt(prmLocationCol);
                editingRowRecords.add(prmLocationCol, prmValue);
            }
        }
    }

    /**
     * 弹出右键菜单 给编辑器组件用的
     * 
     * @param e
     *            鼠标事件源
     */
    void processRightMenu(
            MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && view != null && view.getApplication() != null) {
            Component comp = getEditorComponent();
            // 以下这个判断表示是在表格头中监听到这个事件,此时comp为空
            if (comp == null) {
                return;
            }
            Point tmpPoint = SwingUtilities.convertPoint(comp, e.getPoint(), this);
            view.getApplication().showPopupMenu(this, (int) tmpPoint.getX(), (int) tmpPoint.getY());
        }
    }

    // 以下为Setters &
    // Getters========================================================================================================================================================
    /**
     * 得到与外界通信的句柄
     * 
     * @return 返回与外界通信的句柄
     */
    public IView getView() {
        return view;
    }

    /**
     * 设置视图接口
     * 
     * @param prmView
     *            视图接口
     */
    public void setView(
            IView prmView) {
        view = prmView;
    }//

    /**
     * 和表格选中相关,以后要去掉
     * 
     * @param prmToggleRow
     *            锁定操作行
     */
    public void setToggleRow(
            int prmToggleRow) {
        toggleRow = prmToggleRow;
    }

    /**
     * 和表格选中相关,以后要去掉
     * 
     * @return 锁定操作行
     */
    public int getToggleRow() {
        return toggleRow;
    }

    /**
     * called by UI,和表格选中相关,以后要去掉
     * 
     * @param row
     *            设置行
     */
    public void setLazySelectedRow(
            int row) {
        lazySelectedRow = row;
    }

    /**
     * called by PIMTableView,和表格选中相关,以后要去掉
     * 
     * @return 设置的行
     */
    public int getLazySelectedRow() {
        int tmp = lazySelectedRow;
        lazySelectedRow = -1;
        return tmp;
    }

    /**
     * 　判断本表格是否有快速编辑栏
     * 
     * @return 返回有快速编辑栏的标志
     */
    public boolean hasEditor() {
        return hasEditor;
    }

    /**
     * 　设置表格的带有快速编辑栏的开关
     * 
     * @param b
     *            是否有有快速编辑栏的开关
     */
    public void setHasEditor(
            boolean prmHasEditor) {
        hasEditor = prmHasEditor;
    }

    /**
     * 得到本表格的类型
     * 
     * @return 返回本表格的类型
     */
    public int getType() {
        return type;
    }

    /**
     * 设置本表格的类型
     * 
     * @param type
     *            本表格的类型
     */
    public void setType(
            int type) {
        this.type = type;
    }

    /**
     * 得到快速提示
     * 
     * @param type
     *            访问参数
     * @return 表示提示内容的字符串
     */
    public String getQuickTip(
            int type) {
        // 这几个类型是用于本组不同地方用到的表格
        switch (type) {
        // 导航面板倒数第三项
            case 0:
                return "单击此处添加新任务";
                // 导航面板倒数第四项

            case 1:
                return "单击此处添加新联系人";
                // 导航面板倒数第五项

            case 2:
                return "单击此处添加新日记";
                // 缺省没有

            default:
                return CASUtility.EMPTYSTR;
        }
    }

    /**
     * 设置表格头部,传入空值也有效 Sets the <code>tableHeader</code> working with this <code>PIMTable</code> to
     * <code>newHeader</code>. It is legal to have a <code>null</code> <code>tableHeader</code>.
     *
     * @param tableHeader
     *            传入的表格头
     * @see #getTableHeader
     * @beaninfo bound: true description: The PIMTableHeader instance which renders the column headers.
     */
    public void setTableHeader(
            PIMTableHeader tableHeader) {
        // 优化算法
        if (this.tableHeader != tableHeader) {
            PIMTableHeader old = this.tableHeader;
            // 先断掉表格头与表格的关联
            if (old != null) {
                old.setTable(null);
            }
            this.tableHeader = tableHeader;
            // 建立表格头与表格的关联
            if (tableHeader != null) {
                tableHeader.setTable(this);
            }
            // 最后激发一下表格头变化事件
            firePropertyChange("tableHeader", old, tableHeader);
        }
    }

    /**
     * 得到表格头部 Returns the <code>tableHeader</code> used by this <code>PIMTable</code>.
     *
     * @return the <code>tableHeader</code> used by this table
     * @see #setTableHeader
     */
    public PIMTableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * 设置行高 Sets the height, in pixels, of all cells to <code>rowHeight</code>, revalidates, and repaints. The height of
     * the cells will be equal to the row height minus the row margin.
     *
     * @param rowHeight
     *            new row height
     * @see #setRowHeight
     * @beaninfo bound: true description: The height of the specified row.
     */
    public void setRowHeight(
            int rowHeight) {
        // 出错处理
        if (rowHeight <= 0) {
            throw new IllegalArgumentException("New row height less than 1");
        }
        // 保存旧的,设置新的,先清空行尺寸管理器
        int old = this.rowHeight;
        this.rowHeight = rowHeight;
        rowModel = null;
        // 重新调整一下尺寸并重绘制,最后激发行高变化事件
        resizeAndRepaint();
        firePropertyChange("rowHeight", old, rowHeight);
    }

    /**
     * 得到行高,从默认值中取值 Returns the height of a table row, in pixels. The default row height is 16.0.
     *
     * @return 代表行高的屏幕像素数
     * @see #setRowHeight
     */
    public int getRowHeight() {
        return rowHeight;
    }

    /**
     * 得到行模型(用于行高的调整)
     * 
     * @return 返回得到调整的尺寸次序
     */
    private SizeSequence getRowModel() {
        // 没有才造个新的
        if (rowModel == null) {
            rowModel = new SizeSequence(getRowCount(), getRowHeight());
        }
        return rowModel;
    }

    /**
     * 设置某行的行高 Sets the height for <code>row</code> to <code>rowHeight</code>, revalidates, and repaints. The height of
     * the cells in this row will be equal to the row height minus the row margin.
     *
     * @param row
     *            the row whose height is being changed
     * @param rowHeight
     *            new row height, in pixels the height, in pixels, of the cells in the row
     */
    public void setRowHeight(
            int row,
            int rowHeight) {
        // 小于0等于0抛异常
        if (rowHeight <= 0) {
            throw new IllegalArgumentException("New row height less than 1");
        }
        // 在行尺寸管理器中设置一下
        getRowModel().setSize(row, rowHeight);
        // 重新调整一下并重绘
        resizeAndRepaint();
    }

    /**
     * 得到某行的行高 Returns the height, in pixels, of the cells in <code>row</code>.
     * 
     * @param row
     *            传入的行索引 the row whose height is to be returned
     * @return the height, in pixels, of the cells in the row
     */
    public int getRowHeight(
            int row) {
        // 没有行高管理器取默认值,否则从行高管理器中取值
        return (rowModel == null) ? getRowHeight() : rowModel.getSize(row);
    }

    /**
     * 设置行边缘空白 Sets the amount of empty space between cells in adjacent rows.
     *
     * @param rowMargin
     *            单元格之间的像素数
     * @see #getRowMargin
     * @beaninfo bound: true description: The amount of space between cells.
     */
    public void setRowMargin(
            int rowMargin) {
        // 保存旧的,设置新的,再重新调整一下尺寸并重绘制,
        int old = this.rowMargin;
        this.rowMargin = rowMargin;
        resizeAndRepaint();
        // 最后激发行边缘空白变化事件
        firePropertyChange("rowMargin", old, rowMargin);
    }

    /**
     * 行边缘空白 Gets the amount of empty space, in pixels, between cells. Equivalent to:
     * <code>getIntercellSpacing().height</code>.
     * 
     * @return 得到单元格之间的像素数 the number of pixels between cells in a row
     * @see #setRowMargin
     */
    public int getRowMargin() {
        return rowMargin;
    }

    /**
     * 设置单元格之间的面积 作用是同时设置行边缘空白和列边缘空白 Sets the <code>rowMargin</code> and the <code>columnMargin</code> -- the height and
     * width of the space between cells -- to <code>intercellSpacing</code>.
     *
     * @param intercellSpacing
     *            用面积代表的
     * @see #getIntercellSpacing
     * @beaninfo description: The spacing between the cells, drawn in the background color of the PIMTable.
     */
    public void setIntercellSpacing(
            Dimension intercellSpacing) {
        // 设置行边缘空白,
        setRowMargin(intercellSpacing.height);
        // 在列模型中设置列的边缘空白
        getColumnModel().setColumnMargin(intercellSpacing.width);
        // 重新调整一下尺寸并重绘制,
        resizeAndRepaint();
    }

    /**
     * 返回单元格之间的水平和垂直间距 Returns the horizontal and vertical space between cells. The default spacing is (1, 1), which
     * provides room to draw the grid.
     *
     * @return the horizontal and vertical spacing between cells
     * @see #setIntercellSpacing
     */
    public Dimension getIntercellSpacing() {
        // 数据来源于列模型中列边缘空白,和表格所保存的行边缘空白
        return new Dimension(getColumnModel().getColumnMargin(), rowMargin);
    }

    /**
     * 设置网格线颜色 Sets the color used to draw grid lines to <code>gridColor</code> and redisplays. The default color is
     * look and feel dependent.
     *
     * @param gridColor
     *            the new color of the grid lines
     * @see #setRowMargin
     * @beaninfo bound: true description: The grid color.
     */
    public void setGridColor(
            Color gridColor) {
        // 出错处理
        if (gridColor == null) {
            throw new IllegalArgumentException("New color is null");
        }
        // 保存旧的,设置新的
        Color old = this.gridColor;
        this.gridColor = gridColor;
        // 激发网格线颜色变化事件,最后重绘
        firePropertyChange("gridColor", old, gridColor);
        repaint();
    }

    /**
     * 得到绘制网格线的颜色 Returns the color used to draw grid lines. The default color is look and feel dependent.
     *
     * @return the color used to draw grid lines
     * @see #setGridColor
     */
    public Color getGridColor() {
        return gridColor;
    }

    /**
     * 设置网格线是否显示 Sets whether the table draws grid lines around cells. If <code>showGrid</code> is true it does; if it
     * is false it doesn't. There is no <code>getShowGrid</code> method as this state is held in two variables --
     * <code>showHorizontalLines</code> and <code>showVerticalLines</code> -- each of which can be queried
     * independently.
     *
     * @param showGrid
     *            true if table view should draw grid lines
     * @see #setShowVerticalLines
     * @see #setShowHorizontalLines
     * @beaninfo description: The color used to draw the grid lines.
     */
    public void setShowGrid(
            boolean showGrid) {
        // 水平线和垂直线都设置一下并重绘
        setShowHorizontalLines(showGrid);
        setShowVerticalLines(showGrid);

        repaint();
    }

    /**
     * 设置是否显示水平线 Sets whether the table draws horizontal lines between cells. If <code>showHorizontalLines</code> is
     * true it does; if it is false it doesn't.
     *
     * @param showHorizontalLines
     *            是否显示水平线的开关
     * @see #getShowHorizontalLines
     * @see #setShowGrid
     * @see #setShowVerticalLines
     * @beaninfo bound: true description: Whether horizontal lines should be drawn in between the cells.
     */
    public void setShowHorizontalLines(
            boolean showHorizontalLines) {
        // 保存旧的,设置新的
        boolean old = this.showHorizontalLines;
        this.showHorizontalLines = showHorizontalLines;
        // 激发显示水平网格线事件,最后重绘
        firePropertyChange("showHorizontalLines", old, showHorizontalLines);
        repaint();
    }

    /**
     * 判断设置是否显示水平线 Returns true if the table draws horizontal lines between cells, false if it doesn't. The default is
     * true.
     *
     * @return true if the table draws horizontal lines between cells, false if it doesn't
     * @see #setShowHorizontalLines
     */
    public boolean getShowHorizontalLines() {
        return showHorizontalLines;
    }

    /**
     * 设置是否显示垂直线 Sets whether the table draws vertical lines between cells. If <code>showVerticalLines</code> is true it
     * does; if it is false it doesn't.
     *
     * @param showVerticalLines
     *            是否显示垂直线的开关
     * @see #getShowVerticalLines
     * @see #setShowGrid
     * @see #setShowHorizontalLines
     * @beaninfo bound: true description: Whether vertical lines should be drawn in between the cells.
     */
    public void setShowVerticalLines(
            boolean showVerticalLines) {
        // 保存旧的,设置新的
        boolean old = this.showVerticalLines;
        this.showVerticalLines = showVerticalLines;
        // 激发显示垂直网格线事件,最后重绘
        firePropertyChange("showVerticalLines", old, showVerticalLines);

        repaint();
    }

    /**
     * 判断是否显示垂直线 Returns true if the table draws vertical lines between cells, false if it doesn't. The default is true.
     *
     * @return true if the table draws vertical lines between cells, false if it doesn't
     * @see #setShowVerticalLines
     */
    public boolean getShowVerticalLines() {
        return showVerticalLines;
    }

    /**
     * 设置表格宽度调整的方式 Sets the table's auto resize mode when the table is resized.
     *
     * @param mode
     *            One of 5 legal values: AUTO_RESIZE_OFF, AUTO_RESIZE_NEXT_COLUMN, AUTO_RESIZE_SUBSEQUENT_COLUMNS,
     *            AUTO_RESIZE_LAST_COLUMN, AUTO_RESIZE_ALL_COLUMNS
     * @see #getAutoResizeMode
     * @see #doLayout
     * @beaninfo bound: true description: Whether the columns should adjust themselves automatically. enum:
     *           AUTO_RESIZE_OFF PIMTable.AUTO_RESIZE_OFF AUTO_RESIZE_NEXT_COLUMN PIMTable.AUTO_RESIZE_NEXT_COLUMN
     *           AUTO_RESIZE_SUBSEQUENT_COLUMNS PIMTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS AUTO_RESIZE_LAST_COLUMN
     *           PIMTable.AUTO_RESIZE_LAST_COLUMN AUTO_RESIZE_ALL_COLUMNS PIMTable.AUTO_RESIZE_ALL_COLUMNS
     */
    public void setAutoResizeMode(
            int mode) {
        // 是这五种调整模式之一的话
        if ((mode == AUTO_RESIZE_OFF) || (mode == AUTO_RESIZE_NEXT_COLUMN) || (mode == AUTO_RESIZE_SUBSEQUENT_COLUMNS)
                || (mode == AUTO_RESIZE_LAST_COLUMN) || (mode == AUTO_RESIZE_ALL_COLUMNS)) {
            // 保存旧的,设置新的
            int old = autoResizeMode;
            autoResizeMode = mode;
            // 调整一下尺寸后重绘
            resizeAndRepaint();
            // 表格头也作相应处理
            if (tableHeader != null) {
                tableHeader.resizeAndRepaint();
            }
            // 激发显示垂直网格线事件
            firePropertyChange("autoResizeMode", old, autoResizeMode);
        }
    }

    /**
     * 得到表格宽度调整的方式 Returns the auto resize mode of the table. The default mode is AUTO_RESIZE_SUBSEQUENT_COLUMNS.
     *
     * @return the autoResizeMode of the table
     * @see #setAutoResizeMode
     * @see #doLayout
     */
    public int getAutoResizeMode() {
        return autoResizeMode;
    }

    /**
     * 设置是否从表格数据模型中自动创建各列 Sets this table's <code>autoCreateColumnsFromModel</code> flag. This method calls
     * <code>createDefaultColumnsFromModel</code> if <code>autoCreateColumnsFromModel</code> changes from false to true.
     *
     * @param autoCreateColumnsFromModel
     *            是否从表格数据模型中自动创建各列的开关
     * @see #getAutoCreateColumnsFromModel
     * @see #createDefaultColumnsFromModel
     * @beaninfo bound: true description: Automatically populates the columnModel when a new IPIMTableModel is
     *           submitted.
     */
    public void setAutoCreateColumnsFromModel(
            boolean autoCreateColumnsFromModel) {
        // 优化处理
        if (this.autoCreateColumnsFromModel != autoCreateColumnsFromModel) {
            // 保存旧的,设置新的
            boolean old = this.autoCreateColumnsFromModel;
            this.autoCreateColumnsFromModel = autoCreateColumnsFromModel;
            // 如果要从数据模型中创建列,就调用从数据模型中创建列的方法
            if (autoCreateColumnsFromModel) {
                createDefaultColumnsFromModel();
            }
            // 激发'自动从数据模型中创建列'事件
            firePropertyChange("autoCreateColumnsFromModel", old, autoCreateColumnsFromModel);
        }
    }

    /**
     * 确定是否从表格数据模型中自动创建各列 Determines whether the table will create default columns from the model. If true,
     * <code>setModel</code> will clear any existing columns and create new columns from the new model. Also, if the
     * event in the <code>tableChanged</code> notification specifies that the entire table changed, then the columns
     * will be rebuilt. The default is true.
     *
     * @return the autoCreateColumnsFromModel of the table
     * @see #setAutoCreateColumnsFromModel
     * @see #createDefaultColumnsFromModel
     */
    public boolean getAutoCreateColumnsFromModel() {
        return autoCreateColumnsFromModel;
    }

    /**
     * 设置缺省的绘制器 数Sets a default cell renderer to be used if no renderer has been set in a <code>PIMTableColumn</code>.
     * If renderer is <code>null</code>, removes the default renderer for this column class.
     *
     * @param columnClass
     *            列类型
     * @param renderer
     *            单元格绘制器
     * @see #getDefaultRenderer
     * @see #setDefaultEditor
     */
    public void setDefaultRenderer(
            Class columnClass,
            IPIMCellRenderer renderer) {
        if (renderer != null) {
            defaultRenderersByColumnClass.put(columnClass, renderer);
        } else {
            defaultRenderersByColumnClass.remove(columnClass);
        }
    }

    /**
     * 得到缺省的绘制器 Returns the cell renderer to be used when no renderer has been set in a <code>PIMTableColumn</code>.
     * During the rendering of cells the renderer is fetched from a <code>Hashtable</code> of entries according to the
     * class of the cells in the column. If there is no entry for this <code>columnClass</code> the method returns the
     * entry for the most specific superclass. The <code>PIMTable</code> installs entries for <code>Object</code>,
     * <code>Number</code>, and <code>Boolean</code>, all of which can be modified or replaced.
     *
     * @param columnClass
     *            列类型
     * @return 返回缺省的绘制器
     */
    public IPIMCellRenderer getDefaultRenderer(
            Class columnClass) {
        if (columnClass == null) {
            return null;
        } else {
            Object renderer = defaultRenderersByColumnClass.get(columnClass);
            if (renderer != null) {
                return (IPIMCellRenderer) renderer;
            } else {
                return getDefaultRenderer(columnClass.getSuperclass());
            }
        }
    }

    /**
     * 设置缺省的编辑器 Sets a default cell editor to be used if no editor has been set in a <code>PIMTableColumn</code>. If no
     * editing is required in a table, or a particular column in a table, uses the <code>isCellEditable</code> method in
     * the <code>IPIMTableModel</code> interface to ensure that this <code>PIMTable</code> will not start an editor in
     * these columns. If editor is <code>null</code>, removes the default editor for this column class.
     *
     * @param columnClass
     *            列数类型
     * @param editor
     *            代表本类的缺省编辑器
     * @see IPIMTableModel#isCellEditable
     * @see #getDefaultEditor
     * @see #setDefaultRenderer
     */
    public void setDefaultEditor(
            Class columnClass,
            IPIMCellEditor editor) {
        // 编辑器不为空
        if (editor != null) {
            // 保存到哈希表
            defaultEditorsByColumnClass.put(columnClass, editor);
        } else {
            // 否则从哈希表中移除此项
            defaultEditorsByColumnClass.remove(columnClass);
        }
    }

    /**
     * 得到某类的缺省编辑器 Returns the editor to be used when no editor has been set in a <code>PIMTableColumn</code>. During the
     * editing of cells the editor is fetched from a <code>Hashtable</code> of entries according to the class of the
     * cells in the column. If there is no entry for this <code>columnClass</code> the method returns the entry for the
     * most specific superclass. The <code>PIMTable</code> installs entries for <code>Object</code>, <code>Number</code>
     * , and <code>Boolean</code>, all of which can be modified or replaced.
     *
     * @param columnClass
     *            传入的列类型
     * @return the default cell editor to be used for this columnClass
     * @see #setDefaultEditor
     * @see #getColumnClass
     */
    public IPIMCellEditor getDefaultEditor(
            Class columnClass) {
        // 出错处理
        if (columnClass == null) {
            return null;
        } else {
            // 从哈希表中取出
            Object editor = defaultEditorsByColumnClass.get(columnClass);
            // 不为空就造型后返回
            if (editor != null) {
                return (IPIMCellEditor) editor;
            }
            // 否则找它父类去
            else {
                return getDefaultEditor(columnClass.getSuperclass());
            }
        }
    }

    /**
     * 设置是否可拖动 Sets the <code>dragEnabled</code> property, which must be <code>true</code> to enable automatic drag
     * handling (the first part of drag and drop) on this component. The <code>transferHandler</code> property needs to
     * be set to a non-<code>null</code> value for the drag to do anything. The default value of the
     * <code>dragEnabled</code property is <code>false</code>.
     *
     * <p>
     *
     * When automatic drag handling is enabled, most look and feels begin a drag-and-drop operation whenever the user
     * presses the mouse button over a selection and then moves the mouse a few pixels. Setting this property to
     * <code>true</code> can therefore have a subtle effect on how selections behave.
     *
     * <p>
     *
     * Some look and feels might not support automatic drag and drop; they will ignore this property. You can work
     * around such look and feels by modifying the component to directly call the <code>exportAsDrag</code> method of a
     * <code>TransferHandler</code>.
     *
     * @param b
     *            可拖动的开关
     */
    public void setDragEnabled(
            boolean b) {
        // 主要是图形设备上下文完善的保证
        if (b && GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        dragEnabled = b;
    }

    /**
     * 得到本表格实例是否可拖动
     * 
     * @return the value of the <code>dragEnabled</code> property
     * @see #setDragEnabled
     * @since 1.4
     */
    public boolean getDragEnabled() {
        return dragEnabled;
    }

    //
    // Selection methods
    //
    /**
     * 设置表格的选择模型 Sets the table's selection mode to allow only single selections, a single contiguous interval, or
     * multiple intervals.
     * <P>
     * <bold>Note:</bold> <code>PIMTable</code> provides all the methods for handling column and row selection. When
     * setting states, such as <code>setSelectionMode</code>, it not only updates the mode for the row selection model
     * but also sets similar values in the selection model of the <code>columnModel</code>. If you want to have the row
     * and column selection models operating in different modes, set them both directly.
     * <p>
     * Both the row and column selection models for <code>PIMTable</code> default to using a
     * <code>DefaultListSelectionModel</code> so that <code>PIMTable</code> works the same way as the <code>JList</code>
     * . See the <code>setSelectionMode</code> method in <code>JList</code> for details about the modes.
     *
     * @see JList#setSelectionMode
     * @beaninfo description: The selection mode used by the row and column selection models. enum: SINGLE_SELECTION
     *           ListSelectionModel.SINGLE_SELECTION SINGLE_INTERVAL_SELECTION
     *           ListSelectionModel.SINGLE_INTERVAL_SELECTION MULTIPLE_INTERVAL_SELECTION
     *           ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
     * @param selectionMode
     *            所使用的选择模型
     */
    public void setSelectionMode(
            int selectionMode) {
        // 先清空
        clearSelection();
        // 在列选择模型中设置
        getSelectionModel().setSelectionMode(selectionMode);
        // 在列模型的选择模型中设置
        getColumnModel().getSelectionModel().setSelectionMode(selectionMode);
    }

    /**
     * 设置是否允许行选择
     * 
     * @param rowSelectionAllowed
     *            允许行选择的开关
     * @see #getRowSelectionAllowed
     * @beaninfo bound: true attribute: visualUpdate true description: If true, an entire row is selected for each
     *           selected cell.
     */
    public void setRowSelectionAllowed(
            boolean rowSelectionAllowed) {
        // 保存旧的,设置新的
        boolean old = this.rowSelectionAllowed;
        this.rowSelectionAllowed = rowSelectionAllowed;
        // 优化,不等才重绘
        if (old != rowSelectionAllowed) {
            repaint();
        }
        // 激发允许行选择事件
        firePropertyChange("rowSelectionAllowed", old, rowSelectionAllowed);
    }

    /**
     * 判断是否允许行选择
     *
     * @return true if rows can be selected, otherwise false
     * @see #setRowSelectionAllowed
     */
    public boolean getRowSelectionAllowed() {
        return rowSelectionAllowed;
    }

    /**
     * 设置是否允许列选择
     *
     * @param columnSelectionAllowed
     *            允许列选择的开关
     * @see #getColumnSelectionAllowed
     * @beaninfo bound: true attribute: visualUpdate true description: If true, an entire column is selected for each
     *           selected cell.
     */
    public void setColumnSelectionAllowed(
            boolean columnSelectionAllowed) {
        // 保存旧的,设置新的,在列模型中设置
        boolean old = columnModel.getColumnSelectionAllowed();
        columnModel.setColumnSelectionAllowed(columnSelectionAllowed);
        // 优化,不等才重绘
        if (old != columnSelectionAllowed) {
            repaint();
        }
        // 激发允许列选择事件
        firePropertyChange("columnSelectionAllowed", old, columnSelectionAllowed);
    }

    /**
     * 判断是否允许列选择
     *
     * @return true if columns can be selected, otherwise false
     * @see #setColumnSelectionAllowed
     */
    public boolean getColumnSelectionAllowed() {
        return columnModel.getColumnSelectionAllowed();
    }

    /**
     * 设置是否允许单元格选择 Sets whether this table allows both a column selection and a row selection to exist simultaneously.
     * When set, the table treats the intersection of the row and column selection models as the selected cells.
     * Override <code>isCellSelected</code> to change this default behavior. This method is equivalent to setting both
     * the <code>rowSelectionAllowed</code> property and <code>columnSelectionAllowed</code> property of the
     * <code>columnModel</code> to the supplied value.
     *
     * @param cellSelectionEnabled
     *            允许单元格选择的开关
     * @see #getCellSelectionEnabled
     * @see #isCellSelected
     * @beaninfo bound: true attribute: visualUpdate true description: Select a rectangular region of cells rather than
     *           rows or columns.
     */
    public void setCellSelectionEnabled(
            boolean cellSelectionEnabled) {
        // 设置新的(行和列)
        setRowSelectionAllowed(cellSelectionEnabled);
        setColumnSelectionAllowed(cellSelectionEnabled);
        // 保存旧的(行和列)
        boolean old = this.cellSelectionEnabled;
        this.cellSelectionEnabled = cellSelectionEnabled;
        // 激发允许单元格选择事件
        firePropertyChange("cellSelectionEnabled", old, cellSelectionEnabled);
    }

    /**
     * 判断是否允许单元格选择 Returns true if both row and column selection models are enabled. Equivalent to
     * <code>getRowSelectionAllowed() &&
     * getColumnSelectionAllowed()</code>.
     *
     * @return true if both row and column selection models are enabled
     * @see #setCellSelectionEnabled
     */
    public boolean getCellSelectionEnabled() {
        // 两个标志的确认
        return getRowSelectionAllowed() && getColumnSelectionAllowed();
    }

    /**
     * 设置选中的行跨距 Selects the rows from <code>index0</code> to <code>index1</code>, inclusive.
     *
     * @param index0
     *            起始行号 one end of the interval
     * @param index1
     *            结束行号 the other end of the interval
     */
    public void setRowSelectionInterval(
            int index0,
            int index1) {
        // 保证一下数据正确就将其设置
        selectionModel.setSelectionInterval(boundRow(index0), boundRow(index1));
    }

    /**
     * 设置选中的列跨距 Selects the rows from <code>index0</code> to <code>index1</code>, inclusive.
     *
     * @param index0
     *            起始列号 one end of the interval
     * @param index1
     *            结束列号 the other end of the interval
     */
    public void setColumnSelectionInterval(
            int index0,
            int index1) {
        // 保证一下数据正确就将其在列模型设置
        columnModel.getSelectionModel().setSelectionInterval(boundColumn(index0), boundColumn(index1));
    }

    /**
     * 设置缺省选取的是哪一行哪一列。
     * 
     * @param row
     *            所在行
     * @param column
     *            所在列
     */
    public void setDefaultSelected(
            int row,
            int column) {
        // 如果正在编辑,就要移除编辑器
        if (isEditing()) {
            removeEditor();
        }
        // 有行有列才进行
        if (getRowCount() > 0 && getColumnCount() > 0) {
            // 验证参数的合法性
            if (row >= getRowCount() || column >= getColumnCount() || row < 0 || column < 0) {
                throw new IllegalArgumentException("Illegal argument row or column!");
            }

            setRowSelectionInterval(0, row);
            setColumnSelectionInterval(0, column);
        } else if (getRowCount() == 0 && getColumnCount() > 0) {
            // 处理缺省选中的是PIMTableHeader中的编辑器
            if (hasEditor) {
                // @ToDo
            }
        }
    }

    /**
     * 和滚动有关 called by PIMTableView
     * 
     * @param prmIsAddDelta
     *            是否增加偏移
     */
    public void setIsAddDelta(
            boolean prmIsAddDelta) {
        isAddDelta = prmIsAddDelta;
    }

    /**
     * 得到是否增加偏移
     * 
     * @return 是否增加偏移
     */
    public boolean isAddDelta() {
        return isAddDelta;
    }

    /**
     * 得到选择前景色 Returns the foreground color for selected cells.
     *
     * @return the <code>Color</code> object for the foreground property
     * @see #setSelectionForeground
     * @see #setSelectionBackground
     */
    public Color getSelectionForeground() {
        return selectionForeground;
    }

    /**
     * 设置选中单元格的前景色 Sets the foreground color for selected cells. Cell renderers can use this color to render text and
     * graphics for selected cells.
     * <p>
     * The default value of this property is defined by the look and feel implementation.
     * <p>
     * This is a <a href="http://java.sun.com/docs/books/tutorial/javabeans/whatis/beanDefinition.html">JavaBeans</a>
     * bound property.
     *
     * @param selectionForeground
     *            要设置的颜色
     * @see #getSelectionForeground
     * @see #setSelectionBackground
     * @see #setForeground
     * @see #setBackground
     * @see #setFont
     * @beaninfo bound: true description: A default foreground color for selected cells.
     */
    public void setSelectionForeground(
            Color selectionForeground) {
        // 保存旧的,设置新的
        Color old = this.selectionForeground;
        this.selectionForeground = selectionForeground;
        // 激发前景色改变事件
        firePropertyChange("selectionForeground", old, selectionForeground);
        // 新的颜色与旧的不等才重绘
        if (!selectionForeground.equals(old)) {
            repaint();
        }
    }

    /**
     * 得到选择背景色 Returns the background color for selected cells.
     *
     * @return the <code>Color</code> used for the background of selected list items
     * @see #setSelectionBackground
     * @see #setSelectionForeground
     */
    public Color getSelectionBackground() {
        return selectionBackground;
    }

    /**
     * 设置选中单元格的背景色 Sets the background color for selected cells. Cell renderers can use this color to the fill selected
     * cells.
     * <p>
     * The default value of this property is defined by the look and feel implementation.
     * <p>
     * This is a <a href="http://java.sun.com/docs/books/tutorial/javabeans/whatis/beanDefinition.html">JavaBeans</a>
     * bound property.
     *
     * @param selectionBackground
     *            要设置的颜色
     * @see #getSelectionBackground
     * @see #setSelectionForeground
     * @see #setForeground
     * @see #setBackground
     * @see #setFont
     * @beaninfo bound: true description: A default background color for selected cells.
     */
    public void setSelectionBackground(
            Color selectionBackground) {
        // 保存旧的,设置新的
        Color old = this.selectionBackground;
        this.selectionBackground = selectionBackground;
        // 激发背景色改变事件
        firePropertyChange("selectionBackground", old, selectionBackground);
        // 新的颜色与旧的不等才重绘
        if (!selectionBackground.equals(old)) {
            repaint();
        }
    }

    /**
     * 设置(当一个编辑器被激活时)是否由键盘事件转移焦点给它以响应键盘事件 Sets whether editors in this PIMTable get the keyboard focus when an editor is
     * activated as a result of the PIMTable forwarding keyboard events for a cell. By default, this property is false,
     * and the PIMTable retains the focus unless the cell is clicked.
     *
     * @param surrendersFocusOnKeystroke
     *            为真时表示当一键击导致编辑器被激活时编辑器要求得到焦点 true if the editor should get the focus when keystrokes cause the editor
     *            to be activated
     * @see #getSurrendersFocusOnKeystroke
     */
    public void setSurrendersFocusOnKeystroke(
            boolean surrendersFocusOnKeystroke) {
        this.surrendersFocusOnKeystroke = surrendersFocusOnKeystroke;
    }

    /**
     * 判断(当一个编辑器被激活时)是否由键盘事件转移焦点给它以响应键盘事件 Returns true if the editor should get the focus when keystrokes cause the
     * editor to be activated
     *
     * @return 为真时表示当一键击导致编辑器被激活时编辑器要求得到焦点 true if the editor should get the focus when keystrokes cause the editor to
     *         be activated
     * @see #setSurrendersFocusOnKeystroke
     */
    public boolean getSurrendersFocusOnKeystroke() {
        return surrendersFocusOnKeystroke;
    }

    /**
     * 返回本组件的UI Returns the L&F object that renders this component.
     *
     * @return the <code>PIMTableUI</code> object that renders this component
     */
    public BasicPIMTableUI getUI() {
        return (BasicPIMTableUI) ui;
    }

    /**
     * 设置本组件的UI Sets the L&F object that renders this component and repaints.
     *
     * @param ui
     *            一个 PIMTableUI 型的对象 the PIMTableUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo bound: true hidden: true attribute: visualUpdate true description: The UI object that implements the
     *           Component's LookAndFeel.
     */
    public void setUI(
            BasicPIMTableUI ui) {
        if (this.ui != ui) {
            super.setUI(ui);
            repaint();
        }
    }

    /**
     * 设置本表格的数据模型 Sets the data model for this table to <code>newModel</code> and registers with it for listener
     * notifications from the new data model.
     *
     * @param dataModel
     *            the new data source for this table
     * @see JComponent#getUIClassID
     * @beaninfo bound: true description: The model that is the source of the data for this view.
     */
    public void setModel(
            IPIMTableModel prmDataModel) {
        // 确保一下
        if (prmDataModel == null) {
            throw new IllegalArgumentException("Cannot set a null IPIMTableModel");
        }
        // 优化
        if (dataModel != prmDataModel) {
            IPIMTableModel old = dataModel;

            // 去除监听器
            if (old != null) {
                old.removeTableModelListener(this);
            }
            dataModel = prmDataModel;
            // 新的要加上监听器
            prmDataModel.addTableModelListener(this);
            // 有列模型就激发一下表格模型事件
            if (getColumnModel() != null) {
                tableChanged(new PIMTableModelEvent(prmDataModel, PIMTableModelEvent.HEADER_ROW));
            }
            firePropertyChange("model", old, prmDataModel);
        }
    }

    /**
     * 得到本表格的数据模型 Returns the <code>IPIMTableModel</code> that provides the data displayed by this <code>PIMTable</code>
     * .
     *
     * @return the <code>IPIMTableModel</code> that provides the data displayed by this <code>PIMTable</code>
     * @see #setModel
     */
    public IPIMTableModel getModel() {
        return dataModel;
    }

    /**
     * 设置本表格的列选择模型 Sets the row selection model for this table to <code>newModel</code> and registers for listener
     * notifications from the new selection model.
     *
     * @param newModel
     *            the new selection model
     * @see JComponent#getUIClassID
     * @beaninfo bound: true description: The selection model for rows.
     */
    public void setSelectionModel(
            ListSelectionModel newModel) {
        if (newModel == null)// 确保一下
            throw new IllegalArgumentException("Cannot set a null SelectionModel");

        if (newModel == selectionModel)
            return;

        ListSelectionModel tmpOldModel = selectionModel;
        selectionModel = newModel;

        if (tmpOldModel != null)// 去除监听器
            tmpOldModel.removeListSelectionListener(this);

        if (selectionModel != null)// 新的要加上监听器
            selectionModel.addListSelectionListener(this);

        firePropertyChange("selectionModel", tmpOldModel, newModel);// 激发本体列选择模型事件,并要重绘
        repaint();
    }

    /**
     * 得到本表格的列选择模型 Returns the <code>ListSelectionModel</code> that is used to maintain row selection state.
     *
     * @return the object that provides row selection state, <code>null</code> if row selection is not allowed
     * @see #setSelectionModel
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * 设置本表格的列模型 Sets the column model for this table to <code>newModel</code> and registers for listener notifications
     * from the new column model. Also sets the column model of the <code>PIMTableHeader</code> to
     * <code>columnModel</code>.
     *
     * @param columnModel
     *            the new data source for this table
     * @see JComponent#getUIClassID
     * @beaninfo bound: true description: The object governing the way columns appear in the view.
     */
    public void setColumnModel(
            IPIMTableColumnModel columnModel) {
        if (columnModel == null) {
            throw new IllegalArgumentException("Cannot set a null ColumnModel");
        }
        IPIMTableColumnModel old = this.columnModel;
        // 优化
        if (columnModel != old) {
            // 去除监听器
            if (old != null) {
                old.removeColumnModelListener(this);
            }
            this.columnModel = columnModel;
            // 新的要加上监听器
            columnModel.addColumnModelListener(this);
            // 有表格头,表格头也要设一下列模型
            if (tableHeader != null) {
                tableHeader.setColumnModel(columnModel);
            }
            // 激发本体列模型事件,并要重绘
            firePropertyChange("columnModel", old, columnModel);
            resizeAndRepaint();
        }
    }

    /**
     * 得到本表格的列模型 Returns the <code>IPIMTableColumnModel</code> that contains all column information of this table.
     *
     * @return the object that provides the column state of the table
     * @see #setColumnModel
     */
    public IPIMTableColumnModel getColumnModel() {
        return columnModel;
    }

    /**
     * 设置要在视口中显示的面积尺寸 Sets the preferred size of the viewport for this table.
     *
     * @param size
     *            要在视口中显示的面积尺寸 a <code>Dimension</code> object specifying the <code>preferredSize</code> of a
     *            <code>JViewport</code> whose view is this table
     * @see Scrollable#getPreferredScrollableViewportSize
     * @beaninfo description: The preferred size of the viewport.
     */
    public void setPreferredScrollableViewportSize(
            Dimension size) {
        preferredViewportSize = size;
    }

    /**
     * 用于解一个快速输入异常的bug called by: PIMTextFieldEditor
     * 
     * @return 是否要派发事件
     */
    public boolean getRepostEventFlag() {
        return repostEventFromTable;
    }

    /**
     * 目前本类中用来设置标志位,用于解一个快速输入异常的bug
     * 
     * @param flag
     *            是否要派发事件
     */
    public void setRepostEventFlag(
            boolean flag) {
        repostEventFromTable = flag;
    }

    /**
     * 表格是否允许排序
     * 
     * @return boolean
     */
    public boolean hasSorter() {
        return headerHasSorter;
    }

    /**
     * 表格是否允许排序
     * 
     * @param b
     *            是否允许排序
     */
    public void setHasSorter(
            boolean b) {
        headerHasSorter = b;

        if (!headerHasSorter) {
            if (tableHeader.getHeaderListener() != null) {
                tableHeader.removeMouseListener(tableHeader.getHeaderListener());
            }
        }
    }

    /**
     * 提供设置记录ID的方法
     * 
     * @param ID
     *            记录在数据库中的索引
     * @called by (HeaderListener已被user008注释掉,因为他认为排序时不应该跟选中行变化存在关系); BasicPIMTableUI;
     */
    public void setSelectedRecordID(
            int ID) {
        selectedRecordID = ID;
    }

    /**
     * 提供得到记录ID的方法
     * 
     * @return 记录在数据库中的索引
     * @called by PIMTableView
     */
    public int getSelectedRecordID() {
        return selectedRecordID;
    }

    /**
     * 设置是否绘制删除线
     * 
     * @param shouldDrawDeleteLine
     *            是否绘制删除线
     */
    public void setIsDrawDeleteLine(
            boolean shouldDrawDeleteLine) {
        // if(shouldDrawDeleteLine)
        // {
        // Thread.dumpStack();
        // }
        //
        isDrawDeleteLine = shouldDrawDeleteLine;
        repaint();
    }

    /**
     * 返回是否绘制删除线
     * 
     * @return 是否绘制删除线
     */
    public boolean isDrawDeleteLine() {
        return isDrawDeleteLine;
    }

    /**
     * 设置是否绘制下划线
     * 
     * @param shouldDrawUnderLine
     *            是否绘制下划线
     */
    public void setIsDrawUnderLine(
            boolean shouldDrawUnderLine) {
        isDrawUnderLine = shouldDrawUnderLine;
        repaint();
    }

    /**
     * 返回是否绘制下划线
     * 
     * @return 是否绘制下划线
     */
    public boolean isDrawUnderLine() {
        return isDrawUnderLine;
    }

    /**
     * 初始化表格的字体信息
     * 
     * @param key
     *            键值
     * @param prmValue
     *            字体或颜色
     */
    public void setFontAttribute(
            Object prmKey,
            Object pmrValue) {
        if (getView() == null) {
            return;
        }
        if (fontAttrubutePool == null) {
            fontAttrubutePool = new Hashtable();
        }
        fontAttrubutePool.put(prmKey, pmrValue);
    }

    /**
     * 取得表格的字体信息,字体或颜色
     * 
     * @param key
     *            键值
     * @return 设置的值
     */
    public Object getFontAttribute(
            Object key) {
        if (getView() == null) {
            return null;
        } else {
            return fontAttrubutePool.get(key);
        }
    }

    /**
     * 返回当前单元格编辑器 Returns the cell editor.
     *
     * @return the <code>IPIMTableCellEditor</code> that does the editing
     * @see #cellEditor
     * @called by: BasicPIMTableUI;HeaderListener;BasicPIMTableHeaderUI;PIMViewUtility;
     */
    public IPIMCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * 设置当前单元格编辑器 Sets the <code>cellEditor</code> variable.
     *
     * @param anEditor
     *            the IPIMTableCellEditor that does the editing
     * @see #cellEditor
     * @beaninfo bound: true description: The table's active cell editor, if one exists.
     */
    public void setCellEditor(
            IPIMCellEditor anEditor) {
        // 保存旧的,设置新的,激发单元格编辑器事件
        IPIMCellEditor oldEditor = cellEditor;
        cellEditor = anEditor;
        firePropertyChange("tableCellEditor", oldEditor, anEditor);
    }

    /**
     * 设置正要处于编辑的列 Sets the <code>editingColumn</code> variable.
     * 
     * @param aColumn
     *            要被编辑的单元格所在列 the column of the cell to be edited
     * @see #editingColumn
     */
    public void setEditingColumn(
            int aColumn) {
        editingColumn = aColumn;
    }

    /**
     * 设置正要处于编辑的行 Sets the <code>editingRow</code> variable.
     * 
     * @param aRow
     *            要被编辑的单元格所在行 the row of the cell to be edited
     * @see #editingRow
     */
    public void setEditingRow(
            int aRow) {
        editingRow = aRow;
    }

    /**
     * 得到指定单元格所匹配的绘制器,没有就给个缺省的 Returns an appropriate renderer for the cell specified by this row and column. If the
     * <code>PIMTableColumn</code> for this column has a non-null renderer, returns that. If not, finds the class of the
     * data in this column (using <code>getColumnClass</code>) and returns the default renderer for this type of data.
     * <p>
     * <b>Note:</b> Throughout the table package, the internal implementations always use this method to provide
     * renderers so that this default behavior can be safely overridden by a subclass.
     *
     * @param row
     *            the row of the cell to render, where 0 is the first row
     * @param column
     *            the column of the cell to render, where 0 is the first column
     * @return the assigned renderer; if <code>null</code> returns the default renderer for this type of object
     * @see a.swing.pimtable.DefaultPIMTableCellRenderer
     * @see a.swing.pimtable.PIMTableColumn#setCellRenderer
     * @see #setDefaultRenderer
     */
    public IPIMCellRenderer getCellRenderer(
            int row,
            int column) {
        // 从列号得到列,和绘制器
        PIMTableColumn tableColumn = getColumnModel().getColumn(column);
        IPIMCellRenderer renderer = tableColumn.getCellRenderer();
        // 去池中取它的绘制器
        if (renderer == null) {
            renderer = getDefaultRenderer(getColumnClass(column));
        }
        return renderer;
    }

    /**
     * 设置新的数据模型和列名
     * 
     * @param prmContents
     * @param prmHeaderTitles
     */
    public void setDataVector(
            Object[][] prmContents,
            Object[] prmHeaderTitles) {
        ((PIMTableModelAryBased) dataModel).setData(prmContents, prmHeaderTitles);
    }

    /**
     * 因为表格在视图中只有一个实例,表格模型大体也如此; 必须有设置表格是否可编辑的方法
     * 
     * @param isEditable
     *            是否可编辑
     */
    public void setCellEditable(
            boolean isEditable) {
        ((PIMTableModelAryBased) dataModel).setCellEditable(isEditable);
    }

    // ===================================================================================================================================================================

    // 以下为变量声明========================================================================================================================================================

    private boolean specialProcess;

    /**
     * 和视图操作有关,以后要去掉
     */
    private int lazySelectedRow = -1;
    /**
     * 保存基于本类通信的接口
     */
    protected IView view;
    /**
     * 保存数据模型
     */
    protected IPIMTableModel dataModel;
    /**
     * 保存表格列模型
     */
    protected IPIMTableColumnModel columnModel;
    /**
     * 保存表格列选择模型
     */
    protected ListSelectionModel selectionModel;
    /**
     * 保存表格头部
     */
    protected PIMTableHeader tableHeader;
    /**
     * 保存行高
     */
    protected int rowHeight;
    /**
     * 保存边缘空白
     */
    protected int rowMargin;
    /**
     * 保存网格线颜色
     */
    protected Color gridColor;
    /**
     * 保存是否显示水平网格线的属性
     */
    protected boolean showHorizontalLines;
    /**
     * 保存是否显示垂直网格线的属性
     */
    protected boolean showVerticalLines;
    /**
     * 保存列宽调整方式
     */
    protected int autoResizeMode;
    /**
     * 保存是否由数据模型来创建表格的列模型的标志
     */
    protected boolean autoCreateColumnsFromModel;
    /**
     * 保存首选视口的尺寸
     */
    protected Dimension preferredViewportSize;
    /**
     * 保存是否一行可以被选取的标志
     */
    protected boolean rowSelectionAllowed;
    /**
     * 保存单元格是否可被选中的标志
     */
    protected boolean cellSelectionEnabled;
    /**
     * 保存当前编辑器组件
     */
    transient protected Component editorComp;
    /**
     * 保存单元格编辑器的引用
     */
    transient IPIMCellEditor cellEditor;
    /**
     * 保存正在编辑的列
     */
    transient protected int editingColumn;
    /**
     * 保存正在编辑的行
     */
    transient protected int editingRow;
    /**
     * 保存由各列的类型所代表的缺省绘制器
     */
    transient protected Hashtable defaultRenderersByColumnClass;
    /**
     * 保存由各列的类型所代表的缺省编辑器
     */
    transient protected Hashtable defaultEditorsByColumnClass;
    /**
     * 保存被选中的前景色
     */
    protected Color selectionForeground;
    /**
     * 保存被选中的背景色
     */
    protected Color selectionBackground;
    /**
     * 用于实现表格行列的尺寸调整
     */
    private SizeSequence rowModel;
    /**
     * 保存是否可拖动的属性
     */
    private boolean dragEnabled;
    /**
     * 保存是否能由键盘事件来交出焦点的属性
     */
    private boolean surrendersFocusOnKeystroke;
    /**
     * 保存针对编辑器的属性监听器
     */
    private PropertyChangeListener editorRemover;
    /**
     * 保存是否有快速编辑栏的属性
     */
    private boolean hasEditor;
    /**
     * 保存类型属性????
     */
    protected int type;
    /**
     * 表格是否允许排序
     */
    private boolean headerHasSorter = true;

    /**
     * 以下几个变量用于邮件,任务视图在鼠标点击时(为界面上的行为时) 自身发生变化
     */
    private int toggleRow = -1000;
    /**
     * 是否要绘制删除线
     */
    private boolean isDrawDeleteLine;
    /**
     * 是否要绘制下划线
     */
    private boolean isDrawUnderLine;

    // 以下两个变量和表格的滚动有关
    /**
     * 表格的滚动相关
     */
    private boolean isAddDelta = true;
    /**
     * 表格的滚动相关
     */
    private int lastX;
    /*
     * 在表格的表格头点击排序时,会在此记下,
     */
    /**
     * 选中的记录的数据库索引
     */
    private int selectedRecordID;
    /**
     * 正处于编辑的行的编辑后的数据
     */
    private Vector editingRowRecords;
    // 派发键盘事件的标志位
    /**
     * 解表格快速输入溢出而产生
     */
    private boolean repostEventFromTable;

    // /** 解JDK的BUG设置的
    // */
    // private static final Caret caret = new JTextField().getCaret();

    /**
     * 拖动源
     */
    private DragSource dragSource = new DragSource();

    /**
     * 意图识别器
     */
    private DragGestureRecognizer recongizer;

    /**
     * 用来保存本类的所有行的字体属性,因为我们的字体是基于行的.
     */
    private Hashtable fontAttrubutePool = new Hashtable();

    // **********************************************************************************************************************************
    // 以下是一些本表格用得到的缺省绘制器
    /**
     * 数字型对象绘制器
     */
    public static class NumberRenderer extends DefaultPIMTableCellRenderer {
        /**
         * 缺省的创建器
         */
        public NumberRenderer() {
            super();
            // 缺省为右能者为对齐
            setHorizontalAlignment(JLabel.RIGHT);
        }
    }

    // **********************************************************************************************************************************

    // **********************************************************************************************************************************
    /**
     * 双精度数字型对象绘制器
     */
    public static class DoubleRenderer extends NumberRenderer {
        /**
         * 保存数字型数据的格式化器
         */
        NumberFormat formatter;

        /**
         * 缺省的创建器
         */
        public DoubleRenderer() {
            super();
        }

        /**
         * 保存设置值
         * 
         * @param prmValue
         *            传入的对象
         */
        public void setValue(
                Object prmValue) {
            // 创建格式化器
            if (formatter == null) {
                formatter = NumberFormat.getInstance();
            }
            // 传入值为空显示为空串,否则是经格式化的数据
            setText((prmValue == null) ? CASUtility.EMPTYSTR : formatter.format(prmValue));
        }
    }

    // **********************************************************************************************************************************

    // **********************************************************************************************************************************
    /**
     * 日期型数据的绘制器
     */
    public static class DateRenderer extends DefaultPIMTableCellRenderer {
        /**
         * 保存日期型数据的格式化器
         */
        DateFormat formatter;

        /**
         * 日期型数据的绘制器
         */
        public DateRenderer() {
            super();
        }

        /**
         * 保存设置值
         * 
         * @param prmValue
         *            传入的对象
         */
        public void setValue(
                Object prmValue) {
            // 创建格式化器
            if (formatter == null) {
                formatter = DateFormat.getDateInstance();
            }
            // 传入值为空显示为空串,否则是经格式化的数据
            //
            // 先定义一个空串
            /*
             * String dateStr = PIMUtility.EMPTYSTR; if(application != null) { //得到视图信息 PIMViewInfo pimViewInfo =
             * application.getActiveViewInfo(); //得到应用主类型 int tmpActiveAppType = pimViewInfo.getAppType(); //是日期型才进行
             * if(prmValue != null && prmValue instanceof Date) { Date tmpDate = (Date)prmValue; //这几种类型要小时和分钟
             * if(tmpActiveAppType == ModelConstants.OUTBOX_APP || tmpActiveAppType == ModelConstants.INBOX_APP )
             * //tmpActiveAppType == ModelConstants.SENDED_APP ) { //TODO: CustomOptions中暂时没有得到时间格式的方式,以后在国际化时要作调整
             * //CustomOptions.custOps.get } //联系人类型不要小时和分钟 else if(tmpActiveAppType == ModelConstants.CONTACT_APP) {
             * //TODO: CustomOptions中暂时没有得到时间格式的方式,以后在国际化时要作调整 } } }
             */
            // 设置文本
            setText((String) prmValue);
        }
    }

    // **********************************************************************************************************************************

    // **********************************************************************************************************************************
    /**
     * 图标对象的绘制器
     */
    public static class IconRenderer extends DefaultPIMTableCellRenderer {
        /**
         * 缺省的构建器
         */
        public IconRenderer() {
            super();
            // 图标为中心对齐
            setHorizontalAlignment(JLabel.CENTER);
        }

        /**
         * 保存设置值
         * 
         * @param prmValue
         *            传入的对象
         */
        public void setValue(
                Object prmValue) {
            // 是图标才设,否则为空
            setIcon((prmValue instanceof Icon) ? (Icon) prmValue : null);
        }
    }

    // **********************************************************************************************************************************

    // **********************************************************************************************************************************
    /**
     * 布尔型数据绘制器,是一个检查框
     */
    public static class BooleanRenderer extends JCheckBox implements IPIMCellRenderer {
        /**
         * 缺省的构建器
         */
        public BooleanRenderer() {
            super();
            // 为中心对齐
            setHorizontalAlignment(JLabel.CENTER);
        }

        /**
         * 重载父类中的方法,得到单元格绘制器
         * 
         * @param table
         *            传入的表格实例
         * @param prmValue
         *            传入的要绘制的值
         * @param isSelected
         *            本格是否被选中
         * @param hasFocus
         *            是否有焦点
         * @param row
         *            所在行
         * @param column
         *            所在列
         * @return 返回本格的绘制器组件
         */
        public Component getTableCellRendererComponent(
                PIMTable table,
                Object prmValue,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            // 选中情况的处理,就前景和背景的设置
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            }
            // 就前景和背景的设置
            else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            // 根据传入值设置选中状态
            setSelected((prmValue != null && ((Boolean) prmValue).booleanValue()));
            return this;
        }
    }

    // **********************************************************************************************************************************

    // **********************************************************************************************************************************
    /**
     * 缺省的编辑器
     */
    public static class GenericEditor extends DefaultPIMCellEditor {
        /**
         * 保存字符串这个类的类型Class型
         */
        Class[] argTypes = new Class[] { String.class };
        /**
         * 保存构建器
         */
        java.lang.reflect.Constructor constructor;
        /**
         * 保存要绘制的值
         */
        Object value;

        /**
         * 缺省的构建器
         */
        public GenericEditor() {
            // 调用父类的构建器
            super(new PIMTextFieldEditor());
        }

        /**
         * 重载父类中的方法,停止单元格的编辑
         * 
         * @return 返回命令停止单元格编辑成功的标志
         */
        public boolean stopCellEditing() {
            String s = (String) super.getCellEditorValue();
            // 如果编辑器组件中值为空字串，则如果构建器的声明类是字符串类,保存一下
            if (CASUtility.EMPTYSTR.equals(s)) {
                if (constructor.getDeclaringClass() == String.class) {
                    value = s;
                }
                super.stopCellEditing(); // 调用父类方法停止编辑
            }
            // 不为空的情况下 //TODO_user008：是不是少了else 语句？？？？？
            try {
                // 目前看不懂
                value = constructor.newInstance(new Object[] { s });
            } catch (Exception e) {
                // 设个边框
                ((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
                return false;
            }
            // 调用父类方法停止编辑
            return super.stopCellEditing();
        }

        /**
         * 重载父类中的方法,得到单元格绘制器
         * 
         * @param table
         *            传入的表格实例
         * @param value
         *            传入的要绘制的值
         * @param isSelected
         *            本格是否被选中
         * @param row
         *            所在行
         * @param column
         *            所在列
         * @return 返回本格的绘制器组件
         */
        public Component getTableCellEditorComponent(
                PIMTable table,
                Object prmValue,
                boolean isSelected,
                int row,
                int column) {
            value = null;
            // 为编辑器组件设个边框
            ((JComponent) getComponent()).setBorder(new LineBorder(Color.black));
            try {
                // 得到本类的类型
                Class type = table.getColumnClass(column);
                // Object 型作字符串处理
                if (type == Object.class) {
                    type = String.class;
                }
                // 调用反射得到构建器
                constructor = type.getConstructor(argTypes);
            } catch (Exception e) {
                return null;
            }
            // 调用父类方法得到组件
            return super.getTableCellEditorComponent(table, prmValue, isSelected, row, column);
        }

        /**
         * 得到单元格编辑器中的值
         * 
         * @return 返回单元格编辑器中的值
         */
        public Object getCellEditorValue() {
            return super.getCellEditorValue();
        }
    }

    // **********************************************************************************************************************************

    // **********************************************************************************************************************************
    /**
     * 数字类型
     */
    public static class NumberEditor extends GenericEditor {

        /**
         * 缺省的构建器
         */
        public NumberEditor() {
            // 还是右对齐
            ((JTextField) getComponent()).setHorizontalAlignment(JTextField.RIGHT);
        }
    }

    // **********************************************************************************************************************************

    // **********************************************************************************************************************************
    /**
     * 布尔类型
     */
    public static class BooleanEditor extends DefaultPIMCellEditor {
        /**
         * 缺省的构建器
         */
        public BooleanEditor() {
            super(new PIMCheckBoxEditor());
            // 中心对齐
            JCheckBox checkBox = (JCheckBox) getComponent();
            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
        }
    }

    // **********************************************************************************************************************************

    // **********************************************************************************************************************************
    // This class tracks changes in the keyboard focus state. It is used
    // when the PIMTable is editing to determine when to cancel the edit.
    // If focus switches to a component outside of the jtable, but in the
    // same window, this will cancel editing.
    /**
     * 表格的属性监听器类,就是给编辑器组件用的 处理单元格编辑器的移除
     */
    public static class CellEditorRemover implements PropertyChangeListener {
        /**
         * 保存键盘焦点管理器
         */
        KeyboardFocusManager focusManager;
        /**
         * 改内部类而产生的,对表格的引用
         */
        PIMTable table;

        /**
         * 创建一个单元格编辑器的实例
         * 
         * @param prmTable
         *            本类引用
         * @param fm
         *            传入一个键盘焦点管理器
         */
        public CellEditorRemover(PIMTable prmTable, KeyboardFocusManager fm) {
            this.focusManager = fm;
            table = prmTable;
        }

        /**
         * 实现接口中的方法 在属性变化时应该做的
         * 
         * @param ev
         *            属性变化事件源
         */
        public void propertyChange(
                PropertyChangeEvent ev) {
            // 不在编辑,或在编辑,但不允许"失去焦点就停止编辑" 就只好不做事了
            if (!table.isEditing() || table.getClientProperty("terminateEditOnFocusLost") != Boolean.TRUE) {
                return;
            }
            // 得到持久焦点拥有者,有才继续
            Component c = focusManager.getPermanentFocusOwner();
            while (c != null) {
                // 是表格拥有焦点就不管
                if (c == table) {
                    // focus remains inside the table
                    return;
                }
                // 否则如果是一个窗体或是一个光光的Applet 组件
                else if ((c instanceof Window) || (c instanceof Applet && c.getParent() == null)) {
                    // 如果是根组件,且编辑器还没有停止编辑,就让它停止编辑
                    if (c == SwingUtilities.getRoot(table)) {
                        if (!table.getCellEditor().stopCellEditing()) {
                            table.getCellEditor().cancelCellEditing();
                        }
                    }
                    // 结束循环
                    break;
                }
                // 既然是中间级容器,就取父组件,继续循环
                c = c.getParent();
            }
        }
    }

    // **********************************************************************************************************************************

    /**
     * 本类完全因下一个 adjustSizes 方法而产生
     */
    public static class Resizable2_2 implements Resizable2 {
        /**
         * 改内部类而产生的
         */
        Resizable3 r;
        /**
         * 对表格的引用
         */
        PIMTable table;

        /**
         * 构建器,
         * 
         * @param prmTable
         *            传入引用
         * @param resizable3
         *            一种调整算法
         */
        public Resizable2_2(PIMTable prmTable, Resizable3 resizable3) {
            table = prmTable;
            r = resizable3;
        }

        /**
         * 返回元素数目
         * 
         * @return 暂时不知是什么元素
         */
        public int getElementCount() {
            return r.getElementCount();
        }

        /**
         * 返回一个元素的下界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getLowerBoundAt(
                int i) {
            return r.getMidPointAt(i);
        }

        /**
         * 返回一个元素的上界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getUpperBoundAt(
                int i) {
            return r.getUpperBoundAt(i);
        }

        /**
         * 返回给一个新元素设置尺寸
         * 
         * @param newSize
         *            目前不详
         * @param i
         *            目前不详
         */
        public void setSizeAt(
                int newSize,
                int i) {
            r.setSizeAt(newSize, i);
        }
    }

    /**
     * 本类完全因下一个 setWidthsFromPreferredWidths 方法而产生
     */
    public static class Resizable3_1 implements Resizable3 {
        /**
         * 保存列模型
         */
        IPIMTableColumnModel cm;
        /**
         * 保存表格的引用
         */
        PIMTable table;
        /**
         * 是否反转
         */
        boolean inverse;

        /**
         * 构建器,
         * 
         * @param prmTable
         *            传入引用
         * @param columnModel
         *            列模型
         * @param prmInverse
         *            是否反转
         */
        public Resizable3_1(PIMTable prmTable, IPIMTableColumnModel columnModel, boolean prmInverse) {
            table = prmTable;
            cm = columnModel;
            inverse = prmInverse;
        }

        /**
         * 返回元素数目
         * 
         * @return 暂时不知是什么元素
         */
        public int getElementCount() {
            return cm.getColumnCount();
        }

        /**
         * 返回一个元素的下界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getLowerBoundAt(
                int i) {
            return cm.getColumn(i).getMinWidth();
        }

        /**
         * 得到中点
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getMidPointAt(
                int i) {
            // 不反转就返回其首选宽度
            if (!inverse) {
                return cm.getColumn(i).getPreferredWidth();
            }
            // 否则就返回其宽度
            else {
                return cm.getColumn(i).getWidth();
            }
        }

        /**
         * 返回一个元素的上界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getUpperBoundAt(
                int i) {
            return cm.getColumn(i).getMaxWidth();
        }

        /**
         * 返回给一个新元素设置尺寸
         * 
         * @param newSize
         *            目前不详
         * @param i
         *            目前不详
         */
        public void setSizeAt(
                int newSize,
                int i) {
            // 不反转就设置其宽度
            if (!inverse) {
                cm.getColumn(i).setWidth(newSize);
            }
            // 否则设置其首选宽度
            else {
                cm.getColumn(i).setPreferredWidth(newSize);
            }
        }

    }

    /**
     * 本类完全因下一个 accommodateDelta 方法而产生
     */
    public static class Resizable3_2 implements Resizable3 {
        /**
         * 保存列模型
         */
        IPIMTableColumnModel cm;
        /**
         * 表格的引用
         */
        PIMTable table;
        /**
         * 开始列
         */
        int start;
        /**
         * 结束列
         */
        int end;

        /**
         * 构建器,
         * 
         * @param prmTable
         *            传入引用
         * @param columnModel
         *            列模型
         * @param prmStart
         *            起始行
         * @param prmEnd
         *            结束行
         */
        public Resizable3_2(PIMTable prmTable, IPIMTableColumnModel columnModel, int prmStart, int prmEnd) {
            table = prmTable;
            cm = columnModel;
            start = prmStart;
            end = prmEnd;
        }

        /**
         * 返回元素数目
         * 
         * @return 暂时不知是什么元素
         */
        public int getElementCount() {
            return end - start;
        }

        /**
         * 返回一个元素的下界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getLowerBoundAt(
                int i) {
            return cm.getColumn(i + start).getMinWidth();
        }

        /**
         * 得到中点
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getMidPointAt(
                int i) {
            return cm.getColumn(i + start).getWidth();
        }

        /**
         * 返回一个元素的上界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getUpperBoundAt(
                int i) {
            return cm.getColumn(i + start).getMaxWidth();
        }

        /**
         * 返回给一个新元素设置尺寸
         * 
         * @param newSize
         *            目前不详
         * @param i
         *            目前不详
         */
        public void setSizeAt(
                int newSize,
                int i) {
            cm.getColumn(i + start).setWidth(newSize);
        }

    }

    /**
     * 内部使用的一个接口
     */
    public static interface Resizable2 {
        /**
         * 返回元素数目
         * 
         * @return 暂时不知是什么元素
         */
        public int getElementCount();

        /**
         * 返回一个元素的下界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getLowerBoundAt(
                int i);

        /**
         * 返回一个元素的上界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getUpperBoundAt(
                int i);

        /**
         * 返回给一个新元素设置尺寸
         * 
         * @param newSize
         *            目前不详
         * @param i
         *            目前不详
         */
        public void setSizeAt(
                int newSize,
                int i);
    }

    /**
     * 继承了上一个接口,添加了一个方法
     */
    public static interface Resizable3 extends Resizable2 {

        /**
         * 得到中点
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getMidPointAt(
                int i);
    }

    /**
     * 本类完全因下一个 adjustSizes 方法而产生
     */
    public static class Resizable2_1 implements Resizable2 {
        /**
         * 改内部类而产生的
         */
        Resizable3 r;
        /**
         * 对表格的引用
         */
        PIMTable table;

        /**
         * 构建器,
         * 
         * @param prmTable
         *            传入引用
         * @param resizable3
         *            一种调整算法
         */
        public Resizable2_1(PIMTable prmTable, Resizable3 resizable3) {
            table = prmTable;
            r = resizable3;
        }

        /**
         * 返回元素数目
         * 
         * @return 暂时不知是什么元素
         */
        public int getElementCount() {
            return r.getElementCount();
        }

        /**
         * 返回一个元素的下界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getLowerBoundAt(
                int i) {
            return r.getLowerBoundAt(i);
        }

        /**
         * 返回一个元素的上界限
         * 
         * @param i
         *            目前不详
         * @return 目前不详
         */
        public int getUpperBoundAt(
                int i) {
            return r.getMidPointAt(i);
        }

        /**
         * 返回给一个新元素设置尺寸
         * 
         * @param newSize
         *            目前不详
         * @param i
         *            目前不详
         */
        public void setSizeAt(
                int newSize,
                int i) {
            r.setSizeAt(newSize, i);
        }
    }
}

/*
 * Called from the JComponent's EnableSerializationFocusListener to //* do any Swing-specific pre-serialization
 * configuration. //* ///** 通知组件写入对象的方法 //* //void compWriteObjectNotify() //{ // //super.compWriteObjectNotify (); //
 * if (getToolTipText() == null) // { // ToolTipManager.sharedInstance().unregisterComponent(this); // } //}
 */
