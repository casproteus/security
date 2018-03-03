package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.resource.international.CustViewConsts;
import org.cas.client.resource.international.IntlModelConstants;

public class FieldsDialog extends JDialog implements ICASDialog, ActionListener, ListSelectionListener, MouseListener,
        Runnable {

    /**
     * 创建一个 FieldsDialog 的实例
     * 
     * @param prmDialog
     *            父窗体
     * @param prmViewInfo
     *            视图信息
     */
    public FieldsDialog(Dialog prmDialog, PIMViewInfo prmViewInfo) {
        super(prmDialog, true);
        // 保存引用
        this.parentDialog = prmDialog;
        this.viewInfo = prmViewInfo;
        // 设置标题
        setTitle(CustViewConsts.FIELD_DIALOG_TITLE);
        // 设置对话盒尺寸
        setSize(getDialogSize());
        // 组件初始化并布局
        initComponent();
        // 给组件加上监听器
        addAllListeners();
        // 设置焦点转移顺序
        focusTransferSequence();

        setBounds((CustOpts.SCRWIDTH - getDialogSize().width) / 2, (CustOpts.SCRHEIGHT - getDialogSize().height) / 2,
                getDialogSize().width, getDialogSize().height); // 对话框的默认尺寸。
    }

    /**
     * 创建一个 FieldsDialog 的实例
     * 
     * @param parent
     *            父窗体
     * @param prmViewInfo
     *            视图信息
     */
    public FieldsDialog(Frame parent, PIMViewInfo prmViewInfo) {
        super(parent, true);

        this.viewInfo = prmViewInfo;
        // 设置标题
        setTitle(CustViewConsts.FIELD_DIALOG_TITLE);
        // 设置对话盒尺寸
        setSize(getDialogSize());
        // 组件初始化并布局
        initComponent();
        // 给组件加上监听器
        addAllListeners();
        // 设置焦点转移顺序
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

    /**
     * 初始化加布局;
     */
    private void initComponent() {
        Dimension dlgSize = getDialogSize();
        // 这两个变量用来给组件定位,所有的组件都根据它来计算
        int X_Coordinate = 0;
        int Y_Coordinate = 0;

        // 左上标签
        JLabel allUserableLabel = new JLabel(CustViewConsts.USERABLE_FIELDS);
        allUserableLabel.setDisplayedMnemonic('V');

        // Y_Coordinate += CustOpts.LABEL_HEIGHT + CustOpts.VER_GAP;
        // OK按钮初始化
        ok = new JButton(CustViewConsts.OK);
        ok.setBounds(dlgSize.width - ok.getPreferredSize().width, Y_Coordinate + CustOpts.LBL_HEIGHT,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);
        // 开始时禁止
        ok.setEnabled(false);

        // 完成两个List组件的模型设置
        equipListModels();
        // 左List组件高度和宽度设置
        int tmpListWidth = ok.getPreferredSize().width * 2 + CustOpts.HOR_GAP;
        int tmpListHeight =
                dlgSize.height - CustOpts.LBL_HEIGHT - Y_Coordinate - ok.getSize().height - CustOpts.VER_GAP;
        // 设置左List组件
        allUserableList = new JList(allUserableListModel);
        allUserableLabel.setLabelFor(allUserableList);
        allUserableLabel.setBounds(X_Coordinate, Y_Coordinate, OFFSET, CustOpts.LBL_HEIGHT);
        allUserableList.setBounds(allUserableLabel.getX() + allUserableLabel.getWidth(), allUserableLabel.getY(),
                tmpListWidth, tmpListHeight);
        getContentPane().add(allUserableLabel);
        getContentPane().add(allUserableList);
        // allUserableList.disableDoubleClick();//防止双击关闭对话盒
        // 设置Cancel按钮
        cancel = new JButton(CustViewConsts.CANCEL);
        cancel.setBounds(dlgSize.width - cancel.getPreferredSize().width, Y_Coordinate + ok.getPreferredSize().height
                + CustOpts.VER_GAP + CustOpts.LBL_HEIGHT, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);

        X_Coordinate += tmpListWidth + CustOpts.HOR_GAP;
        // 设置添加按钮 BUTTON_WIDTH
        addOneBTN = new JButton(CustViewConsts.ADD_ONE);
        addOneBTN.setMnemonic('A');
        addOneBTN.setBounds(X_Coordinate, Y_Coordinate + CustOpts.LBL_HEIGHT, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(addOneBTN);

        // 设置删除按钮
        deleteBTN = new JButton(CustViewConsts.DELETE);
        deleteBTN.setMnemonic('C');
        deleteBTN.setBounds(X_Coordinate, Y_Coordinate + CustOpts.LBL_HEIGHT + addOneBTN.getSize().height
                + CustOpts.VER_GAP, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(deleteBTN);

        X_Coordinate += addOneBTN.getSize().width + CustOpts.HOR_GAP;
        Y_Coordinate = 0;
        // 右上标签
        JLabel usedFieldLabel = new JLabel(CustViewConsts.SHOW_IN_THIS_SEQUENCE, 'D');
        usedFieldList = new JList(usedFieldListModel); // 右边的已使用字段list

        // Y_Coordinate += CustOpts.LABEL_HEIGHT + CustOpts.VER_GAP;
        usedFieldLabel.setLabelFor(usedFieldList);
        usedFieldLabel.setBounds(X_Coordinate, Y_Coordinate, OFFSET, CustOpts.LBL_HEIGHT);
        usedFieldList.setBounds(usedFieldLabel.getX() + usedFieldLabel.getWidth(), usedFieldLabel.getY(), tmpListWidth,
                tmpListHeight);
        getContentPane().add(usedFieldList);
        getContentPane().add(usedFieldLabel);
        // usedFieldList.disableDoubleClick();//防止双击关闭对话盒
        Y_Coordinate = dlgSize.height - ok.getPreferredSize().height;
        // 上移按钮
        moveUpBTN = new JButton(CustViewConsts.MOVE_UP);
        moveUpBTN.setMnemonic('U');
        moveUpBTN.setBounds(X_Coordinate, Y_Coordinate, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(moveUpBTN);
        // 开始时禁止
        moveUpBTN.setEnabled(false);

        // 下移按钮
        X_Coordinate += moveUpBTN.getSize().width + CustOpts.HOR_GAP;
        moveDownBTN = new JButton(CustViewConsts.MOVE_DOWN);
        moveDownBTN.setMnemonic('D');
        moveDownBTN.setBounds(X_Coordinate, Y_Coordinate, moveDownBTN.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        getContentPane().add(moveDownBTN);
    }

    /**
     * 给所有的组件加上监听器
     */
    private void addAllListeners() {
        addOneBTN.addActionListener(this);
        deleteBTN.addActionListener(this);
        moveDownBTN.addActionListener(this);
        moveUpBTN.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        // 控制上移下移按钮的使能
        usedFieldList.addListSelectionListener(this);
        usedFieldList.addMouseListener(this);
        allUserableList.addMouseListener(this);
    }

    /**
     * 用来对两个列表框组件的模型进行初始化`
     */
    private void equipListModels() {
        // 建右边的列表框组件的模型实例
        usedFieldListModel = new DefaultListModel();
        // 设置字段信息
        String[] fieldsNames = CASControl.ctrl.getModel().getFieldNames(viewInfo);
        IDStr = fieldsNames[0];
        // 已用字段的list 模型
        // 因为第一个一定是ID,对用户是隐蔽的
        for (int i = 1; i < fieldsNames.length; i++) {
            usedFieldListModel.addElement(fieldsNames[i]);
        }
        // 至此完成了右边的列表框组件的模型的建立

        // 下面是建左边的列表框组件的模型实例
        // 得到当前的应用类型
        int tmpActiveAppType = viewInfo.getAppIndex();
        // TODO:下面一段以后也要移动到工具类中去

        // 取邮件视图的所有字段
        // 目前好象是三个应用
        if (tmpActiveAppType == ModelCons.OUTBOX_APP || tmpActiveAppType == ModelCons.INBOX_APP
                || tmpActiveAppType == ModelCons.SENDED_APP || tmpActiveAppType == ModelCons.DELETED_ITEM_APP) {
            // 从数据模型中取得所有字段
            allFields = IntlModelConstants.EMAIL_FLDS;
        }
        // 取联系人视图的所有字段
        else if (tmpActiveAppType == ModelCons.CONTACT_APP) {
            allFields = IntlModelConstants.CONTACTS_FIELD;
        } else if (tmpActiveAppType == ModelCons.DIARY_APP) {
            allFields = IntlModelConstants.DIARY_FLDS;
        } else if (tmpActiveAppType == ModelCons.TASK_APP) {
            allFields = IntlModelConstants.TASK_FLDS;
        } else if (tmpActiveAppType == ModelCons.CALENDAR_APP) {
            allFields = IntlModelConstants.APPOINTMENT_FLDS;
        }

        // 把它们放进ArrayList
        ArrayList allFieldsArray = new ArrayList(allFields.length);
        for (int i = 0; i < allFields.length; i++) {
            // 一个萝卜一个坑
            allFieldsArray.add(allFields[i]);
        }

        // 可用字段与可显示字段进行比较,以过滤已用字段
        for (int i = 0; i < fieldsNames.length; i++) {
            // 因为ArrayList有contains方法
            if (allFieldsArray.contains(fieldsNames[i])) {
                allFieldsArray.remove(fieldsNames[i]);
            }
        }
        // 2003.10.23 去掉"已读"字段
        allFieldsArray.remove(IntlModelConstants.READED);
        // 联系人的不可显示字段
        if (tmpActiveAppType == ModelCons.CONTACT_APP) {
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.SIZE]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.ATTACHMENT]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.IMPORTANCE]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.ADDRESSER]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.RECIEVEDATE]);
        }
        // 任务的不可显示字段
        else if (tmpActiveAppType == ModelCons.TASK_APP) {
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.SIZE]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.ATTACHMENT]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.ADDRESSER]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.FOLLOWFLAGS]);
        }
        // 日历的不可显示字段
        else if (tmpActiveAppType == ModelCons.CALENDAR_APP) {
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.SIZE]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.ATTACHMENT]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.ADDRESSER]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.FOLLOWFLAGS]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.FLAGSTATUS]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.IMPORTANCE]);
        }
        // 日记的不可显示字段
        else if (tmpActiveAppType == ModelCons.DIARY_APP) {
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.ATTACHMENT]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.IMPORTANCE]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.FLAGSTATUS]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.ADDRESSER]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.RECIEVEDATE]);
            allFieldsArray.remove(IntlModelConstants.EMAIL_FLDS[ModelDBCons.FOLLOWFLAGS]);
        }
        // 调整ArrayList的容量
        allFieldsArray.trimToSize();
        allUserableListModel = new DefaultListModel();
        // 已用字段的list模型
        // 和右边的list一样
        for (int i = 0; i < allFieldsArray.size(); i++) {
            allUserableListModel.addElement(allFieldsArray.get(i));
        }
    }

    /**
     * 以后要改
     * 
     * @param prmViewInfo
     *            视图信息
     */
    public void setVisible(
            PIMViewInfo prmViewInfo) {
        this.viewInfo = prmViewInfo;
        modified = false;
        equipListModels();
        // 开始时禁止
        ok.setEnabled(false);
        allUserableList.setModel(allUserableListModel);
        allUserableList.setSelectedValue(allUserableListModel.get(0), true);

        usedFieldList.setModel(usedFieldListModel);
        usedFieldList.setSelectedValue(usedFieldListModel.get(0), true);
        // 开始时禁止
        moveUpBTN.setEnabled(false);
        setVisible(true);
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        // 加一个字段
        if (e.getSource() == addOneBTN) {
            addOneClicked();
        }
        // 从已用字段列表中删除一个
        else if (e.getSource() == deleteBTN) {
            deleteClicked();
        }
        // 按了上移按钮
        else if (e.getSource() == moveUpBTN) {
            moveUpClicked();
        }
        // 按了下移按钮
        else if (e.getSource() == moveDownBTN) {
            moveDownClicked();
        }
        // 按下OK
        else if (e.getSource() == ok) {
            okClicked(e);
        }
        // 按下cancel
        else if (e.getSource() == cancel) {
            cancelClicked(e);
        }
    }

    /**
     * 按下添加按钮 Called by ActionPerformed
     */
    private void addOneClicked() {
        // 得到模型容量
        int size = allUserableListModel.getSize();
        // 取可用字段列表的选中索引
        int tmpSelectionIndex = allUserableList.getSelectedIndex();
        // 取可用字段列表的选中项
        Object tmpSelectionItem = allUserableList.getSelectedValue();
        // 从可用字段列表中移除
        allUserableListModel.remove(tmpSelectionIndex);
        // 如果是最后一个,且列表框中不止一个选项
        if (tmpSelectionIndex == size - 1 && size > 1) {
            // 重设可用字段列表中选中索引,就是前一个
            allUserableList.setSelectedIndex(tmpSelectionIndex - 1);
        }
        // 如果是最后一个,且列表框中只有一个选项
        else if (tmpSelectionIndex == size - 1 && size == 1) {
            // 自宫
            addOneBTN.setEnabled(false);
        }
        // 正常设置
        else {
            // 重设可用字段列表中选中索引,就是下一个
            allUserableList.setSelectedIndex(tmpSelectionIndex);
        }

        // 在已用字段列表中加一个,并使其可见
        usedFieldListModel.addElement(tmpSelectionItem);
        usedFieldList.setSelectedValue(tmpSelectionItem, true);
        // 添加之后必不能下移
        moveDownBTN.setEnabled(false);
        // 变化了才需要确定
        if (!ok.isEnabled()) {
            ok.setEnabled(true);
        }
        // 现在一定可以执行删除
        if (!deleteBTN.isEnabled()) {
            deleteBTN.setEnabled(true);
        }
        // 现在一定可以执行上移操作
        if (!moveUpBTN.isEnabled()) {
            // moveUpBTN.setEnabled(true);
        }
        // 如果模型中长度为空,白白吧
        if (allUserableListModel.getSize() == 0) {
            addOneBTN.setEnabled(false);
        }
    }

    /**
     * 按下删除按钮 Called by ActionPerformed
     */
    private void deleteClicked() {
        // 得到模型容量
        int size = usedFieldListModel.getSize();
        // 取已用字段列表的选中索引
        int tmpSelectionIndex = usedFieldList.getSelectedIndex();
        // 取已用字段列表的选中项
        Object tmpSelectionItem = usedFieldList.getSelectedValue();
        // 从已用字段列表中移除
        usedFieldListModel.remove(tmpSelectionIndex);

        // 如果是最后一个,且列表框中不止一个选项
        if (tmpSelectionIndex == size - 1 && size > 1) {
            // 重设已用字段列表中选中索引,就是上一个
            usedFieldList.setSelectedIndex(tmpSelectionIndex - 1);
        }
        // 如果是最后一个,且列表框中只有一个选项
        else if (tmpSelectionIndex == size - 1 && size == 1) {
            deleteBTN.setEnabled(false);
        }
        // 正常设置
        else {
            // 重设已用字段列表中选中索引,就是上一个
            usedFieldList.setSelectedIndex(tmpSelectionIndex);
        }

        // 在可用字段列表中加一个,并使其可见
        allUserableListModel.addElement(tmpSelectionItem);
        allUserableList.setSelectedValue(tmpSelectionItem, true);

        // 变化了才需要确定
        if (!ok.isEnabled()) {
            ok.setEnabled(true);
        }
        // 现在一定可以执行添加操作
        if (!addOneBTN.isEnabled()) {
            addOneBTN.setEnabled(true);
        }
        // 如果模型中长度为空,白白吧
        if (allUserableListModel.getSize() == 0) {
            // moveUpBTN.setEnabled(false);
            deleteBTN.setEnabled(false);
        }
    }

    /**
     * 按下上移按钮 Called by ActionPerformed
     */
    private void moveUpClicked() {
        // 取已用字段列表的选中索引
        int tmpSelectionIndex = usedFieldList.getSelectedIndex();
        // 是第一个就不可以进行
        if (tmpSelectionIndex == 0) {
            return;
        }
        // 取已用字段列表的选中项的前一个家伙
        Object tmpSelectionItem = usedFieldListModel.get(tmpSelectionIndex - 1);
        // 将它移除
        usedFieldListModel.remove(tmpSelectionIndex - 1);
        // 在当前位置加入
        usedFieldListModel.add(tmpSelectionIndex, tmpSelectionItem);
        // 将上移了的那个家伙选中
        usedFieldList.setSelectedIndex(tmpSelectionIndex - 1);
        usedFieldList.ensureIndexIsVisible(tmpSelectionIndex - 1);
        // 上移那项必能下移
        // moveDownBTN.setEnabled(true);

        // 变化了才需要确定
        if (!ok.isEnabled()) {
            ok.setEnabled(true);
        }
    }

    /**
     * 按下下移按钮 Called by ActionPerformed
     */
    private void moveDownClicked() {
        // 取已用字段列表的选中索引
        int tmpSelectionIndex = usedFieldList.getSelectedIndex();
        // 是最后一个就不可以进行
        if (tmpSelectionIndex == usedFieldListModel.size()) {
            return;
        }
        // 取已用字段列表的选中项
        Object tmpSelectionItem = usedFieldListModel.get(tmpSelectionIndex);
        // 将它移除
        usedFieldListModel.remove(tmpSelectionIndex);
        // 在当前位置后一个位置加入
        usedFieldListModel.add(tmpSelectionIndex + 1, tmpSelectionItem);
        // 将下移了的那个家伙选中
        usedFieldList.setSelectedIndex(tmpSelectionIndex + 1);
        usedFieldList.ensureIndexIsVisible(tmpSelectionIndex + 1);
        // 下移那项必能上移
        // moveUpBTN.setEnabled(true);

        // 变化了才需要确定
        if (!ok.isEnabled()) {
            ok.setEnabled(true);
        }
    }

    /**
     * 按下OK Called by ActionPerformed
     */
    private void okClicked(
            ActionEvent e) {
        // 得到已用字段列表的所有项
        usedFieldListModel.add(0, IDStr);
        Object[] modifiedFields = usedFieldListModel.toArray();

        // 调用工具类中的方法得到字段的字符串表示,用于写入数据库
        String fields = ParseUtil.imparseFields(viewInfo, modifiedFields);

        // 调用工具类中的方法得到字段宽度的字符表示,用于写入数据库
        String widths = ParseUtil.imparseFieldWidths(viewInfo, modifiedFields);

        // 设置视图信息中的字段和其宽度值
        PIMViewInfo tmpViewInfo = (PIMViewInfo) this.viewInfo.clone();
        tmpViewInfo.setFieldNames(fields);
        tmpViewInfo.setFieldWidths(widths);

        // 下面一句放入数据库

        CASControl.ctrl.getModel().updateViewInfo(tmpViewInfo);

        // 标志更改,以通知父对话盒
        if (parentDialog != null && parentDialog instanceof CustomizeViewDialog) {
            ((CustomizeViewDialog) parentDialog).updateFields();
            parentDialog = null;
        }
        dispose();
    }

    /**
     * 按下cancel Called by ActionPerformed
     */
    private void cancelClicked(
            ActionEvent e) {
        ok.setEnabled(false);
        parentDialog = null;
        dispose();
    }

    /**
     * 控制信息转移顺序 由构建器调用
     */
    private void focusTransferSequence() {
        // 设置Tab建焦点顺序。
        allUserableList.setNextFocusableComponent(addOneBTN);
        addOneBTN.setNextFocusableComponent(deleteBTN);
        deleteBTN.setNextFocusableComponent(usedFieldList);
        usedFieldList.setNextFocusableComponent(moveUpBTN);
        moveUpBTN.setNextFocusableComponent(moveDownBTN);
        moveDownBTN.setNextFocusableComponent(ok);
        ok.setNextFocusableComponent(cancel);
        cancel.setNextFocusableComponent(allUserableList);

        // 最后执行
        SwingUtilities.invokeLater(this);
    }

    /**
     * 返回字段已更改标志
     * 
     * @called by: CustomizeViewDialog
     * @return 是否更改
     */

    public boolean isModified() {
        return modified;
    }

    /**
     * 实现接口中的方法
     */
    public void release() {
        super.dispose();
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
     * 用于上移下移按钮的使能 Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    public void valueChanged(
            ListSelectionEvent e) {
        if (e.getSource() == usedFieldList) {
            // 得到模型容量
            int size = usedFieldListModel.getSize();
            // 取已用字段列表的选中索引
            int tmpSelectionIndex = usedFieldList.getSelectedIndex();

            // 用于上移下移按钮的使能
            // 模型为空,全禁止
            if (size == 0) {
                moveDownBTN.setEnabled(false);
                moveUpBTN.setEnabled(false);
            }
            // 只有一行全禁止
            else if (tmpSelectionIndex == 0 && size == 1) {
                moveDownBTN.setEnabled(false);
                moveUpBTN.setEnabled(false);
            }
            // 在第一行时不可上移
            else if (tmpSelectionIndex == 0) {
                moveUpBTN.setEnabled(false);
                moveDownBTN.setEnabled(true);
            }
            // 在最后一行时不可下移
            else if (tmpSelectionIndex == size - 1) {
                moveDownBTN.setEnabled(false);
                moveUpBTN.setEnabled(true);
            } else {
                moveUpBTN.setEnabled(true);
                moveDownBTN.setEnabled(true);
            }

            // 用于删除按钮的使能
            // 根据规格,"表示为"字段必须有(在每一个联系人视图),与OUTLOOK不同;
            // 允许用户在此编辑栏中输入,此栏必须有输入值,否则不入数据库
            // 此处对右边列表框处理,在选中"表示为"字段时将删除按钮禁掉
            if (usedFieldList.getSelectedValue() != null) {
                String tmpCurrentValue = usedFieldList.getSelectedValue().toString();
                if (viewInfo.getAppIndex() == ModelCons.TASK_APP) {
                    if (tmpCurrentValue.equals(IntlModelConstants.TASK_FLDS[ModelDBCons.CAPTION])
                            || tmpCurrentValue.equals(IntlModelConstants.TASK_FLDS[ModelDBCons.FINISH_FLAG])) {
                        deleteBTN.setEnabled(false);
                    } else {
                        deleteBTN.setEnabled(true);
                    }
                } else if (viewInfo.getAppIndex() == ModelCons.CALENDAR_APP) {
                    if (tmpCurrentValue.equals(IntlModelConstants.TASK_FLDS[ModelDBCons.CAPTION])) {
                        deleteBTN.setEnabled(false);
                    } else {
                        deleteBTN.setEnabled(true);
                    }
                } else if (viewInfo.getAppIndex() == ModelCons.CONTACT_APP) {
                    if (tmpCurrentValue.equals(IntlModelConstants.CONTACTS_FIELD[ModelDBCons.CAPTION])) {
                        deleteBTN.setEnabled(false);
                    } else {
                        deleteBTN.setEnabled(true);
                    }
                }

            }
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component. 用来处理LIST列表框中鼠标双击产生的添加,删除动作
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
        // 鼠标左键双击
        if (e.getClickCount() > 1 && SwingUtilities.isLeftMouseButton(e)) {
            // 在右列表框中执行删除动作
            if (e.getSource() == usedFieldList) {
                deleteClicked();
            }
            // 在左列表框中执行添加动作
            else if (e.getSource() == allUserableList) {
                addOneClicked();
            }
            // 防止兔崽子做三击以上的事儿
            e.consume();
        }
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
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
        // 设置焦点默认落点
        allUserableList.grabFocus();
    }

    public Container getContainer() {
        return getContentPane();
    }

    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    public PIMTextPane getTextPane() {
        return null;
    }

    // 保存父对话盒的引用
    private Dialog parentDialog;

    // 保存当前视图信息的引用
    private PIMViewInfo viewInfo;

    // 保存本对话盒的尺寸
    /**
     * 本对话盒宽度
     */
    public static final int WIDTH = 510;
    /**
     * 本对话盒高度
     */
    public static final int HEIGHT = 300;
    // 大型按钮的宽度
    /**
     * 以后去掉
     */
    public static final int BUTTON_WIDTH = 100;

    // 构建列表框用的一个常量
    /**
     * 以后去掉
     */
    public static final int OFFSET = -1;

    // 左边可用字段对话盒列表框
    private JList allUserableList;
    // 左边可用字段对话盒列表框的模型
    private DefaultListModel allUserableListModel;
    // 右边可用字段对话盒列表框
    private JList usedFieldList;
    // 右边可用字段对话盒列表框的模型
    private DefaultListModel usedFieldListModel;

    // 列表框中间的两个按钮,加一,减一
    private JButton addOneBTN;
    private JButton deleteBTN;

    public JButton ok;// 确定按钮
    public JButton cancel; // 取消按钮
    // 右列表框下方的两个按钮,上移,下移
    private JButton moveUpBTN;
    private JButton moveDownBTN;
    // 以保存当前视图的所有字段
    private Object[] allFields;
    // 保存修改标志,以便父对话盒作相应措施
    private boolean modified;

    // 保存对用户隐蔽的ID
    private String IDStr;
}
