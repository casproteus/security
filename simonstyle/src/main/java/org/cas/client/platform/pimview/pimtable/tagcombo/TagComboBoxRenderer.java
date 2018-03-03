package org.cas.client.platform.pimview.pimtable.tagcombo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


/**
 */

public class TagComboBoxRenderer extends JLabel implements ListCellRenderer
{

    /** 当前选中色,以后要改为和表格一致的颜色
     */    
    private Color current = Color.white;

    /**
     * 构建器
     */
    public TagComboBoxRenderer()
    {
        setOpaque (true);
        setBackground (current);
    }

    /** 实现接口中的方法
     * @param list JList 对象
     * @param value 选中值
     * @param index 所在索引
     * @param isSelected 是否选中
     * @param cellHasFocus 是否有焦点
     * @return 经格式化后的组件
     */
    public Component getListCellRendererComponent (
    JList list,
    Object value,
    int index,
    boolean isSelected,
    boolean cellHasFocus)
    {
        return this;
    }

    /** 重载父类的方法
     * @param g 图形设备 */
    public void paintComponent (Graphics g)
    {
        //setForeground ((ColorComboModel.getInstance ().getSelectedItem ()));
        g.setColor (getParent().getBackground()); //getForeground ());
        g.fillRect (0,0,getWidth (),getHeight ());
    }

}
