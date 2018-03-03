package org.cas.client.platform.pimview.pimtable;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;

/**
 * 本类实际是标记状态这一字段的绘制器
 */

public class PIMCheckBoxEditor extends JCheckBox {
    /** Creates a new instance of PIMCheckBoxEditor */
    public PIMCheckBoxEditor() {
        super();
        setFocusTraversalKeysEnabled(false);
    }

    public PIMCheckBoxEditor(String text, boolean selected, char mnemonic) {
        super(text, selected);
        setMnemonic(mnemonic);
        setFocusTraversalKeysEnabled(false);
    }

    /**
     * Processes key events occurring on this component by dispatching them to any registered KeyListener objects.In
     * this method,the key event is passed to ETextPane object and the formual.
     * 
     * @param e
     *            键盘事件
     */
    public void processKeyEvent(
            KeyEvent e) {
        Container parent = getParent();
        if (canTransfered(e) && parent instanceof PIMTable) {
            ((PIMTable) parent).processKeyEvent(e);
        } else if (canTransfered(e) && parent instanceof PIMTableHeader) {
            ((PIMTableHeader) parent).processKeyEvent(e);
        }

        super.processKeyEvent(e);
    }

    /**
     * 是否交给父类处理,过滤一些键
     * 
     * @return 是否交给父类处理
     * @param event
     *            键盘事件
     */
    public boolean canTransfered(
            KeyEvent event) {
        int code = event.getKeyCode();
        int modify = event.getModifiers();
        if (((modify != KeyEvent.ALT_MASK) && (code == KeyEvent.VK_ENTER)) || code == KeyEvent.VK_TAB) {
            return true;
        }
        return false;
    }

    /**
     * 在本组的目前表格时,组合框内容变化会调用本方法,而且编辑器代表会捕获这个动作 的语义事件,从而将本编辑器组件从表格(头)中移除,这是我们所不期望的,所以要终结 本方法的执行
     * 
     * @param e
     *            动作事件
     */
    public void fireActionPerformed(
            ActionEvent e) {
        Container parent = getParent();
        if (parent != null && (parent instanceof PIMTable || parent instanceof PIMTableHeader)) {
            return;
        } else {
            super.fireActionPerformed(e);
        }
    }

}
