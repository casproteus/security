package org.cas.client.platform.pimview.theme_book;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import org.cas.client.platform.cascustomize.CustOpts;

/**
 * 该类是PIM天视图中下边界的显示面板。
 */

class EPageBottom extends JComponent {
    /**
     * 构造器
     * 
     * @param isLeft
     *            定义面板的位置
     */
    public EPageBottom(boolean isLeft) {
        this.isLeft = isLeft;
    }

    /**
     * 绘制该组件的内容
     * 
     * @param g
     *            绘制图形的句柄
     * @see JComponent#paintComponent
     */
    public void paintComponent(
            Graphics g) {
        int width = getWidth();
        int height = getHeight();
        if (isLeft)
            drawLeftBottom(width, height, g); // 作为左边/上边的组件时调此方法
        else
            drawRightBottom(width, height, g); // 作为左边/上边的组件时调此方法
    }

    /**
     * 根据指定的尺寸绘制左边/上边的组件，作为左边/上边的组件时调此方法
     * 
     * @param width
     *            组件宽度
     * @param height
     *            组件的高度
     * @param g
     *            绘制图形的句柄
     */
    private void drawLeftBottom(
            int width,
            int height,
            Graphics g) {
        if (CustOpts.custOps.isViewTopAndDown())// 上下结构
        {
            g.setColor(normalColor);
            int mid = lines / 2;
            int x = 0;
            int y = 0;
            for (int i = 0; i < mid; ++i) {
                g.drawLine(x, y, x, height);
                ++x;
                ++y;
            }

            g.setColor(midColor);
            g.drawLine(x, y, x, height);
            ++x;
            ++y;

            g.setColor(normalColor);
            for (int i = mid + 1; i < lines; ++i) {
                g.drawLine(x, y, x, height);
                ++x;
                ++y;
            }
        } else // 左右结构
        {
            g.setColor(normalColor);
            int mid = lines / 2;
            int x = lines;
            int y = 0;
            for (int i = 0; i < mid; ++i) {
                g.drawLine(x, y, width, y);
                --x;
                ++y;
            }

            g.setColor(midColor);
            g.drawLine(x, y, width, y);
            --x;
            ++y;

            g.setColor(normalColor);
            for (int i = mid + 1; i < lines; ++i) {
                g.drawLine(x, y, width, y);
                --x;
                ++y;
            }
        }
    }

    /**
     * 根据指定的尺寸绘制右边/下边的组件，作为右边/下边的组件时调此方法
     * 
     * @param width
     *            组件宽度
     * @param height
     *            组件的高度
     * @param g
     *            绘制图形的句柄
     */
    private void drawRightBottom(
            int width,
            int height,
            Graphics g) {
        if (CustOpts.custOps.isViewTopAndDown())// 上下结构
        {
            g.setColor(normalColor);
            int mid = lines / 2;
            int x = 0;
            int y = 0;
            for (int i = 0; i < mid; ++i) {
                g.drawLine(x, y, x, height - (lines - x));
                ++x;
            }

            g.setColor(midColor);
            g.drawLine(x, y, x, height - (lines - x));
            ++x;

            g.setColor(normalColor);
            for (int i = mid + 1; i < lines; ++i) {
                g.drawLine(x, y, x, height - (lines - x));
                ++x;
            }
        } else {
            g.setColor(normalColor);
            int mid = lines / 2;
            int x = 0;
            int y = 0;
            for (int i = 0; i < mid; ++i) {
                g.drawLine(x, y, width - (lines - y), y);
                ++y;
            }

            g.setColor(midColor);
            g.drawLine(x, y, width - (lines - y), y);
            ++y;

            g.setColor(normalColor);
            for (int i = mid + 1; i < lines; ++i) {
                g.drawLine(x, y, width - (lines - y), y);
                ++y;
            }
        }
    }

    /**
     * 返回面板的高度
     * 
     * @return 面板的高度 Get the height of this component.
     */
    int getLines() {
        return lines;
    }

    private boolean isLeft;
    // 为了节约界面资源，让底部比一般边界短一倍。左右边界因为可能响应鼠标操作，所以不能太窄。
    private int lines = EPageEdge.edgeLines / 2;
    private Color normalColor = new Color(75, 75, 75); // 该部分的数据应该从Model中去取值
    private Color midColor = new Color(95, 95, 95);
}
