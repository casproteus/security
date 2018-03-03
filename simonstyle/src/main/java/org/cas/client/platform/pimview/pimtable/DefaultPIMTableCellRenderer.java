package org.cas.client.platform.pimview.pimtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMViewInfo;

public class DefaultPIMTableCellRenderer extends JLabel implements IPIMCellRenderer, Serializable {
    /**
     * Creates a default table cell renderer.
     */
    public DefaultPIMTableCellRenderer() {
        super();
        // setUI(new BasicRenderLabelUI());
        // 本组件是不透明的,(有自己的背景色)
        setOpaque(true);
        // 设置边框
        setBorder(noFocusBorder);
        // setFocusTraversalKeysEnabled(false);
    }

    // implements javax.swing.pimtable.IPIMTableCellRenderer
    /**
     * 实现 IPIMTableCellRenderer 接口中的方法 ,返回绘制器组件给 PIMTable 用
     *
     * @return 经格式化的组件
     * @param prmRow
     *            所在行
     * @param prmTable
     *            the <code>PIMTable</code> that is asking the renderer to draw; can be <code>null</code>
     * @param value
     *            the value of the cell to be rendered. It is up to the specific renderer to interpret and draw the
     *            value. For example, if <code>value</code> is the string PropertyName.BOOLEAN_TRUE, it could be
     *            rendered as a string or it could be rendered as a check box that is checked. <code>null</code> is a
     *            valid value
     * @param prmSelected
     *            true if the cell is to be rendered with the selection highlighted; otherwise false
     * @param prmHasFocus
     *            if true, render cell appropriately. For example, put a special border on the cell, if the cell can be
     *            edited, render in the color used to indicate editing
     * @param prmColumn
     *            the column index of the cell being drawn
     */
    public Component getTableCellRendererComponent(
            PIMTable prmTable,
            Object prmValue,
            boolean prmSelected,
            boolean prmHasFocus,
            int prmRow,
            int prmColumn) {
        hasDeleteLine = false;
        // 前背景色设置-------------------
        if (prmSelected || (prmRow == -1 && prmTable.getTableHeader().isEditing())) // 如本单元格被选中,或是快速编辑栏, 则...
        {
            super.setForeground(prmTable.getSelectionForeground()); // 以 table 中的选中前景色和选中背景色为本单元格的前景色和背景色
            super.setBackground(prmTable.getSelectionBackground());
        } else // 本单元格未被选中,则视前背景色是否为空进行处理.
        {
            super.setForeground((unselectedForeground != null) ? unselectedForeground : prmTable.getForeground()); // 如前景色不为空,将本组件前景色传给父类,否则将
                                                                                                                   // PIMTable
                                                                                                                   // 的前景色传给父类
            super.setBackground((unselectedBackground != null) ? unselectedBackground : prmTable.getBackground()); // 如背景色不为空,将本组件背景色传给父类,否则将
                                                                                                                   // PIMTable
                                                                                                                   // 的背景色传给父类
        }
        // 字体设置---------------- TODO:以后对所有的应用中的字体都要根据用户的设置做处理.
        if (prmTable.getView() != null) {
            setFont((Font) prmTable.getFontAttribute(Integer.toString(prmRow)));
            if (prmRow >= 0) {
                Boolean tmpFontAttr =
                        (Boolean) prmTable.getFontAttribute(Integer.toString(prmRow).concat("HaveStrikethrough"));
                if (tmpFontAttr == null) {
                    hasDeleteLine = false;
                } else {
                    hasDeleteLine = tmpFontAttr.booleanValue();
                }
            }
        } else {
            setFont(prmTable.getFont());
        }

        // 焦点处理--------------
        if (prmHasFocus && prmTable.getSelectionModel().isSelectedIndex(prmRow)) // 如果有焦点,从UI管理器中取缺省 Table 的边框
        { // note:后一个判断是在表格先多选再点击选中某行后会出现焦点框,这应是JDK的bug
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (prmTable.isCellEditable(prmRow, prmColumn)) // 如本单元格可被编辑,从UI管理器中取缺省 Table 的焦点前景色和焦点背景色
            {
                super.setForeground(UIManager.getColor("Table.focusCellForeground"));
                super.setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        } else // 否则设置自定义的无焦点边框
        {
            setBorder(noFocusBorder);
        }

        setValue(prmValue); // 保存本单元格的绘制对象

        // 对InBox中邮件的特殊处理---------------------------------
        if (prmTable.getView() != null) // 处理字体,暂先在这里处理前景,下划线和删除线以后再说
        {
            PIMViewInfo viewInfo = prmTable.getView().getApplication().getActiveViewInfo();
            int tmpAppType = viewInfo.getAppIndex();
            // Warining: 因视图的字体部分没有完全处理好,这个IF先不用,不然视图上会显示不正确
            // 在设置字体功能完善之后,这个IF要开启
            if (tmpAppType == ModelCons.INBOX_APP || tmpAppType == ModelCons.OUTBOX_APP
                    || tmpAppType == ModelCons.SENDED_APP || tmpAppType == ModelCons.DRAFT_APP) {
                // 处理邮件是否已读和未读的字体为正常体和粗斜体
                if (prmTable.getRowCount() > 0 && !prmSelected)// &&(tmpAppType == ModelConstants.INBOX_APP ||
                {
                    super.setForeground((Color) prmTable.getFontAttribute(Integer.toString(prmRow).concat("Color")));
                }
            }
        }

        // 对任务中任务的特殊处理----------------------
        if (prmTable.getView() != null
                && prmTable.getView().getApplication().getActiveViewInfo().getAppIndex() == ModelCons.TASK_APP) {
            // 处理下划线问题,这是处理表格的编辑而未保存状态
            if (prmTable.getToggleRow() != -1000 && prmTable.getToggleRow() == prmRow) {
                hasDeleteLine = prmTable.isDrawDeleteLine();
                if (prmTable.isDrawDeleteLine()) {
                    super.setForeground(Color.lightGray);
                }
            } else {
                // 要用model得到
                if (prmRow < 0) {
                    return this;
                }
                int tmpfinishCol = prmTable.finishColumn();

                // 这是除错
                if (tmpfinishCol <= 0) {
                    hasDeleteLine = false;
                    return this;
                }
                Object tmpFinishIndicate = prmTable.getValueAt(prmRow, tmpfinishCol);
                if (tmpFinishIndicate != null) {
                    boolean tmpFlag =
                            tmpFinishIndicate.toString().equals("1")
                                    || tmpFinishIndicate.toString().equals(PIMPool.BOOLEAN_TRUE);
                    ;
                    // 这里是来显示表格中任务记录是否是已完成的,是的话显示下划线
                    hasDeleteLine = tmpFlag;
                    if (tmpFlag) {
                        super.setForeground(Color.gray);
                    }
                    // 这里是来显示表格中任务记录是否是已完成的,是的话前景色为灰
                    // if (tmpFlag)
                    // {
                    // super.setForeground(finishedForeColor);
                    // }
                } else {
                    hasDeleteLine = false;
                }
            }
        }
        return this;
    }

    /**
     * 重载父类方法 使绘制器自身只处理文本变化事件. Overridden for performance reasons. See the <a href="#override">Implementation Note</a>
     * for more information.
     * 
     * @param propertyName
     *            属性名
     * @param oldValue
     *            旧值
     * @param newValue
     *            新值
     */
    protected void firePropertyChange(
            String propertyName,
            Object oldValue,
            Object newValue) {
        if (propertyName == "text") // Strings get interned...
        {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * 重载父类方法 使绘制器自身不再处理这些信息 Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     * 
     * @param propertyName
     *            属性名
     * @param oldValue
     *            旧值
     * @param newValue
     *            新值
     */
    public void firePropertyChange(
            String propertyName,
            boolean oldValue,
            boolean newValue) {
    }

    /**
     * 设置未选中的情况下前景色 Overrides <code>JComponent.setForeground</code> to assign the unselected-foreground color to the
     * specified color.
     *
     * @param c
     *            set the foreground color to this value
     */
    public void setForeground(
            Color c) {
        super.setForeground(c);// 调用父类方法
        unselectedForeground = c;// 自己再保存一下
    }

    /**
     * 设置设置未选中的情况下背景色 Overrides <code>JComponent.setBackground</code> to assign the unselected-background color to the
     * specified color.
     *
     * @param c
     *            set the background color to this value
     */
    public void setBackground(
            Color c) {
        super.setBackground(c); // 调用父类方法
        unselectedBackground = c;// 自己再保存一下
    }

    /**
     * 通知UI管理器:用户界面观感发生变化 Notification from the <code>UIManager</code> that the look and feel [L&F] has changed.
     * Replaces the current UI object with the latest version from the <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        super.updateUI(); // 调用父类方法
        setForeground(null); // 不设前景和背景
        setBackground(null);
    }

    /*
     * The following methods are overridden as a performance measure to prune code-paths are often called in the case of
     * renders but which we know are unnecessary. Great care should be taken when writing your own renderer to weigh the
     * benefits and drawbacks of overriding methods like these.
     */
    /**
     * 重载了父类的方法 Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more
     * information.
     * 
     * @return 是否透明
     */
    public boolean isOpaque() {
        Color back = getBackground(); // 保存现有背景色
        Component p = getParent();// 得到父组件
        if (p != null)// 如有父类就再去找他爷爷
        {
            p = p.getParent();
        }
        boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground()) && p.isOpaque();
        // 这样就判断 p 是 PIMTable? p should now be the PIMTable.
        return !colorMatch && super.isOpaque();
    }

    /**
     * 重载方法 写绘制器的例行代码 Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more
     * information.
     */
    public void validate() {
    }

    /**
     * 重载方法 写绘制器的例行代码 Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more
     * information.
     */
    public void revalidate() {
    }

    /**
     * 重载父类方法 使绘制器自身不再处理这些信息 Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     * 
     * @param tm
     *            时间
     * @param x
     *            X坐标
     * @param y
     *            Y坐标
     * @param width
     *            宽度
     * @param height
     *            高度
     */
    public void repaint(
            long tm,
            int x,
            int y,
            int width,
            int height) {
    }

    /**
     * 重载父类方法 使绘制器自身不再处理这些信息 Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for
     * more information.
     * 
     * @param r
     *            矩形
     */
    public void repaint(
            Rectangle r) {
    }

    /**
     * 重载父类方法
     * 
     * @param g
     *            图形设备
     */
    public void paint(
            Graphics g) {
        super.paint(g);
        if (hasDeleteLine || hasUnderLine)// 在这里给几个矩形设置几个初始值.
        {
            Insets insets = getInsets(paintViewInsets);
            paintViewR.x = insets.left;
            paintViewR.y = insets.top;
            paintViewR.width = getWidth() - (insets.left + insets.right);
            paintViewR.height = getHeight() - (insets.top + insets.bottom);
            paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
            paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

            String text = getText(); // 在这里我们所需的文字的矩形区域会得到正确处理,以便在上面绘制下划线和删除线
            layoutCL(this, getFontMetrics(g.getFont()), text, null, paintViewR, paintIconR, paintTextR);
        }
        if (hasDeleteLine)// 绘制删除线
        {
            paintDeleteLine(g, paintTextR);
        }
        if (hasUnderLine)// 绘制下划线
        {
            paintUnderLine(g, paintTextR);
        }
    }

    /**
     * 本方法的目的是用来处理文字的矩形
     * 
     * @param label
     *            标签
     * @param fontMetrics
     *            公制字体
     * @param text
     *            文本
     * @param icon
     *            图标
     * @param viewR
     *            可视
     * @param iconR
     *            图标
     * @param textR
     *            文本矩形
     * @return 要绘制的字符串
     */
    private String layoutCL(
            JLabel label,
            FontMetrics fontMetrics,
            String text,
            Icon icon,
            Rectangle viewR,
            Rectangle iconR,
            Rectangle textR) {
        // SwingUtilities 的这个方法对所有矩形参数进行处理,里面的参数将会是实际的大小
        return SwingUtilities.layoutCompoundLabel((JComponent) label, fontMetrics, text, icon,
                label.getVerticalAlignment(), label.getHorizontalAlignment(), label.getVerticalTextPosition(),
                label.getHorizontalTextPosition(), viewR, iconR, textR, label.getIconTextGap());
    }

    /**
     * 绘制删除线
     * 
     * @param g
     *            图形设备
     * @param paintTextR
     *            绘制矩形
     */
    private void paintDeleteLine(
            Graphics g,
            Rectangle paintTextR) {
        String text = getText();
        if (text == null || text.trim().length() == 0)
            return; // 除错

        int textWidth = (int) getFontMetrics(g.getFont()).getStringBounds(text, g).getWidth(); // 得到文本的宽度

        g.drawLine(paintTextR.x, paintTextR.y + paintTextR.height / 2, paintTextR.x + textWidth, paintTextR.y
                + paintTextR.height / 2);
    }

    /**
     * 绘制下划线
     * 
     * @param g
     *            图形设备
     * @param paintTextR
     *            绘制矩形
     */
    private void paintUnderLine(
            Graphics g,
            Rectangle paintTextR) {
        String tmpText = getText();
        if (tmpText == null || tmpText.trim().length() == 0)
            return; // 除错

        int textWidth = (int) getFontMetrics(g.getFont()).getStringBounds(tmpText, g).getWidth(); // 得到文本的宽度

        g.drawLine(paintTextR.x, paintTextR.y + paintTextR.height - 2, paintTextR.x + textWidth, paintTextR.y
                + paintTextR.height - 2);
    }

    /**
     * 设置标签绘制器是否显示下划线
     * 
     * @param isHasUnderLine
     *            是否显示下划线
     */
    public void setHasUnderLine(
            boolean isHasUnderLine) {
        hasUnderLine = isHasUnderLine;
    }

    /**
     * 调用传入对象的toString方法 以得到要显示的内容. Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     *
     * @param value
     *            the string value for this cell; if value is <code>null</code> it sets the text value to an empty
     *            string
     * @see JLabel#setText
     *
     */
    protected void setValue(
            Object value) {
        setText((value == null) ? CASUtility.EMPTYSTR : value.toString());
    }

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1); // 定义一个没有焦点的边框
    private static Rectangle paintIconR = new Rectangle(); // 以下三个矩形用来保存图标,文字,可视区的矩形
    private static Rectangle paintTextR = new Rectangle(); // 文字矩形
    private static Rectangle paintViewR = new Rectangle(); // 可视矩形
    private static Insets paintViewInsets = new Insets(0, 0, 0, 0); // 保存边界
    private boolean hasUnderLine; // 保存是否要绘制下划线的属性
    private boolean hasDeleteLine; // 保存是否要绘制删除线的属性
    // We need a place to store the color the JLabel should be returned
    // to after its foreground and background colors have been set
    // to the selection background color.
    // These ivars will be made protected when their names are finalized.
    private Color unselectedForeground;// 保存被选中单元格的前景色
    private Color unselectedBackground;// 保存被选中单元格的背景色
}
