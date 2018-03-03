package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.cas.client.platform.CASControl;




public class DragCopyAction extends AbstractAction
{
    /** Creates new DragCopyAction
     * 构建器: 拖动复制记录动作
     */
    public DragCopyAction()
    {
    }

    /* action is performed */
    public void actionPerformed(ActionEvent evt)
    {
        CASControl.ctrl.getFolderTree().dragCopy();
    }
}
