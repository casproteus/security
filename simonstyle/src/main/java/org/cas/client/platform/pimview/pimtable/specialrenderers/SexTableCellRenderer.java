package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.PIMTableConstants;

/**
 * 填写注释。
 */

public class SexTableCellRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 SexRenderer 的实例
     */
    public SexTableCellRenderer()
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
        setHorizontalAlignment(CENTER);
        setIcon(null);
        if (value == null)
        {
            setText(null);
        }
        //用于从数据库读出的数据处理,因为有可能是整数型的
        else if (value instanceof Integer)
        {
            int id = ((Integer)value).intValue();
            if (id == 0)
            {
                setText(PIMTableConstants.SEX_ITEMS[id]);
            }
            else if (id == 1 || id == 2)
            {
                setText(PIMTableConstants.SEX_ITEMS[id]);
            }
            else
            {
                setText(CASUtility.EMPTYSTR);
            }
        }
        //用于和编辑器的连动
        else
        {
            String sexStr = value.toString();
            int id = -1;
            try
            {
                id = Integer.parseInt(sexStr);
                if (id == -1)
                {
                    //未经处理
                    setText(CASUtility.EMPTYSTR);
                }
                else
                {
                    setText(PIMTableConstants.SEX_ITEMS[id]);
                }
            }
            catch (Exception e)
            {
                setText(value.toString().equals(PIMTableConstants.SEX_ITEMS[0]) ? CASUtility.EMPTYSTR : value.toString());
                return this;
            }

        }
        return this;
    }

}
