package org.cas.client.platform.pimview.pimtable;

import java.awt.Component;

import javax.swing.CellEditor;

/**
 * 接口,以便于 PIMTable 组件的设置其单元格编辑器
 */

public interface IPIMCellEditor extends CellEditor {

    /**
     * 就一个方法,返回用于编辑的组件 Sets an initial <code>value</code> for the editor. This will cause the editor to
     * <code>stopEditing</code> and lose any partially edited value if the editor is editing when this method is called.
     * <p>
     *
     * Returns the component that should be added to the client's <code>Component</code> hierarchy. Once installed in
     * the client's hierarchy this component will then be able to draw and receive user input.
     *
     * @param table
     *            the <code>PIMTable</code> that is asking the editor to edit; can be <code>null</code>
     * @param value
     *            the value of the cell to be edited; it is up to the specific editor to interpret and draw the value.
     *            For example, if value is the string PropertyName.BOOLEAN_TRUE, it could be rendered as a string or it
     *            could be rendered as a check box that is checked. <code>null</code> is a valid value
     * @param isSelected
     *            true if the cell is to be rendered with highlighting
     * @param row
     *            the row of the cell being edited
     * @param column
     *            the column of the cell being edited
     * @return the component for editing
     */
    Component getTableCellEditorComponent(
            PIMTable table,
            Object value,
            boolean isSelected,
            int row,
            int column);
}
