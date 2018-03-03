package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.Icon;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;



/**
 * 这是用来设置已删除项图标
 */

public class DeleteItemIconRenderer extends DefaultPIMTableCellRenderer
{
    /** 创建一个<CODE>ContactsIconRenderer</CODE>的实例
     */
   /** public DeleteItemIconRenderer ()
    {
        super();
        //目前规格只用到一个图标
        this.taskIcon = CustomOptions.custOps.getTaskIcon(false);
        this.mailIcon = CustomOptions.custOps.getInBoxIcon(false);
        this.contactIcon = CustomOptions.custOps.getContactsIcon(false);
        this.memberListIcon = CustomOptions.custOps.getContactGroupIcon();
        this.diaryIcon = CustomOptions.custOps.getDiaryIcon(false);
        this.calendarIcon = CustomOptions.custOps.getCalendarIcon(false);
        setIcon(this.taskIcon);
    }

    *//** 重载父类中的方法 ,返回绘制器组件给 PIMTable 用
     *
     * @param table         <code>PIMTable</code> 表格实例
     * @param value         要设置的值
     * @param isSelected    表示是否选中的状态
     * @param hasFocus      表示是否有焦点
     * @param row           所在行
     * @param column        所在列
     * @return 绘制器
     *//*
    public Component getTableCellRendererComponent(PIMTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
    {
        //这样省事,焦点和选中就不管了
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        //没有文字
        setText(null);
        //得到记录的ID
        Object[][] contentV = table.getView().getApplication().getViewContents(); //TODO: 这个方法太费时了，太耗资源 
        Object[] record = contentV[row];
        int appType = ((Integer)record[record.length -1]).intValue();
       
        //三个真邮件
        if (appType == ModelConstants.CAN_ENTER_DELETED_APP_LIST[2] ||
        appType == ModelConstants.CAN_ENTER_DELETED_APP_LIST[3] ||
        appType == ModelConstants.CAN_ENTER_DELETED_APP_LIST[4])
        {
            setIcon(mailIcon);
        }
        else if (appType == ModelConstants.CAN_ENTER_DELETED_APP_LIST[1])
        {
            //联系人
            if (value == null || value.toString().length() == 0)
            {
                setIcon(contactIcon);
            }
            //这是肯定的,见规格
            else if (value.toString().equals("0"))
            {
                setIcon(contactIcon);
            }
            else
            {
                setIcon(memberListIcon);
            }
        }
        else if (appType == ModelConstants.CAN_ENTER_DELETED_APP_LIST[5])
        {
            //日记
            setIcon(diaryIcon);
        }
        else if (appType == ModelConstants.CAN_ENTER_DELETED_APP_LIST[6])
        {
            //任务
            setIcon(taskIcon);
        }
        else if (appType == ModelConstants.CALENDAR_APP)
        {
            setIcon(calendarIcon);
        }
        return this;
    }

    *//** 任务图标
     *//*
    private Icon taskIcon;
    *//** 邮件图标
     *//*    
    private Icon mailIcon;
    *//** 联系人图标
     *//*    
    private Icon contactIcon;
    *//** 通讯组成员列表图标
     *//*    
    private Icon memberListIcon;
    *//** 日记图标
     *//*    
    private Icon diaryIcon;
    *//** 日历图标
     *//*
    private Icon calendarIcon;*/
    
    /** 创建一个<CODE>ContactsIconRenderer</CODE>的实例
     */
    public DeleteItemIconRenderer ()
    {
        super();
        //目前规格只用到一个图标
        setIcon(this.unreadIcon);
    }

    /** 重载父类中的方法 ,返回绘制器组件给 PIMTable 用
     *
     * @param prmTable         <code>PIMTable</code> 表格实例
     * @param prmValue         要设置的值
     * @param isSelected    表示是否选中的状态
     * @param prmHasFocus      表示是否有焦点
     * @param prmRow           所在行
     * @param prmColumn        所在列
     * @return 绘制器
     */
    public Component getTableCellRendererComponent(PIMTable prmTable, Object prmValue,
    boolean isSelected, boolean prmHasFocus, int prmRow, int prmColumn)
    {
        super.getTableCellRendererComponent(prmTable,prmValue,isSelected,prmHasFocus,prmRow,prmColumn);//这样省事,焦点和选中就不管了
        setText(null);//没有文字
        //初始可能为空,暂时先这样处理
        if (prmValue == null || prmValue.toString().length() < 2)  //如果没有被赋过值，或被赋予了10以下的值，则一律作为普通邮件赋予图标。
        {                                                             //之所以规定10以下表示普通邮件拿长度比较，是为了处理方便。Table的value都是字串类型。
            //取出已读未读标记。
//            Object tmpValue = ((Object[])((Object[][])prmTable.getView().getApplication().getViewContents())[prmRow])[ModelDBConstants.READED];
//            if (PropertyName.BOOLEAN_TRUE.equals(tmpValue))  //如果已读，则赋予已读图标    //@NOTE:因为已知Table中的内容都是String类型的。
//            {
//                setIcon(readedIcon);
//            }
//            else //如果未读，则赋予未读图标
//            {
                setIcon(unreadIcon);
//            }
        }
        //TODO: 以后有"回复"时再说
        else
        {
            if (prmValue.toString().equalsIgnoreCase("10"))
            {
                setIcon(revertIcon);
            }
            else
            {
                setIcon(revertIcon);
            }
        }
        return this;
    }

    /** 未读图标
     */
    private Icon unreadIcon = CustOpts.custOps.getUnreadMailIcon();
    /** 已读图标
     */
    private Icon readedIcon = CustOpts.custOps.getReadedStateFieldIcon();
    /**已回复了邮件图标
     */
    private Icon revertIcon = CustOpts.custOps.getTaskIcon(false);
}
