package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.quickinputfield.QuickInfoField;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.TreeDialog;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.contact.dialog.selectcontacts.SelectedNewMemberDlg;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.FilterInfo;
import org.cas.client.platform.pimview.View_PIMDetails;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableModelAryBased;
import org.cas.client.resource.international.CustViewConsts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.IntlModelConstants;
import org.cas.client.resource.international.PaneConsts;

public class FilterDialog extends JDialog implements ICASDialog, ActionListener, ItemListener, ListSelectionListener,
        DocumentListener, Runnable, ComponentListener {

    /**
     * 创建一个 FilterDialog 的实例
     * 
     * @param prmDialog
     *            父窗体
     * @param prmViewInfo
     *            视图信息
     * @NOTE: 此构造器只提供给过滤使用
     */
    public FilterDialog(Dialog prmDialog, PIMViewInfo prmViewInfo) {
        super(prmDialog, false);
        parentDialog = prmDialog; // 保存引用
        viewInfo = prmViewInfo;
        isForFind = false;

        initComponents();
    }

    /**
     * 该构造器用于查找对话盒 创建一个 FilterDialog 的实例
     * 
     * @param prmFrame
     *            父窗体
     * @param prmViewInfo
     *            视图信息
     */
    public FilterDialog(Frame prmFrame, PIMViewInfo prmViewInfo, boolean prmIsFind) {
        super(prmFrame, false);
        viewInfo = prmViewInfo; // 保存引用
        isForFind = prmIsFind;

        initComponents(); // 初始化查找对话框
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     * 
     * @param e
     *            the document event
     */
    public void changedUpdate(
            DocumentEvent e) {
    }

    /**
     * Gives notification that there was an insert into the document. The range given by the DocumentEvent bounds the
     * freshly inserted region.
     * 
     * @param e
     *            the document event
     */
    public void insertUpdate(
            DocumentEvent e) {
        if (getEditorStr(valueCombo).length() > 0 && !addItemBTN.isEnabled()) {
            addItemBTN.setEnabled(true);
        }
    }

    /**
     * Gives notification that a portion of the document has been removed. The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     * 
     * @param e
     *            the document event
     */
    public void removeUpdate(
            DocumentEvent e) {
        if (getEditorStr(valueCombo).length() < 1 && addItemBTN.isEnabled()) {
            addItemBTN.setEnabled(false);
        }
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    public void componentHidden(
            ComponentEvent e) {
    }

    /***/
    public void actionPerformed(
            ActionEvent e) {
        Object src = e.getSource();
        if (src == combSeleApp) { // 查询comboBox的动作
            if (!isNoWorkDone()) // 当前应用中已做了一些设置
                if (SOptionPane.showErrorDialog(MessageCons.W20202) == 0) // 提醒工作将丢失.
                    clear(view != null && view.getParent() instanceof JPanel); // 重置!参数为查找结果集table是否已显示 //清除当前所有设置

            app = combSeleApp.getSelectedIndex(); // 设置查找应用
            // equipModel(app); //装配model

            filterPool.setFilterIndex(app); // 设置过滤信息
            filterPool.setFilterPosition(mutableLocation.getText());
            filterPool.clear();
        } else if (src == receiverBTN) { // 收件人按钮
            showMemberDialog(receiveQuickInfo);
            filterPool.setAddresser(receiveQuickInfo.getText()); // 设置收件人的邮件地址
        } else if (src == senderBTN) {
            showMemberDialog(senderQuickInfo);
            filterPool.setAddressee(senderQuickInfo.getText()); // 设置发件人的邮件地址
        } else if (src == categoryBTN) {
            CategoryDialog categroydialog = new CategoryDialog(this, regimentationTextField.getText());
            categroydialog.setVisible(true);
            regimentationTextField.setText(categroydialog.getCategories());
            filterPool.setCategory(regimentationTextField.getText()); // 设置类型字符串
            categroydialog.dispose(); // 释放
        } else if (src == searchBTN) {
            insertTextItem(findTextCombo, findTextComboModel);
            if (view == null) {
                view = new View_PIMDetails(CASUtility.EMPTYSTR); // 实例化表格视图
                // 得到视图信息
                viewInfo = CASControl.ctrl.getViewInfo(CASUtility.getAPPNodeID(app));
                filterApplication = new FilterResultApplication(viewInfo, filterPool);
                view.setApplication(filterApplication);
                view.setViewInfo(viewInfo);
                // 设置为不可编辑
                PIMTable tmpTab = view.getTable();
                tmpTab.setHasEditor(false);
                tmpTab.setAutoscrolls(false);
                ((PIMTableModelAryBased) tmpTab.getModel()).setCellEditable(false);
            } else {
                if (view.getParent() instanceof JPanel) { // 如果已经在tablePanel上添加了表格
                    tabPanel.remove(view); // 在容器中移除已经添加的面板
                    tabPanel.revalidate(); // 刷新
                }

                viewInfo = CASControl.ctrl.getViewInfo(CASUtility.getAPPNodeID(app));
                filterApplication.refreshView(viewInfo);

                view = filterApplication.getCurrentView(); // 得到当前的视图
                PIMTable tmpTab = view.getTable();
                tmpTab.setHasEditor(false);
                tmpTab.setAutoscrolls(false);
                ((PIMTableModelAryBased) tmpTab.getModel()).setCellEditable(false);
            }

            // =====================================================
            // 在数据库中进行搜索，从搜索结果中返回的Vector中判断是否为空：
            // 1。是否存在一个表，有一个表则先清空其TableModel中数据，否则new一个空表
            // 当Vector空则，表上面显示“该视图中没有项目可显示”
            // 2。Vector不空，则从表视图上得相应字段，Vector中取出字段值加入TableModel,显示表
            // 3.根据位置中的文字，选择表中初始应显示的字段是什么
            // --------------------------------------------------------
            // 现数据库搜索功能未做，暂时vector为空,先让表格显示出来

            // 先删除这个位置上的label
            tabPanel.remove(item);
            getContentPane().setSize(WIDTH, OHEIGHT);// 对话盒重改变高度
            view.setBounds(0, 251, WIDTH, 170);
            // 加入带表格的滚动窗口

            tabPanel.add(view);
            // 显示查找数量的label定位后加入
            item.setBounds(0, 424, item.getPreferredSize().width, CustOpts.LBL_HEIGHT);
            tabPanel.add(item);

            int tmpRecordSize = filterApplication.getResultSize();
            item.setText(String.valueOf(tmpRecordSize).concat(CASUtility.SPACE).concat(CustViewConsts.ITEM)); // 设置查找记录的数目

            if (tmpRecordSize == 0)
                SOptionPane.showErrorDialog(MessageCons.C10174);// 系统没有找到与您所设置的查找条件相匹配的项目。
        } else if (src == clearAllBTN) {
            // 该操作将清除系统当前关于搜索条件的全部设置，您确定要清除吗？
            int tmpValue = -1;
            boolean isViewAdded = view != null && view.getParent() instanceof JPanel;
            if (!isNoWorkDone() || isViewAdded) // 如果已经打开搜索条件
            {
                tmpValue = SOptionPane.showErrorDialog(MessageCons.W20202);
            }
            if (tmpValue == 0) {
                clear(isViewAdded); //
            }
        } else if (src == ok) {
            WhereFactory tmpFactory = new WhereFactory(filterPool);
            filterPool.setFilterString(tmpFactory.getWhere(viewInfo));

            int tmpActivePathID = CustOpts.custOps.getActivePathID();// 得到当前的活动路径
            viewInfo = CASControl.ctrl.getViewInfo(tmpActivePathID);// 得到当前树节点路径的视图信息

            if (!isNoWorkDone()) {
                viewInfo.setFilterInfo(filterPool);
            } else {
                viewInfo.setFilterInfo(null);
            }
            // 更新视图信息
            CASControl.ctrl.getModel().updateViewInfo(viewInfo);

            // 在自定义对话框的筛选
            if (parentDialog != null && parentDialog instanceof CustomizeViewDialog) {
                ((CustomizeViewDialog) parentDialog).equipDiscription();
                parentDialog = null;
            }
            // //保存搜索文字的数据--------
            // StringBuffer buffer = new StringBuffer();
            // for (int i = 0; i < items.size();i++)
            // {
            // buffer.append((String)items.get(i)).append("^");
            // }
            // fileRW.setFindHistory(buffer.toString());
            // //保存搜索文字的数据--------
            dispose();
        } else if (src == cancel) {
            this.removeAll();
            dispose();
        } else if (src == browseBTN) {
            TreeDialog treeDialog = new TreeDialog(this, true);
            treeDialog.show();
            if (treeDialog.getSelectedName() != null) {
                // 当前选择的树路径
                String tmpV =
                        CASUtility.NODEPATHSTART.concat(treeDialog.getAllPath()).concat(treeDialog.getSelectedName())
                                .concat(CASUtility.NODEPATHEND);
                // 得到子路径
                boolean containsSub = treeDialog.containsSubFolders();

                tmpV = CASUtility.getSubPath(tmpV);
                filterPool.setFilterPosition(tmpV);

                if (containsSub) {
                    tmpV = tmpV.concat(CustViewConsts.CONTAINS_SUB_FOLDER);
                }
                mutableLocation.setText(tmpV);

                filterPool.setContainsSubFolders(containsSub);
            }
        } else if (src == addItemBTN) {

            Object tmpSelValue =
                    (valueCombo.isEditable()) ? valueCombo.getEditor().getItem() : valueCombo.getSelectedItem();
            // 是否为编辑的

            if (!valueCombo.isEnabled()) // @NOTE: 如果值的使能状态为false，则把这个值设置为""
            {
                tmpSelValue = CASUtility.EMPTYSTR;
            }

            // 处理对输入数据类型不匹配的问题
            try {
                processCast(tmpSelValue); // 判断造型是否异常
            } catch (NumberFormatException ne) {
                // javax.swing.JOptionPane.showMessageDialog(this, "数值无效");
                // ErrorDialog.showErrorDialog(null, "数值无效", ErrorDialog.OK_ONLY, PaneConstant.TITLE);
                SOptionPane.showErrorDialog(PaneConsts.TITLE, SOptionPane.WARNING_MESSAGE, SOptionPane.STYLE_OK,
                        PaneConsts.FILTERDG);

                addItemBTN.setEnabled(false); // 设置使能状态为false
                ((JTextField) valueCombo.getEditor()).selectAll();
                return;
            } catch (java.text.ParseException pe) {
                // javax.swing.JOptionPane.showMessageDialog(this, "日期格式错误");
                // ErrorDialog.showErrorDialog(null, "日期格式错误",ErrorDialog.OK_ONLY, PaneConstant.TITLE);
                SOptionPane.showErrorDialog(PaneConsts.TITLE, SOptionPane.WARNING_MESSAGE, SOptionPane.STYLE_OK,
                        PaneConsts.FILTERDG_2);
                addItemBTN.setEnabled(false);
                ((JTextField) valueCombo.getEditor()).selectAll();
                return;
            }

            conditionList.setEnabled(true); // 设置Enable状态

            String tmpListV =
                    composeConditionStr(combAllCol.getSelectedItem(), conditionCombo.getSelectedItem(), tmpSelValue);
            conditionListModel.addElement(tmpListV);
            conditionList.setSelectedIndex(conditionListModel.getSize() - 1); // 设置条件显示的JList中选中状态
            filterPool.insert(combAllCol.getSelectedIndex(), conditionCombo.getSelectedIndex(), tmpSelValue,
                    conditionType);

            // 添加条件值到filterInfo中

            // @TODO:
        } else if (src == deleteBTN) {
            int tmpIndex = conditionList.getSelectedIndex();
            if (tmpIndex > -1) // 如果存在大于－1的索引在可以做删除动作
            {
                conditionListModel.remove(tmpIndex); // 删除选中的元素
                // 分理出条件语句中的field字符串来，在filterPool对象删除当前字段
                filterPool.remove(tmpIndex);
            }
            int tmpSize = conditionListModel.getSize();
            conditionList.setSelectedIndex((tmpIndex < tmpSize) ? tmpIndex : tmpSize - 1); // 设置索引值
            // 设置条件列表和删除按钮的使能状态
            conditionList.setEnabled(tmpSize > 0);
            deleteBTN.setEnabled(tmpSize > 0);
        }
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     * 
     * @param e
     *            状态变化事件
     */
    public void itemStateChanged(
            ItemEvent e) {
        Object tmpSrc = e.getSource();
        Object tmpSelItem = e.getItem();

        if (tmpSrc == timeSeleCombo) {
            int tmpIdx = timeSeleCombo.getSelectedIndex();
            filterPool.setTimeConditionIndex(tmpIdx);

            if (tmpIdx != 0) {
                filterPool.setTimeValueIndex(timeItemsCombo.getSelectedIndex());
            }
            timeItemsCombo.setEnabled(tmpIdx != 0); // 设置TIME条件的使能状态
        } else if (tmpSrc == timeItemsCombo) {
            filterPool.setTimeValueIndex(timeItemsCombo.getSelectedIndex());
        } else if (tmpSrc == combAllCol) {
            int tmpAppIndex = combSeleApp.getSelectedIndex();
            int tmpFieldIndex = combAllCol.getSelectedIndex();

            String tmpKey = ((String) CustOpts.custOps.APPNameVec.get(tmpAppIndex));
            String[] tmpTypeAry = MainPane.getApp(tmpKey).getAppTypes();
            setConditionModel(tmpTypeAry[tmpFieldIndex]);
            setRelationState(); // 设置关联选项的状态

            filterPool.setFieldIndex(combAllCol.getSelectedIndex());
        } else if (tmpSrc == conditionCombo) {
            setValueState(tmpSelItem);
            filterPool.setConditionIndex(conditionCombo.getSelectedIndex());
        } else if (tmpSrc == valueCombo) {
            filterPool.setValue(e.getItem().toString());
        } else if (tmpSrc == findTextCombo) {
            filterPool.setFindString((String) e.getItem());
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
        deleteBTN.setEnabled(true);
    }

    public Container getContainer() {
        return getContentPane();
    }

    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    public PIMTextPane getTextPane() {
        return null;
    }

    public void reLayout() {
        tabPanel.setBounds(0, CustOpts.VER_GAP, getWidth(), OHEIGHT);
        browseBTN.setBounds(getWidth() - btnWidth - CustOpts.HOR_GAP * 2, CustOpts.VER_GAP, btnWidth,
                CustOpts.BTN_HEIGHT);
        searchBTN.setBounds(browseBTN.getX(), browseBTN.getY() + browseBTN.getHeight() + CustOpts.VER_GAP, btnWidth,
                CustOpts.BTN_HEIGHT);
        clearAllBTN.setBounds(searchBTN.getX(), searchBTN.getY() + searchBTN.getHeight() + CustOpts.VER_GAP, btnWidth,
                CustOpts.BTN_HEIGHT);
        includeDeled.setBounds(clearAllBTN.getX() - 3, clearAllBTN.getY() + clearAllBTN.getHeight() + CustOpts.VER_GAP,
                includeDeled.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cancel.setBounds(clearAllBTN.getX(), HEIGHT - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP * 2
                - CustOpts.BTN_HEIGHT, btnWidth, CustOpts.BTN_HEIGHT);

        int tHalfWidth = (getWidth() - btnWidth - CustOpts.HOR_GAP * 3 - CustOpts.HOR_GAP) / 2;
        labSeleApp.setBounds(CustOpts.HOR_GAP, browseBTN.getY(), 60, CustOpts.LBL_HEIGHT);
        combSeleApp.setBounds(labSeleApp.getX() + labSeleApp.getWidth(), labSeleApp.getY(),
                tHalfWidth - labSeleApp.getWidth(), CustOpts.BTN_HEIGHT);
        locationLabel.setBounds(combSeleApp.getX() + combSeleApp.getWidth() + CustOpts.HOR_GAP, combSeleApp.getY(),
                locationLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        mutableLocation.setBounds(locationLabel.getX() + locationLabel.getWidth(), locationLabel.getY(), tHalfWidth
                - locationLabel.getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT + 1);
        tabbedPane.setBounds(CustOpts.HOR_GAP, combSeleApp.getY() + combSeleApp.getHeight(),// + CustOpts.VER_GAP,
                mutableLocation.getX() - labSeleApp.getX() + mutableLocation.getWidth(), HEIGHT - CustOpts.SIZE_TITLE
                        - CustOpts.SIZE_EDGE - CustOpts.VER_GAP * 3 - CustOpts.BTN_HEIGHT);
        reLayoutFindTab();
        validate();
        repaint();
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
        combSeleApp.requestDefaultFocus();
    }

    /**
     * @NOTE: 在查找的对话框中要过滤掉一些字段,某些字段是不用查找的例如记录的ID号等等 所以此方法暂时保留
     */
    // /** 过滤字段去掉一些没有用的字段
    // */
    // private void filterField(String[] prmFields, DefaultComboBoxModel prmModel)
    // {
    // for (int i = prmFields.length - 1; i >= 0; i--) //过滤掉字段
    // {
    // prmModel.removeElement(prmFields[i]);
    // }
    // }
    /** 实现接口中的方法 */
    public void release() {
    }

    /**
     * 实现接口中的方法,本例返回空
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

    /**
     * 加载和过滤的条件，这是唯一的可以修改VIEWINFO表中存储的过滤条件
     */
    public void loadData() {
        if (filterPool == null || filterPool.getFieldList().size() < 1) // 没有数据可以加载
        {
            return;
        }
        // 加载数据
        ArrayList fieldList = filterPool.getFieldList();
        ArrayList condiList = filterPool.getCondiList();
        ArrayList valueList = filterPool.getValueList();
        ArrayList typesList = filterPool.getTypesList();
        // 判断模型是否为空
        if (conditionListModel == null) {
            conditionListModel = new DefaultListModel();
        }

        String[] prmField = MainPane.getApp(CustOpts.custOps.APPNameVec.get(viewInfo.getAppIndex())).getAppTexts();

        // 组合数据添加到条件列表中
        for (int tmpSize = fieldList.size() - 1, i = tmpSize; i >= 0; i--) {
            int fieldIndex = ((Integer) fieldList.get(tmpSize - i)).intValue();
            int condiIndex = ((Integer) condiList.get(tmpSize - i)).intValue();
            int typesIndex = ((Integer) typesList.get(tmpSize - i)).intValue();
            // 得到条件数组
            String[] tmpCondi = CASUtility.getTypesAry(typesIndex);
            // 组合条件
            String value = composeConditionStr(prmField[fieldIndex], tmpCondi[condiIndex], valueList.get(tmpSize - i));
            conditionListModel.addElement(value);
        }
        conditionList.setModel(conditionListModel);
        // 设置删除按钮的使能状态
        if (!deleteBTN.isEnabled()) {
            deleteBTN.setEnabled(true);
        }
        // 设置条件列表的选中状态和使能状态
        if (!conditionList.isEnabled()) {
            conditionList.setEnabled(true);
            // 设置选中的索引值
            conditionList.setSelectedIndex(conditionListModel.getSize() - 1);
        }
        // **********************************************************************//
        //
        // @TODO: 加载其它选项卡的过滤条件
        //
    }

    /** 设置数据 */
    public void setData() {
        // 处理条件选项
        String findText =
                findTextCombo.isEditable() ? getEditorStr(findTextCombo) : (String) findTextCombo.getSelectedItem();
        String addressee = receiveQuickInfo.getText();
        String addresser = senderQuickInfo.getText();
        String category = regimentationTextField.getText();
        int selTime = timeSeleCombo.getSelectedIndex();
        int timeItem = timeItemsCombo.getSelectedIndex();
        int condiIndex = conditionCombo.getSelectedIndex();
        int findPosition = comboRecommendCol.getSelectedIndex();
        String value = valueCombo.isEditable() ? getEditorStr(valueCombo) : (String) valueCombo.getSelectedItem();

        // 保存数据到filterPool中
        filterPool.setFindString(findText);
        filterPool.setAddressee(addressee);
        filterPool.setAddresser(addresser);
        filterPool.setCategory(category);
        filterPool.setTimeConditionIndex(selTime);
        filterPool.setTimeValueIndex(timeItem);
        filterPool.setConditionIndex(condiIndex);
        filterPool.setFindPositionIndex(findPosition);
        filterPool.setValue(value);
    }

    /**
     * 返回字段已更改标志
     * 
     * @return 是否已更改
     */
    public boolean isModified() {
        return modified;
    }

    /** 初始化祝查找组建 */
    private void initComponents() {
        int w = isForFind ? WIDTH : WIDTH - CustOpts.HOR_GAP - btnWidth; // 得到过滤对话框的尺寸
        int h = isForFind ? HEIGHT : HEIGHT - 2 * CustOpts.VER_GAP - CustOpts.LBL_HEIGHT;
        setBounds((CustOpts.SCRWIDTH - w) / 2, (CustOpts.SCRHEIGHT - h) / 2, w, h); // 对话框的默认尺寸。
        setTitle(CustViewConsts.FIND_TITLE); // 标题
        getContentPane().setLayout(null);

        if (viewInfo != null)
            filterPool = viewInfo.getFilterInfo(); // 实例化过滤规则池
        if (filterPool == null)
            filterPool = new FilterInfo(); // 初始化过滤信息

        if (isForFind)
            initFindComponent(); // 组件初始化并布局
        else
            initFilterComponent(); // 组件初始化并布局

        equipAllComponents(viewInfo); // 装备所有有模型的组件的模型

        searchBTN.addActionListener(this); // 给组件加上监听器
        clearAllBTN.addActionListener(this);
        browseBTN.addActionListener(this);
        findTextCombo.addActionListener(this);
        cancel.addActionListener(this);
        addItemBTN.addActionListener(this);
        deleteBTN.addActionListener(this);
        receiverBTN.addActionListener(this);
        senderBTN.addActionListener(this);
        categoryBTN.addActionListener(this);
        combSeleApp.addActionListener(this);
        timeSeleCombo.addItemListener(this);
        timeItemsCombo.addItemListener(this);
        combAllCol.addItemListener(this);
        conditionCombo.addItemListener(this);
        conditionList.addListSelectionListener(this);
        valueTextDoc.addDocumentListener(this);
        getContentPane().addComponentListener(this);

        if (!isForFind)
            ok.addActionListener(this);
        else
            loadData();

        int tIdx = viewInfo.getAppIndex(); // 初始化要查找的类型
        if (tIdx >= 0)
            combSeleApp.setSelectedIndex(tIdx);
        else
            combSeleApp.setSelectedIndex(CustOpts.custOps.APPNameVec.size());
        filterPool.setFieldIndex(combSeleApp.getSelectedIndex());

        timeItemsCombo.setEnabled(false); // 默认设置
        conditionList.setEnabled(false); // 条件列表
        deleteBTN.setEnabled(false); // 设置使能状态
    }

    /* 对普通选项卡进行初始化其中的组件并布局 */
    private void reLayoutFindTab() {
        int tTabbedPane_W = tabbedPane.getWidth();// 计算出选项卡的宽度和高度
        searchTextLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, btnWidthInTab + CustOpts.HOR_GAP,
                CustOpts.LBL_HEIGHT);
        findTextCombo.setBounds(searchTextLabel.getX() + searchTextLabel.getWidth(), searchTextLabel.getY(),
                tTabbedPane_W - btnWidthInTab - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);

        locationLabel2.setBounds(searchTextLabel.getX(), searchTextLabel.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, searchTextLabel.getWidth(), CustOpts.LBL_HEIGHT);
        comboRecommendCol.setBounds(locationLabel2.getX() + locationLabel2.getWidth(), locationLabel2.getY(),
                findTextCombo.getWidth(), CustOpts.BTN_HEIGHT);

        sperator1.setBounds(locationLabel2.getX(), comboRecommendCol.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP
                / 2, tTabbedPane_W - CustOpts.HOR_GAP * 2, CustOpts.SEP_HEIGHT);

        senderBTN.setBounds(sperator1.getX(), sperator1.getY() + sperator1.getHeight() + CustOpts.VER_GAP,
                btnWidthInTab, CustOpts.BTN_HEIGHT);
        senderQuickInfo.setBounds(senderBTN.getX() + senderBTN.getWidth() + CustOpts.HOR_GAP, senderBTN.getY(),
                comboRecommendCol.getWidth(), CustOpts.BTN_HEIGHT);// 设置高度为20
        receiverBTN.setBounds(senderBTN.getX(), senderBTN.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                btnWidthInTab, CustOpts.BTN_HEIGHT);
        receiveQuickInfo.setBounds(senderQuickInfo.getX(), receiverBTN.getY(), senderQuickInfo.getWidth(),
                CustOpts.BTN_HEIGHT);// 高度20//设置收件人文本框
        categoryBTN.setBounds(receiverBTN.getX(), receiverBTN.getY() + receiverBTN.getHeight() + CustOpts.VER_GAP,
                btnWidthInTab, CustOpts.BTN_HEIGHT);// 设置类别按钮
        regimentationTextField.setBounds(receiveQuickInfo.getX(), categoryBTN.getY(), receiveQuickInfo.getWidth(),
                CustOpts.BTN_HEIGHT);// 设置类别文本框

        sperator2.setBounds(categoryBTN.getX(), categoryBTN.getY() + categoryBTN.getHeight() + CustOpts.VER_GAP / 2,
                sperator1.getWidth(), CustOpts.SEP_HEIGHT);

        timeLabel.setBounds(sperator2.getX(), sperator2.getY() + sperator2.getHeight() + CustOpts.VER_GAP,
                btnWidthInTab + CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        timeSeleCombo.setBounds(timeLabel.getX() + timeLabel.getWidth(), timeLabel.getY(),
                (regimentationTextField.getWidth() - CustOpts.HOR_GAP) / 2, CustOpts.BTN_HEIGHT);
        timeItemsCombo.setBounds(timeSeleCombo.getX() + timeSeleCombo.getWidth() + CustOpts.HOR_GAP,
                timeSeleCombo.getY(), timeSeleCombo.getWidth(), CustOpts.BTN_HEIGHT);// 设置时间选项下拉框

        // *********************************************************************************************
        // 对高级选项卡进行初始化其中的组件并布局
        fieldLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, btnWidthInTab, CustOpts.LBL_HEIGHT);
        combAllCol.setBounds(fieldLabel.getX(), fieldLabel.getY() + fieldLabel.getHeight(), btnWidthInTab,
                CustOpts.BTN_HEIGHT);
        conditionLabel.setBounds(fieldLabel.getX() + combAllCol.getWidth() + CustOpts.HOR_GAP, fieldLabel.getY(),
                combAllCol.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        conditionCombo.setBounds(conditionLabel.getX(), conditionLabel.getY() + conditionLabel.getHeight(),
                combAllCol.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        valueLabel.setBounds(conditionLabel.getX() + conditionLabel.getWidth() + CustOpts.HOR_GAP,
                conditionLabel.getY(), tTabbedPane_W - conditionLabel.getX() - conditionLabel.getWidth()
                        - CustOpts.HOR_GAP * 2, CustOpts.LBL_HEIGHT);
        valueCombo.setBounds(valueLabel.getX(), valueLabel.getY() + valueLabel.getHeight(), valueLabel.getWidth(),
                CustOpts.BTN_HEIGHT);

        deleteBTN.setBounds(tTabbedPane_W - CustOpts.HOR_GAP - deleteBTN.getPreferredSize().width, valueCombo.getY()
                + valueCombo.getHeight() + CustOpts.VER_GAP, deleteBTN.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        addItemBTN.setBounds(deleteBTN.getX() - CustOpts.HOR_GAP - addItemBTN.getPreferredSize().width,
                deleteBTN.getY(), addItemBTN.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        topLabel.setBounds(fieldLabel.getX(), addItemBTN.getY() + addItemBTN.getHeight() - CustOpts.LBL_HEIGHT,
                topLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        conditionList.setBounds(topLabel.getX(), topLabel.getY() + topLabel.getHeight() + CustOpts.VER_GAP,
                tTabbedPane_W - 2 * CustOpts.HOR_GAP, timeLabel.getY() + CustOpts.BTN_HEIGHT - topLabel.getY()
                        - topLabel.getHeight() - CustOpts.VER_GAP);
        // @NOTE:TabPane的高度因为包涵切换标签的高度,不能用.
    }

    /** 给所有有Model的组件配置其数据模型 */
    private void equipAllComponents(
            PIMViewInfo prmViewInfo) {
        // *******************************************************
        // 以下为配置第一、二选项卡中的模型
        equipModel(combSeleApp.getSelectedIndex());
        // *******************************************************//

        valueCombo.setSelectedIndex(-1);
        timeSeleCombo.setSelectedIndex(0);
        findTextCombo.setSelectedIndex(-1);

        filterPool.setFilterIndex(combSeleApp.getSelectedIndex());
        filterPool.setFilterPosition(mutableLocation.getText());
        filterPool.setTimeConditionIndex(0);

        String tmpKey = ((String) CustOpts.custOps.APPNameVec.get(combSeleApp.getSelectedIndex()));
        String[] tmpTypeAry = MainPane.getApp(tmpKey).getAppTypes();
        setConditionModel(tmpTypeAry[combAllCol.getSelectedIndex()]);
        setRelationState(); // 设置初始化状态

        // 初始化App类型
        app = prmViewInfo.getAppIndex();
    }

    /** 实际上是布局; */
    private void initFilterComponent() {

        // 这两个变量用来给组件定位,所有的组件都根据它来计算
        int X_Coordinate = WIDTH - btnWidth;
        int Y_Coordinate = 0;
        btnWidth = CASUtility.getMaxWidth(new JButton[] { browseBTN, searchBTN, clearAllBTN, cancel });
        JLabel searchLabel = new JLabel(CustViewConsts.FIND); // 查找标签（直接放在对话盒面板上）
        combSeleApp = new JComboBox(); // 设置查找下拉框 因为为不可见所有尺寸不重要
        mutableLocation = new JTextField(CustViewConsts.PERSONAL_FOLDER); // 位置内容
        cancel = new JButton(CustViewConsts.CANCEL); // CACEL按钮
        ok = new JButton(CustViewConsts.APPLY); // OK按钮

        searchLabel.setDisplayedMnemonic('K');
        searchLabel.setLabelFor(combSeleApp);
        searchLabel.setVisible(false);
        combSeleApp.setVisible(false); // 设置为不可见
        mutableLocation.setVisible(false); // 设置为不可见

        for (int i = 0, len = CustOpts.custOps.APPCapsVec.size(); i < len; i++)
            combSeleApp.addItem(CustOpts.custOps.APPCapsVec.get(i));
        combSeleApp.addItem(CustViewConsts.ALL_ITEMS);

        searchLabel.setBounds(X_Coordinate, Y_Coordinate, searchLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        combSeleApp.setBounds(searchLabel.getX() + searchLabel.getWidth(), searchLabel.getY(),
                combSeleApp.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        int locationHeight = mutableLocation.getPreferredSize().height;
        mutableLocation.setBounds(0, 0, 20, locationHeight);
        X_Coordinate = WIDTH - cancel.getPreferredSize().width;
        Y_Coordinate = HEIGHT - cancel.getPreferredSize().height;
        cancel.setBounds(X_Coordinate, Y_Coordinate, cancel.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        X_Coordinate -= cancel.getPreferredSize().width + CustOpts.HOR_GAP;
        ok.setBounds(X_Coordinate, Y_Coordinate, ok.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        getContentPane().add(searchLabel);
        getContentPane().add(combSeleApp);
        getContentPane().add(cancel);
        getContentPane().add(ok);

        tabbedPane = initFilterTabbedPane();// 初始化两个选项卡
    }

    /** 由查找对话盒的构造器调用 */
    private void initFindComponent() {
        tabPanel = new JPanel(); // 用于选项卡面板
        browseBTN = new JButton(CustViewConsts.BROWSE); // 设置browse按钮（直接放在对话盒面板上）
        searchBTN = new JButton(CustViewConsts.BEGIN_TO_SEARCH); // 设置search按钮（放在选项卡面板上）
        clearAllBTN = new JButton(CustViewConsts.CLEAR_ALL); // 全部清除按钮 CLEAR_ALL（放在选项卡面板上）
        cancel = new JButton(DlgConst.FINISH_BUTTON); // CACEL按钮（放在选项卡面板上）
        includeDeled = new JCheckBox(CustViewConsts.INCLUD_DELED); // 是否包涵回收站中的项目.
        labSeleApp = new JLabel(CustViewConsts.FIND); // 查找标签（直接放在对话盒面板上）
        combSeleApp = new JComboBox(); // 设置查找下拉框（直接放在对话盒面板上）
        mutableLocation = new JTextField(PaneConsts.HEAD_PAGE); // 位置内容（直接放在对话盒面板上）
        locationLabel = new JLabel(CustViewConsts.LOCATION); // 位置标签（直接放在对话盒面板上）
        item = new JLabel();
        tabbedPane = new JTabbedPane();// 创建出一个选项卡的实例

        getRootPane().setDefaultButton(searchBTN); // browseBTN设为缺省按钮
        tabPanel.setLayout(null);
        browseBTN.setMnemonic('B');
        searchBTN.setMnemonic('S');
        labSeleApp.setDisplayedMnemonic('K');
        labSeleApp.setLabelFor(combSeleApp);

        browseBTN.setMargin(new Insets(4, 4, 4, 4));
        clearAllBTN.setMargin(browseBTN.getMargin());
        searchBTN.setMargin(browseBTN.getMargin());
        cancel.setMargin(browseBTN.getMargin());

        mutableLocation.setHorizontalAlignment(SwingConstants.CENTER);
        mutableLocation.setEditable(false);
        for (int i = 0, len = CustOpts.custOps.APPCapsVec.size(); i < len; i++)
            combSeleApp.addItem(CustOpts.custOps.APPCapsVec.get(i));
        combSeleApp.addItem(CustViewConsts.ALL_ITEMS);

        combSeleApp.requestFocus();
        initFindTabbedPane();// 下面干初始化两个选项卡

        // 布局-------------------
        btnWidth = CASUtility.getMaxWidth(new JButton[] { browseBTN, searchBTN, clearAllBTN, cancel });
        btnWidthInTab = CASUtility.getMaxWidth(new JButton[] { senderBTN, receiverBTN, categoryBTN });
        reLayout();

        item.setBounds(0, 256, WIDTH, CustOpts.LBL_HEIGHT);
        // JSplitPane不能用，影响表格的滚动条显示-------------------------------
        // splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,false);
        // splitPane.setTopComponent(tabPanel);//选项卡面板放上面
        // splitPane.setBottomComponent(tablePanel);
        // splitPane.resetToPreferredSizes();
        // splitPane.setBorder(null);
        // splitPane.setBounds(0, CustOpts.LABEL_HEIGHT+CustOpts.VER_GAP,WIDTH,450);
        // splitPane.setDividerSize(1);
        // JSplitPane不能用，影响表格的滚动条显示-------------------------------

        getContentPane().add(browseBTN);
        tabPanel.add(searchBTN);
        tabPanel.add(clearAllBTN);
        tabPanel.add(includeDeled);
        tabPanel.add(cancel);
        tabPanel.add(item);
        getContentPane().add(labSeleApp);
        getContentPane().add(combSeleApp);
        getContentPane().add(locationLabel);
        getContentPane().add(mutableLocation);
        getContentPane().add(tabPanel);

        SwingUtilities.invokeLater(this);
    }

    /** 初始化两个选项卡（用于init()方法中调用） */
    private void initFindTabbedPane() {
        normalPanel = new JPanel();// 创建出普通面板的实例
        advancePanel = new JPanel();// 创建出高级面板的实例
        searchTextLabel = new JLabel(CustViewConsts.SEARCH_TEXT);// 查找文字标签
        findTextCombo = new JComboBox();// 查找文字标签下拉框
        locationLabel2 = new JLabel(CustViewConsts.LOCATE);// 位置标签
        comboRecommendCol = new JComboBox();
        sperator1 = new PIMSeparator(CASUtility.EMPTYSTR);// 设置分隔线
        senderBTN = new JButton(CustViewConsts.SENDER);// 设置发件人按钮
        senderQuickInfo = new QuickInfoField(normalPanel);// 设置发件人文本框
        receiveQuickInfo = new QuickInfoField(normalPanel); //
        receiverBTN = new JButton(CustViewConsts.RECEIVER);// 设置收件人按钮
        categoryBTN = new JButton(DlgConst.CATEGORIES);
        regimentationTextField = new JTextField();
        sperator2 = new PIMSeparator(CASUtility.EMPTYSTR);// 设置分隔线
        timeLabel = new JLabel(CustViewConsts.TIME);
        timeSeleCombo = new JComboBox(CustViewConsts.TIME_OPTION);// 设置时间类别下拉框
        timeItemsCombo = new JComboBox(CustViewConsts.TIME_VALUE);
        combAllCol = new JComboBox();// 创建字段下拉框
        fieldLabel = new JLabel(CustViewConsts.FIELD_ITEM); // 创建字段下拉框的标签
        combModelAllCol = new DefaultComboBoxModel(IntlModelConstants.CONTACTS_FIELD);// 初始化Model
        conditionLabel = new JLabel(CustViewConsts.CONDITION_ITEM);// 创建条件下拉框的标签
        conditionCombo = new JComboBox();// 创建条件下拉框
        valueLabel = new JLabel(CustViewConsts.VALUE_ITEM);// 创建值下拉框的标签
        valueCombo = new JComboBox();// 创建值下拉框
        deleteBTN = new JButton(CustViewConsts.DELETE_BTN);// 列表框下面的删除按钮
        addItemBTN = new JButton(CustViewConsts.ADD_TO_LIST);// 添至列表按钮
        topLabel = new JLabel(CustViewConsts.SERARCH_BY_THIS_ITEMS);// 最上面那个标签
        conditionListModel = new DefaultListModel();
        conditionList = new JList(conditionListModel);
        valueTextDoc = ((JTextField) valueCombo.getEditor().getEditorComponent()).getDocument();

        normalPanel.setLayout(null);
        advancePanel.setLayout(null);
        searchTextLabel.setDisplayedMnemonic('C');
        locationLabel2.setDisplayedMnemonic('I');
        timeLabel.setDisplayedMnemonic('M');// 设置时间标签
        fieldLabel.setDisplayedMnemonic('I');
        conditionLabel.setDisplayedMnemonic('C');
        valueLabel.setDisplayedMnemonic('U');
        senderBTN.setMnemonic('R');
        receiverBTN.setMnemonic('O');
        categoryBTN.setMnemonic('G');
        deleteBTN.setMnemonic('R');
        addItemBTN.setMnemonic('D');

        senderBTN.setMargin(new Insets(0, 0, 0, 0));
        receiverBTN.setMargin(senderBTN.getMargin());
        categoryBTN.setMargin(senderBTN.getMargin());

        normalPanel.setBorder(null);
        advancePanel.setBorder(null);
        conditionList.setBorder(BorderFactory.createEtchedBorder());
        findTextCombo.setEditable(true); // combo可编辑
        locationLabel2.setLabelFor(comboRecommendCol);
        timeLabel.setLabelFor(timeSeleCombo);
        conditionLabel.setLabelFor(conditionCombo);
        valueLabel.setLabelFor(valueCombo);
        valueCombo.setEditable(true);
        findEditor = (JTextField) findTextCombo.getEditor().getEditorComponent(); // 得到combo的文本编辑器
        // findTextCombo.setMaximumRowCount(5);
        // senderQuickInfo.initArys();
        combAllCol.setModel(combModelAllCol);
        conditionList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        conditionList.setSelectedIndex(0);

        // 装配combo中数据-----------
        String tmpHistory = CustOpts.custOps.getFindHistory();
        items = new Vector();
        if (tmpHistory.length() == 0) {
            findTextComboModel = new DefaultComboBoxModel();
        } else if (tmpHistory.length() != 0) {
            StringTokenizer token = new StringTokenizer(tmpHistory, "^");
            while (token.hasMoreTokens()) {
                items.addElement(token.nextToken());
                // findTextCombo.addItem(token.nextToken());
            }
            findTextComboModel = new DefaultComboBoxModel(items);
        }
        findTextCombo.setModel(findTextComboModel);
        // 装配combo中数据-----------

        // 构建列表框实例并布局
        tabPanel.add(tabbedPane);// 把选项卡加入选项卡面板
        // tabbedPane.addTab(CustViewConsts.NORMAL, normalPanel);//把两个选项卡面板加入到选项卡组件
        tabbedPane.addTab(CustViewConsts.ADVANCE, advancePanel);
        // normalPanel.add(searchTextLabel);
        // normalPanel.add(searchTextLabel);
        // normalPanel.add(findTextCombo);
        // normalPanel.add(locationLabel2);
        // normalPanel.add(comboRecommendCol);
        // normalPanel.add(sperator1);
        // normalPanel.add(senderBTN);
        // normalPanel.add(senderQuickInfo);
        // normalPanel.add(receiverBTN);
        // normalPanel.add(receiveQuickInfo);
        // normalPanel.add(categoryBTN);
        // normalPanel.add(regimentationTextField);
        // normalPanel.add(sperator2);
        // normalPanel.add(timeLabel);
        // normalPanel.add(timeLabel);
        // normalPanel.add(timeSeleCombo);
        // normalPanel.add(timeItemsCombo);
        advancePanel.add(fieldLabel);
        advancePanel.add(combAllCol);
        advancePanel.add(conditionLabel);
        advancePanel.add(conditionCombo);
        advancePanel.add(valueLabel);
        advancePanel.add(valueCombo);
        advancePanel.add(deleteBTN);
        advancePanel.add(addItemBTN);
        advancePanel.add(topLabel);
        advancePanel.add(conditionList);
    }

    /** 显示联系人对话框 */
    private void showMemberDialog(
            QuickInfoField prmText) {
        String tmpRetained = CASUtility.getSelectedText(prmText); // 已经选择的联系人
        SelectedNewMemberDlg tmpMember = new SelectedNewMemberDlg(this, true, tmpRetained);
        tmpMember.setVisible(true);
        // 设置收件人
        prmText.setContents(tmpMember.getReceiverInfsVector(), tmpMember.getReceiverIndexVec());
        tmpMember.dispose(); // 释放
    }

    /** 插入 */
    private void insertTextItem(
            JComboBox prmCombo,
            DefaultComboBoxModel prmModel) {

        Object tmpEditItem = prmCombo.getEditor().getItem(); // 处理ComboBoxEditor的事件，在findTextCombo中插入一个元素事件
        filterPool.setFindString((String) tmpEditItem);
        prmCombo.setSelectedIndex(-1); // 显示选中默认的0号索引

        if (tmpEditItem == null || tmpEditItem.toString().length() < 1 || prmModel == null) // 空返回不用查到Model中
            return;

        int tmpPos = prmModel.getIndexOf(tmpEditItem);

        if (tmpPos != -1) { // 判断是否包含但前的元素
            prmModel.removeElement(tmpEditItem); // 删除当前的元素
            prmModel.insertElementAt(tmpEditItem, 0);
        } else
            prmModel.insertElementAt(tmpEditItem, 0);

        for (int i = prmModel.getSize() - 1; i >= 10; i--)
            // 保持列表中的元素数最多为10个，因为OutLook为5个，我们这个最多为10个
            prmModel.removeElementAt(i);
    }

    private JTabbedPane initFilterTabbedPane() {

        // 这两个变量用来给组件定位,所有的组件都根据它来计算
        int X_Coordinate = 0;
        int Y_Coordinate = 0; // CustOpts.LABEL_HEIGHT + CustOpts.VER_GAP;
        btnWidth = CASUtility.getMaxWidth(new JButton[] { browseBTN, searchBTN, clearAllBTN, cancel });
        // 计算出选项卡的宽度和高度
        int tabbedPaneWidth = WIDTH - btnWidth - CustOpts.HOR_GAP;
        int tabbedPaneHeight = 222;
        // dlgSize.getSize().height - CustOpts.LABEL_HEIGHT - ok.getPreferredSize().height - CustOpts.VER_GAP*2;

        // 创建出一个选项卡的实例
        JTabbedPane tmpTabbedPane = new JTabbedPane();
        // //创建出普通面板的实例
        // JPanel normalPanel = new JPanel();
        // normalPanel.setBorder(null);

        // 创建出高级面板的实例
        JPanel advancePanel = new JPanel();
        advancePanel.setBorder(null);

        // 把选项卡加入对话盒面板
        getContentPane().add(tmpTabbedPane);
        tmpTabbedPane.setBounds(X_Coordinate, Y_Coordinate, tabbedPaneWidth, tabbedPaneHeight);

        // 把两个选项卡面板加入到选项卡组件
        // tmpTabbedPane.addTab(CustomizeViewConstants.NORMAL, normalPanel);
        tmpTabbedPane.addTab(CustViewConsts.ADVANCE, advancePanel);

        // ************************************************************************

        // 对普通选项卡进行初始化其中的组件并布局
        // 重置定位坐标
        X_Coordinate = CustOpts.HOR_GAP;
        Y_Coordinate = CustOpts.VER_GAP;

        // 查找文字标签
        JLabel searchTextLabel = new JLabel(CustViewConsts.SEARCH_TEXT);
        searchTextLabel.setDisplayedMnemonic('C');
        searchTextLabel.setBounds(X_Coordinate, Y_Coordinate, searchTextLabel.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        // normalPanel.add(searchTextLabel);

        // 以下这个变量表示右边组合框，textField 的宽度
        int rightCompWidth = tabbedPaneWidth - btnWidth - CustOpts.HOR_GAP * 4;

        String[] tmp = { "" };
        // 查找文字标签下拉框
        findTextCombo = new JComboBox();
        searchTextLabel.setLabelFor(findTextCombo);
        findTextCombo.setEditable(true); // combo可编辑
        findEditor = (JTextField) findTextCombo.getEditor().getEditorComponent(); // 得到combo的文本编辑器
        // findTextCombo.setMaximumRowCount(5);
        searchTextLabel.setBounds(X_Coordinate, Y_Coordinate, btnWidth + CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        findTextCombo.setBounds(searchTextLabel.getX() + searchTextLabel.getWidth(), searchTextLabel.getY(),
                rightCompWidth, CustOpts.BTN_HEIGHT);
        // normalPanel.add(searchTextLabel);
        // normalPanel.add(findTextCombo);
        // 装配combo中数据-----------
        String tmpHistory = CustOpts.custOps.getFindHistory();
        items = new Vector();
        if (tmpHistory.length() == 0) {
            findTextComboModel = new DefaultComboBoxModel();
        } else if (tmpHistory.length() != 0) {
            StringTokenizer token = new StringTokenizer(tmpHistory, "^");
            while (token.hasMoreTokens()) {
                items.addElement(token.nextToken());
                // findTextCombo.addItem(token.nextToken());
            }
            findTextComboModel = new DefaultComboBoxModel(items);
        }
        findTextCombo.setModel(findTextComboModel);
        findTextCombo.setSelectedIndex(-1);
        // 装配combo中数据-----------

        // 位置标签
        Y_Coordinate += CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;
        JLabel locationLabel = new JLabel(CustViewConsts.LOCATE);
        comboRecommendCol = new JComboBox(tmp);
        locationLabel.setDisplayedMnemonic('I');
        locationLabel.setBounds(X_Coordinate, Y_Coordinate, btnWidth + CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        comboRecommendCol.setBounds(locationLabel.getX() + locationLabel.getWidth(), locationLabel.getY(),
                rightCompWidth, CustOpts.BTN_HEIGHT);
        // normalPanel.add(normalPanel);
        // normalPanel.add(comboRecommendCol);

        // 位置下拉框
        // Y_Coordinate += findTextCombo.getSize().height + CustOpts.VER_GAP ;

        Y_Coordinate += comboRecommendCol.getPreferredSize().height;
        int spLength = tabbedPaneWidth - CustOpts.HOR_GAP * 3;

        // 设置分隔线
        PIMSeparator sperator1 = new PIMSeparator(CASUtility.EMPTYSTR);
        sperator1.setBounds(X_Coordinate, Y_Coordinate - 1, spLength, CustOpts.SEP_HEIGHT);
        // normalPanel.add(sperator1);

        // 设置发件人按钮
        Y_Coordinate += CustOpts.VER_GAP * 3;
        senderBTN = new JButton(CustViewConsts.SENDER);
        senderBTN.setMnemonic('R');
        senderBTN.setBounds(X_Coordinate, Y_Coordinate, btnWidth, CustOpts.BTN_HEIGHT);
        // normalPanel.add(senderBTN);

        // 坐标重置
        X_Coordinate = btnWidth + CustOpts.HOR_GAP * 2;

        // 设置发件人文本框
        senderQuickInfo = new QuickInfoField(normalPanel);
        senderQuickInfo.initArys();
        senderQuickInfo.setBounds(X_Coordinate, Y_Coordinate, rightCompWidth, CustOpts.LBL_HEIGHT);
        // 设置高度为20
        normalPanel.add(senderQuickInfo);

        // 坐标重置
        X_Coordinate = CustOpts.HOR_GAP;
        Y_Coordinate += senderBTN.getPreferredSize().height + CustOpts.VER_GAP;
        // 设置收件人按钮
        receiverBTN = new JButton(CustViewConsts.RECEIVER);
        receiverBTN.setMnemonic('O');
        receiverBTN.setBounds(X_Coordinate, Y_Coordinate, btnWidth, CustOpts.BTN_HEIGHT);
        normalPanel.add(receiverBTN);

        // 设置收件人文本框
        X_Coordinate = btnWidth + CustOpts.HOR_GAP * 2;
        receiveQuickInfo = new QuickInfoField(normalPanel); //
        receiveQuickInfo.setBounds(X_Coordinate, Y_Coordinate, rightCompWidth, CustOpts.LBL_HEIGHT);
        // 高度20
        normalPanel.add(receiveQuickInfo);

        // 设置类别按钮
        X_Coordinate = CustOpts.HOR_GAP;
        Y_Coordinate += receiverBTN.getPreferredSize().height + CustOpts.VER_GAP;
        categoryBTN = new JButton(DlgConst.CATEGORIES);
        categoryBTN.setMnemonic('G');
        categoryBTN.setBounds(X_Coordinate, Y_Coordinate, btnWidth, CustOpts.BTN_HEIGHT);
        normalPanel.add(categoryBTN);

        // 设置类别文本框
        X_Coordinate = btnWidth + CustOpts.HOR_GAP * 2;
        regimentationTextField = new JTextField();
        regimentationTextField.setBounds(X_Coordinate, Y_Coordinate, rightCompWidth, CustOpts.BTN_HEIGHT);
        normalPanel.add(regimentationTextField);

        // 设置分隔线
        X_Coordinate = CustOpts.HOR_GAP;
        Y_Coordinate += comboRecommendCol.getPreferredSize().height;
        PIMSeparator sperator2 = new PIMSeparator(CASUtility.EMPTYSTR);
        sperator2.setBounds(X_Coordinate, Y_Coordinate - 1, spLength, CustOpts.SEP_HEIGHT);
        normalPanel.add(sperator2);

        // 设置时间标签
        Y_Coordinate += CustOpts.VER_GAP * 3;
        JLabel timeLabel = new JLabel(CustViewConsts.TIME);
        timeSeleCombo = new JComboBox(CustViewConsts.TIME_OPTION);
        timeLabel.setDisplayedMnemonic('M');
        timeLabel.setLabelFor(timeSeleCombo);
        // 设置时间类别下拉框
        // Y_Coordinate += regimentationTextField.getSize().height + CustOpts.VER_GAP*2 +
        // sperator2.getPreferredSize().height;
        int tmpTimeSeleComboWidth = receiveQuickInfo.getWidth() / 2 - CustOpts.HOR_GAP;
        timeLabel.setBounds(X_Coordinate, Y_Coordinate, btnWidth + CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        timeSeleCombo.setBounds(timeLabel.getX() + timeLabel.getWidth(), timeLabel.getY(), tmpTimeSeleComboWidth,
                CustOpts.BTN_HEIGHT);
        normalPanel.add(timeLabel);

        // 设置时间选项下拉框
        X_Coordinate += btnWidth + timeSeleCombo.getPreferredSize().width + CustOpts.HOR_GAP * 2;
        rightCompWidth -= timeSeleCombo.getPreferredSize().width + CustOpts.HOR_GAP;
        timeItemsCombo = new JComboBox(CustViewConsts.TIME_VALUE);
        timeItemsCombo.setBounds(X_Coordinate, Y_Coordinate, rightCompWidth, CustOpts.BTN_HEIGHT);
        normalPanel.add(timeItemsCombo);

        // *********************************************************************************************

        // 对高级选项卡进行初始化其中的组件并布局
        // 坐标重置
        X_Coordinate = CustOpts.HOR_GAP;
        Y_Coordinate = CustOpts.VER_GAP - Y_OFFSET;

        // 最上面那个标签
        JLabel topLabel = new JLabel(CustViewConsts.SERARCH_BY_THIS_ITEMS);
        topLabel.setBounds(X_Coordinate, Y_Coordinate, topLabel.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        advancePanel.add(topLabel);
        // 坐标加值
        Y_Coordinate += CustOpts.LBL_HEIGHT; // + CustOpts.VER_GAP;
        // 取列表框宽度和高度
        int listWidth = tabbedPaneWidth - CustOpts.HOR_GAP * 3;
        int listHeight = CustOpts.LBL_HEIGHT * 4 + CustOpts.VER_GAP * 2;
        // 构建列表框实例并布局
        conditionListModel = new DefaultListModel();
        conditionList = new JList(conditionListModel);
        conditionList.setBounds(X_Coordinate, Y_Coordinate, listWidth, listHeight);
        conditionList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        advancePanel.add(conditionList);
        conditionList.setSelectedIndex(0);
        // 保存列表框的尺寸和定位
        conditionBounds = new Rectangle(X_Coordinate, Y_Coordinate, listWidth, listHeight);

        // 坐标加值
        X_Coordinate = tabbedPaneWidth - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP * 2;
        Y_Coordinate += listHeight + CustOpts.VER_GAP;

        // 列表框下面的删除按钮
        deleteBTN = new JButton(CustViewConsts.DELETE_BTN);
        deleteBTN.setMnemonic('R');
        deleteBTN.setBounds(X_Coordinate, Y_Coordinate, deleteBTN.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        advancePanel.add(deleteBTN);

        // 添至列表按钮
        addItemBTN = new JButton(CustViewConsts.ADD_TO_LIST);
        addItemBTN.setMnemonic('D');
        X_Coordinate -= addItemBTN.getPreferredSize().width + CustOpts.HOR_GAP;
        addItemBTN.setBounds(X_Coordinate, Y_Coordinate, addItemBTN.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        advancePanel.add(addItemBTN);

        // 下面是由下到上布局
        X_Coordinate = CustOpts.HOR_GAP;
        Y_Coordinate += CustOpts.VER_GAP + CustOpts.BTN_HEIGHT;
        // 创建字段下拉框
        combAllCol = new JComboBox(tmp);

        // 创建字段下拉框的标签
        JLabel fieldLabel = new JLabel(CustViewConsts.FIELD_ITEM);
        fieldLabel.setDisplayedMnemonic('I');
        fieldLabel.setLabelFor(combAllCol);// 联动
        fieldLabel.setBounds(X_Coordinate, Y_Coordinate, OFFSET, CustOpts.BTN_HEIGHT);
        combAllCol.setBounds(fieldLabel.getX() + fieldLabel.getWidth(), fieldLabel.getY(), btnWidth,
                CustOpts.BTN_HEIGHT);
        advancePanel.add(fieldLabel);
        advancePanel.add(combAllCol);
        // X坐标右移
        X_Coordinate += btnWidth + CustOpts.HOR_GAP;
        // 创建条件下拉框的标签
        JLabel conditionLabel = new JLabel(CustViewConsts.CONDITION_ITEM);
        conditionCombo = new JComboBox();// 创建条件下拉框
        conditionLabel.setDisplayedMnemonic('C');
        conditionLabel.setLabelFor(conditionCombo);
        conditionLabel.setBounds(X_Coordinate, Y_Coordinate, OFFSET, CustOpts.LBL_HEIGHT);
        conditionCombo.setBounds(conditionLabel.getX() + conditionLabel.getWidth(), conditionLabel.getY(),
                combAllCol.getPreferredSize().width - CustOpts.HOR_GAP + 1, CustOpts.BTN_HEIGHT);
        advancePanel.add(conditionLabel);
        advancePanel.add(conditionCombo);

        // X坐标右移
        X_Coordinate += conditionCombo.getPreferredSize().width + CustOpts.HOR_GAP;

        JLabel valueLabel = new JLabel(CustViewConsts.VALUE_ITEM);// 创建值下拉框的标签
        valueCombo = new JComboBox();// 创建值下拉框
        valueLabel.setDisplayedMnemonic('U');
        valueLabel.setLabelFor(valueCombo);
        valueLabel.setBounds(X_Coordinate, Y_Coordinate, OFFSET, CustOpts.LBL_HEIGHT);
        valueCombo.setBounds(valueLabel.getX() + valueLabel.getWidth(), valueLabel.getY(), listWidth - X_Coordinate
                + CustOpts.HOR_GAP, conditionCombo.getPreferredSize().height);
        advancePanel.add(valueLabel);
        advancePanel.add(valueCombo);
        valueCombo.setEditable(true);
        valueTextDoc = ((JTextField) valueCombo.getEditor().getEditorComponent()).getDocument();
        // 保存列表框的尺寸和定位
        valueBounds =
                new Rectangle(X_Coordinate, Y_Coordinate, valueCombo.getPreferredSize().width,
                        valueCombo.getPreferredSize().height);
        // 返回选项卡组件
        return tmpTabbedPane;
    }

    /** 装配model */
    private void equipModel(
            int prmIndex) {
        if (prmIndex >= CustOpts.custOps.APPCapsVec.size()) {
            mutableLocation.setText(CustViewConsts.ALL_ITEMS);
            String[] RecommendColNames = { IntlModelConstants.CAPTION, IntlModelConstants.ATTACH };
            String[] tColNameAry =
                    { IntlModelConstants.CAPTION, IntlModelConstants.READED, IntlModelConstants.ICON,
                            IntlModelConstants.SIZE, IntlModelConstants.ATTACH, IntlModelConstants.ATTACHMENT,
                            IntlModelConstants.IMPORTANCE, IntlModelConstants.FLAGSTATUS, IntlModelConstants.ADDRESSER,
                            IntlModelConstants.RECIEVEDATE, IntlModelConstants.CATEGORY,
                            IntlModelConstants.FOLLOWUPFLAG, IntlModelConstants.FOLDERID, IntlModelConstants.DELETED,
                            IntlModelConstants.FOLLOWUPCOMPLETE, IntlModelConstants.FOLOWUPENDTIME,
                            IntlModelConstants.CONTACT };
            combModelRecommendCol = new DefaultComboBoxModel(RecommendColNames);
            combModelAllCol = new DefaultComboBoxModel(tColNameAry);
        } else {
            mutableLocation.setText(CustOpts.custOps.APPCapsVec.get(prmIndex));
            IApplication tApp = MainPane.getApp(CustOpts.custOps.APPNameVec.get(prmIndex));
            combModelRecommendCol = new DefaultComboBoxModel(tApp.getRecommendColAry());
            combModelAllCol = new DefaultComboBoxModel(tApp.getAppTexts());
        }
        comboRecommendCol.setModel(combModelRecommendCol);
        combAllCol.setModel(combModelAllCol);

        conditionComboModel = new DefaultComboBoxModel(CustViewConsts.NUM_CONDITION);
        conditionCombo.setModel(conditionComboModel); // 数字条件

        combAllCol.setSelectedIndex(0); // 设置选中的索引为0
        conditionCombo.setSelectedIndex(0); // 设置选中的索引为0
        comboRecommendCol.setSelectedIndex(0);

        filterPool.setFindPositionIndex(0);
        filterPool.setFieldIndex(0);
        filterPool.setConditionIndex(0);
    }

    /** 得到文本编译器的字符串对象 */
    private String getEditorStr(
            JComboBox prmCombo) {
        return String.valueOf(prmCombo.getEditor().getItem());
    }

    /** 重新初始化 */
    private void clear(
            boolean prmIsViewAdded) {
        if (prmIsViewAdded) { // 判断是否已经显示了查询的结果集Table
            tabPanel.remove(view); // 删除滚动窗口
            table = null; // 删除后赋空，否则再点搜索就不出
            tabPanel.remove(item); // 删除显示查找数量的label
            item.setBounds(0, 256, item.getPreferredSize().width, item.getPreferredSize().height);// label定位后再加入
            tabPanel.add(item);
            item.setText(CASUtility.SPACE);
            setSize(WIDTH, HEIGHT); // 对话盒高度改一下
        }

        equipAllComponents(viewInfo); // 装备所有有模型的组件的模型
        clearTextField(); // 清空联系人，类型
        conditionListModel.removeAllElements(); // 清空条件列表
        conditionList.setModel(conditionListModel);

        timeItemsCombo.setEnabled(false); // 默认设置
        conditionList.setEnabled(false); // 条件列表
        deleteBTN.setEnabled(false); // 设置使能状态

        filterPool.clear(); // 清除查找容器
    }

    /**
     * 清除文本框
     * 
     * @NOTE: 这几个组件在编辑时没有添加事件，所以无法知道其是否被修改
     */
    private void clearTextField() {
        receiveQuickInfo.setText(CASUtility.EMPTYSTR);
        senderQuickInfo.setText(CASUtility.EMPTYSTR);
        regimentationTextField.setText(CASUtility.EMPTYSTR);
    }

    /** 解析字符串 */
    private void setConditionModel(
            String prmTypeStr) {
        if (prmTypeStr == null)
            return;
        //
        int tmpEndPos = prmTypeStr.indexOf(",");
        String tmpSub = (tmpEndPos == -1) ? prmTypeStr.trim() : prmTypeStr.substring(0, tmpEndPos).trim();
        if (tmpSub.startsWith("VARCHAR") || tmpSub.startsWith("CHAR") || tmpSub.startsWith("LONGVARCHAR")) { // STRING
                                                                                                             // 类型
            conditionComboModel = new DefaultComboBoxModel(CustViewConsts.STR_CONDITION);
            conditionCombo.setModel(conditionComboModel);

            conditionType = CustViewConsts.STRING_TYPE; // 默认为VARCHAR类型
        } else if (tmpSub.startsWith("INTEGER") || tmpSub.startsWith("TINYINT") || tmpSub.startsWith("SMALLINT")
                || tmpSub.startsWith("FLOAT") || tmpSub.startsWith("DOUBLE")) { // 数值类型
            conditionComboModel = new DefaultComboBoxModel(CustViewConsts.NUM_CONDITION);
            conditionCombo.setModel(conditionComboModel);

            conditionType = CustViewConsts.NUM_TYPE; // 默认为INTEGER类型
        } else if (tmpSub.startsWith("BIT")) { // BOOLEAN 类型
            conditionComboModel = new DefaultComboBoxModel(CustViewConsts.BOOL_CONDITION);
            conditionCombo.setModel(conditionComboModel); // 设置条件模型

            valueComboModel = new DefaultComboBoxModel(CustViewConsts.BOOL_VALUE);
            // 实例化条件值（是、不是）
            valueCombo.setModel(valueComboModel); // 设置值的模型

            conditionType = CustViewConsts.BOOL_TYPE; // 设置为BIT类型
        } else if (tmpSub.startsWith("TIME") || tmpSub.startsWith("TIMESTAMP") || tmpSub.startsWith("DATE")) { // 时间类型
            conditionComboModel = new DefaultComboBoxModel(CustViewConsts.TIME_CONDITION);
            conditionCombo.setModel(conditionComboModel);
            // @NOTE : 如果fieldCombo的值为时间类型，则第三个valueCombo应该为不可编辑

            conditionType = CustViewConsts.TIME_TYPE; // 设置为TIME类型
        }
    }

    /** 设置使能状态 */
    private void setRelationState() {
        valueCombo.setEnabled(conditionType != CustViewConsts.TIME_TYPE);// @NOTE: 为时间类型时，使能状态为false
        if (!valueCombo.isEnabled()) // 判断使能状态为false时，设置选择的值为空,为了时间条件设置的
            valueCombo.setSelectedIndex(-1);

        boolean tIsEditor = (conditionType != CustViewConsts.BOOL_TYPE && !isSpec());// 是否可编辑
        addItemBTN.setEnabled(!tIsEditor || conditionType == CustViewConsts.TIME_TYPE);

        boolean tIsOldEditor = valueCombo.isEditable();// 如果为可以编辑状态则valueCombo的值为空，所以要设置使能状态为false
        if (tIsOldEditor != tIsEditor)
            valueCombo.setEditable(tIsEditor);

        if (tIsEditor) {
            if (tIsOldEditor) // 判断原来是否可编辑,如果原来为可以编辑则删除原来注册的监听器
                valueTextDoc.removeDocumentListener(this);
            else {
                valueComboModel = (DefaultComboBoxModel) valueCombo.getModel();
                valueComboModel.removeAllElements(); // 删除原来model中的所有的元素
            }
            JTextField tEditor = (JTextField) valueCombo.getEditor().getEditorComponent();
            tEditor.setText("");
            valueTextDoc = tEditor.getDocument();
            valueTextDoc.addDocumentListener(this);
        } else {
            valueComboModel = new DefaultComboBoxModel(CustViewConsts.BOOL_VALUE);
            valueCombo.setModel(valueComboModel); // 如果不是编辑则为BOOL值的MODEL
        }
    }

    /** 设置valueCombo的使能状态 */
    private void setValueState(
            Object prmSelCondi) {
        valueCombo.setEnabled(!prmSelCondi.equals(CustViewConsts.CON_EXIST) // 存在
                && !prmSelCondi.equals(CustViewConsts.CON_NO_EXIST) // 不存在
                && !prmSelCondi.equals(CustViewConsts.STR_CON_NULL) // 为空
                && !prmSelCondi.equals(CustViewConsts.STR_CON_NONENULL) // 不为空
                && conditionType != CustViewConsts.TIME_TYPE); // 设置模型

        if (conditionType == CustViewConsts.TIME_TYPE // 时间类型下的特殊处理
                && (prmSelCondi.equals(CustViewConsts.CON_IN) // 在
                        || prmSelCondi.equals(CustViewConsts.CON_BETWEEN)
                        || prmSelCondi.equals(CustViewConsts.TIME_CON_NOEARLY) || prmSelCondi
                            .equals(CustViewConsts.TIME_CON_NOLATE))) {
            valueCombo.setEnabled(true);
        }
        if (!valueCombo.isEnabled()) {
            addItemBTN.setEnabled(true);
        } else
        // 可编辑且已经有值的情况
        {
            boolean isEditor = valueCombo.isEditable();

            addItemBTN.setEnabled((isEditor && getEditorStr(valueCombo).length() > 0) || !isEditor);
        }
    }

    /** 对valueCombo输入的类型或者格式匹配的处理 */
    private void processCast(
            Object prmSelValue) throws NumberFormatException, ParseException {
        if (prmSelValue.equals(CASUtility.EMPTYSTR)) // @NOTE : 说明valueCombo的使能状态为false
            return;

        if (conditionType == CustViewConsts.NUM_TYPE) // 数字类型
            Integer.parseInt(prmSelValue.toString());
        else if (conditionType == CustViewConsts.TIME_TYPE) { // 时间类型
            DateFormat format = DateFormat.getDateInstance();
            format.parse(prmSelValue.toString());
        }
    }

    /** 计算字符串显示时的长度，双字节字符占两格 */
    private int getNeededLength(
            String prmString) {
        final int tmpLength = prmString.length();
        int tmpActualLen = tmpLength;
        for (int i = tmpLength - 1; i >= 0; i--)
            if (prmString.charAt(i) > 127)
                tmpActualLen++;
        return tmpActualLen;
    }

    /** 设置在conditionList中显示的字段的宽度和条件宽度的设置 */
    private String buildStr(
            String prmStr,
            int prmLen) {
        int tmpActualLen = getNeededLength(prmStr);
        StringBuffer tmpCon = new StringBuffer();
        tmpCon.append(prmStr);
        for (int i = Math.max(prmLen, tmpActualLen) + 5; i >= tmpActualLen; i--)
            tmpCon.append(CASUtility.SPACE);
        return tmpCon.toString();
    }

    /** 组合条件语句 */
    private String composeConditionStr(
            Object prmField,
            Object prmCondi,
            Object prmValue) {
        StringBuffer tmpBuf = new StringBuffer();
        tmpBuf.append(buildStr(prmField.toString(), CustViewConsts.MAX_FIELD_INTERVAL));
        tmpBuf.append(buildStr(prmCondi.toString(), CustViewConsts.MAX_CONDI_INTERVAL));
        tmpBuf.append(prmValue.toString());
        return tmpBuf.toString();
    }

    /** 是否是特殊的字段 */
    private boolean isSpec() {
        return false;
    }

    /**
     * NOTE: 此方法用于初始化查找对话盒时使用,最后的结构将修改为使用此方法的格式
     */
    // /** 组建的状态
    // */
    // private void initState()
    // {
    // valueCombo.setEditable(true);
    // findTextCombo.setEditable(true);
    // locationCombo.setSelectedIndex(0);
    // timeSeleCombo.setSelectedIndex(0);
    // fieldCombo.setSelectedIndex(0);
    // conditionCombo.setSelectedIndex(0);
    // valueCombo.setSelectedIndex(-1);
    // findTextCombo.setSelectedIndex(-1);
    // }
    /** 判断当前的选项有无设置 */
    private boolean isNoWorkDone() {
        return findTextCombo.isEditable() && getEditorStr(findTextCombo).length() < 1
                && receiveQuickInfo.getText().length() < 1 && senderQuickInfo.getText().length() < 1
                && regimentationTextField.getText().length() < 1
                && comboRecommendCol.getSelectedIndex() == 0
                // && timeItemsCombo.getSelectedIndex() == -1
                && timeSeleCombo.getSelectedIndex() == 0 && combAllCol.getSelectedIndex() == 0
                && conditionCombo.getSelectedIndex() == 0 && valueCombo.isEditable()
                && getEditorStr(valueCombo).length() < 1 && conditionListModel.getSize() < 1;
    }

    private Dialog parentDialog; // 保存父对话盒的引用
    private PIMViewInfo viewInfo; // 保存当前视图信息的引用
    private boolean modified; // 保存修改标志,以便父对话盒作相应措施
    private static final int WIDTH = 500; // 保存本对话盒的尺寸
    private static final int HEIGHT = 300;
    private static final int OHEIGHT = 451; // 对话盒另一高度值
    private static final int Y_OFFSET = 5; // 布局用的一个变量
    private static final int OFFSET = -1; // 构建列表框用的一个常量
    private int btnWidth;
    private int btnWidthInTab;
    private JButton searchBTN; // 右上方四个按钮
    private JButton browseBTN;
    private JButton ok;
    private JButton cancel;
    private JCheckBox includeDeled; // 是否包涵回收站中的项目.
    private JComboBox combSeleApp; // 打开搜索条件按钮
    private JTabbedPane tabbedPane; // 保存搜索条件按钮
    private JButton clearAllBTN; // 全部清除按钮
    private JLabel labSeleApp;
    private JLabel locationLabel;
    private JTextField mutableLocation; // 位置内容

    // 下面是普通选项卡中的组件***************************************************
    private JPanel normalPanel; // 创建出普通面板的实例
    private JPanel advancePanel; // 创建出高级面板的实例
    private JLabel searchTextLabel; // 查找文字标签
    private JLabel locationLabel2; // 位置标签
    private PIMSeparator sperator1; // 设置分隔线
    private PIMSeparator sperator2; // 设置分隔线
    private JLabel timeLabel;
    private JLabel fieldLabel; // 创建字段下拉框的标签
    private JLabel conditionLabel; // 创建条件下拉框的标签
    private JLabel valueLabel; // 创建值下拉框的标签
    private JLabel topLabel; // 最上面那个标签

    private JComboBox findTextCombo; // 查找文字标签下拉框
    private DefaultComboBoxModel findTextComboModel;// 查找文字标签下拉框model
    private JComboBox comboRecommendCol; // 位置下拉框
    private DefaultComboBoxModel combModelRecommendCol; // 位置下拉框的model
    private JButton senderBTN; // 发件人按钮
    private QuickInfoField senderQuickInfo; // 收件人文本框
    private JButton receiverBTN; // 收件人按钮
    private QuickInfoField receiveQuickInfo; // 收件人文本框
    private JButton categoryBTN; // 类别按钮
    private JTextField regimentationTextField; // 类别文本框
    private JComboBox timeSeleCombo; // 设置时间类别下拉框
    private JComboBox timeItemsCombo; // 设置时间选项下拉框
    // 下面是普通选项卡中的组件***************************************************
    private JList conditionList; // 列表框
    private DefaultListModel conditionListModel; // 列表模型
    private Rectangle conditionBounds; // 保存的定位
    private JButton deleteBTN; // 列表框下面的删除按钮
    private JButton addItemBTN; // 添至列表按钮
    private Document valueTextDoc;
    private JComboBox combAllCol; // 字段下拉框
    private DefaultComboBoxModel combModelAllCol; // 字段下拉框MODEL
    private JComboBox conditionCombo; // 条件下拉框
    private DefaultComboBoxModel conditionComboModel; // 条件下拉框MODEL
    private JComboBox valueCombo; // 值下拉框
    private DefaultComboBoxModel valueComboModel; // 值的model
    private Rectangle valueBounds; // 保存的定位
    private PIMTable table;
    private View_PIMDetails view;
    private JPanel tabPanel;
    private JLabel item;
    private Vector items;
    private JTextField findEditor;
    private int conditionType; // 条件的类型
    private FilterInfo filterPool;
    private int app;
    private FilterResultApplication filterApplication;
    private boolean isForFind;
}
