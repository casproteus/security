package org.cas.client.platform.cascontrol.dialog.csvtool;

import org.cas.client.platform.pimview.pimtable.PIMTableModelAryBased;

/**
 * 填写注释。
 * <p>
 * TODO:考虑一下，这个类有必要存在吗？
 */

class FieldsTableModel extends PIMTableModelAryBased {
    /**
     * Creates a new instance of FieldsTableModel
     * 
     * @param prmSelectedVector
     *            :表格行选取标记
     * @param prmDataAry
     *            :表格内容
     * @param prmColumnNamesVector
     *            :表格列名
     */
    FieldsTableModel(Object[][] prmDataAry, Object[] prmColNameAry) {
        super(prmDataAry, prmColNameAry);
    }

    /**
     * 取得复选框的名字
     */
    String getCheckBoxName(
            Object prmObj) {
        return (String) ((Object[]) prmObj)[0];
    }

    /**
     * 取得复选框选择状态
     */
    Boolean getSelectItem(
            Object prmObject) {
        Object[] tmpAry = (Object[]) prmObject;
        return (Boolean) tmpAry[1];
    }
}
