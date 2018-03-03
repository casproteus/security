package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;
import java.util.StringTokenizer;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.PIMTableConstants;




public class MailReceiverRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 PhotoRenderer 的实例
     */
    public MailReceiverRenderer()
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
            setText(PIMTableConstants.NONE);
        }
        //用于从数据库读出的数据处理,因为有可能是整数型的
        else
        {
            // 我们储存的就是字符串嘛
            String str = (String)value;

            String [] items = stringToArray(str.replace(';',','));
            String [] renders = new String[items.length];

            String toShow = CASUtility.EMPTYSTR;
            for (int i = 0 ; i < items.length; i++)
            {
                renders[i] = getRenderString(items[i]);
                if (renders[i] != null && renders[i].length() != 0)
                {
                    //2009.10.9 处理OUTLOOK过来邮件的表示为的一个bug
                    if (renders[i].endsWith("\\)"))
                    {
                        renders[i] = renders[i].substring(0,renders[i].length() - 2).concat(CASUtility.RIGHT_BRACKET);
                    }
                    toShow = toShow + renders[i] + ",";
                }
            }

            if (toShow.length() > 0)
            {
                toShow = toShow.substring(0,toShow.length() - 1);
            }
            //2009.10.9 处理OUTLOOK过来邮件的表示为的一个bug
            toShow = toShow.replace('\\',' ');
            setText(toShow);
        }
        return this;
    }

    /** 解析一个逗号分隔符处理的字符串
     * getTextAreaData
     * @param prmString 字符串
     * @return 解析好的数组
     */
    public String[] stringToArray(String prmString)
    {
        if (prmString != null)
        {
            //构建字符串分隔器
            StringTokenizer token = new StringTokenizer(prmString,",");
            int size = token.countTokens();
            //构建相应容量的字符串数组
            String[] indexes = new String[size];
            size = 0;
            //循环加入,去掉空格的
            while (token.hasMoreTokens())
            {
                indexes[size] = token.nextToken().trim();
                size++;
            }
            return indexes;
        }
        else
        {
            return null;
        }
    }

    /** 解析每个应显示的字符串
     * @param tmpOrgin 要处理的字符串
     * @return 处理好的字符串
     */
    public String getRenderString(String tmpOrgin)
    {
        String tmpString = tmpOrgin;
        if (tmpString != null && tmpString.length() != 0)
        {
            if (tmpString.startsWith(CASUtility.DOUBlEQUOTATION))
            {
                // 它应有对应的一个'\"'
                String tmpSub = tmpString.substring(1,tmpString.length()-1);
                int location = tmpSub.indexOf('\"');
                String renderStr = null;
                if (location == -1)
                {
                    renderStr = tmpSub.trim();
                }
                else
                {
                    renderStr = tmpSub.substring(0,location).trim();
                }
                if (renderStr != null && renderStr.length() != 0)
                {
                    return renderStr;
                }
                // 否则要去取后一个
                int leaderIndex = tmpString.indexOf('<');
                int endIndex =  tmpString.indexOf('>');
                renderStr = tmpString.substring(leaderIndex + 1,endIndex).trim();
                return renderStr;
            }
            // 处理只有邮件地址的情况
            else if (tmpString.startsWith(CASUtility.LEFTSHARPBRACKET))
            {
                // 它应有对应的一个'>'
                int leaderIndex = tmpString.indexOf('<');
                int endIndex =  tmpString.indexOf('>');
                String renderStr = tmpString.substring(leaderIndex + 1,endIndex).trim();
                return renderStr;
            }
            //这种情况我只能认为是一个光光的邮件地址
            else
            {
                return tmpString;
            }
        }
        return null;
    }
}
