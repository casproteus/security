package org.cas.client.platform.magicbath;

import java.awt.Frame;
import java.awt.event.ActionListener;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.cas.client.platform.cascontrol.AbstractApp;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.magicbath.dialog.MagicbathDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;

public class App_Magicbath extends AbstractApp {
    /** Creates a new instance of TaskViewPane */
    public App_Magicbath() {
        super(null);
        initActionFlags();
    }

    public Action getAction(
            Object prmActionName) {
        if (actionFlags.get(prmActionName) != null) {// 看看该ActionName是否被系统维护.如果是系统有维护的,那么就是系统的Action.
            StringBuffer tmpClassPath = new StringBuffer("org.cas.client.platform.");
            tmpClassPath.append("magicbath.action.");// 系统自带的Action都在emo.pim.pimcontrol.commonaction目录下.
            tmpClassPath.append(prmActionName);
            try {
                return (Action) Class.forName(tmpClassPath.toString()).newInstance();
            } catch (Exception e) {
                ErrorUtil.write("find no matching Class in commonaction package:" + prmActionName);
                return null;
            }
        } else
            return null;
    }

    public int getStatus(
            Object prmActionName) {
        return -1;
    }

    public int[] getImportableFields() {
        return null;
    }// 返回应用中可以供导入的字段.

    public String[] getImportDispStr() {
        return null;
    }// 返回应用所支持的可导入内容的字符串数组。

    public String getImportIntrStr(
            Object prmKey) {
        return null;
    }

    public boolean execImport(
            Object prmKey) {
        return false;
    }

    /** 每个希望加入到PIM系统的应用都必须实现该方法，使系统在ViewInfo系统表中为其初始化ViewInfo。 */
    public void initInfoInDB() {
        for (int j = 0; j < MagicbathDefaultViews.INIT_DB_VIEWINFO.length; j++)
            try {
                Statement stmt = PIMDBModel.getStatement();
                stmt.executeUpdate(MagicbathDefaultViews.INIT_DB_VIEWINFO[j]);
            } catch (Exception exp) {
                ErrorUtil.write("Error occured when insert view infos:" + exp);
            }
    }

    public void showDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag) {
        new MagicbathDlg(parent).setVisible(true);
    }

    public ICASDialog getADialog() {
        return new MagicbathDlg(null);
    }

    public JToolBar[] getStaticBars() {
        return null;
    }

    public JToolBar[] getDynamicBars() {
        return null;
    }

    public JPanel[] getStaticStateBars() {
        return null;
    }

    public JPanel[] getDynamicStateBars() {
        return null;
    }

    public JMenuItem getCreateMenu() {
        return null;
    }

    public JMenuItem getCreateMenu(
            Vector prmSelectedRecVec) {
        return null;
    }

    public String[] getAppFields() {
        return null;
    }

    public String[] getAppTypes() {
        return null;
    }

    public String[] getAppTexts() {
        return null;
    }

    public IView getTiedView() {
        return MagicbathTiedView.getInstance();
    }

    public Icon getAppIcon(
            boolean prmIsBig) {
        if (prmIsBig)
            return PIMPool.pool.getIcon("/org/cas/client/platform/magicbath/img/Bath32.gif");
        else
            return PIMPool.pool.getIcon("/org/cas/client/platform/magicbath/img/Bath16.gif");
    }

    /* 返回用于在查找对话盒中显示的一些文本类型的列名.方便用户做简单查找. */
    public String[] getRecommendColAry() {
        return null;
    }

    // 几种特殊的绘制器和编辑器
    public FieldDescription getFieldDescription(
            String prmHeadName,
            boolean prmIsEditable) {
        return null;
    }

    private void initActionFlags() {
        actionFlags = new HashMap();
    }

    private HashMap actionFlags;
}
