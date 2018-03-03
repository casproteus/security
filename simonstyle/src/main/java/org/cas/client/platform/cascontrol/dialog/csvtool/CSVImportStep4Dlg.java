package org.cas.client.platform.cascontrol.dialog.csvtool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.ImportDlgConst;

class CSVImportStep4Dlg extends JDialog implements ActionListener, ComponentListener {
    /*
     * Creates a new instance of SelectedFieldFromModel
     * @param prmDialog 是父对话框
     * @param prmModel 父对话框中传入的表格模型
     * @param prmRowIndex 父对话框中传入的表格行索引
     */
    CSVImportStep4Dlg(CSVImportStep3Dlg prmDialog, final FieldsTableModel prmModel, final int prmRowIndex) {
        super(prmDialog, true);
        setTitle(ImportDlgConst.MODIFY); // 设置标题 "更改映射"
        setBounds((CustOpts.SCRWIDTH - 300) / 2, (CustOpts.SCRHEIGHT - 150) / 2, 300, 150); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // init the contents on the dialog－－－－－－
        model = prmModel;
        dialog = prmDialog;
        rowIndex = prmRowIndex;

        int tmpCount = prmDialog.importableFieldIdx.length;// 返回通讯簿数组的长度
        String[] tmpAllTexts = prmDialog.app.getAppTexts();
        String[] tmpShownStrAry = new String[tmpCount];// 返回存放各个字段的数组
        int tmpMatchedIndex = rowIndex > (tmpCount - 1) ? (tmpCount - 1) : rowIndex; // 如果没有匹配的,那么当前单击的是第几行,comboBox中就默认自动选中第几行.
        for (int i = 0; i < tmpCount; i++) {
            tmpShownStrAry[i] = tmpAllTexts[prmDialog.importableFieldIdx[i]];
            if (tmpShownStrAry[i].equals(dialog.getFieldName(rowIndex))) // 如果有匹配的,则记下来,combobox将默认自动选中改行.
                tmpMatchedIndex = i;
        }

        // ---------实例化各个组件------------------------
        label = new JLabel(ImportDlgConst.SELECTED_AREA);
        box = new JComboBox(tmpShownStrAry);
        checkBox = new JCheckBox(ImportDlgConst.IMPORT_THIS_AREA, true);
        ok = new JButton(ImportDlgConst.OK); // ok按钮上显示的是“下一步”
        cancel = new JButton(ImportDlgConst.CANCEL);
        separator = new JSeparator();// 分隔线

        // ---------分别作属性设置--------
        label.setDisplayedMnemonic('S');
        label.setLabelFor(box);
        box.setSelectedIndex(tmpMatchedIndex);
        checkBox.setMnemonic('I'); // 导入该域(I)
        ok.setFont(CustOpts.custOps.getFontOfDefault());
        cancel.setFont(ok.getFont());
        getRootPane().setDefaultButton(ok);

        // ---------搭建------------------------------------
        getContentPane().add(label);
        getContentPane().add(box); // -5<0,标签和组合框是垂直放置
        getContentPane().add(checkBox);
        getContentPane().add(ok);
        getContentPane().add(cancel);
        getContentPane().add(separator);
        // 取得绘制器中的组件
        // final TableCheckBoxRenderer renderer = dialog.getNativeRenderer();
        // final ECheckBox rendererBox = renderer.getBox();

        // ---------布局计算------------------------
        reLayout();

        // ------加监听器－－－－－－－－
        ok.addActionListener(this); // 取得组合框中所选的项加入至表格中
        cancel.addActionListener(this);
        getContentPane().addComponentListener(this);
    }

    /**
     * Invoked when the component's size changes.
     */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    /**
     * Invoked when the component's position changes.
     */
    public void componentMoved(
            ComponentEvent e) {
    };

    /**
     * Invoked when the component has been made visible.
     */
    public void componentShown(
            ComponentEvent e) {
    };

    /**
     * Invoked when the component has been made invisible.
     */
    public void componentHidden(
            ComponentEvent e) {
    };

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        if (e.getSource() == cancel) {
            isOkChecked = false;
        } else if (e.getSource() == ok) {
            // 取得组合框中所选的项
            String str = (String) box.getSelectedItem();
            // 将所选的项加入至表格中
            model.setValueAt(str, rowIndex, 1);
            searchModelValue(str, rowIndex);
            isOkChecked = true;
        }
        setVisible(false);
    }

    /**
     * 返回组合框所选项的索引
     * 
     * @return 组合框所选项的索引
     */
    int getComboBoxIndex() {
        return box.getSelectedIndex();
    }

    /**
     * 返回复选框选择状态
     * 
     * @return 复选框选择状态
     */
    Boolean getBoxSelection() {
        if (checkBox.isSelected()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 返回当前对话框
     * 
     * @called by: SelectedFieldDialog.java
     * @return 导入状态
     */
    boolean isOkClicked() {
        return isOkChecked;
    }

    /**
     */
    protected void extraAction() {
        ok.removeActionListener(this);
    }

    private void reLayout() {
        cancel.setBounds(getContentPane().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getHeight()
                - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        separator.setBounds(CustOpts.VER_GAP, ok.getY() - CustOpts.SEP_HEIGHT, getWidth() - 3 * CustOpts.VER_GAP,
                CustOpts.SEP_HEIGHT);
        checkBox.setBounds(CustOpts.VER_GAP, separator.getY() - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP, getWidth() - 3
                * CustOpts.VER_GAP, CustOpts.BTN_HEIGHT);

        label.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - 3 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);

        box.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP + CustOpts.LBL_HEIGHT, getWidth() - 3 * CustOpts.HOR_GAP,
                CustOpts.BTN_HEIGHT);

        validate();
    }

    /**
     * 遍历表格找出相同名字的行 并把先前的设为空
     * 
     * @param prmStr
     *            : 判断是否相等的字符串
     * @param cursorRow
     *            : 当前选取的行
     */
    private void searchModelValue(
            String prmStr,
            int cursorRow) {
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            if (i != cursorRow) {
                if (model.getValueAt(i, 1) != null && model.getValueAt(i, 1).equals(prmStr)) {
                    model.setValueAt(CASUtility.EMPTYSTR, i, 1);
                    // 取得模型中指定行第一列的值
                    Object value = model.getValueAt(i, 0);
                    // 取得模型中指定文本域的值
                    String string = model.getCheckBoxName(value);
                    model.setValueAt(new Object[] { string, Boolean.FALSE }, i, 0);

                }
            }
        }
    }

    // 对话盒位置
    private JLabel label;
    private JSeparator separator;// 分隔线
    private JButton ok, cancel;
    private CSVImportStep3Dlg dialog;
    private FieldsTableModel model;
    private final JCheckBox checkBox;
    private int rowIndex;
    private JComboBox box;
    private boolean isOkChecked;
}
