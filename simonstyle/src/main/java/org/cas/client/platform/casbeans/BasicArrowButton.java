package org.cas.client.platform.casbeans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * JButton object that draws a scaled Arrow in one of the cardinal directions.
 * <p>
 * <strong>Warning:</strong> Serialized objects of this class will not be compatible with future Swing releases. The
 * current serialization support is appropriate for short term storage or RMI between applications running the same
 * version of Swing. As of 1.4, support for long term storage of all JavaBeans<sup><font size="-2">TM</font></sup> has
 * been added to the <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
 */

public class BasicArrowButton extends JButton implements SwingConstants {
    protected int direction;

    private Color shadow;
    private Color darkShadow;
    private Color highlight;

    /**
     * 构造器
     * 
     * @param direction
     *            方向
     * @param background
     *            背景色
     * @param shadow
     *            影子
     * @param darkShadow
     *            深影
     * @param highlight
     *            高亮色
     */
    public BasicArrowButton(int direction, Color background, Color shadow, Color darkShadow, Color highlight) {
        super();
        setRequestFocusEnabled(false);
        setDirection(direction);
        setBackground(background);
        this.shadow = shadow;
        this.darkShadow = darkShadow;
        this.highlight = highlight;
    }

    /**
     * 构造器
     * 
     * @param direction
     *            方向
     */
    public BasicArrowButton(int direction) {
        this(direction, UIManager.getColor("control"), UIManager.getColor("controlShadow"), UIManager
                .getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"));
    }

    /**
     * 得到方向
     * 
     * @return 代表方向的整数
     */
    public int getDirection() {
        return direction;
    }

    /**
     * 设置方向
     * 
     * @param dir
     *            代表方向的整数
     */
    public void setDirection(
            int dir) {
        direction = dir;
    }

    // 绘制
    /**
     * 重载
     * 
     * @param g
     *            图形设备
     */
    @Override
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

        g.setColor(getBackground());
        g.fillRect(1, 1, w - 2, h - 2);

        // / Draw the proper Border
        if (isPressed) {
            g.setColor(shadow);
            g.drawRect(0, 0, w - 1, h - 1);
        } else {
            // Using the background color set above
            g.drawLine(0, 0, 0, h - 1);
            g.drawLine(1, 0, w - 2, 0);

            g.setColor(highlight); // inner 3D border
            g.drawLine(1, 1, 1, h - 3);
            g.drawLine(2, 1, w - 3, 1);

            g.setColor(shadow); // inner 3D border
            g.drawLine(1, h - 2, w - 2, h - 2);
            g.drawLine(w - 2, 1, w - 2, h - 3);

            g.setColor(darkShadow); // black drop shadow __|
            g.drawLine(0, h - 1, w - 1, h - 1);
            g.drawLine(w - 1, h - 1, w - 1, 0);
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

    /**
     * 得到首选尺寸
     * 
     * @return 首选尺寸
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(16, 16);
    }

    /**
     * 得到最小尺寸
     * 
     * @return 最小尺寸
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(5, 5);
    }

    /**
     * 得到最大尺寸
     * 
     * @return 最大尺寸
     */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    // 绘制
    /**
     * 画三角形
     * 
     * @param g
     *            图形设备
     * @param x
     *            X坐标
     * @param y
     *            Y坐标
     * @param size
     *            尺寸
     * @param direction
     *            方向
     * @param isEnabled
     *            使能状态
     */
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
        mid = (size / 2) - 1;

        g.translate(x, y);
        if (isEnabled) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.BLACK);
        }

        switch (direction) {
            case NORTH:
                for (i = 0; i < size; i++) {
                    g.drawLine(mid - i, i, mid + i, i);
                }
                if (!isEnabled) {
                    g.setColor(highlight);
                    g.drawLine(mid - i + 2, i, mid + i, i);
                }
                break;

            case SOUTH:
                if (!isEnabled) {
                    g.translate(1, 1);
                    g.setColor(highlight);
                    for (i = size - 1; i >= 0; i--) {
                        g.drawLine(mid - i, j, mid + i, j);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(shadow);
                }

                j = 0;
                for (i = size - 1; i >= 0; i--) {
                    g.drawLine(mid - i, j, mid + i, j);
                    j++;
                }
                break;

            case WEST:
                for (i = 0; i < size; i++) {
                    g.drawLine(i, mid - i, i, mid + i);
                }
                if (!isEnabled) {
                    g.setColor(highlight);
                    g.drawLine(i, mid - i + 2, i, mid + i);
                }
                break;

            case EAST:
                if (!isEnabled) {
                    g.translate(1, 1);
                    g.setColor(highlight);
                    for (i = size - 1; i >= 0; i--) {
                        g.drawLine(j, mid - i, j, mid + i);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(shadow);
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
