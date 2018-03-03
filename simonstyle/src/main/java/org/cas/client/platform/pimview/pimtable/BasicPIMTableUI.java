package org.cas.client.platform.pimview.pimtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellEditor;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.View_PIMDetails;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.util.PIMViewUtil;

/**
 * 本类用于负责表格的绘制和一些用户动作的处理
 * 
 * @NOTE:本类和BasicPIMtableHeaderUI类，被改为非public后发现运行时getUI()返回null，原因未明。
 */

public class BasicPIMTableUI extends ComponentUI {
    /**
     * 创建UI
     * 
     * @param c
     *            传入的组件对象
     * @return 返回本组件的UI
     */
    public static ComponentUI createUI(
            JComponent c) {
        return new BasicPIMTableUI();
    }

    /**
     * 装入UI
     * 
     * @param c
     *            传入的组件对象
     */
    public void installUI(
            JComponent c) {
        pimTable = (PIMTable) c;// 保存组件的引用

        rendererPane = new CellRendererPane(); // 创建单元格绘制面板的实例
        pimTable.add(rendererPane);

        installDefaults(); // 装入缺省值,监听器,键盘映射
        installListeners();
        installKeyboardActions();
    }

    /**
     * 卸载UI
     * 
     * @param c
     *            传入的组件对象
     */
    public void uninstallUI(
            JComponent c) {
        uninstallDefaults(); // 卸载所有
        uninstallListeners();
        uninstallKeyboardActions();

        pimTable.remove(rendererPane);// 清空几个变量
        rendererPane = null;
        pimTable = null;
    }

    /**
     * 重载父类中的方法,得到组件的最小尺寸 Return the minimum size of the table. The minimum height is the row height times the number of
     * rows. The minimum width is the sum of the minimum widths of each column.
     * 
     * @param c
     *            传入的组件对象
     * @return 返回组件的最小尺寸
     */
    public Dimension getMinimumSize(
            JComponent c) {
        long width = 0;
        Enumeration enumeration = pimTable.getColumnModel().getColumns();

        while (enumeration.hasMoreElements()) // 对所有列遍历,累加宽度
        {
            PIMTableColumn aColumn = (PIMTableColumn) enumeration.nextElement();
            width = width + aColumn.getMinWidth();
        }

        return createTableSize(width); // 调用本类的一个方法来得到
    }

    /**
     * 重载父类中的方法,得到组件的首选尺寸 Return the preferred size of the table. The preferred height is the row height times the
     * number of rows. The preferred width is the sum of the preferred widths of each column.
     * 
     * @param c
     *            传入的组件对象
     * @return 返回组件的首选尺寸
     */
    public Dimension getPreferredSize(
            JComponent c) {
        long width = 0;
        Enumeration enumeration = pimTable.getColumnModel().getColumns();

        while (enumeration.hasMoreElements()) // 对所有列遍历,累加宽度,算法同上
        {
            PIMTableColumn aColumn = (PIMTableColumn) enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }

        return createTableSize(width); // 调用本类的一个方法来得到
    }

    /**
     * 重载父类中的方法,得到组件的最大尺寸 Return the maximum size of the table. The maximum height is the row heighttimes the number of
     * rows. The maximum width is the sum of the maximum widths of each column.
     * 
     * @param c
     *            传入的组件对象
     * @return 返回组件的最大尺寸
     */
    public Dimension getMaximumSize(
            JComponent c) {
        long width = 0;
        Enumeration enumeration = pimTable.getColumnModel().getColumns();

        while (enumeration.hasMoreElements()) // 对所有列遍历,累加宽度,算法同上
        {
            PIMTableColumn aColumn = (PIMTableColumn) enumeration.nextElement();
            width = width + aColumn.getMaxWidth();
        }

        return createTableSize(width); // 调用本类的一个方法来得到
    }

    /**
     * 绘制的方法 Paint a representation of the <code>table</code> instance that was set in installUI().
     * 
     * @param g
     *            图形设备上下文
     * @param c
     *            传入的组件对象
     */
    public void paint(
            Graphics g,
            JComponent c) {
        if (pimTable.getRowCount() <= 0 || pimTable.getColumnCount() <= 0)
            return; // 如果列数小于1，则不绘制。

        Rectangle tmpClip = g.getClipBounds(); // 得到g的绘制区域。
        Point tmpUpperLeft = tmpClip.getLocation(); // 得到需要绘制区域左上角的位置，和需要绘制区域右下角的坐标。
        Point tmpLowerRight = new Point(tmpClip.x + tmpClip.width - 1, tmpClip.y + tmpClip.height - 1);
        int rMin = pimTable.rowAtPoint(tmpUpperLeft);
        int rMax = pimTable.rowAtPoint(tmpLowerRight);
        int cMin = pimTable.columnAtPoint(tmpUpperLeft);
        int cMax = pimTable.columnAtPoint(tmpLowerRight);// 得到了需要绘制区域的最小和最大列号，最小和最大行号。

        // 除错处理--------------------------------------------------------
        if (rMin == -1)
            rMin = 0;
        if (cMin == -1)
            cMin = 0;
        if (rMax == -1)
            rMax = pimTable.getRowCount() - 1; // If the table does not have enough columns to
        if (cMax == -1)
            cMax = pimTable.getColumnCount() - 1; // fill the view we'll get -1. Replace this with
                                                  // the index of the last column.
        // Begin to Paint the cells.--------------------------------------
        paintCells(g, rMin, rMax, cMin, cMax); // 画单元格
        paintGrid(g, rMin, rMax, cMin, cMax); // 画网格线
    }

    /**
     * 卸载缺省的设置
     */
    protected void uninstallDefaults() {
        if (pimTable.getTransferHandler() instanceof UIResource)
            pimTable.setTransferHandler(null); // 确保一下
    }

    /**
     * 卸载所有监听器
     */
    protected void uninstallListeners() {
        pimTable.removeFocusListener(focusListener); // 先移除,再清空
        pimTable.removeKeyListener(keyListener);
        pimTable.removeMouseListener(dragRecognizer);
        pimTable.removeMouseMotionListener(dragRecognizer);
        pimTable.removeMouseListener(mouseInputHandler);
        pimTable.removeMouseMotionListener(mouseInputHandler);
        pimTable.removePropertyChangeListener(propertyChangeListener);

        focusListener = null;// 再清空
        keyListener = null;
        mouseInputHandler = null;
        propertyChangeListener = null;
    }

    /**
     * 注销注册的键盘动作
     */
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(pimTable, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null); // 通过
                                                                                                         // SwingUtilities
                                                                                                         // 来完成
        SwingUtilities.replaceUIActionMap(pimTable, null);
    }

    /**
     * 加载缺省的设置 Initialize PIMTable properties, e.g. font, foreground, and background. The font, foreground, and
     * background properties are only set if their current value is either null or a UIResource, other properties are
     * set if the current value is null.
     *
     * @see #installUI
     */
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(pimTable, "Table.background", "Table.foreground", "Table.font"); // 装入观感

        Color sbg = pimTable.getSelectionBackground();

        if (sbg == null || sbg instanceof UIResource) // 得到背景色
            pimTable.setSelectionBackground(UIManager.getColor("PIMTable.selectionBackground"));

        Color sfg = pimTable.getSelectionForeground(); // 得到前景色
        if (sfg == null || sfg instanceof UIResource)
            pimTable.setSelectionForeground(UIManager.getColor("PIMTable.selectionForeground"));

        Color gridColor = pimTable.getGridColor();// 得到网格线颜色
        if (gridColor == null || gridColor instanceof UIResource)
            pimTable.setGridColor(UIManager.getColor("Table.gridColor"));

        // 装入滚动面板的观感 install the scrollpane border
        Container parent = pimTable.getParent(); // should be viewport
        if (parent != null) {
            parent = parent.getParent(); // should be the scrollpane
            if (parent != null && parent instanceof PIMScrollPane)
                LookAndFeel.installBorder((PIMScrollPane) parent, "Table.scrollPaneBorder");
        }

        TransferHandler th = pimTable.getTransferHandler(); // 得到数据转移句柄,没有的话就将table的数据转移句柄设为本类缺省的
        if (th == null || th instanceof UIResource)
            pimTable.setTransferHandler(defaultTransferHandler);

        DropTarget dropTarget = pimTable.getDropTarget(); // 放下目标的处理
        if (dropTarget instanceof UIResource) {
            if (dropTargetListener == null)
                dropTargetListener = new TableDropTargetListener(); // 新建一个

            try {
                dropTarget.addDropTargetListener(dropTargetListener); // 加载上去
            } catch (TooManyListenersException tmle) {
            }
        }
    }

    /**
     * 加载所有监听器 Attaches listeners to the PIMTable.
     */
    protected void installListeners() {
        focusListener = createFocusListener(); // 先统统创建出实例
        keyListener = createKeyListener();
        mouseInputHandler = createMouseInputListener();
        propertyChangeListener = createPropertyChangeListener();

        pimTable.addFocusListener(focusListener); // 统统加上去
        pimTable.addKeyListener(keyListener);
        pimTable.addMouseListener(dragRecognizer);
        pimTable.addMouseMotionListener(dragRecognizer);
        pimTable.addMouseListener(mouseInputHandler);
        pimTable.addMouseMotionListener(mouseInputHandler);
        pimTable.addPropertyChangeListener(propertyChangeListener);
    }

    /**
     * 注册键盘动作 Register all keyboard actions on the PIMTable.
     */
    protected void installKeyboardActions() {
        ActionMap map = getActionMap(); // 得到我们的动作映射表
        SwingUtilities.replaceUIActionMap(pimTable, map); // 把表格的动作映射表换掉
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT); // 得到输入映射,也统统换掉
        SwingUtilities.replaceUIInputMap(pimTable, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
    }

    /**
     * 创建一个键盘监听器的实例 Creates the key listener for handling keyboard navigation in the PIMTable.
     * 
     * @return 返回一个键盘监听器的实例
     */
    protected KeyListener createKeyListener() {
        return null;
    }

    /**
     * 创建一个焦点监听器的实例 Creates the focus listener for handling keyboard navigation in the PIMTable.
     * 
     * @return 返回一个焦点监听器的实例
     */
    protected FocusListener createFocusListener() {
        return new FocusHandler(this);
    }

    /**
     * 创建一个鼠标监听器的实例 Creates the mouse listener for the PIMTable.
     * 
     * @return 返回一个鼠标监听器的实例
     */
    protected MouseInputHandler createMouseInputListener() {
        return new MouseInputHandler(this);
    }

    /**
     * 得到输入映射
     * 
     * @param condition
     *            条件,是一个整形的ID值
     * @return 返回输入映射
     */
    InputMap getInputMap(
            int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)// 是这么个条件就做
        {
            InputMap keyMap = (InputMap) UIManager.get("Table.ancestorInputMap");// 先得到键盘映射
            InputMap rtlKeyMap;

            if (pimTable.getComponentOrientation().isLeftToRight()
                    || ((rtlKeyMap = (InputMap) UIManager.get("Table.ancestorInputMap.RightToLeft")) == null))
                return keyMap; // 如果组件是自左到右或父类也是,就将其返回
            else {
                rtlKeyMap.setParent(keyMap); // 否则使用父类的映射
                return rtlKeyMap;
            }
        }
        return null;
    }

    /**
     * 得到动作映射
     * 
     * @return 返回动作映射
     */
    ActionMap getActionMap() {
        ActionMap map = (ActionMap) UIManager.get("Table.actionMap");// 先得到表格的动作映射
        if (map == null) // 如果没有就创建一个
        {
            map = createActionMap();
            if (map != null) // 再放入观感里去
                UIManager.getLookAndFeelDefaults().put("Table.actionMap", map);
        }
        return map;
    }

    /**
     * 创建动作映射
     * 
     * @return 得到动作映射
     */
    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource(); // 新建一个动作映射资源

        map.put("selectNextColumn", new NavigationalAction(1, 0, false, false, false));// 放入方向键控制
        map.put("selectPreviousColumn", new NavigationalAction(-1, 0, false, false, false));
        map.put("selectNextRow", new NavigationalAction(0, 1, false, false, false));
        map.put("selectPreviousRow", new NavigationalAction(0, -1, false, false, false));

        map.put("selectNextColumnExtendSelection", new NavigationalAction(1, 0, false, true, false));// 放入方向键扩展控制
        map.put("selectPreviousColumnExtendSelection", new NavigationalAction(-1, 0, false, true, false));
        map.put("selectNextRowExtendSelection", new NavigationalAction(0, 1, false, true, false));
        map.put("selectPreviousRowExtendSelection", new NavigationalAction(0, -1, false, true, false));

        map.put("scrollUpChangeSelection", new PagingAction(this, false, false, true, false)); // 放入翻页键控制
        map.put("scrollDownChangeSelection", new PagingAction(this, false, true, true, false));
        map.put("selectFirstColumn", new PagingAction(this, false, false, false, true));
        map.put("selectLastColumn", new PagingAction(this, false, true, false, true));

        map.put("scrollUpExtendSelection", new PagingAction(this, true, false, true, false));// 放入翻页键扩展控制
        map.put("scrollDownExtendSelection", new PagingAction(this, true, true, true, false));
        map.put("selectFirstColumnExtendSelection", new PagingAction(this, true, false, false, true));
        map.put("selectLastColumnExtendSelection", new PagingAction(this, true, true, false, true));

        map.put("selectFirstRow", new PagingAction(this, false, false, true, true));// 放入翻页键扩展控制
        map.put("selectLastRow", new PagingAction(this, false, true, true, true));

        map.put("selectFirstRowExtendSelection", new PagingAction(this, true, false, true, true)); // 放入翻页键扩展控制
        map.put("selectLastRowExtendSelection", new PagingAction(this, true, true, true, true));

        map.put("selectNextColumnCell", new NavigationalAction(1, 0, true, false, true)); // 放入方向页键扩展控制
        map.put("selectPreviousColumnCell", new NavigationalAction(-1, 0, true, false, true));
        map.put("selectNextRowCell", new NavigationalAction(0, 1, true, false, true));
        map.put("selectPreviousRowCell", new NavigationalAction(0, -1, true, false, true));

        map.put("selectAll", new SelectAllAction());// 放入几个选择快捷键控制
        map.put("cancel", new CancelEditingAction());
        map.put("startEditing", new StartEditingAction());

        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction()); // 放入复制,剪切,粘贴快捷键控制
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());

        if (pimTable.getComponentOrientation().isLeftToRight()) // 如果组件是自左到右还要加上大概是滚动控制
        {
            map.put("scrollLeftChangeSelection", new PagingAction(this, false, false, false, false));
            map.put("scrollRightChangeSelection", new PagingAction(this, false, true, false, false));
            map.put("scrollLeftExtendSelection", new PagingAction(this, true, false, false, false));
            map.put("scrollRightExtendSelection", new PagingAction(this, true, true, false, false));
        }
        return map;
    }

    /*
     * Paints the grid lines within <I>aRect</I>, using the grid color set with <I>setGridColor</I>. Paints vertical
     * lines if <code>getShowVerticalLines()</code> returns true and paints horizontal lines if
     * <code>getShowHorizontalLines()</code> returns true.
     * @param g 图形设备上下文
     * @param rMin 最小行
     * @param rMax 最大行
     * @param cMin 最小列
     * @param cMax 最大列
     */
    private void paintGrid(
            Graphics g,
            int rMin,
            int rMax,
            int cMin,
            int cMax) {
        // 设一下绘制颜色
        g.setColor(pimTable.getGridColor());
        // 得到左上角,右下角的单元格,和整个外框
        Rectangle minCell = pimTable.getCellRect(rMin, cMin, true);
        Rectangle maxCell = pimTable.getCellRect(rMax, cMax, true);
        Rectangle damagedArea = minCell.union(maxCell);
        // 如果视图上有水平线
        if (pimTable.getShowHorizontalLines()) {
            int tableWidth = damagedArea.x + damagedArea.width;
            int y = damagedArea.y;
            // 遍历,一行一行地画
            for (int row = rMin; row <= rMax; row++) {
                y += pimTable.getRowHeight(row);
                g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
            }
        }
        // 如果视图上有竖直线
        if (pimTable.getShowVerticalLines()) {
            IPIMTableColumnModel cm = pimTable.getColumnModel();
            int tableHeight = damagedArea.y + damagedArea.height;
            int x;
            // 我们只处理自左到右
            if (pimTable.getComponentOrientation().isLeftToRight()) {
                x = damagedArea.x;
                // 遍历,一列一列地画
                for (int column = cMin; column <= cMax; column++) {
                    int w = cm.getColumn(column).getWidth();
                    // 现在我还没有查出这一个像素是哪儿来的.
                    x += (column == cMax - 1) ? w - 1 : w;
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
                }
            }
        }
    }

    /**
     * 绘制显示出的单元格,可能是所有的可能是一部分
     * 
     * @param g
     *            图形设备上下文
     * @param rMin
     *            最小行
     * @param rMax
     *            最大行
     * @param cMin
     *            最小列
     * @param cMax
     *            最大列
     */
    private void paintCells(
            Graphics g,
            int rMin,
            int rMax,
            int cMin,
            int cMax) {
        PIMTableHeader tmpHeader = pimTable.getTableHeader(); // 得到表格头和拖动列
        PIMTableColumn tmpDraggedCol = (tmpHeader == null) ? null : tmpHeader.getDraggedColumn();

        IPIMTableColumnModel tmpColModel = pimTable.getColumnModel(); // 得到列模型和列边缘空白
        int tmpColMargin = tmpColModel.getColumnMargin();

        Rectangle tmpCellRect; // 声明一个矩形、列和列
        PIMTableColumn tmpColumn;
        int tmpColWidth;
        if (pimTable.getComponentOrientation().isLeftToRight()) // 在表格是自左到右的情况下
        {
            for (int row = rMin; row <= rMax; row++) // 从最小列到最大列开始进行遍历
            {
                tmpCellRect = pimTable.getCellRect(row, cMin, false); // 设置本行的单元格区域,后从最小行到最大行开始进行遍历
                for (int column = cMin; column <= cMax; column++) {
                    tmpColumn = tmpColModel.getColumn(column); // 从列模型中取得列,和宽度,减去边缘空白为实际宽度
                    tmpColWidth = tmpColumn.getWidth();
                    // cellRect.width = columnWidth - columnMargin;在没有网格线的情况下,减去边缘空白会显示出一个像素的白线,故不减
                    if (pimTable.getShowVerticalLines() == false)
                        tmpCellRect.width = tmpColWidth;
                    else
                        tmpCellRect.width = tmpColWidth - tmpColMargin;

                    if (tmpColumn != tmpDraggedCol)// 如当前列不是拖动列就直接绘制单元格
                        paintCell(g, tmpCellRect, row, column);

                    tmpCellRect.x += tmpColWidth; // X坐标递增
                }
            }
        }

        if (tmpDraggedCol != null) // Paint the dragged column if we are dragging.
            paintDraggedArea(g, rMin, rMax, tmpDraggedCol, tmpHeader.getDraggedDistance());

        rendererPane.removeAll(); // Remove any renderers that may be left in the rendererPane.
    }

    /**
     * 绘制指定的单元格
     * 
     * @param g
     *            图形设备上下文
     * @param cellRect
     *            绘制区域
     * @param row
     *            所在列
     * @param column
     *            所在列
     */
    private void paintCell(
            Graphics g,
            Rectangle cellRect,
            int row,
            int column) {
        if (pimTable.isEditing() && pimTable.getEditingRow() == row && pimTable.getEditingColumn() == column)// 如果
        { // 表格处在编辑中,且正是本单元格,
            Component tmpComp = pimTable.getEditorComponent(); // 在上面放上一个组件。
            Rectangle tmpRect = (Rectangle) cellRect.clone(); // 设置其区域
            if (tmpComp instanceof JComboBox && column != pimTable.getColumnCount() - 1) // 在outlook中，它的下拉框
            { // 编辑器显示区域一般向右突破，至箭头按钮空在外面，
                tmpRect.width = tmpRect.width + 18; // 所以加上一个延伸以保持显示效果与微软一致。
            }
            tmpComp.setBounds(tmpRect);
            tmpComp.validate();
        } else // 正常绘制某一单元
        {
            IPIMCellRenderer tmpRenderer = pimTable.getCellRenderer(row, column); // 取得表格的单元格绘制器,
            Component tmpComp = pimTable.prepareRenderer(tmpRenderer, row, column); // 取得表格的该单元格组件,
            rendererPane.paintComponent(g, tmpComp, pimTable, cellRect.x, cellRect.y, cellRect.width, cellRect.height,
                    true); // 在视图上绘制出该组件
        }
    }

    /**
     * 绘制拖动区域
     * 
     * @param g
     *            图形设备上下文
     * @param rMin
     *            最小行
     * @param rMax
     *            最大行
     * @param draggedColumn
     *            拖动列
     * @param distance
     *            距离
     */
    private void paintDraggedArea(
            Graphics g,
            int rMin,
            int rMax,
            PIMTableColumn draggedColumn,
            int distance) {
        // 得到拖动列的索引
        int draggedColumnIndex = viewIndexForColumn(draggedColumn);

        // 取得最上行单元格和最下行(视图上)的单元格(整个表体)区域
        Rectangle minCell = pimTable.getCellRect(rMin, draggedColumnIndex, true);
        Rectangle maxCell = pimTable.getCellRect(rMax, draggedColumnIndex, true);

        // 整合为一个矩形
        Rectangle vacatedColumnRect = minCell.union(maxCell);

        // Paint a gray well in place of the moving column.
        g.setColor(pimTable.getParent().getBackground());
        g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width, vacatedColumnRect.height);

        // Move to the where the cell has been dragged.
        // 整个拖动矩形加上个偏移
        vacatedColumnRect.x += distance;

        // Fill the background.
        g.setColor(pimTable.getBackground());
        g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width, vacatedColumnRect.height);

        // Paint the vertical grid lines if necessary.
        // 下面一部分是绘制拖动列中的网格线
        // 如果表格要绘制竖直线
        if (pimTable.getShowVerticalLines()) {
            g.setColor(pimTable.getGridColor());
            // 取得四个边角的坐标
            int x1 = vacatedColumnRect.x;
            int y1 = vacatedColumnRect.y;
            int x2 = x1 + vacatedColumnRect.width - 1;
            int y2 = y1 + vacatedColumnRect.height - 1;
            // Left 绘制左右网格线
            g.drawLine(x1 - 1, y1, x1 - 1, y2);
            // Right
            g.drawLine(x2, y1, x2, y2);
        }
        // 下面一部分是绘制拖动列中的单元格内容
        for (int row = rMin; row <= rMax; row++) {
            // Render the cell value
            // 得到拖动列的单元格区域,加上偏移
            Rectangle r = pimTable.getCellRect(row, draggedColumnIndex, false);
            r.x += distance;
            // 绘制拖动列内容
            paintCell(g, r, row, draggedColumnIndex);

            // Paint the (lower) horizontal grid line if necessary.
            // 绘制完成了再绘制水平网格线
            if (pimTable.getShowHorizontalLines()) {
                // 设置颜色
                g.setColor(pimTable.getGridColor());
                Rectangle rcr = pimTable.getCellRect(row, draggedColumnIndex, true);
                // 加偏移
                rcr.x += distance;
                int x1 = rcr.x;
                int y1 = rcr.y;
                int x2 = x1 + rcr.width - 1;
                int y2 = y1 + rcr.height - 1;
                // 绘制水平网格线
                g.drawLine(x1, y2, x2, y2);
            }
        }
    }

    /**
     * 得到某列的索引
     * 
     * @param aColumn
     *            指定列
     * @return 返回
     */
    private int viewIndexForColumn(
            PIMTableColumn aColumn) {
        // 得到列模型,从第一列到最后一列开始遍历
        IPIMTableColumnModel cm = pimTable.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            // 比较相等就可返回了
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    /**
     * 创建表格的尺寸
     * 
     * @param width
     *            表格的宽度
     * @return 返回面积
     */
    private Dimension createTableSize(
            long width) {
        int height = 0;
        int rowCount = pimTable.getRowCount();
        // 有几行数据且有几列
        if (rowCount > 0 && pimTable.getColumnCount() > 0) {
            // 得到最后一列的矩形区域
            Rectangle r = pimTable.getCellRect(rowCount - 1, 0, true);
            height = r.y + r.height;
        }
        // Width is always positive. The call to abs() is a workaround for
        // a bug in the 1.1.6 JIT on Windows.
        // 下面四行的处理和系统BUG有关
        long tmp = Math.abs(width);
        if (tmp > Integer.MAX_VALUE) {
            tmp = Integer.MAX_VALUE;
        }
        // 返回时转为整型
        return new Dimension((int) tmp, height);
    }

    /**
     * 创建一个属性变化监听器的实例 Creates the property change listener for the PIMTable.
     * 
     * @return 返回属性变化监听器的实例
     */
    private PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler(this);
    }

    // variables-------------------------------------------------------
    // 保存缺省拖动确认的句柄
    private static final TableDragGestureRecognizer dragRecognizer = new TableDragGestureRecognizer();
    // 保存缺省放下目标句柄
    static DropTargetListener dropTargetListener; // @NOTE:提高可见性，以追求更好的性能。

    // 键盘动作＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
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
         * 以下两个用来保存上次的选定格,用于上下箭头用
         */
        private int lastAnchorColumn = -100;

        /**
         * 上次选定行,用于上下箭头
         */
        private int lastAnchorRow = -100;

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
         *            是否为行选中
         * @param isLead
         *            是否为列选中
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
         * @param table
         *            表格实例
         * @param dx
         *            X坐标的偏移值
         * @param dy
         *            Y坐标的偏移值
         * @param changeLead
         *            不知道
         */
        private void moveWithinTableRange(
                PIMTable table,
                int dx,
                int dy,
                boolean changeLead) {
            // 标志为真的情况下
            if (changeLead) {
                // 起始行取起始行加偏移数和0之间的最大值与总行数比出最小的,列的算法与之相同
                leadRow = clipToRange(leadRow + dy, 0, table.getRowCount(), true, true);
                leadColumn = clipToRange(leadColumn + dx, 0, table.getColumnCount(), false, true);
            }
            // 标志为假的情况下
            else {
                // 起始行取锚定行加偏移数和0之间的最大值与总行数比出最小的,列的算法与之相同
                anchorRow = clipToRange(anchorRow + dy, 0, table.getRowCount(), true, false);
                anchorColumn = clipToRange(anchorColumn + dx, 0, table.getColumnCount(), false, false);
            }
        }

        /**
         * 得到选中的范围
         * 
         * @param sm
         *            ListSelectionModel 接口的实例
         * @return 返回选中的范围
         */
        private int selectionSpan(
                ListSelectionModel sm) {
            // 返回列选择模型中保存的数据
            return sm.getMaxSelectionIndex() - sm.getMinSelectionIndex() + 1;
        }

        /**
         * 比较某一列在列选择模型中的?
         * 
         * @param i
         *            某一列的索引
         * @param sm
         *            ListSelectionModel 接口的实例
         * @return 比较结果
         */
        private int compare(
                int i,
                ListSelectionModel sm) {
            // 本列和列选择模型的最小选中列之间的最大值与列选择模型的最大选中列比出最小的
            return compare(i, sm.getMinSelectionIndex(), sm.getMaxSelectionIndex() + 1);
        }

        /**
         * 比较两个数大小
         * 
         * @param i
         *            第一操作数
         * @param a
         *            第二操作数
         * @param b
         *            第三操作数
         * @return 比较结果
         */
        private int compare(
                int i,
                int a,
                int b) {
            // 第一操作数小于第二操作数返回-1,否则如不小于第三操作数返回1,否则返回0
            return (i < a) ? -1 : (i >= b) ? 1 : 0;
        }

        /**
         * 判断是否在选择范围内移动
         * 
         * @param table
         *            表格实例
         * @param dx
         *            X坐标的偏移值
         * @param dy
         *            Y坐标的偏移值
         * @param ignoreCarry
         *            可忽略条件
         * @return 返回是否在选择范围内移动的标志
         */
        private boolean moveWithinSelectedRange(
                PIMTable table,
                int dx,
                int dy,
                boolean ignoreCarry) {
            // 取表格的列选择模型,及从列模型中得到列选择模型
            ListSelectionModel rsm = table.getSelectionModel();
            ListSelectionModel csm = table.getColumnModel().getSelectionModel();

            // 初始新锚定行和锚定列(加上偏移)
            int newAnchorRow = anchorRow + dy;
            int newAnchorColumn = anchorColumn + dx;

            // 声明行信号和列信号
            int rowSgn;
            int colSgn;
            // 得到选择的行数和列数
            int rowCount = selectionSpan(rsm);
            int columnCount = selectionSpan(csm);

            // 这个变量表示不止一格被选中
            boolean canStayInSelection = (rowCount * columnCount > 1);
            if (canStayInSelection) {
                // 得到操作后的位置
                rowSgn = compare(newAnchorRow, rsm);
                colSgn = compare(newAnchorColumn, csm);
            } else {
                // 在没有选择的情况下
                rowCount = table.getRowCount();
                columnCount = table.getColumnCount();
                // 得到操作后的位置
                rowSgn = compare(newAnchorRow, 0, rowCount);
                colSgn = compare(newAnchorColumn, 0, columnCount);
            }
            // 得到新锚定行和锚定列(加上偏移)
            anchorRow = newAnchorRow - rowCount * rowSgn;
            anchorColumn = newAnchorColumn - columnCount * colSgn;

            // 在不可忽略搬动的情况下递归
            if (!ignoreCarry) {
                return moveWithinSelectedRange(table, rowSgn, colSgn, true);
            }
            // 否则返回仍在选择区域中的标志
            return canStayInSelection;
        }

        /**
         * 实现父类中的抽象方法,执行操作
         * 
         * @param e
         *            动作事件源
         */
        public void actionPerformed(
                ActionEvent e) {
            // int dx, int dy, boolean toggle, boolean extend,boolean inSelection
            PIMTable table = (PIMTable) e.getSource();

            // 这个布尔值表示这种情况是选中一条记录后按下回车键,应弹出编辑或显示
            // 邮件的对话盒 目前在以下几种视图应可用
            boolean showDialog = dx == 0 && dy == 1 && toggle && !extend && inSelection;
            if (table.getView() != null) {
                int tmpAppType = table.getView().getApplication().getActiveViewInfo().getAppIndex();
                if (tmpAppType == ModelCons.INBOX_APP || tmpAppType == ModelCons.OUTBOX_APP
                        || tmpAppType == ModelCons.SENDED_APP || tmpAppType == ModelCons.DRAFT_APP) {
                    // 显示后这退了
                    if (showDialog) {
                        int tmpSelectedRow = table.getSelectedRow();
                        if (tmpSelectedRow >= 0) {
                            Object idOb = table.getValueAt(tmpSelectedRow, 0);
                            if (idOb != null) {
                                table.getView()
                                        .getApplication()
                                        .processMouseDoubleClickAction(table, -1, -1, Integer.parseInt(idOb.toString()));
                            }
                        }
                        return;
                    }
                }
            }

            ListSelectionModel rsm = table.getSelectionModel(); // 得到动作实施者,取其行选择模型

            anchorRow = rsm.getAnchorSelectionIndex(); // 得到选择的起始行数起始列
            leadRow = rsm.getLeadSelectionIndex();

            ListSelectionModel csm = table.getColumnModel().getSelectionModel(); // 从列模型中得到列选择模型,得到选择的锚定行数锚定列
            anchorColumn = csm.getAnchorSelectionIndex();
            leadColumn = csm.getLeadSelectionIndex();

            // 保存旧的锚定行数锚定列
            int oldAnchorColumn = anchorColumn;

            if (table.isEditing()) // && !) //如在编辑中且编辑没有停止,不进行任何操作
            {
                boolean stopCellEditingOK = table.getCellEditor().stopCellEditing();
                if (!stopCellEditingOK) {
                    return;
                } else {
                    table.removeEditor();
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
                // 在表格范围内移动
                moveWithinTableRange(table, dx, dy, extend);
                // 处理上下键
                if (anchorRow == -100) {
                    if (!table.hasEditor() && table.getRowCount() > 0) {
                        // 表格没有快速编辑栏时,应跳到第一行去
                        table.changeSelection(0, anchorColumn, false, extend, false);
                        return;
                    }
                    table.clearSelection();
                    PIMTableHeader header = table.getTableHeader();
                    header.setEditingColumn(anchorColumn);
                    header.setSelectedColumnIndex(anchorColumn);
                    if (!header.editCellAt(1, anchorColumn)) {
                        return;
                    }
                    header.getEditorComponent().requestFocus();
                    header.repaint();
                    return;
                }
                // 如果不要扩展
                if (!extend) {
                    // 在本行中方向键进行左右操作时走这里
                    // 这个变量表示到了最后一列,按下TAB键时,是跳到最前面去的
                    boolean tabFromBegin = false;
                    if (dx == -1) {
                        if (anchorColumn == 0) {
                            // 在邮件类表格视图中,是不可以循环滚动的,要return
                            // 2003.9.29
                            if (table.getView() != null) {
                                int tmpAppType = table.getView().getApplication().getActiveViewInfo().getAppIndex();
                                if (tmpAppType == ModelCons.INBOX_APP || tmpAppType == ModelCons.OUTBOX_APP
                                        || tmpAppType == ModelCons.SENDED_APP || tmpAppType == ModelCons.DRAFT_APP
                                        || tmpAppType == ModelCons.DELETED_ITEM_APP) {
                                    return;
                                }
                            }
                        }
                        anchorColumn++;
                        anchorColumn = selectPreAnchorColumn(anchorColumn, table);
                    } else {
                        // TODO: 这里可能还不彻底
                        // 2003/9/28 加了两个判断,应有所完善主要处理向右方向键
                        if (dy == 0 && dx != 1 && (lastAnchorColumn != -100 && lastAnchorColumn == anchorColumn)
                                && (lastAnchorRow != -100 && lastAnchorRow == anchorRow)) {
                            anchorColumn = 0;
                        }
                        anchorColumn--;
                        anchorColumn = selectNextAnchorColumn(anchorColumn, table);
                        if (anchorColumn < oldAnchorColumn) {
                            tabFromBegin = true;
                        }
                    }
                    // 表格要在锚定列中重设一下选中的
                    // 编辑退出后,才可能知道是表格体选中过
                    table.setLazySelectedRow(anchorRow);
                    table.changeSelection(anchorRow, anchorColumn, false, extend, tabFromBegin);
                    lastAnchorColumn = anchorColumn;
                    lastAnchorRow = anchorRow;
                } else {
                    // 表格要在起始列中重设一下选中的
                    // 编辑退出后,才可能知道是表格体选中过
                    table.setLazySelectedRow(leadRow);
                    table.changeSelection(leadRow, leadColumn, false, extend, false);
                }
            } // 在选择中
            else {
                // 在选择区中调整一下
                if (moveWithinSelectedRange(table, dx, dy, false)) {
                    // 表格要在锚定列中重设一下选中的
                    // 编辑退出后,才可能知道是表格体选中过
                    table.setLazySelectedRow(anchorRow);
                    table.changeSelection(anchorRow, anchorColumn, true, true, false);
                } else {
                    // 这个变量表示到了最后一列,按下TAB键时,是跳到最前面去的
                    boolean tabFromBegin = false;
                    if (dx == -1) {
                        anchorColumn++;
                        anchorColumn = selectPreAnchorColumn(anchorColumn, table);
                    } else {
                        anchorColumn--;
                        anchorColumn = selectNextAnchorColumn(anchorColumn, table);

                        if (anchorColumn < oldAnchorColumn) {
                            tabFromBegin = true;
                        }
                    }
                    // 表格要在锚定列中重设一下选中的
                    // 编辑退出后,才可能知道是表格体选中过
                    table.setLazySelectedRow(anchorRow);
                    // 在shift + tab 键到了首列时,再一次按下,应到上一行,但不加下
                    // 一个判断就仍在本行中
                    if (anchorColumn == table.getColumnCount() - 1 && dx == -1) {
                        anchorRow--;
                    }
                    table.changeSelection(anchorRow, anchorColumn, false, false, tabFromBegin);
                    // 2003.11.13,在表格停止编译后,会跳开,表格会失去焦点,下一句弥补这个缺陷.
                    table.requestFocus();
                }
            }

            if (table.getParent() != null) {
                table.getParent().repaint();
            }
        }

        /**
         * 本方法使用递归算法来找到下一个可置焦点的列索引
         * 
         * @param prmAnchorColumn
         *            原锚定列
         * @param prmTable
         *            主要是由于本方法所在类是静态类, BasicPIMTableHeaderUI 中的表格头的引用不可直接使用,必须传入,
         * @return 计算出的可设置焦点的列索引
         * @called by NavigationalAction.ActionPerformed
         */
        private int selectNextAnchorColumn(
                int prmAnchorColumn,
                PIMTable prmTable) {
            int anchorColumn = prmAnchorColumn + 1;
            if (anchorColumn >= prmTable.getColumnModel().getColumnCount()) {
                anchorColumn = 0;
            }
            PIMTableColumn tmpColumn = prmTable.getColumnModel().getColumn(anchorColumn);
            // TODO: 判断,如本列宽度为零或不可编辑,递归处理
            // 某几个列是不可编辑的,这是列的一个属性,视图面板上的第一列"ID"是隐
            // 私的,其最大宽度是0,也不可以得到焦点
            if (!tmpColumn.getEditorEnable() || tmpColumn.getMaxWidth() == 0) {
                return selectNextAnchorColumn(anchorColumn, prmTable);
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
         * @param prmTable
         *            主要是由于本方法所在类是静态类, BasicPIMTableHeaderUI 中的表格头的引用不可直接使用,必须传入,
         * @return 计算出的可设置焦点的列索引
         * @called by NavigationalAction.ActionPerformed
         */
        private int selectPreAnchorColumn(
                int prmAnchorColumn,
                PIMTable prmTable) {
            int anchorColumn = prmAnchorColumn - 1;
            if (anchorColumn < 0) {
                anchorColumn = prmTable.getColumnModel().getColumnCount() - 1;
            }
            PIMTableColumn tmpColumn = prmTable.getColumnModel().getColumn(anchorColumn);
            // TODO: 判断,如本列宽度为零或不可编辑,递归处理
            // 某几个列是不可编辑的,这是列的一个属性,视图面板上的第一列"ID"是隐
            // 私的,其最大宽度是0,也不可以得到焦点
            if (!tmpColumn.getEditorEnable() || tmpColumn.getMaxWidth() == 0) {
                return selectPreAnchorColumn(anchorColumn, prmTable);
            } else {
                // 正常处理
                return anchorColumn;
            }
        }
    }

    /**
     * 处理翻页动作的类========================================================================================================
     * ==========================================================
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
         * 对本类的引用,静态类用
         */
        BasicPIMTableUI tableUI;

        /**
         * 处理翻页动作的类建器,从上面一个类扩展的
         * 
         * @param ui
         *            是对本类的引用
         * @param extend
         *            是否扩展的标志
         * @param forwards
         *            翻页的方向
         * @param vertically
         *            是否是竖直方向
         * @param toLimit
         *            是否有限制
         */
        public PagingAction(BasicPIMTableUI ui, boolean extend, boolean forwards, boolean vertically, boolean toLimit) {
            // 先调用父类的构建器
            super(0, 0, false, extend, false);
            tableUI = ui;
            // 本地变量初始化一下
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
            PIMTable table = (PIMTable) e.getSource();
            // 如果是到最后(包括最前面)
            if (toLimit) {
                // 垂直方向
                if (vertically) {
                    int rowCount = table.getRowCount();
                    this.dx = 0;
                    // 设置偏移位置
                    this.dy = forwards ? rowCount : -rowCount;
                }
                // 水平方向
                else {
                    int colCount = table.getColumnCount();
                    // 设置偏移位置
                    this.dx = forwards ? colCount : -colCount;
                    this.dy = 0;
                }
            }
            // 一页一页地来
            else {
                // 表格不放在 PIMScrollPane 滚动面板中就返回
                if (!(table.getParent().getParent() instanceof PIMScrollPane)) {
                    return;
                }
                // 得到 ViewPort 视口的面积
                Dimension delta = table.getParent().getSize();
                // 是垂直方向取列选择模型,否则取行选择模型
                ListSelectionModel sm =
                        (vertically) ? table.getSelectionModel() : table.getColumnModel().getSelectionModel();

                // 如果是扩展,开始处取起始索引,否则取锚定索引
                int start = (extend) ? sm.getLeadSelectionIndex() : sm.getAnchorSelectionIndex();

                // 是垂直方向
                if (vertically) {
                    // 得到单元格区域
                    Rectangle r = table.getCellRect(start, 0, true);
                    // 垂直上加高度偏移
                    r.y += forwards ? delta.height : -delta.height;
                    // X轴方向偏移为0
                    this.dx = 0;

                    int newRow = table.rowAtPoint(r.getLocation());
                    // 到行首和行尾的处理
                    if (newRow == -1 && forwards) {
                        newRow = table.getRowCount();
                    }
                    // 设置参数,给父类用
                    this.dy = newRow - start;
                }
                // 是水平方向
                else
                // 得到单元格区域
                {
                    Rectangle r = table.getCellRect(0, start, true);
                    // 水平上加偏移
                    r.x += forwards ? delta.width : -delta.width;
                    int newColumn = table.columnAtPoint(r.getLocation());
                    // 到行首和行尾的处理
                    if (newColumn == -1 && forwards) {
                        newColumn = table.getColumnCount();
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

    // ================================================================================================================================================

    /**
     * 本类用于解析和处理全部选中这个动作 Ctrl + A Action Action to invoke <code>selectAll</code> on the table.
     */
    public static class SelectAllAction extends AbstractAction {
        /**
         * 实现父类中的抽象方法,执行操作
         * 
         * @param e
         *            动作事件源
         */
        public void actionPerformed(
                ActionEvent e) {
            // 很简单,调用表格的方法即可
            PIMTable table = (PIMTable) e.getSource();
            table.selectAll();
        }
    }

    // =================================================================================================================================================

    /**
     * 本类用于解析和处理编辑取消的动作 Esc Action Action to invoke <code>removeEditor</code> on the table.
     */
    public static class CancelEditingAction extends AbstractAction {
        /**
         * 实现父类中的抽象方法,执行操作
         * 
         * @param e
         *            动作事件源
         */
        public void actionPerformed(
                ActionEvent e) {
            // 很简单,调用表格的方法,即移除掉编辑器
            PIMTable table = (PIMTable) e.getSource();
            table.removeEditor();
        }
    }

    // ===================================================================================================================================================================================

    /**
     * 本类用于解析和处理开始编辑的动作 Action to start editing, and pass focus to the editor.
     */
    public static class StartEditingAction extends AbstractAction {
        /**
         * 实现父类中的抽象方法,执行操作
         * 
         * @param e
         *            动作事件源
         */
        public void actionPerformed(
                ActionEvent e) {
            PIMTable table = (PIMTable) e.getSource();
            // 表格没有焦点的处理
            if (!table.hasFocus()) {
                // 得到单元格编辑器
                CellEditor cellEditor = table.getCellEditor();
                // 单元格编辑器不为空且未停止编辑就返回
                if (cellEditor != null && !cellEditor.stopCellEditing()) {
                    return;
                }
                // 否则表格请示焦点后返回
                table.requestFocus();
                return;
            }
            // 取行模型,得到锚定选中的索引
            ListSelectionModel rsm = table.getSelectionModel();
            int anchorRow = rsm.getAnchorSelectionIndex();
            // 取列模型,得到锚定选中的索引
            ListSelectionModel csm = table.getColumnModel().getSelectionModel();
            int anchorColumn = csm.getAnchorSelectionIndex();
            // 在该单元格进行编辑
            table.editCellAt(anchorRow, anchorColumn, null);
            // 取当前编辑器组件
            Component editorComp = table.getEditorComponent();
            // 不为空的情况下让编辑器组件请求焦点
            if (editorComp != null) {
                editorComp.requestFocus();
            }
        }
    }

    // =========================================================================================================================================================================================
    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug. This class should be treated as a
     * &quot;protected&quot; inner class. Instantiate it only within subclasses of BasicTableUI.
     */
    static class FocusHandler implements FocusListener {
        /**
         * 对本类的引用,静态类用
         */
        BasicPIMTableUI tableUI;

        /**
         * 构建器,传入引用
         * 
         * @param tableUI
         *            传入引用
         */
        FocusHandler(BasicPIMTableUI tableUI) {
            this.tableUI = tableUI;
        }

        /**
         * 重新绘制锚定组件的方法
         */
        private void repaintAnchorCell() {
            int rc = tableUI.pimTable.getRowCount();
            int cc = tableUI.pimTable.getColumnCount();
            // 取行模型的锚定选中的索引
            int ar = tableUI.pimTable.getSelectionModel().getAnchorSelectionIndex();
            // 取列模型的锚定选中的索引
            int ac = tableUI.pimTable.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
            // 出错处理
            if (ar < 0 || ar >= rc || ac < 0 || ac >= cc) {
                return;
            }
            // 得到表体的视口区域,重绘一下即可
            Rectangle dirtyRect = tableUI.pimTable.getCellRect(ar, ac, false);
            tableUI.pimTable.repaint(dirtyRect);
        }

        /**
         * 实现焦点监听器接口中的方法,组件获得焦点时调用本方法
         * 
         * @param e
         *            焦点事件源
         */
        public void focusGained(
                FocusEvent e) {
            // 调用一下重绘锚定单元格
            repaintAnchorCell();
        }

        /**
         * 实现焦点监听器接口中的方法,组件失去焦点时调用本方法
         * 
         * @param e
         *            焦点事件源
         */
        public void focusLost(
                FocusEvent e) {
            /*
             * PIMTable table = (PIMTable)e.getSource(); if(table.hasEditor())// && table.getTableHeader().isEditing())
             * { // IPIMTableCellEditor cellEditor = table.getTableHeader().getCellEditor(); if (cellEditor != null) {
             * cellEditor.stopCellEditing(); } table.getTableHeader().removeEditor(); //return ; } //
             */
            // 调用一下重绘锚定单元格
            repaintAnchorCell();
        }
    }

    // =================================================================================================================================================================================================
    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug. This class should be treated as a
     * &quot;protected&quot; inner class. Instantiate it only within subclasses of BasicTableUI.
     */
    static class MouseInputHandler implements MouseListener, MouseMotionListener {
        /**
         * 对本类的引用,静态类用
         */
        BasicPIMTableUI tableUI;

        /**
         * 构建器,传入引用
         * 
         * @param tableUI
         *            传入引用
         */
        MouseInputHandler(BasicPIMTableUI tableUI) {
            this.tableUI = tableUI;
        }

        // Component receiving mouse events during editing.
        // May not be editorComponent.
        /**
         * 保存派发组件的引用
         */
        private Component dispatchComponent;

        /**
         * 保存是否鼠标按下选择的标志
         */
        private boolean selectedOnPress;

        // The Table's mouse listener methods.

        /**
         * 实现接口中的方法,当鼠标点击时会调用本方法
         * 
         * @param e
         *            鼠标事件源
         */
        public void mouseClicked(
                MouseEvent e) {
        }

        /**
         * 设置派发组件 @QESuser008:为什么不能直接返回tmpEditorComponent？
         * 
         * @param e
         *            鼠标事件源
         */
        private void setDispatchComponent(
                MouseEvent e) {
            Component tmpEditorComponent = tableUI.pimTable.getEditorComponent(); // 得到编辑器组件
            Point tmpP = SwingUtilities.convertPoint(tableUI.pimTable, e.getPoint(), tmpEditorComponent); // 将坐标转换为相对于编辑器组件地坐标。
            dispatchComponent = SwingUtilities.getDeepestComponentAt(tmpEditorComponent, tmpP.x, tmpP.y); // 得到其最顶层的组件赋予派发组件变量。
        }

        /**
         * 重新派发事件
         * 
         * @param e
         *            鼠标事件源
         * @return 返回成功的标志
         */
        private boolean repostEvent(
                MouseEvent e) {
            // Check for isEditing() in case another event has caused the editor to be removed. See bug #4306499.
            if (dispatchComponent == null || !tableUI.pimTable.isEditing()) {
                return false;
            }// 出错处理--------------------------------

            // 将本鼠标事件中地x,y坐标转换为相对于派发组件（可能是编辑器组件）的坐标。
            MouseEvent e2 = SwingUtilities.convertMouseEvent(tableUI.pimTable, e, dispatchComponent);
            dispatchComponent.dispatchEvent(e2); // 继续派发事件,（至此事件改为由dispatchComponent派发出的事件了）
            return true; // 返回成功的标志
        }

        /**
         * 设置单元值是否正在被调整
         * 
         * @param flag
         *            是否正在被调整
         */
        private void setValueIsAdjusting(
                boolean flag) {
            // 表格行和列的选择模型均设置正在调整的标志
            tableUI.pimTable.getSelectionModel().setValueIsAdjusting(flag);
            tableUI.pimTable.getColumnModel().getSelectionModel().setValueIsAdjusting(flag);
        }

        // 实现MouseListener的接口－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        /**
         * 实现接口中的方法,当鼠标按下时会调用本方法
         * 
         * @param e
         *            鼠标事件源 鼠标按下事件处理。
         *
         * @NOTE:在adjustTableStatus(e)之后记下table的选中行列号，是为了下一次鼠标按下时， 
         *                                                         在adjustTableStatus(e)方法内部判断“鼠标落下点所在行列”与上次记下的选中行列号是否一致来确定
         *                                                         是否进入编辑状态用的
         *                                                         。adjustTableStatus方法里面不能通过即时地取table地选中行列号来比较，
         *                                                         因为在鼠标拖动时，table地选中行列号会随着鼠标地移动而移动（尽管反白的Cell看上去并没有移动）。
         */
        public void mousePressed(
                MouseEvent e) {
            if (e.isConsumed()) // 除错，本事件如被消耗了就返回，并改标志为假－－－－－－－－－－
            {
                selectedOnPress = false;
                return;
            }
            selectedOnPress = true; // 该标志表示鼠标事件不是一个已消耗的事件。

            // 根据鼠标落点的行是否处于选中状态，决定本次鼠标按下后如果拖放的话将导致拖拉多选还是拖放，如果已选中则拖放，未选中则多选。
            tableUI.pimTable.allowMouseMultSeleOrDragEveFlag = tableUI.pimTable.ALLOWMULSELE;
            int tmpRow = tableUI.pimTable.rowAtPoint(e.getPoint()); // 得到鼠标所在点对应的行和列.
            int[] seleRows = tableUI.pimTable.getSelectedRows();
            if (seleRows.length > 0) {
                for (int i = seleRows.length - 1; i >= 0; i--) {
                    if (seleRows[i] == tmpRow) {
                        tableUI.pimTable.allowMouseMultSeleOrDragEveFlag = tableUI.pimTable.ALLOWDRAG;
                        break;
                    }
                }
            }

            adjustTableStatus(e);
        }

        /**
         * 实现接口中的方法,鼠标释放事件处理
         * 
         * @param e
         *            鼠标事件源
         */
        public void mouseReleased(
                MouseEvent e) {
            if (!selectedOnPress || e.isConsumed() || SwingUtilities.isRightMouseButton(e)
                    || !tableUI.pimTable.isEnabled()) {
                return;
            } // 除错完毕：排除了所有不处理的情况－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－

            // 执行到此，说明本事件未被消耗，且是鼠标左击，且表格处于使能的"经典"有效状态状态.
            repostEvent(e); // 传递事件给谁？

            dispatchComponent = null; // 清空状态 这个变量是干嘛的？
            setValueIsAdjusting(false); // 干嘛用的？可能因为调不到adjustTableStatus，所以专门处理？(adjustTableStatus方法中也有调该方法。）

            if (tableUI.pimTable.getView() != null) // 我想在这里处理有关的任务,邮件等信息
            {
                if (tableUI.pimTable.getView().getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP) {
                    processCurrentViewStyle(); // ？？
                }
            }

            if (tableUI.pimTable.allowMouseMultSeleOrDragEveFlag == tableUI.pimTable.ALLOWDRAG) // 如果按下的行和松开时的所在行相同，则调整选中行，否则认为是做了拖拉选中操作，不做处理。
            {
                int tmpRow = tableUI.pimTable.rowAtPoint(e.getPoint()); // 得到鼠标所在点对应的行和列.
                int tmpCol = tableUI.pimTable.columnAtPoint(e.getPoint());
                tableUI.pimTable.changeSelection(tmpRow, tmpCol, e.isControlDown(), e.isShiftDown(), false); // 改变表格的选中状态。(e);
            }
        }

        /**
         * 实现接口中的方法,鼠标进入事件处理
         * 
         * @param e
         *            鼠标事件源
         */
        public void mouseEntered(
                MouseEvent e) {
        }

        /**
         * 实现接口中的方法,鼠标退出事件处理
         * 
         * @param e
         *            鼠标事件源
         */
        public void mouseExited(
                MouseEvent e) {
        }

        // The Table's mouse motion listener methods.
        /**
         * 实现接口中的方法,鼠标移动事件处理
         * 
         * @param e
         *            鼠标事件源
         */
        public void mouseMoved(
                MouseEvent e) {
        }

        /**
         * 鼠标拖动时使所有经过的行列都被选中.
         * 
         * @param e
         *            鼠标事件源
         */
        public void mouseDragged(
                MouseEvent e) {
            // 因为要支持拖放操作，但是发现支持拖放后，鼠标按下后移动好像超过10个时就导致拖放开始，mouseDragged事件随即
            // 被屏蔽了。目前只好规定：如果选中一个已选中的记录，则可以拖放不可拖拉多选,并在鼠标松开是才改变选中状态。如果选中一个本来未
            // 选中的记录，则在鼠标按下时即清除以前的选中状态，并允许拖拉多选，不许拖放。
            if (e.isConsumed() || SwingUtilities.isRightMouseButton(e) || !tableUI.pimTable.isEnabled()) // 过滤无用信息
            {
                return;
            }// 除错－－－－－－－－－－－－－－
            if (tableUI.pimTable.allowMouseMultSeleOrDragEveFlag == 1) // 如果标记指示说允许拖放，不允许多选，则不多选，直接返回。
            {
                return; // 改标记在mousePress时被设置，如果鼠标所在的是一条已选中的记录则允许拖放不许多选，否则允许多选不许拖放。
            }

            repostEvent(e); // 重新派发本事件
            CellEditor editor = tableUI.pimTable.getCellEditor(); // 得到单元格编辑器
            if (editor == null || editor.shouldSelectCell(e)) // 编辑器没有进入编辑状态，则进行拖拉选中动作，如已经进入编辑则不拖拉选中。
            {
                Point tmpP = e.getPoint();
                int tmpRow = tableUI.pimTable.rowAtPoint(tmpP);
                int tmpCol = tableUI.pimTable.columnAtPoint(tmpP);

                if ((tmpCol == -1) || (tmpRow == -1))// 因为The autoscroller can generate drag events outside the Table's
                                                     // range.须除错。
                {
                    return;
                } // 除错处理完毕-----------------------------------------------------------------------------

                tableUI.pimTable.changeSelection(tmpRow, tmpCol, false, true, false); // 改变表格的选中状态。
            }
        }

        // －－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        /**
         * @called by: self,在鼠标按下时和鼠标抬起(但被确定为无效)时.
         * @param e
         *            鼠标事件
         */
        private void adjustTableStatus(
                MouseEvent e) {
            int tmpRow = tableUI.pimTable.rowAtPoint(e.getPoint()); // 得到鼠标所在点对应的行和列.
            int tmpCol = tableUI.pimTable.columnAtPoint(e.getPoint());

            // @NOTE: The autoscroller can generate drag events outside the Table's range.因而行列号为-1是可能的，这里必须处理。
            if ((tmpRow == -1) || (tmpCol == -1)) {
                return;
            }// 除错------------------------------------------

            if (tableUI.pimTable.hasEditor()) // 如果表头中带编辑栏的话,则做以下额外处理(如：报错，存盘，移除编辑器,还有判断是否只读等）.........................
            {
                PIMTableHeader tmpHeader = tableUI.pimTable.getTableHeader();
                if (tmpHeader.isEditing()) // 如果编辑栏正处在编辑状态，则检查编辑栏中的内容是否合法,不合法则报错。
                {
                    IView tmpView = tableUI.pimTable.getView();
                    if (tmpView != null) {
                        int tmpAppType = tmpView.getApplication().getActiveViewInfo().getAppIndex(); // 得到当前应用类型，类型不同报错内容也不同。
                        if (tmpAppType == ModelCons.CONTACT_APP) {
                            if (PIMViewUtil.isDisplayAsHasValue(tmpHeader) == 0) // "表示为"字段的状态，是有值（正确），没有值但全部字段都没有值（正确），没有值且Header中其他字段有值（错误）
                            {
                                SOptionPane.showErrorDialog(MessageCons.W10619); // “显示为”是一条联系人记录的关键字段，不能为空。请为该联系人记录的“显示为”字段输入内容。
                                return;
                            }// 错误的话报错并返回，不作任何调整－－－－－－－－－－－－－－－－－－－－－－－
                        } else if (tmpAppType == ModelCons.TASK_APP) {
                            if (PIMViewUtil.isSubjectAsHasValue(tmpHeader) == 0) // 如果"主题"字段是有值(正确),没有值但全部字段都没有值(正确),没有值且Header中其他字段有值(错误)
                            {
                                SOptionPane.showErrorDialog(MessageCons.W10622); // “主题”是一条任务记录的关键字段，不能为空。请为该任务记录的“主题”字段输入内容。
                                return;
                            }// 错误的话报错并返回，不作任何调整－－－－－－－－－－－－－－－－－－－－－－－
                        }
                    }

                    // 能执行到此，说明存在表格头且表格头中的内容全为空或合法，另保存并确保快速编辑栏
                    // 退出编辑状态(@NOTE:其中stopCellEditing不会导致存盘，所以要专门调用viewToModel).
                    tmpView.viewToModel();
                    IPIMCellEditor tmpHeaderEditor = tmpHeader.getCellEditor();
                    if (tmpHeaderEditor != null) {
                        tmpHeaderEditor.stopCellEditing();
                        tmpHeader.removeEditor();
                    }
                }

                // 如果选中列是只读字段. 则改为选中第一个可编辑字段。
                if (!tableUI.pimTable.getColumnModel().getColumn(tmpCol).getEditorEnable()) {
                    tmpCol = PIMViewUtil.getFirstTextableColumn(tmpHeader); // TODO:此处仍有BUG,因为如果不可编辑字段被变了位置,就错了
                }
            }
            // 带有快速编辑栏时的"额外"处理结束--------------------------------------------------------------------------------------------

            // 下面是无论是否带有快速编辑栏都要做的"一般"处理---------------------------------------------------------------------------------------
            // 如果事件是鼠标左键，而且表格的该单元格顺利地进入了编辑状态,便把这个鼠标事件重新派发给当前的编辑器组件，干嘛？
            if (SwingUtilities.isLeftMouseButton(e) && tableUI.pimTable.editCellAt(tmpRow, tmpCol, e)) {
                setDispatchComponent(e); // 给派发组件变量赋值（可能是将编辑器组件赋给它了）
                repostEvent(e); // 将事件做适当调整后再继续派发（改为由dispatchComponent发出的事件了）
                tableUI.pimTable.setLazySelectedRow(tmpRow); // 编辑退出后,才可能知道是表格体编辑过
            } // 谁知道究竟发生了什么改变请在此跟贴啊～～～～～～～～
            else if (tableUI.pimTable.isRequestFocusEnabled()) // 如果此单元格不能进入编辑状态,且表格能得到焦点,就让表格得到焦点
            { // @TODO: 关于表格是否可得焦点得判断是不是多余?可能返回false吗?
                tableUI.pimTable.requestFocus();
            }
            // selectedRow怎么可能是selectedRecordID？
            tableUI.pimTable.setSelectedRecordID(tableUI.pimTable.getSelectedRow()); // 这里设置的实际是行号,因为表格头一点击,得到的就是-1

            CellEditor tmpEditor = tableUI.pimTable.getCellEditor(); // 在得到编辑器，并根据是左键事件还是右键事件做不同处理。
            if (tmpEditor == null || tmpEditor.shouldSelectCell(e)) {
                if (SwingUtilities.isRightMouseButton(e)) // 如果是右键事件且选中的是多行，则不改变选中状态。
                {
                    if (tableUI.pimTable.getSelectedRowCount() > 1) {
                        return;
                    }
                }

                boolean tmpAdjusting = e.getID() == MouseEvent.MOUSE_PRESSED; // 区分当前状态（该当前是在鼠标按下时调的还是抬起时调的）并作不同处理。
                setValueIsAdjusting(tmpAdjusting);// 设置调整状态

                // 表格要改变一下选中状态 //编辑退出后,才可能知道是表格体编辑过
                tableUI.pimTable.setLazySelectedRow(tmpRow);

                // 如果新的选中行本来不处于选中状态，或者处于选中状态，但本事件是由mouseRelease时触发。则改变选中状态。
                if (tableUI.pimTable.allowMouseMultSeleOrDragEveFlag == tableUI.pimTable.ALLOWMULSELE) {
                    tableUI.pimTable.changeSelection(tmpRow, tmpCol, e.isControlDown(), e.isShiftDown(), false);
                }
            }
        }

        /**
         * 我先用来处理任务,鼠标一点击完成,所有有文字的部分设置成为删除线画法
         */
        public void processCurrentViewStyle() {
            // 先得到所有显示字段的索引
            int[] displayIndexes = ((View_PIMDetails) tableUI.pimTable.getView()).getDisplayIndexes();
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
                return;
            }

            // 当前鼠标点击所在列的索引
            int editorColumnIndex = tableUI.pimTable.getEditingColumn();
            int eidtorRowIndex = tableUI.pimTable.getEditingRow();
            // 不是也不管
            if (editorColumnIndex != tmpFinishField) {
                return;
            }

            // 下面处理所有的当前编辑行中的可显示文本
            // 根据单选框中的值来确定要加下划线
            PIMCheckBoxEditor prmCheckBoxEditor = (PIMCheckBoxEditor) tableUI.pimTable.getEditorComponent();
            tableUI.pimTable.setToggleRow(eidtorRowIndex);
            tableUI.pimTable.setIsDrawDeleteLine(prmCheckBoxEditor.isSelected());
            // 可能保存值
        }
    }

    // ********************************************************************************************************************************************
    /**
     * 该类是用于表格拖动示意的确认 Drag gesture recognizer for PIMTable components
     */
    static class TableDragGestureRecognizer extends BasicPIMDragGestureRecognizer {
        /**
         * 重载父类中的方法 判断是否可拖动(根据以下几个条件) Determines if the following are true:
         * <ul>
         * <li>the press event is located over a selection
         * <li>the dragEnabled property is true
         * <li>A TranferHandler is installed
         * </ul>
         * <p>
         * This is implemented to perform the superclass behavior followed by a check if the dragEnabled property is set
         * and if the location picked is selected.
         * 
         * @param e
         *            鼠标事件源
         * @return 返回可拖动标志
         */
        protected boolean isDragPossible(
                MouseEvent e) {
            // 如它父类确认有效才进行
            if (super.isDragPossible(e)) {
                // 从这个事件中得到表格实例
                PIMTable table = (PIMTable) this.getComponent(e);
                // 如表格是可拖动的才进行
                if (table.getDragEnabled()) {
                    Point p = e.getPoint();
                    int row = table.rowAtPoint(p);
                    int column = table.columnAtPoint(p);
                    // 把鼠标点击的点保证一下返回有效的标志
                    if ((column != -1) && (row != -1) && table.isCellSelected(row, column)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    // ***************************************************************************************************************************************
    /**
     * 表格拖动后放下目标监听器 A DropTargetListener to extend the default Swing handling of drop operations by moving the tree
     * selection to the nearest location to the mouse pointer. Also adds autoscroll capability.
     */
    static class TableDropTargetListener extends BasicPIMDropTargetListener {
        /**
         * 保存组件状态 called to save the state of a component in case it needs to be restored because a drop is not
         * performed.
         * 
         * @param comp
         *            待保存状态的组件
         */
        protected void saveComponentState(
                JComponent comp) {
            // 保存表格中的选择的行数和列数
            PIMTable table = (PIMTable) comp;
            rows = table.getSelectedRows();
            cols = table.getSelectedColumns();
        }

        /**
         * 恢复该组件状态 called to restore the state of a component because a drop was not performed.
         * 
         * @param comp
         *            待恢复状态的组件
         */
        protected void restoreComponentState(
                JComponent comp) {
            PIMTable table = (PIMTable) comp;
            // 清空表格中的所选中的
            table.clearSelection();
            // 在表格中加上我们这个类(TableDropTargetListener)所保存的选择的行
            for (int i = 0; i < rows.length; i++) {
                table.addRowSelectionInterval(rows[i], rows[i]);
            }
            // 在表格中加上我们这个类(TableDropTargetListener)所保存的选择的列
            for (int i = 0; i < cols.length; i++) {
                table.addColumnSelectionInterval(cols[i], cols[i]);
            }
        }

        /**
         * 更新插入定位 called to set the insertion location to match the current mouse pointer coordinates.
         * 
         * @param comp
         *            一个组件
         * @param p
         *            点击的坐标
         */
        protected void updateInsertionLocation(
                JComponent comp,
                Point p) {
            PIMTable table = (PIMTable) comp;
            // 得到鼠标点击的单元格的行号和列号
            int row = table.rowAtPoint(p);
            int col = table.columnAtPoint(p);
            if (row != -1) {
                // 设置这行为选中
                table.setRowSelectionInterval(row, row);
            }
            if (col != -1) {
                // 设置这列为选中
                table.setColumnSelectionInterval(col, col);
            }
            // 其结果是选择了一格
        }

        /**
         * 保存选择的行数
         */
        private int[] rows;
        /**
         * 保存选择的列数
         */
        private int[] cols;
    }

    /**
     * 保存缺省数据转移句柄
     */
    private static final TransferHandler defaultTransferHandler = new TableTransferHandler();

    // ================================================================================================================================
    /**
     * 本类用于表格的数据转移
     */
    static class TableTransferHandler extends TransferHandler implements UIResource {
        /**
         * 创建一个数据转移对象 Create a Transferable to use as the source for a data transfer.
         *
         * @param c
         *            The component holding the data to be transfered. This argument is provided to enable sharing of
         *            TransferHandlers by multiple components.
         * @return The representation of the data to be transfered.
         */
        public Transferable createTransferable(
                JComponent c) {
            // 确保是表格(PIMTable)才进行
            if (c instanceof PIMTable) {
                PIMTable table = (PIMTable) c;
                int[] rows;
                int[] cols;
                // 如果行和列都不可选中只好白白了
                if (!table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed()) {
                    return null;
                }
                // 如果行不可选中
                if (!table.getRowSelectionAllowed()) {
                    int rowCount = table.getRowCount();
                    // 新建一个数据保存行数
                    rows = new int[rowCount];
                    // 建一个新索引以便后用
                    for (int counter = 0; counter < rowCount; counter++) {
                        rows[counter] = counter;
                    }
                } else {
                    // 可选中就设为表格中选中的行数
                    rows = table.getSelectedRows();
                }

                // 如果列不可选中
                if (!table.getColumnSelectionAllowed()) {
                    int colCount = table.getColumnCount();
                    // 新建一个数据保存列数
                    cols = new int[colCount];
                    // 建一个新索引以便后用
                    for (int counter = 0; counter < colCount; counter++) {
                        cols[counter] = counter;
                    }
                } else {
                    // 可选中就设为表格中选中的列数
                    cols = table.getSelectedColumns();
                }

                // 如果没行没列只好白白了
                if (rows == null || cols == null || rows.length == 0 || cols.length == 0) {
                    return null;
                }

                StringBuffer plainBuf = new StringBuffer();
                StringBuffer htmlBuf = new StringBuffer();
                // 好象要形成一个网页
                htmlBuf.append("<html>\n<body>\n<table>\n");

                // 最外层是以行来遍历
                for (int row = 0; row < rows.length; row++) {
                    htmlBuf.append("<tr>\n");
                    // 内层是以列来遍历
                    for (int col = 0; col < cols.length; col++) {
                        Object obj = table.getValueAt(rows[row], cols[col]);
                        // 单元格为空的处理
                        String val = ((obj == null) ? CASUtility.EMPTYSTR : obj.toString());
                        // 在两种风格中把本格内容都加上去,目前只传递字符串
                        plainBuf.append(val + "\t");
                        htmlBuf.append("  <td>".concat(val).concat("</td>\n"));
                    }
                    // we want a newline at the end of each line and not a tab
                    // 换行处理
                    plainBuf.deleteCharAt(plainBuf.length() - 1).append("\n");
                    htmlBuf.append("</tr>\n");
                }

                // 移除最后一个新行的处理 remove the last newline
                plainBuf.deleteCharAt(plainBuf.length() - 1);
                htmlBuf.append("</table>\n</body>\n</html>");
                // 以这两种风格为参数建一个 BasicPIMTransferable 的实例,将它返回
                return new BasicPIMTransferable(plainBuf.toString(), htmlBuf.toString());
            }

            return null;
        }

        /**
         * 得到组件的源动作
         * 
         * @param c
         *            传入的一个JComponent 组件
         * @return 返回动作的ID
         */
        public int getSourceActions(
                JComponent c) {
            // 目前只可复制
            return COPY;
        }

    }

    // ================================================================================================================================
    /**
     * 属性监听器句柄,用以更新一些变量,状态等 PropertyChangeListener for the table. Updates the appropriate varaible, or TreeState, based
     * on what changes.
     */
    static class PropertyChangeHandler implements PropertyChangeListener {
        /**
         * 对本类的引用,静态类用
         */
        BasicPIMTableUI tableUI;

        /**
         * 构建器中要传入一个引用
         * 
         * @param tableUI
         *            传入引用
         */
        PropertyChangeHandler(BasicPIMTableUI tableUI) {
            this.tableUI = tableUI;
        }

        /**
         * 实现接口中的方法
         * 
         * @param event
         *            属性变化事件源
         */
        public void propertyChange(
                PropertyChangeEvent event) {
            // 得到属性名
            String changeName = event.getPropertyName();
            // 如果组件是自左到右
            if (changeName.equals("componentOrientation")) {
                // 取输入映射表
                InputMap inputMap = tableUI.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                // 将表格的UI输入映射表置换为这个映射表
                SwingUtilities.replaceUIInputMap(tableUI.pimTable, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                        inputMap);
                // 把表格的动作映射表置空
                UIManager.getLookAndFeelDefaults().put("Table.actionMap", null);
                // 把表格原有的动作映射表置换为我们自己的动作映射表
                ActionMap actionMap = tableUI.getActionMap();
                SwingUtilities.replaceUIActionMap(tableUI.pimTable, actionMap);

                // 表格头也作同样处理
                PIMTableHeader header = tableUI.pimTable.getTableHeader();
                if (header != null) {
                    header.setComponentOrientation((ComponentOrientation) event.getNewValue());
                }
            }
            // 如果是数据转移属性
            else if ("transferHandler".equals(changeName)) {
                // 得到放下目标的实例
                DropTarget dropTarget = tableUI.pimTable.getDropTarget();
                // 是UI资源才进行
                if (dropTarget instanceof UIResource) {
                    // 没有拖动目标监听器就建一个它的实例
                    if (dropTargetListener == null) {
                        dropTargetListener = new TableDropTargetListener();
                    }
                    try {
                        // 在放下目标上加上拖动目标监听器
                        dropTarget.addDropTargetListener(dropTargetListener);
                    } catch (TooManyListenersException tmle) {
                    }
                }
            }
        }
    } // End of BasicTableUI.PropertyChangeHandler
      // ================================================================================================================================

    /**
     * 保存表格实例的引用
     */
    protected PIMTable pimTable;
    /**
     * 单元格绘制的面板
     */
    protected CellRendererPane rendererPane;
    /**
     * 保存键盘监听器的引用
     */
    protected KeyListener keyListener;
    /**
     * 保存焦点监听器的引用
     */
    protected FocusListener focusListener;
    /**
     * 保存鼠标监听器的引用
     */
    protected MouseInputHandler mouseInputHandler;
    /**
     * 保存属性变化监听器句柄
     */
    private PropertyChangeListener propertyChangeListener;

}
