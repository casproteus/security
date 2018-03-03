package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.PIMTableConstants;


/**
 * 是否邀请的绘制器
 */

public class IsInviteSbRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 PhotoRenderer 的实例
     */
    public IsInviteSbRenderer ()
    {
        super();
        if (noFocusBorder == null) 
        {
            noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        }
        setOpaque(true);
        setBorder(noFocusBorder);
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
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        setIcon(null);
        if (row < 0)
        {
            setText(null);
            return this;
        }
        if (value == null)
        {
            setText(PIMTableConstants.NO);
        }
        //用于从数据库读出的数据处理,因为有可能是整数型的
        else //if (value instanceof Integer)
        {
            // 我们储存的就是字符串嘛
            String str = value.toString();
            if (str.equals (PIMPool.BOOLEAN_TRUE) || str.equals ("1"))
            {
                setText(PIMTableConstants.YES);
            }
            else
            {
                setText(PIMTableConstants.NO);
            }
        }
        return this;
    }
}
