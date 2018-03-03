package org.cas.client.platform.pimview.pimtable;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

/**
 * 本类实现ImageObserver接口 用的不是状态模式state就是观察者模式Observer
 */

class HeaderImageObserver implements ImageObserver {
    /** 保存表格头 */
    private PIMTableHeader header;
    /** 保存列数 */
    private int col;

    /**
     * 构建一个 HeaderImageObserver 的实例
     * 
     * @param header
     *            列头
     * @param col
     *            列数
     */
    HeaderImageObserver(PIMTableHeader header, int col) {
        this.header = header;
        this.col = col;
    }

    /**
     * 实现 ImageObserver 接口中的方法,判断图片是否更新成功
     * 
     * @return 是否更新成功
     * @param img
     *            图片
     * @param flags
     *            标识,更新方式
     * @param x
     *            调整区域左上角X坐标
     * @param y
     *            调整区域左上角Y坐标
     * @param w
     *            调整区域宽度
     * @param h
     *            调整区域高度
     */
    public boolean imageUpdate(
            Image img,
            int flags,
            int x,
            int y,
            int w,
            int h) {
        // 如果是把原先图片覆盖掉(只支持这种更新方式)
        if ((flags & (FRAMEBITS | ALLBITS)) != 0) {
            // 在该列头上重绘
            Rectangle rect = header.getHeaderRect(col);
            header.repaint(rect);
        }
        // 返回更新成功标志
        return (flags & (ALLBITS | ABORT)) == 0;
    }
}
