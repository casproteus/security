package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.resource.international.DlgConst;



class CopyFolderDialog extends JDialog implements ActionListener
{
    /** Creates new CopyFolderDialog
     * @param parent 调用此dialog的frame, 用于构造父类 */
    public CopyFolderDialog(Frame parent)
    {
        super(parent, true);
        setTitle(DlgConst.COPY_FOLDER_DIALOG);		 //设置标题

        //为null布局初始化坐标
        int x = 0;
        int y = 0;

        //设置对话框顶端ELabel
        JLabel label = new JLabel(DlgConst.COPY_FOLDER_LABEL);
        label.setDisplayedMnemonic('C');
        label.setBounds(x,y, label.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(label);

        y += CustOpts.LBL_HEIGHT;

        //构造treePane中的tree,并显示
        treePane = new JPanel();
        treePane.setBounds( x, y, maxWidth, treeHeight);
        tree = CASControl.ctrl.getFolderTree().duplicate(true);
        srcTree = CASControl.ctrl.getFolderTree();
        tree.setBounds(0, 0, maxWidth, treeHeight);
        treePane.add(tree);
        getContentPane().add(treePane);
        tree.setOperationValid(false);
        label.setLabelFor(tree);

        y += treeHeight + CustOpts.HOR_GAP;
        y += CustOpts.HOR_GAP;
        x = (maxWidth - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP) / 2 + 10;

        //设置底部的2个按钮ok,cancel
        x += CustOpts.BTN_WIDTH + CustOpts.HOR_GAP;
        ok = new JButton(DlgConst.OK);
        ok.setBounds(x, y, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);
        getRootPane().setDefaultButton(ok);
        
        x += CustOpts.BTN_WIDTH + CustOpts.HOR_GAP;
        cancel = new JButton(DlgConst.CANCEL);
        cancel.setBounds(x, y, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);
        y += CustOpts.BTN_HEIGHT + CustOpts.HOR_GAP;

        maxHeight = y;
        setBounds((CustOpts.SCRWIDTH - maxWidth)/2, (CustOpts.SCRHEIGHT - maxHeight)/2, maxWidth, maxHeight);	//对话框的默认尺寸。
		
        //okButton的事件响应, 改变了树结构
        ok.addActionListener(this);
        cancel.addActionListener(this);
    }

    /** Invoked when an action occurs.
     * @param e 动作事件
     */
    public void actionPerformed(ActionEvent e)
    {
		if (e.getSource() == ok)
        {
			CASNode node = (CASNode)CASTree.getSelectNode().clone();
            String parentName = tree.getSelectNodeName();
            if (srcTree.addNode(tree.getSelectedPath(), parentName, node) == true)
            {
                CASControl.ctrl.getFolderPane().setFolderTree(srcTree);
                if (node instanceof CASNode)
                {
                    ((CASNode)node).setModifiable(true);
                }
                dispose();
            }
            else
            {
                //您为当前文件夹输入的名字不能被系统接受，可能的原因有：
                SOptionPane.showErrorDialog(MessageCons.W10635);
            }
        }
    }
    private int maxWidth = 260;
    private int maxHeight;
    private int treeHeight = 200;

    //显示的组件
    private JButton ok,cancel;
    private JPanel treePane;
    private CASTree tree, srcTree;
}
