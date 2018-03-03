package org.cas.client.resource.international;

/** 定义证书的常量信息 */
public interface CertificateConstant
{
    String CER_MANAGE = "证书管理(M)...";
    //-----------------------CerGuideDialog中的常量    
    String GUIDE_IN = "证书导入向导";
    String GUIDE_OUT = "证书导出向导";
    String UP = "上一步(B)";
    String DOWN = "下一步(N)";
    String OVER = "完成";
    String WELCOME_IN = "欢迎使用证书导入向导";
    String WELCOME_OUT = "欢迎使用证书导出向导";
    String ASSERTION = "这个向导帮助您将证书复制到PIM的数据库中。";
    String DETAIL = "由证书颁发机构颁发的证书是用来确认您的身份的文件,"+
    "它还可能含有用来保护数据或建立安全网络连接的信息。";
    String NEXT = "要继续，请单击“下一步”。";
    String INPUT_FILE = "要导入的文件";
    String SPECIFY_FILE = "指定要导入的文件。";
    String FILE_NAME = "文件名(F):";
    String EXPLORER = "浏览(R)...";
    String NOTE = "注意：用下列格式可以在一个文件中存储一个以上证书：";
    String PFX = "个人信息交换-PKCS #12(.PFX,.P12)";
    String OUTPUT_KEY = "导出私钥";
    String OUT_KEYANDCERT = "您可以选择将私钥跟证书一起导出。";
    String PROTECT_KEY = "私钥受密码保护。如果要将私钥跟证书一起导出，您必须"+
    "在后面一页上键入密码。";
    String REQUEST = "要将私钥跟证书一起导出吗?";
    String OUT_KEY = "是，导出私钥(Y)";
    String NOOUTKEY = "不，不要导出私钥(O)";
    String PASSWORD = "密码";
    String SECURE = "为了保证安全，已用密码保护私钥。";
    String INPUT_PASSWORD = "为私钥键入密码。";
    String PASSWORDL = "密码(P):";
    String STRONG_PROTECT = "启用强私钥保护。如果启用这个选项，"+    
    "每次应用程序使用私钥时，您都会得到提示(E)";
    String CANOUT = "将私钥标记成可导出的(M)";
    String FILE_FORMAT = "导出文件格式";
    String EXPLAIN = "可以用不同的文件格式导出证书。";
    String SELECTFORMAT = "选择要使用的格式：";
    String DERFORMAT = "DER 编码二进制 X.509(.CER)(D)";
    String BASE64FOTMAT = "Base64 编码 X.509(.CER)(S)";
    String PKCS7FORMAT = "加密消息语法标准-PKCS#7证书(.P7B)(C)";
    String FULLPATH = "如果可能，将所有证书包括到证书路径中(I)";
    String PKCS12FORMAT = "私人信息交换-PKCS#12(.PFX)(P)";
    String FULL = "如果可能，将所有证书包括到证书路径中(U)";
    String DELETE_KEY = "如果导出成功，删除密钥(K)";
    String COMPLETING_IN = "正在完成证书导入向导";
    String OVER_IN = "您已成功地完成证书导入向导。";
    String SPECIFY_STEPS = "您已指定下列设置：";
    String FORSECURE = "要保证安全，您必须用密码保护私钥。";
    String PASSWORD_USE = "键入并确认密码。";
    String CONFIRM_PASSWORD = "确认密码(C):";
    String OUTPUT_FILE = "要导出的文件";
    String SPECIFY_OUT = "指定要导出的文件名。";
    String COMPLETING_OUT = "正在完成证书导出向导";
    String OVER_OUT = "您已成功地完成证书导出向导。";
    String PFX_TIP = "个人信息交换";
    String SAVEAS = "另存为";
    String DESCRIPTION = "描述：相关的私钥被标为不能导出的。只有证书可以导出。";
    String FILENAME = "文件名";
    String CONTENT = "内容";
    String OUTKEY = "导出密钥";
    String INCLUDE_ALL = "包括证书路径中的所有证书";
    String FILEFORMAT = "文件格式";
    String PIX = "个人信息交换(*.pfx)";
    String DER_ENCODING = "DER 编码二进制 X.509 (*.cer)" ;
    String BASE64_ENCODING = "Base64 编码 X.509 (*.cer)";
    String PKCS7_ENCODING = "密码消息语法标准 - PKCS #7 证书(*.P7B)" ;
    //-----------------------CertManageDialog中的常量
    String USECERT = "选择要使用的证书。";
    String VIEWCERT = "查看证书(V)";
    String IMPORT = "导入(I)...";
    String EXPORT = "导出(E)";
    String DELETE = "删除(R)";
    String YIELD = "生成证书(P)";
    String CLOSE = "关闭(C)";
    String GOOD_NAME = "好记的名称";
    String AWARD = "颁发给";
    String ISSUER = "颁发者";
    String CUT_OFF = "截止日期";
    //-----------------------CertificateDialog中的常量
    String YIELD_TITLE = "生成证书";
    String MAIL_ADDRESS = "邮件地址(E):";
    String LAST_TIME = "证书有效期(D):*";
    String FAMILYNAME = "名字与姓氏(N):";
    String ORGANIZATION_UNIT = "组织单位(U):";
    String ORGANIZATION_NAME = "组织名称(O):";
    String CITY = "城市或区域名称(L):";
    String STATE = "州或省份名称(S):";
    String COUNTRY = "国家(C):";
    String CONFIRM = "确定";
    String TIAN = "天";
}
