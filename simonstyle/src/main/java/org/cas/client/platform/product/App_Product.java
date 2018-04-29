package org.cas.client.platform.product;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.AbstractApp;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.csvtool.CSVImportStep3Dlg;
import org.cas.client.platform.cascontrol.menuaction.OpenAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.output.OutputDefaultViews;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.product.action.NewProductAction;
import org.cas.client.platform.product.dialog.ProductDlg;

public class App_Product extends AbstractApp {
    public App_Product() {
        super(null);
        initActionFlags();
    }

    private void initActionFlags() {
        actionFlags = new HashMap();
        actionFlags.put("NewProductAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
    }

    public Action getAction(
            Object prmActionName) {
        if (actionFlags.get(prmActionName) != null)// 看看该ActionName是否被系统维护.如果是系统有维护的,那么就是系统的Action.
        {
            StringBuffer tmpClassPath = new StringBuffer("org.cas.client.platform.");
            tmpClassPath.append("product.action.");// 系统自带的Action都在emo.pim.pimcontrol.commonaction目录下.
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
        return ProductDefaultViews.importableFieldsIdx;
    }// 返回应用中可以供导入的字段.

    public String[] getImportDispStr() {
        return new String[] { ProductDefaultViews.strForImportItem };
    } // 返回应用所支持的可导入内容的字符串数组。

    public String getImportIntrStr(
            Object prmKey) {
        if (ProductDefaultViews.strForImportItem.equals(prmKey)) {
            return ProductDefaultViews.describeForImportItem;
        }
        return null;
    }

    public boolean execImport(
            Object prmKey) {
        if (ProductDefaultViews.strForImportItem.equals(prmKey)) {
            JFileChooser tmpFileChooser = new JFileChooser();
            tmpFileChooser.setAcceptAllFileFilterUsed(false);
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("vcs"));
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("wab"));
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("txt"));
            if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
                return false;

            File file = tmpFileChooser.getSelectedFile();
            if (file != null) {
                ArrayList contentRowList = new ArrayList();
                String tmpFileTextLineOne = null;// 将磁盘中文件中的信息读入字符串tmpFileText。----------------------------------------
                String tmpFileTextOthers = null;
                try {
                    BufferedReader in = new BufferedReader(new FileReader(file));
                    tmpFileTextLineOne = in.readLine(); // 读取文件中的第一行
                    do {
                        tmpFileTextOthers = in.readLine(); // 读取文件中第二行以下的内容
                        contentRowList.add(tmpFileTextOthers); // 将各行加入ArrayList中
                    } while (tmpFileTextOthers != null); // 读不到内容中跳出循环
                    in.close();
                } catch (IOException e) { // 捕获异常
                    ErrorUtil.write("Error Ocurred when reading file for import Contacts.");
                }

                tmpFileTextLineOne = tmpFileTextLineOne.trim(); // 去掉字符串两端其他无意义的字符。
                int tmpTextLen = tmpFileTextLineOne.length(); // 去掉字符串两端其他无意义的字符后的长度
                // tmpFileText = tmpFileText.substring(0,textLength-1);
                Vector vector = new Vector();// 用来保存字段
                int tmpEnterPosition;
                do {// 将第一行中的各个字段取出来-----------------------------------------------------------
                    tmpEnterPosition = tmpFileTextLineOne.indexOf('\t');
                    if (tmpEnterPosition != -1) {// 如果逗号的位置存在
                        String tempStr = tmpFileTextLineOne.substring(0, tmpEnterPosition);
                        vector.add(tempStr);
                        tmpFileTextLineOne = tmpFileTextLineOne.substring(tmpEnterPosition + 1);
                        tmpTextLen -= tmpEnterPosition + 1;
                    } else {// 如果不存在直接加
                        vector.add(tmpFileTextLineOne);
                        break; // 跳出循环
                    }
                } while (tmpTextLen > 0);

                new CSVImportStep3Dlg(CASControl.ctrl.getMainFrame(), vector.toArray(), contentRowList, this)
                        .setVisible(true);
                return true;
            }
        }
        return false;
    }

    public FieldDescription getFieldDescription(
            String prmHeadName,
            boolean prmIsEditable) {
        return null;
    }

    /**
     * 更新视图
     * 
     * @called by: BaseBookPane;
     * @param e
     *            数据库事件
     */
    public void updateContent(
            PIMModelEvent e) {
        int appType = currentViewInfo.getAppIndex();
        int tmpFolderID = currentViewInfo.getPathID();
        int eventType = e.getType();
        // 处理字段更新事件
        if (eventType == PIMModelEvent.FIELD_CHANGED) {
            currentViewInfo = CASControl.ctrl.getViewInfo(tmpFolderID);
            activeView.setViewInfo(currentViewInfo);
            // activeView.setApplication(this);
        } else if (eventType == PIMModelEvent.UPDATE_FIELD) {
            removeAll();
            currentViewInfo = CASControl.ctrl.getViewInfo(tmpFolderID);
            refreshView(currentViewInfo);
            revalidate();// 设置应用面板时，设置当前的布局无效，重现布局，并绘制当前的组件
            repaint();
        } else {
            currentViewInfo = CASControl.ctrl.getViewInfo(tmpFolderID);
            activeView.setViewInfo(currentViewInfo);
            // activeView.setApplication(this);
        }
    }

    public Object[][] processContent(
            Object[][] pContents,
            Object[] pFieldNames) {
        if (pContents == null || pFieldNames == null || pContents.length < 1 || pFieldNames.length < 1
                || pContents[0].length != pFieldNames.length)
            return pContents;
        for (int i = 0; i < pFieldNames.length; i++)
            if (pFieldNames[i].equals("售价"))
                for (int j = 0; j < pContents.length; j++)
                    if (pContents[j][i] != null)
                        pContents[j][i] = String.valueOf(((Integer) pContents[j][i]).floatValue() / 100);
        return pContents;
    }

    /**
     * 返回当前视图信息
     * 
     * @return PIMViewInfo
     */
    public PIMViewInfo getActiveViewInfo() {
        return this.currentViewInfo;
    }

    /**
     * 根据上下文面板在鼠标双击时要执行的动作: 比如弹出一个对话盒等
     * 
     * @param actionInfo
     *            其他选中信息
     * @param comp
     *            鼠标点击的组件
     * @param x
     *            X坐标
     * @param y
     *            Y坐标
     */
    public void processMouseDoubleClickAction(
            Component prmComp,
            int x,
            int y,
            final int prmID) {
        if (prmID < 0) // 在viewport上双击的事件处理--------------------------------
            new NewProductAction(null).actionPerformed(null); // 弹出新建联系人对话盒
        else
            // 在记录上鼠标双击事件处理-------------------------
            SwingUtilities.invokeLater(new Runnable() { // 放到事件队列的末尾,为的是防止存盘之前就先显示对话盒.
                        public void run() { // 因为存盘动作是由在线程中触发的editStopping方法中调到的.
                            new OpenAction().actionPerformed(null);
                        }
                    });
    }

    /**
     * 在鼠标单击时要执行的动作：设置已读属性和回执处理.
     */
    public void processMouseClickAction(
            Component comp) {
        if (comp instanceof PIMTable) {
            PIMTable tmpTable = (PIMTable) comp;
            int tmpRecordID = Integer.parseInt(tmpTable.getValueAt(tmpTable.getSelectedRow(), 0).toString()); // 取出选中行中的记录ID，
            PIMRecord tmpRec =
                    CASControl.ctrl.getModel().selectRecord(currentViewInfo.getAppIndex(), tmpRecordID,
                            CustOpts.custOps.getActivePathID());
            Hashtable tmphash = tmpRec.getFieldValues();
            Object tmpIsReaded = tmphash.get(PIMPool.pool.getKey(ModelDBCons.READED));// 如果邮件为未读邮件，则将其变成已读，并检查如果要求回执的话进行回执询问
            if (tmpIsReaded == null || tmpIsReaded.toString().equals(PIMPool.BOOLEAN_FALSE)) {
                tmphash.put(PIMPool.pool.getKey(ModelDBCons.READED), Boolean.TRUE);// 设置已读(含图标TODO:图标没有发生效果)
                Object tmpObj = tmphash.get(PIMPool.pool.getKey(ModelDBCons.ICON));
                int iconIndex = tmpObj == null ? 2 : ((Byte) tmpObj).intValue();
                iconIndex = iconIndex == 2 ? 1 : iconIndex;
                tmphash.put(PIMPool.pool.getKey(ModelDBCons.ICON), new Byte((byte) iconIndex));

                CASControl.ctrl.getModel().updateRecord(tmpRec, true);
            }
            layoutContainer(null);
        }
    }

    /** 初始化视图 */
    protected void initView() {
        activeView = getANewView(ModelCons.TABLE_VIEW);// 由视图信息类型得到视图
        activeView.setApplication(this); // 设置视图应用
        activeView.setViewInfo(currentViewInfo);
    }

    /** 每个希望加入到PIM系统的应用都必须实现该方法，使系统在ViewInfo系统表中为其初始化ViewInfo。 */
    public void initInfoInDB() {
        Statement stmt = null;
        StringBuffer tmpSQL = new StringBuffer();
        tmpSQL.append("CREATE CACHED TABLE ").append("Product").append(" (");
        String[] tmpNameAry = getAppFields();
        String[] tmpTypeAry = getAppTypes();
        int tmpLength = tmpNameAry.length;
        for (int j = 0; j < tmpLength - 1; j++)
            tmpSQL.append(tmpNameAry[j]).append(" ").append(tmpTypeAry[j]).append(", ");
        tmpSQL.append(tmpNameAry[tmpLength - 1]).append(" ").append(tmpTypeAry[tmpLength - 1]);
        tmpSQL.append(");");
        try {
            stmt =  PIMDBModel.getStatement();
            stmt.executeUpdate(tmpSQL.toString());
            stmt.executeUpdate("create index folderidx_" + "Product" + " on " + "Product" + " ( folderID)");
        } catch (Exception exp) {
            ErrorUtil.write("Error occured when insert view infos:" + exp);
        }

        for (int j = 0; j < ProductDefaultViews.INIT_DB_VIEWINFO.length; j++)
            try {
                stmt.executeUpdate(ProductDefaultViews.INIT_DB_VIEWINFO[j]);
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
        Integer tmpID = null;
        if (prmRecord == null || (prmRecord.getRecordID() == -1 && prmDrag)) // 如果是新建联系人，不需注册
            new ProductDlg(parent, prmAction, prmRecord).setVisible(true);
        else if (prmRecord.getRecordID() >= 0) { // 如果是编辑联系人对话框，则需要注册。
            tmpID = PIMPool.pool.getKey(prmRecord.getRecordID());
            ProductDlg tmpDlg = null;
            if (ProductDlg.dialogs != null && (tmpDlg = (ProductDlg) ProductDlg.dialogs.get(tmpID)) != null)// 已有对话框与当前记录对应
                tmpDlg.setVisible(true);
            else
                // 无对话框与当前记录对应
                new ProductDlg(parent, prmAction, prmRecord).setVisible(true);
        } else
            // 如拖放动作中,新建一个record,然后从已有的可能是别的应用中的Record中倒内容过去,这是新建的Record的Id被设为-1,以示和新建联系人时的空Record的区别.
            new ProductDlg(parent, prmAction, prmRecord).setVisible(true);
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

    public ICASDialog getADialog() {
        return new ProductDlg((Frame) null, null, null);
    }

    public JMenuItem getCreateMenu() {
        return null;
    }

    public JMenuItem getCreateMenu(
            Vector prmSelectedRecVec) {
        return null;
    }

    public String[] getAppFields() {
        return ProductDefaultViews.FIELDS;
    }

    public String[] getAppTypes() {
        return ProductDefaultViews.TYPES;
    }

    public String[] getAppTexts() {
        return ProductDefaultViews.TEXTS;
    }

    public IView getTiedView() {
        return null;
    }

    public Icon getAppIcon(
            boolean prmIsBig) {
        if (prmIsBig)
            return PIMPool.pool.getIcon("/org/cas/client/platform/product/img/Product32.gif");
        else
            return PIMPool.pool.getIcon("/org/cas/client/platform/product/img/Product16.gif");
    }

    public String[] getRecommendColAry() {
        return null;
    }// 返回用于在查找对话盒中显示的一些文本类型的列名.方便用户做简单查找.

    /**
     * 保留分隔比例
     */
    private int dividerScale;
    // 这个变量和视图初始化时有关,不然无法正确处理保存
    /**
     * 这个变量和视图初始化时有关,不然无法正确处理保存
     */
    private static int pointStartSave;
    private HashMap actionFlags;
}
