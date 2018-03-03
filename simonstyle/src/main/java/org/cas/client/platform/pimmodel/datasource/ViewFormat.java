package org.cas.client.platform.pimmodel.datasource;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.cas.client.platform.casutil.ModelCons;

public class ViewFormat implements Cloneable, Serializable, IDbDataSource {
    /** Creates a new instance of ViewFormat */
    public ViewFormat() {
    }

    /**
     * Getter for property infolder.
     * 
     * @return Value of property infolder.
     */
    public int getFolderID() {
        return folderID;
    }

    /**
     * Setter for property infolder.
     * 
     * @param prmFolderID
     *            New value of property infolder.
     */
    public void setInfolder(
            int prmFolderID) {
        folderID = prmFolderID;
    }

    /**
     * Getter for property appType.
     * 
     * @return Value of property appType.
     */
    public int getAppType() {
        return appType;
    }

    /**
     * Setter for property appType.
     * 
     * @param appType
     *            New value of property appType.
     */
    public void setAppType(
            int appType) {
        this.appType = appType;
    }

    /**
     * Getter for property appSubType.
     * 
     * @return Value of property appSubType.
     */
    public int getAppSubType() {
        return appSubType;
    }

    /**
     * Setter for property appSubType.
     * 
     * @param appSubType
     *            New value of property appSubType.
     */
    public void setAppSubType(
            int appSubType) {
        this.appSubType = appSubType;
    }

    /**
     * Getter for property modeType.
     * 
     * @return Value of property modeType.
     */
    public int getModeType() {
        return modeType;
    }

    /**
     * Setter for property modeType.
     * 
     * @param modeType
     *            New value of property modeType.
     */
    public void setModeType(
            int modeType) {
        this.modeType = modeType;
    }

    /**
     * Getter for property fontSize.
     * 
     * @return Value of property fontSize.
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Setter for property fontSize.
     * 
     * @param fontSize
     *            New value of property fontSize.
     */
    public void setFontSize(
            int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Getter for property fontName.
     * 
     * @return Value of property fontName.
     */
    public java.lang.String getFontName() {
        return fontName;
    }

    /**
     * Setter for property fontName.
     * 
     * @param fontName
     *            New value of property fontName.
     */
    public void setFontName(
            java.lang.String fontName) {
        this.fontName = fontName;
    }

    /**
     * Getter for property fontStyle.
     * 
     * @return Value of property fontStyle.
     */
    public int getFontStyle() {
        return fontStyle;
    }

    /**
     * Setter for property fontStyle.
     * 
     * @param fontStyle
     *            New value of property fontStyle.
     */
    public void setFontStyle(
            int fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * 返回这个对象的克隆
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * 判断对象的相等
     */
    public boolean equals(
            Object obj) {
        if (obj == null || !(obj instanceof ViewFormat))
            return false;

        ViewFormat format = (ViewFormat) obj;
        boolean nameB = (fontName == null) ? fontName == format.getFontName() : fontName.equals(format.getFontName());
        boolean infolderB = (folderID == 0) ? folderID == format.getFolderID() : folderID == format.getFolderID();

        return fontSize == format.getFontSize() && fontStyle == format.getFontStyle() && nameB && infolderB
                && appType == format.getAppType() && appSubType == format.getAppSubType()
                && modeType == format.getModeType() && id == format.getRecordID();
    }

    /**
     * 打印对象的内容
     */
    public String toString() {
        return "id :" + id + ", appType :" + appType + ", appSubType :" + appSubType + ", modetype :" + modeType
                + ", fontSize :" + fontSize + ", fontName :" + fontName + ", fontStyle :" + fontStyle;
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
        ViewFormat viewFormat = new ViewFormat();
        Field[] fields = viewFormat.getClass().getDeclaredFields();
        int fieldCount = fields.length;
        int columnCount = rsmd.getColumnCount();
        try {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                for (int j = 0; j < fieldCount; j++) {
                    String fieldName = fields[j].getName();
                    if (!(columnName.equalsIgnoreCase(fieldName))) {
                        continue;
                    }
                    String fieldType = fields[j].getType().getName();
                    // A.s(" fieldName : "+fieldName);
                    // A.s(" fieldType ; "+fieldType);
                    if (fieldType.equalsIgnoreCase("int")) {
                        fields[i - 1].setInt(viewFormat, rs.getInt(i));
                    } else if (fieldType.equalsIgnoreCase("boolean")) {
                        fields[i - 1].setBoolean(viewFormat, rs.getBoolean(i));
                    } else if (fieldType.equalsIgnoreCase("java.lang.String")) {
                        fields[i - 1].set(viewFormat, rs.getString(i));
                    } else if (fieldType.equalsIgnoreCase("java.awt.Color")) {
                        fields[i - 1].set(viewFormat, new Color(rs.getInt(i)));
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
        return null;
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
        return ModelCons.VIEW_FORMAT_DATA;
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
                } else if (type.equalsIgnoreCase("java.awt.Color")) {
                    statement.setInt(i + 1, ((Color) fields[i].get(this)).getRGB());
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

    /**
     * Getter for property haveStrikethrough.
     * 
     * @return Value of property haveStrikethrough.
     */
    public boolean isHaveStrikethrough() {
        return haveStrikethrough;
    }

    /**
     * Setter for property haveStrikethrough.
     * 
     * @param haveStrikethrough
     *            New value of property haveStrikethrough.
     */
    public void setHaveStrikethrough(
            boolean haveStrikethrough) {
        this.haveStrikethrough = haveStrikethrough;
    }

    /**
     * Getter for property fontColor.
     * 
     * @return Value of property fontColor.
     */
    public java.awt.Color getFontColor() {
        if (fontColor == null) {
            fontColor = Color.black;
        }
        return fontColor;
    }

    /**
     * Setter for property fontColor.
     * 
     * @param fontColor
     *            New value of property fontColor.
     */
    public void setFontColor(
            java.awt.Color fontColor) {
        this.fontColor = fontColor;
    }

    private int id;// 视图信息ID
    private int appType; // 应用类型，如：联系人，任务，日历
    private int appSubType;// 应用子类型，
    private int modeType;// 样式类型
    private int folderID;// 此viewformat属于哪个文件夹下
    private int fontSize; // 字体尺寸
    private String fontName;// 字体名字
    private int fontStyle; // 字体样式
    private boolean haveStrikethrough;// 是否有删除线
    private Color fontColor; // 字体颜色
}
