package org.cas.client.platform.pimview.pimtable.tagcombo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboPopup;


public class TagComboPopup extends BasicComboPopup
{

    /** 本组合框的引用
     */    
    private JComboBox combo;
    /** Implementation of ComboPopup.getList().
     * @return 得到List 组件 */
    public JList getList()
    {
        return list;
    }
    //===================================================================
    // begin Initialization routines
    //
    /** 构建器,传入引用
     * @param combo 传入引用 */    
    public TagComboPopup(JComboBox combo)
    {
        super(combo);
        this.combo = combo;

    }

    /**
     * Creates the JList used in the popup to display
     * the items in the combo box model. This method is called when the UI class
     * is created.
     *
     * @return a <code>JList</code> used to display the combo box items
     */
    protected JList createList()
    {
        return new TagList(this,comboBox.getModel());
    }

    /**
     *  本类用来去匿名类;
     */
    public static class TagList extends JList
    {
        /** 本类引用
         */        
        TagComboPopup popup;
        /*
         * construct
         */
        /** 构建器
         * @param popup 弹出体
         * @param model 数据模型
         */        
        public TagList(TagComboPopup prmPopup, ListModel prmModel)
        {
            super(prmModel);
            popup = prmPopup;
        }
        /** 这是从JDK抄的.
         * @param e 鼠标事件源 */
        public void processMouseEvent(MouseEvent e)
        {
            if (e.isControlDown())
            {
                // Fix for 4234053. Filter out the Control Key from the list.
                // ie., don't allow CTRL key deselection.
                e = new MouseEvent((Component)e.getSource(), e.getID(), e.getWhen(),
                e.getModifiers() ^ InputEvent.CTRL_MASK,
                e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
            }
            super.processMouseEvent(e);
        }
    }
    /** Creates the scroll pane which houses the scrollable list.
     * @return 滚动面板
     */
    protected JScrollPane createScroller()
    {
        return new JScrollPane(list,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /**
     * Configures the scrollable portion which holds the list within
     * the combo box popup. This method is called when the UI class
     * is created.
     */
    protected void configureScroller()
    {
        scroller.setFocusable(false);
        scroller.getVerticalScrollBar().setFocusable(false);
        scroller.setBorder(null);
    }

    /**
     * Configures the popup portion of the combo box. This method is called
     * when the UI class is created.
     */
    protected void configurePopup()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorderPainted(true);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setOpaque(false);
        add(scroller);
        setDoubleBuffered(true);
        setFocusable(false);
    }

    /** Calculates the upper left location of the Popup.
     * @return 弹出位置
     */
    protected  Point getPopupLocation()
    {
        Dimension popupSize = comboBox.getSize();
        Insets insets = getInsets();

        // reduce the width of the scrollpane by the insets so that the popup
        // is the same width as the combo box.
        popupSize.setSize(popupSize.width - (insets.right + insets.left),
        getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
        Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height,
        popupSize.width, popupSize.height);
        Dimension scrollSize = popupBounds.getSize();
        Point popupLocation = popupBounds.getLocation();

        scroller.setMaximumSize(scrollSize);
        scroller.setPreferredSize(scrollSize);
        scroller.setMinimumSize(scrollSize);

        list.revalidate();

        return popupLocation;
    }

    /**
     * Calculate the placement and size of the popup portion of the combo box based
     * on the combo box location and the enclosing screen bounds. If
     * no transformations are required, then the returned rectangle will
     * have the same values as the parameters.
     *
     * @param px starting x location
     * @param py starting y location
     * @param pw starting width
     * @param ph starting height
     * @return a rectangle which represents the placement and size of the popup
     */
    protected Rectangle computePopupBounds(int px,int py,int pw,int ph)
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Rectangle screenBounds;
        // Calculate the desktop dimensions relative to the combo box.
        GraphicsConfiguration gc = comboBox.getGraphicsConfiguration();
        Point p = new Point();
        SwingUtilities.convertPointFromScreen(p, comboBox);
        if (gc != null)
        {
            Insets screenInsets = toolkit.getScreenInsets(gc);
            screenBounds = gc.getBounds();
            screenBounds.width -= (screenInsets.left + screenInsets.right);
            screenBounds.height -= (screenInsets.top + screenInsets.bottom);
            screenBounds.x += (p.x + screenInsets.left);
            screenBounds.y += (p.y + screenInsets.top);
        }
        else
        {
            screenBounds = new Rectangle(p, toolkit.getScreenSize());
        }

        Rectangle rect = new Rectangle(px,py,pw,ph);
        if (!SwingUtilities.isRectangleContainingRectangle(screenBounds, rect)
        && ph < screenBounds.height)
        {
            rect.y = -rect.height;
        }
        // 这里是核心,上面的没太多东西
        //调整弹出体宽度;
        if (list.getPreferredSize().width > pw)
        {
            rect.width = list.getPreferredSize().width;
        }
        //调整弹出体X坐标
        //if (rect.width > pw)
        /*/{
        if (locationX == 0)
        {
            rect.x -= (rect.width - pw -18);
            locationX++;
        }
        else
        {
            rect.x -= (rect.width - pw);
        }
        //}
        //*/
        // 不知是何原因,是我以前没有发现还是怎么的,每一次弹出体第一次出现和以后
        //的出现时,位置似乎并不固定,所以先写死,大体暂时如此;
        //已解 原因在表格中和表格头中
        rect.x -= (rect.width - pw);
        return rect;
    }
    //private int locationX = 0;
}

