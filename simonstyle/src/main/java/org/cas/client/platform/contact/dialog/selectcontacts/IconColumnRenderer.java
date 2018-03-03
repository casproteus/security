package org.cas.client.platform.contact.dialog.selectcontacts;

import java.awt.Component;

import javax.swing.ImageIcon;

import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;



class IconColumnRenderer extends DefaultPIMTableCellRenderer
{
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
    public Component getTableCellRendererComponent (PIMTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
    {
        /*Item item = (Item)value;
        if (item == null)
        {
        return this;
        }

        setIcon (item.getIcon ());
        setText (item.getName ());
        return this;
         */

		if (value instanceof ImageIcon)
		{
			ImageIcon i = (ImageIcon)value;
			setIcon(i);
		}
        return this;
    }
}
