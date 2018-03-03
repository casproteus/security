package org.cas.client.resource.international;

/***/
public interface MenuConstant
{
    // main menu-------------------------------------------------------------------
    // file menu-------------------------------------------------------------------
   
     String FILE_NEW = "新建(N)"; // new menu in file menu
    
     String FILE_OPEN = "打开项目(O)...";// open items menu item in  menu
    
     String FILE_COLSE = "关闭(C)";// close menu item in file menu
    
     String FILE_SAVE = "保存(S)";// save menu item in file menu
   
     String FILE_SAVE_AS = "另存为(A)..."; // save as project menu item in file menu
   
     String FILE_SAVE_ATTACH = "保存附件(H)"; // save attachments menu item in file menu
   
     String FILE_FOLDER = "文件夹(F)"; //  folder menu in file menu
    
     String FILE_ARCHIVE = "存档(R)...";// archive to menu item in file menu
   
     String FILE_IMPORT_EXPORT = "导入(T)...";//"导入和导出(T)..."; // import and export menu item in file menu
  
     String FILE_SET_PRINTOUT = "页面设置(U)";  // Set Printout menu in file menu
    
     String FILE_PRINT_REVIEW = "打印预览(V)";// print review menu item in file menu
  
     String FILE_PRINT = "打印(P)...";  // print menu item in file menu
 
     String FILE_SEND = "发送(D)";   // send menu item in file menu
   
     String FILE_PROPERTIES = "属性(I)..."; // properties menu item in file menu
   
     String FILE_EXIT = "退出(X)"; // exit menu item in file menu
    
    // file -> new menu==============================
    // messages menu item in file-new menu
     String FILE_NEW_MESSAGES = "邮件(M)...";
    // message templates menu item in file-new menu
     String FILE_NEW_TEMPLATES = "模板邮件(P)...";
    // appointment menu item in file-new menu
     String FILE_NEW_APPOINTMENT = "约会(A)...";
    // meeting request menu item in file-new menu
    // String FILE_NEW_MEETING_REQUEST = "会议要求";
    // meeting menu item in file-new menu
     String FILE_NEW_MEETING = "会议(Q)...";
    // task menu item in file-new menu
     String FILE_NEW_TASK = "任务(T)...";
    // task request menu item in file-new menu
    // String FILE_NEW_TASK_REQUEST = "任务要求";
    // journal menu item in file-new menu
     String FILE_NEW_JOURNAL = "手稿(J)...";
    // address list menu item in file-new menu
     String FILE_NEW_LIST = "通讯组(G)...";
    // folder menu item in file-new menu
     String FILE_NEW_FOLDER = "新建文件夹(E)...";
     String FILE_RENAME_FOLDER = "重命名文件夹(R)";
    // appoint menu item in file-new menu
    // String APPOINTMENT = "约会";
    // meeting request menu item in file-new menu
    // String MEETING_REQUEST = "会议请求";
    
    // file -- save attachments menu==============================
    // file name menu item in file-attachments menu
     String FILE_SAVE_ATTACH_NAME = "文件名";
    // all attachments menu item in file-attachments menu
     String FILE_SAVE_ATTACH_ALL = "所有附件";
    
    // file -- folder menu==============================
    // copy folder menu item in file-folder menu
     String FILE_FOLDER_COPY = "复制文件夹(C)";
    // move folder menu item in file-folder menu
     String FILE_FOLDER_MOVE= "移动文件夹(V)...";
    // delete folder menu item in file-folder menu
     String FILE_FOLDER_DEL = "删除文件夹(D)";
    //清空已删除项的清空部分。
     String EMPTY = "清空";
    //清空已删除项的文件夹部分。
     String EMPTY_FOLDER = "文件夹(Y)";
    //清空已删除项文件夹。
     String FILE_FOLDER_EMPTY = "清空\"已删除项\"文件夹(Y)";
    // folder properties menu item in file-folder menu
     String FILE_FOLDER_PROPERTIES = "文件夹属性(I)";
    
    
    // file -- page setup==============================
    // table style menu item in file-page setup menu
     String FILE_PRINTOUT_TABLE = "表格式";
    // meno style menu item in file-page setup menu
     String FILE_PRINTOUT_MEMO = "备忘录";
    // calendar style menu item in file-page setup menu
     String FILE_PRINTOUT_CALENDAR = "日历";
    // weekly calendar style menu item in file-page setup menu
     String FILE_PRINTOUT_WEEKLY = "周历";
    // monthly calendar style menu item in file-page setup menu
     String FILE_PRINTOUT_MONTHLY = "月历";
    // tri-fold style menu item in file-page setup menu
     String FILE_PRINTOUT_TRI = "三栏式";
    // calendar details style menu item in file-page setup menu
     String FILE_PRINTOUT_DETAILS = "详细日历式";
    // caed style menu item in file-page setup menu
     String FILE_PRINTOUT_CARD = "卡片式";
    // small booklet style menu item in file-page setup menu
     String FILE_PRINTOUT_SBOOKLET = "小册式";
    // medium booklet style menu item in file-page setup menu
     String FILE_PRINTOUT_MBOOKLET = "中册式";
    // phone directory style menu item in file-page setup menu
     String FILE_PRINTOUT_PHONE = "电话目录";
    // define print style menu item in file-page setup menu
     String FILE_PRINTOUT_DEFINE = "定义打印样式(S)...";
    
    
    // edit menu-------------------------------------------------------------------
    // Undo menu item in edit menu
     String EDIT_UNDO = "撤销(U)";
    // Redo menu item in edit menu
     String EDIT_REDO = "重作(R)";
    // Cut menu item in edit menu
     String EDIT_CUT = "剪切记录(T)";
    // Copy menu item in edit menu
     String EDIT_COPY = "复制记录(C)";
    // Paste menu item in edit menu
     String EDIT_PASTE = "粘贴记录(P)";
    // Paste Special menu item in edit menu
     String EDIT_PASTE_SPECIAL = "选择性粘贴(S)...";
    // Clear menu item in edit menu
     String EDIT_CLEAR = "清除(A)";
    // Select All menu item in edit menu
     String EDIT_SELECT_ALL = "全选(L)";
    // Delete menu item in edit menu
     String EDIT_DEL = "删除(D)";
    // Find menu item in edit menu
     String EDIT_FIND = "查找(F)...";
    // move to folder menu item in edit menu
     String EDIT_MOVE_TO_FOLDER = "移至文件夹(M)...";
    // copy to folder menu item in edit menu
     String EDIT_COPY_TO_FOLDER = "复制到文件夹(Y)...";
    // Mark as Read menu item in edit menu
     String EDIT_MARK_AS_READ = "标记为\"已读\"(K)";
    // Mark as Unread menu item in edit menu
     String EDIT_MARK_AS_UNREAD = "标记为\"未读\"(N)";
    // Mark All as Read menu item in edit menu
     String EDIT_MARK_ALL = "全部标记为已读(E)";
    // Repetition Period menu item in edit menu
    // String EDIT_REPETITION= "重复周期(V)";
    // Follow-up Flag menu item in edit menu
     String EDIT_FOLLOW_UP = "后续标志(W)...";
    
    //view menu-------------------------------------------------------------------
    // current view menu in edit menu
     String VIEW_CURRENT = "当前视图(V)";
    // go to menu item in edit menu
     String VIEW_GO = "转到(G)";
    // Previous menu in edit menu
     String VIEW_PREVIOUS = "上一个(R)";
    // next menu in edit menu
     String VIEW_NEXT = "下一个(X)";
    // PIM pane menu item in edit menu
     String VIEW_PANE = "导航面板(P)";
     //显示BaseBookPane。
     String VIEW_BOOKHIDE = "显示/隐藏书本(B)";
     //   显示BaseBookPane。
     String VIEW_CIRCUMVIEW = "旋转视图(C)";
    // preview pane menu item in edit menu
     String VIEW_PREVIEW = "显示/隐藏预览(N)";
    // encoding menu item in edit menu
     String VIEW_ENCODING = "编码(E)";
    // font size menu in edit menu
     String VIEW_FONTSIZE = "字体大小(F)";
    // toolbars menu item in edit menu
     String VIEW_TOOLBARS = "工具栏(T)";
    // status bar menu item in edit menu
     String VIEW_STATUS_BAR = "状态栏(S)";
    
    // view -> current view==============================
    // Messages view menu in current view menu
     String VIEW_CURRENT_MESSAGES = "邮件视图(M)";
    // Calendar view menu in current view menu
     String VIEW_CURRENT_CALENDAR = "日历视图(L)";
    // Tasks view menu in current view menu
     String VIEW_CURRENT_TASKS = "任务视图(T)";
    // Contacts view menu in current view menu
     String VIEW_CURRENT_CONTACTS = "联系人视图(C)";
    // Journal view menu in current view menu
     String VIEW_CURRENT_JOURNAL = "手稿视图(J)";
    // custom option menu in current view menu
     String VIEW_CURRENT_DEFINE_VIEW = "自定义视图(D)";
    
    
    //view -> current view -> message###############################
     String VIEW_CURRENT_MESSAGES_MESSAGES = "邮件";
     String VIEW_CURRENT_MESSAGES_BY_FOLLOW_UP_FLAG = "按后续标志";
     String VIEW_CURRENT_MESSAGES_LAST_SEVEN_DAYS = "最近七天";
     String VIEW_CURRENT_MESSAGES_FLAGGED_FOR_NEXT_SEVEN_DAYS = "随后七天标记的项目";
     String VIEW_CURRENT_MESSAGES_BY_CONVENSATION = "按会话的内容";
     String VIEW_CURRENT_MESSAGES_BY_SENDER = "按发件人";
     String VIEW_CURRENT_MESSAGES_UNREAD_MESSAGES = "未读的邮件";
     String VIEW_CURRENT_MESSAGES_SENT_TO = "收件人";
    //view -> current view -> calendar###############################
     String VIEW_CURRENT_CALENDAR_DAY = "天";
     String VIEW_CURRENT_CALENDAR_WEEK = "周";
     String VIEW_CURRENT_CALENDAR_WORK_WEEK = "工作周";
     String VIEW_CURRENT_CALENDAR_MONTH = "月";
     String VIEW_CURRENT_CALENDAR_CURRENT_APPOINTMENTS = "当前约会";
     String VIEW_CURRENT_CALENDAR_EVENTS = "事件";
     String VIEW_CURRENT_CALENDAR_ANNUAL_EVENTS = "年度事件";
     String VIEW_CURRENT_CALENDAR_REGULAR_APPOINTMENTS = "定期约会";
     String VIEW_CURRENT_CALENDAR_CATEGORY = "按类别";
    //view -> current view -> tasks###############################
     String VIEW_CURRENT_TASKS_SIMPLE_LIST = "简单列表";
     String VIEW_CURRENT_TASKS_DETAILED_LIST = "详细列表";
     String VIEW_CURRENT_TASKS_CURRENT_LIST = "当前任务";
     String VIEW_CURRENT_TASKS_NEXT_SEVEN_DAYS = "随后七天";
     String VIEW_CURRENT_TASKS_OVERDUE_TASKS = "过期任务";
     String VIEW_CURRENT_TASKS_By_CATEGORY = "按类别";
     String VIEW_CURRENT_TASKS_ASSIGNMENT = "分配";
     String VIEW_CURRENT_TASKS_BY_PERSON_IN_CHARGE = "按负责人";
     String VIEW_CURRENT_TASKS_COMPLETED_TASKS = "已完成的任务";
    //view -> current view -> contact###############################
    // Address Cards menu item in current view menu
     String VIEW_CURRENT_CONTACTS_CARD = "地址卡";
    // Detailed Address Cardss menu item in current view menu
     String VIEW_CURRENT_CONTACTS_DETAIL = "详细地址卡";
    // Phone List menu item in current view menu
     String VIEW_CURRENT_CONTACTS_PHONE = "电话列表";
    // By Category menu item in current view menu
     String VIEW_CURRENT_CONTACTS_CATEGORY = "按类别";
    // By Company menu item in current view menu
     String VIEW_CURRENT_CONTACTS_COMPANY = "按单位";
    // By Location menu item in current view menu
     String VIEW_CURRENT_CONTACTS_LOCATION = "按位置";
    // By Follow-up Flagmenu item in current view menu
     String VIEW_CURRENT_CONTACTS_FOLLOW_UP = "按后续标志";
     // By Local Folder item in current view menu
     String VIEW_CURRENT_LOCAL_FOLDER = "本地文件夹";
    
    //view -> current view -> journal###############################
     String VIEW_CURRENT_JOURNAL_BY_CATEGORY = "预览";
     String VIEW_CURRENT_JOURNAL_ITEM_LIST = "按日期";
     String VIEW_CURRENT_JOURNAL_WEEK = "按主题";
    
    // view -> Go To==============================
      String VIEW_GO_FOLDER = "文件夹";
      String VIEW_GO_PIM_TODAY = "PIM今日";
      String VIEW_GO_INBOX = "收件箱";
      String VIEW_GO_CALENDAR = "日历";
      String VIEW_GO_TASKS = "任务";
      String VIEW_GO_CONTACTS = "联系人";
      String VIEW_GO_JOURNALS = "手稿";
      String VIEW_GO_GO_TO_TODAY = "转到今日";
      String VIEW_GO_GO_TO_DATE = "转到日期";
    // view -> Previous==============================
      String VIEW_PREVIOUS_ITEM = "项目";
      String VIEW_PREVIOUS_UNREAD_ITEM = "未阅读的项目";
      String VIEW_PREVIOUS_ITEM_IN_CONVENSATION = "会话主题中的项目";
      String VIEW_PREVIOUS_ITEM_FROM_SENDER = "来自发件人的项目";
      String VIEW_PREVIOUS_HIGH_PRIORITY_MESSAGE = "高度重要的邮件";
      String VIEW_PREVIOUS_FLAGGED_MESSAGE = "已标记的邮件";
      String VIEW_PREVIOUS_FIRST_ITEM_IN_FOLDER = "文件夹中第一项";
      String VIEW_PREVIOUS_UNCOMPLETED_TASK = "未完成的任务";
    // view -> Next==============================
      String VIEW_NEXT_ITEM = "项目";
      String VIEW_NEXT_UNREAD_ITEM = "未阅读的项目";
      String VIEW_NEXT_ITEM_IN_CONVENSATION = "会话主题中的项目";
      String VIEW_NEXT_ITEM_FROM_SENDER = "来自发件人的项目";
      String VIEW_NEXT_HIGH_PRIORITY_MESSAGE = "高度重要的邮件";
      String VIEW_NEXT_FLAGGED_MESSAGE = "已标记的邮件";
      String VIEW_NEXT_FIRST_ITEM_IN_FOLDER = "文件夹中第一项";
      String VIEW_NEXT_UNCOMPLETED_TASK = "未完成的任务";
    // view -> Font Size==============================
      String VIEW_FONTSIZE_LARGEST = "最大";
      String VIEW_FONTSIZE_LARGER = "较大";
      String VIEW_FONTSIZE_MEDIUM = "中";
      String VIEW_FONTSIZE_SMALLER = "较小";
      String VIEW_FONTSIZE_SMALLEST = "最小";
     
    //insert menu--------------------------------------------------------------
    // attachments menu in edit menu
     String INSERT_ATTACHMENTS = "附件(F)...";
    // item menu in edit menu
     String INSERT_ITEM = "项目(E)...";
    // signature menu in edit menu
     String INSERT_SIGNATURE = "签名(S)...";
    // picture menu in edit menu
     String INSERT_PICTURE = "图片(P)...";
    // Videos and Audios menu in edit menu
     String INSERT_VIDEOS_AUDOIS = "多媒体(V)...";
    // hyperlink menu in edit menu
     String INSERT_HYPERLINK = "超链接(L)...";
    
    //Format menu--------------------------------------------------------------
     String FORMAT_FONT = "字体(F)...";
     String FORMAT_STYLE = "样式(S)...";
     String FORMAT_PARAGRAPH = "段落(P)...";
     String FORMAT_BULLETS_AND_NUMBERING = "项目符号和编号(N)...";
     String FORMAT_BACKGROUND = "背景(K)";
     String FORMAT_PLAIN_TEXT = "纯文本(T)";
     String FORMAT_HTML = "HTML(H)";
    
    //Tools menu--------------------------------------------------------------
     String TOOLS_SEND_RECEIVE = "发送和接收(E)";
     String TOOLS_REMOUT_ADMIN = "远程邮箱管理(R)...";
     String TOOLS_SPELLING = "保存发件人(S)";
     String TOOLS_CHECK_NAMES = "检查姓名(K)";
     String TOOLS_MAIL_MERGE = "邮件合并(M)...";
     String TOOLS_EXECUTIVE_BOARD_ROOM = "会议室(B)...";
     String TOOLS_RULES_WIZARD = "邮件规则(L)...";
     String TOOLS_JUNK_MAIL = "垃圾邮件(L)";
     String TOOLS_REPLY_TO_SENDER = "答复发件人(R)";
     String TOOLS_REPLY_TO_ALL = "全部答复(Y)";
     String TOOLS_FORWARD = "转发(W)";
     String TOOLS_ACCOUNTS = "电子邮件帐户(A)...";
     String TOOLS_OPTIONS = "选项(O)...";
    
    // tools -> Send/Receive==============================
     String TOOLS_SEND_RECEIVE_SEND_RECEIVE_ALL = "发送和接收全部邮件(A)";
     String TOOLS_SEND_RECEIVE_RECEIVE_ALL = "接收全部邮件(R)";
     String TOOLS_SEND_RECEIVE_SEND_ALL = "发送全部邮件(S)";
     String TOOLS_SEND_RECEIVE_ACCOUNTS_LIST = "帐号列表";
    
    // tools -> Junk Mail
     String TOOLS_JUNK_MAIL_SENDER_LIST = "添加到“垃圾发件人”列表中";
     String TOOLS_JUNK_MAIL_ADULT_CONTENT = "添加到“成人内容”发件列表中";
     
     //tools -> Help
     String TOOLS_HELP_CONTENT = "目录和索引(I)";
    //OVER------------------------------------------------------------------------------
    
    
     String PIMMENU = "PIM菜单";
    /* folder pane popup menu */
    /* mark all mail as read */
     String FOLDER_MARK = "全部标记为已读(E)";
    /* properties of a file */
     String FOLDER_PROPERTY = "属性...";
    
    /* folder pane drag popup menu */
    /* move a node with dragging mouse */
     String DRAG_MOVE = "移动到当前位置";
    /* copy a node with dragging mouse */
     String DRAG_COPY = "复制到当前位置";
    
    /* calendar view popup menu */
    /* new appointment menu item in popup menu */
     String NEW_APPOINTMENT = "新约会(N)...";
    /* new meeting request menu item in popup menu */
     String NEW_MEETING_REQUEST = "新约会邀请(I)...";
    /* change tine zone */
     String CHANGE_TIME_ZONE = "时区设置(A)...";
    /* unit of time span */
     String TIME_INTERVAL_MINUTE = "分钟";
    
    /* go to today menu item in popup menu */
     String GOTO_TODAY = "转到今日(T)";
    /* go to date menu item in popup menu */
     String GOTO_DATE = "转到日期(J)...";
    /* calendar information view option setting menu item in popup menu */
     String OTHER_SETTING = "其他设置(S)...";
     
     String OPEN_APPOINTMENT = "打开";
     String CATEGORY = "类别";
     
    /* attendees option popup menu */
    /* only show work time */
     String SHOW_WORK_TIME = "Show only working time";
    /* show interval time zoom */
     String SHOW_ZOOM_TIME = "Show zoomed out";
    /* update time type */
     String UPDATE_TIME = "Update free/busy";
    
    
    /* Find Next menu in edit menu */
     String EDIT_FIND_NEXT = "查找下一处";
    /* Object menu in edit menu */
     String EDIT_OBJECT = "object";
    /* View Menu*/
    /* Find Next menu in edit menu */
     String VIEW_FIND_CALENDAR = "日历...";
    /* Object menu in edit menu */
    // String VIEW_TOOLBARS = "工具条";
                
    /* view menu */
    /* go to menu in view menu */
     String GO_TO = "跳转到";
    /* day view menu item in view menu */
     String DAY_VIEW = "天";
    /* work week view menu item in view menu */
     String WORK_WEEK_VIEW = "工作周";
    /* week view menu item in view menu */
     String WEEK_VIEW = "周";
    /* month view menu item in view menu */
     String MONTH_VIEW = "月";
    /* year view menu item in view menu */
     String YEAR_VIEW = "年";
    
    /* tools menu */
    /* option menu item in tools menu */
     String OPTION = "选项...";
    
    
    /* file menu*/
    /* send menu in file menu */
     String APPOINTMENT_FILE_SEND = "发送";
    /* save menu in file menu */
     String APPOINTMENT_FILE_SAVE = "保存";
    /* save as menu in file menu */
     String APPOINTMENT_FILE_SAVE_AS = "另存为";
    /* Save Attachments menu in file menu */
     String APPOINTMENT_FILE_SAVE_ATTACHMENTS = "保存附件";
    /* Copy to Folder menu in file menu */
     String APPOINTMENT_FILE_COPY_TO_FOLDER = "拷贝到文件夹";
    /* new menu in file menu */
     String APPOINTMENT_FILE_PAGE_SETUP = "页面设置";
    /* Print Preview in file menu */
     String APPOINTMENT_FILE_PRINT_PRIVIEW = "打印预览";
    /* Print menu in file menu */
     String APPOINTMENT_FILE_PRINT = "打印";
    /* Properties menu in file menu */
     String APPOINTMENT_FILE_PROPERTIES = "属性";
    /* Close menu in file menu */
     String APPOINTMENT_FILE_CLOSE = "关闭";
    /* meeting request menu item in file-new menu */
    
    // edit menu */
    /* Undo menu in edit menu */
     String APPOINTMENT_EDIT_UNDO = "Undo";
    /* Redo menu in edit menu */
     String APPOINTMENT_EDIT_REDO = "Redo";
    /* Cut menu in edit menu */
     String APPOINTMENT_EDIT_CUT = "剪切";
    /* Copy menu in edit menu */
     String APPOINTMENT_EDIT_COPY = "拷贝";
    /* Paste menu in edit menu */
     String APPOINTMENT_EDIT_PASTE = "粘贴";
    /* Paste Special menu in edit menu */
     String APPOINTMENT_EDIT_PASTE_SPECIAL = "选择性粘贴";
    /* Clear menu in edit menu */
     String APPOINTMENT_EDIT_CLEAR = "清除";
    
    /* Select All menu in edit menu */
     String APPOINTMENT_EDIT_SELECT_ALL = "全选";
    /* Mark as Unread menu in edit menu */
     String APPOINTMENT_EDIT_MAEK_AS_UNREAD = "标记为未读";
    /* Find Next menu in edit menu */
     String APPOINTMENT_EDIT_FIND_NEXT = "查找下一处";
    /* Object menu in edit menu */
     String APPOINTMENT_EDIT_OBJECT = "object";
    /* View Menu*/
    /* Find Next menu in View menu */
     String APPOINTMENT_VIEW_FIND_PREVIOUS = "Previous";
    /* Object menu in edit menu */
     String APPOINTMENT_VIEW_NEXT = "Next";
    /* Find Next menu in edit menu */
     String APPOINTMENT_VIEW_FIND_CALENDAR = "日历...";
    /* Object menu in edit menu */
     String APPOINTMENT_VIEW_TOOLBARS = "工具条";
    /* Insert Menu*/
    /* Find Next menu in Insert menu */
     String APPOINTMENT_INSERT_FILE = "文件";
    /* Object menu in Insert menu */
     String APPOINTMENT_INSERT_ITEM = "Item";
    /* Find Next menu in Insert menu */
     String APPOINTMENT_INSERT_OBJECT = "Object...";
    /* Object menu in Insert menu */
    /* Format Menu*/
    /* Font menu in Format menu */
     String APPOINTMENT_FORMAT_FONT = "字体...";
    /* Paragraph menu in Format menu */
     String APPOINTMENT_FORMAT_PARAGRAPH = "段落...";
    /* Tools Menu*/
    /* spell menu in Tools menu */
     String APPOINTMENT_TOOLS_SPELLING = "拼写...";
    /* spell menu in Tools menu */
     String APPOINTMENT_TOOLS_CHECK_NAMES = "检查名字";
    /* spell menu in Tools menu */
     String APPOINTMENT_TOOLS_ADDRESS_BOOK = "地址簿...";
    /* spell menu in Tools menu */
     String APPOINTMENT_TOOLS_FORMS = "Forms";
    /* spell menu in Tools menu */
     String APPOINTMENT_TOOLS_MACRO = "宏";
    /* spell menu in Tools menu */
     String APPOINTMENT_TOOLS_CUSTOMIZE = "自定义...";
    /* Actions Menu*/
    /* spell menu in Actions menu */
     String APPOINTMENT_ACTIONS_NEW_APPOINTMENT = "新约会";
    /* spell menu in Actions menu */
     String APPOINTMENT_ACTIONS_RECURRENCE = "Recurrence";
    /* spell menu in Actions menu */
     String APPOINTMENT_ACTIONS_INVITE_ATTENDEES = "Invite Attendees";
    /* spell menu in Actions menu */
     String APPOINTMENT_ACTIONS_CANCEL_INVITATION = "Cancel Invitation";
    /* spell menu in Actions menu */
     String APPOINTMENT_ACTIONS_FORWARD_AS_ICALENDAR = "Forward as iCalendar";
    /* spell menu in Actions menu */
     String APPOINTMENT_ACTIONS_FORWARD = "Forward";
    /* Help Menu*/
    /* Microsoft Outlook Help menu in Help menu */
     String APPOINTMENT_HELP_MICROSOFT_OUTLOOK_HELP = "PIM帮助";
    /* Microsoft Outlook Help menu in Help menu */
     String APPOINTMENT_HELP_SHOW_THE_OFFICE_ASSISTANT = "显示助手";
    /* Microsoft Outlook Help menu in Help menu */
     String APPOINTMENT_HELP_WHAT_IS_THIS = "这是什么";
    /* Microsoft Outlook Help menu in Help menu */
     String APPOINTMENT_HELP_OFFICE_ON_THE_WEB = "网上资源";
    /* Microsoft Outlook Help menu in Help menu */
     String APPOINTMENT_HELP_DETECT_AND_REPAIR = "检测和修复...";
    /* Microsoft Outlook Help menu in Help menu */
     String APPOINTMENT_HELP_ABOUT_MICROSOFT_OUTLOOK = "关于PIM";
    
}

