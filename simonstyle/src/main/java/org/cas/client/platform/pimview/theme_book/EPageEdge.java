package org.cas.client.platform.pimview.theme_book;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import org.cas.client.platform.cascustomize.CustOpts;

/**
 * 该类是PIM天视图中左右边界的显示面板。
 */

class EPageEdge extends JComponent {
    // 该数值为边界面板的宽度。
    /**
     * 边线数
     */
    public static int edgeLines = 10; // 4 * 2 + 1 + 3;

    /**
     * 构造器
     * 
     * @param left
     *            true: 左边的显示面板部分 false: 右边的显示面板部分
     */
    public EPageEdge(boolean left) {
        this.isLeft = left;
        setOpaque(false);
    }

    /**
     * 绘制组件的内容
     * 
     * @param g
     *            绘制图形的句柄
     */
    public void paintComponent(
            Graphics g) {
        int width = getWidth();
        int height = getHeight();
        if (isLeft) {
            drawLeftEdge(width, height, g); // 作为左边、上边的面板时
        } else {
            drawRightEdge(width, height, g); // 作为右边、下边的面板时
        }
    }

    /**
     * 根据宽度和高度绘制左边/上边的面板
     * 
     * @param width
     *            面板的宽度
     * @param height
     *            面板的高度
     * @param g
     *            绘制图形的句柄
     */
    private void drawLeftEdge(
            int width,
            int height,
            Graphics g) {
        if (CustOpts.custOps.isViewTopAndDown())// 上下结构
        {
            int x = 0;
            int yOffset = edgeLines; // "edgeLines / 2" 为底部的边界的高度.
            int tmpLineLength = width - (edgeLines / 2) - 1; // 定义左右边界线的长度
            for (int i = 0; i < 4; ++i) {
                g.setColor(darkColor);
                g.drawLine(x, yOffset, x + tmpLineLength, yOffset);
                --yOffset;
                ++x;
                g.setColor(baseColor);
                g.drawLine(x, yOffset, x + tmpLineLength, yOffset);
                --yOffset;
            }
            g.setColor(darkColor);
            g.drawLine(x, yOffset, x + tmpLineLength, yOffset);
            --yOffset;
            ++x;

            g.setColor(baseColor);
            g.drawLine(x, yOffset, x + tmpLineLength, yOffset);
            --yOffset;
        } else// 左右结构
        {
            int x = 0;
            int yOffset = edgeLines / 2; // "edgeLines / 2" 为底部的边界的高度.
            int tmpLineLength = height - yOffset - 1; // 定义左右边界线的长度
            for (int i = 0; i < 4; ++i) {
                g.setColor(darkColor);
                g.drawLine(x, yOffset, x, yOffset + tmpLineLength);
                --yOffset;
                ++x;
                g.setColor(baseColor);
                g.drawLine(x, yOffset, x, yOffset + tmpLineLength);
                ++x;
            }
            g.setColor(darkColor);
            g.drawLine(x, yOffset, x, yOffset + tmpLineLength);
            --yOffset;
            ++x;

            g.setColor(baseColor);
            g.drawLine(x, yOffset, x, yOffset + tmpLineLength);
            --yOffset;
        }
    }

    /**
     * 根据宽度和高度绘制右边、下边的面板
     * 
     * @param width
     *            面板的宽度
     * @param height
     *            面板的高度
     * @param g
     *            绘制图形的句柄
     */
    private void drawRightEdge(
            int width,
            int height,
            Graphics g) {
        if (CustOpts.custOps.isViewTopAndDown())// 上下结构
        {
            int tmpLineLength = width - (edgeLines / 2) - 1; // 定义左右边界线的长度;
            int x = 0;
            int yOffset = 0;
            g.setColor(baseColor);
            g.drawLine(x, yOffset, x + tmpLineLength, yOffset);
            ++yOffset;
            ++x;

            for (int i = 0; i < 4; ++i) {
                g.setColor(darkColor);
                g.drawLine(x, yOffset, x + tmpLineLength, yOffset);
                ++yOffset;
                g.setColor(baseColor);
                g.drawLine(x, yOffset, x + tmpLineLength, yOffset);
                ++yOffset;
                ++x;
            }

            g.setColor(darkColor);
            g.drawLine(x, yOffset, x + tmpLineLength, yOffset);
        } else// 左右结构
        {
            int tmpLineLength = height - (edgeLines / 2) - 1; // 定义左右边界线的长度;
            int x = 0;
            int yOffset = 0;
            g.setColor(baseColor);
            g.drawLine(x, yOffset, x, yOffset + tmpLineLength);
            ++yOffset;
            ++x;

            for (int i = 0; i < 4; ++i) {
                g.setColor(darkColor);
                g.drawLine(x, yOffset, x, yOffset + tmpLineLength);
                ++x;
                g.setColor(baseColor);
                g.drawLine(x, yOffset, x, yOffset + tmpLineLength);
                ++yOffset;
                ++x;
            }

            g.setColor(darkColor);
            g.drawLine(x, yOffset, x, yOffset + tmpLineLength);
        }
    }

    /**
     * 返回该面板的宽度，应该不需要该方法，因为edgeLine为全局常量 Get the height of this component.
     * 
     * @return 组件的宽度
     */
    int getLines() {
        return edgeLines;
    }

    private boolean isLeft;
    private Color darkColor = Color.gray;
    private Color baseColor = Color.white;
}
