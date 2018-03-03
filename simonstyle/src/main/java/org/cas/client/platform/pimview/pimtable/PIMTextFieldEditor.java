package org.cas.client.platform.pimview.pimtable;

import java.awt.Container;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

class PIMTextFieldEditor extends JTextField {

    /** Creates a new instance of PIMTextFieldEditor */
    PIMTextFieldEditor() {
        super();
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
            PIMTable table = (PIMTable) parent;
            // 检查表格上的一个标志位,
            if (!table.getRepostEventFlag()) {
                table.processKeyEvent(e);
            }
        } else if (canTransfered(e) && parent instanceof PIMTableHeader) {
            ((PIMTableHeader) parent).processKeyEvent(e);
        }
        super.processKeyEvent(e);
        canSaveContent = false;
    }

    /**
     * 是否交由父类来处理,过滤一些键
     * 
     * @return 是否交由父类来处理
     * @param event
     *            键盘事件
     */
    boolean canTransfered(
            KeyEvent event) {
        // 当使用紫光输入法的时候，在输入中文状态下可以用回车键输入英文，此时此事件可以发送过来并被pim处理
        // 这样使得用户尚未输入完成时将内容进行存储，后果严重。经检查：此种情况下KeyEvent只有release事件，
        // 没有press事件，而直接输入英文时候的keyEvent却press,release事件都有，故加以下代码改正之
        // 另：若有输入法时候用回车输入英文，则下面注释代码中action为null，否则不为null，以后或可使用
        // javax.swing.Action action = null;
        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                havePress = true;
            } else if (event.getID() == KeyEvent.KEY_RELEASED) {
                if (havePress) {
                    canSaveContent = true;
                    havePress = false;
                }
            }
            // javax.swing.InputMap inputMap = getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
            // javax.swing.ActionMap actionMap = getActionMap();
            // javax.swing.KeyStroke ks;
            // if (e.getID() == KeyEvent.KEY_TYPED)
            // {
            // ks = javax.swing.KeyStroke.getKeyStroke(e.getKeyChar());
            // }
            // else
            // {
            // ks = javax.swing.KeyStroke.getKeyStroke(e.getKeyCode(),e.getModifiers(), !(e.getID()==
            // KeyEvent.KEY_PRESSED));
            // }
            // if(inputMap != null && actionMap != null && isEnabled())
            // {
            // Object binding = inputMap.get(ks);
            // action = (binding == null) ? null : actionMap.get(binding);
            // A.s(" action :"+action);
            // }
        }
        int code = event.getKeyCode();
        int modify = event.getModifiers();
        if ((havePress || canSaveContent)
                && ((modify != KeyEvent.ALT_MASK && code == KeyEvent.VK_ENTER) || code == KeyEvent.VK_TAB
                        || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_UP)) {
            return true;
        }
        return false;
    }

    private boolean havePress = false;
    private boolean canSaveContent = false;
}
