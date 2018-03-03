package org.cas.client.platform.pimmodel;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMDBUtil;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimmodel.datasource.FilterInfo;
import org.cas.client.platform.pimmodel.datasource.IDbDataSource;
import org.cas.client.platform.pimmodel.datasource.MailRulePool;
import org.cas.client.platform.pimmodel.datasource.ViewFormat;

/**
 * 本类通过FolderID与文件夹对应,用来描述每个文件夹的显示内容.
 * 
 * @NOTE:注意本类的变量名是与数据库中的字段结构对应的,所以不可改动,也不可加密.
 */
public class PIMViewInfo implements IDbDataSource, Cloneable, Releasable {
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
     * @return String 得到数据库可以解析的用于update记录的语句
     */
    public String createSqlUpdate(
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
        /*
         * StringBuffer sb = new StringBuffer(); sb.append("UPDATE ").append(tableName).append(" SET DISPLAYFIELDS = ?,
         * ") .append("FIELDSWIDTH = ?, ").append("SORT = ?, ") .append("FILTER_INFO = ?, ").append("VIEW_FORMAT = ?, ")
         * .append("PREVIEW_SCALE = ? ").append(" WHERE APPTYPE = ")
         * .append(appType).append(" AND APPSUBTYPE = ").append(appSubType). append(" AND VIEWTYPE = 0"); return
         * sb.toString();
         */
    }

    /**
     * @param statement
     *            数据库生成一个预备语句后，更据类的特点对每一个待定的变量 赋一个值。注意：要与 <code>createSqlPreparedUpdate<code>
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
                } else if (type.equalsIgnoreCase("org.cas.client.platform.pimmodel.datasource.FilterInfo")) {
                    FilterInfo pool = (FilterInfo) fields[i].get(this);
                    byte[] poolByte = PIMDBUtil.encodeSerializeObjectToByteArray(pool);
                    if (poolByte == null) {
                        poolByte = new byte[0];
                    }
                    statement.setBytes(i + 1, poolByte);
                }
            }
        } catch (IllegalAccessException e) {
        }
        /*
         * String sortString = sortCriteria; if (sortString == null) { sortString = PIMUtility.EMPTYSTR; } byte[]
         * filterByte = new byte[0]; if (filter != null) { filterByte =
         * PIMDBUtil.encodeSerializeObjectToByteArray(filter); } byte[] formatByte = new byte[0]; if (viewFormat !=
         * null) { formatByte = PIMDBUtil.encodeSerializeObjectToByteArray(viewFormat); } statement.setString(1,
         * fieldNames); statement.setString(2, fieldWidths); statement.setString(3, sortString); statement.setBytes(4,
         * filterByte); statement.setBytes(5, formatByte); statement.setInt(6, previewScale);
         */
    }

    /**
     * 通过数据库查询的结果得到相应的对象实例
     * 
     * @param rs
     *            通过数据库查询得出的结果集
     * @param rsmd
     *            通过结果集得到的结果集的描述
     * @Exception SQLException 要抛出SQL的异常
     * @TODO:1、速度可以优化. 2、发现构造ViewInfo时候，对于HasEditor属性构造出来后总是true，由于流程理解较难，赞不知何故。 3、如果将实例变量useScope去掉,if
     *                 (fieldType.equalsIgnoreCase("int")) fields[i - 1].setInt(this, rs.getInt(i));就抛异常???
     */
    public void enrichFromRS(
            ResultSet rs,
            ResultSetMetaData rsmd) throws SQLException {
        Field[] fields = getClass().getDeclaredFields();
        int fieldCount = fields.length;
        int columnCount = rsmd.getColumnCount();
        try {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                for (int j = 0; j < fieldCount; j++) {
                    if (!(columnName.equalsIgnoreCase(fields[j].getName())))
                        continue;

                    String fieldType = fields[j].getType().getName();
                    if (fieldType.equalsIgnoreCase("int"))
                        fields[j].setInt(this, rs.getInt(i));
                    else if (fieldType.equalsIgnoreCase("boolean"))
                        fields[j].setBoolean(this, rs.getBoolean(i));
                    else if (fieldType.equalsIgnoreCase("java.lang.String"))
                        fields[j].set(this, rs.getString(i));
                    else if (fieldType.equalsIgnoreCase("org.cas.client.platform.pimmodel.datasource.FilterInfo")) {
                        byte[] filterByte = rs.getBytes(i);
                        FilterInfo filter = null;
                        if (filterByte != null) {
                            filter = (FilterInfo) PIMDBUtil.decodeByteArrayToSerializeObject(filterByte);
                            // A.s("filter : "+filter);
                            // if (filter == null)
                            // A.s("为空");
                            // java.util.ArrayList tmpList = filter.getCondiList();
                            //
                            // for (int s = tmpList.size() - 1; s >= 0; s--)
                            // A.s("tmpList: " + tmpList.get(s).toString());
                        }
                        fields[j].set(this, filter);
                    } else if (fieldType.equalsIgnoreCase("org.cas.client.platform.pimmodel.datasource.ViewFormat")) {
                        byte[] formatByte = rs.getBytes(i);
                        ViewFormat viewFormat = null;
                        if (formatByte != null)
                            viewFormat = (ViewFormat) PIMDBUtil.decodeByteArrayToSerializeObject(formatByte);
                        fields[j].set(this, viewFormat);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 每个类都对应一个表，每个表都有一个索引，通过这个方法返回索引并得到表名 */
    public int getDataType() {
        return ModelCons.VIEW_INFO_DATA;
    }

    /** 将需要得ID赋给对象，此id是model赋值并且在一张表中是唯一的 */
    public void setRecordID(
            int id) {
        this.id = id;
    }

    /**
     * 得到对象在库中存在的id
     * 
     * @NOTE:该ID不是ViewInfo对应的Records的ID，而是ViewInfo本身作为记录存入数据库时对应的ID。
     */
    public int getRecordID() {
        return id;
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     */
    public void release() {
        filter = null;
        viewFormat = null;
        fieldNames = null;
        fieldWidths = null;
        viewName = null;
        sortCriteria = null;
    }

    /***/
    public Object clone() {
        PIMViewInfo temp = new PIMViewInfo();
        temp.setCustomize(customize);
        temp.setFieldNames(fieldNames);
        temp.setFieldWidths(fieldWidths);
        // temp.setFilterCritia(filterCriteria);
        if (this.filter != null)
            temp.setFilterInfo(((FilterInfo) this.filter.clone()));
        if (this.viewFormat != null)
            temp.setViewFormat(((ViewFormat) this.viewFormat.clone()));
        // temp.setGroupBy(groupBy);
        temp.setSortCritia(sortCriteria);
        temp.setPreviewScale(previewScale);
        temp.setRecordID(id);
        temp.setViewName(viewName);
        temp.setViewType(viewType);
        // temp.setDisplay(display);
        temp.setHasEditor(hasEditor);
        temp.setHasPreview(hasPreview);
        temp.setFolderID(folderID);
        temp.setMessageNumber(number);
        temp.setUnreadMessageNumber(unreaded);
        temp.setSycnSetting(sycnSetting);
        temp.setServerFolderPath(serverFolder);

        return temp;
    }

    /**
     */
    public boolean equals(
            Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof PIMViewInfo))
            return false;

        PIMViewInfo temp = (PIMViewInfo) obj;

        String fn = temp.getViewName();
        boolean viewNameB = (fn == null ? fn == viewName : fn.equalsIgnoreCase(viewName));

        fn = temp.getFieldNames();
        boolean fieldNameB = (fn == null ? fn == fieldNames : fn.equalsIgnoreCase(fieldNames));

        fn = temp.getFieldWidths();
        boolean fieldWidthB = (fn == null ? fn == fieldWidths : fn.equalsIgnoreCase(fieldWidths));

        // fn = temp.getFilterCritia();
        // boolean filterCritiaB = (fn == null ? fn == filterCriteria :
        // fn.equalsIgnoreCase(filterCriteria));

        // fn = temp.getGroupBy();
        // boolean groupByB = (fn == null ? fn == groupBy :
        // fn.equalsIgnoreCase(groupBy));

        fn = temp.getSortCritia();
        boolean sortCritiaB = (fn == null ? fn == sortCriteria : fn.equalsIgnoreCase(sortCriteria));
        boolean filterB = filter == null ? filter == temp.getFilterInfo() : filter.equals(temp.getFilterInfo());
        boolean formatB =
                viewFormat == null ? viewFormat == temp.getViewFormat() : viewFormat.equals(temp.getViewFormat());
        return temp.getRecordID() == id && temp.getViewType() == viewType && viewNameB && fieldNameB && fieldWidthB
                && sortCritiaB &&
                // temp.getDisplay() == display &&
                temp.hasEditor() == hasEditor && temp.hasPreview() == hasPreview && filterB && formatB;
    }

    // 提供toString方法供调试用
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
                } else {
                    sb.append(fields[i].get(this));
                }
                sb.append("; ");
            }
        } catch (IllegalAccessException e) {
            // e.printStackTrace();
        }
        return sb.toString();
        /*
         * StringBuffer sb = new StringBuffer(); sb.append("fieldNames :").append(fieldNames).append(", fieldWidths :")
         * .append(fieldWidths).append(", viewName :").append(viewName)
         * .append(", sortCriteria :").append(sortCriteria).append(", filterCriteria
         * :") .append(filterCriteria).append(", groupBy :").append(groupBy) .append(", viewType
         * :").append(viewType).append(", useScope :") .append(useScope).append(", viewID :").append(viewID) .append(",
         * appType :").append(appType).append(", appSubType :")
         * .append(appSubType).append(", customize :").append(customize)
         * .append(", hasEditor :").append(hasEditor).append(", hasPreview :")
         * .append(hasPreview).append(", filter : ").append(filter).append(", viewFormat :") .append(viewFormat); return
         * sb.toString();
         */
    }

    /**
     * Getter for property fieldNames.
     * 
     * @return Value of property fieldNames.
     */
    public String getFieldNames() {
        return this.fieldNames;
    }

    /**
     * Setter for property fieldNames.
     * 
     * @param fieldNames
     *            New value of property fieldNames.
     */
    public void setFieldNames(
            String fieldNames) {
        this.fieldNames = fieldNames;
    }

    /**
     * Getter for property viewName.
     * 
     * @return Value of property viewName.
     */
    public String getViewName() {
        return this.viewName;
    }

    /**
     * Setter for property viewName.
     * 
     * @param viewName
     *            New value of property viewName.
     */
    public void setViewName(
            String viewName) {
        this.viewName = viewName;
    }

    /**
     * Getter for property viewType.
     * 
     * @return Value of property viewType.
     */
    public int getViewType() {
        return viewType;
    }

    /**
     * Setter for property viewType.
     * 
     * @param viewType
     *            New value of property viewType.
     */
    public void setViewType(
            int viewType) {
        this.viewType = viewType;
    }

    /**
     * Getter for property sortCriteria.
     * 
     * @return Value of property sortCriteria.
     */
    public String getSortCritia() {
        return this.sortCriteria;
    }

    /**
     * Setter for property sortCriteria.
     * 
     * @param sortCriteria
     *            New value of property sortCriteria.
     */
    public void setSortCritia(
            String sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    /**
     * Getter for property FilterInfo.
     * 
     * @return Value of property FilterInfo.
     */
    public FilterInfo getFilterInfo() {
        return this.filter;
    }

    /**
     * Setter for property FilterInfo.
     * 
     * @param FilterInfo
     *            New value of property FilterInfo.
     */
    public void setFilterInfo(
            FilterInfo info) {
        this.filter = info;
    }

    //
    public void setViewFormat(
            ViewFormat format) {
        this.viewFormat = format;
    }

    //
    public ViewFormat getViewFormat() {
        return viewFormat;
    }

    /**
     * 判断是否是自定义视图信息
     * 
     * @return boolean
     */
    public boolean isCustomize() {
        return customize;
    }

    /**
     * 设置自定义视图信息是否是自定义
     * 
     * @param boolean custom
     */
    public void setCustomize(
            boolean custom) {
        this.customize = custom;
    }

    /**
     * Getter for property fieldWidths.
     * 
     * @return Value of property fieldWidths.
     */
    public String getFieldWidths() {
        return this.fieldWidths;
    }

    /**
     * Setter for property fieldWidths.
     * 
     * @param fieldWidths
     *            New value of property fieldWidths.
     */
    public void setFieldWidths(
            String fieldWidths) {
        this.fieldWidths = fieldWidths;
    }

    /**
     */
    public void setHasEditor(
            boolean prmHasEditor) {
        hasEditor = prmHasEditor;
    }

    /**
     */
    public boolean hasEditor() {
        return false;// hasEditor;
    }

    /**
     * 返回邮件视图中是否有预览面板
     */
    public boolean hasPreview() {
        return hasPreview;
    }

    /**
     * 设置邮件视图中的预览标志
     */
    public void setHasPreview(
            boolean previewFlag) {
        hasPreview = previewFlag;
    }

    /**
     * Getter for property previewScale.
     * 
     * @return Value of property previewScale.
     */
    public int getPreviewScale() {
        return this.previewScale;
    }

    /**
     * Setter for property previewScale.
     * 
     * @param previewScale
     *            New value of property previewScale.
     */
    public void setPreviewScale(
            int previewScale) {
        this.previewScale = previewScale;
    }

    /**
     * 得到viewinfo中的排序字串
     */
    public String getSortString() {
        String defaultSort = "ID";

        String tmpTableName = (String) CustOpts.custOps.APPNameVec.get(CASUtility.getAppIndexByFolderID(folderID));// 得到ViewInfo中的App对应的表名。
        if (tmpTableName == null || sortCriteria == null || sortCriteria.length() == 0)// 并进行有效性验证。
            return defaultSort;

        String[] tmpFieldNameAry = MainPane.getApp(tmpTableName).getAppFields();

        String sortString = CASUtility.EMPTYSTR;
        String deli = ModelDBCons.DELIMITER;
        StringTokenizer token = new StringTokenizer(sortCriteria, deli);
        try {
            while (token.hasMoreTokens()) {
                String str = token.nextToken().trim(); // 因为ViewInfo中是用int值来表示字段名的，所以要将sortCriteria中以
                int index = Integer.parseInt(str); // 逗号分割的数字解析出来，再从tmpFieldNameVec中的对应位置处知道字段名。
                sortString = sortString.concat((String) tmpFieldNameAry[index]); // 之所以用数字对应字段名是为了省内存。
                str = token.nextToken().trim();
                index = Integer.parseInt(str);
                String mark = " ASC, ";
                if (index == ModelDBCons.DESCEND) {
                    mark = " DESC, ";
                }
                sortString = sortString.concat(mark);
            }
            int index = sortString.lastIndexOf(',');
            sortString = sortString.substring(0, index);
            if (sortString.length() == 0) {
                return defaultSort;
            }
            return sortString;
        } catch (Exception e) {
            return defaultSort;
        }
    }

    /**
     */
    public String getFilterString() {
        if (filter == null) {
            return null;
        }
        return filter.getFilterString();
    }

    public int getAppIndex() {
        return CASUtility.getAppIndexByFolderID(folderID);
    }

    public void setAppIndex(
            int prmIndex) {
        if (prmIndex < 0)
            folderID = prmIndex;
        else
            folderID = CASUtility.getAPPNodeID(prmIndex);
    }

    /**
     * Getter for property unread number.
     * 
     * @return Value of property unread number.
     */
    public int getUnreadMessageNumber() {
        return unreaded;
    }

    /**
     * Setter for property unread number.
     * 
     * @param folderID
     *            New value of property unread number.
     */
    public void setUnreadMessageNumber(
            int unreadNumber) {
        this.unreaded = unreadNumber;
    }

    /**
     * Getter for property messageNumber.
     * 
     * @return Value of property messageNumber.
     */
    public int getMessageNumber() {
        return number;
    }

    /**
     * Setter for property messageNumber.
     * 
     * @param folderID
     *            New value of property messageNumber.
     */
    public void setMessageNumber(
            int messageNumber) {
        this.number = messageNumber;
    }

    /**
     * Getter for property folderPath.
     * 
     * @return Value of property folderPath.
     */
    public int getPathID() {
        return folderID;
    }

    /**
     * Setter for property folderPath.
     * 
     * @param folderID
     *            New value of property folderPath.
     */
    public void setFolderID(
            int prmFolderID) {
        folderID = prmFolderID;
    }

    /**
     * Getter for property synchronization setting.
     * 
     * @return Value of property synchronization setting.
     */
    public int getSycnSetting() {
        return sycnSetting;
    }

    /**
     * Setter for property synchronization setting.
     * 
     * @param folderID
     *            New value of property synchronization setting.
     */
    public void setSycnSetting(
            int prmSycnSetting) {
        sycnSetting = prmSycnSetting;
    }

    /**
     * Getter for property severFolderPath.
     * 
     * @return Value of property severFolderPath.
     */
    public String getServerFolderPath() {
        return serverFolder;
    }

    /**
     * Setter for property severFolderPath.
     * 
     * @param folderID
     *            New value of property severFolderPath.
     */
    public void setServerFolderPath(
            java.lang.String serverFolder) {
        this.serverFolder = serverFolder;
    }

    private int id; // 视图信息ID
    private int viewType; // 视图类型
    private int useScope; // 引用范围 //@NOTE:???这个变量删除就在切换应用时抛异常.????
    private int previewScale = 38; // 有预览时的预览显示比例
    private boolean customize; // 是否是自定义
    private boolean hasEditor; // 是否显示快速编辑栏
    private boolean hasPreview; // 邮件视图中是否有预览面板
    private String fieldNames; // 该字符串中每个字段以，分隔
    private String fieldWidths; // 该字符串中每个字段以，分隔
    private String viewName; // 视图名称
    private String sortCriteria; // 排序准则
    private FilterInfo filter;
    private ViewFormat viewFormat;
    private int folderID; // 文件夹的唯一标识符号,@NOTE:本来用路径标识的,但String类型在有些数据库中不能建立高效的索引,故改为int类型的ID.
    private int number; // IMAP帐号对应视图中的信件数
    private int unreaded; // IMAP帐号对应视图中未读的信件数
    private int sycnSetting; // IMAP帐号对应视图的同步设置
    private String serverFolder; // IMAP帐号对应视图对应服务器上的位置
}

/**
 * 内部数据字段，除了显示的字段外，视图往往还要求一些内部的数据，在显示视图时内部使用例如READED字段等 2004-9-15 note ： 这些字段不保存到数据库中，所以作为视图的外部状态即可，规则
 * 
 * 实现clone时， 包含此字段 实现equals时，包含此字段
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code
 * and Comments
 */
// private String innerFieldName;
// private String filterCriteria;//Critia; //筛选条件
// private String groupBy; //分组依据
// private Properties display; //显示属性
/*
 * protected void test() { String[] dbFieldName = DefaultDBInfo.VIEWINFO_TABLE_FIELD; Class calss = getClass(); Field[]
 * fields = getClass().getDeclaredFields(); int len1 = dbFieldName.length; int len2 = fields.length; for (int i = 0; i <
 * len2; i++) { String name = fields[i].getName(); boolean b = false; for (int j = 0; j < len1; j++) { if
 * (name.equalsIgnoreCase(dbFieldName[j])) { b = true; break; } } if (b) { A.s(" the field '"+name+"' is
 * ok!"); } else { A.s(" hahaha@@@ the field '"+name+"' is failed!!!"); } } } public static void main(String[] args) {
 * PIMViewInfo info = new PIMViewInfo(); info.test(); System.exit(0); }
 */

/**
 * Getter for property filterCriteria.
 * 
 * @return Value of property filterCriteria. public String getFilterCritia() { return this.filterCriteria; }
 */

/**
 * Setter for property filterCriteria.
 * 
 * @param filterCriteria
 *            New value of property filterCriteria. public void setFilterCritia(String filterCriteria) {
 *            this.filterCriteria = filterCriteria; }
 */

/**
 * Getter for property groupBy.
 * 
 * @return Value of property groupBy. public String getGroupBy() { return this.groupBy; }
 */

/**
 * Setter for property groupBy.
 * 
 * @param groupBy
 *            New value of property groupBy. public void setGroupBy(String groupBy) { this.groupBy = groupBy; }
 */

/**
 * Setter for property display.
 * 
 * @param display
 *            New value of property display. public void setDisplay(Properties display) { this.display = display; }
 */

// /**Getter for property useScope.
// * @return Value of property useScope.
// */
// public int getUseScope()
// {
// return this.useScope;
// }

// /**Setter for property useScope.
// * @param useScope New value of property useScope.
// */
// public void setUseScope(int prmUseScope)
// {
// useScope = prmUseScope;
// }
