package org.cas.client.resource.international;

/**定义有关应用及其子视图的常量信息*/
public interface AppConstant
{
    /** 子视图类型，按照ModelConstants中的常量定义顺序排列*/
    public static final String[][] SUB_APP_TYPE =
    {
        //日历，TODO
        null,
        //任务，TODO
        null,
        //联系人
        {
            "地址卡", "电话列表", "按类别", "按单位", "按位置", "按后续标记"
        },
        //日记，TODO
        null,
        //收件箱
        {
            "按收件人", "按后续标志", "按最近七天的标志", "按后续七天的标志",
            "按邮件的内容", "按发件人", "按未读的邮件"
        },
        //发件箱
        {
            "按收件人", "按后续标志", "按最近七天的标志", "按后续七天的标志",
            "按邮件的内容", "按发件人", "按未读的邮件"
        },
        //已发送邮件
        {
            "按收件人", "按后续标志", "按最近七天的标志", "按后续七天的标志",
            "按邮件的内容", "按发件人", "按未读的邮件"
        },
        //删除项，TODO
        null,
        //视图信息，TODO
        null,
        //邮件帐户，TODO
        null,
        //通讯组，TODO
        null,
    };
}
