package org.cas.client.resource.international;
/***/
public interface MailOptionConstant
{
    String MAILOPTION = "邮件选项";
    //========================邮件设置文字
    String MAILSETTING = "邮件设置";
    String IMPORTANCE = "重要性(P):";
    String SENSE = "敏感度(Y):";
    String [] IMPORTANT = {"高","普通","低"};
    String [] SENSITIVE = {"普通","个人","私人性质","机密"};
    //========================安全性文字
    String SECURITY = "安全性";
    String ENCRYPT = "加密邮件内容和附件(E)";
    String SIGNATURE = "为此邮件添加数字签名(D)";
    //========================跟踪选项文字
    String TRAILOPTION = "跟踪选项";
    String ANSWER= "请在阅读此邮件后给出\"已读\"回执(R)";
    //========================传递选项文字
    String TRANSFEROPTION = "传递选项";
    String TRANSFERTO = "使答复发送到(A):";
    String TRANSFERLESS = "发送不早于(B):";
    String CONTACT = "联系人(C)...";
    String CATEGORY = "类别(G)...";
    String NOTHING = "无";
    
    String CLOSING = "关闭";
    //=============安全
    String SECURE = "安全属性";    
    String CLEARSIGNATURE = "以明文签名发送邮件(T)";
    String SECURESET = "安全设置";
}
