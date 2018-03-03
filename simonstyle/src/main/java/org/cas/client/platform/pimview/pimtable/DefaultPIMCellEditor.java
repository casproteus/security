package org.cas.client.platform.pimview.pimtable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;

/**
 */

public class DefaultPIMCellEditor extends AbstractCellEditor implements IPIMCellEditor, TreeCellEditor {
    /** 保存缺省的编辑组件 */
    protected JComponent editorComponent;
    /** 保存编辑器代表 */
    protected EditorDelegate delegate;
    /** 保存鼠标点击单元格进入编辑状态时所需点击的次数 */
    protected int clickCountToStart = 1;

    /**
     * 以一个 JTextField 对象作为参数构建一个实例 Constructs a <code>DefaultPIMCellEditor</code> that uses a text field.
     *
     * @param textField
     *            a <code>JTextField</code> object
     */
    public DefaultPIMCellEditor(final JTextField textField) {
        // 保存编辑器组件
        editorComponent = textField;
        // 设置点击数,点一下就开始编辑,好象多此一举
        this.clickCountToStart = 1;
        // 创建一个新的编辑器代表 ,用匿名类方式覆盖两个方式
        delegate = new TextFieldEditorDelegate(textField, this);
        // 加动作监听器
        textField.addActionListener(delegate);
    }

    /**
     * 以一个 JCheckBox 对象作为参数构建一个实例 目前主要针对 Boolean 型数据 Constructs a <code>DefaultPIMCellEditor</code> object that uses a
     * check box.
     *
     * @param checkBox
     *            a <code>JCheckBox</code> object
     */
    public DefaultPIMCellEditor(final JCheckBox checkBox) {
        // 保存编辑器组件
        editorComponent = checkBox;
        // 创建一个新的编辑器代表 ,用匿名类方式覆盖两个方式
        delegate = new CheckboxEditorDelegate(checkBox, this);

        // 加动作监听器
        checkBox.addActionListener(delegate);
    }

    /**
     * 以一个 JComboBox 对象作为参数构建一个实例 Constructs a <code>DefaultPIMCellEditor</code> object that uses a combo box.
     *
     * @param comboBox
     *            a <code>JComboBox</code> object
     */
    public DefaultPIMCellEditor(final JComboBox comboBox) {
        // 保存编辑器组件
        editorComponent = comboBox;
        comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        // 创建一个新的编辑器代表 ,用匿名类方式覆盖几个方法
        delegate = new ComboBoxEditorDelegate(comboBox, this);
        // 加动作监听器
        comboBox.addActionListener(delegate);
    }

    /**
     * 返回编辑器组件 Returns a reference to the editor component.
     *
     * @return the editor <code>Component</code>
     */
    public Component getComponent() {
        return editorComponent;
    }

    //
    // Modifying
    //

    /**
     * 设置鼠标点击进入编辑状态时所需的点击 Specifies the number of clicks needed to start editing.
     *
     * @param count
     *            an int specifying the number of clicks needed to start editing
     * @see #getClickCountToStart
     */
    public void setClickCountToStart(
            int count) {
        clickCountToStart = count;
    }

    /**
     * 返回鼠标点击进入编辑状态时所需的点击 Returns the number of clicks needed to start editing.
     * 
     * @return the number of clicks needed to start editing
     */
    public int getClickCountToStart() {
        return clickCountToStart;
    }

    //
    // Override the implementations of the superclass, forwarding all methods
    // from the CellEditor interface to our delegate.
    //

    /**
     * 返回单元格编辑器中的值 Forwards the message from the <code>CellEditor</code> to the <code>delegate</code>.
     * 
     * @see EditorDelegate#getCellEditorValue
     * @return 单元格编辑器中的值
     */
    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    /**
     * 根据外界事件来判断单元格是否可编辑 Forwards the message from the <code>CellEditor</code> to the <code>delegate</code>.
     * 
     * @see EditorDelegate#isCellEditable(EventObject)
     * @return 是否可编辑
     * @param anEvent
     *            鼠标事件源
     */
    public boolean isCellEditable(
            EventObject anEvent) {
        // 调用编辑器代表中的方法
        return delegate.isCellEditable(anEvent);
    }

    /**
     * 返回为 true 表示是否要选中本编辑单元格 Forwards the message from the <code>CellEditor</code> to the <code>delegate</code>.
     * 
     * @see EditorDelegate#shouldSelectCell(EventObject)
     * @return 是否要选中本编辑单元格
     * @param anEvent
     *            事件源
     */
    public boolean shouldSelectCell(
            EventObject anEvent) {
        return delegate.shouldSelectCell(anEvent);
    }

    /**
     * 停止编辑,返回为真表示编辑已经停止 Forwards the message from the <code>CellEditor</code> to the <code>delegate</code>.
     * 
     * @see EditorDelegate#stopCellEditing
     * @return 是否编辑停止
     */
    public boolean stopCellEditing() {
        // 调用编辑器代表中的方法
        return delegate.stopCellEditing();
    }

    /**
     * @NOTE：提高父类方法的可见性，为了提高性能。尚未知改方法是否真的有效。
     */
    public void fireEditingStopped() {
        super.fireEditingStopped();
    }

    /**
     * @NOTE：提高父类方法的可见性，为了提高性能。尚未知改方法是否真的有效。
     */
    public void fireEditingCanceled() {
        super.fireEditingStopped();
    }

    /**
     * 放弃编辑 Forwards the message from the <code>CellEditor</code> to the <code>delegate</code>.
     * 
     * @see EditorDelegate#cancelCellEditing
     */
    public void cancelCellEditing() {
        // 调用编辑器代表中的方法
        delegate.cancelCellEditing();
    }

    //
    // Implementing the TreeCellEditor Interface
    //

    /**
     * 实现 <code>TreeCellEditor</code> 接口中的方法,本编辑器可在 tree 中使用 Implements the <code>TreeCellEditor</code> interface.
     * 
     * @return 经处理的的组件
     * @param prmTree
     *            一个树对象
     * @param prmValue
     *            要处理的值
     * @param prmIsSelected
     *            是否选中
     * @param prmExpanded
     *            是否展开
     * @param prmLeaf
     *            是不是叶子
     * @param prmRow
     *            所在行
     */
    public Component getTreeCellEditorComponent(
            JTree prmTree,
            Object prmValue,
            boolean prmIsSelected,
            boolean prmExpanded,
            boolean prmLeaf,
            int prmRow) {
        String stringValue = prmTree.convertValueToText(prmValue, prmIsSelected, prmExpanded, prmLeaf, prmRow, false);

        delegate.setValue(stringValue);
        return editorComponent;
    }

    //
    // Implementing the CellEditor Interface
    //
    /**
     * 实现 <code>TreeCellEditor</code> 接口中的方法,本编辑器将在 PIMTable 中使用 Implements the <code>IPIMTableCellEditor</code>
     * interface.
     * 
     * @param table
     *            表格对象
     * @param value
     *            传入值
     * @param isSelected
     *            是否选中
     * @param row
     *            所在行
     * @param column
     *            所在列
     * @return 经处理后的编辑器组件
     */
    public Component getTableCellEditorComponent(
            PIMTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {
        delegate.setValue(value);
        return editorComponent;
    }

    //
    // Protected EditorDelegate class
    //

    /**
     * 本类定义了编辑器代表 The protected <code>EditorDelegate</code> class.
     */
    public static class EditorDelegate implements ActionListener, ItemListener, Serializable {

        /** 保存编辑单元中的对象 The value of this cell. */
        protected Object value;
        /**
         * 要处理的编辑器接口
         */
        protected DefaultPIMCellEditor cellEditor;

        /**
         * 缺省的构建器
         * 
         * @param prmCellEditor
         *            传入引用
         */
        public EditorDelegate(DefaultPIMCellEditor prmCellEditor) {
            cellEditor = prmCellEditor;
        }

        /**
         * 返回单元格中的值
         * 
         * @return the value of this cell
         */
        public Object getCellEditorValue() {
            return value;
        }

        /**
         * 设置单元格中的值 Sets the value of this cell.
         * 
         * @param value
         *            the new value of this cell
         */
        public void setValue(
                Object value) {
            this.value = value;
        }

        /**
         * Returns true if <code>anEvent</code> is <b>not</b> a <code>MouseEvent</code>. Otherwise, it returns true if
         * the necessary number of clicks have occurred, and returns false otherwise.
         * 首先，我觉得这个方法的方法名字起的不好，应该叫shouldChangeToEditiongState(); 因为它里面是根据事件的类型，返回是否该进入编辑状态的标志。
         * 根据规格，PIMTable双击时弹出一个对话盒，而不进入编辑状态，所以改变原来的>=2的返回true的条件未<2。
         * 即如双击一条记录时，如果双击的鼠标落点所在的Cell本来不是Table的选中Cell（SelectedCell），则第一次鼠标
         * press的时候由于调本方法前判断落点不是已选中的Cell，当即返回false，不会执行本方法。第二次press的时候，
         * 会执行到本方法，但由于clickcount＝＝2，将返回false，导致Cell仍然不能进入编辑状态。 而如果双击的鼠标落点所在的Cell本来已经是Table的选中Cell（SelectedCell），则第一次鼠标
         * press的时候不会被拦住，能顺利进入本方法，并满足clickcount < 2.使编辑器显示出来，而鼠标 双击的的第二次按下事件将被编辑器拦住。
         * 
         * @param anEvent
         *            the event
         * @return true if cell is ready for editing, false otherwise
         * @see #setClickCountToStart
         * @see #shouldSelectCell
         */
        public boolean isCellEditable(
                EventObject anEvent) {
            // 本PIMTable鼠标一点就进入编辑
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent) anEvent).getClickCount() < 2; // >= clickCountToStart;
            }
            return true;
        }

        /**
         * 返回为 true 表示否要选中本编辑单元格 Returns true to indicate that the editing cell may be selected.
         *
         * @param anEvent
         *            the event
         * @return true
         * @see #isCellEditable
         */
        public boolean shouldSelectCell(
                EventObject anEvent) {
            return true;
        }

        /**
         * 返回为 true 表示编辑已经开始了 Returns true to indicate that editing has begun.
         *
         * @param anEvent
         *            the event
         * @return 是否开始编辑
         */
        public boolean startCellEditing(
                EventObject anEvent) {
            return true;
        }

        /**
         * 停止编辑,返回为真表示编辑已经停止 Stops editing and returns true to indicate that editing has stopped. This method calls
         * <code>fireEditingStopped</code>.
         *
         * @return true
         */
        public boolean stopCellEditing() {
            // 激发编辑停止事件
            cellEditor.fireEditingStopped();
            return true;
        }

        /**
         * 表示放弃本次编辑 Cancels editing. This method calls <code>fireEditingCanceled</code>.
         */
        public void cancelCellEditing() {
            // 激发放弃编辑事件
            cellEditor.fireEditingCanceled();
        }

        /**
         * 实现了 ActionListener 中的方法, When an action is performed, editing is ended.
         * 
         * @param e
         *            the action event
         * @see #stopCellEditing
         */
        public void actionPerformed(
                ActionEvent e) {
            // 执行时,命令编辑停止
            cellEditor.stopCellEditing();
        }

        /**
         * 实现了状态监听器 ItemListener 中的方法 当其状态发生变化时,命令编辑停止 When an item's state changes, editing is ended.
         * 
         * @param e
         *            the action event
         * @see #stopCellEditing
         */
        public void itemStateChanged(
                ItemEvent e) {
            // 执行时,命令编辑停止
            cellEditor.stopCellEditing();
        }
    }

    /**
     * 文本组件的编辑器代表
     */
    public static class TextFieldEditorDelegate extends EditorDelegate {
        /**
         * 要处理的编辑器对象
         */
        JTextField textField;

        /**
         * 构建器,传入引用
         * 
         * @param prmTextField
         *            编辑组件
         * @param prmCellEditor
         *            本类引用一
         */
        public TextFieldEditorDelegate(JTextField prmTextField, DefaultPIMCellEditor prmCellEditor) {
            super(prmCellEditor);
            textField = prmTextField;
        }

        /**
         * 重载 public void setValue (Object value) 使操作对象转移为实例中的编辑器组件
         * 
         * @param value
         *            设置的值
         */
        public void setValue(
                Object value) {
            textField.setText((value != null) ? value.toString() : CASUtility.EMPTYSTR);
        }

        /**
         * 重载 public Object getCellEditorValue () 使操作对象转移为实例中的编辑器组件
         * 
         * @return 编辑器中的值
         */
        public Object getCellEditorValue() {
            return textField.getText();
        }
    }

    /**
     * 检查框组件的编辑器代表
     */
    public static class CheckboxEditorDelegate extends EditorDelegate {
        /**
         * 要处理的编辑器对象
         */
        JCheckBox checkBox;

        /**
         * 构建器,传入引用
         * 
         * @param prmCheckBox
         *            编辑组件
         * @param prmCellEditor
         *            本类引用
         */
        public CheckboxEditorDelegate(JCheckBox prmCheckBox, DefaultPIMCellEditor prmCellEditor) {
            super(prmCellEditor);
            checkBox = prmCheckBox;
        }

        /**
         * 重载 public void setValue (Object value) 使操作对象转移为实例中的编辑器组件
         * 
         * @param value
         *            设置的值
         */
        public void setValue(
                Object value) {
            // 初始为 false
            boolean selected = false;
            // 处理 Boolean 型对象
            if (value instanceof Boolean) {
                selected = ((Boolean) value).booleanValue();
            }
            // 处理 String 型对象
            else if (value instanceof String) {
                // 008 要求它能处理"0"和"1"
                selected = value.equals(PIMPool.BOOLEAN_TRUE) || value.equals("1");
            }
            // 设置初始选项
            checkBox.setSelected(selected);
        }

        /**
         * 重载 public Object getCellEditorValue () 使操作对象转移为实例中的编辑器组件
         * 
         * @return 编辑器中的值
         */
        public Object getCellEditorValue() {
            return Boolean.valueOf(checkBox.isSelected());
        }
    }

    /**
     * 组合框组件的编辑器代表
     */
    public static class ComboBoxEditorDelegate extends EditorDelegate {
        /**
         * 要处理的编辑器对象
         */
        JComboBox comboBox;

        /**
         * 构建器,传入引用
         * 
         * @param prmComboBox
         *            编辑器组件
         * @param prmCellEditor
         *            本类引用
         */
        public ComboBoxEditorDelegate(JComboBox prmComboBox, DefaultPIMCellEditor prmCellEditor) {
            super(prmCellEditor);
            comboBox = prmComboBox;
        }

        /**
         * 重载 public void setValue (Object value) 使操作对象转移为实例中的编辑器组件
         * 
         * @param value
         *            设置的值
         */
        public void setValue(
                Object value) {
            comboBox.setSelectedItem(value);
        }

        /**
         * 重载 public Object getCellEditorValue () 使操作对象转移为实例中的编辑器组件
         * 
         * @return 编辑器中的值
         */
        public Object getCellEditorValue() {
            return comboBox.getSelectedItem();
        }

        /**
         * 重载 public boolean shouldSelectCell (EventObject anEvent) 判断以用户的什么行为作为选择了一个单元格
         * 
         * @return 是否选中
         * @param anEvent
         *            事件源
         */
        public boolean shouldSelectCell(
                EventObject anEvent) {
            // 如为鼠标事件
            if (anEvent instanceof MouseEvent) {
                MouseEvent e = (MouseEvent) anEvent;
                // 在鼠标事件的标识不为拖动时,认为就是将要选择这个单元格
                return e.getID() != MouseEvent.MOUSE_DRAGGED;
            }
            // 一般事件均返回为真
            return true;
        }

        /**
         * 重载 public Object getCellEditorValue () 使操作对象转移为实例中的编辑器组件
         * 
         * @return 是否停止
         */
        public boolean stopCellEditing() {
            // 如组合框可编辑
            if (comboBox.isEditable()) {
                // Commit edited value.
                // 委托编辑值。由comboBox 自己处理
                comboBox.actionPerformed(new ActionEvent(cellEditor, 0, CASUtility.EMPTYSTR));
            }
            // 否则以其父类的方法来判断
            return super.stopCellEditing();
        }
    }
}
