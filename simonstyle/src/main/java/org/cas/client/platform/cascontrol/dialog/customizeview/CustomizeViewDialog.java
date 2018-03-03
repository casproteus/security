package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableModelVecBased;
import org.cas.client.resource.international.CustViewConsts;

/**
 * 这是的自定义视图所弹出的对话框（主）▼
 */

public class CustomizeViewDialog extends JDialog implements ICASDialog, ActionListener, ListSelectionListener, Runnable {
    // 保存本对话盒的宽度和高度
    /**
     * 保存本对话盒的宽度
     */
    public static final int WIDTH = 500;
    // 如果要加上Group by, 高度就选取450;
    /**
     * 保存本对话盒的高度
     */
    public static final int HEIGHT = 410;
    // 长按钮的宽度
    /**
     * 以后去掉
     */
    public static final int BUTTON_WIDTH = 100;
    // 间隔
    /**
     * 以后去掉
     */
    public static final int SPAN = 60;

    /**
     * 创建一个 CustomizeViewDialog 的实例
     * 
     * @param parent
     *            父窗体
     */
    public CustomizeViewDialog(Frame parent) {
        super(parent, true);
        this.parentFrame = parent; // 保存父窗体的引用
        doSetTitle(); // 设置标题
        initComponent(); // 组件初始化并布局
        addAllListeners();
        setSize(getDialogSize()); // 设置本对话盒尺寸
        focusTransferSequence();
        setBounds((CustOpts.SCRWIDTH - getDialogSize().width) / 2, (CustOpts.SCRHEIGHT - getDialogSize().height) / 2,
                getDialogSize().width, getDialogSize().height); // 对话框的默认尺寸。

    }

    public void reLayout() {
    };

    /** 临时方法,开禁后作废 */
    private void disableSth() {
        if (currentViewInfo.getViewType() == ModelCons.TEXT_VIEW) { // 文本编辑面板不可调整字段,排序,过滤,字体,暂时也不可恢复默认值
            fieldsBTN.setEnabled(false);
            sortBTN.setEnabled(false);
            filterBTN.setEnabled(false);
            setFormatterBTN.setEnabled(false);
            restoreDefaultBTN.setEnabled(false);
        } else if (currentViewInfo.getViewType() == ModelCons.DAYWEEKMONTH_VIEW) { // 天/周/月视图不可调整字段,排序,过滤,字体,暂时也不可恢复默认值
            fieldsBTN.setEnabled(false);
            sortBTN.setEnabled(false);
            filterBTN.setEnabled(false);
            setFormatterBTN.setEnabled(false);
            restoreDefaultBTN.setEnabled(false);
        } else if (currentViewInfo.getViewType() == ModelCons.CARD_VIEW) // 卡片视图暂时不可调整字体,
            setFormatterBTN.setEnabled(false);
        else {
            fieldsBTN.setEnabled(true);
            sortBTN.setEnabled(true);
            filterBTN.setEnabled(true);
            setFormatterBTN.setEnabled(true);
        }
    }

    /**
     * 取得本对话盒的尺寸
     * 
     * @return 对话盒尺寸
     */
    public Dimension getDialogSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /** 设置对话盒的标题,因为本对话盒标题是动态的 */
    private void doSetTitle() {
        int tmpAppIdx = CustOpts.custOps.getActiveAppType();// 得到当前的应用类型所在的结点名
        String tmpCapStr = CustOpts.custOps.APPCapsVec.get(tmpAppIdx);

        String tmpTitle =
                new StringBuffer().append(CustViewConsts.DEFINE).append('\"').append(tmpCapStr).append('\"')
                        .append(CustViewConsts.VIEW).toString();// 格式化一下

        setTitle(tmpTitle);// 设置标题
    }

    /**
     * 初始化并布局; called by : 构建器
     */
    private void initComponent() {
        Dimension dlgSize = getDialogSize();

        // 设置OK按钮
        ok = new JButton(CustViewConsts.APPLY);
        ok.setBounds(dlgSize.width - ok.getPreferredSize().width * 2 - CustOpts.HOR_GAP,
                dlgSize.height - ok.getPreferredSize().height, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);
        // 一开始这项为禁止
        ok.setEnabled(false);

        // 设置Cancel按钮
        cancel = new JButton(CustViewConsts.CLOSE);
        cancel.setBounds(dlgSize.width - cancel.getPreferredSize().width, dlgSize.height
                - cancel.getPreferredSize().height, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);

        // TODO: width of button must by calculated dynamic

        // 设置新建按钮
        createNewBTN = new JButton(CustViewConsts.CREATE_BTN);
        createNewBTN.setMnemonic('N');
        // int X_Coordinate = dlgSize.width - createNewBTN.getPreferredSize().width - CustOpts.HOR_GAP;
        int X_Coordinate = dlgSize.width - BUTTON_WIDTH;
        int Y_Coordinate = 0;
        createNewBTN.setBounds(X_Coordinate, Y_Coordinate, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(createNewBTN);

        // 设置字段按钮
        Y_Coordinate = Y_Coordinate + createNewBTN.getPreferredSize().height + CustOpts.VER_GAP;
        fieldsBTN = new JButton(CustViewConsts.FIELDS_BTN);
        fieldsBTN.setMnemonic('F');
        fieldsBTN.setBounds(X_Coordinate, Y_Coordinate, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(fieldsBTN);

        // 设置排序按钮
        Y_Coordinate = Y_Coordinate + fieldsBTN.getPreferredSize().height + CustOpts.VER_GAP;
        sortBTN = new JButton(CustViewConsts.SORT_BTN);
        sortBTN.setMnemonic('S');
        sortBTN.setBounds(X_Coordinate, Y_Coordinate, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(sortBTN);

        // 设置过滤按钮
        Y_Coordinate = Y_Coordinate + sortBTN.getPreferredSize().height + CustOpts.VER_GAP;
        filterBTN = new JButton(CustViewConsts.FILTER_BTN);
        filterBTN.setMnemonic('L');
        filterBTN.setBounds(X_Coordinate, Y_Coordinate, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(filterBTN);

        // 设置格式按钮
        Y_Coordinate = Y_Coordinate + filterBTN.getPreferredSize().height + CustOpts.VER_GAP;
        setFormatterBTN = new JButton(CustViewConsts.SET_FORMATS_BTN);
        setFormatterBTN.setMnemonic('A');
        setFormatterBTN.setBounds(X_Coordinate, Y_Coordinate, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(setFormatterBTN);

        // 设置缺省按钮
        Y_Coordinate = Y_Coordinate + setFormatterBTN.getPreferredSize().height + CustOpts.VER_GAP;
        restoreDefaultBTN = new JButton(CustViewConsts.RESTORE_DEFAULTS_BTN);
        restoreDefaultBTN.setMnemonic('R');
        restoreDefaultBTN.setBounds(X_Coordinate, Y_Coordinate, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(restoreDefaultBTN);

        X_Coordinate = 0;
        // 设置描述面板
        Y_Coordinate = Y_Coordinate + restoreDefaultBTN.getPreferredSize().height + CustOpts.VER_GAP * 2;
        descriptionPanel = new JPanel();// CustomizeViewConstants.DESCRIPTION,);
        descriptionPanel.setBounds(X_Coordinate, Y_Coordinate, dlgSize.width,
                dlgSize.height - Y_Coordinate - cancel.getPreferredSize().height - CustOpts.VER_GAP);
        getContentPane().add(descriptionPanel);

        X_Coordinate += CustOpts.HOR_GAP;
        // 设置字段标签
        fieldLabel = new JLabel(CustViewConsts.FILEDS_LABEL);
        fieldLabel.setBounds(X_Coordinate, CustOpts.VER_GAP + CustOpts.LBL_HEIGHT, fieldLabel.getPreferredSize().width,
                CustOpts.LBL_HEIGHT);
        descriptionPanel.add(fieldLabel);

        // 设置排序标签
        sortLabel = new JLabel(CustViewConsts.SORT_LABEL);
        sortLabel.setBounds(X_Coordinate, CustOpts.VER_GAP * 3 + CustOpts.LBL_HEIGHT * 3,
                sortLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        descriptionPanel.add(sortLabel);

        // 设置过滤标签
        filterLabel = new JLabel(CustViewConsts.FILTER_LABEL);
        filterLabel.setBounds(X_Coordinate, CustOpts.VER_GAP * 5 + CustOpts.LBL_HEIGHT * 5,
                filterLabel.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        descriptionPanel.add(filterLabel);

        // 得到描述面板信息的宽度
        int descriptionPanelWidth = descriptionPanel.getSize().width - CustOpts.VER_GAP * 2 - SPAN;

        // 设置字段信息
        fieldInfo = new JTextArea();
        fieldInfo.setBounds(SPAN + CustOpts.HOR_GAP, 1 + CustOpts.VER_GAP + CustOpts.LBL_HEIGHT, descriptionPanelWidth,
                CustOpts.LBL_HEIGHT * 2 + CustOpts.VER_GAP);
        descriptionPanel.add(fieldInfo);

        // 设置排序信息
        sortInfo = new JTextArea();
        sortInfo.setBounds(SPAN + CustOpts.HOR_GAP, 2 + CustOpts.VER_GAP * 3 + CustOpts.LBL_HEIGHT * 3,
                descriptionPanelWidth, CustOpts.LBL_HEIGHT * 2 + CustOpts.VER_GAP);
        descriptionPanel.add(sortInfo);

        // 设置过滤信息
        filterInfo = new JTextArea();
        filterInfo.setBounds(SPAN + CustOpts.HOR_GAP, 3 + CustOpts.VER_GAP * 5 + CustOpts.LBL_HEIGHT * 5,
                descriptionPanelWidth, CustOpts.LBL_HEIGHT * 2 + CustOpts.VER_GAP);
        descriptionPanel.add(filterInfo);

        // 设置表格
        table = new PIMTable();
        table.setHasSorter(false);
        // 设置表格模型
        equipTableModel();

        // 设置滚动面板,把表格加入面板
        sp =
                new PIMScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(sp);
        Y_Coordinate -= CustOpts.VER_GAP * 2;
        sp.setBounds(0, 0, dlgSize.width - BUTTON_WIDTH - CustOpts.HOR_GAP, Y_Coordinate);

    }

    /**
     * 用来动态装配表格中的模型 called by : initComponent()
     */
    private void equipTableModel() {
        // 取当前视图的索引值；
        int tmpActiveApp = CustOpts.custOps.getActiveAppType();
        int tmpPathID = CustOpts.custOps.getActivePathID();
        // /取当前视图信息
        currentViewInfo = CASControl.ctrl.getViewInfo(tmpPathID);
        // 取表格列数
        int tmpColumnNum = CustViewConsts.TABLE_TITLES.length;
        // TODO：这部分一定要做到动态知道应用子类型的视图数目
        // 我现在是写死了,实在没办法
        // 取应用子类型的视图数
        int tmpSubViewCount = CustViewConsts.TABLE_SUBVIEW_COUNT[tmpActiveApp];
        // 用以保存表格数据
        Object[][] data = new Object[tmpSubViewCount][tmpColumnNum];

        // 按行来装配表格数据
        for (int i = 0; i < tmpSubViewCount; i++) {
            // 得到子视图信息
            PIMViewInfo tmpViewInfo = CASControl.ctrl.getViewInfo(CustOpts.custOps.getActivePathID());
            // 当前所见子类型的特殊处理
            if (i == 0) {
                // 装配第一,二列
                data[i][0] = CustViewConsts.CURRENT_VIEW_SETS;
                data[i][1] = CustViewConsts.CURRENT_VIEW_APPSCOPE;
                // 保存当前子类型的所在索引和表格中的当前选中行,以备用
                setCurrentTableLocationItem(i);
                setTableSelectionIndex(i);
            }
            // 正常装配
            else {
                // 装配第一,二列
                data[i][0] = tmpViewInfo.getViewName();
                data[i][1] = new StringBuffer().append(CustViewConsts.ALL).append('\"').
                // append(CustViewConsts.TABLE_TYPES[tmpActiveApp]).append('\"').append(CustViewConsts.FOLDER).
                        toString();
            }
            // 装配第三列
            data[i][2] = CustViewConsts.VIEW_TYPES[tmpViewInfo.getViewType()];
        } // end for

        // 造表格数据模型
        dataModel = new PIMTableModelVecBased(data, CustViewConsts.TABLE_TITLES);
        dataModel.setCellEditable(false);
        /*
         * / { //这样一来，选中的那行中的那格就不变白了。 public boolean isCellEditable(int row, int column) { return false; } }; //
         */
        // 设置表格模型
        table.setModel(dataModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.requestFocus();

        // 设置表格单行选取
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 表格类
        table.getSelectionModel().addListSelectionListener(this);

        // 这句的结果是从上到当前项都选中了,表格中的BUG,以后再说
        table.setDefaultSelected(0, 0);
        table.scrollToRect(0, 0);
        // 表格各列大致按 4,4,3 比例分配
        int[] widths = new int[3];
        int spWidth = getDialogSize().width - BUTTON_WIDTH - CustOpts.HOR_GAP;
        widths[0] = spWidth * 4 / 11;
        widths[1] = spWidth * 4 / 11;
        widths[2] = spWidth - widths[0] * 2 - CustOpts.LBL_HEIGHT;

        /*
         * //设置表格不可拖动,一个补丁办法 for(int i = 0 ; i < tmpColumnNum; i++) {
         * table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
         * table.getColumnModel().getColumn(i).setMaxWidth(widths[i]);
         * table.getColumnModel().getColumn(i).setMinWidth(widths[i]); //由于我在表格UI中没有改，这句一时没多少用，表格完善后一定用这句。
         * //table.getColumnModel().getColumn(i).setEditorEnable(false); } //
         */
    }

    /**
     * 得到表格的当前选中行 called by : equipDiscription() etc.
     * 
     * @return 当前选中行
     */
    public int getTableSelectionIndex() {
        return tableSelectionIndex;
    }

    /**
     * 设置表格的当前选中行 called by :initComponent()
     * 
     * @param prmTableSelectionIndex
     *            选中行
     */
    public void setTableSelectionIndex(
            int prmTableSelectionIndex) {
        tableSelectionIndex = prmTableSelectionIndex;
    }

    /**
     * 得到当前窗体上的应用子类型
     * 
     * @return 当前应用子类型
     */
    public int getCurrentTableLocationItem() {
        return currentTableIndex;
    }

    /**
     * 保存当前窗体上的应用子类型 called by :initComponent()
     * 
     * @param prmTableSelectionIndex
     *            当前应用子类型
     */
    public void setCurrentTableLocationItem(
            int prmTableSelectionIndex) {
        currentTableIndex = prmTableSelectionIndex;
    }

    /**
     * 用于更新描述面板信息 called by :initComponent() 和几个监听器动作 和排序对话盒
     */
    public void equipDiscription() {
        // *
        // 取当前应用类型和选中子类型索引
        // int tmpActiveAppType = CustomOptions.custOps.getActiveAppType();
        // int tmpSubAppType = getTableSelectionIndex();
        // 取视图信息
        PIMViewInfo tmpViewInfo = CASControl.ctrl.getViewInfo(currentViewInfo.getPathID());
        currentViewInfo = tmpViewInfo;
        // 调用本包工具类中的方法得到视图中的所用了的字段
        // ArrayList fields = ParseUtil.parseFields(tmpViewInfo);
        // */
        // 设置字段信息
        String[] fieldsNames = CASControl.ctrl.getModel().getFieldNames(currentViewInfo);
        // 用 ArrayList 来格式化一下
        ArrayList fields = new ArrayList(fieldsNames.length);
        for (int i = 1, count = fieldsNames.length; i < count; i++) {
            fields.add(fieldsNames[i]);
        }
        fieldInfo.setText(fields.toString());
        // 调用本包工具类中的方法得到视图中的排序信息,一个字段加一个升降序标志
        Object[] sortors = ParseUtil.parseSortCritia(currentViewInfo);
        try {
            for (int i = 0; i < sortors.length; i += 2) {
                // 用来处理升降序标志,转为字符信息
                int tmpSortType = Integer.parseInt(sortors[i + 1].toString());
                sortors[i + 1] = CustViewConsts.SORTOR_TABLE[tmpSortType];
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        // 用 ArrayList 来格式化一下
        ArrayList sort = new ArrayList();
        for (int i = 0; i < sortors.length; i++) {
            sort.add(sortors[i]);
        }

        if (sort.size() == 0) {
            // 没有排序信息显示"无"
            sortInfo.setText(CustViewConsts.NULL);
        } else {
            // 设置排序信息
            sortInfo.setText(sort.toString());
        }
        // 目前我对此项一无所知
        filterInfo.setText(CustViewConsts.NULL);
    }

    /**
     * 实现接口中的方法
     */
    public void release() {
    }

    /**
     * 实现接口中的方法,本对话盒返回空
     * 
     * @return NULL
     */
    public PIMRecord getContents() {
        return null;
    }

    public boolean setContents(
            PIMRecord prmRecord) {
        return false;
    }

    public void makeBestUseOfTime() {
    }

    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    public PIMTextPane getTextPane() {
        return null;
    }

    /**
     * 给组件加上监听器 called by :构建器
     */
    public void addAllListeners() {
        // 按钮类
        createNewBTN.addActionListener(this);
        fieldsBTN.addActionListener(this);
        sortBTN.addActionListener(this);
        filterBTN.addActionListener(this);
        setFormatterBTN.addActionListener(this);
        restoreDefaultBTN.addActionListener(this);

        ok.addActionListener(this);
        cancel.addActionListener(this);
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        // 按下新建按钮
        if (e.getSource() == createNewBTN) {
            createNewClicked();
        }
        // 按下字段按钮
        else if (e.getSource() == fieldsBTN) {
            fieldClicked();
        }
        // 按下排序按钮
        else if (e.getSource() == sortBTN) {
            sortClicked();
        }
        // 按下过滤按钮
        else if (e.getSource() == filterBTN) {
            filterClicked();
        }
        // 按下设置格式按钮
        else if (e.getSource() == setFormatterBTN) {
            setFormatClicked();
        }
        // 按下恢复默认值按钮
        else if (e.getSource() == restoreDefaultBTN) {
            restoreDefaultClicked();
        }
        // 按下确定按钮
        else if (e.getSource() == ok) {
            okClicked(e);
        }
        // 按下取消按钮
        else if (e.getSource() == cancel) {
            cancelClicked(e);
        }
    }

    /**
     * 按下cancel Called by ActionPerformed
     */
    private void cancelClicked(
            ActionEvent e) {
        if (fieldsDialog != null) {
            fieldsDialog.actionPerformed(new ActionEvent(fieldsDialog.cancel, 0, null));
        }
        if (sortDialog != null) {
            sortDialog.actionPerformed(new ActionEvent(sortDialog.cancel, 0, null));
        }
    }

    /**
     * 按下OK Called by ActionPerformed
     */
    private void okClicked(
            ActionEvent e) {
        int tmpActiveAppType = CustOpts.custOps.getActiveAppType();
        // 切换当前应用类型的子类型
        CASControl.ctrl.changeApplication(tmpActiveAppType, CustOpts.custOps.getActivePathID());
        cancelClicked(e);
        actionPerformed(new ActionEvent(cancel, 0, null));
    }

    /**
     * 按下新建 Called by ActionPerformed
     */
    private void createNewClicked() {
        // 弹出新建视图对话盒
        new CreateNewDialog(this, currentViewInfo).show();
    }

    /**
     * 在新建视图成功后调用
     */
    public void updateTableContents() {

    }

    /**
     * 按下字段按钮 Called by ActionPerformed
     */
    private void fieldClicked() {
        new FieldsDialog(this, currentViewInfo).show();
    }

    /**
     * 更新所有字段
     */
    public void updateFields() {
        // 下面要对排序处理
        int tmpAppType = currentViewInfo.getAppIndex();
        // 得到新的视图信息
        currentViewInfo = CASControl.ctrl.getViewInfo(CustOpts.custOps.getActivePathID());
        // 从中得到字段名数据组
        String[] modifiedFields = CASControl.ctrl.getModel().getFieldNames(currentViewInfo);
        // 调用本包工具类中的方法得到视图中的排序信息,一个字段加一个升降序标志
        Object[] sortors = ParseUtil.parseSortCritia(currentViewInfo);
        ArrayList fieldlst = new ArrayList(modifiedFields.length);
        if (sortors != null && sortors.length != 0) {
            PIMViewInfo tmpViewInfo = null;
            for (int i = 0; i < modifiedFields.length; i++) {
                // 放入一个LIST以便于工作
                fieldlst.add(modifiedFields[i]);
            }
            // 表示目前排序信息中有几个排序信息
            int count = sortors.length / 2;
            // 表示目前显示字段中有几个排序信息
            int sortCritiaInFieldsCount = 0;
            for (int i = 0; i < sortors.length / 2; i += 2) {
                if (fieldlst.contains(sortors[i].toString())) {
                    //
                    sortCritiaInFieldsCount++;
                }
            }
            if (sortCritiaInFieldsCount < count) {
                tmpViewInfo = (PIMViewInfo) currentViewInfo.clone();
                tmpViewInfo.setSortCritia(null);
                // 下面一句放入数据库
                CASControl.ctrl.getModel().updateViewInfo(tmpViewInfo);
            }

        }
        // TODO:今后要对过滤处理
        equipDiscription();
    }

    /**
     * 按下排序 Called by ActionPerformed
     */
    private void sortClicked() {
        new SortDialog(this, currentViewInfo).show();
    }

    /**
     * 按下筛选 Called by ActionPerformed
     */
    private void filterClicked() {
        // 弹出过滤对话盒
        new FilterDialog(this, currentViewInfo).show();
    }

    /**
     * 按下设置格式 Called by ActionPerformed
     */
    private void setFormatClicked() {
        // 弹出设置格式对话盒
        new SetFormatDialog(this, currentViewInfo).show();
    }

    /**
     * 按下恢复默认值 Called by ActionPerformed
     */
    private void restoreDefaultClicked() {
        int tmpActiveAppType = CustOpts.custOps.getActiveAppType();
        // 取选中的子类型
        int tmpAppSubType = getTableSelectionIndex();

        // TODO: 得从Model中取一个方法
        CASControl.ctrl.getModel().resetViewInfo(tmpActiveAppType, tmpAppSubType, CustOpts.custOps.getActivePathID());
        equipDiscription();
    }

    /**
     * Invoked when this dialog closing. This method should be overrided if user has extra execution while closing the
     * dialog.
     */
    protected void extraAction() {
        ok.removeActionListener(this);
        cancel.removeActionListener(this);
        createNewBTN.removeActionListener(this);
        fieldsBTN.removeActionListener(this);
        sortBTN.removeActionListener(this);
        filterBTN.removeActionListener(this);
        setFormatterBTN.removeActionListener(this);
        restoreDefaultBTN.removeActionListener(this);
        table.getSelectionModel().removeListSelectionListener(this);
        descriptionPanel.removeAll();
        getContentPane().removeAll();
        sp.removeAll();
        table.removeAll();
        if (fieldsDialog != null) {
            fieldsDialog.release();
        }
        if (sortDialog != null) {
            sortDialog.release();
        }
    }

    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    public void valueChanged(
            ListSelectionEvent e) {
        // 得到当前应用主类型
        int tmpActiveAppType = CustOpts.custOps.getActiveAppType();

        // 得到表格中的选中行
        setTableSelectionIndex(table.getSelectedRow());

        // 重置视图信息
        currentViewInfo = CASControl.ctrl.getViewInfo(CustOpts.custOps.getActivePathID());
        // 更新描述面板
        equipDiscription();

        // 在表格选中项变化时,确定可用
        // 恢复默认值在非当前视图不可用
        if (table.getSelectedRow() == getCurrentTableLocationItem()) {
            ok.setEnabled(false);
            restoreDefaultBTN.setEnabled(true);
        }
        // 否则还是禁止
        else {
            ok.setEnabled(true);
            restoreDefaultBTN.setEnabled(false);
        }
        // 有一些视图不可以进行按钮的操作,先禁止
        disableSth();
    }

    public Container getContainer() {
        return getContentPane();
    }

    /**
     * 控制信息转移顺序 由构建器调用
     */
    private void focusTransferSequence() {
        // 设置Tab建焦点顺序。
        // 前我们的表格似乎不会交出焦点
        // table.setNextFocusableComponent(createNewBTN);
        createNewBTN.setNextFocusableComponent(fieldsBTN);
        fieldsBTN.setNextFocusableComponent(sortBTN);
        sortBTN.setNextFocusableComponent(filterBTN);
        filterBTN.setNextFocusableComponent(setFormatterBTN);
        setFormatterBTN.setNextFocusableComponent(restoreDefaultBTN);
        restoreDefaultBTN.setNextFocusableComponent(ok);
        ok.setNextFocusableComponent(cancel);
        cancel.setNextFocusableComponent(createNewBTN);

        // 最后执行
        SwingUtilities.invokeLater(this);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        // 设置焦点默认落点
        table.grabFocus();
        table.scrollToRect(getCurrentTableLocationItem(), 0);
    }

    // 以下为本类的变量声明
    // 保存对父窗体的引用
    private Frame parentFrame;

    // 几个按钮,在右上方
    private JButton ok;
    private JButton cancel;
    private JButton createNewBTN;
    private JButton fieldsBTN;
    private JButton sortBTN;
    private JButton filterBTN;
    private JButton setFormatterBTN;
    private JButton restoreDefaultBTN;

    // 描述面板
    private JPanel descriptionPanel;
    // 描述面板上的几个标签
    private JLabel fieldLabel;
    private JLabel sortLabel;
    private JLabel filterLabel;

    // 保存表格中选中项的视图信息引用
    private PIMViewInfo currentViewInfo;
    // 用于描述面板上显示的字段,排序,过滤信息
    private JTextArea fieldInfo;
    private JTextArea sortInfo;
    private JTextArea filterInfo;

    // 保存表格及其滚动面板
    private PIMScrollPane sp;
    private PIMTable table;
    // 保存表格模型
    private PIMTableModelVecBased dataModel;
    // 保存表格中的选中项的应用子类型索引
    private int tableSelectionIndex;
    // 保存当前窗体中的应用子类型索引
    private int currentTableIndex;

    // 字段对话盒
    private FieldsDialog fieldsDialog;
    // 排序对话盒
    private SortDialog sortDialog;
}
