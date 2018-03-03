package org.cas.client.platform.pimview.pimscrollpane;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import org.cas.client.platform.pimview.pimtable.PIMTable;

public class BasicPIMScrollPaneUI extends PIMScrollPaneUI implements ScrollPaneConstants {
    protected PIMScrollPane pimScrollpane;
    protected ChangeListener vsbChangeListener;
    protected ChangeListener hsbChangeListener;
    protected ChangeListener viewportChangeListener;
    protected PropertyChangeListener spPropertyChangeListener;
    private MouseWheelListener mouseScrollListener;

    /**
     * PropertyChangeListener installed on the vertical scrollbar.
     */
    private PropertyChangeListener vsbPropertyChangeListener;

    /**
     * PropertyChangeListener installed on the horizontal scrollbar.
     */
    private PropertyChangeListener hsbPropertyChangeListener;

    /**
     * The default implementation of createHSBPropertyChangeListener and createVSBPropertyChangeListener share the
     * PropertyChangeListener, which is this ivar.
     */
    private PropertyChangeListener sbPropertyChangeListener;

    /**
     * State flag that shows whether setValue() was called from a user program before the value of "extent" was set in
     * right-to-left component orientation.
     */
    boolean setValueCalled; // @NOTE:提高可见性，以追求更好的性能。

    /**
     * UI标准方法
     * 
     * @return 组件的UI
     * @param x
     *            滚动面板
     */
    public static ComponentUI createUI(
            JComponent x) {
        return new BasicPIMScrollPaneUI();
    }

    /**
     * 重载UI方法
     * 
     * @param g
     *            图形设备
     * @param c
     *            滚动面板
     */
    public void paint(
            Graphics g,
            JComponent c) {
        // PIMScrollPaneBorder vpBorder = scrollpane.getViewportBorder();
        // if (vpBorder != null)
        // {
        // Rectangle r = scrollpane.getViewportBorderBounds();
        // vpBorder.paintBorder(scrollpane, g, r.x, r.y, r.width, r.height);
        // }
    }

    /**
     * 重载
     * 
     * @return null which indicates that the LayoutManager will compute the value
     * @see JComponent#getPreferredSize
     * @param c
     *            滚动面板
     */
    public Dimension getPreferredSize(
            JComponent c) {
        return null;
    }

    /**
     * 重载
     * 
     * @return the preferred size
     * @see #getPreferredSize
     * @param c
     *            滚动面板
     */
    public Dimension getMinimumSize(
            JComponent c) {
        return getPreferredSize(c);
    }

    /**
     * 重载
     * 
     * @return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)
     * @param c
     *            滚动面板
     */
    public Dimension getMaximumSize(
            JComponent c) {
        return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    protected void installDefaults(
            PIMScrollPane scrollpane) {
        LookAndFeel.installBorder(scrollpane, "ScrollPane.border");
        LookAndFeel.installColorsAndFont(scrollpane, "ScrollPane.background", "ScrollPane.foreground",
                "ScrollPane.font");

        PIMScrollPaneBorder vpBorder = scrollpane.getViewportBorder();
        if ((vpBorder == null) || (vpBorder instanceof UIResource)) {
            vpBorder = (PIMScrollPaneBorder) UIManager.getBorder("ScrollPane.viewportBorder");
            scrollpane.setViewportBorder(vpBorder);
        }
    }

    protected void installListeners(
            PIMScrollPane c) {
        vsbChangeListener = createVSBChangeListener();
        vsbPropertyChangeListener = createVSBPropertyChangeListener();
        hsbChangeListener = createHSBChangeListener();
        hsbPropertyChangeListener = createHSBPropertyChangeListener();
        viewportChangeListener = createViewportChangeListener();
        spPropertyChangeListener = createPropertyChangeListener();

        JViewport viewport = pimScrollpane.getViewport();
        JScrollBar vsb = pimScrollpane.getVerticalScrollBar();
        JScrollBar hsb = pimScrollpane.getHorizontalScrollBar();

        if (viewport != null) {
            viewport.addChangeListener(viewportChangeListener);
        }
        if (vsb != null) {
            vsb.getModel().addChangeListener(vsbChangeListener);
            vsb.addPropertyChangeListener(vsbPropertyChangeListener);
        }
        if (hsb != null) {
            hsb.getModel().addChangeListener(hsbChangeListener);
            hsb.addPropertyChangeListener(hsbPropertyChangeListener);
        }

        pimScrollpane.addPropertyChangeListener(spPropertyChangeListener);

        mouseScrollListener = createMouseWheelListener();
        pimScrollpane.addMouseWheelListener(mouseScrollListener);

    }

    protected void installKeyboardActions(
            PIMScrollPane c) {
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
        ActionMap actionMap = getActionMap();

        SwingUtilities.replaceUIActionMap(c, actionMap);
    }

    InputMap getInputMap(
            int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            InputMap keyMap = (InputMap) UIManager.get("ScrollPane.ancestorInputMap");
            InputMap rtlKeyMap;

            if (pimScrollpane.getComponentOrientation().isLeftToRight()
                    || ((rtlKeyMap = (InputMap) UIManager.get("ScrollPane.ancestorInputMap.RightToLeft")) == null)) {
                return keyMap;
            } else {
                rtlKeyMap.setParent(keyMap);
                return rtlKeyMap;
            }
        }
        return null;
    }

    ActionMap getActionMap() {
        ActionMap map = (ActionMap) UIManager.get("ScrollPane.actionMap");

        if (map == null) {
            map = createActionMap();
            if (map != null) {
                UIManager.getLookAndFeelDefaults().put("ScrollPane.actionMap", map);
            }
        }
        return map;
    }

    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();
        map.put("scrollUp", new ScrollAction("scrollUp", SwingConstants.VERTICAL, -1, true));
        map.put("scrollDown", new ScrollAction("scrollDown", SwingConstants.VERTICAL, 1, true));
        map.put("scrollHome", new ScrollHomeAction("ScrollHome"));
        map.put("scrollEnd", new ScrollEndAction("ScrollEnd"));
        map.put("unitScrollUp", new ScrollAction("UnitScrollUp", SwingConstants.VERTICAL, -1, false));
        map.put("unitScrollDown", new ScrollAction("UnitScrollDown", SwingConstants.VERTICAL, 1, false));

        if (pimScrollpane.getComponentOrientation().isLeftToRight()) {
            map.put("scrollLeft", new ScrollAction("scrollLeft", SwingConstants.HORIZONTAL, -1, true));
            map.put("scrollRight", new ScrollAction("ScrollRight", SwingConstants.HORIZONTAL, 1, true));
            map.put("unitScrollRight", new ScrollAction("UnitScrollRight", SwingConstants.HORIZONTAL, 1, false));
            map.put("unitScrollLeft", new ScrollAction("UnitScrollLeft", SwingConstants.HORIZONTAL, -1, false));
        } else {
            map.put("scrollLeft", new ScrollAction("scrollLeft", SwingConstants.HORIZONTAL, 1, true));
            map.put("scrollRight", new ScrollAction("ScrollRight", SwingConstants.HORIZONTAL, -1, true));
            map.put("unitScrollRight", new ScrollAction("UnitScrollRight", SwingConstants.HORIZONTAL, -1, false));
            map.put("unitScrollLeft", new ScrollAction("UnitScrollLeft", SwingConstants.HORIZONTAL, 1, false));
        }
        return map;
    }

    /**
     * UI标准方法
     * 
     * @param x
     *            滚动面板
     */
    public void installUI(
            JComponent x) {
        pimScrollpane = (PIMScrollPane) x;
        installDefaults(pimScrollpane);
        installListeners(pimScrollpane);
        installKeyboardActions(pimScrollpane);
    }

    /**
     * UI标准方法
     */
    protected void uninstallDefaults(
            PIMScrollPane c) {
        LookAndFeel.uninstallBorder(pimScrollpane);

        if (pimScrollpane.getViewportBorder() instanceof UIResource) {
            pimScrollpane.setViewportBorder(null);
        }
    }

    /**
     * UI标准方法
     */
    protected void uninstallListeners(
            JComponent c) {
        JViewport viewport = pimScrollpane.getViewport();
        JScrollBar vsb = pimScrollpane.getVerticalScrollBar();
        JScrollBar hsb = pimScrollpane.getHorizontalScrollBar();

        if (viewport != null) {
            viewport.removeChangeListener(viewportChangeListener);
        }
        if (vsb != null) {
            vsb.getModel().removeChangeListener(vsbChangeListener);
            vsb.removePropertyChangeListener(vsbPropertyChangeListener);
        }
        if (hsb != null) {
            hsb.getModel().removeChangeListener(hsbChangeListener);
            hsb.removePropertyChangeListener(hsbPropertyChangeListener);
        }

        pimScrollpane.removePropertyChangeListener(spPropertyChangeListener);

        if (mouseScrollListener != null) {
            pimScrollpane.removeMouseWheelListener(mouseScrollListener);
        }

        vsbChangeListener = null;
        hsbChangeListener = null;
        viewportChangeListener = null;
        spPropertyChangeListener = null;
        mouseScrollListener = null;
    }

    /**
     * UI标准方法
     */
    protected void uninstallKeyboardActions(
            PIMScrollPane c) {
        SwingUtilities.replaceUIActionMap(c, null);
        SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
    }

    /**
     * UI标准方法
     * 
     * @param c
     *            滚动面板发
     */
    public void uninstallUI(
            JComponent c) {
        uninstallDefaults(pimScrollpane);
        uninstallListeners(pimScrollpane);
        uninstallKeyboardActions(pimScrollpane);
        pimScrollpane = null;
    }

    /**
     * 抄JDK的
     */
    protected void syncScrollPaneWithViewport() {
        JViewport viewport = pimScrollpane.getViewport();
        JScrollBar vsb = pimScrollpane.getVerticalScrollBar();
        JScrollBar hsb = pimScrollpane.getHorizontalScrollBar();
        JViewport rowHead = pimScrollpane.getRowHeader();
        JViewport colHead = pimScrollpane.getColumnHeader();
        boolean ltr = pimScrollpane.getComponentOrientation().isLeftToRight();

        if (viewport != null) {
            Dimension extentSize = viewport.getExtentSize();
            Dimension viewSize = viewport.getViewSize();
            Point viewPosition = viewport.getViewPosition();

            if (vsb != null) {
                int extent = extentSize.height;
                int max = viewSize.height;
                int value = Math.max(0, Math.min(viewPosition.y, max - extent));
                vsb.setValues(value, extent, 0, max);
            }

            if (hsb != null) {
                int extent = extentSize.width;
                int max = viewSize.width;
                int value;

                // 保存旧值(视口的X坐标的定位)
                int oldValue = hsb.getValue();
                if (ltr) {
                    value = Math.max(0, Math.min(viewPosition.x, max - extent));
                    // 我们处理这种行为的原因是,在表格右滚动到了最后,这时如果对表格中
                    // 某列的宽度进行调整(是宽度减小),这时得到的这个 value 值会比旧值
                    // 小,而这个值也将就是表格在视图上所显示出来的部分所在的X坐标,所
                    // 以,在列宽度进行减小调整时,会看到视图中表格在鼠标右边的部分不动
                    // ,而左边部分会向右边靠,这与微软表现不一致,至少是不合用户习惯,所
                    // 以作如下处理:目前也只发现在表格视图的这种情况下,以下这个判断成
                    // 立,只要将此时要设置的值恢复以前即可.
                    if (oldValue > value) {
                        Component comp = pimScrollpane.getViewport().getView();
                        if (comp != null && comp instanceof PIMTable) {
                            PIMTable table = (PIMTable) comp;
                            if (table.getTableHeader().getResizingColumn() != null) {
                                value = oldValue;
                            }
                        }
                    }
                } else {
                    int currentValue = hsb.getValue();

                    /*
                     * Use a particular formula to calculate "value" until effective x coordinate is calculated.
                     */
                    if (setValueCalled && ((max - currentValue) == viewPosition.x)) {
                        value = Math.max(0, Math.min(max - extent, currentValue));
                        /*
                         * After "extent" is set, turn setValueCalled flag off.
                         */
                        if (extent != 0) {
                            setValueCalled = false;
                        }
                    } else {
                        if (extent > max) {
                            viewPosition.x = max - extent;
                            viewport.setViewPosition(viewPosition);
                            value = 0;
                        } else {
                            /*
                             * The following line can't handle a small value of viewPosition.x like Integer.MIN_VALUE
                             * correctly because (max - extent - viewPositoiin.x) causes an overflow. As a result, value
                             * becomes zero. (e.g. setViewPosition(Integer.MAX_VALUE, ...) in a user program causes a
                             * overflow. Its expected value is (max - extent).) However, this seems a trivial bug and
                             * adding a fix makes this often-called method slow, so I'll leave it until someone claims.
                             */
                            value = Math.max(0, Math.min(max - extent, max - extent - viewPosition.x));
                        }
                    }
                }
                hsb.setValues(value, extent, 0, max);
            }

            if (rowHead != null) {
                Point p = rowHead.getViewPosition();
                p.y = viewport.getViewPosition().y;
                p.x = 0;
                rowHead.setViewPosition(p);
            }

            if (colHead != null) {
                Point p = colHead.getViewPosition();
                if (ltr) {
                    p.x = viewport.getViewPosition().x;
                } else {
                    p.x = Math.max(0, viewport.getViewPosition().x);
                }
                p.y = 0;
                colHead.setViewPosition(p);
            }
        }
    }

    /**
     * Listener for viewport events.
     */
    public static class ViewportChangeHandler implements ChangeListener {
        BasicPIMScrollPaneUI spUI;

        /**
         * 构建器,传入引用
         * 
         * @param ScrollPaneUI
         *            传入UI
         */
        public ViewportChangeHandler(BasicPIMScrollPaneUI ScrollPaneUI) {
            spUI = ScrollPaneUI;
        }

        /**
         * 接口的方法,与JDK一样
         * 
         * @param e
         *            变化事件
         */
        public void stateChanged(
                ChangeEvent e) {
            spUI.syncScrollPaneWithViewport();
        }
    }

    protected ChangeListener createViewportChangeListener() {
        return new ViewportChangeHandler(this);
    }

    /**
     * Horizontal scrollbar listener.
     */
    public static class HSBChangeListener implements ChangeListener {
        BasicPIMScrollPaneUI spUI;

        /**
         * 构建器,传入引用
         * 
         * @param ScrollPaneUI
         *            传入UI
         */
        public HSBChangeListener(BasicPIMScrollPaneUI ScrollPaneUI) {
            spUI = ScrollPaneUI;
        }

        /**
         * 接口的方法,与JDK一样
         * 
         * @param e
         *            变化事件
         */
        public void stateChanged(
                ChangeEvent e) {
            JViewport viewport = spUI.pimScrollpane.getViewport();
            if (viewport != null) {
                BoundedRangeModel model = (BoundedRangeModel) (e.getSource());
                Point p = viewport.getViewPosition();
                int value = model.getValue();
                if (spUI.pimScrollpane.getComponentOrientation().isLeftToRight()) {
                    p.x = value;
                } else {
                    int max = viewport.getViewSize().width;
                    int extent = viewport.getExtentSize().width;
                    int oldX = p.x;

                    /*
                     * Set new X coordinate based on "value".
                     */
                    p.x = max - extent - value;

                    /*
                     * If setValue() was called before "extent" was fixed, turn setValueCalled flag on.
                     */
                    if ((extent == 0) && (value != 0) && (oldX == max)) {
                        spUI.setValueCalled = true;
                    } else {
                        /*
                         * When a pane without a horizontal scroll bar was reduced and the bar appeared, the viewport
                         * should show the right side of the view.
                         */
                        if ((extent != 0) && (oldX < 0) && (p.x == 0)) {
                            p.x += value;
                        }
                    }
                }
                viewport.setViewPosition(p);
            }
        }
    }

    /**
     * Returns a <code>PropertyChangeListener</code> that will be installed on the horizontal <code>JScrollBar</code>.
     */
    private PropertyChangeListener createHSBPropertyChangeListener() {
        return getSBPropertyChangeListener();
    }

    /**
     * Returns a shared <code>PropertyChangeListener</code> that will update the listeners installed on the scrollbars
     * as the model changes.
     */
    private PropertyChangeListener getSBPropertyChangeListener() {
        if (sbPropertyChangeListener == null) {
            sbPropertyChangeListener = new ScrollBarPropertyChangeHandler(this);
        }
        return sbPropertyChangeListener;
    }

    protected ChangeListener createHSBChangeListener() {
        return new HSBChangeListener(this);
    }

    /**
     * Vertical scrollbar listener.
     */
    public static class VSBChangeListener implements ChangeListener {
        BasicPIMScrollPaneUI spUI;

        /**
         * 构建器,传入引用
         * 
         * @param ScrollPaneUI
         *            传入UI
         */
        public VSBChangeListener(BasicPIMScrollPaneUI ScrollPaneUI) {
            spUI = ScrollPaneUI;
        }

        /**
         * 接口的方法,与JDK一样
         * 
         * @param e
         *            变化事件
         */
        public void stateChanged(
                ChangeEvent e) {
            JViewport viewport = spUI.pimScrollpane.getViewport();
            if (viewport != null) {
                BoundedRangeModel model = (BoundedRangeModel) (e.getSource());
                Point p = viewport.getViewPosition();
                p.y = model.getValue();
                viewport.setViewPosition(p);
            }
        }
    }

    /**
     * PropertyChangeListener for the ScrollBars.
     */
    public static class ScrollBarPropertyChangeHandler implements PropertyChangeListener {
        BasicPIMScrollPaneUI spUI;

        /**
         * 构建器,传入引用
         * 
         * @param ScrollPaneUI
         *            传入UI
         */
        public ScrollBarPropertyChangeHandler(BasicPIMScrollPaneUI ScrollPaneUI) {
            spUI = ScrollPaneUI;
        }

        // Listens for changes in the model property and reinstalls the
        // horizontal/vertical PropertyChangeListeners.
        /**
         * 属性事件的处理
         * 
         * @param e
         *            属性事件
         */
        public void propertyChange(
                PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            Object source = e.getSource();

            if ("model".equals(propertyName)) {
                JScrollBar sb = spUI.pimScrollpane.getVerticalScrollBar();
                BoundedRangeModel oldModel = (BoundedRangeModel) e.getOldValue();
                ChangeListener cl = null;

                if (source == sb) {
                    cl = spUI.vsbChangeListener;
                } else if (source == spUI.pimScrollpane.getHorizontalScrollBar()) {
                    sb = spUI.pimScrollpane.getHorizontalScrollBar();
                    cl = spUI.hsbChangeListener;
                }
                if (cl != null) {
                    if (oldModel != null) {
                        oldModel.removeChangeListener(cl);
                    }
                    if (sb.getModel() != null) {
                        sb.getModel().addChangeListener(cl);
                    }
                }
            } else if ("componentOrientation".equals(propertyName)) {
                if (source == spUI.pimScrollpane.getHorizontalScrollBar()) {
                    JScrollBar hsb = spUI.pimScrollpane.getHorizontalScrollBar();
                    JViewport viewport = spUI.pimScrollpane.getViewport();
                    Point p = viewport.getViewPosition();
                    if (spUI.pimScrollpane.getComponentOrientation().isLeftToRight()) {
                        p.x = hsb.getValue();
                    } else {
                        p.x = viewport.getViewSize().width - viewport.getExtentSize().width - hsb.getValue();
                    }
                    viewport.setViewPosition(p);
                }
            }
        }
    }

    /**
     * Returns a <code>PropertyChangeListener</code> that will be installed on the vertical <code>JScrollBar</code>.
     */
    private PropertyChangeListener createVSBPropertyChangeListener() {
        return getSBPropertyChangeListener();
    }

    protected ChangeListener createVSBChangeListener() {
        return new VSBChangeListener(this);
    }

    /**
     * MouseWheelHandler is an inner class which implements the MouseWheelListener interface. MouseWheelHandler responds
     * to MouseWheelEvents by scrolling the PIMScrollPane appropriately. If the scroll pane's
     * <code>isWheelScrollingEnabled</code> method returns false, no scrolling occurs.
     *
     * @see javax.swing.PIMScrollPane#isWheelScrollingEnabled
     * @see #createMouseWheelListener
     * @see java.awt.event.MouseWheelListener
     * @see java.awt.event.MouseWheelEvent
     * @since 1.4
     */
    public static class MouseWheelHandler implements MouseWheelListener {
        BasicPIMScrollPaneUI spUI;

        /**
         * 构建器,传入引用
         * 
         * @param ScrollPaneUI
         *            传入UI
         */
        public MouseWheelHandler(BasicPIMScrollPaneUI ScrollPaneUI) {
            spUI = ScrollPaneUI;
        }

        /**
         * Called when the mouse wheel is rotated while over a PIMScrollPane.
         *
         * @param e
         *            MouseWheelEvent to be handled
         * @since 1.4
         */
        public void mouseWheelMoved(
                MouseWheelEvent e) {
            if (spUI.pimScrollpane.isWheelScrollingEnabled() && e.getScrollAmount() != 0) {
                JScrollBar toScroll = spUI.pimScrollpane.getVerticalScrollBar();
                int direction = 0;
                // find which scrollbar to scroll, or return if none
                if (toScroll == null || !toScroll.isVisible()) {
                    toScroll = spUI.pimScrollpane.getHorizontalScrollBar();
                    if (toScroll == null || !toScroll.isVisible()) {
                        return;
                    }
                }
                direction = e.getWheelRotation() < 0 ? -1 : 1;
                if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                    spUI.scrollByUnits(toScroll, direction, e.getScrollAmount());
                } else if (e.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
                    spUI.scrollByBlock(toScroll, direction);
                }
            }
        }
    }

    /*
     * Method for scrolling by a unit increment. Added for mouse wheel scrolling support, RFE 4202656.
     */
    /**
     * 实现单元增量方法
     * 
     * @param scrollbar
     *            滚动条
     * @param direction
     *            方向
     * @param units
     *            单元增量
     */
    public void scrollByUnits(
            JScrollBar scrollbar,
            int direction,
            int units) {
        // This method is called from BasicScrollPaneUI to implement wheel
        // scrolling, as well as from scrollByUnit().
        int delta = units;

        if (direction > 0) {
            delta *= scrollbar.getUnitIncrement(direction);
        } else {
            delta *= -scrollbar.getUnitIncrement(direction);
        }

        int oldValue = scrollbar.getValue();
        int newValue = oldValue + delta;

        // Check for overflow.
        if (delta > 0 && newValue < oldValue) {
            newValue = scrollbar.getMaximum();
        } else if (delta < 0 && newValue > oldValue) {
            newValue = scrollbar.getMinimum();
        }
        scrollbar.setValue(newValue);
    }

    /*
     * Method for scrolling by a block increment. Added for mouse wheel scrolling support, RFE 4202656.
     */
    /**
     * 实现块增量方法
     * 
     * @param scrollbar
     *            滚动条
     * @param direction
     *            方向
     */
    public void scrollByBlock(
            JScrollBar scrollbar,
            int direction) {
        // This method is called from BasicScrollPaneUI to implement wheel
        // scrolling, and also from scrollByBlock().
        int oldValue = scrollbar.getValue();
        int blockIncrement = scrollbar.getBlockIncrement(direction);
        int delta = blockIncrement * ((direction > 0) ? +1 : -1);

        scrollbar.setValue(oldValue + delta);
    }

    /**
     * Creates an instance of MouseWheelListener, which is added to the PIMScrollPane by installUI(). The returned
     * MouseWheelListener is used to handle mouse wheel-driven scrolling.
     *
     * @return MouseWheelListener which implements wheel-driven scrolling
     * @see #installUI
     * @see MouseWheelHandler
     * @since 1.4
     */
    protected MouseWheelListener createMouseWheelListener() {
        return new MouseWheelHandler(this);
    }

    protected void updateScrollBarDisplayPolicy(
            PropertyChangeEvent e) {
        pimScrollpane.revalidate();
        pimScrollpane.repaint();
    }

    protected void updateViewport(
            PropertyChangeEvent e) {
        JViewport oldViewport = (JViewport) (e.getOldValue());
        JViewport newViewport = (JViewport) (e.getNewValue());

        if (oldViewport != null) {
            oldViewport.removeChangeListener(viewportChangeListener);
        }

        if (newViewport != null) {
            Point p = newViewport.getViewPosition();
            if (pimScrollpane.getComponentOrientation().isLeftToRight()) {
                p.x = Math.max(p.x, 0);
            } else {
                int max = newViewport.getViewSize().width;
                int extent = newViewport.getExtentSize().width;
                if (extent > max) {
                    p.x = max - extent;
                } else {
                    p.x = Math.max(0, Math.min(max - extent, p.x));
                }
            }
            p.y = Math.max(p.y, 0);
            newViewport.setViewPosition(p);
            newViewport.addChangeListener(viewportChangeListener);
        }
    }

    protected void updateRowHeader(
            PropertyChangeEvent e) {
        JViewport newRowHead = (JViewport) (e.getNewValue());
        if (newRowHead != null) {
            JViewport viewport = pimScrollpane.getViewport();
            Point p = newRowHead.getViewPosition();
            p.y = (viewport != null) ? viewport.getViewPosition().y : 0;
            newRowHead.setViewPosition(p);
        }
    }

    protected void updateColumnHeader(
            PropertyChangeEvent e) {
        JViewport newColHead = (JViewport) (e.getNewValue());
        if (newColHead != null) {
            JViewport viewport = pimScrollpane.getViewport();
            Point p = newColHead.getViewPosition();
            if (viewport == null) {
                p.x = 0;
            } else {
                if (pimScrollpane.getComponentOrientation().isLeftToRight()) {
                    p.x = viewport.getViewPosition().x;
                } else {
                    p.x = Math.max(0, viewport.getViewPosition().x);
                }
            }
            newColHead.setViewPosition(p);
            pimScrollpane.add(newColHead, COLUMN_HEADER);
        }
    }

    // @NOTE:提高可见性，以追求更好的性能。
    void updateHorizontalScrollBar(
            PropertyChangeEvent pce) {
        updateScrollBar(pce, hsbChangeListener, hsbPropertyChangeListener);
    }

    // @NOTE:提高可见性，以追求更好的性能。
    void updateVerticalScrollBar(
            PropertyChangeEvent pce) {
        updateScrollBar(pce, vsbChangeListener, vsbPropertyChangeListener);
    }

    private void updateScrollBar(
            PropertyChangeEvent pce,
            ChangeListener cl,
            PropertyChangeListener pcl) {
        JScrollBar sb = (JScrollBar) pce.getOldValue();
        if (sb != null) {
            if (cl != null) {
                sb.getModel().removeChangeListener(cl);
            }
            if (pcl != null) {
                sb.removePropertyChangeListener(pcl);
            }
        }
        sb = (JScrollBar) pce.getNewValue();
        if (sb != null) {
            if (cl != null) {
                sb.getModel().addChangeListener(cl);
            }
            if (pcl != null) {
                sb.addPropertyChangeListener(pcl);
            }
        }
    }

    /**
     * 属性监听器,抄JDK的
     */
    public static class PropertyChangeHandler implements PropertyChangeListener {
        BasicPIMScrollPaneUI spUI;

        /**
         * 构建器,传入引用
         * 
         * @param ScrollPaneUI
         *            传入UI
         */
        public PropertyChangeHandler(BasicPIMScrollPaneUI ScrollPaneUI) {
            spUI = ScrollPaneUI;
        }

        /**
         * 接口的方法,与JDK一样
         * 
         * @param e
         *            属性事件
         */
        public void propertyChange(
                PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();

            if (propertyName.equals("verticalScrollBarDisplayPolicy")) {
                spUI.updateScrollBarDisplayPolicy(e);
            } else if (propertyName.equals("horizontalScrollBarDisplayPolicy")) {
                spUI.updateScrollBarDisplayPolicy(e);
            } else if (propertyName.equals("viewport")) {
                spUI.updateViewport(e);
            } else if (propertyName.equals("rowHeader")) {
                spUI.updateRowHeader(e);
            } else if (propertyName.equals("columnHeader")) {
                spUI.updateColumnHeader(e);
            } else if (propertyName.equals("verticalScrollBar")) {
                spUI.updateVerticalScrollBar(e);
            } else if (propertyName.equals("horizontalScrollBar")) {
                spUI.updateHorizontalScrollBar(e);
            } else if (propertyName.equals("componentOrientation")) {
                spUI.pimScrollpane.revalidate();
                spUI.pimScrollpane.repaint();

                InputMap inputMap = spUI.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                SwingUtilities.replaceUIInputMap(spUI.pimScrollpane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                        inputMap);

                UIManager.getLookAndFeelDefaults().put("ScrollPane.actionMap", null);
                ActionMap actionMap = spUI.getActionMap();
                SwingUtilities.replaceUIActionMap(spUI.pimScrollpane, actionMap);
            }
        }
    }

    /**
     * Creates an instance of PropertyChangeListener that's added to the PIMScrollPane by installUI(). Subclasses can
     * override this method to return a custom PropertyChangeListener, e.g.
     * 
     * <pre>
     * class MyScrollPaneUI extends BasicPIMScrollPaneUI {
     *    protected PropertyChangeListener <b>createPropertyChangeListener</b>() {
     *        return new MyPropertyChangeListener();
     *    }
     *    public class MyPropertyChangeListener extends PropertyChangeListener {
     *        public void propertyChange(PropertyChangeEvent e) {
     *            if (e.getPropertyName().equals("viewport")) {
     *                // do some extra work when the viewport changes
     *            }
     *            super.propertyChange(e);
     *        }
     *    }
     * }
     * </pre>
     *
     * @see java.beans.PropertyChangeListener
     * @see #installUI
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler(this);
    }

    /**
     * Action to scroll left/right/up/down.
     */
    public static class ScrollAction extends AbstractAction {
        /** Direction to scroll. */
        protected int orientation;
        /** 1 indicates scroll down, -1 up. */
        protected int direction;
        /** True indicates a block scroll, otherwise a unit scroll. */
        private boolean block;

        public ScrollAction(String name, int orientation, int direction, boolean block) {
            super(name);
            this.orientation = orientation;
            this.direction = direction;
            this.block = block;
        }

        /**
         * Action 接口中的方法
         * 
         * @param e
         *            动作事件
         */
        public void actionPerformed(
                ActionEvent e) {
            PIMScrollPane scrollpane = (PIMScrollPane) e.getSource();
            JViewport vp = scrollpane.getViewport();
            Component view;
            if (vp != null && (view = vp.getView()) != null) {
                Rectangle visRect = vp.getViewRect();
                Dimension vSize = view.getSize();
                int amount;

                if (view instanceof Scrollable) {
                    if (block) {
                        amount = ((Scrollable) view).getScrollableBlockIncrement(visRect, orientation, direction);
                    } else {
                        amount = ((Scrollable) view).getScrollableUnitIncrement(visRect, orientation, direction);
                    }
                } else {
                    if (block) {
                        if (orientation == SwingConstants.VERTICAL) {
                            amount = visRect.height;
                        } else {
                            amount = visRect.width;
                        }
                    } else {
                        amount = 10;
                    }
                }
                if (orientation == SwingConstants.VERTICAL) {
                    visRect.y += (amount * direction);
                    if ((visRect.y + visRect.height) > vSize.height) {
                        visRect.y = Math.max(0, vSize.height - visRect.height);
                    } else if (visRect.y < 0) {
                        visRect.y = 0;
                    }
                } else {
                    if (scrollpane.getComponentOrientation().isLeftToRight()) {
                        visRect.x += (amount * direction);
                        if ((visRect.x + visRect.width) > vSize.width) {
                            visRect.x = Math.max(0, vSize.width - visRect.width);
                        } else if (visRect.x < 0) {
                            visRect.x = 0;
                        }
                    } else {
                        visRect.x -= (amount * direction);
                        if (visRect.width > vSize.width) {
                            visRect.x = vSize.width - visRect.width;
                        } else {
                            visRect.x = Math.max(0, Math.min(vSize.width - visRect.width, visRect.x));
                        }
                    }
                }
                vp.setViewPosition(visRect.getLocation());
            }
        }
    }

    /**
     * Action to scroll to x,y location of 0,0.
     */
    public static class ScrollHomeAction extends AbstractAction {
        public ScrollHomeAction(String name) {
            super(name);
        }

        /**
         * Action 接口中的方法
         * 
         * @param e
         *            动作事件
         */
        public void actionPerformed(
                ActionEvent e) {
            PIMScrollPane scrollpane = (PIMScrollPane) e.getSource();
            JViewport vp = scrollpane.getViewport();
            Component view;
            if (vp != null && (view = vp.getView()) != null) {
                if (scrollpane.getComponentOrientation().isLeftToRight()) {
                    vp.setViewPosition(new Point(0, 0));
                } else {
                    Rectangle visRect = vp.getViewRect();
                    Rectangle bounds = view.getBounds();
                    vp.setViewPosition(new Point(bounds.width - visRect.width, 0));
                }
            }
        }
    }

    /**
     * Action to scroll to last visible location.
     */
    public static class ScrollEndAction extends AbstractAction {
        public ScrollEndAction(String name) {
            super(name);
        }

        /**
         * Action 接口中的方法
         * 
         * @param e
         *            动作事件
         */
        public void actionPerformed(
                ActionEvent e) {
            PIMScrollPane scrollpane = (PIMScrollPane) e.getSource();
            JViewport vp = scrollpane.getViewport();
            Component view;
            if (vp != null && (view = vp.getView()) != null) {
                Rectangle visRect = vp.getViewRect();
                Rectangle bounds = view.getBounds();
                if (scrollpane.getComponentOrientation().isLeftToRight()) {
                    vp.setViewPosition(new Point(bounds.width - visRect.width, bounds.height - visRect.height));
                } else {
                    vp.setViewPosition(new Point(0, bounds.height - visRect.height));
                }
            }
        }
    }
}
