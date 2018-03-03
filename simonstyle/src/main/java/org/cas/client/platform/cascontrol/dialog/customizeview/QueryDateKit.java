package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.util.Calendar;
import java.util.Date;

/**
 * 数据库日期条件工具类
 */

class QueryDateKit {

    /** Creates a new instance of PIMDBQueryUtil */
    public QueryDateKit() {
        initCalendar();
    }

    /**
     * 初始化设置
     */
    private void initCalendar() {
        calendar = Calendar.getInstance();
        initCurDate();
        calendar.set(year, month, date); // 和PIM日历的日期时间一致

        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        weekDays = calendar.getActualMaximum(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 1; // 一个星期的天数 7
    }

    /**
     * 初始化当前的日期
     */
    private void initCurDate() {
        Date tmpCurDate = new Date(); // 保证每一次取到的都是最新的日期
        date = tmpCurDate.getDate(); // 当前的时间
        month = tmpCurDate.getMonth(); // 当前的月份
        year = tmpCurDate.getYear(); // 当前的年份

        calendar.set(year, month, date);
    }

    /**
     * 当前的日期的上一天
     */
    private void lastDate() {
        if (--date <= 0) {
            if (--month < 0) {
                month = 11;
                --year;
            }
            calendar.set(year, month, 1);
            date = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(year, month, date);
        }
    }

    /**
     * 当前日期的下一天
     */
    private void nextDate() {
        calendar.set(year, month, date);

        if (++date > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            if (++month > 11) {
                month = 0;
                ++year;
            }
            date = 1;
        }
    }

    /**
     * 不推荐prmCount超过7天
     */
    private void lastDate(
            int prmCount) {
        for (int i = prmCount - 1; i >= 0; i--) {
            lastDate();
        }
    }

    /**
     * 不推荐prmCount超过7天
     */
    private void nextDate(
            int prmCount) {
        for (int i = prmCount - 1; i >= 0; i--) {
            nextDate();
        }
    }

    /**
     * 上个星期
     */
    private void lastWeek() {
        calendar.set(year, month, date);

        if ((date -= 7) < 0) {
            if (--month < 0) {
                month = 11;
                --year;
            }
            calendar.set(year, month, 1); // 设置当前这个月的1号
            date += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * 下个周
     */
    private void nextWeek() {
        calendar.set(year, month, date);

        int tmpMaxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if ((date += 7) > tmpMaxDays) {
            if (++month > 11) {
                month = 0;
                ++year;
            }
            date -= tmpMaxDays;
        }
    }

    /**
     * 上个月
     */
    private void lastMonth() {
        calendar.set(year, month, date);

        if (--month < 0) {
            month = 11;
            --year;
        }
    }

    /**
     */
    private void nextMonth() {
        calendar.set(year, month, date);

        if (++month > 11) {
            month = 0;
            ++year;
        }
    }

    /**
     */
    public Date getYesterday() {
        initCurDate();
        lastDate();
        return new Date(year, month, date);
    }

    /**
     */
    public Date getToday() {
        initCurDate();
        return new Date(year, month, date);
    }

    /**
     */
    public Date getTomorrow() {
        initCurDate();
        nextDate();
        return new Date(year, month, date);
    }

    /**
     */
    public Date getLastSeven() {
        initCurDate();
        lastWeek();
        return new Date(year, month, date);
    }

    /**
     */
    public Date getNextSeven() {
        initCurDate();
        nextWeek();
        return new Date(year, month, date);
    }

    /**
     */
    public Date[] getLastWeek() {
        initCurDate();

        calendar.set(year, month, date);
        int tmpDayOf = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; // 星期几
        lastDate(tmpDayOf); // 到本星期天
        lastWeek();
        Date tmpBeg = new Date(year, month, date); // 开始日期

        nextWeek(); // 移动7天
        lastDate();
        Date tmpEnd = new Date(year, month, date);

        return new Date[] { tmpBeg, tmpEnd };
    }

    /** 
     */
    public Date[] getNextWeek() {
        initCurDate();
        calendar.set(year, month, date);
        int tmpDayOf = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; // 星期几
        nextDate(weekDays - tmpDayOf); // 下个星期天
        Date tmpBeg = new Date(year, month, date); // 开始日期

        nextWeek();
        lastDate();
        Date tmpEnd = new Date(year, month, date);

        return new Date[] { tmpBeg, tmpEnd };
    }

    /**
     */
    public Date[] getCurrWeek() {
        initCurDate();
        calendar.set(year, month, date);
        int tmpDayOf = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; // 星期几
        lastDate(tmpDayOf); // 本星期天
        Date tmpBeg = new Date(year, month, date); // 开始日期

        nextWeek();
        lastDate();
        Date tmpEnd = new Date(year, month, date);

        return new Date[] { tmpBeg, tmpEnd };
    }

    /**
     */
    public Date[] getLastMonth() {
        initCurDate();
        calendar.set(year, month, date);
        int tmpDayOf = calendar.get(Calendar.DAY_OF_MONTH); // 几号
        lastDate(tmpDayOf); // 到上个月的最后一号
        Date tmpEnd = new Date(year, month, date); // 结束日期

        nextDate(); // 到本月的1号
        lastMonth(); // 上个月的1号

        Date tmpBeg = new Date(year, month, date);
        return new Date[] { tmpBeg, tmpEnd };
    }

    /**
     */
    public Date[] getNextMonth() {
        initCurDate();
        calendar.set(year, month, date);
        int tmpDayOf = calendar.get(Calendar.DAY_OF_MONTH); // 几号
        lastDate(tmpDayOf - 1); // 本月1号
        nextMonth(); // 下个月1号
        Date tmpBeg = new Date(year, month, date); // 开始日期

        nextMonth();
        lastDate(); // 下个月的最后一号
        Date tmpEnd = new Date(year, month, date);
        return new Date[] { tmpBeg, tmpEnd };
    }

    /**
     * 这个月
     */
    public Date[] getCurrMonth() {
        initCurDate();
        calendar.set(year, month, date);
        int tmpDayOf = calendar.get(Calendar.DAY_OF_MONTH); // 几号
        lastDate(tmpDayOf - 1); // 本月1号
        Date tmpBeg = new Date(year, month, date);

        nextMonth();
        lastDate();
        Date tmpEnd = new Date(year, month, date);

        return new Date[] { tmpBeg, tmpEnd };
    }

    /**
     * 设置日期
     */
    public void setDate(
            Date prmDate) {
        begDate = prmDate;
    }

    /**
     * 得到日期
     */
    public Date getDate() {
        return begDate;
    }

    /**
     * 设置有范围的日期
     */
    public void setBoundDate(
            Date prmBeg,
            Date prmEnd) {
        begDate = prmBeg;
        endDate = prmEnd;
    }

    /**
     * 设置日期范围
     */
    public void setBoundDate(
            Date[] prmDateAry) {
        begDate = prmDateAry[0];
        endDate = prmDateAry[1];
    }

    /**
     * 得到开始日期
     */
    public Date getBegDate() {
        return begDate;
    }

    /**
     * 得到结束日期
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * 取到第一天
     */
    public int getBegDay() {
        calendar.set(begDate.getYear() + 1900, begDate.getMonth(), begDate.getDate());
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 取到最后一天
     */
    public int getEndDay() {
        calendar.set(endDate.getYear() + 1900, endDate.getMonth(), endDate.getDate());
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 得到今天是星期几
     */
    public int getBegYear() {
        return begDate.getYear() + 1900;
    }

    /**
     */
    public int getEndYear() {
        return endDate.getYear() + 1900;
    }

    /**
     * 得到月份
     */
    public int getBegMonth() {
        return begDate.getMonth();
    }

    /**
     * 得到月份
     */
    public int getEndMonth() {
        return endDate.getMonth();
    }

    private Date begDate;
    private Date endDate;

    private int month;
    private int year;
    private int date;

    private int weekDays;
    private Calendar calendar;
}
