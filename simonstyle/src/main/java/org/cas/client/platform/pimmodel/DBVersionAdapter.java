package org.cas.client.platform.pimmodel;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMDBUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.util.ModelConstants2;
import org.cas.client.resource.international.IntlModelConstants;

/**
 * 处理在版本更新时，数据库向前兼容问题，把旧版本数据库中的数据，读取到新版本数据库中
 */

class DBVersionAdapter {

    final int OLD_DB_TABLE_APP = 0; // 旧版本数据库
    final int TMP_DB_TABLE_APP = 1; // 临时表
    final int NEW_DB_TABLE_APP = 2; // 新版本数据库表
    final int FLG_DB_TABLE_APP = 3; // 标志表
    final int ALL_DB_TABLE_APP = 4; // 所有表
    // 数据库操作
    final int DB_OPERATION_RENAME = 5;
    final int DB_OPERATION_BACKUP = 6;
    private static final String DB_TEMP_TABLE_PREFIX = "PREFIX_TEMP_"; // 临时表的前缀
    private static final String DB_EXCEPTION_FLAG_PREFIX = "EXCEPTION_FLAG_"; // 标志表的前缀

    // 数据库Exception表，作为标志位表
    final String DB_EXCEPTION_FLAG_RENAME = DB_EXCEPTION_FLAG_PREFIX.concat("RENAME");
    final String DB_EXCEPTION_FLAG_CREATE = DB_EXCEPTION_FLAG_PREFIX.concat("CREATE");
    final String DB_EXCEPTION_FLAG_DELETE = DB_EXCEPTION_FLAG_PREFIX.concat("DELETE");
    final String DB_EXCEPTION_FLAG_IMPORT = DB_EXCEPTION_FLAG_PREFIX.concat("IMPORT");
    /** 创建的临时表字段和类型,这里为了以后扩张用的，可以记录一些版本更新的信息 */
    final String[] TEMP_DB_TABLE_FIELD = { "VERSION", };
    final String[] TEMP_DB_FIELD_TYPES = { " VARCHAR", };

    static int[] TYPE_INT_VALUE = new int[]/* TYPES所有值 */
    { -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 12, 91, 92, 93, 1111 };

    final int PREFIX_LEN = DB_TEMP_TABLE_PREFIX.length(); // 临时表的前缀的长度
    static int CUR_VERSION_UPDATE_FLAG = 0; // 版本号

    /** Creates a new instance of UpdateDB */
    private DBVersionAdapter() {
        initTypesList(); // 准备要转换的类型
        initEnableChangedFlag(); // 初始化应许转化的类型
    }

    // NOTE:此方法没有被使用
    // /**
    // * @删除原来的PIMConfig文件，备份当前的文件
    // */
    // private boolean deleteConfigFile()
    // {
    // String tmpConfigPath = PIMUtility.getConfigFilePath(); //得到配置文件路径
    // File tmpConfigFile = new File(tmpConfigPath);
    //
    // return !tmpConfigFile.isFile() ? true :
    // tmpConfigFile.isFile() && tmpConfigFile.delete() && !tmpConfigFile.isFile();
    // }

    /**
     * 取旧版本数据表的名字
     */
    private ArrayList getOldTableNames() {
        return getTableNames(getConnection(), OLD_DB_TABLE_APP);
    }

    // NOTE:此方法没有被使用
    // /**
    // * 得到临时表的名字
    // */
    // private ArrayList getTempTableNames()
    // {
    // return getTableNames(getConnection(), TMP_DB_TABLE_APP);
    // }

    /**
     * @得到所有新表的名字
     */
    private ArrayList getNewTableNames() {
        return getTableNames(getConnection(), NEW_DB_TABLE_APP);
    }

    // /**
    // * @得到标志表的名字
    // */
    // private ArrayList getFlgTableNames()
    // {
    // return getTableNames(getConnection(), FLG_DB_TABLE_APP);
    // }

    /**
     * @取到所有表的名字
     */
    private ArrayList getAllTableNames() {
        return getTableNames(getConnection(), ALL_DB_TABLE_APP);
    }

    // /**
    // * 取旧版本数据库表的字段
    // */
    // private PIMDBTable getOldTableFields(String prmOldTableName)
    // {
    // return getTableFields(prmOldTableName, getConnection());
    // }

    /**
     * @检查临时表是否已经创建成功
     */
    private PIMDBTable getTempTableFields(
            String prmTempTableName) {
        return getTableFields(prmTempTableName, getConnection()); // 取临时表信息
    }

    /**
     * 取新版本数据库表的字段
     */
    private PIMDBTable getNewTableFields(
            String prmNewTableName) {
        return getTableFields(prmNewTableName, getConnection());
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 初始化数据库操作，检测是否已经更新数据库且更新是失败的 beg
    /**
     * @检查数据库是否已经更新，但更新失败
     */
    private void checkUpdatedException(
            int prmDBOprFLg) {
        curAllTableNames = getAllTableNames(); // 数据库所有表的名字
        if (!isContainsOtherTables()) // 不包含其它表
        {
            oldTableNameList = curAllTableNames; // 数据库所有的表都为旧数据库表，no Exception
        } else {
            // 如果为空,数据库出错
            processContainsFlgTable(getContainsFlgTableName(curAllTableNames), prmDBOprFLg);
            // 清除数据库中无用的数据库表
            clearOtherTables();
        }
    }

    /**
     * @处理包含标志表
     */
    private void processContainsFlgTable(
            String tmpFlgName,
            int prmDBOprFlg) {
        if (tmpFlgName.equals(DB_EXCEPTION_FLAG_RENAME)) // 改名操作 （创建临时表）
        {
            if (prmDBOprFlg == DB_OPERATION_RENAME) // 恢复到prime状态
            {
                // 恢复旧的数据库表的名字
                restoreOldTableName();
                // 删除标志表
                dropFlgTable(DB_EXCEPTION_FLAG_RENAME);
            } else if (prmDBOprFlg == DB_OPERATION_BACKUP) {
                // TODO: 数据库备份更新
            }
        } else if (tmpFlgName.equals(DB_EXCEPTION_FLAG_CREATE)) // 创建新表
        {
            // @NOTE: 临时表还没有改表，所以可以恢复到最初状态
            dropNewTables(); // 删除所有新建的表
            restoreOldTableName(); // 恢复旧的数据库表的名字
            dropFlgTable(DB_EXCEPTION_FLAG_CREATE); // 删除标志表
        } else if (tmpFlgName.equals(DB_EXCEPTION_FLAG_IMPORT)) // 导入操作
        {
            // @NOTE 临时表还没有删除，可以恢复到最初状态
            dropNewTables(); // 删除所有新建的表
            restoreOldTableName(); // 恢复旧的数据库表的名字
            dropFlgTable(DB_EXCEPTION_FLAG_CREATE); // 删除标志表
        } else if (tmpFlgName.equals(DB_EXCEPTION_FLAG_DELETE)) // 删除改名表（删除临时表）
        {
            // @NOTE: 临时表已经删除，无法恢复到最初状态*/
            dropTempTables(); // 删除临时表
        }
    }

    /**
     * @把所有的改名的表的名字改回
     */
    private boolean restoreOldTableName() {
        ArrayList tmpTep = getPrimeTempTableNames(curAllTableNames); // 数据库中的改名表列表
        // 没有改名的数据库表，不用把改名表改回原名
        if (tmpTep == null || tmpTep.isEmpty()) {
            return true;
        }
        // 默认改名成功
        boolean tmpIsSec = true;
        // 临时表和旧表
        String tmpTN, tmpON;
        for (int i = tmpTep.size() - 1; i >= 0; i--) {
            tmpTN = (String) tmpTep.get(i); // 改名表表名
            tmpON = getOldTableName(tmpTN); // 旧数据库表名
            // 判断改名操作是否失败
            if (tmpTN == null || tmpON == null || !renameTempToOldTableName(tmpTN, tmpON)) {
                tmpIsSec = false;
            }
        }
        return tmpIsSec;
    }

    /**
     * @判断当前的表中有无其它类型的表
     */
    private boolean isContainsOtherTables() {
        for (int i = curAllTableNames.size() - 1; i >= 0; i--) {
            String tmpCur = (String) curAllTableNames.get(i);
            /* 临时表、标志表 */
            if (tmpCur.startsWith(DB_TEMP_TABLE_PREFIX) || tmpCur.startsWith(DB_EXCEPTION_FLAG_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @判断是否包含标志表,一般只有一个表
     */
    private String getContainsFlgTableName(
            ArrayList prmCurAllTableNames) {
        for (int i = prmCurAllTableNames.size() - 1; i >= 0; i--) {
            String tmpTabN = (String) prmCurAllTableNames.get(i);
            if (tmpTabN.startsWith(DB_EXCEPTION_FLAG_PREFIX)) // 临时表
            {
                return tmpTabN;
            }
        }
        return null;
    }

    /**
     * @清除所有数据库中其它的表，保留旧数据库表
     * @note 表的名字为DB_EXCEPTION_FLAG_DELETE，表示当前的状态为导入数据已经完成， 但是它不一定是最新的数据库（可能是上几版本），所以还是把它作为旧的数据库来处理。
     *       注意：这里涉及根据数据验证版本号，有主意了：在做到删除临时是， 要在配置文件中记录下当前已经升级到的版本号（need modify must！～）
     */
    private void clearOtherTables() {
        curAllTableNames = getAllTableNames(); // 得到当前所有的表
        for (int i = curAllTableNames.size() - 1; i >= 0; i--) {
            String tmpTabName = (String) curAllTableNames.get(i);

            // 临时表不存在，不可以删除当前的数据库
            if (tmpTabName != null && tmpTabName.equals(DB_EXCEPTION_FLAG_DELETE)) {
                // 版本已经更新
                writeUpdateLogin(); // @TODO: 数据库出错
                dropFlgTable(DB_EXCEPTION_FLAG_DELETE); // 删除标志表
            }
            // 判断当前的数据库中有无临时表和标志表
            if (tmpTabName.startsWith(DB_EXCEPTION_FLAG_PREFIX) || tmpTabName.startsWith(DB_TEMP_TABLE_PREFIX)) // 没有用了，删掉
            {
                // 删除数据库表
                dropTable(getConnection(), tmpTabName);
            }
        }
    }

    /**
     * 记录当前的版本号已经升级了
     */
    private void writeUpdateLogin() {
    }

    /**
     * 取所有新建的表
     */
    private ArrayList getPrimeNewTableNames(
            ArrayList prmAllTableNameList) {
        ArrayList tmpList = new ArrayList();
        Iterator tmpIte = prmAllTableNameList.iterator();
        // 得到最初表的名字
        while (tmpIte != null && tmpIte.hasNext()) {
            String tmpName = (String) tmpIte.next();
            // 判断表的名字是否以临时表、标志表的名字开头
            if (tmpName != null && !tmpName.startsWith(DB_TEMP_TABLE_PREFIX)
                    && !tmpName.startsWith(DB_EXCEPTION_FLAG_PREFIX)) // 表名是以改名以后的名字
            {
                // 添加旧的表的名字
                tmpList.add(tmpName);
            }
        }
        return tmpList;
    }

    /**
     * @是否已经包含了改名的表,不包括标志位表，设置了标志位表是否存在
     */
    private ArrayList getPrimeTempTableNames(
            ArrayList prmAllTableNameList) {
        ArrayList tmpList = new ArrayList(); // 改名表名字列表
        Iterator tmpIte = prmAllTableNameList.iterator();
        // 得到临时表的名字
        while (tmpIte != null && tmpIte.hasNext()) {
            String tmpName = (String) tmpIte.next();
            if (tmpName.startsWith(DB_TEMP_TABLE_PREFIX)) // 表名是以改名以后的名字
            {
                tmpList.add(tmpName);
            }
        }
        return tmpList;
    }

    // end
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // /**
    // * @创建数据库所有表的临时表
    // */
    // private void createTempTables()
    // {
    // oldTableNameList = getOldTableNames();
    // if (oldTableNameList == null || oldTableNameList.size() < 1) //旧数据库的表的名字
    // {
    // isCreateTempTableSucceed = false; //没有表可怜？？
    // return;
    // }
    // String[] tmpNames = (String[]) oldTableNameList.toArray(new String[0]);//类型转换
    // for (int i = tmpNames.length - 1; i >= 0; i --)
    // {
    // pimOldDBTable = getOldTableFields(tmpNames[i]); //得到旧数据库的 PIMDBTable 信息
    // //判断建立临时表是否成功
    // if (!createTempTable(tmpNames[i])
    // || !isCreateTempTableSucceed(getTempTableName(tmpNames[i]))) //注意这里要判断当前的创建是否有错误，错误的时要重新创建
    // {
    // isCreateTempTableSucceed = false;
    // }
    // }
    // }
    /**
     * 创建标志表,默认创建为改名操作标志表 在这张表中加一些信息，例如当前版本号，修改的版本号等等信息， 因为很有可能是在更新一个版本的是否，出错了，但是下次安装更新为另一个版本号的
     */
    private boolean createFlgTable(
            String prmDefaultFlgTableName) {
        StringBuffer tmpSql = new StringBuffer();
        // 建立数据库命令
        tmpSql.append("CREATE CACHED TABLE ").append(prmDefaultFlgTableName).append(CASUtility.LEFT_BRACKET);
        tmpSql.append(TEMP_DB_TABLE_FIELD[0]).append(TEMP_DB_FIELD_TYPES[0]);

        for (int i = 1; i < TEMP_DB_TABLE_FIELD.length; i++) {
            tmpSql.append(TEMP_DB_TABLE_FIELD[i]).append(TEMP_DB_FIELD_TYPES[i]);
        }

        tmpSql.append(");");
        // 建立连接，建立数据库表
        try {
            Statement tmpSmt = getConnection().createStatement();
            tmpSmt.executeUpdate(tmpSql.toString());
            tmpSmt.close();
            tmpSmt = null;
        } catch (SQLException sqle) {
            PIMDBConnecter.instance.reConnectDb();
            return false;
        }

        return true;
    }

    /**
     * @数据库改名
     */
    private void renameOldTables() {
        oldTableNameList = getOldTableNames(); // 从新取
        // 得到旧的表的尺寸
        int tmpSize;
        // 判断有无旧表名
        if (oldTableNameList == null || (tmpSize = oldTableNameList.size()) < 1) // 旧数据库的表的名字
        {
            isCreateTempTableSucceed = false; // 没有表可怜？？
            return;
        }
        //
        for (int i = tmpSize - 1; i >= 0; i--) {
            String tmpNames = (String) oldTableNameList.get(i);
            // 建立数据库表
            if (!excuteCreateTable(getConnection(), buildRenameSqlSentence(tmpNames, getTempTableName(tmpNames)))) {
                isCreateTempTableSucceed = false;
            }
        }
    }

    /**
     * @改表表的名字
     */
    private boolean renameTableName(
            String prmFromName,
            String prmToName) {
        try {
            Statement tmpSmt = getConnection().createStatement(); // 建立状态
            tmpSmt.executeUpdate(buildRenameSqlSentence(prmFromName, prmToName)); // 生成改名的sql语句

            tmpSmt.close();
        } catch (SQLException sqle) {
            PIMDBConnecter.instance.reConnectDb();
            return false;
        }

        return true;
    }

    /**
     * @改名标志表的名字
     */
    private boolean renameFlgTable(
            String prmFromName,
            String prmToName) {
        return renameTableName(prmFromName, prmToName);
    }

    /**
     * @把名字恢复成旧数据库表的名字
     */
    private boolean renameTempToOldTableName(
            String prmTempName,
            String prmOldName) {
        return renameTableName(prmTempName, prmOldName);
    }

    // /**
    // * @判断当前的临时表是否已经建立
    // */
    // private boolean isCurExistTempTable(String prmTempTableName)
    // {return false;}
    /**
     * @取建立临时表是否有错误
     */
    private boolean getCreateTempTableSucceedFlag() {
        return isCreateTempTableSucceed;
    }

    /**
     * @生成字段名字列表
     */
    private ArrayList getFieldNames(
            ArrayList prmFieldList) {
        ArrayList tmpFieldNameList = new ArrayList();
        for (int i = 0; i < prmFieldList.size(); i++) {
            PIMField tmpField = (PIMField) prmFieldList.get(i); // 取出所有的字段
            tmpFieldNameList.add(tmpField.getFieldName());
        }
        return tmpFieldNameList;
    }

    // /**
    // * @是否临时表创建时候成功
    // */
    // private boolean isCreateTempTableSucceed(String prmTempTableName)
    // {
    // pimTepDBTable = getTempTableFields(prmTempTableName); //得到临时表的字段信息
    // Hashtable tmpOldHash = pimOldDBTable.getFieldHashtable();
    // Hashtable tmpTempHash = pimTepDBTable.getFieldHashtable();
    // return (tmpOldHash != null && tmpTempHash != null
    // && !isTableFieldsChanged(tmpOldHash, tmpTempHash)); //表的字段属性改表
    // }

    // /**
    // * @建立数据库临时表
    // */
    // private boolean createTempTable(String prmTableName)
    // {
    // if (prmTableName == null || prmTableName.length() < 1) //注意这里要判断是否创建成功
    // {
    // return false;
    // }
    // //得到字段名
    // ArrayList tmpField = getFieldNames(getOldFieldList());
    // String tmpSqlStr = buildCreateSqlSentence(tmpField); //建立sql语句
    // return excuteCreateTable(getConnection(), tmpSqlStr); //建立临时表
    // }
    /**
     * @建表操作
     */
    private boolean excuteCreateTable(
            Connection prmConn,
            String prmSqlStr) {
        try {
            if (prmConn == null) // 连接失败
            {
                return false;
            }

            Statement tmpStatement = prmConn.createStatement();
            tmpStatement.executeUpdate(prmSqlStr); // 建表操作
            tmpStatement.close(); // 关闭连接
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            PIMDBConnecter.instance.reConnectDb(); // 关闭连接
            return false;
        }
    }

    // /**
    // * 得到旧版本数据库表所有字段列表
    // */
    // private ArrayList getOldFieldList()
    // {
    // return (pimOldDBTable == null)
    // ? null : new ArrayList(pimOldDBTable.getFieldHashtable().values());
    // }

    // /**
    // * @把改了名的表的名字恢复
    // */
    // private String buildResumeSqlSentence(String prmTepTableName)
    // {
    // StringBuffer tmpSql = new StringBuffer();
    // if (prmTepTableName != null && prmTepTableName.length() > 0)
    // {
    // String tmpOldTableName = getOldTableName(prmTepTableName);
    // tmpSql.append("ALTER TABLE ").append(prmTepTableName).append(" RENAME TO ").append(tmpOldTableName).append(";");
    // }
    // return tmpSql.toString();
    // }
    /**
     * @数据库改名
     */
    private String buildRenameSqlSentence(
            String prmSourceTableName,
            String prmTargerTableName) {
        StringBuffer tmpSql = new StringBuffer();
        if (prmSourceTableName != null && prmSourceTableName.length() > 0) {
            tmpSql.append("ALTER TABLE ").append(prmSourceTableName).append(" RENAME TO ").append(prmTargerTableName)
                    .append(";");
        }
        return tmpSql.toString();
    }

    // /**
    // * @生成数据库建表语句
    // */
    // private String buildCreateSqlSentence(ArrayList prmFieldList)
    // {
    // if (prmFieldList == null && prmFieldList.isEmpty())
    // {
    // return null;
    // }
    // //
    // String[] tmpNames = (String[]) prmFieldList.toArray(new String[0]);
    // StringBuffer tmpSql = new StringBuffer();
    // //得到旧的表的名字
    // String tmpOldName = pimOldDBTable.getTableName(); //得到当前表的名字
    // String tmpTempName = getTempTableName(tmpOldName); //得到临时表的名字
    // if (tmpTempName == null) //临时表表名为空
    // {
    // return null;
    // }
    // tmpSql.append("CREATE CACHED TABLE ").append(tmpTempName).append(PIMUtility.LEFT_BRACKET);
    // tmpSql.append(tmpNames[0]); //处理第一个字段
    // for (int tmpSize = tmpNames.length - 1, i = tmpSize; i >= 0; i --)
    // {
    // tmpSql.append(", ").append(tmpNames[tmpSize - i]);
    // }
    // tmpSql.append(") AS ( SELECT * FROM ").append(tmpOldName).append(");");
    // return tmpSql.toString();
    // }

    /**
     * @生成临时的表的名字
     */
    private String getTempTableName(
            String prmOldTableName) {
        return (prmOldTableName != null && prmOldTableName.length() > 0) ? DB_TEMP_TABLE_PREFIX.concat(prmOldTableName)
                : null;
    }

    /*
     * @旧表的名字
     */
    private String getOldTableName(
            String prmTempTableName) {
        return (prmTempTableName.indexOf(DB_TEMP_TABLE_PREFIX) < 0) ? null : prmTempTableName.substring(PREFIX_LEN);
    }

    /**
     * @删除标志表
     */
    private boolean dropFlgTable(
            String prmFlgTableName) {
        return dropTable(getConnection(), prmFlgTableName);
    }

    /**
     * @删除所有新建的数据库表
     */
    private boolean dropNewTables() {
        ArrayList tmpNews = getPrimeNewTableNames(curAllTableNames);
        if (tmpNews == null || tmpNews.size() < 1) {
            return true; // 已经删除
        }
        // 删除新建的表，默认设置为删除成功
        boolean isDroped = true;
        for (int i = tmpNews.size() - 1; i >= 0; i--) {
            // 删除数据库的新建的表
            if (dropTable(getConnection(), (String) tmpNews.get(i))) // 删除所有新建的数据库表
            {
                isDroped = false;
            }
        }
        return isDroped;
    }

    /**
     * @删除所有的临时表
     */
    private boolean dropTempTables() {
        int tmpSize;
        // 如果没有旧的表就没有所谓的临时表
        if (oldTableNameList == null || (tmpSize = oldTableNameList.size()) < 1) {
            return true;
        }
        // 默认为删除成功
        boolean isDroped = true;
        for (int i = tmpSize - 1; i >= 0; i--) {
            // 删除临时表
            if (!dropTable(getConnection(), getTempTableName((String) oldTableNameList.get(i)))) // 删除临时表
            {
                isDroped = false;
            }
        }
        return isDroped;
    }

    // /**
    // * @删除所有的旧的表的名字
    // */
    // private boolean dropOldTables()
    // {
    // //旧的表的名字
    // int tmpSize;
    // //判断是否存在旧的数据库表
    // if (oldTableNameList == null
    // || (tmpSize = oldTableNameList.size()) < 1)
    // {
    // return false;
    // }
    // //默认为删除成功
    // boolean isDroped = true;
    // for (int i = tmpSize - 1; i >= 0; i --)
    // {
    // if (!dropTable(getConnection(), (String) oldTableNameList.get(i)))
    // {
    // isDroped = false;
    // }
    // }
    // return isDroped;
    // }

    /**
     * @删除旧数据库表
     */
    private boolean dropTable(
            Connection prmConn,
            String prmOldTableName) {
        try {
            StringBuffer tmpDrop = new StringBuffer();
            tmpDrop.append("DROP TABLE ").append(prmOldTableName).append(";"); // 删除旧数据库表

            Statement tmpStatement = prmConn.createStatement();
            tmpStatement.executeUpdate(tmpDrop.toString());
            tmpStatement.close();
            return true;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            return false;
        }
    }

    /**
     * @初始化类型信息
     */
    protected void initCategoryInfo(
            Statement prmStmt) throws SQLException {
        StringBuffer tmpSql = new StringBuffer();

        int tmpSize = IntlModelConstants.DEFAULT_CATEGORY.length - 1;
        for (int i = tmpSize; i >= 0; i--) {
            tmpSql.ensureCapacity(0);
            tmpSql.append("INSERT INTO CATEGORY VALUES (");
            tmpSql.append(i).append(",'");
            tmpSql.append(IntlModelConstants.DEFAULT_CATEGORY[tmpSize - i]).append("');");
            // @NOTE: 问题？
            prmStmt.executeUpdate(tmpSql.toString());
        }
    }

    /**
     * @初始化数据库表信息
     */
    private void initViewInfo(
            Statement prmStmt) throws SQLException {
        int tmpCount = ModelConstants2.INIT_DB_CONSTANTS.length - 1;
        // 初始化数据库VIEWINFO表信息
        for (int i = tmpCount; i >= 0; i--) {
            prmStmt.executeUpdate(ModelConstants2.INIT_DB_CONSTANTS[tmpCount - i]);
        }
    }

    /**
     * @创建新表
     */
    private boolean createNewTables(
            Connection prmConn) {
        try {
            String[] tableNameStr = CustOpts.custOps.getTableNames();
            int tmpCount = tableNameStr.length; // 得到当前要创建的表的名字数组
            Statement tmpStatement = prmConn.createStatement();
            // 创建新的数据库
            for (int i = 0; i < tmpCount; i++) {
                StringBuffer tmpSql = new StringBuffer();
                tmpSql.append("CREATE TABLE ");
                tmpSql.append(tableNameStr[i]).append(CASUtility.LEFT_BRACKET);
                // 得到字段和字段对应的类型数组
                String[] tmpField = ModelConstants2.SYSTEMTABLE_FIELD_LIST[i * 2];
                String[] tmpTypeS = ModelConstants2.SYSTEMTABLE_FIELD_LIST[i * 2 + 1];
                // 组装字段和类型的SQL语句
                for (int tmpSize = tmpField.length - 1, j = tmpSize; j >= 0; j--) {
                    tmpSql.append(tmpField[tmpSize - j]).append(tmpTypeS[tmpSize - j]);
                }

                tmpSql.append(");");
                tmpStatement.executeUpdate(tmpSql.toString());
            }
            // 初始化视图信息
            initViewInfo(tmpStatement);
            // 初始化类型信息
            initCategoryInfo(tmpStatement);
            tmpStatement.close();
            return true;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            return false;
        }
    }

    /**
     * @检查是否有可以使用的数据可使用
     */
    private boolean hasValidOldDatabase() {
        return isExsitCurDatabase() && (getConnection() != null || getConnection() != null); // 当前的数据库是否存在，检查数据库的连接
    }

    // 创建临时表 end，表改名
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @取到旧数据库的所有的字段信息
     */
    private PIMDBTable getTableFields(
            String prmTableName,
            Connection prmConn) {
        PIMDBTable tmpDBTable = null;
        try {
            // 得到表的字段信息
            String tmpSqlStr = "SELECT * FROM ".concat(prmTableName).concat(" where 0 = 1");
            Statement tmpSmt = prmConn.createStatement();
            ResultSet tmpRs = tmpSmt.executeQuery(tmpSqlStr);
            ResultSetMetaData tmpRsmd = tmpRs.getMetaData();
            // 实例化字段hash
            Hashtable tmpFieldHash = new Hashtable();

            for (int tmpSize = tmpRsmd.getColumnCount(), i = 0; i < tmpSize; i++) {
                String tmpColN = tmpRsmd.getColumnName(i + 1).toUpperCase();
                int tmpType = tmpRsmd.getColumnType(i + 1);
                tmpFieldHash.put(PIMPool.pool.getKey(i), new PIMField(i, tmpColN, tmpType));
            }

            tmpDBTable = new PIMDBTable(prmTableName, tmpFieldHash);// 注意大小写
            tmpRs.close();
            tmpSmt.close();
            return tmpDBTable;
        } catch (Exception e) {
            tmpDBTable = null;
            PIMDBConnecter.instance.reConnectDb();
        }
        return null;
    }

    /**
     * 得到新数据库的连接
     */
    Connection getConnection() {
        if (dbConn == null) {
            try {
                dbConn = PIMDBConnecter.instance.getConnection(); // 取到连接
            } catch (SQLException e) {// dbConn = null; 添加异常信息，无法连接数据库，数据库有问题
            }
        }
        return dbConn;
    }

    /**
     * @检查当前的数据库更新是否成功
     */
    boolean isUpdateSucceed() {
        return isUpdateSucceed;
    }

    // /**
    // * @Note 不知道为什么数据库文件在新建的时，如果存在旧的文件，新数据库创建不起来？？
    // */
    // private boolean removeCurDatabase()
    // {
    // String tmpDBDir = PIMUtility.getPIMDatabaseDirPath(); //得到当前旧的数据库路径
    // File tmpDBFile = new File(tmpDBDir);
    // //判断数据库文件是否存在，不存在则说明女冠数据库已经移除
    // if (tmpDBFile == null || !tmpDBFile.isDirectory())
    // {
    // return true;
    // }
    // //检测数据库是否已经连接上
    // if (context != null)
    // {
    // closeCurConnection();
    // }
    // //得到数据库中所有数据库文件
    // String [] tmpDB = tmpDBFile.list();
    // for (int i = tmpDB.length - 1; i >= 0; i --)
    // {
    // File tmpDataFile = new File(tmpDBDir ,tmpDB[i]);
    // //@note 不要太乐观，有时时删除不了的??
    // tmpDataFile.delete();//删除数据库文件
    // }
    // return true;
    // }
    /**
     * @是否已经存在了但前版本的数据库
     */
    private boolean isExsitCurDatabase() {
        String tmpDBDir = Utility.getPIMDatabaseDirPath(); // 得到当前新的数据库路径
        String tmpDBN = PIMDBModel.dbName; // 数据库名
        if (tmpDBN == null || tmpDBN.length() < 1) // 数据库名字错误
            return false;

        String tmpName = tmpDBN.startsWith("/") ? tmpDBN.substring(1).trim().concat(".data") : tmpDBN.trim();// 数据库文件名

        return new File(tmpDBDir, tmpName).isFile(); // 检测数据库文件是否存在
    }

    /**
     * @检查数据库
     */
    private boolean checkCurDatabase() {
        Connection tmpConn = getConnection(); // 取到当前的连接
        return (tmpConn != null
                && getAllTableNames().size() == CustOpts.custOps.APPNameVec.size()
                        + ModelConstants2.SYSTEMTABLE_NAME_LIST.length && checkDatabase(tmpConn));
    }

    /* 检查数据库是否有修改 */
    private boolean checkDatabase(
            Connection prmConn) {
        String[] tmpTabNames = (String[]) getNewTableNames().toArray(new String[0]);
        String[] tableNameStr = CustOpts.custOps.getTableNames();
        HashMap tmpNameMap = new HashMap(tableNameStr.length); /* 建立一个Map */
        // 添加表名和索引值
        int tmpSize = tableNameStr.length;
        for (int i = 0; i < tmpSize; i++) {
            tmpNameMap.put(tableNameStr[i], PIMPool.pool.getKey(i)); /* 表名， 索引值 */
        }
        // 循环每一张表判断是否有表的名字被修改
        for (int i = tmpTabNames.length - 1; i >= 0; i--) {
            // 检查数据库表的名字有无改变
            if (isTableNameChanged(tmpTabNames[i])) {
                return false;
            }
            // 得到对应表对应的索引值
            Object tmpIdxObj = tmpNameMap.get(tmpTabNames[i].toUpperCase());
            if (tmpIdxObj == null) // 判断表名是否不存在
            {
                return false;
            }
            // 索引值
            int tmpIndex = ((Integer) tmpIdxObj).intValue();
            PIMDBTable tmpOld = getNewTableFields(tmpTabNames[i]);
            // 字段的长度不同已经判断
            if (tmpIndex < 0
                    || ((String[]) ModelConstants2.SYSTEMTABLE_FIELD_LIST[2 * tmpIndex]).length != tmpOld
                            .getFieldHashtable().size()) {
                return false;
            }
            // 数据库表有无修改
            if (isPIMDBTableChanged(tmpOld, (String[]) ModelConstants2.SYSTEMTABLE_FIELD_LIST[2 * tmpIndex], tmpIndex)) {
                return false;
            }
        }
        return true;
    }

    /* 得到新数据库表 */
    private boolean isPIMDBTableChanged(
            PIMDBTable prmTable,
            String[] prmFieldName,
            int prmIndex) {
        Hashtable tmpFieldHash = prmTable.getFieldHashtable();
        Enumeration tmpKeys = tmpFieldHash.keys();
        // 检查每一个字段类型是否已经修改
        while (tmpKeys != null && tmpKeys.hasMoreElements()) {
            Integer tmpIndex = (Integer) tmpKeys.nextElement();
            // 得到字段
            PIMField tmpField = (PIMField) tmpFieldHash.get(tmpIndex);
            // 得到字段类型的字符串值
            String tmpTypeStr = getTypeString(tmpField.getFieldType());
            if (tmpTypeStr == null) // 类型不匹配
            {
                return true;
            }
            int tmpIndexInt = tmpIndex.intValue();
            String tmpCurTypeStr = ((String[]) ModelConstants2.SYSTEMTABLE_FIELD_LIST[2 * prmIndex + 1])[tmpIndexInt];
            // 如果类型为空或者字段的类型不匹配
            if (tmpCurTypeStr == null || !tmpField.getFieldName().equals(prmFieldName[tmpIndexInt])
                    || !tmpCurTypeStr.trim().startsWith(tmpTypeStr)) {
                return true;
            }
            // 字段类型中特殊处理时间类型TIME和TIMESTAMP
            if (tmpTypeStr.equals(ModelConstants2.TIME) && tmpCurTypeStr.equals(ModelConstants2.TIMESTAMP)) {
                return true;
            }
        }
        return false;
    }

    /**/
    private String getTypeString(
            int prmType) {
        int tmpIndex = Arrays.binarySearch(TYPE_INT_VALUE, prmType);
        return (tmpIndex >= 0) ? ModelConstants2.TYPE_STRING_VALUE[tmpIndex] : null;
    }

    // /**
    // * @设置当前数据库的连接
    // */
    // private void setCurDatabaseConnection(String prmDatabasePath)
    // {
    // dbConnectionPath = prmDatabasePath;
    // }

    /**
     * @判断当前的版本是否需要更新
     */
    boolean isNeededUpdate() {
        boolean isSame = !isCurVersionUpdated() // 当前的版本是否已经升级
                && getConnection() != null // 是否可以连接数据库
                && !checkCurDatabase() // 检查不同版本的数据库是否有修改
                && hasValidOldDatabase(); // 判断是否已经更新

        if (isSame) /* 需要更新数据库 */
        {
            /* 程序发现用户目录下数据文件的版本低于当前程序的版本,系统将对其进行自动升级.请选择确定进行升级,或者选择取消,退出系统进行备份. */
            // ErrorDialog.showErrorDialog(MessageCons.Q20220);
        }
        return isSame;
    }

    /**
     * @判断当前的版本号
     * @note 默认为旧数据库的版本号
     */
    boolean isCurVersionUpdated() {
        int version = CUR_VERSION_UPDATE_FLAG;
        try {
            Statement state = getConnection().createStatement();
            ResultSet rs = state.executeQuery("SELECT VERSION FROM SYSTEMINFO");
            while (rs.next()) {
                version = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (CUR_VERSION_UPDATE_FLAG == version);
    }

    /**
     * 设置当前的版本号
     */
    void setCurVersionUpdated(
            boolean prmIsUpdateSucceed) {
        if (!prmIsUpdateSucceed) {
            return;
        }
        try {
            Connection conn = getConnection();
            Statement state = conn.createStatement();
            state.executeUpdate("UPDATE SYSTEMINFO SET VERSION=" + CUR_VERSION_UPDATE_FLAG);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // CUR_VERSION_UPDATE_FLAG = (prmIsUpdateSucceed == true)
        // ? CUR_VERSION_UPDATE_FLAG ++ : CUR_VERSION_UPDATE_FLAG;
    }

    /**
     * 取到所有原来表的表名, 注意这个context对象用的是旧的数据库的路径
     */
    private ArrayList getTableNames(
            Connection prmConn,
            int prmTableApp) {
        ArrayList tmpTableNameList = new ArrayList(); // hash
        try {
            DatabaseMetaData tmpMeta = prmConn.getMetaData();
            ResultSet tmpRsTables = tmpMeta.getTables(null, null, null, null);
            if (tmpRsTables == null) {
                PIMDBConnecter.instance.reConnectDb();
                return null;
            }
            while (tmpRsTables.next()) {
                String prmTableName = tmpRsTables.getString("TABLE_NAME");
                String tmpParName = getParallelismTableName(prmTableName, prmTableApp);
                if (tmpParName != null) {
                    tmpTableNameList.add(tmpParName);
                }
            }
        } catch (Exception e) {
            tmpTableNameList = null;
            PIMDBConnecter.instance.reConnectDb();
        }
        return tmpTableNameList;
    }

    /**
     * @根据不同的表的类型得到不同的表的名字
     */
    private String getParallelismTableName(
            String prmTableName,
            int prmTableApp) {
        String tmpTableName = prmTableName;
        // 旧表
        if (prmTableApp == OLD_DB_TABLE_APP) {
            tmpTableName =
                    (!tmpTableName.startsWith(DB_TEMP_TABLE_PREFIX) && !tmpTableName
                            .startsWith(DB_EXCEPTION_FLAG_PREFIX)) ? tmpTableName : null; // 得到
        }
        // 改名表
        else if (prmTableApp == TMP_DB_TABLE_APP) {
            // 是以"PREFIX_TEMP_"为前缀,但是不是"PREFIX_TEMP_DB_EXCEPTION_FLAG_TABLE"
            tmpTableName = (tmpTableName.startsWith(DB_TEMP_TABLE_PREFIX)) ? tmpTableName : null; // 得到
        }
        // 新表
        else if (prmTableApp == NEW_DB_TABLE_APP) {
            tmpTableName =
                    (!tmpTableName.startsWith(DB_TEMP_TABLE_PREFIX) && !tmpTableName
                            .startsWith(DB_EXCEPTION_FLAG_PREFIX)) ? tmpTableName : null; // 得到
        }
        // 标志表
        else if (prmTableApp == FLG_DB_TABLE_APP) {
            tmpTableName = (tmpTableName.startsWith(DB_EXCEPTION_FLAG_PREFIX)) ? tmpTableName : null;
        }
        // 所有表
        else if (prmTableApp == ALL_DB_TABLE_APP) {
            tmpTableName = (tmpTableName == null) ? null : tmpTableName; // 没有意思？还要修改的
        }
        return tmpTableName;
    }

    /**
     * @得到新的数据库中的所有表的名字
     */
    private String[] getCurTableNames() {
        String[] tableNameStr = CustOpts.custOps.getTableNames();
        return tableNameStr;
    }

    // //@TODO: 要导入讨厌的VIEWINFO*************************************************************//
    // /** 建立新的VIEWINFO的记录
    // */
    // private void createNewViewInfo(String prmTepName)
    // {
    // //得到新的表中的初始化的记录
    // ArrayList tmpOList = getTempRecords(prmTepName);
    // for (int i = tmpOList.size() - 1; i >= 0; i --)
    // {
    // //得到映射的旧的数据库记录
    // PIMRecord tmpNRec = getMapRecord((PIMRecord)tmpOList.get(i));
    // //过滤特殊的字段
    // tmpNRec = filterSpecField(tmpNRec);
    // updateViewRec(tmpNRec); //更新视图记录
    // }
    // }

    // /** 根据记录得到新的表中的记录
    // */
    // private PIMRecord getMapRecord(PIMRecord prmRec)
    // {
    // return prmRec;
    // }

    // /** 过滤字段
    // */
    // private PIMRecord filterSpecField(PIMRecord prmRec)
    // {
    // int tmpFoldIdx;
    // tmpFoldIdx = getContainsFieldNameIndex("FIELDNAMES");
    // String tmpFoldr = (String) prmRec.getFieldValue(PIMPool.pool.getIntegerKey(tmpFoldIdx));
    // tmpFoldr = tmpFoldr.concat(",14");
    // prmRec.setFieldValue(PIMPool.pool.getIntegerKey(tmpFoldIdx), tmpFoldr);
    //
    // tmpFoldIdx = getContainsFieldNameIndex("FIELDWIDTHS");
    // String tmpWidth = (String) prmRec.getFieldValue(PIMPool.pool.getIntegerKey(tmpFoldIdx));
    // tmpWidth = tmpWidth.concat(",200");
    // prmRec.setFieldValue(PIMPool.pool.getIntegerKey(tmpFoldIdx), tmpFoldr);
    //
    // return prmRec;
    // }

    // /** 更新数据库记录
    // */
    // private void updateViewRec(PIMRecord prmRec)
    // {
    // //得到字段的索引值
    // int tmpFoldIdx;
    // //根据字段的索引值得到字段
    // tmpFoldIdx = getContainsFieldNameIndex("FOLDERID");
    // String tmpFold = (String) prmRec.getFieldValue(PIMPool.pool.getIntegerKey(tmpFoldIdx));
    // tmpFoldIdx = getContainsFieldNameIndex("APPTYPE");
    // Integer tmpApp = (Integer) prmRec.getFieldValue(PIMPool.pool.getIntegerKey(tmpFoldIdx));
    // tmpFoldIdx = getContainsFieldNameIndex("APPSUBTYPE");
    // Integer tmpSub = (Integer) prmRec.getFieldValue(PIMPool.pool.getIntegerKey(tmpFoldIdx));
    // //A.s("FOLD:" + tmpFold + " -- APP:" + tmpApp + " -- SUB:" + tmpSub);
    // StringBuffer tmpCondi = new StringBuffer();
    // //得到要查找的记录的条件
    // tmpCondi.append(" FOLDERID = '").append(tmpFold).append('\'');
    // tmpCondi.append(" AND ");
    // tmpCondi.append("APPTYPE = ").append(tmpApp);
    // tmpCondi.append(" AND ");
    // tmpCondi.append("APPSUBTYPE = ").append(tmpSub);
    //
    // boolean hasRecord =
    // hasRecord(ModelConstants2.VIEWINFO_TABLE_NAME, getConnection(), tmpCondi.toString());
    // //如果存在当前的记录则更新记录
    // if (hasRecord)
    // {
    // updateRecord(prmRec, getConnection()); //更新数据库记录
    // }
    // else
    // {
    // insertRecord(getConnection(), prmRec, ModelConstants2.VIEWINFO_TABLE_NAME);
    // }
    // }
    // //得到新的表中的所有记录
    // ArrayList tmpOList = getRecords(getConnection(), prmTepName)
    //
    // for (int i = tmpNList.size() - 1; i >= 0; i --)
    // {
    // PIMRecord tmpNewRec = (PIMRecord) tmpNList.get(i);
    // //生成新的记录
    // createNewRecord(
    // (PIMRecord)tmpOList.get(i), tmpNewRec);
    // //更新数据VIEWINFO中记录
    // updateRecord(tmpNewRec);
    // }
    // }

    // /** 建立新的数据库记录
    // */
    // private createNewRecord(PIMRecord prmORec, PIMRecord prmNRec)
    // {
    // //生成新的数据库记录
    // setNewPIMRecord(prmORec, prmNRec);
    // //处理特殊的字段
    // int pos = prmNRec.getFieldValues().; //得到新的表的字段名字列表
    //
    // }

    // /**
    // * 删除新版本数据库表中的初始化值，注意要改的值，考虑如果为视图VIEWINFO表，则有风险
    // * @Note 这里的prmNewTableName不是转换的名字
    // */
    // private void removeNewInitRecord(String prmNewTableName)
    // {
    // //注意在删除新数据库中表的数据的数据的时候，当可以替代的表名为null时应为不给导入所以不用删除
    // if (prmNewTableName != null && prmNewTableName.length() > 0
    // && isRemoveInitRecord(prmNewTableName)) //不要删除初始化值
    // {
    // deleteRecord(getConnection(), prmNewTableName);
    // }
    // }

    // /**
    // * @是否要要移除当前表中的初始化值
    // */
    // private boolean isRemoveInitRecord(String prmNewTableName)
    // {
    // return !isInitedTableName(prmNewTableName);
    // }

    // /**
    // * 判断新版本数据库中的初始化值是否与老版本数据库中的初始化值相同
    // */
    // private boolean isInitChanged(String prmOTabName, String prmNTabName) throws SQLException
    // {
    // int tmpOldIds = -1, tmpNewIds = -2;
    // //得到记录数目
    // String tmpSel = "SELECT MAX(ID) FROM ";
    // //得到数据库状态
    // Statement tmpSmt = getConnection().createStatement();
    // ResultSet tmpRs;
    // //得到旧数据库表的结果集
    // tmpRs = tmpSmt.executeQuery(tmpSel.concat(prmOTabName));
    // if (tmpRs.next())
    // {
    // tmpOldIds = tmpRs.getInt(1);
    // }
    // //得到新数据库表的结果集
    // tmpRs = tmpSmt.executeQuery(tmpSel.concat(prmNTabName));
    // if (tmpRs.next())
    // {
    // tmpNewIds = tmpRs.getInt(1);
    // }
    // ///***/A.s("旧表的记录数据数: " + tmpOldIds + " 新表的记录数据数: " + tmpNewIds);
    // return tmpOldIds == tmpNewIds;
    // }

    // /**
    // * 取出新版本数据库中的初始化值，要用来生成新的记录的
    // */
    // private ArrayList getNewInitRecords(String prmTabName)
    // {
    // return getRecords(getConnection(), prmTabName);
    // }

    // /**
    // * 判断视图表是否已经改表，添加了字段 or 删除了字段
    // * @note 这个表改表了是危险的？？。。。。。
    // */
    // private boolean isInitInfoChanged(String prmTabName)
    // {
    // //@note 不希望旧的数据库表中的VIEWINFO表的表名改表
    // if (!oldTableNameList.contains(prmTabName))
    // {
    // return true;
    // }
    // Hashtable tmpOldHash = getOldTableFields(prmTabName).getFieldHashtable();
    // Hashtable tmpNewHash = getNewTableFields(prmTabName).getFieldHashtable();
    // /*判断字段有无改表*/
    // return isTableFieldsChanged(tmpOldHash, tmpOldHash);
    // }

    // //@NOTE: 结束导入VIEWINFO
    // //******************************************************************************************//
    // /**
    // * 得到当前操作的新版本数据库表的名字
    // */
    // private String getCurNewTableName()
    // {
    // return (newDBTable != null) ? newDBTable.getTableName() : null;
    // }

    // /**
    // * 得到当前操作的旧版本数据库表的名字
    // */
    // private String getCurTempTableName()
    // {
    // return (tepDBTable != null) ? tepDBTable.getTableName() : null;
    // }
    /**
     * 判断当前表是否为在数据库中定义要初始化的
     */
    private boolean isInitedTableName(
            String prmNewTableName) {
        for (int i = 0; i < ModelConstants2.INITED_TABLE_NAME_LIST.length; i++) {
            if (ModelConstants2.INITED_TABLE_NAME_LIST[i].equals(prmNewTableName)) {
                return true;
            }
        }
        return false;
    }

    // /**
    // * 判断old数据库中的表在new数据库中是否已经删除
    // */
    // private boolean isTableDeleted(String prmOldTableName)
    // {
    // return (isTableNameChanged(prmOldTableName)
    // && getInstreadTableName(prmOldTableName)== null);
    // }

    // /**
    // * 判断old数据库表中的字段在new数据库中是否已经删除
    // */
    // private boolean isFieldDeleted(String prmOldFieldName)
    // {
    // return (getContainsFieldNameIndex(prmOldFieldName) < 0
    // && getInsteadFieldName(prmOldFieldName) == null);
    //
    // }
    /**
     * 检查数据库表名是否已经改表，删除 or 改名 ？
     */
    private boolean isTableNameChanged(
            String prmOldTableName) {
        String[] tmpNewTableNames = getCurTableNames();
        for (int i = 0; i < tmpNewTableNames.length; i++) {
            if (tmpNewTableNames[i].equals(prmOldTableName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 当前的表中是否包含当前字段
     * 
     * @note prmNewFields 为（Integer， PIMField）
     */
    private int getContainsFieldIndex(
            PIMField prmOldField,
            ArrayList prmFieldsList) {
        Iterator tmpIte = prmFieldsList.listIterator();
        int isContains = -1;
        while (tmpIte != null && tmpIte.hasNext()) {
            PIMField tmpField = (PIMField) tmpIte.next();
            if (tmpField.getFieldName().equals(prmOldField.getFieldName())
                    && tmpField.getFieldType() == prmOldField.getFieldType()) // 判断当前的类型和名字是否相同
            {
                isContains = tmpField.getFieldID();
                break;
            }
        }
        return isContains; //
    }

    /**
     * @取到映射的表的名字
     */
    private String getMappedTableName(
            String prmOldTableName) {
        if (prmOldTableName == null || prmOldTableName.length() < 1) {
            return null;
        }
        return (!isTableNameChanged(prmOldTableName)) ? prmOldTableName : getInstreadTableName(prmOldTableName);
    }

    /**
     * @取到映射的字段的名字
     */
    private PIMField getMappedField(
            PIMField prmOldField,
            ArrayList prmFieldsList) {
        PIMField tmpNewField = null;
        int tmpRecordId = -1;
        if ((tmpRecordId = getContainsFieldIndex(prmOldField, prmFieldsList)) >= 0) {
            tmpNewField = new PIMField(tmpRecordId, prmOldField.getFieldName(), prmOldField.getFieldType()); // 设置ID
        } else {
            PIMField tmpInsteadField = getInstreadField(prmOldField); // 取到可以代替的字段对象
            if (tmpInsteadField != null && (tmpRecordId = getContainsFieldIndex(tmpInsteadField, prmFieldsList)) >= 0) // 如果为空说明没有可以替代的，删除了
            {
                tmpNewField = new PIMField(tmpRecordId, tmpInsteadField.getFieldName(), tmpInsteadField.getFieldType()); // 设置ID
            }
        }
        return tmpNewField;
    }

    // /** 对于一张表建立映射
    // */
    // private void createMap()
    // {
    // }
    /**
     * @得到一条旧版本的记录，生成新版本的记录
     */
    private void setNewPIMRecord(
            PIMRecord prmOldRecord,
            PIMRecord prmNewRecord) {
        // 记录为null
        if (prmOldRecord == null && prmNewRecord == null) {
            return;
        }
        Hashtable prmOldhash = tepDBTable.getFieldHashtable();
        Hashtable prmNewhash = newDBTable.getFieldHashtable();
        newRecordFieldNameList = new ArrayList(); // @Note:
        // 判断表的字段有无改变，无改变则直接把旧的记录导入到新的记录中
        if (!isTableFieldsChanged(prmOldhash, prmNewhash)) {
            Hashtable tmpOldRecordHash = prmOldRecord.getFieldValues();
            String tmpTempName = tepDBTable.getTableName(); // 得到的时临时表名
            String tmpNewTableName = getMappedTableName(getOldTableName(tmpTempName)); // 注意改动
            insertDefaultValue(tmpOldRecordHash, getDefaultInsertHash(tmpNewTableName)); // 设置要修改的值
            prmNewRecord.setFieldValues(tmpOldRecordHash); // 设置新版本记录
            prmNewRecord.setRecordID(prmOldRecord.getRecordID()); // 设置当前记录的ID号
        } else {
            createMappedRecord(prmOldRecord, prmNewRecord); // 设置字段映射，判断有些字段是否要导入
        }
    }

    // /**
    // * 得到旧版本数据库表所有字段列表
    // */
    // private ArrayList getTepFieldList()
    // {
    // return (tepDBTable == null)
    // ? null : new ArrayList(tepDBTable.getFieldHashtable().values());
    // }
    /**
     * 得到新版本数据库表所有字段列表
     */
    private ArrayList getNewFieldList() {
        return (newDBTable == null) ? null : new ArrayList(newDBTable.getFieldHashtable().values());
    }

    /**
     * 实现记录的映射填写各个字段
     */
    private void createMappedRecord(
            PIMRecord prmOldRecord,
            PIMRecord prmNewRecord) {
        // 新版本数据库表所有字段列表
        ArrayList tmpFieldList = getNewFieldList();
        int tmpOldSize = tepDBTable.getFieldHashtable().size(); // 得到旧版本的字段的尺寸
        Hashtable tmpRecordHash = new Hashtable();
        String tmpTempName = tepDBTable.getTableName();
        String tmpNewTableName = getMappedTableName(getOldTableName(tmpTempName)); // 注意改动
        for (int i = 0; i < tmpOldSize; i++) {
            // 旧版本字段
            PIMField tmpOldField = tepDBTable.getField(i);
            // 新版本中与之对应的字段
            PIMField tmpNewField = getMappedField(tmpOldField, tmpFieldList);
            if (tmpNewField == null || prmOldRecord == null) // 不存在
            {
                continue;
            }
            Integer tmpKey = PIMPool.pool.getKey(tmpNewField.getFieldID()); // 得到新数据库表记录中的ID值
            Object tmpObj = prmOldRecord.getFieldValue(i); // 得到旧数据库记录中的值
            // 判断当前的字段类型是否修改
            if (!isFieldTypeChanged(tmpOldField.getFieldType(), tmpNewField.getFieldType()) && tmpKey != null
                    && tmpObj != null) {
                newRecordFieldNameList.add(tmpNewField.getFieldName());
                tmpRecordHash.put(tmpKey, tmpObj); // 把旧版本的值赋值新版本PIMRecord中
            }
            // 类型修改，则要类型转换
            else if (tmpObj != null && isFieldTypeChanged(tmpOldField.getFieldType(), tmpNewField.getFieldType())) {
                tmpObj = processTypeChanged(tmpObj, tmpOldField, tmpNewField); // 处理类型转换
                if (tmpObj != null) {
                    newRecordFieldNameList.add(tmpNewField.getFieldName());
                    tmpRecordHash.put(tmpKey, tmpObj); // 把旧版本的值赋值新版本PIMRecord中
                }
            }
        }
        if (tmpRecordHash != null) {
            // 设置某字段要替换的值
            insertDefaultValue(tmpRecordHash, getDefaultInsertHash(tmpNewTableName));
            prmNewRecord.setFieldValues(tmpRecordHash);
            prmNewRecord.setRecordID(prmOldRecord.getRecordID()); // 设置记录号
        }
    }

    /**
     * @取要修改的字段的值
     */
    private void insertDefaultValue(
            Hashtable prmRecordHash,
            Hashtable prmModifyHash) {
        // @note 这里的prmRecordHash 希望不为空
        if (prmModifyHash == null || prmModifyHash.size() < 1) // 不用插入
        {
            return;
        }
        Set tmpModifyKey = prmModifyHash.keySet(); // 全都是字段名
        String[] tmpKeys = (String[]) tmpModifyKey.toArray(new String[0]);
        for (int i = 0; i < tmpKeys.length; i++) {
            int tmpFieldID = getContainsFieldNameIndex(tmpKeys[i]);
            Object tmpValue = null;
            if (tmpFieldID < 0 || (tmpValue = prmModifyHash.get(tmpKeys[i])) == null) // 如果值为空
            {
                continue;
            }
            prmRecordHash.put(PIMPool.pool.getKey(tmpFieldID), tmpValue);
        }
    }

    /**
     * @计算当前的字段的ID号，注意在newDBTable中
     */
    private int getContainsFieldNameIndex(
            String prmFieldName) {
        ArrayList tmpNewList = getNewFieldList();
        for (int i = 0; i < tmpNewList.size(); i++) {
            PIMField tmpField = (PIMField) tmpNewList.get(i);
            if (tmpField.getFieldName().equals(prmFieldName)) {
                return tmpField.getFieldID();
            }
        }
        return -1;
    }

    // /**
    // * @判断当前记录的字段中已经包含了当前的字段，但是值是需要修改？
    // */
    // private boolean isExsitedModifyField(String prmFieldName)
    // {
    // return (newRecordFieldNameList != null && newRecordFieldNameList.size() > 0
    // && prmFieldName != null && newRecordFieldNameList.contains(prmFieldName));
    // }
    /**
     * @判断当前的字段的类型有无改表
     */
    private boolean isFieldTypeChanged(
            int prmOldType,
            int prmNewType) {
        return (prmOldType != prmNewType);
    }

    // /**
    // * @判断当前的字段名字有无改表
    // */
    // private boolean isFieldNameChanged(PIMField prmOldField, PIMField prmNewField)
    // {
    // return (prmOldField == null || prmNewField == null
    // || !prmOldField.getFieldName().equals(prmNewField.getFieldName()));
    // }
    /**
     * 处理类型转化问题
     */
    private Object processTypeChanged(
            Object prmValueObj,
            PIMField prmOldField,
            PIMField prmNewField) {
        int tmpOldType = prmOldField.getFieldType();
        int tmpNewType = prmNewField.getFieldType();
        // 类型改表的情况
        return changeType(prmValueObj, tmpOldType, tmpNewType);
    }

    /**
     * 检测是否为改名了，取改名以后的字段名
     * 
     * @see DBChangedInfo
     */
    private PIMField getInstreadField(
            PIMField prmOldField) {
        String tmpOldFieldName = prmOldField.getFieldName();
        // 得到可以替代的字段名
        String tmpNewFieldName = getInsteadFieldName(tmpOldFieldName);
        if (tmpNewFieldName == null) {
            return null;
        }
        // 得到数据库表的字段
        ArrayList tmpFieldList = getNewFieldList();
        ArrayList tmpNamesList = getFieldNames(tmpFieldList); // 得到新表的所有字段的名字
        int tmpPos = tmpNamesList.indexOf(tmpNewFieldName); // @note 列表中没有null?
        return (tmpNewFieldName == null || tmpPos < 0) ? null : (PIMField) tmpFieldList.get(tmpPos);
    }

    /**
     * 但前字段对应的字段名
     * 
     * @临时的这个方法需要修改？？？
     */
    private String getInsteadFieldName(
            String prmOldFieldName) {
        return (prmOldFieldName.equals("SEX") || prmOldFieldName.equals("IS_DEFAULT_MAIL")
                || prmOldFieldName.equals("TRACKSTATUS") /* modify 12_24_14 */
                || prmOldFieldName.equals("IMAPPOLLALLFOLDERS") || prmOldFieldName.equals("IMPORTANCE")
                || prmOldFieldName.equals("NEED_AWORK") || prmOldFieldName.equals("ASSIGN")) ? prmOldFieldName : null;
    }

    /**
     * 检查是否改名了，改了以后的数据库表名
     * 
     * @see DBChangedInfo
     */
    private String getInstreadTableName(
            String prmOldTableName) {
        return null;
    }

    /**
     * 检测当前数据库表中的字段有无改表
     */
    private boolean isTableFieldsChanged(
            Hashtable prmOldHash,
            Hashtable prmNewHash) {
        if (prmOldHash == null && prmNewHash == null) // changed
        {
            return false;
        }
        Collection tmpOldCol = prmOldHash.values();
        Collection tmpNewCol = prmNewHash.values();
        if (tmpOldCol.size() != tmpNewCol.size()) {
            return true;
        }
        ArrayList tmpOldList = new ArrayList(tmpOldCol);
        ArrayList tmpNewList = new ArrayList(tmpNewCol);
        if (!tmpOldList.containsAll(tmpNewList)) {
            return true;
        }
        int isFieldChanged = -1;
        for (int i = 0; i < tmpOldList.size(); i++) {
            PIMField tmpOldField = (PIMField) tmpOldList.get(i);
            PIMField tmpNewField = (PIMField) tmpNewList.get(i);
            if (!tmpOldField.getFieldName().equals(tmpNewField.getFieldName())
                    || tmpOldField.getFieldType() != tmpNewField.getFieldType()) {
                isFieldChanged = 1;
                break;
            }
        }
        return (isFieldChanged == 1);
    }

    /**
     * 移动新的记录到新版本的数据库中
     */
    private void importTable(
            String prmTepTabName) {
        String tmpTabName = prmTepTabName;
        // 判断临时表的名字是否为空
        if (tmpTabName == null || tmpTabName.length() < 1) {
            return;
        }
        // 这里取旧的记录要用旧的表的名字
        ArrayList tmpRecList = getTempRecords(tmpTabName); // 得到旧的表的所有的记录
        // 判断有无当前的记录
        int tmpRecSize;
        if (tmpRecList == null || (tmpRecSize = tmpRecList.size()) < 1) {
            return;
        }
        // 生成新的数据库记录，但是当前的记录有可能在新的数据库中无法使用
        for (int i = 0; i < tmpRecSize; i++) {
            PIMRecord tmpNewRec = new PIMRecord();
            PIMRecord tmpOldRec = (PIMRecord) tmpRecList.get(i);
            setNewPIMRecord(tmpOldRec, tmpNewRec); // 设置当前的值
            // 在导入的时，用新的表的名字
            String oldTabName = getOldTableName(prmTepTabName);
            String tmpNewName = getMappedTableName(oldTabName);
            // 判断新的名字是否存在，如果不存在无需导入？
            if (tmpNewName == null || tmpNewName.length() < 1) {
                continue;
            }
            // 导入记录
            importRecord(tmpNewRec, tmpNewName); // 导入一条记录
        }
    }

    /**
     * @暂时方法，很快要修改
     */
    private void initConfigInfo() {
        // deleteConfigFile(); //删除旧版本的配置文件
        // CustomOptions.setSolitarily(true);
    }

    /**
     * 更新数据库
     */
    void updateDatabase() {
        if (!hasValidOldDatabase()) // 检测是否有可以使用的数据库
        {
            return; // 数据库不可用的话返回
        }
        /* 临时的，需要修改 */
        initConfigInfo();
        // /***/ A.s("datebase updating......");
        checkUpdatedException(DB_OPERATION_RENAME); // 检测数据库是否已经更新且更新失败
        /* 注意: 建立数据库临时表， 用来标志数据库动作流程 */
        boolean tmpCreateSucceed = createFlgTable(DB_EXCEPTION_FLAG_RENAME); // 建立标志表默认为RENAME
        if (!tmpCreateSucceed) {
            clearOtherTables();/* 清除数据库中没有用的表 */
            tmpCreateSucceed = createFlgTable(DB_EXCEPTION_FLAG_RENAME); // 建立标志表默认为RENAME
        }
        if (renameTableUpdate()) /* TODO：更新数据库,可以更新两次 */
        {
            isUpdateSucceed = true;
            setCurVersionUpdated(isUpdateSucceed); // 设置当前的版本是否更新成功
        }
    }

    // /**
    // * @创建临时表更新数据库
    // */
    // private void createTempUpdate()
    // {
    // createTempTables(); //建立临时表
    // boolean tmpIsSucceed = getCreateTempTableSucceedFlag(); //创建临时表是否成功
    // if (!tmpIsSucceed && dropTempTables()) //删除临时表，再一次创建
    // {
    // //不成功怎么办?? @是否可以删除临时表继续创建一次
    // createTempTables();
    // }
    // tmpIsSucceed = getCreateTempTableSucceedFlag();
    // if (tmpIsSucceed && dropOldTables()) //删除旧数据库表
    // {
    // //@note 旧的表一定要删掉，否则出错
    // createNewTables(getConnection()); //建立新版本的新数据库表
    // importTables(); //把临时表中的数据导入到新建的表中
    // }
    // }
    /**
     * @数据库改名更新数据库
     */
    private boolean renameTableUpdate() {
        // 把旧版本数据库中的表的名字改为前缀为"PREFIX_TEMP_"的数据库表
        renameOldTables();
        boolean tmpIsSucceed = getCreateTempTableSucceedFlag();
        if (!tmpIsSucceed || !renameFlgTable(DB_EXCEPTION_FLAG_RENAME, DB_EXCEPTION_FLAG_CREATE) // 设置建表标志
                || !createNewTables(getConnection()) // 建立新版本的新数据库表
                || !renameFlgTable(DB_EXCEPTION_FLAG_CREATE, DB_EXCEPTION_FLAG_IMPORT)) // 设置导入标志
        {
            return false;
        }
        importTables(); // 把临时表中的数据导入到新建的表中
        if (!renameFlgTable(DB_EXCEPTION_FLAG_IMPORT, DB_EXCEPTION_FLAG_DELETE) // 设置删除标志
                || !dropTempTables()) // 删除临时表
        {
            return false;
        }
        /* 清除数据库中没有用的数据库表 */
        clearOtherTables();
        return true;
    }

    // /**
    // * @把所有的旧表的名字转化为临时表的名字
    // */
    // private ArrayList getTempTableNameList()
    // {
    // ArrayList tmpList = new ArrayList();
    // //把所有的旧表的名字转化为新表的名字
    // int tmpSize = oldTableNameList.size() - 1;
    // for (int i = tmpSize; i >= 0; i --)
    // {
    // String tmpTab = (String) oldTableNameList.get(tmpSize - i);
    // tmpList.add(getTempTableName(tmpTab));
    // }
    // return tmpList;
    // }
    /**
     * @导入旧数据库表
     */
    private void importTables() {
        // 得到旧版本数据库中的表的名字
        ArrayList tmpTableList = oldTableNameList;
        // 判断当前的数据库表的名字是否为空
        if (tmpTableList == null || tmpTableList.isEmpty()) {
            return;
        }
        // 导入操作
        for (int i = tmpTableList.size() - 1; i >= 0; i--) {
            String tmpOldName = (String) tmpTableList.get(i);
            String tmpNewName = getMappedTableName(tmpOldName); // 可以替代的，新版本的数据库表名
            // @note 如果是要初始化的表则不用导入
            if (tmpNewName == null || tmpNewName.length() < 1 || isInitedTableName(tmpNewName)) {
                continue;
            }
            String tmpTepName = getTempTableName(tmpOldName);
            tepDBTable = getTempTableFields(tmpTepName); // 初始化oldDBTable
            newDBTable = getNewTableFields(tmpNewName); // 初始化newDBTable
            /* 删除新的数据库中的记录，在考虑中是否要删除，自然有初始化的表的是不删除的 */
            if (tmpNewName.equals(ModelConstants2.VIEWINFO_TABLE_NAME)) {
                // /***/A.s("更新前-------------------------------------------");
                // /***/try{isInitChanged(tmpTepName, tmpNewName);}catch (SQLException e){A.s("error...");};
                // /***/A.s("更新后-------------------------------------------");
                // /***/createNewViewInfo(tmpTepName);
                // /***/try{isInitChanged(tmpTepName, tmpNewName);}catch (SQLException e){A.s("error...");};
                continue;
            }
            importTable(tmpTepName); // 导入one表
        }
    }

    /**
     * 导入一条记录，注意的是是否要做清空的工作呀？？？
     * 
     * @note 注意是否要做清空的工作
     */
    private boolean importRecord(
            PIMRecord prmNewRecord,
            String prmNewTableName) {
        Connection tmpConn = getConnection();
        return insertRecord(tmpConn, prmNewRecord, prmNewTableName);
    }

    // /**
    // * 关键问题要注意，这样的时候是否可以导入
    // * @Note 不要用
    // */
    // private PIMRecord getNewDBRecord()
    // {
    // PIMRecord tmpNewRecord = new PIMRecord();
    // Hashtable tmpFieldHash = newDBTable.getFieldHashtable(); //得到PIMField列表
    // if (tmpFieldHash == null || tmpFieldHash.size() < 1)
    // {
    // return null;
    // }
    // Hashtable tmpNewRecordModel = new Hashtable();
    // for (int i = 0; i < tmpFieldHash.size(); i++)
    // {
    // tmpNewRecordModel.put(PIMPool.pool.getIntegerKey(i), null);
    // }
    // tmpNewRecord.setFieldValues(tmpNewRecordModel);
    // return tmpNewRecord;
    // }

    // /**
    // * 取新版本数据库的所有记录
    // */
    // private ArrayList getNewRecords(String prmNewTableName)
    // {
    // Connection tmpConn = getConnection();
    // return getRecords(tmpConn, prmNewTableName);
    // }
    /**
     * @取旧版本数据库的记录
     */
    private ArrayList getTempRecords(
            String prmTempTableName) {
        Connection tmpConn = getConnection();
        return getRecords(tmpConn, prmTempTableName);
    }

    /**
     * 取旧版本数据库表中的所有记录
     */
    private ArrayList getRecords(
            Connection prmConn,
            String prmTabName) {
        ArrayList tmpRecordVec = null;
        try {
            // 判断当前的连接是否成功
            if (prmConn == null || prmTabName == null) {
                return null;
            }
            // 记录列表
            tmpRecordVec = new ArrayList();
            // 查询语句
            String tmpSql = "SELECT * FROM ".concat(prmTabName);
            // 得到查找的结果集
            ResultSet tmpRs = prmConn.createStatement().executeQuery(tmpSql);
            ResultSetMetaData tmpRmd = tmpRs.getMetaData();
            // 数据库表的列数
            int tmpColCount = tmpRmd.getColumnCount();
            while (tmpRs.next()) {
                PIMRecord tmpRec = new PIMRecord();
                // 得到当前记录的ID号
                int tmpRecId = tmpRs.getInt(1);
                // 把当前的记录的ID号设置到PIMRecord中
                tmpRec.setRecordID(tmpRecId);
                // 记录的Hash值
                Hashtable tmpOneHash = getRecordHash(tmpRs, tmpRmd, tmpRecId, 1, tmpColCount); // 这个方法是private
                tmpRec.setFieldValues(tmpOneHash);
                // 得到当前的表的名字
                String tmpTN = (prmTabName.startsWith(DB_TEMP_TABLE_PREFIX)) ? getOldTableName(prmTabName) : prmTabName;
                // 设置当前记录的类型
                tmpRec.setAppIndex(PIMDBUtil.getAppTableIndex(tmpTN));
                tmpRecordVec.add(tmpRec);
            }
            tmpRs.close(); // 关闭结果集
        } catch (SQLException sqle) {
            tmpRecordVec = null;
            PIMDBConnecter.instance.reConnectDb();
        }
        return tmpRecordVec;
    }

    /**
     * 处理移动的数据库的语句
     */
    private void processImportPreparedStatement(
            PreparedStatement prepareStmt,
            PIMDBTable prmDBTable,
            PIMRecord prmRecord,
            Object[] prmKeys) throws SQLException {
        int tmpKeyCount = prmKeys.length;
        Hashtable tmpValueHash = prmRecord.getFieldValues();
        for (int j = 0; j < tmpKeyCount; j++) {
            int fieldType = ((PIMField) prmDBTable.getField(prmKeys[j])).getFieldType();
            Object value = tmpValueHash.get(prmKeys[j]);
            int tmpColumn = j + 1;
            if (fieldType == Types.BIT) {
                prepareStmt.setBoolean(tmpColumn, ((Boolean) value).booleanValue());
            } else if (fieldType == Types.TINYINT) {
                prepareStmt.setByte(tmpColumn, ((Byte) value).byteValue());
            } else if (fieldType == Types.SMALLINT) {
                prepareStmt.setShort(tmpColumn, ((Short) value).shortValue());
            } else if (fieldType == Types.INTEGER) {
                prepareStmt.setInt(tmpColumn, ((Integer) value).intValue());
            } else if (fieldType == Types.FLOAT) {
                prepareStmt.setFloat(tmpColumn, ((Float) value).floatValue());
            } else if (fieldType == Types.DOUBLE) {
                prepareStmt.setDouble(tmpColumn, ((Double) value).doubleValue());
            } else if (fieldType == Types.VARCHAR) {
                prepareStmt.setString(tmpColumn, (String) value);
            } else if (fieldType == Types.TIMESTAMP) {
                prepareStmt.setTimestamp(tmpColumn, new Timestamp(((Date) value).getTime()));
            } else if (fieldType == Types.BINARY) {
                prepareStmt.setObject(tmpColumn, value); // 对象
            }
        }
    }

    /**
     * 插入一条记录
     */
    private boolean insertRecord(
            Connection prmConn,
            PIMRecord prmNewRecord,
            String prmTabName) {
        try {
            // 判断当前的记录是否为空或者是否已经连接上数据库
            if (prmNewRecord == null || prmConn == null) {
                return false;
            }
            // 插入当前的记录
            return insertCurRecord(prmConn, prmNewRecord, prmTabName);
        } catch (SQLException sqle) {
            PIMDBConnecter.instance.reConnectDb();
        }
        return false;
    }

    /**
     * @插入记录
     */
    private boolean insertCurRecord(
            Connection prmConn,
            PIMRecord prmRecord,
            String prmTabName) throws SQLException {
        try {
            // 判断是否已经连接上数据库
            if (prmConn == null) {
                return false;
            }
            // 得到要插入的下一个ID号
            String tmpId = String.valueOf(prmRecord.getRecordID());
            //
            // @NOTE: 导入新的记录时，一般情况下不会出现ID号重的情况
            // 除了原来的表中已经有记录的情况，注意在邮件和联系人中希望和原来的ID号一样
            // 因为邮件AppType号和ID号组成附件名字，联系人的通讯组是根据ID号来组成的
            //
            if (hasRecord(prmTabName, prmConn, " ID = ".concat(tmpId))) {

                // @TODO: 注意对于已经存在的ID号
                if (prmTabName.equals(ModelConstants2.VIEWINFO_TABLE_NAME)) {
                    prmRecord.setRecordID(PIMDBConnecter.instance.getNextID(ModelConstants2.VIEWINFO_TABLE_NAME)); // 插入新的记录
                } else {
                    System.err.println(prmTabName + "-[表]-" + "错误: ID号重复......");
                    // @TODO:做处理
                    return false;
                }
            }
            Hashtable tmpHash = prmRecord.getFieldValues(); // 得到记录的hash列表
            // 得到字段号对象数组
            Object[] tmpKeys = tmpHash.keySet().toArray();
            String tmpSql = getInsertPreparedSql(prmTabName, prmRecord, tmpKeys, newDBTable);
            PreparedStatement tmpPreSt = prmConn.prepareStatement(tmpSql);
            processImportPreparedStatement(tmpPreSt, newDBTable, prmRecord, tmpKeys);
            int tmpRs = prmConn.createStatement().executeUpdate(tmpSql);
            tmpPreSt.close();
            return (tmpRs != 0);
        } catch (SQLException se) {
            se.printStackTrace();
            PIMDBConnecter.instance.reConnectDb();
        }
        return false;
    }

    /**
     * 更新数据库记录
     */
    boolean updateRecord(
            PIMRecord prmRecord,
            Connection prmConn) {
        // 如果当前的记录为空，则返回
        if (prmRecord == null) {
            return false;
        }
        // 得到记录的hashtable
        Hashtable tmpHash = prmRecord.getFieldValues();
        if (tmpHash == null) {
            return false;
        }
        // 得到记录所在表名,判断当前的表的名字是否存在
        String tmpTabN = null;
        int tmpRecType = prmRecord.getAppIndex();
        if (tmpRecType > 0) {
            tmpTabN = (String) CustOpts.custOps.APPNameVec.get(tmpRecType);
        } else {
            tmpTabN = PIMDBUtil.getSystemTableName(prmRecord.getAppIndex());
        }
        if (tmpTabN == null || tmpTabN.length() < 1) {
            return false;
        }
        // /***/A.s("更新记录[表]--" + tmpTabN + "--记录:" + prmRecord.getRecordID());
        try {
            // 判断有无连接上数据库
            if (prmConn == null) {
                return false;
            }

            Object[] tmpKeys = tmpHash.keySet().toArray();
            // 得到更新数据库表的准备SQL语句
            String tmpPre = getUpdatePreparedSql(tmpTabN, prmRecord, tmpKeys, newDBTable);
            PreparedStatement tmpPreSmt = prmConn.prepareStatement(tmpPre);
            // 处理准备的状态
            processImportPreparedStatement(tmpPreSmt, newDBTable, prmRecord, tmpKeys);
            prmConn.createStatement().executeUpdate(tmpPre);
            ;
            tmpPreSmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
            PIMDBConnecter.instance.reConnectDb();
        }
        return false;
    }

    //
    private String getUpdatePreparedSql(
            String prmTabName,
            PIMRecord prmRecord,
            Object[] prmKeys,
            PIMDBTable prmDBTable) {
        // 得到记录的Hash
        Hashtable tmpHash = prmRecord.getFieldValues();
        int tmpSize = prmKeys.length;
        // 得到记录的ID号
        int tmpId = prmRecord.getRecordID();

        StringBuffer tmpSB = new StringBuffer();
        // 当记录中字段的个数为0
        if (tmpHash.size() < 1) {
            tmpSB.append("UPDATE ").append(prmTabName);
            tmpSB.append(" SET ID = ").append(tmpId).append(';'); // 在没有字段的是否添加一个ID字段
        } else {
            tmpSB.append("UPDATE ").append(prmTabName).append(" SET ");
            for (int i = 0; i < tmpSize - 1; i++) {
                // 得到每个字段
                PIMField tmpField = (PIMField) prmDBTable.getField(prmKeys[i]);
                tmpSB.append(tmpField.getFieldName()).append(" = ?, ");
            }
            // 处理最后的一个字段
            PIMField tmpField = (PIMField) prmDBTable.getField(prmKeys[tmpSize - 1]);

            tmpSB.append(tmpField.getFieldName()).append(" = ?").append(" WHERE ID = ").append(tmpId).append(';');
        }

        return tmpSB.toString();
    }

    /**
     * 准备数据插入的sql语句
     */
    private String getInsertPreparedSql(
            String prmTableName,
            PIMRecord prmRecord,
            Object[] prmKeys,
            PIMDBTable prmDBTable) {
        StringBuffer tmpSb = new StringBuffer();
        Hashtable rc = prmRecord.getFieldValues();
        int length = prmKeys.length;
        if (rc.size() == 0) {
            tmpSb.append("INSERT INTO ").append(prmTableName).append(" (ID) VALUES(").append(prmRecord.getRecordID())
                    .append(')');
        } else {
            PIMField field;
            tmpSb.append("INSERT INTO ").append(prmTableName).append(" (");
            for (int i = 0; i < length - 1; i++) {
                field = (PIMField) prmDBTable.getField(prmKeys[i]);
                tmpSb.append(field.getFieldName());
                tmpSb.append(", ");
            }
            field = (PIMField) prmDBTable.getField(prmKeys[length - 1]);
            tmpSb.append(field.getFieldName());
            tmpSb.append(") VALUES(");
            for (int i = 0; i < length - 1; i++) {
                tmpSb.append("?");
                tmpSb.append(", ");
            }
            tmpSb.append("?");
            tmpSb.append(CASUtility.RIGHT_BRACKET);
        }
        return tmpSb.toString();
    }

    /**
     * @取到每一张表中要默认插入的值的字符串
     */
    private Hashtable getDefaultInsertHash(
            String prmTableName) {
        Hashtable tmpValues = initDefaultInsert();
        return prmTableName != null && tmpValues != null ? (Hashtable) tmpValues.get(prmTableName) : null;
    }

    /**
     * 各表中要修改的值
     * 
     * @解决目前的问题的要修改的
     */
    private Hashtable initDefaultInsert() {

        return null;

        // Hashtable APPOINTMENT_VALUES_HASH = new Hashtable();
        // Hashtable CONTACT_VALUES_HASH = new Hashtable();
        // Hashtable TASK_VALUES_HASH = new Hashtable();
        // Hashtable INBOX_VALUES_HASH = new Hashtable();
        // Hashtable DIARY_VALUES_HASH = new Hashtable();
        // Hashtable OUTBOX_VALUES_HASH = new Hashtable();
        // Hashtable SENDEDBOX_VALUES_HASH = new Hashtable();
        //
        // //联系人
        // CONTACT_VALUES_HASH.put("INFOLDER", "[PIM, 资讯管理, 联系人]");
        // INBOX_VALUES_HASH.put("INFOLDER", "[PIM, 资讯管理, 收件箱]");
        // OUTBOX_VALUES_HASH.put("INFOLDER", "[PIM, 资讯管理, 发件箱]");
        // SENDEDBOX_VALUES_HASH.put("INFOLDER", "[PIM, 资讯管理, 已发送项]");
        // DIARY_VALUES_HASH.put("INFOLDER", "[PIM, 资讯管理, 日记]");
        // TASK_VALUES_HASH.put("INFOLDER", "[PIM, 资讯管理, 任务]");
        // APPOINTMENT_VALUES_HASH.put("INFOLDER", "[PIM, 资讯管理, 日历]");
        //
        //
        // CONTACT_VALUES_HASH.put("READED", Boolean.TRUE);
        // DIARY_VALUES_HASH.put("READED", Boolean.TRUE);
        // TASK_VALUES_HASH.put("READED", Boolean.TRUE);
        // OUTBOX_VALUES_HASH.put("READED", Boolean.TRUE);
        // APPOINTMENT_VALUES_HASH.put("READED", Boolean.TRUE);
        // SENDEDBOX_VALUES_HASH.put("READED", Boolean.TRUE);
        // //注意这里的约会要导入"INFOLDER"字段？
        // //日历导入"INFOLDER"字段？？
        //
        // Hashtable tmpDefaultInsert = new Hashtable();
        // tmpDefaultInsert.put(ModelDBConstants.CONTACT_TABLE_NAME, CONTACT_VALUES_HASH);
        // tmpDefaultInsert.put(ModelDBConstants.INBOX_TABLE_NAME, INBOX_VALUES_HASH);
        // tmpDefaultInsert.put(ModelDBConstants.OUTBOX_TABLE_NAME, OUTBOX_VALUES_HASH);
        // tmpDefaultInsert.put(ModelDBConstants.SENDEDBOX_TABLE_NAME, SENDEDBOX_VALUES_HASH);
        // tmpDefaultInsert.put(ModelDBConstants.DIARY_TABLE_NAME, DIARY_VALUES_HASH);
        // tmpDefaultInsert.put(ModelDBConstants.TASK_TABLE_NAME, TASK_VALUES_HASH);
        // tmpDefaultInsert.put(ModelDBConstants.APPOINTMENT_TABLE_NAME, APPOINTMENT_VALUES_HASH);
        // return tmpDefaultInsert;
    }

    /**
     * 取记录集
     * 
     * @NOTE 注意在取记录的是否如果当前字段为空的是否是不加入到Hashtable中的
     */
    private Hashtable getRecordHash(
            ResultSet prmRs,
            ResultSetMetaData prmRsmd,
            int prmRecordID,
            int prmStartColumn,
            int prmColumnCount) throws SQLException {
        Hashtable tmpHash = new Hashtable();
        if (prmStartColumn >= prmColumnCount) // 判断范围
        {
            return tmpHash;
        }
        for (int i = prmStartColumn; i <= prmColumnCount; i++) {
            int tmpFieldType = prmRsmd.getColumnType(i);
            Object tmpValue = null;
            if (tmpFieldType == Types.BIT) // BIT
            {
                tmpValue = new Boolean(prmRs.getBoolean(i));
            } else if (tmpFieldType == Types.TINYINT) // TINYINT
            {
                tmpValue = new Byte(prmRs.getByte(i));
            } else if (tmpFieldType == Types.SMALLINT) // SMALLINT
            {
                tmpValue = new Short(prmRs.getShort(i));
            } else if (tmpFieldType == Types.INTEGER) // INTEGER
            {
                tmpValue = PIMPool.pool.getKey(prmRs.getInt(i));
            } else if (tmpFieldType == Types.FLOAT) // FLOAT
            {
                tmpValue = new Float(prmRs.getFloat(i));
            } else if (tmpFieldType == Types.DOUBLE) // DOUBLE
            {
                tmpValue = new Double(prmRs.getDouble(i));
            } else if (tmpFieldType == Types.TIMESTAMP) // TIMESTAMP
            {
                Timestamp tmpStamp = prmRs.getTimestamp(i);
                if (tmpStamp != null) {
                    Date tmpDate = new Date(tmpStamp.getTime());
                    tmpValue = tmpDate;
                }
            } else if (tmpFieldType == Types.BINARY) // IMAGE
            {
                tmpValue = prmRs.getObject(i);
            } else if (tmpFieldType == Types.VARCHAR) {
                tmpValue = prmRs.getString(i);
            } else if (tmpFieldType == Types.OTHER) {
                byte[] tmpOther = prmRs.getBytes(i);
                if (tmpOther != null) {
                    tmpValue = PIMDBUtil.decodeByteArrayToSerializeObject(tmpOther);
                }
            } else {
                tmpValue = prmRs.getString(i);
            }

            if (tmpValue != null) {
                tmpHash.put(PIMPool.pool.getKey(i - 1), tmpValue);
            }
        }
        return tmpHash;
    }

    /**
     * 查找当前记录在数据库中是否已经存在
     */
    private boolean hasRecord(
            String prmTabN,
            Connection prmConn,
            String prmCondi) {
        try {
            StringBuffer tmpSql = new StringBuffer();
            tmpSql.append("SELECT ID FROM ").append(prmTabN);
            // 添加查询的条件
            if (prmCondi != null && prmCondi.length() > 0) {
                tmpSql.append(" WHERE ").append(prmCondi);
            }
            // 得到准备的状态
            PreparedStatement prepareStmt = prmConn.prepareStatement(tmpSql.toString());
            ResultSet tmpRs = prepareStmt.executeQuery();
            // 判断当前的查找的结果集是否为空
            boolean hasNext = tmpRs.next();

            tmpRs.close();
            tmpRs = null;

            prepareStmt.close();
            prepareStmt = null;
            // 返回是否存在当前的记录
            return hasNext;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // /**
    // *此方法执行后只是将记录移入已删除项中，而不是彻底的删除这条记录
    // */
    // private void deleteRecord(Connection prmConn, String prmTableName)
    // {
    // try
    // {
    // String tmpTableName = prmTableName;
    // StringBuffer tmpSql = new StringBuffer();
    // tmpSql.append("DELETE FROM ").append(tmpTableName).append(';');
    // Statement tmpSmt = prmConn.createStatement();
    // tmpSmt.executeUpdate(tmpSql.toString());
    // tmpSmt.close();
    // tmpSmt = null;
    // }
    // catch (SQLException e)
    // {
    // e.printStackTrace();
    // closeCurConnection();
    // }
    // }
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 类型转换 beg
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @初始化所有的可以转化的类型
     */
    private void initEnableChangedFlag() {
        int tmpTypesSize = typesList.size();
        typeConversionFlag = new byte[tmpTypesSize * tmpTypesSize];
        java.util.Arrays.fill(typeConversionFlag, (byte) -1); // 把所有的值都默认设置为 -1
        /* 应许类型转化的值为： */
        setEnbaleChangedFlag(Types.INTEGER, Types.BIT); // INTEGER类型 转化为 BYTE 类型
        setEnbaleChangedFlag(Types.BIT, Types.VARCHAR); // BIT类型 转化为 VARCHAR 类型
        setEnbaleChangedFlag(Types.SMALLINT, Types.TINYINT); // SMALLINT类型 转化为 TINYINT 类型
        setEnbaleChangedFlag(Types.INTEGER, Types.SMALLINT); // INTEGER类型 转化为 SMALLINT 类型
        setEnbaleChangedFlag(Types.SMALLINT, Types.BIT); // SMALLINT类型 转化为 BIT 类型
        /* 以上这些为目前可以转化的，以后可能会添加 。@note 限制时为了不使数据出错 */
    }

    byte[] typeConversionFlag;

    /**
     * @设置转换标志
     */
    private boolean setEnbaleChangedFlag(
            int prmFir,
            int prmSec) {
        int tmpSourceIndex = getTypesIndex(prmFir);
        int tmpTargerIndex = getTypesIndex(prmSec);
        int tmpTypesSize = typesList.size();
        int tmpAryIndex = tmpSourceIndex * tmpTypesSize + tmpTargerIndex;
        if (tmpTypesSize <= 0 || tmpSourceIndex < 0 || tmpTargerIndex < 0 || tmpAryIndex < 0
                || tmpAryIndex >= tmpTypesSize * tmpTypesSize) {
            return false;
        }
        typeConversionFlag[tmpAryIndex] = (byte) 1; // 设置为Enable
        return true;
    }

    /**
     * @初始化类型转换
     */
    private void initTypesList() {
        try {
            Class tmpClass = Class.forName("java.sql.Types"); // 加载这个类
            Field[] tmpTypeFields = tmpClass.getDeclaredFields(); // 取到所有的定义的类型
            int tmpTypeCount = tmpTypeFields.length;
            typesList = new ArrayList(tmpTypeCount);
            for (int i = 0; i < tmpTypeCount; i++) {
                // 因为这个类中定义的全都是int类型的
                typesList.add(PIMPool.pool.getKey(tmpTypeFields[i].getInt(null)));
            }
        } catch (Exception e) {
        }
    }

    /**
     * @当前的类型在Types中的索引值
     */
    private int getTypesIndex(
            int prmTypesInt) {
        return typesList.indexOf(PIMPool.pool.getKey(prmTypesInt));
    }

    /**
     * 判断类型是否可以转换
     */
    private boolean isTypeChangeEnabled(
            int prmSourceType,
            int prmTargerType) {
        int tmpSourceIndex = getTypesIndex(prmSourceType);
        int tmpTargerIndex = getTypesIndex(prmTargerType);
        int tmpTypesSize = typesList.size();
        int tmpAryIndex = tmpSourceIndex * tmpTypesSize + tmpTargerIndex;

        if (tmpTypesSize <= 0 || tmpSourceIndex < 0 || tmpTargerIndex < 0 || tmpAryIndex < 0
                || tmpAryIndex >= tmpTypesSize * tmpTypesSize) {
            return false;
        }
        return (typeConversionFlag[tmpAryIndex] > 0);
    }

    /**
     * @类型转化，注意当前没有把所有的类型转化都给列出来
     */
    private Object changeType(
            Object prmOldValue,
            int prmOldType,
            int prmNewType) {
        if (prmOldValue == null || !isTypeChangeEnabled(prmOldType, prmNewType)) // 当前类型不支持转换
        {
            return null;
        }
        if (prmOldType == Types.INTEGER && prmNewType == Types.BIT) {
            return (((Integer) prmOldValue).intValue() == (int) 1) ? Boolean.TRUE : Boolean.FALSE; // Integer 类型 转换为 BIT
                                                                                                   // 类型
        } else if (prmOldType == Types.BIT && prmNewType == Types.VARCHAR) {
            return String.valueOf(((Boolean) prmOldValue).booleanValue()); // BIT转换为VARCHAR类型
        } else if (prmOldType == Types.SMALLINT && prmNewType == Types.TINYINT) {
            return new Byte(((Short) prmOldValue).byteValue()); // SMALLINT类型转换为TINYINT类型
        } else if (prmOldType == Types.INTEGER && prmNewType == Types.SMALLINT) {
            return new Short(((Integer) prmOldValue).shortValue()); // INTEGER 转换为 SMALLINT类型
        } else if (prmOldType == Types.SMALLINT && prmNewType == Types.BIT) {
            return (((Short) prmOldValue).shortValue() == (short) 1) ? Boolean.TRUE : Boolean.FALSE; // SMALLINT 类型转为
                                                                                                     // BIT 类型
        } else {
            return null;
        }
    }

    // end 类型转化
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList typesList;

    // private String dbConnectionPath;
    // 连接数据库
    // ///////////////////////////////////////////////////////////////
    private boolean isUpdateSucceed; // 数据库更新是否成功
    private boolean isCreateTempTableSucceed = true; // 创建临时表

    // private boolean isExistExceptionFlagTable; //标志表
    // 连接数据库
    private Connection dbConn; // 数据库连接
    private PIMDBTable tepDBTable; // 旧版本数据库表信息
    private PIMDBTable newDBTable; // 新版本数据库表信息
    // private PIMDBTable pimOldDBTable; //旧版本数据库表信息
    // private PIMDBTable pimTepDBTable; //临时表数据库信息
    //
    private ArrayList oldTableNameList; // 旧版本数据库表
    private ArrayList curAllTableNames; // 当前数据库中所有表的名字
    private ArrayList newRecordFieldNameList;
}
