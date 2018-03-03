package org.cas.client.platform.contact.dialog;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.casbeans.BasicArrowButton;

//NOTE:因为要控制弹出菜单的边界,导致用到了对父对话盒的引用,顾暂时没有和其他对话盒共享(如EmployeeDlg)
class ContactArrowButton extends BasicArrowButton implements ActionListener {
    /**
     * create a new Constructor with no arguments
     * 
     * @param prmPanel
     *            父级容器
     */
    public ContactArrowButton(JScrollPane prmPanel) {
        super(5);
        panel = prmPanel;
        addActionListener(this);
    }

    /**
     * 设置快捷键标签
     * 
     * @called by: ContactGeneralPanel
     * @param label
     *            快捷键标签
     */
    public void setArrowButtonLabel(
            JLabel label) {
        this.arrowButtonLabel = label;
    }

    /**
     * 设置快捷键文本框
     * 
     * @called by: ContactGeneralPanel
     * @param prmTextField
     *            快捷键文本框
     */
    public void setArrowButtonTextField(
            JTextField prmTextField) {
        arrowButtonTextField = prmTextField;
    }

    /**
     * 设置弹出式菜单的文字
     * 
     * @called by: ContactGeneralPanel
     * @param s
     *            弹出式菜单上的文字
     */
    public void setItems(
            String[] s) {
        if (this.items != s) {
            this.items = s;
        }
    }

    /**
     * 得到弹出式菜单上的所有标签
     * 
     * @return 菜单项
     */
    public String[] getItems() {
        return items;
    }

    // 事件激发
    /**
     * nvoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        JPopupMenu menu = new JPopupMenu();
        // int menuWidth = menu.getPreferredSize ().width;

        for (int i = 0; i < items.length; i++) {
            JMenuItem item = new JMenuItem(items[i]);
            ActionListener al = new ItemAction(ContactArrowButton.this);
            item.addActionListener(al);
            menu.add(item);
        }

        int tmpY = getY() + getHeight(); // Button的Y位置。
        int tmpScrHeight = Toolkit.getDefaultToolkit().getScreenSize().height; // 屏幕的高度。
        int tmpDistance = (((ContactGeneralPanel) panel).dlg.getY() // 超出屏幕的尺寸。
                + menu.getPreferredSize().height + getY() + getHeight()) - tmpScrHeight;
        // 如果超出屏幕，则做调整。
        if (tmpDistance > 0) {
            tmpY = getY() - menu.getPreferredSize().height + 1; // 加1为的是使弹出菜单和按钮看上去是连在一起的。
        }
        menu.show(getParent(), getX() + getWidth(), tmpY);
    }

    /**
     * 弹出的菜单
     */
    public static class ItemAction implements ActionListener {
        /**
         * 构建器
         * 
         * @param prmButton
         *            调用的按钮
         */
        public ItemAction(ContactArrowButton prmButton) {
            button = prmButton;
        }

        /**
         * Invoked when an action occurs.
         * 
         * @param e
         *            动作事件
         */
        public void actionPerformed(
                ActionEvent e) {
            final JMenuItem tempItem = (JMenuItem) e.getSource();
            SwingUtilities.invokeLater(new ItemRunnable(button, tempItem));
        }

        private ContactArrowButton button;
    }

    /**
     * 菜单的action
     */
    public static class ItemRunnable implements Runnable {
        /**
         * 构建器
         * 
         * @param prmButton
         *            调用的按钮
         * @param prmItem
         *            菜单项
         */
        public ItemRunnable(ContactArrowButton prmButton, JMenuItem prmItem) {
            button = prmButton;
            item = prmItem;
        }

        // 执行
        /** 接口中的方法 */
        public void run() {
            button.arrowButtonLabel.setText(item.getText());
        }

        private ContactArrowButton button;
        private JMenuItem item;
    }

    // @called by: self; ContactGeneralPanel;
    JLabel arrowButtonLabel;
    JTextField arrowButtonTextField;// @NOTE:此处提高可见性，是为了追求更好的性能。
    private String[] items;
    JScrollPane panel; // @NOTE:此处提高可见性，是为了追求更好的性能。
}
