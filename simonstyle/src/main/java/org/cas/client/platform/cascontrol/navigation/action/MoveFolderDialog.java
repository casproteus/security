package org.cas.client.platform.cascontrol.navigation.action;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.resource.international.DlgConst;


public class MoveFolderDialog extends JDialog implements ActionListener
{
    /** Creates new MoveFolderDialog
     * @param parent 调用此dialog的frame, 用于构造父类 */
    public MoveFolderDialog(Frame parent)
    {
        super(parent, true);
        setTitle(DlgConst.MOVE_FOLDER_DIALOG);//设置标题

        int x = 0;//初始化null布局的坐标
        int y = 0;

        JLabel label = new JLabel(DlgConst.MOVE_FOLDER_LABEL); //设置label
        label.setDisplayedMnemonic('M');
        label.setBounds(x, y, label.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(label);

        y += CustOpts.LBL_HEIGHT;

        treePane = new JPanel();  //获得tree, 并显示
        tree = CASControl.ctrl.getFolderTree().duplicate(true);    //参数true表示树是允许打开的.
        srcTree = CASControl.ctrl.getFolderTree();
        tree.setBounds(0, 0, maxWidth, treeHeight);
        tree.setEnableChangeApp(false);
        treePane.add(tree);
        getContentPane().add(treePane);
        tree.setOperationValid(false);
        label.setLabelFor(tree);

        y += treeHeight + CustOpts.HOR_GAP;
        y += CustOpts.HOR_GAP;
        //x = (maxWidth - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP) / 2 + 10;
		x = (maxWidth - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP);

        //x += CustOpts.BTN_WIDTH + CustOpts.HOR_GAP;  //设置底部的3个按钮
        ok = new JButton(DlgConst.OK);
        ok.setBounds(x, y, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);
        getRootPane().setDefaultButton(ok);
        x += CustOpts.BTN_WIDTH + CustOpts.HOR_GAP;
        cancel = new JButton(DlgConst.CANCEL);
        cancel.setBounds(x, y, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);
        y += CustOpts.BTN_HEIGHT + CustOpts.HOR_GAP;

        maxHeight = y;				 //获得dialog的实际高度
        setBounds((CustOpts.SCRWIDTH - maxWidth)/2, (CustOpts.SCRHEIGHT - maxHeight)/2, maxWidth, maxHeight);	//对话框的默认尺寸。
		
        ok.addActionListener(this);	//ok按钮的事件响应, 移动节点
        cancel.addActionListener(this);
    }

    /** 得到选择的路径
     * @return 选择的路径
     */    
    public int getSelectedPathID()
    {
        return selectedPathID;
    }

    /** 得到应用类型
     * @return 当前应用类型
     */    
    public int getAppIndex()
    {
        return appIndex;
    }

    /** Invoked when this dialog closing.
     * This method should be overrided if user has extra execution
     * while closing the dialog.
     */
    protected void extraAction()
    {
        //tree.setEnableChangeApp(true);
    	dispose();
    }

    /** Invoked when an action occurs.
     * @param e 动作事件
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() ==ok)
        {
            TreePath tmpPath = tree.getSelectedPath();
            srcTree.setSelectionPath(tmpPath);
            selectedPathID = ((CASNode)tmpPath.getLastPathComponent()).getPathID();
            int tmpPathCount = tmpPath.getPathCount();
            appIndex = CustOpts.custOps.APPCapsVec.indexOf(tree.getSelectNodeName());
            //以下判断只是为了,在选取了收件箱下面的文件夹猴的特殊处理
            //TODO:以后需要根据另一种方式判断文件夹的位置
            if (tmpPathCount > 3)
            {
                appIndex = ModelCons.INBOX_APP;
            }
            extraAction();
        }
    }
    
    private int maxWidth = 260;
    private int maxHeight;
    private int treeHeight = 200;
    private int appIndex = -1;
    private int selectedPathID;

    private JButton ok, cancel; //显示的组件
    private JPanel treePane;
    private CASTree tree, srcTree;
}

