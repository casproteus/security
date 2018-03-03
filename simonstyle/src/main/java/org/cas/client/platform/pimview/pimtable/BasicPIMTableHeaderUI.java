package org.cas.client.platform.pimview.pimtable;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.CellEditor;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.calendar.CalendarCombo;
import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.cascontrol.frame.action.NewObjectAction;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.util.PIMViewUtil;

/**
 * 本类用于负责表格头部的绘制和一些用户动作的处理
 * <p>
 * @NOTE:本类和BasicPIMtableUI类，被改为非public后发现运行时getUI()返回null，原因未明。
 */

public class BasicPIMTableHeaderUI extends PIMTableHeaderUI {
    //
    // Factory methods for the Listeners
    //
    /**
     * 本方法用创建负责鼠标动作的监听句柄 Creates the mouse listener for the PIMTable.
     * 
     * @return 鼠标监听器
     */
    protected MouseInputHandler createMouseInputListener() {
        return new MouseInputHandler(this);
    }

    /**
     * 创建一个键盘监听器的实例 Creates the key listener for handling keyboard navigation in the PIMTable.
     * 
     * @return 返回一个键盘监听器的实例
     */
    // protected KeyListener createKeyListener()
    // {
    // return new KeyHandle();
    // }

    //
    // The installation/uninstall procedures and support
    //
    /**
     * 继承了一个UI所必须重载的方法,八股文 创建UI
     * 
     * @return UI
     * @param h
     *            要使用本UI的组件
     */
    public static ComponentUI createUI(
            JComponent h) {
        return new BasicPIMTableHeaderUI();
    }

    // Installation
    /**
     * 继承了一个UI所必须重载的方法,八股文 装载UI
     * 
     * @param c
     *            要使用本UI的组件
     */
    public void installUI(
            JComponent c) {
        // 保存要使用本UI的组件的引用
        header = (PIMTableHeader) c;

        rendererPane = new CellRendererPane();
        // 使得该组件可使用单元格绘制功能
        header.add(rendererPane);
        // 装载缺省设置
        installDefaults();
        // 装载所有监听器和键盘动作
        installListeners();
        installKeyboardActions();
    }

    /**
     * 加载缺省设置 Initialize PIMTableHeader properties, e.g. font, foreground, and background. The font, foreground, and
     * background properties are only set if their current value is either null or a UIResource, other properties are
     * set if the current value is null.
     *
     * @see #installUI
     */
    protected void installDefaults() {
        // 为要使用本UI的组件设置几个观感
        LookAndFeel
                .installColorsAndFont(header, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
    }

    /**
     * Attaches listeners to the PIMTableHeader. 加载监听器
     */
    protected void installListeners() {
        // 新建出鼠标监听器的实例
        mouseInputHandler = createMouseInputListener();
        // keyListener = createKeyListener();
        // 加载
        header.addMouseListener(mouseInputHandler);
        header.addMouseMotionListener(mouseInputHandler);
        // header.addKeyListener(keyListener);
    }

    /**
     * 用于给编辑器组件,使得双击快速编辑栏能够弹出联系人对话盒. called by : PIMTableHeader
     * 
     * @return 鼠标监听器
     */
    public MouseInputHandler getMouseListener() {
        return mouseInputHandler;
    }

    /**
     * Register all keyboard actions on the PIMTableHeader. 注册所有键盘动作
     */
    protected void installKeyboardActions() {
        // 得到键盘动作映射
        ActionMap map = getActionMap();
        // 为表格头设置该键盘动作映射
        SwingUtilities.replaceUIActionMap(header, map);
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SwingUtilities.replaceUIInputMap(header, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
    }

    // //////////////////////////////////////////////////////////////////////////
    // ////////////////// 这个方法是杨志南加的//////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////

    /**
     * 得到输入映射
     * 
     * @param condition
     *            条件,是一个整形的ID值
     * @return 返回输入映射
     */
    InputMap getInputMap(
            int condition) {
        // 是这么个条件就做
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            // 先得到键盘映射
            InputMap keyMap = (InputMap) UIManager.get("Table.ancestorInputMap");
            InputMap rtlKeyMap;
            // 如果组件是自左到右或父类也是,就将其返回
            if (header.getComponentOrientation().isLeftToRight()
                    || ((rtlKeyMap = (InputMap) UIManager.get("Table.ancestorInputMap.RightToLeft")) == null)) {
                return keyMap;
            }
            // 否则使用父类的映射
            else {
                rtlKeyMap.setParent(keyMap);
                return rtlKeyMap;
            }
        }
        return null;
    }

    /**
     * 得到键盘动作映射的方法
     * 
     * @return 动作映射表
     */
    ActionMap getActionMap() {
        // 创建出其实例
        ActionMap map = createActionMap();
        // 为观感设置该项
        if (map != null) {
            UIManager.getLookAndFeelDefaults().put("TableHeader.actionMap", map);
        }
        return map;
    }

    /**
     * 得到键盘动作映射的方法,主要是为了加入"cancel"动作
     * 
     * @return 动作映射表
     */
    ActionMap createActionMap() {
        // 创建出其实例
        ActionMap map = new ActionMapUIResource();

        // 加入"cancel"动作和"Tab"动作
        map.put("cancel", new CancelEditingAction());
        map.put("selectNextColumn", new NavigationalAction(1, 0, false, false, false));
        map.put("selectPreviousColumn", new NavigationalAction(-1, 0, false, false, false));
        map.put("selectNextRow", new NavigationalAction(0, -2, false, false, false));
        map.put("selectPreviousRow", new NavigationalAction(0, -1, false, false, false));

        map.put("selectNextColumnExtendSelection", new NavigationalAction(1, 0, false, true, false));
        map.put("selectPreviousColumnExtendSelection", new NavigationalAction(-1, 0, false, true, false));
        map.put("selectNextRowExtendSelection", new NavigationalAction(0, 1, false, true, false));
        map.put("selectPreviousRowExtendSelection", new NavigationalAction(0, -1, false, true, false));

        map.put("scrollUpChangeSelection", new PagingAction(this, false, false, true, false));
        map.put("scrollDownChangeSelection", new PagingAction(this, false, true, true, false));
        map.put("selectFirstColumn", new PagingAction(this, false, false, false, true));
        map.put("selectLastColumn", new PagingAction(this, false, true, false, true));

        map.put("scrollUpExtendSelection", new PagingAction(this, true, false, true, false));
        map.put("scrollDownExtendSelection", new PagingAction(this, true, true, true, false));
        map.put("selectFirstColumnExtendSelection", new PagingAction(this, true, false, false, true));
        map.put("selectLastColumnExtendSelection", new PagingAction(this, true, true, false, true));

        map.put("selectFirstRow", new PagingAction(this, false, false, true, true));
        map.put("selectLastRow", new PagingAction(this, false, true, true, true));

        map.put("selectFirstRowExtendSelection", new PagingAction(this, true, false, true, true));
        map.put("selectLastRowExtendSelection", new PagingAction(this, true, true, true, true));

        map.put("selectNextColumnCell", new NavigationalAction(1, 0, true, false, true));
        map.put("selectPreviousColumnCell", new NavigationalAction(-1, 0, true, false, true));
        map.put("selectNextRowCell", new NavigationalAction(0, 1, true, false, true));
        map.put("selectPreviousRowCell", new NavigationalAction(0, -1, true, false, true));

        // 返回该映射
        return map;
    }

    // Uninstall methods
    /**
     * 继承了一个UI所必须重载的方法,八股文 卸掉UI
     * 
     * @param c
     *            传入的组件
     */
    public void uninstallUI(
            JComponent c) {
        // 卸掉设置的观感
        uninstallDefaults();
        // 卸掉所有监听器和键盘动作
        uninstallListeners();
        uninstallKeyboardActions();

        // 清空所有变量
        header.remove(rendererPane);
        rendererPane = null;
        header = null;
    }

    /**
     * 卸掉缺省设置
     *
     * @see #installDefaults
     */
    protected void uninstallDefaults() {
    }

    /**
     * 卸掉所有监听器
     */
    protected void uninstallListeners() {
        // 移除
        header.removeMouseListener(mouseInputHandler);
        header.removeMouseMotionListener(mouseInputHandler);
        // header.removeKeyListener(keyListener);
        // 清空
        mouseInputHandler = null;
        // keyListener = null;
    }

    /**
     * 卸掉所有监听器
     */
    protected void uninstallKeyboardActions() {
        // 调用SwingUtilities 工具,扔了它
        SwingUtilities.replaceUIActionMap(header, null);
    }

    //
    // Paint Methods and support
    //
    /**
     * 在组件上绘制...
     * 
     * @param g
     *            图形设备上下文
     * @param c
     *            待绘制的组件
     */
    public void paint(
            Graphics g,
            JComponent c) {
        if (header.getColumnModel().getColumnCount() <= 0)// 如果列数少于1，则不进行任何绘制。
            return;

        IPIMTableColumnModel tmpColModel = header.getColumnModel();

        // 得到需要绘制的起始和终止列的列号。
        Rectangle tmpClipBounds = g.getClipBounds();// 取得需要绘制的区域。
        Point tmpLeft = tmpClipBounds.getLocation();// 需要绘制区域左上角的坐标。
        Point tmpRight = new Point(tmpClipBounds.x + tmpClipBounds.width - 1, tmpClipBounds.y);// 得到需要绘制的区域的右上角的坐标。
        int tmpColMin = header.columnAtPoint(tmpLeft);
        int tmpColMax = header.columnAtPoint(tmpRight); // 至此得到需要绘制的最左边和最右边的列的列号。
        // 出错处理-----------------------------------------------------------------------------
        if (tmpColMin == -1)
            tmpColMin = 0;
        if (tmpColMax == -1)
            tmpColMax = tmpColModel.getColumnCount() - 1;
        // --------------------------------------------------------------------------------------

        PIMTableColumn tmpDraggedColumn = header.getDraggedColumn();// 得到正在被拖动的表头中的列的实例。

        int tmpColMargin = tmpColModel.getColumnMargin();// 得到表头的边界宽度。
        Rectangle tmpCellRect = header.getHeaderRect(tmpColMin);// 得到最左边一列上文字区域。
        int tmpColWidth;// 声明一个用于暂存列宽的变量。
        PIMTableColumn tmpCol;// 声明一个列。

        int tmpRowCountInHeader = header.getTable().hasEditor() ? 2 : 1;// 得到表头中显示的行数。
        for (int i = 0; i < tmpRowCountInHeader; i++) {
            tmpCellRect = header.getHeaderRect(i, tmpColMin, false);
            if (i == 1) {
                // 此处如果不设，cellRect.y的值是columnHeight的二分之一，然而视图中表头下方有一个象素宽的灰线。
                // 再加上编辑器的border和其边界间也有一个象素的距离，所以这里将第二列的高度提高2，以使界面美观。
                tmpCellRect.y -= 2;
                // 高度变矮三个象素，为了留出一个浅灰条，和灰色border组成一个分隔条。
                tmpCellRect.height = tmpCellRect.height - 3; // Note：PIMTableHeader类的editCellAt()方法中的Border绘制时，亦应该如此调整。
            }
            // 开始循环绘制
            for (int column = tmpColMin; column <= tmpColMax; column++)
            // for(int column = cMax; column >= cMin ; column--)
            {
                // 得到列,取其宽
                tmpCol = tmpColModel.getColumn(column);
                tmpColWidth = tmpCol.getWidth();
                // 得到实际宽度
                tmpCellRect.width = tmpColWidth - tmpColMargin;
                // 是第一行宽度再加3
                if (i == 0) {
                    tmpCellRect.width = tmpCellRect.width + 3;
                }
                // 是第二行宽度只加1
                else if (i == 1) {
                    tmpCellRect.width = tmpCellRect.width + 1;
                }

                // 因为有此筛选条件的话，选中一个表头的第一行时，它的第二行（编辑栏）
                // 会因为不被绘制而变成灰色，与MS不同，测试者可能会认为是Bug，故改之。
                // if (aColumn != draggedColumn)
                {
                    // 是第一行就正常绘制
                    if (i == 0) {
                        paintCell(g, tmpCellRect, 0, column);
                    }
                    // 是第二行就绘制表格的快速编辑栏
                    else if (i == 1) {
                        paintCell(g, tmpCellRect, PIMTableHeader.HEADER_ROW, column);
                    }
                }
                // X坐标加上偏移
                tmpCellRect.x += tmpColWidth;
            }
        }
        /*
         * if (header.getTable ().hasEditor ()) { cellRect.x = 0; cellRect.y = columnHeight / 2; for(int column = cMin;
         * column <= cMax ; column++) { aColumn = cm.getColumn (column); columnWidth = aColumn.getWidth ();
         * //columnHeight = header.getHeight (); cellRect.width = columnWidth - columnMargin; if (aColumn !=
         * draggedColumn) { paintCell (g, cellRect, PIMTableHeader.HEADER_ROW, column); } cellRect.x += columnWidth; } }
         */

        // Paint the dragged column if we are dragging.
        // 绘制处于拖放中的Column。
        // 确保
        if (tmpDraggedColumn != null) {
            // 得到拖动列的索引
            int draggedColumnIndex = viewIndexForColumn(tmpDraggedColumn);
            // 得到拖动列的单元格区域
            Rectangle draggedCellRect = header.getHeaderRect(draggedColumnIndex);

            // Draw a gray well in place of the moving column.
            // 把该列颜色设为灰色以表示此列在拖动
            g.setColor(header.getParent().getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);

            // X坐标设置为原点加上偏移
            draggedCellRect.x += header.getDraggedDistance();

            // Fill the background.
            // 填充背景
            g.setColor(header.getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);
            // 绘制
            paintCell(g, draggedCellRect, draggedColumnIndex);
        }

        // 清空 rendererPane 绘制面板上的所有组件 Remove all components in the rendererPane.
        rendererPane.removeAll();
    }

    /**
     * 得到表格头绘制器
     * 
     * @param columnIndex
     *            所在列的索引值
     * @return 列头绘制器
     * @NOTE:提高可见性，以追求更好的性能。
     */
    Component getHeaderRenderer(
            int columnIndex) {
        // 根据索引值取得该列
        PIMTableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        // 得到绘制器
        IPIMCellRenderer renderer = aColumn.getHeaderRenderer();
        // 为空从表格头中取缺省绘制器
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        // 返回绘制器组件(没有被选中,没有焦点)
        return renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1,
                columnIndex);
    }

    /**
     * 负责绘制处于拖放中的Column。 Deprecated
     * 
     * @param g
     *            图形设备上下文
     * @param prmCellRect
     *            绘制区域
     * @param prmColumnIndex
     *            所在列的索引值
     */
    private void paintCell(
            Graphics g,
            Rectangle prmCellRect,
            int prmColumnIndex) {
        // 取表格头绘制器
        Component component = getHeaderRenderer(prmColumnIndex);
        // 在绘制面板上绘制出来
        rendererPane.paintComponent(g, component, header, prmCellRect.x, prmCellRect.y, prmCellRect.width,
                prmCellRect.height, true);
    }

    /**
     * 用于绘制处于非拖放状态的表头和编辑区。
     * 
     * @param g
     *            图形设备上下文
     * @param prmCellRect
     *            绘制区域
     * @param prmRow
     *            所在行的索引值
     * @param prmColumnIndex
     *            所在列的索引值
     */
    private void paintCell(
            Graphics g,
            Rectangle prmCellRect,
            int prmRow,
            int prmColumnIndex) {
        // 如果该Cell处于编辑状态。
        if (header.isEditing() && header.getEditingColumn() == prmColumnIndex && prmRow == PIMTableHeader.HEADER_ROW)
        // && header.getColumnModel().getColumn(prmColumnIndex).getEditorEnable())
        {
            // 得到编辑器组件
            Component component = header.getEditorComponent();
            // 设置其区域
            Rectangle rect = (Rectangle) prmCellRect.clone();
            // if (prmColumnIndex == 2)
            // 在outlook中，它的下拉框编辑器显示区域一般向右突破，至箭头按钮空在外面
            // 所以加上一个延伸以保持显示效果与微软一致
            if (component instanceof JComboBox && prmColumnIndex != header.getTable().getColumnCount() - 1) {
                rect.width = rect.width + 18;
            }
            component.setBounds(rect);
            // Thread.dumpStack();
            // 使得重绘
            component.validate();
        } else // 如果该Cell处于非编辑状态。
        {
            // 得到绘制器（是谁的？头还是编辑栏）。
            Component component = getHeaderRenderer(prmColumnIndex);
            // 是编辑栏的,这个 IF 是用来重新指向编辑栏的绘制组件
            if (header.getTable().hasEditor() && prmRow == PIMTableHeader.HEADER_ROW) {
                PIMTable table = header.getTable();
                // 从表格中得到编辑栏的绘制组件
                IPIMCellRenderer renderer = table.getCellRenderer(-1, prmColumnIndex);
                // 确保一下
                if (renderer == null) {
                    // 得到缺省的绘制组件
                    renderer = table.getDefaultRenderer(table.getColumnClass(prmColumnIndex));
                }
                // 得到它,my baby
                component =
                        renderer.getTableCellRendererComponent(table, header.getValueAt(prmColumnIndex), false, false,
                                -1, prmColumnIndex);
            }
            // 那就绘吧
            rendererPane.paintComponent(g, component, header, prmCellRect.x, prmCellRect.y, prmCellRect.width,
                    prmCellRect.height, true);
        }
    }

    /**
     * 取某一列的列索引
     * 
     * @param aColumn
     *            待询问的列
     * @return 索引值
     * @NOTE:提高可见性，以追求更好的性能。
     */
    int viewIndexForColumn(
            PIMTableColumn aColumn) {
        // 从表格头中取列模型
        IPIMTableColumnModel cm = header.getColumnModel();
        // 按列数遍历
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                // 找到就直接返回
                return column;
            }
        }
        // 没有就返回-1
        return -1;
    }

    //
    // Size Methods
    //
    /**
     * 得到表格头高度的方法
     * 
     * @return 高度
     */
    private int getHeaderHeight() {
        int height = 0;
        // 定义缺省容纳初始为假
        boolean accomodatedDefault = false;
        // 得到列模型
        IPIMTableColumnModel columnModel = header.getColumnModel();
        // 按列数遍历
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            PIMTableColumn aColumn = columnModel.getColumn(column);
            // 如果这列有绘制器或行高大于0(好象是一个优化的算法)
            if (aColumn.getHeaderRenderer() != null || !accomodatedDefault) {
                // 取绘制器
                Component comp = getHeaderRenderer(column);
                // 取绘制器的行高
                int rendererHeight = comp.getPreferredSize().height;
                // 遍历出一个最大高度
                height = Math.max(height, rendererHeight);
                if (rendererHeight > 0) {
                    // 表示绘制器高度一定大于零
                    accomodatedDefault = true;
                }
            }
        }
        // 表格头有编辑器的话再加2个像素保险(Inset)
        if (header.getTable().hasEditor()) {
            height *= 2;
        }
        return height;
    }

    /**
     * 创建表格头尺寸的方法
     * 
     * @param width
     *            设计表格头宽度
     * @return 表格头尺寸
     */
    private Dimension createHeaderSize(
            long width) {
        // None of the callers include the intercell spacing, do it here.
        // 取得可用的表格头宽度
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        // 以此新建一个返回
        return new Dimension((int) width, getHeaderHeight());
    }

    /**
     * 重载父类中的方法 取组件的最小尺寸 所有尺寸:最大尺寸,最小尺寸,首选尺寸 三个都一样,定死的
     * 
     * @return 最小尺寸
     * @param c
     *            要访问的组件 Return the minimum size of the header. The minimum width is the sum of the minimum widths of
     *            each column (plus inter-cell spacing).
     */
    public Dimension getMinimumSize(
            JComponent c) {
        long width = 0;
        // 得到所有列
        Enumeration enumeration = header.getColumnModel().getColumns();
        // 遍历
        while (enumeration.hasMoreElements()) {
            PIMTableColumn aColumn = (PIMTableColumn) enumeration.nextElement();
            // 累加宽度
            width = width + aColumn.getMinWidth();
        }
        // 返回表格头宽度
        return createHeaderSize(width);
    }

    /**
     * 重载父类中的方法 取组件的首选尺寸 所有尺寸:最大尺寸,最小尺寸,首选尺寸 三个都一样,定死的
     * 
     * @param c
     *            要访问的组件
     * @return 首选尺寸
     */
    public Dimension getPreferredSize(
            JComponent c) {
        long width = 0;
        // 得到所有列
        Enumeration enumeration = header.getColumnModel().getColumns();
        // 遍历
        while (enumeration.hasMoreElements()) {
            PIMTableColumn aColumn = (PIMTableColumn) enumeration.nextElement();
            // 累加宽度
            width = width + aColumn.getPreferredWidth();
        }
        // 返回表格头宽度
        return createHeaderSize(width);
    }

    /**
     * 重载父类中的方法 取组件的首选尺寸 所有尺寸:最大尺寸,最小尺寸,首选尺寸 三个都一样,定死的 Return the maximum size of the header. The maximum width is the
     * sum of the maximum widths of each column (plus inter-cell spacing).
     * 
     * @param c
     *            PIMTable
     * @return 最大尺寸
     */
    public Dimension getMaximumSize(
            JComponent c) {
        long width = 0;
        // 得到所有列
        Enumeration enumeration = header.getColumnModel().getColumns();
        // 遍历
        while (enumeration.hasMoreElements()) {
            PIMTableColumn aColumn = (PIMTableColumn) enumeration.nextElement();
            // 累加宽度
            width = width + aColumn.getMaxWidth();
        }
        // 返回表格头宽度
        return createHeaderSize(width);
    }

    /** 保存列调整时所使用的鼠标形状. */
    static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    //
    // Instance Variables
    //

    /** 保存表格头 The PIMTableHeader that is delegating the painting to this UI. */
    protected PIMTableHeader header;
    /** 保存单元格绘制面板 The PIMTableHeader that is delegating the painting to this UI. */
    protected CellRendererPane rendererPane;

    /**
     * 保存鼠标动作监听器 Listeners that are attached to the PIMTable
     */
    protected MouseInputHandler mouseInputHandler;

    // 保存键盘动作监听器
    // private KeyListener keyListener;

    // ========================================================================================================================
    /**
     * 本类用于放弃编辑动作 Esc Action Action to invoke <code>removeEditor</code> on the table.
     */
    public static class CancelEditingAction extends AbstractAction implements Runnable {
        /**
         * 对头部的引用
         */
        PIMTableHeader header;

        /**
         * 实现父类(抽象类)中的抽象方法,执行一个动作
         * 
         * @param e
         *            动作事件
         */
        public void actionPerformed(
                ActionEvent e) {
            // 取得表格头
            header = (PIMTableHeader) e.getSource();
            // 从中移除编辑器
            header.removeEditor();
            SwingUtilities.invokeLater(this);
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
         * causes the object's <code>run</code> method to be called in that separately executing thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may take any action whatsoever.
         *
         * @see java.lang.Thread#run()
         */
        public void run() {
            Vector tmpRecord = ((DefaultPIMTableColumnModel) header.getColumnModel()).getNewRecord();
            for (int i = 0, count = tmpRecord.size(); i < count; i++) {
                tmpRecord.set(i, null);
            }
            // 处理"取消"动作后的焦点问题
            if (header.getTable().hasEditor()) {
                int anchorRow = 1;
                // 下面要去得到第一个可进行字符操作的编辑器单元所在的列的索引
                int anchorColumn = PIMViewUtil.getFirstTextableColumn(header);
                if (anchorColumn == -1) {
                    anchorColumn = 1;
                }
                header.setEditingColumn(anchorColumn);
                header.setSelectedColumnIndex(anchorColumn);
                header.initDefaultEditor(anchorRow, anchorColumn);
                /*
                 * if (!header.editCellAt(anchorRow, anchorColumn)) { return; } //
                 */
                header.getEditorComponent().requestFocus();
                // Vector tmpNewRecord = ((DefaultPIMTableColumnModel)header.getColumnModel()).getNewRecord();
                // tmpNewRecord.setElementAt(null,anchorColumn);

                IPIMCellEditor cellEditor = header.getCellEditor();
                cellEditor.getTableCellEditorComponent(header.getTable(), null, true, anchorRow, anchorColumn);
                // table.getTableHeader().repaint();
            }
            header.repaint();
        }

    }

    // ==========================================================================================================================
    /**
     * 本类用于实现鼠标所有动作 This inner class is marked &quot;public&quot; due to a compiler bug. This class should be treated as
     * a &quot;protected&quot; inner class. Instantiate it only within subclasses of BasicTableUI.
     */
    public static class MouseInputHandler implements MouseListener, MouseMotionListener {
        /**
         * 对本类的引用
         */
        BasicPIMTableHeaderUI headerUI;
        /** 要把鼠标事件派发给的组件 */
        private Component dispatchComponent;
        /** 表示"鼠标是否按下选择"这个状态 */
        private boolean selectedOnPress;
        /** 保存鼠标X坐标偏移的像素数 */
        private int mouseXOffset;
        /** 保存列调整时组件上所显示的鼠标光标 */
        private Cursor otherCursor = resizeCursor;

        /**
         * 本字段用于尺寸调整后自动保存调整的尺寸，以便下次在面板上显示出本表格的 实例时使用调整后的宽度尺寸
         */
        private boolean isResizingAccidentOccur;

        /**
         * 构建器,传入引用
         * 
         * @param headerUI
         *            传入引用
         */
        public MouseInputHandler(BasicPIMTableHeaderUI headerUI) {
            this.headerUI = headerUI;
        }

        /**
         * 实现 mouseInputHandler 接口中的方法 表示鼠标点击,在本类中用于处理双击弹出编辑对话盒
         * 
         * @param e
         *            鼠标事件源
         */
        public void mouseClicked(
                MouseEvent e) {
            // 如果是在表头的第二行，左键双击或双击以上。
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1
                    && headerUI.header.rowAtPoint(e.getPoint()) == 1) {
                int anchorColumn = headerUI.header.editingColumn;
                PIMViewInfo tmpViewInfo = headerUI.header.getTable().getView().getApplication().getActiveViewInfo();
                int tmpActiveAppType = tmpViewInfo.getAppIndex();
                //
                IPIMCellEditor cellEditor = headerUI.header.getCellEditor();
                PIMViewUtil.processDisplayAsField(headerUI.header);
                if (cellEditor != null) {
                    cellEditor.stopCellEditing();
                }

                headerUI.header.removeEditor();

                int[] indexes = CASControl.ctrl.getModel().getFieldNameIndex(tmpViewInfo);
                Vector newRecord = ((DefaultPIMTableColumnModel) headerUI.header.getColumnModel()).getNewRecord();
                PIMRecord record = new PIMRecord();
                int appType = tmpViewInfo.getAppIndex();
                record.setAppIndex(appType);
                Hashtable rs = new Hashtable();
                record.setFieldValues(rs);
                record.setRecordID(-1);
                // 第一个是ID,设了也没有用
                for (int i = 0; i < indexes.length; i++) {
                    Object value = newRecord.get(i);
                    value = PIMViewUtil.getValueForSaveToDB(appType, indexes[i], value);
                    if (value != null) {
                        rs.put(PIMPool.pool.getKey(indexes[i]), value);
                    }
                }
                rs.put(PIMPool.pool.getKey(ContactDefaultViews.TYPE), PIMPool.pool.getKey(0));

                new NewObjectAction(record).actionPerformed(null);

                // 清空header中被拖动列的引用。
                headerUI.header.setDraggedColumn(null);
                // 清空header中被改尺寸列的引用。
                headerUI.header.setResizingColumn(null);
                headerUI.header.setDraggedDistance(0);
                // removeEditor();
                int columnCount = headerUI.header.getTable().getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    headerUI.header.setValueAt(null, i);
                }
                // *
                headerUI.header.setEditingColumn(anchorColumn);
                headerUI.header.setSelectedColumnIndex(anchorColumn);
                if (!headerUI.header.editCellAt(1, anchorColumn)) {
                    return;
                }
                headerUI.header.requestFocus();
                headerUI.header.getEditorComponent().requestFocus();
                IPIMCellEditor tmpCellEditor = headerUI.header.getCellEditor();
                tmpCellEditor.getTableCellEditorComponent(headerUI.header.getTable(), null, true, 1, anchorColumn);
                // */
                headerUI.header.repaint();
            }
        }

        /**
         * 本方法用于设置派发事件的组件,用于编辑
         * 
         * @param e
         *            鼠标事件源
         */
        private void setDispatchComponent(
                MouseEvent e) {
            // 得到表格头的编辑组件
            Component editorComponent = headerUI.header.getEditorComponent();
            Point tmpP = e.getPoint();
            // 将鼠标点击坐标值转为表格头编辑组件上的坐标
            Point p2 = SwingUtilities.convertPoint(headerUI.header, tmpP, editorComponent);
            // 再把该事件应用编辑器组件
            dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, p2.x, p2.y);
        }

        /**
         * 重新派发该鼠标事件
         * 
         * @param e
         *            鼠标事件源
         * @return 派发成功的标志
         */
        private boolean repostEvent(
                MouseEvent e) {
            // Check for isEditing() in case another event has
            // caused the editor to be removed. See bug #4306499.
            // 如果表格头不可编辑或将要把鼠标事件派发给的那个组件为空,就返回假
            if (dispatchComponent == null || !headerUI.header.isEditing()) {
                return false;
            }
            // 把表格头得到的鼠标事件转给要把鼠标事件派发给的那个组件
            MouseEvent e2 = SwingUtilities.convertMouseEvent(headerUI.header, e, dispatchComponent);
            // 派发事件
            dispatchComponent.dispatchEvent(e2);
            dispatchComponent.requestFocus();
            return true;
        }

        /**
         * 判断某一列尺寸是否可重新调整
         * 
         * @param column
         *            该列
         * @return 是否可调整
         */
        private boolean canResize(
                PIMTableColumn column) {
            // 如果列没有或表格头不允许尺寸调整且不允许调整尺寸返回不可
            return (column != null) && headerUI.header.getResizingAllowed() && column.getResizable()
                    && column.getMaxWidth() != column.getMinWidth();

        }

        /**
         * 判断某一列尺寸是否可重新调整
         * 
         * @param prmP
         *            鼠标点击的坐标
         * @return 可调整的列
         */
        private PIMTableColumn getResizingColumn(
                Point prmP) {
            // 得到鼠标所在行
            int row = headerUI.header.rowAtPoint(prmP);
            // 排错
            if (row == 1) {
                return null;
            }
            return getResizingColumn(prmP, headerUI.header.columnAtPoint(prmP));
        }

        /**
         * 得到尺寸调整后的那个列
         * 
         * @param prmP
         *            鼠标点击的坐标
         * @param column
         *            列索引
         * @return 可调整的列
         */
        private PIMTableColumn getResizingColumn(
                Point prmP,
                int column) {
            if (column == -1) // ??这句出错好像多余？
            {
                return null;
            }

            Rectangle r = headerUI.header.getHeaderRect(column); // 得到该列表头的区域，
            r.grow(-3, 0); // 对r进行左右积压处理。即左右扩展为3，上下扩展为0。

            if (r.contains(prmP)) // 鼠标在区域内，即不应该改尺寸，则返回空。
            {
                return null;
            }
            // 得到X坐标中间点
            int midPoint = r.x + r.width / 2;
            int columnIndex;
            // 如果组件的方向是自左到右
            if (headerUI.header.getComponentOrientation().isLeftToRight()) {
                // 小于中间点取前一列,否则取该列
                columnIndex = (prmP.x < midPoint) ? column - 1 : column;
            } else {
                // 大于中间点取该列,否则取前一列
                columnIndex = (prmP.x < midPoint) ? column : column - 1;
            }
            // 除错
            if (columnIndex == -1) {
                return null;
            }
            // 返回这一列
            return headerUI.header.getColumnModel().getColumn(columnIndex);
        }

        // /** 判断某一鼠标事件是否要被忽略
        // * @param e 鼠标事件源
        // * @return 本鼠标事件是否可忽略
        // */
        // private boolean shouldIgnore(MouseEvent e)
        // {
        // //是一个可消耗事件或者是非鼠标左键按下且表格头被禁止
        // //return e.isConsumed() || (!(SwingUtilities.isLeftMouseButton(e) && header.isEnabled()));
        // return e.isConsumed();
        // }
        /**
         * 调整有焦点和选中的列的方法
         * 
         * @param e
         *            鼠标事件源
         */
        private void adjustFocusAndSelection(
                MouseEvent e) {
            // 处理可忽略鼠标事件
            // if (shouldIgnore(e))
            // {
            // return;
            // }
            // 得到鼠标点。
            Point tmpP = e.getPoint();

            // 得到鼠标点所点的行号。
            int row = headerUI.header.rowAtPoint(tmpP);
            // 得到鼠标点对应的model中的列号。
            int column = headerUI.header.columnAtPoint(tmpP);

            // 处理出错The autoscroller can generate drag events outside the Table's range.
            if ((column == -1) || (row == -1)) {
                return;
            } // 除错over-----------------------------------------------------

            // 若table的表头上显示有编辑栏，且表的正文（不包含表头的编辑栏）中有
            // Cell正处于编辑状态，则令正在编辑的Cell停止编辑。
            // if (header.getTable().hasEditor() && header.getTable().isEditing())
            if (headerUI.header.getTable().hasEditor() && !headerUI.header.isEditing()) {
                IPIMCellEditor tableCellEditor = headerUI.header.getTable().getCellEditor();
                IView view = headerUI.header.getTable().getView();
                if (view != null) {
                    PIMViewInfo viewInfo = view.getApplication().getActiveViewInfo();
                    int appType = viewInfo.getAppIndex();
                    if (appType == ModelCons.CONTACT_APP || appType == ModelCons.TASK_APP) {
                        view.viewToModel();
                    }
                }

                if (tableCellEditor != null) {
                    tableCellEditor.stopCellEditing();
                }
            }

            // 如果本列是不可编辑的，只做如下工作：
            // 移除编辑器，得到焦点
            PIMTableColumn tableColumn = headerUI.header.getColumnModel().getColumn(column);
            if (column != -1 && !tableColumn.getEditorEnable()) {
                if (headerUI.header.getTable().hasEditor() && headerUI.header.editCellAt(row, column, e)) {
                    headerUI.header.removeEditor();
                }
                headerUI.header.repaint();
                // 以下的几句话好象有点失控
                // 得到表格头的绘制器组件
                Component rendererComponent = headerUI.getHeaderRenderer(headerUI.header.columnAtPoint(tmpP)); // header.getEditorComponent();
                // 将鼠标点击坐标值转为表格头绘制组件上的坐标
                Point p2 = SwingUtilities.convertPoint(headerUI.header, tmpP, rendererComponent);
                // 再把该事件应用绘制器组件
                dispatchComponent = SwingUtilities.getDeepestComponentAt(rendererComponent, p2.x, p2.y);

                if (headerUI.header.isRequestFocusEnabled()) {
                    // 令header得到焦点。
                    headerUI.header.requestFocus();
                    // 2003.10.31 后一个判断解点排序后如果表格有选中就出现双焦点的问题
                    if (headerUI.header.getTable().hasEditor() && headerUI.header.getTable().getSelectedRow() < 0) {
                        int tmpLocateCol = column;
                        if (headerUI.header.getTable().getView() != null) {
                            tmpLocateCol = PIMViewUtil.getFirstTextableColumn(headerUI.header);
                        }
                        headerUI.header.setEditingColumn(tmpLocateCol);
                        headerUI.header.setSelectedColumnIndex(tmpLocateCol);
                        headerUI.header.editCellAt(1, tmpLocateCol);
                        headerUI.header.getEditorComponent().requestFocus();
                    }
                }
                return;
            }

            if (headerUI.header.getTable().hasEditor() && headerUI.header.isEditing()) {
                // 对表示为字段进行处理
                PIMViewUtil.processDisplayAsField(headerUI.header);
            }
            // 若table的表头上显示有编辑栏，且令该表头中的该Cell进入编辑状态返回为true。
            if (headerUI.header.getTable().hasEditor() && headerUI.header.editCellAt(row, column, e)) // &&
                                                                                                      // header.getColumnModel().getColumn(column).getEditorEnable())
            {
                // 清除Table中的选取
                headerUI.header.getTable().clearSelection();
                // 设置派发事件的组件对象
                setDispatchComponent(e);
                // 重新派发事件
                repostEvent(e);
            }
            // 否则，如表格头可得到焦点
            // 在表格头有快速编辑栏时,鼠标点击在表格体某格
            // 后,排序或者尺寸调整,会进入本方法,产生双焦点问题,但是实际的焦点在表格头,
            // 我多加一个判断:在表格中有选中行中,不进入下面一段代码.
            else if (headerUI.header.isRequestFocusEnabled() && headerUI.header.getTable().getSelectedRow() < 0) {
                // 令header得到焦点。
                headerUI.header.requestFocus();
                if (headerUI.header.getTable().hasEditor()) {
                    int tmpLocateCol = PIMViewUtil.getFirstTextableColumn(headerUI.header);
                    headerUI.header.setEditingColumn(tmpLocateCol);
                    headerUI.header.setSelectedColumnIndex(tmpLocateCol);
                    headerUI.header.editCellAt(1, tmpLocateCol);
                    headerUI.header.getEditorComponent().requestFocus();
                }
            }

            // CellEditor editor = headerUI.header.getCellEditor();
            // //如编辑器不为空或编辑器可将被选中
            // if (editor == null || editor.shouldSelectCell(e))
            // {
            // //鼠标是按下就设列调整状态为真
            // boolean adjusting = (e.getID() == MouseEvent.MOUSE_PRESSED) ? true : false;
            // }
        }

        /**
         * 交换鼠标光标
         */
        private void swapCursor() {
            Cursor tmp = headerUI.header.getCursor();// 保存
            headerUI.header.setCursor(otherCursor);// 设置
            otherCursor = tmp; // 交换了
        }

        /**
         * 实现 MouseListener 接口中的方法－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－ 表示鼠标按下,处理表格头按下时的几种情况: 1.右键菜单 2.选中单元格
         * 3.处理单元格宽度调整状态 4.处理某列被拖动 5.处理调整一下焦点和选中
         * 
         * @param e
         *            鼠标事件源
         */
        public void mousePressed(
                MouseEvent e) {
            headerUI.header.setDraggedColumn(null); // 清空header中被拖动列的引用。
            headerUI.header.setResizingColumn(null); // 清空header中被改尺寸列的引用。
            headerUI.header.setDraggedDistance(0); // 复位拖放距离值。

            Point tmpP = e.getPoint(); // 得到鼠标点。
            int tmpSeleCol = headerUI.header.columnAtPoint(tmpP); // 得到列索引 find out which header cell was hit by
                                                                  // now----------------------------------------------------------------------
            headerUI.header.setSelectedColumnIndex(tmpSeleCol);

            if (tmpSeleCol != -1) {
                // The last 3 pixels + 3 pixels of next column are for resizing（左右各留三个象素用于边尺寸。其他位置用于拖放。）
                // getResizingColumn方法经判断发现点p不在index列的表头区域的去掉 //左右3象素剩下的区域内，将返回null。
                PIMTableColumn tmpResizingColumn = getResizingColumn(tmpP, tmpSeleCol);

                if (canResize(tmpResizingColumn) && headerUI.header.rowAtPoint(tmpP) == 0) // 如果列可进行尺寸调整而且点不在在编辑栏，则准备拖动
                {
                    headerUI.header.setResizingColumn(tmpResizingColumn); // 设置为调整后的列

                    // 2003.10.10 在表格体有编辑器时,表格的列宽将无法调整;所以在这里先拿掉它,在列宽调整后会存盘,之后再更新视图,这时会
                    // 恢复,不要紧,不过如果要保存表格中的修改字段可能不行了.
                    if (headerUI.header.getTable().getEditorComponent() != null) {
                        headerUI.header.getTable().removeEditor();
                    }
                    // 表示处于尺寸调整状态，在鼠标释放时要去保存尺寸数据
                    if (headerUI.header.rowAtPoint(tmpP) == 0) {
                        isResizingAccidentOccur = true;
                    }
                    // 如果组件的方向是自左到右
                    if (headerUI.header.getComponentOrientation().isLeftToRight()) {
                        // 重设偏移点X坐标
                        mouseXOffset = tmpP.x - tmpResizingColumn.getWidth();
                    }
                }
                // 否则如果列可进行排序
                else if (headerUI.header.getReorderingAllowed()) // 如果支持排序动作。
                {
                    PIMTableColumn hitColumn = headerUI.header.getColumnModel().getColumn(tmpSeleCol);
                    // 设置拖动列
                    headerUI.header.setDraggedColumn(hitColumn);
                    // 重设偏移点X坐标
                    mouseXOffset = tmpP.x;
                }
            }

            if (e.isConsumed()) // 如果如果这个事件可被消耗掉
            {
                selectedOnPress = false; // ??这个设置没有用到？
                return;
            }
            selectedOnPress = true; // 设置列的按下选择状态 //??这个设置没有用到？

            // 重调整一下焦点和选中
            adjustFocusAndSelection(e);
        }

        /**
         * 实现 mouseInputHandler 接口中的方法 表示鼠标释放
         */
        public void mouseReleased(
                MouseEvent e) {
            // 设置拖动距离
            setDraggedDistance(0, headerUI.viewIndexForColumn(headerUI.header.getDraggedColumn()));

            // 清空表格头中的调整列和拖动列两个变量
            headerUI.header.setResizingColumn(null);
            headerUI.header.setDraggedColumn(null);

            // 得到鼠标点。
            Point tmpP = e.getPoint();
            // 如果表格配有编辑器
            if (headerUI.header.getTable().hasEditor() && headerUI.header.rowAtPoint(tmpP) == 1) {
                // 重新派发这个事件
                // if (column != -1 && header.getColumnModel().getColumn(column).getEditorEnable())
                {
                    repostEvent(e);
                    headerUI.header.requestFocus();
                    // 清空变量
                    dispatchComponent = null;
                }
            } else {
                repostEvent(e);
                // 清空变量
                dispatchComponent = null;
            }

            // 把调整后的尺寸数据保存
            if (isResizingAccidentOccur) {
                // Thread.dumpStack();
                isResizingAccidentOccur = false;
                // TODO:下面要把排序信息记录入数据库
                IView view = headerUI.header.getTable().getView();
                // 得到 PIMViewInfo 的实例 以处理排序信息保存到数据库
                if (view != null && view.getApplication().getActiveViewInfo() != null) {
                    view.updateTableInfo(IView.COLUMN_WIDTH_CHANGED, null);
                }
            }

            // 处理右键事件-----------------------------------------------------------------
            // 是一个可消耗事件或者是非鼠标左键按下且表格头被禁止
            if (SwingUtilities.isRightMouseButton(e)) {
                // 在某些情况下,此时表格有有选中行,要清除.2003.10.10
                if (headerUI.header.getTable().getSelectedRows().length > 0 && headerUI.header.getTable().hasEditor()) {
                    headerUI.header.getTable().clearSelection();
                }
                IView tmpView = headerUI.header.getTable().getView();
                if (tmpView != null) {
                    IApplication tmpApplication = tmpView.getApplication();
                    if (tmpApplication != null) {
                        tmpApplication.showPopupMenu(headerUI.header, e.getX(), e.getY());
                        if (!headerUI.header.getTable().hasEditor()) {
                            headerUI.header.getTable().requestFocus();
                        }
                    }
                }
            }
        }

        /***/
        public void mouseEntered(
                MouseEvent e) {
        }

        /***/
        public void mouseExited(
                MouseEvent e) {
        }

        /**
         * 实现 MouseInputListener
         * 接口中的方法------------------------------------------------------------------------------------------
         * 如果处在非调整状态(表格头单元格),什么都不做,如果处于两列之间就要改变鼠标光标形态
         * 
         * @NOTE:如果其前一列不可调整尺寸，也不可交换光标
         * @param e
         *            鼠标事件源
         */
        public void mouseMoved(
                MouseEvent e) {
            if (canResize(getResizingColumn(e.getPoint())) != (headerUI.header.getCursor() == resizeCursor)) {
                swapCursor();
            }
        }

        /**
         * 实现 MouseInputListener 接口中的方法 表示鼠标拖动,拖动包括单列尺寸变动和移动列
         * 
         * @param e
         *            鼠标事件源
         */
        public void mouseDragged(
                MouseEvent e) {
            // 如是表格配有编辑器且点在表格头的编辑器中就返回
            if (headerUI.header.getTable().hasEditor() && e.getY() > headerUI.header.getHeight() / 2 - 2) {
                return;
            } // 如果点在表头的编辑栏中，则不做拖放处理。--------------------
              // 进行拖动
            int mouseX = e.getX();

            // 取可调整列和拖动列
            PIMTableColumn resizingColumn = headerUI.header.getResizingColumn();
            PIMTableColumn draggedColumn = headerUI.header.getDraggedColumn();
            // 取组件的方向
            boolean headerLeftToRight = headerUI.header.getComponentOrientation().isLeftToRight();
            // 有要拖动的列存在
            if (resizingColumn != null) {
                // 保存原有尺寸
                int oldWidth = resizingColumn.getWidth();
                int newWidth;
                // 如果组件的方向是自左到右
                if (headerLeftToRight) {
                    newWidth = mouseX - mouseXOffset;
                }
                // 如果组件的方向是自右到左
                else {
                    newWidth = mouseXOffset - mouseX;
                }
                // 重设该列的宽度
                resizingColumn.setWidth(newWidth);

                Container container;

                // 如表格头无父级和爷级组件或爷级组件不是 PIMScrollPane 就返回
                if ((headerUI.header.getParent() == null)
                        || ((container = headerUI.header.getParent().getParent()) == null)
                        || !(container instanceof PIMScrollPane)) {
                    return;
                }

                // 涉及表格在 PIMScrollPane 中的显示的处理
                // // 如果 PIMScrollPane 组件的方向不是自左到右 且自己(表格头)也不是
                if (!container.getComponentOrientation().isLeftToRight() && !headerLeftToRight) {
                    // 得到表格
                    PIMTable table = headerUI.header.getTable();
                    // 保证正确
                    if (table != null) {
                        // 取视口宽度
                        JViewport viewport = ((PIMScrollPane) container).getViewport();
                        int viewportWidth = viewport.getWidth();
                        // 取新旧差值
                        int diff = newWidth - oldWidth;
                        int newHeaderWidth = table.getWidth() + diff;
                        Dimension tableSize = table.getSize();
                        // 加上差值
                        tableSize.width += diff;
                        // 重设置宽度
                        table.setSize(tableSize);

                        // 新宽度大于视口宽度,且表格各列宽度不可自动调整
                        if ((newHeaderWidth >= viewportWidth)
                                && (table.getAutoResizeMode() == PIMTable.AUTO_RESIZE_OFF)) {
                            // 取视点
                            Point tmpP = viewport.getViewPosition();
                            tmpP.x = Math.max(0, Math.min(newHeaderWidth - viewportWidth, tmpP.x + diff));
                            // 重新设置视点
                            viewport.setViewPosition(tmpP);
                            mouseXOffset += diff;
                        }
                    }
                }
                // 2003.9.16 保证在宽度变化时虚假网格张绘制正确
                container.repaint();
            }
            // 否则如果拖动的列存在
            else if (draggedColumn != null) {
                // 取表格列模型,取偏移,和拖动方向
                IPIMTableColumnModel cm = headerUI.header.getColumnModel();
                int draggedDistance = mouseX - mouseXOffset;
                int direction = (draggedDistance < 0) ? -1 : 1;
                // 调用外部类方法,得到拖动列的索引
                int columnIndex = headerUI.viewIndexForColumn(draggedColumn);
                // 计算移动到位置所处列的索引值
                int newColumnIndex = columnIndex + (headerLeftToRight ? direction : -direction);
                // 保证新索引在合理范围内
                if (0 <= newColumnIndex && newColumnIndex < cm.getColumnCount()) {
                    // 取新索引处的列宽
                    int width = cm.getColumn(newColumnIndex).getWidth();
                    // 以拖动到的目的列宽度的一半为准
                    if (Math.abs(draggedDistance) > (width / 2)) {
                        // 加偏移
                        mouseXOffset = mouseXOffset + direction * width;
                        headerUI.header.setDraggedDistance(draggedDistance - direction * width);
                        // 在列模型中调整
                        cm.moveColumn(columnIndex, newColumnIndex);
                        return;
                    }
                }
                // 设置拖动距离
                setDraggedDistance(draggedDistance, columnIndex);
            }
        }// －－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－

        //
        // Protected & Private Methods
        //
        /**
         * 设置鼠标拖动的距离
         * 
         * @param draggedDistance
         *            鼠标事件源
         * @param column
         *            所在列
         */
        private void setDraggedDistance(
                int draggedDistance,
                int column) {
            // 通过表格头来设置
            headerUI.header.setDraggedDistance(draggedDistance);
            // 下面大概是激发一下列模型的变化事件
            if (column != -1) {
                headerUI.header.getColumnModel().moveColumn(column, column);
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////////
    // ///////////////// 我加了两个类,先试一试看 //////////////////
    // //////////////////////////////////////////////////////////////////////////
    /**
     * 键盘动作,本类好象是用来进行方向键处理
     */
    public static class NavigationalAction extends AbstractAction {
        /**
         * 保存X坐标偏移
         */
        protected int dx;
        /**
         * 保存Y坐标偏移
         */
        protected int dy;
        /**
         * 绑定标志
         */
        protected boolean toggle;
        /**
         * 扩展标志
         */
        protected boolean extend;
        /**
         * 选中标志
         */
        protected boolean inSelection;

        /**
         * 锚定行
         */
        protected int anchorRow;
        /**
         * 锚定列
         */
        protected int anchorColumn;
        /**
         * 表格视口起始行
         */
        protected int leadRow;
        /**
         * 表格视口起始列
         */
        protected int leadColumn;

        /**
         * 创建方向键控制类的一个实例
         * 
         * @param dx
         *            X坐标偏移
         * @param dy
         *            Y坐标偏移
         * @param toggle
         *            是否绑定的标志
         * @param extend
         *            可扩展标志
         * @param inSelection
         *            选中标志
         */
        public NavigationalAction(int dx, int dy, boolean toggle, boolean extend, boolean inSelection) {
            this.dx = dx;
            this.dy = dy;
            this.toggle = toggle;
            this.extend = extend;
            this.inSelection = inSelection;
        }

        /**
         * 修饰显示范围
         * 
         * @param isRow
         *            是否为行变化
         * @param isLead
         *            是否从选择模型的起始选中开始
         * @param i
         *            第一操作数
         * @param a
         *            第一操作数
         * @param b
         *            第三操作数
         * @return 第一操作数和第二操作数的大数和第三操作数-1相比较,取两者的最小值
         */
        private int clipToRange(
                int i,
                int a,
                int b,
                boolean isRow,
                boolean isLead) {
            if (isRow && !isLead) {
                if (i < 0) {
                    return -100;
                }
            }

            return Math.min(Math.max(i, a), b - 1);
        }

        /**
         * 在表格范围内移动
         * 
         * @param header
         *            表格实例
         * @param dx
         *            X坐标的偏移值
         * @param dy
         *            Y坐标的偏移值
         * @param changeLead
         *            从选择模型的起始选中开始
         */
        private void moveWithinTableRange(
                PIMTableHeader header,
                int dx,
                int dy,
                boolean changeLead) {
            // 标志为真的情况下
            if (changeLead) {
                // 起始行取起始行加偏移数和0之间的最大值与总行数比出最小的,列的算法与之相同
                // leadRow = clipToRange(leadRow+dy, 0, table.getRowCount());
                leadRow = 1;
                leadColumn = clipToRange(leadColumn + dx, 0, header.getTable().getColumnCount(), false, true);
            }
            // 标志为假的情况下
            else {
                // 起始行取锚定行加偏移数和0之间的最大值与总行数比出最小的,列的算法与之相同
                anchorRow = clipToRange(anchorRow + dy, 1, 2, true, false);
                // anchorRow = 1;
                anchorColumn = clipToRange(anchorColumn + dx, 0, header.getTable().getColumnCount(), false, false);
            }
        }

        // /** 得到选中的范围
        // * @param sm ListSelectionModel 接口的实例
        // * @return 返回选中的范围
        // */
        // private int selectionSpan(ListSelectionModel sm)
        // {
        // //返回列选择模型中保存的数据
        // return sm.getMaxSelectionIndex() - sm.getMinSelectionIndex() + 1;
        // }

        // /** 比较某一列在列选择模型中的?
        // * @param i 某一列的索引
        // * @param sm ListSelectionModel 接口的实例
        // * @return 比较结果
        // */
        // private int compare(int i, ListSelectionModel sm)
        // {
        // //本列和列选择模型的最小选中列之间的最大值与列选择模型的最大选中列比出最小的
        // return compare(i, sm.getMinSelectionIndex(), sm.getMaxSelectionIndex()+1);
        // }

        // /** 比较两个数大小
        // * @param i 第一操作数
        // * @param a 第二操作数
        // * @param b 第三操作数
        // * @return 比较结果
        // */
        // private int compare(int i, int a, int b)
        // {
        // //第一操作数小于第二操作数返回-1,否则如不小于第三操作数返回1,否则返回0
        // return (i < a) ? -1 : (i >= b) ? 1 : 0 ;
        // }

        // /** 判断是否在选择范围内移动
        // * @param header 本表格头对象
        // * @param dx X坐标的偏移值
        // * @param dy Y坐标的偏移值
        // * @param ignoreCarry 可忽略条件
        // * @return 返回是否在选择范围内移动的标志
        // * 注:本方法在表格头中似乎无用,可能可以不用了
        // */
        // private boolean moveWithinSelectedRange(PIMTableHeader header, int dx, int dy, boolean ignoreCarry)
        // {
        // //取表格的列选择模型,及从列模型中得到列选择模型
        // ListSelectionModel rsm = header.getTable().getSelectionModel();
        // ListSelectionModel csm = header.getTable().getColumnModel().getSelectionModel();
        //
        // //初始新锚定行和锚定列(加上偏移)
        // //int newAnchorRow = anchorRow + dy;
        // int newAnchorRow = 1;
        // int newAnchorColumn = anchorColumn + dx;
        //
        // //声明行信号和列信号
        // int rowSgn;
        // int colSgn;
        // //得到选择的行数和列数
        // int rowCount = selectionSpan(rsm);
        // int columnCount = selectionSpan(csm);
        //
        // //这个变量表示不止一格被选中
        // boolean canStayInSelection = (rowCount * columnCount > 1);
        // if (canStayInSelection)
        // {
        // //得到操作后的位置
        // rowSgn = compare(newAnchorRow, rsm);
        // colSgn = compare(newAnchorColumn, csm);
        // }
        // else
        // {
        // //在没有选择的情况下
        // rowCount = header.getTable().getRowCount();
        // columnCount = header.getTable().getColumnCount();
        // //得到操作后的位置
        // rowSgn = compare(newAnchorRow, 0, rowCount);
        // colSgn = compare(newAnchorColumn, 0, columnCount);
        // }
        // //得到新锚定行和锚定列(加上偏移)
        // anchorRow = newAnchorRow - rowCount * rowSgn;
        // anchorColumn = newAnchorColumn - columnCount * colSgn;
        //
        // //在不可忽略搬动的情况下递归
        // if (!ignoreCarry)
        // {
        // return moveWithinSelectedRange(header, rowSgn, colSgn, true);
        // }
        // //否则返回仍在选择区域中的标志
        // return canStayInSelection;
        // }

        /**
         * 实现父类中的抽象方法,执行操作
         * 
         * @param e
         *            动作事件源
         */
        public void actionPerformed(
                ActionEvent e) {
            PIMTableHeader header = (PIMTableHeader) e.getSource(); // 得到动作实施者,取其行选择模型
            anchorRow = leadRow = 1; // 得到选择的起始行数起始列

            ListSelectionModel csm = header.getColumnModel().getSelectionModel(); // 从列模型中得到列选择模型,得到选择的锚定行数锚定列
            anchorColumn = header.getSelectedColumnIndex();
            leadColumn = csm.getLeadSelectionIndex();

            // 保存旧的锚定行数锚定列
            int oldAnchorColumn = anchorColumn;

            // 如在编辑中且编辑没有停止,不进行任何操作
            CellEditor editor = header.getCellEditor();
            if (editor != null) {
                if (header.isEditing()) {
                    // 对日期选择组合框进行输入校验
                    if (header.getEditorComponent() instanceof CalendarCombo) {
                        Toolkit.getDefaultToolkit().beep();
                        // TODO:输入校验
                        String tmpTimeText = ((CalendarCombo) header.getEditorComponent()).getTimeText();
                        if (CASUtility.getFormatedTime(tmpTimeText, 0) == null) {
                            Toolkit.getDefaultToolkit().beep();
                            return;
                        }
                    } // end 日期选择组合框进行输入校验
                      // 对表示为字段进行处理
                    PIMViewUtil.processDisplayAsField(header);
                }
                if (header.isEditing() && !editor.stopCellEditing()) {
                    return;
                }

            }
            // 不幸地，这一个策略导致BUG是因为以下调用的requestFocus()方法的非同步的本性。
            // 尽管条件要求快速,在典型的情形下调用 invokeLater()
            // 导致一个延迟使得这个方法得以工作,然后允许焦点全部消失。
            // 正确的解决方法看来是要改进 requestFocus() 以便它专门把一个
            // 焦点请求插入事件队列面不管此时谁拥有焦点,这样对 requestFocus()
            // 的调动能成功。
            // 当组件已经有焦点忽略对 requestFocus() 的调用的最佳方法
            // 可能是在请求焦点事件出了事件队列不久前的时候才能成功。
            // Unfortunately, this strategy introduces bugs because
            // of the asynchronous nature of requestFocus() call below.
            // Introducing a delay with invokeLater() makes this work
            // in the typical case though race conditions then allow
            // focus to disappear altogether. The right solution appears
            // to be to fix requestFocus() so that it queues a request
            // for the focus regardless of who owns the focus at the
            // time the call to requestFocus() is made. The optimisation
            // to ignore the call to requestFocus() when the component
            // already has focus may ligitimately be made as the
            // request focus event is dequeued, not before.

            // 如果不在选择中
            if (!inSelection) {
                moveWithinTableRange(header, dx, dy, extend);
                // 这一步是要进入到表格体
                // ||后的是操作翻页键时的处理.
                if (anchorRow == -100 || dy > 0) {
                    IView tmpView = header.getTable().getView();
                    int tmpKeyFieldValueState = 0;
                    // 在联系人视图中进行对表示为字段的处理
                    if (tmpView != null
                            && tmpView.getApplication().getActiveViewInfo().getAppIndex() == ModelCons.CONTACT_APP) {
                        if ((tmpKeyFieldValueState = PIMViewUtil.isDisplayAsHasValue(header)) == 0) // 如果主题为空,且不是所有字段都为空.
                        {
                            // “显示为”是一条联系人记录的关键字段，不能为空。请为该联系人记录的“显示为”字段输入内容。
                            SOptionPane.showErrorDialog(MessageCons.W10619);
                            return;
                        }
                    }
                    // 在任务视图中进行对主题为字段的处理
                    else if (tmpView != null
                            && tmpView.getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP) {
                        if ((tmpKeyFieldValueState = PIMViewUtil.isSubjectAsHasValue(header)) == 0) // 如果主题为空,且不是所有字段都为空.
                        {
                            // “主题”是一条任务记录的关键字段，不能为空。请为该任务记录的“主题”字段输入内容。
                            SOptionPane.showErrorDialog(MessageCons.W10622);
                            return;
                        }
                    }
                    PIMTable table = header.getTable();

                    if (table.getRowCount() < 1) {
                        header.setEditingColumn(anchorColumn);
                        header.setSelectedColumnIndex(anchorColumn);
                        if (anchorRow != -1 && anchorColumn != -1) {
                            if (!header.editCellAt(1, anchorColumn)) {
                                return;
                            }
                            header.getEditorComponent().requestFocus();
                        }
                        return;
                    }
                    // table.setRowSelectionInterval(0, 0);
                    // table.setColumnSelectionInterval(0, 4);
                    table.changeSelection(0, anchorColumn, toggle, extend, false);
                    IView view = table.getView();
                    if (tmpKeyFieldValueState != -1 && view != null) // 如果主题有值,@NOTE:排除掉了全部字段都为空的情况.
                    {
                        PIMViewInfo viewInfo = view.getApplication().getActiveViewInfo();
                        int appType = viewInfo.getAppIndex();
                        if (appType == ModelCons.CONTACT_APP || appType == ModelCons.TASK_APP) {
                            // header.setEditingColumn(0);
                            // header.setSelectedColumnIndex(0);
                            int tmpLocateCol = PIMViewUtil.getFirstTextableColumn(header);
                            header.editCellAt(1, tmpLocateCol);
                            view.viewToModel();
                        }
                    }
                    table.requestFocus();
                    return;
                } else {
                    header.setEditingColumn(anchorColumn);
                    header.setSelectedColumnIndex(anchorColumn);
                    if (anchorRow != -1 && anchorColumn != -1) {
                        if (!header.editCellAt(anchorRow, anchorColumn)) {
                            return;
                        }
                        header.getEditorComponent().requestFocus();
                    }
                }
            }
            // 在选择中 ,表示是TAB键控制
            // 采用的是我们自己的控制方式
            else {
                // 这个变量表示到了最后一列,按下TAB键时,是跳到最前面去的
                boolean tabFromBegin = false;
                if (dx == -1) {
                    anchorColumn = selectPreAnchorColumn(anchorColumn, header);
                } else {
                    // 递归查找到下一个可置焦点的列索引
                    anchorColumn = selectNextAnchorColumn(anchorColumn, header);
                    if (anchorColumn < oldAnchorColumn) {
                        tabFromBegin = true;
                    }
                }

                // 表格头要在锚定列中重设一下选中的
                header.setEditingColumn(anchorColumn);
                header.setSelectedColumnIndex(anchorColumn);
                if (anchorRow != -1 && anchorColumn != -1) {
                    if (!header.editCellAt(anchorRow, anchorColumn)) {
                        return;
                    }
                    header.getEditorComponent().requestFocus();
                }

                if (header.getTable().getAutoscrolls()) {
                    Rectangle rect = header.getHeaderRect(anchorRow, anchorColumn, false);

                    // 如果是从最前面开始,滚动矩形的X坐标置0
                    if (tabFromBegin && rect != null) {
                        rect.x = 0;
                    }
                    if (rect != null) {
                        header.scrollRectToVisible(rect);
                        header.getTable().scrollRectToVisible(rect);
                    }
                }
            }
            // 视口重绘网格线
            if (header.getTable().getParent() != null) {
                header.getTable().getParent().repaint();
            }
        }

        /**
         * 本方法使用递归算法来找到下一个可置焦点的列索引
         * 
         * @param prmAnchorColumn
         *            原锚定列
         * @param prmTableHeader
         *            主要是由于本方法所在类是静态类, BasicPIMTableHeaderUI 中的表格头的引用不可直接使用,必须传入,
         * @return 计算出的可设置焦点的列索引
         * @called by NavigationalAction.ActionPerformed
         */
        private int selectNextAnchorColumn(
                int prmAnchorColumn,
                PIMTableHeader prmTableHeader) {
            int anchorColumn = prmAnchorColumn + 1;
            if (anchorColumn >= prmTableHeader.getColumnModel().getColumnCount()) {
                anchorColumn = 0;
            }
            PIMTableColumn tmpColumn = prmTableHeader.getColumnModel().getColumn(anchorColumn);
            // TODO: 判断,如本列宽度为零或不可编辑,递归处理
            // 某几个列是不可编辑的,这是列的一个属性,视图面板上的第一列"ID"是隐
            // 私的,其最大宽度是0,也不可以得到焦点

            // 2003.12.1 前一复合判断不合理,是用在约会上的,暂时如此
            if ((!tmpColumn.getEditorEnable() && prmTableHeader.getTable().getView() != null)
                    || tmpColumn.getMaxWidth() == 0) {
                return selectNextAnchorColumn(anchorColumn, prmTableHeader);
            } else {
                // 正常处理
                return anchorColumn;
            }
        }

        /**
         * 本方法使用递归算法来找到前一个可置焦点的列索引
         * 
         * @param prmAnchorColumn
         *            原锚定列
         * @param prmTableHeader
         *            主要是由于本方法所在类是静态类, BasicPIMTableHeaderUI 中的表格头的引用不可直接使用,必须传入,
         * @return 计算出的可设置焦点的列索引
         * @called by NavigationalAction.ActionPerformed
         */
        private int selectPreAnchorColumn(
                int prmAnchorColumn,
                PIMTableHeader prmTableHeader) {
            int anchorColumn = prmAnchorColumn - 1;
            if (anchorColumn < 0) {
                anchorColumn = prmTableHeader.getColumnModel().getColumnCount() - 1;
            }
            PIMTableColumn tmpColumn = prmTableHeader.getColumnModel().getColumn(anchorColumn);
            // TODO: 判断,如本列宽度为零或不可编辑,递归处理
            // 某几个列是不可编辑的,这是列的一个属性,视图面板上的第一列"ID"是隐
            // 私的,其最大宽度是0,也不可以得到焦点

            // 2003.12.1 前一复合判断不合理,是用在约会上的,暂时如此
            if ((!tmpColumn.getEditorEnable() && prmTableHeader.getTable().getView() != null)
                    || tmpColumn.getMaxWidth() == 0) {
                return selectPreAnchorColumn(anchorColumn, prmTableHeader);
            } else {
                // 正常处理
                return anchorColumn;
            }
        }
    }

    // =================================================================================
    /**
     * 处理翻页动作的类
     */
    public static class PagingAction extends NavigationalAction {
        /**
         * 向上还是向下翻的标志
         */
        private boolean forwards;
        /**
         * 垂直还是左右翻的标志
         */
        private boolean vertically;
        /**
         * 是否有限制
         */
        private boolean toLimit;

        /**
         * 本类引用
         */
        BasicPIMTableHeaderUI headerUI;

        /**
         * 处理翻页动作的类建器,从上面一个类扩展的
         * 
         * @param header
         *            本类引用
         * @param extend
         *            是否扩展的标志
         * @param forwards
         *            翻页的方向
         * @param vertically
         *            是否是竖直方向
         * @param toLimit
         *            是否有限制
         */
        public PagingAction(BasicPIMTableHeaderUI header, boolean extend, boolean forwards, boolean vertically,
                boolean toLimit) {
            // 先调用父类的构建器
            super(0, 0, false, extend, false);
            // 本地变量初始化一下
            headerUI = header;
            this.forwards = forwards;
            this.vertically = vertically;
            this.toLimit = toLimit;
        }

        /**
         * 实现父类中的抽象方法,执行操作
         * 
         * @param e
         *            动作事件源
         */
        public void actionPerformed(
                ActionEvent e) {
            PIMTableHeader header = (PIMTableHeader) e.getSource();
            // 如果是到最后(包括最前面)
            if (toLimit) {
                // 垂直方向
                if (vertically) {
                    int rowCount = header.getTable().getRowCount();
                    this.dx = 0;
                    // 设置偏移位置
                    this.dy = forwards ? rowCount : -rowCount;
                }
                // 水平方向
                else {
                    int colCount = header.getTable().getColumnCount();
                    // 设置偏移位置
                    this.dx = forwards ? colCount : -colCount;
                    this.dy = 0;
                }
            }
            // 一页一页地来
            else {
                // 表格不放在 PIMScrollPane 滚动面板中就返回
                if (!(header.getTable().getParent().getParent() instanceof PIMScrollPane)) {
                    return;
                }
                // 得到 ViewPort 视口的面积
                Dimension delta = header.getTable().getParent().getSize();
                // 是垂直方向取列选择模型,否则取行选择模型
                ListSelectionModel sm =
                        (vertically) ? header.getTable().getSelectionModel() : header.getTable().getColumnModel()
                                .getSelectionModel();

                // 如果是扩展,开始处取起始索引,否则取锚定索引
                int start = (extend) ? sm.getLeadSelectionIndex() : sm.getAnchorSelectionIndex();

                // 是垂直方向
                if (vertically) {
                    // 得到单元格区域
                    Rectangle r = header.getTable().getCellRect(start, 0, true);
                    // 垂直上加高度偏移
                    r.y += forwards ? delta.height : -delta.height;
                    // X轴方向偏移为0
                    this.dx = 0;

                    int newRow = header.getTable().rowAtPoint(r.getLocation());
                    // 到行首和行尾的处理
                    if (newRow == -1 && forwards) {
                        newRow = header.getTable().getRowCount();
                    }
                    // 设置参数,给父类用
                    this.dy = newRow - start;
                }
                // 是水平方向
                else
                // 得到单元格区域
                {
                    Rectangle r = header.getTable().getCellRect(0, start, true);
                    // 水平上加偏移
                    r.x += forwards ? delta.width : -delta.width;
                    int newColumn = header.getTable().columnAtPoint(r.getLocation());
                    // 到行首和行尾的处理
                    if (newColumn == -1 && forwards) {
                        newColumn = header.getTable().getColumnCount();
                    }
                    // 设置参数,给父类用
                    this.dx = newColumn - start;
                    this.dy = 0;
                }
            }
            // 最后调用父类的方法完成
            super.actionPerformed(e);
        }
    }
}
