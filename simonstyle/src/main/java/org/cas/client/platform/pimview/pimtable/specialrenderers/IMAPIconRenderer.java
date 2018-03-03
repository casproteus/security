package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;

import javax.swing.Icon;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;


/**
 * 这是用来设置IMAP文件夹图标
 */

public class IMAPIconRenderer extends DefaultPIMTableCellRenderer
{
    /** 创建一个<CODE>ContactsIconRenderer</CODE>的实例
     */
    public IMAPIconRenderer ()
    {
        super();
        //目前规格只用到一个图标
        this.imapfolderIcon = CustOpts.custOps.getImapItemIcon(false);
        setIcon(this.imapfolderIcon);
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
        
//        Object tmpValue = ((Object[])((Object[][])prmTable.getView().getApplication().getViewContents())[prmRow])[ModelDBConstants.FOLDER];
        setText("收件箱");  //存的本来就是String
        
        setIcon(imapfolderIcon);
        return this;
    }
    
    /**
     * 已读图标
     */
    private Icon imapfolderIcon;
}
