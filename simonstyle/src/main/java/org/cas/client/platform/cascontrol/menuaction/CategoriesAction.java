package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;

public class CategoriesAction extends SAction {
    /** Creates a new instance of CategoriesAction */
    public CategoriesAction() {
        super(IStatCons.ALL_APP);
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Vector records = CASControl.ctrl.getSelectRecords();
        if (records != null) {
            if (records.size() == 1) { // 单条记录
                PIMRecord tmpRecord = (PIMRecord) records.get(0);
                Object categoryObj = null;
                if (tmpRecord.getFieldValues() != null) {
                    categoryObj = tmpRecord.getFieldValues().get(PIMPool.pool.getKey(ModelDBCons.CATEGORY));
                }
                String categoryStr = CASUtility.EMPTYSTR;
                if (categoryObj != null) {
                    categoryStr = categoryObj.toString();
                }
                CategoryDialog categorydialog = new CategoryDialog(CASControl.ctrl.getMainFrame(), categoryStr);
                categorydialog.setVisible(true);
                String newCategory = categorydialog.getCategories();
                tmpRecord.setFieldValue(ModelDBCons.CATEGORY, newCategory);
                if (tmpRecord.getFieldValues() != null) {
                    CASControl.ctrl.getModel().updateRecord(tmpRecord, true);
                } else {
                    CASControl.ctrl.getModel().insertRecord(tmpRecord, true);
                }
            } else if (records.size() > 1) { // 多选统一设一个类别
                CategoryDialog categorydialog = new CategoryDialog(CASControl.ctrl.getMainFrame(), null);
                categorydialog.setVisible(true);
                String newCategory = categorydialog.getCategories();
                for (int i = 0; i < records.size(); i++) {
                    PIMRecord tmpRecord = (PIMRecord) records.get(i);
                    tmpRecord.setFieldValue(ModelDBCons.CATEGORY, newCategory);
                    CASControl.ctrl.getModel().updateRecord(tmpRecord, true);
                }
            }
        }
    }
}
