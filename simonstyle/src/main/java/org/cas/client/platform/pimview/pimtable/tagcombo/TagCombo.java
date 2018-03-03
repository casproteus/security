package org.cas.client.platform.pimview.pimtable.tagcombo;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;

import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableHeader;



/**
 * 本 <code>Item</code> 封装一个图标和一个字符串
 */

public class TagCombo extends JComboBox
{
    /** Creates a new instance of ColorCombo */
    public TagCombo()
    {
        super();
        setUI (new TagComboBoxUI (this));
        setRenderer(new ItemCellRenderer());
        //setEditable(true);
        //setBackground (Color.white);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
        setFocusTraversalKeysEnabled(false);
    }

    /**
     * 在本组的目前表格时,组合框内容变化会调用本方法,而且编辑器代表会捕获这个动作
     * 的语义事件,从而将本编辑器组件从表格(头)中移除,这是我们所不期望的,所以要终结
     * 本方法的执行
     */
    protected void fireActionEvent()
    {
        Container parent = getParent();
        if (parent != null && (parent instanceof PIMTable || parent instanceof PIMTableHeader))
        {
            return;
        }
        else
        {
            super.fireActionEvent();
        }
    }
    /** Invoked when a key has been pressed.
     *  因为本组件的UI和标准的不一致,不超掉会出异常
     * @param e the key event.
     */
    public void keyPressed(KeyEvent e)
    {
    }
    /**是否交由父类处理,过滤一些键
     * @param event 键盘事件
     * @return 是否交由父类处理
     */
    public boolean canTransfered(KeyEvent event)
    {
        int code = event.getKeyCode();
        int modify = event.getModifiers();
        if (((modify != KeyEvent.ALT_MASK) && (code == KeyEvent.VK_ENTER)) ||
        code == KeyEvent.VK_TAB)
        {
            return true;
        }
        return false;
    }
    /**
     * @param e 键盘事件 */
    public void processKeyEvent(KeyEvent e)
    {
        Container parent = getParent();
        if (canTransfered(e) && parent instanceof PIMTable)
        {
            ((PIMTable)parent).processKeyEvent(e);
        }
        else if (canTransfered(e) && parent instanceof PIMTableHeader)
        {
            ((PIMTableHeader)parent).processKeyEvent(e);
        }

        super.processKeyEvent(e);
    }
    /** 本组件的尺寸
     */    
    private final Dimension d = new Dimension (38,20);

}

