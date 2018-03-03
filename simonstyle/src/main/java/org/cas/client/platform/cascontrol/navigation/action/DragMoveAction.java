package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.cas.client.platform.CASControl;




public class DragMoveAction extends AbstractAction
{
    /** Creates new DragMoveAction
     * 构建器:拖动移动动作
     */
    public DragMoveAction()
    {
    }

    /* action is performed */
    public void actionPerformed(ActionEvent evt)
    {
        CASControl.ctrl.getFolderTree().dragMove();
    }
}
