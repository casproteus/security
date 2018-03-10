package org.cas.client.platform.bar.dialog;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class BarUtility {

    // 先检查是否存在尚未输入完整信息的产品，如果检查到存在这种产品，方法中会自动弹出对话盒要求用户填写详细信息。
    public static void checkUnCompProdInfo() {
        String sql = "select * from product where subject = '' and DELETED != 'true'"; // 是否存在上没有名字的产品？
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tRowCount = rs.getRow();
            if (tRowCount > 0) {
                int[] tIDAry = new int[tRowCount];
                String[] tCodeAry = new String[tRowCount];
                int[] tPriceAry = new int[tRowCount];
                String[] tMnemonicAry = new String[tRowCount];
                String[] tSubjectAry = new String[tRowCount];
                int[] tStoreAry = new int[tRowCount];
                String[] tUnitAry = new String[tRowCount];
                String[] tCategoryAry = new String[tRowCount];
                int[] tCostAry = new int[tRowCount];
                String[] tContentAry = new String[tRowCount];

                rs.beforeFirst();
                int tIdx = 0;
                while (rs.next()) {
                    tIDAry[tIdx] = rs.getInt("ID");
                    tCodeAry[tIdx] = rs.getString("Code");
                    tPriceAry[tIdx] = rs.getInt("Price");
                    tMnemonicAry[tIdx] = rs.getString("Mnemonic");
                    tSubjectAry[tIdx] = rs.getString("Subject");
                    tStoreAry[tIdx] = rs.getInt("Store");
                    tUnitAry[tIdx] = rs.getString("Unit");
                    tCategoryAry[tIdx] = rs.getString("Category");
                    tCostAry[tIdx] = rs.getInt("Cost");
                    tContentAry[tIdx] = rs.getString("Content");
                    tIdx++;
                }
                for (int i = 0; i < tRowCount; i++) {
                    new MerchandiseDlg(BarFrame.instance, tIDAry[i], tCodeAry[i], tPriceAry[i], tMnemonicAry[i],
                            tSubjectAry[i], tStoreAry[i], tUnitAry[i], tCategoryAry[i], tCostAry[i], tContentAry[i])
                            .setVisible(true);

                }
            }
            rs.close();// 关闭
        } catch (SQLException exp) {// 如果没有匹配是否会到该代码块？
            ErrorUtil.write(exp);
        }
    }

    public static boolean isNumber(
            int pKeyCode) {
        return (pKeyCode >= 48 && pKeyCode <= 58) || (pKeyCode >= 96 && pKeyCode <= 106);
    }

    // 返回Option中设置的，条码的位数。
    public static int getProdCodeLen() {
        String tValue = (String) CustOpts.custOps.getValue(BarDlgConst.ProdCodeLength);
        int tProdCodeLen = 100;
        try {
            tProdCodeLen = Integer.parseInt(tValue);
        } catch (Exception exp) {
        }
        return tProdCodeLen;
    }
}
