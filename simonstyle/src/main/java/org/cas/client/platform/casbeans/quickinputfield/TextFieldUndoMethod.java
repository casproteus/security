package org.cas.client.platform.casbeans.quickinputfield;

import java.util.LinkedList;

import org.cas.client.platform.casutil.CASUtility;

/**
 * @TODO:此类为临时类,整合以后将被删除
 */

class TextFieldUndoMethod {
    // 设置undo、redo操作次数最多为100次，注最后一个为“”字符对象，用于undo到最后一步时撤销为空
    final int STACK_MAX_LEN = 100 + 1;
    static final int ALL_UNDO_MODEL = 0;
    static final int ONE_UNDO_MODEL = 1;

    /** Creates a new instance of TextFieldUndoMethed */
    TextFieldUndoMethod(int prmUndoModel) {
        this();
        undoModel = (prmUndoModel == ALL_UNDO_MODEL || prmUndoModel == ONE_UNDO_MODEL) ? prmUndoModel : ONE_UNDO_MODEL; // 为用户定义模式
    }

    /** Creates a new instance of TextFieldUndoMethed */
    TextFieldUndoMethod() {
        undoModel = ONE_UNDO_MODEL; // 初始化默认撤销模式
        initUndoStack();
    }

    /** 初始化记录列表 */
    private void initUndoStack() {
        itemUndoStack = new LinkedList();
        itemUndoStack.clear();
        // 默认的在undo到最后一步时为空，让文本显示为空
        itemUndoStack.addFirst(CASUtility.EMPTYSTR);
    }

    /**
     * 取到执行undo操作后得到的上一步的结果字符串
     */
    String getUndoItem() {
        String tmpUndoItem;
        if (!isCanUndo()) {
            tmpUndoItem = null;
            isUndo = false;
        } else {
            tmpUndoItem = String.valueOf(itemUndoStack.get(++curPosIndex));
            isUndo = true;
        }
        return tmpUndoItem;
    }

    /**
     * 取到执行redo操作后得到的下一步的结果字符串
     * 
     * @return String 返回上一步操作时，文本框中的字符串
     */
    String getRedoItem() {
        String tmpRedoItem;
        if (!isCanRedo()) {
            tmpRedoItem = null;
            isRedo = false;
        } else {
            tmpRedoItem = String.valueOf(itemUndoStack.get(--curPosIndex));
            isRedo = true;
        }
        return tmpRedoItem;
    }

    /**
     * 是否可以撤销操作
     * 
     * @return boolean 是否可以实行年点操作
     */
    boolean isCanUndo() {
        return (itemUndoStack == null || itemUndoStack.size() <= 1 || (curPosIndex + 1 > itemUndoStack.size() - 1)) ? false
                : true;
    }

    /**
     * 是否可以重复操作
     * 
     * @return boolean 是否可以实行redo操作
     */
    boolean isCanRedo() {
        return (itemUndoStack == null || itemUndoStack.size() <= 1 || curPosIndex < 1) ? false : true;
    }

    /**
     * 取到当前的指针值
     * 
     * @return int 当前的指针值
     */
    protected int getCurPos() {
        return curPosIndex;
    }

    /**
     * 设置当前的指针，不推荐使用
     * 
     * @param prmPos
     *            当前指针
     * @return boolean 如果指针没有越界为true，否则为false
     */
    protected boolean setCurPos(
            int prmPos) {
        if ((itemUndoStack == null || itemUndoStack.size() < 1)
                || (prmPos + 1 > itemUndoStack.size() - 1 || prmPos < 0)) {
            return false;
        } else {
            curPosIndex = prmPos;
        }
        return true;
    }

    /**
     * 删除所有的可以undo和redo的操作
     */
    void removeAllElements() {
        initUndoStack();
        // 赋值指针为0
        curPosIndex = 0;
    }

    /**
     * 命令解析器
     * 
     * @param prmItemStr
     *            添加的字符串
     */
    void addEditItem(
            String prmItemStr) {
        if (undoModel == ALL_UNDO_MODEL) // ALL_UNDO_MODEL
        {
            addAllUndoElement(prmItemStr);
        } else // ONE_UNDO_MODEL
        {
            addOneUndoElement(prmItemStr);
        }
    }

    /**
     * 添加文本插入,删除操作后的结果字符串
     * 
     * @param prmItemStr
     *            添加的字符串
     */
    void addOneUndoElement(
            String prmItemStr) {
        String tmpItemStr = prmItemStr;
        // 如果第一个值和添加的值相同则返回
        // @note 有问题
        if ((itemUndoStack == null || itemUndoStack.size() < 1)
                || (itemUndoStack.getFirst().equals(tmpItemStr) && curPosIndex < 1))// 添加的元素上一次添加元素相同则不添加
        {
            return;
        }
        // 删除元素
        removeElements();
        // 如果操作数操作最大值STACK_MAX_LEN时，删除最后一个元素
        while (isStackFull()) {
            removeLastElement();
        }
        curPosIndex = 0; // 赋值当前指针值为 0
        tmpItemStr = (tmpItemStr == null || tmpItemStr.length() < 1) ? CASUtility.EMPTYSTR : tmpItemStr;
        // 添加新元素
        itemUndoStack.addFirst(tmpItemStr);
    }

    /**
     * 所有的操作都记录的情况
     * 
     * @param prmItemStr
     *            添加的字符串
     */
    private void addAllUndoElement(
            String prmItemStr) {
        String tmpItemStr = prmItemStr;
        // 如果第一个值和添加的值相同则返回
        // @note 有问题
        if ((itemUndoStack == null || itemUndoStack.size() < 1)
                || (itemUndoStack.getFirst().equals(tmpItemStr) && curPosIndex < 1))// 添加的元素上一次添加元素相同则不添加
        {
            return;
        }
        // 如果操作数操作最大值STACK_MAX_LEN时，删除最后一个元素
        while (isStackFull()) {
            removeLastElement();
        }
        tmpItemStr = (tmpItemStr == null || tmpItemStr.length() < 1) ? CASUtility.EMPTYSTR : tmpItemStr;
        // 添加新元素
        itemUndoStack.addFirst(tmpItemStr);
        setCurPosIndex();
    }

    /**
     * 设置是否成功
     * 
     * @return boolean 设置是否成功
     */
    private boolean setCurPosIndex() {
        curPosIndex = (curPosIndex < 1) ? 0 : ++curPosIndex;
        return curPosIndex < itemUndoStack.size();
    }

    /**
     * 当列表元素个数大于最大值 STACK_MAX_LEN 时，删除末尾元素
     * 
     * @return boolean 大于 STACK_MAX_LEN 为 true，否则为 false
     */
    private boolean isStackFull() {
        return itemUndoStack.size() >= STACK_MAX_LEN;
    }

    /**
     * 添加一个元素的时候，如果当前的指针值不为0时，删除指针前的所有元素
     */
    private void removeElements() {
        // 如果元素指针为最后一个值，清空所有元素
        int tmpStackSize = itemUndoStack.size();
        if (curPosIndex == tmpStackSize - 1 && tmpStackSize > 1) {
            initUndoStack();
        } else if (curPosIndex > 0 && curPosIndex < tmpStackSize - 1) {
            // 移去0 到 curPosIndex - 1的所有元素
            removeItemRange(0, curPosIndex);
        }
    }

    /**
     * 删除itemUndoStack中索引值为[prmBegIndex,prmEndIndex]之间的值
     * 
     * @param prmBegIndex
     *            开始值 prmEndIndex 结束值
     */
    private void removeItemRange(
            int prmBegIndex,
            int prmEndIndex) {
        int tmpRemoveCount = ((prmEndIndex - prmBegIndex) >= 0 && prmBegIndex >= 0) ? (prmEndIndex - prmBegIndex) : 0;
        for (int i = prmBegIndex; i < prmEndIndex; i++, tmpRemoveCount--) {
            // 判断删除越界
            if (itemUndoStack.size() - 1 > tmpRemoveCount) {
                itemUndoStack.remove(prmBegIndex);
            }
        }
    }

    /*
     * 当列表中的元素达到最大容量的时候删除最后的一个元素
     */
    private void removeLastElement() {
        // 判断最后的一个元素是否为空
        if (itemUndoStack.getLast() != null && itemUndoStack.size() >= 2) {
            // 删除的时候要注意，删除的不时最后一个“”字符，而是倒算第二个非“”的元素
            itemUndoStack.remove(itemUndoStack.size() - 2);
        }
    }

    /**
     * 是否执行了undo操作
     * 
     * @return boolean 是否已经undo
     */
    boolean isUndoed() {
        return isUndo;
    }

    /**
     * 是否执行了redo操作
     * 
     * @return boolean 是否已经redo
     */
    boolean isRedoed() {
        return isRedo;
    }

    /**
     * 设置是否redo标记
     */
    void setRedo(
            boolean prmIsRedo) {
        isRedo = prmIsRedo;
    }

    /**
     * 设置是否undo标记
     */
    void setUndo(
            boolean prmIsUndo) {
        isUndo = prmIsUndo;
    }

    // 列表的一个指针，当curPosIndex为0时，可以实行undo操作；为列表的最大值时可以redo操作，大于0且小于最大值时可以undo和redo
    private int curPosIndex;
    private int undoModel;
    // 是否已经undo、redo
    private boolean isUndo;
    private boolean isRedo;
    //
    private LinkedList itemUndoStack;
}
