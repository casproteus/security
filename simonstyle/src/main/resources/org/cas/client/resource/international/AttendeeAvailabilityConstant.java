package org.cas.client.resource.international;

/** 定义有关应用及其子视图的常量信息*/
public interface AttendeeAvailabilityConstant 
{
    /** Creates new Attendee_Availability_Constant */
    //////////// TOP_RIGHT /////////////////////////////////
   String SHOW_ATTENDEE_AVAILABILITY = "Show attendee availability";
   String SHOW_ATTENDEE_STATUS = "Show attendee status";
   String ALL_ATTENDEES = "ALL Attendees";
   String ATTENDEE_AVAILABILITY = "Attendee Availability";
  ///////////////Lower///////////////////////////////////////////
  
   String INVITE_OTHERS = " Invite Others... ";
   String OPTIONS = "选项";
   String AUTOPICK = "AutoPick";
   String TENTATIVE = "Tentative";
   String BUSY ="忙";
   String OUT_OF_OFFICE = "外出";
   String NO_INFORMATION = "No Information";
   String MEETING_START_TIME = "会议开始时间:";
   String MEETING_END_TIME = "会议结束时间:  ";
  ///////////////////////////////////////////////////////
   String NAME = "Name";
   String ATTENDANCE = "Attendance";
   String RESPONSE = "Response";
   String SEND_MEETING_TO_THIS_ATTENDEES = "send meeting to this attendees";
   String NOT_SEND_MEETING_TO_THIS_ATTENDEES = "Don't send meeting to this attendees";  
   String REQUIRED_ATTENDEE = "Required Attendee";
   String OPTIONAL_ATTENDEE = "Optional Attendee";
   String RESOURCE = "Resource(Room or Equipment)";
   String NONE = "None";
   String ACCEPTED = "Accepted";
   String DECLINED = "Declined";
    int height = 20;
    int separate = 8;
    int layer1height = height + 1*separate;
    int layer2height = height + 1*separate;
    int layer3height = height + separate;
    int UNITCOUNT = 60;
    int GRIDLINEWIDTH = 1;
    int ROWHEIGHT = 20;
    String[] ALLTIMESTRING = {" 1:00"," 2:00"," 3:00"," 4:00"," 5:00"," 6:00"," 7:00"," 8:00"," 9:00","10:00","11:00","12:00"," 1:00"," 2:00"," 3:00"," 4:00"," 5:00"," 6:00"," 7:00"," 8:00"," 9:00","10:00","11:00"};
    int ALLTIMELONG = 24;
    String[] ZOOMEDALLTIMESTRING = {" 3:00"," 6:00"," 9:00","12:00"," 3:00"," 6:00"," 9:00"};
    int ZOOMEDALLTIMELONG = 8;
    String[] WORKTIMESTRING = {" 9:00","10:00","11:00","12:00"," 1:00"," 2:00"," 3:00"," 4:00"};
    int WORKTIMELONG = 9;
    String[] ZOOMEDWORKTIMESTRING = {" 9:00","12:00"," 3:00"};
    int ZOOMEDWORKTIMELONG = 4;
    String CLICK_HERE_TO_ADD_ATTENDEE = "click here to add attendee";
    String SEND_MEETING_TO_THIS_ATTENDEE = "send meeting to this attendee";
    String NOT_SEND_MEETING_TO_THIS_ATTENDEE = "Don't send meeting to this attendee";
    String  MEETING_ORGARNIZER = "Meeting Orgarnizer";
}
