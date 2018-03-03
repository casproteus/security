package org.cas.client.platform.cascontrol.navigation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.CellEditor;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.menuaction.FolderNewAction;
import org.cas.client.platform.cascontrol.menuaction.FolderRenameAction;
import org.cas.client.platform.cascontrol.menuaction.LastFolderAction;
import org.cas.client.platform.cascontrol.menuaction.NextFolderAction;
import org.cas.client.platform.cascontrol.navigation.action.MoveToFolderAction;
import org.cas.client.platform.cascontrol.navigation.action.RemoveFolderAction;
import org.cas.client.platform.cascontrol.navigation.menu.DragPopupMenu;
import org.cas.client.platform.cascontrol.navigation.menu.FolderPopupMenu;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.resource.international.PaneConsts;

/**
 * 该类维护一个含有文件夹样式的树结构及其对树的操作. 但没有独立出来，应该将删除节点等的操作方法内部做掉。不应该调用 emo.pim.action.folderlist.NewFolderAction等。应该是外面的调用里面的。
 */

public class CASTree extends JComponent implements MouseMotionListener, KeyListener, TreeSelectionListener,
        MouseListener, DropTargetListener, FocusListener, CellEditorListener {
    private static CASTree instance = null;

    public static CASTree getInstance() {
        if (instance == null)
            instance = new CASTree();
        return instance;
    }

    private CASTree() {
        // 实例化===========================================================
        changeSupport = new PropertyChangeSupport(this);// 实例化一个属性变化监听器.

        root = new CASNode("PIM", null, false, FolderPopupMenu.CANNOT_NEW_FOLDER, -1);// 实例化根节点.
        tree = new JTree(root); // 实例化JTree.
        scrollPane = new JScrollPane(tree); // 将tree加到滚动面板上.

        rootPath = new TreePath(root.getPath()); // 根结点对应路径.
        secondNode =
                new CASNode(PaneConsts.HEAD_PAGE, CustOpts.custOps.getTodayworkIcon(false), false,
                        FolderPopupMenu.CAN_NEW_FOLDER, 9770402);

        render = new FolderNodeRender(false); // 给tree赋一个绘制器.
        editor = new FolderNodeEditor(tree, render); // 给tree赋一个编辑器.
        dropTarget = new DropTarget(tree, DnDConstants.ACTION_MOVE, this);

        // 内容记录============================================================================
        treeSelectionModel = (DefaultTreeSelectionModel) tree.getSelectionModel();
        defaultCursor = tree.getCursor(); // 记住JTree的默认光标.当拖拉以后要恢复到该坐标.
        treeModel = (DefaultTreeModel) tree.getModel(); // 保存一个对tree的model的引用,因为常用,省去每次造型.
        srcRow = CustOpts.custOps.getActiveAppType() + 1;

        // 属性设置============================================================================
        treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); // 设置,使tree只允许单选.

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        tree.setCellEditor(editor);
        tree.setCellRenderer(render);
        tree.setOpaque(false);
        tree.setEditable(true);
        tree.setRootVisible(false); // 使根节点(显示一个文件包和"PIM"字串的节点)不显示.(不设的话默认为显示).
        tree.setShowsRootHandles(true); // 使根节点允许伸展,收缩.用户输入密码前和用户嫌PIM占地方时可以将tree收起来.
        tree.putClientProperty("JTree.lineStyle", "Angled");// 给tree标记一个属性.(每个JComponent都维护一个小hashtatle,用于存外部赋予的属性,该属性变化时将发出propertychanged事件.)

        setLayout(new BorderLayout());

        // 搭建============================================================================
        add(scrollPane); // 加入含有树节点的面板

        // 加监听器============================================================================
        tree.addTreeSelectionListener(this);
        tree.addKeyListener(this);
        tree.addMouseListener(this);
        tree.addMouseMotionListener(this);
        tree.addFocusListener(this);
        tree.getCellEditor().addCellEditorListener(this);// new FolderEditorListener()); //给tree的editor加监听器.

        // 准备面板内容.=========================================================================
        addNode(null, null, secondNode); // 加入根结点

        initAppNodes();// 组装树上各点。

        // 从CustOps中取出应该选中的节点的路径,并做节点名字转换处理.然后选中===================================
        setSelectNode(CustOpts.custOps.getActivePathID()); // 取用户自定义设置，设置导航面板的缺省选取节点
        // 至此将该节点设置为选中状态.

        // 更新绘制=============================================================================
        updateRenderer();

        // 等Model准备好了之后,抽个时间将各个文件夹下的未读文件数标注一下.===============================
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    ICASModel tmpModel = CASControl.ctrl.getModel();
                    if (tmpModel == null || !tmpModel.isConnectedWell()) {
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                        }
                    } else {
                        // 构造时不初始化未读项目数,缩短启动时间,等用户登录后打开后才读,或在后台线程中读. 初始化未读状态
                        // putUnreads(tmpRootPath, PaneConstant.HEAD_PAGE, true);
                        break;
                    }
                }
            }
        }).start();
    }

    // 以下处理键盘事件===========================================================
    public void keyPressed(
            KeyEvent evt) {
        if (!isOperationValid())
            return;

        CASNode tmpNode = getSelectNode();
        if (!(tmpNode instanceof CASNode))
            return;

        int key = evt.getKeyCode();
        if ((key & KeyEvent.VK_DELETE) == KeyEvent.VK_DELETE)
            if (((CASNode) tmpNode).isModifiable())
                new RemoveFolderAction().actionPerformed(null); // 删除节点的操作
            else if ((key & KeyEvent.VK_INSERT) == KeyEvent.VK_INSERT)
                new FolderNewAction().actionPerformed(null); // 新建节点的操作
            else if ((key & KeyEvent.VK_F2) == KeyEvent.VK_F2)
                new FolderRenameAction().actionPerformed(null);
    }

    public void keyReleased(
            KeyEvent e) {
    }

    public void keyTyped(
            KeyEvent e) {
    }

    // 鼠标事件在此处理================================================================================================================
    public void mouseClicked(
            MouseEvent evt) {
    }

    public void mouseEntered(
            MouseEvent e) {
    }

    public void mouseExited(
            MouseEvent e) {
    }

    public void mousePressed(
            MouseEvent evt) {
    }// EMenuSelectionManager.clearPath();

    public void mouseReleased(
            MouseEvent evt) {
        Object tmpSource = evt.getSource();
        if (tmpSource != tree)
            return; // 目前只处理tree的鼠标事件,其它事件忽略掉-------------------------------------------

        // TreePath tmpOldSelePath = selePath != null ? selePath : null; //????防止selePath,tmpPath都为空时,报空
        TreePath tmpSelectedPath = tree.getSelectionPath();// tmpTree.getPathForLocation(evt.getX(), evt.getY());
                                                           // //得到鼠标落点所属节点的路径.
        selePath = tmpSelectedPath; // 不宜在此一次性判断tmpFolderPath是否为null,因为对于拖放,path为空时仍然有意义.

        int tmpButtonID = -1; // 因为0和1分别用来表示鼠标左键和鼠标右键了.所以初始化为负数.
        if (SwingUtilities.isLeftMouseButton(evt))
            tmpButtonID = LEFT_BUTTON;
        else if (SwingUtilities.isRightMouseButton(evt))
            tmpButtonID = RIGHT_BUTTON;// 记录是鼠标的左健还是右建,用于跟实例变量ButtonId比较.

        if (!dragStarted) {// 鼠标单击松开的处理(而不是拖放松开)================================================
            if (tmpSelectedPath == null) { // 先排除击空的情况.
            // ((PIMNode)tmpOldSelePath.getLastPathComponent()).setSelected(true);//???
            // tree.treeDidChange(); //????
                return;
            } // 至此已排除击空的情况-----------------------------------------------------------

            if (isRemoveListener) {// TODO:目前没能实现在右键菜单消失后选中的结点回到右键单击前，所以临时处理：无论左键右键,单击时都切换应用.
                int tmpNodeID = ((CASNode) selePath.getLastPathComponent()).getPathID();
                int tmpAppType = CASUtility.getAppIndexByFolderID(tmpNodeID);
                CASControl.ctrl.changeApplication(tmpAppType, tmpNodeID);
                tree.setSelectionPath(tmpSelectedPath);
            }

            if (tmpButtonID == RIGHT_BUTTON) { // 右键或中键单击,则弹出右键菜单,鼠标左键单击松开的处理-------------------------
                int tmpPathCount = selePath.getPathCount();
                if (tmpPathCount < 3)
                    return; // 过滤掉(本身就不可见的)PIM节点,个人资讯节点.-------------

                CASNode tmpNode = (CASNode) selePath.getLastPathComponent();
                if (tmpNode == null)
                    return; // 过滤掉没有选中的情况.--------------------------------

                tree.setSelectionPath(tmpSelectedPath);

                FolderPopupMenu tmpPopMenu = new FolderPopupMenu(selePath.toString());// 机制可以采取PIM菜单统一的机制,减缓Action的实例化.
                tmpPopMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        } else {// 鼠标拖放松开的处理.========================================================================================
            selePath = tmpSelectedPath;
            tree.setCursor(defaultCursor); // 首先恢复鼠标形状.

            dragStarted = false; // 开始拖放标记恢复初始值false.
            srcRow = tree.getRowForPath(selePath); // 用于标记拖放的起始行.

            if (buttonID != tmpButtonID) { // 防错处理,如果鼠标ID不对,则不作动作.
                ErrorUtil.write("the id of mouseEve is wrong(EFolderTree.mouseReleased())"); // 鼠标的ID错误");
                return;
            }

            if (buttonID == LEFT_BUTTON) // 左键拖放松开时的处理...
                dragMove();
            else { // 右键拖放并松开时的处理...
                int x = evt.getX();
                int y = evt.getY();
                Component parent = evt.getComponent();
                if (x < parent.getWidth())
                    new DragPopupMenu().show(parent, x, y);
            }
        }
    }

    // 以下为鼠标移动事件的处理***********************************************************************88
    /**
     * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will
     * continue to be delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     */
    public void mouseDragged(
            MouseEvent evt) {
        // if (!isOperationValid())
        // {
        // return;
        // }
        // if (SwingUtilities.isLeftMouseButton(evt))
        // {
        // //左键的拖动
        // buttonID = LEFT_BUTTON;
        // }
        // else if (SwingUtilities.isRightMouseButton(evt))
        // {
        // //右键的拖动
        // buttonID = RIGHT_BUTTON;
        // }
        // else
        // //如果不是左键或右键,则不处理,也不改变拖放标志.
        // {
        // buttonID = -1;
        // return;
        // }
        // Point pt = evt.getPoint();
        // int row = getMouseForRow(pt);
        // TreePath tmpPath = tree.getPathForRow(row);
        // if (tmpPath == null)
        // {
        // return;
        // }
        // if (!dragStarted)
        // {
        // dragStarted = true;
        // Rectangle rect = tree.getRowBounds(row);
        // if (rect.contains(pt))
        // {
        // srcRow = row;
        // tree.setCursor(DragSource.DefaultMoveDrop);
        // }
        // else
        // {
        // return;
        // }
        // }
        // tree.setSelectionRow(row);
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseMoved(
            MouseEvent e) {
    }

    // 鼠标事件处理代码结束=======================================================

    /** Invoked when a component gains the keyboard focus. */
    public void focusGained(
            FocusEvent e) {
        // TreePath activePath = tree.getSelectionPath();
        // String savedPath = CustomOptions.custOps.getActiveFolderPath();
        // if (activePath != null && savedPath.equals(activePath.toString()))
        // {
        // return;
        // }
        // activePath = getNodePath(rootPath, savedPath);
        // tree.setSelectionPath(activePath);
    }

    /** Invoked when a component loses the keyboard focus. */
    public void focusLost(
            FocusEvent e) {
    }

    // Tree事件监听=======================================================================================================
    /**
     * 节点数值变化的操作
     * 
     * @param evt
     *            树节点选择的事件
     */
    public void valueChanged(
            TreeSelectionEvent evt) {
        if (tree.isSelectionEmpty() || isSelectModel) {
            CASControl.ctrl.resetSystemStatus(IStatCons.FOLDER_SELECTED);
            return;
        }
        TreePath tmpPath = evt.getPath();
        if (tmpPath == null) {
            CASControl.ctrl.resetSystemStatus(IStatCons.FOLDER_SELECTED);
            return;
        }

        CASNode tmpNode = (CASNode) tmpPath.getLastPathComponent();
        if (tmpNode instanceof CASNode) {
            if (tmpNode.toString().equals(PaneConsts.HEAD_PAGE))
                CASControl.ctrl.resetSystemStatus(IStatCons.FOLDER_SELECTED);
            else
                CASControl.ctrl.addSystemStatus(IStatCons.FOLDER_SELECTED);
        }
    }

    // ==========================================================================

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters the operable part of the drop site for
     * the <code>DropTarget</code> registered with this listener.
     * 
     * @param dtde
     *            the <code>DropTargetDragEvent</code>
     */
    public void dragEnter(
            DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has exited the operable part of the drop site
     * for the <code>DropTarget</code> registered with this listener.
     * 
     * @param dte
     *            the <code>DropTargetEvent</code>
     */
    public void dragExit(
            DropTargetEvent dte) {
    }

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still over the operable part of the drop site
     * for the <code>DropTarget</code> registered with this listener.
     * 
     * @param dtde
     *            the <code>DropTargetDragEvent</code>
     */
    public void dragOver(
            DropTargetDragEvent dtde) {
        row = getMouseForRow(dtde.getLocation());
        if (tree.getPathForRow(row) == null)
            return;

        tree.setSelectionRow(row);
    }

    /**
     * Called if the user has modified the current drop gesture.
     * 
     * @param dtde
     *            the <code>DropTargetDragEvent</code>
     */
    public void dropActionChanged(
            DropTargetDragEvent dtde) {
    }

    /**
     * Called when the drag operation has terminated with a drop on the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     * <p>
     * This method is responsible for undertaking the transfer of the data associated with the gesture. The
     * <code>DropTargetDropEvent</code> provides a means to obtain a <code>Transferable</code> object that represents
     * the data object(s) to be transfered.
     * <P>
     * From this method, the <code>DropTargetListener</code> shall accept or reject the drop via the acceptDrop(int
     * dropAction) or rejectDrop() methods of the <code>DropTargetDropEvent</code> parameter.
     * <P>
     * Subsequent to acceptDrop(), but not before, <code>DropTargetDropEvent</code>'s getTransferable() method may be
     * invoked, and data transfer may be performed via the returned <code>Transferable</code>'s getTransferData()
     * method.
     * <P>
     * At the completion of a drop, an implementation of this method is required to signal the success/failure of the
     * drop by passing an appropriate <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method.
     * <P>
     * Note: The data transfer should be completed before the call to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method. After that, a call to the getTransferData() method of the
     * <code>Transferable</code> returned by <code>DropTargetDropEvent.getTransferable()</code> is guaranteed to succeed
     * only if the data transfer is local; that is, only if <code>DropTargetDropEvent.isLocalTransfer()</code> returns
     * <code>true</code>. Otherwise, the behavior of the call is implementation-dependent.
     * <P>
     * 
     * @param dtde
     *            the <code>DropTargetDropEvent</code>
     */
    public void drop(
            DropTargetDropEvent prmDtde) {
        tree.setSelectionRow(srcRow); // 先使树选中此时鼠标的落点。
        try {
            DataFlavor tmpRecFlavor =
                    new DataFlavor(Class.forName("org.cas.client.platform.pimmodel.PIMRecord"), "PIM Record");
            Transferable tmpTransferable = prmDtde.getTransferable();

            if (tmpTransferable.isDataFlavorSupported(tmpRecFlavor)) {
                prmDtde.acceptDrop(DnDConstants.ACTION_MOVE);
                Vector tmpTargetVector = (Vector) tmpTransferable.getTransferData(tmpRecFlavor);

                if (tmpTargetVector == null)
                    return;
                else
                    MoveToFolderAction.moveRecords(tmpTargetVector, ((CASNode) tree.getPathForRow(row)
                            .getLastPathComponent()).getPathID());
            } else {
                prmDtde.rejectDrop();
            }
        } catch (ClassNotFoundException ex) {
            prmDtde.rejectDrop();
        } catch (UnsupportedFlavorException ufException) {
            prmDtde.rejectDrop();
        } catch (IOException ex) {
            prmDtde.rejectDrop();
        }
    }

    // 开始CellEdit监听＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
    /**
     * 该方法取消所作的修改
     * 
     * @param evt
     *            修改节点的事件
     * @see javax.swing.event.ChangeEvent
     */
    public void editingCanceled(
            ChangeEvent evt) {
        setIsRename(false);
        return;
    }

    /**
     * 该方法停止编辑的节点
     * 
     * @param evt
     *            结点变化事件
     */
    public void editingStopped(
            ChangeEvent evt) {
        setIsRename(false);

        CASNode tmpEditNode = getSelectNode();
        String tmpOldCap = tmpEditNode.getName(); // 获得节点的名称
        String tmpNewCap = (String) ((CellEditor) evt.getSource()).getCellEditorValue();

        if (tmpOldCap.equals(tmpNewCap))
            return; // 没有变化,则直接返回.-----------------------

        if (tmpNewCap.length() == 0) // 没有输入任何内容,则报错,继续编辑.
        {
            SOptionPane.showErrorDialog(MessageCons.W10509);
            tmpNewCap = tmpEditNode.getName();
            ((CASNode) tmpEditNode).setName(tmpNewCap);
            CASUtility.EMPTYSTR.substring(2);// @TODO:暂时加此句使出异常,能使节点不推出编辑状态,以便用户继续编辑.
            return;// --------------------------------------------------
        }

        for (int i = 0; i < tmpNewCap.length(); i++) {
            char ch = tmpNewCap.charAt(i);
            if (!((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch == '_') || (ch > 256) || (ch >= '0' && ch <= '9'))) {
                SOptionPane.showErrorDialog(MessageCons.W10509);
                tmpNewCap = tmpEditNode.getName();
                ((CASNode) tmpEditNode).setName(tmpNewCap);
                CASUtility.EMPTYSTR.substring(2);// @TODO:暂时加此句使出异常,能使节点不推出编辑状态,以便用户继续编辑.
                return;// ----------------------------------------------
            }
        }

        CASNode tmpParentNode = (CASNode) tmpEditNode.getParent();
        for (int i = 0, tmpLen = tmpParentNode.getChildCount(); i < tmpLen; i++) {
            if (tmpNewCap.equals(((CASNode) tmpParentNode.getChildAt(i)).getName())) {
                SOptionPane.showErrorDialog(MessageCons.W10509); // 重命名无效,可能的原因有.........
                tmpNewCap = tmpEditNode.getName();
                ((CASNode) tmpEditNode).setName(tmpNewCap);
                CASUtility.EMPTYSTR.substring(2);// @TODO:暂时加此句使出异常,能使节点不推出编辑状态,以便用户继续编辑.
                ErrorUtil.write("the Exception above does not matter, just let it go.");
                return;// ----------------------------------------------
            }
        }

        ((CASNode) tmpEditNode).setName(tmpNewCap);

        // 开始调整Config文件中的导航树节点结构信息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        String tmpParentPathStr = new TreePath(tmpParentNode.getPath()).toString(); // 上一级节点的String.
        String tmpChildNames = (String) CustOpts.custOps.getValue(tmpParentPathStr); // 上级节点以前在Config中的下属节点
        Vector tmpVec = CASUtility.parserStrToVec(tmpChildNames, CustOpts.BIAS, tmpChildNames.length()); // 字串.

        StringBuffer tmpNames = new StringBuffer();
        int tmpLen = tmpVec.size();
        for (int i = 0; i < tmpLen; i++) // 遍历每一个节点.
        {
            String tmpChildName = (String) tmpVec.get(i);// 发现某个节点的值
            if (tmpChildName.equals(tmpOldCap)) // 和旧的值相同的,
                tmpChildName = tmpNewCap; // 就把它用新值替换掉.
            tmpNames.append(tmpChildName); // 这样就得到了父节点在Config文件中对应的新的Vulue值.
            tmpNames.append(CustOpts.BIAS);
        }
        tmpChildNames = tmpNames.toString(); // @NOTE:因为是改名操作,所以tmpLen必然大
        tmpChildNames = tmpChildNames.substring(0, tmpChildNames.length() - 1); // 于0.这里也就不需要判断尺寸,去掉最后一个
        CustOpts.custOps.setKeyAndValue(tmpParentPathStr, tmpChildNames); // 斜线。并存入Config.
        // 至此被改名节点所在的一级的结构调整完毕－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        if (PaneConsts.PIM_PATH.equals(tmpParentPathStr))// @NOTE如果是应用级的节点被改了名字，那么还必须调整一下AppCapVec。
            CustOpts.custOps.APPCapsVec =
                    CASUtility.parserStrToVec(tmpChildNames, CustOpts.BIAS, tmpChildNames.length());

        // 开始调整改名节点以下的各级的结构－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        tmpParentPathStr = tmpParentPathStr.substring(0, tmpParentPathStr.length() - 1);// 去掉最后的一个方括号。
        String tmpNewkey = tmpParentPathStr.concat(", ").concat(tmpNewCap); // 得到新的key。
        String tmpOldKey = tmpParentPathStr.concat(", ").concat(tmpOldCap); // 得到原有的key。
        int tmpPos = tmpOldKey.length();
        Enumeration tmpEnum = CustOpts.custOps.getKeys();
        while (tmpEnum.hasMoreElements()) {
            String tmpKey = tmpEnum.nextElement().toString(); // @NOTE:tmpKey是完整的key值(后面至少会有']')而tmpOldKey
            if (tmpKey.startsWith(tmpOldKey) && // 后面是去掉了']'的.所以tmpKey必然要长一点,不用担心subStr
                    (tmpKey.substring(tmpPos).startsWith(",") || tmpKey.substring(tmpPos).startsWith("]"))) // 会出界.另外
            { // 判断后面的字符是否","或"]",是为了确保"新建"与"新建1"漏网.
                Object tmpValue = CustOpts.custOps.getValue(tmpKey);
                CustOpts.custOps.removeKeyAndValue(tmpKey);
                if (tmpValue != null)
                    CustOpts.custOps.setKeyAndValue(tmpNewkey.concat(tmpKey.substring(tmpOldKey.length())), tmpValue);
            }
        }
        CASControl.ctrl.getFolderPane().setFolderTree(this);
    }

    // CellEdit监听结束-------------------------------------------------

    /**
     * Add property change listener.
     * 
     * @param listener
     *            property change listener to be added
     */
    public void addPropertyChangeListener(
            PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove property change listener.
     * 
     * @param listener
     *            property change listener to be removed
     */
    public void removePropertyChangeListener(
            PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * 重新保存某一级别的节点的结构关系,当新建或删除/修改了文件夹以后通常调用本方法.
     * 
     * @param prmParentPath
     *            :保存树相应路径,该节点的字节点将被重新保存.
     */
    public void resetPath(
            TreePath prmParentPath) {
        CASNode tmpParentNode = (CASNode) prmParentPath.getLastPathComponent(); // 取得父结点
        StringBuffer tmpChildNames = new StringBuffer(); // 用于存放该节点下面的子目录字符串
        for (int i = 0, tmpChildCount = tmpParentNode.getChildCount(); i < tmpChildCount; i++) {
            CASNode tmpChildNode = (CASNode) tmpParentNode.getChildAt(i); // 取出每个子节点
            tmpChildNames.append(tmpChildNode.getName()).append(CustOpts.BIAS); // 将名字进行累加.
        }
        if (tmpChildNames.length() > 1) // 累加的结果修正后存入config.
            CustOpts.custOps.setKeyAndValue(prmParentPath.toString(),
                    tmpChildNames.substring(0, tmpChildNames.length() - 1));
        else
            CustOpts.custOps.removeKeyAndValue(prmParentPath.toString());
    }

    public void putUnreads(
            TreePath prmParentPath,
            String prmRootName,
            boolean prmIsEdit) {
        ICASModel model = CASControl.ctrl.getModel();// 循环里面会用到model,特在此准备。

        String tmpChildFolderStr = (String) CustOpts.custOps.getValue(prmParentPath.toString());// 获得参数路径下的所有文件夹。
        if (tmpChildFolderStr == null)
            return; // 没有子文件夹则返回。

        // 将路径下的文件夹解析入Vector，并依次遍历每一个，进行未读邮件数设置。
        Vector tmpChildFolderVec =
                CASUtility.parserStrToVec(tmpChildFolderStr, CustOpts.BIAS, tmpChildFolderStr.length());
        for (int size = tmpChildFolderVec.size(), i = 0; i < size; i++) {
            // 先得到每个节点对应的应用的类型,和路径字串。
            String tmpFoldPathStr = (String) tmpChildFolderVec.get(i);
            int tmpNodeID = ((CASNode) prmParentPath.getLastPathComponent()).getPathID();
            int tmpAppType =
                    CustOpts.custOps.APPCapsVec.indexOf(tmpFoldPathStr) > 0 ? i : CASUtility
                            .getAppIndexByFolderID(tmpNodeID);

            // 将类型和路径字串传入Model的方法，得到对应的未读项目的数目。
            int tmpUnreadCount = model.getNewItemCount(tmpAppType, tmpNodeID);

            if (!prmIsEdit || (prmIsEdit && tmpUnreadCount > 0)) {
                hashtable.put(tmpFoldPathStr, PIMPool.pool.getKey(tmpUnreadCount));
                updateRenderer();
            }
        }

        CASNode parentNode = (CASNode) prmParentPath.getLastPathComponent();
        for (int count = parentNode.getChildCount(), j = 0; j < count; j++) {
            CASNode childNode = (CASNode) parentNode.getChildAt(j);
            String childNodeName = childNode.getName();
            putUnreads(new TreePath(childNode.getPath()), childNodeName, prmIsEdit);
        }
    }

    /** 设置节点布局 call by: 初始化和导航树更新 */
    public void updateRenderer() {
        tree.setCellRenderer(new DefaultTreeCellRenderer());
        tree.setCellRenderer(render);
    }

    /**
     * 设置是否允许鼠标按下后切换视图 call by: MoveFolderDialog.class
     * 
     * @param prmIsRemvoe
     *            是否允许
     */
    public void setEnableChangeApp(
            boolean prmIsRemvoe) {
        isRemoveListener = prmIsRemvoe;
    }

    /**
     * 在树结构上添加一个节点
     * 
     * @param prmParentNodePath
     *            父节点的路径
     * @param prmParentNodeName
     *            父节点的名称
     * @param prmNode
     *            需要添加的节点
     * @return true: 添加成功 false: 添加不成功
     */
    public boolean addNode(
            TreePath prmParentNodePath,
            String prmParentNodeName,
            CASNode prmNode) {
        if (prmParentNodeName == null)
            prmParentNodePath = rootPath;
        else if (prmParentNodePath == null)
            if ((prmParentNodePath = getNodePath(rootPath, prmParentNodeName)) == null)
                return false;
        // 至此完成了新的选中路径的设置=============================

        if (getNodePath(prmParentNodePath, prmNode.getName()) != null)
            return false; // 判断添加的节点是否合法.(即找不到参数节点对应的路径的话,表示在当前路径下尚没有与参数重名的节点.)=============================

        CASNode tmpParentNode = (CASNode) prmParentNodePath.getLastPathComponent();// 得到实例变量selePath上的末端节点(即选中的节点).
        tmpParentNode.insert(prmNode, tmpParentNode.getChildCount()); // 插入新节点.

        selePath = new TreePath(prmNode.getPath()); // 更新选中路径.
        return true;
    }

    /**
     * 从树上删除指定路径的节点
     * 
     * @param prmPath
     *            指定的节点的路径
     * @see javax.swing.tree.TreePath
     */
    public void removeNode(
            TreePath prmPath) {
        CASNode node = (CASNode) prmPath.getLastPathComponent();
        treeModel.removeNodeFromParent(node);
    }

    /**
     * Expand a non-leaf node
     * 
     * @param prmPath
     *            tree path of the node to be expanded
     * @param prmNodeName
     *            node's name
     * @param prmIsExpand
     *            true: expand this node false: collapse this node
     */
    public void setNodeExpand(
            TreePath prmPath,
            String prmNodeName,
            boolean prmIsExpand) {
        TreePath tmpNodePath = getNodePath(prmPath, prmNodeName);
        if (tmpNodePath == null)
            return;
        if (prmIsExpand)
            tree.expandPath(tmpNodePath);
        else
            tree.collapsePath(tmpNodePath);
    }

    /**
     * 该方法判断当前节点是否可以弹出菜单
     * 
     * @return true: the node can popup a menu false: the node can not popup a menu
     */
    public boolean isOperationValid() {
        return modifiable;
    }

    /**
     * 该方法设置当前节点是否可以弹出菜单
     * 
     * @param isValid
     *            true: the node can popup a menu false: the node can not popup a menu
     */
    public void setOperationValid(
            boolean isValid) {
        modifiable = isValid;
    }

    /**
     * 该方法展开树结点
     * 
     * @param prmPath
     *            :展开路径
     */
    public void expandPath(
            TreePath prmPath) {
        tree.expandPath(prmPath);
    }

    /**
     * 该方法设置树的选择方式
     * 
     * @param isSelectModel
     *            true: in select status false: not in select status
     */
    public void setSelectModel(
            boolean isSelectModel) {
        this.isSelectModel = isSelectModel;
        if (isSelectModel) {
            setOperationValid(false);
            render = new FolderNodeRender(true);
            tree.setCellRenderer(render);
        }
    }

    /**
     * 该方法取消树中的选择项
     */
    public void clearAllCheck() {
        CASNode node = root;
        while (node != null) {
            node.setSelected(false);
            node = (CASNode) node.getNextNode();
        }
        tree.treeDidChange();
    }

    /**
     * 该方法返回当前节点的路径
     * 
     * @return 当前节点的路径
     */
    public TreePath getSelectedPath() {
        return selePath;
    }

    /**
     * 该方法返回当前节点的名称
     * 
     * @return 当前节点的名称
     */
    public String getSelectNodeName() {
        if (getSelectNode() == null) {
            return null;
        } else {
            return getSelectNode().getName();
        }
    }

    /**
     * @param 根据结点ID值取得导航树上的与之匹配的路径
     *            .
     * @called by : $ResumeAction;
     * @return 返回对应应用的路径 @NOTE:如果有两个路径与之匹配(如"回收站"外一个,"回收站"内一个),则返回的是第一个.
     */
    public TreePath getTreePath(
            int prmNodeID) {
        CASNode tmpNode = getRootNode();
        while (tmpNode != null) // 遍历树结点
        {
            if (tmpNode.getPathID() == prmNodeID) {
                return new TreePath(tmpNode.getPath()); // 匹配的话返回该结点的路径.
            }
            tmpNode = (CASNode) tmpNode.getNextNode();
        }
        return null;
    }

    /**
     * Get the tree path of a node from root node.
     * 
     * @param nodeName
     *            node's name
     * @return the tree path of a node from root node
     */
    public TreePath getPathFromRoot(
            String nodeName) {
        return getNodePath(rootPath, nodeName);
    }

    /**
     * Get the tree path of a nodeCaption.从prmParentPath往下深度遍历，直到那个节点的Caption与第二个参数 相同，然后该节点得到getPath，然后new一个TreePath返回。
     * 
     * @param prmParentPath
     *            the tree path of node's parent
     * @param prmNodeCaption
     *            node's Caption
     * @return the tree path of a node
     */
    public TreePath getNodePath(
            TreePath prmParentPath,
            String prmNodeCaption) {
        if (prmNodeCaption == null)
            return null; // 如果连节点的名字都没有,显然没有办法返回树的完整路径,返回空.
        prmNodeCaption = prmNodeCaption.trim();

        CASNode tmpParentNode = (CASNode) prmParentPath.getLastPathComponent();
        int tmpLevel = tmpParentNode.getLevel() + 1;

        CASNode tmpCurrentNode = (CASNode) tmpParentNode.getNextNode();// 重新声明一个节点变量,并让它先指向父结点.(旨在通过调其getNextNode()方法,对以下的节点做遍历)
        TreePath tmpCurrentPath; // 用于暂存每一个节点对应的TreePath,并且如果和传入参数吻合,就将其返回.
        String tmpCurrentCapt; // 用于暂存每一个节点对应的节点名.用于和传入的节点名比较.
        while (tmpCurrentNode != null) {
            tmpCurrentPath = new TreePath(tmpCurrentNode.getPath());// 得到当前级别节点的TreePath数组.并以之建立一个(根枝叶)完整的TreePath实例,存入tmpNodePath对象.
            if (!prmParentPath.isDescendant(tmpCurrentPath)) // 检查，如果已经不是在参数父节点下，则停止遍历。
                break;

            if (tmpCurrentNode.getLevel() != tmpLevel) {
                tmpCurrentNode = (CASNode) tmpCurrentNode.getNextNode();
                continue;
            }

            tmpCurrentCapt = tmpCurrentNode.getUserObject().toString(); // 得到当前级的节点的显示文字.
            if (tmpCurrentCapt.equalsIgnoreCase(prmNodeCaption)) // 而且 当前节点上的显示文字和传入参数一致
                return tmpCurrentPath; // 当前路径即为跟参数匹配的路径,返回.

            tmpCurrentNode = (CASNode) tmpCurrentNode.getNextNode(); // 否则,指向下一个节点,并继续遍历(JTree的遍历顺序为,由高到低,完全遍历完第一个节点,再遍历第二个节点).
        }
        return null;
    }

    /**
     * 取得树的选择模型
     * 
     * @return 选择模型
     */
    public TreeSelectionModel getSelectioinModel() {
        return tree.getSelectionModel();
    }

    /**
     * 取得树
     */
    public JTree getTree() {
        return tree;
    }

    /**
     * Set a node.
     * 
     * @param prmPath
     *            the tree path of the node's parent
     * @param prmNodeName
     *            name of the node to be selected
     */
    public void setSelectNode(
            int prmPathID) {
        TreePath tmpPath = getTreePath(prmPathID);
        setSelectionPath(tmpPath);
        tree.scrollPathToVisible(tmpPath);
        tree.expandPath(tmpPath);
    }

    /**
     * Selects the node identified by the specified path. If any component of the path is hidden (under a collapsed
     * node), and <code>getExpandsSelectedPaths</code> is true it is exposed (made viewable).
     * 
     * @param prmPath
     *            the <code>TreePath</code> specifying the node to select
     */
    public void setSelectionPath(
            TreePath prmPath) {
        selePath = prmPath;
        tree.getSelectionModel().setSelectionPath(prmPath);
    }

    /**
     * Store the selected status.
     * 
     * @param isStore
     *            true: stroe selected status false: do not store selected status
     */
    public void storeSelectedStatus(
            boolean isStore) {
        CASNode node = root;
        while (node != null) {
            ((CASNode) node).storeSelectedStatus(isStore);
            node = (CASNode) node.getNextNode();
        }
    }

    /**
     * Duplicate this component(not equals to clone)
     * 
     * @param prmIsExpand
     *            expand the path which is selected
     * @return the duplication of this component
     */
    public CASTree duplicate(
            boolean prmIsExpand) {
        CASTree tmpFolderTree = new CASTree();
        CASTree.tree.setModel(treeModel);
        CASTree.tree.setSelectionPath(selePath);
        if (prmIsExpand) {
            CASTree.tree.expandPath(selePath);
        }
        return tmpFolderTree;
    }

    /**
     * Duplicate this component(not equals to clone)
     * 
     * @param prmIsExpand
     *            expand the path which is selected
     * @return the duplication of this component
     */
    public CASTree replicateTree(
            boolean prmIsExpand) {
        CASTree tmpFolderTree = new CASTree();
        CASTree.tree.setModel(treeModel);
        CASTree.tree.setSelectionModel(treeSelectionModel);
        CASTree.tree.setShowsRootHandles(false);
        CASTree.tree.setSelectionPath(selePath);
        if (prmIsExpand) {
            CASTree.tree.expandPath(selePath);
        }
        return tmpFolderTree;
    }

    /**
     * Edit a node.
     * 
     * @param path
     *            the tree path of the node to be editted
     */
    public void editTreeNode(
            TreePath prmPath) {
        isRenaming = true;
        CASNode node = (CASNode) prmPath.getLastPathComponent();
        Icon icon = node.getIcon();
        if (icon != null) {
            render.setLeafIcon(icon);
            render.setOpenIcon(icon);
            render.setClosedIcon(icon);
        }
        tree.startEditingAtPath(prmPath);
        JTextField tmpEditor = ((FolderNodeEditor) tree.getCellEditor()).getRealEditor();
        if (tmpEditor != null) {
            tmpEditor.selectAll();
        }
    }

    /**
     * 通过鼠标拖放移动一个节点的处理.
     * 
     * @called by: DragMoveAction;
     */
    public void dragMove() {
        TreePath tmpSrcPath = tree.getPathForRow(srcRow); // 获得源文件夹节点的路径。
        int tmpSrcPathID = ((CASNode) tmpSrcPath.getLastPathComponent()).getPathID();
        TreePath tmpSelePath = tree.getSelectionPath(); // 获得当前选中节点(也是目标节点）的路径。
        int tmpSelePathID = ((CASNode) tmpSelePath.getLastPathComponent()).getPathID();

        if (tmpSrcPath == null || tmpSelePath == null || tmpSrcPath.equals(tmpSelePath)) // 如果源路径或目标路径为null，或源和
        { // 目标相同，则将当前应用切换到目标文件夹上，如果目标为空，就切换到源上。都为null则不做事返回。
            int tmpPathID = (tmpSrcPath == null ? -1 : tmpSrcPathID); // 如果源不为null，就先取出远的值。
            if (tmpPathID < 0) // 如果目标不为null，就再以目标的值为准。
                tmpPathID = (tmpSelePath == null ? tmpPathID : tmpSelePathID);
            if (tmpPathID > 0) // 不为null，说明源和目标中至少有一个有值，则切换过去
                CASControl.ctrl.changeApplication(CASUtility.getAppIndexByFolderID(tmpPathID), tmpPathID);
        } else { // 源和目标都不为null，而且不相等的情况：将源中的内容导到目标中去。
            if (tmpSelePath.equals(rootPath)) {
                SOptionPane.showErrorDialog(MessageCons.W10645); // 对不起！系统无法识别您想要做的转换动作，请选择一个代表具体功能的文件夹或子文件夹。
                return;
            }
            CASNode srcNode = (CASNode) tmpSrcPath.getLastPathComponent();
            if (srcNode instanceof CASNode) {
                if (((CASNode) srcNode).isModifiable() == false) {
                    // SOptionPane.showMessageDialog(null,ErrorConstant.FOLDER_MOVE_ERROR);
                    return;
                }
            }

            CASNode node = (CASNode) srcNode.clone();
            String parentName = ((CASNode) tmpSelePath.getLastPathComponent()).getName();
            if (addNode(tmpSelePath, parentName, node) == true) {
                CASNode srcParentNode = (CASNode) srcNode.getParent();
                TreePath parentPath = new TreePath(srcParentNode.getPath());
                removeNode(tmpSrcPath);
                resetPath(parentPath);
                resetPath(tmpSelePath);
            } else
                // 我漏了
                SOptionPane.showErrorDialog(MessageCons.W10634);// 对不起，系统目前不支持从您选中的记录到该文件夹中记录的转换。请新建一条记录，然后通过复制后粘贴将您想要的内容导入新记录。
        }
    }

    /**
     * Copy a node with dragging mouse.
     */
    public void dragCopy() {
        TreePath srcPath = tree.getPathForRow(srcRow);
        TreePath tmpSelePath = tree.getSelectionPath();

        if (srcPath.equals(tmpSelePath)) {
            return;
        }
        if (tmpSelePath.equals(rootPath)) {
            // 对不起！系统无法识别您想要做的转换动作，请选择一个代表具体功能的文件夹或子文件夹。
            SOptionPane.showErrorDialog(MessageCons.W10645);
            return;
        }

        CASNode srcNode = (CASNode) srcPath.getLastPathComponent();
        CASNode node = (CASNode) srcNode.clone();
        String parentName = ((CASNode) tmpSelePath.getLastPathComponent()).getName();
        if (addNode(tmpSelePath, parentName, node) == true) {
            if (node instanceof CASNode) {
                ((CASNode) node).setModifiable(true);
            }
            resetPath(tmpSelePath);
        } else {
            // 我漏了
            // 对不起，系统目前不支持从您选中的记录到该文件夹中记录的转换。请新建一条记录，然后通过复制后粘贴将您想要的内容导入新记录。
            SOptionPane.showErrorDialog(MessageCons.W10634);
        }
    }

    public void autoOrganizeByDate() {
        // 自动组织日记
        int tmpDepth = // 取得日记结点的深度
                4 + root.getChildAt(0).getChildAt(0).getChildCount() + root.getChildAt(0).getChildAt(1).getChildCount()
                        + root.getChildAt(0).getChildAt(2).getChildCount();
        autoOrganizeByDate(tree.getPathForRow(tmpDepth));
        // 自动组织收件箱
        tmpDepth = // 取得收件箱结点的深度
                5 + root.getChildAt(0).getChildAt(0).getChildCount() + root.getChildAt(0).getChildAt(1).getChildCount()
                        + root.getChildAt(0).getChildAt(2).getChildCount()
                        + root.getChildAt(0).getChildAt(3).getChildCount();
        autoOrganizeByDate(tree.getPathForRow(tmpDepth));
    }

    /**
     * 该方法返回当前选择的节点
     * 
     * @return 当前选择的节点
     */
    public static CASNode getSelectNode() {
        TreePath tmpSelePath = tree.getSelectionPath();
        if (tmpSelePath == null) {
            return null;
        }
        CASNode node = ((CASNode) tmpSelePath.getLastPathComponent());
        return node;
    }

    /**
     * 根据日期返回一个格式化的整型值(例如返回20040330)
     * 
     * @param tmpDate
     * @return id
     */
    public static int getFormatDateInt(
            Date tmpDate) {
        int year = tmpDate.getYear() + 1900;
        int month = tmpDate.getMonth() + 1;
        int day = tmpDate.getDate();
        int id = year * 10000 + month * 100 + day;
        return id;
    }

    /**
     * 取得文件夹的未读数
     * 
     * @return Hashtable(文件夹信息)
     * @called by: EFolderNodeRender.java
     */
    public static Hashtable getFoldeUnreads() {
        return hashtable;
    }

    /**
     * @called by: emo.pim.pimcontrol.action.pimmenu.view.NextFolderAction;
     */
    public LastFolderAction getLastFolderAction() {
        if (lastFolderAction == null) {
            lastFolderAction = new LastFolderAction();
        }
        return lastFolderAction;
    }

    /**
     * @called by: emo.pim.pimcontrol.action.pimmenu.view.LastFolderAction;
     */
    public NextFolderAction getNextFolderAction() {
        if (nextFolderAction == null) {
            nextFolderAction = new NextFolderAction();
        }
        return nextFolderAction;
    }

    /**
     * 装饰器，用于向tree添加鼠标监听器
     * 
     * @param l
     *            向tree添加的鼠标监听器
     * @call by PIMNavigationTree。PIMNavigationTree()
     */
    protected void addTreeMouseListener(
            MouseListener l) {
        tree.addMouseListener(l);
    }

    /**
     * 装饰器，用于向tree删除鼠标监听器
     * 
     * @param l
     *            向tree删除的鼠标监听器
     * @call by PIMNavigationTree。removeMouseListener()
     */
    protected void removeTreeMouseListener(
            MouseListener l) {
        tree.removeMouseListener(l);
    }

    /**
     * @callec by: FolderNodeEditor;
     */
    static boolean isRename() {
        return isRenaming;
    }

    /**
     * @called by: FolderEditorListener;
     */
    private void setIsRename(
            boolean prmIsRename) {
        isRenaming = prmIsRename;
    }

    /**
     * 根据INFOLDER路径得到对应的数据库的表的名字
     * 
     * @param prmFolder
     *            INFOLDER路径
     * @return 数据库表的名字
     * @Note:本方法应该仅限于FolderTree包内使用，用于将从Tree上的显示的文字路径转换成表名。其它地方不应该出现有保存跟界面文字关联的信息。 因为界面文字在每次启动前都可能被改动了。所以存盘对象绝对不可以使用其信息。
     */
    static String convertFolderToTableName(
            String prmFolder) {
        // 判断当前的FOLDER是否合法
        if (prmFolder == null || prmFolder.length() < 1) {
            return null;
        }
        // 判断当前的FOLDER是否合法
        if (prmFolder == null || prmFolder.length() < 1) {
            return null;
        }
        // 得到子字符串
        StringBuffer tab = new StringBuffer();
        tab.append(prmFolder);
        tab.deleteCharAt(prmFolder.length() - 1); // 删除最后的一个']'

        String appName = null;
        StringTokenizer ken = new StringTokenizer(tab.toString(), ",");
        tab.setLength(0);
        if (ken.countTokens() < 3) {
            throw new IllegalArgumentException("Illegal FOLDER PATH :" + prmFolder);
        }

        if (ken != null && ken.hasMoreTokens()) {
            int count = 0;
            for (int size = prmFolder.length(), i = 0; i < size; i++) {
                if (prmFolder.charAt(i) == ',') {
                    count++;
                }
            }
            if (count == 2) {
                ken.nextToken();
                ken.nextToken();
                int tmpIndex = CustOpts.custOps.APPCapsVec.indexOf(ken.nextToken().trim());
                appName = (String) CustOpts.custOps.APPNameVec.get(tmpIndex);
            } else if (count >= 3) {
                ken.nextToken();
                ken.nextToken();
                ken.nextToken();
                int tmpIndex = CustOpts.custOps.APPCapsVec.indexOf(ken.nextToken().trim());
                appName = (String) CustOpts.custOps.APPNameVec.get(tmpIndex);
            }
        }
        if (appName == null) {
            return null;
        }
        // 添加应用的类型
        tab.append(appName);
        while (ken.hasMoreTokens()) {
            tab.append("_").append(ken.nextToken().trim());
        }

        String name = tab.toString();
        return name.length() > 0 ? name : null;
    }

    /*
     * 自动整理功能 系统新建一个规定格式为(XXXX年X月)子文件夹,然后将根目录中的记录移至对应日期的子文件夹中〕 设计思路：把日记根目录下的所有记录取出，遍历每一个记录，并将他们分别移动到各自对应的目录（数据库表）下。
     * called by EFolderTree
     * @param currentPath 选中结点的路径
     * @param folderTree EFolderTree
     */
    private void autoOrganizeByDate(
            TreePath prmCurrentPath) {
        CASControl.ctrl.lockModel();
        int tmpNodeID = ((CASNode) prmCurrentPath.getLastPathComponent()).getPathID();
        // 此标记用于通知数据库此时正在执行跟数据库有关并且比较费时的工作，故此时不能关闭数据库。
        int tmpCurAppType = CASUtility.getAppIndexByFolderID(tmpNodeID);
        // DefaultTreeModel tmpTreeModel =
        // (DefaultTreeModel)prmFolderTree.getTree().getModel();
        // 保存一个对tree的model的引用,因为常用,省去每次造型.
        PIMViewInfo tmpParentViewInfo = CASControl.ctrl.getViewInfo(tmpNodeID);
        Vector tmpVector = CASControl.ctrl.getModel().getAllRecord(tmpParentViewInfo, null);
        // 取出所有记录
        int id[] = new int[tmpVector.size()];
        if (tmpCurAppType == ModelCons.DIARY_APP) {
            for (int k = 0; k < tmpVector.size(); k++) {

                id[k] = ((PIMRecord) tmpVector.get(k)).getRecordID();
            }
        } else if (tmpCurAppType == ModelCons.INBOX_APP) {
            for (int k = 0; k < tmpVector.size(); k++) {
                Object tmpValue = ((PIMRecord) tmpVector.get(k)).getFieldValue(ModelDBCons.RECIEVEDATE);
                if (tmpValue != null) {
                    id[k] =
                            getFormatDateInt((Date) ((PIMRecord) tmpVector.get(k))
                                    .getFieldValue(ModelDBCons.RECIEVEDATE));
                }
            }
        }

        for (int j = 0; j < id.length; j++) {
            int year = id[j] / 10000;
            int month = id[j] / 100 - year * 100;
            String tmpNewNodeName = null;
            if (year != 0 || month != 0) // 防止出现0年0月的情况
            {
                tmpNewNodeName =
                        Integer.toString(year).concat(PaneConsts.YEAR).concat(Integer.toString(month))
                                .concat(PaneConsts.MONTH);

                // 设置新结点的名称
                String tmpPathString =
                        prmCurrentPath.toString().substring(
                                prmCurrentPath.toString().indexOf(CASUtility.NODEPATHSTART) + 1,
                                prmCurrentPath.toString().indexOf(CASUtility.NODEPATHEND));
                TreePath tmpNewNodePath = new TreePath(tmpPathString.concat(CASUtility.COMMA).concat(tmpNewNodeName));
                CASNode tmpParentNode = (CASNode) prmCurrentPath.getLastPathComponent();
                int tmpIndex = tmpParentNode.getChildCount();
                CASNode tmpNewNode = null;
                if (tmpIndex == 0) // 当没有子结点时
                {
                    tmpNewNode =
                            new CASNode(tmpNewNodeName, MainPane.getApp(CustOpts.custOps.APPNameVec.get(tmpCurAppType))
                                    .getAppIcon(true), false, FolderPopupMenu.CANNOT_NEW_FOLDER, -1);
                    // tmpTreeModel.insertNodeInto(tmpNewNode, tmpParentNode,
                    // tmpIndex);
                    // addNode(prmCurrentPath,PaneConsts.TABTITLE[tmpCurAppType], tmpNewNode); //向树中加入结点
                } else // 防止加入相同名称的结点
                {
                    int tmpKey = 1;
                    for (int i = 0; i < tmpIndex; i++) {
                        String tmpName = ((CASNode) tmpParentNode.getChildAt(i)).getName();
                        if (tmpName.equals(tmpNewNodeName)) {
                            tmpKey = 0;
                            break;
                        }
                    }
                    if (tmpKey != 0) // 判断是否重名
                    {
                        tmpNewNode =
                                new CASNode(tmpNewNodeName, MainPane.getApp(
                                        CustOpts.custOps.APPNameVec.get(tmpCurAppType)).getAppIcon(true), false,
                                        FolderPopupMenu.CANNOT_NEW_FOLDER, -1);
                        // tmpTreeModel.insertNodeInto(tmpNewNode,
                        // tmpParentNode, tmpIndex);
                        // addNode(prmCurrentPath, PaneConsts.TABTITLE[tmpCurAppType], tmpNewNode); //向树中加入结点
                    }
                }

                // 向增加的结点设置infolder字段
                PIMViewInfo tmpChildViewInfo = (PIMViewInfo) tmpParentViewInfo.clone();
                tmpChildViewInfo.setFolderID(tmpNewNode.getPathID());
                // 设置子结点的字段
                CASControl.ctrl.getModel().addViewInfo(tmpChildViewInfo);
                CustOpts.custOps.setActivePathID(tmpNewNode.getPathID());

                // 保存增加的结点
                CASControl.ctrl.getFolderPane().setFolderTree(this);
                TreePath tmpParentPath = getPathFromRoot(PaneConsts.HEAD_PAGE);
                resetPath(tmpParentPath);

                // 整理记录
                PIMRecord tmpRec = (PIMRecord) tmpVector.elementAt(j);
                tmpRec.setInfolderID(tmpNodeID);
                CASControl.ctrl.getModel().permanentlyDeleteRecord(tmpRec, false, true);
                tmpRec.setInfolderID(tmpNewNode.getPathID());
                CASControl.ctrl.getModel().insertRecord(tmpRec, false);
            }
        }
        CASControl.ctrl.unlockModel(); // 工作完成（接方法开头）
    }

    private void initAppNodes() {
        TreePath tmpPIMNodePath = getPathFromRoot(PaneConsts.HEAD_PAGE);
        for (int i = 0; i < CustOpts.custOps.APPNameVec.size(); i++) { // @NOTE:这里不能将Size提前，因为如果
            String tmpAppName = CustOpts.custOps.APPNameVec.get(i).toString(); // 装配过程中发现无效节点，size将变化。
            String tmpCapName = CustOpts.custOps.APPCapsVec.get(i).toString();
            IApplication tmpApp = MainPane.getApp(tmpAppName);
            if (tmpApp != null) {
                CASNode tmpNode =
                        new CASNode(tmpCapName, tmpApp.getAppIcon(true), true, FolderPopupMenu.CAN_NEW_FOLDER, -1);
                addNode(tmpPIMNodePath, PaneConsts.HEAD_PAGE, tmpNode);
                ((CASNode) tmpPIMNodePath.getLastPathComponent()).add(tmpNode);
                TreePath tmpTreePath = new TreePath(tmpNode.getPath());
                tmpNode.setPathID(CustOpts.custOps.getNodeID(tmpTreePath.toString().concat("_ID")));
                initTreeNode(tmpTreePath, tmpCapName, tmpApp);
            } else {
                JOptionPane.showMessageDialog(CASControl.ctrl.getMainFrame(), // 提示“系统发现插件XX出现问题，与之相关的
                        PaneConsts.PLUGINMISSINGMSG1.concat(tmpAppName) // 功能不再有效，直至该插件被重新正确添加。”
                                .concat(PaneConsts.PLUGINMISSINGMSG2));
                CustOpts.custOps.APPNameVec.remove(tmpAppName);
                CustOpts.custOps.APPCapsVec.remove(tmpCapName);
                i--; // 调整一下，使本次i值不增加1。
            }
        }
        tree.expandPath(tmpPIMNodePath); // 添加完所有节点之后将树展开到应用一级.如果不显示展开的话,遇到系统有恰好当前选中节点
    } // 是根节点,将导致整棵树不显示.本来在方法内调不是很好,只因为可以少new一次NodePath.

    /*
     * 使用递归机制，从传入点往下初始化树结构。
     * @param prmParentPath: 树的路径
     * @param prmRootName: 结点名字
     */
    private void initTreeNode(
            TreePath prmParentPath,
            String prmParentName,
            IApplication prmApp) {
        String tmpChildNodeNames = (String) CustOpts.custOps.getValue(prmParentPath.toString());// 得到Option中，以该节点名为键值的“子节点字串”。
        if (tmpChildNodeNames == null)
            return; // 如果为空,表示该节点下面没有子节点了,立即返回.

        Vector tmpNodeStrVec = CASUtility.parserStrToVec(tmpChildNodeNames, CustOpts.BIAS, tmpChildNodeNames.length());// 将子节点字串解析到Vector中。
        for (int i = 0, tmpLen = tmpNodeStrVec.size(); i < tmpLen; i++) {
            String tmpChildName = tmpNodeStrVec.get(i).toString();

            CASNode tmpNode =
                    new CASNode(tmpChildName, prmApp.getAppIcon(true), true, FolderPopupMenu.CAN_NEW_FOLDER, -1);
            addNode(prmParentPath, prmParentName, tmpNode);
        }

        CASNode parentNode = (CASNode) prmParentPath.getLastPathComponent();
        int count = parentNode.getChildCount();
        for (int j = 0; j < count; j++) {
            CASNode childNode = (CASNode) parentNode.getChildAt(j);
            String childNodeName = childNode.getName();
            TreePath tmpTreePath = new TreePath(childNode.getPath());
            childNode.setPathID(CustOpts.custOps.getNodeID(tmpTreePath.toString().concat("_ID")));
            initTreeNode(tmpTreePath, childNodeName, prmApp);
        }
    }

    /*
     * 该方法根据指定的点返回鼠标在树中的行。
     * @param pt 指定的点
     * @return 相应的行数
     */
    private int getMouseForRow(
            Point pt) {
        int rowHeight = tree.getRowBounds(0).height;
        int row = pt.y / rowHeight;
        if (pt.y % rowHeight != 0) {
            ++row;
        }
        --row;
        return row;
    }

    public CASNode getRootNode() {
        return root;
    }

    private LastFolderAction lastFolderAction;
    private NextFolderAction nextFolderAction;

    private static Hashtable hashtable = new Hashtable(); // 用来存放各个文件夹下的未读项目数，供NodeRender调用。
    private static JTree tree;
    private CASNode secondNode;
    private TreePath selePath;
    private JScrollPane scrollPane;
    private CASNode root;
    private FolderNodeRender render;
    private FolderNodeEditor editor;
    private DefaultTreeModel treeModel;
    private DefaultTreeSelectionModel treeSelectionModel;
    private TreePath rootPath;
    private PropertyChangeSupport changeSupport;
    private boolean isSelectModel;
    private DropTarget dropTarget;

    private boolean isRemoveListener = true; // 此变量只是为了控制,MoveFolderDialog中tree的选取切换问题

    private boolean modifiable = true;
    private static boolean isRenaming;

    private int buttonID;
    private boolean dragStarted;
    private int srcRow; // 用于标记拖放的起始行.
    private int row;
    private Cursor defaultCursor;
    private int LEFT_BUTTON;
    private int RIGHT_BUTTON = 1;
}
/**
 * putFolderUnreads call by: modelChangedListener,//this contr－－已被注释掉，为了缩短启动时间。
 */
// public void putActiveFolderUnreads()
// {
// IModel tmpModel = PIMControl.ctrl.getModel();
// String tmpActiveFolderPath = CustomOptions.custOps.getActiveFolderPath();
// int tmpActiveType = CustomOptions.custOps.getActiveAppType();
//
// hashtable.put(tmpActiveFolderPath,
// Pool.getInstance().getIntegerKey(tmpModel.getNewItemCount(tmpActiveType,tmpActiveFolderPath)));
// updateRenderer();
// }

// /**保存对应路径的树结构,当新建或删除/修改了文件夹以后通常调用本方法.
// * @NOTE:本方法不适合改名后调用,因为改名操作涉及某个节点往下的所有节点的key调整:即不但要保存新的结构,还要去掉旧的信息.
// * 本方法只负责将新的结构保存,但如果key值改变了的话,旧的key值Value对是不会在新的被存入的同时被覆盖的.
// * 添加节点的动作不会涉及key值的调整,仅仅是Value的调整.适合用本方法进行保存,缺点是根新添加的节点同级的其他节点也被保存了一遍,不必要.
// * @TODO:如果加的子节点参数可能会更好.但是嵌套结构将不再优雅.
// * @param prmParentPath :保存树相应路径
// */
// public void saveTree(TreePath prmParentPath)
// {
// PIMNode tmpParentNode = (PIMNode)prmParentPath.getLastPathComponent(); //取得父结点
// StringBuffer tmpNodeNames = new StringBuffer(); //用于存放该节点下面的子目录字符串
// int tmpChildCount = tmpParentNode.getChildCount(); //参数节点下的子节点数目
// TreePath[] tmpChildPaths = new TreePath[tmpChildCount]; //存放参数节点的各子节点的路径
// for (int i = 0; i < tmpChildCount; i++)
// {
// PIMNode tmpChildNode = (PIMNode)tmpParentNode.getChildAt(i); //取出每个子节点
// tmpChildPaths[i] = new TreePath(tmpChildNode.getPath()); //将路径存入数组
// tmpNodeNames.append(tmpChildNode.getName()).append('/'); //将名字进行累加.
// }
//
// if (tmpNodeNames.length() > 1) //累加的结果修正后存入config.
// CustOpts.custOps.setKeyAndValue(prmParentPath.toString(), tmpNodeNames.substring(0, tmpNodeNames.length() - 1));
//
// for (int i = 0, length = tmpChildPaths.length; i < length; i++) //每个子节点进行嵌套保存.
// saveTree(tmpChildPaths[i]);
// }
