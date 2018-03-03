package org.cas.client.platform.cascontrol.navigation;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Image;

import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimview.PicturePane;
import org.cas.client.resource.international.PaneConsts;

/**
 * 文件夹面板
 */
public class CASNavigationPane extends PicturePane {
    /**
     * 构造器
     * 
     * @see java.awt.Image#
     * @called by: emo.pim.PIMMainFrame;
     * @param prmTree
     *            导航树
     * @param prmImage
     *            <code>Image</code>传入的图片
     */
    public CASNavigationPane(Image prmImage) {
        super(prmImage);
        // add(new JLabel(PaneConstants.TREE_FOLDER_LIST));//显示在导航面板头部的“导航面板”字样。
        beautifyLable = new PicturePane(PIMPool.pool.getImage(PaneConsts.IAMGE_PATH.concat("NavigationBK.jpg")));

        setFolderTree(CASTree.getInstance());
    }

    /**
     * 设置文件夹树，本法经常被调到，如改变文件夹Caption时。
     * 
     * @param tree
     *            <code>EFolderTree</code>
     * @see CASTree
     * @called by: emo.pim.PIMControl;
     */
    public void setFolderTree(
            CASTree prmFolderTree) {
        if (folderTree != null) {
            remove(beautifyLable);
            remove(folderTree);
        }

        folderTree = prmFolderTree;

        add(folderTree);
        add(beautifyLable);// 保持beautifyLable在folderTree之后被增加，好让它显示在后层。

        folderTree.setOperationValid(true);
    }

    /**
     * 取得导航树
     * 
     * @return 导航树
     */
    public CASTree getFolderTree() {
        return folderTree;
    }

    /** 面板布局 */
    @Override
    public void layoutContainer(
            Container prmContainer) {
        // canvas.setBounds(0, 0, getWidth(), PaneConstant.TREE_TITLE_HEIGHT);
        folderTree.setBounds(0, 0, getWidth(), getHeight());
        beautifyLable.setBounds(0, getHeight() / 2 - 40, getWidth(), 80);// 该面板作为装饰用的,所以必须最后add,以使其显示在最下面一层.
    }

    public static int getPathIDByDlg(
            Dialog prmDialog) {
        FolderSelectDialog folderDialog = new FolderSelectDialog(prmDialog);
        folderDialog.setVisible(true);
        int tmpID = folderDialog.getPathID();
        folderDialog.dispose();
        return tmpID; // 得到是按确定按钮还是按取消按钮退出的
    }

    private PicturePane beautifyLable;
    private CASTree folderTree;
}

// /** 绘制
// * @param g <code>Graphics</code>
// * @see java.awt.Graphics
// */
// public void paintComponent(Graphics g)
// {
// int width = getWidth();
// g.setColor(Color.white);
// g.fillRect(0, 0, width - 2, PaneConstant.TREE_TITLE_HEIGHT);
// int w = getWidth();
// int h = PaneConstant.TREE_TITLE_HEIGHT;
// if (w < 0 || h < 0)
// {
// return;
// }
// ((Graphics2D)g).setPaint(new GradientPaint(w, h, color1, w, 0, color2));
// g.fillRect(1, 1, width - 3, h);
// g.setColor(Color.lightGray);
// g.fillRect(1, 1, width - 2, PaneConstant.TREE_TITLE_HEIGHT);
// g.setColor(Color.gray);
// g.drawLine(1, PaneConstant.TREE_TITLE_HEIGHT, width - 2, PaneConstant.TREE_TITLE_HEIGHT);
// g.drawLine(width - 2, 0, width - 2, PaneConstant.TREE_TITLE_HEIGHT - 1);
// g.setColor(Color.black);
// }
