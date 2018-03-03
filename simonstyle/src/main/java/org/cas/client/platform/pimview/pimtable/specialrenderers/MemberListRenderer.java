package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.IntlModelConstants;



public class MemberListRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 IPAddressRenderer 的实例
     */
    public MemberListRenderer()
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
        if (value != null)
        {
            str = value.toString().trim();
        }
        if (row >= 0)
        {

            if (str == null || str.length() == 0)
            {
                for (int i = 0 ; i < table.getRowCount();i++) //显示"表示为"字段
                {
                    Object headerTitle = table.getModel().getColumnTitle(i);
                    if (headerTitle instanceof String && headerTitle.toString() == IntlModelConstants.CONTACTS_FIELD[ModelDBCons.CAPTION])
                    {
                        setText(table.getValueAt(row,i).toString());
                        break;
                    }
                }
            }
            //*
            else
            {
                //因为此时保存的是所有的ID的逗号字符串,所以要全渐解析出,从模型中一个个找出所有的表示为
                int [] indexes = CASUtility.stringToArray(str);

                if (indexes == null && indexes.length == 0)  return this; //除错

                ICASModel model = CASControl.ctrl.getModel();
                PIMRecord record;
                int tmpNodeID = CASUtility.getAppIndexByFolderID(CustOpts.custOps.APPNameVec.indexOf("Contact"));
                StringBuffer sb = new StringBuffer();
                //一个个地取记录
                for (int i = 0 ; i < indexes.length ; i++)
                {
                    record = model.selectRecord(ModelCons.CONTACT_APP, indexes[i], tmpNodeID);
                    Hashtable tmpHash = record.getFieldValues();
                  
                    Object displayAs = tmpHash.get(PIMPool.pool.getKey(ModelDBCons.CAPTION));  //找出表示为,加入
                    if (displayAs != null)
                    {
                        sb.append(displayAs.toString()).append(',');
                    }
                } // end for
                String text = sb.toString();
                setText(text.substring(0,text.length() - 1));
            }
            // */
        }
        
        return this;
    }
}
