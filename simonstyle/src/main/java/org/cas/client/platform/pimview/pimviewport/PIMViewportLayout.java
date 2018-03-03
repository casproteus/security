package org.cas.client.platform.pimview.pimviewport;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.Scrollable;

import org.cas.client.platform.pimview.pimtable.PIMTable;

public class PIMViewportLayout implements LayoutManager, Serializable {
    /**
     * Adds the specified component to the layout. Not used by this class.
     * 
     * @param name
     *            the name of the component
     * @param c
     *            the the component to be added
     */
    public void addLayoutComponent(
            String name,
            Component c) {
    }

    /**
     * Removes the specified component from the layout. Not used by this class.
     * 
     * @param c
     *            the component to remove
     */
    public void removeLayoutComponent(
            Component c) {
    }

    /**
     * Returns the preferred dimensions for this layout given the components in the specified target container.
     * 
     * @param parent
     *            the component which needs to be laid out
     * @return a <code>Dimension</code> object containing the preferred dimensions
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(
            Container parent) {
        Component view = ((PIMViewport) parent).getView();
        if (view == null) {
            return new Dimension(0, 0);
        } else if (view instanceof Scrollable) {
            return ((Scrollable) view).getPreferredScrollableViewportSize();
        } else {
            return view.getPreferredSize();
        }
    }

    /**
     * Returns the minimum dimensions needed to layout the components contained in the specified target container.
     *
     * @param parent
     *            the component which needs to be laid out
     * @return a <code>Dimension</code> object containing the minimum dimensions
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(
            Container parent) {
        return new Dimension(4, 4);
    }

    /**
     * Called by the AWT when the specified container needs to be laid out.
     *
     * @param parent
     *            the container to lay out
     */
    public void layoutContainer(
            Container parent) {
        PIMViewport vp = (PIMViewport) parent;
        Component view = vp.getView();
        Scrollable scrollableView = null;

        if (view == null) {
            return;
        } else if (view instanceof Scrollable) {
            scrollableView = (Scrollable) view;
        }

        /*
         * All of the dimensions below are in view coordinates, except vpSize which we're converting.
         */
        // 以下所有尺寸都是在表格的坐标系中,除了我们要转化的PIMViewport的尺寸
        Dimension viewPrefSize = view.getPreferredSize();

        // 这是PIMViewport的尺寸
        Dimension vpSize = vp.getSize();
        Dimension extentSize = vp.toViewCoordinates(vpSize);

        // 这是表格实际所需的尺寸
        Dimension viewSize = new Dimension(viewPrefSize);

        if (scrollableView != null) {
            if (scrollableView.getScrollableTracksViewportWidth()) {
                // 在视图表格上,这句不执行
                viewSize.width = vpSize.width;
            }
            if (scrollableView.getScrollableTracksViewportHeight()) {
                // 在视图表格上,这句不执行
                viewSize.height = vpSize.height;
            }
        }

        Point viewPosition = vp.getViewPosition();
        /*
         * If the new viewport size would leave empty space to the right of the view, right justify the view or left
         * justify the view when the width of the view is smaller than the container.
         */
        // 当表格宽度小于本容器时,如果新视口尺寸右边留有空余,右边调整
        if (scrollableView == null || vp.getParent() == null
                || vp.getParent().getComponentOrientation().isLeftToRight()) {
            if ((viewPosition.x + extentSize.width) > viewSize.width) {
                // TMD 为什么在表格缩小宽度时会执行以下语句? 暂不管它
                // if(view instanceof PIMTable)
                // {
                // PIMTable table = (PIMTable)view;
                // //delta = oldViewPositionWidth - table.getPreferredSize().width ;
                // if(table.getView() != null && table.getTableHeader().getResizingColumn() != null )//&&
                // //delta > 0 )
                // {
                // }
                // else
                // {
                // //viewPosition.x = Math.max(0, viewSize.width - extentSize.width);
                // }
                // }
            }
        } else {
            // 以下的在视图表格宽度减小时也不执行
            if (extentSize.width > viewSize.width) {
                viewPosition.x = viewSize.width - extentSize.width;
            } else {
                viewPosition.x = Math.max(0, Math.min(viewSize.width - extentSize.width, viewPosition.x));
            }
        }

        /*
         * If the new viewport size would leave empty space below the view, bottom justify the view or top justify the
         * view when the height of the view is smaller than the container.
         */
        if ((viewPosition.y + extentSize.height) > viewSize.height) {
            viewPosition.y = Math.max(0, viewSize.height - extentSize.height);
        }

        /*
         * If we haven't been advised about how the viewports size should change wrt to the viewport, i.e. if the view
         * isn't an instance of Scrollable, then adjust the views size as follows. If the origin of the view is showing
         * and the viewport is bigger than the views preferred size, then make the view the same size as the viewport.
         */
        int delta;
        if (scrollableView == null) {
            // 在视图表格上,这句不执行
            if ((viewPosition.x == 0) && (vpSize.width > viewPrefSize.width)) {
                viewSize.width = vpSize.width;
            }
            if ((viewPosition.y == 0) && (vpSize.height > viewPrefSize.height)) {
                viewSize.height = vpSize.height;
            }
        }
        // */
        if (oldViewPositionX != viewPosition.x) {
            if (view instanceof PIMTable) {
                PIMTable table = (PIMTable) view;
                delta = oldViewPositionWidth - table.getPreferredSize().width;
                if (table.getView() != null && table.getTableHeader().getResizingColumn() != null && delta > 0) {
                    // 现在要处理的时机是找到了,只是不知道如何正确来处理
                    viewPosition = new Point(viewPosition.x + delta, viewPosition.y);
                    viewSize = new Dimension(viewSize.width + delta, viewSize.height);
                    vp.setToggle(true);
                    // PIMScrollPane scrollPane = (PIMScrollPane)vp.getParent();
                    // JScrollBar hsb = scrollPane.getHorizontalScrollBar();
                }
            }
        } else {
            vp.setToggle(false);
        }
        // */
        vp.setViewPosition(viewPosition);
        vp.setViewSize(viewSize);
        oldViewPositionX = viewPosition.x;
        oldViewPositionWidth = viewPrefSize.width;
    }

    private int oldViewPositionX;
    private int oldViewPositionWidth;
}
