package org.cas.client.platform.pimmodel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMDBUtil;
import org.cas.client.platform.pimmodel.util.ModelConstants2;

/**
 * 此类用于缓存数据库表的结构，包括两个信息：表字段的名字和对应的SQL类型 表字段的名字保持了对DefaultDBInfo表的TABLE_FIELD_LIST全局静态数组的引用，而SQL类型则是从数据库中得到
 * SQL类型数组用到时才实例化，并缓存在内存中，以便提高数据库操作时性能，避免每次操作都要取数据库表的结构信息
 */

class DBTableStructurePool {
    /**
     * 取到当前数据库表的所有的字段的名字
     * 
     * @param prmTableName
     *            数据库表的名字
     * @return 字段的名字数组，遇到错误则返回null
     */
    String[] getFieldNameArray(
            String prmTableName) {
        int tmpIndex = PIMDBUtil.getFieldAndTypeIndex(prmTableName);
        if (tmpIndex >= 0 && tmpIndex < CustOpts.custOps.APPNameVec.size())
            return MainPane.getApp(prmTableName).getAppFields();
        else
            return ModelConstants2.SYSTEMTABLE_FIELD_LIST[-2 * tmpIndex];
    }

    /**
     * 得到当前数据库表的字段的类型数组
     * 
     * @param prmTableName
     *            数据库表的名字
     * @return 得到当前数据库表的字段的类型数组
     */
    int[] getFieldTypes(
            String prmTableName,
            Connection prmConn) {
        try {
            String sql = "SELECT * FROM ".concat(prmTableName).concat(" WHERE 0 = 1");// 数据库状态,注意在SQL中"0 = 1",得到数据库的查找记录数目为0
            ResultSet rs = prmConn.createStatement().executeQuery(sql);// 得到数据库表的结构
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            int tmpCount = rd.getColumnCount();
            int[] tmpTypes = new int[tmpCount];
            for (int i = 0; i < tmpCount; i++)
                tmpTypes[i] = rd.getColumnType(i + 1);

            return tmpTypes;
        } catch (SQLException se) {
            PIMDBConnecter.instance.reConnectDb();
            se.printStackTrace();
            return null;
        }
    }
}

// /**得到当前数据库表的字段的类型数组，此方法被DBTableStructurePool调用，目的为了缓存数据库表的结构，不用每次都取数据库表的结构
// *
// * @param prmTableName 数据库表的名字
// * @return 得到当前数据库表的字段的类型数组
// */
// String[] getFieldTypes(String prmTableName)
// {
// int tmpIndex = PIMDBUtil.getFieldAndTypeIndex(prmTableName);
// if(tmpIndex >= 0 && tmpIndex < CustOpts.custOps.APPNameVec.size())
// return MainPane.getApp(prmTableName).getAppTypes();
// else
// return ModelConstants2.SYSTEMTABLE_FIELD_LIST[-2 * tmpIndex + 1];
// }

// /**
// * @param columnIndex 列的索引值，注意列值在计算时，是从0开始计算的
// * @param tableName 数据库表的名字
// *
// * @return 返回当前数据库表的第n列字段的类型,如果出现错误则返回DEFAULT_ERROR_TYPE = -1000,既类型错误
// */
// int getFieldType(int columnIndex, String tableName)
// {
// int tmpIndex = PIMDBUtil.getFieldAndTypeIndex(tableName);
// if (tmpIndex < 0
// || tmpIndex >= types.length)
// {
// return DEFAULT_ERROR_TYPE;
// }
//
// //得到数据库表的名字
// if (types[tmpIndex] == null)
// {
// types[tmpIndex] = model.getFieldTypes(tableName);
// }
// return columnIndex < 0 || columnIndex >= types[tmpIndex].length
// ? DEFAULT_ERROR_TYPE : types[tmpIndex][columnIndex];
// }
// private static final int DEFAULT_ERROR_TYPE = -1000; //这个值只要不是java.sql.Types类中定义的类型即可，此处默认定义为-1000;
