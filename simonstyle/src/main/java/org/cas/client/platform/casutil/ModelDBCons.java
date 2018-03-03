package org.cas.client.platform.casutil;

/**
 * 该接口用于存放PIM数据库中各个表的名字和各个表中的字段的索引号, 建立索引号的目的: 1/它将作为key值用于存各个PIMRecord的各种属性到PIMRecord的HashTable中.
 * 2/通过建立一个索引关系,隐藏真正的表的各个字段名.据说这样方便(在表名的字符串需要改动时), 3/在IntlModelConstants中有些用于提供可在视图上显示的字段名的数组,他们中每个元素在数组
 * 中的Index号恰好就是在本类里定义的int值.(以维护的不便换取性能的提高<节省了一个keyValue结构>).
 * 
 * @NOTE:所以:改动Int值时, 1/要改IntlModelConstants中相应数组的元素顺序结构; 2/要改DefaltDBinfo中的相应数组的元素顺序结构(别忘了对应的类型也要调整)
 *                   3/要改PIMDefaultFieldWidths中的相应数组的元素顺序结构
 */

public interface ModelDBCons {
    // 此变量表示可能的最大key值, @called by: Pool;@NOTE:此变量需要随时维护(目前来说尤其当联系人字段有增加时);
    public static final int MAXKEYVALUE = 87;
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 表名
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String TASK_TABLE_NAME = "TASK"; // 任务表名
    public static final String OUTBOX_TABLE_NAME = "OUTBOX"; // 发件箱表名
    public static final String INBOX_TABLE_NAME = "MAIL"; // 收件箱表名
    public static final String SENDEDBOX_TABLE_NAME = "SENDEDBOX";// 已发送邮箱表名
    public static final String DRAFTBOX_TABLE_NAME = "DRAFT"; // 草稿箱名
    public static final String DIARY_TABLE_NAME = "DIARY"; // 日记表名
    public static final String APPOINTMENT_TABLE_NAME = "APPOINTMENT"; // 约会表名
    public static final String EVENT_TABLE_NAME = "EVENT"; // 事件表名
    public static final String DELETED_ITEM_TABLE_NAME = "RECYCLE"; // 删除项表名
    public static final String FOLDER_TABLE_NAME = "FOLDER"; // 文件夹表名
    public static final String ACCOUNT_TABLE_NAME = "ACCOUNT"; // 帐户信息表名
    public static final String ACCOUNT_TABLE_NAME1 = "ACCOUNT1"; // 临时的帐户信息的表名
    public static final String MAIL_RULE_TABLE_NAME = "MAILRULE"; // 邮件规则表名
    public static final String CATEGORY_TABLE_NAME = "CATEGORY"; // 类别表表名
    public static final String FILTER_TABLE_NAME = "FILTER"; // 过虑信息表表名
    public static final String VIEW_FORMAT_TABLE_NAME = "VIEWFORMAT"; // 视图格式化信息
    public static final String IMAP_TABLE_NAME = "IMAP";

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 各大应用共有的字段
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int ID = 0; // 唯一标识号
    public static final int CAPTION = 1; // 显示标题(可能是表示为/主题等)
    public static final int READED = 2; // 是否已读@NOTE:该变量从使用上来说只对邮件有意义,但鉴于在DeleteItem中需要用到此字段来决定显示颜色,为防止该key值在不同应用中具有不同的意义,只好每个字段中都增加该字段.
    public static final int ICON = 3; // 图标
    public static final int SIZE = 4; // 尺寸----------------------------------------
    public static final int ATTACH = 5; // 标识重名附件
    public static final int ATTACHMENT = 6; // 附件
    public static final int IMPORTANCE = 7; // 重要性 高：1，普通：3，低：5；
    public static final int FLAGSTATUS = 8; // 标记状态
    public static final int ADDRESSER = 9; // 发件人
    public static final int RECIEVEDATE = 10; // 接收时间,对于联系人,任务,约会(日记)可能为创建时间
    public static final int COMMENT = 11; // 正文内容
    public static final int CATEGORY = 12; // 类别
    public static final int FOLLOWFLAGS = 13; // 后续标记
    public static final int FOLDERID = 14; // 所在节点
    public static final int DELETED_FLAG = 15; // 删除标志
    public static final int FOLLOWUPCOMPLETE = 16; // 后续标记是否需要提醒
    public static final int FOLOWUPENDTIME = 17; // 后续标记提醒时间
    public static final int CONTACT = 18; // 联系人

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 任务表的键值
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int FINISH_FLAG = CONTACT + 1; // 完成
    public static final int END_TIME = FINISH_FLAG + 1; // 截止时间
    public static final int COMPANY = END_TIME + 1; // 单位
    public static final int TASK_GROSS = COMPANY + 1; // 工作总量
    public static final int BEGIN_TIME = TASK_GROSS + 1; // 开始时间
    public static final int REALLY_TASK = BEGIN_TIME + 1; // 实际工作
    public static final int OWNER = REALLY_TASK + 1; // 所有者
    public static final int COMPLETED = OWNER + 1; // 完成率
    public static final int FINISH_DATE = COMPLETED + 1;// 完成日期
    public static final int MODIFY_TIME = FINISH_DATE + 1; // 修改时间
    public static final int STATUS = MODIFY_TIME + 1; // 状态
    public static final int CREATE_TIME = STATUS + 1; // 创建时间
    public static final int NEEDTOBESEND = CREATE_TIME + 1; // 分配
    public static final int TALLY_INFO = NEEDTOBESEND + 1; // 记帐信息
    public static final int MILESTONE = TALLY_INFO + 1; // 里程
    public static final int ADDRESSEE = MILESTONE + 1; // 收件人
    public static final int AWOKE_DATE = ADDRESSEE + 1; // 提醒
    public static final int AWOKE_TIME = AWOKE_DATE + 1; // 提醒时间
    public static final int NEED_AWOKE = AWOKE_TIME + 1; // 是否需要提醒
    public static final int SOUND = NEED_AWOKE + 1; // 记录要播放的声音信息
    public static final int MODE_INDEX = SOUND + 1; // 按天、周、月、年
    public static final int EVERY_MODE_INDEX = MODE_INDEX + 1; // 每种模式的选现索引
    public static final int CALENDAR_INDEX = EVERY_MODE_INDEX + 1; // 是公历还是农历
    public static final int FIRST_NUMBER = CALENDAR_INDEX + 1; // 每一项的第一个数字
    public static final int SECOND_NUMBER = FIRST_NUMBER + 1; // 每一项的第二个数字
    public static final int THIRD_NUMBER = SECOND_NUMBER + 1; // 每一项的第三个数字
    public static final int WEEK_FLAGS = THIRD_NUMBER + 1; // 按周方式下的间隔选择
    public static final int START_DATE = WEEK_FLAGS + 1; // 开始时间
    public static final int END_INDEX = START_DATE + 1; // 结束选项索引
    public static final int REPEAT_NUMBER = END_INDEX + 1; // 重复次数
    public static final int END_DATE = REPEAT_NUMBER + 1; // 结束日期
    public static final int SAVE_COPY = END_DATE + 1; // 保存更新副本
    public static final int SEND_TO_ME_REPORT = SAVE_COPY + 1; // 给我发送状态报告
    public static final int NEED_REPEAT = SEND_TO_ME_REPORT + 1; // 是否设置重复周期
    public static final int TASK_IDENTIFIER = NEED_REPEAT + 1; // 唯一表示任务ID

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 日记表的键值
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int DIARY_CREATE_TIME = CONTACT + 1;// 创建时间
    public static final int DIARY_MODIFY_TIME = DIARY_CREATE_TIME + 1;// 修改时间
    public static final int DIARY_WEATHER = DIARY_MODIFY_TIME + 1; // 天气
    public static final int DIARY_MOOD = DIARY_WEATHER + 1; // 心情

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 日历表的键值
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int CALENDAR_ADDRESSEE = CONTACT + 1; // 收件人列表
    public static final int CALENDAR_ADDRESS = CALENDAR_ADDRESSEE + 1; // 开会的地址
    public static final int CALENDAR_BEGIN_TIME = CALENDAR_ADDRESS + 1; // 开始时间
    public static final int CALENDAR_END_TIME = CALENDAR_BEGIN_TIME + 1; // 结束时间
    public static final int CALENDAR_CALL_ACTOR = CALENDAR_END_TIME + 1; // 是否邀请与会者
    public static final int CALENDAR_ALL_DAY_EVENT = CALENDAR_CALL_ACTOR + 1; // 全天事件
    public static final int CALENDAR_NEED_REMIND = CALENDAR_ALL_DAY_EVENT + 1; // 是否提醒
    public static final int CALENDAR_REMIND_TIME = CALENDAR_NEED_REMIND + 1; // 提醒时提前多长时间
    public static final int CALENDAR_REMIND_SOUND = CALENDAR_REMIND_TIME + 1; // 提醒时播放的声音
    public static final int CALENDAR_TIME_DISPLAY = CALENDAR_REMIND_SOUND + 1; // 时间显示为
    // 以下为重复周期的内容
    public static final int CALENDAR_MODE_INDEX = CALENDAR_TIME_DISPLAY + 1; // 定期模式的选择索引。0：定期模式选择“按天”；1：选择“按周”；2：选择“按月”；
                                                                             // 3：选择“按年"
    public static final int CALENDAR_SELECTED_INDEX = CALENDAR_MODE_INDEX + 1; // 在确定的定期模式中的单选按钮的选择索引
    // 在确定的定期模式中和确定的单选按钮中的三个需要的数字，比如：
    // 选择按月的第二个按钮时，三个数字分别表示每几个月的第几个（工作日，星期日，星期一等）。如果所选按钮中不足三个数字，则取最小值，其余的值为缺省值
    public static final int CALENDAR_FIRST_NUM = CALENDAR_SELECTED_INDEX + 1;
    public static final int CALENDAR_SECOND_NUM = CALENDAR_FIRST_NUM + 1;
    public static final int CALENDAR_THIRD_NUM = CALENDAR_SECOND_NUM + 1;
    // 重复范围的表示
    public static final int CALENDAR_REPEAT_BEGIN_TIME = CALENDAR_THIRD_NUM + 1; // 重复范围中的开始时间
    public static final int CALENDAR_REPEAT_END_INDEX = CALENDAR_REPEAT_BEGIN_TIME + 1; // 重复范围中的结束时间的单选按钮的选择索引
    public static final int CALENDAR_REPEAT_NUM = CALENDAR_REPEAT_END_INDEX + 1; // 重复范围中如果选择第二个按钮中的记录的重复几次后的次数
    public static final int CALENDAR_REPEAT_END_TIME = CALENDAR_REPEAT_NUM + 1; // 重复范围中如果选择第三个按钮中的结束日期
    // 与会者状态
    public static final int CALENDAR_NAME = CALENDAR_REPEAT_END_TIME + 1; // 名称
    public static final int CALENDAR_APPEARED = CALENDAR_NAME + 1; // 出席
    public static final int CALENDAR_RESPONSE = +1; // 状态

    // 升序降序_________________________________________________________________
    public static final int ASCEND = 0;// 升序
    public static final int DESCEND = 1;// 降序

    /**
     * ================================ 临时为IMAP文件夹和本地文件夹新增加的 ================================
     */
    public static final int FOLDER = 17;
    public static final int UNSEEN = 18;
    public static final int COUNT = 19;
    public static final int SYNCSETTING = 20;
    public static final int EMPTY = 21;

    public static final String DELIMITER = ","; // PIMViewInfo中fieldNames和fieldWidths中的分隔符。
    public static final int OLDID = 1001; // TODO:为了测试,
}

/**
 * 原 emo.pimmodel.database.DBConstants 中的常量部分结束
 */
/********************************************************************************
 * 以下是原 emo.pim.pimmodel.FieldConstants 中的常量
 ********************************************************************************/
/*
 * public static final String TEXT = "VARCHAR"; public static final String NUMBER = "DOUBLE"; public static final String
 * BOOLEAN = "BIT"; public static final String DATE_TIME = "DATE"; public static final String INTEGERNUM = "INTEGER";
 * public static final String ICON = "BINARY"; public static final String OTHER_TYPE = "OTHER"; public static final
 * String SMALLINT_TYPE = "SMALLINT"; public static final String TIMESTAMP = "TIMESTAMP";
 */

/********************************************************************************
 * 以下是原 emo.pim.pimmodel.FieldConstants 中的常量部分结束
 *******************************************************************************
 * 
 * //用于设置各种表格中不同项的字体 //未读邮件 public static final int UNREADED_MAIL = 0; //未发送的邮件 public static final int UNSEND_MAIL =
 * UNREADED_MAIL + 1; //到期的电子邮件 public static final int EXPIRE_MAIL = UNSEND_MAIL + 1; //过期的电子邮件 public static final int
 * OVERDUE_MAIL = EXPIRE_MAIL +1; //未读的项目 public static final int UNREADED_TASK = OVERDUE_MAIL + 1; //过期的项目 public
 * static final int OVERDUE_TASK = UNREADED_TASK + 1; //已完成但不读的任务 public static final int COMPLETED_BUT_UNREADED_TASK =
 * OVERDUE_TASK + 1; //已完成并已阅读的任务 public static final int COMPLETED_AND_READED_TASK = COMPLETED_BUT_UNREADED_TASK + 1;
 * //过期任务 public static final int EXPIRE_TASK = COMPLETED_AND_READED_TASK + 1; //未读任务 public static final int
 * UNEXPIRE_TASK = EXPIRE_TASK + 1; //通讯组 public static final int COMMUNICATION_GROUP = UNEXPIRE_TASK + 1; //不受欢迎的联系人
 * public static final int UNDESIRABLE_CONTACT = COMMUNICATION_GROUP + 1; //未读的联系人 public static final int
 * RNREADED_CONTACT = UNDESIRABLE_CONTACT + 1; //过期的联系人 public static final int OVERDUE_CONTACT = RNREADED_CONTACT + 1;
 */

// public static final String CLIENT_CONTACT_TABLE_NAME = "CLIENT";

// public static final int CHN_NAME = CONTACT + 1;
// public static final int ENG_NAME = CHN_NAME + 1;
// public static final int NAME_PY = ENG_NAME + 1;
// public static final int CITY = NAME_PY + 1;
// public static final int GENDER = CITY + 1;
// public static final int BIRTH_DAY = GENDER + 1;
// public static final int ADDRESS_1 = BIRTH_DAY + 1;
// public static final int ADDRESS_2 = ADDRESS_1 + 1;
// public static final int POSTCODE = ADDRESS_2 + 1;
// public static final int POST_ADDRESS = POSTCODE + 1;
// public static final int HOME_PHONE = POST_ADDRESS + 1;
// public static final int CELL_PHONE = HOME_PHONE + 1;
// public static final int ID_CARD = CELL_PHONE + 1;
// public static final int NATIVE_PLACE = ID_CARD + 1;
// public static final int NATION = NATIVE_PLACE + 1;
// public static final int POLITICAL_PARTY = NATION + 1;
// public static final int IS_FOREIGN = POLITICAL_PARTY + 1;
// public static final int OFFICE_PHONE = IS_FOREIGN + 1;
// public static final int E_MAIL = OFFICE_PHONE + 1;
// public static final int EMPLOYEE_TYPE = E_MAIL + 1;
// public static final int IS_PROBATION = EMPLOYEE_TYPE + 1;
// public static final int RANK = IS_PROBATION + 1;
// public static final int WORK_DATE = RANK + 1;
// public static final int EMPLOYMENT_DATE = WORK_DATE + 1;
// public static final int NEXT_EVALUATING_DATE = EMPLOYMENT_DATE + 1;
// public static final int REGION = NEXT_EVALUATING_DATE + 1;
// public static final int CENTRAL_CITY = REGION + 1;
// public static final int BUSINESS_UNIT = CENTRAL_CITY + 1;
// public static final int SYS_DEPARTMENT = BUSINESS_UNIT + 1;
// public static final int START_TIME = SYS_DEPARTMENT + 1;
// public static final int IS_PHOTO = START_TIME + 1;
// public static final int END_PROBATION_DATE = IS_PHOTO + 1;
// public static final int PROVINCE = END_PROBATION_DATE + 1;
// public static final int CITIZEN_SHIP = PROVINCE + 1;
