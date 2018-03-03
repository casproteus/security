package org.cas.client.resource.international;

/** */
public interface PIMTableConstants
{
    /**
     * 用于标记状态
     */
    public static final String[] FLAGS_STATUS_CONSTANTS =
    {
        "普通","已标记","已完成"
    };
    
    /**
     * 用于任务的状态字段
     */
    public static final String[] TASK_STATUS_CONSTANTS =
    {
        "未开始","进行中","已完成","正在等待其他人","已推迟"
    };
    
    
    /**
     * 用于性别  
     */
    public static final String [] SEX_ITEMS = {"未指定","男","女"};
        
    /**
     * 用于一些无选择项;如照片
     */
    public static final String HAVE = "有";
    public static final String NONE = "无";
    
    public static final String CONTACT = "联系人";
    public static final String COMMUNICATION_GROUP = "通讯组";
    
    /**
     * 用于一些无选择项;如纯文本发送
     */
    public static final String YES = "是";
    public static final String NO = "否";
    
}
