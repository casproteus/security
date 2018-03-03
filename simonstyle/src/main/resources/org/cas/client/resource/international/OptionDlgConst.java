package org.cas.client.resource.international;

/***/
public interface OptionDlgConst
{
    /**********************************
     ******* 选 项 对 话 盒 ***********
     **********************************/    
    String OPTION_MAIL = "邮件";
    String OPTION_CALENDAR = "日历";
    String OPTION_TASK = "任务";
    String OPTION_CONTACT = "联系人";
    String OPTION_DIARY = "手稿";
    String OPTION_OTHER = "其他";
    
    /***********************************
     ********* DlgChangePassword *******
     ***********************************/
    String OPTION_CHANGE_PASSWORD = "更改密码...";
    String OPTION_NEW_PASSWORD = "新密码(N)";
    String OPTION_CONFIRM_PASSWORD = "确认密码(C)";
    
    /***********************************
     ********* EmailOptionDialog *******
     ***********************************/
    String OPTION_EMAIL_OPTION = "电子邮件选项";
    String OPTION_EMAIL_DISPOSE = "邮件处理";
    String OPTION_ADVANCE = "高级";
    String OPTION_TRACK = "跟踪";
    
    /***********************************
     ********* TimeZoonDialog **********
     ***********************************/
    String OPTION_TIMEZONE = "时区";
    String OPTION_DISLAY_APPEND_ZONE = "显示附加时区(S)(会在日历视图中显示两个时间)";
    String OPTION_CHANGE_ZONE = "交换时区(W)";
    String OPTION_CURRENT_ZONE = "当前时区";
    String OPTION_LABEL = "标签(L)";
    String OPTION_ZONE = "时区(Z)";
    String OPTION_CURRENT_TIME = "当前时间(T)";
    String OPTION_ADJUST_TIME = "为夏令时调整时间(D)";
    String OPTION_LABEL_APPEND = "标签(B)";
    String OPTION_ZONE_APPEND = "时区(O)";
    String OPTION_ADJUST_TIEM_APPEND = "为夏令时调整时间(U)";    
    
    //-----  TabCalendar ------//
    String OPTION_WORKDAY = "工作日";
    String [] WEEK = 
    {
        "星期日","星期一","星期二","星期三","星期四","星期五","星期六"
    };
    
    String OPTION_FIRST_DAY_OF_WEEK = "一周的第一天(E)";
    String OPTION_FIRST_WEEK_OF_YEAR = "一年的第一周(Y)";
    String OPTION_BACKGROUND = "背景颜色(B)";
    String OPTION_AM = "上午:";
    String OPTION_PM = "下午:";
    String OPTION_START_AM_TIME = "开始时间(S)";
    String OPTION_END_AM_TIEM = "结束时间(N)";
    String OPTION_START_PM_TIME = "开始时间(B)";
    String OPTION_END_PM_TIME = "结束时间(F)";
    String OPTION_GUISE = "外观";
    String OPTION_DIAPLAY_WEEKS = "在日期选择区中显示周数(K)";
    String OPTION_DEAL_MEETING = "处理会议";
    String OPTION_RECE_MEETING = "自动接收会议并处理取消通知(M)";
    String OPTION_REFUSE_MEETING = "自动谢绝冲突的会议要求(C)";
    String OPTION_REF_RE_MEETING = "自动谢绝重复的会议要求(A)";
    String [] YEAR_DATA =
    {
        "开始于一月一日","第一个满四天的周","第一个全周"
    };
    
    //------- TabEmailAdvance --------//
    String OPTION_SAVE_EMAIL = "保存邮件";
    String OPTION_NO_SEND_MAIL_ADDRESS = "未发送的邮件保存位置(U)";
    String OPTION_HAVE_SEND_MAIL_ADDRESS = "已发送的邮件保存位置(S)";
    String OPTION_CONFIG_IMPORTANCE = "设置重要性(I)";
    String OPTION_CONFIG_TENDMESS = "设置敏感度(T)";
    String OPTION_WHEN_SEND_MAIL = "发送邮件时";
    String OPTION_CHECK_NAME = "自动检查姓名(N)";
    String OPTION_DELETE_MEETINGL = "应答时从收件箱删除会议要求(M)";
    String [] SEND_DATE =
    {
        "收件箱","发件箱","草稿","已发送的邮件"
    };
    
    //------- TabEmailManage ---------//
    String OPTION_CLOSE_MAIL = "在答复和转发时关闭原始邮件(C)";
    String OPTION_SAVE_COPY = "在”已发送的邮件“文件夹中保留副本(V)";
    String OPTION_REMIND_MAIL_RECEIVED = "新邮件到达时给出通知(N)";
    String OPTION_ANSWER_RETURN = "答复和转发";
    String OPTION_WHEN_ANSWERED = "答复邮件时(R)";
    String OPTION_WHEN_RETURN = "转发邮件时(F)";
    String OPTION_WHEN_EDITED = "编辑邮件时标记修订(M)";
    String [] COMBO_DATA =
    {
        "附加原始邮件","包含原始邮件"
    };
    
    //--------- TabEmailTrack -----------//
    String OPTION_DIALOG = "请求\"已读\"回执";
//    String OPTION_STRACK_ADDRESSER = "用这些选项跟踪收件人收到邮件的日期和时间";
    String OPTION_STRACK_ADDRESSER = "使用此选项确认收件人是否已某一邮件";
    String OPTION_DEAL_RECEIPT = "在到达时处理回执(C)";
    String OPTION_DEAL_MOVED = "处理后将回执移至(M)";
//    String OPTION_SEND_RECEIPT_QUEST = "请在阅读所有邮件后给出”已读“回执的请求(R)";
    String OPTION_SEND_RECEIPT_QUEST = "所有发送的邮件都要求\"已读\"回执(R)";
//    String OPTION_RECEIPT = "处理回执";
    String OPTION_RECEIPT = "响应\"已读\"回执";
//    String OPTION_SEND_RESPONSE = "问题发送响应(A)";
    String OPTION_SEND_RESPONSE = "总是发送响应(A)";
    String OPTION_NEVER_SEND_RESPONSE = "从不发送响应(N)";
    String OPTION_REMIND_ME = "发送响应前向我询问(K)";
    String [] TRACK_DATA  =
    {"收件箱","发件箱","草稿","已发送的邮件","已删除的邮件"};
    
    //--------- TabLinkMan ----------//
    String OPTION_NEW_CONTACT_OPTION = "新联系人的姓名和档案选项";
    String OPTION_SELECTED_GRADATION = "选择PIM使用新名称的顺序";
    String OPTION_DEFAULT_ALLNAME_GRADATION = "默认的全名顺序(N)";
    String OPTION_DEFAULT_DIAPLAY_GRADATION = "默认的表示为顺序(X)";
    String OPTION_SAVE_ATTACH_DEFAULT_CONFIG = "选择存档联系人的默认设置";
    String OPTION_AUTO_ADD_ANSWER = "自动加进要答复的人(U)";
    String OPTION_SEND_MAIL_AS_CARD = "发送邮件时作为CARD发送(C)";
    String [] DEFAULT_DATA =
    {
        "姓氏,职务 名字",
        "姓氏名字 职务",
        "名字,姓氏(单位)",
        "名字姓氏(单位)",
        "单位(名字,姓氏)",
        "姓氏名字",
        "名字姓氏",
        "单位 姓氏",
        "姓氏",
        "名字",
        "单位"
    };
    
    //----------- TabMail --------------//
    String OPTION_EMAIL = "电子邮件";
    String OPTION_CHANGE_MAIL_POSITION = "更改邮件的位置和处理方法";
    String OPTION_EMAIL_OPTION_MAIL = "电子邮件选项(M)";
    String OPTION_MAIL_ACCOUNT_OPTION = "邮件帐号选项";
    String OPTION_CONTROL_SEND_RECEIVE = "控制PIM何时接收和发送邮件。";
    String OPTION_OPENTYPE = "以系统默认邮件客户端浏览邮件(D)";
    String OPTION_CONNECT_SEND = "连接后立即发送邮件(S)";
    String OPTION_PER = "每隔(N)";
    String OPTION_CHECK_NEWMAIL = "分钟检查新邮件";
    String OPTION_MAIL_FORMAT = "邮件格式";
    String OPTION_SEND_FORMAT = "以该邮件格式发送(O)";
    String OPTION_CONFIG = "设置";
    
    //----------- TabNote -------------//
    String OPTION_DIARY_FORMAT = "手稿格式";
    String OPTION_FONT_STYLE = "字形(F)";
    String OPTION_FONT_COLOR = "字体颜色(C)";
    String OPTION_FONT_BACKGROUND = "背景(K)";
    String OPTION_CURRENT_PASSWORD = "当前密码(P)";
    String OPTION_FONT_SIZE = "字号(Z)";
    String OPTION_PASSWORD_OPTION = "密码选项";
    String OPTION_CHANGE_PASSWORDS = "修改密码(M)";
    
    //----------- TabOrther -----------//
    String OPTION_NORMAL = "常规";
    String OPTION_EMPTY_DELETE_ITEM = "退出时清空[已删除项]文件夹(E)";
    String OPTION_START_GOTO = "启动时定位到(S)";
    String OPTION_WARN_DELFOREVER = "永久删除项目前提出警告(B)";
    String OPTION_SAVE_ATTACH = "存档";
    String OPTION_ALL_PATH = "所有项目的存档路径,并清空[已删除项]文件夹";
    String OPTION_DEFAULT_PATH = "存档路径默认(F)";
    String OPTION_BROUNS = "浏览(W)";
    String OPTION_BROUNS_FRAME = "预览窗格";
    String OPTION_SGIN_READED = "在预览窗口中将邮件标记[已读](M)";
    String OPTION_WAITING = "等待";
    String OPTION_AFTER_READED = "秒后将项目标记为[已读]";
    String OPTION_SELECTED_READED = "当选定内容更改时将项目标记为[已读](R)";
    String [] GOTO_DATA =
    {
        "收件箱","联系人","日历","任务","手稿"
    };
    
    //--------- TabTask -------------//
    String OPTION_COLOR_OPTION = "颜色选项";
    String OPTION_OVERDUE_TASK = "过期任务(O)";
    String OPTION_FINISHED_TASK = "已完成任务(C)";
    String OPTION_REMIND_TIME = "提醒时间(R)";
    String OPITON_ADVANCE_TASK = "高级任务";
    String OPTION_CONFIG_REMIND_FOR_HAVE_ENDTIME = "给带有截至日期的任务设置提醒(T)";
    String OPTION_SAVE_LIST_COPY = "在任务列表尚保留已分配任务的更新副本(K)";
    String OPTION_ASSGIN_TASK_SEND_REPORT = "分配的任务完成后发送状态报告(S)";
    
    String [] zoneData =
    {
        "(GMT+04:30) 喀布尔",
        "(GMT-09:00) 阿拉斯加",
        "(GMT+03:00) 科威特，利雅得",
        "(GMT+04:00) 阿布扎比，马斯喀特",
        "(GMT+03:00) 巴格达",
        "(GMT-04:00) 大西洋时间(加拿大)",
        "(GMT+09:30) 达尔文",
        "(GMT+10:00) 堪培拉，墨尔本，悉尼",
        "(GMT-01:00) 亚速尔群岛",
        "(GMT-06:00) 萨斯喀彻温",
        "(GMT-01:00) 佛得角群岛",
        "(GMT+04:00) 巴库，第比利斯， 埃里温",
        "(GMT+09:30) 阿德莱德",
        "(GMT-06:00) 中美洲",
        "(GMT+06:00) 阿斯塔纳，达卡",
        "(GMT+01:00) 贝尔格莱德，布拉迪斯拉发，布达佩斯，卢布尔雅那，布拉格",
        "(GMT+01:00) 萨拉热窝，斯科普里，维尔纽斯，索非亚，华沙，萨格勒布",
        "(GMT+11:00) 马加丹，索罗门群岛，新喀里多尼亚",
        "(GMT-06:00) 中部时间(美国和加拿大)",
        "(GMT+08:00) 北京，重庆，香港特别行政区，乌鲁木齐",
        "(GMT-12:00) 埃尼威托克，夸贾林岛",
        "(GMT+03:00) 内罗毕",
        "(GMT+10:00) 布里斯班",
        "(GMT+02:00) 布加勒斯特",
        "(GMT-03:00) 巴西利亚",
        "(GMT-05:00) 东部时间(美国和加拿大)",
        "(GMT+02:00) 开罗",
        "(GMT+05:00) 叶卡捷琳堡",
        "(GMT+12:00) 富士，堪察加半岛，马绍尔群岛",
        "(GMT+02:00) 赫尔辛基，里加，塔林",
        "(GMT) 格林威治平时; 都柏林, 爱丁堡, 伦敦, 里斯本",
        "(GMT-03:00) 格陵兰",
        "(GMT) 蒙罗维亚，卡萨布兰卡",
        "(GMT+02:00) 雅典，伊斯坦布尔，明斯克",
        "(GMT-10:00) 夏威夷",
        "(GMT+05:30) 加尔各答，马德拉斯，孟买，新德里",
        "(GMT+03:30) 德黑兰",
        "(GMT+02:00) 耶路撒冷",
        "(GMT+09:00) 汉城",
        "(GMT-06:00) 墨西哥城",
        "(GMT-02:00) 中大西洋",
        "(GMT-07:00) 山地时间(美国和加拿大)",
        "(GMT+06:30) 仰光",
        "(GMT+06:00) 阿拉木图，新西伯利亚",
        "(GMT+05:45) 加德满都",
        "(GMT+12:00) 惠灵顿，奥克兰",
        "(GMT-03:30) 纽芬兰",
        "(GMT+08:00) 伊尔库茨克，乌兰巴托",
        "(GMT+07:00) 克拉斯诺亚尔斯克",
        "(GMT-04:00) 圣地亚哥",
        "(GMT-08:00) 太平洋时间(美国和加拿大)；蒂华纳",
        "(GMT+01:00) 布鲁塞尔，哥本哈根，马德里，巴黎",
        "(GMT+03:00) 莫斯科，圣彼得堡, 喀山,伏尔加格勒",
        "(GMT-03:00) 布宜诺斯艾利斯，乔治敦",
        "(GMT-05:00) 波哥大，利马",
        "(GMT-04:00) 加拉加斯，拉巴斯",
        "(GMT-11:00) 中途岛，萨摩亚群岛",
        "(GMT+07:00) 曼谷，雅加达，河内",
        "(GMT+08:00) 吉隆坡，新加坡",
        "(GMT+02:00) 哈拉雷，比勒陀利亚",
        "(GMT+06:00) 斯里哈亚华登尼普拉",
        "(GMT+08:00) 台北",
        "(GMT+10:00) 霍巴特",
        "(GMT+09:00) 东京，大坂，札幌",
        "(GMT+13:00) 努库阿洛法",
        "(GMT-05:00) 印地安那(东)",
        "(GMT-07:00) 亚利桑那",
        "(GMT+10:00) 符拉迪沃斯托克",
        "(GMT+08:00) 珀斯",
        "(GMT+01:00) 中非西部",
        "(GMT+01:00) 阿姆斯特丹，柏林，伯尔尼，罗马，斯德哥尔摩，维也纳",
        "(GMT+05:00) 伊斯兰堡，卡拉奇，塔什干",
        "(GMT+10:00) 关岛，莫尔兹比港，符拉迪沃斯托克",
        "(GMT+09:00) 雅库茨克"
    };
}
