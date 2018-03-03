package org.cas.client.platform.casbeans.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicComboPopup;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.resource.international.MonthConstant;

class CalendarBasicPopup extends BasicComboPopup implements ActionListener, MouseListener {
    private static final long serialVersionUID = 0; // user008：仅仅用来去掉报警信息。

    /**
     * 创建一个 CalendarBasicPopup 的实例
     * 
     * @param combo
     *            传入引用
     */
    public CalendarBasicPopup(JComboBox combo) {
        super(combo);
        this.combo = combo;
    }

    /**
     * 得到日期面板
     * 
     * @return 日期面板
     */
    public DateSeleAreaPane getCalendarComboPane() {
        return calendarPane;
    }

    /**
     * 在弹出体上加一个日期面板
     */
    public void addPanel() {
        // 加一个日期面板
        calendarPane = new DateSeleAreaPane(null);
        calendarPane.setSize(((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSAWidth() + 5,
                ((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSAHeight() + 4);
    }

    /**
     * 重载,弹出我们的日期
     */
    protected void configurePopup() {
        setLayout(new BorderLayout()); // new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorderPainted(true);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setOpaque(false);
        add(scroller);
        setDoubleBuffered(true);
        setFocusable(false);
    }

    /**
     * 提供给日期选择combo的方法
     * 
     * @return 定时哭
     * @called by CalendarCombo
     */
    Timer getTimer() {
        return timer;
    }

    /**
     * Invoked when an action occurs. 本方法的目的是给日历面板加监听器
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        // * 上帝,只有统统不为空,才加得上去
        if (calendarPane.getMonthGroup() != null) {
            if (calendarPane.getMonthGroup().getContainer(0) != null) {
                // show 出来了,能加了
                if (calendarPane.getMonthGroup().getContainer(0).getDayCanvas() != null && isVisible()) {
                    calendarPane.getMonthGroup().getContainer(0).getDayCanvas().addMouseListener(this);
                    actionAddedFlag = true;
                    // 定时器不干活,可以歇会儿
                    timer.stop();
                }
            }
        }
        // */
    }

    /**
     * Creates the scroll pane which houses the scrollable list.
     * 
     * @return 滚动面板
     */
    protected JScrollPane createScroller() {
        addPanel();
        // 在赛扬C633上经这么多时间一般可给面板加上鼠标监听器
        timer = new Timer(200, this);
        return new JScrollPane(calendarPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /**
     * Calculates the upper left location of the Popup.
     * 
     * @return 弹出位置
     */
    protected Point getPopupLocation() {
        Dimension popupSize = comboBox.getSize();
        Insets insets = getInsets();

        // reduce the width of the scrollpane by the insets so that the popup
        // is the same width as the combo box.
        popupSize.setSize(popupSize.width - (insets.right + insets.left),
                getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
        Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height, popupSize.width, popupSize.height);
        Dimension scrollSize =
                new Dimension(((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSAWidth() + 10,
                        ((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSAHeight() + 200);
        Point popupLocation = popupBounds.getLocation();
        scroller.setMaximumSize(scrollSize);
        scroller.setPreferredSize(scrollSize);
        scroller.setMinimumSize(scrollSize);

        list.revalidate();

        return popupLocation;
    }

    /**
     * Calculate the placement and size of the popup portion of the combo box based on the combo box location and the
     * enclosing screen bounds. If no transformations are required, then the returned rectangle will have the same
     * values as the parameters.
     *
     * @param px
     *            starting x location
     * @param py
     *            starting y location
     * @param pw
     *            starting width
     * @param ph
     *            starting height
     * @return a rectangle which represents the placement and size of the popup
     */
    protected Rectangle computePopupBounds(
            int px,
            int py,
            int pw,
            int ph) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Rectangle screenBounds;
        // Calculate the desktop dimensions relative to the combo box.
        Point p = new Point();
        SwingUtilities.convertPointFromScreen(p, comboBox);
        GraphicsConfiguration gc = comboBox.getGraphicsConfiguration();
        if (gc != null) {
            Insets screenInsets = toolkit.getScreenInsets(gc);
            screenBounds = gc.getBounds();
            screenBounds.width -= (screenInsets.left + screenInsets.right);
            screenBounds.height -= (screenInsets.top + screenInsets.bottom);
            screenBounds.x += (p.x + screenInsets.left);
            screenBounds.y += (p.y + screenInsets.top);
        } else {
            screenBounds = new Rectangle(p, toolkit.getScreenSize());
        }

        Rectangle rect = new Rectangle(px, py, pw, ph);
        if (!SwingUtilities.isRectangleContainingRectangle(screenBounds, rect) && ph < screenBounds.height) {
            rect.y = -rect.height;
        }
        // 这里是核心,上面的没太多东西
        // 调整弹出体宽度;
        rect.width = calendarPane.getSize().width - 3;
        rect.height = calendarPane.getSize().height - 3;

        // todo:不知是何原因,是我以前没有发现还是怎么的,每一次弹出体第一次出现和以后
        // 的出现时,位置似乎并不固定,所以先写死,大体暂时如此;
        // 已解 原因在表格中和表格头中
        rect.x -= (rect.width - pw);
        // //Thread.dumpStack();
        // if(combo.getParent() instanceof PIMTable || combo.getParent() instanceof PIMTableHeader)
        // {
        // rect.x -= (rect.width - combo.getSize().width);
        // }
        // else
        // {
        // rect.x -= (rect.width - pw);
        // }
        return rect;
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
        actionAddedFlag = false;
        // 一点就关
        if (e.getClickCount() >= 1) {
            setVisible(false);
            // 得到选中
            int[] dayData = calendarPane.getLastDate();
            Date tmpDate = new Date(dayData[0] - 1900, dayData[1], dayData[2]);
            // 放入模型
            combo.getModel().setSelectedItem(tmpDate);
            // 在编辑器中设置一下
            StringBuffer sb =
                    new StringBuffer().append(tmpDate.getYear() + 1900).append('-').append(tmpDate.getMonth() + 1)
                            .append('-').append(tmpDate.getDate()).append(' ');
            String text = (sb.append('(').append(MonthConstant.WEEKDAYS[tmpDate.getDay()]).append(')').toString());
            ((JTextField) combo.getEditor()).setText(text);
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
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
        actionAddedFlag = false;
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
        // 果然不出所料,鼠标监听器加了没反应,下面的关键是找绘制日历的是那个组件.
        actionAddedFlag = false;
    }

    /**
     * 本组件
     */
    private JComboBox combo;
    /**
     * 日期选择区
     */
    private DateSeleAreaPane calendarPane;
    /**
     * 定时器,用于给弹出体加监听器
     */
    private Timer timer;
    /**
     * 表示是否第一次弹出
     */
    private boolean actionAddedFlag;

    // 存放年月日
    /**
     * 表示日期的数组
     */
    int[] daysAry = new int[3];
    // 第一次运行时这个COMBO要从字符串中得到年月日
    /**
     * 第一次运行
     */
    int firstFlag;

}
