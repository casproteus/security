package org.cas.client.platform.pimmodel.datasource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public interface IDbDataSource {
    int getRecordID(); // 得到对象在库中存在的id

    void setRecordID(
            int id); // 将需要得ID赋给对象，此id是model赋值并且在一张表中是唯一的

    int getDataType(); // 每个类都对应一个表，每个表都有一个索引，通过这个方法返回索引并得到表名

    /**
     * @param tableName
     *            需要操作的数据库的表名
     * @return String 通过类中的字段得到数据库可以解析的用于Insert一条记录的语句
     */
    String createSqlInsert(
            String tableName);

    /**
     * @param tableName
     *            需要操作的数据库的表名
     * @return String 创造一条db解析的用于贮备数据库信息的预备语句
     */
    String createSqlPreparedInsert(
            String tableName);

    /**
     * @param tableName
     *            需要操作的数据库的表名
     * @return String 得到数据库可以解析的用于update记录的语句
     */
    String createSqlUpdate(
            String tableName);

    /**
     * @param tableName
     *            需要操作的数据库的表名
     * @return String 创造一条db解析的用于贮备数据库信息的预备语句
     */
    String createSqlPreparedUpdate(
            String tableName);

    /**
     * @param statement
     *            数据库生成一个预备语句后，更据类的特点对每一个待定的变量 赋一个值。注意：要与<code>createSqlPreparedUpdate<code>
     *                 中定义的需要赋值的变量一一对应
     * @Exception SQLException 要抛出SQL的异常
     */
    void processPreparedStatement(
            PreparedStatement statement) throws SQLException;

    /**
     * 通过数据库查询的结果得到相应的对象实例
     * 
     * @param rs
     *            通过数据库查询得出的结果集
     * @param rsmd
     *            通过结果集得到的结果集的描述
     * @Exception SQLException 要抛出SQL的异常
     */
    void enrichFromRS(
            ResultSet rs,
            ResultSetMetaData rsmd) throws SQLException;
}
