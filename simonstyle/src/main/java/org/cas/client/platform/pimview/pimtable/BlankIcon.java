package org.cas.client.platform.pimview.pimtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * 本类作用是在组件上画出一单色的正方形色块
 */

class BlankIcon implements Icon {
    /** 保存填充色 */
    private Color fillColor;
    /** 保存尺寸 */
    private int size;

    /**
     * 缺省的构建器
     */
    BlankIcon() {
        this(null, 11);
    }

    /**
     * 构建器,创建出一个 BlankIcon 的实例
     * 
     * @param color
     *            填充色
     * @param size
     *            尺寸
     */
    BlankIcon(Color color, int size) {
        fillColor = color;
        this.size = size;
    }

    /**
     * 实现 Icon 接口中的方法 在组件上绘制图标
     * 
     * @param c
     *            要绘上本箭头的组件
     * @param g
     *            图形设备上下文
     * @param x
     *            绘制区域的X坐标
     * @param y
     *            绘制区域的Y坐标
     */
    public void paintIcon(
            Component c,
            Graphics g,
            int x,
            int y) {
        // 设置了填充色就开始绘制
        if (fillColor != null) {
            g.setColor(fillColor);
            // 填充这个矩形区域
            g.drawRect(x, y, size - 1, size - 1);
        }
    }

    /**
     * 现 Icon 接口中的方法,返回图标的宽度
     * 
     * @return 图标的宽度
     */
    public int getIconWidth() {
        return size;
    }

    /**
     * 现 Icon 接口中的方法,返回图标的高度
     * 
     * @return 返回图标的高度
     */
    public int getIconHeight() {
        return size;
    }
}
