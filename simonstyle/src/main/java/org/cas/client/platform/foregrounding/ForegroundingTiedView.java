package org.cas.client.platform.foregrounding;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.ViewFormat;
import org.cas.client.platform.pimview.AbstractPIMView;
import org.cas.client.platform.pimview.CommonTitle;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.IPIMCellEditor;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pimview.util.PIMViewUtil;
import org.cas.client.resource.international.IntlModelConstants;

public class ForegroundingTiedView extends AbstractPIMView implements MouseListener, FocusListener, KeyListener,
        ComponentListener, Releasable, PropertyChangeListener {
    /**
     * @called by: AbstractAppPane 生成一个新的View实例，但是这时候View是空的，没有关于IApplication或ViewInfo信息的。 所以预览区域不能初始化－－涉及对话盒panel信息。
     */
    public ForegroundingTiedView() {
        titlePane = new CommonTitle(0);// 创建标题面板
        titlePane2 = new CommonTitle(0);// 创建标题面板
        table = new PIMTable();
        scrollPane = new PIMScrollPane();

        // 界面上的所有组件构造结束－－－－－－－－－－
        table.setView(this);
        table.setAutoResizeMode(PIMTable.AUTO_RESIZE_OFF);
        table.setToggleRow(-1000);
        table.getTableHeader().setReorderingAllowed(false);// 设置表格不允许拖拉

        scrollPane.setViewportView(table);
        add(titlePane);
        add(scrollPane);
        add(titlePane2);
        // 界面搭建立完成－－－－－－－－－－

        scrollPane.getViewport().addMouseListener(this);// 该方法中对table的各种组件增加事件监听，加到TableView上。
        scrollPane.getViewport().addFocusListener(this);
        table.addMouseListener(this);
        table.addKeyListener(this);
        table.addFocusListener(this);
        table.addComponentListener(this);
        table.addHeadFocusListener(this);
        // 监听器添加完成－－－－－－－－－－
    }

    // 实现IView的接口-------------------------------------------------------------------
    public void setIconAndTitle(
            Icon prmIcon,
            String prmTitle) {
        titlePane.setPaneTitle(prmIcon, prmTitle);
        titlePane2.setPaneTitle(prmIcon, prmTitle);
    }

    public static int titlePaneHeight = 20;// 默认标题高度

    /** 自我更新一下 */
    public void updatePIMUI() {
    }

    /**
     * 用来处理表格头中的鼠标点击事件, 在某些应用视图为 PIMTable 表格时,表格头的鼠标动作,会激发排序,列宽度尺寸等 发生变化,表格会在适当时候调用本方法以保存该项信息
     * 
     * @param changedType
     *            见本类的几个常量
     * @param changedInfo
     *            封装变化的信息 在排序时,代表一个 int [2], 第一个为表格头上的排序列的索引,第二个为表示升降序标 志, 因为表格头目前尚未提供鼠标操作后得到排序信息的方法暂如此.其他几种变化目前似乎 用这个参数
     * @called by 如HeaderListener(目前)
     */
    public void updateTableInfo(
            int changedType,
            Object changedInfo) {
        if (changedType == IView.SORTOR_CHANGED && changedInfo != null && changedInfo instanceof int[]) {
            int[] prmSortInfo = (int[]) changedInfo;
            // 构造排序信息的字符串
            String sortCritia;
            String ascent = Integer.toString(prmSortInfo[1]);
            // 得到在表格列模型中的实际列;
            PIMTableColumn sortColumn = table.getColumnModel().getColumn(prmSortInfo[0]);

            // 克隆处理,这样安全
            PIMViewInfo viewInfo = (PIMViewInfo) currentViewInfo.clone();
            // 用以保存在循环过程中找到的索引值
            int indexInDatabase = 0;
            // 用以标记出是否找到索引值
            boolean finded = false;

            for (int i = 0, count = headerTitles.length; i < count; i++) {
                // 判断常量中的哪个字段与排序列的相等
                if (sortColumn.getHeaderValue() == headerTitles[i]) {
                    // 找到便记下该索引并置标记,跳出
                    indexInDatabase = displayIndexes[i];
                    finded = true;
                    break;
                }
            }
            // 找到了该字段
            if (finded) {
                // TODO: 在实际情况下,有些列是不可排序的,应弹出个信息框后返回,
                // 构造排序信息
                sortCritia = new StringBuffer().append(indexInDatabase).append(",").append(ascent).toString();
                viewInfo.setSortCritia(sortCritia);
                // 入库操作
                CASControl.ctrl.getModel().updateViewInfo(viewInfo);
            }
        } else if (changedType == IView.COLUMN_WIDTH_CHANGED) {
            // 先造出一个字符串
            String widthStr = CASUtility.EMPTYSTR;
            for (int i = 0, count = table.getColumnModel().getColumnCount(); i < count; i++) {
                int tmpColumnWidth = table.getColumnModel().getColumn(i).getWidth();
                // 表格默认列宽为10075,在一般情况下不可能出现这个值,但在PIMTable 中
                // 表格有时得到的就是这个值,这时作个小处理,如果列宽大于5000,说明取
                // 值有问题,就退出不存盘了. 2003.10.10
                if (tmpColumnWidth > 5000) {
                    return;
                }
                widthStr = widthStr + tmpColumnWidth + ",";
            }
            // 砍掉尾巴上的逗号
            if (widthStr.length() != 0) {
                widthStr = widthStr.substring(0, widthStr.length() - 1);
            }
            // 克隆处理,这样安全
            PIMViewInfo viewInfo = (PIMViewInfo) currentViewInfo.clone();
            // 保存数据
            viewInfo.setFieldWidths(widthStr);
            CASControl.ctrl.getModel().updateViewInfo(viewInfo);
        }
    }

    /**
     * 更新Model
     */
    public void viewToModel() {
        if (table.hasEditor() && table.getTableHeader().isEditing()) // 如果有快速编辑栏,且快速编辑栏正处于被编辑状态.则要追加一条记录.
        {
            IPIMCellEditor tmpCellEditorInHead = table.getTableHeader().getCellEditor(); // 得到快速编辑栏上的编辑器.
            if (tmpCellEditorInHead != null) // 如果得到了,说明表格头正在进行编辑,就另其停止编辑.
            {
                tmpCellEditorInHead.stopCellEditing();
                table.getTableHeader().removeEditor(); // 并且将编辑器从快速编辑栏上移走.
            }

            Vector tmpShiftEditorContent = ((DefaultPIMTableColumnModel) table.getColumnModel()).getNewRecord(); // 从model中申请一条新记录.

            PIMRecord tmpRecord = new PIMRecord(); // 构造一个空的PIMRecord,并对其进行类型设置;
            Hashtable tmpValueHash = new Hashtable();
            tmpRecord.setFieldValues(tmpValueHash);
            int tmpAppType = getApplication().getActiveViewInfo().getAppIndex();
            tmpRecord.setAppIndex(tmpAppType);

            Object tmpValue; // 用于在循环中咱存每一个Cell中内容的临时变量.
            for (int i = 0; i < headerTitles.length; i++) // 按列遍历,对PIMRecord的各项属性进行设置--------------------------------
            {
                tmpValue = tmpShiftEditorContent.get(i); // tmpValue指向新记录的每一个字段.
                tmpValue = PIMViewUtil.getValueForSaveToDB(tmpAppType, displayIndexes[i], tmpValue);
                if (tmpValue != null) {
                    tmpRecord.setFieldValue(displayIndexes[i], tmpValue);
                } else if (displayIndexes[i] == ModelDBCons.IMPORTANCE) {
                    tmpRecord.setFieldValue(displayIndexes[i], new Short((short) 1));
                }
            } // 至此完成了新的PIMRecord记录的构造---------------------------------------------------

            if (tmpValueHash.size() != 0) // 将构造好的PIMRecord存入model(数据库).
            {
                if (tmpAppType == ModelCons.TASK_APP) {
                    processRelationFields(tmpValueHash); //
                }
                tmpRecord.setInfolderID(CustOpts.custOps.getActivePathID());
                CASControl.ctrl.getModel().insertRecord(tmpRecord, true);

                int columnCount = table.getColumnCount(); // 清空快速编辑栏上的内容.
                for (int i = 0; i < columnCount; i++) {
                    table.getTableHeader().setValueAt(null, i);
                }
            }
        } else {
            int tmpSeleRow = table.getSelectedRow(); // 得到选中行
            if (tmpSeleRow == -1) // 除错,当从Table体中改选中快速编辑栏时,触发快速编辑栏的鼠标监听器BasicPIMTableUI的mousePress方法,
            { // 该方法中调用了adjustFocusAndSelection方法,其中又调用viewToModel.
                return;
            }

            int tmpRecordID = ((Integer) table.getValueAt(tmpSeleRow, 0)).intValue(); // 得到Record的ID号.
            int tmpAppType = currentViewInfo.getAppIndex();
            PIMRecord tmpRecord =
                    CASControl.ctrl.getModel()
                            .selectRecord(tmpAppType, tmpRecordID, CustOpts.custOps.getActivePathID());
            // 从model中得到与选中行相对应的PIMRecord.
            Hashtable tmpValueHash = tmpRecord.getFieldValues();
            // TODO:
            // tmpRecord为从model中取得的记录，其中的hashtable可能为空，从而导致后面循环中出现空指针，需要理解流程后从根本解决掉。下面的判断是临时处理：
            if (tmpValueHash == null) {
                tmpValueHash = new Hashtable();
                ErrorUtil.write("once more: get a record with null hashtable, PIMTableView.viewToModel()");
            }
            List tmpValuesInEditingRow = table.getEditingRowRecords(); // 取出Table中正在编辑的行中的所有值.

            if (tmpValuesInEditingRow == null || tmpValuesInEditingRow.size() < 1) // 为小心起见的除错处理,理论上不会出现不满足的情况.
            {
                ErrorUtil.write("TableView.viewToModel() find a row with 0 columns.");
                return;
            }

            Object tmpValue = null;
            int saveItemCount = 0; // 这个标志表示有数据改动
            for (int i = 0; i < displayIndexes.length; i++) {
                tmpValue = tmpValuesInEditingRow.get(i);
                tmpValue = PIMViewUtil.getValueForSaveToDB(tmpAppType, displayIndexes[i], tmpValue);
                if (tmpValue != null && shouldSave(tmpValueHash.get(PIMPool.pool.getKey(displayIndexes[i])), tmpValue)) // @NOTE:人为的清空动作会导致tmpValue==PIMUtility.EMPTYSTR,不会导致为null.
                {
                    saveItemCount++;
                    tmpValueHash.put(PIMPool.pool.getKey(displayIndexes[i]), tmpValue);
                }
            }
            // 不知为何,表格在向下向上键操作后焦点会出问题,这个参数一时还不可用
            if (saveItemCount > 0) {
                CASControl.ctrl.getModel().updateRecord(tmpRecord, true);
            }
        }
    }

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的ID的方法
     * 
     * @return 所有选中的记录的ID
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public int[] getSelectedRecordIDs() {
        int[] tmpRows = table.getSelectedRows();

        if (tmpRows == null || tmpRows.length == 0)// 表示表格没有选中状态
        {
            return new int[0];
        } else {
            int[] tmpIDs = new int[tmpRows.length];
            try {
                for (int i = 0; i < tmpRows.length; i++) {
                    tmpIDs[i] = Integer.parseInt(table.getValueAt(tmpRows[i], 0).toString());
                }
            } catch (Exception e) {
                ErrorUtil.write(e);
            }
            return tmpIDs;
        }
    }

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的方法
     * 
     * @return 所有选中的记录
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public Vector getSelectedRecords() {
        int[] tmpRows = table.getSelectedRows();
        if (tmpRows == null || tmpRows.length == 0)
            return new Vector(0); // 先排除表格没有被选中的情况
        else {
            ICASModel tmpModel = CASControl.ctrl.getModel();
            Vector tmpResult = new Vector(tmpRows.length);
            int tmpAppType = getApplication().getActiveViewInfo().getAppIndex();

            int[] ids = new int[tmpRows.length];
            for (int i = 0; i < tmpRows.length; i++)
                ids[i] = Integer.parseInt(table.getValueAt(tmpRows[i], 0).toString());
            tmpResult = tmpModel.selectRecords(tmpAppType, ids, CustOpts.custOps.getActivePathID());
            return tmpResult;
        }
    }

    /**
     * 应控制的要求,需要有选取视图中的所有记录的方法
     * 
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public void seleteAllRecords() {
        table.setRowSelectionInterval(0, table.getRowCount() - 1);
        table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
    }

    /**
     * 处理行高/字体问题,本方法在视图更新和字体对话盒操作后,以及本类init方法中调用,以处理所有字体和前景色
     * 
     * 对于以下几个应用取到的视图显示的记录列表时，列表的最后几个字段依次为：
     * 
     * ModelConstants.INBOX_APP ModelDBConstants.FOLOWUPENDTIME ModelDBConstants.READED
     * 
     * 
     * ModelConstants.CONTACT_APP ModelDBConstants.FOLOWUPENDTIME ModelDBConstants.READED
     * 
     * 
     * ModelConstants.TASK_APP ModelDBConstants.END_TIME ModelDBConstants.FINISH_FLAG ModelDBConstants.READED
     * 
     * ModelConstants.CALENDAR_APP ModelDBConstants.CALENDAR_END_TIM
     * 
     * 对于已删除记录最后两个字段为INFOLDER字段和APPTYPE字段，为区分绘制已删除项中各条记录的ICON图标
     */
    public void updateFontsAndColor() {
        ViewFormat tmpViewFormat = currentViewInfo.getViewFormat();
        if (tmpViewFormat == null) {
            Font tmpFont = CustOpts.custOps.getFontOfDefault();
            tmpViewFormat = new ViewFormat();
            tmpViewFormat.setFontName(tmpFont.getFontName());
            tmpViewFormat.setFontStyle(tmpFont.getStyle());
            tmpViewFormat.setFontSize(tmpFont.getSize());
            currentViewInfo.setViewFormat(tmpViewFormat);
        }

        if (contents == null)
            return;

        // 处理本类的属性池,以后可能还要根据邮件规则来设置字体,因Model没有实现格式的保存,我先做个实验,在邮件中设置一下,
        int tmpAppType = getApplication().getActiveViewInfo().getAppIndex();
        int tmpActiveFolderID = getApplication().getActiveViewInfo().getPathID();

        if (tmpAppType == ModelCons.INBOX_APP || tmpAppType == ModelCons.OUTBOX_APP
                || tmpAppType == ModelCons.SENDED_APP || tmpAppType == ModelCons.DRAFT_APP) {
            // 假设
            int fontRuleCount = 4;
            ICASModel model = CASControl.ctrl.getModel();
            for (int i = 0; i < fontRuleCount; i++) {
                ViewFormat viewFormat = model.getViewFormat(tmpAppType, 1, i, tmpActiveFolderID);
                lazyFontAndColorPool.put(Integer.toString(i),
                        new Font(viewFormat.getFontName(), viewFormat.getFontStyle(), viewFormat.getFontSize()));
                lazyFontAndColorPool.put(Integer.toString(i).concat("Color"), viewFormat.getFontColor());
                lazyFontAndColorPool.put(Integer.toString(i).concat("HaveStrikethrough"),
                        viewFormat.isHaveStrikethrough() ? Boolean.TRUE : Boolean.FALSE);
            }
            // TODO: 缺省字体从何处得到?
            // 缺省字体和颜色
            Font defaultFont = new Font("Dialog", 0, 12);
            Color defaultColor = CustOpts.custOps.getReadedMailColor();
            lazyFontAndColorPool.put("-1", defaultFont);
            lazyFontAndColorPool.put("-1Color", defaultColor);
            lazyFontAndColorPool.put("-1HaveStrikethrough", Boolean.FALSE);
            // 属性池处理完毕------------------------------

            // 用于处理过期
            Date currentDate = GregorianCalendar.getInstance().getTime();

            for (int i = 0; i < contents.length; i++) {
                Object[] tmpV = contents[i];
                String key = "-1";

                int size = tmpV.length;

                // 处理到期和过期
                Object tmpFlag = tmpV[size - 2]; // ModelDBConstants.FOLOWUPENDTIME
                // = size - 2 //倒数第二个
                if (tmpFlag != null) {
                    Date tmpDate = (Date) tmpFlag;
                    if (currentDate.compareTo(tmpDate) == 1) // 当前时间大于设置的时间,表示已过期
                    {
                        if (tmpDate.getYear() == currentDate.getYear() // 年月日相同表示到期
                                && tmpDate.getMonth() == currentDate.getMonth()
                                && tmpDate.getDate() == currentDate.getDate()) {
                            key = "2";
                            processASingleRow(i, key);
                            continue;
                        } else {
                            key = "3";
                            processASingleRow(i, key);
                            continue;
                        }
                    }
                }

                tmpFlag = tmpV[size - 1]; // ModelDBConstants.READED = size -
                // 1
                // //最后一个
                // 处理已读,为空是未读的
                if (tmpFlag != null && tmpFlag.toString().equals(PIMPool.BOOLEAN_TRUE)) {
                    key = "-1";
                    processASingleRow(i, key);
                    continue;
                } else {
                    key = "0";
                    processASingleRow(i, key);
                    continue;
                }
            } // end for
        } else if (true)// tmpAppType == ModelConstants.CONTACT_APP)
        {
            // 假设;我们目前只能处理通讯组,未读,过期,到期的联系人
            int fontRuleCount = 4;
            for (int i = 0; i < fontRuleCount; i++) {
                // ViewFormat viewFormat =
                // model.getViewFormat(tmpAppType,1,i,PIMUtility.EMPTYSTR);
                // lazyFontAndColorPool.put(Integer.toString(i),new
                // Font(viewFormat.getFontName(),viewFormat.getFontStyle(),viewFormat.getFontSize()));
            }

            lazyFontAndColorPool.put("0", new Font("Dialog", 0, 12)); //
            lazyFontAndColorPool.put("1", new Font("Dialog", 1, 12));
            lazyFontAndColorPool.put("2", new Font("Dialog", 0, 12));
            lazyFontAndColorPool.put("3", new Font("Dialog", 0, 12));

            lazyFontAndColorPool.put("0Color", Color.black);
            lazyFontAndColorPool.put("1Color", Color.black);
            lazyFontAndColorPool.put("2Color", Color.blue);
            lazyFontAndColorPool.put("3Color", Color.red);

            lazyFontAndColorPool.put("-1HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("0HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("1HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("2HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("3HaveStrikethrough", Boolean.FALSE);

            // 缺省字体和颜色
            Font defaultFont = new Font("Dialog", 0, 12);
            Color defaultColor = Color.black;
            lazyFontAndColorPool.put("-1", defaultFont);
            lazyFontAndColorPool.put("-1Color", defaultColor);
            // 属性池处理完毕

            // 用于处理过期
            Date currentDate = GregorianCalendar.getInstance().getTime();

            for (int i = 0; i < contents.length; i++) {
                Object[] tmpV = contents[i];
                int size = tmpV.length;
                Object tmpFlag = tmpV[size - 3]; // ModelDBConstants.FOLOWUPENDTIME
                // = size - 3 //倒数第三个
                String key = "-1";

                if (tmpFlag != null) // 处理到期和过期
                {
                    Date tmpDate = (Date) tmpFlag;
                    int result = currentDate.compareTo(tmpDate);
                    // 当前时间大于设置的时间,表示已过期
                    if (result == 1) {
                        key = "3";
                        processASingleRow(i, key);
                        continue;
                    }
                    // 年月日相同表示到期
                    else if (tmpDate.getYear() == currentDate.getYear() && tmpDate.getMonth() == currentDate.getMonth()
                            && tmpDate.getDate() == currentDate.getDate()) {
                        key = "2";
                        processASingleRow(i, key);
                        continue;
                    }
                }
                // TODO: 处理未读的联系人,这里要求邮件那边要处理一下一个标记,现在暂时以不为空表示未读
                tmpFlag = tmpV[size - 2]; // ModelDBConstants.READED = size -
                // 2
                // //倒数第二个
                // 处理已读,为空是未读的
                if (tmpFlag != null && !tmpFlag.toString().equals("-1")) {
                    key = "1";
                    processASingleRow(i, key);
                    continue;
                }
                // 处理通讯组
                tmpFlag = tmpV[size - 1]; // ModelDBConstants.TYPE 倒数第一个
                if ("1".equals(tmpFlag)) {
                    key = "0";
                    processASingleRow(i, key);
                    continue;
                } else {
                    key = "-1";
                    processASingleRow(i, key);
                    continue;
                }
            } // end for
        } else if (tmpAppType == ModelCons.TASK_APP) {
            // 假设;
            int fontRuleCount = 4;
            for (int i = 0; i < fontRuleCount; i++) {
                // ViewFormat viewFormat =
                // model.getViewFormat(tmpAppType,1,i,PIMUtility.EMPTYSTR);
                // lazyFontAndColorPool.put(Integer.toString(i),new
                // Font(viewFormat.getFontName(),viewFormat.getFontStyle(),viewFormat.getFontSize()));
            }

            lazyFontAndColorPool.put("0", new Font("Dialog", 0, 12)); //
            lazyFontAndColorPool.put("1", new Font("Dialog", 1, 12));
            lazyFontAndColorPool.put("2", new Font("Dialog", 0, 12));
            lazyFontAndColorPool.put("3", new Font("Dialog", 0, 12));

            lazyFontAndColorPool.put("0Color", CustOpts.custOps.getReadedMailColor());
            lazyFontAndColorPool.put("1Color", CustOpts.custOps.getReadedMailColor());
            lazyFontAndColorPool.put("2Color", Color.blue);
            lazyFontAndColorPool.put("3Color", Color.red);

            // 缺省字体和颜色
            Font defaultFont = new Font("Dialog", 0, 12);
            Color defaultColor = Color.black;
            lazyFontAndColorPool.put("-1", defaultFont);
            lazyFontAndColorPool.put("-1Color", defaultColor);

            lazyFontAndColorPool.put("-1HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("0HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("1HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("2HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("3HaveStrikethrough", Boolean.FALSE);
            // 属性池处理完毕

            // 用于处理过期
            Date currentDate = GregorianCalendar.getInstance().getTime();

            if (contents == null) {
                return;
            }

            for (int i = 0; i < contents.length; i++) {
                Object[] tmpV = contents[i];
                int size = tmpV.length;

                String key = "-1";
                Object tmpFlag;
                // //先处理过期
                // Object tmpFlag = tmpV[size - 3]; //ModelDBConstants.END_TIME
                // // 倒数第三个
                // if (tmpFlag != null)
                // {
                // Date tmpDate = (Date)tmpFlag;
                // int result = currentDate.compareTo(tmpDate);
                // //当前时间大于设置的时间,表示已过期
                // if (result == 1)
                // {
                // key = "2";
                // processASingleRow(i, key);
                // continue;
                // }
                // }

                // 取完成字段
                tmpFlag = tmpV[size - 2]; // ModelDBConstants.FINISH_FLAG ==
                // size - 2 倒数第二个
                if (tmpFlag != null
                        && (tmpFlag.toString().equals(PIMPool.BOOLEAN_TRUE) || tmpFlag.toString().equals("1"))) {
                    // TODO: 处理未读的联系人,这里要求任务那边要处理一下一个标记,现在暂时以不为空表示未读
                    tmpFlag = tmpV[size - 1]; // ModelDBConstants.READED ==
                    // size
                    // - 1 倒数第一个
                    // 处理已读,为空是未读的
                    if (tmpFlag != null && !tmpFlag.toString().equals("-1")) {
                        key = "1";
                        processASingleRow(i, key);
                        continue;
                    } else {
                        key = "0";
                        processASingleRow(i, key);
                        continue;
                    }
                }
                // 处理已读,为空是未读的
                if (tmpFlag != null && !tmpFlag.toString().equals("-1")) {
                    key = "3";
                    processASingleRow(i, key);
                    continue;
                } else {
                    key = "-1";
                    processASingleRow(i, key);
                    continue;
                }
            } // end for
        } else if (tmpAppType == ModelCons.CALENDAR_APP) {
            // 假设;
            int fontRuleCount = 2;
            for (int i = 0; i < fontRuleCount; i++) {
                // ViewFormat viewFormat =
                // model.getViewFormat(tmpAppType,1,i,PIMUtility.EMPTYSTR);
                // lazyFontAndColorPool.put(Integer.toString(i),new
                // Font(viewFormat.getFontName(),viewFormat.getFontStyle(),viewFormat.getFontSize()));
            }

            lazyFontAndColorPool.put("0", new Font("Dialog", 0, 12)); //
            lazyFontAndColorPool.put("1", new Font("Dialog", 1, 12));

            lazyFontAndColorPool.put("0Color", Color.black);
            lazyFontAndColorPool.put("1Color", Color.black);

            // 缺省字体和颜色
            Font defaultFont = new Font("Dialog", 0, 12);
            Color defaultColor = Color.black;
            lazyFontAndColorPool.put("-1", defaultFont);
            lazyFontAndColorPool.put("-1Color", defaultColor);

            lazyFontAndColorPool.put("-1HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("0HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("1HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("2HaveStrikethrough", Boolean.FALSE);
            lazyFontAndColorPool.put("3HaveStrikethrough", Boolean.FALSE);
            // 属性池处理完毕

            // 用于处理过期
            Date currentDate = GregorianCalendar.getInstance().getTime();

            if (contents == null) {
                return;
            }

            for (int i = 0; i < contents.length; i++) {
                Object[] tmpV = contents[i];

                int size = tmpV.length;

                String key = "-1";

                // 先处理过期
                Object tmpFlag = tmpV[size - 1]; // ModelDBConstants.CALENDAR_END_TIME
                // = size - 1 倒数第一个
                if (tmpFlag != null) {
                    Date tmpDate = (Date) tmpFlag;
                    int result = currentDate.compareTo(tmpDate);
                    // 当前时间大于设置的时间,表示已过期
                    if (result == 1) {
                        key = "1";
                        processASingleRow(i, key);
                        continue;
                    }
                }
                // 处理已读,为空是未读的
                if (tmpFlag != null && !tmpFlag.toString().equals("-1")) {
                    key = "0";
                    processASingleRow(i, key);
                    continue;
                } else {
                    key = "-1";
                    processASingleRow(i, key);
                    continue;
                }
            } // end for
        }
    }

    /**
     * called by PIMApplication,when 调此方法:对Table视图实现上下翻页,对文本视图实现按日期翻页,卡片 视图实现左右翻页.
     */
    public void setPageBack(
            boolean prmIsBack) {
        JViewport viewPort = scrollPane.getViewport();
        if (scrollPane.getVerticalScrollBar().isShowing()) {
            Point vp = viewPort.getViewPosition();
            int rowHeight = table.getParent().getHeight() + 5;
            int tableHeight = table.getHeight();
            if (prmIsBack) {
                if (vp.y - rowHeight > 0) {
                    vp.y -= rowHeight;
                } else {
                    vp.y = 0;
                }
            } else {
                if (vp.y + rowHeight < tableHeight) {
                    vp.y += rowHeight;
                } else {
                    vp.y = scrollPane.getVerticalScrollBar().getValue();
                }
            }
            viewPort.setViewPosition(vp);
        }
    }

    public void closed() {
        if (table.hasEditor()) {
            table.editingStopped(null);
        }
    }

    // 实现MouseListener接口------------------------------------------------------------------
    /**
     * Invoked when the mouse button has been clicked (pressed and released) on the ViewPort 或者 PIMTable.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
        if (e.getModifiers() == MouseEvent.META_MASK) // 处理鼠标右键菜单
        {
            getApplication().showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
        } else if (e.getClickCount() > 1)// 不是右键,但是是双击的情况
        {
            Object source = e.getSource();
            // 将table组件的鼠标双击事件转换为打开一个联系人的详细信息事件。
            // 或者新建一个联系人事件（点在空白处时）。
            if (source == table) {
                // 如果选中有效,且是鼠标左键双击以上的事件.
                if (table.getSelectedRow() != -1 && SwingUtilities.isLeftMouseButton(e)) {
                    int tmpSeleID = ((Integer) table.getValueAt(table.getSelectedRow(), 0)).intValue(); // 得到选中记录对应的ID值.@NOTE:0位置为专为ID值保留.
                    table.editingStopped(null); // 谨防表格中有Cell在编辑,如果没有改方法会直接退出.
                    getApplication().processMouseDoubleClickAction(table, e.getX(), e.getY(),
                            PIMPool.pool.getKey(tmpSeleID).intValue());
                }
            }
            // viewPort的鼠标事件处理,全部转移给application.=================================================================================
            else if (source == scrollPane.getViewport()) {
                if (SwingUtilities.isLeftMouseButton(e)) // 左键双击---------------
                {
                    getApplication().processMouseDoubleClickAction(table, e.getX(), e.getY(), -1); // 其中包含弹出新建联系人对话盒等动作.
                }
            }
        }
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on the ViewPort 或者 PIMTable.
     * 问题在于如果仅仅在这个方法内部改变系统的"有记录选中"状态的话,那么键盘等其它硬件输入设备触发的选中将没有相应的系统状态调整. 为了能够一劳永逸地应付所有的输入设备,我们应该选择在更底层地UI中找合适地监控点.
     * 这样做的缺点是:1/系统内部的模块化粒度变粗.2/会不会有这种情况:二次开发厂商希望通过二次开发选中一条记录,但不影响系统状态.
     * 目前认为这两个缺点不构成问题:View认识了控制的问题,可以通过后续版本的进一步模块化完成,改成监听器模式.二次开发厂商应该用更好的方式 达到自己的目的,而不应该希望系统发生不合理的机制.
     * 所以,对于本视图,我们选择在table的SelectionModelListener中设点.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
        // EMenuSelectionManager.clearPath();//清除菜单，比如右键菜单，主菜单。
        int[] selects = table.getSelectedRows();
        int tmpRow = table.getSelectedRow();// 得到记录,并判断记录有无变化?无变化或无选中/当前视图没有要求预览则返回.
        if (selects != null && selects.length > 0 && CustOpts.custOps.isPreviewShown() && oldSelectedRow != tmpRow
                && tmpRow >= 0) {
            oldSelectedRow = table.getSelectedRow();

            int tmpRecordID = Integer.parseInt(table.getValueAt(oldSelectedRow, 0).toString()); // 取出选中行中的记录ID，
            final PIMRecord tmpRec = CASControl.ctrl.getModel().selectRecord( // 并从model中抓出记录。
                    currentViewInfo.getAppIndex(), tmpRecordID, // TODO:应该改为从Table
                    CustOpts.custOps.getActivePathID()); // 的modle中取出相关信息

            if (tmpRec != null && getDlg().setContents(tmpRec)) // 正常显示的话则设置已读等属性.
            {
                getApplication().processMouseClickAction(table); // 此处需要交给应用处理,因为不同应用会有所不同,如邮件可能
            } // 会发送回执,并设置已读标志等.
        }

        if (selects.length == 1 && tmpRow >= 0) {
            int id = Integer.parseInt(table.getValueAt(tmpRow, 0).toString());
            table.setSelectedRecordID(id);
        } else {
            table.setSelectedRecordID(-1);
        }
    }

    /**
     * Invoked when a mouse button has been released on the ViewPort 或者 PIMTable.
     * 
     * @NOTE: 如果正在table的单元格的编辑器中编辑时,触发了Table的mouse方法,
     *        必定已经触发过了CellEditorListener接口中的editingStopped,即是说:已经触发过viewToModel(存盘方法).
     *        而如果正在快速编辑栏上编辑时,触发了Table或viewport的mouse方法,则还没有存盘过.
     *        如果正在table的编辑器中编辑,点击viewPort触发本方法,则没有触发过editingStopped方法,也即没有存过盘.
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        if (table.hasEditor()) // 有编辑栏的话(按目前的规格,有快速编辑栏同时意味着表格体允许编辑),需要检查上面是否有内容在编辑,如果有并且必要的字段有内容,则保存到model.否则报错.
        {
            if (e.getSource() == table) // 如果点在table上,则如果本来在table上编辑,stopEditing方法已被触发过,如果本来在快速编辑栏上编辑,则还没有存盘.
            {
                // 本来因该判断的,但鉴于如果从快速编辑栏转到table体中,仍需要满足PIMViewUtil.isDisplayAsHasValue()
                // == 1
                // 的判断,而反之,如果不是从速编辑栏转到table体,编辑栏必定全是null,返回值必为-1,故都用该条件作一次性判断.
                if (getApplication().getActiveViewInfo().getAppIndex() == ModelCons.CONTACT_APP) {
                    if (PIMViewUtil.isDisplayAsHasValue(table.getTableHeader()) == 1) {
                        viewToModel();
                    } else if (PIMViewUtil.isDisplayAsHasValue(table.getTableHeader()) == 0) {
                        // “显示为”是一条联系人记录的关键字段，不能为空。请为该联系人记录的“显示为”字段输入内容。
                        SOptionPane.showErrorDialog(MessageCons.W10619);
                        return;
                    }
                } else if (getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP) {
                    if (PIMViewUtil.isSubjectAsHasValue(table.getTableHeader()) == 1) {
                        viewToModel();
                    } else if (PIMViewUtil.isSubjectAsHasValue(table.getTableHeader()) == 0) { // “主题”是一条任务记录的关键字段，不能为空。请为该任务记录的“主题”字段输入内容。
                        SOptionPane.showErrorDialog(MessageCons.W10622);
                        // TODO:return ;
                    }
                }
                // }
                // else 不处理----------------
            } else // if(e.getSource() == scrollPane.getViewport())
            // 如果是点在ViewPort上,
            {
                if (table.getEditorComponent() != null) // 因为如果原来正在Table的单元中,点击viewport不会触发PIMTable的editStop事件,
                { // 所以这里判断如果本来是在Table中编辑,需要补充存盘.
                    table.editingStopped(null);
                    table.clearSelection();
                } else
                // 本来因该判断的,但鉴于如果从快速编辑栏转到table体中,仍需要满足PIMViewUtil.isDisplayAsHasValue()
                // == 1
                { // 的判断,而反之,如果不是从速编辑栏转到table体,编辑栏必定全是null,返回值必为-1,故都用该条件作一次性判断.
                    if (getApplication().getActiveViewInfo().getAppIndex() == ModelCons.CONTACT_APP) {
                        if (PIMViewUtil.isDisplayAsHasValue(table.getTableHeader()) == 1) {
                            viewToModel();
                        } else if (PIMViewUtil.isDisplayAsHasValue(table.getTableHeader()) == 0) {
                            // “显示为”是一条联系人记录的关键字段，不能为空。请为该联系人记录的“显示为”字段输入内容。
                            SOptionPane.showErrorDialog(MessageCons.W10619);
                            return;
                        }
                    } else if (getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP) {
                        if (PIMViewUtil.isSubjectAsHasValue(table.getTableHeader()) == 1) {
                            viewToModel();
                        } else if (PIMViewUtil.isSubjectAsHasValue(table.getTableHeader()) == 0) { // “主题”是一条任务记录的关键字段，不能为空。请为该任务记录的“主题”字段输入内容。
                            SOptionPane.showErrorDialog(MessageCons.W10622);
                            return;
                        }
                    }
                }
            }
        }
    }

    // 实现FocusListener的接口------------------------------------------------------------
    // readed by : 只有联系人视图中的表格是可以编辑的.
    /**
     * Invoked when a component gains the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusGained(
            FocusEvent e) {
    }

    /**
     * Invoked when a component loses the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusLost(
            FocusEvent e) {
        if (table.hasEditor() && table.getTableHeader().isEditing()) {
            IPIMCellEditor tmpCellEditorInHead = table.getTableHeader().getCellEditor();
            if (e.getSource() == table.getTableHeader() && tmpCellEditorInHead != null) {
                tmpCellEditorInHead.stopCellEditing();
                table.getTableHeader().removeEditor();
            }
        }
    }

    // 实现ComponentLisener的接口-------------------------------------------------------
    /** Invoked when a key has been Typed. */
    public void keyTyped(
            KeyEvent e) {
    }

    /** 处理table视图上的上下键盘事件。@NTOE:本方法中的处理和mousePressed方法比较类似. */
    public void keyPressed(
            KeyEvent e) {
    }

    /** Invoked when a key has been released. */
    public void keyReleased(
            KeyEvent e) {
        if (CustOpts.custOps.isPreviewShown() && e.getKeyCode() == 38 || e.getKeyCode() == 40) {// 只处理上下键，并且存在预览面版的情况。
            int tmpRow = table.getSelectedRow(); // 因为keyPress事件发生时候，table的seledtedRow属性尚未被调整过来，所以需要
            if (oldSelectedRow != tmpRow) { // 如果新老选中行号不同，则进行预览及已读/回执等情况。
                oldSelectedRow = tmpRow;
                int tmpRecordID = Integer.parseInt(table.getValueAt(oldSelectedRow, 0).toString()); // 取出选中行中的记录ID，
                final PIMRecord tmpRec = CASControl.ctrl.getModel().selectRecord( // 并从model中抓出记录。
                        currentViewInfo.getAppIndex(), tmpRecordID, // TODO:应该改为从Table
                        CustOpts.custOps.getActivePathID()); // 的modle中取出相关信息
                if (tmpRec != null && getDlg().setContents(tmpRec)) // 正常显示的话则设置已读等属性.
                    getApplication().processMouseClickAction(table); // 此处需要交给应用处理,因为不同应用会有所不同,
                // 如邮件可能会发送回执,并设置已读标志等.
            }
            table.setSelectedRecordID(Integer.parseInt(table.getValueAt(tmpRow, 0).toString()));
        }
    }

    // 实现ComponentLisener的接口-------------------------------------------------------
    /**
     * Invoked when the component has been made invisible.
     * 
     * @param e
     *            组件变化事件
     */
    public void componentHidden(
            ComponentEvent e) {
    }

    /**
     * Invoked when the component's position changes.
     * 
     * @param e
     *            组件变化事件
     */
    public void componentMoved(
            ComponentEvent e) {
    }

    /**
     * Invoked when the component's size changes.
     * 
     * @param e
     *            组件变化事件
     */
    public void componentResized(
            ComponentEvent e) {
        if (e.getSource() == table && table.hasEditor() && table.getTableHeader().getEditorComponent() != null)
            table.getTableHeader().getEditorComponent().requestFocus();
    }

    /**
     * Invoked when the component has been made visible.
     * 
     * @param e
     *            组件变化事件
     */
    public void componentShown(
            ComponentEvent e) {
    }

    // 实现propertyChangeListener接口------------------------------------------------------------------
    /**
     * This method gets called when a bound property is changed. 保存新设的分隔值
     * 
     * @param e
     *            属性事件
     */
    public void propertyChange(
            PropertyChangeEvent e) {
    }

    // 实现Releaseable接口---------------------------------------------------------------------------
    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     * 
     */
    public void release() {
        if (oldViewInfo != null)
            oldViewInfo.release();
        if (currentViewInfo != null)
            currentViewInfo.release();
        oldViewInfo = null;
        currentViewInfo = null;
        if (table != null) {
            table.removeMouseListener(this);
            table.removeFocusListener(this);
            table.removeComponentListener(this);
            table.getTableHeader().removeFocusListener(this);
            table.removeAll();
            table.release();
            table = null;
        }
        if (scrollPane != null) {
            scrollPane.getViewport().removeAll();
            scrollPane.getViewport().removeMouseListener(this);
            scrollPane.getViewport().removeFocusListener(this);
            scrollPane.release();
            scrollPane = null;
        }
        if (lazyFontAndColorPool != null) {
            lazyFontAndColorPool.clear();
            lazyFontAndColorPool = null;
        }
        // TODO:可能与DefaultPIMTableModel中的dispose有重复的地方
        if (contents != null) {
            for (int i = contents.length; --i >= 0;) {
                Object obj = contents[i];
                if (obj != null && obj instanceof Vector)
                    ((Vector) obj).clear();
                obj = null;
            }
            // contents.clear();
            contents = null;
        }
        displayIndexes = null;
        displayWidths = null;
        headerTitles = null;
        // tableContents = null;
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    public void setBounds(
            int prmX,
            int prmY,
            int prmWidth,
            int prmHeight) {
        super.setBounds(prmX, prmY, prmWidth, prmHeight);

        if (CustOpts.custOps.isBaseBookHide()) {// 非Book风格。
            if (CustOpts.custOps.isPreviewShown()) {
                int tmpPos = CustOpts.custOps.getSplitHeight();
                if (CustOpts.custOps.isViewTopAndDown()) {
                    scrollPane.setBounds(0, titlePaneHeight, prmWidth, tmpPos - titlePaneHeight);
                    getDlg().getContainer().setBounds(0, tmpPos + MainPane.getDividerSize(), prmWidth,
                            prmHeight - tmpPos - MainPane.getDividerSize());
                    getDlg().reLayout();
                } else {
                    scrollPane.setBounds(0, titlePaneHeight, tmpPos, prmHeight - titlePaneHeight);
                    getDlg().getContainer().setBounds(tmpPos + MainPane.getDividerSize(), titlePaneHeight,
                            prmWidth - tmpPos - MainPane.getDividerSize(), prmHeight - titlePaneHeight);
                    getDlg().reLayout();
                }
                titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                titlePane2.setBounds(0, 0, 0, 0);
                // scrollPane2.setBounds(0,0,0,0);
            } else {
                titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                scrollPane.setBounds(0, titlePaneHeight, prmWidth, prmHeight - titlePaneHeight);
                titlePane2.setBounds(0, 0, 0, 0);
                if (dialog != null)
                    getDlg().getContainer().setBounds(0, 0, 0, 0);
            }
        } else { // Book风格。
            int tmpPos = CustOpts.custOps.getSplitHeight();
            if (CustOpts.custOps.isPreviewShown()) {
                if (CustOpts.custOps.isViewTopAndDown()) {
                    titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                    scrollPane.setBounds(0, titlePaneHeight, prmWidth, tmpPos - titlePaneHeight);
                    titlePane2.setBounds(0, tmpPos + MainPane.getDividerSize(), prmWidth, titlePaneHeight);
                    getDlg().getContainer().setBounds(0, tmpPos + MainPane.getDividerSize() + titlePaneHeight,
                            prmWidth, prmHeight - tmpPos - MainPane.getDividerSize() - titlePaneHeight);// 因为总宽度可能为奇数，除以2后由于精度导致一个像素的空白，故用（prmHeight
                    // -
                    // prmHeight
                    // /
                    // 2）避免。
                    getDlg().reLayout();
                } else {
                    titlePane.setBounds(0, 0, tmpPos, titlePaneHeight);
                    scrollPane.setBounds(0, titlePaneHeight, tmpPos, prmHeight - titlePaneHeight);
                    // 因为总宽度可能为奇数，除以2后由于精度导致一个像素的空白，故用（prmHeight - prmHeight /
                    // 2）避免。
                    titlePane2.setBounds(tmpPos + MainPane.getDividerSize(), 0,
                            prmWidth - tmpPos - MainPane.getDividerSize(), titlePaneHeight);
                    getDlg().getContainer().setBounds(tmpPos + MainPane.getDividerSize(), titlePaneHeight,
                            prmWidth - tmpPos - MainPane.getDividerSize(), prmHeight - titlePaneHeight);
                    getDlg().getContainer().setPreferredSize(
                            new Dimension(prmWidth - tmpPos - MainPane.getDividerSize(), prmHeight - titlePaneHeight));
                    getDlg().reLayout();
                }
                // scrollPane2.setBounds(0, 0, 0, 0);
            } else { // 无预览(两侧都是表格）。
                if (CustOpts.custOps.isViewTopAndDown()) {// 上下结构
                    titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                    scrollPane.setBounds(0, titlePaneHeight, prmWidth, prmHeight - titlePaneHeight);// tmpPos
                    // -
                    // titlePaneHeight);
                    titlePane2.setBounds(0, 0, 0, 0);// tmpPos +
                    // MainPane.getDividerSize(),
                    // prmWidth,
                    // titlePaneHeight);
                    // scrollPane2.setBounds(0, tmpPos +
                    // MainPane.getDividerSize() + titlePaneHeight,
                    // prmWidth, prmHeight - tmpPos - MainPane.getDividerSize()
                    // - titlePaneHeight);
                } else { // 左右结构
                    titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                    scrollPane.setBounds(0, titlePaneHeight, prmWidth, prmHeight - titlePaneHeight);
                    titlePane2.setBounds(0, 0, 0, 0);// tmpPos +
                    // MainPane.getDividerSize(),
                    // 0, prmWidth - tmpPos
                    // -
                    // MainPane.getDividerSize(),
                    // titlePaneHeight);
                    // scrollPane2.setBounds(tmpPos + MainPane.getDividerSize(),
                    // titlePaneHeight,
                    // prmWidth - tmpPos - MainPane.getDividerSize(), prmHeight
                    // - titlePaneHeight);
                }
            }
        }
    }

    /** 初始化视图上面应该显示的内容和形式（如列宽等） 该方法被调用之前，setPane和setViewInfo方法必须都是已经设过了的。 */
    public void init(
            boolean prmAppChanged) {
        int tmpOldRowCount = table.getRowCount();
        int tmpOldColCount = table.getColumnCount();
        int tmpOldSeleRowCount = table.getSelectedRowCount();
        int tmpOldSeleRow = table.getSelectedRow();
        int tmpOldSeleCol = table.getSelectedColumn();
        int tmpOldHeaderEditCol = table.getTableHeader().getEditingColumn();
        // 更新前表格的选中等状态记录完成，因为在model数据更新等动作导致表内容的更新，然而更新之后的选中状态不能改变，所以更新数据前把它们记录了一下。

        ICASModel tmpModel = CASControl.ctrl.getModel();
        PIMViewInfo tmpViewInfo = getApplication().getActiveViewInfo();
        tmpViewInfo.setFolderID(5100);
        tmpViewInfo.setAppIndex(CustOpts.custOps.APPNameVec.indexOf("Product"));
        String[] tmpFieldNames = tmpModel.getFieldNames(tmpViewInfo);
        contents = tmpModel.selectRecords(tmpViewInfo); // 得到准备要显示的内容，TODO：要么对该方法返回的数组做尺寸限制，要么尝试改到绘制的时候再显示。
        getApplication().processContent(contents, tmpFieldNames);
        displayIndexes = tmpModel.getFieldNameIndex(tmpViewInfo);
        headerTitles = getDisplayObject(tmpFieldNames);// 设置表头上显示的文字及图表。
        table.setHasEditor(tmpViewInfo.hasEditor());
        table.setCellEditable(tmpViewInfo.hasEditor());
        table.initHeaderSortStatus();
        table.setDataVector(contents, headerTitles);
        // 数据内容的更新完成--------

        // updateFontsAndColor(); //处理行高,字体的显示.
        // 字体颜色等格式信息更新完成------------------

        Vector tmpVec = table.getEditingRowRecords(); // 处理表格中的存放编辑后的值的Vector
        if (tmpVec != null)
            for (int i = 0; i < table.getColumnCount(); i++)
                tmpVec.add(i, null);
        // 处理表格中的存放编辑后的值的Vector完成---------------------

        // 开始设置每列宽度,编辑器和绘制器。
        IPIMTableColumnModel tmpColModel = table.getColumnModel();
        PIMTableColumn tmpCol = null;
        displayWidths = tmpModel.getFieldWidths(tmpViewInfo);
        for (int i = 0, len = displayWidths.length; i < len; i++) {
            tmpCol = tmpColModel.getColumn(i);

            tmpCol.setWidth(displayWidths[i]);
            tmpCol.setPreferredWidth(displayWidths[i]);
            // 设置了每列的列宽。
            FieldDescription tmpDescription = getApplication().getFieldDescription(tmpFieldNames[i], true);
            if (tmpDescription != null) {
                if (tmpDescription.isColumResizeable()) { // ??为什么有特殊绘制器的列不再允许改变尺寸呢?
                    int tmpWidth = tmpCol.getPreferredWidth();
                    tmpCol.setMaxWidth(tmpWidth);
                    tmpCol.setMinWidth(tmpWidth);
                }
                tmpCol.setCellEditor(tmpDescription.getCellEditor());
                tmpCol.setCellRenderer(tmpDescription.getCellRendor());
            }// 设置了每列的编辑器和绘制器。
        }
        tmpColModel.getColumn(0).setMinWidth(0);
        tmpColModel.getColumn(0).setMaxWidth(0);// 列宽设置完成

        if (table.getTableHeader().getCellEditor() != null)// 这种情况是表示视图切换时,上一表格视图是带快速编辑栏的不带快速编辑栏,但表格头却有编辑器
            table.getTableHeader().removeEditor(); // 而且新旧视图信息不同.实际的处理应是只要表格的快速编辑栏中有编辑器，就必须移除
        processTableSelectionStatus(tmpOldSeleRow, tmpOldSeleCol, tmpOldSeleRowCount, tmpOldRowCount, tmpOldColCount,
                tmpOldHeaderEditCol);
        // 字段宽度、特殊列、快速编辑栏、选中相关的设置完成（恢复到数据更新前的状态）----------------------------

        oldViewInfo = (PIMViewInfo) tmpViewInfo.clone(); // oldViewInfo专用于处理选中的方法.@TODO:有必要克隆吗?

        Component columnHeader = scrollPane.getColumnHeader();// 得到表格个列头（？）如果不为空的话，则给表格头和列头设置颜色。
        if (columnHeader != null) {
            JViewport viewport = (JViewport) columnHeader;
            Component tableHeader = viewport.getView();
            if (tableHeader != null) {
                tableHeader.setBackground(java.awt.Color.white);
                columnHeader.setBackground(java.awt.Color.white);
            }
        }

        if (CustOpts.custOps.isPreviewShown()) {
            double tmpScale = CustOpts.custOps.getSplitHeightScale();
            dividerLocation = (int) ((1 - tmpScale) * CustOpts.custOps.getFrameHeight());
            dividerLocation = dividerLocation < 0 ? 0 : dividerLocation;
            // 由于认为分割条的位置在应用切换时候,跳来跳去不好看,所以配置文件中存放了程序中所有应用的共同的分割条位置.

            if (prmAppChanged) { // 检查预览面板上的内容是否已准备好了，没有的话先将其初始化好。
                if (dialog != null) {
                    remove(dialog.getContainer());
                    dialog.release();
                    dialog = null;
                }
            }

            oldSelectedRow = table.getSelectedRow();
            if (oldSelectedRow == -1)
                getDlg().setContents(null);
            else
                getDlg().setContents(
                        CASControl.ctrl.getModel().selectRecord(tmpViewInfo.getAppIndex(),
                                Integer.parseInt(table.getValueAt(oldSelectedRow, 0).toString()),
                                tmpViewInfo.getPathID()));
        }

        if (dealType == 1) {// 处理表格头
            table.getTableHeader().requestFocus();
            if (columnAgain < 0)
                columnAgain = PIMViewUtil.getFirstTextableColumn(table.getTableHeader());
            // table.getTableHeader().editCellAt(1, columnAgain);
            // 2003.11.14 这样处理好象好一些
            table.getTableHeader().initDefaultEditor(1, columnAgain);
            table.getTableHeader().getEditorComponent().requestFocus();

            Component tmpComp = (JComponent) table.getTableHeader().getEditorComponent();
            tmpComp.invalidate();
            table.getTableHeader().invalidate();
            table.getTableHeader().repaint();
            tmpComp.requestFocus();

            dealType = 0;
        }

        table.revalidate();
        table.validate();
        table.invalidate();
        if (table.hasEditor() && table.getSelectedRow() >= 0)
            table.requestFocus();
        table.repaint();
    }

    /** 返回object数组，表示视图上应该显示的东西，包括图片 */
    private Object[] getDisplayObject(
            String[] fieldNames) {
        int length = fieldNames.length;
        Object[] displayObj = new Object[length];
        for (int i = 0; i < length; i++) {
            Object display = null;
            // TODO : 判断是否是显示图片，附件、图标、标记状态是图标，暂时用固定字符串代替。
            if (fieldNames[i].equalsIgnoreCase(IntlModelConstants.ATTACHMENT))
                display = CustOpts.custOps.getAttachFieldIcon(false);
            else if (fieldNames[i].equalsIgnoreCase(IntlModelConstants.FLAGSTATUS))
                display = CustOpts.custOps.getMarkStateFieldIcon(0);
            else if (fieldNames[i].equalsIgnoreCase(IntlModelConstants.ICON))
                display = CustOpts.custOps.getTypeFieldIcon();
            else if (fieldNames[i].equalsIgnoreCase(IntlModelConstants.IMPORTANCE))
                display = CustOpts.custOps.getImportantFieldIcon();
            else if (fieldNames[i].equalsIgnoreCase(IntlModelConstants.TASK_FLDS[ModelDBCons.FINISH_FLAG].toString()))
                display = CustOpts.custOps.getCompleteFieldIcon();
            else
                display = fieldNames[i];
            if (display != null)
                displayObj[i] = display;
        }
        return displayObj;
    }

    /*
     * 用于任务中的几个字段的关联 @param prmValueHash 记录的哈希表
     */
    private void processRelationFields(
            Hashtable prmValueHash) {
        final String tmpCompleted = "100";
        // 处理与任务的完成,状态,完成率,三者的联动,关键就是完成率
        // 前提,我们完成这个字段是永远在的,
        // TODO: 第一判断是完成字段,其次是完成率字段
        Object tmpValue = prmValueHash.get(PIMPool.pool.getKey(ModelDBCons.FINISH_FLAG));
        // 已完成,全部是完成
        if (tmpValue != null && tmpValue.toString().equals("1")) {
            prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.STATUS), PIMPool.pool.getKey(2));
            prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.COMPLETED), tmpCompleted);
        } else {
            // 在输入时未对完成字段进行处理,但对状态设置为已完成
            tmpValue = prmValueHash.get(PIMPool.pool.getKey(ModelDBCons.STATUS));
            if (tmpValue != null && tmpValue.toString().equals("2")) {
                prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.FINISH_FLAG), new Short((short) 1));
                prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.COMPLETED), tmpCompleted);
            } else { // 处理完成率字段
                // 看看有没有设置值,没有的活就设0
                tmpValue = prmValueHash.get(PIMPool.pool.getKey(ModelDBCons.COMPLETED));
                if (tmpValue != null && tmpValue.toString().length() > 0) {
                    if (tmpValue.toString().trim().equals(tmpCompleted)) {// 要设置其他两个已完成
                        prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.FINISH_FLAG), new Short((short) 1));
                        prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.STATUS), PIMPool.pool.getKey(2));
                    } else {
                        prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.FINISH_FLAG), new Short((short) 0));
                        prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.STATUS), PIMPool.pool.getKey(1));// 设置状态为进行中
                    }
                } else {
                    prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.FINISH_FLAG), new Short((short) 0));
                    prmValueHash.put(PIMPool.pool.getKey(ModelDBCons.COMPLETED), "0");
                }
            }
        }
    }

    /**
     * 处理表格的选中问题
     * 
     * @param prmSeleRow
     *            原有选中行
     * @param selectedCol
     *            原有选中列
     * @param selectedRowCount
     *            原有选中行数
     * @param rowCount
     *            原有行数
     * @param columnCount
     *            原有列数
     * @param tableHeaderEditColumn
     *            表格的原有选中列
     */
    private void processTableSelectionStatus(
            int prmSeleRow,
            int selectedCol,
            int selectedRowCount,
            int rowCount,
            int columnCount,
            int tableHeaderEditColumn) {
        // TODO:以后可能会涉及到视图类型(图标,卡片等)的处理
        // 这里是第一次进入视图时所进入的
        if (oldViewInfo == null) {
            if (table.getRowCount() > 0) {
                // 把焦点放到第一个单元所在的列的索引.0行
                table.getSelectionModel().setSelectionInterval(0, 0);
                table.getColumnModel().getSelectionModel().setSelectionInterval(1, 1);
            }
        }
        // *************************************************************************
        // 上面一部分应该没有问题
        // 这种情况是处理同一视图主类型的表格,可能原来就有选中状态,可能经过排序,
        // 列宽调整,增添新行,表格体中数据编辑,删除行和删除多行,
        // prmSeleRow,selectedCol是在本表格尚未作任何操作前保存下的表格的选中行和列
        // oldViewInfo 之前是表示切换前表格中已经有过表格体的选中操作,(唐建所作,具体我也不清楚)
        // oldViewInfo 开始的判断部分表示是因排序,宽度尺寸变化,字段增减而切换过来的视图,
        // 主类型和子类型相同
        else if (table.getSelectedRow() == -1 && prmSeleRow != -1 && prmSeleRow < table.getRowCount()
                && (oldViewInfo != null && oldViewInfo.getAppIndex() == currentViewInfo.getAppIndex())) {
            int tmpSelectedRow = prmSeleRow;
            String oldSortCritia = oldViewInfo.getSortCritia();
            String newSortCritia = currentViewInfo.getSortCritia();

            // 排序得用它,因为可能要进行表格体的滚动才能显示到选中的列
            boolean tmpChangSelection = false;
            /*
             * table.getSelectedRow() + " " + prmSeleRow + " rowCount"+ rowCount + " table.getRowCount()
             * " + table.getModel().getRowCount() + " contents.size()" + contents.size() +" tableContents"+
             * tableContents.length +"LazySelectedRow()"+table.getLazySelectedRow()); //
             */
            /*
             * 处理要点: 1.如列数变化,要去找当前行的第一个文本输入单元的所在列 2.如排序变化,要去找索引相匹配的行 3.如行数增加,要去找索引相匹配的行 4.如行数减少,先本行,再上移,没有就清空选中,进快速编辑栏
             */

            // 下面要去得到第一个可进行字符操作的编辑器单元所在的列的索引
            int tmpColumn = selectedCol;
            int scrollCol = selectedCol;
            // 列的处理,表示如没有选中,取第一个textField 的所在列
            if (selectedCol <= 0) {
                tmpColumn = PIMViewUtil.getFirstTextableColumn(table.getTableHeader());
                scrollCol = tmpColumn;
            }

            if (columnCount != table.getColumnCount()
                    || !oldViewInfo.getFieldNames().equals(currentViewInfo.getFieldNames())) { // 先处理列
                tmpColumn = PIMViewUtil.getFirstTextableColumn(table.getTableHeader());
                scrollCol = tmpColumn;
            } else if (rowCount > table.getRowCount()) { // 处理行数变化
                // 列不处理
                // 行一般也不用处理,但有一点,删除到最后一行,表格的选择模型自动跳
                // 到第一行,友好的行为应该是上移一行
            } else if (rowCount < table.getRowCount()) {// 这里行增加了
                // 得到第一个可进行字符操作的编辑器单元所在的列的索引
                // 只要处理列的锁定单元格就可以了
                tmpColumn = PIMViewUtil.getFirstTextableColumn(table.getTableHeader());
                scrollCol = tmpColumn;
            }
            // 下面的IF是来判断是否是排序变化了
            // 仅在同一视图经过排序操作才需要根据ID来确定选中的行和列
            // 判断条件如下:原视图信息的引用不为空,新旧视图信息的主类型相同,子应用类型相同,
            // 排序信息不同或旧视图信息的排序信息为空且新视图信息的排序信息不为空
            // 或者新视图信息的排序信息为空且旧视图信息的排序信息不为空
            else if (oldViewInfo.getAppIndex() == currentViewInfo.getAppIndex()
                    && ((oldSortCritia == null && newSortCritia != null)
                            || (oldSortCritia != null && newSortCritia != null && !oldSortCritia.equals(newSortCritia)) || (newSortCritia == null && oldSortCritia != null))
                    && table.getSelectedRecordID() >= 0 && selectedRowCount == 1) {
                // TODO:对选中的ID进行处理;转化成行号 ; 可能有删除的情况
                tmpSelectedRow = parseRowOfID(table.getSelectedRecordID());
                if (tmpSelectedRow < 0) {
                    ErrorUtil.write("find no Row ID match in method:parseRowOfID, class PIMTableVew");
                    tmpSelectedRow = 0;
                }
                scrollCol = table.getTableHeader().getOperatingColumn();
                tmpChangSelection = true;
            }
            // 行一般也不用处理,但有一点,删除到最后一行,表格的选择模型自动跳
            // 到第一行,友好的行为应该是上移一行
            // 要考虑列宽调整或是表格内容变化
            else if (rowCount == table.getRowCount() && prmSeleRow == 0
                    && oldViewInfo.getFieldWidths().equals(currentViewInfo.getFieldWidths())
                    && table.getLazySelectedRow() != prmSeleRow) {
                tmpSelectedRow = table.getRowCount() - 1;
                // tmpChangSelection = true;
            }
            table.setRowSelectionInterval(tmpSelectedRow, tmpSelectedRow);
            // 2003.10.8 在外部Action调用后,锁定列会是1,比如删除,要确保一下
            if (tmpColumn == 1)
                tmpColumn = PIMViewUtil.getFirstTextableColumn(table.getTableHeader());
            table.getColumnModel().getSelectionModel().setSelectionInterval(tmpColumn, tmpColumn);
            // 这里表示要进行一下可能的滚动
            /*
             * if(tmpChangSelection) { table.changeSelection(tmpSelectedRow,scrollCol,false,false,false); } //
             */
            // 应注掉,不进入编辑状态2003.11.27
            // table.editCellAt(tmpSelectedRow,tmpColumn);
            table.requestFocus();
            // 2003.10.31 解一个非可编辑表格在删除后会失去焦点的bug.
            if (table.getSelectedRow() >= 0) {
                // updatePIMUI();
                // SwingUtilities.invokeLater(this);
            }
            table.repaint();

            // 下面几句只好先注掉,因为表格的滚动行为会使所需显示的单元格到视口左
            // 边,不合我们要求,只好暂时注掉,先有了最佳解时再来处理.
            //
            if (tmpChangSelection && prmSeleRow != -1) {
                table.setIsAddDelta(true);
                table.scrollToRect(tmpSelectedRow, scrollCol);
                table.setIsAddDelta(true);
            }
        }
        // *************************************************************************
        // 这种情况是表示表格原来就快速编辑栏视图的切换
        // 目的是要把焦点放到快速编辑栏中去
        // 判断条件:带快速编辑栏,是切换的,且表格的主类型或者子类型不相同
        else if ((oldViewInfo.getAppIndex() != currentViewInfo.getAppIndex()) && table.hasEditor()) {
            int anchorRow = 1;
            // 下面要去得到第一个可进行字符操作的编辑器单元所在的列的索引
            int anchorColumn = PIMViewUtil.getFirstTextableColumn(table.getTableHeader());
            if (anchorColumn == -1)
                anchorColumn = 1;
            // 以下两句必须有,在联系人表格右滚动至后部时,切入任务,表格头快速编辑栏显示不对

            table.changeSelection(0, 0, false, false, false);
            table.clearSelection();

            table.getTableHeader().setEditingColumn(anchorColumn);
            table.getTableHeader().setSelectedColumnIndex(anchorColumn);
            table.getTableHeader().initDefaultEditor(anchorRow, anchorColumn);

            table.getTableHeader().getEditorComponent().requestFocus();
            Vector tmpNewRecord = ((DefaultPIMTableColumnModel) table.getColumnModel()).getNewRecord();
            tmpNewRecord.setElementAt(null, anchorColumn);

            IPIMCellEditor cellEditor = table.getTableHeader().getCellEditor();
            cellEditor.getTableCellEditorComponent(table, null, true, anchorRow, anchorColumn);
            // table.getTableHeader().repaint();
        }
        // *************************************************************************
        // 这种情况是表示表格原来就选中快速编辑栏的,
        // 目的是要把焦点放到快速编辑栏中去,而且选中单元格不变
        // 判断条件:带快速编辑栏,而且选中行小于零
        else if ((table.hasEditor() && prmSeleRow < 0)) {
            // 下面要去得到第一个可进行字符操作的编辑器单元所在的列的索引
            int anchorColumn = tableHeaderEditColumn;
            columnAgain = anchorColumn;
            if (rowCount != table.getRowCount()) {
                // 下面要去得到第一个可进行字符操作的编辑器单元所在的列的索引
                anchorColumn = PIMViewUtil.getFirstTextableColumn(table.getTableHeader());
                columnAgain = anchorColumn;
                // 快速编辑栏编辑至最后,不调用以下两句就无法在按下回车后滚动到头部
                // 理想的做法是要得到表格在视图中显示的第一行,到这一行的行头去最
                // 好
                table.changeSelection(0, 0, false, false, true);
                table.clearSelection();

                dealType = 1;
                // updatePIMUI();
                // SwingUtilities.invokeLater(this);
                return;
            }

            Vector tmpNewRecord = ((DefaultPIMTableColumnModel) table.getColumnModel()).getNewRecord();
            if (anchorColumn != -1)
                tmpNewRecord.setElementAt(null, anchorColumn);

            dealType = 1;
        }

        // *************************************************************************
        // 这种情况表示是切换到没有快速编辑栏的表格视图,默认定焦点到第一行第一列
        else {
            if (table.getRowCount() > 0) {
                table.getSelectionModel().setSelectionInterval(0, 0);
                table.getColumnModel().getSelectionModel().setSelectionInterval(1, 1);
            } else
                table.clearSelection();
        } // end else (last)
    }

    /**
     * 处理选中行
     * 
     * @param id
     *            原来选中的ID
     * @return 表格应选中的行
     */
    private int parseRowOfID(
            int id) {
        int realRow = -1;
        int tmpRow = 0;
        try {
            for (int i = 0; i < table.getRowCount(); i++) {
                tmpRow = Integer.parseInt(table.getValueAt(i, 0).toString());
                if (tmpRow == id) {
                    realRow = i;
                    break;
                }
            }
        } catch (Exception e) {
            ErrorUtil.write(e);
        }
        return realRow;
    }

    /**
     * 用于确定视图修改后的值是否和记录原值相等,不等才有必要入数据库
     * 
     * @param oldValue
     *            记录中的原有值
     * @param newValue
     *            视图中的修改的值
     * @return 是否要保存
     */
    private boolean shouldSave(
            Object oldValue,
            Object newValue) {
        if (oldValue == null && newValue != null)
            return true;
        else if (oldValue != null && newValue == null)
            return true;
        else if (oldValue.toString().equals(newValue)) // 相等才为真
            return false;
        else
            return true;
    }

    /**
     * 处理表格每一行的字体,前景色,行高
     * 
     * @param row
     *            行号
     * @param key
     *            从本类属性池中取字体和颜色的键值,暂为一个字符串
     */
    private void processASingleRow(
            int row,
            String key) {
        Object rowFont = lazyFontAndColorPool.get(key);
        Object rowColor = lazyFontAndColorPool.get(key.concat("Color"));
        Object rowHaveStrikethrough = lazyFontAndColorPool.get(key.concat("HaveStrikethrough"));
        // 对表格的每一行进行设置
        table.setFontAttribute(Integer.toString(row), rowFont);
        table.setFontAttribute(Integer.toString(row).concat("Color"), rowColor);
        table.setFontAttribute(Integer.toString(row).concat("HaveStrikethrough"), rowHaveStrikethrough);
        FontMetrics fm = getFontMetrics((Font) rowFont);
        int rowHeight = fm.getHeight();
        table.setRowHeight(row, rowHeight + 2);
    }

    public ICASDialog getDlg() {
        if (dialog == null) {
            dialog = MainPane.getApp("Foregrounding").getADialog();
            dialog.getContainer().setBackground(Color.lightGray);
            add(dialog.getContainer());
        }
        return dialog;
    }

    /**
     * 返回表格实例
     * 
     * @return PIMTable
     */
    public PIMTable getTable() {
        return table;
    }

    /**
     * 得到表格头显示对象
     * 
     * @return 表格头显示对象
     */
    public Object[] getDisplayObject() {
        return headerTitles;
    }

    /**
     * called by : PIMTable 和 PIMTableHeader 等
     * 
     * @return 在数据库中的索引
     */
    public int[] getDisplayIndexes() {
        return displayIndexes;
    }

    /**
     * 得到显示宽度
     * 
     * @return 每列显示宽度
     */
    public int[] getDisplayWidths() {
        return displayWidths;
    }

    private ICASDialog dialog;// 该对话盒的getContainer()返回的面板将显示于预览区域。

    protected CommonTitle titlePane;// 标题面板

    protected CommonTitle titlePane2;// 标题面板2

    // protected JSplitPane splitPane;//分隔面板

    private int dividerLocation;// 保留分隔条位置

    protected static PIMViewInfo oldViewInfo;// 因表格视图切换后的种种情况,保存一个原视图信息的引用是有用的,比如用来判断tableView前后是否服务于同一个应用.

    private int oldSelectedRow = -1;// 表格原有选中行,用于处理回执

    private PIMTable table;// 主体表格

    // private PIMTable table2;//主体表格

    private PIMScrollPane scrollPane;// 表格配套的滚动面板

    // private PIMScrollPane scrollPane2;

    private Object[][] contents;// 从数据库中得到的记录的所有数据

    private int[] displayIndexes;// 要显示的数据的索引

    private int[] displayWidths;// 每列的宽度

    private Object[] headerTitles;// 表格头要显示的值

    private int dealType;// 和RUN方法有关,在表格头处理

    private int columnAgain;// 这个以后要去掉

    // 本池保存当前表格视图所用到的所有字体和前景颜色,然后再分配到每一行去, 以减少所生成的对象数量
    private Hashtable lazyFontAndColorPool = new Hashtable();
}
