package org.cas.client.platform.employee;

import org.cas.client.platform.cascustomize.CustOpts;

public interface EmployeeDefaultViews {
    String[] TEXTS = { "ID", "已删除", "位置", "显示为", "备注", "姓", "名", "昵称", "职位", "宅电", "家庭住址", "手机", "电子邮件地址", "即时通讯号码",
            "即时通讯类型", "主页", "性别", "关系", "分类", "照片", "进单位时间", "工资", "保险", "社保号码", "员工号", "密码", "身份证", "生日", "纪念日类型",
            "纪念日", "银行卡号" };
    String[] FIELDS = { "ID", "DELETED", "FOLDERID", "SUBJECT", "CONTENT", "FNAME", "NAME", "NNAME", "TITLE", "PHONE",
            "ADDRESS", "CPHONE", "EMAIL", "CNUMBER", "CTOOL", "WEBPAGE", "SEX", "RELATION", "CATEGORY", "PHOTO",
            "JOINTIME", "SALARY", "INSURANCE", "SSCNUMBER", "CODE", "PASSWORD", "IDCARD", "BIRTHDAY",
            "ANNIVERSARYTYPE", "ANNIVERSARY", "BANKNUMBER" };
    String[] TYPES = { "INTEGER IDENTITY PRIMARY KEY", "BIT DEFAULT false", "INTEGER", "VARCHAR", "VARCHAR", "VARCHAR",
            "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR",
            "VARCHAR", "BIT DEFAULT false", "VARCHAR", "VARCHAR", "BINARY", "TIMESTAMP", "INTEGER", "INTEGER",
            "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "TIMESTAMP", "VARCHAR", "TIMESTAMP", "VARCHAR" };

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
    int JOINTIME = 20;
    int SALARY = 21;
    int INSURANCE = 22;
    int SSCNUMBER = 23;
    int CODE = 24;
    int PASSWORD = 25;
    int IDCARD = 26;
    int BIRTHDAY = 27;
    int ANNIVERSARYTYPE = 28;
    int ANNIVERSARY = 29;
    int BANKNUMBER = 30;

    String strForImportItem = "导入雇员数据";
    String describeForImportItem = "导入外部雇员信息文件";
    int[] importableFieldsIdx = { SUBJECT, CONTENT, FNAME, NAME, NNAME, TITLE, PHONE, ADDRESS, CPHONE, EMAIL, CNUMBER,
            CTYPE, WEBPAGE, SEX, RELATION, CATEGORY, JOINTIME, SALARY, INSURANCE, SSCNUMBER, CODE, PASSWORD, IDCARD,
            BIRTHDAY, ANNIVERSARYTYPE, ANNIVERSARY, BANKNUMBER };
    String[] RecommendCols = { "显示为", "姓", "名", "昵称", "职位", "电子邮件" };

    /** 用来初始化pim系统在database中的缺省值 */
    final String INSERT_INTO =
            "INSERT INTO VIEWINFO(VIEWNAME, VIEWTYPE, FIELDNAMES, FIELDWIDTHS, HASEDITOR, HASPREVIEW, FOLDERID, UNREADED, NUMBER, SYCNSETTING, SERVERFOLDER) VALUES(";

    final String VIEW_NAME_DEFAULT = "研发部";
    final String VIEW_NAME_1 = "研发部";
    final String VIEW_NAME_2 = "市场部";
    final String VIEW_NAME_3 = "客服部";
    final String VIEW_NAME_4 = "行政部";

    // 根节点
    final String VIEWINFO_DEFAULT =
            "', 0, '0,24,5,6,3,7,16,8,20,21,18,11,12,13,14,15,4,9,10,17,22,23,25', '0,60,80,40,40,80,40,60,90,50,60,100,200,110,100,220,200,120,370,140,50,120,70', FALSE, FALSE, 5000, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_DEFAULT = INSERT_INTO.concat("'").concat(VIEW_NAME_DEFAULT).concat(VIEWINFO_DEFAULT);
    // 计件员工
    final String VIEWINFO_1 =
            "', 0, '0,24,5,6,3,7,16,8,18,11,12,20', '0,40,40,60,120,60,80,80,60,100,200,90', FALSE, FALSE, 5001, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_1 = INSERT_INTO.concat("'").concat(VIEW_NAME_1).concat(VIEWINFO_1);
    // 普通员工
    final String VIEWINFO_2 =
            "', 0, '0,24,3,16,8,20,21,22,23,10,9,11,12,4,17', '0,60,80,40,60,90,50,50,120,370,120,100,200,200,140', FALSE, FALSE, 5002, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_2 = INSERT_INTO.concat("'").concat(VIEW_NAME_2).concat(VIEWINFO_2);
    // 保密型--一般员工可能不应该看到过多的信息.
    final String VIEWINFO_3 =
            "', 0, '0,24,3,7,16,8,11,12,13,14,15', '0,60,80,80,40,60,100,200,110,100,220', FALSE, FALSE, 5003, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_3 = INSERT_INTO.concat("'").concat(VIEW_NAME_3).concat(VIEWINFO_3);
    // 详细型
    final String VIEWINFO_4 =
            "', 0, '0,24,3,7,16,8,20,11,12,13,14,15,4,18,5,6,9,10,17,21,22,23,25', '0,60,80,80,40,60,90,100,200,110,100,220,200,60,40,40,120,370,140,50,50,120,70', FALSE, FALSE, 5004, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_4 = INSERT_INTO.concat("'").concat(VIEW_NAME_4).concat(VIEWINFO_4);

    final String[] INIT_DB_VIEWINFO = new String[] { INSERT_VIEWINF_DEFAULT, INSERT_VIEWINF_1, INSERT_VIEWINF_2,
            INSERT_VIEWINF_3, INSERT_VIEWINF_4 };
}
