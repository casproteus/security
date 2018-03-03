package org.cas.client.platform.cascontrol.navigation;

import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;

/**
 * 文件夹列表的节点编辑器
 */
class FolderNodeEditor extends CASTreeCellEditor {
    /**
     * 构造器
     * 
     * @param tree
     *            树的实例
     * @param render
     *            树节点的类型
     */
    FolderNodeEditor(JTree tree, FolderNodeRender render) {
        super(tree, render);
    }

    /**
     * 该方法判断该节点是否可以被编辑
     * 
     * @param evt
     *            相关的事件
     * @return true: 重新命名 false: 不可以改名
     * @see java.util.EventObject
     */
    public boolean canEditImmediately(
            EventObject evt) {
        return CASTree.isRename();
    }

    public JTextField getRealEditor() {
        if (realEditor != null && realEditor instanceof DefaultCellEditor) {
            return (JTextField) ((DefaultCellEditor) realEditor).getComponent();
        } else {
            return null;
        }
    }
}
