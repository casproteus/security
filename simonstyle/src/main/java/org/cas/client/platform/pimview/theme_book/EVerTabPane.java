package org.cas.client.platform.pimview.theme_book;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;

/**
 * 该类是PIM视图中的标签部分的选项卡。
 */

final class EVerTabPane extends JTabbedPane {
    /**
     * 构造器
     * 
     * @param prmTabPlacement
     *            选项卡放置的位置
     */
    EVerTabPane(int prmTabPlacement) {
        setTabPlacement(prmTabPlacement); // JTabbedPane的方法.
        font = getFont();
        fm = getFontMetrics(font);
        tabHeight = textHeight = fm.getHeight(); // 并标签的宽度，为字高。
        setUI(new EVerticalTabbedPaneUI(this));
    }

    /**
     * 改写绘制方法，改为能够绘制重叠的标签，以及改为较为美观的梯形。-------------------------
     * 
     * @param g
     *            图形设备
     */
    public void paintComponent(
            Graphics g) {
        AffineTransform tmpTransform = new AffineTransform(); // 确定标签上的文字右旋还是左旋。
        if (tabPlacement == SwingConstants.LEFT || tabPlacement == SwingConstants.TOP) {
            if (CustOpts.custOps.isViewTopAndDown()) {
                setTabPlacement(SwingConstants.TOP); // JTabbedPane的方法.
                fontRenderContext = new FontRenderContext(null, true, true);
                paintTop(g);
            } else {
                setTabPlacement(SwingConstants.LEFT); // JTabbedPane的方法.
                tmpTransform.setToRotation(-Math.PI / 2);
                fontRenderContext = new FontRenderContext(tmpTransform, true, true);
                paintLeft(g);
            }
        } else if (tabPlacement == SwingConstants.RIGHT || tabPlacement == SwingConstants.BOTTOM) {
            if (CustOpts.custOps.isViewTopAndDown()) {
                setTabPlacement(SwingConstants.BOTTOM); // JTabbedPane的方法.
                tmpTransform.setToRotation(Math.PI);
                fontRenderContext = new FontRenderContext(tmpTransform, true, true);
                paintDown(g);
            } else {
                setTabPlacement(SwingConstants.RIGHT); // JTabbedPane的方法.
                tmpTransform.setToRotation(Math.PI / 2);
                fontRenderContext = new FontRenderContext(tmpTransform, true, true);
                paintRight(g);
            }
        }
    }

    // redefine the method of parent class'--------------------------------------
    /**
     * 加选项卡面板
     * 
     * @called by emo.pim.pimview.BaseBookpane; 该方法添加需要的选项卡,为父类的私有同名方法提供一个共有入口。
     * @param prmTitle
     *            选项卡的标题
     * @param prmFontColor
     *            选项卡的颜色
     */
    void addTab(
            String prmTitle,
            Color prmFontColor) {
        super.addTab(null, new JPanel());
        titles.add(prmTitle);
        fontColors.add(prmFontColor);
        tabLengths.add(PIMPool.pool.getKey(fm.stringWidth(prmTitle) + 20)); // 得到该标签的宽度。边界为20。
        titleLens.add(PIMPool.pool.getKey(fm.stringWidth(prmTitle))); // 为了待会能将文字绘制在正中间，得到该标签上的文字的宽度。
    }

    /**
     * 得到选项卡索引
     * 
     * @called by:emo.pim.pimview.BaseBookPane 根据传入的y坐标返回
     * @param prmPosition_Y
     *            鼠标点击位置
     * @return 选项卡索引
     */
    int getTabIndex(
            int prmPosition_X,
            int prmPosition_Y) {
        if (tabPlacement == SwingConstants.LEFT) // 由高到低着个匹配。最大为当前选中Tab的Index号，最小为0，没有匹配则返回－2。
        {
            // 先确定左边有多少个
            int tmpCount = CustOpts.custOps.getActiveAppType() + 1;
            if (tmpCount == 0) {
                tmpCount = getTabCount();
            }
            int tmpTabY;
            int tmpTabHeight;
            for (int i = tmpCount; i > 0; i--) {
                tmpTabY = tmpY[i - 1][5];
                tmpTabHeight = (int) getBoundsAt(i - 1).getHeight(); // 得到待画的标签的高度。
                if (prmPosition_Y >= tmpTabY && prmPosition_Y < tmpTabY + tmpTabHeight) {
                    return i - 1;
                }
            }
            return -1;
        } else if (tabPlacement == SwingConstants.TOP) {
            // 先确定上边有多少个
            int tmpCount = CustOpts.custOps.getActiveAppType() + 1;
            if (tmpCount == 0) {
                tmpCount = getTabCount();
            }
            int tmpTabX;
            int tmpTabWidth;
            for (int i = tmpCount; i > 0; i--) {
                tmpTabX = tmpX[i - 1][5];
                tmpTabWidth = (int) getBoundsAt(i - 1).getWidth(); // 得到待画的标签的高度。
                if (prmPosition_X >= tmpTabX && prmPosition_X < tmpTabX + tmpTabWidth) {
                    return i - 1;
                }
            }
            return -1;
        } else if (tabPlacement == SwingConstants.RIGHT) {
            // 先确定左边有多少个
            int tmpSelectedIndex = CustOpts.custOps.getActiveAppType();
            int tmpTabCount = getTabCount();
            int tmpTabY;
            int tmpTabHeight;
            for (int i = tmpSelectedIndex + 1; i < tmpTabCount; i++) {
                tmpTabY = tmpY[i][0];
                tmpTabHeight = (int) getBoundsAt(i).getHeight(); // 得到待画的标签的高度。
                if (prmPosition_Y >= tmpTabY && prmPosition_Y < tmpTabY + tmpTabHeight) {
                    return i;
                }
            }
            return -1;
        } else {
            // 先确定底边有多少个
            int tmpSelectedIndex = CustOpts.custOps.getActiveAppType();
            int tmpTabCount = getTabCount();
            int tmpTabX;
            int tmpTabWidth;
            for (int i = tmpSelectedIndex + 1; i < tmpTabCount; i++) {
                tmpTabX = tmpX[i][0];
                tmpTabWidth = (int) getBoundsAt(i).getWidth(); // 得到待画的标签的高度。
                if (prmPosition_X >= tmpTabX && prmPosition_X < tmpTabX + tmpTabWidth) {
                    return i;
                }
            }
            return -1;
        }
    }

    /*
     * 为左侧标签时的绘制方法。
     */
    private void paintLeft(
            Graphics g) {
        int yAry[] = new int[6];
        int xAry[] = new int[6]; // 先准备好绘制时会用到的多边形的六个点。
        xAry[0] = xAry[1] = 0; // 左右结构时发挥作用
        xAry[2] = xAry[5] = tabHeight - 4;
        xAry[3] = xAry[4] = tabHeight;
        // 先确定需要绘制多少个
        int tmpSelectedIndex = CustOpts.custOps.getActiveAppType();

        int tmpTabNumToPaint = tmpSelectedIndex + 1; // 例如：日历的Index是0,应该画1个书签。

        if (tmpSelectedIndex < 0) // 左侧和上侧的不可能标签数目为0的，底部和右侧才可能有这种情况，如回收站应用。
        {
            tmpTabNumToPaint = getTabCount();
        }
        // 再确定供绘制的空间有多高
        int tmpHeight = getHeight();
        // 还要确定起始的位置在哪里,
        int tmpTopPos_Y = 0;
        tmpY = new int[getTabCount()][6]; // 假设每个Tab都画的话，每个Tab的位置各在哪里？
        int tmpTabHeight;
        // Note：如果有必要，这里能够优化，即不绘制看不到的tab。
        // 因为是左侧，所以应该从索引号小的开始画。
        for (int i = 0; i < tmpTabNumToPaint; i++) {
            tmpTabHeight = (int) getBoundsAt(i).getHeight(); // 得到待画的标签的高度。
            if (tmpTopPos_Y + tmpTabHeight > tmpHeight) // 如果此时Tab的长度超出供绘制的空间,说明应该多用一层来绘制。
            {
                tmpTopPos_Y = 0; // 然而，新的一层是从头开始绘制的，所以，tmpTabsLength应该从头开始积累。
            }
            tmpY[i][0] = yAry[0] = tmpTopPos_Y + 5;
            tmpY[i][1] = yAry[1] = tmpTopPos_Y + tmpTabHeight - 5;
            tmpY[i][2] = yAry[2] = tmpTopPos_Y + tmpTabHeight;
            tmpY[i][3] = yAry[3] = tmpTopPos_Y + tmpTabHeight;
            tmpY[i][4] = yAry[4] = tmpTopPos_Y;
            tmpY[i][5] = yAry[5] = tmpTopPos_Y;
            //
            xAry[0] = xAry[1] = 2;
            g.setColor(getBackgroundAt(i));
            g.fillPolygon(xAry, yAry, 6); // 绘制该Tab的背景色。
            xAry[0] = xAry[1] = 0;
            // 绘制边框
            g.setColor((Color) fontColors.get(i));
            g.drawLine(xAry[0] + 1, yAry[0] + 1, xAry[1] + 1, yAry[1] - 1);
            g.drawLine(xAry[4], yAry[4] + 1, xAry[5], yAry[5] + 1);
            g.drawLine(xAry[5], yAry[5] + 1, xAry[0] + 2, yAry[0]);
            g.setColor(Color.black);
            g.drawLine(xAry[1] + 1, yAry[1], xAry[2], yAry[2] - 1);
            g.drawLine(xAry[2], yAry[2] - 1, xAry[3], yAry[3] - 1);

            g.drawLine(xAry[0], yAry[0] + 1, xAry[1], yAry[1] - 1);
            g.drawLine(xAry[1] + 1, yAry[1] + 1, xAry[2], yAry[2]);
            g.drawLine(xAry[2], yAry[2], xAry[3], yAry[3]);
            g.drawLine(xAry[4], yAry[4], xAry[5], yAry[5]);
            g.drawLine(xAry[5], yAry[5], xAry[0] + 1, yAry[0]);
            // 绘制文字
            textLayout = new TextLayout((String) titles.get(i), font, fontRenderContext); // 初始化标签的文本布局对象。

            AffineTransform tmp = ((Graphics2D) g).getTransform(); // 左右旋转信息。
            g.setFont(font); // 取得用户定义的字体。
            g.setColor((Color) fontColors.get(i)); // 取得颜色。
            // 以下代码使标签文字旋转了90度。其绘制机制是
            // 1、旋转的轴心是字所占矩形区域的左下角。
            // 2、没旋转时字的位置：字所占矩形区域的左下角与Tab所占矩形区域的左上角重合。
            // 3、参数x表示上下方向的位移。y表示左右方向的位移。/
            int yOffset = (((Integer) tabLengths.get(i)).intValue() + ((Integer) titleLens.get(i)).intValue()) / 2;
            g.translate(0, tmpY[i][4]);
            ((Graphics2D) g).rotate(-Math.PI * 90 / 180);
            textLayout.draw((Graphics2D) g, -yOffset, textHeight - 2);
            g.translate(0, -tmpY[i][4]);
            ((Graphics2D) g).setTransform(tmp);
            // //以下文字使标签文字呈竖排显示，两种策略我个人感觉是旋转的好，尽管不是很清楚，但不旋转问题更多，如：
            // //颜色刺眼，遇到英文要特殊处理，左右两侧文字读起来有些便扭等。故先注释掉了。
            // char[] tCharAry = ((String)titles.get(i)).toCharArray();
            // for(int m = 0, len = tCharAry.length; m < len; m++)
            // g.drawString(String.valueOf(tCharAry[m]), 2,tmpY[i][4] + 8 + ((m + 1) * (g.getFontMetrics().getHeight() -
            // 1)));

            tmpTopPos_Y += tmpTabHeight - 8; // 加上已经被绘制的标签的总高度。"6"为Tab与tab间相互重叠的区域宽度。
        }
        // 画左边的纸隔板的左边沿的线。
        if (tmpTabNumToPaint == 0 || tmpSelectedIndex < 0) {
            g.drawLine(tabHeight - 1, 0, tabHeight - 1, getHeight());
        } else {
            g.drawLine(tabHeight - 1, 0, tabHeight - 1, tmpY[tmpTabNumToPaint - 1][4]);
            g.drawLine(tabHeight - 1, tmpY[tmpTabNumToPaint - 1][3] + 1, tabHeight - 1, getHeight());
        }
    }

    /*
     * 为顶部标签时的绘制方法。
     */
    private void paintTop(
            Graphics g) {
        int yAry[] = new int[6];
        yAry[4] = yAry[5] = 0;// 上下结构时发挥作用
        yAry[0] = yAry[3] = tabHeight - 4;
        yAry[1] = yAry[2] = tabHeight;
        // 先确定需要绘制多少个
        int tmpSelectedIndex = CustOpts.custOps.getActiveAppType();
        int tmpTabNumToPaint = tmpSelectedIndex + 1;

        if (tmpSelectedIndex < 0) // 判断了两种特殊情况中的右合并情况:getAppType为-2;getAppType为-1时应执行不到这里。因为Bounds为0。
        {
            tmpTabNumToPaint = getTabCount();
        }
        // 再确定供绘制的空间有多宽
        int tmpWidth = getWidth();
        // 还要确定起始的位置在哪里,
        int tmpTopPos_X = 0;
        int xAry[] = new int[6]; // 先准备好绘制时会用到的多边形的六个点。
        tmpX = new int[getTabCount()][6]; // 初始化用于存放每个Tab高度位置的数组。
        int tmpTabLength;
        // Note：如果有必要，这里能够优化，即不绘制看不到的tab。
        // 因为是左侧，所以应该从索引号小的开始画。
        for (int i = 0; i < tmpTabNumToPaint; i++) {
            tmpTabLength = (int) getBoundsAt(i).getWidth(); // 得到待画的标签的高度。
            if (tmpTopPos_X + tmpTabLength > tmpWidth) // 如果此时Tab的长度超出供绘制的空间,说明应该多用一层来绘制。
            {
                tmpTopPos_X = 0; // 然而，新的一层是从头开始绘制的，所以，tmpTabsLength应该从头开始积累。
            }
            tmpX[i][0] = xAry[0] = tmpTopPos_X;
            tmpX[i][1] = xAry[1] = tmpTopPos_X;
            tmpX[i][2] = xAry[2] = tmpTopPos_X + tmpTabLength;
            tmpX[i][3] = xAry[3] = tmpTopPos_X + tmpTabLength;
            tmpX[i][4] = xAry[4] = tmpTopPos_X + tmpTabLength - 5;
            tmpX[i][5] = xAry[5] = tmpTopPos_X + 5;
            //
            yAry[5] = yAry[4] = 2;
            g.setColor(getBackgroundAt(i));
            g.fillPolygon(xAry, yAry, 6); // 绘制该Tab的背景色。
            yAry[5] = yAry[4] = 0;
            // 绘制边框
            g.setColor((Color) fontColors.get(i));
            g.drawLine(xAry[5] + 1, yAry[5] + 1, xAry[4] - 1, yAry[4] + 1);
            g.drawLine(xAry[1] + 1, yAry[1], xAry[0] + 1, yAry[0] - 2);
            g.drawLine(xAry[0] + 2, yAry[0] - 2, xAry[5], yAry[5] + 2);
            g.setColor(Color.black);
            g.drawLine(xAry[4], yAry[4] + 1, xAry[3] - 1, yAry[3]);
            g.drawLine(xAry[3] - 1, yAry[3], xAry[2] - 1, yAry[2]);

            g.drawLine(xAry[5] + 1, yAry[5], xAry[4] - 1, yAry[4]);
            g.drawLine(xAry[4] + 1, yAry[4] + 1, xAry[3], yAry[3]);
            g.drawLine(xAry[3], yAry[3], xAry[2], yAry[2]);
            g.drawLine(xAry[1], yAry[1], xAry[0], yAry[0] - 2);
            g.drawLine(xAry[0], yAry[0], xAry[5], yAry[5] + 1);
            // 绘制文字
            textLayout = new TextLayout((String) titles.get(i), font, fontRenderContext); // 初始化标签的文本布局对象。

            g.setFont(font); // 取得用户定义的字体。
            g.setColor((Color) fontColors.get(i)); // 取得颜色。

            /**
             * 1、旋转的轴心是字所占矩形区域的左下角。 2、没旋转时字的位置：字所占矩形区域的左下角与Tab所占矩形区域的左上角重合。 3、参数x表示上下方向的位移。y表示左右方向的位移。
             * */
            g.translate(tmpX[i][1], 0); // 开始绘制。
            textLayout.draw((Graphics2D) g, 11, 13);
            g.translate(-tmpX[i][1], 0);
            tmpTopPos_X += tmpTabLength - 8; // 加上已经被绘制的标签的总高度。"6"为Tab与tab间相互重叠的区域宽度。
        }
        // 画左边的纸隔板的左边沿的线。
        if (tmpTabNumToPaint == 0 || tmpSelectedIndex < 0) {
            g.drawLine(0, tabHeight - 1, getWidth(), tabHeight - 1);
        } else {
            g.drawLine(0, tabHeight - 1, tmpX[tmpTabNumToPaint - 1][1], tabHeight - 1);
            g.drawLine(tmpX[tmpTabNumToPaint - 1][2] + 1, tabHeight - 1, getWidth(), tabHeight - 1);
        }
    }

    /*
     * 为右侧标签时的绘制方法。
     */
    private void paintRight(
            Graphics g) {
        int yAry[] = new int[6];
        int xAry[] = new int[6]; // 先准备好绘制时会用到的多边形的六个点。
        xAry[0] = xAry[1] = 0;
        xAry[2] = xAry[5] = 3;
        xAry[3] = xAry[4] = tabHeight - 2;
        // 先确定需要绘制多少个
        int tmpSelectedIndex = CustOpts.custOps.getActiveAppType(); // 得到当前选中的Tab号。
        int tmpTabNumToPaint = getTabCount() - tmpSelectedIndex - 1; // 再确定供绘制的空间有多高
        int tmpHeight = getHeight();
        // 还要确定起始的位置在哪里,
        int tmpTopPos_Y = 0;
        tmpY = new int[getTabCount()][6]; // 初始化用于存放每个Tab高度位置的数组。
        int tmpTabLength; // 用于暂存每次取出的标签的长度。
        for (int i = 0; i < getTabCount(); i++) {
            tmpTabLength = (int) getBoundsAt(i).getHeight();
            if (tmpTopPos_Y + tmpTabLength > tmpHeight) // 如果此时Tab的长度超出供绘制的空间,说明应该多用一层来绘制。
            {
                tmpTopPos_Y = 0; // 然而，新的一层是从头开始绘制的，所以，tmpTabsLength应该从头开始积累。
            }
            tmpY[i][0] = yAry[0] = tmpTopPos_Y;
            tmpY[i][1] = yAry[1] = tmpTopPos_Y + tmpTabLength;
            tmpY[i][2] = yAry[2] = tmpTopPos_Y + tmpTabLength;
            tmpY[i][3] = yAry[3] = tmpTopPos_Y + tmpTabLength - 5;
            tmpY[i][4] = yAry[4] = tmpTopPos_Y + 5;
            tmpY[i][5] = yAry[5] = tmpTopPos_Y;

            tmpTopPos_Y += tmpTabLength - 8; // 加上已经被绘制的标签的总高度。"6"为Tab与tab间相互重叠的区域宽度。
        }

        g.setFont(font); // 取得用户定义的字体，准备开始绘制。
        // Note：如果有必要，这里能够优化，即不绘制看不到的tab。
        // 因为是右侧，所以应该从索引号大的开始画。
        for (int i = tmpTabNumToPaint; i > 0; i--) {
            tmpTabLength = (int) getBoundsAt(i + tmpSelectedIndex).getHeight();
            g.setColor(getBackgroundAt(i + tmpSelectedIndex));
            g.fillPolygon(xAry, tmpY[i + tmpSelectedIndex], 6); // 绘制该Tab的背景色。
            // 绘制边框
            g.setColor((Color) fontColors.get(i + tmpSelectedIndex));
            g.drawLine(xAry[4], tmpY[i + tmpSelectedIndex][4] + 1, xAry[3], tmpY[i + tmpSelectedIndex][3] - 1);
            g.drawLine(xAry[0], tmpY[i + tmpSelectedIndex][0] + 1, xAry[5], tmpY[i + tmpSelectedIndex][5] + 1);
            g.drawLine(xAry[5], tmpY[i + tmpSelectedIndex][5] + 1, xAry[4] + 1, tmpY[i + tmpSelectedIndex][4] + 1);
            g.setColor(Color.black);
            g.drawLine(xAry[3], tmpY[i + tmpSelectedIndex][3], xAry[2], tmpY[i + tmpSelectedIndex][2] - 1);
            g.drawLine(xAry[2], tmpY[i + tmpSelectedIndex][2] - 1, xAry[1], tmpY[i + tmpSelectedIndex][1] - 1);

            g.drawLine(xAry[4] + 1, tmpY[i + tmpSelectedIndex][4] + 1, xAry[3] + 1, tmpY[i + tmpSelectedIndex][3] - 1);
            g.drawLine(xAry[3], tmpY[i + tmpSelectedIndex][3] + 1, xAry[2], tmpY[i + tmpSelectedIndex][2]);
            g.drawLine(xAry[2], tmpY[i + tmpSelectedIndex][2], xAry[1], tmpY[i + tmpSelectedIndex][1]);
            g.drawLine(xAry[0], tmpY[i + tmpSelectedIndex][0], xAry[5], tmpY[i + tmpSelectedIndex][5]);
            g.drawLine(xAry[5], tmpY[i + tmpSelectedIndex][5], xAry[4], tmpY[i + tmpSelectedIndex][4]);
            // 绘制文字
            textLayout = new TextLayout((String) titles.get(i + tmpSelectedIndex), font, fontRenderContext); // 初始化标签的文本布局对象。

            AffineTransform tmp = ((Graphics2D) g).getTransform(); // 左右旋转信息。
            g.setColor((Color) fontColors.get(i + tmpSelectedIndex)); // 取得颜色。

            g.translate(0, tmpY[i + tmpSelectedIndex][0]); // 开始绘制。

            ((Graphics2D) g).rotate(90 * Math.PI / 180);
            textLayout.draw((Graphics2D) g, 11, -2); // 11表示的是下式所表示的意义，因为其值定是11，故直接写11于此。
            // 11 = (((Integer)tabLengths.get(i)).intValue() - ((Integer)titleLens.get(i)).intValue()) / 2;
            // 其绘制机制是
            // 1、旋转的轴心是字所占矩形区域的左下角。
            // 2、没旋转时字的位置：字所占矩形区域的左下角与Tab所占矩形区域的左上角重合。
            // 3、参数x表示上下方向的位移。y表示左右方向的位移。
            g.translate(0, -tmpY[i + tmpSelectedIndex][0]);
            ((Graphics2D) g).setTransform(tmp);
        }

        // 画右边的纸隔板的右边沿的线。
        g.setColor(java.awt.Color.black);
        if (tmpSelectedIndex < getTabCount() - 1) {
            g.drawLine(0, 0, 0, tmpY[tmpSelectedIndex + 1][0]);
            g.drawLine(0, tmpY[tmpSelectedIndex + 1][1], 0, getHeight());
        } else {
            g.drawLine(0, 0, 0, getHeight());
        }
    }

    /*
     * 为底部标签时的绘制方法。
     */
    private void paintDown(
            Graphics g) {
        int yAry[] = new int[6];
        yAry[0] = yAry[5] = 0;// 上下结构时发挥作用
        yAry[1] = yAry[4] = 3;
        yAry[2] = yAry[3] = tabHeight - 2;
        // 先确定需要绘制多少个
        int tmpSelectedIndex = CustOpts.custOps.getActiveAppType(); // 得到当前选中的Tab号。
        int tmpTabNumToPaint = getTabCount() - tmpSelectedIndex - 1; // tmpSelectedIndex为-2时不会执行到这里，因为Bounds为0，不会被复制。
        // 再确定供绘制的空间有多宽
        int tmpWidth = getWidth();
        // 还要确定起始的位置在哪里,
        int tmpTopPos_X = 0;
        int xAry[] = new int[6]; // 先准备好绘制时会用到的多边形的六个点。
        tmpX = new int[getTabCount()][6]; // 初始化用于存放每个Tab高度位置的数组。
        int tmpTabLength; // 用于暂存每次取出的标签的长度。
        for (int i = 0; i < getTabCount(); i++) {
            tmpTabLength = (int) getBoundsAt(i).getWidth();
            if (tmpTopPos_X + tmpTabLength > tmpWidth) // 如果此时Tab的长度超出供绘制的空间,说明应该多用一层来绘制。
            {
                tmpTopPos_X = 0; // 然而，新的一层是从头开始绘制的，所以，tmpTabsLength应该从头开始积累。
            }
            tmpX[i][0] = xAry[0] = tmpTopPos_X;
            tmpX[i][1] = xAry[1] = tmpTopPos_X;
            tmpX[i][2] = xAry[2] = tmpTopPos_X + 5;
            tmpX[i][3] = xAry[3] = tmpTopPos_X + tmpTabLength - 5;
            tmpX[i][4] = xAry[4] = tmpTopPos_X + tmpTabLength;
            tmpX[i][5] = xAry[5] = tmpTopPos_X + tmpTabLength;

            tmpTopPos_X += tmpTabLength - 8; // 加上已经被绘制的标签的总高度。"6"为Tab与tab间相互重叠的区域宽度。
        }

        g.setFont(font); // 取得用户定义的字体，准备开始绘制。
        // Note：如果有必要，这里能够优化，即不绘制看不到的tab。
        // 因为是右/下侧，所以应该从索引号大的开始画。
        for (int i = tmpTabNumToPaint; i > 0; i--) {
            tmpTabLength = (int) getBoundsAt(i + tmpSelectedIndex).getHeight();
            g.setColor(getBackgroundAt(i + tmpSelectedIndex));
            g.fillPolygon(tmpX[i + tmpSelectedIndex], yAry, 6); // 绘制该Tab的背景色。
            // 绘制边框
            g.setColor((Color) fontColors.get(i + tmpSelectedIndex));
            g.drawLine(tmpX[i + tmpSelectedIndex][2] + 1, yAry[2], tmpX[i + tmpSelectedIndex][3] - 1, yAry[3]);
            g.drawLine(tmpX[i + tmpSelectedIndex][0] + 1, yAry[0], tmpX[i + tmpSelectedIndex][1] + 1, yAry[1]);
            g.drawLine(tmpX[i + tmpSelectedIndex][1] + 1, yAry[1], tmpX[i + tmpSelectedIndex][2] + 1, yAry[2] + 1);
            g.setColor(Color.black);
            g.drawLine(tmpX[i + tmpSelectedIndex][3], yAry[3], tmpX[i + tmpSelectedIndex][4] - 1, yAry[4]);
            g.drawLine(tmpX[i + tmpSelectedIndex][4] - 1, yAry[4], tmpX[i + tmpSelectedIndex][5] - 1, yAry[5]);

            g.drawLine(tmpX[i + tmpSelectedIndex][2] + 1, yAry[2] + 1, tmpX[i + tmpSelectedIndex][3] - 1, yAry[3] + 1);
            g.drawLine(tmpX[i + tmpSelectedIndex][3] + 1, yAry[3], tmpX[i + tmpSelectedIndex][4], yAry[4]);
            g.drawLine(tmpX[i + tmpSelectedIndex][4], yAry[4], tmpX[i + tmpSelectedIndex][5], yAry[5]);
            g.drawLine(tmpX[i + tmpSelectedIndex][0], yAry[0], tmpX[i + tmpSelectedIndex][1], yAry[1]);
            g.drawLine(tmpX[i + tmpSelectedIndex][1], yAry[1], tmpX[i + tmpSelectedIndex][2], yAry[2]);
            // 绘制文字
            textLayout = new TextLayout((String) titles.get(i + tmpSelectedIndex), font, fontRenderContext); // 初始化标签的文本布局对象。

            AffineTransform tmp = ((Graphics2D) g).getTransform(); // 左右旋转信息。
            g.setColor((Color) fontColors.get(i + tmpSelectedIndex)); // 取得颜色。

            g.translate(tmpX[i + tmpSelectedIndex][5], 0); // 开始绘制。

            ((Graphics2D) g).rotate(Math.PI);
            textLayout.draw((Graphics2D) g, 11, -2); // 11表示的是下式所表示的意义，因为其值定是11，故直接写11于此。
            // 11 = (((Integer)tabLengths.get(i)).intValue() - ((Integer)titleLens.get(i)).intValue()) / 2;
            // 其绘制机制是
            // 1、旋转的轴心是字所占矩形区域的左下角。
            // 2、没旋转时字的位置：字所占矩形区域的左下角与Tab所占矩形区域的左上角重合。
            // 3、参数x表示上下方向的位移。y表示左右方向的位移。
            g.translate(-tmpX[i + tmpSelectedIndex][5], 0);
            ((Graphics2D) g).setTransform(tmp);
        }

        // 画右边的纸隔板的右边沿的线。
        g.setColor(java.awt.Color.black);
        if (tmpSelectedIndex < getTabCount() - 1) {
            g.drawLine(0, 0, tmpX[tmpSelectedIndex + 1][0], 0);
            g.drawLine(tmpX[tmpSelectedIndex + 1][5], 0, getWidth(), 0);
        } else {
            g.drawLine(0, 0, getWidth(), 0);
        }
    }

    int tabHeight; // @for BaseBokePane

    private Font font;
    private FontMetrics fm;
    private int textHeight;
    private FontRenderContext fontRenderContext;
    private TextLayout textLayout; // 标签上的文本布局。
    private ArrayList titles = new ArrayList();
    private ArrayList fontColors = new ArrayList(); // 标签上的前景色。
    private ArrayList tabLengths = new ArrayList(); // 标签上的长度。 //@NOTE:提高可见性，以追求更好的性能。
    private ArrayList titleLens = new ArrayList(); // 标签上的文本长度。
    private int tmpX[][]; // 假设每个Tab都画的话，每个Tab的位置各在哪里？
    private int tmpY[][]; // 假设每个Tab都画的话，每个Tab的位置各在哪里？

    // =========================================================================
    /**
     * 由于在TabbedPane上控制绘制，故本类的责任是除了调Component的PaintIcon， 和返回响应Tab的宽度外什么都不做。
     */
    private class EVerticalTabbedPaneUI extends BasicTabbedPaneUI {
        /**
         * 构建器,传入引用
         * 
         * @param prmVecTabPanel
         *            本类引用
         */
        EVerticalTabbedPaneUI(EVerTabPane prmVecTabPanel) {
            vecTabPanel = prmVecTabPanel;
        }

        /**
         * 在UI中不进行绘制
         * 
         * @param g
         *            图形设备
         * @param c
         *            本体组件
         */
        public void paint(
                Graphics g,
                JComponent c) {
        }

        // ---------------------------------------------------------------------------------------------
        /**
         * 覆盖父类的此方法。
         * 
         * @param pane
         *            选项卡面板
         * @param i
         *            第几个选项
         * @return 定位矩形
         */
        public Rectangle getTabBounds(
                JTabbedPane pane,
                int i) {
            if (CustOpts.custOps.isViewTopAndDown()) {
                return new Rectangle(0, 0, ((Integer) vecTabPanel.tabLengths.get(i)).intValue(), vecTabPanel.tabHeight);
            } else {
                return new Rectangle(0, 0, vecTabPanel.tabHeight, ((Integer) vecTabPanel.tabLengths.get(i)).intValue());
            }
        }

        private EVerTabPane vecTabPanel;
    }
}
