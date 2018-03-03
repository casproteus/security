package org.cas.client.platform.pimview.pimtable;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import sun.awt.dnd.SunDragSourceContextPeer;

/**
 * 本类封装了鼠标的两个监听器,实现两接口中的几个相关方法
 * <p>
 * @called by: BasicPIMTableUI.TableDragGestureRecognizer;
 */

class BasicPIMDragGestureRecognizer implements MouseListener, MouseMotionListener {
    /**
     * 静态方法 返回鼠标运动逻辑像素
     * 
     * @return 从系统取不到就以5个点来表示鼠标运行了
     */
    private static int getMotionThreshold() {
        // 如果已检查则将其值返回
        if (checkedMotionThreshold) {
            return motionThreshold;
        } else {
            // 否则设置检查标志为真
            checkedMotionThreshold = true;
            try {
                // 取系统鼠标运动逻辑像素数这个属性 gestureMotionThreshold
                motionThreshold =
                        ((Integer) Toolkit.getDefaultToolkit().getDesktopProperty("DnD.gestureMotionThreshold"))
                                .intValue();
            } catch (Exception e) {
                // 取不到就设为5,
                motionThreshold = 5;
            }
        }
        return motionThreshold;
    }

    /**
     * 映射拖动操作的属性
     * 
     * @param e
     *            鼠标事件源
     * @return 不大清楚
     */
    protected int mapDragOperationFromModifiers(
            MouseEvent e) {
        int tmpMods = e.getModifiersEx(); // 得到鼠标事件源的扩展信息,Ctrl or Shift etc.
        if ((tmpMods & InputEvent.BUTTON1_DOWN_MASK) != InputEvent.BUTTON1_DOWN_MASK) { // 如不是左键按下就返回无
            return TransferHandler.NONE;
        }

        JComponent tmpComp = getComponent(e); // 得到发出鼠标事件的组件
        TransferHandler tmpTransHandler = tmpComp.getTransferHandler(); // 得到转移句柄
        // 通过SunDragSourceContextPeer类中的方法将该扩展信息转为映射的ID值后返回,目前这个类我还没查到其帮助文档在哪里
        return SunDragSourceContextPeer
                .convertModifiersToDropAction(tmpMods, tmpTransHandler.getSourceActions(tmpComp));
    }

    /**
     * 实现 MouseListener 接口中的方法 ,在此不作处理
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
    }

    /**
     * 实现 MouseListener 接口中的方法 ,
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
        dndArmedEvent = null; // 先置空dndArmedEvent拖放标志.
        if (isDragPossible(e) && mapDragOperationFromModifiers(e) != TransferHandler.NONE)// 如发出鼠标事件的组件可拖动,且该事件确是鼠标拖动事件
        {
            dndArmedEvent = e; // 保存该鼠标事件源
            e.consume(); // 把它消耗掉,不再往下传了
        }
    }

    /**
     * 实现 MouseListener 接口中的方法
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        dndArmedEvent = null;
    }

    /**
     * 实现 MouseListener 接口中的方法 ,在此不作处理
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * 实现 MouseListener 接口中的方法 ,在此不作处理
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * 实现 MouseMotionListener 接口中的方法 ,在此不作处理
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseDragged(
            MouseEvent e) {
        // 按下鼠标左键了
        if (dndArmedEvent != null) {
            // 把它消耗掉,不再往下传了
            e.consume();
            // 得到拖动属性
            int tmpAction = mapDragOperationFromModifiers(e);
            // 是空处理
            if (tmpAction == TransferHandler.NONE) {
                return;
            }
            // 得到坐标偏移
            int dx = Math.abs(e.getX() - dndArmedEvent.getX());
            int dy = Math.abs(e.getY() - dndArmedEvent.getY());
            // 鼠标运动超过逻辑像素数,开始操作转移
            if ((dx > getMotionThreshold()) || (dy > getMotionThreshold())) {
                // start transfer... shouldn't be a click at this point
                // 取所需参数
                JComponent c = getComponent(e);
                TransferHandler th = c.getTransferHandler();
                // 输出操作,
                th.exportAsDrag(c, dndArmedEvent, tmpAction);
                // 清空该事件
                dndArmedEvent = null;
            }
        }
    }

    /**
     * 实现 MouseMotionListener 接口中的方法 ,在此不作处理
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseMoved(
            MouseEvent e) {
    }

    // /** 从鼠标事件源中取得转移句柄
    // * @param e 鼠标事件源
    // * @return 转移句柄
    // */
    // private TransferHandler getTransferHandler (MouseEvent e)
    // {
    // //得到发出鼠标事件的组件
    // JComponent c = getComponent (e);
    // //c为空则返回空,否则返回其转移句柄
    // return c == null ? null : c.getTransferHandler ();
    // }

    /**
     * 查询发出鼠标事件的组件是否可拖动 Determines if the following are true:
     * <ul>
     * <li>the press event is located over a selection
     * <li>the dragEnabled property is true
     * <li>A TranferHandler is installed
     * </ul>
     * <p>
     * This is implemented to check for a TransferHandler. Subclasses should perform the remaining conditions.
     * 
     * @param e
     *            转移句柄
     * @return 是否可拖动
     */
    protected boolean isDragPossible(
            MouseEvent e) {
        JComponent tmpComp = getComponent(e); // 得到发出鼠标事件的组件
        return (tmpComp == null) ? true : (tmpComp.getTransferHandler() != null); // c为空则返回真,否则返回其以其转移句柄是否为空
    }

    /**
     * 从鼠标事件源中返回发出该鼠标事件的组件
     * 
     * @param e
     *            鼠标事件源
     * @return 派发事件的组件
     */
    protected JComponent getComponent(
            MouseEvent e) {
        // 得到事件源
        Object src = e.getSource();
        if (src instanceof JComponent) {
            // 是 JComponent 组件就返回
            JComponent c = (JComponent) src;
            return c;
        }
        // 否则返回空
        return null;
    }

    private MouseEvent dndArmedEvent; // 保存MouseEvent事件源
    private static int motionThreshold; // 保存鼠标运动逻辑像素的值(鼠标在屏幕上移动几个像素算拖动)
    private static boolean checkedMotionThreshold; // 保存检查动作开始的标志
}
