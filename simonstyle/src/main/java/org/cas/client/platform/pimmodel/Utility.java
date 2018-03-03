package org.cas.client.platform.pimmodel;

import java.io.File;
import java.util.StringTokenizer;

import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.CASUtility;

class Utility {
    /**
     * 取得保存数据库的路径
     * 
     * @return 数据库保存路径
     */
    static String getPIMDatabaseDirPath() {
        String tmpDBPathStr = CASUtility.getPIMDirPath() + System.getProperty("file.separator") + "database";
        // 至此构造出PIM的数据库文件在系统目录下的路径字符串。
        File tmpDir = new File(tmpDBPathStr);
        if (!tmpDir.exists()) {
            if (!tmpDir.mkdirs()) {
                ErrorUtil.write("create database directory failed！");
                System.exit(0);
            }
        }
        return tmpDBPathStr;
    }

    /**
     * 当设置视图的缺省viewInfo时，此数组的值作为键值。 NOTE:必须与INIT_DB_CONSTANTS中的appType和subApptype相同，既当“20”在VIEWINFO_KEY
     * 中的下标为0时，INIT_DB_CONSTANTS中下标为0的insert信息中的appType和subApptype为2和0；
     */
    static final String[] VIEWINFO_KEY = new String[] {
            // TODO:此处需重新整理
            "20", "21", "22", "23", "24", "25", "40", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54",
            "55", "56", "60", "61", "62", "63", "64", "65", "66", "10", "11", "12", "13", "14", "15", "16", "17", "18",
            "30", "31", "32", "70" };

    /**
     * 根据PIMViewInfo中viewNames字段得到数据库表的字段
     * 
     * @param app
     *            应用类型
     * @param prmFieldNames
     *            PIMViewInfo中存放的待显示的数据库字段的索引值例如1,2,3,45,6等
     * @return String 返回1,2,3,45,6等对应的数据库字段
     */
    static String getTableField(
            int prmApp,
            String prmFieldNames) {
        // 只有7个应用的prmFieldNames字段为非空
        if (prmFieldNames == null || prmFieldNames.length() < 1)
            return null;

        // 得到应用类型对应的数据库表的名字,在数据库表中的索引值
        String[] tmpField;// 数据库表对应的所有的字段
        String tmpKey = (String) CustOpts.custOps.APPNameVec.get(prmApp);
        tmpField = MainPane.getApp(tmpKey).getAppFields();

        StringTokenizer token = new StringTokenizer(prmFieldNames, ModelDBCons.DELIMITER);
        StringBuffer fields = new StringBuffer();
        if (token.hasMoreTokens()) // 特殊处理第一个元素
        {
            fields.append(tmpField[Integer.valueOf(token.nextToken().trim()).intValue()]);
        }
        while (token.hasMoreTokens()) {
            fields.append(',').append(tmpField[Integer.valueOf(token.nextToken().trim()).intValue()]);
        }
        // 返回当前的所有字段的名字
        return fields.toString();
    }

    /**
     * 把二维的对象数组转化为一维的对象数组,如果传入对象是三维的,则返回结果为二维.
     * 
     * @param prmObjtAry
     *            三维或二维的的对象数组
     * @return 一维或二维的对象数组
     */
    static Object[] reduceDirection(
            Object[][] prmObjtAry) {
        int tmpLen = prmObjtAry.length;
        int tmpCount = 0;
        for (int i = 0; i < tmpLen; i++) {
            tmpCount += prmObjtAry[i].length;
        }
        Object[] tmpResultAry = new Object[tmpCount];
        for (int pos = 0, i = 0; i < tmpLen; i++) {
            int l = prmObjtAry[i].length;
            if (l < 1) {
                continue;
            }

            System.arraycopy(prmObjtAry[i], 0, tmpResultAry, pos, l);
            pos += l;
        }
        return tmpResultAry;
    }

    /**
     * 此方法将二维数组进行经纬互换
     * 
     * @Note:本方法假设参数是规整(定长,等长)的二维数组,否则,以参数的第一个元素的尺寸作为参考.
     * @param pSourceAry
     *            源目标数组
     * @return 目标数组
     */
    static Object[][] covertObjectArrayByLatitude(
            Object[][] pSourceAry) {
        int tLen = pSourceAry.length;
        if (tLen < 1)
            return new Object[0][0];

        int tSubLen = pSourceAry[0].length;
        Object[][] tTargetAry = new Object[tSubLen][tLen];

        for (int i = 0; i < tSubLen; i++) {
            for (int j = 0; j < tLen; j++)
                tTargetAry[i][j] = pSourceAry[j][i];
        }
        return tTargetAry;
    }
}
