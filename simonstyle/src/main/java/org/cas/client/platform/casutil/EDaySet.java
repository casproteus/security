package org.cas.client.platform.casutil;

/**
 * 该类维护一个日期数据的数组。
 */

public class EDaySet implements Cloneable {
    /**
     * 构造器
     */
    public EDaySet() {
        this(null);
    }

    /**
     * 构造器
     * 
     * @param dates
     *            日期数据的数组
     */
    public EDaySet(EDate[] dates) {
        this.dates = dates;
        if (dates == null) {
            dateLen = 0;
        } else {
            dateLen = dates.length;
        }
    }

    /**
     * 该方法返回该日期数据的数组
     * 
     * @return 该数组
     */
    public EDate[] getArray() {
        if (dates == null) {
            return new EDate[0];
        }
        return dates;
    }

    /**
     * 返回特定的日期数据在该数组中的索引
     * 
     * @param date
     *            特定的数据类型
     * @return 在该数组中的索引
     */
    public int getDateIndex(
            EDate date) {
        for (int i = 0; i < dateLen; ++i) {
            if (dates[i].equals(date)) {
                return i; // 有该数据
            }
        }
        return -1;
    }

    /**
     * 返回特定的索引对应的日期数据
     * 
     * @param index
     *            特定的索引
     * @return 对应的日期数据
     */
    public EDate getIndexDate(
            int index) {
        if (index < 0 || index >= dateLen) {
            return null;
        }
        return dates[index];
    }

    /**
     * 该方法返回该数组的长度
     * 
     * @return 数组的长度
     */
    public int getDayCount() {
        return dateLen;
    }

    /**
     * 在数组中添加数据
     * 
     * @param date
     *            需要加入的日期数据
     */
    public void addDate(
            EDate date) {
        dates = insertOneDay(dates, date);
        dateLen = dates.length;
    }

    /**
     * 该方法返回两个存储日期数据的数组中相交的数据
     * 
     * @param daySet
     *            指定的存储日期数据的数组
     * @return 存储两个数组中相交的数据的数组
     */
    public EDaySet getInterSet(
            EDaySet daySet) {
        if (dates == null || daySet == null) {
            return null;
        }

        int count = getEqualsCount(daySet);
        if (count == 0) {
            return null;
        }
        EDate finalDate[] = new EDate[count];

        EDate aDate[] = dates;
        EDate bDate[] = daySet.getArray();
        int len1 = aDate.length;
        int len2 = bDate.length;
        int index = 0;
        for (int i = 0; i < len1; ++i) {
            for (int j = 0; j < len2; ++j) {
                if (aDate[i].equals(bDate[j])) {
                    finalDate[index] = (EDate) aDate[i].clone(); // 将相同的部分加入
                    ++index;
                }
            }
        }
        return new EDaySet(finalDate);
    }

    /**
     * 该方法将两个日期数组中的数据合并
     * 
     * @param daySet
     *            设定的日期数组
     * @return 合并后的数组
     */
    public EDaySet getMergeSet(
            EDaySet daySet) {
        if (dates == null && daySet == null) {
            return null;
        } else if (dates == null) {
            return daySet;
        } else if (daySet == null) {
            return (EDaySet) this.clone();
        }

        EDate aDate[] = dates;
        EDate bDate[] = daySet.getArray();
        int len1 = aDate.length;
        int len2 = bDate.length;
        int count = len1 + len2 - getEqualsCount(daySet); // 新的数组的长度
        EDate finalDate[] = new EDate[count];
        System.arraycopy(aDate, 0, finalDate, 0, len1);
        for (int i = 0; i < len2; ++i) {
            finalDate = insertOneDay(finalDate, bDate[i]);
        }
        return new EDaySet(finalDate);
    }

    /**
     * 从当前的实例中移除特定的日期数据，并返回新的实例
     * 
     * @param daySet
     *            特定的日期数据
     * @return day's set after removing
     */
    public EDaySet removeSet(
            EDaySet daySet) {
        if (dates == null) {
            return null;
        }
        if (daySet == null) {
            return (EDaySet) this.clone();
        }

        int count = getEqualsCount(daySet);
        EDate[] aDate = dates;
        EDate[] bDate = daySet.getArray();
        int len1 = aDate.length;
        if (count == len1) {
            return new EDaySet(new EDate[0]);
        }
        EDate[] finalDate = new EDate[len1 - count];
        int index = 0;
        for (int i = 0; i < len1; ++i) {
            if (!isInArray(bDate, aDate[i])) {
                finalDate[index] = (EDate) aDate[i].clone();
                ++index;
            }
        }
        return new EDaySet(finalDate);
    }

    /**
     * 该方法判断一个特定的日期数据是否在当前的实例中
     * 
     * @param eDate
     *            特定的日期数据
     * @return true: 该数据在当前的实例中 false: 该数据不在当前的实例中 Check a date is belongs to this day's set.
     */
    public boolean isContain(
            EDate eDate) {
        return isInArray(dates, eDate);
    }

    /**
     * 克隆该对象
     * 
     * @return 对象的克隆
     * @see Object#clone Get the clone of this day's set.
     */
    public Object clone() {
        try {
            if (dateLen == 0) {
                return null;
            }
            EDaySet daySet = (EDaySet) super.clone();
            daySet.dates = new EDate[dateLen];
            for (int i = 0; i < dateLen; ++i) {
                daySet.dates[i] = (EDate) dates[i].clone();
            }
            daySet.dateLen = dateLen;
            return daySet;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * 该方法返回某个区域的日期数据的实例
     * 
     * @param begin
     *            开始位置
     * @param end
     *            结束位置
     * @return 一个存放该区域数据的实例 Get a day's set between begin position and end position.
     */
    public EDaySet getIndexOf(
            int begin,
            int end) {

        if (dateLen <= begin) {
            return null;
        }
        EDaySet daySet = new EDaySet();
        for (int i = begin; i < end; ++i) {
            daySet.addDate(dates[i]);
        }
        return daySet;
    }

    /**
     * 该方法返回两个数组中相同的数目
     * 
     * @param daySet
     *            指定的存储日期数据的数组
     * @return 相同的数目
     */
    private int getEqualsCount(
            EDaySet daySet) {
        if (dates == null || daySet == null) {
            return 0;
        }

        EDate aDate[] = dates;
        EDate bDate[] = daySet.getArray();
        int len1 = aDate.length;
        int len2 = bDate.length;
        int count = 0;
        for (int i = 0; i < len1; ++i) {
            for (int j = 0; j < len2; ++j) {
                if (aDate[i].equals(bDate[j])) {
                    ++count;
                }
            }
        }
        return count;
    }

    /**
     * 将数组重复的数据部分去掉，组成一个新的数组
     * 
     * @param srcDate
     *            存储原日期数据的数组
     * @param 特定的日期数据
     * @return 存放日期数据的数组
     */
    private EDate[] insertOneDay(
            EDate[] srcDate,
            EDate date) {
        if (date == null) {
            return srcDate;
        } else if (isInArray(srcDate, date)) {
            return srcDate;
        }

        EDate[] tmpDate;
        if (srcDate == null) {
            tmpDate = new EDate[1];
            tmpDate[0] = (EDate) date.clone();
        } else {
            int len = srcDate.length;
            tmpDate = new EDate[len + 1];
            int index = 0;
            for (int i = 0; i < len; ++i, ++index) {
                if (srcDate[i].compareTo(date) < 0) {
                    tmpDate[i] = (EDate) srcDate[i].clone();
                } else {
                    tmpDate[i] = (EDate) date.clone();
                    break;
                }
            }
            if (index < len) {
                System.arraycopy(srcDate, index, tmpDate, index + 1, len - index);
            } else {
                tmpDate[len] = (EDate) date.clone();
            }
        }
        return tmpDate;
    }

    /**
     * 判断特定的日期数据是否在数组中
     * 
     * @param 存放日期数据的数组
     * @param 特定的日记数据
     * @return true:该数据在数组中 false:该数据不在数组中
     */
    private boolean isInArray(
            EDate[] array,
            EDate date) {
        if (date == null) {
            return true;
        }
        if (array == null) {
            return false;
        }

        int len = array.length;
        for (int i = 0; i < len; ++i) {
            if (array[i].equals(date)) {
                return true;
            }
        }
        return false;
    }

    private EDate[] dates;
    private int dateLen;
}
