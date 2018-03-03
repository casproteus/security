package org.cas.client.platform.pimview.theme_book;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import org.cas.client.platform.cascustomize.CustOpts;

/**
 * TODO:该类应该为包内可见。
 */

class EBinderClamp extends JComponent {
    private static int DEFULT_WIDTH = 32;
    private static int SHORT_WIDTH = 5;
    private static int CLAMPWIDTH = 5;

    /** 构造器 */
    public EBinderClamp() {
        setOpaque(false);
        CustOpts.custOps = CustOpts.custOps;
        sepratorColor = Color.lightGray;
    }

    /**
     * 绘制面板中的组件
     * 
     * @param g
     *            绘制的图形
     */
    public void paintComponent(
            Graphics g) {
        isBookHide = CustOpts.custOps.isBaseBookHide();
        isTopAndDown = CustOpts.custOps.isViewTopAndDown();
        if (isBookHide) {
            if (isTopAndDown) {
                g.setColor(sepratorColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.setColor(sepratorColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        } else {
            if (isTopAndDown) {
                int tmpDotTop = 8; // 确定画黑点时，x轴方向的缩进。
                int tmpDotLeft = 30; // 确定画黑点时，y轴方向的缩进。
                int tmpDotRight = getWidth() - tmpDotLeft; // 确定需要画点的区域的右边限。
                int tmpDotDown = getHeight() - tmpDotTop; // 确定需要画点的区域的下边限。

                // 先画好夹子的背景色，默认白色。
                g.setColor(CustOpts.custOps.getClampColor());
                g.fillRect(tmpDotLeft - 2, tmpDotTop - 2, tmpDotRight - tmpDotLeft + 3, tmpDotDown - tmpDotTop + 3);

                // 在背景上画点，默认黑点。
                g.setColor(Color.black);
                boolean tmpIsFirst = true;
                for (int i = tmpDotTop; i < tmpDotDown; i += 1, tmpIsFirst = !tmpIsFirst) {
                    for (int j = tmpDotLeft + (tmpIsFirst ? 0 : 1); j < tmpDotRight; j += 2) {
                        g.drawLine(j, i, j, i);
                    }
                }

                // 给带点区域添加边框线，默认为黑色。
                g.drawRect(tmpDotLeft - 3, tmpDotTop - 3, tmpDotRight - tmpDotLeft + 5, tmpDotDown - tmpDotTop + 5);

                // 画上下两端的装饰物。
                g.drawImage(CustOpts.custOps.getClampTopImage(), tmpDotLeft - 10, 7, this);
                g.drawImage(CustOpts.custOps.getClampEndImage(), tmpDotRight + 2, 7, this);

                // 如果书本为打开状态。
                if (isSpinVisible) {
                    // 给左右两侧的纸张画两个装订线边界。
                    g.setColor(Color.white);
                    g.fillRect(0, 0, getWidth(), middle - CLAMPWIDTH);
                    g.fillRect(0, middle + CLAMPWIDTH, getWidth(), middle - CLAMPWIDTH);
                    // 画左右纸张的边界线，分别默认为黑色和蓝灰色。
                    g.setColor(Color.black);
                    g.drawLine(0, middle - CLAMPWIDTH, getWidth(), middle - CLAMPWIDTH);
                    g.setColor(new Color(115, 162, 198));
                    g.drawLine(0, middle + CLAMPWIDTH - 1, getWidth(), middle + CLAMPWIDTH - 1);

                    // 画夹子上的别针。
                    int tmpDis =
                            (getWidth() - 2 * CustOpts.custOps.getVerticalDis())
                                    / (CustOpts.custOps.getClampNumber() + 1); // 算出每两个别针间的距离。
                    int x = tmpDis + CustOpts.custOps.getVerticalDis();
                    for (int i = 0; i < CustOpts.custOps.getClampNumber(); ++i) {
                        g.drawImage(CustOpts.custOps.getClampImage(), x, 0, this); // 绘制别针的图片
                        x += tmpDis;
                    }
                }
            } else {
                int tmpDotLeft = 8; // 确定画黑点时，x轴方向的缩进。
                int tmpDotTop = 30; // 确定画黑点时，y轴方向的缩进。
                int tmpDotRight = getWidth() - tmpDotLeft; // 确定需要画点的区域的右边限。
                int tmpDotDown = getHeight() - tmpDotTop; // 确定需要画点的区域的下边限。

                // 先画好夹子的背景色，默认白色。
                g.setColor(CustOpts.custOps.getClampColor());
                g.fillRect(tmpDotLeft - 2, tmpDotTop - 2, tmpDotRight - tmpDotLeft + 3, tmpDotDown - tmpDotTop + 3);

                // 在背景上画点，默认黑点。
                g.setColor(Color.black);
                boolean tmpIsFirst = true;
                for (int i = tmpDotTop; i < tmpDotDown; i += 1, tmpIsFirst = !tmpIsFirst) {
                    for (int j = tmpDotLeft + (tmpIsFirst ? 0 : 1); j < tmpDotRight; j += 2) {
                        g.drawLine(j, i, j, i);
                    }
                }

                // 给带点区域添加边框线，默认为黑色。
                g.drawRect(tmpDotLeft - 3, tmpDotTop - 3, tmpDotRight - tmpDotLeft + 5, tmpDotDown - tmpDotTop + 5);

                // 画上下两端的装饰物。
                g.drawImage(CustOpts.custOps.getClampTopImage(), 7, tmpDotTop - 10, this);
                g.drawImage(CustOpts.custOps.getClampEndImage(), 7, tmpDotDown + 2, this);

                // 如果书本为打开状态。
                if (isSpinVisible) {
                    // 给左右两侧的纸张画两个装订线边界。
                    g.setColor(Color.white);
                    g.fillRect(0, 0, middle - CLAMPWIDTH, getHeight());
                    g.fillRect(middle + CLAMPWIDTH, 0, middle - CLAMPWIDTH, getHeight());
                    // 画左右纸张的边界线，分别默认为黑色和蓝灰色。
                    g.setColor(Color.black);
                    g.drawLine(middle - CLAMPWIDTH, 0, middle - CLAMPWIDTH, getHeight());
                    g.setColor(new Color(115, 162, 198));
                    g.drawLine(middle + CLAMPWIDTH - 1, 0, middle + CLAMPWIDTH - 1, getHeight());

                    // 画夹子上的别针。
                    int tmpDis =
                            (getHeight() - 2 * CustOpts.custOps.getVerticalDis())
                                    / (CustOpts.custOps.getClampNumber() + 1); // 算出每两个别针间的距离。
                    int y = tmpDis + CustOpts.custOps.getVerticalDis();
                    for (int i = 0; i < CustOpts.custOps.getClampNumber(); ++i) {
                        g.drawImage(CustOpts.custOps.getClampImage(), 0, y, this); // 绘制别针的图片
                        y += tmpDis;
                    }

                }
            }
        }
    }

    /**
     * 该方法返回装订线的宽度
     * 
     * @return 装订线的宽度
     */
    static int getBinderWidth() {
        if (CustOpts.custOps.isBaseBookHide())
            return SHORT_WIDTH;
        else
            return DEFULT_WIDTH;
    }

    /**
     * 该方法返回装订线夹子的宽度
     * 
     * @return 装订线夹子的颜色
     */
    public static int getClampWidth() {
        return CLAMPWIDTH;
    }

    /**
     * 设置装订线是否可见
     * 
     * @called by: emo.pim.pimcontrol.BaseBookPane;
     * @param prmIsvisible
     *            是否为可见
     */
    public void setSpinsVisible(
            boolean prmIsvisible) {
        isSpinVisible = prmIsvisible;
    }

    private int middle = DEFULT_WIDTH / 2;
    private boolean isSpinVisible;

    private boolean isBookHide;
    private boolean isTopAndDown;
    private Color sepratorColor;
}
