package org.cas.client.platform.pimview;

import java.awt.Container;
import java.awt.event.FocusEvent;
import java.util.Vector;

import javax.swing.Icon;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.calendar.DateSeleAreaPane;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.EDate;
import org.cas.client.platform.casutil.EDaySet;
import org.cas.client.platform.casutil.Releasable;

/**
 * TextView中的主要组件为two TitlePanes and two DetailPane from the corresponding Dialogs,
 * 他们俩都维护一个Record的引用,并在各自失去焦点而且内容发生改变时改变Record相应字段的内容并将Record更新到数据库中.
 * 该视图因为需要将插件应用的对话盒面板显示在界面上，而且需要根据Record更新上面的内容。所以维护了两个IPIMDialog的引用。
 */
public class View_PIMFilmstrip extends AbstractPIMView implements Releasable {
    public View_PIMFilmstrip() {
        titlePane = new CommonTitle(0);
        titlePane2 = new CommonTitle(0);
        titlePane.setAlignmentX(0);
        titlePane2.setAlignmentX(0);
        add(titlePane, -1);
        add(titlePane2, -1);
    }

    /** 初始化 */
    public void init(
            boolean prmAppChanged) {
        if (getApplication() == null) // 如果View还没有附上任何一个应用，则不进行初始化。
            return;

        dialog1 = getApplication().getADialog();// Dialog和container不能象TalbeView那样在构造时就进行初始化，因为DetailView必须在
        dialog2 = getApplication().getADialog();// 知道自己被嫁给谁之后，才知道该显示那个插件应用的对话盒面板。
        container1 = dialog1.getContainer();
        container2 = dialog2.getContainer();
        add(container1, -1);
        add(container2, -1);

        contends = CASControl.ctrl.getModel().selectRecords(currentViewInfo); // 得到准备要显示的内容，TODO：要么对该方法返回的数组做尺寸限制，要么尝试改到绘制的时候再显示。
    }

    // 实现IView的接口-------------------------------------------------------------
    public void setIconAndTitle(
            Icon prmIcon,
            String prmTitle) {
        titlePane.setPaneTitle(prmIcon, prmTitle);
        titlePane2.setPaneTitle(prmIcon, prmTitle);
    }

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的ID的方法
     * 
     * @return 所有选中的记录的ID
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public int[] getSelectedRecordIDs() {
        return null;
        // if(contentContainer1.hasFocus())
        // return contentContainer1.getRecord();
        // else
        // return contentContainer2.getRecord();
    }

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的方法
     * 
     * @return 所有选中的记录
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public Vector getSelectedRecords() {
        Vector vector = new Vector();
        // vector.addElement(contentContainer1.getRecord());
        // vector.addElement(contentContainer2.getRecord());
        return vector;
    }

    /**
     * 应控制的要求,需要有选取视图中的所有记录的方法
     * 
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public void seleteAllRecords() {
    }

    /**
     * 本方法在视图更新和字体对话盒操作后调用,以处理所有字体和前景色
     */
    public void updateFontsAndColor() {
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
    }

    /**
     * 本方法用于设置View上各个组件的尺寸。
     */
    public void setBounds(
            int prmX,
            int prmY,
            int prmWidth,
            int prmHeight) {
        super.setBounds(prmX, prmY, prmWidth, prmHeight);
        if (CustOpts.custOps.isBaseBookHide())// 非Book风格。
        {
            if (CustOpts.custOps.isPreviewShown()) {
                int tmpPos = CustOpts.custOps.getSplitHeight();
                if (CustOpts.custOps.isViewTopAndDown()) {
                    container1.setBounds(0, titlePaneHeight, prmWidth, tmpPos - titlePaneHeight);
                    container2.setBounds(0, tmpPos + MainPane.getDividerSize(), prmWidth,
                            prmHeight - tmpPos - MainPane.getDividerSize());
                } else {
                    container1.setBounds(0, titlePaneHeight, tmpPos, prmHeight - titlePaneHeight);
                    container2.setBounds(tmpPos + MainPane.getDividerSize(), titlePaneHeight, prmWidth - tmpPos
                            - MainPane.getDividerSize(), prmHeight - titlePaneHeight);
                }
                titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                titlePane2.setBounds(0, 0, 0, 0);
            } else {
                titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                container1.setBounds(0, titlePaneHeight, prmWidth, prmHeight - titlePaneHeight);
                titlePane2.setBounds(0, 0, 0, 0);
                container2.setBounds(0, 0, 0, 0);
            }
        } else // Book风格。
        {
            int tmpPos = CustOpts.custOps.getSplitHeight();
            if (CustOpts.custOps.isPreviewShown()) {
                if (CustOpts.custOps.isViewTopAndDown()) {
                    titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                    container1.setBounds(0, titlePaneHeight, prmWidth, tmpPos - titlePaneHeight);

                    titlePane2.setBounds(0, tmpPos + MainPane.getDividerSize(), prmWidth, titlePaneHeight);
                    container2.setBounds(0, tmpPos + MainPane.getDividerSize() + titlePaneHeight, prmWidth, prmHeight
                            - tmpPos - MainPane.getDividerSize() - titlePaneHeight);
                } else {
                    titlePane.setBounds(0, 0, tmpPos, titlePaneHeight);
                    container1.setBounds(0, titlePaneHeight, tmpPos, prmHeight - titlePaneHeight);

                    titlePane2.setBounds(tmpPos + MainPane.getDividerSize(), 0,
                            prmWidth - tmpPos - MainPane.getDividerSize(), titlePaneHeight);
                    container2.setBounds(tmpPos + MainPane.getDividerSize(), titlePaneHeight, prmWidth - tmpPos
                            - MainPane.getDividerSize(), prmHeight - titlePaneHeight);
                }
            } else// Book风格，无预览视图
            {
                titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                container1.setBounds(0, titlePaneHeight, prmWidth, prmHeight - titlePaneHeight);
                titlePane2.setBounds(0, 0, 0, 0);
                container2.setBounds(0, 0, 0, 0);
            }
        }
    }

    /**
     * 自我更新一下
     */
    public void updatePIMUI() {
    }

    /** 更新Model */
    public void viewToModel() {
    }

    /**
     * called by PIMApplication,when 书边上被点击时,调此方法:对Table视图实现上下翻页,对文本视图实现按日期翻页,卡片 视图实现左右翻页.
     */
    public void setPageBack(
            boolean prmIsBack) {
        int[] date = DateSeleAreaPane.getInstance().getLastDate();
        EDate tmpDate = new EDate(date[0], date[1], date[2]);
        if (prmIsBack) {
            tmpDate = tmpDate.getLastDate();

        } else {
            tmpDate = tmpDate.getNextDate();

        }
        EDate[] dates = new EDate[] { tmpDate };
        EDaySet dayset = new EDaySet(dates);
        DateSeleAreaPane.getInstance().setDaySet(dayset);
        // 改变日期选择区内的日期,并触发事件监听器
    }

    /**
     */
    public void closed() {
        focusLost(null);
    }

    // 实现FocusLisener的接口----------------------------------------------------
    /**
     * Invoked when a component gains the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusGained(
            FocusEvent e) {
        // // if (e.getSource() == textPane)
        // // {
        // // //日记有多个文件夹时，日记页面视图的切换
        // // IModel tmpModel = PIMControl.ctrl.getModel();
        // // int tmpAppType = PIMUtility.getAppType(PIMUtility
        // // .getActiveTreePath(getID()).toString());
        // // String tmpPath = PIMUtility.getActiveTreePath(getID()).toString();
        // // int subType = CustomOptions.custOps.getSubViewIndex(
        // // tmpPath);
        // // PIMViewInfo viewInfo = PIMControl.ctrl.getViewInfo(
        // // tmpAppType, subType, tmpPath);
        // // if (viewInfo != null) //判断当前表是否存在
        // // // TODO:结点被删掉以后，viewInfo好像没有被删掉，这时此判断就无效了
        // // {
        // // curRecord = tmpModel.selectRecord(ModelConstants.DIARY_APP,
        // // getID(), PIMUtility.getActiveTreePath(getID())
        // // .toString());
        // // }
        // // String currentPath = CustomOptions.custOps
        // // .getActiveFolderPath();
        // // String activePath = PIMUtility.getActiveTreePath(getID())
        // // .toString();
        // // String diaryPath = PIMUtility.getTreePath(ModelConstants.DIARY_APP);
        // // EFolderTree folderTree = PIMControl.ctrl.getFolderTree();
        // // //boolean isModifiable =
        // // // ((EFolderNode)folderTree.getSelectNode()).isModifiable();//TODO:
        // // // 当前增加一个结点并设置为不可重命名时，第二次启动后改结点的属性没保存
        // // String nodeName = ((EFolderNode)folderTree.getTree()
        // // .getSelectionPath().getLastPathComponent()).getName();
        // // if (curRecord != null)
        // // {
        // // if (PIMUtility.isSystemNode(nodeName)
        // // && !(currentPath.equals(diaryPath))) //如果是系统定义的结点
        // // {
        // // if (curRecord.getFieldValues() == null)
        // // {
        // // tmpPath = PIMUtility
        // // .getTreePath(ModelConstants.DIARY_APP);
        // // PIMControl.ctrl.changeApplication(
        // // ModelConstants.DIARY_APP,
        // // CustomOptions.custOps
        // // .getSubViewIndex(tmpPath), tmpPath);
        // // }
        // // else if (!(currentPath.equals(activePath))) //如果当前选中的结点不是当前活动的结点
        // // {
        // // int tmpSeleType = PIMUtility.getAppType(activePath);
        // // PIMControl.ctrl
        // // .changeApplication(
        // // tmpSeleType,
        // // CustomOptions.custOps
        // // .getSubViewIndex(
        // // activePath.toString()),
        // // activePath.toString());
        // // }
        // // }
        // // if (curRecord.getFieldValues() != null
        // // && currentPath.equals(diaryPath))
        // // {
        // // int tmpSeleApp = PIMUtility.getAppType(activePath);
        // // PIMControl.ctrl.changeApplication(
        // // tmpSeleApp,
        // // CustomOptions.custOps.getSubViewIndex(
        // // activePath.toString()),
        // // activePath.toString());
        // // }
        // // }
        // // else if (!(currentPath.equals(diaryPath)))
        // // {
        // // String prmPath = PIMUtility
        // // .getTreePath(ModelConstants.DIARY_APP);
        // // PIMControl.ctrl.changeApplication(
        // // ModelConstants.DIARY_APP,
        // // CustomOptions.custOps.getSubViewIndex(
        // // prmPath), prmPath);
        // // }
        // // }
        // if (e.getSource() == contentContainer1 || e.getSource() == contentContainer2)
        // {
        // focusComp = (PIMTextPane)e.getSource(); //为当前作用的面板赋值
        // int tmpID = getID();
        // EDate tmpDate = PIMUtility.IDToDate(tmpID);
        // int[] date = ((PIMSplitPane)
        // ((PIMMainFrame)PIMControl.ctrl.getPIMFrame()).getSplitPane()).getDateSelectAreaPane().getLastDate();
        // EDate tmpSeleDate = new EDate(date[0], date[1], date[2]);
        // int seleID = Integer.parseInt(PIMUtility.getDateString(tmpSeleDate));
        // int currentID = Integer.parseInt(PIMUtility.getDateString(tmpSeleDate.getLastDate()));
        // int todayID = Integer.parseInt(PIMUtility.getDateString(tmpSeleDate.getNextDate()));
        // // if (application instanceof PimApplicationPane)
        // {
        // // PIMTextView currentView = (PIMTextView) ((PimApplicationPane)application).getCurrentView();
        // // PIMTextView currentView = (PIMTextView)((AbstractApp)getApplication()).getCurrentView();//
        // // PIMTextView todayView = (PIMTextView)((App_Diary)getApplication()).getTodayView();
        //
        // // currentView.
        // if (getID() == seleID && tmpID == todayID && tmpID % 2 == 0)
        // {
        // EDate[] dates = new EDate[] { tmpSeleDate.getNextDate()};
        // EDaySet dayset = new EDaySet(dates);
        // ((PIMSplitPane)
        // ((PIMMainFrame)PIMControl.ctrl.getPIMFrame()).getSplitPane()).getDateSelectAreaPane().setDaySet(dayset);
        // }//todayView
        // else if (tmpID == currentID && tmpID % 2 != 0)
        // {
        // if (PIMUtility.getTodayDateInt() == seleID)
        // {
        // EDate[] dates = new EDate[] { tmpSeleDate.getLastDate()};
        // EDaySet dayset = new EDaySet(dates);
        // ((PIMSplitPane)
        // ((PIMMainFrame)PIMControl.ctrl.getPIMFrame()).getSplitPane()).getDateSelectAreaPane().setDaySet(dayset);
        // }
        // }
        // }
        // }
    }

    /**
     * Invoked when a component loses the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusLost(
            FocusEvent e) {
        // // if (application instanceof PimApplicationPane)
        // // {
        // // IModel tmpModel = PIMControl.ctrl.getModel();
        // // PIMRecord tmpRecordInModel = null;
        // // PIMRecord tmpRecordInView = getRecord();
        // //
        // // int tmpRecID = getID();
        // // if
        // (CustomOptions.custOps.getActiveFolderPath().equals(PIMUtility.getActiveTreePath(tmpRecID).toString())//系统定义的文件夹
        // // ||
        // CustomOptions.custOps.getActiveFolderPath().equals(PIMUtility.getTreePath(ModelConstants.DIARY_APP)))//日记文件夹
        // // {
        // // tmpRecordInModel = tmpModel.selectRecord(ModelConstants.DIARY_APP, tmpRecID,
        // PIMUtility.getActiveTreePath(tmpRecID).toString());
        // // if (tmpRecordInModel == null || tmpRecordInModel.getFieldValues() == null)
        // // {
        // // tmpRecordInModel = tmpModel.selectRecord(ModelConstants.DIARY_APP, tmpRecID,
        // PIMUtility.getTreePath(ModelConstants.DIARY_APP));
        // // }
        // // }
        // // else//如果是用户自己定义的文件夹
        // // {
        // // tmpRecordInModel = tmpModel.selectRecord(ModelConstants.DIARY_APP, tmpRecID,
        // CustomOptions.custOps.getActiveFolderPath());
        // // }
        // //
        // // //-----------------------------------
        // // if (tmpRecordInModel.getFieldValues() == null) //为空则插入一个新的record.
        // // {
        // // if (tmpRecordInView != null)
        // // {
        // // if
        // (!(tmpRecordInView.getFieldValue(ModelDBConstants.CAPTION).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.CAPTION))
        // //如果当前视图中的内容和model中记录的内容一样,就不必存盘了,省一次刷新.
        // // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.COMMENT).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.COMMENT))
        // // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.DIARY_WEATHER).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.DIARY_WEATHER))
        // // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.DIARY_MOOD).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.DIARY_MOOD))
        // // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.ID).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.ID))))
        // // {
        // // //TODO:因为一日之计上也有日记,所以不能这样加路径,以后视规格发展决定怎么做法.
        // // //tmpRecord.setFieldValue(ModelDBConstants.INFOLDER,CustomOptions.custOps.getActiveFolderPath());
        // // tmpModel.insertRecord(tmpRecordInView, false);
        // // }
        // // }
        // // }
        // // else //不为空则更新record.
        // // {
        // // if (tmpRecordInView != null)
        // // {
        // // boolean equ = false;
        // // equ = tmpRecordInView.getFieldValue(
        // // ModelDBConstants.CAPTION).equals(
        // // tmpRecordInModel
        // // .getFieldValue(ModelDBConstants.CAPTION))
        // //
        // // && tmpRecordInView
        // // .getFieldValue(ModelDBConstants.COMMENT)
        // // .equals(tmpRecordInModel
        // // .getFieldValue(ModelDBConstants.COMMENT))
        // // && tmpRecordInView
        // // .getFieldValue(ModelDBConstants.DIARY_WEATHER)
        // // .equals(tmpRecordInModel
        // // .getFieldValue(ModelDBConstants.DIARY_WEATHER))
        // //
        // // && tmpRecordInView
        // // .getFieldValue(ModelDBConstants.DIARY_MOOD)
        // // .equals(tmpRecordInModel
        // // .getFieldValue(ModelDBConstants.DIARY_MOOD))
        // //
        // // && tmpRecordInView.getFieldValue(ModelDBConstants.ID)
        // // .equals(tmpRecordInModel
        // // .getFieldValue(ModelDBConstants.ID));
        // //
        // // A.s("is equ === " + equ);
        // // if (!equ)
        // // {
        // // tmpModel.updateRecord(tmpRecordInView, false); //不必更新视图
        // // }
        // // }
        // // }
        // // }
        //
        // // if (application instanceof PimApplicationPane || (application instanceof VirtualApplication))
        // //@TODO:目前刚加监听器时,尚未赋予Application.
        // {
        // IPIMModel tmpModel = PIMControl.ctrl.getModel();
        // int id = getID();
        // PIMRecord tmpRecordInModel = tmpModel.selectRecord(ModelConstants.DIARY_APP, getID(),
        // PIMUtility.getTreePath(ModelConstants.DIARY_APP));
        //
        // if (tmpRecordInModel.getFieldValues() == null) //为空则插入一个新的record.
        // {
        // PIMRecord tmpRecordInView = getRecord();
        // if
        // (!(tmpRecordInView.getFieldValue(ModelDBConstants.CAPTION).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.CAPTION))
        // //如果当前视图中的内容和model中记录的内容一样,就不必存盘了,省一次刷新.
        // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.COMMENT).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.COMMENT))
        // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.DIARY_WEATHER).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.DIARY_WEATHER))
        // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.DIARY_MOOD).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.DIARY_MOOD))
        // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.ID).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.ID))))
        // {
        // //TODO:因为一日之计上也有日记,所以不能这样加路径,以后视规格发展决定怎么做法.
        // //tmpRecord.setFieldValue(ModelDBConstants.INFOLDER,CustomOptions.custOps.getActiveFolderPath());
        // tmpModel.insertRecord(tmpRecordInView, false); //不必更新视图
        // }
        // }
        // else //不为空则更新record.
        // {
        // PIMRecord tmpRecordInView = getRecord();
        // if
        // (!(tmpRecordInView.getFieldValue(ModelDBConstants.CAPTION).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.CAPTION))
        // //如果当前视图中的内容和model中记录的内容一样,就不必存盘了,省一次刷新.
        // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.COMMENT).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.COMMENT))
        // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.DIARY_WEATHER).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.DIARY_WEATHER))
        // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.DIARY_MOOD).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.DIARY_MOOD))
        // &&
        // tmpRecordInView.getFieldValue(ModelDBConstants.ID).equals(tmpRecordInModel.getFieldValue(ModelDBConstants.ID))))
        // {
        // tmpModel.updateRecord(tmpRecordInView, false); //不必更新视图
        // }
        // }
        // }
        //
        // if (e == null)
        // {
        // if (focusComp == textPane)
        // {
        // focusComp = textPane2;
        // }
        // else
        // {
        // focusComp = textPane;
        // }
        // }
    }

    //
    // public void setDiaryDate(int diaryDate)
    // {
    // if (headPanel instanceof PIMDiaryLabel)
    // {
    // ((PIMDiaryLabel)headPanel).setChangedDate(diaryDate);
    // }
    // }

    // /** 组件初始化并加入面板
    // */
    // private void initComponent()
    // {
    // //首先清空当前View上的所有东西。
    // splitPane.removePropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY,
    // this);
    // removeAll();
    //
    // //TODO: 这一句是临时处理的,以后模型事件完善后要去除.
    // dividerScale = currentViewInfo.getPreviewScale();
    //
    // //创建标题面板
    // titlePane.setPaneTitle(CustomOptions.custOps.getDiaryIcon(false),
    // PaneConstant.DIARY);
    //
    // //按照视图风格类型创建具体视图。
    // activeView = getView(currentViewInfo.getViewType());
    // activeView.setApplication(this);
    //
    // //如果是那种两页纸左右对开，中间露出夹子的视图，则需要再增加一个TitlePane和一个TextPane(放在左侧的)。
    // //@TODO: 日记应用的文本视图中当焦点离开一个文本视图时,该文本视图中的内容要组装成一个Record存入model.
    // //焦点离开时存盘,将作为文本组件的一项可选择的功能,即需要提供方法,以允许客户定制.
    // if (currentViewInfo.getViewType() == ModelConstants.TEXT_VIEW)
    // {
    //
    // if (titlePane2 == null)
    // {
    // titlePane2 = new PIMTitlePane();
    // titlePane2.setPaneTitle(
    // CustomOptions.custOps.getDiaryIcon(false),
    // PaneConstant.DIARY);
    // titlePane2.setAlignmentX(0);
    // }
    //
    // if (toDayView == null)
    // {
    // toDayView = new PIMTextView();
    // }
    // toDayView.setApplication(this);
    //
    // if (currentViewInfo.hasPreview())
    // {
    // dividerScale = currentViewInfo.getPreviewScale();
    // if (topComp == null)
    // {
    // topComp = new JPanel();
    // topComp.setLayout(null);
    // topComp.setOpaque(false);
    // }
    // topComp.add(titlePane);
    // topComp.add(activeView.getContainer());
    //
    // topComp.add(titlePane2);
    // topComp.add(toDayView.getContainer());
    //
    // splitPane.setTopComponent(topComp);
    // splitPane.setBottomComponent(PIMPreviewPane.getInstance());
    //
    // add(splitPane);
    // splitPane.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY,
    // this);
    // add(titlePane);
    // revalidate();
    // }
    // else
    // {
    // if (topComp != null)
    // {
    // topComp.removeAll();
    // }
    // //添加到容器上
    // add(titlePane);
    // add(activeView.getContainer());
    //
    // //2是右边的,一般用来写今天的日记
    // add(titlePane2);
    // add(toDayView.getContainer());
    // add(titlePane);
    // layoutPane();
    // }
    // }
    // else if (currentViewInfo.getViewType() == ModelConstants.TABLE_VIEW)
    // {
    // //添加到容器上
    // if (currentViewInfo.hasPreview())
    // {
    // dividerScale = currentViewInfo.getPreviewScale();
    // splitPane.setTopComponent(activeView.getContainer());
    // //得到视图上的第一个记录
    // PIMRecord tmpRec;
    // String path = null;
    // if (activeView.getSelectedRecords().size() <= 0)
    // {
    // tmpRec = null;
    // }
    // else
    // {
    // tmpRec = (PIMRecord)activeView.getSelectedRecords().get(0);
    // path =
    // PIMUtility.getPIMMailDirPath().concat(
    // tmpRec.getFieldValue(ModelDBConstants.ID).toString()
    // + '_'
    // + Integer.toString(ModelConstants.INBOX_APP)
    // + '&');
    // }
    //
    // //TODO: 目前预览面板只能很好地支持邮件类,完善后要将传入参数修正.
    // splitPane.setBottomComponent(PIMPreviewPane.getInstance());
    // add(splitPane);
    // splitPane.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY,
    // this);
    // add(titlePane);
    // revalidate();
    // }
    // else
    // {
    // //不显示预览面板时就只加滚动面板
    // add(activeView.getContainer());
    // add(titlePane);
    // layoutPane();
    // }
    //
    // }
    // }
    //
    // /** 布局面板
    // * 该方法由子类实现
    // * 抽象方法
    // */
    // protected void layoutPane()
    // {
    // if (currentViewInfo.getViewType() == ModelConstants.TEXT_VIEW)
    // {
    //
    // }
    // else
    // {
    // if (currentViewInfo.hasPreview())
    // {
    // splitPane.setBounds(0, 20, getWidth(), getHeight() - 20);
    // titlePane.setBounds(0, 0, getWidth(), 20);
    // activeView.updateUIStyle();
    // if (dividerLocation <= 0)
    // {
    // splitPane.setDividerLocation(dividerScale * 0.01);
    // }
    // else
    // {
    // splitPane.setDividerLocation(dividerLocation);
    // }
    // dividerLocation =
    // splitPane.getDividerLocation() < 0 ? dividerLocation :
    // splitPane.getDividerLocation();
    //
    // }
    // else
    // {
    // activeView.getContainer().setBounds(0, 20, getWidth(), getHeight() - 20);
    // titlePane.setBounds(0, 0, getWidth(), 20);
    // activeView.updateUIStyle();
    // }
    // }
    // }

    // ///////////////////////////////////////////
    // PIMDiaryLabel.dateChanged() need modify//
    // //////////////////////////////////////////

    /**
     * 日记表格视图专用
     * 
     * @called by:DiaryPane;
     * @param record
     *            一条日记记录
     */
    // public void setSelectedRecord(PIMRecord record)
    // {
    // Hashtable tmpHash = record.getFieldValues();
    // if (tmpHash != null) //如果库中已有记录。
    // {
    // String tmpContent = (String)tmpHash.get(pool
    // .getIntegerKey(ModelDBConstants.COMMENT));
    // textPane.setText(tmpContent);
    // int tmpCaretPos = textPane.getCaretPosition();
    // if (tmpCaretPos > -1 && tmpCaretPos < tmpContent.length())
    // {
    // textPane.setCaretPosition(tmpCaretPos);
    // }
    // ((PIMDiaryLabel)headPanel).setRecord(record);
    // }
    // }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等结构中数据的移除和释放、 视图中UI的卸载等
     * 
     */
    public void release() {
        removeAll();
    }

    //
    // public PIMTitlePane getTodayTitlePane()
    // {
    // if (headPanel.getID() == today_id)
    // {
    // return titlePane;
    // }
    // else
    // {
    // return titlePane2;
    // }
    // }

    /**
     * @called by PIMDiaryLabel
     * @return titlePane PIMTitlePane
     */
    public CommonTitle getTitlePane() {
        if (dialog1.getContainer().hasFocus()) {
            return titlePane;
        } else {
            return titlePane2;
        }
    }

    private Object[][] contends;
    private ICASDialog dialog1;
    private ICASDialog dialog2;
    private Container container1;
    private Container container2;
    private CommonTitle titlePane, titlePane2;// 标题面板
    boolean is_left = true;
    public static int titlePaneHeight = 20;// 默认标题高度
}
