package org.cas.client.platform.employee;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
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
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.employee.action.NewEmployeeAction;
import org.cas.client.platform.employee.dialog.EmployeeDlg;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.View_PIMThumbnails;
import org.cas.client.platform.pimview.pimtable.DefaultPIMCellEditor;
import org.cas.client.platform.pimview.pimtable.ImageIconPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.Item;
import org.cas.client.platform.pimview.pimtable.tagcombo.TagCombo;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.IntlModelConstants;
import org.cas.client.resource.international.PIMTableConstants;

public class App_Employee extends AbstractApp {
    /** Creates a new instance of TaskViewPane */
    public App_Employee() {
        super(null);
        initActionFlags();
    }

    @Override
	public Action getAction(
            Object prmActionName) {
        if (actionFlags.get(prmActionName) != null)// 看看该ActionName是否被系统维护.如果是系统有维护的,那么就是系统的Action.
        {
            StringBuffer tmpClassPath = new StringBuffer("org.cas.client.platform.");
            tmpClassPath.append("employee.action.");// 系统自带的Action都在emo.pim.pimcontrol.commonaction目录下.
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
        return EmployeeDefaultViews.importableFieldsIdx;
    }// 返回应用中可以供导入的字段.

    @Override
	public String[] getImportDispStr() {// 返回应用所支持的可导入内容的字符串数组。
        return new String[] { EmployeeDefaultViews.strForImportItem };
    }

    @Override
	public String getImportIntrStr(
            Object prmKey) {
        if (EmployeeDefaultViews.strForImportItem.equals(prmKey)) {
            return EmployeeDefaultViews.describeForImportItem;
        }
        return null;
    }

    @Override
	public boolean execImport(
            Object prmKey) {
        if (EmployeeDefaultViews.strForImportItem.equals(prmKey)) {
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

    /** 每个希望加入到PIM系统的应用都必须实现该方法，使系统在ViewInfo系统表中为其初始化ViewInfo。 */
    @Override
	public void initInfoInDB() {
        Statement stmt = null;
        StringBuffer tmpSQL = new StringBuffer();
        tmpSQL.append("CREATE TABLE ").append("Employee").append(" (");
        String[] tmpNameAry = getAppFields();
        String[] tmpTypeAry = getAppTypes();
        int tmpLength = tmpNameAry.length;
        for (int j = 0; j < tmpLength - 1; j++)
            tmpSQL.append(tmpNameAry[j]).append(" ").append(tmpTypeAry[j]).append(", ");
        tmpSQL.append(tmpNameAry[tmpLength - 1]).append(" ").append(tmpTypeAry[tmpLength - 1]);
        tmpSQL.append(", PRIMARY KEY (" + tmpNameAry[0] + "));");
        try {
            stmt = PIMDBModel.getStatement();
            stmt.executeUpdate(tmpSQL.toString());
            stmt.executeUpdate("create index folderidx_" + "Employee" + " on " + "Employee" + " ( folderID)");
        } catch (Exception exp) {
            ErrorUtil.write("Error occured when insert view infos:" + exp);
        }

        for (int j = 0; j < EmployeeDefaultViews.INIT_DB_VIEWINFO.length; j++)
            try {
                stmt.executeUpdate(EmployeeDefaultViews.INIT_DB_VIEWINFO[j]);
            } catch (Exception exp) {
                ErrorUtil.write("Error occured when insert view infos:" + exp);
            }
    }

    @Override
	public void showDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag) {
        Integer tmpID = null;
        if (prmRecord == null || (prmRecord.getRecordID() == -1 && prmDrag)) // 如果是新建联系人，不需注册
            new EmployeeDlg(parent, prmAction, prmRecord).setVisible(true);
        else if (prmRecord.getRecordID() >= 0) { // 如果是编辑联系人对话框，则需要注册。
            tmpID = PIMPool.pool.getKey(prmRecord.getRecordID());
            EmployeeDlg tmpDlg = null;
            if (EmployeeDlg.dialogs != null && (tmpDlg = (EmployeeDlg) EmployeeDlg.dialogs.get(tmpID)) != null)// 已有对话框与当前记录对应
                tmpDlg.setVisible(true);
            else
                // 无对话框与当前记录对应
                new EmployeeDlg(parent, prmAction, prmRecord).setVisible(true);
        } else
            // 如拖放动作中,新建一个record,然后从已有的可能是别的应用中的Record中倒内容过去,这是新建的Record的Id被设为-1,以示和新建联系人时的空Record的区别.
            new EmployeeDlg(parent, prmAction, prmRecord).setVisible(true);
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
	public ICASDialog getADialog() {
        return new EmployeeDlg((Frame) null, null, null);
    }

    @Override
	public JMenuItem getCreateMenu() {
        JMenuItem tmpMenu = new JMenuItem(DlgConst.CONTACTS, CustOpts.custOps.getContactsIcon(false));
        tmpMenu.setMnemonic('c');
        tmpMenu.setAction(new NewEmployeeAction(null));
        return tmpMenu;
        // new JMenuItem(MenuConstant.FILE_NEW_LIST, CustomOptions.custOps.getContactGroupIcon(),'G',
        // array[PIMActionName.ID_FILE_NEW_LIST] = new NewListAction(PIMActionFlag.STATUS_ALL))
    }

    @Override
	public JMenuItem getCreateMenu(
            Vector prmSelectedRecVec) {
        return null;
    }

    @Override
	public String[] getAppFields() {
        return EmployeeDefaultViews.FIELDS;
    }

    @Override
	public String[] getAppTypes() {
        return EmployeeDefaultViews.TYPES;
    }

    @Override
	public String[] getAppTexts() {
        return EmployeeDefaultViews.TEXTS;
    }

    @Override
	public IView getTiedView() {
        return null;
    }

    @Override
	public Icon getAppIcon(
            boolean prmIsBig) {
        if (prmIsBig)
            return PIMPool.pool.getIcon("/org/cas/client/platform/employee/img/Employee32.gif");
        else
            return PIMPool.pool.getIcon("/org/cas/client/platform/employee/img/Employee16.gif");
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
        if (prmHeadName == IntlModelConstants.ICON)// 人头
        {
            FieldDescription tmpDes = new FieldDescription();
            // tmpDes.setCellRendor(new OutputsIconRenderer());
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
        /*
         * // 根据规格,"表示为"字段必须有(在每一个联系人视图),与OUTLOOK不同 // 允许用户在此编辑栏中输入,此栏必须有输入值,否则不入数据库 // 此处不再对该字段的编辑器作特殊处理 else
         * if(prmHeadNames[i] == EmployeeDefaultViews.TEXTS[EmployeeDefaultViewstants.DISPLAY_AS]) { if (prmHasEditor) {
         * prmEditor = new DefaultPIMCellEditor(new FileAsCellEditor())); } } //
         */

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
    @Override
	public void processMouseDoubleClickAction(
            Component prmComp,
            int x,
            int y,
            final int prmID) {
        if (prmID < 0) // 在viewport上双击的事件处理--------------------------------
            new NewEmployeeAction(null).actionPerformed(null); // 弹出新建联系人对话盒
        else
            // 在记录上鼠标双击事件处理-------------------------
            SwingUtilities.invokeLater(new Runnable() { // 放到事件队列的末尾,为的是防止存盘之前就先显示对话盒.
                        @Override
						public void run() { // 因为存盘动作是由在线程中触发的editStopping方法中调到的.
                            new OpenAction().actionPerformed(null);
                        }
                    });
    }

    @Override
	public void updateContent(
            PIMModelEvent e) {
        int tmpFoldID = CustOpts.custOps.getActivePathID();
        currentViewInfo = CASControl.ctrl.getViewInfo(tmpFoldID);
        activeView.setViewInfo(currentViewInfo);
        // activeView.setApplication(this);
    }

    @Override
	public Object[][] processContent(
            Object[][] pContents,
            Object[] pFieldNames) {
        if (pContents == null || pFieldNames == null || pContents.length < 1 || pFieldNames.length < 1
                || pContents[0].length != pFieldNames.length)
            return pContents;
        for (int i = 0; i < pFieldNames.length; i++)
            if (pFieldNames[i].equals("工资") || pFieldNames[i].equals("保险")) {
                for (int m = 0; m < pContents.length; m++)
                    if (pContents[m][i] != null)
                        pContents[m][i] = String.valueOf(((Integer) pContents[m][i]).floatValue() / 100);
            }
        return pContents;
    }

    // 重写JComponent的一个方法。
    /**
     * TODO:? 重载,目前卡片视图的键盘信息被本类拦住了
     * 
     * @param e
     *            键盘事件
     */
    @Override
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
        actionFlags.put("NewEmployeeAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
    }

    private HashMap actionFlags;
}
