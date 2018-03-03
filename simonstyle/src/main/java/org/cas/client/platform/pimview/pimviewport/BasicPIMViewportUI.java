package org.cas.client.platform.pimview.pimviewport;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;

public class BasicPIMViewportUI extends PIMViewportUI {

    // Shared UI object
    private static PIMViewportUI viewportUI;

    /**
     * 标准方法
     * 
     * @return 组件UI
     * @param c
     *            视口
     */
    public static ComponentUI createUI(
            JComponent c) {
        if (viewportUI == null) {
            viewportUI = new BasicPIMViewportUI();
        }
        return viewportUI;
    }

    /**
     * 标准方法
     * 
     * @param c
     *            视口
     */
    public void installUI(
            JComponent c) {
        super.installUI(c);
        installDefaults(c);
    }

    /**
     * 标准方法
     * 
     * @param c
     *            视口
     */
    public void uninstallUI(
            JComponent c) {
        super.uninstallUI(c);

    }

    /**
     * 标准方法
     */
    protected void installDefaults(
            JComponent c) {
        LookAndFeel.installColorsAndFont(c, "Viewport.background", "Viewport.foreground", "Viewport.font");
    }

    /**
     * 标准方法
     */
    protected void uninstallDefaults(
            JComponent c) {
    }
}
