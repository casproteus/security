package org.cas.client.platform.pimview.pimtable;

import javax.swing.ImageIcon;

/**
 * 本 <code>Item</code> 封装一个图标和一个字符串
 */

public class Item {
    /** 保存一个图标 */
    private ImageIcon icon;
    /** 保存一个字符串 */
    private String string;

    /**
     * 构建器,用以创建一个<code>Item<code>实例
     * 
     * @param array
     *            一个图标和一个字符串
     */
    public Item(Object[] array) {
        icon = (ImageIcon) array[0];
        string = (String) array[1];
    }

    /**
     * 返回图标
     * 
     * @return 图标
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * 返回字符串
     * 
     * @return 字符串
     */
    public String getName() {
        return string;
    }
}
