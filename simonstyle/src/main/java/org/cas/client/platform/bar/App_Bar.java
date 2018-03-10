package org.cas.client.platform.bar;

import java.awt.Component;
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
import org.cas.client.platform.foregrounding.ForegroundingDefaultViews;
import org.cas.client.platform.foregrounding.ForegroundingTiedView;
import org.cas.client.platform.foregrounding.dialog.ForegroundingDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.util.ModelConstants2;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;

public class App_Bar extends AbstractApp {
    /** Creates a new instance of TaskViewPane */
    public App_Bar() {
        super(null);
        initActionFlags();
    }

    /**
     * 改变视图
     * 
     * @param contentsV
     *            表格数据
     * @param newViewInfo
     *            视图信息
     */
    public void refreshView(
            PIMViewInfo prmNewViewInfo) {
        removeAll();
        if (prmNewViewInfo == null) // 这种可能性发生在用户选择根结点时,appIndex为-1,
                                    // 没有对应的ViewInfo.
            return; // 对应的appPane临时用BaseBookClosedPane.

        int tmpViewType = prmNewViewInfo.getViewType();
        if (currentViewInfo == null || tmpViewType != currentViewInfo.getViewType() || tmpViewType == 5)
            activeView = getANewView(tmpViewType); // 需要新类型的视图时，就新建一个，同时意味着原来引用的视图将被释放掉。再用到，
                                                   // 再新建,如果视图类型一致,就不需要新建.这样比维护4个View实例更节省一些.
        currentViewInfo = prmNewViewInfo;

        activeView.setApplication(this);// 让所选的"当前视图"认识谁是它目前的应用,用来让其知道该把键盘/鼠标事件交给谁处理.
        // 本来觉得应该重构一下,毕竟View是没有必要认识应用的,应该改为用从外部加监听的方法实现该功能.但是旋想到这里直接用IApp
        // 作为监听器并无不妥，而且这个监听器因为总是只有一个，所以用set方法设置即可，无必要用add方法增加。
        activeView.setViewInfo(currentViewInfo);// 这句代码必须在setApplication之后调用,因为方法中需要调用setTitleAndIcon
                                                // 方法,其中要调用到application的getIcon方法.
        add((Component) activeView);
    }

    public Action getAction(
            Object prmActionName) {
        if (actionFlags.get(prmActionName) != null) {// 看看该ActionName是否被系统维护.如果是系统有维护的,那么就是系统的Action.
            StringBuffer tmpClassPath = new StringBuffer("org.cas.client.platform.");
            tmpClassPath.append("foregrounding.action.");// 系统自带的Action都在emo.pim.pimcontrol.commonaction目录下.
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
        Statement stmt = null;
        for (int j = 0; j < ForegroundingDefaultViews.INIT_DB_VIEWINFO.length; j++)
            try {
                stmt = PIMDBModel.getConection().createStatement();
                stmt.executeUpdate(BarDefaultViews.INIT_DB_VIEWINFO[j]);
            } catch (Exception exp) {
                ErrorUtil.write("Error occured when insert view infos:" + exp);
            }
        // 增建一个雇员绩效考评表。
        String sql =
                "CREATE CACHED TABLE evaluation (ID INTEGER IDENTITY PRIMARY KEY, startTime VARCHAR,"
                        .concat(" endTime VARCHAR, SUBJECT VARCHAR, receive INTEGER, target INTEGER, profit INTEGER);");
        try {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        }
    }

    public void showDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag) {
        new ForegroundingDlg(parent).setVisible(true);
    }

    public ICASDialog getADialog() {
        return new ForegroundingDlg(null);
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
        return new BarTiedView();
    }

    public Icon getAppIcon(
            boolean prmIsBig) {
        if (prmIsBig)
            return PIMPool.pool.getIcon("/org/cas/client/platform/foregrounding/img/Bath32.gif");
        else
            return PIMPool.pool.getIcon("/org/cas/client/platform/foregrounding/img/Bath16.gif");
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
