package org.cas.client.platform.casbeans.calendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.EDate;
import org.cas.client.platform.casutil.EDaySet;
import org.cas.client.resource.international.DlgConst;

class ESingleCalendar extends JComponent implements DlgConst, SwingConstants {
    /* the layout manager of single calendar */
    class SingleCalendarLayout implements LayoutManager2 {
        /**
         * Adds the specified component with the specified name to the layout.
         * 
         * @param name
         *            the component name
         * @param comp
         *            the component to be added
         */
        public void addLayoutComponent(
                String s,
                Component comp) {
        }

        /**
         * Adds the specified component to the layout, using the specified constraint object.
         * 
         * @param comp
         *            the component to be added
         * @param obj
         *            where/how the component is added to the layout.
         */
        public void addLayoutComponent(
                Component comp,
                Object obj) {
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
         * Calculates the preferred size dimensions for the specified panel given the components in the specified parent
         * container.
         * 
         * @param parent
         *            the component to be laid out
         */
        public Dimension preferredLayoutSize(
                Container parent) {
            return getSize();
        }

        /**
         * Calculates the minimum size dimensions for the specified panel given the components in the specified parent
         * container.
         * 
         * @param parent
         *            the component to be laid out
         */
        public Dimension minimumLayoutSize(
                Container parent) {
            return new Dimension(0, 0);
        }

        /**
         * Calculates the maximum size dimensions for the specified panel given the components in the specified parent
         * container.
         * 
         * @param parent
         *            the component to be laid out
         */
        public Dimension maximumLayoutSize(
                Container parent) {
            return getSize();
        }

        /**
         * Returns the alignment along the x axis.
         * 
         * @param target
         *            alignment target
         * @return the alignment along the x axis
         */
        public float getLayoutAlignmentX(
                Container target) {
            return 0.0f;
        }

        /**
         * Returns the alignment along the y axis.
         * 
         * @param target
         *            alignment target
         * @return the alignment along the y axis
         */
        public float getLayoutAlignmentY(
                Container target) {
            return 0.0f;
        }

        /**
         * Invalidates the layout, indicating that if the layout manager has cached information it should be discarded.
         * 
         * @param target
         *            invalidates target
         */
        public void invalidateLayout(
                Container target) {
        }

        /**
         * Lays out the container in the specified panel.
         * 
         * @param parent
         *            the component which needs to be laid out
         */
        public void layoutContainer(
                Container parent) {
            layoutPane();
        }
    }

    /** Creates new ESingleCalendar */
    public ESingleCalendar() {
        container = this;
        changeSupport = new PropertyChangeSupport(this);

        setLayout(new SingleCalendarLayout());
        setRequestFocusEnabled(true);
        insets = getInsets();

        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int nowMonth = cal.get(Calendar.MONTH);
        int nowDate = cal.get(Calendar.DATE);
        todaySet = new EDaySet();
        todaySet.addDate(new EDate(nowYear, nowMonth, nowDate));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;

        initComponent();
        initWindowComponent();

        addFocusListener(new FocusAdapter() {
            /**
             * Transfer the focus to text field.
             * 
             * @param evt
             *            focus event
             */
            public void focusGained(
                    FocusEvent evt) {
                if (isFocusShow) {
                    showCalendarWindow();
                } else {
                    textField.requestFocus();
                }
            }
        });
    }

    private void initComponent() {
        textField = new JTextField();
        textField.setEditable(false);
        textField.setBackground(Color.white);
        textField.setOpaque(true);
        textField.setBorder(null);
        add(textField);

        arrowButton = new BasicArrowButton(SOUTH);
        add(arrowButton);
        arrowButton.setRequestFocusEnabled(false);

        arrowButton.addMouseListener(new MouseAdapter() {
            /**
             * Press mouse.
             * 
             * @param evt
             *            mouse event
             */
            public void mousePressed(
                    MouseEvent evt) {
                if (javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
                    if (arrowButton.isEnabled()) {
                        showCalendarWindow();
                    }
                }
            }
        });

        textField.addFocusListener(new FocusAdapter() {
            /**
             * The component losts focus.
             * 
             * @param evt
             *            focus event
             */
            public void focusLost(
                    FocusEvent evt) {
                if (!evt.isTemporary()) {
                    MenuSelectionManager.defaultManager().clearSelectedPath();
                }
            }
        });

        textField.addMouseListener(new MouseAdapter() {
            /**
             * Press mouse.
             * 
             * @param evt
             *            mouse event
             */
            public void mousePressed(
                    MouseEvent evt) {
                if (javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
                    if (textField.isEnabled()) {
                        showCalendarWindow();
                    }
                }
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            /**
             * Release keyboard.
             * 
             * @param evt
             *            key event
             */
            public void keyReleased(
                    KeyEvent evt) {
                if (!textField.isEnabled()) {
                    return;
                }
                int key = evt.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (calendarWindow.isVisible()) {
                        MenuSelectionManager.defaultManager().clearSelectedPath();
                    }
                } else if (key == KeyEvent.VK_SPACE) {
                    showCalendarWindow();
                }
            }
        });
    }

    private void initWindowComponent() {
        calendarWindow = new JPopupMenu();
        JPanel pane = new JPanel(null);
        Color backColor = new Color(255, 239, 206);
        pane.setBackground(backColor);
        pane.setBorder(BorderFactory.createLineBorder(new Color(58, 110, 165), 2));
        calendarWindow.add(pane);
        Insets paneInsets = pane.getInsets();

        group = new PIMMonthGroup();
        group.setSingleDay(true);
        group.setDragable(false);
        int groupWidth = group.getMonthSelectWidth();
        int groupHeight = group.getMonthSelectHeight();
        group.setBounds(paneInsets.left, paneInsets.top, groupWidth, groupHeight);
        pane.add(group);
        daySet = group.getDaySet();
        textField.setText(daySet.getIndexDate(0).getFullDateString());

        today = new JButton(TODAY_BUTTON);
        today.setOpaque(true);
        today.setBounds((groupWidth - CustOpts.BTN_WIDTH - paneInsets.left - paneInsets.right) / 2, groupHeight
                + paneInsets.top + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        pane.add(today);

        winWidth = groupWidth + paneInsets.left + paneInsets.right;
        winHeight = groupHeight + 2 * CustOpts.VER_GAP + CustOpts.BTN_HEIGHT + paneInsets.top + paneInsets.bottom;
        pane.setPreferredSize(new Dimension(winWidth, winHeight));

        group.addPropertyChangeListener(new PropertyChangeListener() {
            /**
             * A new date is selected.
             * 
             * @param evt
             *            property change event
             */
            public void propertyChange(
                    PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if (propertyName.equalsIgnoreCase(PIMMonthGroup.DATE_PROPERTY)) {
                    daySet = (EDaySet) evt.getNewValue();
                    MenuSelectionManager.defaultManager().clearSelectedPath();
                    setText(daySet.getIndexDate(0).getFullDateString());
                    changeSupport.firePropertyChange("Date", null, daySet);
                } else if (propertyName.equalsIgnoreCase(PIMMonthGroup.DUP_PROPERTY)) {
                    MenuSelectionManager.defaultManager().clearSelectedPath();
                }
            }
        });

        today.addActionListener(new ActionListener() {
            /**
             * Performe selecting today.
             * 
             * @param evt
             *            action event
             */
            public void actionPerformed(
                    ActionEvent evt) {
                group.setDaySet(todaySet);
            }
        });
    }

    private void layoutPane() {
        int width = getWidth();
        int height = getHeight();

        int buttonSize = height - (insets.top + insets.bottom);
        textField.setBounds(insets.left, insets.top, width - (insets.left + insets.right + buttonSize), buttonSize);
        arrowButton.setBounds(width - (insets.right + buttonSize), insets.top, buttonSize, buttonSize);
    }

    private void showCalendarWindow() {
        textField.requestFocus();
        if (calendarWindow.isVisible()) {
            MenuSelectionManager.defaultManager().clearSelectedPath();
        } else {
            adjustPopupPosition();
        }
    }

    private void adjustPopupPosition() {
        int x = 0;
        int y = 0;
        int width = getWidth();
        int height = getHeight();

        Point pt = getLocationOnScreen();
        if (pt.x + winWidth > screenWidth) {
            x = screenWidth - pt.x - winWidth;
        } else if (pt.x + winWidth < winWidth) {
            x = -pt.x;
        }

        if (pt.y + winHeight > screenHeight) {
            y -= winHeight;
        } else {
            y = height;
        }

        calendarWindow.show(this, x, y);
    }

    /**
     * Set a text to text field.
     * 
     * @param text
     *            text string
     */
    public void setText(
            String text) {
        textField.setText(text);
    }

    /**
     * Set whether you can select date with dragging mouse.
     * 
     * @param isDraggable
     *            true: you can select date with dragging mouse false: you can not select date with dragging mouse
     */
    public void setDragable(
            boolean isDragable) {
        group.setDragable(isDragable);
    }

    /**
     * Get selected day's set.
     * 
     * @return selected day's set
     */
    public EDaySet getCustomDate() {
        return daySet;
    }

    /**
     * Set day's set.
     * 
     * @param daySet
     *            day's set to be set
     */
    public void setCustomDate(
            EDaySet daySet) {
        group.setDaySet(daySet);
    }

    /* get the width of calendar */
    public int getCalendarWidth() {
        return group.getMonthSelectWidth();
    }

    /* get the height of calendar */
    public int getCalendarHeight() {
        return group.getMonthSelectHeight();
    }

    /**
     * set whether it shows calendar when getting focus.
     * 
     * @param isShowFocus
     *            true: show calendar when getting focus false: not show calendar when getting focus
     */
    public void setFocusShow(
            boolean isFocusShow) {
        this.isFocusShow = isFocusShow;
    }

    /**
     * Set the component valid or invalid.
     * 
     * @param isEnable
     *            true: valid false: invalid
     */
    public void setEnabled(
            boolean isEnable) {
        textField.setEnabled(isEnable);
        arrowButton.setEnabled(isEnable);
    }

    /**
     * Add property change listener.
     * 
     * @param listener
     *            property change listener to be added
     */
    public void addPropertyChangeListener(
            PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove property change listener.
     * 
     * @param listener
     *            property change listener to be removed
     */
    public void removePropertyChangeListener(
            PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    private ESingleCalendar container;

    private JTextField textField;
    private BasicArrowButton arrowButton;
    private Insets insets;

    private JPopupMenu calendarWindow;
    private PIMMonthGroup group;
    private JButton today;
    private EDaySet todaySet, daySet;

    private int screenWidth, screenHeight;
    private int winWidth, winHeight;
    private boolean isFocusShow = false;

    private PropertyChangeSupport changeSupport;
}
