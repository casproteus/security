package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.Icon;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;


/**
 * 这是用来设置草稿箱图标
 */
public class DraftIconRenderer extends DefaultPIMTableCellRenderer
{
    /** 创建一个<CODE>ContactsIconRenderer</CODE>的实例
     */
    public DraftIconRenderer ()
    {
        super();
        this.unreadIcon = CustOpts.custOps.getUnreadMailIcon();
        this.readedIcon = CustOpts.custOps.getReadedStateFieldIcon();
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
        if (prmValue == null)  //如果没有被赋过值，或被赋予了10以下的值，则一律作为普通邮件赋予图标。
        {
        	return this;
        }
            //取出已读未读标记。
//            Object tmpValue = ((Object[])((Object[][])prmTable.getView().getApplication().getViewContents())[prmRow])[ModelDBConstants.READED];
        if (((Byte)prmValue).intValue()== 1)  //如果已读，则赋予已读图标    //@NOTE:因为已知Table中的内容都是String类型的。
        {
            setIcon(readedIcon);
        }
        else if (((Byte)prmValue).intValue() == 2)
        {
        	setIcon(unreadIcon);
        }
        else if (((Byte)prmValue).intValue() == 3)
        {
        	setIcon(replyIcon);
        }
        else if (((Byte)prmValue).intValue() == 4)
        {
        	setIcon(forwardIcon);
        }
        else if (((Byte)prmValue).intValue() == 11)
        {
        	setIcon(taskIcon);
        }
        else if (((Byte)prmValue).intValue() == 12)
        {
        	setIcon(taskIcon);
        }
        else if (((Byte)prmValue).intValue() == 13)
        {
        	setIcon(taskIcon);
        }
        else //如果未读，则赋予未读图标
        {
            setIcon(unreadIcon);
        }
        return this;
    }

    /** 未读图标
     */
    private Icon unreadIcon;
    /** 已读图标
     */    
    private Icon readedIcon;
    /**任务图标
     */
    private Icon taskIcon = CustOpts.custOps.getTaskIcon(false);
    /**转发图标
     */
    private Icon forwardIcon = CustOpts.custOps.getForwordIcon();
    /**答复图标
     */
    private Icon replyIcon = CustOpts.custOps.getReplyIcon();
}
