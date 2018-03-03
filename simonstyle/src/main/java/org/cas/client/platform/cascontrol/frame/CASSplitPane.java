package org.cas.client.platform.cascontrol.frame;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;

import javax.swing.JSplitPane;

import org.cas.client.platform.cascontrol.navigation.CASNavigationPane;
import org.cas.client.platform.cascustomize.CustOpts;

public class CASSplitPane extends JSplitPane {
    /**
     * 构建器:分割窗体
     * 
     * @param prmFrame
     *            :父窗体
     * @param prmNewOrientation
     *            : 方向
     */
    public CASSplitPane(Frame prmFrame, int prmNewOrientation) {
        super(prmNewOrientation);
        setPreferredSize(new Dimension(150, 0));

        navigationPane = new CASNavigationPane(CustOpts.custOps.getNavigationPaneImage());
        navigationPane.setSize(getDSAWidth(), getDSAHeight());

        // add(DateSeleAreaPane.getInstance(),JSplitPane.TOP);这个东东尽量别秀了。因为它宽度变化时会不美观，而且导航面板的宽度是一定需要经常改变的。
        add(navigationPane, JSplitPane.BOTTOM);// @NOTE:经实验，发现放在上面和下面的都可以。

        // 因为目前将其作为Panel来用了，所以加了这两句进行修饰。
        setDividerSize(1);// 仅仅为了使上面不要太宽，但设为0的话，会有一个黑点在右上角。
        setEnabled(false);// 使鼠标移到顶部的plider上时不显示方向箭头。

        setDividerLocation(CustOpts.custOps.getDatePaneHight());
    }

    /**
     * 覆盖setDividerLocation，使上半区的高度为日期选择区的整数倍
     * 
     * @param 设定的上半区的高度
     */
    public void setDividerLocation(
            int location) {
        int splitPaneHeight = getHeight();

        if (dividerLocation == location) {
            return;
        } else if (location == splitPaneHeight / UNIT_HIGHT * UNIT_HIGHT) {
            return;
        } else if (location == UNIT_HIGHT * 3) {
            return;
        }

        // 将拖动后的分隔位置调整为一个日期选择区的缺省高度的整数倍
        dividerLocation = (location + UNIT_HIGHT / 2) / UNIT_HIGHT * UNIT_HIGHT;
        while ((dividerLocation > this.getPreferredSize().height)) {
            dividerLocation -= UNIT_HIGHT;
        }

        super.setDividerLocation(dividerLocation);
        CustOpts.custOps.setDatePaneHight(location);
    }

    /**
     * 设置大小
     * 
     * @param prmWidth
     *            : 宽度
     * @param prmHeight
     *            : 高度
     */
    public void setSize(
            int prmWidth,
            int prmHeight) {
        super.setSize(prmWidth, prmHeight);

        if (getHeight() - getDividerLocation() < UNIT_HIGHT) {
            setDividerLocation(getDividerLocation() - UNIT_HIGHT);
        }
    }

    /**
     * @called by:emo.pim.PIMMain;
     */
    public int getDSAWidth() {
        FontMetrics tmpDayFm = getFontMetrics(CustOpts.custOps.getFontOfDay());
        int tmpWidth = tmpDayFm.stringWidth("9");
        return (7 * 3 + 4) * tmpWidth + tmpWidth;
    }

    /**
     * @called by:emo.pim.PIMMain; 得到日期选择区的高度。
     */
    public int getDSAHeight() {
        int tmpHeight = getFontMetrics(CustOpts.custOps.getFontOfDay()).getHeight();
        return 7 * tmpHeight + tmpHeight / 2 + getDSABarHeight();
    }

    /**
     * 得到日期选择区中bar的高度
     * 
     * @return int bar height
     */
    public int getDSABarHeight() {
        return getFontMetrics(CustOpts.custOps.getFontOfDay()).getHeight() + 6;
    }

    /**
     * 取得导航面板树
     */
    public CASNavigationPane getNavigationPane() {
        return navigationPane;
    }

    private CASNavigationPane navigationPane;

    private int UNIT_HIGHT = getDSAHeight();// 一个日期选择区的缺省高度
    private int dividerLocation;
}

//
// /**
// *取得日历选择区的面板
// */
// public DateSeleAreaPane getDateSelectAreaPane()
// {
// return dateSelectArea;
// }
//
// private DateSeleAreaPane dateSelectArea;
