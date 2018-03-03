package org.cas.client.platform.cascontrol;

import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.pimmodel.event.IPIMModelListener;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.resource.international.PaneConsts;

/** 用于响应model的事件。 */
public class ModelChangeListener implements IPIMModelListener {

    /**
     * Creates a new instance of ModelChangeListener 构建器: model变化事件
     */
    public ModelChangeListener() {
    }

    /** 响应model的事件 */
    public void recordStateChanged(
            PIMModelEvent e) {
        CASControl.ctrl.getMainPane().updateView(e);
        TreePath tmpRootPath = CASTree.getInstance().getPathFromRoot(PaneConsts.HEAD_PAGE);
        CASTree.getInstance().putUnreads(tmpRootPath, PaneConsts.HEAD_PAGE, false);
    }
}
