package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;



/**
 * 所在文件夹的处理
 */

public class InFolderRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 InFolderRenderer 的实例
     */
    public InFolderRenderer()
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
        if (row < 0 || value == null)
        {
            setText(null);
            return this;
        }
        else
        {
            String str = value.toString();
            int tmpBegPos = str.lastIndexOf(' ');
            if (tmpBegPos != -1 && str.endsWith("]")) //NODE: 判断INFOLDER字段是否已经做了处理,处理了则不用再处理
            {
                str = str.substring(tmpBegPos + 1);
                str = str.substring(0, str.length() - 1);
            }
            setText(str);
        }
        return this;
    }
}
