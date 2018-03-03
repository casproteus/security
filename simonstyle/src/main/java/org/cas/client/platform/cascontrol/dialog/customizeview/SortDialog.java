package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.group.PIMButtonGroup;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.resource.international.CustViewConsts;
import org.cas.client.resource.international.IntlModelConstants;
import org.cas.client.resource.international.PaneConsts;

public class SortDialog extends JDialog implements ICASDialog, ActionListener, ListSelectionListener, Runnable {

    /**
     * 创建一个 SortDialog 的实例
     * 
     * @param prmDialog
     *            父窗体
     * @param prmViewInfo
     *            视图信息
     */
    public SortDialog(Dialog prmDialog, PIMViewInfo prmViewInfo) {
        super(prmDialog, true);
        // 保存引用
        this.parentDialog = prmDialog;
        this.viewInfo = prmViewInfo;
        // 设置标题
        setTitle(CustViewConsts.SORT);
        // 设置对话盒尺寸
        setSize(getDialogSize());
        // 组件初始化并布局
        initComponent();
        // 给组件加上监听器
        addAllListeners();
        // 设置一下焦点转移顺序
        focusTransferSequence();

        setBounds((CustOpts.SCRWIDTH - getDialogSize().width) / 2, (CustOpts.SCRHEIGHT - getDialogSize().height) / 2,
                getDialogSize().width, getDialogSize().height); // 对话框的默认尺寸。
    }

    /**
     * 创建一个 SortDialog 的实例
     * 
     * @param parent
     *            父窗体
     * @param prmViewInfo
     *            视图信息
     */
    public SortDialog(Frame parent, PIMViewInfo prmViewInfo) {
        super(parent, true);
        // 保存引用

        this.viewInfo = prmViewInfo;
        // 设置标题
        setTitle(CustViewConsts.SORT);
        // 设置对话盒尺寸
        setSize(getDialogSize());
        // 组件初始化并布局
        initComponent();
        // 给组件加上监听器
        addAllListeners();
        // 设置一下焦点转移顺序
        focusTransferSequence();

        setBounds((CustOpts.SCRWIDTH - getDialogSize().width) / 2, (CustOpts.SCRHEIGHT - getDialogSize().height) / 2,
                getDialogSize().width, getDialogSize().height); // 对话框的默认尺寸。
    }

    public void reLayout() {
    };

    /**
     * 返回本对话盒的尺寸
     * 
     * @return 对话盒尺寸
     */
    public Dimension getDialogSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /** 初始化加布局; */
    private void initComponent() {
        Dimension dlgSize = getDialogSize();
        // 这两个变量用来给组件定位,所有的组件都根据它来计算
        int X_Coordinate = 0;
        int Y_Coordinate = 0;

        // 设置OK按钮
        ok = new JButton(CustViewConsts.OK);
        ok.setBounds(dlgSize.width - ok.getPreferredSize().width, Y_Coordinate, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);
        // ok.setEnabled(false);

        // 设置Cancel按钮
        cancel = new JButton(CustViewConsts.CANCEL);
        cancel.setBounds(dlgSize.width - cancel.getPreferredSize().width, Y_Coordinate + ok.getPreferredSize().height
                + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);

        // 设置Clear all 按钮
        clearAllBTN = new JButton(CustViewConsts.CLEAR_ALL);
        clearAllBTN.setMnemonic('C');
        clearAllBTN.setBounds(dlgSize.width - clearAllBTN.getPreferredSize().width,
                Y_Coordinate + ok.getPreferredSize().height * 2 + CustOpts.VER_GAP * 2, CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);

        // 用来装配下拉框的模型
        equipComboboxModels();

        // 第一Combo的标签
        JLabel firstLabel = new JLabel(CustViewConsts.SORT_GIST);
        firstCombo = new JComboBox(firstComboModel); // 设置第一Combo
        firstLabel.setDisplayedMnemonic('S');
        firstLabel.setLabelFor(firstCombo);
        firstLabel.setBounds(X_Coordinate, Y_Coordinate, OFFSET, CustOpts.LBL_HEIGHT);
        firstCombo.setBounds(firstLabel.getX() + firstLabel.getWidth(), firstLabel.getY(), BUTTON_WIDTH,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(firstLabel);
        getContentPane().add(firstCombo);

        // 设置第一Combo的升序按钮
        firstAscentRadio = new JRadioButton();
        firstAscentRadio.setText(CustViewConsts.ASCENT);
        int tmpRadioY_Coordinate =
                firstCombo.getLocation().y - CustOpts.HOR_GAP * 2 - firstAscentRadio.getSize().height;
        firstAscentRadio.setBounds(X_Coordinate + firstCombo.getSize().width + CustOpts.HOR_GAP, tmpRadioY_Coordinate,
                firstAscentRadio.getPreferredSize().width, firstAscentRadio.getPreferredSize().height);
        getContentPane().add(firstAscentRadio);

        // 设置第一Combo的降序按钮
        firstDescentRadio = new JRadioButton();
        firstDescentRadio.setText(CustViewConsts.DESCENT);
        firstDescentRadio.setBounds(X_Coordinate + firstCombo.getSize().width + CustOpts.HOR_GAP, tmpRadioY_Coordinate
                + CustOpts.HOR_GAP + firstAscentRadio.getSize().height, firstDescentRadio.getPreferredSize().width,
                firstDescentRadio.getPreferredSize().height);
        getContentPane().add(firstDescentRadio);

        // 将这两个单选按钮设为一组
        groupFirst = new PIMButtonGroup();
        groupFirst.add(firstAscentRadio);
        groupFirst.add(firstDescentRadio);

        // 设置第二Combo的标签
        Y_Coordinate += firstCombo.getSize().height + CustOpts.VER_GAP;
        JLabel secondLabel = new JLabel(CustViewConsts.SECOND_GIST);
        secondCombo = new JComboBox(secondComboModel); // 设置第二Combo
        secondLabel.setDisplayedMnemonic('T');
        secondLabel.setLabelFor(secondCombo);
        Y_Coordinate += CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;
        secondLabel.setBounds(X_Coordinate, Y_Coordinate, OFFSET, CustOpts.LBL_HEIGHT);
        secondCombo.setBounds(secondLabel.getX() + secondLabel.getWidth(), secondLabel.getY(), BUTTON_WIDTH,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(secondLabel);
        getContentPane().add(secondCombo);

        // 设置第二Combo的升序按钮
        secondAscentRadio = new JRadioButton();
        secondAscentRadio.setText(CustViewConsts.ASCENT);
        tmpRadioY_Coordinate = secondCombo.getLocation().y - CustOpts.HOR_GAP * 2 - secondAscentRadio.getSize().height;
        secondAscentRadio.setBounds(X_Coordinate + firstCombo.getSize().width + CustOpts.HOR_GAP, tmpRadioY_Coordinate,
                secondAscentRadio.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        getContentPane().add(secondAscentRadio);

        // 设置第二Combo的降序按钮
        secondDescentRadio = new JRadioButton();
        secondDescentRadio.setText(CustViewConsts.DESCENT);
        secondDescentRadio.setBounds(X_Coordinate + secondCombo.getSize().width + CustOpts.HOR_GAP,
                tmpRadioY_Coordinate + CustOpts.HOR_GAP + secondAscentRadio.getSize().height,
                secondDescentRadio.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        getContentPane().add(secondDescentRadio);

        // 将这两个单选按钮设为一组
        groupSecond = new PIMButtonGroup();
        groupSecond.add(secondAscentRadio);
        groupSecond.add(secondDescentRadio);

        // 第三Combo的标签
        Y_Coordinate += secondLabel.getSize().height + CustOpts.VER_GAP;
        JLabel thirdLabel = new JLabel(CustViewConsts.THIRD_GIST);
        thirdCombo = new JComboBox(thirdComboModel);
        thirdLabel.setDisplayedMnemonic('B');
        thirdLabel.setLabelFor(thirdCombo);
        // 设置第三Combo
        Y_Coordinate += CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;
        thirdLabel.setBounds(X_Coordinate, Y_Coordinate, OFFSET, CustOpts.LBL_HEIGHT);
        thirdCombo.setBounds(X_Coordinate, Y_Coordinate, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(thirdLabel);
        getContentPane().add(thirdCombo);

        // 设置第三Combo的升序按钮
        thirdAscentRadio = new JRadioButton();
        thirdAscentRadio.setText(CustViewConsts.ASCENT);
        tmpRadioY_Coordinate = thirdCombo.getLocation().y - CustOpts.HOR_GAP * 2 - thirdAscentRadio.getSize().height;
        thirdAscentRadio.setBounds(X_Coordinate + thirdCombo.getSize().width + CustOpts.HOR_GAP, tmpRadioY_Coordinate,
                thirdAscentRadio.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        getContentPane().add(thirdAscentRadio);

        // 设置第三Combo的降序按钮
        thirdDescentRadio = new JRadioButton();
        thirdDescentRadio.setText(CustViewConsts.DESCENT);
        thirdDescentRadio.setBounds(X_Coordinate + thirdCombo.getSize().width + CustOpts.HOR_GAP, tmpRadioY_Coordinate
                + CustOpts.HOR_GAP + thirdAscentRadio.getSize().height, thirdDescentRadio.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        getContentPane().add(thirdDescentRadio);

        // 将这两个单选按钮设为一组
        groupThird = new PIMButtonGroup();
        groupThird.add(thirdAscentRadio);
        groupThird.add(thirdDescentRadio);

        // 设置这些按钮的使能状态
        initComboAndFlags();
    }

    /** 为所有的下拉框模型装配数据 */
    private void equipComboboxModels() {
        // 这个字符串要去掉第一个
        String[] tmpAllFields = CASControl.ctrl.getModel().getFieldNames(viewInfo);
        // 这是比上一数据多了一个(无)选项,所以长度不变
        Object[] allFields = new Object[tmpAllFields.length];
        allFields[0] = new StringBuffer().append('(').append(CustViewConsts.NULL).append(')').toString();
        System.arraycopy(tmpAllFields, 1, allFields, 1, tmpAllFields.length - 1);
        // 先统统加上相同的
        // TODO:以后要优化
        firstComboModel = new DefaultComboBoxModel(allFields);
        secondComboModel = new DefaultComboBoxModel(allFields);
        thirdComboModel = new DefaultComboBoxModel(allFields);
    }

    /**
     * 设置所有按钮的使能状态,初始化时调用 called by :initComponent()
     */
    private void initComboAndFlags() {
        // 调用本包工具类中的方法得到视图中的序信息:
        // 成对出现,一个字段名称加升降序标志(0升1降)
        Object[] tmpSort = ParseUtil.parseSortCritia(viewInfo);
        // 得到排序数量
        int tmpConditionNum = tmpSort.length / 2;
        // 最多有三个排序依据，有四种不同的情况
        switch (tmpConditionNum) {
        // 没有任何排序
            case 0:
                // 开第一个,选中取空
                firstCombo.setSelectedIndex(0);
                // 默认为降序
                firstDescentRadio.setSelected(true);
                //
                firstAscentRadio.setEnabled(false);
                firstDescentRadio.setEnabled(false);

                // 第二选区全禁止
                secondCombo.setSelectedIndex(0);
                secondCombo.setEnabled(false);
                secondAscentRadio.setEnabled(false);
                // 默认为降序
                secondDescentRadio.setSelected(true);
                secondDescentRadio.setEnabled(false);

                // 第三选区全禁止
                thirdCombo.setEnabled(false);
                thirdCombo.setSelectedIndex(0);
                thirdAscentRadio.setEnabled(false);
                // 默认为降序
                thirdDescentRadio.setSelected(true);
                thirdDescentRadio.setEnabled(false);

                clearAllBTN.setEnabled(false);
                break;

            case 1:
                // 从数组中提出信息来装配第一下拉框选中项
                firstCombo.setSelectedItem(tmpSort[0]);

                // 从数组中提出信息来装配升降序按钮
                if (tmpSort[1].toString().equals("0")) {
                    firstAscentRadio.setSelected(true);
                    firstDescentRadio.setSelected(false);
                } else {
                    firstAscentRadio.setSelected(false);
                    firstDescentRadio.setSelected(true);
                }
                firstAscentRadio.setEnabled(true);
                firstDescentRadio.setEnabled(true);
                // 开第二个,选中取空
                secondCombo.setSelectedIndex(0);
                // 默认为降序
                secondDescentRadio.setSelected(true);
                //
                secondAscentRadio.setEnabled(false);
                secondDescentRadio.setEnabled(false);

                // 第三选区全禁止
                thirdCombo.setEnabled(false);
                thirdCombo.setSelectedIndex(0);
                thirdAscentRadio.setEnabled(false);
                // 默认为降序
                thirdDescentRadio.setSelected(true);
                thirdDescentRadio.setEnabled(false);
                break;

            case 2:
                // 从数组中提出信息来装配第一下拉框选中项
                firstCombo.setSelectedItem(tmpSort[0]);
                // 从数组中提出信息来装配升降序按钮
                if (tmpSort[1].toString().equals("0")) {
                    firstAscentRadio.setSelected(true);
                    firstDescentRadio.setSelected(false);
                } else {
                    firstAscentRadio.setSelected(false);
                    firstDescentRadio.setSelected(true);
                }
                firstAscentRadio.setEnabled(true);
                firstDescentRadio.setEnabled(true);

                // 从数组中提出信息来装配第二下拉框选中项
                secondCombo.setSelectedItem(tmpSort[2]);
                // 从数组中提出信息来装配升降序按钮
                if (tmpSort[3].toString().equals("0")) {
                    secondAscentRadio.setSelected(true);
                    secondDescentRadio.setSelected(false);
                } else {
                    secondAscentRadio.setSelected(false);
                    secondDescentRadio.setSelected(true);
                }
                secondAscentRadio.setEnabled(true);
                secondDescentRadio.setEnabled(true);

                // 开第三个,选中取空
                thirdCombo.setSelectedIndex(0);
                // 默认为降序
                thirdDescentRadio.setSelected(true);
                //
                thirdAscentRadio.setEnabled(false);
                thirdDescentRadio.setEnabled(false);
                break;

            case 3:
                // 从数组中提出信息来装配第一下拉框选中项
                firstCombo.setSelectedItem(tmpSort[0]);
                // 从数组中提出信息来装配升降序按钮
                if (tmpSort[1].toString().equals("0")) {
                    firstAscentRadio.setSelected(true);
                    firstDescentRadio.setSelected(false);
                } else {
                    firstAscentRadio.setSelected(false);
                    firstDescentRadio.setSelected(true);
                }
                firstAscentRadio.setEnabled(true);
                firstDescentRadio.setEnabled(true);

                // 从数组中提出信息来装配第二下拉框选中项
                secondCombo.setSelectedItem(tmpSort[2]);
                // 从数组中提出信息来装配升降序按钮
                if (tmpSort[3].toString().equals("0")) {
                    secondAscentRadio.setSelected(true);
                    secondDescentRadio.setSelected(false);
                } else {
                    secondAscentRadio.setSelected(false);
                    secondDescentRadio.setSelected(true);
                }
                secondAscentRadio.setEnabled(true);
                secondDescentRadio.setEnabled(true);

                // 从数组中提出信息来装配第三下拉框选中项
                thirdCombo.setSelectedItem(tmpSort[4]);
                // 从数组中提出信息来装配升降序按钮
                if (tmpSort[5].toString().equals("0")) {
                    thirdAscentRadio.setSelected(true);
                    thirdDescentRadio.setSelected(false);
                } else {
                    thirdAscentRadio.setSelected(false);
                    thirdDescentRadio.setSelected(true);
                }
                thirdAscentRadio.setEnabled(true);
                thirdDescentRadio.setEnabled(true);

            default:
                break;
        }
    }

    /**
     * 控制信息转移顺序 由构建器调用
     */
    private void focusTransferSequence() {
        // 设置Tab建焦点顺序。
        // 从第一选区开始
        firstCombo.setNextFocusableComponent(firstAscentRadio);
        firstAscentRadio.setNextFocusableComponent(firstDescentRadio);
        // 到第二选区
        firstDescentRadio.setNextFocusableComponent(secondCombo);
        secondCombo.setNextFocusableComponent(secondAscentRadio);
        secondAscentRadio.setNextFocusableComponent(secondDescentRadio);
        // 到第三选区
        secondDescentRadio.setNextFocusableComponent(thirdCombo);
        thirdCombo.setNextFocusableComponent(thirdAscentRadio);
        thirdAscentRadio.setNextFocusableComponent(thirdDescentRadio);
        // 到ok选区
        thirdDescentRadio.setNextFocusableComponent(ok);
        ok.setNextFocusableComponent(cancel);
        cancel.setNextFocusableComponent(clearAllBTN);
        clearAllBTN.setNextFocusableComponent(firstCombo);

        // 最后执行
        SwingUtilities.invokeLater(this);
    }

    /**
     * 给所有的组件加上监听器
     */
    private void addAllListeners() {
        // 第一选区
        // firstCombo.addItemListener(this);
        firstCombo.addActionListener(this);
        firstAscentRadio.addActionListener(this);
        firstDescentRadio.addActionListener(this);

        // 第二选区
        // secondCombo.addItemListener(this);
        secondCombo.addActionListener(this);
        secondAscentRadio.addActionListener(this);
        secondDescentRadio.addActionListener(this);

        // 第三选区
        // thirdCombo.addItemListener(this);
        thirdCombo.addActionListener(this);
        thirdAscentRadio.addActionListener(this);
        thirdDescentRadio.addActionListener(this);

        // 确定和清除按钮
        ok.addActionListener(this);
        cancel.addActionListener(this);
        clearAllBTN.addActionListener(this);
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        Object obj = e.getSource();

        if (obj == clearAllBTN) {
            clearAllClicked();
        }
        // 点了OK自然要把信息装配起来保存入数据库
        else if (obj == ok) {
            okClicked(e);
        }
        // 点了cancel
        else if (obj == cancel) {
            cancelClicked(e);
        } else if (obj == firstCombo || obj == secondCombo || obj == thirdCombo) {
            itemStateChanged(e);
        }
        /*
         * else if (e.getSource() == firstAscentRadio || e.getSource() == firstDescentRadio || e.getSource() ==
         * secondAscentRadio || e.getSource() == secondDescentRadio || e.getSource() == thirdAscentRadio ||
         * e.getSource() == thirdDescentRadio) { ok.setEnabled(true); }
         */
    }

    /**
     * 第一Combo作了选择 Called by ActionPerformed
     */
    private void firstComboSelected() {
        int seleIndex = firstCombo.getSelectedIndex();
        if (seleIndex == 0) {
            //
            firstAscentRadio.setSelected(false);
            firstAscentRadio.setEnabled(false);
            firstDescentRadio.setSelected(true);
            firstDescentRadio.setEnabled(false);

            //
            secondCombo.setSelectedIndex(0);
            secondCombo.setEnabled(false);
            //
            secondAscentRadio.setSelected(false);
            secondAscentRadio.setEnabled(false);
            //
            secondDescentRadio.setSelected(true);
            secondDescentRadio.setEnabled(false);

            //
            thirdCombo.setSelectedIndex(0);
            thirdCombo.setEnabled(false);
            thirdAscentRadio.setSelected(false);
            thirdAscentRadio.setEnabled(false);
            thirdDescentRadio.setSelected(true);
            thirdDescentRadio.setEnabled(false);

            clearAllBTN.setEnabled(false);
        } else {
            //
            firstAscentRadio.setEnabled(true);
            firstDescentRadio.setEnabled(true);
            //
            secondCombo.setEnabled(true);

            clearAllBTN.setEnabled(true);
        }

        if ((firstCombo.getSelectedItem().toString().equals(secondCombo.getSelectedItem().toString()) && firstCombo
                .getSelectedIndex() != 0)
                || (secondCombo.getSelectedItem().toString().equals(thirdCombo.getSelectedItem().toString()) && thirdCombo
                        .getSelectedIndex() != 0)
                || (firstCombo.getSelectedItem().toString().equals(thirdCombo.getSelectedItem().toString()) && thirdCombo
                        .getSelectedIndex() != 0)) {
            ok.setEnabled(false);
        } else {
            ok.setEnabled(true);
        }
        cb1 = seleIndex;
    }

    /**
     * 第二Combo作了选择 Called by ActionPerformed
     */
    private void secondComboSelected() {
        int seleIndex = secondCombo.getSelectedIndex();
        if (seleIndex == 0) {
            //
            secondAscentRadio.setSelected(false);
            secondAscentRadio.setEnabled(false);
            //
            secondDescentRadio.setSelected(true);
            secondDescentRadio.setEnabled(false);

            //
            thirdCombo.setSelectedIndex(0);
            thirdCombo.setEnabled(false);
            thirdAscentRadio.setSelected(false);
            thirdAscentRadio.setEnabled(false);
            thirdDescentRadio.setSelected(true);
            thirdDescentRadio.setEnabled(false);
        } else {
            //
            secondAscentRadio.setEnabled(true);
            secondDescentRadio.setEnabled(true);
            //
            thirdCombo.setEnabled(true);
        }

        if ((firstCombo.getSelectedItem().toString().equals(secondCombo.getSelectedItem().toString()) && firstCombo
                .getSelectedIndex() != 0)
                || (secondCombo.getSelectedItem().toString().equals(thirdCombo.getSelectedItem().toString()) && thirdCombo
                        .getSelectedIndex() != 0)
                || (firstCombo.getSelectedItem().toString().equals(thirdCombo.getSelectedItem().toString()) && thirdCombo
                        .getSelectedIndex() != 0)) {
            ok.setEnabled(false);
        } else {
            ok.setEnabled(true);
        }
        cb2 = seleIndex;
    }

    /**
     * 第三Combo作了选择 Called by ActionPerformed
     */
    private void thirdComboSelected() {
        int seleIndex = thirdCombo.getSelectedIndex();
        if (seleIndex == 0) {
            //
            thirdAscentRadio.setSelected(false);
            thirdAscentRadio.setEnabled(false);

            thirdDescentRadio.setSelected(true);
            thirdDescentRadio.setEnabled(false);
        } else {
            //
            thirdAscentRadio.setEnabled(true);
            thirdDescentRadio.setEnabled(true);
        }

        if ((firstCombo.getSelectedItem().toString().equals(secondCombo.getSelectedItem().toString()) && firstCombo
                .getSelectedIndex() != 0)
                || (secondCombo.getSelectedItem().toString().equals(thirdCombo.getSelectedItem().toString()) && thirdCombo
                        .getSelectedIndex() != 0)
                || (firstCombo.getSelectedItem().toString().equals(thirdCombo.getSelectedItem().toString()) && thirdCombo
                        .getSelectedIndex() != 0)) {
            ok.setEnabled(false);
        } else {
            ok.setEnabled(true);
        }
        cb3 = seleIndex;
    }

    /**
     * 第三Combo作了选择 Called by ActionPerformed
     */
    private void clearAllClicked() {

        // 开第一个,选中取空
        firstCombo.setSelectedIndex(0);
        // 默认为降序
        firstDescentRadio.setSelected(true);
        //
        firstAscentRadio.setEnabled(false);
        firstDescentRadio.setEnabled(false);

        // 第二选区全禁止
        secondCombo.setSelectedIndex(0);
        secondCombo.setEnabled(false);
        secondAscentRadio.setEnabled(false);
        // 默认为降序
        secondDescentRadio.setSelected(true);
        secondDescentRadio.setEnabled(false);

        // 第三选区全禁止
        thirdCombo.setEnabled(false);
        thirdCombo.setSelectedIndex(0);
        thirdAscentRadio.setEnabled(false);
        // 默认为降序
        thirdDescentRadio.setSelected(true);
        thirdDescentRadio.setEnabled(false);

        clearAllBTN.setEnabled(false);
        ok.setEnabled(false);
    }

    /**
     * 按下cancel按钮 Called by ActionPerformed
     */
    private void cancelClicked(
            ActionEvent e) {
        // ok.setEnabled(false);
        dispose();
    }

    /**
     * 以后要去掉
     * 
     * @param prmViewInfo
     *            视图信息
     */
    public void setVisible(
            PIMViewInfo prmViewInfo) {
        this.viewInfo = prmViewInfo;
        modified = false;
        initComboAndFlags();
        // 开始时禁止
        // ok.setEnabled(false);
        setVisible(true);
    }

    /**
     * 按下确定按钮 Called by ActionPerformed
     */
    private void okClicked(
            ActionEvent e) {
        // 先定义一个空字符串
        String result = null;
        // 保存当前视图中所用的全部字段
        Object[] allFields = null;
        // 得到当前的应用类型
        int tmpActiveAppType = viewInfo.getAppIndex();
        // 取邮件视图的所有字段
        // 目前好象是三个应用
        if (tmpActiveAppType == ModelCons.OUTBOX_APP || tmpActiveAppType == ModelCons.INBOX_APP
                || tmpActiveAppType == ModelCons.SENDED_APP || tmpActiveAppType == ModelCons.DELETED_ITEM_APP) {
            // 从数据模型中取得所有字段
            allFields = IntlModelConstants.EMAIL_FLDS;
        }
        // 取联系人视图的所有字段
        else if (tmpActiveAppType == ModelCons.CONTACT_APP) {
            // 从数据模型中取得所有字段
            allFields = IntlModelConstants.CONTACTS_FIELD;
        } else if (tmpActiveAppType == ModelCons.DIARY_APP) {
            allFields = IntlModelConstants.DIARY_FLDS;
        } else if (tmpActiveAppType == ModelCons.TASK_APP) {
            allFields = IntlModelConstants.TASK_FLDS;
        } else if (tmpActiveAppType == ModelCons.CALENDAR_APP) {
            allFields = IntlModelConstants.APPOINTMENT_FLDS;
        }

        // 表示第一个下拉框选中了 stop rocking!
        if (firstCombo.getSelectedIndex() > 0) {
            String str = firstCombo.getSelectedItem().toString();
            // 从所有字段中遍历
            for (int j = 0; j < allFields.length; j++) {
                // 取出其索引
                if (str.equals(allFields[j].toString())) {
                    result = Integer.toString(j).concat(",");
                    break;
                }
            }
            // 从升降序按钮中解出'0','1'信息
            if (firstAscentRadio.isSelected()) {
                result = result.concat("0,");
            } else {
                result = result + "1,";
            }
        }
        // 表示第二个下拉框选中了
        if (secondCombo.getSelectedIndex() > 0) {
            String str = secondCombo.getSelectedItem().toString();
            // 从所有字段中遍历
            for (int j = 0; j < allFields.length; j++) {
                // 取出其索引
                if (str.equals(allFields[j].toString())) {
                    result = result + j + ',';
                    break;
                }
            }
            // 从升降序按钮中解出'0','1'信息
            if (secondAscentRadio.isSelected()) {
                result = result + "0,";
            } else {
                result = result + "1,";
            }
        }
        // 表示第三个下拉框选中了
        if (thirdCombo.getSelectedIndex() > 0) {
            String str = thirdCombo.getSelectedItem().toString();
            // 从所有字段中遍历
            for (int j = 0; j < allFields.length; j++) {
                // 取出其索引
                if (str.equals(allFields[j].toString())) {
                    result = result + j + ',';
                    break;
                }
            }
            // 从升降序按钮中解出'0','1'信息
            if (thirdAscentRadio.isSelected()) {
                result = result + "0,";
            } else {
                result = result + "1,";
            }
        }
        // 在排序结果不为空时才操作存储
        if (result != null) {
            // 克隆出来
            PIMViewInfo tmpViewInfo = (PIMViewInfo) viewInfo.clone();
            // 设置进去
            tmpViewInfo.setSortCritia(result.substring(0, result.length() - 1));
            // 修改标志置真,以供父对话盒使用
            modified = true;
            // 下面一句放入数据库
            CASControl.ctrl.getModel().updateViewInfo(tmpViewInfo);
        } else {
            // 克隆出来
            PIMViewInfo tmpViewInfo = (PIMViewInfo) viewInfo.clone();
            // 设置进去
            tmpViewInfo.setSortCritia(null);
            // 修改标志置真,以供父对话盒使用
            modified = true;
            // 下面一句放入数据库
            CASControl.ctrl.getModel().updateViewInfo(tmpViewInfo);
        }
        ok.setEnabled(false);
        // 标志更改,以通知父对话盒
        if (parentDialog != null && parentDialog instanceof CustomizeViewDialog) {
            ((CustomizeViewDialog) parentDialog).equipDiscription();
            parentDialog = null;
        }
        dispose();
        // setVisible(false);
    }

    /**
     * 实现接口中的方法
     */
    public void release() {
        dispose();
    }

    /**
     * 实现接口中的方法,本例返回空
     * 
     * @return NULL
     */
    public PIMRecord getContents() {
        return null;
    }

    public boolean setContents(
            PIMRecord prmRecord) {
        return false;
    }

    public void makeBestUseOfTime() {
    }

    /**
     * 返回字段已更改标志
     * 
     * @called by: CustomizeViewDialog
     * @return 是否已更改
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     * 
     * @param e
     *            状态变化事件
     */
    public void itemStateChanged(
            ActionEvent e) {
        if (e.getSource() == firstCombo) {
            if (firstCombo.getSelectedIndex() == secondCombo.getSelectedIndex() && secondCombo.getSelectedIndex() != 0
                    || firstCombo.getSelectedIndex() == thirdCombo.getSelectedIndex()
                    && thirdCombo.getSelectedIndex() != 0) {
                // Thread.dumpStack();
                firstCombo.setSelectedIndex(cb1);
                SOptionPane.showErrorDialog(PaneConsts.TITLE, SOptionPane.WARNING_MESSAGE, SOptionPane.STYLE_OK,
                        CustViewConsts.WARNIN);
                return;
            }
            firstComboSelected();
        } else if (e.getSource() == secondCombo) {
            // Thread.dumpStack();
            if (firstCombo.getSelectedIndex() == secondCombo.getSelectedIndex() && secondCombo.getSelectedIndex() != 0
                    || secondCombo.getSelectedIndex() == thirdCombo.getSelectedIndex()
                    && thirdCombo.getSelectedIndex() != 0) {
                secondCombo.setSelectedIndex(cb2);
                SOptionPane.showErrorDialog(PaneConsts.TITLE, SOptionPane.WARNING_MESSAGE, SOptionPane.STYLE_OK,
                        CustViewConsts.WARNIN);
                return;
            }
            secondComboSelected();
        } else if (e.getSource() == thirdCombo) {
            if (firstCombo.getSelectedIndex() == thirdCombo.getSelectedIndex() && secondCombo.getSelectedIndex() != 0
                    || secondCombo.getSelectedIndex() == thirdCombo.getSelectedIndex()
                    && thirdCombo.getSelectedIndex() != 0) {
                thirdCombo.setSelectedIndex(cb3);
                SOptionPane.showErrorDialog(PaneConsts.TITLE, SOptionPane.WARNING_MESSAGE, SOptionPane.STYLE_OK,
                        CustViewConsts.WARNIN);
                return;
            }
            thirdComboSelected();
        }

        if (firstCombo.getSelectedIndex() == 0 && secondCombo.getSelectedIndex() == 0
                && thirdCombo.getSelectedIndex() == 0) {
            ok.setEnabled(false);
        }

    }

    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    public void valueChanged(
            ListSelectionEvent e) {

    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        firstCombo.grabFocus(); // 设置焦点默认落点
    }

    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    public PIMTextPane getTextPane() {
        return null;
    }

    public Container getContainer() {
        return getContentPane();
    }

    private int cb1 = 0, cb2 = 0, cb3 = 0;
    // 保存父对话盒的引用
    private Dialog parentDialog;
    // 保存当前视图信息的引用
    private PIMViewInfo viewInfo;
    // 保存修改标志,以便父对话盒作相应措施
    private boolean modified;

    // 保存本对话盒的尺寸
    /**
     * 本对话盒宽度
     */
    public static final int WIDTH = 240;
    // 高度
    /**
     * 本对话盒高度
     */
    public static final int HEIGHT = 165;

    // 大型按钮的宽度
    /**
     * 以后要去掉
     */
    public static final int BUTTON_WIDTH = 100;

    // 构建列表框用的一个常量
    /**
     * 以后要去掉
     */
    public static final int OFFSET = -1;

    // buttons
    // 清除所有按钮
    public JButton ok;
    public JButton cancel;
    private JButton clearAllBTN;
    // 第一下拉框
    private JComboBox firstCombo;
    // 与上对应的两单选框
    private JRadioButton firstAscentRadio;
    private JRadioButton firstDescentRadio;

    // 第一下拉框
    private JComboBox secondCombo;
    // 与上对应的两单选框
    private JRadioButton secondAscentRadio;
    private JRadioButton secondDescentRadio;

    // 第一下拉框
    private JComboBox thirdCombo;
    // 与上对应的两单选框
    private JRadioButton thirdAscentRadio;
    private JRadioButton thirdDescentRadio;

    // 第一下拉框对应的模型
    private DefaultComboBoxModel firstComboModel;
    // 第二下拉框对应的模型
    private DefaultComboBoxModel secondComboModel;
    // 第三下拉框对应的模型
    private DefaultComboBoxModel thirdComboModel;

    private PIMButtonGroup groupFirst;
    private PIMButtonGroup groupSecond;
    private PIMButtonGroup groupThird;

}
