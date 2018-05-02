package org.cas.client.platform.bar;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.cas.client.platform.foregrounding.dialog.ForegroundingDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
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
    @Override
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

    @Override
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

    @Override
    public int getStatus(
            Object prmActionName) {
        return -1;
    }

    @Override
    public int[] getImportableFields() {
        return null;
    }// 返回应用中可以供导入的字段.

    @Override
    public String[] getImportDispStr() {
        return null;
    }// 返回应用所支持的可导入内容的字符串数组。

    @Override
    public String getImportIntrStr(
            Object prmKey) {
        return null;
    }

    @Override
    public boolean execImport(
            Object prmKey) {
        return false;
    }

    /** 每个希望加入到PIM系统的应用都必须实现该方法，使系统在ViewInfo系统表中为其初始化ViewInfo。 */
    @Override
    public void initInfoInDB() {
        Statement stm = PIMDBModel.getStatement();
        try {
            // 增建一个dining_Table表。select ID, Name, posX, posY, width, height, type from Tables order by DSP_INDEX"
            String sql =
                    "CREATE CACHED TABLE DINING_TABLE (ID INTEGER IDENTITY PRIMARY KEY, name VARCHAR(255),"
                    .concat(" DSP_INDEX INTEGER, posX INTEGER, posY INTEGER, width INTEGER, height INTEGER, type INTEGER, billNum INTEGER, status INTEGER);");
            stm.executeUpdate(sql);
            
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T1', '0', 10, 10, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T2', '1', 160, 10, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T3', '2', 320, 10, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T4', '3', 480, 10, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T5', '4', 640, 10, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T6', '5', 10, 10, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T7', '6', 160, 110, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T8', '7', 320, 110, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T9', '8', 480, 110, 120, 60, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO DINING_TABLE (name, DSP_INDEX, posX, posY, width, height, type) VALUES ('T10', '9', 640, 110, 120, 60, 0)";
            stm.executeUpdate(sql);

            //create a printer device table
            sql =
                    "CREATE CACHED TABLE Hardware (ID INTEGER IDENTITY PRIMARY KEY, name VARCHAR(255),"
                    .concat(" category INTEGER, langType INTEGER,  ip VARCHAR(255), style INTEGER, status INTEGER);");
            stm.executeUpdate(sql);

            sql = "INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('P1', 0, 0, '192.168.1.86', 1, 0)";	//打印机，全打
            stm.executeUpdate(sql);
            sql = "INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('P2', 0, 0, '192.168.1.87', 0, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('P3', 0, 0, '192.168.1.88', 0, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('P4', 0, 0, '192.168.1.89', 0, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('P5', 0, 0, '192.168.1.90', 0, 0)";
            stm.executeUpdate(sql);
            sql = "INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('P6', 0, 0, '192.168.1.91', 0, 0)";
            stm.executeUpdate(sql);
            
            stm.close();
        } catch (Exception e) {
        	ErrorUtil.write(e);
        }
    }

    @Override
    public void showDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag) {
        new ForegroundingDlg(parent).setVisible(true);
    }

    @Override
    public ICASDialog getADialog() {
        return new ForegroundingDlg(null);
    }

    @Override
    public JToolBar[] getStaticBars() {
        return null;
    }

    @Override
    public JToolBar[] getDynamicBars() {
        return null;
    }

    @Override
    public JPanel[] getStaticStateBars() {
        return null;
    }

    @Override
    public JPanel[] getDynamicStateBars() {
        return null;
    }

    @Override
    public JMenuItem getCreateMenu() {
        return null;
    }

    @Override
    public JMenuItem getCreateMenu(
            Vector prmSelectedRecVec) {
        return null;
    }

    @Override
    public String[] getAppFields() {
        return null;
    }

    @Override
    public String[] getAppTypes() {
        return null;
    }

    @Override
    public String[] getAppTexts() {
        return null;
    }

    @Override
    public IView getTiedView() {
        return new BarTiedView();
    }

    @Override
    public Icon getAppIcon(
            boolean prmIsBig) {
        if (prmIsBig)
            return PIMPool.pool.getIcon("/org/cas/client/platform/foregrounding/img/Bath32.gif");
        else
            return PIMPool.pool.getIcon("/org/cas/client/platform/foregrounding/img/Bath16.gif");
    }

    /* 返回用于在查找对话盒中显示的一些文本类型的列名.方便用户做简单查找. */
    @Override
    public String[] getRecommendColAry() {
        return null;
    }

    // 几种特殊的绘制器和编辑器
    @Override
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
