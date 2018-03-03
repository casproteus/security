package org.cas.client.platform.casbeans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class ArrowButton_UD extends JButton implements SwingConstants {
    protected int direction;

    /***/
    public ArrowButton_UD(int direction) {
        super();
        setRequestFocusEnabled(false);
        setDirection(direction);
    }

    /***/
    public void setDirection(
            int dir) {
        direction = dir;
    }

    Color SCROLLBAR_BACKCOLOR = getBackground();
    Color SCROLLBAR_SHADOW = Color.GRAY;
    Color SCROLLBAR_TOP_LTCOLOR = Color.GRAY;
    Color SCROLLBAR_SHADOW_LTCOLOR = Color.GRAY;
    Color OBJECT_FONTCOLOR = Color.BLACK;
    Color OBJECT_DARKER_BACKCOLOR = Color.GRAY;
    Color OBJECT_BRIGHTER_BACKCOLOR = Color.LIGHT_GRAY;

    /***/
    public void paint(
            Graphics g) {
        Color origColor;
        boolean isPressed, isEnabled;
        int w, h, size;

        w = getSize().width;
        h = getSize().height;
        origColor = g.getColor();
        isPressed = getModel().isPressed();
        isEnabled = isEnabled();
        if (direction == WEST || direction == EAST) { // 水平方向的按钮
            g.setColor(SCROLLBAR_BACKCOLOR);// 按钮颜色
            g.fillRect(1, 2, w - 3, h - 3);

            g.setColor(Color.white); // 白色边
            g.drawLine(0, 0, w - 1, 0);// 画上边线;右边要去掉两个象素另做处理
            g.drawLine(0, 1, 0, h - 1); // 画左边线
            g.drawLine(1, h - 2, w - 3, h - 2);// 画下边线;右边要去掉两个象素另做处理
            g.setColor(SCROLLBAR_BACKCOLOR);// 拖动手柄颜色
            g.drawLine(0, h - 2, 0, h - 2); // 下边倒数第一个象素的左顶点
            g.drawLine(w - 2, h - 2, w - 2, h - 2);// 处理右边倒数第二个象素
            g.setColor(Color.white);
            g.drawLine(w - 1, h - 1, w - 1, h - 1);// 右下顶点
            g.drawLine(w - 2, 1, w - 2, h - 3);// 处理右边倒数第二个象素，用白色画h-2长的线
            g.setColor(SCROLLBAR_SHADOW_LTCOLOR); // 按钮阴影柔化颜色
            g.drawLine(w - 1, 0, w - 1, 0); // 处理最右边上面第一个象素
            g.drawLine(w - 1, h - 2, w - 1, h - 2);// 处理最右边从下往上第二个象素
            g.setColor(SCROLLBAR_SHADOW);// 按钮阴影颜色
            g.drawLine(2, 1, w - 4, 1);// 处理上面第二个象素
            g.drawLine(1, h - 1, w - 2, h - 1);// 下边
            g.drawLine(w - 1, 1, w - 1, h - 3);// 处理右边倒数一个象素，用按钮阴影颜色画h-3长的线

            g.setColor(SCROLLBAR_TOP_LTCOLOR);// 按钮顶点柔化颜色
            // 对中间部分做处理，也就是上去掉一个象素，左边去掉一个象素，下、右边去掉两个象素的长方形做处理
            g.drawLine(1, 1, 1, 1);// 左上角
            g.drawLine(w - 3, 1, w - 3, 1);// 右上角
            g.drawLine(1, h - 3, 1, h - 3); // 左下角
            g.drawLine(w - 3, h - 3, w - 3, h - 3);// 右下角

            g.drawLine(w - 2, h - 1, w - 2, h - 1);// 最下边倒数第一个象素
        } else { // 垂直方向的按钮
            g.setColor(SCROLLBAR_BACKCOLOR);// 按钮颜色
            g.fillRect(2, 1, w - 4, h - 3);

            g.setColor(Color.white); // 白色边
            g.drawLine(0, 0, w, 0);// 画上边线
            g.drawLine(0, 1, 0, h - 1); // 画左边线;下边要去掉两个象素另做处理
            g.drawLine(w - 2, 1, w - 2, h - 3);// 画右边线;下边要去掉两个象素另做处理
            g.drawLine(1, h - 2, w - 3, h - 2);// 画下边的白边
            g.setColor(SCROLLBAR_BACKCOLOR);// 按钮颜色
            g.drawLine(w - 2, 0, w - 2, 0);// 最上边从右往左第二个象素
            g.drawLine(w - 2, h - 2, w - 2, h - 2);// 倒数第二行的倒数第二个象素
            g.setColor(Color.white);
            g.drawLine(w - 1, h - 1, w - 1, h - 1);// 右下角
            g.setColor(SCROLLBAR_SHADOW_LTCOLOR); // 按钮阴影柔化颜色
            g.drawLine(0, h - 1, 0, h - 1); // 最下面第一个象素
            g.drawLine(w - 2, h - 1, w - 2, h - 1);// 最下面倒数第二个象素
            g.setColor(SCROLLBAR_SHADOW);// 拖动手柄阴影颜色
            g.drawLine(1, 2, 1, h - 4);// 最左边
            g.drawLine(w - 1, 1, w - 1, h - 3);// 最右边
            g.drawLine(1, h - 1, w - 3, h - 1);// 处理最下边

            g.setColor(SCROLLBAR_TOP_LTCOLOR);// 按钮顶点柔化颜色
            // 对中间部分做处理，也就是上、右各去掉一个象素，下、左边去掉两个象素的长方形做处理
            g.drawLine(1, 1, 1, 1);// 左上角
            g.drawLine(w - 3, 1, w - 3, 1);// 右上角
            g.drawLine(w - 3, h - 3, w - 3, h - 3); // 右下角
            g.drawLine(1, h - 3, 1, h - 3);// 左下角

            g.drawLine(w - 1, h - 2, w - 1, h - 2);// 最右边从下往上第二个象素
        }

        // If there's no room to draw arrow, bail
        if (h < 5 || w < 5) {
            g.setColor(origColor);
            return;
        }

        if (isPressed) {
            g.translate(1, 1);
        }

        // Draw the arrow
        size = Math.min((h - 4) / 3, (w - 4) / 3);
        size = Math.max(size, 2);
        paintTriangle(g, (w - size) / 2, (h - size) / 2, size, direction, isEnabled);

        // Reset the Graphics back to it's original settings
        if (isPressed) {
            g.translate(-1, -1);
        }
        g.setColor(origColor);

    }

    /***/
    public Dimension getPreferredSize() {
        if (direction == WEST || direction == EAST) // ScollBar为水平时
            return new Dimension(17, 15);
        else
            // ScrollBar为垂直时
            return new Dimension(15, 17);
    }

    /***/
    public Dimension getMinimumSize() {
        return new Dimension(5, 5);
    }

    /***/
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /** Returns whether this Component can be focused. */
    public boolean isFocusable() {
        return false;
    }

    /***/
    public void paintTriangle(
            Graphics g,
            int x,
            int y,
            int size,
            int direction,
            boolean isEnabled) {
        Color oldColor = g.getColor();
        int mid, i, j;

        j = 0;
        size = Math.max(size, 2);
        mid = size / 2;

        g.translate(x, y);

        Color up = null;
        Color down = null;
        if (isEnabled) {
            g.setColor(OBJECT_FONTCOLOR);
        } else {
            up = OBJECT_DARKER_BACKCOLOR;
            down = OBJECT_BRIGHTER_BACKCOLOR;
            if (up == null || down == null) {
                up = Color.gray;
                down = Color.white;
            }
        }

        switch (direction) {
            case NORTH:
                if (!isEnabled) {
                    g.setColor(up);
                }
                for (i = 0; i < size; i++) {
                    g.drawLine(mid - i, i, mid + i, i);
                }
                if (!isEnabled) {
                    g.setColor(down);
                    g.drawLine(mid - i + 2, i, mid + i, i);
                }
                break;

            case SOUTH:
                if (!isEnabled) {
                    g.translate(1, 1);
                    g.setColor(down);
                    for (i = size - 1; i >= 0; i--) {
                        g.drawLine(mid - i, j, mid + i, j);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(up);
                }

                j = 0;
                for (i = size - 1; i >= 0; i--) {
                    g.drawLine(mid - i, j, mid + i, j);
                    j++;
                }
                break;

            case WEST:
                if (!isEnabled) {
                    g.setColor(up);
                }
                for (i = 0; i < size; i++) {
                    g.drawLine(i, mid - i, i, mid + i);
                }
                if (!isEnabled) {
                    g.setColor(down);
                    g.drawLine(i, mid - i + 2, i, mid + i);
                }
                break;

            case EAST:
                if (!isEnabled) {
                    g.translate(1, 1);
                    g.setColor(down);
                    for (i = size - 1; i >= 0; i--) {
                        g.drawLine(j, mid - i, j, mid + i);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(up);
                }

                j = 0;
                for (i = size - 1; i >= 0; i--) {
                    g.drawLine(j, mid - i, j, mid + i);
                    j++;
                }
                break;

            default:
                break;
        }
        g.translate(-x, -y);
        g.setColor(oldColor);
    }
}
