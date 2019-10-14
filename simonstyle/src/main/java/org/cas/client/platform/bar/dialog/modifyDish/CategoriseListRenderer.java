package org.cas.client.platform.bar.dialog.modifyDish;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

public class CategoriseListRenderer implements ListCellRenderer, Serializable {

    /**
     * Return a component that has been configured to display the specified value. That component's <code>paint</code>
     * method is then called to "render" the cell. If it is necessary to compute the dimensions of a list because the
     * list cells do not have a fixed size, this method is called to generate a component on which
     * <code>getPreferredSize</code> can be invoked.
     *
     * @param list
     *            The JList we're painting.
     * @param value
     *            The value returned by list.getModel().getElementAt(index).
     * @param index
     *            The cells index.
     * @param isSelected
     *            True if the specified cell was selected.
     * @param cellHasFocus
     *            True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     *
     * @see JList
     * @see ListSelectionModel
     * @see ListModel
     */
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        return (JPanel)value;
    }

}
