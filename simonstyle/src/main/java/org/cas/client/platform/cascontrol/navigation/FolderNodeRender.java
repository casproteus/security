package org.cas.client.platform.cascontrol.navigation;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.cas.client.platform.cascustomize.CustOpts;

/**
 * 决定文件夹列表的节点的类型
 */
class FolderNodeRender extends CASTreeCellRenderer {
    /**
     * 构建器
     * 
     * @param 文件夹是否被选中
     */
    FolderNodeRender(boolean isSelect) {
        this.isSelect = isSelect;
        setBackgroundNonSelectionColor(CustOpts.custOps.getNavigationPaneBG());// 可能因为父类绘制方法中忽略了Opaque的情况，
        // 所以设置透明是没有效果的。所以只好给绘制器设置跟
        if (isSelect) // 背景一致的BackGround.
        {
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            panel.add(checkBox); // 加入选择框
            panel.add(this);
        }
        setOpaque(false);
    }

    /**
     * 用图标重新绘制节点。
     * 
     * @param tree
     *            树
     * @param value
     *            值
     * @param sel
     *            是否选中
     * @param expanded
     *            是否被展开
     * @param leaf
     *            是否是叶子结点
     * @param row
     *            行数
     * @param hasFocus
     *            是否有焦点
     * @return 得到处理后的节点绘制器
     */
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        CASNode node = (CASNode) value;
        Icon icon = node.getIcon();

        String tmpStr = getText(); // 处理绘制器
        // TODO: 要从我们的树中去得到新邮件数
        Hashtable tmpAppendSet = CASTree.getFoldeUnreads();

        if (row > 0 && tmpAppendSet != null) {
            TreePath path = tree.getPathForRow(row);
            if (path != null) {
                Object tmp = tmpAppendSet.get(tree.getPathForRow(row).toString());
                if (tmp != null && !"0".equals(tmp.toString())) {
                    tmpStr = tmpStr + " (" + tmp.toString() + ')';
                    setText(tmpStr);
                }
            }
        }
        if (icon != null) {
            setIcon(icon); // 设置图标
        }
        if (isSelect) {
            checkBox.setVisible(true);
            checkBox.setSelected(node.isSelected());
            return panel;
        }
        setOpaque(false);
        return this;
    }

    /**
     * Paints the value. The background is filled based on selected.
     * 
     * @NTOE：这段代码经常出线错误，比如绘制不全，绘制重叠等。本想改在状态栏上显示未读邮件数目，但是由于邮件可能被邮件规则自动搬运到各个 文件夹下面，状态栏不如文件夹后面提示数字来得一目了然，所以，还得继续坚持用这种方法。
     * @param g
     *            图形设备
     */
    public void paint(
            Graphics g) {
        super.paint(g);

        String showText = getText();

        int beginIndex = showText.indexOf('('); // 这是邮件数的字符串
        if (beginIndex >= 0) {
            Color oldColor = g.getColor();

            g.setColor(Color.white); // 要绘制右边的背景色

            String tmpRightText = showText.substring(beginIndex);// 得到文本的宽度
            Rectangle2D rightTextRect = getFontMetrics(g.getFont()).getStringBounds(tmpRightText, g);
            int textWidth = (int) rightTextRect.getWidth();
            // g.fillRect(getWidth() - textWidth - 3, 0, textWidth + 3, getHeight());
            g.setColor(Color.blue);

            // int leftTextY = getHeight() - (getHeight() - (int)rightTextRect.getHeight())/ 2;
            // @TODO:要直接再绘制一下后面的字符串,不再计算Y定位,不然计算量太大,而且要产生大量对象,暂时如此
            g.drawString(tmpRightText, getWidth() - textWidth - 3, getHeight() - 5);
            g.setColor(oldColor);
        }
    }

    private boolean isSelect; // 该节点是否可以被选择
    private JCheckBox checkBox = new JCheckBox();
    private JPanel panel = new JPanel();
}
