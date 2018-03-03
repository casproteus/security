package org.cas.client.resource.international;

/** JUnit测试组合。包括测试案例： */
public interface PIMTableRendererConstant
{
    //用于联系人的表格中识记单元格
     String GENERAL     = "普通";
     String FINISHED    = "已完成";
     String MARKED      = "已标记";
    
     String [] MARK_STATUS = {GENERAL,FINISHED,GENERAL}; 
    //用于弹出日期选择区的组合框
     String SUNDAY    = "星期日";
     String MONDAY    = "星期一";
     String TUESDAY   = "星期二";
     String WEDNESDAY = "星期三";
     String THURSDAY  = "星期四";
     String FRIDAY    = "星期五";
     String SATURDAY  = "星期六";
    
     String [] WEEKDAYS = {SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY};
}
