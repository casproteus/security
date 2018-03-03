package org.cas.client.platform.pimview.pimtable.tagcombo;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimview.pimtable.Item;

/**
 * 这是标记状态组合框的绘制器▼
 * 由于某种原因,包的分配有点问题
 * TODO:今后有空包要重新分配一下
 */

public class ItemCellRenderer extends JLabel implements ListCellRenderer
{

    /**
     * 缺省构建器
     */
    public ItemCellRenderer()
    {
        setOpaque(true);
    }

    /** 重载父类中的方法 ,返回绘制器组件给 PIMTable 用
     *
     * @return 绘制器组件
     * @see JList#
     * @see ListSelectionModel#
     * @see ListModel#
     * @param list List 对象
     * @param index 所在索引
     * @param cellHasFocus 是否有焦点
     * @param value 要设置的值
     * @param isSelected 表示是否选中的状态
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {

        //调用模型中提供的方法来设置文字和图标
        setText(getName(value));
        setIcon(getIcon(value));

        //设置选中状态下的前景色和背景色
        if (isSelected)
        {
            setForeground(list.getSelectionForeground());
            setBackground(list.getSelectionBackground());
        }
        //设置未选中状态下的前景色和背景色
        else
        {
            setForeground(list.getForeground());
            setBackground(list.getBackground());
        }

        //设置有焦点状态下的前景色和背景色,与选中状态下一样
        if (cellHasFocus)
        {
            setForeground(list.getSelectionForeground());
            setBackground(list.getSelectionBackground());
        }
        //设置无焦点状态下的前景色和背景色,与选中状态下一样
        else
        {
            setForeground(list.getForeground());
            setBackground(list.getBackground());
        }

        return this;
    }
    /**
     * 提供访问 TagItem 中的字符串的方法
     * @param object  一个对象
     * @return 一个图标
     * @called by TagCombo 的绘制器
     */
    public String getName(Object object)
    {
        //可造型判断
        if (object instanceof Item)
        {
            Item item = (Item)object;
            //为空返回空串,否则返回其中的字符串
            return item == null ? CASUtility.EMPTYSTR : item.getName();
        }
        //返回空
        else
        {
            return null;
        }

    }

    /**
     * 提供访问一个 TagCombo 的图标方法
     * @param object  一个对象
     * @return 一个图标
     * @called by TagCombo 的绘制器
     */
    public Icon getIcon(Object object)
    {
        //可造型判断
        if (object instanceof Item)
        {
            Item item = (Item)object;
            //为空返回空,否则返回其中的图标
            return item == null ? null : item.getIcon();
        }
        //返回空
        else
        {
            return null;
        }

    }
}
