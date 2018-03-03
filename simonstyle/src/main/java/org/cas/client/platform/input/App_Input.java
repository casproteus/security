package org.cas.client.platform.input;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.AbstractApp;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.menuaction.CategoriesAction;
import org.cas.client.platform.cascontrol.menuaction.DeleteAction;
import org.cas.client.platform.cascontrol.menuaction.FollowUpAction;
import org.cas.client.platform.cascontrol.menuaction.MarkFollowUpCompleteAction;
import org.cas.client.platform.cascontrol.menuaction.OpenAction;
import org.cas.client.platform.cascontrol.menuaction.RemoveFollowupFlagAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.contact.action.DisplayFieldsAction;
import org.cas.client.platform.contact.action.NewListAction;
import org.cas.client.platform.contact.action.SortAction;
import org.cas.client.platform.input.action.NewInputAction;
import org.cas.client.platform.input.dialog.InputDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.View_PIMThumbnails;
import org.cas.client.platform.pimview.pimcard.CardViewPanel;
import org.cas.client.platform.pimview.pimtable.DefaultPIMCellEditor;
import org.cas.client.platform.pimview.pimtable.ImageIconPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.Item;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.tagcombo.TagCombo;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.IntlModelConstants;
import org.cas.client.resource.international.PIMTableConstants;
import org.cas.client.resource.international.PopupMenuConstant;

/**
 * 该应用不能支持CVS外部数据导入,因为用到了ForeignKey,对于这类字段,外部数据如果用int表示,则导入时意义多半将发生错误, 如果用文本表示,则类型转换将过于烦琐和低效.
 */
public class App_Input extends AbstractApp {
    /** Creates a new instance of TaskViewPane */
    public App_Input() {
        super(null);
        initActionFlags();
        initForeignKeys();
    }

    public Action getAction(
            Object prmActionName) {
        if (actionFlags.get(prmActionName) != null) {// 看看该ActionName是否被系统维护.如果是系统有维护的,那么就是系统的Action.
            StringBuffer tmpClassPath = new StringBuffer("org.cas.client.platform.");
            tmpClassPath.append("input.action.");// 系统自带的Action都在emo.pim.pimcontrol.commonaction目录下.
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
        StringBuffer tmpSQL = new StringBuffer();
        tmpSQL.append("CREATE CACHED TABLE ").append("Input").append(" (");
        String[] tmpNameAry = getAppFields();
        String[] tmpTypeAry = getAppTypes();
        int tmpLength = tmpNameAry.length;
        for (int j = 0; j < tmpLength - 1; j++)
            tmpSQL.append(tmpNameAry[j]).append(" ").append(tmpTypeAry[j]).append(", ");
        tmpSQL.append(tmpNameAry[tmpLength - 1]).append(" ").append(tmpTypeAry[tmpLength - 1]);
        tmpSQL.append(");");
        try {
            stmt = PIMDBModel.getConection().createStatement();
            stmt.executeUpdate(tmpSQL.toString());
            stmt.executeUpdate("create index folderidx_" + "Input" + " on " + "Input" + " ( folderID)");
        } catch (Exception exp) {
            ErrorUtil.write("Error occured when insert view infos:" + exp);
        }

        for (int j = 0; j < InputDefaultViews.INIT_DB_VIEWINFO.length; j++)
            try {
                stmt.executeUpdate(InputDefaultViews.INIT_DB_VIEWINFO[j]);
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
            new InputDlg(parent, prmAction, prmRecord).setVisible(true);
        else if (prmRecord.getRecordID() >= 0) { // 如果是编辑联系人对话框，则需要注册。
            tmpID = PIMPool.pool.getKey(prmRecord.getRecordID());
            InputDlg tmpDlg = null;
            if (InputDlg.dialogs != null && (tmpDlg = (InputDlg) InputDlg.dialogs.get(tmpID)) != null)// 已有对话框与当前记录对应
                tmpDlg.setVisible(true);
            else
                // 无对话框与当前记录对应
                new InputDlg(parent, prmAction, prmRecord).setVisible(true);
        } else
            // 如拖放动作中,新建一个record,然后从已有的可能是别的应用中的Record中倒内容过去,这是新建的Record的Id被设为-1,以示和新建联系人时的空Record的区别.
            new InputDlg(parent, prmAction, prmRecord).setVisible(true);
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
        return new InputDlg((Frame) null, null, null);
    }

    public JMenuItem getCreateMenu() {
        JMenuItem tmpMenu = new JMenuItem(DlgConst.CONTACTS, CustOpts.custOps.getContactsIcon(false));
        tmpMenu.setMnemonic('c');
        tmpMenu.setAction(new NewInputAction(null));
        return tmpMenu;
        // new JMenuItem(MenuConstant.FILE_NEW_LIST, CustomOptions.custOps.getContactGroupIcon(),'G',
        // array[PIMActionName.ID_FILE_NEW_LIST] = new NewListAction(PIMActionFlag.STATUS_ALL))
    }

    public JMenuItem getCreateMenu(
            Vector prmSelectedRecVec) {
        return null;
    }

    public String[] getAppFields() {
        return InputDefaultViews.FIELDS;
    }

    public String[] getAppTypes() {
        return InputDefaultViews.TYPES;
    }

    public String[] getAppTexts() {
        return InputDefaultViews.TEXTS;
    }

    public IView getTiedView() {
        return null;
    }

    public Icon getAppIcon(
            boolean prmIsBig) {
        if (prmIsBig)
            return PIMPool.pool.getIcon("/org/cas/client/platform/input/img/Input32.gif");
        else
            return PIMPool.pool.getIcon("/org/cas/client/platform/input/img/Input16.gif");
    }

    /* 返回用于在查找对话盒中显示的一些文本类型的列名.方便用户做简单查找. */
    public String[] getRecommendColAry() {
        return null;
    }

    // 几种特殊的绘制器和编辑器
    public FieldDescription getFieldDescription(
            String prmHeadName,
            boolean prmIsEditable) {
        if (prmHeadName == IntlModelConstants.ICON)// 人头
        {
            FieldDescription tmpDes = new FieldDescription();
            // tmpDes.setCellRendor(new ContactsIconRenderer());
            // if (prmIsEditable)
            // {
            // tmpDes.setCellEditor(null);//tmpTableColumn.setEditorEnable(false);
            // }
            // tmpDes.setColumResizeable(true);
            return tmpDes;
        }
        // else if(prmHeadNames == IntlModelConstants.ATTATCH) //附件 这一部分因规格变动所以就没有了,联系人不再有附件这一字段
        // {
        // FieldDescription tmpDes = new FieldDescription();
        // tmpDes.setCellRendor(new AttachmentRenderer());
        // tmpDes.setColumResizeable(true);
        // return tmpDes;
        // }
        else if (prmHeadName == IntlModelConstants.FLAGSTATUS)// 标志状态
        {
            FieldDescription tmpDes = new FieldDescription();
            tmpDes.setCellRendor(new ImageIconPIMTableCellRenderer());
            if (prmIsEditable) {
                tmpDes.setCellEditor(new DefaultPIMCellEditor(getItemEditor()));
            }
            tmpDes.setColumResizeable(true);
            return tmpDes;
        }
        return null;
    }

    /**
     * 根据上下文面板在鼠标双击时要执行的动作: 比如弹出一个对话盒等
     * 
     * @param comp
     *            鼠标点击的组件
     * @param x
     *            X坐标
     * @param y
     *            Y坐标
     * @param actionInfo
     *            其他信息
     */
    public void processMouseDoubleClickAction(
            Component prmComp,
            int x,
            int y,
            final int prmID) {
        if (prmID < 0) // 在viewport上双击的事件处理--------------------------------
            new NewInputAction(null).actionPerformed(null); // 弹出新建联系人对话盒
        else
            // 在记录上鼠标双击事件处理-------------------------
            SwingUtilities.invokeLater(new Runnable() { // 放到事件队列的末尾,为的是防止存盘之前就先显示对话盒.
                        public void run() { // 因为存盘动作是由在线程中触发的editStopping方法中调到的.
                            new OpenAction().actionPerformed(null);
                        }
                    });
    }

    public Object[][] processContent(
            Object[][] pContents,
            Object[] pFieldNames) {
        if (pContents == null || pFieldNames == null || pContents.length < 1 || pFieldNames.length < 1
                || pContents[0].length != pFieldNames.length)
            return pContents;
        for (int i = 0; i < pFieldNames.length; i++)
            if (pFieldNames[i].equals("产品")) {
                for (int j = 0; j < pContents.length; j++)
                    if (pContents[j][i] != null) {
                        int tID = CASUtility.getIndexInAry(prodIdAry, ((Integer) pContents[j][i]).intValue());
                        if (tID < 0)
                            pContents[j][i] = CASUtility.EMPTYSTR;
                        else
                            pContents[j][i] = prodNameAry[tID];
                    }
            } else if (pFieldNames[i].equals("经手人")) {
                for (int m = 0; m < pContents.length; m++)
                    if (pContents[m][i] != null) {
                        int tID = CASUtility.getIndexInAry(employIdAry, ((Integer) pContents[m][i]).intValue());
                        if (tID < 0)
                            pContents[m][i] = CASUtility.EMPTYSTR;
                        else
                            pContents[m][i] = employNameAry[tID];
                    }
            } else if (pFieldNames[i].equals("总价") || pFieldNames[i].equals("折扣") || pFieldNames[i].equals("其他开销")
                    || pFieldNames[i].equals("欠款")) {
                for (int m = 0; m < pContents.length; m++)
                    if (pContents[m][i] != null)
                        pContents[m][i] = String.valueOf(((Integer) pContents[m][i]).floatValue() / 100);
            }
        return pContents;
    }

    public void updateContent(
            PIMModelEvent e) {
        int tmpFoldID = CustOpts.custOps.getActivePathID();
        currentViewInfo = CASControl.ctrl.getViewInfo(tmpFoldID);
        activeView.setViewInfo(currentViewInfo);
        // activeView.setApplication(this);
    }

    // 重写JComponent的一个方法。
    /**
     * TODO:? 重载,目前卡片视图的键盘信息被本类拦住了
     * 
     * @param e
     *            键盘事件
     */
    public void processKeyEvent(
            KeyEvent e) {
        // warning 卡片视图的键盘事件被转移了
        if (e.getID() == KeyEvent.KEY_PRESSED && getActiveViewInfo().getViewType() == ModelCons.CARD_VIEW)
            ((View_PIMThumbnails) activeView).processKeyEvent(e);
        else
            super.processKeyEvent(e);
    }

    /* 本方法专门用来配置联系人表格上第三列(标记状态)的编辑器 */
    private JComboBox getItemEditor() {
        Icon iconNormal = CustOpts.custOps.getMarkStateFieldIcon(1);
        Icon iconMarked = CustOpts.custOps.getMarkStateFieldIcon(0);
        Icon iconFinished = CustOpts.custOps.getMarkStateFieldIcon(2);
        Icon[] icons = { iconNormal, iconMarked, iconFinished };
        Item[] items = new Item[3];
        for (int i = 0; i < items.length; i++)
            items[i] = new Item(new Object[] { icons[i], PIMTableConstants.FLAGS_STATUS_CONSTANTS[i] });

        JComboBox combo = new TagCombo();
        combo.setModel(new DefaultComboBoxModel(items));
        // combo.setRenderer(new ItemCellRenderer());

        return combo;
    }

    private void initActionFlags() {
        actionFlags = new HashMap();
        actionFlags.put("NewInputAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
    }

    private void initForeignKeys() {
        // for the Employee
        String sql = "select ID, SUBJECT from Employee";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            employIdAry = new int[tmpPos + 1];
            employNameAry = new String[tmpPos + 1];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                employIdAry[tmpPos] = rs.getInt("ID");
                employNameAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            employIdAry[tmpPos] = -1;
            employNameAry[tmpPos] = "-";
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        // for the service.
        sql = "select ID, SUBJECT from Product";
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            prodIdAry = new int[tmpPos];
            prodNameAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                prodIdAry[tmpPos] = rs.getInt("ID");
                prodNameAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
    }

    private int[] prodIdAry;
    private String[] prodNameAry;
    private int[] employIdAry;
    private String[] employNameAry;
    private HashMap actionFlags;
}
