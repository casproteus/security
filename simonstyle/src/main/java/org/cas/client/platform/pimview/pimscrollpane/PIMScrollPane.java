package org.cas.client.platform.pimview.pimscrollpane;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimview.pimviewport.PIMViewport;

public class PIMScrollPane extends JComponent implements ScrollPaneConstants, AdjustmentListener, Releasable {
    private PIMScrollPaneBorder viewportBorder;
    /**
     * 静态初始化块,载入UI
     */
    static {
        UIManager.getDefaults().put("PIMScrollPaneUI",
                "org.cas.client.platform.pimview.pimscrollpane.BasicPIMScrollPaneUI");
    }
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "PIMScrollPaneUI";

    /**
     * The display policy for the vertical scrollbar. The default is
     * <code>PIMScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED</code>.
     * 
     * @see #setVerticalScrollBarPolicy
     */
    protected int verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED;

    /**
     * The display policy for the horizontal scrollbar. The default is
     * <code>PIMScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED</code>.
     * 
     * @see #setHorizontalScrollBarPolicy
     */
    protected int horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED;

    /**
     * The scrollpane's viewport child. Default is an empty <code>JViewport</code>.
     * 
     * @see #setViewport
     */
    protected JViewport viewport;

    /**
     * The scrollpane's vertical scrollbar child. Default is a <code>JScrollBar</code>.
     * 
     * @see #setVerticalScrollBar
     */
    protected JScrollBar verticalScrollBar;

    /**
     * The scrollpane's horizontal scrollbar child. Default is a <code>JScrollBar</code>.
     * 
     * @see #setHorizontalScrollBar
     */
    protected JScrollBar horizontalScrollBar;

    /**
     * The row header child. Default is <code>null</code>.
     * 
     * @see #setRowHeader
     */
    protected JViewport rowHeader;

    /**
     * The column header child. Default is <code>null</code>.
     * 
     * @see #setColumnHeader
     */
    protected JViewport columnHeader;

    /**
     * The component to display in the lower left corner. Default is <code>null</code>.
     * 
     * @see #setCorner
     */
    protected Component lowerLeft;

    /**
     * The component to display in the lower right corner. Default is <code>null</code>.
     * 
     * @see #setCorner
     */
    protected Component lowerRight;

    /**
     * The component to display in the upper left corner. Default is <code>null</code>.
     * 
     * @see #setCorner
     */
    protected Component upperLeft;

    /**
     * The component to display in the upper right corner. Default is <code>null</code>.
     * 
     * @see #setCorner
     */
    protected Component upperRight;

    /*
     * State flag for mouse wheel scrolling
     */
    private boolean wheelScrollState = true;

    /**
     * Creates a <code>PIMScrollPane</code> that displays the view component in a viewport whose view position can be
     * controlled with a pair of scrollbars. The scrollbar policies specify when the scrollbars are displayed, For
     * example, if <code>vsbPolicy</code> is <code>VERTICAL_SCROLLBAR_AS_NEEDED</code> then the vertical scrollbar only
     * appears if the view doesn't fit vertically. The available policy settings are listed at
     * {@link #setVerticalScrollBarPolicy} and {@link #setHorizontalScrollBarPolicy}.
     *
     * @see #setViewportView
     *
     * @param view
     *            the component to display in the scrollpanes viewport
     * @param vsbPolicy
     *            an integer that specifies the vertical scrollbar policy
     * @param hsbPolicy
     *            an integer that specifies the horizontal scrollbar policy
     */
    public PIMScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        setLayout(new PIMScrollPaneLayout.UIResource());
        setVerticalScrollBarPolicy(vsbPolicy);
        setHorizontalScrollBarPolicy(hsbPolicy);
        setViewport(createViewport());
        setVerticalScrollBar(createVerticalScrollBar());
        setHorizontalScrollBar(createHorizontalScrollBar());
        if (view != null) {
            setViewportView(view);
        }
        setOpaque(true);
        updateUI();

        if (!this.getComponentOrientation().isLeftToRight()) {
            viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
        }
    }

    /**
     * Creates a <code>PIMScrollPane</code> that displays the contents of the specified component, where both horizontal
     * and vertical scrollbars appear whenever the component's contents are larger than the view.
     *
     * @see #setViewportView
     * @param view
     *            the component to display in the scrollpane's viewport
     */
    public PIMScrollPane(Component view) {
        this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Creates an empty (no viewport view) <code>PIMScrollPane</code> with specified scrollbar policies. The available
     * policy settings are listed at {@link #setVerticalScrollBarPolicy} and {@link #setHorizontalScrollBarPolicy}.
     *
     * @see #setViewportView
     *
     * @param vsbPolicy
     *            an integer that specifies the vertical scrollbar policy
     * @param hsbPolicy
     *            an integer that specifies the horizontal scrollbar policy
     */
    public PIMScrollPane(int vsbPolicy, int hsbPolicy) {
        this(null, vsbPolicy, hsbPolicy);
    }

    /**
     * Creates an empty (no viewport view) <code>PIMScrollPane</code> where both horizontal and vertical scrollbars
     * appear when needed.
     */
    public PIMScrollPane() {
        this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Returns the look and feel (L&F) object that renders this component.
     *
     * @return the <code>PIMScrollPaneUI</code> object that renders this component
     * @see #setUI
     * @beaninfo bound: true hidden: true attribute: visualUpdate true description: The UI object that implements the
     *           Component's LookAndFeel.
     */
    public PIMScrollPaneUI getUI() {
        return (PIMScrollPaneUI) ui;
    }

    /**
     * Sets the <code>PIMScrollPaneUI</code> object that provides the look and feel (L&F) for this component.
     *
     * @param ui
     *            the <code>PIMScrollPaneUI</code> L&F object
     * @see #getUI
     */
    public void setUI(
            PIMScrollPaneUI ui) {
        super.setUI(ui);
    }

    protected void paintBorder(
            java.awt.Graphics g) {
        PIMScrollPaneBorder border = getViewportBorder();
        if (border != null) {
            border.paintBorder(this, g, 0, 0, 0, 0);
        }
    }

    /**
     * Replaces the current <code>PIMScrollPaneUI</code> object with a version from the current default look and feel.
     * To be called when the default look and feel changes.
     *
     * @see JComponent#updateUI
     * @see UIManager#getUI
     */
    public void updateUI() {
        setUI((PIMScrollPaneUI) UIManager.getUI(this));
    }

    /**
     * Returns the suffix used to construct the name of the L&F class used to render this component.
     *
     * @return the string "PIMScrollPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     *
     * @beaninfo hidden: true
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Sets the layout manager for this <code>PIMScrollPane</code>. This method overrides <code>setLayout</code> in
     * <code>java.awt.Container</code> to ensure that only <code>LayoutManager</code>s which are subclasses of
     * <code>PIMScrollPaneLayout</code> can be used in a <code>PIMScrollPane</code>. If <code>layout</code> is non-null,
     * this will invoke <code>syncWithScrollPane</code> on it.
     *
     * @see java.awt.Container#getLayout
     * @see java.awt.Container#setLayout
     * @beaninfo hidden: true
     * @param layout
     *            the specified layout manager
     */
    public void setLayout(
            LayoutManager layout) {
        if (layout instanceof PIMScrollPaneLayout) {
            super.setLayout(layout);
            ((PIMScrollPaneLayout) layout).syncWithScrollPane(this);
        } else if (layout == null) {
            super.setLayout(layout);
        } else {
            String s = "layout of PIMScrollPane must be a PIMScrollPaneLayout";
            throw new ClassCastException(s);
        }
    }

    /**
     * Calls <code>revalidate</code> on any descendant of this <code>PIMScrollPane</code>. For example, the viewport's
     * view, will cause a request to be queued that will validate the <code>PIMScrollPane</code> and all its
     * descendants.
     *
     * @return true
     * @see JComponent#revalidate
     *
     * @beaninfo hidden: true
     */
    public boolean isValidateRoot() {
        return true;
    }

    /**
     * Returns the vertical scroll bar policy value.
     * 
     * @return the <code>verticalScrollBarPolicy</code> property
     * @see #setVerticalScrollBarPolicy
     */
    public int getVerticalScrollBarPolicy() {
        return verticalScrollBarPolicy;
    }

    /**
     * Determines when the vertical scrollbar appears in the scrollpane. Legal values are:
     * <ul>
     * <li>PIMScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
     * <li>PIMScrollPane.VERTICAL_SCROLLBAR_NEVER
     * <li>PIMScrollPane.VERTICAL_SCROLLBAR_ALWAYS
     * </ul>
     *
     * @see #getVerticalScrollBarPolicy
     * @beaninfo preferred: true bound: true description: The scrollpane vertical scrollbar policy enum:
     *           VERTICAL_SCROLLBAR_AS_NEEDED PIMScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED VERTICAL_SCROLLBAR_NEVER
     *           PIMScrollPane.VERTICAL_SCROLLBAR_NEVER VERTICAL_SCROLLBAR_ALWAYS
     *           PIMScrollPane.VERTICAL_SCROLLBAR_ALWAYS
     * @param policy
     *            one of the three values listed above
     */
    public void setVerticalScrollBarPolicy(
            int policy) {
        switch (policy) {
            case VERTICAL_SCROLLBAR_AS_NEEDED:
            case VERTICAL_SCROLLBAR_NEVER:
            case VERTICAL_SCROLLBAR_ALWAYS:
                break;

            default:
                throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
        }
        int old = verticalScrollBarPolicy;
        verticalScrollBarPolicy = policy;
        firePropertyChange("verticalScrollBarPolicy", old, policy);
        revalidate();
        repaint();
    }

    /**
     * Returns the horizontal scroll bar policy value.
     * 
     * @return the <code>horizontalScrollBarPolicy</code> property
     * @see #setHorizontalScrollBarPolicy
     */
    public int getHorizontalScrollBarPolicy() {
        return horizontalScrollBarPolicy;
    }

    /**
     * Determines when the horizontal scrollbar appears in the scrollpane. The options are:
     * <ul>
     * <li>PIMScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
     * <li>PIMScrollPane.HORIZONTAL_SCROLLBAR_NEVER
     * <li>PIMScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
     * </ul>
     *
     * @see #getHorizontalScrollBarPolicy
     * @beaninfo preferred: true bound: true description: The scrollpane scrollbar policy enum:
     *           HORIZONTAL_SCROLLBAR_AS_NEEDED PIMScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED HORIZONTAL_SCROLLBAR_NEVER
     *           PIMScrollPane.HORIZONTAL_SCROLLBAR_NEVER HORIZONTAL_SCROLLBAR_ALWAYS
     *           PIMScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
     * @param policy
     *            one of the three values listed above
     */
    public void setHorizontalScrollBarPolicy(
            int policy) {
        switch (policy) {
            case HORIZONTAL_SCROLLBAR_AS_NEEDED:
            case HORIZONTAL_SCROLLBAR_NEVER:
            case HORIZONTAL_SCROLLBAR_ALWAYS:
                break;

            default:
                throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
        }
        int old = horizontalScrollBarPolicy;
        horizontalScrollBarPolicy = policy;
        firePropertyChange("horizontalScrollBarPolicy", old, policy);
        revalidate();
        repaint();
    }

    /**
     * Returns the <code>PIMScrollPaneBorder</code> object that surrounds the viewport.
     *
     * @return the <code>viewportBorder</code> property
     * @see #setViewportBorder
     */
    public PIMScrollPaneBorder getViewportBorder() {
        return viewportBorder;
    }

    /**
     * Adds a border around the viewport. Note that the border isn't set on the viewport directly,
     * <code>JViewport</code> doesn't support the <code>JComponent</code> border property. Similarly setting the
     * <code>PIMScrollPane</code>s viewport doesn't affect the <code>viewportBorder</code> property.
     * <p>
     * The default value of this property is computed by the look and feel implementation.
     *
     * @param viewportBorder
     *            the border to be added
     * @see #getViewportBorder
     * @see #setViewport
     *
     * @beaninfo preferred: true bound: true description: The border around the viewport.
     */
    public void setViewportBorder(
            PIMScrollPaneBorder viewportBorder) {
        PIMScrollPaneBorder oldValue = this.viewportBorder;
        this.viewportBorder = viewportBorder;
        firePropertyChange("viewportBorder", oldValue, viewportBorder);
    }

    /**
     * Returns the bounds of the viewport's border.
     *
     * @return a <code>Rectangle</code> object specifying the viewport border
     */
    public Rectangle getViewportBorderBounds() {
        Rectangle borderR = new Rectangle(getSize());

        Insets insets = getInsets();
        borderR.x = insets.left;
        borderR.y = insets.top;
        borderR.width -= insets.left + insets.right;
        borderR.height -= insets.top + insets.bottom;

        boolean leftToRight = true;

        /*
         * If there's a visible column header remove the space it needs from the top of borderR.
         */

        JViewport colHead = getColumnHeader();
        if ((colHead != null) && (colHead.isVisible())) {
            int colHeadHeight = colHead.getHeight();
            borderR.y += colHeadHeight;
            borderR.height -= colHeadHeight;
        }

        /*
         * If there's a visible row header remove the space it needs from the left of borderR.
         */

        JViewport rowHead = getRowHeader();
        if ((rowHead != null) && (rowHead.isVisible())) {
            int rowHeadWidth = rowHead.getWidth();
            if (leftToRight) {
                borderR.x += rowHeadWidth;
            }
            borderR.width -= rowHeadWidth;
        }

        /*
         * If there's a visible vertical scrollbar remove the space it needs from the width of borderR.
         */
        JScrollBar vsb = getVerticalScrollBar();
        if ((vsb != null) && (vsb.isVisible())) {
            int vsbWidth = vsb.getWidth();
            if (!leftToRight) {
                borderR.x += vsbWidth;
            }
            borderR.width -= vsbWidth;
        }

        /*
         * If there's a visible horizontal scrollbar remove the space it needs from the height of borderR.
         */
        JScrollBar hsb = getHorizontalScrollBar();
        if ((hsb != null) && (hsb.isVisible())) {
            borderR.height -= hsb.getHeight();
        }

        return borderR;
    }

    /**
     * By default <code>PIMScrollPane</code> creates scrollbars that are instances of this class. <code>Scrollbar</code>
     * overrides the <code>getUnitIncrement</code> and <code>getBlockIncrement</code> methods so that, if the viewport's
     * view is a <code>Scrollable</code>, the view is asked to compute these values. Unless the unit/block increment
     * have been explicitly set.
     * <p>
     * <strong>Warning:</strong> Serialized objects of this class will not be compatible with future Swing releases. The
     * current serialization support is appropriate for short term storage or RMI between applications running the same
     * version of Swing. As of 1.4, support for long term storage of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
     *
     * @see Scrollable
     * @see PIMScrollPane#createVerticalScrollBar
     * @see PIMScrollPane#createHorizontalScrollBar
     */
    public static class ScrollBar extends JScrollBar implements UIResource {
        /**
         * Set to true when the unit increment has been explicitly set. If this is false the viewport's view is obtained
         * and if it is an instance of <code>Scrollable</code> the unit increment from it is used.
         */
        private boolean unitIncrementSet;
        /**
         * Set to true when the block increment has been explicitly set. If this is false the viewport's view is
         * obtained and if it is an instance of <code>Scrollable</code> the block increment from it is used.
         */
        private boolean blockIncrementSet;
        PIMScrollPane scrolPane;

        /**
         * Creates a scrollbar with the specified orientation, where the options are:
         * <ul>
         * <li>PIMScrollPane.VERTICAL
         * <li>PIMScrollPane.HORIZONTAL
         * </ul>
         *
         * @param scrolPane
         *            本类引用
         * @param orientation
         *            an integer specifying one of the legal orientation values shown above
         */
        public ScrollBar(PIMScrollPane scrolPane, int orientation) {
            super(orientation);
            this.scrolPane = scrolPane;
        }

        /**
         * Messages super to set the value, and resets the <code>unitIncrementSet</code> instance variable to true.
         *
         * @param unitIncrement
         *            the new unit increment value, in pixels
         */
        public void setUnitIncrement(
                int unitIncrement) {
            unitIncrementSet = true;
            super.setUnitIncrement(unitIncrement);
        }

        /**
         * Computes the unit increment for scrolling if the viewport's view is a <code>Scrollable</code> object.
         * Otherwise return <code>super.getUnitIncrement</code>.
         *
         * @param direction
         *            less than zero to scroll up/left, greater than zero for down/right
         * @return an integer, in pixels, containing the unit increment
         * @see Scrollable#getScrollableUnitIncrement
         */
        public int getUnitIncrement(
                int direction) {
            JViewport vp = scrolPane.getViewport();
            if (!unitIncrementSet && (vp != null) && (vp.getView() instanceof Scrollable)) {
                Scrollable view = (Scrollable) (vp.getView());
                Rectangle vr = vp.getViewRect();
                return view.getScrollableUnitIncrement(vr, getOrientation(), direction);
            } else {
                return super.getUnitIncrement(direction);
            }
        }

        /**
         * Messages super to set the value, and resets the <code>blockIncrementSet</code> instance variable to true.
         *
         * @param blockIncrement
         *            the new block increment value, in pixels
         */
        public void setBlockIncrement(
                int blockIncrement) {
            blockIncrementSet = true;
            super.setBlockIncrement(blockIncrement);
        }

        /**
         * Computes the block increment for scrolling if the viewport's view is a <code>Scrollable</code> object.
         * Otherwise the <code>blockIncrement</code> equals the viewport's width or height. If there's no viewport
         * return <code>super.getBlockIncrement</code>.
         *
         * @param direction
         *            less than zero to scroll up/left, greater than zero for down/right
         * @return an integer, in pixels, containing the block increment
         * @see Scrollable#getScrollableBlockIncrement
         */
        public int getBlockIncrement(
                int direction) {
            JViewport vp = scrolPane.getViewport();
            if (blockIncrementSet || vp == null) {
                return super.getBlockIncrement(direction);
            } else if (vp.getView() instanceof Scrollable) {
                Scrollable view = (Scrollable) (vp.getView());
                Rectangle vr = vp.getViewRect();
                return view.getScrollableBlockIncrement(vr, getOrientation(), direction);
            } else if (getOrientation() == VERTICAL) {
                return vp.getExtentSize().height;
            } else {
                return vp.getExtentSize().width;
            }
        }

        /**
         * 重载鼠标处理事件方法,目的是绘网格线
         * 
         * @param e
         *            鼠标事件源
         */
        public void processMouseEvent(
                MouseEvent e) {
            super.processMouseEvent(e);
            if (e.getSource() == scrolPane.horizontalScrollBar && SwingUtilities.isLeftMouseButton(e)
                    && e.getID() == MouseEvent.MOUSE_RELEASED) {
                scrolPane.repaint();
            }
        }
    }

    /**
     * Returns a <code>PIMScrollPane.ScrollBar</code> by default. Subclasses may override this method to force
     * <code>PIMScrollPaneUI</code> implementations to use a <code>JScrollBar</code> subclass. Used by
     * <code>PIMScrollPaneUI</code> implementations to create the horizontal scrollbar.
     *
     * @return a <code>JScrollBar</code> with a horizontal orientation
     * @see JScrollBar
     */
    public JScrollBar createHorizontalScrollBar() {
        ScrollBar tmp = new ScrollBar(this, JScrollBar.HORIZONTAL);
        tmp.addAdjustmentListener(this);
        return tmp;
    }

    /**
     * Returns the horizontal scroll bar that controls the viewport's horizontal view position.
     *
     * @return the <code>horizontalScrollBar</code> property
     * @see #setHorizontalScrollBar
     */
    public JScrollBar getHorizontalScrollBar() {
        return horizontalScrollBar;
    }

    /**
     * Adds the scrollbar that controls the viewport's horizontal view position to the scrollpane. This is usually
     * unnecessary, as <code>PIMScrollPane</code> creates horizontal and vertical scrollbars by default.
     *
     * @param horizontalScrollBar
     *            the horizontal scrollbar to be added
     * @see #createHorizontalScrollBar
     * @see #getHorizontalScrollBar
     *
     * @beaninfo expert: true bound: true description: The horizontal scrollbar.
     */
    public void setHorizontalScrollBar(
            JScrollBar horizontalScrollBar) {
        JScrollBar old = getHorizontalScrollBar();
        this.horizontalScrollBar = horizontalScrollBar;
        add(horizontalScrollBar, HORIZONTAL_SCROLLBAR);
        firePropertyChange("horizontalScrollBar", old, horizontalScrollBar);

        revalidate();
        repaint();
    }

    /**
     * Returns a <code>PIMScrollPane.ScrollBar</code> by default. Subclasses may override this method to force
     * <code>PIMScrollPaneUI</code> implementations to use a <code>JScrollBar</code> subclass. Used by
     * <code>PIMScrollPaneUI</code> implementations to create the vertical scrollbar.
     *
     * @return a <code>JScrollBar</code> with a vertical orientation
     * @see JScrollBar
     */
    public JScrollBar createVerticalScrollBar() {
        return new ScrollBar(this, JScrollBar.VERTICAL);
    }

    /**
     * Returns the vertical scroll bar that controls the viewports vertical view position.
     *
     * @return the <code>verticalScrollBar</code> property
     * @see #setVerticalScrollBar
     */
    public JScrollBar getVerticalScrollBar() {
        return verticalScrollBar;
    }

    /**
     * Adds the scrollbar that controls the viewports vertical view position to the scrollpane. This is usually
     * unnecessary, as <code>PIMScrollPane</code> creates vertical and horizontal scrollbars by default.
     *
     * @param verticalScrollBar
     *            the new vertical scrollbar to be added
     * @see #createVerticalScrollBar
     * @see #getVerticalScrollBar
     *
     * @beaninfo expert: true bound: true description: The vertical scrollbar.
     */
    public void setVerticalScrollBar(
            JScrollBar verticalScrollBar) {
        JScrollBar old = getVerticalScrollBar();
        this.verticalScrollBar = verticalScrollBar;
        add(verticalScrollBar, VERTICAL_SCROLLBAR);
        firePropertyChange("verticalScrollBar", old, verticalScrollBar);

        revalidate();
        repaint();
    }

    /**
     * Returns a new <code>JViewport</code> by default. Used to create the viewport (as needed) in
     * <code>setViewportView</code>, <code>setRowHeaderView</code>, and <code>setColumnHeaderView</code>. Subclasses may
     * override this method to return a subclass of <code>JViewport</code>.
     *
     * @return a new <code>JViewport</code>
     */
    protected JViewport createViewport() {
        return new PIMViewport();
    }

    /**
     * Returns the current <code>JViewport</code>.
     *
     * @see #setViewport
     * @return the <code>viewport</code> property
     */
    public JViewport getViewport() {
        return viewport;
    }

    /**
     * Removes the old viewport (if there is one); forces the viewPosition of the new viewport to be in the +x,+y
     * quadrant; syncs up the row and column headers (if there are any) with the new viewport; and finally syncs the
     * scrollbars and headers with the new viewport.
     * <p>
     * Most applications will find it more convenient to use <code>setViewportView</code> to add a viewport and a view
     * to the scrollpane.
     *
     * @param viewport
     *            the new viewport to be used; if viewport is <code>null</code>, the old viewport is still removed and
     *            the new viewport is set to <code>null</code>
     * @see #createViewport
     * @see #getViewport
     * @see #setViewportView
     *
     * @beaninfo expert: true bound: true attribute: visualUpdate true description: The viewport child for this
     *           scrollpane
     *
     */
    public void setViewport(
            JViewport viewport) {
        JViewport old = getViewport();
        this.viewport = viewport;
        if (viewport != null) {
            add(viewport, VIEWPORT);
        } else if (old != null) {
            remove(old);
        }
        firePropertyChange("viewport", old, viewport);

        revalidate();
        repaint();
    }

    /**
     * Creates a viewport if necessary and then sets its view. Applications that don't provide the view directly to the
     * <code>PIMScrollPane</code> constructor should use this method to specify the scrollable child that's going to be
     * displayed in the scrollpane. For example:
     * 
     * <pre>
     * PIMScrollPane scrollpane = new PIMScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * </pre>
     * 
     * Applications should not add children directly to the scrollpane.
     *
     * @param view
     *            the component to add to the viewport
     * @see #setViewport
     * @see JViewport#setView
     */
    public void setViewportView(
            Component view) {
        if (getViewport() == null) {
            setViewport(createViewport());
        }
        getViewport().setView(view);
    }

    /**
     * Returns the row header.
     * 
     * @return the <code>rowHeader</code> property
     * @see #setRowHeader
     */
    public JViewport getRowHeader() {
        return rowHeader;
    }

    /**
     * Removes the old rowHeader, if it exists. If the new rowHeader isn't <code>null</code>, syncs the y coordinate of
     * its viewPosition with the viewport (if there is one) and then adds it to the scrollpane.
     * <p>
     * Most applications will find it more convenient to use <code>setRowHeaderView</code> to add a row header component
     * and its viewport to the scrollpane.
     *
     * @param rowHeader
     *            the new row header to be used; if <code>null</code> the old row header is still removed and the new
     *            rowHeader is set to <code>null</code>
     * @see #getRowHeader
     * @see #setRowHeaderView
     *
     * @beaninfo bound: true expert: true description: The row header child for this scrollpane
     */
    public void setRowHeader(
            JViewport rowHeader) {
        JViewport old = getRowHeader();
        this.rowHeader = rowHeader;
        if (rowHeader != null) {
            add(rowHeader, ROW_HEADER);
        } else if (old != null) {
            remove(old);
        }
        firePropertyChange("rowHeader", old, rowHeader);
        revalidate();
        repaint();
    }

    /**
     * Creates a row-header viewport if necessary, sets its view and then adds the row-header viewport to the
     * scrollpane. For example:
     * 
     * <pre>
     * PIMScrollPane scrollpane = new PIMScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * scrollpane.setRowHeaderView(myBigComponentsRowHeader);
     * </pre>
     *
     * @see #setRowHeader
     * @see JViewport#setView
     * @param view
     *            the component to display as the row header
     */
    public void setRowHeaderView(
            Component view) {
        if (getRowHeader() == null) {
            setRowHeader(createViewport());
        }
        getRowHeader().setView(view);
    }

    /**
     * Returns the column header.
     * 
     * @return the <code>columnHeader</code> property
     * @see #setColumnHeader
     */
    public JViewport getColumnHeader() {
        return columnHeader;
    }

    /**
     * Removes the old columnHeader, if it exists. If the new columnHeader isn't <code>null</code>, sync the x
     * coordinate of the its viewPosition with the viewport (if there is one) and then add it to the scrollpane.
     * <p>
     * Most applications will find it more convenient to use <code>setRowHeaderView</code> to add a row header component
     * and its viewport to the scrollpane.
     *
     * @see #getColumnHeader
     * @see #setColumnHeaderView
     * @beaninfo bound: true description: The column header child for this scrollpane attribute: visualUpdate true
     * @param columnHeader
     *            列头
     */
    public void setColumnHeader(
            JViewport columnHeader) {
        JViewport old = getColumnHeader();
        this.columnHeader = columnHeader;
        if (columnHeader != null) {
            add(columnHeader, COLUMN_HEADER);
        } else if (old != null) {
            remove(old);
        }
        firePropertyChange("columnHeader", old, columnHeader);

        revalidate();
        repaint();
    }

    /**
     * Creates a column-header viewport if necessary, sets its view, and then adds the column-header viewport to the
     * scrollpane. For example:
     * 
     * <pre>
     * PIMScrollPane scrollpane = new PIMScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * scrollpane.setColumnHeaderView(myBigComponentsColumnHeader);
     * </pre>
     *
     * @see #setColumnHeader
     * @see JViewport#setView
     *
     * @param view
     *            the component to display as the column header
     */
    public void setColumnHeaderView(
            Component view) {
        if (getColumnHeader() == null) {
            setColumnHeader(createViewport());
        }
        getColumnHeader().setView(view);
    }

    /**
     * Returns the component at the specified corner. The <code>key</code> value specifying the corner is one of:
     * <ul>
     * <li>PIMScrollPane.LOWER_LEFT_CORNER
     * <li>PIMScrollPane.LOWER_RIGHT_CORNER
     * <li>PIMScrollPane.UPPER_LEFT_CORNER
     * <li>PIMScrollPane.UPPER_RIGHT_CORNER
     * <li>PIMScrollPane.LOWER_LEADING_CORNER
     * <li>PIMScrollPane.LOWER_TRAILING_CORNER
     * <li>PIMScrollPane.UPPER_LEADING_CORNER
     * <li>PIMScrollPane.UPPER_TRAILING_CORNER
     * </ul>
     *
     * @param key
     *            one of the values as shown above
     * @return one of the components listed below or <code>null</code> if <code>key</code> is invalid:
     *         <ul>
     *         <li>lowerLeft
     *         <li>lowerRight
     *         <li>upperLeft
     *         <li>upperRight
     *         </ul>
     * @see #setCorner
     */
    public Component getCorner(
            String key) {
        if (key.equals(LOWER_LEFT_CORNER)) {
            return lowerLeft;
        } else if (key.equals(LOWER_RIGHT_CORNER)) {
            return lowerRight;
        } else if (key.equals(UPPER_LEFT_CORNER)) {
            return upperLeft;
        } else if (key.equals(UPPER_RIGHT_CORNER)) {
            return upperRight;
        } else {
            return null;
        }
    }

    /**
     * Adds a child that will appear in one of the scroll panes corners, if there's room. For example with both
     * scrollbars showing (on the right and bottom edges of the scrollpane) the lower left corner component will be
     * shown in the space between ends of the two scrollbars. Legal values for the <b>key</b> are:
     * <ul>
     * <li>PIMScrollPane.LOWER_LEFT_CORNER
     * <li>PIMScrollPane.LOWER_RIGHT_CORNER
     * <li>PIMScrollPane.UPPER_LEFT_CORNER
     * <li>PIMScrollPane.UPPER_RIGHT_CORNER
     * <li>PIMScrollPane.LOWER_LEADING_CORNER
     * <li>PIMScrollPane.LOWER_TRAILING_CORNER
     * <li>PIMScrollPane.UPPER_LEADING_CORNER
     * <li>PIMScrollPane.UPPER_TRAILING_CORNER
     * </ul>
     * <p>
     * Although "corner" doesn't match any beans property signature, <code>PropertyChange</code> events are generated
     * with the property name set to the corner key.
     *
     * @param key
     *            identifies which corner the component will appear in
     * @param corner
     *            one of the following components:
     *            <ul>
     *            <li>lowerLeft <li>lowerRight <li>upperLeft <li>upperRight
     *            </ul>
     */
    public void setCorner(
            String key,
            Component corner) {
        Component old;
        if (key.equals(LOWER_LEFT_CORNER)) {
            old = lowerLeft;
            lowerLeft = corner;
        } else if (key.equals(LOWER_RIGHT_CORNER)) {
            old = lowerRight;
            lowerRight = corner;
        } else if (key.equals(UPPER_LEFT_CORNER)) {
            old = upperLeft;
            upperLeft = corner;
        } else if (key.equals(UPPER_RIGHT_CORNER)) {
            old = upperRight;
            upperRight = corner;
        } else {
            throw new IllegalArgumentException("invalid corner key");
        }
        add(corner, key);
        firePropertyChange(key, old, corner);
        revalidate();
        repaint();
    }

    /**
     * Sets the orientation for the vertical and horizontal scrollbars as determined by the
     * <code>ComponentOrientation</code> argument.
     *
     * @param co
     *            one of the following values:
     *            <ul>
     *            <li>java.awt.ComponentOrientation.LEFT_TO_RIGHT
     *            <li>java.awt.ComponentOrientation.RIGHT_TO_LEFT
     *            <li>java.awt.ComponentOrientation.UNKNOWN
     *            </ul>
     * @see java.awt.ComponentOrientation
     */
    public void setComponentOrientation(
            ComponentOrientation co) {
        super.setComponentOrientation(co);
        if (verticalScrollBar != null) {
            verticalScrollBar.setComponentOrientation(co);
        }
        if (horizontalScrollBar != null) {
            horizontalScrollBar.setComponentOrientation(co);
        }
    }

    /**
     * Indicates whether or not scrolling will take place in response to the mouse wheel. Wheel scrolling is enabled by
     * default.
     *
     * @see #setWheelScrollingEnabled
     * @since 1.4
     * @beaninfo bound: true description: Flag for enabling/disabling mouse wheel scrolling
     * @return 鼠标滚球是否有效
     */
    public boolean isWheelScrollingEnabled() {
        return wheelScrollState;
    }

    /**
     * Enables/disables scrolling in response to movement of the mouse wheel. Wheel scrolling is enabled by default.
     *
     * @param handleWheel
     *            <code>true</code> if scrolling should be done automatically for a MouseWheelEvent, <code>false</code>
     *            otherwise.
     * @see #isWheelScrollingEnabled
     * @see java.awt.event.MouseWheelEvent
     * @see java.awt.event.MouseWheelListener
     * @since 1.4
     * @beaninfo bound: true description: Flag for enabling/disabling mouse wheel scrolling
     */
    public void setWheelScrollingEnabled(
            boolean handleWheel) {
        boolean old = wheelScrollState;
        wheelScrollState = handleWheel;
        firePropertyChange("wheelScrollingEnabled", old, handleWheel);
    }

    /**
     * See <code>readObject</code> and <code>writeObject</code> in <code>JComponent</code> for more information about
     * serialization in Swing.
     */
    private void writeObject(
            ObjectOutputStream s) throws IOException {
    }

    /**
     * Returns a string representation of this <code>PIMScrollPane</code>. This method is intended to be used only for
     * debugging purposes, and the content and format of the returned string may vary between implementations. The
     * returned string may be empty but may not be <code>null</code>.
     *
     * @return a string representation of this <code>PIMScrollPane</code>.
     */
    protected String paramString() {
        String viewportBorderString = (viewportBorder != null ? viewportBorder.toString() : CASUtility.EMPTYSTR);
        String viewportString = (viewport != null ? viewport.toString() : CASUtility.EMPTYSTR);
        String verticalScrollBarPolicyString;
        if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_AS_NEEDED";
        } else if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_NEVER) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_NEVER";
        } else if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_ALWAYS";
        } else {
            verticalScrollBarPolicyString = CASUtility.EMPTYSTR;
        }
        String horizontalScrollBarPolicyString;
        if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_AS_NEEDED";
        } else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_NEVER";
        } else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_ALWAYS";
        } else {
            horizontalScrollBarPolicyString = CASUtility.EMPTYSTR;
        }
        String horizontalScrollBarString =
                (horizontalScrollBar != null ? horizontalScrollBar.toString() : CASUtility.EMPTYSTR);
        String verticalScrollBarString =
                (verticalScrollBar != null ? verticalScrollBar.toString() : CASUtility.EMPTYSTR);
        String columnHeaderString = (columnHeader != null ? columnHeader.toString() : CASUtility.EMPTYSTR);
        String rowHeaderString = (rowHeader != null ? rowHeader.toString() : CASUtility.EMPTYSTR);
        String lowerLeftString = (lowerLeft != null ? lowerLeft.toString() : CASUtility.EMPTYSTR);
        String lowerRightString = (lowerRight != null ? lowerRight.toString() : CASUtility.EMPTYSTR);
        String upperLeftString = (upperLeft != null ? upperLeft.toString() : CASUtility.EMPTYSTR);
        String upperRightString = (upperRight != null ? upperRight.toString() : CASUtility.EMPTYSTR);

        return super.paramString() + ",columnHeader=" + columnHeaderString + ",horizontalScrollBar="
                + horizontalScrollBarString + ",horizontalScrollBarPolicy=" + horizontalScrollBarPolicyString
                + ",lowerLeft=" + lowerLeftString + ",lowerRight=" + lowerRightString + ",rowHeader=" + rowHeaderString
                + ",upperLeft=" + upperLeftString + ",upperRight=" + upperRightString + ",verticalScrollBar="
                + verticalScrollBarString + ",verticalScrollBarPolicy=" + verticalScrollBarPolicyString + ",viewport="
                + viewportString + ",viewportBorder=" + viewportBorderString;
    }

    /**
     * Invoked when the value of the adjustable has changed.
     * 
     * @param e
     *            调整事件
     */
    public void adjustmentValueChanged(
            AdjustmentEvent e) {
        repaint();
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        viewport = null;
        verticalScrollBar = null;
        rowHeader = null;
        horizontalScrollBar = null;
        columnHeader = null;
    }
}
