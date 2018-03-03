package org.cas.client.platform.casutil;

import java.awt.Component;
import java.awt.Graphics;
import java.util.Calendar;
import java.util.Date;

import org.cas.client.resource.international.MonthConstant;

public class EDate implements MonthConstant, Cloneable {
    /** Creates new EDate */
    public EDate(int year, int month, int date) {
        this.year = year;
        this.month = month + 1;
        this.date = date;

        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(year, month, date);
        this.day = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;

        cur_date = new Date(year, month - 1, date);
        // day = cur_date.getDay();
        formatDateString();
    }

    public Date getCurDate() {
        return cur_date;
    }

    /**
     * Get Edate of Calendar's instance.
     * 
     * @param cal
     *            the intanceof Calendar
     * @return specified date
     */
    public static EDate getCalendarDate(
            Calendar cal) {
        int nowYear = cal.get(Calendar.YEAR);
        int nowMonth = cal.get(Calendar.MONTH);
        int nowDate = cal.get(Calendar.DATE);
        return new EDate(nowYear, nowMonth, nowDate);
    }

    /**
     * Get the year from the Edate.
     * 
     * @return year of the EDate
     */
    public int getYear() {
        return year;
    }

    /**
     * Get the month from the Edate.
     * 
     * @return month of the EDate
     */
    public int getMonth() {
        return month;
    }

    /**
     * Get the date from the Edate.
     * 
     * @return date of the EDate
     */
    public int getDate() {
        return date;
    }

    /**
     * Get the week day from the Edate.
     * 
     * @return week day of the EDate
     */
    public int getDay() {
        return day;
    }

    /**
     * Get a Calendar's instance from a Edate.
     * 
     * @param a
     *            Edate
     * @return a Calendar's instance
     */
    public static Calendar getCalendar(
            EDate eDate) {
        if (eDate == null) {
            return null;
        }
        int nowYear = eDate.getYear();
        int nowMonth = eDate.getMonth() - 1;
        int nowDate = eDate.getDate();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.set(nowYear, nowMonth, nowDate);
        return nowCalendar;
    }

    /**
     * Compare this EDate with another EDate.
     * 
     * @param des
     *            the destination to compare width
     * @return the result of the comparing < 0: the Edate is before destination = 0: the EDate is equals to destination
     *         > 0: the EDate is after destination
     */
    public int compareTo(
            EDate des) {
        int desYear = des.getYear();
        int desMonth = des.getMonth();
        int desDate = des.getDate();

        int monthDis = (year - desYear) * 12 + (month - desMonth);
        if (monthDis != 0) {
            return monthDis;
        }
        return date - desDate;
    }

    /**
     * Check this EDate is equals to destination.
     * 
     * @param obj
     *            comparing destination
     * @return the result of the comparing true: this EDate is equals to destination false: this EDate is not equals to
     *         destination
     */
    public boolean equals(
            Object obj) {
        if (!(obj instanceof EDate)) {
            return false;
        }
        EDate des = (EDate) obj;
        return (this.compareTo(des) == 0) ? true : false;
    }

    /**
     * Select the template of date string.
     * 
     * @param g
     *            graphics handler
     * @param dateWidth
     *            the width of a container to load date string
     */
    public void selectDateFormat(
            Graphics g,
            int dateWidth,
            Component prmC) {
        int width;
        dateStringModel = MAX_MODELS - 1;
        for (int i = 0; i < MAX_MODELS; ++i) {
            width = prmC.getFontMetrics(g.getFont()).stringWidth(stringPattern[i]);
            if (width < dateWidth) {
                dateStringModel = i;
                break;
            }
        }
    }

    /**
     * Get the date string.
     * 
     * @return date string
     */
    public String getDateString() {
        return dateString[dateStringModel];
    }

    /**
     * Get the week day string.
     * 
     * @return week day string
     */
    public String getDayString() {
        return WEEK_NAME[day];
    }

    /**
     * Get the date & week day string.
     * 
     * @return date & week day string
     */
    public String getFullDateString() {
        String str = Integer.toString(date) + "/";
        str += Integer.toString(month) + "/";
        str += Integer.toString(year);
        str += "(" + WEEK_SHORT_NAME[day] + ")";
        return str;
    }

    /**
     * Get the abbreviated string of week day.
     * 
     * @return abbreviated string of week day
     */
    public String getShortDayString() {
        return WEEK_SHORT_NAME[day];
    }

    /**
     * Get the date string(year + month + date).
     * 
     * @return date string(year + month + date)
     */
    public String getDateRange() {
        String str = MONTH_NAMES[month - 1] + " ";
        str += Integer.toString(date) + ", ";
        str += Integer.toString(getYear());

        return str;
    }

    /**
     * Get the hash code of this EDate.
     * 
     * @return the hash code of this EDate
     */
    public int hashCode() {
        return (year * 10000 + month * 100 + date);
    }

    /**
     * Get the previous date from this date.
     * 
     * @return the previous date from this date
     */
    public EDate getLastDate() {
        int nowYear = year;
        int nowMonth = month - 1;
        int nowDate = date - 1;

        if (nowDate <= 0) {
            --nowMonth;
            if (nowMonth < 0) {
                nowMonth = 11;
                --nowYear;
            }
            Calendar tmpCalendar = Calendar.getInstance();
            tmpCalendar.set(nowYear, nowMonth, 1);
            int lastDays = tmpCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            nowDate = lastDays;
        }
        return new EDate(nowYear, nowMonth, nowDate);
    }

    /**
     * Get the next date from this date.
     * 
     * @return the next date from this date
     */
    public EDate getNextDate() {
        int nowYear = year;
        int nowMonth = month - 1;
        int nowDate = date + 1;

        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.set(nowYear, nowMonth, 1);
        int nowDays = tmpCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (nowDate > nowDays) {
            ++nowMonth;
            if (nowMonth > 11) {
                nowMonth -= 12;
                ++nowYear;
            }
            nowDate = 1;
        }
        return new EDate(nowYear, nowMonth, nowDate);
    }

    /**
     * Get the clone of this date.
     * 
     * @return the clone of this date
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    private void formatDateString() {
        dateString = new String[MAX_MODELS];
        String str;

        String monthStr = MONTH_NAMES[month - 1];
        String monthShort = MONTH_NAMES[month - 1];
        String weekStr = WEEK_NAME[day];
        String dateStr = Integer.toString(date);

        for (int i = 0; i < MAX_MODELS; ++i) {
            if (i == SHORT_MONTH_MODEL) {
                str = weekStr + ", " + monthShort + " " + dateStr + MonthConstant.DATE;
            } else if (i == SHORT_WEEK_MODEL) {
                str = monthShort + " " + dateStr + MonthConstant.DATE; // weekShort + "," +
            } else if (i == NO_MONTH_MODEL) {
                str = dateStr + MonthConstant.DATE; // weekShort + " " +
            } else if (i == NUMBER_DATE_MODEL) {
                str = Integer.toString(month) + "/" + dateStr + MonthConstant.DATE;
            } else {
                str = weekStr + ", " + monthStr + " " + dateStr + MonthConstant.DATE;
            }
            dateString[i] = str;
        }
    }

    private int year, month, date, day;
    private String dateString[];
    private Calendar calendar = Calendar.getInstance();
    private Date cur_date;
    private int NORMAL_MODEL = 0;
    private int SHORT_MONTH_MODEL = 1;
    private int SHORT_WEEK_MODEL = 2;
    private int NO_MONTH_MODEL = 3;
    private int NUMBER_DATE_MODEL = 4;
    private int MAX_MODELS = NUMBER_DATE_MODEL + 1;
    private int dateStringModel = NORMAL_MODEL;

    private String stringPattern[] = { "Wednesday, September 00", // weekday, month, date
            "Wednesday, WWW 00", // weekday, short month, date
            "WWW, WWW 00", // short weekday, shortmonth, date
            "WWW 00", // short weekday, date
            "00/00" // month number, date number
    };
}
