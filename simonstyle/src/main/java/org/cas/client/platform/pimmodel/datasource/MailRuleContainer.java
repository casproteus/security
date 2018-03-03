package org.cas.client.platform.pimmodel.datasource;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.PIMDBUtil;

public class MailRuleContainer implements IDbDataSource {
    /** Creates a new instance of MailRuleContainer */
    public MailRuleContainer() {
    }

    /** Creates a new instance of MailRuleContainer */
    public MailRuleContainer(int series, boolean isSender, String ruleName) {
        this.series = series;
        this.isSender = isSender;
        this.ruleName = ruleName;
    }

    /**
     * Getter for property series.
     * 
     * @return Value of property series.
     */
    public int getSeriesNumber() {
        return this.series;
    }

    /**
     * Setter for property series.
     * 
     * @param series
     *            New value of property series.
     */
    public void setSeriesNumber(
            int series) {
        this.series = series;
    }

    /**
     * Getter for property ruleName.
     * 
     * @return Value of property ruleName.
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     * Setter for property ruleName.
     * 
     * @param ruleName
     *            New value of property ruleName.
     */
    public void setRuleName(
            String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * Getter for property isSender.
     * 
     * @return Value of property isSender.
     */
    public boolean isIsSender() {
        return this.isSender;
    }

    /**
     * Setter for property isSender.
     * 
     * @param isSender
     *            New value of property isSender.
     */
    public void setIsSender(
            boolean isSender) {
        this.isSender = isSender;
    }

    /**
     * Getter for property isSelected.
     * 
     * @return Value of property isSelected.
     */
    public boolean isIsSelected() {
        return this.isSelected;
    }

    /**
     * Setter for property isSelected.
     * 
     * @param isSelected
     *            New value of property isSelected.
     */
    public void setIsSelected(
            boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * Getter for property conditionPool.
     * 
     * @return Value of property conditionPool.
     */
    public MailRulePool getConditionPool() {
        return this.conditionPool;
    }

    /**
     * Setter for property conditionPool.
     * 
     * @param conditionPool
     *            New value of property conditionPool.
     */
    public void setConditionPool(
            MailRulePool conditionPool) {
        this.conditionPool = conditionPool;
    }

    /**
     * Getter for property exceptionPool.
     * 
     * @return Value of property exceptionPool.
     */
    public MailRulePool getExceptionPool() {
        return this.exceptionPool;
    }

    /**
     * Setter for property exceptionPool.
     * 
     * @param exceptionPool
     *            New value of property exceptionPool.
     */
    public void setExceptionPool(
            MailRulePool exceptionPool) {
        this.exceptionPool = exceptionPool;
    }

    /**
     * Getter for property actionPool.
     * 
     * @return Value of property actionPool.
     */
    public MailRulePool getActionPool() {
        return this.actionPool;
    }

    /**
     * Setter for property actionPool.
     * 
     * @param actionPool
     *            New value of property actionPool.
     */
    public void setActionPool(
            MailRulePool actionPool) {
        this.actionPool = actionPool;
    }

    /**
     * Getter for property junkOrAdult.
     * 
     * @return Value of property junkOrAdult.
     */
    public int getJunkOrAdult() {
        return this.junkOrAdult;
    }

    /**
     * Setter for property junkOrAdult.
     * 
     * @param junkOrAdult
     *            New value of property junkOrAdult.
     */
    public void setJunkOrAdult(
            int junkOrAdult) {
        this.junkOrAdult = junkOrAdult;
    }

    /**
     * 通过数据库查询的结果得到相应的对象实例
     * 
     * @param rs
     *            通过数据库查询得出的结果集
     * @param rsmd
     *            通过结果集得到的结果集的描述
     * @Exception SQLException 要抛出SQL的异常
     */
    public void enrichFromRS(
            ResultSet rs,
            ResultSetMetaData rsmd) throws SQLException {
        MailRuleContainer mailRule = new MailRuleContainer();
        Field[] fields = mailRule.getClass().getDeclaredFields();
        int fieldCount = fields.length;
        int columnCount = rsmd.getColumnCount();
        try {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                for (int j = 0; j < fieldCount; j++) {
                    String fieldName = fields[j].getName();
                    if (columnName.equalsIgnoreCase(fieldName)) {
                        String fieldType = fields[j].getType().getName();
                        if (fieldType.equalsIgnoreCase("int")) {
                            fields[i - 1].setInt(mailRule, rs.getInt(i));
                        } else if (fieldType.equalsIgnoreCase("boolean")) {
                            fields[i - 1].setBoolean(mailRule, rs.getBoolean(i));
                        } else if (fieldType.equalsIgnoreCase("java.lang.String")) {
                            fields[i - 1].set(mailRule, rs.getString(i));
                        } else if (fieldType
                                .equalsIgnoreCase("org.cas.client.platform.pimmodel.datasource.MailRulePool")) {
                            byte[] poolByte = rs.getBytes(i);
                            MailRulePool pool = null;
                            if (poolByte != null) {
                                pool = (MailRulePool) PIMDBUtil.decodeByteArrayToSerializeObject(poolByte);
                            }
                            fields[i - 1].set(mailRule, pool);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * @param tableName
     *            需要操作的数据库的表名
     * @return String 通过类中的字段得到数据库可以解析的用于Insert一条记录的语句
     */
    public String createSqlInsert(
            String tableName) {
        return null;
    }

    /**
     * @param tableName
     *            需要操作的数据库的表名
     * @return String 创造一条db解析的用于贮备数据库信息的预备语句
     */
    public String createSqlPreparedInsert(
            String tableName) {
        StringBuffer sb = new StringBuffer();
        Field[] fields = getClass().getDeclaredFields();
        int fieldCount = fields.length;
        sb.append("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < fieldCount - 1; i++) {
            sb.append(fields[i].getName().toUpperCase()).append(",");
        }
        sb.append(fields[fieldCount - 1].getName().toUpperCase()).append(") VALUES (");
        for (int i = 0; i < fieldCount - 1; i++) {
            sb.append("?,");
        }
        sb.append("?);");
        return sb.toString();
    }

    /**
     * @param tableName
     *            需要操作的数据库的表名
     * @return String 创造一条db解析的用于贮备数据库信息的预备语句
     */
    public String createSqlPreparedUpdate(
            String tableName) {
        StringBuffer sb = new StringBuffer();
        Field[] fields = getClass().getDeclaredFields();
        int fieldCount = fields.length;
        sb.append("UPDATE ").append(tableName).append(" SET ");
        for (int i = 0; i < fieldCount - 1; i++) {
            sb.append(fields[i].getName().toUpperCase()).append("=?,");
        }
        sb.append(fields[fieldCount - 1].getName().toUpperCase()).append("=? WHERE ID = ").append(id);
        return sb.toString();
    }

    /**
     * @param tableName
     *            需要操作的数据库的表名
     * @return String 得到数据库可以解析的用于update记录的语句
     */
    public String createSqlUpdate(
            String tableName) {
        return null;
    }

    /**
     * 每个类都对应一个表，每个表都有一个索引，通过这个方法返回索引并得到表名
     */
    public int getDataType() {
        return ModelCons.MAIL_RULE_DATA;
    }

    /**
     * 得到对象在库中存在的id
     */
    public int getRecordID() {
        return id;
    }

    /**
     * @param statement
     *            数据库生成一个预备语句后，更据类的特点对每一个待定的变量 赋一个值。注意：要与<code>createSqlPreparedUpdate<code>
     *                 中定义的需要赋值的变量一一对应
     * @Exception SQLException 要抛出SQL的异常
     */
    public void processPreparedStatement(
            PreparedStatement statement) throws SQLException {
        Field[] fields = getClass().getDeclaredFields();
        int fieldCount = fields.length;
        try {
            for (int i = 0; i < fieldCount; i++) {
                String type = fields[i].getType().getName();
                // A.s(" type :"+type);
                if (type.equalsIgnoreCase("int")) {
                    statement.setInt(i + 1, fields[i].getInt(this));
                } else if (type.equalsIgnoreCase("boolean")) {
                    statement.setBoolean(i + 1, fields[i].getBoolean(this));
                } else if (type.equalsIgnoreCase("java.lang.String")) {
                    statement.setString(i + 1, (String) fields[i].get(this));
                } else if (type.equalsIgnoreCase("org.cas.client.platform.pimmodel.datasource.MailRulePool")) {
                    MailRulePool pool = (MailRulePool) fields[i].get(this);
                    byte[] poolByte = PIMDBUtil.encodeSerializeObjectToByteArray(pool);
                    if (poolByte == null) {
                        poolByte = new byte[0];
                    }
                    statement.setBytes(i + 1, poolByte);
                }
            }
        } catch (IllegalAccessException e) {
        }
    }

    /**
     * 将需要得ID赋给对象，此id是model赋值并且在一张表中是唯一的
     */
    public void setRecordID(
            int id) {
        this.id = id;
    }

    //
    public String toString() {
        Field[] fields = getClass().getDeclaredFields();
        int fieldCount = fields.length;
        StringBuffer sb = new StringBuffer();
        try {
            for (int i = 0; i < fieldCount; i++) {
                String type = fields[i].getType().getName();
                String fieldName = fields[i].getName();
                sb.append(fieldName).append(" = ");
                if (type.equalsIgnoreCase("int")) {
                    sb.append(fields[i].getInt(this));
                } else if (type.equalsIgnoreCase("boolean")) {
                    sb.append(fields[i].getBoolean(this));
                } else if (type.equalsIgnoreCase("java.lang.String")) {
                    sb.append((String) fields[i].get(this));
                } else if (type.equalsIgnoreCase("org.cas.client.platform.pimmodel.datasource.MailRulePool")) {
                    sb.append((MailRulePool) fields[i].get(this));
                }
                sb.append("; ");
            }
        } catch (IllegalAccessException e) {
            // e.printStackTrace();
        }
        return sb.toString();
    }

    /*
     * protected void test() { String[] dbFieldName = emo.pim.pimmodel.database.DefaultDBInfo.MAILRULE_TABLE_FIELD;
     * Class calss = getClass(); Field[] fields = getClass().getDeclaredFields(); int len1 = dbFieldName.length; int
     * len2 = fields.length; for (int i = 0; i < len2; i++) { String name = fields[i].getName(); boolean b = false; for
     * (int j = 0; j < len1; j++) { if (name.equalsIgnoreCase(dbFieldName[j])) { b = true; break; } } if (b) {
     * A.s(" the field '"+name+"' is ok!"); } else { A.s(" hahaha@@@ the field '"+name+"' is failed!!!"); } } } public
     * static void main(String[] args) { MailRuleContainer pool = new MailRuleContainer(); pool.test(); System.exit(0);
     * }
     */

    private int id;
    private int series;
    // 0:不是垃圾或成人内容的规则，1：垃圾邮件规则。2：成人邮件规则
    private int junkOrAdult;
    private boolean isSender;
    private boolean isSelected;
    private String ruleName;
    /**
     * [0] : 条件选项 [1] : 例外选项 [2] : 动作选项
     */
    // private MailRulePool[] mailRule = new MailRulePool[3];
    private MailRulePool conditionPool;
    private MailRulePool exceptionPool;
    private MailRulePool actionPool;
}
