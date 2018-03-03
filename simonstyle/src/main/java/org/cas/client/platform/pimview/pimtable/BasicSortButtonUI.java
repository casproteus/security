package org.cas.client.platform.pimview.pimtable;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import org.cas.client.platform.casutil.CASUtility;

class BasicSortButtonUI extends ButtonUI {
    // Shared UI object
    /**
     * 本按钮绘制器的UI
     */
    private final static BasicSortButtonUI buttonUI = new BasicSortButtonUI();

    // Visual constants
    /**
     * 缺省文本和图标的间距
     */
    protected int defaultTextIconGap;

    // Offset controlled by set method
    /**
     * 偏移控制相关
     */
    private int shiftOffset;
    /**
     * 缺省文本偏移,和控制相关
     */
    protected int defaultTextShiftOffset;

    // Has the shared instance defaults been initialized?
    /**
     * 和初始化有关的变量,具体不清楚
     */
    private boolean defaults_initialized;

    /**
     * 和系统L & F有关的一个前缀
     */
    private final static String propertyPrefix = "Button" + ".";

    // ********************************
    // Create PLAF
    // ********************************
    /**
     * UI标准方法
     * 
     * @return UI
     * @param c
     *            JButton
     */
    public static ComponentUI createUI(
            JComponent c) {
        return buttonUI;
    }

    /**
     * 得到属性前缀,抄JDK的
     * 
     * @return 属性前缀
     */
    protected String getPropertyPrefix() {
        return propertyPrefix;
    }

    // ********************************
    // Install PLAF
    // ********************************
    /**
     * UI标准方法
     * 
     * @param c
     *            JButton
     */
    public void installUI(
            JComponent c) {
        installDefaults((AbstractButton) c);
        installListeners((AbstractButton) c);
        installKeyboardActions((AbstractButton) c);
        BasicHTML.updateRenderer(c, ((AbstractButton) c).getText());
    }

    /**
     * UI标准方法,抄JDK的
     * 
     * @param b
     *            抽象按钮
     */
    protected void installDefaults(
            AbstractButton b) {
        // load shared instance defaults
        String pp = getPropertyPrefix();
        if (!defaults_initialized) {
            defaultTextIconGap = ((Integer) UIManager.get(pp.concat("textIconGap"))).intValue();
            defaultTextShiftOffset = ((Integer) UIManager.get(pp.concat("textShiftOffset"))).intValue();

            // next four lines part of optimized component defaults installation
            /*
             * defaultForeground = UIManager.getColor(pp + "foreground"); defaultBackground = UIManager.getColor(pp +
             * "background"); defaultFont = UIManager.getFont(pp + "font"); defaultBorder = UIManager.getBorder(pp +
             * "border");
             */

            defaults_initialized = true;
        }

        // set the following defaults on the button
        if (b.isContentAreaFilled()) {
            b.setOpaque(true);
        } else {
            b.setOpaque(false);
        }

        if (b.getMargin() == null || (b.getMargin() instanceof UIResource)) {
            b.setMargin(UIManager.getInsets(pp.concat("margin")));
        }

        // *** begin optimized defaults install ***

        /*
         * Color currentForeground = b.getForeground(); Color currentBackground = b.getBackground(); Font currentFont =
         * b.getFont(); Border currentBorder = b.getBorder(); if (currentForeground == null || currentForeground
         * instanceof UIResource) { b.setForeground(defaultForeground); } if (currentBackground == null ||
         * currentBackground instanceof UIResource) { b.setBackground(defaultBackground); } if (currentFont == null ||
         * currentFont instanceof UIResource) { b.setFont(defaultFont); } if (currentBorder == null || currentBorder
         * instanceof UIResource) { b.setBorder(defaultBorder); }
         */

        // *** end optimized defaults install ***

        // old code below works for component defaults installation, but it is slow
        LookAndFeel.installColorsAndFont(b, pp.concat("background"), pp.concat("foreground"), pp.concat("font"));
        LookAndFeel.installBorder(b, pp.concat("border"));

    }

    /**
     * UI标准方法,抄JDK的
     * 
     * @param b
     *            抽象按钮
     */
    protected void installListeners(
            AbstractButton b) {
        BasicButtonListener listener = createButtonListener(b);
        if (listener != null) {
            // put the listener in the button's client properties so that
            // we can get at it later
            b.putClientProperty(this, listener);

            b.addMouseListener(listener);
            b.addMouseMotionListener(listener);
            b.addFocusListener(listener);
            b.addPropertyChangeListener(listener);
            b.addChangeListener(listener);
        }
    }

    /**
     * UI标准方法,抄JDK的
     * 
     * @param b
     *            抽象按钮
     */
    protected void installKeyboardActions(
            AbstractButton b) {
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        if (listener != null) {
            listener.installKeyboardActions(b);
        }
    }

    // ********************************
    // Uninstall PLAF
    // ********************************
    /**
     * UI标准方法
     * 
     * @param c
     *            JButton
     */
    public void uninstallUI(
            JComponent c) {
        uninstallKeyboardActions((AbstractButton) c);
        uninstallListeners((AbstractButton) c);
        uninstallDefaults((AbstractButton) c);
        BasicHTML.updateRenderer(c, CASUtility.EMPTYSTR);
    }

    /**
     * UI标准方法,抄JDK的
     * 
     * @param b
     *            抽象按钮
     */
    protected void uninstallKeyboardActions(
            AbstractButton b) {
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        if (listener != null) {
            listener.uninstallKeyboardActions(b);
        }
    }

    /**
     * UI标准方法,抄JDK的
     * 
     * @param b
     *            抽象按钮
     */
    protected void uninstallListeners(
            AbstractButton b) {
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        b.putClientProperty(this, null);
        if (listener != null) {
            b.removeMouseListener(listener);
            b.removeMouseListener(listener);
            b.removeMouseMotionListener(listener);
            b.removeFocusListener(listener);
            b.removeChangeListener(listener);
            b.removePropertyChangeListener(listener);
        }
    }

    /**
     * UI标准方法,抄JDK的
     * 
     * @param b
     *            抽象按钮
     */
    protected void uninstallDefaults(
            AbstractButton b) {
        LookAndFeel.uninstallBorder(b);
        defaults_initialized = false;
    }

    // ********************************
    // Create Listeners
    // ********************************
    /**
     * UI标准方法,抄JDK的
     * 
     * @param b
     *            抽象按钮
     * @return 按钮的监听器
     */
    protected BasicButtonListener createButtonListener(
            AbstractButton b) {
        return new BasicButtonListener(b);
    }

    /**
     * 得到缺省图标文本间隔
     * 
     * @return 缺省图标文本间隔
     * @param b
     *            按钮
     */
    int getDefaultTextIconGap(
            AbstractButton b) {
        return defaultTextIconGap;
    }

    /*
     * These rectangles/insets are allocated once for all ButtonUI.paint() calls. Re-using rectangles rather than
     * allocating them in each paint call substantially reduced the time it took paint to run. Obviously, this method
     * can't be re-entered.
     */
    /**
     * 可视矩形
     */
    private static Rectangle viewRect = new Rectangle();
    /**
     * 文本矩形
     */
    private static Rectangle textRect = new Rectangle();
    /**
     * 图标矩形
     */
    private static Rectangle iconRect = new Rectangle();

    // ********************************
    // Paint Methods
    // ********************************

    /**
     * 重载绘制方法
     * 
     * @param g
     *            图形设备
     * @param c
     *            要绘制上去的组件
     */
    public void paint(
            Graphics g,
            JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        FontMetrics fm = c.getFontMetrics(g.getFont());

        Insets i = c.getInsets();

        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = b.getWidth() - (i.right + viewRect.x);
        viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

        textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

        Font f = c.getFont();
        g.setFont(f);

        // layout the text and icon
        String text =
                SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), b.getIcon(), b.getVerticalAlignment(),
                        b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                        viewRect, iconRect, textRect, b.getText() == null ? 0 : b.getIconTextGap());

        boolean showIcon = true;
        if (text.endsWith(".")) {
            viewRect.x = i.left;
            viewRect.y = i.top;
            viewRect.width = b.getWidth() - (i.right + viewRect.x);
            viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

            textRect.x = textRect.y = textRect.width = textRect.height = 0;
            iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

            text =
                    SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), null, b.getVerticalAlignment(),
                            b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                            viewRect, iconRect, textRect, b.getText() == null ? 0 : b.getIconTextGap());
            showIcon = false;
        }
        clearTextShiftOffset();

        // perform UI specific press action, e.g. Windows L&F shifts text
        if (model.isArmed() && model.isPressed()) {
            paintButtonPressed(g, b);
        }

        // Paint the Icon
        if (b.getIcon() != null && showIcon) {
            paintIcon(g, c, iconRect);
        }

        if (text != null && text.length() != 0) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                v.paint(g, textRect);
            } else {
                paintText(g, b, textRect, text);
            }
        }

        if (b.isFocusPainted() && b.hasFocus()) {
            // paint UI specific focus
            paintFocus(g, b, viewRect, textRect, iconRect);
        }
    }

    /**
     * UI标准方法,抄JDK的
     * 
     * @param g
     *            图形设备
     * @param c
     *            按钮
     * @param iconRect
     *            图标矩形
     */
    protected void paintIcon(
            Graphics g,
            JComponent c,
            Rectangle iconRect) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        Icon icon = b.getIcon();
        Icon tmpIcon = null;

        if (icon == null) {
            return;
        }

        if (!model.isEnabled()) {
            if (model.isSelected()) {
                tmpIcon = (Icon) b.getDisabledSelectedIcon();
            } else {
                tmpIcon = (Icon) b.getDisabledIcon();
            }
        } else if (model.isPressed() && model.isArmed()) {
            tmpIcon = (Icon) b.getPressedIcon();
            if (tmpIcon != null) {
                // revert back to 0 offset
                clearTextShiftOffset();
            }
        } else if (b.isRolloverEnabled() && model.isRollover()) {
            if (model.isSelected()) {
                tmpIcon = (Icon) b.getRolloverSelectedIcon();
            } else {
                tmpIcon = (Icon) b.getRolloverIcon();
            }
        } else if (model.isSelected()) {
            tmpIcon = (Icon) b.getSelectedIcon();
        }

        if (tmpIcon != null) {
            icon = tmpIcon;
        }

        if (model.isPressed() && model.isArmed()) {
            icon.paintIcon(c, g, iconRect.x + getTextShiftOffset(), iconRect.y + getTextShiftOffset());
        } else {
            icon.paintIcon(c, g, iconRect.x, iconRect.y);
        }

    }

    /**
     * As of Java 2 platform v 1.4 this method should not be used or overriden. Use the paintText method which takes the
     * AbstractButton argument.
     * 
     * @param g
     *            图形设备
     * @param c
     *            按钮
     * @param textRect
     *            文本矩形
     * @param text
     *            要绘制上去的真实文本
     */
    protected void paintText(
            Graphics g,
            JComponent c,
            Rectangle textRect,
            String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        FontMetrics fm = c.getFontMetrics(g.getFont());
        int mnemonicIndex = b.getDisplayedMnemonicIndex();

        /* Draw the Text */
        if (model.isEnabled()) {
            /*** paint the text normally */
            g.setColor(b.getForeground());
            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x + getTextShiftOffset(),
                    textRect.y + fm.getAscent() + getTextShiftOffset());
        } else {
            /*** paint the text disabled ***/
            g.setColor(b.getBackground().brighter());
            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x,
                    textRect.y + fm.getAscent());
            g.setColor(b.getBackground().darker());
            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x - 1,
                    textRect.y + fm.getAscent() - 1);
        }
    }

    /**
     * Method which renders the text of the current button.
     * <p>
     * 
     * @param g
     *            Graphics context
     * @param b
     *            Current button to render
     * @param textRect
     *            Bounding rectangle to render the text.
     * @param text
     *            String to render
     * @since 1.4
     */
    protected void paintText(
            Graphics g,
            AbstractButton b,
            Rectangle textRect,
            String text) {
        paintText(g, (JComponent) b, textRect, text);
    }

    // Method signature defined here overriden in subclasses.
    // Perhaps this class should be abstract?
    /**
     * 抄JDK的
     * 
     * @param g
     *            图形设备
     * @param b
     *            抽象按钮
     * @param viewRect
     *            可视矩形
     * @param textRect
     *            文本矩形
     * @param iconRect
     *            图标矩形
     */
    protected void paintFocus(
            Graphics g,
            AbstractButton b,
            Rectangle viewRect,
            Rectangle textRect,
            Rectangle iconRect) {
    }

    // Method signature defined here overriden in subclasses.
    // Perhaps this class should be abstract?
    /**
     * 抄JDK的
     * 
     * @param g
     *            图形设备
     * @param b
     *            抽象按钮
     */
    protected void paintButtonPressed(
            Graphics g,
            AbstractButton b) {
    }

    /**
     * 清除文本转换偏移,具体不清楚
     */
    protected void clearTextShiftOffset() {
        this.shiftOffset = 0;
    }

    /**
     * 设置文本转换偏移,具体不清楚
     */
    protected void setTextShiftOffset() {
        this.shiftOffset = defaultTextShiftOffset;
    }

    /**
     * 得到文本转换偏移,具体不清楚
     * 
     * @return 文本转换偏移值
     */
    protected int getTextShiftOffset() {
        return shiftOffset;
    }

    // ********************************
    // Layout Methods
    // ********************************
    /**
     * 重载父类中的方法,得到组件的最小尺寸 Return the minimum size of the table. The minimum height is the row height times the number of
     * rows. The minimum width is the sum of the minimum widths of each column.
     * 
     * @return 返回组件的最小尺寸
     * @param c
     *            传入的组件对象
     */
    public Dimension getMinimumSize(
            JComponent c) {
        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
        }
        return d;
    }

    /**
     * 重载父类中的方法,得到组件的首选尺寸 Return the preferred size of the table. The preferred height is the row height times the
     * number of rows. The preferred width is the sum of the preferred widths of each column.
     * 
     * @param c
     *            传入的组件对象
     * @return 返回组件的首选尺寸
     */
    public Dimension getPreferredSize(
            JComponent c) {
        AbstractButton b = (AbstractButton) c;
        return BasicGraphicsUtils.getPreferredButtonSize(b, b.getIconTextGap());
    }

    /**
     * 重载父类中的方法,得到组件的最大尺寸 Return the maximum size of the table. The maximum height is the row heighttimes the number of
     * rows. The maximum width is the sum of the maximum widths of each column.
     * 
     * @param c
     *            传入的组件对象
     * @return 返回组件的最大尺寸
     */
    public Dimension getMaximumSize(
            JComponent c) {
        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
        }
        return d;
    }

}
