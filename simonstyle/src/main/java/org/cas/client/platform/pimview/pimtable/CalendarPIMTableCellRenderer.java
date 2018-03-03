package org.cas.client.platform.pimview.pimtable;

import java.awt.Component;
import java.util.Calendar;

import org.cas.client.resource.international.MonthConstant;

class CalendarPIMTableCellRenderer extends DefaultPIMTableCellRenderer {
    /**
     * 创建一个<CODE>ImageIconPIMtableCellRender</CODE>的实例
     */
    CalendarPIMTableCellRenderer() {
        super();
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
        // value处理
        if (value instanceof int[]) {
            int[] tmpDate = (int[]) value;
            StringBuffer sb =
                    new StringBuffer().append(tmpDate[0]).append('-').append(tmpDate[1] + 1).append('-')
                            .append(tmpDate[2]).append(' ');
            Calendar calendar = Calendar.getInstance();
            calendar.set(tmpDate[0], tmpDate[1], tmpDate[2]);
            sb.append(MonthConstant.WEEKDAYS[calendar.getTime().getDay()]);
            setText(sb.toString());
        }
        return this;

    }
}
