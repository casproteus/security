package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;


/**
 * 邮件大小的绘制器
 */

public class MailSizeRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 MailSizeRenderer 的实例
     */
    public MailSizeRenderer()
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
        setText(null);

        String str = null;
        if (value == null)
        {
            setText("0KB");
            return this;
        }
        str = value.toString().trim();
        if (row >= 0)
        {
            long mailSize = 0;
            try
            {
                mailSize = Long.parseLong(str);
            }
            catch (Exception e)
            {}
            //小于1KB
            if (mailSize <= 999)
            {
                setText(Integer.toString(1).concat(KB_SUFFIX));
                return this;
            }
            //小于1MB
            else if (mailSize <= 999999)
            {
                mailSize = Math.round(mailSize /1000);
                setText(Long.toString(mailSize).concat(KB_SUFFIX));
            }
            //大于1MB
            else
            {
                mailSize = Math.round(mailSize /1000000);
                setText(NumberFormat.getNumberInstance().format(mailSize).concat(MB_SUFFIX));
            }
        }
        else
        {
            setText(CASUtility.EMPTYSTR);
        }
        return this;
    }
    //    public static void main(String[] args)
    //    {
    //        String result = NumberFormat.getNumberInstance().format(145879658500L);
    //        //if(result.
    //    }
    /** KB显示
     */    
    private static final String KB_SUFFIX = "KB";
    /** M显示
     */    
    private static final String MB_SUFFIX = "MB";
}
