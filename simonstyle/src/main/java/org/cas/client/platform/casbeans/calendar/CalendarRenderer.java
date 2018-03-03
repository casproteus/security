package org.cas.client.platform.casbeans.calendar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import org.cas.client.resource.international.MonthConstant;

class CalendarRenderer extends JLabel implements ListCellRenderer {
    /**
     * 创建一个 CalendarRenderer 的实例
     */
    public CalendarRenderer() {
        setOpaque(true);
        setFocusTraversalKeysEnabled(true);
    }

    /**
     * 是否交由父类处理
     * 
     * @return 是否交由父类处理
     */
    public boolean isFocusTraversable() {
        return true;
    }

    /**
     * @return 是否交由父类处理
     * @param event
     *            键盘事件
     */
    public boolean canTransfered(
            KeyEvent event) {
        int code = event.getKeyCode();
        int modify = event.getModifiers();
        if (((modify != KeyEvent.ALT_MASK) && (code == KeyEvent.VK_ENTER)) || code == KeyEvent.VK_TAB) {
            return true;
        }
        return false;
    }

    /**
     * @param e
     *            键盘事件
     */
    public void processKeyEvent(
            KeyEvent e) {
        Container parent = getParent();
        if (canTransfered(e) && parent instanceof JComboBox) {
            ((JComboBox) getParent()).processKeyEvent(e);
        }
        super.processKeyEvent(e);
    }

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
        // Thread.dumpStack();
        // *为空就设置文本为空
        if (value == null) {
            setText(null);
        }
        // 是日期型就按其处理:
        // TODO: 这以后一定要实现国际化
        else if (value instanceof Date) {
            Date tmpDayData = (Date) value;
            StringBuffer sb =
                    new StringBuffer().append(tmpDayData.getYear() + 1900).append('-')
                            .append(tmpDayData.getMonth() + 1).append('-').append(tmpDayData.getDate()).append(' ');
            setText(sb.append('(').append(MonthConstant.WEEKDAYS[tmpDayData.getDay()]).append(')').toString());
        } else if (value instanceof String) {
            setText((String) value);
        }
        // */

        return this;
    }

    /**
     * 重载父类中的方法
     * 
     * @param g
     *            图形设备
     */
    public void paintComponent(
            Graphics g) {
        // setForeground ((ColorComboModel.getInstance ().getSelectedItem ()));
        // 处理一下背景色
        g.setColor(getParent().getBackground()); // getForeground ());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

}
