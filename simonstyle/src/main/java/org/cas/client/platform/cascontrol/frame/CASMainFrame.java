package org.cas.client.platform.cascontrol.frame;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.UIManager;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASFrame;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascontrol.menuaction.AboutAction;
import org.cas.client.platform.cascontrol.navigation.CASNavigationPane;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.resource.international.PaneConsts;

import com.jeans.trayicon.TrayIconCallback;
import com.jeans.trayicon.TrayIconException;
import com.jeans.trayicon.TrayIconPopup;
import com.jeans.trayicon.TrayIconPopupSeparator;
import com.jeans.trayicon.TrayIconPopupSimpleItem;
import com.jeans.trayicon.WindowsTrayIcon;

/**
 * PMI主窗口 @TODO:不知为何relayout方法总是会多次被调用.可以认为被调两次是jdk的问题,但被调三次则是谁的问题:-?
 * 本窗体故意不采用Windows风格，而用中间风格，除了为了使各个平台风格一致，也因为windows风格太丑，而且Windows都已经在抛弃传统的 windows风格，我们更没有理由用了。
 */
public class CASMainFrame extends JFrame implements WindowListener, KeyEventPostProcessor, ICASFrame,
        ComponentListener, MouseListener {
    /**
     * @NOTE:为了优化启动速度，将实例化model等和界面显示无关得动作放到线程中运行。 为了使界面早点显示出来。该改动在2.0G得机器上使启动“减少”约600ms，1 在667的
     *                                             机器上约“减少”2s.同时为了防止用户在model还没有在线程中构造完成就开始操作，（多数的操作都会引发model搜索）。
     *                                             在CASControl的changeApplication方法中增加了判断－－如果model没有实例化，直接返回，不响应。
     */
    public static void main(
            String args[]) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);// 不在Main方法的开头就写这句，而在其它地方写的话将没有效果

        CASControl.ctrl.initModel();

        initFonts();

        // broken! showFlashDialog();
        System.setErr(new PrintStream(new ErrorUtil()));// 将没有捕获的exception写入errordialog
        // broken! showSystemTrayIcon();

        mainFrame = new CASMainFrame(); // 初始化主窗体并注册
        CASControl.ctrl.setMainFrame(mainFrame);

        // @NOTE:关于SetVisible和show的区别，show好像是一个在线程里是窗口显示的机制。因为
        // 像下面的代码，如果改为show的话，就不能确保SwingUtility.invakelater()方法一定生效。
        new LoginDlg(null).setVisible(true);
        if (!LoginDlg.PASSED)
            return;
        // 为各个版本做的代码=================================================
        // //正德-魔之浴
        CASControl.ctrl.changeApplication(CustOpts.custOps.APPNameVec.indexOf("Product"), 5100);// setPane方法中将确保activePane被实例化。
        mainFrame.setVisible(true); // 显示主窗体
        // 正德-批发部
        // CASControl.ctrl.changeApplication(CustOpts.custOps.APPNameVec.indexOf("Forgrounding"),
        // 5500);//setPane方法中将确保activePane被实例化。
        // mainFrame.setVisible(true); //显示主窗体
        // //显示主窗体
        // CASControl.ctrl.changeApplication(CustOpts.custOps.APPNameVec.indexOf("Pos"), 5600);//
        // setPane方法中将确保activePane被实例化。
        // 正德-海神
        // PosFrame.main(null);
        // =================================================
        // TODO:此处需要修改,具体规格还需要询问王强,是自动打开到离开时的界面,还是让用户输入密码,或是分几种情况.
        // //设置当前的视图类型。
        // final int tmpActiveApptype = tmpCustomOpt.getActiveAppType();
        // final int tmpActiveViewType = tmpCustomOpt.getActiveViewType(tmpActiveApptype);
        // final String tmpActiveFolderPath = tmpCustomOpt.getActiveFolderPath();
        // setPane(tmpActiveApptype, tmpActiveViewType,tmpActiveFolderPath);
        Thread tmpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                CASUtility.checkRegist();
                //
                // // 这些动作耗时多，不能放在启动时处理。
                // // new AutoOrganize().actionPerformed(null);
                // // CASControl.ctrl.setActionStatus();
                // // try{
                // // Class.forName("org.cas.client.platform.pimview.pimtable.PIMTable");
                // // Class.forName("org.cas.client.resource.international.pim.IntlModelConstants");
                // // }
                // // catch (ClassNotFoundException ex){
                // // /*stgo*/A.s(ex);
                // // }
            }
        }, "PIMModelInit");
        tmpThread.start();
        /**
         * @NOTE:当做安装版本的时候,应将本段以及上面的相关语句注掉,改为用由安装程序提供的更快更省的快闪屏. startWindow.dispose(); //关闭快闪屏，否则快闪屏就一直留在屏幕上了。
         */

        CustOpts.custOps.setInstallPath(mainFrame.getClass().getResource("").getPath());
    }

    /** Creates a new instance of PIMMainFrame */
    public CASMainFrame() {
        super(PaneConsts.TITLE); // 设置标题。
        initComponents(); // 初始化界面。
        // initTray();
    }

    @Override
    public void updateActionStatus() {
    }

    /**
     * 临时方法。
     * 
     * @called by: client/platform/MainPane.java
     * @called by: client/platform/pimview/dialog/NewFolderDialog.java(3); MoveFolderDialog.java(3);
     *         CopyFolderDialog.java(3); /FolderSelectDialog.java(4);
     * @called by: client/platform/CASControl/action/folderlist/DragCopyAction.java; RemoveFolderAction.java;
     *         DragMoveAction.java; RenameFolderAction.java
     * @return 文件夹列表的实例
     */
    @Override
    public CASNavigationPane getFolderPane() {
        return splitPane.getNavigationPane();
    }

    @Override
    public JPanel[] getToolBarPanes() {
        JPanel[] tmpPanes = new JPanel[1];
        tmpPanes[0] = northPane;
        return tmpPanes;
    }

    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    }

    @Override
    public void componentMoved(
            ComponentEvent e) {
        // 每次移动都保存进hash表，影响性能；而且最后存盘如果出错一样徒劳。 CustomOptions.custOps.setFrameX(getX());
        // 所以屏蔽，改为在退出时一次性保存，不知道会不会受最小化状态影响？ CustomOptions.custOps.setFrameY(getY());
    }

    @Override
    public void componentShown(
            ComponentEvent e) {
    }

    @Override
    public void componentHidden(
            ComponentEvent e) {
    }

    // Windows 监听事件处理-------------------------------------------------------------------------------------
    @Override
    public void windowOpened(
            WindowEvent e) {
    }

    @Override
    public void windowClosed(
            WindowEvent e) {
    }

    @Override
    public void windowIconified(
            WindowEvent e) {
    }

    @Override
    public void windowDeiconified(
            WindowEvent e) {
    }

    @Override
    public void windowActivated(
            WindowEvent e) {
    }

    @Override
    public void windowDeactivated(
            WindowEvent e) {
    }

    @Override
    public void windowClosing(
            WindowEvent e) {
        setVisible(false); // 先隐藏视图，为的是给用户感觉关闭得比较快。
    }

    @Override
    public boolean postProcessKeyEvent(
            KeyEvent e) {
        eventDispatched(e);
        return e.isConsumed();
    }

    /**
     * 处理全局快捷键.
     * 
     * @NOTE:临时写死,试验性实现了mp3的播放功能. 等做到该模块的时候应该改为反射实现.
     */
    public void eventDispatched(
            AWTEvent event) {
    }

    @Override
    public void mouseClicked(
            MouseEvent e) {
    }

    @Override
    public void mouseEntered(
            MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton)
            ((JButton) source).setBorder(BorderFactory.createEtchedBorder());
    }

    @Override
    public void mouseExited(
            MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton)
            ((JButton) source).setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    public void mousePressed(
            MouseEvent e) {
    }

    @Override
    public void mouseReleased(
            MouseEvent e) {
    }

    @Override
    protected void processWindowEvent(
            WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
        }
        super.processWindowEvent(e);
    }

    private String getTime(
            int length) {
        int min = length / 60000;
        int second = (length % 60000) / 1000;
        StringBuffer result = new StringBuffer(6);
        if (min < 10)
            result.append('0');
        result.append(min).append(':');

        if (second < 10)
            result.append('0');
        result.append(second);

        return result.toString();
    }

    /** @NOTE:如果是预览中用,则因为没有工具条被添加,所以不会进入循环,本法不做事. */
    @Override
    public void reLayout() {
        if (getWidth() < 600) // 窗体尺寸------------
            setSize(600, getHeight());
        if (getHeight() < 400)
            setSize(getWidth(), 400);

        int x = 0; // 工具条-------------
        int y = 0;
        Component tmpCom;
        int i = 0;
        int tmpLineCount = 0; // 用来记下上一个工具条用的行数,以便调整y值.
                              // @NOTE:初始化为0防止了第一个就是大工具条.
        for (int len = northPane.getComponentCount(); i < len; i++) {
            tmpCom = northPane.getComponent(i);
            int tmpW = tmpCom.getPreferredSize().width;
            if (x + tmpW <= getWidth()) { // 当前行能够布下的小工具条.
                tmpCom.setBounds(x, y, tmpW, CustOpts.BAR_HEIGHT);
                tmpLineCount = 1;
            } else { // 当前行布不下去的大工具条.
                x = 0; // 大工具条和大工具条之后的工具条都从x的0位置开始布.
                y += CustOpts.BAR_HEIGHT * tmpLineCount; // 根据上个工具条占的行数调整y值.
                tmpLineCount = tmpW / getWidth() + 1; // 得到当前的大工具条占的行数.
                tmpCom.setBounds(x, y, tmpW, CustOpts.BAR_HEIGHT * tmpLineCount);
                if (i > 0) {
                    Component tmpC = northPane.getComponent(i - 1);
                    tmpC.setBounds(tmpC.getX(), tmpC.getY(), getWidth() - tmpC.getX(), CustOpts.BAR_HEIGHT);
                }
            }
            x = tmpCom.getX() + tmpCom.getWidth(); // 调整新的x. @NOTE:y不需要调整,它将在发现本行不能摆开的时候调整.
        }
        if (i > 0) {
            Component tmpC = northPane.getComponent(i - 1);
            tmpC.setBounds(tmpC.getX(), tmpC.getY(), getWidth() - tmpC.getX(), tmpC.getHeight());
            northPane.setPreferredSize(new Dimension(getWidth(), y + tmpC.getHeight()));
        }

        x = 0; // 状态栏---------
        y = 0;
        i = 0;
        for (int len = southPane.getComponentCount(); i < len; i++) {
            tmpCom = southPane.getComponent(i);
            int tmpW = tmpCom.getPreferredSize().width;
            if (x + tmpW <= getWidth())
                tmpCom.setBounds(x, y, tmpW, CustOpts.LBL_HEIGHT);
            else {
                x = 0;
                y += CustOpts.BAR_HEIGHT;
                tmpCom.setBounds(x, y, tmpW, CustOpts.LBL_HEIGHT);
                if (i > 0) {
                    Component tmpC = southPane.getComponent(i - 1);
                    tmpC.setBounds(tmpC.getX(), tmpC.getY(), getWidth() - tmpC.getX(), CustOpts.LBL_HEIGHT);
                }
            }
            x = tmpCom.getX() + tmpCom.getWidth(); // 调整新的x. @NOTE:y不需要调整,它将在发现本行不能摆开的时候调整.
        }
        if (i > 0) {
            Component tmpC = southPane.getComponent(i - 1);
            tmpC.setBounds(tmpC.getX(), tmpC.getY(), getWidth() - tmpC.getX(), CustOpts.LBL_HEIGHT);
            southPane.setPreferredSize(new Dimension(getWidth(), y + CustOpts.LBL_HEIGHT));
        }

        if (this.isVisible() && (mainPane.getHeight() < splitPane.getDividerLocation() + 15)) { // 导航面板---------
            splitPane.setDividerLocation(mainPane.getHeight());
            // splitPane.getDividerLocation() - PIMSplitPane.UNIT_HIGHT);
        }
        invalidate();
        validate();
        repaint();
    }

    public JPanel[] getStatusBarPanes() {
        JPanel[] tmpPanes = new JPanel[1];
        tmpPanes[0] = southPane;
        return tmpPanes;
    }

    public void setNavigationVisible(
            boolean isVisible) {
        if (isVisible)
            centerPanel.setDividerLocation(dividerLocation);
        else {
            int tmpOldPos = centerPanel.getDividerLocation();
            if (tmpOldPos > 5) // 如果本来就没有显示，那么就不用记录Pos了，等再次显示的时候，直接定位到150即可。
                dividerLocation = tmpOldPos;
            centerPanel.setDividerLocation(0);
        }
    }

    /** @called by:PIMBarManager. */
    public MainPane getMainPane() {
        return mainPane;
    }

    public CASSplitPane getSplitPane() {
        return splitPane;
    }

    // 初始化界面。标题已经设置，本方法设置的有：窗体图标；Border布局：中部的pimView；
    // Container的布局：
    // |------------------------------Container-------------------------------|
    // |.............................菜单&工具条..............................|
    // |----------------------------------------------------------------------|
    // |菜||-------------------------CenterPane---------------------------||菜 |
    // |单||--------------------------------------------------------------||单 |
    // | &||.......... ||.................................................||& |
    // |工|| 日期选择区 ||.................................................||工 |
    // |具||.......... ||.................................................||具 |
    // |条||.......... ||.......... MainPane..............................||条 |
    // |..||-SplitPane-||................................................ ||..|
    // |..||...........||................................................ ||..|
    // |..||..导航面板..||.................................................||..|
    // |..||...........||................................................ ||..|
    // |..||...........||................................................ ||..|
    // |..||...........||................................................ ||..|
    // |..||...........||................................................ ||..|
    // |..||...........||................................................ ||..|
    // |..||--------------------------------------------------------------||..|
    // |----------------------------------------------------------------------|
    // |.............................. 菜单&工具条.............................|
    // |-----------------------------------------------------------------------|
    /*
     * 初始化工具 TODO:默认应该初始化好菜单条和站驻状态为all的工具条.其它工具条则应该在切换到各个应用的时候向应用询问有什么工具条条要增加.
     * 为all当应用不再为当前应用时(不一定是被关闭了,比如联系人,邮件等一类应用就是不会随着失去焦点而被关闭的.)其对应的其它工具条 (即站驻属性不的工具条)随之被关闭.取而代之的是新的应用对应的工具条.
     * 所有的工具条应该显示在上下左右哪个面板上,由记录在其 自身上的属性决定.对应记录于各个应用包下的
     * @param 保存容器
     */
    private void initComponents() {
        setIconImage(CustOpts.custOps.getFrameLogoImage()); // 设置主窗体的LOGO。
        setBounds(
                // 设置主窗口位置尺寸;此句需先于initMenu(),因为menu的显示时，会根据主窗体的大小设一次menu的长宽。
                CustOpts.custOps.getFrameX(), CustOpts.custOps.getFrameY(), CustOpts.custOps.getFrameWidth(),
                CustOpts.custOps.getFrameHeight());
        // setExtendedState(JFrame.MAXIMIZED_BOTH);//CustOpts.custOps.getFrameState());

        // 初始化----------------
        mainPane = CASControl.ctrl.getMainPane(); // centerPanel居中放置的是MainPane。
        splitPane = new CASSplitPane(this, JSplitPane.VERTICAL_SPLIT); // centerPanel居左放置的是一个上下结构的SplitPane。
        centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); // 放置于Container的中部++++++++++++++
        JMenuBar menuBar = new CASMenuBar(this);
        northPane = new JPanel();
        southPane = new JPanel();

        // 属性----------------
        centerPanel.setBorder(null);
        centerPanel.setDividerSize(4);
        northPane.setLayout(null);
        southPane.setLayout(null);
        getContentPane().setLayout(new BorderLayout());
        setNavigationVisible(CustOpts.custOps.isNavigationVisible());
        CustOpts.custOps.setBaseBookHide(false);
        // 搭建----------------
        setJMenuBar(menuBar);
        JToolBar[] tmpToolbars = mainPane.getStaticToolbars();
        if (tmpToolbars != null)
            for (int i = 0, len = tmpToolbars.length; i < len; i++)
                northPane.add(tmpToolbars[i]);
        JPanel[] tmpStatebars = mainPane.getStaticStatebars();
        if (tmpStatebars != null)
            for (int i = 0, len = tmpStatebars.length; i < len; i++)
                southPane.add(tmpStatebars[i]);
        centerPanel.setLeftComponent(splitPane);
        centerPanel.setRightComponent(mainPane);
        getContentPane().add(northPane, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER); // Container的中部布局完毕
        getContentPane().add(southPane, BorderLayout.SOUTH);

        // 监听----------------
        getContentPane().addComponentListener(this);
        addWindowListener(this); // 加窗口退出事件监听。也是程序退出监听。
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
    }

    private void initTray() {
        try {
            WindowsTrayIcon.setWindowsMessageCallback(new WindowsMessageCallback());
            m_hIcon = new WindowsTrayIcon(CustOpts.custOps.getFrameLogoImage(), 16, 16);
            m_hIcon.setPopup(makePopup());
            m_hIcon.setVisible(true);
            WindowsTrayIcon.keepAlive();
        } catch (TrayIconException | InterruptedException e) {
            ErrorUtil.write("Error occured when init tray icon: " + e.getStackTrace());
        }
    }

    // Create the popup menu for each Tray Icon (on right mouse click)
    private TrayIconPopup makePopup() {
        // Make new popup menu
        TrayIconPopup popup = new TrayIconPopup();
        // Add about, configure & exit item
        TrayIconPopupSimpleItem item = new TrayIconPopupSimpleItem("关于(&A)...");
        item.addActionListener(new AboutAction());
        popup.addMenuItem(item);
        // Add configure item
        item = new TrayIconPopupSimpleItem("还原(&S)");
        item.addActionListener(new ConfigureListener());
        popup.addMenuItem(item);
        // Add a separator
        TrayIconPopupSeparator sep = new TrayIconPopupSeparator();
        popup.addMenuItem(sep);
        // Add exit item
        item = new TrayIconPopupSimpleItem("退出(&X)");
        item.addActionListener(new ExitListener());
        popup.addMenuItem(item);
        return popup;
    }

    private class WindowsMessageCallback implements TrayIconCallback {
        @Override
        public int callback(
                int param) {
            // Param contains the integer value send with sendWindowsMessage(appName,param)
            ErrorUtil.write("[Other instance started (parameter: " + param + ")].");
            setVisible(true);
            toFront();
            requestFocus();
            return 4321;
        }
    }

    // Callback listener handles exit button / exit popup menu
    private class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(
                ActionEvent evt) {
            CASControl.ctrl.getMainFrame().setVisible(false); // 先隐藏视图，为的是给用户感觉关闭得比较快。
            try {
                CASControl.ctrl.exitSystem(); // 保存所有状态和数据后退出。
            } catch (Exception exp) {
                ErrorUtil.write(exp);
                System.exit(0);
            }
        }
    }

    // Callback listener handles about button
    private class ConfigureListener implements ActionListener {
        @Override
        public void actionPerformed(
                ActionEvent evt) {
            mainFrame.setVisible(true);
        }
    }

    // TODO:讨论是否可以通过这种设置来统一调整整个程序的风格，那么就要求绘制的代码不要取设置Font粗细等
    // 所有UIManager中能设置的属性。前提时先了解UIManager共可以管理哪些属性。
    private static void initFonts() {
        Font tFont = CustOpts.custOps.getFontOfDefault();
        UIManager.put("Button.font", tFont);
        UIManager.put("CheckBox.font", tFont);
        UIManager.put("CheckBoxMenuItem.acceleratorFont", tFont);
        UIManager.put("CheckBoxMenuItem.font", tFont);
        UIManager.put("ColorChooser.font", tFont);
        UIManager.put("ComboBox.font", tFont);
        UIManager.put("DesktopIcon.font", tFont);
        UIManager.put("EditorPane.font", tFont);
        UIManager.put("FormattedTextField.font", tFont);
        UIManager.put("InternalFrame.titleFont", tFont);
        UIManager.put("Label.font", tFont);
        UIManager.put("List.font", tFont);
        UIManager.put("Menu.acceleratorFont", tFont);
        UIManager.put("Menu.font", tFont);
        UIManager.put("MenuBar.font", tFont);
        UIManager.put("MenuItem.acceleratorFont", tFont);
        UIManager.put("MenuItem.font", tFont);
        UIManager.put("OptionPane.font", tFont);
        UIManager.put("Panel.font", tFont);
        UIManager.put("PasswordField.font", tFont);
        UIManager.put("PopupMenu.font", tFont);
        UIManager.put("ProgressBar.font", tFont);
        UIManager.put("RadioButton.font", tFont);
        UIManager.put("RadioButtonMenuItem.acceleratorFont", tFont);
        UIManager.put("RadioButtonMenuItem.font", tFont);
        UIManager.put("ScrollPane.font", tFont);
        UIManager.put("Spinner.font", tFont);
        UIManager.put("TabbedPane.font", tFont);
        UIManager.put("Table.font", tFont);
        UIManager.put("TableHeader.font", tFont);
        UIManager.put("TextArea.font", tFont);
        UIManager.put("TextField.font", tFont);
        UIManager.put("TextPane.font", tFont);
        UIManager.put("TitledBorder.font", tFont);
        UIManager.put("ToggleButton.font", tFont);
        UIManager.put("ToolBar.font", tFont);
        UIManager.put("ToolTip.font", tFont);
        UIManager.put("Tree.font", tFont);
        UIManager.put("Viewport.font", tFont);
    }

    /*
     * @NOTE:当做安装版本的时候,应将本段以及后面的相关dispose语句注掉,改为用由安装程序提供的更快更省的快闪屏.
     */
    private static void showFlashDialog() {
        JLabel tmpStartLabel =
                new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                        PIMPool.pool.getClass().getResource(PaneConsts.IAMGE_PATH.concat("Start1.gif")))));
        Dimension tmpIconSize = tmpStartLabel.getPreferredSize();
        JWindow startWindow = new JWindow((Frame) null);
        startWindow.getContentPane().add(tmpStartLabel);
        Dimension tmpSrcSize = Toolkit.getDefaultToolkit().getScreenSize();
        startWindow.setBounds((tmpSrcSize.width - tmpIconSize.width) / 2, (tmpSrcSize.height - tmpIconSize.height) / 2,
                tmpIconSize.width, tmpIconSize.height);
        startWindow.setVisible(true);
    }

    private static void showSystemTrayIcon() {
        try { // 显示系统栏图标
            WindowsTrayIcon.initTrayIcon("SamHagen");
        } catch (Exception e) {
            ErrorUtil.write("Error occured when init the tray icon: " + e.getStackTrace());
        }
    }

    // variables------------------------------------------------------------------
    private WindowsTrayIcon m_hIcon;
    private JPanel northPane;
    private JPanel southPane;
    private JSplitPane centerPanel;
    private MainPane mainPane;
    private int dividerLocation = 150;
    public static CASMainFrame mainFrame;
    private static CASSplitPane splitPane;
}

// /**该方法在某些情况下可能比实现ComponentListener接口好，因为该方法缺点是多执行一次判断，优点是少Load一个接口类。
// * 该方法的缺点是使程序结构复杂，可读性下降，不易维护。
// * 事件的发送者和监听者之间缺乏明确的对应关系。
// * Process the frame Component event.
// * @param e the component event.
// */
// protected void processComponentEvent(ComponentEvent e)
// {
// super.processComponentEvent(e);
// }

