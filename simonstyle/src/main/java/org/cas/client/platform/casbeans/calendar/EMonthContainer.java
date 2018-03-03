package org.cas.client.platform.casbeans.calendar;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Calendar;

import javax.swing.JComponent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.MonthConstant;

/**
 * 该类是日历选择区中的主要面板，包括日历条面板，日期部分，当前周的面板。
 */

class EMonthContainer extends JComponent {
    /**
     * 构造器
     * 
     * @param year
     *            设定的年份
     * @param month
     *            设定的月份
     * @param index
     *            目前还不清楚
     * @param xCoun
     * @param maxCount
     * @param group
     *            该组件的管理信息类
     */
    public EMonthContainer(int year, int month, int index, int xCount, int maxCount, PIMMonthGroup group) {
        this.containerIndex = index;
        this.maxCount = maxCount;
        this.group = group;
        container = this;

        fontWidth = getFontMetrics(CustOpts.custOps.getFontOfDay()).stringWidth("9");
        fontHeight = getFontMetrics(CustOpts.custOps.getFontOfDay()).getHeight();
        weekFontWidth = getFontMetrics(CustOpts.custOps.getFontOfWeek()).stringWidth("9");
        weekFontHeight = getFontMetrics(CustOpts.custOps.getFontOfWeek()).getHeight();
        int barWidth = ((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSABarHeight();
        weekViewFlag = ((Boolean) group.getValue(MonthPropertyConstants.WEEK_NUMBER_SHOW)).booleanValue();

        totalWidth = group.getMonthSelectWidth();
        dayWidth = totalWidth - 2 * fontWidth;
        totalHeight = group.getMonthSelectHeight() - barWidth;

        startFlag = (containerIndex == 0);
        endFlag = (containerIndex + 1 == maxCount);
        forwardFlag = (containerIndex + 1 == xCount);

        viewYear = year;
        viewMonth = month;
        correctMonth(); // 校验参数是否合法
        calDate.setMinimalDaysInFirstWeek(7);

        dayBoldFont = CustOpts.custOps.getFontOfDay().deriveFont(Font.PLAIN);

        barCanvas = new ECalendarBar(this);
        barCanvas.setBounds(0, 0, totalWidth, barWidth);
        add(barCanvas); // 加入日历条面板

        dayCanvas = new ECalendarCanvas(this);
        dayCanvas.setBounds(2 * fontWidth, barWidth, dayWidth, totalHeight);
        add(dayCanvas); // 日期部分的面板

        weekCanvas = new EWeekCanvas(this);
        weekCanvas.setBounds(0, barWidth, 2 * fontWidth, totalHeight);
        add(weekCanvas); // 加入选择每周数据的面板,该面板在设置显示周数后显示
    }

    /**
     * 校验显示的月是否合法，并作修改
     */
    private void correctMonth() {
        while (viewMonth < 0) {
            viewMonth += 12; // 月数增加12个月
            --viewYear; // 年数减少一年
        }
        while (viewMonth > 11) {
            viewMonth -= 12; // 减少12个月
            ++viewYear; // 年数增加一年
        }
    }

    /**
     * 根据确定的年和月显示相应的视图
     * 
     * @param year
     *            需要显示的年份
     * @param month
     *            需要显示的月份
     */
    public void showNewCalendar(
            int year,
            int month) {
        repaintCalendar(year, month);
        group.changeMonth(containerIndex, viewYear, viewMonth, false);
    }

    /**
     * 重新根据确定的年和月绘制相应的视图
     * 
     * @param year
     *            确定的年
     * @param month
     *            确定的月
     */
    public void repaintCalendar(
            int year,
            int month) {
        viewYear = year;
        viewMonth = month;
        correctMonth();

        isSelectWeek = false;
        barCanvas.setNewMonth(viewYear, viewMonth);
        dayCanvas.setNewMonth(viewYear, viewMonth);
        weekCanvas.setNewMonth(viewYear, viewMonth);
        dayCanvas.getMonthStatus();

        barCanvas.repaint();
        dayCanvas.paintImmediately(0, 0, dayCanvas.getWidth(), dayCanvas.getHeight());
        weekCanvas.repaint();
    }

    /**
     * 返回是否在容器中的标志
     * 
     * @return true: 鼠标在容器中 false:鼠标不在容器中
     */
    public boolean isInContainer() {
        return isInContainer;
    }

    /**
     * 设置鼠标的位置标志
     * 
     * @param isIn
     *            true:鼠标在容器中 false:鼠标不在容器中
     */
    public void setInContainer(
            boolean isIn) {
        isInContainer = isIn;
    }

    public void viewSelectedStatus(
            boolean isTmp) {
        isSelectWeek = false;
        dayCanvas.showSelectedDays(isTmp);
    }

    public void restoreSelectedStatus(
            boolean flag,
            boolean isTmp) {
        isSelectWeek = false;
        dayCanvas.restoreAllDay(flag, isTmp);
    }

    /**
     * 该方法返回当前显示的年份
     * 
     * @return 当前年份
     */
    public int getYear() {
        return viewYear;
    }

    /**
     * 该方法返回当前显示的月份
     * 
     * @return 当前月份
     */
    public int getMonth() {
        return viewMonth;
    }

    /**
     * 根据传入的坐标和标志显示多选天数据相应的视图
     * 
     * @param x
     *            选择的X坐标。
     * @param y
     *            选择的Y坐标
     * @param isDrag
     *            选择区域是否是鼠标的拖拉事件产生的。
     */
    public void setMultipleDay(
            int x,
            int y,
            boolean isDrag) {
        dayCanvas.selectMultipleDays(x, y, isDrag);
    }

    /**
     * 根据传入的坐标和标志显示多选周数据相应的视图
     * 
     * @param x
     *            选择的X坐标
     * @param y
     *            选择的Y坐标
     * @param isDrag
     *            选择区域是否是鼠标的拖拉事件产生的。
     */
    public void setMultipleWeek(
            int x,
            int y,
            boolean isDrag) {
        weekCanvas.selectMultipleWeeks(x, y, isDrag);
    }

    public void adjustContainer(
            int dayIndex,
            int dis,
            int day[],
            boolean isTmp) {
        if (dayIndex == -1) {
            group.setMultipleStatus(isTmp);
            if (dis > 0) {
                group.arrangeContainer(true, day);
            } else if (dis < 0) {
                group.arrangeContainer(false, day);
            } else {
                group.changeMonth(day[3], day[0], day[1], true);
            }
        } else {
            group.viewSelectedStatus(-1, isTmp);
        }
    }

    public void writeTemporaryData(
            int year,
            int month,
            int date,
            boolean isWeek,
            boolean isLast) {
        group.addTemporaryDay(year, month, date, isWeek);
        if (isLast) {
            group.setLastDay(containerIndex, year, month, date);
        }
    }

    public void restoreCalendar(
            int status,
            boolean isTmp) {
        if (isTmp == false) {
            if (status == MonthConstant.NO_WEEK_DAY) {
                restoreSelectedStatus(false, isTmp);
                return;
            } else if (status != MonthConstant.ALL_DAY) {
                return;
            }
        }
        restoreSelectedStatus(true, isTmp);
    }

    /**
     * 根据传入的位置处理相应的选择
     * 
     * @param x
     *            传入的X坐标
     * @param y
     *            传入的Y坐标
     */
    public void performSelect(
            int x,
            int y) {
        group.setMultipleStatus(false);
        if (group.isMouseInCalendar()) {
            writeCalendarData();
            if (((Boolean) group.getValue(MonthPropertyConstants.WORK_WEEK_MODEL)).booleanValue()) {
                group.selectWorkWeekModel(true);
            }
        } else {
            group.removeTemporary();
        }
    }

    private void writeCalendarData() {
        if (group.hasData() == false) {
            return;
        }
        final int status = group.getRemoveStatus();
        final int selModel = group.getSelectedModel();
        if (status == MonthConstant.ALL_DAY || selModel == MonthConstant.MONTH_MODEL) {
            group.removeAllDay(-1, true);
        } else if (status == MonthConstant.NO_WEEK_DAY) {
            group.removeAllDay(-1, false);
        }

        group.writeCalendarData(true);
        group.viewSelectedStatus(-1, false);

        if (changeYear != -1 && changeMonth != -1) {
            showNewCalendar(changeYear, changeMonth);
        }
        changeYear = -1;
        changeMonth = -1;
    }

    /**
     * 处理鼠标拖动引起的视图变化
     * 
     * @param evt
     *            鼠标拖动事件
     * @param isWeek
     *            是否是选择了整月的标志
     */
    public void performMouseDragged(
            MouseEvent evt,
            boolean isWeek) {
        if (!javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
            return;
        }

        int day[];
        int dayIndex;

        int x = evt.getX();
        int y = evt.getY();
        Point pt = getLocationOnScreen();
        int calX = pt.x;
        int calY = pt.y;
        boolean statusFlag = group.getInOutFlag();
        if (group.isMouseInCalendar()) {
            if (statusFlag == false) {
                restoreAllCalendar(false);
                day = group.getTmpLastDay();
                dayIndex = group.getContainerIndex(day[0], day[1]);
                adjustContainer(dayIndex, 0, day, true);
                group.setInOutFlag(true);
            }
            if ((((Boolean) group.getValue(MonthPropertyConstants.SINGLE_MODEL)).booleanValue() && isWeek)
                    || (!((Boolean) group.getValue(MonthPropertyConstants.DRAGABLE)).booleanValue())) {
                restoreAllCalendar(false);
                return;
            }

            boolean initFlag = group.getDragInitFlag();
            int removeStatus = group.getRemoveStatus();
            if (initFlag == false) {
                if (removeStatus != MonthConstant.NONE_DAY) {
                    restoreAllCalendar(false);
                }
                group.setDragInitFlag(true);
            }
            group.changeSlectedDays();
            boolean dragFlag = true;
            if (evt.isShiftDown()) {
                dragFlag = false;
            }
            if (isWeek) {
                group.setMultipleWeek(calX + x, calY + y, dragFlag);
            } else {
                group.setMultipleDay(calX + x, calY + y, dragFlag);
            }
        } else {
            if (statusFlag == true) {
                restoreAllCalendar(true);
                day = group.getLastDay();
                dayIndex = group.getContainerIndex(day[0], day[1]);
                int status = group.getRemoveStatus();
                group.setRemoveStatus(MonthConstant.NONE_DAY);
                adjustContainer(dayIndex, 0, day, false);
                group.setInOutFlag(false);
                group.setRemoveStatus(status);
            }
        }
    }

    public void restoreAllCalendar(
            boolean isTmp) {
        group.restoreAllCalendar(isTmp);
    }

    /**
     * 获得相应的管理类
     * 
     * @param 返回该管理类的实例
     */
    public PIMMonthGroup getContainerManager() {
        return group;
    }

    public boolean isStart() {
        return startFlag;
    }

    public boolean isEnd() {
        return endFlag;
    }

    public boolean isForward() {
        return forwardFlag;
    }

    /**
     * 返回是否是整月模式
     * 
     * @return true: 整月被选择 false: 选择了某一天
     */
    public boolean isSelectWeek() {
        return isSelectWeek;
    }

    /**
     * 设置选择整月的标志
     * 
     * @param isWeek
     *            整月的标志
     */
    public void setSelectWeek(
            boolean isWeek) {
        isSelectWeek = isWeek;
    }

    /**
     * 设置当前视图中新的年份
     */
    public void setChangeYear(
            int year) {
        changeYear = year;
    }

    /**
     * 设置当前视图中新的月份
     */
    public void setChangeMonth(
            int month) {
        changeMonth = month;
    }

    /**
     * 返回日期部分的面板的实例
     * 
     * @return 返回当前的日期部分的面板实例
     */
    public ECalendarCanvas getDayCanvas() {
        return dayCanvas;
    }

    /**
     * 重新绘制选择每周数据的面板
     */
    public void repaintWeekCanvas() {
        if (weekCanvas != null) {
            weekCanvas.repaint();
        }
    }

    /**
     * 选择整月的模式
     */
    public void selectMonthModel() {
        dayCanvas.selectMonthModel();
    }

    // public static void main(String [] args)
    // {
    // javax.swing.JFrame f = new javax.swing.JFrame();
    // f.setSize(600,600);
    // java.awt.Container c = f.getContentPane();
    // EMonthContainer pane = new EMonthContainer(null);
    // c.setLayout(new java.awt.BorderLayout());
    // c.add(pane);
    // f.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
    // f.setVisible(true);
    // }
    //
    private PIMMonthGroup group;
    private ECalendarCanvas dayCanvas;
    private ECalendarBar barCanvas;
    private EWeekCanvas weekCanvas;
    private EMonthContainer container;

    private Font dayBoldFont;

    private int viewYear, viewMonth;
    private int fontWidth, fontHeight, weekFontWidth, weekFontHeight;
    private int totalWidth, totalHeight, dayWidth;
    private int containerIndex, maxCount;
    private boolean startFlag, endFlag, forwardFlag, weekViewFlag;
    private int changeYear = -1;
    private int changeMonth = -1;

    private boolean isInContainer;

    private boolean isSelectWeek;

    private Calendar calDate = Calendar.getInstance();
}
