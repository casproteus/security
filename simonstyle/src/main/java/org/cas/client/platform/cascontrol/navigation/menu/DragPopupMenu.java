package org.cas.client.platform.cascontrol.navigation.menu;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cas.client.platform.cascontrol.navigation.action.DragCopyAction;
import org.cas.client.platform.cascontrol.navigation.action.DragMoveAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.MenuConstant;


public class DragPopupMenu extends JPopupMenu 
{
    /** Creates new DragPopupMenu */
    public DragPopupMenu()
    {
        setMenuItem(menuItem, itemAction);
    }
    
    /**
     * 依次添加PopMenu的菜单项
     * @param menuItem 菜单项的名字
     * @param itemAction 菜单项的事件
     */
    protected void setMenuItem(String[] menuItem, Action[] itemAction)
    {
        int len = menuItem.length;
        JMenuItem item;
        for (int i = 0; i < len; ++i)
        {
            if (menuItem[i].length() == 0)
            {
                addSeparator();
            }
            else
            {
                item = new JMenuItem(menuItem[i]);
                item.setFont(CustOpts.custOps.getFontOfDefault());
                item.addActionListener(itemAction[i]);
                add(item);
            }
        }
    }
    
    private String[] menuItem =
    {
        MenuConstant.DRAG_MOVE,
        MenuConstant.DRAG_COPY,
    };

    private Action[] itemAction =
    {
        new DragMoveAction(),
        new DragCopyAction(),
    };
}
