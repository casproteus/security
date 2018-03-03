package org.cas.client.platform.casbeans.calendar;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;

import javax.swing.JComponent;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.MonthConstant;

/**
 * 该类负责绘制日历选择区中周数据的面板。
 */

class EWeekCanvas extends JComponent implements MouseListener, MouseMotionListener {
    /**
     * 构造器
     * 
     * @param container
     *            面板的容器
     */
    public EWeekCanvas(EMonthContainer container) {
        parent = container;
        initData(); // 初始化数据
        /**
         * 添加鼠标监听器
         */
        addMouseListener(this);
        // 添加鼠标动作的监听器
        addMouseMotionListener(this);
    }

    /**
     * 该方法初始化需要的数据
     */
    private void initData() {
        group = parent.getContainerManager();

        weekFont = CustOpts.custOps.getFontOfWeek();
        fontWidth = getFontMetrics(CustOpts.custOps.getFontOfDay()).stringWidth("9");
        fontHeight = getFontMetrics(CustOpts.custOps.getFontOfDay()).getHeight();
        weekFontWidth = getFontMetrics(CustOpts.custOps.getFontOfWeek()).stringWidth("9");
        weekFontHeight = getFontMetrics(CustOpts.custOps.getFontOfWeek()).getHeight();
        dayCanvas = parent.getDayCanvas();

        viewYear = parent.getYear();
        viewMonth = parent.getMonth();

        cursor = parent.getCursor();
    }

    /**
     * 绘制组件的内容
     * 
     * @param g
     *            绘制图形的句柄
     */
    public void paintComponent(
            Graphics g) {
        Dimension d = getSize();
        width = d.width;
        height = d.height;
        g.setColor(backColor);
        g.fillRect(0, 0, width, height);
        line = dayCanvas.getRows() + 1;
        if (!(((Boolean) group.getValue(MonthPropertyConstants.WEEK_NUMBER_SHOW)).booleanValue())) {
            return;
        }

        int x = width - 1 - 2 * weekFontWidth;
        int y = fontHeight + 1;
        g.setColor(textColor);
        g.drawLine(width - 1, y, width - 1, y + line * fontHeight);
        int weekModel = ((Integer) group.getValue(MonthPropertyConstants.FIRST_WEEK)).intValue();

        g.setFont(weekFont);
        int dayInfo[] = dayCanvas.getDayInformation();
        int year, month, date, pos;
        int weekNum;
        String str;
        y = 2 * fontHeight;
        for (int i = 0; i < line; ++i) {
            pos = i * 3;
            year = dayInfo[pos];
            month = dayInfo[pos + 1];
            date = dayInfo[pos + 2];
            calDate.set(year, month, date);
            calDate.setFirstDayOfWeek(Calendar.SUNDAY
                    + ((Integer) group.getValue(MonthPropertyConstants.FIRST_DAY)).intValue());
            setStartWeek(weekModel);
            weekNum = calDate.get(Calendar.WEEK_OF_YEAR);
            str = Integer.toString(weekNum);
            if (weekNum < 10) {
                g.drawString(str, x + weekFontWidth, y);
            } else {
                g.drawString(str, x, y);
            }
            y += fontHeight;
        }
    }

    /**
     * 该方法设置开始的周的模式
     * 
     * @param weekModel
     *            新的周数据的模式
     */
    private void setStartWeek(
            int weekModel) {
        switch (weekModel) {
            case CustOpts.WEEK_FOUR_DAY:
                calDate.setMinimalDaysInFirstWeek(4);
                break;

            case CustOpts.WEEK_FULL_DAY:
                calDate.setMinimalDaysInFirstWeek(7);
                break;

            case CustOpts.WEEK_ONE_DAY:
            default:
                calDate.setMinimalDaysInFirstWeek(1);
                break;
        }
    }

    /**
     * 该方法提供多选周的操作
     * 
     * @param x
     *            开始的X坐标
     * @param y
     *            开始的Y坐标
     * @param isDrag
     *            true:拖动的操作 false:正常的操作
     */
    public void selectMultipleWeeks(
            int x,
            int y,
            boolean isDrag) {
        int day[];
        if (isDrag) {
            day = group.getTmpLastDay();
        } else {
            day = group.getLastDay();
            group.setRemoveStatus(MonthConstant.ALL_DAY);
            parent.restoreAllCalendar(false);
        }
        Point pt;
        boolean dirFlag = true;
        int dayIndex = group.getContainerIndex(day[0], day[1]);
        int monthDis = (viewYear - day[0]) * 12 + (viewMonth - day[1]);

        int firstWeekDay = ((Integer) group.getValue(MonthPropertyConstants.FIRST_DAY)).intValue();
        calDate.set(day[0], day[1], day[2]);
        calDate.setFirstDayOfWeek(Calendar.SUNDAY + firstWeekDay);
        int lastWeek = calDate.get(Calendar.WEEK_OF_YEAR);
        calDate.set(day[0], 11, 31);
        calDate.setFirstDayOfWeek(Calendar.SUNDAY + firstWeekDay);
        int maxLastWeek = calDate.get(Calendar.WEEK_OF_YEAR);
        int nowDay = dayCanvas.getSelectDate(x, y, false);
        int dis = dayCanvas.getDirection();
        calDate.set(viewYear, viewMonth + dis, nowDay);
        calDate.setFirstDayOfWeek(Calendar.SUNDAY + firstWeekDay);
        int nowWeek = calDate.get(Calendar.WEEK_OF_YEAR);

        int count = MonthConstant.MAX_WEEK_COUNT;
        if (monthDis > 0) {
            dirFlag = true;
            if (monthDis <= 2) {
                if (viewYear == day[0]) {
                    count = nowWeek - lastWeek + 1;
                } else {
                    if (dis < 0) {
                        nowWeek = 0;
                    }
                    count = maxLastWeek - lastWeek + nowWeek + 1;
                }
            }
        } else if (monthDis < 0) {
            dirFlag = false;
            if (monthDis >= -2) {
                if (viewYear == day[0]) {
                    count = lastWeek - nowWeek + 1;
                } else {
                    count = maxLastWeek - lastWeek + nowWeek + 1;
                }
            }
        } else {
            pt = dayCanvas.getDayPosition(day[2], 0);
            if (pt == null) {
                return;
            }
            int srcLine = pt.y / fontHeight - 1;
            int nowLine = y / fontHeight - 1;
            if (nowLine >= srcLine) {
                dirFlag = true;
                count = nowLine - srcLine + 1;
            } else {
                dirFlag = false;
                count = srcLine - nowLine + 1;
            }
        }
        int weekCount = group.getWeekCount();
        if (group.getRemoveStatus() == MonthConstant.ALL_DAY) {
            weekCount = 0;
        } else {
            weekCount -= getDuplicateWeeks(dayIndex, monthDis, day, count, dirFlag);
        }
        if (count + weekCount > MonthConstant.MAX_WEEK_COUNT) {
            count = MonthConstant.MAX_WEEK_COUNT - weekCount;
        }
        selectWeekLength(dayIndex, monthDis, day, count, dirFlag);
    }

    private int getDuplicateWeeks(
            int dayIndex,
            int dis,
            int day[],
            int len,
            boolean isForward) {
        calDate.set(day[0], day[1], day[2]);
        int firstWeekDay = ((Integer) group.getValue(MonthPropertyConstants.FIRST_DAY)).intValue();
        int offset = calDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY - firstWeekDay;
        int lastDays = group.getDays(day[0], day[1] - 1);
        int theDays = group.getDays(day[0], day[1]);
        int month = day[1];
        int date;

        int weekOffset = 0;
        if (isForward) {
            date = day[2] - offset;
            for (int i = 0; i < len; ++i) {
                for (int j = 0; j < 7; ++j, ++date) {
                    if (date < 1) {
                        --month;
                        date += lastDays;
                        theDays = lastDays;
                        lastDays = group.getDays(day[0], month - 1);
                    } else if (date > theDays) {
                        ++month;
                        date -= theDays;
                        lastDays = theDays;
                        theDays = group.getDays(day[0], month);
                    }
                    if (j == 0) {
                        if (group.isDuplication(day[0], month, date, true)) {
                            ++weekOffset;
                        }
                    }
                }
            }
        } else {
            date = day[2] + (6 - offset);
            for (int i = 0; i < len; ++i) {
                for (int j = 6; j >= 0; --j, --date) {
                    if (date < 1) {
                        --month;
                        date += lastDays;
                        theDays = lastDays;
                        lastDays = group.getDays(day[0], month - 1);
                    } else if (date > theDays) {
                        ++month;
                        date -= theDays;
                        lastDays = theDays;
                        theDays = group.getDays(day[0], month);
                    }
                    if (j == 6) {
                        if (group.isDuplication(day[0], month, date, true)) {
                            ++weekOffset;
                        }
                    }
                }
            }
        }
        return weekOffset;
    }

    private void selectWeekLength(
            int dayIndex,
            int dis,
            int day[],
            int len,
            boolean isForward) {
        calDate.set(day[0], day[1], day[2]);
        int firstWeekDay = ((Integer) group.getValue(MonthPropertyConstants.FIRST_DAY)).intValue();
        int offset = calDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY - firstWeekDay;

        int lastDays = group.getDays(day[0], day[1] - 1);
        int theDays = group.getDays(day[0], day[1]);

        int month = day[1];
        int date;
        if (isForward) {
            date = day[2] - offset;
            for (int i = 0; i < len; ++i) {
                for (int j = 0; j < 7; ++j, ++date) {
                    if (date < 1) {
                        --month;
                        date += lastDays;
                        theDays = lastDays;
                        lastDays = group.getDays(day[0], month - 1);
                    } else if (date > theDays) {
                        ++month;
                        date -= theDays;
                        lastDays = theDays;
                        theDays = group.getDays(day[0], month);
                    }
                    parent.writeTemporaryData(day[0], month, date, true, false);
                }
            }
        } else {
            date = day[2] + (6 - offset);
            for (int i = 0; i < len; ++i) {
                for (int j = 6; j >= 0; --j, --date) {
                    if (date < 1) {
                        --month;
                        date += lastDays;
                        theDays = lastDays;
                        lastDays = group.getDays(day[0], month - 1);
                    } else if (date > theDays) {
                        ++month;
                        date -= theDays;
                        lastDays = theDays;
                        theDays = group.getDays(day[0], month);
                    }
                    parent.writeTemporaryData(day[0], month, date, true, false);
                }
            }
        }
        parent.adjustContainer(dayIndex, dis, day, true);
        if (group.getRemoveStatus() != MonthConstant.ALL_DAY) {
            group.viewSelectedStatus(-1, false);
        }
    }

    /**
     * 该方法设置新的月份
     * 
     * @param year
     *            新的年份
     * @param month
     *            新的月份
     */
    public void setNewMonth(
            int year,
            int month) {
        viewYear = year;
        viewMonth = month;
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
     * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will
     * continue to be delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered
     * during a native Drag&Drop operation.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseDragged(
            MouseEvent e) {
        parent.performMouseDragged(e, true);
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
        parent.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        parent.setCursor(cursor);
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseMoved(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param evt
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent evt) {
        if (!javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
            return;
        }
        if (line == 0 || ((Boolean) group.getValue(MonthPropertyConstants.SINGLE_MODEL)).booleanValue()) {
            return;
        }

        group.setSelectedModel(MonthConstant.WEEK_MODEL);
        group.setMultipleStatus(true);
        int x = evt.getX();
        int y = evt.getY();
        int yDis = fontHeight;
        line = dayCanvas.getRows() + 1;
        if (y <= yDis || y > line * fontHeight + yDis) {
            return;
        }
        int pos = (y - yDis) / fontHeight;
        group.selectWorkWeekModel(false);

        boolean clrFlag = false;
        group.setValue(MonthPropertyConstants.MULTIPLE_WEEK, Boolean.FALSE);
        if (!evt.isControlDown() && !evt.isShiftDown()) {
            clrFlag = true;
        } else {
            if (evt.isShiftDown()) {
                group.setValue(MonthPropertyConstants.MULTIPLE_WEEK, Boolean.TRUE);
                selectMultipleWeeks(x, y, false);
                return;
            }
            int weekCount = group.getWeekCount();
            weekCount += group.getTmpWeekCount();
            if (weekCount >= MonthConstant.MAX_WEEK_COUNT) {
                return;
            }
        }
        dayCanvas.selectWeek(pos, clrFlag);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param evt
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent evt) {
        parent.performSelect(evt.getX(), evt.getY());
    }

    private EMonthContainer parent;
    private PIMMonthGroup group;
    private ECalendarCanvas dayCanvas;

    private Color backColor = CustOpts.custOps.getContentBG(); // new Color(255, 239, 206);
    private Color textColor = Color.black;

    private Font weekFont;
    private int viewYear, viewMonth;
    private int fontWidth, fontHeight, weekFontWidth, weekFontHeight;
    private int width, height;
    private int line;

    private Cursor cursor;
    private Calendar calDate = Calendar.getInstance();
}
