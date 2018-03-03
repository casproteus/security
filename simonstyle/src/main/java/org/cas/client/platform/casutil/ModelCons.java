package org.cas.client.platform.casutil;

public interface ModelCons {
    // 由于准备将本类和ModelDB类合并掉.同时又因为数据库结构调整,重新设置了默认列.正好将新的固定列的index定义于此.
    int folderIDIdx = 2;
    /************************************************************************
     * 原 emo.pim.pimmodel.ViewConstants 中的常量
     ************************************************************************/
    // 定义视图类型
    // 表格
    static final int TABLE_VIEW = 0;
    // 卡片
    static final int CARD_VIEW = 1;
    // 图标
    static final int ICON_VIEW = 2;
    // 天周月
    static final int DAYWEEKMONTH_VIEW = 3;
    // 时间线
    static final int TEXT_VIEW = 4;

    // ////////////////////////////////////////////////////////
    // 定义应用类型
    // ////////////////////////////////////////////////////////

    // 文件夹的索引， 依次为
    // 约会，任务，联系人，日记，收件箱，已发送项，发件箱，已删除项
    // 日历
    public static final int CLOSED = -1;

    public static final int CALENDAR_APP = CLOSED + 1;
    // 任务－－－－－－－－－1
    public static final int TASK_APP = CALENDAR_APP + 1;
    // 联系人－－－－－－－－2
    public static final int CONTACT_APP = TASK_APP + 1;
    // 日记－－－－－－－－－3
    public static final int DIARY_APP = CONTACT_APP + 1;
    // 收件箱－－－－－－－－4
    public static final int INBOX_APP = DIARY_APP + 1;
    // 发件箱－－－－－－－－5
    public static final int OUTBOX_APP = INBOX_APP + 1;
    // 已发送邮件－－－－－－6
    public static final int SENDED_APP = OUTBOX_APP + 1;
    // 草稿箱
    public static final int DRAFT_APP = SENDED_APP + 1;
    // 删除项－－－－－－－－7
    public static final int DELETED_ITEM_APP = DRAFT_APP + 1;
    // IMAP文件夹
    public static final int IMAP_APP = DELETED_ITEM_APP + 1;
    // 通讯组列表
    public static final int TELE_LIST_APP = IMAP_APP + 1;
    /**************************************************
     * 定义可存盘的类的标志号，通过这个标志号可以确定表名*
     *
     * @TODO:(Sam)这几个常量应该对用户隐藏,即model包以外的类可以不需要知道.
     *************************************************/
    // PIMViewInfo类
    public static final int VIEW_INFO_DATA = 0;
    // MailRuleContainer类
    public static final int MAIL_RULE_DATA = 1;
    // AccountInfo类
    public static final int ACCOUNT_INFO_DATA = 2;
    // ViewFormat类
    public static final int VIEW_FORMAT_DATA = 3;

    // //////////////////////////////////////////////////////
    // 应用子类型
    // //////////////////////////////////////////////////////

    // 联系人应用的子类型
    // 地址卡
    public static final int CONTACT_ADDRESS_CARD = 0;
    // 电话列表
    public static final int CONTACT_PHONE_LIST = 1;
    // 按类别
    public static final int CONTACT_BY_CATEGORY = 2;
    // 按单位
    public static final int CONTACT_BY_CAMPANY = 3;
    // 按位置
    public static final int CONTACT_BY_LOCATION = 4;
    // 按后续标记
    public static final int CONTACT_BY_FLAGS = 5;
    // 用于自定义视图，它需要知道某一视图有多少应用子类型
    public static final int CONTACT_SUBVIEW_COUNT = CONTACT_BY_FLAGS + 1;

    // ///////////////////////////邮件视图
    // 按收件人
    public static final int INBOX_BY_SENT_TO = 0;
    // 按后续标志
    public static final int INBOX_BY_FOLLOW_UP_FLAG = 1;
    // 按最近七天的标志
    public static final int INBOX_BY_LAST_SEVEN_DAYS = 2;
    // 按后续七天的标志
    public static final int INBOX_BY_FLAGGED_FOR_NEXT_SEVEN_DAYS = 3;
    // 按邮件的内容
    public static final int INBOX_BY_CONVENSATION = 4;
    // 按发件人
    public static final int INBOX_BY_SENDER = 5;
    // 按未读的邮件
    public static final int INBOX_BY_UNREAD_MESSAGES = 6;
    // 用于自定义视图，它需要知道某一视图有多少应用子类型
    public static final int MAIL_SUBVIEW_COUNT = INBOX_BY_UNREAD_MESSAGES + 1;

    // 日记子应用类型的定义
    // 预览
    public static final int DIARY_BY_PREVIEW = 0;
    // 按日期
    public static final int DIARY_BY_DATE = 1;
    // 主题
    public static final int DIARY_BY_SUBJECT = 2;

    /********** 约会应用类型子类型的定义 **********************/
    // //天
    // public static final int APPOINTMENT_BY_DAY = 0;
    // //周
    // public static final int APPOINTMENT_BY_WEEK = 1;
    // //工作周
    // public static final int APPOINTMENT_BY_WORK_WEEK = 2;
    // //月
    // public static final int APPOINTMENT_BY_MONTH = 3;
    // //当前约会
    public static final int APPOINTMENT_BY_CURRENT_APPOINTMENT = 4;
    // //事件
    // public static final int APPOINTMENT_BY_EVENT = 5;
    // //年度事件
    // public static final int APPOINTMENT_BY_YEAR_EVENT = 6;
    // //定期约会
    // public static final int APPOINTMENT_BY_DATED_APPOINTMENT = 7;
    // //按类别
    // public static final int APPOINTMENT_BY_CATGORY = 8;

    /******** 任务应用类型的子类型 ********************************/
    // 简单列表
    public static final int TASK_BY_SIMPLE_LIST = 0;
    // 详细列表
    public static final int TASK_BY_DETAILED_LIST = 1;
    // 当前任务
    public static final int TASK_BY_CURRENT_TASK = 2;
    // 随后七天
    public static final int TASK_BY_FOLLOWED_SEVEN_DAYS = 3;
    // 过期任务
    public static final int TASK_BY_OVERDUE = 4;
    // 按类别
    public static final int TASK_BY_CATEGORY = 5;
    // 分配
    public static final int TASK_BY_ASSIGN = 6;
    // 按负责人
    public static final int TASK_BY_PRINCIPAL = 7;
    // 已完成的任务
    public static final int TASK_BY_FINISHED_TASK = 8;

    /** 参数：所有的帐号、无效帐号、有效帐号 */
    // 所有的邮件帐号：包括可用和不可用的帐号
    public static final int ACCOUNT_ALL = 0;
    // 无效帐号
    public static final int ACCOUNT_INVALID = 1;
    // 有效帐号
    public static final int ACCOUNT_VALID = 2;
}
