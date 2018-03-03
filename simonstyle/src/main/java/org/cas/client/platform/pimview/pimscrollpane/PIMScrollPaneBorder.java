package org.cas.client.platform.pimview.pimscrollpane;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class PIMScrollPaneBorder extends AbstractBorder implements UIResource {

    private static final Insets insets = new Insets(1, 1, 2, 2);

    /**
     * 实现接口中的方法,画边框
     * 
     * @param c
     *            绘制组件
     * @param g
     *            图形设备
     * @param x
     *            X坐标
     * @param y
     *            Y坐标
     * @param w
     *            宽度
     * @param h
     *            高度
     */
    public void paintBorder(
            Component c,
            Graphics g,
            int x,
            int y,
            int w,
            int h) {

        PIMScrollPane scroll = (PIMScrollPane) c;
        JComponent colHeader = scroll.getColumnHeader();
        int colHeaderHeight = 0;
        if (colHeader != null) {
            colHeaderHeight = colHeader.getHeight();
        }

        JComponent rowHeader = scroll.getRowHeader();
        int rowHeaderWidth = 0;
        if (rowHeader != null) {
            rowHeaderWidth = rowHeader.getWidth();
        }

        g.translate(x, y);

        g.setColor(MetalLookAndFeel.getControlDarkShadow());
        g.drawRect(0, 0, w - 2, h - 2);
        g.setColor(MetalLookAndFeel.getControlHighlight());

        g.drawLine(w - 1, 1, w - 1, h - 1);
        g.drawLine(1, h - 1, w - 1, h - 1);

        g.setColor(MetalLookAndFeel.getControl());
        g.drawLine(w - 2, 2 + colHeaderHeight, w - 2, 2 + colHeaderHeight);
        g.drawLine(1 + rowHeaderWidth, h - 2, 1 + rowHeaderWidth, h - 2);

        g.translate(-x, -y);

    }

    /**
     * 得到边距
     * 
     * @param c
     *            绘制组件
     * @return 边距
     */
    public Insets getBorderInsets(
            Component c) {
        return insets;
    }
}
