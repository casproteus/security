package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.EventListenerList;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;

/**
 * 这个类是对文件夹切换事件进行管理，实现文件夹的前进后退功能。 参考jdk的undo/redo。
 */

public class CASFolderManager {
    private static int LIMIT = 100;

    /** Creates a new instance of PIMFolderManager */
    public CASFolderManager() {
        this(LIMIT);
    }

    /**
     * Creates a new instance of PIMFolderManager
     * 
     * @prmLimit 前进后退的层数限制，prmLimit < 0时取缺省值
     */
    public CASFolderManager(int prmLimit) {
        // 次数限制<0时取缺省值
        // 因为当前文件夹的ID占用一个位置，
        // 实现n次前进后退需要n+1个位置存放文件夹ID
        if (prmLimit >= 0) {
            limit = prmLimit + 1;
        } else {
            limit = LIMIT + 1;
        }
        // 初始化Undo/Redo的次数
        folder = new int[limit];
        // 初始化文件夹载栈中的位置
        firstFolder = lastFolder = currentFolder = 0;
        folder[currentFolder] = CustOpts.custOps.getActiveAppType();
    }

    /**
     * 这个方法将一个文件夹切换事件记录加入到堆栈
     * 
     * @param folderIndex
     *            加入堆栈的文件夹切换事件记录，新文件夹ID
     * @Called by: PIMControl
     */
    public synchronized void addFolder(
            int prmFolderIndex) {
        if (!isUndoOrRedoStatus()) // 若文件夹切换是由undo/redo引起的，则不记录此次切换
        {
            // 准备存放新文件夹ID的位置
            currentFolder = adjust(currentFolder + 1);
            // 若记录的事件超过limit，清除最前面的记录，
            // 事件记录的起始位置向前移动一位
            if (currentFolder == firstFolder) {
                firstFolder = adjust(firstFolder + 1);
            }
            // 清除redo信息
            lastFolder = currentFolder;
            // 记录新文件夹的ID
            folder[currentFolder] = prmFolderIndex;
        }
    }

    /**
     * 实现文件夹后退功能
     * 
     * @return 能够后退返回true 不能后退返回false
     * @Called by: LastFolderAction
     */
    public synchronized boolean backward() {
        if (hasLastFolder()) // 是否可以后退，不能返回false
        {
            // 当前文件夹的指针后推一位，指向前一文件夹
            currentFolder = adjust(currentFolder - 1);
            // 更新文件夹视图
            updateFolder();
            return true;
        }
        return false;
    }

    /**
     * 实现文件夹前进功能
     * 
     * @return 能够后退返回true 不能后退返回false
     * @Called by: NextFolderAction
     */
    public synchronized boolean forward() {
        if (hasNextFolder()) // 是否可以前进，不能返回false
        {
            // 当前文件夹的指针前进一位，指向后一文件夹
            currentFolder = adjust(currentFolder + 1);
            // 更新文件夹视图
            updateFolder();
            return true;
        }
        return false;
    }

    /**
     * 更新当前文件夹的视图
     */
    private synchronized void updateFolder() {
        // 设置标志，防止文件夹的前进后退被作为文件夹切换事件记录在UndoManager中
        setUndoOrRedoStatus(true);
        // 切换视图
        // PIMControl.ctrl.changeApplication(folder[currentFolder]);
        fireFolderChangeActionPerformed(new ActionEvent(PIMPool.pool.getKey(folder[currentFolder]), 0, null));
        // 恢复正常状态
        setUndoOrRedoStatus(false);
    }

    /**
     * 是否可以后退
     * 
     * @return 可以后退返回true 不可后退返回false
     * @Called by: LastFolderAction
     */
    public synchronized boolean hasLastFolder() {
        return currentFolder != firstFolder;
    }

    /**
     * 是否可以前进
     * 
     * @return 可以前进返回true 不可前进返回false
     * @Called by: NextFolderAction
     */
    public synchronized boolean hasNextFolder() {
        return currentFolder != lastFolder;
    }

    /**
     * 打印当前堆栈的状态，堆栈中各个指针的位置 /* private void printLocation(String msg) { A.s(msg); A.s(firstFolder); A.s(currentFolder);
     * A.s(lastFolder); A.s(folder[currentFolder]); }
     */

    // /**
    // * 检查所有的数组指针是否越界，如果越界做相应调整。
    // * java风格。
    // */
    // private void adjustPoint()
    // {
    // firstFolder = adjust(firstFolder);
    // currentFolder = adjust(currentFolder);
    // lastFolder = adjust(lastFolder);
    // }

    /**
     * 检查指定的数值作为数组指针是否越界，如果越界做相应调整。 因为采用int值记录有关信息，所以只能写出C风格的代码。
     * 
     * @param point
     *            被检查的数组指针的值
     * @param 调整之后的值
     */
    private int adjust(
            int prmPoint) {
        // 若point指向的数组位置超过folder[]的限制，移动到数组头部
        if (prmPoint >= limit) {
            prmPoint -= limit;
        }
        // 若point指向的数组位置超过folder[]的限制，移动到数组尾部
        else if (prmPoint < 0) {
            prmPoint += limit;
        }
        return prmPoint;
    }

    private void setUndoOrRedoStatus(
            boolean prmUndoOrRedo) {
        undoOrRedoStatus = prmUndoOrRedo;
    }

    private boolean isUndoOrRedoStatus() {
        return undoOrRedoStatus;
    }

    /**
     * Registers ActionListener to receive events.
     * 
     * @param listener
     *            The listener to register.
     * @Called by: PIMControl
     */
    public synchronized void addFolderChangeListener(
            ActionListener prmListener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(ActionListener.class, prmListener);
    }

    /**
     * Removes ActionListener from the list of listeners.
     * 
     * @param listener
     *            The listener to remove.
     */
    public synchronized void removeFolderChangeListener(
            ActionListener prmListener) {
        listenerList.remove(ActionListener.class, prmListener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param event
     *            The event to be fired
     */
    private void fireFolderChangeActionPerformed(
            ActionEvent prmEvent) {
        if (listenerList == null) {
            return;
        }
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(prmEvent);
            }
        }
    }

    // 判断当前事件是否由undo/redo触发的
    private boolean undoOrRedoStatus;

    // 缺省的limit
    private final int limit;
    // 文件夹切换堆栈
    private int[] folder;
    // 可undo的第一个文件夹在folder中的位置
    private int firstFolder;
    // 可redo的最后一个文件夹在folder中的位置
    private int lastFolder;
    // 当前显示的文件夹在folder中的位置
    private int currentFolder;

    /** Utility field used by event firing mechanism. */
    private EventListenerList listenerList;

}
