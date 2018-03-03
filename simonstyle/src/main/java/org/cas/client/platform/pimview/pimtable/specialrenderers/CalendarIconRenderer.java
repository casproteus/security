package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.Icon;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;



/**
 * 这是用来设置日历表格的图标
 */

public class CalendarIconRenderer extends DefaultPIMTableCellRenderer
{
    /** 创建一个<CODE>ContactsIconRenderer</CODE>的实例
     */
    public CalendarIconRenderer ()
    {
        super();
        //目前规格只用到一个图标
        this.icon = CustOpts.custOps.getCalendarIcon (false);
        setIcon(this.icon);
    }

    /** 重载父类中的方法 ,返回绘制器组件给 PIMTable 用
     *
     * @param table         <code>PIMTable</code> 表格实例
     * @param value         要设置的值
     * @param isSelected    表示是否选中的状态
     * @param hasFocus      表示是否有焦点
     * @param row           所在行
     * @param column        所在列
     * @return 绘制器
     */
    public Component getTableCellRendererComponent(PIMTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
    {
        //这样省事,焦点和选中就不管了
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        //没有文字
        setText(null);
        //初始可能为空,暂时先这样处理
        if (value == null || value.toString().length() == 0)
        {
            setIcon(icon);
        }
        //这是肯定的,见规格
        else if (value.toString().equals("0"))
        {
            setIcon(icon);
        }
        //TODO: 以后有"1"时再说
        else
        {
            setIcon(icon);
        }
        return this;
    }

    /** 保存所要绘制的图片
     */
    private Icon icon;
}
