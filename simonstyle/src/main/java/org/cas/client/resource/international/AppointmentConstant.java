package org.cas.client.resource.international;

/** 定义有关应用及其子视图的常量信息 */

public interface AppointmentConstant  
{
     int height = 20;
     int labelWidth = 50;
     int width = 434;
     int separate = 8;
     int subjectHeight1 = 2 * (height + separate) + separate;    
     int subjectHeight2 = 3 * (height + separate)+ separate;
     int timePaneHeight = 3 * (height + 2*separate) + separate;
     int contactsPaneHeight = (height + separate) + separate;
     int categoryPaneHeight = (height + 2*separate) + separate;
     int startTimeHeight = 20;
     int startDataHeight = 20;
     int startDataWidth =130;
     int startTimeWidth =130;
     int iconButtonHeight = 20;
     int buttonHeight = 20;
     int categoryButtonWidth = 77;
     int textWidth =480;
     int textHeight = 170;      
     String INVITETO = "To...";
      String ALL_DAY_EVENT = "全天事件(Y)";
      String ARECURRENCE = "recurrence";   // defined by Appointment
      String ACONTACTS = "联系人...";   // defined by Appointment
      String AREMINDER = "Reminder";   // defined by Appointment
      String SOUND = "播放声音(P)"; 
      String APRIVATE = "私人的"; // defined by Appointment
      String END_TIME = "结束时间(M):"; 
      String START_TIME = "开始时间(K):"; 
      String ASUBJECT = "主题(J)"; // defined by Appointment
      String ALOCATION = "地址(L)"; // defined by Appointment
      String SHOW_TIME_AS = "Show Time As:"; 
      String MINUTES = " minutes";
      String AAPPOINTMENT = "约会";   // defined by Appointment
      String NOTITLE = "无标题";
     String TITLE_2 = "会议";
     String TITLE_3 = "事件";
     String TITLE_4 = "邀请事件";

     String APPOINTMENT_STATE = "与会者状态";
     String RECEIVER = "收件人(X)...";
     String INVITE_APPOINTMENT = "邀请与会者(N):";
     String CANCEL_INVITOR = "取消议会(N)";
     String REPEAT_CYCLE = "重复周期(U)...";
     String REMIND = "提醒(R):";
     String ADVANCE = "提前";
     String TIME_DISPLAY_AS = "时间显示为(W)";
     String INVITE_ORHERS = "邀请其他人(Q)...";
     String MUST_SELECT = "必选议会者";
     String MAY_SELECT = "可选议会者";
    //column name
     String COLUMN_NAME = "名称";
     String COLUMN_ATTENDANCE = "出席";
     String COLUMN_RESPOND = "响应";
    //comboBox item
     String HAVE_APPECTED = "已接受";
     String HAVE_REFUSED = "已谢绝";
     String TRANSITORLY_CONFIRM = "暂定";
     String NONE = "无";
    
     String MEETING_ORGANIGER = "会议组织者";
     String ADD_MEET_ATTANDER = "点击此处添加议会者";
    
    
     String [] TIME_DISPLAY_AS_ARRAY =
    { "闲","暂定","忙","外出"};
    
     String MINUTE = "分";
     String HOUR = "小时";
     String DAY = "天";
     
     String[] TIME_MODE =
     {"按天","按周","按月","按年"};
     
}
