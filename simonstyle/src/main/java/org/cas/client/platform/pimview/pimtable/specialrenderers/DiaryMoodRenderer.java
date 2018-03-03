package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;



/**
 * 这实际是用来设置日记心情的绘制器
 */

public class DiaryMoodRenderer extends DefaultPIMTableCellRenderer
{
    /** 创建一个<CODE>ContactsIconRenderer</CODE>的实例
     */
    public DiaryMoodRenderer()
    {
        super();
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
            setIcon(CustOpts.custOps.getMoodIcon(0));
        }
        else
        {
            int imgID = Integer.parseInt(value.toString());
            setIcon(CustOpts.custOps.getMoodIcon(imgID));
        }
        return this;
    }

}
