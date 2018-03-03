package org.cas.client.platform.pimview.pimviewport;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.RepaintManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ViewportUI;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;

public class PIMViewport extends JViewport implements MouseListener {

    static {
        UIManager.getDefaults().put("PIMViewportUI", "org.cas.client.platform.pimview.pimviewport.BasicPIMViewportUI");
    }

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "PIMViewportUI";

    /** Creates a <code>PIMViewport</code>. */
    public PIMViewport() {
        super();
        updateUI();
        setLayout(createLayoutManager());
        setOpaque(true);
        addMouseListener(this);
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return a <code>ViewportUI</code> object
     */
    public ViewportUI getUI() {
        return (ViewportUI) ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui
     *            the <code>ViewportUI</code> L&F object
     * @see UIDefaults#getUI
     * @beaninfo bound: true hidden: true attribute: visualUpdate true description: The UI object that implements the
     *           Component's LookAndFeel.
     */
    public void setUI(
            ViewportUI ui) {
        super.setUI(ui);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((PIMViewportUI) UIManager.getUI(this));
    }

    /**
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return the string "ViewportUI"
     *
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Scrolls the view so that <code>Rectangle</code> within the view becomes visible.
     * <p>
     * This attempts to validate the view before scrolling if the view is currently not valid - <code>isValid</code>
     * returns false. To avoid excessive validation when the containment hierarchy is being created this will not
     * validate if one of the ancestors does not have a peer, or there is no validate root ancestor, or one of the
     * ancestors is not a <code>Window</code> or <code>Applet</code>.
     * <p>
     * Note that this method will not scroll outside of the valid viewport; for example, if <code>contentRect</code> is
     * larger than the viewport, scrolling will be confined to the viewport's bounds.
     *
     * @param contentRect
     *            the <code>Rectangle</code> to display
     * @see JComponent#isValidateRoot
     * @see java.awt.Component#isValid
     * @see java.awt.Component#getPeer
     */
    public void scrollRectToVisible(
            Rectangle contentRect) {
        Component view = getView();

        if (view == null) {
            return;
        } else {
            if (!view.isValid()) {
                // If the view is not valid, validate. scrollRectToVisible
                // may fail if the view is not valid first, contentRect
                // could be bigger than invalid size.
                validateView();
            }
            int dx = 0, dy = 0;

            dx = positionAdjustment(getWidth(), contentRect.width, contentRect.x);
            dy = positionAdjustment(getHeight(), contentRect.height, contentRect.y);

            if (dx != 0 || dy != 0) {
                Point viewPosition = getViewPosition();
                Dimension viewSize = view.getSize();
                int startX = viewPosition.x;
                int startY = viewPosition.y;
                Dimension extent = getExtentSize();

                viewPosition.x -= dx;
                viewPosition.y -= dy;
                // Only constrain the location if the view is valid. If the
                // the view isn't valid, it typically indicates the view
                // isn't visible yet and most likely has a bogus size as will
                // we, and therefore we shouldn't constrain the scrolling
                if (view.isValid()) {
                    if (getParent().getComponentOrientation().isLeftToRight()) {
                        if (viewPosition.x + extent.width > viewSize.width) {
                            viewPosition.x = Math.max(0, viewSize.width - extent.width);
                        } else if (viewPosition.x < 0) {
                            viewPosition.x = 0;
                        }
                    } else {
                        if (extent.width > viewSize.width) {
                            viewPosition.x = viewSize.width - extent.width;
                        } else {
                            viewPosition.x = Math.max(0, Math.min(viewSize.width - extent.width, viewPosition.x));
                        }
                    }
                    if (viewPosition.y + extent.height > viewSize.height) {
                        viewPosition.y = Math.max(0, viewSize.height - extent.height);
                    } else if (viewPosition.y < 0) {
                        viewPosition.y = 0;
                    }
                }
                // if ((viewPosition.x != startX || viewPosition.y != startY) && !toggleFlag) {
                if (viewPosition.x != startX || viewPosition.y != startY) {
                    setViewPosition(viewPosition);
                    // NOTE: How JViewport currently works with the
                    // backing store is not foolproof. The sequence of
                    // events when setViewPosition
                    // (scrollRectToVisible) is called is to reset the
                    // views bounds, which causes a repaint on the
                    // visible region and sets an ivar indicating
                    // scrolling (scrollUnderway). When
                    // JViewport.paint is invoked if scrollUnderway is
                    // true, the backing store is blitted. This fails
                    // if between the time setViewPosition is invoked
                    // and paint is received another repaint is queued
                    // indicating part of the view is invalid. There
                    // is no way for JViewport to notice another
                    // repaint has occured and it ends up blitting
                    // what is now a dirty region and the repaint is
                    // never delivered.
                    // It just so happens JTable encounters this
                    // behavior by way of scrollRectToVisible, for
                    // this reason scrollUnderway is set to false
                    // here, which effectively disables the backing
                    // store.
                    scrollUnderway = false;
                }
            }
        }
    }

    /**
     * 设置锁定,以后要去除
     * 
     * @param isToggle
     *            是否锁定
     */
    public void setToggle(
            boolean isToggle) {
        toggleFlag = isToggle;
    }

    /**
     * 得到是否锁定,以后要去除
     * 
     * @return 是否锁定
     */
    public boolean getToggle() {
        return toggleFlag;
    }

    private boolean toggleFlag;

    /**
     * Ascends the <code>Viewport</code>'s parents stopping when a component is found that returns <code>true</code> to
     * <code>isValidateRoot</code>. If all the <code>Component</code>'s parents are visible, <code>validate</code> will
     * then be invoked on it. The <code>RepaintManager</code> is then invoked with <code>removeInvalidComponent</code>.
     * This is the synchronous version of a <code>revalidate</code>.
     */
    private void validateView() {
        Component validateRoot = null;

        /*
         * Find the first JComponent ancestor of this component whose isValidateRoot() method returns true.
         */
        for (Component c = this; c != null; c = c.getParent()) {
            if ((c instanceof CellRendererPane) || (c.getPeer() == null)) {
                return;
            }
            if ((c instanceof JComponent) && (((JComponent) c).isValidateRoot())) {
                validateRoot = c;
                break;
            }
        }

        // If no validateRoot, nothing to validate from.
        if (validateRoot == null) {
            return;
        }

        // Make sure all ancestors are visible.
        Component root = null;

        for (Component c = validateRoot; c != null; c = c.getParent()) {
            // We don't check isVisible here, otherwise if the component
            // is contained in something like a JTabbedPane when the
            // component is made visible again it won't have scrolled
            // to the correct location.
            if (c.getPeer() == null) {
                return;
            }
            if ((c instanceof Window) || (c instanceof Applet)) {
                root = c;
                break;
            }
        }

        // Make sure there is a Window ancestor.
        if (root == null) {
            return;
        }

        // Validate the root.
        validateRoot.validate();

        // And let the RepaintManager it does not have to validate from
        // validateRoot anymore.
        RepaintManager rm = RepaintManager.currentManager(this);

        if (rm != null) {
            rm.removeInvalidComponent((JComponent) validateRoot);
        }
    }

    /*
     * Used by the scrollRectToVisible method to determine the proper direction and amount to move by. The integer
     * variables are named width, but this method is applicable to height also. The code assumes that
     * parentWidth/childWidth are positive and childAt can be negative.
     */
    private int positionAdjustment(
            int parentWidth,
            int childWidth,
            int childAt) {

        // +-----+
        // | --- | No Change
        // +-----+
        if (childAt >= 0 && childWidth + childAt <= parentWidth) {
            return 0;
        }

        // +-----+
        // --------- No Change
        // +-----+
        if (childAt <= 0 && childWidth + childAt >= parentWidth) {
            return 0;
        }

        // +-----+ +-----+
        // | ---- -> | ----|
        // +-----+ +-----+
        if (childAt > 0 && childWidth <= parentWidth) {
            return -childAt + parentWidth - childWidth;
        }

        // +-----+ +-----+
        // | -------- -> |--------
        // +-----+ +-----+
        if (childAt >= 0 && childWidth >= parentWidth) {
            return -childAt;
        }

        // +-----+ +-----+
        // ---- | -> |---- |
        // +-----+ +-----+
        if (childAt <= 0 && childWidth <= parentWidth) {
            return -childAt;
        }

        // +-----+ +-----+
        // -------- | -> --------|
        // +-----+ +-----+
        if (childAt < 0 && childWidth >= parentWidth) {
            return -childAt + parentWidth - childWidth;
        }

        return 0;
    }

    /**
     * Depending on whether the <code>backingStore</code> is enabled, either paint the image through the backing store
     * or paint just the recently exposed part, using the backing store to "blit" the remainder. <blockquote> The term
     * "blit" is the pronounced version of the PDP-10 BLT (BLock Transfer) instruction, which copied a block of bits.
     * (In case you were curious.) </blockquote>
     *
     * @param g
     *            the <code>Graphics</code> context within which to paint
     */
    public void paint(
            Graphics g) {
        super.paint(g);
        if (!(getView() instanceof PIMTable)) {
            return;
        }
        PIMTable table = (PIMTable) getView();
        // BasicPIMTableUI tableUI = (BasicPIMTableUI)table.getUI();
        Dimension tableRe = table.getPreferredSize();
        if (tableRe.height >= getSize().height) {
            return;
        }
        drawLine(g, table, tableRe);
    }

    /**
     * 在视口上画线
     */
    private void drawLine(
            Graphics g,
            PIMTable table,
            Dimension tableRe) {
        // 得到滚动了后的坐标系
        Point offsetPoint = getViewPosition();

        // 这是在X轴方向应减的偏移,
        int Xoffset = offsetPoint.x;
        // 绘制的起始Y坐标就是表格的高度
        int startY = tableRe.height;
        int startX = 0;

        int deltaWidth = tableRe.width - Xoffset;
        // 这是绘制区域的宽度
        int horizontalLineWidth = tableRe.width > getSize().width ? deltaWidth : tableRe.width;
        int viewportHeitht = getSize().height;
        // 这是绘制区域的高度
        int VerticalLineHeight = viewportHeitht - tableRe.height;
        // 绘制间隔行高
        int horizontalStep = table.getRowHeight();
        // TODO：先消除以前画的痕迹。

        g.setColor(CustOpts.custOps.getContentBG());

        g.fillRect(startX, startY, horizontalLineWidth, VerticalLineHeight);
        g.setColor(CustOpts.custOps.getGridColor());
        // 如果视图上有水平线
        if (table.getShowHorizontalLines()) {
            for (int y = startY; y <= viewportHeitht; y += horizontalStep) {
                g.drawLine(startX, y, startX + horizontalLineWidth - 1, y);
            }
        }
        // 如果视图上有竖直线
        if (table.getShowVerticalLines()) {
            IPIMTableColumnModel cm = table.getColumnModel();
            int ColumnCount = cm.getColumnCount();
            int x = Xoffset > 0 ? -Xoffset : 0;
            for (int column = 0; column < ColumnCount; column++) {
                x += cm.getColumn(column).getWidth();

                if (x <= 0) // || x > getSize().width)
                {
                    continue;
                }
                // 为了偏移了的一个像素的问题
                // 我目前还没有查出是如何产生的.
                // 表格中的处理是把最后一列的像素减个1
                // 因偏移一个像素,在表格中处理后,在这里只最后一行要减个1
                if (column == ColumnCount - 1) {
                    // if(x == getWidth())
                    // {
                    // x--;
                    // }

                } else {
                    // 因表格最后一列的像素少1,所以表格倒数第二条线在可见的情况下
                    // 要减个1
                    int tmp = cm.getColumn(column + 1).getWidth();
                    if (x + tmp >= getWidth()) {
                        x--;
                    }
                    // 这种情况表示表格下的水平滚动条滑动了最后,表格尺寸变小,上面
                    // IF的这种情况判断不出来,只好写死为倒数第二列
                    else if (column == ColumnCount - 2) {
                        x--;
                    }
                }
                g.drawLine(x, startY, x, viewportHeitht);
            }
        }
    }

    /**
     * 重载,目的是画表格的网格线
     * 
     * @param tm
     *            时间
     * @param x
     *            X坐标
     * @param y
     *            Y坐标
     * @param w
     *            宽度
     * @param h
     *            高度
     */
    public void repaint(
            long tm,
            int x,
            int y,
            int w,
            int h) {
        super.repaint(tm, x, y, w, h);
        if (!(getView() instanceof PIMTable)) {
            return;
        }
        PIMTable table = (PIMTable) getView();
        Dimension tableRe = table.getPreferredSize();
        if (tableRe.height >= getSize().height) {
            return;
        }
        drawLine(getGraphics(), table, tableRe);
    }

    /**
     * Computes the parameters for a blit where the backing store image currently contains <code>oldLoc</code> in the
     * upper left hand corner and we're scrolling to <code>newLoc</code>. The parameters are modified to return the
     * values required for the blit.
     *
     * @param dx
     *            the horizontal delta
     * @param dy
     *            the vertical delta
     * @param blitFrom
     *            the <code>Point</code> we're blitting from
     * @param blitTo
     *            the <code>Point</code> we're blitting to
     * @param blitSize
     *            the <code>Dimension</code> of the area to blit
     * @param blitPaint
     *            the area to blit
     * @return true if the parameters are modified and we're ready to blit; false otherwise
     */
    protected boolean computeBlit(
            int dx,
            int dy,
            Point blitFrom,
            Point blitTo,
            Dimension blitSize,
            Rectangle blitPaint) {
        return super.computeBlit(dx, dy, blitFrom, blitTo, blitSize, blitPaint);
    }

    /**
     * Returns the size of the visible part of the view in view coordinates.
     *
     * @return a <code>Dimension</code> object giving the size of the view
     */
    public Dimension getExtentSize() {
        return getSize();
    }

    /**
     * Converts a size in pixel coordinates to view coordinates. Subclasses of viewport that support
     * "logical coordinates" will override this method.
     *
     * @param size
     *            a <code>Dimension</code> object using pixel coordinates
     * @return a <code>Dimension</code> object converted to view coordinates
     */
    public Dimension toViewCoordinates(
            Dimension size) {
        return new Dimension(size);
    }

    /**
     * Converts a point in pixel coordinates to view coordinates. Subclasses of viewport that support
     * "logical coordinates" will override this method.
     *
     * @param p
     *            a <code>Point</code> object using pixel coordinates
     * @return a <code>Point</code> object converted to view coordinates
     */
    public Point toViewCoordinates(
            Point p) {
        return new Point(p);
    }

    /**
     * Sets the size of the visible part of the view using view coordinates.
     *
     * @param newExtent
     *            a <code>Dimension</code> object specifying the size of the view
     */
    public void setExtentSize(
            Dimension newExtent) {
        super.setExtentSize(newExtent);
    }

    /**
     * Subclassers can override this to install a different layout manager (or <code>null</code>) in the constructor.
     * Returns a new <code>ViewportLayout</code> object.
     *
     * @return a <code>LayoutManager</code>
     */
    protected LayoutManager createLayoutManager() {
        return new PIMViewportLayout();
    }

    /**
     * 设置视口点
     * 
     * @param p
     *            视口点
     */
    public void setViewPosition(
            Point p) {
        // 调试结果表明,看来除了视口的布局器在布局行为发生时,会改变视点的X坐标
        // 其他组件的行为没有再设X坐标.
        super.setViewPosition(p);
    }

    /**
     * 设置视口尺寸
     * 
     * @param newSize
     *            视口尺寸
     */
    public void setViewSize(
            Dimension newSize) {
        super.setViewSize(newSize);
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        repaint();
    }

}
