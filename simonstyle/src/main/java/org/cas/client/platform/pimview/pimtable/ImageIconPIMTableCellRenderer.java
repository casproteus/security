package org.cas.client.platform.pimview.pimtable;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.PIMTableConstants;

/**
 * 本类实际是标记状态这一字段的绘制器
 */

public class ImageIconPIMTableCellRenderer extends DefaultPIMTableCellRenderer {
    /**
     * 创建一个<CODE>ImageIconPIMtableCellRender</CODE>的实例
     */
    public ImageIconPIMTableCellRenderer() {
        super();
        this.markedStateIcon = CustOpts.custOps.getMarkStateFieldIcon(0);
        this.finishededStateIcon = CustOpts.custOps.getMarkStateFieldIcon(2);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    /**
     * 重载父类中的方法 ,返回绘制器组件给 PIMTable 用
     *
     * @param table
     *            <code>PIMTable</code> 表格实例
     * @param value
     *            要设置的值
     * @param isSelected
     *            表示是否选中的状态
     * @param hasFocus
     *            表示是否有焦点
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @return 绘制器
     */
    public Component getTableCellRendererComponent(
            PIMTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setText(null);

        if (value == null) {
            setIcon(null);
        }
        // 用于和编辑器的连动
        else if (value instanceof Item) {
            Item item = (Item) value;
            String str = item.getName();
            if (str.equals(PIMTableConstants.FLAGS_STATUS_CONSTANTS[0])) {
                setIcon(null); // used by Test
            } else if (str.equals(PIMTableConstants.FLAGS_STATUS_CONSTANTS[1])) {
                setIcon(markedStateIcon);
            } else if (str.equals(PIMTableConstants.FLAGS_STATUS_CONSTANTS[2])) {

                setIcon(finishededStateIcon);
            }
        }
        // 用于处理从数据库中的读出值,
        // 因为在数据库中是以0,1,2和上述值对应的
        else {
            String str = value.toString();
            if (str.equals("0")) {
                setIcon(null); // used by Test
            } else if (str.equals("1")) {
                setIcon(markedStateIcon);
            } else if (str.equals("2")) {
                setIcon(finishededStateIcon);
            } else {
                setIcon(null); // used by Test
            }
        }
        return this;
    }

    /**
     * 标记状态图片
     */
    private Icon markedStateIcon;
    /**
     * 已完成图标
     */
    private Icon finishededStateIcon;
    /**
     * 缺省尺寸
     */
    private final Dimension d = new Dimension(38, 20);
}
