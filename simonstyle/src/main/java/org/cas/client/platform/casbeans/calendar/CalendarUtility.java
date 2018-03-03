package org.cas.client.platform.casbeans.calendar;

public class CalendarUtility {
    private static int GREGORIAN_CALENDAR = 1;
    private static int JULIAN_CALENDAR; // = 0;
    private static int NONEXISTENT_DAY = -1;
    private static int[] monthDays = new int[] { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    private static int DIVIDING_YEAR = 1582;
    // private static int DIVIDING_MONTH = 10;
    // private static int DIVIDING_DAY = 5;
    // private static int DIVIDING_DAYS = 10;
    private static int[] CONSTELLATION = new int[] { 1222, 122, 222, 321, 421, 522, 622, 722, 822, 922, 1022, 1122,
            1222 };
    private static int startLunarYear = -849; // 记录从公元前850年开始
    /** 公元前850年到公元2100年的农历闰月信息 */
    private static String LEAP_MONTH = "0c0080050010a0070030c0080050010a0070030c0080050020a0070030c0080050020"
            + "a0070030c0090050020a0070030c0090050020a0060030c0060030c00900600c0c0060c00c00c00c"
            + "0c000600c0c0006090303030006000c00c060c0006c00000c0c0c0060003030006c00009009c0090"
            + "c00c009000300030906030030c0c00060c00090c0060600c0030060c00c003006009060030c00600"
            + "60c0090900c00090c0090c00c006030006060003030c0c00030c0060030c0090060030c0090300c0"
            + "080050020a0060030c0080050020b0070030c0090050010a0070030b0090060020a0070040c00800"
            + "50020a0060030c0080050020b0070030c0090050010a0070030b0090060020a0070040c008005002"
            + "0a0060030c0080050020b0070030c0090050000c0090090900900909009009009090090090900900"
            + "90090900900909009009009090090090900900900909009009090090090900900900909009009090"
            + "09009009090090090900900900909009009090060030c0090050010a0070030b0080050010900700"
            + "40c0080050020a0060030c0090040010a0060030c0090050010a0070030b0080050010a008005001"
            + "090050020a0060030c0080040010a0060030c0090050010a0070030b0080050010a0070030b00800"
            + "5001090070040c0080050020a0060030c0080040010a0060030c0090050010a0070030b008005001"
            + "090070040c0080050020a0060030c0080040010a0060030c0090050010a0060030c0090050010a00"
            + "70030b008005001090070040c0080050020a0060030c0080040010a0070030b0080050010a007004"
            + "0c0080050020a0060030c0080040010a0070030c0090050010a0070030b0080050020a0060030c00"
            + "80040010a0060030c0090050050020a0060030c0090050010b0070030c0090050010a0070040c008"
            + "0040020a0060030c0080050020a0060030c0090050010a0070030b0080040020a0060040c0090050"
            + "020b0070030c00a0050010a0070030b0090050020a0070030c0080040020a0060030c0090050010a"
            + "0070030c0090050030b007005001090050020a007004001090060020c0070050c0090060030b0080"
            + "040020a0060030b0080040010a0060030b0080050010a0050040c0080050010a0060030c00800500"
            + "10b0070030c007005001090070030b0070040020a0060030c0080040020a0070030b0090050010a0"
            + "060040c0080050020a0060040c0080050010b0070030c007005001090070030c0080050020a00700"
            + "30c0090050020a0070030c0090050020a0060040c0090050020a0060040c0090050010b0070030c0"
            + "080050030b007004001090060020c008004002090060020a008004001090050030b0080040020a00"
            + "60040b0080040c00a0060020b007005001090060030b0070050020a0060020c00800400209007003"
            + "0c008005002090070040c0080040020a0060040b0090050010a0060030b0080050020a0060040c00"
            + "80050010b00700300108005001090070030c0080050020a007003001090050030a0070030b009005"
            + "0020a0060040c0090050030b0070040c0090050010c0070040c0080060020b00700400a090060020"
            + "b007003002090060020a005004001090050030b007004001090050040c0080040c00a0060020c007"
            + "005001090060030b0070050020a0060020c008004002090060030b008004002090060030b0080040"
            + "020a0060040b0080040010b0060030b0070050010a00600400207005003080060040030700500307"
            + "006004003070050030800600400307005004090060040030700500409006005002070050030a0060"
            + "05003070050040020600400206005003002060040030700500409006004003070050040800700500"
            + "3080050040a006005003070050040020600500308005004002060050020700500400206005003070"
            + "06004002070050030800600400307005004080060040a00600500308005004002070050040900600"
            + "4002060050030b006005002070050030800600400307005004080060040030700500408006004002" + "0";

    // -849-2100
    /** Creates a new instance of CalendarUtility */
    private CalendarUtility() {
    }

    /**
     * 数学方法，用于取的浮点数的小数部分。
     */
    public static double tail(
            double x) {
        return x - Math.floor(x);
    }

    // 计算阳历------------------------------------------------------------------
    /**
     * 判断阳历year年month月day日是Gregorian历还是Julian历。
     * 
     * @param year
     *            阳历年份
     * @param month
     *            阳历月份
     * @param day
     *            阳历日期
     * @return GREGORIAN_CALENDAR 是Gregorian JULIAN_CALENDAR 是Julian历 NONEXISTENT_DAY 是Gregorge历所删去的那10天
     */
    public static int getCalendarType(
            int year,
            int month,
            int day) {
        if ((year > DIVIDING_YEAR) || ((year == DIVIDING_YEAR) && (month > 10))
                || ((year == DIVIDING_YEAR) && (month == 10) && (day > 14))) {
            return GREGORIAN_CALENDAR; // Gregorian
        } else if ((year == DIVIDING_YEAR) && (month == 10) && (day >= 5) && (day <= 14)) {
            return NONEXISTENT_DAY; // 不存在的日期
        } else {
            return JULIAN_CALENDAR; // Julian
        }
    }

    /**
     * 返回阳历year年month月day日的是当年的第几天，如2000年3月1日为61。
     * 
     * @param year
     *            阳历年份
     * @param month
     *            阳历月份
     * @param day
     *            阳历日期
     * @return year年month月day日是当年的第几天
     */
    public static int daysOfYear(
            int year,
            int month,
            int day) {
        final int calendarType = getCalendarType(year, month, day);
        if (calendarType == NONEXISTENT_DAY) {
            return -1;
        }
        // 确定是否为阳历闰月
        // 整百需整除400是在Gregorian中确定的
        if (((calendarType == GREGORIAN_CALENDAR) && (((year % 100 != 0) && (year % 4 == 0)) || (year % 400 == 0)))
                || ((calendarType == JULIAN_CALENDAR) && (year % 4 == 0))) {
            monthDays[2] = 29;
        } else {
            monthDays[2] = 28;
        }
        // 累加天数
        int days = 0;
        for (int i = 1; i < month; i++) {
            days += monthDays[i];
        }
        days += day;
        // 1582年被扣掉10天
        if ((year == DIVIDING_YEAR) && (calendarType == GREGORIAN_CALENDAR)) {
            days -= 10;
        }
        return days;
    }

    /**
     * 计算反日差天数，即返回阳历y年日差天数为x时所对应的月日数 如y=2000，x=274时，返回1001(表示10月1日，即返回100*m+d)
     * 
     * @param year
     *            阳历年份
     * @param days
     *            当年的日差天数
     * @return 月份*100+日期
     */
    public static int locationOfDay(
            int year,
            int days) {
        int month;
        int daysAfterMonth;
        int daysBeforeMonth = 0;
        for (month = 1; month < 13; month++) {
            daysAfterMonth = daysOfYear(year, month + 1, 1) - 1;
            if (days < daysAfterMonth) {
                break;
            }
            daysBeforeMonth = daysAfterMonth;
        }
        return month * 100 + days - daysBeforeMonth;
    }

    /**
     * 返回y年的年差天数（y年1月1日距相应历种的1年1月1日的天数）， Julian历和Gregorian历不同。1582年按照Julian历计算。 未见使用。
     * 
     * @param year
     *            阳历年份 return 阳历y年1月1日距相应历种的1年1月1日的天数。
     */
    public static int daysBeforeYear(
            int year) {
        if (year <= DIVIDING_YEAR) {
            // Julian的年差天数
            return (year - 1) * 365 + (year - 1) / 4; // Julian的年差天数
        } else {
            // Gregorian的年差天数
            return (year - 1) * 365 + (year - 1) / 4 - (year - 1) / 100 + (year - 1) / 400;
        }
    }

    /**
     * 返回等效标准天数 y年m月d日相应历种的1年1月1日的等效(即对Gregorian历与Julian历是统一的)天数
     * 
     * @param year
     *            阳历年份
     * @param month
     *            阳历月份
     * @param day
     *            阳历日期
     * @return 等效标准天数
     */
    public static int equivalentDays(
            int year,
            int month,
            int day) {
        // -2不知是什么意思
        int days = (year - 1) * 365 + ((year - 1) / 4) + daysOfYear(year, month, day) - 2;
        // Julian的等效标准天数
        if (year > DIVIDING_YEAR) {
            // +2不知什么意思，可能与扣掉的10天有关
            // -(1582 -1) / 100 + (1582 - 1) / 400 = -12
            days += -((year - 1) / 100) + ((year - 1) / 400) + 2;
            // Gregorian的等效标准天数
        }
        return days;
    }

    /**
     * 返回儒略日 zone时区y年m月d日h时min分sec秒距儒略历公元前4713年1月1日格林尼治时间正午12时的天数
     * 
     * @param year
     *            阳历年份
     * @param month
     *            阳历月份
     * @param day
     *            阳历日期
     * @param hour
     *            小时
     * @param min
     *            分钟
     * @param sec
     *            秒
     * @return 儒略历的天数
     */
    public static double julianDay(
            int year,
            int month,
            int day,
            int hour,
            int min,
            int sec,
            int zone) {
        // TODO：具体算法不清楚。
        return ((hour + (min + sec / 60.0) / 60.0) / 24.0 - 0.5 - zone / 24.0) + 1721425
                + equivalentDays(year, month, day);
        // double julianTime = (hour + (min + sec /60.0) / 60.0 ) / 24.0 - 0.5 - zone /24.0;
        // double julianDays = equivalentDays(year,month,day) + 1721425 + julianTime;//儒略日
        // return julianDays;
    }

    // /**
    // * 返回星期数（y年m月d日的星期数，如星期日为0）
    // * @param year 阳历年份
    // * @param month 阳历月份
    // * @param day 阳历日期
    // * @return 星期数
    // */
    // private int weekDay(int year, int month, int day)
    // {
    // return equivalentDays(year, month, day) % 7;
    // }
    /**
     * 返回星座数（m月d日的星座序号，如摩羯座为0）
     * 
     * @param month
     *            阳历月份
     * @param day
     *            阳历日期
     * @return 星座数，摩羯座为0
     */
    public static int constellation(
            int month,
            int day) {
        // 转换日期表示方法
        int tmpDate = month * 100 + day;
        // 年头岁尾，摩羯座
        if (tmpDate >= CONSTELLATION[0] || tmpDate < CONSTELLATION[1]) {
            return 0;
        } else {
            for (int i = 1; i < 12; i++) {
                if (tmpDate >= CONSTELLATION[i] && tmpDate < CONSTELLATION[i + 1]) {
                    return i;
                }
            }
        }
        return -1;
    }

    // 计算阳历------------------------------------------------------------------
    // 计算阴历==================================================================
    /**
     * 返回y年第n个节气（如小寒为1）的日差天数值 别人拟合出的结果，原理不清楚。
     * 
     * @param year
     *            阳历年份
     * @param n
     *            第n个节气
     * @param isDing
     *            平气或定气 return y年第n个节气（如小寒为1）的日差天数值
     */
    public static double solarTerm(
            int year,
            int n,
            boolean isDing) {
        // 儒略日
        final double julianDays =
                year * (365.2423112 - 6.4e-14 * (year - 100) * (year - 100) - 3.047e-8 * (year - 100)) + 15.218427 * n
                        + 1721050.71301;
        if (isDing) {
            // 角度
            final double angle = 3e-4 * year - 0.372781384 - 0.2617913325 * n;
            // 年差实均数
            final double yearDispersion =
                    (1.945 * Math.sin(angle) - 0.01206 * Math.sin(2 * angle)) * (1.048994 - 2.583e-5 * year);
            // 朔差实均数
            final double shuoDispersion = -18e-4 * Math.sin(2.313908653 * year - 0.439822951 - 3.0443 * n);
            return julianDays + yearDispersion + shuoDispersion - equivalentDays(year, 1, 0) - 1721425;
        } else {
            return julianDays - equivalentDays(year, 1, 0) - 1721425;
        }
    }

    /**
     * 返回甲子数x对应的天干数（如33为3）
     * 
     * @param x
     *            甲子数
     * @return 甲子数x对应的天干数
     */
    public static int gan(
            int x) {
        return x % 10;
    }

    /**
     * 返回甲子数x对应的地支数（如33为9）
     * 
     * @param x
     *            甲子数
     * @return 甲子数x对应的地支数（如33为9）
     */
    public static int zhi(
            int x) {
        return x % 12;
    }

    /**
     * 返回y年m月d日h时的年干支数（1-60）。 TODO：原程序中以立春为农历年的分界，而不是春节，可能要修改。
     * 
     * @param year
     *            阳历年份
     * @param month
     *            阳历月份
     * @param days
     *            阳历日期
     * @param hour
     *            小时
     * @return 年干支
     */
    public static int ganZhiOfYear(
            int year,
            int month,
            int days,
            int hour) {
        if ((daysOfYear(year, month, days) + hour / 24.0) < solarTerm(year, 3, false) - 1)
        // 判断是否过立春
        {
            year -= 1;
        }
        // 公元4年为农历甲子
        return (year - 3) % 60;
    }

    /**
     * 返回y年m月d日h时的月干支数（1-60）。
     * 
     * @param year
     *            阳历年份
     * @param month
     *            阳历月份
     * @param days
     *            阳历日期
     * @param hour
     *            小时
     * @return 月干支
     */
    public static int ganZhiOfMonth(
            int year,
            int month,
            int days,
            int hour) {
        TermDate[] termDates =
                new TermDate[] { new TermDate(year, month * 2 - 2), new TermDate(year, month * 2 - 1),
                        new TermDate(year, month * 2), new TermDate(year, month * 2 + 1) };
        // 将本月的两个节气移到1和2位置
        if (termDates[0].termMonth == month) {
            termDates[2] = termDates[1];
            termDates[1] = termDates[0];
        } else if (termDates[3].termMonth == month) {
            termDates[1] = termDates[2];
            termDates[2] = termDates[3];
        }
        // 调整，与上一点可能重复
        int lengthOfMonth = daysOfYear(year, month, 31) - daysOfYear(year, month, 0);
        if (termDates[2].termDay > lengthOfMonth) {
            termDates[2].termDay -= lengthOfMonth;
        }
        double termDate = ((termDates[1].term % 2) == 1) ? termDates[1].termDay : termDates[2].termDay;
        int gzOfMonth = ((days + hour / 24.0) < termDate) ? (month - 2) : (month - 1);
        if (gzOfMonth <= 0) {
            gzOfMonth += 12;
        }
        return (gan(ganZhiOfYear(year, month, days, hour)) * 12 + gzOfMonth - 10) % 60;
    }

    private static class TermDate {
        TermDate(int year, int term) {
            if (term > 24) {
                term -= 24;
            }
            this.term = term;
            termDays = solarTerm(year, term, true);
            termDate = locationOfDay(year, (int) Math.round(termDays - 0.5));
            termMonth = termDate / 100;
            termDay = termDate % 100 + tail(termDays);
        }

        final int term; // 节气数
        final double termDays; // 节气的日差天数
        final int termDate; // 节气的阳历日期
        final int termMonth; // 节气的阳历月份
        double termDay; // 节气的阳历日期，时间
    }

    /**
     * 返回y年m月d日h时的日干支数（1-60）。
     * 
     * @param year
     *            阳历年份
     * @param month
     *            阳历月份
     * @param days
     *            阳历日期
     * @param hour
     *            小时
     * @return 日干支
     */
    public static int ganZhiOfDay(
            int year,
            int month,
            int day,
            int hour) {
        // 农历子时在23点。
        double gzOfDay = equivalentDays(year, month, day);
        if (hour > 23) {
            gzOfDay++;
        }
        return ((int) Math.round(gzOfDay) + 15) % 60;
    }

    /**
     * 返回y年m月d日h时的时干支数（1-60）。
     * 
     * @param year
     *            阳历年份
     * @param month
     *            阳历月份
     * @param days
     *            阳历日期
     * @param hour
     *            小时
     * @return 时干支
     */
    public static int ganZhiOfHour(
            int year,
            int month,
            int day,
            int hour) {
        // var v=12*gan(dGz(y,m,d,h))+floor((h+1)/2)-11;
        // if(h==23)
        // v-=12;
        // return round(rem(v,60));
        int gzOfHour = gan(ganZhiOfDay(year, month, day, hour)) * 12 + (hour + 1) / 2 - 11;
        if (hour == 23) {
            gzOfHour -= 12;
        }
        return gzOfHour % 60;
    }

    /**
     * 查表获得某年的闰月数
     * 
     * @param year
     *            查询的年份
     * @return 0，无闰月 1-12，闰月的月份
     */
    public static int getLeapMon(
            int year) {
        return Integer.parseInt(new String(new char[] { LEAP_MONTH.charAt(year - startLunarYear) }), 16);
    }

    // ------农历及日月食------//
    /*
     * 角度函数。 TODO ：含义不明
     */
    public static double ang(
            double x,
            double t,
            double c1,
            double t0,
            double t2,
            double t3) {
        return tail(c1 * x) * 2 * Math.PI + t0 - t2 * t * t - t3 * t * t * t;
    }

    /**
     * 返回农历日数及日月食信息的函数，如-324.57923415，负号表示闰月，百位3表示月偏食 (2为月全食,1为日食0为无食),百位及十位表示日数,小数部分是朔望时刻(单位为天,若该天 不朔或望则小数部分为零)
     */
    public static double lunDate(
            int year,
            int month,
            int day) {
        double t = (year - 1899.5) / 100;
        double ms = Math.floor((year - 1900) * 12.3685);
        double rpi = 180 / Math.PI;
        int zone = 8;
        double f0 = ang(ms, t, 0, 0.75933, 2.172e-4, 1.55e-7) + 0.53058868 * ms - 8.37e-4 * t + zone / 24.0 + 0.5;
        double fc = 0.1734 - 3.93e-4 * t;
        double j0 = 693595 + 29 * ms;
        double aa0 = ang(ms, t, 0.08084821133, 359.2242 / rpi, 0.0000333 / rpi, 0.00000347 / rpi);
        double ab0 = ang(ms, t, 7.171366127999999e-2, 306.0253 / rpi, -0.0107306 / rpi, -0.00001236 / rpi);
        double ac0 = ang(ms, t, 0.08519585128, 21.2964 / rpi, 0.0016528 / rpi, 0.00000239 / rpi);
        // int leap = 0; //闰月数,0则不闰
        int ecli = 0; // 日月食
        double lunD = -1; // 农历日数
        // int shuoD=0; //本阴历月的阴历朔日数
        // 原来高朋修改的算法使用的int值，造成误差，
        double shuoT = 0; // 本阴历月的朔时刻
        double wangD = 0; // 本阴历月的望时刻
        double wangT = 0; // 本阴历月的阴历望日数
        // */
        for (int i = -2; i <= 26; i++) {
            // k=整数为朔,k=半整数为望
            double k = i / 2.0;
            double aa = aa0 + 0.507984293 * k;
            double ab = ab0 + 6.73377553 * k;
            double ac = ac0 + 6.818486628 * k;
            double f1 =
                    f0 + 1.53058868 * k + fc * Math.sin(aa) - 0.4068 * Math.sin(ab) + 0.0021 * Math.sin(2.0 * aa)
                            + 0.0161 * Math.sin(2.0 * ab) + 0.0104 * Math.sin(2.0 * ac) - 0.0074 * Math.sin(aa - ab)
                            - 0.0051 * Math.sin(aa + ab);
            double j = j0 + 28 * k + f1; // 朔或望的等效标准天数及时刻
            // 原来高朋修改的算法中使用的是int，有误差，不妥
            // 记录当前日期的j值
            double lunD0 = equivalentDays(year, month, day) - Math.floor(j);
            // (int)Math.round(j - 0.5); //当前日距朔日的差值
            double k1 = 0;
            if ((k == Math.floor(k)) && (lunD0 >= 0) && (lunD0 <= 29)) {
                k1 = k; // 记录当前时间对应的k值
                shuoT = tail(j);
                lunD = lunD0 + 1;
            }
            // 原来高朋在修改此算法中使用的是else if不妥
            if (k == (k1 + 0.5)) {
                wangT = tail(j);
                wangD = Math.floor(j) - (equivalentDays(year, month, day) - lunD + 1) + 1;
            }
            // 判断日月食
            if (((lunD == 1) && (k == k1)) || ((lunD == wangD) && (k == (k1 + 0.5)))) {
                if (Math.abs(Math.sin(ac)) <= 0.36) {
                    double s =
                            5.19595 - 0.0048 * Math.cos(aa) + 0.002 * Math.cos(2 * aa) - 0.3283 * Math.cos(ab) - 0.006
                                    * Math.cos(aa + ab) + 0.0041 * Math.cos(aa - ab);
                    double r =
                            0.207 * Math.sin(aa) + 0.0024 * Math.sin(2 * aa) - 0.039 * Math.sin(ab) + 0.0115
                                    * Math.sin(2 * ab) - 0.0073 * Math.sin(aa + ab) - 0.0067 * Math.sin(aa - ab)
                                    + 0.0117 * Math.sin(2 * ac);
                    double p = Math.abs(s * Math.sin(ac) + r * Math.cos(ac));
                    double q =
                            0.0059 + 0.0046 * Math.cos(aa) - 0.0182 * Math.cos(ab) + 0.0004 * Math.cos(2 * ab) - 0.0005
                                    * Math.cos(aa + ab);
                    if ((p - q) <= 1.5572) {
                        ecli = 1; // 日食
                        if (k != Math.floor(k)) {
                            if ((p + q) >= 1.0129) {
                                ecli = 3; // 月偏食
                            } else {
                                ecli = 2; // 月全食
                            }
                        }
                    }
                }
            }
        } // i循环结束
        double v = lunD;
        if (v == 1) {
            v += shuoT; // 朔日则返回朔的时刻
        } else if (v == wangD) {
            v += wangT; // 望日则返回望的时刻
        }
        return ecli * 100 + v;
        // */
    }

    /**
     * lunMon(y,m,d)——返回y年m月d日农历月数，若是闰月则取负值，如-12表示闰十二月
     */
    public static int lunMon(
            int year,
            int month,
            int day) {
        int lunD = (int) Math.round(lunDate(year, month, day) + 0.5) % 100;
        int totalLeap = 0;
        for (int i = -849; i <= year; i++) {
            if (getLeapMon(i) != 0) {
                totalLeap++;
            }
        }
        // 从当年到-849年的有效总月数(扣除闰月)
        int totalMonth =
                (int) Math
                        .round((equivalentDays(year, month, day) - equivalentDays(-849, 1, 21) - lunD) / 29.530588 - 0.5)
                        - totalLeap;
        // 历史上的修改月建
        if (year <= 240) {
            totalMonth++;
        }
        if (year <= 237) {
            totalMonth--;
        }
        if (year <= 24) {
            totalMonth++;
        }
        if (year <= 9) {
            totalMonth--;
        }
        if (year <= -255) {
            totalMonth++;
        }
        if (year <= -256) {
            totalMonth += 2;
        }
        if (year <= -722) {
            totalMonth++;
        }
        int lunM = (totalMonth - 3) % 12 + 1;
        if ((lunM == (getLeapMon(year) - 1)) && (month == 1) && (day < lunD)) {
            lunM *= -1;
        } else {
            if (lunM == (getLeapMon(year) - 1)) {
                lunM *= -1;
            } else {
                if ((lunM < getLeapMon(year)) || (month < lunM) && (getLeapMon(year) != 0)) {
                    lunM++;
                }
                lunM = (lunM - 1) % 12 + 1;
            }
        }
        return lunM;
    }

    // 阴历日期的索引值
    public static int getLunDateIndex(
            int year,
            int month,
            int date) {
        double result = lunDate(year, month, date);
        if (result < 0) {
            result = Math.abs(result);
        }
        int index = (int) Math.floor(result - Math.floor(result / 100) * 100);
        return (index / 10) * 10 + index % 10 - 1;
        // return 0;
    }

    // 传统的节气：立春、雨水等
    public static int getSolarTermIndex(
            int year,
            int month,
            int date) {
        int offset = daysOfYear(year, month, date);
        for (int i = 1; i < 25; i++) {
            if ((int) solarTerm(year, i, true) == offset) {
                return i - 1;
            }
        }
        return -1;
    }

    // public static void main(String[] avgs)
    // {
    // A.s(solarTerm(4, 1, true));
    // A.s((solarTerm(4, 1, true) - 7) * 24);
    // A.s(
    // "ganZhiOfYear 4/2/7/12 :     " + ganZhiOfYear(4, 2, 7, 12));
    // A.s(
    // "ganZhiOfMonth 4/1/20/12 :   " + ganZhiOfMonth(4, 1, 20, 12));
    // A.s(
    // "ganZhiOfDay 5/1/29/12 :     " + ganZhiOfDay(5, 1, 29, 12));
    // A.s(
    // "ganZhiOfHour 5/1/27/5 :     " + ganZhiOfHour(5, 1, 27, 5));
    // A.s("leap month in 2009 :        " + getLeapMon(2009));
    // int year = 2004;
    // int month = 1;
    // int date = 21;
    // String str = getGanZhiYear(year, month, date);
    // //A.s("纪年 : "+str);
    // //str = getGanZhiMonth(year, month, date);
    // str = getLunarDate(year, month, date);
    // A.s("农历日期： " + str);
    // }
}
