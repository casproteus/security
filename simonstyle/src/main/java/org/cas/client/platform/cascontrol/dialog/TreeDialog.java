package org.cas.client.platform.cascontrol.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PropertyDialogConstant;

public class TreeDialog extends JDialog implements ActionListener, ItemListener, ComponentListener {

    public TreeDialog(Dialog prmParent, boolean prmContainsSubFolderCheck) {
        super(prmParent, true);// 设置标题

        containsCheckBox = prmContainsSubFolderCheck;
        setTitle(PropertyDialogConstant.SELECTED);
        getContentPane().setLayout(null);
        setBounds((CustOpts.SCRWIDTH - 260) / 2, (CustOpts.SCRHEIGHT - 400) / 2, 260, 400); // 对话框的默认尺寸。

        label = new JLabel(PropertyDialogConstant.FOLDER);// 设置label
        treePane = new JPanel();// 获得tree, 并显示
        tree = CASControl.ctrl.getFolderTree().duplicate(true);
        srcTree = CASControl.ctrl.getFolderTree();
        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);
        checkBox = new JCheckBox(PropertyDialogConstant.PIMDIALOG_CHECKBOX_SUB_FOLDER, false);

        label.setDisplayedMnemonic('F');
        checkBox.setMnemonic('C');
        label.setLabelFor(tree);
        tree.setEnableChangeApp(false); // 不允许鼠标按下后切换视图
        tree.setSelectionPath(srcTree.getSelectedPath());
        tree.setOperationValid(false);
        tree.updateRenderer();
        treePane.setBackground(Color.WHITE);
        treePane.setBorder(BorderFactory.createEtchedBorder());
        treePane.setLayout(new BorderLayout());
        // 自根结点扩展树??没用
        // EFolderNode node = new EFolderNode(emo.resource.international.pim.PaneConstant.DELETE_ITEM,null, null, null,
        // false,emo.pim.pimcontrol.popupmenu.FolderPopupMenu.CAN_NEW_FOLDER);
        // tree.setNodeExpand(new
        // TreePath(node.getPath()),emo.resource.international.pim.PaneConstant.DELETE_ITEM,true);
        // tree.expandPath(tree.getPathFromRoot(emo.resource.international.pim.PaneConstant.DELETE_ITEM));
        // tree.expandPath(new TreePath(((JTree)tree).getModel().getRoot()));

        reLayout();

        treePane.add(tree);
        getContentPane().add(label);
        getContentPane().add(treePane);
        getContentPane().add(ok);
        getContentPane().add(cancel);

        if (prmContainsSubFolderCheck)
            checkBox.addItemListener(this);
        ok.addActionListener(this); // ok按钮的事件响应, 移动节点
        cancel.addActionListener(this);
        addComponentListener(this);
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    public void componentHidden(
            ComponentEvent e) {
    }

    private void reLayout() {
        label.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, label.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        cancel.setBounds(getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.BTN_WIDTH, getHeight() - CustOpts.SIZE_TITLE
                - CustOpts.SIZE_EDGE - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        ok.setBounds(cancel.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        treePane.setBounds(label.getX(), label.getY() + label.getHeight(), getWidth() - CustOpts.HOR_GAP * 3, ok.getY()
                - label.getY() - label.getHeight() - CustOpts.VER_GAP);
        if (containsCheckBox)
            checkBox.setBounds(treePane.getX(), ok.getY(), checkBox.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        validate();
    }

    /** 是否包含子文件夹 @return 包含则返回true，否则为false */
    public boolean containsSubFolders() {
        return containsCheckBox && containsSubFolder;
    }

    public String getSelectedName() {
        return nodeName;
    }

    public int getSelectedNodeID() {
        return ((CASNode) tree.getSelectedPath().getLastPathComponent()).getPathID();
    }

    /* called by:NewRuleDialog得到当前结点的路径 */
    public String getAllPath() {
        Object[] paths = tree.getSelectedPath().getPath();
        StringBuffer tmpPath = new StringBuffer();
        for (int i = 0; i < paths.length - 1; i++) {
            tmpPath.append(paths[i]).append(", ");
        }
        return tmpPath.toString();
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        Object s = e.getSource();
        if (s == ok) {
            TreePath selectedpath = tree.getSelectedPath();
            nodeName = selectedpath.getLastPathComponent().toString();
            dispose();
        } else if (s == cancel)
            dispose();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(
            ItemEvent e) {
        containsSubFolder = checkBox.isSelected();
    }

    private String nodeName;
    private JLabel label; // 显示的组件
    private JCheckBox checkBox;
    private JButton ok;
    private JButton cancel;
    private JPanel treePane;
    private CASTree tree;
    private CASTree srcTree;
    boolean containsSubFolder = false;// 默认选择包括子文件夹
    boolean containsCheckBox;
}
