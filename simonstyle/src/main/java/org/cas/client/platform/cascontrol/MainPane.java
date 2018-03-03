package org.cas.client.platform.cascontrol;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.PIMBarOptimize;
import org.cas.client.platform.casbeans.textpane.IMailHyperlinkListener;
import org.cas.client.platform.cascontrol.menuaction.HideBookAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.magicbath.dialog.CASBathBar;
import org.cas.client.platform.magicbath.dialog.MagicbathGeneralPanel;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.PicturePane;
import org.cas.client.platform.pimview.theme_book.BookThemePane;
import org.cas.client.resource.international.PaneConsts;

/**
 * 1、该类做为一个传递命令者存在，它负责把来自control的命令传给放在其上面的具体 视图（比如BaseBookPane风格的视图），是控制模块跟系统使用的具体视图分开（PIMControl
 * 可以不认识BaseBook），从而在将来替换不同风格的视图时，控制，model部分不受影响。 2、在跟EIO等其它程序集成时，该类顺便实现客户系统所要求的接口（比如EIO要求加到
 * 其InternalFrame上的视图要实现IApplication接口。
 */
public class MainPane extends PicturePane implements MouseListener, MouseMotionListener, ComponentListener {
    private final int MINWIDTH = 5;
    private final int MINHEIGHT = 5;

    public MainPane() {
        super(CustOpts.custOps.getMainPaneBGImg());// 设置背景图片。
        if (CustOpts.custOps.getMainPaneBGImg() == null) // 如果用户未设图片则设背景色。
            setBackground(CustOpts.custOps.getMainPaneBGColor());

        initComponent(); // 布局MainPane中的组件
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

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     */
    public void mouseClicked(
            MouseEvent e) {
        if (e.getComponent() == this && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
            new HideBookAction().actionPerformed(null);
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
        if (!isMousePressed)
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @changeApplicationPane方法放在invokeLater中执行,因为如果Editor仍然拥有焦点时被removeEditor,回导致异常.
     * @param evt
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent evt) {
        isMousePressed = true;
        oldPosX = -10000;
        Object tmpSource = evt.getComponent();
        if (tmpSource == this) {
            int tmpX = evt.getX();
            int tmpY = evt.getY();
            if (tmpX < baseBookPane.getX() - 8 || tmpX > baseBookPane.getX() + baseBookPane.getWidth() + 8
                    || tmpY < baseBookPane.getY() - 8 || tmpY > baseBookPane.getY() + baseBookPane.getHeight() + 8) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            } else if (evt.getButton() == MouseEvent.BUTTON3) {
                resizeAnchorFlag = 0;
                anchorX = evt.getX();
                anchorY = evt.getY();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            } else {
                if (tmpX < baseBookPane.getX() && tmpX > baseBookPane.getX() - 8) {// 左上、左、左下情况
                    if (tmpY < baseBookPane.getY() + 8 && tmpY > baseBookPane.getY() - 8)// 左上
                        resizeAnchorFlag = 1;
                    else if (tmpY > baseBookPane.getY() + baseBookPane.getHeight() - 8
                            && tmpY < baseBookPane.getY() + baseBookPane.getHeight() + 8)// 左下
                        resizeAnchorFlag = 3;
                    else
                        // 左边
                        resizeAnchorFlag = 2;
                } else if (tmpX > baseBookPane.getX() + baseBookPane.getWidth()
                        && tmpX < baseBookPane.getX() + baseBookPane.getWidth() + 8) {// 右上、右、右下情况
                    if (tmpY < baseBookPane.getY() + 8 && tmpY > baseBookPane.getY() - 8)// 右上
                        resizeAnchorFlag = 7;
                    else if (tmpY > baseBookPane.getY() + baseBookPane.getHeight() - 8
                            && tmpY < baseBookPane.getY() + baseBookPane.getHeight() + 8)// 右下
                        resizeAnchorFlag = 5;
                    else
                        // 右边
                        resizeAnchorFlag = 6;
                } else if (tmpY < baseBookPane.getY() && tmpY > baseBookPane.getY() - 8) {// 上边
                    if (tmpX < baseBookPane.getX() + 8 && tmpX > baseBookPane.getX() - 8)// 左上
                        resizeAnchorFlag = 1;
                    else if (tmpX > baseBookPane.getX() + baseBookPane.getWidth() - 8
                            && tmpX < baseBookPane.getX() + baseBookPane.getWidth() + 8)// 右上
                        resizeAnchorFlag = 7;
                    else
                        resizeAnchorFlag = 8;
                } else if (tmpY > baseBookPane.getY() + baseBookPane.getHeight()
                        && tmpY < baseBookPane.getY() + baseBookPane.getHeight() + 8) {// 下边
                    if (tmpX < baseBookPane.getX() + 8 && tmpX > baseBookPane.getX() - 8)// 左下
                        resizeAnchorFlag = 3;
                    else if (tmpX > baseBookPane.getX() + baseBookPane.getWidth() - 8
                            && tmpX < baseBookPane.getX() + baseBookPane.getWidth() + 8)// 右下
                        resizeAnchorFlag = 5;
                    else
                        resizeAnchorFlag = 4;
                }
            }
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        isMousePressed = false;
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        Object tmpSource = e.getComponent();
        if (dragged) {
            if (tmpSource == this) {
                Graphics g = getGraphics();
                g.setXORMode(CustOpts.custOps.getContentBG());
                if (resizeAnchorFlag == 8) {// 上
                    int tmpConX = baseBookPane.getX();
                    int tmpConY = oldPosY;
                    int tmpConWidth = baseBookPane.getWidth();
                    int tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConWidth > MINWIDTH && tmpConHeight > MINHEIGHT) {
                        CustOpts.custOps.setContentY((double) oldPosY / getHeight());
                        CustOpts.custOps.setContentHeight((double) tmpConHeight / getHeight());
                    }
                } else if (resizeAnchorFlag == 0) {// 移动
                    int tmpConX = baseBookPane.getX() + (oldPosX - anchorX);
                    int tmpConY = baseBookPane.getY() + (oldPosY - anchorY);
                    int tmpConWidth = baseBookPane.getWidth();
                    int tmpConHeight = baseBookPane.getHeight();
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConX < getWidth() && tmpConY < getHeight()) {
                        CustOpts.custOps.setContentX((double) tmpConX / getWidth());
                        CustOpts.custOps.setContentY((double) tmpConY / getHeight());
                    }
                } else if (resizeAnchorFlag == 1) {// 上左
                    int tmpConX = oldPosX;
                    int tmpConY = oldPosY;
                    int tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                    int tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConWidth > MINWIDTH && tmpConHeight > MINHEIGHT) {
                        CustOpts.custOps.setContentX((double) tmpConX / getWidth());
                        CustOpts.custOps.setContentY((double) tmpConY / getHeight());
                        CustOpts.custOps.setContentWidth((double) tmpConWidth / getWidth());
                        CustOpts.custOps.setContentHeight((double) tmpConHeight / getHeight());
                    }
                } else if (resizeAnchorFlag == 2) {// 左
                    int tmpConX = oldPosX;
                    int tmpConY = baseBookPane.getY();
                    int tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                    int tmpConHeight = baseBookPane.getHeight();
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConWidth > MINWIDTH && tmpConHeight > MINHEIGHT) {
                        CustOpts.custOps.setContentX((double) tmpConX / getWidth());
                        CustOpts.custOps.setContentWidth((double) tmpConWidth / getWidth());
                    }
                } else if (resizeAnchorFlag == 3) {// 左下
                    int tmpConX = oldPosX;
                    int tmpConY = baseBookPane.getY();
                    int tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                    int tmpConHeight = oldPosY - baseBookPane.getY();
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConWidth > MINWIDTH && tmpConHeight > MINHEIGHT) {
                        CustOpts.custOps.setContentX((double) tmpConX / getWidth());
                        CustOpts.custOps.setContentWidth((double) tmpConWidth / getWidth());
                        CustOpts.custOps.setContentHeight((double) tmpConHeight / getHeight());
                    }
                } else if (resizeAnchorFlag == 4) {// 下
                    int tmpConX = baseBookPane.getX();
                    int tmpConY = baseBookPane.getY();
                    int tmpConWidth = baseBookPane.getWidth();
                    int tmpConHeight = oldPosY - tmpConY;
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConWidth > MINWIDTH && tmpConHeight > MINHEIGHT)
                        CustOpts.custOps.setContentHeight((double) tmpConHeight / getHeight());
                } else if (resizeAnchorFlag == 5) {// 下右
                    int tmpConX = baseBookPane.getX();
                    int tmpConY = baseBookPane.getY();
                    int tmpConWidth = oldPosX - baseBookPane.getX();
                    int tmpConHeight = oldPosY - baseBookPane.getY();
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConWidth > MINWIDTH && tmpConHeight > MINHEIGHT) {
                        CustOpts.custOps.setContentWidth((double) tmpConWidth / getWidth());
                        CustOpts.custOps.setContentHeight((double) tmpConHeight / getHeight());
                    }
                } else if (resizeAnchorFlag == 6) {// 右
                    int tmpConX = baseBookPane.getX();
                    int tmpConY = baseBookPane.getY();
                    int tmpConWidth = oldPosX - tmpConX;
                    int tmpConHeight = baseBookPane.getHeight();
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConWidth > MINWIDTH && tmpConHeight > MINHEIGHT)
                        CustOpts.custOps.setContentWidth((double) tmpConWidth / getWidth());
                } else if (resizeAnchorFlag == 7) {// 右上
                    int tmpConX = baseBookPane.getX();
                    int tmpConY = oldPosY;
                    int tmpConWidth = oldPosX - baseBookPane.getX();
                    int tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                    if (tmpConWidth > MINWIDTH && tmpConHeight > MINHEIGHT) {
                        CustOpts.custOps.setContentY((double) tmpConY / getHeight());
                        CustOpts.custOps.setContentWidth((double) tmpConWidth / getWidth());
                        CustOpts.custOps.setContentHeight((double) tmpConHeight / getHeight());
                    }
                }
            }
            invalidate();
            revalidate();
            repaint();
            oldPosX = -10000;
            dragged = false;
        }
    }

    public void mouseDragged(
            MouseEvent e) {
        dragged = true;
        Object tmpSource = e.getComponent();
        if (tmpSource == this) {
            Graphics g = getGraphics();
            g.setXORMode(CustOpts.custOps.getContentBG());

            if (resizeAnchorFlag == 8) {// 上
                int tmpConX = baseBookPane.getX();
                int tmpComY = oldPosY;
                int tmpConWidth = baseBookPane.getWidth();
                int tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpComY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = baseBookPane.getX();
                tmpComY = oldPosY;
                tmpConWidth = baseBookPane.getWidth();
                tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpComY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            }

            if (resizeAnchorFlag == 0) {// 移动
                int tmpConX = baseBookPane.getX() + (oldPosX - anchorX);
                int tmpConY = baseBookPane.getY() + (oldPosY - anchorY);
                int tmpConWidth = baseBookPane.getWidth();
                int tmpConHeight = baseBookPane.getHeight();
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = baseBookPane.getX() + (oldPosX - anchorX);
                tmpConY = baseBookPane.getY() + (oldPosY - anchorY);
                tmpConWidth = baseBookPane.getWidth();
                tmpConHeight = baseBookPane.getHeight();
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            } else if (resizeAnchorFlag == 1) {// 上左
                int tmpConX = oldPosX;
                int tmpConY = oldPosY;
                int tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                int tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = oldPosX;
                tmpConY = oldPosY;
                tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            } else if (resizeAnchorFlag == 2) {// 左
                int tmpConX = oldPosX;
                int tmpConY = baseBookPane.getY();
                int tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                int tmpConHeight = baseBookPane.getHeight();
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = oldPosX;
                tmpConY = baseBookPane.getY();
                tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                tmpConHeight = baseBookPane.getHeight();
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            } else if (resizeAnchorFlag == 3) {// 左下
                int tmpConX = oldPosX;
                int tmpConY = baseBookPane.getY();
                int tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                int tmpConHeight = oldPosY - baseBookPane.getY();
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = oldPosX;
                tmpConY = baseBookPane.getY();
                tmpConWidth = baseBookPane.getWidth() - (oldPosX - baseBookPane.getX());
                tmpConHeight = oldPosY - baseBookPane.getY();
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            } else if (resizeAnchorFlag == 4) {// 下
                int tmpConX = baseBookPane.getX();
                int tmpConY = baseBookPane.getY();
                int tmpConWidth = baseBookPane.getWidth();
                int tmpConHeight = oldPosY - tmpConY;
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = baseBookPane.getX();
                tmpConY = baseBookPane.getY();
                tmpConWidth = baseBookPane.getWidth();
                tmpConHeight = oldPosY - tmpConY;
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            } else if (resizeAnchorFlag == 5) {// 下右
                int tmpConX = baseBookPane.getX();
                int tmpConY = baseBookPane.getY();
                int tmpConWidth = oldPosX - baseBookPane.getX();
                int tmpConHeight = oldPosY - baseBookPane.getY();
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = baseBookPane.getX();
                tmpConY = baseBookPane.getY();
                tmpConWidth = oldPosX - baseBookPane.getX();
                tmpConHeight = oldPosY - baseBookPane.getY();
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            } else if (resizeAnchorFlag == 6) {// 右
                int tmpConX = baseBookPane.getX();
                int tmpConY = baseBookPane.getY();
                int tmpConWidth = oldPosX - tmpConX;
                int tmpConHeight = baseBookPane.getHeight();
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = baseBookPane.getX();
                tmpConY = baseBookPane.getY();
                tmpConWidth = oldPosX - tmpConX;
                tmpConHeight = baseBookPane.getHeight();
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            } else if (resizeAnchorFlag == 7) {// 右上
                int tmpConX = baseBookPane.getX();
                int tmpConY = oldPosY;
                int tmpConWidth = oldPosX - baseBookPane.getX();
                int tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                if (oldPosX != -10000)
                    for (int i = 8; i > 0; i--)
                        g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
                oldPosX = e.getX();
                oldPosY = e.getY();
                tmpConX = baseBookPane.getX();
                tmpConY = oldPosY;
                tmpConWidth = oldPosX - baseBookPane.getX();
                tmpConHeight = baseBookPane.getHeight() - (oldPosY - baseBookPane.getY());
                for (int i = 8; i > 0; i--)
                    g.drawRect(tmpConX + i, tmpConY + i, tmpConWidth - i * 2, tmpConHeight - i * 2);
            }
        }
    }

    /** Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed. */
    public void mouseMoved(
            MouseEvent e) {
        if (isMousePressed)
            return;

        if (e.getComponent() == this) {
            int tmpX = e.getX();
            int tmpY = e.getY();
            if (tmpX < baseBookPane.getX() - 8 || tmpX > baseBookPane.getX() + baseBookPane.getWidth() + 8
                    || tmpY < baseBookPane.getY() - 8 || tmpY > baseBookPane.getY() + baseBookPane.getHeight() + 8) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }

            if (tmpX < baseBookPane.getX() && tmpX > baseBookPane.getX() - 8) {// 左上、左、左下情况
                if (tmpY < baseBookPane.getY() + 8 && tmpY > baseBookPane.getY() - 8)// 左上
                    setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                else if (tmpY > baseBookPane.getY() + baseBookPane.getHeight() - 8
                        && tmpY < baseBookPane.getY() + baseBookPane.getHeight() + 8)// 左下
                    setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                else
                    // 左边
                    setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            } else if (tmpX > baseBookPane.getX() + baseBookPane.getWidth()
                    && tmpX < baseBookPane.getX() + baseBookPane.getWidth() + 8) {// 右上、右、右下情况
                if (tmpY < baseBookPane.getY() + 8 && tmpY > baseBookPane.getY() - 8)// 右上
                    setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                else if (tmpY > baseBookPane.getY() + baseBookPane.getHeight() - 8
                        && tmpY < baseBookPane.getY() + baseBookPane.getHeight() + 8)// 右下
                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                else
                    // 右边
                    setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            } else if (tmpY < baseBookPane.getY() && tmpY > baseBookPane.getY() - 8) {// 上边
                if (tmpX < baseBookPane.getX() + 8 && tmpX > baseBookPane.getX() - 8)// 左上
                    setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                else if (tmpX > baseBookPane.getX() + baseBookPane.getWidth() - 8
                        && tmpX < baseBookPane.getX() + baseBookPane.getWidth() + 8)// 右上
                    setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                else
                    setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            } else if (tmpY > baseBookPane.getY() + baseBookPane.getHeight()
                    && tmpY < baseBookPane.getY() + baseBookPane.getHeight() + 8) {// 下边
                if (tmpX < baseBookPane.getX() + 8 && tmpX > baseBookPane.getX() - 8)// 左下
                    setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                else if (tmpX > baseBookPane.getX() + baseBookPane.getWidth() - 8
                        && tmpX < baseBookPane.getX() + baseBookPane.getWidth() + 8)// 右下
                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                else
                    setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            } else
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * 布局整个MainPane中组件的位置.
     * 
     * @NOTE:上下边界故意留出两个象素,仅仅为了美观.
     */
    public void layoutContainer(
            Container prmContainer) {
        int tmpWidth = getWidth();
        int tmpHeight = getHeight();
        // the following is added for WangFang's Bath room
        if (CustOpts.custOps.getActivePathID() == 9770402) { // 关闭状态。
            bathPane.setBounds(2, 2, tmpWidth - 4, tmpHeight - 4);
            baseBookPane.setBounds(2, 2, 0, 0);// add end.
        } else {
            bathPane.setBounds(2, 2, 0, 0);// add end.
            if (CustOpts.custOps.isBaseBookHide())
                baseBookPane.setBounds(2, 2, tmpWidth - 4, tmpHeight - 4);
            else
                baseBookPane.setBounds((int) (tmpWidth * CustOpts.custOps.getContentX()),
                        (int) (tmpHeight * CustOpts.custOps.getContentY()),
                        (int) (tmpWidth * CustOpts.custOps.getContentWidth()),
                        (int) (tmpHeight * CustOpts.custOps.getContentHeight()));
        }
    }

    /**
     * 布局整个MainPane中组件的位置.
     * 
     * @NOTE:上下边界故意留出两个象素,仅仅为了美观.
     */
    public void reLayout() {
        int tmpWidth = getWidth();
        int tmpHeight = getHeight();
        // the following is added for WangFang's Bath room
        if (CustOpts.custOps.getActivePathID() == 9770402) { // 关闭状态。
            bathPane.setBounds(2, 2, tmpWidth - 4, tmpHeight - 4);
            baseBookPane.setBounds(2, 2, 0, 0);// add end.
        } else {
            bathPane.setBounds(2, 2, 0, 0);// add end.
            if (CustOpts.custOps.isBaseBookHide())
                baseBookPane.setBounds(2, 2, tmpWidth - 4, tmpHeight - 4);
            else
                baseBookPane.setBounds((int) (tmpWidth * CustOpts.custOps.getContentX()),
                        (int) (tmpHeight * CustOpts.custOps.getContentY()),
                        (int) (tmpWidth * CustOpts.custOps.getContentWidth()),
                        (int) (tmpHeight * CustOpts.custOps.getContentHeight()));
        }
        revalidate();
        invalidate();
        repaint();
    }

    /**
     * 增加了一个边框,仅仅为了美观.
     */
    public void paintComponent(
            java.awt.Graphics g) {
        super.paintComponent(g);
        int tmpLeft = 0;
        int tmpTop = 0;
        int tmpRight = getWidth();
        int tmpButtom = getHeight();
        int tmpColor = 255;
        for (int i = 0; i < 3; i++) {
            g.setColor(new java.awt.Color(tmpColor, tmpColor, tmpColor));
            g.drawRect(tmpLeft, tmpTop, tmpRight - tmpLeft, tmpButtom - tmpTop);
            tmpLeft++;
            tmpTop++;
            tmpRight--;
            tmpButtom--;
            tmpColor -= 20;
        }
        for (int i = 0; i < 1; i++) {
            g.setColor(new java.awt.Color(tmpColor, tmpColor, tmpColor));
            g.drawRect(tmpLeft, tmpTop, tmpRight - tmpLeft, tmpButtom - tmpTop);
            tmpLeft++;
            tmpTop++;
            tmpRight--;
            tmpButtom--;
            tmpColor += 20;
        }
    }

    /**
     * @called by: PIMControl Note:传入的参数必须是有效且界内的，所有的判断再上游应判断好。 NOTE: 用于传递Control的命令到BaseBookPane或将来的其它的具体视图.
     * @param prmIndex
     *            主索引
     */
    public void changeApplication(
            int prmAppIndex,
            int prmFolderPathID) {
        if (activeApp != null)
            CASControl.ctrl.removeToolbars(activeApp.getDynamicBars());

        if (prmAppIndex < 0) {
            activeApp = baseBookPane.getColsePane();
            activeApp.refreshView(null);
        } else {
            activeApp = getApp((String) CustOpts.custOps.APPNameVec.get(prmAppIndex));
            activeApp.refreshView(CASControl.ctrl.getViewInfo(prmFolderPathID));
        }
        CASControl.ctrl.addToolbars(activeApp.getDynamicBars());
        baseBookPane.changeApplication(prmAppIndex, prmFolderPathID);// 书本风格的修饰调整一下，换上新的ActiveApp。
    }

    /**
     * @called by emo.pim.pimcontrol.action.model.ModelChangeListener;
     * @NOTE:目前是BasebookPane，将来可能会换成其他风格的面板。
     */
    public void updateView(
            PIMModelEvent e) {
        baseBookPane.updateView(e); // 调它上面的具体视图的更新model方法。

        int currentAppType = CustOpts.custOps.getActiveAppType();
        if (currentAppType == ModelCons.CLOSED) {
            changeApplication(currentAppType, PaneConsts.PIM_PATH_ID);
            return;
        } else if (currentAppType != e.getAppType()) // 其他事件源和当前视图不符的情况将导致事件被忽略.
            return;

        activeApp.updateContent(e);// 调视图的更新方法。
    }

    /**
     * 选取视图中的所有记录。
     * 
     * @called by seleteAllAction
     */
    public void seleteAllRecord() {
        if (baseBookPane != null)
            baseBookPane.seleteAllRecord();
    }

    /**
     * Invoke when the application frame closed.
     * 
     * @param save
     *            true if the frame need to be saved.
     */
    public void closed(
            boolean save) {
        baseBookPane.closed(true);
    }

    // ------------------------------
    /**
     * @called by:PIMControle. TODO：暂时提供，以后要去掉，不希望MainPane的客户认识BaseBookPane。
     */
    public BookThemePane getBaseBookPane() {
        return baseBookPane;
    }

    /**
     * 取得被选中的记录。
     * 
     * @return 被选中的记录
     * @called by CutAction & CopyAction
     */
    public Vector getSelectRecords() {
        return baseBookPane != null ? baseBookPane.getSelectRecords() : new Vector(0);
    }

    /**
     * 取得被选中的记录。
     * 
     * @return 被选中的记录
     * @called by CutAction & CopyAction
     */
    public int[] getSelectRecordsID() {
        if (baseBookPane != null)
            return baseBookPane.getSelectRecordsID();
        else
            return new int[0];
    }

    /**
     * 通过先根据传入的应用名字检查是否已经在共享池中存在，存在则返回。 如果不存在的话，则通过反射实例化一个，返回，同时存入共享池，供下次用。
     * 
     * @NOTE：应用的实例不宜过大（即二次开发厂商应注意，IApplication的实现不宜具有过多属性或在构造时实例化过多内容。以减少内存占用。
     * @param prmClassName
     * @return
     */
    public static IApplication getApp(
            Object prmClassName) {
        Object tmpObj = applications.get(prmClassName);
        if (tmpObj == null) {
            tmpObj = getClassInstance(prmClassName.toString());
            applications.put(prmClassName, tmpObj);
        }
        return (IApplication) tmpObj;
    }

    // called by views.
    public static int getDividerSize() {
        return BookThemePane.getDividerSize();
    }

    public IApplication getActiveApp() {
        return activeApp;
    }

    // Called by PIMControl.
    public Action getAction(
            Object prmActionName) {
        Action tmpAction = null;
        Vector tmpVec = CustOpts.custOps.APPNameVec;
        for (int i = 0, tmpLength = tmpVec.size(); i < tmpLength; i++) {
            tmpAction = getApp(tmpVec.get(i)).getAction(prmActionName);
            if (tmpAction != null)
                return tmpAction;
        }
        return tmpAction;
    }

    public JToolBar[] getStaticToolbars() {
        Vector<JToolBar> tmpBarVec = new Vector<JToolBar>();
        Vector tmpVec = CustOpts.custOps.APPNameVec;
        JToolBar[] tmpBars = null;
        for (int i = 0, tmpLength = tmpVec.size(); i < tmpLength; i++) {
            tmpBars = getApp(tmpVec.get(i)).getStaticBars();
            if (tmpBars == null)
                continue;
            else
                for (int j = 0, len = tmpBars.length; j < len; j++)
                    tmpBarVec.add(tmpBars[j]);
        }

        tmpBarVec.add(new PIMBarOptimize()); // @TODO:本工具条为开发者调试用,出厂前应去掉.

        if (tmpBarVec.size() > 0) {
            tmpBars = new JToolBar[tmpBarVec.size()];
            for (int i = 0; i < tmpBars.length; i++)
                tmpBars[i] = (JToolBar) tmpBarVec.get(i);
            return tmpBars;
        } else
            return null;
    }

    public JPanel[] getStaticStatebars() {
        Vector<JPanel> tmpBarVec = new Vector<JPanel>();
        Vector tmpVec = CustOpts.custOps.APPNameVec;
        JPanel[] tmpBars = null;
        for (int i = 0, tmpLength = tmpVec.size(); i < tmpLength; i++) {
            tmpBars = getApp(tmpVec.get(i)).getStaticStateBars();
            if (tmpBars == null)
                continue;
            else
                for (int j = 0, len = tmpBars.length; j < len; j++)
                    tmpBarVec.add(tmpBars[j]);
        }

        tmpBarVec.add(new CASBathBar()); // @TODO:本工具条为开发者调试用,出厂前应去掉.

        if (tmpBarVec.size() > 0) {
            tmpBars = new JPanel[tmpBarVec.size()];
            for (int i = 0; i < tmpBars.length; i++)
                tmpBars[i] = (JPanel) tmpBarVec.get(i);
            return tmpBars;
        } else
            return null;
    }

    /*
     * Called from ctrl by ImportantDlg，to get the Contents to import from each App.
     */
    public Vector getImportDisStr() {
        String[] tmpItemAry = null;
        Vector tmpNameVec = CustOpts.custOps.APPNameVec;
        Vector ItemVec = new Vector();
        for (int i = 0, tmpAppCount = tmpNameVec.size(); i < tmpAppCount; i++) {
            tmpItemAry = getApp(tmpNameVec.get(i)).getImportDispStr();
            if (tmpItemAry != null)
                for (int r = 0, tmpItemCount = tmpItemAry.length; r < tmpItemCount; r++)
                    ItemVec.add(tmpItemAry[r]);
        }
        return ItemVec;
    }

    public String getImportIntrStr(
            Object prmKey) {
        String tmpIntrStr = null;
        Vector tmpNameVec = CustOpts.custOps.APPNameVec;
        for (int i = 0, tmpAppCount = tmpNameVec.size(); i < tmpAppCount; i++) {
            tmpIntrStr = getApp(tmpNameVec.get(i)).getImportIntrStr(prmKey);
            if (tmpIntrStr != null)
                break;
        }
        return tmpIntrStr;
    }

    public void execImport(
            Object prmKey) {
        Vector tmpNameVec = CustOpts.custOps.APPNameVec;
        for (int i = 0, tmpAppCount = tmpNameVec.size(); i < tmpAppCount; i++) {
            if (getApp(tmpNameVec.get(i)).execImport(prmKey))
                break;
        }
    }

    public int getStatus(
            Object prmActionName) {
        Vector tmpVec = CustOpts.custOps.APPNameVec;
        for (int tmpLength = tmpVec.size(), i = 0; i < tmpLength; i++) {
            int tmpStauts = getApp(tmpVec.get(i)).getStatus(prmActionName);
            if (tmpStauts != -1)
                return tmpStauts;
        }
        return -1;
    }

    /**
     * 布局MainPane中的组件： EActivityeTodayTitle。 警告: 该部分的内容需要取得用户需要的视图。
     */
    private void initComponent() {
        baseBookPane = new BookThemePane(null); // 新建BaseBookPane并加到面板上
        bathPane = new MagicbathGeneralPanel(); // the following is added for 洗浴中心.
        add(baseBookPane);
        add(bathPane);// 洗浴中心结束--------
        // TODO：等完善后再打开//新建便签面板并加到面板上
        // notePaper = new NotePaper();
        // notePaper.setBounds(0, 2, 200, 150);
        // add(notePaper);
        reLayout();
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
    }

    private static IApplication getClassInstance(
            String prmClassname) {
        StringBuffer tmpClassPath = new StringBuffer("org.cas.client.platform.");
        tmpClassPath.append(prmClassname.toLowerCase());
        tmpClassPath.append(".App_");
        tmpClassPath.append(prmClassname);
        try {
            return (IApplication) Class.forName(tmpClassPath.toString()).newInstance();
        } catch (Exception e) {
        }
        return null;
    }

    public IMailHyperlinkListener getMailListner() {
        if (applications.get("mail") != null)
            return (IMailHyperlinkListener) applications.get("mail");
        else
            return null;
    }

    // --------------------------------------------------------------------------
    private static HashMap applications = new HashMap();
    private static Frame parent;
    private IApplication activeApp; // 当前应用(卡片,表格)
    private BookThemePane baseBookPane;
    private boolean dragged;
    private boolean isMousePressed;
    private int oldPosX = -10000;
    private int oldPosY = -1;
    private int anchorX, anchorY;// ForMove;
    private int resizeAnchorFlag;
    // the following is added for WangFang's BathRoom.
    private MagicbathGeneralPanel bathPane;
}

// //实现来自ApplicationPane的接口----------------------------------------------
// /** Invoke when the application frame activated that may be occurs when
// * the mainframe activated (or after the menu operation(not implement now)).
// */
// public void activated()
// {
// }
//
// /** Invoke when arrange window
// */
// public void arrange()
// {
// }
//
//
// /** Invoke when the application frame deactivated that may be occurs when
// * the mainframe deactivated or menu operation. (not implement now)
// */
// public void deactivated()
// {
// }
//
// /** get the mediator to draw Object.
// * @return the mediator of draw Object.
// */
// public ShapeMediator getMediator()
// {
// //TODO: replace it to make it significative.
// return null;
// }
//
// /** Get the container of Application ,this container will be used as the
// * contentpane for frame.
// * @return the container of Application.
// */
// public java.awt.Container getPane()
// {
// //TODO: replace it to make it significative.
// return null;
// }
//
// /** Get the status panel.
// * @return the status panel.
// */
// public javax.swing.JPanel getStatusPanel()
// {
// //TODO: replace it to make it significative.
// return null;
// }
//
// /**
// * Invoke when the application frame selected when user selects the frame
// * with mouse or key, or automatically selected when other frame is closed,
// * hidden or iconified.
// */
// public void selected()
// {
// }
//
// /**
// * Set the all status for main control to diaplay.
// * @param statusSet the status set instance for application to fill.
// */
// public void setStatusSet(StatusSet statusSet)
// {
// }
//
// /**
// * Invoke when the application frame unselected when user selects other
// * frame with mouse or key, or this frame is closed, hidden or iconified.
// * @return return true if unselect successfully.
// */
// public boolean unselected()
// {
// //TODO: replace it to make it significative.
// return true;
// }
//
// /**
// * Update UI style when user set.
// */
// public void updateUIStyle()
// {
// }

/*
 * //mainPane只响应鼠标Press事件，动作是使书本合上。 getToolkit().addAWTEventListener(new AWTEventListener() { // dispatch awt event
 * public void eventDispatched(AWTEvent evt) { if (!(evt instanceof MouseEvent)) { return; } MouseEvent mouseEvt =
 * (MouseEvent)evt; if (mouseEvt.getID() == MouseEvent.MOUSE_PRESSED) { closeAllFloatWindow(mouseEvt.getSource()); } }
 * }, AWTEvent.MOUSE_EVENT_MASK );
 */
