package org.cas.client.platform.cascontrol.navigation.action;

import org.cas.client.platform.casutil.EDaySet;

/**
 *当日历选择区中选择的日期发生变化的时候，将日期变化的信息封装在此类中，通过日历选择区中的监听器发给需要监听的对象
 */

public class DateChangeEvent
{
    public DateChangeEvent(Object source, EDaySet oldSet, EDaySet newSet)
    {
        this.source = source;
        this.oldSet = oldSet;
        this.newSet = newSet;
    }
    /**
     *取得此事件中的老EDaySet,
     *@return EDaySet 原来的EDaySet
     */
    public EDaySet getOldDateSet()
    {
        return oldSet;
    }
    /**
     *取得此事件中的新EDaySet,
     *@return EDaySet 新的EDaySet
     */
    public EDaySet getNewDateSet()
    {
        return newSet;
    }
    
    /**
     *取得数据源
     */
    public Object getSource()
    {
        return source;
    }
    
    private EDaySet oldSet;
    private EDaySet newSet;
    private Object source;
}
