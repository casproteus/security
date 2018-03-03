package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.PIMTableConstants;



/**
 * 填写注释。
 */

public class TaskStatusCellRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 SexRenderer 的实例
     */
    public TaskStatusCellRenderer()
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
            setText(null);
        }
        //用于从数据库读出的数据处理,因为是整数型的
        else if (value instanceof Integer)
        {
            int i = ((Integer)value).intValue();
            if (i >= 0 && i < PIMTableConstants.TASK_STATUS_CONSTANTS.length - 1)
            {
                setText(PIMTableConstants.TASK_STATUS_CONSTANTS[i]);
            }
            else
            {
                setText(PIMTableConstants.TASK_STATUS_CONSTANTS[0]);
            }
        }
        //从组合框中读出的数据处理
        // 目前似乎从数据库那边传来的值有错
        else if (value instanceof String)
        {
            String str = (String)value;
            if (str.length() == 1)
            {
                try
                {
                    int index = Integer.parseInt(value.toString().trim());
                    setText(PIMTableConstants.TASK_STATUS_CONSTANTS[index]);
                }
                catch (Exception e)
                {
                    setText(null);
                }
            }
            else if (str.trim().equals("-1"))
            {
                setText(PIMTableConstants.TASK_STATUS_CONSTANTS[0]);
            }
            else
            {
                setText(value.toString());
            }
        }

        return this;
    }

}
