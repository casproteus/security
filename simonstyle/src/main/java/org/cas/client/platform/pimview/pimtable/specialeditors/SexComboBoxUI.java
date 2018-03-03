package org.cas.client.platform.pimview.pimtable.specialeditors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;


/**
 */

public class SexComboBoxUI extends BasicComboBoxUI
{

    /** 创建一个 SexComboBoxUI 的实例
     * @param combo 传入引用
     */
    public SexComboBoxUI(JComboBox combo)
    {
        super();
        comboBox = combo;
    }
    /** UI 的标准方法
     * @param c 要装入的组件
     */
    public void installUI(JComponent c) 
    {
        super.installUI(c);
        comboBox.setLayout(createLayoutManager());
    }

    /**
     * Creates an button which will be used as the control to show or hide
     * the popup portion of the combo box.
     *
     * @return a button which represents the popup control
     */
    protected JButton createArrowButton() 
    {
        return new BasicArrowButton(BasicArrowButton.SOUTH,
        UIManager.getColor("ComboBox.buttonBackground"),
        UIManager.getColor("ComboBox.buttonShadow"),
        UIManager.getColor("ComboBox.buttonDarkShadow"),
        UIManager.getColor("ComboBox.buttonHighlight"));
    }

    /** 主要是为了处理箭头
     * @return 弹出体
     */    
    protected ComboPopup createPopup() 
    {
        popup = new BasicComboPopup(comboBox);
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }

    /** 得到弹出体
     * @return 弹出体组件
     */    
    BasicComboPopup getPopup()
    {
        return popup;
    }
    /** 弹出体的引用
     */    
    private BasicComboPopup popup;
    /** 本组件
     */    
    private JComboBox comboBox;
}
