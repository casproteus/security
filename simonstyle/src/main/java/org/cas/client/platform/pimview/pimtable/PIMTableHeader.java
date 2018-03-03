package org.cas.client.platform.pimview.pimtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.frame.action.NewObjectAction;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.util.PIMViewUtil;

public class PIMTableHeader extends JComponent implements IPIMTableColumnModelListener, CellEditorListener,
        ListSelectionListener, Border, MouseListener, KeyListener, Runnable, Releasable {
    /**
     * 本类的UIClassID 本类的标识符
     * 
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "PIMTableHeaderUI";

    /**
     * 缺省构建器,生成的表格为空 Constructs a <code>PIMTableHeader</code> with a default <code>IPIMTableColumnModel</code>.
     *
     * @see #createDefaultColumnModel
     */
    public PIMTableHeader() {
        this(null);
    }

    /**
     * 创建一个 PIMTableHeader 的实例,传入参数为一个实现 IPIMTableColumnModel 接口的类 Constructs a <code>PIMTableHeader</code> which is
     * initialized with <code>cm</code> as the column model. If <code>cm</code> is <code>null</code> this method will
     * initialize the table header with a default <code>IPIMTableColumnModel</code>.
     *
     * @param cm
     *            the column model for the table
     * @see #createDefaultColumnModel
     */
    public PIMTableHeader(IPIMTableColumnModel cm) {
        // 父类先构建出来
        super();

        // 设置不接受焦点
        setFocusable(false);

        // 如无表格列模型,就使用缺省的列模型
        if (cm == null) {
            cm = createDefaultColumnModel();
        }

        // 设置列模型
        setColumnModel(cm);
        setBorder(this);
        // Initialize local ivars
        // 初始化本地变量
        initializeLocalVars();
        // 具体事件处理时，会对鼠标位置做判断，使header中有编辑栏时，编辑栏和表头对鼠标事件有不同的相应。
        headerList = new HeaderListener(this, (SortButtonRenderer) defaultRenderer);
        this.addMouseListener(headerList);
        // 这句是必须要有的 Get UI going
        updateUI();

        setFocusTraversalKeysEnabled(false);
    }

    //
    // Local behavior attributes
    //

    /**
     * 设置 PIMTable 表格,与其关联 Sets the table associated with this header.
     * 
     * @param table
     *            the new table
     * @beaninfo bound: true description: The table associated with this header.
     */
    public void setTable(
            PIMTable table) {
        PIMTable old = this.table;
        this.table = table;
        // 激发表格变化这个属性事件
        firePropertyChange("table", old, table);
    }

    /**
     * 返回相关联的表格 Returns the table associated with this header.
     * 
     * @return the <code>table</code> property
     */
    public PIMTable getTable() {
        return table;
    }

    /**
     * 设置是否允许重新排序 Sets whether the user can drag column headers to reorder columns.
     *
     * @param reorderingAllowed
     *            true if the table view should allow reordering; otherwise false
     * @see #getReorderingAllowed
     * @beaninfo bound: true description: Whether the user can drag column headers to reorder columns.
     */
    public void setReorderingAllowed(
            boolean reorderingAllowed) {
        boolean old = this.reorderingAllowed;
        this.reorderingAllowed = reorderingAllowed;
        // 激发允许重新排序这个属性事件
        firePropertyChange("reorderingAllowed", old, reorderingAllowed);
    }

    /**
     * 返回是否允许重新排序的标志 Returns true if the user is allowed to rearrange columns by dragging their headers, false
     * otherwise. The default is true. You can rearrange columns programmatically regardless of this setting.
     *
     * @return the <code>reorderingAllowed</code> property
     * @see #setReorderingAllowed
     */
    public boolean getReorderingAllowed() {
        return reorderingAllowed;
    }

    /**
     * 设置是否允许重新调整尺寸 Sets whether the user can resize columns by dragging between headers.
     *
     * @param resizingAllowed
     *            true if table view should allow resizing
     * @see #getResizingAllowed
     * @beaninfo bound: true description: Whether the user can resize columns by dragging between headers.
     */
    public void setResizingAllowed(
            boolean resizingAllowed) {
        boolean old = this.resizingAllowed;
        this.resizingAllowed = resizingAllowed;
        // 激发允许重新调整尺寸这个属性事件
        firePropertyChange("resizingAllowed", old, resizingAllowed);
    }

    /**
     * 返回是否允许重新调整尺寸的标志 Returns true if the user is allowed to resize columns by dragging between their headers, false
     * otherwise. The default is true. You can resize columns programmatically regardless of this setting.
     *
     * @return the <code>resizingAllowed</code> property
     * @see #setResizingAllowed
     */
    public boolean getResizingAllowed() {
        return resizingAllowed;
    }

    /**
     * 得到当前拖动中的列,仅在拖动事件中才有意义 Returns the the dragged column, if and only if, a drag is in process, otherwise returns
     * <code>null</code>.
     *
     * @return the dragged column, if a drag is in process, otherwise returns <code>null</code>
     * @see #getDraggedDistance
     */
    public PIMTableColumn getDraggedColumn() {
        return draggedColumn;
    }

    /**
     * 返回表头事件
     * 
     * @return HeaderListener
     */
    public HeaderListener getHeaderListener() {
        return headerList;
    }

    /**
     * 得到把一个列拖动的距离,仅在拖动事件中才有意义 Returns the column's horizontal distance from its original position, if and only if, a
     * drag is in process. Otherwise, the the return value is meaningless.
     *
     * @return the column's horizontal distance from its original position, if a drag is in process, otherwise the
     *         return value is meaningless
     * @see #getDraggedColumn
     */
    public int getDraggedDistance() {
        return draggedDistance;
    }

    /**
     * 得到处于尺寸调整中的列,仅在列尺寸调整事件中才有意义 Returns the resizing column. If no column is being resized this method returns
     * <code>null</code>.
     *
     * @return the resizing column, if a resize is in process, otherwise returns <code>null</code>
     */
    public PIMTableColumn getResizingColumn() {
        return resizingColumn;
    }

    /**
     * 废弃的方法,在响应列拖动或尺寸调整时实时重绘,现在无限制 设置是否允许表格实时更新 Obsolete as of Java 2 platform v1.3. Real time repaints, in response to
     * column dragging or resizing, are now unconditional.
     * 
     * @param flag
     *            是否允许表格实时更新的标志
     */
    /*
     * Sets whether the body of the table updates in real time when a column is resized or dragged.
     * @param flag true if tableView should update the body of the table in real time
     * @see #getUpdateTableInRealTime
     */
    public void setUpdateTableInRealTime(
            boolean flag) {
        updateTableInRealTime = flag;
    }

    /**
     * 废弃的方法,在响应列拖动或尺寸调整时实时重绘,现在无限制 判断是否允许表格实时更新 Obsolete as of Java 2 platform v1.3. Real time repaints, in response to
     * column dragging or resizing, are now unconditional.
     * 
     * @return 返回是否允许表格实时更新的标志
     */
    /*
     * Returns true if the body of the table view updates in real time when a column is resized or dragged. User can set
     * this flag to false to speed up the table's response to user resize or drag actions. The default is true.
     * @return true if the table updates in real time
     * @see #setUpdateTableInRealTime
     */
    public boolean getUpdateTableInRealTime() {
        return updateTableInRealTime;
    }

    /**
     * 设置缺省绘制器的方法,传入一个实现了 IPIMTableCellRenderer 接口的对象 Sets the default renderer to be used when no
     * <code>headerRenderer</code> is defined by a <code>PIMTableColumn</code>.
     * 
     * @param defaultRenderer
     *            the default renderer
     */
    public void setDefaultRenderer(
            IPIMCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    /**
     * 返回当前缺省绘制器 Returns the default renderer used when no <code>headerRenderer</code> is defined by a
     * <code>PIMTableColumn</code>.
     * 
     * @return the default renderer
     */
    public IPIMCellRenderer getDefaultRenderer() {
        return defaultRenderer;
    }

    /**
     * 根据传入的点坐标返回其所处于的那一列,返回-1表示越界了,
     * 
     * @param point
     *            一个点 Returns the index of the column that <code>point</code> lies in, or -1 if it lies out of bounds.
     *
     * @return the index of the column that <code>point</code> lies in, or -1 if it lies out of bounds
     */
    public int columnAtPoint(
            Point point) {
        int x = point.x;
        // 如果组件不是自左到右
        if (!getComponentOrientation().isLeftToRight()) {
            // 从右边儿开始
            x = getWidthInRightToLeft() - x;
        }
        // 调用列模型中的方法
        return getColumnModel().getColumnIndexAtX(x);
    }

    /**
     * 本方法得到鼠标点击所在行
     * 
     * @return 所在行(因为有可能是快速编辑栏的表格)
     * @param point
     *            点击位置
     */
    public int rowAtPointEx(
            Point point) {
        int y = point.y;
        if (table.hasEditor()) {
            if (y >= 0 && y < getHeight() / 2) {
                return 0;
            } else if (y > getHeight() / 2 && y <= getHeight()) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * 得到列头部某列的矩形区域 返回是否允许表格实时更新的标志
     * 
     * @param column
     *            待查询的列索引
     * @see PIMTable#getCellRect
     * @return 返回给定索引值的列头部矩形区域
     *
     */
    public Rectangle getHeaderRect(
            int column) {
        Rectangle r = new Rectangle();
        // 得到列模型
        IPIMTableColumnModel cm = getColumnModel();
        // 得到本组件高度
        r.height = getHeight();

        // 排错
        if (column < 0) {
        }
        // 如果大于总列数
        else if (column >= cm.getColumnCount()) {
            // 如果组件是自左到右
            if (getComponentOrientation().isLeftToRight()) {
                // 取X坐标
                r.x = getWidth();
            }
        }
        // 正常
        else {
            // 遍历,取得该列的宽度
            for (int i = 0; i < column; i++) {
                r.x += cm.getColumn(i).getWidth();
            }
            // 如果表格配有编辑器
            if (table.hasEditor()) {
                // 高度减半
                r.height /= 2;
            }
            // 取得该列宽度
            r.width = cm.getColumn(column).getWidth();
        }
        return r;
    }

    /**
     * 得到列头部某列的矩形区域
     * 
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @param includeSpacing
     *            是否包括空格
     * @return 返回该列头部某列的矩形区域
     */
    public Rectangle getHeaderRect(
            int row,
            int column,
            boolean includeSpacing) {
        Rectangle r = new Rectangle();
        // 得到列模型
        IPIMTableColumnModel cm = getColumnModel();
        // 设置有效标志为真
        boolean valid = true;
        // 错误处理
        if (row < 0) {
            valid = false;
        } else if (row >= 2) {
            // 表格头的行数大于实际数的处理为:
            // 区域Y相对坐标设置为高度
            r.y = getHeight();
            valid = false;
        } else {
            // 正常情况下
            // 如果有快速编辑栏
            if (table.hasEditor()) {
                // 区域高度减半
                r.height = getHeight() / 2;
            } else {
                // 否则为表格头高度
                r.height = getHeight();
            }
            // 得到区域的Y相对坐标
            r.y = row * r.height;
        }
        // 至此,以上部分完成了区域高度和Y坐标的设置

        // 列无效,有效标志为假
        if (column < 0) {
            valid = false;
        }
        // 在列索引大于有效列数时
        else if (column >= cm.getColumnCount()) {
            // 在组件朝向是自左到右时
            if (getComponentOrientation().isLeftToRight()) {
                // 设置X相对坐标
                r.x = getWidth();
            }
            valid = false;
        } else {
            // 正常处理,
            for (int i = 0; i < column; i++) {
                // 通过累加求得该列的X坐标
                r.x += cm.getColumn(i).getWidth();
            }
            // 有快速编辑栏
            if (table.hasEditor()) {
                // r.height /= 2;
            }

            r.width = cm.getColumn(column).getWidth();
        }
        // 至此,以上部分完成了区域宽度和X坐标的设置
        if (valid && !includeSpacing) {
            r.setBounds(r.x + 1 / 2, r.y + 1 / 2, r.width - 1, r.height - 1);
        }
        // 不知为何,表格体的X坐标总是向左偏移一个像素.先这么处理
        // 一下,看有什么问题.
        // r.x += 1;
        return r;
    }

    /**
     * 得到工具条提示 Allows the renderer's tips to be used if there is text set.
     * 
     * @param event
     *            the location of the event identifies the proper renderer and, therefore, the proper tip
     * @return the tool tip for this component
     */
    public String getToolTipText(
            MouseEvent event) {
        // 先定义一个字符串
        String tip = null;
        Point p = event.getPoint();
        int column;

        // Locate the renderer under the event location
        // 如果鼠标点击的位置是有效列,
        if ((column = columnModel.getColumnIndexAtX(p.x)) != -1) {
            // 取该列,得到它的头部绘制器
            PIMTableColumn aColumn = columnModel.getColumn(column);
            IPIMCellRenderer renderer = aColumn.getHeaderRenderer();
            // 如该列没有绘制器取缺省绘制器
            if (renderer == null) {
                renderer = defaultRenderer;
            }
            // 得到绘制器组件
            Component component =
                    renderer.getTableCellRendererComponent(getTable(), aColumn.getHeaderValue(), false, false, -1,
                            column);

            // Now have to see if the component is a JComponent before
            // getting the tip
            // 在得到工具条提示前看一下它是不是一个JComponent
            if (component instanceof JComponent) {
                // Convert the event to the renderer's coordinate system
                MouseEvent newEvent;
                // 得到该列头部的单元格区域,
                Rectangle cellRect = getHeaderRect(column);
                // 得到相对坐标
                p.translate(-cellRect.x, -cellRect.y);
                // 新建一个鼠标事件
                newEvent =
                        new MouseEvent(component, event.getID(), event.getWhen(), event.getModifiers(), p.x, p.y,
                                event.getClickCount(), event.isPopupTrigger());
                // 通过 JComponent 的方法来得到工具条提示
                tip = ((JComponent) component).getToolTipText(newEvent);
            }
        }

        // No tip from the renderer get our own tip
        // 没有得到工具条提示的洲
        if (tip == null) {
            tip = getToolTipText();
        }

        return tip;
    }

    //
    // Managing PIMTableHeaderUI
    //

    /**
     * 得到UI 重载了父类中的方法,八股文 Returns the look and feel (L&F) object that renders this component.
     *
     * @return the <code>PIMTableHeaderUI</code> object that renders this component
     */
    public PIMTableHeaderUI getUI() {
        return (PIMTableHeaderUI) ui;
    }

    /**
     * 设置UI 重载了父类中的方法,八股文 Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui
     *            the <code>PIMTableHeaderUI</code> L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(
            PIMTableHeaderUI ui) {
        // 一个优化
        if (this.ui != ui) {
            super.setUI(ui);
            repaint();
        }
    }

    /**
     * 更新UI 重载了父类中的方法,八股文 Notification from the <code>UIManager</code> that the look and feel (L&F) has changed.
     * Replaces the current UI object with the latest version from the <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((PIMTableHeaderUI) UIManager.getUI(this));
        // 调了revalidate方法，和repaint方法。
        resizeAndRepaint();
        // PENDING //TODO：这句话可能不但没有必要而且导致视图闪烁（即多话了一边）。
        invalidate();
    }

    /**
     * 得到本UI类的标识符ID Returns the suffix used to construct the name of the look and feel (L&F) class used to render this
     * component.
     * 
     * @return the string "PIMTableHeaderUI"
     */
    public String getUIClassID() {
        return uiClassID;
    }

    //
    // Managing models
    //

    /**
     * 设置本类实例的列模型 Sets the column model for this table to <code>newModel</code> and registers for listener notifications
     * from the new column model.
     *
     * @param columnModel
     *            the new data source for this table
     * @beaninfo bound: true description: The object governing the way columns appear in the view.
     */
    public void setColumnModel(
            IPIMTableColumnModel columnModel) {
        // 出错处理
        if (columnModel == null) {
            throw new IllegalArgumentException("Cannot set a null ColumnModel");
        }

        IPIMTableColumnModel old = this.columnModel;
        // 优化算法,如新旧列模型不等
        if (columnModel != old) {
            // 如旧列模型不为空就要移除其上的所有监听器
            if (old != null) {
                old.removeColumnModelListener(this);
            }
            // 给新的加上列模型监听器
            this.columnModel = columnModel;
            columnModel.addColumnModelListener(this);
            // 激发列模型事件
            firePropertyChange("columnModel", old, columnModel);
            // 调整尺寸所重绘
            resizeAndRepaint();
        }
    }

    /**
     * 得到本类实例的列模型 Returns the <code>IPIMTableColumnModel</code> that contains all column information of this table
     * header.
     *
     * @return the <code>columnModel</code> property
     * @see #setColumnModel
     */
    public IPIMTableColumnModel getColumnModel() {
        return columnModel;
    }

    //
    // Implementing IPIMTableColumnModelListener interface
    //

    /**
     * 实现接口中的方法 根据传入的列模型事件来进行列的添加,要做的工作也就是重绘一下 Invoked when a column is added to the table column model.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by <code>PIMTable</code>.
     *
     * @param e
     *            the event received
     * @see IPIMTableColumnModelListener
     */
    public void columnAdded(
            PIMTableColumnModelEvent e) {
        resizeAndRepaint();
    }

    /**
     * 实现接口中的方法 根据传入的列模型事件来进行列的移除,要做的工作也就是重绘一下 Invoked when a column is removed from the table column model.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by <code>PIMTable</code>.
     *
     * @param e
     *            the event received
     * @see IPIMTableColumnModelListener
     */
    public void columnRemoved(
            PIMTableColumnModelEvent e) {
        resizeAndRepaint();
    }

    /**
     * 实现接口中的方法 根据传入的列模型事件来进行列的移动,要做的工作也就是重绘一下 Invoked when a column is repositioned.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by <code>PIMTable</code>.
     *
     * @param e
     *            the event received
     * @see IPIMTableColumnModelListener
     */
    public void columnMoved(
            PIMTableColumnModelEvent e) {
        repaint();
    }

    /**
     * 实现接口中的方法 列边缘空白变化了,要做的工作也就是重绘一下 Invoked when a column is moved due to a margin change.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by <code>PIMTable</code>.
     *
     * @param e
     *            the event received
     * @see IPIMTableColumnModelListener
     */
    public void columnMarginChanged(
            ChangeEvent e) {
        resizeAndRepaint();
    }

    // --Redrawing the header is slow in cell selection mode.
    // --Since header selection is ugly and it is always clear from the
    // --view which columns are selected, don't redraw the header.
    /**
     * 实现接口中的方法 列选中状态变化的方法 Invoked when the selection model of the <code>IPIMTableColumnModel</code> is changed. This
     * method currently has no effect (the header is not redrawn).
     * <p>
     * Application code will not use these methods explicitly, they are used internally by <code>PIMTable</code>.
     *
     * @param e
     *            the event received
     * @see IPIMTableColumnModelListener
     */
    public void columnSelectionChanged(
            ListSelectionEvent e) {
    } // repaint(); }

    //
    // Package Methods
    //

    /**
     * 创建一个缺省的列模型 Returns the default column model object which is a <code>DefaultPIMTableColumnModel</code>. A subclass
     * can override this method to return a different column model object
     *
     * @return the default column model object
     */
    protected IPIMTableColumnModel createDefaultColumnModel() {
        return new DefaultPIMTableColumnModel(table.hasEditor());
    }

    /**
     * 创建缺省的绘制器代表 eturns a default renderer to be used when no header renderer is defined by a
     * <code>PIMTableColumn</code>.
     *
     * @return 返回的是一个可用于排序的按钮对象
     */
    protected IPIMCellRenderer createDefaultRenderer() {
        return new SortButtonRenderer();
    }

    /**
     * 初始化实例中的本地变量 Initializes the local variables and properties with default values. Used by the constructor methods.
     */
    protected void initializeLocalVars() {
        // 有自己的背景色
        setOpaque(true);
        table = null;

        // 允许表格体可排序,允许各列尺寸可调整
        reorderingAllowed = true;
        resizingAllowed = true;
        draggedColumn = null;
        draggedDistance = 0;
        resizingColumn = null;
        // 允许实时绘制
        updateTableInRealTime = true;
        // 不允许由键盘事件来转移焦点
        setSurrendersFocusOnKeystroke(false);
        // I'm registered to do tool tips so we can draw tips for the
        // renderers
        // 得到工具条提示的一个实例,用于工具条提示
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(this);
        // 有缺省绘制器,单元格不可编辑
        setDefaultRenderer(createDefaultRenderer());
        setCellEditor(null);
    }

    /**
     * 尺寸调整并重绘的方法 Sizes the header and marks it as needing display. Equivalent to <code>revalidate</code> followed by
     * <code>repaint</code>.
     */
    public void resizeAndRepaint() {
        revalidate();
        repaint();
    }

    /**
     * 设置处于拖动状态的列 Sets the header's <code>draggedColumn</code> to <code>aColumn</code>.
     * <p>
     * Application code will not use this method explicitly, it is used internally by the column dragging mechanism.
     *
     * @param aColumn
     *            the column being dragged, or <code>null</code> if no column is being dragged
     */
    public void setDraggedColumn(
            PIMTableColumn aColumn) {
        draggedColumn = aColumn;
    }

    /**
     * 设置列被拖动的距离 Sets the header's <code>draggedDistance</code> to <code>distance</code>.
     * 
     * @param distance
     *            the distance dragged
     */
    public void setDraggedDistance(
            int distance) {
        draggedDistance = distance;
    }

    /**
     * 设置处于尺寸调整状态的列 Sets the header's <code>resizingColumn</code> to <code>aColumn</code>.
     * <p>
     * Application code will not use this method explicitly, it is used internally by the column sizing mechanism.
     *
     * @param aColumn
     *            the column being resized, or <code>null</code> if no column is being resized
     */
    public void setResizingColumn(
            PIMTableColumn aColumn) {
        resizingColumn = aColumn;
    }

    /**
     * 用于表格头操作:排序,列宽调整,列移动后保存操作列,以便表格视图刷新后可以显示当前列
     * 
     * @param aColumn
     *            操作列
     * @called by HeaderListener:
     */
    public void setOperatingColumn(
            int aColumn) {
        operatingColumn = aColumn;
    }

    /**
     * 用于表格头操作:排序,列宽调整,列移动后保存操作列,以便表格视图刷新后可以 显示当前列
     * 
     * @return 操作列
     * @called by PIMTableView:
     */
    public int getOperatingColumn() {
        return operatingColumn;
    }

    /**
     * 序列化时用的方法,向一个输出流中写数据 See <code>readObject</code> and <code>writeObject</code> in <code>JComponent</code> for more
     * information about serialization in Swing.
     * 
     * @param s
     *            一个对象输出流
     * @throws IOException
     *             本方法可能会抛出的<CODE></CODE>
     */
    private void writeObject(
            ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        // 如果是本类ui,就将本类载入
        if ((ui != null) && (getUIClassID().equals(uiClassID))) {
            ui.installUI(this);
        }
    }

    /**
     * 得到组件自百到左的宽度
     * 
     * @return 返回组件自左到右的宽度
     */
    private int getWidthInRightToLeft() {
        // 如果有表格且表格列宽度允许调整的情况下,返回表格宽度
        if ((table != null) && (table.getAutoResizeMode() != PIMTable.AUTO_RESIZE_OFF)) {
            return table.getWidth();
        }
        // 否则返回父类宽度
        return super.getWidth();
    }

    /**
     * 返回表示本类实例表示属性的字符串,调试用.可为空串,但不可为空 Returns a string representation of this <code>PIMTableHeader</code>. This method
     * is intended to be used only for debugging purposes, and the content and format of the returned string may vary
     * between implementations. The returned string may be empty but may not be <code>null</code>.
     * <P>
     * Overriding <code>paramString</code> to provide information about the specific new aspects of the JFC components.
     *
     * @return a string representation of this <code>PIMTableHeader</code>
     */
    protected String paramString() {
        /*
         * //允许排序 String reorderingAllowedString = (reorderingAllowed ? PropertyName.BOOLEAN_TRUE :
         * PropertyName.BOOLEAN_FALSE); //允许列宽度可调整 String resizingAllowedString = (resizingAllowed ?
         * PropertyName.BOOLEAN_TRUE : PropertyName.BOOLEAN_FALSE); //允许实时绘制 String updateTableInRealTimeString =
         * (updateTableInRealTime ? PropertyName.BOOLEAN_TRUE : PropertyName.BOOLEAN_FALSE); //把这几个串统统加在一起吧!!! return
         * super.paramString() + ",draggedDistance=" + draggedDistance + ",reorderingAllowed=" + reorderingAllowedString
         * + ",resizingAllowed=" + resizingAllowedString + ",updateTableInRealTime=" + updateTableInRealTimeString;
         */

        return CASUtility.EMPTYSTR;
    }

    // ////////////////////////////////////////////////////////////

    /**
     * 判断某单元格是否可编辑
     * 
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @return 返回准备好的组件
     */
    public boolean editCellAt(
            int row,
            int column) {
        return editCellAt(row, column, null);
    }

    /**
     * 判断某单元格是否可编辑,本方法意图的理解度还不高
     * 
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @param e
     *            一个事件对象
     * @return 返回某单元格是否可编辑的标志
     */
    public boolean editCellAt(
            int row,
            int column,
            EventObject e) {
        // 除错处理，因为在本方法被调之前，应该stopCellEditing()方法和其中会调到的setCellEdit(null)应该都已执行过。
        // 若发现，下面的条件成立，说明上述两个方法执行时出了错误！
        if (cellEditor != null && !cellEditor.stopCellEditing()) {
            return false;
        }
        // 如果该Cell是禁止编辑的则不用白费劲了。
        if (!isCellEditable(row, column)) {
            return false;
        }
        IPIMCellEditor editor = getCellEditor(column);

        // 编辑器不为空且认为该事件是让编辑的事件
        if (editor != null && editor.isCellEditable(e)) {
            // 编辑器自己准备一下
            editorComp = prepareEditor(editor, row, column);
            Rectangle rect = getHeaderRect(column);
            // 因为header的绘制器BasePIMTableHeaderUI中为了去掉表头第一行下面的一条灰线，让编辑栏提高了两个象素
            rect.y = getHeight() / 2 - 2;
            // 又为了让表头和表体中间隔一个分割条，让编辑栏的高度变矮了3个象素。
            // 此处也作相应调整。因为是边框，因而更矮一点。
            rect.height -= 3 + 1;
            // 解第一次弹出时组合框没有超出边框的缺陷
            if (editorComp instanceof JComboBox && column != getTable().getColumnCount() - 1) {
                rect.width = rect.width + 18;
            }
            // 设置一下编辑器的定位,
            editorComp.setBounds(rect);
            // 将它加载
            add(editorComp);
            // 让它显示
            editorComp.validate();
            // 设置单元格编辑器,
            setCellEditor(editor);
            // 设置处于编辑的列
            setEditingColumn(column);
            // 加单元格编辑器监听器
            editor.addCellEditorListener(this);
            // 因为要求默认切换视图后表格上没有光标,所以我伪装了一下,但之后有时会
            // 没有光标,暂时如此
            if (editorComp instanceof JTextField) {
                if (oldCaretColor != null) {
                    ((JTextField) editorComp).setCaretColor(oldCaretColor);
                }
                editorComp.addKeyListener(this);
                editorComp.addMouseListener(this);
            }
            return true;
        }
        return false;
    }

    /**
     * 用于设置焦点位置 called by: PIMTableView
     * 
     * @param row
     *            所在行
     * @param column
     *            所在列
     */
    public void initDefaultEditor(
            int row,
            int column) {
        IPIMCellEditor editor = getCellEditor(column);
        // 编辑器自己准备一下
        editorComp = prepareEditor(editor, row, column);
        Rectangle rect = getHeaderRect(column);
        // 因为header的绘制器BasePIMTableHeaderUI中为了去掉表头第一行下面的一条灰线，让编辑栏提高了两个象素
        rect.y = getHeight() / 2 - 2;
        // 又为了让表头和表体中间隔一个分割条，让编辑栏的高度变矮了3个象素。
        // 此处也作相应调整。因为是边框，因而更矮一点。
        rect.height -= 3 + 1;
        // 设置一下编辑器的定位,
        editorComp.setBounds(rect);
        // 将它加载
        add(editorComp);
        // 让它显示
        // editorComp.validate();
        // 设置单元格编辑器,
        setCellEditor(editor);
        // 设置处于编辑的列
        setEditingColumn(column);
        // 加单元格编辑器监听器
        editor.addCellEditorListener(this);
        if (editorComp instanceof JTextField) {
            ((JTextField) editorComp).setCaretColor(Color.white);
            editorComp.addKeyListener(this);
            editorComp.addMouseListener(this);
        }
        return;
    }

    /**
     * 准备一个编辑器,用于绘制 这个方法主要调用单元格编辑器的editor.getTableCellEditorComponent方法对编 编辑器组件进行设置
     * 
     * @param editor
     *            需要进行准备的单元格编辑器
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @return 返回准备好的组件
     */
    public Component prepareEditor(
            IPIMCellEditor editor,
            int row,
            int column) {
        // Object value = table.getQuickTip (table.getType ());
        // 好象是得到快速编辑栏中的值
        Object value = getValueAt(column);

        // 设一个标志
        // TODO: 以后可能要作一个处理
        boolean isSelected = true;
        // 得到编辑器上的组件
        Component comp = editor.getTableCellEditorComponent(table, value, isSelected, HEADER_ROW, column);
        // 造型处理
        if (comp instanceof JComponent) {
            JComponent jComp = (JComponent) comp;
            // 取焦点,在表格重绘后它将得到焦点
            if (jComp.getNextFocusableComponent() == null) {
                jComp.setNextFocusableComponent(this);
            }
        }
        return comp;
    }

    /**
     * 设置正处于编辑状态的列的索引
     * 
     * @param aColumn
     *            传入的列
     */
    public void setEditingColumn(
            int aColumn) {
        editingColumn = aColumn;
    }

    /**
     * 得到正处于编辑状态的列的索引
     * 
     * @return 返回正处于编辑状态的列的索引
     */
    public int getEditingColumn() {
        return editingColumn;
    }

    /**
     * 移除编辑器所用的方法
     */
    public void removeEditor() {
        IPIMCellEditor editor = getCellEditor();
        // 不为空才移除
        if (editor != null) {
            // 先移除监听器
            editor.removeCellEditorListener(this);
            // 编辑器组件不为空就将它移除
            if (editorComp != null) {
                editorComp.removeMouseListener(this);
                remove(editorComp);
            }
            // 得到该列的单元格大小
            Rectangle cellRect = getHeaderRect(editingColumn);
            // 设置单元格编辑器为空和正在编辑的列不存在
            setCellEditor(null);
            setEditingColumn(-1);
            // 编辑器组件也设为空
            editorComp = null;
            resizeAndRepaint();
            repaint(cellRect);
        }
    }

    /**
     * 得到给定索引值的列头部单元格的编辑器
     * 
     * @param column
     *            给定的列索引
     * @return 返回给定索引值的列头部单元格的编辑器
     */
    public IPIMCellEditor getCellEditor(
            int column) {
        // 得到model中该号对应的Column对象。
        PIMTableColumn tableColumn = getColumnModel().getColumn(column);
        // 得到单元格编辑器
        IPIMCellEditor editor = tableColumn.getCellEditor();
        if (editor == null) {
            // 为空的话去取缺省编辑器
            editor = table.getDefaultEditor(table.getColumnClass(column));
        }
        return editor;
    }

    /**
     * 设置当前的单元格编辑器
     * 
     * @param anEditor
     *            一个单元格编辑器对象
     */
    public void setCellEditor(
            IPIMCellEditor anEditor) {
        // 保存旧的,设置新的,激发事件,完了
        IPIMCellEditor oldEditor = cellEditor;
        cellEditor = anEditor;
        firePropertyChange("headerCellEditor", oldEditor, anEditor);
    }

    /**
     * 得到当前的单元格编辑器
     * 
     * @return 返回当前的单元格编辑器
     */
    public IPIMCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * 得到当前编辑器组件
     * 
     * @return 返回当前编辑器组件
     */
    public Component getEditorComponent() {
        return editorComp;
    }

    /**
     * 得知当前选中单元格是否可编辑
     * 
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @return 返回当前选中单元格是否可编辑的标志
     */
    public boolean isCellEditable(
            int row,
            int column) {
        // Thread.dumpStack();
        // 表格头肯定不可编辑
        if (row == 0) {
            return false;
        }
        // 快速编辑栏肯定可编辑
        else if (row == 1) {
            return true;
        }
        // 否则为假
        return false;
    }

    /**
     * 判断当前选中单元格是否在被编辑
     * 
     * @return 返回当前选中单元格是否在被编辑的标志
     */
    public boolean isEditing() {
        return (cellEditor == null) ? false : true;
    }

    /**
     * 得到点所在坐标的行索引
     * 
     * @param point
     *            一个点坐标
     * @return 返回点所在坐标的行索引
     */
    public int rowAtPoint(
            Point point) {
        int y = point.y;
        // 因为为快速编辑栏,所以用一半作除数
        int div = 1;
        if (getTable().hasEditor()) {
            div = 2;
        }
        int result = y / (getHeight() / div);
        // 出错处理
        if (result < 0) {
            return -1;
        } else if (result >= 2) {
            return -1;
        } else {
            // 返回结果
            return result;
        }
    }

    /**
     * 实现接口中的方法 当编辑取消时所要做的 This tells the listeners the editor has canceled editing
     * 
     * @param e
     *            变化事件源
     */
    public void editingCanceled(
            ChangeEvent e) {
        removeEditor();
    }

    /**
     * 实现接口中的方法 当编辑结束时所要做的 This tells the listeners the editor has ended editing
     * 
     * @param e
     *            变化事件源
     */
    public void editingStopped(
            ChangeEvent e) {
        IPIMCellEditor editor = getCellEditor();
        if (editor != null && table.hasEditor()) {
            Object value = editor.getCellEditorValue();
            setValueAt(value, editingColumn);
            removeEditor();
        }
    }

    /**
     * 得到所在列的表格头部单元格的值(其实为快速编辑栏中的值)
     * 
     * @param column
     *            所在列的索引
     * @return 返回所在列的表格头部单元格的值
     */
    public Object getValueAt(
            int column) {
        return getColumnModel().getValueAt(table.convertColumnIndexToModel(column));
    }

    /**
     * 将指定单元格设为指定值(其实为快速编辑栏中的值)
     * 
     * @param aValue
     *            指定值
     * @param column
     *            所在列
     */
    public void setValueAt(
            Object aValue,
            int column) {
        getColumnModel().setValueAt(aValue, table.convertColumnIndexToModel(column));
    }

    /**
     * 实现接口中的方法,在单元格选中状态变化时所要做的,啥也没干 Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    public void valueChanged(
            ListSelectionEvent e) {
        if (!table.hasEditor()) {
            return;
        } else {
            if (table.getColumnCount() <= 0) {
                return;
            }
        }
    }

    /**
     * 判断一个键盘事件是否为编辑键操作
     * 
     * @param event
     *            一个键盘事件源
     * @return 返回该事件是否为快捷键操作
     */
    public boolean isDirectionKey(
            KeyEvent event) {
        // 得到击键编码
        int inputKeyCode = event.getKeyCode();
        // 如果按下的键是: 方向键(上,下,左,右)
        // 翻页键(向上一页,向下一页,到页头,到页尾)
        // 在这几种情况下为真,否则为假
        if (inputKeyCode == KeyEvent.VK_LEFT || inputKeyCode == KeyEvent.VK_RIGHT || inputKeyCode == KeyEvent.VK_UP
                || inputKeyCode == KeyEvent.VK_DOWN || inputKeyCode == KeyEvent.VK_PAGE_DOWN
                || inputKeyCode == KeyEvent.VK_PAGE_UP || inputKeyCode == KeyEvent.VK_HOME
                || inputKeyCode == KeyEvent.VK_END) {
            return true;
        }
        return false;
    }

    /**
     * 把一个键盘击键映射一个键盘事件
     * 
     * @return TAB键是否处理
     */
    public boolean getSurrendersFocusOnKeystroke() {
        return surrendersFocusOnKeystroke;
    }

    /**
     * 设置是否响应键盘事件来进行焦点转移
     * 
     * @param surrendersFocusOnKeystroke
     *            是否响应键盘事件来进行焦点转移的标志
     */
    public void setSurrendersFocusOnKeystroke(
            boolean surrendersFocusOnKeystroke) {
        this.surrendersFocusOnKeystroke = surrendersFocusOnKeystroke;
    }

    /**
     * 把一个键盘事件重新派发给另一个组件(编辑器组件)
     * 
     * @param e
     *            键盘事件源
     * @param editorComponent
     *            要派发给的编辑器组件
     * @return 派发成功的标志
     */
    private boolean repostEvent(
            KeyEvent e,
            Component editorComponent) {
        // 得到接口中的组件,得到编辑器组件上的最顶层容器
        Component dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, 2, 2);
        // 没有嘛当然失败
        if (dispatchComponent == null) {
            return false;
        }
        // 如它在屏幕上就把事件传递给它
        // if (dispatchComponent.isShowing())
        {
            // ((JComponent)dispatchComponent).dispatchEvent(e);
        }
        return true;
    }

    /**
     * 保存选中列,在存盘后再次进入视图有用
     * 
     * @param col
     *            要设置的列
     */
    public void setSelectedColumnIndex(
            int col) {
        this.selectedColumn = col;
    }

    /**
     * 在存盘后再次进入视图有用
     * 
     * @return 得到选中的列
     */
    public int getSelectedColumnIndex() {
        return selectedColumn;
    }

    /**
     * 得到该键盘事件是否为编辑的快捷键
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

        // 处理回车键
        if (code == KeyEvent.VK_ENTER) {
            if (getTable().hasEditor() && getTable().getView() != null) {
                IView view = getTable().getView();
                if (view != null) {
                    // 可能要在此下断点,对"表示为"字段处理,如果该字段为空,
                    // 就要否决掉,不入数据库,响铃报警.(可能还要处理日期型)
                    if (getTable().getView().getApplication().getActiveViewInfo().getAppIndex() == ModelCons.CONTACT_APP) {
                        // 如果"表示为"字段没有值就不入数据库
                        if (PIMViewUtil.isDisplayAsHasValue(this) == 0) {
                            // “显示为”是一条联系人记录的关键字段，不能为空。请为该联系人记录的“显示为”字段输入内容。
                            SOptionPane.showErrorDialog(MessageCons.W10619);
                            return;
                        }
                    } // end veto
                      // 对任务"主题"字段处理
                    if (getTable().getView().getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP) {
                        // 如果"主题"字段没有值就不入数据库
                        if (PIMViewUtil.isSubjectAsHasValue(this) == 0) {
                            // “主题”是一条任务记录的关键字段，不能为空。请为该任务记录的“主题”字段输入内容。
                            SOptionPane.showErrorDialog(MessageCons.W10622);
                            return;
                        }
                    } // end veto
                    view.viewToModel();
                    // 暂时定为第4列
                    int anchorColumn = PIMViewUtil.getFirstTextableColumn(this);
                    setEditingColumn(anchorColumn);
                    setSelectedColumnIndex(anchorColumn);
                    if (!editCellAt(1, anchorColumn)) {
                        return;
                    }
                    getEditorComponent().requestFocus();
                    SwingUtilities.invokeLater(this);
                }
            } else if (getTable().hasEditor()) {
                removeEditor();
                // 暂时定为第0列
                Vector shiftEditorContent = ((DefaultPIMTableColumnModel) getColumnModel()).getNewRecord();
                PIMTableModelVecBased tmpModel = (PIMTableModelVecBased) getTable().getModel();
                tmpModel.insertRow(tmpModel.getRowCount(), (Vector) shiftEditorContent.clone());

                for (int i = 0; i < getTable().getColumnCount(); i++) {
                    setValueAt(null, i);
                }
                int anchorColumn = 0;
                setEditingColumn(anchorColumn);
                setSelectedColumnIndex(anchorColumn);
                if (!editCellAt(1, anchorColumn)) {
                    return;
                }
                getEditorComponent().requestFocus();
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
            int anchorRow = getTable().getSelectionModel().getAnchorSelectionIndex();
            int anchorColumn = getColumnModel().getSelectionModel().getAnchorSelectionIndex();
            // 在有效的情况下
            if (anchorRow != -1 && anchorColumn != -1 && !isEditing()) { // 不在编辑该单元格就返回
                if (!editCellAt(anchorRow, anchorColumn)) {
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
     * 处理鼠标事件
     * 
     * @param e
     *            鼠标事件源
     */
    public void processMouseEvent(
            MouseEvent e) {
        super.processMouseEvent(e);
        if (SwingUtilities.isLeftMouseButton(e) && e.getID() == MouseEvent.MOUSE_RELEASED) {
            SwingUtilities.invokeLater(this);
        }
    }

    /**
     * 得到组件的边距 Returns the insets of the border.
     * 
     * @param c
     *            the component for which this border insets value applies
     * @return 返回组件的边距
     */
    public Insets getBorderInsets(
            Component c) {
        // 边像素均为1;
        return new Insets(1, 1, 1, 1);
    }

    /**
     * 判断表格头部边框是否透明本方法负责在绘制时填充自己的背景色 Returns whether or not the border is opaque. If the border is opaque, it is
     * responsible for filling in it's own background when painting.
     * 
     * @return 返回表格头部边框是否透明的标志
     */
    public boolean isBorderOpaque() {
        return true;
    }

    /**
     * 画边框 Paints the border for the specified component with the specified position and size.
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
        // 保存旧色;设新颜色为灰色
        Color oldColor = g.getColor();
        g.setColor(Color.gray);
        // 得到本表格头部的首选尺寸
        Dimension dim = getUI().getPreferredSize(this);
        // 不知为什么,在非面板表格中只能取width,否则得到的尺寸是错的.
        // 所以作如下处理.
        if (getTable().getView() == null) {
            g.drawRect(x, y, width - 1, dim.height - 2);
        } else {
            g.drawRect(x, y, dim.width - 1, dim.height - 2);
        }
        if (table.hasEditor()) {
            g.setColor(Color.lightGray);
            g.fillRect(x, dim.height - 6 + y, dim.width - 1, 4);
        }
        // 恢复旧色
        g.setColor(oldColor);
        // 注: 传入的宽度和高度似乎无用
    }

    /**
     * 本类中的鼠标监听器是加给快速编辑栏中的当前处于激活状态(有焦点)下的编辑器组件
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
        int tmpEditingCol = editingColumn; // TODO:为什么要复制一份？下面的代码会导致editingColumn的值发生变化吗？
        // 如果是鼠标左键双击以上，则保存当前编辑列
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1) { // && rowAtPoint(e.getPoint()) == 1)
            if (getTable().getTableHeader().getEditingColumn() < 0) // 除错：说在表格体上双击会激发本事件,故做条件截获该情况。
                return;

            PIMViewInfo tmpViewInfo = getTable().getView().getApplication().getActiveViewInfo();
            int tmpAppType = tmpViewInfo.getAppIndex();
            // 如果是任务或者联系人，则将快速编辑栏中的内容放入对话盒。
            if (tmpAppType == ModelCons.CONTACT_APP || tmpAppType == ModelCons.TASK_APP) {
                // 快速输入栏是否仍然在编辑状态，如果是（不为null即说明是）则调整表示为字段，并另其退出编辑。
                IPIMCellEditor tmpCellEditor = table.getTableHeader().getCellEditor();
                PIMViewUtil.processDisplayAsField(this);
                if (tmpCellEditor != null)
                    tmpCellEditor.stopCellEditing(); // @NOTE:和Table体的停止编辑不同，表格头的停止编辑不导致存盘；
                                                     // 仅仅是把内容放到表格头的数组中。
                table.getTableHeader().removeEditor();

                int[] tmpIndexes = CASControl.ctrl.getModel().getFieldNameIndex(tmpViewInfo);
                Vector tmpValuesVec = ((DefaultPIMTableColumnModel) getColumnModel()).getNewRecord();
                for (int i = tmpValuesVec.size() - 1; i >= 0; i--)
                    // 因为显示为有个空字串，导致打tab键后，显示为不会变化，这里给个空
                    if (CASUtility.EMPTYSTR.equals(tmpValuesVec.get(i)))
                        tmpValuesVec.set(i, null);
                PIMRecord tmpCurRecord = new PIMRecord();
                Hashtable tmpValueHash = new Hashtable();
                tmpCurRecord.setFieldValues(tmpValueHash);
                tmpCurRecord.setAppIndex(tmpAppType);
                tmpCurRecord.setRecordID(-1);
                Object tmpCurValue = null;

                for (int i = 0; i < tmpIndexes.length; i++) { // 第一个是ID,设了也没有用
                    tmpCurValue = PIMViewUtil.getValueForSaveToDB(tmpAppType, tmpIndexes[i], tmpValuesVec.get(i));
                    if (tmpCurValue != null)
                        tmpValueHash.put(PIMPool.pool.getKey(tmpIndexes[i]), tmpCurValue);
                }
                new NewObjectAction(tmpCurRecord).actionPerformed(null);
                // if (tmpViewInfo.getAppType() == ModelConstants.CONTACT_APP){
                // tmpValueHash.put(PIMPool.pool.getIntegerKey(ModelDBConstants.TYPE),new
                // Short((short)0));//PIMPool.pool.getIntegerKey(0));
                // if (tmpValueHash.size() == 1) //弹出新建联系人对话盒
                // new NewContactAction().actionPerformed(null);
                // else
                // new NewContactAction(tmpCurRecord).actionPerformed(null);
                // }
                // else if (tmpViewInfo.getAppType() == ModelConstants.TASK_APP){
                // //弹出新建任务对话盒
                // if (tmpValueHash.size() == 0)
                // new NewTaskAction(false).actionPerformed(null);
                // else
                // new NewTaskAction(tmpCurRecord,false).actionPerformed(null);
                // }

                setDraggedColumn(null); // 清空header中被拖动列的引用。

                setResizingColumn(null);// 清空header中被改尺寸列的引用。
                setDraggedDistance(0);
                // removeEditor();
                int columnCount = getTable().getColumnCount();
                for (int i = 0; i < columnCount; i++)
                    setValueAt(null, i);
                // *
                setEditingColumn(tmpEditingCol);
                setSelectedColumnIndex(tmpEditingCol);
                if (!editCellAt(1, tmpEditingCol))
                    return;
                requestFocus();
                getEditorComponent().requestFocus();
                getCellEditor().getTableCellEditorComponent(table, null, true, 1, tmpEditingCol);
                // */
                repaint();
            }
            // e.consume();
        } else if (SwingUtilities.isRightMouseButton(e)) // 如果是右键事件，则弹右键菜单。
            processRightMenu(e);
    }

    /**
     * 弹出右键菜单 给编辑器组件用的
     * 
     * @param e
     *            鼠标事件源
     */
    private void processRightMenu(
            MouseEvent e) {
        // 是一个可消耗事件或者是非鼠标左键按下且表格头被禁止
        // return e.isConsumed() || (!(SwingUtilities.isLeftMouseButton(e) && header.isEnabled()));
        IView view = getTable().getView();
        if (view != null) {
            if (view.getApplication() != null) {
                Component comp = getEditorComponent(); // .getTableCellEditorComponent(table, value, true,
                // HEADER_ROW, column);
                // 以下这个判断表示是在表格体中监听到这个事件,此时comp为空
                if (comp == null) {
                    return;
                }
                Point tmpPoint = SwingUtilities.convertPoint(comp, e.getPoint(), this);
                view.getApplication().showPopupMenu(this, (int) tmpPoint.getX(), (int) tmpPoint.getY());
            }
        }
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
     * pressed event.
     * 
     * @param e
     *            键盘事件
     */
    public void keyPressed(
            KeyEvent e) {
        if (e.getSource() == getEditorComponent() && getEditorComponent() instanceof JTextField
                && oldCaretColor != null) {
            ((JTextField) editorComp).setCaretColor(oldCaretColor);
        }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
     * released event.
     * 
     * @param e
     *            键盘事件
     */
    public void keyReleased(
            KeyEvent e) {
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
     * event.
     * 
     * @param e
     *            键盘事件
     */
    public void keyTyped(
            KeyEvent e) {
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p>
     * 这个方法是用于使表格上层的滚动组件绘制其网格线用的 The general contract of the method <code>run</code> is that it may take any action
     * whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        PIMScrollPane scrollPane = (PIMScrollPane) getParent().getParent();
        scrollPane.repaint();
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        columnModel.removeColumnModelListener(this);
        removeMouseListener(headerList);
        table = null;
    }

    /**
     * 保存对<CODE>PIMTable</CODE>表格的引用,缺省为空 The table for which this object is the header; the default is
     * <code>null</code>.
     */
    protected PIMTable table;

    /**
     * 保存 <code>IPIMTableColumnModel</code> 表格列模型的引用 The <code>IPIMTableColumnModel</code> of the table header.
     */
    protected IPIMTableColumnModel columnModel;

    /**
     * 保存用户可否调整表格列尺寸的标志 If true, reordering of columns are allowed by the user; the default is true.
     */
    protected boolean reorderingAllowed;

    /**
     * 头部排序监听器的引用,移除用
     */
    private HeaderListener headerList;

    /**
     * 保存用户可否移动表格列的标志 If true, resizing of columns are allowed by the user; the default is true.
     */
    protected boolean resizingAllowed;

    /**
     * 废弃的一个标志,用来表示表格是否允许实时绘制的标志 Obsolete as of Java 2 platform v1.3. Real time repaints, in response to column dragging
     * or resizing, are now unconditional.
     */
    /*
     * If this flag is true, then the header will repaint the table as a column is dragged or resized; the default is
     * true.
     */
    protected boolean updateTableInRealTime;

    /**
     * 保存正在被调整尺寸的列的索引,为空表示没有列在被操作 The index of the column being resized. <code>null</code> if not resizing.
     */
    transient protected PIMTableColumn resizingColumn;

    /**
     * 保存正在被拖动的列的索引,为空表示没有列在被拖动 The index of the column being dragged. <code>null</code> if not dragging.
     */
    transient protected PIMTableColumn draggedColumn;

    /**
     * 表示列被拖动的距离 The distance from its original position the column has been dragged.
     */
    transient protected int draggedDistance;

    /**
     * 保存<code>PIMTableColumn</code>列缺省的绘制器, 因为列本身不定义一个表格头部绘制器 The default renderer to be used when a
     * <code>PIMTableColumn</code> does not define a <code>headerRenderer</code>.
     */
    private IPIMCellRenderer defaultRenderer;
    /**
     * 保存表格单元格的绘制器,用来为快速编辑栏用
     */
    transient protected IPIMCellEditor cellEditor;
    /**
     * 被编辑器列的索引
     */
    transient protected int editingColumn;
    /**
     * 保存编辑器组件的引用
     */
    transient protected Component editorComp;
    /**
     * 用于编辑器的绘制
     */
    public static final int HEADER_ROW = -10;
    /**
     * 保存可否通过键盘动作来转移焦点的标志
     */
    private boolean surrendersFocusOnKeystroke;

    /**
     * 用于表格头操作:排序,列宽调整,列移动后保存操作列,以便表格视图刷新后可以
     */
    private int operatingColumn;

    /**
     * 这个变量以后要有更好的处理方法
     */
    static Color oldCaretColor = new JTextField().getCaretColor();

    /**
     * 保存选中列,在存盘后再次进入视图有用
     */
    private int selectedColumn = -1;
}
