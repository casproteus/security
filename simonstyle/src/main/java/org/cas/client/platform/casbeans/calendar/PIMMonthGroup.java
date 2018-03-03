package org.cas.client.platform.casbeans.calendar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.EDate;
import org.cas.client.platform.casutil.EDaySet;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.resource.international.MonthConstant;

/**
 * 该类用来管理日期选择区中日期部分的相关信息。
 */

class PIMMonthGroup extends JComponent implements LayoutManager2 {
    public static final String DATE_PROPERTY = "Date";
    /** the event of selected date */

    public static final String DUP_PROPERTY = "Duplicate";

    /** selected dates are same as old */

    /**
     * 构造器
     */
    public PIMMonthGroup() {
        changeSupport = new PropertyChangeSupport(this);
        initDateHouse();
        dayWidth = getMonthSelectWidth();
        dayHeight = getMonthSelectHeight();
        lastYear = calendar.get(Calendar.YEAR);
        lastMonth = calendar.get(Calendar.MONTH);
        lastDate = calendar.get(Calendar.DATE);
        setLayout(this);
    }

    /**
     * 初始化日期相关的属性集
     */
    private void initDateHouse() {
        normalDayHash = new Hashtable();
        weekDayHash = new Hashtable();

        dayHash = new Hashtable();
        weekHash = new Hashtable();

        oldDayHash = new Hashtable();
        oldWeekHash = new Hashtable();

        dayEqualHash = new Hashtable();
        weekEqualHash = new Hashtable();

        // TODO:这些属性集全部移到CustOption中去。
        propertyHash = new Hashtable(); // 存放月历的属性集
        propertyHash.put(MonthPropertyConstants.WEEK_NUMBER_SHOW, Boolean.FALSE);
        propertyHash.put(MonthPropertyConstants.FIRST_DAY, PIMPool.pool.getKey(0));
        propertyHash.put(MonthPropertyConstants.FIRST_WEEK, PIMPool.pool.getKey(CustOpts.WEEK_ONE_DAY));

        propertyHash.put(MonthPropertyConstants.SINGLE_MODEL, Boolean.FALSE);
        propertyHash.put(MonthPropertyConstants.WORK_WEEK_MODEL, Boolean.FALSE);
        propertyHash.put(MonthPropertyConstants.MULTIPLE_WEEK, Boolean.FALSE);
        propertyHash.put(MonthPropertyConstants.DRAGABLE, Boolean.TRUE);
    }

    /** 布局该面板的内容 */
    private void layoutPane() {
        paneWidth = getWidth();
        paneHeight = getHeight();
        removeAll();
        // @NOTE:此处定义"最小误差为10",即是说,如果容器放MonthGroup有空余空间时,如果空余空间超过10个象素,则多加一个MonthGroup面板.
        // 如果不到10个象素,即使增加一个面板的话因为不容易被看到,容易被用户忽略,顾不如不加,反正10个象素留白不至于影响美观.
        xCount = paneWidth % dayWidth > 10 ? paneWidth / dayWidth + 1 : paneWidth / dayWidth; // 横向显示的日期面板的数目
        yCount = paneHeight % dayHeight > 10 ? paneHeight / dayHeight + 1 : paneHeight / dayHeight; // 纵向显示的日期面板的数目
        maxCount = xCount * yCount;
        if (maxCount == 0) {
            return;
        }

        int year = lastYear;
        int month = lastMonth;
        if (monthContainerAry != null) {
            year = monthContainerAry[0].getYear();
            month = monthContainerAry[0].getMonth();
        }
        monthContainerAry = new EMonthContainer[maxCount];
        showMonthContainer(year, month);
    }

    /**
     * 显示该面板中某年/某月对应的每一天的数据
     * 
     * @param year
     *            当前年
     * @param month
     *            当前月
     */
    private void showMonthContainer(
            int year,
            int month) {
        int tmpActYear, tmpActMonth;
        int tmpIndex = 0;
        final int tmpLeftX = 0;// (paneWidth - xCount * dayWidth) / 2; //横向显示的日期面板两侧的空余
        final int tmpTopY = 0;// (paneHeight - yCount * dayHeight) / 2; //纵向显示的日期面板上下的空余
        // 习惯上使用i表示横向，j表示纵向
        for (int i = 0; i < yCount; ++i) {
            for (int j = 0; j < xCount; ++j, ++tmpIndex) {
                tmpActYear = year;
                tmpActMonth = month + tmpIndex;

                // 进位
                if (tmpActMonth >= 12) {
                    tmpActYear += tmpActMonth / 12;
                    tmpActMonth %= 12;
                }
                monthContainerAry[tmpIndex] =
                        new EMonthContainer(tmpActYear, tmpActMonth, tmpIndex, xCount, maxCount, this);
                // 记录当前日期对应的日期面板
                if (tmpActYear == lastYear && tmpActMonth == lastMonth) {
                    lastIndex = tmpIndex;
                }
                monthContainerAry[tmpIndex].setBounds(tmpLeftX + j * dayWidth, tmpTopY + i * dayHeight, dayWidth,
                        dayHeight);
                add(monthContainerAry[tmpIndex]); // 把当前月中的每一个小的日期面板加上去
            }
        }
    }

    /**
     * 改变月数据
     */
    public void changeMonth(
            int index,
            int year,
            int month,
            boolean flag) {
        int tmpYear, tmpMonth;
        for (int i = 0; i < maxCount; ++i) {
            if ((i == index) && (flag == false)) {
                continue;
            }
            tmpYear = year;
            tmpMonth = month - index + i;

            // 调整年份和月份
            if (tmpMonth >= 12) {
                tmpYear -= tmpMonth / 12;
                tmpMonth %= 12;
            } else if (tmpMonth < 0) // 与>12不同是因为java对负数的处理规则
            {
                tmpYear -= tmpMonth / 12;
                tmpMonth %= 12;
                tmpYear--;
                tmpMonth += 12;
            }

            monthContainerAry[i].repaintCalendar(tmpYear, tmpMonth);
        }
    }

    public void writeCalendarData(
            boolean prmIsTmp) {
        Enumeration keys;
        String key;
        int day[];
        int year, month;// , date;
        int beginIndex, endIndex, len;

        Hashtable hash1, hash2;
        if (prmIsTmp) {
            hash1 = dayHash;
            hash2 = weekHash;
        } else {
            hash1 = normalDayHash;
            hash2 = weekDayHash;
        }

        if (selectedModel == MonthConstant.DAY_MODEL || selectedModel == MonthConstant.WORK_MODEL) {
            keys = hash1.keys();
            while (keys.hasMoreElements()) {
                beginIndex = 0;
                key = (String) (keys.nextElement());
                len = key.length();
                endIndex = key.indexOf(',');
                if (endIndex == -1) {
                    break;
                }
                year = Integer.parseInt(key.substring(beginIndex, endIndex));
                month = Integer.parseInt(key.substring(endIndex + 1, len));
                day = (int[]) (hash1.get(key));
                if (day != null) {
                    len = day.length;
                    for (int i = 0; i < len; ++i) {
                        if (prmIsTmp) {
                            addNewDay(year, month, day[i], false);
                        } else {
                            addDateHouse(year, month, day[i]);
                        }
                    }
                }
            }
        } else {
            keys = hash2.keys();
            while (keys.hasMoreElements()) {
                beginIndex = 0;
                key = (String) (keys.nextElement());
                len = key.length();
                endIndex = key.indexOf(',');
                if (endIndex == -1) {
                    break;
                }
                year = Integer.parseInt(key.substring(beginIndex, endIndex));
                month = Integer.parseInt(key.substring(endIndex + 1, len));
                day = (int[]) (hash2.get(key));
                if (day != null) {
                    len = day.length;
                    for (int i = 0; i < len; ++i) {
                        if (prmIsTmp) {
                            // A.s("month =22222= " + month + " day =2222="+day[i]);
                            addNewDay(year, month, day[i], true);
                        } else {
                            addDateHouse(year, month, day[i]);
                        }
                    }
                }
            }
        }
        if (prmIsTmp) {
            removeTemporary();
            lastYear = lastTmpYear;
            lastMonth = lastTmpMonth;
            lastDate = lastTmpDate;
            lastIndex = lastTmpIndex;

            dateArray = null;
            writeCalendarData(false);
        } else {
            if (selectedModel == MonthConstant.WEEK_MODEL) {
                if (dateArray != null && dateArray.length > 7) {
                    selectedModel = MonthConstant.MONTH_MODEL;
                }
            }
            postFireChangeEvent();
        }

        // Enumeration tmpEnumKeys;
        // String tmpKey;
        // int day[];
        // int year, month;
        // int beginIndex, endIndex, len;
        //
        // Hashtable hash1, hash2;
        // if (prmIsTmp)
        // {
        // hash1 = dayHash;
        // hash2 = weekHash;
        // }
        // else
        // {
        // hash1 = normalDayHash;
        // hash2 = weekDayHash;
        // }
        //
        // if (selectedModel == MonthConstant.DAY_MODEL
        // || selectedModel == MonthConstant.WORK_MODEL)
        // {
        // tmpEnumKeys = hash1.keys();
        // }
        // else
        // {
        // Thread.dumpStack();
        // tmpEnumKeys = hash2.keys();
        // }
        //
        // while (tmpEnumKeys.hasMoreElements())
        // {
        // beginIndex = 0;
        // tmpKey = (String)(tmpEnumKeys.nextElement());
        // len = tmpKey.length();
        // endIndex = tmpKey.indexOf(',');
        // if (endIndex == -1)
        // {
        // break;
        // }
        // year = Integer.parseInt(tmpKey.substring(beginIndex, endIndex));
        // month = Integer.parseInt(tmpKey.substring(endIndex + 1, len));
        // day = (int[])((selectedModel == MonthConstant.DAY_MODEL
        // || selectedModel == MonthConstant.WORK_MODEL)
        // ? hash1.get(tmpKey) : hash2.get(tmpKey));
        //
        // if (day != null)
        // {
        // len = day.length;
        // for (int i = 0; i < len; ++i)
        // {
        // if (prmIsTmp)
        // {
        // A.s("month =****=   " + month + "   day =***= " + day[i]);
        // addNewDay(year, month, day[i],
        // !(selectedModel == MonthConstant.DAY_MODEL || selectedModel == MonthConstant.WORK_MODEL)); //false);
        // }
        // else
        // {
        // // A.s("month ==   " + month + "   day == " + day[i]);
        // addDateHouse(year, month, day[i]);
        // }
        // }
        // }
        // }
        // if (prmIsTmp)
        // {
        // removeTemporary();
        // lastYear = lastTmpYear;
        // lastMonth = lastTmpMonth;
        // lastDate = lastTmpDate;
        // lastIndex = lastTmpIndex;
        // dateArray = null;
        // writeCalendarData(false);
        // }
        // else
        // {
        // if (selectedModel == MonthConstant.WEEK_MODEL)
        // {
        // if (dateArray != null && dateArray.length > 7)
        // {
        // selectedModel = MonthConstant.MONTH_MODEL;
        // }
        // }
        // postFireChangeEvent();
        // }
    }

    private void postFireChangeEvent() {
        if (!isDuplicate()) {
            if (dateArray == null) {
                oldDateArray = null;
                return;
            }
            int len = dateArray.length;
            if (len <= 0) {
                oldDateArray = null;
                return;
            }
            if (dateArray != null) {
                // for (int i = 0; i < dateArray.length; i++)
                // {
                // A.s("day == " + dateArray[i].getDate());
                // }
            }
            daySet = new EDaySet(dateArray);
            oldDateArray = new EDate[len];
            oldDateArray = (EDate[]) (dateArray.clone());
            changeSupport.firePropertyChange(DATE_PROPERTY, null, daySet);

        } else {
            changeSupport.firePropertyChange(DUP_PROPERTY, null, null);
        }
    }

    private boolean isDuplicate() {
        if (dateArray == null || oldDateArray == null) {
            return false;
        }
        final int newLen = dateArray.length;
        final int oldLen = oldDateArray.length;
        if (newLen != oldLen) {
            return false;
        }
        for (int i = 0; i < newLen; ++i) {
            if (!dateArray[i].equals(oldDateArray[i])) {
                return false;
            }
        }
        return true;
    }

    private void addDateHouse(
            int year,
            int month,
            int date) {
        if (dateArray == null) {
            dateArray = new EDate[1];
            dateArray[0] = new EDate(year, month, date);
        } else {
            int tmpYear, tmpMonth, tmpDate;
            int len = dateArray.length;
            EDate tmpSet[] = new EDate[len];
            System.arraycopy(dateArray, 0, tmpSet, 0, len);
            dateArray = new EDate[len + 1];
            int index = 0;
            for (int i = 0; i < len; ++i, ++index) {
                tmpYear = tmpSet[i].getYear();
                tmpMonth = tmpSet[i].getMonth() - 1;
                tmpDate = tmpSet[i].getDate();
                // 相差的月份，distinction
                final int monthDis = (tmpYear - year) * 12 + (tmpMonth - month);
                if (monthDis < 0) {
                    dateArray[i] = (EDate) (tmpSet[i].clone());
                } else if (monthDis == 0) {
                    if (tmpDate < date) {
                        dateArray[i] = (EDate) (tmpSet[i].clone());
                    } else {
                        dateArray[i] = new EDate(year, month, date);
                        break;
                    }
                } else {
                    dateArray[i] = new EDate(year, month, date);
                    break;
                }
            }

            if (index < len) {
                System.arraycopy(tmpSet, index, dateArray, index + 1, len - index);
            } else {
                dateArray[len] = new EDate(year, month, date);
            }
        }
    }

    private void selectDayModel(
            boolean isSingleDay) {
        propertyHash.put(MonthPropertyConstants.SINGLE_MODEL, isSingleDay ? Boolean.TRUE : Boolean.FALSE);
        propertyHash.put(MonthPropertyConstants.WORK_WEEK_MODEL, Boolean.FALSE);
        removeStatus = MonthConstant.ALL_DAY;
        restoreAllCalendar(false);
        removeStatus = MonthConstant.NONE_DAY;
        removeAllDay(-1, true);
        addNewDay(lastYear, lastMonth, lastDate, false);
        setChangedIndex();
        viewSelectedStatus(-1, false);
        selectedModel = MonthConstant.DAY_MODEL;
        oldDateArray = null;
        dateArray = null;
        writeCalendarData(false);
    }

    public void changeWeekToDay() {
        Enumeration keys = weekDayHash.keys();
        String key;
        int day[];
        int year, month;
        int beginIndex, endIndex;
        while (keys.hasMoreElements()) {
            beginIndex = 0;
            key = (String) (keys.nextElement());
            endIndex = key.indexOf(',');
            if (endIndex == -1) {
                break;
            }
            year = Integer.parseInt(key.substring(beginIndex, endIndex));
            month = Integer.parseInt(key.substring(endIndex + 1, key.length()));
            day = (int[]) (weekDayHash.get(key));
            if (day != null) {
                for (int i = 0; i < day.length; ++i) {
                    addNewDay(year, month, day[i], false);
                }
            }
        }
        weekDayHash.clear();
    }

    private void addNewDay(
            int year,
            int month,
            int date,
            boolean weekFlag) {
        addHashtable(year, month, date, weekFlag, false);
    }

    public void addTemporaryDay(
            int year,
            int month,
            int date,
            boolean weekFlag) {
        addHashtable(year, month, date, weekFlag, true);
    }

    private void addHashtable(
            int year,
            int month,
            int date,
            boolean weekFlag,
            boolean isTemp) {
        Hashtable hash1, hash2;
        if (isTemp) {
            hash1 = dayHash;
            hash2 = weekHash;
        } else {
            hash1 = normalDayHash;
            hash2 = weekDayHash;
        }

        if (month < 0) {
            month = 11;
            --year;
        } else if (month > 11) {
            month = 0;
            ++year;
        }

        String str = Integer.toString(year);
        str += "," + Integer.toString(month);
        int day[];
        if (weekFlag == false) {
            day = (int[]) hash1.get(str);
        } else {
            day = (int[]) hash2.get(str);
        }
        if (day == null) {
            day = new int[1];
            day[0] = date;
        } else {
            int len = day.length;
            for (int i = 0; i < len; ++i) {
                if (day[i] == date) {
                    return;
                }
            }
            int tmpDay[] = new int[len];
            System.arraycopy(day, 0, tmpDay, 0, len);
            day = new int[len + 1];
            System.arraycopy(tmpDay, 0, day, 0, len);
            day[len] = date;
        }
        if (weekFlag == false) {
            hash1.put(str, day);
        } else {
            hash2.put(str, day);
        }
    }

    public int[] getTmpLastDay() {
        int day[] = new int[4];
        day[0] = lastTmpYear;
        day[1] = lastTmpMonth;
        day[2] = lastTmpDate;
        day[3] = lastTmpIndex;
        return day;
    }

    public int[] getSelectedDay(
            int year,
            int month,
            boolean isWeek,
            boolean isTmp) {
        if (isTmp) {
            return getHashDays(year, month, isWeek, dayHash, weekHash);
        } else {
            return getHashDays(year, month, isWeek, normalDayHash, weekDayHash);
        }
    }

    private int[] getHashDays(
            int year,
            int month,
            boolean isWeek,
            Hashtable hash1,
            Hashtable hash2) {
        if (month < 0) {
            month = 11;
            --year;
        } else if (month > 11) {
            month = 0;
            ++year;
        }

        String str = Integer.toString(year);
        str += "," + Integer.toString(month);
        int day1[] = (int[]) hash1.get(str);
        if (!isWeek) {
            return day1;
        }
        int len1 = (day1 == null) ? 0 : day1.length;
        int day2[] = (int[]) hash2.get(str);
        if (isWeek && removeStatus == MonthConstant.NO_WEEK_DAY) {
            return day2;
        }
        int len2 = (day2 == null) ? 0 : day2.length;
        if (len1 + len2 == 0) {
            return null;
        }
        int day[] = new int[len1 + len2];
        if (day1 != null) {
            System.arraycopy(day1, 0, day, 0, len1);
        }
        if (day2 != null) {
            System.arraycopy(day2, 0, day, len1, len2);
        }
        return day;
    }

    public void removeTemporary() {
        dayHash.clear();
        weekHash.clear();
        oldDayHash.clear();
        oldWeekHash.clear();
        removeStatus = MonthConstant.NONE_DAY;
        multipleFlag = false;
        dragInitFlag = false;
        inOutFlag = true;
    }

    public int getDayCount() {
        int dayCount = getModelDays(normalDayHash);
        dayCount += getModelDays(weekDayHash);
        return dayCount;
    }

    public int getTmpDayCount() {
        int dayCount = getModelDays(dayHash);
        dayCount += getModelDays(weekHash);
        return dayCount;
    }

    public int getWeekCount() {
        return (getModelDays(weekDayHash) / 7);
    }

    public int getTmpWeekCount() {
        return (getModelDays(weekHash) / 7);
    }

    private int getModelDays(
            Hashtable hash) {
        Enumeration keys = hash.keys();
        String key;
        int day[];
        int len = 0;
        while (keys.hasMoreElements()) {
            key = (String) (keys.nextElement());
            day = (int[]) (hash.get(key));
            len += day.length;
        }
        return len;
    }

    public boolean isDuplication(
            int year,
            int month,
            int date,
            boolean isWeek) {
        Hashtable hash1, hash2;
        if (isWeek) {
            hash1 = weekDayHash;
            hash2 = oldWeekHash;
        } else {
            hash1 = normalDayHash;
            hash2 = oldDayHash;
        }

        if (hash1.isEmpty() && hash2.isEmpty()) {
            return false;
        }

        String key = null;
        int day[];
        int dayLen;
        boolean tmpFlag = false;
        if (!hash1.isEmpty() && !tmpFlag) {
            key = Integer.toString(year) + ',';
            key += Integer.toString(month);
            day = (int[]) (hash1.get(key));
            if (day == null) {
                return false;
            }
            dayLen = day.length;
            for (int i = 0; i < dayLen; ++i) {
                if (day[i] == date) {
                    tmpFlag = true;
                    break;
                }
            }
        }

        if (removeStatus != MonthConstant.ALL_DAY) {
            return tmpFlag;
        }
        if (hash2.isEmpty() || tmpFlag == false) {
            return false;
        }

        day = (int[]) (hash2.get(key));
        if (day == null) {
            return true;
        }
        dayLen = day.length;
        for (int i = 0; i < dayLen; ++i) {
            if (day[i] == date) {
                return false;
            }
        }
        return true;
    }

    public int getRemoveStatus() {
        return removeStatus;
    }

    public void setRemoveStatus(
            int status) {
        removeStatus = status;
    }

    public void removeAllDay(
            int index,
            boolean all) {
        normalDayHash.clear();
        if (all == true) {
            weekDayHash.clear();
        }
    }

    public void viewSelectedStatus(
            int index,
            boolean isTmp) {
        for (int i = 0; i < maxCount; ++i) {
            if (i == index) {
                continue;
            }
            monthContainerAry[i].viewSelectedStatus(isTmp);
        }
    }

    public boolean getDayAttribute(
            int day) {
        if (day < 0 || day >= 7) {
            return false;
        }
        return workDaySet.get(day);
    }

    public int getContainerIndex(
            int defYear,
            int defMonth) {
        int year, month;
        for (int i = 0; i < maxCount; ++i) {
            year = monthContainerAry[i].getYear();
            month = monthContainerAry[i].getMonth();
            if (year == defYear && month == defMonth) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 本方法为杨志南所添加,以便在表格单元格编辑器中使用
     * 
     * @return 返回一块日历面板, 为空表示所在区域无法显示该组件或所给索引超界
     * @param prmIndex
     *            放0
     */
    public EMonthContainer getContainer(
            int prmIndex) {
        if (prmIndex < 0 || prmIndex >= maxCount) {
            return null;
        } else {
            return monthContainerAry[prmIndex];
        }
    }

    public void arrangeContainer(
            boolean flag,
            int[] day) {
        if (flag == true) {
            changeMonth(0, day[0], day[1], true);
        } else {
            changeMonth(maxCount - 1, day[0], day[1], true);
        }
    }

    public void showSelectedDays(
            int year,
            int month) {
        if (month < 0) {
            month = 11;
            --year;
        } else if (month > 11) {
            month = 0;
            ++year;
        }
        int index = getContainerIndex(year, month);
        if (index != -1) {
            viewSelectedStatus(index, false);
        }
    }

    public int getDays(
            int year,
            int month) {
        while (month < 0) {
            month += 12;
            --year;
        }
        while (month > 11) {
            month -= 12;
            ++year;
        }
        calendar.set(year, month, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public void restoreAllCalendar(
            boolean flag) {
        for (int i = 0; i < maxCount; ++i) {
            monthContainerAry[i].restoreCalendar(removeStatus, flag);
        }
    }

    public boolean isMouseInCalendar() {
        for (int i = 0; i < maxCount; ++i) {
            if (monthContainerAry[i].isInContainer()) {
                mouseInIndex = i;
                return true;
            }
        }
        mouseInIndex = -1;
        return false;
    }

    public void setMultipleDay(
            int x,
            int y,
            boolean isDrag) {
        if (mouseInIndex == -1) {
            return;
        }
        Point pt = monthContainerAry[mouseInIndex].getLocationOnScreen();
        x -= pt.x;
        y -= pt.y;
        monthContainerAry[mouseInIndex].setMultipleDay(x, y, isDrag);
    }

    public void setMultipleWeek(
            int x,
            int y,
            boolean isDrag) {
        if (mouseInIndex == -1) {
            return;
        }
        Point pt = monthContainerAry[mouseInIndex].getLocationOnScreen();
        x -= pt.x;
        y -= pt.y;
        monthContainerAry[mouseInIndex].setMultipleWeek(x, y, isDrag);
    }

    public void changeSlectedDays() {
        if (oldDayHash.isEmpty() && oldWeekHash.isEmpty()) {
            if (!dayHash.isEmpty()) {
                oldDayHash = ((Hashtable) dayHash.clone());
            }
            if (!weekHash.isEmpty()) {
                oldWeekHash = ((Hashtable) weekHash.clone());
            }
            return;
        }

        dayEqualHash.clear();
        weekEqualHash.clear();

        Enumeration keys = oldDayHash.keys();
        String key;
        while (keys.hasMoreElements()) {
            key = (String) (keys.nextElement());
            int day1[] = ((int[]) oldDayHash.get(key));
            int day2[] = ((int[]) dayHash.get(key));
            int count = getEqualCount(day1, day2);
            if (count == 0) {
                dayEqualHash.put(key, day1);
                continue;
            }

            int len = day1.length;
            int finalDay[] = new int[len - count];
            int index = 0;
            for (int i = 0; i < len; ++i) {
                if (!isContain(day1[i], day2)) {
                    finalDay[index] = day1[i];
                    ++index;
                }
            }
            dayEqualHash.put(key, finalDay);
        }

        keys = oldWeekHash.keys();
        while (keys.hasMoreElements()) {
            key = (String) (keys.nextElement());
            int day1[] = ((int[]) oldWeekHash.get(key));
            int day2[] = ((int[]) weekHash.get(key));
            int count = getEqualCount(day1, day2);
            if (count == 0) {
                weekEqualHash.put(key, day1);
                continue;
            }

            int len = day1.length;
            int finalDay[] = new int[len - count];
            int index = 0;
            for (int i = 0; i < len; ++i) {
                if (!isContain(day1[i], day2)) {
                    finalDay[index] = day1[i];
                    ++index;
                }
            }
            weekEqualHash.put(key, finalDay);
        }

        if (!dayEqualHash.isEmpty()) {
            oldDayHash = ((Hashtable) dayHash.clone());
            dayHash = ((Hashtable) dayEqualHash.clone());
        }
        if (!weekEqualHash.isEmpty()) {
            oldWeekHash = ((Hashtable) weekHash.clone());
            weekHash = ((Hashtable) weekEqualHash.clone());
        }

        if (!dayHash.isEmpty() || !weekHash.isEmpty()) {
            restoreAllCalendar(true);
        }

        if (!oldDayHash.isEmpty()) {
            dayHash = ((Hashtable) oldDayHash.clone());
        }
        if (!oldWeekHash.isEmpty()) {
            weekHash = ((Hashtable) oldWeekHash.clone());
        }

        dayHash.clear();
        weekHash.clear();
    }

    private int getEqualCount(
            int[] day1,
            int[] day2) {
        if (day1 == null || day2 == null) {
            return 0;
        }

        int len1 = day1.length;
        int len2 = day2.length;
        int count = 0;
        for (int i = 0; i < len1; ++i) {
            for (int j = 0; j < len2; ++j) {
                if (day1[i] == day2[j]) {
                    ++count;
                }
            }
        }
        return count;
    }

    private boolean isContain(
            int nowDay,
            int[] day) {
        if (day == null) {
            return false;
        }
        int len = day.length;
        for (int i = 0; i < len; ++i) {
            if (nowDay == day[i]) {
                return true;
            }
        }
        return false;
    }

    public void setMultipleStatus(
            boolean isMultiple) {
        multipleFlag = isMultiple;
    }

    public boolean getMultipleStatus() {
        return multipleFlag;
    }

    public void setDragInitFlag(
            boolean isInit) {
        dragInitFlag = isInit;
    }

    public boolean getDragInitFlag() {
        return dragInitFlag;
    }

    public void setInOutFlag(
            boolean isInOut) {
        inOutFlag = isInOut;
    }

    public boolean getInOutFlag() {
        return inOutFlag;
    }

    private void setChangedIndex() {
        int index = getContainerIndex(lastYear, lastMonth);
        if (index == -1) {
            changeMonth(lastIndex, lastYear, lastMonth, true);
        } else {
            lastIndex = index;
        }
    }

    public Object getValue(
            String key) {
        return propertyHash.get(key);
    }

    public boolean hasData() {
        if (!dayHash.isEmpty() || !weekHash.isEmpty()) {
            return true;
        }
        return false;
    }

    public void setLastDay(
            int index,
            int year,
            int month,
            int date) {
        setLastDay(index, year, month, date, true);
    }

    /**
     * ----------------------------------------------------------------------- The follow methods are used for
     * interactiving with externel application.
     */
    /**
     * Set the work week days.
     * 
     * @param workWeekDay
     *            a int include work week status(in 7 bits)
     */
    public void setWorkWeekDay(
            int workWeekDay) {
        for (int i = 0; i < 7; ++i) {
            if ((workWeekDay & (int) Math.pow(2, i)) != 0) {
                workDaySet.set(i);
            } else {
                workDaySet.clear(i);
            }
        }
        if (selectedModel == MonthConstant.WORK_MODEL) {
            selectWorkWeekModel(true);
        }
    }

    /**
     * Set a group's propertyHash value.
     * 
     * @param key
     *            propertyHash name
     * @param value
     *            the propertyHash value
     */
    public void setValue(
            String key,
            Object value) {
        propertyHash.put(key, value);
    }

    /**
     * Set calendar model to single model.
     * 
     * @param isSingle
     *            true: single model false: non-single model
     */
    public void setSingleDay(
            boolean isSingle) {
        selectDayModel(true);
    }

    /**
     * Select date with dragging mouse.
     * 
     * @param isDragable
     *            true: can select data with dragging mouse false: can not select data with dragging mouse
     */
    public void setDragable(
            boolean isDragable) {
        propertyHash.put(MonthPropertyConstants.DRAGABLE, isDragable ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Set calendar view model to Day model.
     */
    public void selectDayModel() {
        selectDayModel(false);
    }

    /**
     * Set start date.
     * 
     * @param index
     *            the index of group
     * @param year
     *            start year
     * @param month
     *            start month
     * @param date
     *            start date
     * @param isTmp
     *            true: set the date false: set the date of temporary
     */
    public void setLastDay(
            int index,
            int year,
            int month,
            int date,
            boolean isTmp) {
        if (isTmp) {
            lastTmpIndex = index;
            lastTmpYear = year;
            lastTmpMonth = month;
            lastTmpDate = date;
        } else {
            lastIndex = index;
            lastYear = year;
            lastMonth = month;
            lastDate = date;
        }
    }

    /**
     * 获得日历的宽度， 每个日期占3个字符的宽度，一行7天，选择每周数据的面板占2个字符宽度， 再加上3个字符的宽度作为空余。
     * 
     * @called by: ECalendarBar;ECalendarCanvas;ESingleCalendar;
     * @return 日历部分需要的宽度
     */
    public int getMonthSelectWidth() {
        return (7 * 3 + 5) * getFontMetrics(CustOpts.custOps.getFontOfDay()).stringWidth("9");
    }

    /**
     * 获得日历的高度， 每个月显示6个星期，加星期缩写，加半行空余，加标题bar。
     * 
     * @called by:ECalendarCanvas;EMonthContainer;EMonthGroup;ESingleCalendar;
     * @return 日历部分需要的高度
     */
    public int getMonthSelectHeight() {
        int tmpHeight = getFontMetrics(CustOpts.custOps.getFontOfDay()).getHeight();
        return 7 * tmpHeight + tmpHeight / 2
                + ((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSABarHeight();
    }

    /**
     * Set view model to work week model.
     * 
     * @param isWorkWeek
     *            true: set view model to work week model false: cancel the work week model
     */
    public void selectWorkWeekModel(
            boolean isWorkWeek) {
        propertyHash.put(MonthPropertyConstants.SINGLE_MODEL, Boolean.FALSE);
        propertyHash.put(MonthPropertyConstants.WORK_WEEK_MODEL, isWorkWeek ? Boolean.TRUE : Boolean.FALSE);
        if (isWorkWeek == true) {
            selectWeekModel(false, true);
        }
    }

    /**
     * Set view model to work week model or week model.
     * 
     * @param isWeek
     *            true: set view model to week model false: cancel the week model
     * @param isWorkWeek
     *            true: set view model to work week model false: cancel the work week model
     */
    public void selectWeekModel(
            boolean isWeek,
            boolean isWorkWeek) {
        propertyHash.put(MonthPropertyConstants.SINGLE_MODEL, Boolean.FALSE);
        propertyHash.put(MonthPropertyConstants.WORK_WEEK_MODEL, isWorkWeek ? Boolean.TRUE : Boolean.FALSE);
        if (isWeek == true || isWorkWeek == true) {
            removeStatus = MonthConstant.ALL_DAY;
            restoreAllCalendar(false);
            removeStatus = MonthConstant.NONE_DAY;
            removeAllDay(-1, true);
            calendar.set(lastYear, lastMonth, lastDate);
            int firstWeekDay = 0;
            if (isWorkWeek == false) {
                firstWeekDay = ((Integer) propertyHash.get(MonthPropertyConstants.FIRST_DAY)).intValue();
            }
            calendar.setFirstDayOfWeek(Calendar.SUNDAY);

            int offset = calendar.get(Calendar.DAY_OF_WEEK) - firstWeekDay - 1;
            int lastDays = getDays(lastYear, lastMonth - 1);
            int theDays = getDays(lastYear, lastMonth);
            int day, month;
            boolean backFlag = false;
            boolean forwardFlag = false;
            boolean firstFlag = true;
            int lastIndex = 0;
            for (int i = 0; i < 7; ++i) {
                if (isWorkWeek == true && workDaySet.get(i) == false) {
                    continue;
                }
                lastIndex = i;
                day = lastDate - offset + i;
                month = lastMonth;
                if (day < 1) {
                    if (firstFlag == true) {
                        backFlag = true;
                    }
                    --month;
                    day += lastDays;
                } else if (day > theDays) {
                    if (firstFlag == true) {
                        forwardFlag = true;
                    }
                    ++month;
                    day -= theDays;
                }
                addNewDay(lastYear, month, day, isWeek);
                if (firstFlag == true) {
                    firstFlag = false;
                }
            }

            int tmpYear = lastYear;
            int tmpMonth = lastMonth;
            if (backFlag == true && lastDate - offset + lastIndex < 1) {
                lastMonth -= 1;
                if (lastMonth < 0) {
                    lastMonth = 11;
                    --lastYear;
                }
            } else if (forwardFlag == true) {
                lastMonth += 1;
                if (lastMonth > 11) {
                    lastMonth = 0;
                    ++lastYear;
                }
            }
            setChangedIndex();
            viewSelectedStatus(-1, false);
            lastYear = tmpYear;
            lastMonth = tmpMonth;

            if (isWeek == true) {
                selectedModel = MonthConstant.WEEK_MODEL;
            } else {
                selectedModel = MonthConstant.WORK_MODEL;
            }
            oldDateArray = null;
            dateArray = null;
            writeCalendarData(false);
        }
    }

    /**
     * Set view model to month model.
     */
    public void selectMonth() {
        setChangedIndex();
        int index = getContainerIndex(lastYear, lastMonth);
        // if (monthContainerAry == null || monthContainerAry[index] == null)
        // {
        // return;
        // }

        monthContainerAry[index].selectMonthModel();
        selectedModel = MonthConstant.MONTH_MODEL;
        oldDateArray = null;
        writeCalendarData(true);
    }

    /**
     * Get the start date.
     * 
     * @return the start date
     */
    public int[] getLastDay() {
        int day[] = new int[4];
        day[0] = lastYear;
        day[1] = lastMonth;
        day[2] = lastDate;
        day[3] = lastIndex;
        return day;
    }

    /**
     * Get the view model.
     * 
     * @return the view model
     */
    public int getSelectedModel() {
        return selectedModel;
    }

    /**
     * Set the view model.
     * 
     * @param model
     *            the view model
     */
    public void setSelectedModel(
            int model) {
        selectedModel = model;
    }

    /**
     * Add propertyHash change listener.
     * 
     * @param listener
     *            propertyHash change listener to added
     */
    public void addPropertyChangeListener(
            PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove propertyHash change listener.
     * 
     * @param listener
     *            propertyHash change listener to be removed
     */
    public void removePropertyChangeListener(
            PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Get selected dates.
     * 
     * @return selected dates
     */
    public EDaySet getDaySet() {
        return daySet;
    }

    /**
     * Set dates.
     * 
     * @param dateSet
     *            日期
     */
    public void setDaySet(
            EDaySet dateSet) {
        if (dateSet == null) {
            return;
        }

        if (selectedModel == MonthConstant.DAY_MODEL) {
            EDate tmpDate = dateSet.getIndexDate(0);
            lastYear = tmpDate.getYear();
            lastMonth = tmpDate.getMonth() - 1;
            lastDate = tmpDate.getDate();
        }

        removeStatus = MonthConstant.ALL_DAY;
        restoreAllCalendar(false);
        removeStatus = MonthConstant.NONE_DAY;
        removeAllDay(-1, true);

        EDate[] currentDate = dateSet.getArray();
        int len = currentDate.length;
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                int year = currentDate[i].getYear();
                int month = currentDate[i].getMonth() - 1;
                int date = currentDate[i].getDate();
                addNewDay(year, month, date, false);
            }
        }

        setChangedIndex();
        viewSelectedStatus(-1, false);

        dateArray = null;
        writeCalendarData(false);
    }

    /**
     * Set a week in a order.
     * 
     * @param year
     *            a year including the week
     * @param month
     *            a month including the week
     * @param date
     *            a date in the week
     */
    public void setWeek(
            int year,
            int month,
            int date) {
        if (selectedModel != MonthConstant.WEEK_MODEL && selectedModel != MonthConstant.MONTH_MODEL || daySet == null) {
            return;
        }
        propertyHash.put(MonthPropertyConstants.SINGLE_MODEL, Boolean.FALSE);
        propertyHash.put(MonthPropertyConstants.WORK_WEEK_MODEL, Boolean.FALSE);

        lastYear = year;
        lastMonth = month;
        lastDate = date;

        if (selectedModel == MonthConstant.WEEK_MODEL) {
            changeWeek();
        } else {
            changeMonth();
        }

        setLastDay(lastIndex, lastYear, lastMonth, lastDate);
    }

    private void changeWeek() {
        int year = lastYear;
        int month = lastMonth;
        int date = lastDate;

        removeStatus = MonthConstant.ALL_DAY;
        restoreAllCalendar(false);
        removeStatus = MonthConstant.NONE_DAY;
        removeAllDay(-1, true);

        int theDays = getDays(year, month);
        for (int j = 0; j < 7; ++j) {
            addNewDay(year, month, date, true);
            ++date;
            if (date > theDays) {
                date -= theDays;
                ++month;
                if (month > 11) {
                    month -= 12;
                    ++year;
                }
                theDays = getDays(year, month);
            }
        }

        setChangedIndex();
        viewSelectedStatus(-1, false);

        dateArray = null;
        writeCalendarData(false);
    }

    private void changeMonth() {
        // Thread.dumpStack();
        int year = lastYear;
        int month = lastMonth;
        int date = lastDate;

        int weeks = daySet.getDayCount() / 7;
        EDaySet nowSet = new EDaySet();

        int theDays = getDays(year, month);
        for (int i = 0; i < weeks; ++i) {
            for (int j = 0; j < 7; ++j) {
                nowSet.addDate(new EDate(year, month, date));
                ++date;
                if (date > theDays) {
                    date -= theDays;
                    ++month;
                    if (month > 11) {
                        month -= 12;
                        ++year;
                    }
                    theDays = getDays(year, month);
                }
            }
        }

        EDaySet deleteSet = daySet.removeSet(nowSet);
        if (deleteSet == null) {
            return;
        }

        int len = deleteSet.getDayCount();
        for (int i = 0; i < len; ++i) {
            year = deleteSet.getIndexDate(i).getYear();
            month = deleteSet.getIndexDate(i).getMonth() - 1;
            date = deleteSet.getIndexDate(i).getDate();
            addTemporaryDay(year, month, date, true);
        }
        removeStatus = MonthConstant.ALL_DAY;
        restoreAllCalendar(true);
        weekHash.clear();

        removeStatus = MonthConstant.NONE_DAY;
        removeAllDay(-1, true);
        len = nowSet.getDayCount();
        for (int i = 0; i < len; ++i) {
            year = nowSet.getIndexDate(i).getYear();
            month = nowSet.getIndexDate(i).getMonth() - 1;
            date = nowSet.getIndexDate(i).getDate();
            addNewDay(year, month, date, true);
        }

        setChangedIndex();
        viewSelectedStatus(-1, false);

        dateArray = null;
        writeCalendarData(false);
    }

    /**
     * Repaint all calendars in the group.
     */
    public void repaintCalendarGroup() {
        for (int i = 0; i < maxCount; ++i) {
            int year = monthContainerAry[i].getYear();
            int month = monthContainerAry[i].getMonth();
            if (selectedModel == MonthConstant.WEEK_MODEL || selectedModel == MonthConstant.MONTH_MODEL) {
                modifyWeekData();
            }
            monthContainerAry[i].repaintCalendar(year, month);
        }
    }

    private void modifyWeekData() {
        if (daySet == null) {
            return;
        }

        removeAllDay(-1, true);
        int len = daySet.getDayCount();
        int theDays = getDays(lastYear, lastMonth);
        int lastDays = getDays(lastYear, lastMonth - 1);
        calendar.set(lastYear, lastMonth, lastDate);
        int dayOffset = calendar.get(Calendar.DAY_OF_WEEK);
        dayOffset -= (Calendar.SUNDAY + ((Integer) propertyHash.get(MonthPropertyConstants.FIRST_DAY)).intValue());
        lastDate -= dayOffset;
        if (lastDate < 0) {
            lastDate += lastDays;
            --lastMonth;
            if (lastMonth < 0) {
                lastMonth = 11;
                --lastYear;
            }
        } else if (lastDate > theDays) {
            lastDate -= theDays;
            ++lastMonth;
            if (lastMonth > 11) {
                lastMonth = 0;
                ++lastYear;
            }
        }

        daySet = new EDaySet();
        EDate tmpDate = new EDate(lastYear, lastMonth, lastDate);
        int year, month, date;
        for (int i = 0; i < len; ++i) {
            year = tmpDate.getYear();
            month = tmpDate.getMonth() - 1;
            date = tmpDate.getDate();
            addNewDay(year, month, date, false);
            daySet.addDate(tmpDate);
            tmpDate = tmpDate.getNextDate();
        }

        dateArray = daySet.getArray();
        postFireChangeEvent();
    }

    /**
     * 得到月视图面板宽度
     * 
     * @return 月视图面板宽度
     */
    public int getMonthPaneWidth() {
        return dayWidth;
    }

    /**
     * 得到月视图面板高度
     * 
     * @return 高度
     */
    public int getMonthPaneHeight() {
        return dayHeight;
    }

    /**
     * Adds the specified component to the layout, using the specified constraint object.
     * 
     * @param comp
     *            the component to be added
     * @param constraints
     *            where/how the component is added to the layout.
     */
    public void addLayoutComponent(
            Component comp,
            Object constraints) {
    }

    /**
     * If the layout manager uses a per-component string, adds the component <code>comp</code> to the layout,
     * associating it with the string specified by <code>name</code>.
     *
     * @param name
     *            the string to be associated with the component
     * @param comp
     *            the component to be added
     */
    public void addLayoutComponent(
            String name,
            Component comp) {
    }

    /**
     * Returns the alignment along the x axis. This specifies how the component would like to be aligned relative to
     * other components. The value should be a number between 0 and 1 where 0 represents alignment along the origin, 1
     * is aligned the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(
            Container target) {
        return 0.0f;
    }

    /**
     * Returns the alignment along the y axis. This specifies how the component would like to be aligned relative to
     * other components. The value should be a number between 0 and 1 where 0 represents alignment along the origin, 1
     * is aligned the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(
            Container target) {
        return 0.0f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager has cached information it should be discarded.
     */
    public void invalidateLayout(
            Container target) {
    }

    /**
     * Lays out the specified container.
     * 
     * @param parent
     *            the container to be laid out
     */
    public void layoutContainer(
            Container parent) {
        layoutPane();
    }

    /**
     * Calculates the maximum size dimensions for the specified container, given the components it contains.
     * 
     * @see java.awt.Component#getMaximumSize
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(
            Container target) {
        return getSize();
    }

    /**
     * Calculates the minimum size dimensions for the specified container, given the components it contains.
     * 
     * @param parent
     *            the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(
            Container parent) {
        return new Dimension(0, 0);
    }

    /**
     * Calculates the preferred size dimensions for the specified container, given the components it contains.
     * 
     * @param parent
     *            the container to be laid out
     *
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(
            Container parent) {
        return getSize();
    }

    /**
     * Removes the specified component from the layout.
     * 
     * @param comp
     *            the component to be removed
     */
    public void removeLayoutComponent(
            Component comp) {
    }

    private Hashtable normalDayHash, weekDayHash; // TODO_注释:
    private Hashtable dayHash, weekHash;
    private Hashtable oldDayHash, oldWeekHash;
    private Hashtable dayEqualHash, weekEqualHash;
    private Hashtable propertyHash;

    private int paneWidth, paneHeight;
    private int xCount, yCount;

    // private MonthLayout layout;

    /**
     * @supplierCardinality 0..*
     */
    private EMonthContainer[] monthContainerAry;

    private int dayWidth, dayHeight;
    private int lastYear, lastMonth, lastDate;
    private int lastTmpYear, lastTmpMonth, lastTmpDate;
    private int lastIndex = -1;
    private int lastTmpIndex = -1;
    private int mouseInIndex = -1;

    private int maxCount;

    private boolean multipleFlag;
    private boolean dragInitFlag;
    private boolean inOutFlag = true;

    private Calendar calendar = Calendar.getInstance();

    private int removeStatus = MonthConstant.NONE_DAY;
    private int selectedModel = MonthConstant.DAY_MODEL;

    private EDate[] dateArray;
    private EDate[] oldDateArray;
    private EDaySet daySet;
    private PropertyChangeSupport changeSupport;

    private BitSet workDaySet = new BitSet(7);
}
