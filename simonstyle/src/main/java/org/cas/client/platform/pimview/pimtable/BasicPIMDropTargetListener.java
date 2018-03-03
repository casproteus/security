package org.cas.client.platform.pimview.pimtable;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.plaf.UIResource;

import org.cas.client.platform.casutil.PIMPool;

/**
 * 本类实际上是集成了DropTargetListener、UIResource 和 ActionListener 三个接口 并实现了其中的方法.本类将在PIMTable 中被使用.
 */

class BasicPIMDropTargetListener implements DropTargetListener, UIResource, ActionListener {

    /**
     * 构建一个拖动目标监听器的实例
     */
    protected BasicPIMDropTargetListener() {
    }

    /**
     * 本方法被调用以保存实例中一个组件的状态 以防一个放下动作未被执行而它需要被恢复 called to save the state of a component in case it needs to be restored
     * because a drop is not performed.
     * 
     * @param c
     *            the <code>Component</code>
     */
    protected void saveComponentState(
            JComponent c) {
    }

    /**
     * 万一一个放下动作未被执行,本方法被调用以恢复实例中一个组件的状态
     * 
     * @param c
     *            待更新组件 called to restore the state of a component in case a drop is not performed.
     */
    protected void restoreComponentState(
            JComponent c) {
    }

    /**
     * 万一一个放下动作未被执行,本方法被调用以恢复实例中一个组件的状态
     * 
     * @param c
     *            待更新组件 called to restore the state of a component in case a drop is performed.
     */
    protected void restoreComponentStateForDrop(
            JComponent c) {
    }

    /**
     * 本方法被调用以设置插入点和当前鼠标位置相一致
     * 
     * @param c
     *            待更新组件
     * @param p
     *            鼠标位置 called to set the insertion location to match the current mouse pointer coordinates.
     */
    protected void updateInsertionLocation(
            JComponent c,
            Point p) {
    }

    /**
     * 更新自动滚动的几何区域,几何上保持一个矩形, 在定时器的工作时期内,如果指针在本组件内部,这个区域会引发一个滚动, 这个导致定时器倒计时的区域在这两个矩形之间 执行后,用组件的可视区域作为外部区域,边缘部分基于
     * Scrollable 接口 如果可卷动者沿着一个轴是可卷动的,滚动步长就作为滚动边缘部分, 如果组件是不可滚动的,边缘部分就为零.
     * 
     * @param c
     *            待更新组件 Update the geometry of the autoscroll region. The geometry is maintained as a pair of
     *            rectangles. The region can cause a scroll if the pointer sits inside it for the duration of the timer.
     *            The region that causes the timer countdown is the area between the two rectangles.
     *            <p>
     *            This is implemented to use the visible area of the component as the outer rectangle and the insets are
     *            based upon the Scrollable information (if any). If the Scrollable is scrollable along an axis, the
     *            step increment is used as the autoscroll inset. If the component is not scrollable, the insets will be
     *            zero (i.e. autoscroll will not happen).
     */
    void updateAutoscrollRegion(
            JComponent c) {
        // 计算可视区域 compute the outer
        Rectangle visible = c.getVisibleRect();
        outer.reshape(visible.x, visible.y, visible.width, visible.height);

        // 计算边缘部分 compute the insets
        // TBD - the thing with the scrollable
        Insets i = new Insets(0, 0, 0, 0);
        if (c instanceof Scrollable) {
            // c是实现 Scrollable 接口的组件
            Scrollable s = (Scrollable) c;
            // 设置上,下,左,右四个边缘尺寸
            i.left = s.getScrollableUnitIncrement(visible, SwingConstants.HORIZONTAL, 1);
            i.top = s.getScrollableUnitIncrement(visible, SwingConstants.VERTICAL, 1);
            i.right = s.getScrollableUnitIncrement(visible, SwingConstants.HORIZONTAL, -1);
            i.bottom = s.getScrollableUnitIncrement(visible, SwingConstants.VERTICAL, -1);
        }

        // 设置内部矩形的尺寸 set the inner from the insets
        inner.reshape(visible.x + i.left, visible.y + i.top, visible.width - (i.left + i.right), visible.height
                - (i.top + i.bottom));
    }

    /**
     * 执行一个自动滚动操作,执行此方法组件会用scrollRectToVisible方法 以Scrollable接口的单元增量来实现滚动. 如果光标在自动滚动区域的角落上,就会在两个轴线上滚动 Perform an
     * autoscroll operation. This is implemented to scroll by the unit increment of the Scrollable using
     * scrollRectToVisible. If the cursor is in a corner of the autoscroll region, more than one axis will scroll.
     * 
     * @param c
     *            滚动组件
     * @param pos
     *            位置
     */
    void autoscroll(
            JComponent c,
            Point pos) {
        // 保证造型正确
        if (c instanceof Scrollable) {
            Scrollable s = (Scrollable) c;
            // 在Y轴方向
            if (pos.y < inner.y) {
                // 向下滚动,先得到单元增量 scroll top downward
                int dy = s.getScrollableUnitIncrement(outer, SwingConstants.VERTICAL, 1);
                // 得到交要显示出的矩形
                Rectangle r = new Rectangle(inner.x, outer.y - dy, inner.width, dy);
                c.scrollRectToVisible(r);
            } else if (pos.y > (inner.y + inner.height)) {
                // 向上,先得到单元增量 scroll bottom upward
                int dy = s.getScrollableUnitIncrement(outer, SwingConstants.VERTICAL, -1);
                // 得到交要显示出的矩形
                Rectangle r = new Rectangle(inner.x, outer.y + outer.height, inner.width, dy);
                c.scrollRectToVisible(r);
            }
            // 在X轴方向
            if (pos.x < inner.x) {
                // 向右滚动,先得到单元增量 scroll left side to the right
                int dx = s.getScrollableUnitIncrement(outer, SwingConstants.HORIZONTAL, 1);
                Rectangle r = new Rectangle(outer.x - dx, inner.y, dx, inner.height);
                // 得到交要显示出的矩形
                c.scrollRectToVisible(r);
            } else if (pos.x > (inner.x + inner.width)) {
                // 向左滚动,先得到单元增量 scroll left side to the right
                int dx = s.getScrollableUnitIncrement(outer, SwingConstants.HORIZONTAL, -1);
                Rectangle r = new Rectangle(outer.x + outer.width, inner.y, dx, inner.height);
                // 得到交要显示出的矩形
                c.scrollRectToVisible(r);
            }
        }
    }

    /**
     * 如果一些内部属性未初始化,就将它们初始化, 这方法实现得很懒以避免桌面特性的载入。 Initializes the internal properties if they haven't been already
     * inited. This is done lazily to avoid loading of desktop properties.
     */
    private void initPropertiesIfNecessary() {
        // 定时器为空就将其初始化
        if (timer == null) {
            Toolkit t = Toolkit.getDefaultToolkit();
            // 初始时间跨度和时间时间跨度均为100
            Integer initial = PIMPool.pool.getKey(100);
            Integer interval = PIMPool.pool.getKey(100);

            try {
                // 得到系统的自动滚动的初始时间跨度
                initial = (Integer) t.getDesktopProperty("DnD.Autoscroll.initialDelay");
            } catch (Exception e) {
                // ignore
            }
            try {
                // 得到系统的自动滚动的时间跨度
                interval = (Integer) t.getDesktopProperty("DnD.Autoscroll.interval");
            } catch (Exception e) {
                // ignore
            }
            // 新造一个定时器的实例
            timer = new Timer(interval.intValue(), this);
            // 允许激发多个
            timer.setCoalesce(true);
            timer.setInitialDelay(initial.intValue());

            try {
                // 得到系统的自动滚动的时间滞留间距
                hysteresis = ((Integer) t.getDesktopProperty("DnD.Autoscroll.cursorHysteresis")).intValue();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * 静态方法,根据鼠标放下事件得到所需的滚动组件
     * 
     * @param e
     *            拖动事件源
     * @return 滚动组件
     */
    static JComponent getComponent(
            DropTargetEvent e) {
        // 得到鼠标放下事件的上下文
        DropTargetContext context = e.getDropTargetContext();
        // 造型后返回
        return (JComponent) context.getComponent();
    }

    // --- ActionListener methods --------------------------------------

    /**
     * 实现接口中的方法,定时器一触发便会调用本方法,执行自动滚动 The timer fired, perform autoscroll if the pointer is within the autoscroll region.
     * <P>
     * 
     * @param e
     *            the <code>ActionEvent</code>
     */
    public synchronized void actionPerformed(
            ActionEvent e) {
        updateAutoscrollRegion(component);
        // 鼠标点在内,外部矩形之间时就自动滚动
        if (outer.contains(lastPosition) && !inner.contains(lastPosition)) {
            // 在本包中,实现滚动的部分就是PIMTable表体
            autoscroll(component, lastPosition);
        }
    }

    // --- DropTargetListener methods -----------------------------------

    /**
     * 实现 DropTargetListener 接口中的方法 进入拖动
     * 
     * @param e
     *            事件源
     */
    public void dragEnter(
            DropTargetDragEvent e) {
        // 从事件源中得到待滚动组件
        component = getComponent(e);
        // 得到转移句柄
        TransferHandler th = component.getTransferHandler();
        // 了解是否可导入
        canImport = th.canImport(component, e.getCurrentDataFlavors());
        // 如果可导入
        if (canImport) {
            // 保存该组件的状态信息
            saveComponentState(component);
            // 得到鼠标最后坐标
            lastPosition = e.getLocation();
            // 更新自动滚动区域
            updateAutoscrollRegion(component);
            // 初始化一些属性
            initPropertiesIfNecessary();
        }
    }

    /**
     * 实现 DropTargetListener 接口中的方法 拖动结束
     * 
     * @param e
     *            事件源
     */
    public void dragOver(
            DropTargetDragEvent e) {
        // 保证正确
        if (canImport) {
            // 得到鼠标坐标
            Point p = e.getLocation();
            updateInsertionLocation(component, p);

            // check autoscroll
            synchronized (this) {
                // 如果鼠标拖拉间距小于设定值
                if (Math.abs(p.x - lastPosition.x) > hysteresis || Math.abs(p.y - lastPosition.y) > hysteresis) {
                    // 不自动滚动no autoscroll
                    if (timer.isRunning()) {
                        timer.stop();
                    }
                } else {
                    // 进入自动滚动
                    if (!timer.isRunning()) {
                        timer.start();
                    }
                }
                // 保存鼠标最后点的坐标
                lastPosition = p;
            }
        }
    }

    /**
     * 实现 DropTargetListener 接口中的方法 拖动退出
     * 
     * @param e
     *            事件源
     */
    public void dragExit(
            DropTargetEvent e) {
        // 恢复这个滚动组件的状态信息
        if (canImport) {
            restoreComponentState(component);
        }
        // 清空这些属性
        cleanup();
    }

    /**
     * 实现 DropTargetListener 接口中的方法 拖动后放开
     * 
     * @param e
     *            事件源
     */
    public void drop(
            DropTargetDropEvent e) {
        if (canImport) {
            // 恢复这个滚动组件的状态信息
            restoreComponentStateForDrop(component);
        }
        // 清空这些属性
        cleanup();
    }

    /**
     * 实现 DropTargetListener 接口中的方法 放下动作变化了
     * 
     * @param e
     *            事件源
     */
    public void dropActionChanged(
            DropTargetDragEvent e) {
    }

    /**
     * 本方法在拖动动作完成后清空一些变量 Cleans up internal state after the drop has finished (either succeeded or failed).
     */
    private void cleanup() {
        // 清空定时器
        if (timer != null) {
            timer.stop();
        }
        // 清空滚动组件和鼠标最后点的坐标
        component = null;
        lastPosition = null;
    }

    // --- fields --------------------------------------------------
    /** 保存定时器 */
    private Timer timer;
    /** 保存鼠标最后放开的坐标 */
    private Point lastPosition;
    /** 保存滚动相关的两个矩形,鼠标在两者之间拖动会引发组件自动滚动 */
    private Rectangle outer = new Rectangle();
    /**
     * 滚动相关的矩形
     */
    private Rectangle inner = new Rectangle();
    /** 滞后间距 */
    private int hysteresis = 10;
    /** 可导入标志,为DropTargetListener 接口中的方法所用 */
    private boolean canImport;

    /**
     * 保存当前组件,组件是从'放下'事件中得到的,这个变量被定时器所用,当一个拖动 结束时或一个'放下'动作发生后,这个值会清除掉. The current component. The value is catched from
     * the drop events and used by the timer. When a drag exits or a drop occurs, this value is cleared.
     */
    private JComponent component;

}
