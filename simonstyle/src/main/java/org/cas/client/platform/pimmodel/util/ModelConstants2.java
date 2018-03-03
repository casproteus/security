package org.cas.client.platform.pimmodel.util;

import java.awt.Color;

import org.cas.client.platform.casutil.TmpConstants;

public interface ModelConstants2 {
    // 以下字段是供给外部，用来构造SQL语句。【外部经常使用】--------------------------------------
    // 下面五个目前被PIMDBModel用到.
    String ICON = "ICON"; // 图标
    String INFOLDER = "INFOLDER"; // INFOLDER路径
    String FOLOWUPENDTIME = "FOLOWUPENDTIME"; // 后继结束事件
    String IS_ACCOUNT_FORBID = "IS_ACCOUNT_FORBID"; // 帐号状态，boolean变量 IS_ACCOUNT_FORBID，标识状态，true为当前帐号有效，false为当前的帐号无效
    String USERNAME = "USERNAME"; // 用户名
    // 下面两个目前被DBVersionAdapter用到.
    String TIMESTAMP = "TIMESTAMP"; //
    String TIME = "TIME";
    // 结束------------------------------------------------------------------------------

    // 设置ViewFormat的缺省值----------------------------------------------
    String INSERT_INTO_VIEWFORMAT =
            "INSERT INTO VIEWFORMAT(APPTYPE, MODETYPE, INFOLDER, FONTSIZE, FONTNAME, FONTSTYLE, HAVESTRIKETHROUGH, FONTCOLOR, UNREADED, NUMBER, SYCNSETTING, SERVERFOLDER) VALUES(";
    int blackRgb = Color.black.getRGB();
    int redRgb = Color.red.getRGB();
    String[] INIT_DB_CONSTANTS = new String[] // 初始化ViewFormat时通过本数组得到各语句.
            {
            /** 收件箱的format缺省设置 */
            INSERT_INTO_VIEWFORMAT + "4, 0, 200, 12, '宋体', 1, FALSE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 未读的邮件：宋体，加粗，小五
                    INSERT_INTO_VIEWFORMAT + "4, 1, 200, 12, '宋体', 1, FALSE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 未发送的邮件：宋体、倾斜、小五,
                    INSERT_INTO_VIEWFORMAT + "4, 2, 200, 12, '宋体', 0, TRUE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 到期的邮件：宋体、常规、小五、删除线,
                    INSERT_INTO_VIEWFORMAT + "4, 3, 200, 12, '宋体', 0, FALSE, " + redRgb + ", 0, 0, 0, ' '" + ");", // 过期的邮件：宋体、常规、红色，小五,
                    /** 发件箱的format缺省设置 */
                    INSERT_INTO_VIEWFORMAT + "5, 0, 201, 12, '宋体', 1, FALSE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 未读的邮件：宋体，加粗，小五
                    INSERT_INTO_VIEWFORMAT + "5, 1, 201, 12, '宋体', 1, FALSE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 未发送的邮件：宋体、倾斜、小五
                    INSERT_INTO_VIEWFORMAT + "5, 2, 201, 12, '宋体', 0, TRUE, " + blackRgb + ", 0, 0, 0, ' '" + ");", // 到期的邮件：宋体、常规、小五、删除线
                    INSERT_INTO_VIEWFORMAT + "5, 3, 201, 12, '宋体', 0, FALSE, " + redRgb + ", 0, 0, 0, ' '" + ");",// 过期的邮件：宋体、常规、红色，小五
                    /** 已发送项的format缺省设置 */
                    INSERT_INTO_VIEWFORMAT + "6, 0, 202, 12, '宋体', 1, FALSE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 未读的邮件：宋体，加粗，小五
                    INSERT_INTO_VIEWFORMAT + "6, 1, 202, 12, '宋体', 1, FALSE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 未发送的邮件：宋体、倾斜、小五
                    INSERT_INTO_VIEWFORMAT + "6, 2, 202, 12, '宋体', 0, TRUE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 到期的邮件：宋体、常规、小五、删除线
                    INSERT_INTO_VIEWFORMAT + "6, 3, 202, 12, '宋体', 0, FALSE, " + redRgb + ", 0, 0, 0, ' '" + ");",// 过期的邮件：宋体、常规、红色，小五
                    /** 草稿箱的format缺省设置 */
                    INSERT_INTO_VIEWFORMAT + "7, 0, 203, 12, '宋体', 1, FALSE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 未读的邮件：宋体，加粗，小五
                    INSERT_INTO_VIEWFORMAT + "7, 1, 203, 12, '宋体', 1, FALSE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 未发送的邮件：宋体、倾斜、小五
                    INSERT_INTO_VIEWFORMAT + "7, 2, 203, 12, '宋体', 0, TRUE, " + blackRgb + ", 0, 0, 0, ' '" + ");",// 到期的邮件：宋体、常规、小五、删除线
                    INSERT_INTO_VIEWFORMAT + "7, 3, 203, 12, '宋体', 0, FALSE, " + redRgb + ", 0, 0, 0, ' '" + ");"// 过期的邮件：宋体、常规、红色，小五
            };// 设置ViewFormat的缺省值----------------------------------------------

    // 以下字段为本接口内部各表的结构中共同用到的字段,所以特别列出来，让用到他们的数组保持对它们的引用，节约内存开销
    String TYPE_ID = " INTEGER IDENTITY PRIMARY KEY, "; // ID
    String TYPE_INTEGER = " INTEGER, ";
    String TYPE_SMALLINT = " SMALLINT, ";
    String TYPE_VARCHAR = " VARCHAR, ";
    String TYPE_BINARY = " BINARY, ";
    String TYPE_BIT = " BIT, ";
    String TYPE_TIMESTAMP = " TIMESTAMP, ";
    String TYPE_TINYINT = " TINYINT, ";
    String TYPE_BITE_DEFAULT_TRUE = " BIT DEFAULT 'TRUE', ";
    String TYPE_BITE_DEFAULT_FALSE = " BIT DEFAULT 'FALSE', ";
    /**
     * 以下为各个数据库表的字段名，其中一共有n张数据库表 每张数据库表对应的类型字符串，这些字符串是用来创建DBMS对象：建立数据库表
     */
    // 视图信息表名
    public static final String VIEWINFO_TABLE_NAME = "VIEWINFO";
    // 留作扩展用的,同时起到占位作用,是Index号和Namelist数组以及SYSTEMTABLE_FIELD_LIST数组中的位置对应起来--
    String[] EXTENDS_TABLE_FIELD = new String[] { "ID", "NAME" };
    String[] EXTENDS_TABLE_FIELD_TYPE = new String[] { TYPE_ID, " VARCHAR" };// ---------------------------------------------------------------
                                                                             // ViewInfo需要的字段列表==============================================
    String[] VIEWINFO_TABLE_FIELD = new String[] { "ID", "APPTYPE", "VIEWTYPE", "USESCOPE", "PREVIEWSCALE",
            "CUSTOMIZE", "HASEDITOR", "HASPREVIEW", "FIELDNAMES", "FIELDWIDTHS", "VIEWNAME", "SORTCRITERIA", "FILTER",
            "VIEWFORMAT", "FOLDERID", "NUMBER", "UNREADED", "SYCNSETTING", "SERVERFOLDER" };
    String[] VIEWINFO_TABLE_FIELD_TYPE = new String[] {// 字段类型
            TYPE_ID, TYPE_INTEGER, TYPE_INTEGER, TYPE_INTEGER, " INTEGER DEFAULT 38, ", TYPE_BIT,
                    TYPE_BITE_DEFAULT_TRUE, TYPE_BIT, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR,
                    TYPE_BINARY, TYPE_BINARY, TYPE_INTEGER, TYPE_INTEGER, TYPE_INTEGER, " INTEGER DEFAULT 0, ",
                    " VARCHAR" };// ================================================================
                                 // 帐号表的字段========================================================
    String[] ACCOUNT_TABLE_FIELD = new String[] { "ID", "ACCOUNTNAME", "SMTPDISPLAYNAME", "SMTPORGNAME",
            "SMTPEMAILADDRESS", "SMTPREPLYTOEMAIL", "SMTPSERVER", "SMTPUSESICILY", "SMTPUSERNAME", "SMTPPROMPTPWD",
            "SMTPPASSWORD2", "SMTPPORT", "SMTPSECURECONNECTION", "SMTPTIMEOUT", "SMTPSPLITMSG", "LEAVEMAILONSERVER",
            "SMTPSPLITMSGSIZE", "REMOVEWHENDELETED", "REMOVEWHENEXPIRED", "EXPIREDAYS", "INCOMESERVER", "POP3USERNAME",
            "POP3SERVER", "POP3PROMPTPWD", "POP3USESICILY", "POP3SKIPACCOUNT", "POP3PASSWORD2", "CONNECTIONTYPE",
            "CONNECTOID", "BKPCONNECTOID", "POP3PORT", "POP3SECURECONNECTION", "POP3TIMEOUT", "IMAPUSERNAME",
            "IMAPSERVER", "IMAPPROMPTPWD", "IMAPPASSWORD2", "IMAPPOLLING", "IMAPUSESICILY", "IMAPTIMEOUT",
            "IMAPSECURECONNECTION", "IMAPPORT", "IMAPDIRTY", "IMAPROOTFOLDER", "IMAPPOLLALLFOLDERS",
            "IMAPSVRSIDESPECIALFOLD", "IS_DEFAULT_MAIL", "REMEBER_SMTP_PWD", "ATTESTATION_FLAG", "LAST_TIME",
            TmpConstants.DEL, "PRIVATE_KEY", "CERTIFICATE", "CERTIFICATENAME", "CANOUTPUT", "ENCRYPT_ALGORITHM",
            "CER_SIGNATURE", "CER_ENCRYPT", "CER_SIGN_POS", "CER_ENCRYPT_POS", IS_ACCOUNT_FORBID };
    String[] ACCOUNT_TABLE_FIELD_TYPE = new String[] {// 字段类型
            TYPE_ID, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_SMALLINT,
                    TYPE_VARCHAR, TYPE_BIT, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_BIT, TYPE_VARCHAR, TYPE_BIT, TYPE_BIT,
                    TYPE_VARCHAR, TYPE_BIT, TYPE_BIT, TYPE_INTEGER, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_BIT,
                    TYPE_BIT, TYPE_BIT, TYPE_VARCHAR, TYPE_SMALLINT, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR,
                    TYPE_BIT, TYPE_INTEGER, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_BIT, TYPE_VARCHAR, TYPE_BIT, TYPE_BIT,
                    TYPE_INTEGER, TYPE_BIT, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_VARCHAR, TYPE_BIT, TYPE_BIT,
                    TYPE_BIT, TYPE_SMALLINT, TYPE_TIMESTAMP, TYPE_BIT, TYPE_BINARY, TYPE_BINARY, TYPE_BINARY,
                    TYPE_BINARY, TYPE_VARCHAR, TYPE_BINARY, TYPE_BINARY, TYPE_VARCHAR, TYPE_VARCHAR,
                    " BIT DEFAULT 'FALSE'" };// ===============================================================
                                             // 邮件规则表的字段////////////////////////////////////////////////////
    String[] MAILRULE_TABLE_FIELD = new String[] { "ID", "SERIES", "JUNKORADULT", "ISSENDER", "ISSELECTED", "RULENAME",
            "CONDITIONPOOL", "EXCEPTIONPOOL", "ACTIONPOOL" };
    String[] MAILRULE_TABLE_FIELD_TYPE = new String[] {// 字段类型
            TYPE_ID, TYPE_INTEGER, TYPE_INTEGER, TYPE_BIT, TYPE_BIT, TYPE_VARCHAR, TYPE_BINARY, TYPE_BINARY, " BINARY" };// ///////////////////////////////////////////////////////////////
    // 类别表字段----------------------------------------------------------
    String[] CATEGORY_TABLE_FIELD = new String[] { "ID", "NAME" };
    String[] CATEGORY_TABLE_FIELD_TYPE = new String[] {// 字段类型
            TYPE_ID, " VARCHAR" };// ----------------------------------------------------------------
    // 在自定义视图时定义的ViewFormat信息*************************************
    String[] VIEWFORMAT_TABLE_FIELD = new String[] { "ID", "APPTYPE", "MODETYPE", INFOLDER, "FONTSIZE", "FONTNAME",
            "FONTSTYLE", "HAVESTRIKETHROUGH", "FONTCOLOR", "UNREADED", "NUMBER", "SYCNSETTING", "SERVERFOLDER" };
    String[] VIEWFORMAT_TABLE_FIELD_TYPE = new String[] {// 字段类型
            TYPE_ID, TYPE_INTEGER, TYPE_INTEGER, TYPE_VARCHAR, TYPE_INTEGER, TYPE_VARCHAR, TYPE_INTEGER, TYPE_BIT,
                    TYPE_INTEGER, TYPE_INTEGER, TYPE_INTEGER, " INTEGER DEFAULT 0, ", " VARCHAR" };// ****************************************************************
                                                                                                   // 保存系统信息的表。目前只有版本信息，若以后没有其他信息，可以考虑与其他表合并--
    String[] SYSTEMINFO_TABLE_FIELE = new String[] { "ID", "VERSION" };
    String[] SYSTEMINFO_TABLE_FIELE_TYPE = new String[] {// 字段类型
            TYPE_ID, " VARCHAR" };// --------------------------------------------------------------
                                  // 保存用户登入的用户名、密码、类型（权限）================================
    String[] USERIDENTITY_TABLE_FIELD = new String[] { "ID", USERNAME, "PASSWORD", "TYPE" };
    String[] USERIDENTITY_FIELD_TYPE = new String[] {// 字段类型,字段中没有添加这个四个字段对应的键值，目的：安全
            TYPE_ID, TYPE_VARCHAR, TYPE_VARCHAR, " INTEGER" };// ================================================================

    /**
     * @Note：该表中的元素顺序必须跟数组TABLE_NAME_LIST中的元素位置顺序保持绝对一致，因为在初始化数据库时，将以TABLE_NAME_LIST中元素 的index号的2倍关系在本数组中找其字段名数组和字段类型数组。
     */
    String[][] SYSTEMTABLE_FIELD_LIST = new String[][] { EXTENDS_TABLE_FIELD, EXTENDS_TABLE_FIELD_TYPE,
            VIEWINFO_TABLE_FIELD, VIEWINFO_TABLE_FIELD_TYPE, ACCOUNT_TABLE_FIELD, ACCOUNT_TABLE_FIELD_TYPE,
            MAILRULE_TABLE_FIELD, MAILRULE_TABLE_FIELD_TYPE, CATEGORY_TABLE_FIELD, CATEGORY_TABLE_FIELD_TYPE,
            VIEWFORMAT_TABLE_FIELD, VIEWFORMAT_TABLE_FIELD_TYPE, SYSTEMINFO_TABLE_FIELE, SYSTEMINFO_TABLE_FIELE_TYPE,
            USERIDENTITY_TABLE_FIELD, USERIDENTITY_FIELD_TYPE };
    /**
     * 初始化的时候需要建立的所有表的名字 每次增加或减少表都要在这里注册 系统默认具备的表的名字，数据库初始化时，除了需要根据Config2.ini文件建立相关的表，还要建立以下各表。
     * 
     * @Note：该表中的元素顺序必须跟数组SYSTEMTABLE_FIELD_LIST中的元素位置顺序保持绝对一致，因为在初始化数据库时，将以本数组中元素 
     *                                                                             的index号的2倍关系在TABLE_NAME_LIST中找其字段名数组和字段类型数组
     *                                                                             。
     */
    String[] SYSTEMTABLE_NAME_LIST = new String[] { "EXTENDS", VIEWINFO_TABLE_NAME, "ACCOUNT", "MAILRULE", "CATEGORY",
            "VIEWFORMAT", "SYSTEMINFO", "USERIDENTITY", };
    // 上面未各系统表的名称,AppIndex号和结构，@NOTE：系统表的App均为负数，用户表的App为正数。
    int EXTENDS = 0;
    int VIEWINFO = EXTENDS - 1;
    int ACCOUNT = VIEWINFO - 1;
    int MAILRULE = ACCOUNT - 1;
    int CATEGORY = MAILRULE - 1;
    int VIEWFORMAT = CATEGORY - 1;
    int SYSTEMINFO = VIEWFORMAT - 1;
    int USERIDENTITY = SYSTEMINFO - 1;
    // 系统表的名称和Index号定义结束----------------------------------------------------------------------------------

    /* TYPES 对应类型的字符串 */
    String[] TYPE_STRING_VALUE = new String[] { "BIT"/*-7*/, "TINYINT"/*-6*/, "BIGINT"/*-5*/,
            "LONGVARBINARY"/*-4*/, "VARBINARY"/*-3*/, "BINARY"/*-2*/, "LONGVARCHAR"/*-1*/, "NULL"/* 0 */,
            "CHAR"/* 1 */, "NUMERIC"/* 2 */, "DECIMAL"/* 3 */, "INTEGER"/* 4 */, "SMALLINT"/* 5 */, "FLOAT"/* 6 */,
            "REAL"/* 7 */, "DOUBLE"/* 8 */, "VARCHAR"/* 12 */, "DATE"/* 91 */, TIME/* 92 */, TIMESTAMP/* 93 */, "OTHER"/* 1111 */
    };

    /**
     * 要初始化的表的名字， 这些表是在数据库中定义要初始化的。but,What's the gag?
     * 
     * @used only by DBVersionAdapter so far.
     * @note 这些表的记录不可修改，或者待修改
     */
    String[] INITED_TABLE_NAME_LIST = { "FOLDER", "VIEWFORMAT", "CATEGORY" };
}
