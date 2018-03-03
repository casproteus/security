package org.cas.client.platform.pimview.pimtable.specialrenderers;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.border.EmptyBorder;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;



/**
 * 填写注释。
 */

public class SessionRenderer extends DefaultPIMTableCellRenderer
{

    /**
     * 创建一个 SessionRenderer 的实例
     */
    public SessionRenderer()
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
        ICASModel model = CASControl.ctrl.getModel();
        PIMRecord record = null;
        Object idOb = table.getValueAt(row,0);
        int id = Integer.parseInt(idOb.toString());
        int tmpApptype = table.getView().getApplication().getActiveViewInfo().getAppIndex();
        record = model.selectRecord(tmpApptype,id,CASUtility.getAppIndexByFolderID(tmpApptype));
        Hashtable hash = record.getFieldValues();
        Object tmpOb = hash.get(PIMPool.pool.getKey(ModelDBCons.CAPTION));
        setText(tmpOb == null ? CASUtility.EMPTYSTR : tmpOb.toString());
        return this;
    }
}
