package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;
import java.util.Date;

import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.PIMTableRendererConstant;



/**
 * 这实际是用来设置表格中的日期型字段的绘制器
 */

public class DateRenderer extends DefaultPIMTableCellRenderer
{
    /** 创建一个<CODE>DateRenderer</CODE>的实例
     */
    public DateRenderer()
    {
        super();
    }
    /** 创建一个<CODE>DateRenderer</CODE>的实例,
     * @param prmShowHourAndMinute 是否显示小时分钟
     */
    public DateRenderer(boolean prmShowHourAndMinute)
    {
        super();
        showHourAndMinute = prmShowHourAndMinute;
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

        int tmpActiveAppType = ModelCons.TASK_APP;
        if (table.getView() != null)
        {
            //得到视图信息
            PIMViewInfo pimViewInfo = table.getView().getApplication().getActiveViewInfo();
            //得到应用主类型和应用子类型
            tmpActiveAppType = pimViewInfo.getAppIndex();
        }
        String dateStr = null;
        //TODO;将来可能出问题的地方
        if (value == null)
        {
            // 不处理
        }
        //是日期型才进行
        else if (value instanceof Date)
        {
            Date tmpDate = (Date)value;
            //这几种类型要小时和分钟
            if (showHourAndMinute)
            {
                dateStr = new StringBuffer().append(tmpDate.getYear()+1900).
                append('-').append(tmpDate.getMonth() + 1).append('-').append(tmpDate.getDate()).
                append(" (").append(PIMTableRendererConstant.WEEKDAYS[tmpDate.getDay()]).append(") ").
                append(tmpDate.getHours()).append(':').append(tmpDate.getMinutes() > 9 ?
                Integer.toString(tmpDate.getMinutes()) : "0".concat(Integer.toString(tmpDate.getMinutes()))).toString();
            }
            else if (tmpActiveAppType == ModelCons.OUTBOX_APP ||
            tmpActiveAppType == ModelCons.INBOX_APP ||
            tmpActiveAppType == ModelCons.DELETED_ITEM_APP ||
            tmpActiveAppType == ModelCons.DRAFT_APP ||
            tmpActiveAppType == ModelCons.SENDED_APP)
            {
                //TODO: CustomOptions中暂时没有得到时间格式的方式,以后在国际化时要作调整
                //CustomOptions.custOps.get
                dateStr = new StringBuffer().append(tmpDate.getYear()+1900).
                append('-').append(tmpDate.getMonth() + 1).append('-').append(tmpDate.getDate()).
                append(" (").append(PIMTableRendererConstant.WEEKDAYS[tmpDate.getDay()]).append(") ").
                append(tmpDate.getHours()).append(':').append(tmpDate.getMinutes() > 9 ?
                Integer.toString(tmpDate.getMinutes()) : "0".concat(Integer.toString(tmpDate.getMinutes()))).toString();
            }
            //联系人类型不要小时和分钟
            else if (tmpActiveAppType == ModelCons.CONTACT_APP ||
            tmpActiveAppType == ModelCons.TASK_APP)
            {
                //TODO: CustomOptions中暂时没有得到时间格式的方式,以后在国际化时要作调整
                dateStr = new StringBuffer().append(tmpDate.getYear()+1900).append('-').
                append(tmpDate.getMonth() + 1).append('-').append(tmpDate.getDate()).append(" (").
                append(PIMTableRendererConstant.WEEKDAYS[tmpDate.getDay()]).append(')').toString();
            }
        }
        else if (value instanceof String)
        {
            dateStr = (String)value;
        }
        else
        {
            dateStr = value.toString();
        }
        setText(dateStr);
        return this;
    }

    /** 是否要显示小时和分钟
     */    
    private boolean showHourAndMinute;
}
