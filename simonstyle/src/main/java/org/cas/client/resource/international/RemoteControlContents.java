package org.cas.client.resource.international;

/***/

public interface RemoteControlContents
{
    //标题
    public final static String REMOTECONTROL = "邮箱远程管理";
    //账号列表的标签
    public final static String ACCOUNTS = "账号:";
    //邮件信息列表的标签
    public final static String MESSAGESLIST = "邮件信息列表:";
    //刷新按钮
    public final static String REFURISH = "刷新列表(R)";
    //确定按钮
    public final static String OK_BTN = "确定";
    //取消按钮
    public final static String CANCEL_BTN = "取消";
    //关闭按钮
    public final static String EXECUTE = "应用(A)";
    //直接删除按钮
    public final static String DIRECTDELETE = "直接删除(D)";
    //收取并删除按钮
    public final static String RECEIVEANDDEL = "收取删除(B)";
    //收取按钮
    public final static String RECEIVE = "收取(R)";
    //不收取按钮
    public final static String DERECEIVE = "不收取(N)";
    //无账号
    public final static String NOACCOUNT = "<无账号>";
    
    String[] ACTION = 
    {
        "不收取","收取","收取删除","直接删除"
    };
    //==========列头============
    public final static String COLUMNHEAD_ACTION = "动作";
    String[] FIELDS =
    {
        COLUMNHEAD_ACTION, IntlModelConstants.IMPORTANCE, IntlModelConstants.ADDRESSER,
        IntlModelConstants.CAPTION, IntlModelConstants.RECIEVEDATE, IntlModelConstants.SIZE
    };
    int[] FIELDSWIDTH = 
    {
        50,                 20,                            100,
        240,                        130,                     50
    };
}
