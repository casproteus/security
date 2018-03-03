package org.cas.client.platform.casbeans.calendar;

import java.awt.Container;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JViewport;
import javax.swing.event.EventListenerList;

import org.cas.client.platform.cascontrol.navigation.action.DateChangeEvent;
import org.cas.client.platform.cascontrol.navigation.action.DateChangeListener;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.EDate;
import org.cas.client.platform.casutil.EDaySet;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimview.PicturePane;
import org.cas.client.resource.international.MonthConstant;
import org.cas.client.resource.international.PaneConsts;

public class DateSeleAreaPane extends PicturePane implements PropertyChangeListener {
    private static DateSeleAreaPane instance;

    public static DateSeleAreaPane getInstance() {
        if (instance == null) {
            instance = new DateSeleAreaPane(null);
        }
        return instance;
    }

    /**
     * 日期选择区面板构造器
     * 
     * @called by: PIMMainFrame; CalendarBasicPopup;
     * @param prmImage
     *            传入图片
     */
    public DateSeleAreaPane(Image prmImage) {
        super(prmImage);
        // 初始化paneHouse数组的MONTH_PANE索引处的元素为DateSeleAreaPane
        // 得到初始化时应该显示的时间。
        daySet = CASUtility.getTodayDate();

        // 初始化monthGroup信息。
        monthGroup = new PIMMonthGroup();
        monthGroup.setValue(MonthPropertyConstants.WEEK_NUMBER_SHOW,
                new Boolean(CustOpts.custOps.isWEEK_NUMBER_VISIBLE()));
        monthGroup
                .setValue(MonthPropertyConstants.FIRST_DAY, PIMPool.pool.getKey(CustOpts.custOps.getFIRST_WEEK_DAY()));
        monthGroup.setWorkWeekDay(CustOpts.custOps.getWORK_WEEK_DAY());
        monthGroup.setValue(MonthPropertyConstants.FIRST_WEEK,
                PIMPool.pool.getKey(CustOpts.custOps.getFIRST_YEAR_WEEK()));
        monthGroup.setDaySet(daySet);

        add(monthGroup);
        monthGroup.addPropertyChangeListener(this);
    }

    /**
     * 获得MouthGroup
     * 
     * @return MouthGroup
     */
    public PIMMonthGroup getMonthGroup() {
        return monthGroup;
    }

    // 以下方法可以删除
    /**
     * 布局面板专用
     */
    public void layoutContainer(
            Container prmContainer) {
        int paneWidth = getWidth();
        int paneHeight = getHeight();

        monthGroup.setBounds(0, 0, paneWidth, paneHeight);
        setPreferredSize(new java.awt.Dimension(paneWidth, paneHeight));
        // 这里不处理一下在我的日期选择区组合框中会有死循环
        if (getParent() instanceof JViewport) {
            monthGroup.setBounds(0, 0, paneWidth, paneHeight);
        } else {
            setBounds(0, 0, paneWidth, paneHeight);
        }
    }

    /**
     * 大概是设置处理一天(单选)
     * 
     * @param flag
     *            是否为单选
     */
    protected void setSingle(
            boolean flag) {
        monthGroup.setSingleDay(flag);
    }

    /**
     * 设置工作周
     * 
     * @param flag
     *            是否是工作周
     */
    public void setWorkWeek(
            boolean flag) {
        monthGroup.selectWorkWeekModel(flag);
        if (!flag) {
            monthGroup.selectDayModel();
        }
    }

    /**
     * 设置日?不知什么意思
     */
    public void setDay() {
        monthGroup.selectDayModel();
    }

    /**
     * 是否设置周信息
     * 
     * @param flag
     *            是否设置
     */
    public void setWeek(
            boolean flag) {
        monthGroup.selectWeekModel(flag, false);
    }

    /**
     * 方法名有问题
     * 
     * @param year
     *            年
     * @param month
     *            月
     * @param date
     *            日
     */
    public void setWeek(
            int year,
            int month,
            int date) {
        monthGroup.setWeek(year, month, date);
    }

    /**
     * 设置月?不知什么意思
     */
    public void setMonth() {
        monthGroup.selectMonth();
    }

    /**
     * 设置日期信息
     * 
     * @param daySet
     *            日期信息
     */
    public void setDaySet(
            EDaySet prmDaySet) {
        monthGroup.setDaySet(prmDaySet);
    }

    /**
     * 得到最后日期
     * 
     * @return 年月日数组
     */
    public int[] getLastDate() {
        return monthGroup.getLastDay();
    }

    /**
     * 设置是否翻到最后
     * 
     * @param isBack
     *            是否翻到最后
     */
    public void setPageBack(
            boolean isBack) {
        int viewModel = getCalendarViewModel();
        if (viewModel == MonthConstant.DAY_MODEL) {
            if (isBack) {
                EDate tmpDate = daySet.getIndexDate(0);
                daySet.addDate(tmpDate.getLastDate());
                int count = daySet.getDayCount();
                daySet = daySet.removeSet(daySet.getIndexOf(count - 1, count));
            } else {
                int count = daySet.getDayCount();
                EDate tmpDate = daySet.getIndexDate(count - 1);
                daySet.addDate(tmpDate.getNextDate());
                daySet = daySet.removeSet(daySet.getIndexOf(0, 1));
            }
            setDaySet(daySet);
        } else {
            EDate tmpDate = daySet.getIndexDate(0);
            int year = tmpDate.getYear();
            int month = tmpDate.getMonth() - 1;
            int date = tmpDate.getDate();
            int days;
            if (isBack) {
                days = CASUtility.getDays(year, month - 1);
                date -= 7;
                if (date < 1) {
                    date += days;
                    --month;
                    if (month < 0) {
                        month = 11;
                        --year;
                    }
                }
            } else {
                days = CASUtility.getDays(year, month);
                date += 7;
                if (date > days) {
                    date -= days;
                    ++month;
                    if (month > 11) {
                        month = 0;
                        ++year;
                    }
                }
            }

            if (viewModel == MonthConstant.WORK_MODEL) {
                monthGroup.setLastDay(0, year, month, date, false);
                monthGroup.selectWorkWeekModel(true);
            } else {
                setWeek(year, month, date);
                // TODO: 调其实例的方法。BaseBookPane.getCalendarViewPane().setViewScrollBar();
            }
        }
    }

    /**
     * This method gets called when a bound property is changed.
     * 
     * @param evt
     *            A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange(
            PropertyChangeEvent evt) {
        if (evt.getNewValue() == null) {
            return;
        }
        daySet = (EDaySet) (evt.getNewValue()); // 当前选中的日期。
        int newModel = monthGroup.getSelectedModel(); // 从monthGroup中获得选择模式。

        calendarModel = newModel;
        nowSet = (EDaySet) daySet.clone();
        for (int i = 0; i < listeners.size(); i++) {
            ((DateSelectionListener) listeners.get(i)).setSelectedDayModel(newModel, daySet);
        }
    }

    /**
     * 加日期变化监听器
     */
    public void addSelectionChangeListener(
            DateSelectionListener prmListener) {
        listeners.add(prmListener);
    }

    /**
     * 加日期变化监听器
     */
    public void addDateChangeListener(
            DateChangeListener listener) {
        listenerList.add(DateChangeListener.class, listener);
    }

    /**
     * 移除日期变化监听器
     */
    public void removeDateChangeListener(
            DateChangeListener listener) {
        listenerList.remove(DateChangeListener.class, listener);
    }

    /**
     * 发日期变化事件
     */
    public void fireDateChangeEvent(
            DateChangeEvent evt) {
        Object tmpListeners[] = listenerList.getListenerList();
        for (int i = tmpListeners.length - 2; i >= 0; i -= 2) {
            if (tmpListeners[i] == DateChangeListener.class) {
                ((DateChangeListener) tmpListeners[i + 1]).dateChanged(evt);
            }
        }
    }

    // =======================================================================
    public EDaySet getDaySet() {
        if (nowSet == null) {
            EDate[] currentDate = new EDate[1];
            currentDate[0] = new EDate(nowYear, nowMonth, nowDate);
            return new EDaySet(currentDate);
        } else {
            return nowSet;
        }
    }

    /**
     * 设置为PIM今日
     */
    public void setToday() {
        EDaySet tmpDaySet = new EDaySet();
        tmpDaySet.addDate(new EDate(nowYear, nowMonth, nowDate));
        setDaySet2(tmpDaySet);
    }

    /**
     * 设置某天的数据为日历视图的当前显示
     * 
     * @param daySet
     *            需要显示的某天的数据
     */
    public void setDaySet2(
            EDaySet daySet) {
        int viewModel = calendarModel;
        setCalendarViewModel(MonthConstant.DAY_MODEL);
        setDaySet(daySet);
        setCalendarViewModel(viewModel);
    }

    /**
     * 设置日历视图的显示模式
     * 
     * @param model
     *            view model to be set
     * @Called by: DayViewAction; WorkWeekViewAction; WeekViewAction; MorthViewAction
     */
    public void setCalendarViewModel(
            int model) {
        switch (model) {
            case MonthConstant.DAY_MODEL:
                setDay();
                break;

            case MonthConstant.WORK_MODEL:
                setWorkWeek(true);
                break;

            case MonthConstant.WEEK_MODEL:
                setWeek(true);
                break;

            case MonthConstant.MONTH_MODEL:
                setMonth();
                break;

            default:
                break;
        }
        calendarModel = model;
    }

    /**
     * 应该是开始的月份。 该方法返回当前的星期数 Get current week number in a order(the week including today is as No.26).
     * 
     * @return 当前的星期数
     */
    public int getCurrentWeek() {
        int[] tmpLastDate = getLastDate();
        int tmpYear = tmpLastDate[0];
        int tmpMonth = tmpLastDate[1];
        int tmpDate = tmpLastDate[2];

        cal.set(nowYear, nowMonth, nowDate);
        int nowWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int nowMaxWeek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);

        cal.set(tmpYear, tmpMonth, tmpDate);
        int selWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int selMaxWeek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
        if (tmpYear == nowYear && tmpMonth >= nowMonth && tmpDate > nowDate) {
            if (selWeek < nowWeek) {
                selWeek = selMaxWeek;
            }
        }

        int week;
        if (tmpYear == nowYear) {
            week = selWeek - nowWeek + PaneConsts.MAX_WEEK / 2;
            if (week > PaneConsts.MAX_WEEK) {
                week = PaneConsts.MAX_WEEK;
            }
        } else if (tmpYear > nowYear) {
            week = PaneConsts.MAX_WEEK;
            if (tmpYear - nowYear < 2) {
                week = nowMaxWeek - nowWeek + selWeek + PaneConsts.MAX_WEEK / 2;
                if (week > PaneConsts.MAX_WEEK) {
                    week = PaneConsts.MAX_WEEK;
                }
            }
        } else {
            week = 0;
            if (nowYear - tmpYear < 2) {
                week = PaneConsts.MAX_WEEK / 2 - (selMaxWeek - selWeek + nowWeek);
                if (week < 0) {
                    week = 0;
                }
            }
        }

        return week;
    }

    /**
     * Set week number.
     * 
     * @param week
     *            number
     */
    public void setWeekNumber(
            int week) {
        if (week < 0 || week > PaneConsts.MAX_WEEK) {
            return;
        }

        cal.set(nowYear, nowMonth, nowDate);
        int offset = cal.get(Calendar.DAY_OF_WEEK);
        offset -= CustOpts.custOps.getFIRST_WEEK_DAY();
        if (offset < 0) {
            offset += 7;
        }
        ++offset;

        int year = nowYear;
        int month = nowMonth;
        int lastDays = CASUtility.getDays(year, month - 1);
        int date = nowDate;
        date = date - offset + 1;
        if (date < 1) {
            date = lastDays + date;
            --month;
            if (month < 0) {
                month += 12;
                --year;
            }
            lastDays = CASUtility.getDays(year, month - 1);
        }

        int weekCount = PaneConsts.MAX_WEEK / 2;
        int theDays = CASUtility.getDays(year, month);
        if (week >= PaneConsts.MAX_WEEK / 2) {
            while (week > weekCount) {
                date += 7;
                ++weekCount;
                if (date > theDays) {
                    date -= theDays;
                    ++month;
                    if (month > 11) {
                        month -= 12;
                        ++year;
                    }
                    theDays = CASUtility.getDays(year, month);
                }
            }
        } else {
            while (week < weekCount) {
                date -= 7;
                --weekCount;
                if (date < 1) {
                    date += lastDays;
                    --month;
                    if (month < 0) {
                        month += 12;
                        --year;
                    }
                    lastDays = CASUtility.getDays(year, month - 1);
                }
            }
        }
        setWeek(year, month, date);
    }

    /**
     * 返回视图的显示模式
     * 
     * @return 当前的显示模式：1、DAY_MODEL 2、WORK_MODEL等等
     * @see PIMMonthGroup
     * @Called by: DateSeleAreaPane
     */
    public int getCalendarViewModel() {
        return calendarModel;
    }

    // 月视图的引用
    private PIMMonthGroup monthGroup;
    // 选择的日期
    private EDaySet daySet;

    private EventListenerList listenerList = new EventListenerList();
    ArrayList listeners = new ArrayList();

    private EDaySet nowSet;
    private Calendar cal = Calendar.getInstance();
    private int nowYear, nowMonth, nowDate;
    // 初始化视图类型，目前写死为EMonthGroup.DAY_MODEL。
    private int calendarModel = MonthConstant.DAY_MODEL;
}
