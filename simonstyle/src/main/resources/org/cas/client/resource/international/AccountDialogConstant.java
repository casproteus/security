package org.cas.client.resource.international;

/**定义附件对话框中有关视图的常量信息*/
public interface AccountDialogConstant
{
    //---------------------AccountDialog-----------------------------
     String ACCOUNT_TITLE    = "Internet 帐户";
     String ADD              = "添加(A)";
     String REMOVE           = "删除(R)";
     String PROPERTY         = "属性(P)";
     String DEFAULT          = "设为默认值(D)";
     String FORBID			 = "停用帐号(F)";
     String ACTIVATION		 = "激活帐号(F)";
     String FORBIDACCOUNT_1  = "邮件(停用)";
     String FORBIDACCOUNT	 = "(停用)";
     String INFO             = "导入(I)";
     String OUTFO            = "导出(E)";
     String CONFIG_SORT      = "设置顺序(S)";
     String CLOSE            = "关闭";
     String ACCOUNT          = "帐户";
     String EMAIL            = "邮件";
     String DEFAULT_EMAIL    = "邮件(默认)";
     String SEVER            = "连接类型";
     String MENUITEM_EMAIL   = "邮件(H)";
     String MENUITEM_SEVER   = "目录服务(D)";
    
    //---------------------ConfigDialog------------------------------
     String CONFIG_TITLE     = "请输入发件服务器的帐号和密码";
     String RADIOBUTTON1     = "使用与邮件接收服务器相同的设置(U)";
     String ENTRY_INFO       = "登陆信息";
     String ENTRY_WAY        = "使用不同的设置(D)";
    
    //---------------------InternetLinkGuideDialog-------------------
     String INTERNETLINKTITLE= "Internet 连接向导";
     String BACK             = "上一步(B)";
     String NEXT             = "下一步(N)"; 
     String FINISH           = "完成";
     String LAN              = "局域网";
     String WANTDISPLAYNAME  = "想显示的名称。";
     String DISPLAY_NAME     = "显示姓名(D)";
     String MY_EMAIL_SEVER   = "我的邮件接收服务器是(S)";
     String EMAIL_RECEIVE    = "邮件接收(POP3 或 IMAP)服务器(R)";
     String SMTP_SEVER       = "邮件发送服务器(SMTP)(D)";
     String EXAMPLE          = "例如：John Smith";
     String INPUT_USER_PAS   = "键入服务器提供商给您的帐户和密码。";
     String USE_OUTPUT_PAS   = "我的服务器需要安全验证";
     String USR_CONTECT_TYPE = "使用哪种方式连接 Internet ?";
     String USE_INTERNET     = "手动建立 Internet 连接(U)";
     String REMBER_PAS       = "记住密码(W)";
     String EXPLAIN          =
    "您的电子邮件地址是别人用来给您发送电子邮件的地址。";
     String EXPLAIN1         =
    "例如：someone@evermoresw.com";
     String EXPLAIN2         =
    "SMTP 服务器是您用来发送邮件的服务器。";    
     String EXPLAIN3         =
    "如果Internet服务提供商要求您使用“身份验证”来访问电子邮件帐户，请选择“需要身份验证”选项。";
     String EXPLAIN4         =
    "如果您已经有Internet服务提供商的帐户，并得到了Internet所有连接信息，可以使用电话线连接您的帐户，如果您已经连接到局域网，并且该局域网(LAN)同Internet连接的，则可以通过局域网访问Internet。";
     String EXPLAIN5         =
    "您已成功地输入了设置帐户所需的所有信息。要保存这些设置，请单击“完成”。";
     String WHEN_EMAIL_SEND  = 
    "当您发送电子邮件时，您的姓名将出现在发送邮件的“发件人”字段。";
    
    //---------------------PropertyDialog-----------------------------
     String PROPERTY_TITLE   = "属性";
     String SEVERCOM         = "服务器";
     String NORMAL           = "常规";
     String CONTECT          = "连接";
     String SECURE           = "安全";
     String ADVANCE          = "高级";
     String OK               = "确定";
     String CANCEL           = "取消";
    
    //---------------------NormalPanel--------------------------------
     String EMAIL_ACCOUNT    = "邮件帐户";
     String INSTEAD_SEVER    = "请输入您用来指代这些服务器的名称.";
     String USER_INFO        = "用户信息";
     String NAME             = "姓名(N)";
     String COMPANY          = "单位(D)";
     String EMIALADDRESS     = "电子邮件地址(E)";
     String ANSWERADDRESS    = "答复地址(Y)";
    
    //--------------------SeverPanel----------------------------------
     String SEVER_INFO       = "服务器信息";
     String MYRECEIVE_SEVER  = "我的邮件接收服务器是(M)"; 
     String RECEIVE_EMAIL    = "接收邮件(I)(";
     String SEND_EMAIL       = "发送邮件(O)(SMTP)";
     String RECEIVE_SEVERCOM = "接收邮件服务器";
     String ACCOUNT_NAME     = "帐户名(C)";
     String PASSWORD         = "密码(P)";
     String SEND_SEVERCOM    = "发送邮件服务器";
     String NEED_CHECK       = "我的服务器要求身份验证(V)";
     String CONFIG           = "设置(E)";
     String USEHANDLINK      = "手动连接Internet";
     String USETELLINK       = "用电话线连接";
    
    //--------------------LinkPanel-----------------------------------
     String USER_LAN         = "使用局域网(LAN)连接(L)";
     String LAN_MODEL        = "局域网不可能用时使用调制解调器连接(V)";
     String USER_TELELINE    = "使用电话线连接(M)";
     String MODEL            = "调制解调器";
     String USER_CONTECT     = "使用以下拨号网络连接(U)";
     String TELE_CONTECT     = "拨号连接";
     String IMAGE_CONTECT    = "虚拟拨号专用连接";
    
    //--------------------advancePanel--------------------------------
     String SEVERCOM_NUMBER  = "服务端口号";
    // String SEND_EMAIL_SMTP  = "发送邮件(SMTP)(0):";
    // String RECIEVE_EMAIL_POP= "接收邮件(POP3)(0):";
     String USER_DEFAULT     = "使用默认值(U)";
     String SEVER_OUT_TIME   = "服务器超时";
     String SHORT            = "短";
     String LONG             = "长";
     String MINUTE           = "分";
     String SEND             = "传送";
     String SAVE_EMAIL       = "在服务器上保留邮件的副本(L)";
     String IN               = "在(R)";
     String AFTER_DAYS_DEL   = "天之后从服务器删除";
    
    //--------------------imapPanel-----------------------------------
     String FOLDER           = "文件夹";
     String NODE_PATH        = "根文件夹路径(F):";
     String EXAM_ALL_FOLDER  = "检查所有文件夹中的新邮件(C)";
     
     //--------------------securePanel-----------------------------------
     String SIGNATURE = "签名证书";
     String SIG_NOTE = "选择签名证书。这将决定在用这个帐户签署邮件时所使用的用来证明您身份的数字ID。";     
     String SIG_CERTIFICATE = "证书:";
     String SELECT_SIG_CERT = "选择(S)...";
     
     String ENCRYPT = "加密首选项";
     String ENC_NOTE = "选择用于加密邮件内容的证书和算法。这些信息将包含在您数字签名的的邮件中,这样其他人就可"+ 
     "以用这些设置来给您发送加密邮件了";
     String ENC_CERTIFICATE = "证书:";     
     String SELECT_ENC_CERT = "选择(L)...";
     String ALGORITHM = "算法:";
     String[] ALG_CONTENT = {"3DES","RC2","IDEA","CAST5","AES(128-bit)","AES(192-bit)","AES(256-bit)"};
}
