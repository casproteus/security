package org.cas.client.platform.pimview.pimtable;

import java.awt.Component;
import java.awt.Insets;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;

/**
 * 用于表格头部的绘制
 */

class SortButtonRenderer extends JButton implements IPIMCellRenderer {
    /** 表示在表格视图第一次show出来时,此时没有向上或向下箭头 */
    public static final int NONE = 0;
    /** 表示向下箭头 */
    public static final int DOWN = 1;
    /** 表示向上箭头 */
    public static final int UP = 2;

    /** 保存表格中处于排序的列 */
    private int pushedColumn;
    /** 保存一个SortButtonRenderer实例的状态 */
    private Hashtable state;

    /**
     * 保存一个向下按钮,提供给绘制器
     */
    private JButton downButton;

    /**
     * 保存一个向上按钮,提供给绘制器
     */
    private JButton upButton;

    /**
     * 创建一个SortButtonRenderer的实例
     */
    SortButtonRenderer() {
        pushedColumn = -1;
        state = new Hashtable(); // 创建state的实例

        // 设置一些基本参数
        setUI(new BasicSortButtonUI());
        setMargin(new Insets(0, 0, 0, 0)); // 边缘空白
        setHorizontalTextPosition(LEFT); // 水平对齐方式
        setIcon(new BlankIcon());
        setFont(CustOpts.custOps.getFontOfTableHeader());

        // 对向下箭头按钮
        downButton = new JButton();
        downButton.setUI(new BasicSortButtonUI());
        downButton.setFont(getFont());
        // 边缘空白
        downButton.setMargin(new Insets(0, 0, 0, 0));
        // 水平对齐方式
        downButton.setHorizontalTextPosition(LEFT);
        // 设置向下箭头的图标
        downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
        // 设置按下状态的图标
        downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

        upButton = new JButton();
        upButton.setUI(new BasicSortButtonUI());
        upButton.setFont(getFont());
        // 边缘空白
        upButton.setMargin(new Insets(0, 0, 0, 0));
        // 水平对齐方式
        upButton.setHorizontalTextPosition(LEFT);
        // 设置向上箭头的图标
        upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
        // 设置按下状态的图标
        upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));
    }

    /**
     * 重载父类中的方法 ,返回绘制器组件给 PIMTable 用
     *
     * @param table
     *            <code>PIMTable</code> 表格实例
     * @param value
     *            要设置的值
     * @param isSelected
     *            表示是否选中的状态
     * @param hasFocus
     *            表示是否有焦点
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @return 绘制器
     */
    public Component getTableCellRendererComponent(
            PIMTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        JButton button = this; // ??把this赋给它岂不是太大了？
        // value为空的处理
        if (value == null) {
            button.setText(CASUtility.EMPTYSTR);
        } else {
            // 如果Cell中要显示的是图标，且非BevelArrow图标。
            if (value instanceof ImageIcon && !(value instanceof BevelArrowIcon)) {
                button.setText(CASUtility.EMPTYSTR);
                button.setIcon((ImageIcon) value);
                // 判断当前是否以当前列为排序标准(处于下压状态)
                boolean isPressed = (column == pushedColumn);
                button.getModel().setPressed(isPressed);
                button.getModel().setArmed(isPressed);
                return button;
            } else // 否则，返回的JButton将是没有图标的。
            {
                // 不设图标,但不返回
                button.getModel().setPressed(false);
                button.getModel().setArmed(false);
                button.setIcon(null);
            }
        }
        // 在Cell没有图标的情况下.
        Object obj = state.get(PIMPool.pool.getKey(column)); // 得到该列的升降序状态。
        if (obj != null) // 有升降序状态则
        {
            // 这个判断被杨志南注掉,因为在列宽变化后,会影响排序状态的显示
            // if (getCursor() != BasicPIMTableHeaderUI.resizeCursor) //如果光标是resize形状。
            // {
            if (((Integer) obj).intValue() == DOWN) // ??这种赋值语句是否应该提前，以减少两次赋值。
            {
                button = downButton;
                // button.setText(value.toString());
            } else {
                button = upButton;
                // button.setText(value.toString());
            }
            // }
        }

        if (value != null) {
            button.setText(value.toString());
        }

        // boolean isPressed = (column == pushedColumn);
        // 这两句被user008注掉，因为认为不需要执行。
        // button.getModel().setPressed(isPressed);
        // button.getModel().setArmed(isPressed);

        // 处理宽度过小的问题 事实上在此处无法处理
        // if(button.getGraphics() != null && button != this)
        // {
        // Graphics g = button.getGraphics();
        // Insets insets = button.getInsets(paintViewInsets);
        //
        // Icon icon = button.getIcon();
        // String text = button.getText();
        //
        // paintViewR.x = insets.left;
        // paintViewR.y = insets.top;
        // paintViewR.width = button.getWidth() - (insets.left + insets.right);
        // paintViewR.height = button.getHeight() - (insets.top + insets.bottom);
        // paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
        // paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;
        //
        // String showText = layoutCL(button, fm, text, icon, paintViewR, paintIconR, paintTextR);
        // int textWidth = (int)fm.getStringBounds(text,g).getWidth();
        // int minRequestWidth = button.getWidth() - (insets.left + insets.right + icon.getIconWidth());
        //
        // }
        return button;
    }

    // /** 本方法的目的是用来得知文本是否足够放下,在文本不够放时,最后显示三个小数点
    // * 在这种情况下,我们不绘制升降箭头
    // * 抄JDK的
    // * @param button 按钮
    // * @param fontMetrics 公制字体
    // * @param text 原文本
    // * @param icon 图标
    // * @param viewR 可视的矩形
    // * @param iconR 图标矩形
    // * @param textR 文本矩形
    // * @return 应显示的文字
    // */
    // private String layoutCL(JButton button, FontMetrics fontMetrics, String text,
    // Icon icon, Rectangle viewR, Rectangle iconR, Rectangle textR)
    // {
    // //SwingUtilities 的这个方法对所有矩形参数进行处理,里面的参数将会是实际的大小
    // return SwingUtilities.layoutCompoundLabel((JComponent) button,fontMetrics,
    // text,icon,button.getVerticalAlignment(),button.getHorizontalAlignment(),
    // button.getVerticalTextPosition(),button.getHorizontalTextPosition(),
    // viewR,iconR,textR,button.getIconTextGap());
    // }

    /**
     * 设置被按下的那一列的索引
     * 
     * @Called by: HeaderListener;
     * @param col
     *            所在列
     */
    void setPressedColumn(
            int col) {
        pushedColumn = col;
    }

    /**
     * 设置当前按照哪一列排序。
     * 
     * @Called by: HeaderListener;
     * @param col
     *            所在列
     */
    void setSelectedColumn(
            int col) {
        if (col < 0) // 除错------------------------------------------------------------
        {
            return;
        }

        Integer value = null; // 声明用于
        Object obj = state.get(PIMPool.pool.getKey(col)); // 从哈希表中得到该列对应的升、降序标志。

        if (obj == null) // 如果为空，则默认是降序。
        {
            value = PIMPool.pool.getKey(DOWN);
        } else {
            if (((Integer) obj).intValue() == DOWN) {
                value = PIMPool.pool.getKey(UP);
            } else {
                value = PIMPool.pool.getKey(DOWN);
            }
        } // 至此得到该列是升序的还是降序的。---------------------------------------------------
        state.clear(); // NOTE:此处可能是暂时写法，为何用一个hashTable存一个boolean标志？
        state.put(PIMPool.pool.getKey(col), value);
    }

    /**
     * 设置排序列。用于表格的初始化,多种排序状态
     * 
     * @Called by: PIMTable;
     * @param col
     *            所在列
     * @param isAscent
     *            是否升序
     */
    void setSelectedColumn(
            int col,
            boolean isAscent) {
        if (col < 0) // 除错------------------------------------------------------------
        {
            return;
        }
        int tmpIsAscent = isAscent ? UP : DOWN;
        Integer value = PIMPool.pool.getKey(tmpIsAscent);
        state.put(PIMPool.pool.getKey(col), value);
    }

    /**
     * 清除标志
     * 
     * @Called by: PIMTable;
     */
    void clearState() {
        state.clear();
    }

    /**
     * 返回传入列的升降序状态值。
     * 
     * @Called by: HeaderListener;
     * @return 升降序状态
     * @param col
     *            所在列
     */
    int getState(
            int col) {
        int retValue;
        // 从哈希表中得到该列对应的升、降序标志。
        Object obj = state.get(PIMPool.pool.getKey(col));
        // 为空的处理
        if (obj == null) {
            retValue = NONE;
        } else {
            // 向下
            if (((Integer) obj).intValue() == DOWN) {
                retValue = DOWN;
            } else
            // 向下
            {
                retValue = UP;
            }
        }
        return retValue;
    }
}
