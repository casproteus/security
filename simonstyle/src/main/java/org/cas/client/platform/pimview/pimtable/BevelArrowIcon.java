package org.cas.client.platform.pimview.pimtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * 本类构造一个图标,根据一些参数确定是绘制向上或是向下的箭头
 */

class BevelArrowIcon implements Icon {
    /** 箭头方向向上的ID */
    public static final int UP = 0; // direction
    /** 箭头方向向上的ID */
    public static final int DOWN = 1;
    /** 箭头缺省尺寸 */
    private static final int DEFAULT_SIZE = 11;
    /** 保存边1的绘制颜色 */
    private Color edge1;
    /** 保存边2的绘制颜色 */
    private Color edge2;
    /** 保存填充色 */
    private Color fill;
    /** 保存实例的尺寸 */
    private int size;
    /** 保存实例的箭头方向 */
    private int direction;

    /**
     * 构建器,创建出其实例
     * 
     * @param direction
     *            箭头方向
     * @param isRaisedView
     *            是否有上升边框的视感
     * @param isPressedView
     *            是否有鼠标下压的视感
     */
    BevelArrowIcon(int direction, boolean isRaisedView, boolean isPressedView) {
        // 如果有上升边框的视感
        if (isRaisedView) {
            // 如果有鼠标下压的视感
            if (isPressedView) {
                // 初始化一些信息
                init(UIManager.getColor("controlLtHighlight"), UIManager.getColor("controlDkShadow"),
                        UIManager.getColor("controlShadow"), DEFAULT_SIZE, direction);
            } else {
                // 初始化一些信息
                init(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"),
                        UIManager.getColor("control"), DEFAULT_SIZE, direction);
            }
        } else {
            // 如果有鼠标下压的视感
            if (isPressedView) {
                // 初始化一些信息
                init(UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"),
                        UIManager.getColor("controlShadow"), DEFAULT_SIZE, direction);
            } else {
                // 初始化一些信息
                init(UIManager.getColor("controlShadow"), UIManager.getColor("controlHighlight"),
                        UIManager.getColor("control"), DEFAULT_SIZE, direction);
            }
        }
    }

    /**
     * 构建器,创建出其实例
     * 
     * @param edge1
     *            边框1
     * @param edge2
     *            边框2
     * @param fill
     *            填充色
     * @param size
     *            尺寸
     * @param direction
     *            箭头方向是否有鼠标下压的视感
     */
    BevelArrowIcon(Color edge1, Color edge2, Color fill, int size, int direction) {
        // 初始化一些信息
        init(edge1, edge2, fill, size, direction);
    }

    /**
     * 实现 Icon 接口中的方法 绘制图标
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
        // 根据ID来确定画向下或向上的箭头
        switch (direction) {
            case DOWN:
                drawDownArrow(g, x, y);
                break;
            case UP:
                drawUpArrow(g, x, y);
                break;
            default:
                break;
        }
    }

    /**
     * 实现 Icon 接口中的方法 得到图标宽度
     * 
     * @return 图标宽度
     */
    public int getIconWidth() {
        return size;
    }

    /**
     * 实现 Icon 接口中的方法 得到图标高度
     * 
     * @return 图标高度
     */
    public int getIconHeight() {
        return size;
    }

    /**
     * 初始化一些信息
     * 
     * @param edge1
     *            边框1
     * @param edge2
     *            边框2
     * @param fill
     *            填充色
     * @param size
     *            尺寸
     * @param direction
     *            箭头方向是否有鼠标下压的视感
     */
    private void init(
            Color edge1,
            Color edge2,
            Color fill,
            int size,
            int direction) {
        // 统统给本地的实例变量
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.fill = fill;
        this.size = size;
        this.direction = direction;
    }

    /**
     * 绘制向下箭头
     * 
     * @param g
     *            图形设备上下文
     * @param xo
     *            起始X坐标
     * @param yo
     *            起始Y坐标
     */
    private void drawDownArrow(
            Graphics g,
            int xo,
            int yo) {
        // 设置三角箭头左边颜色
        g.setColor(edge1);
        // 画水平边和左边
        g.drawLine(xo, yo, xo + size - 1, yo);
        g.drawLine(xo, yo + 1, xo + size - 3, yo + 1);
        // 设置三角箭头右边颜色
        g.setColor(edge2);
        // 画右边
        g.drawLine(xo + size - 2, yo + 1, xo + size - 1, yo + 1);
        int x = xo + 1;
        int y = yo + 2;
        int dx = size - 6;
        // 具体画法,本人觉得没有必要研究下去
        while (y + 1 < yo + size) {
            g.setColor(edge1);
            g.drawLine(x, y, x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (0 < dx) {
                g.setColor(fill);
                g.drawLine(x + 2, y, x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(edge2);
            g.drawLine(x + dx + 2, y, x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            x += 1;
            y += 2;
            dx -= 2;
        }
        g.setColor(edge1);
        g.drawLine(xo + (size / 2), yo + size - 1, xo + (size / 2), yo + size - 1);
    }

    /**
     * 绘制向上箭头
     * 
     * @param g
     *            图形设备上下文
     * @param xo
     *            起始X坐标
     * @param yo
     *            起始Y坐标
     */
    private void drawUpArrow(
            Graphics g,
            int xo,
            int yo) {
        // 设置三角箭头左边颜色
        g.setColor(edge1);
        int x = xo + (size / 2);
        // 画一个点
        g.drawLine(x, yo, x, yo);
        x--;
        int y = yo + 1;
        int dx = 0;
        // 具体画法,本人觉得没有必要研究下去
        while (y + 3 < yo + size) {
            g.setColor(edge1);
            g.drawLine(x, y, x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (0 < dx) {
                g.setColor(fill);
                g.drawLine(x + 2, y, x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(edge2);
            g.drawLine(x + dx + 2, y, x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            x -= 1;
            y += 2;
            dx += 2;
        }
        // 画右边
        g.setColor(edge1);
        g.drawLine(xo, yo + size - 3, xo + 1, yo + size - 3);
        // 画水平边和左边
        g.setColor(edge2);
        g.drawLine(xo + 2, yo + size - 2, xo + size - 1, yo + size - 2);
        g.drawLine(xo, yo + size - 1, xo + size, yo + size - 1);
    }
}
