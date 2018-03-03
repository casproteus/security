package org.cas.client.platform.contact;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
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
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.calendar.CalendarCombo;
import org.cas.client.platform.cascontrol.AbstractApp;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.csvtool.CSVImportStep3Dlg;
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
import org.cas.client.platform.contact.action.NewContactAction;
import org.cas.client.platform.contact.action.NewListAction;
import org.cas.client.platform.contact.action.SortAction;
import org.cas.client.platform.contact.dialog.ContactDlg;
import org.cas.client.platform.contact.dialog.selectcontacts.MainListDialog;
import org.cas.client.platform.employee.EmployeeDefaultViews;
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
import org.cas.client.platform.pimview.pimtable.PIMTable.DateRenderer;
import org.cas.client.platform.pimview.pimtable.specialeditors.FileAsCellEditor;
import org.cas.client.platform.pimview.pimtable.specialeditors.SexComboBox;
import org.cas.client.platform.pimview.pimtable.specialrenderers.MemberListRenderer;
import org.cas.client.platform.pimview.pimtable.specialrenderers.PhotoRenderer;
import org.cas.client.platform.pimview.pimtable.specialrenderers.SexTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.specialrenderers.TypeRenderer;
import org.cas.client.platform.pimview.pimtable.tagcombo.TagCombo;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.IntlModelConstants;
import org.cas.client.resource.international.PIMTableConstants;
import org.cas.client.resource.international.PopupMenuConstant;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

/**
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code
 * and Comments
 */
public class App_Contact extends AbstractApp {
    /** Creates a new instance of TaskViewPane */
    public App_Contact() {
        super(null);
        initActionFlags();
    }

    public Action getAction(
            Object prmActionName) {
        if (actionFlags.get(prmActionName) != null)// 看看该ActionName是否被系统维护.如果是系统有维护的,那么就是系统的Action.
        {
            StringBuffer tmpClassPath = new StringBuffer("org.cas.client.platform.");
            tmpClassPath.append("contact.action.");// 系统自带的Action都在emo.pim.pimcontrol.commonaction目录下.
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
        return ContactDefaultViews.IMPORT_MAP;
    }// 返回应用中可以供导入的字段.

    public String[] getImportDispStr() {
        return new String[] { ContactDefaultViews.strForImportItem };
    }// 返回应用所支持的可导入内容的字符串数组。

    public String getImportIntrStr(
            Object prmKey) {
        if (ContactDefaultViews.strForImportItem.equals(prmKey)) {
            return ContactDefaultViews.describeForImportItem;
        }
        return null;
    }

    public boolean execImport(
            Object prmKey) {
        if (ContactDefaultViews.strForImportItem.equals(prmKey)) {
            JFileChooser tmpFileChooser = new JFileChooser();
            tmpFileChooser.setAcceptAllFileFilterUsed(false);
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("vcs"));
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("wab"));
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("txt"));
            if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
                return false;

            // try//取得打开对话框中选取得文件
            // {
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
            // }
            // catch (Exception exp)
            // {
            // //调用“打开” 对话框时遇到错误，可能的原因是 “打开” 对话框内部产生空指针异常。
            // ErrorDlg.showErrorDialog(MessageCons.E10668);
            // }
        }
        return false;
    }

    /** 每个希望加入到PIM系统的应用都必须实现该方法，使系统在ViewInfo系统表中为其初始化ViewInfo。 */
    public void initInfoInDB() {
        Statement stmt = null;
        StringBuffer tmpSQL = new StringBuffer();
        tmpSQL.append("CREATE CACHED TABLE ").append("Contact").append(" (");
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
            stmt.executeUpdate("create index folderidx_" + "Contact" + " on " + "Contact" + " ( folderID)");
        } catch (Exception exp) {
            ErrorUtil.write(exp);
        }

        // make sure that the viewinfo table is already there. for the first time use of the system,
        // this might occors befor system init the viewInfo table.
        for (int j = 0; j < ContactDefaultViews.INIT_DB_VIEWINFO.length; j++)
            try {
                stmt.executeUpdate(ContactDefaultViews.INIT_DB_VIEWINFO[j]);
            } catch (Exception exp) {
                try {
                    wait(500);
                    stmt.executeUpdate(ContactDefaultViews.INIT_DB_VIEWINFO[j]);
                } catch (Exception pp) {
                    ErrorUtil.write(exp);
                }
            }
    }

    public void showDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag) {
        Object tmpValue = (prmRecord == null) ? null : prmRecord.getFieldValue(ContactDefaultViews.TYPE);
        if ((tmpValue == null) || ((Boolean) tmpValue).booleanValue() == false) {
            Integer tmpID = null;
            if (prmRecord == null || (prmRecord.getRecordID() == -1 && prmDrag)) // 如果是新建联系人，不需注册
                new ContactDlg(parent, prmAction, prmRecord).setVisible(true);
            else if (prmRecord.getRecordID() >= 0) { // 如果是编辑联系人对话框，则需要注册。
                tmpID = PIMPool.pool.getKey(prmRecord.getRecordID());
                ContactDlg tmpDlg = null;
                if (ContactDlg.dialogs != null && (tmpDlg = (ContactDlg) ContactDlg.dialogs.get(tmpID)) != null)// 已有对话框与当前记录对应
                    tmpDlg.setVisible(true);
                else
                    // 无对话框与当前记录对应
                    new ContactDlg(parent, prmAction, prmRecord).setVisible(true);
            } else
                // 如拖放动作中,新建一个record,然后从已有的可能是别的应用中的Record中倒内容过去,这是新建的Record的Id被设为-1,以示和新建联系人时的空Record的区别.
                new ContactDlg(parent, prmAction, prmRecord).setVisible(true);
        } else {
            Integer id = null;
            MainListDialog tmpDlg = null;
            if (prmRecord == null) {
                tmpDlg = new MainListDialog(parent, prmAction, prmRecord);
                tmpDlg.show();
            } else if (prmRecord.getRecordID() >= 0) {
                id = PIMPool.pool.getKey(prmRecord.getRecordID());
                tmpDlg = (MainListDialog) MainListDialog.dialogs.get(id);
                if (tmpDlg == null) { // 无对话框与当前记录对应
                    tmpDlg = new MainListDialog(parent, prmAction, prmRecord);
                    tmpDlg.show();
                } else
                    // 已有对话框与当前记录对应
                    tmpDlg.setVisible(true);
            } else {
                tmpDlg = new MainListDialog(parent, prmAction, prmRecord); // 是否为新建通讯组对话框标记为true。
                tmpDlg.show();
            }
        }
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
        return new ContactDlg((Frame) null, null, null);
    }

    public JMenuItem getCreateMenu() {
        JMenuItem tmpMenu = new JMenuItem(DlgConst.CONTACTS, CustOpts.custOps.getContactsIcon(false));
        tmpMenu.setMnemonic('c');
        tmpMenu.setAction(new NewContactAction(null));
        return tmpMenu;
        // new JMenuItem(MenuConstant.FILE_NEW_LIST, CustomOptions.custOps.getContactGroupIcon(),'G',
        // array[PIMActionName.ID_FILE_NEW_LIST] = new NewListAction(PIMActionFlag.STATUS_ALL))
    }

    public JMenuItem getCreateMenu(
            Vector prmSelectedRecVec) {
        return null;
    }

    public String[] getAppFields() {
        return ContactDefaultViews.FIELDS;
    }

    public String[] getAppTypes() {
        return ContactDefaultViews.TYPES;
    }

    public String[] getAppTexts() {
        return ContactDefaultViews.TEXTS;
    }

    public IView getTiedView() {
        return null;
    }

    public Icon getAppIcon(
            boolean prmIsBig) {
        if (prmIsBig)
            return PIMPool.pool.getIcon("/org/cas/client/platform/contact/img/contact32.gif");
        else
            return PIMPool.pool.getIcon("/org/cas/client/platform/contact/img/contact16.gif");
    }

    /* 返回用于在查找对话盒中显示的一些文本类型的列名.方便用户做简单查找. */
    public String[] getRecommendColAry() {
        return ContactDefaultViews.RecommendCols;
    }

    // 几种特殊的绘制器和编辑器
    public FieldDescription getFieldDescription(
            String prmHeadName,
            boolean prmIsEditable) {
        if (prmHeadName == IntlModelConstants.ICON)// 人头
        {
            FieldDescription tmpDes = new FieldDescription();
            tmpDes.setCellRendor(new ContactsIconRenderer());
            if (prmIsEditable) {
                tmpDes.setCellEditor(null);// tmpTableColumn.setEditorEnable(false);
            }
            tmpDes.setColumResizeable(true);
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
         * if(prmHeadNames[i] == ContactDefaultViews.TEXTS[ContactDefaultViewstants.DISPLAY_AS]) { if (prmHasEditor) {
         * prmEditor = new DefaultPIMCellEditor(new FileAsCellEditor())); } } //
         */
        else if (prmHeadName == ContactDefaultViews.TEXTS[ContactDefaultViews.BIRTHDAY]
                || prmHeadName == ContactDefaultViews.TEXTS[ContactDefaultViews.ANNIVERSARY]) // 时间类的
        {
            FieldDescription tmpDes = new FieldDescription();
            tmpDes.setCellRendor(new DateRenderer());
            if (prmIsEditable)
                tmpDes.setCellEditor(new DefaultPIMCellEditor(new CalendarCombo(true)));
            return tmpDes;
        } else if (prmHeadName == ContactDefaultViews.TEXTS[ContactDefaultViews.SEX]) { // 性别

            FieldDescription tmpDes = new FieldDescription();
            tmpDes.setCellRendor(new SexTableCellRenderer());
            if (prmIsEditable)
                tmpDes.setCellEditor(new DefaultPIMCellEditor(new SexComboBox()));
            return tmpDes;
        } else if (prmHeadName == ContactDefaultViews.TEXTS[ContactDefaultViews.PHOTO]) { // 照片
            FieldDescription tmpDes = new FieldDescription();
            tmpDes.setCellRendor(new PhotoRenderer());
            if (prmIsEditable)
                tmpDes.setCellEditor(null);// tmpTableColumn.setEditorEnable(false);
            return tmpDes;
        } else if (prmHeadName == ContactDefaultViews.TEXTS[ContactDefaultViews.RELATION]) { // 联系人字段
            FieldDescription tmpDes = new FieldDescription();
            // tmpDes.setCellRendor(new ContactsFieldRenderer());
            if (prmIsEditable) {
                tmpDes.setCellEditor(null);
                tmpDes.setCellRendor(null);// tmpTableColumn.setEditorEnable(false);
            }
            return tmpDes;
        } else if (prmHeadName == ContactDefaultViews.TEXTS[ContactDefaultViews.MEMBERLIST]) { // 通讯组成员
            FieldDescription tmpDes = new FieldDescription();
            tmpDes.setCellRendor(new MemberListRenderer());
            // TODO:以后再处理
            if (prmIsEditable) {
                tmpDes.setCellEditor(null);// tmpTableColumn.setEditorEnable(false);
                tmpDes.setCellRendor(null);
            }
            return tmpDes;
        } else if (prmHeadName == ContactDefaultViews.TEXTS[ContactDefaultViews.TYPE]) { // 类型
            FieldDescription tmpDes = new FieldDescription();
            tmpDes.setCellRendor(new TypeRenderer());
            // TODO:以后再处理
            if (prmIsEditable) {
                // tmpTableColumn.setEditorEnable(false);
                tmpDes.setCellEditor(new DefaultPIMCellEditor(new FileAsCellEditor()));
            }
            return tmpDes;
        }
        // else if (prmHeadNames == ContactDefaultViews.TEXTS[ContactDefaultViewstants.INFOLDER]){ //所在文件夹
        // FieldDescription tmpDes = new FieldDescription();
        // tmpDes.setCellRendor(new InFolderRenderer());
        // prmRenderer = ;
        // if (prmHasEditor){
        // tmpTableColumn.setEditorEnable(false);
        // tmpDes.setCellEditor(null);
        // }
        // return tmpDes;
        // }
        else if (prmHeadName == ContactDefaultViews.TEXTS[ContactDefaultViews.WEBPAGE]) { // WEB页,如不作校验可不处理
            FieldDescription tmpDes = new FieldDescription();
            // tmpDes.setCellRendor(new InFolderRenderer());
            if (prmIsEditable) {
                // tmpTableColumn.setEditorEnable(false);
                // tmpDes.setCellEditor(null);
            }
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
        if (prmID < 0)// 在viewport上双击的事件处理--------------------------------
            new NewContactAction(null).actionPerformed(null); // 弹出新建联系人对话盒
        else
            // 在记录上鼠标双击事件处理-------------------------
            SwingUtilities.invokeLater(new Runnable() { // 放到事件队列的末尾,为的是防止存盘之前就先显示对话盒.
                        public void run() { // 因为存盘动作是由在线程中触发的editStopping方法中调到的.
                            new OpenAction().actionPerformed(null);
                        }
                    });
    }

    public void updateContent(
            PIMModelEvent e) {
        int tmpFoldID = CustOpts.custOps.getActivePathID();
        currentViewInfo = CASControl.ctrl.getViewInfo(tmpFoldID);
        activeView.setViewInfo(currentViewInfo);
        // activeView.setApplication(this);
    }

    // //关于邮件菜单的处理------------------------------------------------------
    // /** 根据上下文显示弹出式菜单,如果说，邮件菜单中想提供一个类似“根据该/改些记录建立一个其它应用的项目。
    // * (目前，只有联系人这一系统级应用有这个需求，所谓以人为本）该如何实现呢？需要注意的两点：一、将要建立的
    // * 项目是未知的，不知道将来会有什么应用。二，并不是每个将来新增加的项目都一定需要根据联系人来新建的。
    // * 这样看来，首先必须Application接口需要一个方法，用于判断这个应用是否支持从联系人来新建（返回不为空表示支持）。
    // * 参数是一个联系人，返回值为JMenuItem。menuItem带着一个Action，用户显示新建对话盒，并把联系人设置进去。
    // * 目前只有约会、任务支持，以后所有项目都应该能支持联系人。便于当用户找到一个
    // * 人时，可以立即把跟他有关的材料全部找出来。表示“以人为中心，找到人，找到一切”。
    // * 另外一种用法是，找到一个文件或一个项目，就能找到人，从而找到相关的其它项目。表示“找到任意一条记录，就
    // * 能立即只道，其关联着的其它项目。如找到一个任务（项目）就能只道相关的人，和起止时间，然后就找到了这个时间内
    // * 跟这个人的所有约会记录、跟这个人的聊天记录、跟包含任务名的日记、邮件等。
    // * 同理，找到了一个会义，就找到了联系人和起止时间，然后就找到了相关的工作（或者说项目，或者说任务）也可以包含
    // * 其它应用，比如该时间段内内容包含某个项目主体的随想记录。或其它。
    // * 所以所有相关的
    // * @param showInfo 其他选中信息
    // * showInfo == null : 点击空白处
    // * showInfo是Integer ：选中一个
    // * showInfo是int[]： 选中多个
    // * @param prmComp 鼠标点击的组件
    // * @param x X坐标
    // * @param y Y坐标
    // * @NOTE: 好像对Table以外的视图没有效...
    // */
    // public void showPopupMenu(Component prmComp, int x, int y)
    // {
    // JPopupMenu tmpPopMenu = new JPopupMenu();
    // //TODO:可能是EBean的Bug,不重新new就不再显示??!!!只好暂时注掉,以后要打开! getPopUpMenu();
    // tmpPopMenu.removeAll();
    // //在联系人应用中弹出右键菜单------------------------------------------------
    // if (prmComp instanceof PIMTable || prmComp instanceof CardViewPanel)
    // {
    // JMenuItem tmpMenuItem = new JMenuItem(PopupMenuConstant.OPEN_SELECTED_ITEM, 'O');
    // tmpMenuItem.addActionListener(new OpenAction());
    // tmpPopMenu.add(tmpMenuItem);//打开
    // //tmpPopMenu.add(new JMenuItem(PopupMenuConstant.CONTACT_PRINT, new PrintAction())); //TODO: 启用 打印.
    // tmpPopMenu.addSeparator();
    // // tmpPopMenu.add(new JMenuItem(PopupMenuConstant.CONTACT_FORWARD, 'W', new
    // ForwardAction(PIMActionFlag.STATUS_RECORD_SELECTED)));//作为VCard转发.
    // // tmpPopMenu.addSeparator();
    // //因为每个应用都应该
    // for(int i = 0; i < CustOpts.custOps.APPNameVec.size(); i++){
    // ErrorUtil.write(MainPane.getApp(
    // (String)CustOpts.custOps.APPNameVec.get(i)).getCreateMenu());
    // tmpPopMenu.add(MainPane.getApp(
    // (String)CustOpts.custOps.APPNameVec.get(i)).getCreateMenu());
    // }
    // // tmpPopMenu.add(new JMenuItem(PopupMenuConstant.CONTACT_MESSAGE, 'M', new SendMailToAction())); //发送邮件.
    // // tmpPopMenu.add(new JMenuItem(PopupMenuConstant.CONTACT_APPOINTMENT, 'A', new SendAppointmentToAction(false)));
    // // tmpPopMenu.add(new JMenuItem(PopupMenuConstant.CONTACT_MEETINGREQUEST, 'E', new
    // SendAppointmentToAction(true))); //约会记录
    // // tmpPopMenu.add(new JMenuItem(PopupMenuConstant.CONTACT_TASK, 'T', new SendTaskToAction(false))); //约会要求
    // // tmpPopMenu.add(new JMenuItem(PopupMenuConstant.CONTACT_TASKREQUEST, 'V', new SendTaskToAction(true))); //任务记录
    // tmpPopMenu.addSeparator();
    // // tmpPopMenu.add(new JMenuItem(MenuConstant.TOOLS_EXECUTIVE_BOARD_ROOM, 'B',
    // PIMActionName.ACTIONS[PIMActionName.ID_TOOLS_EXECUTIVE_BOARD_ROOM]));//会议室.
    // // tmpPopMenu.addSeparator();
    //
    // tmpMenuItem = new JMenuItem(PopupMenuConstant.CONTACT_FOLLOWUP, 'U');
    // tmpMenuItem.setAction(new FollowUpAction());
    // tmpPopMenu.add(tmpMenuItem);//后续标志.
    //
    // tmpMenuItem = new JMenuItem(PopupMenuConstant.FLAG_COMPLETE, 'p');
    // tmpMenuItem.setAction(new MarkFollowUpCompleteAction());
    // tmpPopMenu.add(tmpMenuItem); //标记完成菜单.
    //
    // tmpMenuItem = new JMenuItem(PopupMenuConstant.CLEAR_FLAG, 'c');
    // tmpMenuItem.setAction(new RemoveFollowupFlagAction());
    // tmpPopMenu.add(tmpMenuItem);//清除标记菜单.
    // tmpPopMenu.addSeparator();
    //
    // tmpMenuItem = new JMenuItem(DlgConst.CATEGORIES, 'G');
    // tmpMenuItem.setAction(new CategoriesAction());
    // tmpPopMenu.add(tmpMenuItem);//类别.
    // tmpPopMenu.addSeparator();
    //
    // tmpMenuItem = new JMenuItem(PopupMenuConstant.DELETE, 'D');
    // tmpMenuItem.setAction(new DeleteAction());
    // tmpPopMenu.add(tmpMenuItem); //删除.
    // }else{
    // JMenuItem tmpMenuItem = new JMenuItem(PopupMenuConstant.CONTACT_NEWCONTACT, 'N');
    // tmpMenuItem.setPreferredSize(new Dimension(100, CustOpts.LBL_HEIGHT));
    // tmpMenuItem.addActionListener(new NewContactAction(null));
    // tmpPopMenu.add(tmpMenuItem);//新建
    // tmpMenuItem = new JMenuItem(PopupMenuConstant.OPEN_SELECTED_ITEM, 'O');
    // tmpMenuItem.addActionListener(new OpenAction());
    // tmpPopMenu.add(tmpMenuItem);//打开
    // tmpPopMenu.addSeparator();
    // tmpMenuItem = new JMenuItem(PopupMenuConstant.DELETE, 'D');
    // tmpMenuItem.addActionListener(new DeleteAction());
    // tmpPopMenu.add(tmpMenuItem);//删除
    // }
    // tmpPopMenu.show(prmComp, x, y);
    // }

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

    public Object[][] processContent(
            Object[][] pContents,
            Object[] pFieldNames) {
        if (pContents == null || pFieldNames == null || pContents.length < 1 || pFieldNames.length < 1
                || pContents[0].length != pFieldNames.length)
            return pContents;
        for (int i = 0; i < pFieldNames.length; i++)
            if (pFieldNames[i].equals("帐务")) {
                for (int m = 0; m < pContents.length; m++)
                    if (pContents[m][i] != null)
                        pContents[m][i] = String.valueOf(((Integer) pContents[m][i]).floatValue() / 100);
            }
        return pContents;
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
        actionFlags.put("NewContactAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
    }

    private HashMap actionFlags;
}
