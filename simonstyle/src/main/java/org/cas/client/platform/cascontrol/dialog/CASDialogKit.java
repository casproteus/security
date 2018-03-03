package org.cas.client.platform.cascontrol.dialog;

import java.awt.Container;

import javax.swing.JComponent;

import org.cas.client.platform.cascustomize.CustOpts;

/**
 * 用于在绘制dialog时提供一定的帮助，加快绘制的速度。
 */

public class CASDialogKit {
    /**
     * 获得组件的最大首选宽度
     * 
     * @param comp
     *            需要比较宽度的组件
     * @return 被比较组件的最大宽度
     */
    public static int getMaxWidth(
            JComponent[] comp) {
        int maxWidth = 0;

        for (int i = 0; i < comp.length; i++) {
            maxWidth = maxWidth > comp[i].getPreferredSize().width ? maxWidth : comp[i].getPreferredSize().width;
        }

        return maxWidth;
    }

    /**
     * Creates a new instance of PIMDialogKit
     * 
     * @param c
     *            添加组件的容器
     */
    public CASDialogKit(Container c) {
        setContainer(c);
        setDefaultGap();
    }

    /**
     * 获得kit处理的容器
     * 
     * @return 该容器
     */
    public Container getContainer() {
        return container;
    }

    /**
     * 设置kit处理的容器
     * 
     * @param c
     *            该容器
     */
    public void setContainer(
            Container c) {
        container = c;
        container.setLayout(null);
    }

    /** 设置组件的缺省间距 */
    public void setDefaultGap() {
        hGap = CustOpts.HOR_GAP;
        vGap = CustOpts.VER_GAP;
    }

    /**
     * 设置组件的水平间距
     * 
     * @param gap
     *            水平间距值
     */
    public void setHGap(
            int gap) {
        hGap = gap;
    }

    /**
     * 设置组件的竖直间距
     * 
     * @param gap
     *            竖直间距值
     */
    public void setVGap(
            int gap) {
        vGap = gap;
    }

    /**
     * 获得组件的右边界的坐标
     * 
     * @param comp
     *            该组件
     * @return 右边界坐标
     */
    public int getRightLine(
            JComponent comp) {
        return comp.getX() + comp.getWidth();
    }

    /**
     * 获得组件的右边界的纵坐标的最大值
     * 
     * @param comp
     *            这组组件
     * @return 最大值
     */
    public int getRightLine(
            JComponent[] comp) {
        int maxWidth = 0;
        int tmpRightLine;

        for (int i = 0; i < comp.length; i++) {
            tmpRightLine = getRightLine(comp[i]);
            maxWidth = maxWidth > tmpRightLine ? maxWidth : tmpRightLine;
        }

        return maxWidth;
    }

    /**
     * 获得组件下沿的坐标
     * 
     * @param comp
     *            该组件
     * @return 下沿坐标
     */
    public int getBottom(
            JComponent comp) {
        checkSize(comp);
        return comp.getY() + comp.getHeight();
    }

    /**
     * 返回组件comp下面一个组件的Y坐标
     * 
     * @param comp
     *            该组件
     * @return Y坐标
     */
    public int getNextY(
            JComponent comp) {
        return getBottom(comp) + CustOpts.VER_GAP;
    }

    /**
     * 返回组件comp右边一个组件的X坐标
     * 
     * @param comp
     *            该组件
     * @return X坐标
     */
    public int getNextX(
            JComponent comp) {
        return getRightLine(comp) + CustOpts.HOR_GAP;
    }

    /**
     * 返回所有组件横向排列的宽度
     * 
     * @param comp
     *            一组组件
     * @return 宽度
     */
    public int getWidth(
            JComponent[] comp) {
        return getWidth(comp, hGap);
    }

    /**
     * 返回所有组件横向排列的宽度
     * 
     * @param comp
     *            一组组件
     * @param gap
     *            间距
     * @return 宽度
     */
    public int getWidth(
            JComponent[] comp,
            int gap) {
        int totalWidth = (comp.length - 1) * gap;

        for (int i = 0; i < comp.length; i++) {
            totalWidth += comp[i].getPreferredSize().width;
        }

        return totalWidth;
    }

    /**
     * 当组件的长或宽为0时，设为首选大小
     *
     * @param comp
     *            被调整的组件
     */
    private void checkSize(
            JComponent comp) {
        if (0 == comp.getWidth()) {
            comp.setSize(comp.getPreferredSize().width, comp.getHeight());
        }

        if (0 == comp.getHeight()) {
            comp.setSize(comp.getWidth(), comp.getPreferredSize().height);
        }
    }

    // *************************************************************************
    // 加入ebean组件时，建议采用ebean的added方法。
    /**
     * 用于将子组件加入父组件中。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param x
     *            X坐标
     * @param y
     *            Y坐标
     */
    public void add(
            JComponent comp,
            int x,
            int y) {
        checkSize(comp);
        comp.setBounds(x, y, comp.getWidth(), comp.getHeight());
        container.add(comp);
    }

    /**
     * 用于将子组件加入坐标组件的上边。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param origin
     *            父组件
     */
    public void addAbove(
            JComponent comp,
            JComponent origin) {
        addAbove(comp, origin, vGap);
    }

    /**
     * 用于将子组件加入坐标组件的上边。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param origin
     *            上组件
     * @param gap
     *            间距
     */

    public void addAbove(
            JComponent comp,
            JComponent origin,
            int gap) {
        checkSize(comp);
        add(comp, origin.getX(), origin.getY() - gap - comp.getHeight());
    }

    /**
     * 用于将子组件加入坐标组件的下边。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param origin
     *            父组件
     */
    public void addUnder(
            JComponent comp,
            JComponent origin) {
        addUnder(comp, origin, vGap);
    }

    /**
     * 用于将子组件加入坐标组件的下边。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param origin
     *            父组件
     * @param gap
     *            间距
     */
    public void addUnder(
            JComponent comp,
            JComponent origin,
            int gap) {
        checkSize(comp);
        add(comp, origin.getX(), origin.getY() + origin.getHeight() + gap);
    }

    /**
     * 用于将子组件加入坐标组件的左边。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param origin
     *            父组件
     */
    public void addLeft(
            JComponent comp,
            JComponent origin) {
        addLeft(comp, origin, hGap);
    }

    /**
     * 用于将子组件加入坐标组件的左边。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param origin
     *            父组件
     * @param gap
     *            间距
     */
    public void addLeft(
            JComponent comp,
            JComponent origin,
            int gap) {
        checkSize(comp);
        add(comp, origin.getX() - gap - comp.getWidth(), origin.getY());
    }

    /**
     * 用于将子组件加入坐标组件的右边。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param origin
     *            父组件
     */
    public void addRight(
            JComponent comp,
            JComponent origin) {
        addRight(comp, origin, hGap);
    }

    /**
     * 用于将子组件加入坐标组件的右边。对于ebean，建议采用ebean的added方法。
     * 
     * @param comp
     *            子组件
     * @param origin
     *            父组件
     * @param gap
     *            间距
     */
    public void addRight(
            JComponent comp,
            JComponent origin,
            int gap) {
        checkSize(comp);
        add(comp, origin.getX() + origin.getWidth() + gap, origin.getY());
    }

    /*
     * // for test public static void main(String args[]) { JFrame f = new JFrame(); Container pane =
     * f.getContentPane(); pane.setLayout(null); PIMDialogKit kit = new PIMDialogKit(pane); EButton center = new
     * EButton("center"); kit.add(center, 200, 200); kit.addLeft(new EButton("left"), center); kit.addRight(new
     * EButton("right"), center); kit.addAbove(new EButton("above"), center); //kit.addUnder(new EButton("under"),
     * center); //ETextArea text = new ETextArea();//BUTTON_HEIGHT * 3, 100, true); //text.setRows(5); EComboBox box =
     * new EComboBox(); box.setSize(box.getMinimumSize()); kit.addUnder(box, center);
     * f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); f.setSize(500,500); f.setVisible(true); }
     */

    private Container container; // 容器
    private int hGap; // 水平间距
    private int vGap; // 竖直间距
}
