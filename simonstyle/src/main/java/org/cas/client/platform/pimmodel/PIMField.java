package org.cas.client.platform.pimmodel;

class PIMField {
    /**
     * Creates a new instance of PIMField
     */
    PIMField(int fieldID, String fieldName, int fieldType, boolean customize, int type, boolean primaryKey,
            int subType, boolean normal, boolean showing) {
        this.fieldID = fieldID;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.type = type;
        this.primaryKey = primaryKey;
        // 以下暂时不用
        this.subType = subType;
        this.normal = normal;
        this.showing = showing;
    }

    /**
     * Creates a new instance of PIMField
     */
    PIMField(int fieldID, String fieldName, int fieldType, boolean customize, int type, boolean primaryKey,
            int subType, boolean normal) {
        this(fieldID, fieldName, fieldType, customize, type, primaryKey, subType, normal, true);
    }

    /**
     * Creates a new instance of PIMField
     */
    PIMField(int fieldID, String fieldName, int fieldType, boolean customize, int type, boolean primaryKey, int subType) {
        this(fieldID, fieldName, fieldType, customize, type, primaryKey, subType, false, true);
    }

    /**
     * Creates a new instance of PIMField
     */
    PIMField(int fieldID, String fieldName, int fieldType, boolean customize, int type, boolean primaryKey) {
        this(fieldID, fieldName, fieldType, customize, type, primaryKey, -1, false, true);
    }

    /**
     * Creates a new instance of PIMField
     */
    PIMField(int fieldID, String fieldName, int fieldType, boolean customize, int type) {
        this(fieldID, fieldName, fieldType, customize, type, false, -1, false, true);
    }

    /**
     * Creates a new instance of PIMField
     */
    PIMField(int fieldID, String fieldName, int fieldType, boolean customize) {
        this(fieldID, fieldName, fieldType, customize, 0, false, -1, false, true);
    }

    /**
     * Creates a new instance of PIMField
     */
    PIMField(int fieldID, String fieldName, int fieldType) {
        this(fieldID, fieldName, fieldType, false, 0, false, -1, false, true);
    }

    /**
     * 返回字段名
     * 
     * @return: String
     */
    String getFieldName() {
        return fieldName;
    }

    /**
     * 返回字段类型
     * 
     * @return: String
     */
    int getFieldType() {
        return fieldType;
    }

    /**
     * 返回字段的子类型。如：联系人常用中的地址字段
     * 
     * @return: int
     */
    int getSubType() {
        return subType;
    }

    /**
     * 返回字段所属于的类型。如：联系人，任务
     * 
     * @return: int
     */
    int getType() {
        return type;
    }

    /**
     * 是否常用字段。
     * 
     * @return: boolean
     */
    boolean isNormal() {
        return normal;
    }

    /**
     * 是否主键。主要用于数据库的判断。
     * 
     * @return: boolean
     */
    boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * 是否显示。
     * 
     * @return: boolean
     */
    boolean isShowing() {
        return showing;
    }

    /**
     * 是否显示。不一定需要
     * 
     * @param: boolean showing
     */
    void setShowing(
            boolean showing) {
        this.showing = showing;
    }

    /**
     * 判断当前字段是否是自定义字段
     * 
     * @return boolean
     */
    boolean isCustomize() {
        return false;
    }

    /**
     * 设置字段名 注意该字段必须是用户自定义字段
     * 
     * @param: String fieldName
     */
    void setFieldName(
            String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * 设置字段类型 注意该字段必须是用户自定义字段
     * 
     * @param: String fieldType
     */
    void setFieldType(
            int fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * 设置字段所属于的类型。如：联系人，任务 注意该字段必须是用户自定义字段
     * 
     * @param: int type
     */
    void setType(
            int type) {
        this.type = type;
    }

    /**
     * Getter for property fieldID.
     * 
     * @return Value of property fieldID.
     */
    int getFieldID() {
        return this.fieldID;
    }

    /**
     * Setter for property fieldID.
     * 
     * @param fieldID
     *            New value of property fieldID.
     */
    void setFieldID(
            int fieldID) {
        this.fieldID = fieldID;
    }

    // 字段名字
    private String fieldName;
    // 字段ID
    private int fieldID;
    // private String fieldType;
    private int fieldType;
    // type : 应用程序的代号，@see : emo.pim.util.ModelConstants.CALENDAR_APP等一系列
    private int type;
    // subType ： 每个应用的子类型，可通过视图菜单看到这些子类型
    private int subType;
    // 是否常用字段
    private boolean normal;
    // 主键
    private boolean primaryKey;
    // 是否显示
    private boolean showing;
}
