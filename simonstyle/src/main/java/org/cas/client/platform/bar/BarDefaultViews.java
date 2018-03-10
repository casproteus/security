package org.cas.client.platform.bar;

public interface BarDefaultViews {
    /** 用来初始化pim系统在database中的缺省值 */
    final String INSERT_INTO =
            "INSERT INTO VIEWINFO(VIEWNAME, VIEWTYPE, FIELDNAMES, FIELDWIDTHS, HASEDITOR, HASPREVIEW, FOLDERID, UNREADED, NUMBER, SYCNSETTING, SERVERFOLDER) VALUES(";

    final String VIEW_NAME_DEFAULT = "Pos前台";

    final String VIEWINFO_DEFAULT = "', 5, '', '', FALSE, FALSE, 5600, 0, 0, 0, ' ');";
    final String INSERT_VIEWINF_DEFAULT = INSERT_INTO.concat("'").concat(VIEW_NAME_DEFAULT).concat(VIEWINFO_DEFAULT);

    final String[] INIT_DB_VIEWINFO = new String[] { INSERT_VIEWINF_DEFAULT };

}
