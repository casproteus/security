package org.cas.client.platform.casbeans.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;

import javax.swing.JComponent;
import javax.swing.JToolTip;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.MonthConstant;

/**
 * 该类提供日期选择区中日期部分的面板。
 */

class ECalendarCanvas extends JComponent implements MouseListener, MouseMotionListener {
    /**
     * 构造器
     * 
     * @param container
     *            该面板的容器
     * @caled by: EMonthContainer
     */
    public ECalendarCanvas(EMonthContainer container) {
        parent = container;
        initData(); // 从container处获得数据
        nowYear = calDate.get(Calendar.YEAR);
        nowMonth = calDate.get(Calendar.MONTH);
        nowDate = calDate.get(Calendar.DATE);
        dayInfo = new int[6 * 3];
        getMonthStatus(); // 获得当前月的初始化数据
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * 从container处获得数据的数据
     */
    private void initData() {
        group = parent.getContainerManager();

        dayFont = CustOpts.custOps.getFontOfDay();

        fontWidth = getFontMetrics(dayFont).stringWidth("9");
        fontHeight = getFontMetrics(dayFont).getHeight();

        totalWidth = group.getMonthSelectWidth();
        dayWidth = totalWidth - 2 * fontWidth;
        totalHeight =
                group.getMonthSelectHeight()
                        - ((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSABarHeight();

        startFlag = parent.isStart();
        endFlag = parent.isEnd();
        viewYear = parent.getYear();
        viewMonth = parent.getMonth();
    }

    /**
     * 绘制组件的内容
     * 
     * @param g
     *            绘制图形的句柄
     */
    public void paintComponent(
            Graphics g) {
        graphics = g;
        g.setFont(dayFont);
        dayLine = 0;
        g.setColor(backColor);
        g.fillRect(0, 0, dayWidth, totalHeight);

        g.setColor(textColor);
        int x = xInit;
        int y = yInit;
        drawWeekTitle(g, x, y); // 绘制每周的标题分
        y += fontHeight;

        y = drawLastMonthDate(g, x, y); // 绘制上一月中最后几天的数据，并返回结束的Y坐标

        g.setColor(textColor);
        String str;
        int now = 0;
        int offset = 0;
        drawDate: for (int i = 0; i < 6; ++i, ++dayLine) {
            x = xInit;
            for (int j = 0; j < 7; ++j) {
                if ((i == 0) && (firstDay - 1 > j)) {
                    x += dayDis;
                    continue;
                }

                now = i * 7 + j - (firstDay - 1);
                if (now >= days) {
                    offset = j % 7;
                    if (j == 0) {
                        --dayLine;
                    }
                    break drawDate;
                }

                ++now;
                str = Integer.toString(now);
                int tmpX = x;
                if (str.length() <= 1) {
                    tmpX += fontWidth;
                }
                g.drawString(str, tmpX, y); // 绘制该月的数据
                if (j == 0) {
                    setDayInformation(dayLine, now, 0);
                }

                if (nowYear == viewYear && nowMonth == viewMonth && nowDate == now) {
                    g.setColor(rectColor);
                    g.drawRect(x - xOffset, y - yInit + yOffset, dayDis, fontHeight + 1);
                    g.setColor(textColor); // 以红色绘制系统中今日的时间
                }
                x += dayDis;
            }
            y += fontHeight;
        }
        monthLine = dayLine;

        drawNextMonthDate(g, x, y, offset); // 绘制下月的时间在本视图中的数据

        showSelectedDays(group.getMultipleStatus());
        parent.repaintWeekCanvas();
        graphics = null;
    }

    /**
     * 绘制每周的标题部分
     * 
     * @param g
     *            绘制图形的句柄
     * @param x
     *            标题的起始X坐标
     * @param y
     *            标题的起始Y坐标
     */
    private void drawWeekTitle(
            Graphics g,
            int x,
            int y) {
        int dayLen = MonthConstant.WEEK_TITLE.length;
        for (int i = firstWeekDay; i < dayLen; ++i) {
            g.drawString(MonthConstant.WEEK_TITLE[i], x + fontWidth, y);
            x += dayDis;
        }
        for (int i = 0; i < firstWeekDay; ++i) {
            g.drawString(MonthConstant.WEEK_TITLE[i], x + fontWidth, y);
            x += dayDis;
        }
        g.drawLine(xInit - xOffset, y + 1, dayWidth - xInit - 2 * fontWidth + fontWidth / 2, y + 1);
    }

    /**
     * 绘制每月的最后几天。
     * 
     * @param g
     *            绘制图形的句柄
     * @param x
     *            绘制最后几天的初始X坐标
     * @param y
     *            绘制最后几天的初始Y坐标
     * @return 每月的一号的Y坐标
     */
    private int drawLastMonthDate(
            Graphics g,
            int x,
            int y) {
        if (!startFlag) {
            return y;
        }

        String str;
        int startDay = (firstDay == 1) ? 0 : 7 - (firstDay - 1);
        for (int i = startDay; i < 7; ++i) {
            int tmpDay = lastDays - (7 - i) + 1;
            g.setColor(invalidateColor);
            str = Integer.toString(tmpDay);
            if (i == startDay) {
                setDayInformation(dayLine, tmpDay, -1);
            }
            // A.s("string === " + str);
            g.drawString(str, x, y);
            g.setColor(textColor);
            x += dayDis;
        }
        if (firstDay == 1) {
            yDis = yInit + fontHeight;
            y += fontHeight;
            ++dayLine;
        }
        return y;
    }

    /**
     * 绘制本视图中下一月的数据。
     * 
     * @param g
     *            绘制图形的句柄
     * @param x
     *            起始X坐标
     * @param y
     *            起始Y坐标
     * @param offset
     *            相应的偏移量
     */
    private void drawNextMonthDate(
            Graphics g,
            int x,
            int y,
            int offset) {
        if (!endFlag) {
            return;
        }

        String str;
        int num = 0;
        for (int i = offset; i < 7; ++i, ++num) {
            g.setColor(invalidateColor);
            str = Integer.toString(i - offset + 1);
            g.drawString(str, x + fontWidth, y);
            x += dayDis;
        }
        if (offset == 0) {
            ++dayLine;
            setDayInformation(dayLine, 1, 1);
        }
        y += fontHeight;
        ++num;

        int len = dayLine;
        for (int i = 0; i < 5 - len; ++i) {
            x = xInit;
            ++dayLine;
            for (int j = 0; j < 7; ++j, ++num) {
                g.setColor(invalidateColor);
                int yExt = 0;
                if (num < 10) {
                    yExt = fontWidth;
                }
                str = Integer.toString(num);
                g.drawString(str, x + yExt, y);
                if (j == 0) {
                    setDayInformation(dayLine, num, 1);
                }
                x += dayDis;
            }
        }
    }

    /**
     * 显示选择区域的数据
     * 
     * @param isTmp
     *            目前还不清楚
     */
    public void showSelectedDays(
            boolean isTmp) {
        int selDay[];
        if (startFlag) {
            selDay = group.getSelectedDay(viewYear, viewMonth - 1, true, isTmp);
            if (selDay != null) {
                setSelectedStatus(selDay, -1); // 上一个月的数据
            }
        }

        selDay = group.getSelectedDay(viewYear, viewMonth, true, isTmp);
        if (selDay != null) {
            setSelectedStatus(selDay, 0); // 本月中的数据
        }

        if (endFlag) {
            selDay = group.getSelectedDay(viewYear, viewMonth + 1, true, isTmp);
            if (selDay != null) {
                setSelectedStatus(selDay, 1); // 下一个月的数据
            }
        }
    }

    /**
     * 绘制选择的区域的数据
     * 
     * @param selDay
     *            [] 多选区域的数据
     * @param 数据所处的位置
     */
    private void setSelectedStatus(
            int[] selDay,
            int dir) {
        int len = selDay.length;
        Point pt;
        for (int i = 0; i < len; ++i) {
            pt = getDayPosition(selDay[i], dir);
            if (pt == null) {
                continue;
            }
            selectDate(pt.x, pt.y, false);
        }
    }

    /**
     * 恢复日期选择区域中的数据
     * 
     * @param selDay
     *            [] 需要恢复的区域数据
     * @param flag
     *            -1:上月的数据 0：当月的数据 1：后一个月的数据
     */
    private void restoreDayStatus(
            int[] selDay,
            int flag) {
        int len = selDay.length;
        Point pt;
        for (int i = 0; i < len; ++i) {
            pt = getDayPosition(selDay[i], flag);
            if (pt == null) {
                continue;
            }
            restoreDate(pt.x, pt.y, flag);
        }
    }

    /**
     * 获得给定日期的点的位置
     * 
     * @param day
     *            给定的日期
     * @param flag
     *            -1:本视图中上月的数据 0：本视图中当月的数据 1：本视图中下个月的数据
     */
    public Point getDayPosition(
            int day,
            int flag) {
        int x = xInit;
        int y = yInit;
        int offset;
        --day;
        switch (flag) {
            case -1:
                if (firstDay == 1 && startFlag) {
                    offset = 7;
                } else {
                    offset = firstDay - 1;
                }
                int minDay = lastDays - offset;
                if (day < minDay) {
                    return null;
                }
                day -= minDay;
                x += day % 7 * dayDis;
                break;

            case 0:
                if (startFlag && firstDay == 1) {
                    y += fontHeight;
                }
                offset = 7 - (firstDay - 1);
                if (day < offset) {
                    day += (firstDay - 1);
                } else {
                    day -= offset;
                    y += fontHeight;
                }
                x += day % 7 * dayDis;
                y += day / 7 * fontHeight;
                break;

            case 1:
                if (startFlag && firstDay == 1) {
                    y += fontHeight;
                }
                int lastDay = days;
                lastDay -= (7 - (firstDay - 1));
                y += fontHeight;
                y += lastDay / 7 * fontHeight;
                lastDay %= 7;
                offset = 7 - lastDay;
                if (day < offset) {
                    day += lastDay;
                } else {
                    day -= offset;
                    y += fontHeight;
                }
                x += day % 7 * dayDis;
                y += day / 7 * fontHeight;
                break;

            default:
                return null;
        }
        return new Point(x, y);
    }

    /**
     * 设置某一天的数据信息
     * 
     * @param index
     *            目前还不清楚
     * @param day
     *            给定的日期
     * @param offset
     *            给定的偏移量
     */
    private void setDayInformation(
            int index,
            int day,
            int offset) {
        int year = viewYear;
        int month = viewMonth + offset;
        if (month < 0) {
            month += 12;
            --year;
        } else if (month > 11) {
            month -= 12;
            ++year;
        }
        int pos = index * 3;
        dayInfo[pos] = year;
        dayInfo[pos + 1] = month;
        dayInfo[pos + 2] = day;
    }

    /**
     * 获得该面板中年/月/日的信息
     */
    public int[] getDayInformation() {
        for (int i = 0; i < 18; i += 3) {
            if (dayInfo[i] == 0 && dayInfo[i + 1] == 0 && dayInfo[i + 2] == 0) {
                dayInfo[i] = viewYear;
                dayInfo[i + 1] = viewMonth;
                dayInfo[i + 2] = 1;
            }
        }
        return dayInfo;
    }

    /**
     * 选择日期的数据
     * 
     * @param x
     *            起始X坐标
     * @param y
     *            起始Y坐标
     * @param writeFlag
     */
    private boolean selectDate(
            int x,
            int y,
            boolean writeFlag) {
        int tmpDay = getSelectDate(x, y, writeFlag);
        if (tmpDay == -1) {
            return false;
        }

        boolean isSelectWeek = parent.isSelectWeek();
        if (!isSelectWeek && !setColorFlag) {
            return false;
        }

        boolean flag = refreshFlag;
        int tmpInt = direction;
        if (writeFlag && group.getRemoveStatus() == MonthConstant.ALL_DAY) {
            parent.restoreAllCalendar(false);
        }
        refreshFlag = flag;
        direction = tmpInt;

        if (writeFlag || isSelectWeek) {
            boolean lastDayFlag = false;
            boolean isMultipleWeek = ((Boolean) group.getValue(MonthPropertyConstants.MULTIPLE_WEEK)).booleanValue();
            if (lastFlag && !multipleDayFlag && !isMultipleWeek) {
                lastDayFlag = true;
            }
            parent.writeTemporaryData(viewYear, viewMonth + direction, tmpDay, isSelectWeek, lastDayFlag);
        }
        if (setColorFlag) {
            if (direction != 0) {
                if (refreshFlag) {
                    parent.setChangeYear(viewYear);
                    parent.setChangeMonth(viewMonth + direction);
                    setDateStatus(x, y, tmpDay, selectColor, selTextColor, 0); // 绘制选择区域的内容
                    return false;
                }
            }
            setDateStatus(x, y, tmpDay, selectColor, selTextColor, 0); // 绘制选择区域的内容
        }
        return true;
    }

    /**
     * 恢复没有选择的情况，
     */
    public void restoreAllDay(
            boolean flag,
            boolean isTmp) {
        int selDay[];
        if (startFlag) {
            selDay = group.getSelectedDay(viewYear, viewMonth - 1, flag, isTmp);
            if (selDay != null) {
                restoreDayStatus(selDay, -1);
            }
        }

        selDay = group.getSelectedDay(viewYear, viewMonth, flag, isTmp);
        if (selDay != null) {
            restoreDayStatus(selDay, 0);
        }

        if (endFlag) {
            selDay = group.getSelectedDay(viewYear, viewMonth + 1, flag, isTmp);
            if (selDay != null) {
                restoreDayStatus(selDay, 1);
            }
        }
    }

    private void restoreDate(
            int x,
            int y,
            int flag) {
        int day = getSelectDate(x, y, false);
        if (day == -1) {
            return;
        }
        if (setColorFlag) {
            setDateStatus(x, y, day, backColor, textColor, flag); // 绘制选择区域的内容
        }
    }

    /**
     * 绘制选择区域的内容
     * 
     * @param x
     *            选择区域的X坐标
     * @param y
     *            选择区域的Y坐标
     * @param day
     *            选择日期的数据
     * @param bkColor
     *            选择区域的背景颜色
     * @param strColor
     *            绘制选择日期的字串颜色
     * @param flag
     *            0:本月的数据 其它：不是本月的数据
     */
    private void setDateStatus(
            int x,
            int y,
            int prmDay,
            Color bkColor,
            Color strColor,
            int flag) {
        int col = (x - xInit) / dayDis;
        int row = (y - yInit) / fontHeight;
        Graphics g = graphics;
        if (g == null) {
            g = getGraphics();
        }
        if (g == null) {
            return;
        }
        g.setFont(dayFont);
        g.setColor(bkColor);
        x = col * dayDis + xInit - xOffset;
        y = row * fontHeight + yInit + yOffset + 1;
        g.fillRect(x, y, dayDis, fontHeight + 1);

        if (flag == 0) {
            g.setColor(strColor);
        } else {
            g.setColor(invalidateColor);
        }
        y += (yInit - (yOffset + 1));
        String str = Integer.toString(prmDay);
        x += xOffset;
        if (prmDay < 10) {
            x += fontWidth;
        }
        g.drawString(str, x, y);

        if (nowYear == viewYear && nowMonth == viewMonth) {
            row = (nowDate + (firstDay - 1) - 1) / 7;
            col = (nowDate + (firstDay - 1) - 1) % 7;
            x = col * dayDis + xInit - xOffset;
            y = row * fontHeight + yInit + yDis - fontHeight;
            g.setColor(rectColor);
            g.drawRect(x, y + yOffset, dayDis, fontHeight + 1); // 以红色绘制系统时间
        }
    }

    /**
     * 根据坐标获得选择的确定日期
     * 
     * @param x
     *            初始X坐标
     * @param y
     *            初始Y坐标
     * @param writeFlag
     *            目前还不能确定该标志
     * @return 选择的日期
     */
    public int getSelectDate(
            int x,
            int y,
            boolean writeFlag) {
        direction = 0;
        refreshFlag = false;
        setColorFlag = true;
        int yStart = yDis;
        if (startFlag && firstDay == 1) {
            yStart -= fontHeight;
        }
        if ((x < xInit) || (x > xInit + 7 * dayDis) || (y < yStart) || (y >= 7 * fontHeight)) {
            return -1;
        }

        int col = (x - xInit) / dayDis;
        int row = (y - yDis) / fontHeight;
        if (y < yDis) {
            row = -1;
        }
        int day = row * 7 + col + 1;
        if ((!startFlag && day < firstDay) || (day > days + firstDay - 1 && !endFlag)) {
            setColorFlag = false;
        }

        int data = days + firstDay - 1;
        if (day <= data) {
            day -= (firstDay - 1);
            if (day <= 0) {
                direction = -1;
                boolean weekFlag = parent.isSelectWeek();
                if ((writeFlag && !weekFlag) || (weekFlag && col == maxDay)) {
                    refreshFlag = true;
                }
                day += lastDays;
            }
        } else {
            day -= data;
            direction = 1;
            int offset = (7 - (data % 7)) % 7;
            boolean weekFlag = parent.isSelectWeek();
            if ((writeFlag && !weekFlag)) {
                refreshFlag = true;
            }
            if (weekFlag) {
                if (((Boolean) group.getValue(MonthPropertyConstants.WORK_WEEK_MODEL)).booleanValue() && col == maxDay) {
                    refreshFlag = true;
                } else if (day - offset > maxDay) {
                    refreshFlag = true;
                }
            }
        }
        return day;
    }

    /**
     * 获得当前月的初始化数据
     */
    public void getMonthStatus() {
        dayLine = 0;
        monthLine = 0;
        int actYear = viewYear;
        int actMonth = viewMonth;
        lastDays = group.getDays(actYear, actMonth - 1);

        firstWeekDay = ((Integer) group.getValue(MonthPropertyConstants.FIRST_DAY)).intValue();
        calDate.set(viewYear, viewMonth, 1);

        firstDay = calDate.get(Calendar.DAY_OF_WEEK);

        firstDay -= (firstWeekDay + 1);

        if (firstDay < 0) {
            firstDay += 7;
        }
        ++firstDay;
        days = calDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayDis = 3 * fontWidth;
        xOffset = fontWidth / 2;
        yOffset = fontHeight / 6;

        xInit = fontWidth / 2;
        yInit = fontHeight;
        yDis = yInit;

        for (int i = 0; i < 18; ++i) {
            dayInfo[i] = 0;
        }
    }

    public int getDirection() {
        return direction;
    }

    /**
     * 获得当前视图中的行数
     */
    public int getRows() {
        return dayLine;
    }

    /**
     * 处理选择一月的数据
     */
    public void selectWeek(
            int line,
            boolean flag) {
        if (flag) {
            group.setRemoveStatus(MonthConstant.ALL_DAY);
        } else {
            group.setRemoveStatus(MonthConstant.NO_WEEK_DAY);
        }
        parent.restoreAllCalendar(false);

        int x = xInit;
        int y = line * fontHeight + yInit;
        parent.setSelectWeek(true);

        boolean tmpFlag = false;
        for (int i = 0; i < 7; ++i) {
            lastFlag = false;
            if (!tmpFlag) {
                lastFlag = true;
                tmpFlag = true;
            }
            if (!selectDate(x, y, false)) {
                break;
            }
            x += dayDis;
        }
        group.viewSelectedStatus(-1, true);
    }

    /**
     * 选择多天的数据
     * 
     * @param x
     *            起始X坐标
     * @param y
     *            起始Y坐标
     * @param isDrag
     *            true: 是鼠标拖动 false:不是鼠标拖动
     */
    public void selectMultipleDays(
            int x,
            int y,
            boolean isDrag) {
        int day[];
        if (isDrag) {
            day = group.getTmpLastDay();
        } else {
            group.setRemoveStatus(MonthConstant.ALL_DAY);
            parent.restoreAllCalendar(false);
            day = group.getLastDay();
        }
        Point pt;
        boolean dirFlag = true;
        int dayIndex = group.getContainerIndex(day[0], day[1]);
        int monthDis = (viewYear - day[0]) * 12 + (viewMonth - day[1]);

        int count = MonthConstant.MAX_DAY_COUNT;
        int nowDay = getSelectDate(x, y, false);
        if (monthDis > 0) {
            dirFlag = true;
            if (monthDis == 1) {
                count = group.getDays(day[0], day[1]) - day[2] + 1 + nowDay;
            }
        } else if (monthDis < 0) {
            dirFlag = false;
            if (monthDis == -1) {
                count = days - nowDay + 1 + day[2];
            }
        } else {
            pt = getDayPosition(day[2], 0);
            if (pt == null) {
                return;
            }
            pt.x = (pt.x - xInit) / dayDis;
            pt.y = (pt.y - yInit) / fontHeight;
            int tmpX = (x - xInit) / dayDis;
            int tmpY = (y - yInit) / fontHeight;
            if (tmpY > pt.y || (tmpY == pt.y && tmpX >= pt.x)) {
                dirFlag = true;
                count = (tmpY - pt.y) * 7;
                count += (tmpX - pt.x) + 1;
            } else {
                dirFlag = false;
                count = (pt.y - tmpY) * 7;
                count += (pt.x - tmpX) + 1;
            }
        }

        boolean isSingle = ((Boolean) group.getValue(MonthPropertyConstants.SINGLE_MODEL)).booleanValue();
        boolean isDragable = ((Boolean) group.getValue(MonthPropertyConstants.DRAGABLE)).booleanValue();
        if (isSingle && isDragable) {
            selectDayLength(dayIndex, monthDis, day, count, dirFlag);
            return;
        }

        int dayCount = group.getDayCount();
        int status = group.getRemoveStatus();
        if (isDrag && count > MonthConstant.MAX_DAY_MODEL && status != MonthConstant.NONE_DAY) {
            group.setSelectedModel(MonthConstant.WEEK_MODEL);
            pt = parent.getLocationOnScreen();
            group.setMultipleWeek(pt.x + x, pt.y + y, isDrag);
        } else {
            group.setSelectedModel(MonthConstant.DAY_MODEL);
            if (status != MonthConstant.NONE_DAY) {
                dayCount = 0;
            } else {
                dayCount -= getDuplicateDays(dayIndex, monthDis, day, count, dirFlag);
            }
            if (count + dayCount > MonthConstant.MAX_DAY_COUNT) {
                count = MonthConstant.MAX_DAY_COUNT - dayCount;
            }
            selectDayLength(dayIndex, monthDis, day, count, dirFlag);
        }
    }

    private int getDuplicateDays(
            int dayIndex,
            int dis,
            int[] day,
            int len,
            boolean flag) {
        int thisMonthDays = group.getDays(day[0], day[1]);
        int lastMonthDays = group.getDays(day[0], day[1] - 1);
        int month = day[1];
        int status = group.getRemoveStatus();

        int dayOffset = 0;
        if (flag) {
            for (int i = 0; i < len; ++i) {
                if (day[2] + i <= thisMonthDays) {
                    if (group.isDuplication(day[0], day[1], day[2] + i, false)) {
                        if (status == MonthConstant.NONE_DAY) {
                            ++dayOffset;
                        }
                    }
                } else {
                    month = day[1] + 1;
                    if (group.isDuplication(day[0], month, day[2] + i - thisMonthDays, false)) {
                        if (status == MonthConstant.NONE_DAY) {
                            ++dayOffset;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < len; ++i) {
                if (day[2] - i >= 1) {
                    if (group.isDuplication(day[0], day[1], day[2] - i, false)) {
                        if (status == MonthConstant.NONE_DAY) {
                            ++dayOffset;
                        }
                    }
                } else {
                    month = day[1] - 1;
                    if (group.isDuplication(day[0], month, day[2] - i + lastMonthDays, false)) {
                        if (status == MonthConstant.NONE_DAY) {
                            ++dayOffset;
                        }
                    }
                }
            }
        }
        return dayOffset;
    }

    private void selectDayLength(
            int dayIndex,
            int dis,
            int[] day,
            int len,
            boolean flag) {
        int thisMonthDays = group.getDays(day[0], day[1]);
        int lastMonthDays = group.getDays(day[0], day[1] - 1);
        int month = day[1];
        if (flag) {
            for (int i = 0; i < len; ++i) {
                if (day[2] + i <= thisMonthDays) {
                    parent.writeTemporaryData(day[0], day[1], day[2] + i, false, false);
                } else {
                    month = day[1] + 1;
                    parent.writeTemporaryData(day[0], month, day[2] + i - thisMonthDays, false, false);
                }
            }
        } else {
            for (int i = 0; i < len; ++i) {
                if (day[2] - i >= 1) {
                    parent.writeTemporaryData(day[0], day[1], day[2] - i, false, false);
                } else {
                    month = day[1] - 1;
                    parent.writeTemporaryData(day[0], month, day[2] - i + lastMonthDays, false, false);
                }
            }
        }
        parent.adjustContainer(dayIndex, dis, day, true);
        if (group.getRemoveStatus() == MonthConstant.NONE_DAY) {
            group.viewSelectedStatus(-1, false);
        }
    }

    /**
     * 设置新的月份作为显示视图
     */
    public void setNewMonth(
            int year,
            int month) {
        viewYear = year;
        viewMonth = month;
    }

    /**
     * 选择整月的数据变化
     */
    public void selectMonthModel() {
        if (((Boolean) group.getValue(MonthPropertyConstants.SINGLE_MODEL)).booleanValue()) {
            return;
        }
        int lastDay[] = group.getLastDay();
        if (group.getSelectedModel() == MonthConstant.MONTH_MODEL && lastDay[0] == viewYear && lastDay[1] == viewMonth) {
            return;
        }
        group.setSelectedModel(MonthConstant.MONTH_MODEL); // 切换为整月模式，
        group.setRemoveStatus(MonthConstant.ALL_DAY);
        parent.restoreAllCalendar(false);
        int start = 0;
        if (startFlag && firstDay == 1) {
            start = 1;
        }
        int count = monthLine + 1;
        int[] old = group.getLastDay();
        for (int i = start; i < count; ++i) {
            selectWeek(i, true);
        }
        group.setLastDay(old[3], old[0], old[1], old[2]);
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
        parent.setInContainer(true);
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
        parent.setInContainer(false);
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
        if (!javax.swing.SwingUtilities.isLeftMouseButton(e)) // 不是左键则退出.
        {
            return;
        }

        group.setMultipleStatus(true);
        int tmpX = e.getX();
        int tmpY = e.getY();

        if (tmpX >= xInit && tmpX <= xInit + 7 * dayDis && tmpY <= yInit) // 鼠标在这个区域中则选则整个月的所有日期.
        {
            selectMonthModel();
        } else {
            group.setSelectedModel(MonthConstant.DAY_MODEL); //
            parent.setSelectWeek(false);
            group.setValue(MonthPropertyConstants.MULTIPLE_WEEK, Boolean.FALSE);
            multipleDayFlag = false;
            group.setRemoveStatus(MonthConstant.ALL_DAY);
            if (!(((Boolean) group.getValue(MonthPropertyConstants.SINGLE_MODEL)).booleanValue())) {
                if (e.isShiftDown()) // Shift+MouseClick
                {
                    multipleDayFlag = true;
                    selectMultipleDays(tmpX, tmpY, false); // 多选区域
                    return;
                } else if (e.isControlDown()) // Ctrl+MouserClick
                {
                    int count = group.getDayCount();
                    if (count >= MonthConstant.MAX_DAY_COUNT) {
                        return;
                    }
                    group.setRemoveStatus(MonthConstant.NONE_DAY);
                    group.selectWorkWeekModel(false);
                    group.changeWeekToDay();
                }
            }
            lastFlag = true;
            selectDate(tmpX, tmpY, true);
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        parent.performSelect(e.getX(), e.getY());
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will
     * continue to be delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered
     * during a native Drag&Drop operation.
     * 
     * @param evt
     *            鼠标事件源
     */
    public void mouseDragged(
            MouseEvent evt) {
        parent.performMouseDragged(evt, false);
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseMoved(
            MouseEvent e) {
        Point p = e.getPoint();
        int x = p.x;
        int y = p.y;
        // setToolTipText("x : Y\n"+x+" : "+y);
        int days = getSelectDate(x, y, false);
        // A.s("days : "+days);
        if (days >= 0 && days <= 31) {
            int month = viewMonth;
            int tmpYear = viewYear;
            if (y > totalHeight / 2 && days < 10) {
                if (month == 11) {
                    tmpYear++;
                    month = 0;
                } else {
                    month++;
                }
            } else if (y < totalHeight / 2 && days > 20) {
                if (month == 0) {
                    tmpYear--;
                    month = 11;
                } else {
                    month--;
                }
            }
            // CalendarUtility.
            // A.s("year : "+viewYear+"; month : "+month+"; days :"+days);
            // Date date = new Date(viewYear, month, days);
            month++;
            String tip = getTipString(tmpYear, month, days);
            setToolTipText(tip);
        }
    }

    /**
     * tip的结果形式如下： "公历：公元2004年6月5日\n农历：2004甲申【猴】年四月十八\n 甲申年 乙卯日 庚午月 \n[芒种 ][世界环境日]" []中的内容为可选项，有则显示，无则空白
     */
    private String getTipString(
            int prmYear,
            int prmMonth,
            int prmDate) {
        // 初始化提示信息字串，下面一段后tip的结果形式如下：
        // "公历：公元2004年6月5日\n农历：2004
        String tip =
                MonthConstant.GONG_LI + prmYear + MonthConstant.YEAR + prmMonth + MonthConstant.MONTH + prmDate
                        + MonthConstant.DATE + '\n' + MonthConstant.NONG_LI + prmYear;
        // 下面的操作将在tip后加入形式如下内容：甲申【猴】年四月十八\n甲申年
        int gzIndex = CalendarUtility.ganZhiOfYear(prmYear, prmMonth, prmDate, 12);
        int gIndex = CalendarUtility.gan(gzIndex);
        int zIndex = CalendarUtility.zhi(gzIndex);
        // 当鼠标游动到边缘的地方，得到的日期为空，此时不显示tooltip
        if (gIndex < 0 || zIndex < 0) {
            return "";
        }
        String ganZhiStr =
                (MonthConstant.TIAN_GAN.substring(gIndex, gIndex + 1)).concat(MonthConstant.DI_ZHI.substring(zIndex,
                        zIndex + 1));
        tip =
                tip + ganZhiStr + MonthConstant.BRACKET1 + MonthConstant.SHENG_XIAO.substring(zIndex, zIndex + 1)
                        + MonthConstant.BRACKET2 + MonthConstant.YEAR;
        int lunMonIndex = CalendarUtility.lunMon(prmYear, prmMonth, prmDate);
        if (lunMonIndex == 12 || lunMonIndex == -12) {
            lunMonIndex = 0;
        }
        String lunMonStr = CASUtility.EMPTYSTR;
        if (lunMonIndex < 0) {
            lunMonStr = MonthConstant.EMBOLISM;
            lunMonIndex = Math.abs(lunMonIndex);
        }
        lunMonStr = lunMonStr.concat(MonthConstant.NONG_LI_MONTH[lunMonIndex]);
        tip = tip + lunMonStr;
        int lunDateIndex = CalendarUtility.getLunDateIndex(prmYear, prmMonth, prmDate);
        tip += MonthConstant.NONG_LI_DATE[lunDateIndex] + '\n' + ganZhiStr + MonthConstant.YEAR + ' ';
        // 下面的操作将在tip后加入形式如下内容：
        // 乙卯日 庚午月 \n[芒种 ][世界环境日]"
        gzIndex = CalendarUtility.ganZhiOfMonth(prmYear, prmMonth, prmDate, 12);
        gIndex = CalendarUtility.gan(gzIndex);
        zIndex = CalendarUtility.zhi(gzIndex);
        ganZhiStr =
                (MonthConstant.TIAN_GAN.substring(gIndex, gIndex + 1)).concat(MonthConstant.DI_ZHI.substring(zIndex,
                        zIndex + 1));
        tip += ganZhiStr + MonthConstant.MONTH + ' ';
        gzIndex = CalendarUtility.ganZhiOfDay(prmYear, prmMonth, prmDate, 12);
        gIndex = CalendarUtility.gan(gzIndex);
        zIndex = CalendarUtility.zhi(gzIndex);
        ganZhiStr =
                (MonthConstant.TIAN_GAN.substring(gIndex, gIndex + 1)).concat(MonthConstant.DI_ZHI.substring(zIndex,
                        zIndex + 1));
        tip += ganZhiStr + MonthConstant.DATE + '\n';

        int constelationIndex = CalendarUtility.constellation(prmMonth, prmDate);
        if (constelationIndex != -1) {
            tip += MonthConstant.CONSTELLATION[constelationIndex] + ' ';
        }

        int solarTermIndex = CalendarUtility.getSolarTermIndex(prmYear, prmMonth, prmDate);
        if (solarTermIndex != -1) {
            tip += MonthConstant.SOLAR_TERM[solarTermIndex] + ' ';
        }
        // 取得传统节日的索引，以日期的数字变为字串所得
        String feastKey = CASUtility.EMPTYSTR;
        // 农历节日
        if (lunMonIndex < 9) {
            feastKey += 0;
        }
        feastKey += lunMonIndex + 1;
        if (lunDateIndex < 9) {
            feastKey += 0;
        }
        feastKey += lunDateIndex + 1;
        int len = MonthConstant.TRADITIONAL_FEAST.length;
        for (int i = len - 2; i >= 0; i--) {
            if (feastKey.equals(MonthConstant.TRADITIONAL_FEAST[i])) {
                tip += MonthConstant.TRADITIONAL_FEAST[i + 1];
                break;
            }
        }
        // 公历节日
        feastKey = CASUtility.EMPTYSTR;
        if (prmMonth < 10) {
            feastKey += 0;
        }
        feastKey += prmMonth;
        if (prmDate < 10) {
            feastKey += 0;
        }
        feastKey += prmDate;
        len = MonthConstant.INTER_FEAST.length;
        for (int i = len - 2; i >= 0; i--) {
            if (feastKey.equals(MonthConstant.INTER_FEAST[i])) {
                tip = tip + ' ' + MonthConstant.INTER_FEAST[i + 1];
                break;
            }
        }
        return tip;

        // tip = "公历："+prmYear+"年"+prmMonth+"月"+days+"日\n"
        // + "农历："+prmYear+CalendarUtility.getGanZhiYear(prmYear, prmMonth, prmDate)
        // + '【'+CalendarUtility.getShengXiaoStr(prmYear, prmMonth, prmDate)+'】'
        // + "年"+lunMonStr
        // + CalendarUtility.getLunarDate(prmYear, prmMonth, prmDate)+'\n'
        // + CalendarUtility.getGanZhiMonth(prmYear, prmMonth, prmDate)+"月"
        // + CalendarUtility.getGanZhiDate(prmYear, prmMonth, prmDate)+"日"+'\n'
        // + "节气"+CalendarUtility.getSolarTerm(prmYear, prmMonth, prmDate);
    }

    /**
     * 重载父类的方法，创建PIM自己的tooltip，原因时缺省的tooltip不能换行绘制
     * 
     * @return JToolTip 提示信息
     * @see PimToolTip PIM定义的可以实现换行绘制的提示框
     */
    public JToolTip createToolTip() {
        PimToolTip tip = new PimToolTip();
        tip.setComponent(this);
        return tip;
    }

    private EMonthContainer parent;
    private PIMMonthGroup group;

    private int fontWidth, fontHeight;
    private int dayWidth, totalWidth, totalHeight;

    private Color backColor = CustOpts.custOps.getContentBG();
    private Color textColor = Color.black;
    private Color rectColor = Color.red;
    private Color selectColor = Color.lightGray;
    private Color selTextColor = Color.white;
    private Color invalidateColor = Color.lightGray;

    private Graphics graphics;
    private int nowYear, nowMonth, nowDate, viewYear, viewMonth;
    private Font dayFont;

    private int firstDay, firstWeekDay;
    private int days, lastDays;
    private int dayLine, monthLine;
    private int xInit, yInit, yDis, dayDis;
    private int xOffset, yOffset;
    private int[] dayInfo;
    private int direction;
    private int maxDay = 6;

    private boolean refreshFlag;
    private boolean setColorFlag = true;
    private boolean lastFlag;
    private boolean startFlag, endFlag;

    private boolean multipleDayFlag;
    private Calendar calDate = Calendar.getInstance();
}
