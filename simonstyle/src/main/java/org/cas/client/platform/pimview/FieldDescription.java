package org.cas.client.platform.pimview;

import org.cas.client.platform.pimview.pimtable.IPIMCellEditor;
import org.cas.client.platform.pimview.pimtable.IPIMCellRenderer;

public class FieldDescription {
    public boolean isColumResizeable() {
        return isColumnResizeable;
    }

    public IPIMCellEditor getCellEditor() {
        return cellEditor;
    }

    public IPIMCellRenderer getCellRendor() {
        return cellRenderer;
    }

    public void setColumResizeable(
            boolean prmIsResizeable) {
        isColumnResizeable = prmIsResizeable;
    }

    public void setCellEditor(
            IPIMCellEditor prmEditor) {
        cellEditor = prmEditor;
    }

    public void setCellRendor(
            IPIMCellRenderer prmRenderer) {
        cellRenderer = prmRenderer;
    }

    private boolean isColumnResizeable;
    private IPIMCellEditor cellEditor;
    private IPIMCellRenderer cellRenderer;
}
