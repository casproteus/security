package org.cas.client.platform.pimview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cas.client.platform.cascontrol.thread.ThreadActionsFacade;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.PaneConsts;

public class CommonTitle extends JPanel implements LayoutManager2, MouseListener {
    /**
     * 默认标题高度
     */
    private final int DEFAULT_HEIGHT = 20;
    private final int IMAPDEFAULT_HEIGHT = 80;
    private final int PANE_HEIGHT = 40;

    /**
     * 构造器
     */
    public CommonTitle(int type) {
        if (type == 1)// imap时type为一。
        {
            title1 = new JLabel(CASUtility.EMPTYSTR);
            title2 = new JLabel(CASUtility.EMPTYSTR);
            title1.setOpaque(true);
            title2.setOpaque(true);
            button1 = new JButton(PaneConsts.SYNCHACCOUNT);
            Icon arrowheadIcon = PIMPool.pool.getIcon(PaneConsts.IAMGE_PATH.concat("arrowhead.gif"));

            upPanel = new JPanel();
            downPanel = new JPanel();

            setBounds(0, 0, getWidth(), IMAPDEFAULT_HEIGHT);
            setPreferredSize(new Dimension(getWidth(), IMAPDEFAULT_HEIGHT));
            setLayout(this);

            upPanel.setBackground(Color.DARK_GRAY);
            title1.setForeground(Color.WHITE);
            title1.setBackground(Color.DARK_GRAY);
            title2.setForeground(Color.WHITE);
            title2.setBackground(Color.DARK_GRAY);

            upPanel.add(title1);
            upPanel.add(title2);

            downPanel.add(button1);

            add(upPanel);
            add(downPanel);

            button1.addMouseListener(this);
        } else if (type == 2)// Local情况下type为2
        {
            title1 = new JLabel(CASUtility.EMPTYSTR);
            title2 = new JLabel(CASUtility.EMPTYSTR);
            title1.setOpaque(true);
            title2.setOpaque(true);
            button4 = new JButton(PaneConsts.SENDANDRECEIVE);
            button4.addMouseListener(this);

            upPanel = new JPanel();
            downPanel = new JPanel();

            setBounds(0, 0, getWidth(), IMAPDEFAULT_HEIGHT);
            setPreferredSize(new Dimension(getWidth(), IMAPDEFAULT_HEIGHT));
            setLayout(this);

            upPanel.setBackground(Color.DARK_GRAY);
            title1.setForeground(Color.WHITE);
            title1.setBackground(Color.DARK_GRAY);
            title2.setForeground(Color.WHITE);
            title2.setBackground(Color.DARK_GRAY);

            upPanel.add(title1);
            upPanel.add(title2);

            downPanel.add(button4);
            add(upPanel);
            add(downPanel);

            button4.addMouseListener(this);
        } else if (type == 0)// 其它情况下type为0。
        {
            label = new JLabel();
            title = new JLabel();

            label.setOpaque(true);
            title.setOpaque(true);

            setPreferredSize(new Dimension(getWidth(), DEFAULT_HEIGHT));
            setLayout(this);

            add(label);
            add(title);
        }
        this.type = type;
        addMouseListener(this);
    }

    /**
     * 设置面板的标题和图标
     * 
     * @param paneIcon
     *            图标
     * @param paneTitle
     *            标题
     */
    public void setPaneTitle(
            Icon paneIcon,
            String paneTitle) {
        label.setIcon(paneIcon);
        title.setText(paneTitle);
    }

    /**
     * 设置面板的标题
     * 
     * @param paneTitle1
     *            第一行标题
     * @param paneTitle2
     *            第二行标题
     */
    public void setPaneTitle(
            String paneTitle1,
            String paneTitle2) {
        title1.setText(paneTitle1);
        title2.setText(paneTitle2);
    }

    /**
     * Adds the specified component to the layout, using the specified constraint object.
     * 
     * @param comp
     *            the component to be added
     * @param constraints
     *            where/how the component is added to the layout.
     */
    public void addLayoutComponent(
            Component comp,
            Object constraints) {
    }

    /**
     * If the layout manager uses a per-component string, adds the component <code>comp</code> to the layout,
     * associating it with the string specified by <code>name</code>.
     *
     * @param name
     *            the string to be associated with the component
     * @param comp
     *            the component to be added
     */
    public void addLayoutComponent(
            String name,
            Component comp) {
    }

    /**
     * Returns the alignment along the x axis. This specifies how the component would like to be aligned relative to
     * other components. The value should be a number between 0 and 1 where 0 represents alignment along the origin, 1
     * is aligned the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(
            Container target) {
        return 0.0f;
    }

    /**
     * Returns the alignment along the y axis. This specifies how the component would like to be aligned relative to
     * other components. The value should be a number between 0 and 1 where 0 represents alignment along the origin, 1
     * is aligned the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(
            Container target) {
        return 0.0f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager has cached information it should be discarded.
     */
    public void invalidateLayout(
            Container target) {
    }

    /**
     * Lays out the specified container.
     * 
     * @param parent
     *            the container to be laid out
     */
    public void layoutContainer(
            Container parent) {
        Rectangle b = parent.getBounds();
        int w = b.width;
        int h = b.height;

        if (type == 1) {
            upPanel.setBounds(0, 0, getWidth(), PANE_HEIGHT);
            title1.setBounds(CustOpts.VER_GAP, 0, getWidth(), DEFAULT_HEIGHT);
            title2.setBounds(CustOpts.VER_GAP, DEFAULT_HEIGHT, getWidth(), PANE_HEIGHT - DEFAULT_HEIGHT);

            int tmpHeight = IMAPDEFAULT_HEIGHT - PANE_HEIGHT;
            downPanel.setBounds(0, PANE_HEIGHT, getWidth(), tmpHeight);
            int y = tmpHeight / 2 - (CustOpts.BTN_HEIGHT + 4) / 2;
            int width = (int) button1.getPreferredSize().getWidth() + 20;
            int height = CustOpts.BTN_HEIGHT + 4;
            button1.setBounds(CustOpts.VER_GAP + 2, y, width, height);
        } else if (type == 0) {
            int iconW = (int) label.getPreferredSize().getWidth();
            label.setBounds(0, 0, iconW, DEFAULT_HEIGHT);
            title.setBounds(iconW + 2, 0, w - iconW - 2, DEFAULT_HEIGHT);
        } else if (type == 2) {
            upPanel.setBounds(0, 0, getWidth(), PANE_HEIGHT);
            title1.setBounds(CustOpts.VER_GAP, 0, getWidth(), DEFAULT_HEIGHT);
            title2.setBounds(CustOpts.VER_GAP, DEFAULT_HEIGHT, getWidth(), PANE_HEIGHT - DEFAULT_HEIGHT);

            int tmpHeight = IMAPDEFAULT_HEIGHT - PANE_HEIGHT;
            downPanel.setBounds(0, PANE_HEIGHT, getWidth(), tmpHeight);
            int y = tmpHeight / 2 - (CustOpts.BTN_HEIGHT + 4) / 2;
            int width = (int) button4.getPreferredSize().getWidth() + 60;
            int height = CustOpts.BTN_HEIGHT + 4;
            button4.setBounds(CustOpts.VER_GAP + 2, y, width, height);
        }
    }

    /**
     * Calculates the maximum size dimensions for the specified container, given the components it contains.
     * 
     * @see java.awt.Component#getMaximumSize
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(
            Container target) {
        return target.getSize();
    }

    /**
     * Calculates the minimum size dimensions for the specified container, given the components it contains.
     * 
     * @param parent
     *            the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(
            Container parent) {
        if (type == 1 || type == 2) {
            return new Dimension(parent.getWidth(), IMAPDEFAULT_HEIGHT);
        } else {
            return new Dimension(parent.getWidth(), DEFAULT_HEIGHT);
        }
    }

    /**
     * Calculates the preferred size dimensions for the specified container, given the components it contains.
     * 
     * @param parent
     *            the container to be laid out
     *
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(
            Container parent) {
        return null;
        // return parent.getSize();
    }

    /**
     * Removes the specified component from the layout.
     * 
     * @param comp
     *            the component to be removed
     */
    public void removeLayoutComponent(
            Component comp) {
    }

    /**
     * 是否是IMAP协议帐户所对应的界面
     * 
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     */
    public void mouseClicked(
            MouseEvent e) {
        Object object = e.getSource();
        if (object == button1) {

        } else if (object == button4) {
        }
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(
            MouseEvent e) {
    }

    private JLabel label;
    private JLabel title;
    private JLabel title1;
    private JLabel title2;
    private JPanel upPanel;
    private JPanel downPanel;
    private JButton button1;
    private JButton button4;
    private int type;
}
