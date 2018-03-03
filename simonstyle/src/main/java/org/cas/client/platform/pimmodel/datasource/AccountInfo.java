package org.cas.client.platform.pimmodel.datasource;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.cas.client.platform.casutil.ModelCons;

public class AccountInfo implements IDbDataSource {
    /** Creates a new instance of AccountInfo */
    public AccountInfo() {
    }

    /**
     * Getter for property accountName.
     * 
     * @return Value of property accountName.
     */
    public String getAccountName() {
        return this.accountName;
    }

    /**
     * Setter for property accountName.
     * 
     * @param accountName
     *            New value of property accountName.
     */
    public void setAccountName(
            String accountName) {
        this.accountName = accountName;
    }

    /**
     * Getter for property isPop3.
     * 
     * @return Value of property isPop3.
     */
    public boolean isPop3() {
        return this.isPop3;
    }

    /**
     * Setter for property isPop3.
     * 
     * @param isPop3
     *            New value of property isPop3.
     */
    public void setIsPop3(
            boolean isPop3) {
        this.isPop3 = isPop3;
    }

    /**
     * Getter for property serverName.
     * 
     * @return Value of property serverName.
     */
    public String getServerName() {
        return this.serverName;
    }

    /**
     * Setter for property serverName.
     * 
     * @param serverName
     *            New value of property serverName.
     */
    public void setServerName(
            String serverName) {
        this.serverName = serverName;
    }

    /**
     * Getter for property displayName.
     * 
     * @return Value of property displayName.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Setter for property displayName.
     * 
     * @param displayName
     *            New value of property displayName.
     */
    public void setDisplayName(
            String displayName) {
        this.displayName = displayName;
    }

    /**
     * Getter for property organizationName.
     * 
     * @return Value of property organizationName.
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * Setter for property organizationName.
     * 
     * @param organizationName
     *            New value of property organizationName.
     */
    public void setOrganizationName(
            String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Getter for property emailAddress.
     * 
     * @return Value of property emailAddress.
     */
    public String getEmailAddress() {
        return this.emailAddress;
    }

    /**
     * Setter for property emailAddress.
     * 
     * @param emailAddress
     *            New value of property emailAddress.
     */
    public void setEmailAddress(
            String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Getter for property replyToAddress.
     * 
     * @return Value of property replyToAddress.
     */
    public String getReplyToAddress() {
        return this.replyToAddress;
    }

    /**
     * Setter for property replyToAddress.
     * 
     * @param replyToAddress
     *            New value of property replyToAddress.
     */
    public void setReplyToAddress(
            String replyToAddress) {
        this.replyToAddress = replyToAddress;
    }

    /**
     * Getter for property receiveServerName.
     * 
     * @return Value of property receiveServerName.
     */
    public String getReceiveServerName() {
        return this.receiveServerName;
    }

    /**
     * Setter for property receiveServerName.
     * 
     * @param receiveServerName
     *            New value of property receiveServerName.
     */
    public void setReceiveServerName(
            String receiveServerName) {
        this.receiveServerName = receiveServerName;
    }

    /**
     * Getter for property sendServerName.
     * 
     * @return Value of property sendServerName.
     */
    public String getSendServerName() {
        return this.sendServerName;
    }

    /**
     * Setter for property sendServerName.
     * 
     * @param sendServerName
     *            New value of property sendServerName.
     */
    public void setSendServerName(
            String sendServerName) {
        this.sendServerName = sendServerName;
    }

    /**
     * Getter for property receiveAccountName.
     * 
     * @return Value of property receiveAccountName.
     */
    public String getReceiveAccountName() {
        return this.receiveAccountName;
    }

    /**
     * Setter for property receiveAccountName.
     * 
     * @param receiveAccountName
     *            New value of property receiveAccountName.
     */
    public void setReceiveAccountName(
            String receiveAccountName) {
        this.receiveAccountName = receiveAccountName;
    }

    /**
     * Getter for property receivePsd.
     * 
     * @return Value of property receivePsd.
     */
    public String getReceivePsd() {
        return this.receivePsd;
    }

    /**
     * Setter for property receivePsd.
     * 
     * @param receivePsd
     *            New value of property receivePsd.
     */
    public void setReceivePsd(
            String receivePsd) {
        this.receivePsd = receivePsd;
    }

    /**
     * Getter for property isRememberReceivePsd.
     * 
     * @return Value of property isRememberReceivePsd.
     */
    public boolean isRememberReceivePsd() {
        return this.isRememberReceivePsd;
    }

    /**
     * Setter for property isRememberReceivePsd.
     * 
     * @param isRememberReceivePsd
     *            New value of property isRememberReceivePsd.
     */
    public void setIsRememberReceivePsd(
            boolean isRememberReceivePsd) {
        this.isRememberReceivePsd = isRememberReceivePsd;
    }

    /**
     * Getter for property isNeedValidate.
     * 
     * @return Value of property isNeedValidate.
     */
    public boolean isNeedValidate() {
        return this.isNeedValidate;
    }

    /**
     * Setter for property isNeedValidate.
     * 
     * @param isNeedValidate
     *            New value of property isNeedValidate.
     */
    public void setIsNeedValidate(
            boolean isNeedValidate) {
        this.isNeedValidate = isNeedValidate;
    }

    /**
     * Getter for property isSameSet.
     * 
     * @return Value of property isSameSet.
     */
    public boolean isSameSet() {
        return this.isSameSet;
    }

    /**
     * Setter for property isSameSet.
     * 
     * @param isSameSet
     *            New value of property isSameSet.
     */
    public void setIsSameSet(
            boolean isSameSet) {
        this.isSameSet = isSameSet;
    }

    /**
     * Getter for property sendAccountName.
     * 
     * @return Value of property sendAccountName.
     */
    public String getSendAccountName() {
        return this.sendAccountName;
    }

    /**
     * Setter for property sendAccountName.
     * 
     * @param sendAccountName
     *            New value of property sendAccountName.
     */
    public void setSendAccountName(
            String sendAccountName) {
        this.sendAccountName = sendAccountName;
    }

    /**
     * Getter for property sendPsd.
     * 
     * @return Value of property sendPsd.
     */
    public String getSendPsd() {
        return this.sendPsd;
    }

    /**
     * Setter for property sendPsd.
     * 
     * @param sendPsd
     *            New value of property sendPsd.
     */
    public void setSendPsd(
            String sendPsd) {
        this.sendPsd = sendPsd;
    }

    /**
     * Getter for property isRememberSendPsd.
     * 
     * @return Value of property isRememberSendPsd.
     */
    public boolean isRememberSendPsd() {
        return this.isRememberSendPsd;
    }

    /**
     * Setter for property isRememberSendPsd.
     * 
     * @param isRememberSendPsd
     *            New value of property isRememberSendPsd.
     */
    public void setIsRememberSendPsd(
            boolean isRememberSendPsd) {
        this.isRememberSendPsd = isRememberSendPsd;
    }

    /**
     * Getter for property connectionIndex.
     * 
     * @return Value of property connectionIndex.
     */
    public byte getConnectionIndex() {
        return this.connectionIndex;
    }

    /**
     * Setter for property connectionIndex.
     * 
     * @param connectionIndex
     *            New value of property connectionIndex.
     */
    public void setConnectionIndex(
            byte connectionIndex) {
        this.connectionIndex = connectionIndex;
    }

    /**
     * Getter for property isUseModem.
     * 
     * @return Value of property isUseModem.
     */
    public boolean isUseModem() {
        return this.isUseModem;
    }

    /**
     * Setter for property isUseModem.
     * 
     * @param isUseModem
     *            New value of property isUseModem.
     */
    public void setIsUseModem(
            boolean isUseModem) {
        this.isUseModem = isUseModem;
    }

    /**
     * Getter for property dialUpConnectionIndex.
     * 
     * @return Value of property dialUpConnectionIndex.
     */
    public int getDialUpConnectionIndex() {
        return this.dialUpConnectionIndex;
    }

    /**
     * Setter for property dialUpConnectionIndex.
     * 
     * @param dialUpConnectionIndex
     *            New value of property dialUpConnectionIndex.
     */
    public void setDialUpConnectionIndex(
            int dialUpConnectionIndex) {
        this.dialUpConnectionIndex = dialUpConnectionIndex;
    }

    /**
     * Getter for property smtpPort.
     * 
     * @return Value of property smtpPort.
     */
    public String getSmtpPort() {
        return this.smtpPort;
    }

    /**
     * Setter for property smtpPort.
     * 
     * @param smtpPort
     *            New value of property smtpPort.
     */
    public void setSmtpPort(
            String smtpPort) {
        this.smtpPort = smtpPort;
    }

    /**
     * Getter for property pop3Port.
     * 
     * @return Value of property pop3Port.
     */
    public String getPop3Port() {
        return this.pop3Port;
    }

    /**
     * Setter for property pop3Port.
     * 
     * @param pop3Port
     *            New value of property pop3Port.
     */
    public void setPop3Port(
            String pop3Port) {
        this.pop3Port = pop3Port;
    }

    /**
     * Getter for property serverTimeOut.
     * 
     * @return Value of property serverTimeOut.
     */
    public int getServerTimeOut() {
        return this.serverTimeOut;
    }

    /**
     * Setter for property serverTimeOut.
     * 
     * @param serverTimeOut
     *            New value of property serverTimeOut.
     */
    public void setServerTimeOut(
            int serverTimeOut) {
        this.serverTimeOut = serverTimeOut;
    }

    /**
     * Getter for property isLeaveMailOnServer.
     * 
     * @return Value of property isLeaveMailOnServer.
     */
    public boolean isLeaveMailOnServer() {
        return this.isLeaveMailOnServer;
    }

    /**
     * Setter for property isLeaveMailOnServer.
     * 
     * @param isLeaveMailOnServer
     *            New value of property isLeaveMailOnServer.
     */
    public void setIsLeaveMailOnServer(
            boolean isLeaveMailOnServer) {
        this.isLeaveMailOnServer = isLeaveMailOnServer;
    }

    /**
     * Getter for property isAppointedDelTime.
     * 
     * @return Value of property isAppointedDelTime.
     */
    public boolean isAppointedDelTime() {
        return this.isAppointedDelTime;
    }

    /**
     * Setter for property isAppointedDelTime.
     * 
     * @param isAppointedDelTime
     *            New value of property isAppointedDelTime.
     */
    public void setIsAppointedDelTime(
            boolean isAppointedDelTime) {
        this.isAppointedDelTime = isAppointedDelTime;
    }

    /**
     * Getter for property numberOfDays.
     * 
     * @return Value of property numberOfDays.
     */
    public int getNumberOfDays() {
        return this.numberOfDays;
    }

    /**
     * Setter for property numberOfDays.
     * 
     * @param numberOfDays
     *            New value of property numberOfDays.
     */
    public void setNumberOfDays(
            int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    /**
     * Getter for property rootPath.
     * 
     * @return Value of property rootPath.
     */
    public String getRootPath() {
        return this.rootPath;
    }

    /**
     * Setter for property rootPath.
     * 
     * @param rootPath
     *            New value of property rootPath.
     */
    public void setRootPath(
            String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Getter for property isCheckAllNewMail.
     * 
     * @return Value of property isCheckAllNewMail.
     */
    public boolean isCheckAllNewMail() {
        return this.isCheckAllNewMail;
    }

    /**
     * Setter for property isCheckAllNewMail.
     * 
     * @param isCheckAllNewMail
     *            New value of property isCheckAllNewMail.
     */
    public void setIsCheckAllNewMail(
            boolean isCheckAllNewMail) {
        this.isCheckAllNewMail = isCheckAllNewMail;
    }

    /**
     * Getter for property isDefault.
     * 
     * @return Value of property isDefault.
     */
    public boolean isDefault() {
        return this.isDefault;
    }

    /**
     * Setter for property isDefault.
     * 
     * @param isDefault
     *            New value of property isDefault.
     */
    public void setIsDefault(
            boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Getter for property lastTime.
     * 
     * @return Value of property lastTime.
     */
    public Date getLastTime() {
        return this.lastTime;
    }

    /**
     * Setter for property lastTime.
     * 
     * @param lastTime
     *            New value of property lastTime.
     */
    public void setLastTime(
            Date lastTime) {
        this.lastTime = lastTime;
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
                } else if (type.equalsIgnoreCase("byte")) {
                    sb.append(fields[i].getByte(this));
                } else if (type.equalsIgnoreCase("java.lang.String")) {
                    sb.append((String) fields[i].get(this));
                } else if (type.equalsIgnoreCase("java.util.Date")) {
                    sb.append(((Date) fields[i].get(this)).toString());
                }
                sb.append("; ");
            }
        } catch (IllegalAccessException e) {
            // e.printStackTrace();
            sb.setLength(0);
            sb.append("IllegalAccessException at emo.pim.pimmodel.datasource.AccountInfo");
        }
        return sb.toString();
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
        AccountInfo account = new AccountInfo();
        Field[] fields = account.getClass().getDeclaredFields();
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
                            fields[i - 1].setInt(account, rs.getInt(i));
                        } else if (fieldType.equalsIgnoreCase("boolean")) {
                            fields[i - 1].setBoolean(account, rs.getBoolean(i));
                        } else if (fieldType.equalsIgnoreCase("byte")) {
                            fields[i - 1].setByte(account, rs.getByte(i));
                        } else if (fieldType.equalsIgnoreCase("java.lang.String")) {
                            fields[i - 1].set(account, rs.getString(i));
                        } else if (fieldType.equalsIgnoreCase("java.util.Date")) {
                            Timestamp stamp = rs.getTimestamp(i);
                            Date date = new Date(stamp.getTime());
                            fields[i - 1].set(account, date);
                        }
                    }
                }
            }
        } catch (Exception e) {
            account = null;
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
        return ModelCons.ACCOUNT_INFO_DATA;
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
                } else if (type.equalsIgnoreCase("byte")) {
                    statement.setByte(i + 1, fields[i].getByte(this));
                } else if (type.equalsIgnoreCase("java.lang.String")) {
                    statement.setString(i + 1, (String) fields[i].get(this));
                } else if (type.equalsIgnoreCase("java.util.Date")) {
                    Date date = (Date) fields[i].get(this);
                    Timestamp stamp = new Timestamp(date == null ? 0 : date.getTime());
                    statement.setTimestamp(i + 1, stamp);
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

    /*
     * protected void test() { String[] dbFieldName = emo.pim.pimmodel.database.DefaultDBInfo.ACCOUNT_TABLE_FIELD1;
     * Class calss = getClass(); Field[] fields = getClass().getDeclaredFields(); int len1 = dbFieldName.length; int
     * len2 = fields.length; for (int i = 0; i < len2; i++) { String name = fields[i].getName(); boolean b = false; for
     * (int j = 0; j < len1; j++) { if (name.equalsIgnoreCase(dbFieldName[j])) { b = true; break; } } if (b) {
     * A.s(" the field '"+name+"' is ok!"); } else { A.s(" hahaha@@@ the field '"+name+"' is failed!!!"); } } } public
     * static void main(String[] args) { AccountInfo pool = new AccountInfo(); pool.test(); System.exit(0); }
     */
    private int id;

    // NOTE:此方法没有被使用
    // private int series;
    // 常规页中
    // 帐号名字
    private String accountName;
    // 是pop3还是smtp
    private boolean isPop3;
    // 服务器
    private String serverName;
    // 显示姓名
    private String displayName;
    // 单位名字
    private String organizationName;
    // 邮件地址
    private String emailAddress;
    // 回复地址
    private String replyToAddress;
    // 服务器页
    // 接收邮件服务器名
    private String receiveServerName;
    // 发送邮件服务器名
    private String sendServerName;
    // 接收服务器帐户名
    private String receiveAccountName;
    // 接收服务器密码
    private String receivePsd;
    // 是否记住接收服务器密码
    private boolean isRememberReceivePsd;
    // 发送邮件服务器是否要求身份验证
    private boolean isNeedValidate;
    // 登陆信息是否使用与邮件接收服务器相同的信息
    private boolean isSameSet;
    // 使用不同设置的时候的帐户名
    private String sendAccountName;
    // 使用不同设置的时候的密码
    private String sendPsd;
    // 使用不同设置的时候是否记住密码
    private boolean isRememberSendPsd;
    // 连接页
    // 使用局域网连接还是电话线连接
    private byte connectionIndex;
    // 是否局域网不可能使用时使用调制解调器连接
    private boolean isUseModem;
    // 使用以下拨号网络连接
    private int dialUpConnectionIndex;
    // 高级页
    // 服务器端口号smtp端口
    private String smtpPort;
    // 服务器端口号
    private String pop3Port;
    // 服务器超时
    private int serverTimeOut;
    // 是否在服务器上保留邮件的副本
    private boolean isLeaveMailOnServer;
    // 是否在指定的几天后删除
    private boolean isAppointedDelTime;
    // 指定的天数
    private int numberOfDays;
    // IMAP页
    // 根文件夹路径
    private String rootPath;
    // 是否检查所有文件夹中的新邮件
    private boolean isCheckAllNewMail;
    // 是否时缺省帐号
    private boolean isDefault;
    // 此帐号接收的最后一封邮件的时间
    private Date lastTime;
}
