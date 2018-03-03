package org.cas.client.platform.foregrounding;

public interface ForegroundingDefaultViews {
    /** 用来初始化pim系统在database中的缺省值 */
    final String INSERT_INTO =
            "INSERT INTO VIEWINFO(VIEWNAME, VIEWTYPE, FIELDNAMES, FIELDWIDTHS, HASEDITOR, HASPREVIEW, FOLDERID, UNREADED, NUMBER, SYCNSETTING, SERVERFOLDER) VALUES(";

    final String VIEW_NAME_DEFAULT = "店面前台";

    final String VIEWINFO_DEFAULT =
            "', 5, '0,5,3,9,7,6,8,4,10', '0,50,150,60,50,80,120,300,60', FALSE, FALSE, 5500, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_DEFAULT = INSERT_INTO.concat("'").concat(VIEW_NAME_DEFAULT).concat(VIEWINFO_DEFAULT);

    final String[] INIT_DB_VIEWINFO = new String[] { INSERT_VIEWINF_DEFAULT };
}
