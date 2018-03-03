package org.cas.client.platform.cascontrol.navigation;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PropertyDialogConstant;

class FolderSelectDialog extends JDialog implements ActionListener, TreeSelectionListener {
    /**
     * Creates new MoveFolderDialog
     * 
     * @param frame
     *            调用此dialog的frame, 用于构造父类
     */
    public FolderSelectDialog(Dialog prmParent) {
        super(prmParent, true);
        setTitle(PropertyDialogConstant.SELECTED);// 设置标题

        int x = 0;
        int y = 0;// 初始化null布局的坐标

        JLabel label = new JLabel(PropertyDialogConstant.FOLDER);// 设置label
        label.setDisplayedMnemonic('F');
        label.setBounds(x, y, label.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(label);

        y += CustOpts.LBL_HEIGHT;

        treePane = new JPanel(); // 获得tree, 并显示
        tree = CASControl.ctrl.getFolderTree().replicateTree(true);

        // tree.expandPath(prmPath);
        // 不允许鼠标按下后切换视图
        // tree.setEnableChangeApp(false);
        // 自根结点扩展树??没用
        // EFolderNode node = new EFolderNode(emo.resource.international.pim.PaneConstant.DELETE_ITEM,null, null, null,
        // false,emo.pim.pimcontrol.popupmenu.FolderPopupMenu.CAN_NEW_FOLDER);
        // tree.setNodeExpand(new
        // TreePath(node.getPath()),emo.resource.international.pim.PaneConstant.DELETE_ITEM,true);
        // tree.expandPath(tree.getPathFromRoot(emo.resource.international.pim.PaneConstant.DELETE_ITEM));
        // tree.expandPath(new TreePath(((JTree)tree).getModel().getRoot()));
        // srcTree = PIMControl.ctrl.getFolderTree();
        tree.setBounds(0, 0, maxWidth, treeHeight);

        treePane.add(tree);
        treePane.setBounds(x, y, maxWidth, treeHeight);
        getContentPane().add(treePane);
        tree.setOperationValid(false);
        tree.getSelectioinModel().addTreeSelectionListener(this);
        label.setLabelFor(tree);

        y += treeHeight + CustOpts.HOR_GAP;
        y += CustOpts.HOR_GAP;
        // x = (maxWidth - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP) / 2;
        x = maxWidth - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP;

        // 设置底部的2个按钮
        // x += CustOpts.BTN_WIDTH + CustOpts.HOR_GAP;
        ok = new JButton(DlgConst.OK);
        ok.setBounds(x, y, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        ok.setEnabled(false);
        getContentPane().add(ok);
        getRootPane().setDefaultButton(ok);
        ok.addActionListener(this); // ok按钮的事件响应,移动节点

        x += CustOpts.BTN_WIDTH + CustOpts.HOR_GAP;
        cancel = new JButton(DlgConst.CANCEL);
        cancel.setBounds(x, y, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);
        cancel.addActionListener(this);
        y += CustOpts.BTN_HEIGHT + CustOpts.HOR_GAP;

        maxHeight = y; // 获得dialog的实际高度
        setBounds((CustOpts.SCRWIDTH - maxWidth) / 2, (CustOpts.SCRHEIGHT - maxHeight) / 2, maxWidth, maxHeight); // 对话框的默认尺寸。
    }

    public int getPathID() {
        return folderID;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        Object src = e.getSource();
        if (src == ok) {
            folderID = ((CASNode) tree.getSelectedPath().getLastPathComponent()).getPathID();
        } else if (src == cancel) {
            tree.getSelectioinModel().removeTreeSelectionListener(this);
            dispose();
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(
            TreeSelectionEvent e) {
        ok.setEnabled(true);
    }

    private int maxWidth = 260;
    private int maxHeight;
    private int treeHeight = 200;
    private int folderID;

    // 显示的组件
    private JButton ok, cancel;
    private JPanel treePane;
    private CASTree tree;
}
