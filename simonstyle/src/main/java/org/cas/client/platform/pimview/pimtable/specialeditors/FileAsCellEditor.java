package org.cas.client.platform.pimview.pimtable.specialeditors;

import java.awt.Container;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableHeader;



/**
 * 它只需要处理几个控制键就可以的
 */

public class FileAsCellEditor extends JTextField
{

    /** Creates a new instance of PIMTextFieldEditor */
    public FileAsCellEditor()
    {
        super();
        setFocusTraversalKeysEnabled(false);
    }
    /** Processes key events occurring on this component by dispatching
     * them to any registered KeyListener objects.In this method,the
     * key event is passed to ETextPane object and the formual.
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

        //super.processKeyEvent(e);
    }
    /** 是否交由父类处理,过滤一些键
     * @return 是否交由父类处理
     * @param event 键盘事件 */
    public boolean canTransfered(KeyEvent event)
    {
        int code = event.getKeyCode();
        int modify = event.getModifiers();
        if ((modify != KeyEvent.ALT_MASK && code == KeyEvent.VK_ENTER) ||
        code == KeyEvent.VK_TAB || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_UP
        || code == KeyEvent.VK_CANCEL || code == KeyEvent.VK_ESCAPE)
        {
            return true;
        }
        return false;
    }
}
