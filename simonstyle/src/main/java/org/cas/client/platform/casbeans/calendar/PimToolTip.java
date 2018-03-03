package org.cas.client.platform.casbeans.calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * 该类提供日期选择区中日期部分的面板。
 */

class PimToolTip extends JToolTip {
    /**
     * 构造器
     */
    public PimToolTip() {
        super();
        setBorder(border);
    }

    // /**
    // * 提供绘制方法，将已经切割好的string绘制
    // * @see getPerferredSize 此方法中将tooltip的string切割
    // */
    // public void paintComponent(Graphics g)
    // {
    // g.setColor(UIConstants.TOOLTIP_BACKCOLOR);
    // g.fillRect(0, 0, getWidth(), getHeight());
    // g.setColor(UIConstants.TOOLTIP_FONTCOLOR);
    // if (strs != null)
    // {
    // FontMetrics metrics = getFontMetrics(getFont());
    // int metHeight = metrics.getHeight();
    // int metAscent = metrics.getAscent() + 1;
    // for (int i = 0; i < line; i++)
    // {
    // g.drawString(strs[i], MARGIN, metHeight * i + metAscent);
    // }
    // }
    // }
    /**
     * 重载父类方法，返回适合文字的大小
     *
     * @return 取得ToolTip的大小,同时切割需要绘制的string段
     */
    public Dimension getPreferredSize() {
        FontMetrics metrics = getFontMetrics(getFont());
        String tipText = getTipText();
        if (tipText == null) {
            return new Dimension(0, 0);
        }
        char[] charArr = tipText.toCharArray();
        int len = charArr.length;
        int count = 0;
        for (int i = 0; i < len; i++) {
            if (charArr[i] == '\n') {
                count++;
            }
        }
        // 如果最后一个字符是'\n'符号，应将其省略
        if (charArr[len - 1] != '\n') {
            count++;
        }
        line = count;
        strs = new String[line];
        int start = 0;
        int index = 0;
        for (int i = 0; i < len; i++) {
            if (charArr[i] == '\n') {
                strs[index] = tipText.substring(start, i);
                index++;
                // 跳一个指针，让过'\n'，在绘制时不包括回车符号
                start = i + 1;
            }
        }
        if (start != len) {
            strs[index] = tipText.substring(start, len);
        }
        int maxWidth = 0;
        for (int i = 0; i < line; i++) {
            int width = SwingUtilities.computeStringWidth(metrics, strs[i]);
            maxWidth = maxWidth > width ? maxWidth : width;
        }
        int height = metrics.getHeight() * line;
        return new Dimension(maxWidth + 2 * MARGIN, height + 2);
    }

    // //ToolTip的宽度确定，只调整高度
    // public void confirmWidth(int width)
    // {
    // }
    //
    // //ToolTip的高度确定，只调整宽度
    // public void confirmHeight(int height)
    // {
    // }
    //
    // //TooTip自适应最好的高、宽度，符合黄金分割
    // public void confirmBestSize()
    // {
    // }

    private Border border = BorderFactory.createLineBorder(Color.black, 1);
    private String[] strs;
    private int line = 0;
    private static final int MARGIN = 5;
}
