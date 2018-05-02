package org.cas.client.platform.pimmodel;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.magicbath.dialog.MagicbathDlgConst;
import org.cas.client.platform.pimmodel.util.ModelConstants2;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.IntlModelConstants;
import org.cas.client.resource.international.PaneConsts;

/**
 * 这个类的用途是通过给定的驱动程序、用户名、密码连接到使用的数据库上。而驱动程序、用户名、密码 则可以改变，甚至在程序运行的过程中就可以改变，这是以后要求多用户时候要做的事情。所以应该现在 应该只是建立连接而后检查数据库等操作在这里完成
 */

class PIMDBConnecter {
    // TODO：结构调整的时候需要将这几个常量的信息放入数据字典中或者资源文件中，方便修改，现在这样混在一起结构很不清晰
    private static final String defaultDriver = "org.hsqldb.jdbcDriver";

    // private static final String defaultDriver = "org.apache.derby.jdbc.EmbeddedDriver";

    private static final String defaultUserName = "sa";

    private static final String defaultPassword = CASUtility.EMPTYSTR;

    // 数据库在当前磁盘文件上的路径.例C:\Documents and Settings\Administrator\.storm0711\database\pim
    // 设置PIM 数据库路径、PIM数据库URL
    private static final String url = "jdbc:hsqldb:".concat(Utility.getPIMDatabaseDirPath()).concat(PIMDBModel.dbName);

    protected static PIMDBConnecter instance = new PIMDBConnecter();

    /**
     * check if database is created and valid.
     * 
     * @return: boolean
     * @NOTE: if return false, possible reason could be: 1/Manager throw out exception when creating a connection base
     *        on the URL(haven't seen happened) 2/Manager created connection successfully, but there's no database yet
     *        in the location where the URL pointing to, so Manager.connect() method will generate a .log file and a
     *        .properties file then return true, while considering that this method didn't found any table in the given
     *        database, so it retrned false.
     */
    boolean checkDatabase() {
        connection = buildConnection(defaultUserName, defaultPassword);
        if (connection == null) {
            JOptionPane.showMessageDialog(null, DlgConst.UNNORMALCLOSED);
            connection = buildConnection(defaultUserName, defaultPassword);
            if(connection == null) {
            	ErrorUtil.write("Cannot generate coneection to db, system exit.");
            	System.exit(0);
            }
        }

        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM SYSTEMINFO");
            dbValidation = (rs != null && rs.next());
        } catch (Exception e) {
            dbValidation = false;
        }

        if (!dbValidation) {// 建立数据库连接,并且检查数据库,检查数据库是否为空数据库//数据库连接正常则返回,不正常进入.
            try {
                createPIMDatabase();// 尝试建立一个新的数据库.
                ResultSet rs = connection.createStatement().executeQuery("select * from systeminfo");
                dbValidation = (rs != null && rs.next());
            } catch (Exception e) {
                dbValidation = false;
                e.printStackTrace();
            }
            reConnectDb();
        }
        return dbValidation;
    }

    /**
     * 得到数据库的连接 如果数据库没有连接上，则连接数据库，如果连接上了则返回连接
     * 
     * @return
     * @throws SQLException
     */
    Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed())
            return connection;
        else if (connection != null) {
            connection.close();
            connection = null;
            ErrorUtil.write("the connection was not closed by last Action.");
        }
        // 进行补救,使程序能继续进行=====================
        connection = buildConnection(defaultUserName, defaultPassword);
        if (!dbValidation) // It's somewhat funny! dose it offen need to connect twice?
        {
            ErrorUtil
                    .write("God! You can't believe it, method getConnection() failed! now System is ready to connec again!");
            connection = buildConnection(defaultUserName, defaultPassword);
        }
        return dbValidation ? connection : null;
    }

    /**
     * 获得数据库的字段名信息
     * 
     * @return: Hashtable
     */
    protected Hashtable getSystemTableFieldName() {
        Hashtable fieldNamesHash = new Hashtable();
        Vector tableNames = CustOpts.custOps.APPNameVec;
        int tableCount = tableNames.size();

        for (int i = 0; i < tableCount; i++) {
            String tableName = ((String) tableNames.get(i)).toUpperCase();
            Vector columnNames = new Vector();
            String[] oneTableField = ModelConstants2.SYSTEMTABLE_FIELD_LIST[i * 2];
            // String[] fieldTypeList = DefaultDBInfo.TABLE_FIELD_LIST[i * 2 + 1];
            int fieldCount = oneTableField.length;
            for (int j = 0; j < fieldCount; j++)
                columnNames.add(oneTableField[j].toUpperCase());

            fieldNamesHash.put(tableName, columnNames);
        }
        return fieldNamesHash;
    }

    /**
	 */
    protected void initCategory(
            Connection conn,
            Statement stmt) throws SQLException {
        String[] cate = IntlModelConstants.DEFAULT_CATEGORY;
        int count = cate.length;
        StringBuffer sb = new StringBuffer(); // 数据库执行的SQL语句

        for (int i = 0; i < count; i++) // 在CATEGORY表中插入记录
        {
            sb.setLength(0);
            sb.append("INSERT INTO CATEGORY VALUES(");
            sb.append(i).append(",'")
              .append(cate[i]).append("',")
              .append(i).append(");");
            stmt.executeUpdate(sb.toString());
        }
    }

    /**
     * 关闭连接
     * 
     * @TODO:改为线程中队列末尾关闭,并要求多次合并.
     */
    void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed())
                    connection.close();
                connection = null;
            } catch (SQLException e) {
                connection = null;
                e.printStackTrace();
            }
        }
    }

    /**
     * @关闭后重新打开当前连接。方便内存回收
     */
    void reConnectDb() {
        // SwingUtilities.invokeLater(new Runnable() {
        // @Override
        // public void run() {
        if (connection != null)
            try {
                if (!connection.isClosed())
                    connection.close();
                connection = buildConnection(defaultUserName, defaultPassword);
            } catch (SQLException e) {
                connection = null;
                e.printStackTrace();
            }
        // }
        // });
    }

    /**
     * 取得指定表中的下一条记录的id值
     *
     * @param tableName
     *            指定的数据库中的表名
     * @return int 下一条记录的id值
     */
    int getNextID(
            String tableName) {
        int ID = -1;
        String sql = "select max (ID)+1 from ".concat(tableName);
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            rs.next();
            ID = rs.getInt(1);
            st.close();
        } catch (SQLException se) {
            reConnectDb(); // se.printStackTrace();
        }
        return ID;
    }

    /*
     * create a database, by default it will create a main table for each application, while will not create table for
     * sub folders
     * @called by PIMDBModel.checkDatabase();
     * @return: boolean
     */
    private boolean createPIMDatabase() throws SQLException {
        if (createDefaultDB())// 检测是否需要建一个Default数据库,如果需要,则关闭现有数据库,建立默认数据,重新建立链接.
            return true;

        StringBuffer tmpSQL = new StringBuffer();
        Statement tmpStateMent = connection.createStatement(); // 建立数据库连接执行状态
        for (int i = ModelConstants2.SYSTEMTABLE_NAME_LIST.length - 1; i >= 0; i--) { // 先把固定的表建好。
            tmpSQL.setLength(0);
            tmpSQL.append("CREATE CACHED TABLE ").append(ModelConstants2.SYSTEMTABLE_NAME_LIST[i]).append(" (");
            String[] fieldList = ModelConstants2.SYSTEMTABLE_FIELD_LIST[i * 2];
            String[] fieldTypeList = ModelConstants2.SYSTEMTABLE_FIELD_LIST[i * 2 + 1];
            int tmpLength = fieldList.length;
            for (int j = 0; j < tmpLength; j++)
                tmpSQL.append(fieldList[j]).append(fieldTypeList[j]);
            tmpSQL.append(");");
            try {
                tmpStateMent.executeUpdate(tmpSQL.toString());
            } catch (Exception e) {
                System.out.println(tmpSQL.toString() + e);
            }
        }

        CustOpts.custOps.setSysTableNames(ModelConstants2.SYSTEMTABLE_NAME_LIST);
        dbValidation = true;

        // 初始化数据库VIEWINFO表和类型表以及系统信息表
        for (int i = 0; i < ModelConstants2.INIT_DB_CONSTANTS.length; i++)
            // 先初始化系统表的ViewInfo-------
            tmpStateMent.executeUpdate(ModelConstants2.INIT_DB_CONSTANTS[i]);
        // 这里不需要通知各个应用去初始化自己需要的数据库表，因为：
        // 1.在第一次使用时，FileCustOption在比较已安装模块时，会发现用户目录下的配置文件中缺少模块，并逐个通知其应用去初始化相关表。
        // 2.以后如果数据库被中途删除，那么就等着报警吧，重新修复个空表也没有什么意义。
        // for(int i = 0, len = CustOpts.custOps.APPNameVec.size(); i < len; i++) //再初始化各个应用的ViewInfo------
        // MainPane.getApp((String) CustOpts.custOps.APPNameVec.get(i)).initInfoInDB();

        //why do we neet to give a default category set ? initCategory(connection, tmpStateMent);
        initSystemInfo(connection, tmpStateMent); // 此表用来记录当前系统的版本信息,升级时使用
        initDefaulUser(connection, tmpStateMent);

        tmpStateMent.close();
        tmpStateMent = null;
        return true;
    }

    private boolean createDefaultDB() {
        String tSrcPath = CASControl.ctrl.getSourcePath();
        // when is debugging on source code, will use the db under simonstyle.
        if (tSrcPath.indexOf("/org/") > 0) {
            tSrcPath = tSrcPath.substring(0, tSrcPath.indexOf("/org/") + 1);
        }
        tSrcPath =
                tSrcPath.concat(".Storm070111").concat(System.getProperty("file.separator")).concat("database")
                        .concat(System.getProperty("file.separator")).concat("pim");
        if (!new File(tSrcPath.concat(".backup")).exists())
            return false;

        try { // 关闭数据库.
            Statement stmt = connection.createStatement();
            boolean success = stmt.execute("SHUTDOWN");
            stmt.close();// 关闭
            closeConnection();
        } catch (SQLException se) {
            PIMDBConnecter.instance.closeConnection();
            se.printStackTrace();
        }

        String tDesPath =
                System.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".Storm070111")
                        .concat(System.getProperty("file.separator")).concat("database")
                        .concat(System.getProperty("file.separator")).concat("pim");

        CASUtility.startBackUp(tSrcPath, tDesPath, "backup");
        CASUtility.startBackUp(tSrcPath, tDesPath, "data");
        CASUtility.startBackUp(tSrcPath, tDesPath, "properties");
        CASUtility.startBackUp(tSrcPath, tDesPath, "script");

        tSrcPath = CASControl.ctrl.getSourcePath();
        tSrcPath = tSrcPath.substring(0, tSrcPath.indexOf("/org/") + 1);
        tSrcPath = tSrcPath.concat(".Storm070111").concat(System.getProperty("file.separator")).concat("PIMConfig");
        tDesPath =
                System.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".Storm070111")
                        .concat(System.getProperty("file.separator")).concat("PIMConfig");

        CASUtility.startBackUp(tSrcPath, tDesPath, "ini");
        connection = buildConnection(defaultUserName, defaultPassword);// 重新建立连接
        return true;
    }

    /**
     * 建立连接 @NOTE: 如果本来URL位置处没有数据库的话,建立连接也能成功(DriverManager在URL指定的位置处建立一个
     * .log文件和一个.properties文件.这时候本方法将返回true.如果DriverManager建连接时遇到异常,本方法才返回false.
     * 
     * @param: String user 要连接的用户名，不允许为空
     * @param: String pwd 要连接用户的密码，不允许为空
     * @return: boolean
     */
    private Connection buildConnection(
            String user,
            String pwd) {
        if (!isDriverLoaded && !loadDriver()) { // 如果驱动程序没有加载,且加载也加载不成功,则返回.
            dbValidation = false;
            return null;
        } else {
            try {
                Connection tConn = DriverManager.getConnection(url, user, pwd); // 得到数据库的连接.derby的参数:(url.concat(";create=true"));
                dbValidation = true;
                return tConn;
            } catch (SQLException e) {
                dbValidation = false;
            }
            return null;
        }
    }

    /** 加载驱动程序 */
    private boolean loadDriver() {
        try {
            Class.forName(defaultDriver).newInstance();
            isDriverLoaded = true;
        } catch (Exception e) {
            isDriverLoaded = false;
        }
        return isDriverLoaded;
    }

    /** 初始化系统信息表,把当前系统的版本号插入到SYSTEMINFO表中 */
    private void initSystemInfo(
            Connection conn,
            Statement stmt) throws SQLException {
        String str =
                "INSERT INTO SYSTEMINFO (VERSION) VALUES ('".concat(PaneConsts.TITLE.concat(MagicbathDlgConst.Version))
                        .concat("')");
        stmt.executeUpdate(str); // 0:the Version of the software first used to access this DB.
        str = "INSERT INTO SYSTEMINFO (VERSION) VALUES ('".concat(CASUtility.getSerialNumber()).concat("')");
        stmt.executeUpdate(str); // 1:the SN of the software first used to access this DB.
        str = "INSERT INTO SYSTEMINFO (VERSION) VALUES ('1')";
        stmt.executeUpdate(str); // 2:this value will be used to indicate real started time.
        str = "INSERT INTO SYSTEMINFO (VERSION) VALUES ('0')";
        stmt.executeUpdate(str); // 3:this value will be used to indicate real totle received money.
        str = "INSERT INTO SYSTEMINFO (VERSION) VALUES ('0')";
        stmt.executeUpdate(str); // 4:this value will be used to indicate real totle paid money.
        str =
                "INSERT INTO SYSTEMINFO (VERSION) VALUES ('".concat(
                        String.valueOf(Calendar.getInstance().get(Calendar.YEAR))).concat("')");
        stmt.executeUpdate(str); // 5:indicate the YearNumber the soft first used.
        str =
                "INSERT INTO SYSTEMINFO (VERSION) VALUES ('".concat(
                        String.valueOf(Calendar.getInstance().get(Calendar.MONTH))).concat("')");
        stmt.executeUpdate(str); // 6:indicate the YearNumber the soft first used.
        str =
                "INSERT INTO SYSTEMINFO (VERSION) VALUES ('".concat(
                        String.valueOf(Calendar.getInstance().get(Calendar.DATE))).concat("')");
        stmt.executeUpdate(str); // 7:indicate the YearNumber the soft used last time.
    }

    private void initDefaulUser(
            Connection conn,
            Statement stmt) throws SQLException {
        String str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('Tropical_fish', '345', 432, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('妙音居士', '453', 296, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('Grace', '21', 0, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('王竖', '328', 132, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('小童', '378', 0, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('战', '8', 12, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('admin', '5555', 2, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('user1', '1111', 1, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('user2', '2222', 1, 0)";
        stmt.executeUpdate(str);
        str = "INSERT INTO UserIdentity (UserName, Password, type, LANG) VALUES ('user3', '3333', 1, 0)";
        stmt.executeUpdate(str);
    }

    private Connection connection;
    private boolean isDriverLoaded;
    private boolean dbValidation;
}

// private InputStream fis;
// /** Creates a new instance of PIMDBContext */
// private PIMDBConnecter()
// {
// String className = getClass().getName();
// //以下四个变量的取值据说都是将来在支持多用户时有用,目前可以看到的是系统没有被设置过相关值,getProperty方法返回的都是参数(默认值).
// driver = System.getProperty(className.concat(".driver"), "org.hsqldb.jdbcDriver");
// url = System.getProperty(className.concat(".url"), "jdbc:hsqldb:.");
// userName = System.getProperty(className.concat(".username"), "sa");
// password = System.getProperty(className.concat(".password"), PIMUtility.EMPTYSTR);
// }

// /** 返回视图信息
// * @param int appType
// * @param int appSubType
// * @return PIMViewInfo
// */
// protected PIMViewInfo initViewInfo(int appType, int appSubType)
// {
// StringBuffer sql = new StringBuffer();
// sql.append("SELECT * FROM ").append(ModelDBConstants.VIEWINFO_TABLE_NAME); //VIEWINFO表名字
// sql.append(" WHERE APPTYPE = ").append(appType);
//
// PIMViewInfo viewInfo = new PIMViewInfo();
// try
// {
// Connection conn = PIMDBConnecter.instance.getConnection(); //得到数据库连接
// Statement smt = conn.createStatement();
// ResultSet rs = smt.executeQuery(sql.toString());
// ResultSetMetaData rsmd = rs.getMetaData();
// while (rs.next())
// {
// viewInfo.enrichFromRS(rs, rsmd);
// viewInfo.setAppIndex(appType);
// viewInfo.setAppSubType(appSubType);
//
// if (appType == ModelConstants.DELETED_ITEM_APP) //已删除项TABLE没有编辑条
// {
// viewInfo.setHasEditor(false);
// }
// }
//
// rs.close();
// rs = null;
// smt.close();
// smt = null;
// PIMDBConnecter.instance.closeConnection();
// }
// catch (SQLException se)
// {
// PIMDBConnecter.instance.closeConnection();
// se.printStackTrace();
// }
// catch (NumberFormatException nfe)
// {
// PIMDBConnecter.instance.closeConnection();
// nfe.printStackTrace();
// }
// return viewInfo;
// }
// 数据库连接池
// static
// {
/*
 * try { String home = System.getProperty ("user.home"); String sep = System.getProperty ("file.separator"); String
 * strDataDirectory = home + sep + ".pim"; String strDBPath = strDataDirectory + sep + "pim"; String dbUrl =
 * "jdbc:hsqldb:" + strDBPath + "/pim"; File dataDir = new File (strDataDirectory); if (!dataDir.exists ()) { if
 * (!dataDir.mkdirs ()) { System.exit (0); } } //new PIMConnectionDriver ("org.hsqldb.jdbcDriver", dbUrl, "sa",
 * PIMUtility.EMPTYSTR); } catch (Exception e) { e.printStackTrace (); }
 */
// }
// private String schemaFile = "/org/cas/client/platform/pimmodel/database/schema.xml";
// private String fileName =
// "/org/cas/client/platform/pimmodel/database/hsql.sql";//getClass().getResource("hsql.sql").getFile();///client/platform/pimmodel/databasel/
