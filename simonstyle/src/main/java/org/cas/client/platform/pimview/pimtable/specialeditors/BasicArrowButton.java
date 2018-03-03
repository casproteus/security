package org.cas.client.platform.pimview.pimtable.specialeditors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;


/**
 * 这实际是用来设置表格的组合框编辑器,
 * 因为JDK的同名类有缺陷,在组合框高度在20个像素左右时会向左含偏移一两个像素,
 * 本类的目的就是用来处理这个缺陷
 */

public class BasicArrowButton extends JButton implements SwingConstants
{
    //
    /** 方向
     */    
    protected int direction;
    //
    /** 影子色
     */    
    private Color shadow;
    //
    /** 深影色
     */    
    private Color darkShadow;
    //
    /** 高亮色
     */    
    private Color highlight;

    /** 构建器,传入方向和颜色
     * @param direction 方向
     * @param background 背景色
     * @param shadow 背影
     * @param darkShadow 深背影
     * @param highlight 高亮色
     */
    public BasicArrowButton(int direction, Color background, Color shadow,
    Color darkShadow, Color highlight)
    {
        super();
        //可见它一般不单独用
        setRequestFocusEnabled(false);
        setDirection(direction);
        setBackground(background);

        //保存
        this.shadow = shadow;
        this.darkShadow = darkShadow;
        this.highlight = highlight;
    }

    /** 构建器,传入方向
     * @param direction 方向
     */
    public BasicArrowButton(int direction)
    {
        this(direction, UIManager.getColor("control"), UIManager.getColor("controlShadow"),
        UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"));
    }

    /** 得到方向
     * @return 方向
     */
    public int getDirection()
    {
         return direction; 
    }

    /** 设置方向
     * @param dir 方向
     */
    public void setDirection(int dir)
    {
         direction = dir; 
    }

    /** 重载绘制方法
     * @param g 图形设备
     */
    public void paint(Graphics g)
    {
        Color origColor;
        boolean isPressed, isEnabled;
        int w, h, size;

        //得到宽度和高度
        w = getSize().width;
        h = getSize().height;

        origColor = g.getColor();
        isPressed = getModel().isPressed();
        isEnabled = isEnabled();

        //绘制背景色
        g.setColor(getBackground());
        g.fillRect(1, 1, w-2, h-2);

        /// Draw the proper Border
        if (isPressed)
        {
            g.setColor(shadow);
            g.drawRect(0, 0, w-1, h-1);
        } else
        {
            // Using the background color set above
            g.drawLine(0, 0, 0, h-1);
            g.drawLine(1, 0, w-2, 0);

            //我有空再来琢磨吧
            g.setColor(highlight);    // inner 3D border
            g.drawLine(1, 1, 1, h-3);
            g.drawLine(2, 1, w-3, 1);

            //我有空再来琢磨吧
            g.setColor(shadow);       // inner 3D border
            g.drawLine(1, h-2, w-2, h-2);
            g.drawLine(w-2, 1, w-2, h-3);

            //我有空再来琢磨吧
            g.setColor(darkShadow);     // black drop shadow  __|
            g.drawLine(0, h-1, w-1, h-1);
            g.drawLine(w-1, h-1, w-1, 0);
        }

        // If there's no room to draw arrow, bail
        if (h < 5 || w < 5)
        {
            g.setColor(origColor);
            return;
        }

        if (isPressed)
        {
            g.translate(1, 1);
        }

        // Draw the arrow
        size = Math.min((h - 4) / 3, (w - 4) / 3);
        size = Math.max(size, 2);
        //我的修改在这里
        //paintTriangle(g, (w - size) / 2, (h - size) / 2,
        paintTriangle(g, (w - size) / 2+1, (h - size) / 2,
        size, direction, isEnabled);

        // Reset the Graphics back to it's original settings
        if (isPressed)
        {
            g.translate(-1, -1);
        }
        g.setColor(origColor);

    }

    /**
     * 得到首选尺寸
     * @return 首选尺寸
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(16, 16);
    }

    /**
     * 得到最小尺寸
     * @return 最小尺寸
     */
    public Dimension getMinimumSize()
    {
        return new Dimension(5, 5);
    }

    /**
     * 得到最大尺寸
     * @return 最大尺寸
     */
    public Dimension getMaximumSize()
    {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * 得到是否可得到焦点
     * @return 不可
     */
    public boolean isFocusTraversable()
    {
        return false;
    }

    /** 画三角形
     * @param g 图形设备
     * @param x X坐标
     * @param y Y坐标
     * @param size 尺寸
     * @param direction 方向
     * @param isEnabled 使能状态参数
     */    
    public void paintTriangle(Graphics g, int x, int y, int size,
    int direction, boolean isEnabled)
    {
        Color oldColor = g.getColor();
        int mid, i, j;

        j = 0;
        size = Math.max(size, 2);
        mid = (size / 2) - 1;

        g.translate(x, y);
        if (isEnabled)
        {
            g.setColor(darkShadow);
        }
        else
        {
            g.setColor(shadow);
        }

        switch (direction)
        {
            case NORTH:
                for (i = 0; i < size; i++)
                {
                    g.drawLine(mid-i, i, mid+i, i);
                }
                if (!isEnabled)
                {
                    g.setColor(highlight);
                    g.drawLine(mid-i+2, i, mid+i, i);
                }
                break;

            case SOUTH:
                //
                if (!isEnabled)
                {
                    g.translate(1, 1);
                    g.setColor(highlight);
                    for (i = size-1; i >= 0; i--)
                    {
                        g.drawLine(mid-i, j, mid+i, j);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(shadow);
                }

                j = 0;
                for (i = size-1; i >= 0; i--)
                {
                    g.drawLine(mid-i, j, mid+i, j);
                    j++;
                }
                break;

            case WEST:
                for (i = 0; i < size; i++)
                {
                    g.drawLine(i, mid-i, i, mid+i);
                }
                if (!isEnabled)
                {
                    g.setColor(highlight);
                    //
                    g.drawLine(i, mid-i+2, i, mid+i);
                }
                break;

            case EAST:
                if (!isEnabled)
                {
                    g.translate(1, 1);
                    g.setColor(highlight);
                    for (i = size-1; i >= 0; i--)
                    {
                        g.drawLine(j, mid-i, j, mid+i);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(shadow);
                }

                j = 0;
                for (i = size-1; i >= 0; i--)
                {
                    g.drawLine(j, mid-i, j, mid+i);
                    j++;
                }
                break;
            default : break;
        }
        g.translate(-x, -y);
        g.setColor(oldColor);
    }

}

