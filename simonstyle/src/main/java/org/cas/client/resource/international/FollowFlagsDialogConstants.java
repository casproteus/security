package org.cas.client.resource.international;

/***/
public interface FollowFlagsDialogConstants
{
    public static final String DEFAULTSELECTION = "需后续工作";
    
    public static final String FOLLOW_FLAG_TITLE = "后续标志"; 
    
    public static final String TOP_LABEL = "为项目添加标志可提醒您需要对该项进行后续工作，完成后续工作后，您可其设置为\"完成\"。";
    
    public static final String FLAG = "标志(F):";
    public static final String EXPIRE_TIME = "到期时间(D):";
    public static final String CLEAR_FLAG = "清除标志(C)";
    public static final String FINISHED = "已完成(O)";
    public static final String [] FLAG_ITEMS ={DEFAULTSELECTION,"呼叫","不转发","仅供参考","转发","无需响应","已读","答复","答复所有人","校对","请打电话","安排会议","发送会议","发送电子邮件","发送信件"};
    public static final String OK_BTN = "确定";
    public static final String CANCEL_BTN = "取消";
}
