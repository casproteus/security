package org.cas.client.platform.cascontrol.dialog.csvtool;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.cas.client.platform.pimview.pimtable.IPIMCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;

/**
 * 填写注释。
 */

class TableCheckBoxRenderer extends JComponent implements IPIMCellRenderer {
    /**
     * 就一个方法,返回用于编辑的组件 Returns the component used for drawing the cell. This method is used to configure the renderer
     * appropriately before drawing.
     *
     * @param table
     *            the <code>PIMTable</code> that is asking the renderer to draw; can be <code>null</code>
     * @param value
     *            the value of the cell to be rendered. It is up to the specific renderer to interpret and draw the
     *            value. For example, if <code>value</code> is the string PropertyName.BOOLEAN_TRUE, it could be
     *            rendered as a string or it could be rendered as a check box that is checked. <code>null</code> is a
     *            valid value
     * @param isSelected
     *            true if the cell is to be rendered with the selection highlighted; otherwise false
     * @param hasFocus
     *            if true, render cell appropriately. For example, put a special border on the cell, if the cell can be
     *            edited, render in the color used to indicate editing
     * @param row
     *            the row index of the cell being drawn. When drawing the header, the value of <code>row</code> is -1
     * @param column
     *            the column index of the cell being drawn
     * @return 经处理后的绘制器
     */
    public Component getTableCellRendererComponent(
            PIMTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        // 取得表格模型
        FieldsTableModel model = (FieldsTableModel) table.getModel();
        box.setText(model.getCheckBoxName(value));
        if (Boolean.TRUE.equals(model.getSelectItem(value))) {
            box.setSelected(true);
        } else {
            box.setSelected(false);
        }
        return box;
    }

    private JCheckBox box = new JCheckBox();
}
