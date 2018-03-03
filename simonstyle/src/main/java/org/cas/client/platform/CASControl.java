package org.cas.client.platform;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.ModelChangeListener;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascontrol.menuaction.CASFolderManager;
import org.cas.client.platform.cascontrol.navigation.CASNavigationPane;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascontrol.navigation.action.FolderChangerAction;
import org.cas.client.platform.cascontrol.thread.ThreadActionsFacade;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.EDate;
import org.cas.client.platform.casutil.EDaySet;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMDBUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.resource.international.ImportDlgConst;
import org.cas.client.resource.international.PaneConsts;

//菜单状态更新的机制替换说明:
//EIO的机制:每次动作从系统费老劲拿一个值来,然后遍历每一个Action,依次通过一个相与过程,并对Action进行有效性设置.菜单显示时可能会参考Action的值.
//现在是每次动作对一个系统值相与.无任何遍历.菜单Item显示时调出对应的actionflag,通过相与过程进行有效性设置.遍历应该高效的多.没有多余对象.
public class CASControl {
    // Singleton----------------------------------------------------------------------------
    public static CASControl ctrl = new CASControl();

    private CASControl() {
        initactionFlags();
    }

    /**
     * @NOTE:如果给某个设置了两个标记,则表示两个标记中任意一个被置位时,该命令即有效.而不表示两个都被置位时才有效.因为菜单在使用时是拿状态 
     *                                                                        与系统状态相与.如果希望在两者(或多者)都满足的时候该菜单项才有效.则应该给状态置的最高位置位
     *                                                                        (与NEED_ALL_MATCH或即可).
     *                                                                        本机制目前还未能支持"同时满足某几个状态时"
     *                                                                        或者"满足另外几个状态中任意一个"的混和状态设置的功能.
     *                                                                        所以当需要该功能时,必须在ISystemStatusCons类中专门加一些状态
     *                                                                        ,然后在状态变化时做多一些判断处理来实现.
     */
    private void initactionFlags() {
        systemStatus = IStatCons.OPEN;
        actionFlags = new HashMap();
        // file----------------------------------------------------------
        actionFlags.put("OpenAction", PIMPool.pool.getKey(IStatCons.RECORD_SELECTED)); // 打开
        actionFlags.put("CloseAction", PIMPool.pool.getKey(IStatCons.OPEN)); // 关闭
        actionFlags.put("SaveAsAction", PIMPool.pool.getKey(IStatCons.RECORD_SELECTED)); // 保存
        actionFlags.put("SaveAttachNameAction",
                PIMPool.pool.getKey(IStatCons.HAVE_ATTACH | IStatCons.RECORD_SELECTED | IStatCons.NEED_ALL_MATCH));// 另存为
        actionFlags.put("FolderPropertyAction", PIMPool.pool.getKey(IStatCons.FOLDER_SELECTED)); // 文件夹
        actionFlags.put("FolderNewAction", PIMPool.pool.getKey(IStatCons.FOLDER_SELECTED)); // 新建文件夹
        actionFlags.put("FolderRenameAction", PIMPool.pool.getKey(IStatCons.FOLDER_SELECTED)); // 重命名文件夹
        actionFlags.put("FolderMoveAction", PIMPool.pool.getKey(IStatCons.FOLDER_SELECTED)); // 移动文件夹
        actionFlags.put("FolderDelAction", PIMPool.pool.getKey(IStatCons.FOLDER_SELECTED)); // 删除文件夹
        actionFlags.put("AchiveAction", PIMPool.pool.getKey(IStatCons.ALWAYS)); // 归档
        actionFlags.put("ImportExportAction", PIMPool.pool.getKey(IStatCons.ALWAYS)); // 导入导出
        actionFlags.put("PrintReviewAction", PIMPool.pool.getKey(IStatCons.OPEN)); // 打印预览
        actionFlags.put("PrintAction", PIMPool.pool.getKey(IStatCons.OPEN)); // 打印
        actionFlags.put("SendAction", PIMPool.pool.getKey(IStatCons.RECORD_SELECTED)); // 发送
        actionFlags.put("PropertiesAction", PIMPool.pool.getKey(IStatCons.FOLDER_SELECTED)); // 属性
        actionFlags.put("FileExitAction", PIMPool.pool.getKey(IStatCons.ALWAYS)); // 退出
        // edit----------------------------------------------------------
        actionFlags.put("SelectAllAction", PIMPool.pool.getKey(IStatCons.FOLDER_SELECTED)); // 查找
        actionFlags.put("DeleteAction", PIMPool.pool.getKey(IStatCons.RECORD_SELECTED)); // 查找
        actionFlags.put("FindAction", PIMPool.pool.getKey(IStatCons.ALWAYS)); // 查找
        actionFlags.put("FolderMoveAction", PIMPool.pool.getKey(IStatCons.FOLDER_SELECTED)); // 查找
        actionFlags.put("CategoriesAction", PIMPool.pool.getKey(IStatCons.RECORD_SELECTED)); // 查找

        // view----------------------------------------------------------
        actionFlags.put("DifineViewAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
        actionFlags.put("ViewGotoAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
        actionFlags.put("LastFolderAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
        actionFlags.put("NextFolderAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
        actionFlags.put("PaneAction", PIMPool.pool.getKey(IStatCons.ALWAYS)); // 显示_隐藏导航组件
        actionFlags.put("HideBookAction", PIMPool.pool.getKey(IStatCons.ALWAYS)); // 显示_隐藏书本组件
        actionFlags.put("CircumViewAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
        actionFlags.put("PreviewAction", PIMPool.pool.getKey(IStatCons.OPEN));
        actionFlags.put("NextMainBGAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
        actionFlags.put("CustomizeBGAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
        // tool----------------------------------------------------------
        actionFlags.put("ModifyData", PIMPool.pool.getKey(IStatCons.ALWAYS));
        actionFlags.put("OnlineDiscussAction", PIMPool.pool.getKey(IStatCons.ALWAYS)); // 显示聊天插件.
        // help----------------------------------------------------------
        actionFlags.put("OnlineRegister", PIMPool.pool.getKey(IStatCons.ALWAYS));
        actionFlags.put("AboutAction", PIMPool.pool.getKey(IStatCons.ALWAYS));
    }

    // =====================================================================================================================
    /**
     * @called by: IPIMFrame.java; ControlSystem.java;
     * @NOTE: 本方法由于耗时稍多，可考虑在线程里调用。
     */
    public void initModel() {
        model = new PIMDBModel();
        model.addPIMModelListener(new ModelChangeListener());
        ThreadActionsFacade.getInstance(); // 初始化并启动后台线程
    }

    // ==================================================================================================================================================
    public void removeToolbars(
            JToolBar[] prmBars) {
        if (prmBars == null || prmBars.length == 0)
            return;
        JPanel[] tmpBarPaneAry = mainFrame.getToolBarPanes(); // 得到所有放Toolbar的面板.
        for (int i = 0; i < prmBars.length; i++) { // 遍历每个参数bar.
            Class tmpClass = prmBars[i].getClass(); // 取出当前参数bar的Class.
            for (int j = 0; j < tmpBarPaneAry.length; j++) { // 对这个bar遍历每个面板.
                boolean tmpFound = false; // 匹配到后置此标志,退出循环,开始对下个参数bar的移除.
                for (int l = 0, len = tmpBarPaneAry[j].getComponentCount(); l < len; l++) {// 遍历当前面板上的每个组件.
                    if (tmpBarPaneAry[j].getComponent(l).getClass() == tmpClass) { // 如果组件类型和当前bar类型一致.
                        tmpBarPaneAry[j].remove(l); // 从当前面板上移除当前bar.
                        tmpFound = true;
                        break; // 并退出对当前面板上组件的遍历
                    }
                }
                if (tmpFound)
                    break; // 以及对面板的遍历.
            }
        }
        mainFrame.reLayout();
    }

    public void addToolbars(
            JToolBar[] prmBars) {
        if (prmBars == null || prmBars.length == 0)
            return;
        JPanel[] tmpBarPaneAry = mainFrame.getToolBarPanes();
        for (int i = 0; i < prmBars.length; i++) {
            tmpBarPaneAry[0].add(prmBars[i]);
        }
        mainFrame.reLayout();
    }

    /** 获得相关应用的model */
    public ICASModel getModel() {
        return model;
    }

    /**
     * 设置model
     * 
     * @param model
     *            被设置的model。
     * @called by emo.pim.ModeSetter
     */
    public void setModel(
            ICASModel model) {
        this.model = model;
    }

    /**
     * Getter for property mainPane.
     * 
     * @return Value of property mainPane.
     */
    public MainPane getMainPane() {
        if (mainPane == null)
            mainPane = new MainPane();
        return mainPane;
    }

    /**
     * Getter for property mainPane.
     * 
     * @return Value of property mainPane.
     * @called by emo.pim.ModeSetter
     */
    public void setMainPane(
            MainPane mainPane) {
        this.mainPane = mainPane;
    }

    /**
     * 获得相关应用视图的ViewInfo
     * 
     * @TODO:本方法面临巨大改动,下一版将尝试将每一个ViewInfo替换成一个View。当用户增减文件夹的时候，只是根据FolderName 
     *                                                                        增加和减少View。当用户将记录在文件夹间移动的时候，后台调整的只是Infolder字段
     *                                                                        。
     * @NOTE：另外，本方法的参数中的FolderPath为，完整的文件夹路径。需要注意的是：文件夹的名字可能被用户通过修改Config2 
     *                                                                     文件改变的。所以使用的时候必须先根据于Config2文件中文件夹名的比较结果得到对应的Appname
     *                                                                     ，才能作为key，从数据库中换 取viewInfo或将来的View。
     */
    public synchronized PIMViewInfo getViewInfo(
            int prmFolderID) {
        if (prmFolderID <= 0 || model == null)
            return null;
        else
            return model.getViewInfo(prmFolderID);
    }

    /**
     * 改变视图 1、先判断了model为null的特殊情形（该情形只有在系统启动时默认显示Cover时才遇到，以后可能随规格变动）； 2、对传入参数为空的情况做处理，给其赋一个默认值，目前用的是根路径。
     * 3、过滤掉文件夹路径和视图类型都相同的情形； 4、保存当前的设置 5、更新导航树的状态 6、视图更新 7、更新菜单工具条的状态
     * 
     * @param prmAppIndex
     *            应用的索引值
     * @called by PIMUndoFolderManager.updateFolder();
     */
    public void changeApplication(
            int prmAppIndex,
            int prmViewInfoPathID) {
        if (model == null) { // 由于model可能是在线程中实例化的，所以，如果用户在界面刚
            if (PaneConsts.PIM_PATH_ID == prmViewInfoPathID) {
                CustOpts.custOps.setActiveAppType(-1);
                mainPane.changeApplication(prmAppIndex, prmViewInfoPathID);
            }
            return; // 不响应。（希望用户再次点击时，model已经实例化完毕。）
        } // TODO：可以改善为弹出modle初始化进度条
          // 如果传入的路径有问题，则视为希望选中根节点。
        if (prmViewInfoPathID <= 0) // 比如已知的：当点击baseBook书签以外的区域，就会
            prmViewInfoPathID = PaneConsts.PIM_PATH_ID; // 调closeBookAction，而这个action中
                                                        // 就会调本方法，并传入空字串。
        if (CustOpts.custOps.getActiveAppType() == prmAppIndex
                && (CustOpts.custOps.getActivePathID() == prmViewInfoPathID)) // 排除所有不需要处理的情况:应用类型,视
            return; // 图类型,文件夹类型都相同的情况.
        // 至此除错结束---------------------------------------------

        // 开始将新的状态信息保存到CustOpts中--------------------------
        CustOpts.custOps.setActivePathID(prmViewInfoPathID); // NOTE：该句应尽早调，应为setPane等其他语句中需要根据用户的当前设置确定TabbedPane的布局。
        CustOpts.custOps.setActiveAppType(prmAppIndex); // TODO：可能会有更好的方法，由于视图切换时需要保存当前的视图类型。

        CASTree.getInstance().setSelectNode(prmViewInfoPathID); // 调整导航面板上的被选中节点,使保持和视图一致.NOTE:要保
                                                                // 证不触发其他动作.如果是从导航面版上点击鼠标而触发的应用
                                                                // 切换,此句代码是多余的,鉴于时间暂不敏感,就多设一次吧.
        getMainPane().changeApplication(prmAppIndex, prmViewInfoPathID);

        getFolderManager().addFolder(prmAppIndex); // 更新前进后退的UndoManager
        CASTree.getInstance().getLastFolderAction().update();
        CASTree.getInstance().getNextFolderAction().update();
    }

    /**
     * @Called by: DateSeleAreaPane; 设置天视图 在触发其他视图转换到天视图时调用。
     * @param daySet
     *            EDaySet设置所选取的天
     */
    public void setSelectedDay(
            EDaySet prmDaySet) {
        if (prmDaySet == null) {
            ErrorUtil.write("the selected Date is null(PIMControl.setSelectedDay())"); // 选中的日期为null!!!!!!!!!!!");
            return;
        }
        daySet = (EDaySet) prmDaySet.clone();
        mainPane.updateView(null);
    }

    /**
     * @called by: PIMTextView 返回Int格式的日期值.
     */
    public int getSelectedDayInt() {
        if (daySet == null)
            return -1;
        int tmpDateInt;
        EDate[] tmpEDate = daySet.getArray();
        if (tmpEDate.length > 0) {
            String tmpDate = null;
            tmpDateInt = tmpEDate[0].getMonth();
            if (tmpDateInt < 10)
                tmpDate = String.valueOf(tmpEDate[0].getYear()).concat("0").concat(String.valueOf(tmpDateInt));
            else
                tmpDate = String.valueOf(tmpEDate[0].getYear()).concat(String.valueOf(tmpDateInt));

            tmpDateInt = tmpEDate[0].getDate();
            if (tmpDateInt < 10)
                tmpDate = tmpDate.concat("0").concat(String.valueOf(tmpDateInt));
            else
                tmpDate = tmpDate.concat(String.valueOf(tmpDateInt));
            return Integer.parseInt(tmpDate);
        }
        return -1;
    }

    public Vector getSelectRecords() {
        return mainPane.getSelectRecords();
    }

    /**
     * 通过该方法使系统的某一位被置位.
     * 
     * @param prmStatus
     */
    public void addSystemStatus(
            int prmStatus) {
        systemStatus |= prmStatus;
        // menuBar.updateToolbarStatus(systemStatus);//应为工具条一直都处于可见状态,所以每次系统状态变化时都要更新.
    }

    public void resetSystemStatus(
            int prmStatus) {
        systemStatus &= ~prmStatus;
        // menuBar.updateToolbarStatus(systemStatus);//应为工具条一直都处于可见状态,所以每次系统状态变化时都要更新.
    }

    /** 取得Action的状态值 */
    public int getSystemStatus()// int prmAppType, String folderPath)
    {
        return systemStatus;
    }

    /** @called by: emo.pim.PIMMain;emo.pim.pimcontrol.action.pimmenu.view.LastFolderAction;NextFolderAction */
    public CASFolderManager getFolderManager() {
        if (folderUndoManager == null) {
            folderUndoManager = new CASFolderManager();
            folderUndoManager.addFolderChangeListener(new FolderChangerAction());
        }
        return folderUndoManager;
    }

    /**
     * @called by: 设置PIM的主窗体。
     * @param frame
     *            需要应用的主窗体。
     */
    public void setMainFrame(
            ICASFrame prmMainFrame) {
        mainFrame = prmMainFrame;
    }

    /**
     * @called by : ETimeBar;actions which to show a dialog. 获得PIM的主窗体，整合运行时获得EIO的主窗口
     * @return 主窗体
     */
    public Frame getMainFrame() {
        return (Frame) mainFrame;
    }

    public void showMainFrame() {
        if (mainFrame == null) {
            CASMainFrame.main(null);
            mainFrame = CASMainFrame.mainFrame; // 初始化主窗体并注册
        }
        getMainFrame().setVisible(true);
    }

    /**
     * 取得导航面板树
     * 
     * @return 导航面板树
     */
    public CASTree getFolderTree() {
        return mainFrame.getFolderPane().getFolderTree();
    }

    public CASNavigationPane getFolderPane() {
        return mainFrame.getFolderPane();
    }

    public void closePIMAPP() {
        if (model != null)
            model.compactDataFile();
    }

    /** 取得当前视图信息 */
    public PIMViewInfo getCurrentViewInfo() {
        return model.getViewInfo(CustOpts.custOps.getActivePathID());
    }

    /**
     * 遍历系统本身及各个应用,得到任意动作名对应的动作对象.
     * 
     * @param prmActionName
     * @return
     */
    public Action getAction(
            String prmActionName) {
        if (actionFlags.get(prmActionName) != null) {// 看看该ActionName是否被系统维护.如果是系统有维护的,那么就是系统的Action.
            StringBuffer tmpClassPath = new StringBuffer("org.cas.client.platform.");
            tmpClassPath.append("cascontrol.menuaction.");// 系统自带的Action都在emo.pim.pimcontrol.commonaction目录下.
            tmpClassPath.append(prmActionName);
            try {
                return (Action) Class.forName(tmpClassPath.toString()).newInstance();
            } catch (Exception e) {
                ErrorUtil.write("find no matching Class in commonaction package:" + prmActionName);
                return null;
            }
        } else
            // 否则就是其它应用提供的Action,则通过向各个应用询问得到对应的Action对象.
            return mainPane.getAction(prmActionName);
    }

    public String[] getImportDisStr() {
        Vector tmpVec = mainPane.getImportDisStr();
        String[] tmpStrAry = new String[tmpVec.size()];// + 2];
        for (int i = 0, tmpLength = tmpStrAry.length /*- 2*/; i < tmpLength; i++)
            tmpStrAry[i] = (String) tmpVec.get(i);
        // tmpStrAry[tmpStrAry.length - 2] = ImportDialogConstant.IMPORT_INFO;
        // tmpStrAry[tmpStrAry.length - 1] = ImportDialogConstant.EXPORT_INFO;
        return tmpStrAry;
    }

    public String getImportIntrStr(
            Object prmKey) {
        if (ImportDlgConst.IMPORT_INFO.equals(prmKey))
            return ImportDlgConst.IMPORT_INFO;
        else if (ImportDlgConst.EXPORT_INFO.equals(prmKey))
            return ImportDlgConst.EXPORT_INFO;
        else
            return mainPane.getImportIntrStr(prmKey);
    }

    public void execImport(
            Object prmKey) {
        if (ImportDlgConst.IMPORT_INFO.equals(prmKey)) {
            // new CerGuideDialog(this, CertificateConstant.GUIDE_IN, true).show();
        } else if (ImportDlgConst.EXPORT_INFO.equals(prmKey)) {
            // new CertManageDialog(this).show();
        } else
            mainPane.execImport(prmKey);
    }

    /**
     * 通过任意的动作名通过遍历所有的应用,得到该动作的状态属性,用于在不同状态下显示菜单时和当时的系统状态相比较.
     * 
     * @called only by PIMBarMenu;
     * @param prmActionName
     * @return
     */
    public int getStatus(
            Object prmActionName) {
        Integer tmpStatus = (Integer) actionFlags.get(prmActionName);
        if (tmpStatus != null)
            return tmpStatus.intValue();
        return mainPane.getStatus(prmActionName);
    }

    // ====================================================================
    // ------------- 这 里 开 始 菜 单 项 状 态 控 制 ----------------------
    // ====================================================================
    /**
     */
    public String getRegistedUserName() {
        // @TODO:改为调EIO的注册信息。
        return "";
    }

    /**
     */
    public String getOrganizerUnit() {
        // @TODO:改为调EIO的注册信息。
        return "";
    }

    /**
     */
    public String getOrganizerName() {
        // @TODO:改为调EIO的注册信息。
        return "";
    }

    /**
     */
    public String getCityName() {
        // @TODO:改为调EIO的注册信息。
        return "";
    }

    /**
     */
    public String getContryName() {
        // @TODO:改为调EIO的注册信息。
        return "";
    }

    /**
     */
    public String getStateName() {
        // @TODO:改为调EIO的注册信息。
        return "";
    }

    /**
     */
    public String getcContryCode() {
        // @TODO:改为调EIO的注册信息。
        return "";
    }

    /**
     * 退出系统。在单独运行时，菜单中的和点击关闭窗体的按钮都调用此方法
     * 
     * @NOTE:和EIO整合运行时，上述两个动作不调本方法，只setVisible（false）。
     */
    public void exitSystem() {
        // WindowsTrayIcon.cleanUp(); //关闭系统栏图标。

        if (mainFrame != null && ((Frame) mainFrame).getExtendedState() != JFrame.ICONIFIED)// 保存所有状态
            // @NOTE:此处需要过滤掉最小化的情况，因为窗体启动时将被设置为上次退出被保存的状态值。
            CustOpts.custOps.setFrameState(((Frame) mainFrame).getExtendedState());// 而如果将要设置的状态是ICONIFIED的话，将导致窗体上的菜单栏秀不出来，而窗体也不会被最小化。

        if (mainFrame != null && ((Frame) mainFrame).getExtendedState() == Frame.NORMAL) {
            CustOpts.custOps.setFrameWidth(((Frame) mainFrame).getWidth());
            CustOpts.custOps.setFrameHeight(((Frame) mainFrame).getHeight());
            CustOpts.custOps.setFrameX(((Frame) mainFrame).getX());
            CustOpts.custOps.setFrameY(((Frame) mainFrame).getY());
        }

        CustOpts.custOps.saveData(); // 将所有用户自定义设置信息存入硬盘。

        ThreadActionsFacade.cancel();
        if (mainPane != null)
            mainPane.closed(true);

        Thread tmpThread = new Thread() {
            @Override
            public void run() {
                try {
                    closePIMAPP(); // 在线程中关闭数据库后退出。
                    PIMDBUtil.deletePIMTempDirectory(); // 删除临时目录
                    CASUtility.backup();
                } catch (Exception e) {
                    ErrorUtil.write(e);
                } finally {
                    // shall not do system exit, because it might be running in a container. System.exit(0);
                }
            }
        };
        tmpThread.start();
    }

    /** Get the path of the source code. */
    public String getSourcePath() {
        String src = getClass().getResource(CASUtility.EMPTYSTR).getPath();

        int p = src.indexOf("C:/");
        if (p > 0) {
            src = src.substring(p);
        }

        if (src.indexOf("/lib/") > 0) {
            return src.substring(0, src.indexOf("/lib/") + 1);
        } else {
            if (src.indexOf("lib") > 0) {
                return src.substring(0, src.indexOf("lib"));
            } else {
                return src.substring(0, src.indexOf("/target/") + 1);
            }
        }
    }

    /** 原来用一个布尔值有问题，对锁、锁、开、开的情况不能处理 */
    public void lockModel() {
        this.modelLock++;
    }

    public void unlockModel() {
        this.modelLock--;
    }

    // variables-----------------------------------------------------------------
    ICASFrame mainFrame; // @NOTE:提高可见性，是为了追求更好的性能。
    private MainPane mainPane;
    private ICASModel model;

    private CASFolderManager folderUndoManager;

    private EDaySet daySet; // 用于存放日期选择区所选中的时间.
    private int modelLock;
    private static int systemStatus;// 当前程序所处的状态,各菜单在显示的时候根据自身的属性定制值与该值相与的结果决定是否显示成灰色.
    public static int pasteType;// 黏贴状态
    public static final int NULL = 0; // 无内容
    public static final int CUT = 1; // 剪切，即Cut后Paste
    public static final int COPY = 2;// 复制，即Copy后Paste
    private HashMap actionFlags;
}
// public void getSysetmStatus()
// {
// int tmpSystemState = 0; //用于标识系统当前的状态.
// String tmpHasAttach = PIMUtility.EMPTYSTR; //用于临时标识当前选中的记录是否带有附件.
// Boolean tmpIsRead = null; //用于临时标识当前选中的记录是否已读.
// int tmpPathCount = 0;//根据pathcount判断是否有可编辑的文件夹
// int tmpSeleCount; //根据vector的大小判断是否有记录被选中
//
// Vector tmpAccount = model.getMailAccount(ModelConstants.ACCOUNT_VALID);//给系统状态增加"当前是否具有有效账号"的状态信息.
// tmpSystemState |= (tmpAccount == null || tmpAccount.size() == 0) ? tmpSystemState :
// IActionFlagCons.ONLY_USE_WHEN_HAS_ACCOUNT;
//
// Vector tmpRecords = getSelectRecords();
// if (tmpRecords == null)
// tmpSeleCount = 0;
// else
// tmpSeleCount = tmpRecords.size();
//
// if (tmpSeleCount == 1)//如果有且只有一条记录被选中,则设置附件和已读状态.
// {
// Object tmpObj = ((PIMRecord)tmpRecords.get(0)).getFieldValue(ModelDBConstants.ATTACHMENT);//标志与附件有关的action状态
// if(tmpObj != null)
// tmpHasAttach = (String)tmpObj;
//
// tmpObj = ((PIMRecord)tmpRecords.get(0)).getFieldValue(ModelDBConstants.READED); //标志已读未读状态
// if(tmpObj != null)
// tmpIsRead = (Boolean)tmpObj;
// }
//
// TreePath tmpPath = getFolderTree().getSelectedPath();
// if (tmpPath != null)
// tmpPathCount |= tmpPath.getPathCount(); //根据pathcount判断是否有可编辑的文件夹
//
// // 标志Undo的状态
// UndoableEdit tmpUndoEdit = null;//getPIMUndoAction().getUndoEdit();
// // 标志Redo的状态
// UndoableEdit tmpRedoEdit = null;//getPIMRedoAction().getRedoEdit();
//
// switch (prmAppType)
// {
// // case ModelConstants.LOCAL_APP :
// // tmpSystemState |= PIMActionFlag.STATUS_LOCAL;
// // tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | PIMActionFlag.STATUS_HAVE_UNDO;
// // tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | PIMActionFlag.STATUS_HAVE_REDO;
// // tmpSystemState |= PIMActionFlag.STATUS_ALL_APP;
// // break;
// case ModelConstants.CALENDAR_APP :
// tmpSystemState |= IActionFlagCons.STATUS_CALENDAR /*| PIMActionFlag.STATUS_CAN_FIND*/;
// // tmpSystemState |= tmpSize > 0
// // ?
// // : ;
// // tmpSystemState = tmpPathCount > 3 ? tmpSystemState | PIMActionFlag.STATUS_FOLDER_SELECTED : tmpSystemState;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState |= IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.TASK_APP :
// // tmpSystemState |= tmpSize > 0
// // ? PIMActionFlag.STATUS_RECORD_SELECTED | PIMActionFlag.STATUS_TASK
// // : PIMActionFlag.STATUS_TASK;
// tmpSystemState |= IActionFlagCons.STATUS_TASK | IActionFlagCons.STATUS_SELECT_ALL | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState |= IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.CONTACT_APP :
// // tmpSystemState |= tmpSize > 0
// // ? PIMActionFlag.STATUS_RECORD_SELECTED | PIMActionFlag.STATUS_CONTACT
// // : PIMActionFlag.STATUS_CONTACT;
// tmpSystemState |= IActionFlagCons.STATUS_CONTACT | IActionFlagCons.STATUS_SELECT_ALL;
// tmpSystemState = pasteType > 0 ? tmpSystemState | IActionFlagCons.STATUS_HAVE_PASTE : tmpSystemState;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_CAN_FIND;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED |
// IActionFlagCons.STATUS_CAN_FIND;
// }
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.DIARY_APP :
// // tmpSystemState |= tmpSize > 0
// // ? PIMActionFlag.STATUS_RECORD_SELECTED | PIMActionFlag.STATUS_DIARY
// // : PIMActionFlag.STATUS_DIARY;
// tmpSystemState |= IActionFlagCons.STATUS_DIARY | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState |= IActionFlagCons.STATUS_ALL_APP | IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.INBOX_APP :
// if (tmpPathCount == 3)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 3)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// case ModelConstants.OUTBOX_APP :
// tmpSystemState |= /*tmpSize > 0
// ? PIMActionFlag.STATUS_RECORD_SELECTED | PIMActionFlag.STATUS_MAIL | PIMActionFlag.STATUS_SELECT_ALL
// :*/ IActionFlagCons.STATUS_MAIL | IActionFlagCons.STATUS_SELECT_ALL | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = PIMUtility.EMPTYSTR.equals(tmpHasAttach) ? tmpSystemState : tmpSystemState |
// IActionFlagCons.STATUS_HAVE_ATTACH;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// // tmpSystemState = tmpPasteType > 0 ? tmpSystemState | PIMActionFlag.STATUS_HAVE_PASTE : tmpSystemState;
// tmpSystemState =
// tmpIsRead != null && tmpIsRead.booleanValue() ?
// tmpSystemState | IActionFlagCons.STATUS_HAVE_READED : tmpSystemState | IActionFlagCons.STATUS_HAVENOT_READED;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.SENDED_APP :
// tmpSystemState |= tmpSeleCount > 0
// ? IActionFlagCons.STATUS_RECORD_SELECTED | IActionFlagCons.STATUS_MAIL | IActionFlagCons.STATUS_SELECT_ALL
// : IActionFlagCons.STATUS_MAIL | IActionFlagCons.STATUS_SELECT_ALL;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = PIMUtility.EMPTYSTR.equals(tmpHasAttach) ? tmpSystemState : tmpSystemState |
// IActionFlagCons.STATUS_HAVE_ATTACH;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState = pasteType > 0 ? tmpSystemState | IActionFlagCons.STATUS_HAVE_PASTE : tmpSystemState;
// tmpSystemState =
// tmpIsRead != null && tmpIsRead.booleanValue() ?
// tmpSystemState | IActionFlagCons.STATUS_HAVE_READED : tmpSystemState | IActionFlagCons.STATUS_HAVENOT_READED;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP | IActionFlagCons.STATUS_CAN_FIND;
// break;
// case ModelConstants.DRAFT_APP :
// tmpSystemState |= IActionFlagCons.STATUS_MAIL | IActionFlagCons.STATUS_SELECT_ALL;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = PIMUtility.EMPTYSTR.equals(tmpHasAttach) ? tmpSystemState : tmpSystemState |
// IActionFlagCons.STATUS_HAVE_ATTACH;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState =
// tmpIsRead != null && tmpIsRead.booleanValue() ?
// tmpSystemState | IActionFlagCons.STATUS_HAVE_READED : tmpSystemState | IActionFlagCons.STATUS_HAVENOT_READED;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP | IActionFlagCons.STATUS_CAN_FIND;
// break;
// case ModelConstants.DELETED_ITEM_APP :
// if (tmpSeleCount == 1)
// { //此处需要优化
// int tmpType = ((PIMRecord)tmpRecords.get(0)).getAppIndex();
// switch (tmpType)
// {
// case ModelConstants.CALENDAR_APP :
// // tmpSystemState |= PIMActionFlag.STATUS_RECORD_SELECTED | PIMActionFlag.STATUS_CALENDAR;
// // tmpSystemState = tmpPathCount > 3 ? tmpSystemState | PIMActionFlag.STATUS_FOLDER_SELECTED : tmpSystemState;
// tmpSystemState |= IActionFlagCons.STATUS_CALENDAR | IActionFlagCons.STATUS_CAN_FIND;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState |= IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.TASK_APP :
// // tmpSystemState |= tmpSize > 0
// // ? PIMActionFlag.STATUS_RECORD_SELECTED | PIMActionFlag.STATUS_TASK
// // : PIMActionFlag.STATUS_TASK;
// tmpSystemState |= IActionFlagCons.STATUS_TASK | IActionFlagCons.STATUS_SELECT_ALL | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState |= IActionFlagCons.STATUS_ALL_APP;
// break;
//
// case ModelConstants.CONTACT_APP :
// // tmpSystemState |= PIMActionFlag.STATUS_RECORD_SELECTED | PIMActionFlag.STATUS_CONTACT;
// tmpSystemState |= IActionFlagCons.STATUS_CONTACT | IActionFlagCons.STATUS_SELECT_ALL;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_CAN_FIND;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED |
// IActionFlagCons.STATUS_CAN_FIND;
// }
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.DIARY_APP :
// // tmpSystemState |= PIMActionFlag.STATUS_RECORD_SELECTED | PIMActionFlag.STATUS_DIARY;
// tmpSystemState |= IActionFlagCons.STATUS_DIARY | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState |= IActionFlagCons.STATUS_ALL_APP | IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.INBOX_APP :
// tmpSystemState |= IActionFlagCons.STATUS_RECORD_SELECTED | IActionFlagCons.STATUS_MAIL |
// IActionFlagCons.STATUS_SELECT_ALL | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = PIMUtility.EMPTYSTR.equals(tmpHasAttach) ? tmpSystemState : tmpSystemState |
// IActionFlagCons.STATUS_HAVE_ATTACH;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState = pasteType > 0 ? tmpSystemState | IActionFlagCons.STATUS_HAVE_PASTE : tmpSystemState;
// tmpSystemState =
// tmpIsRead != null && tmpIsRead.booleanValue() ? tmpSystemState =
// tmpSystemState | IActionFlagCons.STATUS_HAVE_READED : tmpSystemState | IActionFlagCons.STATUS_HAVENOT_READED;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.OUTBOX_APP :
// tmpSystemState |=/* PIMActionFlag.STATUS_RECORD_SELECTED |*/ IActionFlagCons.STATUS_MAIL |
// IActionFlagCons.STATUS_SELECT_ALL | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = PIMUtility.EMPTYSTR.equals(tmpHasAttach) ? tmpSystemState : tmpSystemState |
// IActionFlagCons.STATUS_HAVE_ATTACH;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// // tmpSystemState = tmpPasteType > 0 ? tmpSystemState | PIMActionFlag.STATUS_HAVE_PASTE : tmpSystemState;
// tmpSystemState =
// tmpIsRead != null && tmpIsRead.booleanValue() ? tmpSystemState =
// tmpSystemState | IActionFlagCons.STATUS_HAVE_READED : tmpSystemState | IActionFlagCons.STATUS_HAVENOT_READED;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.SENDED_APP :
// tmpSystemState |= IActionFlagCons.STATUS_RECORD_SELECTED | IActionFlagCons.STATUS_MAIL |
// IActionFlagCons.STATUS_SELECT_ALL | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = PIMUtility.EMPTYSTR.equals(tmpHasAttach) ? tmpSystemState : tmpSystemState |
// IActionFlagCons.STATUS_HAVE_ATTACH;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState = pasteType > 0 ? tmpSystemState | IActionFlagCons.STATUS_HAVE_PASTE : tmpSystemState;
// tmpSystemState =
// tmpIsRead != null && tmpIsRead.booleanValue() ? tmpSystemState =
// tmpSystemState | IActionFlagCons.STATUS_HAVE_READED : tmpSystemState | IActionFlagCons.STATUS_HAVENOT_READED;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP;
// break;
// case ModelConstants.DRAFT_APP :
// tmpSystemState |= /*PIMActionFlag.STATUS_RECORD_SELECTED | */IActionFlagCons.STATUS_MAIL |
// IActionFlagCons.STATUS_SELECT_ALL | IActionFlagCons.STATUS_CAN_FIND;
// if (tmpPathCount == 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED;
// }
// else if (tmpPathCount > 4)
// {
// tmpSystemState = tmpSystemState | IActionFlagCons.STATUS_FOLDER_SELECTED | IActionFlagCons.STATUS_USER_DEFINED;
// }
// tmpSystemState = PIMUtility.EMPTYSTR.equals(tmpHasAttach) ? tmpSystemState : tmpSystemState |
// IActionFlagCons.STATUS_HAVE_ATTACH;
// tmpSystemState = tmpUndoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_UNDO;
// tmpSystemState = tmpRedoEdit == null ? tmpSystemState : tmpSystemState | IActionFlagCons.STATUS_HAVE_REDO;
// tmpSystemState =
// tmpIsRead != null && tmpIsRead.booleanValue() ? tmpSystemState =
// tmpSystemState | IActionFlagCons.STATUS_HAVE_READED : tmpSystemState | IActionFlagCons.STATUS_HAVENOT_READED;
// tmpSystemState |= IActionFlagCons.STATUS_IS_MARK | IActionFlagCons.STATUS_ALL_APP;
// break;
// default :
// tmpSystemState = tmpAccount != null && tmpAccount.size() > 0 ? IActionFlagCons.ONLY_USE_WHEN_HAS_ACCOUNT :
// IActionFlagCons.STATUS_CLOSED;
// //tmpSystemState = PIMActionFlag.STATUS_CLOSED;
// }
// }
// else
// {
// tmpSystemState = tmpAccount != null && tmpAccount.size() > 0 ? IActionFlagCons.ONLY_USE_WHEN_HAS_ACCOUNT :
// IActionFlagCons.STATUS_CLOSED;
// //tmpSystemState = PIMActionFlag.STATUS_CLOSED;
// }
// break;
// default :
// tmpSystemState = tmpAccount != null && tmpAccount.size() > 0 ? IActionFlagCons.ONLY_USE_WHEN_HAS_ACCOUNT :
// IActionFlagCons.STATUS_CLOSED;
// //tmpSystemState = PIMActionFlag.STATUS_CLOSED;
// }
// }
/**
 * 现在切换应用的时候要将db关闭后再次打开，当邮件收发的时候如此会导致错误 因此在此处设置一个标志位来使control参考不要释放，当接收或者发送邮件结束后，此 标志位需要复位，复位时候进行一次打断db连接的动作
 */
// public void setDispatchMailFlag(boolean prmDispatcher)
// {
// this.dispatchMailFlag = prmDispatcher;
// }
// //同上，可取得此标志
// public boolean getDispatchMailFlag()
// {
// return this.dispatchMailFlag;
// }
