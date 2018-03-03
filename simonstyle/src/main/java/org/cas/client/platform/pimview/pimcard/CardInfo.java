package org.cas.client.platform.pimview.pimcard;

import java.awt.Font;
import java.io.Serializable;

public class CardInfo implements Cloneable, Serializable {
    /**
     * 缺省构造器
     */
    public CardInfo() {
    }

    /**
     * 是否显示空字段
     * 
     * @return boolean
     */
    public boolean isShowEmptyField() {
        return showEmptyField;
    }

    /**
     * 设置是否显示空字段
     * 
     * @param b
     *            是否显示
     */
    public void setShowEmptyField(
            boolean b) {
        showEmptyField = b;
    }

    /**
     * 卡片标题字体
     * 
     * @return Font
     */
    public Font getTitleFont() {
        return titleFont;
    }

    /**
     * 卡片标题字体
     * 
     * @param f
     *            字体
     */
    public void setTitleFont(
            Font f) {
        this.titleFont = f;
    }

    /**
     * 卡片正文字体
     * 
     * @return Font
     */
    public Font getTextFont() {
        return textFont;
    }

    /**
     * 卡片正文字体
     * 
     * @param f
     *            字体
     */
    public void setTextFont(
            Font f) {
        this.textFont = f;
    }

    /**
     * 卡片宽度，用字符个数表示
     * 
     * @return int
     */
    public int getCardWidth() {
        return cardWidth;
    }

    /**
     * 卡片宽度，用字符个数表示
     * 
     * @param w
     *            卡片宽度
     */
    public void setCardWidth(
            int w) {
        this.cardWidth = w;
    }

    /**
     * 得到卡片中显示的字段的索引
     * 
     * @return 显示的字段的索引
     */
    public int[] getDisplayIndexes() {
        return displayIndexes;
    }

    /**
     * 设置卡片中显示的字段的索引
     * 
     * @param ary
     *            卡片中显示的字段的索引
     */
    public void setDisplayIndexes(
            int[] ary) {
        this.displayIndexes = ary;
    }

    /**
     * 得到卡片视图显示字段的字段名
     * 
     * @return 卡片视图显示字段的字段名数组
     */
    public String[] getDisplayFieldNames() {
        return displayFieldNames;
    }

    /**
     * 设置卡片视图显示字段的字段名
     * 
     * @param array
     *            卡片视图显示字段的字段名
     */
    public void setDisplayFieldNames(
            String[] array) {
        this.displayFieldNames = array;
    }

    private int[] displayIndexes;
    private String[] displayFieldNames;
    // 30个字符
    private int cardWidth = 30;
    private boolean showEmptyField = true;
    private Font titleFont = new Font("宋体", Font.PLAIN, 12);
    private Font textFont = new Font("宋体", Font.PLAIN, 14);
}
