package org.cas.client.platform.casbeans.quickinputfield;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

//绘制器，它继承JLabel==========================================================================================
class QuickInfoRender extends JLabel implements ListCellRenderer {
    QuickInfoRender() {
        setOpaque(true);
    }

    /**
     * 实现接口中的方法
     * 
     * @param list
     *            列表框
     * @param value
     *            显示值
     * @param index
     *            所在索引行
     * @param isSelected
     *            是否选中
     * @param cellHasFocus
     *            是否有焦点
     * @return 经处理后的组件
     */
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        // 设置背景色
        this.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        // 设置前景色
        this.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        // 获得模型
        QuickInfoModel model = (QuickInfoModel) list.getModel();
        // 设置Text
        this.setText(model.getName(value));

        return this; // 返回此组件
    }
}
