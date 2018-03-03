package org.cas.client.platform.cascontrol;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cas.client.platform.cascontrol.menuaction.DeleteAction;
import org.cas.client.platform.cascontrol.menuaction.OpenAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.contact.action.NewContactAction;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.PicturePane;
import org.cas.client.platform.pimview.View_PIMDetails;
import org.cas.client.platform.pimview.View_PIMFilmstrip;
import org.cas.client.platform.pimview.View_PIMIcons;
import org.cas.client.platform.pimview.View_PIMThumbnails;
import org.cas.client.resource.international.PopupMenuConstant;

/** TODO:由于一个应用可以使用不同风格的视图 */
public abstract class AbstractApp extends PicturePane implements IApplication {
    /**
     * @param prmImag
     *            面板上的图像
     */
    public AbstractApp(Image prmImag) {
        super(prmImag);
    }

    // 对视图样式信息和视图数据信息的处理---------------------------------------
    /**
     * 返回当前视图信息
     * 
     * @return PIMViewInfo
     */
    public PIMViewInfo getActiveViewInfo() {
        return currentViewInfo;
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
            Component prmComp,
            int x,
            int y) {
        JPopupMenu tmpPopMenu = new JPopupMenu();
        tmpPopMenu.removeAll();
        JMenuItem tmpMenuItem = new JMenuItem(PopupMenuConstant.CONTACT_NEWCONTACT, 'N');
        tmpMenuItem.setPreferredSize(new Dimension(100, CustOpts.LBL_HEIGHT));
        tmpMenuItem.addActionListener(new NewContactAction(null));
        tmpPopMenu.add(tmpMenuItem);// 新建
        tmpMenuItem = new JMenuItem(PopupMenuConstant.OPEN_SELECTED_ITEM, 'O');
        tmpMenuItem.addActionListener(new OpenAction());
        tmpPopMenu.add(tmpMenuItem);// 打开
        tmpPopMenu.addSeparator();
        tmpMenuItem = new JMenuItem(PopupMenuConstant.DELETE, 'D');
        tmpMenuItem.addActionListener(new DeleteAction());
        tmpPopMenu.add(tmpMenuItem);// 删除
        tmpPopMenu.show(prmComp, x, y);
    }

    // 鼠标点击事件的处理------------------------------------------------------
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
    }

    /**
     * 根据上下文棉板在鼠标单击时要行的动作: 显示预览
     * 
     * @param comp
     *            鼠标点击的组件
     */
    public void processMouseClickAction(
            Component comp) {
    }

    /**
     * 改变视图
     * 
     * @param contentsV
     *            表格数据
     * @param newViewInfo
     *            视图信息
     */
    public void refreshView(
            PIMViewInfo prmNewViewInfo) {
        removeAll();
        if (prmNewViewInfo == null) // 这种可能性发生在用户选择根结点时,appIndex为-1, 没有对应的ViewInfo.
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

    // 对视图中数据的操作------------------------------------------------------
    /**
     * 选取视图中的所有记录。
     * 
     * @called by seleteAllAction
     */
    public void seleteAllRecord() {
        if (activeView != null)
            activeView.seleteAllRecords();
    }

    /**
     * 取得被选中的记录。
     * 
     * @return 被选中的记录
     * @called by CutAction & CopyAction
     */
    public int[] getSelectRecordsID() {
        if (activeView != null)
            return activeView.getSelectedRecordIDs(); // RecordsID();
        else
            return new int[0];
    }

    /**
     * 取得被选中的记录。
     * 
     * @return 被选中的记录
     * @called by CutAction & CopyAction
     */
    public Vector getSelectRecords() {
        return activeView != null ? activeView.getSelectedRecords() : new Vector(0);
    }

    // 日记视图之特有方法结束
    public void setPageBack(
            boolean prmIsBack) {
        if (currentViewInfo.getViewType() == ModelCons.TABLE_VIEW)
            ((View_PIMDetails) activeView).setPageBack(prmIsBack);
    }

    /**
     * Invoke when the application frame closed.
     * 
     * @param prmNeedSave
     *            处理关闭的信息
     */
    public void closed(
            boolean prmNeedSave) {
        if (activeView != null)
            activeView.closed();
    }

    public Object[][] processContent(
            Object[][] pContents,
            Object[] pFieldNames) {
        if (pContents == null || pFieldNames == null || pContents.length < 1 || pFieldNames.length < 1
                || pContents[0].length != pFieldNames.length)
            return pContents;
        return pContents;
    }

    public void updateContent(
            PIMModelEvent e) {
    }

    /**
     * 布局面板 重写LayoutManager2的一个方法。note：LayoutManager2是父类所实现的接口，它继承另一接口：LayoutManager。
     */
    public void layoutContainer(
            Container parent) {
        activeView.setBounds(0, 0, getWidth(), getHeight());
    }

    // over-------------------------------------------------
    public void setBounds(
            int x,
            int y,
            int width,
            int height) {
        super.setBounds(x, y, width, height);
        if (activeView != null)
            activeView.setBounds(x, y, width, height);
    }

    /** 得到当前视图 */
    public IView getCurrentView() {
        return activeView;
    }

    /**
     * 根据视图子类型创建视图,目前这些视图类型是足够的，但是以后版本应改掉所有写死的地方。将可扩展性提高到极限。
     * 
     * @TODO：可否将实例化过的View缓存？
     * @param viewType
     *            视图子类型索引
     * @return IView 返回视图
     */
    protected IView getANewView(
            int prmViewType) {
        switch (prmViewType) {
            case ModelCons.TABLE_VIEW:
                tableView = new View_PIMDetails(CASUtility.EMPTYSTR);
                activeView = tableView;
                break;

            case ModelCons.CARD_VIEW:
                cardView = View_PIMThumbnails.getInstance();
                activeView = cardView;
                break;

            case ModelCons.ICON_VIEW:
                iconView = new View_PIMIcons();
                activeView = iconView;
                break;

            case ModelCons.TEXT_VIEW:
                if (textView == null)
                    textView = new View_PIMFilmstrip();
                activeView = textView;
                break;

            default:
                activeView = getTiedView();
        }
        return activeView;
    }

    /** TODO:释放当前不用的视图占用的内存，方法尚需补充 */
    protected void releasOtherViewMem(
            int viewType) {
        switch (viewType) {
            case ModelCons.TABLE_VIEW:
                if (cardView != null) {
                    cardView.release();
                    cardView = null;
                }
                if (textView != null) {
                    textView.release();
                    textView = null;
                }
                break;

            case ModelCons.CARD_VIEW:
                if (tableView != null) {
                    tableView.release();
                    tableView = null;
                }
                if (textView != null) {
                    textView.release();
                    textView = null;
                }
                break;

            case ModelCons.DAYWEEKMONTH_VIEW:
                break;

            case ModelCons.ICON_VIEW:
                break;

            case ModelCons.TEXT_VIEW:
                if (tableView != null) {
                    tableView.release();
                    tableView = null;
                }
                if (cardView != null) {
                    cardView.release();
                    cardView = null;
                }
                break;

            default:
                break;
        }
    }

    /** 负责返回popUpMenu的实例,如果没有初始化则初始化. */
    protected JPopupMenu getPopUpMenu() {
        if (popUpMenu == null) {
            popUpMenu = new JPopupMenu();
        }
        return popUpMenu;
    }

    // --------------------------------------------------------------
    protected static PIMViewInfo currentViewInfo;// 当前视图信息
    protected static IView activeView;// 当前视图(表格,卡片)

    private View_PIMFilmstrip textView;// 文本格视图
    private View_PIMDetails tableView;// 表格视图
    private View_PIMThumbnails cardView;// 卡片视图
    private View_PIMIcons iconView;// 图标视图

    private JPopupMenu popUpMenu;
}
// private int dividerLocation;//保留分隔条位置
// private int dividerScale;// 保留分隔比例
// private TexturePaint picture;//和画文件夹有关,

// //对预览面板和预览面板头部面板的处理--------------------------------------
// /**
// * 显示预览面板
// *
// * @param isShowPreviewPane
// * 是否显示预览
// */
// public void showPreviewPane(boolean isShowPreviewPane)
// {
// //Marked by 008 因为不懂此处为什么要克隆? PIMViewInfo tmpViewInfo = (PIMViewInfo)currentViewInfo.clone();
// if (isShowPreviewPane != currentViewInfo.hasPreview())
// {
// currentViewInfo.setHasPreview(!currentViewInfo.hasPreview());
// PIMControl.ctrl.getModel().updateViewInfo(currentViewInfo);
// }
// }
//
// /**
// * 对应于每一张视图,都有是否显示预览面板的不同视图,提供这个方法以便主菜单 视图项来处理预览窗的选中状态.
// *
// * @return 是否显示预览面
// */
// public boolean isShowPreviewPane()
// {
// if (currentViewInfo != null)
// {
// return currentViewInfo.hasPreview();
// }
// return false;
// }

// //@NOTE:此处提高可见性，是为了追求更好的性能
// void resetComponent()
// {
// // splitPane.removePropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, this);
// removeAll(); //首先清空当前View上的所有东西。
//
// dividerScale = currentViewInfo.getPreviewScale(); //保存分割线的位置TODO: 这一句是临时处理的,以后模型事件完善后要去除.
//
// if (applicationIndex == ModelConstants.CALENDAR_APP
// && currentViewInfo.getViewType() == ModelConstants.DAYWEEKMONTH_VIEW)
// {
// //主视图，在天视图下只占左半部分，右半部分显示任务，联系人和日记。
// if (datePane == null)
// {
// datePane = new EDatePane();
// }
// add(datePane);
//
// //按照Lotus1-2-3，EDayPlanerRight只有在天视图下才有，
// int tmpViewMode = 0;
// //CustomOptions.custOps.getActiveViewType(0); //TODO:此处0表示日历对应的应用类型号是0，应该改为动态取得，不该写死。
// if (tmpViewMode == MonthConstant.DAY_MODEL)
// {
// if (infoPane == null)
// {
// infoPane = new EDayPlanerRight();
// }
// else
// {
// if (tmpViewMode == MonthConstant.DAY_MODEL)
// {
// infoPane.updateMiniPanes();
//
// }
// }
// add(infoPane);
// }
// return;
// }
//
// // titlePane.setPaneTitle(//创建标题面板
// // PIMViewUtil.getPaneIcon(applicationIndex), PIMViewUtil.getPaneTitle(applicationIndex));
//
// activeView = getView(currentViewInfo.getViewType()); //初始化当前应用面板上对应的视图，即currentView变量。
// activeView.setApplication(this);
//
// //对日记做了专门的处理------------------------------------------------------------------------
// if (applicationIndex == ModelConstants.DIARY_APP
// && currentViewInfo.getViewType() == ModelConstants.TEXT_VIEW)
// {
// if (activeView instanceof PIMTextView)
// {
// ((PIMTextView)activeView).setDiaryDate(-1);
// }
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
// // topComp.add(titlePane);
// topComp.add(activeView);
//
// topComp.add(titlePane2);
// topComp.add(toDayView);
//
// // splitPane.setTopComponent(topComp);
// preViewPane = PIMPreviewPane.getTheSecondInstance();
// // splitPane.setBottomComponent(preViewPane);
// javax.swing.SwingUtilities.invokeLater(new Runnable()
// {
// public void run()
// {
// preViewPane.getTextPane().scrollRectToVisible(new java.awt.Rectangle(0, 0, 1, 1));
// }
// });
//
// // add(splitPane);
// // splitPane.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, this);
// // add(titlePane);
// revalidate();
// }
// else
// {
// if (topComp != null)
// {
// topComp.removeAll();
// }
// //添加到容器上
// //add(titlePane);
// add(activeView);
//
// //2是右边的,一般用来写今天的日记
// //add(titlePane2);
// add(toDayView);
// layoutContainer(null);
// }
// return;
// } //------------------------------------------------------------
//
// if (!currentViewInfo.hasPreview()) //对不需要预览面板的情况的处理
// {
// //不显示预览面板时就只加滚动面板
// add(activeView);
// // add(titlePane);
// revalidate();
// return;
// } //------------------------------------------------------------
//
// dividerScale = currentViewInfo.getPreviewScale(); //对需要显示预览面板的情况的处理
// // splitPane.setTopComponent(activeView.getContainer());
// preViewPane = PIMPreviewPane.getInstance();
// // splitPane.setBottomComponent(preViewPane);
// // add(splitPane);
// // splitPane.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, this);
// // add(titlePane);
// revalidate();
// //得到视图上的第一个记录
// final PIMRecord tmpRec;
// if (activeView.getSelectedRecords().size() <= 0)
// {
// tmpRec = null;
// }
// else
// {
// if (SeleRec == null)
// tmpRec = (PIMRecord)activeView.getSelectedRecords().get(0);
// else
// tmpRec = (PIMRecord)SeleRec[0];
// SeleRec = null;
// if (preViewPane.setContents(tmpRec)) //显示这条记录,成功的话设置其已读属性.
// {
// javax.swing.SwingUtilities.invokeLater(new Runnable()
// {
// public void run()
// {
// preViewPane.getTextPane().scrollRectToVisible(PIMPreviewPane.TOPRECTANGLE);
// Hashtable tmphash = tmpRec.getFieldValues();
// emo.pim.Pool pool = PIMPool.pool;
//
// //如果邮件为未读邮件，则将其变成已读，并检查如果要求回执的话进行回执询问
// Object tmpIsReaded = tmphash.get(pool.getIntegerKey(ModelDBConstants.READED));
// if (tmpIsReaded == null
// || tmpIsReaded.toString().equals(PropertyName.BOOLEAN_FALSE))
// {
// //设置已读(含图标TODO:图标没有发生效果)
// tmphash.put(pool.getIntegerKey(ModelDBConstants.READED), Boolean.TRUE);
// tmphash.put(pool.getIntegerKey(ModelDBConstants.ICON), new Byte((byte)1));
// //检查是否要发送回执？
// Object tmpReceipt =
// tmphash.get(pool.getIntegerKey(ModelDBConstants.TRACKSTATUS));
// if (tmpReceipt != null && ((Boolean)tmpReceipt).booleanValue())
// {
// if (ErrorDialog.showErrorDialog(MessageCons.Q50217) == 0)
// {
// PIMUtility.sendNotification(tmpRec);
// }
// }
// SeleRec = new PIMRecord[] { tmpRec };
// PIMControl.ctrl.getModel().updateRecord(tmpRec, true);
// }
// }
// });
// }
// }
// }

// /**
// * 保存预览面板分隔点
// */
// private void savePreviewScale()
// {
// int previewScale = 100 * dividerLocation / (getHeight() - 20);
// PIMViewInfo tmpViewInfo = (PIMViewInfo)currentViewInfo.clone();
// tmpViewInfo.setPreviewScale(previewScale);
// PIMControl.ctrl.getModel().updateViewInfo(tmpViewInfo);
// }

// 更新和改换视图的能力----------------------------------------------------
// /**响应数据库事件并更新视图
// * @param e
// */
// public void updateView(PIMModelEvent e)
// {
// int tmpEveType = e.getType();
// int tmpAppType = currentViewInfo.getAppType();
// int tmpSubApp = currentViewInfo.getAppSubType();
// String tmpFoldPath = CustomOptions.custOps.getActiveFolderPath();
//
// Vector tmpVec = PIMControl.ctrl.getMainPane().getSelectRecords();//处理菜单更新动作
// if (tmpVec != null && tmpVec.size() >= 0)
// {
// // PIMControl.ctrl.updateActionStatus(CustomOptions.custOps.getActiveAppType());
// PIMControl.ctrl.setActionStatus();
// }
//
// if (tmpEveType == PIMModelEvent.FIELD_CHANGED || tmpEveType == PIMModelEvent.UPDATE_FIELD)//处理字段更新事件
// {
// currentViewInfo = PIMControl.ctrl.getViewInfo(tmpAppType, tmpSubApp, tmpFoldPath);
// activeView.setViewInfo(currentViewInfo);
// A.s(" field name =*****= " + currentViewInfo.getFilterString());
// }

// else if (tmpEveType == PIMModelEvent.UPDATE_FIELD)//暂时由于表格事件分为二种,在我的处理中暂时还不得不和上面一样
// {
// currentViewInfo = PIMControl.ctrl.getViewInfo(tmpAppType, tmpSubApp, tmpFoldPath);
// int newScale = currentViewInfo.getPreviewScale();//TODO: 这里临时处理一下滚动范围以后要统一通过事件类型来处理
// if (dividerScale != newScale)
// {
// return;
// }
// contents = PIMControl.ctrl.getContents(currentViewInfo);
// //2003.10.22 没有办法,现在出现了判断要显示预览面板,目前没有对应的信息
// //所以要更新一下视图信息并布局.
// activeView = getView(currentViewInfo.getViewType());
// activeView.setApplication(this);
// }
// else
// {
// //2003.10.22 没有办法,现在出现了判断要显示预览面板,目前没有对应的信息
// //所以要更新一下视图信息并布局.
// currentViewInfo = PIMControl.ctrl.getViewInfo(tmpAppType, tmpSubApp, tmpFoldPath);
// contents = PIMControl.ctrl.getContents(currentViewInfo);
// javax.swing.SwingUtilities.invokeLater(new Runnable()
// //@NOTE:为了防止remove组件时组件的焦点转移尚未完毕造成JDK异常,
// { //比如当Table在编辑状态,双击编辑器时触发edtingStop(),进而viewToModel()
// public void run() //-->updateRecord()时就会在编辑器仍然拥有焦点时移除,造成异常.
// {
// resetComponent();
// }
// });
// activeView.setApplication(this);
// }
// }
/**
 * 作为实验用途，专用于IMAP视图的显示
 * 
 * @return
 */
// protected AbstractPIMView getView()
// {
// tableView = PIMTableView.getInstance("IMAP");
// activeView = tableView;
// return activeView;
// }
