package org.cas.client.platform.casbeans.calendar;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.MonthConstant;

class CalendarTextEditor extends JTextField implements ComboBoxEditor {

    /**
     * 创建一个 CalendarTextEditor 的实例
     * 
     * @param type
     *            类型
     */
    public CalendarTextEditor(int type) {
        super(type);
    }

    /**
     * Return the component that should be added to the tree hierarchy for this editor
     */
    public Component getEditorComponent() {
        return this;
    }

    /**
     * Set the item that should be edited. Cancel any editing if necessary
     * 
     * @param prmObj
     *            传入的对象,是一个日期型
     */
    public void setItem(
            Object prmObj) {
        // setText(object != null ? object.toString() : PIMUtility.EMPTYSTR);
        if (prmObj == null) {
            setText(CASUtility.EMPTYSTR);
        } else {
            if (prmObj instanceof Date) {
                Date tmpDate = (Date) prmObj;
                // 在编辑器中设置一下
                StringBuffer sb =
                        new StringBuffer().append(tmpDate.getYear() + 1900).append('-').append(tmpDate.getMonth() + 1)
                                .append('-').append(tmpDate.getDate()).append(' ');
                String text = (sb.append('(').append(MonthConstant.WEEKDAYS[tmpDate.getDay()]).append(')').toString());
                CalendarCombo combo = (CalendarCombo) getParent();
                String tmp = combo.getTime();

                setText(tmp == null ? text : text.concat(tmp));
            }
            // TODO: 要有解析的可能
            else if (prmObj instanceof String) {
                setText(prmObj.toString());
            } else {
                setText(prmObj.toString());
            }
        }
    }

    /** Return the edited item **/
    public Object getItem() {
        return getText();
    }

    // /** Ask the editor to start editing and to select everything **/
    // public void selectAll()
    // {
    // super.selectAll();
    // }
    //
    // /** Add an ActionListener. An action event is generated when the edited item changes **/
    // public void addActionListener(ActionListener l){}
    //
    // /** Remove an ActionListener **/
    // public void removeActionListener(ActionListener l){}

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
     * @param event
     *            键盘事件
     * @return 是否交由父类处理
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
}
