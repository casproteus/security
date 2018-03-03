package org.cas.client.resource.international;

/***/
public interface TaskDialogConstant
{
    //--------------------------------------------
    //************  任务对话盒
    String TASK = "任务";
    String NONAME = "未命名";
     String NO_TITLE = "无标题";
     String NONE = "无";
     String DETIAL = "细节";
     String OK = "确定";
     String CANCEL = "取消";
     String RECEIVER = "收件人(X)";
     String TOPIC = "主题(J)";
     String START_DATE = "开始日期(R)";
     String END_DATE = "截至日期(D)";
     String ASSIGN_TASK = "分配任务(N)";
     String REPEAT_CYCLE = "设置重复周期(U)...";
     String STATE = "状态(K)";
     String P_R_I = "优先级(Y)";
     String OWNER = "所有者";
     String FINIST_RAY = "完成率(L)";
     String SEND_MAIL_STATE_REPORT = "发送状态报告(S)...";
     String REMIND = "提醒(M)";
     String PLAY_SOUNDS = "播放声音(P)";
     String SAVE_UPDATE_COPY = "在我的任务列表中保存任务的更新副本(T)";
     String SEND_ME_TASK_STATE_REPORT = "在任务完成后给我发送状态报告(M)";
     String CONTACT = "联系人(C)";
     String FINISHED_DATE = "完成日期(M)";
     String WORK_TOTAL = "工作总量(W)";
     String WORK_FACT = "实际工作(U)";
     String WORK_COMPANY = "单位(P)";
     String UPDATE_LIST = "更新列表";
     String LONG_WAY = "里程(G)";
     String ACCOUNT_IMPORMATION = "记帐信息(B)";
     String CREATE_NO_UPDATE_COPY = "创建未分配副本(V)";   
     String WARNING_ONE = "任务的截止日期不能在开始日期前";
     String SAVE = "保存";
     String SEND = "发送";
     
    String [] STATUS_DATA = new String []
    {
        "未开始","进行中","已完成","正在等待其他人","已推迟"
    };
    
    //--------------------------------
    //********** ParseString *********
     String HOUR = "小时";
     String DAY = "天";
     String WEEK = "周";
     String WARNING_1 = "输入错误";
     String WARNING_1_CONTENT = "请输入有效数字";
     String WARNING_2 = "数字太大";
     String WARNING_2_CONTENT = "请输入更短的数字"; 
    
    //---------------------------------
    //********** CycleDialog **********
     String RIVET_DATE_MODE = "定期模式";
     String ACCORD_DAY = "按天(D)";
     String ACCORD_WEEK = "按周(W)";
     String ACCORD_MONTH = "按月(M)";
     String ACCORD_YEAR = "按年(Y)";
    
     String PER = "每(V)";
     String PER_WORK_DAY = "每个工作日(E)";
     String WHEN_TASK_FINISHED = "每当任务完成后第(G)";
     String DAYS_START_AGAIN = "天后重新开始";
     String AGAIN_SPAN_IS = "重复间隔为(C)";
     String AFTER_WEEK = "周后的";
    
     String SUNDAY = "星期日";
     String MONDAY = "星期一";
     String TUESDAY = "星期二";
     String WEDNESDAY = "星期三";
     String THUSDAY = "星期四";
     String FIRDAY = "星期五";
     String SATURDAY = "星期六";
     String WEEK_START_AGAIN = "周重新开始";
     String MONTH_START = "个月的第";
     String PER_WEEK = "每(E)";
     String MONTHS_TAL = "个月的";
     String START_AGAIN_MONTHS = "个月重新开始";
     String PER_YEAR1 = "每年(V)";
     String PER_YEAR2 = "每年(E)";
     String DE = "的";
     String AGAIN_BOUND = "重复范围";
     String START = "开始(S)";
     String NONE_END_DATE = "无结束日期(O)";
     String ANAIN = "重复(F)";
     String END_TIMES = "次后结束";
     String END_WITH = "结束于(B)";
    
     Object [] WEEK_COMBO_DATA = new Object[]
    {
        "日子","工作日","周末","星期日","星期一","星期二","星期三","星期四","星期五","星期六"
    };
    
     Object [] MONTH_COMBO_DATA = new Object[]
    {
        "第一个","第二个","第三个","第四个","最后个"
    };    
     
     Object [] MONTH_DATA = new Object[]
    {
        "一月","二月","三月","四月",
        "五月","六月","七月","八月",
        "九月","十月","十一月","十二月"
    };
    
}
