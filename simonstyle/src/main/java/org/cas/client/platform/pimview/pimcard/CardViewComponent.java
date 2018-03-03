package org.cas.client.platform.pimview.pimcard;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.resource.international.PIMTableConstants;
import org.cas.client.resource.international.PIMTableRendererConstant;

public class CardViewComponent extends JComponent implements ActionListener {
    /**
     * Creates a new instance of CardViewComponent
     * 
     * @param info
     *            视图信息
     * @param record
     *            数据内容
     */
    public CardViewComponent(CardInfo info, Object[] record) {
        this.cardInfo = info;
        this.record = record;

        createCardInfo();

        displayIndexes = cardInfo.getDisplayIndexes();
        displayFieldNames = cardInfo.getDisplayFieldNames();
        if (displayIndexes != null) {
            length = displayIndexes.length;
        }
        displayValues = new Vector();
        for (int i = 0; i < length; i++) {
            displayValues.addElement(record[displayIndexes[i]]);
        }
        // 目前是联系人
        if (record != null) {
            displayAs = (String) record[ModelDBCons.CAPTION];
        }

        calcWidthAndHeight();

        if (isSelected()) {
            setBorder(lineBorder);
        }
    }

    /**
     * 计算宽高
     */
    public void calcWidthAndHeight() {
        FontMetrics titleFontMetrics = getFontMetrics(cardInfo.getTitleFont());
        int titleCharWidth = titleFontMetrics.charWidth('W');
        titleHeight = titleFontMetrics.getHeight();

        FontMetrics textFontMetrics = getFontMetrics(cardInfo.getTextFont());
        int textCharWidth = textFontMetrics.charWidth('W');
        textHeight = textFontMetrics.getHeight();

        int maxCharWidth = titleCharWidth > textCharWidth ? titleCharWidth : textCharWidth;
        cardWidth = cardInfo.getCardWidth() * maxCharWidth;

        if (cardInfo.isShowEmptyField()) {
            cardHeight = titleHeight + textHeight * length;
        } else {
            Object temp = null;
            cardHeight = titleHeight;
            for (int i = 0; i < length; i++) {
                temp = displayValues.elementAt(i);
                // displayIndexes
                if (temp != null && temp.toString().trim().length() != 0) {
                    if (getStringValue(displayIndexes[i], temp) != null) {
                        cardHeight += textHeight;
                    }
                }
            }
        }
    }

    /**
     * 绘制
     * 
     * @param g
     *            图形设备
     */
    public void paintComponent(
            Graphics g) {
        super.paintComponent(g);
        // 如果显示为字段是空,这张卡片根本就不存在,无需处理.
        // 2003.9.29
        if (displayAs == null) {
            return;
        }

        // Graphics2D g2 = (Graphics2D)g;
        // g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        Color oldColor = g.getColor();

        if (selected) {
            g.setColor(seleColor);
        } else {
            g.setColor(Color.lightGray);
        }
        g.fillRect(0, 0, width, titleHeight);

        // 判断选中
        if (selected) {
            g.setColor(Color.white);
        } else {
            g.setColor(Color.black);
        }
        // 绘制显示为
        g.setFont(cardInfo.getTitleFont());
        g.drawString(displayAs, 2, titleHeight - 3);

        // 填充背景色
        g.setColor(Color.white);
        g.fillRect(0, titleHeight, width, height - titleHeight);
        g.setColor(Color.black);

        int dx = 2;
        int dy = titleHeight;
        String value = null;
        g.setFont(cardInfo.getTextFont());
        for (int i = 1; i < length; i++) {
            value = (String) displayValues.elementAt(i);
            if (displayIndexes[i] == ModelDBCons.CAPTION) {
                continue;
            }
            if (cardInfo.isShowEmptyField()) {
                dy += textHeight;
                g.drawString(displayFieldNames[i], dx, dy);
                dx += getMaximumLengthInFieldNames();
                if (value == null) {
                    value = CASUtility.EMPTYSTR;
                }
                g.drawString(value, dx, dy);
            } else {
                if (value != null && value.length() != 0) {
                    String tmpValue = getStringValue(displayIndexes[i], value);
                    if (tmpValue != null) {
                        dy += textHeight;
                        g.drawString(displayFieldNames[i], dx, dy);
                        dx += getMaximumLengthInFieldNames();
                        g.drawString(tmpValue, dx, dy);
                    }
                }
            }

            dx = 2;
        }

        g.setColor(oldColor);
    }

    /**
     * 创建卡片视图信息
     */
    protected void createCardInfo() {
        if (cardInfo == null) {
            cardInfo = new CardInfo();
        }
    }

    /**
     * 获得记录的ID
     * 
     * @return int
     */
    public int getRecordID() {
        return Integer.parseInt(displayValues.elementAt(0).toString());
    }

    /**
     * 判断是否被选中
     * 
     * @return boolean
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置选中状态
     * 
     * @param b
     *            是否选中
     */
    public void setSelected(
            boolean b) {
        selected = b;
        if (selected) {
            setBorder(lineBorder);
        } else {
            setBorder(null);
        }
        repaint();
    }

    /**
     * 获得卡片视图信息
     * 
     * @return CardInfo
     */
    public CardInfo getCardInfo() {
        return cardInfo;
    }

    /**
     * 设置卡片视图信息
     * 
     * @param cardInfo
     *            卡片视图信息
     */
    public void setCardInfo(
            CardInfo cardInfo) {
        this.cardInfo = cardInfo;
        calcWidthAndHeight();
        repaint();
    }

    /**
     * 获得卡片的宽度
     * 
     * @return int
     */
    public int getCardWidth() {
        return cardWidth;
    }

    /**
     * 获得卡片的高度
     * 
     * @return int
     */
    public int getCardHeight() {
        return cardHeight;
    }

    /**
     * 计算所有字段名中的最大长度
     * 
     * @return int
     */
    public int getMaximumLengthInFieldNames() {
        FontMetrics textFontMetrics = getFontMetrics(cardInfo.getTextFont());
        int textCharWidth = 0;
        for (int i = 0; i < length; i++) {
            int temp = textFontMetrics.stringWidth(displayFieldNames[i]);
            if (temp > textCharWidth) {
                textCharWidth = temp;
            }
        }

        return textCharWidth + 4;
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
    }

    /**
     * Getter for property cardIndex.
     * 
     * @return Value of property cardIndex.
     */
    public int getCardIndex() {
        return this.cardIndex;
    }

    /**
     * Setter for property cardIndex.
     * 
     * @param cardIndex
     *            New value of property cardIndex.
     */
    public void setCardIndex(
            int cardIndex) {
        this.cardIndex = cardIndex;
    }

    /**
     * Getter for property cardRecord.
     * 
     * @return Value of property cardRecord.
     */
    public Object[] getCardRecord() {
        return this.record;
    }

    /**
     *
     */
    private String getStringValue(
            int id,
            Object prmValue) {
        // 标记状态
        if (id == ModelDBCons.FLAGSTATUS) {
            if (prmValue == null || prmValue.toString().length() == 0) {
                return null;
            } else {
                String str = prmValue.toString();
                if (str.equals("1")) {
                    return PIMTableConstants.FLAGS_STATUS_CONSTANTS[1];
                } else if (str.equals("2")) {
                    return PIMTableConstants.FLAGS_STATUS_CONSTANTS[2];
                } else {
                    return PIMTableConstants.FLAGS_STATUS_CONSTANTS[0];
                }
            }
        } else if (id == ContactDefaultViews.SEX) {
            if (prmValue == null || prmValue.toString().length() == 0)
                return null;
            else {
                String str = prmValue.toString();
                if (str.equals("1"))
                    return PIMTableConstants.SEX_ITEMS[1];
                else if (str.equals("2"))
                    return PIMTableConstants.SEX_ITEMS[2];
                else
                    return PIMTableConstants.SEX_ITEMS[0];
            }
        } else if (id == ContactDefaultViews.PHOTO) {
            if (prmValue == null || prmValue.toString().length() == 0)
                return null;
            else
                return PIMTableConstants.HAVE;
        } else if (id == ContactDefaultViews.BIRTHDAY || id == ContactDefaultViews.ANNIVERSARY
                || id == ModelDBCons.FOLOWUPENDTIME) {
            if (prmValue != null) {
                Date tmpDate = (Date) prmValue;
                String dateStr =
                        new StringBuffer().append(tmpDate.getYear() + 1900).append('-').append(tmpDate.getMonth() + 1)
                                .append('-').append(tmpDate.getDate()).append(" (")
                                .append(PIMTableRendererConstant.WEEKDAYS[tmpDate.getDay()]).append(')').toString();
                return dateStr;
            } else
                return null;
        }
        // 联系人字段,不予处理
        // else if(id == ModelDBConstants.CONTACTORS)
        // {
        // }
        // TODO:通讯组列表,暂不显示
        else if (id == ContactDefaultViews.MEMBERLIST)
            return null; // dlg.getContents().getFieldValue(ModelDBConstants.CAPTION);
        // 类型
        else if (id == ContactDefaultViews.TYPE) {
            if (prmValue == null || prmValue.toString().length() == 0)
                return null;
            else {
                String str = prmValue.toString();
                if (str.equals("1"))
                    return PIMTableConstants.COMMUNICATION_GROUP;
                else
                    return PIMTableConstants.CONTACT;
            }
        }
        // 所在文件夹,暂时不处理
        // else if (id == ModelDBConstants.INFOLDER)
        // return null; //PIMTableConstants.CONTACT;
        else if (id == ModelDBCons.ICON)
            return null;
        return prmValue.toString();
    }

    private int cardIndex = -1;
    private int titleHeight;
    private int textHeight;
    private int cardWidth;
    private int cardHeight;
    private int length;
    private String displayAs;
    private Vector displayValues;
    private int[] displayIndexes;
    private String[] displayFieldNames;
    private Object[] record;
    private CardInfo cardInfo;
    private boolean selected;
    Color seleColor = new Color(0, 97, 57); // TODO:出厂前改为从CustomOption中取所附属的appPane的对应的标签的背景色.
    private Border lineBorder = new LineBorder(seleColor);

    // TODO:以后要增加两个数组来保存真正可显示的字段名和字符串,
}
