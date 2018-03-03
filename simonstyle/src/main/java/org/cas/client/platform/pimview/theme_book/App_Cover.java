package org.cas.client.platform.pimview.theme_book;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.cas.client.platform.cascontrol.AbstractApp;
import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;

/**
 * 初始的Note面板 TODO:要移到theme_book包内。
 */

class App_Cover extends AbstractApp implements IApplication {
    App_Cover() {
        super(CustOpts.custOps.getCoverImage());
        pingsPanel = new PingsPanel(this);
        noteBottom = new NoteBottom(this);
        add(pingsPanel);
        add(noteBottom);
    }

    // 实现IApplication接口,仅仅为了统一对待---------------------------------------
    public PIMViewInfo getActiveViewInfo() {
        return null;
    }

    public void showPopupMenu(
            Component comp,
            int x,
            int y) {
    }

    public void updateContent(
            PIMModelEvent e) {
    }

    public void seleteAllRecord() {
    }

    public int[] getSelectRecordsID() {
        return null;
    }

    public Vector getSelectRecords() {
        return null;
    }

    public void initInfoInDB() {
    }

    public void showDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag) {
    }

    public ICASDialog getADialog() {
        return null;
    }

    public JToolBar[] getStaticBars() {
        return null;
    }

    public JPanel[] getStaticStateBars() {
        return null;
    }

    public JPanel[] getDynamicStateBars() {
        return null;
    }

    public JToolBar[] getDynamicBars() {
        return null;
    }

    public JMenuItem getCreateMenu() {
        return null;
    }

    public JMenuItem getCreateMenu(
            Vector prmSelectedRecVec) {
        return null;
    }

    public String[] getAppFields() {
        return null;
    }

    public String[] getAppTypes() {
        return null;
    }

    public String[] getAppTexts() {
        return null;
    }

    public Action getAction(
            Object prmActionName) {
        return null;
    }

    public int getStatus(
            Object prmActionName) {
        return -1;
    }

    public String[] getImportDispStr() {
        return null;
    } // 返回应用所支持的可导入内容的字符串数组。

    public int[] getImportableFields() {
        return null;
    }// 返回应用中可以供导入的字段.

    public String getImportIntrStr(
            Object prmKey) {
        return null;
    }// 返回可导入内容对饮的说明文字。

    public FieldDescription getFieldDescription(
            String prmHeadName,
            boolean prmIsEditable) {
        return null;
    }

    public boolean execImport(
            Object prmKey) {
        return false;
    }

    public IView getTiedView() {
        return null;
    }

    public Icon getAppIcon(
            boolean prmIsBig) {
        return null;
    }

    public String[] getRecommendColAry() {
        return null;
    }// 返回用于在查找对话盒中显示的一些文本类型的列名.方便用户做简单查找.

    // implements over-----------------------------------------
    /**
     * 改变视图
     * 
     * @param contentsV
     *            表格数据
     * @param newViewInfo
     *            视图信息
     */
    public void refreshView(
            PIMViewInfo prmNewViewInfo) {
        currentViewInfo = prmNewViewInfo;
    }

    /**
     * 本方法用于设置View上各个组件的尺寸。
     */
    public void setBounds(
            int prmX,
            int prmY,
            int prmWidth,
            int prmHeight) {
        super.setBounds(prmX, prmY, prmWidth, prmHeight);
        layoutContainer(null);
    }

    /** 面板布局 */
    public void layoutContainer(
            Container prmContainer) {
        int tmpWidth = getWidth();
        int tmpHeight = getHeight();
        int tmpPos = CustOpts.custOps.getSplitHeight();

        if (isLeft) {
            if (CustOpts.custOps.isViewTopAndDown()) {
                setPaintArea(0, 0, tmpWidth - edgeWidth / 2, tmpPos + 9);
                pingsPanel.setBounds(0, tmpPos - 8, tmpWidth - 9, BookThemePane.getDividerSize() + 8);
                noteBottom.setBounds(tmpWidth - edgeWidth / 2, 0, edgeWidth / 2, tmpPos + 9);
            } else {
                setPaintArea(0, 0, tmpPos + 9, tmpHeight - edgeWidth / 2);
                pingsPanel.setBounds(tmpPos - 8, 0, BookThemePane.getDividerSize() + 8, tmpHeight - 9);
                noteBottom.setBounds(0, tmpHeight - edgeWidth / 2, tmpPos + 9, edgeWidth / 2);
            }
        } else {
            if (CustOpts.custOps.isViewTopAndDown()) {
                setPaintArea(0, tmpPos + BookThemePane.getDividerSize() - 10, tmpWidth - edgeWidth / 2, tmpHeight
                        - tmpPos - BookThemePane.getDividerSize() + 22);
                pingsPanel.setBounds(0, tmpPos - 8, tmpWidth - 9, BookThemePane.getDividerSize() + 8);
                noteBottom.setBounds(tmpWidth - edgeWidth / 2, tmpPos + BookThemePane.getDividerSize() - 10,
                        edgeWidth / 2, tmpWidth - tmpPos - BookThemePane.getDividerSize() + 22);
            } else {
                setPaintArea(tmpPos + BookThemePane.getDividerSize() - 10, 0,
                        tmpWidth - tmpPos - BookThemePane.getDividerSize() + 22, tmpHeight - edgeWidth / 2);
                pingsPanel.setBounds(tmpPos - 8, 0, BookThemePane.getDividerSize() + 8, tmpHeight - 9);
                noteBottom.setBounds(tmpPos + BookThemePane.getDividerSize() - 10, tmpHeight - edgeWidth / 2, tmpWidth
                        - tmpPos - BookThemePane.getDividerSize() + 22, edgeWidth / 2);
            }
        }
    }

    boolean isLeft; // 是否为是左边的TAB面板
    private PingsPanel pingsPanel; // 中间的文件夹组件
    private NoteBottom noteBottom; // 关闭按钮
    final int edgeWidth = 10; // EPageEdge.edgeLines;TAB面板的宽度

    // ==================================================================================
    /**
     * 中间的文件夹组件
     */
    class PingsPanel extends JComponent {
        App_Cover coverPane; // 保存本类引用

        /**
         * 构造方法
         * 
         * @param prmPane
         *            传入本类引用
         */
        public PingsPanel(App_Cover prmPane) {
            coverPane = prmPane;
        }

        /**
         * 绘制方法
         * 
         * @param g
         *            图形设备
         */
        public void paintComponent(
                Graphics g) {
            super.paintComponent(g);
            Image clampNoHole = CustOpts.custOps.getClampNoHoleImage();// 中间的文件夹图片
            if (CustOpts.custOps.isViewTopAndDown()) {
                // 画夹子上的别针。
                int tmpDis =
                        (getWidth() - 2 * CustOpts.custOps.getVerticalDis()) / (CustOpts.custOps.getClampNumber() + 1);
                int x = tmpDis + CustOpts.custOps.getVerticalDis();
                for (int i = 0; i < CustOpts.custOps.getClampNumber(); ++i) {
                    g.drawImage(clampNoHole, x, getHeight() / 2 - 6 - 6, coverPane); // 绘制书夹状的图片
                    x += tmpDis;
                }
            } else {
                // 画夹子上的别针。
                int tmpDis =
                        (getHeight() - 2 * CustOpts.custOps.getVerticalDis()) / (CustOpts.custOps.getClampNumber() + 1);
                int y = tmpDis + CustOpts.custOps.getVerticalDis();
                for (int i = 0; i < CustOpts.custOps.getClampNumber(); ++i) {
                    g.drawImage(clampNoHole, getWidth() / 2 - 6 - 6, y, coverPane); // 绘制书夹状的图片
                    y += tmpDis;
                }
            }
        }
    }

    // ==================================================================================
    /**
     * 绘制Note底部
     */
    class NoteBottom extends JComponent {

        App_Cover coverPane; // 本类引用

        /**
         * 构建器
         * 
         * @param pane
         *            书视图
         */
        public NoteBottom(App_Cover pane) {
            coverPane = pane;
        }

        /**
         * 绘制方法
         * 
         * @param g
         *            图形设备
         */
        public void paintComponent(
                Graphics g) {
            int width = getWidth();
            int height = getHeight();
            if (coverPane.isLeft)
                drawBookLeftBottom(width, height, g);
            else
                drawBookRightBottom(width, height, g);
        }

        /**
         * 绘制左边底部边界
         * 
         * @param width
         *            宽度
         * @param height
         *            高度
         * @param g
         *            图形设备
         */
        private void drawBookLeftBottom(
                int width,
                int height,
                Graphics g) {
            g.setColor(lineColor);
            if (CustOpts.custOps.isViewTopAndDown()) {
                int x = 0;
                int y = 1;
                int tmpheight = height;
                g.drawLine(x, y, x, tmpheight);
                for (int i = 0; i < edgeWidth; ++i) {
                    g.drawLine(x, y, x, tmpheight);
                    ++x;
                    --tmpheight;
                }
            } else {
                int tmpWidth = width;
                int x = 0;
                int y = 0;
                g.drawLine(x, y, width, y);
                for (int i = 0; i < edgeWidth; ++i) {
                    g.drawLine(x, y, tmpWidth, y);
                    --tmpWidth;
                    ++y;
                }
            }
        }

        /**
         * 绘制右边底部边界
         * 
         * @param width
         *            宽度
         * @param height
         *            高度
         * @param g
         *            图形设备
         */
        private void drawBookRightBottom(
                int width,
                int height,
                Graphics g) {
            g.setColor(lineColor);
            if (CustOpts.custOps.isViewTopAndDown()) {
                int x = 0;
                int y = -1;
                int tmpheight = height;
                g.drawLine(x, y, x, tmpheight);
                for (int i = 0; i < edgeWidth; ++i) {
                    g.drawLine(x, y, x, tmpheight);
                    ++x;
                    ++y;
                }
            } else {
                int xOffset = edgeWidth;
                int x = -1;
                int y = 0;
                g.drawLine(x, y, width, y);
                g.drawLine(x++, y++, width, y);
                for (int i = 2; i < edgeWidth; ++i) {
                    g.drawLine(x, y, width - x, y);
                    ++x;
                    ++y;
                }
            }
        }

        /**
         * 书页线的颜色
         */
        Color lineColor = new Color(75, 75, 75);
    }
}

/**
 * 内部类 绘制边界区域
 *
 * class clamp extends JComponent { /** 绘制方法
 *
 * public void paintComponent(Graphics g) { int width = getWidth(); int height = getHeight(); if (isLeft) {
 * drawBookLeftEdge(width, height, g); } else { drawBookRightEdge(width, height, g); } } /** 绘制左边边界
 *
 * private void drawBookLeftEdge(int width, int height, Graphics g) { int x = 0; int y = 0; int yOffset = edgeWidth;
 * g.setColor(CustomOptions.custOps.getClampColor()); for (int i = 0; i < edgeWidth; ++i) { g.drawLine(x, yOffset, x,
 * height - (edgeWidth - yOffset)); --yOffset; ++x; } } /** 绘制右边边界
 *
 * private void drawBookRightEdge(int width, int height, Graphics g) { int x = 0; int y = 0; int yOffset = 0;
 * g.setColor(CustomOptions.custOps.getClampColor()); for (int i = 0; i < edgeWidth; ++i) { g.drawLine(x, yOffset, x,
 * height - (edgeWidth - yOffset)); ++yOffset; ++x; } } }
 */
