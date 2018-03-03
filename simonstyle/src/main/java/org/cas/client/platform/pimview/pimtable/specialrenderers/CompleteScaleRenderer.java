package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;



/**
 * 主要处理-1这种情况
 */

public class CompleteScaleRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 SexRenderer 的实例
     */
    public CompleteScaleRenderer()
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
        if (value == null)
        {
            setText(CASUtility.EMPTYSTR);
        }
        //用于从数据库读出的数据处理,因为是整数型的
        else if (row >= 0)
        {
            if (value.toString().trim().equals("-1"))
            {
                setText("0%");
            }
            else
            {
                setText(value.toString().concat("%"));
            }
        }
        else
        {
            if (value.toString().trim().length() == 0)
            {
                setText(value.toString());
            }
            else
            {
                setText(value.toString().concat("%"));
            }

        }
        return this;
    }

}
