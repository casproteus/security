package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.Icon;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;


/**
 * Add your notes here.
 * 邮件的重要性和任务的重要性是两回事
 */
public class MailImportanceRenderer extends DefaultPIMTableCellRenderer
{
    /** 创建一个<CODE>ContactsIconRenderer</CODE>的实例
     */
    public MailImportanceRenderer()
    {
        super();
        //目前规格只用到一个图标
        iconImportance = CustOpts.custOps.getImportantFieldIcon();
        iconBlank = CustOpts.custOps.getMarkStateFieldIcon(1);
        iconUnimporance = CustOpts.custOps.getUnImportantFieldIcon();
        setIcon(null);
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
        //        //初始可能为空,暂时先这样处理
        //        if(value == null || value.toString().equals(PIMUtility.EMPTYSTR))
        //        {
        //            setIcon(icon);
        //        }
        //        //这是肯定的,见规格
        //        else if(value.toString().equals("0"))
        //        {
        //            setIcon(icon);
        //        }
        //        //TODO: 以后有"1"时再说
        //        else
        //        {
        //            setIcon(icon);
        //        }
        if (value == null)
        {
            if (row  < 0)
            {
                setIcon(null);
            }
            else
            {
                setIcon(null);
            }
        }
        //用于和编辑器的连动
        //        else if(value instanceof Item)
        //        {
        //            Item item = (Item)value;
        //            String str = item.getName();
        //            if (str.equals(PIMTableConstants.IMPORTANCE_STATUS_CONSTANTS[0]))
        //            {
        //                setIcon(iconUnimporance);// used by Test
        //            }
        //            else if (str.equals(PIMTableConstants.IMPORTANCE_STATUS_CONSTANTS[1]))
        //            {
        //                setIcon(null);
        //            }
        //            else if (str.equals(PIMTableConstants.IMPORTANCE_STATUS_CONSTANTS[2]))
        //            {
        //
        //                setIcon(iconImportance);
        //            }
        //        }
        //用于处理从数据库中的读出值,
        //因为在数据库中是以0,1,2和上述值对应的
        else
        {
            String str = value.toString();
            if (str.equals("5"))
            {
                setIcon(iconUnimporance);
            }
            else if (str.equals("3"))
            {
                setIcon(null);
            }
            else if (str.equals("1"))
            {
                setIcon(iconImportance);
            }
            else
            {
                setIcon(null);
            }
        }
        return this;
    }

    /** 重要性图标
     */
    private Icon iconImportance ;
    /** 普通
     */    
    private Icon iconBlank ;
    /** 不重要图标
     */    
    private Icon iconUnimporance ;
}
