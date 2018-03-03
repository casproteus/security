package org.cas.client.resource.international;


/***/
public interface MailRuleConstant
{
    //邮件规则主视图文字常量
    public static final String MAIN_TITLE = "邮件规则";
    public static final String BELOW_RULE = "以下列顺序应用规则(A):";
    public static final String NEW_BTN = "新建(N)...";
    public static final String MODIFY_BTN = "修改(M)...";
    public static final String DELETE_BTN = "删除(D)";
    public static final String UP_BTN = "上移(U)";
    public static final String DOWN_BTN = "下移(W)";
    public static final String RUN_BTN = "立即运行(R)...";
    public static final String OK_BTN = "确定";
    public static final String CANCEL_BTN = "取消";
    //新建邮件规则视图文字常量
    public static final String NEWRULE_TITLE = "新建邮件规则";
    public static final String RULE_TYPE = "选择规则类型:";
    public static final String RECEIVE_CHK = "邮件到达时检查(R)";
    public static final String SEND_CHK = "发送邮件后检查(S)";
    public static final String SPECIFY_TERM = "指定条件";
    public static final String FOUND = "创建:";
    public static final String TERM = "条件(C)";
    public static final String EXCEPTION = "例外(E)";
    public static final String PROPER_TERM = "当邮件到达时满足以下条件(O):";
    public static final String PROPER_TERM1 = "当发送邮件后满足以下条件(O):";
    public static final String SPECIFY_ACTION = "指定动作(I)";
    public static final String EXECUTE_ACTION = "执行以下动作(P):";
    public static final String RULE_NAME = "规则名称(N):";
    public static final String LAUNCH_RULE = "启用此规则(T)";
    public static final String RELATION_BTN = "联系人...";
    public static final String APPEND_BTN = "添加";
    public static final String ERASE_BTN = "删除";
    //立即运行规则视图文字常量
    public static final String RUNRULE_TITLE = "立即运行规则";
    public static final String SELECT_RUNRULE = "选择要运行的规则(R):";
    public static final String RUN_POSITION = "在此文件夹中运行:";
    public static final String INCLUDE = "包括子文件夹(S)";
    public static final String RULE_APPLY = "规则应用于(P):";
    public static final String EXPLORE_BTN = "浏览(B)...";
    public static final String OPERATE_BTN = "立即运行(O)";
    public static final String CLOSE_BTN = "关闭";
    public static final String REVISE_RULE_TITLE = "修改邮件规则";
    public static final String ALL_MAILS = "所有邮件";
    public static final String NO_READED_MAILS = "未读邮件";
    public static final String HAS_READED_MAILS = "已读邮件";
    
    public static final String []RULE_APPLYING = {ALL_MAILS, NO_READED_MAILS, HAS_READED_MAILS};
    //接收和发送中条件、例外的共有项
    public static final String ACCOUNTS = "接收邮件账号";
    public static final String MYNAME = "我的姓名";
    public static final String ADDRESSER = "发件人";
    public static final String ADDRESSEE = "收件人";
    public static final String TOPIC = "主题";
    public static final String TEXT = "正文";
    public static final String TOPIC_OR_TEXT = "主题或正文";
    public static final String ACTION = "动作标记";
    public static final String IMPORTANCE = "重要性标记";
    public static final String SENSE = "敏感度标记";
    public static final String CATEGORY = "类别";
    public static final String ATTACH = "是否带有附件";
    public static final String SIZE = "邮件大小";
    public static final String TIME = "接收时间";
    //接收和发送动作的共有项
    public static final String MOVE_TO = "将副本移动到";
    public static final String IMPORTANT = "重要性标记为";
    public static final String STOP_OTHER = "停止处理其他规则";
    public static final String CLASS = "分配类别为";
    public static final String ACT = "动作标记为";
    //接收邮件时条件选项(顺序不可变动）
    public static final String RECEIVE_TERM[] = new String []
    {
        ACCOUNTS,MYNAME,ADDRESSER,ADDRESSEE,TOPIC,TEXT,TOPIC_OR_TEXT,
        ACTION,IMPORTANCE,SENSE,CATEGORY,ATTACH,SIZE,TIME
    };
    //接收邮件时例外选项(顺序不可变动）
    public static final String RECEIVE_EXCEPTION[] = new String[]
    {
        "直接发送给我","只发送给我",ACCOUNTS,MYNAME,ADDRESSER,ADDRESSEE,TOPIC,
        TEXT,TOPIC_OR_TEXT,ACTION,IMPORTANCE,SENSE,CATEGORY,ATTACH,SIZE,TIME
    };
    //接收邮件时动作选项(顺序不可变动）
    public static final String RECEIVE_ACTION[] = new String[]
    {
        "将它移动到",MOVE_TO,"删除","永久删除","转寄","打印","特定消息通知",
        ACT,"清除邮件标记",CLASS,IMPORTANT,STOP_OTHER
    };
    //发送邮件时条件或例外选项(顺序不可变动）
    public static final String SEND_EXCEPTION_TERM[] = new String[]
    {
        ADDRESSEE,TOPIC,TEXT,TOPIC_OR_TEXT,IMPORTANCE,SENSE,CATEGORY,
        ATTACH,SIZE
    };
    //发送邮件时动作选项(顺序不可变动）
    public static final String SEND_ACTION[] = new String[]
    {
        MOVE_TO,ACT,CLASS,IMPORTANT,"敏感度标记为",
        "被读取时通知我","送达时通知我","抄送给","推迟传递时间",STOP_OTHER
    };
    //
    public static final String PROPERTYS = "常规";
    public static final String CONDITION = "规则条件";
    public static final String ACTIONS = "执行动作";
    
    public static final String INCLUDING = "包含";
    public static final String NOINCLUDING = "不包含";
    public static final String YES = "是";
    public static final String NO = "不是";
    public static final String NOCAN= "否";
    public static final String LESS_FLG = "小于";
    public static final String MORE_FLG = "大于";
    public static final String LATER_FLG = "晚于";
    public static final String EARLY_FLG = "早于";
    public static final String EXIST = "在";
    public static final String NONE_EXIST = "不在";
    public static final String RECIPIENT = "收件人框中";
    public static final String CC = "抄送框中";
    public static final String RECIPIENT_OR_CC = "收件人框中";
    public static final String YES_NO[] = new String[]
    {
        YES,NO
    };
    public static final String LESS[] = new String[]
    {
        LESS_FLG, MORE_FLG
    };
    public static final String LATER[] = new String[]
    {
        LATER_FLG, EARLY_FLG
    };
    public static final String EXISTENCE[] = new String[]
    {
        EXIST, NONE_EXIST
    };
    public static final String INCLUDING_YES[] = new String[]
    {
        INCLUDING,NOINCLUDING,YES,NO
    };
    //动作标记
    public static final String ACTION_MARK[] = new String[]
    {
        "任何","呼叫","不转发","后续","仅供参考","转发","无须相应","阅读","答复",
        "答复所有人","校对"
    };
    /**/
    public static final int[] IMPORTANCE_VALUE = new int[]
    {
        1, 3, 5
    };
    /**/
    public static final int[] SENSE_VALUE = new int[]
    {
        0, 1, 2, 3
    };
    //分类
    public static final String SORT[] = new String[]
    {
        CategoryDialogConstants.VIP,
        CategoryDialogConstants.MATCH, CategoryDialogConstants.STRATEGY,
        CategoryDialogConstants.WAIT, CategoryDialogConstants.PHONE,
        CategoryDialogConstants.PERSON, CategoryDialogConstants.SUPPLIER,
        CategoryDialogConstants.VIEWPOINT, CategoryDialogConstants.INTERNATIONAL,
        CategoryDialogConstants.POSTCARD, CategoryDialogConstants.VACATION,
        CategoryDialogConstants.GIFT, CategoryDialogConstants.PURPOSE,
        CategoryDialogConstants.BUSINESS
    };
    public static final String INDRECT_FROWORD = "直接";
    public static final String ATTACH_FORWORD = "用附件形式";
    //转寄
    public static final String FORWARD[] = new String[]
    {
        INDRECT_FROWORD, ATTACH_FORWORD
    };
    //姓名在
    public static final String NAME[] = new String[]
    {
        RECIPIENT, CC, RECIPIENT_OR_CC
    };
    public static final String WARNING = "缺少设置值";
    public static final String WARNING_TITLE = "警告"; 
    public static final String NEWNAME = "新建邮件规则";
    public static final String USEDTERM = "这个条件您已使用过，请删除后重新设置！";
    public static final String AND = "并且";
    public static final String EXCLUSION = "除非";
    public static final String OR_EXCLUSION = "或除非";
    public static final String[] WEEK = new String[]
    {
        "星期日","星期一","星期二","星期三","星期四","星期五","星期六"
    };
    public static final String RECEIVE_WARNING = "该规则将应用于每个邮件(已收到的)。是否正确？";
    public static final String SEND_WARNING = "该规则将应用于每个邮件(已发送的)。是否正确？";
    public static final String ACT_WARNING = "此规则未指定要执行的动作。是否继续？";
}
