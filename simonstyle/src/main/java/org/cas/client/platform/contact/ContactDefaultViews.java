package org.cas.client.platform.contact;


public interface ContactDefaultViews {
    String[] TEXTS = { "ID", "已删除", "位置", "显示为", "备注", "姓", "名", "昵称", "职位", "宅电", "家庭住址", "手机", "电子邮件地址", "即时通讯号码",
            "即时通讯类型", "主页", "性别", "关系", "分类", "照片", "生日", "工作单位", "单位电话", "单位传真", "单位地址", "单位邮编", "纪念日类型", "纪念日",
            "是通讯组", "通讯组成员", "家庭邮编", "帐务" };

    String[] FIELDS = { "ID", "DELETED", "FOLDERID", "SUBJECT", "CONTENT", "FNAME", "NAME", "NNAME", "TITLE", "PHONE",
            "ATTRESS", "CPHONE", "EMAIL", "CNUMBER", "CTOOL", "WP", "SEX", "RELATION", "CATEGORY", "PHOTO", "BIRTHDAY",
            "COMPANY", "COMPTEL", "COMPFAX", "COMPATTR", "COMPPOSTCODE", "ANNIVERSARYTYPE", "ANNIVERSARY", "TYPE",
            "MEMBERLIST", "HOMEPOSTCODE", "ACCOUNT" };

    String[] TYPES = { "INTEGER IDENTITY PRIMARY KEY", "BIT DEFAULT false", "INTEGER", "VARCHAR(255)", "VARCHAR(255)",
            "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)",
            "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "BIT DEFAULT false",
            "VARCHAR(255)", "VARCHAR(255)", "BINARY", "TIMESTAMP", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)",
            "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "TIMESTAMP", "BIT DEFAULT false", "VARCHAR(255)",
            "VARCHAR(255)", "INTEGER" };

    String strForImportItem = "导入地址簿";
    String describeForImportItem = "导入外部地址簿文件";

    String[] RecommendCols = { "称谓", "名字", "姓氏", "昵称", "职务", "电子邮件" };

    /**
     * 该变量服务用于导入对话盒,其中包含的字段索引将是导入对话盒中允许导入的字段. //TODO:目前是按照微软产生的字段名字及其顺序来定 准备哪些字段用于和将从中导入内容的文本或vcs文件中的内容匹配.
     * //NOTE:如果以后发现有增加别的字段的需要的话,本数组将继续修改.
     */
    int[] IMPORT_MAP = new int[] { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26,
            27 };
    int[] DISPLA_AS_DOUBLE = new int[] { // 判断选中属性是否为“显示为”,"姓"，“名字”，“邮件”
            3, // @note 这些属性是按照先后被代替的顺序来排列的。
                    5, 6, 12 };

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 各大应用共有的字段
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    int ID = 0;
    int DELETED = 1;
    int FOLDERID = 2;
    int SUBJECT = 3;
    int CONTENT = 4;
    int FNAME = 5;
    int NAME = 6;
    int NNAME = 7;
    int TITLE = 8;
    int PHONE = 9;
    int ADDRESS = 10;
    int CPHONE = 11;
    int EMAIL = 12;
    int CNUMBER = 13;
    int CTYPE = 14;
    int WEBPAGE = 15;
    int SEX = 16;
    int RELATION = 17;
    int CATEGORY = 18;
    int PHOTO = 19;
    int BIRTHDAY = 20;
    int COMPANY = 21;
    int COMPTEL = 22;
    int COMPFAX = 23;
    int COMPADDR = 24;
    int COMPPOSTCODE = 25;
    int ANNIVERSARYTYPE = 26;
    int ANNIVERSARY = 27;
    int TYPE = 28;
    int MEMBERLIST = 29;
    int HOMEPOSTCODE = 30;
    int ACCOUNT = 31;

    final String INSERT_INTO =
            "INSERT INTO VIEWINFO(VIEWNAME, VIEWTYPE, FIELDNAMES, FIELDWIDTHS, HASEDITOR, HASPREVIEW, FOLDERID, UNREADED, NUMBER, SYCNSETTING, SERVERFOLDER) VALUES(";

    final String VIEW_NAME_Default = "家人";
    final String VIEW_NAME_1 = "家人";
    final String VIEW_NAME_2 = "朋友";
    final String VIEW_NAME_3 = "同学";
    final String VIEW_NAME_4 = "同事";
    final String VIEW_NAME_5 = "网友";
    final String VIEW_NAME_6 = "客户";
    final String VIEW_NAME_7 = "陌生人";
    /***** 联系人视图的缺省信息 *****************************************************************************************************/
    // 默认
    final String VIEWINFO_DEFAULT =
            "', 0, '0,3,21,8,7,11,22,23,9,13,14,12,15,17,18', '20,80,200,60,80,100,120,120,120,110,100,200,220,140,60', FALSE, FALSE, 100, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_DEFAULT = INSERT_INTO.concat("'").concat(VIEW_NAME_Default).concat(VIEWINFO_DEFAULT);
    // 手牌
    final String VIEWINFO_1 =
            "', 0, '0,5,6,3,7,18,16,21,8,10,9,20,27,24,25,4', '20,40,40,80,80,60,40,200,60,370,120,90,120,390,70,200', FALSE, FALSE, 101, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_1 = INSERT_INTO.concat("'").concat(VIEW_NAME_1).concat(VIEWINFO_1);
    // VIP客户
    final String VIEWINFO_2 =
            "', 0, '0,3,31,21,8,7,11,22,23,9,13,14,12,15,17,18', '20,80,60,200,60,80,100,120,120,120,110,100,200,220,140,60', FALSE, FALSE, 102, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_2 = INSERT_INTO.concat("'").concat(VIEW_NAME_2).concat(VIEWINFO_2);
    // 同学
    final String VIEWINFO_3 =
            "', 0, '0,5,6,16,21,8,10,9,20,26,27,24,25,4', '20,40,40,40,200,60,370,120,90,100,120,390,70,200', FALSE, FALSE, 103, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_3 = INSERT_INTO.concat("'").concat(VIEW_NAME_3).concat(VIEWINFO_3);
    // 同事
    final String VIEWINFO_4 =
            "', 0, '0,3,21,8,7,11,22,23,9,13,14,12,15,17,18', '20,80,200,60,80,100,120,120,120,110,100,200,220,140,60', FALSE, FALSE, 104, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_4 = INSERT_INTO.concat("'").concat(VIEW_NAME_4).concat(VIEWINFO_4);
    // 网友
    final String VIEWINFO_5 =
            "', 0, '0,5,6,16,21,8,10,9,20,26,27,24,25,4', '20,40,40,40,200,60,370,120,90,100,120,390,70,200', FALSE, FALSE, 105, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_5 = INSERT_INTO.concat("'").concat(VIEW_NAME_5).concat(VIEWINFO_5);
    // 客户
    final String VIEWINFO_6 =
            "', 0, '0,3,21,8,7,11,22,23,9,13,14,12,15,17,18', '20,80,200,60,80,100,120,120,120,110,100,200,220,140,60', FALSE, FALSE, 106, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_6 = INSERT_INTO.concat("'").concat(VIEW_NAME_6).concat(VIEWINFO_6);
    // 陌生人
    final String VIEWINFO_7 =
            "', 0, '0,5,6,16,21,8,10,9,20,26,27,24,25,4', '20,40,40,40,200,60,370,120,90,100,120,390,70,200', FALSE, FALSE, 107, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_7 = INSERT_INTO.concat("'").concat(VIEW_NAME_7).concat(VIEWINFO_7);

    public static final String[] INIT_DB_VIEWINFO =
            new String[] { INSERT_VIEWINF_DEFAULT, INSERT_VIEWINF_1, INSERT_VIEWINF_2, INSERT_VIEWINF_3,
                    INSERT_VIEWINF_4, INSERT_VIEWINF_5, INSERT_VIEWINF_6, INSERT_VIEWINF_7 };
}
