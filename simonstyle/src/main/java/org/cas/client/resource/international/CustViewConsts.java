package org.cas.client.resource.international;

/***/
public interface CustViewConsts
{
    //自定义视图面板上的
    //以下两个用于组合对话盒标题
    public static final String DEFINE = "定义";
    public static final String VIEW = "视图";
    
    //以下用于组合第一个最上面的Elabel
    public static final String FOLDER_OF = "的视图";
    public static final String INCLUD_DELED = "包含删除项";
    //所有项目
    public static final String ALL_ITEMS = "所有项目";
    //任何类型的PIM项目
    public static final String ALL_PIM_ITEMS = "任何类型的PIM项目";
    
    // 先 8 个吧
//    public static final String [] TABLE_TYPES = {CALENDAR,TASK,CONTACTS,DIARY,INBOX,OUTBOX,
//    SENDEDBOX,DRAFTBOX/*,DELETED_ITEM*/};
    //修理数组。这里是 TABLE_SUBVIEW_COUNT 的各个内容。
    //marked 06/05/03临时调试用的，因为系统改为动态，所以第一个不一定再是约会，素衣出现数组越界异常。
    //@TODO：稍候根据Model性能实验结果，确定model的做法，最终考虑是否将其相干的菜单项去掉算了。
    int  TABLE_SUBVIEW_COUNT_0 = 3;//9;
    int  TABLE_SUBVIEW_COUNT_1 = 3;//9;
    int  TABLE_SUBVIEW_COUNT_2 = 3;//6;
    int  TABLE_SUBVIEW_COUNT_3 = 3;//3;
    int  TABLE_SUBVIEW_COUNT_4 = 3;//7;
    int  TABLE_SUBVIEW_COUNT_5 = 3;//7;
    int  TABLE_SUBVIEW_COUNT_6 = 3;//7;
    int  TABLE_SUBVIEW_COUNT_7 = 3;//7;
    /** 修理数组。请核对：数组 TABLE_SUBVIEW_COUNT 总长度是 8，修理长度是 8。 */
    int []  TABLE_SUBVIEW_COUNT = {TABLE_SUBVIEW_COUNT_0, TABLE_SUBVIEW_COUNT_1, TABLE_SUBVIEW_COUNT_2, TABLE_SUBVIEW_COUNT_3, TABLE_SUBVIEW_COUNT_4, 
        TABLE_SUBVIEW_COUNT_5, TABLE_SUBVIEW_COUNT_6, TABLE_SUBVIEW_COUNT_7};

    //以下用于按钮
    //
    public static final String CREATE_BTN = "新建(N)...";
    public static final String FIELDS_BTN = "字段(F)...";
    public static final String SORT_BTN = "排序(S)...";
    public static final String FILTER_BTN = "筛选(L)...";
    public static final String SET_FORMATS_BTN = "设置格式(A)...";
    public static final String RESTORE_DEFAULTS_BTN = "恢复默认值(R)";
    
    public static final String APPLY = "应用";
    public static final String CLOSE = "关闭";
    //
    public static final String OK = "确定";
    public static final String CANCEL = "取消";
    
    public static final String NULL = "无";
    //
    public static final String DESCRIPTION = "描述";
    
    //
    public static final String FILEDS_LABEL = "字段";
    public static final String SORT_LABEL = "排序";
    public static final String FILTER_LABEL = "筛选";
	
    //用于表格
    public static final String VIEW_NAME = "视图名称";
    public static final String APPLY_RANGE = "应用范围";
    public static final String VIEW_TYPE = "视图类型";
    
    //
    public static final String CURRENT_VIEW_SETS ="<当前视图设置>";
    public static final String CURRENT_VIEW_APPSCOPE  ="该文件夹";
    //
    public static final String ALL = "全部";
    
    //定义视图类型 与util 下 interface ModelConstants 对应
    public static final String TABLE_TYPE= "表格";    
    public static final String CARD_TYPE = "卡片式";
    public static final String DAY_WEEK_MONTH = "天/周/月";
    public static final String ICON_VIEW = "图标";
    public static final String TIMELINE_VIEW = "文本";
    
    //
    public static final String [] TABLE_TITLES = {VIEW_NAME,APPLY_RANGE,VIEW_TYPE};
    
    //
    public static final String [] VIEW_TYPES = {TABLE_TYPE,CARD_TYPE,ICON_VIEW,DAY_WEEK_MONTH,TIMELINE_VIEW};
    
    //用于新建按钮
    public static final String CREATE_NEW_VIEW = "创建新视图";
    public static final String NEW_VIEW_NAME = "新视图名称(N)";
    public static final String FIELD_TEXT = "新视图";
    public static final String VIEW_TYPE_LABEL = "视图类型(V)";
    public static final String APPLY_SCOPE = "应用范围";

    public static final String EVERYBODY_CAN_WATCH = "任何人都可看到该文件夹(T)";
    public static final String ONLY_MYSELF_CAN_WATCH = "只有本人能够看到该文件夹(F)";
    public static final String ALL_FOLDERS = "文件夹(A)";    
    //用于字段按钮
    public static final String FIELD_DIALOG_TITLE = "显示字段";
    public static final String USERABLE_FIELDS = "可用字段(V)";
    public static final String SHOW_IN_THIS_SEQUENCE = "按此顺序显示这些字段(D)";

    //
    public static final String MOVE_UP = "上移(U)";
    public static final String MOVE_DOWN = "下移(D)";
    //
    public static final String ADD_ONE = "添加(A)->";
    public static final String DELETE = "<-删除(C)";
    
    //用于排序按钮
    //
    public static final String SORT = "排序";
    //
    public static final String SORT_GIST = "排序依据(S)";
    public static final String SECOND_GIST = "第二依据(T)";
    public static final String THIRD_GIST = "第三依据(B)";
    //
    public static final String ASCENT = "升序";
    public static final String DESCENT = "降序";
    //
    public static final String WARNIN = "请选择不相同的排序标准!";
    
    public static final String [] SORTOR_TABLE = {ASCENT,DESCENT};
    //
    public static final String CLEAR_ALL = "全部清除(C)";
    
    public static final String FIND_TITLE = "选择条件";
    //用于筛选按钮
    public static final String FILTER = "筛选...";
    //
    public static final String FIND = "查找(K):";
    //
    public static final String MAIL = "邮件";
    public static final String FILE = "文件";
    public static final String USER = "用户";
    //
    public static final String [] FIND_OBJECTS = {MAIL,FILE,USER};
    
    public static final String LOCATION = "位置:";
    //按钮
    public static final String BROWSE = "浏览(B)...";
    public static final String BEGIN_TO_SEARCH = "开始查找(S)";
    public static final String STOP = "停止(P)";
    public static final String NEW_RESEARCH = "新搜索(N)";
    
    //普通tabpane
    public static final String NORMAL = "便捷";
    public static final String SEARCH_TEXT = "查找文字(C):";
    public static final String LOCATE = "位置(I):";
    //
    public static final String ONLY_THEME = "仅主题";
    public static final String ONLY_ACCESSORY = "仅附件";
    public static final String BOTH_TWO = "两者都有";
    
    //
    public static final String [] LOCATION_ITEMS = {ONLY_THEME,ONLY_ACCESSORY,BOTH_TWO};
    
    //
    public static final String SENDER = "联系人(R)...";
    public static final String RECEIVER = "联系人(O)...";
    
    public static final String TIME = "时间(M)";
    
    //高级tabpane
    public static final String ADVANCE = "通用";
    //
    public static final String SERARCH_BY_THIS_ITEMS = "按下列条件查找项目:";
    public static final String NONE_ITEMS = "<将标准从下面位置添加到该列表中>";
    public static final String PERSONAL_FOLDER = "个人文件夹";
    
    public static final String ADD_TO_LIST = "添至列表(D)";
    public static final String DELETE_BTN = "删除(R)";
    public static final String FIELD_ITEM = "字段(I)";
    public static final String CONDITION_ITEM = "条件(C)";
    public static final String VALUE_ITEM = "值(U)";
    
    public static final String EQUAL = "等于";
    public static final String AND = "与(N)";
    public static final String OR = "或(R)";
    
    public static final String OPEN_SEARCH_CONDITION = "打开搜索条件(O)...";
    public static final String SAVE_SEARCH_CONDITION = "保存搜索条件(S)...";
    
    //用于设置格式按钮
    public static final String SET_FORMAT_TITLE = "设置格式";
    
    public static final String MODE = "样式:";
    public static final String PREVIEW = "预览:";
    public static final String FONTS = "字体(F)...";
    /** 修理数组。这里是 EMAIL_FONTS 的各个内容。*/
    String  EMAIL_FONTS_0 = "未读邮件";
    String  EMAIL_FONTS_1 = "未发送的邮件";
    String  EMAIL_FONTS_2 = "到期的电子邮件";
    String  EMAIL_FONTS_3 = "过期的电子邮件";
    /** 修理数组。请核对：数组 EMAIL_FONTS 总长度是 4，修理长度是 4。 */
    String []  EMAIL_FONTS = {EMAIL_FONTS_0, EMAIL_FONTS_1, EMAIL_FONTS_2, EMAIL_FONTS_3};

    /** 修理数组。这里是 CONTACT_FONTS 的各个内容。*/
    String  CONTACT_FONTS_0 = "通讯组";
    String  CONTACT_FONTS_1 = "未读的联系人";
    String  CONTACT_FONTS_2 = "到期的联系人";
    String  CONTACT_FONTS_3 = "过期的联系人";
    /** 修理数组。请核对：数组 CONTACT_FONTS 总长度是 4，修理长度是 4。 */
    String []  CONTACT_FONTS = {CONTACT_FONTS_0, CONTACT_FONTS_1, CONTACT_FONTS_2, CONTACT_FONTS_3};

    /** 修理数组。这里是 TASK_FONTS 的各个内容。*/
    String  TASK_FONTS_0 = "已完成但未读的任务";
    String  TASK_FONTS_1 = "已完成并已阅读的任务";
    String  TASK_FONTS_2 = "过期任务";
    String  TASK_FONTS_3 = "未读任务";
    /** 修理数组。请核对：数组 TASK_FONTS 总长度是 4，修理长度是 4。 */
    String []  TASK_FONTS = {TASK_FONTS_0, TASK_FONTS_1, TASK_FONTS_2, TASK_FONTS_3};

    /** 修理数组。这里是 CALENDAR_FONTS 的各个内容。*/
    String  CALENDAR_FONTS_0 = "未读的项目";
    String  CALENDAR_FONTS_1 = "过期项目";
    /** 修理数组。请核对：数组 CALENDAR_FONTS 总长度是 2，修理长度是 2。 */
    String []  CALENDAR_FONTS = {CALENDAR_FONTS_0, CALENDAR_FONTS_1};

    //用于恢复默认值按钮
    //待续...
    //查找对话盒    
//    String DIARY_ITEM = "手稿条目";
    String NO_DISPLAY = "该视图中没有项目可显示。";
    String CLEAR_SEARCH = "这将清除当前的搜索。";
    
    //
    String CONTAINS_SUB_FOLDER = "(包含子文件夹)";
    //时间选项
    
    public static final int STRING_TYPE = 0; //字符串类型
    public static final int NUM_TYPE = 1; //数字类型
    public static final int TIME_TYPE = 2; //时间类型
    public static final int BOOL_TYPE = 3; //布尔类型
    
    //------------------------------------------------------------
    public static final String TIME_CON_NONE = "无"; 
    public static final String TIME_CON_RECEIVE = "接时间"; 
    public static final String TIME_CON_SEND = "发送时间";
    public static final String TIME_CON_MATURE = "到期";
    public static final String TIME_CON_OVERDUE = "过期";
    public static final String TIME_CON_CREATE = "创建时间";
    public static final String TIME_CON_MODIFY = "修改时间";
    //时间值
    public static final String TIME_VA_ANYTIME = "任何时间";
    public static final String TIME_VA_YESTODAY = "昨天";
    public static final String TIME_VA_TODAY = "今天";
    public static final String TIME_VA_SEVEN = "最近7天";
    public static final String TIME_VA_LASTWEEK = "上周";
    public static final String TIME_VA_THISWEEK = "本周";
    public static final String TIME_VA_LASTMONTH = "上个月";
    public static final String TIME_VA_THISMONTH = "本月";
    //约会、会议..位置
    public static final String LOCAL_TOPIC = "仅主题字段";
    public static final String LOCAL_TEXT = "常用文本字段";
    public static final String CON_IN = "在";
    public static final String CON_EXIST = "存在";
    public static final String CON_NO_EXIST = "不存在";
    public static final String CON_BETWEEN = "介于";
    public static final String CON_EQUAL = "等于";
    public static final String CON_NO_EQUAL = "不等于";
    
    //数字条件
    public static final String NUM_CON_AT_MORE = "至多";
    public static final String NUM_CON_AT_LESS = "至少";
    public static final String NUM_CON_MORE = "多于";
    public static final String NUM_CON_LESS = "少于";
    //字符串条件
    public static final String STR_CON_CONTAIN = "包含";
    public static final String STR_CON_YES = "是";
    public static final String STR_CON_NOCONTAIN = "不包含";
    public static final String STR_CON_NULL = "为空";
    public static final String STR_CON_NONENULL = "非空";
    //时间条件
    public static final String TIME_CON_TOMORROW = "明天";
    public static final String TIME_CON_NEXT_SEVEN = "随后7天";
    public static final String TIME_CON_NEXT_WEEK = "下周";
    public static final String TIME_CON_NEXT_MONTH = "下个月";
    public static final String TIME_CON_NOEARLY = "不早于";
    public static final String TIME_CON_NOLATE = "不晚于";
    //boolean 值
    public static final String BOOL_VA_YES = "是";
    public static final String BOOL_VA_NO = "不是";
    //时间选项
    public static final String[] TIME_OPTION = 
    {
        TIME_CON_NONE,
        TIME_CON_RECEIVE,
        TIME_CON_SEND,
        TIME_CON_MATURE,
        TIME_CON_OVERDUE,
        TIME_CON_CREATE,
        TIME_CON_MODIFY
    };
    
    //时间选项的值
    public static final String[] TIME_VALUE =
    {
        TIME_VA_ANYTIME,
        TIME_VA_YESTODAY,
        TIME_VA_TODAY,
        TIME_VA_SEVEN,
        TIME_VA_LASTWEEK,
        TIME_VA_THISWEEK,
        TIME_VA_LASTMONTH,
        TIME_VA_THISMONTH
    };

    /**以下为ConditionComboBox的条件值*/
    //数字条件
    public static final String[] NUM_CONDITION = 
    {
        CON_EQUAL,
        CON_NO_EQUAL,
        NUM_CON_AT_MORE,
        NUM_CON_AT_LESS,
        NUM_CON_MORE,
        NUM_CON_LESS,
        CON_BETWEEN,
        CON_EXIST,
        CON_NO_EXIST
    };
    //字符串条件
    public static final String[] STR_CONDITION =
    {
        STR_CON_CONTAIN,
        STR_CON_YES,
        STR_CON_NOCONTAIN,
        STR_CON_NULL,
        STR_CON_NONENULL
    };
    //布尔条件
    public static final String[] BOOL_CONDITION = 
    {
        CON_EQUAL,
        CON_NO_EQUAL,
        CON_EXIST,
        CON_NO_EXIST
    };
    //布尔值
    public static final String[] BOOL_VALUE = 
    {
        BOOL_VA_YES,
        BOOL_VA_NO
    };
    //时间条件
    public static final String[] TIME_CONDITION =
    {
        TIME_VA_ANYTIME,
        TIME_VA_YESTODAY,
        TIME_VA_TODAY,
        TIME_CON_TOMORROW,
        TIME_VA_SEVEN,
        TIME_CON_NEXT_SEVEN,
        TIME_VA_LASTWEEK,
        TIME_VA_THISWEEK,
        TIME_CON_NEXT_WEEK,
        TIME_VA_LASTMONTH,
        TIME_VA_THISMONTH,
        TIME_CON_NEXT_MONTH,
        CON_IN,
        TIME_CON_NOEARLY,
        TIME_CON_NOLATE,
        CON_BETWEEN,
        CON_EXIST,
        CON_NO_EXIST
    };
    
    //特殊字段
    public static final String [] SPEC_FIELD = 
    {
        "重要性", "敏感度", "图标"
    };
    //
    public static final int MAX_FIELD_INTERVAL = 16;
    //
    public static final int MAX_CONDI_INTERVAL = 13;
    //查找状态栏中显示的符合条件的结果项
    public static final String ITEM = "项"; 
    //
    public static final String ELSE = "否";
}
