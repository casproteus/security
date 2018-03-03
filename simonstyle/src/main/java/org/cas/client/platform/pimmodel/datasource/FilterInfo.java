package org.cas.client.platform.pimmodel.datasource;

import java.io.Serializable;
import java.util.ArrayList;

import org.cas.client.platform.casutil.PIMPool;

public class FilterInfo implements Cloneable, Serializable {
    /** Creates a new instance of FilterInfo */
    public FilterInfo() {
        init();
    }

    public void init() {
        fieldList = new ArrayList();
        valueList = new ArrayList();
        condiList = new ArrayList();
        typesList = new ArrayList();
    }

    // 设置过滤的头信息
    public void setFilterIndex(
            int filterIndex) {
        this.filterIndex = filterIndex;
    }

    // 返回过滤的头信息
    public int getFilterIndex() {
        return filterIndex;
    }

    // 设置查找位置
    public void setFilterPosition(
            String prmFilterPosition) {
        filterPosition = prmFilterPosition;
    }

    // 设置是否包括查找的子文件夹
    public void setContainsSubFolders(
            boolean prmContainsSubFolders) {
        containsSubFolders = prmContainsSubFolders;
    }

    // 返回是否包含子文件夹
    public boolean isContainsSubFolders() {
        return containsSubFolders;
    }

    // 返回查找位置
    public String getFilterPosition() {
        return filterPosition;
    }

    // 设置查找字符串
    public void setFindString(
            String findString) {
        this.findString = findString;
    }

    // 返回查找字符串
    public String getFindString() {
        return findString;
    }

    // 设置查找位置的索引值
    public void setFindPositionIndex(
            int findPositionIndex) {
        this.findPositionIndex = findPositionIndex;
    }

    // 返回查找位置的索引值
    public int getFindPositionIndex() {
        return findPositionIndex;
    }

    // 设置收件人
    public void setAddresser(
            String addresser) {
        this.addresser = addresser;
    }

    // 返回收件人
    public String getAddresser() {
        return addresser;
    }

    // 设置发件人
    public void setAddressee(
            String addressee) {
        this.addressee = addressee;
    }

    // 返回发件人
    public String getAddressee() {
        return addressee;
    }

    // 设置类型
    public void setCategory(
            String category) {
        this.category = category;
    }

    // 返回类型
    public String getCategory() {
        return category;
    }

    /**
     * 得到查询字符串
     */
    public String getSeacrchString() {
        return queryString;
    }

    /**
     * 设置查询字符串
     */
    public void setSearchString(
            String queryString) {
        this.queryString = queryString;
    }

    // 设置时间条件索引值
    public void setTimeConditionIndex(
            int timeCondiIndex) {
        this.timeCondiIndex = timeCondiIndex;
    }

    // 取到时间条件索引值
    public int getTimeConditionIndex() {
        return timeCondiIndex;
    }

    // 设置时间值的索引值
    public void setTimeValueIndex(
            int timeValueIndex) {
        this.timeValueIndex = timeValueIndex;
    }

    // 返回时间值的索引值
    public int getTimeValueIndex() {
        return timeValueIndex;
    }

    // 第二选项卡

    // 设置字段索引值
    public void setFieldIndex(
            int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    // 取到字段索引值
    public int getFieldIndex() {
        return fieldIndex;
    }

    // 得到字段列表
    public ArrayList getFieldList() {
        return fieldList;
    }

    // 得到值列表
    public ArrayList getValueList() {
        return valueList;
    }

    // 得到条件列表
    public ArrayList getCondiList() {
        return condiList;
    }

    // 设置条件索引值
    public void setConditionIndex(
            int conditionIndex) {
        this.conditionIndex = conditionIndex;
    }

    // 返回条件索引值
    public int getConditionIndex() {
        return conditionIndex;
    }

    // 设置值
    public void setValue(
            String value) {
        this.value = value;
    }

    // 返回值
    public String getValue() {
        return value;
    }

    // 返回类型列表
    public ArrayList getTypesList() {
        return typesList;
    }

    // 把值添加到列表中
    public void insert(
            int fieldIndex,
            int conditionIndex,
            Object value,
            int type) {
        fieldList.add(PIMPool.pool.getKey(fieldIndex));
        condiList.add(PIMPool.pool.getKey(conditionIndex));
        valueList.add(value);
        typesList.add(PIMPool.pool.getKey(type));
    }

    // 删除当前字段的索引值
    public void remove(
            int index) {
        if (index >= 0 && index < fieldList.size()) // 存在当前的字段的索引值则删除
        {
            fieldList.remove(index);
            valueList.remove(index);
            condiList.remove(index);
            typesList.remove(index);
        }
    }

    // 判断当前的字段列表的值的个数
    public int getSize() {
        return fieldList.size();
    }

    // 判断当前的filterInfo是否为空，即是否有修改
    public boolean isEmpty() {
        return

        findString == null && findPositionIndex == -1 && addresser == null && addressee == null && category == null
                && timeCondiIndex == -1 && timeValueIndex == -1 && fieldIndex == -1 && conditionIndex == -1
                && value == null && fieldList.size() < 1 && valueList.size() < 1 && condiList.size() < 1;
    }

    /**
     * 是否等于初始化的值
     */
    public boolean isEqualsInit() {
        return findString == null && findPositionIndex == 0 && addresser == null && addressee == null
                && category == null && timeCondiIndex == 0
                && timeValueIndex == -1 // @NOTE: 这个值初始化时没有设置
                && fieldIndex == 0 && conditionIndex == 0 && value == null && fieldList.size() < 1
                && valueList.size() < 1 && condiList.size() < 1 && !containsSubFolders;
    }

    public void clear() {
        //
        // NOTE: 在清除的时，有两个字段时不清除的，filterString 和 filterPosition字段
        //
        findString = null;
        value = null;
        addresser = null;
        addressee = null;
        category = null;
        findPositionIndex = -1;
        timeCondiIndex = -1;
        timeValueIndex = -1;
        fieldIndex = -1;
        conditionIndex = -1;
        fieldList.clear();
        valueList.clear();
        condiList.clear();
        containsSubFolders = false;
    }

    // 对对象进行克隆
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    // 打印对象的内容
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("fieldIndex :").append(fieldIndex).append(", filterPosition :").append(filterPosition)
                .append(", findString :").append(findString).append(", findPositionIndex :").append(findPositionIndex)
                .append(", addresser :").append(addresser).append(", addressee :").append(addressee)
                .append(", timeCondiIndex :").append(timeCondiIndex).append(", timeValueIndex :")
                .append(timeValueIndex).append(", fieldIndex :").append(fieldIndex).append(", conditionIndex :")
                .append(conditionIndex).append(", value :").append(value).append(", category :").append(category);
        for (int i = fieldList.size() - 1; i >= 0; i--) {
            sb.append("\n").append("fieldList item(").append(i).append(") = ").append(fieldList.get(i)).append("|");
        }
        for (int i = valueList.size() - 1; i >= 0; i--) {
            sb.append("\n").append("valueList item(").append(i).append(") = ").append(valueList.get(i)).append("|");
        }
        for (int i = condiList.size() - 1; i >= 0; i--) {
            sb.append("\n").append("condiList item(").append(i).append(") = ").append(condiList.get(i)).append("|");
        }
        return sb.toString();
    }

    // -----------------------------------------------------------
    //
    private boolean containsSubFolders;
    // 高级筛选的头信息
    private int filterIndex = -1; // 查找的项目

    private String filterPosition; // 查找项目的的位置
    // 普通选项卡的条件
    private String findString; // 查找的字符串

    private int findPositionIndex = -1; // 查找位置的索引值

    private String addresser; // 收件人

    private String addressee; // 发件人

    private String category; // 类型

    private int timeCondiIndex = -1; // 时间条件索引值

    private int timeValueIndex = -1; // 时间值索引值

    private int fieldIndex = -1; // 字段条件值索引值

    private int conditionIndex = -1; // 条件值索引值

    private String value; // 值

    private String queryString; // 查询字符串

    private ArrayList fieldList; // 字段列表

    private ArrayList valueList; // 值列表

    private ArrayList condiList; // 条件列表

    private ArrayList typesList; // 类型列表

    /**
     */
    public String getFilterString() {
        StringBuffer sb = new StringBuffer();
        // if (sb.length() == 0)
        // {
        // return null;
        // }
        // if (addresser != null)
        // {
        // sb.append(" ADDRESSER = ").append(addresser);
        // }
        // if (addressee != null)
        // {
        // sb.append(" ADDRESSEE = ").append(addressee);
        // }
        return (filterString == null || filterString.length() < 1) ? null : sb.append(filterString).toString();
    }

    /**
     */
    public void setFilterString(
            String prmFilter) {
        filterString = prmFilter;
    }

    private String filterString;
}
