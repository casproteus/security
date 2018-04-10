package org.cas.client.platform.product;


public interface ProductDefaultViews {

    String[] TEXTS = {
    /* 0 */"ID", "已删除", "位置", "品名", "备注",
    /* 5 */"条码", "单位", "售价", "产地", "品牌",
    /* 10 */"类别", "助记", "成本价格", "库存" };
    String strForImportItem = "导入产品资料";
    String describeForImportItem = "导入外部产品资料数据";
    int[] importableFieldsIdx = { 3, 4, 5, 6, 7, 8, 9, 10 };

    String[] FIELDS = { "ID", "DELETED", "FOLDERID", "SUBJECT", "CONTENT", "CODE", "UNIT", "PRICE", "PRODUCAREA",
            "BRAND", "CATEGORY", "MNEMONIC", "COST", "STORE" };
    String[] TYPES = { "INTEGER IDENTITY PRIMARY KEY", "BIT DEFAULT false", "INTEGER", "VARCHAR(255)", "VARCHAR(255)",
            "VARCHAR(255)", "VARCHAR(255)", "INTEGER", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)",
            "INTEGER", "INTEGER" };

    int ID = 0; // "ID",
    int DELETED = 1; // "已删除",
    int FOLDERID = 2; // "位置",
    int SUBJECT = 3; // "品名",
    int CONTENT = 4; // "备注"
    int CODE = 5; // "条码",
    int UNIT = 6;
    int PRICE = 7;
    int PRODUCAREA = 8;
    int BRAND = 9;
    int CATEGORY = 10;
    int MNEMONIC = 11;
    int COST = 12;

    /** 用来初始化pim系统在database中的缺省值 */
    final String INSERT_INTO =
            "INSERT INTO VIEWINFO(VIEWNAME, VIEWTYPE, FIELDNAMES, FIELDWIDTHS, HASEDITOR, HASPREVIEW, FOLDERID, UNREADED, NUMBER, SYCNSETTING, SERVERFOLDER) VALUES(";

    final String VIEW_NAME_DEFAULT = "足疗";
    final String VIEW_NAME_0 = "足疗";
    final String VIEW_NAME_1 = "桑拿";
    final String VIEW_NAME_2 = "零售";

    final String VIEWINFO_DEFAULT =
            "', 0, '0,5,3,9,7,6,8,4,10', '0,50,150,60,50,80,120,300,60', FALSE, FALSE, 5100, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_DEFAULT = INSERT_INTO.concat("'").concat(VIEW_NAME_DEFAULT).concat(VIEWINFO_DEFAULT);
    // 足疗
    final String VIEWINFO_1 =
            "', 0, '0,3,9,4,10,7,6,8,5', '0,130,60,280,60,50,40,100,50', FALSE, FALSE, 5101, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_1 = INSERT_INTO.concat("'").concat(VIEW_NAME_0).concat(VIEWINFO_1);
    // 桑拿
    final String VIEWINFO_2 =
            "', 0, '0,5,7,6,3,9,8,10,4', '0,50,50,40,130,60,100,60,280', FALSE, FALSE, 5102, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_2 = INSERT_INTO.concat("'").concat(VIEW_NAME_1).concat(VIEWINFO_2);
    // 零售
    final String VIEWINFO_3 =
            "', 0, '0,5,7,6,3,9,8,10,4', '0,50,50,40,130,60,100,60,280', FALSE, FALSE, 5103, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_3 = INSERT_INTO.concat("'").concat(VIEW_NAME_2).concat(VIEWINFO_3);

    final String[] INIT_DB_VIEWINFO = new String[] { INSERT_VIEWINF_DEFAULT, INSERT_VIEWINF_1, INSERT_VIEWINF_2,
            INSERT_VIEWINF_3 };
}
