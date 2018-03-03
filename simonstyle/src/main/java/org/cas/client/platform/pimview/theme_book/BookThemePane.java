package org.cas.client.platform.pimview.theme_book;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.cascontrol.menuaction.CircumViewAction;
import org.cas.client.platform.cascontrol.menuaction.CloseBookLeftAction;
import org.cas.client.platform.cascontrol.menuaction.CloseBookRightAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.PicturePane;
import org.cas.client.resource.international.PaneConsts;

public class BookThemePane extends PicturePane implements MouseListener, MouseMotionListener, PaneConsts {
    private static int BOOKEDGE = 2; // 书的Tab页与书的边界（表示书的厚度的修饰）之间的间隔

    /**
     * 构造器 @for:emo.pim.MainPane. 由ActivityTodayPane调用，属于今日面板
     * 
     * @param prmImgPath
     *            <code>Image</code>
     * @see java.awt.Image
     */
    public BookThemePane(Image prmImgPath) {
        super(prmImgPath);

        leftTabPane = new EVerTabPane(SwingConstants.LEFT); // 左侧的TabPane
        rightTabPane = new EVerTabPane(SwingConstants.RIGHT); // 右侧的TabPane
        leftEdge = new EPageEdge(true); // 左边界
        leftBottom = new EPageBottom(true); // 左底边界
        binderClam = new EBinderClamp(); // 夹缝装订夹视图
        rightEdge = new EPageEdge(false); // 右边界
        rightBottom = new EPageBottom(false); // 右底边界
        coverPane = new App_Cover();
        activeApp = coverPane;
        // PaneManager manager = PaneManager.getInstance();
        //
        // activityTitle = new EActivityTodayTitle(null);//manager.getImage(TODAY_TITLE,true));
        // activityTitle.addHotButton(CUSTOM_TODAY);
        // add(activityTitle);
        //
        // activityTitle.addPropertyChangeListener(new PropertyChangeListener()
        // {
        // /* select a node in navigator tree */
        // public void propertyChange(PropertyChangeEvent evt)
        // {
        // changeActiveTodayTitle((String) evt.getNewValue());
        // }
        // });
        //
        // activityToday = new ActivityTodayPane();
        // add(activityToday);

        int bookMarkerCount = CustOpts.custOps.APPCapsVec.size(); // TAB面板的数量
        for (int i = 0; i < bookMarkerCount; ++i) {
            leftTabPane.addTab((String) CustOpts.custOps.APPCapsVec.get(i), CustOpts.custOps.getTabFGAt(i));
            rightTabPane.addTab((String) CustOpts.custOps.APPCapsVec.get(i), CustOpts.custOps.getTabFGAt(i));
            leftTabPane.setBackgroundAt(i, CustOpts.custOps.getTabBGAt(i));
            rightTabPane.setBackgroundAt(i, CustOpts.custOps.getTabBGAt(i));
        }

        addMouseListener(this);
        leftEdge.addMouseListener(this); // 左右页边界/TabPane加监听器。
        rightEdge.addMouseListener(this);
        binderClam.addMouseListener(this);
        leftTabPane.addMouseListener(this);
        rightTabPane.addMouseListener(this);
        binderClam.addMouseMotionListener(this);

        add(leftTabPane);
        add(rightTabPane);
        add(leftEdge);
        add(leftBottom);
        add(rightEdge);
        add(rightBottom);
        add(binderClam); // 必须在最后加，防止它在前面当着别人。

        setLayout(this);
    }

    /**
     * 面板布局效果如下： | 　|| 　| |C| | 　 || 　| |左 ||左 | |l| |右 ||右 | |T 　||边 | |a| |边 ||T 　| |a 　||界 | |m| |界 ||a 　| |b 　|| 　|
     * |p| | ||b | |面 || 　|____________| |___________| 　||面 | |板 || 　| 左底边界 　| |　 右底边界 | 　||板 |
     *
     * Clamp上有绘制别针的功能，和cover上专门负责绘制别针的层的功能重复了， 因为考虑到可能CoverPane较少会用到，且少一层效率较高，所以当书本不是关闭状态时， 其上的别针是在EBinder类中直接画的。
     */
    public void layoutContainer(
            Container prmContainer) {
        int tmpWidth = getWidth(); // 该变量最开始被初始化为BaseBookPane的宽度。
        int tmpHeight = getHeight();
        if (CustOpts.custOps.isBaseBookHide())// 无书本风格。
        {
            leftTabPane.setBounds(0, 0, 0, 0);
            // 最左边的组件leftTabPane布局完毕-----------------------------------------------
            leftEdge.setBounds(0, 0, 0, 0);
            // 最左侧的边界布局完毕-----------------------------------------------------------
            leftBottom.setBounds(0, 0, 0, 0);
            // 左下的边界布局完毕-----------------------------------------------------------
            rightTabPane.setBounds(0, 0, 0, 0);
            // 最右边的组件rightTabPane布局完毕-----------------------------------------------
            rightEdge.setBounds(0, 0, 0, 0);
            // 最右侧的边界布局完毕-----------------------------------------------------------
            rightBottom.setBounds(0, 0, 0, 0);
            // 右下的边界布局完毕-----------------------------------------------------------
            if (activeTabIndex == ModelCons.CLOSED) // 关闭状态。
            {
                binderClam.setBounds(0, 0, 0, 0);// TODO:设法改进一下，最好只有BaseBookPane认识这个夹子。
                // 最中间的夹板布局完毕-----------------------------------------------------------

                ((JComponent) activeApp).setBounds(0, 0, 0, 0);
                // activeView布局完毕-----------------------------------------------------------
            } else {
                if (CustOpts.custOps.isPreviewShown()) {
                    if (CustOpts.custOps.isViewTopAndDown()) {
                        tmpPos = (int) (tmpHeight * CustOpts.custOps.getSplitHeightScale());
                        if (tmpPos < 100)
                            tmpPos = 100;
                        else if (tmpPos > tmpHeight - 100)
                            tmpPos = tmpHeight - 100;
                        CustOpts.custOps.setSplitHeight(tmpPos);
                        binderClam.setBounds(0, tmpPos, tmpWidth, binderClam.getBinderWidth());
                    } else {
                        tmpPos = (int) (tmpWidth * CustOpts.custOps.getSplitHeightScale());
                        if (tmpPos < 100)
                            tmpPos = 100;
                        else if (tmpPos > tmpWidth - 100)
                            tmpPos = tmpWidth - 100;
                        CustOpts.custOps.setSplitHeight(tmpPos);
                        binderClam.setBounds(tmpPos, 0, binderClam.getBinderWidth(), tmpHeight);
                    }
                } else {
                    binderClam.setBounds(0, 0, 0, 0);
                }
                binderClam.setSpinsVisible(true);
                // 最中间的夹板布局完毕-----------------------------------------------------------

                ((JComponent) activeApp).setBounds(0, 0, tmpWidth, tmpHeight);
                // activeView布局完毕-----------------------------------------------------------
            }
        } else // 书本风格。
        {
            if (CustOpts.custOps.isViewTopAndDown())// 书本上下
            {
                if (activeTabIndex == ModelCons.CLOSED) // 书本上下＋向右的关闭状态。
                {
                    if (isLeftClosed) // 书本上下＋向左的关闭状态。
                    {
                        leftTabPane.setBounds(0, 0, tmpWidth, rightTabPane.tabHeight);
                        // 最左边的组件leftTabPane布局完毕-----------------------------------------------
                        leftEdge.setBounds(0, 0, 0, 0);
                        // 最左侧的边界布局完毕-----------------------------------------------------------
                        leftBottom.setBounds(0, 0, 0, 0);
                        // 左下的边界布局完毕-----------------------------------------------------------
                        rightTabPane.setBounds(0, 0, 0, 0);
                        // 最右边的组件rightTabPane布局完毕-----------------------------------------------
                        rightEdge.setBounds(0, 0, 0, 0);
                        // 最右侧的边界布局完毕-----------------------------------------------------------
                        rightBottom.setBounds(0, 0, 0, 0);
                        // 右下的边界布局完毕-----------------------------------------------------------
                        tmpPos = (int) (tmpHeight * CustOpts.custOps.getSplitHeightScale());
                        if (tmpPos < 100)
                            tmpPos = 100;
                        else if (tmpPos > tmpHeight - 100)
                            tmpPos = tmpHeight - 100;
                        CustOpts.custOps.setSplitHeight(tmpPos - rightTabPane.tabHeight);

                        binderClam.setBounds(BOOKEDGE, tmpPos, tmpWidth - leftEdge.getLines() / 2 - BOOKEDGE * 2,
                                binderClam.getBinderWidth());
                        binderClam.setSpinsVisible(false);
                        // 最中间的夹板布局完毕-----------------------------------------------------------

                        // 得出ActiveView页面宽。
                        // tmpWidth变量在最上下侧的边界和Tab被布局完后被初始化为Activeview的宽度。
                        ((JComponent) activeApp).setBounds(0, rightTabPane.tabHeight, tmpWidth, tmpHeight
                                - rightTabPane.tabHeight * 2);
                        // activeView布局完毕----------------------------------------------------------
                    } else {
                        leftTabPane.setBounds(0, 0, 0, 0);
                        // 最左边的组件leftTabPane布局完毕---------------------------------------------
                        leftEdge.setBounds(0, 0, 0, 0);
                        // 最左侧的边界布局完毕--------------------------------------------------------
                        leftBottom.setBounds(0, 0, 0, 0);
                        // 左下的边界布局完毕----------------------------------------------------------

                        rightTabPane.setBounds(0, tmpHeight - rightTabPane.tabHeight, tmpWidth, rightTabPane.tabHeight);
                        // 最右边的组件rightTabPane布局完毕---------------------------------------------
                        rightEdge.setBounds(0, 0, 0, 0);
                        // 最右侧的边界布局完毕---------------------------------------------------------
                        rightBottom.setBounds(0, 0, 0, 0);
                        // 右下的边界布局完毕-----------------------------------------------------------
                        tmpPos = (int) (tmpHeight * CustOpts.custOps.getSplitHeightScale());
                        if (tmpPos < 100)
                            tmpPos = 100;
                        else if (tmpPos > tmpHeight - 100)
                            tmpPos = tmpHeight - 100;
                        CustOpts.custOps.setSplitHeight(tmpPos - rightTabPane.tabHeight);

                        binderClam.setBounds(BOOKEDGE, tmpPos, tmpWidth - leftEdge.getLines() / 2 - BOOKEDGE * 2,
                                binderClam.getBinderWidth());
                        binderClam.setSpinsVisible(false);
                        // 最中间的夹板布局完毕----------------------------------------------------------

                        // //得出ActiveView页面宽。
                        // tmpWidth -= leftBottom.getLines() + 2 * BOOKEDGE;
                        // //该变量在最左侧和最右侧的边界和Tab被布局完后被初始化为Activeview的宽度。
                        // tmpHeight -= rightTabPane.tabHeight * 2 + BOOKEDGE * 2 + leftEdge.getLines() * 2;

                        ((JComponent) activeApp).setBounds(0, rightTabPane.tabHeight, tmpWidth, tmpHeight
                                - rightTabPane.tabHeight * 2);
                        // activeView布局完毕-----------------------------------------------------------
                    }

                } else// 书本上下＋非关闭状态。
                {
                    tmpPos = (int) (tmpHeight * CustOpts.custOps.getSplitHeightScale());
                    if (tmpPos < 100)
                        tmpPos = 100;
                    else if (tmpPos > tmpHeight - 100)
                        tmpPos = tmpHeight - 100;
                    CustOpts.custOps.setSplitHeight(tmpPos - rightTabPane.tabHeight - leftEdge.getLines() - BOOKEDGE);

                    leftTabPane.setBounds(0, 0, tmpWidth, rightTabPane.tabHeight);
                    // 最左边的组件leftTabPane布局完毕-----------------------------------------------
                    leftEdge.setBounds(BOOKEDGE, rightTabPane.tabHeight + BOOKEDGE, tmpWidth - 2 * BOOKEDGE,
                            leftEdge.getLines());
                    // 最左侧的边界布局完毕-----------------------------------------------------------
                    leftBottom.setBounds(tmpWidth - leftBottom.getLines() - BOOKEDGE,
                            rightTabPane.tabHeight + BOOKEDGE, leftEdge.getLines() / 2, tmpPos - rightTabPane.tabHeight
                                    - BOOKEDGE + binderClam.getBinderWidth() / 2 - 5);
                    // 左下的边界布局完毕-----------------------------------------------------------
                    rightTabPane.setBounds(0, tmpHeight - rightTabPane.tabHeight, tmpWidth, rightTabPane.tabHeight);
                    // 最右边的组件rightTabPane布局完毕-----------------------------------------------
                    rightEdge.setBounds(BOOKEDGE, tmpHeight - rightTabPane.tabHeight - leftEdge.getLines() - BOOKEDGE,
                            tmpWidth - 2 * BOOKEDGE, leftEdge.getLines());
                    // 最右侧的边界布局完毕-----------------------------------------------------------
                    rightBottom.setBounds(tmpWidth - leftBottom.getLines() - BOOKEDGE,
                            tmpPos + binderClam.getBinderWidth() / 2 + 5, leftEdge.getLines() / 2, tmpHeight - tmpPos
                                    - binderClam.getBinderWidth() / 2 - 5 - rightTabPane.tabHeight - BOOKEDGE);
                    // 右下的边界布局完毕-----------------------------------------------------------
                    binderClam.setBounds(BOOKEDGE, tmpPos, tmpWidth - leftEdge.getLines() / 2 - BOOKEDGE * 2,
                            binderClam.getBinderWidth());
                    binderClam.setSpinsVisible(true);
                    // 得出ActiveView页面宽。
                    tmpWidth -= leftBottom.getLines() + 2 * BOOKEDGE;
                    // 该变量在最左侧和最右侧的边界和Tab被布局完后被初始化为Activeview的宽度。
                    tmpHeight -= rightTabPane.tabHeight * 2 + BOOKEDGE * 2 + leftEdge.getLines() * 2;

                    ((JComponent) activeApp).setBounds(BOOKEDGE,
                            rightTabPane.tabHeight + BOOKEDGE + leftEdge.getLines(), tmpWidth, tmpHeight);
                    // activeView布局完毕-----------------------------------------------------------
                }
            } else// 书本左右。
            {
                if (activeTabIndex == ModelCons.CLOSED) // 书本左右＋向右的关闭状态。
                {
                    if (this.isLeftClosed) {
                        leftTabPane.setBounds(0, 0, rightTabPane.tabHeight, tmpHeight);
                        // 最左边的组件leftTabPane布局完毕-----------------------------------------------
                        leftEdge.setBounds(0, 0, 0, 0);
                        // 最左侧的边界布局完毕-----------------------------------------------------------
                        leftBottom.setBounds(0, 0, 0, 0);
                        // 左下的边界布局完毕-----------------------------------------------------------
                        rightTabPane.setBounds(0, 0, 0, 0);
                        // 最右边的组件rightTabPane布局完毕-----------------------------------------------
                        rightEdge.setBounds(0, 0, 0, 0);
                        // 最右侧的边界布局完毕-----------------------------------------------------------
                        rightBottom.setBounds(0, 0, 0, 0);
                        // 右下的边界布局完毕-----------------------------------------------------------
                        tmpPos = (int) (tmpWidth * CustOpts.custOps.getSplitHeightScale());
                        if (tmpPos < 100)
                            tmpPos = 100;
                        else if (tmpPos > tmpWidth - 100)
                            tmpPos = tmpWidth - 100;
                        CustOpts.custOps.setSplitHeight(tmpPos - rightTabPane.tabHeight);

                        binderClam.setBounds(tmpPos, BOOKEDGE, binderClam.getBinderWidth(),
                                tmpHeight - leftEdge.getLines() / 2 - BOOKEDGE * 2);
                        binderClam.setSpinsVisible(false); // 不显示其中间的别针，因为
                        // 最中间的夹板布局完毕-----------------------------------------------------------

                        // 得出ActiveView页面宽。
                        tmpWidth -= rightTabPane.tabHeight * 2; // 该变量在最左侧和最右侧的边界和Tab被布局完后被初始化为Activeview的宽度。
                        ((JComponent) activeApp).setBounds(rightTabPane.tabHeight, 0, tmpWidth, tmpHeight);
                        // activeView布局完毕----------------------------------------------------------
                    } else {
                        leftTabPane.setBounds(0, 0, 0, 0);
                        // 最左边的组件leftTabPane布局完毕---------------------------------------------
                        leftEdge.setBounds(0, 0, 0, 0);
                        // 最左侧的边界布局完毕--------------------------------------------------------
                        leftBottom.setBounds(0, 0, 0, 0);
                        // 左下的边界布局完毕----------------------------------------------------------
                        rightTabPane.setBounds(tmpWidth - rightTabPane.tabHeight, 0, rightTabPane.tabHeight, tmpHeight);
                        // 最右边的组件rightTabPane布局完毕---------------------------------------------
                        rightEdge.setBounds(0, 0, 0, 0);
                        // 最右侧的边界布局完毕---------------------------------------------------------
                        rightBottom.setBounds(0, 0, 0, 0);
                        // 右下的边界布局完毕-----------------------------------------------------------
                        tmpPos = (int) (tmpWidth * CustOpts.custOps.getSplitHeightScale());
                        if (tmpPos < 100)
                            tmpPos = 100;
                        else if (tmpPos > tmpWidth - 100)
                            tmpPos = tmpWidth - 100;
                        CustOpts.custOps.setSplitHeight(tmpPos - rightTabPane.tabHeight);

                        binderClam.setBounds(tmpPos, BOOKEDGE, binderClam.getBinderWidth(),
                                tmpHeight - leftEdge.getLines() / 2 - BOOKEDGE * 2);
                        binderClam.setSpinsVisible(false);
                        // 最中间的夹板布局完毕----------------------------------------------------------

                        // 得出ActiveView页面宽。
                        tmpWidth -= rightTabPane.tabHeight * 2; // 该变量在最左侧和最右侧的边界和Tab被布局完后被初始化为Activeview的宽度。
                        ((JComponent) activeApp).setBounds(rightTabPane.tabHeight, 0, tmpWidth, tmpHeight);
                        // activeView布局完毕-----------------------------------------------------------
                    }
                } else// 书本左右＋非关闭
                {
                    tmpPos = (int) (tmpWidth * CustOpts.custOps.getSplitHeightScale());
                    if (tmpPos < 100)
                        tmpPos = 100;
                    else if (tmpPos > tmpWidth - 100)
                        tmpPos = tmpWidth - 100;
                    CustOpts.custOps.setSplitHeight(tmpPos - rightTabPane.tabHeight - leftEdge.getLines() - BOOKEDGE);
                    leftTabPane.setBounds(0, 0, rightTabPane.tabHeight, tmpHeight);
                    // 最左边的组件leftTabPane布局完毕-----------------------------------------------
                    leftEdge.setBounds(rightTabPane.tabHeight + BOOKEDGE, BOOKEDGE, leftEdge.getLines(), tmpHeight - 2
                            * BOOKEDGE);
                    // 最左侧的边界布局完毕-----------------------------------------------------------
                    leftBottom.setBounds(rightTabPane.tabHeight + BOOKEDGE, tmpHeight - leftBottom.getLines()
                            - BOOKEDGE, tmpPos - rightTabPane.tabHeight - BOOKEDGE + binderClam.getBinderWidth() / 2
                            - 5, leftEdge.getLines() / 2);
                    // 左下的边界布局完毕-----------------------------------------------------------
                    rightTabPane.setBounds(tmpWidth - rightTabPane.tabHeight, 0, rightTabPane.tabHeight, tmpHeight);
                    // 最右边的组件rightTabPane布局完毕-----------------------------------------------
                    rightEdge.setBounds(tmpWidth - rightTabPane.tabHeight - leftEdge.getLines() - BOOKEDGE, BOOKEDGE,
                            leftEdge.getLines(), tmpHeight - 2 * BOOKEDGE);
                    // 最右侧的边界布局完毕-----------------------------------------------------------
                    rightBottom
                            .setBounds(tmpPos + binderClam.getBinderWidth() / 2 + 5, tmpHeight - leftBottom.getLines()
                                    - BOOKEDGE, tmpWidth - tmpPos - binderClam.getBinderWidth() / 2 - 5
                                    - rightTabPane.tabHeight - BOOKEDGE, leftEdge.getLines() / 2);
                    // 右下的边界布局完毕-----------------------------------------------------------

                    binderClam.setBounds(tmpPos, BOOKEDGE, binderClam.getBinderWidth(), tmpHeight - leftEdge.getLines()
                            / 2 - BOOKEDGE * 2);
                    binderClam.setSpinsVisible(true);
                    // 最中间的夹板布局完毕-----------------------------------------------------------

                    // 得出ActiveView页面宽。
                    tmpWidth -= rightTabPane.tabHeight * 2 + BOOKEDGE * 2 + leftEdge.getLines() * 2;
                    // 该变量在最左侧和最右侧的边界和Tab被布局完后被初始化为Activeview的宽度。
                    tmpHeight -= leftBottom.getLines() + 2 * BOOKEDGE;

                    ((JComponent) activeApp).setBounds(rightTabPane.tabHeight + BOOKEDGE + leftEdge.getLines(),
                            BOOKEDGE, tmpWidth, tmpHeight);
                    // activeView布局完毕-----------------------------------------------------------
                }
            }
        }
    }

    /**
     * called by MainPane.
     * 
     * @return
     */
    public static int getDividerSize() {
        return EBinderClamp.getBinderWidth();
    }

    /**
     * 覆盖父类的方法，以达到BaseBookPane本身实现透明，但不影响加在其上的组件的显示的效果。
     * 
     * @param g
     *            图形设备
     */
    public void paintComponent(
            java.awt.Graphics g) {
        if (!CustOpts.custOps.isBaseBookHide() && activeTabIndex != ModelCons.CLOSED) {
            if (CustOpts.custOps.isViewTopAndDown())// 上下
            {
                // 画上边的纸隔板。
                g.setColor(CustOpts.custOps.getTabBGAt(activeTabIndex));
                g.fillRect(0, rightTabPane.tabHeight, getWidth(), binderClam.getY() - rightTabPane.tabHeight
                        + binderClam.getBinderWidth() / 2 - 7);
                // 画下边的纸隔板。
                g.setColor(CustOpts.custOps.getTabBGAt(activeTabIndex + 1));
                g.fillRect(0, binderClam.getY() + binderClam.getBinderWidth() / 2 + 7, getWidth(), getHeight()
                        - binderClam.getY() - binderClam.getBinderWidth() / 2 - 7 - rightTabPane.tabHeight + 1);
            } else// 左右
            {
                // 画左边的纸隔板。
                g.setColor(CustOpts.custOps.getTabBGAt(activeTabIndex));
                g.fillRect(rightTabPane.tabHeight, 0,
                        binderClam.getX() - rightTabPane.tabHeight + binderClam.getBinderWidth() / 2 - 7, getHeight());

                // 画右边的纸隔板。
                g.setColor(CustOpts.custOps.getTabBGAt(activeTabIndex + 1));
                g.fillRect(binderClam.getX() + binderClam.getBinderWidth() / 2 + 7, 0, getWidth() - binderClam.getX()
                        - binderClam.getBinderWidth() / 2 - 7 - rightTabPane.tabHeight + 1, getHeight());
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent evt) {
        if (evt.getComponent() == binderClam && evt.getClickCount() == 2) {
            new CircumViewAction().actionPerformed(null);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
        if (isMousePressed) {
            return;
        }
        Object tmpSource = e.getComponent();
        if (tmpSource == leftEdge || tmpSource == rightEdge || tmpSource == leftTabPane || tmpSource == rightTabPane) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (tmpSource == binderClam) {
            if (CustOpts.custOps.isViewTopAndDown()) {
                setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            }
        }
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
        if (!isMousePressed)
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @changeApplicationPane方法放在invokeLater中执行,因为如果Editor仍然拥有焦点时被removeEditor,回导致异常.
     * @param evt
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent evt) {
        isMousePressed = true;
        Object tmpSource = evt.getComponent();
        if (tmpSource == leftTabPane) // 左边的Tab页.
        {
            if (!SwingUtilities.isLeftMouseButton(evt)) // 有必要再判断一下吗？
                return;

            int x = evt.getX();
            int y = evt.getY();

            final int tmpSeleIndex = leftTabPane.getTabIndex(x, y);
            if (tmpSeleIndex < 0) // 点了左边的空白将导致书本向右关闭。
            {
                isLeftClosed = false;
                CASControl.ctrl.getFolderTree().grabFocus(); // @NOTE:为了防止remove组件时组件的焦点转移尚未完毕造成JDK异常,
                SwingUtilities.invokeLater(new Runnable() // 所以先将焦点移开,并将可能导致移除Table中的编辑器动作的方法放到事件队列的最后.
                        {
                            public void run() {
                                CloseBookRightAction.getInstance().actionPerformed(null);
                            }
                        });
            } else {
                CASControl.ctrl.getFolderTree().grabFocus();// @NOTE:为了防止remove组件时组件的焦点转移尚未完毕造成JDK异常,

                SwingUtilities.invokeLater(new Runnable() // 所以先将焦点移开,并将可能导致移除Table中的编辑器动作的方法放到事件队列的最后.
                        {
                            public void run() {
                                CASControl.ctrl.changeApplication(tmpSeleIndex, CASUtility.getAPPNodeID(tmpSeleIndex));
                            }
                        });
            }
        } else if (tmpSource == rightTabPane) // 右边的Tab页.
        {
            if (!SwingUtilities.isLeftMouseButton(evt))
                return;

            int x = evt.getX();
            int y = evt.getY();

            final int tmpSeleIndex = rightTabPane.getTabIndex(x, y);
            if (tmpSeleIndex < 0) {
                isLeftClosed = true;
                CASControl.ctrl.getFolderTree().grabFocus(); // @NOTE:为了防止remove组件时组件的焦点转移尚未完毕造成JDK异常,
                SwingUtilities.invokeLater(new Runnable() // 所以先将焦点移开,并将可能导致移除Table中的编辑器动作的方法放到事件队列的最后.
                        {
                            public void run() {
                                CloseBookLeftAction.getInstance().actionPerformed(null);
                            }
                        });
            } else {
                CASControl.ctrl.getFolderTree().grabFocus(); // @NOTE:为了防止remove组件时组件的焦点转移尚未完毕造成JDK异常,
                SwingUtilities.invokeLater(new Runnable() // 所以先将焦点移开,并将可能导致移除Table中的编辑器动作的方法放到事件队列的最后.
                        {
                            public void run() {
                                CASControl.ctrl.changeApplication(tmpSeleIndex, CASUtility.getAPPNodeID(tmpSeleIndex));
                            }
                        });
            }
        } else if (tmpSource == leftEdge)// 笔记本的边界.
        {
            activeApp.setPageBack(true);
        } else if (tmpSource == rightEdge) {
            activeApp.setPageBack(false);
        } else if (tmpSource == binderClam) {
            oldDeviderPos = -10000;
        }
        // else if (tmpSource == this)
        // {
        // if(evt.getButton() == MouseEvent.BUTTON1)
        // {
        // if(CustOpts.custOps.getActiveAppType() == ModelCons.CLOSED)//单击空白也将Active的应用打开。
        // {
        // CASControl.ctrl.getFolderTree().grabFocus(); //@NOTE:为了防止remove组件时组件的焦点转移尚未完毕造成JDK异常,
        // SwingUtilities.invokeLater(new Runnable() //所以先将焦点移开,并将可能导致移除Table中的编辑器动作的方法放到事件队列的最后.
        // {
        // public void run()
        // {
        // int tmpPathID = CustOpts.custOps.getActivePathID();
        // int tmpApp = PIMUtility.getAppIndexByFolderID(tmpPathID);
        // if(tmpApp < 1)//第一次使用时，因为没有保存值，所以app为－1。以后如果节点所在的应用被卸载，也会得到－1。
        // { //这时，暂定将应用切换到第一个上。
        // tmpApp = 0;
        // tmpPathID = PIMUtility.getAPPNodeID(tmpApp);
        // }
        // CASControl.ctrl.changeApplication(tmpApp, tmpPathID);
        // }
        // });
        // }
        // else
        // {
        //
        // }
        // }
        // }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        isMousePressed = false;
        Object tmpSource = e.getComponent();
        if (pliderDragged) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Graphics g = getGraphics();
            g.setXORMode(CustOpts.custOps.getContentBG());
            if (tmpSource == binderClam) {
                if (CustOpts.custOps.isViewTopAndDown()) {
                    for (int i = 8; i > 0; i--) {
                        g.drawLine(0, oldDeviderPos + i, getWidth(), oldDeviderPos + i);
                    }
                    CustOpts.custOps.setSplitHeightScale((double) oldDeviderPos / getHeight());
                } else {
                    for (int i = 8; i > 0; i--) {
                        g.drawLine(oldDeviderPos + i, 0, oldDeviderPos + i, getHeight());
                    }
                    CustOpts.custOps.setSplitHeightScale((double) oldDeviderPos / getWidth());
                }
                revalidate();
                invalidate();
            }
            repaint();
            oldDeviderPos = -10000;
            pliderDragged = false;
        }
    }

    public void mouseDragged(
            MouseEvent e) {
        pliderDragged = true;
        Object tmpSource = e.getComponent();
        if (tmpSource == binderClam) {
            Graphics g = getGraphics();
            g.setXORMode(CustOpts.custOps.getContentBG());
            if (CustOpts.custOps.isViewTopAndDown()) {
                for (int i = 8; i > 0; i--) {
                    g.drawLine(0, oldDeviderPos + i, getWidth(), oldDeviderPos + i);
                }
                oldDeviderPos = e.getY() + binderClam.getY();
                for (int i = 8; i > 0; i--) {
                    g.drawLine(0, oldDeviderPos + i, getWidth(), oldDeviderPos + i);
                }
            } else {
                for (int i = 8; i > 0; i--) {
                    g.drawLine(oldDeviderPos + i, 0, oldDeviderPos + i, getHeight());
                }
                oldDeviderPos = e.getX() + binderClam.getX();
                for (int i = 8; i > 0; i--) {
                    g.drawLine(oldDeviderPos + i, 0, oldDeviderPos + i, getHeight());
                }
            }
        }
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     */
    public void mouseMoved(
            MouseEvent e) {

    }

    // ------------------------------
    /**
     * 切换文件夹时，设置为缺省的视图
     * 
     * @called by: PIMControl Note:传入的参数必须是有效且界内的，所有的判断必须在上游应判断好。
     *         NOTE:Actveview和binderClam必须一起拿掉再一起加上，为的是达到binderClam在下，activeView在上的视觉效果。
     * @param prmIndex
     *            主索引
     */
    public void changeApplication(
            int prmAppIndex,
            int prmFolderPathID) {
        if (activeApp != null) // 移掉现有的组件和中间的书夹子。
            remove((JComponent) activeApp);
        remove(binderClam);

        activeApp = CASControl.ctrl.getMainPane().getActiveApp();
        activeTabIndex = prmAppIndex;
        add((JComponent) activeApp);// 添加新的activeView和binderClamp。
        add(binderClam);

        revalidate();// 设置应用面板时，设置当前的布局无效，重现布局，并绘制当前的组件
        repaint();
    }

    // geters &
    // setters----------------------------------------------------------------------------------------------------------------
    /**
     * 得到当前视图
     * 
     * @return 当前视图
     * @for:PIMControl;AppointmentDialog.
     */
    public IApplication getActiveApp() {
        return activeApp;
    }

    public IApplication getColsePane() {
        coverPane.isLeft = isLeftClosed;
        return coverPane;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // 以下方法将被mainPane调用以响应控制部分的动作 //
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * 选取视图中的所有记录。
     * 
     * @for:seleteAllAction
     */
    public void seleteAllRecord() {
        activeApp.seleteAllRecord();
    }

    /**
     * 取得被选中的记录。
     * 
     * @return 被选中的记录
     * @for:CutAction & CopyAction
     */
    public int[] getSelectRecordsID() {
        return activeApp.getSelectRecordsID();
    }

    /**
     * 取得被选中的记录。
     * 
     * @return 被选中的记录
     * @for:CutAction & CopyAction
     */
    public Vector getSelectRecords() {
        return activeApp.getSelectRecords();
    }

    /**
     * 更新视图
     * 
     * @for:MainPane,仅仅用来传递control的命令,传递给当前的视图,令其更新显示内容.
     * @param e
     *            数据库事件
     */
    public void updateView(
            PIMModelEvent e) {
    }

    /**
     * Invoke when the application frame closed.
     * 
     * @param save
     *            true if the frame need to be saved.
     * @for:MainPane,仅仅用来传递control的命令,传递给当前的视图,便于其保存正在编辑内容.
     */
    public void closed(
            boolean save) {
        activeApp.closed(true);
    }

    // private void changeActiveTodayTitle(String actionName)
    // {
    // if (actionName.equalsIgnoreCase(CUSTOM_TODAY))
    // {
    // activityTitle.removeAllHotButton();
    // activityTitle.addHotButton(SAVE_CHANGES);
    // activityTitle.addHotButton(CALCEL_CHANGES);
    // activityTitle.setTitle(CUSTOM_TODAY);
    // activityTitle.revalidate();
    // activityToday.setTodayData();
    // }
    // else
    // {
    // if (actionName.equalsIgnoreCase(SAVE_CHANGES))
    // {
    // }
    // activityTitle.removeAllHotButton();
    // activityTitle.addHotButton(CUSTOM_TODAY);
    // activityTitle.setTitle(null);
    // activityTitle.revalidate();
    // activityToday.setActivityToday(true);
    // }
    // activityTitle.setFocus();
    // activityTitle.setNextFocusableComponent(activityToday.getViewComponet());
    // }

    // variables-----------------------------------------------------------------
    private int tmpPos;// 改变量用于确定binderClam的位置。因为paint方法需要在布局之后仍然能够取到这个值，
    private boolean isLeftClosed;
    private EPageEdge leftEdge; // 左边
    private EPageEdge rightEdge; // 右边
    private EVerTabPane leftTabPane; // 左TAB面板
    private EVerTabPane rightTabPane; // 右TAB面板
    private EPageBottom leftBottom; // 左边底部
    private EPageBottom rightBottom; // 右边底部
    private EBinderClamp binderClam; // 中间的装饰
    private boolean pliderDragged;
    private boolean isMousePressed;
    int oldDeviderPos = -10000;
    private int activeTabIndex = ModelCons.CLOSED; // 当前TAB面板的选中项
    App_Cover coverPane;
    private IApplication activeApp; // 当前应用(卡片,表格)
}

/*
 * Rectangle rect = leftTabPane.getBoundsAt (0); int firstTabHeight = rect.y + rect.height; if (activeTabIndex < 0) { }
 * else { leftTabHeight = 0; for (int i = 0; i <= activeTabIndex; ++i) { leftTabHeight += leftTabPane.getBoundsAt
 * (i).height; } leftTabHeight -= rect.y; leftTabPane.setBounds (0, 0, tabWidth, height);// + firstTabHeight -
 * bottomWidth); }
 */
/*
 * rect = rightTabPane.getBoundsAt (0); firstTabHeight = rect.y + rect.height; int rectHeight = 0; rightTabHeight = 0;
 * for (int i = 0; i < tabCount; ++i) { rightTabHeight += rightTabPane.getBoundsAt (i).height; } rightTabHeight -=
 * rect.y; if (activeTabIndex == _RIGHTCLOSED) { //rightTabPane.setBounds (width - tabWidth, 0, tabWidth, height +
 * firstTabHeight - bottomWidth); } else if (activeTabIndex >= tabCount) { rightTabPane.setSize (0, 0); } else { for
 * (int i = 0; i <= activeTabIndex; ++i) { rectHeight += rightTabPane.getBoundsAt (i).height; } rightTabPane.setBounds
 * (width - tabWidth, 0, tabWidth, height + rectHeight - bottomWidth); } private int leftTabHeight, rightTabHeight;
 */

// private AbstractPane viewAry[];
