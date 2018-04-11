package org.cas.client.platform.pimmodel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.thread.ThreadActionsFacade;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMDBUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.TmpConstants;
import org.cas.client.platform.pimmodel.datasource.IDbDataSource;
import org.cas.client.platform.pimmodel.datasource.MailRuleContainer;
import org.cas.client.platform.pimmodel.datasource.MailRulePool;
import org.cas.client.platform.pimmodel.datasource.ViewFormat;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimmodel.util.ModelConstants2;
import org.cas.client.resource.international.IntlModelConstants;

public final class PIMDBModel extends AbstractModel {
    public static final String dbName = "/pim"; // 数据库名@NOTE:forward slash can be used in both windows and linux.

    // 注意：HSQL结果集的getObject(i)返回TINYINT类型时,返回的不是Byte对象,而是Integer对象,所以要通过getByte()得到其byte值，
    // 如果其值没有设置，将返回0,SMALLINT同byte一样,而只有对象能添加到列表中，
    // 所以此处共享一个Byte对象和Short对象其对应的值为0，以便返回0值时，共享以下对象
    public static final Byte DEFAULT_BYTE_OBJECT = new Byte((byte) 0);

    public static final Short DEFAULT_SHORT_OBJECT = new Short((short) 0);

    /** Creates a new instance of DefaultPIMDBModel */
    public PIMDBModel() {
        // 检查数据库状态初始化数据库并建立连接,此处添加更新数据库的操作,此功能待添加
        // @NOTE: 数据库初始化错误情况：(1)不存在数据库文件(2)数据库版本不同，存在数据库文件需要更新
        if (!PIMDBConnecter.instance.checkDatabase()) {// 建立数据库连接,并且检查数据库是否为空数据库//数据库连接正常则返回,不正常进入.
            ErrorUtil.write("数据库文件已损坏, 无法建立连接");
            // TODO:,此处添加更新数据库的操作,此功能待添加
        }
        tableStructurePool = new DBTableStructurePool();// 数据库表结构缓存
        initTableNames();// 取到数据库中所有的表的名字【共享数据库表的名字，不用每次取数据库的名字，减少数据库操作】
        // printTableContent(ModelConstants2.VIEWINFO_TABLE_NAME);
    }

    public static Connection getConection() {
        try {
            return PIMDBConnecter.instance.getConnection();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查当前输入的用户名和密码是否有效，如果数据库表中没有用户名和密码记录时，则会在用户第一次输入时保存用户输入的用户名和密码。 用户名不能为null, 也不能为""字符,
     * 密码不能为null,但是可以为""字符.【用户名和密码大小写敏感】 用户名和密码最大长度都为15个[char]，eio中定义最大为15『位』。超过长度只匹配前15个char
     * 
     * @param prmUserName
     *            用户名
     * @param prmPassword
     *            密码
     * @return int 0 : 通过 （数据库中无用户和密码信息时，第一次输入的用户名和密码时，次方法都会返回 0：通过） 1 : 无此帐号 2 : 密码不符合
     */
    @Override
    public int certificate(
            String prmUserName,
            String prmPassword) {
        // final int MAX_LEN = 15; //设置定用户名和密码的最大长度为15个字符
        try {
            Connection conn = PIMDBConnecter.instance.getConnection(); // 得到数据库连接
            if (conn == null)
                return 2;

            if (prmUserName == null || prmUserName.length() < 1) // 用户名错误
                return 1;
            else if (prmPassword == null) // 密码错误
                return 2;

            // String tmpUserName, tmpPassword;
            // tmpUserName = prmUserName.length() > MAX_LEN ? prmUserName.substring(0, MAX_LEN) : prmUserName;
            // tmpPassword = prmPassword.length() > MAX_LEN ? prmPassword.substring(0, MAX_LEN) : prmPassword;

            int resultInt = 1; // 默认为无此帐号
            String sql =
                    "SELECT COUNT(*) FROM ".concat(ModelConstants2.SYSTEMTABLE_NAME_LIST[ModelConstants2.USERIDENTITY
                            * -1]);
            Statement smt = conn.createStatement();
            // checkContainsTable(smt, DefaultDBInfo.USER_IDENTITY); //如果不存在当前的数据库表，则创建。
            ResultSet rs = smt.executeQuery(sql);

            int size = !rs.next() ? 0 : rs.getInt(1);
            if (size == 0) { // 用户信息表中不存在记录保存当前的记录
                sql =
                        "INSERT INTO ".concat(ModelConstants2.SYSTEMTABLE_NAME_LIST[ModelConstants2.USERIDENTITY * -1])
                                .concat("(USERNAME, PASSWORD)").concat("VALUES('").concat(prmUserName).concat("', '")
                                .concat(prmPassword).concat("');");
                resultInt = smt.executeUpdate(sql) > 0 ? 0 : 1; // 插入成功则返回通过，否则为无此帐号
            } else {
                sql =
                        "SELECT PASSWORD FROM "
                                .concat(ModelConstants2.SYSTEMTABLE_NAME_LIST[ModelConstants2.USERIDENTITY * -1])
                                .concat(" WHERE ").concat(ModelConstants2.USERNAME).concat(" = '").concat(prmUserName)
                                .concat("'");
                rs = smt.executeQuery(sql);

                while (rs.next()) {
                    resultInt = 2; // 有匹配的帐号，所以设置为密码不匹配
                    if (prmPassword.equals(rs.getString(1))) { // 注意用户名和密码区分大小写
                        resultInt = 0; // 通过
                        break;
                    }
                }
            }

            rs.close(); // 关闭结果集
            rs = null;

            smt.close();
            return resultInt;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return 2; // 捕捉到异常返回：密码不符合。
        }
    }

    /**
     * 在数据库中插入一条记录, 调用此方法时,可选择是否要刷新视图 : 因为刷新视图的动作很影响性能,用户可以选择,插入n条记录以后,最后一条刷新操作,可以提高数据库操作的性能
     * 
     * @param prmRecord
     *            记录实例
     * @param prmIsRefresh
     *            是否刷新,此参数决定用来对于插入一条记录时是否刷新视图
     * 
     * @return boolean 如果记录插入成功则返回ture，否则为false
     */
    @Override
    public boolean insertRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh) {
        try {
            if (prmRecord == null)// 判断记录是否为空
                return false;

            Connection conn = PIMDBConnecter.instance.getConnection();
            if (conn == null) {// "<error message> + param1.toString() + param2.label + param2.toString()";
                ErrorUtil.write("记录插入失败".concat(prmRecord.toString()).concat(" 刷新:")
                        .concat(String.valueOf(prmIsRefresh)));
                return false;
            }

            boolean result = insertRecord(conn, prmRecord);
            if (prmIsRefresh)
                firePIMModelDataInserted(prmRecord.getAppIndex(), new PIMRecord[] { prmRecord }); // 发送成功插入记录的事件

            PIMDBConnecter.instance.reConnectDb();// 关闭数据库连接
            return result;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return false;
        }
    }

    /**
     * note: 在操作批量处理数据时，此方法要注意，如果在操作中遇到异常 则没有插入的记录将被忽略，如果有记录插入则要刷新视图，在所有的记录插入数据库后再刷新视图
     * 
     * 尝试：采用异常的批处理，对于当前操作的表中的所有的字段，生成预编译的SQL语句，使用同一个状态集完成所有记录的插入 因为，预编译时不能估计到待插入的记录的字段，所以SQL中采用了所有的字段
     * 结果：性能反而降低，在导入联系人的测试中，导入4800条的记录时，目前的方案使用的时间反而是批处理使用的时间的一半 所以此处还是采用此方法来批量插入记录
     * 
     * @param: List 记录
     * @return boolean 如果插入成功返回true,否则返回false
     * 
     * @NOTE:默认插入到同一张数据库表中【只支持到插入到一张数据库表中】
     */
    @Override
    public boolean insertRecords(
            List prmRecords) {
        int size = -1;
        if (prmRecords == null || (size = prmRecords.size()) < 1)
            return false; // 判断列表中没有记录则不用

        int tmpCount = 0; // 记录成功插入的记录的数目
        PIMRecord[] tmpRecords = new PIMRecord[size];
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            for (int i = 0; i < size; i++) {
                PIMRecord tmpRd = ((PIMRecord) prmRecords.get(i));
                if (insertRecord(conn, tmpRd))
                    tmpRecords[tmpCount++] = tmpRd; // 记录插入成功则添加到已经成功插入的记录数组中
            }
        } catch (SQLException e) { // 捕捉到任何的SQLException异常都返回，此处返回时，无需处理
            e.printStackTrace();
        }

        PIMDBConnecter.instance.reConnectDb(); // 关闭数据库连接
        if (tmpCount > 0) // 有插入数据库记录则发送视图更新操作，否则不用刷新视图
            firePIMModelDataInserted(((PIMRecord) prmRecords.get(0)).getAppIndex(), tmpRecords);
        return tmpCount == size;
    }

    // ==============================================================================//
    // 数据库更新操作
    // ==============================================================================//
    /**
     * 更新数据库记录,在数据库中更新一条记录时调用此方法时,可选择是否要刷新视图 NOTE : 因为刷新视图的动作很影响性能,用户可以选择,更新n条记录以后,最后一条刷新操作,可以提高数据库操作的性能
     * 
     * 
     * @param: IRecord prmRecord 数据库记录
     * @param prmIsRefresh
     *            刷新视图
     * @return: boolean 更新成功返回true, 否则返回false
     */
    @Override
    public boolean updateRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh) {
        try {
            if (prmRecord == null)// 判断记录是否为空
                return false;

            Connection conn = PIMDBConnecter.instance.getConnection();
            if (conn == null) {
                // "<error message> + param1.toString() + param2.label + param2.toString()";
                String tmpErrorMessage =
                        "记录更新失败".concat(prmRecord.toString()).concat(" 刷新:").concat(String.valueOf(prmIsRefresh));
                ErrorUtil.write(tmpErrorMessage);
                return false;
            }

            boolean result = updateRecord(conn, prmRecord);// 插入记录
            if (prmIsRefresh)
                firePIMModelDataUpdated(prmRecord.getAppIndex(), new PIMRecord[] { prmRecord }); // 发送成功插入记录的事件
            PIMDBConnecter.instance.reConnectDb();// 关闭数据库连接
            return result;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return false;
        }
    }

    /**
     * note: 在操作批量处理数据时，此方法要注意，如果在操作中遇到异常 则没有更新的记录将被忽略，如果有更新则要刷新视图，在所有的记录得到更新后再刷新视图
     * 
     * 尝试：采用异常的批处理，对于当前操作的表中的所有的字段，生成预编译的SQL语句，使用同一个状态集完成所有记录的插入 因为，预编译时不能估计到待插入的记录的字段，所以SQL中采用了所有的字段
     * 结果：性能反而降低，目前的方案使用的时间反而是批处理使用的时间的一半，所以此处还是采用此方法来批量更新记录
     * 
     * @param: List 记录
     * @return boolean 如果记录更新成功返回true,否则返回false
     * 
     * @NOTE:默认更新同一张数据库表中的记录【只支持更新同一张数据库表】
     */
    @Override
    public boolean updateRecords(
            List prmRecords) {
        int size = -1;
        if (prmRecords == null || (size = prmRecords.size()) == 0) // 记录为空
            return false;

        int count = 0; // 记录成功插入的记录的数目
        PIMRecord[] tmpRecords = new PIMRecord[size];
        try {
            Connection conn = PIMDBConnecter.instance.getConnection(); // 数据库连接
            if (conn == null)
                return false;

            for (int i = 0; i < size; i++) { // 更新数据库表记录
                PIMRecord tmpRd = (PIMRecord) prmRecords.get(i);
                if (updateRecord(conn, tmpRd))
                    tmpRecords[count++] = tmpRd;
            }
        } catch (SQLException e) { // 捕捉到任何的SQLException异常都返回，此处返回时，无需处理
            e.printStackTrace();
        }

        PIMDBConnecter.instance.reConnectDb(); // 关闭
        if (count > 0) { // 有更新数据库记录则发送视图更新操作，否则不用刷新视图
            int type = ((PIMRecord) prmRecords.get(0)).getAppIndex();
            firePIMModelDataUpdated(type, tmpRecords);
        }
        return count == size;
    }

    // ==============================================================================//
    // 删除数据库记录操作
    // ==============================================================================//
    /**
     * 删除一条记录,此方法不删除在回收张的记录,只删除appType > 7 或者appType < 7的记录
     * 
     * note: 以下为应用类型对应删除的目的地：
     * 
     * 日历－－－－－－－－－0 (ModelConstants.CALENDAR_APP 回收站 任务－－－－－－－－－1 (ModelConstants.TASK_APP 回收站 联系人－－－－－－－－2
     * (ModelConstants.CONTACT_APP 回收站 日记－－－－－－－－－3 (ModelConstants.DIARY_APP 回收站 收件箱－－－－－－－－4 (ModelConstants.INBOX_APP
     * 回收站 发件箱－－－－－－－－5 (ModelConstants.OUTBOX_APP 回收站 已发送邮件－－－－－－6 (ModelConstants.SENDED_APP 回收站 删除项－－－－－－－－7
     * (ModelConstants.DELETED_ITEM_APP <此处没有做处理，因为已删除项，没有删除的概念了，删除即标识彻底删除记录了，所以此处忽略此应用类型> 通讯组列表－－－－－－8 彻底删除
     * 视图信息－－－－－－－9 彻底删除 邮件帐户－－－－－－－10 彻底删除
     * 
     * @see emo.pim.pimutil.ModelCons
     * 
     * @param: IRecord prmRecord 待删除的记录
     * @return: boolean 删除成功返回true,否则返回false
     */
    @Override
    public boolean deleteRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh) {
        int appType = prmRecord.getAppIndex(); // 注意插件应用没有相应的回收站数据库表
        boolean deleteRt = false;
        if (appType >= 0) {
            prmRecord.setDeleted(true);
            deleteRt = updateRecord(prmRecord, false);
            PIMDBConnecter.instance.reConnectDb();
            if (prmIsRefresh)
                firePIMModelDataDeleted(appType, new PIMRecord[] { prmRecord }); // 发送记录更新事件
            return deleteRt;
        } else
            return dropRecord(prmRecord); // 对于删除应用类型小于0的表的记录时，彻底删除记录
    }

    /**
     * 删除多条记录,此方法不删除在回收张的记录,只删除appType > 7 或者appType < 7的记录
     * 
     * note: 以下为应用类型对应删除的目的地：
     * 
     * 日历－－－－－－－－－0 (ModelConstants.CALENDAR_APP 回收站 任务－－－－－－－－－1 (ModelConstants.TASK_APP 回收站 联系人－－－－－－－－2
     * (ModelConstants.CONTACT_APP 回收站 日记－－－－－－－－－3 (ModelConstants.DIARY_APP 回收站 收件箱－－－－－－－－4 (ModelConstants.INBOX_APP
     * 回收站 发件箱－－－－－－－－5 (ModelConstants.OUTBOX_APP 回收站 已发送邮件－－－－－－6 (ModelConstants.SENDED_APP 回收站 删除项－－－－－－－－7
     * (ModelConstants.DELETED_ITEM_APP <此处没有做处理，因为已删除项，没有删除的概念了，删除即标识彻底删除记录了，所以此处忽略此应用类型> 通讯组列表－－－－－－8 彻底删除
     * 视图信息－－－－－－－9 彻底删除 邮件帐户－－－－－－－10 彻底删除
     * 
     * @see emo.pim.pimutil.ModelCons
     * 
     * @param: List prmRecords 待删除的记录列表
     * @return: boolean 如果记录为空则返回false，因为没有记录，否则记录删除删除成功则返回true，否则返回false
     */
    @Override
    public boolean deleteRecords(
            List prmRecords) {
        if (prmRecords == null || prmRecords.size() < 1) // 空则返回
            return false;
        int i = 0;
        for (int len = prmRecords.size() - 1; i < len; i++)
            deleteRecord((PIMRecord) prmRecords.get(i), false);
        return deleteRecord((PIMRecord) prmRecords.get(i), true);
    }

    // ==============================================================================//
    // 恢复被删除的记录
    // ==============================================================================//
    /**
     * 恢复被删除的记录，从当前应用对应的回收站表中恢复对应的记录 可以恢复的记录表对应的应用类型为：
     * 
     * 视图应用名 应用类型 回收站表
     * 
     * 日历 ModelConstants.TASK_APP PPOINTMENT_RECYCLE 任务 ModelConstants.CALENDAR_APP TASK_RECYCLE 联系人
     * ModelConstants.CONTACT_APP CONTACT_RECYCLE 日记 ModelConstants.DIARY_APP DIARY_RECYCLE 收件箱 ModelConstants.INBOX_APP
     * INBOX_RECYCLE 发件箱 ModelConstants.OUTBOX_APP OUTBOX_RECYCLE 已发送邮件 ModelConstants.SENDED_APP SENDEDBOX_RECYCLE
     * 
     * 
     * @param PIMRecord
     *            数据库记录
     * @param prmIsRefresh
     *            时候要刷新视图
     * 
     * @return 恢复是否成功
     */
    @Override
    public boolean restoreDeletedRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh) {
        return false;
        // Vector tmpRecords = new Vector();
        // tmpRecords.add(prmRecord);
        //
        // int app = prmRecord.getAppIndex();
        // //判断待恢复的记录是否在已删除项中的记录,如果不在已删除项中则选择已删除项中的记录
        // return (app == ModelConstants.DELETED_ITEM_APP)
        // ? restoreFromRecycleBin(tmpRecords, prmIsRefresh)
        // : restoreFromRecycleBin(getRecycleRecords(tmpRecords), prmIsRefresh);
    }

    /**
     * 恢复被删除的记录，从当前应用对应的回收站表中恢复对应的记录 可以恢复的记录表对应的应用类型为：
     * 
     * 视图应用名 应用类型 回收站表
     * 
     * 日历 ModelConstants.TASK_APP APPOINTMENT_RECYCLE 任务 ModelConstants.CALENDAR_APP TASK_RECYCLE 联系人
     * ModelConstants.CONTACT_APP CONTACT_RECYCLE 日记 ModelConstants.DIARY_APP DIARY_RECYCLE 收件箱 ModelConstants.INBOX_APP
     * INBOX_RECYCLE 发件箱 ModelConstants.OUTBOX_APP OUTBOX_RECYCLE 已发送邮件 ModelConstants.SENDED_APP SENDEDBOX_RECYCLE
     * 
     * 
     * @param records
     *            数据库记录列表
     * @param prmIsRefresh
     *            时候要刷新视图
     * 
     * @return 恢复是否成功
     */
    @Override
    public boolean restoreDeletedRecords(
            List records) {
        if (records == null || (records.size()) < 1) // 判断有无记录需要恢复,无则返回false
        {
            return false;
        }

        int app = ((PIMRecord) records.get(0)).getAppIndex();
        // 判断待恢复的记录是否在已删除项中的记录,如果不在已删除项中则选择已删除项中的记录
        return (app == ModelCons.DELETED_ITEM_APP) ? restoreFromRecycleBin(records, true) : restoreFromRecycleBin(
                getRecycleRecords(records), true);
    }

    /**
     * 彻底删除一条记录,此方法针对删除在回收站中的记录
     * 
     * @TODO:邮件附件原来为根据类型APP和记录的ID号,以及附件的名字来得到 例如:&3_89_tutian.*,修改以后,所有的邮件删除后,都移动到回收站中,当前的记录已经改变
     *                                        即App变,ID号变,所以得不到对应的附件名字(虽然可以根据INFOLDER得到App,但是ID号变了)
     *                                        所以此次附件名字有待修改,(修改在保存附件时即生成附件名后&3_89_tutian.*,替代原来的数据库表附件字段tutian.*的名字)
     * 
     * @param prmRecord
     *            记录 prmIsRefresh 是否刷新视图
     * @return boolean 删除是否成功
     */
    @Override
    public boolean permanentlyDeleteRecord(
            PIMRecord prmRecord,
            boolean isRefresh,
            boolean isDelAttach) {
        try {
            // 数据库连接
            Connection conn = PIMDBConnecter.instance.getConnection();
            if (conn == null) {
                return false;
            }

            // 判断是否为已删除项中记录
            int type = prmRecord.getAppIndex();

            boolean deleteRs = deleteRecord(conn, prmRecord, getTableName(type));

            Vector recVec = new Vector();
            recVec.add(prmRecord);

            if (isDelAttach) {
                CASUtility.deleteAttachFiles(recVec); // TODO
            }
            // 删除邮件对应的附件

            if (isRefresh && deleteRs) {
                firePIMModelDataDeleted(type, new PIMRecord[] { prmRecord });
            }
            // 关闭
            PIMDBConnecter.instance.reConnectDb();
            return deleteRs;

        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return false;
        }
    }

    /**
     * 彻底删除多条记录,如果SQL执行失败且如果已经有记录被删除则刷新视图删除相应记录的附件 如果没有记录被删除则跳出程序不刷新视图，或者抛出SQLException异常，且没有记录被删除则不刷新视图
     * 
     * @param: Vector records 待彻底删除的记录列表
     * @return: boolean 如果删除成功则返回ture，否则捕捉到SQLException，列表中的记录没有完全被删除则返回false，既只要有一个记录没有被成功删除则返回false
     */
    @Override
    public boolean permanentlyDeleteRecords(
            List prmRecords,
            boolean isDelAttach) {
        int size = -1;
        if (prmRecords == null || (size = prmRecords.size()) < 1) {
            return false;
        }

        int count = 0;
        PIMRecord[] freshRd = new PIMRecord[size];
        int type = ((PIMRecord) prmRecords.get(0)).getAppIndex(); // 得到记录所在的App类型,如果为7则为已删除项
        try {
            // 连接数据库
            Connection conn = PIMDBConnecter.instance.getConnection();
            if (conn == null) {
                return false;
            }

            for (int i = 0; i < size; i++) // 彻底删除所有的记录,如果有记录删除失败则结束删除动作
            {
                PIMRecord tmpRecord = (PIMRecord) prmRecords.get(i);
                boolean deleteRs = deleteRecord(conn, tmpRecord, getTableName(tmpRecord));

                if (deleteRs) {
                    freshRd[count++] = tmpRecord;
                }
            }
        } catch (Exception e) // 此处不做处理，遇到异常
        {
            e.printStackTrace();
        }
        PIMDBConnecter.instance.reConnectDb(); // 关闭数据库的连接
        if (count > 0) {
            firePIMModelDataDeleted(type, freshRd);
            Vector vector = new Vector(count);
            for (int i = 0; i < count; i++) {
                vector.add(freshRd[i]);
            }
            if (isDelAttach) {
                CASUtility.deleteAttachFiles(vector); // 删除附件
            }
        }
        // 如果刷新的记录数据和总的记录的数目相同则删除成功,否则定有某些记录删除失败
        return size == count;
    }

    // ****************************************************************************************************************************/
    //
    //
    // ****************************************************************************************************************************/
    // 选择记录=============================================================//
    /**
     * note: 选择记录时，选择某应用的某个INFOLDER路径下的id号等于recId的记录
     * 
     * 表的名字有APP和INFOLDER来判断, (1) app < 7,则根据INFOLDER得到数据库表的名字 (2) app = 7,则根据INFOLDER得到未删除前的表,由此得到此表对应的回收站的表 (3) app >
     * 7,则根据APP来得到表的名字,此时没有INFOLDER字段
     * 
     * @param int appType 应用的类型
     * @param int recordID 记录的ID号
     * @return PIMRecord 返回选择的记录
     */
    @Override
    public PIMRecord selectRecord(
            int appType,
            int recId,
            int prmFolder) {
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库连接
            PIMRecord tmpRecord = selectRecord(conn, appType, recId, getTableName(appType));// 选择记录

            PIMDBConnecter.instance.reConnectDb();
            return tmpRecord;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return null;
        }
    }

    // NOTE: 通知外部修改
    /**
     * 根据具体中间结构名，遍历某一些记录
     * 
     * @param int appType
     * @param int[]recordIDs
     * @return PIMRecord
     */
    @Override
    public Vector selectRecords(
            int app,
            int[] recIds,
            int prmFolderID) {
        try {
            // 得到数据库连接
            Connection conn = PIMDBConnecter.instance.getConnection();
            if (conn == null) {
                return null;
            }

            String tabName = getTableName(app);

            Vector recordsVt =
                    getRecordsVec(conn, app, tabName, prmFolderID, createSelectPrepareSql(tabName, recIds, null));

            // 关闭连接
            PIMDBConnecter.instance.reConnectDb();

            return recordsVt;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return null;
        }

    }

    /* 为了查询的需要***************************************************************************** */
    /**
     * 根据具体中间结构名，遍历某一些记录
     * 
     * @param int appType
     * @param string
     *            [] where语句条件
     * @return Vector 返回记录列表
     */
    @Override
    public Vector selectRecords(
            int prmApp,
            int prmFolderID,
            String prmSql) {
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 数据库连接
            String tmpTabName = getTableName(prmApp);
            if (tmpTabName == null)
                return null;

            String sql = "SELECT * FROM ".concat(tmpTabName);// 执行数据库查询操作
            if (prmSql != null && prmSql.length() > 0) {
                sql = sql.concat(" WHRER ").concat(prmSql);
            }

            Vector recordVt = getRecordsVec(conn, prmApp, tmpTabName, prmFolderID, sql);// 选择对应的记录

            PIMDBConnecter.instance.reConnectDb();// 关闭数据库连接
            return recordVt.size() < 1 ? null : recordVt;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return null;
        }
    }

    // ====================================================================//
    // 切换视图时，取当前table(或者text)视图的所有记录来显示
    // ====================================================================//
    /**
     * 返回所有的记录,此方法为切换视图时，取当前table(或者text)视图的所有记录来显示 注意：已删除项要取所有项目的回收站表的记录
     * 
     * (支持的)视图类型：
     * 
     * 日历－－－－－－－－－0 任务－－－－－－－－－1 联系人－－－－－－－－2 日记－－－－－－－－－3 收件箱－－－－－－－－4 发件箱－－－－－－－－5 已发送邮件－－－－－－6 删除项－－－－－－－－7
     * 
     * NOTE:在取此记录时，取了当前对应的数据库表的所有的记录的【所有字段(危险，性能有大影响)】
     * 
     * @param PIMViewInfo
     *            viewInfo 视图记录信息
     * @return Vector 返回的记录信息，是一个列表，如果没有记录则返回null
     */
    @Override
    public Object[][] selectRecords(
            PIMViewInfo prmViewInfo) {
        try {
            String sql = createViewInfoQueryString(prmViewInfo); // A.s("   initSingleAppRecords[][]  sql ====== " +
                                                                 // sql);
            if (sql == null)
                return null;

            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库连接
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // 建立数据库连接状态
            ResultSet rs = stmt.executeQuery(sql);

            Object[][] values = getRecAryFromResultSet(rs, null, -1);
            // @NOTE:这个地方可以优化的第一次知道了各个列的类型以后,以后各个列的类型也就知道了,所以不用每次都判断为什么类型,如果有多条记录是,可以有优化的空间

            rs.close();// 关闭结果集合
            rs = null;
            stmt.close();// 关闭状态
            stmt = null;
            PIMDBConnecter.instance.reConnectDb(); // 关闭数据库连接

            return values;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
        } catch (ClassCastException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
        }
        return null;
    }

    // *************************************************************************************************************
    // 取数据库表的记录，用于视图显示
    // *************************************************************************************************************
    /**
     * 返回一个包括PIMRecord的Vector，取到了当前视图下的所有的记录
     * 
     * @param PIMViewInfo
     *            viewInfo 视图信息
     * @return Vector 当前视图下所有的记录，列表中存放的时PIMRecord对象，如果没有记录则返回空的vector而非null
     */
    @Override
    public Vector getAllRecord(
            PIMViewInfo viewInfo,
            String prmSql) {
        Vector recordVect = new Vector();// 得到数据库表的名字
        String tmpSql = createSelectSQLByViewInfo(viewInfo, getTableName(viewInfo.getAppIndex()), prmSql);
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 数据库连接
            Statement smt = conn.createStatement();// 生成查询的数据库状态
            ResultSet rs = smt.executeQuery(tmpSql);
            ResultSetMetaData rsmd = rs.getMetaData();

            int tmpColCount = rsmd.getColumnCount(); // 得到数据库表列数
            int tmpAppIndex = viewInfo.getAppIndex();
            int tmpFolderID = viewInfo.getPathID();
            while (rs.next()) {
                PIMRecord tmpRecord = new PIMRecord();
                Hashtable recHash = createRecordHash(rs, rsmd, 1, tmpColCount);
                tmpRecord.setFieldValues(recHash);
                tmpRecord.setAppIndex(tmpAppIndex);
                tmpRecord.setInfolderID(tmpFolderID);
                int tmpId = rs.getInt(1);
                tmpRecord.setRecordID(tmpId);
                recordVect.add(tmpRecord);
            }
        } catch (SQLException e) // 此处遇到异常后，只返回异常发生前取到的记录
        {
            e.printStackTrace();
        }

        PIMDBConnecter.instance.reConnectDb(); // 关闭数据库连接
        return recordVect;
    }

    /**
     * 插入多条mail记录
     *
     * @para: recordList 需要插入的记录的列表
     * @return: boolean 插入是否成功
     * @called by :
     */
    @Override
    public Vector insertMailRecords(
            List prmRecordList) {
        int tmpSize;
        if (prmRecordList == null || (tmpSize = prmRecordList.size()) < 1)
            return null;// 空则返回

        Vector tmpRecVec = new Vector();
        PIMRecord[] tmpRecAry = new PIMRecord[tmpSize];
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 数据库连接
            for (int i = 0; i < tmpSize; i++)// 插入邮件记录
            {
                PIMRecord tmpRec = (PIMRecord) prmRecordList.get(i);
                tmpRecAry[i] = tmpRec;

                // if(haveRepeatMail(tmpRec) || !insertRecord(conn, tmpRec))//判断有无重复的邮件, 插入邮件记录
                // continue;

                tmpRecVec.add(tmpRec);
            }

            PIMDBConnecter.instance.reConnectDb();
        } catch (Exception e) {
            // TODO: 弹出警告对话框，写入错误日志
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
        }

        firePIMModelDataInserted(tmpRecAry[0].getAppIndex(), tmpRecAry);
        return tmpRecVec.size() < 1 ? null : tmpRecVec;
    }

    /* 刷新视图 */
    @Override
    public void refreshView(
            int prmApp) {
        firePIMModelDataUpdated(prmApp);
    }

    /* 刷新视图 */
    @Override
    public void deleteRefreshView(
            int prmRefreshApp,
            PIMRecord[] prmRecord) {
        firePIMModelDataDeleted(prmRefreshApp, prmRecord);
    }

    /* 刷新视图 */
    @Override
    public void insertRefreshView(
            int prmRefreshApp,
            PIMRecord[] prmRecord) {
        firePIMModelDataInserted(prmRefreshApp, prmRecord);
    }

    /* 刷新视图 */
    @Override
    public void updateRefreshView(
            int prmRefreshApp,
            PIMRecord[] prmRecord) {
        firePIMModelDataUpdated(prmRefreshApp, prmRecord);
    }

    /**
     * 返回视图信息
     * 
     * @param int appType
     * @param int appSubType
     * @return PIMViewInfo
     */
    @Override
    public PIMViewInfo getViewInfo(
            int prmFolderID) {
        PIMViewInfo tViewInfo = (PIMViewInfo) getDataSource(ModelCons.VIEW_INFO_DATA, null, prmFolderID);// 取ViewInfo,中间的参数是被忽略的.
        return tViewInfo;
    }

    /**
     * 设置视图信息
     * 
     * @param PMViewInfo
     *            viewInfo 传递的是一个新的视图信息。
     * @return boolean
     */
    @Override
    public boolean updateViewInfo(
            PIMViewInfo viewInfo) {
        return updateDataSource(viewInfo, 0);
    }

    /**
     * 得到默认帐号
     */
    @Override
    public PIMRecord getDefaultAccount() {
        try {
            // 帐号表的名字
            String tabName = PIMDBUtil.getSystemTableName(ModelConstants2.ACCOUNT);

            // 查询默认帐号的SQL语句
            String sql = "SELECT * FROM ".concat(tabName).concat(" WHERE IS_DEFAULT_MAIL = 1;");

            // 得到数据库连接
            Connection conn = PIMDBConnecter.instance.getConnection();
            Statement smt = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();

            PIMRecord rec = new PIMRecord();
            Hashtable tmpHash = null;
            // 注意只有一个默认帐号
            if (rs.next()) {
                int recId = rs.getInt("ID");
                rec.setRecordID(recId);
                tmpHash = createRecordHash(rs, rsmd, 1, count);
            }

            boolean isNull = tmpHash == null;
            if (!isNull) {
                rec.setFieldValues(tmpHash);
            }

            // 设置为邮件帐号类型
            rec.setAppIndex(ModelConstants2.ACCOUNT);

            // 关闭
            rs.close();
            rs = null;
            smt.close();
            smt = null;
            PIMDBConnecter.instance.reConnectDb();

            return isNull ? null : rec;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
        } catch (ClassCastException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SHUTDOWN COMPACT writes out a new .script file which contains the data for all the tables, including CACHED and
     * CACHED tables. It then deletes the existing text table files and the .data file before rewriting them. After
     * this, it backs up the .data file in the same way as normal SHUTDOWN. This operations shrinks all files to the
     * minimum size. NOTE: Only an administrator may do this. 生成一个新的包含所有表(CACHED和TEXT)数据的.script,
     * 同在在重写它们之前,删除存在的TEXT类型表和.data文件, 并把CACHED类型的表的数据已压缩的形式备份到.data文件,并关闭数据库 在应用程序退出时调用此方法
     * 
     * @return boolean
     */
    @Override
    public boolean compactDataFile() {
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            Statement stmt = conn.createStatement();
            boolean success = stmt.execute("SHUTDOWN");

            stmt.close();// 关闭
            stmt = null;

            PIMDBConnecter.instance.closeConnection();
            return success;
        } catch (SQLException se) {
            PIMDBConnecter.instance.closeConnection();
            se.printStackTrace();
            return false;
        }
    }

    /**
     * 得到联系人相关的字段: (1)ID字段 (2)FieldAs字段 (3)CommGroup通讯组 (4)EMail (5)EMail_1 (6)EMail_2 (7)INFOLDER表路径
     * 说明:INFOLDER路径不用从数据库取,根据数据库表的名字得到
     */
    @Override
    public Vector getContactQuickInfo() {
        String tmpName = "CONTACT";// 得到所有的联系人类型的数据库表的名字
        int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf(tmpName);
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库连接
            Vector data = new Vector(7);
            PreparedStatement ps = null;
            ResultSet rs = null;

            int tmpLen = 0;
            String buf = "SELECT COUNT(*) FROM ".concat(tmpName);
            ps = conn.prepareStatement(buf);
            rs = ps.executeQuery();
            if (rs.next())
                tmpLen += rs.getInt(1);
            rs.close();
            rs = null;

            if (tmpLen == 0)
                tmpLen = 1;

            int[] tmpTypeAry = new int[tmpLen];
            String[] tmpIDAry = new String[tmpLen];
            String[] tmpFileAsAry = new String[tmpLen]; // 存储全部的表示为字段
            String[] tmpEmailAry = new String[tmpLen];
            String[] tmpEmailAry_2 = new String[tmpLen];
            String[] tmpEmailAry_3 = new String[tmpLen];
            int[] tmpFolderAry = new int[tmpLen];

            int pos = 0;
            String sql = "SELECT ID, CAPTION, TYPE, EMAIL, EMAIL_2, EMAIL_3 FROM ".concat(tmpName);
            int tmpFolderID = CASUtility.getAPPNodeID(tmpAppIndex);

            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                tmpTypeAry[pos] = rs.getShort("TYPE");
                tmpIDAry[pos] = rs.getString("ID");
                tmpFileAsAry[pos] = rs.getString("CAPTION");
                tmpEmailAry[pos] = rs.getString("EMAIL");
                tmpEmailAry_2[pos] = rs.getString("EMAIL_2");
                tmpEmailAry_3[pos] = rs.getString("EMAIL_3");
                tmpFolderAry[pos] = tmpFolderID;

                pos++;
            }
            rs.close();
            rs = null;

            data.add(tmpIDAry);
            data.add(tmpFileAsAry);
            data.add(tmpTypeAry);
            data.add(tmpEmailAry);
            data.add(tmpEmailAry_2);
            data.add(tmpEmailAry_3);
            data.add(tmpFolderAry);

            // 关闭
            ps.close();
            ps = null;
            PIMDBConnecter.instance.reConnectDb();

            return data;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return null;
        }
    }

    /**
     * 返回所有的联系人中的ID字段
     * 
     * @return int[]
     */
    @Override
    public String[] getAllContactRecordId() {
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库连接
            String sql = "SELECT ID FROM CONTACT";
            Statement ps = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            String[] tmpResultAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                tmpResultAry[tmpPos] = rs.getString("ID");
                tmpPos++;
            }

            rs.close();// 关闭
            ps.close();
            rs = null;
            ps = null;
            PIMDBConnecter.instance.reConnectDb();

            return tmpResultAry;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return null;
        }
    }

    /**
     * 返回所有的联系人中的表示为字段
     * 
     * @return String[]
     */
    @Override
    public String[] getAllContactDisplayAs() {
        try {
            Connection conn = PIMDBConnecter.instance.getConnection(); // 得到数据库连接
            String sql = "SELECT SUBJECT FROM CONTACT";
            Statement ps = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            String[] tmpResultAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                tmpResultAry[tmpPos] = rs.getString(1);
                tmpPos++;
            }

            rs.close();// 关闭
            rs = null;
            ps.close();
            ps = null;
            PIMDBConnecter.instance.reConnectDb();

            return tmpResultAry;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return null;
        }
    }

    /**
     * 返回所有的联系人中的邮箱字段
     * 
     * @return String[]
     */
    @Override
    public String[][] getAllEmailAddress() {
        try {
            Connection conn = PIMDBConnecter.instance.getConnection(); // 得到数据库连接
            String sql = "SELECT EMAIL FROM CONTACT";
            Statement ps = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            String[][] tmpResultAry = new String[tmpPos][3];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                tmpResultAry[tmpPos] = new String[] { rs.getString("EMAIL") };
                tmpPos++;
            }
            rs.close();// 关闭
            ps.close();
            rs = null;
            ps = null;
            PIMDBConnecter.instance.reConnectDb();

            String[][] tmpMails = new String[tmpPos][1];
            for (int i = 0; i < tmpPos; i++) { // 将二维数组中所有的null,换为"".
                String[] tmpObjAry = tmpResultAry[i];
                for (int j = 0; j < 1; j++)
                    tmpMails[i][j] = tmpObjAry[j] == null ? CASUtility.EMPTYSTR : (String) tmpObjAry[j];
            }
            return tmpMails;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return null;
        }
    }

    /**
     * 返回当前ACCOUNT（帐号）表中符合条件的帐号记录
     * 
     * : 默认情况下.状态非以下三种时，返回ACCOUNT表中所有的帐号
     * 
     * @param prmAccountState
     * 
     *            三种状态：ModelConstants.ACCOUNT_ALL (所有帐号) ModelConstants.ACCOUNT_VALID (有效帐号)
     *            ModelConstants.ACCOUNT_INVALID (无效帐号)
     * 
     * @return 返回当前ACCOUNT（帐号）表中符合条件的帐号记录,根据param不同可以分别返回所有帐号、有效帐号、无效帐号列表
     * 
     */
    @Override
    public Vector getMailAccount(
            int prmAccountState) {
        PIMViewInfo viewInfo = new PIMViewInfo();// 设置为邮件帐号类型
        viewInfo.setAppIndex(ModelConstants2.ACCOUNT);

        // =FALSE条件设置前提：默认数据库ACCOUNT表IS_ACCOUNT_FORBID字段DEFAULT值为FALSE;
        String state =
                prmAccountState == ModelCons.ACCOUNT_VALID ? "=FALSE "
                        : (prmAccountState == ModelCons.ACCOUNT_INVALID ? "=TRUE " : null);

        String where = (state != null) ? ModelConstants2.IS_ACCOUNT_FORBID.concat(state) : null;

        return getAllRecord(viewInfo, where);
    }

    /**
     * 通过给定的PIMViewInfo得到其中的字段的索引
     *
     * @return int[] 名字的索引
     */
    @Override
    public int[] getFieldNameIndex(
            PIMViewInfo info) {
        String deli = ModelDBCons.DELIMITER;
        String fields = info.getFieldNames();
        StringTokenizer token = new StringTokenizer(fields, deli);
        int size = token.countTokens();
        int[] fieldNameIndex = new int[size];
        size = 0;
        while (token.hasMoreTokens()) {
            fieldNameIndex[size] = Integer.parseInt(token.nextToken().trim());
            size++;
        }
        return fieldNameIndex;
    }

    /**
     * 通过给定的PIMViewInfo得到其中的字段名称
     */
    @Override
    public String[] getFieldNames(
            PIMViewInfo prmViewInfo) {
        StringTokenizer tmpTokenizer = new StringTokenizer(prmViewInfo.getFieldNames(), ModelDBCons.DELIMITER);
        int tmpSize = tmpTokenizer.countTokens();
        String[] tmpIndexesAry = new String[tmpSize]; // 至此得到prmViewInfo中的字段,并存到字符串数组tmpIndexesAry中.

        tmpSize = 0; // @Note:此处先改变tmpSize的值为0,然后再恢复tmpSize的值为的仅仅
        while (tmpTokenizer.hasMoreTokens()) { // 是少定义一个临时变量"i".
            tmpIndexesAry[tmpSize] = tmpTokenizer.nextToken().trim();
            tmpSize++;
        }// 至此，tmpSize的值又恢复到原值（即Tokenizer的元素数）－－－－－－－

        Object[] tmpFieldNameAry = getFieldNameAry(prmViewInfo.getAppIndex()); // 得到prmViewInfo所描述的应用所对应的数
        // 据库表中的所有的字段名.
        for (int i = 0; i < tmpSize; i++)
            // 将tmpIndexesAry数组中每个元素用其对应的字段名代替.
            tmpIndexesAry[i] = (String) tmpFieldNameAry[Integer.parseInt(tmpIndexesAry[i])];

        return tmpIndexesAry;
    }

    /**
     * 通过给定的PIMViewInfo得到其中的字段宽度，每个字段与getFieldsFromViewInfo 得到的字段对应
     */
    @Override
    public int[] getFieldWidths(
            PIMViewInfo info) {
        String deli = ModelDBCons.DELIMITER;
        String widths = info.getFieldWidths();
        StringTokenizer token = new StringTokenizer(widths, deli);
        int size = token.countTokens();
        int[] widthArr = new int[size];
        size = 0;
        while (token.hasMoreTokens()) {
            widthArr[size] = Integer.parseInt(token.nextToken().trim());
            size++;
        }
        return widthArr;
    }

    /**
     * 从数据库中得到所有的类别名字
     */
    @Override
    public String[] getAllCategoryName() {
        String tabName = ModelDBCons.CATEGORY_TABLE_NAME;

        String sql = "SELECT * FROM ".concat(tabName);
        Vector nameVec = new Vector();
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 数据库连接

            Statement smt = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql);
            while (rs.next()) {
                nameVec.add(rs.getString(2));
            }

            // 关闭
            smt.close();
            smt = null;
            rs.close();
            rs = null;
            PIMDBConnecter.instance.reConnectDb();

            return (String[]) nameVec.toArray(new String[0]);
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 把需要加入的名字加入数据库中
     */
    @Override
    public boolean addCategroyName(
            String cate) {
        return insOrDelCateName(cate, true);
    }

    /**
     * 将指定的名字从库中删除
     */
    @Override
    public boolean deleteCategroyName(
            String cate) {
        return insOrDelCateName(cate, false);
    }

    /**
     * 将类别重置
     */
    @Override
    public boolean resetCategroyName() {
        // 类型数据库表名
        String tabName = ModelDBCons.CATEGORY_TABLE_NAME;

        String sql = "DELETE FROM ".concat(tabName);

        try {
            Connection conn = PIMDBConnecter.instance.getConnection();

            Statement smt = conn.createStatement();// 删除原来的类型表的名字
            smt.executeUpdate(sql);

            PIMDBConnecter.instance.initCategory(conn, smt);// 初始化类型

            smt.close();
            smt = null;
            PIMDBConnecter.instance.reConnectDb();

            return true;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存数据，这些数据都实现了IDbDataSource接口
     * 
     * @param prmSource
     *            需要保存的数据
     * @param event
     *            当数据发生变化是要发什么事件通知视图
     * @return boolean 保存动作是否成功
     */
    @Override
    public boolean saveDataSource(
            IDbDataSource prmSource,
            int event) {
        boolean result = insOrUpdateDataSource(prmSource, true);
        if (result) {
            // TODO:fireEvent
        }
        return result;
    }

    /**
     * 更新数据，这些数据都实现了IDbDataSource接口
     * 
     * @param source
     *            需要更新的数据
     * @param event
     *            当数据发生变化是要发什么事件通知视图
     * @return boolean 更新动作是否成功
     */
    @Override
    public boolean updateDataSource(
            IDbDataSource source,
            int event) {
        int tmpDataType = source.getDataType();
        boolean isViewInfo = tmpDataType == ModelCons.VIEW_INFO_DATA;
        PIMViewInfo oldInfo = null;
        PIMViewInfo viewInfo = null;
        if (isViewInfo) {
            viewInfo = (PIMViewInfo) source;
            // 在取到旧的VIEWINFO记录时,因为新的viewInfo记录中的INFOLDER路径可能已经修改
            // 所以这里不能通过以下的方法来得到PIMViewInfo记录对象,因为是更新viewInfo所以记录的ID号是不变的,可以通过ID号
            // 来得到旧的VIEWINFO记录
            // oldInfo = getViewInfo(viewInfo.getAppType(), viewInfo.getAppSubType(), viewInfo.getFolderPath());
            oldInfo = (PIMViewInfo) getDataSource(ModelCons.VIEW_INFO_DATA, viewInfo.getRecordID());
            // @TODO:数据库表改名,外部更新PIMViewInfo的功能有误,所以此处的功能暂时不用
            int newFolderID = ((PIMViewInfo) source).getPathID();
            if (!(newFolderID == oldInfo.getPathID())) {
                // renameTable(oldInfo.getFolderID(), newFolderID);
                updateViewInfoNodeID((PIMViewInfo) source, oldInfo);
            }
        }
        boolean result = insOrUpdateDataSource(source, false);
        if (result) {
            // TODO:fireEvent
            if (isViewInfo) {
                fireFieldChangedEvent(viewInfo.getAppIndex(), getChangedFlag(oldInfo, viewInfo));
            }
            if (tmpDataType == ModelCons.VIEW_FORMAT_DATA) {
                // TODO: 发格式化变化的事件
                fireFormatChangedEvent();
            }
        }
        return result;
    }

    /**
     * 删除数据，这些数据都实现了IDbDataSource接口
     * 
     * @param source
     *            需要删除的数据
     * @param event
     *            当数据发生变化是要发什么事件通知视图
     * @return boolean 删除动作是否成功
     */
    @Override
    public boolean deleteDataSource(
            IDbDataSource source,
            int event) {
        // 数据类型,VIEWINFO,VIEWFORMAT等
        int tmpDataType = source.getDataType();
        String tabName = PIMDBUtil.getTableName(tmpDataType);
        int id = source.getRecordID(); // 记录的ID号

        try {
            Connection conn = PIMDBConnecter.instance.getConnection();

            StringBuffer sql = new StringBuffer();// 删除DataSource的sql语句
            if (tmpDataType == ModelCons.VIEW_INFO_DATA)
            // 判断是否为viewInfo,如果为viewInfo,则根据INFOLDER,得到INFOLDER的子路径,删除相关的子文件夹的VIEWINFO记录
            {
                int tmpFolderID = ((PIMViewInfo) source).getPathID();
                sql.append("DELETE FROM ").append(tabName).append(" WHERE FOLDERID = ").append(tmpFolderID).append(";");
                // "' OR ");
                // sql.append("FOLDERID = ").append(
                // tmpFolderID.substring(0, tmpFolderID.length() - 1).concat(",")).append(
                // "%';");
            } else {
                sql.append("DELETE FROM ").append(tabName).append(" WHERE ID = ");
                sql.append(id);
            }

            Statement smt = conn.createStatement();
            int rs = smt.executeUpdate(sql.toString());

            smt.close();
            smt = null;
            PIMDBConnecter.instance.reConnectDb();

            return rs != 0;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取得需要的数据，通过数据类型和条件
     *
     * @param prmDataType
     *            数据类型，与ModelConstants中的常量对应 根据这个DataType可以取出ViewInfo或MailRule的表.
     * @param condition
     *            要取得数据需要的条件。如想取得app＝2，supApp ＝ 0的PIMViewInfo信息 应传入new int[]{2,0};
     * @called by: (will)MailRule;
     */
    @Override
    public IDbDataSource getDataSource(
            int prmDataType,
            int[] condition) {
        // prmDataType为PIMViewInfo类型,返回数据库表的名字viewinfo
        String tmpTableName = PIMDBUtil.getTableName(prmDataType);
        String tmpSql = "SELECT * FROM ".concat(tmpTableName);

        // 得到WHERE条件
        String tmpWhere = getWhereExpression(prmDataType, condition);
        tmpSql = tmpSql.concat(tmpWhere).concat(String.valueOf(';'));
        IDbDataSource tmpSource = getSourceClass(prmDataType);
        //
        if (tmpSource == null) {
            ErrorUtil.write("failed in getting IDbDataSource_1_(PIMDBModel.getDATASource())");
            // PIMDBModel中getDataSource时没有能够根据参数prmDataType（"+prmDataType+"）得到相应的IDbDataSource，只得到一个null）");
            return null;
        }
        try {
            Connection tmpConn = PIMDBConnecter.instance.getConnection();
            Statement tmpState = tmpConn.createStatement();
            // A.s("tmpSql = " + tmpSql);

            ResultSet rs = tmpState.executeQuery(tmpSql);
            ResultSetMetaData rsmd = rs.getMetaData();
            // TODO: 改善代码
            boolean b = false;
            while (rs.next()) {
                b = true;
                tmpSource.enrichFromRS(rs, rsmd);
            }
            if (!b) {
                // 初始化指定的视图的设置
                // 通过参数中的视图类型决定使用DefaultDBInfo中定义的哪一个insert语句对相应的viewinfo进行初始化
                // 构建初始化的viewinfo供返回
            }
            if (prmDataType == ModelCons.VIEW_INFO_DATA) {
                PIMViewInfo tmpInfo = (PIMViewInfo) tmpSource;
                tmpInfo.setAppIndex(condition[0]);
            } else if (prmDataType == ModelCons.VIEW_FORMAT_DATA) {
                ViewFormat tmpFormat = (ViewFormat) tmpSource;
                tmpFormat.setAppSubType(condition[0]);
                tmpFormat.setAppSubType(condition[1]);
                tmpFormat.setModeType(condition[2]);
            }
            return tmpSource;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return null;
    }

    // 得到所有的记录信息，condiation可以为空
    @Override
    public List getAllDataSource(
            int prmDataType,
            int[] prmCondition) {
        Vector allData = new Vector();
        String tmpTableName = PIMDBUtil.getTableName(prmDataType);
        String tmpSql = "SELECT * FROM ".concat(tmpTableName);
        if (prmCondition != null) {
            String tmpWhere = getWhereExpression(prmDataType, prmCondition);
            tmpSql = tmpSql.concat(tmpWhere);
        }
        tmpSql = tmpSql.concat(String.valueOf(';'));
        IDbDataSource tmpSource = getSourceClass(prmDataType);
        if (tmpSource == null) {
            return allData;
        }
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(tmpSql);
            ResultSetMetaData tmpRSMD = rs.getMetaData();
            while (rs.next()) {
                tmpSource.enrichFromRS(rs, tmpRSMD);
                allData.add(tmpSource);
            }
            return allData;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allData;
    }

    /**
     * 将邮件地址列表添加到“垃圾发件人”列表或“成人内容发件人”列表中
     * 
     * @param mailList
     *            被添加的邮件地址列表
     * @param isJunk
     *            true : 添加到“垃圾发件人”列表中 false: 添加到“成人内容发件人”列表中
     */
    @Override
    public void addToJunkList(
            String[] mailList,
            boolean isGarbage) {
        List allMailRule = getAllDataSource(ModelCons.MAIL_RULE_DATA, null);
        MailRuleContainer rule = null;
        int size = allMailRule.size();
        for (int i = 0; i < size; i++) {
            MailRuleContainer temp = (MailRuleContainer) allMailRule.get(i);
            int junkOrAdult = temp.getJunkOrAdult();
            if (junkOrAdult == 0) {
                continue;
            }
            if (isGarbage == (junkOrAdult == 1)) {
                rule = temp;
                break;
            }
        }
        // boolean isUpdate = true;
        if (rule == null) {
            String ruleName =
                    isGarbage ? IntlModelConstants.JUNK_MAIL_RULE_NAME : IntlModelConstants.ADULT_MAIL_RULE_NAME;
            rule = new MailRuleContainer(size, false, ruleName);
            // isUpdate = false;
            int junkOrAdult = isGarbage ? 1 : 2;
            rule.setJunkOrAdult(junkOrAdult);
            rule.setIsSelected(true);
            // 创建条件规则
            MailRulePool condition = new MailRulePool();
            condition.setSenderOption(0);
            StringBuffer tmpSB = new StringBuffer();
            int count = mailList.length;
            for (int i = 0; i < count - 1; i++) {
                tmpSB.append(mailList[i]).append(',');
            }
            tmpSB.append(mailList[count - 1]);
            rule.setConditionPool(condition);
            // 创建动作规则
            MailRulePool action = new MailRulePool();
            action.setMoveOption(0);
            action.setMovePlaceID(6248);
            rule.setActionPool(action);
            saveDataSource(rule, 0);
        } else {
            MailRulePool condition = rule.getConditionPool();
            String sender = condition.getSender();
            int count = mailList.length;
            for (int i = 0; i < count; i++) {
                if (sender.indexOf(mailList[i]) == -1) {
                    sender = sender.concat(",").concat(mailList[i]);
                }
            }
            condition.setSender(sender);
            rule.setConditionPool(condition);
            updateDataSource(rule, 0);
        }
    }

    /**
     * 标记邮件为已读/未读
     * 
     * @param appType
     *            被标记邮件的应用类型
     * @param ids
     *            被标记邮件的id，若为null，则标记所有邮件。
     * @param hasRead
     *            true : 标记为已读 false: 标记为未读
     */
    @Override
    public void markAsRead(
            int prmApp,
            int prmFolder,
            int[] prmIDs,
            boolean prmReaded,
            int icon,
            boolean isRefresh) {
        // 得到记录所在的表的名字
        String tabName = CustOpts.custOps.APPNameVec.get(prmApp);
        int len = prmIDs.length;
        StringBuffer sql = new StringBuffer();

        sql.append("UPDATE ").append(tabName).append(" SET ").append(TmpConstants.READED).append('=').append(prmReaded)
                .append(',').append(ModelConstants2.ICON).append('=').append(icon).append(" WHERE ID IN (");
        sql.append(prmIDs[0]); // 特殊处理第一个
        for (int i = 1; i < len; i++) {
            sql.append(',').append(prmIDs[i]);
        }
        sql.append(");");

        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            Statement smt = conn.createStatement();
            smt.executeUpdate(sql.toString());

            // 刷新视图
            if (isRefresh) {
                firePIMModelDataUpdated(prmApp);
            }

            smt.close();
            smt = null;

            PIMDBConnecter.instance.reConnectDb();
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
        }
    }

    /**
     * 导出数据到一个文件 Called by:
     */
    @Override
    public void exportInfo() {
    }

    /**
	 */
    @Override
    public void importInfo() {
    }

    /**
     * 得到给定表名的数据库表的下一个id值
     *
     * @param tableName
     *            给定的数据库的表名
     * @return int 下一个id的值
     */
    @Override
    public int getAppNextID(
            int app) {
        String tableName = CustOpts.custOps.APPNameVec.get(app);
        String sql = "select max (ID)+1 from ".concat(tableName);
        int ID = -1;
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            rs.next();
            ID = rs.getInt(1);
            st.close();
            conn.close();
        } catch (SQLException se) {
            // se.printStackTrace();
        }
        return ID;
    }

    /**
     * 此方法得到发件箱中的所有记录而不用viewInfo中提供的过虑条件,因为在发送的时候所有的邮件都要发送
     */
    @Override
    public Vector getOutboxRecords() {
        return getAppRecords(ModelCons.OUTBOX_APP);
    }

    /**
     * 通过起始和结束时间搜索一个约会记录
     */
    @Override
    public PIMRecord[] getDayRecords(
            Date beginDate,
            int prmFolderID) {
        PIMRecord[] records = null;
        Vector vec = new Vector();
        String tmpTableName = CustOpts.custOps.APPNameVec.get(0);

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ").append(tmpTableName);
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            Date _date = null;
            Statement smt = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql.toString());
            while (rs.next()) {
                _date = rs.getTimestamp(ModelDBCons.CALENDAR_BEGIN_TIME + 1);
                // A.s(" day == " + _date.getDate() + " day_2 == " + beginDate.getDate());
                // A.s(" month == " + _date.getMonth() + " month_2 == " + beginDate.getMonth()
                // + "\n");

                if (_date.getDate() == beginDate.getDate() && _date.getMonth() == beginDate.getMonth() + 1) {
                    // Connection conn, int appType, int recId, String prmTableName, String prmInfolder)
                    vec.add(selectRecord(conn, ModelCons.CALENDAR_APP, rs.getInt("ID"), tmpTableName));
                }
            }
            if (vec != null && vec.size() > 0) {
                int size = vec.size();
                records = new PIMRecord[size];
                for (int idx = 0; idx < size; idx++) {
                    records[idx] = (PIMRecord) vec.get(idx);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * 设置视图的缺省值
     * 
     * @param: prmAppType 需要恢复缺省值的视图的类型索引
     * @param: prmSubAppType 需要恢复缺省值的视图的子类型索引
     * 
     *         操作过程为删除原来的ViewInfo记录,重新插入viewInfo记录
     */
    @Override
    public void resetViewInfo(
            int prmAppType,
            int prmSubAppType,
            int prmFolderID) {
        int index = -1;// 当前的App和SubApp的组合在DefaultDBInfo.VIEWINFO_KEY中的索引值
        String key = String.valueOf(prmAppType).concat(String.valueOf(prmSubAppType));

        int length = Utility.VIEWINFO_KEY.length;// 得到索引值
        for (int i = 0; i < length; i++)
            if (Utility.VIEWINFO_KEY[i].equals(key)) {
                index = i;
                break;
            }

        if (index == -1)
            return;// 如果不存在则返回

        PIMViewInfo oldViewInfo = getViewInfo(prmFolderID); // 删除旧的ViewInfo
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库连接
            Statement smt = conn.createStatement();// 删除旧的VIEWINFO表中记录
            String sql = "DELETE FROM VIEWINFO WHERE ID =".concat(String.valueOf(oldViewInfo.getRecordID()));
            smt.executeUpdate(sql);

            sql = ModelConstants2.INIT_DB_CONSTANTS[index];// 插入默认的记录
            smt.executeUpdate(sql);

            smt.close();
            smt = null;
            PIMDBConnecter.instance.reConnectDb();
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return;
        }
        // 发送VIEWINFO修改事件
        fireFieldChangedEvent(prmAppType, PIMModelEvent.RESET_VIEWINFO);
    }

    /**
     * 得到指定的表中未读记录的数目
     * 
     * @param: prmAppType 指定应用的索引
     * @param: prmFolderPath 取得哪个文件夹下的新记录的条数
     * @NOTE:目前来说,已删除项无法建立子文件夹,所以getDeletedNewItemCount()中不需要传参数
     */
    @Override
    public int getNewItemCount(
            int prmAppType,
            int prmFolderID) {
        if (prmAppType < 0 || prmAppType > ModelConstants2.ACCOUNT) {
            return 0;
        }

        // 已删除项做特殊处理,已删除项(App = 7, INFOLDER = [PIM, 咨询管理, 已删除项])
        if (prmAppType == ModelCons.DELETED_ITEM_APP) {
            return getDeletedNewItemCount();
        }
        // 判断当前的表的名字是否存在
        String tmpTabName = CustOpts.custOps.APPNameVec.get(prmAppType);
        if (tmpTabName == null) {
            ErrorUtil.write("PIMDBModel.getNewItem()--found no table match the prm.");
            return 0;
        }

        // 异常则返回-1;
        int tmpCount = 0;
        try {
            // 得到数据库连接
            Connection tmpConn = PIMDBConnecter.instance.getConnection();
            if (tmpConn == null) {
                ErrorUtil.write("PIMDBModel.getNewItem()--failed when connecting the DB.");
                return tmpCount;
            }

            Statement smt = tmpConn.createStatement();
            // 查询所有的记录,判断
            String tmpSQL = "SELECT COUNT(*) FROM ".concat(tmpTabName).concat(" WHERE READED != TRUE;");
            ResultSet tmpRS = smt.executeQuery(tmpSQL);
            if (tmpRS.next()) {
                tmpCount = tmpRS.getInt(1);
            }
            tmpRS.close();
            tmpRS = null;
            smt.close();
            smt = null;

            PIMDBConnecter.instance.reConnectDb();
            return tmpCount;
        } catch (Exception e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return tmpCount;
        }
    }

    /**
     * 得到视图绘制时需要的ViewFormat
     * 
     * @param: prmAppType 选择的应用类型
     * @param: prmSupAppType 选择的应用子类型
     * @param: prmModeType 选择的样式类型，随应用的不同而不同，如在邮件中有已读、未读、垃圾等
     * @param: prmFolderPath 此viewForat所在的文件夹选项
     * 
     * @return: ViewFormat 保存Format的类,暂时只有font的信息
     */
    @Override
    public ViewFormat getViewFormat(
            int prmAppType,
            int prmSubAppType,
            int prmModeType,
            int prmFolderID) {
        ViewFormat format =
                (ViewFormat) getDataSource(ModelCons.VIEW_FORMAT_DATA, new int[] { prmAppType, prmSubAppType,
                        prmModeType });
        if (format == null) {
            ErrorUtil.write("failed in getting the action of format(PIMDBModel.getViewFormat())");
            // 在PIMDBModel中getViewFormat方法中取得format的动作失败"));
            format = new ViewFormat();
            format.setAppType(prmAppType);
            format.setAppSubType(prmSubAppType);
            format.setModeType(prmModeType);
            format.setInfolder(prmFolderID);
            format.setFontColor(java.awt.Color.black);
            format.setFontSize(9);
            format.setFontStyle(0);
            format.setFontName("宋体");
        }
        return format;
    }

    /**
     * 更新ViewFormat的信息
     * 
     * @param prmFormat
     *            需要更新的ViewFormat
     * @return boolean 更新是否成功
     */
    @Override
    public boolean updateViewFormat(
            ViewFormat prmFormat) {
        return updateDataSource(prmFormat, 0);
    }

    /**
     * 将一条新的ViewInfo放入数据库,当新建文件夹的时候需要 目前是createTable，@TODO: 以后改成创建一个View。如果View能够提高性能的话。
     * 
     * @param: prmViewInfo 需要添加的viewinfo
     * @return: boolean 是否添加成功
     */
    @Override
    public boolean addViewInfo(
            PIMViewInfo prmViewInfo) {
        int tmpFolderID = prmViewInfo.getPathID(); // 根据路径得到数据库表的名字--------------------------
        if (tmpFolderID <= 0) // 判断当前的FOLDER是否合法
        {
            ErrorUtil.write("illegal ViewInfo, addViewInfo action failed!");
            return false;
        }

        return saveDataSource(prmViewInfo, 0);
    }

    /**
     * 将指定的viewinfo删除
     *
     * @param: prmViewInfo 需要删除的viewinfo
     * @return: 是否删除成功
     */
    @Override
    public boolean deleteViewInfo(
            PIMViewInfo prmViewInfo) {
        deleteFolder(prmViewInfo.getPathID(), prmViewInfo.getAppIndex());
        return deleteDataSource(prmViewInfo, 0);
    }

    // TODO:
    /**
     * 取得联系人记录中是否是通讯组列表的字段,组成数组返回,除了是删除项的不取,其他的全取
     * 
     * @return int[] 是否是通讯组的字段值组成的数组
     * 
     * @NOTE:此方法要修改,现在联系人可能有多张数据库表CONTACT_1,CONTACT_2, 所以原来标识一个通讯组成员的,ID连接的字段,要修改格式, 此方法可能要废弃,重写方法.
     */
    @Override
    public int[] getAllCommGroupAttrList() {
        String sql = "SELECT TYPE FROM Contact"; // 查询语句
        try {
            Connection conn = PIMDBConnecter.instance.getConnection(); // 数据库连接
            Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // 建立状态集
            ResultSet rs = smt.executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            Object[] data = new Object[rs.getRow()];
            rs.beforeFirst();
            int r = 0;
            while (rs.next()) {
                data[r] = PIMPool.pool.getKey(rs.getShort("TYPE"));
                r++;
            }

            rs.close();
            smt.close();
            rs = null;
            smt = null;
            PIMDBConnecter.instance.reConnectDb(); // 关闭

            int[] flags = new int[data.length];
            for (int i = 0; i < data.length; i++)
                flags[i] = ((Integer) data[i]).intValue();

            return flags;
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    /**
     * 根据相关的联系人得到其相关的证书
     * 
     * @param 邮件地址列表
     * @return 每个邮件地址对应的证书
     */
    @Override
    public Vector getCertificate(
            Vector prmEmailAddress) {
        // 判断邮件地址是否为空
        if (prmEmailAddress == null || prmEmailAddress.size() < 1) {
            return null;
        }

        // 取到所有的联系人的记录
        Vector cerVec = getAllCertificate();
        Vector resultCer = new Vector();
        // 建立邮件地址影射，邮件地址对应证书在cerVec中的索引值

        java.util.HashMap mailMap = new java.util.HashMap();

        for (int i = cerVec.size() - 1; i >= 0; i--) {
            PIMRecord rec = (PIMRecord) cerVec.get(i);
            String tmpMail;
            for (int j = 1; j <= 3; j++) {
                tmpMail = (String) rec.getFieldValues().get(new Integer(j));
                if (tmpMail != null) {
                    // 如果邮件地址相同则会覆盖以前相同的邮件地址
                    mailMap.put(tmpMail, new int[] { i, j - 1 });
                }
            }
        }

        int size = prmEmailAddress.size();// 检索证书
        for (int i = 0; i < size; i++) {
            // 得到邮件地址
            String tmpAddr = (String) prmEmailAddress.get(i);
            // 进行邮件地址匹配
            int[] index = (int[]) mailMap.get(tmpAddr);
            if (index == null) // 当前的邮件地址在联系表中不存在
            {
                return null;
            }
            // 判断为第几个电子邮件
            PIMRecord rec = (PIMRecord) cerVec.get(index[0]);
            ArrayList tmpCer =
                    (ArrayList) PIMDBUtil.decodeByteArrayToSerializeObject((byte[]) rec.getFieldValue(PIMPool.pool
                            .getKey(4)));

            // 得到证书
            int arrayIndex = Integer.parseInt(((ArrayList) tmpCer.get(3)).get(index[1]).toString());
            resultCer.add(((ArrayList) tmpCer.get(arrayIndex)).get(arrayIndex));
        }
        return resultCer;
    }

    /**
     * 得到所有的证书,和私钥, 如果证书或者私钥为null,则返回null
     * 
     * @param prmMailAddress
     *            为用户帐号的邮件地址
     * @return Vector vector中第一个元素为私钥,第二个元素为证书,如果证书或者私钥为null,则返回null
     */
    @Override
    public Vector getPrivateKeyAndCer(
            String prmMailAddress) {
        if (prmMailAddress == null || prmMailAddress.length() < 1)
            return null; // 判断邮件地址是否为空

        String tmpTableName = ModelDBCons.ACCOUNT_TABLE_NAME; // 得到帐表的名字

        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库的连接

            StringBuffer tmpSql = new StringBuffer(); // 查找证书的SQL语句,查询其中的邮件地址和相关的证书(CERTIFICATE)和私钥
            tmpSql.append("SELECT PRIVATE_KEY, CERTIFICATE FROM ").append(tmpTableName);
            tmpSql.append(" WHERE ").append("SMTPEMAILADDRESS = '").append(prmMailAddress.trim()).append('\'');

            Statement pStmt = conn.createStatement();// 预备数据库查询状态
            ResultSet rs = pStmt.executeQuery(tmpSql.toString());

            Vector tmpCer = null;// 结果集，所有的私钥和相关的证书
            if (rs.next()) {
                tmpCer = new Vector();
                for (int i = 1; i <= 2; i++)// 得到私钥和证书
                {
                    ArrayList list = (ArrayList) PIMDBUtil.decodeByteArrayToSerializeObject(rs.getBytes(i));
                    if (list == null || list.size() < 1) {
                        return null;
                    }
                    tmpCer.add(list);
                }
            }
            rs.close();
            rs = null;
            pStmt.close();
            pStmt = null;
            PIMDBConnecter.instance.reConnectDb();

            return tmpCer;
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return null;
    }

    /**
     * 得到所有的证书,和私钥
     * 
     * @param prmMailAddress
     *            为用户帐号的邮件地址
     * @return Vector vector中第一个元素为私钥,第二个元素为证书,如果证书或者私钥为null,则返回null
     */
    @Override
    public Vector getSingalPrivateKeyAndCer(
            String prmMailAddress) {
        if (prmMailAddress == null || prmMailAddress.length() < 1)// 判断邮件地址是否为空
            return null;

        String tmpTableName = ModelDBCons.ACCOUNT_TABLE_NAME;// 得到帐表的名字

        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库的连接
            if (conn == null)// 判断连接为空，尝试再次连接
                return null;

            // 查找证书的SQL语句,查询其中的邮件地址和相关的证书(CERTIFICATE)和私钥
            StringBuffer tmpSql = new StringBuffer();
            tmpSql.append("SELECT PRIVATE_KEY, CERTIFICATE, CER_SIGN_POS FROM ").append(tmpTableName);
            tmpSql.append(" WHERE ").append("SMTPEMAILADDRESS = '").append(prmMailAddress.trim()).append('\'');

            Statement pStmt = conn.createStatement();// 预备数据库查询状态
            ResultSet rs = pStmt.executeQuery(tmpSql.toString());

            Vector tmpCer = null;// 结果集，所有的私钥和相关的证书
            if (rs.next()) {
                String posStr = rs.getString("CER_SIGN_POS");
                int pos = -1;
                if (posStr != null && (pos = Integer.valueOf(posStr).intValue()) != -1) {
                    tmpCer = new Vector();
                    for (int i = 1; i <= 2; i++)// 得到私钥和证书
                    {
                        ArrayList list = (ArrayList) PIMDBUtil.decodeByteArrayToSerializeObject(rs.getBytes(i));
                        if (list != null) {
                            tmpCer.add(list.get(pos));
                        }
                    }
                }
            }
            rs.close();
            rs = null;
            pStmt.close();
            pStmt = null;
            PIMDBConnecter.instance.reConnectDb();
            return tmpCer;
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isConnectedWell() {
        return !(getConection() == null);
    }

    /*
     * (non-Javadoc) 此方法用于在需要对model释放内存的时候调用。主要是当db在连接的状态下会有 很多被占用的内存不能释放，所以再次要关闭db连接，并将此方法中用于保存 临时变量的Hashtab等结构清理干净
     * @see Releasable#release()
     */
    @Override
    public void release() {
        PIMDBConnecter.instance.reConnectDb();
        ThreadActionsFacade.getInstance().resetMemoryTask(1000 * 5);
    }

    // --------------------------------------------------------------------------------------------------//
    // 以上为PIMMODEL中最常使用的方法
    // --------------------------------------------------------------------------------------------------//

    // 此方法返回联系人的相关信息字段：ID、CAPTION、EMAIL、EMAIL_2、EMAIL_3, TYPE字段，为QuickInfoField使用
    public Object[][] getContactsForQuickInfo() {
        String[] tmpFields = { "ID", "CAPTION", "EMAIL", "EMAIL_2", "EMAIL_3", "TYPE" };
        String tmpTableName = "CONTACT";

        Object[][] tmpValues =
                selectFieldsToArray(tmpFields, tmpTableName, null, PIMDBUtil.converTableNameToFolder(tmpTableName));// TODO:这里的作为潜在字段的foderPath应该改为folderID.

        return Utility.covertObjectArrayByLatitude(tmpValues);// Utility.reduceDirection(tmpValues));
    }

    /**
     * : 以下为调试数据库的方法
     */
    void printlnTableNames() {
        StringBuffer names = new StringBuffer();
        Collections.sort(tableNames); // 对表的名字进行排序
        for (int size = tableNames.size(), i = 0; i < size; i++) {
            names.append(tableNames.get(i));
            names.append("\r\n");
        }
        System.out.println("数据库所有表名:" + names.toString());
    }

    /**
     * 打印出表中所有的记录,每个字段对应的键值
     * 
     * @NOTE: 测试方法
     * 
     * @param tableName
     *            数据库表的名字
     * @return Vector
     * 
     *         private Vector printTableContent(String tableName) { try { Vector tmpVect = new Vector(); Connection conn
     *         = PIMDBConnecter.instance.getConnection();//得到数据库连接 if (conn == null) return null; if (tableName == null)
     *         //判断数据库表名为空则返回null return null;
     * 
     *         StringBuffer sql = new StringBuffer();//SQL语句 sql.append("SELECT * FROM ").append(tableName); Statement
     *         stmt = conn.createStatement();//ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
     * 
     *         ResultSet rs = stmt.executeQuery(sql.toString()); ResultSetMetaData rsmd = rs.getMetaData(); int
     *         columnCount = rsmd.getColumnCount();
     * 
     *         //rs.afterLast();//because the rs returned by HSQLDB is a TYPE_FORWARD_ONLY one. //rs.relative(-1); //int
     *         rowCount = rs.getRow(); //计算当前的结果集中记录的数目 //rs.beforeFirst(); //定位结果集值第一行 //A.s("记录数目："+rowCount);
     * 
     *         Hashtable hash = null;
     * 
     *         StringBuffer word = new StringBuffer();
     * 
     *         while (rs.next()) { PIMRecord tmpRecord = new PIMRecord(); int tmpId = rs.getInt(1);
     *         tmpRecord.setRecordID(tmpId); hash = createRecordHash(rs, rsmd, 1, columnCount); if (hash != null) {
     *         tmpRecord.setFieldValues(hash); Hashtable t = tmpRecord.getFieldValues(); java.util.Enumeration e =
     *         t.keys(); while (e.hasMoreElements()) { Integer key = (Integer) e.nextElement(); Object o =
     *         (Object)t.get(key); A.s("key : " + String.valueOf(key) + " |Value : " + String.valueOf(o));
     *         word.append("key: ").append(key).append(" value: ").append(String.valueOf(o)); } tmpVect.add(tmpRecord);
     *         } } try { PrintWriter pout = new PrintWriter( new BufferedWriter(new FileWriter("c:\\VIEWINFO.txt")));
     *         pout.write(word.toString()); pout.close(); } catch (IOException ioe){ioe.printStackTrace();}
     * 
     *         return tmpVect; } catch (SQLException e) { PIMDBConnecter.instance.reConnectDb(); } return null; }
     */

    void insertColumn() {
        try {
            Connection con = PIMDBConnecter.instance.getConnection();

            if (con == null) {
                throw new IllegalArgumentException("连接数据库失败");
            }

            //
            // ALTER TABLE <tablename> ADD COLUMN <columnname> Datatype [(columnSize[,precision])] [DEFAULT
            // 'defaultValue' [NOT NULL]] [BEFORE <existingcolumn>];
            // 在数据库中插入一列
            String insertSql = "ALTER TABLE ACCOUNT ADD COLUMN IS_ACCOUNT_FORBID BIT DEFAULT FALSE";

            Statement smt = con.createStatement();
            smt.executeUpdate(insertSql);

            // UPDATE table SET column = Expression [, ...] ;

            String updateSql = "UPDATE ACCOUNT SET IS_ACCOUNT_FORBID = FALSE";
            smt.executeUpdate(updateSql);

            smt.close();
            smt = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void printTableStructor(
            String prmTableName) {

        try {
            // 得到数据库的连接
            Connection conn = PIMDBConnecter.instance.getConnection();
            if (conn == null) {
                return;
            }

            // NOTE把"MAIL_123"这样的数据库表的名字转化为"MAIL"后再得到其PIMDBTable的实例，因为"MAIL_123"和"MAIL"表的结构是一样的,要过滤
            String tmpTableName = PIMDBUtil.getMainTableName(prmTableName);
            // 数据库状态,注意在SQL中"0 = 1",得到数据库的查找记录数目为0
            String sql = "SELECT * FROM ".concat(tmpTableName).concat(" WHERE 0 = 1");
            // 得到数据库表的结构
            ResultSet rs = conn.createStatement().executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            int count = rd.getColumnCount();

            StringBuffer word = new StringBuffer();
            for (int i = 0; i < count; i++) {
                word.append(i).append(":   ").append(rd.getColumnName(i + 1)).append("\r\n");
            }

            try {

                PrintWriter pout =
                        new PrintWriter(new BufferedWriter(new FileWriter("c:\\".concat(prmTableName).concat(
                                "_TABLE_FIELD.txt"))));
                pout.write(word.toString());
                pout.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // 关闭结果集合
            rs.close();
            rs = null;

            // 关闭数据库连接
            PIMDBConnecter.instance.reConnectDb();
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
        }
    }

    /**
     * 此方法返回被删除的记录，从各个表中取一次
     *
     * @param: prmViewInfo 取得应用的viewinfo
     * @param: isVector 返回的值中用的是vector还是Record
     * @param prmSql
     *            数据库查找的SQL语句
     * @return: Vector 取得已经删除的记录。如果isVector是true，返回的Vector中的元素还是Vector，如果isVector是false，返回的Vector中的元素是Record
     */
    protected Vector getDeletedRecords(
            PIMViewInfo prmViewInfo,
            String prmSql) {
        int tmpApp = prmViewInfo.getAppIndex();

        Vector recordVects = new Vector(); // 准备最终将被返回的用于存放结果集的Vector.
        try {
            // 准备连接
            Connection tmpConn = PIMDBConnecter.instance.getConnection();
            if (tmpConn == null) {
                return null;
            }

            // 准备SQL语句
            Statement stmt = tmpConn.createStatement();

            Vector tableNameStr = CustOpts.custOps.APPNameVec;
            int len = tableNameStr.size();
            for (int i = 0; i < len; i++) {
                // 得到所有的回收站表的名字
                String tmpTableName = ((String) tableNameStr.get(i)).concat("_RECYCLE");
                String sql = createSelectSQLByViewInfo(prmViewInfo, tmpTableName, prmSql);

                // 查找的结果集
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int tmpColCount = rsmd.getColumnCount();

                while (rs.next()) {
                    PIMRecord tmpRd = new PIMRecord();
                    tmpRd.setAppIndex(tmpApp);
                    tmpRd.setRecordID(rs.getInt(1));
                    tmpRd.setInfolderID(rs.getInt(tmpColCount - 1)); // 这个INFOLDER字段是重数据库库中取出来的，标识源数据所在的INFOLDER路径
                    tmpRd.setDeleted(true);
                    // 设置删除标志为true,标识当前的记录为已删除项中的记录
                    // 最后的两个字段"INFOLDER"和"OLDID"不放到hash中,而是作为PIMRecord的属性,
                    tmpRd.setFieldValues(createRecordHash(rs, rsmd, 1, tmpColCount));
                    recordVects.addElement(tmpRd);
                }
                rs.close();
                rs = null;
            }
            stmt.close();
            stmt = null;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        PIMDBConnecter.instance.reConnectDb();
        return recordVects;
    }

    /**
     * 返回所有的记录
     * 
     * @param PIMViewInfo
     *            viewInfo
     * @return Vector
     */
    protected Vector initSingleAppRecords(
            PIMViewInfo viewInfo,
            String prmWhereSql) {
        // 得到数据库表的名字
        int tmpAppType = viewInfo.getAppIndex();
        String tmpTableName = getTableName(tmpAppType);
        if (tmpTableName == null) {
            return null;
        }
        String sql = createSelectSQLByViewInfo(viewInfo, tmpTableName, prmWhereSql);

        Vector recordVects = new Vector();
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData tmpRSMD = rs.getMetaData();
            int columnCount = tmpRSMD.getColumnCount();

            boolean delApp = tmpAppType == ModelCons.DELETED_ITEM_APP;
            while (rs.next()) {
                Vector tmpSingle = getRecordVector(rs, tmpRSMD, columnCount);
                // 注意:添加INFOLDER为了在已删除项中选择记录时使用
                if (delApp)
                    tmpSingle.add(rs.getString(ModelConstants2.INFOLDER));
                tmpSingle.add(new Integer(tmpAppType)); // 设置记录的App类型
                recordVects.addElement(tmpSingle);
            }

            // 关闭结果集合
            rs.close();
            rs = null;

            stmt.close();
            stmt = null;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        PIMDBConnecter.instance.reConnectDb();
        return recordVects;
    }

    private Object[] getFieldNameAry(
            int prmType) {
        String tmpAppName = CustOpts.custOps.APPNameVec.get(prmType);
        return MainPane.getApp(tmpAppName).getAppTexts();
    }

    /**
     * 得到数据库表的名字, 此处得到的数据库的表的名字不包括已删除项的表的名字,已删除项的表的名字对应到了删除前的表的名字
     * 
     * @NOTE 已删除项的VIEWINFO的FOLDER路径为【PIM, 资讯管理, 已删除项】，无法对应至表的名字
     * @param prmRecord
     *            记录,已删除项的记录的INFOLDER字段为删除记录前的INFOLDER字段
     */
    private String getTableName(
            PIMRecord prmRecord) {
        return getTableName(prmRecord.getAppIndex());
    }

    /**
     * 根据应用类型和对应的INFOLDER路径得到数据库表的名字
     * 
     * 应用类型为：
     * 
     * 日历－－－－－－－－－0 ModelConstants.TASK_APP 任务－－－－－－－－－1 ModelConstants.CALENDAR_APP 联系人－－－－－－－－2
     * ModelConstants.CONTACT_APP 日记－－－－－－－－－3 ModelConstants.DIARY_APP 收件箱－－－－－－－－4 ModelConstants.INBOX_APP
     * 发件箱－－－－－－－－5 ModelConstants.OUTBOX_APP 已发送邮件－－－－－－6 ModelConstants.SENDED_APP 删除项－－－－－－－－7
     * ModelConstants.DELETED_ITEM_APP 通讯组列表－－－－－－8 视图信息－－－－－－－9 邮件帐户－－－－－－－10
     * 
     * @see emo.pim.pimutil.ModelCons
     * 
     * @NOTE: 已删除项 prmApp = 7, 路径为记录删除前的路径例如"[PIM, 资讯管理, 收件箱]"
     * 
     * @param prmApp
     *            应用类型
     * @param prmFolder
     *            导航树的路径
     * 
     * @return 数据库表的名字
     * 
     */
    private String getTableName(
            int prmApp) {
        if (prmApp < 0)// 判断为VIEWINFO ACCOUNT等表的类型，直接返回数据库表的名字
            return ModelConstants2.SYSTEMTABLE_NAME_LIST[-1 * prmApp];
        return CustOpts.custOps.APPNameVec.get(prmApp);// 转换INFOLDER到数据库表的名字对已删除项做特殊的处理
    }

    /*
     * @NOTE:此方法没有发送事件，代替了以前的public boolean insertRecord(PIMRecord prmRecord) 方法。
     * @NOTE:此方法中插入记录后没有关闭连接。
     * @NOTE: 参数prmRecord一般情况下是没有ID号的,需要本方法计算一个新的ID号赋予它,但是有时 候prmRecord又是有ID值的,比如插入日记记录时以当日日期作为ID值,这时候不需要再计算新的ID值
     * 赋给prmRecord,为稳健起见,验证一下这个记录库中是否已经存在,如果存在(这种情况不应该存在),则写入错误日志.(use008)
     * @NOTE: 局限性:这种做法使本系统只能"一日一记",不能"一日多记". 插入记录的时候,可以把日记插入到已删除项中,这个需要做特殊的处理
     * app为ModelConstants.DELETE_ITEM_APP(已删除项的APP类型), 而INFOLDER为删除前的INFOLDER路径
     * 例如:插入联系人记录到已删除一般为插入副本,则App类型为"CONTACT_APP",INFOLDER为[PIM, 咨询管理, 联系人];
     */
    private boolean insertRecord(
            Connection prmConn,
            PIMRecord prmRecord) throws SQLException {
        Hashtable tmpValues = prmRecord.getFieldValues(); // 得到数据库的字段以及对应值
        if (tmpValues == null || tmpValues.size() < 1)
            return false;

        String tmpTabname = getTableName(prmRecord); // 根据FOLDER字段得到数据库表的名字
        if (tmpTabname == null || tmpTabname.length() < 1)
            return false;

        String sql = getInsertPreparedSql(tmpTabname, prmRecord); // 准备数据库插入记录的预编译语句
        PreparedStatement psmt = prmConn.prepareStatement(sql); // 建立数据库的预编译状态
        processPreparedStatement(psmt, prmRecord, tmpTabname, prmConn);
        int rows = psmt.executeUpdate(); // 执行插入记录的操作

        final String IDENTITY = "{CALL IDENTITY()}"; // 用于插入记录时，返回数据库表中当前插入的记录的ID号
        ResultSet rs = prmConn.createStatement().executeQuery(IDENTITY);// 执行获取插入当前记录的ID号的值
        int identity = rs.next() ? rs.getInt(1) : -1; // 得到记录的ID号，并设置当前插入记录的ID号
        prmRecord.setRecordID(identity);

        rs.close(); // 关闭结果集
        psmt.close(); // 关闭@NOTE:没有关闭连接。
        return rows != 0 && identity != -1;
    }

    /*
     * 取准备在当前表中插入记录的预编译的SQL语句 note: 在生成的预编译的SQL语句中对于ID值做了特殊的处理，除了Diary(日记)表外所有的表的 ID值由数据库统一分配，且根据数据库表ID最大值自动加以1且不重复。
     * 日记表的ID格式为用户设定格式为：<year><month><date>,设定为日记记录时当前的系统日期转化的格式 例如：2004-12-06ID值表示为20041206
     * 其它表的ID值在生成的SQL语句中默认设置为NULL，DB在检测到ID值设置为NULL后，会自动分配其ID值 要取数据库表中当前插入记录的ID值，只要调用存储过程{call identity()}即可获取
     * @param prmTableName 数据库表的名字
     * @param prmRecord 待插入的记录
     * @return 在当前表中插入记录的预编译的SQL语句
     */
    private String getInsertPreparedSql(
            String prmTableName,
            PIMRecord prmRecord) {
        prmRecord.getFieldValues().remove(PIMPool.pool.getKey(ModelDBCons.ID));// 则删除其ID字段，以免在处理状态集是出错

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ").append(prmTableName);
        sql.append("(ID ");
        int tmpKeyCount = 0;
        String[] tmpFieldNameAry = tableStructurePool.getFieldNameArray(prmTableName);
        Enumeration tmpEnum = prmRecord.getFieldValues().keys();
        while (tmpEnum.hasMoreElements()) {
            int tmpKey = ((Integer) tmpEnum.nextElement()).intValue();
            tmpKeyCount++;
            sql.append(',');
            sql.append(tmpFieldNameAry[tmpKey]);
        }

        sql.append(")VALUES(");
        sql.append("NULL");// 每条记录的首个字段,规定为ID.
        for (int i = tmpKeyCount; i > 0; i--) {
            sql.append(", ");
            sql.append('?');
        }

        sql.append(CASUtility.RIGHT_BRACKET);
        return sql.toString();
    }

    /*
     * 处理预编译的SQL语句,对设置其值,注意设置其值时，要判断其类型，故要获得此类型 原来每次操作数据库时，都要求先获得数据库表的结构，根据表的字段的多少会实例化相同数目的PIMField对象，很浪费内存。
     * 现采用对数据库表的结构的缓存方式，缓存用到的数据库表的结构，可在此缓存中取到数据库表的结构类型，提升数据库性能
     * 注：<表结构缓存只保存对应的字段值和字段类型,其中字段值为String类型的，在DeafultDBInfo接口类中已经设定为 public static final，故其实创建此缓存时，只增加了使用到的表的字段类型的数组对象>
     * @param prmPrepareStmt 数据库预编译状态
     * @param prmRec 待插入的记录
     * @param tableName 数据库表的名字
     * @throws SQLException 操作数据库捕捉到SQLException异常值，则抛出此异常
     */
    private void processPreparedStatement(
            PreparedStatement prmPrepareStmt,
            PIMRecord prmRec,
            String prmTableName,
            Connection prmConn) throws SQLException {
        Hashtable tmpValues = prmRec.getFieldValues();
        int[] tmpTypes = tableStructurePool.getFieldTypes(prmTableName, prmConn);

        Enumeration tmpKeyEnum = tmpValues.keys();
        int tmpPoint = 1; // 此处ID已经占用了一个0，所以重1开始
        while (tmpKeyEnum != null && tmpKeyEnum.hasMoreElements()) {
            Object tmpKey = tmpKeyEnum.nextElement();
            int tmpType = tmpTypes[((Integer) tmpKey).intValue()];
            setObjectInType(prmPrepareStmt, tmpPoint++, tmpValues.get(tmpKey), tmpType);
        }
    }

    /*
     * 注意PIM数据库表字段类型中，有两种类型要特别处理的， 一种是java.sql.Date类型，view中传入的PIMRecord的日类型都是java.util.Date类型的要转换为java.sql.Date类型，
     * 还有一种用户传递的是String类型，但是对应的数据类型为Types.BINARY类型，所以要转化为byte[]类型 Internal setObject implementation. <p>
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the object containing the input parameter value
     * @param prmType the SQL type (as defined in java.sql.Types) to be sent to the database
     * @throws SQLException if a database access error occurs
     */
    private void setObjectInType(
            PreparedStatement psmt,
            int prmParaIdx,
            Object prmValue,
            int prmType) throws SQLException {
        if (prmValue == null) // 要判断是否要设置其值，是否要返回，状态集时候要设置为null
            return;

        switch (prmType) {
            case Types.BIT:
                psmt.setBoolean(prmParaIdx, ((Boolean) prmValue).booleanValue());
                break;
            case Types.TINYINT:
                psmt.setByte(prmParaIdx, ((Number) prmValue).byteValue());
                break;
            case Types.SMALLINT:
                psmt.setShort(prmParaIdx, ((Number) prmValue).shortValue());
                break;
            case Types.INTEGER:
                psmt.setInt(prmParaIdx, ((Number) prmValue).intValue());
                break;
            case Types.VARCHAR:
                psmt.setString(prmParaIdx, (String) prmValue);
                break;
            case Types.TIMESTAMP:
                if (prmValue == CASUtility.EMPTYSTR)
                    psmt.setTimestamp(prmParaIdx, null);
                else
                    psmt.setTimestamp(prmParaIdx, new Timestamp(((Date) prmValue).getTime()));
                break;
            case Types.BINARY:
                psmt.setBytes(prmParaIdx,
                        prmValue instanceof String ? PIMDBUtil.encodeImageToByteArray((String) prmValue)
                                : (byte[]) prmValue);
                break;
            case Types.OTHER:
                psmt.setBytes(prmParaIdx, PIMDBUtil.encodeSerializeObjectToByteArray(prmValue));
                break;
            default:
                psmt.setString(prmParaIdx, prmValue.toString());
                break;
        }
    }

    /*
     * @NOTE: 此方法没有发送事件，将代替 public boolean updateRecord(PIMRecord prmRecord) 方法。
     * 准备数据库更新记录的预编译语句时不用考虑ID的情况，对于更新数据库记录操作时，把ID也设置值，更新没有问题 原因：例如原记录的ID号为123,则在更新时，再次把ID设置为123,不会出错，HSQL数据库支持此操作。
     * @param prmConn 数据库连接
     * @param prmRecord 待更新的记录
     * @return 更新成功则返回ture，否则返回false
     * @throws SQLException
     */
    private boolean updateRecord(
            Connection prmConn,
            PIMRecord prmRecord) throws SQLException {
        Hashtable fieldValues = prmRecord.getFieldValues(); // 得到数据库的字段以及对应值
        if (fieldValues == null || fieldValues.size() < 1)
            return false;

        String tabname = getTableName(prmRecord); // 根据FOLDER字段得到数据库表的名字
        if (tabname == null || tabname.length() < 1)
            return false;

        String sql = getUpdatePreparedSql(tabname, prmRecord); // 准备数据库更新记录的预编译语句
        PreparedStatement psmt = prmConn.prepareStatement(sql); // 建立数据库的预编译状态
        processPreparedStatement(psmt, prmRecord, tabname, prmConn);
        int updateRows = psmt.executeUpdate(); // 执行更新记录的操作

        psmt.close(); // 关闭
        psmt = null;

        return updateRows != 0;
    }

    /*
     * 取更新的数据库记录操作的预编译语句，根据记录的字段数目生成要更新的预编译语句 : 更新操作的预编译SQL语句如下格式： update <tablename> set <fieldname1> = ?, <fieldname2>
     * = ?, <fieldname3> = ?,... where id = recordId;
     * @param prmTableName 待更新的数据库表的名字
     * @param prmRecord 待更新的数据库记录
     * @return 返回更新的数据库记录操作的预编译语句
     */
    private String getUpdatePreparedSql(
            String prmTableName,
            PIMRecord prmRecord) {
        final String updateSql = "UPDATE ";
        StringBuffer sql = new StringBuffer(); // 更新数据库表操作的SQL语句
        String rootName = PIMDBUtil.getMainTableName(prmTableName);

        String[] fieldNames = tableStructurePool.getFieldNameArray(rootName); // 得到根节点的表的表名

        sql.append(updateSql).append(prmTableName).append(" SET ");
        Enumeration tmpEnum = prmRecord.getFieldValues().keys();

        int fieldIndex = -1;
        if (tmpEnum.hasMoreElements()) {
            fieldIndex = ((Integer) tmpEnum.nextElement()).intValue();
            sql.append(fieldNames[fieldIndex]).append("=?");
        }
        while (tmpEnum.hasMoreElements()) {
            fieldIndex = ((Integer) tmpEnum.nextElement()).intValue();
            sql.append(',').append(fieldNames[fieldIndex]).append("=?");
        }
        sql.append(" WHERE ID = ").append(prmRecord.getRecordID()).append(";");
        return sql.toString();
    }

    /*
     * @NOTE:此方法针对App类型大于7的类型的数据的删除,彻底删除数据库表中的记录
     * @param prmRecord 待删除的数据库记录
     * @return 删除成功则返回ture，否则返回false
     */
    private boolean dropRecord(
            PIMRecord prmRecord) {
        try { // 检查记录是否存在
            Connection conn = PIMDBConnecter.instance.getConnection(); // 连接数据库
            String tabName = getTableName(prmRecord); // 得到数据库表的名字
            if (tabName == null)
                return false;
            boolean deleteRs = deleteRecord(conn, prmRecord, tabName);
            PIMDBConnecter.instance.reConnectDb(); // 关闭连接
            return deleteRs;
        } catch (SQLException se) {
            se.printStackTrace();
            PIMDBConnecter.instance.reConnectDb();
            return false;
        }
    }

    /**
     * @NOTE:此方法针对App类型大于7的类型的数据的删除,彻底删除数据库表中的记录
     * 
     * @param prmRecords
     *            待删除的数据库记录列表
     * @return 删除成功则返回ture，否则返回false
     */
    private boolean dropRecords(
            List prmRecords) {
        // 检查记录是否存在
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 连接数据库

            String tabName = getTableName((PIMRecord) prmRecords.get(0));// 得到数据库表的名字
            if (tabName == null) {
                return false;
            }

            boolean deleteRs = deleteRecords(conn, prmRecords, tabName);

            PIMDBConnecter.instance.reConnectDb();// 关闭连接

            return deleteRs;
        } catch (SQLException se) {
            se.printStackTrace();
            PIMDBConnecter.instance.reConnectDb();
            return false;
        }
    }

    /**
     * 此方法执行后将彻底删除数据库中的这条记录, 彻底删除数据库记录的SQL语句类型格式： delete from <tablename> while id = recordId;
     * 
     * @param conn
     *            数据库连接
     * @param prmRecord
     *            待删除的数据库表的记录
     * @param prmTableName
     *            待删除的记录所在的数据库表的名字
     * @return 删除成功则返回ture，否则返回false
     * @throws SQLException
     *             删除时遇到SQLException则抛出异常
     */
    private boolean deleteRecord(
            Connection conn,
            PIMRecord prmRecord,
            String prmTableName) throws SQLException {
        // 得到记录的ID号
        int recId = prmRecord.getRecordID();

        // 数据库删除语句
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM ").append(prmTableName).append(" WHERE ID = ").append(recId).append(';');

        // 执行数据库删除语句
        Statement state = conn.createStatement();
        int rs = state.executeUpdate(sql.toString());

        // 关闭
        state.close();
        state = null;

        return rs != 0;
    }

    /**
     * 此方法执行后将彻底删除数据库中的这条记录,彻底删除数据库记录的SQL语句类型格式： note : 只支持删除在同一张表中的记录
     * 
     * delete from <tablename> while <fieldname = 'id'> in (id1, id2, id3,...);
     * 
     * @param conn
     *            数据库连接
     * @param prmRecord
     *            待删除的数据库表的记录列表
     * @param prmTableName
     *            待删除的记录所在的数据库表的名字
     * @return 删除成功则返回ture，否则返回false
     * @throws SQLException
     *             删除时遇到SQLException则抛出异常
     */
    private boolean deleteRecords(
            Connection conn,
            List prmRecord,
            String prmTableName) throws SQLException {

        // 得到记录的ID号
        StringBuffer ids = new StringBuffer();
        int size = prmRecord.size();
        if (size > 0) {
            ids.append(((PIMRecord) prmRecord.get(0)).getRecordID());
        }
        for (int i = 1; i < size; i++) {
            ids.append(", ").append(((PIMRecord) prmRecord.get(i)).getRecordID());
        }

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM ").append(prmTableName);
        sql.append(" WHERE ID IN (").append(ids.toString()).append(')');

        Statement state = conn.createStatement();
        int rs = state.executeUpdate(sql.toString());

        // 关闭
        state.close();
        state = null;

        return rs != 0;
    }

    /**
     * 记录删除后，删除此记录原来所在的表中的当前记录,注意删除原表中记录时，要得到两个基本信息 记录的ID号和记录的INFOLDER信息，在传入的记录数组中，每个记录都有OLDID字段和INFOLDER字段
     * 即对应预原记录的ID号和INFOLDER信息
     * 
     * @param prmDeleteRecords
     *            已经在回收站表中的记录
     * @param prmTableName
     *            记录所在的原来的表名字
     * @return 删除成功则返回true，否则返回false
     */
    private boolean deleteSourceRecords(
            PIMRecord[] prmDeleteRecords,
            String prmTableName) {
        try {
            Connection con = PIMDBConnecter.instance.getConnection();
            // 得到记录的ID号
            StringBuffer ids = new StringBuffer();
            int size = prmDeleteRecords.length;
            if (size > 0) {
                ids.append(prmDeleteRecords[0].getFieldValue(ModelDBCons.OLDID));
            }
            for (int i = 1; i < size; i++) {
                ids.append(", ").append(prmDeleteRecords[i].getFieldValue(ModelDBCons.OLDID));
            }
            StringBuffer sql = new StringBuffer();
            sql.append("DELETE FROM ").append(prmTableName);
            sql.append(" WHERE ID IN (").append(ids.toString()).append(')');
            Statement state = con.createStatement();
            int rs = state.executeUpdate(sql.toString());
            // 关闭
            state.close();
            state = null;
            return rs != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @note: 插入记录至回收站表，回收站表的ID号为DB自动分配的，其表的和对应原表的字段只增加了两个字段OLDID和INFOLDER
     *        字段，OLDID字段用来标识回收站表中记录在原表中的ID号，当恢复此记录时，要用到此信息，把此记录恢复到原表，并分配其原来的ID值
     * 
     *        INFODLER字段为标识这个记录由那一张数据库表移动过来的
     * 
     * @note: 大问题，如果出现其它的数据库表的名字改变了，怎么办？ 提出一个解决方案：可以数据库改名的操作记录到具体的一张表中，以便收缩数据库表名更改的记录
     * 
     */
    private boolean insertRecordToRecycleBin(
            Connection conn,
            String prmRecycleTableName,
            String prmRootTableName,
            PIMRecord prmRecord) throws SQLException {
        // 备份原记录的ID号和INFOLDER字段
        int recId = prmRecord.getRecordID();
        int tmpFolderID = prmRecord.getInfolderID();

        // 判断插入的记录是否插入到回收站中,如果插入到回收站中则使用插入回收站的SQL语句
        String preparedSql = null;
        preparedSql = getInsertToRecycleBinSql(prmRecycleTableName, prmRootTableName, prmRecord);

        // 处理插入的数据库语句
        PreparedStatement psmt = conn.prepareStatement(preparedSql);
        processPreparedStatement(psmt, prmRecord, prmRootTableName, conn);
        int updateRows = psmt.executeUpdate();

        final String IDENTITY = "{CALL IDENTITY()}"; // 用于插入记录时，返回数据库表中当前插入的记录的ID号
        ResultSet rs = psmt.executeQuery(IDENTITY); // 执行获取插入当前记录的ID号的值

        // 得到记录的ID号，并设置当前插入记录的ID号
        int identity = rs.next() ? rs.getInt(1) : -1;
        Hashtable values = prmRecord.getFieldValues();

        values.put(PIMPool.pool.getKey(ModelDBCons.OLDID), PIMPool.pool.getKey(recId)); // 设置OLDID字段
        values.put(PIMPool.pool.getKey(ModelCons.folderIDIdx), PIMPool.pool.getKey(tmpFolderID)); // 设置INFOLDER字段
        prmRecord.setRecordID(identity);
        prmRecord.setAppIndex(ModelCons.DELETED_ITEM_APP); // 设置记录的类型为已删除项类型
        rs.close();
        rs = null;

        // 关闭
        psmt.close();
        psmt = null;

        return updateRows != 0 && identity != -1;
    }

    /**
     * 为插入的记录准备预编译的SQL语句，此处要特殊处理ID、OLDID、INFOLDER字段 ODLID = record.getRecordID() INFODLER =
     * record.getInfolder()，这两个字段可由待插入回收站的记录得到，OLDID字段用来标识回收站表中记录在原表中的ID号，
     * 当恢复此记录时，要用到此信息，把此记录恢复到原表，并分配其原来的ID值.INFODLER字段为标识这个记录由那一张数据库表移动过来的
     */
    private String getInsertToRecycleBinSql(
            String prmRecycleTableName,
            String prmRootTableName,
            PIMRecord prmRecord) {
        // 设置回收站表的旧的ID号和INFOLDER字段,OLDID为恢复时,设置当前记录在源表中的ID号,INFOLDER映射记录待恢复的数据库表
        prmRecord.getFieldValues().remove(PIMPool.pool.getKey(ModelDBCons.ID));
        StringBuffer sql = new StringBuffer();
        int keyCount = 0;
        sql.append("INSERT INTO ").append(prmRecycleTableName).append("(ID, OLDID, INFOLDER");
        String[] fieldNames = tableStructurePool.getFieldNameArray(prmRootTableName);

        int oldId = prmRecord.getRecordID();
        int tmpFolderID = prmRecord.getInfolderID();
        Enumeration tmpEnum = prmRecord.getFieldValues().keys();
        while (tmpEnum.hasMoreElements()) {
            int key = ((Integer) tmpEnum.nextElement()).intValue();
            keyCount++;
            sql.append(',');
            sql.append(fieldNames[key]);
        }
        sql.append(")VALUES(NULL, ").append(oldId).append(',').append(tmpFolderID);

        for (int i = keyCount; i > 0; i--) {
            sql.append(',');
            sql.append('?');
        }
        sql.append(CASUtility.RIGHT_BRACKET);
        return sql.toString();
    }

    /** 取当前的记录在已删除项中的对应的记录 */
    private Vector getRecycleRecords(
            List prmRecords) {
        try {
            PIMRecord firstRd = (PIMRecord) prmRecords.get(0);
            int tmpApp = firstRd.getAppIndex(); // 得到记录的类型
            int tmpFolderID = firstRd.getInfolderID(); // 得到记录所在的路径

            // 建立SQL的ID IN (id1, id2, id3, ...)字符串
            StringBuffer ids = new StringBuffer();
            ids.append(firstRd.getRecordID());
            for (int size = prmRecords.size(), i = 1; i < size; i++) {
                ids.append(',').append(((PIMRecord) prmRecords.get(i)).getRecordID());
            }

            String tableName = PIMDBUtil.getRecycleTableName(tmpApp); // 得到回收站的表的名字

            // 建立查询已删除项的记录
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM ");
            sql.append(tableName).append(" WHERE ");
            sql.append("OLDID IN (").append(ids).append(") AND "); // 旧记录的ID号
            sql.append("INFOLDER = ").append(tmpFolderID).append(";");

            // 得到数据库连接
            Connection conn = PIMDBConnecter.instance.getConnection();
            if (conn == null) {
                return null;
            }

            // 查询记录
            Vector tmpRds = getRecordsVec(conn, ModelCons.DELETED_ITEM_APP, tableName, tmpFolderID, sql.toString());
            PIMDBConnecter.instance.reConnectDb();

            return tmpRds;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return null;
        }
    }

    /**
     * 恢复回收站中的记录,如果恢复失败则不删除当前的记录,继续恢复下一条记录
     * 
     * 注意这里的记录都是在回收站中的记录
     */
    private boolean restoreFromRecycleBin(
            List prmRecords,
            boolean prmIsRefresh) {
        PIMRecord tmpRd = null; // 临时变量
        int size = prmRecords.size(); //
        int count = 0;

        for (int i = 0; i < size; i++) {
            tmpRd = (PIMRecord) prmRecords.get(i);
            PIMRecord restoreRd = (PIMRecord) tmpRd.clone(); // @TODO：有性能问题，需要处理
            if (restoreRecord(tmpRd)) {
                permanentlyDeleteRecord(restoreRd, false, false); // 彻底删除记录
                count++;
            }
        }
        if (prmIsRefresh && size > 0) {
            firePIMModelDataInserted(tmpRd.getAppIndex()); // 目前刷新的为恢复目标应用的视图
        }
        return size == count; // 没有记录则返回ture
    }

    /**
     * 移动数据记录,把一条记录移动到另张表中
     */
    private boolean restoreRecord(
            PIMRecord prmRecord) {
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            // 根据FOLDER字段得到待恢复的数据库表的名字
            String targetTableName = CustOpts.custOps.APPNameVec.get(prmRecord.getAppIndex());
            if (targetTableName == null || targetTableName.length() < 1) {
                return false;
            }
            String rootName = PIMDBUtil.getMainTableName(targetTableName);
            Hashtable values = prmRecord.getFieldValues();
            Integer tmpOldId = (Integer) values.get(PIMPool.pool.getKey(ModelDBCons.OLDID));
            // @NOTE:此处的记录恢复以后,记录的ID号为原来的ID号为oldId
            prmRecord.setRecordID(tmpOldId.intValue()); // 设置记录的ID号
            prmRecord.setAppIndex(PIMDBUtil.getAppTableIndex(rootName));
            // 设置记录的类型
            values.put(PIMPool.pool.getKey(ModelDBCons.ID), tmpOldId); // 覆盖设置记录的ID字段
            values.remove(PIMPool.pool.getKey(ModelDBCons.OLDID));
            // 移除INFOLDER字段信息
            String preparedSql = getRestoreRecycleSql(prmRecord, targetTableName);
            // 处理插入的数据库语句
            PreparedStatement prepareStmt = conn.prepareStatement(preparedSql);
            processPreparedStatement(prepareStmt, prmRecord, rootName, conn);
            int updateRows = prepareStmt.executeUpdate();
            // 设置记录恢复后的类型
            // 关闭
            prepareStmt.close();
            PIMDBConnecter.instance.reConnectDb();
            return updateRows != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            PIMDBConnecter.instance.reConnectDb();
            return false;
        }

    }

    private String getRestoreRecycleSql(
            PIMRecord prmRecord,
            String targetTableName) {
        StringBuffer sql = new StringBuffer();
        int keyCount = 0;
        sql.append("INSERT INTO ").append(targetTableName).append('(');

        String[] fieldNames = tableStructurePool.getFieldNameArray(PIMDBUtil.getMainTableName(targetTableName));
        Enumeration tmpEnum = prmRecord.getFieldValues().keys();
        if (tmpEnum.hasMoreElements()) {
            int key = ((Integer) tmpEnum.nextElement()).intValue();
            sql.append(fieldNames[key]);
        }
        while (tmpEnum.hasMoreElements()) {
            int key = ((Integer) tmpEnum.nextElement()).intValue();
            keyCount++;
            sql.append(',');
            sql.append(fieldNames[key]);
        }
        sql.append(")VALUES(?");
        for (int i = keyCount; i > 0; i--) {
            sql.append(',');
            sql.append('?');
        }
        sql.append(CASUtility.RIGHT_BRACKET);
        return sql.toString();
    }

    /*
     * 目前已删除项没有对应真实的数据库表，所以通过VIEWINFO的FIELDNAME()字段来取要显示的字段时 建立要查询的字段的SELECT <field, field, ...> FROM，此处的field
     * 为视图显示和内部使用的字段 以上的'field'字段为共有字段，即在各个表中都存在以上的字段【已删除项中显示的字段都是共有字段】 note: 已删除项中显示的如果不是共有字段查询时会抛出 SQLException 异常
     * 额外字段：添加查找INFOLDER字段,INFOLDER字段标识当前已经删除的记录删除前对应的文件夹路径
     * @param prmViewInfo 视图信息
     * @return 已删除项中所有的记录
     */
    private Object[][] getDeletedRecordsAry(
            PIMViewInfo prmViewInfo) {
        try {
            // 准备连接
            Connection tmpConn = PIMDBConnecter.instance.getConnection();
            if (tmpConn == null) {
                return null;
            }

            // 建立要查询的字段的SELECT <field, field, ...> FROM，此处的field 为视图显示和内部使用的字段
            int type = ModelCons.INBOX_APP; // 【已删除项中显示的字段都是共有字段】,所以这里赋值任何一个类型都可
            String fields = Utility.getTableField(type, prmViewInfo.getFieldNames());

            StringBuffer whereStr = new StringBuffer();
            String filter = prmViewInfo.getFilterString();
            String sortInfo = prmViewInfo.getSortString();

            if (filter != null) {
                whereStr.append(" WHERE");
                whereStr.append(" (").append(filter).append(')');
            }
            if (sortInfo != null) {
                whereStr.append(" ORDER BY ").append(sortInfo);
            }

            // 准备SQL语句
            Statement stmt = tmpConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            StringBuffer sql = new StringBuffer();

            Vector tableNameStr = CustOpts.custOps.APPNameVec;
            int len = tableNameStr.size();
            Object[][][] valueAry = new Object[len][][];

            int resultAryLength = 0;
            for (int i = 0; i < len; i++) {
                // 得到所有的回收站表的名字
                String tmpTableName = ((String) tableNameStr.get(i)).concat("_RECYCLE");

                sql.append("SELECT ").append(fields);

                // 添加查找INFOLDER字段,INFOLDER字段标识当前已经删除的记录删除前对应的文件夹路径
                sql.append(",").append(ModelConstants2.INFOLDER).append(" AS INFOLDER_");
                sql.append(" FROM ").append(tmpTableName);
                sql.append(whereStr);

                tmpTableName = PIMDBUtil.getMainTableName(tmpTableName);
                Integer apptype = PIMPool.pool.getKey(PIMDBUtil.getAppTableIndex(tmpTableName));

                // 查找的结果集
                ResultSet rs = stmt.executeQuery(sql.toString());
                valueAry[i] = getRecAryFromResultSet(rs, apptype, -1); // 在最后添加一个 apptype应用类型
                resultAryLength += valueAry[i].length;
                sql.setLength(0);
            }

            Object[][] resultAry = new Object[resultAryLength][];
            for (int pos = 0, i = 0; i < len; i++) {
                int l = valueAry[i].length;
                if (l < 1) {
                    continue;
                }

                System.arraycopy(valueAry[i], 0, resultAry, pos, l);
                pos += l;
            }

            stmt.close();
            stmt = null;

            PIMDBConnecter.instance.reConnectDb();

            return resultAry;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            PIMDBConnecter.instance.reConnectDb(); // 关闭数据库连接
            return null;
        }

    }

    /*
     * 取记录时，根据视图中【过滤】信息, 生成过滤信息条件
     * @param viewInfo 视图对象
     * @return 注意出异常时返回null，否则返回过滤、排序信息条件的SQL语句 以下为除了取显示的字段外，每个应用额外要取的字段 ModelConstants.INBOX_APP
     * ModelDBConstants.FOLOWUPENDTIME ModelDBConstants.READED ModelConstants.CONTACT_APP
     * ModelDBConstants.FOLOWUPENDTIME ModelDBConstants.READED ModelConstants.TASK_APP ModelDBConstants.END_TIME
     * ModelDBConstants.FINISH_FLAG ModelDBConstants.READED ModelConstants.CALENDAR_APP
     * ModelDBConstants.CALENDAR_END_TIME
     */
    private String createViewInfoQueryString(
            PIMViewInfo viewInfo) {
        String tmpTableName = getTableName(viewInfo.getAppIndex()); // 得到数据库表的名字,MODIFY:原来根据TYPE得到数据库表名,现根据INFOLDER字段得到数据库表的名字
        if (tmpTableName == null) {
            ErrorUtil.write("Sorry, model found no table name match the path inputted");
            return null;
        }// 对数据表名的有效性判断结束----------------------------------------

        String tmpFieldNameStr = Utility.getTableField(viewInfo.getAppIndex(), viewInfo.getFieldNames());
        if (tmpFieldNameStr == null)
            tmpFieldNameStr = "*";
        // NOTE："AS FOLOWUPENDTIME_"为表示如果在显示的字段中已经存在额外要取的字段时，把额外的字段改名为不重名，以免数据库操作异常

        StringBuffer s = new StringBuffer(tmpFieldNameStr);
        // s.append(',').append(ModelConstants2.FOLOWUPENDTIME).append(" AS FOLOWUPENDTIME_");
        // s.append(',').append(TmpConstants.READED).append(" AS READED_");
        // s.append(',').append(DefaultDBInfo.TYPE).append(" AS TYPE_");
        // s.append(',').append(DefaultDBInfo.END_TIME).append(" AS TIME_");
        // s.append(',').append(ModelDBConstants.CALENDAR_END_TIME).append(" AS END_TIME_");

        // 得到数据库表中所有的记录的SQL查询语句
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ").append(s.toString()).append(" FROM ").append(tmpTableName);

        String filterString = viewInfo.getFilterString();// 过滤条件字符串
        if (filterString != null)
            sb.append(" WHERE (").append(filterString).append(')');
        else
            sb.append(" WHERE (FOLDERID = ").append(viewInfo.getPathID()).append(" and DELETED != true)");

        String sortInfo = viewInfo.getSortString();// 排序条件字符串
        if (sortInfo != null) {
            sb.append(" ORDER BY ").append(sortInfo);
        }

        return sb.append(';').toString();
    }

    /*
     * @param prmExtObj:额外的数据,好像有时候会让二维数组中带一列额外的数据,如ID等.
     */
    private Object[][] getRecAryFromResultSet(
            ResultSet prmRs,
            Object prmExtObj,
            int prmColCount) throws SQLException {
        prmRs.afterLast();
        prmRs.relative(-1);
        int rowCount = prmRs.getRow(); // 计算当前的结果集中记录的数目
        prmRs.beforeFirst(); // 定位结果集值第一行

        ResultSetMetaData rsmd = prmRs.getMetaData();
        int colCount = prmColCount > 0 ? prmColCount : rsmd.getColumnCount();

        boolean nonNullExpand = (prmExtObj != null);
        if (nonNullExpand) {
            colCount++;
        }

        Object[][] values = new Object[rowCount][colCount];
        int i = 0;
        while (prmRs.next()) {
            values[i] = getRecordArray(prmRs, rsmd, colCount);
            if (nonNullExpand)
                values[i][colCount - 1] = prmExtObj;
            i++;
        }
        return values;
    }

    private Object[] getRecordArray(
            ResultSet rs,
            ResultSetMetaData rsmd,
            int prmColCount) throws SQLException {
        Object[] values = new Object[prmColCount];

        int colCount = rsmd.getColumnCount();
        for (int i = 1; i <= colCount; i++) // 处理结果集中第一行其它的所有值【除了ID外的字段】
        {
            int type = rsmd.getColumnType(i);

            Object tmpValue = null;

            switch (type) {
                case Types.BINARY:
                    tmpValue = rs.getBytes(i);
                    break;

                case Types.TINYINT:
                    byte byteV = rs.getByte(i);
                    tmpValue = byteV != 0 ? new Byte(byteV) : DEFAULT_BYTE_OBJECT; // 郁闷的数字,HSQL结果集的getObject(i)返回TINYINT类型时,返回的不是Byte对象,而是Integer对象
                    break;

                case Types.SMALLINT:
                    short shortV = rs.getShort(i);
                    tmpValue = shortV != 0 ? new Short(shortV) : DEFAULT_SHORT_OBJECT; // 郁闷的数字,HSQL结果集的getObject(i)返回SMALLINT类型时,返回的不是Byte对象,而是Integer对象
                    break;

                case Types.OTHER:
                    byte[] other = rs.getBytes(i);
                    if (other != null) {
                        tmpValue = PIMDBUtil.decodeByteArrayToSerializeObject(other);
                    }
                    break;

                default:
                    tmpValue = rs.getObject(i); // 注意根据类型直接返回对应java对象的有INTEGER、BIT、DATE、VARCHAR(返回java.sql.TimeStamp类型，为java.util.Date的子类型)
            }
            values[i - 1] = tmpValue;
        }

        return values;
    }

    // 选择记录
    private PIMRecord selectRecord(
            Connection conn,
            int appType,
            int recId,
            String prmTableName) throws SQLException {
        PIMRecord tmpRecord = new PIMRecord(); // 实例化记录
        tmpRecord.setRecordID(recId);
        tmpRecord.setAppIndex(appType);

        if (prmTableName == null)
            return null;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ").append(prmTableName).append(" WHERE ID = ").append(recId);

        Statement smt = conn.createStatement();// 执行查找
        ResultSet rs = smt.executeQuery(sql.toString());
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnCount = rsmd.getColumnCount();// 得到数据库表的列数
        Hashtable tmpHash = null;
        if (rs.next())
            tmpHash = createRecordHash(rs, rsmd, 1, columnCount);

        if (tmpHash != null)
            tmpRecord.setFieldValues(tmpHash);

        return tmpRecord;
    }

    /**
     * SQL：select * from <tablename> where id in (id1, id2, id3, id4, ...) ...
     */
    private String createSelectPrepareSql(
            String tableName,
            int[] ids,
            String where) {
        StringBuffer sql = new StringBuffer();
        StringBuffer idr = new StringBuffer();

        idr.append(ids[0]);
        for (int size = ids.length, i = 1; i < size; i++) {
            idr.append(',').append(ids[i]);
        }

        sql.append("SELECT * FROM ");
        sql.append(tableName).append(" WHERE ");
        sql.append("ID IN (").append(idr).append(")").append(where == null ? ";" : " AND ".concat(where));
        return sql.toString();
    }

    // 选择记录，返回的列表中存放的是PIMRecord对象
    private Vector getRecordsVec(
            Connection conn,
            int appType,
            String prmTableName,
            int prmInfolderID,
            String sql) throws SQLException {
        Statement smt = conn.createStatement();
        ResultSet rs = smt.executeQuery(sql);

        Vector resultVects = new Vector();
        int[] types = tableStructurePool.getFieldTypes(PIMDBUtil.getMainTableName(prmTableName), conn);
        int endColumn = types.length;
        final String old_id_field = "OLDID";
        if (appType == ModelCons.DELETED_ITEM_APP) {
            while (rs.next()) {
                // 实例化记录
                int recId = rs.getInt(1);
                PIMRecord tmpRecord = new PIMRecord();
                Hashtable tmpHash = createRecordHash(rs, types, 1, endColumn);
                tmpRecord.setFieldValues(tmpHash);
                tmpRecord.setRecordID(recId);
                tmpRecord.setAppIndex(ModelCons.DELETED_ITEM_APP);
                tmpRecord.setInfolderID(rs.getInt(ModelConstants2.INFOLDER));
                tmpHash.put(PIMPool.pool.getKey(ModelDBCons.OLDID), PIMPool.pool.getKey(rs.getInt(old_id_field)));
                resultVects.add(tmpRecord);
            }
        } else {
            while (rs.next()) {
                // 实例化记录
                int recId = rs.getInt(1);
                PIMRecord tmpRecord = new PIMRecord();
                Hashtable tmpHash = createRecordHash(rs, types, 1, endColumn);
                tmpRecord.setFieldValues(tmpHash);
                tmpRecord.setRecordID(recId);
                tmpRecord.setAppIndex(appType);
                tmpRecord.setInfolderID(prmInfolderID);
                resultVects.add(tmpRecord);
            }
        }
        smt.close();

        return resultVects;
    }

    private String createSelectSQLByViewInfo(
            PIMViewInfo viewInfo,
            String prmTableName,
            String prmSql) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ").append(prmTableName);
        boolean notNull = (prmSql != null && prmSql.length() > 0);
        if (notNull) {
            sql.append(" WHERE ").append(prmSql);
        }
        String filter = viewInfo.getFilterString();
        if (filter != null) {
            sql.append(notNull ? " AND" : " WHERE");
            sql.append(" (").append(filter).append(')');
        }
        String sortCriteria = viewInfo.getSortString();
        if (sortCriteria != null) {
            sql.append(" ORDER BY ").append(sortCriteria);
        }
        sql.append(';');
        return sql.toString();
    }

    /**
     * 从结果集中取到值，添加到列表中 NOTE: 对于默认值为取字符串
     * 
     * 处理的SQL类型
     * 
     * Types.VARCHAR Types.BINARY Types.OTHER Types.TIMESTAMP
     *
     * @param data
     *            结构列表
     * @param rs
     *            查询结果集
     * @param rsmd
     *            结果集元数据
     * @param columnCount
     *            列的数目
     * @throws SQLException
     *             执行时捕捉到异常则抛出
     */
    private Vector getRecordVector(
            ResultSet rs,
            ResultSetMetaData rsmd,
            int columnCount) throws SQLException {
        Vector values = new Vector(columnCount);
        // 处理结果集中第一行其它的所有值【除了ID外的字段】
        for (int i = 1; i <= columnCount; i++) {
            int type = rsmd.getColumnType(i);

            Object tmpValue = null;

            switch (type) {

                case Types.BINARY:
                    tmpValue = rs.getBytes(i);
                    break;

                case Types.TINYINT:
                    byte byteV = rs.getByte(i);
                    tmpValue = byteV != 0 ? new Byte(byteV) : DEFAULT_BYTE_OBJECT; // 郁闷的数字,HSQL结果集的getObject(i)返回TINYINT类型时,返回的不是Byte对象,而是Integer对象
                    break;

                case Types.SMALLINT:
                    short shortV = rs.getShort(i);
                    tmpValue = shortV != 0 ? new Short(shortV) : DEFAULT_SHORT_OBJECT; // 郁闷的数字,HSQL结果集的getObject(i)返回SMALLINT类型时,返回的不是Byte对象,而是Integer对象
                    break;

                case Types.OTHER:
                    byte[] other = rs.getBytes(i);
                    if (other != null) {
                        tmpValue = PIMDBUtil.decodeByteArrayToSerializeObject(other);
                    }
                    break;

                default:
                    tmpValue = rs.getObject(i); // 注意根据类型直接返回对应java对象的有INTEGER、BIT、DATE、VARCHAR(返回java.sql.TimeStamp类型，为java.util.Date的子类型)
            }
            values.add(i - 1, tmpValue);
        }

        return values;
    }

    // --------------------------------------------------------------------------------------------------//
    // 以上为PIMMODEL中最常使用的方法
    // --------------------------------------------------------------------------------------------------//

    private Object[][] selectFieldsToArray(
            String[] prmFields,
            String prmTableName,
            String prmCondition,
            Object prmEntendedObejct) {
        Object[][] selectResults = null;
        try {
            Connection conn = PIMDBConnecter.instance.getConnection(); // 得到数据库连接
            Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // 建立可以滚动的结果集
            ResultSet rst = smt.executeQuery(createSelectedSQL(prmFields, prmTableName, prmCondition));
            selectResults = getRecAryFromResultSet(rst, prmEntendedObejct, prmFields.length); // 从结果集中返回查找的对象数组

            smt.close();
            smt = null;
            rst.close();
            rst = null;
        } catch (SQLException se) {
            se.printStackTrace();
        }
        PIMDBConnecter.instance.reConnectDb(); // 关闭数据库连接
        return selectResults;
    }

    private String createSelectedSQL(
            String[] prmFields,
            String prmTableName,
            String prmWhereCondition) {
        int tmpLen = (prmFields == null ? 0 : prmFields.length);
        StringBuffer tmpSB = new StringBuffer();
        tmpSB.append(tmpLen == 0 ? PIMDBUtil.ASTERISK : prmFields[0]);
        for (int i = 1; i < tmpLen; i++)
            tmpSB.append(PIMDBUtil.COMMA).append(prmFields[i]);

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ").append(tmpSB.toString()).append(" FROM ").append(prmTableName);
        sql.append(prmWhereCondition == null ? ";" : " WHERE ".concat(prmWhereCondition));
        return sql.toString();
    }

    // /* 要插入的记录是否已经重复
    // * @TODO:应注明在什么情况下会有必须调用本方法的必要。
    // */
    // private boolean haveRepeatMail(PIMRecord prmRecord) throws SQLException{
    // Object tmpSizeProp = prmRecord.getFieldValue(ModelDBCons.SIZE);
    // int tmpOriSize = 0;
    // if(tmpSizeProp != null)
    // tmpOriSize = ((Integer) tmpSizeProp).intValue();//至此得到参数记录的尺寸属性（int值表示）
    // Object tmpSendPerson = prmRecord.getFieldValue(ModelDBCons.ADDRESSER);
    // Object sendTime = prmRecord.getFieldValue(MailDefaultViews.SENDTIME);
    //
    // String tmpTableName = ModelDBCons.INBOX_TABLE_NAME;
    // StringBuffer tmpSB = new StringBuffer();
    // tmpSB.append("SELECT SIZE,ADDRESSER,SENDTIME FROM ").append(tmpTableName);//构建搜索出表中所有尺寸和发信人，时间的字段的SQL
    //
    // Statement stmt = PIMDBConnecter.instance.getConnection().createStatement();
    // ResultSet rs = stmt.executeQuery(tmpSB.toString());
    // while (rs.next())//遍历结果集中的每一条记录，并检查
    // {
    // int rSize = rs.getInt(1);
    // String rSender = rs.getString(2);
    // Object time = rs.getTimestamp(3);
    // Timestamp rTime = null;
    // if(time != null)
    // rTime = (Timestamp) time;
    //
    // boolean isSameSize = (tmpOriSize == rSize);
    //
    // boolean isSameSender = (rSender == null && tmpSendPerson == null)
    // || (rSender != null && tmpSendPerson != null && rSender.equals(tmpSendPerson));
    // boolean isSameSendTime = (rTime == null && sendTime == null)
    // || (rTime != null && sendTime != null && rTime.getTime() == ((Date) sendTime).getTime());
    //
    // if(isSameSize && isSameSender && isSameSendTime){
    // rs.close();
    // rs = null;
    // stmt.close();
    // stmt = null;
    // return true;
    // }
    // }
    // rs.close();
    // rs = null;
    // stmt.close();
    // stmt = null;
    // return false;
    // }

    /**
     * 得到刷新视图的类型
     */
    private int getChangedFlag(
            PIMViewInfo oldInfo,
            PIMViewInfo newInfo) {
        // 空则返回,返回为更新指定视图的显示字段
        if (oldInfo == null || newInfo == null) {
            return PIMModelEvent.UPDATE_FIELD;
        }

        // 得到视图的名字
        String old = oldInfo.getViewName();
        String current = newInfo.getViewName();
        if (old != null && current != null && !(old.equals(current))) {
            return PIMModelEvent.FIELD_CHANGED; // 添加或者删除了字段，即：字段产生了变化
        }

        return PIMModelEvent.UPDATE_FIELD;
    }

    /**
     * 使用的SQL类型：INTEGER、SMALLINT、VARCHAR、BINARY、DATE、TINYINT、BIT
     */
    private Hashtable createRecordHash(
            ResultSet rs,
            int[] types,
            int startColumn,
            int columnCount) throws SQLException {
        Hashtable tmpHash = new Hashtable();
        if (startColumn >= columnCount) {
            startColumn = 1;
            columnCount = types.length;
        }

        for (int i = startColumn; i <= columnCount; i++) {
            Object tmpValue = null;

            int columnType = types[i - 1];
            switch (columnType) {

                case Types.BINARY:
                    tmpValue = rs.getBytes(i);
                    break;

                case Types.TINYINT:
                    byte byteV = rs.getByte(i);
                    tmpValue = byteV != 0 ? new Byte(byteV) : DEFAULT_BYTE_OBJECT; // 郁闷的数字,HSQL结果集的getObject(i)返回TINYINT类型时,返回的不是Byte对象,而是Integer对象
                    break;

                case Types.SMALLINT:
                    short shortV = rs.getShort(i);
                    tmpValue = shortV != 0 ? new Short(shortV) : DEFAULT_SHORT_OBJECT; // 郁闷的数字,HSQL结果集的getObject(i)返回SMALLINT类型时,返回的不是Byte对象,而是Integer对象
                    break;

                case Types.OTHER:
                    byte[] other = rs.getBytes(i);
                    if (other != null)
                        tmpValue = PIMDBUtil.decodeByteArrayToSerializeObject(other);
                    break;

                default:
                    tmpValue = rs.getObject(i); // 注意根据类型直接返回对应java对象的有INTEGER、BIT、DATE(返回java.sql.Date类型，为java.util.Date的子类型)
            }

            if (tmpValue != null)
                tmpHash.put(PIMPool.pool.getKey(i - 1), tmpValue);
        }
        return tmpHash;
    }

    /**
     * 建立记录hash
     */
    private Hashtable createRecordHash(
            ResultSet rs,
            ResultSetMetaData prmRsmd,
            int startColumn,
            int columnCount) throws SQLException {
        int[] types = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            types[i] = prmRsmd.getColumnType(i + 1);
        }
        return createRecordHash(rs, types, startColumn, columnCount);
    }

    /**
     * 删除或者插入类型名
     */
    private boolean insOrDelCateName(
            String cate,
            boolean isInsert) {
        // 数据库表名字
        String tabName = ModelDBCons.CATEGORY_TABLE_NAME;
        //
        StringBuffer sql = new StringBuffer();
        if (isInsert) {
            sql.append("INSERT INTO ").append(tabName).append(" VALUES(");
            sql.append("'").append(cate).append("');");
        } else {
            sql.append("DELETE FROM ").append(tabName).append(" WHERE NAME = '").append(cate).append("';");
        }

        try {
            Connection conn = PIMDBConnecter.instance.getConnection();
            Statement smt = conn.createStatement();
            int rows = smt.executeUpdate(sql.toString());

            // 关闭
            smt.close();
            smt = null;
            PIMDBConnecter.instance.reConnectDb();

            return rows != 0;
        } catch (SQLException e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 判断当前的记录是否已经存在
     * 
     * @param prmTableName
     *            数据库表的马名字
     * @param prmConn
     *            数据库连接
     * @param prmId
     *            记录的ID号
     * @return boolean 如果存在则返回true,否则为false
     * 
     *         note: 此方法可以被废弃,但是如果用来检验一些重要的数据库表,例如VIEWINFO表的话,还是可以保证一定的安全
     */
    private boolean haveRecord(
            String prmTableName,
            Connection prmConn,
            int prmId) throws SQLException {
        StringBuffer sql = new StringBuffer();
        // 查找当前的ID号是否存在
        sql.append("SELECT ID FROM ").append(prmTableName).append(" WHERE ID = ?");
        PreparedStatement prepareStmt = prmConn.prepareStatement(sql.toString());
        prepareStmt.setInt(1, prmId);

        ResultSet result = prepareStmt.executeQuery();
        boolean haveNext = result.next();

        // 关闭连接
        result.close();
        result = null;
        prepareStmt.close();
        prepareStmt = null;

        return haveNext;
    }

    /**
     * 取到某个类型的实现IDbDataSource接口的类的实例
     * 
     * @param prmDataType
     *            类型,PIMViewInfo,ViewFormat等类型
     * @param prmRecordId
     *            记录的ID号码
     * @return
     * @NOTE:这些表在数据库中是唯一的,不会有同类型的表,所以根据记录的ID号和表的类型即可得到IDbDataSource
     */
    private IDbDataSource getDataSource(
            int prmDataType,
            int prmRecordId) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ").append(PIMDBUtil.getTableName(prmDataType));
        sql.append(" WHERE ID = ").append(prmRecordId); // 设置查询记录的ID号

        IDbDataSource tmpSource = getSourceClass(prmDataType); // 得到具体类的实例
        if (tmpSource == null) // 判空
            return null;

        try {
            Connection conn = PIMDBConnecter.instance.getConnection(); // 建立数据库连接
            Statement smt = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql.toString());
            ResultSetMetaData rsmd = rs.getMetaData();

            if (rs.next()) {
                tmpSource.enrichFromRS(rs, rsmd);
                return tmpSource;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**/
    private IDbDataSource getDataSource(
            int prmDataType,
            int[] prmCondition,
            int prmFolderID) {
        IDbDataSource tmpSource = getSourceClass(prmDataType);
        if (tmpSource == null) {
            ErrorUtil.write("PIMDBModel.getDataSource():can get none IDbDataSource but a null with giving prm,");
            return null;// 时没有能够根据参数prmDataType（"+prmDataType+"）得到相应的IDbDataSource，只得到一个null）");
        }

        try {
            Connection tmpConn = PIMDBConnecter.instance.getConnection();
            Statement tmpState = tmpConn.createStatement();
            ResultSet tmpRS =
                    tmpState.executeQuery("SELECT * FROM ".concat(PIMDBUtil.getTableName(prmDataType)).concat(
                            getWhereExpOfMidData(prmDataType, prmCondition, prmFolderID)));
            ResultSetMetaData tmpRSMD = tmpRS.getMetaData();

            // TODO:这里的注释不够详细：如：但从数据库中根据类型和文件夹取ViewInfo的时候，为什么要用.next()进行区分判断？表示什么状况？
            if (tmpRS.next()) { // @NOTE:这里只有一个结果,用if,不用where
                tmpSource.enrichFromRS(tmpRS, tmpRSMD);
            } else {
                ErrorUtil.write("find no viewinfo mathing the given folderID.");
                return null;
            }

            if (prmDataType == ModelCons.VIEW_INFO_DATA)
                ((PIMViewInfo) tmpSource).setFolderID(prmFolderID);
            else if (prmDataType == ModelCons.VIEW_FORMAT_DATA) {
                ViewFormat tmpFormat = (ViewFormat) tmpSource;
                tmpFormat.setAppSubType(prmCondition[0]);
                tmpFormat.setAppSubType(prmCondition[1]);
                tmpFormat.setModeType(prmCondition[2]);
            }
            return tmpSource;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据数据类型得到实现IDbDataSource接口类的实例
     */
    private IDbDataSource getSourceClass(
            int prmDataType) {
        String tmpClassName = PIMDBUtil.getClassName(prmDataType);
        Object obj = null;
        try {
            Class tmpClass = Class.forName(tmpClassName);
            obj = tmpClass.newInstance();
        } catch (Exception e) {
            obj = null;
        }
        return (IDbDataSource) obj;
    }

    /**
     * 生成SQL语句的where表达式
     * 
     * @param prmFolderID
     *            表示一个文件夹的Int值.
     */
    private String getWhereExpOfMidData(
            int prmDataType,
            int[] prmCondition,
            int prmFolderID) {
        String tmpWhere = " WHERE ";
        if (prmDataType == ModelCons.VIEW_INFO_DATA) {
            tmpWhere = tmpWhere.concat(" FOLDERID = ").concat(String.valueOf(prmFolderID));
        } else if (prmDataType == ModelCons.MAIL_RULE_DATA || prmDataType == ModelCons.ACCOUNT_INFO_DATA) {
            tmpWhere = tmpWhere.concat(" SERIES = ").concat(String.valueOf(prmCondition[0]));
        } else if (prmDataType == ModelCons.VIEW_FORMAT_DATA) {
            tmpWhere =
                    tmpWhere + " APPTYPE = " + String.valueOf(prmCondition[0]) + " AND APPSUBTYPE = "
                            + String.valueOf(prmCondition[1]) + " AND MODETYPE = " + String.valueOf(prmCondition[2]);
        }
        return tmpWhere;
    }

    /**
     * 生成SQL语句的where表达式
     */
    private String getWhereExpression(
            int prmDataType,
            int[] condition) {
        String tmpWhere = " WHERE ";
        if (prmDataType == ModelCons.VIEW_INFO_DATA) {
            int tmpLength = condition.length;
            if (tmpLength == 1) {
                tmpWhere = tmpWhere.concat(" APPTYPE = ").concat(String.valueOf(condition[0]));
            } else if (tmpLength == 2) {
                tmpWhere =
                        tmpWhere.concat(" APPTYPE = ").concat(String.valueOf(condition[0]))
                                .concat(" AND APPSUBTYPE = ").concat(String.valueOf(condition[1]));
            }
        } else if (prmDataType == ModelCons.MAIL_RULE_DATA || prmDataType == ModelCons.ACCOUNT_INFO_DATA) {
            tmpWhere = tmpWhere.concat(" SERIES = ").concat(String.valueOf(condition[0]));
        } else if (prmDataType == ModelCons.VIEW_FORMAT_DATA) {
            tmpWhere =
                    tmpWhere + " APPTYPE = " + String.valueOf(condition[0]) + " AND APPSUBTYPE = "
                            + String.valueOf(condition[1]) + " AND MODETYPE = " + String.valueOf(condition[2]);
        }
        return tmpWhere;
    }

    // @TODO:本方法不知道为什么没有直接将SQL写成"append("UPDATE ModelDBConstants.VIEWINFO_TABLE_NAME SET FOLDERID =
    // tmpNewFolID WHERE FOLDERID = tmpOldFolID;,却要绕个大弯去搞什么RecordID? 目前太多地方编译不过,等能编译后试一下.
    private void updateViewInfoNodeID(
            PIMViewInfo prmNewInfo,
            PIMViewInfo prmOldInfo) {
        // 判断是否为viewInfo,如果是则更新相关的子viewInfo的FOLDERID信息
        int tmpOldFolID = prmOldInfo.getPathID();
        int tmpNewFolID = prmNewInfo.getPathID();

        ArrayList tmpRecIdList = new ArrayList();
        ArrayList tmpNodeIDList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ID, FOLDERID FROM ").append(ModelConstants2.VIEWINFO_TABLE_NAME);
        sql.append(" WHERE FOLDERID = ").append(tmpOldFolID);

        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 数据库连接
            Statement smt = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql.toString());
            while (rs.next()) {
                tmpRecIdList.add(PIMPool.pool.getKey(rs.getInt(1)));
                tmpNodeIDList.add(PIMPool.pool.getKey(rs.getInt(2)));
            }
            rs.close();
            rs = null;

            for (int size = tmpNodeIDList.size(), i = 0; i < size; i++) {
                sql.setLength(0);
                sql.append("UPDATE ").append(ModelConstants2.VIEWINFO_TABLE_NAME).append(" SET FOLDERID  = ")
                        .append(tmpNewFolID).append(" WHERE ID = ").append(tmpRecIdList.get(i));

                smt.executeUpdate(sql.toString());
            }
            smt.close();
            smt = null;

            PIMDBConnecter.instance.reConnectDb();
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
        }
    }

    /**
     * 插入或者更新数据源
     * 
     * @param source
     * @param inInsert
     *            标识符,操作是否为插入
     * @return boolean 插入或者更新成功则返回true,否则为false
     */
    private boolean insOrUpdateDataSource(
            IDbDataSource source,
            boolean isInsert) {
        if (source == null)
            return false;

        int tmpDataType = source.getDataType(); // 得到数据的类型
        String tmpTableName = PIMDBUtil.getTableName(tmpDataType);

        if (tmpTableName == null || tmpTableName.length() < 1)// 判断数据库表的名字
            return false;

        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 数据库连接
            if (isInsert) // 判断是否为插入操作
            {
                int id = PIMDBConnecter.instance.getNextID(tmpTableName);
                source.setRecordID(id);
            }

            boolean result = executeUpdate(tmpTableName, conn, source, isInsert);
            PIMDBConnecter.instance.reConnectDb();
            return result;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return false;
        }
    }

    /**
     * 新建导航面板应用的子文件夹时,创建INFOLDER对应数据库表
     * 
     * @param prmFolder
     *            INFOLDER
     * @param conn
     *            Connection数据库连接实例
     * @return boolean 创建是否成功
     */

    /**
     * 删除FOLDER执行动作
     */
    private void deleteFolder(
            int prmFolderID,
            int type) {
        // //删除相互关联的数据库表, 不对已删除项做处理
        // String[] tmpNames =
        // PIMDBUtil.deleteTableNames(
        // (String[]) tableNames.toArray(new String[0]),
        // PIMDBUtil.convertFolderToTableName(prmFolderID));

        // for (int len = tmpNames.length, i = 0; i < len; i++)
        // {
        // A.s(tmpNames[i]);
        // }
        // dropTables(tmpNames);
    }

    /**
     * 得到所有的数据库表的名字
     * 
     * @return ArrayList 得到所有的数据库的名字
     */
    private boolean initTableNames() {
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库的连接
            DatabaseMetaData dm = conn.getMetaData();
            ResultSet rs = dm.getTables(null, null, null, null);

            tableNames = new Vector();// 数据库所有的表的名字
            while (rs.next())
                tableNames.add((rs.getString("TABLE_NAME")).trim().toUpperCase());

            rs.close();// 关闭结果集
            rs = null;
            PIMDBConnecter.instance.reConnectDb();

            return tableNames.size() > 0;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    /**
     * 执行更新数据库表记录
     * 
     * note : 此方法中插入记录时,受IDbDataSource接口范化的类的createSqlPreparedInsert()方法的影响, 还是通过取到最大的ID号来得到下个记录的ID号,并插入记录,这没有为其自动分配ID号
     */
    private boolean executeUpdate(
            String prmTableName,
            Connection conn,
            IDbDataSource source,
            boolean isInsert) throws SQLException {
        if (isInsert && haveRecord(prmTableName, conn, source.getRecordID()))
            return false; // 判断当前的数据库中是否已经有当前的ID号

        // 整备插入语句或者更新语句
        String preparedSql = null;
        if (isInsert) {
            preparedSql = source.createSqlPreparedInsert(prmTableName);
        } else {
            preparedSql = source.createSqlPreparedUpdate(prmTableName);
            // @TODO: 在数据库中改名数据库表
        }

        // 执行IDbDataSource接口不同的子类实现的processPreparedStatement()方法
        PreparedStatement prepareStmt = conn.prepareStatement(preparedSql);
        source.processPreparedStatement(prepareStmt);
        int rs = prepareStmt.executeUpdate();

        // 关闭
        prepareStmt.close();
        prepareStmt = null;

        return rs != 0;
    }

    /**
     * 返回当前应用的所有的记录
     * 
     * @return Vector 返回当前的应用的所有的记录
     */
    private Vector getAppRecords(
            int prmAppType) {
        String tmpTableName = getTableName(prmAppType);// 得到该应用所有表的名字
        if (tmpTableName == null)
            return null;

        Vector tmpRecordVec = new Vector();
        try// 查询联系人的SQL语句
        {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库连接
            Statement smt = conn.createStatement();// 遍历所有的联系人表

            String sql = "SELECT * FROM ".concat(tmpTableName);
            ResultSet rs = smt.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            while (rs.next()) {
                PIMRecord tmpRecord = new PIMRecord();
                tmpRecord.setAppIndex(prmAppType);

                // 设置记录的ID号
                int id = rs.getInt(1);
                tmpRecord.setRecordID(id);

                // 设置记录的INFOLDER字段
                tmpRecord.setInfolderID(CASUtility.getAPPNodeID(prmAppType));

                Hashtable recHash = createRecordHash(rs, rsmd, 1, columnCount);
                tmpRecord.setFieldValues(recHash);
                tmpRecordVec.add(tmpRecord);
            }
            rs.close();
            rs = null;

            smt.close();// 关闭
            smt = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PIMDBConnecter.instance.reConnectDb();
        return tmpRecordVec;
    }

    /**
     * 得到已删除项中所有未读的记录的数目
     * 
     * @param prmFolderPath
     *            INFOLDER路径
     * @return int 得到删除的未读记录的数目
     * @NOTE:此方法已经被修改,因为数据库表的结构被修改,所以此方法已经被修改
     */
    private int getDeletedNewItemCount() {
        try {
            int count = 0;
            Connection conn = PIMDBConnecter.instance.getConnection();
            if (conn == null) {
                // todo: 写入errorlog
                return count;
            }

            Statement smt = conn.createStatement();

            // 得到回收站所有表的名字
            Vector names = CustOpts.custOps.APPNameVec;
            for (int len = names.size(), i = 0; i < len; i++) {
                String sql =
                        "SELECT COUNT(*) FROM ".concat((String) names.get(i)).concat("_RECYCLE")
                                .concat(" WHERE READED != TRUE;");
                ResultSet rs = smt.executeQuery(sql);
                if (rs.next()) {
                    count += rs.getInt(1);
                }
                rs.close();
            }
            // 关闭
            smt.close();
            smt = null;
            PIMDBConnecter.instance.reConnectDb();
            return count;
        } catch (Exception e) {
            PIMDBConnecter.instance.reConnectDb();
            e.printStackTrace();
            return 0;
        }
    }

    /*
     * 得到所有的联系人的证书
     * @return vector 存在证书的联系人记录，记录包括ID, EMAIL, EMAIL_2, EMAIL_3, CER
     * @不是所有的联系人都有证书，每个联系人可能有三个邮件地址,不同的邮件地址的证书不同
     */
    private Vector getAllCertificate() {
        StringBuffer tmpSql = new StringBuffer();// 查找证书的SQL语句,查询其中的联系人(CONTACT)和相关的证书(CER)
        tmpSql.append("SELECT ID, EMAIL, EMAIL_2, EMAIL_3, CER FROM CONTACT");// 预备数据库查询状态
        try {
            Connection conn = PIMDBConnecter.instance.getConnection();// 得到数据库的连接
            Statement pStmt = conn.createStatement();
            ResultSet rs = pStmt.executeQuery(tmpSql.toString());
            ResultSetMetaData rsmd = rs.getMetaData();
            int tmpColumn = rsmd.getColumnCount();// 得到列的数目

            int[] types = tableStructurePool.getFieldTypes("CONTACT", conn);// 结果集，所有的联系人和相关的证书
            Vector tmpCer = new Vector();
            while (rs.next()) {
                int recId = rs.getInt(1);// 得到当前的记录的ID号
                Hashtable recHash = createRecordHash(rs, types, 1, tmpColumn);

                if (recHash.get(new Integer(4)) == null) // 判断当前的联系人是否有证书，如果没有证书则不用添加
                    continue;

                PIMRecord rec = new PIMRecord();// 生成记录
                rec.setRecordID(recId);
                rec.setFieldValues(recHash);
                tmpCer.add(rec);
            }

            rs.close();
            rs = null;
            pStmt.close();
            pStmt = null;
            PIMDBConnecter.instance.reConnectDb();

            return tmpCer;
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    private DBTableStructurePool tableStructurePool;

    private Vector tableNames;// 负责保存数据库的表结构的信息
}

// /**
// * 确认存在
// * @param prmTableName
// * @throws SQLException
// */
// private boolean checkContainsTable(Statement prmSmt, String prmTableName) throws SQLException
// {
// if (tableNames.contains(prmTableName)) //包含则返回
// {
// return true;
// }
// String[] tableName = CustomOptions.custOps.getTableNames();
// int size = tableName.length;
//
// int iTablePos = -1;
// for (int i = size; -- i >= 0; ) //判断当前的数据库表是否可以建立
// {
// if (tableName[i].equalsIgnoreCase(prmTableName))
// {
// iTablePos = i;
// break;
// }
// }
// if (iTablePos > -1) //
// {
// //建立数据库表
// StringBuffer sql = new StringBuffer();
//
// sql.append("CREATE CACHED TABLE ").append(prmTableName)
// .append(" (");
// String[] fieldList = DefaultDBInfo.TABLE_FIELD_LIST[iTablePos * 2];
// String[] fieldTypeList = DefaultDBInfo.TABLE_FIELD_LIST[iTablePos * 2 + 1];
// int fieldCount = fieldList.length;
// for (int i = 0; i < fieldCount; i++)
// {
// sql.append(fieldList[i]).append(fieldTypeList[i]);
// }
// sql.append(");");
//
// if (prmSmt.executeUpdate(sql.toString()) > 0)
// {
// tableNames.add(prmTableName);
// }
// }
// return iTablePos > -1;
// }
//
/* 以下为插入记录的批处理操作 */
// /**
// *
// * @param prmTableName
// * @return
// */
// String getBatchInsertPreparedSql(String prmTableName, int prmAppType)
// {
// final String insertSql = "INSERT INTO ";
// //取到根节点的表的名字，例如：[PIM, 信息管理, 收件箱, 文件夹]的根节点的名字为[PIM, 信息管理, 收件箱]
// String rootName = PIMDBUtil.getMainTableName(prmTableName);
// String[] fieldNames = tableStructurePool.getFieldNameArray(rootName);
//
// StringBuffer sql = new StringBuffer();
// sql.append(insertSql).append(prmTableName); //"INSERT INTO <tablename>"
// sql.append('(');
//
// int length = fieldNames.length, last = length - 1;
// for (int i = 0; i < last; i++) //"INSERT INTO <tablename> (...)"
// {
// sql.append(fieldNames[i]).append(',');
// }
// sql.append(fieldNames[last]);
// sql.append(")VALUES("); //"INSERT INTO <tablename> (...) VALUES ("
// boolean isDiary = prmAppType == ModelConstants.DIARY_APP; //判断当前的类型是否为日记类型
// sql.append(isDiary ? "NULL" : "?");
// for (int i = 1; i < length; i ++)
// {
// sql.append(",?");
// }
// sql.append(')'); //"INSERT INTO <tablename> (...) VALUES (?,...);"
// return sql.toString();
// }
//
// /**
// *
// * @param prepareStmt
// * @param prmRecord
// * @param fieldTypes
// * @throws SQLException
// */
// void processBatchPreparedStatement(
// PreparedStatement prepareStmt, PIMRecord prmRecord, final int[] fieldTypes)
// throws SQLException
// {
//
// Hashtable values = prmRecord.getFieldValues();
//
// boolean isNotDiary = prmRecord.getAppIndex() != ModelConstants.DIARY_APP;
// if (isNotDiary) //判断是否为日记类型，非日记则不要处理ID
// {
// values.remove(Pool.getInstance().getIntegerKey(ModelDBConstants.ID));
// }
//
// Enumeration tmpEnum = values.keys();
// while (tmpEnum != null && tmpEnum.hasMoreElements())
// {
// Object key = tmpEnum.nextElement();
// int keyInt = ((Integer) key).intValue();
//
// int type = fieldTypes[keyInt]; //得到字段的类型
// Object value = getConvertObject(values.get(key), type);
// //从数据库表结构缓存中得到对应字段的类型
// prepareStmt.setObject(keyInt + 1, value, type);
// }
// }
//
// /**
// * 插入多个记录,注意这里涉及插入多条记录到统一张TABLE中是可以优化算法
// * 采用区别于插入单条记录的方式，采用生成预编译的SQL语句包括的所有的字段，再根据字段中
// *
// * @param: List 记录
// * @return boolean 如果插入成功返回true,否则返回false
// *
// * @NOTE:默认插入到统一张数据库表中【只支持到插入到一张数据库表中】
// */
// public boolean insertRecords(List prmRecords)
// {
// long beg = System.currentTimeMillis();
// //records为空则返回
// int size = -1;
// if (prmRecords == null || (size = prmRecords.size()) < 1) //判断列表中没有记录则不用
// {
// return false;
// }
// int count = 0; //记录成功插入的记录的数目
// PIMRecord[] tmpRecords = new PIMRecord[size];
// PIMRecord tmpRd = (PIMRecord)prmRecords.get(0);
// String tableName = getTableName(tmpRd); //得到数据库表的名字
// int type = tmpRd.getType(); //得到应用的类型
//
// int[] fieldTypes =
// tableStructurePool.getFieldTypeArray(PIMDBUtil.getMainTableName(tableName)); //从缓存中取到当前数据库表字段的类型数组
// try
// {
// Connection conn = PIMDBConnecter.instance.getConnection();
// if (conn == null)
// {
// return false;
// }
// String rootName = PIMDBUtil.getMainTableName(tableName);
// PreparedStatement psmt = conn.prepareStatement(getInsertPreparedSql(tableName, tmpRd)); //批量处理的SQL语句
// for (int i = 0; i < size; i++)
// {
// tmpRd = ((PIMRecord)prmRecords.get(i));
// processPreparedStatement(psmt, tmpRd, rootName);
// psmt.executeUpdate();
// tmpRecords[count++] = tmpRd;
// }
// psmt.close();
// psmt = null;
// }
// catch (SQLException e)
// {
// e.printStackTrace();
// }
// //关闭数据库连接
// PIMDBConnecter.instance.reConnectDb();
// //有插入数据库记录则发送视图更新操作，否则不用刷新视图
// if (count > 0)
// {
// firePIMModelDataInserted(type, tmpRecords);
// }
// return count == size;
// }
// /**插入一条mail记录
// * @para: prmRecord 需要插入的记录
// * @return: boolean 插入是否成功
// */
// public boolean insertMailRecord(PIMRecord prmRecord)
// {
// boolean tmpHaveRepeatMail = false;
// try
// {
// tmpHaveRepeatMail = haveRepeatMail(prmRecord);
// PIMDBConnecter.instance.reConnectDb();//关闭连接
// }
// catch (SQLException se)
// {
// PIMDBConnecter.instance.reConnectDb();
// se.printStackTrace();
// }
// if (tmpHaveRepeatMail)
// return false;
//
// return insertRecord(prmRecord, true);
// }
// /** 删除数据库中的表
// */
// private boolean dropTables(String[] prmTableName)
// {
// if (prmTableName == null || prmTableName.length < 1)
// {
// return false;
// }
//
// try
// {
// //得到数据库连接
// Connection conn = PIMDBConnecter.instance.getConnection();
// Statement smt = conn.createStatement();
//
// //循环删除
// for (int size = prmTableName.length, i = 0; i < size; i++)
// {
// String sql = "DROP TABLE ".concat(prmTableName[i]);
// smt.execute(sql);
//
// //操作失败,则不会删除当前的数据库表
// tableNames.remove(prmTableName[i].trim().toUpperCase());
// }
//
// smt.close();
// smt = null;
// PIMDBConnecter.instance.reConnectDb();
// return true;
// }
// catch (SQLException se)
// {
// PIMDBConnecter.instance.reConnectDb();
// se.printStackTrace();
// return false;
// }
// }
// TODO:
// /** @NOTE:在IMODEL接口中没有定义此方法
// */
// public void resetViewInfo(int prmAppType, int prmSubAppType, String prmFolderPath)
// {
// String key = String.valueOf(prmAppType).concat(String.valueOf(prmSubAppType));
// int index = -1;
// int length = DefaultDBInfo.VIEWINFO_KEY.length;
// for (int i = 0; i < length; i++)
// {
// if (DefaultDBInfo.VIEWINFO_KEY[i].equals(key))
// {
// index = i;
// break;
// }
// }
// if (index == -1)
// {
// return;
// }
// PIMViewInfo oldViewInfo = getViewInfo(prmAppType, prmSubAppType);
// try
// {
// Connection conn = PIMDBConnecter.instance.getConnection();
// if (conn == null && PIMDBConnecter.instance.buildConnection())
// {
// conn = PIMDBConnecter.instance.getConnection();
// }
// if (conn == null)
// {
// //todo: 写入errorlog
// return;
// }
// Statement state = conn.createStatement();
// int rows = state.executeUpdate(
// "DELETE FROM VIEWINFO WHERE ID =".concat(String.valueOf(oldViewInfo.getRecordID())));
// String insertSql = DefaultDBInfo.INIT_DB_CONSTANTS[index];
// state.executeUpdate(insertSql);
//
// //关闭
// state.close();
// state = null;
// PIMDBConnecter.instance.reConnectDb();
// }
// catch (SQLException e)
// {
// PIMDBConnecter.instance.reConnectDb();
// e.printStackTrace();
// return;
// }
// fireFieldChangedEvent(prmAppType, PIMModelEvent.RESET_VIEWINFO);
// }
// /**
// * 根据INDEX来创建VIEWINFO对象,很危险.
// *
// * @param index[] 为要显示的字段
// * @param prmAppType 类型
// * @return PIMViewInfo对象,注意当前的这个PIMVIEWINFO非VIEWINFO中的记录
// */
// private PIMViewInfo createMainViewInfo(int[] indexs, int prmAppType)
// {
// PIMViewInfo viewInfo = new PIMViewInfo();
//
// viewInfo.setAppType(prmAppType); // 设置应用类型
//
// StringBuffer idx = new StringBuffer();
//
// idx.append(indexs[0]); //特殊处理第一个元素,但是如果一个元素都没有则Exception
// for (int size = indexs.length, i = 1; i < size; i++)
// {
// idx.append(',').append(indexs[i]);
// }
// viewInfo.setFieldNames(idx.toString()); //设置字段的名字
//
// viewInfo.setFolderPath(PIMUtility.getTreePath((String)CustomOptions.custOps.APPNameVec.get(prmAppType)));
// //设置INFOLDER字段
// return viewInfo;
// }
// /**
// * 修改数据库的名字
// *
// * @param prmOldName 数据库表原来的名字
// * @param prmNewName 数据库表的新的名字
// * @return boolean 数据库表改名是否成功
// * @throws SQLException SQL执行异常
// */
// private void alterTableName(Statement smt, String prmOldName, String prmNewName)
// throws SQLException
// {
// String sql = "ALTER TABLE ".concat(prmOldName).concat(" RENAME TO ").concat(prmNewName);
//
// smt.executeUpdate(sql);
// //有异常则操作失败
// tableNames.remove(prmOldName.toUpperCase());
// tableNames.add(prmNewName.toUpperCase());
// }
// /**
// * 目前已删除项没有对应真实的数据库表，所以通过VIEWINFO的FIELDNAME()字段来取要显示的字段时
// * 建立要查询的字段的SELECT <field, field, ...> FROM，此处的field 为视图显示和内部使用的字段
// * 以上的'field'字段为共有字段，即在各个表中都存在以上的字段【已删除项中显示的字段都是共有字段】
// *
// * note: 已删除项中显示的如果不是共有字段查询时会抛出 SQLException 异常
// *
// * 额外字段：添加查找INFOLDER字段,INFOLDER字段标识当前已经删除的记录删除前对应的文件夹路径
// * @param prmViewInfo
// * @return
// */
// private Object[][] getImapRecordsArray(PIMViewInfo prmViewInfo)
// {
// try
// {
// //准备连接
// Connection tmpConn = PIMDBConnecter.instance.getConnection();
// if (tmpConn == null)
// {
// return null;
// }
//
// //建立要查询的字段的SELECT <field, field, ...> FROM，此处的field 为视图显示和内部使用的字段
// int type = ModelConstants.DELETED_ITEM_APP; //【临时借用已删除项作为IMAP的应用】
// String fields = Utility.getTableField(type, prmViewInfo.getFieldNames());
//
// StringBuffer whereStr = new StringBuffer();
// String filter = prmViewInfo.getFilterString();
// String sortInfo = prmViewInfo.getSortString();
//
// if (filter != null)
// {
// whereStr.append(" WHERE");
// whereStr.append(" (").append(filter).append(')');
// }
// if (sortInfo != null)
// {
// whereStr.append(" ORDER BY ").append(sortInfo);
// }
// whereStr.append(";");
//
//
// Vector imapTableNames = new Vector();
// for (int size = tableNames.size(), i = 0; i < size; i ++)
// {
// String tableName = (String)tableNames.get(i);
// if (!tableName.equals("IMAP") && tableName.startsWith("IMAP"))
// {
// imapTableNames.add(tableName);
// }
// }
//
// int len = imapTableNames.size();
// Object[][][] valueAry = new Object[len][][];
//
// int resultAryLength = 0;
// //准备SQL语句
// Statement stmt =
// tmpConn.createStatement(
// ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
// StringBuffer sql = new StringBuffer();
//
// for (int i = 0; i < len; i++)
// {
// //得到所有的回收站表的名字
// String tmpTableName = (String)imapTableNames.get(i);
//
// sql.append("SELECT ").append(fields);
// sql.append(" FROM ").append(tmpTableName);
// sql.append(whereStr);
//
// String tableName = PIMDBUtil.getMainTableName(tmpTableName);
// Integer apptype =
// PIMPool.pool.getIntegerKey(PIMDBUtil.getAppTableIndex(tableName));
//
// //查找的结果集
// ResultSet rs = stmt.executeQuery(sql.toString());
// valueAry[i] = getRecordArrayFromResultSet(rs, apptype, -1); //在最后添加一个 apptype应用类型
// resultAryLength += valueAry[i].length;
// sql.setLength(0);
// rs.close();
// }
//
// Object[][] resultAry = new Object[resultAryLength][];
// for (int pos = 0, i = 0; i < len; i++)
// {
// int l = valueAry[i].length;
// if (l < 1)
// {
// continue;
// }
//
// System.arraycopy(valueAry[i], 0, resultAry, pos, l);
// pos += l;
// }
//
// stmt.close();
// stmt = null;
//
// PIMDBConnecter.instance.reConnectDb();
//
// return resultAry;
// }
// catch (SQLException sqle)
// {
// sqle.printStackTrace();
// PIMDBConnecter.instance.reConnectDb(); //关闭数据库连接
// return null;
// }
// }
//
// /**
// * 对本地文件夹记录的获取
// * @param prmViewInfo
// * @return
// */
// private Object[][] getLocalRecords(PIMViewInfo prmViewInfo)
// {
// try
// {
// //准备连接
// Connection tmpConn = PIMDBConnecter.instance.getConnection();
// if (tmpConn == null)
// {
// return null;
// }
//
// //建立要查询的字段的SELECT <field, field, ...> FROM，此处的field 为视图显示和内部使用的字段
// int type = ModelConstants.INBOX_APP;
// String fields = Utility.getTableField(type, prmViewInfo.getFieldNames());
//
// StringBuffer whereStr = new StringBuffer();
// String filter = prmViewInfo.getFilterString();
// String sortInfo = prmViewInfo.getSortString();
//
// if (filter != null)
// {
// whereStr.append(" WHERE");
// whereStr.append(" (").append(filter).append(')');
// }
// if (sortInfo != null)
// {
// whereStr.append(" ORDER BY ").append(sortInfo);
// }
// whereStr.append(";");
//
// Vector tableNameStr = CustomOptions.custOps.APPNameVec;//getRecycleBinTableNames();
// int len = tableNameStr.size();
// Object[][][] valueAry = new Object[len][][];
//
// int resultAryLength = 0;
// //准备SQL语句
// Statement stmt =
// tmpConn.createStatement(
// ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
// StringBuffer sql = new StringBuffer();
//
// for (int i = 0; i < len; i++)
// {
// //得到所有的回收站表的名字
// String tmpTableName = ((String)tableNameStr.get(i)).concat("_RECYCLE");
//
// sql.append("SELECT ").append(fields);
// sql.append(" FROM ").append(tmpTableName);
// sql.append(whereStr);
//
// String tableName = PIMDBUtil.getMainTableName(tmpTableName);
// Integer apptype =
// PIMPool.pool.getIntegerKey(PIMDBUtil.getAppTableIndex(tableName));
//
// //查找的结果集
// ResultSet rs = stmt.executeQuery(sql.toString());
// valueAry[i] = getRecordArrayFromResultSet(rs, apptype, -1); //在最后添加一个 apptype应用类型
// resultAryLength += valueAry[i].length;
// sql.setLength(0);
// rs.close();
// }
//
// Object[][] resultAry = new Object[resultAryLength][];
// for (int pos = 0, i = 0; i < len; i++)
// {
// int l = valueAry[i].length;
// if (l < 1)
// {
// continue;
// }
//
// System.arraycopy(valueAry[i], 0, resultAry, pos, l);
// pos += l;
// }
//
// stmt.close();
// stmt = null;
//
// PIMDBConnecter.instance.reConnectDb();
//
// return resultAry;
//
// /*String sql = createViewInfoQueryString(viewInfo);
// if (sql == null)
// {
// return null;
// }
//
// //得到数据库连接
// Connection conn = PIMDBConnecter.instance.getConnection();
// if (conn == null)
// {
// return null;
// }
//
// //建立数据库连接状态
// Statement stmt =
// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
// ResultSet rs = stmt.executeQuery(sql);
// Object[][] values = getRecordArrayFromResultSet(rs, null, -1);
// //@NOTE:这个地方可以优化的第一次知道了各个列的类型以后,以后各个列的类型也就知道了,所以不用每次都判断为什么类型,如果有多条记录是,可以有优化的空间
//
// //关闭结果集合
// rs.close();
// rs = null;
//
// //关闭状态
// stmt.close();
// stmt = null;
//
// //关闭数据库连接
// PIMDBConnecter.instance.reConnectDb();
//
// return values;*/
// }
// catch (SQLException sqle)
// {
// sqle.printStackTrace();
// PIMDBConnecter.instance.reConnectDb(); //关闭数据库连接
// return null;
// }
// }
// /** 修改所有的数据库的表的名字
// */
// private boolean renameTable(String prmOldFolder, String prmToFolder)
// {
// //得到所有要改名的数据库表的名字
// Hashtable nameHash = PIMDBUtil.renamedTableName((String[]) tableNames.toArray(new String[0]),
// PIMDBUtil.convertFolderToTableName(prmOldFolder),
// PIMDBUtil.convertFolderToTableName(prmToFolder));
// // 没有要改名的数据库的表的名字,理论上说要有改名的数据库的表的名字的
// if (nameHash.size() < 1)
// {
// return false;
// }
// try
// {
// //得到数据库的连接
// Connection conn = PIMDBConnecter.instance.getConnection();
//
// if (conn == null && PIMDBConnecter.instance.buildConnection())
// {
// conn = PIMDBConnecter.instance.getConnection();
// }
// if (conn == null) //数据库连接失败
// {
// throw new SQLException();
// }
//
// Statement smt = conn.createStatement();
// //遍历执行改名操作
// Enumeration tmpEnum = nameHash.keys();
// while (tmpEnum.hasMoreElements())
// {
// String oldName = tmpEnum.nextElement().toString();
// //得到要修改的数据库的表的名字
// //ALTER TABLE <tablename> RENAME TO <newname>;
// alterTableName(smt, oldName, (String) nameHash.get(oldName));
// }
//
// smt.close();
// smt = null;
// PIMDBConnecter.instance.reConnectDb();
// return true;
//
// }
// catch (SQLException se)
// {
// PIMDBConnecter.instance.reConnectDb();
// se.printStackTrace();
// //@TODO:这里要注意了,要做处理否则出错;
// return false;
// }
// }
