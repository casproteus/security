package org.cas.client.platform.refund;

public interface RefundDefaultViews {
    String[] TEXTS = { "ID", "已删除", "位置", "主题", "备注", "营业员", "时间", "产品", "数量", "总价", "折扣", "客户", "欠款", "类别", "毛利" };

    String[] FIELDS = { "ID", "DELETED", "FOLDERID", "SUBJECT", "CONTENT", "EMPLOYEEID", "TIME", "PRODUCTID", "AMOUNT",
            "TOLTALPRICE", "DISCOUNT", "CONTACTID", "ARREARAGE", "CATEGORY", "PROFIT" };
    String[] TYPES = { "INTEGER IDENTITY PRIMARY KEY", "BIT DEFAULT false", "INTEGER", "VARCHAR(255)", "VARCHAR(255)",
            "INTEGER", "VARCHAR(255)", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER",
            "VARCHAR(255)", "INTEGER" };
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
    int CONTACTID = 11;
    int ARREARAGE = 12;
    int CATEGORY = 13;

    /** 用来初始化pim系统在database中的缺省值 */
    final String INSERT_INTO =
            "INSERT INTO VIEWINFO(VIEWNAME, VIEWTYPE, FIELDNAMES, FIELDWIDTHS, HASEDITOR, HASPREVIEW, FOLDERID, UNREADED, NUMBER, SYCNSETTING, SERVERFOLDER) VALUES(";

    final String VIEW_NAME_DEFAULT = "退货";
    final String VIEW_NAME_1 = "服务";
    final String VIEW_NAME_2 = "商品";
    // 销售
    final String VIEWINFO_DEFAULT =
            "', 0, '0,7,8,9,10,12,5,6,4,11', '0,130,40,70,70,80,80,90,280,80', FALSE, FALSE, 5700, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_DEFAULT = INSERT_INTO.concat("'").concat(VIEW_NAME_DEFAULT).concat(VIEWINFO_DEFAULT);
    // 服务记录
    final String VIEWINFO_1 =
            "', 0, '0,7,8,6,5,11,4,8,9,10', '0,100,40,160,120,100,150,40,70,70', FALSE, FALSE, 5701, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_1 = INSERT_INTO.concat("'").concat(VIEW_NAME_1).concat(VIEWINFO_1);
    // 零售记录
    final String VIEWINFO_2 =
            "', 0, '0,7,8,6,5,11,4,9,10,12', '0,130,40,90,80,80,280,70,70,80', FALSE, FALSE, 5702, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_2 = INSERT_INTO.concat("'").concat(VIEW_NAME_2).concat(VIEWINFO_2);

    final String[] INIT_DB_VIEWINFO = new String[] { INSERT_VIEWINF_DEFAULT, INSERT_VIEWINF_1, INSERT_VIEWINF_2 };
}
