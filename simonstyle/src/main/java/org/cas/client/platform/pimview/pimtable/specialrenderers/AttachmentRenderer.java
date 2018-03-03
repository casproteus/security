package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.Icon;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;



/**
 * 这实际是用来设置联系人表格头中用来表示是有无附件的图标,它的ID
 * 是73,字段名称是"附件"
 */

public class AttachmentRenderer extends DefaultPIMTableCellRenderer
{
    /** 创建一个<CODE>AttachmentRenderer</CODE>的实例
     */
    public AttachmentRenderer()
    {
        super();
        //有附件才有图标
        this.icon = CustOpts.custOps.getAttachFieldIcon(false);
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

        //这以后见规格,主要现在和邮件系统没有联动
        if (value == null || value.toString().length() == 0)
        {
            setIcon(null);
        }
        //这以后见规格
        else if (value.toString().equals("0"))
        {
            setIcon(icon);
        }
        //这以后见规格
        else if (value.toString().equals("1"))
        {
            setIcon(icon);
        }
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
