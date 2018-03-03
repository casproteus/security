package org.cas.client.platform.casutil;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.BreakIterator;

import javax.swing.JLabel;

import org.cas.client.platform.cascustomize.CustOpts;

public class ErrorLabel extends JLabel {

    /** Creates new ErrorLabel */
    public ErrorLabel(String prmMes) {
        string = prmMes;
        metrics = getFontMetrics(CustOpts.custOps.getFontOfDefault());
    }

    /** paint the contents text. */
    public void paintComponent(
            Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int y = getFontHeight();
        int h = getFontHeight();
        int j = 0;
        int stringLength = string.length();
        String str[] = new String[stringLength];
        for (j = 0; j < stringLength; j++) {
            str[j] = string.substring(j, j + 1);
        }
        int x = 0;
        for (int i = 0; i < stringLength; i++) {
            if (str[i].equals("\n")) {
                x = 0;
                y += h;
                continue;
            }
            g2d.drawString(str[i], x, y);
            x += metrics.stringWidth(str[i]);
        }
    }

    private int getFontHeight() {
        int ascent, descent, leading, charHeight;
        ascent = metrics.getAscent();
        descent = metrics.getDescent();
        leading = metrics.getLeading();
        charHeight = ascent + descent + leading;
        return charHeight;
    }

    /**
     * 该方法从ErrorDialog中提出公共使用。 给定一个字符串和一个切割的宽度，会返回切割后加回车的字符串。
     * 
     * @param str
     *            供切割的字符串。
     * @param metrics
     *            字体的属性。
     * @param cut
     *            切割的宽度。
     * @return 切割后的字符串。
     */
    public static String getAreaContents(
            String str,
            FontMetrics metrics,
            int cut) {
        String sub = "";
        String tempSub = str;
        int length = str.length();
        String string = "";
        // boolean isCutted = false;
        while (sub.length() <= length) {
            String tempString = ErrorLabel.getBreakString(tempSub, metrics, cut);
            if (tempString == null) {
                break;
            }
            sub = sub.concat(tempString);
            int temp = sub.length();
            if (!ErrorLabel.hasEnter()) {
                string = string.concat(tempString).concat("\n");
            } else {
                string = string.concat(tempString);
            }
            tempSub = str.substring(temp, length);
            if (tempSub.length() <= 0) {
                break;
            }
        }
        return string;
    }

    /* 根据传进来的字符串、宽度、字体属性找到一个恰当换行的子字符串 */
    public static String getBreakString(
            String str,
            FontMetrics metric,
            int width) {
        int boundedPos = findBoundedPos(str, metric, width);
        int breakPos = findBreakPos(str, boundedPos);
        if (breakPos != 0) {
            return str.substring(0, breakPos);
        } else {
            return null;
        }
    }

    /* 找到传进来的宽度对应的字符位置 */
    private static int findBoundedPos(
            String str,
            FontMetrics metric,
            int width) {
        int currX = 0, nextX = 0;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            nextX += metric.charWidth(str.charAt(i));
            if (width >= currX && width < nextX) {
                if ((width - currX) < (nextX - width)) {
                    return i;
                } else {
                    return i + 1;
                }
            }
            currX = nextX;
        }
        // didn't find, return end offset
        return length;
    }

    /* 根据字符位置寻找合适的断行位置 */
    private static int findBreakPos(
            String str,
            int boundedPos) {
        iterator.setText(str);
        int pos;
        for (int p = boundedPos - 1; p > 0; p--) {
            if (str.charAt(p) == '\n') {
                hasEnter = true;
                return p;
            }
        }
        hasEnter = false;
        char boundedChar1 = (boundedPos - 1) > 0 ? str.charAt(boundedPos - 1) : (char) -1;
        char boundedChar2 = boundedPos < str.length() ? str.charAt(boundedPos) : (char) -1;
        if (CustOpts.OS != 0) {
            if (boundedChar1 == ' ' || boundedChar2 == ' ') {
                if (boundedPos == iterator.last()) {
                    return boundedPos;
                }
                // 边界位置处于一个空格处,根据微软规则有可能断行位置在最后一个连续空格处
                pos = iterator.preceding(boundedPos);
                pos = iterator.following(pos);
            } else if (boundedChar1 == '\n' || boundedChar1 == '\r' || isSpecial(boundedChar2)) {// 边界位置处于一个回车处,直接返回这个位置
                if (isSpecial(boundedChar2)) {
                    isSpecial = true;
                    pos = boundedPos + 1;
                } else {
                    isSpecial = false;
                    pos = boundedPos;
                }
            } else {
                if (boundedPos == iterator.last()) {
                    return boundedPos;
                }
                int offset = boundedPos < iterator.last() ? boundedPos + 1 : boundedPos;
                if (offset >= str.length()) {
                    return boundedPos;
                }
                pos = iterator.preceding(offset);
            }
        } else {
            if (boundedChar1 == ' ' || boundedChar2 == ' ') {
                if (boundedPos == iterator.last()) {
                    return boundedPos;
                }
                // 边界位置处于一个空格处,根据微软规则有可能断行位置在最后一个连续空格处
                pos = iterator.preceding(boundedPos);
                pos = iterator.following(pos);
            } else if (boundedChar1 == '\n' || boundedChar1 == '\r' || isSpecial(boundedChar2)) {
                // 边界位置处于一个回车处,直接返回这个位置
                if (isSpecial(boundedChar2)) {
                    isSpecial = true;
                    pos = boundedPos + 1;
                } else {
                    isSpecial = false;
                    pos = boundedPos;
                }
            } else {
                if (boundedPos == iterator.last()) {
                    return boundedPos;
                }
                int offset = boundedPos < iterator.last() ? boundedPos + 1 : boundedPos;
                if (offset >= str.length()) {
                    return boundedPos;
                }
                pos = iterator.preceding(offset);
            }
        }
        if (pos == 0) {
            pos = boundedPos;
        }
        return pos;
    }

    private static boolean isSpecial(
            char c) {
        if (c == '\uff0c' || c == '\u3002' || c == '\u201d' || c == '\uff1a' || c == '\uff1b' || c == '\uff09'
                || c == '\uff1f' || c == '\u2019' || c == '\u3001' || c == ',' || c == '\u201c' || c == '.' || c == ':'
                || c == ';' || c == '\'' || c == '"' || c == ')' || c == '?' || c == '/') {
            return true;
        }
        /** End of Debug */
        return false;
    }

    /**
     * 该方法返回该内容中是否含有回车符。
     * 
     * @return true:含有回车符。 false:不含有回车符。
     */
    public static boolean hasEnter() {
        return hasEnter;
    }

    /**
     * 该方法判断是否含有特殊字符。
     * 
     * @return true:含有特殊字符。 false:不含有特殊字符。
     */
    public static boolean isSpecial() {
        return isSpecial;
    }

    private static BreakIterator iterator = BreakIterator.getLineInstance();
    private static boolean hasEnter;
    private static boolean isSpecial;
    private String string;
    private FontMetrics metrics;
}
