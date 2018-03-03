package org.cas.client.platform.casutil;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * 定义缓冲区，final类。
 */
public final class PIMPool {
    /* enter string */
    public static final String ENTER_STRING = "\r\n";

    /* boolean's true string */
    public static final String BOOLEAN_TRUE = "true";

    /* boolean's false string */
    public static final String BOOLEAN_FALSE = "false";

    /* integer type string */
    public static final String INTEGER_TYPE = "Integer:";

    /* boolean type string */
    public static final String BOOLEAN_TYPE = "Boolean:";

    /* string type string */
    public static final String STRING_TYPE = "String:";
    // --------------------------------------------------------------------------
    public static PIMPool pool = new PIMPool();

    /**
     * 根据路径获得图片 @NOTE:图片有可能是被打到jar包里的.
     * 
     * @param prmPath
     *            <code>String</code>路径
     * @return Image
     * @see java.awt.Image
     */
    public Image getImage(
            String prmPath) {
        Object temImage = imagePool.get(prmPath); // 从池中取出对应对象。
        if (temImage == null) { // 如果池中尚无对应对象，则新建一个，并加入池中。
            URL tmpURL = getClass().getResource(prmPath);
            if (tmpURL == null)
                return null;
            try {
                temImage = Toolkit.getDefaultToolkit().getImage(tmpURL);
            } catch (Exception e) {
                return null;
            }
            imagePool.put(prmPath, temImage);
        }
        return (Image) temImage;
    }

    /**
     * 根据路径获得图片
     * 
     * @param prmPath
     *            <code>String</code>路径
     * @return Image
     * @see java.awt.Image
     */
    public Icon getIcon(
            String prmPath) {
        Object temIcon = iconPool.get(prmPath); // 从池中取出对应对象。
        if (temIcon == null) // 如果池中尚无对应对象，
        { // 则新建一个，并加入池中。
            Image tmpImg = getImage(prmPath);
            if (tmpImg != null) {
                temIcon = new ImageIcon(tmpImg);
                iconPool.put(prmPath, temIcon);
            }
        }
        return (Icon) temIcon;
    }

    /**
     * called by CustOptions; 注意：此方法内为节约时间，再使用强制类型转换前未进行类型检查。所以 如果一定不可往hashTable放Color以外的对象。
     * 
     * @param prmRed
     *            <code>int</code>红色
     * @param prmGreen
     *            <code>int</code>绿色
     * @param prmBlue
     *            <code>int</code>蓝色
     * @return Color
     * @see java.awt.Color
     */
    public Color getColor(
            int prmRed,
            int prmGreen,
            int prmBlue) {
        String temKey = stringBuffer.append(prmRed).append('.').append(prmGreen).append('.').append(prmBlue).toString();
        Object temColor = colorPool.get(temKey); // 从池中取出对应对象。
        if (temColor == null) // 如果池中尚无对应对象，
        { // 则新建一个，并加入池中。
            temColor = new Color(prmRed, prmGreen, prmBlue);
            colorPool.put(temKey, temColor);
        }
        stringBuffer.delete(0, stringBuffer.length());
        return (Color) temColor;
    }

    /**
     * called by CustOptions; 注意：此方法内为节约时间，再使用强制类型转换前未进行类型检查。所以 如果一定不可往hashTable放Color以外的对象。
     * 
     * @param prmFontName
     *            <code>String</code>字体名
     * @param prmType
     *            <code>int</code>字体类型
     * @param prmSize
     *            <code>int</code>字体大小
     * @return Font
     * @see java.awt.Font
     */
    public Font getFont(
            String prmFontName,
            int prmType,
            int prmSize) {
        String temKey = stringBuffer.append(prmFontName).append(prmType).append(prmSize).toString();

        Object temFont = fontPool.get(temKey); // 从池中取出对应对象。
        if (temFont == null) // 如果池中尚无对应对象，
        { // 则新建一个，并加入池中。
            temFont = new Font(prmFontName, prmType, prmSize);
            fontPool.put(temKey, temFont);
        }
        stringBuffer.delete(0, stringBuffer.length());
        return (Font) temFont;
    }

    public Integer getIntegerKey(
            String prmKey) {
        return getKey(Integer.parseInt(prmKey));
    }

    public Integer getKey(
            int prmKey) {
        if (prmKey >= 0 && prmKey < ModelDBCons.MAXKEYVALUE) {
            return keyPool[prmKey] == null ? (keyPool[prmKey] = new Integer(prmKey)) : keyPool[prmKey];
        } else {
            return new Integer(prmKey);
        }
    }

    // 菜单的缓冲能力-------------------------------------------------------
    public JMenu getAMenu() {
        JMenu result; // 检查缓冲区是否空
        if (menuIndex == 0) {
            result = new JMenu(); // 如果缓冲区空，创建一个新的对象
        } else {
            result = freeStackMenu[--menuIndex]; // 从缓冲区末尾移出一个对象
        }
        return result;
    }

    public JMenuItem getAMenuItem() {
        JMenuItem result; // 检查缓冲区是否空
        if (menuItemIndex == 0) {
            result = new JMenuItem(); // 如果缓冲区空，创建一个新的对象
        } else {
            result = freeStackMenu[--menuItemIndex]; // 从缓冲区末尾移出一个对象
        }
        return result;
    }

    public void keepTheJMenu(
            JMenu prmMenu) {
        if (menuIndex < freeStackMenu.length) // 如果缓冲池没有满，则存入缓冲池中。
        {
            freeStackMenu[menuIndex++] = prmMenu;
        }
    }

    public void keepTheJMenuItem(
            JMenuItem prmMenuItem) {
        if (menuItemIndex < freeStackMenuItem.length)// 如果缓冲池没有满，则存入缓冲池中。
        {
            freeStackMenuItem[menuItemIndex++] = prmMenuItem;
        }
    }

    // 下面四个变量用于支持菜单的缓冲能力
    private final JMenu[] freeStackMenu = new JMenu[10];
    private final JMenuItem[] freeStackMenuItem = new JMenuItem[30];
    private int menuIndex;
    private int menuItemIndex;

    private StringBuffer stringBuffer = new StringBuffer();
    private Hashtable imagePool = new Hashtable();
    private Hashtable iconPool = new Hashtable();
    private Hashtable colorPool = new Hashtable();
    private Hashtable fontPool = new Hashtable();
    private Integer[] keyPool = new Integer[ModelDBCons.MAXKEYVALUE]; // 用于共享所有的key值
}
