package org.cas.client.platform.cascontrol.navigation;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 该类维护对树节点的基本操作。
 */

public class CASNode extends DefaultMutableTreeNode implements Cloneable {
    /**
     * 构造器
     * 
     * @param prmNodeName
     *            节点的名称
     * @param prmIcon
     *            节点的图标
     * @param prmModifiable
     *            true:可以修改 false: 不可以修改
     * @param prmCanNewFolder
     *            能否创建新结点
     * @param prmPathInt
     *            : 每个节点(也即路径)对应一个Int值,该Int值作为记录在数据库中的路径属性,随每条记录 记入数据库中,方便为检索记录时建立索引.并为分批从数据库中取数据创造可能.
     */
    public CASNode(String prmNodeName, Icon prmIcon, boolean prmModifiable, int prmCanNewFolder, int prmPathInt) {
        super(prmNodeName);
        nodeName = prmNodeName;
        icon = prmIcon;
        modifiable = prmModifiable;
        canNewFolder = prmCanNewFolder;
        folderID = prmPathInt;
    }

    /**
     * 当前节点的克隆
     * 
     * @return 当前节点的克隆
     * @see javax.swing.tree.DefaultMutableTreeNode#clone
     */
    public Object clone() {
        CASNode node = (CASNode) super.clone();
        node.nodeName = nodeName;
        node.icon = icon;
        node.modifiable = modifiable;
        node.canNewFolder = canNewFolder;
        if (isLeaf())
            return node;

        return cloneChildNode(node);
    }

    /**
     * 该方法返回节点的名称
     * 
     * @return 节点的名称
     */
    public String getName() {
        return nodeName;
    }

    /**
     * 该方法设置节点的新的名称
     * 
     * @param name
     *            节点的新的名称
     */
    public void setName(
            String prmName) {
        nodeName = prmName;
        setUserObject(prmName);
    }

    /**
     * 该方法返回节点的图标
     * 
     * @return 节点的图标
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * 该方法为节点设置新的图标
     * 
     * @param icon
     *            节点的新的图标
     */
    public void setIcon(
            Icon prmIcon) {
        icon = prmIcon;
    }

    /**
     * 该方法返回该节点是否可以被修改。
     * 
     * @return true: 可以修改。 false: 不可以修改。
     */
    public boolean isModifiable() {
        return modifiable;
    }

    /**
     * 该方法设置当前节点是否可以修改。
     * 
     * @param modifiable
     *            true: 可以修改。 false: 不可以修改。
     */
    public void setModifiable(
            boolean modifiable) {
        this.modifiable = modifiable;
    }

    /**
     * 该方法设置结点是否 能够被新建子结点
     * 
     * @param prmCanNew
     *            结点名
     */
    public void setNodeCanNew(
            int prmCanNew) {
        canNewFolder = prmCanNew;
    }

    /**
     * 该方法返回该节点是否可以选择
     * 
     * @return true: 可以选择 false: 不可以选择
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * 该方法设置当前节点可不可以被选择
     * 
     * @param prmIsSelected
     *            true: 可以选择 false: 不可以选择
     */
    public void setSelected(
            boolean prmIsSelected) {
        isSelected = isSelected ? false : prmIsSelected;
    }

    /**
     * 该方法设置节点状态的存储状态
     * 
     * @param prmIsStore
     *            true: 存储节点的状态 false: 不存储节点的状态
     */
    public void storeSelectedStatus(
            boolean prmIsStore) {
        if (prmIsStore)
            oldSelected = isSelected;
        else
            isSelected = oldSelected;
    }

    /**
     * 该方法克隆节点的子节点
     * 
     * @param node
     *            需要克隆的节点
     * @return 科隆后的结点
     */
    private CASNode cloneChildNode(
            CASNode node) {
        CASNode srcChild, desChild;
        int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            srcChild = (CASNode) getChildAt(i);
            desChild = (CASNode) srcChild.clone();
            node.add(desChild);
        }
        return node;
    }

    public int getPathID() {
        return folderID;
    }

    public void setPathID(
            int prmPathID) {
        folderID = prmPathID;
    }

    private String nodeName;
    private Icon icon;
    private boolean modifiable;
    private boolean isSelected;
    private boolean oldSelected;
    private int canNewFolder;
    private int folderID;
}
