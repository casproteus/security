package org.cas.client.platform.pimview.pimtable;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.event.SwingPropertyChangeSupport;

import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;

/**
 */

public class PIMTableColumn extends Object implements Serializable {

    /**
     * 从Java 2 v1.3 开始废除,请用专用字符串来鉴别属性 Obsolete as of Java 2 platform v1.3. Please use string literals to identify
     * properties.
     */
    /*
     * 警告:本常量的值'columWidth'是错误的,因为这个属性的名字是"columnWidth". Warning: The value of this constant, "columWidth" is wrong as
     * the name of the property is "columnWidth".
     */
    public final static String COLUMN_WIDTH_PROPERTY = "columWidth";

    /**
     * 从Java 2 v1.3 开始废除,请用专用字符串来鉴别属性 Obsolete as of Java 2 platform v1.3. Please use string literals to identify
     * properties.
     */
    public final static String HEADER_VALUE_PROPERTY = "headerValue";

    /**
     * 从Java 2 v1.3 开始废除,请用专用字符串来鉴别属性 Obsolete as of Java 2 platform v1.3. Please use string literals to identify
     * properties.
     */
    public final static String HEADER_RENDERER_PROPERTY = "headerRenderer";

    /**
     * Obsolete as of Java 2 platform v1.3. Please use string literals to identify properties.
     */
    public final static String CELL_RENDERER_PROPERTY = "cellRenderer";

    //
    // Instance Variables
    //

    /**
     * 
     * The index of the column in the model which is to be displayed by this <code>PIMTableColumn</code>. As columns are
     * moved around in the view <code>modelIndex</code> remains constant.
     */
    protected int modelIndex;

    /**
     * This object is not used internally by the drawing machinery of the <code>PIMTable</code>; identifiers may be set
     * in the <code>PIMTableColumn</code> as as an optional way to tag and locate table columns. The table package does
     * not modify or invoke any methods in these identifier objects other than the <code>equals</code> method which is
     * used in the <code>getColumnIndex()</code> method in the <code>DefaultPIMTableColumnModel</code>.
     */
    protected Object identifier;

    /** 列的宽度 The width of the column. */
    protected int width;

    /** 列的最小宽度 The minimum width of the column. */
    protected int minWidth;

    /** 列的首选宽度 The preferred width of the column. */
    private int preferredWidth;

    /** 列的最大宽度 The maximum width of the column. */
    protected int maxWidth;

    /** 以下两个变量保存本列头部的单元绘制器和单元编辑器的引用 The renderer used to draw the header of the column. */
    protected IPIMCellRenderer headerRenderer;
    /**
     * 单元编辑器的引用
     */
    protected IPIMCellEditor headerEditor;

    /** 保存表格头的值 The header value of the column. */
    protected Object headerValue;

    /** 保存本列内容的单元绘制器的引用 The renderer used to draw the data cells of the column. */
    protected IPIMCellRenderer cellRenderer;

    /** 保存本列内容的单元编辑器的引用 The editor used to edit the data cells of the column. */
    protected IPIMCellEditor cellEditor;

    /** 本值为真时表示用户允许重新调整本列的宽度,缺省为真 If true, the user is allowed to resize the column; the default is true. */
    protected boolean isResizable;

    /**
     * 从Java 2 v1.3 开始废除,请用专用字符串来鉴别属性 This field was not used in previous releases and there are currently no plans to
     * support it in the future.
     *
     * @deprecated as of Java 2 platform v1.3
     */
    /*
     * Counter used to disable posting of resizing notifications until the end of the resize.
     */
    transient protected int resizedPostingDisableCount;

    /**
     * 用来保存注册的监听器,相当于一个Vector If any <code>PropertyChangeListeners</code> have been registered, the
     * <code>changeSupport</code> field describes them.
     */
    private SwingPropertyChangeSupport changeSupport;

    /**
     * Obsolete as of Java 2 platform v1.3. Please use string literals to identify properties.
     */
    public final static String EDITOR_ENABLE_PROPERTY = "editorEnable";

    /** 本变量用于单独一列编辑状态的使能,缺省为真, */
    protected boolean editorEnable = true;

    //
    // Constructors
    //

    /**
     * 缺省的构建器 Cover method, using a default model index of 0, default width of 75, a <code>null</code> renderer and a
     * <code>null</code> editor. This method is intended for serialization.
     * 
     * @see #PIMTableColumn(int, int, IPIMCellRenderer, IPIMCellEditor)
     */
    public PIMTableColumn() {
        // 在数据模型中为第一列
        this(0);
    }

    /**
     * 构建一个PIMTableColumn的实例,
     * 
     * @param modelIndex
     *            本列在数据模型中的索引 Cover method, using a default width of 75, a <code>null</code> renderer and a
     *            <code>null</code> editor.
     * @see #PIMTableColumn(int, int, IPIMCellRenderer, IPIMCellEditor)
     */
    public PIMTableColumn(int modelIndex) {
        this(modelIndex, 10075, null, null);
    }

    /**
     * 构建一个PIMTableColumn的实例,
     * 
     * @param modelIndex
     *            本列在数据模型中的索引
     * @param width
     *            本列的宽度 Cover method, using a <code>null</code> renderer and a <code>null</code> editor.
     * @see #PIMTableColumn(int, int, IPIMCellRenderer, IPIMCellEditor)
     */
    public PIMTableColumn(int modelIndex, int width) {
        this(modelIndex, width, null, null);
    }

    /**
     * 构建一个PIMTableColumn的实例,
     * 
     * @param modelIndex
     *            本列在数据模型中的索引
     * @param width
     *            本列的宽度
     * @param cellRenderer
     *            本列的单元绘制器
     * @param cellEditor
     *            本列的单元编辑器 Creates and initializes an instance of <code>PIMTableColumn</code> with
     *            <code>modelIndex</code>. All <code>PIMTableColumn</code> constructors delegate to this one. The
     *            <code>modelIndex</code> is the index of the column in the model which will supply the data for this
     *            column in the table. The <code>modelIndex</code> does not change as the columns are reordered in the
     *            view. The width parameter is used to set both the <code>preferredWidth</code> for this column and the
     *            initial width. The renderer and editor are the objects used respectively to render and edit values in
     *            this column. When these are <code>null</code>, default values, provided by the
     *            <code>getDefaultRenderer</code> and <code>getDefaultEditor</code> methods in the <code>PIMTable</code>
     *            class are used to provide defaults based on the type of the data in this column. This column-centric
     *            rendering strategy can be circumvented by overriding the <code>getCellRenderer</code> methods in the
     *            <code>PIMTable</code>.
     *            <p>
     *
     * @see PIMTable#getDefaultRenderer(Class)
     * @see PIMTable#getDefaultEditor(Class)
     * @see PIMTable#getCellRenderer(int, int)
     * @see PIMTable#getCellEditor(int, int)
     */
    public PIMTableColumn(int modelIndex, int width, IPIMCellRenderer cellRenderer, IPIMCellEditor cellEditor) {
        super();
        this.modelIndex = modelIndex;
        // 列宽度和首选宽度一样
        this.width = width;
        this.preferredWidth = width;

        this.cellRenderer = cellRenderer;
        this.cellEditor = cellEditor;

        // Set other instance variables to default values.
        // 设置一些其它缺省值
        minWidth = 15;
        maxWidth = Integer.MAX_VALUE;
        // 宽度可变
        isResizable = true;

        resizedPostingDisableCount = 0;
        // 本列表格头的值为空
        headerValue = null;
    }

    //
    // Modifying and Querying attributes
    // 修改和查询属性一些方法
    //

    /**
     * 激发属性变化事件
     * 
     * @param propertyName
     *            属性名称
     * @param oldValue
     *            原有属性
     * @param newValue
     *            属性新值
     */
    private void firePropertyChange(
            String propertyName,
            Object oldValue,
            Object newValue) {
        if (changeSupport != null) // 判断属性集是否为空
        {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * 激发属性变化事件
     * 
     * @param propertyName
     *            属性名称
     * @param oldValue
     *            原有属性
     * @param newValue
     *            属性新值
     */
    private void firePropertyChange(
            String propertyName,
            int oldValue,
            int newValue) {
        if (oldValue != newValue) // 判断新旧两值是否相同,以提高程序效率
        {
            firePropertyChange(propertyName, PIMPool.pool.getKey(oldValue), PIMPool.pool.getKey(newValue));
        }
    }

    /**
     * 激发属性变化事件
     * 
     * @param propertyName
     *            属性名称
     * @param oldValue
     *            原有属性
     * @param newValue
     *            属性新值
     */
    private void firePropertyChange(
            String propertyName,
            boolean oldValue,
            boolean newValue) {
        if (oldValue != newValue) // 判断新旧两值是否相同,以提高程序效率
        {
            firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        }
    }

    /**
     * 设置本列在模型中的位置索引,当它在视图中被移动后仍能保持这个常量. Sets the model index for this column. The model index is the index of the
     * column in the model that will be displayed by this <code>PIMTableColumn</code>. As the
     * <code>PIMTableColumn</code> is moved around in the view the model index remains constant.
     * 
     * @param modelIndex
     *            the new modelIndex
     * @beaninfo bound: true description: The model index.
     */
    public void setModelIndex(
            int modelIndex) {
        int old = this.modelIndex;
        this.modelIndex = modelIndex;
        // 激发"modelIndex"模型索引事件
        firePropertyChange("modelIndex", old, modelIndex);
    }

    /**
     * 返回本列在数据模型中的索引 Returns the model index for this column.
     * 
     * @return the <code>modelIndex</code> property
     */
    public int getModelIndex() {
        return modelIndex;
    }

    /**
     * 设置本列的标识符, 注意:这个标识符只是纯粹用于本列实例的标识和定位 Sets the <code>PIMTableColumn</code>'s identifier to <code>anIdentifier</code>
     * .
     * <p>
     * Note: identifiers are not used by the <code>PIMTable</code>, they are purely a convenience for the external
     * tagging and location of columns.
     *
     * @param identifier
     *            an identifier for this column
     * @see #getIdentifier
     * @beaninfo bound: true description: A unique identifier for this column.
     */
    public void setIdentifier(
            Object identifier) {
        Object old = this.identifier;
        this.identifier = identifier;
        // 激发标识符变化事件.
        firePropertyChange("identifier", old, identifier);
    }

    /**
     * 返回本列的标识符, 注意:这个标识符只是纯粹用于本列实例的标识和定位 Returns the <code>identifier</code> object for this column. Note identifiers
     * are not used by <code>PIMTable</code>, they are purely a convenience for external use. If the
     * <code>identifier</code> is <code>null</code>, <code>getIdentifier()</code> returns <code>getHeaderValue</code> as
     * a default.
     *
     * @return the <code>identifier</code> property
     * @see #setIdentifier
     */
    public Object getIdentifier() {
        return (identifier != null) ? identifier : getHeaderValue();

    }

    /**
     * 设置列头对象,用于在视图中的绘制,当本类实例创建后,缺省返回值为空. Sets the <code>Object</code> whose string representation will be used as the
     * value for the <code>headerRenderer</code>. When the <code>PIMTableColumn</code> is created, the default
     * <code>headerValue</code> is <code>null</code>.
     * 
     * @param headerValue
     *            the new headerValue
     * @see #getHeaderValue
     * @beaninfo bound: true description: The text to be used by the header renderer.
     */
    public void setHeaderValue(
            Object headerValue) {
        Object old = this.headerValue;
        this.headerValue = headerValue;
        // 激发"headerValue"列头对象变化事件
        firePropertyChange("headerValue", old, headerValue);
    }

    /**
     * 返回列头对象,用于在视图中的绘制,当本类实例创建后,缺省返回值为空. Returns the <code>Object</code> used as the value for the header renderer.
     *
     * @return the <code>headerValue</code> property
     * @see #setHeaderValue
     */
    public Object getHeaderValue() {
        return headerValue;
    }

    //
    // Renderers and Editors
    //

    /**
     * 设置列头部绘制器 Sets the <code>IPIMTableCellRenderer</code> used to draw the <code>PIMTableColumn</code>'s header to
     * <code>headerRenderer</code>.
     *
     * @param headerRenderer
     *            the new headerRenderer
     *
     * @see #getHeaderRenderer
     * @beaninfo bound: true description: The header renderer.
     */
    public void setHeaderRenderer(
            IPIMCellRenderer headerRenderer) {
        // 先保存旧值,再设新值
        IPIMCellRenderer old = this.headerRenderer;
        this.headerRenderer = headerRenderer;
        // 激发头部绘制器变化事件
        firePropertyChange("headerRenderer", old, headerRenderer);
    }

    /**
     * 返回表格头的绘制器,如返回值为空,PIMTableHeader会使用它的缺省绘制器, 本方法返回值缺省为空. Returns the <code>IPIMTableCellRenderer</code> used to
     * draw the header of the <code>PIMTableColumn</code>. When the <code>headerRenderer</code> is <code>null</code>,
     * the <code>PIMTableHeader</code> uses its <code>defaultRenderer</code>. The default value for a
     * <code>headerRenderer</code> is <code>null</code>.
     *
     * @return the <code>headerRenderer</code> property
     * @see #setHeaderRenderer
     * @see #setHeaderValue
     * @see javax.swing.pimtable.PIMTableHeader#getDefaultRenderer()
     */
    public IPIMCellRenderer getHeaderRenderer() {
        return headerRenderer;
    }

    /**
     * 设置单元格绘制器,用来绘制本列的各个值. Sets the <code>IPIMTableCellRenderer</code> used by <code>PIMTable</code> to draw individual
     * values for this column.
     *
     * @param cellRenderer
     *            the new cellRenderer
     * @see #getCellRenderer
     * @beaninfo bound: true description: The renderer to use for cell values.
     */
    public void setCellRenderer(
            IPIMCellRenderer prmRenderer) {
        IPIMCellRenderer old = cellRenderer;
        cellRenderer = prmRenderer;

        firePropertyChange("cellRenderer", old, cellRenderer);// 激发单元格绘制器变化事件
    }

    /**
     * 返回PIMTable类用来为本列绘制外形的绘制器 Returns the <code>IPIMTableCellRenderer</code> used by the <code>PIMTable</code> to draw
     * values for this column. The <code>cellRenderer</code> of the column not only controls the visual look for the
     * column, but is also used to interpret the value object supplied by the <code>PIMTableModel</code>. When the
     * <code>cellRenderer</code> is <code>null</code>, the <code>PIMTable</code> uses a default renderer based on the
     * class of the cells in that column. The default value for a <code>cellRenderer</code> is <code>null</code>.
     *
     * @return the <code>cellRenderer</code> property
     * @see #setCellRenderer
     * @see PIMTable#setDefaultRenderer
     */
    public IPIMCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    /**
     * 设置单元格编辑器 Sets the editor to used by when a cell in this column is edited.
     *
     * @param cellEditor
     *            the new cellEditor
     * @see #getCellEditor
     * @beaninfo bound: true description: The editor to use for cell values.
     */
    public void setCellEditor(
            IPIMCellEditor cellEditor) {
        IPIMCellEditor old = this.cellEditor;
        this.cellEditor = cellEditor;
        // 激发单元格编辑器变化事件
        firePropertyChange("cellEditor", old, cellEditor);
    }

    /**
     * Returns the <code>IPIMTableCellEditor</code> used by the <code>PIMTable</code> to edit values for this column.
     * When the <code>cellEditor</code> is <code>null</code>, the <code>PIMTable</code> uses a default editor based on
     * the class of the cells in that column. The default value for a <code>cellEditor</code> is <code>null</code>.
     *
     * @return the <code>cellEditor</code> property
     * @see #setCellEditor
     * @see PIMTable#setDefaultEditor
     */
    public IPIMCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * 设置当前宽度,用setPreferredWidth方法代替, 如超出最大或最小宽度的范围将作适当的调整. 如何计算这个宽度可参阅PIMTable类中的 sizeColumnsToFit 这个方法 This method
     * should not be used to set the widths of columns in the <code>PIMTable</code>, use <code>setPreferredWidth</code>
     * instead. Like a layout manager in the AWT, the <code>PIMTable</code> adjusts a column's width automatically
     * whenever the table itself changes size, or a column's preferred width is changed. Setting widths programmatically
     * therefore has no long term effect.
     * <p>
     * This method sets this column's width to <code>width</code>. If <code>width</code> exceeds the minimum or maximum
     * width, it is adjusted to the appropriate limiting value.
     * <p>
     * 
     * @param width
     *            the new width
     * @see #getWidth
     * @see #setMinWidth
     * @see #setMaxWidth
     * @see #setPreferredWidth
     * @see PIMTable#sizeColumnsToFit(int)
     * @beaninfo bound: true description: The width of the column.
     */
    public void setWidth(
            int width) {
        int old = this.width;
        // 先得到传入值和最小值中的最大值,再与最大值作比较,取两者的最小值.
        int temp = Math.min(Math.max(width, minWidth), maxWidth);
        // 这个默认宽度是75,
        // if(temp == 75 || (temp - old) % 221 == 0 || temp > 500 )
        // {
        // return ;
        // }
        this.width = temp;
        // 激发宽度变化事件
        firePropertyChange("width", old, this.width);
    }

    /**
     * 返回表格列的宽度,缺省为75. Returns the width of the <code>PIMTableColumn</code>. The default width is 75.
     *
     * @return the <code>width</code> property
     * @see #setWidth
     */
    public int getWidth() {
        return width;
    }

    /**
     * 设置首选宽度,如超出最大或最小宽度的范围将作适当的调整. 如何计算这个宽度可参阅PIMTable类中的 sizeColumnsToFit 这个方法 Sets this column's preferred width to
     * <code>preferredWidth</code>. If <code>preferredWidth</code> exceeds the minimum or maximum width, it is adjusted
     * to the appropriate limiting value.
     * <p>
     * For details on how the widths of columns in the <code>PIMTable</code> (and <code>PIMTableHeader</code>) are
     * calculated from the <code>preferredWidth</code>, see the <code>sizeColumnsToFit</code> method in
     * <code>PIMTable</code>.
     *
     * @param preferredWidth
     *            the new preferred width
     * @see #getPreferredWidth
     * @see PIMTable#sizeColumnsToFit(int)
     * @beaninfo bound: true description: The preferred width of the column.
     */
    public void setPreferredWidth(
            int preferredWidth) {
        int old = this.preferredWidth;
        // 先得到传入值和最小值中的最大值,再与最大值作比较,取两者的最小值.
        this.preferredWidth = Math.min(Math.max(preferredWidth, minWidth), maxWidth);
        // 激发首选宽度变化事件
        firePropertyChange("preferredWidth", old, this.preferredWidth);
    }

    /**
     * 返回首选宽度,缺省为75 Returns the preferred width of the <code>PIMTableColumn</code>. The default preferred width is 75.
     *
     * @return the <code>preferredWidth</code> property
     * @see #setPreferredWidth
     */
    public int getPreferredWidth() {
        return preferredWidth;
    }

    /**
     * 设置最小宽度,如当前宽度和首选宽度小于此值也要作调整. Sets the <code>PIMTableColumn</code>'s minimum width to <code>minWidth</code>; also
     * adjusts the current width and preferred width if they are less than this value.
     *
     * @param minWidth
     *            the new minimum width
     * @see #getMinWidth
     * @see #setPreferredWidth
     * @see #setMaxWidth
     * @beaninfo bound: true description: The minimum width of the column.
     */
    public void setMinWidth(
            int minWidth) {
        int old = this.minWidth;
        // 取得可用的最小宽度
        this.minWidth = Math.max(minWidth, 0);
        // 如传入值小于首选宽度则将它设为首选宽度
        if (width < minWidth) {
            setWidth(minWidth);
        }
        // 如传入值小于首选宽度则将它设为首选宽度
        if (preferredWidth < minWidth) {
            setPreferredWidth(minWidth);
        }
        // 激发最小宽度变化事件
        firePropertyChange("minWidth", old, this.minWidth);
    }

    /**
     * 返回最小宽度,该值不得小于用户的设定宽度或标题宽度,缺省为15. Returns the minimum width for the <code>PIMTableColumn</code>. The
     * <code>PIMTableColumn</code>'s width can't be made less than this either by the user or programmatically. The
     * default minWidth is 15.
     *
     * @return the <code>minWidth</code> property
     * @see #setMinWidth
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * Sets the <code>PIMTableColumn</code>'s maximum width to <code>maxWidth</code>; also adjusts the width and
     * preferred width if they are greater than this value.
     *
     * @param maxWidth
     *            the new maximum width
     * @see #getMaxWidth
     * @see #setPreferredWidth
     * @see #setMinWidth
     * @beaninfo bound: true description: The maximum width of the column.
     */
    public void setMaxWidth(
            int maxWidth) {
        int old = this.maxWidth;
        // 取得可用的最大宽度
        this.maxWidth = Math.max(minWidth, maxWidth);
        // 如传入值大于现宽度则将它设为当前宽度
        if (width > maxWidth) {
            setWidth(maxWidth);
        }
        // 如传入值大于首选宽度则将它设为首选宽度
        if (preferredWidth > maxWidth) {
            setPreferredWidth(maxWidth);
        }
        // 激发最大宽度变化事件
        firePropertyChange("maxWidth", old, this.maxWidth);
    }

    /**
     * Returns the maximum width for the <code>PIMTableColumn</code>. The <code>PIMTableColumn</code>'s width can't be
     * made larger than this either by the user or programmatically. The default maxWidth is Integer.MAX_VALUE.
     *
     * @return the <code>maxWidth</code> property
     * @see #setMaxWidth
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Sets whether this column can be resized.
     *
     * @param isResizable
     *            if true, resizing is allowed; otherwise false
     * @see #getResizable
     * @beaninfo bound: true description: Whether or not this column can be resized.
     */
    public void setResizable(
            boolean isResizable) {
        boolean old = this.isResizable;
        this.isResizable = isResizable;
        // 激发'尺寸可调整'事件
        firePropertyChange("isResizable", old, this.isResizable);
    }

    /**
     * 如果用户允许调整列宽度则返回为真,否则为假, Returns true if the user is allowed to resize the <code>PIMTableColumn</code>'s width,
     * false otherwise. You can change the width programmatically regardless of this setting. The default is true.
     *
     * @return the <code>isResizable</code> property
     * @see #setResizable
     */
    public boolean getResizable() {
        return isResizable;
    }

    /**
     * Resizes the <code>PIMTableColumn</code> to fit the width of its header cell. This method does nothing if the
     * header renderer is <code>null</code> (the default case). Otherwise, it sets the minimum, maximum and preferred
     * widths of this column to the widths of the minimum, maximum and preferred sizes of the Component delivered by the
     * header renderer. The transient "width" property of this PIMTableColumn is also set to the preferred width. Note
     * this method is not used internally by the table package.
     *
     * @see #setPreferredWidth
     */
    public void sizeWidthToFit() {
        if (headerRenderer == null) {
            return;
        }
        Component c = headerRenderer.getTableCellRendererComponent(null, getHeaderValue(), false, false, 0, 0);

        setMinWidth(c.getMinimumSize().width);
        setMaxWidth(c.getMaximumSize().width);
        setPreferredWidth(c.getPreferredSize().width);

        setWidth(getPreferredWidth());
    }

    /**
     * 本字段在以前版本中未使用,而且现在不计划在今后支持 This field was not used in previous releases and there are currently no plans to
     * support it in the future.
     *
     * @deprecated as of Java 2 platform v1.3
     */
    public void disableResizedPosting() {
        resizedPostingDisableCount++;
    }

    /**
     * 本字段在以前版本中未使用,而且现在不计划在今后支持 This field was not used in previous releases and there are currently no plans to
     * support it in the future.
     *
     * @deprecated as of Java 2 platform v1.3
     */
    public void enableResizedPosting() {
        resizedPostingDisableCount--;
    }

    //
    // Property Change Support
    //

    /**
     * 添加属性变化监听器 Adds a <code>PropertyChangeListener</code> to the listener list. The listener is registered for all
     * properties.
     * <p>
     * A <code>PropertyChangeEvent</code> will get fired in response to an explicit call to <code>setFont</code>,
     * <code>setBackground</code>, or <code>setForeground</code> on the current component. Note that if the current
     * component is inheriting its foreground, background, or font from its container, then no event will be fired in
     * response to a change in the inherited property.
     *
     * @param listener
     *            the listener to be added
     *
     */
    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
        // changeSupport 是一个相当于Vector 的容器
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a <code>PropertyChangeListener</code> from the listener list. The <code>PropertyChangeListener</code> to
     * be removed was registered for all properties.
     *
     * @param listener
     *            the listener to be removed
     *
     */

    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * 得到己加载的属性变化监听器的集合. Returns an array of all the <code>PropertyChangeListener</code>s added to this PIMTableColumn
     * with addPropertyChangeListener().
     *
     * @return all of the <code>PropertyChangeListener</code>s added or an empty array if no listeners have been added
     * @since 1.4
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        // 如为空则返回一个长度为零的集合.
        if (changeSupport == null) {
            // 长度为零的集合是新建的.
            return new PropertyChangeListener[0];
        }
        return changeSupport.getPropertyChangeListeners();
    }

    //
    // Protected Methods
    //

    /**
     * 创建缺省的表格头绘制器. As of Java 2 platform v1.3, this method is not called by the <code>PIMTableColumn</code>
     * constructor. Previously this method was used by the <code>PIMTableColumn</code> to create a default header
     * renderer. As of Java 2 platform v1.3, the default header renderer is <code>null</code>.
     * <code>PIMTableHeader</code> now provides its own shared default renderer, just as the <code>PIMTable</code> does
     * for its cell renderers.
     *
     * @return the default header renderer
     * @see javax.swing.pimtable.PIMTableHeader#createDefaultRenderer()
     */
    protected IPIMCellRenderer createDefaultHeaderRenderer() {

        DefaultPIMTableCellRenderer label = new HeadCellRenderer();
        // 设置水平对齐方式为中心对齐
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    /**
     * 设置本列单元格是否可被编辑 ,目前不完善,只能在初始化表格时使用, 可能要加激发器
     *
     * @param prmFlag
     *            可被编辑的标志
     */
    public void setEditorEnable(
            boolean prmFlag) {
        boolean old = this.editorEnable;
        this.editorEnable = prmFlag;
        // 激发单元格绘制器变化事件
        firePropertyChange("editorEnable", old, editorEnable);
    }

    /**
     * 取得本列单元格是否可被编辑
     * 
     * @return 本列单元格是否可被编辑
     */
    public boolean getEditorEnable() {
        return editorEnable;
    }

    /**
     * 新建一个 PIMTabler的单元绘制器,覆盖 getTableCellRendererComponent方法
     */
    public static class HeadCellRenderer extends DefaultPIMTableCellRenderer {
        /**
         * 重载父类中的方法 ,返回绘制器组件给 PIMTable 用
         *
         * @param table
         *            <code>PIMTable</code> 表格实例
         * @param value
         *            要设置的值
         * @param isSelected
         *            表示是否选中的状态
         * @param hasFocus
         *            表示是否有焦点
         * @param row
         *            所在行
         * @param column
         *            所在列
         * @return 绘制器
         */
        public Component getTableCellRendererComponent(
                PIMTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            if (table != null) {
                // 使用传入的参数table的表格头
                PIMTableHeader header = table.getTableHeader();
                if (header != null) {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                }
            }
            // DefaultPIMTableCellRenderer 本身是一个label,可设置文字.
            setText((value == null) ? CASUtility.EMPTYSTR : value.toString());
            // 将边框设为 TableHeader.cellBorder 式样
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            return this;
        }
    }
}
