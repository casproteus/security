package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimview.pimtable.IPIMCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;


/**
 */

public class CheckBoxCellRenderer extends JCheckBox implements IPIMCellRenderer, Serializable
{
    /** 定义一个没有焦点的边框 */
    protected static Border noFocusBorder = new EmptyBorder (1, 1, 1, 1);

    // We need a place to store the color the JLabel should be returned
    // to after its foreground and background colors have been set
    // to the selection background color.
    // These ivars will be made protected when their names are finalized.
    /** 保存被选中单元格的前景色*/
    private Color unselectedForeground;
    /** 保存被选中单元格的背景色*/
    private Color unselectedBackground;

    /**
     * Creates a default table cell renderer.
     */
    public CheckBoxCellRenderer()
    {
        super();
        //本组件是不透明的,(有自己的背景色)
        setOpaque (true);
        //设置边框
        setBorder (noFocusBorder);
        //setFocusTraversalKeysEnabled(false);
    }
    
    /**
     * called by PIMViewUtil.java
     * 用于IMAP文件夹 
     * @param text
     * @param picture
     * @param selected
     */
    public CheckBoxCellRenderer(String text, boolean selected, char mnemonic)
    {
        super(text, selected);
        setMnemonic(mnemonic);
        this.setSelected(selected);
        //本组件是不透明的,(有自己的背景色)
        setOpaque (true);
        //设置边框
        setBorder (noFocusBorder);
    }

    /**
     * 设置未选中的情况下前景色
     * Overrides <code>JComponent.setForeground</code> to assign
     * the unselected-foreground color to the specified color.
     *
     * @param c set the foreground color to this value
     */
    public void setForeground (Color c)
    {
        //调用父类方法
        super.setForeground (c);
        //自己再保存一下
        unselectedForeground = c;
    }

    /**
     *设置设置未选中的情况下背景色
     * Overrides <code>JComponent.setBackground</code> to assign
     * the unselected-background color to the specified color.
     *
     * @param c set the background color to this value
     */
    public void setBackground (Color c)
    {
        //调用父类方法
        super.setBackground (c);
        //自己再保存一下
        unselectedBackground = c;
    }

    /**
     * 通知UI管理器:用户界面观感发生变化
     * Notification from the <code>UIManager</code> that the look and feel
     * [L&F] has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI ()
    {
        //调用父类方法
        super.updateUI ();
        //不设前景和背景
        setForeground (null);
        setBackground (null);
    }

    // implements javax.swing.pimtable.IPIMTableCellRenderer
    /** 实现 IPIMTableCellRenderer 接口中的方法 ,返回绘制器组件给 PIMTable 用
     * Returns the default table cell renderer.
     *
     * @param table  the <code>PIMTable</code>
     * @param value  the value to assign to the cell at
     * 			<code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent (PIMTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
    {
        //如本单元格被选中
        if (isSelected)
        {
            //以 table 中的选中前景色和选中背景色为本单元格的前景色和背景色
            super.setForeground (table.getSelectionForeground ());
            super.setBackground (table.getSelectionBackground ());
        }
        //本单元格未被选中
        else
        {
            //如前景色不为空,将本组件前景色传给父类,否则将 PIMTable 的前景色传给父类
            super.setForeground ((unselectedForeground != null) ? unselectedForeground
            : table.getForeground ());
            //如背景色不为空,将本组件背景色传给父类,否则将 PIMTable 的背景色传给父类
            super.setBackground ((unselectedBackground != null) ? unselectedBackground
            : table.getBackground ());
        }
        //特殊的第一行处理,show 一把就知道了
        if (row == -1 && table.getTableHeader().isEditing())
        {
            super.setForeground (table.getSelectionForeground ());
            super.setBackground (table.getSelectionBackground ());
        }

        //以 PIMTable 的字体为本组件的字体
        setFont (table.getFont ());

        //焦点处理,如果有焦点,从UI管理器中取缺省 Table 的边框
        if (hasFocus)
        {
            setBorder (UIManager.getBorder ("Table.focusCellHighlightBorder"));
            //如本单元格可被编辑,
            if (table.isCellEditable (row, column))
            {
                //从UI管理器中取缺省 Table 的焦点前景色和焦点背景色
                super.setForeground (UIManager.getColor ("Table.focusCellForeground"));
                super.setBackground (UIManager.getColor ("Table.focusCellBackground"));
            }
        } else
        {
            //否则设置自定义的无焦点边框
            setBorder (noFocusBorder);
        }
        //保存本单元格的绘制对象
        setValue (value);

        return this;
    }

    /*
     * The following methods are overridden as a performance measure to
     * to prune code-paths are often called in the case of renders
     * but which we know are unnecessary.  Great care should be taken
     * when writing your own renderer to weigh the benefits and
     * drawbacks of overriding methods like these.
     */

    /** 重载了父类的方法
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     * @return 是否透明
     */
    public boolean isOpaque ()
    {
        //保存现有背景色
        Color back = getBackground ();
        //得到父组件
        Component p = getParent ();
        //如有父类就再去找他爷爷
        if (p != null)
        {
            p = p.getParent ();
        }
        // 这样就判断 p 是 PIMTable? p should now be the PIMTable.
        boolean colorMatch = (back != null) && (p != null) &&
        back.equals (p.getBackground ()) && p.isOpaque ();

        return !colorMatch && super.isOpaque ();
    }

    /**
     * 重载方法
     * 写绘制器的例行代码
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void validate ()
    {}

    /**
     * 重载方法
     * 写绘制器的例行代码
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void revalidate ()
    {}

    /** 重载父类方法
     * 使绘制器自身不再处理这些信息
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @param tm 矩形区域
     */
    public void repaint (long tm, int x, int y, int width, int height)
    {}

    /** 重载父类方法
     * 使绘制器自身不再处理这些信息
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     * @param r 矩形区域
     */
    public void repaint (Rectangle r)
    { }

    /** 重载父类方法
     * 使绘制器自身只处理文本变化事件.
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     * @param propertyName 属性名
     * @param oldValue 旧值
     * @param newValue 新值
     */
    protected void firePropertyChange (String propertyName, Object oldValue, Object newValue)
    {
        // Strings get interned...
        if (propertyName=="text")
        {
            super.firePropertyChange (propertyName, oldValue, newValue);
        }
    }

    /** 重载父类方法
     * 使绘制器自身不再处理这些信息
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     * @param propertyName 属性名
     * @param oldValue 旧值
     * @param newValue 新值
     */
    public void firePropertyChange (String propertyName, boolean oldValue, boolean newValue)
    { }

    /**
     * 调用传入对象的toString方法 以得到要显示的内容.
     * Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     *
     * @param value  the string value for this cell; if value is
     *		<code>null</code> it sets the text value to an empty string
     * @see JLabel#setText
     *
     */
    protected void setValue (Object value)
    {
        if (value == null)
        {
            setSelected(false);
        }
        else
        {
            boolean selected = false;
            if (value instanceof Boolean)
            {
                selected = ((Boolean)value).booleanValue ();
            }
            //处理 String 型对象
            else if (value instanceof String)
            {
                //008 要求它能处理"0"和"1"
                selected = value.equals (PIMPool.BOOLEAN_TRUE) || value.equals ("1");
            }
            setSelected(selected);
        }

    }
    /**
     * DefaultPIMTableCellRenderer类的子类,实现接口中的方法,
     *DefaultPIMTableCellRenderer类不直接继承 UIResource, 以便使用者能够
     * 用DefaultPIMTableCellRenderer类的子类安全地重载单元格绘制器属性,
     * A subclass of <code>DefaultPIMTableCellRenderer</code> that
     * implements <code>UIResource</code>.
     * <code>DefaultPIMTableCellRenderer</code> doesn't implement
     * <code>UIResource</code>
     * directly so that applications can safely override the
     * <code>cellRenderer</code> property with
     * <code>DefaultPIMTableCellRenderer</code> subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    public static class UIResource extends CheckBoxCellRenderer
    implements javax.swing.plaf.UIResource
    {
    }

}
