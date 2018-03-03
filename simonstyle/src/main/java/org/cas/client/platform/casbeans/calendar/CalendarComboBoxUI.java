package org.cas.client.platform.casbeans.calendar;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import org.cas.client.platform.pimview.pimtable.specialeditors.BasicArrowButton;

class CalendarComboBoxUI extends BasicComboBoxUI {
    /**
     * 本组件
     */
    private JComboBox comboBox;
    /**
     * 调整后的弹出体
     */
    private CalendarBasicPopup calendarPopup;
    /**
     * 我们的箭头
     */
    private JButton arowBTN;// BasicArrowButton arowBTN;

    /**
     * 创建一个<CODE>CalendarComboBoxUI</CODE>的实例
     * 
     * @param combo
     *            传入引用
     */
    public CalendarComboBoxUI(JComboBox combo) {
        comboBox = combo;
    }

    /**
     * 重载,改变弹出体
     * 
     * @return 日期选择区面板
     */
    protected ComboPopup createPopup() {
        calendarPopup = new CalendarBasicPopup(comboBox);
        BasicComboPopup popup = calendarPopup;
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }

    /**
     * Creates an button which will be used as the control to show or hide the popup portion of the combo box.
     *
     * @return a button which represents the popup control
     */
    protected JButton createArrowButton() {
        arowBTN =
                new BasicArrowButton(BasicArrowButton.SOUTH, UIManager.getColor("ComboBox.buttonBackground"),
                        UIManager.getColor("ComboBox.buttonShadow"), Color.black,
                        UIManager.getColor("ComboBox.buttonHighlight"));
        return arowBTN;
    }

    /**
     * 得到箭头按钮
     * 
     * @return 箭头按钮
     */
    public JButton getArrowButton() {
        return arowBTN;
    }

    /**
     * UI标准方法
     * 
     * @param c
     *            要装入的组件
     */
    public void installUI(
            JComponent c) {
        super.installUI(c);
        comboBox.setLayout(createLayoutManager());
    }

    /**
     * Creates a layout manager for managing the components which make up the combo box.
     *
     * @return an instance of a layout manager
     */
    // protected LayoutManager createLayoutManager() {
    // return new ComboBoxLayoutManager();
    // }

    public CalendarBasicPopup getPopupComponent() {
        return calendarPopup;
    }
    /**
     * This layout manager handles the 'standard' layout of combo boxes. It puts the arrow button to the right and the
     * editor to the left. If there is no editor it still keeps the arrow button to the right.
     *
     * This public inner class should be treated as protected. Instantiate it only within subclasses of
     * <code>BasicComboBoxUI</code>.
     */
    // public class ComboBoxLayoutManager implements LayoutManager {
    // public void addLayoutComponent(String name, Component comp) {}
    //
    // public void removeLayoutComponent(Component comp) {}
    //
    // public Dimension preferredLayoutSize(Container parent) {
    // JComboBox cb = (JComboBox)parent;
    // return parent.getPreferredSize();
    // }
    //
    // public Dimension minimumLayoutSize(Container parent) {
    // JComboBox cb = (JComboBox)parent;
    // return parent.getMinimumSize();
    // }
    //
    // public void layoutContainer(Container parent) {
    // JComboBox cb = (JComboBox)parent;
    // int width = cb.getWidth();
    // int height = cb.getHeight();
    //
    // Insets insets = getInsets();
    // int buttonSize = height - (insets.top + insets.bottom);
    // Rectangle cvb;
    //
    // if ( arrowButton != null ) {
    // if(cb.getComponentOrientation().isLeftToRight()) {
    // int tmpOriginX = width - (insets.right + buttonSize);
    // if(tmpOriginX % 2 == 0)
    // {
    // tmpOriginX -= 1;
    // buttonSize += 1;
    // }
    // arrowButton.setBounds( width - (insets.right + buttonSize),
    // insets.top,
    // buttonSize, buttonSize);
    // }
    // else {
    // arrowButton.setBounds( insets.left, insets.top,
    // buttonSize, buttonSize);
    // }
    // }
    // if ( editor != null ) {
    // cvb = rectangleForCurrentValue();
    // editor.setBounds(cvb);
    // }
    // }
    // }
}
