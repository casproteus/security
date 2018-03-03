package org.cas.client.resource.international;

/***/
public interface IPIMFieldConstant
{

    public static final String[] CONTACTS_FIELDS =
    {
	"FTP站点", "Internet闲-忙信息", "ISDN","Outlook版本", "Outlook内部版本",
	"TTY/TDD电话","Web页"
    };
    public static final String[] FIELDS_ZONE =
    {
	"常用字段","\"地址\"字段","\"电子邮件\"字段","\"传真/其他号码\"字段",
	"\"杂项\"字段","\"姓名\"字段","\"个人\"字段","\"电话号码\"字段",
	"所有\"联系人\"字段","文件夹中用户定义的字段","所有\"文档\"字段",
	"所有\"邮件\"字段","所有\"约会\"字段","所有\"任务\"字段",
	"所有\"手稿\"字段","所有\"公告\"字段","所有通讯组列表字段"
    };
    public static final String[][] FIELDS_NAME = 
    {
	//常用字段
	{"ISDN", "TTY/TDD 电话", "Web 页", "办公地点", "标记状态", "表示为", "部门", "车载电话", "单位", "单位主要电话"},
	//地址字段
	{"办公地点","部门","城市","地点","国家/地区","家庭住址 - 城市","家庭住址 - 国家(城市)","家庭住址 - 街道"},
	//电子邮件字段
	{"电子邮件","电子邮件 2","电子邮件 3"},
	//传真/其他号码字段
	{"FTP 站点","ISDN","电报","计算机网络名称","家庭传真"},
	//杂项字段
	{"FTP 站点","部门", "顾客标识符", "计算机网络名称", "联系人字段", "私有", "提醒", "提醒时间", "提醒主体"},
	//姓名字段
	{"表示为","称谓","经理姓名","名字","昵称","配偶","缩写","姓氏"}	
    };
    public static final int[][] FIELDS_TYPE = 
    {
	{0,0,0,0,11,0,0,0,0,0},
	{0,0,0,0,0,0,0,0},
	{0,0,0},
	{0,0,0,0,0},
	{0,0,0,0,0,0,0,0,0},
	{0,0,0,0,0,0,0,0}
    };
    public static final int[][] FIELDS_FORMAT = 
    {
	{100,100,100,100,100,100,100,100,100,100},
	{100,100,100,100,100,100,100,100},
	{100,100,100},
	{100,100,100,100, 100},
	{100,100,100,100,100,100,100,100,100},
	{100,100,100,100,100,100,100,100}
    };
    public static final String[][] CALENDAR_VIEW_FIELDS =
    {
	{"开始时间", "结束时间"},
	{"开始时间", "结束时间"},
	{"图标","附件","主题","姓氏","地点","开始时间","结束时间","定期模式","类别"},
	{"图标","附件","主题","地点","定期开始时间","持续时间","定期模式","类别"},
	{"图标","附件","主题","地点","定期开始时间","持续时间","定期模式","类别"},
	{"图标","附件","主题","地点","定期模式","定期开始时间","定期结束时间","类别"},
	{"图标","附件","主题","地点","开始时间","结束时间","定期模式","类别"}
    };
    public static final String[][] VIEW_TYPE = 
    {
	{"天/周/月","带自动预览的天/周/月视图","当前约会","事件","年度事件","定期约会","按类别"},
	{"地址卡","详细地址卡","电话列表","按类别","按单位","按位置","按后续标志"},
	{"邮件","邮件自动预览","按后续标志","最近七天","随后七天标记的项目","按会话内容","按发件人","未读的邮件","收件人"},
	{"简单列表","详细列表","当前任务","随后七天","过期任务","按类别","分配","按负责人","已完成的任务"}
    };
    public static final String[] FIELD_TYPE =
    {
	"文本","数字","百分比","货币", "是否", "日期/时间", "持续时间","关键词","合并","公式", "整数"
    };
    public static final String[][] FORMAT_TYPE = 
    {
	{
	    "文本"
	},
	{
	    "全部数字：1,234,567 -1,234,567","截断数字：1,235, -1,235"
	}
    };
    //任务完成的百分比
    public static final int PERCENT_COMPLETE = 0;
    //任务的实际工作时间
    public static final int ACTUAL_WORK = PERCENT_COMPLETE + 1;
    //委派任务的人员
    public static final int ASSIGNED = ACTUAL_WORK + 1;
    //邮件的账号
    public static final int ACCOUNT = ASSIGNED + 1;
    
    public static final int ADDRESS_SELECTED = ACCOUNT + 1;
    public static final int ADDRESS_SELECTOR = ADDRESS_SELECTED + 1;
    //联系人的纪念日
    public static final int ANNIVERSARY = ADDRESS_SELECTOR + 1;
}
