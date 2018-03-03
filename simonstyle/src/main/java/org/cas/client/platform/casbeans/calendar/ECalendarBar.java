package org.cas.client.platform.casbeans.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JWindow;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.MonthConstant;

/**
 * 该类主要提供日历选择区面板中日历条面板及其相应的动作控制。
 */

class ECalendarBar extends JComponent implements MouseMotionListener, MouseListener, ActionListener {
    /**
     * 该类提供日期选择区面板中日历条的显示
     * 
     * @param container
     *            父容器
     */
    public ECalendarBar(EMonthContainer container) {
        parent = container;
        initData(); // 初始化日历条中的内容，不过其中好像有动态月份的面板，该面板应该在MousePress中去实例化。
        setLayout(new BorderLayout());

        initTitleBar();
        dirTimer = new javax.swing.Timer(1, this);
        listOffset = fontHeight / 6;
        listWidth = totalWidth - 2 * 3 * fontWidth;
        listHeight = 7 * fontHeight + listOffset;

        addMouseListener(this);

        addMouseMotionListener(this);
    }

    /**
     * 初始化标题条中的数据
     */
    private void initTitleBar() {
        int xPoint[] = new int[3];
        int yPoint[] = new int[3];
        int trigHeight = (int) ((double) fontWidth / 1.732);

        int barWidth = ((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSABarHeight();
        xPoint[0] = fontWidth;
        xPoint[1] = xPoint[0] + fontWidth;
        xPoint[2] = xPoint[1];
        yPoint[0] = barWidth / 2;
        yPoint[1] = yPoint[0] - trigHeight;
        yPoint[2] = yPoint[0] + trigHeight;
        backPolygon = new Polygon(xPoint, yPoint, 3);

        xPoint[0] = totalWidth - fontWidth;
        xPoint[1] = xPoint[0] - fontWidth;
        xPoint[2] = xPoint[1];
        forwardPolygon = new Polygon(xPoint, yPoint, 3);

        int dis = 3 * fontWidth;
        backRect = new Rectangle(0, 0, dis, barWidth);
        forwardRect = new Rectangle(totalWidth - dis, 0, dis, barWidth);

        int xStart = 0;
        int xEnd = totalWidth;
        if (startFlag == true) {
            xStart += dis;
            xEnd -= dis;
        }
        if (parent.isForward()) {
            xEnd -= dis;
        }
        titleRect = new Rectangle(xStart, 0, xEnd, barWidth);
    }

    /**
     * 初始化日历条中的数据
     */
    private void initData() {
        group = parent.getContainerManager();

        dayFont = CustOpts.custOps.getFontOfDSABar();
        fontWidth = getFontMetrics(dayFont).stringWidth("9");
        fontHeight = getFontMetrics(dayFont).getHeight();
        totalWidth = group.getMonthSelectWidth();

        startFlag = parent.isStart();
        endFlag = parent.isEnd();
        viewYear = parent.getYear();
        viewMonth = parent.getMonth();

        listWindow = new JWindow();
        monthList = new MonthList(this);
        listWindow.setContentPane(monthList); // 将月份的动态选择加入ListWindow
    }

    /**
     * 绘制日历面板中日历条的内容
     * 
     * @param g
     *            绘制图形的句柄
     */
    public void paintComponent(
            Graphics g) {
        g.setFont(dayFont);

        int w = getWidth();
        int h = getHeight();
        if (w < 0 || h < 0) {
            return;
        }
        int barHeight = ((CASMainFrame) CASControl.ctrl.getMainFrame()).getSplitPane().getDSABarHeight();
        ((Graphics2D) g).setPaint(new GradientPaint(w, h, TOOLBAR_COLOR0, w, 0, TOOLBAR_COLOR3));
        // g.setColor(barBackColor);
        g.fillRect(0, 0, totalWidth, barHeight);
        // g.fillRect(0, 0, w, h);

        g.setColor(leftLineColor);
        g.drawLine(0, 0, totalWidth - 1, 0);
        g.drawLine(0, 1, 0, barHeight);
        g.setColor(rightLineColor);
        g.drawLine(0, barHeight - 1, totalWidth, barHeight - 1);
        g.drawLine(totalWidth, 0, totalWidth, barHeight);

        g.setColor(barTextColor);
        int x = fontWidth;
        int y = fontHeight - 2;
        if (startFlag == true) {
            g.fillPolygon(backPolygon);
            g.drawPolygon(backPolygon);
        }

        String str = MonthConstant.MONTH_NAMES[viewMonth];
        str += ' ' + Integer.toString(viewYear);
        int len = dayFm.stringWidth(str);
        x = (totalWidth - len) / 2;
        g.drawString(str, x, y);

        if (parent.isForward()) {
            g.fillPolygon(forwardPolygon);
            g.drawPolygon(forwardPolygon);
        }
    }

    /**
     * 显示动态月份部分
     */
    private void showMonth(
            int x,
            int y) {
        if (titleRect.contains(x, y)) {
            Point pt = parent.getLocationOnScreen();
            x = pt.x + 3 * fontWidth;
            y = pt.y - 3 * fontHeight + listOffset;
            monthList.setMonth(viewYear, viewMonth);
            listWindow.setBounds(x, y, listWidth, listHeight);
            listWindow.setVisible(true);
            listWindow.toFront();
        } else {
            int dir = getDirection(x, y);
            if (dir != 0) {
                dirX = x;
                dirY = y;
                dirTimer.setDelay(500);
                dirTimer.start();
                timerStartFlag = true;
            }
        }
    }

    /**
     * 改变选择的月份的视图
     * 
     * @param dir
     *            选择的月份的索引
     */
    private void selectMonth(
            int dir) {
        parent.showNewCalendar(viewYear, viewMonth + dir);
    }

    /**
     * 获得位置的类型
     * 
     * @return -1:向后移动 1：向前移动 0：没有变化
     */
    private int getDirection(
            int x,
            int y) {
        if (backRect.contains(x, y) && startFlag == true) {
            return -1;
        } else if (forwardRect.contains(x, y) && parent.isForward()) {
            return 1;
        }
        return 0;
    }

    /**
     * 滚动月份的显示列表
     * 
     * @param x
     *            提供位置X坐标
     * @param y
     *            提供位置Y坐标
     */
    private void scrollMonthList(
            int x,
            int y) {
        Point pt = listWindow.getLocationOnScreen();
        if (y >= pt.y && y <= pt.y + listHeight) {
            monthList.autoScrollList(-1, true);
            oldTime = -1;
            int index = (y - pt.y) / fontHeight;
            Graphics g = monthList.getGraphics();
            if (x >= pt.x && x <= pt.x + listWidth) {
                monthList.selectItem(g, index);
            } else {
                monthList.restoreItem(g, index);
            }
        } else {
            int yDis = 0;
            boolean dirFlag = true;
            if (y < pt.y) {
                yDis = pt.y - y;
                dirFlag = true;
            } else if (y > pt.y + listHeight) {
                yDis = y - pt.y - listHeight;
                dirFlag = false;
            }

            int ms = 100;
            if (yDis < listHeight && yDis >= listHeight / 2) {
                ms *= 2;
            } else if (yDis < listHeight / 2 && yDis >= listHeight / 4) {
                ms *= 4;
            } else if (yDis < listHeight / 4) {
                ms *= 8;
            }
            if (oldTime != ms) {
                monthList.autoScrollList(ms, dirFlag);
                oldTime = ms;
            }
        }
    }

    /**
     * 重新设置新的年和月
     * 
     * @param year
     *            新的年份
     * @param month
     *            新的月份
     */
    public void setNewMonth(
            int year,
            int month) {
        viewYear = year;
        viewMonth = month;
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
     * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will
     * continue to be delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered
     * during a native Drag&Drop operation.
     * 
     * @param evt
     *            鼠标事件源
     */
    public void mouseDragged(
            MouseEvent evt) {
        if (!javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
            return;
        }
        int x = evt.getX();
        int y = evt.getY();
        if (!(listWindow.isShowing())) {
            int dir = getDirection(x, y);
            if (dir != 0 && timerStartFlag == false) {
                dirTimer.setDelay(500);
                dirTimer.start();
                timerStartFlag = true;
            } else if (timerStartFlag == true) {
                dirTimer.stop();
                timerStartFlag = false;
            }
        } else {
            Point barPoint = getLocationOnScreen();
            x += barPoint.x;
            y += barPoint.y;
            scrollMonthList(x, y);
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
        parent.setInContainer(true);
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
        parent.setInContainer(false);
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseMoved(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param evt
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent evt) {
        if (!javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
            return;
        }
        int x = evt.getX();
        int y = evt.getY();
        mousePressFlag = true;
        showMonth(x, y);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        mousePressFlag = false;
        if (timerStartFlag == true) {
            dirTimer.stop();
            timerStartFlag = false;
        }

        if (listWindow.isShowing()) {
            monthList.autoScrollList(-1, true);
            oldTime = -1;
            listWindow.setVisible(false);
            if (monthList.isSelectValid()) // 选择的月份合法
            {
                int year = monthList.getSelectedYear();
                int month = monthList.getSelectedMonth();
                if (year != viewYear || month != viewMonth) {
                    parent.showNewCalendar(year, month);
                }
            }
        }
    }

    /**
     * 实现动作的方法
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        if (e.getSource() == dirTimer) {
            dirTimer.setDelay(200);
            int dir = getDirection(dirX, dirY);
            if (dir != 0) {
                selectMonth(dir);
            }
        }

    }

    private static int getToolBarColor(
            int x,
            int n) {
        if (n == 0) {
            return x + (int) ((0.4 * (16 - (x + 1) / 16f)) / 0.6 * 4);
        } else if (n > 0 && n <= 12) {
            return x + (int) ((0.4 * (16 - (x + 1) / 16f)) / 0.6 * n);
        } else if (n > 12) {
            return x
                    + (int) (((0.4 * (16 - (x + 1) / 16f)) / 0.6 * 12) + ((0.4 * (16 - (x + 1) / 16f)) / 0.6 * (n - 12)));
        } else {
            return 0;
        }
    }

    // 工具条、弹出菜单渐进色
    public static Color TOOLBAR_COLOR0 = new Color(getToolBarColor(SystemColor.control.getRed(), 0), getToolBarColor(
            SystemColor.control.getGreen(), 0), getToolBarColor(SystemColor.control.getBlue(), 0));
    public static Color TOOLBAR_COLOR3 = new Color(getToolBarColor(SystemColor.control.getRed(), 23), getToolBarColor(
            SystemColor.control.getGreen(), 23), getToolBarColor(SystemColor.control.getBlue(), 23));
    public static Color TOOLBAR_COLOR4;

    private EMonthContainer parent;
    private PIMMonthGroup group;

    private Color leftLineColor = Color.white;
    private Color rightLineColor = Color.black;
    private Color barTextColor = Color.black;
    private Rectangle backRect, forwardRect, titleRect;
    private Polygon backPolygon, forwardPolygon;
    private int oldTime = -1;

    int fontWidth, fontHeight, totalWidth; // @NOTE:提高可见性，以追求更好的性能。
    private boolean startFlag, endFlag;
    int viewYear, viewMonth; // @NOTE:提高可见性，以追求更好的性能。

    private JWindow listWindow;
    private MonthList monthList;
    int listOffset; // @NOTE:提高可见性，以追求更好的性能。
    int listWidth, listHeight; // @NOTE:提高可见性，以追求更好的性能。
    Font dayFont; // @NOTE:提高可见性，以追求更好的性能。

    private int dirX, dirY;
    private boolean mousePressFlag;
    private boolean timerStartFlag;
    private javax.swing.Timer dirTimer;

    FontMetrics dayFm; // @NOTE:提高可见性，以追求更好的性能。

    // =============================================================================
    /**
     * 该类提供在日期选择区的日历条上按下鼠标的动态面板
     */
    public static class MonthList extends JComponent {
        ECalendarBar tmpCalBar;

        /**
         * 构造器
         * 
         * @param prmCalendarBar
         *            本类引用
         */
        public MonthList(ECalendarBar prmCalendarBar) {
            tmpCalBar = prmCalendarBar;
            tmpCalBar.dayFm = getFontMetrics(CustOpts.custOps.getFontOfDSABar());
            // 实例化内部类
            ListTimerAdapter tmpListTimerAda = new ListTimerAdapter(ECalendarBar.MonthList.this);
            listTimer = new javax.swing.Timer(1, tmpListTimerAda);

            setBorder(BorderFactory.createLineBorder(Color.black, 1));
            insets = getInsets();
        }

        /**
         * 绘制面板中的内容
         * 
         * @param g
         *            绘制的图形句柄
         */
        public void paintComponent(
                Graphics g) {
            g.setFont(tmpCalBar.dayFont);
            g.setColor(backColor);
            g.fillRect(insets.left, insets.top, tmpCalBar.listWidth - insets.left - insets.right, tmpCalBar.listHeight
                    - insets.top - insets.bottom);

            g.setColor(textColor);
            int x = 0;
            int y = tmpCalBar.fontHeight - tmpCalBar.listOffset;
            int start = -(maxCount / 2);
            startMonth = selMonth + start;
            startYear = selYear;
            if (startMonth < 0) {
                startMonth += 12;
                --startYear;
            }

            int month, year;
            String str;
            for (int i = 0; i < maxCount; ++i) {
                month = startMonth + i;
                year = startYear;
                if (month > 11) {
                    month -= 12;
                    year = startYear + 1;
                }
                if (year == tmpCalBar.viewYear && month == tmpCalBar.viewMonth) // 和显示相同年份，相同月份
                {
                    selectItem(g, i);
                    g.setColor(textColor);
                } else {
                    str = MonthConstant.MONTH_NAMES[month];
                    str += CASUtility.SPACE + Integer.toString(year);
                    x = (tmpCalBar.listWidth - tmpCalBar.dayFm.stringWidth(str)) / 2;
                    g.drawString(str, x, y);
                }
                y += tmpCalBar.fontHeight;
            }
        }

        /**
         * 设置面板中显示的月份
         */
        protected void setMonth(
                int year,
                int month) {
            selMonth = month;
            selYear = year;
            if (selMonth < 0) {
                selMonth = 11;
                --selYear;
            } else if (selMonth > 11) {
                selMonth = 0;
                ++selYear;
            }
            repaint();
        }

        /**
         * 动态月份的选择项
         * 
         * @param g
         *            绘制的图形句柄
         * @param index
         *            选中的当前索引。
         */
        protected void selectItem(
                Graphics g,
                int index) {
            if (oldIndex != -1 && oldIndex != index) {
                restoreItem(g, oldIndex);
            }
            oldIndex = index;
            setItemStatus(g, index, selectColor, selTextColor);
        }

        /**
         * 该方法设置新的选择区
         * 
         * @param g
         *            绘制的图形句柄
         * @param index
         *            选中的当前索引。
         */
        protected void restoreItem(
                Graphics g,
                int index) {
            if (oldIndex != -1) {
                setItemStatus(g, index, backColor, textColor);
            }
            oldIndex = -1;
        }

        /**
         * 该方法绘制选定项目的字串
         * 
         * @param g
         *            绘制的图形句柄
         * @param index新的选择项
         * @param bkColor
         *            绘制的背景颜色
         * @param strColor绘制字串的颜色
         */
        private void setItemStatus(
                Graphics g,
                int index,
                Color bkColor,
                Color strColor) {
            int y = index * tmpCalBar.fontHeight;
            int offset = tmpCalBar.listOffset;
            g.setFont(tmpCalBar.dayFont);
            g.setColor(bkColor);
            g.fillRect(insets.left, y + offset + insets.right, tmpCalBar.listWidth - insets.left - insets.right,
                    tmpCalBar.fontHeight - insets.top - insets.bottom);
            y += tmpCalBar.fontHeight - offset;

            g.setColor(strColor);
            currentMonth = startMonth + index;
            currentYear = startYear;
            if (currentMonth > 11) {
                currentMonth -= 12;
                ++currentYear;
            }
            String str = MonthConstant.MONTH_NAMES[currentMonth];
            str += ' ' + Integer.toString(currentYear);
            int x = (tmpCalBar.listWidth - tmpCalBar.dayFm.stringWidth(str)) / 2;
            g.drawString(str, x, y);
        }

        /**
         * 该方法自动滚动显示的列表
         * 
         * @param ms
         *            延时的时间
         * @param dirFlag
         *            移动的标志
         */
        protected void autoScrollList(
                int ms,
                boolean dirFlag) {
            listTimer.stop();
            if (ms == -1) {
                return;
            }
            this.dirFlag = dirFlag;
            listTimer.setDelay(ms);
            listTimer.start();
        }

        /**
         * 获得选择的年份
         * 
         * @return 当前年份
         */
        protected int getSelectedYear() {
            return currentYear;
        }

        /**
         * 获得选择的月份
         * 
         * @return 当前月份
         */
        protected int getSelectedMonth() {
            return currentMonth;
        }

        /**
         * 是不是有效的选择
         * 
         * @return true: 旧的选择项不是-1 false:旧的选择项是-1
         *
         */
        protected boolean isSelectValid() {
            return (oldIndex == -1) ? false : true;
        }

        /**
         * 调用释放方法
         */
        public void free() {
            tmpCalBar = null;
        }

        private Color backColor = Color.white;
        private Color textColor = Color.black;
        private Color selectColor = Color.black;
        private Color selTextColor = Color.white;

        int selYear, selMonth; // @NOTE:提高可见性，以追求更好的性能。
        private int startYear, startMonth;
        private int currentYear, currentMonth;
        private int oldIndex = -1;
        private Insets insets;

        private javax.swing.Timer listTimer;
        boolean dirFlag; // true--Up, false--Down //@NOTE:提高可见性，以追求更好的性能。

        private int maxCount = 7; // 显示日期的最大数目

        // =======================================================================
        /**
         * 该类提供向上移动动态年分的动作
         */
        public static class ListTimerAdapter extends KeyAdapter implements ActionListener {
            MonthList tmpMonthList;

            /**
             * 构建器,传入引用
             * 
             * @param prmMonthList
             *            传入引用
             */
            public ListTimerAdapter(MonthList prmMonthList) {
                tmpMonthList = prmMonthList;
            }

            /**
             * 键盘事件的动作
             * 
             * @param evt
             *            相关的事件
             */
            public void actionPerformed(
                    ActionEvent evt) {
                if (tmpMonthList.dirFlag) // 相当于向上的动作
                {
                    tmpMonthList.setMonth(tmpMonthList.selYear, tmpMonthList.selMonth - 1);
                } else {
                    tmpMonthList.setMonth(tmpMonthList.selYear, tmpMonthList.selMonth + 1);
                }
            }

            /**
             * 释放对象
             * 
             * @see 释放对象
             */
            public void free() {
                tmpMonthList = null;
            }
        }
    }
}
