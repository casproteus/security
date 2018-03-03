package org.cas.client.platform.pimview.pimtable.tagcombo;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import org.cas.client.platform.pimview.pimtable.specialeditors.BasicArrowButton;



/**
 * 处理箭头
 */

public class TagComboBoxUI extends BasicComboBoxUI
{
    /** 本组合框的引用
     */    
    private JComboBox comboBox;
    /** 创建一个 TagComboBoxUI 的实例
     * @param combo 传入引用 */
    public TagComboBoxUI(JComboBox combo)
    {
        comboBox = combo;
    }
    /**
     * Creates an button which will be used as the control to show or hide
     * the popup portion of the combo box.
     *
     * @return a button which represents the popup control
     */
    protected JButton createArrowButton() 
    {
        //主要是这里
        return new BasicArrowButton(BasicArrowButton.SOUTH,
        UIManager.getColor("ComboBox.buttonBackground"),
        UIManager.getColor("ComboBox.buttonShadow"),
        UIManager.getColor("ComboBox.buttonDarkShadow"),
        UIManager.getColor("ComboBox.buttonHighlight"));
    }

    /** UI 的标准方法
     * @param c 要装载的组件
     */
    public void installUI(JComponent c) 
    {
        super.installUI(c);
        comboBox.setLayout(createLayoutManager());
    }

    /**
     * Creates the popup portion of the combo box.
     *
     * @return an instance of <code>ComboPopup</code>
     * @see ComboPopup
     */
    protected ComboPopup createPopup() 
    {
        BasicComboPopup popup = new TagComboPopup(comboBox);
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }

    /**
     * Returns the calculated size of the display area. The display area is the
     * portion of the combo box in which the selected item is displayed. This
     * method will use the prototype display value if it has been set.
     * <p>
     * For combo boxes with a non trivial number of items, it is recommended to
     * use a prototype display value to significantly speed up the display
     * size calculation.
     *
     * @return the size of the display area calculated from the combo box items
     * @see javax.swing.JComboBox#setPrototypeDisplayValue
     */
    protected Dimension getDisplaySize() 
    {
        Dimension dim = super.getDisplaySize();
        return dim;
    }

    /**
     * Creates a layout manager for managing the components which make up the
     * combo box.
     *
     * @return an instance of a layout manager
     */
    //protected LayoutManager createLayoutManager() {
    //    return new ComboBoxLayoutManager();
    //}

    /**
     * This layout manager handles the 'standard' layout of combo boxes.  It puts
     * the arrow button to the right and the editor to the left.  If there is no
     * editor it still keeps the arrow button to the right.
     *
     * This public inner class should be treated as protected.
     * Instantiate it only within subclasses of
     * <code>BasicComboBoxUI</code>.
     * 以后可能需要处理一下布局
     */
    //    public class ComboBoxLayoutManager implements LayoutManager {
    //        public void addLayoutComponent(String name, Component comp) {}
    //
    //        public void removeLayoutComponent(Component comp) {}
    //
    //        public Dimension preferredLayoutSize(Container parent) {
    //            JComboBox cb = (JComboBox)parent;
    //            return parent.getPreferredSize();
    //        }
    //
    //        public Dimension minimumLayoutSize(Container parent) {
    //            JComboBox cb = (JComboBox)parent;
    //            return parent.getMinimumSize();
    //        }
    //
    //        public void layoutContainer(Container parent) {
    //            JComboBox cb = (JComboBox)parent;
    //            int width = cb.getWidth();
    //            int height = cb.getHeight();
    //
    //            Insets insets = getInsets();
    //            int buttonSize = height - (insets.top + insets.bottom);
    //            Rectangle cvb;
    //
    //            if ( arrowButton != null ) {
    //	        if(cb.getComponentOrientation().isLeftToRight()) {
    //                    int tmpOriginX = width - (insets.right + buttonSize);
    //                    if(tmpOriginX % 2 == 0)
    //                    {
    //                        tmpOriginX -= 1;
    //                        buttonSize += 1;
    //                    }
    //		    arrowButton.setBounds( width - (insets.right + buttonSize),
    //					   insets.top,
    //					   buttonSize, buttonSize);
    //		}
    //		else {
    //		    arrowButton.setBounds( insets.left, insets.top,
    //					   buttonSize, buttonSize);
    //		}
    //            }
    //            if ( editor != null ) {
    //                cvb = rectangleForCurrentValue();
    //                editor.setBounds(cvb);
    //            }
    //        }
    //    }
}
