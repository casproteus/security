package org.cas.client.platform.pimview.pimcard;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;

public class BasicCardViewPanelUI extends CardViewPanelUI {

    // Shared UI object
    private static CardViewPanelUI panelUI;

    private CardViewPanel cardViewPanel;

    /**
     * 标准方法
     * 
     * @return 卡片视图的UI
     * @param c
     *            卡片视图
     */
    public static ComponentUI createUI(
            JComponent c) {
        if (panelUI == null) {
            panelUI = new BasicCardViewPanelUI();
        }
        return panelUI;
    }

    /**
     * 标准方法
     * 
     * @param c
     *            卡片视图
     */
    public void installUI(
            JComponent c) {
        cardViewPanel = (CardViewPanel) c;
        super.installUI(cardViewPanel);
        installDefaults(cardViewPanel);
        // installKeyboardActions();
    }

    /**
     * 标准方法
     * 
     * @param c
     *            卡片视图
     */
    public void uninstallUI(
            JComponent c) {
        super.uninstallUI(c);
        // uninstallKeyboardActions();
        cardViewPanel = null;
    }

    /**
     * 标准方法
     */
    protected void installDefaults(
            CardViewPanel p) {
        LookAndFeel.installColorsAndFont(p, "Panel.background", "Panel.foreground", "Panel.font");
        LookAndFeel.installBorder(p, "Panel.border");
    }

    /**
     * 标准方法
     */
    protected void uninstallDefaults(
            CardViewPanel p) {
        LookAndFeel.uninstallBorder(p);
    }
    // 注:以下代码今后可能会派用场
    // /** 注销注册的键盘动作
    // */
    // protected void uninstallKeyboardActions()
    // {
    // //通过 SwingUtilities 来完成
    // SwingUtilities.replaceUIInputMap(cardViewPanel, JComponent.
    // WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
    // SwingUtilities.replaceUIActionMap(cardViewPanel, null);
    // }
    //
    // /** 注册键盘动作
    // * Register all keyboard actions on the PIMTable.
    // */
    // protected void installKeyboardActions()
    // {
    // //得到我们的动作映射表
    // ActionMap map = getActionMap();
    // //把表格的动作映射表换掉
    // SwingUtilities.replaceUIActionMap(cardViewPanel, map);
    // //得到输入映射,也统统换掉
    // InputMap inputMap = getInputMap(JComponent.
    // WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    // SwingUtilities.replaceUIInputMap(cardViewPanel,
    // JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
    // inputMap);
    // }
    //
    // /** 得到输入映射
    // * @param condition 条件,是一个整形的ID值
    // * @return 返回输入映射
    // */
    // InputMap getInputMap(int condition)
    // {
    // //是这么个条件就做
    // if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    // {
    // //先得到键盘映射
    // InputMap keyMap = (InputMap)UIManager.get("Table.ancestorInputMap");
    // InputMap rtlKeyMap;
    // //如果组件是自左到右或父类也是,就将其返回
    // if (cardViewPanel.getComponentOrientation().isLeftToRight() ||
    // ((rtlKeyMap = (InputMap)UIManager.get("Table.ancestorInputMap.RightToLeft")) == null))
    // {
    // return keyMap;
    // }
    // //否则使用父类的映射
    // else
    // {
    // rtlKeyMap.setParent(keyMap);
    // return rtlKeyMap;
    // }
    // }
    // return null;
    // }
    //
    // /** 得到动作映射
    // * @return 返回动作映射
    // */
    // ActionMap getActionMap()
    // {
    // //创建出其实例
    // ActionMap map = createActionMap();
    // //为观感设置该项
    // if (map != null)
    // {
    // UIManager.getLookAndFeelDefaults().put("CardViewPanel.actionMap", map);
    // }
    // return map;
    // }
    //
    // /** 创建动作映射
    // * @return 得到动作映射
    // */
    // ActionMap createActionMap()
    // {
    // // BasicLookAndFeel.makeComponentInputMap(c
    // // BasicLookAndFeel.loadKeyBindings(retMap
    // // BasicLookAndFeel.
    // //新建一个动作映射资源
    // ActionMap map = new ActionMapUIResource();
    // //放入方向键控制
    // map.put("selectNextColumn", new NavigationalAction
    // (1, 0, false, false, false));
    // map.put("selectPreviousColumn", new NavigationalAction
    // (-1, 0, false, false, false));
    // map.put("selectNextRow", new NavigationalAction
    // (0, 1, false, false, false));
    // map.put("selectPreviousRow", new NavigationalAction
    // (0, -1, false, false, false));
    // //放入方向键扩展控制
    // map.put("selectNextColumnExtendSelection", new NavigationalAction
    // (1, 0, false, true, false));
    // map.put("selectPreviousColumnExtendSelection", new NavigationalAction
    // (-1, 0, false, true, false));
    // map.put("selectNextRowExtendSelection", new NavigationalAction
    // (0, 1, false, true, false));
    // map.put("selectPreviousRowExtendSelection", new NavigationalAction
    // (0, -1, false, true, false));
    // //放入方向页键扩展控制
    // map.put("TAB",
    // new NavigationalAction(1, 0, true, false, true));
    // map.put("selectPreviousColumnCell",
    // new NavigationalAction(-1, 0, true, false, true));
    // map.put("selectNextRowCell",
    // new NavigationalAction(0, 1, true, false, true));
    // map.put("selectPreviousRowCell",
    // new NavigationalAction(0, -1, true, false, true));
    //
    // return map;
    // }
    //
    //
    // /**
    // * 键盘动作,本类好象是用来进行方向键处理
    // */
    // private static class NavigationalAction extends AbstractAction
    // {
    // /** 保存X坐标偏移
    // */
    // protected int dx;
    // /** 保存Y坐标偏移
    // */
    // protected int dy;
    // /** 绑定标志
    // */
    // protected boolean toggle;
    // /** 扩展标志
    // */
    // protected boolean extend;
    // /** 选中标志
    // */
    // protected boolean inSelection;
    //
    // /** 创建方向键控制类的一个实例
    // * @param dx X坐标偏移
    // * @param dy Y坐标偏移
    // * @param toggle 是否绑定的标志
    // * @param extend 可扩展标志
    // * @param inSelection 选中标志
    // */
    // protected NavigationalAction(int dx, int dy, boolean toggle, boolean extend,
    // boolean inSelection)
    // {
    // this.dx = dx;
    // this.dy = dy;
    // this.toggle = toggle;
    // this.extend = extend;
    // this.inSelection = inSelection;
    // }
    //
    // /** 实现父类中的抽象方法,执行操作
    // * @param e 动作事件源
    // */
    // public void actionPerformed(ActionEvent e)
    // {
    // //int dx, int dy, boolean toggle, boolean extend,boolean inSelection
    //
    // }
    // }
}
