package org.cas.client.platform.input;

import org.cas.client.platform.cascustomize.CustOpts;

/**
 * 该应用不能支持CVS外部数据导入,因为用到了ForeignKey,对于这类字段,外部数据如果用int表示,则导入时意义多半将发生错误, 如果用文本表示,则类型转换将过于烦琐和低效.
 */
public interface InputDefaultViews {
    String[] TEXTS = { "ID", "已删除", "位置", "主题", "备注", "供应商", "时间", "产品", "数量", "总价", "折扣", "其他开销", "欠款", "类别" };

    String[] FIELDS = { "ID", "DELETED", "FOLDERID", "SUBJECT", "CONTENT", "EMPLOYEEID", "TIME", "PRODUCTID", "AMOUNT",
            "TOLTALPRICE", "DISCOUNT", "OTHERCOST", "ARREARAGE", "CATEGORY" };
    String[] TYPES = { "INTEGER IDENTITY PRIMARY KEY", "BIT DEFAULT false", "INTEGER", "VARCHAR", "VARCHAR", "INTEGER",
            "TIMESTAMP", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "VARCHAR" };
    int ID = 0;
    int DELETED = 1;
    int FOLDERID = 2;
    int SUBJECT = 3;
    int CONTENT = 4;
    int EMPLOYEEID = 5;
    int TIME = 6;
    int PRODUCTID = 7;
    int AMOUNT = 8;
    int TOLTALPRICE = 9;
    int DISCOUNT = 10;
    int OTHERCOST = 11;
    int ARREARAGE = 12;
    int CATEGORY = 13;

    /** 用来初始化pim系统在database中的缺省值 */
    final String INSERT_INTO =
            "INSERT INTO VIEWINFO(VIEWNAME, VIEWTYPE, FIELDNAMES, FIELDWIDTHS, HASEDITOR, HASPREVIEW, FOLDERID, UNREADED, NUMBER, SYCNSETTING, SERVERFOLDER) VALUES(";

    final String VIEW_NAME_DEFAULT = "今年";
    final String VIEW_NAME_1 = "今年";
    final String VIEW_NAME_2 = "去年";
    final String VIEW_NAME_3 = "前年";

    final String VIEWINFO_DEFAULT =
            "', 0, '0,6,7,8,9,10,11,12,5,4', '0,90,130,40,70,70,80,70,80,280', FALSE, FALSE, 5200, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_DEFAULT = INSERT_INTO.concat("'").concat(VIEW_NAME_DEFAULT).concat(VIEWINFO_DEFAULT);

    final String VIEWINFO_1 =
            "', 0, '0,7,8,9,10,11,12,6,4,5', '0,130,40,70,70,80,70,90,280,80', FALSE, FALSE, 5201, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_1 = INSERT_INTO.concat("'").concat(VIEW_NAME_1).concat(VIEWINFO_1);

    final String VIEWINFO_2 =
            "', 0, '0,6,7,8,9,10,11,12,5,4', '0,90,130,40,70,70,80,70,80,280', FALSE, FALSE, 5202, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_2 = INSERT_INTO.concat("'").concat(VIEW_NAME_2).concat(VIEWINFO_2);

    final String VIEWINFO_3 =
            "', 0, '0,7,8,9,10,11,12,6,4,5', '0,130,40,70,70,80,70,90,280,80', FALSE, FALSE, 5203, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_3 = INSERT_INTO.concat("'").concat(VIEW_NAME_3).concat(VIEWINFO_3);

    final String[] INIT_DB_VIEWINFO = new String[] { INSERT_VIEWINF_DEFAULT, INSERT_VIEWINF_1, INSERT_VIEWINF_2,
            INSERT_VIEWINF_3 };

}
