package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.FilterInfo;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.View_PIMDetails;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.CustViewConsts;
import org.cas.client.resource.international.IntlModelConstants;

/**
 * 查询结果集视图应用
 */

class FilterResultApplication implements IApplication {
    /**
     * Creates a new instance of FilterResultApplication
     * 
     * @param view
     *            视图接口
     */
    public FilterResultApplication(PIMViewInfo prmViewInfo, FilterInfo prmFilterInfo) {
        pimViewInfo = prmViewInfo;

        // addFieldColumn(ModelDBConstants.INFOLDER); //添加一个字段INFOLDER字段
        // addFieldColumn(ModelDBConstants.DELETED_FLAG); //添加以一个是否已经删除字段

        query = new SearchQuery(prmFilterInfo, new WhereFactory(prmFilterInfo));
        director = new SearchDirector(query);
        director.setContainsDel(true);

        currentView = new View_PIMDetails(CASUtility.EMPTYSTR);
        getContents(); // 得到当前联系人的所有记录
    }

    /**
     * 返回当前搜索到的记录数
     */
    public int getResultSize() {
        return contents == null ? 0 : contents.length;
    }

    /**
     * 返回当前的PIMTableView视图
     */
    public View_PIMDetails getCurrentView() {
        return currentView; // 所有的视图都是表格currentView;
    }

    /**
     * 设置联系人记录
     * 
     * @param prmContact
     *            联系人记录
     */
    public void setSearchInfo(
            FilterInfo prmFilterInfo) {
        searchInfo = prmFilterInfo;
        query.setSearch(searchInfo);
        director.setBuilder(query);
    }

    /**
     * 得到当前ViewInfo下的所有的记录
     */
    private void getContents() {
        // if (contents == null)
        // {
        // contents = new Vector(); //实例化当前ViewInfo下的所有的记录
        // }
        // else
        // {
        // contents.clear(); //清除所有的记录
        // }
        contents = null; // 清空
        add(pimViewInfo.getAppIndex());
    }

    // NOTE: 此方法要注意了,在过滤查找的对话盒table中添加一些列,例如"所在文件夹",是否删除等等,所以保留此方法
    /**
     * 在Viewinfo中增加字段
     * 
     * @param prmFieldIndex
     *            字段的索引值
     * @param prmWidth
     *            字段的宽度
     */
    // private void addFieldColumn(int prmFieldIndex)
    // {
    // String tmpField = pimViewInfo.getFieldNames(); //得到要显示的字段名字对应的ID号
    // String tmpFieldStr = ",".concat(String.valueOf(prmFieldIndex));
    // if (tmpField.indexOf(tmpFieldStr.concat(",")) < 0
    // && !tmpField.endsWith(tmpFieldStr)) //判断在当前的字段字符串中是否已经存在当前的要添加的字段
    // {
    // pimViewInfo.setFieldNames(tmpField.concat(tmpFieldStr));
    //
    // String tmpWid = Integer.toString(PIMDefaultFieldWidths.EMAIL_FIELD_WIDTHS[prmFieldIndex]);
    // tmpWid = pimViewInfo.getFieldWidths().concat(",").concat(tmpWid);
    // pimViewInfo.setFieldWidths(tmpWid);
    // }
    // }

    /**
     * 改变视图
     * 
     * @param contentsV
     *            数据内容 // * @param newViewInfo 视图信息
     */
    public void refreshView(
            PIMViewInfo newViewInfo) {
        pimViewInfo = newViewInfo;

        // addFieldColumn(ModelDBConstants.INFOLDER); //添加一个INFOLDER字段
        // addFieldColumn(ModelDBConstants.DELETED_FLAG); //添加以一个是否已经删除字段

        getContents(); // 得到当前视图下的所有的记录
        currentView = new View_PIMDetails(CASUtility.EMPTYSTR); // 所有的视图都是表格
        currentView.setApplication(this);
        currentView.setViewInfo(pimViewInfo);
    }

    /**
     * 加载数据内容
     */
    private void add(
            int prmApp) {
        switch (prmApp) {
            case ModelCons.INBOX_APP: // 收件箱
                addList(director.getResultSet(ModelCons.INBOX_APP));
                break;

            case ModelCons.OUTBOX_APP:
                addList(director.getResultSet(ModelCons.OUTBOX_APP));
                break;

            case ModelCons.SENDED_APP:
                addList(director.getResultSet(ModelCons.SENDED_APP));
                break;

            case ModelCons.DRAFT_APP:
                addList(director.getResultSet(ModelCons.DRAFT_APP));
                break;

            case ModelCons.TASK_APP: // 任务
                addList(director.getResultSet(prmApp));
                break;

            case ModelCons.CONTACT_APP: // 联系人
                addList(director.getResultSet(prmApp));
                break;

            case ModelCons.DIARY_APP: // 日记
                addList(director.getResultSet(prmApp));
                break;

            case ModelCons.CALENDAR_APP: // 约会
                addList(director.getResultSet(prmApp));
                break;

            case ModelCons.DELETED_ITEM_APP:
                addList(director.getResultSet(new int[] { ModelCons.INBOX_APP, ModelCons.OUTBOX_APP,
                        ModelCons.SENDED_APP, ModelCons.TASK_APP, ModelCons.CONTACT_APP, ModelCons.DIARY_APP,
                        ModelCons.CALENDAR_APP }, new int[] { ModelCons.INBOX_BY_SENT_TO, ModelCons.INBOX_BY_SENT_TO,
                        ModelCons.INBOX_BY_SENT_TO, ModelCons.TASK_BY_SIMPLE_LIST, ModelCons.CONTACT_PHONE_LIST,
                        ModelCons.DIARY_BY_DATE, ModelCons.APPOINTMENT_BY_CURRENT_APPOINTMENT }));
                break;

            default:
                addList(director.getResultSet(new int[] { ModelCons.INBOX_APP, ModelCons.OUTBOX_APP,
                        ModelCons.SENDED_APP, ModelCons.TASK_APP, ModelCons.CONTACT_APP, ModelCons.DIARY_APP,
                        ModelCons.CALENDAR_APP }, new int[] { ModelCons.INBOX_BY_SENT_TO, ModelCons.INBOX_BY_SENT_TO,
                        ModelCons.INBOX_BY_SENT_TO, ModelCons.TASK_BY_SIMPLE_LIST, ModelCons.CONTACT_PHONE_LIST,
                        ModelCons.DIARY_BY_DATE, ModelCons.APPOINTMENT_BY_CURRENT_APPOINTMENT }));
                break;
        }
    }

    /**
     * 加载邮件记录
     */
    private void addList(
            Object[][] prmList) {
        if (prmList != null && prmList.length > 0) {
            int length = prmList.length; // 行数
            int colNum = 0;
            for (int i = 0; i < length; i++) // 确定最大列数
            {
                int col = prmList[i].length;
                colNum = colNum >= col ? colNum : col;
            }

            if (contents == null) {
                contents = new Object[length][colNum];
            }

            for (int i = 0; i < length; i++) {
                for (int j = 0; j < colNum; j++) {
                    contents[i][j] = prmList[i][j];
                }
            }
        }
    }

    /**
     * 返回当前视图信息
     * 
     * @return PIMViewInfo
     */
    public PIMViewInfo getActiveViewInfo() {
        return pimViewInfo;
    }

    /** 每个希望加入到PIM系统的应用都必须实现该方法，使系统在ViewInfo系统表中为其初始化ViewInfo。 */
    public void initInfoInDB() {
    }

    /**
     * 取得被选中的记录。
     * 
     * @return 被选中的记录
     * @called by CutAction & CopyAction
     */
    public Vector getSelectRecords() {
        return null;
    }

    /**
     * 取得被选中的记录。
     * 
     * @return 被选中的记录
     * @called by CutAction & CopyAction
     */
    public int[] getSelectRecordsID() {
        return null;
    }

    /**
     * 根据上下文棉板在鼠标单击时要行的动作: 显示预览
     * 
     * @param comp
     *            鼠标点击的组件
     */
    public void processMouseClickAction(
            Component comp) {
        PIMTable tmpTab = (PIMTable) comp;

        int rowCount = tmpTab.getRowCount();
        int selRow = tmpTab.getSelectedRow();

        if (selRow >= rowCount || selRow < 0) {
            return;
        }
        // 如果当前的表中存在数据
        int tmpFolderID;
        int tmpApp;
        // 如果没有记录，或者选择的行大于表格的行数,在这种情况下不切换视图
        if (rowCount <= 0) {
            tmpApp = pimViewInfo.getAppIndex();
            tmpFolderID = CASUtility.getAPPNodeID(tmpApp);
        } else {
            // 得到"所在文件夹"在表格中列的列值
            int tmpFolIndex = tmpTab.getColumn(IntlModelConstants.FOLDERID).getModelIndex();
            int tmpDelIndex = tmpTab.getColumn(IntlModelConstants.DELETED).getModelIndex();

            Object tmpF = tmpTab.getValueAt(tmpTab.getSelectedRow(), tmpFolIndex);
            Object tmpD = tmpTab.getValueAt(tmpTab.getSelectedRow(), tmpDelIndex);

            // 判断当前的记录是否已经删除，如果删除则切换视图到已删除项
            if (tmpD != null && tmpD.equals(CustViewConsts.STR_CON_YES)) {
                tmpApp = ModelCons.DELETED_ITEM_APP;
                tmpFolderID = CASUtility.getAPPNodeID(tmpApp);
            } else if (tmpF != null) {
                // 组成记录所在文件夹的路径
                tmpFolderID = CASUtility.getMatchedNodeID(tmpF.toString());
                // 得到当前路径所在的App类型
                tmpApp = CASUtility.getAppIndexByFolderID(tmpFolderID);
            } else {
                tmpApp = pimViewInfo.getAppIndex();
                // 得到当前路径所在的App类型
                tmpFolderID = CASUtility.getAPPNodeID(tmpApp);
            }
        }
        // 切换到当前的试图
        CASControl.ctrl.changeApplication(tmpApp, tmpFolderID);
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
            Component comp,
            int x,
            int y,
            int prmSeleID) {
        // int tmpApplicationIndex = pimViewInfo.getAppType();
        // /**@NOTE: 日记是没有办法显示*/
        //
        // if (prmSeleID < 0) //当前记录的ID号小于 1 为当前的数据库表中没有当前的记录，所以新建
        // {
        // //弹出新建联系人对话盒
        // switch (tmpApplicationIndex)
        // {
        // case ModelConstants.CONTACT_APP:
        // new NewContactAction().actionPerformed(null); //联系人
        // break;
        // case ModelConstants.TASK_APP:
        // new NewTaskAction().actionPerformed(null); //任务
        // break;
        // case ModelConstants.INBOX_APP:
        // case ModelConstants.OUTBOX_APP:
        // case ModelConstants.SENDED_APP:
        // new NewMessageAction().actionPerformed(null); //邮件
        // break;
        // case ModelConstants.CALENDAR_APP : //约会
        // new NewAppointmentAction(0, false).actionPerformed(null);
        // break;
        // /**NOTE: 在所有项目中的点击空白的记录时，不用新建，因为也不知道要新建什么*/
        // default:
        // break;
        // }
        // }
        // //在记录上鼠标双击事件处理
        // else
        // {
        // model = PIMControl.ctrl.getModel();
        // //由id初始化联系人对话框
        // switch (tmpApplicationIndex)
        // {
        // case ModelConstants.CONTACT_APP: //打开联系人对话框
        // PIMRecord tmpContact = model.selectRecord(pimViewInfo.getAppType(), prmSeleID);
        // ContactDialog.showContactDialog(PIMControl.ctrl.getMainFrame(),
        // new UpdateContactAction(), tmpContact);
        // case ModelConstants.TASK_APP: //打开任务对话框
        // PIMRecord tmpTask = model.selectRecord(pimViewInfo.getAppType(), prmSeleID);
        // TaskDialog.showTaskDialog(PIMControl.ctrl.getMainFrame(),
        // new UpdateContactAction(), tmpTask, false);
        // break;
        // case ModelConstants.INBOX_APP: //打开邮件对话框
        // case ModelConstants.OUTBOX_APP:
        // case ModelConstants.SENDED_APP:
        // PIMRecord tmpMail = model.selectRecord(pimViewInfo.getAppType(),prmSeleID);
        // new OpenAction(tmpMail).actionPerformed(null);
        // break;
        // case ModelConstants.CALENDAR_APP : //打开约会或会议对话框
        // PIMRecord tmpAppointment = model.selectRecord(pimViewInfo.getAppType(), prmSeleID);
        // AppointmentDialog.showDialog(PIMControl.ctrl
        // .getMainFrame(),new UpdateContactAction(), tmpAppointment, false);
        // break;
        // case ModelConstants.DELETED_ITEM_APP:
        // Vector tmpVe = currentView.getSelectedRecords();
        // if (tmpVe.size() == 0)
        // {
        // return ;
        // }
        // Object ob = tmpVe.get(0);
        // PIMRecord tmpRecord = (PIMRecord)ob;
        // new OpenAction(tmpRecord).actionPerformed(null);;
        // break;
        // case ModelConstants.DIARY_APP : //NOTE: 日记无法显示
        // break;
        // default:
        // break;
        // }
        // }
    }

    /**
     * 刷新视图
     */
    public void refreshView() {
    }

    /**
     * 选取视图中的所有记录。
     * 
     * @called by seleteAllAction
     */
    public void seleteAllRecord() {
    }

    /**
     * 根据上下文显示弹出式菜单
     * 
     * @param showInfo
     *            其他选中信息
     * @param comp
     *            鼠标点击的组件
     * @param x
     *            X坐标
     * @param y
     *            Y坐标
     */
    public void showPopupMenu(
            Component comp,
            int x,
            int y) {
    }

    public Object[][] processContent(
            Object[][] pContents,
            Object[] pFieldNames) {
        if (pContents[0].length != pFieldNames.length)
            return pContents;
        return pContents;
    }

    /**
     * 更新视图
     * 
     * @param e
     *            数据库事件
     */
    public void updateContent(
            PIMModelEvent e) {
    }

    /**
     * 翻页功能,对于非书本视图可以实现成滚屏效果
     * 
     * @param prmIsBack
     *            ：是否向回翻。
     */
    public void setPageBack(
            boolean prmIsBack) {
    }

    /**
     * 视图关闭时应有保存尚未保存的数据的功能
     * 
     * @param prmNeedSave
     */
    public void closed(
            boolean prmNeedSave) {
    }

    /**
     * 事实调用方法
     * 
     * @param parent
     *            : 父窗格
     * @param prmAction
     *            : 保存记录事件
     * @param prmRecord
     *            : 预约记录
     */
    public void showDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag) {
    }

    public ICASDialog getADialog() {
        return null;
    }

    public JToolBar[] getStaticBars() {
        return null;
    }

    public JPanel[] getStaticStateBars() {
        return null;
    }

    public JPanel[] getDynamicStateBars() {
        return null;
    }

    public JToolBar[] getDynamicBars() {
        return null;
    }

    public JMenuItem getCreateMenu() {
        return null;
    }

    public JMenuItem getCreateMenu(
            Vector prmSelectedRecVec) {
        return null;
    }

    public ICASDialog showAppointmentDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag) {
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

    public Action getAction(
            Object prmActionName) {
        return null;
    }

    public int getStatus(
            Object prmActionName) {
        return -1;
    }

    public int[] getImportableFields() {
        return null;
    } // 返回应用中可以供导入的字段.

    public String[] getImportDispStr() {
        return null;
    } // 返回应用所支持的可导入内容的字符串数组。

    public String getImportIntrStr(
            Object prmKey) {
        return null;
    }

    public FieldDescription getFieldDescription(
            String prmHeadName,
            boolean prmIsEditable) {
        return null;
    }

    public boolean execImport(
            Object prmKey) {
        return false;
    }

    public IView getTiedView() {
        return null;
    }

    public Icon getAppIcon(
            boolean prmIsBig) {
        return null;
    }

    public String[] getRecommendColAry() {
        return null;
    }// 返回用于在查找对话盒中显示的一些文本类型的列名.方便用户做简单查找.

    private PIMViewInfo pimViewInfo;
    private View_PIMDetails currentView;
    private SearchQuery query;
    private Object[][] contents;
    private FilterInfo searchInfo;
    private SearchDirector director;
}
